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
