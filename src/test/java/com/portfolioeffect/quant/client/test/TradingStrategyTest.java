/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
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
package com.portfolioeffect.quant.client.test;

import static com.portfolioeffect.quant.client.portfolio.PortfolioUtil.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.model.TimeValue;
import com.portfolioeffect.quant.client.portfolio.Portfolio;

public class TradingStrategyTest {
	protected Logger logger = Logger.getLogger(this.getClass());
	
	private String userName;
	private String password;
	private String APIKey;

	@Before
	public void setCredentials() throws IOException {

		String resourceName = "credentials.properties";
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties props = new Properties();
		InputStream resourceStream = loader.getResourceAsStream(resourceName);
		props.load(resourceStream);

		userName = props.getProperty("userName");
		password = props.getProperty("password");
		APIKey = props.getProperty("apiKey");

	}


	@Test
	public void simpleBacktest() throws ComputeErrorException {

		util_setCredentials(userName, password, APIKey);

		logger.info("Compute time left: " + util_getComputeTime("timeLeft"));

		Portfolio portfolio = portfolio_create("t-1", "t-1", "SPY");
		
		portfolio_addPosition(portfolio, "GOOG", 200);
		portfolio_settings(portfolio,"portfolioMetricsMode",  "price");
		portfolio_settings(portfolio,"resultsSamplingInterval", "10m");
		portfolio_settings(portfolio, "windowLength", "10m");
		
		TimeValue resultPosition = position_expectedReturn(portfolio, "GOOG");
		double[] expReturn = resultPosition.getValue();
		long[] time = resultPosition.getTime();
		int[] strategyQuantity = new int[expReturn.length];

		// naive strategy
		for (int i = 0; i < expReturn.length; i++) {
			if (expReturn[i] > 0) {
				strategyQuantity[i] = 200; //long
			}
			else {
				strategyQuantity[i] = 0; // none
			}
		}
		
		Portfolio portfolioStrategy = portfolio_create("t-1", "t-1", "SPY");
		portfolio_addPosition(portfolioStrategy, "GOOG", strategyQuantity, time);

		TimeValue resultStrategyVaR = portfolio_VaR(portfolioStrategy, 0.95);
		TimeValue resultStrategyProfit = portfolio_profit(portfolioStrategy);

		logger.info("Naive Strategy, window =  10m " +
				"\t VaR(0.95)=" + resultStrategyVaR.getValueLast() + 
				"\t profit=" + resultStrategyProfit.getValueLast());

		// -------------------------------------------------------------------------------
        // Change rolling window length to 20 minutes
		
		portfolio_settings(portfolio, "windowLength", "20m");
		
		resultPosition = position_expectedReturn(portfolio, "GOOG");
		expReturn = resultPosition.getValue();
		time = resultPosition.getTime();
		strategyQuantity = new int[expReturn.length];

		// naive strategy
		for (int i = 0; i < resultPosition.getValue().length; i++)
			if (expReturn[i] > 0)
				strategyQuantity[i] = 200;// long
			else
				strategyQuantity[i] = 0; // sell

		portfolioStrategy = portfolio_create("t-1", "t-1", "SPY");
		portfolio_addPosition(portfolioStrategy, "GOOG", strategyQuantity, time);

		resultStrategyVaR = portfolio_VaR(portfolioStrategy, 0.95);
		resultStrategyProfit = portfolio_profit(portfolioStrategy);

		logger.info("Naive Strategy, window =  20m" + 
				"\t VaR(0.95)=" + resultStrategyVaR.getValueLast() + 
				"\t profit=" + resultStrategyProfit.getValueLast());

	}

}
