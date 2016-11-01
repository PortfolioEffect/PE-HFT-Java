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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.portfolio.Position;
import com.portfolioeffect.quant.client.result.LazyMetric;
import com.portfolioeffect.quant.client.type.MetricsTypeFull;

public class LazyMetricBuilder {

	private HashMap<String, String> map;

	private HashMap<String, List<String>> mapList;
	private int windowLength;
	private int rollingWindowLength;
	private int forecastStep;

	public LazyMetricBuilder(){
		map = new HashMap<String, String>();
		mapList = new HashMap<String, List<String>>();

	}
	
	public LazyMetricBuilder(LazyMetricBuilder builder){
		
		map = new HashMap<String, String>(builder.map);
		
		mapList = new HashMap<String, List<String>>();
		for(Entry<String, List<String> > e: builder.mapList.entrySet()){
			mapList.put(e.getKey(), new ArrayList<String>(e.getValue()));
		}
		
		windowLength = builder.windowLength;
		rollingWindowLength = builder.rollingWindowLength;
		forecastStep = builder.forecastStep;
	}
	
	public LazyMetricBuilder(String jsonStr){
		this();
		Gson gson = new Gson();
		Type type = new TypeToken<HashMap<String,String>>() {}.getType(); 
		HashMap<String, String> map = gson.fromJson(jsonStr, type);
		
		for(Entry<String,String> e: map.entrySet()){
			setParam(e.getKey(), e.getValue());
		}
		
	}
	
	public LazyMetricBuilder(Map<String, String> map){
		this();
		for(Entry<String,String> e: map.entrySet()){
			setParam(e.getKey(), e.getValue());
		}
		
	}
	
	
	public LazyMetric build(Portfolio portfolio){
		
		makeBeforeBuildPortfolio(portfolio);
			//windowLength =Portfolio.parseWindowLength(portfolio.getWindowLength());
		
		LazyMetric lazyMetric = portfolio.getLazyMetric(this);
		
		
		
		
		makeAfterBuildPortfolio(portfolio, lazyMetric);
		
		return lazyMetric;
		
	}

	private void makeAfterBuildPortfolio(Portfolio portfolio, LazyMetric lazyMetric) {
		if(!map.get("metric").contains("TIME_SHIFT") && map.containsKey("timeShift")  && map.get("timeShift").equals("true") && map.containsKey("forecastStep") ){
			LazyMetricBuilder timeShiftBuilder = new LazyMetricBuilder();
			timeShiftBuilder.setParam("metric","TIME_SHIFT");
			timeShiftBuilder.setParam("timeShift",map.get("forecastStep"));
			lazyMetric.setTimeShift(timeShiftBuilder.build(portfolio));
		}
	}

	private void makeBeforeBuildPortfolio(Portfolio portfolio) {
		String type = map.get("metric");
		if(!type.contains("PORTFOLIO_") && !type.contains("POSITION_")&& !type.contains("INDEX_"))
			map.put("metric", "PORTFOLIO_"+type);
		
		if(type.contains("POSITION_") || type.contains("INDEX_"))
				map.put("metric", "PORTFOLIO_"+type.split("_",2)[1]);
				
		
		if(!map.containsKey("windowLength"))
			setParam("windowLength", portfolio.getWindowLength());
		
		
	}
	
	public LazyMetric build(Portfolio portfolio, boolean isLeftOffCompute){
		
		boolean isMultibatch =portfolio.isMultiBatch();
		if(isLeftOffCompute){
			portfolio.clearCache();
			portfolio.setMultiBatchEnabled(true);			
		}
		
		makeBeforeBuildPortfolio(portfolio);
			//windowLength =Portfolio.parseWindowLength(portfolio.getWindowLength());
		LazyMetric lazyMetric = portfolio.getLazyMetric(this);
		
		
		
		
		makeAfterBuildPortfolio(portfolio, lazyMetric);
		
		
		if(isLeftOffCompute){
			portfolio.setMultiBatchEnabled(isMultibatch);			
		}
		
		return lazyMetric;
		
	}
	
