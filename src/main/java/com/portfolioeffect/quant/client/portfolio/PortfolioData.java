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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolioeffect.quant.client.ClientConnection;


public class PortfolioData {

	private static final HashSet<String> estimatorParams;
	private static final HashSet<String> portfoliorParams;
	
	static{
		estimatorParams= new HashSet<String>();
		portfoliorParams= new HashSet<String>();
		
		estimatorParams.add("fromTime");
		estimatorParams.add("toTime");
		estimatorParams.add("portfolioId");
		estimatorParams.add("indexSymbol");
		estimatorParams.add("spotWindowLength");
		estimatorParams.add("timeScale");
		estimatorParams.add("samplingInterval");
		estimatorParams.add("priceSamplingInterval");
		estimatorParams.add("jumpsModel");
		estimatorParams.add("isNoiseModelEnabled");
		estimatorParams.add("isFractalPriceModelEnabled");
		estimatorParams.add("isStartWhenBurn");
		estimatorParams.add("synchronizationModel");
		estimatorParams.add("riskMethodology");
		estimatorParams.add("stream");
		estimatorParams.add("trainingModel");
		
		portfoliorParams.add("windowLength");
		portfoliorParams.add("portfolioId");
		portfoliorParams.add("shortSalesMode"); 
		portfoliorParams.add("isHoldingPeriodEnabled");
		portfoliorParams.add("isRebalancingHistoryEnabled");
		portfoliorParams.add("isDriftEnabled");
		portfoliorParams.add("txnCostFixed");
		portfoliorParams.add("txnCostPerShare");
		portfoliorParams.add("densityApproxModel");
		portfoliorParams.add("isNoBurnMoment");
		portfoliorParams.add("portfolioMetricsMode");
			
				
		
	
	}
	
	
	private boolean isNaNFiltered = true;
	private boolean isNaN2Zero=false;
	
	private HashMap<String, String> estimatorSettings;
	
	private HashMap<String, String> portfolioSettings;

	private HashMap<String, ArrayCache> symbolQuantityMap;
	private HashMap<String, ArrayCache> symbolQuantityTimeMap;
	private HashMap<String, ArrayCache> symbolPriceMap;
	private HashMap<String, ArrayCache> symbolPriceTimeMap;
	private List<String> symbolNamesList;      
	private long dataId;
	

	private ArrayCache indexPrice;
	private ArrayCache indexTimeMillisec;
	private HashMap<String, Long> priceID;
	private HashMap<String, Long> quantityID;

	private Set<String> userPrice;

	public PortfolioData(PortfolioData data) throws IOException {


		this.symbolQuantityMap = new HashMap<String, ArrayCache>();
		for(String e: data.symbolQuantityMap.keySet()){
			symbolQuantityMap.put(e, new ArrayCache(  data.symbolQuantityMap.get(e).getIntArray() ) );
		}
		
		
		this.symbolQuantityTimeMap = new HashMap<String, ArrayCache>();
		for(String e: data.symbolQuantityTimeMap.keySet()){
			symbolQuantityTimeMap.put(e, new ArrayCache(  data.symbolQuantityTimeMap.get(e).getLongArray() ) );
		}
		
		this.symbolPriceMap = new HashMap<String, ArrayCache>();
		for(String e: data.symbolPriceMap.keySet()){
			symbolPriceMap.put(e, new ArrayCache(  data.symbolPriceMap.get(e).getDoubleArray() ) );
		}
		
		
		this.symbolPriceTimeMap = new HashMap<String, ArrayCache>();
		for(String e: data.symbolPriceTimeMap.keySet()){
			symbolPriceTimeMap.put(e, new ArrayCache(  data.symbolPriceTimeMap.get(e).getLongArray() ) );
		}
		
		
		
		this.symbolNamesList = new ArrayList<String>(data.symbolNamesList);
		this.dataId = data.dataId;
		

		if(data.indexPrice!=null)
			this.indexPrice = new ArrayCache(data.indexPrice.getDoubleArray());
		if(data.indexTimeMillisec!=null)
			this.indexTimeMillisec = new ArrayCache(data.indexTimeMillisec.getLongArray());
		this.priceID = new HashMap<String, Long>(data.priceID);
		this.quantityID = new HashMap<String, Long>(data.quantityID);

		this.userPrice = new HashSet<String>(data.userPrice);
		 
		estimatorSettings = new HashMap<String, String>(data.estimatorSettings);
		portfolioSettings = new HashMap<String, String>(data.portfolioSettings);
		
		estimatorSettings.put("portfolioId", ""+ClientConnection.getNewId());

	}

	
	public PortfolioData() {

		symbolQuantityMap = new HashMap<String, ArrayCache>();
		symbolQuantityTimeMap = new HashMap<String, ArrayCache>();
		symbolPriceMap = new HashMap<String, ArrayCache>();
		symbolPriceTimeMap = new HashMap<String, ArrayCache>();
		symbolNamesList = new ArrayList<String>();
		estimatorSettings = new HashMap<String, String>();
		portfolioSettings = new HashMap<String, String>();
		portfolioSettings.put("windowLength", "1d");
		estimatorSettings.put("fromTime","#");
		estimatorSettings.put("toTime", "#");
		dataId = 0;
		estimatorSettings.put("portfolioId", ""+ClientConnection.getNewId());

		indexPrice = null;
		indexTimeMillisec = null;
		priceID = new HashMap<String, Long>();
		quantityID = new HashMap<String, Long>();

		userPrice = new HashSet<String>();

	}

