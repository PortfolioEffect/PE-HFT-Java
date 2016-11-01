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

import java.util.ArrayList;
import java.util.List;

import com.portfolioeffect.quant.client.util.SimpleLazyMetricBuilder;

public class Position {
	private com.portfolioeffect.quant.client.portfolio.Position position;


	public Position(com.portfolioeffect.quant.client.portfolio.Position position) {
		this.position = position;
	}

	// ---------------Metrics---------------------

	private Metric buildMetric(String metric) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName(metric);
		return new Metric(builder.build(position));
	}

	private Metric buildMetricCI(String metric, double confidenceInterval) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName(metric);
		builder.setConfidenceInterval(confidenceInterval);
		return new Metric(builder.build(position));
	}

	private Metric buildMetricCI(String metric, double confidenceIntervalA, double confidenceIntervalB) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName(metric);
		builder.setConfidenceIntervalAlphaBeta(confidenceIntervalA, confidenceIntervalB);
		return new Metric(builder.build(position));
	}

	private Metric buildMetricTR(String metric, double thresholdReturn) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName(metric);
		builder.setThresholdReturn(thresholdReturn);
		return new Metric(builder.build(position));
	}

	/**
	 * 
	 * Creates monetary value of a position from the beginning of the holding
	 * period.
	 */
	public Metric value() {
		return buildMetric("VALUE");
	}

	/**
	 * 
	 * Creates position log_return from the beginning of the holding period.
	 */
	public Metric log_return() {
		return buildMetric("RETURN");
	}

	/**
	 * 
	 * Creates position cumulative expected return.
	 */
	public Metric expected_return() {
		return buildMetric("EXPECTED_RETURN");
	}

	/**
	 * 
	 * Creates profit for the position.
	 */
	public Metric profit() {
		return buildMetric("PROFIT");
	}

	/**
	 * 
	 * Creates position beta (market sensitivity) according to the Single Index
	 * Model.
	 */
	public Metric beta() {
		return buildMetric("BETA");
	}

	/**
	 * 
	 * Creates position alpha (ex-ante) according to the Single Index Model.
	 */
	public Metric alpha_exante() {
		return buildMetric("ALPHA");
	}

	/**
	 * 
	 * Creates variance of position returns.
	 */
	public Metric variance() {
		return buildMetric("VARIANCE");
	}

	/**
	 * 
	 * Creates Creates maximum drawdown of position returns.
	 */
	public Metric max_drawdown() {
		return buildMetric("MAX_DRAWDOWN");
	}

	/**
	 * 
	 * Creates Calmar ratio (cumulative return to maximum drawdown) of a
	 * position
	 */
	public Metric calmar_ratio() {
		return buildMetric("CALMAR_RATIO");
	}

	/**
	 * 
	 * Creates position Value-at-Risk at a given confidence interval.
	 * Computation employs distribution's skewness and kurtosis to account for
	 * non-normality.
	 */
	public Metric value_at_risk(double confidenceInterval) {
		return buildMetricCI("VAR", confidenceInterval);
	}

	/**
	 * 
	 * Creates position conditional Value-at-Risk (Expected Tail Loss) at a
	 * given confidence interval. Computation employs distribution's skewness
	 * and kurtosis to account for non-normality.
	 */
	public Metric expected_shortfall(double confidenceInterval) {
		return buildMetricCI("CVAR", confidenceInterval);
	}

	/**
	 * 
	 * Creates modified Sharpe ratio of a position at a given confidence
	 * interval. Computation employs distribution skewness and kurtosis to
	 * account for non-normality.
	 */
	public Metric mod_sharpe_ratio(double confidenceInterval) {
		return buildMetricCI("SHARPE_RATIO_MOD", confidenceInterval);
	}

	/**
	 * 
	 * Creates Stable Tail Adjusted Return Ratio (STARR) of a position at a
	 * given confidence interval. Computation employs distribution's skewness
	 * and kurtosis to account for non-normality.
	 */
	public Metric starr_ratio(double confidenceInterval) {
		return buildMetricCI("STARR_RATIO",confidenceInterval);
	}

	/**
	 * 
	 * Creates Sharpe Ratio of a position.
	 */
	public Metric sharpe_ratio() {
		return buildMetric("SHARPE_RATIO");
	}

	/**
	 * 
	 * Creates Treynor Ratio of a position.
	 */
	public Metric treynor_ratio() {
		return buildMetric("TREYNOR_RATIO");
	}

	/**
	 * 
	 * Creates skewness of position returns.
	 */
	public Metric skewness() {
		return buildMetric("SKEWNESS");
	}

	/**
	 * 
	 * Creates kurtosis of position returns.
	 */
	public Metric kurtosis() {
		return buildMetric("KURTOSIS");
	}

	/**
	 * 
	 * Creates information ratio of a position.
	 */
	public Metric information_ratio() {
		return buildMetric("INFORMATION_RATIO");
	}

	/**
	 * 
	 * Creates position Jensen's alpha (excess return) according to the Single
	 * Index Model.
	 */
	public Metric alpha_jensens() {
		return buildMetric("ALPHA_JENSEN");
	}

	/**
	 * 
	 * Creates Omega Ratio of a position. Computation employs distribution's
	 * skewness and kurtosis to account for non-normality.
	 */
	public Metric omega_ratio(double thresholdReturn) {
		return buildMetricTR("OMEGA_RATIO", thresholdReturn);
	}

	/**
	 * 
	 * Creates Rachev ratio of a position at given confidence intervals.
	 * Computation employs distribution skewness and kurtosis to account for
	 * non-normality.
	 */
	public Metric rachev_ratio(double confidenceIntervalA, double confidenceIntervalB) {
		return buildMetricCI("RACHEV_RATIO", confidenceIntervalA, confidenceIntervalB);
	}

	/**
	 * 
	 * Creates gain variance of position returns.
	 */
	public Metric gain_variance() {
		return buildMetric("GAIN_VARIANCE");
	}

	/**
	 * 
	 * Creates loss variance of position returns.
	 */
	public Metric loss_variance() {
		return buildMetric("LOSS_VARIANCE");
	}

	/**
	 * 
	 * Creates downside variance of position returns.
	 */
	public Metric downside_variance(double thresholdReturn) {
		return buildMetricTR("DOWNSIDE_VARIANCE", thresholdReturn);
	}

	/**
	 * 
	 * Creates upside variance of position returns.
	 */
	public Metric upside_variance(double thresholdReturn) {
		return buildMetricTR("UPSIDE_VARIANCE", thresholdReturn);
	}

	/**
	 * 
	 * Creates position cumulative expected return below a certain threshold.
	 */
	public Metric expected_downside_return(double thresholdReturn) {
		return buildMetricTR("EXPECTED_DOWNSIDE_THRESHOLD_RETURN", thresholdReturn);
	}

	/**
	 * 
	 * Creates position cumulative expected return above a certain threshold.
	 */
	public Metric expected_upside_return(double thresholdReturn) {
		return buildMetricTR("EXPECTED_UPSIDE_THRESHOLD_RETURN", thresholdReturn);
	}

	/**
	 * 
	 * Creates Hurst exponent of position returns.
	 */
	public Metric hurst_exponent() {
		return buildMetric("HURST_EXPONENT");
	}

	/**
	 * 
	 * Creates fractal dimension of position returns.
	 */
	public Metric fractal_dimension() {
		return buildMetric("FRACTAL_DIMENSION");
	}

	/**
	 * 
	 * Creates monetary value of accumulated position transactional costs.
	 */
	public Metric txn_costs() {
		return buildMetric("TRANSACTION_COSTS_SIZE");
	}

	/**
	 * 
	 * Creates Sortino ratio of a position.
	 */
	public Metric sortino_ratio(double thresholdReturn) {
		return buildMetricTR("SORTINO_RATIO", thresholdReturn);
	}

	/**
	 * 
	 * Creates upside to downside variance ratio of a position.
	 */
	public Metric upside_downside_variance_ratio(double thresholdReturn) {
		return buildMetricTR("UPSIDE_DOWNSIDE_VARIANCE_RATIO", thresholdReturn);
	}

	/**
	 * 
	 * Creates gain to loss variance ratio of position returns.
	 */
	public Metric gain_loss_variance_ratio() {
		return buildMetric("GAIN_LOSS_VARIANCE_RATIO");
	}

	/**
	 * 
	 * Creates down capture ratio of a position.
	 */
	public Metric down_capture_ratio() {
		return buildMetric("DOWN_CAPTURE_RATIO");
	}

	/**
	 * 
	 * Creates up capture ratio of a position.
	 */
	public Metric up_capture_ratio() {
		return buildMetric("UP_CAPTURE_RATIO");
	}

	/**
	 * 
	 * Creates down number ratio of a position.
	 */
	public Metric down_number_ratio() {
		return buildMetric("DOWN_NUMBER_RATIO");
	}

	/**
	 * 
	 * Creates up number ratio of a position.
	 */
	public Metric up_number_ratio() {
		return buildMetric("UP_NUMBER_RATIO");
	}

	/**
	 * 
	 * Creates down percentage ratio of position returns.
	 */
	public Metric down_percentage_ratio() {
		return buildMetric("DOWN_PERCENTAGE_RATIO");
	}

	/**
	 * 
	 * Creates up percentage ratio of a position.
	 */
	public Metric up_percentage_ratio() {
		return buildMetric("UP_PERCENTAGE_RATIO");
	}

	/**
	 * 
	 * Creates N-th cumulant of position return distribution.
	 * 
	 * @param order
	 *            moment order (1 or 4)
	 */
	public Metric cumulant(int order) {
		return buildMetric("CUMULANT" + order);
	}

	/**
	 * 
	 * Creates N-th order central moment of position return distribution.
	 * 
	 * @param order
	 *            moment order (1 or 4)
	 */
	public Metric moment(int order) {
		return buildMetric("MOMENT" + order);
	}

	/**
	 * Sets new position quantity.
	 * 
	 * @param quantity
	 */
	public void set_quantity(int quantity) {

		position.setPositionQuantity(quantity);
	}

	/**
	 * Sets new position quantity.
	 * 
	 * @param quantity
	 * @param timeQuantity
	 */
	public void set_quantity(int[] quantity, long[] timeQuantity) {

		position.setPositionQuantity(quantity, timeQuantity);
	}

	/**
	 * Sets new position quantity.
	 * 
	 * @param quantity
	 * @param timeQuantity
	 */
	public void set_quantity(int[] quantity, String[] timeQuantity) {

		position.setPositionQuantity(quantity, timeQuantity);
	}

	/**
	 * Returns total number of shares associated with the given symbol in this
	 * portfolio.
	 * 
	 * @return
	 */
	public Metric quantity() {

		return buildMetric("QUANTITY");
	}

	/**
	 * Returns position price.
	 * 
	 * @return
	 */
	public Metric price() {

		return buildMetric("PRICE");
	}

	/**
	 * Creates ratio of a monetary position value to the monetary value of the
	 * whole portfolio. Expressed in decimal points of portfolio value.
	 * 
	 * @return
	 */
	public Metric weight() {

		return buildMetric("WEIGHT");
	}

	/**
	 * Creates autocovariance of position returns for a certain time lag.
	 * 
	 * @return
	 */
	public Metric return_autocovariance(int lag) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName("RETURN_AUTOCOVARIANCE");
		builder.setLag(lag);
		return new Metric(builder.build(position));

	}

	/**
	 * Creates correlation between current position and positionB.
	 * 
	 * @return
	 */
	public Metric correlation(Position positionB) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName("CORRELATION");

		return new Metric(builder.build(position, positionB.position));

	}

	/**
	 * Creates covariance between current position and positionB.
	 * 
	 * @return
	 */
	public Metric covariance(Position positionB) {
		SimpleLazyMetricBuilder builder = new SimpleLazyMetricBuilder();
		builder.setMetricName("COVARIANCE");

		return new Metric(builder.build(position, positionB.position));

	}
	
	public com.portfolioeffect.quant.client.portfolio.Position getPosition() {
		return position;
	}


}
