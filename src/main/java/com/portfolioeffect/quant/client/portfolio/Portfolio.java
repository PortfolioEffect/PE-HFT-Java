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

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;

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
import java.util.concurrent.atomic.AtomicLong;

import javax.management.RuntimeErrorException;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolioeffect.quant.client.ClientConnection;
import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.model.ConnectFailedException;
import com.portfolioeffect.quant.client.result.LazyMetric;
import com.portfolioeffect.quant.client.result.Metric;
import com.portfolioeffect.quant.client.util.Console;
import com.portfolioeffect.quant.client.util.DateTimeUtil;
import com.portfolioeffect.quant.client.util.LazyMetricBuilder;
import com.portfolioeffect.quant.client.util.MessageStrings;
import com.portfolioeffect.quant.client.util.MetricUpdateCallback;
import com.portfolioeffect.quant.client.util.SimpleMetricUpdateCallback;
import com.portfolioeffect.quant.client.util.Util;

public class Portfolio {

	public boolean isBatchOn() {
		return container.isBatchOn;
	}

	public void setAutoBatchEnabled(boolean isEnabled) {
		container.isBatchOn = isEnabled;
	}

	private PortfolioContainer container;

	public void del() {
		container = null;
	}

	public Portfolio(Portfolio portfolio) throws IOException{

		this(new PortfolioContainer(portfolio.container));

	}

	public Portfolio(PortfolioContainer portfolio) {

		container = portfolio;

	}

	public Portfolio(ClientConnection clientConnection) {

		container = new PortfolioContainer(clientConnection);

		setDefaultParams();

	}

	public Portfolio(ClientConnection clientConnection, String fromTime, String toTime, String indexName) {

		container = new PortfolioContainer(clientConnection);

		setDefaultParams();
		setFromTime(fromTime);
		setToTime(toTime);
		addIndex(indexName);
	}

	public Portfolio(ClientConnection clientConnection, String fromTime, String toTime) {
		this(clientConnection, fromTime, toTime, "SPY");
	}

	private void setDefaultParams() {
		clearCache();

		container.portfolioData.setFromTime("#");
		container.portfolioData.setToTime("#");	

	}

	private String getMetricTypeList(String metric) throws Exception {

		String result = "";
		try {
			Gson gson = new Gson();
			Type mapTypeMetrics = new TypeToken<HashMap<String, String>>() {
			}.getType();

			HashMap<String, String> metricArgs = gson.fromJson(metric, mapTypeMetrics);
			// metricArgs.putAll(container.portfolioData.getSettings());
			ArrayList<HashMap<String, String>> paramsArgs = new ArrayList<HashMap<String, String>>();
			paramsArgs.add(metricArgs);

			result = gson.toJson(paramsArgs);

		} catch (Exception e) {

			throw new Exception(e.getMessage().split(":")[1]);

		}

		return result;
	}

	private String getFullMetricType(String metric) throws Exception {
		//
		// if(metric.contains("isFullMetricType"))
		// return metric;

		String result = "";
		try {
			Gson gson = new Gson();
			Type mapTypeMetrics = new TypeToken<HashMap<String, String>>() {
			}.getType();

			HashMap<String, String> metricArgsGson = gson.fromJson(metric, mapTypeMetrics);
			// metricArgsGson.put("isFullMetricType", "true");
			HashMap<String, String> metricArgs = new HashMap<String, String>(container.portfolioData.getSettingsReal());
			// metricArgs.putAll(metricArgsGson);

			for (String e : metricArgs.keySet())
				Util.putIfAbsent(metricArgsGson, e, metricArgs.get(e));
			// metricArgsGson.putIfAbsent(e, metricArgs.get(e));

			result = gson.toJson(metricArgsGson);

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
				HashMap<String, String> metricArgsGson = gson.fromJson(e, mapTypeMetrics);
				// HashMap<String,String> metricArgs = new HashMap<String,
				// String>(container.portfolioData.getSettings());
				// metricArgs.putAll(metricArgsGson);
				// paramsArgs.add(metricArgs);
				paramsArgs.add(metricArgsGson);

			}

			result = gson.toJson(paramsArgs);

		} catch (Exception e) {

			throw new Exception(e.getMessage().split(":")[1]);

		}

