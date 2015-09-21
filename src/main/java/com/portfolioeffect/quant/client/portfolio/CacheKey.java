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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CacheKey {

	private final Map<String, String> keyMap;

	public Map<String, String> getKeyMap() {
		return keyMap;
	}

	public CacheKey(String metric, String params ) {

		Gson gson = new Gson();

		Type mapType = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();
		ArrayList<HashMap<String, String>> listMetric = gson.fromJson(metric, mapType);
		ArrayList<HashMap<String, String>> listParams;
		if(params.length()!=0) {
			listParams	= gson.fromJson(params, mapType);
		}
		else {
			listParams = new ArrayList<HashMap<String,String>>();
		}
				
		keyMap = listMetric.get(0);
		
		
		for(HashMap<String,String> e: listParams){
			
			keyMap.putAll(e);
			
			
		}
		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (obj instanceof CacheKey)
			return this.keyMap.equals(((CacheKey) obj).keyMap);
		else
			return false;
	}

	@Override
	public int hashCode() {
		return keyMap.hashCode();
	}

}
