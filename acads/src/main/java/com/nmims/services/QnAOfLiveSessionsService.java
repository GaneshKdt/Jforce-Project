package com.nmims.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nmims.beans.SessionBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.daos.ContentDAO;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.ZoomManager;

@Service
public class QnAOfLiveSessionsService {
	
	@Autowired
	private ContentDAO contentDAO;
	
	@Autowired
	SessionDayTimeAcadsBean sessionQA;
	
	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	private ZoomManager zoomManger;
	
	private static final Logger logger = LoggerFactory.getLogger("qnaOfLiveSessionsService");
	
	public List<SessionDayTimeAcadsBean> setMirrorSessionQnACount(List<SessionDayTimeAcadsBean> sessionList, String facultyId){
		for(SessionDayTimeAcadsBean bean : sessionList) {
			try {
				Integer  mirrorCount = contentDAO.getMirrorSessionQnACount(facultyId, bean.getId() );
				Integer totalCount = Integer.parseInt(bean.getCount())+ mirrorCount;
				bean.setCount(totalCount.toString());
			}catch (Exception e) {
				// TODO: handle exception
				  
			}
		}
		return sessionList;
	}
	
	public List<SessionQueryAnswer> getSingleSessionsQnAForFaculty(String session_id, String facultyId){
		List<SessionQueryAnswer> sessionQA = new ArrayList<SessionQueryAnswer>();
		List<SessionQueryAnswer> mirrorSessionQA = new ArrayList<SessionQueryAnswer>();
		try {
			sessionQA = contentDAO.getSingleSessionsQA(session_id, facultyId);
			mirrorSessionQA = contentDAO.getMirrorSessionQnA(session_id, facultyId);
			sessionQA.addAll(mirrorSessionQA);
			return sessionQA;			
		}catch (Exception e) {
			// TODO: handle exception
			  
			return sessionQA;
		}
		
	}
	
	public void webinarQnAReport(String sessionDate) throws IOException{
		ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");
		
		logger.info("Scheduler called on date : "+ sessionDate);

		ArrayList<SessionBean> sessionsList = cDao.getSessionsHeldPerDay(sessionDate);
		if (sessionsList != null || sessionsList.size() != 0) {
			for (SessionBean session : sessionsList) {
				JsonObject qaReport = zoomManger.getQAReportFromWebinar(session.getMeetingKey());
				if (qaReport != null) {
					logger.info("sessionId : "+session.getId()+" subject : "+session.getSubject()+" sessionName : "+session.getSessionName()+" Main meeting QnA "+session.getMeetingKey());
					saveSingleSessionQA(qaReport,"main", session);
				}
				//added for parallel sessions or mirror sessions
				if (session.getAltMeetingKey() != null) {
					JsonObject altQaReport = zoomManger.getQAReportFromWebinar(session.getAltMeetingKey());
					if (altQaReport != null) {
						logger.info("sessionId : "+session.getId()+" subject : "+session.getSubject()+" sessionName : "+session.getSessionName()+" Alt meeting QnA "+session.getAltMeetingKey());
						saveSingleSessionQA(altQaReport,"alt", session);
					}	
				}
				
				if (session.getAltMeetingKey2() != null) {
					JsonObject altQaReport2 = zoomManger.getQAReportFromWebinar(session.getAltMeetingKey2());
					if (altQaReport2 != null) {
						logger.info("sessionId : "+session.getId()+" subject : "+session.getSubject()+" sessionName : "+session.getSessionName()+" Alt2 meeting QnA "+session.getAltMeetingKey2());
						saveSingleSessionQA(altQaReport2,"alt2", session);
					}	
				}
				
				if (session.getAltMeetingKey3() != null) {
					JsonObject altQaReport3 = zoomManger.getQAReportFromWebinar(session.getAltMeetingKey3());
					if (altQaReport3 != null) {
						logger.info("sessionId : "+session.getId()+" subject : "+session.getSubject()+" sessionName : "+session.getSessionName()+" Alt3 meeting QnA "+session.getAltMeetingKey3());
						saveSingleSessionQA(altQaReport3,"alt3", session);
					}	
				}
			}
		}
	}
	
