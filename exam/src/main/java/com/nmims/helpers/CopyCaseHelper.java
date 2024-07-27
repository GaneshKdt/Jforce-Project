package com.nmims.helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ResultDomain;
import com.nmims.daos.AssignmentsDAO;

@Component
public class CopyCaseHelper {

	@Autowired
	@Qualifier("asignmentsDAO")
	AssignmentsDAO dao;
	
	
	@Value( "${ASSIGNMENT_FILES_PATH}" )
	private String ASSIGNMENT_FILES_PATH;
	
	@Value( "${ASSIGNMENT_PREVIEW_PATH}" )
	private String ASSIGNMENT_PREVIEW_PATH;
	 
	@Value( "${ASSIGNMENT_REPORTS_GENERATE_PATH}" )
	private String ASSIGNMENT_REPORTS_GENERATE_PATH;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
//	@Value( "${LOCAL_ACTIVE_PROCESSORS}" )
//	private int LOCAL_ACTIVE_PROCESSORS;

	private final int  MIN_LENGTH_FOR_COMPARISION = 30;
	private static final Logger logger = LoggerFactory.getLogger(CopyCaseHelper.class);
	private static final Logger assignmentCCLogger = LoggerFactory.getLogger("assignmentCopyCase");
	private static final Logger projectCCLogger = LoggerFactory.getLogger("projectCopyCase");
	private static final Logger copyLeaksControllerLogger = LoggerFactory.getLogger("assignmentWebCopyCase");
	Executor executor = Executors.newFixedThreadPool(8);

	//@Async
	public void checkAssignmentCopyCases(List<AssignmentFileBean> assignmentFilesList, AssignmentFileBean searchBean, 
			List<AssignmentFileBean> assignmentQuestionFilesList) throws Exception{

		ArrayList<List<String>> allFileContentsList = new ArrayList<>();
		
		//Read and compare with all QP files of different masterkeys of the subject
		ArrayList<String> allQuestionFileContent = new ArrayList<>();
		int count_QP_File=1;
		for (AssignmentFileBean questionFileBean : assignmentQuestionFilesList) {
			assignmentCCLogger.info("QP file count:{}/{} || Masterkey:{} || QPFilePath:{}",
					 count_QP_File, assignmentQuestionFilesList.size(), questionFileBean.getConsumerProgramStructureId(), ASSIGNMENT_FILES_PATH + questionFileBean.getQuestionFilePreviewPath());
			ArrayList<String> questionFileContent = (ArrayList<String>) readLineByLine(null,ASSIGNMENT_FILES_PATH + questionFileBean.getQuestionFilePreviewPath(),new ArrayList<String>());
			//combine all QP file content to compare with student file content line by line
			allQuestionFileContent.addAll(questionFileContent);
			count_QP_File++;
		}
		
		// qn file content seperated line by line trimming space
		int count = 1;
		for (AssignmentFileBean assignmentFileBean : assignmentFilesList) {
			assignmentFileBean.setStudentFilePath(ASSIGNMENT_PREVIEW_PATH + assignmentFileBean.getPreviewPath());
			assignmentCCLogger.info("Reading file Count:{}/{} || StudentFilePathURL:{}",count, assignmentFilesList.size(), assignmentFileBean.getStudentFilePath());
			long readStartTime = System.currentTimeMillis();
			List<String> fileContent = readLineByLine(null, assignmentFileBean.getStudentFilePath(),allQuestionFileContent);
			long readEndTime = System.currentTimeMillis();
			assignmentCCLogger.info("Time taken to read file = " + (readEndTime - readStartTime));
			if (fileContent != null) {
				allFileContentsList.add(fileContent);
			} else {
				assignmentCCLogger.info("Student uploaded QP file: {}",assignmentFileBean.getStudentFilePath());
				allFileContentsList.add(new ArrayList<String>());
			}
			count++;
		}
		
		// Multi threading copy case process
		copyCaseProcess(assignmentFilesList, searchBean, allFileContentsList, assignmentCCLogger);
	}



