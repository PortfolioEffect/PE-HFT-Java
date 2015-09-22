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
