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


	

	PDF,
	HURST_EXPONENT,
	FRACTAL,
	RETURN_AUTOCOVARIANCE,
	SORTINO_RATIO,
	GAIN_LOSS_VARIANCE_RATIO,
	UPSIDE_DOWNSIDE_VARIANCE_RATIO,
	OMEGA_RATIO,
	RACHEV_RATIO,
	VAR,
	CVAR,
	MAX_DRAWDOWN,
	DRAWDOWN,

	DOWN_CAPTURE_RATIO,
	UP_CAPTURE_RATIO,

	DOWN_NUMBER_RATIO,
	UP_NUMBER_RATIO,
	DOWN_PERCENTAGE_RATIO,
	UP_PERCENTAGE_RATIO,

	CALMAR_RATIO,
	VALUE,
	RETURN,
	EXPECTED,
	RETURN_DAILY,

	PROFIT,
	TRANSACTION_COSTS_SIZE,
	BETA,
	ALPHA_JENSEN,
	ALPHA,
	VARIANCE,
	SKEWNESS,
	KURTOSIS,

	MOMENT3,
	MOMENT4,
	CUMULANT3,
	CUMULANT4,

	MOMENT1,
	MOMENT2,
	CUMULANT1,
	CUMULANT2,

	SHARPE_RATIO,
	INFORMATION_RATIO,

	USER_DATA,

	WEIGHT,
	
	SHARPE_RATIO_MOD,
	STARR_RATIO,
	TREYNOR_RATIO,

	GAIN_VARIANCE,
	LOSS_VARIANCE,
	
	DOWNSIDE_VARIANCE, 
	UPSIDE_VARIANCE,

	EXPECTED_UPSIDE_THRESHOLD_RETURN,
	EXPECTED_DOWNSIDE_THRESHOLD_RETURN,
	ONLY_TIME,
	CONTRAINTS_ONLY,
	EQUIWEIGHT,
	POSITIONS_SUM_ABS_WEIGHT;

}
