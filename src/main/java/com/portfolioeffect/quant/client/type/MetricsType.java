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


public enum MetricsType {


	PORTFOLIO_OPTIMIZATION(),

	PORTFOLIO_PDF(new String[][] { { "from", "NUMBER" }, { "to", "NUMBER" }, { "number", "NUMBER" } }),
	POSITION_PDF(new String[][] {{ "position", "STRING" },  { "from", "NUMBER" }, { "to", "NUMBER" }, { "number", "NUMBER" } }),

	PORTFOLIO_HURST_EXPONENT(),
	POSITION_HURST_EXPONENT(new String[][] {  { "position", "STRING" } }),


	POSITION_MATRIX(new String[][] { { "value", "STRING" }  }),
	POSITION_SORT(new String[][] { { "sort_by", "STRING" }, { "sort_direction", "STRING" } }),

	PORTFOLIO_FRACTAL_DIMENSION(),
	POSITION_FRACTAL_DIMENSION(new String[][] {  { "position", "STRING" } }),


	PORTFOLIO_RETURN_AUTOCOVARIANCE(new String[][] {  { "lag", "NUMBER" } }),
	POSITION_RETURN_AUTOCOVARIANCE(new String[][] {  { "position", "STRING" }, { "lag", "NUMBER" } }),
	INDEX_RETURN_AUTOCOVARIANCE(new String[][] {  { "lag", "NUMBER" } }),


	PORTFOLIO_SORTINO_RATIO(new String[][] { { "thresholdReturn", "NUMBER" } }),
	PORTFOLIO_GAIN_LOSS_VARIANCE_RATIO(),
	PORTFOLIO_UPSIDE_DOWNSIDE_VARIANCE_RATIO(new String[][] { { "thresholdReturn", "NUMBER" } }),
	PORTFOLIO_OMEGA_RATIO(new String[][] { { "thresholdReturn", "NUMBER" } }),
	PORTFOLIO_RACHEV_RATIO(new String[][] { { "confidenceIntervalAlpha", "NUMBER" } , { "confidenceIntervalBeta", "NUMBER" } }),
	PORTFOLIO_VAR(new String[][] { { "confidenceInterval", "NUMBER" } }),
	PORTFOLIO_CVAR(new String[][] { { "confidenceInterval", "NUMBER" } }),
	PORTFOLIO_MAX_DRAWDOWN(),
	PORTFOLIO_DRAWDOWN(),

	PORTFOLIO_DOWN_CAPTURE_RATIO(),
	PORTFOLIO_UP_CAPTURE_RATIO(),


	PORTFOLIO_DOWN_NUMBER_RATIO(),
	PORTFOLIO_UP_NUMBER_RATIO(),
	PORTFOLIO_DOWN_PERCENTAGE_RATIO(),
	PORTFOLIO_UP_PERCENTAGE_RATIO(),


	PORTFOLIO_CALMAR_RATIO(),
	PORTFOLIO_VALUE(),
	PORTFOLIO_RETURN(),
	PORTFOLIO_EXPECTED_RETURN(),
	PORTFOLIO_RETURN_DAILY(),

	PORTFOLIO_PROFIT(),
	PORTFOLIO_TRANSACTION_COSTS_SIZE(),
	PORTFOLIO_BETA(),
	PORTFOLIO_ALPHA_JENSEN(),
	PORTFOLIO_ALPHA(),
	PORTFOLIO_VARIANCE(),
	PORTFOLIO_SKEWNESS(),
	PORTFOLIO_KURTOSIS(),

	PORTFOLIO_MOMENT3(),
	PORTFOLIO_MOMENT4(),
	PORTFOLIO_CUMULANT3(),
	PORTFOLIO_CUMULANT4(),


	PORTFOLIO_MOMENT1(),
	PORTFOLIO_MOMENT2(),
	PORTFOLIO_CUMULANT1(),
	PORTFOLIO_CUMULANT2(),


	PORTFOLIO_SHARPE_RATIO(),
	PORTFOLIO_INFORMATION_RATIO(),

