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
package com.portfolioeffect.quant.message.util.msg.test;

public class SampleClientRequest {

	private final String requestType;
	private final String request;

	private final int[] price;
	private final int[] time;
	private final float[] result;
	

	public SampleClientRequest(String requestType, String request, int[] price, int time[], float[] result) {

		this.requestType = requestType;
		this.request = request;
		this.price = price;
		this.time = time;
		this.result = result;
	}

	public String getRequestType() {
		return requestType;
	}

	public String getRequest() {
		return request;
	}

	public int[] getPrice() {
		return price;
	}

	public int[] getTime() {
		return time;
	}

	public float[] getResult() {
		return result;
	}

}
