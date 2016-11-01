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
package com.portfolioeffect.quant.client.portfolio.optimizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.result.LazyMetric;
import com.portfolioeffect.quant.client.result.Metric;
import com.portfolioeffect.quant.client.type.MetricsTypeFull;
import com.portfolioeffect.quant.client.util.LinearForecastBuilder;
import com.portfolioeffect.quant.client.util.SimpleLazyMetricBuilder;
import com.portfolioeffect.quant.client.util.SimpleMetricUpdateCallback;

public class StrategyOptimizer extends PortfolioOptimizer {

	
	private LinearForecastBuilder expectedReturnForecastBuilder;
	private LinearForecastBuilder varianceForecastBuilder;
	private LinearForecastBuilder cumulant3ForecastBuilder;
	private LinearForecastBuilder cumulant4ForecastBuilder;
	private LinearForecastBuilder betaForecastBuilder;
	private String forecastStep=""; 
	
	
	
	private ForecastedValues forecastedValue = null;

	public StrategyOptimizer(Portfolio portfolio, double localOptimStopStep, double globalOptimProbability) {
		super(portfolio, localOptimStopStep, globalOptimProbability);

	}

	public StrategyOptimizer(Portfolio portfolio) {
		super(portfolio);

	}

	

	public void setForecastedValue(ForecastedValues forecastedValue) {
		this.forecastedValue = forecastedValue;
	}

	public ForecastedValues getForecastedValue() {
		return forecastedValue;
	}

