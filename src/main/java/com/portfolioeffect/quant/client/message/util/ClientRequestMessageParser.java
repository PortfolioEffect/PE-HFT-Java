/*
 * #%L
 * Ice-9 Platform Java API
 * %%
 * Copyright (C) 2010 - 2014 Snowfall Systems, Inc.
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
import com.portfolioeffect.quant.client.message.TestEcho;
import com.portfolioeffect.quant.client.message.TestRequest;
import com.portfolioeffect.quant.client.message.TestUpdate;
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

	public static TestUpdate parseTestUpdateMsg(Message msg) {
		long totalCount = msg.getLong("TotalCount");
		long time = msg.getLong("Time");
		double price = msg.getDouble("Price");

		TestUpdate logonRequest =  new TestUpdate(price, time, totalCount);
		return logonRequest;
	}

	public static TestEcho parseTestEcho(Message msg) {
		TestEcho testEcho =  new TestEcho();
		return testEcho;
	}

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


	public static NonparametricComputeRequest parseNonparametricComputeRequest(Message msg) {

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
