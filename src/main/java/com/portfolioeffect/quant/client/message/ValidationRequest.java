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

public class ValidationRequest extends AbstractMessage {

	private static final long serialVersionUID = 5182547119624094141L;
	
	public ValidationRequest(String request) {
		super(null, request);
	}

}
