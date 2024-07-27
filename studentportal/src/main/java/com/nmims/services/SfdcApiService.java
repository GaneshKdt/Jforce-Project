package com.nmims.services;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.StringValue;
import com.nmims.assembler.ObjectConverter;
import com.nmims.beans.CashFreeResponseBean;
import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentInfoCheckDAO;
import com.nmims.dto.StudentProfileDto;
import com.nmims.helpers.EmailHelper;
import com.nmims.helpers.PersonStudentPortalBean;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.publisher.IdCardEventPublisher;
import com.nmims.util.StudentProfileUtils;

/**
 * Service Layer for SfdcApiRestController.
 * @author Raynal Dcunha
 */
@Service
public class SfdcApiService {
	private PortalDao portalDAO;
	private LDAPDao ldapDAO;
	private StudentInfoCheckDAO studentInfoCheckDAO;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	private static final String[] shorterMonthValues = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	private static final String EMAIL_VALIDATION_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	
	private static final Logger profileSyncSfdcLogger = LoggerFactory.getLogger("profileSyncSFDC");
	
	@Value("${CASHFREE_TOKEN_CLIENTID}")
	private String CASHFREE_TOKEN_CLIENTID;
	
	@Value("${CASHFREE_TOKEN_CLIENT_SECRET}")
	private String CASHFREE_TOKEN_CLIENT_SECRET;
	
	@Value("${CASHFREE_PEM_FILEPATH}")
	private String CASHFREE_PEM_FILEPATH;
	
	@Value("${CASHFREE_BANK_AUTHORIZATION_URL}")
	private String CASHFREE_BANK_AUTHORIZATION_URL;
	
	@Value("${CASHFREE_AADHAR_AUTHORIZATION_URL}")
	private String CASHFREE_AADHAR_AUTHORIZATION_URL;
	
	@Autowired
	SalesforceHelper sfdc;
	
	@Autowired
	IdCardEventPublisher eventPublisher;
	
	@Autowired
	IdCardService idCardService;
	
	@Autowired
	EmailHelper emailHelper;
	
	@Autowired
	ApplicationContext act;
	
	public SfdcApiService(PortalDao portalDAO, LDAPDao ldapDAO, StudentInfoCheckDAO studentInfoCheckDAO) {
		//Objects.requireNonNull(obj) :  Fail-fast approach, field is guaranteed to be non-null.
		Objects.requireNonNull(portalDAO);
		Objects.requireNonNull(ldapDAO);
		Objects.requireNonNull(studentInfoCheckDAO);
		this.portalDAO = portalDAO;
		this.ldapDAO = ldapDAO;
		this.studentInfoCheckDAO = studentInfoCheckDAO;
	}
	
