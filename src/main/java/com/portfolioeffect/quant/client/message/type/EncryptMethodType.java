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
