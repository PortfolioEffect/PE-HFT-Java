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
package com.portfolioeffect.quant.client.model;

import com.portfolioeffect.quant.client.util.MessageStrings;

public class ConnectFailedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2787244297983174640L;

	public  ConnectFailedException(){
		super(MessageStrings.SERVER_TIME_OUT_NO_CONNECT);		
		
	}
	
	public  ConnectFailedException(String msg){
		super(msg);		
		
	}

	

}
