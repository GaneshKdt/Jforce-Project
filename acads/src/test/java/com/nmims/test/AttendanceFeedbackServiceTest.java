package com.nmims.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.PageAcads;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.daos.AttendanceFeedbackDAO;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AttendanceFeedbackServiceTest {
	
	@Autowired
	AttendanceFeedbackDAO attendanceFeedbackDAO;
	
	private String CURRENT_ACAD_YEAR="2021";
	private String CURRENT_ACAD_MONTH="Jul";
	private String userAuthorisedCode="a029000000rXz4k,a029000000Ebpis, a029000000Ebpir";
	
	private ArrayList<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList("BBA", "B.Com", "PD - WM","PD - DM",
			"M.Sc. (App. Fin.)", "CP-WL", "BBA-BA"));

	@Test
	public void getAttendanceDetails() {
		SessionAttendanceFeedbackAcads searchBean=new SessionAttendanceFeedbackAcads();
		String centerCode=null;
		searchBean.setMonth("Jul");
		searchBean.setYear("2021");
		searchBean.setHasModuleId("N");
		searchBean.setSubject("Organisational Behaviour");
		searchBean.setFacultyFullName("Alpha Lokhande");
		
		PageAcads<SessionAttendanceFeedbackAcads> page=new PageAcads<>();
		if ((searchBean.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR) && searchBean.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH))
				|| (searchBean.getYear().equalsIgnoreCase("2021") && (searchBean.getMonth().equalsIgnoreCase("Jan") || searchBean.getMonth().equalsIgnoreCase("Apr")))
				|| (searchBean.getYear().equalsIgnoreCase("2020") && (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Oct")))) {
			page = attendanceFeedbackDAO.getAttendance(1, Integer.MAX_VALUE, searchBean, userAuthorisedCode);
		}else {
			page = attendanceFeedbackDAO.getAttendanceFromHistory(1, Integer.MAX_VALUE, searchBean, centerCode);
		}
		
	}
	
	@Test
	public void getSubjectFacultyWiseAverage(){
		SessionAttendanceFeedbackAcads  searchBean=new SessionAttendanceFeedbackAcads();
		List<SessionAttendanceFeedbackAcads>	facultyFeedbackList=attendanceFeedbackDAO.getSubjectFacultyWiseAverage(searchBean);
	}
	
	@Test
	public void getListOfSubjectFacultyWiseFeedbackAverage(HttpServletRequest request) {
		SessionAttendanceFeedbackAcads  searchBean=new SessionAttendanceFeedbackAcads();
		List<SessionAttendanceFeedbackAcads> getSubjectFacultyWiseAverageList = new ArrayList<SessionAttendanceFeedbackAcads>();

		if ((searchBean.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR)
				&& searchBean.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH))
				|| (searchBean.getYear().equalsIgnoreCase("2021") && (searchBean.getMonth().equalsIgnoreCase("Jan")
						|| searchBean.getMonth().equalsIgnoreCase("Apr")))
				|| (searchBean.getYear().equalsIgnoreCase("2020") && (searchBean.getMonth().equalsIgnoreCase("Jul")
						|| searchBean.getMonth().equalsIgnoreCase("Oct")))) {
			getSubjectFacultyWiseAverageList = attendanceFeedbackDAO.getSubjectFacultyWiseAverage(searchBean);

		} else {
			getSubjectFacultyWiseAverageList = attendanceFeedbackDAO
					.getSubjectFacultyWiseAverageFromHistory(searchBean);
		}

	}
	
	@Test
	public void getMapOfSubjectFacultySessionWiseAverage(SessionAttendanceFeedbackAcads searchBean, String CURRENT_ACAD_YEAR, String CURRENT_ACAD_MONTH, HttpServletRequest request){
		HashMap<String, SessionAttendanceFeedbackAcads> mapOfSubjectFacultySessionWiseAverage = new HashMap<String, SessionAttendanceFeedbackAcads>();
		
		if ((searchBean.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR) && searchBean.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH))
				|| (searchBean.getYear().equalsIgnoreCase("2021") && (searchBean.getMonth().equalsIgnoreCase("Jan") || searchBean.getMonth().equalsIgnoreCase("Apr")))
				|| (searchBean.getYear().equalsIgnoreCase("2020") && (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Oct")))) {
			mapOfSubjectFacultySessionWiseAverage = attendanceFeedbackDAO.getMapOfSubjectFacultySessionWiseAverage(searchBean,request);
		}else {
			
			mapOfSubjectFacultySessionWiseAverage = attendanceFeedbackDAO.getMapOfSubjectFacultySessionWiseAverageFromHistory(searchBean, request);
		}
		
	}
	
	@Test
	public void getMapOfSessionIdAndFeedBackBean(SessionAttendanceFeedbackAcads searchBean, String CURRENT_ACAD_YEAR, String CURRENT_ACAD_MONTH, HttpServletRequest request){
		HashMap<String, SessionAttendanceFeedbackAcads> mapOfSessionIdAndFeedBackBean=new HashMap<String, SessionAttendanceFeedbackAcads>();
		if ((searchBean.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR) && searchBean.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH))
				|| (searchBean.getYear().equalsIgnoreCase("2021") && (searchBean.getMonth().equalsIgnoreCase("Jan") || searchBean.getMonth().equalsIgnoreCase("Apr")))
				|| (searchBean.getYear().equalsIgnoreCase("2020") && (searchBean.getMonth().equalsIgnoreCase("Jul") || searchBean.getMonth().equalsIgnoreCase("Oct")))) {
			mapOfSessionIdAndFeedBackBean = attendanceFeedbackDAO.getMapOfSessionIdAndFeedBackBean();
		}else {
			
			mapOfSessionIdAndFeedBackBean = attendanceFeedbackDAO.getMapOfSessionIdAndFeedBackBean();
		}
	}
}
