/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
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
