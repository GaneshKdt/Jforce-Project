package com.nmims.helpers;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChatBotEncryptionHelper {
	//key which is provided by Findability science
	private static final String KEY ="ZK2wWDs5pgUP69gL";
	//vector which is provided by Findability science
	private static final String VECTOR="pn9dzwzJ58p64dLj";
	private static Logger logger = (Logger)LoggerFactory.getLogger(ChatBotEncryptionHelper.class);
	public String encrypt(String stringToBeEnc) {
		try 
		{
		IvParameterSpec iv= new IvParameterSpec(VECTOR.getBytes("UTF-8"));
		SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"),"AES" );
		Cipher cipher =Cipher.getInstance("AES/CBC/PKCS5PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec,iv);
		byte[] encrypted= cipher.doFinal(stringToBeEnc.getBytes());
		System.out.println("This is the encrypted data "+Base64.encodeBase64String(encrypted));
		return Base64.encodeBase64String(encrypted);
		} catch (Exception e)
		{
			logger.error("Exception in the Catch block of the encrypt "+ e);
		}
		return  null;
	}
	public String decrypt(String sapid) {
		try {
			IvParameterSpec iv = new IvParameterSpec(VECTOR.getBytes("UTF-8"));
			SecretKeySpec secretKey = new SecretKeySpec(KEY.getBytes("UTF-8"),"AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(cipher.DECRYPT_MODE, secretKey,iv);
			byte[] decryptedsapid=cipher.doFinal(Base64.decodeBase64(sapid));
			System.out.println("This is the decryptedsapid "+ new String(decryptedsapid));
			return new String (decryptedsapid);
		} catch (Exception e) {
			logger.error("Exception in the Catch block of the decrypt "+ e);
		}
		return null;
	}
}