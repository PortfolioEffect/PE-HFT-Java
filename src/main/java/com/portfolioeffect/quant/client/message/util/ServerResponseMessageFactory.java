
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
import com.portfolioeffect.quant.client.message.type.SessionRejectReasonType;
import com.portfolioeffect.quant.client.message.type.SessionStatusType;

public class ServerResponseMessageFactory {

	public static Message createLogonResponseMsg(TemplateRegistry templateRegistry,
			int heartBtInt, EncryptMethodType encryptMethodType, SessionStatusType sessionStatusType, 
			int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("Logon");
		Message msg = new Message(template);
		msg.setLong("SendingTime", timestamp);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setInteger("HeartBtInt", heartBtInt); 
		msg.setInteger("SessionStatus", sessionStatusType.getCode());
		msg.setInteger("EncryptMethod", encryptMethodType.getCode());
		return msg;
	}

	public static Message createLogoutResponseMsg(TemplateRegistry templateRegistry, 
			int sessionStatus,  String comment, int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("Logout");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setInteger("SessionStatus", sessionStatus);
		msg.setString("Text", comment); 
		msg.setLong("SendingTime", timestamp);
		return msg;
	}

	public static Message createRejectMsg(
			TemplateRegistry templateRegistry,  int refSeqNum, 
			SessionRejectReasonType reasonType, 
			String refMesssageType, String text, int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("Reject");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setInteger("RefSeqNum", refSeqNum);
		msg.setInteger("SessionRejectReason", reasonType.getCode());
		msg.setString("RefMsgType", refMesssageType);
		msg.setString("Text", text);
		return msg;
	}	

	public static Message createTestRequestMsg(TemplateRegistry templateRegistry, 
			String testReqID, int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("TestRequest");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("TestReqID", testReqID);
		return msg;
	}
	
	public static Message createNonparametricComputeResponse(TemplateRegistry templateRegistry,
			String responseType, String response, float[] data,			
			int msgSeqNum, long timestamp) {

		MessageTemplate template = templateRegistry.get("FunctionComputeResponse");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);

		msg.setString("ResponseType", responseType);

		if(response!=null)
			msg.setString("Response", response);

		if(data!=null){


			Sequence sequence = template.getSequence("Data");
			SequenceValue sequenceValue = new SequenceValue(sequence);
			for(int i=0; i<data.length; i++){
					FieldValue[] fieldValue = new FieldValue[1];
					fieldValue[0] =  new DecimalValue(data[i]);
					sequenceValue.add(fieldValue);
				}


			msg.setFieldValue("Data", sequenceValue);		
		}

