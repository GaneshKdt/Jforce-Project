package com.nmims.test.helpers;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.helpers.CreatePDF;

public class CreatePDFTest {
	

	CreatePDF services=new CreatePDF();
	 ServiceRequestDao SRD=new  ServiceRequestDao();
	 
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	} 

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTest()  {
		
	}

	@Test
	
	public void testCreatePDF() throws Exception {
		
		
	}

	@Test
	public void testGenerateApprovalFied() throws Exception {
	
	
		
	}

	@Test
	public void testGenerateDeclaration() throws Exception{
		
	}

	@Test
	public void testGenerateWithdrawalReasonTable() throws Exception {
		
	}

	@Test
	public void testGenerateStudentInfoTable() throws Exception {
		
	}

	@Test
	public void testGenerateHeaderTable() throws Exception {
		
	}

	@Test
	public void testOptionsTableRow() throws Exception {
		
	}

	@Test

	public void testCreateSrFeeReceipt() throws Exception {
		String FEE_RECEIPT_PATH="C:/FeeReceipts/";
		StudentStudentPortalBean student = new StudentStudentPortalBean();
		student.setSapid("77117816462");  
		student.setFirstName("Maulik");
		student.setLastName("Sheth");
		student.setProgram("PGDSCM");
		
		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal(); 
		sr.setServiceRequestType("Issuance of Marksheet");
		sr.setSapId("77117816462");
		sr.setAmount("1100"); 
		String courierAmount = "100"; 
		sr.setMonth("Dec");
		sr.setYear("2019");
		ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = new ArrayList<ServiceRequestStudentPortal>();
		
		ServiceRequestStudentPortal bean = new ServiceRequestStudentPortal();
		bean.setDescriptionToBeShownInMarksheetSummary("Issuance of Marksheet for student " + 
				"77117816462 for Exam Jun-2018 & Sem : 2 ");
		bean.setMonth("Jun");
		bean.setYear("2018");
		bean.setSem("2");
		bean.setAmount("500");
		long id = 61823;
		bean.setId( id );
		bean.setSapId("77117816462");  
		marksheetDetailAndAmountToBePaidList.add(bean);
		
		bean = new ServiceRequestStudentPortal();
		bean.setDescriptionToBeShownInMarksheetSummary("Issuance of Marksheet for student " + 
				"77117816462 for Exam Dec-2019 & Sem : 4 ");
		bean.setMonth("Dec");
		bean.setYear("2019");
		bean.setSem("4");
		bean.setAmount("500");
		id = 61824;
		bean.setId( id );
		bean.setSapId("77117816462");
		bean.setTranStatus("Payment Successful");
		bean.setAmountToBeDisplayedForMarksheetSummary("500");
		marksheetDetailAndAmountToBePaidList.add(bean);
		
		 /*String fileName = sr.getSapId()+"_SR_"+ sr.getMonth() + sr.getYear() +  ".pdf";
		String year = "2019";
		String month = "Dec";
		String sapid="77117816462";
		String doucmentType="pdf";
		
		int receiptNo=SRD.insertDocumentRecordAndReturnReceiptNo( FEE_RECEIPT_PATH +  " " + fileName , year, month,  sapid , doucmentType );
		System.out.println(receiptNo); */
		
//		
//		assertEquals("Valid Id No.", 61823, 61823);
//		
//		assertEquals("Valid ID NO.", id, 61824);
//		
//	    assertNotNull("Object Should Not be Null", bean); 
//	    
//	    assertEquals("Invalid ID No.", id, 61827);

	}

}
