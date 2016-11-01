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
package com.portfolioeffect.quant.client.result;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.portfolio.PortfolioContainer;
import com.portfolioeffect.quant.client.portfolio.Position;
import com.portfolioeffect.quant.client.type.MetricsTypeFull;
import com.portfolioeffect.quant.client.util.LazyMetricBuilder;

public class LazyMetric extends AbstractMethodResult{

	private Portfolio portfolio;
	private String metricType;

	private HashMap<String,String> metricMapFull;
	private HashMap<String,String> metricParams;

	private final LazyMetricBuilder lazyMetricBuilder;




	private HashMap<String,String> portfolioParams;
	
	private boolean isComputed = false;
	
	private LazyMetric timeShift=null;

	public LazyMetric(Metric result){

		lazyMetricBuilder = null;
		isComputed = true;

		isDebug = result.isDebug;

		hasResult = result.hasResult;
		hasError = result.hasError;
		hasWarning = result.hasWarning;
		errorMessage = result.errorMessage;
		warningMessage = result.warningMessage;

		message = result.message;
		infoParams = result.infoParams;
		data = new HashMap<String, Object>(result.data);
		dataType = new HashMap<String, String>(result.dataType);
		portfolio = null;
	}

	public LazyMetric(PortfolioContainer container,String metricType, String metricTypeFull, LazyMetricBuilder builder)  {
		super();
		
		portfolio = new Portfolio(container);
		this.metricType = metricTypeFull;
		Gson gson = new Gson();
		Type mapType = new TypeToken<HashMap<String,String>>() {}.getType();
		
		metricMapFull= gson.fromJson(metricTypeFull, mapType);
		metricParams = gson.fromJson(metricType, mapType);
		portfolioParams = new HashMap<String, String>(portfolio.getPortfolioData().getPortfolioSettings());	
		portfolioParams.putAll(portfolio.getPortfolioData().getEstimatorSettings());
		
		lazyMetricBuilder = new LazyMetricBuilder(builder);
		

	}
	
	  
	
//	public static LazyMetric create(String metric, Portfolio portfolio){
//		
//		Gson gson = new Gson();
//		Type mapType = new TypeToken<HashMap<String,String>>() {}.getType();
//		HashMap<String,String> metricMap= gson.fromJson(metric, mapType);
//		
//		String type = metricMap.get("metric");
//		metricMap.put("metric", "PORTFOLIO_"+type);
//					
//		String metricType= gson.toJson(metricMap, mapType);
//		
//		return portfolio.getLazyMetric(metricType);
//	}
	
// public static LazyMetric createWithFullMetric(String metric, Portfolio portfolio){
//		
//	 	return portfolio.getLazyMetric(metric);
//		
//	}
	
//	public static LazyMetric create(String metric, Position position){
//		
//		Gson gson = new Gson();
//		Type mapType = new TypeToken<HashMap<String,String>>() {}.getType();
//		HashMap<String,String> metricMap= gson.fromJson(metric, mapType);
//		
//		String type = metricMap.get("metric");
//		metricMap.put("metric", "POSITION_"+type);
//		
//		metricMap.put("position", position.getName());
//					
//		String metricType= gson.toJson(metricMap, mapType);
//		
//		return position.getPortfolio().getLazyMetric(metricType);
//	}
	
//	public static LazyMetric create(String metric, Position positionA, Position positionB){
//		
//		Gson gson = new Gson();
//		Type mapType = new TypeToken<HashMap<String,String>>() {}.getType();
//		HashMap<String,String> metricMap= gson.fromJson(metric, mapType);
//		
//		String type = metricMap.get("metric");
//		metricMap.put("metric", "POSITION_"+type);
//		
//		metricMap.put("positionA", positionA.getName());
//		metricMap.put("positionB", positionB.getName());
//					
//		String metricType= gson.toJson(metricMap, mapType);
//		
//		return positionA.getPortfolio().getLazyMetric(metricType);
//	}
//	
	
	
	public LazyMetric(String errorMessage) {
		super(errorMessage);
		lazyMetricBuilder = null;
		
	}

	public LazyMetric(String errorMessage, String warnnigMessage) {
		super(errorMessage, warnnigMessage);
		lazyMetricBuilder= null;
		
	}

