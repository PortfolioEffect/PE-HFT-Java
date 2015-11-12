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

public enum SessionRejectReasonType {

	InvalidTagNumber(0),
	RequiredTagMissing(1),
	TagNotDefinedForThisMessageType(2),
	UndefinedTag(3),
	TagSpecifiedWithoutAValue(4),
	ValueIsIncorrect(5),
	IncorrectDataFormatForValue(6),
	DecryptionProblem(7),
	SignatureProblem(8),
	CompIDProblem(9),
	SendingTimeAccuracyProblem(10),
	InvalidMsgType(11),
	XMLValidationError(12),
	TagAppearsMoreThanOnce(13),
	TagSpecifiedOutOfRequiredOrder(14),
	RepeatingGroupFieldsOutOfOrder(15),
	IncorrectNumInGroupCountForRepeatingGroup(16),
	Non(17),
	Invalid(18),
	Other(19);
	
	private static HashMap<Integer, SessionRejectReasonType> codeValueMap = new HashMap<Integer, SessionRejectReasonType>();
	private final int code;

	private SessionRejectReasonType(int code) {
		this.code = code;
	}

	static
	{
		for (SessionRejectReasonType  type : SessionRejectReasonType.values())
		{
			codeValueMap.put(type.code, type);
		}
	}

	public int getCode() {
		return code;
	}

	public static SessionRejectReasonType geSessionRejectReasonType(int codeValue) {
		return codeValueMap.get(codeValue);
	}

	
}
