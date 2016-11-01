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

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TLongArrayList;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.portfolio.ArrayCache;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.portfolio.Position;
import com.portfolioeffect.quant.client.result.LazyMetric;
import com.portfolioeffect.quant.client.result.Metric;
import com.portfolioeffect.quant.client.util.MessageStrings;
import com.portfolioeffect.quant.client.util.SimpleMetricUpdateCallback;

public class PortfolioOptimizer {

	private static final String METRIC_PORTFOLIO_OPTIMIZATION = "{\"metric\":\"PORTFOLIO_OPTIMIZATION\"}";
	protected Portfolio portfolio;
	protected Portfolio optimizedPortfolio = null;
	protected volatile Portfolio resultPortfolio = null;
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
		
		if(direction.equals("min"))
			direction="minimize";
		
		if(direction.equals("max"))
			direction="maximize";
		
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

	
	public void addPortfolioConstraint(String constraintName, TDoubleArrayList expectedValues, TLongArrayList timeMilliSec) {
		
		addPortfolioConstraint(constraintName, expectedValues.toArray(),  timeMilliSec.toArray());
		
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

	
	public void addPortfolioConstraint(String constraintName, String constraintType, TDoubleArrayList expectedValues, TLongArrayList timeMilliSec, double confidenceInterval) {
		addPortfolioConstraint(constraintName, constraintType,  expectedValues.toArray(),  timeMilliSec.toArray(),  confidenceInterval);
		
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

	
	public void addPortfolioConstraint(String constraintName, String constraintType, TDoubleArrayList expectedValues, TLongArrayList timeMillisec) {
		 addPortfolioConstraint(constraintName,  constraintType,  expectedValues.toArray(),  timeMillisec.toArray());
	}
	
	public void addPortfolioConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMillisec) {
		addPortfolioConstraint(constraintName, constraintType, expectedValues, timeMillisec, 0.95, new String[] {});
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double expectedValue, double confidenceInterval, String position) {
		addPortfolioConstraint(constraintName, constraintType, expectedValue, confidenceInterval, new String[] { position });
	}

	
	public void addPortfolioConstraint(String constraintName, String constraintType, TDoubleArrayList expectedValues, TLongArrayList timeMillisec, double confidenceInterval,
			String position) {
		addPortfolioConstraint( constraintName,  constraintType,  expectedValues.toArray(),  timeMillisec.toArray(), confidenceInterval,  position);
	}
	
	public void addPortfolioConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMillisec, double confidenceInterval,
			String position) {
		addPortfolioConstraint(constraintName, constraintType, expectedValues, timeMillisec, confidenceInterval, new String[] { position });
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double expectedValue, String[] positions) {
		addPortfolioConstraint(constraintName, constraintType, expectedValue, 0.95, positions);
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, TDoubleArrayList expectedValues, TLongArrayList timeMillisec, String[] positions) {
		 addPortfolioConstraint(constraintName, constraintType, expectedValues.toArray(),  timeMillisec.toArray(),  positions);
	}
	