	// Copy Case proccess by multi-threading
	public void copyCaseProcess(List<AssignmentFileBean> assignmentFilesList, AssignmentFileBean searchBean,
			ArrayList<List<String>> allFileContentsList, Logger copyCaseLogger) throws InterruptedException, ExecutionException {
		//all answers as array of strings
		HashMap<String, String> studentWithCopyAboveThreshold = new HashMap<>();
		HashMap<String, String> studentWithCopyAboveThreshold2 = new HashMap<>();

		HashMap<String,String> studentsAbove90 = new HashMap<>();
		HashMap<String,String> studentsBetween80And90 = new HashMap<String,String>();
		

		ArrayList<ResultDomain> copyResultList = new ArrayList<>();
		ArrayList<ResultDomain> copyResultList2 = new ArrayList<>();
//		List<String> firstPdfTemp = allFileContentsList.get(0);
//		List<String> secondPdfTemp = allFileContentsList.get(1);
		
		
//		for (String line : firstPdfTemp) {
//		}
//		
//		
//		for (String line : secondPdfTemp) {
//		}
		List<CompletableFuture<String>> pageContentFutures = 
			      
		    	  
				IntStream.range(0, allFileContentsList.size())

			    .mapToObj(index -> 
			    		compareCopyCase(
							index,
							allFileContentsList,
							assignmentFilesList,
							searchBean, 
							studentsAbove90, 
							studentsBetween80And90, 
							copyResultList,
							studentWithCopyAboveThreshold,
							copyResultList2,
							studentWithCopyAboveThreshold2,
							copyCaseLogger)
		        		)
		        	//compareCopyCase(current_file_index, allFileContentsList, assignmentFilesList, firstPdf, numberOfLinesInFirstFile, searchBean, currentStudentAssignment,
		        
				//		studentsAbove90,studentsBetween80And90,copyResultList, studentWithCopyAboveThreshold))
		        .collect(Collectors.toList());
		// Create a combined Future using allOf()
		CompletableFuture<Void> allFutures = CompletableFuture.allOf(
		        pageContentFutures.toArray(new CompletableFuture[pageContentFutures.size()])
		);
		
		CompletableFuture<List<String>> allPageContentsFuture = allFutures.thenApply(v -> {
		   return pageContentFutures.stream()
		           .map(pageContentFuture -> pageContentFuture.join())
		           .collect(Collectors.toList());
		});

		        
		// Count the number of web pages having the "CompletableFuture" keyword.
		CompletableFuture<Long> countFuture = allPageContentsFuture.thenApply(pageContents -> {
			
			
			copyCaseLogger.info("---------------------------------Saving CC details... Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}---------------------------------",searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
			saveToDesk(copyResultList,searchBean, studentWithCopyAboveThreshold, assignmentFilesList,studentsAbove90, studentWithCopyAboveThreshold2, copyCaseLogger);
			copyCaseLogger.info("---------------------------------Done saving CC details... Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}---------------------------------",searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
			//return copyResultList;
			
			
		    return pageContents.stream()
		            .filter(pageContent -> pageContent.contains("CompletableFuture"))
		            .count();
		});

		System.out.println("Number of Web Pages having CompletableFuture keyword - " + 
		        countFuture.get());
	
		copyCaseLogger.info("--------------------CopyCase Algorithm Ended Subject:{} || Month-Year:{}-{} || Threshold 1-Threshold 2: {}-{}--------------------", searchBean.getSubject(), searchBean.getMonth(), searchBean.getYear(), searchBean.getMinMatchPercent(), searchBean.getThreshold2());
	}
	
	
	
	
	CompletableFuture<String> compareCopyCase(
			int firstIndex,
			ArrayList<List<String>> allFileContentsList,
			List<AssignmentFileBean> assignmentFilesList,
			AssignmentFileBean searchBean, 
			HashMap<String,String> studentsAbove90, 
			HashMap<String,String> studentsBetween80And90, 
			ArrayList<ResultDomain> copyResultList,
			HashMap<String, String> studentWithCopyAboveThreshold,
			ArrayList<ResultDomain> copyResultList2,
			HashMap<String, String> studentWithCopyAboveThreshold2,
			Logger copyCaseLogger) {
//		Executor executor = Executors.newFixedThreadPool(LOCAL_ACTIVE_PROCESSORS); 
		return CompletableFuture.supplyAsync(() -> {
			
	       	 final int current_file_index = firstIndex;
	       	copyCaseLogger.info("comparing file no  "+firstIndex + " subject - "+searchBean.getSubject()+" ");
			AssignmentFileBean currentStudentAssignment = assignmentFilesList.get(firstIndex);
			//List<String> firstPdf = readLineByLine(null, currentStudentAssignment.getStudentFilePath());
			List<String> firstPdf = allFileContentsList.get(firstIndex);
			int numberOfLinesInFirstFile = firstPdf.size();
			
			// Code to download and return the web page's content
			for (int secondIndex = firstIndex+1; secondIndex < allFileContentsList.size(); secondIndex++) {
				int maxConseutiveLinesMatched = 0;
				int consecutiveMatchingLinesCounter = 0;
				
				//assignmentCCLogger.info("Comparing File " + firstIndex + " with File " + secondIndex);
				AssignmentFileBean otherStudentAssignment = assignmentFilesList.get(secondIndex);
				//List<String> secondPdf = readLineByLine(null, otherStudentAssignment.getStudentFilePath());
				List<String> secondPdf = allFileContentsList.get(secondIndex);
			//	long comparisionStartTime = System.currentTimeMillis();

				int blankLines = 0;
				int noOfMatches = 0;

				int numberOfLinesInSecondFile = secondPdf.size();
				
				//Store lines matched, so that they are not considered for match again
				ArrayList<String> linesMatchedInFirstFile = new ArrayList<String>();
				ArrayList<String> linesMatchedInSecondFile = new ArrayList<String>();
				for (int p = 0; p < firstPdf.size(); p++) {

					///String firstLine = StringUtils.trimToEmpty(firstPdf.get(p).replaceAll("\\s+", ""));
					String firstFileLine="";
					if(firstPdf.get(p)!=null) {
						firstFileLine = firstPdf.get(p);
					}
					//int flen =firstFileLine.length();
					boolean matched = false;
					for (int j = 0; j < secondPdf.size(); j++) {
						// if (p < secondTotalStringLength) {
						///String secondLine = StringUtils.trimToEmpty(secondPdf.get(j).replaceAll("\\s+", ""));
						String secondFileLine="";
						if(secondPdf.get(j)!=null) {
							secondFileLine = secondPdf.get(j);
						}
						/*if(secondLine!=null){
							secondLine=secondLine.trim();
						}*/

						//if (!StringUtils.isWhitespace(firstFileLine) && !StringUtils.isWhitespace(secondFileLine)) {

						///int flen =StringUtils.length(firstLine);
						///int seclen = StringUtils.length(secondLine);


						//int seclen = secondFileLine.length();

						//int max = flen>seclen?flen:seclen;
						//int dd = flen>seclen?flen-seclen:seclen-flen;
						//double dff = (dd/(double)max)*100;

						//if(dff<70){
						
						if (!linesMatchedInSecondFile.contains(secondFileLine) && firstFileLine.contains(secondFileLine)) {
							matched = true;
							noOfMatches++;
							linesMatchedInSecondFile.add(secondFileLine);//This line should not be considered for next time
							break;
						} else if (!linesMatchedInFirstFile.contains(firstFileLine) && secondFileLine.contains(firstFileLine)) {
							matched = true;
							noOfMatches++;
							linesMatchedInFirstFile.add(firstFileLine);//This line should not be considered for next time
							break;
						}
						//}
						//}
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
				int totalStringLength = numberOfLinesInSecondFile > numberOfLinesInFirstFile ? numberOfLinesInSecondFile
						: numberOfLinesInFirstFile;
				//double matching = noOfMatches > totalStringLength ? 100	: (noOfMatches / (double) totalStringLength) * 100;
				
				
				double matching = (noOfMatches / (double) totalStringLength) * 100;

				/*if (matching < 50) {
					totalStringLength = totalStringLength - 20;
					matching = (noOfMatches / (double) totalStringLength) * 100;
				}*/

				//long comparisionEndTime = System.currentTimeMillis();
				if(matching > searchBean.getMinMatchPercent()){
					String firstSapId = currentStudentAssignment.getSapId();
					String secondSapId = otherStudentAssignment.getSapId();
					
					ResultDomain copyResult = new ResultDomain();
					copyResult.setNoOfMatches(noOfMatches);
					copyResult.setMatching(matching);
					copyResult.totalStringLength = totalStringLength;
					copyResult.blankLines = blankLines;
					copyResult.firstFile = currentStudentAssignment.getStudentFilePath();
					copyResult.secondFile = otherStudentAssignment.getStudentFilePath();
					
					copyResult.setSubject(currentStudentAssignment.getSubject());
					copyResult.setMaxConseutiveLinesMatched(maxConseutiveLinesMatched);
					copyResult.setNumberOfLinesInFirstFile(numberOfLinesInFirstFile);
					copyResult.setNumberOfLinesInSecondFile(numberOfLinesInSecondFile);
					copyResult.setNoOfMatches(noOfMatches);

					copyResult.setSapId1(firstSapId);
					copyResult.setFirstName1(currentStudentAssignment.getFirstName());
					copyResult.setLastName1(currentStudentAssignment.getLastName());
					copyResult.setProgram1(currentStudentAssignment.getProgram());
					copyResult.setCenterName1(currentStudentAssignment.getCenterName());

					copyResult.setSapId2(secondSapId);
					copyResult.setFirstName2(otherStudentAssignment.getFirstName());
					copyResult.setLastName2(otherStudentAssignment.getLastName());
					copyResult.setProgram2(otherStudentAssignment.getProgram());
					copyResult.setCenterName2(otherStudentAssignment.getCenterName());
					copyResult.setMatchingFor80to90("No");
					
					if(matching >= 80.00 && matching<=89.99){
						
						studentsBetween80And90.put(firstSapId, null);
						studentsBetween80And90.put(secondSapId, null);
						copyResult.setMatchingFor80to90("Yes");
					}
					
					copyResultList.add(copyResult);
					
					studentWithCopyAboveThreshold.put(firstSapId, null);
					studentWithCopyAboveThreshold.put(secondSapId, null);
					
					if(matching > 90.00){
						studentsAbove90.put(firstSapId, null);
						studentsAbove90.put(secondSapId, null);
					}
				

					
				}
				if(searchBean.getThreshold2() != 0) {
					// If threshold 2 is same as threshold 1 then avoid threshold 2 process
					if(searchBean.getMinMatchPercent() != searchBean.getThreshold2()) {
						if(matching > searchBean.getThreshold2()){
							String firstSapId = currentStudentAssignment.getSapId();
							String secondSapId = otherStudentAssignment.getSapId();
							
							ResultDomain copyResult2 = new ResultDomain();
							copyResult2.setNoOfMatches(noOfMatches);
							copyResult2.setMatching(matching);
							copyResult2.totalStringLength = totalStringLength;
							copyResult2.blankLines = blankLines;
							copyResult2.firstFile = currentStudentAssignment.getStudentFilePath();
							copyResult2.secondFile = otherStudentAssignment.getStudentFilePath();
							
							copyResult2.setSubject(currentStudentAssignment.getSubject());
							copyResult2.setMaxConseutiveLinesMatched(maxConseutiveLinesMatched);
							copyResult2.setNumberOfLinesInFirstFile(numberOfLinesInFirstFile);
							copyResult2.setNumberOfLinesInSecondFile(numberOfLinesInSecondFile);
							copyResult2.setNoOfMatches(noOfMatches);
	
							copyResult2.setSapId1(firstSapId);
							copyResult2.setFirstName1(currentStudentAssignment.getFirstName());
							copyResult2.setLastName1(currentStudentAssignment.getLastName());
							copyResult2.setProgram1(currentStudentAssignment.getProgram());
							copyResult2.setCenterName1(currentStudentAssignment.getCenterName());
	
							copyResult2.setSapId2(secondSapId);
							copyResult2.setFirstName2(otherStudentAssignment.getFirstName());
							copyResult2.setLastName2(otherStudentAssignment.getLastName());
							copyResult2.setProgram2(otherStudentAssignment.getProgram());
							copyResult2.setCenterName2(otherStudentAssignment.getCenterName());
							copyResult2.setMatchingFor80to90("No");
							
							if(matching >= 80.00 && matching<=89.99){
								
								studentsBetween80And90.put(firstSapId, null);
								studentsBetween80And90.put(secondSapId, null);
								copyResult2.setMatchingFor80to90("Yes");
							}
							
							
							
							copyResultList2.add(copyResult2);
							studentWithCopyAboveThreshold2.put(firstSapId, null);
							studentWithCopyAboveThreshold2.put(secondSapId, null);
							
						}
					}
				}
				noOfMatches = 0;
			}
			if( copyResultList2 != null || copyResultList2.size() > 0 ) {
				searchBean.setCopyResultList2(copyResultList2);
			}
			copyCaseLogger.info(" Done comparing file no  "+firstIndex + " subject - "+searchBean.getSubject()+" ");

			return "abc";
		}, executor
);
	} 
	
	
	public double compareQPCopyCase(
			int firstIndex,
			ArrayList<List<String>> allFileContentsList,
			String month,String year
			) throws Exception{
//		Executor executor = Executors.newFixedThreadPool(LOCAL_ACTIVE_PROCESSORS); 
			
			
//			final int current_file_index = firstIndex;
//			assignmentCopyCaseLogger.info("comparing file no  "+firstIndex + " subject - "+searchBean.getSubject()+" ");
//			AssignmentFileBean currentStudentAssignment = assignmentFilesList.get(firstIndex);
			//List<String> firstPdf = readLineByLine(null, currentStudentAssignment.getStudentFilePath());
			List<String> firstPdf = allFileContentsList.get(firstIndex);
			List<String> commanText=new ArrayList<String>();
			int numberOfLinesInFirstFile = firstPdf.size();
			Double matchingPercentage=0.00;
			
			// Code to download and return the web page's content
			for (int secondIndex = firstIndex+1; secondIndex < allFileContentsList.size(); secondIndex++) {
				int maxConseutiveLinesMatched = 0;
				int consecutiveMatchingLinesCounter = 0;
				
				//assignmentCopyCaseLogger.info("Comparing File " + firstIndex + " with File " + secondIndex);
				//
//				AssignmentFileBean otherStudentAssignment = assignmentFilesList.get(secondIndex);
				//List<String> secondPdf = readLineByLine(null, otherStudentAssignment.getStudentFilePath());
				List<String> secondPdf = allFileContentsList.get(secondIndex);
				//	long comparisionStartTime = System.currentTimeMillis();
				
				int blankLines = 0;
				int noOfMatches = 0;
				
				int numberOfLinesInSecondFile = secondPdf.size();
				
				//Store lines matched, so that they are not considered for match again
				ArrayList<String> linesMatchedInFirstFile = new ArrayList<String>();
				ArrayList<String> linesMatchedInSecondFile = new ArrayList<String>();
				Set<String> commanQuestions=new LinkedHashSet<String>();
				for (int p = 0; p < firstPdf.size(); p++) {
					
					///String firstLine = StringUtils.trimToEmpty(firstPdf.get(p).replaceAll("\\s+", ""));
					String firstFileLine="";
					if(firstPdf.get(p)!=null) {
						firstFileLine = firstPdf.get(p);
					}
					//int flen =firstFileLine.length();
					boolean matched = false;
					for (int j = 0; j < secondPdf.size(); j++) {
						// if (p < secondTotalStringLength) {
						///String secondLine = StringUtils.trimToEmpty(secondPdf.get(j).replaceAll("\\s+", ""));
						String secondFileLine="";
						if(secondPdf.get(j)!=null) {
							secondFileLine = secondPdf.get(j);
						}
						
						if (!linesMatchedInSecondFile.contains(secondFileLine) && firstFileLine.contains(secondFileLine)) {
							matched = true;
							noOfMatches++;
							linesMatchedInSecondFile.add(secondFileLine);//This line should not be considered for next time
							break;
						} else if (!linesMatchedInFirstFile.contains(firstFileLine) && secondFileLine.contains(firstFileLine)) {
							matched = true;
							noOfMatches++;
							linesMatchedInFirstFile.add(firstFileLine);//This line should not be considered for next time
							break;
						}
						//}
						//}
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
				int totalStringLength = numberOfLinesInSecondFile > numberOfLinesInFirstFile ? numberOfLinesInSecondFile
						: numberOfLinesInFirstFile;
				//double matching = noOfMatches > totalStringLength ? 100	: (noOfMatches / (double) totalStringLength) * 100;
				
				
				double matching = (noOfMatches / (double) totalStringLength) * 100;
				matchingPercentage=matching;
//				cyclewisePercentage.put(month+" "+year, matching);
				
				
				/*if (matching < 50) {
					totalStringLength = totalStringLength - 20;
					matching = (noOfMatches / (double) totalStringLength) * 100;
				}*/
				
				noOfMatches = 0;
			}
//			assignmentCopyCaseLogger.info(" Done comparing file no  "+firstIndex + " subject - "+searchBean.getSubject()+" ");
			
			return matchingPercentage;
	} 

	private void saveToDesk(ArrayList<ResultDomain> copyResultList,	AssignmentFileBean searchBean, 
			HashMap<String, String> studentWithCopyAboveThreshold, List<AssignmentFileBean> assignmentFilesList,HashMap<String,String> studentsAboveNinety, HashMap<String, String> studentWithCopyAboveThreshold2, Logger copyCaseLogger) {
		try {
			int thresholdNO = 0;
			String subject = searchBean.getSubject();
//		XSSFWorkbook workbook = new XSSFWorkbook();
//		XSSFSheet sheet = workbook.createSheet("Detailed Threshold1 " + String.valueOf(searchBean.getMinMatchPercent()));
			copyCaseLogger.info("Detailed Threshold 1: {} || Total Records:{} || Subject:{}", String.valueOf(searchBean.getMinMatchPercent()), copyResultList.size(), searchBean.getSubject());
		 thresholdNO = 1;
		 
		 //		int rownum = 0;
//		int cellnum = 0;
//		Row row = sheet.createRow(rownum++);
//		Cell cell = row.createCell(cellnum++);
//		cell.setCellValue("Sr. No.");
//
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Subject");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Student ID 1");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("First Name 1");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Last Name 1");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Program 1");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("IC 1");
//
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Student ID 2");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("First Name 2");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Last Name 2");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Program 2");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("IC 2");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Matching %");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Matching % in 80 to 80.99");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Max Consecutive Lines Matched");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("# of Lines in First File");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("# of Lines in Second File");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("# of Lines matched");

		for (ResultDomain copyResult : copyResultList) {
			try {
				dao.insertDetailedThresholdSheetData( thresholdNO,copyResult.getSubject(),searchBean.getMonth(),searchBean.getYear(),
						copyResult.getSapId1(),copyResult.getFirstName1(),copyResult.getLastName1(),copyResult.getProgram1(),copyResult.getCenterName1(),
						copyResult.getSapId2(),copyResult.getFirstName2(),copyResult.getLastName2(),copyResult.getProgram2(),copyResult.getCenterName2(),
						copyResult.getMatching(),copyResult.getMatchingFor80to90(),copyResult.getMaxConseutiveLinesMatched(),copyResult.getNumberOfLinesInFirstFile(),
						copyResult.getNumberOfLinesInSecondFile(),copyResult.getNoOfMatches(),
						searchBean.getLastModifiedBy());
//				cellnum = 0;
//				row = sheet.createRow(rownum++);
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(rownum-1);
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getSubject());
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getSapId1());
//				//assignmentCCLogger.info("Student ID 1 : " +copyResult.getSapId1() +" Detailed Threshold1 sheet data : " + String.valueOf(searchBean.getMinMatchPercent() + " Subject - "+searchBean.getSubject()));
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getFirstName1());
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getLastName1());
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getProgram1());
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getCenterName1());
//				//assignmentCCLogger.info("IC 1 : " +copyResult.getCenterName1() +" Detailed Threshold1 sheet data : " + String.valueOf(searchBean.getMinMatchPercent() + " Subject - "+searchBean.getSubject()));
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getSapId2());
//				//assignmentCCLogger.info("Student ID 2 : " +copyResult.getSapId2() +" Detailed Threshold1 sheet data : " + String.valueOf(searchBean.getMinMatchPercent() + " Subject - "+searchBean.getSubject()));
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getFirstName2());
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getLastName2());
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getProgram2());
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getCenterName2());
//				////assignmentCCLogger.info("IC 2 : " +copyResult.getCenterName2() +" Detailed Threshold1 sheet data : " + String.valueOf(searchBean.getMinMatchPercent() + " Subject - "+searchBean.getSubject()));
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getMatching());
////				//assignmentCCLogger.info("Matching % : " +copyResult.getMatching() +" Detailed Threshold1 sheet data : " + String.valueOf(searchBean.getMinMatchPercent() + " Subject - "+searchBean.getSubject()));
//				
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getMatchingFor80to90());
////				//assignmentCCLogger.info("Matching % in 80 to 80.99 : " +copyResult.getMatchingFor80to90() +" Detailed Threshold1 sheet data : " + String.valueOf(searchBean.getMinMatchPercent() + " Subject - "+searchBean.getSubject()));
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getMaxConseutiveLinesMatched());
////				//assignmentCCLogger.info("Max Consecutive Lines Matched : " +copyResult.getMaxConseutiveLinesMatched() +" Detailed Threshold1 sheet data : " + String.valueOf(searchBean.getMinMatchPercent() + " Subject - "+searchBean.getSubject()));
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getNumberOfLinesInFirstFile());
////				//assignmentCCLogger.info("# of Lines in First File : " +copyResult.getNumberOfLinesInFirstFile() +" Detailed Threshold1 sheet data : " + String.valueOf(searchBean.getMinMatchPercent() + " Subject - "+searchBean.getSubject()));
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getNumberOfLinesInSecondFile());
//				//assignmentCCLogger.info("# of Lines in Second File : " +copyResult.getNumberOfLinesInSecondFile() +" Detailed Threshold1 sheet data : " + String.valueOf(searchBean.getMinMatchPercent() + " Subject - "+searchBean.getSubject()));
//				cell = row.createCell(cellnum++);
//				cell.setCellValue(copyResult.getNoOfMatches());
				//assignmentCCLogger.info("# of Lines matched : " +copyResult.getNoOfMatches() +" Detailed Threshold1 sheet data : " + String.valueOf(searchBean.getMinMatchPercent() + " Subject - "+searchBean.getSubject()));
			} catch (Exception e) {
				copyCaseLogger.info("Exception Error Detailed Threshold 1 || Subject-{} || Error-{}", searchBean.getSubject(), e);
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}

		}
		
		
		//XSSFSheet sheet2 = workbook.createSheet("Unique Threshold1 " + String.valueOf(searchBean.getMinMatchPercent()));
		copyCaseLogger.info("Unique Threshold 1 || Total Records:{} || Subject:{}", studentWithCopyAboveThreshold.size(), searchBean.getSubject());