	/**
	 * Updates the Student Details in LDAP and MySQL database.
	 * @param studentProfileDto - DTO which contains the student details
	 * @return	A JSON formatted String which contains fields from the StudentProfileDto
	 * @throws Exception
	 */
	public String updateProfileSFDC(StudentProfileDto studentProfileDto) throws Exception {
		profileSyncSfdcLogger.info("[START] updateProfileFromSFDC API RequestBody: " + studentProfileDto.toString());
		StudentStudentPortalBean enteredStudentBean = ObjectConverter.convertObjToXXX(studentProfileDto, new TypeReference<StudentStudentPortalBean>() {});
		String studentSapid = enteredStudentBean.getSapid();
		
		validateStudentBean(enteredStudentBean);				//validating StudentBean
		profileSyncSfdcLogger.info("Validation of fields completed for user: " + studentSapid);
		
		
		//Storing consumerType and consumerProgramStructureId of Student
		String consumerType = getStudentConsumerType(enteredStudentBean.getCenterName(), enteredStudentBean.getProgram());
		profileSyncSfdcLogger.info("ConsumerType: " + consumerType + " generated for user: " + studentSapid + " using CenterName: " + enteredStudentBean.getCenterName() + " and Program: " + enteredStudentBean.getProgram());
		
		int consumerProgramStructureId = getStudentConsumerProgramStructureId(consumerType, enteredStudentBean.getProgram(), enteredStudentBean.getProgramStructure());
		profileSyncSfdcLogger.info("ConsumerProgramStructureId: " + consumerProgramStructureId + " generated for user: " + studentSapid + " using ConsumerType: " + consumerType + ", Program: " + enteredStudentBean.getProgram() + " and ProgramStructure: " +  enteredStudentBean.getProgramStructure());
		if(consumerProgramStructureId == 0)
			throw new IllegalArgumentException("Unable to get CPS Id of student, due to invalid Program, Program Structure or CenterName values");
		
		
		//Storing programChanged field by checking if oldProgram field of student is empty/null or equals program field of student
		String programChanged = (!StringUtils.isBlank(enteredStudentBean.getOldProgram()) && !(enteredStudentBean.getProgram().equals(enteredStudentBean.getOldProgram()))) ? "Y" : null;
		profileSyncSfdcLogger.info("ProgramChanged Flag: " + programChanged + " generated for user: " + studentSapid + " using OldProgram: " + enteredStudentBean.getOldProgram() + " and Program: " + enteredStudentBean.getProgram());
		
		
		//Populating address field for the student
		//Checking if the below fields are null before concatenating as a String for student address
		String studentHouseNoName = !StringUtils.isBlank(enteredStudentBean.getHouseNoName()) ? enteredStudentBean.getHouseNoName() + ", " : "";
		String studentLocality = !StringUtils.isBlank(enteredStudentBean.getLocality()) ? enteredStudentBean.getLocality() + "," : "";
		String studentStreet = !StringUtils.isBlank(enteredStudentBean.getStreet()) ? enteredStudentBean.getStreet() + "," : "";
		String studentPin = !StringUtils.isBlank(enteredStudentBean.getPin()) ? enteredStudentBean.getPin() + "," : "";
		String studentCity = !StringUtils.isBlank(enteredStudentBean.getCity()) ? enteredStudentBean.getCity() : "";
		String studentAddress = studentHouseNoName + studentLocality + studentStreet + studentPin + studentCity;
		enteredStudentBean.setAddress(studentAddress);
		profileSyncSfdcLogger.info("StudentAddress: " + studentAddress + " generated for user: " + studentSapid + " using HouseNoName: " + studentHouseNoName + ", Locality: " + studentLocality + ", Street: " + studentStreet + ", Pin: " + studentPin + " and City: " + studentCity);
		
		
		//Storing user's LDAP details
		PersonStudentPortalBean ldapFindPerson = ldapDAO.getUserDetailsLdap(studentSapid);
		profileSyncSfdcLogger.info("Successfully stored LDAP details of user: " + studentSapid);
		
		
		//Update Details in LDAP
		if("PROD".equalsIgnoreCase(ENVIRONMENT)) {
			updateUserLdapObject(studentSapid, enteredStudentBean.getFirstName(), enteredStudentBean.getLastName(), enteredStudentBean.getEmailId(), 
								enteredStudentBean.getMobile(), enteredStudentBean.getAltPhone(), enteredStudentBean.getProgram());
			profileSyncSfdcLogger.info("LDAP Object updated successfully of user: " + studentSapid);
		}
		
		//using try catch for the updateStudentContactFromSFDC method, to make it @Transactional
		try {
			//Update Student Details
			int noOfRowsUpdated = portalDAO.updateStudentDetailsFromSFDC(studentSapid, enteredStudentBean.getFirstName(), enteredStudentBean.getLastName(), enteredStudentBean.getFatherName(), enteredStudentBean.getMotherName(), enteredStudentBean.getHusbandName(), 
												enteredStudentBean.getDob(), enteredStudentBean.getImageUrl(), enteredStudentBean.getEmailId(), enteredStudentBean.getMobile(), enteredStudentBean.getAltPhone(), 
												studentAddress, enteredStudentBean.getHouseNoName(), enteredStudentBean.getStreet(), enteredStudentBean.getLandMark(), enteredStudentBean.getLocality(), enteredStudentBean.getPin(), enteredStudentBean.getCity(), enteredStudentBean.getState(), enteredStudentBean.getCountry(),
												enteredStudentBean.getCenterName(), enteredStudentBean.getCenterCode(), enteredStudentBean.getProgram(), enteredStudentBean.getPrgmStructApplicable(), 
												enteredStudentBean.getEnrollmentMonth(), enteredStudentBean.getEnrollmentYear(), enteredStudentBean.getValidityEndMonth(), enteredStudentBean.getValidityEndYear(), programChanged, enteredStudentBean.getOldProgram(), 
												enteredStudentBean.getHighestQualification(), consumerType, consumerProgramStructureId);
			profileSyncSfdcLogger.info(noOfRowsUpdated + " records updated for student: " + studentSapid + ", in database students table");
		}
		catch(Exception ex) {
			//Reverting stored user details in LDAP
			profileSyncSfdcLogger.info("Portal Updation failed, due to: " + ex.toString() + "; Reverting changes in LDAP for user: " + studentSapid);
			updateUserLdapObject(studentSapid, ldapFindPerson.getFirstName(), ldapFindPerson.getLastName(), ldapFindPerson.getEmail(), 
								ldapFindPerson.getContactNo(), ldapFindPerson.getAltContactNo(), ldapFindPerson.getProgram());
			profileSyncSfdcLogger.info("Changes reverted in LDAP for user: " + studentSapid);
			throw new RuntimeException("Portal database updation failed!");
		}
		
		//Returns the Student Details In JSON formatted String
		String studentDetailsInJsonString = getStudentDetailsAsJsonString(studentSapid, enteredStudentBean);
		profileSyncSfdcLogger.info("updateProfileFromSFDC API ResponseBody: " + studentDetailsInJsonString);
		if("PROD".equalsIgnoreCase(ENVIRONMENT)){
		//update IdCard after the student update
		enteredStudentBean.setConsumerProgramStructureId(String.valueOf(consumerProgramStructureId));
		idCardService.updateIdCard(enteredStudentBean);
		}
		return studentDetailsInJsonString;
	}
	
