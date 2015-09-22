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
package com.portfolioeffect.quant.message.util.msg.test;

public class SampleClientRequestLongTime {

	private final String requestType;
	private final String request;

	private final int[] price;
	private final long[] time;
	private final float[] result;
	

	public SampleClientRequestLongTime(String requestType, String request, int[] price, long time[], float[] result) {
		this.requestType = requestType;
		this.request = request;
		this.price = price;
		this.time = time;
		this.result = result;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getRequest() {
		return request;
	}

	public int[] getPrice() {
		return price;
	}

	public long[] getTime() {
		return time;
	}

	public float[] getResult() {
		return result;
	}

}
