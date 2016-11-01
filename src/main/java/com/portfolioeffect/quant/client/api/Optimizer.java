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
package com.portfolioeffect.quant.client.api;

import java.io.IOException;

import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.portfolio.optimizer.ForecastedValues;
import com.portfolioeffect.quant.client.portfolio.optimizer.PortfolioOptimizer;
import com.portfolioeffect.quant.client.portfolio.optimizer.StrategyOptimizer;
import com.portfolioeffect.quant.client.result.LazyMetric;
import com.portfolioeffect.quant.client.util.LinearForecastBuilder;

/**
 * Class for storing optimization goals and constraints.
 * 
 * @author oleg
 *
 */
public class Optimizer {
	private Portfolio portfolio;
	private PortfolioOptimizer optimizer;
	private boolean  wrongPortfolioMode;
	 

	public Optimizer(Metric goal, String direction, double approxError, double optimumProbability) {

		portfolio = goal.getMetric().getPortfolio();

		boolean isStrategyOptimizer = portfolio.getPortfolioMetricsMode().equals("portfolio") || portfolio.getPortfolioMetricsMode().equals("");

		if (isStrategyOptimizer)
			optimizer = new StrategyOptimizer(portfolio);
		else
			optimizer = new PortfolioOptimizer(portfolio);

		optimizer.setOptimizationGoal(goal.getMetric(), direction);
		optimizer.setErrorInDecimalPoints(approxError);
		optimizer.setGlobalOptimumProbability(optimumProbability);
		if (isStrategyOptimizer) {

			LinearForecastBuilder builder = new LinearForecastBuilder();

			builder.setRollingWindow("1s");
			builder.setRegressionUpdateInterval("1s");
			builder.setForecastStep("1s");

			((StrategyOptimizer) optimizer).setForecastBuilder(builder);
			((StrategyOptimizer) optimizer).setForecastStep(portfolio.getSamplingInterval());
		}

	}

	public Optimizer(Metric goal, String direction) {
		this(goal, direction, 1e-12, 0.99);
	}

	
	public void setForecastStep(String step) {
		((StrategyOptimizer) optimizer).setForecastStep(step);
	}

	/**
	 * Runs portfolio optimization procedure and returns corresponding metric.
	 * 
	 * @return
	 * @throws IOException 
	 * @throws ComputeErrorException 
	 * @throws Exception
	 */
	public Metric run() throws  ComputeErrorException {
		if(wrongPortfolioMode)
			return  new Metric(new LazyMetric("Wrong portfolio mode for using forecast."));
		return new Metric(optimizer.getOptimizedPortfolio());
	}

	/**
	 * Adds portfolio optimization constraint restricting optimal portfolio's
	 * beta to a certain range.
	 * 
	 * @param constraintMertic
	 *            object of class metric to be used for computing optimization
	 *            constraint
	 * @param constraintType
	 *            optimization constraint type: "=" - an equality constraint,
	 *            ">=" - an inclusive lower bound constraint, "<=" - an
	 *            inclusive upper bound constraint
	 * @param constraintValue
	 *            value to be used as a constraint boundary
	 */
	public Optimizer constraint(Metric constraintMertic, String constraintType, double constraintValue) {

		optimizer.addConstraint(constraintMertic.getMetric(), constraintType, constraintValue);
		return this;

	}
	
	public Optimizer setForecast(Forecast forecast) {

		try{
			((StrategyOptimizer) optimizer).setExpectedReturnForecastBuilder(forecast.getBuilder());
			((StrategyOptimizer) optimizer).setBetaForecastBuilder(forecast.getBuilder());
			((StrategyOptimizer) optimizer).setVarianceForecastBuilder(forecast.getBuilder());
			((StrategyOptimizer) optimizer).setCumulant3ForecastBuilder(forecast.getBuilder());
			((StrategyOptimizer) optimizer).setCumulant4ForecastBuilder(forecast.getBuilder());
		}catch(ClassCastException e){
			wrongPortfolioMode = true;
		}
			
			return this;


	}


	/**
	 * Sets user-defined forecasted values for a given metric and returns
	 * modified optimizer object. By default value of the metric at time "t" is
	 * used as a forecast for "t+1".
	 * 
	 * @param metricType
	 *            choose forecast metric type: "Beta" - position beta,
	 *            "Variance" - position variance, "ExpReturn" - position
	 *            expected return, "Cumulant3" - position 3-th cumulant,
	 *            "Cumulant4" - position 4-th cumulant
	 * @param forecast
	 * @return
	 */
	public Optimizer forecast(String metricType, Forecast forecast) {

		if (metricType.equals("ExpReturn")) {
			((StrategyOptimizer) optimizer).setExpectedReturnForecastBuilder(forecast.getBuilder());
			return this;
		}

		if (metricType.equals("Beta")) {
			((StrategyOptimizer) optimizer).setBetaForecastBuilder(forecast.getBuilder());
			return this;
		}

		if (metricType.equals("Variance")) {
			((StrategyOptimizer) optimizer).setVarianceForecastBuilder(forecast.getBuilder());
			return this;
		}

		if (metricType.equals("Cumulant3")) {
			((StrategyOptimizer) optimizer).setCumulant3ForecastBuilder(forecast.getBuilder());
			return this;
		}

		if (metricType.equals("Cumulant4")) {
			((StrategyOptimizer) optimizer).setCumulant4ForecastBuilder(forecast.getBuilder());
			return this;
		}

		return this;
	}

	public Optimizer forecast(String metricType, String symbol, double[] value, long[] time) {

		ForecastedValues forecastedValues = new ForecastedValues(portfolio);

		if (metricType.equals("ExpReturn")) {
			forecastedValues.setSymbolForecastedExpReturn(symbol, value, time);
			return this;
		}

		if (metricType.equals("Beta")) {
			forecastedValues.setSymbolForecastedBeta(symbol, value, time);
			return this;
		}

		if (metricType.equals("Variance")) {
			forecastedValues.setSymbolForecastedVariance(symbol, value, time);
			return this;
		}

		if (metricType.equals("Cumulant3")) {
			forecastedValues.setSymbolForecastedCumulant3(symbol, value, time);
			return this;
		}

		if (metricType.equals("Cumulant4")) {
			forecastedValues.setSymbolForecastedCumulant4(symbol, value, time);
			return this;
		}

		((StrategyOptimizer) optimizer).setForecastedValue(forecastedValues);

		return this;
	}

}