	/**
	 * Validation of StudentBean fields
	 * @param studentBean - bean containing Student details
	 * @throws Exception
	 */
	public void validateStudentBean(StudentStudentPortalBean studentBean) throws Exception {
		int studentValidityEndYear = Integer.parseInt(studentBean.getValidityEndYear());
		int enrollmentYear = Integer.parseInt(studentBean.getEnrollmentYear());
		
		ArrayList<String> programList = new ArrayList<>();
		ArrayList<String> programStructureList = new ArrayList<>();
		HashMap<String, String> centerCodeAndNameMap = new HashMap<>();
		try {
			programList = portalDAO.getAllPrograms(); 
			programStructureList = portalDAO.getAllActiveProgramStructure();
			centerCodeAndNameMap = portalDAO.getCenterCodeAndNameMap();
		}
		catch(Exception ex) {
			profileSyncSfdcLogger.error("Unable to get all active Centers, Program & Program Structure types for: " + studentBean.getSapid() + ", due to " + ex.toString());
			throw new RuntimeException("Unable to get all active Centers, Program & Program Structure types from database!");
		}
		
		if(studentBean.getSapid().length() != 11) 
			throw new IllegalArgumentException("Invalid Sapid");
		
		if(StringUtils.isBlank(studentBean.getFirstName()))
			throw new IllegalArgumentException("Invalid Input: FirstName cannot be empty or null");
		
		if(!StudentProfileUtils.isValidLastName(studentBean.getLastName()))				//lastName is stored as empty if it is null or contains only dot (period) character, card: 14965
			studentBean.setLastName("");
		
		if(StringUtils.isBlank(studentBean.getMotherName()))
			throw new IllegalArgumentException("Invalid Input: MotherName cannot be empty or null");
		
		if(StringUtils.isBlank(studentBean.getFatherName()))
			throw new IllegalArgumentException("Invalid Input: FatherName cannot be empty or null");
		
		Pattern pattern = Pattern.compile(EMAIL_VALIDATION_REGEX);		//using OWASP Email Validation Regex
		Matcher matcher = pattern.matcher(studentBean.getEmailId());
		if(!matcher.matches())
			throw new IllegalArgumentException("Invalid EmailId");
		
		if(! Arrays.stream(shorterMonthValues).anyMatch(studentBean.getEnrollmentMonth() :: equals))
			throw new IllegalArgumentException("Invalid enrollmentMonth");
		
		if(! (2000 <= enrollmentYear && enrollmentYear <= 2099))
			throw new IllegalArgumentException("Invalid enrollmentYear");
		
		if(! Arrays.stream(shorterMonthValues).anyMatch(studentBean.getValidityEndMonth() :: equals))
			throw new IllegalArgumentException("Invalid validityEndMonth");
			
		if(! (2000 <= studentValidityEndYear && studentValidityEndYear <= 2099))
			throw new IllegalArgumentException("Invalid validityEndYear");
		
		if(! programList.contains(studentBean.getProgram())) 
			throw new IllegalArgumentException("Invalid Program");
		
		if(! programStructureList.contains(studentBean.getPrgmStructApplicable())) 
			throw new IllegalArgumentException("Invalid Program Structure");
		
		//checks if entered CenterCode is present in database
		if(centerCodeAndNameMap.containsKey(studentBean.getCenterCode())) {
			//if CenterCode is present in database, it's corresponding CenterName is checked
			if(! centerCodeAndNameMap.get(studentBean.getCenterCode()).equals(studentBean.getCenterName()))
				throw new IllegalArgumentException("Invalid CenterName");
		}
		else {
			throw new IllegalArgumentException("Invalid CenterId");
		}
		
		//Check if a String is a Valid Date
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-M-d", Locale.US);
		try {
			LocalDate.parse(studentBean.getDob(), dateFormatter);
		}
		catch(DateTimeParseException ex) {
			throw new IllegalArgumentException("Invalid DateOfBirth");
		}
	}
	
