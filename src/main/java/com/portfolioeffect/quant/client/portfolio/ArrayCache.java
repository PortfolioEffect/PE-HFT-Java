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
package com.portfolioeffect.quant.client.portfolio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.portfolioeffect.quant.client.util.Console;
import com.portfolioeffect.quant.client.util.MessageStrings;

public class ArrayCache {

	
	
	private File file;
	private int size;
	private RandomAccessFile stream =null;
	private int[] dimensions = new int[] { 1 };
	private boolean isAllZero = true;
	private boolean isAllNaN = true;
	
	private ArrayCacheType type;
	private int nanNumber=0;
	
	private Lock lock = new ReentrantLock();
	
	
	private boolean isNaNFiltered=true;
	private boolean isNaN2Zero=false;
	private byte[] byteBufer= new byte[8];

	public ArrayCache(ArrayCacheType type) throws IOException {
		this.type = type;
		this.size = 0;

		file = File.createTempFile("quant", "tmp");
		file.deleteOnExit();

		stream = new RandomAccessFile(file, "rw");
		
		

	}
	
	
	private boolean checkAllNaN(double[] value){
		boolean flag =true;
		
		for(int i=0; i<value.length && flag;i++)
			flag = flag && Double.isNaN(value[i]); 
		
		return flag;
	}
	
	private boolean checkAllNaN(double[][] value){
		boolean flag =true;
		
		for(int i=0; i<value.length && flag;i++)
			for(int j=0; i<value[i].length && flag;j++)
				flag = flag && Double.isNaN(value[i][j]); 
		
		return flag;
	}

	private boolean checkAllNaN(float[] value){
		boolean flag =true;
		
		for(int i=0; i<value.length && flag;i++)
			flag = flag && Float.isNaN(value[i]); 
		
		return flag;
	}


	public ArrayCache(double[] value) throws IOException {
		this(ArrayCacheType.DOUBLE_VECTOR);
		isAllNaN = checkAllNaN(value);
		write(value);
		
		
	}
	
	public ArrayCache(String[] value) throws IOException {
		this(ArrayCacheType.STRING_VECTOR);
		
		write(value);
		
		
	}
	
	
	private void nanFilter(double value){		
				
		if(Double.isNaN(value))
			nanNumber++;
		
	}
	
	public ArrayCache(double[][] value) throws IOException {
		this(ArrayCacheType.DOUBLE_MATRIX);
		
			dimensions=new int[]{value[0].length};			 
			for(int i=0; i<value.length;i++)
				write(value[i]);
		
			isAllNaN = checkAllNaN(value);
		
		
	}
	
	public ArrayCache(float[] value) throws IOException {
		this(ArrayCacheType.DOUBLE_VECTOR);
		isAllNaN = checkAllNaN(value);
			writeAsDouble(value);
		
		
	}

	public ArrayCache(long[] value) throws IOException {
		this(ArrayCacheType.LONG_VECTOR);

		
			write(value);
		
		

	}

	public ArrayCache(int[] value) throws IOException {
		this(ArrayCacheType.LONG_VECTOR);

		
			writeAsLong(value);		
		

	}

