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
package com.portfolioeffect.quant.client.result;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TLongArrayList;

import java.io.IOException;

import com.portfolioeffect.quant.client.model.ComputeErrorException;

public class SavedMetric extends AbstractMethodResult{

	private String description=""; 
	public SavedMetric(double[] value, long[] time) throws IOException {
		setValue(value);
		setTime(time);
	}
	
	
	public SavedMetric(TDoubleArrayList value, TLongArrayList time) throws IOException {
		setValue(value.toArray());
		setTime(time.toArray());
	}

	@Override
	protected void computeMetric() throws ComputeErrorException {
		
		
	}

	@Override
	public void compute() {
		
		
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	public String getDescription(){
		return description;
	}

}