	// save single session QA
			public void saveSingleSessionQA(JsonObject qaReport,String type, SessionBean session) throws IOException {
				ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");
				try {
					if (qaReport != null) {

						// extract questions and answers from webinar json object
						JsonArray QAarray = (JsonArray) qaReport.get("questions");

						if (qaReport.get("questions") != null && QAarray.size() != 0) {
							JsonArray question_details = new JsonArray();
							String key = qaReport.get("id").toString();

							String emailid = "";
							
							if(type.equalsIgnoreCase("main")) {
								emailid = cDao.getFacultyEmailId(session.getFacultyId());
							}else if(type.equalsIgnoreCase("alt")) {
								String facultyId = ( ("NGASCE7777".equals(session.getAltFacultyId()) || "NGASCE9999".equals(session.getAltFacultyId())) ? session.getFacultyId() :session.getAltFacultyId()) ;
								emailid = cDao.getFacultyEmailId(facultyId);
							}else if(type.equalsIgnoreCase("alt2")) {
								String facultyId = ( ("NGASCE7777".equals(session.getAltFacultyId2()) || "NGASCE9999".equals(session.getAltFacultyId2())) ? session.getFacultyId() :session.getAltFacultyId2()) ;
								emailid = cDao.getFacultyEmailId(facultyId);
							}else if(type.equalsIgnoreCase("alt3")) {
								String facultyId = ( ("NGASCE7777".equals(session.getAltFacultyId3()) || "NGASCE9999".equals(session.getAltFacultyId3())) ? session.getFacultyId() :session.getAltFacultyId3()) ;
								emailid = cDao.getFacultyEmailId(facultyId);	
							}
							logger.info("faculty emailid :"+emailid);
							sessionQA.setSession_id(session.getId());
							String SessionName = session.getSessionName();
							String subject = session.getSubject();
							String facultyEmail = emailid;
							String emailbody = "";
							
							int count=0;
							for (JsonElement qa : QAarray) {
								try {
									String studentEmail = ((JsonObject) qa).get("email").getAsString();

									String studentName = ((JsonObject) qa).get("name").getAsString();
									emailbody += studentName + ": ";

									question_details = (JsonArray) ((JsonObject) qa).get("question_details");

									String sapid = cDao.getstudentByEmail(studentEmail);
									
									if( sapid != null && !"".equals(sapid) ) {
									for (JsonElement qn_details : question_details) {
										String question = ((JsonObject) qn_details).get("question").getAsString();
										String answer = ((JsonObject) qn_details).get("answer").getAsString().trim();
										emailbody += question + "\n";

										if (!answer.isEmpty()) {
											emailbody += "faculty: " + answer + "\n";
											sessionQA.setIsAnswered("Answered");
										} else {
											sessionQA.setIsAnswered("Open");
										}
										// populate QA bean to save
										sessionQA.setQuestion(question.toString());
										sessionQA.setAnswer(answer.toString());
										sessionQA.setMeetingKey(key);
										sessionQA.setSapId(sapid);
										
										boolean flag=cDao.getQueryOccurenceForSapIdInMeeting(sessionQA);
										if(!flag) {
										boolean countflag=cDao.saveSessionQA(sessionQA, logger);
										if(countflag) {
										count=count+1;
										}
										}else {
										logger.info("Data Already available for " +sessionQA.getSapId()+" "+sessionQA.getQuestion()+" in meetingkey "+key);
										}
									}
								}

								} catch (Exception e) {
									 StringWriter sw = new StringWriter();
									 e.printStackTrace(new PrintWriter(sw));
									 String exceptionAsString = sw.toString();
									 logger.error("Exception occur "+exceptionAsString);
								}
							}
							logger.info(count+" records inserted in meeting key "+key);
							// String emailbody1 = questions.toString().replaceAll(",",
							// "\n").replaceAll("[{}\\[\\]]","").replace("\"question_details\":", "");

							MailSender mailSender = (MailSender) act.getBean("mailer");
							ArrayList<String> recipent = new ArrayList<String>(
									Arrays.asList(facultyEmail, "sneha.utekar@nmims.edu")); // sneha.utekar@nmims.edu
							mailSender.sendEmail("Q&A of " + SessionName + " " + subject, emailbody, recipent);
						}
					}
				} catch (Exception e) {
					 StringWriter sw = new StringWriter();
					 e.printStackTrace(new PrintWriter(sw));
					 String exceptionAsString = sw.toString();
					 logger.error("Exception occur "+exceptionAsString);
				}
			}
}
