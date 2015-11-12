/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
 * %%
 * This file is part of PortfolioEffect Quant Client.
 * 
 * PortfolioEffect Quant Client is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * PortfolioEffect Quant Client is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with PortfolioEffect Quant Client. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.portfolioeffect.quant.client.portfolio;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolioeffect.quant.client.ClientConnection;
import com.portfolioeffect.quant.client.model.ConnectFailedException;
import com.portfolioeffect.quant.client.result.MethodResult;
import com.portfolioeffect.quant.client.util.Console;
import com.portfolioeffect.quant.client.util.DateTimeUtil;
import com.portfolioeffect.quant.client.util.MessageStrings;

public class Estimator {

	
	private static final int NUMBER_OF_TRIES = 10;
	private boolean isDebug = false;
	private EstimatorData estimatorData = new EstimatorData();
	
	private ClientConnection clientConnection;

	private List<CacheKey> cachedValueList;
	private PortfolioCache estimatorCache;

	public Estimator(Estimator estimator) throws Exception {

		this.estimatorData = new EstimatorData(estimator.estimatorData);
		this.clientConnection = estimator.clientConnection;


		this.cachedValueList = new ArrayList<CacheKey>();
		this.isDebug = estimator.isDebug;
		this.estimatorCache = new PortfolioCache();



	}

	public Estimator(ClientConnection clientConnection) {

		this.estimatorCache = new PortfolioCache();
		this.clientConnection = clientConnection;
		this.cachedValueList = new ArrayList<CacheKey>();
		
		setDefaultParams();

	}

	private void setDefaultParams() {

		estimatorData.setFromTime("#");
		estimatorData.setToTime("#");
		estimatorData.setParam("priceSamplingInterval", "1s");
		estimatorData.setParam("windowLength", "1s");
		estimatorData.setParam("samplingInterval", "all");
		estimatorData.setParam("jumpsModel", "moments");	
		
		clearCache();

	}

	private String getMetricTypeList(String metric) throws Exception {

		String result = "";
		try {
			Gson gson = new Gson();
			Type mapTypeMetrics = new TypeToken<HashMap<String, String>>() {
			}.getType();

			HashMap<String, String> metricArgs = gson.fromJson(metric, mapTypeMetrics);
			metricArgs.putAll(estimatorData.getSettings());
			ArrayList<HashMap<String, String>> paramsArgs = new ArrayList<HashMap<String, String>>();
			paramsArgs.add(metricArgs);

			result = gson.toJson(paramsArgs);

		} catch (Exception e) {

			throw new Exception(e.getMessage().split(":")[1]);

		}

		return result;
	}

	
	public void setParam(String key, String value) {
		estimatorData.getSettings().put(key, value);
		clearCache();
	}

	public String getParam(String key) {
		if (estimatorData.getSettings().containsKey(key))
			return estimatorData.getSettings().get(key);
		else
			return "";
	}

	public void setEstimatorSettings(Map<String, String> map) {
		estimatorData.setSettings(new HashMap<String, String>(map));

		clearCache();
	}

	public void setEstimatorSettings(String settingsJSON) {
		estimatorData.setSettingJSON(settingsJSON);
		clearCache();
	}

	public HashMap<String, String> getEstimatorSettings() {
		return estimatorData.getSettings();
	}

	public String getEstimatorSettingsJSON() {
		return estimatorData.getSettingJSON();
	}

	public MethodResult addAsset(String assetName) {

		if (estimatorData.getIndexPrice() != null) {

			estimatorData.setIndexPrice(null);
			estimatorData.setIndexTimeMillisec(null);
		}

		estimatorData.setPriceID(estimatorData.getNextDataId());
		estimatorData.setIndexSymbol(assetName);
		clearCache();

		return new MethodResult();

	}

	public MethodResult addAsset(double[] indexPrice, long timeStepMilliSec) {
		long[] timeMilliSec = new long[indexPrice.length];

		for (int i = 0; i < indexPrice.length; i++)
			timeMilliSec[i] = i * timeStepMilliSec + 1000;

		return addAsset(indexPrice, timeMilliSec);
	}

