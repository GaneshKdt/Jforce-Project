package com.nmims.helpers;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service("almashinesHelper")
public class AlmashinesHelper {

	@Value("${ALMASHINES_API_KEY}")
	private String API_KEY;
	@Value("${ALMASHINES_API_SECRET}")
	private String API_SECRET;

	public String almashinesEncryptCipher(String plainText) throws Exception {
		final Cipher encryptCipher = Cipher.getInstance("AES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, generateMySQLAESKey(API_SECRET, "UTF-8"));
		String encryptedText = new String(Hex.encodeHex(encryptCipher.doFinal(plainText.getBytes("UTF-8"))));

		return encryptedText;
	}

	private SecretKeySpec generateMySQLAESKey(String key, String encoding) throws Exception {

		final byte[] finalKey = new byte[16];
		int i = 0;
		for (byte b : key.getBytes(encoding))
			finalKey[i++ % 16] ^= b;
		return new SecretKeySpec(finalKey, "AES");
	}

	public String loginUser(String encryptedText) {

		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<String> response = null;
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("apikey", API_KEY);
		body.add("apisecret", API_SECRET);
		body.add("data", encryptedText);
		String url = "https://www.almashines.com/data/api/loginUser";
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		RestTemplate restTemplate = new RestTemplate();
		response = restTemplate.postForEntity(url, requestEntity, String.class);

		return response.getHeaders().getLocation().toString();

	}

}
