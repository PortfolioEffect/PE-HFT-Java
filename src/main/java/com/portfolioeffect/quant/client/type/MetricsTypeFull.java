/*
 * #%L
 * ICE-9 Library
 * %%
 * Copyright (C) 2011 - 2012 Snowfall Systems, Inc.
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
/**
 *  SNOWFALL SYSTEMS, INC. CONFIDENTIAL CONTROLLED.  DO NOT COPY OR DISTRIBUTE FURTHER.
 *  (c) 2012 Snowfall Systems, Inc.  All rights reserved.
 */
package com.portfolioeffect.quant.client.type;

import java.util.ArrayList;


public enum MetricsTypeFull {


	PORTFOLIO_OPTIMIZATION("portfolio otimization"),

	PORTFOLIO_PDF(new String[][] { { "from", "from" }, { "to", "to" }, { "number", "number" } },"portfolio PDF"),
	POSITION_PDF(new String[][] {{ "position", "symbol" },  { "from", "from" }, { "to", "to" }, { "number", "number" } },"position PDF"),

	PORTFOLIO_HURST_EXPONENT("portfolio Hurst exponent"),
	POSITION_HURST_EXPONENT(new String[][] {  { "position", "symbol" } },"position Hurst exponent"),


	POSITION_MATRIX(new String[][] { { "value", "value" }  },"position matrix"),
	POSITION_SORT(new String[][] { { "sort_by", "sort by" }, { "sort_direction", "sort direction" } },"position sort"),

	PORTFOLIO_FRACTAL_DIMENSION("portfolio fractal dimension"),
	POSITION_FRACTAL_DIMENSION(new String[][] {  { "position", "symbol" } },"position fractal dimension"),


	PORTFOLIO_RETURN_AUTOCOVARIANCE(new String[][] {  { "lag", "time lag" } },"portfolio return autocovariance"),
	POSITION_RETURN_AUTOCOVARIANCE(new String[][] {  { "position", "symbol" }, { "lag", "time lag" } },"position return autocovariance"),
	INDEX_RETURN_AUTOCOVARIANCE(new String[][] {  { "lag", "time lag" } },"index return autocovariance"),


	PORTFOLIO_SORTINO_RATIO(new String[][] { { "thresholdReturn", "threshold return" } },"portfolio Sortino ratio"),
	PORTFOLIO_GAIN_LOSS_VARIANCE_RATIO("portfolio gain/loss variance ration"),
	PORTFOLIO_UPSIDE_DOWNSIDE_VARIANCE_RATIO(new String[][] { { "thresholdReturn", "threshold return" } },"portfolio upside/downside variance ratio"),
	PORTFOLIO_OMEGA_RATIO(new String[][] { { "thresholdReturn", "threshold return" } },"portfolio omega ratio"),
	PORTFOLIO_RACHEV_RATIO(new String[][] { { "confidenceIntervalAlpha", "confidence interval alpha" } , { "confidenceIntervalBeta", "confidence interval beta" } },"portfolio Rachev ratio"),
	PORTFOLIO_VAR(new String[][] { { "confidenceInterval", "confidence interval" } },"portfolio VaR"),
	PORTFOLIO_CVAR(new String[][] { { "confidenceInterval", "confidence interval" } },"portfolio CVaR"),
	PORTFOLIO_MAX_DRAWDOWN("portfolio maximum drawdown"),
	PORTFOLIO_DRAWDOWN("portfolio drawdown"),

	PORTFOLIO_DOWN_CAPTURE_RATIO("portfolio down capture ratio"),
	PORTFOLIO_UP_CAPTURE_RATIO("portfolio up capture ratio"),


	PORTFOLIO_DOWN_NUMBER_RATIO("portfolio down number ratio"),
	PORTFOLIO_UP_NUMBER_RATIO("portfolio up number ratio"),
	PORTFOLIO_DOWN_PERCENTAGE_RATIO("portfolio down percentage ratio"),
	PORTFOLIO_UP_PERCENTAGE_RATIO("portfolio up percentage ratio"),


