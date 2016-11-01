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
package com.portfolioeffect.quant.client.util;

import java.util.Map;
import com.portfolioeffect.quant.client.portfolio.Portfolio;
import com.portfolioeffect.quant.client.portfolio.Position;
import com.portfolioeffect.quant.client.result.LazyMetric;
import com.portfolioeffect.quant.client.type.MetricsTypeFull;

public class SimpleLazyMetricBuilder extends LazyMetricBuilder{

	
	public SimpleLazyMetricBuilder(){
		super();

	}
	
	public SimpleLazyMetricBuilder(LazyMetricBuilder builder){
		super(builder);
		
	}
	
	public SimpleLazyMetricBuilder(String jsonStr){
		super(jsonStr);				
	}
	
	public SimpleLazyMetricBuilder(Map<String, String> map){
		super(map);		
	}
	
	
	public LazyMetric build(Portfolio portfolio){
		
		return super.build(portfolio);		
		
	}
	
	public LazyMetric build(Position position){
		
		return super.build(position);		
			
	}
	
	public LazyMetric build(Position positionA, Position positionB){
		return super.build(positionA, positionB);
		
	}
	
		
	
	

	public SimpleLazyMetricBuilder setMetricName(String metric) {
		setParam("metric", "" + metric);		
		return this;
	}

	public SimpleLazyMetricBuilder setMetric(MetricsTypeFull metric) {
		super.setParam("metric", "" + metric);
		return this;

	}

	public SimpleLazyMetricBuilder setMetricName(MetricsTypeFull metric) {
		super.setParam("metric", "" + metric);
		return this;

	}


	public SimpleLazyMetricBuilder setPositionName(String position) {
		setParam("position", position);
		return this;

	}

	public SimpleLazyMetricBuilder setTransform(String value) {
		setParam("transform", value);
		return this;

	}

	public SimpleLazyMetricBuilder setValueType(String value) {
		setParam("valueType", value);
		return this;
	}
	
	public SimpleLazyMetricBuilder setTimeShift(String value) {
		super.setParam("timeShift", value);
		return this;
	}


	public SimpleLazyMetricBuilder setRollingWindow(String value) {
				
		setParam("rollingWindow", value);
		return this;
	}

	public SimpleLazyMetricBuilder setForecastStep(String value) {
		
		super.setParam("forecastStep", value);
		return this;
	}

	public SimpleLazyMetricBuilder addIndependentVariable(LazyMetric value) throws Exception {
		addToList("independentVariable", value);
		return this;
	}

	public SimpleLazyMetricBuilder setDependentVariable(LazyMetric value) throws Exception {
		setParam("dependentVariable", value);
		return this;
	}
	
	public SimpleLazyMetricBuilder setVariance(LazyMetric value) throws Exception {
		setParam("VARIANCE", value);
		return this;
	}
	
	public SimpleLazyMetricBuilder setBeta(LazyMetric value) throws Exception {
		setParam("BETA", value);
		return this;
	}
	
	
	public SimpleLazyMetricBuilder setCumulant3(LazyMetric value) throws Exception {
		setParam("CUMULANT3", value);
		return this;
	}

	
	public SimpleLazyMetricBuilder setCumulant4(LazyMetric value) throws Exception {
		setParam("CUMULANT4", value);
		return this;
	}
	
	public SimpleLazyMetricBuilder setExpReturn(LazyMetric value) throws Exception {
		setParam("EXPECTED_RETURN", value);
		return this;
	}


	
	public SimpleLazyMetricBuilder setWindowLength(String windowLength) {
		
		setParam("windowLength", windowLength);
		return this;

	}

	public SimpleLazyMetricBuilder setSeasonInterval(String value) {
		setParam("seasonInterval", value);
		return this;

	}

	public SimpleLazyMetricBuilder setSortDirection(String sortDirection) {
		setParam("sort_direction", sortDirection);// ascending -- descending
		return this;

	}

	public SimpleLazyMetricBuilder setSortDirectionToAscending() {
		setParam("sort_direction", "ascending");// ascending -- descending
		return this;

	}

	public SimpleLazyMetricBuilder setSortDirectionToDescending() {
		setParam("sort_direction", "descending");// ascending -- descending
		return this;
	}

	public SimpleLazyMetricBuilder setValue(String metric) {
		setParam("value", "" + metric);
		return this;

	}

	public SimpleLazyMetricBuilder setSortBy(String metric) {
		setParam("sort_by", "" + metric);
		return this;

	}

	public SimpleLazyMetricBuilder setConfidenceInterval(double confidenceInterval) {
		setParam("confidenceInterval", "" + confidenceInterval);
		return this;

	}

	public SimpleLazyMetricBuilder setConfidenceIntervalAlphaBeta(double confidenceIntervalAlpha, double confidenceIntervalBeta) {
		setParam("confidenceIntervalAlpha", "" + confidenceIntervalAlpha);
		setParam("confidenceIntervalBeta", "" + confidenceIntervalBeta);
		return this;
	}

	public SimpleLazyMetricBuilder setPositionA(String position) {
		setParam("positionA", position);
		return this;

	}

	public SimpleLazyMetricBuilder setPositionB(String position) {
		setParam("positionB", position);
		return this;

	}

	public SimpleLazyMetricBuilder setLag(int lag) {
		setParam("lag", "" + lag);
		return this;

	}

	public SimpleLazyMetricBuilder setThresholdReturn(double thresholdReturn) {
		setParam("thresholdReturn", "" + thresholdReturn);
		return this;

	}

	public SimpleLazyMetricBuilder reset() {
		super.reset();
		return this;
	}

		
}
