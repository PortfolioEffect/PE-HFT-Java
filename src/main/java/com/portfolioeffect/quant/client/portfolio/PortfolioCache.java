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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PortfolioCache {
	
	private  Map<CacheKey, ArrayCache> fileMapPrice = new HashMap<CacheKey, ArrayCache>();
	private  Map<CacheKey, ArrayCache> fileMapTimeMillis = new HashMap<CacheKey, ArrayCache>();

	
	
	
	public  void addMetric(CacheKey keyMap, double[] value) throws Exception {

		try {
			
			
		
		
			fileMapPrice.put(keyMap, new ArrayCache(value));

		} catch (IOException e) {

		}

	}

	public  void addMetric(CacheKey keyMap, ArrayCache value) {
		
		fileMapPrice.put(keyMap, value);

	}

	public  void addTime(CacheKey keyMap, long[] value) throws Exception {



		fileMapTimeMillis.put(keyMap, new ArrayCache(value));

	}

	public  void addTime(CacheKey keyMap, ArrayCache value)
			throws IOException {

		
		fileMapTimeMillis.put(keyMap, value);

	}

	public  ArrayCache getMetric(CacheKey keyMap) {
		
		return fileMapPrice.get(keyMap);

	}

	public  ArrayCache getTime(CacheKey keyMap) {
		
		

		return fileMapTimeMillis.get(keyMap);

	}

	public  void remove(CacheKey keyMap) throws IOException {


		
		fileMapPrice.remove(keyMap);

		
		fileMapTimeMillis.remove(keyMap);
		
	}

	public  boolean containsKey(CacheKey keyMap) {

		
		return fileMapPrice.containsKey(keyMap);

	}

};
