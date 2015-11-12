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
package com.portfolioeffect.quant.client.test;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.portfolioeffect.quant.client.model.ComputeErrorException;
import com.portfolioeffect.quant.client.model.TimeValue;
import com.portfolioeffect.quant.client.model.TimeValuePDF;
import com.portfolioeffect.quant.client.portfolio.Portfolio;

import static com.portfolioeffect.quant.client.portfolio.PortfolioUtil.*;

public class PortfolioMetricTest {

	private Logger logger = Logger.getLogger(this.getClass());
	
	private final String userName = "oleg4test";
	private final String password = "oleg4testabirval";
	private final String APIKey = "08ed15919960bed1085c825da1a670fc";

	@Test
	public void testGetMetricsWithDataBasePrice() throws ComputeErrorException {

		util_setCredentials(userName, password, APIKey);

		logger.info("Compute time left: " + util_getComputeTime("timeLeft"));

		Portfolio portfolio = portfolio_create("t-1", "t-1", "SPY");

		portfolio_addPosition(portfolio, "C", 200);
		portfolio_addPosition(portfolio, "GOOG", 200);
		portfolio_addPosition(portfolio, "AAPL", 200);

		portfolio_settings(portfolio,"portfolioMetricsMode",  "price");
		portfolio_settings(portfolio,"timeScale", "1m");
		portfolio_settings(portfolio,"resultsSamplingInterval", "10m");
		portfolio_settings(portfolio, "windowLength", "1h");

		TimeValue resultPosition = position_variance(portfolio, "GOOG");
		double valuePosition[] = resultPosition.getValue();
		long[] timeMillisPostion = resultPosition.getTime();

		TimeValue resultPortfolio = portfolio_skewness(portfolio);
		double valuePortfolio[] = resultPortfolio.getValue();
		long[] timeMillisPortfolio = resultPortfolio.getTime();

		logger.info("Compute time left: " + util_getComputeTime("timeLeft"));

		logger.info("\n\nResult:");
		int dataLenght = valuePosition.length;
		for (int i = 0; i < dataLenght; i++) {
			Timestamp timeStamp = new Timestamp(timeMillisPostion[i]);
			logger.info(timeStamp + "     " + valuePosition[i] + "     " + valuePortfolio[i]);
		}

		logger.info("\n\n\n");
	}
	
	@Test
	public void testGetMetricsWithUSerPrice() throws ComputeErrorException {

		util_setCredentials(userName, password, APIKey);

		logger.info("Compute time left: " + util_getComputeTime("timeLeft"));
		
		int N= 23400*10;//10 days
		
		long[] time = generateOneSecsMillisec(N);
		double indexPrice[]= generateOneSecsPrice(N);
		double assetAPrice[]= generateOneSecsPrice(N);
		double assetBPrice[]= generateOneSecsPrice(N);

		Portfolio portfolio = portfolio_create(indexPrice, time);

		portfolio_addPosition(portfolio, "A", 1, assetAPrice, time);
		portfolio_addPosition(portfolio, "B", 1, assetBPrice, time);
		
		
		portfolio_settings(portfolio,"portfolioMetricsMode",  "price");
		portfolio_settings(portfolio,"resultsSamplingInterval", "1d");
		portfolio_settings(portfolio,"isFractalPriceModelEnabled", "false");
		portfolio_settings(portfolio,"timeScale", "1s");
		
		
		TimeValue resultPosition = position_variance(portfolio, "A");
		double valuePosition[] = resultPosition.getValue();
		long[] timeMillisPostion = resultPosition.getTime();

		TimeValue resultPortfolio = portfolio_skewness(portfolio);
		double valuePortfolio[] = resultPortfolio.getValue();
		long[] timeMillisPortfolio = resultPortfolio.getTime();

		logger.info("Compute time left: " + util_getComputeTime("timeLeft"));

		logger.info("\n\nResult:");
		int dataLenght = valuePosition.length;
		for (int i = 0; i < dataLenght; i++) {
			Timestamp timeStamp = new Timestamp(timeMillisPostion[i]);
			logger.info(timeStamp + "     " + valuePosition[i] + "     " + valuePortfolio[i]);
		}

		logger.info("\n\n\n");
	}


	@Test
	public void testGetPDF() throws Exception {

		util_setCredentials(userName, password, APIKey);

		logger.info("Compute time left: " + util_getComputeTime("timeLeft"));

		Portfolio portfolio = portfolio_create("t-1", "t-1", "SPY");

		portfolio_addPosition(portfolio, "C", 200);
		portfolio_addPosition(portfolio, "GOOG", 200);
		portfolio_addPosition(portfolio, "AAPL", 200);

		portfolio_settings(portfolio,"portfolioMetricsMode",  "price");
		portfolio_settings(portfolio,"timeScale", "1m");
		portfolio_settings(portfolio,"resultsSamplingInterval", "10m");
		portfolio_settings(portfolio, "windowLength", "1h");
		

		int nPoint = 100;
		TimeValuePDF resultPortfolio = portfolio_pdf(portfolio, 0.0, 1.0, nPoint);
		double valuePDF[][] = resultPortfolio.getPDF();
		double valueX[][] = resultPortfolio.getX();
		long[] timeMillisPortfolio = resultPortfolio.getTime();

		logger.info("Compute time left: " + util_getComputeTime("timeLeft"));

		int timeIndex = 10;
		Timestamp timeStamp = new Timestamp(timeMillisPortfolio[timeIndex]);
		logger.info("\n\nResult PDF(" + timeStamp + "):"+timeMillisPortfolio[timeIndex]);
		for (int i = 0; i < nPoint; i++) {

			logger.info(valueX[timeIndex][i] + "\t" + valuePDF[timeIndex][i]);
		}

		logger.info("\n\n\n");
		
		TimeValue result = portfolio_expectedReturn(portfolio);
		timeStamp = new Timestamp(result.getTime()[timeIndex]);
		logger.info(timeStamp);
		logger.info(result.getValue()[timeIndex] + "\t" + result.getTime()[timeIndex]);
		
		result = portfolio_variance(portfolio);
		logger.info(result.getValue()[timeIndex] + "\t" + result.getTime()[timeIndex]);
		
		result = portfolio_skewness(portfolio);
		logger.info(result.getValue()[timeIndex] + "\t" + result.getTime()[timeIndex]);
		
		result = portfolio_kurtosis(portfolio);
		logger.info(result.getValue()[timeIndex] + "\t" + result.getTime()[timeIndex]);
		
		result = portfolio_VaR(portfolio,0.95);
		logger.info(result.getValue()[timeIndex] + "\t" + result.getTime()[timeIndex]);
		
	}
	
	
	
	private double[] generateOneSecsPrice(int n){
		
		double price[] = new double[n];
		
		price[0] =100;
		double sigma= Math.sqrt(1e-6);
	
		for(int i=1; i<n;i++){
			
			double u = Math.random();
			double v = Math.random();
			
			double ret = Math.sqrt( -2.0* Math.log(u) ) *Math.cos(2.0*Math.PI*v);
			double logPrice = Math.log(price[i-1]) + ret*sigma;
			price[i] = Math.exp(logPrice);
			
	
			
		}

		return price;
		
	}
	
	private long[] generateOneSecsMillisec(int n){
		
		long[] time = new long[n];
		
		for(int i=0;i<n ;i++)
			time[i] = i*1000+1000;
		
		return time;
		
	}

}
