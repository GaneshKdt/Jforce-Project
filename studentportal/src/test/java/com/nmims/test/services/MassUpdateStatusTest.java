package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.repository.ServiceRequestRepositoryImpl;
import com.nmims.services.MassUpdateSRStatusService;
import com.nmims.services.ServiceRequestService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MassUpdateStatusTest {
	
	@Autowired
	MassUpdateSRStatusService 	massUpdateSRStatusService ;

	
	@Autowired
	ServiceRequestService  serviceRequestService;
	
	@MockBean
	ServiceRequestDao serviceRequestDao;
	
	@Autowired
	ServiceRequestRepositoryImpl serviceRequestRepositoryImpl ;
	
	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	
	
	/**
	 * Test For Validation For Empty Params
	 * Request Status ,Cancellation Reason , Userid ,ServiceRequestId's
	 */
	@Test
	public void wrongRequestStatusParamTest() {
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage("Invalid Status selected");
		List<String> serviceRequestIdList = Arrays.asList("213665");
		String requestStatus = "Cancel";
		String cancellationReason = "Test 1 ";

		String userId = "Shivam";
		ServiceRequestStudentPortal serviceRequestStudentList = new ServiceRequestStudentPortal();
		serviceRequestStudentList.setId(213665L);
		serviceRequestStudentList.setServiceRequestType("Issuance of Marksheet");
		serviceRequestStudentList.setSapId("77220283294");
		serviceRequestStudentList.setRequestStatus("Closed");
		serviceRequestStudentList.setCancellationReason("Test Cancellation Reason");
		
		massUpdateSRStatusService.massUpdateSR(serviceRequestIdList, requestStatus, cancellationReason, userId);
	}
	
	@Test
	public void nullCancellationReasonParamTest() {
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage("Cancellation Reason not provided.");
		List<String> serviceRequestIdList = Arrays.asList("213665");
		String requestStatus = "Cancelled";
		String cancellationReason = null;

		String userId = "Shivam";
		ServiceRequestStudentPortal serviceRequestStudentList = new ServiceRequestStudentPortal();
		serviceRequestStudentList.setId(213665L);
		serviceRequestStudentList.setServiceRequestType("Issuance of Marksheet");
		serviceRequestStudentList.setSapId("77220283294");
		serviceRequestStudentList.setRequestStatus("Closed");
		serviceRequestStudentList.setCancellationReason("Test Cancellation Reason");
		
		massUpdateSRStatusService.massUpdateSR(serviceRequestIdList, requestStatus, cancellationReason, userId);
	}
	@Test
	public void serviceRequsestIdCancellationReasonParamTest() {
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage("No Service Request IDs provided!");
		List<String> serviceRequestIdList = null;//Arrays.asList("213665");
		String requestStatus = "Cancelled";
		String cancellationReason = "Test1";

		String userId = "Shivam";
		ServiceRequestStudentPortal serviceRequestStudentList = new ServiceRequestStudentPortal();
		serviceRequestStudentList.setId(213665L);
		serviceRequestStudentList.setServiceRequestType("Issuance of Marksheet");
		serviceRequestStudentList.setSapId("77220283294");
		serviceRequestStudentList.setRequestStatus("Closed");
		serviceRequestStudentList.setCancellationReason("Test Cancellation Reason");
		
		massUpdateSRStatusService.massUpdateSR(serviceRequestIdList, requestStatus, cancellationReason, userId);
	}
	@Test
	public void nullUserIdParamTest() {
		exceptionRule.expect(IllegalArgumentException.class);
		exceptionRule.expectMessage("Unable to detect User ID. Please try again!");
		List<String> serviceRequestIdList = Arrays.asList("213665");
		String requestStatus = "Cancelled";
		String cancellationReason = "Test Cancellation Reason ";
		String userId = null ;

		ServiceRequestStudentPortal serviceRequestStudentList = new ServiceRequestStudentPortal();
		serviceRequestStudentList.setId(213665L);
		serviceRequestStudentList.setServiceRequestType("Issuance of Marksheet");
		serviceRequestStudentList.setSapId("77220283294");
		serviceRequestStudentList.setRequestStatus("Closed");
		serviceRequestStudentList.setCancellationReason("Test Cancellation Reason");
		
		massUpdateSRStatusService.massUpdateSR(serviceRequestIdList, requestStatus, cancellationReason, userId);
	}
	/**
	 * End Test For Validation 
	 */
	
	/* ----------------------------------------------------------------------------------------------------------------------------------*/
	
	
	
	
	/**
	 * Start Test for Service Request Data Of Student 
	 */
	
	
	
	/**
	 * Test For Successfull data
	 */
	@Test
	public void serviceRequestStudentDataTest() {
		List<String> serviceRequestIdList = Arrays.asList("213665");
		String requestStatus = "Cancelled";
		String cancellationReason = "Test Cancellation Reason ";
		String userId = "nelson" ;
		/**
		SrAdminUpdateDto srAdminUpdateDto = new SrAdminUpdateDto();
		srAdminUpdateDto.setId(serviceRequ estId);
		srAdminUpdateDto.setServiceRequestType(serviceRequestType);
		srAdminUpdateDto.setSapid(sapId);
		srAdminUpdateDto.setRequestStatus(requestStatus);
		srAdminUpdateDto.setCancellationReason(cancellationReason);
		 */

		ServiceRequestStudentPortal serviceRequestStudentList = new ServiceRequestStudentPortal();
		serviceRequestStudentList.setId(213665L);
		serviceRequestStudentList.setServiceRequestType("Issuance of Marksheet");
		serviceRequestStudentList.setSapId("77220283294");
		serviceRequestStudentList.setRequestStatus("Closed");
		serviceRequestStudentList.setCancellationReason("Test Cancellation Reason");
		List<ServiceRequestStudentPortal> studentServiceRequestDetails = new ArrayList<ServiceRequestStudentPortal>(Arrays.asList(serviceRequestStudentList));
		System.out.println("studentServiceRequestDetails  :  "+studentServiceRequestDetails);
		when(serviceRequestRepositoryImpl.serviceRequestTypeSapidList(serviceRequestIdList)).thenReturn(studentServiceRequestDetails);
		when(serviceRequestDao.getServiceRequestTypeSapIdBySrIds(serviceRequestIdList)).thenReturn(studentServiceRequestDetails);
		Map<String,String> serviceRequestMap = massUpdateSRStatusService.massUpdateSR(serviceRequestIdList, requestStatus, cancellationReason, userId);
		if(!StringUtils.isEmpty(serviceRequestMap.get("errorMessage"))) 
			assertEquals(false, true);
		else
			assertEquals(true, true);
	
	
	}
	/**
	 * Test For Exception in Student Service Request Details 
	 * 
	 */
	
//	@Test
	public void errorServiceRequestStudentDataTest() {
		List<String> serviceRequestIdList = Arrays.asList("213665");
		String requestStatus = "Cancelled";
		String cancellationReason = "Test Cancellation Reason ";
		String userId = "nelson" ;
		/**
		SrAdminUpdateDto srAdminUpdateDto = new SrAdminUpdateDto();
		srAdminUpdateDto.setId(serviceRequ estId);
		srAdminUpdateDto.setServiceRequestType(serviceRequestType);
		srAdminUpdateDto.setSapid(sapId);
		srAdminUpdateDto.setRequestStatus(requestStatus);
		srAdminUpdateDto.setCancellationReason(cancellationReason);
		 */

		ServiceRequestStudentPortal serviceRequestStudentList = new ServiceRequestStudentPortal();
		serviceRequestStudentList.setId(213665L);
		serviceRequestStudentList.setServiceRequestType("Issuance of Marksheet");
		serviceRequestStudentList.setSapId("77220283294");
		serviceRequestStudentList.setRequestStatus("Closed");
		serviceRequestStudentList.setCancellationReason("Test Cancellation Reason");
		List<ServiceRequestStudentPortal> studentServiceRequestDetails = new ArrayList<ServiceRequestStudentPortal>();
//		System.out.println("studentServiceRequestDetails  :  "+studentServiceRequestDetails);
//		serviceRequestDao.getServiceRequestTypeSapIdBySrIds(serviceRequestIdList);
//		when(serviceRequestDao.getServiceRequestTypeSapIdBySrIds(serviceRequestIdList)).thenThrow(DataAccessException.class);
		when(serviceRequestRepositoryImpl.serviceRequestTypeSapidList(serviceRequestIdList)).thenReturn(studentServiceRequestDetails);
		Map<String,String> serviceRequestMap = massUpdateSRStatusService.massUpdateSR(serviceRequestIdList, requestStatus, cancellationReason, userId);
		System.out.println("serviceRequestMap.get(\"errorMessage\")  : "  +serviceRequestMap.get("errorMessage"));
		if("Invalid Service Request IDs: [213665]".equalsIgnoreCase(serviceRequestMap.get("errorMessage"))) 
			assertEquals(true, true);
		else
			assertEquals(false, true);
	
	
	}
//	@Test
//	public void 
}
