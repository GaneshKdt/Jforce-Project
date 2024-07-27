package com.nmims.helpers.openSAML.idp;

import org.joda.time.DateTime;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nmims.beans.SamlRequest;
import com.nmims.helpers.openSAML.OpenSAMLSignatureFieldsGenerator;
import com.nmims.helpers.openSAML.OpenSAMLUtils;


@Component
public class ArtifactResolver{

	@Autowired
	OpenSAMLSignatureFieldsGenerator signatureFieldsGenerator;

	@Value("${REV_SSO_REV_DESTINATION}")
	private String REV_SSO_REV_DESTINATION;

	@Value("${REV_SSO_REV_RESPONSE_TO}")
	private String REV_SSO_REV_RESPONSE_TO;

	@Value("${CS_SAML_IDP_ID}")
	private String CS_SAML_IDP_ID;

	@Value("${CS_SAML_SSL_CERT_ALIAS_NAME}")
	private String CS_SAML_SSL_CERT_ALIAS_NAME;

	private static final Logger logger = LoggerFactory.getLogger(ArtifactResolver.class);
 
	SamlRequest params = new SamlRequest();
	   public Response generateSAMLResponse(SamlRequest samlReq){
	    	 params = samlReq;
	         
	    	 ArtifactResolver.init();
	    	 Response response = buildResponse();
	    	 return response;
	    }

     public static void init(){
         try {
             InitializationService.initialize();
         } catch (InitializationException e) {
 			logger.info("exception : "+e.getMessage());      
	 	}
     }

     private Response buildResponse() {

          //build response using format sent by rev
          Response response = OpenSAMLUtils.buildSAMLObject(Response.class);
          response.setDestination(REV_SSO_REV_DESTINATION);
          response.setIssueInstant(new DateTime());
          response.setInResponseTo(REV_SSO_REV_RESPONSE_TO);
          
          response.setID(CS_SAML_IDP_ID);
          
          Issuer issuer2 = OpenSAMLUtils.buildSAMLObject(Issuer.class);
          issuer2.setValue(CS_SAML_IDP_ID);

          response.setIssuer(issuer2);
          Status status2 = OpenSAMLUtils.buildSAMLObject(Status.class);
          StatusCode statusCode2 = OpenSAMLUtils.buildSAMLObject(StatusCode.class);
          statusCode2.setValue(StatusCode.SUCCESS);
          status2.setStatusCode(statusCode2);

          response.setStatus(status2);
          
          Assertion assertion = buildAssertion();
          response.getAssertions().add(assertion);

          signResponse(response);
          return response;
      }
     
    private void signResponse(Response response) {
    	
        Signature signature = OpenSAMLUtils.buildSAMLObject(Signature.class);
        Credential credential = signatureFieldsGenerator.getCredential();
//        signature.setSigningCredential(credential);
//       signature.setKeyInfo(credentialGenerator.getKeyInfo(credential));
        signature.setKeyInfo(signatureFieldsGenerator.getKeyInfo(credential));
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);

        // Need to supply an org.opensaml.security.credential.Credential;
        signature.setSigningCredential(credential);
        response.setSignature(signature);
        try {
            XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller(response).marshall(response);
        } catch (MarshallingException e) {
            throw new RuntimeException(e);
        }