	public    int getSize() {
		
		return size;
		
	}
	
	
	
	
	
	
	
	
	public   double getNextDouble() throws IOException {
		
		if (type != ArrayCacheType.DOUBLE_VECTOR && type != ArrayCacheType.DOUBLE_MATRIX)
			throw  new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		double x= readNextDouble();
		
		return x;
	}

	
	public   void write(double[] value) throws IOException  {
		lockToWrite();

		
		
		if (type != ArrayCacheType.DOUBLE_VECTOR && type != ArrayCacheType.DOUBLE_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		
		for (double e : value) 
			writeNextDouble(e);
		
		unlockToWrite();
	}
	
	public   void write(double value) throws IOException  {
		lockToWrite();
		
	
		
		if (type != ArrayCacheType.DOUBLE_VECTOR && type != ArrayCacheType.DOUBLE_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		writeNextDouble(value);
		unlockToWrite();
		
	}
	
	
	
	
	public   void writeNextLong(long value) throws IOException  {
		size++;
		isAllZero = isAllZero && value == 0;
		 for (int i = 7; i > 0; i--) {
		     byteBufer[i] = (byte) value;
			 value >>>= 8;
			 }
			 byteBufer[0] = (byte) value;
			 
		stream.write(byteBufer);
		
	}
	
	public   void writeNextDouble(double value) throws IOException  {
		size ++;
		nanFilter(value);
		isAllNaN = isAllNaN && Double.isNaN(value);
		isAllZero = isAllZero && value == 0.0;
		long longValue  = Double.doubleToRawLongBits(value); 
		for (int i = 7; i > 0; i--) {
		     byteBufer[i] = (byte) longValue;
		     longValue >>>= 8;
		 }
		 byteBufer[0] = (byte) longValue;
			 
		stream.write(byteBufer);
		
	}
	
	public long readNextLong() throws IOException{
		long value = 0;
		stream.read(byteBufer);
		
		 for(int i = 0; i < 8; i++) {
		       value <<= 8;
		       value ^= byteBufer[i] & 0xFF;
		   }
		

		return value;
	}

	public double readNextDouble() throws IOException{
		
		return Double.longBitsToDouble(readNextLong());
	}
	
	public   void write(String[] value) throws IOException  {
		lockToWrite();
		
		

		if (type != ArrayCacheType.STRING_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		size += value.length;
		byte[] splitSymbol=(new String("#-#")).getBytes();
		for (String e : value) {
			stream.write(e.getBytes());
			stream.write(splitSymbol);
		}
		unlockToWrite();
	}


//	public   void write(float[] value) throws IOException  {
//		lockToWrite();
//		
//		
//		
//		if (type != ArrayCacheType.FLOAT_VECTOR && type != ArrayCacheType.FLOAT_MATRIX)
//			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
//		size += value.length;
//		for (float e : value) {
//			nanFilter(e);
//			isAllZero = isAllZero && e == 0.0;
//			stream.writeFloat(e);
//		}
//		unlockToWrite();
//	}

	public   void writeAsDouble(float[] value) throws IOException  {
		lockToWrite();
		
		
		
		if (type != ArrayCacheType.DOUBLE_VECTOR && type != ArrayCacheType.DOUBLE_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
				
		for (int i = 0; i < value.length; i++) {
			
			double x = value[i];
			writeNextDouble(x);
			
		}
		unlockToWrite();
	}

//	public   void writeAsFloat(double[] value) throws IOException  {
//		lockToWrite();
//		
//		
//		
//		if (type != ArrayCacheType.FLOAT_VECTOR && type != ArrayCacheType.FLOAT_MATRIX)
//			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
//		size += value.length;
//		for (double e : value) {
//			nanFilter(e);
//			isAllZero = isAllZero && e == 0.0;
//			stream.writeFloat((float) e);
//		}
//		unlockToWrite();
//	}

//	public   void write(int[] value) throws IOException  {
//		lockToWrite();
//		
//		
//		
//		if (type != ArrayCacheType.INT_VECTOR && type != ArrayCacheType.INT_MATRIX)
//			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
//		size += value.length;
//		for (int e : value) {
//			isAllZero = isAllZero && e == 0;
//			stream.writeInt(e);
//		}
//		unlockToWrite();
//	}

	public   void writeAsLong(int[] value) throws IOException  {
		lockToWrite();
		
		
		
		if (type != ArrayCacheType.LONG_VECTOR && type != ArrayCacheType.LONG_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		
		for (int e : value) {
			writeNextLong((long) e);
		}
		unlockToWrite();
	}

	public   void write(long[] value) throws IOException {
		lockToWrite();
		
		
		
		if (type != ArrayCacheType.LONG_VECTOR && type != ArrayCacheType.LONG_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		
		for (long e : value) {
			
			writeNextLong(e);
		}
		unlockToWrite();
	}
	
	public   void writeNextLong(long[] value) throws IOException {
		
		
		for (long e : value) {
				writeNextLong(e);
		}
		
	}


	public   double[] getDoubleArray() throws IOException{
		lockToRead();
		
		
		
		if (type != ArrayCacheType.DOUBLE_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		
		double[] value;// = new double[size];
		if(isNaNFiltered)
			 value= new double[size-nanNumber];
		else
			value= new double[size];
		for (int i = 0, j=0; i < size; i++) {
			double x=readNextDouble();
			if(isNaNFiltered && Double.isNaN(x))
				continue;
			if(isNaN2Zero && Double.isNaN(x))
				x=0.0;
			value[j++] =  x;
		}
		unlockToRead();

		return value;
	}
	
	
//	public   double[] getFloatArrayAsDouble() throws IOException {
//		lockToRead();
//		
//		
//		
//		if (type != ArrayCacheType.FLOAT_VECTOR)
//			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
//		
//		double[] value ;
//		if(isNaNFiltered)
//			 value= new double[size-nanNumber];
//		else
//			value= new double[size];
//		for (int i = 0, j=0; i < size; i++) {
//		
//			float x = stream.readFloat();
//			
//			if(isNaNFiltered && Double.isNaN(x))
//				continue;
//			if(isNaN2Zero && Double.isNaN(x))
//				x=(float) 0.0;
//			value[j++] =  x;
//		}
//		
//		unlockToRead();
//		return value;
//	}
//
	
	public   float[] getDoubleAsFloatArray() throws IOException {
		lockToRead();

		
		
		
		if (type != ArrayCacheType.DOUBLE_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
	
		
		
		float[] value ;
		if(isNaNFiltered)
			 value= new float[size-nanNumber];
		else
			value= new float[size];
		for (int i = 0, j=0; i < size; i++){
			
			float x = (float) readNextDouble();
			
			if(isNaNFiltered && Double.isNaN(x))
				continue;
			if(isNaN2Zero && Double.isNaN(x) )
				x=(float) 0.0;
			value[j++] =  x;
			 
		}

	
		unlockToRead();
		return value;
	}


	public   long[] getDoubleAsLongArray() throws IOException {
		lockToRead();

		
		
		
		if (type != ArrayCacheType.DOUBLE_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
	
		
		
		long[] value ;
		if(isNaNFiltered)
			 value= new long[size-nanNumber];
		else
			value= new long[size];
		for (int i = 0, j=0; i < size; i++){
			
			long x = (long) readNextDouble();
			
			if(isNaNFiltered && Double.isNaN(x))
				continue;
			if(isNaN2Zero && Double.isNaN(x) )
				x=(long) 0.0;
			value[j++] =  x;
			 
		}

	
		unlockToRead();
		return value;
	}

	public   String[] getStringArray() throws IOException{
		lockToRead();
		if (type != ArrayCacheType.STRING_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		
		
		
		byte[] buffer = new byte[(int) stream.length()];
		stream.read(buffer);
		
		String[] value = (new String(buffer)).split("#-#");
		unlockToRead();
		return value;
	}


	public   double[][] getDoubleMatrix() throws IOException  {
		lockToRead();
		if ( type != ArrayCacheType.DOUBLE_MATRIX )
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		if (dimensions.length != 1) {
			throw new RuntimeException(MessageStrings.WRONG_RESULT_FORMAT);
		}

		double[][] value = new double[size / dimensions[0]][dimensions[0]];

		for (int i = 0; i < value.length; i++)
			for (int j = 0; j < value[0].length; j++){
				
				value[i][j] = readNextDouble();
			}


		unlockToRead();
		return value;
	}

	
	public   int[] getIntArray() throws IOException {
		lockToRead();
		if (type != ArrayCacheType.LONG_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));

		int[] value = new int[size];
		for (int i = 0; i < size; i++)
			value[i] = (int) readNextLong();

		
		unlockToRead();
		return value;
	}

	public   double[] getIntAsDoubleArray() throws IOException {
		lockToRead();
		if (type != ArrayCacheType.LONG_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		
		double[] value = new double[size];
		for (int i = 0; i < size; i++)
			value[i] = readNextLong();

		
		unlockToRead();
		return value;
	}

	public   long[] getLongArray(ArrayCache valuesCache) throws IOException {
		lockToRead();
		valuesCache.lockToRead();
		
		
		if (type != ArrayCacheType.LONG_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		if(valuesCache.type!=ArrayCacheType.DOUBLE_VECTOR ){
		
			long[] value = new long[size];
			for (int i = 0; i < size; i++)
				value[i] = readNextLong();
	
			
			unlockToRead();
			valuesCache.unlockToRead();
			return value;
		}
		
		
		long[] value;// = new double[size];
		if(valuesCache.isNaNFiltered)
			 value= new long[size-valuesCache.nanNumber];
		else
			value= new long[size];
		
		for (int i = 0, j=0; i < size; i++) {
			long t=readNextLong();
			double x = valuesCache.readNextDouble(); 
			if(valuesCache.isNaNFiltered && Double.isNaN(x))
				continue;
			
			value[j++] =  t;
		}
		
		
		
		
		valuesCache.unlockToRead();
		unlockToRead();
		return value;

		
		
	}
	
public   long[] getLongArray() throws IOException {
	lockToRead();
		if (type != ArrayCacheType.LONG_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		
		
			long[] value = new long[size];
			for (int i = 0; i < size; i++)
				value[i] = readNextLong();
	
			
			unlockToRead();
			return value;
		
		
				
	}


//	public   float[] getFloatArray() throws IOException{
//		lockToRead();
//		
//		if (type == ArrayCacheType.DOUBLE_VECTOR)
//			return getDoubleAsFloatArray();
//
//		if (type != ArrayCacheType.FLOAT_VECTOR)
//			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
//		
//		float[] value;
//		if(isNaNFiltered)
//			 value= new float[size-nanNumber];
//		else
//			value= new float[size];
//		for (int i = 0, j=0; i < size; i++) {
//			float x=stream.readFloat();
//			if(isNaNFiltered && Double.isNaN(x))
//				continue;
//			if(isNaN2Zero)
//				x=(float)0.0;
//			value[j++] =  x;
//		}
//		
//		
//		unlockToRead();
//		return value;
//	}
//
	public   static ArrayCache[] splitBatchDouble(ArrayCache value, int batchSize) throws IOException  {

		if (value.type != ArrayCacheType.DOUBLE_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  value.type));

		ArrayCache[] batchValue = new ArrayCache[batchSize];
		for (int i = 0; i < batchSize; i++)
			batchValue[i] = new ArrayCache(ArrayCacheType.DOUBLE_VECTOR);

		int len = 0;
		
		value.lockToRead();
		
		while (len < value.size) {
			for (int k = 0; k < batchSize; k++) {
				double x = value.readNextDouble();
				if(Double.isNaN(x))
					batchValue[k].nanNumber++;
				batchValue[k].isAllZero = batchValue[k].isAllZero && x == 0.0;
				batchValue[k].writeNextDouble(x);
				
				len++;

			}
		}

		value.unlockToRead();
		return batchValue;
	}

	public static ArrayCache[] splitBatchDouble(ArrayCache value) throws IOException {
		if (value.type != ArrayCacheType.DOUBLE_VECTOR && value.type != ArrayCacheType.DOUBLE_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  value.type));

		ArrayCache[] batchValue = new ArrayCache[value.dimensions.length];
		for (int i = 0; i < value.dimensions.length; i++) {
			if (value.dimensions[i] == 1)
				batchValue[i] = new ArrayCache(ArrayCacheType.DOUBLE_VECTOR);
			else
				batchValue[i] = new ArrayCache(ArrayCacheType.DOUBLE_MATRIX);

			batchValue[i].setDimensions(new int[] { value.dimensions[i] });
		}

		int len = 0;
		value.lockToRead();
		while (len < value.size) {
			for (int k = 0; k < value.dimensions.length; k++) {
				for (int m = 0; m < value.dimensions[k]; m++) {
					double x = value.readNextDouble();

					batchValue[k].isAllZero = batchValue[k].isAllZero && x == 0.0;
					if(Double.isNaN(x))
						batchValue[k].nanNumber++;
					batchValue[k].writeNextDouble(x);
					
					len++;
				}
			}
		}
		
		value.unlockToRead();
		return batchValue;
	}

	public static ArrayCache[] splitBatchDoubleToInt(ArrayCache value, int batchSize) throws IOException {

		if (value.type != ArrayCacheType.DOUBLE_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  value.type));

		ArrayCache[] batchValue = new ArrayCache[batchSize];
		for (int i = 0; i < batchSize; i++)
			batchValue[i] = new ArrayCache(ArrayCacheType.LONG_VECTOR);

		int len = 0;
		value.lockToRead();
		while (len < value.size) {
			for (int k = 0; k < batchSize; k++) {
				int x = (int) value.readNextDouble();
				batchValue[k].isAllZero = batchValue[k].isAllZero && x == 0.0;

				batchValue[k].writeNextLong(x);
				
				len++;

			}
		}

		value.unlockToRead();
		return batchValue;
	}
	
	public static ArrayCache[] splitBatchDoubleMatrixToInt(ArrayCache value) throws IOException {

		if (value.type != ArrayCacheType.DOUBLE_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  value.type));

		ArrayCache[] batchValue = new ArrayCache[value.dimensions[0]];
		for (int i = 0; i < value.dimensions[0]; i++)
			batchValue[i] = new ArrayCache(ArrayCacheType.LONG_VECTOR);

		int len = 0;
		value.lockToRead();
		while (len < value.size) {
			for (int k = 0; k < value.dimensions[0]; k++) {
				int x = (int) value.readNextDouble();
				batchValue[k].isAllZero = batchValue[k].isAllZero && x == 0.0;

				batchValue[k].writeNextLong(x);
				
				len++;

			}
		}

		value.unlockToRead();
		return batchValue;
	}
	public static ArrayCache[] splitBatchDoubleMatrixToInt(ArrayCache value, int n) throws IOException {

		
		if (value.type != ArrayCacheType.DOUBLE_MATRIX && value.type != ArrayCacheType.DOUBLE_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  value.type));

		ArrayCache[] batchValue = new ArrayCache[n];
		for (int i = 0; i < n; i++)
			batchValue[i] = new ArrayCache(ArrayCacheType.LONG_VECTOR);			
		

		int len = 0;
		value.lockToRead();
		while (len < value.size) {
			for (int k = 0; k < n; k++) {
				int x = (int) value.readNextDouble();
				batchValue[k].isAllZero = batchValue[k].isAllZero && x == 0.0;

				batchValue[k].writeNextLong(x);
				
				len++;

			}
		}
		
		value.unlockToRead();
		

		
		return batchValue;
	}




	public static ArrayCache copyArrayCacheLong(ArrayCache value) throws IOException {

		ArrayCache batchValue = new ArrayCache(value.type);

		value.lockToRead();
		for (int k = 0; k < value.size; k++) {
			long x = value.readNextLong();

			batchValue.isAllZero = batchValue.isAllZero && x == 0.0;
			batchValue.writeNextLong(x);
			

		}

		value.unlockToRead();
		return batchValue;
	}

	@Override
	protected void finalize() throws Throwable {
		if(stream != null)
			stream.close();
		file.delete();
		file = null;

		super.finalize();
	}
	
	
	public void delete() throws Throwable {
		if(stream != null)
			stream.close();
		file.delete();
		file = null;
		
	}

	public int[] getDimensions() {
		
		return dimensions;
	}

	public void setDimensions(int[] dimensions) {
		if (dimensions != null)
			this.dimensions = dimensions;
		
		if(dimensions.length == 1 && dimensions[0]>1)
			type =ArrayCacheType.DOUBLE_MATRIX;
			
	}

	public   boolean isAllZero() {
		return isAllZero;
	}
	
	public   boolean isNaNFiltered() {
		return isNaNFiltered;
	}

	public   void setNaNFiltered(boolean isNaNFiltered) {
		
		this.isNaNFiltered = isNaNFiltered;
	}

	public   boolean isNaN2Zero() {
		return isNaN2Zero;
	}

	public   void setNaN2Zero(boolean isNaN2Zero) {
		this.isNaN2Zero = isNaN2Zero;
	}



	public   ArrayCacheType getType() {
		return type;
	}
	
	public   int getNanNumber() {
		return nanNumber;
	}

	
	public void lockToRead() throws IOException{
		lock.lock();
		if(stream == null)
			stream = new RandomAccessFile(file, "rw");
		stream.seek(0);
	}
	
	public void unlockToRead() throws IOException{
		if(stream != null)
			stream.close();
		stream = null;
		lock.unlock();
	}
	
	public void lockToWrite() throws IOException{
		lock.lock();
		if(stream == null)
			stream = new RandomAccessFile(file, "rw");
		stream.seek(stream.length());
		
	}
	
	public void unlockToWrite() throws IOException{
		if(stream != null)
			stream.close();
		stream = null;
		lock.unlock();
		
	}
	
	public boolean isAllNaN() {
		if(type==ArrayCacheType.DOUBLE_VECTOR)
			return isAllNaN;
		if(type==ArrayCacheType.DOUBLE_MATRIX)
			return isAllNaN;
		
		return false;
	}


}