	PORTFOLIO_CALMAR_RATIO("portfolio Calmar ratio"),
	PORTFOLIO_VALUE("portfolio value"),
	PORTFOLIO_RETURN("portfolio return"),
	PORTFOLIO_EXPECTED_RETURN("portfolio expexcted return"),
	PORTFOLIO_RETURN_DAILY("portfolio return daily"),

	PORTFOLIO_PROFIT("portfolio profit"),
	PORTFOLIO_TRANSACTION_COSTS_SIZE("portfolio transaction cost size"),
	PORTFOLIO_BETA("portfolio beta"),
	PORTFOLIO_ALPHA_JENSEN("portofolio Jensen's alpha"),
	PORTFOLIO_ALPHA("portfolio alpha"),
	PORTFOLIO_VARIANCE("portfolio variance"),
	PORTFOLIO_SKEWNESS("porfolio skewness"),
	PORTFOLIO_KURTOSIS("portfolio kurtosis"),

	PORTFOLIO_MOMENT3("portfolio third moment"),
	PORTFOLIO_MOMENT4("portfolio fourth moment"),
	PORTFOLIO_CUMULANT3("portfolio third cumulant"),
	PORTFOLIO_CUMULANT4("portfolio fourth cumulant"),


	PORTFOLIO_MOMENT1("portfolio first moment"),
	PORTFOLIO_MOMENT2("portfolio second moment"),
	PORTFOLIO_CUMULANT1("portoflio first moment"),
	PORTFOLIO_CUMULANT2("portfolio second moment"),


	PORTFOLIO_SHARPE_RATIO("portfolio Sharp ratio"),
	PORTFOLIO_INFORMATION_RATIO("portfolio information ratio"),

	USER_DATA(new String[][] { { "position", "symbol" } },"user data"),

	POSITION_WEIGHT(new String[][] { { "position", "symbol" } },"position weight"),
	
	PORTFOLIO_SHARPE_RATIO_MOD(new String[][] { { "confidenceInterval","confidence interval" } },"portfolio modified Sharp ratio"),
	PORTFOLIO_STARR_RATIO(new String[][] { { "confidenceInterval","confidence interval" } },"portfolio Starr ratio"),
	PORTFOLIO_TREYNOR_RATIO("portfolio Treynor ratio"),


	PORTFOLIO_GAIN_VARIANCE("portfolio gain variance"),
	PORTFOLIO_LOSS_VARIANCE("porfolio loss variance"),

	POSITION_GAIN_VARIANCE(new String[][] {  { "position", "symbol" } },"position gain variance"),
	POSITION_LOSS_VARIANCE(new String[][] {  { "position", "symbol" } },"position loss variance"),

	PORTFOLIO_DOWNSIDE_VARIANCE(new String[][] { { "thresholdReturn", "threshold return" } },"portfolio downside variance"), 
	PORTFOLIO_UPSIDE_VARIANCE(new String[][] { { "thresholdReturn", "threshold return" } },"portfolio upside variance"),

	POSITION_DOWNSIDE_VARIANCE(new String[][] { { "thresholdReturn", "threshold return" }, { "position", "position" } },"position downside variance"),
	POSITION_UPSIDE_VARIANCE(new String[][] { { "thresholdReturn", "threshold return" }, { "position", "position" } },"position upside variance"),



	PORTFOLIO_EXPECTED_UPSIDE_THRESHOLD_RETURN(new String[][] { { "thresholdReturn", "threshold return" } },"portfolio expected upside return"),
	PORTFOLIO_EXPECTED_DOWNSIDE_THRESHOLD_RETURN(new String[][] { { "thresholdReturn", "threshold return" } },"portfolio expected downsid return"),

