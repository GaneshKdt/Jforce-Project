package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ExamScheduleinfoBean;
import com.nmims.daos.MettlTeeDAO;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.TeeSSOHelper;
import com.nmims.services.MBAWXTeeService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class sendExamJoinLinkEmailForMbaWxTest {

	
	@Mock
	MettlTeeDAO ssoDao;
	
	@Mock
	TeeSSOHelper teeSSOHelper;
	
	@Mock
	MailSender mailSender;
	
	@InjectMocks
	MBAWXTeeService service;
	
	
	 @Test 
	 public void  testSendExamLinkToRegulerStudent() {
		  MockitoAnnotations.initMocks(this);
		  System.out.println("---- sendExamJoinLinkEmailForMbaWxTest.testSendExamLinkToRegulerStudent() -----");
		  List<ExamScheduleinfoBean> successfulEmails = new ArrayList<ExamScheduleinfoBean>();
			List<ExamScheduleinfoBean> failedEmails = new ArrayList<ExamScheduleinfoBean>();
			List<ExamScheduleinfoBean> examAttemptedregularStudentList=new ArrayList<ExamScheduleinfoBean>();
			ExamScheduleinfoBean bean=new ExamScheduleinfoBean();
			ExamScheduleinfoBean bean1=new ExamScheduleinfoBean();
			bean.setSapid("77122104930");
			bean.setTimeboundId("1398");
			bean1.setSapid("77221189823");
			bean1.setTimeboundId("1788");
			examAttemptedregularStudentList.add(bean);
			examAttemptedregularStudentList.add(bean1);
			List<ExamScheduleinfoBean> resitStudentList = new ArrayList<ExamScheduleinfoBean>();
			List<ExamScheduleinfoBean> regularStudentList = new ArrayList<ExamScheduleinfoBean>();
			//esm.sapid, esm.subject, esm.timebound_id, esm.emailId, esm.examStartDateTime, esm.schedule_id, esm.assessmentName
			ExamScheduleinfoBean regulerBean=new ExamScheduleinfoBean();
			ExamScheduleinfoBean regulerBean1=new ExamScheduleinfoBean();
			
			regulerBean.setSubject("Integrated Marketing Communications");
			regulerBean.setSapid("77122590244");
			regulerBean.setTimeboundId("1400");
			regulerBean.setEmailId("pradeepwaghmode8@gmail.com");
			regulerBean.setExamStartDateTime("2023-07-18 11:55:00");
			regulerBean.setScheduleId("12450164");
			regulerBean.setAssessmentName("MBA Wx TEE Integrated Marketing Communication New 1st July 2023 Slot 2");
			
			regulerBean1.setSubject("Text mining and Natural Language Pro-cessing using Deep learning");
			regulerBean1.setSapid("77221189823");
			regulerBean1.setTimeboundId("1788");
			regulerBean1.setEmailId("pradeepwaghmode8@gmail.com");
			regulerBean1.setExamStartDateTime("2023-06-17 11:00:00");
			regulerBean1.setScheduleId("12328382");
			regulerBean1.setAssessmentName("Text mining and Natural Language Processing using Deep Learning - Slot 2 - 17th Jun 2023-Revised");
			regularStudentList.add(regulerBean);
			regularStudentList.add(regulerBean1);
			 try {
				when(ssoDao.getRegularLastExamAttemptdStudent()).thenReturn(examAttemptedregularStudentList);
				when(ssoDao.getResitStudentByExamStartTime()).thenReturn(resitStudentList);
				when(ssoDao.getRegularStudentByExamStartTime()).thenReturn(regularStudentList);
				when(teeSSOHelper.getFormattedDateAndTimeForEmail(regulerBean.getExamStartDateTime())).thenReturn("Mon, Jul 17, 2023 19:00:00 IST");
				when(teeSSOHelper.generateMettlExamLink(regulerBean)).thenReturn("https://uat-studentzone-ngasce.nmims.edu/ltidemo/start_mettl_assessment?sapid=77122692092&timeboundId=1817&scheduleId=12592082").thenReturn("https://uat-studentzone-ngasce.nmims.edu/ltidemo/start_mettl_assessment?sapid=77122692092&timeboundId=1817&scheduleId=12592082");
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
		  boolean actualResult=false;
		  boolean expectedResult=true;
		  try {
			  service.sendExamJoinLinksForMbaWxStudents();
			  actualResult=true;
		  }
		  catch (Exception e) {
			 // e.printStackTrace();
			  actualResult=false;
		}
		  
		  assertEquals(expectedResult , actualResult);
		  
	 }
	 
	 @Test 
	 public void  testSendExamLinkToResitStudetns() {
		 MockitoAnnotations.initMocks(this);
		 System.out.println("----- sendExamJoinLinkEmailForMbaWxTest.testSendExamLinkToResitStudetns() -----");
		 List<ExamScheduleinfoBean> successfulEmails = new ArrayList<ExamScheduleinfoBean>();
		 List<ExamScheduleinfoBean> failedEmails = new ArrayList<ExamScheduleinfoBean>();
		 List<ExamScheduleinfoBean> examAttemptedregularStudentList=new ArrayList<ExamScheduleinfoBean>();
		 ExamScheduleinfoBean bean=new ExamScheduleinfoBean();
		 ExamScheduleinfoBean bean1=new ExamScheduleinfoBean();
		 bean.setSapid("77122104930");
		 bean.setTimeboundId("1398");
		 bean1.setSapid("77221189823");
		 bean1.setTimeboundId("1788");
		 examAttemptedregularStudentList.add(bean);
		 examAttemptedregularStudentList.add(bean1);
		 List<ExamScheduleinfoBean> resitStudentList = new ArrayList<ExamScheduleinfoBean>();
		 List<ExamScheduleinfoBean> regularStudentList = new ArrayList<ExamScheduleinfoBean>();
		 //esm.sapid, esm.subject, esm.timebound_id, esm.emailId, esm.examStartDateTime, esm.schedule_id, esm.assessmentName
		 ExamScheduleinfoBean regulerBean=new ExamScheduleinfoBean();
		 ExamScheduleinfoBean regulerBean1=new ExamScheduleinfoBean();
		 
		 regulerBean.setSubject("Integrated Marketing Communications");
		 regulerBean.setSapid("77122590244");
		 regulerBean.setTimeboundId("1400");
		 regulerBean.setEmailId("pradeepwaghmode8@gmail.com");
		 regulerBean.setExamStartDateTime("2023-07-18 11:55:00");
		 regulerBean.setScheduleId("12450164");
		 regulerBean.setAssessmentName("MBA Wx TEE Integrated Marketing Communication New 1st July 2023 Slot 2");
		 
		 regulerBean1.setSubject("Text mining and Natural Language Pro-cessing using Deep learning");
		 regulerBean1.setSapid("77221189823");
		 regulerBean1.setTimeboundId("1788");
		 regulerBean1.setEmailId("pradeepwaghmode8@gmail.com");
		 regulerBean1.setExamStartDateTime("2023-06-17 11:00:00");
		 regulerBean1.setScheduleId("12328382");
		 regulerBean1.setAssessmentName("Text mining and Natural Language Processing using Deep Learning - Slot 2 - 17th Jun 2023-Revised");
		 resitStudentList.add(regulerBean);
		 resitStudentList.add(regulerBean1);
		 try {
				when(ssoDao.getRegularLastExamAttemptdStudent()).thenReturn(examAttemptedregularStudentList);
				when(ssoDao.getResitStudentByExamStartTime()).thenReturn(resitStudentList);
				when(ssoDao.getRegularStudentByExamStartTime()).thenReturn(regularStudentList);
				when(teeSSOHelper.getFormattedDateAndTimeForEmail(regulerBean.getExamStartDateTime()))
						.thenReturn("Mon, Jul 17, 2023 19:00:00 IST").thenReturn("Mon, Jul 17, 2023 19:00:00 IST");
				when(teeSSOHelper.generateMettlExamLink(regulerBean)).thenReturn(
						"https://uat-studentzone-ngasce.nmims.edu/ltidemo/start_mettl_assessment?sapid=77122590244&timeboundId=1400&scheduleId=12450164https://uat-studentzone-ngasce.nmims.edu/ltidemo/start_mettl_assessment?sapid=77122692092&timeboundId=1817&scheduleId=12592082")
						.thenReturn(
								"https://uat-studentzone-ngasce.nmims.edu/ltidemo/start_mettl_assessment?sapid=77122692092&timeboundId=1817&scheduleId=12592082https://uat-studentzone-ngasce.nmims.edu/ltidemo/start_mettl_assessment?sapid=77122692092&timeboundId=1817&scheduleId=12592082");

		 } catch (Exception e1) {
			 // TODO Auto-generated catch block
			 //e1.printStackTrace();
		 }
		 boolean actualResult=false;
		 boolean expectedResult=true;
		 try {
			 service.sendExamJoinLinksForMbaWxStudents();
			 actualResult=true;
		 }
		 catch (Exception e) {
			 // e.printStackTrace();
			 actualResult=false;
		 }
		 
		 assertEquals(expectedResult , actualResult);
		 
	 }
	 
}
