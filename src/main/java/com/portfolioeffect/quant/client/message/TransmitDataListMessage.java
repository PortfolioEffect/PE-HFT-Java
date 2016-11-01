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
package com.portfolioeffect.quant.client.message;

import java.util.ArrayList;

public class TransmitDataListMessage {
	
	private final ArrayList<String> dataList;
	private final String windowLength;
	private final String fromTime;
	private final String toTime;
	private final String priceSamplingInterval;
	private final String riskMethodology;
	private final String trainingPeriodEnabled;
	
	
	public TransmitDataListMessage(ArrayList<String> dataList, String windowLength, String fromTime, String toTime, String priceSamplingInterval, String riskMethodology, String trainingPeriodEnabled) {
		this.dataList = dataList;
		this.windowLength = windowLength;
		this.fromTime = fromTime;
		this.toTime = toTime;
		this.priceSamplingInterval =  priceSamplingInterval;
		this.riskMethodology = riskMethodology;
		this.trainingPeriodEnabled = trainingPeriodEnabled;
	}
	
	public String getRiskMethodology() {
		return riskMethodology;
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

	public String getTrainingPeriodEnabled() {
		return trainingPeriodEnabled;
	}
	

}
