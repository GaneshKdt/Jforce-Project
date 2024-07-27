package com.nmims.listeners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletConfigAware;

import com.nmims.beans.ResultDomain;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.daos.TestDAO;
import com.nmims.daos.TestDAOForRedis;
import com.nmims.helpers.IATestHelper;
import com.nmims.helpers.MailSender;
import com.nmims.services.LostFocusCopyCaseService;
import com.nmims.services.MBAXIACopyCaseService;

@Component
public class TestIAScheduler 
implements ApplicationContextAware, ServletConfigAware
{

	@Value( "${ASSIGNMENT_FILES_PATH}" )
	private String ASSIGNMENT_FILES_PATH;
	
	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	@Value( "${IA_ASSIGNMENT_FILES_PATH}" )
	private String IA_ASSIGNMENT_FILES_PATH;
	
	@Autowired
	LostFocusCopyCaseService lostFocusService;

	@Autowired
	MBAXIACopyCaseService mbaxIACopyCaseService;
	
	private static ApplicationContext act = null;
	private static ServletConfig sc = null;

	private static final String MARK_COPYCASE_STATUS = "CopyCase";
	private static final Logger logger = LoggerFactory.getLogger("plagiarism");
	
	@Override
	public void setServletConfig(ServletConfig sc) {
		this.sc = sc;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.act = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return act;
	}
	
	private final int  MIN_LENGTH_FOR_COMPARISION = 20;

	
	@Scheduled(fixedDelay=15*60*1000)
	public void hitApiToMoveRedisTestAnswersToDB(){
		if(!"tomcat4".equalsIgnoreCase(SERVER) ||
		     !"PROD".equalsIgnoreCase(ENVIRONMENT)){ 
			 return; 
			 
		 }
		  
		TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
		daoForRedis.hitApiToMoveRedisTestAnswersToDB();
	}	  
	
	@Scheduled(fixedDelay=15*60*1000)
	public void runPlagiarismCheckForTestDescriptiveAnswers(){

		if(!"tomcat4".equalsIgnoreCase(SERVER) ||
			!"PROD".equalsIgnoreCase(ENVIRONMENT)){ 
			System.out.println("Not running TestIAScheduler : runPlagiarismCheckForTestDescriptiveAnswers scheduler  since this is not tomcat4. This is "+SERVER);
			logger.info("Not running TestIAScheduler : runPlagiarismCheckForTestDescriptiveAnswers scheduler  since this is not tomcat4. This is "+SERVER);
			return; 
		}

		logger.info("in runPlagiarismCheckForTestDescriptiveAnswers starting plagirism check");
		
		try {

			TestDAO dao = (TestDAO)act.getBean("testDao");
			
			//1. get tests for checking 
			List<TestExamBean> testsApplicableForPlagiarismCheck = dao.getTestsApplicableForPlagiarismCheck();

			//2. get all answers for each test
			//testsApplicableForPlagiarismCheck.forEach(test -> {

			for(TestExamBean test : testsApplicableForPlagiarismCheck) {
				
				logger.info("in runPlagiarismCheckForTestDescriptiveAnswers checking for testid : "+test.getId());
				
				List<StudentQuestionResponseExamBean> answers = new ArrayList<>();
				
				if("Assignment".equalsIgnoreCase(test.getTestType()) || "Project".equalsIgnoreCase(test.getTestType())) {
					answers = getAnswerInTextFromPDF(test.getId(),dao);
				}else {
					List<StudentQuestionResponseExamBean> dqAnswerList = dao.getDescriptiveAnswersForPlagiarismCheckByTestId(test.getId());		//list containing students descriptive answer URLs
					answers = getDescriptiveAnswerPdfInTextFormat(dqAnswerList);								//extracted text from the descriptive answer PDF is stored as student answer
				}
				
				String checkForPlagiarismError = checkForPlagiarismForTest(test,answers); 
				
				if(StringUtils.isBlank(checkForPlagiarismError)) {
					logger.info("in runPlagiarismCheckForTestDescriptiveAnswers setting plagirism check done for testid : "+test.getId());
					String setIsPlagiarismCheckDoneToYByTestIdError = dao.setIsPlagiarismCheckDoneToYByTestId(test.getId());
					if(!StringUtils.isBlank(setIsPlagiarismCheckDoneToYByTestIdError)) {
						//sned setIsPlagiarismCheckDoneToYByTestIdError mail
					}
				}else {
					//send checkForPlagiarismError mail
				}

				/*
				 * Added method to mark lost focus copy case
				 * */
				System.out.println("calling markLostFocusCopyCaseBatchJob got testId: "+test.getId());
				if( "Y".equals( test.getProctoringEnabled() ) )
					lostFocusService.markCopyCaseBatchJob( test );
			}

			logger.info("in runPlagiarismCheckForTestDescriptiveAnswers plagirism check done");
			//3. compare for plagiarism 
			
			//4. set flag for plagiarismCheck to true
			
			try {
				mbaxIACopyCaseService.runPlagiarismCheckForTestDescriptiveAnswers();
			}catch (Exception e) {
				// TODO: handle exception
				logger.info("in runPlagiarismCheckForTestDescriptiveAnswers got exception : "+ExceptionUtils.getFullStackTrace(e));
			}

			System.out.println("copyCaseBatchJob completed");
		}catch (Exception e) {
			logger.info("in runPlagiarismCheckForTestDescriptiveAnswers got exception : "+ExceptionUtils.getFullStackTrace(e));
		}
	}

	private List<StudentQuestionResponseExamBean> getAnswerInTextFromPDF(Long id, TestDAO dao) {
		List<StudentQuestionResponseExamBean> answersWithPDFLink = dao.getPDFAnswersForPlagiarismCheckByTestId(id); 
		System.out.println("IN getAnswerInTextFromPDF answersWithPDFLink size : "+answersWithPDFLink.size());
		List<StudentQuestionResponseExamBean> answers = new ArrayList<>();
		
		for(StudentQuestionResponseExamBean answerWithPDFLink  :answersWithPDFLink ) {
			try {
				String pdfFilePath = answerWithPDFLink.getAnswer();
				answerWithPDFLink.setFileLink(pdfFilePath);
				if(pdfFilePath.contains(".pdf") || pdfFilePath.contains(".PDF")  ) {

					URL url = new URL(answerWithPDFLink.getAnswer());
					String tempPDFFilePath = IA_ASSIGNMENT_FILES_PATH + "CopyCases/tempPDFForCopyCase.pdf";
					
					File pdfFile = new File(tempPDFFilePath);
					FileUtils.copyURLToFile(url, pdfFile);

					 System.out.println("IN getAnswerInTextFromPDF() : ");
					 System.out.println(" pdfFile :"+pdfFile.getName());

					String answerInTextFromPDFFile = getAnswerInTextFromPDFFile(pdfFile);
					answerWithPDFLink.setAnswer(answerInTextFromPDFFile.replaceAll("[^a-zA-Z0-9:;.?! ]",""));
					
				}
				answers.add(answerWithPDFLink);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				
			}  
			
		}
		
		return answers;
	}

	private String getAnswerInTextFromPDFFile(File pdfFile) {
		List<String> ans = new ArrayList<>();
		PDFTextStripper stripper;
		PDDocument doc = null;
		//List<String> newOne = new ArrayList<>();
		String allFileContent = "";
		StringBuffer  stringBuffer = new StringBuffer (); 
		try {
			doc = PDDocument.load(pdfFile);
			int count = doc.getNumberOfPages();
			stripper = new PDFTextStripper();
			stripper.setStartPage(1);
			stripper.setEndPage(count);
			String x1 = stripper.getText(doc);
			// break up the file content returned as a string into individual
			// lines

			ans = Arrays.asList(x1.split(stripper.getLineSeparator()));

			for (String s : ans) {
				s = s.trim();
				if (StringUtils.isBlank(s) || StringUtils.isEmpty(s)) {

				} else {
					//s = s.replaceAll("\\s+", " ");
					//s = s.replaceAll("\\p{C}", " ");
					//Join all lines of pdf
					stringBuffer.append(s);
				}
			}

		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			
			return "";
		}catch (IOException e) {
			// TODO Auto-generated catch block
			
			return "";
		}catch (Exception e) {
			// TODO Auto-generated catch block
			
			return "";
		} finally {
			if (doc != null)
				try {
					doc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
				}
		}

		return stringBuffer.toString();

	}

	private String checkForPlagiarismForTest(TestExamBean test, List<StudentQuestionResponseExamBean> answers) {
		

		TestDAO dao = (TestDAO)act.getBean("testDao");
		MailSender mailSender = (MailSender)act.getBean("mailer");
		
		List<ResultDomain> copyResultList = new ArrayList<>();
		HashMap<String, String> studentWithCopyAboveThreshold = new HashMap<>();
		HashMap<String,String> studentsAbove90 = new HashMap<>();
		HashMap<String,String> studentsBetween80And90 = new HashMap<String,String>();
		
		for(int i = 0; i < answers.size(); i++) {
			StudentQuestionResponseExamBean currentAnswerStudentDetails = answers.get(i);
			String currentAnswersToBeChecked = currentAnswerStudentDetails.getAnswer();
			
			if(StringUtils.isBlank(currentAnswersToBeChecked)) {
				continue;
			}
			
			double copyCaseThreshold = (double)currentAnswerStudentDetails.getCopyCaseThreshold();
			
			List<String> currentAnswersToBeCheckedArray =  splitStringByFullstopOrQuestionMark(currentAnswersToBeChecked);
			int noOfLinesInCurrentAnswer = currentAnswersToBeCheckedArray.size();
			
			
			for(int j = i+1; j < answers.size(); j++) {

				StudentQuestionResponseExamBean secondAnswerStudentDetails = answers.get(j);
				
				if(currentAnswerStudentDetails.getSapid().equals(secondAnswerStudentDetails.getSapid())) {
					continue;
				}
				
				String secondAnswersToBeChecked = secondAnswerStudentDetails.getAnswer();
				
				List<String> secondAnswersToBeCheckedArray =  splitStringByFullstopOrQuestionMark(secondAnswersToBeChecked);
				int maxConseutiveLinesMatched = 0;
				int consecutiveMatchingLinesCounter = 0;
				
				long comparisionStartTime = System.currentTimeMillis();

				int blankLines = 0;
				int noOfMatches = 0;

				
				//Store lines matched, so that they are not considered for match again
				ArrayList<String> linesMatchedInFirstFile = new ArrayList<String>();
				ArrayList<String> linesMatchedInSecondFile = new ArrayList<String>();
				
				int noOfLinesInSecondAnswer = secondAnswersToBeCheckedArray.size();
				
				for(String currentAnswersToBeCheckedLine : currentAnswersToBeCheckedArray) {
					
					boolean matched = false;
					
					for(String secondAnswersToBeCheckedLine : secondAnswersToBeCheckedArray) {

						if (!linesMatchedInSecondFile.contains(secondAnswersToBeCheckedLine) && currentAnswersToBeCheckedLine.contains(secondAnswersToBeCheckedLine)) {
							matched = true;
							noOfMatches++;
							linesMatchedInSecondFile.add(secondAnswersToBeCheckedLine);//This line should not be considered for next time
							
							break;
						} else if (!linesMatchedInFirstFile.contains(currentAnswersToBeCheckedLine) && secondAnswersToBeCheckedLine.contains(currentAnswersToBeCheckedLine)) {
							matched = true;
							noOfMatches++;
							linesMatchedInFirstFile.add(currentAnswersToBeCheckedLine);//This line should not be considered for next time
							

							break;
						}
						
					}
					
					if(matched){
						consecutiveMatchingLinesCounter++;
						if(maxConseutiveLinesMatched < consecutiveMatchingLinesCounter){
							maxConseutiveLinesMatched = consecutiveMatchingLinesCounter;
						}
					}else{
						consecutiveMatchingLinesCounter = 0;
					}
					
				}
				

				int totalStringLength = noOfLinesInSecondAnswer > noOfLinesInCurrentAnswer 
										? noOfLinesInSecondAnswer
										: noOfLinesInCurrentAnswer;
				
				
				double matching = (noOfMatches / (double) totalStringLength) * 100;

				long comparisionEndTime = System.currentTimeMillis();


				if(matching >= copyCaseThreshold){
					String firstSapId = currentAnswerStudentDetails.getSapid();
					String secondSapId = secondAnswerStudentDetails.getSapid();
					
					ResultDomain copyResult = new ResultDomain();
					copyResult.setTestId(test.getId());
					copyResult.setQuestionId(currentAnswerStudentDetails.getQuestionId());
					copyResult.setNoOfMatches(noOfMatches);
					copyResult.setMatching(matching);
					copyResult.totalStringLength = totalStringLength;
					copyResult.blankLines = blankLines;
					
					copyResult.setFirstTestDescriptiveAnswer(currentAnswerStudentDetails.getAnswer());
					copyResult.setSecondTestDescriptiveAnswer(secondAnswerStudentDetails.getAnswer());

					copyResult.setFirstSapidTestAnswerCreatedDate(currentAnswerStudentDetails.getCreatedDate());
					copyResult.setSecondSapidTestAnswerCreatedDate(secondAnswerStudentDetails.getCreatedDate());
					
					
					copyResult.setSubject(test.getSubject());
					copyResult.setMaxConseutiveLinesMatched(maxConseutiveLinesMatched);
					copyResult.setNumberOfLinesInFirstFile(noOfLinesInCurrentAnswer);
					copyResult.setNumberOfLinesInSecondFile(noOfLinesInSecondAnswer);
					copyResult.setNoOfMatches(noOfMatches);

					copyResult.setSapId1(firstSapId);
					copyResult.setFirstName1(currentAnswerStudentDetails.getFirstName());
					copyResult.setLastName1(currentAnswerStudentDetails.getLastName());

					copyResult.setSapId2(secondSapId);
					copyResult.setFirstName2(secondAnswerStudentDetails.getFirstName());
					copyResult.setLastName2(secondAnswerStudentDetails.getLastName());
					copyResult.setMatchingFor80to90("No");
					
					if(matching >= 70.00 && matching<=84.99){
						
						studentsBetween80And90.put(firstSapId, null);
						studentsBetween80And90.put(secondSapId, null);
						copyResult.setMatchingFor80to90("Yes");
					}
					
					copyResultList.add(copyResult);
					
					studentWithCopyAboveThreshold.put(firstSapId, null);
					studentWithCopyAboveThreshold.put(secondSapId, null);
					
					if(matching >= 85.00){
						studentsAbove90.put(firstSapId, null);
						studentsAbove90.put(secondSapId, null);
					}

					//Add remark in answer set evaluated to true end
				
					//save copycase entry in test_copycase start
					copyResult.setMarkedForCopyCase("Not Marked For CopyCase.");
					String saveCopyCaseEntryError = dao.saveCopyCaseEntryInTable(copyResult); 
					if(!StringUtils.isBlank(saveCopyCaseEntryError) ) {
						//send email of stacktrace
						mailSender.mailStackTraceForIATestCopyCaseError(
						ENVIRONMENT+" : Error in saveCopyCaseEntryError ",
						saveCopyCaseEntryError+". \n<br> ");
						}
					
					//save copycase entry in test_copycase end
					
				}
				noOfMatches = 0;
				
			}
			
		}
		saveToDeskAndEmailFile(copyResultList,answers,test,studentWithCopyAboveThreshold,studentsAbove90);
		return "";
	}

	@Async
	private void saveToDeskAndEmailFile(List<ResultDomain> copyResultList,	
							List<StudentQuestionResponseExamBean> answers,
							TestExamBean test,
							//AssignmentFileBean searchBean, 
							HashMap<String, String> studentWithCopyAboveThreshold, 
							HashMap<String,String> studentsAboveNinety) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Copy Result");
		int rownum = 0;
		int cellnum = 0;
		Row row = sheet.createRow(rownum++);
		
		Cell cell = row.createCell(cellnum++);
		cell.setCellValue("Sr. No.");

		cell = row.createCell(cellnum++);
		cell.setCellValue("Subject");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Student ID 1");

		cell = row.createCell(cellnum++);
		cell.setCellValue("Student 1 First Name ");

		cell = row.createCell(cellnum++);
		cell.setCellValue("Student 1 Last Name ");

		cell = row.createCell(cellnum++);
		cell.setCellValue("Student ID 2");

		cell = row.createCell(cellnum++);
		cell.setCellValue("Student 2 First Name ");

		cell = row.createCell(cellnum++);
		cell.setCellValue("Student 2 Last Name ");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Matching %");
		
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Max Consecutive Lines Matched");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("# of Lines in First Answer");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("# of Lines in Second Answer");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("# of Lines matched");

		cell = row.createCell(cellnum++);
		cell.setCellValue("Sapid1 Answered DateTime");
		
		cell = row.createCell(cellnum++);
		cell.setCellValue("Sapid2 Answered DateTime");
		
		for (ResultDomain copyResult : copyResultList) {
			cellnum = 0;
			row = sheet.createRow(rownum++);
			cell = row.createCell(cellnum++);
			cell.setCellValue(rownum-1);
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getSubject());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getSapId1());
			

			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getFirstName1());

			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getLastName1());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getSapId2());
			

			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getFirstName2());

			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getLastName2());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getMatching());
			
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getMaxConseutiveLinesMatched());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getNumberOfLinesInFirstFile());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getNumberOfLinesInSecondFile());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getNoOfMatches());
			
			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getFirstSapidTestAnswerCreatedDate());

			cell = row.createCell(cellnum++);
			cell.setCellValue(copyResult.getSecondSapidTestAnswerCreatedDate());

		}
		
		try {
			String fileName = test.getTestName().substring(0,11).replaceAll(" ", "_")
							  .replaceAll(":", "")
							  .replaceAll("\\\\", "")
							  .replaceAll("/", "")
							  .replaceAll("\\*", "")
							  .replaceAll("\\?", "")
							  .replaceAll("\t", "")
							  .replaceAll("<", "")
							  .replaceAll(">", "")
							  .replaceAll("|", "")+ "_Copy_Result.xlsx";
			
			String folderName = IA_ASSIGNMENT_FILES_PATH + "CopyCases/"+ test.getMonth() + test.getYear()+ "/";
			
			String filePath = folderName +fileName;

			File folder = new File(folderName);
			if (!folder.exists()) {   
				folder.mkdirs();   
			} 
			FileOutputStream out = new FileOutputStream(new File(filePath));
			workbook.write(out);
			out.close();
			

			MailSender mailSender = (MailSender)act.getBean("mailer");

			String[] emailIds = {"NGASCE.Exams@nmims.edu","Jigna.Patel@nmims.edu","nashrah.shaikh@nmims.edu",
					"harshalee.ullal@nmims.edu","pooja.jadhav@nmims.edu","christopher.kevin@nmims.edu","khatija.shaikh@nmims.edu","Ankita.Parmar@nmims.edu","laxmi.raaj@nmims.edu", "aziz.merchant@nmims.edu"}; 
			
			mailSender.sendIATestCopyCaseReport("CopyCase Report for "+test.getTestName(), fileName, folderName,emailIds );
			
		} catch (Exception e) {
			logger.info("in runPlagiarismCheckForTestDescriptiveAnswers got exception in saveToDeskAndEmailFile : "+ExceptionUtils.getFullStackTrace(e));
		}
	}

	List<String>  splitStringByFullstopOrQuestionMark( String stringToBeSplit )  {
		
		//Split lines by full stop or by question.
		String[] splits = stringToBeSplit.split("\\.|\\?|\\:");

		List<String> returnArray = new ArrayList<>();

		for (String line : splits) {

			if(line.length() > MIN_LENGTH_FOR_COMPARISION && (!(returnArray.contains(line))) ){
				returnArray.add(line.replaceAll("\\s+","").replaceAll("\\p{Blank}","").trim().toUpperCase());
			}
		}
		return returnArray;
	}
	
	/**
	 * The Descriptive answer of student is extracted from it's source PDF file, the list of extracted student answer text is returned.
	 * @param studentDescriptiveAnswersList - list containing descriptive answer URL of the students
	 * @return list of descriptive answers in text format
	 */
	private List<StudentQuestionResponseExamBean> getDescriptiveAnswerPdfInTextFormat(List<StudentQuestionResponseExamBean> studentDescriptiveAnswersList) {
		logger.info("Extracting text from descriptive answers URL list of size: {}", studentDescriptiveAnswersList.size());
		List<StudentQuestionResponseExamBean> answers = new ArrayList<>();
		
		for(StudentQuestionResponseExamBean studentAnswer: studentDescriptiveAnswersList) {
			try {
				String extractedText = IATestHelper.readPDFText(studentAnswer.getAnswer());						//read text from the PDF source URL
				logger.info("Successfully extracted text of descriptive answer PDF for student: {} with answerId: {}", studentAnswer.getSapid(), studentAnswer.getId());
				
				studentAnswer.setAnswer(extractedText);
				answers.add(studentAnswer);
			}
			catch(Exception ex) {
//				ex.printStackTrace();
				logger.error("Error while extracting text from the descriptive answer file URL: {} of student: {} with answerId: {}, Exception thrown: {}", 
							studentAnswer.getAnswer(), studentAnswer.getSapid(), studentAnswer.getId(), ex.toString());
			}
		}
		
		return answers;
	}
}
