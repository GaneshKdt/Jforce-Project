package com.nmims.services;

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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.nmims.beans.ResultDomain;
import com.nmims.beans.StudentQuestionResponseExamBean;
import com.nmims.beans.TestExamBean;
import com.nmims.daos.MBAXIADAO;
import com.nmims.helpers.MailSender;

@Service("mbaxIACopyCaseService")
public class MBAXIACopyCaseService {

	@Autowired
	MBAXLostFocusCopyCaseService mbaxLostFocusCopyCaseService;

	@Autowired
	MBAXIADAO mbaxIADao;

	@Autowired
	MailSender mailer;

	private final int  MIN_LENGTH_FOR_COMPARISION = 20;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;

	@Value( "${ASSIGNMENT_FILES_PATH}" )
	private String ASSIGNMENT_FILES_PATH;

	@Value( "${IA_ASSIGNMENT_FILES_PATH}" )
	private String IA_ASSIGNMENT_FILES_PATH;

	public void runPlagiarismCheckForTestDescriptiveAnswers() throws Exception{

		/*
		 * fetch tests applicable for plagiarism check  
		 */
		List<TestExamBean> testsApplicableForPlagiarismCheck = mbaxIADao.getTestsApplicableForPlagiarismCheck();

		/*
		 * get all answers for each test
		 * testsApplicableForPlagiarismCheck.forEach(test -> {
		 */
		
		for(TestExamBean test : testsApplicableForPlagiarismCheck) {

			System.out.println("in runPlagiarismCheckForTestDescriptiveAnswers got testId: "+test.getId());
			
			List<StudentQuestionResponseExamBean> answers = new ArrayList<>();

			if("Assignment".equalsIgnoreCase(test.getTestType())) {
				answers = getAnswerInTextFromPDF(test.getId(), mbaxIADao);
			}else {
				answers = mbaxIADao.getDescriptiveAnswersForPlagiarismCheckByTestId(test.getId()); 
			}

			String checkForPlagiarismError = checkForPlagiarismForTest(test,answers); 

			if(StringUtils.isBlank(checkForPlagiarismError)) {
				
				String setIsPlagiarismCheckDoneToYByTestIdError = mbaxIADao.setIsPlagiarismCheckDoneToYByTestId(test.getId());
				
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
				mbaxLostFocusCopyCaseService.markCopyCaseBatchJob( test );
		}

		System.out.println("copyCaseBatchJob completed");

	}

	public void runCopyCaseForInternalAssessment(Long testId) throws Exception{

		System.out.println("in runCopyCaseForTest got testId: "+testId);
		
		/*
		 * fetch tests applicable for plagiarism check  
		 */
		TestExamBean test = mbaxIADao.getTestsDetailsForPlagiarismCheck(testId);

		List<StudentQuestionResponseExamBean> answers = new ArrayList<>();

		if("Assignment".equalsIgnoreCase(test.getTestType())) {
			answers = getAnswerInTextFromPDF(test.getId(), mbaxIADao);
		}else {
			answers = mbaxIADao.getDescriptiveAnswersForPlagiarismCheckByTestId(test.getId()); 
		}

		String checkForPlagiarismError = checkForPlagiarismForTest(test,answers); 

		if(StringUtils.isBlank(checkForPlagiarismError)) {

			String setIsPlagiarismCheckDoneToYByTestIdError = mbaxIADao.setIsPlagiarismCheckDoneToYByTestId(test.getId());

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
			mbaxLostFocusCopyCaseService.markCopyCaseBatchJob( test );

		System.out.println("copyCaseBatchJob completed");

	}

	private List<StudentQuestionResponseExamBean> getAnswerInTextFromPDF( Long id, MBAXIADAO dao ) {

		List<StudentQuestionResponseExamBean> answersWithPDFLink = dao.getPDFAnswersForPlagiarismCheckByTestId(id); 
		List<StudentQuestionResponseExamBean> answers = new ArrayList<>();

		System.out.println("IN getAnswerInTextFromPDF answersWithPDFLink size : "+answersWithPDFLink.size());

		for(StudentQuestionResponseExamBean answerWithPDFLink  :answersWithPDFLink ) {

			try {

				String pdfFilePath = answerWithPDFLink.getAnswer();
				answerWithPDFLink.setFileLink(pdfFilePath);

				if(pdfFilePath.contains(".pdf") || pdfFilePath.contains(".PDF")  ) {

					URL url = new URL(answerWithPDFLink.getAnswer());
					String tempPDFFilePath = IA_ASSIGNMENT_FILES_PATH + "CopyCases/tempPDFForCopyCase.pdf";

					File pdfFile = new File(tempPDFFilePath);
					FileUtils.copyURLToFile(url, pdfFile);

					System.out.println("IN getAnswerInTextFromPDF() got pdfFile :"+pdfFile.getName());

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
		String allFileContent = "";
		StringBuffer  stringBuffer = new StringBuffer (); 

		try {

			doc = PDDocument.load(pdfFile);
			int count = doc.getNumberOfPages();
			stripper = new PDFTextStripper();
			stripper.setStartPage(1);
			stripper.setEndPage(count);
			String x1 = stripper.getText(doc);

			/* 
			 * break up the file content returned as a 
			 * string into individual lines
			 */

			ans = Arrays.asList(x1.split(stripper.getLineSeparator()));

			for (String s : ans) {
				s = s.trim();
				if (StringUtils.isBlank(s) || StringUtils.isEmpty(s)) {

				} else {

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

				if(currentAnswerStudentDetails.getSapid() == secondAnswerStudentDetails.getSapid()) {
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

					//save copy case entry in test_copycase start
					copyResult.setMarkedForCopyCase("Not Marked For CopyCase.");
					
					String saveCopyCaseEntryError = mbaxIADao.saveCopyCaseEntryInTable(copyResult); 
					
					if(!StringUtils.isBlank(saveCopyCaseEntryError) ) {
						//send email of stacktrace
						mailer.mailStackTraceForIATestCopyCaseError(
								ENVIRONMENT+" : Error in saveCopyCaseEntryError ", saveCopyCaseEntryError+". \n<br> ");
					}

					//save copy case entry in test_copycase end
					
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
			String fileName = test.getTestName().substring(0,20).replaceAll(" ", "_")
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

			String[] emailIds = {"NGASCE.Exams@nmims.edu","Jigna.Patel@nmims.edu","nashrah.shaikh@nmims.edu",
					"harshalee.ullal@nmims.edu","pooja.jadhav@nmims.edu","christopher.kevin@nmims.edu","khatija.shaikh@nmims.edu","Ankita.Parmar@nmims.edu","laxmi.raaj@nmims.edu","aziz.merchant@nmims.edu"}; 
			/*
			 * use while testing 
			 * String[] emailIds = {"jforcesolution@gmail.com"}; 
			 */
			mailer.sendIATestCopyCaseReport("CopyCase Report for "+test.getTestName(), fileName, folderName,emailIds );


		} catch (Exception e) {

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

}