	@Override
	public LazyMetric getOptimizedPortfolio() throws ComputeErrorException  {

		
		try {
			optimizedPortfolio = new Portfolio(portfolio);
			resultPortfolio =  new Portfolio(portfolio);
		} catch (IOException e) {
			
			
			return new LazyMetric(e.getMessage());
		}		
		
		
		SimpleLazyMetricBuilder metricBuilder = new SimpleLazyMetricBuilder();
		LinearForecastBuilder forecastBuilder;
		LazyMetric lazyMetric;
		
		
		try {
			if(forecastStep.equals(""))
				throw new Exception("forecast step is not set");
			//String windowLength = portfolio.getSamplingInterval();
			
			optimizedPortfolio.addUserData("expTimeStep", new double[]{ Portfolio.parseWindowLength(forecastStep) }, new long[]{-1} );
			
			
			checkAllForecastBuilderSet();
			HashMap<String, String> forecastParams = new HashMap<String, String>();
			forecastParams.put("section", "FORECAST_PARAMS");
			String metricValue;
			ArrayList<String> metricList= new ArrayList<String>();
			Gson gson = new Gson();
			String metric;
			
			for(String position: portfolio.getSymbols()){
				metricBuilder.setMetric(MetricsTypeFull.POSITION_VARIANCE);
				metricBuilder.setWindowLength(forecastStep);
				metricBuilder.setPositionName(position);
				lazyMetric = metricBuilder.build(optimizedPortfolio, true);
				forecastBuilder = new LinearForecastBuilder(varianceForecastBuilder);
				forecastBuilder.setDependentVariable(lazyMetric);
				forecastBuilder.setForecastStep(forecastStep);
				//forecastBuilder.setTransform("none");
				metric= forecastBuilder.buildStrRequest();
				metricList.add(metric);
			}
			metricValue = gson.toJson(metricList);
			forecastParams.put("positionVariance", metricValue);
			metricList.clear();
			
			
			for(String position: portfolio.getSymbols()){
				metricBuilder.setMetric(MetricsTypeFull.POSITION_CUMULANT3);
				metricBuilder.setWindowLength(forecastStep);
				metricBuilder.setPositionName(position);
				lazyMetric = metricBuilder.build(optimizedPortfolio, true);
				
				forecastBuilder = new LinearForecastBuilder(cumulant3ForecastBuilder);
				forecastBuilder.setForecastStep(forecastStep);
				forecastBuilder.setDependentVariable(lazyMetric);
				forecastBuilder.setTransform("none");
				metric= forecastBuilder.buildStrRequest();
				metricList.add(metric);
			}
			metricValue = gson.toJson(metricList);
			forecastParams.put("positionCumulant3", metricValue);
			metricList.clear();

			
			for(String position: portfolio.getSymbols()){
				metricBuilder.setMetric(MetricsTypeFull.POSITION_CUMULANT4);
				metricBuilder.setWindowLength(forecastStep);
				metricBuilder.setPositionName(position);
				lazyMetric = metricBuilder.build(optimizedPortfolio, true);
				
				forecastBuilder = new LinearForecastBuilder(cumulant4ForecastBuilder);
				forecastBuilder.setDependentVariable(lazyMetric);
				forecastBuilder.setForecastStep(forecastStep);
				forecastBuilder.setTransform("none");
				metric= forecastBuilder.buildStrRequest();
				metricList.add(metric);
			}
			metricValue = gson.toJson(metricList);
			forecastParams.put("positionCumulant4", metricValue);
			metricList.clear();
			
			
			for(String position: portfolio.getSymbols()){
				metricBuilder.setMetric(MetricsTypeFull.POSITION_BETA);
				metricBuilder.setWindowLength(forecastStep);
				metricBuilder.setPositionName(position);
				lazyMetric = metricBuilder.build(optimizedPortfolio, true);
				
				forecastBuilder = new LinearForecastBuilder(betaForecastBuilder);
				forecastBuilder.setDependentVariable(lazyMetric);
				forecastBuilder.setForecastStep(forecastStep);
				forecastBuilder.setTransform("none");
				metric= forecastBuilder.buildStrRequest();
				metricList.add(metric);
			}
			metricValue = gson.toJson(metricList);
			forecastParams.put("positionBeta", metricValue);
			metricList.clear();


			for(String position: portfolio.getSymbols()){
				metricBuilder.setMetric(MetricsTypeFull.POSITION_EXPECTED_RETURN);
				metricBuilder.setWindowLength(forecastStep);
				metricBuilder.setPositionName(position);
				lazyMetric = metricBuilder.build(optimizedPortfolio, true);
				
				forecastBuilder = new LinearForecastBuilder(expectedReturnForecastBuilder);
				forecastBuilder.setDependentVariable(lazyMetric);
				forecastBuilder.setForecastStep(forecastStep);
				forecastBuilder.setTransform("none");
				metric= forecastBuilder.buildStrRequest();
				metricList.add(metric);
			}
			metricValue = gson.toJson(metricList);
			forecastParams.put("positionExpectedReturn", metricValue);
			metricList.clear();

			
			
			
			metricBuilder.setMetric(MetricsTypeFull.INDEX_EXPECTED_RETURN);
			metricBuilder.setWindowLength(forecastStep);
			lazyMetric = metricBuilder.build(optimizedPortfolio, true);
			
			forecastBuilder = new LinearForecastBuilder(expectedReturnForecastBuilder);
			forecastBuilder.setForecastStep(forecastStep);
			forecastBuilder.setDependentVariable(lazyMetric);
			//forecastBuilder.setTransform("none");
			metricValue= forecastBuilder.buildStrRequest();
			forecastParams.put("indexExpectedReturn", metricValue);
			
			
			
			
			metricBuilder.setMetric(MetricsTypeFull.INDEX_VARIANCE);
			metricBuilder.setWindowLength(forecastStep);
			lazyMetric = metricBuilder.build(optimizedPortfolio, true);
			
			forecastBuilder = new LinearForecastBuilder(varianceForecastBuilder);
			forecastBuilder.setForecastStep(forecastStep);
			forecastBuilder.setDependentVariable(lazyMetric);
			//forecastBuilder.setTransform("none");
			metricValue= forecastBuilder.buildStrRequest();
			forecastParams.put("indexVariance", metricValue);
			
			metricBuilder.setMetric(MetricsTypeFull.INDEX_CUMULANT3);
			metricBuilder.setWindowLength(forecastStep);
			lazyMetric = metricBuilder.build(optimizedPortfolio, true);
			
			forecastBuilder = new LinearForecastBuilder(cumulant3ForecastBuilder);
			forecastBuilder.setForecastStep(forecastStep);
			forecastBuilder.setDependentVariable(lazyMetric);
			forecastBuilder.setTransform("none");
			metricValue= forecastBuilder.buildStrRequest();
			forecastParams.put("indexCumulant3", metricValue);
			
			metricBuilder.setMetric(MetricsTypeFull.INDEX_CUMULANT4);
			metricBuilder.setWindowLength(forecastStep);
			lazyMetric = metricBuilder.build(optimizedPortfolio, true);
			
			forecastBuilder = new LinearForecastBuilder(cumulant4ForecastBuilder);
			forecastBuilder.setForecastStep(forecastStep);
			forecastBuilder.setDependentVariable(lazyMetric);
			forecastBuilder.setTransform("none");
			metricValue= forecastBuilder.buildStrRequest();
			forecastParams.put("indexCumulant4", metricValue);
			

			
			paramsBuffer.add(forecastParams);

			optimizationInit();

		} catch (Exception e1) {
			return new LazyMetric(e1.getMessage());
		}

		if (forecastedValue != null) {

			Metric checkResult = forecastedValue.addToPortfolio(optimizedPortfolio);
			if (checkResult.hasError())
				return new LazyMetric(checkResult.getErrorMessage());

		}

		return makeOptimization();

	}
	
