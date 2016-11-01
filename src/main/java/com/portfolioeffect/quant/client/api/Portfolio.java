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

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.util.SimpleLazyMetricBuilder;

/**
 *
 * Container class for storing portfolio parameters.
 * 
 * @author oleg
 * 
 *
 */
public class Portfolio {

	private com.portfolioeffect.quant.client.portfolio.Portfolio portfolio;

	
	public Portfolio(com.portfolioeffect.quant.client.portfolio.Portfolio portfolio)  {
		this.portfolio = portfolio;
	}
	
	public Portfolio(Portfolio portfolio) throws IOException  {

		this.portfolio = new com.portfolioeffect.quant.client.portfolio.Portfolio(portfolio.portfolio);

	}

	/**
	 * Creates new empty Portfolio.
	 * 
	 * @param fromTime
	 *            Start of market data interval in "yyyy-MM-dd hh:mm:ss" format
	 *            when internal market data is used. Offset from last available
	 *            date/time by N days is denoted as "t-N" (e.g. "t-7" denotes
	 *            offset by 7 days).
	 * @param toTime
	 *            End of market data interval in "yyyy-MM-dd hh:mm:ss" format
	 *            when internal market data is used. Offset from last available
	 *            date/time by N days is denoted as "t-N" (e.g. "t-7" denotes
	 *            offset by 7 days).
	 * @param index
	 *            Index symbol that should be used in the Single Index Model.
	 *            Defaults to "SPY".
	 * @throws ComputeErrorException 
	 */
	public Portfolio(String fromTime, String toTime, String index) throws ComputeErrorException {

		portfolio = new com.portfolioeffect.quant.client.portfolio.Portfolio(Util.getClientConnection(), fromTime, toTime, index);

	}

	/**
	 * Creates new empty Portfolio.
	 * 
	 * @param fromTime
	 *            Start of market data interval in "yyyy-MM-dd hh:mm:ss" format
	 *            when internal market data is used. Offset from last available
	 *            date/time by N days is denoted as "t-N" (e.g. "t-7" denotes
	 *            offset by 7 days).
	 * @param toTime
	 *            End of market data interval in "yyyy-MM-dd hh:mm:ss" format
	 *            when internal market data is used. Offset from last available
	 *            date/time by N days is denoted as "t-N" (e.g. "t-7" denotes
	 *            offset by 7 days).
	 *            <p>
	 *            Index symbol that should be used in the Single Index Model.
	 *            Defaults to "SPY".
	 *            </p>
	 * @throws ComputeErrorException 
	 */
	public Portfolio(String fromTime, String toTime) throws ComputeErrorException {
		this(fromTime, toTime, "SPY");

	}
	/**
	 * Creates new empty Portfolio.
	 * 
	 * @param indexPrice
	 * @param indexTime
	 * @throws ComputeErrorException
	 */
	public Portfolio(double[] indexPrice, long[] indexTime) throws ComputeErrorException {
		portfolio = new com.portfolioeffect.quant.client.portfolio.Portfolio(Util.getClientConnection());
		portfolio.addIndex(indexPrice, indexTime);
		portfolio.setFromTime( (new Timestamp(indexTime[0])).toString() );
		portfolio.setToTime( (new Timestamp(indexTime[indexTime.length-1])).toString() );
	}

	/**
	 * Adds position to an existing portfolio
	 * 
	 * @param symbol
	 * @param quantity
	 * @return
	 */
	public Position add_position(String symbol, int quantity) {

		com.portfolioeffect.quant.client.portfolio.Position position = new com.portfolioeffect.quant.client.portfolio.Position(portfolio, symbol, quantity);

		return new Position(position);

	}

	/**
	 * Adds position to an existing portfolio
	 * 
	 * @param symbol
	 * @param quantity
	 * @param timeQuantity
	 * @return
	 */
	public Position add_position(String symbol, int[] quantity, long[] timeQuantity) {

		com.portfolioeffect.quant.client.portfolio.Position position = new com.portfolioeffect.quant.client.portfolio.Position(portfolio, symbol, quantity,
				timeQuantity);
		return new Position(position);

	}

