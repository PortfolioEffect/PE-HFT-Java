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

import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.util.LinearForecastBuilder;

/**
 * Container class for storing forecast model and its parameters.
 * 
 * @author oleg
 *
 */
public class Forecast {

	private LinearForecastBuilder builder;

	/**
	 * for example model="EWMA"or"HAR"or ... , window="20d", step = "1d",
	 * transform = "log"or "none", seasonalityInterval="none" or
	 * "30m",updateInterval="1m",valueType="forecast"
	 * 
	 * @param model
	 * @param window
	 * @param step
	 * @param transform
	 * @param seasonalityInterval
	 * @param updateInterval
	 * @param valueType
	 * @throws ComputeErrorException 
	 * 
	 */
	public Forecast(Metric metric, String model, String window, String step, String transform, String seasonalityInterval, String updateInterval,
			String valueType) throws ComputeErrorException{

		builder = new LinearForecastBuilder();

		// builder.setForecastModel(model);
		builder.setTransform(transform);
		builder.setRollingWindow(window);
		builder.setRegressionUpdateInterval(updateInterval);
		builder.setForecastStep(step);
		builder.setValueType(valueType);
		builder.setTimeShiftEnable(true);
		builder.setDependentVariable(metric.getMetric());

		if (!seasonalityInterval.equals("none"))
			builder.setSeasonInterval(seasonalityInterval);

		if (model.equals("HAR"))
			builder.setForecastModel("[{\"windowLength\":\"1d\"},{\"windowLength\":\"5d\"},{\"windowLength\":\"21d\"}]");

		else if (model.equals("EWMA"))
			builder.setForecastModel("[]");
		else
			builder.setForecastModel(model);
	}

	/**
	 * 
	 * 
	 */
	public Forecast(){
		builder = new LinearForecastBuilder();
		builder.setTimeShiftEnable(true);
	}
	
	/**
	 * 
	 * @param metric
	 * @throws ComputeErrorException 
	 * @throws Exception
	 */
	public Forecast(Metric metric) throws ComputeErrorException{
		builder = new LinearForecastBuilder();
		builder.setTimeShiftEnable(true);
		builder.setDependentVariable(metric.getMetric());
	}
	
	/**
	 * 
	 * @param transform
	 *            = "none" or "log"
	 * @return
	 */
	public Forecast setTransform(String transform) {
		builder.setTransform(transform);
		return this;
	}

	/**
	 * 
	 * @param window
	 * @return
	 */
	public Forecast setWindow(String window) {

		builder.setRollingWindow(window);
		return this;
	}

	/**
	 * 
	 * @param updateInterval
	 * @return
	 */
	public Forecast setUpdateInterval(String updateInterval) {
		builder.setRegressionUpdateInterval(updateInterval);
		return this;
	}

	/**
	 * 
	 * @param step
	 * @return
	 */
	public Forecast setForecastStep(String step) {
		builder.setForecastStep(step);
		return this;
	}

	/**
	 * 
	 * @param valueType
	 *            "forecast" or "error"
	 * @return
	 */
	public Forecast setValueType(String valueType) {

		builder.setValueType(valueType);
		return this;
	}

	public Forecast setTimeShiftEnable(boolean flag) {
		builder.setTimeShiftEnable(flag);
		return this;
	}

	/**
	 * 
	 * @param metric
	 * @return
	 * @throws ComputeErrorException 
	 * 
	 */
	public Forecast setDependentVariable(Metric metric) throws ComputeErrorException {
		builder.setDependentVariable(metric.getMetric());
		return this;
	}

	/**
	 * 
	 * @param seasonalityInterval
	 * @return
	 */
	public Forecast setDependentVariable(String seasonalityInterval) {
		builder.setSeasonInterval(seasonalityInterval);
		return this;
	}

	/**
	 * 
	 * @return
	 * 
	 * 
	 */
	public Forecast setForecastModel(String model) {
		if (model.equals("HAR"))
			builder.setForecastModel("[{\"windowLength\":\"1d\"},{\"windowLength\":\"5d\"},{\"windowLength\":\"21d\"}]");

		else if (model.equals("EWMA"))
			builder.setForecastModel("[]");
		else
			builder.setForecastModel(model);

		return this;

	}

	public LinearForecastBuilder getBuilder() {
		return builder;
	}

	/**
	 * Adds given metric as an explanatory variable to forecast model.
	 * 
	 * @param metric
	 * @return 
	 * @throws ComputeErrorException 
	 * 
	 */
	public Forecast input(Metric metric) throws ComputeErrorException{
		builder.addIndependentVariable(metric.getMetric());
		return this;
	}

	/**
	 * Adds given metric as an explanatory variable to forecast model.
	 * 
	 * @return Object of class forecast
	 * @throws ComputeErrorException 
	 * 
	 */
	public Metric apply() throws ComputeErrorException {

		return new Metric(builder.build());
	}

	/**
	 * 
	 * @param seasonalityInterval
	 * @return
	 */
	public 	Forecast setSeasonalityInterval(String seasonalityInterval){

		if (!seasonalityInterval.equals("none"))
		builder.setSeasonInterval(seasonalityInterval);
		return this;

	}
}
