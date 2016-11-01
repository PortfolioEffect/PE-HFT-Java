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
import java.util.ArrayList;

import com.portfolioeffect.quant.client.portfolio.ArrayCache;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.result.Metric;
import com.portfolioeffect.quant.client.util.MessageStrings;

public class ForecastedValues {

	private boolean[][] isSymbolCumulantPresent;
	private boolean[] isIndexCumulantPresent = { false, false, false, false };
	private int symbolNamber = 0;
	private ArrayList<String> symbolsName;

	private ArrayCache[][] forecastedSymbolValue;
	private ArrayCache[][] forecastedSymbolValueTime;

	private ArrayCache[] forecastedIndexValue;
	private ArrayCache[] forecastedIndexValueTime;

	private boolean[][] isCumulants;
	private boolean[] isCumulantsIndex = { false, false };

	private boolean isTimeStep;
	private ArrayCache timeStep;
	private ArrayCache timeStepTimeMilliSec;
	private int N = 1;

	public ForecastedValues(Portfolio portfolio) {

		symbolsName = new ArrayList<String>(portfolio.getSymbolNamesList());

		symbolNamber = symbolsName.size();

		isSymbolCumulantPresent = new boolean[symbolNamber][];

		forecastedSymbolValue = new ArrayCache[symbolNamber][];
		forecastedSymbolValueTime = new ArrayCache[symbolNamber][];

		isCumulants = new boolean[symbolNamber][];

		forecastedIndexValue = new ArrayCache[4];
		forecastedIndexValueTime = new ArrayCache[4];

		for (int i = 0; i < symbolNamber; i++) {
			forecastedSymbolValue[i] = new ArrayCache[5];
			forecastedSymbolValueTime[i] = new ArrayCache[5];

			isCumulants[i] = new boolean[2];
			isCumulants[i][0] = false;
			isCumulants[i][1] = false;
			isSymbolCumulantPresent[i] = new boolean[5];
			isSymbolCumulantPresent[i][0] = false;
			isSymbolCumulantPresent[i][1] = false;
			isSymbolCumulantPresent[i][2] = false;
			isSymbolCumulantPresent[i][3] = false;
			isSymbolCumulantPresent[i][4] = false; // beta
		}

		isIndexCumulantPresent[0] = true;// index expReturn is not need

	}

	public Metric isAllForecastedValuesPresent() {

		String symbols = "";

		if (!isTimeStep) {

			symbols += "forecast time step;\n";

		}

		if (symbols.equals(""))
			return new Metric();
		else
			return new Metric("The next values not defined:\n" + symbols);
	}
//checkResult(portfolio.addUserData("expTimeStep", timeStep, timeStepTimeMilliSec));
	public Metric setForecastTimeStep(double value) {
		return setForecastTimeStep(new double[] { value }, new long[] { -1 });
	}

	public Metric setForecastTimeStep(String value) {
		try {

			N = parseTimeInterval(value, "forecast time step");

			return setForecastTimeStep(new double[] { N }, new long[] { -1 });
		} catch (Exception e) {
			return new Metric(e.getMessage());
		}
	}

	
	public Metric setForecastTimeStep(TDoubleArrayList value, TLongArrayList time) {
		return setForecastTimeStep( value.toArray(), time.toArray());
	}
	
	public Metric setForecastTimeStep(double[] value, long[] time) {

		try {
			timeStep = new ArrayCache(value);

			timeStepTimeMilliSec = new ArrayCache(time);

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new Metric(e.getMessage());
			else
				return new Metric(MessageStrings.ERROR_FILE);
		}

		isTimeStep = true;

		return new Metric();
	}

	
	public Metric setSymbolForecastedExpReturn(String symbol, TDoubleArrayList value, TLongArrayList time) {
		return  setSymbolForecastedExpReturn( symbol,  value.toArray(),  time.toArray());
	}
	
	public Metric setSymbolForecastedExpReturn(String symbol, double[] value, long[] time) {

		if (!symbolsName.contains(symbol))
			return new Metric(String.format(MessageStrings.SYMBOL_NOT_IN_PORTFOLIO, symbol));

		int index = symbolsName.indexOf(symbol);

		try {
			forecastedSymbolValue[index][0] = new ArrayCache(value);

			forecastedSymbolValueTime[index][0] = new ArrayCache(time);

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new Metric(e.getMessage());
			else
				return new Metric(MessageStrings.ERROR_FILE);
		}

		isSymbolCumulantPresent[index][0] = true;

		return new Metric();
	}

