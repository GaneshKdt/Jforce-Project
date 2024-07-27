package com.nmims.repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.naming.InvalidNameException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.stereotype.Repository;

import com.nmims.beans.Person;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.StudentDAO;

@Repository
public class StudentDetailsRepositoryImpl implements StudentDetailsRepository {
	private final StudentDAO studentDAO;
	private final LDAPDao ldapDao;
	
	private static final Logger logger = LoggerFactory.getLogger(StudentDetailsRepositoryImpl.class);

	@Autowired
	public StudentDetailsRepositoryImpl(StudentDAO studentDAO, LDAPDao ldapDao) {
		Objects.requireNonNull(studentDAO);			//Fail-fast approach, field is guaranteed to be non-null.
		Objects.requireNonNull(ldapDao);
		this.studentDAO = studentDAO;
		this.ldapDao = ldapDao;
	}
	
	@Override
	public int updateStudentProfile(StudentExamBean student, String userId) {
		return studentDAO.updateStudentDetails(student.getSapid(), student.getSem(), student.getFirstName(), student.getMiddleName(), student.getLastName(), 
												student.getFatherName(), student.getMotherName(), student.getHusbandName(), student.getGender(), student.getDob(), 
												student.getEmailId(), student.getMobile(), student.getAltPhone(), 
												student.getAddress(), student.getHouseNoName(), student.getStreet(), student.getLandMark(), student.getLocality(), 
												student.getPin(), student.getCity(), student.getState(), student.getCountry(),
												student.getProgramCleared(), student.getProgramStatus(), student.getProgramRemarks(), 
												student.getIndustry(), student.getDesignation(), userId);
	}

	@Override
	public StudentExamBean getStudentProfile(Long sapid, int sem) {
		return studentDAO.getStudentDetails(sapid, sem);
	}

	@Override
	public Person getStudentLdapObjectAttributes(String sapid) {
		try {
			return ldapDao.getUserDetailsLdap(sapid);
		}
		catch(CommunicationException ex) {
//			ex.printStackTrace();
			logger.error("Unable to establish connection with the LDAP Server. Exception thrown: {}", ex.toString());
		}
		catch(InvalidNameException ex) {
//			ex.printStackTrace();
			logger.error("Invalid / Illegal characters found in sapid while fetching attributes in LDAP Server for Student: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(NameNotFoundException ex) {
//			ex.printStackTrace();
			logger.error("Unable to find LDAP Object of Student with Sapid: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while searching Student {} and fetching attributes in LDAP Server, Exception thrown: {}", sapid, ex.toString());
		}
		
		throw new RuntimeException("Unable to Search and retrieve Student Attributes from LDAP Server!");		//Runtime Exception is thrown if any one of the catch blocks are executed
	}

	@Override
	public LocalDateTime updateStudentLdapObjectAttributes(String sapid, String firstName, String lastName, String displayName, String email, String mobile, String altMobile, String program) {
		try {
			ldapDao.updateUserDetailsLdap(sapid, firstName, lastName, displayName, email, mobile, altMobile, program);			//Modify Student Attributes
			
			//get lastModifiedDateTime of the LDAP Object and parse as LocalDateTime
			String lastModified = ldapDao.getWhenChangedLdapAttribute(sapid);
			return convertLdapUTCTimeToIST(lastModified);
		}
		catch(CommunicationException ex) {
//			ex.printStackTrace();
			logger.error("Unable to establish connection with the LDAP Server. Exception thrown: {}", ex.toString());
		}
		catch(InvalidNameException ex) {
//			ex.printStackTrace();
			logger.error("Invalid / Illegal characters found in sapid while modifying attributes in LDAP Server for Student: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(NameNotFoundException ex) {
//			ex.printStackTrace();
			logger.error("Unable to find LDAP Object for modifying attributes of Student with Sapid: {}, Exception thrown: {}", sapid, ex.toString());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while modifying attributes of Student {} in LDAP Server, Exception thrown: {}", sapid, ex.toString());
		}
		
		throw new RuntimeException("Unable to Modify Student Attributes in LDAP Server!");		//Runtime Exception is thrown if any one of the catch blocks are executed
	}

	/**
	 * Convert UTC DateTime obtained from LDAP into IST LocalDateTime
	 * @param dateTime - UTC dateTime in String
	 * @return LocalDateTime of UTC dateTime converted to IST
	 */
	private LocalDateTime convertLdapUTCTimeToIST(String dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss[,S][.S]X").withZone(ZoneId.of("UTC"));
		ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime, formatter);
	
		return zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
	}
}
