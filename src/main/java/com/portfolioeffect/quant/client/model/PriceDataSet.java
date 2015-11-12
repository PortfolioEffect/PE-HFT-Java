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
package com.portfolioeffect.quant.client.model;

import gnu.trove.list.array.TIntArrayList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.portfolioeffect.quant.client.util.MessageStrings;



public class PriceDataSet {

	/**
	 * price in cents
	 */
	
	private final byte[] timeSec;
	private final byte[] price;

	private final int[] priceValueMissing;
	private final int[] priceIndexMissing;

	private final int[] timeSecValueMissing;
	private final int[] timeSecIndexMissing;

	private int iniPrice;
	private int iniTimeSec;
	private final int numberTimeSteps;

	public double[] getPrice() {

		double[] realPrice = new double[price.length];

		long priceValue = iniPrice;

		int missingCount = 0;
		for (int i = 0; i < price.length; i++) {

			if (priceIndexMissing[missingCount] == i)
				priceValue = priceValueMissing[missingCount++];

			priceValue += price[i];
			
			double value= priceValue;
			realPrice[i] =  value * 0.01;

		}

		return realPrice;

	}

	public int[] getTimeSec() {
		int[] realTimeSec = new int[timeSec.length];

		int timeSecValue = iniTimeSec;
		int missingCount = 0;
		for (int i = 0; i < timeSec.length; i++) {
			if (timeSecIndexMissing[missingCount] == i)
				timeSecValue = timeSecValueMissing[missingCount++];
			timeSecValue += timeSec[i];
			realTimeSec[i] = timeSecValue;

		}

		return realTimeSec;

	}

	public int[] getTimeNanoSec() {
		int[] realTimeNanoSec = new int[timeSec.length];

		return realTimeNanoSec;
	}

	public PriceDataSet(double[] price, int[] timeSeconds) throws Exception {

		numberTimeSteps = price.length;

		TIntArrayList priceValueMissing = new TIntArrayList() ;
		TIntArrayList priceIndexMissing = new TIntArrayList();

		TIntArrayList timeSecValueMissing = new TIntArrayList();
		TIntArrayList timeSecIndexMissing = new TIntArrayList();

		int[] frequensy = new int[128];

		int timeSec[];

		if (timeSeconds.length != price.length && timeSeconds.length != 0) {
			throw new Exception(MessageStrings.WRONG_VECTOR_LEN_PRICE_AND_TIME);

		}
		if (timeSeconds.length == 0) {
			timeSec = new int[price.length];
			for (int i = 0; i < price.length; i++)
				timeSec[i] = i + 1;
		} else {
			timeSec = timeSeconds;
		}

		this.price = new byte[price.length];
		this.timeSec = new byte[timeSec.length];

		this.price[0] = 0;
		iniPrice = (int) (price[0] * 100);
		for (int i = 1; i < price.length; i++) {

			int diff = (int) (price[i] * 100) - (int) (price[i - 1] * 100);

			if (Math.abs(diff) <= 127) {
				this.price[i] = (byte) (diff);
				for (int j = 0; j <= Math.abs(diff); j++)
					frequensy[j]++;
			} else {
				this.price[i] = 0;
				priceValueMissing.add((int) (price[i] * 100));
				priceIndexMissing.add(i);
			}
		}

		this.timeSec[0] = 0;
		iniTimeSec = timeSec[0];
		for (int i = 1; i < timeSec.length; i++) {
			int diff = timeSec[i] - timeSec[i - 1];
			if (diff <= 0) {
				throw new Exception(String.format(MessageStrings.WRONG_TIME_DATA, ""+ i));
			}
			if (Math.abs(diff) <= 127) {
				this.timeSec[i] = (byte) (diff);
			} else {
				this.timeSec[i] = 0;
				timeSecValueMissing.add(timeSec[i]);
				timeSecIndexMissing.add(i);
			}
		}

		priceValueMissing.add(-1);
		priceIndexMissing.add(-1);
		timeSecValueMissing.add(-1);
		timeSecIndexMissing.add(-1);

		this.priceValueMissing = priceValueMissing.toArray();
		this.priceIndexMissing = priceIndexMissing.toArray();
		this.timeSecValueMissing = timeSecValueMissing.toArray();
		this.timeSecIndexMissing = timeSecIndexMissing.toArray();

	}

	public int getNumberTimeSteps() {
		return numberTimeSteps;
	}

	public byte[] toBinaryZipCompress() {
		byte[] yourBytes = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			GZIPOutputStream gzos = new GZIPOutputStream(bos) {
				{
					this.def.setLevel(Deflater.BEST_COMPRESSION);
				}
			};

			out = new ObjectOutputStream(gzos);
			out.writeObject(this);
			out.flush();
			gzos.finish();
			yourBytes = bos.toByteArray();
			out.close();
			bos.close();
			return yourBytes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static PriceDataSet fromBinaryZip(byte[] yourBytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
			ObjectInput in = new ObjectInputStream(new GZIPInputStream(bis));
			Object result = in.readObject();
			bis.close();
			in.close();
			return (PriceDataSet) result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
