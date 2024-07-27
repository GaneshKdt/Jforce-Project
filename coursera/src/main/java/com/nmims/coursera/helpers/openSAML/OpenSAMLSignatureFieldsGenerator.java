package com.nmims.coursera.helpers.openSAML;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.keyinfo.KeyInfoGenerator;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorFactory;
import org.opensaml.xmlsec.keyinfo.KeyInfoGeneratorManager;
import org.opensaml.xmlsec.keyinfo.NamedKeyInfoGeneratorManager;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.impl.KeyNameBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenSAMLSignatureFieldsGenerator {

	private static BasicX509Credential signingCredential = null;
	final static Signature signature = null;
	
	@Value("${METTL_SAML_SSL_PASSWORD}")
	private String CS_SAML_SSL_PASSWORD;
		
	@Value("${METTL_SAML_SSL_CERT_ALIAS_NAME}")
	private String CS_SAML_SSL_CERT_ALIAS_NAME;
		
	@Value("${METTL_SAML_SSL_CERT_FILE_NAME}")
	private String CERT_FILE_NAME;
	
	public Credential getCredential() {
		generateCredential();
		return signingCredential;
	}
	
	private void generateCredential(){
		KeyStore ks = null;
		FileInputStream fis = null;
		char[] password = this.CS_SAML_SSL_PASSWORD.toCharArray();
			
		try {
			// Get Default Instance of KeyStore
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			fis = new FileInputStream(CERT_FILE_NAME);
	 
			// Load KeyStore
			ks.load(fis, password);
	 
			// Close InputFileStream
			fis.close();
			// Get Private Key Entry From Certificate
			KeyStore.PrivateKeyEntry pkEntry = null;
	
			pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry(this.CS_SAML_SSL_CERT_ALIAS_NAME, new KeyStore.PasswordProtection(
					this.CS_SAML_SSL_PASSWORD.toCharArray()));
			PrivateKey pk = pkEntry.getPrivateKey();
			X509Certificate certificate = (X509Certificate) pkEntry.getCertificate();
			BasicX509Credential credential = new BasicX509Credential(certificate, pk);

			signingCredential = credential;
		} catch (Exception e) {
//			System.out.println("Failed to Get Private Entry From the keystore:: " + this.CERT_FILE_NAME+ e);
		}
	}
	
	public KeyInfo getKeyInfo(Credential cred) {
		return getKeyInfoI(cred);
	}
	
	private KeyInfo getKeyInfoI(Credential c) {
		KeyName keyName = new KeyNameBuilder().buildObject();
		EncryptionConfiguration secConfiguration = SecurityConfigurationSupport.getGlobalEncryptionConfiguration();
		NamedKeyInfoGeneratorManager namedKeyInfoGeneratorManager = secConfiguration.getDataKeyInfoGeneratorManager();
		KeyInfoGeneratorManager keyInfoGeneratorManager = namedKeyInfoGeneratorManager.getDefaultManager();
		KeyInfoGeneratorFactory keyInfoGeneratorFactory = keyInfoGeneratorManager.getFactory(c);
		KeyInfoGenerator keyInfoGenerator = keyInfoGeneratorFactory.newInstance();
		KeyInfo keyInfo;
		try {
			keyInfo = keyInfoGenerator.generate(c);
			keyInfo.getKeyNames().add(keyName);
			return keyInfo;			
		} catch (SecurityException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	
}
