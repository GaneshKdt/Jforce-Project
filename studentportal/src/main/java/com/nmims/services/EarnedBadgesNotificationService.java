package com.nmims.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.EmailMessageBean;
import com.nmims.beans.OpenBadgeBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.EarnedBadgesNotificationDao;
import com.nmims.daos.EmailMessageDAO;
import com.nmims.daos.PortalDao;
import com.nmims.daos.StudentDAO;
import com.nmims.helpers.EmailHelper;
import com.nmims.interfaces.EarnedBadgesNotificationInterface;

@Service("earnedBadgesNotificationService")
public class EarnedBadgesNotificationService implements EarnedBadgesNotificationInterface{
	
	@Autowired
	PortalDao portalDao;
	
	@Autowired
	StudentDAO studentDAO;
	
	@Autowired
	EarnedBadgesNotificationDao earnedBadgesNotificationDao;
	
	@Autowired
	EmailHelper emailHelper;
	
	@Autowired
	EmailMessageDAO emailMessageDAO;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	private static final Logger logger = LoggerFactory.getLogger("badgeScheduler");
	
	private static final List<String> BADGE_TOPICS = Arrays.asList("badges-earned/unlocked-option1", "badges-earned/unlocked-option2", "badges-earned/unlocked-option3");
	private static final String COMMUNICATION_MAIL_FILTER_CRITERIA = "Badges Earned/Unlocked";
	private static final String YES="Y";
	private static final String MODULE="badges";
	private static final List<Integer> EXCLUDED_BADGE_IDS=Arrays.asList(118,119,120,121,122);
	private static final String PORTAL_VISIT_STREAK_BADGE_TOPIC="badges-earned/unlocked-option1";

	@Override
	public void sendEmail(String sapId, Integer badgeId, Long issuedId, String awardedAt) {
		HashMap<String, String> mailData = new HashMap<>();
		Random random = new Random();
		try {
			String randomBadgeTopic = BADGE_TOPICS.get(random.nextInt(BADGE_TOPICS.size()));
			
			if(EXCLUDED_BADGE_IDS.contains(badgeId))
				randomBadgeTopic = PORTAL_VISIT_STREAK_BADGE_TOPIC;
			
			Integer criteriaType = earnedBadgesNotificationDao.getCriteriaTypeByAwardedAt(awardedAt);
			StudentStudentPortalBean registrationData = registrationDataByCriteriaType(criteriaType, awardedAt, badgeId, sapId);
			Map<String, Object> personDetails = studentDAO.getStudentPersonalDetails(Long.valueOf(sapId));
			OpenBadgeBean badgeDetails = earnedBadgesNotificationDao.getBadgeDetailsByBadgeId(badgeId);
			
			EmailMessageBean messageBean = emailMessageDAO.getMessageForModule(emailMessageDAO.getModuleIdByModuleAndTopic(MODULE, randomBadgeTopic));
			
			mailData.put("topic", randomBadgeTopic);
			mailData.put("module", MODULE);
			mailData.put("studentName", personDetails.get("firstName") + " "+ personDetails.get("lastName"));
			mailData.put("program", registrationData.getProgram());
			mailData.put("sem", registrationData.getSem());
			mailData.put("email", (String) personDetails.get("emailId"));
			mailData.put("feat", badgeDetails.getBadgeName());
			mailData.put("badgeUrl", badgeDetails.getAttachment());
			mailData.put("awardedAt", messageBean.getSubject());
			mailData.put("body", messageBean.getBody());
			mailData.put("fromEmailId", messageBean.getFromEmailId());
			mailData.put("filterCriteria", COMMUNICATION_MAIL_FILTER_CRITERIA);
			mailData.put("sapIdRecipient", sapId);
			
			callMailSenderService(mailData, issuedId);
		}catch (Exception e) {
			logger.error("Error: while sending email from EarnedBadgesNotificationService for module: {} topic: {} issuedId: {}. "
					+ "Exception: ",mailData.get("module"), mailData.get("topic"), issuedId, e.getMessage(), e);
	    }
	}
	
	public void callMailSenderService(HashMap<String, String> mailData, Long issuedId) throws Exception {
	    try {
	    	RestTemplate restTemplate = new RestTemplate();
	        String url = SERVER_PATH +"mailer/m/sendMailAndSaveCommunicationMail";
	        HashMap<String, String> response  = restTemplate.postForObject(url, mailData, HashMap.class);
            if ("true".equals(response.get("success"))) {
            	earnedBadgesNotificationDao.updateEarnedBadgesNotificationStatus(YES, issuedId);
	            logger.info("{} for module: {} topic: {} issuedId: {}",response.get("message"), MODULE, mailData.get("topic"), issuedId);
	        } else {
	            logger.error("Error: failed to send mail for module: {} topic: {} issuedId: {} due to {}", mailData.get("module"), mailData.get("topic"), mailData.get("email"), issuedId);
	        }
	    } catch (HttpClientErrorException e) {
	        logger.error("Exception: while calling the mailer app from EarnedBadgesNotificationService: {}", e.getMessage());
	        throw e;
	    }
	}

	private StudentStudentPortalBean registrationDataByCriteriaType(Integer criteriaType, String awardedAt, Integer badgeId, String sapId) {
		switch (criteriaType) {
		case 1:
			return registrationDataWithCriteriaTypeOne(sapId, awardedAt);
		case 2:
			return registrationDataWithCriteriaTypeTwo(sapId);
		case 3:
			return registrationDataWithCriteriaTypeThree(badgeId, sapId);
		default:
			logger.error("Error: invalid criteria type {} for badgeId {} for sapId {}", criteriaType, badgeId, sapId);
			throw new RuntimeException("Error: invalid criteria type "+criteriaType);
		}
	}

	private StudentStudentPortalBean registrationDataWithCriteriaTypeOne(String sapId, String awardedAt) {
		Integer semester = earnedBadgesNotificationDao.getSemesterBySapIdAndAwardedAt(sapId, awardedAt);
		return earnedBadgesNotificationDao.getRegistrationBySapIdAndSemester(sapId, semester);
	}
	
	private StudentStudentPortalBean registrationDataWithCriteriaTypeTwo(String sapId) {
		return portalDao.getStudentsMostRecentRegistrationData(sapId);
	}
	
	private StudentStudentPortalBean registrationDataWithCriteriaTypeThree(Integer badgeId, String sapId) {
		Integer semester = earnedBadgesNotificationDao.getSemesterByBadgeIdAndSapId(badgeId);
		return earnedBadgesNotificationDao.getRegistrationBySapIdAndSemester(sapId, semester);
	}
}