        try {
            Signer.signObject(signature);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Assertion buildAssertion() {

		
		
        Assertion assertion = OpenSAMLUtils.buildSAMLObject(Assertion.class);

        Issuer issuer = OpenSAMLUtils.buildSAMLObject(Issuer.class);
        issuer.setValue(CS_SAML_IDP_ID);
        assertion.setIssuer(issuer);
        assertion.setIssueInstant(new DateTime());

        assertion.setID(OpenSAMLUtils.generateSecureRandomId());

        Subject subject = OpenSAMLUtils.buildSAMLObject(Subject.class);
        assertion.setSubject(subject);

        NameID nameID = OpenSAMLUtils.buildSAMLObject(NameID.class);
//        nameID.setFormat(NameIDType.TRANSIENT);
        nameID.setValue(CS_SAML_SSL_CERT_ALIAS_NAME);
//        nameID.setSPNameQualifier("SP name qualifier");
//        nameID.setNameQualifier("Name qualifier");

        subject.setNameID(nameID);

        subject.getSubjectConfirmations().add(buildSubjectConfirmation());

//        assertion.setConditions(buildConditions());

        

        assertion.getAuthnStatements().add(buildAuthnStatement());
        
        assertion.getAttributeStatements().add(buildAttributeStatement(params));


        return assertion;
    }

    private SubjectConfirmation buildSubjectConfirmation() {
        SubjectConfirmation subjectConfirmation = OpenSAMLUtils.buildSAMLObject(SubjectConfirmation.class);
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

        SubjectConfirmationData subjectConfirmationData = OpenSAMLUtils.buildSAMLObject(SubjectConfirmationData.class);
//        subjectConfirmationData.setInResponseTo("Made up ID");
//        subjectConfirmationData.setNotBefore(new DateTime().minusDays(2));
        subjectConfirmationData.setNotOnOrAfter(new DateTime().plusDays(2));
//        subjectConfirmationData.setRecipient(SPConstants.ASSERTION_CONSUMER_SERVICE);

        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

        return subjectConfirmation;
    }

    private AuthnStatement buildAuthnStatement() {
        AuthnStatement authnStatement = OpenSAMLUtils.buildSAMLObject(AuthnStatement.class);
        AuthnContext authnContext = OpenSAMLUtils.buildSAMLObject(AuthnContext.class);
        AuthnContextClassRef authnContextClassRef = OpenSAMLUtils.buildSAMLObject(AuthnContextClassRef.class);
        authnContextClassRef.setAuthnContextClassRef(AuthnContext.PPT_AUTHN_CTX);
        authnContext.setAuthnContextClassRef(authnContextClassRef);
        authnStatement.setAuthnContext(authnContext);

        authnStatement.setAuthnInstant(new DateTime());

        return authnStatement;
    }

//    private Conditions buildConditions() {
//        Conditions conditions = OpenSAMLUtils.buildSAMLObject(Conditions.class);
//        conditions.setNotBefore(new DateTime().minusDays(2));
//        conditions.setNotOnOrAfter(new DateTime().plusDays(2));
//        AudienceRestriction audienceRestriction = OpenSAMLUtils.buildSAMLObject(AudienceRestriction.class);
//        Audience audience = OpenSAMLUtils.buildSAMLObject(Audience.class);
//        audience.setAudienceURI(SPConstants.ASSERTION_CONSUMER_SERVICE);
//        audienceRestriction.getAudiences().add(audience);
//        conditions.getAudienceRestrictions().add(audienceRestriction);
//        return conditions;
//    }

    private AttributeStatement buildAttributeStatement(SamlRequest parameters) {
        AttributeStatement attributeStatement = OpenSAMLUtils.buildSAMLObject(AttributeStatement.class);

        attributeStatement.getAttributes().add(createAttribute("first_name", parameters.getFirstName()));
        attributeStatement.getAttributes().add(createAttribute("last_name", parameters.getLastName()));
        attributeStatement.getAttributes().add(createAttribute("email_id", parameters.getEmailId()));
        attributeStatement.getAttributes().add(createAttribute("interaction_source", parameters.getInteractionSource()));
       
        Attribute commonName = createAttribute("http://schemas.xmlsoap.org/claims/CommonName", parameters.getUserId());
        commonName.setNameFormat("urn:oasis:names:tc:SAML:2.0:attrname-format:basic");
        attributeStatement.getAttributes().add(commonName);
        
        attributeStatement.getAttributes().add(createAttribute("Reg_Code", parameters.getReg_Code()));

        return attributeStatement;

    }
    
    private Attribute createAttribute(String name, String value) {

        Attribute attribute = OpenSAMLUtils.buildSAMLObject(Attribute.class);

        XSStringBuilder stringBuilder = (XSStringBuilder)XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
        XSString xmlValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        if(value != null) {
        	xmlValue.setValue(value);
    	}else {
    		xmlValue.setValue("");
    	}
        attribute.getAttributeValues().add(xmlValue);
        attribute.setName(name);
        
        return attribute;
    }
}
