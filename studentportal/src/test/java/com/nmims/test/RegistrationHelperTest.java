package com.nmims.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.PortalDao;
import com.nmims.helpers.RegistrationHelper;

public class RegistrationHelperTest extends StudentPortalTests{

	@Autowired
	ApplicationContext act;
	
	@Autowired
	RegistrationHelper rh;
	
	
	
	@Before
	public void setup(){
	    MockitoAnnotations.initMocks(this); //without this you will get NPE
	}
	
	//This test scenario will test if helper method is returning registration object or null?
	@Test
	public void testNull() {
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
	    String expected="True";
	    String actual="";
		ArrayList<StudentMarksBean> allStudentRegistrations = pDao.getAllRegistrationsFromSAPID("77120103300");
		HashMap<String, StudentMarksBean> monthYearAndStudentRegistrationMap = new HashMap<>();
		List<ExamOrderStudentPortalBean> liveFlagList = pDao.getLiveFlagDetails();
		for (StudentMarksBean bean : allStudentRegistrations) {
			monthYearAndStudentRegistrationMap.put(bean.getMonth() + "-" + bean.getYear(), bean);
		}
		
		try
		{
		StudentMarksBean sr=rh.CheckStudentRegistrationForCourses(monthYearAndStudentRegistrationMap, 27.00, 27.00, 27.00, liveFlagList);
		
		if(sr!=null)
		{
		actual="True";
		}
		else
		{
			actual="False";
		}
		}
		catch(Exception e) {
			actual="False";
			
		}
		assertEquals(expected, actual);
	}

}

	