	public Metric setSymbolForecastedBeta(String symbol, TDoubleArrayList value, TLongArrayList time) {
		return  setSymbolForecastedBeta( symbol,  value,  time);
	}
	
	public Metric setSymbolForecastedBeta(String symbol, double[] value, long[] time) {

		if (!symbolsName.contains(symbol))
			return new Metric(String.format(MessageStrings.SYMBOL_NOT_IN_PORTFOLIO, symbol));

		int index = symbolsName.indexOf(symbol);

		try {
			forecastedSymbolValue[index][4] = new ArrayCache(value);
			forecastedSymbolValueTime[index][4] = new ArrayCache(time);

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new Metric(e.getMessage());
			else
				return new Metric(MessageStrings.ERROR_FILE);
		}

		isSymbolCumulantPresent[index][4] = true;

		return new Metric();
	}

	public Metric setSymbolForecastedVariance(String symbol, TDoubleArrayList value, TLongArrayList time) {
		return  setSymbolForecastedVariance( symbol,  value.toArray(),  time.toArray()) ;
	}
	
	public Metric setSymbolForecastedVariance(String symbol, double[] value, long[] time) {

		if (!symbolsName.contains(symbol))
			return new Metric(String.format(MessageStrings.SYMBOL_NOT_IN_PORTFOLIO, symbol));

		int index = symbolsName.indexOf(symbol);

		try {
			forecastedSymbolValue[index][1] = new ArrayCache(value);

			forecastedSymbolValueTime[index][1] = new ArrayCache(time);

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new Metric(e.getMessage());
			else
				return new Metric(MessageStrings.ERROR_FILE);
		}

		isSymbolCumulantPresent[index][1] = true;

		return new Metric();
	}

	public Metric setSymbolForecastedSkewness(String symbol, TDoubleArrayList value, TLongArrayList time) {
		return  setSymbolForecastedSkewness( symbol,  value.toArray(),  time.toArray());
	}
	
	public Metric setSymbolForecastedSkewness(String symbol, double[] value, long[] time) {

		if (!symbolsName.contains(symbol))
			return new Metric(String.format(MessageStrings.SYMBOL_NOT_IN_PORTFOLIO, symbol));

		int index = symbolsName.indexOf(symbol);

		try {
			forecastedSymbolValue[index][2] = new ArrayCache(value);

			forecastedSymbolValueTime[index][2] = new ArrayCache(time);

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new Metric(e.getMessage());
			else
				return new Metric(MessageStrings.ERROR_FILE);
		}

		isSymbolCumulantPresent[index][2] = true;

		return new Metric();
	}

	public Metric setSymbolForecastedKurtosis(String symbol, TDoubleArrayList value, TLongArrayList time) {
		return setSymbolForecastedKurtosis(symbol, value.toArray()	,  time.toArray());
	}
	
	public Metric setSymbolForecastedKurtosis(String symbol, double[] value, long[] time) {

		if (!symbolsName.contains(symbol))
			return new Metric(String.format(MessageStrings.SYMBOL_NOT_IN_PORTFOLIO, symbol));

		int index = symbolsName.indexOf(symbol);

		try {
			forecastedSymbolValue[index][3] = new ArrayCache(value);

			forecastedSymbolValueTime[index][3] = new ArrayCache(time);

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new Metric(e.getMessage());
			else
				return new Metric(MessageStrings.ERROR_FILE);
		}

		isSymbolCumulantPresent[index][3] = true;

		return new Metric();
	}

	
	public Metric setSymbolForecastedCumulant1(String symbol, TDoubleArrayList value, TLongArrayList time) {
		return  setSymbolForecastedCumulant1(symbol, value.toArray()	,  time.toArray());
	}
	
	public Metric setSymbolForecastedCumulant1(String symbol, double[] value, long[] time) {

		return setSymbolForecastedExpReturn(symbol, value, time);

	}

