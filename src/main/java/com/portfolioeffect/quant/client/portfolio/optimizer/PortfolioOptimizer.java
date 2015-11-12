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
package com.portfolioeffect.quant.client.portfolio.optimizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.Gson;
import com.portfolioeffect.quant.client.portfolio.ArrayCache;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.result.MethodResult;
import com.portfolioeffect.quant.client.util.MessageStrings;

public class PortfolioOptimizer {

	private static final String METRIC_PORTFOLIO_OPTIMIZATION = "{\"metric\":\"PORTFOLIO_OPTIMIZATION\"}";
	protected Portfolio portfolio;
	protected String paramsString = "";
	protected ArrayList<HashMap<String, String>> paramsBuffer = new ArrayList<HashMap<String, String>>();
	private int constraintNumber = 0;
	private static AtomicLong constrainCount = new AtomicLong();

	private double errorInDecimalPoints;
	private double globalOptimumProbability;
	private int portfolioValue = -1;

	public PortfolioOptimizer(Portfolio portfolio) {
		this(portfolio, 1e-12, 0.99);
	}

	public PortfolioOptimizer(Portfolio portfolio, double errorInDecimalPoints, double globalOptimumProbability) {
		this.portfolio = portfolio;
		this.errorInDecimalPoints = errorInDecimalPoints;
		this.globalOptimumProbability = globalOptimumProbability;
	}

	// Optimization goals
	public void setOptimizationGoal(String optimizationMetric, String direction) {
		HashMap<String, String> sb = populateRequiredOptimizationGoalParams(optimizationMetric, direction);
		sb.put("confidenceInterval", "0.95");

		setOptimizationGoal(sb);
	}

	public void setOptimizationGoal(String optimizationMetric, String direction, double confidenceInterval) {
		HashMap<String, String> sb = populateRequiredOptimizationGoalParams(optimizationMetric, direction);
		sb.put("confidenceInterval", "" + confidenceInterval);

		setOptimizationGoal(sb);
	}

	private HashMap<String, String> populateRequiredOptimizationGoalParams(String optimizationMetric, String direction) {

		HashMap<String, String> sb = new HashMap<String, String>();
		sb.put("optimizationMetric", optimizationMetric);
		sb.put("direction", direction);

		return sb;
	}

	private void setOptimizationGoal(HashMap<String, String> params) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("section", "GOAL");
		map.putAll(params);
		map.put("localOptimStopStep", "" + errorInDecimalPoints);
		map.put("globalOptimProbability", "" + globalOptimumProbability);

