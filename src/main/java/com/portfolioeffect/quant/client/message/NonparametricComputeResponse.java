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

public class NonparametricComputeResponse extends AbstractMessage {

	private static final long serialVersionUID = 7607257030020984850L;
	private final double[] data;
	
	public NonparametricComputeResponse(String msgType, String msgBody, double[] data) {
		super(msgType, msgBody);
		this.data = data;
	}

	public double[] getData() {
		return data;
	}

}
