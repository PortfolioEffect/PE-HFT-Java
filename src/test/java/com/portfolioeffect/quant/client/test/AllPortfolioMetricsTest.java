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
import com.portfolioeffect.quant.client.api.Util;
import com.portfolioeffect.quant.client.model.ComputeErrorException;
@Ignore
public class AllPortfolioMetricsTest {
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
	public void portfolio_value() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.value();
		Assert.assertTrue(test.getValue().length > 0);

	}

	@Test
	public void portfolio_expected_shortfall() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.expected_shortfall(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_value_at_risk() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.value_at_risk(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_alpha_exante() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.alpha_exante();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_beta() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.beta();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_calmar_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.calmar_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_cumulant() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.cumulant(3);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_down_capture_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.down_capture_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_down_number_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.down_number_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_down_percentage_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.down_percentage_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_downside_variance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.downside_variance(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_expected_downside_return() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.expected_downside_return(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_expected_return() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.expected_return();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_expected_upside_return() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.expected_upside_return(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_fractal_dimension() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.fractal_dimension();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_gain_loss_variance_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.gain_loss_variance_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_gain_variance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.gain_variance();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_hurst_exponent() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.hurst_exponent();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_information_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.information_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_alpha_jensens() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.alpha_jensens();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_kurtosis() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.kurtosis();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_loss_variance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.loss_variance();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_max_drawdown() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.max_drawdown();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_mod_sharpe_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.mod_sharpe_ratio(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_moment() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.moment(4);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_omega_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.omega_ratio(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_profit() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.profit();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_rachev_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.rachev_ratio(0.95, 0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_log_return() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.log_return();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_sharpe_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.sharpe_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_skewness() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.skewness();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_sortino_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.sortino_ratio(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_starr_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.starr_ratio(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_treynor_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.treynor_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_txn_costs() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.txn_costs();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_up_capture_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.up_capture_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_up_number_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.up_number_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_up_percentage_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.up_percentage_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_upside_downside_variance_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.upside_downside_variance_ratio(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_upside_variance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.upside_variance(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void portfolio_variance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		portfolio.add_position("GOOG", 100);
		portfolio.add_position("AAPL", 100);
		Metric test = portfolio.variance();
		Assert.assertTrue(test.getValue().length > 0);
	}

}
