package com.nmims.coursera.helpers.openSAML;

import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.ArtifactResolutionService;
import org.opensaml.saml.saml2.metadata.EncryptionMethod;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml.saml2.metadata.impl.OrganizationBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationDisplayNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationNameBuilder;
import org.opensaml.saml.saml2.metadata.impl.OrganizationURLBuilder;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.encryption.KeySize;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CourseraMetadataGenerator {
	
	@Autowired
	private OpenSAMLSignatureFieldsGenerator signatureFieldsGenerator;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${COURSERA_METADATA_LOCATION}")
	private String COURSERA_METADATA_LOCATION;

	@Value("${COURSERA_IDP_SERVICE_LOCATION}")
	private String COURSERA_IDP_SERVICE_LOCATION;
	
	public static void init() throws InitializationException{
		InitializationService.initialize();
	}
	
	public EntityDescriptor generateMetadata() {
		EntityDescriptor idpEntityDescriptor = OpenSAMLUtils.buildSAMLObject(EntityDescriptor.class);
		idpEntityDescriptor.setEntityID(COURSERA_METADATA_LOCATION);
		IDPSSODescriptor idpSSODescriptor = getIDPSSODescriptor();
		idpEntityDescriptor.getRoleDescriptors().add(idpSSODescriptor);
		idpEntityDescriptor.setOrganization(generateOrganization());
		return idpEntityDescriptor;
	}
	
	private IDPSSODescriptor getIDPSSODescriptor() {
		IDPSSODescriptor idpSSODescriptor = OpenSAMLUtils.buildSAMLObject(IDPSSODescriptor.class);
		idpSSODescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
		idpSSODescriptor.setWantAuthnRequestsSigned(false);
		idpSSODescriptor.getKeyDescriptors().add(getSigningKeyDescriptor());
		idpSSODescriptor.getKeyDescriptors().add(getEncryptionKeyDescriptor());
		idpSSODescriptor.getArtifactResolutionServices().add(getArtifactResolutionServicePOST());
		idpSSODescriptor.getArtifactResolutionServices().add(getArtifactResolutionServiceRedirect());
		idpSSODescriptor.getNameIDFormats().add(getNameIdFormat());
		idpSSODescriptor.getSingleSignOnServices().add(getPostSingleSignOnService());
		idpSSODescriptor.getSingleSignOnServices().add(getRedirectSingleSignOnService());
		return idpSSODescriptor;
	}
	
	private KeyDescriptor getSigningKeyDescriptor() {
		KeyDescriptor signingKeyDescriptor = OpenSAMLUtils.buildSAMLObject(KeyDescriptor.class);
		signingKeyDescriptor.setUse(UsageType.SIGNING);
		Credential credential = signatureFieldsGenerator.getCredential();
		signingKeyDescriptor.setKeyInfo(signatureFieldsGenerator.getKeyInfo(credential));
		return signingKeyDescriptor;
	}
	
	private KeyDescriptor getEncryptionKeyDescriptor() {
		KeyDescriptor encryptionKeyDescriptor = OpenSAMLUtils.buildSAMLObject(KeyDescriptor.class);
		encryptionKeyDescriptor.setUse(UsageType.ENCRYPTION);
		Credential credential = signatureFieldsGenerator.getCredential();
		encryptionKeyDescriptor.setKeyInfo(signatureFieldsGenerator.getKeyInfo(credential));
		encryptionKeyDescriptor.getEncryptionMethods().add(getEncryptionMethod());
		return encryptionKeyDescriptor;
	}
	
	private EncryptionMethod getEncryptionMethod() {
		EncryptionMethod encryptionMethod = OpenSAMLUtils.buildSAMLObject(EncryptionMethod.class);
		encryptionMethod.setAlgorithm(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128);
		KeySize keySize = OpenSAMLUtils.buildSAMLObject(KeySize.class);
		keySize.setValue(128);
		encryptionMethod.setKeySize(keySize);
		return encryptionMethod;
	}
	
	private ArtifactResolutionService getArtifactResolutionServicePOST() {
		ArtifactResolutionService artifactResolutionService = OpenSAMLUtils.buildSAMLObject(ArtifactResolutionService.class);
		artifactResolutionService.setLocation(COURSERA_IDP_SERVICE_LOCATION);
		artifactResolutionService.setIndex(0);
		artifactResolutionService.setBinding(SAMLConstants.POST_METHOD);
		return artifactResolutionService;
	}
	
	private ArtifactResolutionService getArtifactResolutionServiceRedirect() {
		ArtifactResolutionService artifactResolutionService = OpenSAMLUtils.buildSAMLObject(ArtifactResolutionService.class);
		artifactResolutionService.setLocation(COURSERA_IDP_SERVICE_LOCATION);
		artifactResolutionService.setIndex(0);
		artifactResolutionService.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		return artifactResolutionService;
	}
	
	private NameIDFormat getNameIdFormat() {
		NameIDFormat nameIdFormat = OpenSAMLUtils.buildSAMLObject(NameIDFormat.class);
		nameIdFormat.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:unspecified");
		return nameIdFormat;
	}

	private SingleSignOnService getPostSingleSignOnService() {
		SingleSignOnService ssoService = OpenSAMLUtils.buildSAMLObject(SingleSignOnService.class);
		ssoService.setBinding(SAMLConstants.POST_METHOD);
		ssoService.setLocation(COURSERA_IDP_SERVICE_LOCATION);
		return ssoService;
	}
	
	private SingleSignOnService getRedirectSingleSignOnService() {
		SingleSignOnService ssoService = OpenSAMLUtils.buildSAMLObject(SingleSignOnService.class);
		ssoService.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
		ssoService.setLocation(COURSERA_IDP_SERVICE_LOCATION);
		return ssoService;
	}
	
	private Organization generateOrganization() {
	    OrganizationBuilder organizationBuilder = new OrganizationBuilder();
	    Organization organization = organizationBuilder.buildObject();

	    //Setting Organization Name
	    OrganizationNameBuilder nameBuilder = new OrganizationNameBuilder();
	    OrganizationName organizationName = nameBuilder.buildObject();
	    organizationName.setXMLLang("en-US");
	    organizationName.setValue("SVKM's NMIMS");
	    organization.getOrganizationNames().add(organizationName);
	    
	    //Setting Organization Display Name
	    OrganizationDisplayNameBuilder displayNameBuilder = new OrganizationDisplayNameBuilder();
	    OrganizationDisplayName organizationDisplayName = displayNameBuilder.buildObject();
	    organizationDisplayName.setXMLLang("en-US");
	    organizationDisplayName.setValue("SVKM's NMIMS");
	    organization.getDisplayNames().add(organizationDisplayName);
	    
	    //Setting Organization URL
	    OrganizationURLBuilder urlBuilder = new OrganizationURLBuilder();
	    OrganizationURL organizationURL = urlBuilder.buildObject();
	    organizationURL.setXMLLang("en-US");
	    organizationURL.setValue(SERVER_PATH);
	    organization.getURLs().add(organizationURL);
	    
	    return organization;
	}

}
