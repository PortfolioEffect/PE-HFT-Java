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

import com.portfolioeffect.quant.client.api.Metric;
import com.portfolioeffect.quant.client.api.Portfolio;
import com.portfolioeffect.quant.client.api.Position;
import com.portfolioeffect.quant.client.api.Util;
import com.portfolioeffect.quant.client.model.ComputeErrorException;

@Ignore
public class AddPositionsTest {

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

		//or set you credentials
		//To obtain a free non-professional
		//account, you need to follow a quick sign-up process on our website: www.portfolioeffect.com/registration. 
		userName = props.getProperty("userName");
		password = props.getProperty("password");
		APIKey = props.getProperty("apiKey");

	}

	@Test
	public void addSingleQuantity() throws ComputeErrorException {

		Util.util_setCredentials(userName, password, APIKey);

		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric variance = portfolio.variance();
		Assert.assertTrue(variance.getValue().length > 0);
		

	}

	@Test
	public void addMultipleQuantities() throws ComputeErrorException {
		Util.util_setCredentials(userName, password, APIKey);

		Portfolio portfolio = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position position = portfolio.add_position("AAPL", new int[] { 100, 200 }, new long[] { 1434117340000L, 1434290140000L });
		Metric variance = portfolio.variance();
		Assert.assertTrue(variance.getValue().length > 0);
	}
	
	@Test
	public void addMultipleQuantitiesDateFormat() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("2015-06-12 09:30:00", "2015-06-20 16:00:00", "SPY");
		Position position = portfolio.add_position("AAPL", new int[] { 100, 200 }, new String[] { "2015-06-12 09:55:00", "2015-06-16 09:55:00" });
		Metric variance = portfolio.variance();
		Assert.assertTrue(variance.getValue().length > 0);
	}

	@Test
	public void addSingleQuantityFromData() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-3", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric price = positionA.price();

		portfolio = new Portfolio("t-3", "t", "SPY");
		positionA = portfolio.add_position("AAPL", 100, price.getValue(), price.getTime());

		Metric variance = portfolio.variance();
		Assert.assertTrue(variance.getValue().length > 0);
	}
}
