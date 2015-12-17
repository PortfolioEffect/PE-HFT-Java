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
package com.portfolioeffect.quant.client.result;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.portfolioeffect.quant.client.portfolio.ArrayCache;
import com.portfolioeffect.quant.client.portfolio.ArrayCacheType;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.util.MessageStrings;

public class MethodResult {

	private boolean hasResult;
	private final boolean hasError;
	private final boolean hasWarning;
	private final String errorMessage;
	private final String warningMessage;

	private String message;
	private HashMap<String, String> infoParams = new HashMap<String, String>();
	private Map<String, Object> data = new HashMap<String, Object>();
	private Map<String, String> dataType = new HashMap<String, String>();

	public MethodResult() {
		this.hasError = false;
		this.hasWarning = false;
		this.errorMessage = "";
		this.message = "";
		this.warningMessage = "";
		this.hasResult = false;
	}

	public MethodResult(String errorMessage) {
		this.hasError = true;
		this.hasWarning = false;
		this.errorMessage = errorMessage;
		this.message = "";
		this.warningMessage = "";
		this.hasResult = false;
	}

	public MethodResult(String errorMessage, String warnnigMessage) {
		this.hasError = false;
		this.hasWarning = true;
		this.errorMessage = errorMessage;
		this.message = "";
		this.warningMessage = warnnigMessage;
		this.hasResult = false;
	}

	public MethodResult(boolean hasError, String errorMessage) {
		this.hasError = hasError;
		this.errorMessage = errorMessage;
		this.message = "";
		this.hasWarning = false;
		this.warningMessage = "";
		this.hasResult = false;

	}

	public HashMap<String, String> getInfoParams() {
		return infoParams;
	}

	public void setInfoParams(HashMap<String, String> infoParams) {
		this.infoParams = infoParams;
		this.hasResult = true;
	}

	public void setInfo(HashMap<String, String> info) {
		infoParams.putAll(info);
		this.hasResult = true;
		
	}

	public String getDataType(String key) {
		return dataType.get(key);
	}

	public void setData(String key, ArrayCache value) {
		data.put(key, value);
		dataType.put(key, value.getType().toString());
		this.hasResult = true;
	}

	public ArrayCache getDataArrayCache(String key) {

		return (ArrayCache) data.get(key);
	}

	public Object getData(String key) {

		return data.get(key);
	}

	public String getDataString(String key) {

		return (String) data.get(key);
	}

	public String[] getDataNames() {
		return data.keySet().toArray(new String[data.keySet().size()]);
	}

	public Portfolio getPortfolio(String key) {

		return (Portfolio) data.get(key);
	}

	public void setPortfolio(String key, Portfolio portfolio) {
		dataType.put(key, "PORTFOLIO");
		data.put(key, portfolio);
		this.hasResult = true;
	}

	public long[] getLongArray(String key) {
		try {
			if(data.containsKey("value"))
				return ((ArrayCache) data.get(key)).getLongArray( getDataArrayCache("value") );
			else
				return ((ArrayCache) data.get(key)).getLongArray();
			
		} catch (IOException e) {
			throw new RuntimeException(e);

		}
	}

	public long getLastLong(String key) {

		long[] x = new long[] { -1 };
		try {
			x = ((ArrayCache) data.get(key)).getLongArray(getDataArrayCache("value"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return x[x.length - 1];

	}

	public double getLastDouble(String key) {
		double[] x = new double[] { 0 };
		try {
			x = ((ArrayCache) data.get(key)).getDoubleArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return x[x.length - 1];
	}

	public double[] getLastDoubleArray(String key) {

		double[][] x = new double[][] { { 0 } };
		try {
			x = ((ArrayCache) data.get(key)).getDoubleMatrix();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return x[x.length - 1];

	}

	public double[] getDoubleArray(String key) {
		try {
			return ((ArrayCache) data.get(key)).getDoubleArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public double[][] getDoubleMatrix(String key) {
		try {
			return ((ArrayCache) data.get(key)).getDoubleMatrix();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public String getInfoParam(String key) {
		if (!infoParams.containsKey(key))
			return String.format(MessageStrings.NO_PARAM, key);
		else
			return infoParams.get(key);
	}

	public String getParamName() {
		String params = "";

		for (String e : infoParams.keySet())
			params += " " + e;

		return params;
	}

	public int getValueInt(String key) {
		if (!infoParams.containsKey(key))
			return 0;
		else
			return Integer.parseInt(infoParams.get(key));
	}

	public boolean hasWarning() {
		return hasWarning;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;

	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public boolean hasError() {
		return hasError;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void makeLastResultOnlyDouble(String key) {
		try {
			ArrayCache newResult = new ArrayCache(ArrayCacheType.NULL);
			double[] resultArray = null;
			try {
				resultArray = getDoubleArray(key);
			} catch (Exception e) {

			}

			if (resultArray.length == 0)
				return;

			newResult = new ArrayCache(new double[] { resultArray[resultArray.length - 1] });
			newResult.closeOut();
			setData(key, newResult);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void makeLastResultOnlyLong(String key) {
		try {
			ArrayCache newTime = new ArrayCache(ArrayCacheType.NULL);
			long[] timeArray = null;
			try {
				timeArray = getLongArray(key);
			} catch (Exception e) {

			}
			newTime = new ArrayCache(new long[] { timeArray[timeArray.length - 1] });
			newTime.closeOut();
			setData(key, newTime);
		} catch (IOException e) {
			throw new RuntimeException(e);

		}

	}

	public void makeLastResultOnlyDouble(String key, int batchSize) {

		try {

			ArrayCache newResult = new ArrayCache(ArrayCacheType.NULL);
			double[] resultArray = null;
			try {
				resultArray = getDoubleArray(key);
			} catch (Exception e) {

			}
			double[] valuesLast = new double[batchSize];

			if (resultArray.length == 0)
				return;

			for (int i = 0; i < batchSize; i++)
				valuesLast[i] = resultArray[resultArray.length - batchSize + i];
			newResult = new ArrayCache(valuesLast);

			setData(key, newResult);

		} catch (IOException e) {
			throw new RuntimeException(e);

		}
	}

	public void makeLastResultOnlyLong(String key, int batchSize) {

		try {

			ArrayCache newTime = new ArrayCache(ArrayCacheType.NULL);
			long[] timeArray = null;

			timeArray = getLongArray(key);

			newTime = new ArrayCache(new long[] { timeArray[timeArray.length - 1] });

			setData(key, newTime);
		} catch (IOException e) {
			throw new RuntimeException(e);

		}
	}

		
	public String[] getStringArray(String key) {
		try {
			return ((ArrayCache) data.get(key)).getStringArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public boolean isHasResult() {
		return hasResult;
	}
	
	public boolean isNaNFiltered() {
		if(data.containsKey("value"))
			return getDataArrayCache("value").isNaNFiltered();
		return true;
	}

	public void setNaNFiltered(boolean isNaNFiltered) {
		if(data.containsKey("value"))
			getDataArrayCache("value").setNaNFiltered(isNaNFiltered);
		
	}

	public boolean isNaN2Zero() {
		if(data.containsKey("value"))
			return getDataArrayCache("value").isNaN2Zero();
		return true;
	}

	public void setNaN2Zero(boolean isNaN2Zero) {
		if(data.containsKey("value"))
			getDataArrayCache("value").setNaN2Zero(isNaN2Zero);
	}


}