		return msg;
	}
	
	public static Message createNonparametricComputeResponse(TemplateRegistry templateRegistry,
			String responseType, String response, double[] data,			
			int msgSeqNum, long timestamp) {

		MessageTemplate template = templateRegistry.get("FunctionComputeResponse");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);

		msg.setString("ResponseType", responseType);

		if(response!=null)
			msg.setString("Response", response);

		if(data!=null){
			Sequence sequence = template.getSequence("Data");
			SequenceValue sequenceValue = new SequenceValue(sequence);
			for(int i=0; i<data.length; i++){
					FieldValue[] fieldValue = new FieldValue[1];
					fieldValue[0] =  new DecimalValue(data[i]);
					sequenceValue.add(fieldValue);
				}


			msg.setFieldValue("Data", sequenceValue);		
		}

		return msg;
	}


	public static Message createValidationResponse(TemplateRegistry templateRegistry,
			String responseType, String response,			
			int msgSeqNum, long timestamp) {

		MessageTemplate template = templateRegistry.get("RemoteResponse");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);

		msg.setString("ResponseType", responseType);

		
			msg.setString("Response", response);

				return msg;
	}
	
	public static Message createPortfolioComputeResponse(TemplateRegistry templateRegistry,
			String responseType, String response, double[][] data,			
			int msgSeqNum, long timestamp) {

		MessageTemplate template = templateRegistry.get("PortfolioComputeResponse");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);

		msg.setString("ResponseType", responseType);

		if(response!=null)
			msg.setString("Response", response);

		if(data!=null){


			msg.setInteger("DataChunkSize", data[0].length);
			msg.setInteger("NumberChunks", data.length);


			Sequence sequence = template.getSequence("Data");
			SequenceValue sequenceValue = new SequenceValue(sequence);
			for(int i=0; i<data.length; i++)
				for(int j=0; j<data[i].length;j++)
				{
					FieldValue[] fieldValue = new FieldValue[1];
					fieldValue[0] =  new DecimalValue(data[i][j]);
					sequenceValue.add(fieldValue);
				}


			msg.setFieldValue("Data", sequenceValue);		
		}

		return msg;
	}


	public static Message createTransmitDataResponse(TemplateRegistry templateRegistry,
			String responseType, String response, 			
			int msgSeqNum, long timestamp) {

		MessageTemplate template = templateRegistry.get("TransmitDataResponse");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);

		msg.setString("ResponseType", responseType);

		if(response!=null)
			msg.setString("Response", response);

		
		return msg;
	}


	public static Message createTransmitDataRequest(TemplateRegistry templateRegistry,
			String requestType, String request, int[] data, long[] time, 			
			int msgSeqNum, long timestamp) {
		
		MessageTemplate template = templateRegistry.get("TransmitDataRequest");
		Message msg = new Message(template);
		
		
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("RequestType", requestType);

		if(request!=null) {
			msg.setString("Request", request);
		}
		
		msg.setByteVector("DataInt",  ArrayUtil.packAndCompressInts(data));
		msg.setByteVector("Time", ArrayUtil.packAndCompressLongs(time));
		int originalLength = data.length;
		msg.setInteger("OriginalLength", originalLength);
		
		return msg;


	
	
	}
	
	public static Message createTransmitDataRequest(TemplateRegistry templateRegistry,
			String requestType, String request, float[] data, long[] time, 			
			int msgSeqNum, long timestamp) {
		
		MessageTemplate template = templateRegistry.get("TransmitDataRequest");
		Message msg = new Message(template);
		
		
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("RequestType", requestType);

		if(request!=null) {
			msg.setString("Request", request);
		}
		
		msg.setByteVector("DataFloat",  ArrayUtil.packAndCompressFloats(data));
		msg.setByteVector("Time", ArrayUtil.packAndCompressLongs(time));
		int originalLength = data.length;
		msg.setInteger("OriginalLength", originalLength);
		
		return msg;


	
	
	}
	
	public static Message createTransmitDataRequest(TemplateRegistry templateRegistry,
			String requestType, String request, byte[] data, byte[] time, int length, 			
			int msgSeqNum, long timestamp) {
		
		MessageTemplate template = templateRegistry.get("TransmitDataRequest");
		Message msg = new Message(template);
		
		
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("RequestType", requestType);

		if(request!=null) {
			msg.setString("Request", request);
		}
		
		msg.setByteVector("DataFloat",  data);
		msg.setByteVector("Time", time);
		int originalLength = length;
		msg.setInteger("OriginalLength", originalLength);
		
		return msg;


	
	
	}
	

	
	public static Message createTransmitDataRequest(TemplateRegistry templateRegistry,
			String requestType, String request, double[] data, long[] time, 			
			int msgSeqNum, long timestamp) {
		
		MessageTemplate template = templateRegistry.get("TransmitDataRequest");
		Message msg = new Message(template);
		
		
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("RequestType", requestType);

		if(request!=null) {
			msg.setString("Request", request);
		}
		
		msg.setByteVector("DataFloat",  ArrayUtil.packAndCompressFloats(data));
		msg.setByteVector("Time", ArrayUtil.packAndCompressLongs(time));
		int originalLength = data.length;
		msg.setInteger("OriginalLength", originalLength);
		
		return msg;


	
	
	}

	
	public static Message createTransmitDataRequest(TemplateRegistry templateRegistry,
			String requestType, String request,  			
			int msgSeqNum, long timestamp) {
		
		MessageTemplate template = templateRegistry.get("TransmitDataRequest");
		Message msg = new Message(template);
		
		
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("RequestType", requestType);

		if(request!=null) {
			msg.setString("Request", request);
		}
		
		msg.setInteger("OriginalLength", 0);
		
		return msg;
	}
	
	public static Message createHeartbeat(
			TemplateRegistry templateRegistry, String testReqID, int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("Heartbeat");
		Message msg = new Message(template);
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("TestReqID", testReqID);
		return msg;
	}

	
}
