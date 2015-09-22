/*
 * #%L
 * ICE-9 - Algo Client API
 * %%
 * Copyright (C) 2010 - 2015 Snowfall Systems, Inc.
 * %%
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 * #L%
 */
package com.portfolioeffect.quant.client.message;

import java.io.Serializable;

public abstract class FastMessage implements Serializable{

	private static final long serialVersionUID = 4782149744538729261L;

	protected final StandardHeader messageHeader;

	public FastMessage(StandardHeader messageHeader) {
		this.messageHeader = messageHeader;
	}

	public StandardHeader getMessageHeader() {
		return messageHeader;
	}
		
}
