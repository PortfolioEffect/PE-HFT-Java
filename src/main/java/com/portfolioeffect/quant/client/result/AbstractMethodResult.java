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

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.portfolio.ArrayCache;
import com.portfolioeffect.quant.client.portfolio.ArrayCacheType;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.portfolio.PortfolioContainer;
import com.portfolioeffect.quant.client.util.Console;
import com.portfolioeffect.quant.client.util.DateTimeUtil;
import com.portfolioeffect.quant.client.util.MessageStrings;

public abstract class AbstractMethodResult {

	protected boolean isDebug = false;

	protected boolean hasResult;
	protected boolean hasError;
	protected boolean hasWarning;
	protected String errorMessage;
	protected String warningMessage;

	
	protected String message;
	protected HashMap<String, String> infoParams = new HashMap<String, String>();
	protected Map<String, Object> data = new HashMap<String, Object>();
	protected Map<String, String> dataType = new HashMap<String, String>();
	
	protected abstract void computeMetric() throws ComputeErrorException;
	public abstract void compute();
	

	public AbstractMethodResult() {
		this.hasError = false;
		this.hasWarning = false;
		this.errorMessage = "";
		this.message = "";
		this.warningMessage = "";
		this.hasResult = false;
		
	}

		public AbstractMethodResult(String errorMessage) {
		this.hasError = true;
		this.hasWarning = false;
		this.errorMessage = errorMessage;
		this.message = "";
		this.warningMessage = "";
		this.hasResult = false;
	}

	public AbstractMethodResult(String errorMessage, String warnnigMessage) {
		this.hasError = false;
		this.hasWarning = true;
		this.errorMessage = errorMessage;
		this.message = "";
		this.warningMessage = warnnigMessage;
		this.hasResult = false;
	
	}