	POSITION_EXPECTED_UPSIDE_THRESHOLD_RETURN(new String[][] { { "thresholdReturn", "threshold return" }, { "position", "symbol" } },"position expected upside return"),
	POSITION_EXPECTED_DOWNSIDE_THRESHOLD_RETURN(new String[][] { { "thresholdReturn", "threshold return" }, { "position", "symbol" } },"position expected downside return"),

	POSITION_GAIN_LOSS_VARIANCE_RATIO(new String[][] { { "position", "symbol" } },"position gain/loss variance ratio"), 
	POSITION_UPSIDE_DOWNSIDE_VARIANCE_RATIO(new String[][] { { "thresholdReturn", "threshold return" }, { "position", "symbol" } },"position upside/downside variance ratio"),


	POSITION_RACHEV_RATIO(new String[][] { { "confidenceIntervalAlpha", "confidence interval alpha" }, { "confidenceIntervalBeta", "confidence interval beta" }, { "position", "symbol" } },"position Rachev ratio"),
	POSITION_VAR(new String[][] { { "confidenceInterval", "confidence interval" }, { "position", "symbol" } },"position VaR"),
	POSITION_CVAR(new String[][] { { "confidenceInterval", "confidence interval" }, { "position", "symbol" } },"position CVaR"),			     

	POSITION_SORTINO_RATIO(new String[][] { { "thresholdReturn", "threshold return" }, { "position", "symbol" } },"position Sortion ratio"),
	POSITION_OMEGA_RATIO(new String[][] { { "thresholdReturn", "threshold return" }, { "position", "symbol" } },"position omega ratio"),
	POSITION_INFORMATION_RATIO(new String[][] { { "position", "symbol" } },"position information ratio"), 
	POSITION_SHARPE_RATIO(new String[][] { { "position", "symbol" } },"position Sharpe ratio"),
	POSITION_SHARPE_RATIO_MOD(new String[][] { { "confidenceInterval", "confidence interval" }, { "position", "symbol" } },"position modified Sharp ratio"),
	POSITION_STARR_RATIO(new String[][] { { "confidenceInterval", "confidence interval" }, { "position", "symbol" } },"position Starr ratio"), 
	POSITION_TREYNOR_RATIO(new String[][] { { "position", "symbol" } },"position Tryenor ratio"),


	POSITION_CALMAR_RATIO(new String[][] { { "position", "symbol" } },"position Calmar ratio"),



	POSITION_PROFIT(new String[][] { { "position", "symbol" } },"position profit"),
	POSITION_TRANSACTION_COSTS_SIZE(new String[][] { { "position", "symbol" } },"position transaction cost size"),
	POSITION_COVARIANCE(new String[][] { { "positionA", "first symbol" }, { "positionB", "second symbol" } },"position covariance"),
	POSITION_CORRELATION(new String[][] { { "positionA", "first symbol" }, { "positionB", "second symbol" } },"position correlation"), 

	ONLY_TIME("time"),


	INDEX_PRICE("index price"),
	INDEX_RETURN("index return"),
	INDEX_EXPECTED_RETURN("index expected return"),
	INDEX_VARIANCE("index variance"),
	INDEX_SKEWNESS("index skewness"),
	INDEX_KURTOSIS("index kurtosis"),

	INDEX_MOMENT3("index third moment"),
	INDEX_MOMENT4("index fourth moment"),
	INDEX_CUMULANT3("index third cumulant"),
	INDEX_CUMULANT4("index fourth cumulant"),

	INDEX_MOMENT1("index first moment"),
	INDEX_MOMENT2("index second moment"),
	INDEX_CUMULANT1("index first cumulant"),
	INDEX_CUMULANT2("index second cumulant"),



	POSITION_DOWN_CAPTURE_RATIO(new String[][] { { "position", "symbol" } },"position down capture ratio"),
	POSITION_UP_CAPTURE_RATIO(new String[][] { { "position", "symbol" } },"position up capture ratio"),

