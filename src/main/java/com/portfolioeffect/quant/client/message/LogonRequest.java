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
