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

import com.portfolioeffect.quant.client.message.type.EncryptMethodType;
import com.portfolioeffect.quant.client.message.type.SessionStatusType;


public class LogonResponse extends FastMessage{

	private static final long serialVersionUID = -3894267039689187741L;
	private final SessionStatusType sessionStatusType; // field "SessionStatus"
	private final int heartbeatIntervalSec; // field "HeartBtInt"
	private final EncryptMethodType encryptMethodType;
	
	public LogonResponse(StandardHeader messageHeader, EncryptMethodType encryptMethodType, 
			SessionStatusType sessionStatusType, int heartbeatIntervalSec) {
		super(messageHeader);
		this.sessionStatusType = sessionStatusType;
		this.heartbeatIntervalSec = heartbeatIntervalSec;
		this.encryptMethodType = encryptMethodType;
	}
	
	public SessionStatusType getSessionStatusType() {
		return sessionStatusType;
	}
	
	public int getHeartbeatIntervalSec() {
		return heartbeatIntervalSec;
	}

	public EncryptMethodType getEncryptMethodType() {
		return encryptMethodType;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
