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

import org.apache.log4j.Logger;
import org.junit.Test;

import com.portfolioeffect.quant.client.model.TimeValue;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.portfolio.optimizer.PortfolioOptimizer;

public class PortfolioOptimizationTest {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private final String userName = "oleg4test";
	private final String password = "oleg4testabirval";
	private final String APIKey = "08ed15919960bed1085c825da1a670fc";

	@Test
	public void testStaticOptimization() throws Exception {
		logger.info("Static optimization:");
		util_setCredentials(userName, password, APIKey);

		Portfolio portfolio = portfolio_create("t-1", "t-1", "SPY");

		portfolio_addPosition(portfolio, "SPY", 200);
		portfolio_addPosition(portfolio, "C", 200);
		portfolio_addPosition(portfolio, "GOOG", 200);
		portfolio_addPosition(portfolio, "AAPL", 200);


		portfolio_settings(portfolio,"portfolioMetricsMode", "price"); // static optimization
		portfolio_settings(portfolio,"resultsSamplingInterval", "10m");

		PortfolioOptimizer optimizer = optimization_goal(portfolio, "VaR", "minimize", 0.95);

		Portfolio optimizedPortfolio = optimization_run(optimizer);

		TimeValue result = portfolio_VaR(portfolio, 0.95);
		TimeValue optimizedResult = portfolio_VaR(optimizedPortfolio, 0.95);

		logger.info("static portfolio:  init VaR(0.95=" + result.getValueLast() + "\t optim VaR(0.95)=" + optimizedResult.getValueLast());

		portfolio_settings(portfolio,"portfolioMetricsMode", "porfolio");
		portfolio_settings(optimizedPortfolio,"portfolioMetricsMode","porfolio");

		result = portfolio_VaR(portfolio, 0.95);
		optimizedResult = portfolio_VaR(optimizedPortfolio, 0.95);

		logger.info("strategy:  init VaR(0.95=" + result.getValueLast() + "\t optim VaR(0.95)=" + optimizedResult.getValueLast());

	}

	@Test
	public void testStrategyOptimization() throws Exception {
		logger.info("Strategy optimization:");
		util_setCredentials(userName, password, APIKey);

		Portfolio portfolio = portfolio_create("t-1", "t-1", "SPY");

		portfolio_addPosition(portfolio, "SPY", 200);
		portfolio_addPosition(portfolio, "C", 200);
		portfolio_addPosition(portfolio, "GOOG", 200);
		portfolio_addPosition(portfolio, "AAPL", 200);

		
		portfolio_settings(portfolio,"resultsSamplingInterval", "10m");

		PortfolioOptimizer optimizer = optimization_goal(portfolio, "VaR", "minimize", 0.95, "10m", "10m", "exp_smoothing", "10m");

		Portfolio optimizedPortfolio = optimization_run(optimizer);

		TimeValue result = portfolio_VaR(portfolio, 0.95);
		TimeValue optimizedResult = portfolio_VaR(optimizedPortfolio, 0.95);

		logger.info("strategy:  init VaR(0.95=" + result.getValueLast() + "\t optim VaR(0.95)=" + optimizedResult.getValueLast());

	}

	@Test
	public void testStrategyOptimizationWithConstraint() throws Exception {
		logger.info("\n\nStrategy optimization with constraint :");
		util_setCredentials(userName, password, APIKey);

		Portfolio portfolio = portfolio_create("t-1", "t-1", "SPY");

		portfolio_addPosition(portfolio, "SPY", 200);
		portfolio_addPosition(portfolio, "C", 200);
		portfolio_addPosition(portfolio, "GOOG", 200);
		portfolio_addPosition(portfolio, "AAPL", 200);

		portfolio_settings(portfolio,"resultsSamplingInterval", "10m");
		
		PortfolioOptimizer optimizer = optimization_goal(portfolio, "VaR", "minimize", 0.95, "10m", "10m", "exp_smoothing", "10m");

		optimization_constraint_allWeights(optimizer, ">=", 0.0);
		Portfolio optimizedPortfolio = optimization_run(optimizer);

		TimeValue result = portfolio_VaR(portfolio, 0.95);
		TimeValue optimizedResult = portfolio_VaR(optimizedPortfolio, 0.95);

		logger.info("strategy:  init VaR(0.95=" + result.getValueLast() + "\t optim VaR(0.95)=" + optimizedResult.getValueLast());

	}

}
