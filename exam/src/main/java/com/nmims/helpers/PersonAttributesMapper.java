package com.nmims.helpers;

import java.util.Date;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import com.nmims.beans.Person;
import org.springframework.ldap.core.AttributesMapper;

public class PersonAttributesMapper implements AttributesMapper {

	@Override
	public Object mapFromAttributes(Attributes attrs) throws NamingException {
		Person person = new Person();

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

		try{
			
			if(attrs.get("lastLogon") != null){
				String valueStr =  (String)attrs.get("lastLogon").get();
				
				long value = Long.parseLong(valueStr);
				long llastLogonAdjust=11644473600000L;  // adjust factor for converting it to java    

				Date lastLogonDate = new Date(value/10000-llastLogonAdjust); //
				lastLogon = lastLogonDate.toString();
			}
			
			if(attrs.get("lastLogonTimestamp") != null){
				String valueStr =  (String)attrs.get("lastLogonTimestamp").get();
				
				long value = Long.parseLong(valueStr);
				long llastLogonAdjust=11644473600000L;  // adjust factor for converting it to java    

				Date lastLogonDate = new Date(value/10000-llastLogonAdjust); //
				lastLogon = lastLogonDate.toString();
			}
		}catch(Exception e){
			
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
		return person;
	}

}
