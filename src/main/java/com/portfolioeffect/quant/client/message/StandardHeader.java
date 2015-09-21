/*
 * #%L
 * ICE-9 - Algo Client API
 * %%
 * Copyright (C) 2010 - 2015 Snowfall Systems, Inc.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * #L%
 */
package com.portfolioeffect.quant.client.message;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.portfolioeffect.quant.client.message.type.FastMessageType;

public class StandardHeader {
 
	private final String applicationVersionId; // field ""ApplVerID"
	private final FastMessageType messageType; // field "MessageType"
	private final int messageSequenceNumber; // field "MsgSeqNum"
	private final long messageSendingTime; // field "SendingTime"
	
	public StandardHeader(String applicationVersionId,
			FastMessageType messageType, int messageSequenceNumber, long messageSendingTime) {
		this.applicationVersionId = applicationVersionId;
		this.messageType = messageType;
		this.messageSequenceNumber = messageSequenceNumber;
		this.messageSendingTime = messageSendingTime;
	}
		
	public String getApplicationVersionId() {
		return applicationVersionId;
	}
	public FastMessageType getMessageType() {
		return messageType;
	}
	
	public int getMessageSequenceNumber() {
		return messageSequenceNumber;
	}
	public long getMessageSendingTime() {
		return messageSendingTime;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
