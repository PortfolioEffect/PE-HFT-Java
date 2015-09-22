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
package com.portfolioeffect.quant.client.util;

public class Console {
	public static void write(String str){
		System.out.print(str);
	}
	
	public static void writeln(String str){
		System.out.println(str);
	}
	
	public static void writeMessage(String str){
		System.out.println("\n"+str);
	}
	
	public static void writeStackTrace(Exception e){
		e.printStackTrace();
	}
}
