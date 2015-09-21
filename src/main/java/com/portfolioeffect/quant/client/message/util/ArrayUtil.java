/*
 * #%L
 * Ice-9 Tickerplant - Server
 * %%
 * Copyright (C) 2014 - 2015 Snowfall Systems, Inc.
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
package com.portfolioeffect.quant.client.message.util;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.Adler32;

import me.lemire.integercompression.Composition;
import me.lemire.integercompression.DeltaZigzagBinaryPacking;
import me.lemire.integercompression.DeltaZigzagVariableByte;
import me.lemire.integercompression.IntWrapper;
import me.lemire.integercompression.IntegerCODEC;

import org.iq80.snappy.Snappy;

public class ArrayUtil {

	private static final IntegerCODEC codec = new Composition(new DeltaZigzagBinaryPacking(), new DeltaZigzagVariableByte());
	
	public static byte[] packAndCompressInts(int[] intArray) {
		return compressBytes(toByteArray(packInt(intArray)));
	}
	
	public static int[] unpackAndDecompressInts(int originalLength, byte[] bytesArray) throws Exception {
		return unpackInt(toIntArray(decompressBytes(bytesArray)), originalLength);
	}
	
	public static byte[] packAndCompressFloats(float[] floatArray) {
		return compressBytes(toByteArray(packInt(toIntArray(floatArray))));
	}
	
	public static byte[] packAndCompressLongs(long[] longArray) {
		return compressBytes(toByteArray(packInt(toIntArray(longArray))));
	}
	
	public static byte[] packAndCompressFloats(double[] doubleArray) {
		return compressBytes(toByteArray(packInt(toIntArray(doubleArray))));
	}
	
	
	public static float[] unpackAndDecompressFloats(int originalLength, byte[] bytesArray) throws Exception {
		return toFloatArray(unpackInt(toIntArray(decompressBytes(bytesArray)), originalLength));
	}
	
	public static long[] unpackAndDecompressLongs(int originalLength, byte[] bytesArray) throws Exception {
		return toLongArray(unpackInt(toIntArray(decompressBytes(bytesArray)), originalLength*2));
	}
	

	
	public static byte[] compressBytes(byte[] bytesArray) { 
		
		
		int decompressedLength = bytesArray.length;	
		int maxCompressedLength = Snappy.maxCompressedLength(decompressedLength);
		byte[] compressed = new byte[maxCompressedLength];
		int compressedLength = Snappy.compress(bytesArray, 0, decompressedLength, compressed, 0);
		byte[] truncated = Arrays.copyOfRange(compressed, 0, compressedLength );
		byte[] checkSumExt = Arrays.copyOf(truncated, truncated.length+8);
		
		Adler32 adler32 = new Adler32();
		adler32.update(truncated);
		long checkSum =adler32.getValue();
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		byte[] checkSumBuf  =buffer.putLong(checkSum).array();
		
		checkSumExt[truncated.length] = checkSumBuf[0];
		checkSumExt[truncated.length+1] = checkSumBuf[1];
		checkSumExt[truncated.length+2] = checkSumBuf[2];
		checkSumExt[truncated.length+3] = checkSumBuf[3];
		checkSumExt[truncated.length+4] = checkSumBuf[4];
		checkSumExt[truncated.length+5] = checkSumBuf[5];
		checkSumExt[truncated.length+6] = checkSumBuf[6];
		checkSumExt[truncated.length+7] = checkSumBuf[7];
		
		
		return checkSumExt;
	}
	
	public static byte[] decompressBytes(byte[] bytesArray) throws Exception { 
		
		byte[] checkSumBuf = new byte[8];
		checkSumBuf[0] = bytesArray[bytesArray.length-8];
		checkSumBuf[1] = bytesArray[bytesArray.length-7];
		checkSumBuf[2] = bytesArray[bytesArray.length-6];
		checkSumBuf[3] = bytesArray[bytesArray.length-5];
		checkSumBuf[4] = bytesArray[bytesArray.length-4];
		checkSumBuf[5] = bytesArray[bytesArray.length-3];
		checkSumBuf[6] = bytesArray[bytesArray.length-2];
		checkSumBuf[7] = bytesArray[bytesArray.length-1];
		
		
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	    buffer.put(checkSumBuf);
	    buffer.flip();//need flip 
	    long checkSum = buffer.getLong();
	    
	    Adler32 adler32 = new Adler32();
		adler32.update(bytesArray, 0, bytesArray.length-8);
		if(checkSum !=adler32.getValue())
			throw new Exception("Data corruption detected - checksum failure. Please, try again.");
	    
		return Snappy.uncompress(bytesArray, 0, bytesArray.length -8 );
	}

	public static int[] packInt(int[] data) {
		int[] outBuf = new int[data.length * 4];
		IntWrapper inPos = new IntWrapper();
		IntWrapper outPos = new IntWrapper();
		codec.compress(data, inPos, data.length, outBuf, outPos);
		return Arrays.copyOf(outBuf, outPos.get());
	}

	public static int[] unpackInt(int[] data, int len) {
		int[] outBuf = new int[len + 1024];
		IntWrapper inPos = new IntWrapper();
		IntWrapper outPos = new IntWrapper();
		codec.uncompress(data, inPos, data.length, outBuf, outPos);
		return Arrays.copyOf(outBuf, outPos.get());
	}
	
	
	/*
	 * To Byte Array Methods 
	 */
	
	public static byte[] toByteArray(float[] floatArray){
	    int times = Float.SIZE / Byte.SIZE;
	    byte[] bytes = new byte[floatArray.length * times];
	    for(int i=0;i<floatArray.length;i++){
	        ByteBuffer.wrap(bytes, i*times, times).putFloat(floatArray[i]);
	    }
	    return bytes;
	}
	
	public static byte[] toByteArray(double[] doubleArray){
	    int times = Double.SIZE / Byte.SIZE;
	    byte[] bytes = new byte[doubleArray.length * times];
	    for(int i=0;i<doubleArray.length;i++){
	        ByteBuffer.wrap(bytes, i*times, times).putDouble(doubleArray[i]);
	    }
	    return bytes;
	}
	
	public static byte[] toByteArray(int[] intArray){
		int times = Integer.SIZE / Byte.SIZE;
		byte[] bytes = new byte[intArray.length * times];
		for(int i=0;i<intArray.length;i++){
			ByteBuffer.wrap(bytes, i*times, times).putInt(intArray[i]);
		}
		return bytes;
	}
	
	public static byte[] toByteArray(long[] longArray){
	    int times = Long.SIZE / Byte.SIZE;
	    byte[] bytes = new byte[longArray.length * times];
	    for(int i=0;i<longArray.length;i++){
	        ByteBuffer.wrap(bytes, i*times, times).putLong(longArray[i]);
	    }
	    return bytes;
	}
	
	/*
	 * To primitive array methods
	 */

	public static double[] toDoubleArray(byte[] byteArray){
	    int times = Double.SIZE / Byte.SIZE;
	    double[] doubles = new double[byteArray.length / times];
	    for(int i=0;i<doubles.length;i++){
	        doubles[i] = ByteBuffer.wrap(byteArray, i*times, times).getDouble();
	    }
	    return doubles;
	}
	
	public static float[] toFloatArray(byte[] byteArray){
	    int times = Float.SIZE / Byte.SIZE;
	    float[] floats = new float[byteArray.length / times];
	    for(int i=0;i<floats.length;i++){
	        floats[i] = ByteBuffer.wrap(byteArray, i*times, times).getFloat();
	    }
	    return floats;
	}


	public static int[] toIntArray(byte[] byteArray){
	    int times = Integer.SIZE / Byte.SIZE;
	    int[] ints = new int[byteArray.length / times];
	    for(int i=0;i<ints.length;i++){
	        ints[i] = ByteBuffer.wrap(byteArray, i*times, times).getInt();
	    }
	    return ints;
	}
	
	
	public static long[] toLongArray(byte[] byteArray){
	    int times = Long.SIZE / Byte.SIZE;
	    long[] longs = new long[byteArray.length / times];
	    for(int i=0;i<longs.length;i++){
	    	longs[i] = ByteBuffer.wrap(byteArray, i*times, times).getLong();
	    }
	    return longs;
	}
	
	public static int[] toIntArray(float[] floatArray){
		int[] ints = new int[floatArray.length];
		for(int i = 0; i < floatArray.length; i++) {
			int bits = Float.floatToIntBits(floatArray[i]);
			ints[i] = bits;
		}
		
		return ints; 
	}
	
	
	public static int[] toIntArray(long[] longArray){
		int[] ints = new int[longArray.length*2];
		for(int i = 0 , j=0; i < longArray.length; i++, j+=2) {
			
	
			
			int x = (int)(longArray[i] >> 32);
			int y = (int)longArray[i];
			ints[i] = x;
			ints[i+longArray.length] = y;

		}
		
		return ints; 
	}
	
	public static long[] toLongArray(int[] intArray){
	    
	    long[] longs = new long[intArray.length/2];
	    for(int i=0 , j=0;i<longs.length;i++,j+=2){
	    	
	    	int x= intArray[i]; 
	    	int y= intArray[i+longs.length];
	    	longs[i] =(((long)x) << 32) | (y & 0xffffffffL);
	    			
	    }
	    return longs;
	}
	
	
	public static int[] toIntArray(double[] doubleArray){
		int[] ints = new int[doubleArray.length];
		for(int i = 0; i < doubleArray.length; i++) {
			int bits = Float.floatToIntBits((float)doubleArray[i]);
			ints[i] = bits;
		}
		
		return ints; 
	}
	
	
	public static float[] toFloatArray(int[] intArray) {
		float[] floats = new float[intArray.length];
		for(int i = 0; i < intArray.length; i++) {
			float value = Float.intBitsToFloat(intArray[i]);
			floats[i] = value;
		}
		
		return floats; 
	}
}
