/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2015 - 2016 Snowfall Systems, Inc.
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
package com.portfolioeffect.quant.client.util;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;

import java.util.HashMap;

public class Util {

	
		public static void putIfAbsent(HashMap<String,String> map, String key, String value){
			if(!map.containsKey(key))
				map.put(key, value);			
			
		}
		
		
		public static TLongArrayList toTLongArrayList(TIntArrayList h, TIntArrayList millisec){
			
			
			long[] v= new long[h.size()];
			
			for(int i=0; i<h.size();i++)
				v[i] = h.getQuick(i)*3600000L + millisec.getQuick(i);
			
			return new TLongArrayList(v);
			
			
			
		}
		
		public static TLongArrayList toTLongArrayList(int[] h, int[] millisec){
			
			
			long[] v= new long[h.length];
			
			for(int i=0; i<h.length;i++)
				v[i] = h[i]*3600000L + millisec[i];
			
			return new TLongArrayList(v);
			
			
			
		}
		
		
		public static TIntArrayList getHours(TLongArrayList t){
				TIntArrayList hour= new TIntArrayList(t.size());
			
			for(int i=0; i<t.size();i++)
				hour.add( (int) (t.getQuick(i)/3600000L) );
			
			return hour;
		}
		
		
		public static TIntArrayList getMillisecFromHourStart(TLongArrayList t){
			TIntArrayList hour= new TIntArrayList(t.size());
		
		for(int i=0; i<t.size();i++)
			hour.add( (int) (t.getQuick(i)%3600000L) );
		
		return hour;
	}

}


