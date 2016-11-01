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
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.result.LazyMetric;

public class LinearForecastBuilder extends LazyMetricBuilder{

	private String seasonInterval="";
	
	private Portfolio portfolio;
	
	private String forecastModel="";
	
	private LazyMetric dependentVariable;
	
	
	public LinearForecastBuilder(String jsonStr){
		this();
		setParam(jsonStr);		
	}
	public LinearForecastBuilder(){
		super();
		setParam("metric", "LINEAR_REGRESSION");		
		setParam("valueType", "forecast");

	}
	
	public LinearForecastBuilder(LinearForecastBuilder builder){
		super(builder);
		seasonInterval=builder.seasonInterval;
		portfolio = builder.portfolio;
		forecastModel=builder.forecastModel;
		dependentVariable = builder.dependentVariable;
		
		
	}
	
	
	
	public LazyMetric build() throws ComputeErrorException {
		
		makeForBuild();
		return super.build(portfolio);		
		
	}
	private void makeForBuild() throws ComputeErrorException{
		if(forecastModel!=null && forecastModel.length()>0){
			
			Gson gson = new Gson();
			Type type = new TypeToken<HashMap<String,String>[]>() {}.getType(); 
			HashMap<String,String>[] model = gson.fromJson(forecastModel, type);
			
			if(dependentVariable == null)
				throw new ComputeErrorException("dependent variable is not set");
			
			LazyMetricBuilder independentVariableBuilder = dependentVariable.getLazyMetricBuilder();
			
			
			for(HashMap<String,String> e: model){
				
				if(e.containsKey("forecastModelParam")){
					e.remove("forecastModelParam");
					setParam(e);
					continue;
				}
				
				independentVariableBuilder.setParam(e);			
				addIndependentVariable( independentVariableBuilder.build(portfolio));
			}
			forecastModel="";
		}
		
		if(seasonInterval.length()>0){
			SimpleLazyMetricBuilder seasonBuilder = new SimpleLazyMetricBuilder();
			seasonBuilder.setMetricName("SEASONALITY");
			seasonBuilder.setSeasonInterval(seasonInterval);
			LazyMetric season = seasonBuilder.build(portfolio);
			
			try {
				addIndependentVariable(season);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
	}
	
	public LazyMetric build( boolean isLeftOffCompute) throws ComputeErrorException{
		
		makeForBuild();
		return super.build(portfolio, isLeftOffCompute);		
		
	}
	
	public String buildStrRequest() throws ComputeErrorException {
		
		makeForBuild();
		return super.buildStrRequest(portfolio);		
		
	}
	
		
	public LinearForecastBuilder setTransform(String value) {
		setParam("transform", value);
		return this;
	}
	
	public LinearForecastBuilder setRegressionUpdateInterval(String value) {
		setParam("regressionUpdateInterval", value);
		return this;
	}

	public LinearForecastBuilder setValueType(String value) {
		setParam("valueType", value);
		return this;
	}
	
	public LinearForecastBuilder setTimeShiftEnable( boolean value) {
		super.setParam("timeShift", ""+value);
		return this;
	}

	public LinearForecastBuilder setRollingWindow(String value) {
		setParam("rollingWindow", value);
		return this;
	}

	public LinearForecastBuilder setForecastStep(String value) {
		
		super.setParam("forecastStep", value);
		return this;
	}

	public LinearForecastBuilder addIndependentVariable(LazyMetric value) throws ComputeErrorException {
		addToList("independentVariable", value);
		return this;
	}

	public LinearForecastBuilder setDependentVariable(LazyMetric value) throws ComputeErrorException  {
		
		portfolio  = value.getPortfolio();
		dependentVariable = value;
		setParam("dependentVariable", value);
		setParam("metricType",value.getLazyMetricBuilder().getParam("metricType"));
		return this;
	}
	
	
	public LinearForecastBuilder setSeasonInterval(String value) {
		seasonInterval = value;				
		
		return this;

	}
//	
//	public LazyMetric apply(String modelStr, LazyMetric dependentVariable) throws Exception{
//		portfolio = dependentVariable.getPortfolio();
//		
//		setDependentVariable(dependentVariable);
//		
//		setForecastModel(modelStr);
//		
//		
//				
//		return build();
//	}
	
	public LinearForecastBuilder setForecastModel(String modelStr) {
		
		forecastModel = modelStr;
		
		return this;
	}
		
}
