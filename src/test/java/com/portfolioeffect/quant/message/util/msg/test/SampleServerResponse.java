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
package com.portfolioeffect.quant.message.util.msg.test;

public class SampleServerResponse {

	private final String responseType;
	public String getResponseType() {
		return responseType;
	}

	public String getResponse() {
		return response;
	}

	public double[][] getData() {
		return data;
	}

	private final String response;

	private final double[][] data;
	
	public SampleServerResponse(String responseType) {
		this(responseType, null);

	}

	public SampleServerResponse(String responseType, String response) {
		this(responseType, response, null);

	}

	public SampleServerResponse(String responseType, String response,
			double[][] data) {

		this.responseType = responseType;
		this.response = response;
		this.data = data;
		
	}

	
}
