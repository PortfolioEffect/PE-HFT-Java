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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.portfolioeffect.quant.client.api.Forecast;
import com.portfolioeffect.quant.client.api.Metric;
import com.portfolioeffect.quant.client.api.Portfolio;
import com.portfolioeffect.quant.client.api.Position;
import com.portfolioeffect.quant.client.api.Util;
import com.portfolioeffect.quant.client.model.ComputeErrorException;

@Ignore
public class ForecastTest {
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
	public void simple_forecast() throws ComputeErrorException {
		Util.util_setCredentials(userName, password, APIKey);
		
        Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
        Position positionG = portfolio.add_position("GOOG", 100);
        Position positionA = portfolio.add_position("AAPL", 100);
        
        Forecast forecastObject = new  Forecast(positionA.variance());
        forecastObject.input(positionA.beta());
        Metric forecastVector = forecastObject.apply();

        Assert.assertTrue(forecastVector.getValue().length>0);
    }
        


	@Test
	public void forecast_settings_model() throws ComputeErrorException{
		Util.util_setCredentials(userName, password, APIKey);
		
		Portfolio  portfolio1 = new  Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
        Position positionG = portfolio1.add_position("GOOG", 100);
        Position positionA = portfolio1.add_position("AAPL", 100);
        
        Forecast forecastObject = new  Forecast(positionA.variance());
        forecastObject.input( positionA.beta());
        Metric forecastVector1 = forecastObject.apply();
        
        forecastObject =  new Forecast(positionA.variance());
        forecastObject.setForecastModel("HAR");
        forecastObject.input( positionA.beta());
        Metric forecastVector2 = forecastObject.apply();
        
        Assert.assertTrue(forecastVector1.getLastValue() != forecastVector2.getLastValue());

        
	}
	
	
	@Test
	public void forecast_settings_window() throws ComputeErrorException{
	     Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
	     Position positionG = portfolio1.add_position("GOOG", 100);
	     Position positionA = portfolio1.add_position("AAPL", 100);
	    
	     Forecast forecastObject = new Forecast(positionA.variance());
	     forecastObject.setWindow("5d");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector1 = forecastObject.apply();
	     
	     forecastObject = new  Forecast(positionA.variance());
	     forecastObject.setWindow("10d");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector2 = forecastObject.apply();

	     Assert.assertTrue(forecastVector1.getLastValue() != forecastVector2.getLastValue());

	}
	
	@Test
	public void forecast_settings_step() throws ComputeErrorException{
	     Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
	     Position positionG = portfolio1.add_position("GOOG", 100);
	     Position positionA = portfolio1.add_position("AAPL", 100);
	     
	     Forecast forecastObject = new Forecast(positionA.variance());
	     forecastObject.setForecastStep("1d");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector1 = forecastObject.apply();
	     
	     forecastObject = new  Forecast(positionA.variance());
	     forecastObject.setForecastStep("1h");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector2 = forecastObject.apply();

	     Assert.assertTrue(forecastVector1.getLastValue() != forecastVector2.getLastValue());

	}
	
	@Test
	public void forecast_settings_transform() throws ComputeErrorException{
	     Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
	     Position positionG = portfolio1.add_position("GOOG", 100);
	     Position positionA = portfolio1.add_position("AAPL", 100);
	     
	     Forecast forecastObject = new Forecast(positionA.variance());
	     forecastObject.setTransform("log");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector1 = forecastObject.apply();
	     
	     forecastObject = new  Forecast(positionA.variance());
	     forecastObject.setTransform("none");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector2 = forecastObject.apply();

	     Assert.assertTrue(forecastVector1.getLastValue() != forecastVector2.getLastValue());

	}
	
	@Test
	public void forecast_settings_seasonalityInterval() throws ComputeErrorException{
	     Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
	     Position positionG = portfolio1.add_position("GOOG", 100);
	     Position positionA = portfolio1.add_position("AAPL", 100);
	     
	     Forecast forecastObject = new Forecast(positionA.variance());
	     forecastObject.setSeasonalityInterval("none");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector1 = forecastObject.apply();
	     
	     forecastObject = new  Forecast(positionA.variance());
	     forecastObject.setSeasonalityInterval("1h");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector2 = forecastObject.apply();

	     Assert.assertTrue(forecastVector1.getLastValue() != forecastVector2.getLastValue());

	}
	
	@Test
	public void forecast_settings_updateInterval() throws ComputeErrorException{
	     Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
	     Position positionG = portfolio1.add_position("GOOG", 100);
	     Position positionA = portfolio1.add_position("AAPL", 100);
	     
	     Forecast forecastObject = new Forecast(positionA.variance());
	     forecastObject.setUpdateInterval("1m");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector1 = forecastObject.apply();
	     
	     forecastObject = new  Forecast(positionA.variance());
	     forecastObject.setUpdateInterval("5m");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector2 = forecastObject.apply();

	     Assert.assertTrue(forecastVector1.getLastValue() != forecastVector2.getLastValue());

	}

	@Test
	public void forecast_settings_valueType() throws ComputeErrorException{
	     Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
	     Position positionG = portfolio1.add_position("GOOG", 100);
	     Position positionA = portfolio1.add_position("AAPL", 100);
	     
	     Forecast forecastObject = new Forecast(positionA.variance());
	     forecastObject.setValueType("forecast");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector1 = forecastObject.apply();
	     
	     forecastObject = new  Forecast(positionA.variance());
	     forecastObject.setValueType("error");
	     forecastObject.input( positionA.beta());
	     Metric forecastVector2 = forecastObject.apply();

	     Assert.assertTrue(forecastVector1.getLastValue() != forecastVector2.getLastValue());

	}
	
	@Test
	public void forecast_settings() throws ComputeErrorException{
	     Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
	     Position positionG = portfolio1.add_position("GOOG", 100);
	     Position positionA = portfolio1.add_position("AAPL", 100);
	     
	     Forecast forecastObject = new Forecast(positionA.variance());
	     
	     forecastObject.input( positionA.beta());
	     Metric forecastVector1 = forecastObject.apply();
	     
	     forecastObject = new  Forecast(positionA.variance());
	      Metric forecastVector2 = forecastObject.apply();

	     Assert.assertTrue(forecastVector1.getLastValue() != forecastVector2.getLastValue());

	}




	
}
