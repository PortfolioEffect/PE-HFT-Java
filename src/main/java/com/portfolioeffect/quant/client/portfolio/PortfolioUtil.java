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
package com.portfolioeffect.quant.client.portfolio;

import java.util.List;

import com.portfolioeffect.quant.client.ClientConnection;
import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.model.TimeValue;
import com.portfolioeffect.quant.client.model.TimeValuePDF;
import com.portfolioeffect.quant.client.portfolio.optimizer.ForecastedValues;
import com.portfolioeffect.quant.client.portfolio.optimizer.PortfolioOptimizer;
import com.portfolioeffect.quant.client.portfolio.optimizer.StrategyOptimizer;
import com.portfolioeffect.quant.client.result.MethodResult;
import com.portfolioeffect.quant.client.type.MetricsType;
import com.portfolioeffect.quant.client.util.DateTimeUtil;
import com.portfolioeffect.quant.client.util.MetricStringBuilder;

public class PortfolioUtil {
	
	private static boolean isNaNFiltered=true;
	
	public static boolean isNaNFiltered() {
		return isNaNFiltered;
	}

	public static void setNaNFiltered(boolean isNaNFiltered) {
		PortfolioUtil.isNaNFiltered = isNaNFiltered;
	}

	public static boolean isNaN2Zero() {
		return isNaN2Zero;
	}

	public static void setNaN2Zero(boolean isNaN2Zero) {
		PortfolioUtil.isNaN2Zero = isNaN2Zero;
	}

	private static boolean isNaN2Zero=false;


	private static ClientConnection client = new ClientConnection();

	public static void util_setCredentials(String userName, String password, String apiKey) {
		util_setCredentials(userName, password, apiKey, "snowfall04.snowfallsystems.com");
	}

	public static void util_setCredentials(String userName, String password, String apiKey, String hostName) {

		client.stop();
		client.setUsername(userName);
		client.setPassword(password);
		client.setApiKey(apiKey);
		client.setHost(hostName);

	}
	public static void util_setCredentials(ClientConnection client, String userName, String password, String apiKey, String hostName) {

		client.stop();
		client.setUsername(userName);
		client.setPassword(password);
		client.setApiKey(apiKey);
		client.setHost(hostName);

	}

	public static void util_setCredentials(ClientConnection clientA) {

		client = clientA;

	}

	public static Portfolio portfolio_create(String fromTime, String toTime, String indexSymbol) throws ComputeErrorException {
		Portfolio portfolio = new Portfolio(client);

		checkResult(portfolio.setFromTime(fromTime));
		checkResult(portfolio.setToTime(toTime));
		checkResult(portfolio.addIndex(indexSymbol));

		return portfolio;
	}
	
	public static Portfolio portfolio_create(ClientConnection client, String fromTime, String toTime, String indexSymbol) throws ComputeErrorException {
		Portfolio portfolio = new Portfolio(client);

		checkResult(portfolio.setFromTime(fromTime));
		checkResult(portfolio.setToTime(toTime));
		checkResult(portfolio.addIndex(indexSymbol));

		return portfolio;
	}


	public static Portfolio portfolio_create(double indexPrice[], long indexTimeMillisec[]) throws ComputeErrorException {
		Portfolio portfolio = new Portfolio(client);

		checkResult(portfolio.addIndex(indexPrice, indexTimeMillisec));

		return portfolio;
	}

	public static Portfolio portfolio_create(double indexPrice[], String indexDateTime[]) throws ComputeErrorException {
		Portfolio portfolio = new Portfolio(client);

		checkResult(portfolio.addIndex(indexPrice, DateTimeUtil.toPOSIXTime(indexDateTime)));

		return portfolio;
	}

	public static void portfolio_addPosition(Portfolio portfolio, String assetName, int quantity) throws ComputeErrorException {

		checkResult(portfolio.addPosition(assetName, quantity));

	}

	public static void portfolio_addPosition(Portfolio portfolio, String assetName, int[] quantity, long[] qunatityTimeMilliSec) throws ComputeErrorException {

		checkResult(portfolio.addPosition(assetName, quantity, qunatityTimeMilliSec));

	}

	public static void portfolio_addPosition(Portfolio portfolio, String assetName, int[] quantity, String[] qunatityDataTime) throws ComputeErrorException {

		checkResult(portfolio.addPosition(assetName, quantity, DateTimeUtil.toPOSIXTime(qunatityDataTime)));

	}

	public static void portfolio_addPosition(Portfolio portfolio, String assetName, int quantity, double[] price, long[] priceTimeMillisec)
			throws ComputeErrorException {

		checkResult(portfolio.addPosition(assetName, price, quantity, priceTimeMillisec));

	}

	public static void portfolio_addPosition(Portfolio portfolio, String assetName, int quantity, double[] price, String[] priceDataTime)
			throws ComputeErrorException {

		checkResult(portfolio.addPosition(assetName, price, quantity, DateTimeUtil.toPOSIXTime(priceDataTime)));

	}

