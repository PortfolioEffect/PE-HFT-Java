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

import java.util.HashMap;

import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.result.MethodResult;

public class StrategyOptimizer extends PortfolioOptimizer {

	private String forecasterType = "exp_smoothing";
	private String forecastPortfolioWindow="";
	private String forecastExpWindow="";

	public StrategyOptimizer(Portfolio portfolio, double localOptimStopStep, double globalOptimProbability) {
		super(portfolio, localOptimStopStep, globalOptimProbability);

	}

	public StrategyOptimizer(Portfolio portfolio) {
		super(portfolio);

	}

	private ForecastedValues forecastedValue = null;

	public void setForecastedValue(ForecastedValues forecastedValue) {
		this.forecastedValue = forecastedValue;
	}

	public ForecastedValues getForecastedValue() {
		return forecastedValue;
	}

	@Override
	public MethodResult getOptimizedPortfolio(boolean isResultSelfPortfolio) throws Exception {

		Portfolio optimizedPortfolio;

		try {

			HashMap<String, String> forecastParams = new HashMap<String, String>();
			forecastParams.put("section", "FORECAST_PARAMS");
			forecastParams.put("type", forecasterType);
			forecastParams.put("forecastPortfolioWindow", forecastPortfolioWindow);
			forecastParams.put("forecastExpWindow", forecastExpWindow);
			
			paramsBuffer.add(forecastParams);

			optimizedPortfolio = optimizationInit(isResultSelfPortfolio);

		} catch (Exception e1) {
			return new MethodResult(e1.getMessage());
		}

		if (forecastedValue != null) {

			MethodResult checkResult = forecastedValue.addToPortfolio(optimizedPortfolio);
			if (checkResult.hasError())
				return new MethodResult(checkResult.getErrorMessage());

		}

		return makeOptimization(optimizedPortfolio);

	}

	public String getForecasterType() {
		return forecasterType;
	}

	/**
	 * 
	 * "simple"
	 * "exp_smoothing"
	 */
	public void setForecasterType(String forecasterType) {
		this.forecasterType = forecasterType;
	}
	
	public String getForecastPortfolioWindow() {
		return forecastPortfolioWindow;
	}

	public void setForecastPortfolioWindow(String forecastPortfolioWindow) {
		this.forecastPortfolioWindow = forecastPortfolioWindow;
	}

	public String getForecastExpWindow() {
		return forecastExpWindow;
	}

	public void setForecastExpWindow(String forecastExpWindow) {
		this.forecastExpWindow = forecastExpWindow;
	}


}
