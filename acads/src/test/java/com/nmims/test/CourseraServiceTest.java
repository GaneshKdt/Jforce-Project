package com.nmims.test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.CourseraMappingBean;
import com.nmims.daos.CourseraDao;

@RunWith(SpringRunner.class)
public class CourseraServiceTest {
	
	@Mock
	CourseraDao cDAo;

	@Test
	public void getLearnerURL() {
		String consumerProgramStructureId="138";
		String expecteString="https://www.coursera.org/programs/ngasce-mba-marketing-management-glslk?attemptSSOLogin=true";
		when(cDAo.getLeanersURLbyMasterKey(consumerProgramStructureId)).thenReturn("https://www.coursera.org/programs/ngasce-mba-marketing-management-glslk?attemptSSOLogin=true");
		String learnerURL=cDAo.getLeanersURLbyMasterKey(consumerProgramStructureId);
		assertEquals(expecteString, learnerURL);
	}
	
	@Test
	public void getLearnerURL2() {
		String consumerProgramStructureId="111";
		String expecteString="";
		when(cDAo.getLeanersURLbyMasterKey(consumerProgramStructureId)).thenReturn("");
		String learnerURL=cDAo.getLeanersURLbyMasterKey(consumerProgramStructureId);
		assertEquals(expecteString, learnerURL);
	}
	
	@Test
	public void checkStudentPaidForCourseraUnpaid() {
		String sapId="77221914866";
		CourseraMappingBean bean1=new CourseraMappingBean();
		when(cDAo.checkStudentOptedForCoursera(sapId)).thenReturn(bean1);
		CourseraMappingBean bean=cDAo.checkStudentOptedForCoursera(sapId);
		assertEquals(bean1, bean);
	}
	
	@Test
	public void checkStudentPaidForCourseraPaid() {
		String sapId="77221914866";
		CourseraMappingBean bean1=new CourseraMappingBean();
		bean1.setCount(1);
		bean1.setExpiryDate(LocalDateTime.now());
		when(cDAo.checkStudentOptedForCoursera(sapId)).thenReturn(bean1);
		CourseraMappingBean bean=cDAo.checkStudentOptedForCoursera(sapId);
		assertEquals(bean1, bean);
	}

}
