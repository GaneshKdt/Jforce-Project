package com.nmims.daos;

import java.io.UnsupportedEncodingException;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.InvalidAttributeValueException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.OperationNotSupportedException;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import com.nmims.beans.PersonAcads;
import com.nmims.helpers.PersonAttributesMapper;

public class LDAPDao {
	
	private static final Logger logger = LoggerFactory.getLogger(LDAPDao.class);
	
	private LdapTemplate ldapTemplate;
	private static final String ADS_UF_DONT_EXPIRE_PASSWD_NORMAL_ACCNT = "66048";

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	
	
	public PersonAcads findPerson(String userId) {
		String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		return (PersonAcads) ldapTemplate.lookup(dn, 
				new PersonAttributesMapper());
		/*Person p = new Person();
		p.setFirstName("Sachin");
		p.setLastName("Parab");
		return p;*/
	}

	public boolean login(String username, String password) throws Exception{
		
		AndFilter filter = new AndFilter();
		ldapTemplate.setIgnorePartialResultException(true); 
		filter.and(new EqualsFilter("objectclass", "person")).and(new EqualsFilter("cn", username));
		return ldapTemplate.authenticate(DistinguishedName.EMPTY_PATH, filter.toString(), password);
		//return true;
	}
	
	private byte[] encodePassword(String password) throws UnsupportedEncodingException {
		String newQuotedPassword = "\"" + password + "\"";
		return newQuotedPassword.getBytes("UTF-16LE");
	}
	
	public void changePassword(String newPassword, String userId) throws Exception{
		
		try {
		    ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", encodePassword( newPassword )) );
		    ModificationItem repitem2 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword",  newPassword ) );
		    //String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
		    String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		    DistinguishedName userDN = new DistinguishedName( dn);
		    
		    ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1, repitem2 } );
		    logger.info("Password changed successfuly");
		} catch ( Exception exc ) {
			logger.error( "changePassword()", exc);
			
			throw exc;
		}
	}
	
public void changeRoles(String roles, String userId) throws Exception{
		
		try {
		    PersonAcads p = findPerson(userId);
		    roles = p.getRoles() +","+roles;
		    ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("businessRoles",  roles ) );
		    //String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
		    String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		    DistinguishedName userDN = new DistinguishedName( dn);
		    
		    ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1 } );
		    logger.info("Roles changed successfuly");
		} catch ( Exception exc ) {
			logger.error( "changeRoles()", exc);
			
			throw exc;
		}
	}
	
	public void updateFirstTimeProfile(String newPassword, String userId, String email,String mobile,String altMobile, String postalAddress) throws Exception{
			
			try {
				
				if(altMobile == null || "".equals(altMobile.trim())){
					altMobile = "Not Available";
				}
			    ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", encodePassword( newPassword )) );
			    ModificationItem repitem2 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword",  newPassword ) );
			    ModificationItem repitem3 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail",  email ) );
			    ModificationItem repitem4 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mobile",  mobile ) );
			    ModificationItem repitem5 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("postalAddress",  postalAddress ) );
			    ModificationItem repitem6 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("info",  "Confirmed" ) );
			    ModificationItem repitem7 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("otherHomePhone",  altMobile ) );
			    
			    //String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
			    String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
			    DistinguishedName userDN = new DistinguishedName( dn);
			    
			    ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1, repitem2,  repitem3, repitem4, repitem5, repitem6, repitem7} );
			    logger.info("Password and profile changed successfuly");
			} catch ( Exception exc ) {
				logger.error( "changePassword()", exc);
				
				throw exc;
			}
		}
	
	public void updateProfile( String userId, String email,String mobile,String altMobile, String postalAddress) throws Exception{
		
		try {

			if(altMobile == null || "".equals(altMobile.trim())){
				altMobile = "Not Available";
			}
			
		    ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail",  email ) );
		    ModificationItem repitem2 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mobile",  mobile ) );
		    ModificationItem repitem3 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("postalAddress",  postalAddress ) );
		    ModificationItem repitem4 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("otherHomePhone",  altMobile ) );
		    
		    //String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
		    String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		    DistinguishedName userDN = new DistinguishedName( dn);
		    
		    ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1, repitem2,  repitem3, repitem4} );
		    logger.info("Profile updated successfuly");
		} catch ( Exception exc ) {
			logger.error( "changePassword()", exc);
			
			throw exc;
		}
	}
	
