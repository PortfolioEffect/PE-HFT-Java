/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
 * %%
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 * #L%
 */
package com.portfolioeffect.quant.client.util;

public class StopWatch {
	
	
	private long t;
	private long tLast;
	
	
	public void reset(){
		t=0;
	}
	
	public void start(){
		
			tLast = System.currentTimeMillis();		
			
	}
	
	public void stop(){
		t+= System.currentTimeMillis() - tLast;
		tLast =0;
	}
	
	public double getTimeSec(){
		return 1e-3*t;
	}

}