	public String getIndexSymbol() {
		return estimatorSettings.get("indexSymbol");
	}

	public void setIndexSymbol(String indexSymbol) {
		estimatorSettings.put("indexSymbol", indexSymbol);
	}

	public HashMap<String, ArrayCache> getSymbolQuantityMap() {
		return symbolQuantityMap;
	}

	public void setSymbolQuantityMap(HashMap<String, ArrayCache> symbolQuantityMap) {
		this.symbolQuantityMap = symbolQuantityMap;
	}

	public HashMap<String, ArrayCache> getSymbolQuantityTimeMap() {
		return symbolQuantityTimeMap;
	}

	public void setSymbolQuantityTimeMap(HashMap<String, ArrayCache> symbolQuantityTimeMap) {
		this.symbolQuantityTimeMap = symbolQuantityTimeMap;
	}

	public HashMap<String, ArrayCache> getSymbolPriceMap() {
		return symbolPriceMap;
	}

	public void setSymbolPriceMap(HashMap<String, ArrayCache> symbolPriceMap) {
		this.symbolPriceMap = symbolPriceMap;
	}

	public HashMap<String, ArrayCache> getSymbolPriceTimeMap() {
		return symbolPriceTimeMap;
	}

	public void setSymbolPriceTimeMap(HashMap<String, ArrayCache> symbolPriceTimeMap) {
		this.symbolPriceTimeMap = symbolPriceTimeMap;
	}

	public List<String> getSymbolNamesList() {
		return symbolNamesList;
	}

	public void setSymbolNamesList(List<String> symbolNamesList) {
		this.symbolNamesList = symbolNamesList;
	}

	public String getFromTime() {
		return estimatorSettings.get("fromTime");
	}

	public void setFromTime(String fromTime) {
		estimatorSettings.put("fromTime", fromTime);
	}

	public String getToTime() {
		return estimatorSettings.get("toTime");
	}

	public void setToTime(String toTime) {
		estimatorSettings.put("toTime",toTime);
	}

	public long getDataId() {
		return dataId;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}

	public long getPortfolioId() {
		return Long.valueOf( estimatorSettings.get("portfolioId") );
	}

	
	public void setPortfolioId(long portfolioId) {
		estimatorSettings.put("portfolioId", ""+ClientConnection.getNewId());
		
	}

	public ArrayCache getIndexPrice() {
		return indexPrice;
	}

	public void setIndexPrice(ArrayCache indexPrice) {
		this.indexPrice = indexPrice;
	}

	public ArrayCache getIndexTimeMillisec() {
		return indexTimeMillisec;
	}

	public void setIndexTimeMillisec(ArrayCache indexTimeMillisec) {
		this.indexTimeMillisec = indexTimeMillisec;
	}

	public HashMap<String, Long> getPriceID() {
		return priceID;
	}

	public void setPriceID(HashMap<String, Long> priceID) {
		this.priceID = priceID;
	}

	public HashMap<String, Long> getQuantityID() {
		return quantityID;
	}

	public void setQuantityID(HashMap<String, Long> quantityID) {
		this.quantityID = quantityID;
	}

	public long getNextDataId() {

		return dataId++;
	}

	public Set<String> getUserPrice() {
		return userPrice;
	}

	public void setUserPrice(Set<String> userPrice) {
		this.userPrice = userPrice;
	}
	
	public HashMap<String, String> getSettingsReal() {
		HashMap<String, String> settings = new HashMap<String, String>();
		
		
		
		for(Entry<String, String> e: estimatorSettings.entrySet())
			settings.put(e.getKey(), e.getValue());
			
		for(Entry<String, String> e: portfolioSettings.entrySet())
				settings.put(e.getKey(), e.getValue());
		
		
		return settings;
	}
	
