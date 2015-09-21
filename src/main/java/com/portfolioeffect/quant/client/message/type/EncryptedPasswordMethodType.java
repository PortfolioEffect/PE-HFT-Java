/*
 * #%L
 * ICE-9 - Algo Client API
 * %%
 * Copyright (C) 2010 - 2015 Snowfall Systems, Inc.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 * #L%
 */
package com.portfolioeffect.quant.client.message.type;

import java.util.HashMap;

public enum EncryptedPasswordMethodType {

	AES(0);
	
	private static HashMap<Integer, EncryptedPasswordMethodType> codeValueMap = new HashMap<Integer, EncryptedPasswordMethodType>();
	private final int code;

	private EncryptedPasswordMethodType(int code) {
		this.code = code;
	}

	static
	{
		for (EncryptedPasswordMethodType  type : EncryptedPasswordMethodType.values())
		{
			codeValueMap.put(type.code, type);
		}
	}

	public int getCode() {
		return code;
	}

	public static EncryptedPasswordMethodType getEncryptedPasswordMethodType(int codeValue) {
		return codeValueMap.get(codeValue);
	}
}
