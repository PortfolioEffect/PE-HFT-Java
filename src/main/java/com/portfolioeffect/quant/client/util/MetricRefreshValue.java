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
package com.portfolioeffect.quant.client.util;

public class MetricRefreshValue {
	
	private final long time;
	
	private final double value;
	private final String metricKey;
	private final int arrayIndex;

	
	public MetricRefreshValue(String index, int arrayIndex, double value, long time) {
		this.metricKey = index;
		this.arrayIndex=arrayIndex;
		this.value = value;
		this.time = time;
	}
	
	
	
	public long getTime() {
		return time;
	}

	public double getValue() {
		return value;
	}

	public String getMetricKey() {
		return metricKey;
	}
	
	public int getArrayIndex() {
		return arrayIndex;
	}



}
