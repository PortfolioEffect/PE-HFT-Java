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
import com.portfolioeffect.quant.client.result.LazyMetric;
/**
 * Class that incorporates the notion of "lazy" portfolio or position metric
 * @author oleg
 *
 */
public class Metric {
	
	private LazyMetric metric;
	

	public Metric(LazyMetric metric) {
		this.metric = metric;
	}
	
	
	public boolean hasError(){
		return metric.hasError();
	}
	
	public String getError(){
		return metric.getErrorMessage();
	}
	
	public double[] getValue() throws ComputeErrorException{
		checkError();
		return metric.getValue();
	}
	
	public long[] getTime() throws ComputeErrorException{
		checkError();
		return metric.getTime();
	}
	
	public long getLastTime() throws ComputeErrorException{
		checkError();
		return metric.getLastTime();
	}
	
	public double getLastValue() throws ComputeErrorException{
		checkError();
		return metric.getLastValue();
	}
	
	public Portfolio getPortfolio() throws ComputeErrorException{
		checkError();
		return new Portfolio(metric.getPortfolio("portfolio"));
	}
	
	public LazyMetric getMetric() {
		return metric;
	}
	
	public void checkError() throws ComputeErrorException{
		if(hasError())
			throw new ComputeErrorException(getError());
	}

}
