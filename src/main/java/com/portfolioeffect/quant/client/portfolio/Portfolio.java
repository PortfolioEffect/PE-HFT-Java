/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
 * %%
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
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

public class Portfolio {

	private static final int MAX_PDF_POINTS = 300;
	private static final int NUMBER_OF_TRIES = 10;
	private boolean isDebug = false;
	private PortfolioData portfolioData = new PortfolioData();

	private HashMap<String, ArrayCache> symbolUserDataMap;
	private HashMap<String, ArrayCache> symbolUserDataTimeMap;

	private Set<String> userData;
	private ClientConnection clientConnection;
	private ArrayList<String> batchMetricsPosition;
	private ArrayList<String> batchMetricsPortfolio;
	private long[] samplingTimes = null;

	private List<CacheKey> cachedValueList;
	private PortfolioCache portfolioCache;

	public Portfolio(Portfolio portfolio) throws Exception {

		this.portfolioData = new PortfolioData(portfolio.portfolioData);
		this.clientConnection = portfolio.clientConnection;

		this.symbolUserDataMap = new HashMap<String, ArrayCache>();
		this.symbolUserDataTimeMap = new HashMap<String, ArrayCache>();

		this.cachedValueList = new ArrayList<CacheKey>();

		this.userData = new HashSet<String>();
		this.isDebug = portfolio.isDebug;
		this.portfolioCache = new PortfolioCache();

		for (String symbol : portfolio.userData) {
			addUserData(symbol, portfolio.symbolUserDataMap.get(symbol).getDoubleArray(), portfolio.symbolUserDataTimeMap.get(symbol).getLongArray());
		}

		batchMetricsPosition = null;
		batchMetricsPortfolio = null;

	}

	public Portfolio(ClientConnection clientConnection) {

		this.portfolioCache = new PortfolioCache();
		this.clientConnection = clientConnection;
		this.symbolUserDataMap = new HashMap<String, ArrayCache>();
		this.symbolUserDataTimeMap = new HashMap<String, ArrayCache>();
		this.cachedValueList = new ArrayList<CacheKey>();
		this.userData = new HashSet<String>();
		this.batchMetricsPosition = null;
		this.batchMetricsPortfolio = null;

		setDefaultParams();

	}

	private void setDefaultParams() {

		// this params set on the server
		// setParam("windowLength","1d");
		// setParam("timeScale","1d");
		// setParam("samplingInterval","1s");
		// setParam("priceSamplingInterval","0s");
		// setParam("shortSalesMode","lintner"); //"markowitz"
		// setParam("jumpsModel","moments");
		// setParam("factorModel","sim"); //direct"
		//
		//
		// setParam("isPriceJumpsFilterEnabled", String.valueOf(false));
		// setParam("isHoldingPeriodEnabled", String.valueOf(false));
		// setParam("isRebalancingHistoryEnabled", String.valueOf(true));
		// setParam("isNoiseModelEnabled", String.valueOf(true));
		// setParam("isJumpsModelEnabled", String.valueOf(true));
		// setParam("isTimeScalingModelEnabled", String.valueOf(true));
		// setParam("isNonGaussianModelEnabled", String.valueOf(true));

		portfolioData.setFromTime("#");
		portfolioData.setToTime("#");

		clearCache();

	}

	private String getMetricTypeList(String metric) throws Exception {

		String result = "";
		try {
			Gson gson = new Gson();
			Type mapTypeMetrics = new TypeToken<HashMap<String, String>>() {
			}.getType();

			HashMap<String, String> metricArgs = gson.fromJson(metric, mapTypeMetrics);
			metricArgs.putAll(portfolioData.getSettings());
			ArrayList<HashMap<String, String>> paramsArgs = new ArrayList<HashMap<String, String>>();
			paramsArgs.add(metricArgs);

			result = gson.toJson(paramsArgs);

		} catch (Exception e) {

			throw new Exception(e.getMessage().split(":")[1]);

		}

		return result;
	}

	private String getMetricTypeList(ArrayList<String> metrics) throws Exception {

		String result = "";

		try {
			Gson gson = new Gson();
			ArrayList<HashMap<String, String>> paramsArgs = new ArrayList<HashMap<String, String>>();

			for (String e : metrics) {

				Type mapTypeMetrics = new TypeToken<HashMap<String, String>>() {
				}.getType();
				HashMap<String, String> metricArgs = gson.fromJson(e, mapTypeMetrics);
				metricArgs.putAll(portfolioData.getSettings());
				paramsArgs.add(metricArgs);

			}

			result = gson.toJson(paramsArgs);

		} catch (Exception e) {

			throw new Exception(e.getMessage().split(":")[1]);

		}

		return result;
	}

	public void setParam(String key, String value) {
		portfolioData.getSettings().put(key, value);
		if (key.equals("samplingInterval")) {
			removeUserData("sampligTimes");
		}
		clearCache();
	}

	public String getParam(String key) {
		if (portfolioData.getSettings().containsKey(key))
			return portfolioData.getSettings().get(key);
		else
			return "";
	}

	public void setPortfolioSettings(Map<String, String> map) {
		portfolioData.setSettings(new HashMap<String, String>(map));

		clearCache();
	}

	public void setPortfolioSettings(String settingsJSON) {
		portfolioData.setSettingJSON(settingsJSON);
		clearCache();
	}

	public HashMap<String, String> getPortfolioSettings() {
		return portfolioData.getSettings();
	}

	public String getPortfolioSettingsJSON() {
		return portfolioData.getSettingJSON();
	}

	public MethodResult addIndex(String assetName) {

		if (portfolioData.getIndexPrice() != null) {

			portfolioData.setIndexPrice(null);
			portfolioData.setIndexTimeMillisec(null);
		}

		portfolioData.getPriceID().put(assetName, portfolioData.getNextDataId());
		portfolioData.setIndexSymbol(assetName);
		clearCache();

		return new MethodResult();

	}

