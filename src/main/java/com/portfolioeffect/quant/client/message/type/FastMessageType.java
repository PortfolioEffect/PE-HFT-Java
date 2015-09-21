
package com.portfolioeffect.quant.client.message.type;
/*
 * #%L
 * Ice-9 Platform Java API
 * %%
 * Copyright (C) 2010 - 2014 Snowfall Systems, Inc.
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

import java.util.HashMap;
import java.util.Map;

public enum FastMessageType {
	
	LOGON ("A"),
	LOGOUT("5"),
	HEARTBEAT("0"),
	TEST_REQUEST("1"),
	REJECT("3"),
	
	REMOTE_REQUEST("U12"),
	REMOTE_RESPONSE("U13"),
	
	
	GET_REMAINING_TRAFFIC_REQUEST("U14"),
	GET_REMAINING_TRAFFIC_RESPONSE("U15"),
	NON_PARAM_METRIC_REQUEST("U16"),
	NON_PARAM_METRIC_RESPONSE("U17"),
	VALIDATE_METRIC_TYPE_REQUEST("U18"),
	VALIDATE_METRIC_TYPE_RESPONSE("U19"),
	PORTFOLIO_ESTIMATION_REQUEST("U20"),
	PORTFOLIO_ESTIMATION_RESPONSE("U21"),
	PORTFOLIO_OPTIMIZATION_REQUEST("U22"),
	PORTFOLIO_OPTIMIZATION_RESPONSE("U23"),
	LOAD_DATA_REQUEST("U24"),
	LOAD_DATA_RESPONSE("U25"),
	TRANSMIT_DATA_REQUEST("U26"),
	TRANSMIT_DATA_RESPONSE("U27"),
	TRANSACTIONAL_PORTFOLIO_ESTIMATION_REQUEST("U28");
	
	
	private String code; 
	
	private FastMessageType (String code) {
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	private static Map<String, FastMessageType> cache;
    
	static {
        cache = new HashMap<String, FastMessageType>();
        for(FastMessageType tag : values()) {
            cache.put(tag.getCode(), tag);
        }
    }
   
    public static FastMessageType getFastMessageType(String code) {
        return cache.get(code);
    }
	
}