	public AbstractMethodResult(boolean hasError, String errorMessage) {
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

	
	
	public String getDataType(String key) throws ComputeErrorException {
		computeMetric();
		return dataType.get(key);
	}

	public void setData(String key, ArrayCache value){
		if(value.isAllNaN()){
			hasError = true;
			errorMessage="Incomplete data for request";			
		}
		data.put(key, value);
		dataType.put(key, value.getType().toString());
		this.hasResult = true;
	}
	
	public void setData(String key, double[] value) throws IOException {
		ArrayCache arrayValue = new ArrayCache(value);
		
		if(arrayValue.isAllNaN()){
			hasError = true;
			errorMessage="Incomplete data for request";			
		}
		data.put(key, arrayValue);
		dataType.put(key, arrayValue.getType().toString());
		this.hasResult = true;
	}
	
	public void setData(String key, long[] value) throws IOException {
		
		ArrayCache arrayValue = new ArrayCache(value);
		data.put(key, arrayValue);
		dataType.put(key, arrayValue.getType().toString());
		this.hasResult = true;
	}
	
	public void setValue(double[] value) throws IOException {
		setData("value", value);
	}
	
	public void setTime(long[] value) throws IOException {
		setData("time", value);
	}

	
	public ArrayCache getDataArrayCache(String key) throws ComputeErrorException {
		computeMetric();
		return (ArrayCache) data.get(key);
	}

	public void setData(String key, ArrayCache[] value) {
		data.put(key, value);
		dataType.put(key, value[0].getType().toString());
		this.hasResult = true;
	}

	public ArrayCache[] getDataArrayCacheArray(String key) throws ComputeErrorException {
		computeMetric();
		return (ArrayCache[]) data.get(key);
	}

	public Object getData(String key) throws Exception {
		computeMetric();
		return data.get(key);
	}

	public String getDataString(String key) throws ComputeErrorException {
		computeMetric();
		return (String) data.get(key);
	}

	public String[] getDataNames() throws ComputeErrorException {
		computeMetric();
		return data.keySet().toArray(new String[data.keySet().size()]);
	}

	public Portfolio getPortfolio(String key) throws ComputeErrorException {
		computeMetric();
		return (Portfolio) data.get(key);
	}

	public void setPortfolio(String key, Portfolio portfolio) {
		dataType.put(key, "PORTFOLIO");
		data.put(key, portfolio);
		this.hasResult = true;
	}

	public long[] getLongArray(String key) throws ComputeErrorException {
		computeMetric();
		try {
			if (data.containsKey("value")) {

				long[] data = ((ArrayCache) this.data.get(key)).getLongArray(getDataArrayCache("value"));
				if (isDebug)
					if (key.equals("time") && data.length > 0)
						Console.writeln("RESULT TIME(" + data.length + "): " + (new Timestamp(data[0] + DateTimeUtil.CLIENT_TIME_DELTA)) + "\t"
								+ (new Timestamp(data[data.length - 1] + DateTimeUtil.CLIENT_TIME_DELTA)));

				return data;
			} else {
				long[] data = ((ArrayCache) this.data.get(key)).getLongArray();
				if (isDebug)
					if (key.equals("time") && data.length > 0)
						Console.writeln("RESULT TIME(" + data.length + "): " + (new Timestamp(data[0] + DateTimeUtil.CLIENT_TIME_DELTA)) + "\t"
								+ (new Timestamp(data[data.length - 1] + DateTimeUtil.CLIENT_TIME_DELTA)));

				return data;
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		}
	}
	
	public long[] getTimeWithShift() throws ComputeErrorException {
		long[] dataL= getLongArray("time");
		double[] dataD = getDoubleArray("value");
		
		for(int i=0; i< dataL.length;i++){
			dataL[i]+= ((long)dataD[i])*1000;
		}
		
		return dataL;
		
	}
	
	public void addShiftToTime() throws ComputeErrorException, IOException {
		
		long[] data = getTimeWithShift();
		setTime(data);		
		
	}
	
	public void addShiftToTime(AbstractMethodResult result) throws ComputeErrorException, IOException {
		
		long[] data = result.getTimeWithShift();
		setTime(data);		
		
	}
	
	public void setTime(AbstractMethodResult result) throws ComputeErrorException{
		setData("time", result.getDataArrayCache("time"));
	}
	
	public long[] getTime() throws ComputeErrorException{
		return getLongArray("time");		
	}
	
	public long getLastTime() throws ComputeErrorException{
		return getLastLong("time");		
	}
	
	public double[] getValue() throws ComputeErrorException{
		return getDoubleArray("value");
	}
	
	public double getLastValue() throws ComputeErrorException{
		return getLastDouble("value");
	}
	
	
	public long[] getDoubleAsLongArray(String key) throws ComputeErrorException {
		computeMetric();
		try {
			
				long[] data = ((ArrayCache) this.data.get(key)).getDoubleAsLongArray();
				if (isDebug)
					if (key.equals("time") && data.length > 0)
						Console.writeln("RESULT TIME(" + data.length + "): " + (new Timestamp(data[0] + DateTimeUtil.CLIENT_TIME_DELTA)) + "\t"
								+ (new Timestamp(data[data.length - 1] + DateTimeUtil.CLIENT_TIME_DELTA)));

				return data;
			

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);

		}
	}

	public long getLastLong(String key) throws ComputeErrorException {
		computeMetric();
		long[] x = new long[] {};
		try {
			x = ((ArrayCache) data.get(key)).getLongArray(getDataArrayCache("value"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if(x.length==0)
			return -1;
		return x[x.length - 1];

	}

	public double getLastDouble(String key) throws ComputeErrorException {
		computeMetric();
		double[] x = new double[] {  };
		try {
			ArrayCache d =((ArrayCache) data.get(key));
			x =  d.getDoubleArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if(x.length==0)
			return Double.NaN;
		return x[x.length - 1];
	}

	public double[] getLastDoubleArray(String key) throws ComputeErrorException {
		computeMetric();
		double[][] x = new double[][] { {  } };
		try {
			x = ((ArrayCache) data.get(key)).getDoubleMatrix();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return x[x.length - 1];

	}

	public double[] getDoubleArray(String key) throws ComputeErrorException {
		computeMetric();
		try {
			double[] data = ((ArrayCache) this.data.get(key)).getDoubleArray();

			if (isDebug)
				if (data.length > 0)
					Console.writeln("RESULT DATA(" + data.length + "): " + data[0] + "\t" + data[data.length - 1]);

			return data;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public double[][] getDoubleMatrix(String key) throws ComputeErrorException {
		computeMetric();
		try {
			return ((ArrayCache) data.get(key)).getDoubleMatrix();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public String getInfoParam(String key) throws ComputeErrorException {
		computeMetric();
		if (!infoParams.containsKey(key))
			return String.format(MessageStrings.NO_PARAM, key);
		else
			return infoParams.get(key);
	}

	public String getParamName() throws ComputeErrorException {
		computeMetric();
		String params = "";

		for (String e : infoParams.keySet())
			params += " " + e;

		return params;
	}

	public int getValueInt(String key) throws ComputeErrorException {
		computeMetric();
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
		compute();
		return hasError;
	}

	public String getWarningMessage() {
		return warningMessage;
	}

	public void makeLastResultOnlyDouble(String key) throws ComputeErrorException {
		computeMetric();
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

			setData(key, newResult);

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public void makeLastResultOnlyLong(String key) throws ComputeErrorException {
		computeMetric();
		try {
			ArrayCache newTime = new ArrayCache(ArrayCacheType.NULL);
			long[] timeArray = null;
			try {
				timeArray = getLongArray(key);
			} catch (Exception e) {

			}
			newTime = new ArrayCache(new long[] { timeArray[timeArray.length - 1] });

			setData(key, newTime);
		} catch (IOException e) {
			throw new RuntimeException(e);

		}

	}

	public void makeLastResultOnlyDouble(String key, int batchSize) throws ComputeErrorException {
		computeMetric();

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

	public void makeLastResultOnlyLong(String key, int batchSize) throws ComputeErrorException {
		computeMetric();

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

	public String[] getStringArray(String key) throws ComputeErrorException {
		computeMetric();
		try {
			return ((ArrayCache) data.get(key)).getStringArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public boolean isHasResult() {
		compute();
		return hasResult;
	}

	public boolean isNaNFiltered() throws ComputeErrorException {
		compute();
		if (data.containsKey("value"))
			return getDataArrayCache("value").isNaNFiltered();
		return true;
	}

	public void setNaNFiltered(boolean isNaNFiltered) throws Exception {
		compute();
		if (data.containsKey("value"))
			getDataArrayCache("value").setNaNFiltered(isNaNFiltered);
		else
			throw new Exception("Error in MethodResult -1");

	}

	public boolean isNaN2Zero() throws ComputeErrorException {
		compute();
		if (data.containsKey("value"))
			return getDataArrayCache("value").isNaN2Zero();
		return true;
	}

	public void setNaN2Zero(boolean isNaN2Zero) throws Exception {
		compute();
		if (data.containsKey("value"))
			getDataArrayCache("value").setNaN2Zero(isNaN2Zero);
		else
			throw new Exception("Error in MethodResult -1");
	}

	public boolean isDebug() {
		return isDebug;
	}

	public void setDebug(boolean isDebug) {
		this.isDebug = isDebug;
	}

	@Override
	protected void finalize() throws Throwable {
		 data.clear(); 
		super.finalize();
	}
	
	
	//getIntArray("millisec_from_hour_start")
//	[10:28:16] Oleg Nechaev: getIntArray("hours")
//	[10:31:55] Oleg Nechaev: long[] getMillisec(int[] h, int[] millisec)

	
	
	

	//3600000
	
	public int[] getIntArray(String key) throws ComputeErrorException{
		
		long[] t = getLongArray("time");
		
		
			
		
		if(key.equals("hours")){
			
			int[] hour= new int[t.length];
			
			for(int i=0; i<t.length;i++)
				hour[i] = (int) (t[i]/3600000L);
			
			return hour;
			
		}
		
		
		if(key.equals("millisec_from_hour_start")){
			
			int[] millisec= new int[t.length];
			
			for(int i=0; i<t.length;i++)
				millisec[i] = (int) (t[i]%3600000L);
			
			return millisec;
			
		}
		
		
		return null;
		
	}
	
	
	public long[] getMillisec(int[] h, int[] millisec){
		
		long[] hour= new long[h.length];
		
		for(int i=0; i<h.length;i++)
			hour[i] = h[i]*3600000L + millisec[i];
		
		return hour;
		
	}
	
	
	
	public long[] getMillisec(TIntArrayList h, TIntArrayList millisec){
		
		long[] v= new long[h.size()];
		
		for(int i=0; i<h.size();i++)
			v[i] = h.getQuick(i)*3600000L + millisec.getQuick(i);
		
		return v;
		
	}
	
	public TLongArrayList toTLongArrayList(TIntArrayList h, TIntArrayList millisec){
		
		
		long[] v= new long[h.size()];
		
		for(int i=0; i<h.size();i++)
			v[i] = h.getQuick(i)*3600000L + millisec.getQuick(i);
		
		return new TLongArrayList(v);
		
		
		
	}
	
	public TLongArrayList toTLongArrayList(int[] h, int[] millisec){
		
		
		long[] v= new long[h.length];
		
		for(int i=0; i<h.length;i++)
			v[i] = h[i]*3600000L + millisec[i];
		
		return new TLongArrayList(v);
		
		
		
	}
	
}
