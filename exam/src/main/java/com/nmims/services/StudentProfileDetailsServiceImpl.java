package com.nmims.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.nmims.assembler.ObjectConverter;
import com.nmims.beans.Person;
import com.nmims.beans.StudentExamBean;
import com.nmims.dto.StudentProfileDetailsExamDto;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.interfaces.StudentDetailsServiceInterface;
import com.nmims.repository.StudentDetailsRepository;
import com.nmims.util.StudentProfileUtils;

/**
 * Implementation of the StudentDetailsServiceInterface containing methods for performing CRUD operations on Student Profile Details
 * @author Raynal Dcunha
 */
@Service
@Qualifier("studentProfileDetailsService")
public class StudentProfileDetailsServiceImpl implements StudentDetailsServiceInterface {
	private final SalesforceHelper salesforceHelper;
	private final StudentDetailsRepository studentDetailsRepository;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	private static final Logger logger = LoggerFactory.getLogger(StudentProfileDetailsServiceImpl.class);
	
	@Autowired
	public StudentProfileDetailsServiceImpl(SalesforceHelper salesforceHelper, StudentDetailsRepository studentDetailsRepository) {
		Objects.requireNonNull(salesforceHelper);				//Fail-fast approach, field is guaranteed to be non-null.
		Objects.requireNonNull(studentDetailsRepository);
		this.salesforceHelper = salesforceHelper;
		this.studentDetailsRepository = studentDetailsRepository;
	}
	
	@Override
	@Transactional
	public void updateStudentProfileDetails(StudentProfileDetailsExamDto studentProfileDetailsDto, String userId) {
		logger.info("updateStudentProfileDetails() Student details Bean: {}", studentProfileDetailsDto.toString());
		StudentExamBean studentBean = ObjectConverter.convertObjWithEmptyStringAsNull(studentProfileDetailsDto, new TypeReference<StudentExamBean>() {});
		String studentSapid = studentBean.getSapid();
		
		validateStudentAttributes(studentBean);				//validating StudentBean
		logger.info("Validation of fields completed for Student: {}", studentSapid);
		
		//Populating address field for the student
		String studentAddress = createStudentAddress(studentBean.getHouseNoName(), studentBean.getLocality(), 
													studentBean.getStreet(), studentBean.getPin(), studentBean.getCity());
		logger.info("Address of student {}: {}", studentSapid, studentAddress);
		studentBean.setAddress(studentAddress);
		
		//Storing user's LDAP details
		Person ldapFindPerson = studentDetailsRepository.getStudentLdapObjectAttributes(studentSapid);
		logger.info("Successfully stored LDAP details of Student: {}", studentSapid);
		
		//Update Student Details
		int noOfRowsUpdated = studentDetailsRepository.updateStudentProfile(studentBean, userId);
		logger.info("{} records updated in students table for Student: {}", noOfRowsUpdated ,studentSapid);
		
		if("PROD".equalsIgnoreCase(ENVIRONMENT)) {
			//Update Details in LDAP
			LocalDateTime lastChangedDateTime = updateStudentLdapObject(studentSapid, studentBean.getFirstName(), studentBean.getLastName(), studentBean.getEmailId(), 
																		studentBean.getMobile(), studentBean.getAltPhone(), studentBean.getProgram());
			logger.info("LDAP Object attributes updated successfully of Student: {}, attributes last changed on {}", studentSapid, lastChangedDateTime);
			
			//using try catch to make LDAP & Salesforce database calls @Transactional
			try {
				//bypass student before Jul2014 as Student record not present in Salesforce
				if(enrollmentAfterSalesforceDate(studentBean.getEnrollmentMonth(), studentBean.getEnrollmentYear())) {
					logger.info("Updating profile details of Student {} in Salesfoce as the enrollment MonthYear {}{} is after the Salesforce date of Jul2014", 
								studentSapid, studentBean.getEnrollmentMonth(), studentBean.getEnrollmentYear());
					
					Map<String, String> salesforceUpdateResponse = salesforceHelper.updateSalesforceAccountFields(studentBean);
					logger.info("Salesforce update() of Student {} completed with responseCode: {} and responseMessage: {}", studentSapid,
								salesforceUpdateResponse.get("responseCode"), salesforceUpdateResponse.get("responseMessage"));
					
					if(!salesforceUpdateResponse.get("responseCode").equals("200")) 
						throw new DataAccessResourceFailureException(salesforceUpdateResponse.get("responseMessage"));
				}
			}
			catch(Exception ex) {
//				ex.printStackTrace();
				//Reverting above stored student attributes in LDAP
				LocalDateTime revertedDateTime = updateStudentLdapObject(studentSapid, ldapFindPerson.getFirstName(), ldapFindPerson.getLastName(), ldapFindPerson.getEmail(), 
																		ldapFindPerson.getContactNo(), ldapFindPerson.getAltContactNo(), ldapFindPerson.getProgram());
				logger.error("Attribute Changes reverted in LDAP for Student: {}, attributes last changed on {}", studentSapid, revertedDateTime);
				throw new RuntimeException("Unable to update Student details in Salesforce!");
			}
		}
	}