	public String buildStrRequest(Portfolio portfolio){
		
		
		makeBeforeBuildPortfolio(portfolio);
	
		return portfolio.getStrRequest(this);	
		
		
	}
	
	
	
	public LazyMetric build(Position position,  boolean isLeftOffCompute){
		
		boolean isMultibatch =position.getPortfolio().isMultiBatch();
		if(isLeftOffCompute){
			position.getPortfolio().clearCache();
			position.getPortfolio().setMultiBatchEnabled(true);			
		}
		
		
		makeBeforeBuildPosition(position);
			//windowLength =Portfolio.parseWindowLength(position.getPortfolio().getWindowLength());
		
		LazyMetric lazyMetric = position.getPortfolio().getLazyMetric(this);
		
		makeAfterBuildPosition(position, lazyMetric);
		
		
		if(isLeftOffCompute){
			position.getPortfolio().setMultiBatchEnabled(isMultibatch);			
		}
		
		return lazyMetric;
		
	}

	private void makeAfterBuildPosition(Position position, LazyMetric lazyMetric) {
		if(!map.get("metric").contains("TIME_SHIFT") && map.containsKey("timeShift")  && map.get("timeShift").equals("true") && map.containsKey("forecastStep") ){
			LazyMetricBuilder timeShiftBuilder = new LazyMetricBuilder();
			timeShiftBuilder.setParam("metric","TIME_SHIFT");
			timeShiftBuilder.setParam("timeShift",map.get("forecastStep"));
			lazyMetric.setTimeShift(timeShiftBuilder.build(position.getPortfolio()));
		}
	}

	private void makeBeforeBuildPosition(Position position) {
		String type = map.get("metric");
		if(!type.contains("PORTFOLIO_") && !type.contains("POSITION_")&& !type.contains("INDEX_"))
			map.put("metric", "POSITION_"+type);
		

		if(type.contains("PORTFOLIO_") || type.contains("INDEX_"))
				map.put("metric", "POSITION_"+type.split("_",2)[1]);
				
		
		map.put("position", position.getName());
		
		if(!map.containsKey("windowLength"))
			setParam("windowLength", position.getPortfolio().getWindowLength());
	}
	
	public LazyMetric build(Position position){
		
		makeBeforeBuildPosition(position);
		
		LazyMetric lazyMetric = position.getPortfolio().getLazyMetric(this);
		
		makeAfterBuildPosition(position, lazyMetric);
		
		return lazyMetric;
		
	}
	
	public LazyMetricBuilder removeList(String key){
			mapList.remove(key);
			return this;
	}
	
	public LazyMetricBuilder removeParam(String key){
		map.remove(key);
		return this;
	}
	
	public LazyMetric build(Position positionA, Position positionB){
		
		String type = map.get("metric");
		map.put("metric", "POSITION_"+type);
		
		map.put("positionA", positionA.getName());
		map.put("positionB", positionB.getName());
		
		if(!map.containsKey("windowLength"))
			setParam("windowLength", positionA.getPortfolio().getWindowLength());
			//windowLength =Portfolio.parseWindowLength(positionA.getPortfolio().getWindowLength());
		
		LazyMetric lazyMetric = positionA.getPortfolio().getLazyMetric(this);
		
		return lazyMetric;
		
	}
	
	
	
	public LazyMetricBuilder addToList(String key, String value) {

		if (value.equals("windowLength")){
			windowLength = Math.max(windowLength, Portfolio.parseWindowLength(value));			
			
		}

		if (mapList.containsKey(key)) {
			mapList.get(key).add(value);
		} else {
			mapList.put(key, new ArrayList<String>());
			mapList.get(key).add(value);
		}

		return this;
	}
	
	public LazyMetricBuilder addToList(String key, Position value) {

		if (value.equals("windowLength")){
			windowLength = Math.max(windowLength, Portfolio.parseWindowLength(value.getName()));			
			
		}

		if (mapList.containsKey(key)) {
			mapList.get(key).add(value.getName());
		} else {
			mapList.put(key, new ArrayList<String>());
			mapList.get(key).add(value.getName());
		}

		return this;
	}


