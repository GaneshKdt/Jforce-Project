package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import com.nmims.beans.UFMNoticeBean;
import com.nmims.daos.UFMNoticeDAO;
import com.nmims.services.UFMNoticeService;
import com.nmims.stratergies.impl.UFMActionStrategy;
import com.nmims.stratergies.impl.UFMDocumentGeneratorStrategy;
import com.nmims.stratergies.impl.UFMShowCauseStrategy;

@RunWith(SpringRunner.class)
public class UFMNoticeCOCServiceTest 
{
	@Mock
	UFMNoticeDAO ufmNoticeDAO;
	
	@InjectMocks
	UFMShowCauseStrategy ufmShowCauseStrategy;
	
	@InjectMocks
	UFMDocumentGeneratorStrategy ufmDocumentGeneratorStrategy;
	
	@InjectMocks
	UFMActionStrategy ufmActionStrategy;
	
	@InjectMocks
	UFMNoticeService ufmNoticeService;
	
	@Test
	public void performUploadCOCNoticeFiles() {
		
		UFMNoticeBean bean = new UFMNoticeBean();
		List<UFMNoticeBean> successList = new ArrayList<>();
		List<UFMNoticeBean> successListDocuments = new ArrayList<>();
		List<UFMNoticeBean> errorList = new ArrayList<>();
		
		bean.setSapid("77120231917");
		bean.setSubject("Business Communication");
		bean.setExamDate("30-12-2022");
		bean.setExamTime("13:00:00");
		bean.setUfmMarkReason("Discussing answers during the exam in whatsapp group for all subjects attempted in June 2022 Exam");
		bean.setShowCauseDeadline("2023-01-15 12:59:00");
		bean.setYear("2022");
		bean.setMonth("Dec");
		bean.setCategory("COC");
		bean.setLastModifiedBy("Shivam Pandey");
		bean.setCreatedBy("Shivam Pandey");
		bean.setCreatedDate("2023-01-16 16:53:30");
		bean.setLastModifiedDate("2023-01-16 16:53:30");
		bean.setStage("Show Cause - Awaiting Of Student Response");
		bean.setShowCauseGenerationDate("2023-01-16 16:53:30");
		bean.setShowCauseNoticeURL("Dec2020/77115001076_609285.pdf");
		bean.setShowCauseResponse("Testing!!");
		bean.setShowCauseSubmissionDate("2023-01-16 16:53:30");
		bean.setDecisionNoticeURL("Jun2022/77117940393_264528.pdf");
		
		String expected="Success";
		String actual="";
			
		ufmNoticeService.performUploadCOCNoticeFiles(bean, successList, successListDocuments, errorList);
		
		if(errorList.isEmpty())
		{
			actual="Success";
		}
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void performUploadCOCActionFiles() {
		
		UFMNoticeBean bean = new UFMNoticeBean();
		List<UFMNoticeBean> successList = new ArrayList<>();
		List<UFMNoticeBean> successListDocuments = new ArrayList<>();
		List<UFMNoticeBean> errorList = new ArrayList<>();
		
		bean.setSapid("77120231917");
		bean.setSubject("Business Communication");
		bean.setExamDate("30-12-2022");
		bean.setExamTime("13:00:00");
		bean.setUfmMarkReason("Discussing answers during the exam in whatsapp group for all subjects attempted in June 2022 Exam");
		bean.setShowCauseDeadline("2023-01-15 12:59:00");
		bean.setYear("2022");
		bean.setMonth("Dec");
		bean.setCategory("COC");
		bean.setLastModifiedBy("Shivam Pandey");
		bean.setCreatedBy("Shivam Pandey");
		bean.setCreatedDate("2023-01-16 16:53:30");
		bean.setLastModifiedDate("2023-01-16 16:53:30");
		bean.setStage("Show Cause - Awaiting Of Student Response");
		bean.setShowCauseGenerationDate("2023-01-16 16:53:30");
		bean.setShowCauseNoticeURL("Dec2020/77115001076_609285.pdf");
		bean.setShowCauseResponse("Testing!!");
		bean.setShowCauseSubmissionDate("2023-01-16 16:53:30");
		bean.setDecisionNoticeURL("Jun2022/77117940393_264528.pdf");
		
		String expected="Success";
		String actual="";
			
		ufmNoticeService.performUploadCOCActionFiles(bean, successList, successListDocuments, errorList);
		
		if(errorList.isEmpty())
		{
			actual="Success";
		}
		
		assertEquals(expected, actual);
	}
}
