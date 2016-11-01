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
package com.portfolioeffect.quant.client.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.portfolioeffect.quant.client.api.Forecast;
import com.portfolioeffect.quant.client.api.Metric;
import com.portfolioeffect.quant.client.api.Optimizer;
import com.portfolioeffect.quant.client.api.Portfolio;
import com.portfolioeffect.quant.client.api.Position;
import com.portfolioeffect.quant.client.api.Util;
import com.portfolioeffect.quant.client.model.ComputeErrorException;

@Ignore
public class OptimizationTest {
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
		Util.util_setCredentials(userName, password, APIKey);

	}
	
    @Test
	public void simpleOptimization() throws ComputeErrorException{
      Portfolio   portfolio = new Portfolio("t-1", "t", "SPY");
        portfolio.add_position("GOOG", 100);
        portfolio.add_position("AAPL", 100);
        portfolio.settings( "portfolioMetricsMode","price").settings("resultsSamplingInterval","1m");
        Optimizer optimizer = new  Optimizer(portfolio.variance(), "min");
        Portfolio optimPortfolio = optimizer.run().getPortfolio();
        Metric variance = optimPortfolio.variance();
        
        Assert.assertTrue(variance.getValue().length>0);        
    }
    
    @Test 
    public void  portfolioOptimization() throws ComputeErrorException{
      Portfolio  portfolio = new Portfolio("t-1", "t", "SPY");
        portfolio.add_position("GOOG", 100);
        portfolio.add_position("AAPL", 100);
        portfolio.settings("portfolioMetricsMode","portfolio").settings("resultsSamplingInterval","1m");
        Optimizer optimizer = new Optimizer(portfolio.variance(), "min");
        Portfolio optimPortfolio = optimizer.run().getPortfolio();
        Metric variance = optimPortfolio.variance();
        Assert.assertTrue(variance.getValue().length>0);

     }
    
    
    @Test 
    public void  optimizationConstraint() throws ComputeErrorException{
        Portfolio portfolio = new  Portfolio("t-1", "t", "SPY");
        portfolio.add_position("GOOG", 100);
        portfolio.add_position("AAPL", 100);
        portfolio.settings("portfolioMetricsMode","price").settings("resultsSamplingInterval","1m");
        Optimizer optimizer = new Optimizer(portfolio.variance(), "min");
        optimizer.constraint( portfolio.expected_return(), ">=", 0);
        Portfolio optimPortfolio = optimizer.run().getPortfolio();
        Metric variance = optimPortfolio.variance();
        Assert.assertTrue(variance.getValue().length>0);

    }
    
    
    
    @Test 
    public void  testOptimizationForecast() throws ComputeErrorException{
    	Portfolio portfolio = new  Portfolio("t-1", "t", "SPY");
        portfolio.add_position("GOOG", 100);
        portfolio.add_position("AAPL", 100);
        portfolio.settings("portfolioMetricsMode","portfolio").settings("resultsSamplingInterval","1m");
        Optimizer optimizer = new Optimizer(portfolio.variance(), "min");
        optimizer.constraint( portfolio.expected_return(), ">=", 0);
        
        Forecast forecast = new Forecast();
        forecast.setForecastModel("HAR");
        optimizer.setForecast(forecast);
        
        Portfolio optimPortfolio = optimizer.run().getPortfolio();
        Metric variance = optimPortfolio.variance();
        Assert.assertTrue(variance.getValue().length>0);

    }
    
    
    @Test 
    public void  optimizationForecastSamplingInterval() throws ComputeErrorException{
        Portfolio portfolio = new Portfolio( "2014-11-17 09:30:00", "2014-12-17 16:00:00", "SPY");
        Position positionG = portfolio.add_position("GOOG", 200);
        Position  positionA = portfolio.add_position("AAPL", 100);
        portfolio.settings("inputSamplingInterval","30m").settings("resultsSamplingInterval","1d");
        
        
        Optimizer optimizer1 = new Optimizer(portfolio.variance(), "min");
        optimizer1.constraint(portfolio.log_return(),">=",0);
        Portfolio optimPortfolio= optimizer1.run().getPortfolio();
        Metric variance1 = optimPortfolio.variance();
        
        Forecast forecast = (new Forecast()).setForecastModel("HAR").setForecastStep("1d");
        
        Optimizer optimizer2 = new Optimizer(portfolio.variance(), "min");
        optimizer2.constraint(portfolio.log_return(), ">=", 0);
        optimizer2.forecast("Variance",forecast);
        Portfolio  optimPortfolioForecast = optimizer2.run().getPortfolio();
        Metric variance2 = optimPortfolioForecast.variance();
        
        Assert.assertTrue(variance2.getLastValue()!=variance1.getLastValue());
    }

}