		return result;
	}

	public boolean isMultiBatch() {
		return container.isMultiBatch;
	}

	public void setMultiBatchEnabled(boolean isMultiBatch) {
		container.isMultiBatch = isMultiBatch;
	}

	public void setParam(String key, String value) {
		
		if(container.portfolioData.containsParam(key) && container.portfolioData.getParam(key).equals(value))
			return;

		if (!container.isMultiBatch || container.portfolioData.checkEstimatorParams(key))
			clearCache();

		if (key.equals("windowLength")) {
			updateBurnWindowLength( Portfolio.parseWindowLength(value) );
			// int w= parseWindowLength(value);
			// container.windowLength = Math.max(container.windowLength, w);
		}

		container.portfolioData.setParam(key, value);
		
		if (key.equals("samplingInterval")) {
			removeUserData("sampligTimes");
		}

	}

	private void updateBurnWindowLength(int windowLength) {

		
		container.windowLength = Math.max(container.windowLength, windowLength);
	}

		
	
	private void updateBurnWindowLength(String windowLength, String rollingWindow) {

		int w = parseWindowLength(windowLength);
		int rw = parseWindowLength(rollingWindow);
		container.windowLength = Math.max(container.windowLength, w + rw);
	}

	public void removeParam(String key) {
		clearCache();
		container.portfolioData.removeParam(key);

	}

	public String getParam(String key) {
		if (container.portfolioData.containsParam(key))
			return container.portfolioData.getParam(key);
		else
			return "";
	}

	public void setPortfolioSettings(Map<String, String> map) {
		clearCache();
		container.portfolioData.setSettings(new HashMap<String, String>(map));

	}

	public void setPortfolioSettings(String settingsJSON) {
		clearCache();
		container.portfolioData.setSettingJSON(settingsJSON);

	}

	public HashMap<String, String> getPortfolioSettingsReal() {
		return container.portfolioData.getSettingsReal();
	}
	
	public HashMap<String, String> getPortfolioSettingsR() {
		return container.portfolioData.getSettings();
	}

	public String getPortfolioSettingsJSON() {
		return container.portfolioData.getSettingJSON();
	}

	public Metric addIndex(String assetName) {
		clearCache();

		if (container.portfolioData.getIndexPrice() != null) {

			container.portfolioData.setIndexPrice(null);
			container.portfolioData.setIndexTimeMillisec(null);
		}

		container.portfolioData.getPriceID().put(assetName, container.portfolioData.getNextDataId());
		container.portfolioData.setIndexSymbol(assetName);

		return new Metric();

	}

	public Metric addIndex(TDoubleArrayList indexPrice, long timeStepMilliSec) {
		return  addIndex( indexPrice.toArray(),  timeStepMilliSec);
	}
	
	public Metric addIndex(double[] indexPrice, long timeStepMilliSec) {
		clearCache();
		long[] timeMilliSec = new long[indexPrice.length];

		for (int i = 0; i < indexPrice.length; i++)
			timeMilliSec[i] = i * timeStepMilliSec + 1000;

		return addIndex(indexPrice, timeMilliSec);
	}
	
	public Metric addIndex(TDoubleArrayList indexPrice, TLongArrayList timeStepMilliSec) {
		return  addIndex( indexPrice.toArray(),  timeStepMilliSec.toArray());
	}
	public Metric addIndex(double[] price, long[] timeMilliSec) {
		clearCache();

		if (container.portfolioData.getIndexPrice() != null) {

			container.portfolioData.setIndexPrice(null);
			container.portfolioData.setIndexTimeMillisec(null);
		}

		if (price.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_PRICE);
		}
		if (timeMilliSec.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_TIME);
		}

		if (timeMilliSec.length != price.length)
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);

		try {
			container.portfolioData.setIndexPrice(new ArrayCache(price));

			container.portfolioData.setIndexTimeMillisec(new ArrayCache(timeMilliSec));

		} catch (IOException e) {
			return processException(e);
		}

		container.portfolioData.setIndexSymbol("index");
		container.portfolioData.getPriceID().put("index", container.portfolioData.getNextDataId());

		return new Metric();
	}

	public Metric addIndex(TFloatArrayList indexPrice, long timeStepMilliSec) {
		return  addIndex( indexPrice.toArray(),  timeStepMilliSec);
	}
	
	public Metric addIndex(float[] indexPrice, long timeStepMilliSec) {
		clearCache();
		long[] timeMilliSec = new long[indexPrice.length];

		for (int i = 0; i < indexPrice.length; i++)
			timeMilliSec[i] = i * timeStepMilliSec + 1000;

		return addIndex(indexPrice, timeMilliSec);
	}

	
	public Metric addIndex(TFloatArrayList indexPrice, TLongArrayList timeStepMilliSec) {
		return  addIndex( indexPrice.toArray(),  timeStepMilliSec.toArray());
	}
	
	public Metric addIndex(float[] price, long[] timeMilliSec) {
		clearCache();

		if (container.portfolioData.getIndexPrice() != null) {

			container.portfolioData.setIndexPrice(null);
			container.portfolioData.setIndexTimeMillisec(null);
		}

		if (price.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_PRICE);
		}
		if (timeMilliSec.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_TIME);
		}
		if (timeMilliSec.length != price.length)
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);

		try {
			container.portfolioData.setIndexPrice(new ArrayCache(price));

			container.portfolioData.setIndexTimeMillisec(new ArrayCache(timeMilliSec));

		} catch (IOException e) {
			return processException(e);
		}

		container.portfolioData.setIndexSymbol("index");
		container.portfolioData.getPriceID().put("index", container.portfolioData.getNextDataId());

		return new Metric();
	}

	public ClientConnection getClient() {
		return container.clientConnection;
	}

	public void clearCache() {

		try {
			container = new PortfolioContainer(container);
		} catch (IOException e1) {

		}

		container.clearCache();

	}

	public Metric addPosition(String[] assetName, int[] quantity) {
		clearCache();

		for (int i = 0; i < assetName.length; i++) {
			Metric result = addPosition(assetName[i], quantity[i]);
			if (result.hasError()) {

				return result;
			}
		}

		return new Metric();
	}

	public Metric addPosition(String assetName, int quantity) {
		clearCache();

		if (container.portfolioData.getIndexSymbol() == null)
			return new Metric("Add index first");

		int quantityArray[] = new int[1];
		quantityArray[0] = quantity;

		long quantityTime[] = new long[1];
		quantityTime[0] = -1;

		return addPosition(assetName, quantityArray, quantityTime);

	}

	public Metric addPosition(String assetName, int[] quantity, long[] timeMillSec) {
		clearCache();

		removePositionQuantity(assetName);
		removePositionPrice(assetName);
		container.portfolioData.getPriceID().put(assetName, container.portfolioData.getNextDataId());

		if (container.portfolioData.getIndexSymbol() == null) {
			return new Metric("Add index first");
		}

		Metric result = addQuantity(assetName, quantity, timeMillSec);
		container.portfolioData.getSymbolNamesList().add(assetName);

		return result;
	}

	public Metric setStreamQuantity(String assetName, int quantity) {
		return setStreamQuantity(assetName, quantity, System.currentTimeMillis() + DateTimeUtil.CLIENT_TIME_DELTA);
	}

	public Metric setStreamQuantity(String assetName, int quantity, String time) {
		return setStreamQuantity(assetName, quantity, DateTimeUtil.toPOSIXTimeWithDelta(time)[0]);
	}

	public Metric setStreamQuantity(String assetName, int quantity, long timeMilles) {

		try {

			container.clientConnection.transmitStreamQuantity(assetName, quantity, timeMilles);

			container.portfolioData.getSymbolQuantityMap().get(assetName).writeAsLong(new int[] { quantity });
			container.portfolioData.getSymbolQuantityTimeMap().get(assetName).write(new long[] { timeMilles });
			Thread.sleep(10);

		} catch (Exception e) {

			return processException(e);

		}

		return new Metric();
	}

	private Metric addQuantity(String assetName, int[] quantity, long[] timeMillSec) {
		clearCache();

		container.portfolioData.getQuantityID().put(assetName, container.portfolioData.getNextDataId());
		ArrayCache cashQuantity;
		ArrayCache cashQuantityTime;

		if (timeMillSec.length != quantity.length)
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_AND_TIME);

		try {
			cashQuantity = new ArrayCache(quantity);

			container.portfolioData.getSymbolQuantityMap().put(assetName, cashQuantity);
			cashQuantityTime = new ArrayCache(timeMillSec);

			container.portfolioData.getSymbolQuantityTimeMap().put(assetName, cashQuantityTime);
		} catch (IOException e) {
			return processException(e);
		}

		return new Metric();
	}

	private Metric addQuantity(String assetName, ArrayCache quantity, ArrayCache timeMillSec) {
		clearCache();

		container.portfolioData.getQuantityID().put(assetName, container.portfolioData.getNextDataId());
		container.portfolioData.getSymbolQuantityMap().put(assetName, quantity);
		container.portfolioData.getSymbolQuantityTimeMap().put(assetName, timeMillSec);

		return new Metric();
	}

	public Metric addPosition(String assetName, double[] price, int[] quantity, long[] timeMillSec) {
		return addPosition(assetName, price, timeMillSec, quantity, timeMillSec);
	}

	public Metric addPosition(String assetName, double[] price, int[] quantity, long timeStepMilliSec) {

		if (price.length != quantity.length) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_AND_TIME);
		}
		long[] timeMilliSec = new long[price.length];

		for (int i = 0; i < price.length; i++)
			timeMilliSec[i] = i * timeStepMilliSec + 1000;

		return addPosition(assetName, price, quantity, timeMilliSec);
	}

	public Metric addPosition(String assetName, double[] price, int quantity, long timeStepMilliSec) {

		long[] timeMilliSec = new long[price.length];

		for (int i = 0; i < price.length; i++) {
			timeMilliSec[i] = i * timeStepMilliSec + 1000;
		}

		return addPosition(assetName, price, quantity, timeMilliSec);
	}

	public Metric addPosition(String assetName, double[] price, int quantity, long[] priceTimeMillSec) {

		int quantityArray[] = new int[1];
		quantityArray[0] = quantity;

		long quantityTime[] = new long[1];
		quantityTime[0] = -1;

		return addPosition(assetName, price, priceTimeMillSec, quantityArray, quantityTime);
	}

	public Metric addPosition(String assetName, float[] price, int quantity, long[] priceTimeMillSec) {

		int quantityArray[] = new int[1];
		quantityArray[0] = quantity;

		long quantityTime[] = new long[1];
		quantityTime[0] = -1;

		return addPosition(assetName, price, priceTimeMillSec, quantityArray, quantityTime);
	}

	public Metric addPosition(String assetName, double[] price, long[] priceTimeMillSec, int[] quantity, long[] quantityTimeMillSec) {
		clearCache();

		container.portfolioData.getPriceID().put(assetName, container.portfolioData.getNextDataId());

		if (price.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_PRICE);
		} else if (priceTimeMillSec.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_TIME);
		} else if (quantity.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_QUANTITY);
		} else if (quantityTimeMillSec.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_TIME);
		} else if (container.portfolioData.getIndexSymbol() == null) {
			return new Metric(MessageStrings.ADD_INDEX);
		}

		if (quantityTimeMillSec.length != quantity.length)
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_AND_TIME);
		if (priceTimeMillSec.length != price.length)
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);

		removePositionQuantity(assetName);
		removePositionPrice(assetName);

		Metric resultQuantity = addQuantity(assetName, quantity, quantityTimeMillSec);

		if (resultQuantity.hasError()) {
			return resultQuantity;
		}

		ArrayCache cashPrice;
		ArrayCache cashPriceTime;

		try {
			cashPrice = new ArrayCache(price);

			container.portfolioData.getSymbolPriceMap().put(assetName, cashPrice);
			cashPriceTime = new ArrayCache(priceTimeMillSec);

			container.portfolioData.getSymbolPriceTimeMap().put(assetName, cashPriceTime);
		} catch (IOException e) {
			return processException(e);
		}

		container.portfolioData.getSymbolNamesList().add(assetName);
		container.portfolioData.getUserPrice().add(assetName);

		return new Metric();
	}

	public Metric addPosition(String assetName, float[] price, long[] priceTimeMillSec, int[] quantity, long[] quantityTimeMillSec) {
		clearCache();
		container.portfolioData.getPriceID().put(assetName, container.portfolioData.getNextDataId());

		if (price.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_PRICE);
		} else if (priceTimeMillSec.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_PRICE_TIME);
		} else if (quantity.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_QUANTITY);
		} else if (quantityTimeMillSec.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_TIME);
		} else if (container.portfolioData.getIndexSymbol() == null) {
			return new Metric(MessageStrings.ADD_INDEX);
		}
		if (quantityTimeMillSec.length != quantity.length)
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_QUANTITY_AND_TIME);
		if (priceTimeMillSec.length != price.length)
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);

		removePositionQuantity(assetName);
		removePositionPrice(assetName);

		Metric resultQuantity = addQuantity(assetName, quantity, quantityTimeMillSec);

		if (resultQuantity.hasError()) {
			return resultQuantity;
		}

		ArrayCache cashPrice;
		ArrayCache cashPriceTime;

		try {
			cashPrice = new ArrayCache(price);

			container.portfolioData.getSymbolPriceMap().put(assetName, cashPrice);
			cashPriceTime = new ArrayCache(priceTimeMillSec);

			container.portfolioData.getSymbolPriceTimeMap().put(assetName, cashPriceTime);
		} catch (IOException e) {
			return processException(e);
		}

		container.portfolioData.getSymbolNamesList().add(assetName);
		container.portfolioData.getUserPrice().add(assetName);

		return new Metric();
	}

	public Metric addUserData(String dataName, TDoubleArrayList value, TLongArrayList timeMillSec) {
		return addUserData(dataName,  value.toArray(),  timeMillSec.toArray());
	}
	
	public Metric addUserData(String dataName, double[] value, long[] timeMillSec) {
		clearCache();

		container.portfolioData.getPriceID().put(dataName, container.portfolioData.getNextDataId());

		if (value.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_DATA);
		} else if (timeMillSec.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_TIME);
		}

		if (value.length != timeMillSec.length)
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_DATA_AND_TIME);

		removeUserData(dataName);

		ArrayCache cashData;
		ArrayCache cashTime;

		try {
			cashData = new ArrayCache(value);

			container.symbolUserDataMap.put(dataName, cashData);
			cashTime = new ArrayCache(timeMillSec);

			container.symbolUserDataTimeMap.put(dataName, cashTime);

		} catch (IOException e) {
			return processException(e);
		}

		container.userData.add(dataName);

		return new Metric();
	}

	public Metric addUserData(String dataName, ArrayCache value, ArrayCache timeMillSec) {
		clearCache();

		container.portfolioData.getPriceID().put(dataName, container.portfolioData.getNextDataId());

		if (value.getSize() == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_DATA);
		} else if (timeMillSec.getSize() == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_TIME);
		}

		removeUserData(dataName);
		container.symbolUserDataMap.put(dataName, value);
		container.symbolUserDataTimeMap.put(dataName, timeMillSec);
		container.userData.add(dataName);

		return new Metric();
	}

	/**
	 * Remove position from portfolio
	 * 
	 * @param symbol
	 */
	public void removePositionPrice(String symbol) {
		clearCache();

		if (container.portfolioData.getSymbolNamesList().contains(symbol)) {

			if (container.portfolioData.getUserPrice().contains(symbol)) {
				container.portfolioData.getUserPrice().remove(symbol);
				container.portfolioData.getSymbolPriceMap().remove(symbol);
				container.portfolioData.getSymbolPriceTimeMap().remove(symbol);
				container.portfolioData.getSymbolNamesList().remove(symbol);
			}

			container.portfolioData.getSymbolNamesList().remove(symbol);

		}
	}

	public void removeUserData(String symbol) {
		clearCache();
		if (container.userData.contains(symbol)) {
			container.userData.remove(symbol);
			container.symbolUserDataMap.remove(symbol);
			container.symbolUserDataTimeMap.remove(symbol);
		}

	}

	public void removePositionQuantity(String symbol) {
		clearCache();

		if (container.portfolioData.getSymbolNamesList().contains(symbol)) {
			container.portfolioData.getSymbolQuantityMap().remove(symbol);
			container.portfolioData.getSymbolQuantityTimeMap().remove(symbol);

		}
	}

	public Metric setPositionQuantity(String name, int quantity) {
		int quantityArray[] = new int[1];
		quantityArray[0] = quantity;
		long quantityTime[] = new long[1];
		quantityTime[0] = -1;
		return setPositionQuantity(name, quantityArray, quantityTime);
	}

	public Metric setPositionQuantity(String name, TIntArrayList quantity, TLongArrayList timeMillesc) {
		return setPositionQuantity(name,  quantity.toArray(),  timeMillesc.toArray());
	}
	
	public Metric setPositionQuantity(String name, int[] quantity, long[] timeMillesc) {
		clearCache();
		if (container.portfolioData.getSymbolNamesList().contains(name)) {
			removePositionQuantity(name);
			return addQuantity(name, quantity, timeMillesc);
		} else {
			return new Metric(String.format(MessageStrings.POSITION_NOT_FOUND, name));
		}
	}

	public Metric setPositionQuantity(String name, ArrayCache quantity, ArrayCache timeMillesc) {
		clearCache();
		if (container.portfolioData.getSymbolNamesList().contains(name)) {
			removePositionQuantity(name);
			return addQuantity(name, quantity, timeMillesc);
		} else {
			return new Metric(String.format(MessageStrings.POSITION_NOT_FOUND, name));
		}
	}

	public Metric setPositionQuantity(String name, TDoubleArrayList quantityD, TLongArrayList timeMillesc) {
		return setPositionQuantity( name,  quantityD.toArray(),  timeMillesc.toArray());
	}
	
	public Metric setPositionQuantity(String name, double[] quantityD, long[] timeMillesc) {
		clearCache();

		int[] quantity = new int[quantityD.length];
		for (int i = 0; i < quantity.length; i++) {
			quantity[i] = (int) quantityD[i];
		}
		if (container.portfolioData.getSymbolNamesList().contains(name)) {
			removePositionQuantity(name);
			return addQuantity(name, quantity, timeMillesc);
		} else {
			return new Metric(String.format(MessageStrings.POSITION_NOT_FOUND, name));
		}
	}

	public Metric setPositionQuantity(String name, TIntArrayList quantity, String[] timeMillesc) {
		return  setPositionQuantity( name,  quantity.toArray(),  timeMillesc);
		
	}
	
	public Metric setPositionQuantity(String name, int[] quantity, String[] timeMillesc) {
		clearCache();

		if (container.portfolioData.getUserPrice().contains(name)) {

			double[] price;
			long[] time;

			try {
				price = container.portfolioData.getSymbolPriceMap().get(name).getDoubleArray();
				time = container.portfolioData.getSymbolPriceTimeMap().get(name).getLongArray();
			} catch (Exception e) {
				return new Metric(e.getMessage());
			}
			return addPosition(name, price, time, quantity, DateTimeUtil.toPOSIXTime(timeMillesc));

		} else {

			return addPosition(name, quantity, DateTimeUtil.toPOSIXTime(timeMillesc));
		}

	}

	public PortfolioData getPortfolioData() {
		return container.portfolioData;
	}

	public Portfolio setMetricKey(String key) {
		container.batchMetricKey = key;
		return this;
	}

	public void addMetricToBatch(String metricType) {
		//metricType = selectPortfolioParamsT(metricType);
		try {
			metricType = getFullMetricType(metricType);
		} catch (Exception e) {

			// Console.writeln(e.getMessage());
		}

		if (container.batchMetricsPortfolio != null || container.batchMetricsPosition != null) {
			if (metricType.contains("PORTFOLIO")) {
				container.batchMetricsPortfolio.add(metricType);
				if (container.batchMetricKey != null) {
					container.batchMetricPortfolioKeys.add(container.batchMetricKey);
					container.batchMetricKey = null;
				} else
					container.batchMetricPortfolioKeys.add(metricType);
			}
			if (metricType.contains("POSITION") || metricType.contains("INDEX")) {
				container.batchMetricsPosition.add(metricType);
				if (container.batchMetricKey != null) {
					container.batchMetricPositionKeys.add(container.batchMetricKey);
					container.batchMetricKey = null;
				} else
					container.batchMetricPositionKeys.add(metricType);
			}
		}
	}

	public void addMetricToBatch(Map<String, String> metricType) {

		Gson gson = new Gson();
		addMetricToBatch(gson.toJson(metricType));
	}

	public Metric runBatch(List<Map<String, String>> metrics) {
		startBatch();
		for (Map<String, String> e : metrics) {
			addMetricToBatch(e);
		}

		return finishBatch();
	}
	
	public List<LazyMetric> runBatchLazy(List<Map<String, String>> metrics) {
		
		Type mapType = new TypeToken<HashMap<String,String>>() {}.getType();
		Gson gson = new Gson();
		ArrayList<LazyMetric> result = new ArrayList<LazyMetric>(); 
		for(Map<String,String> e: metrics){
			LazyMetricBuilder builder = new LazyMetricBuilder(e);
			result.add(getLazyMetric(builder));
		}
		
		for(LazyMetric e: result){
			
			e.compute();

		}
		
		return result;
	}


	public Metric runBatch(List<Map<String, String>> metrics, List<String> metricKeys) {
		startBatch();
		int i = 0;
		for (Map<String, String> e : metrics) {
			setMetricKey(metricKeys.get(i));
			i++;
			addMetricToBatch(e);
		}

		return finishBatch();
	}

	public Metric runBatchList(List<String> metrics, List<String> metricKeys) {
		startBatch();
		int i = 0;
		for (String e : metrics) {
			setMetricKey(metricKeys.get(i));
			i++;
			addMetricToBatch(e);
		}

		return finishBatch();
	}

	public Metric runBatchList(List<String> metrics) {
		startBatch();
		for (String e : metrics) {
			addMetricToBatch(e);
		}

		return finishBatch();
	}

	public Metric runBatch(String metricsStr) {

		Gson gson = new Gson();
		Type mapType = new TypeToken<List<Map<String, String>>>() {
		}.getType();
		List<Map<String, String>> list = gson.fromJson(metricsStr, mapType);

		return runBatch(list);
	}

	public Metric runBatch(String metricsStr, String metricsKeys) {

		Gson gson = new Gson();
		Type mapType = new TypeToken<List<Map<String, String>>>() {
		}.getType();
		List<Map<String, String>> list = gson.fromJson(metricsStr, mapType);

		mapType = new TypeToken<List<String>>() {
		}.getType();
		List<String> keys = gson.fromJson(metricsKeys, mapType);

		return runBatch(list, keys);
	}

	public Metric getMetric(Map<String, String> metricType) {

		Gson gson = new Gson();

		Metric result = getMetric(gson.toJson(metricType), "");
		return result;

	}

	public Metric getMetric(String metricType) {
		
	//	metricType = selectPortfolioParams(metricType);
		try {
			metricType = getFullMetricType(metricType);
		} catch (Exception e) {

			Console.writeln(e.getMessage());
		}

		if (container.isBatchStart) {
			addMetricToBatch(metricType);
			return new Metric();
		}

		Metric result = getMetric(metricType, "");

		result.setDebug(container.isDebug);
		return result;
	}

	private String selectPortfolioParamsT(String metricType){
		
		Gson gson = new Gson();
		Type mapTypeMetrics = new TypeToken<HashMap<String, String>>() {}.getType();

		HashMap<String, String> argsGson = gson.fromJson(metricType, mapTypeMetrics);
		HashMap<String, String> argsGsonNew = new HashMap<String, String>(argsGson);
		for(String e: argsGson.keySet())
			if(container.portfolioData.checkPortfolioParams(e)){
				setParam(e, argsGson.get(e));
				argsGsonNew.remove(e);
			}
		
		metricType = gson.toJson(argsGsonNew, mapTypeMetrics);
		
		return metricType;
		
	}
	
	public LazyMetric getLazyMetric(LazyMetricBuilder metricBuilder) {
		
		HashMap<String, String> portfolioSetting = new HashMap<String, String>( container.portfolioData.getPortfolioSettings() );
		HashMap<String, String> estimatorSetting = new HashMap<String, String>( container.portfolioData.getEstimatorSettings() );
		
		String metricTypeB = metricBuilder.getParam("metricType");
		metricBuilder.removeParam("metricType");
		
		updateBurnWindowLength( metricBuilder.getActualWindowLenght() );
		
//		for(String e: metricBuilder.getWindowLengthSetMax())
//			updateBurnWindowLength(e);
		
//		for(String e: metricBuilder.getWindowLengthSetAdd())
//			addToBurnWindowLength(e);
//		addToBurnWindowLength(getMaxWindow(metricBuilder.getWindowLengthSetAdd()));
		

		String metricType = selectPortfolioParamsT(metricBuilder.getJsonString());
		
		metricBuilder.setParam("metricType",metricTypeB);
		
		try {
			String metricTypeFull = getFullMetricType(metricType);

			if (isContainsResult(metricTypeFull)) {
				
				
				
				container.portfolioData.setPortfolioSettings(portfolioSetting);
				container.portfolioData.setEstimatorSettings(estimatorSetting);
							
				return new LazyMetric(container, metricType, metricTypeFull, metricBuilder);
			}

			if (!container.isBatchStart)
				startBatch();

			LazyMetric result = new LazyMetric(container, metricType, metricTypeFull, metricBuilder);
			addMetricToBatch(metricTypeFull);

			if (!isBatchOn())
				finishBatch();
			
			if(isMultiBatch()){
				container.portfolioData.setPortfolioSettings(portfolioSetting);
				container.portfolioData.setEstimatorSettings(estimatorSetting);
			}
			
			
			return result;

		} catch (Exception e) {
			
			
			container.portfolioData.setPortfolioSettings(portfolioSetting);
			container.portfolioData.setEstimatorSettings(estimatorSetting);
			
			return new LazyMetric("Exception error:" + e.getMessage());

		}

	}
	