	/**
	 * Adds position to an existing portfolio
	 * 
	 * @param symbol
	 * @param quantity
	 * @param timeQuantity
	 * @return
	 */
	public Position add_position(String symbol, int[] quantity, String[] timeQuantity) {

		com.portfolioeffect.quant.client.portfolio.Position position = new com.portfolioeffect.quant.client.portfolio.Position(portfolio, symbol, quantity,
				timeQuantity);
		return new Position(position);

	}

	/**
	 * Adds position to an existing portfolio
	 * 
	 * @param symbol
	 * @param quantity
	 * @param price
	 * @param priceTime
	 * @return
	 */
	public Position add_position(String symbol, int quantity, double[] price, long[] priceTime) {

		com.portfolioeffect.quant.client.portfolio.Position position = new com.portfolioeffect.quant.client.portfolio.Position(portfolio, symbol, price,
				quantity, priceTime);

		return new Position(position);

	}

	/**
	 * Adds position to an existing portfolio
	 * 
	 * @param symbol
	 * @param quantity
	 * @param quantityTime
	 * @param price
	 * @param priceTime
	 * @return
	 */
	public Position add_position(String symbol, int[] quantity, long[] quantityTime, double[] price, long[] priceTime) {

		com.portfolioeffect.quant.client.portfolio.Position position = new com.portfolioeffect.quant.client.portfolio.Position(portfolio, symbol, price,
				priceTime, quantity, quantityTime);

		return new Position(position);

	}

	/**
	 * Removes position from an existing portfolio.
	 * 
	 * @param symbol
	 */
	public void remove_position(String symbol) {
		portfolio.removePositionPrice(symbol);
		portfolio.removePositionQuantity(symbol);
	}

