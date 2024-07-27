package com.nmims.helpers;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.*;

public class AESencrp {

	private static final String ALGO = "AES";
	private static final byte[] keyValue = 
			new byte[] { 'T', '5', 'e', '8', 'e', 'Z', 't',
		'S', 'W', '%', 'r','T', ')', 'K', '0', '#' };

	public static String encrypt(String Data) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encryptedValue = new BASE64Encoder().encode(encVal);
		return encryptedValue;
	}

	public static String decrypt(String encryptedData) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}
	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(keyValue, ALGO);
		return key;
	}
	
	public static void main(String[] args) throws Exception {
		String password = "77108060073";
        String passwordEnc = AESencrp.encrypt(password);
        String passwordDec = AESencrp.decrypt("h7B/NQilD YCCNT8M/gwQbLs2Bxsx8unoLjvVDaAm7w=");

        System.out.println("Plain Text : " + password);
        System.out.println("Encrypted Text : " + passwordEnc);
        System.out.println("Decrypted Text : " + passwordDec);
	}

}