	/**
	 * consumerType of the student is determined by the program and centerName fields
	 * @param centerName - centerName of the student
	 * @param program - program of the student
	 * @return consumerType of the student
	 */
	public String getStudentConsumerType(String centerName, String program) {
		String consumerType = "";
		try {
			ArrayList<String> consumerTypeNameList = portalDAO.getAllConsumerTypes();
			
			if(consumerTypeNameList.contains(centerName)) 
				consumerType = centerName;
			else if("EPBM".equalsIgnoreCase(program) || "MPDV".equalsIgnoreCase(program)) 
				consumerType = "SAS";
			else 
				consumerType = "Retail";
		}
		catch(Exception ex) {
			profileSyncSfdcLogger.error("Unable to get all active Consumer types, due to " + ex.toString());
			throw new RuntimeException("Unable to get all active Consumer types from database!");
		}
		
		return consumerType;
	}
	
	/**
	 * Master key of the student is obtained by using the consumerType, program and programStructureApplicable fields of the student
	 * @param consumerType - consumerType of the student
	 * @param program - program of the student
	 * @param programStructure - programStructureApplicable of the student
	 * @return consumerProgramStructureId of the student
	 */
	public int getStudentConsumerProgramStructureId(String consumerType, String program, String programStructure) {
		int consumerProgramStructureId = 0;
		try {
			int consumerTypeId = portalDAO.getConsumerTypeIdByConsumerType(consumerType);
			int programId = portalDAO.getProgramIdByCode(program);
			int programStructureId = portalDAO.getProgramStructureIdByProgramStructure(programStructure);
			
			consumerProgramStructureId = portalDAO.getConsumerProgramStructureIdById(consumerTypeId, programId, programStructureId);
		}
		catch(Exception ex) {
			profileSyncSfdcLogger.error("Error while retrieving CPS Id from database. Exception thrown: " + ex.toString());
		}
		
		return consumerProgramStructureId;
	}
	
