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

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.hash.TLongHashSet;

import java.util.ArrayList;

import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.result.AbstractMethodResult;

public class MetricSynchronizer {

	ArrayList<AbstractMethodResult> metricList;
	boolean isSynchronized;
	TDoubleArrayList[] values;
	TLongArrayList time;
 	
	public MetricSynchronizer() {
		metricList = new ArrayList<AbstractMethodResult>();
		isSynchronized = false;
	}
	
	
	public void add(AbstractMethodResult metric){
		metricList.add(metric);
		isSynchronized = false;
	}
	
	
	public double[] getValue(int i) throws ComputeErrorException{
	
		return getValueList(i).toArray();
	}
	
	public TDoubleArrayList getValueList(int i) throws ComputeErrorException{
		synchronize();
		
		return values[i];
				
	}
	
	public long[] getTime() throws ComputeErrorException{
		
		return getTimeList().toArray();
		
	}
	
	public TLongArrayList getTimeList() throws ComputeErrorException{
		synchronize();
		
		return time;
	}
	
	
	private void synchronize() throws ComputeErrorException{
		if(isSynchronized)
			return;
		
		TLongHashSet set = new TLongHashSet();
		for(AbstractMethodResult e: metricList){
			set.addAll(e.getTime());
		}
		
		time = new TLongArrayList(set);
		
		time.sort();
		
		
		values = new TDoubleArrayList[metricList.size()];
		
			
		
		int i=0;
		for(AbstractMethodResult e: metricList){
			
			values[i] = new TDoubleArrayList();
			
			long[] eTime = e.getTime();
			double[] eValue = e.getValue();
			for(int j=0,  k=0; j<time.size() ; j++){
								
				if( k< eTime.length &&  time.getQuick(j) == eTime[k]){
					values[i].add(eValue[k]);
					k++;
					
				}else
					
					values[i].add(Double.NaN);					
				
			}
			
			i++;
		}
		
		isSynchronized = true;
		
	}
	
	 

}
