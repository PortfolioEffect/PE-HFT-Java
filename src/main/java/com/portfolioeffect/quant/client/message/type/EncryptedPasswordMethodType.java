/*
 * #%L
 * ICE-9 - Algo Client API
 * %%
 * Copyright (C) 2010 - 2015 Snowfall Systems, Inc.
 * %%
 * This software may be modified and distributed under the terms
 * of the BSD license.  See the LICENSE file for details.
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