	public MethodResult addIndex(double[] indexPrice, long timeStepMilliSec) {
		long[] timeMilliSec = new long[indexPrice.length];

		for (int i = 0; i < indexPrice.length; i++)
			timeMilliSec[i] = i * timeStepMilliSec + 1000;

		return addIndex(indexPrice, timeMilliSec);
	}

	public MethodResult addIndex(double[] price, long[] timeMilliSec) {

		if (portfolioData.getIndexPrice() != null) {

			portfolioData.setIndexPrice(null);
			portfolioData.setIndexTimeMillisec(null);
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
			portfolioData.setIndexPrice(new ArrayCache(price));

			portfolioData.setIndexTimeMillisec(new ArrayCache(timeMilliSec));

		} catch (IOException e) {
			return processException(e);
		}

		portfolioData.setIndexSymbol("index");
		portfolioData.getPriceID().put("index", portfolioData.getNextDataId());

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

		if (portfolioData.getIndexPrice() != null) {

			portfolioData.setIndexPrice(null);
			portfolioData.setIndexTimeMillisec(null);
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
			portfolioData.setIndexPrice(new ArrayCache(price));

			portfolioData.setIndexTimeMillisec(new ArrayCache(timeMilliSec));

		} catch (IOException e) {
			return processException(e);
		}

		portfolioData.setIndexSymbol("index");
		portfolioData.getPriceID().put("index", portfolioData.getNextDataId());

		clearCache();

		return new MethodResult();
	}

	public ClientConnection getClient() {
		return clientConnection;
	}

	public void clearCache() {
		for (CacheKey key : cachedValueList) {
			try {
				portfolioCache.remove(key);
			} catch (IOException e) {
				processException(e);

			}
		}
		cachedValueList = new ArrayList<CacheKey>();

	}

	public MethodResult addPosition(String[] assetName, int[] quantity) {

		for (int i = 0; i < assetName.length; i++) {
			MethodResult result = addPosition(assetName[i], quantity[i]);
			if (result.hasError()) {

				return result;
			}
		}

		return new MethodResult();
	}

	public MethodResult addPosition(String assetName, int quantity) {

		if (portfolioData.getIndexSymbol() == null)
			return new MethodResult("Add index first");

		int quantityArray[] = new int[1];
		quantityArray[0] = quantity;

		long quantityTime[] = new long[1];
		quantityTime[0] = -1;

		return addPosition(assetName, quantityArray, quantityTime);

	}

	public MethodResult addPosition(String assetName, int[] quantity, long[] timeMillSec) {

		removePositionQuantity(assetName);
		removePositionPrice(assetName);
		portfolioData.getPriceID().put(assetName, portfolioData.getNextDataId());

		if (portfolioData.getIndexSymbol() == null) {
			return new MethodResult("Add index first");
		}

		MethodResult result = addQuantity(assetName, quantity, timeMillSec);
		portfolioData.getSymbolNamesList().add(assetName);
		clearCache();
		return result;
	}

	private MethodResult addQuantity(String assetName, int[] quantity, long[] timeMillSec) {

		portfolioData.getQuantityID().put(assetName, portfolioData.getNextDataId());
		ArrayCache cashQuantity;
		ArrayCache cashQuantityTime;
		
		if(timeMillSec.length!=quantity.length)
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_AND_TIME);

		try {
			cashQuantity = new ArrayCache(quantity);

			portfolioData.getSymbolQuantityMap().put(assetName, cashQuantity);
			cashQuantityTime = new ArrayCache(timeMillSec);

			portfolioData.getSymbolQuantityTimeMap().put(assetName, cashQuantityTime);
		} catch (IOException e) {
			return processException(e);
		}

