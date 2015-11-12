/*
 * #%L
 * ICE-9 HF Analytics Platform
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
package com.portfolioeffect.quant.client.util;

import java.math.BigDecimal;

public class ProgressBar {

	private double cumulPercent;
	private double lastPercent;
	private int oldPercent=-1;
	private long  startTime;
	private boolean isON = true;
	
	
	private double scale = 1 ;
	

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public ProgressBar() {
		
	}

	public ProgressBar(double scale) {
		this.scale = scale;		
	}

	public void printCompletionStatus(int count, int maxCount) {
		double a=count;
		a= a/maxCount;
		printCompletionStatus(a);
		
	}
	
	public void printCompletionStatus(double percent) {
		
		
		
		if(percent < lastPercent  && lastPercent>=1.0 )
			lastPercent =0;
		double delta = percent - lastPercent;
		cumulPercent +=delta; 
		lastPercent = percent;
		
		int percentInt = (int) Math.round(cumulPercent/scale*100);
		
		
		
		percentInt = percentInt < oldPercent ? oldPercent : percentInt;
		int curentPercent= oldPercent;
		
		
		
		
		while(curentPercent<= percentInt ){
		if (curentPercent == 0 && oldPercent != 0) {
			oldPercent = curentPercent;
			if(isON && percentInt==0)
				Console.write("[" + percentInt + "%");
			startTime = System.currentTimeMillis();
			//return;
			break;
		}
		if (curentPercent == 100 && oldPercent != 100) {

			
			BigDecimal sec = new BigDecimal(String.valueOf((System.currentTimeMillis() - startTime)*1e-3)).setScale(2, BigDecimal.ROUND_HALF_UP);
			oldPercent = curentPercent;
			if(isON)
				Console.writeln("100%] ( " + sec  +" sec )");
			
			break;
		}
		if ((curentPercent > oldPercent) && (curentPercent % 2 > 0)) {
			if(isON)
				Console.write(".");
		} else if ((curentPercent > oldPercent) && curentPercent % 10 == 0) {
			if(isON)
				Console.write(curentPercent + "%");
		}
			oldPercent = curentPercent;
			curentPercent++;
		}
		oldPercent = percentInt;
		if(cumulPercent>=scale)
			reset();
	}

	public void reset() {
		cumulPercent =0;
		lastPercent =0;
		oldPercent =-1;
		scale=1;
	}
	
	public boolean isON() {
		return isON;
	}

	public void setON(boolean isON) {
		this.isON = isON;
	}

}
