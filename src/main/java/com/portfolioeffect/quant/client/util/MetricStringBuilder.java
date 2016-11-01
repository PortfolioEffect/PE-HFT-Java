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
package com.portfolioeffect.quant.client.util;



import java.util.HashMap;

import com.google.gson.Gson;
import com.portfolioeffect.quant.client.type.MetricsTypeFull;


public class MetricStringBuilder {
	
	private HashMap<String,String> map = new HashMap<String, String>();

	
	public MetricStringBuilder setMetric(String metric){
		map.put("metric",""+metric);
		return this;	
	}
	
	public MetricStringBuilder setMetric(MetricsTypeFull metric) {
		map.put("metric", "" + metric);
		return this;

	}
	
	public MetricStringBuilder setPosition(String position){
		map.put("position",position);
		return this;
		
	}
	
	public MetricStringBuilder setWindowLength(String windowLength){
		map.put("windowLength",windowLength);
		return this;
		
	}
	
	
	
	public MetricStringBuilder setSortDirection(String sortDirection){
		map.put("sort_direction",sortDirection);//ascending  -- descending
		return this;
		
	}
	
	public MetricStringBuilder setSortDirectionToAscending(){
		map.put("sort_direction","ascending");//ascending  -- descending
		return this;
		
	}
	public MetricStringBuilder setSortDirectionToDescending(){
		map.put("sort_direction","descending");//ascending  -- descending
		return this;	
	}
	
	
	public MetricStringBuilder setValue(String metric){
		map.put("value",""+metric);
		return this;
		
	}
	
	public MetricStringBuilder setSortBy(String metric){
		map.put("sort_by",""+metric);
		return this;
		
	}
	
	
	public MetricStringBuilder setConfidenceInterval(double confidenceInterval){
		map.put("confidenceInterval",""+confidenceInterval);
		return this;
		
	}
	
	public MetricStringBuilder setConfidenceIntervalAlphaBeta( double confidenceIntervalAlpha, double confidenceIntervalBeta){
		map.put("confidenceIntervalAlpha",""+confidenceIntervalAlpha);
		map.put("confidenceIntervalBeta",""+confidenceIntervalBeta);
		return this;	
	}
	
	public MetricStringBuilder setPositionA(String position) {
		map.put("positionA", position);
		return this;

	}
	
	public MetricStringBuilder setPositionB(String position) {
		map.put("positionB", position);
		return this;

	}
	
	public MetricStringBuilder setLag(int lag){
		map.put("lag",""+lag);
		return this;
		
	}
	
	public MetricStringBuilder setThresholdReturn(double thresholdReturn){
		map.put("thresholdReturn",""+thresholdReturn);
		return this;
		
	}
	
	
	public MetricStringBuilder reset(){
		map.clear();	
		return this;
	}
	
	
	public  String getJSON(){
		Gson gson = new Gson();
		return gson.toJson(map);
	}
	
//	public MetricStringBuilder addKeyValue(String key,String value){
//		
//		map.put(key, value.replace('\"', '\''));
//		
//		
//		return this;
//		
//	}
//	
//	public MetricStringBuilder addJSONString(String key,String value){
//		
//		map.put(key, value.replace('\"', '\''));
//		
//		
//		return this;
//		
//	}

	public HashMap<String, String> getMap() {
		return map;
	}
	
	

}
