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
package com.portfolioeffect.quant.client.portfolio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.portfolioeffect.quant.client.util.Console;
import com.portfolioeffect.quant.client.util.MessageStrings;

public class ArrayCache {

	private File file;
	private int size;
	private ObjectInputStream inStream = null;
	private ObjectOutputStream outStream = null;
	private int[] dimensions = new int[] { 1 };
	private boolean isAllZero = true;
	private ArrayCacheType type;

	public ArrayCacheType getType() {
		return type;
	}

	private boolean inUse = false;

	public ArrayCache(ArrayCacheType type) throws IOException {
		inUse = false;
		this.type = type;
		this.size = 0;

		file = File.createTempFile("quant", "tmp");
		file.deleteOnExit();

		FileOutputStream out = new FileOutputStream(file);

		outStream = new ObjectOutputStream(new BufferedOutputStream(out));

	}

	public ArrayCache(double[] value) throws IOException {
		this(ArrayCacheType.DOUBLE_VECTOR);
		
		write(value);
		
		closeOut();
	}
	
	public ArrayCache(double[][] value) throws IOException {
		this(ArrayCacheType.DOUBLE_MATRIX);
		
			dimensions=new int[]{value[0].length};			 
			for(int i=0; i<value.length;i++)
				write(value[i]);
		
		
		closeOut();
	}
	
	public ArrayCache(float[] value) throws IOException {
		this(ArrayCacheType.DOUBLE_VECTOR);
		
			writeAsDouble(value);
		
		closeOut();
	}

	public ArrayCache(long[] value) throws IOException {
		this(ArrayCacheType.LONG_VECTOR);

		
			write(value);
		
		closeOut();

	}

	public ArrayCache(int[] value) throws IOException {
		this(ArrayCacheType.INT_VECTOR);

		
			write(value);
		
		closeOut();

	}

	public int getSize() {
		return size;
	}

	public void closeOut() throws IOException {
		if (outStream != null) {
			outStream.flush();
			outStream.close();
			outStream = null;
		}
	}

	public void openInput() throws IOException {

		if (outStream != null) {
			closeOut();
		}

		if (inUse) {
			closeInput();
			openInput();
			return;
		}

		inUse = true;

		try {
			FileInputStream in = new FileInputStream(file);

			inStream = new ObjectInputStream(new BufferedInputStream(in));
		} catch (Exception e) {

			Console.writeStackTrace(e);
		}

	}

	public double getNextDouble() throws IOException {
		if (type != ArrayCacheType.DOUBLE_VECTOR && type != ArrayCacheType.DOUBLE_MATRIX)
			throw  new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		return inStream.readDouble();
	}

	public void closeInput() throws IOException {

		if (inUse) {
			if (inStream != null)
				inStream.close();
			inStream = null;

		}

		inUse = false;

	}

