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
package com.portfolioeffect.quant.client.message;


import com.portfolioeffect.quant.client.message.util.ArrayUtil;



public class TransmitDataRequest extends AbstractMessage{


	private static final long serialVersionUID = -5922745454349158176L;

	private final int dataLength;
	private final byte[] dataInt;
	private final byte[] dataFloat;
	private final byte[] time;
	
	public TransmitDataRequest(String msgType, String msgBody, byte[] dataInt, byte[] dataFloat, byte[] time, int dataLength) {
		super(msgType, msgBody);
		this.dataFloat = dataFloat;
		this.dataInt=dataInt;
		this.time=time;
		this.dataLength = dataLength;
	}
	
	public byte[] getDataIntByte(){
		return dataInt;
	}
	
	public int[] getDataInt() throws Exception {
		if(dataInt ==null)
			throw new Exception("No such type data: dataInt");
		
		return ArrayUtil.unpackAndDecompressInts(dataLength, dataInt);
	}

	public byte[] getDataFloatByte(){
		return dataFloat;
	}
	
	public float[] getDataFloat() throws Exception {
		
		if(dataFloat ==null)
			throw new Exception("No such type data: dataFloat");
		return ArrayUtil.unpackAndDecompressFloats(dataLength, dataFloat);
	}


	public byte[] getDataTimeByte(){
		return time;
	}
	
	public long[] getTime() throws Exception {
		if(time ==null)
			throw new Exception("No such type data: time");
		return ArrayUtil.unpackAndDecompressLongs(dataLength, time);
	}

	public int getDataLength() {
		return dataLength;
	}
	
}
