/*
 * #%L
 * ICE-9 - Algo Client API
 * %%
 * Copyright (C) 2010 - 2015 Snowfall Systems, Inc.
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
package com.portfolioeffect.quant.client.message.type;

import java.util.HashMap;

public enum EncryptMethodType {

	NONE (0),
	PKCS(1),
	DES(2),
	PKCSDES(3),
	PGPDES(4),
	PGPDESMD5(5),
	PEM(6);

	private static HashMap<Integer, EncryptMethodType> codeValueMap = new HashMap<Integer, EncryptMethodType>();
	private final int code;

	private EncryptMethodType(int code) {
		this.code = code;
	}

	static
	{
		for (EncryptMethodType  type : EncryptMethodType.values())
		{
			codeValueMap.put(type.code, type);
		}
	}

	public int getCode() {
		return code;
	}

	public static EncryptMethodType getEncryptMethodType(int codeValue) {
		return codeValueMap.get(codeValue);
	}
}
