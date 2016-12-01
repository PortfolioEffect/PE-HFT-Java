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
import java.util.HashMap;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.portfolioeffect.quant.client.api.Metric;
import com.portfolioeffect.quant.client.api.Portfolio;
import com.portfolioeffect.quant.client.api.Position;
import com.portfolioeffect.quant.client.api.Util;
import com.portfolioeffect.quant.client.model.ComputeErrorException;

@Ignore
public class PortfolioSettingsTest {
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
		Util.util_setCredentials(userName, password, APIKey);

	}

	@Test
	public void setting() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		portfolio.settings("resultsSamplingInterval", "30m");
		Metric variance = portfolio.variance();
		Assert.assertTrue(variance.getValue().length == 13);

	}

	@Test
	public void getSettings() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		portfolio.settings("resultsSamplingInterval", "30m");

		HashMap<String, String> setting = portfolio.settings();
		Assert.assertTrue(setting.get("resultsSamplingInterval").equals("30m"));

	}

	@Test
	public void setting_portfolioMetricsMode() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", 100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("portfolioMetricsMode", "portfolio");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", 100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("portfolioMetricsMode", "price");
		Metric variance1 = portfolio1.variance();
		Metric variance2 = portfolio2.variance();
		Assert.assertTrue(variance1.getLastValue() != variance2.getLastValue());

	}

	@Test
	public void setting_windowLength() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-20 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", 100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("windowLength", "1d");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-20 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", 100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("windowLength", "10d");
		Metric variance1 = portfolio1.variance();
		Metric variance2 = portfolio2.variance();
		Assert.assertTrue(variance1.getLastValue() != variance2.getLastValue());

	}

	@Test
	public void setting_jumpsModel() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", 100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("jumpsModel", "moments");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", 100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("jumpsModel", "none");
		Metric variance1 = portfolio1.variance();
		Metric variance2 = portfolio2.variance();
		Assert.assertTrue(variance1.getLastValue() != variance2.getLastValue());

	}

	@Test
	public void setting_noiseModel() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", 100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("noiseModel", "true");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", 100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("noiseModel", "false");
		Metric variance1 = portfolio1.variance();
		Metric variance2 = portfolio2.variance();
		Assert.assertTrue(variance1.getLastValue() != variance2.getLastValue());

	}

	@Test
	public void setting_fractalPriceModel() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", 100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("fractalPriceModel", "true");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", 100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("fractalPriceModel", "false");
		Metric variance1 = portfolio1.variance();
		Metric variance2 = portfolio2.variance();
		Assert.assertTrue(variance1.getLastValue() != variance2.getLastValue());

	}

	@Test
	public void setting_driftTerm() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-24 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", 100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("driftTerm", "true");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-24 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", 100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("driftTerm", "false");
		Metric expected_shortfall1 = portfolio1.expected_shortfall(0.95);
		Metric expected_shortfall2 = portfolio2.expected_shortfall(0.95);
		Assert.assertTrue(expected_shortfall1.getLastValue() != expected_shortfall2.getLastValue());

	}

	@Test
	public void setting_inputSamplingInterval() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", 100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("inputSamplingInterval", "1m");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", 100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("inputSamplingInterval", "1s");
		Metric variance1 = portfolio1.variance();
		Metric variance2 = portfolio2.variance();
		Assert.assertTrue(variance1.getLastValue() != variance2.getLastValue());

	}

	@Test
	public void setting_timeScale() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", 100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("timeScale", "1d");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", 100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("timeScale", "2d");
		Metric variance1 = portfolio1.variance();
		Metric variance2 = portfolio2.variance();
		Assert.assertTrue(variance1.getLastValue() != variance2.getLastValue());

	}

	@Test
	public void setting_holdingPeriodsOnly() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2014-10-01 09:30:00", "2014-10-02 16:00:00", "SPY");
		Position positionA = portfolio1.add_position("AAPL", new int[] { 0, 300, 150, 0 }, new String[] { "2014-09-30 09:30:00", "2014-10-01 09:30:00",
				"2014-10-01 15:30:00", "2014-10-02 11:30:00" });
		portfolio1.settings("holdingPeriodsOnly", "true");
		Portfolio portfolio2 = new Portfolio("2014-10-01 09:30:00", "2014-10-02 16:00:00", "SPY");
		positionA = portfolio2.add_position("AAPL", new int[] { 0, 300, 150, 0 }, new String[] { "2014-09-30 09:30:00", "2014-10-01 09:30:00",
				"2014-10-01 15:30:00", "2014-10-02 11:30:00" });
		portfolio2.settings("holdingPeriodsOnly", "false");
		Metric variance1 = portfolio1.variance();
		Metric variance2 = portfolio2.variance();
		Assert.assertTrue(variance1.getLastValue() != variance2.getLastValue());

	}

	@Test
	public void setting_shortSalesMode() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", -100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("shortSalesMode", "markowitz");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", -100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("shortSalesMode", "lintner");
		Metric variance1 = portfolio1.variance();
		Metric variance2 = portfolio2.variance();
		Assert.assertTrue(variance1.getLastValue() != variance2.getLastValue());

	}

	@Test
	public void setting_densityModel() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", 100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("densityModel", "NORMAL");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", 100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("densityModel", "GLD");
		Metric expected_shortfall1 = portfolio1.expected_shortfall(0.95);
		Metric expected_shortfall2 = portfolio2.expected_shortfall(0.95);
		Assert.assertTrue(expected_shortfall1.getLastValue() != expected_shortfall2.getLastValue());

	}

	@Test
	public void setting_factorModel() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		Position positionG = portfolio1.add_position("GOOG", 100);
		Position positionA = portfolio1.add_position("AAPL", 100);
		portfolio1.settings("factorModel", "sim");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		positionG = portfolio2.add_position("GOOG", 100);
		positionA = portfolio2.add_position("AAPL", 100);
		portfolio2.settings("factorModel", "direct");
		Metric variance1 = portfolio1.variance();
		Metric variance2 = portfolio2.variance();
		Assert.assertTrue(variance1.getLastValue() != variance2.getLastValue());
	}

	@Test
	public void setting_txnCostPerShare() throws ComputeErrorException {
		Portfolio portfolio1 = new Portfolio("2014-10-01 09:30:00", "2014-10-02 16:00:00", "SPY");
		Position positionA = portfolio1.add_position("AAPL", new int[] { 0, 300, 150, 0 }, new String[] { "2014-09-30 09:30:00", "2014-10-01 09:30:00",
				"2014-10-01 15:30:00", "2014-10-02 11:30:00" });
		portfolio1.settings("txnCostPerShare", "0.005");
		Portfolio portfolio2 = new Portfolio("2015-06-12 09:30:00", "2015-06-14 16:00:00", "SPY");
		positionA = portfolio2.add_position("AAPL", new int[] { 0, 300, 150, 0 }, new String[] { "2014-09-30 09:30:00", "2014-10-01 09:30:00",
				"2014-10-01 15:30:00", "2014-10-02 11:30:00" });
		portfolio2.settings("txnCostPerShare", "0.001");
		Metric txn_costs1 = portfolio1.txn_costs();
		Metric txn_costs2 = portfolio2.txn_costs();
		Assert.assertTrue(txn_costs1.getLastValue() != txn_costs2.getLastValue());

	}

}
