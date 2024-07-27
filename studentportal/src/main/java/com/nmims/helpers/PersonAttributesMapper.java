package com.nmims.helpers;

import java.util.Date;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

public class PersonAttributesMapper implements AttributesMapper {

	@Override
	public Object mapFromAttributes(Attributes attrs) throws NamingException {
		PersonStudentPortalBean person = new PersonStudentPortalBean();

		String firstName = "";
		String lastName = "";
		String displayName = "";
		String program = "";
		String email = "";
		String lastLogon = "";
		String contactNo = "";
		String altContactNo = "";
		String postalAddress = "";
		String identityConfirmed = "";
		String roles = "";
		String password = "";
		
		if(attrs.get("givenName") != null){
			firstName = (String)attrs.get("givenName").get();
		}

		if(attrs.get("sn") != null){
			lastName = (String)attrs.get("sn").get();
		}

		if(attrs.get("displayName") != null){
			displayName = (String)attrs.get("displayName").get();
		}

		if(attrs.get("department") != null){
			program = (String)attrs.get("department").get();
		}

		if(attrs.get("mail") != null){
			email = (String)attrs.get("mail").get();
		}
		
		if(attrs.get("mobile") != null){
			contactNo = (String)attrs.get("mobile").get();
		}
		
		if(attrs.get("postalAddress") != null){
			postalAddress = (String)attrs.get("postalAddress").get();
		}
		if(attrs.get("info") != null){
			identityConfirmed = (String)attrs.get("info").get();
		}
		if(attrs.get("otherHomePhone") != null){
			altContactNo = (String)attrs.get("otherHomePhone").get();
		}
		if(attrs.get("businessRoles") != null){
			roles = (String)attrs.get("businessRoles").get();
		}
		if(attrs.get("accountExpires") != null){
			String accntExpires = (String)attrs.get("accountExpires").get();
			System.out.println("Account Exires = "+accntExpires);
		}
		if(attrs.get("userPassword") != null){
			password = new String((byte[])attrs.get("userPassword").get());
		}

		try{
			
			if(attrs.get("lastLogon") != null){
				System.out.println("lastLogon = "+(String)attrs.get("lastLogon").get());
				String valueStr =  (String)attrs.get("lastLogon").get();
				
				long value = Long.parseLong(valueStr);
				long llastLogonAdjust=11644473600000L;  // adjust factor for converting it to java    

				Date lastLogonDate = new Date(value/10000-llastLogonAdjust); //
				lastLogon = lastLogonDate.toString();
				System.out.println("Date from lastLogon = "+lastLogon);
			}
			
			if(attrs.get("lastLogonTimestamp") != null){
				System.out.println("lastLogonTimestamp = "+(String)attrs.get("lastLogonTimestamp").get());
				String valueStr =  (String)attrs.get("lastLogonTimestamp").get();
				
				long value = Long.parseLong(valueStr);
				long llastLogonAdjust=11644473600000L;  // adjust factor for converting it to java    

				Date lastLogonDate = new Date(value/10000-llastLogonAdjust); //
				lastLogon = lastLogonDate.toString();
				System.out.println("Date from lastLogonTimestamp = "+lastLogon);
			}
		}catch(Exception e){
			//e.printStackTrace();
		}
		
		

		person.setFirstName(firstName);
		person.setLastName(lastName);
		person.setDisplayName(displayName);
		person.setProgram(program);
		person.setEmail(email);
		person.setLastLogon(lastLogon);
		person.setContactNo(contactNo);
		person.setAltContactNo(altContactNo);
		person.setPostalAddress(postalAddress);
		person.setIdentityConfirmed(identityConfirmed);
		person.setRoles(roles);
		person.setPassword(password);
		
		return person;
	}

}