	/**
	 * Sends the Student details from the database
	 * @param sapid - SAPID of the student
	 * @return A JSON formatted String of the Student details 
	 */
	public String getStudentDetailsAsJsonString(String sapid, StudentStudentPortalBean enteredStudentBean) {
		//Getting the Student Details
		StudentStudentPortalBean studentDetails = new StudentStudentPortalBean();
		try {
			studentDetails = studentInfoCheckDAO.getstudentData(sapid);
			enteredStudentBean.setRegDate(studentDetails.getRegDate());
			studentDetails.setProgramStructure(studentDetails.getPrgmStructApplicable());		//explicitly setting programStructure value as both programStructure and prgmStructApplicable fields exist in StudentStudentPortalBean,
																								//but in exam.students table the column name is PrgmStructApplicable, hence programStructure field gets set as null 
			profileSyncSfdcLogger.info("Fetched details of Student: " + sapid);
		}
		catch(Exception ex) {
			profileSyncSfdcLogger.error("Unable to fetch details of student: " + sapid + " from database, due to " + ex.toString());
		}
		
		//Migrating the Student data present in StudentBean to the DTO Layer
		StudentProfileDto returnedStudentProfileDto = ObjectConverter.convertObjToXXX(studentDetails, new TypeReference<StudentProfileDto>() {});
		
		//Filtering out certain properties from the DTO layer which are not required
		String studentProfileDtoString = ObjectConverter.filterPropertiesFromObjectAsJsonString(returnedStudentProfileDto, new String[] {"husbandName", "imageUrl", "mobile", "houseNoName", "street", "locality", "centerCode", "prgmStructApplicable", "oldProgram"});
		profileSyncSfdcLogger.info("Details of Student: " + sapid + " converted, and returned as a JSON String for API response");
		
		return studentProfileDtoString;
	}
	
	/**
	 * Update attributes of the user LDAP Object
	 * @param userId - ID of the user
	 * @param firstName - first name of the user
	 * @param lastName - last name of the user
	 * @param emailId - email address of the user
	 * @param mobileNo - mobile number of the user
	 * @param altPhone - alternate phone number of the user
	 * @param program - program of the user
	 */
	private void updateUserLdapObject(String userId, String firstName, String lastName, String emailId, String mobileNo, String altPhone, String program) {
		String displayName = StudentProfileUtils.getValidLdapDisplayName(firstName, lastName);					//stores LDAP acceptable display name
		lastName = StudentProfileUtils.getValidLdapLastName(lastName);											//returns LDAP acceptable last name
		
		ldapDAO.updateLdapProfileSFDC(userId, firstName, lastName, displayName, emailId, mobileNo, altPhone, program);
	}
	
	public String checkPDDMPassSemForRereg(String sapid) throws Exception  {
		
		int sem_pass_count = sfdc.checkPDDMPassSemForRereg(sapid);
		if(sem_pass_count == 1)
			return "true";
		else
			return "false";
	}
	
	
	/**
	* Creates a record in the mails table and user_mails  using the provided MailStudentPortalBean.
	* @param mailStudentPortalBean The MailStudentPortalBean object containing the information for the record.
	* @return long mailTemplateId of mails table.
	* @throws Exception If an error occurs during the record creation process.
	*/
	@Transactional(readOnly = false,propagation = Propagation.REQUIRED , rollbackFor = Exception.class)
	public Long createRecordInUserMailTableAndMailTable(MailStudentPortalBean mailStudentPortalBean) throws Exception{
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		long mailTemplateId = pDao.insertEMailRecord(mailStudentPortalBean);
		pDao.insertUserEMailRecord(mailStudentPortalBean , mailTemplateId);
		return mailTemplateId;
		}
	
	
	public CashFreeResponseBean getCashFreeToken(final String type) {
		CashFreeResponseBean reponseBean = new CashFreeResponseBean();
		if(type!=null && StringUtils.isNotBlank(type)) {
			
			if(type.equalsIgnoreCase("Bank")) {
				reponseBean = getCashFreeBankAuthorizedToken();
			}else if(type.equalsIgnoreCase("Aadhar")){
				reponseBean = getCashFreeAdharAuthorizedToken();
			}else {
				reponseBean.setType(type);
				reponseBean.setStatus("error");
				reponseBean.setMessage("type parameter is not valid");
				profileSyncSfdcLogger.error("Error: CashFree Token {} type parameter is not valid",reponseBean.getType());
			}
			reponseBean.setType(type);
			return reponseBean;
		}else {
			reponseBean.setStatus("error");
			reponseBean.setMessage("type parameter is empty");
			return reponseBean;
		}
	}
	
	private CashFreeResponseBean getCashFreeAdharAuthorizedToken() {
		CashFreeResponseBean bean = new  CashFreeResponseBean();
		String encryptedSignature ="";
		try {
			encryptedSignature = getCashFreeEncryptedSignature();
			bean.setToken(getAuthorizedToken(encryptedSignature, CASHFREE_AADHAR_AUTHORIZATION_URL));
			bean.setStatus("success");
			profileSyncSfdcLogger.info("CashFree Authorized Adhar token:{}",bean.getToken());
		}catch (Exception e) {
			bean.setStatus("error");
			bean.setMessage(e.getMessage());
			profileSyncSfdcLogger.error("Exception Error: getCashFreeAadharAuthorizedToken() Error :{}",Throwables.getStackTraceAsString(e));
		}
		return bean;
	}