	@Override
	public StudentProfileDetailsExamDto viewStudentProfileDetails(Long sapid, Integer sem) {
		StudentExamBean studentBean = studentDetailsRepository.getStudentProfile(sapid, sem);
		StudentProfileDetailsExamDto studentProfileBean = ObjectConverter.convertObjToXXX(studentBean, new TypeReference<StudentProfileDetailsExamDto>() {});
		logger.info("Profile details of Student {}: {}", sapid, studentProfileBean);
		return studentProfileBean;
	}
	
	/**
	 * Validating fields of the Student Bean
	 * @param student - bean containing the student details
	 */
	private void validateStudentAttributes(StudentExamBean student) {
		//Checking if the Current Age field matches the Date of Birth.
		//Commented as currentAge field is calculated via a formula in Salesforce, hence field is not synced from portal
//		int expectedAge = calculateAge(student.getDob());
//		logger.info("Expected Age calculated as {} from Date of Birth: {}", expectedAge, student.getDob());
//		if(Integer.parseInt(student.getCurrentAge()) != expectedAge) 
//			throw new IllegalArgumentException("Current Age does not match with the Date of Birth. Please check and submit the details again!");
		
		if(!StudentProfileUtils.isValidLastName(student.getLastName()))				//lastName is stored as empty if it is null or contains of only dot (period) character, card: 14965
			student.setLastName("");
		
		//Checks if student' OldProgram field is not null and not equal to the Program field value, and if the ProgramChanged Flag is set to 'Y' 
		if(Objects.nonNull(student.getOldProgram()) && !student.getProgram().equals(student.getOldProgram())
			&& (!Objects.nonNull(student.getProgramChanged()) || !student.getProgramChanged().equals("Y")))
			throw new IllegalArgumentException("Student has changed their program. The ProgramChanged Flag needs to be set as true.");
	}
	
	/**
	 * Creating the StudentAddress field by concatenating the houseNoName, locality, street, pin and city fields
	 * @return concatenated Address String
	 */
	private static String createStudentAddress(String houseNoName, String locality, String street, String pin, String city) {
		List<String> addressParamList = new ArrayList<>(Arrays.asList(houseNoName, locality, street, pin, city));
		String addressParamsConcat = addressParamList.stream()
													 .filter(param -> (param != null && !param.trim().isEmpty()))
													 .collect(Collectors.joining(","));
		
		String address = addressParamsConcat.replaceFirst(houseNoName + ",", houseNoName + ", ");
		return address;
	}
	
	/**
	 * Calculating Age from the passed Date
	 * @param date - Date of Birth
	 * @return age calculated from the date of birth
	 */
	private static int calculateAge(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate birthDate = LocalDate.parse(date, formatter);
		
		ZoneId zone = ZoneId.of("Asia/Kolkata");
		LocalDate currentDate = LocalDate.now(zone);
		
		return Period.between(birthDate, currentDate).getYears();
    }
	
	/**
	 * Checking if the enrollment is after the Salesforce Date (June 2014)
	 * @param enrollMonth - enrollment Month
	 * @param enrollYear - enrollment Year
	 * @return boolean value of enrollment YearMonth after than Salesforce YearMonth
	 */
	private static Boolean enrollmentAfterSalesforceDate(String enrollMonth, String enrollYear) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMM");
		YearMonth enrollmentYearMonth = YearMonth.parse(enrollYear + enrollMonth, formatter);
		YearMonth salesforceYearMonth = YearMonth.of(2014, 6);
		
		return enrollmentYearMonth.isAfter(salesforceYearMonth);
	}
	
	/**
	 * Update attributes of the student' LDAP Object
	 * @param sapid - studentNo of the student
	 * @param firstName - first name of the student
	 * @param lastName - last name of the student
	 * @param emailId - email address of the student
	 * @param mobileNo - mobile number of the student
	 * @param altPhone - alternate phone number of the student
	 * @param program - program of the student
	 * @return update date time 
	 */
	private LocalDateTime updateStudentLdapObject(String sapid, String firstName, String lastName, String emailId, String mobileNo, String altPhone, String program) {
		String displayName = StudentProfileUtils.getValidLdapDisplayName(firstName, lastName);					//LDAP acceptable display name
		lastName = StudentProfileUtils.getValidLdapLastName(lastName);											//LDAP acceptable last name
		
		return studentDetailsRepository.updateStudentLdapObjectAttributes(sapid, firstName, lastName, displayName, emailId, mobileNo, altPhone, program);
	}
}
