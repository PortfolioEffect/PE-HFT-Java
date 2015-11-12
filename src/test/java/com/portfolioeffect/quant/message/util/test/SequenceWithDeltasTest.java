/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
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
package com.portfolioeffect.quant.message.util.test;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openfast.Context;
import org.openfast.DecimalValue;
import org.openfast.FieldValue;
import org.openfast.GroupValue;
import org.openfast.IntegerValue;
import org.openfast.Message;
import org.openfast.SequenceValue;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.template.Sequence;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import com.portfolioeffect.quant.message.util.msg.test.SampleClientRequest;

public class SequenceWithDeltasTest {

	private String templatesFileName = "sequenceWithDeltas.xml";
	private TemplateRegistry templateRegistry ;
	private FastEncoder encoder;

	@Before
	public void before() {
		XMLMessageTemplateLoader xmlMessageTemplateLoader = new XMLMessageTemplateLoader();
		xmlMessageTemplateLoader.setLoadTemplateIdFromAuxId(true);
		xmlMessageTemplateLoader.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(templatesFileName));
		templateRegistry = xmlMessageTemplateLoader.getTemplateRegistry();

		Context encodingContext = new Context();
		encodingContext.setTemplateRegistry(templateRegistry);
		encoder = new FastEncoder(encodingContext);

	}
	
	public static void populatePriceArray(int[] array) {
		for(int i = 0; i < array.length; i++) {
			array[i] = (int) ( 200000 + 50 * Math.random());
		}
	}
	
	public static void populateResult(float[] array) {
		for(int i = 0; i < array.length; i++) {
			array[i] = (float) (.9 +  0.05* Math.random());
		}
	}
	
	public static void populateTimeArray(int[] array) {
		for(int i = 0; i < array.length; i++) {
			array[i] = 1411554044 + i * 1000;
		}
	}
	

	@Test
	public void testSequenceWithDeltas () {
		final int daysCount = 256;
		final int obsInDay = 23400;
		final int assetsCount = 1;

		final String requestType = "SomeRequest";
		final String request = "Request";
		int msgSeqNum = 1;
		long timestamp = 0;

		long timeStartMillis = System.currentTimeMillis();

		int[] price = new int[obsInDay * daysCount * assetsCount];
		populatePriceArray(price);
		
		int[] time = new int[obsInDay * daysCount * assetsCount];
		populateTimeArray(time);

		float[] result = new float[obsInDay * daysCount* assetsCount];
		populateResult(result);
		
		long timeEndMillis = System.currentTimeMillis();
		System.out.println("Allocation (ms): " + (timeEndMillis - timeStartMillis));

		timeStartMillis = System.currentTimeMillis();
		Message msg = createSampleClientRequest(templateRegistry, requestType, request, price, time, result, msgSeqNum, timestamp);
		timeEndMillis = System.currentTimeMillis();

		int sizeActual = encoder.encode(msg).length /1024;
		int sizeTheoretical =  obsInDay * daysCount * assetsCount * (4 + 4  + 4) / 1024;

		System.out.println("Factory (ms): " + (timeEndMillis - timeStartMillis) + 
				" \n  Size [actual] (kb): " + sizeActual + " Size [theoretical] (kb): " + sizeTheoretical + " Compressed size: " + sizeActual/(double)sizeTheoretical * 100 + "%");

		timeStartMillis = System.currentTimeMillis();
		SampleClientRequest clientRequestMessage = parseSampleClientRequest(msg); 
		timeEndMillis = System.currentTimeMillis();
		System.out.println("Parser (ms): " + (timeEndMillis - timeStartMillis));
		
		Assert.assertArrayEquals(price, clientRequestMessage.getPrice());
		Assert.assertArrayEquals(time, clientRequestMessage.getTime());
	}


	public static Message createSampleClientRequest(TemplateRegistry templateRegistry,
			String requestType, String request, int[] price, int[] time, float[] result, int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("RemoteRequest");
		Message msg = new Message(template);

		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("RequestType", requestType);

		if(request!=null)
			msg.setString("Request", request);

		if(price!=null){

			Sequence sequence = template.getSequence("Data");
			SequenceValue sequenceValue = new SequenceValue(sequence);
			for(int i=0; i<price.length; i++)
			{
				FieldValue[] fieldValue = new FieldValue[3];

				fieldValue[0] =  new IntegerValue( price[i]);
				fieldValue[1] =  new IntegerValue(time[i]);
				fieldValue[2] =  new DecimalValue(result[i]);
				sequenceValue.add(fieldValue);
			}

			msg.setFieldValue("Data", sequenceValue);

		}

		return msg;
	}


	public static SampleClientRequest parseSampleClientRequest(Message msg) {
	
		String requestType = msg.getString("RequestType");
		String request = null;

		if (msg.isDefined("Request"))
			request = msg.getString("Request");

		int price[] = null;
		int time[] = null;
		float result[] = null;
		SequenceValue sValue = msg.getSequence("Data");

		int numberChunks = sValue.getValues().length;

		price = new int[numberChunks];
		time = new int[numberChunks];
		result = new float[numberChunks];

		int i = 0;
		for (GroupValue gv : sValue.getValues()) {
			price[i] = gv.getInt("price");
			time[i] = gv.getInt("time");
			result[i] =(float) gv.getDouble("result");
			i++;
		}

		return new SampleClientRequest(requestType, request, price, time, result);
	}

}
