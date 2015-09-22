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
package com.portfolioeffect.quant.client.message.util;
import gnu.crypto.cipher.Rijndael;
import gnu.crypto.util.Base64;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.security.InvalidKeyException;

public class CryptograhicUtils {

	public static String encrypt (String message, String key) throws InvalidKeyException, UnsupportedEncodingException {
		byte[] plainText;
		byte[] encryptedText;
		Rijndael cipher = new Rijndael();
		// create a key
		byte[] keyBytes = key.getBytes();
		Object keyObject = cipher.makeKey(keyBytes, 16);
		//make the length of the text a multiple of the block size
		if ((message.length() % 16) != 0) {
			while ((message.length() % 16) != 0) {
				message += " ";
			}
		}
		// initialize byte arrays for plain/encrypted text
		plainText = message.getBytes("UTF8");
		encryptedText = new byte[message.length()];
		// encrypt text in 8-byte chunks
		for (int i=0; i<Array.getLength(plainText); i+=16) {
			cipher.encrypt(plainText, i, encryptedText, i, keyObject, 16);
		}
		String encryptedString = Base64.encode(encryptedText);
		return encryptedString;
	}

	public static String decrypt (String message, String key) throws InvalidKeyException, UnsupportedEncodingException {
		byte[] encryptedText;
		byte[] decryptedText;
		Rijndael cipher = new Rijndael();
		//create the key
		byte[] keyBytes = key.getBytes();
		Object keyObject = cipher.makeKey(keyBytes, 16);
		//make the length of the string a multiple of
		//the block size
		if ((message.length() % 16) != 0) {
			while ((message.length() % 16) != 0) {
				message += " ";
			}
		}
		//initialize byte arrays that will hold encrypted/decrypted
		//text
		encryptedText = Base64.decode(message);
		decryptedText = new byte[message.length()];
		//Iterate over the byte arrays by 16-byte blocks and decrypt.
		for (int i=0; i<Array.getLength(encryptedText); i+=16) {
			cipher.decrypt(encryptedText, i, decryptedText, i, keyObject, 16);
		}
		String decryptedString = new String(decryptedText, "UTF8");
		return decryptedString;
	}
	
}
