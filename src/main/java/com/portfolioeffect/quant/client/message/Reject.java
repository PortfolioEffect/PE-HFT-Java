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

import com.portfolioeffect.quant.client.message.type.SessionRejectReasonType;


public class Reject extends FastMessage{
	
	private static final long serialVersionUID = -7641658610904185337L;
	private final int refSeqNum;
	private Integer refTagID, refApplExtID;
	private SessionRejectReasonType sessionRejectReasonType;
	private String refMsgType, refCstmApplVerID, refApplVerID, text;
	
	public Reject(StandardHeader messageHeader, int refSeqNum) {
		super(messageHeader);
		this.refSeqNum = refSeqNum;
	}

	public int getRefSeqNum() {
		return refSeqNum;
	}

	public Integer getRefTagID() {
		return refTagID;
	}

	public Integer getRefApplExtID() {
		return refApplExtID;
	}

	public SessionRejectReasonType getSessionRejectReasonType() {
		return sessionRejectReasonType;
	}

	public String getRefMsgType() {
		return refMsgType;
	}

	public String getRefCstmApplVerID() {
		return refCstmApplVerID;
	}

	public String getRefApplVerID() {
		return refApplVerID;
	}

	public String getText() {
		return text;
	}

	public void setRefTagID(Integer refTagID) {
		this.refTagID = refTagID;
	}

	public void setRefApplExtID(Integer refApplExtID) {
		this.refApplExtID = refApplExtID;
	}

	public void setSessionRejectReasonType(SessionRejectReasonType sessionRejectReasonType) {
		this.sessionRejectReasonType = sessionRejectReasonType;
	}

	public void setRefMsgType(String refMsgType) {
		this.refMsgType = refMsgType;
	}

	public void setRefCstmApplVerID(String refCstmApplVerID) {
		this.refCstmApplVerID = refCstmApplVerID;
	}

	public void setRefApplVerID(String refApplVerID) {
		this.refApplVerID = refApplVerID;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