	public MethodResult addAsset(double[] price, long[] timeMilliSec) {

		if (estimatorData.getIndexPrice() != null) {

			estimatorData.setIndexPrice(null);
			estimatorData.setIndexTimeMillisec(null);
		}

		if (price.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE);
		}
		if (timeMilliSec.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_TIME);
		}
		
		if(timeMilliSec.length!=price.length)
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);
			

		try {
			estimatorData.setIndexPrice(new ArrayCache(price));

			estimatorData.setIndexTimeMillisec(new ArrayCache(timeMilliSec));

		} catch (IOException e) {
			return processException(e);
		}

		estimatorData.setIndexSymbol("index");
		estimatorData.setPriceID(estimatorData.getNextDataId());

		clearCache();

		return new MethodResult();
	}

	public MethodResult addIndex(float[] indexPrice, long timeStepMilliSec) {
		long[] timeMilliSec = new long[indexPrice.length];

		for (int i = 0; i < indexPrice.length; i++)
			timeMilliSec[i] = i * timeStepMilliSec + 1000;

		return addIndex(indexPrice, timeMilliSec);
	}

	public MethodResult addIndex(float[] price, long[] timeMilliSec) {

		if (estimatorData.getIndexPrice() != null) {

			estimatorData.setIndexPrice(null);
			estimatorData.setIndexTimeMillisec(null);
		}

		if (price.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE);
		}
		if (timeMilliSec.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_TIME);			
		}
		if(timeMilliSec.length!=price.length)
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);

		try {
			estimatorData.setIndexPrice(new ArrayCache(price));

			estimatorData.setIndexTimeMillisec(new ArrayCache(timeMilliSec));

		} catch (IOException e) {
			return processException(e);
		}

		estimatorData.setIndexSymbol("index");
		estimatorData.setPriceID( estimatorData.getNextDataId());

		clearCache();

		return new MethodResult();
	}

	public ClientConnection getClient() {
		return clientConnection;
	}

	public void clearCache() {
		for (CacheKey key : cachedValueList) {
			try {
				estimatorCache.remove(key);
			} catch (IOException e) {
				processException(e);

			}
		}
		cachedValueList = new ArrayList<CacheKey>();

	}











	public EstimatorData getEstimatorData() {
		return estimatorData;
	}
	

	private void processNoCashError(String str) throws Exception{
		if(str.contains("No data in cache"))
			throw new Exception(str);
			
	}
	
	private MethodResult transmitData(ArrayList<String> dataList) throws Exception {

		if (isDebug) {

			System.out.println("\n transmitData: " + dataList);
			System.out.println("" + getParam("windowLength") + "\t" + getParam("priceSamplingInterval") + "\t");

		}

		String windowLength = getParam("windowLength");
		String priceSamplingInterval = getParam("priceSamplingInterval");
		MethodResult result = clientConnection.transmitDataList(estimatorData.getFromTime(), estimatorData.getToTime(), dataList, windowLength,
				priceSamplingInterval,"PE");

		if (result.hasError()){
			processNoCashError(result.getErrorMessage());
			return result;
		}

		if (result.getMessage().length() == 0)
			return new MethodResult();

		Gson gson = new Gson();
		Type mapType = new TypeToken<String[]>() {
		}.getType();
		String[] dataTransmit = gson.fromJson(result.getMessage(), mapType);// result.getMessage().split(";");

		for (String e : dataTransmit) {

			String[] ePars = e.split("-");
			if (ePars[0].equals("h") || ePars[0].equals("hI")) {

				result = new MethodResult(MessageStrings.ERROR_HIST_PRICE);

				if (result.hasError()){
					processNoCashError(result.getErrorMessage());
					return result;
				}

			} else if (ePars[0].equals("u")) {

				String position = ePars[1].split(":")[1];

				if (position.equals("index")) {
					result = clientConnection.transmitUserPrice(ePars[1], estimatorData.getIndexPrice().getFloatArray(), estimatorData.getIndexTimeMillisec()
							.getLongArray());
					if (result.hasError()){
						processNoCashError(result.getErrorMessage());
						return result;
					}
				} 
			} else {
				return new MethodResult(MessageStrings.ERROR_TRANSMIT);
			}

		}

		return new MethodResult();

	}

	public MethodResult getMetric(String metricType) {

		if (estimatorData.getFromTime().length() == 0 || estimatorData.getToTime().length() == 0) {
			clientConnection.resetProgressBar();
			return new MethodResult("Set time interval  first");
		}

		for (int ii = 0; ii < NUMBER_OF_TRIES; ii++) {
			
			
						

			try {
				
				{

					MethodResult result = clientConnection.validateStringRequest("[{no:\"no\"}]");
					if (result.hasError())
						throw new Exception(result.getErrorMessage());
					
					
					
				}
				
				ArrayList<String> dataList = new ArrayList<String>();
				if (estimatorData.getIndexPrice() == null) {

					dataList.add("hI-" + estimatorData.getPortfolioId() + ":" + estimatorData.getIndexSymbol() + ":"
							+ estimatorData.getPriceID());
					setParam("indexName","hI-" + estimatorData.getPortfolioId() + ":" + estimatorData.getIndexSymbol() + ":"
							+ estimatorData.getPriceID());

					//indexPosition = estimatorData.getIndexSymbol();
				} else {
					dataList.add("u-" + estimatorData.getPortfolioId() + ":" + "index" + ":" + estimatorData.getPriceID());
					setParam("indexName", estimatorData.getPortfolioId() + ":" + "index" + ":" + estimatorData.getPriceID());
					//indexPosition = "index";

				}



				String metricTypeFull = getMetricTypeList(metricType);
				CacheKey key = new CacheKey(metricTypeFull, "");

				
				if (estimatorCache.containsKey(key)) {

					MethodResult result = new MethodResult();

					result.setData("value", estimatorCache.getMetric(key));
					result.setData("time", estimatorCache.getTime(key));

					return result;
				}

				clientConnection.printProgressBar(0);
				MethodResult result = null;

//				ArrayList<String> positionList = new ArrayList<String>();
				//String indexPosition = "";

				

				
	
//				
//				if (indexPosition.length() != 0) {
//					if (estimatorData.getIndexPrice() != null)
//						indexPosition = estimatorData.getPortfolioId() + ":" + indexPosition + ":" + estimatorData.getPriceID()+ "="
//								+ estimatorData.getPortfolioId() + ":" + indexPosition + ":" + estimatorData.getQuantityID().get(indexPosition);
//					else
//						indexPosition = "hI-" + estimatorData.getPortfolioId() + ":" + indexPosition + ":" + estimatorData.getPriceID().get(indexPosition)
//								+ "=" + estimatorData.getPortfolioId() + ":" + indexPosition + ":" + estimatorData.getQuantityID().get(indexPosition);
//				}
				
				MethodResult resultTransmit = transmitData(dataList);

				if (resultTransmit.hasError()) {
					clientConnection.resetProgressBar();
					return new MethodResult(resultTransmit.getErrorMessage());
				}

				if (isDebug) {

					Console.writeln("\n " + metricTypeFull);
					//Console.writeln(indexPosition);		

				}
				
				
				result = clientConnection.estimateEstimator(metricTypeFull);
				
				

				if (!result.hasError()) {

					estimatorCache.addMetric(key, result.getDataArrayCache("value"));
					estimatorCache.addTime(key, result.getDataArrayCache("time"));
					cachedValueList.add(key);

				} else {
					clientConnection.createCallGroup(1);
				}

				return result;

			} catch (Exception e) {

				MethodResult result = processException(e);
				if (result == null)
					continue;
				return result;

			}

		}
		clientConnection.resetProgressBar();
		return new MethodResult(MessageStrings.FAILED_SERVER_TIME_OUT);

	}

	
	
		private MethodResult processException(Exception e) {

		if (isDebug)
			Console.writeStackTrace(e);

		if (e instanceof ConnectFailedException) {

			MethodResult isRestarted = clientConnection.restart();
			if (isRestarted.hasError()) {
				clientConnection.resetProgressBar();
				return new MethodResult(isRestarted.getErrorMessage());
			}

			return null;
		}
		
		if (e.getMessage() == null || e.getMessage().contains("No data in cache") || e.getMessage().contains("null")) {

			MethodResult isRestarted = clientConnection.restart();
			if (isRestarted.hasError()) {
				clientConnection.resetProgressBar();
				return new MethodResult(isRestarted.getErrorMessage());
			}

			return null;

		}

		if (e.getMessage() == null) {
			Console.writeStackTrace(e);
			return new MethodResult(MessageStrings.ERROR_101);
		}

		clientConnection.resetProgressBar();
		return new MethodResult(e.getMessage());

	}

	
	private MethodResult processException(IOException e) {
		if (isDebug) {
			Console.writeStackTrace(e);
		}
		if (e.getMessage() != null) {
			return new MethodResult(e.getMessage());
		} else {
			return new MethodResult(MessageStrings.ERROR_FILE);
		}
	}

	
	
	
	
	
	public void createCallGroup(int groupSize) {
		clientConnection.createCallGroup(groupSize);
	}


	public boolean isPriceJumpsFilterEnabled() {
		return getParam("isPriceJumpsFilterEnabled").equals("true");

	}



	public String getFromTime() {
		return estimatorData.getFromTime();
	}

	public MethodResult setFromTime(String fromTime) {

		updatePriceData();
		estimatorData.setFromTime("");

		if (fromTime.contains("t")) {
			int daysBack;

			if (fromTime.trim().equals("t")) {
				estimatorData.setFromTime(fromTime);
				return new MethodResult();
			} else {
				String[] a = fromTime.trim().split("-");
				try {

					estimatorData.setFromTime(fromTime);
					return new MethodResult();

				} catch (Exception e) {
					return new MethodResult(String.format(MessageStrings.WRONG_TIME_FORMAT_FROM, fromTime));
				}
			}

		} else if (!fromTime.contains(":")) {

			String s = fromTime.trim() + " 09:30:01";
			try {
				Timestamp t = Timestamp.valueOf(s);
				estimatorData.setFromTime(fromTime);
				return new MethodResult();
			} catch (Exception e) {
				return new MethodResult(String.format(MessageStrings.WRONG_TIME_FORMAT_FROM, fromTime));

			}
		}

		try {
			Timestamp t = Timestamp.valueOf(fromTime);
		} catch (Exception e) {
			return new MethodResult(String.format(MessageStrings.WRONG_TIME_FORMAT_FROM, fromTime));
		}
		estimatorData.setFromTime(fromTime);
		return new MethodResult();
	}

	public String getToTime() {
		return estimatorData.getToTime();
	}

	public void updatePriceData() {
		estimatorData.setPriceID(estimatorData.getNextDataId());
		clearCache();
	}

	public MethodResult setToTime(String toTime) {

		updatePriceData();
		estimatorData.setToTime("");

		if (toTime.contains("t")) {

			if (toTime.trim().equals("t")) {
				estimatorData.setToTime(toTime);
				return new MethodResult();
			}

			else {

				String[] a = toTime.trim().split("-");
				try {
					int daysBack = Integer.parseInt(a[1].split("d")[0]);
					estimatorData.setToTime(toTime);
					return new MethodResult();
				} catch (Exception e) {

					return new MethodResult(String.format(MessageStrings.WRONG_TIME_FORMAT_TO, toTime));

				}
			}

		} else if (!toTime.contains(":")) {

			String s = toTime.trim() + " 09:30:01";
			try {
				Timestamp t = Timestamp.valueOf(s);
				estimatorData.setToTime(toTime);
				return new MethodResult();
			} catch (Exception e) {

				return new MethodResult(String.format(MessageStrings.WRONG_TIME_FORMAT_TO, toTime));

			}
		}

		try {
			Timestamp t = Timestamp.valueOf(toTime);

		} catch (Exception e) {

			return new MethodResult(String.format(MessageStrings.WRONG_TIME_FORMAT_TO, toTime));

		}
		estimatorData.setToTime(toTime);
		return new MethodResult();
	}

	public String getSamplingInterval() {
		return getParam("samplingInterval");
	}

	/**
	 * 
	 * @param samplingIntervalServer
	 *            "all" or "none" - with out sampling ; "Xs" X -seconds; "Xm" X
	 *            -minutes; "Xh" X - hours; "Xd" X -days; "Xw" X -weeks; "Xmo" X
	 *            - months; "Xy" X - years; "last" - only final result
	 */
	public void setSamplingInterval(String samplingIntervalServer) {
		setParam("samplingInterval", samplingIntervalServer);
		clearCache();
	}

	
	
	
	@Override
	protected void finalize() throws Throwable {
		clearCache();
		super.finalize();
	}

	
	
	
	
	
	public String getPriceSamplingInterval() {
		return getParam("priceSamplingInterval");
	}

	public void setPriceSamplingInterval(String priceSamplingInterval) {
		setParam("priceSamplingInterval", priceSamplingInterval);
		clearCache();
	}

	public String getIndexSymbol() {
		return estimatorData.getIndexSymbol();
	}

		public String getFactorModel() {
		return getParam("factorModel");
	}

	public boolean isJumpsModelEnabled() {
		return getParam("isJumpsModelEnabled").equals("true");

	}

	/**
	 * 
	 * @param jumpsModel
	 *            moments none all
	 */
	public void setJumpsModel(String jumpsModel) {
		setParam("jumpsModel", jumpsModel);
		clearCache();
	}

	public String getJumpsModel() {
		return getParam("jumpsModel");
	}

	
	public String getWindowLength() {
		return getParam("windowLength");
	}

	
	
	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public long getNewDataId() {
		return estimatorData.getNextDataId();
	}

	public ClientConnection getClientConnection() {
		return clientConnection;
	}

	public void setClientConnection(ClientConnection clientConnection) {
		this.clientConnection = clientConnection;
	}

	public MethodResult getAllSymbolsList() {

		for (int ii = 0; ii < NUMBER_OF_TRIES; ii++) {
			try {

				return clientConnection.getAllSymbolsList();

			} catch (Exception e) {

				MethodResult result = processException(e);
				if (result == null)
					continue;
				return result;

			}

		}

		return new MethodResult(MessageStrings.FAILED_SERVER_TIME_OUT);

	}

	

}
