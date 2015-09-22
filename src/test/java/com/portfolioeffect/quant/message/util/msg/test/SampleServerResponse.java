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

public class SampleServerResponse {

	private final String responseType;
	public String getResponseType() {
		return responseType;
	}

	public String getResponse() {
		return response;
	}

	public double[][] getData() {
		return data;
	}

	private final String response;

	private final double[][] data;
	
	public SampleServerResponse(String responseType) {
		this(responseType, null);

	}

	public SampleServerResponse(String responseType, String response) {
		this(responseType, response, null);

	}

	public SampleServerResponse(String responseType, String response,
			double[][] data) {

		this.responseType = responseType;
		this.response = response;
		this.data = data;
		
	}

	
}