//		rownum = 0;
//		cellnum = 0;
//		row = sheet2.createRow(rownum++);
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Sr. No.");
//
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Student ID");
//		
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Name");
		
		
		
		for (AssignmentFileBean assignment : assignmentFilesList) {
			try {
				String sapId = assignment.getSapId();
				//assignmentCCLogger.info("Student ID  : " +sapId + " Unique Threshold1 sheet data : " + " Subject - "+searchBean.getSubject());
				if(studentWithCopyAboveThreshold.containsKey(sapId)){
					String Name = assignment.getFirstName() + " " + assignment.getLastName();
					dao.insertUniqueThresholdSheetData(thresholdNO,subject,searchBean.getMonth(),searchBean.getYear(),sapId,Name,searchBean.getLastModifiedBy());
//					cellnum = 0;
//					row = sheet2.createRow(rownum++);
//					cell = row.createCell(cellnum++);
//					cell.setCellValue(rownum-1);
//
//					cell = row.createCell(cellnum++);
//					cell.setCellValue(sapId);
//					
//					cell = row.createCell(cellnum++);
//					cell.setCellValue(assignment.getFirstName() + " " + assignment.getLastName());
				}
			} catch (Exception e) {
				copyCaseLogger.info("Exception Error Unique Threshold 1 || Subject-{} || Error-{}", searchBean.getSubject(), e);
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		
		//XSSFSheet sheet3 = workbook.createSheet("Below Threshold1 "  + String.valueOf(searchBean.getMinMatchPercent()));
		copyCaseLogger.info("Below Threshold 1: {} || Total Records:{} || Subject:{}", String.valueOf(searchBean.getMinMatchPercent()), (assignmentFilesList.size() - studentWithCopyAboveThreshold.size()), searchBean.getSubject());

//		rownum = 0;
//		cellnum = 0;
//		row = sheet3.createRow(rownum++);
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Sr. No.");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Student ID");
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Name");
		
		for (AssignmentFileBean assignment : assignmentFilesList) {
			try {
				String sapId = assignment.getSapId();
				//assignmentCCLogger.info("Student ID  : " +sapId +" Below Threshold1 sheet data "  + String.valueOf(searchBean.getMinMatchPercent()) + " Subject - "+searchBean.getSubject());
				if(!studentWithCopyAboveThreshold.containsKey(sapId)){
					String Name = assignment.getFirstName() + " " + assignment.getLastName();
					dao.insertBelowThresholdSheetData(thresholdNO,subject,searchBean.getMonth(),searchBean.getYear(),sapId,Name,searchBean.getLastModifiedBy());
//					cellnum = 0;
//					row = sheet3.createRow(rownum++);
//					cell = row.createCell(cellnum++);
//					cell.setCellValue(rownum-1);
//
//					cell = row.createCell(cellnum++);
//					cell.setCellValue(sapId);
//					
//					cell = row.createCell(cellnum++);
//					cell.setCellValue(assignment.getFirstName() + " " + assignment.getLastName());
				}
			} catch (Exception e) {
				copyCaseLogger.info("Exception Error Below Threshold 1 || Subject-{} || Error-{}", searchBean.getSubject(), e);
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		//Newly Added to add a new Sheet for students in 90 + range//
		//XSSFSheet sheet4  = workbook.createSheet("Students above 90%");
		copyCaseLogger.info("Students above 90% || Total Records:{} || Subject:{}", studentsAboveNinety.size(), searchBean.getSubject());

//		rownum = 0;
//		cellnum = 0;
//		row = sheet4.createRow(rownum++);
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Sr. No.");
//
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Student ID");
//		
//		cell = row.createCell(cellnum++);
//		cell.setCellValue("Name");
		
		for(AssignmentFileBean assignment : assignmentFilesList){
			try {
				String sapid = assignment.getSapId();
				//assignmentCCLogger.info("Student ID  : " +sapid +" Students above 90% : Subject - "+searchBean.getSubject());
				if(studentsAboveNinety.containsKey(sapid)){
					String Name = assignment.getFirstName() + " " + assignment.getLastName();
					dao.insertStudentsAbove90(subject,sapid,Name,searchBean.getMonth(),searchBean.getYear(),searchBean.getLastModifiedBy());
//					cellnum = 0;
//					row = sheet4.createRow(rownum++);
//					cell = row.createCell(cellnum++);
//					cell.setCellValue(rownum-1);
//					cell = row.createCell(cellnum++);
//					cell.setCellValue(sapid);
//					cell = row.createCell(cellnum++);
//					cell.setCellValue(assignment.getFirstName() + " " + assignment.getLastName());
					
				}
			} catch (Exception e) {
				copyCaseLogger.info("Exception Error Students above 90% || Subject-{} || Error-{}", searchBean.getSubject(), e);
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		
		//End//
		
		// If threshold 2 is same as threshold 1 then avoid threshold 2 list
		if(searchBean.getMinMatchPercent() != searchBean.getThreshold2()) {
//			Newly Added to add a new Sheet for students in 90 + range//
//			XSSFSheet sheet5  = workbook.createSheet("Detailed Threshold2 " + String.valueOf(searchBean.getThreshold2()));
			copyCaseLogger.info("Detailed Threshold 2: {} || Total Records:{} || Subject:{}", String.valueOf(searchBean.getThreshold2()), searchBean.getCopyResultList2().size(), searchBean.getSubject());
			thresholdNO = 2;
//			rownum = 0;
//			cellnum = 0;
//			Row row2 = sheet5.createRow(rownum++);
//			Cell cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Sr. No.");
//	
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Subject");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Student ID 1");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("First Name 1");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Last Name 1");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Program 1");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("IC 1");
//			
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Student ID 2");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("First Name 2");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Last Name 2");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Program 2");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("IC 2");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Matching %");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Matching % in 80 to 80.99");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("Max Consecutive Lines Matched");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("# of Lines in First File");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("# of Lines in Second File");
//			cell2 = row2.createCell(cellnum++);
//			cell2.setCellValue("# of Lines matched");
			for (ResultDomain copyResult : searchBean.getCopyResultList2() ) {
				try {
					dao.insertDetailedThresholdSheetData( thresholdNO,copyResult.getSubject(),searchBean.getMonth(),searchBean.getYear(),
							copyResult.getSapId1(),copyResult.getFirstName1(),copyResult.getLastName1(),copyResult.getProgram1(),copyResult.getCenterName1(),
							copyResult.getSapId2(),copyResult.getFirstName2(),copyResult.getLastName2(),copyResult.getProgram2(),copyResult.getCenterName2(),
							copyResult.getMatching(),copyResult.getMatchingFor80to90(),copyResult.getMaxConseutiveLinesMatched(),copyResult.getNumberOfLinesInFirstFile(),
							copyResult.getNumberOfLinesInSecondFile(),copyResult.getNoOfMatches(),searchBean.getLastModifiedBy());
//					cellnum = 0;
//					row2 = sheet5.createRow(rownum++);
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(rownum-1);
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getSubject());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getSapId1());
//					//assignmentCCLogger.info("Student ID 1 : " +copyResult.getSapId1() + "Detailed Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2())  + " Subject - "+searchBean.getSubject());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getFirstName1());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getLastName1());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getProgram1());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getCenterName1());
//					//assignmentCCLogger.info("IC 1 : " +copyResult.getCenterName1() + "Detailed Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2())  + " Subject - "+searchBean.getSubject());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getSapId2());
//					//assignmentCCLogger.info("Student ID 2 : " +copyResult.getSapId2() + "Detailed Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2())  + " Subject - "+searchBean.getSubject());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getFirstName2());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getLastName2());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getProgram2());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getCenterName2());
//					//assignmentCCLogger.info("IC 2 : " +copyResult.getCenterName2() + "Detailed Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2())  + " Subject - "+searchBean.getSubject());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getMatching());
//					//assignmentCCLogger.info("Matching % : " +copyResult.getMatching() + "Detailed Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2())  + " Subject - "+searchBean.getSubject());
//					
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getMatchingFor80to90());
//					//assignmentCCLogger.info("Matching % in 80 to 80.99 : " +copyResult.getMatchingFor80to90() + "Detailed Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2())  + " Subject - "+searchBean.getSubject());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getMaxConseutiveLinesMatched());
//					//assignmentCCLogger.info("Max Consecutive Lines Matched : " +copyResult.getMaxConseutiveLinesMatched() + "Detailed Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2())  + " Subject - "+searchBean.getSubject());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getNumberOfLinesInFirstFile());
//					//assignmentCCLogger.info("# of Lines in First File : " +copyResult.getNumberOfLinesInFirstFile() + "Detailed Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2())  + " Subject - "+searchBean.getSubject());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getNumberOfLinesInSecondFile());
//					//assignmentCCLogger.info("# of Lines in Second File : " +copyResult.getNumberOfLinesInSecondFile() + "Detailed Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2())  + " Subject - "+searchBean.getSubject());
//					cell2 = row2.createCell(cellnum++);
//					cell2.setCellValue(copyResult.getNoOfMatches());
//					assignmentCCLogger.info("# of Lines matched : " +copyResult.getNoOfMatches() + "Detailed Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2())  + " Subject - "+searchBean.getSubject());
				} catch (Exception e) {
					copyCaseLogger.info("Exception Error Detailed Threshold 2 || Subject-{} || Error-{}", searchBean.getSubject(), e);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
	
			
//			XSSFSheet sheet6 = workbook.createSheet("Unique Threshold2 " + String.valueOf(searchBean.getThreshold2()));
			copyCaseLogger.info("Unique Threshold 2 || Total Records:{} || Subject:{}", studentWithCopyAboveThreshold2.size(), searchBean.getSubject());
//			rownum = 0;
//			cellnum = 0;
//			row = sheet6.createRow(rownum++);
//			cell = row.createCell(cellnum++);
//			cell.setCellValue("Sr. No.");
//			cell = row.createCell(cellnum++);
//			cell.setCellValue("Student ID");
//			cell = row.createCell(cellnum++);
//			cell.setCellValue("Name");
			
			for (AssignmentFileBean assignment : assignmentFilesList) {
				try {
					String sapId = assignment.getSapId();
//					assignmentCCLogger.info("Sapid : " +sapId +" Unique Threshold2 sheet data " + String.valueOf(searchBean.getThreshold2()));
					if(studentWithCopyAboveThreshold2.containsKey(sapId)){
						String name = assignment.getFirstName() + " " + assignment.getLastName();
						dao.insertUniqueThresholdSheetData(thresholdNO,subject,searchBean.getMonth(),searchBean.getYear(),sapId,name,searchBean.getLastModifiedBy());
//						cellnum = 0;
//						row = sheet6.createRow(rownum++);
//						cell = row.createCell(cellnum++);
//						cell.setCellValue(rownum-1);
//						cell = row.createCell(cellnum++);
//						cell.setCellValue(sapId);
//						cell = row.createCell(cellnum++);
//						cell.setCellValue(assignment.getFirstName() + " " + assignment.getLastName());
					}
				} catch (Exception e) {
					copyCaseLogger.info("Exception Error Unique Threshold 2 || Subject-{} || Error-{}", searchBean.getSubject(), e);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			
//			XSSFSheet sheet7 = workbook.createSheet("Below Threshold2 "  + String.valueOf(searchBean.getThreshold2()));
			copyCaseLogger.info("Below Threshold 2 : {} || Total Records:{} || Subject:{}", String.valueOf(searchBean.getMinMatchPercent()), (assignmentFilesList.size() - studentWithCopyAboveThreshold2.size()), searchBean.getSubject());
//			rownum = 0;
//			cellnum = 0;
//			row = sheet7.createRow(rownum++);
//			cell = row.createCell(cellnum++);
//			cell.setCellValue("Sr. No.");
//			cell = row.createCell(cellnum++);
//			cell.setCellValue("Student ID");
//			cell = row.createCell(cellnum++);
//			cell.setCellValue("Name");
			
			for (AssignmentFileBean assignment : assignmentFilesList) {
				try {
					String sapId = assignment.getSapId();
//					assignmentCCLogger.info("Sapid : " +assignment.getSapId() + " Below Threshold2 sheet data "  + String.valueOf(searchBean.getThreshold2()));
					
					if(!studentWithCopyAboveThreshold2.containsKey(sapId)){
						String name = assignment.getFirstName() + " " + assignment.getLastName();
						dao.insertBelowThresholdSheetData(thresholdNO,subject,searchBean.getMonth(),searchBean.getYear(),sapId,name,searchBean.getLastModifiedBy());
//						cellnum = 0;
//						row = sheet7.createRow(rownum++);
//						cell = row.createCell(cellnum++);
//						cell.setCellValue(rownum-1);
//						cell = row.createCell(cellnum++);
//						cell.setCellValue(sapId);
//						cell = row.createCell(cellnum++);
//						cell.setCellValue(assignment.getFirstName() + " " + assignment.getLastName());
					}
				} catch (Exception e) {
					copyCaseLogger.info("Exception Error Below Threshold 2 sheet data || Subject-{} || Error-{}", searchBean.getSubject(), e);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			
		}
		
		
//		try {
//			String fileName = searchBean.getSubject().replaceAll(":", "") + "_Copy_Result.xlsx";
//			String filePath = ASSIGNMENT_REPORTS_GENERATE_PATH + searchBean.getMonth() + searchBean.getYear()+ "/" +fileName;
//			FileOutputStream out = new FileOutputStream(new File(filePath));
//			workbook.write(out);
//			out.close();
//		} catch (Exception e) { 
//			assignmentCCLogger.info("Exception - while generating report : " + " Subject - "+searchBean.getSubject() + " "+e);
//		}
		} catch (Exception e) {
			copyCaseLogger.info("Exception Error saveToDesk() || Subject-{} || Error-{}", searchBean.getSubject(), e);
		}
	}

	public List<String> readLineByLine(String prefix, String fileName, ArrayList<String> questionFileContent)  {
		List<String> ans = new ArrayList<>();
		PDFTextStripper stripper;
		PDDocument doc = null;
		//List<String> newOne = new ArrayList<>();
		String allFileContent = "";
		try {
			InputStream is = new URL(fileName).openStream();
			if (prefix != null)
				doc = PDDocument.load(prefix + File.separator + is);
			else
				doc = PDDocument.load(is);
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
					s = s.replaceAll("\\s+", "");
					s = s.replaceAll("\\p{C}", "");
					//Join all lines of pdf
					allFileContent = allFileContent + s.toUpperCase().trim();
				}
			}

		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			
			return null;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			
			return null;
		}catch (Exception e) {
			// TODO Auto-generated catch block
			
			return null;
		} finally {
			if (doc != null)
				try {
					doc.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
				}
		}
		
		
		//Split lines by full stop or by question.
		String[] splits = allFileContent.split("\\.|\\?|\\:");
		ArrayList<String> fileContent = new ArrayList<>();

		for (String line : splits) {
			if(line.length() > MIN_LENGTH_FOR_COMPARISION && (!(questionFileContent.contains(line))) ){
				fileContent.add(line);
			}
		}

		return fileContent;

	}
	
	//@Async
	public void checkCopyCasesForProject(List<AssignmentFileBean> projectFilesList, AssignmentFileBean searchBean) throws Exception{
		ArrayList<List<String>> allFileContentsList = new ArrayList<>();
		// qn file content seperated line by line trimming space
		int count = 1;
		for (AssignmentFileBean projectFileBean : projectFilesList) {
			projectFileBean.setStudentFilePath(ASSIGNMENT_PREVIEW_PATH + projectFileBean.getPreviewPath());
			projectCCLogger.info("Reading file Count:{}/{} || StudentFilePathURL:{}",count, projectFilesList.size(), projectFileBean.getStudentFilePath());
			long readStartTime = System.currentTimeMillis();
			List<String> fileContent = readLineByLine(null, projectFileBean.getStudentFilePath(), new ArrayList<String>());
			long readEndTime = System.currentTimeMillis();
			projectCCLogger.info("Time taken to read file = " + (readEndTime - readStartTime));
			if (fileContent != null) {
				allFileContentsList.add(fileContent);
			} else {
				projectCCLogger.info("Student uploaded QP file: {}",projectFileBean.getStudentFilePath());
				allFileContentsList.add(new ArrayList<String>());
			}
			count++;
		}
		
		// now using commont method of multi-threading copy case proccess - Prashant 6-4-2023
//		HashMap<String, String> studentWithCopyAboveThreshold = new HashMap<>();
//		HashMap<String,String> studentsAbove90 = new HashMap<>();
//		HashMap<String,String> studentsBetween80And90 = new HashMap<String,String>();
//		ArrayList<ResultDomain> copyResultList = new ArrayList<>();
//		ArrayList<ResultDomain> copyResultList2 = new ArrayList<>();
//		List<String> firstPdfTemp = allFileContentsList.get(0);
//		List<String> secondPdfTemp = allFileContentsList.get(1);
//		
//		
//		for (int firstIndex = 0; firstIndex < allFileContentsList.size(); firstIndex++) {
//			AssignmentFileBean currentStudentAssignment = projecttFilesList.get(firstIndex);
//			//List<String> firstPdf = readLineByLine(null, currentStudentAssignment.getStudentFilePath());
//			List<String> firstPdf = allFileContentsList.get(firstIndex);
//			int numberOfLinesInFirstFile = firstPdf.size();
//			
//			for (int secondIndex = firstIndex+1; secondIndex < allFileContentsList.size(); secondIndex++) {
//				int maxConseutiveLinesMatched = 0;
//				int consecutiveMatchingLinesCounter = 0;
//				logger.info("Comparing File  " + firstIndex +  " with File " + secondIndex);
//				AssignmentFileBean otherStudentAssignment = projecttFilesList.get(secondIndex);
//
//				List<String> secondPdf = allFileContentsList.get(secondIndex);
//				long comparisionStartTime = System.currentTimeMillis();
//
//				int blankLines = 0;
//				int noOfMatches = 0;
//
//				int numberOfLinesInSecondFile = secondPdf.size();
//				
//				//Store lines matched, so that they are not considered for match again
//				ArrayList<String> linesMatchedInFirstFile = new ArrayList<String>();
//				ArrayList<String> linesMatchedInSecondFile = new ArrayList<String>();
//				
//				for (int p = 0; p < firstPdf.size(); p++) {
//					String firstFileLine="";
//                    if(firstPdf.get(p)!=null) {
//                    	firstFileLine = firstPdf.get(p);
//                    } 
//					boolean matched = false;
//					for (int j = 0; j < secondPdf.size(); j++) {
//						String secondFileLine="";
//						if(secondPdf.get(j)!=null) {
//							secondFileLine = secondPdf.get(j);
//						}
//						
//						if (!linesMatchedInSecondFile.contains(secondFileLine) && firstFileLine.contains(secondFileLine)) {
//							matched = true;
//							noOfMatches++;
//							linesMatchedInSecondFile.add(secondFileLine);//This line should not be considered for next time
//							break;
//						} else if (!linesMatchedInFirstFile.contains(firstFileLine) && secondFileLine.contains(firstFileLine)) {
//							matched = true;
//							noOfMatches++;
//							linesMatchedInFirstFile.add(firstFileLine);//This line should not be considered for next time
//							break;
//						}
//					}
//					if(matched){
//						consecutiveMatchingLinesCounter++;
//						if(maxConseutiveLinesMatched < consecutiveMatchingLinesCounter){
//							maxConseutiveLinesMatched = consecutiveMatchingLinesCounter;
//						}
//					}else{
//						consecutiveMatchingLinesCounter = 0;
//					}
//				}
//				logger.info("First file " + currentStudentAssignment.getStudentFilePath());
//				logger.info("Second file " + otherStudentAssignment.getStudentFilePath());
//				
//				logger.info("Lines in First File = " + numberOfLinesInFirstFile);
//				logger.info("Lines in Second File = " + numberOfLinesInSecondFile);
//				logger.info("Total Comparisions = " + (numberOfLinesInFirstFile * numberOfLinesInSecondFile));
//				logger.info("Number of matches is = " + noOfMatches);
//
//				int totalStringLength = numberOfLinesInSecondFile > numberOfLinesInFirstFile ? numberOfLinesInSecondFile
//						: numberOfLinesInFirstFile;
//				//double matching = noOfMatches > totalStringLength ? 100	: (noOfMatches / (double) totalStringLength) * 100;
//				
//				
//				double matching = (noOfMatches / (double) totalStringLength) * 100;
//
//				/*if (matching < 50) {
//					totalStringLength = totalStringLength - 20;
//					matching = (noOfMatches / (double) totalStringLength) * 100;
//				}*/
//
//				long comparisionEndTime = System.currentTimeMillis();
//
//				if(matching > searchBean.getMinMatchPercent()){
//					String firstSapId = currentStudentAssignment.getSapId();
//					String secondSapId = otherStudentAssignment.getSapId();
//					
//					ResultDomain copyResult = new ResultDomain();
//					copyResult.setNoOfMatches(noOfMatches);
//					copyResult.setMatching(matching);
//					copyResult.totalStringLength = totalStringLength;
//					copyResult.blankLines = blankLines;
//					copyResult.firstFile = currentStudentAssignment.getStudentFilePath();
//					copyResult.secondFile = otherStudentAssignment.getStudentFilePath();
//					
//					copyResult.setSubject(currentStudentAssignment.getSubject());
//					copyResult.setMaxConseutiveLinesMatched(maxConseutiveLinesMatched);
//					copyResult.setNumberOfLinesInFirstFile(numberOfLinesInFirstFile);
//					copyResult.setNumberOfLinesInSecondFile(numberOfLinesInSecondFile);
//					copyResult.setNoOfMatches(noOfMatches);
//
//					copyResult.setSapId1(firstSapId);
//					copyResult.setFirstName1(currentStudentAssignment.getFirstName());
//					copyResult.setLastName1(currentStudentAssignment.getLastName());
//					copyResult.setProgram1(currentStudentAssignment.getProgram());
//					copyResult.setCenterName1(currentStudentAssignment.getCenterName());
//
//					copyResult.setSapId2(secondSapId);
//					copyResult.setFirstName2(otherStudentAssignment.getFirstName());
//					copyResult.setLastName2(otherStudentAssignment.getLastName());
//					copyResult.setProgram2(otherStudentAssignment.getProgram());
//					copyResult.setCenterName2(otherStudentAssignment.getCenterName());
//					copyResult.setMatchingFor80to90("No");
//					
//					
//					
//					copyResultList.add(copyResult);
//					
//					studentWithCopyAboveThreshold.put(firstSapId, null);
//					studentWithCopyAboveThreshold.put(secondSapId, null);
//					
//					if(matching > 90.00){
//						studentsAbove90.put(firstSapId, null);
//						studentsAbove90.put(secondSapId, null);
//					}
//					
//					
//					
//				}
//				if(searchBean.getThreshold2() != 0) {
//					if(matching > searchBean.getThreshold2()){
//						String firstSapId = currentStudentAssignment.getSapId();
//						String secondSapId = otherStudentAssignment.getSapId();
//						
//						ResultDomain copyResult2 = new ResultDomain();
//						copyResult2.setNoOfMatches(noOfMatches);
//						copyResult2.setMatching(matching);
//						copyResult2.totalStringLength = totalStringLength;
//						copyResult2.blankLines = blankLines;
//						copyResult2.firstFile = currentStudentAssignment.getStudentFilePath();
//						copyResult2.secondFile = otherStudentAssignment.getStudentFilePath();
//						
//						copyResult2.setSubject(currentStudentAssignment.getSubject());
//						copyResult2.setMaxConseutiveLinesMatched(maxConseutiveLinesMatched);
//						copyResult2.setNumberOfLinesInFirstFile(numberOfLinesInFirstFile);
//						copyResult2.setNumberOfLinesInSecondFile(numberOfLinesInSecondFile);
//						copyResult2.setNoOfMatches(noOfMatches);
//
//						copyResult2.setSapId1(firstSapId);
//						copyResult2.setFirstName1(currentStudentAssignment.getFirstName());
//						copyResult2.setLastName1(currentStudentAssignment.getLastName());
//						copyResult2.setProgram1(currentStudentAssignment.getProgram());
//						copyResult2.setCenterName1(currentStudentAssignment.getCenterName());
//
//						copyResult2.setSapId2(secondSapId);
//						copyResult2.setFirstName2(otherStudentAssignment.getFirstName());
//						copyResult2.setLastName2(otherStudentAssignment.getLastName());
//						copyResult2.setProgram2(otherStudentAssignment.getProgram());
//						copyResult2.setCenterName2(otherStudentAssignment.getCenterName());
//						copyResult2.setMatchingFor80to90("No");
//						
//						if(matching >= 80.00 && matching<=89.99){
//							
//							studentsBetween80And90.put(firstSapId, null);
//							studentsBetween80And90.put(secondSapId, null);
//							copyResult2.setMatchingFor80to90("Yes");
//						}
//						
//						copyResultList2.add(copyResult2);
//						
//						
//					}
//				}
//				noOfMatches = 0;
//			}
//		}
//		searchBean.setCopyResultList2(copyResultList2);
//		HashMap<String, String> studentWithCopyAboveThreshold2 = new HashMap<>();
//		logger.info("Saving to Disc");
//		saveToDesk(copyResultList,searchBean, studentWithCopyAboveThreshold, projecttFilesList,studentsAbove90,studentWithCopyAboveThreshold2);
//		logger.info("Done Saving to Disc");
		
		
		// Multi threading copy case process
		copyCaseProcess(projectFilesList, searchBean, allFileContentsList,projectCCLogger);
	}
	
	
	public List<String> readAllContaint(MultipartFile file)  throws Exception{
		List<String> ans = new ArrayList<>();
		PDFTextStripper stripper;
		PDDocument doc = null;
		//List<String> newOne = new ArrayList<>();
		String allFileContent = "";
			//read data from file
			InputStream is = file.getInputStream();
			//loads the document
				doc = PDDocument.load(is);
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
					s.replaceAll("\\p{C}", "");
					//Join all lines of pdf
					allFileContent = allFileContent+" " + s.trim();
				}
			}
			
			String[] splits = allFileContent.split("\\.|\\?|\\:");
			ArrayList<String> fileContent = new ArrayList<>();

			for (String line : splits) {
				if(line.length() > MIN_LENGTH_FOR_COMPARISION) {
				String trimmedStr = line.trim();
				fileContent.add(trimmedStr);
				}
				
			}
		
        