	USER_DATA(new String[][] { { "position", "STRING" } }),

	POSITION_WEIGHT(new String[][] { { "position", "STRING" } }),
	POSITION_COMPONENT_CVAR(new String[][] { { "confidenceInterval", "NUMBER" }, { "position", "STRING" } } ),//????????????
	POSITION_COMPONENT_VAR(new String[][] { { "confidenceInterval", "NUMBER" }, { "position", "STRING" } }  ),//??????????????		

	PORTFOLIO_SHARPE_RATIO_MOD(new String[][] { { "confidenceInterval","NUMBER" } }),
	PORTFOLIO_STARR_RATIO(new String[][] { { "confidenceInterval","NUMBER" } }),
	PORTFOLIO_TREYNOR_RATIO(),


	PORTFOLIO_GAIN_VARIANCE(),
	PORTFOLIO_LOSS_VARIANCE(),

	POSITION_GAIN_VARIANCE(new String[][] {  { "position", "STRING" } }),
	POSITION_LOSS_VARIANCE(new String[][] {  { "position", "STRING" } }),

	PORTFOLIO_DOWNSIDE_VARIANCE(new String[][] { { "thresholdReturn", "NUMBER" } }), 
	PORTFOLIO_UPSIDE_VARIANCE(new String[][] { { "thresholdReturn", "NUMBER" } }),

	POSITION_DOWNSIDE_VARIANCE(new String[][] { { "thresholdReturn", "NUMBER" }, { "position", "STRING" } }),
	POSITION_UPSIDE_VARIANCE(new String[][] { { "thresholdReturn", "NUMBER" }, { "position", "STRING" } }),



	PORTFOLIO_EXPECTED_UPSIDE_THRESHOLD_RETURN(new String[][] { { "thresholdReturn", "NUMBER" } }),
	PORTFOLIO_EXPECTED_DOWNSIDE_THRESHOLD_RETURN(new String[][] { { "thresholdReturn", "NUMBER" } }),

	POSITION_EXPECTED_UPSIDE_THRESHOLD_RETURN(new String[][] { { "thresholdReturn", "NUMBER" }, { "position", "STRING" } }),
	POSITION_EXPECTED_DOWNSIDE_THRESHOLD_RETURN(new String[][] { { "thresholdReturn", "NUMBER" }, { "position", "STRING" } }),

	POSITION_GAIN_LOSS_VARIANCE_RATIO(new String[][] { { "position", "STRING" } }), 
	POSITION_UPSIDE_DOWNSIDE_VARIANCE_RATIO(new String[][] { { "thresholdReturn", "NUMBER" }, { "position", "STRING" } }),


	POSITION_RACHEV_RATIO(new String[][] { { "confidenceIntervalAlpha", "NUMBER" }, { "confidenceIntervalBeta", "NUMBER" }, { "position", "STRING" } }),
	POSITION_VAR(new String[][] { { "confidenceInterval", "NUMBER" }, { "position", "STRING" } }),
	POSITION_CVAR(new String[][] { { "confidenceInterval", "NUMBER" }, { "position", "STRING" } }),			     

	POSITION_SORTINO_RATIO(new String[][] { { "thresholdReturn", "NUMBER" }, { "position", "STRING" } }),
	POSITION_OMEGA_RATIO(new String[][] { { "thresholdReturn", "NUMBER" }, { "position", "STRING" } }),
	POSITION_INFORMATION_RATIO(new String[][] { { "position", "STRING" } }), 
	POSITION_SHARPE_RATIO(new String[][] { { "position", "STRING" } }),
	POSITION_SHARPE_RATIO_MOD(new String[][] { { "confidenceInterval", "NUMBER" }, { "position", "STRING" } }),
	POSITION_STARR_RATIO(new String[][] { { "confidenceInterval", "NUMBER" }, { "position", "STRING" } }), 
	POSITION_TREYNOR_RATIO(new String[][] { { "position", "STRING" } }),