	public Metric setSymbolForecastedCumulant2(String symbol, TDoubleArrayList value, TLongArrayList time) {
		return  setSymbolForecastedCumulant2(symbol, value.toArray()	,  time.toArray());
	}
	
	public Metric setSymbolForecastedCumulant2(String symbol, double[] value, long[] time) {

		return setSymbolForecastedVariance(symbol, value, time);

	}

	public Metric setSymbolForecastedCumulant3(String symbol, TDoubleArrayList value, TLongArrayList time) {
		return  setSymbolForecastedCumulant3(symbol, value.toArray()	,  time.toArray());
	}
	
	
	public Metric setSymbolForecastedCumulant3(String symbol, double[] value, long[] time) {

		Metric result = setSymbolForecastedSkewness(symbol, value, time);
		isCumulants[symbolsName.indexOf(symbol)][0] = true;
		return result;

	}

	public Metric setSymbolForecastedCumulant4(String symbol, TDoubleArrayList value, TLongArrayList time) {
		return  setSymbolForecastedCumulant4(symbol, value.toArray()	,  time.toArray());
	}
	
	
	public Metric setSymbolForecastedCumulant4(String symbol, double[] value, long[] time) {

		Metric result = setSymbolForecastedKurtosis(symbol, value, time);
		isCumulants[symbolsName.indexOf(symbol)][1] = true;
		return result;
	}

	public Metric setIndexForecastedVariance( TDoubleArrayList value, TLongArrayList time) {
		return  setIndexForecastedVariance( value.toArray()	,  time.toArray());
	}
	
	public Metric setIndexForecastedVariance(double[] value, long[] time) {

		try {
			forecastedIndexValue[1] = new ArrayCache(value);

			forecastedIndexValueTime[1] = new ArrayCache(time);

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new Metric(e.getMessage());
			else
				return new Metric(MessageStrings.ERROR_FILE);
		}

		isIndexCumulantPresent[1] = true;

		return new Metric();
	}

	public Metric setIndexForecastedSkewness( TDoubleArrayList value, TLongArrayList time) {
		return setIndexForecastedSkewness( value.toArray()	,  time.toArray());
	}
	
	public Metric setIndexForecastedSkewness(double[] value, long[] time) {

		try {
			forecastedIndexValue[2] = new ArrayCache(value);

			forecastedIndexValueTime[2] = new ArrayCache(time);

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new Metric(e.getMessage());
			else
				return new Metric(MessageStrings.ERROR_FILE);
		}

		isIndexCumulantPresent[2] = true;

		return new Metric();
	}

	public Metric setIndexForecastedKurtosis( TDoubleArrayList value, TLongArrayList time) {
		return setIndexForecastedKurtosis( value.toArray()	,  time.toArray());
	}
	
	
	public Metric setIndexForecastedKurtosis(double[] value, long[] time) {

		try {
			forecastedIndexValue[3] = new ArrayCache(value);

			forecastedIndexValueTime[3] = new ArrayCache(time);

		} catch (IOException e) {
			if (e.getMessage() != null)
				return new Metric(e.getMessage());
			else
				return new Metric(MessageStrings.ERROR_FILE);
		}

		isIndexCumulantPresent[3] = true;

		return new Metric();
	}

	public Metric setIndexForecastedCumulant2( TDoubleArrayList value, TLongArrayList time) {
		return setIndexForecastedCumulant2( value.toArray()	,  time.toArray());
	}
	
	
	public Metric setIndexForecastedCumulant2(double[] value, long[] time) {

		return setIndexForecastedVariance(value, time);

	}
	
	public Metric setIndexForecastedCumulant3( TDoubleArrayList value, TLongArrayList time) {
		return setIndexForecastedCumulant3( value.toArray()	,  time.toArray());
	}
	

	public Metric setIndexForecastedCumulant3(double[] value, long[] time) {
		Metric result = setIndexForecastedSkewness(value, time);
		isCumulantsIndex[0] = true;
		return result;
	}

	public Metric setIndexForecastedCumulant4( TDoubleArrayList value, TLongArrayList time) {
		return setIndexForecastedCumulant4( value.toArray()	,  time.toArray());
	}
	
	
	public Metric setIndexForecastedCumulant4(double[] value, long[] time) {

		Metric result = setIndexForecastedKurtosis(value, time);
		isCumulantsIndex[1] = true;
		return result;
	}

