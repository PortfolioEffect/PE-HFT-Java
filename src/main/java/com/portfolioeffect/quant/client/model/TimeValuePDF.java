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

public class TimeValuePDF {
	
	private final double[][] pdf;
	private final double[][] x;
	private final long[] time;
	
	public TimeValuePDF(double[][] pdf, double[][] x, long[] time){
		this.pdf = pdf;
		this.x = x;
		this.time = time;
	}
	
	public double[][] getPDF() {
		return pdf;
	}

	public double[][] getX() {
		return x;
	}
	
	public long[] getTime(){
		return time;
	}

	

}
