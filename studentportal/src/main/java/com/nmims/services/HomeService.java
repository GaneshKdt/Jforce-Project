package com.nmims.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.VideoContentStudentPortalBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentDAO;
import com.nmims.helpers.MailSender;
import com.nmims.util.ContentUtil;

@Service("homeService")
public class HomeService {
	
	@Autowired
	private ContentDAO contentDAO;
	
	@Autowired 
	private PortalDao portalDAO;
	
	@Autowired
	private LDAPDao ldapDAO;
	
	@Autowired
	private StudentDAO studentDAO;
	
	@Autowired
	private MailSender mailer;

	@Value( "${SERVER_PATH}" )
	private String server_path;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeService.class);
	
	private static final String EMAIL_VALIDATION_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
	private static final String EMAIL_MASKING_REGEX = "(?<=.)[^@](?=[^@]*?[^@]@)|(?:(?<=@.)|(?!^)\\G(?=[^@]*$)).(?=.*[^@]\\.)";
	private static final String PASSWORD_VALIDATION_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\\$!%\\*#\\?&])[A-Za-z\\d@\\$!%\\*#\\?&]{8,20}$";
	
	/**
	 * Fetching session recordings based on applicable PSS id's and academic cycle for home page from De-Normalized table.
	 * @param currentSemPSSId - list contains current student applicable program sem subject Id's.
	 * @param month - student registration month for academic cycle.
	 * @param year - student registration year for academic cycle.
	 * @return List - returns the sorted video contents list in the descending order.
	 */
	public List<VideoContentStudentPortalBean> getVideos(List<String> currentSemPSSId, String month, String year){
		List<VideoContentStudentPortalBean> videoList = null;
		String acadDateFormat = null;
		
		// Preparing a comma separated single string of multiple current semester PSS  id's
		String passIds = ContentUtil.frameINClauseString(currentSemPSSId);
		
		//Convert student registration month and year to YYYY-MM-DD format for acadDateFormat 
		acadDateFormat = ContentUtil.pepareAcadDateFormat(month,year);
		
		//Fetching session recordings based on applicable PSS id's and academic cycle  
		videoList = contentDAO.getSessionRecordingOnHome(passIds,acadDateFormat);
		
		//Sort the video content list in the descending order based on videoContentId
		videoList = ContentUtil.sortInDesc(videoList);

		//return sorted list
		return videoList;
	}

	public List<VideoContentStudentPortalBean> getVideos(String programSemsubjectId, String acadDateFormat,String earlyAccess){
		List<VideoContentStudentPortalBean> videoList = null;
		
		//Fetching session recordings based on applicable PSS id's and academic cycle  
		videoList = portalDAO.getSessionRecording(programSemsubjectId,acadDateFormat,earlyAccess);
		
		//return video content list
		return videoList;
	}
	
	/**
	 * The userId of the user is validated via LDAP and their details are fetched from the LDAP and MySQL database, 
	 * the email of the user is then validated using OWASP validation regex which checks if the email is in proper format,
	 * a mail consisting of the user's password is then sent to the user's registered email address
	 * @param userId - id of the user
	 * @return email address of the user when mail is sent successfully, empty string if an error occurs while sending the mail
	 * @throws Exception
	 */
	public String forgotPasswordVerifyDetails(String userId) throws Exception {
		Map<String, String> userDetails = ldapDAO.getUserEmailPassword(userId);
		String name = userDetails.get("name");
		String email = userDetails.get("email");
		String password = userDetails.get("password");
		logger.info("Retreived LDAP details of user: " + userId + ", name: " + name + ", email: " + email + ", password: " + password);

		if(userId.startsWith("77") || userId.startsWith("79")) {		//To check if the user is a student
			try {
				Map<String, Object> studentDetails = studentDAO.getStudentFirstNameAndEmail(userId);
				name = (String) studentDetails.get("firstName");
				email = (String) studentDetails.get("emailId");
				logger.info("Retreived details of student: " + userId + ", name: " + name + ", email: " + email);
			}
			catch(DataAccessException ex) {
				//EmptyData Exception is thrown when there is no record of the student in exam.students table
				logger.error("Student " + userId + " not present in database, Exception: " + ex.toString());
				throw new RuntimeException("Unable to find student details! Please recheck your Student Number " + 
											"or contact ngasce@nmims.edu for any further assistance.");
			}
		}

		//Validating the format of the user email address
		Pattern pattern = Pattern.compile(EMAIL_VALIDATION_REGEX);
		Matcher matcher = pattern.matcher(email);

		if(matcher.matches()) {
			logger.info("OWASP validation regex matched for user: " + userId + ", with email: " + email);
			
			HashMap<String, String> parameters = new HashMap<>();
			parameters.put("module", "portal");
			parameters.put("topic", "password");
			parameters.put("name", name);
			parameters.put("email", email);
			parameters.put("password", password);
			
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.postForObject(server_path+"mailer/m/sendMail", parameters, HashMap.class);
				logger.info("Forgot Password email sent to user via the new mailer app : " + userId);
			}
			catch(HttpStatusCodeException e) {
                logger.error("HttpStatusCodeException while sending mail using mailer app to the student: " + userId + " with mailId: " + email);
                throw new RuntimeException("Unable to send forgot password mail. Please try again after some time.");
            }
			
			/*
			mailer.sendPasswordEmail(name, email, password);
			String maskedEmail = email.replaceAll(EMAIL_MASKING_REGEX, "*");	//Email Address of the user is masked, as student email will be displayed on the front end
			return maskedEmail;
			*/
			
			return email;
		}
		else {
			//An Exception is thrown when the user's email address fails the OWASP validation
			logger.error("OWASP validation regex failed for user: " + userId + ", with emailId: " + email);
			throw new RuntimeException("Registered Email Address is invalid! " + 
										"Please contact ngasce@nmims.edu to update your email address.");
		}
	}
	
	/**
	 * The Password and confirmPassword fields are validated.
	 * Checks if the password matches the password validation regex, and if the password and confirmPassword fields match. 
	 * @param userId - id of the user
	 * @param password - password as a String
	 * @param confirmPassword - password repeated (case-sensitive)
	 */
	public void validateStudentPassword(String userId, String password, String confirmPassword) {
		Pattern pattern = Pattern.compile(PASSWORD_VALIDATION_REGEX);
		Matcher matcher = pattern.matcher(password);

		if(!matcher.matches()) {
			logger.error("Password validation regex failed for user: {} with password: {}", userId, password);
			throw new IllegalArgumentException("Password does not meet the Password Policy. Please enter password as per policy.");
		}
		
		if(!password.equals(confirmPassword)) {
			logger.error("Entered passwords of student: {} do not match. Password: {} Confirm Password: {}", userId, password, confirmPassword);
			throw new IllegalArgumentException("Confirm password does not match the entered password. Please check and try again!");
		}
	}
	
	public boolean checkLOUConfirmed(String sapid) {
		return portalDAO.checkLOUConfirmed(sapid);
	}
	public void savelouConfirmed(String sapid) {
		portalDAO.savelouConfirmed(sapid);
	}
}
