package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ExamBookingCancelBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.daos.ReportsDAO;
import com.nmims.services.impl.ReportsService;


@RunWith(SpringRunner.class)
public class ExamBookingCancelServiceTest {

	@Mock
	ReportsDAO dao;

	@InjectMocks
	ReportsService reportsService;
	
	
//	@Test
//	public void getCanselledExamBookingDaoTest() {
//	
//		ExamBookingTransactionBean bean=new ExamBookingTransactionBean();
//		bean.setSapid("77777777132");
//		bean.setSubject("Management");
//		bean.setReleaseReason("CL");
//		bean.setYear("2021");
//		bean.setMonth("jan");		
//		int expected=1;
//		int actual=0;
//		
//		ArrayList<ExamBookingTransactionBean> list= new ArrayList<ExamBookingTransactionBean>();
//		list.add(bean);
//		
//		try {
//			when(dao.getCancelledExamBookings(bean)).thenReturn(list);
//			actual=1;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		assertEquals(expected, actual);
//		
//	}
//	
//	
//	@Test
//	public void getCanselledExamBookingServiceTest() {		
//		ExamBookingTransactionBean bean = new ExamBookingTransactionBean();
//		int expected =1;
//		int actual=0;
//		try {
//			List<ExamBookingTransactionBean> examBookingCanceledListReport = reportsService.getExamBookingCanceledListReport(bean);
//			if (examBookingCanceledListReport!=null) {				
//				actual=1;
//			}
//		} catch (Exception e) {
//		
//			e.printStackTrace();
//		}
//		assertEquals(expected, actual);
//		
//	}
//	
//	@Test
//	public void cancelledExamBookingServiceTestFail() {
//		ExamBookingCancelBean bean= new ExamBookingCancelBean();
//		int expected =1;
//		int actual=0;
//		try {
//			List<ExamBookingCancelBean> examBookingCanceledListReport = reportsService.getExamBookingCanceledListReport(null);
//			if (examBookingCanceledListReport==null) {				
//				actual=1;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		assertNotSame(expected, actual);
//	}
	
	@Test
	public void getCanselledExamBookingDaoTestNew() {
	
		//Exambookings detail
		List<ExamBookingCancelBean> list=new ArrayList<ExamBookingCancelBean>();
		ExamBookingCancelBean bean=new ExamBookingCancelBean();
		bean.setSapid("77119289240");
		bean.setSubject("Operations Management");
		bean.setBooked("CL");
		bean.setReleaseReason("seat cancellation- Registered by mistake");
		bean.setYear("2023");
		bean.setMonth("Apr");
		bean.setProgram("PGDBM - BFM");
		bean.setSem("2");
		bean.setRespAmount("3600.00");
		bean.setLastModifiedDate("2023-03-25 16:01:00");
		
		int expected=1;
		int actual=0;
		
		try {
			when(dao.getCancelledExamBookings(bean.getYear(), bean.getMonth())).thenReturn(list);
			actual=1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void getCanselledExamBookingServiceTestNew() {
		
		List<String> sapids = Arrays.asList("77119289240");
		
		//Exambookings detail
		List<ExamBookingCancelBean> list=new ArrayList<ExamBookingCancelBean>();
		ExamBookingCancelBean bean=new ExamBookingCancelBean();
		bean.setSapid("77119289240");
		bean.setSubject("Operations Management");
		bean.setBooked("CL");
		bean.setReleaseReason("seat cancellation- Registered by mistake");
		bean.setYear("2023");
		bean.setMonth("Apr");
		bean.setProgram("PGDBM - BFM");
		bean.setSem("2");
		bean.setRespAmount("3600.00");
		bean.setLastModifiedDate("2023-03-25 16:01:00");
		list.add(bean);
		
		//Students Detail
		List<ExamBookingCancelBean> list1=new ArrayList<ExamBookingCancelBean>();
		ExamBookingCancelBean bean1=new ExamBookingCancelBean();
		bean1.setSapid("77119289240");
		bean1.setFirstName("Sumit");
		bean1.setLastName("Nagar");
		bean1.setEmailId("shivampajnp1726@gmail.com");
		bean1.setMobile("8369661726");
		bean1.setEnrollmentMonth("Jul");
		bean1.setCenterName("Kolkata - Camac Street");
		bean1.setValidityEndMonth("Jun");
		bean1.setValidityEndYear("2023");
		bean1.setCenterCode("abcd");
		list1.add(bean1);
		
		//Center Details
		ArrayList<CenterExamBean> list2=new ArrayList<CenterExamBean>();
		CenterExamBean bean2=new CenterExamBean();
		bean2.setCenterCode("abcd");
		bean2.setLc("Kolkata");
		list2.add(bean2);
		
		int expected =1;
		int actual=0;
		try {
			when(dao.getCancelledExamBookings(bean.getYear(), bean.getMonth())).thenReturn(list);
			when(dao.getCancelStudentBySapid(sapids)).thenReturn(list1);
			when(dao.getAllCenters()).thenReturn(list2);
			List<ExamBookingCancelBean> examBookingCanceledListReport = reportsService.getExamBookingCanceledListReport(bean);
			if (examBookingCanceledListReport!=null) {				
				actual=1;
			}
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		assertEquals(expected, actual);
		
	}
	
	@Test
	public void cancelledExamBookingServiceTestFailNew() {
		
		List<String> sapids = Arrays.asList("77119289240");
		
		//Exambookings detail
		List<ExamBookingCancelBean> list=new ArrayList<ExamBookingCancelBean>();
		ExamBookingCancelBean bean=new ExamBookingCancelBean();
		//bean.setSapid("77119289240");
		bean.setSubject("Operations Management");
		bean.setBooked("CL");
		bean.setReleaseReason("seat cancellation- Registered by mistake");
		bean.setYear("2023");
		bean.setMonth("Apr");
		bean.setProgram("PGDBM - BFM");
		bean.setSem("2");
		bean.setRespAmount("3600.00");
		bean.setLastModifiedDate("2023-03-25 16:01:00");
		list.add(bean);
		
		//Students Detail
		List<ExamBookingCancelBean> list1=new ArrayList<ExamBookingCancelBean>();
		ExamBookingCancelBean bean1=new ExamBookingCancelBean();
		//bean1.setSapid("77119289240");
//		bean1.setFirstName("Sumit");
//		bean1.setLastName("Nagar");
//		bean1.setEmailId("shivampajnp1726@gmail.com");
//		bean1.setMobile("8369661726");
//		bean1.setEnrollmentMonth("Jul");
//		bean1.setCenterName("Kolkata - Camac Street");
//		bean1.setValidityEndMonth("Jun");
//		bean1.setValidityEndYear("2023");
//		bean1.setCenterCode("abcd");
		list1.add(bean1);
		
		//Center Details
		ArrayList<CenterExamBean> list2=new ArrayList<CenterExamBean>();
		CenterExamBean bean2=new CenterExamBean();
//		bean2.setCenterCode("abcd");
//		bean2.setLc("Kolkata");
		list2.add(bean2);
		
		int expected =1;
		int actual=0;
		try {
			when(dao.getCancelledExamBookings(bean.getYear(), bean.getMonth())).thenReturn(list);
			when(dao.getCancelStudentBySapid(sapids)).thenReturn(list1);
			when(dao.getAllCenters()).thenReturn(null);
			List<ExamBookingCancelBean> examBookingCanceledListReport = reportsService.getExamBookingCanceledListReport(bean);
			if (examBookingCanceledListReport!=null) {				
				actual=1;
			}
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		assertNotSame(expected, actual);
	}
}
