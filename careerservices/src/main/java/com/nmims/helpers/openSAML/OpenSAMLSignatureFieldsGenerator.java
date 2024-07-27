package com.nmims.helpers.openSAML;
 
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

 
@Service
public class OpenSAMLSignatureFieldsGenerator{
	private static BasicX509Credential signingCredential = null;
	final static Signature signature = null;
	

	@Value("${CS_SAML_SSL_PASSWORD}")
	private String CS_SAML_SSL_PASSWORD;
		
	@Value("${CS_SAML_SSL_CERT_ALIAS_NAME}")
	private String CS_SAML_SSL_CERT_ALIAS_NAME;
		
	@Value("${CS_SAML_SSL_CERT_FILE_NAME}")
	private String CS_SAML_SSL_CERT_FILE_NAME;

	private static final Logger logger = LoggerFactory.getLogger(OpenSAMLSignatureFieldsGenerator.class);
 
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
			fis = new FileInputStream(CS_SAML_SSL_CERT_FILE_NAME);
	 
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
			// Get Public Key Entry From Certificate
//			KeyStore.P publicKey = ks.getEntry(this.CS_SAML_SSL_CERT_ALIAS_NAME, new KeyStore.PasswordProtection(
//					this.CS_SAML_SSL_PASSWORD.toCharArray()));
//			
//				
//			credential.setPublicKey();
			signingCredential = credential;
		} catch ( Throwable throwable ) {
			logger.info("Failed to Get Private Entry From the keystore:: " + this.CS_SAML_SSL_CERT_FILE_NAME+ 
					ExceptionUtils.getFullStackTrace(throwable));
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
			logger.info("in allocateFacultyToQueries got exception : "+e.getMessage());
			return null;
		}
	}
}