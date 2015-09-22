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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.portfolioeffect.quant.client.message.type.SessionStatusType;

public class LogoutResponse extends FastMessage{
	
	private static final long serialVersionUID = -741945219227326915L;
	private final SessionStatusType sessionStatusType; 	// field "SessionStatus"
	private String text; 						// field "Text"
	
	public LogoutResponse(StandardHeader messageHeader, SessionStatusType sessionStatusType) {
		super(messageHeader);
		this.sessionStatusType = sessionStatusType;
	}
	
	public SessionStatusType getSessionStatusType() {
		return sessionStatusType;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
}