	private void checkAllForecastBuilderSet() throws Exception {
		
		if(expectedReturnForecastBuilder ==null)
			throw new Exception("forecast bulider for expected return is not set");
		
		if(varianceForecastBuilder ==null)
			throw new Exception("forecast bulider for variance is not set");
		
		if(cumulant3ForecastBuilder ==null)
			throw new Exception("forecast bulider for cumulant3 is not set");
		
		if(cumulant4ForecastBuilder ==null)
			throw new Exception("forecast bulider for cumulant4 is not set");
		
		if(betaForecastBuilder == null)
			throw new Exception("forecast bulider for beta is not set");
			
		
	}

	@Override
	public LazyMetric getOptimizedPortfolioStream() throws Exception {
		
		
		optimizedPortfolio = new Portfolio(portfolio);
		resultPortfolio =  new Portfolio(portfolio);
		
		
		optimizedPortfolio.initStreamSingleMetric(new SimpleMetricUpdateCallback() {

			@Override
			public void onDataRefresh(float[] data, long[] time) {

//				Console.writeln("==>");
//				Console.writeln("" + (new Timestamp(time[0])));
//				for (int i = 0; i < data.length; i++)
//					Console.write(data[i] + "\t");
//				Console.writeln("\n>==");

				int nSymbols = resultPortfolio.getSymbolNamesList().size();
				int len = 0;
				for (int i = 0; i < time.length; i++)
					for (int k = 0; k < nSymbols; k++) {
						resultPortfolio.setStreamQuantity(resultPortfolio.getSymbolNamesList().get(k), (int) data[len], time[i]);
						len++;
					}

			}
		});

		
		try {

			HashMap<String, String> forecastParams = new HashMap<String, String>();
			forecastParams.put("section", "FORECAST_PARAMS");
			//forecastParams.put("type", forecasterType);
			//forecastParams.put("forecastPortfolioWindow", forecastPortfolioWindow);
			//forecastParams.put("forecastExpWindow", forecastExpWindow);
			
			paramsBuffer.add(forecastParams);
			optimizationInit();
		

		} catch (Exception e1) {
			return new LazyMetric(e1.getMessage());
		}

		if (forecastedValue != null) {

			Metric checkResult = forecastedValue.addToPortfolio(optimizedPortfolio);
			if (checkResult.hasError())
				return new LazyMetric(checkResult.getErrorMessage());

		}

		return makeOptimization();

		
	}
	public LinearForecastBuilder getExpectedReturnForecastBuilder() {
		return expectedReturnForecastBuilder;
	}

	public void setExpectedReturnForecastBuilder(LinearForecastBuilder expectedReturnForecastBuilder) {
		this.expectedReturnForecastBuilder = expectedReturnForecastBuilder;
	}

	public LinearForecastBuilder getVarianceForecastBuilder() {
		return varianceForecastBuilder;
	}

	public void setVarianceForecastBuilder(LinearForecastBuilder varianceForecastBuilder) {
		this.varianceForecastBuilder = varianceForecastBuilder;
	}

	public LinearForecastBuilder getCumulant3ForecastBuilder() {
		return cumulant3ForecastBuilder;
	}

	public void setCumulant3ForecastBuilder(LinearForecastBuilder cumulant3ForecastBuilder) {
		this.cumulant3ForecastBuilder = cumulant3ForecastBuilder;
	}

	public LinearForecastBuilder getCumulant4ForecastBuilder() {
		return cumulant4ForecastBuilder;
	}

	public void setCumulant4ForecastBuilder(LinearForecastBuilder cumulant4ForecastBuilder) {
		this.cumulant4ForecastBuilder = cumulant4ForecastBuilder;
	}

	public LinearForecastBuilder getBetaForecastBuilder() {
		return betaForecastBuilder;
	}

	public void setBetaForecastBuilder(LinearForecastBuilder betaForecastBuilder) {
		this.betaForecastBuilder = betaForecastBuilder;
	}

	public void setForecastBuilder(LinearForecastBuilder forecastBuilder) {
		if(expectedReturnForecastBuilder==null)
			expectedReturnForecastBuilder = forecastBuilder;
		
		if(varianceForecastBuilder==null)
			varianceForecastBuilder = forecastBuilder;
		
		if(cumulant3ForecastBuilder==null)
			cumulant3ForecastBuilder = forecastBuilder;
		
		if(cumulant4ForecastBuilder==null)
			cumulant4ForecastBuilder = forecastBuilder;
		
		if(betaForecastBuilder==null)
			betaForecastBuilder = forecastBuilder;	
		
	}
	
	public String getForecastStep() {
		return forecastStep;
	}

	public void setForecastStep(String forecastTimeStep) {
		this.forecastStep = forecastTimeStep;
	}



}
