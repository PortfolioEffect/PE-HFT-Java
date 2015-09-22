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

import org.openfast.Message;

public class ClientMessage {

	private Message message;
	private boolean isRejected;
	private boolean isEmpty;
	
	public ClientMessage(Message message) {
		this(message, false, false);
	}
	
	public ClientMessage(Message message, boolean isRejected, boolean isEmpty) {
		this.message = message;
		this.isRejected = isRejected;
		this.isEmpty = isEmpty;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public boolean isRejected() {
		return isRejected;
	}

	public void setRejected(boolean isRejected) {
		this.isRejected = isRejected;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}
	
	
	
}