	public void addPortfolioConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMillisec, String[] positions) {
		addPortfolioConstraint(constraintName, constraintType, expectedValues, timeMillisec, 0.95, positions);
	}

	public void addPortfolioConstraint(String constraintName, String constraintType, double expectedValue, double confidenceInterval, String[] positions) {
		constraintType = getConstraintType(constraintType);
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

	
	public void addPortfolioConstraint(String constraintName, String constraintType, TDoubleArrayList expectedValues, TLongArrayList timeMilliSec, double confidenceInterval,
			String[] positions) {
		addPortfolioConstraint(constraintName, constraintType,  expectedValues.toArray(), timeMilliSec.toArray(), confidenceInterval, positions); 
	}
	
	public void addPortfolioConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMilliSec, double confidenceInterval,
			String[] positions) {
		constraintType = getConstraintType(constraintType);
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
	public void addPositionConstraint(String constraintName, String constraintType, TDoubleArrayList expectedValues, TLongArrayList timeMilliSec, String positionName) {
		 addPositionConstraint(constraintName,  constraintType,  expectedValues.toArray(),  timeMilliSec.toArray(), positionName);
	}
	public void addPositionConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMilliSec, String positionName) {
		constraintType = getConstraintType(constraintType);
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
		constraintType = getConstraintType(constraintType);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("section", "CONSTRAINT_" + constraintNumber++);
		map.put("position", positionName);
		map.put("constraintName", constraintName);
		map.put("constraintType", constraintType);
		map.put("expectedValueDataName", "expectedValueDataName-" + constrainCount.getAndIncrement());
		map.put("expectedValue", "" + expectedValue);

		paramsBuffer.add(map);

	}

	public void addPositionConstraint(String constraintName, String constraintType, TDoubleArrayList expectedValues, TLongArrayList timeMilliSec) {
		addPositionConstraint(constraintName,  constraintType,  expectedValues.toArray(),  timeMilliSec.toArray());
	}
	
	public void addPositionConstraint(String constraintName, String constraintType, double[] expectedValues, long[] timeMilliSec) {
		constraintType = getConstraintType(constraintType);
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
		constraintType = getConstraintType(constraintType);
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
		constraintType = getConstraintType(constraintType);
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

	public LazyMetric getOptimizedPortfolio() throws ComputeErrorException  {
		
		try {
			optimizedPortfolio = new Portfolio(portfolio);
			resultPortfolio = new Portfolio(portfolio);
		} catch (IOException e) {
			return new LazyMetric(e.getMessage());
		}
		

		try {
			optimizationInit();
		} catch (Exception e1) {
			return new LazyMetric(e1.getMessage());
		}

		return makeOptimization();
	}

	public LazyMetric getOptimizedPortfolioStream() throws Exception {

		optimizedPortfolio = new Portfolio(portfolio);
		resultPortfolio = new Portfolio(portfolio);

		optimizedPortfolio.initStreamSingleMetric(new SimpleMetricUpdateCallback() {

			@Override
			public void onDataRefresh(float[] data, long[] time) {

				//
				// Console.writeln("==>");
				// Console.writeln("" + (new Timestamp(time[0])));
				// for (int i = 0; i < data.length; i++)
				// Console.write(data[i] + "\t");
				// Console.writeln("\n>==");

				int nSymbols = resultPortfolio.getSymbolNamesList().size();
				int len = 0;
				for (int i = 0; i < time.length; i++)
					for (int k = 0; k < nSymbols; k++) {
						resultPortfolio.setStreamQuantity(resultPortfolio.getSymbolNamesList().get(k), (int) data[len], time[i]);
						len++;
					}

			}
		});

		try {
			optimizationInit();
		} catch (Exception e1) {
			return new LazyMetric(e1.getMessage());
		}

		return makeOptimization();
	}

	public void stopStream() {
		optimizedPortfolio.stopStream();
	}

	protected LazyMetric makeOptimization() throws ComputeErrorException{
		
		
		Metric checkResult = optimizedPortfolio.addUserData("portfolioValue", new double[] { portfolioValue }, new long[] { -1 });
		if (checkResult.hasError()){
			
			return new LazyMetric(checkResult.getErrorMessage());
		}

		Metric result = optimizedPortfolio.getMetric(METRIC_PORTFOLIO_OPTIMIZATION, paramsString);
		if (!result.hasError()) {

			int nSymbols = optimizedPortfolio.getSymbolNamesList().size();

			ArrayCache[] quantity;
			try {
				result.getDataArrayCache("value").lockToRead();
				result.getDataArrayCache("time").lockToRead();
				quantity = ArrayCache.splitBatchDoubleMatrixToInt(result.getDataArrayCache("value"), nSymbols);

				for (int k = 0; k < nSymbols; k++) {
					resultPortfolio.setPositionQuantity(portfolio.getSymbolNamesList().get(k), quantity[k],
							ArrayCache.copyArrayCacheLong(result.getDataArrayCache("time")));
				}
				result.getDataArrayCache("value").unlockToRead();
				result.getDataArrayCache("time").unlockToRead();

			} catch (IOException e) {
		
				return processException(e);
			}

		} else {
			optimizedPortfolio.getClient().createCallGroup(1);
			return new LazyMetric(result.getErrorMessage());
		}

		Metric resultOpt = new Metric();

		resultOpt.setPortfolio("portfolio", resultPortfolio);
		resultOpt.setInfoParams(result.getInfoParams());
		
		return new LazyMetric(resultOpt);
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

	protected void optimizationInit() throws Exception {

		if (portfolio.getSymbolNamesList().size() == 0)
			throw new Exception("Empty portfolio");

		Gson gson = new Gson();
		paramsString = gson.toJson(paramsBuffer);

	}

	protected LazyMetric processException(IOException e) {

		if (e.getMessage() != null)
			return new LazyMetric(e.getMessage());
		else
			return new LazyMetric(MessageStrings.ERROR_FILE);

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

	private String getMetric(LazyMetric lazy) {

		return lazy.getMetricParams().get("metric").replace("PORTFOLIO_", "");
	}

	private String getSymbol(LazyMetric lazy) {

		return lazy.getMetricParams().get("position");
	}

	private String getConfidenceInterval(LazyMetric lazy) {

		return lazy.getMetricParams().get("confidenceInterval");
	}

	private String getConstraintType(String type) {

		if (type.equals("<="))
			return "lessOrEquals";

		if (type.equals("="))
			return "equals";

		if (type.equals(">="))
			return "greaterOrEquals";

		return type;
	}

	private String getOptimizationMetricT(String goal) {

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

	// -----------------------------------------------------------------------------------------------------
	public void setOptimizationGoal(LazyMetric lazy, String direction) {
		String optimizationMetric = getMetric(lazy);
		String confidenceInterval = getConfidenceInterval(lazy);
		HashMap<String, String> sb = populateRequiredOptimizationGoalParams(optimizationMetric, direction);
		if (confidenceInterval != null)
			sb.put("confidenceInterval", confidenceInterval);
		else
			sb.put("confidenceInterval", "0,95");

		setOptimizationGoal(sb);
	}

	
	// position constraints

	public void addConstraint(LazyMetric lazy, String constraintType, TDoubleArrayList expectedValues, TLongArrayList timeMilliSec) {
		addConstraint( lazy, constraintType,  expectedValues.toArray(), timeMilliSec.toArray());
	}
	
	public void addConstraint(LazyMetric lazy, String constraintType, double[] expectedValues, long[] timeMilliSec) {
		String constraintName = getMetric(lazy);

		String positionName = getSymbol(lazy);
		String confidenceInterval = getConfidenceInterval(lazy);

		HashMap<String, String> sb = new HashMap<String, String>();
		constraintType = getConstraintType(constraintType);
		String userDataName = "OptimizationConstraint" + portfolio.getNewDataId();
		portfolio.addUserData(userDataName, expectedValues, timeMilliSec);

		if (positionName != null)
			sb.put("position", positionName);
		if (confidenceInterval != null)
			sb.put("confidenceInterval", "" + confidenceInterval);

		sb.put("constraintName", constraintName);
		sb.put("constraintType", constraintType);
		sb.put("expectedValueDataName", userDataName);
		
		
		if(constraintName.equals("POSITIONS_SUM_ABS_WEIGHT")){
			String positionsStr=lazy.getMetricParams().get("positions");
			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<String>>() {}.getType();
			ArrayList<String> positions = gson.fromJson(positionsStr, type);
			if (positions.size() != 0) {
				String positionList = "";
				for (String e : positions) {
					positionList += e + "---";
				}
				sb.put("positions", positionList);

			}

			
		}

		addPortfolioConstraint(sb);
	}

	public void addConstraint(LazyMetric lazy, String constraintType, double expectedValue) {
		
		
		
		
		String constraintName = getMetric(lazy);
		
		if(constraintName.equals("VALUE")){
			
			setPortfolioValue((int) expectedValue);
			return;
			
			
		}
		
		
		String positionName = getSymbol(lazy);
		String confidenceInterval = getConfidenceInterval(lazy);
		constraintType = getConstraintType(constraintType);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("section", "CONSTRAINT_" + constraintNumber++);
		if (positionName != null)
			map.put("position", positionName);
		if (confidenceInterval != null)
			map.put("confidenceInterval", "" + confidenceInterval);

		map.put("constraintName", constraintName);
		map.put("constraintType", constraintType);

		map.put("expectedValueDataName", "expectedValueDataName-" + constrainCount.getAndIncrement());
		map.put("expectedValue", "" + expectedValue);

		
		
		if(constraintName.equals("POSITIONS_SUM_ABS_WEIGHT")){
			String positionsStr=lazy.getMetricParams().get("positions");
			Gson gson = new Gson();
			Type type = new TypeToken<ArrayList<String>>() {}.getType();
			ArrayList<String> positions = gson.fromJson(positionsStr, type);
			if (positions.size() != 0) {
				String positionList = "";
				for (String e : positions) {
					positionList += e + "---";
				}
				map.put("positions", positionList);

			}

			
		}
		
		paramsBuffer.add(map);

	}

		
	
}