	public void write(double[] value) throws IOException  {

		if (type != ArrayCacheType.DOUBLE_VECTOR && type != ArrayCacheType.DOUBLE_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		size += value.length;
		for (double e : value) {
			isAllZero = isAllZero && e == 0.0;
			outStream.writeDouble(e);
		}
	}

	public void write(float[] value) throws IOException  {
		if (type != ArrayCacheType.FLOAT_VECTOR && type != ArrayCacheType.FLOAT_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		size += value.length;
		for (float e : value) {
			isAllZero = isAllZero && e == 0.0;
			outStream.writeFloat(e);
		}
	}

	public void writeAsDouble(float[] value) throws IOException  {
		if (type != ArrayCacheType.DOUBLE_VECTOR && type != ArrayCacheType.DOUBLE_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		size += value.length;
		
		for (int i = 0; i < value.length; i++) {
			double x = value[i];
			isAllZero = isAllZero && x == 0.0;
			outStream.writeDouble(x);
		}
	}

	public void writeAsFloat(double[] value) throws IOException  {
		if (type != ArrayCacheType.FLOAT_VECTOR && type != ArrayCacheType.FLOAT_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		size += value.length;
		for (double e : value) {
			isAllZero = isAllZero && e == 0.0;
			outStream.writeFloat((float) e);
		}
	}

	public void write(int[] value) throws IOException  {
		if (type != ArrayCacheType.INT_VECTOR && type != ArrayCacheType.INT_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		size += value.length;
		for (int e : value) {
			isAllZero = isAllZero && e == 0;
			outStream.writeInt(e);
		}
	}

	public void writeAsLong(int[] value) throws IOException  {
		if (type != ArrayCacheType.LONG_VECTOR && type != ArrayCacheType.LONG_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		size += value.length;
		for (int e : value) {
			isAllZero = isAllZero && e == 0;
			outStream.writeLong((long) e);
		}
	}

	public void write(long[] value) throws IOException {
		if (type != ArrayCacheType.LONG_VECTOR && type != ArrayCacheType.LONG_MATRIX)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		size += value.length;
		for (long e : value) {
			isAllZero = isAllZero && e == 0;
			outStream.writeLong(e);
		}
	}

	public double[] getDoubleArray() throws IOException{
		closeOut();
		openInput();
		if (type != ArrayCacheType.DOUBLE_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		
		double[] value = new double[size];
		for (int i = 0; i < size; i++) {
		
			value[i] = inStream.readDouble();
		}
		closeInput();

		return value;
	}

	public double[][] getDoubleMatrix() throws IOException  {
		closeOut();
		openInput();
		
		
		if ( type != ArrayCacheType.DOUBLE_MATRIX )
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		if (dimensions.length != 1) {
			throw new RuntimeException(MessageStrings.WRONG_RESULT_FORMAT);
		}

		double[][] value = new double[size / dimensions[0]][dimensions[0]];

		for (int i = 0; i < value.length; i++)
			for (int j = 0; j < value[0].length; j++)
				value[i][j] = inStream.readDouble();

		closeInput();

		return value;
	}

	public double[] getFloatArrayAsDouble() throws IOException {
		if (type != ArrayCacheType.FLOAT_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		closeOut();
		openInput();
		
		double[] value = new double[size];
		for (int i = 0; i < size; i++) {
		
			value[i] = inStream.readFloat();
		}
		closeInput();

		return value;
	}

	public int[] getIntArray() throws IOException {
		if (type != ArrayCacheType.INT_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		closeOut();
		openInput();

		int[] value = new int[size];
		for (int i = 0; i < size; i++)
			value[i] = inStream.readInt();

		closeInput();

		return value;
	}

	public double[] getIntAsDoubleArray() throws IOException {

		if (type != ArrayCacheType.INT_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		closeOut();
		openInput();

		double[] value = new double[size];
		for (int i = 0; i < size; i++)
			value[i] = inStream.readInt();

		closeInput();

		return value;
	}

	public float[] getDoubleAsFloatArray() throws IOException {

		if (type != ArrayCacheType.DOUBLE_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		closeOut();
		openInput();

		float[] value = new float[size];
		for (int i = 0; i < size; i++)
			value[i] = (float) inStream.readDouble();

		closeInput();

		return value;
	}

	public long[] getLongArray() throws IOException {

		if (type != ArrayCacheType.LONG_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		closeOut();
		openInput();

		long[] value = new long[size];
		for (int i = 0; i < size; i++)
			value[i] = inStream.readLong();

		closeInput();

		return value;
	}

	public float[] getFloatArray() throws IOException{

		if (type == ArrayCacheType.DOUBLE_VECTOR)
			return getDoubleAsFloatArray();

		if (type != ArrayCacheType.FLOAT_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  type));
		closeOut();
		openInput();

		float[] value = new float[size];
		for (int i = 0; i < size; i++)
			value[i] = inStream.readFloat();

		closeInput();

		return value;
	}

	public static ArrayCache[] splitBatchDouble(ArrayCache value, int batchSize) throws IOException  {

		if (value.type != ArrayCacheType.DOUBLE_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  value.type));

		ArrayCache[] batchValue = new ArrayCache[batchSize];
		for (int i = 0; i < batchSize; i++)
			batchValue[i] = new ArrayCache(ArrayCacheType.DOUBLE_VECTOR);

		int len = 0;
		value.openInput();
		while (len < value.size) {
			for (int k = 0; k < batchSize; k++) {
				double x = value.inStream.readDouble();
				batchValue[k].isAllZero = batchValue[k].isAllZero && x == 0.0;
				batchValue[k].outStream.writeDouble(x);
				batchValue[k].size++;
				len++;

			}
		}

		for (int k = 0; k < batchSize; k++) {
			batchValue[k].closeOut();
		}
		value.closeInput();

		return batchValue;
	}

	public static ArrayCache[] splitBatchDouble(ArrayCache value) throws IOException {
		if (value.type != ArrayCacheType.DOUBLE_VECTOR)
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
		value.openInput();
		while (len < value.size) {
			for (int k = 0; k < value.dimensions.length; k++) {
				for (int m = 0; m < value.dimensions[k]; m++) {
					double x = value.inStream.readDouble();

					batchValue[k].isAllZero = batchValue[k].isAllZero && x == 0.0;
					batchValue[k].outStream.writeDouble(x);
					batchValue[k].size++;
					len++;
				}
			}
		}

		for (int k = 0; k < value.dimensions.length; k++) {
			batchValue[k].closeOut();
		}
		value.closeInput();

		return batchValue;
	}

	public static ArrayCache[] splitBatchDoubleToInt(ArrayCache value, int batchSize) throws IOException {

		if (value.type != ArrayCacheType.DOUBLE_VECTOR)
			throw new RuntimeException(String.format(MessageStrings.WRONG_DATA_TYPE,  value.type));

		ArrayCache[] batchValue = new ArrayCache[batchSize];
		for (int i = 0; i < batchSize; i++)
			batchValue[i] = new ArrayCache(ArrayCacheType.INT_VECTOR);

		int len = 0;
		value.openInput();
		while (len < value.size) {
			for (int k = 0; k < batchSize; k++) {
				int x = (int) value.inStream.readDouble();
				batchValue[k].isAllZero = batchValue[k].isAllZero && x == 0.0;

				batchValue[k].outStream.writeInt(x);
				batchValue[k].size++;
				len++;

			}
		}

		for (int k = 0; k < batchSize; k++) {
			batchValue[k].closeOut();
		}
		value.closeInput();

		return batchValue;
	}

	public static ArrayCache copyArrayCacheLong(ArrayCache value) throws IOException {

		ArrayCache batchValue = new ArrayCache(value.type);

		value.openInput();
		for (int k = 0; k < value.size; k++) {
			long x = value.inStream.readLong();

			batchValue.isAllZero = batchValue.isAllZero && x == 0.0;
			batchValue.outStream.writeLong(x);
			batchValue.size++;

		}

		batchValue.closeOut();
		value.closeInput();

		return batchValue;
	}

	@Override
	protected void finalize() throws Throwable {
		if (outStream != null)
			outStream.close();

		if (inStream != null)
			inStream.close();
		outStream = null;
		inStream = null;

		file.delete();

		file = null;

		super.finalize();
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

	public boolean isAllZero() {
		return isAllZero;
	}

}
