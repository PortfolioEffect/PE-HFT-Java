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