	POSITION_CALMAR_RATIO(new String[][] { { "position", "STRING" } }),



	POSITION_PROFIT(new String[][] { { "position", "STRING" } }),
	POSITION_TRANSACTION_COSTS_SIZE(new String[][] { { "position", "STRING" } }),
	POSITION_COVARIANCE(new String[][] { { "positionA", "STRING" }, { "positionB", "STRING" } }),
	POSITION_CORRELATION(new String[][] { { "positionA", "STRING" }, { "positionB", "STRING" } }), 

	ONLY_TIME(),


	INDEX_PRICE(),
	INDEX_RETURN(),
	INDEX_EXPECTED_RETURN(),
	INDEX_RETURN_DAILY(),//Depricated
	INDEX_VARIANCE(),
	INDEX_SKEWNESS(),
	INDEX_KURTOSIS(),

	INDEX_MOMENT3(),
	INDEX_MOMENT4(),
	INDEX_CUMULANT3(),
	INDEX_CUMULANT4(),

	INDEX_MOMENT1(),
	INDEX_MOMENT2(),
	INDEX_CUMULANT1(),
	INDEX_CUMULANT2(),



	POSITION_DOWN_CAPTURE_RATIO(new String[][] { { "position", "STRING" } }),
	POSITION_UP_CAPTURE_RATIO(new String[][] { { "position", "STRING" } }),

	POSITION_DOWN_PERCENTAGE_RATIO(new String[][] { { "position", "STRING" } }),
	POSITION_UP_PERCENTAGE_RATIO(new String[][] { { "position", "STRING" } }),
	POSITION_DOWN_NUMBER_RATIO(new String[][] { { "position", "STRING" } }),
	POSITION_UP_NUMBER_RATIO(new String[][] { { "position", "STRING" } }),


	POSITION_MAX_DRAWDOWN(new String[][] { { "position", "STRING" } }),
	POSITION_DRAWDOWN(new String[][] { { "position", "STRING" } }),
	POSITION_RETURN(new String[][] { { "position", "STRING" } }),
	POSITION_PRICE(new String[][] { { "position", "STRING" } }),
	POSITION_QUANTITY(new String[][] { { "position", "STRING" } }),	
	POSITION_VALUE(new String[][] { { "position", "STRING" } }),
	POSITION_EXPECTED_RETURN(new String[][] { { "position", "STRING" } }),
	POSITION_RETURN_DAILY(new String[][] { { "position", "STRING" } }),//Deprecated

	POSITION_VARIANCE(new String[][] { { "position", "STRING" } }),
	POSITION_SKEWNESS(new String[][] { { "position", "STRING" } }),
	POSITION_KURTOSIS(new String[][] { { "position", "STRING" } }),

	POSITION_MOMENT3(new String[][] { { "position", "STRING" } }),
	POSITION_MOMENT4(new String[][] { { "position", "STRING" } }),
	POSITION_CUMULANT3(new String[][] { { "position", "STRING" } }),
	POSITION_CUMULANT4(new String[][] { { "position", "STRING" } }),

	POSITION_MOMENT1(new String[][] { { "position", "STRING" } }),
	POSITION_MOMENT2(new String[][] { { "position", "STRING" } }),
	POSITION_CUMULANT1(new String[][] { { "position", "STRING" } }),
	POSITION_CUMULANT2(new String[][] { { "position", "STRING" } }),

	POSITION_ALPHA(new String[][] { { "position", "STRING" } }), 
	POSITION_ALPHA_JENSEN(new String[][] { { "position", "STRING" } }),
	POSITION_BETA(new String[][] { { "position", "STRING" } });



	private String[][] metricArguments;

	private MetricsType() {
		this(new String[0][0]);
	}

	private MetricsType(String[][] metricArguments ) {
		this.metricArguments = new String[metricArguments.length][];
		for (int i = 0; i < metricArguments.length; i++)
			this.metricArguments[i] = metricArguments[i];
	}

	public String[][] getMetricArguments() {
		return metricArguments;
	}





}
