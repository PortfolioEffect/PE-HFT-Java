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
import com.portfolioeffect.quant.message.util.msg.test.SampleClientRequestLongTime;

public class ByteVectorCompressedTest {

	private String templatesFileName = "byteVector.compressed.xml";
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
	
	public static void populateTimeArray(long[] array) {
		for(int i = 0; i < array.length; i++) {
			array[i] = 	1409648396000L + i * 1000L;
		}			   
	}
	

	
	@Test
	public void testByteArrayCompressedTest () throws Exception {
		
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
		
		int[] time = new int[obsInDay * daysCount* assetsCount];
		populateTimeArray(time);
		
		
		float[] result = new float[obsInDay * daysCount* assetsCount];
		populateResult(result);
		
		long timeEndMillis = System.currentTimeMillis();
		System.out.println("Allocation (ms): " + (timeEndMillis - timeStartMillis));

		timeStartMillis = System.currentTimeMillis();
		Message msg = createRemoteClientRequest(templateRegistry, requestType, request, price, time, result, msgSeqNum, timestamp);
		timeEndMillis = System.currentTimeMillis();
		
		int sizeActual = encoder.encode(msg).length /1024;
		int sizeTheoretical =  obsInDay * daysCount * assetsCount * ( 12) / 1024;
		
		System.out.println("Factory (ms): " + (timeEndMillis - timeStartMillis) + 
				" \n  Size [actual] (kb): " + sizeActual + " Size [theoretical] (kb): " + sizeTheoretical + " Compressed size: " + sizeActual/(double)sizeTheoretical * 100 + "%");

		timeStartMillis = System.currentTimeMillis();
		SampleClientRequest clientRequestMessage = parseClientRemoteRequest(msg);
		timeEndMillis = System.currentTimeMillis();
		System.out.println("Parser (ms): " + (timeEndMillis - timeStartMillis));
		
		Assert.assertArrayEquals(price, clientRequestMessage.getPrice());
		Assert.assertArrayEquals(time, clientRequestMessage.getTime());
	//	Assert.assertArrayEquals(result, clientRequestMessage.getResult(), 0.000005);
	}
	
	@Test
	public void testByteArrayCompressedLongTimeTest () throws Exception {
		
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
		
		long[] time = new long[obsInDay * daysCount* assetsCount];
		populateTimeArray(time);
		
		
		float[] result = new float[obsInDay * daysCount* assetsCount];
		populateResult(result);
		
		long timeEndMillis = System.currentTimeMillis();
		System.out.println("Allocation (ms): " + (timeEndMillis - timeStartMillis));

		timeStartMillis = System.currentTimeMillis();
		Message msg = createRemoteClientRequestLongTime(templateRegistry, requestType, request, price, time, result, msgSeqNum, timestamp);
		timeEndMillis = System.currentTimeMillis();
		
		int sizeActual = encoder.encode(msg).length /1024;
		int sizeTheoretical =  obsInDay * daysCount * assetsCount * ( 14) / 1024;
		
		System.out.println("Factory (ms): " + (timeEndMillis - timeStartMillis) + 
				" \n  Size [actual] (kb): " + sizeActual + " Size [theoretical] (kb): " + sizeTheoretical + " Compressed size: " + sizeActual/(double)sizeTheoretical * 100 + "%");

		timeStartMillis = System.currentTimeMillis();
		SampleClientRequestLongTime clientRequestMessage = parseClientRemoteRequestLongTime(msg);
		timeEndMillis = System.currentTimeMillis();
		System.out.println("Parser (ms): " + (timeEndMillis - timeStartMillis));
		
		Assert.assertArrayEquals(price, clientRequestMessage.getPrice());
		Assert.assertArrayEquals(time, clientRequestMessage.getTime());
		
	//	Assert.assertArrayEquals(time,  ArrayUtil.toLongArray( ArrayUtil.toIntArray(time)));
	//	Assert.assertArrayEquals(result, clientRequestMessage.getResult(), 0.000005);
	}

	
	
	public static Message createRemoteClientRequest(TemplateRegistry templateRegistry,
			String requestType, String request, int[] price, int[] time, float[] result, int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("RemoteRequest");
		Message msg = new Message(template);
		
		
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("RequestType", requestType);

		if(request!=null) {
			msg.setString("Request", request);
		}
		
		msg.setByteVector("Price",  ArrayUtil.packAndCompressInts(price));
		msg.setByteVector("Time", ArrayUtil.packAndCompressInts(time));
		msg.setByteVector("Result", ArrayUtil.packAndCompressFloats(result));
		
		int originalLength = price.length;
		msg.setInteger("OriginalLength", originalLength);
		
		return msg;
	}
	
	public static SampleClientRequest parseClientRemoteRequest(Message msg) throws Exception {
	
		String requestType = msg.getString("RequestType");
		String request = null;

		if (msg.isDefined("Request"))
			request = msg.getString("Request");

		int originalLength = msg.getInt("OriginalLength");
		int price[] = ArrayUtil.unpackAndDecompressInts(originalLength, msg.getBytes("Price"));
		int time[] =  ArrayUtil.unpackAndDecompressInts(originalLength, msg.getBytes("Time"));
		float result[] =  ArrayUtil.unpackAndDecompressFloats(originalLength, msg.getBytes("Result"));
		
		return new SampleClientRequest(requestType, request, price, time, result);
	}

	
	public static Message createRemoteClientRequestLongTime(TemplateRegistry templateRegistry,
			String requestType, String request, int[] price, long[] time, float[] result, int msgSeqNum, long timestamp) {
		MessageTemplate template = templateRegistry.get("RemoteRequest");
		Message msg = new Message(template);
		
		
		msg.setInteger("MsgSeqNum", msgSeqNum);
		msg.setLong("SendingTime", timestamp);
		msg.setString("RequestType", requestType);

		if(request!=null) {
			msg.setString("Request", request);
		}
		
		msg.setByteVector("Price",  ArrayUtil.packAndCompressInts(price));
		msg.setByteVector("Time", ArrayUtil.packAndCompressLongs(time));
		msg.setByteVector("Result", ArrayUtil.packAndCompressFloats(result));
		
		int originalLength = price.length;
		msg.setInteger("OriginalLength", originalLength);
		
		return msg;
	}
	
	public static SampleClientRequestLongTime parseClientRemoteRequestLongTime(Message msg) throws Exception {
	
		String requestType = msg.getString("RequestType");
		String request = null;

		if (msg.isDefined("Request"))
			request = msg.getString("Request");

		int originalLength = msg.getInt("OriginalLength");
		int price[] = ArrayUtil.unpackAndDecompressInts(originalLength, msg.getBytes("Price"));
		long time[] =  ArrayUtil.unpackAndDecompressLongs(originalLength, msg.getBytes("Time"));
		float result[] =  ArrayUtil.unpackAndDecompressFloats(originalLength, msg.getBytes("Result"));
		
		return new SampleClientRequestLongTime(requestType, request, price, time, result);
	}


	
}
