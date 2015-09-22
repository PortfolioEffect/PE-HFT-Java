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

import java.util.ArrayList;

public class TransmitDataListMessage {
	
	private final ArrayList<String> dataList;
	private final String windowLength;
	private final String fromTime;
	private final String toTime;
	private final String priceSamplingInterval;
	
	public TransmitDataListMessage(ArrayList<String> dataList, String windowLength, String fromTime, String toTime, String priceSamplingInterval) {
		this.dataList = dataList;
		this.windowLength = windowLength;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.priceSamplingInterval =  priceSamplingInterval;
	}
	
	public ArrayList<String> getDataList() {
		return dataList;
	}

	public String getWindowLength() {
		return windowLength;
	}

	public String getFromTime() {
		return fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public String getPriceSamplingInterval() {
		return priceSamplingInterval;
	}

	

}