	public HashMap<String, String> getSettings() {
		HashMap<String, String> settings = new HashMap<String, String>();
		
		
		
		for(Entry<String, String> e: estimatorSettings.entrySet())
			settings.put(renameParamsInverse(e.getKey()), e.getValue());
			
		for(Entry<String, String> e: portfolioSettings.entrySet())
				settings.put(renameParamsInverse(e.getKey()), e.getValue());
		
		
		return settings;
	}

	public HashMap<String, String> getPortfolioSettings() {
		return portfolioSettings;
	}

	public HashMap<String, String> getEstimatorSettings() {
		return estimatorSettings;
	}

	
	
	private String renameParams(String param){

		if(param.equals("holdingPeriodsOnly"))
			return "isHoldingPeriodEnabled";
		
		if(param.equals("noiseModel"))
			return "isNoiseModelEnabled";
		
		
		if(param.equals("fractalPriceModel"))
			return "isFractalPriceModelEnabled";


		if(param.equals("densityModel"))
			return "densityApproxModel";

		if(param.equals("driftTerm"))
			return "isDriftEnabled";

		if(param.equals("resultsSamplingInterval"))
			return "samplingInterval";

		if(param.equals("inputSamplingInterval"))
			return "priceSamplingInterval";

		
		return param;
		
	}
	
	
	private String renameParamsInverse(String param){

		if(param.equals("isHoldingPeriodEnabled"))
			return "holdingPeriodsOnly";
		
		if(param.equals("isNoiseModelEnabled"))
			return "noiseModel";
		
		
		if(param.equals("isFractalPriceModelEnabled"))
			return "fractalPriceModel";


		if(param.equals("densityApproxModel"))
			return "densityModel";

		if(param.equals("isDriftEnabled"))
			return "driftTerm";

		if(param.equals("samplingInterval"))
			return "resultsSamplingInterval";

		if(param.equals("priceSamplingInterval"))
			return "inputSamplingInterval";

		
		return param;
		
	}

	
	public void setParam(String key, String value){
		
		if(key.equals("resultsNAFilter")){
			setNaNFiltered(value.equals("true"));
			return;
		}
		
		key = renameParams(key);
		
		if(estimatorParams.contains(key))
			estimatorSettings.put(key, value);
		else
			portfolioSettings.put(key, value);
	}
	
	public void removeParam(String key){
		if(estimatorParams.contains(key))
			estimatorSettings.remove(key);
		else
			portfolioSettings.remove(key);
	}
	
	public boolean containsParam(String key){
		if(estimatorParams.contains(key))
			return estimatorSettings.containsKey(key);
		else
			return portfolioSettings.containsKey(key);
	}
	
	public String getParam(String key){
		
		if(key.equals("resultsNAFilter")){
			
			return ""+isNaNFiltered;
		}
		
		
		if(estimatorParams.contains(key))
			return estimatorSettings.get(key);
		else
			return portfolioSettings.get(key);
	}
	
	public void setSettings(HashMap<String, String> settings) {
		
		for(Entry<String,String>  e: settings.entrySet()){
			
			setParam(e.getKey(),e.getValue());
			
		}		
		
	}
	
	public void setSettingJSON(String JSONString){
		Gson gson = new Gson();
		Type mapType = new TypeToken<HashMap<String,String>>() {}.getType();
		HashMap<String,String> newSettings= gson.fromJson(JSONString, mapType);
		setSettings(newSettings );
	}
	
	public String getSettingJSON(){
		Gson gson = new Gson();
		HashMap<String, String> settings = new HashMap<String, String>();
		settings.putAll(estimatorSettings);
		settings.putAll(portfolioSettings);
		return  gson.toJson(settings);	
	}


	public  boolean isNaNFiltered() {
		return isNaNFiltered;
	}

	public void setNaNFiltered(boolean isNaNFiltered) {
		this.isNaNFiltered = isNaNFiltered;
	}

	public  boolean isNaN2Zero() {
		return isNaN2Zero;
	}

	public  void setNaN2Zero(boolean isNaN2Zero) {
		this.isNaN2Zero = isNaN2Zero;
	}
	
	public boolean checkEstimatorParams(String params){
		return estimatorParams.contains(params);
	}
	
	public boolean checkPortfolioParams(String params){
		return estimatorParams.contains(params) || portfoliorParams.contains(params);
	}
	public void setPortfolioSettings(HashMap<String, String> portfolioSettings) {
		this.portfolioSettings = portfolioSettings;
	}
	public void setEstimatorSettings(HashMap<String, String> estimatorSettings) {
		this.estimatorSettings = estimatorSettings;
	}

}
