package com.wms.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author truongbx
 * @since 3/12/2020
 */
 public class EncyptionSecurity {
	private  byte[] getSHA(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			return md.digest(input.trim().getBytes(StandardCharsets.UTF_8));
		}catch (Exception e){
			return null;
		}

	}

	private  String toHexString(byte[] hash) {
		if (hash == null){
			return "";
		}
		// Convert byte array into signum representation
		BigInteger number = new BigInteger(1, hash);

		// Convert message digest into hex value
		StringBuilder hexString = new StringBuilder(number.toString(16));

		// Pad with leading zeros
		while (hexString.length() < 32)
		{
			hexString.insert(0, '0');
		}

		return hexString.toString();
	}
	public String encrypt(String input){
		return toHexString(getSHA(input));
	}
}