		clearCache();
		return new MethodResult();
	}

	private MethodResult addQuantity(String assetName, ArrayCache quantity, ArrayCache timeMillSec) {

		portfolioData.getQuantityID().put(assetName, portfolioData.getNextDataId());
		portfolioData.getSymbolQuantityMap().put(assetName, quantity);
		portfolioData.getSymbolQuantityTimeMap().put(assetName, timeMillSec);

		clearCache();
		return new MethodResult();
	}

	public MethodResult addPosition(String assetName, double[] price, int[] quantity, long[] timeMillSec) {
		return addPosition(assetName, price, timeMillSec, quantity, timeMillSec);
	}

	public MethodResult addPosition(String assetName, double[] price, int[] quantity, long timeStepMilliSec) {
		if (price.length != quantity.length) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_AND_TIME);
		}
		long[] timeMilliSec = new long[price.length];

		for (int i = 0; i < price.length; i++)
			timeMilliSec[i] = i * timeStepMilliSec + 1000;

		return addPosition(assetName, price, quantity, timeMilliSec);
	}

	public MethodResult addPosition(String assetName, double[] price, int quantity, long timeStepMilliSec) {
		long[] timeMilliSec = new long[price.length];

		for (int i = 0; i < price.length; i++) {
			timeMilliSec[i] = i * timeStepMilliSec + 1000;
		}

		return addPosition(assetName, price, quantity, timeMilliSec);
	}

	public MethodResult addPosition(String assetName, double[] price, int quantity, long[] priceTimeMillSec) {

		int quantityArray[] = new int[1];
		quantityArray[0] = quantity;

		long quantityTime[] = new long[1];
		quantityTime[0] = -1;

		return addPosition(assetName, price, priceTimeMillSec, quantityArray, quantityTime);
	}

	public MethodResult addPosition(String assetName, float[] price, int quantity, long[] priceTimeMillSec) {

		int quantityArray[] = new int[1];
		quantityArray[0] = quantity;

		long quantityTime[] = new long[1];
		quantityTime[0] = -1;

		return addPosition(assetName, price, priceTimeMillSec, quantityArray, quantityTime);
	}

	public MethodResult addPosition(String assetName, double[] price, long[] priceTimeMillSec, int[] quantity, long[] quantityTimeMillSec) {

		portfolioData.getPriceID().put(assetName, portfolioData.getNextDataId());

		if (price.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE);
		} else if (priceTimeMillSec.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_TIME);
		} else if (quantity.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_QUANTITY);
		} else if (quantityTimeMillSec.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_TIME);
		} else if (portfolioData.getIndexSymbol() == null) {
			return new MethodResult(MessageStrings.ADD_INDEX);
		}
		
		if(quantityTimeMillSec.length!=quantity.length)
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_AND_TIME);
		if(priceTimeMillSec.length!=price.length)
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);

		removePositionQuantity(assetName);
		removePositionPrice(assetName);

		MethodResult resultQuantity = addQuantity(assetName, quantity, quantityTimeMillSec);

		if (resultQuantity.hasError()) {
			return resultQuantity;
		}

		ArrayCache cashPrice;
		ArrayCache cashPriceTime;

		try {
			cashPrice = new ArrayCache(price);

			portfolioData.getSymbolPriceMap().put(assetName, cashPrice);
			cashPriceTime = new ArrayCache(priceTimeMillSec);

			portfolioData.getSymbolPriceTimeMap().put(assetName, cashPriceTime);
		} catch (IOException e) {
			return processException(e);
		}

		portfolioData.getSymbolNamesList().add(assetName);
		portfolioData.getUserPrice().add(assetName);
		clearCache();
		return new MethodResult();
	}

	public MethodResult addPosition(String assetName, float[] price, long[] priceTimeMillSec, int[] quantity, long[] quantityTimeMillSec) {

		portfolioData.getPriceID().put(assetName, portfolioData.getNextDataId());

		if (price.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE);
		} else if (priceTimeMillSec.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE_TIME);
		} else if (quantity.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_QUANTITY);
		} else if (quantityTimeMillSec.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_TIME);
		} else if (portfolioData.getIndexSymbol() == null) {
			return new MethodResult(MessageStrings.ADD_INDEX);
		}
		if(quantityTimeMillSec.length!=quantity.length)
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_AND_TIME);
		if(priceTimeMillSec.length!=price.length)
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);

		removePositionQuantity(assetName);
		removePositionPrice(assetName);

		MethodResult resultQuantity = addQuantity(assetName, quantity, quantityTimeMillSec);

		if (resultQuantity.hasError()) {
			return resultQuantity;
		}

		ArrayCache cashPrice;
		ArrayCache cashPriceTime;

		try {
			cashPrice = new ArrayCache(price);

			portfolioData.getSymbolPriceMap().put(assetName, cashPrice);
			cashPriceTime = new ArrayCache(priceTimeMillSec);

			portfolioData.getSymbolPriceTimeMap().put(assetName, cashPriceTime);
		} catch (IOException e) {
			return processException(e);
		}

		portfolioData.getSymbolNamesList().add(assetName);
		portfolioData.getUserPrice().add(assetName);
		clearCache();
		return new MethodResult();
	}

	public MethodResult addUserData(String dataName, double[] value, long[] timeMillSec) {

		portfolioData.getPriceID().put(dataName, portfolioData.getNextDataId());

		if (value.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_DATA);
		} else if (timeMillSec.length == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_TIME);
		}
		
		if(value.length!=timeMillSec.length)
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_DATA_AND_TIME);
		
		removeUserData(dataName);

		ArrayCache cashData;
		ArrayCache cashTime;

		try {
			cashData = new ArrayCache(value);

			symbolUserDataMap.put(dataName, cashData);
			cashTime = new ArrayCache(timeMillSec);

			symbolUserDataTimeMap.put(dataName, cashTime);

		} catch (IOException e) {
			return processException(e);
		}

		userData.add(dataName);
		clearCache();

		return new MethodResult();
	}

	public MethodResult addUserData(String dataName, ArrayCache value, ArrayCache timeMillSec) {

		portfolioData.getPriceID().put(dataName, portfolioData.getNextDataId());

		if (value.getSize() == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_DATA);
		} else if (timeMillSec.getSize() == 0) {
			return new MethodResult(MessageStrings.WRONG_VECTOR_LEN_TIME);
		}
		
		

		removeUserData(dataName);
		symbolUserDataMap.put(dataName, value);
		symbolUserDataTimeMap.put(dataName, timeMillSec);
		userData.add(dataName);

		clearCache();

		return new MethodResult();
	}

	/**
	 * Remove position from portfolio
	 * 
	 * @param symbol
	 */
	public void removePositionPrice(String symbol) {

		if (portfolioData.getSymbolNamesList().contains(symbol)) {

			if (portfolioData.getUserPrice().contains(symbol)) {
				portfolioData.getUserPrice().remove(symbol);
				portfolioData.getSymbolPriceMap().remove(symbol);
				portfolioData.getSymbolPriceTimeMap().remove(symbol);
				portfolioData.getSymbolNamesList().remove(symbol);
			}

			portfolioData.getSymbolNamesList().remove(symbol);
			clearCache();
		}
	}

	public void removeUserData(String symbol) {
		if (userData.contains(symbol)) {
			userData.remove(symbol);
			symbolUserDataMap.remove(symbol);
			symbolUserDataTimeMap.remove(symbol);
		}
		clearCache();
	}

	public void removePositionQuantity(String symbol) {

		if (portfolioData.getSymbolNamesList().contains(symbol)) {
			portfolioData.getSymbolQuantityMap().remove(symbol);
			portfolioData.getSymbolQuantityTimeMap().remove(symbol);
			clearCache();
		}
	}

	public MethodResult setPositionQuantity(String name, int quantity) {
		int quantityArray[] = new int[1];
		quantityArray[0] = quantity;
		long quantityTime[] = new long[1];
		quantityTime[0] = -1;
		return setPositionQuantity(name, quantityArray, quantityTime);
	}

	public MethodResult setPositionQuantity(String name, int[] quantity, long[] timeMillesc) {
		if (portfolioData.getSymbolNamesList().contains(name)) {
			removePositionQuantity(name);
			return addQuantity(name, quantity, timeMillesc);
		} else {
			return new MethodResult(String.format(MessageStrings.POSITION_NOT_FOUND, name));
		}
	}

	public MethodResult setPositionQuantity(String name, ArrayCache quantity, ArrayCache timeMillesc) {
		if (portfolioData.getSymbolNamesList().contains(name)) {
			removePositionQuantity(name);
			return addQuantity(name, quantity, timeMillesc);
		} else {
			return new MethodResult(String.format(MessageStrings.POSITION_NOT_FOUND, name));
		}
	}

	public MethodResult setPositionQuantity(String name, double[] quantityD, long[] timeMillesc) {

		int[] quantity = new int[quantityD.length];
		for (int i = 0; i < quantity.length; i++) {
			quantity[i] = (int) quantityD[i];
		}
		if (portfolioData.getSymbolNamesList().contains(name)) {
			removePositionQuantity(name);
			return addQuantity(name, quantity, timeMillesc);
		} else {
			return new MethodResult(String.format(MessageStrings.POSITION_NOT_FOUND, name));
		}
	}

	public MethodResult setPositionQuantity(String name, int[] quantity, String[] timeMillesc) {

		if (portfolioData.getUserPrice().contains(name)) {

			double[] price;
			long[] time;

			try {
				price = portfolioData.getSymbolPriceMap().get(name).getDoubleArray();
				time = portfolioData.getSymbolPriceTimeMap().get(name).getLongArray();
			} catch (Exception e) {
				return new MethodResult(e.getMessage());
			}
			return addPosition(name, price, time, quantity, DateTimeUtil.toPOSIXTime(timeMillesc));

		} else {

			return addPosition(name, quantity, DateTimeUtil.toPOSIXTime(timeMillesc));
		}

	}

	public PortfolioData getPortfolioData() {
		return portfolioData;
	}

	public void addMetricToBatch(String metricType) {
		if (batchMetricsPortfolio != null || batchMetricsPosition != null) {
			if (metricType.contains("PORTFOLIO")) {
				batchMetricsPortfolio.add(metricType);
			}
			if (metricType.contains("POSITION") || metricType.contains("INDEX")) {
				batchMetricsPosition.add(metricType);
			}
		}
	}

	public void addMetricToBatch(Map<String, String> metricType) {

		Gson gson = new Gson();
		addMetricToBatch(gson.toJson(metricType));
	}

	public MethodResult runBatch(List<Map<String, String>> metrics) {
		startBatch();
		for (Map<String, String> e : metrics) {
			addMetricToBatch(e);
		}

		return finishBatch();
	}

	public MethodResult runBatchList(List<String> metrics) {
		startBatch();
		for (String e : metrics) {
			addMetricToBatch(e);
		}

		return finishBatch();
	}

	public MethodResult runBatch(String metricsStr) {

		Gson gson = new Gson();
		Type mapType = new TypeToken<List<Map<String, String>>>() {
		}.getType();
		List<Map<String, String>> list = gson.fromJson(metricsStr, mapType);

		return runBatch(list);
	}

	public MethodResult getMetric(Map<String, String> metricType) {

		Gson gson = new Gson();

		MethodResult result = getMetric(gson.toJson(metricType), "");
		return result;

	}

	public MethodResult getMetric(String metricType) {
		MethodResult result = getMetric(metricType, "");
		return result;
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
		MethodResult result = clientConnection.transmitDataList(portfolioData.getFromTime(), portfolioData.getToTime(), dataList, windowLength,
				priceSamplingInterval);

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
					result = clientConnection.transmitUserPrice(ePars[1], portfolioData.getIndexPrice().getFloatArray(), portfolioData.getIndexTimeMillisec()
							.getLongArray());
					if (result.hasError()){
						processNoCashError(result.getErrorMessage());
						return result;
					}
				} else {

					ArrayCache cache = portfolioData.getSymbolPriceMap().get(position);

					if (cache == null)
						cache = symbolUserDataMap.get(position);

					if (cache == null)
						throw new Exception(String.format(MessageStrings.PRICE_FOR_SYMBOL_NO, position));

					float price[] = cache.getFloatArray();

					cache = portfolioData.getSymbolPriceTimeMap().get(position);

					if (cache == null)
						cache = symbolUserDataTimeMap.get(position);

					if (cache == null)
						throw new Exception(String.format(MessageStrings.TIME_FOR_SYMBOL_NO, position));
					long time[] = cache.getLongArray();

					if (price == null || time == null)
						throw new Exception(String.format(MessageStrings.PRICE_FOR_SYMBOL_NO, position));

					result = clientConnection.transmitUserPrice(ePars[1], price, time);
					if (result.hasError()){
						processNoCashError(result.getErrorMessage());
						return result;
					}

				}

			} else if (ePars[0].equals("q")) {
				String position = ePars[1].split(":")[1];

				ArrayCache xq = portfolioData.getSymbolQuantityMap().get(position);

				if (xq == null)
					throw new Exception(String.format(MessageStrings.QUANTITY_FOR_SYMBOL_NO, position));

				int quantity[] = xq.getIntArray();

				ArrayCache xt = portfolioData.getSymbolQuantityTimeMap().get(position);

				if (xt == null)
					throw new Exception(String.format(MessageStrings.TIME_FOR_SYMBOL_NO, position));

				long time[] = xt.getLongArray();

				result = clientConnection.transmitQuantity(ePars[1], quantity, time);
				if (result.hasError()){
					processNoCashError(result.getErrorMessage());
					return result;
				}

			} else {
				return new MethodResult(MessageStrings.ERROR_TRANSMIT);
			}

		}

		return new MethodResult();

	}

	public MethodResult getMetric(String metricType, String params) {

		if (portfolioData.getFromTime().length() == 0 || portfolioData.getToTime().length() == 0) {
			clientConnection.resetProgressBar();
			return new MethodResult("Set time interval  first");
		}

		for (int ii = 0; ii < NUMBER_OF_TRIES; ii++) {

			try {

				if (portfolioData.getSymbolNamesList().size() == 0) {
					clientConnection.resetProgressBar();
					return new MethodResult(MessageStrings.EMPTY_PORTFOLIO);
				}

				String metricTypeFull = getMetricTypeList(metricType);// ,
																		// params);

				String[] positions = null;

				CacheKey key = new CacheKey(metricTypeFull, params);

				if (portfolioCache.containsKey(key)) {

					MethodResult result = new MethodResult();

					result.setData("value", portfolioCache.getMetric(key));
					result.setData("time", portfolioCache.getTime(key));

					return result;
				}

				clientConnection.printProgressBar(0);

				{

					MethodResult result = clientConnection.validateStringRequest(metricTypeFull);
					if (result.hasError())
						throw new Exception(result.getErrorMessage());

					positions = result.getStringArray("positions");
				}

				MethodResult result = null;

				ArrayList<String> positionList = new ArrayList<String>();
				String indexPosition = "";

				if (Arrays.asList(positions).contains("@_ALL_PORTFOLIO_")) {

					String position = "";
					if (metricTypeFull.contains("position")) {

						String[] firstSplit = metricTypeFull.split(":");

						if (firstSplit.length == 2) {
							firstSplit[1] = firstSplit[1].trim();
							if (firstSplit[1].length() != 0) {
								String[] secondSplit = firstSplit[1].split(",");

								for (int i = 0; i < secondSplit.length; i++) {

									String[] thirdSplit = secondSplit[i].split("=");

									if (thirdSplit[0].trim().equals("position")) {

										position = thirdSplit[1].trim();
									}

								}
							}

						}
					}

					if (position.length() != 0 && !portfolioData.getSymbolNamesList().contains(position)) {
						clientConnection.resetProgressBar();
						return new MethodResult(String.format(MessageStrings.POSITION_NOT_FOUND, position));
					}

					if (position.length() != 0) {

						positionList.add(position);
					}

					for (String symbol : portfolioData.getSymbolNamesList()) {
						if (!symbol.equals(position)) {

							positionList.add(symbol);
						}
					}

				} else {

					for (int i = 0; i < positions.length; i++) {
						if (positions[i].equals("@_INDEX_")) {

						} else {
							if (!portfolioData.getSymbolNamesList().contains(positions[i])) {
								clientConnection.resetProgressBar();
								return new MethodResult(String.format(MessageStrings.POSITION_NOT_FOUND, positions[i]));

							}

							positionList.add(positions[i]);

						}
					}

				}

				// ------------------------------
				ArrayList<String> dataList = new ArrayList<String>();
				if (portfolioData.getIndexPrice() == null) {

					dataList.add("hI-" + portfolioData.getPortfolioId() + ":" + portfolioData.getIndexSymbol() + ":"
							+ portfolioData.getPriceID().get(portfolioData.getIndexSymbol()));

					indexPosition = portfolioData.getIndexSymbol();
				} else {

					dataList.add("u-" + portfolioData.getPortfolioId() + ":" + "index" + ":" + portfolioData.getPriceID().get(portfolioData.getIndexSymbol()));

					indexPosition = "index";

				}

				for (String symbol : portfolioData.getSymbolNamesList()) {
					if (portfolioData.getUserPrice().contains(symbol)) {

						dataList.add("u-" + portfolioData.getPortfolioId() + ":" + symbol + ":" + portfolioData.getPriceID().get(symbol));

					} else {

						dataList.add("h-" + portfolioData.getPortfolioId() + ":" + symbol + ":" + portfolioData.getPriceID().get(symbol));
					}

					dataList.add("q-" + portfolioData.getPortfolioId() + ":" + symbol + ":" + portfolioData.getQuantityID().get(symbol));

				}

				// ------------------------------

				ArrayList<String> positionNames = new ArrayList<String>();
				for (String e : positionList) {
					if (portfolioData.getUserPrice().contains(e))
						positionNames.add(portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getPriceID().get(e) + "="
								+ portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getQuantityID().get(e));
					else
						positionNames.add("h-" + portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getPriceID().get(e) + "="
								+ portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getQuantityID().get(e));

				}

				if (indexPosition.length() != 0) {
					if (portfolioData.getIndexPrice() != null)
						indexPosition = portfolioData.getPortfolioId() + ":" + indexPosition + ":" + portfolioData.getPriceID().get(indexPosition) + "="
								+ portfolioData.getPortfolioId() + ":" + indexPosition + ":" + portfolioData.getQuantityID().get(indexPosition);
					else
						indexPosition = "hI-" + portfolioData.getPortfolioId() + ":" + indexPosition + ":" + portfolioData.getPriceID().get(indexPosition)
								+ "=" + portfolioData.getPortfolioId() + ":" + indexPosition + ":" + portfolioData.getQuantityID().get(indexPosition);
				}

				for (String e : userData) {

					dataList.add("u-" + portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getPriceID().get(e));

					positionNames.add(portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getPriceID().get(e));

				}

				MethodResult resultTransmit = transmitData(dataList);

				if (resultTransmit.hasError()) {
					clientConnection.resetProgressBar();
					return new MethodResult(resultTransmit.getErrorMessage());
				}

				if (isDebug) {

					Console.writeln("\n " + metricTypeFull);
					Console.writeln(indexPosition);
					Console.writeln(positionNames + "\n");

				}
				result = clientConnection.estimateTransactional(metricTypeFull, indexPosition, positionNames, params);

				if (!result.hasError()) {

					portfolioCache.addMetric(key, result.getDataArrayCache("value"));
					portfolioCache.addTime(key, result.getDataArrayCache("time"));
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

	public void clearBatchMetricT() {
		batchMetricsPortfolio.clear();
		batchMetricsPosition.clear();
		batchMetricsPortfolio = null;
		batchMetricsPosition = null;
	}

	public MethodResult finishBatch() {

		if (batchMetricsPortfolio == null && batchMetricsPosition == null)
			return new MethodResult();

		String factorModel = portfolioData.getParam("factorModel");
		if (factorModel == "sim" || factorModel == null) {

			batchMetricsPortfolio.addAll(batchMetricsPosition);
			MethodResult result = computeBatch(batchMetricsPortfolio);
			clearBatchMetricT();
			return result;
		} else {

			MethodResult result;

			if (batchMetricsPortfolio.size() != 0 && batchMetricsPosition.size() != 0) {

				clientConnection.createCallGroup(2);
				result = computeBatch(batchMetricsPortfolio);
				if (result.hasError()) {
					clearBatchMetricT();
					clientConnection.createCallGroup(1);
					return result;
				}

				result = computeBatch(batchMetricsPosition);
				clearBatchMetricT();
				clientConnection.createCallGroup(1);
				return result;
			} else {

				batchMetricsPortfolio.addAll(batchMetricsPosition);
				result = computeBatch(batchMetricsPortfolio);
				clearBatchMetricT();
				return result;
			}
		}
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

	private MethodResult computeBatch(ArrayList<String> batchMetrics) {

		ArrayList<String> metricsTypeNewList = new ArrayList<String>();
		for (String e : batchMetrics) {
			CacheKey key;
			try {
				String metricTypeFull = getMetricTypeList(e);
				key = new CacheKey(metricTypeFull, "");

			} catch (Exception e1) {
				return processException(e1);
			}
			if (portfolioCache.containsKey(key))
				continue;
			else
				metricsTypeNewList.add(e);
		}

		if (metricsTypeNewList.size() == 0) {
			return new MethodResult();
		}

		batchMetrics.clear();
		clientConnection.printProgressBar(0);

		if (portfolioData.getFromTime().length() == 0 || portfolioData.getToTime().length() == 0) {
			clientConnection.resetProgressBar();
			return new MethodResult("Set time interval  first");
		}

		for (int ii = 0; ii < NUMBER_OF_TRIES; ii++) {
			try {
				String metricsType = getMetricTypeList(metricsTypeNewList);
				if (portfolioData.getSymbolNamesList().size() == 0) {
					clientConnection.resetProgressBar();
					return new MethodResult(MessageStrings.EMPTY_PORTFOLIO);
				}
				String[] positions = null;
				{

					// System.out.println("validate-->");

					MethodResult result = clientConnection.validateStringRequest(metricsType);
					// System.out.println(">---validate");

					if (result.hasError()) {
						throw new Exception(result.getErrorMessage());
					}

					positions = result.getStringArray("positions");

				}
				MethodResult result = null;

				ArrayList<String> positionList = new ArrayList<String>();
				String indexPosition = "";

				if (Arrays.asList(positions).contains("@_ALL_PORTFOLIO_")) {
					for (String symbol : portfolioData.getSymbolNamesList()) {
						positionList.add(symbol);
					}
				} else {
					for (int i = 0; i < positions.length; i++) {
						if (positions[i].equals("@_INDEX_")) {
						} else {
							if (!portfolioData.getSymbolNamesList().contains(positions[i])) {
								clientConnection.resetProgressBar();
								return new MethodResult(String.format(MessageStrings.POSITION_NOT_FOUND, positions[i]));
							}
							positionList.add(positions[i]);
						}
					}
				}

				ArrayList<String> positionNames = new ArrayList<String>();
				for (String e : positionList) {
					if (portfolioData.getUserPrice().contains(e)) {
						positionNames.add(portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getPriceID().get(e) + "="
								+ portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getQuantityID().get(e));
					} else {
						positionNames.add("h-" + portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getPriceID().get(e) + "="
								+ portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getQuantityID().get(e));
					}
				}

				ArrayList<String> dataList = new ArrayList<String>();
				if (portfolioData.getIndexPrice() == null) {
					dataList.add("hI-" + portfolioData.getPortfolioId() + ":" + portfolioData.getIndexSymbol() + ":"
							+ portfolioData.getPriceID().get(portfolioData.getIndexSymbol()));
					indexPosition = portfolioData.getIndexSymbol();
				} else {
					dataList.add("u-" + portfolioData.getPortfolioId() + ":" + "index" + ":" + portfolioData.getPriceID().get(portfolioData.getIndexSymbol()));
					indexPosition = "index";
				}

				for (String symbol : portfolioData.getSymbolNamesList()) {
					if (portfolioData.getUserPrice().contains(symbol)) {
						dataList.add("u-" + portfolioData.getPortfolioId() + ":" + symbol + ":" + portfolioData.getPriceID().get(symbol));
					} else {
						dataList.add("h-" + portfolioData.getPortfolioId() + ":" + symbol + ":" + portfolioData.getPriceID().get(symbol));
					}

					dataList.add("q-" + portfolioData.getPortfolioId() + ":" + symbol + ":" + portfolioData.getQuantityID().get(symbol));
				}

				// ------------------------------

				if (indexPosition.length() != 0) {
					if (portfolioData.getIndexPrice() != null) {
						indexPosition = portfolioData.getPortfolioId() + ":" + indexPosition + ":" + portfolioData.getPriceID().get(indexPosition) + "="
								+ portfolioData.getPortfolioId() + ":" + indexPosition + ":" + portfolioData.getQuantityID().get(indexPosition);
					} else {
						indexPosition = "hI-" + portfolioData.getPortfolioId() + ":" + indexPosition + ":" + portfolioData.getPriceID().get(indexPosition)
								+ "=" + portfolioData.getPortfolioId() + ":" + indexPosition + ":" + portfolioData.getQuantityID().get(indexPosition);
					}
				}

				for (String e : userData) {
					dataList.add("u-" + portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getPriceID().get(e));
				}

				for (String e : userData) {
					dataList.add("u-" + portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getPriceID().get(e));

					positionNames.add(portfolioData.getPortfolioId() + ":" + e + ":" + portfolioData.getPriceID().get(e));

				}

				MethodResult resultTransmit = transmitData(dataList);

				if (resultTransmit.hasError()) {
					return new MethodResult(resultTransmit.getErrorMessage());
				}
				if (isDebug) {

					Console.writeln("\n " + metricsType);
					Console.writeln(indexPosition);
					Console.writeln(positionNames + "\n");

				}

				result = clientConnection.estimateTransactional(metricsType, indexPosition, positionNames, "");

				if (!result.hasError()) {
					ArrayCache batchValues[] = ArrayCache.splitBatchDouble(result.getDataArrayCache("value"));

					for (int k = 0; k < metricsTypeNewList.size(); k++) {
						String metricTypeFull = getMetricTypeList(metricsTypeNewList.get(k));
						CacheKey key = new CacheKey(metricTypeFull, "");
						portfolioCache.addMetric(key, batchValues[k]);
						portfolioCache.addTime(key, ArrayCache.copyArrayCacheLong(result.getDataArrayCache("time")));
						cachedValueList.add(key);
					}

					metricsTypeNewList.clear();

				} else {
					clientConnection.createCallGroup(1);
				}

				return new MethodResult();
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

	public MethodResult getPDF(double from, double to, int number) throws Exception {
		return getPDF(from, to, number, "");
	}

	public MethodResult getPDF(int number) throws Exception {
		return getPDF(0, 1, number, "");
	}

	public MethodResult getPDF(int number, String position) throws Exception {
		return getPDF(0, 1, number, position);

	}

	public MethodResult getPDF(double from, double to, int number, String position) throws Exception {
		if (getSymbolNamesList().size() == 0)
			return new MethodResult(MessageStrings.EMPTY_PORTFOLIO);

		number = number > MAX_PDF_POINTS ? 300 : number;

		HashMap<String, String> params = new HashMap<String, String>();

		String sampling = getSamplingInterval();

		Gson gson = new Gson();

		MethodResult result;
		if (position.length() == 0) {
			params.put("metric", "PORTFOLIO_PDF");
		} else {
			params.put("metric", "POSITION_PDF");
			params.put("position", position);
		}

		params.put("from", "" + from);
		params.put("to", "" + to);
		params.put("number", "" + number);

		result = getMetric(gson.toJson(params));

		long[] time = null;

		double[][] PDF = null;
		double[][] x = null;

		if (!result.hasError()) {

			time = result.getLongArray("time");
			PDF = new double[time.length][number];
			x = new double[time.length][number];
			ArrayCache qResult = result.getDataArrayCache("value");

			try {
				qResult.openInput();

				for (int j = 0; j < time.length; j++)
					for (int i = 0; i < number; i++) {
						x[j][i] = qResult.getNextDouble();
						PDF[j][i] = qResult.getNextDouble();

					}

				qResult.closeInput();
			} catch (Exception e) {
				if (isDebug())
					Console.writeStackTrace(e);
				if (e.getMessage() != null)
					return new MethodResult(e.getMessage());
				else
					return new MethodResult(MessageStrings.ERROR_FILE);
			}

		} else {
			return new MethodResult(result.getErrorMessage());
		}

		if (sampling.equals("last")) {

			double[][] newPDF = new double[1][number];
			double[][] newX = new double[1][number];

			for (int i = 0; i < number; i++) {
				newPDF[0][i] = PDF[PDF.length - 1][i];
				newX[0][i] = x[x.length - 1][i];
			}

			PDF = newPDF;
			x = newX;
			long newTime[] = new long[] { time[time.length - 1] };
			time = newTime;
		}

		getClient().resetProgressBar();

		MethodResult resultP = new MethodResult();
		resultP.setData("time", new ArrayCache(time));
		resultP.setData("x", new ArrayCache(x));
		resultP.setData("pdf", new ArrayCache(PDF));

		return resultP;
	}

	public MethodResult getPositionQuantity(String symbol) {

		if (portfolioData.getSymbolQuantityMap().containsKey(symbol))
			try {
				double[] value = portfolioData.getSymbolQuantityMap().get(symbol).getIntAsDoubleArray();
				long[] time = portfolioData.getSymbolQuantityTimeMap().get(symbol).getLongArray();
				String samplingInterval = getParam("samplingInterval");

				if (samplingInterval.equals("last")) {

					MethodResult result = new MethodResult();
					result.setData("value", new ArrayCache(new double[] { value[value.length - 1] }));
					result.setData("time", new ArrayCache(new long[] { time[time.length - 1] }));

					return result;

				}

				MethodResult result = new MethodResult();
				result.setData("value", new ArrayCache(value));
				result.setData("time", new ArrayCache(time));

				return result;

			} catch (Exception e) {
				return processException(e);
			}
		else {
			return new MethodResult(String.format(MessageStrings.POSITION_NOT_FOUND, symbol));
		}
	}

	/**
	 * Returns an unmodifiable list of symbols that have non-zero position
	 * weights in the portfolio
	 * 
	 * @return symbol array
	 */
	public String[] getSymbols() {
		String[] names = new String[portfolioData.getSymbolNamesList().size()];
		int i = 0;
		for (String name : portfolioData.getSymbolNamesList()) {
			names[i] = name;
			i++;
		}
		return names;
	}

	public int[] getQuantities() throws Exception {

		String[] positions = getSymbols();
		int[] quantities = new int[positions.length];

		for (int i = 0; i < positions.length; i++) {
			quantities[i] = portfolioData.getSymbolQuantityMap().get(positions[i]).getIntArray()[0];
		}

		return quantities;
	}

	public void createCallGroup(int groupSize) {
		clientConnection.createCallGroup(groupSize);
	}

	public List<String> getSymbolNamesList() {
		return portfolioData.getSymbolNamesList();
	}

	public Map<String, ArrayCache> getSymbolQuantityMap() {
		return portfolioData.getSymbolQuantityMap();
	}

	public boolean isPriceJumpsFilterEnabled() {
		return getParam("isPriceJumpsFilterEnabled").equals("true");

	}

	public boolean isHoldingPeriodEnabled() {
		return getParam("isHoldingPeriodEnabled").equals("true");
	}

	public void setHoldingPeriodEnabled(boolean isHoldingPeriodEnabled) {
		setParam("isHoldingPeriodEnabled", String.valueOf(isHoldingPeriodEnabled));
	}

	public String getFromTime() {
		return portfolioData.getFromTime();
	}

	public MethodResult setFromTime(String fromTime) {

		updatePriceData();
		portfolioData.setFromTime("");

		if (fromTime.contains("t")) {
			int daysBack;

			if (fromTime.trim().equals("t")) {
				portfolioData.setFromTime(fromTime);
				return new MethodResult();
			} else {
				String[] a = fromTime.trim().split("-");
				try {

					portfolioData.setFromTime(fromTime);
					return new MethodResult();

				} catch (Exception e) {
					return new MethodResult(String.format(MessageStrings.WRONG_TIME_FORMAT_FROM, fromTime));
				}
			}

		} else if (!fromTime.contains(":")) {

			String s = fromTime.trim() + " 09:30:01";
			try {
				Timestamp t = Timestamp.valueOf(s);
				portfolioData.setFromTime(fromTime);
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
		portfolioData.setFromTime(fromTime);
		return new MethodResult();
	}

	public String getToTime() {
		return portfolioData.getToTime();
	}

	public void updatePriceData() {
		for (String e : portfolioData.getPriceID().keySet()) {
			if (!portfolioData.getUserPrice().contains(e))
				portfolioData.getPriceID().put(e, portfolioData.getNextDataId());

		}
		clearCache();
	}

	public MethodResult setToTime(String toTime) {

		updatePriceData();
		portfolioData.setToTime("");

		if (toTime.contains("t")) {

			if (toTime.trim().equals("t")) {
				portfolioData.setToTime(toTime);
				return new MethodResult();
			}

			else {

				String[] a = toTime.trim().split("-");
				try {
					int daysBack = Integer.parseInt(a[1].split("d")[0]);
					portfolioData.setToTime(toTime);
					return new MethodResult();
				} catch (Exception e) {

					return new MethodResult(String.format(MessageStrings.WRONG_TIME_FORMAT_TO, toTime));

				}
			}

		} else if (!toTime.contains(":")) {

			String s = toTime.trim() + " 09:30:01";
			try {
				Timestamp t = Timestamp.valueOf(s);
				portfolioData.setToTime(toTime);
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
		portfolioData.setToTime(toTime);
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
		removeUserData("sampligTimes");
		samplingTimes = null;
		clearCache();
	}

	public long[] getSamplingIntervalArray() {
		return samplingTimes;
	}

	public void setSamplingInterval(long[] samplingTimes) {

		this.samplingTimes = samplingTimes;
		double[] value = new double[samplingTimes.length + 1];
		long[] time = new long[samplingTimes.length + 1];

		value[0] = 0;
		time[0] = samplingTimes[0] - 1000;

		for (int i = 1; i < value.length; i++) {
			value[i] = i;
			time[i] = samplingTimes[i - 1];
		}

		setParam("samplingInterval", "all");
		addUserData("sampligTimes", value, time);
		clearCache();
	}

	public void startBatch() {
		batchMetricsPosition = new ArrayList<String>();
		batchMetricsPortfolio = new ArrayList<String>();
	}

	@Override
	protected void finalize() throws Throwable {
		clearCache();
		super.finalize();
	}

	public boolean isNoiseModelEnabled() {
		return getParam("isNoiseModelEnabled").equals("true");
	}

	public void setNoiseModelEnabled(boolean isNoiseFilterEnabled) {
		setParam("isNoiseModelEnabled", String.valueOf(isNoiseFilterEnabled));
		clearCache();
	}

	public boolean isNonGaussianModelEnabled() {
		return getParam("isNonGaussianModelEnabled").equals("true");
	}

	public void setNonGaussianModelEnabled(boolean isNonGaussianModelEnabled) {
		setParam("isNonGaussianModelEnabled", String.valueOf(isNonGaussianModelEnabled));
		clearCache();
	}

	public String getShortSalesMode() {
		return getParam("shortSalesMode");
	}

	/**
	 * 
	 * @param shortSalesMode
	 *            "lintner" "markowitz"
	 */
	public void setShortSalesMode(String shortSalesMode) {
		setParam("shortSalesMode", shortSalesMode);
		clearCache();
	}

	public String getPriceSamplingInterval() {
		return getParam("priceSamplingInterval");
	}

	public void setPriceSamplingInterval(String priceSamplingInterval) {
		setParam("priceSamplingInterval", priceSamplingInterval);
		clearCache();
	}

	public String getIndexSymbol() {
		return portfolioData.getIndexSymbol();
	}

	public void setFactorModel(String factorModel) {
		setParam("factorModel", factorModel);
		clearCache();
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

	public void setWindowLength(String windowLengthString) {
		setParam("windowLength", windowLengthString);
		clearCache();

	}

	public String getWindowLength() {
		return getParam("windowLength");
	}

	public void setTimeScale(String timeScaleSecString) {
		setParam("timeScale", timeScaleSecString);
		clearCache();
	}

	public String getTimeScale() {
		return getParam("timeScale");
	}

	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	public long getNewDataId() {
		return portfolioData.getNextDataId();
	}

	public ClientConnection getClientConnection() {
		return clientConnection;
	}

	public void setClientConnection(ClientConnection clientConnection) {
		this.clientConnection = clientConnection;
	}

	public void setTxnCostPerShare(double value) {
		setParam("txnCostPerShare", String.valueOf(value));
	}

	public double getTxnCostPerShare() {
		return Double.valueOf(getParam("txnCostPerShare"));
	}

	public void setTxnCostFixed(double value) {
		setParam("txnCostFixed", String.valueOf(value));
	}

	public double getTxnCostFixed() {
		return Double.valueOf(getParam("txnCostFixed"));
	}

	public boolean isDriftEnabled() {
		return Boolean.valueOf(getParam("isDriftEnabled"));
	}

	public void setDriftEnabled(boolean isDriftEnabled) {
		setParam("isDriftEnabled", String.valueOf(isDriftEnabled));
	}

	public void setDensityApproxModel(String densityApproxModel) {
		setParam("densityApproxModel", densityApproxModel);
	}

	public String getDensityApproxModelString() {
		return getParam("densityApproxModel");
	}

	public void setPortfolioMetricsMode(String mode) {
		setParam("portfolioMetricsMode", mode);
	}

	public String getPortfolioMetricsMode() {
		return getParam("portfolioMetricsMode");
	}

	public void setStartWhenBurn(boolean mode) {
		setParam("isStartWhenBurn", "" + mode);
	}

	public boolean isStartWhenBurn() {
		return Boolean.valueOf(getParam("isStartWhenBurn"));
	}

}
