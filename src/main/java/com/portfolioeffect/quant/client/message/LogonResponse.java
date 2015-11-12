/*
 * #%L
 * ICE-9 - Algo Client API
 * %%
 * Copyright (C) 2010 - 2015 Snowfall Systems, Inc.
 * %%
 * This file is part of PortfolioEffect Quant Client.
 * 
 * PortfolioEffect Quant Client is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * PortfolioEffect Quant Client is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with PortfolioEffect Quant Client. If not, see <http://www.gnu.org/licenses/>.
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
