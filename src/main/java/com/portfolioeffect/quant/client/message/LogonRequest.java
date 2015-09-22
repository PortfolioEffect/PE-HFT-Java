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
package com.portfolioeffect.quant.client.message;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.portfolioeffect.quant.client.message.type.EncryptedPasswordMethodType;


public class LogonRequest extends FastMessage{

	private static final long serialVersionUID = -4997514014817540988L;
	private final String username;
	private final EncryptedPasswordMethodType encPwdMethodType;	 		 
	private final int encPasswordLength;	 		 
	private final String encryptedPassword;
	
	public LogonRequest(StandardHeader messageHeader, String username, String encryptedPassword, EncryptedPasswordMethodType encPwdMethodType, int encryptedPasswordLength) {
		super(messageHeader);
		this.username = username;
		this.encryptedPassword = encryptedPassword;
		this.encPasswordLength = encryptedPasswordLength;
		this.encPwdMethodType = encPwdMethodType;
	}

	public String getUsername() {
		return username;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public EncryptedPasswordMethodType getEncPwdMethodType() {
		return encPwdMethodType;
	}

	public int getEncPasswordLength() {
		return encPasswordLength;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
