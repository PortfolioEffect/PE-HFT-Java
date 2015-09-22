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
import java.util.HashMap;

import com.google.gson.Gson;


public class CalculationStatusMessage {
	
	private final String status;
	private final int[] dimension;
	private final HashMap<String,String> resultInfo;
	
	public CalculationStatusMessage(String status, int[] dimension, HashMap<String, String> resultInfo) {
		this.status = status;
		this.dimension = dimension;
		this.resultInfo = resultInfo;
	}
	
	
	public String getStatus() {
		return status;
	}
	public  int[] getDimension() {
		return dimension;
	}
	public HashMap<String, String> getResultInfo() {
		return resultInfo;
	}
	
	public String toJSON(){
		 Gson gson = new Gson();
		 
		 return gson.toJson(this);
		
	}

}