	public LazyMetricBuilder addToList(String key, LazyMetric lazyMetric) throws ComputeErrorException {

		if(lazyMetric.getLazyMetricBuilder()==null)
			throw new ComputeErrorException("LazyMetricBuilder is not set");
		addToList(key, lazyMetric.getLazyMetricBuilder());
		
		
		return this;

	}
	
	
	
	private LazyMetricBuilder addToList(String key, LazyMetricBuilder lazyMetric) {

		
		String value="";
		Gson gson = new Gson();
		value = gson.toJson(lazyMetric.getParamsMap());
		
		addToList(key, value);
		
		windowLength = Math.max(windowLength, lazyMetric.windowLength);
		rollingWindowLength = Math.max(rollingWindowLength, lazyMetric.rollingWindowLength);
		forecastStep = Math.max(forecastStep, lazyMetric.forecastStep);
		
		
		return this;

	}


	public LazyMetricBuilder setParam(String key, String value) {
		
		if(value.length()==0)
			return removeParam(key);
			
		
			
		
		if (key.equals("windowLength"))
			windowLength = Portfolio.parseWindowLength(value);
			
		if(key.equals("rollingWindow"))
			rollingWindowLength = Portfolio.parseWindowLength(value);
			
		if(key.equals("forecastStep"))
			forecastStep = Portfolio.parseWindowLength(value);
			
		
		if(!map.containsKey("metric") && key.equals("metric")){
			
			String metricTpe = value.replaceFirst("POSITION_", "");
			metricTpe = metricTpe.replaceFirst("PORTFOLIO_", "");				
			map.put("metricType", metricTpe);
		}
		
		
		map.put(key, value);
		return this;
	}
	
	
	public LazyMetricBuilder setParam(String jsonStr) {
		
		Gson gson = new Gson();
		Type type = new TypeToken<HashMap<String,String>>() {}.getType(); 
		HashMap<String, String> map = gson.fromJson(jsonStr, type);
		
		for(Entry<String,String> e: map.entrySet()){
			setParam(e.getKey(), e.getValue());

		}
		return this;
	}
	
	
	public LazyMetricBuilder setParam(HashMap<String,String> mapParam) {
		
		
		for(Entry<String,String> e: mapParam.entrySet()){
			setParam(e.getKey(), e.getValue());

		}
		return this;
	}
	
	
	public LazyMetricBuilder setParam(String key, LazyMetric lazyMetric) throws ComputeErrorException {
		if(lazyMetric.getLazyMetricBuilder()==null)
			throw new ComputeErrorException("LazyMetricBuilder is not set");
		setParam(key, lazyMetric.getLazyMetricBuilder());		
		return this;
	}
	
	private LazyMetricBuilder setParam(String key, LazyMetricBuilder lazyMetric) {
		String value="";
		Gson gson = new Gson();
		value = gson.toJson(lazyMetric.getParamsMap());
		
		setParam(key, value);
		windowLength = Math.max(windowLength, lazyMetric.windowLength);
		rollingWindowLength = Math.max(rollingWindowLength, lazyMetric.rollingWindowLength);
		forecastStep = Math.max(forecastStep, lazyMetric.forecastStep);
		
		return this;
	}


	
	public LazyMetricBuilder reset() {
		map.clear();
		mapList.clear();
		windowLength =0;
		rollingWindowLength =0;
		forecastStep =0;
		
		return this;
	}

	private HashMap<String, String> getParamsMap() {

		HashMap<String, String> fullMap = new HashMap<String, String>(map);

		Gson gson = new Gson();
		for (String e : mapList.keySet()) {
			fullMap.put(e, gson.toJson(mapList.get(e)));
		}

		return fullMap;
	}
	
	public String getJsonString() {

		HashMap<String, String> fullMap = new HashMap<String, String>(map);

		Gson gson = new Gson();
		for (String e : mapList.keySet()) {
			fullMap.put(e, gson.toJson(mapList.get(e)));
		}

		return gson.toJson(fullMap);
	}

		
	public int getActualWindowLenght(){
		return forecastStep +windowLength +rollingWindowLength;
	}

	public String getParam(String key) {

		return map.get(key);
	}

	public boolean contains(String string) {
		
		return map.containsKey(string);
	}
	
	
}