		paramsBuffer.add(map);
	}

	public void addPortfolioConstraint(String constraintName, double expectedValue) {

		HashMap<String, String> sb = new HashMap<String, String>();

		sb.put("constraintName", constraintName);
		sb.put("expectedValue", "" + expectedValue);

		addPortfolioConstraint(sb);
	}

	public void addPortfolioConstraint(String constraintName, double[] expectedValues, long[] timeMilliSec) {

		HashMap<String, String> sb = new HashMap<String, String>();

		String userDataName = "OptimizationConstraint" + portfolio.getNewDataId();
		portfolio.addUserData(userDataName, expectedValues, timeMilliSec);

		sb.put("constraintName", constraintName);
		sb.put("expectedValueDataName", userDataName);

		addPortfolioConstraint(sb);
	}

	public void addPortfolioConstraint(String constraintName, String[] positions) {

		HashMap<String, String> sb = new HashMap<String, String>();
		sb.put("constraintName", constraintName);
		if (positions.length != 0) {
			String positionList = "";
			for (String e : positions) {
				positionList += e + "---";
			}
			sb.put("positions", positionList);

		}
		addPortfolioConstraint(sb);
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMilliSec, double confidenceInterval) {
		addPortfolioConstraint(constraintName, constraintType, expectedValues, timeMilliSec, confidenceInterval, new String[] {});
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double expectedValue, double confidenceInterval) {
		addPortfolioConstraint(constraintName, constraintType, expectedValue, confidenceInterval, new String[] {});
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double expectedValue) {
		addPortfolioConstraint(constraintName, constraintType, expectedValue, 0.95, new String[] {});
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMillisec) {
		addPortfolioConstraint(constraintName, constraintType, expectedValues, timeMillisec, 0.95, new String[] {});
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double expectedValue, double confidenceInterval, String position) {
		addPortfolioConstraint(constraintName, constraintType, expectedValue, confidenceInterval, new String[] { position });
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMillisec, double confidenceInterval,
			String position) {
		addPortfolioConstraint(constraintName, constraintType, expectedValues, timeMillisec, confidenceInterval, new String[] { position });
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double expectedValue, String[] positions) {
		addPortfolioConstraint(constraintName, constraintType, expectedValue, 0.95, positions);
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMillisec, String[] positions) {
		addPortfolioConstraint(constraintName, constraintType, expectedValues, timeMillisec, 0.95, positions);
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double expectedValue, double confidenceInterval, String[] positions) {

		HashMap<String, String> sb = new HashMap<String, String>();

		sb.put("constraintName", constraintName);
		sb.put("constraintType", constraintType);
		sb.put("confidenceInterval", "" + confidenceInterval);
		sb.put("expectedValue", "" + expectedValue);
		sb.put("expectedValueDataName", "expectedValueDataName-" + constrainCount.getAndIncrement());

		if (positions.length != 0) {
			String positionList = "";
			for (String e : positions) {
				positionList += e + "---";
			}
			sb.put("positions", positionList);

		}

		addPortfolioConstraint(sb);
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMilliSec, double confidenceInterval,
			String[] positions) {

		HashMap<String, String> sb = new HashMap<String, String>();

		String userDataName = "OptimizationConstraint" + portfolio.getNewDataId();

		portfolio.addUserData(userDataName, expectedValues, timeMilliSec);

		sb.put("constraintName", constraintName);
		sb.put("constraintType", constraintType);
		sb.put("confidenceInterval", "" + confidenceInterval);
		sb.put("expectedValueDataName", userDataName);

		if (positions.length != 0) {
			String positionList = "";
			for (String e : positions) {
				positionList += e + "---";
			}
			sb.put("positions", positionList);

		}

		addPortfolioConstraint(sb);
	}

	private void addPortfolioConstraint(HashMap<String, String> params) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("section", "CONSTRAINT_" + constraintNumber++);
		map.putAll(params);

		paramsBuffer.add(map);

	}

	public String getParamsBuffer() {
		return paramsBuffer.toString();
	}

	// position constraints
	public void addPositionConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMilliSec, String positionName) {

		String userDataName = "OptimizationConstraint" + portfolio.getNewDataId();
		portfolio.addUserData(userDataName, expectedValues, timeMilliSec);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("section", "CONSTRAINT_" + constraintNumber++);
		map.put("position", positionName);
		map.put("constraintName", constraintName);
		map.put("constraintType", constraintType);
		map.put("expectedValueDataName", userDataName);

		paramsBuffer.add(map);

	}

	public void addPositionConstraint(String constraintName, String constraintType, double expectedValue, String positionName) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("section", "CONSTRAINT_" + constraintNumber++);
		map.put("position", positionName);
		map.put("constraintName", constraintName);
		map.put("constraintType", constraintType);
		map.put("expectedValueDataName", "expectedValueDataName-" + constrainCount.getAndIncrement());
		map.put("expectedValue", "" + expectedValue);

		paramsBuffer.add(map);

	}

	public void addPositionConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMilliSec) {

		String userDataName = "OptimizationConstraint" + portfolio.getNewDataId();
		portfolio.addUserData(userDataName, expectedValues, timeMilliSec);

		for (String positionName : portfolio.getSymbols()) {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("section", "CONSTRAINT_" + constraintNumber++);
			map.put("position", positionName);
			map.put("constraintName", constraintName);
			map.put("constraintType", constraintType);
			map.put("expectedValueDataName", userDataName);

			paramsBuffer.add(map);

		}

	}

	public void addPositionConstraint(String constraintName, String constraintType, double expectedValue) {

		for (String positionName : portfolio.getSymbols()) {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("section", "CONSTRAINT_" + constraintNumber++);
			map.put("position", positionName);
			map.put("constraintName", constraintName);
			map.put("constraintType", constraintType);
			map.put("expectedValueDataName", "expectedValueDataName-" + constrainCount.getAndIncrement());
			map.put("expectedValue", "" + expectedValue);

			paramsBuffer.add(map);

		}

	}
	
	public void addPositionConstraint(String constraintName, String constraintType, double expectedValue, String[] symbols) {

		for (String positionName : symbols) {

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("section", "CONSTRAINT_" + constraintNumber++);
			map.put("position", positionName);
			map.put("constraintName", constraintName);
			map.put("constraintType", constraintType);
			map.put("expectedValueDataName", "expectedValueDataName-" + constrainCount.getAndIncrement());
			map.put("expectedValue", "" + expectedValue);

			paramsBuffer.add(map);

		}

	}


	public void addPositionConstraint(String constraintName, String positionName) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("section", "CONSTRAINT_" + constraintNumber++);
		map.put("position", positionName);
		map.put("constraintName", constraintName);

		paramsBuffer.add(map);
	}

	public MethodResult getOptimizedPortfolio() throws Exception {
		return getOptimizedPortfolio(false);
	}

	public MethodResult getOptimizedPortfolio(boolean isResultSelfPortfolio) throws Exception {

		Portfolio optimizedPortfolio;

		try {
			optimizedPortfolio = optimizationInit(isResultSelfPortfolio);
		} catch (Exception e1) {
			return new MethodResult(e1.getMessage());
		}

		return makeOptimization(optimizedPortfolio);
	}

	protected MethodResult makeOptimization(Portfolio optimizedPortfolio) throws Exception {

		MethodResult checkResult = optimizedPortfolio.addUserData("portfolioValue", new double[] { portfolioValue }, new long[] { -1 });
		if (checkResult.hasError())
			return new MethodResult(checkResult.getErrorMessage());

		MethodResult result = optimizedPortfolio.getMetric(METRIC_PORTFOLIO_OPTIMIZATION, paramsString);
		if (!result.hasError()) {

			int nSymbols = optimizedPortfolio.getSymbolNamesList().size();

			ArrayCache[] quantity;
			try {
				quantity = ArrayCache.splitBatchDoubleToInt(result.getDataArrayCache("value"), nSymbols);
				for (int k = 0; k < nSymbols; k++) {
					optimizedPortfolio.setPositionQuantity(portfolio.getSymbolNamesList().get(k), quantity[k],
							ArrayCache.copyArrayCacheLong(result.getDataArrayCache("time")));
				}

			} catch (IOException e) {
				return processException(e);
			}

		} else {
			optimizedPortfolio.getClient().createCallGroup(1);
			return new MethodResult(result.getErrorMessage());
		}

		MethodResult resultOpt = new MethodResult();
		resultOpt.setPortfolio("portfolio", optimizedPortfolio);
		resultOpt.setInfoParams(result.getInfoParams());

		return resultOpt;
	}

	protected Portfolio optimizationInit(boolean isResultSelfPortfolio) throws Exception {
		if (portfolio.getSymbolNamesList().size() == 0)
			throw new Exception("Empty portfolio");

		Portfolio optimizedPortfolio;

		if (!isResultSelfPortfolio) {

			optimizedPortfolio = new Portfolio(portfolio);

		} else
			optimizedPortfolio = portfolio;

		Gson gson = new Gson();
		paramsString = gson.toJson(paramsBuffer);
		return optimizedPortfolio;
	}

	protected MethodResult processException(IOException e) {

		if (e.getMessage() != null)
			return new MethodResult(e.getMessage());
		else
			return new MethodResult(MessageStrings.ERROR_FILE);

	}

	public void resetParams() {
		paramsString = "";
		constraintNumber = 0;
		paramsBuffer = new ArrayList<HashMap<String, String>>();
	}

	public double getErrorInDecimalPoints() {
		return errorInDecimalPoints;
	}

	public void setErrorInDecimalPoints(double errorInDecimalPoints) {
		this.errorInDecimalPoints = errorInDecimalPoints;
	}

	public double getGlobalOptimumProbability() {
		return globalOptimumProbability;
	}

	public void setGlobalOptimumProbability(double globalOptimumProbability) {
		this.globalOptimumProbability = globalOptimumProbability;
	}

	public int getPortfolioValue() {
		return portfolioValue;
	}

	public void setPortfolioValue(int portfolioValue) {
		this.portfolioValue = portfolioValue;
	}

}
