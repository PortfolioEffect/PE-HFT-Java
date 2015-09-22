package com.portfolioeffect.quant.client.message.type;

import java.util.HashMap;

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

public enum SessionStatusType {

	SESSION_ACTIVE(0),
	SESSION_PASSWORD_CHANGED(1),
	SESSION_PASSWORD_DUE_TO_EXPIRE(2),
	NEW_SESSION_PASSWORD_DOES_NOT_COMPLY_WITH_POLICY(3),
	SESSION_LOGOUT_COMPLETE(4),
	INVALID_USERNAME_OR_PASSWORD(5),
	ACCOUNT_LOCKED(6),
	LOGONS_NOT_ALLOWED (7),
	PASSWORD_EXPIRED(8);
	
	private static HashMap<Integer, SessionStatusType> codeValueMap = new HashMap<Integer, SessionStatusType>();
	private final int code;
	
	private SessionStatusType(int code) {
		this.code = code;
	}

	static
	{
		for (SessionStatusType  type : SessionStatusType.values())
		{
			codeValueMap.put(type.code, type);
		}
	}

	public int getCode() {
		return code;
	}

	public static SessionStatusType getSessionStatusType(int codeValue) {
		return codeValueMap.get(codeValue);
	}
}
