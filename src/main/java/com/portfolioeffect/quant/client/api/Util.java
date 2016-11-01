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
package com.portfolioeffect.quant.client.api;

import com.portfolioeffect.quant.client.ClientConnection;
import com.portfolioeffect.quant.client.model.ComputeErrorException;


/**
 * Utility Methods
 * 
 * @author oleg
 * 
 * 
 *
 */
public class Util {

	private static ClientConnection clientConnection;
	
	private static String userNameS;
	private static String passwordS;
	private static String apiKeyS;
	private static String hostS;
	
	public static  void util_setCredentials(String userName, String password, String apiKey, String host){
		
		userNameS = userName;
		passwordS = password;
		apiKeyS = apiKey;
		hostS = host;
		clientConnection =null;
		
	}
	
	
	public static  void util_setCredentials(String userName, String password, String apiKey){
		userNameS = userName;
		passwordS = password;
		apiKeyS = apiKey;
		hostS = "quant07.portfolioeffect.com";
		
		clientConnection =null;
		
	}
	
	
	
	static ClientConnection  getClientConnection() throws ComputeErrorException{
		
		if(clientConnection!=null)
			return clientConnection;
		
		if(hostS==null)
			throw new ComputeErrorException("User credentials is not set");
		
		clientConnection = new ClientConnection();
		
		clientConnection.setUsername(userNameS);
		clientConnection.setPassword(passwordS);
		clientConnection.setApiKey(apiKeyS);
		clientConnection.setHost(hostS);
		
		return clientConnection;
	}
	
	public static void resetClientConnection(){
		clientConnection = null;
	}
	
	/**
	 * 
	 * @param time
	 *            = "timeMax" or "timeLeft"
	 * @return
	 * @throws ComputeErrorException
	 */
	public static int util_getComputeTime(String time) throws ComputeErrorException {
		
		getClientConnection();

		com.portfolioeffect.quant.client.result.Metric result =  clientConnection.getComputeTimeLeft();

		if (result.hasError()) {
			throw new ComputeErrorException("Error: " + result.getErrorMessage());
		}


		if (time.equals("timeMax"))
			return result.getValueInt("timeMax");

		return result.getValueInt("timeLeft");

	}
	
	public static void checkResult(Metric result) throws ComputeErrorException {

		if (result.hasError()) {
			throw new ComputeErrorException("Error: " + result.getError());

		}

	}
	
	
	

}
