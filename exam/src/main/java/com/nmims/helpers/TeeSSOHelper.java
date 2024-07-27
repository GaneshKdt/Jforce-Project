package com.nmims.helpers;

import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.SimpleDateFormat;
import com.nmims.beans.DemoExamBean;
import com.nmims.beans.ExamScheduleinfoBean;
import com.nmims.beans.MettlSSOInfoBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.TcsOnlineExamBean;
import com.nmims.daos.MettlTeeDAO;

@Component
public class TeeSSOHelper {

	@Autowired
	MettlTeeDAO ssoDao;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	TNSMailSender tnsMailSender;
	
	@Autowired
	EmailHelper emailHelper;
	

	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;

	public String generateMettlLink(MettlSSOInfoBean booking) throws Exception {
		TreeMap<String, String> params = new TreeMap<String, String>();
		
		// fields for booking update
		params.put("sapid", booking.getSapid());
		params.put("emailId", booking.getEmailId());
		params.put("imageUrl", booking.getImageURL());
		
		params.put("subject", booking.getSubject());
		params.put("year", booking.getYear());
		params.put("month", booking.getMonth());
		params.put("trackId", booking.getTrackId());
		
		if(!StringUtils.isBlank(booking.getJoinURL()) && booking.getJoinURL().contains("ecc=")) {
			params.put("ecc", booking.getJoinURL().split("ecc=")[1]);
		} else {
			params.put("accessUrl", booking.getJoinURL());
		}
		
		params.put("scheduleId", booking.getScheduleId());
		
		params.put("startTime", booking.getExamStartDateTime());
		params.put("reportingStartTime", booking.getReporting_start_date_time());
		params.put("endTime", booking.getExamEndDateTime());
		params.put("accessEndTime", booking.getAccessEndDateTime());
		
		params.put("firstname", booking.getFirstname());
		params.put("lastname", booking.getLastname());

		return SERVER_PATH + "ltidemo/mettl_sso_student?joinKey=" + URLEncoder.encode(encryptParameters(params), "UTF-8");
	}
	

	private String encryptParameters(TreeMap<String, String> params) throws Exception {
		try {
			List<String> valuesToEncrypt = new ArrayList<String>();
			for (Entry<String, String> entry : params.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if(!StringUtils.isBlank(key) && !StringUtils.isBlank(value)) {
					valuesToEncrypt.add(key + "=" + value);	
				}
			}
			String data = StringUtils.join(valuesToEncrypt, "\n");
			
			String encryptedData = AESencrp.encrypt(data);
			String encryptedDataBase64 = getStringBase64Encoded(encryptedData);
			return encryptedDataBase64;
		}catch (Exception e) {
			
			throw new Exception("Error Encrypting Parameters");
		}
	}
	
	private String getStringBase64Encoded(String input) {
		return new String(Base64.encodeBase64(input.getBytes()));
	}
	
	public void sendJoinMail(MettlSSOInfoBean booking) throws Exception {
        //tnsMailSender.sendJoinLinkEmailToStudent(booking);
		HashMap<String, String> payload = new HashMap<>();
		payload.put("module", "exam");
		payload.put("topic", "exam-join-link");
		payload.put("email", booking.getEmailId());
		payload.put("subject", booking.getSubject());
		payload.put("examStartDateTime", booking.getFormattedDateStringForEmail());
		payload.put("joinURL", booking.getJoinURL());
		
		emailHelper.sendAmazonSESMail(payload);
	}
	

	public String generateMettlLinkForDemoExam(DemoExamBean demoExam, StudentExamBean student) throws Exception {
		TreeMap<String, String> params = new TreeMap<String, String>();
		
		// fields for booking update
		params.put("sapid", student.getSapid());
		params.put("subject", demoExam.getSubject());
		params.put("accessUrl", demoExam.getLink());
		params.put("emailId", student.getEmailId());
		params.put("firstname", student.getFirstName());
		params.put("lastname", student.getLastName());
		params.put("scheduleName", demoExam.getSubject());
		params.put("scheduleId", "" + demoExam.getId());
		
		return SERVER_PATH + "ltidemo/mettl_sso_demo_student?joinKey=" + URLEncoder.encode(encryptParameters(params), "UTF-8");
	}
	

	public String getFormattedDateTimeForEmail(String string) throws ParseException {

		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat formatForEmail = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm:ss");
		
		Date examDate = inputFormat.parse(string);
		return formatForEmail.format(examDate) + " IST";
	}
	
	public String getFormattedDateAndTimeForEmail(String string) throws ParseException {
		
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		SimpleDateFormat formatForEmail = new SimpleDateFormat("EEE, MMM dd, yyyy HH:mm:ss");
		
		Date examDate = inputFormat.parse(string);
		return formatForEmail.format(examDate) + " IST";
	}


	public String generateMettlExamLink(ExamScheduleinfoBean studentInfoBean) throws Exception{
		// TODO Auto-generated method stub

		TreeMap<String, String> params = new TreeMap<String, String>();
		
		// fields for mbawx exam join link params
		params.put("sapid",studentInfoBean.getSapid());
		params.put("timeboundId", studentInfoBean.getTimeboundId());
		params.put("scheduleId", studentInfoBean.getScheduleId());
		
		String joinUrl=SERVER_PATH+"ltidemo/start_mettl_assessment?joinKey=" + URLEncoder.encode(encryptParameters(params), "UTF-8");
		return joinUrl;
	}
	
	public String sendExamJoinLinkMail (ExamScheduleinfoBean booking) throws Exception {
		HashMap<String, String> payload = new HashMap<>();
		payload.put("module", "exam");
		payload.put("topic", "mbawx-exam-join-link");
		payload.put("email", booking.getEmailId());
		payload.put("subject", booking.getSubject());
		payload.put("examStartDateTime", booking.getFormattedDateStringForEmail());
		payload.put("joinURL", booking.getJoinURL());
		//System.out.println("Payload :: "+payload);
		
		return emailHelper.sendAmazonSESMail(payload);
	}
}
