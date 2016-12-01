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
public class AllPositionMetricsTest {
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
	public void position_value() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.value();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_expected_shortfall() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.expected_shortfall(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_value_at_risk() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.value_at_risk(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_alpha_exante() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.alpha_exante();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_beta() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.beta();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_calmar_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.calmar_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_cumulant() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.cumulant(3);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_down_capture_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.down_capture_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_down_number_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.down_number_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_down_percentage_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.down_percentage_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_downside_variance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.downside_variance(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_expected_downside_return() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.expected_downside_return(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_expected_return() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.expected_return();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_expected_upside_return() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.expected_upside_return(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_fractal_dimension() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.fractal_dimension();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_gain_loss_variance_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.gain_loss_variance_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_gain_variance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.gain_variance();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_hurst_exponent() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.hurst_exponent();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_information_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.information_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_alpha_jensens() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.alpha_jensens();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_kurtosis() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.kurtosis();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_loss_variance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.loss_variance();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_max_drawdown() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.max_drawdown();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_mod_sharpe_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.mod_sharpe_ratio(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_moment() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.moment(4);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_omega_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.omega_ratio(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_profit() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.profit();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_rachev_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.rachev_ratio(0.95, 0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_log_return() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.log_return();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_sharpe_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.sharpe_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_skewness() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.skewness();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_sortino_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.sortino_ratio(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_starr_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.starr_ratio(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_treynor_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.treynor_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_txn_costs() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.txn_costs();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_up_capture_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.up_capture_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_up_number_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.up_number_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_up_percentage_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.up_percentage_ratio();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_upside_downside_variance_ratio() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.upside_downside_variance_ratio(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_upside_variance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.upside_variance(0.95);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_variance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.variance();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_quantity() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.quantity();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_price() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.price();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_weight() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.weight();
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_return_autocovariance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.return_autocovariance(10);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_correlation() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.correlation(positionG);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void position_covariance() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		Metric test = positionA.covariance(positionG);
		Assert.assertTrue(test.getValue().length > 0);
	}

	@Test
	public void set_quantity() throws ComputeErrorException {
		Portfolio portfolio = new Portfolio("t-1", "t", "SPY");
		Position positionG = portfolio.add_position("GOOG", 100);
		Position positionA = portfolio.add_position("AAPL", 100);
		positionA.set_quantity(200);
		Metric test = positionA.quantity();
		Assert.assertTrue(test.getValue()[0] == 200);

	}

}
