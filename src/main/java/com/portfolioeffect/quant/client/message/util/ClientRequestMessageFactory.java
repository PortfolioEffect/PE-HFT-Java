/**
 *  SNOWFALL SYSTEMS, INC. CONFIDENTIAL CONTROLLED.  DO NOT COPY OR DISTRIBUTE FURTHER.
 *  (c) 2012 Snowfall Systems, Inc.  All rights reserved.
 */
package com.portfolioeffect.quant.client.message.util;
/*
 * #%L
 * Ice-9 Platform Java API
 * %%
 * Copyright (C) 2010 - 2014 Snowfall Systems, Inc.
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

import org.openfast.*;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Sequence;
import org.openfast.template.TemplateRegistry;

import com.portfolioeffect.quant.client.message.type.EncryptMethodType;
import com.portfolioeffect.quant.client.message.type.EncryptedPasswordMethodType;

public class ClientRequestMessageFactory {

	public static Message createLogonRequest(TemplateRegistry templateRegistry, 
			int heartBtInt, EncryptMethodType encryptMethodType, String username, 
			String encryptedPassword, EncryptedPasswordMethodType encryptedPasswordMethodType,
			int encryptedPasswordLength, int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("Logon");
		Message msg = new Message(template);
		msg.setString("Username", username); 
		msg.setString("EncryptedPassword", encryptedPassword); 
		msg.setInteger("EncryptedPasswordLen", encryptedPasswordLength);
		msg.setInteger("EncryptedPasswordMethod", encryptedPasswordMethodType.getCode());
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setInteger("EncryptMethod", encryptMethodType.getCode());
		msg.setInteger("HeartBtInt", heartBtInt);
		msg.setLong("SendingTime", timestamp);
		return msg;
	}


	public static Message createLogoutRequest(TemplateRegistry templateRegistry, 
			int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("Logout");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		return msg;
	}

	public static Message createHeartbeat(TemplateRegistry templateRegistry,
			int msgSeqNum, String testReqId) {
		MessageTemplate template = templateRegistry.get("Heartbeat");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", System.currentTimeMillis());
		msg.setString("TestReqID", testReqId);
		return msg;
	}
	
	public static Message createNonparametricComputeRequest(TemplateRegistry templateRegistry, String requestType, 
			String request, double[] price, int[] time, int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("FunctionComputeRequest");
		Message msg = new Message(template);

		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);

		msg.setString("Request", request);
		msg.setString("RequestType", requestType);

		Sequence sequence = template.getSequence("Data");
		SequenceValue sequenceValue = new SequenceValue(sequence);
		for(int i=0; i<price.length; i++){
			FieldValue[] fieldValue = new FieldValue[2];
			int priceCent = (int) (price[i]*100);
			fieldValue[0] =  new IntegerValue( priceCent);
			fieldValue[1] =  new IntegerValue(time[i]);					
			sequenceValue.add(fieldValue);
		}

		msg.setFieldValue("Data", sequenceValue);



		return msg;
	}

	public static Message createValidationRequest(TemplateRegistry templateRegistry,
			String request,			
			int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("IndicatorValidationRequest");
		Message msg = new Message(template);

		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("Request", request);


		return msg;
	}


	public static Message createLoadDataRequest(TemplateRegistry templateRegistry, String requestType,
			String request, double[] data,			
			int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("LoadDataRequest");
		Message msg = new Message(template);

		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("Request", request);
		msg.setString("RequestType", requestType);

		Sequence sequence = template.getSequence("Data");
		SequenceValue sequenceValue = new SequenceValue(sequence);
		for(int i=0; i<data.length; i++){
			FieldValue[] fieldValue = new FieldValue[1];
			fieldValue[0] =  new DecimalValue( data[i]);
			sequenceValue.add(fieldValue);
		}

		msg.setFieldValue("Data", sequenceValue);

		return msg;
	}

	public static Message createLoadDataRequest(TemplateRegistry templateRegistry, String requestType,
			String request, int[] data,			
			int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("LoadDataRequest");
		Message msg = new Message(template);

		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);

		msg.setString("Request", request);
		msg.setString("RequestType", requestType);

		Sequence sequence = template.getSequence("Data");
		SequenceValue sequenceValue = new SequenceValue(sequence);
		for(int i=0; i<data.length; i++){
			FieldValue[] fieldValue = new FieldValue[1];
			fieldValue[0] =  new DecimalValue( data[i]);
			sequenceValue.add(fieldValue);
		}

		msg.setFieldValue("Data", sequenceValue);



		return msg;
	}

	
	public static Message createTransactionalPortfolioComputeRequest(TemplateRegistry templateRegistry, String requestType,
			String request, String params,  int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("TransactionalPortfolioComputeRequest");
		Message msg = new Message(template);

		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		
		msg.setString("RequestType", requestType);
		msg.setString("Request", request);
		msg.setString("Params", params);

		return msg;
	}

}
