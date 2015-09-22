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

import java.io.Serializable;

public class AbstractMessage  implements Serializable{

	private static final long serialVersionUID = 887789912910828492L;
	
	private final String msgType;
	private final String msgBody;
	
	public AbstractMessage(String msgType, String msgBody) {
		this.msgType = msgType;
		this.msgBody = msgBody;
	}

	public String getMsgType() {
		return msgType;
	}
	
	public String getMsgBody() {
		return msgBody;
	}

	
}
