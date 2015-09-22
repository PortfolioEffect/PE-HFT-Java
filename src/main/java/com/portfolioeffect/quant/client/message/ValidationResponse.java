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

public class ValidationResponse extends AbstractMessage{

	private static final long serialVersionUID = -732485482787861869L;

	public ValidationResponse(String msgType, String msgBody) {
		super(msgType, msgBody);
	}


}