return  fileContent;
}
	
	
	
	public List<String> readAllContaint(String file) throws Exception {
		
		System.setProperty("org.apache.pdfbox.baseParser.pushBackSize", "256000");

		List<String> ans = new ArrayList<>();
		PDFTextStripper stripper;
		PDDocument doc = null;
		//List<String> newOne = new ArrayList<>();
		String allFileContent = "";
			//read data from file
			InputStream is = new URL(file).openStream();
			//loads the document
				doc = PDDocument.load(is);
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
					s.replaceAll("\\p{C}", "");
					//Join all lines of pdf
					allFileContent = allFileContent+" " + s.trim();
				}
			}
			
			String[] splits = allFileContent.split("\\.|\\?|\\:");
			ArrayList<String> fileContent = new ArrayList<>();

			for (String line : splits) {
				if(line.length() > MIN_LENGTH_FOR_COMPARISION) {
				String trimmedStr = line.trim();
					fileContent.add(trimmedStr);
				}
				
			}
		
        
return  fileContent;
}


	
	
	public void cehckCopycaseOnWeb(List<AssignmentFileBean> assignmentFilesList, AssignmentFileBean searchBean) {
		

		
		String url=SERVER_PATH+"exam/m/getWebCCReportFromCopyLeaks"; 
		RestTemplate template=new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		
		
		
		for(AssignmentFileBean assignmentFileBean: assignmentFilesList) {
		
			
		Map<String,AssignmentFileBean> assignmentFileBeanMap = new HashMap();
		assignmentFileBeanMap.put("searchBean", searchBean);
		assignmentFileBeanMap.put("assignmentFileBean",assignmentFileBean);
		
		
		
		HttpEntity<Map<String,AssignmentFileBean>> entity = new HttpEntity<Map<String,AssignmentFileBean>>(assignmentFileBeanMap,headers);
		ResponseEntity<Object> response=null;
		Map<String,Object> responseBody=null;
		try {
		response=template.exchange(
				url,
				HttpMethod.POST, entity,  Object.class);
		 responseBody = (HashMap<String,Object>)response.getBody();
		}
		catch (Exception e) {
			//System.out.println("CopyCaseHelper.cehckCopycaseOnWeb()");
			copyLeaksControllerLogger.error("Error : While sending request to copyleks controller :: "+e.getMessage());
			//e.printStackTrace();
			// TODO: handle exception
		}
		
		//System.out.println("Response :: "+response);
		assignmentFileBeanMap=null;
		}
		
		
	}
}