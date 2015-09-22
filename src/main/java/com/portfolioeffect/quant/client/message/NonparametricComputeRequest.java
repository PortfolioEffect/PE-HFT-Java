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
