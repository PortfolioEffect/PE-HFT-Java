/*
 * #%L
 * Ice-9 Platform Java API
 * %%
 * Copyright (C) 2010 - 2014 Snowfall Systems, Inc.
 * %%
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 * #L%
 */
package com.portfolioeffect.quant.client.message.util;

import org.openfast.GroupValue;
import org.openfast.Message;
import org.openfast.SequenceValue;

import com.portfolioeffect.quant.client.message.Heartbeat;
import com.portfolioeffect.quant.client.message.LogonResponse;
import com.portfolioeffect.quant.client.message.LogoutResponse;
import com.portfolioeffect.quant.client.message.NonparametricComputeResponse;
import com.portfolioeffect.quant.client.message.Reject;
import com.portfolioeffect.quant.client.message.StandardHeader;
import com.portfolioeffect.quant.client.message.TestRequest;
import com.portfolioeffect.quant.client.message.TransmitDataRequest;
import com.portfolioeffect.quant.client.message.TransmitDataResponse;
import com.portfolioeffect.quant.client.message.ValidationResponse;
import com.portfolioeffect.quant.client.message.type.EncryptMethodType;
import com.portfolioeffect.quant.client.message.type.FastMessageType;
import com.portfolioeffect.quant.client.message.type.SessionRejectReasonType;
import com.portfolioeffect.quant.client.message.type.SessionStatusType;

public class ServerResponseMessageParser {

	public static StandardHeader parseMessageHeader(Message msg) {

		String applicationVersionId = msg.getString("ApplVerID"); 
		String messageCode = msg.getString("MessageType");
		FastMessageType messageType = FastMessageType.getFastMessageType(messageCode);
		int messageSequenceNumber = msg.getInt("MsgSeqNum"); 
		long messageSendingTime = msg.getLong("SendingTime"); 

		StandardHeader messageHeader = new StandardHeader(applicationVersionId, messageType, 
				messageSequenceNumber, messageSendingTime);
		return messageHeader;
	}

	public static LogonResponse parseLogonResponse(Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);
		int sessionStatusCode = msg.getInt("SessionStatus");
		SessionStatusType sessionStatusType = SessionStatusType.getSessionStatusType(sessionStatusCode);
		int heartbeatIntervalSec = msg.getInt("HeartBtInt");	
		int encryptMethodCode = msg.getInt("EncryptMethod");
		EncryptMethodType encryptMethodType = EncryptMethodType.getEncryptMethodType(encryptMethodCode);
		LogonResponse logonResponse =  new LogonResponse(messageHeader, encryptMethodType, sessionStatusType, heartbeatIntervalSec);
		return logonResponse;
	}


	public static LogoutResponse parseLogoutResponse(Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);
		int sessionStatusCode = msg.getInt("SessionStatus");
		SessionStatusType sessionStatusType = SessionStatusType.getSessionStatusType(sessionStatusCode);
		LogoutResponse logoutResponse =  new LogoutResponse(messageHeader, sessionStatusType);	
		if(msg.isDefined("Text")) {
			String text = msg.getString("Text");	
			logoutResponse.setText(text);
		}

		return logoutResponse;
	}

	public static Heartbeat parseHeartbeat(Message msg){
		StandardHeader messageHeader = parseMessageHeader(msg);
		Heartbeat heartbeat = new Heartbeat(messageHeader);
		if(msg.isDefined("TestReqID")) {
			String testReqID = msg.getString("TestReqID");	
			heartbeat.setTestReqID(testReqID);
		}
		return heartbeat;
	}


	public static TestRequest parseTestRequest(Message msg){
		StandardHeader messageHeader = parseMessageHeader(msg);
		TestRequest testRequest = new TestRequest(messageHeader);
		if(msg.isDefined("TestReqID")) {
			String testReqID = msg.getString("TestReqID");	
			testRequest.setTestReqID(testReqID);
		}
		return testRequest;
	}
	
	public static NonparametricComputeResponse parseNonparametricComputeResponse(Message msg) {

		StandardHeader messageHeader = parseMessageHeader(msg);
		String responseType = msg.getString("ResponseType");
		String response = null;

		if (msg.isDefined("Response"))
			response = msg.getString("Response");

		
		SequenceValue sValue = msg.getSequence("Data");
		int  lenght = sValue.getLength();
		double data[] = new double[lenght];
		int i=0;
		for (GroupValue gv : sValue.getValues()) {
				
				double  curetntData = gv.getDouble("data");		
				data[i] = curetntData;
				i++;
				

			}
		return new NonparametricComputeResponse(responseType, response, data);
	}

	public static ValidationResponse parseValidationResponse(Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);
		String responseType = msg.getString("ResponseType");
		String	response = msg.getString("Response");
		return new ValidationResponse(responseType, response); 
	}
	
	public static TransmitDataResponse parseTransmitDataResponse(Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);
		String responseType = msg.getString("ResponseType");
		String response = null;

		if (msg.isDefined("Response")) {
			response = msg.getString("Response");
		}
		
		
			return new TransmitDataResponse(responseType, response);
	}
	
	public static TransmitDataRequest parseTransmitDataRequest(Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);
				
		String requestType = msg.getString("RequestType");
		String request = null;
		if (msg.isDefined("Request"))
			request = msg.getString("Request");

		int originalLength = msg.getInt("OriginalLength");
		
		byte[] dataInt=null;
		byte[] dataFloat=null;
		
		if(msg.isDefined("DataInt")){
			dataInt = msg.getBytes("DataInt");			
		}
		
		if(msg.isDefined("DataFloat")){
			dataFloat =  msg.getBytes("DataFloat");			
		}
		
		byte time[] = null; 
		if(msg.isDefined("Time")){		
			time =  msg.getBytes("Time");
		}
		
		return new TransmitDataRequest(requestType, request, dataInt, dataFloat, time, originalLength);
	}

	public static Reject parseReject(Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);
		int refSeqNum = msg.getInt("RefSeqNum");
		Reject rejectMessage = new Reject(messageHeader, refSeqNum);

		if(msg.isDefined("RefTagID")) {
			int refTagID = msg.getInt("RefTagID");
			rejectMessage.setRefTagID(refTagID);
		}
		if(msg.isDefined("RefMsgType")){
			String refMsgType = msg.getString("RefMsgType").trim();
			rejectMessage.setRefMsgType(refMsgType);
		}
		if(msg.isDefined("RefApplVerID")) {
			String refApplVerID = msg.getString("RefApplVerID").trim();
			rejectMessage.setRefApplVerID(refApplVerID);
		}
		if(msg.isDefined("RefApplExtID")){
			int refApplExtID = msg.getInt("RefApplExtID");
			rejectMessage.setRefApplExtID(refApplExtID);
		}
		if(msg.isDefined("RefCstmApplVerID")) {
			String refCstmApplVerID = msg.getString("RefCstmApplVerID").trim();
			rejectMessage.setRefCstmApplVerID(refCstmApplVerID);
		}
		if(msg.isDefined("SessionRejectReason")) {
			int sessionRejectReasonCode = msg.getInt("SessionRejectReason");
			SessionRejectReasonType sessionRejectReasonType = SessionRejectReasonType.geSessionRejectReasonType(sessionRejectReasonCode);
			rejectMessage.setSessionRejectReasonType(sessionRejectReasonType);
		}
		if(msg.isDefined("Text")) {
			String text = msg.getString("Text");
			rejectMessage.setText(text);
		}
		return rejectMessage;
	}
	
}
