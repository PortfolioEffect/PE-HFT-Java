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
package com.portfolioeffect.quant.client.type;

public enum CacheModeType {

	DISK_CACHE (true, true),
	IN_MEMORY_CACHE (true, false),
	NO_CACHE (false, false);
	
	private final boolean isCacheEnabled, isDiskCache;
	
	private CacheModeType(boolean isCacheEnabled, boolean isDiskCache) {
		this.isCacheEnabled= isCacheEnabled;
		this.isDiskCache = isDiskCache;
	}

	public boolean isCacheEnabled() {
		return isCacheEnabled;
	}

	public boolean isDiskCache() {
		return isDiskCache;
	}
	
}
