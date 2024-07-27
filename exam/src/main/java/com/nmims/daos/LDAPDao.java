package com.nmims.daos;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import com.nmims.beans.Person;
import com.nmims.helpers.PersonAttributesMapper;

public class LDAPDao {
	
	private static final Logger logger = LoggerFactory.getLogger(LDAPDao.class);
	
	private LdapTemplate ldapTemplate;
	 

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}
	
	
	public Person findPerson(String userId) {
		String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		return (Person) ldapTemplate.lookup(dn, 
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
		    Person p = findPerson(userId);
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
	
	/**
	 * The userId of the user is verified and the corresponding details of the user is retrieved from the LDAP database
	 * @param userId - id of the user
	 * @return Person containing the user details
	 * @throws InvalidNameException 
	 */
	public Person getUserDetailsLdap(String userId) throws InvalidNameException  {
		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		LdapName userDN = new LdapName(dn);
		AndFilter filter = new AndFilter().and(new EqualsFilter("objectClass", "person")).and(new EqualsFilter("cn", userId));
	
		@SuppressWarnings("unchecked")		//Suppressing warning for "unchecked conversion" warning as the typecast to Person Object is safe.
		List<Person> userDetails = ldapTemplate.search(userDN, filter.toString(), new AttributesMapper() {
			@Override
			public Person mapFromAttributes(final Attributes attrs) throws NamingException {
				final Person personBean  = new Person();
				
				if(Objects.nonNull(attrs.get("department")))	//Checking if the getAttribute returns null to avoid NoSuchElementException
					personBean.setProgram((String) attrs.get("department").get());
				
				if(Objects.nonNull(attrs.get("displayName")))
					personBean.setDisplayName((String) attrs.get("displayName").get());
				
				if(Objects.nonNull(attrs.get("givenName")))
					personBean.setFirstName((String) attrs.get("givenName").get());
				
				if(Objects.nonNull(attrs.get("mail")))	
					personBean.setEmail((String) attrs.get("mail").get());
				
				if(Objects.nonNull(attrs.get("mobile")))
					personBean.setContactNo((String) attrs.get("mobile").get());
				
				if(Objects.nonNull(attrs.get("otherHomePhone")))
					personBean.setAltContactNo((String) attrs.get("otherHomePhone").get());
				
				if(Objects.nonNull(attrs.get("sn")))
					personBean.setLastName((String) attrs.get("sn").get());
				
				return personBean;
			}
		});
		
		return userDetails.get(0);
	}

	/**
	 * LDAP Attributes of the user is modified.
	 * @param userId - id of the user
	 * @param firstName - first name of the user
	 * @param lastName - last name of the user
	 * @param displayName - display name of the user
	 * @param email - email address of the user
	 * @param mobile - mobile number of the user
	 * @param altMobile - alternate phone number of the user
	 * @param program - user program
	 * @throws InvalidNameException
	 */
	public void updateUserDetailsLdap(String userId, String firstName, String lastName, String displayName, String email, String mobile, String altMobile, String program) 
			throws InvalidNameException {
		if(!Objects.nonNull(altMobile) || altMobile.trim().isEmpty())		//if altMobile field is null or empty, store altMobile attribute as 'Not Available'
			altMobile = "Not Available";
		
		ModificationItem repitem1 = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("department", program));
		ModificationItem repitem2 = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("displayName", displayName));
		ModificationItem repitem3 = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("givenName", firstName));
		ModificationItem repitem4 = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail", email));
		ModificationItem repitem5 = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mobile", mobile));
		ModificationItem repitem6 = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("otherHomePhone", altMobile));
		ModificationItem repitem7 = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", lastName));

		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		LdapName userDN = new LdapName(dn);

		ldapTemplate.modifyAttributes(userDN, new ModificationItem[] { repitem1, repitem2, repitem3, repitem4, repitem5, repitem6, repitem7 });
	}
	
	/**
	 * Get the whenChange attribute from the user' LDAP Object
	 * @param userId - id of the user
	 * @return whenChanged DateTime attribute as a String
	 * @throws InvalidNameException
	 */
	public String getWhenChangedLdapAttribute(String userId) throws InvalidNameException {
		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		LdapName userDN = new LdapName(dn);
		AndFilter filter = new AndFilter().and(new EqualsFilter("objectClass", "person")).and(new EqualsFilter("cn", userId));
	
		return (String) ldapTemplate.searchForObject(userDN, filter.toString(), new AbstractContextMapper() {
			@Override
			protected String doMapFromContext(DirContextOperations ctx) {
				return ctx.getStringAttribute("whenChanged");
			}
		});
	}
}