	public static void portfolio_addPosition(Portfolio portfolio, String assetName, int[] quantity, long[] qunatityTimeMilliSec, double[] price,
			long[] priceTimeMillisec) throws ComputeErrorException {

		checkResult(portfolio.addPosition(assetName, price, priceTimeMillisec, quantity, qunatityTimeMilliSec));

	}

	public static void portfolio_addPosition(Portfolio portfolio, String assetName, int[] quantity, String[] qunatityDataTime, double[] price,
			String[] priceDataTime) throws ComputeErrorException {

		checkResult(portfolio.addPosition(assetName, price, DateTimeUtil.toPOSIXTime(priceDataTime), quantity, DateTimeUtil.toPOSIXTime(qunatityDataTime)));

	}

	public static void position_setQuantity(Portfolio portfolio, String symbol, int quantity) throws ComputeErrorException {
		checkResult(portfolio.setPositionQuantity(symbol, quantity));

	}

	public static void position_setQuantity(Portfolio portfolio, String symbol, int[] quantity, long[] timeMilliSes) throws ComputeErrorException {
		checkResult(portfolio.setPositionQuantity(symbol, quantity, timeMilliSes));

	}

	public static void position_setQuantity(Portfolio portfolio, String symbol, int[] quantity, String[] dataTime) throws ComputeErrorException {

	}

