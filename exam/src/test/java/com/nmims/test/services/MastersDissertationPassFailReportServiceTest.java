package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nmims.beans.DissertationResultDTO;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.Q7Q8DissertationResultBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.dto.TEEResultDTO;
import com.nmims.services.impl.EmbaPassFailReportServiceImpl;



@RunWith(SpringRunner.class)
@SpringBootTest
public class MastersDissertationPassFailReportServiceTest {

	
	@Mock
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	
	@InjectMocks
	EmbaPassFailReportServiceImpl service;
	
	
	 @Test 
	 public void  MastersDissertationPassFialReportTest() {
		  MockitoAnnotations.initMocks(this);
		
		  TEEResultDTO resultBean=new TEEResultDTO();
		  resultBean.setExamYear("2023");
		  resultBean.setExamMonth("Apr");
		 // resultBean.set
		  
		  ArrayList<ProgramExamBean> pssList=new ArrayList<ProgramExamBean>();
		  
		  ProgramExamBean pss1=new ProgramExamBean();
		  pss1.setId("1990");
		  pss1.setName("Masters Dissertation Part - I");
		  pss1.setConsumerProgramStructureId("131");
		  
		  ProgramExamBean pss2=new ProgramExamBean();
		  pss2.setId("1991");
		  pss2.setName("Masters Dissertation Part - II");
		  pss2.setConsumerProgramStructureId("131");
		  
		  ProgramExamBean pss3=new ProgramExamBean();
		  pss3.setId("2787");
		  pss3.setName("Masters Dissertation Part - I");
		  pss3.setConsumerProgramStructureId("158");
		  ProgramExamBean pss4=new ProgramExamBean();
		  pss4.setId("2788");
		  pss4.setName("Masters Dissertation Part - II");
		  pss4.setConsumerProgramStructureId("158");
		  
		 pssList.add(pss1);
		 pssList.add(pss2);
		 pssList.add(pss3);
		 pssList.add(pss4);
		 
		 List<String> userInputmasterKeyList=new ArrayList<String>();
		 userInputmasterKeyList.add("131");
		 userInputmasterKeyList.add("158");
		 
		 ArrayList<StudentSubjectConfigExamBean> timeboundIdList=new ArrayList<StudentSubjectConfigExamBean>();
		 StudentSubjectConfigExamBean studentSubjectConfigBean=new StudentSubjectConfigExamBean();
		 studentSubjectConfigBean.setId("1568");
		 studentSubjectConfigBean.setPrgm_sem_subj_id("1990");
		 studentSubjectConfigBean.setExamMonth("Apr");
		 studentSubjectConfigBean.setExamYear("2023");
		 studentSubjectConfigBean.setBatchId(387);
		 
		 StudentSubjectConfigExamBean studentSubjectConfigBean1=new StudentSubjectConfigExamBean();
		 studentSubjectConfigBean1.setId("1566");
		 studentSubjectConfigBean1.setPrgm_sem_subj_id("1991");
		 studentSubjectConfigBean1.setExamMonth("Apr");
		 studentSubjectConfigBean1.setExamYear("2023");
		 studentSubjectConfigBean1.setBatchId(389);
		 
		 timeboundIdList.add(studentSubjectConfigBean);
		 timeboundIdList.add(studentSubjectConfigBean1);
		 
		 ArrayList<DissertationResultDTO> q7ResultList=new ArrayList<DissertationResultDTO>();
		 ArrayList<DissertationResultDTO> q8ResultList=new ArrayList<DissertationResultDTO>();
		 
		 DissertationResultDTO q7Bean=new DissertationResultDTO();
		 q7Bean.setTimeBoundId(1568);
		 q7Bean.setPrgm_sem_subj_id(1990);
		 q7Bean.setSapid(77121233979L);
		 q7Bean.setComponent_a_score(19.50);
		 q7Bean.setComponent_b_score(36.00);
		 q7Bean.setIsPass("Y");
		 q7Bean.setComponent_a_status("Attempted");
		 q7Bean.setComponent_b_status("Attempted");
		 q7Bean.setIsResultLive("Y");
		 
		 q7ResultList.add(q7Bean);
		 
		 DissertationResultDTO q7Bean1=new DissertationResultDTO();
		 q7Bean1.setTimeBoundId(1568);
		 q7Bean1.setPrgm_sem_subj_id(1990);
		 q7Bean1.setSapid(77121590063L);
		 q7Bean1.setComponent_a_score(18.50);
		 q7Bean1.setComponent_b_score(37.00);
		 q7Bean1.setIsPass("Y");
		 q7Bean1.setComponent_a_status("Attempted");
		 q7Bean1.setComponent_b_status("Attempted");
		 q7Bean1.setIsResultLive("Y");
		 
		 q7ResultList.add(q7Bean1);
		 
		 
		 
		 DissertationResultDTO q8Bean=new DissertationResultDTO();
		 q8Bean.setTimeBoundId(1566);
		 q8Bean.setPrgm_sem_subj_id(1992);
		 q8Bean.setSapid(77421301870L);
		 q8Bean.setComponent_c_score(34.00);
		 q8Bean.setIsPass("Y");
		 q8Bean.setComponent_c_status("Attempted");
		 q8Bean.setIsResultLive("Y");
		 q8ResultList.add(q8Bean);
		 
		 DissertationResultDTO q8Bean1=new DissertationResultDTO();
		 q8Bean1.setTimeBoundId(1566);
		 q8Bean1.setPrgm_sem_subj_id(1992);
		 q8Bean1.setSapid(77421634095L);
		 q8Bean1.setComponent_c_score(31.00);
		 q8Bean1.setIsPass("Y");
		 q8Bean1.setComponent_c_status("Attempted");
		 q8Bean1.setIsResultLive("Y");
		 q8ResultList.add(q8Bean1);
		 
		ArrayList<Long> sapidsList=new ArrayList<Long>();
		sapidsList.add(77421301870L);
		sapidsList.add(77421634095L);
		sapidsList.add(77121233979L);
		sapidsList.add(77121590063L);
		
		 
		 ArrayList<DissertationResultDTO> q7q8studentIfoList=new ArrayList<DissertationResultDTO>();
		 
		 DissertationResultDTO studentInfo=new DissertationResultDTO();
		 studentInfo.setSapid(77421301870L);
		 studentInfo.setFirstName("Vishal");
		 studentInfo.setLastName("Garg");
		 studentInfo.setCenterName("Gurgaon - Golf Course Road");
		 studentInfo.setProgram("M.Sc. (AI & ML Ops)");
		 
		 DissertationResultDTO studentInfo1=new DissertationResultDTO();
		 studentInfo1.setSapid(77421634095L);
		 studentInfo1.setFirstName("Vipin");
		 studentInfo1.setLastName("Ahuja");
		 studentInfo1.setCenterName("Jaipur - Mansarovar");
		 studentInfo1.setProgram("M.Sc. (AI & ML Ops)");
		 
		 DissertationResultDTO studentInfo2=new DissertationResultDTO();
		 studentInfo2.setSapid(77121233979L);
		 studentInfo2.setFirstName("Rahul");
		 studentInfo2.setLastName("Chattopadhyay");
		 studentInfo2.setCenterName("Bangalore - Sanjay Nagar");
		 studentInfo2.setProgram("M.Sc. (AI & ML Ops)");
		 
		 DissertationResultDTO studentInfo3=new DissertationResultDTO();
		 studentInfo3.setSapid(77121590063L);
		 studentInfo3.setFirstName("Vidhi");
		 studentInfo3.setLastName("Waghela");
		 studentInfo3.setCenterName("Mumbai - Andheri Azad Nagar");
		 studentInfo3.setProgram("M.Sc. (AI & ML Ops)");
		 
		 q7q8studentIfoList.add(studentInfo);
		 q7q8studentIfoList.add(studentInfo1);
		 q7q8studentIfoList.add(studentInfo2);
		 q7q8studentIfoList.add(studentInfo3);
		 
		 
		 Map<String,String> batchIdAndBatchNameMap=new HashMap<String, String>();
		 batchIdAndBatchNameMap.put("387", "MSc (AI & Ml Ops) - Jan 2023 - Quarter 7 - Cohort 2 - Batch 1  (Saketh)~7");
		 batchIdAndBatchNameMap.put("389", "MSc (AI & Ml Ops) - Jan 2023 - Quarter 8 - Cohort 1 - Batch 2~8");
		 
		 ArrayList<String> q7TimeboundIdList=new ArrayList<String>();
		 q7TimeboundIdList.add("1568");
		 ArrayList<String> q8TimeboundIdList=new ArrayList<String>();
		 q7TimeboundIdList.add("1566");
		 
				when(examsAssessmentsDAO.getPSSBySubjectForQ7Q8()).thenReturn(pssList);
				when(examsAssessmentsDAO.getMsterKey(resultBean.getConsumerTypeId(), resultBean.getProgramStructureId(), resultBean.getProgramId())).thenReturn(userInputmasterKeyList);
				when(examsAssessmentsDAO.getTimeboubdIdByPSSAndBatch("'1990','1991','2787','2788'", resultBean.getBatchId(), resultBean.getExamMonth(), resultBean.getExamYear())).thenReturn(timeboundIdList);
				when(examsAssessmentsDAO.getReportFromQ7ByTimeBoundId(ArgumentMatchers.anyList(),ArgumentMatchers.eq(resultBean.getSapid()))).thenReturn(q7ResultList);
				when(examsAssessmentsDAO.getReportFromQ8ByTimeBoundId(ArgumentMatchers.anyList(),ArgumentMatchers.eq(resultBean.getSapid()))).thenReturn(q8ResultList);
				when(examsAssessmentsDAO.getStudentInfoBySapId(ArgumentMatchers.anyList())).thenReturn(q7q8studentIfoList);
				when(examsAssessmentsDAO.getBatchIdAndBatchNameMap(ArgumentMatchers.anyList())).thenReturn(batchIdAndBatchNameMap);
		 
		 boolean actualResult=false;
		  boolean expectedResult=true;
		 
		 try {
			Q7Q8DissertationResultBean q7q8Report=service.getDissertionReport(resultBean, "");
			if(q7q8Report.getQ7ResultList().size() >0 || q7q8Report.getQ8ResultList().size()>0)
			 actualResult=true;
			else 
				actualResult=false;
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			//e.printStackTrace();
			 actualResult=false;
		}
		  
		 assertEquals(expectedResult , actualResult);
		 
	 }
	 
