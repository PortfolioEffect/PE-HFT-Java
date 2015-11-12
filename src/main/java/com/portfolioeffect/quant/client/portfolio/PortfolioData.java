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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolioeffect.quant.client.ClientConnection;


public class PortfolioData {


	private HashMap<String, String> settings;
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

	public PortfolioData(PortfolioData data) {


		this.symbolQuantityMap = new HashMap<String, ArrayCache>(data.symbolQuantityMap);
		this.symbolQuantityTimeMap = new HashMap<String, ArrayCache>(data.symbolQuantityTimeMap);
		this.symbolPriceMap = new HashMap<String, ArrayCache>(data.symbolPriceMap);
		this.symbolPriceTimeMap = new HashMap<String, ArrayCache>(data.symbolPriceTimeMap);
		this.symbolNamesList = new ArrayList<String>(data.symbolNamesList);
		this.dataId = data.dataId;
		

		this.indexPrice = data.indexPrice;
		this.indexTimeMillisec = data.indexTimeMillisec;
		this.priceID = new HashMap<String, Long>(data.priceID);
		this.quantityID = new HashMap<String, Long>(data.quantityID);

		this.userPrice = new HashSet<String>(data.userPrice);
		this.settings = new HashMap<String, String>(data.settings);
		
		settings.put("portfolioId", ""+ClientConnection.getNewId());

	}

	
	public PortfolioData() {

		symbolQuantityMap = new HashMap<String, ArrayCache>();
		symbolQuantityTimeMap = new HashMap<String, ArrayCache>();
		symbolPriceMap = new HashMap<String, ArrayCache>();
		symbolPriceTimeMap = new HashMap<String, ArrayCache>();
		symbolNamesList = new ArrayList<String>();
		settings = new HashMap<String, String>();
		settings.put("fromTime","#");
		settings.put("toTime", "#");
		dataId = 0;
		settings.put("portfolioId", ""+ClientConnection.getNewId());

		indexPrice = null;
		indexTimeMillisec = null;
		priceID = new HashMap<String, Long>();
		quantityID = new HashMap<String, Long>();

		userPrice = new HashSet<String>();

	}

	public String getIndexSymbol() {
		return settings.get("indexSymbol");
	}

	public void setIndexSymbol(String indexSymbol) {
		settings.put("indexSymbol", indexSymbol);
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
		return settings.get("fromTime");
	}

	public void setFromTime(String fromTime) {
		settings.put("fromTime", fromTime);
	}

	public String getToTime() {
		return settings.get("toTime");
	}

	public void setToTime(String toTime) {
		settings.put("toTime",toTime);
	}

	public long getDataId() {
		return dataId;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}

	public long getPortfolioId() {
		return Long.valueOf( settings.get("portfolioId") );
	}

	
	public void setPortfolioId(long portfolioId) {
		settings.put("portfolioId", ""+ClientConnection.getNewId());
		
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
	
	public HashMap<String, String> getSettings() {
		return settings;
	}

	
	public void setParam(String key, String value){
		settings.put(key, value);		
	}
	
	public String getParam(String key){
		return settings.get(key);
	}
	
	public void setSettings(HashMap<String, String> settings) {
		this.settings = settings;
	}
	
	public void setSettingJSON(String JSONString){
		Gson gson = new Gson();
		Type mapType = new TypeToken<HashMap<String,String>>() {}.getType();
		settings  = gson.fromJson(JSONString, mapType);
	}
	
	public String getSettingJSON(){
		Gson gson = new Gson();
		return  gson.toJson(settings);	
	}


}