	public static TimeValue position_variance(Portfolio portfolio, String symbol) throws ComputeErrorException {

		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_VARIANCE).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_hurstExponent(Portfolio portfolio) throws ComputeErrorException {

		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_HURST_EXPONENT);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_hurstExponent(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_HURST_EXPONENT).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_fractalDimension(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_FRACTAL_DIMENSION);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_fractalDimension(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_FRACTAL_DIMENSION).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_returnAutocovariance(Portfolio portfolio, int lag) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_RETURN_AUTOCOVARIANCE).setLag(lag);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_returnAutocovariance(Portfolio portfolio, String symbol, int lag) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_RETURN_AUTOCOVARIANCE).setPosition(symbol).setLag(lag);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_sortinoRatio(Portfolio portfolio, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_SORTINO_RATIO).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));
	}

	public static TimeValue portfolio_gainLossVarianceRatio(Portfolio portfolio) throws ComputeErrorException {

		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_GAIN_LOSS_VARIANCE_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_upsideDownsideVarianceRatio(Portfolio portfolio, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_UPSIDE_DOWNSIDE_VARIANCE_RATIO).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_omegaRatio(Portfolio portfolio, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_OMEGA_RATIO).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_rachevRatio(Portfolio portfolio, double confidenceIntervalA, double confidenceIntervalB) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_RACHEV_RATIO).setConfidenceIntervalAlphaBeta(confidenceIntervalA, confidenceIntervalB);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_VaR(Portfolio portfolio, double confidenceInterval) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_VAR).setConfidenceInterval(confidenceInterval);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_CVaR(Portfolio portfolio, double confidenceInterval) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_CVAR).setConfidenceInterval(confidenceInterval);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_maxDrawdown(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_MAX_DRAWDOWN);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_drawdown(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_DRAWDOWN);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_downCaptureRatio(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_DOWN_CAPTURE_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_upCaptureRatio(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_UP_CAPTURE_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_downNumberRatio(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_DOWN_NUMBER_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_upNumberRatio(Portfolio portfolio) throws ComputeErrorException {

		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_UP_NUMBER_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_downPercentageRatio(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_DOWN_PERCENTAGE_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_upPercentageRatio(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_UP_PERCENTAGE_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_calmarRatio(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_CALMAR_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_value(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_VALUE);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_return(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_RETURN);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_expectedReturn(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_EXPECTED_RETURN);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_profit(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_PROFIT);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_transactionCostsSize(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_TRANSACTION_COSTS_SIZE);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_beta(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_BETA);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_jensensAlpha(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_ALPHA_JENSEN);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_alpha(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_ALPHA);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_variance(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_VARIANCE);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_skewness(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_SKEWNESS);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_kurtosis(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_KURTOSIS);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_moment(Portfolio portfolio, int order) throws ComputeErrorException {

		MetricsType metric = null;
		switch (order) {
		case 1:
			metric = MetricsType.PORTFOLIO_MOMENT1;
			break;
		case 2:
			metric = MetricsType.PORTFOLIO_MOMENT2;
			break;
		case 3:
			metric = MetricsType.PORTFOLIO_MOMENT3;
			break;
		case 4:
			metric = MetricsType.PORTFOLIO_MOMENT4;
			break;
		default:
			throw new ComputeErrorException("Error: allowable value of the order from 1 to 4 ");
		}

		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(metric);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_cumulant(Portfolio portfolio, int order) throws ComputeErrorException {
		MetricsType metric = null;
		switch (order) {
		case 1:
			metric = MetricsType.PORTFOLIO_CUMULANT1;
			break;
		case 2:
			metric = MetricsType.PORTFOLIO_CUMULANT2;
			break;
		case 3:
			metric = MetricsType.PORTFOLIO_CUMULANT3;
			break;
		case 4:
			metric = MetricsType.PORTFOLIO_CUMULANT4;
			break;
		default:
			throw new ComputeErrorException("Error: allowable value of the order from 1 to 4 ");
		}

		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(metric);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_sharpeRatio(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_SHARPE_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_informationRatio(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_INFORMATION_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_weight(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_WEIGHT).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_modifiedSharpeRatio(Portfolio portfolio, double confidenceInterval) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_SHARPE_RATIO_MOD).setConfidenceInterval(confidenceInterval);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_starrRatio(Portfolio portfolio, double confidenceInterval) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_STARR_RATIO).setConfidenceInterval(confidenceInterval);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_treynorRatio(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_TREYNOR_RATIO);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_gainVariance(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_GAIN_VARIANCE);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_lossVariance(Portfolio portfolio) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_LOSS_VARIANCE);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_gainVariance(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_GAIN_VARIANCE).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_lossVariance(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_LOSS_VARIANCE).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_downsideVariance(Portfolio portfolio, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_DOWNSIDE_VARIANCE).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_upsideVariance(Portfolio portfolio, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_UPSIDE_VARIANCE).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_downsideVariance(Portfolio portfolio, String symbol, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_DOWNSIDE_VARIANCE).setPosition(symbol).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_upsideVariance(Portfolio portfolio, String symbol, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_UPSIDE_VARIANCE).setPosition(symbol).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_expectedUpsideThresholdReturn(Portfolio portfolio, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_EXPECTED_UPSIDE_THRESHOLD_RETURN).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue portfolio_expectedDownsideThresholdReturn(Portfolio portfolio, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.PORTFOLIO_EXPECTED_DOWNSIDE_THRESHOLD_RETURN).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_expectedUpsideThresholdReturn(Portfolio portfolio, String symbol, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_EXPECTED_UPSIDE_THRESHOLD_RETURN).setPosition(symbol).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_expectedDownsideThresholdReturn(Portfolio portfolio, String symbol, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_EXPECTED_DOWNSIDE_THRESHOLD_RETURN).setPosition(symbol).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_gainLossVarianceRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_GAIN_LOSS_VARIANCE_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_upsideDownsideVarianceRatio(Portfolio portfolio, String symbol, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_UPSIDE_DOWNSIDE_VARIANCE_RATIO).setPosition(symbol).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_rachevRatio(Portfolio portfolio, String symbol, double confidenceIntervalA, double confidenceIntervalB)
			throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_RACHEV_RATIO).setPosition(symbol).setConfidenceIntervalAlphaBeta(confidenceIntervalA, confidenceIntervalB);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_VaR(Portfolio portfolio, String symbol, double confidenceInterval) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_VAR).setPosition(symbol).setConfidenceInterval(confidenceInterval);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_CVaR(Portfolio portfolio, String symbol, double confidenceInterval) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_CVAR).setPosition(symbol).setConfidenceInterval(confidenceInterval);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_sortinoRatio(Portfolio portfolio, String symbol, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_SORTINO_RATIO).setPosition(symbol).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);
		

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_omegaRatio(Portfolio portfolio, String symbol, double thresholdReturn) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_OMEGA_RATIO).setPosition(symbol).setThresholdReturn(thresholdReturn);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_informationRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_INFORMATION_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_sharpeRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_SHARPE_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_modifiedSharpeRatio(Portfolio portfolio, String symbol, double confidenceInterval) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_SHARPE_RATIO_MOD).setPosition(symbol).setConfidenceInterval(confidenceInterval);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_starrRatio(Portfolio portfolio, String symbol, double confidenceInterval) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_STARR_RATIO).setPosition(symbol).setConfidenceInterval(confidenceInterval);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_treynorRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_TREYNOR_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_calmarRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_CALMAR_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_profit(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_PROFIT).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_transactionCostsSize(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_TRANSACTION_COSTS_SIZE).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_covariance(Portfolio portfolio, String symbol1, String symbol2) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_COVARIANCE).setPositionA(symbol1).setPositionB(symbol2);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_correlation(Portfolio portfolio, String symbol1, String symbol2) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_CORRELATION).setPositionA(symbol1).setPositionB(symbol2);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_downCaptureRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_DOWN_CAPTURE_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_upCaptureRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_UP_CAPTURE_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_downPercentageRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_DOWN_PERCENTAGE_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_upPercentageRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_UP_PERCENTAGE_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_downNumberRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_DOWN_NUMBER_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_upNumberRatio(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_UP_NUMBER_RATIO).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_maxDrawdown(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_MAX_DRAWDOWN).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_drawdown(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_DRAWDOWN).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_return(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_RETURN).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_price(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_PRICE).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_quantity(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_QUANTITY).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_value(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_VALUE).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_expectedReturn(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_EXPECTED_RETURN).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_skewness(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_SKEWNESS).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_kurtosis(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_KURTOSIS).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_moment(Portfolio portfolio, String symbol, int order) throws ComputeErrorException {
		MetricsType metric = null;
		switch (order) {
		case 1:
			metric = MetricsType.POSITION_MOMENT1;
			break;
		case 2:
			metric = MetricsType.POSITION_MOMENT2;
			break;
		case 3:
			metric = MetricsType.POSITION_MOMENT3;
			break;
		case 4:
			metric = MetricsType.POSITION_MOMENT4;
			break;
		default:
			throw new ComputeErrorException("Error: allowable value of the order from 1 to 4 ");
		}

		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(metric).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_cumulant(Portfolio portfolio, String symbol, int order) throws ComputeErrorException {
		MetricsType metric = null;
		switch (order) {
		case 1:
			metric = MetricsType.POSITION_CUMULANT1;
			break;
		case 2:
			metric = MetricsType.POSITION_CUMULANT2;
			break;
		case 3:
			metric = MetricsType.POSITION_CUMULANT3;
			break;
		case 4:
			metric = MetricsType.POSITION_CUMULANT4;
			break;
		default:
			throw new ComputeErrorException("Error: allowable value of the order from 1 to 4 ");
		}

		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(metric).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_alpha(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_ALPHA).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_jensensAlpha(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_ALPHA_JENSEN).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static TimeValue position_beta(Portfolio portfolio, String symbol) throws ComputeErrorException {
		MetricStringBuilder metricBuilder = new MetricStringBuilder();
		metricBuilder.setMetric(MetricsType.POSITION_BETA).setPosition(symbol);

		MethodResult result = portfolio.getMetric(metricBuilder.getJSON());
		result.setNaNFiltered(isNaNFiltered);
		result.setNaN2Zero(isNaN2Zero);

		checkResult(result);
		if (!result.isHasResult())
			return new TimeValue(new double[0], new long[0]);

		return new TimeValue(result.getDoubleArray("value"), result.getLongArray("time"));

	}

	public static List<String> portfolio_symbols(Portfolio portfolio) {
		return portfolio.getSymbolNamesList();
	}

	public static void portfolio_removePosition(Portfolio portfolio, String symbol) {

		portfolio.removePositionPrice(symbol);
		portfolio.removePositionQuantity(symbol);

	}

	public static TimeValuePDF portfolio_pdf(Portfolio portfolio, double pValueLeft, double pValueRight, int nPoints) throws Exception {

		MethodResult result = portfolio.getPDF(pValueLeft, pValueRight, nPoints);

		checkResult(result);

		return new TimeValuePDF(result.getDoubleMatrix("pdf"), result.getDoubleMatrix("x"), result.getLongArray("time"));
	}

	public static TimeValuePDF position_pdf(Portfolio portfolio, String symbol, double pValueLeft, double pValueRight, int nPoints) throws Exception {

		MethodResult result = portfolio.getPDF(pValueLeft, pValueRight, nPoints);

		checkResult(result);

		return new TimeValuePDF(result.getDoubleMatrix("pdf"), result.getDoubleMatrix("x"), result.getLongArray("time"));
	}

	/**
	 * 
	 * @param portfolio
	 * @param key
	 * @param value
	 * 
	 *            <p>
	 *            key = "portfolioMetricsMode" -> value = "price","portfolio"
	 *            </p>
	 * 
	 *            <p>
	 *            key = "windowLength" -> value = "Xs" X -seconds; "Xm" X
	 *            -minutes; "Xh" X - hours; "Xd" X -days; "Xw" X -weeks; "Xmo" X
	 *            - months; "Xy" X - years; * "all" - cumulative value without
	 *            window
	 *            </p>
	 * 
	 *            <p>
	 *            key = "holdingPeriodsOnly" -> value="true", "false"
	 *            </p>
	 * 
	 *            <p>
	 *            key = "shortSalesMode" -> value = "lintner" or "markowitz"
	 *            </p>
	 * 
	 *            <p>
	 *            key = jumpsModel -> value "moments" - filtering jumps happens
	 *            only in the calculation of moments and related metrics, "none"
	 *            jumps is not filtering, "all" filtering jumps happens for all
	 *            metrics including price as metrics
	 *            </p>
	 * 
	 *            <p>
	 *            key = "noiseModel" -> value="true", "false"
	 *            </p>
	 * 
	 *            <p>
	 *            key = "factorModel" -> "sim" for single index model or
	 *            "direct" for direct model
	 *            </p>
	 * 
	 *            <p>
	 *            key = "densityModel" -> "GLD" -generalized lambda
	 *            distribution, "CORNISH_FISHER" - Cornish Fisher expansion,
	 *            "NORMAL" - normal distribution
	 *            </p>
	 * 
	 *            <p>
	 *            key = "resultsSamplingInterval" -> value = "all" or - with out
	 *            sampling ; "Xs" X -seconds; "Xm" X -minutes; "Xh" X - hours;
	 *            "Xd" X -days; "Xw" X -weeks; "Xmo" X - months; "Xy" X - years;
	 *            "last" - only final result
	 *            </p>
	 * 
	 *            <p>
	 *            key = "driftTerm" -> value="true", "false"
	 *            </p>
	 * 
	 *            <p>
	 *            key = "inputSamplingInterval" -> value = "all" or - with out
	 *            sampling ; "Xs" X -seconds; "Xm" X -minutes; "Xh" X - hours;
	 *            "Xd" X -days; "Xw" X -weeks; "Xmo" X - months; "Xy" X - years;
	 *            "last" - only final result
	 *            </p>
	 * 
	 *            <p>
	 *            key = "timeScale" -> value = "Xs" X -seconds; "Xm" X -minutes;
	 *            "Xh" X - hours; "Xd" X -days; "Xw" X -weeks; "Xmo" X - months;
	 *            "Xy" X - years;
	 *            </p>
	 * 
	 * 
	 *            <p>
	 *            key = "txnCostPerShare" -> value="number"
	 *            </p>
	 * 
	 *            <p>
	 *            key = "txnCostFixed" -> value="number"
	 *            </p>
	 * 
	 *            <p>
	 *            key = "isFractalPriceModelEnabled" -> value="true" or false
	 *            </p>
	 * 
	 * @throws ComputeErrorException
	 * 
	 */
	public static void portfolio_settings(Portfolio portfolio, String key, String value) throws ComputeErrorException {

		if (key.equals("portfolioMetricsMode")) {
			portfolio.setPortfolioMetricsMode(value);
			return;
		}

		if (key.equals("windowLength")) {
			portfolio.setWindowLength(value);
			return;
		}

		if (key.equals("holdingPeriodsOnly")) {
			portfolio.setHoldingPeriodEnabled(Boolean.valueOf(value));
			return;
		}

		if (key.equals("shortSalesMode")) {
			portfolio.setShortSalesMode(value);
			return;
		}

		if (key.equals("jumpsModel")) {
			portfolio.setJumpsModel(value);
			return;
		}

		if (key.equals("noiseModel")) {
			portfolio.setNoiseModelEnabled(Boolean.valueOf(value));
			return;
		}

		if (key.equals("isFractalPriceModelEnabled")) {
			portfolio.setFractalPriceModelEnabled(Boolean.valueOf(value));
			return;
		}

		if (key.equals("factorModel")) {
			portfolio.setFactorModel(value);
			return;
		}

		if (key.equals("densityModel")) {
			portfolio.setDensityApproxModel(value);
			return;
		}

		if (key.equals("resultsSamplingInterval")) {
			portfolio.setSamplingInterval(value);
			return;
		}

		if (key.equals("driftTerm")) {
			portfolio.setDriftEnabled(Boolean.valueOf(value));
			return;
		}

		if (key.equals("inputSamplingInterval")) {
			portfolio.setPriceSamplingInterval(value);
			return;
		}

		if (key.equals("timeScale")) {
			portfolio.setTimeScale(value);
			return;
		}

		if (key.equals("txnCostPerShare")) {
			portfolio.setTxnCostPerShare(Double.valueOf(value));
			return;
		}

		if (key.equals("txnCostFixed")) {
			portfolio.setTxnCostFixed(Double.valueOf(value));
			return;
		}

		throw new ComputeErrorException("wrong key: " + key);

	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param forecastPortfolioWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastTimeStep
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastType
	 *            = "exp_smoothing" or "simple"
	 * @param forecastExponentialWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param errorInDecimalPoints
	 *            = for example 1e-6 or 1e-9 or ...
	 * @param globalOptimumProbability
	 *            = for example 0.8 or 0.99 or ....
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, String forecastPortfolioWindow,
			String forecastTimeStep, String forecastType, String forecastExponentialWindow, double errorInDecimalPoints, double globalOptimumProbability) {
		return optimization_goal(portfolio, goal, direction, 0.95, forecastPortfolioWindow, forecastTimeStep, forecastType, forecastExponentialWindow,
				errorInDecimalPoints, globalOptimumProbability);
	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param forecastPortfolioWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastTimeStep
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastType
	 *            = "exp_smoothing" or "simple"
	 * @param forecastExponentialWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, String forecastPortfolioWindow,
			String forecastTimeStep, String forecastType, String forecastExponentialWindow) {

		return optimization_goal(portfolio, goal, direction, 0.95, forecastPortfolioWindow, forecastTimeStep, forecastType, forecastExponentialWindow, 1e-12,
				0.99);

	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param forecastPortfolioWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastTimeStep
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastType
	 *            = "exp_smoothing" or "simple"
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, String forecastPortfolioWindow,
			String forecastTimeStep, String forecastType) {

		return optimization_goal(portfolio, goal, direction, 0.95, forecastPortfolioWindow, forecastTimeStep, forecastType, "5m", 1e-12, 0.99);
	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param forecastPortfolioWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastTimeStep
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, String forecastPortfolioWindow,
			String forecastTimeStep) {

		return optimization_goal(portfolio, goal, direction, 0.95, forecastPortfolioWindow, forecastTimeStep, "exp_smoothing", "5m", 1e-12, 0.99);
	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param forecastPortfolioWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, String forecastPortfolioWindow) {

		return optimization_goal(portfolio, goal, direction, 0.95, forecastPortfolioWindow, forecastPortfolioWindow, "exp_smoothing", "5m", 1e-12, 0.99);
	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction) {

		return optimization_goal(portfolio, goal, direction, 0.95, "1m", "1m", "exp_smoothing", "5m", 1e-12, 0.99);
	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param confidenceInterval
	 *            = for example 0.95
	 * @param forecastPortfolioWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastTimeStep
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastType
	 *            = "exp_smoothing" or "simple"
	 * @param forecastExponentialWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, double confidenceInterval,
			String forecastPortfolioWindow, String forecastTimeStep, String forecastType, String forecastExponentialWindow) {

		return optimization_goal(portfolio, goal, direction, confidenceInterval, forecastPortfolioWindow, forecastTimeStep, forecastType,
				forecastExponentialWindow, 1e-12, 0.99);

	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param confidenceInterval
	 *            = for example 0.95
	 * @param forecastPortfolioWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastTimeStep
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastType
	 *            = "exp_smoothing" or "simple"
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, double confidenceInterval,
			String forecastPortfolioWindow, String forecastTimeStep, String forecastType) {

		return optimization_goal(portfolio, goal, direction, confidenceInterval, forecastPortfolioWindow, forecastTimeStep, forecastType, "5m", 1e-12, 0.99);
	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param confidenceInterval
	 *            = for example 0.95
	 * @param forecastPortfolioWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastTimeStep
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, double confidenceInterval,
			String forecastPortfolioWindow, String forecastTimeStep) {

		return optimization_goal(portfolio, goal, direction, confidenceInterval, forecastPortfolioWindow, forecastTimeStep, "exp_smoothing", "5m", 1e-12, 0.99);
	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param confidenceInterval
	 *            = for example 0.95
	 * @param forecastPortfolioWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, double confidenceInterval,
			String forecastPortfolioWindow) {

		return optimization_goal(portfolio, goal, direction, confidenceInterval, forecastPortfolioWindow, forecastPortfolioWindow, "exp_smoothing", "5m",
				1e-12, 0.99);
	}

	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param confidenceInterval
	 *            = for example 0.95
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, double confidenceInterval) {

		return optimization_goal(portfolio, goal, direction, confidenceInterval, "1m", "1m", "exp_smoothing", "5m", 1e-12, 0.99);
	}

	// public static PortfolioOptimizer optimization_goal(Portfolio portfolio,
	// String goal, String direction, double confidenceInterval,
	// String forecastPortfolioWindow, String forecastTimeStep, String
	// forecastType, String forecastExponentialWindow){
	//
	// return optimization_goal(portfolio, goal, direction, confidenceInterval,
	// forecastPortfolioWindow, forecastTimeStep, forecastType,
	// forecastExponentialWindow, 1e-12, 0.99)
	//
	//
	// }
	/**
	 * 
	 * @param portfolio
	 *            -- no comments
	 * @param goal
	 *            = "Variance", "VaR", "CVaR", "ExpectedReturn", "Return",
	 *            "SharpeRatio", "ModifiedSharpeRatio", "StarrRatio",
	 *            "ContraintsOnly", "EquiWeight"
	 * @param direction
	 *            = "minimize" or "maximize"
	 * @param confidenceInterval
	 *            = for example 0.95
	 * @param forecastPortfolioWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastTimeStep
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param forecastType
	 *            = "exp_smoothing" or "simple"
	 * @param forecastExponentialWindow
	 *            = for example "10s" or "1m" or "1h" or ...
	 * @param errorInDecimalPoints
	 *            = for example 1e-6 or 1e-9 or ...
	 * @param globalOptimumProbability
	 *            = for example 0.8 or 0.99 or ....
	 * @return
	 */
	public static PortfolioOptimizer optimization_goal(Portfolio portfolio, String goal, String direction, double confidenceInterval,
			String forecastPortfolioWindow, String forecastTimeStep, String forecastType, String forecastExponentialWindow, double errorInDecimalPoints,
			double globalOptimumProbability) {
		PortfolioOptimizer optimizer;
		if (portfolio.getPortfolioMetricsMode().equals("price"))
			optimizer = new PortfolioOptimizer(portfolio);
		else {
			optimizer = new StrategyOptimizer(portfolio);
			((StrategyOptimizer) optimizer).setForecasterType(forecastType);
			((StrategyOptimizer) optimizer).setForecastPortfolioWindow(forecastPortfolioWindow);
			((StrategyOptimizer) optimizer).setForecasterType(forecastType);
			((StrategyOptimizer) optimizer).setForecastExpWindow(forecastExponentialWindow);

			ForecastedValues forecastedValues = new ForecastedValues(portfolio);
			forecastedValues.setForecastTimeStep(forecastTimeStep);
			((StrategyOptimizer) optimizer).setForecastedValue(forecastedValues);
		}

		optimizer.setOptimizationGoal(getOptimizationMetric(goal), direction, confidenceInterval);

		optimizer.setErrorInDecimalPoints(errorInDecimalPoints);
		optimizer.setGlobalOptimumProbability(globalOptimumProbability);

		return optimizer;

	}

	public static void optimization_forecast(PortfolioOptimizer optimizer, String metricType, String symbol, double[] value, long[] time) {
		ForecastedValues forecastedValues = ((StrategyOptimizer) optimizer).getForecastedValue();

		if (metricType.equals("Beta")) {
			forecastedValues.setSymbolForecastedBeta(symbol, value, time);
			return;
		}

		if (metricType.equals("ExpReturn")) {
			forecastedValues.setSymbolForecastedExpReturn(symbol, value, time);
			return;
		}

		if (metricType.equals("Variance")) {
			forecastedValues.setSymbolForecastedVariance(symbol, value, time);
			return;
		}

		if (metricType.equals("Skewness")) {
			forecastedValues.setSymbolForecastedSkewness(symbol, value, time);
			return;
		}

		if (metricType.equals("Kurtosis")) {
			forecastedValues.setSymbolForecastedKurtosis(symbol, value, time);
			return;
		}

		if (metricType.equals("Cumulant1")) {
			forecastedValues.setSymbolForecastedCumulant1(symbol, value, time);
			return;
		}

		if (metricType.equals("Cumulant2")) {
			forecastedValues.setSymbolForecastedCumulant2(symbol, value, time);
			return;
		}

		if (metricType.equals("Cumulant3")) {
			forecastedValues.setSymbolForecastedCumulant3(symbol, value, time);
			return;
		}

		if (metricType.equals("Cumulant4")) {
			forecastedValues.setSymbolForecastedCumulant4(symbol, value, time);
			return;
		}

	}

	/**
	 * for index
	 * 
	 * @param optimizer
	 * @param metricType
	 * @param value
	 * @param time
	 */
	public static void optimization_forecast(PortfolioOptimizer optimizer, String metricType, double[] value, long[] time) {
		ForecastedValues forecastedValues = ((StrategyOptimizer) optimizer).getForecastedValue();

		if (metricType.equals("Variance")) {
			forecastedValues.setIndexForecastedVariance(value, time);
			return;
		}

		if (metricType.equals("Skewness")) {
			forecastedValues.setIndexForecastedSkewness(value, time);
			return;
		}

		if (metricType.equals("Kurtosis")) {
			forecastedValues.setIndexForecastedKurtosis(value, time);
			return;
		}

		if (metricType.equals("Cumulant2")) {
			forecastedValues.setIndexForecastedCumulant2(value, time);
			return;
		}

		if (metricType.equals("Cumulant3")) {
			forecastedValues.setIndexForecastedCumulant3(value, time);
			return;
		}

		if (metricType.equals("Cumulant4")) {
			forecastedValues.setIndexForecastedCumulant4(value, time);
			return;
		}

	}

	public static Portfolio optimization_run(PortfolioOptimizer optimizer) throws Exception {

		MethodResult optimazerResultStrategy = optimizer.getOptimizedPortfolio();
		checkResult(optimazerResultStrategy);

		return optimazerResultStrategy.getPortfolio("portfolio");

	}

	public static void optimization_constraint_allWeights(PortfolioOptimizer optimizer, String constraintType, double constraintValue) {

		optimizer.addPositionConstraint("POSITION_WEIGHT", getConstraintType(constraintType), constraintValue);

	}

	public static void optimization_constraint_CVaR(PortfolioOptimizer optimizer, String constraintType, double constraintValue, double confidenceInterval) {

		optimizer.addPortfolioConstraint("CVAR", getConstraintType(constraintType), constraintValue, confidenceInterval);
	}

	public static void optimization_constraint_CVaR(PortfolioOptimizer optimizer, String constraintType, double constraintValue) {

		optimizer.addPortfolioConstraint("CVAR", getConstraintType(constraintType), constraintValue, 0.95);

	}

	public static void optimization_constraint_VaR(PortfolioOptimizer optimizer, String constraintType, double constraintValue, double confidenceInterval) {

		optimizer.addPortfolioConstraint("VAR", getConstraintType(constraintType), constraintValue, confidenceInterval);
	}

	public static void optimization_constraint_VaR(PortfolioOptimizer optimizer, String constraintType, double constraintValue) {

		optimizer.addPortfolioConstraint("VAR", getConstraintType(constraintType), constraintValue, 0.95);

	}

	public static void optimization_constraint_beta(PortfolioOptimizer optimizer, String constraintType, double constraintValue) {

		optimizer.addPortfolioConstraint("BETA", getConstraintType(constraintType), constraintValue);

	}

	public static void optimization_constraint_expectedReturn(PortfolioOptimizer optimizer, String constraintType, double constraintValue) {

		optimizer.addPortfolioConstraint("EXPECTED_RETURN", getConstraintType(constraintType), constraintValue);

	}

	public static void optimization_constraint_modifiedSharpeRatio(PortfolioOptimizer optimizer, String constraintType, double constraintValue,
			double confidenceInterval) {

		optimizer.addPortfolioConstraint("MODIFIED_SHARPE_RATIO", getConstraintType(constraintType), constraintValue, confidenceInterval);
	}

	public static void optimization_constraint_modifiedSharpeRatio(PortfolioOptimizer optimizer, String constraintType, double constraintValue) {

		optimizer.addPortfolioConstraint("MODIFIED_SHARPE_RATIO", getConstraintType(constraintType), constraintValue, 0.95);

	}

	public static void optimization_constraint_portfolioValue(PortfolioOptimizer optimizer, int constraintValue) {

		optimizer.setPortfolioValue(constraintValue);
	}

	public static void optimization_constraint_return(PortfolioOptimizer optimizer, String constraintType, double constraintValue) {

		optimizer.addPortfolioConstraint("RETURN", getConstraintType(constraintType), constraintValue);

	}

	public static void optimization_constraint_sharpeRatio(PortfolioOptimizer optimizer, String constraintType, double constraintValue) {

		optimizer.addPortfolioConstraint("SHARPE_RATIO", getConstraintType(constraintType), constraintValue);

	}

	public static void optimization_constraint_starrRatio(PortfolioOptimizer optimizer, String constraintType, double constraintValue, double confidenceInterval) {

		optimizer.addPortfolioConstraint("STARR_RATIO", getConstraintType(constraintType), constraintValue, confidenceInterval);
	}

	public static void optimization_constraint_starrRatio(PortfolioOptimizer optimizer, String constraintType, double constraintValue) {

		optimizer.addPortfolioConstraint("STARR_RATIO", getConstraintType(constraintType), constraintValue, 0.95);

	}

	public static void optimization_constraint_sumOfAbsWeights(PortfolioOptimizer optimizer, String constraintType, double constraintValue, String[] symbols) {

		optimizer.addPortfolioConstraint("POSITIONS_SUM_ABS_WEIGHT", getConstraintType(constraintType), constraintValue, symbols);

	}

	public static void optimization_constraint_variance(PortfolioOptimizer optimizer, String constraintType, double constraintValue) {

		optimizer.addPortfolioConstraint("VARIANCE", getConstraintType(constraintType), constraintValue);

	}

	public static void optimization_constraint_weight(PortfolioOptimizer optimizer, String constraintType, double constraintValue, String[] symbols) {

		optimizer.addPositionConstraint("POSITION_WEIGHT", getConstraintType(constraintType), constraintValue, symbols);

	}

	/*
	 * test this !!!
	 */
	private static String getConstraintType(String type) {

		if (type.equals("<="))
			return "lessOrEquals";

		if (type.equals("="))
			return "equals";

		if (type.equals(">="))
			return "greaterOrEquals";

		return type;
	}

	private static String getOptimizationMetric(String goal) {

		if (goal.equals("Variance"))
			return "VARIANCE";

		if (goal.equals("VaR"))
			return "VAR";

		if (goal.equals("CVaR"))
			return "CVAR";

		if (goal.equals("ExpectedReturn"))
			return "EXPECTED_RETURN";

		if (goal.equals("Return"))
			return "RETURN";

		if (goal.equals("SharpeRatio"))
			return "SHARPE_RATIO";

		if (goal.equals("ModifiedSharpeRatio"))
			return "MODIFIED_SHARPE_RATIO";

		if (goal.equals("StarrRatio"))
			return "STARR_RATIO";

		if (goal.equals("ContraintsOnly"))
			return "ZERO";

		if (goal.equals("EquiWeight"))
			return "NONE";

		return goal;

	}

	/**
	 * 
	 * @param time
	 *            = "timeMax" or "timeLeft"
	 * @return
	 * @throws ComputeErrorException
	 */
	public static int util_getComputeTime(String time) throws ComputeErrorException {

		MethodResult result = client.getComputeTimeLeft();

		checkResult(result);

		if (time.equals("timeMax"))
			return result.getValueInt("timeMax");

		return result.getValueInt("timeLeft");

	}

	private static void checkResult(MethodResult result) throws ComputeErrorException {

		if (result.hasError()) {
			throw new ComputeErrorException("Error: " + result.getErrorMessage());

		}

	}

}
