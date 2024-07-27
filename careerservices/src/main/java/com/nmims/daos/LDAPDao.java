package com.nmims.daos;

import java.io.UnsupportedEncodingException;

import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import com.nmims.beans.PersonCareerservicesBean;
import com.nmims.helpers.PersonAttributesMapper;

public class LDAPDao {
	
	private LdapTemplate ldapTemplate;

	private static final Logger logger = LoggerFactory.getLogger(LDAPDao.class);
 
	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	
	
	public PersonCareerservicesBean findPerson(String userId) {
		String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		return (PersonCareerservicesBean) ldapTemplate.lookup(dn, 
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
//		    logger.info("Password changed successfuly");
		} catch ( Throwable throwable ) {
			logger.info("in changePassword got exception : "+ExceptionUtils.getFullStackTrace(throwable));
			throw throwable;
		}
	}
	
public void changeRoles(String roles, String userId) throws Exception{
		
		try {
		    PersonCareerservicesBean p = findPerson(userId);
		    roles = p.getRoles() +","+roles;
		    ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("businessRoles",  roles ) );
		    //String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
		    String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		    DistinguishedName userDN = new DistinguishedName( dn);
		    
		    ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1 } );
//		    logger.info("Roles changed successfuly");
		} catch ( Throwable throwable ) {
			logger.info("in changeRoles got exception : "+ExceptionUtils.getFullStackTrace(throwable));
			throw throwable;
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
//			    logger.info("Password and profile changed successfuly");
			} catch ( Throwable throwable ) {
				logger.info("in updateFirstTimeProfile got exception : "+ExceptionUtils.getFullStackTrace(throwable));
				throw throwable;
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
//		    logger.info("Profile updated successfuly");
		} catch ( Throwable throwable ) {
			logger.info("in updateProfile got exception : "+ExceptionUtils.getFullStackTrace(throwable));
			throw throwable;
		}
	}
	
	public void updateProgram( String userId, String program) throws Exception{
		
		try {

		    ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("department",  program ) );

		    
		    //String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
		    String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		    DistinguishedName userDN = new DistinguishedName( dn);
		    
		    ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1} );

		} catch ( Throwable throwable ) {
			logger.info("in updateProgram got exception : "+ExceptionUtils.getFullStackTrace(throwable));
			throw throwable;
		}
	}

}
