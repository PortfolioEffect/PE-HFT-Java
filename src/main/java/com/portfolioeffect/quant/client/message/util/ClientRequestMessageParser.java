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
import com.portfolioeffect.quant.client.message.LogonRequest;
import com.portfolioeffect.quant.client.message.LogoutRequest;
import com.portfolioeffect.quant.client.message.NonparametricComputeRequest;
import com.portfolioeffect.quant.client.message.PortfolioComputeRequest;
import com.portfolioeffect.quant.client.message.StandardHeader;
import com.portfolioeffect.quant.client.message.TestRequest;
import com.portfolioeffect.quant.client.message.ValidationRequest;
import com.portfolioeffect.quant.client.message.type.EncryptedPasswordMethodType;
import com.portfolioeffect.quant.client.message.type.FastMessageType;
 
/**
 * @author alex
 * 
 */
public class ClientRequestMessageParser {

	
	public static StandardHeader parseMessageHeader(Message msg) {
		String applicationVersionId = msg.getString("ApplVerID").trim(); 
		String messageCode = msg.getString("MessageType").trim();
		FastMessageType messageType = FastMessageType.getFastMessageType(messageCode);
		int messageSequenceNumber = msg.getInt("MsgSeqNum"); 
		long messageSendingTime = msg.getLong("SendingTime"); 

		StandardHeader messageHeader = new StandardHeader(applicationVersionId, messageType,
				messageSequenceNumber, messageSendingTime);
		return messageHeader;
	}

	// Logon request parser
	public static LogonRequest parseLogonMsg(Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);
		String username = msg.getString("Username").trim();
		String encryptedPassword = msg.getString("EncryptedPassword").trim();
		int encryptPasswordLength = msg.getInt("EncryptedPasswordLen");
		int encryptMethodCode = msg.getInt("EncryptedPasswordMethod");

		EncryptedPasswordMethodType encryptMethodType = EncryptedPasswordMethodType.getEncryptedPasswordMethodType(encryptMethodCode);
		LogonRequest logonRequest =  new LogonRequest(messageHeader, username, encryptedPassword, encryptMethodType,  encryptPasswordLength);
		return logonRequest;
	}

	// Logout request parser
	public static LogoutRequest parseLogoutRequest(Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);
		LogoutRequest logoutRequest =  new LogoutRequest(messageHeader);
		return logoutRequest;
	}

	public static Heartbeat parseHeartbeat(Message msg){
		StandardHeader messageHeader = parseMessageHeader(msg);
		Heartbeat heartbeat = new Heartbeat(messageHeader);
		if(msg.isDefined("TestReqID")) {
			String testReqID = msg.getString("TestReqID").trim();
			heartbeat.setTestReqID(testReqID);
		}
		return heartbeat;
	}


	public static TestRequest parseTestRequest(Message msg){
		StandardHeader messageHeader = parseMessageHeader(msg);
		TestRequest testRequest = new TestRequest(messageHeader);
		if(msg.isDefined("TestReqID")) {
			String testReqID = msg.getString("TestReqID").trim();
			testRequest.setTestReqID(testReqID);
		}
		return testRequest;
	}
	
	
	public static NonparametricComputeRequest parseNonparametricComputeRequest(
			Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);

		
		String requestType = msg.getString("RequestType");
		String request = msg.getString("Request");

		SequenceValue sValue = msg.getSequence("Data");
			int lenght= sValue.getLength();
			double price[] = new double[lenght];
			int time[] = new int[lenght];
			
			int i = 0;
			for (GroupValue gv : sValue.getValues()) {
				int priceCent = gv.getInt("price");
				double priceDollar = priceCent * 0.01;
				int curentTime = gv.getInt("time");
				price[i] = priceDollar;
				time[i] = curentTime;
				i++;
			}
		
		return new NonparametricComputeRequest(requestType, request, price, time); 
	}


	public static ValidationRequest parseValidationRequest(
			Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);
		String request = null;
		if (msg.isDefined("Request"))
			request = msg.getString("Request");

		return new ValidationRequest(request); 
				
	}

	
	public static PortfolioComputeRequest parsePortfolioComputeRequest(
			Message msg) {
		StandardHeader messageHeader = parseMessageHeader(msg);
		
		String request = null;
		String params = null;
		if (msg.isDefined("Request"))
			request = msg.getString("Request");
		
		if (msg.isDefined("Params"))
			params = msg.getString("Params");
		
		String requestType = msg.getString("RequestType");

		return new PortfolioComputeRequest(requestType, request, params);				
				
	}


}
