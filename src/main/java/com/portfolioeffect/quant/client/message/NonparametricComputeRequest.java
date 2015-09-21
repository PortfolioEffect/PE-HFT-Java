/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * #L%
 */
package com.portfolioeffect.quant.client.message;

public class NonparametricComputeRequest extends AbstractMessage{

	private static final long serialVersionUID = -2444727489474193730L;

	private final double[] price;
	private final int[] time;	

	public NonparametricComputeRequest(String msgType, String msgBody, double[] price, int time[]) {
		super(msgType, msgBody);
		this.price = price;
		this.time = time;
	}

	public double[] getPrice() {
		return price;
	}

	public int[] getTime() {
		return time;
	}
	
	
}