public String getStrRequest(LazyMetricBuilder metricBuilder) {
		
		HashMap<String, String> portfolioSetting = new HashMap<String, String>( container.portfolioData.getPortfolioSettings() );
		HashMap<String, String> estimatorSetting = new HashMap<String, String>( container.portfolioData.getEstimatorSettings() );
		
		String metricTypeB = metricBuilder.getParam("metricType");
		metricBuilder.removeParam("metricType");
		
		updateBurnWindowLength( metricBuilder.getActualWindowLenght() );
		
		

		String metricType = selectPortfolioParamsT(metricBuilder.getJsonString());
		
		metricBuilder.setParam("metricType",metricTypeB);
		
		String metricTypeFull;
		
		try {
			
			metricTypeFull = getFullMetricType(metricType);

			
		} catch (Exception e) {
			
			
			container.portfolioData.setPortfolioSettings(portfolioSetting);
			container.portfolioData.setEstimatorSettings(estimatorSetting);
			
			return "Exception error:" + e.getMessage();

		}
		
		return metricTypeFull;

	}


	private void processNoCashError(String str) throws Exception {
		if (str.contains("No data in cache"))
			throw new Exception(str);

	}

	private Metric transmitData(ArrayList<String> dataList) throws Exception {
		
		
		String windowLength = container.windowLength + "s";// getParam("windowLength");
		String priceSamplingInterval = getParam("priceSamplingInterval");
		String momentsModel = getParam("riskMethodology");
		Metric result = container.clientConnection.transmitDataList(container.portfolioData.getFromTime(), container.portfolioData.getToTime(), dataList,
				windowLength, priceSamplingInterval, momentsModel, getTrainingModel());

		if (result.hasError()) {
			processNoCashError(result.getErrorMessage());
			return result;
		}

		if (result.getMessage().length() == 0)
			return new Metric();

		Gson gson = new Gson();
		Type mapType = new TypeToken<String[]>() {
		}.getType();
		String[] dataTransmit = gson.fromJson(result.getMessage(), mapType);// result.getMessage().split(";");

		for (String e : dataTransmit) {

			String[] ePars = e.split("-");
			if (ePars[0].equals("h") || ePars[0].equals("hI")) {

				result = new Metric(MessageStrings.ERROR_HIST_PRICE);

				if (result.hasError()) {
					processNoCashError(result.getErrorMessage());
					return result;
				}

			} else if (ePars[0].equals("u")) {

				String position = ePars[1].split(":")[1];

				if (position.equals("index")) {
					result = container.clientConnection.transmitUserPrice(ePars[1], container.portfolioData.getIndexPrice().getDoubleAsFloatArray(),
							container.portfolioData.getIndexTimeMillisec().getLongArray());
					if (result.hasError()) {
						processNoCashError(result.getErrorMessage());
						return result;
					}
				} else {

					ArrayCache cache = container.portfolioData.getSymbolPriceMap().get(position);

					if (cache == null)
						cache = container.symbolUserDataMap.get(position);

					if (cache == null)
						throw new Exception(String.format(MessageStrings.PRICE_FOR_SYMBOL_NO, position));

					float price[] = cache.getDoubleAsFloatArray();

					cache = container.portfolioData.getSymbolPriceTimeMap().get(position);

					if (cache == null)
						cache = container.symbolUserDataTimeMap.get(position);

					if (cache == null)
						throw new Exception(String.format(MessageStrings.TIME_FOR_SYMBOL_NO, position));
					long time[] = cache.getLongArray();

					if (price == null || time == null)
						throw new Exception(String.format(MessageStrings.PRICE_FOR_SYMBOL_NO, position));

					result = container.clientConnection.transmitUserPrice(ePars[1], price, time);
					if (result.hasError()) {
						processNoCashError(result.getErrorMessage());
						return result;
					}

				}

			} else if (ePars[0].equals("q")) {
				String position = ePars[1].split(":")[1];

				ArrayCache xq = container.portfolioData.getSymbolQuantityMap().get(position);

				if (xq == null)
					throw new Exception(String.format(MessageStrings.QUANTITY_FOR_SYMBOL_NO, position));

				int quantity[] = xq.getIntArray();

				ArrayCache xt = container.portfolioData.getSymbolQuantityTimeMap().get(position);

				if (xt == null)
					throw new Exception(String.format(MessageStrings.TIME_FOR_SYMBOL_NO, position));

				long time[] = xt.getLongArray();

				result = container.clientConnection.transmitQuantity(ePars[1], quantity, time);
				if (result.hasError()) {
					processNoCashError(result.getErrorMessage());
					return result;
				}

			} else {
				return new Metric(MessageStrings.ERROR_TRANSMIT);
			}

		}

		return new Metric();

	}

	public boolean isContainsResult(String metricType) throws Exception {

		String metricTypeFull = getMetricTypeList(metricType);// ,

		CacheKey key = new CacheKey(metricTypeFull, "");
		return container.portfolioCache.containsKey(key);
	}

	public Metric getMetric(String metricType, String params) {
		//metricType = selectPortfolioParams(metricType);
		try {
			metricType = getFullMetricType(metricType);
		} catch (Exception e) {

			// Console.writeln(e.getMessage());
		}

		if (container.portfolioData.getFromTime().length() == 0 || container.portfolioData.getToTime().length() == 0) {
			container.clientConnection.resetProgressBar();
			return new Metric("Set time interval  first");
		}

		for (int ii = 0; ii < container.NUMBER_OF_TRIES; ii++) {

			try {

				if (container.portfolioData.getSymbolNamesList().size() == 0) {
					container.clientConnection.resetProgressBar();
					return new Metric(MessageStrings.EMPTY_PORTFOLIO);
				}

				String metricTypeFull = getMetricTypeList(metricType);// ,
																		// params);

				String[] positions = null;

				CacheKey key = new CacheKey(metricTypeFull, params);

				if (container.portfolioCache.containsKey(key)) {

					Metric result = new Metric();

					result.setData("value", container.portfolioCache.getMetric(key));
					result.setData("time", container.portfolioCache.getTime(key));
					result.setDebug(container.isDebug);
					result.setNaNFiltered(isNaNFiltered());
					result.setNaN2Zero(isNaN2Zero());
					return result;
				}

				container.clientConnection.printProgressBar(0);

				{

					Metric result = container.clientConnection.validateStringRequest(metricTypeFull);
					if (result.hasError())
						throw new Exception(result.getErrorMessage());

					positions = result.getStringArray("positions");
				}

				Metric result = null;

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

					if (position.length() != 0 && !container.portfolioData.getSymbolNamesList().contains(position)) {
						container.clientConnection.resetProgressBar();
						return new Metric(String.format(MessageStrings.POSITION_NOT_FOUND, position));
					}

					if (position.length() != 0) {

						positionList.add(position);
					}

					for (String symbol : container.portfolioData.getSymbolNamesList()) {
						if (!symbol.equals(position)) {

							positionList.add(symbol);
						}
					}

				} else {

					for (int i = 0; i < positions.length; i++) {
						if (positions[i].equals("@_INDEX_")) {

						} else {
							if (!container.portfolioData.getSymbolNamesList().contains(positions[i])) {
								container.clientConnection.resetProgressBar();
								return new Metric(String.format(MessageStrings.POSITION_NOT_FOUND, positions[i]));

							}

							positionList.add(positions[i]);

						}
					}

				}

				// ------------------------------
				ArrayList<String> dataList = new ArrayList<String>();
				if (container.portfolioData.getIndexPrice() == null) {

					dataList.add("hI-" + container.portfolioData.getPortfolioId() + ":" + container.portfolioData.getIndexSymbol() + ":"
							+ container.portfolioData.getPriceID().get(container.portfolioData.getIndexSymbol()));

					indexPosition = container.portfolioData.getIndexSymbol();
				} else {

					dataList.add("u-" + container.portfolioData.getPortfolioId() + ":" + "index" + ":"
							+ container.portfolioData.getPriceID().get(container.portfolioData.getIndexSymbol()));

					indexPosition = "index";

				}

				for (String symbol : container.portfolioData.getSymbolNamesList()) {
					if (container.portfolioData.getUserPrice().contains(symbol)) {

						dataList.add("u-" + container.portfolioData.getPortfolioId() + ":" + symbol + ":" + container.portfolioData.getPriceID().get(symbol));

					} else {

						dataList.add("h-" + container.portfolioData.getPortfolioId() + ":" + symbol + ":" + container.portfolioData.getPriceID().get(symbol));
					}

					dataList.add("q-" + container.portfolioData.getPortfolioId() + ":" + symbol + ":" + container.portfolioData.getQuantityID().get(symbol));

				}

				// ------------------------------

				ArrayList<String> positionNames = new ArrayList<String>();
				for (String e : positionList) {
					if (container.portfolioData.getUserPrice().contains(e))
						positionNames.add(container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getPriceID().get(e) + "="
								+ container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getQuantityID().get(e));
					else
						positionNames.add("h-" + container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getPriceID().get(e) + "="
								+ container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getQuantityID().get(e));

				}

				if (indexPosition.length() != 0) {
					if (container.portfolioData.getIndexPrice() != null)
						indexPosition = container.portfolioData.getPortfolioId() + ":" + indexPosition + ":"
								+ container.portfolioData.getPriceID().get(indexPosition) + "=" + container.portfolioData.getPortfolioId() + ":"
								+ indexPosition + ":" + container.portfolioData.getQuantityID().get(indexPosition);
					else
						indexPosition = "hI-" + container.portfolioData.getPortfolioId() + ":" + indexPosition + ":"
								+ container.portfolioData.getPriceID().get(indexPosition) + "=" + container.portfolioData.getPortfolioId() + ":"
								+ indexPosition + ":" + container.portfolioData.getQuantityID().get(indexPosition);
				}

				for (String e : container.userData) {

					dataList.add("u-" + container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getPriceID().get(e));

					positionNames.add(container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getPriceID().get(e));

				}

				Metric resultTransmit = transmitData(dataList);

				if (resultTransmit.hasError()) {
					throw new Exception(resultTransmit.getErrorMessage());
					// return new
					// MethodResult(resultTransmit.getErrorMessage());
				}

				result = container.clientConnection.estimateTransactional(metricTypeFull, indexPosition, positionNames, params);

				if (!result.hasError()) {

					container.portfolioCache.addMetric(key, result.getDataArrayCacheArray("values")[0]);
					container.portfolioCache.addTime(key, result.getDataArrayCacheArray("times")[0]);
					container.cachedValueList.add(key);

					Metric resultT = new Metric();

					resultT.setData("value", result.getDataArrayCacheArray("values")[0]);
					resultT.setData("time", result.getDataArrayCacheArray("times")[0]);
					resultT.setInfo(result.getInfoParams());

					resultT.setDebug(container.isDebug);
					resultT.setNaNFiltered(isNaNFiltered());
					resultT.setNaN2Zero(isNaN2Zero());
					result = resultT;

				} else {
					container.clientConnection.createCallGroup(1);
				}

				return result;

			} catch (Exception e) {

				Metric result = processException(e);
				if (result.getErrorMessage().equals("Server too busy, try again later"))
					continue;
				return result;

			}

		}
		container.clientConnection.resetProgressBar();
		return new Metric(MessageStrings.FAILED_SERVER_TIME_OUT);

	}

	public Metric getAllSymbolsList() {

		for (int ii = 0; ii < container.NUMBER_OF_TRIES; ii++) {

			CacheKey keyId = new CacheKey("[{name:\"allSymbolsId\"}]", "");
			CacheKey keyDescription = new CacheKey("[{name:\"allSymbolsDescription\"}]", "");
			CacheKey keyExchange = new CacheKey("[{name:\"allSymbolsExchange\"}]", "");

			if (container.portfolioCache.containsKey(keyId)) {

				Metric result = new Metric();

				result.setData("id", container.portfolioCache.getMetric(keyId));
				result.setData("description", container.portfolioCache.getMetric(keyDescription));
				result.setData("exchange", container.portfolioCache.getMetric(keyExchange));

				return result;
			}

			try {

				Metric result = container.clientConnection.getAllSymbolsList();

				container.portfolioCache.addMetric(keyId, result.getDataArrayCache("id"));
				container.portfolioCache.addMetric(keyDescription, result.getDataArrayCache("description"));
				container.portfolioCache.addMetric(keyExchange, result.getDataArrayCache("exchange"));

				return result;

			} catch (Exception e) {

				Metric result = processException(e);
				if (result.getErrorMessage().equals("Server too busy, try again later"))
					continue;
				return result;

			}

		}

		return new Metric(MessageStrings.FAILED_SERVER_TIME_OUT);

	}

	public void clearBatchMetric() {
		container.batchMetricsPortfolio.clear();
		container.batchMetricsPosition.clear();
		container.batchMetricsPortfolio = null;
		container.batchMetricsPosition = null;
	}

	public Metric finishBatch() {

		if (!container.isBatchStart)
			return new Metric();

		if (container.clientConnection.isStreamEnabled().get()) {
			return new Metric(container.clientConnection.STREAM_IS_ALREADY_RUNNING);
		}

		container.isBatchStart = false;

		if (container.batchMetricsPortfolio == null && container.batchMetricsPosition == null)
			return new Metric();

		String factorModel = container.portfolioData.getParam("factorModel");
		if (factorModel == "sim" || factorModel == null) {

			container.batchMetricsPortfolio.addAll(container.batchMetricsPosition);
			container.batchMetricPortfolioKeys.addAll(container.batchMetricPositionKeys);
			Metric result = computeBatch(container.batchMetricsPortfolio, container.batchMetricPortfolioKeys);
			clearBatchMetric();
			return result;
		} else {

			Metric result;

			if (getParam("stream").equals("on") && factorModel != "sim")
				return new Metric("Streaming mode is supported only with SIM portfolio");

			if (container.batchMetricsPortfolio.size() != 0 && container.batchMetricsPosition.size() != 0) {

				container.clientConnection.createCallGroup(2);
				result = computeBatch(container.batchMetricsPortfolio);
				if (result.hasError()) {
					clearBatchMetric();
					container.clientConnection.createCallGroup(1);
					return result;
				}

				result = computeBatch(container.batchMetricsPosition);
				clearBatchMetric();
				container.clientConnection.createCallGroup(1);
				return result;
			} else {

				container.batchMetricsPortfolio.addAll(container.batchMetricsPosition);
				result = computeBatch(container.batchMetricsPortfolio);
				clearBatchMetric();
				return result;
			}
		}
	}

	private Metric processException(Exception e) {

		if (container.isDebug) {
			if (e.getMessage() == null || !e.getMessage().equals("Cannot connect to server. Server request failed due to a timeout."))
				Console.writeStackTrace(e);
		}

		if (e instanceof ConnectFailedException) {

			Metric isRestarted = container.clientConnection.restart();
			if (isRestarted.hasError()) {
				container.clientConnection.resetProgressBar();
				return new Metric(isRestarted.getErrorMessage());
			}

			return new Metric("Server too busy, try again later");

		}

		if (e.getMessage() == null || e.getMessage().contains("No data in cache") || e.getMessage().contains("null")) {

			Metric isRestarted = container.clientConnection.restart();
			if (isRestarted.hasError()) {
				container.clientConnection.resetProgressBar();
				return new Metric(isRestarted.getErrorMessage());
			}

			return new Metric("Server too busy, try again later");

		}

		if (e.getMessage() == null) {
			Console.writeStackTrace(e);
			return new Metric(MessageStrings.ERROR_101);
		}

		container.clientConnection.resetProgressBar();
		return new Metric(e.getMessage());

	}

	private Metric computeBatch(ArrayList<String> batchMetrics) {
		return computeBatch(batchMetrics, new ArrayList<String>());
	}

	private Metric computeBatch(ArrayList<String> batchMetrics, ArrayList<String> batchMetricKeys) {

		ArrayList<String> metricsTypeNewList = new ArrayList<String>();
		for (String e : batchMetrics) {
			CacheKey key;
			try {
				String metricTypeFull = getMetricTypeList(e);
				key = new CacheKey(metricTypeFull, "");

			} catch (Exception e1) {
				return processException(e1);
			}
			if (container.portfolioCache.containsKey(key) && !getParam("stream").equals("on"))
				continue;
			else
				metricsTypeNewList.add(e);
		}

		if (metricsTypeNewList.size() == 0) {
			return new Metric();
		}

		batchMetrics.clear();
		container.clientConnection.printProgressBar(0);

		if (container.portfolioData.getFromTime().length() == 0 || container.portfolioData.getToTime().length() == 0) {
			container.clientConnection.resetProgressBar();
			return new Metric("Set time interval  first");
		}

		for (int ii = 0; ii < container.NUMBER_OF_TRIES; ii++) {
			try {
				String metricsType = getMetricTypeList(metricsTypeNewList);
				if (container.portfolioData.getSymbolNamesList().size() == 0) {
					container.clientConnection.resetProgressBar();
					return new Metric(MessageStrings.EMPTY_PORTFOLIO);
				}
				String[] positions = null;
				{

					Metric result = container.clientConnection.validateStringRequest(metricsType);

					if (result.hasError()) {
						throw new Exception(result.getErrorMessage());
					}

					positions = result.getStringArray("positions");

				}
				Metric result = null;

				ArrayList<String> positionList = new ArrayList<String>();
				String indexPosition = "";

				if (Arrays.asList(positions).contains("@_ALL_PORTFOLIO_")) {
					for (String symbol : container.portfolioData.getSymbolNamesList()) {
						positionList.add(symbol);
					}
				} else {
					for (int i = 0; i < positions.length; i++) {
						if (positions[i].equals("@_INDEX_")) {
						} else {
							if (!container.portfolioData.getSymbolNamesList().contains(positions[i])) {
								container.clientConnection.resetProgressBar();
								return new Metric(String.format(MessageStrings.POSITION_NOT_FOUND, positions[i]));
							}
							positionList.add(positions[i]);
						}
					}
				}

				ArrayList<String> positionNames = new ArrayList<String>();
				for (String e : positionList) {
					if (container.portfolioData.getUserPrice().contains(e)) {
						positionNames.add(container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getPriceID().get(e) + "="
								+ container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getQuantityID().get(e));
					} else {
						positionNames.add("h-" + container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getPriceID().get(e) + "="
								+ container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getQuantityID().get(e));
					}
				}

				ArrayList<String> dataList = new ArrayList<String>();
				if (container.portfolioData.getIndexPrice() == null) {
					dataList.add("hI-" + container.portfolioData.getPortfolioId() + ":" + container.portfolioData.getIndexSymbol() + ":"
							+ container.portfolioData.getPriceID().get(container.portfolioData.getIndexSymbol()));
					indexPosition = container.portfolioData.getIndexSymbol();
				} else {
					dataList.add("u-" + container.portfolioData.getPortfolioId() + ":" + "index" + ":"
							+ container.portfolioData.getPriceID().get(container.portfolioData.getIndexSymbol()));
					indexPosition = "index";
				}

				for (String symbol : container.portfolioData.getSymbolNamesList()) {
					if (container.portfolioData.getUserPrice().contains(symbol)) {
						dataList.add("u-" + container.portfolioData.getPortfolioId() + ":" + symbol + ":" + container.portfolioData.getPriceID().get(symbol));
					} else {
						dataList.add("h-" + container.portfolioData.getPortfolioId() + ":" + symbol + ":" + container.portfolioData.getPriceID().get(symbol));
					}

					dataList.add("q-" + container.portfolioData.getPortfolioId() + ":" + symbol + ":" + container.portfolioData.getQuantityID().get(symbol));
				}

				// ------------------------------

				if (indexPosition.length() != 0) {
					if (container.portfolioData.getIndexPrice() != null) {
						indexPosition = container.portfolioData.getPortfolioId() + ":" + indexPosition + ":"
								+ container.portfolioData.getPriceID().get(indexPosition) + "=" + container.portfolioData.getPortfolioId() + ":"
								+ indexPosition + ":" + container.portfolioData.getQuantityID().get(indexPosition);
					} else {
						indexPosition = "hI-" + container.portfolioData.getPortfolioId() + ":" + indexPosition + ":"
								+ container.portfolioData.getPriceID().get(indexPosition) + "=" + container.portfolioData.getPortfolioId() + ":"
								+ indexPosition + ":" + container.portfolioData.getQuantityID().get(indexPosition);
					}
				}

				for (String e : container.userData) {
					dataList.add("u-" + container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getPriceID().get(e));
				}

				for (String e : container.userData) {
					dataList.add("u-" + container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getPriceID().get(e));

					positionNames.add(container.portfolioData.getPortfolioId() + ":" + e + ":" + container.portfolioData.getPriceID().get(e));

				}

				Metric resultTransmit = transmitData(dataList);

				if (resultTransmit.hasError()) {
					throw new Exception(resultTransmit.getErrorMessage());
					// return new
					// MethodResult(resultTransmit.getErrorMessage());
				}

				container.clientConnection.setBatchMetricKeys(batchMetricKeys, container.portfolioData.getPortfolioId());
				result = container.clientConnection.estimateTransactional(metricsType, indexPosition, positionNames, "");

				if (!result.hasError()) {

					for (int k = 0; k < result.getDataArrayCacheArray("values").length; k++) {
						String metricTypeFull = getMetricTypeList(metricsTypeNewList.get(k));
						CacheKey key = new CacheKey(metricTypeFull, "");
						container.portfolioCache.addMetric(key, result.getDataArrayCacheArray("values")[k]);
						container.portfolioCache.addTime(key, result.getDataArrayCacheArray("times")[k]);
						container.cachedValueList.add(key);
					}

					metricsTypeNewList.clear();

				} else {
					container.clientConnection.createCallGroup(1);
				}

				return new Metric();
			} catch (Exception e) {
				Metric result = processException(e);
				if (result.getErrorMessage().equals("Server too busy, try again later"))
					continue;
				return result;
			}
		}

		container.clientConnection.resetProgressBar();
		return new Metric(MessageStrings.FAILED_SERVER_TIME_OUT);
	}

	private Metric processException(IOException e) {
		if (container.isDebug) {
			Console.writeStackTrace(e);
		}
		if (e.getMessage() != null) {
			return new Metric(e.getMessage());
		} else {
			return new Metric(MessageStrings.ERROR_FILE);
		}
	}

	public LazyMetric getPDF(double from, double to, int number) throws Exception {
		return getPDF(from, to, number, "");
	}

	public LazyMetric getPDF(int number) throws Exception {
		return getPDF(0, 1, number, "");
	}

	public LazyMetric getPDF(int number, String position) throws Exception {
		return getPDF(0, 1, number, position);

	}

	public LazyMetric getPDF(double from, double to, int number, String position) throws Exception {
		if (getSymbolNamesList().size() == 0)
			return new LazyMetric(MessageStrings.EMPTY_PORTFOLIO);

		number = number > container.MAX_PDF_POINTS ? 300 : number;

		HashMap<String, String> params = new HashMap<String, String>();

		String sampling = getSamplingInterval();

		Gson gson = new Gson();

		Metric result;
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
				qResult.lockToRead();

				for (int j = 0; j < time.length; j++)
					for (int i = 0; i < number; i++) {
						x[j][i] = qResult.getNextDouble();
						PDF[j][i] = qResult.getNextDouble();

					}
				qResult.unlockToRead();

			} catch (Exception e) {
				if (isDebug())
					Console.writeStackTrace(e);
				if (e.getMessage() != null)
					return new LazyMetric(e.getMessage());
				else
					return new LazyMetric(MessageStrings.ERROR_FILE);
			}

		} else {
			return new LazyMetric(result.getErrorMessage());
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

		Metric resultP = new Metric();
		resultP.setData("time", new ArrayCache(time));
		resultP.setData("x", new ArrayCache(x));
		resultP.setData("pdf", new ArrayCache(PDF));

		return new LazyMetric(resultP);
	}

	public Metric getPositionQuantity(String symbol) {

		if (container.portfolioData.getSymbolQuantityMap().containsKey(symbol))
			try {
				double[] value = container.portfolioData.getSymbolQuantityMap().get(symbol).getIntAsDoubleArray();
				long[] time = container.portfolioData.getSymbolQuantityTimeMap().get(symbol).getLongArray();
				String samplingInterval = getParam("samplingInterval");

				if (samplingInterval.equals("last")) {

					Metric result = new Metric();
					result.setData("value", new ArrayCache(new double[] { value[value.length - 1] }));
					result.setData("time", new ArrayCache(new long[] { time[time.length - 1] }));

					return result;

				}

				Metric result = new Metric();
				result.setData("value", new ArrayCache(value));
				result.setData("time", new ArrayCache(time));

				return result;

			} catch (Exception e) {
				return processException(e);
			}
		else {
			return new Metric(String.format(MessageStrings.POSITION_NOT_FOUND, symbol));
		}
	}

	/**
	 * Returns an unmodifiable list of symbols that have non-zero position
	 * weights in the portfolio
	 * 
	 * @return symbol array
	 */
	public String[] getSymbols() {
		String[] names = new String[container.portfolioData.getSymbolNamesList().size()];
		int i = 0;
		for (String name : container.portfolioData.getSymbolNamesList()) {
			names[i] = name;
			i++;
		}
		return names;
	}

	public int[] getQuantities() throws Exception {

		String[] positions = getSymbols();
		int[] quantities = new int[positions.length];

		for (int i = 0; i < positions.length; i++) {
			quantities[i] = container.portfolioData.getSymbolQuantityMap().get(positions[i]).getIntArray()[0];
		}

		return quantities;
	}

	public void createCallGroup(int groupSize) {
		container.clientConnection.createCallGroup(groupSize);
	}

	public List<String> getSymbolNamesList() {
		return container.portfolioData.getSymbolNamesList();
	}

	public Map<String, ArrayCache> getSymbolQuantityMap() {
		return container.portfolioData.getSymbolQuantityMap();
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

	public void setHoldingPeriodEnabled(String isHoldingPeriodEnabled) {
		setParam("isHoldingPeriodEnabled", isHoldingPeriodEnabled);
	}

	public String getFromTime() {
		return container.portfolioData.getFromTime();
	}

	public Metric setFromTime(String fromTime) {
		clearCache();

		updatePriceData();
		container.portfolioData.setFromTime("");

		if (fromTime.contains("t")) {
			int daysBack;

			if (fromTime.trim().equals("t")) {
				container.portfolioData.setFromTime(fromTime);
				return new Metric();
			} else {
				String[] a = fromTime.trim().split("-");
				try {

					container.portfolioData.setFromTime(fromTime);
					return new Metric();

				} catch (Exception e) {
					return new Metric(String.format(MessageStrings.WRONG_TIME_FORMAT_FROM, fromTime));
				}
			}

		} else if (!fromTime.contains(":")) {

			String s = fromTime.trim() + " 09:30:01";
			try {
				Timestamp t = Timestamp.valueOf(s);
				container.portfolioData.setFromTime(fromTime);
				return new Metric();
			} catch (Exception e) {
				return new Metric(String.format(MessageStrings.WRONG_TIME_FORMAT_FROM, fromTime));

			}
		}

		try {
			Timestamp t = Timestamp.valueOf(fromTime);
		} catch (Exception e) {
			return new Metric(String.format(MessageStrings.WRONG_TIME_FORMAT_FROM, fromTime));
		}
		container.portfolioData.setFromTime(fromTime);
		return new Metric();
	}

	public String getToTime() {
		return container.portfolioData.getToTime();
	}

	public void updatePriceData() {
		clearCache();
		for (String e : container.portfolioData.getPriceID().keySet()) {
			if (!container.portfolioData.getUserPrice().contains(e))
				container.portfolioData.getPriceID().put(e, container.portfolioData.getNextDataId());

		}

	}

	public Metric setToTime(String toTime) {
		clearCache();

		updatePriceData();
		container.portfolioData.setToTime("");

		if (toTime.contains("t")) {

			if (toTime.trim().equals("t")) {
				container.portfolioData.setToTime(toTime);
				return new Metric();
			}

			else {

				String[] a = toTime.trim().split("-");
				try {
					int daysBack = Integer.parseInt(a[1].split("d")[0]);
					container.portfolioData.setToTime(toTime);
					return new Metric();
				} catch (Exception e) {

					return new Metric(String.format(MessageStrings.WRONG_TIME_FORMAT_TO, toTime));

				}
			}

		} else if (!toTime.contains(":")) {

			String s = toTime.trim() + " 09:30:01";
			try {
				Timestamp t = Timestamp.valueOf(s);
				container.portfolioData.setToTime(toTime);
				return new Metric();
			} catch (Exception e) {

				return new Metric(String.format(MessageStrings.WRONG_TIME_FORMAT_TO, toTime));

			}
		}

		try {
			Timestamp t = Timestamp.valueOf(toTime);

		} catch (Exception e) {

			return new Metric(String.format(MessageStrings.WRONG_TIME_FORMAT_TO, toTime));

		}
		container.portfolioData.setToTime(toTime);
		return new Metric();
	}

	public String getSamplingInterval() {
		return getParam("samplingInterval");
	}

	/**
	 * set times when the data are calculated
	 * 
	 * @param samplingIntervalServer
	 *            "all" or - with out sampling ; "Xs" X -seconds; "Xm" X
	 *            -minutes; "Xh" X - hours; "Xd" X -days; "Xw" X -weeks; "Xmo" X
	 *            - months; "Xy" X - years; "last" - only final result
	 */
	public void setSamplingInterval(String samplingIntervalServer) {
		setParam("samplingInterval", samplingIntervalServer);
		removeUserData("sampligTimes");
		container.samplingTimes = null;

	}

	public long[] getSamplingIntervalArray() {
		return container.samplingTimes;
	}

	
	public void setSamplingInterval(TLongArrayList samplingTimes) {
		setSamplingInterval( samplingTimes.toArray());
	}
	
	/**
	 * set times when the data are calculated
	 * 
	 * @param samplingTimes
	 *            - arrays of data milliseconds
	 */
	public void setSamplingInterval(long[] samplingTimes) {
		clearCache();
		container.samplingTimes = samplingTimes;
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

	}

	public void startBatch() {
		if (container.clientConnection.isStreamEnabled().get()) {
			return;
		}
		if (container.isBatchStart)
			return;
		container.isBatchStart = true;
		container.batchMetricKey = null;
		container.batchMetricsPosition = new ArrayList<String>();
		container.batchMetricsPortfolio = new ArrayList<String>();
		container.batchMetricPortfolioKeys = new ArrayList<String>();
		container.batchMetricPositionKeys = new ArrayList<String>();
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

	}

	public void setNoiseModelEnabled(String isNoiseFilterEnabled) {
		setParam("isNoiseModelEnabled", isNoiseFilterEnabled);

	}

	public boolean isNonGaussianModelEnabled() {
		return getParam("isNonGaussianModelEnabled").equals("true");
	}

	public void setNonGaussianModelEnabled(boolean isNonGaussianModelEnabled) {
		setParam("isNonGaussianModelEnabled", String.valueOf(isNonGaussianModelEnabled));

	}

	public String getShortSalesMode() {
		return getParam("shortSalesMode");
	}

	/**
	 * 
	 * @param shortSalesMode
	 *            = "lintner" or "markowitz"
	 */
	public void setShortSalesMode(String shortSalesMode) {
		setParam("shortSalesMode", shortSalesMode);

	}

	public String getPriceSamplingInterval() {
		return getParam("priceSamplingInterval");
	}

	public void setPriceSamplingInterval(String priceSamplingInterval) {
		setParam("priceSamplingInterval", priceSamplingInterval);

	}

	public String getIndexSymbol() {
		return container.portfolioData.getIndexSymbol();
	}

	/**
	 * 
	 * 
	 * @param factorModel
	 *            = "sim" for single index model or "direct" for direct model
	 * 
	 */
	public void setFactorModel(String factorModel) {
		setParam("factorModel", factorModel);

	}

	public String getFactorModel() {
		return getParam("factorModel");
	}

	public boolean isJumpsModelEnabled() {
		return getParam("isJumpsModelEnabled").equals("true");

	}

	/**
	 * set model type for filtering jumps
	 * 
	 * @param jumpsModel
	 *            = "moments" - filtering jumps happens only in the calculation
	 *            of moments and related metrics, "none" jumps is not filtering,
	 *            "all" filtering jumps happens for all metrics including price
	 *            as metrics
	 */
	public void setJumpsModel(String jumpsModel) {
		setParam("jumpsModel", jumpsModel);

	}

	public String getJumpsModel() {
		return getParam("jumpsModel");
	}

	/**
	 * 
	 * @param windowLengthString
	 *            "Xs" X -seconds; "Xm" X -minutes; "Xh" X - hours; "Xd" X
	 *            -days; "Xw" X -weeks; "Xmo" X - months; "Xy" X - years;
	 * 
	 *            "all" - cumulative value without window
	 * 
	 */
	public void setWindowLength(String windowLengthString) {
		setParam("windowLength", windowLengthString);

	}

	public String getWindowLength() {
		return getParam("windowLength");
	}

	/**
	 * time scale
	 * 
	 * "Xs" X -seconds; "Xm" X -minutes; "Xh" X - hours; "Xd" X -days; "Xw" X
	 * -weeks; "Xmo" X - months; "Xy" X - years;
	 * 
	 * 
	 * @param timeScaleSecString
	 */
	public void setTimeScale(String timeScaleSecString) {
		setParam("timeScale", timeScaleSecString);

	}

	public String getTimeScale() {
		return getParam("timeScale");
	}

	public boolean isDebug() {
		return container.isDebug;
	}

	public void setDebug(boolean isDebug) {
		container.isDebug = isDebug;
		container.clientConnection.setDebugModeEnabled(isDebug);
	}

	public long getNewDataId() {
		return container.portfolioData.getNextDataId();
	}

	public ClientConnection getClientConnection() {
		return container.clientConnection;
	}

	public void setClientConnection(ClientConnection clientConnection) {
		this.container.clientConnection = clientConnection;
	}

	/**
	 * transaction cost per share
	 * 
	 * @param value
	 */
	public void setTxnCostPerShare(double value) {
		setParam("txnCostPerShare", String.valueOf(value));
	}

	public double getTxnCostPerShare() {
		return Double.valueOf(getParam("txnCostPerShare"));
	}

	/**
	 * transaction cost fixed
	 * 
	 * @param value
	 */
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

	public void setDriftEnabled(String isDriftEnabled) {
		setParam("isDriftEnabled", isDriftEnabled);
	}

	/**
	 * 
	 * @param model
	 *            = "PortfolioEffect" or "RiskMetrics"
	 */
	public void setRiskMethodology(String model) {
		setParam("riskMethodology", model);
		if (getParam("priceSamplingInterval").equals("") && model.equals("RiskMetrics"))
			setParam("priceSamplingInterval", "1d");
	}

	public String getRiskMethodology() {
		return getParam("riskMethodology");
	}

	public void setLambdaRiskMetrics(double lambda) {
		setParam("lambdaRiskMetrics", String.valueOf(lambda));
	}

	public double getLambdaRiskMetrics() {
		return Double.valueOf(getParam("lambdaRiskMetrics"));
	}

	/**
	 * 
	 * 
	 * @param densityApproxModel
	 *            = "GLD" -generalized lambda distribution, "CORNISH_FISHER" -
	 *            Cornish Fisher expansion "NORMAL" - normal distribution
	 */

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

	public void setFractalPriceModelEnabled(boolean flag) {
		setParam("isFractalPriceModelEnabled", "" + flag);
	}

	public void setFractalPriceModelEnabled(String flag) {
		setParam("isFractalPriceModelEnabled", "" + flag);
	}

	public void setSynchonizationModelEnabled(String flag) {
		setParam("synchronizationModel", "" + flag);
	}

	public void setSynchonizationModelEnabled(boolean flag) {
		setParam("synchronizationModel", "" + flag);
	}

	public boolean getSynchronizationModelEnabled() {
		return Boolean.parseBoolean(getParam("synchronizationModel"));
	}

	public void setSpotWindowLength(String value) {
		setParam("spotWindowLength", value);
	}

	public void setTrainingModel(boolean flag) {
		setParam("trainingModel", "" + flag);
	}

	public void setTrainingModel(String flag) {
		setParam("trainingModel", "" + flag);
	}

	public String getTrainingModel() {
		return getParam("trainingModel");
	}

	public boolean isTrainingModel() {
		return Boolean.parseBoolean(getParam("trainingModel"));
	}

	public String getSpotWindowLength(String value) {
		return getParam("spotWindowLength");
	}

	public boolean isFractalPriceModelEnabled() {
		return Boolean.valueOf(getParam("isFractalPriceModelEnabled"));
	}

	public Metric findSymbols(String searchStr, int numResults) throws Exception {

		ArrayList<String> id = new ArrayList<String>();
		ArrayList<String> description = new ArrayList<String>();
		ArrayList<String> exchange = new ArrayList<String>();

		Metric allSymbols = getAllSymbolsList();
		String[] idStrings = allSymbols.getStringArray("id");
		String[] descriptionString = allSymbols.getStringArray("description");
		String[] exchangeString = allSymbols.getStringArray("exchange");

		for (int i = 0; i < idStrings.length; i++) {
			if (StringUtils.containsIgnoreCase(idStrings[i], searchStr) || StringUtils.containsIgnoreCase(descriptionString[i], searchStr)) {
				id.add(idStrings[i]);
				description.add(descriptionString[i]);
				exchange.add(exchangeString[i]);
			}
			if (id.size() >= numResults) {
				break;
			}
		}

		Metric result = new Metric();

		ArrayCache idCache = new ArrayCache(id.toArray(new String[0]));
		result.setData("id", idCache);

		ArrayCache descriptionCache = new ArrayCache(description.toArray(new String[0]));
		result.setData("description", descriptionCache);

		ArrayCache exchangeCache = new ArrayCache(exchange.toArray(new String[0]));
		result.setData("exchange", exchangeCache);

		return result;
	}

	public void initStreamSingleMetric() {

		container.clientConnection.stop();
		ClientConnection tClient = new ClientConnection();
		tClient.setUsername(container.clientConnection.getUsername());
		tClient.setPassword(container.clientConnection.getPassword());
		tClient.setApiKey(container.clientConnection.getApiKey());
		tClient.setHost(container.clientConnection.getHost());
		tClient.setPort(container.clientConnection.getPort());

		container.clientConnection = tClient;
		setParam("stream", "on");

	}

	public void initStreamSingleMetric(SimpleMetricUpdateCallback callback) {

		initStreamSingleMetric();
		setStreamRefreshCallbackPureData(callback);
	}

	public void initStream() {

		initStreamSingleMetric();
		startBatch();
	}

	public void initStream(MetricUpdateCallback callback) {

		initStream();
		setStreamRefreshCallback(callback);
	}

	public Metric startStream() {
		return finishBatch();
	}

	public void stopStream() {
		container.clientConnection.stopStream();
		removeParam("stream");
		// clearCache();
	}

	public void setStreamRefreshCallback(MetricUpdateCallback streamRefreshCallback) {
		container.clientConnection.setStreamRefreshCallback(streamRefreshCallback);
	}

	public void setStreamRefreshCallbackPureData(SimpleMetricUpdateCallback streamRefreshCallbackPureData) {
		container.clientConnection.setStreamRefreshCallbackPureData(streamRefreshCallbackPureData);
	}

	public long getId() {
		return container.portfolioData.getPortfolioId();
	}

	public boolean isNaNFiltered() {
		return container.portfolioData.isNaNFiltered();
	}

	public void setNaNFiltered(boolean isNaNFiltered) {
		container.portfolioData.setNaNFiltered(isNaNFiltered);
	}

	public boolean isNaN2Zero() {
		return container.portfolioData.isNaN2Zero();
	}

	public void setNaN2Zero(boolean isNaN2Zero) {
		container.portfolioData.setNaN2Zero(isNaN2Zero);
	}

	public static int parseWindowLength(String s) {

		String res[] = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

		boolean error = false;

		if (s.equals("all"))
			return -1;

		if (s.equals("last"))
			return 0;

		if (s.equals("none"))
			return -1;

		int number = 1;
		int scale = 0;
		if (res.length != 2) {
			error = true;

		} else {
			try {
				number = Integer.parseInt(res[0]);
			} catch (Exception e) {
				error = true;
			}

			if (res[1].equals("s"))
				scale = 1;
			if (res[1].equals("m"))
				scale = 60;
			if (res[1].equals("h"))
				scale = 60 * 60;
			if (res[1].equals("d"))
				scale = 23400;
			if (res[1].equals("w"))
				scale = 23400 * 5;
			if (res[1].equals("mo"))
				scale = 23400 * 21;
			if (res[1].equals("y"))
				scale = 23400 * 256;

			if (scale == 0)
				error = true;
		}

		if (error)
			return -1;

		return number * scale;

	}
	
	public Position[] getPositions(){
		
		Position[] positions = new Position[getSymbolNamesList().size()];
		
		int i=0;
		for(String e: getSymbolNamesList()){
			positions[i] = new Position(this, e);
			i++;
		}
		
		return positions;
		
	}
	
	public Position getPosition(String symbol){
		
		if(getSymbolNamesList().contains(symbol))
			return new Position(this, symbol);
		
		return null;
	}


}
