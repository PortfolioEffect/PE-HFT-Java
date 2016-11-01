/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2015 - 2016 Snowfall Systems, Inc.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.portfolioeffect.quant.client.ClientConnection;
import com.portfolioeffect.quant.client.result.Metric;
import com.portfolioeffect.quant.client.util.Console;
import com.portfolioeffect.quant.client.util.MessageStrings;

public class PortfolioContainer {

	static final int MAX_PDF_POINTS = 300;
	static final int NUMBER_OF_TRIES = 20;
	boolean isDebug = false;
	PortfolioData portfolioData = new PortfolioData();

	HashMap<String, ArrayCache> symbolUserDataMap;
	HashMap<String, ArrayCache> symbolUserDataTimeMap;

	Set<String> userData;
	ClientConnection clientConnection;
	ArrayList<String> batchMetricsPosition;
	ArrayList<String> batchMetricPortfolioKeys;
	ArrayList<String> batchMetricPositionKeys;
	ArrayList<String> batchMetricsPortfolio;
	String batchMetricKey=null;
	
	long[] samplingTimes = null;
	boolean isBatchStart = false;
	
	boolean isMultiBatch = true;
	boolean isBatchOn = true;
	

	List<CacheKey> cachedValueList;
	PortfolioCache portfolioCache;
	
	int windowLength=23400;
	 

	public PortfolioContainer(ClientConnection clientConnection) {
		
		this.portfolioCache = new PortfolioCache();
		this.clientConnection = clientConnection;
		this.symbolUserDataMap = new HashMap<String, ArrayCache>();
		this.symbolUserDataTimeMap = new HashMap<String, ArrayCache>();
		this.cachedValueList = new ArrayList<CacheKey>();
		this.userData = new HashSet<String>();
		this.batchMetricsPosition = null;
		this.batchMetricsPortfolio = null;

		
	}
	
	public PortfolioContainer(PortfolioContainer portfolio) throws IOException {
		
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
		batchMetricPortfolioKeys = null;
		batchMetricPositionKeys = null;
		
		windowLength = portfolio.windowLength;
		isMultiBatch = portfolio.isMultiBatch;
		
			
		
	}
	
	public void clearCache(){

		for (CacheKey key : cachedValueList) {
			try {
				portfolioCache.remove(key);
			} catch (IOException e) {
				processException(e);

			}
		}
		cachedValueList = new ArrayList<CacheKey>();
		//windowLength = 23400;
	}
	
	public Metric addUserData(String dataName, double[] value, long[] timeMillSec) {

		portfolioData.getPriceID().put(dataName, portfolioData.getNextDataId());

		if (value.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_DATA);
		} else if (timeMillSec.length == 0) {
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_TIME);
		}
		
		if(value.length!=timeMillSec.length)
			return new Metric(MessageStrings.WRONG_VECTOR_LEN_DATA_AND_TIME);
		
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

		return new Metric();
	}
	
	public void removeUserData(String symbol) {
		if (userData.contains(symbol)) {
			userData.remove(symbol);
			symbolUserDataMap.remove(symbol);
			symbolUserDataTimeMap.remove(symbol);
		}
		clearCache();
	}

	
	
	private Metric processException(IOException e) {
		if (isDebug) {
			Console.writeStackTrace(e);
		}
		if (e.getMessage() != null) {
			return new Metric(e.getMessage());
		} else {
			return new Metric(MessageStrings.ERROR_FILE);
		}
	}
}
