/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
 * %%
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
 * #L%
 */
package com.portfolioeffect.quant.client.message;

public class ServiceMessage {

	private Long timestamp;
	private boolean isTerminated;
	
	public ServiceMessage(Long timestamp) {
		this(timestamp, false);
	}
	
	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isTerminated() {
		return isTerminated;
	}

	public void setTerminated(boolean isTerminated) {
		this.isTerminated = isTerminated;
	}

	public ServiceMessage(Long timestamp, boolean isTerminated) {
		this.timestamp = timestamp;
		this.isTerminated = isTerminated;
	}

	
	
	
	
}
