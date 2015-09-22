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
package com.portfolioeffect.quant.client.util;



import java.util.HashMap;

import com.google.gson.Gson;


public class MetricStringBuilder {
	
	private HashMap<String,String> map = new HashMap<String, String>();

	
	public MetricStringBuilder setMetric(String metric){
		map.put("metric",""+metric);
		return this;
		
	}
	
	public MetricStringBuilder setPosition(String position){
		map.put("position",position);
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
	
	
 

}
