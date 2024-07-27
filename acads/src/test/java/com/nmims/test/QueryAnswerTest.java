package com.nmims.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.QueryAnswerListBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.services.QueryAnswerService;
import com.nmims.util.ContentUtil;

@RunWith(SpringRunner.class)
public class QueryAnswerTest{
	
	@Mock
	SessionQueryAnswerDAO sessionQueryAnswerDAO;
	
	@InjectMocks
	QueryAnswerService qnaService;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this); //without this you will get NPE
	}
	
	//After July2021 but not paid
	@Test
	public void getAssignedqueriesForFacultyTestJul2021Enrollment() {
		
		ArrayList<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList("BBA", "B.Com", "PD - WM","PD - DM",
					"M.Sc. (App. Fin.)", "CP-WL", "BBA-BA"));
		String facultyId="NGASCE0411";
		//List<SessionQueryAnswer> allQueries = sessionQueryAnswerDAO.getAllCourseQueriresByFaculty(facultyId);
		List<SessionQueryAnswer> allQueries = new ArrayList<SessionQueryAnswer>();
		SessionQueryAnswer sessionQueryAnswer=new SessionQueryAnswer();
		sessionQueryAnswer.setId("134");
		sessionQueryAnswer.setSapId("77121374502");
		sessionQueryAnswer.setProgramSemSubjectId("2115");
		sessionQueryAnswer.setHasTimeBoundId("N");
		allQueries.add(sessionQueryAnswer);
		String expected="N";
		String actual="";
		HashMap<String, StudentAcadsBean> mapOfStudentAcadsBean = new HashMap<String, StudentAcadsBean>();
		StudentAcadsBean studentBean1=new StudentAcadsBean();
		studentBean1.setSapid("77121374502");
		studentBean1.setProgram("MBA (HRM)");
		studentBean1.setEnrollmentMonth("Jul");
		studentBean1.setEnrollmentMonth("2021");
		mapOfStudentAcadsBean.put("77121374502", studentBean1);
		
		if ("N".equals(sessionQueryAnswer.getHasTimeBoundId())) {
			if (sessionQueryAnswer.getSapId() != null) {
				StudentAcadsBean studentBean = new StudentAcadsBean();
				if (mapOfStudentAcadsBean.containsKey(sessionQueryAnswer.getSapId())) {
					studentBean = mapOfStudentAcadsBean.get(sessionQueryAnswer.getSapId());
				} else {
					when(sessionQueryAnswerDAO.getStudentDataBySapId(sessionQueryAnswer.getSapId())).thenReturn(studentBean1);
					studentBean = sessionQueryAnswerDAO.getStudentDataBySapId(sessionQueryAnswer.getSapId());
					mapOfStudentAcadsBean.put(sessionQueryAnswer.getSapId(), studentBean);
				}

				if (!nonPG_ProgramList.contains(studentBean.getProgram())) {
					
					String enrollement="2021-07-01";
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date date;
						try {
						date = formatter.parse(enrollement);
						Date dateCompare = formatter.parse("2021-07-01");

						if (date.compareTo(dateCompare) >= 0) {
							when(sessionQueryAnswerDAO.checkForPaidStudentOnsapIdAndPssId(sessionQueryAnswer.getSapId(), sessionQueryAnswer.getProgramSemSubjectId())).thenReturn(false);
							// student after Jul2021 enrollment
							boolean check = sessionQueryAnswerDAO.checkForPaidStudentOnsapIdAndPssId(sessionQueryAnswer.getSapId(), sessionQueryAnswer.getProgramSemSubjectId());
							if (check) {
								sessionQueryAnswer.setIsLiveAccess("Y");
							} else {
								sessionQueryAnswer.setIsLiveAccess("N");
							}
						}
						actual=sessionQueryAnswer.getIsLiveAccess();
						}catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
				}
			}
		}
		assertEquals(expected, actual);
	}
	
	//Before July2021
	@Test
	public void getAssignedqueriesForFacultyTestBeforeJul2021() {
		
		ArrayList<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList("BBA", "B.Com", "PD - WM","PD - DM",
					"M.Sc. (App. Fin.)", "CP-WL", "BBA-BA"));
		
		List<SessionQueryAnswer> allQueries = new ArrayList<SessionQueryAnswer>();
		SessionQueryAnswer sessionQueryAnswer=new SessionQueryAnswer();
		sessionQueryAnswer.setId("134");
		sessionQueryAnswer.setSapId("77121374502");
		sessionQueryAnswer.setProgramSemSubjectId("2115");
		sessionQueryAnswer.setHasTimeBoundId("N");
		allQueries.add(sessionQueryAnswer);
		String expected=null;
		String actual=null;
		HashMap<String, StudentAcadsBean> mapOfStudentAcadsBean = new HashMap<String, StudentAcadsBean>();
		StudentAcadsBean studentBean1=new StudentAcadsBean();
		studentBean1.setSapid("77121374502");
		studentBean1.setProgram("MBA (HRM)");
		studentBean1.setEnrollmentMonth("Jan");
		studentBean1.setEnrollmentMonth("2021");
		mapOfStudentAcadsBean.put("77121374502", studentBean1);
		
		if ("N".equals(sessionQueryAnswer.getHasTimeBoundId())) {
			if (sessionQueryAnswer.getSapId() != null) {
				StudentAcadsBean studentBean = new StudentAcadsBean();
				if (mapOfStudentAcadsBean.containsKey(sessionQueryAnswer.getSapId())) {
					studentBean = mapOfStudentAcadsBean.get(sessionQueryAnswer.getSapId());
				} else {
					when(sessionQueryAnswerDAO.getStudentDataBySapId(sessionQueryAnswer.getSapId())).thenReturn(studentBean1);
					studentBean = sessionQueryAnswerDAO.getStudentDataBySapId(sessionQueryAnswer.getSapId());
					mapOfStudentAcadsBean.put(sessionQueryAnswer.getSapId(), studentBean);
				}

				if (!nonPG_ProgramList.contains(studentBean.getProgram())) {
					String enrollement="2021-01-01";
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date date;
						try {
						date = formatter.parse(enrollement);
						Date dateCompare = formatter.parse("2021-07-01");

						if (date.compareTo(dateCompare) >= 0) {
							boolean check = false;
							if (check) {
								sessionQueryAnswer.setIsLiveAccess("Y");
							} else {
								sessionQueryAnswer.setIsLiveAccess("N");
							}
						}
						actual=sessionQueryAnswer.getIsLiveAccess();
						}catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
				}
			}
		}
		assertEquals(expected, actual);
	}
	
	//After July2021 and paid for live sessions
	@Test
	public void getAssignedqueriesForFacultyTestAfterJuly2021() {

		ArrayList<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList("BBA", "B.Com", "PD - WM","PD - DM",
					"M.Sc. (App. Fin.)", "CP-WL", "BBA-BA"));
		
		List<SessionQueryAnswer> allQueries = new ArrayList<SessionQueryAnswer>();
		SessionQueryAnswer sessionQueryAnswer=new SessionQueryAnswer();
		sessionQueryAnswer.setId("134");
		sessionQueryAnswer.setSapId("77121374502");
		sessionQueryAnswer.setProgramSemSubjectId("2115");
		sessionQueryAnswer.setHasTimeBoundId("N");
		allQueries.add(sessionQueryAnswer);
		String expected="Y";
		String actual=null;
		HashMap<String, StudentAcadsBean> mapOfStudentAcadsBean = new HashMap<String, StudentAcadsBean>();
		StudentAcadsBean studentBean1=new StudentAcadsBean();
		studentBean1.setSapid("77121374502");
		studentBean1.setProgram("MBA (HRM)");
		studentBean1.setEnrollmentMonth("Jan");
		studentBean1.setEnrollmentMonth("2022");
		mapOfStudentAcadsBean.put("77121374502", studentBean1);
		
		if ("N".equals(sessionQueryAnswer.getHasTimeBoundId())) {
			if (sessionQueryAnswer.getSapId() != null) {
				StudentAcadsBean studentBean = new StudentAcadsBean();
				if (mapOfStudentAcadsBean.containsKey(sessionQueryAnswer.getSapId())) {
					studentBean = mapOfStudentAcadsBean.get(sessionQueryAnswer.getSapId());
				} else {
					when(sessionQueryAnswerDAO.getStudentDataBySapId(sessionQueryAnswer.getSapId())).thenReturn(studentBean1);
					studentBean = sessionQueryAnswerDAO.getStudentDataBySapId(sessionQueryAnswer.getSapId());
					mapOfStudentAcadsBean.put(sessionQueryAnswer.getSapId(), studentBean);
				}

				if (!nonPG_ProgramList.contains(studentBean.getProgram())) {
					String enrollement="2022-07-01";
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						Date date;
						try {
						date = formatter.parse(enrollement);
						Date dateCompare = formatter.parse("2021-07-01");

						if (date.compareTo(dateCompare) >= 0) {
							when(sessionQueryAnswerDAO.checkForPaidStudentOnsapIdAndPssId(sessionQueryAnswer.getSapId(), sessionQueryAnswer.getProgramSemSubjectId())).thenReturn(true);
							boolean check = sessionQueryAnswerDAO.checkForPaidStudentOnsapIdAndPssId(sessionQueryAnswer.getSapId(), sessionQueryAnswer.getProgramSemSubjectId());
							if (check) {
								sessionQueryAnswer.setIsLiveAccess("Y");
							} else {
								sessionQueryAnswer.setIsLiveAccess("N");
							}
						}
						actual=sessionQueryAnswer.getIsLiveAccess();
						}catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
				}
			}
		}
		assertEquals(expected, actual);
	}
	
	@Test
	public void Test1() {
		String facultyId="";
		String expected="Y";
		List<SessionQueryAnswer> allQueries=new ArrayList<SessionQueryAnswer>();
		QueryAnswerListBean allListsBean=new QueryAnswerListBean();
		SessionQueryAnswer sessionQueryAnswer=new SessionQueryAnswer();
		sessionQueryAnswer.setId("134");
		sessionQueryAnswer.setSapId("77121374502");
		sessionQueryAnswer.setProgramSemSubjectId("2115");
		sessionQueryAnswer.setHasTimeBoundId("N");
		sessionQueryAnswer.setIsAnswered("N");
		sessionQueryAnswer.setIsLiveAccess("Y");
		allQueries.add(sessionQueryAnswer);
		
		List<SessionQueryAnswer> answeredQueries = new ArrayList<SessionQueryAnswer>();
		answeredQueries.add(sessionQueryAnswer);
		allListsBean.setAnsweredQueries(answeredQueries);
		
		HashMap<String, StudentAcadsBean> mapOfStudentAcadsBean = new HashMap<String, StudentAcadsBean>();
		StudentAcadsBean studentBean1=new StudentAcadsBean();
		studentBean1.setSapid("77121374502");
		studentBean1.setProgram("MBA (HRM)");
		studentBean1.setEnrollmentMonth("Jan");
		studentBean1.setEnrollmentMonth("2022");
		mapOfStudentAcadsBean.put("77121374502", studentBean1);
		when(sessionQueryAnswerDAO.getStudentDataBySapId(sessionQueryAnswer.getSapId())).thenReturn(studentBean1);
		
		when(sessionQueryAnswerDAO.checkForPaidStudentOnsapIdAndPssId(sessionQueryAnswer.getSapId(), sessionQueryAnswer.getProgramSemSubjectId())).thenReturn(true);
		
		ArrayList<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList("BBA", "B.Com", "PD - WM","PD - DM",
				"M.Sc. (App. Fin.)", "CP-WL", "BBA-BA"));
		
		when(sessionQueryAnswerDAO.getAllCourseQueriresByFaculty(facultyId)).thenReturn(allQueries);
		
		try {
		when(qnaService.getAssignedqueriesForFaculty(nonPG_ProgramList, facultyId)).thenReturn(allListsBean);
		allListsBean=qnaService.getAssignedqueriesForFaculty(nonPG_ProgramList, facultyId);
		}catch (Exception e) {
			// TODO: handle exception
		}
		String actual=allListsBean.getAnsweredQueries().get(0).getIsLiveAccess();
		assertEquals(expected, actual);
	}
}