	/**
	 * Set portfolio params. Advanced settings that regulate how porfolio
	 * metrics are computed, returned and stored.
	 * <p>
	 * Default: portfolioMetricsMode="portfolio", windowLength = "1d",
	 * holdingPeriodsOnly = FALSE, shortSalesMode = "
	 * lintner", jumpsModel = "moments
	 * ", noiseModel = TRUE, fractalPriceModel=TRUE, factorModel = "
	 * sim", densityModel=" GLD", driftTerm=TRUE, resultsSamplingInterval = "1s
	 * ", inputSamplingInterval=" none", timeScale="1d", txnCostPerShare=0,
	 * txnCostFixed=0
	 * </p>
	 * 
	 * <p>
	 * key="portfolioMetricsMode" Used to select method of computing portfolio
	 * metrics. Available modes are: "portfolio" - risk and performance metrics
	 * are computed based on the history of position rebalancing (see
	 * windowLength parameter) and should be used to backtest and compare
	 * trading strategies of different frequency and style, "price" - metrics
	 * are always computed without a history of previous rebalancing (classic
	 * interpretation). Defaults to "portfolio".
	 * </p>
	 *
	 * <p>
	 * key="windowLength" - Rolling window length for metric estimations and
	 * position rebalancing history. Available interval values are: "Xs" -
	 * seconds, "Xm" - minutes, "Xh" - hours, "Xd" - trading days (6.5 hours in
	 * a trading day), "Xw" - weeks (5 trading days in 1 week), "Xmo" - month
	 * (21 trading day in 1 month), "Xy" - years (256 trading days in 1 year),
	 * "all" - all observations are used. Default value is "1d" - one trading
	 * day.
	 * </p>
	 * 
	 * <p>
	 * key="holdingPeriodsOnly - Used when portfolioMetricsMode = "portfolio".
	 * Defaults to FALSE, which means that trading strategy risk and performance
	 * metrics will be scaled to include intervals when trading strategy did not
	 * have market exposure. When TRUE, trading strategy metrics are scaled
	 * based on actual holding intervals when there was exposure to the market.
	 * </p>
	 * 
	 * <p>
	 * key="shortSalesMode" - Used to specify how position weights are computed.
	 * Available modes are: "lintner" - the sum of absolute weights is equal to
	 * 1 (Lintner assumption), "markowitz" - the sum of weights must equal to 1
	 * (Markowitz assumption). Defaults to "lintner", which implies that the sum
	 * of absolute weights is used to normalize investment weights.
	 * </p>
	 * 
	 * <p>
	 * key="jumpsModel" - Used to select jump filtering mode when computing
	 * return statistics. Available modes are: "none" - price jumps are not
	 * filtered anywhere, "moments" - price jumps are filtered only when
	 * computing moments (variance, skewness, kurtosis) and derived metrics,
	 * "all" - price jumps are filtered everywhere. Defaults to "moments", which
	 * implies that only return moments and related metrics would be using
	 * jump-filtered returns in their calculations.
	 * </p>
	 * 
	 * <p>
	 * key="noiseModel" - Used to enable microstructure noise model of
	 * distribution returns. Defaults to TRUE, which implies that microstructure
	 * effects are modeled and resulting HF noise is removed from metric
	 * calculations.
	 * </p>
	 * 
	 * <p>
	 * key="fractalPriceModel" - Used to enable mono-fractal price assumptions
	 * (fGBM) when time scaling return moments. Defaults to TRUE, which implies
	 * that computed Hurst exponent is used to scale return moments. When FALSE,
	 * price is assumed to follow regular GBM with Hurst exponent = 0.5.
	 * </p>
	 * 
	 * <p>
	 * key="factorModel" - Used to select factor model for computing portfolio
	 * metrics. Available models are: "sim" - portfolio metrics are computed
	 * using the Single Index Model, "direct" - portfolio metrics are computed
	 * using portfolio value itself. Defaults to "sim", which implies that the
	 * Single Index Model is used to compute portfolio metrics./
	 * <p>
	 * 
	 * 
	 * <p>
	 * key="densityModel" - Used to select density approximation model of return
	 * distribution. Available models are: "GLD" - Generalized Lambda
	 * Distribution, "CORNER_FISHER" - Corner-Fisher approximation, "NORMAL" -
	 * Gaussian distribution. Defaults to "GLD", which would fit a broad range
	 * of distribution shapes.
	 * </p>
	 * 
	 * <p>
	 * key="driftTerm" - Used to enable drift term (expected return) when
	 * computing probability density approximation and related metrics (e.g.
	 * CVaR, Omega Ratio, etc.). Defaults to FALSE, which implies that
	 * distribution is centered around zero return.
	 * </p>
	 * 
	 * <p>
	 * key="resultsNAFilter" - Used to enable filtering of NA values in computed
	 * results. Defaults to TRUE, which implies that output results have all NA
	 * values removed.
	 * </p>
	 * 
	 * <p>
	 * key="resultsSamplingInterval" - Interval to be used for sampling computed
	 * results before returning them to the caller. Available interval values
	 * are: "Xs" - seconds, "Xm" - minutes, "Xh" - hours, "Xd" - trading days
	 * (6.5 hours in a trading day), "Xw" - weeks (5 trading days in 1 week),
	 * "Xmo" - month (21 trading day in 1 month), "Xy" - years (256 trading days
	 * in 1 year), "last" - last result in a series is returned, "none" - no
	 * sampling. Large sampling interval would produce smaller vector of results
	 * and would require less time spent on data transfer. Default value of "1s"
	 * indicates that data is returned for every second during trading hours.
	 * </p>
	 * 
	 * <p>
	 * key="inputSamplingInterval" - Interval to be used as a minimum step for
	 * sampling input prices. Available interval values are: "Xs" - seconds,
	 * "Xm" - minutes, "Xh" - hours, "Xd" - trading days (6.5 hours in a trading
	 * day), "Xw" - weeks (5 trading days in 1 week), "Xmo" - month (21 trading
	 * day in 1 month), "Xy" - years (256 trading days in 1 year), "none" - no
	 * sampling. Default value is "none", which indicates that no sampling is
	 * applied.
	 * </p>
	 * 
	 * <p>
	 * key="timeScale" - Interval to be used for scaling return distribution
	 * statistics and producing metrics forecasts at different horizons.
	 * Available interval values are: "Xs" - seconds, "Xm" - minutes, "Xh" -
	 * hours, "Xd" - trading days (6.5 hours in a trading day), "Xw" - weeks (5
	 * trading days in 1 week), "Xmo" - month (21 trading day in 1 month), "Xy"
	 * - years (256 trading days in 1 year), "all" - actual interval specified
	 * in during portfolio creation. Default value is "1d" - one trading day.
	 * </p>
	 * 
	 * <p>
	 * key="txnCostPerShare" - Amount of transactional costs per share. Defaults
	 * to 0.
	 * </p>
	 * 
	 * <p>
	 * key="txnCostFixed" - Amount of fixed costs per transaction.
	 * <p/>
	 * 
	 * @param key
	 * @param value
	 */
	public Portfolio settings(String key, String value) {
		portfolio.setParam(key, value);
		return this;
	}

