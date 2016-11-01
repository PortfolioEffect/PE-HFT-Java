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
package com.portfolioeffect.quant.client.portfolio;

import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;

import java.sql.Timestamp;
import com.portfolioeffect.quant.client.result.Metric;

public class Position {
	private final String name;

	private final Portfolio portfolio;

	  
	public static Position[] create(Portfolio portfolio, String[] symbols, TIntArrayList quantity){
		return create( portfolio, symbols,  quantity.toArray());
	}
	
	public static Position[] create(Portfolio portfolio, String[] symbols, int[] quantity){
		Position[] positions = new Position[symbols.length];
		for(int i=0; i<symbols.length ;i++)
			positions[i] = new Position(portfolio, symbols[i], quantity[i]);
		
		return positions;
	}
	
	public Position(Portfolio portfolio, String assetName, int quantity) {

		this.name = assetName;
		this.portfolio = portfolio;

		portfolio.addPosition(assetName, quantity);

	}

	public Position(Portfolio portfolio, String assetName) {

		this.name = assetName;
		this.portfolio = portfolio;	

	}
	
	public Position(Portfolio portfolio, String assetName, TIntArrayList quantity, TLongArrayList timeMillSec) {
		 this( portfolio,  assetName, quantity.toArray(),  timeMillSec.toArray());
	}
	
	public Position(Portfolio portfolio, String assetName, int[] quantity, long[] timeMillSec) {

		this.name = assetName;
		this.portfolio = portfolio;

		portfolio.addPosition(assetName, quantity, timeMillSec);

	}
	
	public Position(Portfolio portfolio, String assetName, TIntArrayList quantity, String[] timeMillSec) {
		this(portfolio,  assetName,  quantity.toArray(),  timeMillSec);
	}
	
	public Position(Portfolio portfolio, String assetName, int[] quantity, String[] timeMillSec) {

		this.name = assetName;
		this.portfolio = portfolio;

		long[] time = new long[timeMillSec.length];
		
		int i=0;
		for(String e: timeMillSec){
			time[i++]= Timestamp.valueOf(e).getTime();
		}
		
		
		
		portfolio.addPosition(assetName, quantity, time);

	}

	public Position(Portfolio portfolio, String assetName, TDoubleArrayList price, TIntArrayList quantity, TLongArrayList timeMillSec) {
		this( portfolio,  assetName,  price.toArray(),  quantity.toArray(),  timeMillSec.toArray());
	}
	
	public Position(Portfolio portfolio, String assetName, double[] price, int[] quantity, long[] timeMillSec) {

		this.name = assetName;
		this.portfolio = portfolio;

		portfolio.addPosition(assetName, price, quantity, timeMillSec);

	}

	public Position(Portfolio portfolio, String assetName, TDoubleArrayList price, TIntArrayList quantity, long timeStepMilliSec) {
		this( portfolio,  assetName,  price.toArray(),  quantity.toArray(),  timeStepMilliSec);
	}
	
	public Position(Portfolio portfolio, String assetName, double[] price, int[] quantity, long timeStepMilliSec) {

		this.name = assetName;
		this.portfolio = portfolio;

		portfolio.addPosition(assetName, price, quantity, timeStepMilliSec);

	}

	public Position(Portfolio portfolio, String assetName, TDoubleArrayList price, int quantity, long timeStepMilliSec) {
		this( portfolio,  assetName,  price.toArray(),  quantity,  timeStepMilliSec);
	}
	
	public Position(Portfolio portfolio, String assetName, double[] price, int quantity, long timeStepMilliSec) {

		this.name = assetName;
		this.portfolio = portfolio;

		portfolio.addPosition(assetName, price, quantity, timeStepMilliSec);

	}

	public Position(Portfolio portfolio, String assetName, double[] price, int quantity, long[] priceTimeMillSec) {

		this.name = assetName;
		this.portfolio = portfolio;

		portfolio.addPosition(assetName, price, quantity, priceTimeMillSec);

	}

	public Position(Portfolio portfolio, String assetName, TFloatArrayList price, int quantity, TLongArrayList priceTimeMillSec) {
		this( portfolio, assetName,  price.toArray(),  quantity,  priceTimeMillSec.toArray());
	}
	
	public Position(Portfolio portfolio, String assetName, float[] price, int quantity, long[] priceTimeMillSec) {

		this.name = assetName;
		this.portfolio = portfolio;

		portfolio.addPosition(assetName, price, quantity, priceTimeMillSec);
	}

	public Position(Portfolio portfolio, String assetName, TDoubleArrayList price, TLongArrayList priceTimeMillSec, TIntArrayList quantity, TLongArrayList quantityTimeMillSec) {
		this(portfolio, assetName, price.toArray(),  priceTimeMillSec.toArray(),  quantity.toArray(),  quantityTimeMillSec.toArray());
	}
	
	public Position(Portfolio portfolio, String assetName, double[] price, long[] priceTimeMillSec, int[] quantity, long[] quantityTimeMillSec) {

		this.name = assetName;
		this.portfolio = portfolio;

		portfolio.addPosition(assetName, price, priceTimeMillSec, quantity, quantityTimeMillSec);

	}

	public Position(Portfolio portfolio, String assetName, TFloatArrayList price, TLongArrayList priceTimeMillSec, TIntArrayList quantity, TLongArrayList quantityTimeMillSec) {
		this( portfolio,  assetName,  price.toArray(),  priceTimeMillSec.toArray(),  quantity.toArray(),  quantityTimeMillSec.toArray());
	}
	
	public Position(Portfolio portfolio, String assetName, float[] price, long[] priceTimeMillSec, int[] quantity, long[] quantityTimeMillSec) {

		this.name = assetName;
		this.portfolio = portfolio;

		portfolio.addPosition(assetName, price, priceTimeMillSec, quantity, quantityTimeMillSec);
		
		

	}

	
	public void removePositionPrice(){
		portfolio.removePositionPrice(name);
		
	}
	
	public void removePositionQuantity(){
		portfolio.removePositionQuantity(name);
		
	}
	
	public void setPositionQuantity( int quantity){
		portfolio.setPositionQuantity(name,  quantity);
	}
	
	
	public Metric setPositionQuantity(TIntArrayList quantity, TLongArrayList timeMillesc) {
		return  setPositionQuantity( quantity.toArray(),  timeMillesc.toArray());
	}
	
	public Metric setPositionQuantity(int[] quantity, long[] timeMillesc) {
		return portfolio.setPositionQuantity(name, quantity, timeMillesc);
	}

	
	public Metric setPositionQuantity( TDoubleArrayList quantityD, TLongArrayList timeMillesc) {
		return  setPositionQuantity(  quantityD.toArray(),  timeMillesc.toArray());
	}
	
	public Metric setPositionQuantity( double[] quantityD, long[] timeMillesc) {
		return portfolio.setPositionQuantity(name, quantityD, timeMillesc);
	}

	public Metric setPositionQuantity(TIntArrayList quantity, String[] timeMillesc) {
		return setPositionQuantity( quantity.toArray(),  timeMillesc);
	}
	
	public Metric setPositionQuantity(int[] quantity, String[] timeMillesc) {
		return portfolio.setPositionQuantity(name, quantity, timeMillesc);				
	}

	
	
	public String getName() {
		return name;
	}
	
	public Portfolio getPortfolio() {
		return portfolio;
	}


}

