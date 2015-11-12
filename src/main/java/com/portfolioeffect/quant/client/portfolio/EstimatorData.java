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

public class EstimatorData {

	private HashMap<String, String> settings;
	private long dataId;
	private ArrayCache indexPrice;
	private ArrayCache indexTimeMillisec;
	private long priceID;

	public EstimatorData(EstimatorData data) {

		indexPrice = data.indexPrice;
		indexTimeMillisec = data.indexTimeMillisec;
		dataId = data.dataId;
		priceID = data.priceID;
		this.settings = new HashMap<String, String>(data.settings);
		settings.put("portfolioId", "" + ClientConnection.getNewId());

	}

	public EstimatorData() {
		settings = new HashMap<String, String>();
		settings.put("fromTime", "#");
		settings.put("toTime", "#");
		dataId = 0;
		settings.put("portfolioId", "" + ClientConnection.getNewId());
		indexPrice = null;
		indexTimeMillisec = null;
		priceID = 0;

	}

	public String getIndexSymbol() {
		return settings.get("indexSymbol");
	}

	public void setIndexSymbol(String indexSymbol) {
		settings.put("indexSymbol", indexSymbol);
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
		settings.put("toTime", toTime);
	}

	public long getDataId() {
		return dataId;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}

	public long getPortfolioId() {
		return Long.valueOf(settings.get("portfolioId"));
	}

	public void setPortfolioId(long portfolioId) {
		settings.put("portfolioId", "" + ClientConnection.getNewId());

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

	public long getPriceID() {
		return priceID;
	}

	public void setPriceID(long priceID) {
		this.priceID = priceID;
	}

	public long getNextDataId() {

		return dataId++;
	}

	public HashMap<String, String> getSettings() {
		return settings;
	}

	public void setParam(String key, String value) {
		settings.put(key, value);
	}

	public String getParam(String key) {
		return settings.get(key);
	}

	public void setSettings(HashMap<String, String> settings) {
		this.settings = settings;
	}

	public void setSettingJSON(String JSONString) {
		Gson gson = new Gson();
		Type mapType = new TypeToken<HashMap<String, String>>() {
		}.getType();
		settings = gson.fromJson(JSONString, mapType);
	}

	public String getSettingJSON() {
		Gson gson = new Gson();
		return gson.toJson(settings);
	}

}