	/**
	 * Get portfolio params.
	 * 
	 * Advanced settings that regulate how porfolio metrics are computed,
	 * returned and stored.
	 * <p>
	 * Default: portfolioMetricsMode="portfolio", windowLength = "1d",
	 * holdingPeriodsOnly = FALSE, shortSalesMode = "
	 * lintner", jumpsModel = "moments
	 * ", noiseModel = TRUE, fractalPriceModel=TRUE, factorModel = "
	 * sim", densityModel=" GLD", driftTerm=TRUE, resultsSamplingInterval = "1s
	 * ", inputSamplingInterval=" none", timeScale="1d", txnCostPerShare=0,
	 * txnCostFixed=0
	 * </p>
	 * 
	 * <p>
	 * key="portfolioMetricsMode" Used to select method of computing portfolio
	 * metrics. Available modes are: "portfolio" - risk and performance metrics
	 * are computed based on the history of position rebalancing (see
	 * windowLength parameter) and should be used to backtest and compare
	 * trading strategies of different frequency and style, "price" - metrics
	 * are always computed without a history of previous rebalancing (classic
	 * interpretation). Defaults to "portfolio".
	 * </p>
	 *
	 * <p>
	 * key="windowLength" - Rolling window length for metric estimations and
	 * position rebalancing history. Available interval values are: "Xs" -
	 * seconds, "Xm" - minutes, "Xh" - hours, "Xd" - trading days (6.5 hours in
	 * a trading day), "Xw" - weeks (5 trading days in 1 week), "Xmo" - month
	 * (21 trading day in 1 month), "Xy" - years (256 trading days in 1 year),
	 * "all" - all observations are used. Default value is "1d" - one trading
	 * day.
	 * </p>
	 * 
	 * <p>
	 * key="holdingPeriodsOnly - Used when portfolioMetricsMode = "portfolio".
	 * Defaults to FALSE, which means that trading strategy risk and performance
	 * metrics will be scaled to include intervals when trading strategy did not
	 * have market exposure. When TRUE, trading strategy metrics are scaled
	 * based on actual holding intervals when there was exposure to the market.
	 * </p>
	 * 
	 * <p>
	 * key="shortSalesMode" - Used to specify how position weights are computed.
	 * Available modes are: "lintner" - the sum of absolute weights is equal to
	 * 1 (Lintner assumption), "markowitz" - the sum of weights must equal to 1
	 * (Markowitz assumption). Defaults to "lintner", which implies that the sum
	 * of absolute weights is used to normalize investment weights.
	 * </p>
	 * 
	 * <p>
	 * key="jumpsModel" - Used to select jump filtering mode when computing
	 * return statistics. Available modes are: "none" - price jumps are not
	 * filtered anywhere, "moments" - price jumps are filtered only when
	 * computing moments (variance, skewness, kurtosis) and derived metrics,
	 * "all" - price jumps are filtered everywhere. Defaults to "moments", which
	 * implies that only return moments and related metrics would be using
	 * jump-filtered returns in their calculations.
	 * </p>
	 * 
	 * <p>
	 * key="noiseModel" - Used to enable microstructure noise model of
	 * distribution returns. Defaults to TRUE, which implies that microstructure
	 * effects are modeled and resulting HF noise is removed from metric
	 * calculations.
	 * </p>
	 * 
	 * <p>
	 * key="fractalPriceModel" - Used to enable mono-fractal price assumptions
	 * (fGBM) when time scaling return moments. Defaults to TRUE, which implies
	 * that computed Hurst exponent is used to scale return moments. When FALSE,
	 * price is assumed to follow regular GBM with Hurst exponent = 0.5.
	 * </p>
	 * 
	 * <p>
	 * key="factorModel" - Used to select factor model for computing portfolio
	 * metrics. Available models are: "sim" - portfolio metrics are computed
	 * using the Single Index Model, "direct" - portfolio metrics are computed
	 * using portfolio value itself. Defaults to "sim", which implies that the
	 * Single Index Model is used to compute portfolio metrics./
	 * <p>
	 * 
	 * 
	 * <p>
	 * key="densityModel" - Used to select density approximation model of return
	 * distribution. Available models are: "GLD" - Generalized Lambda
	 * Distribution, "CORNER_FISHER" - Corner-Fisher approximation, "NORMAL" -
	 * Gaussian distribution. Defaults to "GLD", which would fit a broad range
	 * of distribution shapes.
	 * </p>
	 * 
	 * <p>
	 * key="driftTerm" - Used to enable drift term (expected return) when
	 * computing probability density approximation and related metrics (e.g.
	 * CVaR, Omega Ratio, etc.). Defaults to FALSE, which implies that
	 * distribution is centered around zero return.
	 * </p>
	 * 
	 * <p>
	 * key="resultsNAFilter" - Used to enable filtering of NA values in computed
	 * results. Defaults to TRUE, which implies that output results have all NA
	 * values removed.
	 * </p>
	 * 
	 * <p>
	 * key="resultsSamplingInterval" - Interval to be used for sampling computed
	 * results before returning them to the caller. Available interval values
	 * are: "Xs" - seconds, "Xm" - minutes, "Xh" - hours, "Xd" - trading days
	 * (6.5 hours in a trading day), "Xw" - weeks (5 trading days in 1 week),
	 * "Xmo" - month (21 trading day in 1 month), "Xy" - years (256 trading days
	 * in 1 year), "last" - last result in a series is returned, "none" - no
	 * sampling. Large sampling interval would produce smaller vector of results
	 * and would require less time spent on data transfer. Default value of "1s"
	 * indicates that data is returned for every second during trading hours.
	 * </p>
	 * 
	 * <p>
	 * key="inputSamplingInterval" - Interval to be used as a minimum step for
	 * sampling input prices. Available interval values are: "Xs" - seconds,
	 * "Xm" - minutes, "Xh" - hours, "Xd" - trading days (6.5 hours in a trading
	 * day), "Xw" - weeks (5 trading days in 1 week), "Xmo" - month (21 trading
	 * day in 1 month), "Xy" - years (256 trading days in 1 year), "none" - no
	 * sampling. Default value is "none", which indicates that no sampling is
	 * applied.
	 * </p>
	 * 
	 * <p>
	 * key="timeScale" - Interval to be used for scaling return distribution
	 * statistics and producing metrics forecasts at different horizons.
	 * Available interval values are: "Xs" - seconds, "Xm" - minutes, "Xh" -
	 * hours, "Xd" - trading days (6.5 hours in a trading day), "Xw" - weeks (5
	 * trading days in 1 week), "Xmo" - month (21 trading day in 1 month), "Xy"
	 * - years (256 trading days in 1 year), "all" - actual interval specified
	 * in during portfolio creation. Default value is "1d" - one trading day.
	 * </p>
	 * 
	 * <p>
	 * key="txnCostPerShare" - Amount of transactional costs per share. Defaults
	 * to 0.
	 * </p>
	 * 
	 * <p>
	 * key="txnCostFixed" - Amount of fixed costs per transaction.
	 * <p/>
	 * 
	 * @param key
	 * @return value
	 */

