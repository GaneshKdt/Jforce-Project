package com.nmims.daos;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.InvalidAttributeValueException;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.OperationNotSupportedException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import com.nmims.helpers.PersonAttributesMapper;
import com.nmims.helpers.PersonStudentPortalBean;

public class LDAPDao {

	private static final Logger logger = LoggerFactory.getLogger(LDAPDao.class);

	private LdapTemplate ldapTemplate;
	private static final String ACCOUNT_NEVER_EXPIRE_VALUE = "9223372036854775807";
	private static final String ADS_UF_DONT_EXPIRE_PASSWD_NORMAL_ACCNT = "66048";

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}


	public PersonStudentPortalBean findPerson(String userId) {
		String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		return (PersonStudentPortalBean) ldapTemplate.lookup(dn, 
				new PersonAttributesMapper());
		/*Person p = new Person();
		p.setFirstName("Sachin");
		p.setLastName("Parab");
		return p;*/
	}
	
	public String getUserPassword(String userId) {
		String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		try {
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filterFormat = "(objectClass=user)";
			String filter = String.format(filterFormat, dn); 
			//String searchFilter = "(&(objectClass=user)(sAMAccountName=myOtherLdapUsername))";
			List answer =  ldapTemplate.search(dn, filter,
						new AttributesMapper() {
							
							@Override
							public Object mapFromAttributes(Attributes attr) throws NamingException {
								// TODO Auto-generated method stub
								String pwd = new String((byte[]) attr.get("userpassword").get());
								return pwd;
							}
						});
			return (String) answer.get(0);
		}
		catch (Exception e) {
			// TODO: handle exception
			//e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * The userId of the user is verified and the corresponding details of the user is retrieved from the LDAP database
	 * @param userId - id of the user
	 * @return PersonStudentPortalBean containing the user details
	 */
	public PersonStudentPortalBean getUserDetailsLdap(String userId)  {
		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		try {
			LdapName userDN = new LdapName(dn);
			AndFilter filter = new AndFilter().and(new EqualsFilter("objectClass", "person")).and(new EqualsFilter("cn", userId));
		
			List<PersonStudentPortalBean> userDetails = ldapTemplate.search(userDN, filter.toString(), new AttributesMapper<PersonStudentPortalBean>() {
				@Override
				public PersonStudentPortalBean mapFromAttributes(Attributes attrs) throws NamingException {
					PersonStudentPortalBean personBean  = new PersonStudentPortalBean();
					
					if(attrs.get("department") != null)
						personBean.setProgram((String) attrs.get("department").get());
					
					if(attrs.get("displayName") != null)
						personBean.setDisplayName((String) attrs.get("displayName").get());
					
					if(attrs.get("givenName") != null)
						personBean.setFirstName((String) attrs.get("givenName").get());
					
					if(attrs.get("mail") != null)	
						personBean.setEmail((String) attrs.get("mail").get());
					
					if(attrs.get("mobile") != null)
						personBean.setContactNo((String) attrs.get("mobile").get());
					
					if(attrs.get("otherHomePhone") != null)
						personBean.setAltContactNo((String) attrs.get("otherHomePhone").get());
					
					if(attrs.get("sn") != null)
						personBean.setLastName((String) attrs.get("sn").get());
					
					return personBean;
				}
			});
			
			return userDetails.get(0);
		}
		catch(CommunicationException ex) {
			logger.error("LDAP server Exception in search(), could not establish connection, Exception thrown: " + ex.toString());
			throw new RuntimeException("Could not establish connection with LDAP server!");
		}
		catch(InvalidNameException ex) {
			logger.error("LDAP server Exception in search(), Invalid / Illegal characters in sapid, Exception thrown: " + ex.toString());
			throw new RuntimeException("Invalid / Illegal characters detected in sapid!");
		}
		catch(NameNotFoundException ex) {
			logger.error("LDAP server Exception in search(), Sapid not found, Exception thrown: " + ex.toString());
			throw new RuntimeException("SAPID not found in LDAP server!");
		}
		catch(Exception ex) {
			logger.error("LDAP server Exception in search(), Exception thrown: " + ex.toString());
			throw new RuntimeException("LDAP server Exception!");
		}
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
			//exc.printStackTrace();
			throw exc;
		}
	}

	public void changeRoles(String roles, String userId) throws Exception{

		try {
			PersonStudentPortalBean p = findPerson(userId);
			// roles = roles;
			ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("businessRoles",  roles ) );
			//String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
			String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
			DistinguishedName userDN = new DistinguishedName( dn);

			ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1 } );
			logger.info("Roles changed successfuly");
		} catch ( Exception exc ) {
			logger.error( "changeRoles()", exc);
			//exc.printStackTrace();
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
			//exc.printStackTrace();
			throw exc;
		}
	}

	public void updateProfile( String userId, String email,String mobile,String altMobile) throws Exception{

		try {

			if(altMobile == null || "".equals(altMobile.trim())){
				altMobile = "Not Available";
			}

			ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail",  email ) );
			ModificationItem repitem2 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mobile",  mobile ) );
			ModificationItem repitem3 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("otherHomePhone",  altMobile ) );

			//String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
			String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
			DistinguishedName userDN = new DistinguishedName( dn);

			ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1,repitem2,repitem3} );
			logger.info("Profile updated successfuly");
		} catch ( Exception exc ) {
			logger.error( "changePassword()", exc);
			//exc.printStackTrace();
			throw exc;
		}
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
	 */
	public void updateLdapProfileSFDC(String userId, String firstName, String lastName, String displayName, String email, String mobile, String altMobile, String program)  {
		try {
			if(altMobile == null || altMobile.trim().isEmpty()) {
				altMobile = "Not Available";
			}
			
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
		catch(CommunicationException ex) {
			logger.error("LDAP server Exception in modifyAttributes(), could not establish connection, Exception thrown: " + ex.toString());
			throw new RuntimeException("Could not establish connection with LDAP server!");
		}
		catch(InvalidNameException ex) {
			logger.error("LDAP server Exception in modifyAttributes(), Invalid / Illegal characters in sapid, Exception thrown: " + ex.toString());
			throw new RuntimeException("Invalid / Illegal characters detected in sapid!");
		}
		catch(NameNotFoundException ex) {
			logger.error("LDAP server Exception in modifyAttributes(), Sapid not found, Exception thrown: " + ex.toString());
			throw new RuntimeException("SAPID not found in LDAP server!");
		}
		catch(Exception ex) {
			logger.error("LDAP server Exception in modifyAttributes(), Exception thrown: " + ex.toString());
			throw new RuntimeException("LDAP server Exception!");
		}
	}

	public void updateProfileTemp( String userId, String email,String mobile,String altMobile, String postalAddress,String program) {

		try {

			if(altMobile == null || "".equals(altMobile.trim())){
				altMobile = "Not Available";
			}

			ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail",  email ) );
			ModificationItem repitem2 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mobile",  mobile ) );
			ModificationItem repitem3 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("postalAddress",  postalAddress ) );
			ModificationItem repitem4 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("otherHomePhone",  altMobile ) );
			ModificationItem repitem5 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("department",  program ) );

			//String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
			String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
			DistinguishedName userDN = new DistinguishedName( dn);

			ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1, repitem2,  repitem3, repitem4, repitem5} );
			//System.out.println("Profile updated successfuly");
		} catch ( Exception exc ) {
			logger.error( "changePassword()", exc);
			//exc.printStackTrace();
			//throw exc;
		}
	}

	public void updateAttributes(String userId){
		try {

			ModificationItem repitem1 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPrincipalName",  userId+"@svkmgrp.com" ) );
			ModificationItem repitem2 = new ModificationItem( DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountControl",  ADS_UF_DONT_EXPIRE_PASSWD_NORMAL_ACCNT ) );


			//String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS,OU=Institutes";
			String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
			DistinguishedName userDN = new DistinguishedName( dn);

			ldapTemplate.modifyAttributes( userDN, new ModificationItem[] { repitem1, repitem2} );
			logger.info("Attributes updated successfuly");
		} catch ( Exception exc ) {
			logger.error( "updateAttributes()", exc);
			//exc.printStackTrace();
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
			//System.out.println("Program updated successfuly for "+userId);
		} catch ( Exception exc ) {
			logger.error( "changePassword()", exc);
			//exc.printStackTrace();
			throw exc;
		}
	}

	public void createUser(PersonStudentPortalBean p)  {
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
			personAttributes.put( "businessRoles", " ");
			//personAttributes.put( "userAccountControl", "512" ); /// 512 = normal luser
			personAttributes.put( "userPassword", p.getPassword() ); 

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
			//System.out.println("User created successfully");

		} catch ( InvalidAttributeValueException exc ) {
			//exc.printStackTrace();
			logger.error( "createUser()", exc);
			p.setErrorMessage("Invalid value for attributes for student "+p.getUserId());
			p.setErrorRecord(true);
			//throw new LdapSaveException( exc.getMessage() );
		} catch ( NameAlreadyBoundException exc ) {   /// USER EXISTS....
			//exc.printStackTrace();
			//System.out.println("Name exists");
			updateProfileTemp(p.getUserId(),p.getEmail(), p.getContactNo(),p.getAltContactNo(), p.getPostalAddress(), p.getProgram());
			logger.error( "createUser()", exc);
			p.setErrorMessage("User already exists : "+p.getUserId());
			p.setErrorRecord(true);
			//throw new DuplicateUserException(  "User ["+ luzer.getEmailAddress() + "] allready exists in AD." );
		} catch ( NameNotFoundException exc ) {
			//exc.printStackTrace();
			logger.error( "createUser()", exc);
			p.setErrorMessage("Unknown error for student "+p.getUserId());
			p.setErrorRecord(true);
			//throw new LdapSaveException( exc.getMessage() );
		} catch ( OperationNotSupportedException exc ) {  // CAN NOT ADD USER
			//exc.printStackTrace();
			logger.error( "createUser()", exc);
			p.setErrorMessage("Password not matching policies for student "+p.getUserId());
			p.setErrorRecord(true);
			//throw new PasswordStrengthException( exc.getMessage() );
		} catch ( Exception exc ) {
			//exc.printStackTrace();
			logger.error( "createUser()", exc);
			p.setErrorMessage("Unknown error for student "+p.getUserId());
			p.setErrorRecord(true);
			//throw new LdapSaveException( exc.getMessage() );
		}
	}


	public boolean checkUserExists(String userId) {
		String dn = "CN="+userId+",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		DistinguishedName userDN = new DistinguishedName( dn);
		try {
			ldapTemplate.lookup(userDN);
		} catch (NameNotFoundException e) {
			//LDAP throws exception if user not found.
			//System.out.println("Name not found");
			return false;
		}
		
		return true;
	}
	
	/**
	 * The userId of the user is verified and the corresponding details of the user is retrieved from the LDAP database
	 * @param userId - id of the user
	 * @return Hashmap containing the user details (name, email, password)
	 * @throws InvalidNameException
	 */
	public HashMap<String, String> getUserEmailPassword(String userId) throws Exception {
		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		try {
			LdapName userDN = new LdapName(dn);
			
			AndFilter filter = new AndFilter().and(new EqualsFilter("objectClass", "person")).and(new EqualsFilter("cn", userId));
			
			List<HashMap<String, String>> userDetails = ldapTemplate.search(userDN, filter.toString(), new AttributesMapper<HashMap<String, String>>() {
				@Override
				public HashMap<String, String> mapFromAttributes(Attributes attrs) throws NamingException {
					HashMap<String, String> map = new HashMap<>();
					map.put("name", (String) attrs.get("displayName").get());
					map.put("email", (String) attrs.get("mail").get());
					map.put("password", new String((byte[]) attrs.get("userPassword").get()));
					
					return map;
				}
			});
			
			return userDetails.get(0);
		}
		catch(CommunicationException ex ) {
			//Communication Exception is thrown when a connection to the LDAP Server cannot be established
			logger.error("LDAP server Exception, could not establish connection, Exception thrown: " + ex.toString());
			throw new RuntimeException("Could not establish connection with server. Please try again!");
		}
		catch(InvalidNameException ex) {
			//InvalidName Exception is thrown when an illegal character is entered as the userId
			logger.error("Invalid Characters detected in userId: " + userId + ", Exception thrown: " + ex.toString());
			throw new RuntimeException("Invalid characters detected, please recheck your User ID (Student Number).");
		}
		catch(NameNotFoundException ex) {
			//NameNotFound Exception is thrown when the userId is not found in LDAP
			logger.error("Unable to find object linked to " + userId + " in LDAP, Exception: " + ex.toString());
			throw new RuntimeException("User ID does not exist, please recheck your User ID (Student Number) " + 
										"or contact ngasce@nmims.edu for any further assistance.");
		}
		catch(NullPointerException ex) {
			//NullPointerException is thrown when the password/mail attributes of the user is missing in LDAP
			logger.error("Missing userPassword or mail attributes in LDAP for userId: " + userId + ", exception thrown: " + ex.toString());
			throw new RuntimeException("Email Address not registered. " + 
										"Please contact ngasce@nmims.edu to register/update your email address.");
		}
	}
	
	/**
	 * Get the mail attribute from the user' LDAP Object
	 * @param userId - id of the user
	 * @return mail attribute as a String
	 * @throws InvalidNameException
	 */
	public String getMailLdapAttribute(String userId) throws InvalidNameException {
		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		LdapName userDN = new LdapName(dn);
		AndFilter filter = new AndFilter().and(new EqualsFilter("objectClass", "person")).and(new EqualsFilter("cn", userId));
	
		return ldapTemplate.searchForObject(userDN, filter.toString(), new AbstractContextMapper<String>() {
			@Override
			protected String doMapFromContext(DirContextOperations ctx) {
				return ctx.getStringAttribute("mail");
			}
		});
	}
	
	/**
	 * Get the mobile attribute from the user' LDAP Object
	 * @param userId - id of the user
	 * @return mobile attribute as a String
	 * @throws InvalidNameException
	 */
	public String getMobileLdapAttribute(String userId) throws InvalidNameException {
		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		LdapName userDN = new LdapName(dn);
		AndFilter filter = new AndFilter().and(new EqualsFilter("objectClass", "person")).and(new EqualsFilter("cn", userId));
	
		return ldapTemplate.searchForObject(userDN, filter.toString(), new AbstractContextMapper<String>() {
			@Override
			protected String doMapFromContext(DirContextOperations ctx) {
				return ctx.getStringAttribute("mobile");
			}
		});
	}
	
	/**
	 * mail LDAP Attribute of the user is modified
	 * @param userId - id of the user
	 * @param value - value to be updated
	 * @throws InvalidNameException
	 */
	public void updateMailLdapAttribute(String userId, String value) throws InvalidNameException  {
		ModificationItem repitem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mail", value));
		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		LdapName userDN = new LdapName(dn);
		ldapTemplate.modifyAttributes(userDN, new ModificationItem[] { repitem });
	}
	
	/**
	 * mobile LDAP Attribute of the user is modified
	 * @param userId - id of the user
	 * @param value - value to be updated
	 * @throws InvalidNameException
	 */
	public void updateMobileLdapAttribute(String userId, String value) throws InvalidNameException  {
		ModificationItem repitem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("mobile", value));
		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		LdapName userDN = new LdapName(dn);
		ldapTemplate.modifyAttributes(userDN, new ModificationItem[] { repitem });
	}
	
	/**
	 * The Other Home Phone (AltPhone) attribute in the user' LDAP Object is modified
	 * @param userId - id of the user
	 * @value - value to be updated
	 * @throws InvalidNameException
	 */
	public void updateUserAltPhoneLdap(String userId, String value) throws InvalidNameException  {
		if(Objects.isNull(value) || value.trim().isEmpty())
			value = "Not Available";
		
		ModificationItem repitem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("otherHomePhone", value));
		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		LdapName userDN = new LdapName(dn);
		ldapTemplate.modifyAttributes(userDN, new ModificationItem[] { repitem });
	}
	
	public void updateRolesLdapAttribute(String userId, String value) throws InvalidNameException  {
		ModificationItem repitem = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("businessRoles", value));
		String dn = "CN=" + userId + ",OU=NGA-SCE Student,OU=NMIMS Students,OU=NMIMS,OU=Institute";
		LdapName userDN = new LdapName(dn);
		ldapTemplate.modifyAttributes(userDN, new ModificationItem[] { repitem });
	}
	
	
}