	public LazyMetric(boolean hasError, String errorMessage) {
		super(hasError, errorMessage);
		lazyMetricBuilder = null;
		
	}

	
	

	public String getMetricType() {
		return metricType;
	}
	
	public Portfolio getPortfolio(){
		return portfolio;
	}
	
	

	
	public void compute() {

		if (metricType == null)
			isComputed = true;

		if (isComputed) {
			return;
		}

		try {
			if (!portfolio.isContainsResult(metricType))
				portfolio.finishBatch();
		} catch (Exception e) {
			if (isDebug)
				e.printStackTrace();
		}

		

		Metric result = portfolio.getMetric(metricType);
		
		isComputed = true;

		isDebug = result.isDebug;

		hasResult = result.hasResult;
		hasError = result.hasError;
		hasWarning = result.hasWarning;
		errorMessage = result.errorMessage;
		warningMessage = result.warningMessage;

		message = result.message;
		infoParams = result.infoParams;
		data = new HashMap<String, Object>(result.data);
		dataType = result.dataType;
		portfolio.del();
		portfolio = null;
		
		if(timeShift!=null)
			try {
				addShiftToTime(timeShift);
			} catch (ComputeErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

	}

	@Override
	protected void computeMetric() throws ComputeErrorException {

		compute();

		if (hasError)
			throw new ComputeErrorException(errorMessage);

	}
	
	public String getDescription(){
		
		String description ="";
		
		MetricsTypeFull metric = MetricsTypeFull.valueOf(metricMapFull.get("metric").trim());
		
		description = metric.getDescription();
		
		if(metricMapFull.containsKey("goal")){
			description +="("+ getDescription(metricMapFull.get("goal"))+")";
		}
		
		String[][] args = metric.getMetricArguments();
		for(int i=0;i< args.length ;i++){
			if(i==0)
				description+=": ";
			else
				description+=", ";
				
			description+=args[i][1]+"="+ metricMapFull.get(args[i][0]);
		}
		description+="";
		
		return description;
		
	}
	
   private String getDescription(String str){
		
	  
	   Gson gson = new Gson();
		Type mapType = new TypeToken<HashMap<String,String>>() {}.getType();
		HashMap<String,String> metricMap= gson.fromJson(str, mapType);
		
	  
		String description ="";
		
		MetricsTypeFull metric = MetricsTypeFull.valueOf(metricMap.get("metric").trim());
		
		description = metric.getDescription();
		String[][] args = metric.getMetricArguments();
		for(int i=0;i< args.length ;i++){
			if(i==0)
				description+=": ";
			else
				description+=", ";
				
			description+=args[i][1]+"="+ metricMap.get(args[i][0]);
		}
		description+="";
		
		return description;
		
	}
	
	
	public String getSymbol(){
		
		if(metricMapFull.containsKey("position"))
			return metricMapFull.get("position");
		else
			return "Portfolio";
	}
	

	
	@Override
	protected void finalize() throws Throwable {
		portfolio = null;
		super.finalize();
	}
	
	public HashMap<String, String> getMetricParams() {
		return metricParams;
	}


	public HashMap<String, String> getPortfolioParams() {
		return portfolioParams;
	}
	
	public LazyMetricBuilder getLazyMetricBuilder() {
		return new LazyMetricBuilder( lazyMetricBuilder );
	}



	public void setTimeShift(LazyMetric timeShift) {
		this.timeShift = timeShift;
	}

	
	public LazyMetric addInput(LazyMetric[] metrics) throws Exception{
		LazyMetricBuilder builder = new LazyMetricBuilder(lazyMetricBuilder);
		
		LazyMetricBuilder builderIn = metrics[0].getLazyMetricBuilder();
		if(builderIn.contains("forecastStep") && builderIn.contains("timeShift")){
			builder.setParam("forecastStep", builderIn.getParam("forecastStep"));
			builder.setParam("timeShift", builderIn.getParam("timeShift"));
		}
			
		
		for(LazyMetric e:metrics){
			String metricType = e.getLazyMetricBuilder().getParam("metricType");
			builder.setParam(metricType, e);						
		}
		
		return builder.build(portfolio);
	}
	public LazyMetric addInput(LazyMetric metric) throws Exception{
		
		return addInput(new LazyMetric[]{metric});
		
	}
	
	
}
