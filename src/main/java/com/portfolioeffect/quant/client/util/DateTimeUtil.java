/*
 * #%L
 * PortfolioEffect - Quant Client
 * %%
 * Copyright (C) 2011 - 2015 Snowfall Systems, Inc.
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
package com.portfolioeffect.quant.client.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeUtil {

	public static final long CLIENT_TIME_DELTA;
	private static final DateFormat FULL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static {
		CLIENT_TIME_DELTA = getDeltaMilliSec();
	}
	
	private static long getDeltaMilliSec(){
		Calendar dataBaseTime = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));
		dataBaseTime.setTimeInMillis(new Date().getTime());		
		Calendar localTime = Calendar.getInstance();
				
		long dataBaseMillisec = (dataBaseTime.get(Calendar.HOUR_OF_DAY)*60 + dataBaseTime.get(Calendar.MINUTE) ) *60 *1000;
		long localMillisec = (localTime.get(Calendar.HOUR_OF_DAY)*60 + localTime.get(Calendar.MINUTE) ) *60 *1000;
		long deltaMillisec = dataBaseMillisec - localMillisec;
		
		int baseDay =  dataBaseTime.get(Calendar.DAY_OF_MONTH);
		int localDay =  localTime.get(Calendar.DAY_OF_MONTH);
		
		if(baseDay<localDay) {
			deltaMillisec-= 24*60*60*1000;
		}
		
		if(baseDay>localDay) {
			deltaMillisec+= 24*60*60*1000;
		}
		
		return deltaMillisec;
	}

	public static String[] POSIXTimeToDateStr(long [] timesMills, String formatStr) {
		DateFormat format = new SimpleDateFormat(formatStr);
		return POSIXTimeToDateStr(timesMills, format);
	}
	
	public static String[] POSIXTimeToDateStr(long timesMills, String formatStr) {
		return POSIXTimeToDateStr(new long[] {timesMills}, formatStr);
	}
	
	public static int[] POSIXTimeToDateInt(long timesMills, String formatStr) {
		return POSIXTimeToDateInt(new long [] {timesMills}, formatStr);
	}
	
	public static int[] POSIXTimeToDateInt(long [] timesMills, String formatStr) {
		int field = -1;
		if(formatStr.equals("yyyy")) {
			field = Calendar.YEAR;
		}
		else if (formatStr.equals("MM")) {
			field = Calendar.MONTH;
		}
		else if (formatStr.equals("dd")) {
			field = Calendar.DAY_OF_MONTH;
		}
		else if (formatStr.equals("uu")) {
			field = Calendar.DAY_OF_WEEK;
		}
		else if (formatStr.equals("HH")) {
			field = Calendar.HOUR_OF_DAY;
		}
		else if (formatStr.equals("mm")) {
			field = Calendar.MINUTE;
		}
		else if (formatStr.equals("ss")) {
			field = Calendar.SECOND;
		}
		else if (formatStr.equals("SSS")) {
			field = Calendar.MILLISECOND;
		}
		else {
			throw new NumberFormatException(MessageStrings.NUMBER_FORMAT);
		}
		
		Calendar dataBaseTime = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));
		int []  vals = new int[timesMills.length];
				
		for (int i = 0; i < timesMills.length; i ++ ) {
			dataBaseTime.setTimeInMillis(timesMills[i]);
			vals[i] = dataBaseTime.get(field);
		}
		return vals;
	}
	
	public static String[] POSIXTimeToDateStr(long timesMills, DateFormat dateFormat) {
		return POSIXTimeToDateStr(new long [] {timesMills}, dateFormat);
	}
	
	public static String[] POSIXTimeToDateStr(long [] timesMills, DateFormat dateFormat) {
		Calendar dataBaseTime = new GregorianCalendar(TimeZone.getTimeZone("America/New_York"));
		dateFormat.setCalendar(dataBaseTime);
		String []  datesStr = new String[timesMills.length];		
		for (int i = 0; i < timesMills.length; i ++ ) {
			dataBaseTime.setTimeInMillis(timesMills[i]);
			datesStr[i] = dateFormat.format(dataBaseTime.getTime());
		}
		return datesStr;
	}
	
	public static String[] POSIXTimeToDateStr(long [] timesMills) {
		return POSIXTimeToDateStr(timesMills, FULL_DATE_FORMAT);
	}
	
	public static String[] POSIXTimeToDateStr(long timesMills) {
		return POSIXTimeToDateStr(new long[] {timesMills}, FULL_DATE_FORMAT);
	}
	
	public static long[] toPOSIXTime(String timeString) {
		return toPOSIXTime(new String[] {timeString});
	}
	
	public static long[] toPOSIXTime(String[] timeString) {
		long[] timeLong = new long[timeString.length];
		for (int i = 0; i < timeString.length; i++)
			timeLong[i] = Timestamp.valueOf(timeString[i]).getTime();
		return timeLong;
	}	
	
}