	 @Test 
	 public void  MastersDissertationPassFialReportForJanCycleTest() {
		 MockitoAnnotations.initMocks(this);
		 
		 TEEResultDTO resultBean=new TEEResultDTO();
		 resultBean.setExamMonth("Jan");
		 // resultBean.set
		 
		 ArrayList<ProgramExamBean> pssList=new ArrayList<ProgramExamBean>();
		 
		 ProgramExamBean pss1=new ProgramExamBean();
		 pss1.setId("1990");
		 pss1.setName("Masters Dissertation Part - I");
		 pss1.setConsumerProgramStructureId("131");
		 
		 ProgramExamBean pss2=new ProgramExamBean();
		 pss2.setId("1991");
		 pss2.setName("Masters Dissertation Part - II");
		 pss2.setConsumerProgramStructureId("131");
		 
		 ProgramExamBean pss3=new ProgramExamBean();
		 pss3.setId("2787");
		 pss3.setName("Masters Dissertation Part - I");
		 pss3.setConsumerProgramStructureId("158");
		 ProgramExamBean pss4=new ProgramExamBean();
		 pss4.setId("2788");
		 pss4.setName("Masters Dissertation Part - II");
		 pss4.setConsumerProgramStructureId("158");
		 
		 pssList.add(pss1);
		 pssList.add(pss2);
		 pssList.add(pss3);
		 pssList.add(pss4);
		 
		 List<String> userInputmasterKeyList=new ArrayList<String>();
		 userInputmasterKeyList.add("131");
		 userInputmasterKeyList.add("158");
		 
		 ArrayList<StudentSubjectConfigExamBean> timeboundIdList=new ArrayList<StudentSubjectConfigExamBean>();
		 StudentSubjectConfigExamBean studentSubjectConfigBean=new StudentSubjectConfigExamBean();
		 studentSubjectConfigBean.setId("1568");
		 studentSubjectConfigBean.setPrgm_sem_subj_id("1990");
		 studentSubjectConfigBean.setExamMonth("Apr");
		 studentSubjectConfigBean.setExamYear("2023");
		 studentSubjectConfigBean.setBatchId(387);
		 
		 StudentSubjectConfigExamBean studentSubjectConfigBean1=new StudentSubjectConfigExamBean();
		 studentSubjectConfigBean1.setId("1566");
		 studentSubjectConfigBean1.setPrgm_sem_subj_id("1991");
		 studentSubjectConfigBean1.setExamMonth("Apr");
		 studentSubjectConfigBean1.setExamYear("2023");
		 studentSubjectConfigBean1.setBatchId(389);
		 
		 timeboundIdList.add(studentSubjectConfigBean);
		 timeboundIdList.add(studentSubjectConfigBean1);
		 
		 ArrayList<DissertationResultDTO> q7ResultList=new ArrayList<DissertationResultDTO>();
		 ArrayList<DissertationResultDTO> q8ResultList=new ArrayList<DissertationResultDTO>();
		 
		 DissertationResultDTO q7Bean=new DissertationResultDTO();
		 q7Bean.setTimeBoundId(1568);
		 q7Bean.setPrgm_sem_subj_id(1990);
		 q7Bean.setSapid(77121233979L);
		 q7Bean.setComponent_a_score(19.50);
		 q7Bean.setComponent_b_score(36.00);
		 q7Bean.setIsPass("Y");
		 q7Bean.setComponent_a_status("Attempted");
		 q7Bean.setComponent_b_status("Attempted");
		 q7Bean.setIsResultLive("Y");
		 
		 q7ResultList.add(q7Bean);
		 
		 DissertationResultDTO q7Bean1=new DissertationResultDTO();
		 q7Bean1.setTimeBoundId(1568);
		 q7Bean1.setPrgm_sem_subj_id(1990);
		 q7Bean1.setSapid(77121590063L);
		 q7Bean1.setComponent_a_score(18.50);
		 q7Bean1.setComponent_b_score(37.00);
		 q7Bean1.setIsPass("Y");
		 q7Bean1.setComponent_a_status("Attempted");
		 q7Bean1.setComponent_b_status("Attempted");
		 q7Bean1.setIsResultLive("Y");
		 
		 q7ResultList.add(q7Bean1);
		 
		 
		 
		 DissertationResultDTO q8Bean=new DissertationResultDTO();
		 q8Bean.setTimeBoundId(1566);
		 q8Bean.setPrgm_sem_subj_id(1992);
		 q8Bean.setSapid(77421301870L);
		 q8Bean.setComponent_c_score(34.00);
		 q8Bean.setIsPass("Y");
		 q8Bean.setComponent_c_status("Attempted");
		 q8Bean.setIsResultLive("Y");
		 q8ResultList.add(q8Bean);
		 
		 DissertationResultDTO q8Bean1=new DissertationResultDTO();
		 q8Bean1.setTimeBoundId(1566);
		 q8Bean1.setPrgm_sem_subj_id(1992);
		 q8Bean1.setSapid(77421634095L);
		 q8Bean1.setComponent_c_score(31.00);
		 q8Bean1.setIsPass("Y");
		 q8Bean1.setComponent_c_status("Attempted");
		 q8Bean1.setIsResultLive("Y");
		 q8ResultList.add(q8Bean1);
		 
		 ArrayList<Long> sapidsList=new ArrayList<Long>();
		 sapidsList.add(77421301870L);
		 sapidsList.add(77421634095L);
		 sapidsList.add(77121233979L);
		 sapidsList.add(77121590063L);
		 
		 
		 ArrayList<DissertationResultDTO> q7q8studentIfoList=new ArrayList<DissertationResultDTO>();
		 
		 DissertationResultDTO studentInfo=new DissertationResultDTO();
		 studentInfo.setSapid(77421301870L);
		 studentInfo.setFirstName("Vishal");
		 studentInfo.setLastName("Garg");
		 studentInfo.setCenterName("Gurgaon - Golf Course Road");
		 studentInfo.setProgram("M.Sc. (AI & ML Ops)");
		 
		 DissertationResultDTO studentInfo1=new DissertationResultDTO();
		 studentInfo1.setSapid(77421634095L);
		 studentInfo1.setFirstName("Vipin");
		 studentInfo1.setLastName("Ahuja");
		 studentInfo1.setCenterName("Jaipur - Mansarovar");
		 studentInfo1.setProgram("M.Sc. (AI & ML Ops)");
		 
		 DissertationResultDTO studentInfo2=new DissertationResultDTO();
		 studentInfo2.setSapid(77121233979L);
		 studentInfo2.setFirstName("Rahul");
		 studentInfo2.setLastName("Chattopadhyay");
		 studentInfo2.setCenterName("Bangalore - Sanjay Nagar");
		 studentInfo2.setProgram("M.Sc. (AI & ML Ops)");
		 
		 DissertationResultDTO studentInfo3=new DissertationResultDTO();
		 studentInfo3.setSapid(77121590063L);
		 studentInfo3.setFirstName("Vidhi");
		 studentInfo3.setLastName("Waghela");
		 studentInfo3.setCenterName("Mumbai - Andheri Azad Nagar");
		 studentInfo3.setProgram("M.Sc. (AI & ML Ops)");
		 
		 q7q8studentIfoList.add(studentInfo);
		 q7q8studentIfoList.add(studentInfo1);
		 q7q8studentIfoList.add(studentInfo2);
		 q7q8studentIfoList.add(studentInfo3);
		 
		 
		 Map<String,String> batchIdAndBatchNameMap=new HashMap<String, String>();
		 batchIdAndBatchNameMap.put("387", "MSc (AI & Ml Ops) - Jan 2023 - Quarter 7 - Cohort 2 - Batch 1  (Saketh)~7");
		 batchIdAndBatchNameMap.put("389", "MSc (AI & Ml Ops) - Jan 2023 - Quarter 8 - Cohort 1 - Batch 2~8");
		 
		 ArrayList<String> q7TimeboundIdList=new ArrayList<String>();
		 q7TimeboundIdList.add("1568");
		 ArrayList<String> q8TimeboundIdList=new ArrayList<String>();
		 q7TimeboundIdList.add("1566");
		 
		 when(examsAssessmentsDAO.getPSSBySubjectForQ7Q8()).thenReturn(pssList);
		 when(examsAssessmentsDAO.getMsterKey(resultBean.getConsumerTypeId(), resultBean.getProgramStructureId(), resultBean.getProgramId())).thenReturn(userInputmasterKeyList);
		 when(examsAssessmentsDAO.getTimeboubdIdByPSSAndBatch("'1990','1991','2787','2788'", resultBean.getBatchId(), resultBean.getExamMonth(), resultBean.getExamYear())).thenReturn(timeboundIdList);
		 when(examsAssessmentsDAO.getReportFromQ7ByTimeBoundId(ArgumentMatchers.anyList(),ArgumentMatchers.eq(resultBean.getSapid()))).thenReturn(q7ResultList);
		 when(examsAssessmentsDAO.getReportFromQ8ByTimeBoundId(ArgumentMatchers.anyList(),ArgumentMatchers.eq(resultBean.getSapid()))).thenReturn(q8ResultList);
		 when(examsAssessmentsDAO.getStudentInfoBySapId(ArgumentMatchers.anyList())).thenReturn(q7q8studentIfoList);
		 when(examsAssessmentsDAO.getBatchIdAndBatchNameMap(ArgumentMatchers.anyList())).thenReturn(batchIdAndBatchNameMap);
		 
		 boolean actualResult=false;
		 boolean expectedResult=true;
		 
		 try {
			 Q7Q8DissertationResultBean q7q8Report=service.getDissertionReport(resultBean, "");
			 if(q7q8Report.getQ7ResultList().size() >0 || q7q8Report.getQ8ResultList().size()>0)
				 actualResult=true;
			 else 
				 actualResult=false;
		 } catch (Exception e) {
			 e.printStackTrace();
			 // TODO: handle exception
			 //e.printStackTrace();
			 actualResult=false;
		 }
		 
		 assertEquals(expectedResult , actualResult);
		 
	 }
	 
	 
//	 @Test 
//	 public void  MastersDissertationPassFialReportForSingleStudentTest() {
//		 MockitoAnnotations.initMocks(this);
//		 TEEResultDTO resultBean=new TEEResultDTO();
//		 resultBean.setSapid("77421301870");
//		 
//		 ArrayList<DissertationResultDTO> q7ReportList=new ArrayList<DissertationResultDTO>();
//		ArrayList<DissertationResultDTO> q8ReportList=new ArrayList<DissertationResultDTO>();
//		
//		 ArrayList<DissertationResultDTO> q7ResultList=new ArrayList<DissertationResultDTO>();
//		 ArrayList<DissertationResultDTO> q8ResultList=new ArrayList<DissertationResultDTO>();
//		 
//		 DissertationResultDTO q7Bean=new DissertationResultDTO();
//		 q7Bean.setTimeBoundId(1568);
//		 q7Bean.setPrgm_sem_subj_id(1990);
//		 q7Bean.setSapid(77121233979L);
//		 q7Bean.setComponent_a_score(19.50);
//		 q7Bean.setComponent_b_score(36.00);
//		 q7Bean.setIsPass("Y");
//		 q7Bean.setComponent_a_status("Attempted");
//		 q7Bean.setComponent_b_status("Attempted");
//		 q7Bean.setIsResultLive("Y");
//		 
//		 q7ResultList.add(q7Bean);
//		 
//		 
//		 
//		 
//		 DissertationResultDTO q8Bean1=new DissertationResultDTO();
//		 q8Bean1.setTimeBoundId(1773);
//		 q8Bean1.setPrgm_sem_subj_id(1992);
//		 q8Bean1.setSapid(77121233979L);
//		 q8Bean1.setComponent_c_score(31.00);
//		 q8Bean1.setIsPass("Y");
//		 q8Bean1.setComponent_c_status("Attempted");
//		 q8Bean1.setIsResultLive("Y");
//		 q8ResultList.add(q8Bean1);
//		 
//		 List<StudentSubjectConfigExamBean> timeBoundIdList=new ArrayList<StudentSubjectConfigExamBean>();
//		 
//		 StudentSubjectConfigExamBean q7ReportBean=new StudentSubjectConfigExamBean();
//		 q7ReportBean.setId("1568");
//		 q7ReportBean.setBatchId(387);
//		 q7ReportBean.setPrgm_sem_subj_id("387");
//		 timeBoundIdList.add(q7ReportBean);
//		 
//		 StudentSubjectConfigExamBean q8ReportBean=new StudentSubjectConfigExamBean();
//		 q8ReportBean.setId("1773");
//		 q8ReportBean.setBatchId(451);
//		 q8ReportBean.setPrgm_sem_subj_id("1991");
//		 timeBoundIdList.add(q8ReportBean);
//				 
//		 
//		 when(examsAssessmentsDAO.getReportFromQ7ByTimeBoundId(ArgumentMatchers.anyList(),ArgumentMatchers.eq(resultBean.getSapid()))).thenReturn(q7ReportList);
//		 when(examsAssessmentsDAO.getReportFromQ8ByTimeBoundId(ArgumentMatchers.anyList(),ArgumentMatchers.eq(resultBean.getSapid()))).thenReturn(q8ReportList);
//		 when(examsAssessmentsDAO.getBatchIdByTimeBoundId(ArgumentMatchers.anyList())).thenReturn(timeBoundIdList);
////		 when(examsAssessmentsDAO.getTimeboubdIdByPSSAndBatch("'1990','1991','2787','2788'", resultBean.getBatchId(), resultBean.getExamMonth(), resultBean.getExamYear())).thenReturn(timeboundIdList);
////		 when(examsAssessmentsDAO.getReportFromQ7ByTimeBoundId(ArgumentMatchers.anyList(),ArgumentMatchers.eq(resultBean.getSapid()))).thenReturn(q7ResultList);
////		 when(examsAssessmentsDAO.getReportFromQ8ByTimeBoundId(ArgumentMatchers.anyList(),ArgumentMatchers.eq(resultBean.getSapid()))).thenReturn(q8ResultList);
////		 when(examsAssessmentsDAO.getStudentInfoBySapId(ArgumentMatchers.anyList())).thenReturn(q7q8studentIfoList);
////		 when(examsAssessmentsDAO.getBatchIdAndBatchNameMap(ArgumentMatchers.anyList())).thenReturn(batchIdAndBatchNameMap);
//		 
//		boolean actualResult=false; 
//		 try {
//			 Q7Q8DissertationResultBean q7q8Report=service.getDissertionReport(resultBean, "");
//			 System.out.println("q7q8Report :: "+q7q8Report);
//			 if(q7q8Report.getQ7ResultList().size() >0 || q7q8Report.getQ8ResultList().size()>0)
//				 actualResult=true;
//			 else 
//				 actualResult=false;
//		 } catch (Exception e) {
//			 e.printStackTrace();
//			 // TODO: handle exception
//			 //e.printStackTrace();
//			 actualResult=false;
//		 }
//	 }

}