	private CashFreeResponseBean getCashFreeBankAuthorizedToken() {
		CashFreeResponseBean bean = new  CashFreeResponseBean();
		String encryptedSignature ="";
		try {
			encryptedSignature = getCashFreeEncryptedSignature();
			bean.setToken(getAuthorizedToken(encryptedSignature, CASHFREE_BANK_AUTHORIZATION_URL));
			bean.setStatus("success");
			profileSyncSfdcLogger.info("CashFree Authorized Bank token:{}",bean.getToken());
		}catch (Exception e) {
			bean.setStatus("error");
			bean.setMessage(e.getMessage());
			profileSyncSfdcLogger.error("Exception Error: getCashFreeBankAuthorizedToken() Error :{}",Throwables.getStackTraceAsString(e));
		}
		return bean;
	}

	private String getCashFreeEncryptedSignature() throws Exception{
		final String clientIdWithEpochTimeStamp = CASHFREE_TOKEN_CLIENTID+"."+Instant.now().getEpochSecond();
		String encryptedSignature ="";
		try {
	        byte[] keyBytes = Files.readAllBytes(new File(CASHFREE_PEM_FILEPATH).toPath()); // Absolute Path to be replaced
	        String publicKeyContent = new String(keyBytes);
	        publicKeyContent = publicKeyContent.replaceAll("[\\t\\n\\r]", "").replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
	        KeyFactory kf = KeyFactory.getInstance("RSA");
	        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
	        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
	        final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
	        profileSyncSfdcLogger.info("Cashe Free clientIdWithEpochTimeStamp : "+clientIdWithEpochTimeStamp);
	        encryptedSignature = Base64.getEncoder().encodeToString(cipher.doFinal(clientIdWithEpochTimeStamp.getBytes()));
	        profileSyncSfdcLogger.info("Cashe Free encryptedSignature : "+encryptedSignature);
	        if(encryptedSignature!=null && StringUtils.isNotBlank(encryptedSignature)) {
	        	return encryptedSignature;
	        }else {
	        	throw new Exception("Invalid clientId and clientSecret combination");
	        }
	    } catch (Exception e) {
	    	profileSyncSfdcLogger.error("Exception Error: getCashFreeTokenForSFDC() Error :{}",Throwables.getStackTraceAsString(e));
	        //e.printStackTrace();
	    	throw new Exception(e);	
	    }
		
	}
	
	public String getAuthorizedToken(final String encryptedSignature, final String URL) throws Exception{
		String token="";
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("X-Client-Id", CASHFREE_TOKEN_CLIENTID);
			headers.add("X-Client-Secret", CASHFREE_TOKEN_CLIENT_SECRET);
			headers.add("X-Cf-Signature", encryptedSignature);
			HttpEntity<String> entity = new HttpEntity<String>("",headers);
			ResponseEntity<String> response = restTemplate.exchange(URL, HttpMethod.POST, entity, String.class);
			JsonObject jsonResponse = new JsonParser().parse(response.getBody()).getAsJsonObject();
			if ("200".equalsIgnoreCase(response.getStatusCode().toString())) {
				String status = jsonResponse.get("status").getAsString();
				if("SUCCESS".equalsIgnoreCase(status)) {
					JsonObject dataResponse = jsonResponse.get("data").getAsJsonObject();
					token = dataResponse.get("token").getAsString();
					if(token!=null && StringUtils.isNotBlank(token)) {
						return token;
					}else {
						profileSyncSfdcLogger.error("Error generating authorized token");
						throw new Exception("Error generating authorized token");
					}
				}
			}
		}
		catch (Exception e) {
			profileSyncSfdcLogger.error("Exception Error: getAuthorizedToken() Error :{}",Throwables.getStackTraceAsString(e));
			throw new Exception("Invalid clientId and clientSecret combination");
			//e.printStackTrace();
		}
		return token;
	}
		
	
}
