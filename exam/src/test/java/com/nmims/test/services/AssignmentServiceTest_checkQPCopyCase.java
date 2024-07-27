package com.nmims.test.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ResultDomain;
import com.nmims.beans.UserAuthorizationExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.helpers.CopyCaseHelper;
import com.nmims.services.AssignmentService;
import com.nmims.services.LevelBasedProjectService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.mock.web.MockMultipartFile;
import java.util.ArrayList;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AssignmentServiceTest_checkQPCopyCase {


		
	  
	  @InjectMocks
	  AssignmentService asgService;
	  
	  @InjectMocks 
	  CopyCaseHelper qpHelper1;
	  
	  @Mock
	  CopyCaseHelper qpHelper;
	  
	  
	  @Mock 
	  AssignmentsDAO asgdao;
	  
	  @Mock
	  ProjectSubmissionDAO projectSubmissionDAO;
	  
	 
	  
	  
	  
	  @Before
	    public void setUp() {
	        MockitoAnnotations.initMocks(this);

	        // Set the value of the ASSIGNMENT_FILES_PATH field
	        ReflectionTestUtils.setField(asgService, "ASSIGNMENT_FILES_PATH", "https://assignment-files.s3.ap-south-1.amazonaws.com/");
	    }
	  
	  @Test public void  testCheckQPCopyCase() {
	  MockitoAnnotations.initMocks(this);
	  System.out.println("Testt.testCheckQPCopyCase()");
	  
	  
	  
	  // CopyCaseHelper qpHelper=new CopyCaseHelper();
	  
	  List<String> userQP=new ArrayList<String>();
	  userQP.add("Mr. James Steven is the Sr Manager in an established IT company"); 
	  userQP.add("He works with a core team of 20 and at most times communicated through written verbal communication"); 
	  userQP.add("He intends to sound persuasive while he communicates with his team and he wants them to feel equal at all times"); 
	  userQP.add("Define strategies of writing persuasive messages" );
	  userQP.add("Define what is a Report and a Proposal");
	  userQP.add("How can social media impact communication in business");
	  userQP.add(" What are the strategies for Business Communication through Social Network " );
	  userQP.add("You are planning to switch your job and are in the process of applying to another company. What is a job market"); 
	  userQP.add("What are the points of an organization and job opportunity will you consider while in the process of applying for the job"); 
	  
	  List<String> Jun2023QP=new ArrayList<String>();
	  Jun2023QP. add("Bad gums may mean a bad mood Researchers discovered that 85% of people whohave suffered a bad mood had periodontal disease, an inflammation of the gums" );
	  Jun2023QP.add("Suppose that in a certain community bad moods are quite rare, occurring with only 10% probability" );
	  Jun2023QP.add("Draw the tree diagram for the above problem. Handwritten tree diagram is prohibited" );
	  Jun2023QP.add("Write the interpretation of EXCEL Tables");
	  Jun2023QP.add("What measures will help you enhance your credibility in the market");
	  Jun2023QP.add("Based on the stage that you are in your career, prepare a chronological or functional resume for a position in your dream company" ); 
	  Jun2023QP.add("You need to create your resume in any 1 format chronological OR functional, properly justifying your choice" );
	  Jun2023QP.add("Your credibility is affected by your attention to detail while completing business messages" ); 
	  Jun2023QP.add("Highlight the importance of proofreading your documents with any 4 time-testem techniques" ); 
	  Jun2023QP.add("In order to make your content mobile friendly what are the key aspects you need to focus on" );
	 
	  ArrayList<List<String>> allFileContentsList=new ArrayList<List<String>>();
	  allFileContentsList.add(userQP);
//	  allFileContentsList.add(Jun2023QP);
	  
	  ExamOrderExamBean examorderbean= new ExamOrderExamBean();
	  examorderbean.setOrder("30.00");
	  examorderbean.setYear("2023");
	  examorderbean.setMonth("Jun");
	  
	  ArrayList<ExamOrderExamBean> lstFiveCycleList = new
	  ArrayList<ExamOrderExamBean>();
		ExamOrderExamBean firstcycle = new ExamOrderExamBean();
		firstcycle.setYear("2023");
		firstcycle.setMonth("Jun");
		firstcycle.setOrder("30.00");
		ExamOrderExamBean secondcycle = new ExamOrderExamBean();
		secondcycle.setYear("2023");
		secondcycle.setMonth("Apr");
		secondcycle.setOrder("29.50");
		ExamOrderExamBean Thirdcycle = new ExamOrderExamBean();
		Thirdcycle.setYear("2022");
		Thirdcycle.setMonth("Dec");
		Thirdcycle.setOrder("29.00");
		ExamOrderExamBean Forthcycle = new ExamOrderExamBean();
		Forthcycle.setYear("2022");
		Forthcycle.setMonth("Sep");
		Forthcycle.setOrder("28.50");
		ExamOrderExamBean fifthcycle = new ExamOrderExamBean();
		fifthcycle.setYear("2022");
		fifthcycle.setMonth("Jun");
		fifthcycle.setOrder("28.00");
		lstFiveCycleList.add(firstcycle);
		lstFiveCycleList.add(secondcycle);
		lstFiveCycleList.add(Thirdcycle);
		lstFiveCycleList.add(Forthcycle);
		lstFiveCycleList.add(fifthcycle);
	  
		List<String> subjectcodId= new ArrayList<String>();
		subjectcodId.add("246");
		subjectcodId.add("245");

//	  String masterkeyList=
//	  "'1','3','4','5','6','7','9','10','11','12','13','14','15','16','18','19','20','21','22','24','25','26','0','27','28','29','30','31','32','33','34','35','36','37','38','39','8','17','86','93','90','95','89','97','98','94','96','92','110','23','112','116','118','108','127','128','142','143','144','145','147','149','148','146','150','152','153','154','155','159','163','164'";
	  String masterkeyList="'83','82','80','81'";
	  String subject="Business Communication";
	  
	  List<String> filePathPrivew=new ArrayList<String>(); 
	  filePathPrivew.add("Jun2023/Business_Communication_-_June_2023_Revised_vsudNRFVsS.pdf");
	  filePathPrivew.add( "Jun2023/Business_Communication_-_June_2023_Revised_mHrCcDWbal.pdf");
	  filePathPrivew.add( "Jun2023/Business_Communication_-_June_2023_Revised_hJXzmq0A8T.pdf");
	  
	  Double result=60.00;
	  
	  
	  CopyCaseHelper qpHelperMock = mock(CopyCaseHelper.class);
	  
	  
	  
	  // Read the bytes from the InputStream into a ByteArrayOutputStream
	  
	  Path pdfPath = Paths.get("D:\\pdfiles\\Business_Communication_-_Assignment_April_2023_cBPb0R22iP.pdf");
	  byte[] pdfBytes=null;
	  
	  // Read the PDF file bytes
	  try {
		  pdfBytes = Files.readAllBytes(pdfPath); 
		  }
	  catch (IOException e1) { // TODO Auto-generated catch block
	 // e1.printStackTrace(); 
	  }
	 
	  
	  // Create a MockMultipartFile object for the PDF file 
	  MockMultipartFile file = new MockMultipartFile("file.pdf", "file.pdf", "application/pdf", pdfBytes);
	  
	  System.out.println("qpHelper :: "+qpHelper);
	  double per=36.0;
	  
	  try {
	  
	  if (file != null) { 
		  System.out.println("check 1");
	 
	  when(qpHelper.readAllContaint(file)).thenReturn(userQP); 
//	  when(qpHelperMock.readAllContaint(file)).thenReturn(userQP);
	 System.out.println("check 2"); 
	 } 
	  
	  when(asgdao.getCurrentLiveCycle()).thenReturn(examorderbean);
	  when(asgdao.getLastFiveCycle(28.00, 30.00)).thenReturn(lstFiveCycleList);
	  when(asgdao.getSubjectCidId(masterkeyList, subject,firstcycle.getYear(),firstcycle.getMonth())).thenReturn(subjectcodId);
	  when(asgdao.getQPS("Jun", "2023",subject,subjectcodId.get(0))).thenReturn(filePathPrivew);
	  List<String> list=qpHelper1.readAllContaint("https://assignment-files.s3.ap-south-1.amazonaws.com/Jun2023/Business_Communication_-_June_2023_Revised_vsudNRFVsS.pdf");
	  when(qpHelper.readAllContaint("https://assignment-files.s3.ap-south-1.amazonaws.com/Jun2023/Business_Communication_-_June_2023_Revised_vsudNRFVsS.pdf")).thenReturn(list);
	  
	  System.out.println("--list --::"+list);
	  allFileContentsList.add(list);
	  double matching= qpHelper1.compareQPCopyCase(0, allFileContentsList,firstcycle.getYear(),firstcycle.getMonth());
	  double actual=matching;
//	  when(qpHelper.compareQPCopyCase(anyInt(), any(), anyString(), anyString()))
//	    .thenReturn(matching);
	  
	  when(qpHelper.compareQPCopyCase(anyInt(), any(), anyString(), anyString()))
	    .thenReturn(matching)  // Return value for the first call
//	    .thenReturn(26.0)  // Return value for the second call
//	    .thenReturn(38.0)  // Return value for the third call
	    .thenReturn(00.0);  // Return value for subsequent calls
	  
//	  when(qpHelperMock.compareQPCopyCase(0,allFileContentsList,firstcycle.getYear(),firstcycle.getMonth())).thenReturn(36.0);
	  List<ResultDomain> resultDomainBeanList=null; 
	  List<String> masterKeys=new ArrayList<String>();
	  masterKeys.add("83"); 
	  masterKeys.add("82");
	  masterKeys.add("80"); 
	  masterKeys.add("81");
	  System.out.println("File containt ::"+file.getBytes());
	  
	  resultDomainBeanList=asgService.checkQPCopyCase("Business Communication",file, masterKeys);
	  
		
	  boolean expected=false; 
	  
	  AtomicReference<Double> expectedmatchingPercentage = new AtomicReference<>(0.0);

	  resultDomainBeanList.stream().forEach(x -> {
	      if (x.getMonth().equalsIgnoreCase("Jun") && x.getYear().equalsIgnoreCase("2023")) {
	          System.out.println("cycle :: " + x.getMonth() + " " + x.getYear());
	          System.out.println("Percentage :: " + x.getMatching());
	          System.out.println("Matching Text :: " + x.getCommonText());
	          expectedmatchingPercentage.set(x.getMatching());
	      }
	  });

	  // Access the updated value outside the lambda expression
	  double expectedValue = expectedmatchingPercentage.get();
	  double delta = 0.1;
	 
	  if(expectedValue==actual) {
		  expected=true;
	  }


		if (!resultDomainBeanList.isEmpty()) { 
			
			assertEquals(true, expected);
		}
	}

	catch (Exception e) { // TODO: handle exception e.printStackTrace(); }
		System.out.println("Execption is :: " + e.getMessage());
		//e.printStackTrace();
		// TODO: handle exception }

	}

}
	  @Test 
	  public void  testCheckQPCopyCases() {
		  MockitoAnnotations.initMocks(this);
		  System.out.println("----------------Testt.testCheckQPCopyCase()------------------");
		  
		  
		  
		  // CopyCaseHelper qpHelper=new CopyCaseHelper();
		  
		
		  List<String> userQP=new ArrayList<String>();
		  userQP. add("Bad gums may mean a bad mood Researchers discovered that 85% of people whohave suffered a bad mood had periodontal disease, an inflammation of the gums" );
		  userQP.add("Suppose that in a certain community bad moods are quite rare, occurring with only 10% probability" );
		  userQP.add("Draw the tree diagram for the above problem. Handwritten tree diagram is prohibited" );
		  userQP.add("Write the interpretation of EXCEL Tables");
		  userQP.add("1000 light bulbs with a mean life of 120 days are installed in a new factory and their length of life is normally distributed with standard deviation of 20 days");
		  userQP.add("If it is decided to replace all the bulbs together, what interval should be allowed between replacements if not more than 10% should expire before replacement" ); 
		  userQP.add("Using MS-EXCEL show the Regression model, consider ‘Instagram followers’ as dependent variable and ‘no f post per day’ as an independent variable" );
		  userQP.add("Write the conclusion on the fitting of your model also" ); 
		  userQP.add("Students should write the assignment in their own words. Copying of assignments from other students is not allowed" ); 
		  userQP.add("Draw the tree diagram for the above problem. Handwritten tree diagram is prohibited" );
		 
		  ArrayList<List<String>> allFileContentsList=new ArrayList<List<String>>();
		  allFileContentsList.add(userQP);
//		  allFileContentsList.add(Jun2023QP);
		  
		  ExamOrderExamBean examorderbean= new ExamOrderExamBean();
		  examorderbean.setOrder("30.00");
		  examorderbean.setYear("2023");
		  examorderbean.setMonth("Jun");
		  
		  ArrayList<ExamOrderExamBean> lstFiveCycleList = new
		  ArrayList<ExamOrderExamBean>();
			ExamOrderExamBean firstcycle = new ExamOrderExamBean();
			firstcycle.setYear("2023");
			firstcycle.setMonth("Jun");
			firstcycle.setOrder("30.00");
			ExamOrderExamBean secondcycle = new ExamOrderExamBean();
			secondcycle.setYear("2023");
			secondcycle.setMonth("Apr");
			secondcycle.setOrder("29.50");
			ExamOrderExamBean Thirdcycle = new ExamOrderExamBean();
			Thirdcycle.setYear("2022");
			Thirdcycle.setMonth("Dec");
			Thirdcycle.setOrder("29.00");
			ExamOrderExamBean Forthcycle = new ExamOrderExamBean();
			Forthcycle.setYear("2022");
			Forthcycle.setMonth("Sep");
			Forthcycle.setOrder("28.50");
			ExamOrderExamBean fifthcycle = new ExamOrderExamBean();
			fifthcycle.setYear("2022");
			fifthcycle.setMonth("Jun");
			fifthcycle.setOrder("28.00");
			lstFiveCycleList.add(firstcycle);
			lstFiveCycleList.add(secondcycle);
			lstFiveCycleList.add(Thirdcycle);
			lstFiveCycleList.add(Forthcycle);
			lstFiveCycleList.add(fifthcycle);
		  
			List<String> subjectcodId= new ArrayList<String>();
			subjectcodId.add("118");

		  String masterkeyList="'28','45'";
		  String subject="Business Statistics";
		  
		  List<String> filePathPrivew=new ArrayList<String>(); 
		  filePathPrivew.add("Jun2023/Business_Statistics_-_June_2023_yUscY8s3ke.pdf");
		  
		  Double result=60.00;
		  
		  
		  CopyCaseHelper qpHelperMock = mock(CopyCaseHelper.class);
		  
		  
		  
		  // Read the bytes from the InputStream into a ByteArrayOutputStream
		  
		  Path pdfPath = Paths.get("D:\\pdfiles\\Business_Statistics_-_Assignment_April_2023_1chKENmWDA.pdf");
		  byte[] pdfBytes=null;
		  
		  // Read the PDF file bytes
		  try {
			  pdfBytes = Files.readAllBytes(pdfPath); 
			  }
		  catch (IOException e1) { // TODO Auto-generated catch block
		//  e1.printStackTrace();
		  }
		 
		  
		  // Create a MockMultipartFile object for the PDF file 
		  MockMultipartFile file = new MockMultipartFile("file.pdf", "file.pdf", "application/pdf", pdfBytes);
		  
		  try {
		  
		  if (file != null) { 
			  System.out.println("check 1");
		 
		  when(qpHelper.readAllContaint(file)).thenReturn(userQP); 
//		  when(qpHelperMock.readAllContaint(file)).thenReturn(userQP);
		 System.out.println("check 2"); 
		 } 
		  
		  when(asgdao.getCurrentLiveCycle()).thenReturn(examorderbean);
		  when(asgdao.getLastFiveCycle(28.00, 30.00)).thenReturn(lstFiveCycleList);
		  when(asgdao.getSubjectCidId("'28','45'", subject,firstcycle.getYear(),firstcycle.getMonth())).thenReturn(subjectcodId);
		  when(asgdao.getQPS("Jun", "2023",subject,"118")).thenReturn(filePathPrivew);
		  List<String> list=qpHelper1.readAllContaint("https://assignment-files.s3.ap-south-1.amazonaws.com/Jun2023/Business_Statistics_-_June_2023_yUscY8s3ke.pdf");
		  when(qpHelper.readAllContaint("https://assignment-files.s3.ap-south-1.amazonaws.com/Jun2023/Business_Statistics_-_June_2023_yUscY8s3ke.pdf")).thenReturn(list);
		  
		  System.out.println("--list --::"+list);
		  allFileContentsList.add(list);
		  double matching= qpHelper1.compareQPCopyCase(0, allFileContentsList,firstcycle.getYear(),firstcycle.getMonth());
		  double actual=matching;
//		  when(qpHelper.compareQPCopyCase(anyInt(), any(), anyString(), anyString()))
//		    .thenReturn(matching);
		  
		  when(qpHelper.compareQPCopyCase(anyInt(), any(), anyString(), anyString()))
		    .thenReturn(matching)  // Return value for the first call
//		    .thenReturn(26.0)  // Return value for the second call
//		    .thenReturn(38.0)  // Return value for the third call
		    .thenReturn(00.0);  // Return value for subsequent calls
		  
//		  when(qpHelperMock.compareQPCopyCase(0,allFileContentsList,firstcycle.getYear(),firstcycle.getMonth())).thenReturn(36.0);
		  List<ResultDomain> resultDomainBeanList=null; 
		  List<String> masterKeys=new ArrayList<String>();
		  masterKeys.add("28"); 
		  masterKeys.add("45");
		  System.out.println("File containt ::"+file.getBytes());
		  
		  resultDomainBeanList=asgService.checkQPCopyCase(subject,file, masterKeys);
			
		  boolean expected=false; 
		  
		  AtomicReference<Double> expectedmatchingPercentage = new AtomicReference<>(0.0);

		  resultDomainBeanList.stream().forEach(x -> {
		      if (x.getMonth().equalsIgnoreCase("Jun") && x.getYear().equalsIgnoreCase("2023")) {
		          System.out.println("cycle :: " + x.getMonth() + " " + x.getYear());
		          System.out.println("Percentage :: " + x.getMatching());
		          System.out.println("Matching Text :: " + x.getCommonText());
		          expectedmatchingPercentage.set(x.getMatching());
		      }
		  });

		  // Access the updated value outside the lambda expression
		  double expectedValue = expectedmatchingPercentage.get();
		  double delta = 0.1;
		 
		  if(expectedValue==actual) {
			  expected=true;
		  }


			if (!resultDomainBeanList.isEmpty()) { 
				
				assertEquals(true, expected);
			}
		}

		catch (Exception e) { // TODO: handle exception e.printStackTrace(); }
			System.out.println("Execption is :: " + e.getMessage());
			//e.printStackTrace();
			// TODO: handle exception }

		}

}
	  
	  
	  
	  @Test
	  public void checkQPWithDifferentCycle() {
		  

		  MockitoAnnotations.initMocks(this);
		  System.out.println("Testt.testCheckQPCopyCase()");
		  
		  
		  
		  // CopyCaseHelper qpHelper=new CopyCaseHelper();
		  
		  
		  List<String> userQP=new ArrayList<String>();
		  userQP.add("Mr. James Steven is the Sr Manager in an established IT company"); 
		  userQP.add("He works with a core team of 20 and at most times communicated through written verbal communication"); 
		  userQP.add("He intends to sound persuasive while he communicates with his team and he wants them to feel equal at all times"); 
		  userQP.add("? All Questions carry equal marks" );
		  userQP.add("Define what is a Report and a Proposal");
		  userQP.add("How can social media impact communication in business");
		  userQP.add(" What are the strategies for Business Communication through Social Network " );
		  userQP.add("You are planning to switch your job and are in the process of applying to another company. What is a job market"); 
		  userQP.add("Business Communication Internal Assignment Applicable for April 2023 Examination 2"); 
		  
		 
		  ArrayList<List<String>> allFileContentsList=new ArrayList<List<String>>();
		  allFileContentsList.add(userQP);
//		  allFileContentsList.add(Jun2023QP);
		  
		  ExamOrderExamBean examorderbean= new ExamOrderExamBean();
		  examorderbean.setOrder("30.00");
		  examorderbean.setYear("2023");
		  examorderbean.setMonth("Jun");
		  
		  ArrayList<ExamOrderExamBean> lstFiveCycleList = new
		  ArrayList<ExamOrderExamBean>();
			ExamOrderExamBean firstcycle = new ExamOrderExamBean();
			firstcycle.setYear("2023");
			firstcycle.setMonth("Jun");
			firstcycle.setOrder("30.00");
			ExamOrderExamBean secondcycle = new ExamOrderExamBean();
			secondcycle.setYear("2023");
			secondcycle.setMonth("Apr");
			secondcycle.setOrder("29.50");
			ExamOrderExamBean Thirdcycle = new ExamOrderExamBean();
			Thirdcycle.setYear("2022");
			Thirdcycle.setMonth("Dec");
			Thirdcycle.setOrder("29.00");
			ExamOrderExamBean Forthcycle = new ExamOrderExamBean();
			Forthcycle.setYear("2022");
			Forthcycle.setMonth("Sep");
			Forthcycle.setOrder("28.50");
			ExamOrderExamBean fifthcycle = new ExamOrderExamBean();
			fifthcycle.setYear("2022");
			fifthcycle.setMonth("Jun");
			fifthcycle.setOrder("28.00");
			lstFiveCycleList.add(firstcycle);
			lstFiveCycleList.add(secondcycle);
			lstFiveCycleList.add(Thirdcycle);
			lstFiveCycleList.add(Forthcycle);
			lstFiveCycleList.add(fifthcycle);
		  
			List<String> subjectcodId= new ArrayList<String>();
			subjectcodId.add("246");
			subjectcodId.add("245");

//		  String masterkeyList=
//		  "'1','3','4','5','6','7','9','10','11','12','13','14','15','16','18','19','20','21','22','24','25','26','0','27','28','29','30','31','32','33','34','35','36','37','38','39','8','17','86','93','90','95','89','97','98','94','96','92','110','23','112','116','118','108','127','128','142','143','144','145','147','149','148','146','150','152','153','154','155','159','163','164'";
		  String masterkeyList="'83','82','80','81'";
		  String subject="Business Communication";
		  
		  List<String> filePathPrivew=new ArrayList<String>(); 
		  filePathPrivew.add("Jun2023/Business_Communication_-_June_2023_Revised_vsudNRFVsS.pdf");
		  filePathPrivew.add( "Jun2023/Business_Communication_-_June_2023_Revised_mHrCcDWbal.pdf");
		  filePathPrivew.add( "Jun2023/Business_Communication_-_June_2023_Revised_hJXzmq0A8T.pdf");
		  
		  Double result=60.00;
		  
		  
		  CopyCaseHelper qpHelperMock = mock(CopyCaseHelper.class);
		  
		  
		  
		  // Read the bytes from the InputStream into a ByteArrayOutputStream
		  
		  Path pdfPath = Paths.get("D:\\pdfiles\\Business_Communication_-_Assignment_April_2023_cBPb0R22iP.pdf");
		  byte[] pdfBytes=null;
		  
		  // Read the PDF file bytes
		  try {
			  pdfBytes = Files.readAllBytes(pdfPath); 
			  }
		  catch (IOException e1) { // TODO Auto-generated catch block
		 // e1.printStackTrace();
		  }
		 
		  
		  // Create a MockMultipartFile object for the PDF file 
		  MockMultipartFile file = new MockMultipartFile("file.pdf", "file.pdf", "application/pdf", pdfBytes);
		  
		  System.out.println("qpHelper :: "+qpHelper);
		  double per=36.0;
		  
		  try {
		  
		  if (file != null) { 
			  System.out.println("check 1");
		 
		  when(qpHelper.readAllContaint(file)).thenReturn(userQP); 
//		  when(qpHelperMock.readAllContaint(file)).thenReturn(userQP);
		 System.out.println("check 2"); 
		 } 
		  
		  when(asgdao.getCurrentLiveCycle()).thenReturn(examorderbean);
		  when(asgdao.getLastFiveCycle(28.00, 30.00)).thenReturn(lstFiveCycleList);
		  when(asgdao.getSubjectCidId(masterkeyList, subject,firstcycle.getYear(),firstcycle.getMonth())).thenReturn(subjectcodId).thenReturn(subjectcodId);
		  when(asgdao.getQPS("Jun", "2023",subject,subjectcodId.get(0))).thenReturn(filePathPrivew).thenReturn(filePathPrivew).thenReturn(filePathPrivew).thenReturn(filePathPrivew);
		  List<String> list=qpHelper1.readAllContaint("https://assignment-files.s3.ap-south-1.amazonaws.com/Jun2023/Business_Communication_-_June_2023_Revised_vsudNRFVsS.pdf");
		  List<String> list1=qpHelper1.readAllContaint("https://assignment-files.s3.ap-south-1.amazonaws.com/Apr2023/Business_Communication_-_Assignment_April_2023_UOd8pHtjMZ.pdf");
		  when(qpHelper.readAllContaint("https://assignment-files.s3.ap-south-1.amazonaws.com/Jun2023/Business_Communication_-_June_2023_Revised_vsudNRFVsS.pdf"))
		    .thenReturn(list)
		    .thenReturn(list)
		    .thenReturn(list1)
		    .thenReturn(Collections.emptyList());
		  
		  System.out.println("--list --::"+list);
		  allFileContentsList.add(list1);
		  double matching= qpHelper1.compareQPCopyCase(0, allFileContentsList,firstcycle.getYear(),firstcycle.getMonth());
		  double actual=matching;
//		  when(qpHelper.compareQPCopyCase(anyInt(), any(), anyString(), anyString()))
//		    .thenReturn(matching);
		  
		  when(qpHelper.compareQPCopyCase(anyInt(), any(), anyString(), anyString()))
		    .thenReturn(00.0)  // Return value for the first call
		    .thenReturn(00.0)  // Return value for the second call
		    .thenReturn(matching)  // Return value for the third call
		    .thenReturn(00.0);  // Return value for subsequent calls
		  
//		  when(qpHelperMock.compareQPCopyCase(0,allFileContentsList,firstcycle.getYear(),firstcycle.getMonth())).thenReturn(36.0);
		  List<ResultDomain> resultDomainBeanList=null; 
		  List<String> masterKeys=new ArrayList<String>();
		  masterKeys.add("83"); 
		  masterKeys.add("82");
		  masterKeys.add("80"); 
		  masterKeys.add("81");
		  System.out.println("File containt ::"+file.getBytes());
		  
		  resultDomainBeanList=asgService.checkQPCopyCase("Business Communication",file, masterKeys);
		  
			
		  boolean expected=false; 
		  
		  AtomicReference<Double> expectedmatchingPercentage = new AtomicReference<>(0.0);

		  resultDomainBeanList.stream().forEach(x -> {
		      if (x.getMonth().equalsIgnoreCase("Apr") && x.getYear().equalsIgnoreCase("2023")) {
		          System.out.println("cycle :: " + x.getMonth() + " " + x.getYear());
		          System.out.println("Percentage :: " + x.getMatching());
		          expectedmatchingPercentage.set(x.getMatching());
		      }
		  });

		  // Access the updated value outside the lambda expression
		  double expectedValue = expectedmatchingPercentage.get();
		  double delta = 0.1;
		 
		  if(expectedValue==actual) {
			  expected=true;
		  }


			if (!resultDomainBeanList.isEmpty()) { 
				
				assertEquals(true, expected);
			}
		}

		catch (Exception e) { // TODO: handle exception e.printStackTrace(); }
			System.out.println("Execption is :: " + e.getMessage());
			//e.printStackTrace();
			// TODO: handle exception }

		}


	  }
	  
	  


 }