	public String settings(String key) {
		return portfolio.getParam(key);
	}

	public HashMap<String, String> settings() {

		return portfolio.getPortfolioSettingsR();
	}

	public void settings(HashMap<String, String> map) {

		portfolio.setPortfolioSettings(map);
	}

	// ---------------Metrics---------------------

	private Metric buildMetric(String metric) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName(metric);
		return new Metric(builder.build(portfolio));
	}

	private Metric buildMetricCI(String metric, double confidenceInterval) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName(metric);
		builder.setConfidenceInterval(confidenceInterval);
		return new Metric(builder.build(portfolio));
	}

	private Metric buildMetricCI(String metric, double confidenceIntervalA, double confidenceIntervalB) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName(metric);
		builder.setConfidenceIntervalAlphaBeta(confidenceIntervalA, confidenceIntervalB);
		return new Metric(builder.build(portfolio));
	}

	private Metric buildMetricTR(String metric, double thresholdReturn) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName(metric);
		builder.setThresholdReturn(thresholdReturn);
		return new Metric(builder.build(portfolio));
	}

	/**
	 * 
	 * Creates monetary value of a portfolio from the beginning of the holding
	 * period.
	 */
	public Metric value() {
		return buildMetric("VALUE");
	}

	/**
	 * 
	 * Creates portfolio log_return from the beginning of the holding period.
	 */
	public Metric log_return() {
		return buildMetric("RETURN");
	}

	/**
	 * 
	 * Creates portfolio cumulative expected return.
	 */
	public Metric expected_return() {
		return buildMetric("EXPECTED_RETURN");
	}

	/**
	 * 
	 * Creates profit for the selected symbol in the portfolio.
	 */
	public Metric profit() {
		return buildMetric("PROFIT");
	}

	/**
	 * 
	 * Creates portfolio beta (market sensitivity) according to the Single Index
	 * Model.
	 */
	public Metric beta() {
		return buildMetric("BETA");
	}

	/**
	 * 
	 * Creates portfolio alpha (ex-ante) according to the Single Index Model.
	 */
	public Metric alpha_exante() {
		return buildMetric("ALPHA");
	}

	/**
	 * 
	 * Creates variance of portfolio returns.
	 */
	public Metric variance() {
		return buildMetric("VARIANCE");
	}

	/**
	 * 
	 * Creates Creates maximum drawdown of portfolio returns.
	 */
	public Metric max_drawdown() {
		return buildMetric("MAX_DRAWDOWN");
	}

	/**
	 * 
	 * Creates Calmar ratio (cumulative return to maximum drawdown) of a
	 * portfolio
	 */
	public Metric calmar_ratio() {
		return buildMetric("CALMAR_RATIO");
	}

	/**
	 * 
	 * Creates portfolio Value-at-Risk at a given confidence interval.
	 * Computation employs distribution's skewness and kurtosis to account for
	 * non-normality.
	 */
	public Metric value_at_risk(double confidenceInterval) {
		return buildMetricCI("VAR", confidenceInterval);
	}

	/**
	 * 
	 * Creates portfolio conditional Value-at-Risk (Expected Tail Loss) at a
	 * given confidence interval. Computation employs distribution's skewness
	 * and kurtosis to account for non-normality.
	 */
	public Metric expected_shortfall(double confidenceInterval) {
		return buildMetricCI("CVAR", confidenceInterval);
	}

	/**
	 * 
	 * Creates modified Sharpe ratio of a portfolio at a given confidence
	 * interval. Computation employs distribution skewness and kurtosis to
	 * account for non-normality.
	 */
	public Metric mod_sharpe_ratio(double confidenceInterval) {
		return buildMetricCI("SHARPE_RATIO_MOD", confidenceInterval);
	}

	/**
	 * 
	 * Creates Stable Tail Adjusted Return Ratio (STARR) of a portfolio at a
	 * given confidence interval. Computation employs distribution's skewness
	 * and kurtosis to account for non-normality.
	 */
	public Metric starr_ratio(double confidenceInterval) {
		return buildMetricCI("STARR_RATIO",confidenceInterval);
	}

	/**
	 * 
	 * Creates Sharpe Ratio of a portfolio.
	 */
	public Metric sharpe_ratio() {
		return buildMetric("SHARPE_RATIO");
	}

	/**
	 * 
	 * Creates Treynor Ratio of a portfolio.
	 */
	public Metric treynor_ratio() {
		return buildMetric("TREYNOR_RATIO");
	}

	/**
	 * 
	 * Creates skewness of portfolio returns.
	 */
	public Metric skewness() {
		return buildMetric("SKEWNESS");
	}

	/**
	 * 
	 * Creates kurtosis of portfolio returns.
	 */
	public Metric kurtosis() {
		return buildMetric("KURTOSIS");
	}

	/**
	 * 
	 * Creates information ratio of a portfolio.
	 */
	public Metric information_ratio() {
		return buildMetric("INFORMATION_RATIO");
	}

	/**
	 * 
	 * Creates portfolio Jensen's alpha (excess return) according to the Single
	 * Index Model.
	 */
	public Metric alpha_jensens() {
		return buildMetric("ALPHA_JENSEN");
	}

	/**
	 * 
	 * Creates Omega Ratio of a portfolio. Computation employs distribution's
	 * skewness and kurtosis to account for non-normality.
	 */
	public Metric omega_ratio(double thresholdReturn) {
		return buildMetricTR("OMEGA_RATIO", thresholdReturn);
	}

	/**
	 * 
	 * Creates Rachev ratio of a portfolio at given confidence intervals.
	 * Computation employs distribution skewness and kurtosis to account for
	 * non-normality.
	 */
	public Metric rachev_ratio(double confidenceIntervalA, double confidenceIntervalB) {
		return buildMetricCI("RACHEV_RATIO", confidenceIntervalA, confidenceIntervalB);
	}

	/**
	 * 
	 * Creates gain variance of portfolio returns.
	 */
	public Metric gain_variance() {
		return buildMetric("GAIN_VARIANCE");
	}

	/**
	 * 
	 * Creates loss variance of portfolio returns.
	 */
	public Metric loss_variance() {
		return buildMetric("LOSS_VARIANCE");
	}

	/**
	 * 
	 * Creates downside variance of portfolio returns.
	 */
	public Metric downside_variance(double thresholdReturn) {
		return buildMetricTR("DOWNSIDE_VARIANCE", thresholdReturn);
	}

	/**
	 * 
	 * Creates upside variance of portfolio returns.
	 */
	public Metric upside_variance(double thresholdReturn) {
		return buildMetricTR("UPSIDE_VARIANCE", thresholdReturn);
	}

	/**
	 * 
	 * Creates portfolio cumulative expected return below a certain threshold.
	 */
	public Metric expected_downside_return(double thresholdReturn) {
		return buildMetricTR("EXPECTED_DOWNSIDE_THRESHOLD_RETURN", thresholdReturn);
	}

	/**
	 * 
	 * Creates portfolio cumulative expected return above a certain threshold.
	 */
	public Metric expected_upside_return(double thresholdReturn) {
		return buildMetricTR("EXPECTED_UPSIDE_THRESHOLD_RETURN", thresholdReturn);
	}

	/**
	 * 
	 * Creates portfolio Hurst exponent as a weighted sum of the Hurst exponents
	 * of its position returns.
	 */
	public Metric hurst_exponent() {
		return buildMetric("HURST_EXPONENT");
	}

	/**
	 * 
	 * Creates portfolio fractal dimension as a weighted sum of fractal
	 * dimensions of its position returns.
	 */
	public Metric fractal_dimension() {
		return buildMetric("FRACTAL_DIMENSION");
	}

	/**
	 * 
	 * Creates monetary value of accumulated portfolio transactional costs.
	 */
	public Metric txn_costs() {
		return buildMetric("TRANSACTION_COSTS_SIZE");
	}

	/**
	 * 
	 * Creates Sortino ratio of a portfolio.
	 */
	public Metric sortino_ratio(double thresholdReturn) {
		return buildMetricTR("SORTINO_RATIO", thresholdReturn);
	}

	/**
	 * 
	 * Creates upside to downside variance ratio of a portfolio.
	 */
	public Metric upside_downside_variance_ratio(double thresholdReturn) {
		return buildMetricTR("UPSIDE_DOWNSIDE_VARIANCE_RATIO", thresholdReturn);
	}

	/**
	 * 
	 * Creates gain to loss variance ratio of portfolio returns.
	 */
	public Metric gain_loss_variance_ratio() {
		return buildMetric("GAIN_LOSS_VARIANCE_RATIO");
	}

	/**
	 * 
	 * Creates down capture ratio of a portfolio.
	 */
	public Metric down_capture_ratio() {
		return buildMetric("DOWN_CAPTURE_RATIO");
	}

	/**
	 * 
	 * Creates up capture ratio of a portfolio.
	 */
	public Metric up_capture_ratio() {
		return buildMetric("UP_CAPTURE_RATIO");
	}

	/**
	 * 
	 * Creates down number ratio of a portfolio.
	 */
	public Metric down_number_ratio() {
		return buildMetric("DOWN_NUMBER_RATIO");
	}

	/**
	 * 
	 * Creates up number ratio of a portfolio.
	 */
	public Metric up_number_ratio() {
		return buildMetric("UP_NUMBER_RATIO");
	}

	/**
	 * 
	 * Creates down percentage ratio of portfolio returns.
	 */
	public Metric down_percentage_ratio() {
		return buildMetric("DOWN_PERCENTAGE_RATIO");
	}

	/**
	 * 
	 * Creates up percentage ratio of a portfolio.
	 */
	public Metric up_percentage_ratio() {
		return buildMetric("UP_PERCENTAGE_RATIO");
	}

	/**
	 * 
	 * Creates N-th cumulant of portfolio return distribution.
	 * 
	 * @param order
	 *            moment order (1 or 4)
	 */
	public Metric cumulant(int order) {
		return buildMetric("CUMULANT" + order);
	}

	/**
	 * 
	 * Creates N-th order central moment of portfolio return distribution.
	 * 
	 * @param order
	 *            moment order (1 or 4)
	 */
	public Metric moment(int order) {
		return buildMetric("MOMENT" + order);
	}
	
	/**
	 * 
	 * The goal of optimization is only to satisfy the all constraints.
	 * 
	 * @return 
	 */
	public Metric constraints_only() {
		return buildMetric("CONTRAINTS_ONLY");
	}
	
	/**
	 * No optimization is performed and constraints are not processes.
	 *	Portfolio positions are returned with equal weights
  	 * 
	 * @return
	 */
	public Metric equiweight() {
		return buildMetric("EQUIWEIGHT");
	}
	
	
	
	

	/**
	 * Returns a list of portfolio symbols with non-zero weights.
	 * 
	 * @return
	 */
	public String[] symbols() {
		return portfolio.getSymbols();
	}

	/**
	 * Returns position for a given symbol if this position is found inside a
	 * given portfolio
	 * 
	 * @param symbol
	 * @return
	 */
	public Position get_position(String symbol) {
		com.portfolioeffect.quant.client.portfolio.Position position = portfolio.getPosition(symbol);
		if (position != null)
			return new Position(position);

		return null;
	}

	/**
	 * 
	 * @return List of symbols, exchanges and description
	 * @throws Exception
	 */
	public List<String[]> symbols_available() throws Exception {

		com.portfolioeffect.quant.client.result.Metric methodResult = portfolio.getAllSymbolsList();

		if (methodResult.hasError())
			throw new Exception(methodResult.getErrorMessage());

		ArrayList<String[]> list = new ArrayList<String[]>();

		list.add(methodResult.getStringArray("id"));
		list.add(methodResult.getStringArray("exchange"));
		list.add(methodResult.getStringArray("description"));
		
		return list;
	}
	
	public com.portfolioeffect.quant.client.portfolio.Portfolio getPortfolio() {
		return portfolio;
	}

}
