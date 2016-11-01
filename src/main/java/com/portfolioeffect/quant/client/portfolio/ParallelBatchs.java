/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2015 Snowfall Systems, Inc.
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.portfolioeffect.quant.client.ClientConnection;
import com.portfolioeffect.quant.client.result.Metric;

public class ParallelBatchs {

	private static final int THREAD_NUMBER = 50;
	
	private List<Portfolio> portfolioList;
	
	public ParallelBatchs(){
		portfolioList = new ArrayList<Portfolio>();
	}
	
	public void addPortfolio(Portfolio portfolio){
		portfolioList.add(portfolio);
	}
	
	public void finishBatch(){
		finishBatch(portfolioList);
		portfolioList.clear();
	}

	public static void finishBatch(List<Portfolio> portfolioList){
		
		Portfolio[] portfolioArray = portfolioList.toArray(new Portfolio[portfolioList.size()]);
		
		finishBatch(portfolioArray);
		
	}
	
	public static void finishBatch(Portfolio[] portfolioArray){
		
		List<Task> taskList = new ArrayList<Task>();
		for(Portfolio e: portfolioArray){
			taskList.add(new Task(e));
		}
		
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD_NUMBER);
		
		
		List<Future<Boolean>> futureList = new ArrayList<Future<Boolean>>();
		for(Task e: taskList)
			futureList.add(executorService.submit(e));
		
		ClientConnection clientProgressBar = new ClientConnection();
		clientProgressBar.printProgressBar(0.0);
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {}
			double nDone=0;
			for(Future<Boolean> e: futureList){
				if(e.isDone())
					nDone++;				
			}
			 
			clientProgressBar.printProgressBar(nDone/futureList.size());
			
			if(nDone==futureList.size())
				break;
		}

		
		executorService.shutdown();

		
	}
	
	
	
	
	
}

class Task implements Callable<Boolean> {

	private Portfolio portfolio;

	public Task(Portfolio portfolio) {
		this.portfolio = portfolio;
	}

	@Override
	public Boolean call() {

		ClientConnection curentClient = portfolio.getClient();

		ClientConnection tClient = new ClientConnection();
		tClient.setUsername(curentClient.getUsername());
		tClient.setPassword(curentClient.getPassword());
		tClient.setApiKey(curentClient.getApiKey());
		tClient.setHost(curentClient.getHost());
		tClient.setPort(curentClient.getPort());
		tClient.proggressBarOff();

		while (true) {
			Metric connectResul = tClient.restart();

			if (connectResul.hasError() && connectResul.getErrorMessage().contains("Exceeded maximum number of parallel connections")) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {

					// e.printStackTrace();
				}
				continue;
			}

			

			portfolio.setClientConnection(tClient);
			portfolio.finishBatch();
			tClient.stop();
			portfolio.setClientConnection(curentClient);
			break;
		}

		return true;
	}

}