	POSITION_DOWN_PERCENTAGE_RATIO(new String[][] { { "position", "symbol" } },"position down percentage ratio"),
	POSITION_UP_PERCENTAGE_RATIO(new String[][] { { "position", "symbol" } },"position up percentage ratio"),
	POSITION_DOWN_NUMBER_RATIO(new String[][] { { "position", "symbol" } },"position down number ratio"),
	POSITION_UP_NUMBER_RATIO(new String[][] { { "position", "symbol" } },"position up number ratio"),


	POSITION_MAX_DRAWDOWN(new String[][] { { "position", "symbol" } },"position maximum drawdown"),
	POSITION_DRAWDOWN(new String[][] { { "position", "symbol" } },"position drawdown"),
	POSITION_RETURN(new String[][] { { "position", "symbol" } },"position return"),
	POSITION_PRICE(new String[][] { { "position", "symbol" } },"position price"),
	POSITION_QUANTITY(new String[][] { { "position", "symbol" } },"position quantity"),	
	POSITION_VALUE(new String[][] { { "position", "symbol" } },"position value"),
	POSITION_EXPECTED_RETURN(new String[][] { { "position", "symbol" } },"position expected return"),
	

	POSITION_VARIANCE(new String[][] { { "position", "symbol" } },"position variance"),
	POSITION_SKEWNESS(new String[][] { { "position", "symbol" } },"position skewness"),
	POSITION_KURTOSIS(new String[][] { { "position", "symbol" } },"position kurtosis"),

	POSITION_MOMENT3(new String[][] { { "position", "symbol" } }, "position third moment"),
	POSITION_MOMENT4(new String[][] { { "position", "symbol" } },"position fourth"),
	POSITION_CUMULANT3(new String[][] { { "position", "symbol" } },"position third cumulant"),
	POSITION_CUMULANT4(new String[][] { { "position", "symbol" } }, "position fourth cumulant"),

	POSITION_MOMENT1(new String[][] { { "position", "symbol" } }, "position first moment"),
	POSITION_MOMENT2(new String[][] { { "position", "symbol" } }, "position second moment"),
	POSITION_CUMULANT1(new String[][] { { "position", "symbol" } },"position first cumulant"),
	POSITION_CUMULANT2(new String[][] { { "position", "symbol" } },"position second cumulant"),

	POSITION_ALPHA(new String[][] { { "position", "symbol" } },"position alpha"), 
	POSITION_ALPHA_JENSEN(new String[][] { { "position", "symbol" } },"position Jensen's alpha"),
	POSITION_BETA(new String[][] { { "position", "symbol" } },"position beta"),
	
	FORECAST_EWMA(new String[][] {{ "valueType", "value type" }, { "rollingWindow", "rolling window" }}, "forecast EWMA"),
	
	FORECAST_HAR(new String[][] {{ "valueType", "value type" }, { "rollingWindow", "rolling window" }, { "windowLengthA", "first window length" },
			{ "windowLengthB", "second window length" },{ "windowLengthC", "third window length" }, { "forecastStep", "forecast step" } }, "forecast HAR-log"),
 
	SEASONALITY(new String[][] {{ "seasonInterval", "season interval" }}, "Seson"),
	PORTFOLIO_LINEAR_REGRESSION(new String[][] {{ "valueType", "value type" }, { "rollingWindow", "rolling window" }, { "forecastStep", "forecast step" } }, " linear model forecast");
	  

	private String[][] metricArguments;
		
	private String description;

	private MetricsTypeFull(String descrition) {
		this(new String[0][0],descrition);
	}

	
	private MetricsTypeFull(String[][] metricArguments, String description) {
		this.metricArguments = new String[metricArguments.length][];
		for (int i = 0; i < metricArguments.length; i++)
			this.metricArguments[i] = metricArguments[i];
		this.description =description;
	}

	public String[][] getMetricArguments() {
		return metricArguments;
	}


	
	public String getDescription() {
		return description;
	}



}
