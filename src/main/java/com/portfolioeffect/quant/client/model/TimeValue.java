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
package com.portfolioeffect.quant.client.model;

import com.portfolioeffect.quant.client.result.Metric;

public class TimeValue {
	
	private final double[] value;
	private final long[] time;
	
	public TimeValue(Metric result) throws ComputeErrorException{
		this(result.getDoubleArray("value"), result.getLongArray("time"));
	}
	
	public TimeValue(Metric result, Metric resultT) throws ComputeErrorException{
		this(result.getDoubleArray("value"), resultT.getDoubleAsLongArray("value"));
	}
	
	public TimeValue(double[] value, long[] time){
		this.value = value;
		this.time = time;
	}
	
	public double[] getValue() {
		return value;
	}

	public long[] getTime() {
		return time;
	}

	
	public double getValueLast() {
		return value[value.length-1];
	}

	public long getTimeLast() {
		return time[time.length -1];
	}


}
