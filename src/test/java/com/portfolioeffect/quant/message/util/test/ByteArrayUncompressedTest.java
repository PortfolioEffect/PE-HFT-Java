/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
 * %%
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 * #L%
 */
package com.portfolioeffect.quant.message.util.test;



import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastEncoder;
import org.openfast.template.MessageTemplate;
import org.openfast.template.TemplateRegistry;
import org.openfast.template.loader.XMLMessageTemplateLoader;

import com.portfolioeffect.quant.client.message.util.ArrayUtil;
import com.portfolioeffect.quant.message.util.msg.test.SampleClientRequest;

public class ByteArrayUncompressedTest {

	private String templatesFileName = "byteVector.original.xml";
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
			array[i] = (int) ( 200000 + 100 * Math.random());
		}
	}
	
	public static void populateResult(float[] array) {
		for(int i = 0; i < array.length; i++) {
			array[i] = (float) (Math.round((9 +  Math.random() * 2 ) *10000)/10000);
		}
	}
	
	public static void populateTimeArray(int[] array) {
		for(int i = 0; i < array.length; i++) {
			array[i] = 1411554044 + i * 1000;
		}
	}
	
	@Test
	public void testRemoteRequest () {
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
		
		int[] time = new int[obsInDay * daysCount *  assetsCount];
		populateTimeArray(time);
		
		float[] result = new float[obsInDay * daysCount* assetsCount];
		populateResult(result);
		
		long timeEndMillis = System.currentTimeMillis();
		System.out.println("Allocation (ms): " + (timeEndMillis - timeStartMillis));

		timeStartMillis = System.currentTimeMillis();
		Message msg = createRemoteClientRequest(templateRegistry, requestType, request, price, time, result, msgSeqNum, timestamp);
		timeEndMillis = System.currentTimeMillis();
		
		int sizeActual = encoder.encode(msg).length /1024;
		int sizeTheoretical =  obsInDay * daysCount * assetsCount * (12) / 1024;
		
		System.out.println("Factory (ms): " + (timeEndMillis - timeStartMillis) + 
				" \n  Size [actual] (kb): " + sizeActual + " Size [theoretical] (kb): " + sizeTheoretical + " Compressed size: " + sizeActual/(double)sizeTheoretical * 100 + "%");

		timeStartMillis = System.currentTimeMillis();
		SampleClientRequest clientRequestMessage = parseClientRemoteRequest(msg);
		timeEndMillis = System.currentTimeMillis();
		System.out.println("Parser (ms): " + (timeEndMillis - timeStartMillis));
		
		Assert.assertArrayEquals(price, clientRequestMessage.getPrice());
		Assert.assertArrayEquals(time, clientRequestMessage.getTime());
	}
	
	
	public static Message createRemoteClientRequest(TemplateRegistry templateRegistry,
			String requestType, String request, int[] price, int[] time, float[] result, int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("RemoteRequest");
		Message msg = new Message(template);
		
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("RequestType", requestType);

		if(request!=null)
			msg.setString("Request", request);

		if(price!=null){
			msg.setByteVector("Price", ArrayUtil.toByteArray(price));
			msg.setByteVector("Time", ArrayUtil.toByteArray(time));
			msg.setByteVector("Result", ArrayUtil.toByteArray(result));
		}
		return msg;
	}
	

	public static SampleClientRequest parseClientRemoteRequest(
			Message msg) {

		String requestType = msg.getString("RequestType");
		String request = null;

		if (msg.isDefined("Request"))
			request = msg.getString("Request");

		int price[] = ArrayUtil.toIntArray(msg.getBytes("Price"));
		int time[] = ArrayUtil.toIntArray(msg.getBytes("Time"));
		float result[] = ArrayUtil.toFloatArray(msg.getBytes("Result"));
	
		return new SampleClientRequest(requestType, request, price, time, result);
	}
	
}