	private Metric checkResult(Metric result) throws Exception {

		if (result.hasError())
			throw new Exception(result.getErrorMessage());

		return result;

	}

	private int parseTimeInterval(String s, String where) throws Exception {

		String ERROR = String.format(MessageStrings.INCOR_PARAM_FORMAT, where);

		String res[] = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");

		String error = "";

		int number = 1;
		int scale = 0;
		if (res.length != 2) {
			error = ERROR;
			// 1s, 1m, 1d, 1w, 1mo, 1y
		} else {
			try {
				number = Integer.parseInt(res[0]);
			} catch (Exception e) {
				error = ERROR;
			}

			if (res[1].equals("s"))
				scale = 1;
			if (res[1].equals("m"))
				scale = 60;
			if (res[1].equals("h"))
				scale = 60 * 60;
			if (res[1].equals("d"))
				scale = 23400;
			if (res[1].equals("w"))
				scale = 23400 * 5;
			if (res[1].equals("mo"))
				scale = 23400 * 21;
			if (res[1].equals("y"))
				scale = 23400 * 256;

			if (scale == 0)
				error = ERROR;
		}

		if (error.length() != 0) {
			throw new Exception(error);
		}

		return number * scale;

	}

	public Metric addToPortfolio(Portfolio portfolio) {

		try {

			checkResult(isAllForecastedValuesPresent());
			if(timeStep!=null && timeStepTimeMilliSec!=null)
				checkResult(portfolio.addUserData("expTimeStep", timeStep, timeStepTimeMilliSec));

			if (isIndexCumulantPresent[1])
				checkResult(portfolio.addUserData("IndexVariance", forecastedIndexValue[1], forecastedIndexValueTime[1]));

			if (isIndexCumulantPresent[2])
				if (isIndexCumulantPresent[0])
					checkResult(portfolio.addUserData("IndexCumulant3", forecastedIndexValue[2], forecastedIndexValueTime[2]));
				else
					checkResult(portfolio.addUserData("IndexSkewness", forecastedIndexValue[2], forecastedIndexValueTime[2]));

			if (isIndexCumulantPresent[3])
				if (isIndexCumulantPresent[1])
					checkResult(portfolio.addUserData("IndexCumulant4", forecastedIndexValue[3], forecastedIndexValueTime[3]));
				else
					checkResult(portfolio.addUserData("IndexKurtosis", forecastedIndexValue[3], forecastedIndexValueTime[3]));

			for (int i = 0; i < symbolNamber; i++) {

				String symbol = symbolsName.get(i);

				if (isSymbolCumulantPresent[i][0])
					checkResult(portfolio.addUserData(symbol + "ExpReturn", forecastedSymbolValue[i][0], forecastedSymbolValueTime[i][0]));

				if (isSymbolCumulantPresent[i][1])
					checkResult(portfolio.addUserData(symbol + "Variance", forecastedSymbolValue[i][1], forecastedSymbolValueTime[i][1]));

				if (isSymbolCumulantPresent[i][2])
					if (isCumulants[i][0])
						checkResult(portfolio.addUserData(symbol + "Cumulant3", forecastedSymbolValue[i][2], forecastedSymbolValueTime[i][2]));
					else
						checkResult(portfolio.addUserData(symbol + "Skewness", forecastedSymbolValue[i][2], forecastedSymbolValueTime[i][2]));

				if (isSymbolCumulantPresent[i][3])
					if (isCumulants[i][1])
						checkResult(portfolio.addUserData(symbol + "Cumulant4", forecastedSymbolValue[i][2], forecastedSymbolValueTime[i][3]));
					else
						checkResult(portfolio.addUserData(symbol + "Kurtosis", forecastedSymbolValue[i][2], forecastedSymbolValueTime[i][3]));

				if (isSymbolCumulantPresent[i][4])
					checkResult(portfolio.addUserData(symbol + "Beta", forecastedSymbolValue[i][4], forecastedSymbolValueTime[i][4]));
			}

		} catch (Exception e) {
			return new Metric(e.getMessage());
		}

		return new Metric();
	}

}