public void updateProgram( String userId, String program) throws Exception{
		
		try {

			
		    ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("department",  program ) );

		    
		    //String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
		    String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		    DistinguishedName userDN = new DistinguishedName( dn);
		    
		    ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1} );
		} catch ( Exception exc ) {
			logger.error( "changePassword()", exc);
			
			throw exc;
		}
	}

public void createUser(PersonAcads p, String roles)  {
	try {
	    Attributes personAttributes = new BasicAttributes();
	    String userId = p.getUserId();
	    personAttributes.put( "objectclass", "person" );
	    personAttributes.put( "objectclass", "user" );
	    personAttributes.put( "givenName", p.getFirstName() );
	    personAttributes.put( "userPrincipalName", userId+"@svkmgrp.com");
	    personAttributes.put( "sn", p.getLastName());
	    personAttributes.put( "description", "Created via student portal" );
	    personAttributes.put( "sAMAccountName", userId);
	    //personAttributes.put( "userAccountControl", "512" ); /// 512 = normal luser
	    personAttributes.put( "userPassword", p.getPassword() ); 
	    personAttributes.put( "businessRoles", roles ); 
	    
	    if(p.getEmail() != null && (!"".equals(p.getEmail().trim())) ){
	    	personAttributes.put( "mail", p.getEmail() ); 
	    }
	    if(p.getContactNo() != null && (!"".equals(p.getContactNo().trim())) ){
	    	personAttributes.put( "mobile", p.getContactNo() ); 
	    }
	    
	    if(p.getAltContactNo() != null && (!"".equals(p.getAltContactNo().trim())) ){
	    	personAttributes.put( "otherHomePhone", p.getAltContactNo() ); 
	    }
	    
	    if(p.getPostalAddress() != null && (!"".equals(p.getPostalAddress().trim())) ){
	    	personAttributes.put( "postalAddress", p.getPostalAddress() ); 
	    }
	    
	    if(p.getProgram() != null && (!"".equals(p.getProgram().trim())) ){
	    	personAttributes.put( "department", p.getProgram() ); 
	    }
 

	    personAttributes.put("unicodepwd", encodePassword( p.getPassword() ) );
	    personAttributes.put( "displayName", p.getFirstName()+" "+p.getLastName()); 
	    //personAttributes.put( "accountExpires", ACCOUNT_NEVER_EXPIRE_VALUE); 
	    personAttributes.put( "userAccountControl", ADS_UF_DONT_EXPIRE_PASSWD_NORMAL_ACCNT );
	    
	    String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
	    // Set up user distinguished name and clreate it.
	    DistinguishedName newUserDN = new DistinguishedName( dn);
	    ldapTemplate.bind(newUserDN, null, personAttributes);
	    
	} catch ( InvalidAttributeValueException exc ) {
		
		logger.error( "createUser()", exc);
		p.setErrorMessage("Invalid value for attributes for student "+p.getUserId());
		p.setErrorRecord(true);
		//throw new LdapSaveException( exc.getMessage() );
	} catch ( NameAlreadyBoundException exc ) {   /// USER EXISTS....
		//
		
		logger.error( "createUser()", exc);
		p.setErrorMessage("User already exists : "+p.getUserId());
		p.setErrorRecord(true);
		//throw new DuplicateUserException(  "User ["+ luzer.getEmailAddress() + "] allready exists in AD." );
	} catch ( NameNotFoundException exc ) {
		
		logger.error( "createUser()", exc);
		p.setErrorMessage("Unknown error for student "+p.getUserId());
		p.setErrorRecord(true);
		//throw new LdapSaveException( exc.getMessage() );
	} catch ( OperationNotSupportedException exc ) {  // CAN NOT ADD USER
		
		logger.error( "createUser()", exc);
		p.setErrorMessage("Password not matching policies for student "+p.getUserId());
		p.setErrorRecord(true);
		//throw new PasswordStrengthException( exc.getMessage() );
	} catch ( Exception exc ) {
		
		logger.error( "createUser()", exc);
		p.setErrorMessage("Unknown error for student "+p.getUserId());
		p.setErrorRecord(true);
		//throw new LdapSaveException( exc.getMessage() );
	}
}

}
