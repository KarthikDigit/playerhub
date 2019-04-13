package com.playerhub.preference;

import android.annotation.SuppressLint;
import android.util.Log;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

	static final String TAG = "Encryption";

	/*
	 * AES 128/192/256 Encryption
	 */

	/*
	 * Key generation based on the seed and keylength 
	 * @param seed - secret seed can be string/numeric/alphanumeric/mix of acceptable characters
	 * @param keylength - can be 128/192/256
	 * @return key
	 */
	@SuppressLint("TrulyRandom")
	public static SecretKeySpec generateSecretKey(String seed, int keylength) {
		// Set up secret key spec for 128/192/256 bit AES encryption and decryption
		SecretKeySpec secretKeySpec = null;
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(seed.getBytes("UTF-8"));
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(keylength, secureRandom);
			secretKeySpec = new SecretKeySpec(
					(keyGenerator.generateKey()).getEncoded(), "AES");
		} catch (Exception e) {
			Log.e(TAG, "AES secret key spec error");
		}

		return secretKeySpec;
	}


	/**
	 * Encrypts the string using AES
	 * 
	 * @param stringToEncrypt - the string which needs to be encrypted
	 * @param secretKeySpec - secret key generated based on seed and key length
	 * @return - the encrypted in byte format
	 */
	public static byte[] encryptAES(String stringToEncrypt, SecretKeySpec secretKeySpec) {

		// Encode the original data with AES
		byte[] encodedBytes = null;
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			encodedBytes = cipher.doFinal(stringToEncrypt.getBytes("UTF-8"));
		} catch (Exception e) {
			Log.e(TAG, "AES encryption error");
		}

		return encodedBytes;
	}

	/**
	 * Decrypts the string using AES
	 * 
	 * NOTE: Be careful, we need to pass encodedbytes for decryption
	 * 
	 * 
	 * @param toDecrypt - the string which needs to be decrypted
	 * @param secretKeySpec - secret key generated based on seed and key length
	 * @return - the decrypted in byte format
	 */
	public static byte[] decryptAES(byte[] toDecrypt,
			SecretKeySpec secretKeySpec) {

		// Decode the encoded data with AES
		byte[] decodedBytes = null;
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			decodedBytes = cipher.doFinal(toDecrypt);
		} catch (Exception e) {
			Log.e(TAG, "AES decryption error");
		}

		return decodedBytes;
	}

}