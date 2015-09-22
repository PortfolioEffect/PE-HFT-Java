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

public class PortfolioComputeRequest extends AbstractMessage{

	private static final long serialVersionUID = 648756079532012293L;
	private final String params;
	
	public PortfolioComputeRequest(String msgType, String msgBody, String params) {
		super(msgType, msgBody);
		this.params = params;
	}
	
	public String getParams() {
		return params;
	}
}
