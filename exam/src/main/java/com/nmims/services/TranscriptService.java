package com.nmims.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nmims.beans.BulkTranscriptGenerationBean;
import com.nmims.beans.MDMSubjectCodeMappingBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ServiceRequestDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.PassFailSubjectHelper;
import com.nmims.helpers.TranscriptPDFCreator;
import com.nmims.services.impl.TranscriptGenerationServiceInterface;
@Service
public class TranscriptService implements TranscriptGenerationServiceInterface{
	private static final Logger logger = LoggerFactory.getLogger(TranscriptService.class);

	@Autowired
	ServiceRequestDAO serviceRequestDao;
	
	@Autowired
	PassFailDAO passFailDao;
	
	@Autowired
	MarksheetService mservice;
	
	@Autowired
	StudentService studentService;
	@Autowired
	StudentMarksDAO dao;
	
	@Autowired
	SubjectCreditService creditService;
	
	@Autowired
	ExamBookingDAO eDao;
	
	@Value( "${HALLTICKET_PATH}" )
	private String HALLTICKET_PATH;
	
	@Value( "${TRANSCRIPT_PATH}" )
	private String TRANSCRIPT_PATH;
	
	private HashMap<String, ProgramExamBean> programDetailsMap = null;
	
	private final Integer CREDIT_ENROLL_YEAR = 2021; //applicable year to get credit column in transcript

	
	public void checkForServiceRequestIdPresent(String commaSepratedList) throws Exception {
		if (StringUtils.isBlank(commaSepratedList)) {
			logger.info("No Service Id Is In Input ");
			throw new Exception("Please Enter at least 1 SRid");
		}
	}
	private ArrayList<PassFailExamBean> getPassRecordsForStudentSelfTranscript(String sapid) {
		return passFailDao.getPassRecordsForStudentSelfTranscript(sapid);
	}
	
	private double getLastWrittenPassExamMonthYear(String sapid) {
		return passFailDao.getLastWrittenPassExamMonthYear(sapid);
	}
	
	private double getLastAssignmentPassExamMonthYear(String sapid) {
		return passFailDao.getLastAssignmentPassExamMonthYear(sapid);
	}
	private String findLastYearMonthOfStudent(double lastWrittenAttemptOrder,double lastAssignmentAttemptOrder,String sapid) {
		String lastExamMonthYear="";
		if (lastWrittenAttemptOrder > lastAssignmentAttemptOrder) {
			lastExamMonthYear = passFailDao.getWrittenLastPassExamMonth(sapid);
		} else {
			lastExamMonthYear = passFailDao.getAssignmentLastPassExamMonth(sapid);
		}
		return lastExamMonthYear;
	}
	private StudentExamBean getSingleStudentsData(String sapid) {
		return eDao.getSingleStudentsData(sapid);
	}
	private HashMap<String,ArrayList<String>> getSemWiseSubjectsMap(String program,String programStructureApllicable) {
		return passFailDao.getSemWiseSubjectsMap(program,programStructureApllicable);
	}
	//Check if student is lateral student and has done program with NGASCE in past. In this case his passed subjects will become waived off subjects
	private void checkIfForLatral(StudentExamBean singleStudentData) {
		if("Jul2019".equalsIgnoreCase(singleStudentData.getPrgmStructApplicable()) && singleStudentData.getWaivedOffSubjects().contains("Business Statistics")) {
			singleStudentData.getWaivedOffSubjects().add("Decision Science");
		}
	}
	private void checkNullForPassList(ArrayList<PassFailExamBean> passList) throws Exception {
		if (CollectionUtils.isEmpty(passList)) {
			throw new Exception("Passing Data Not Found");
			
		}
	}
	private ArrayList<StudentExamBean> lateralStudentList(StudentExamBean singleStudentData) throws Exception{
		ArrayList<StudentExamBean> lateralStudentList = new ArrayList<StudentExamBean>();
		StudentExamBean lateralStudent = new StudentExamBean();
		if(singleStudentData.getIsLateral().equalsIgnoreCase("Y")){
			if(StringUtils.isEmpty(singleStudentData.getPreviousStudentId()))
				throw new Exception("Previous Sapid Not Found ");
			lateralStudent = eDao.getSingleStudentsData(singleStudentData.getPreviousStudentId());
//			if(lateralStudent))
			logger.info("StudentData {} ",lateralStudent);

			lateralStudentList.add(lateralStudent);
			if(lateralStudent.getIsLateral().equalsIgnoreCase("Y") ) {
				List<StudentExamBean> lateralStudentList2 = getLateralStudentsData(eDao, lateralStudent);

				lateralStudentList.addAll(lateralStudentList2);
				logger.info("Lateral Student Data{} ",lateralStudentList2);
			}
			Collections.reverse(lateralStudentList);
			
	}
		return lateralStudentList;
		}
	private HashMap<String, String> getlateralSapidLastExamMonthYearMap(StudentExamBean singleStudentData,HashMap<String,ArrayList<String>> semWiseSubjectMap,ArrayList<PassFailExamBean> passList,ArrayList<StudentExamBean> lateralStudentList) {
		HashMap<String, String> lateralSapidLastExamMonthYearMap = new HashMap<String, String>();
		String lateralLastExamMonthYear = "";
			singleStudentData.getWaivedOffSubjects().remove("Project"); 
			//fetching previous program pass list
			ArrayList<PassFailExamBean> lateralPassList = mservice.getLateralPasslist(singleStudentData,lateralStudentList,semWiseSubjectMap,passFailDao );
			logger.info("Lateral Student passList{} ",lateralPassList);
	
			passList.addAll(lateralPassList); // adding lateral passlist detail in current passlist 
			//to find out last Exam month year of lateral student to be printed on transcript pdf
			for(StudentExamBean lateralData :lateralStudentList) {
				double lateralLastWrittenAttemptOrder = getLastWrittenPassExamMonthYear(lateralData.getSapid());
				double lateralLastAssignmentAttemptOrder =getLastAssignmentPassExamMonthYear(lateralData.getSapid());
				lateralLastExamMonthYear=findLastYearMonthOfStudent(lateralLastWrittenAttemptOrder,lateralLastAssignmentAttemptOrder,lateralData.getSapid());
				lateralSapidLastExamMonthYearMap.put(lateralData.getSapid(), lateralLastExamMonthYear);
			}
			return lateralSapidLastExamMonthYearMap;
		}
	public List<String> removeErrorSrIdFromSuccessList(List<String>successList,List<String>errorList) {
		ArrayList<String> ErrorList= new ArrayList<>();
		for(String success : successList) {
			if (errorList.contains(success)){
				ErrorList.add(success);
				
			}
		}
		return ErrorList;
	}
	
	public ArrayList<StudentExamBean> getLateralStudentsData(ExamBookingDAO eDao, StudentExamBean lateralStudent){
		ArrayList<StudentExamBean> lateralStudentList = new ArrayList<StudentExamBean>();
		StudentExamBean prevLateralStudent = eDao.getSingleStudentsData(lateralStudent.getPreviousStudentId());
		logger.info("Previous Lateral Student Data {} ",prevLateralStudent);

		lateralStudentList.add(prevLateralStudent);
		if(prevLateralStudent.getIsLateral().equalsIgnoreCase("Y") ) { // check again if lateral student is again lateral or not
			List<StudentExamBean> lateralDataList2 = getLateralStudentsData(eDao,prevLateralStudent); // if yes then again repeat the method
			lateralStudentList.addAll(lateralDataList2);
		}
		return lateralStudentList;
	}
	
	public BulkTranscriptGenerationBean generateBulkTranscript(StudentExamBean bean) throws Exception {
		BulkTranscriptGenerationBean bulkTranscriptBean= new BulkTranscriptGenerationBean();
		
		checkForServiceRequestIdPresent(bean.getServiceRequestIdList());
		
		List<String> serviceRequestId=convertCommaSepratedToList(bean.getServiceRequestIdList());
		
		logger.info("List Of Srid's {} ",serviceRequestId);
		//valid sr id list
		List<String> srSuccessList =addingSuccessSridsInList(serviceRequestId);
		logger.info("SuccessList of Service Request {}",srSuccessList);
		//update name of method
		String errorList=generateCommaSepratedErrorList(serviceRequestId,srSuccessList );
		List<String> SRErrorList = convertCommaSepratedToList(errorList);
		logger.info("Srid's Whose Service Request Type is not Issuance Of Transcript  {}",SRErrorList);
		logger.info("Successfull Sr Id commaSepratedlist",srSuccessList);
		String successList= generateCommaSepratedListFromList(srSuccessList);
		logger.info("Successfull Srid List ",successList);
		List<MarksheetBean> studentForSRList =checkDataOnSrId(successList);
		logger.info("Successfull Srid's StudentData",studentForSRList);
		
		BulkTranscriptGenerationBean errorwhileCreatingSuccessfull =createTranscript(studentForSRList,bean);
		if(!CollectionUtils.isEmpty(errorwhileCreatingSuccessfull.getErrorwhileCreatingSuccessfull())) {
			SRErrorList.addAll(errorwhileCreatingSuccessfull.getErrorwhileCreatingSuccessfull());
			List<String>success=removeErrorSrIdFromSuccessList(srSuccessList, SRErrorList);
			if(!CollectionUtils.isEmpty(success)) {
				srSuccessList.removeAll(success);
			}
			bulkTranscriptBean.setErrorwhileCreatingSuccessfull(errorwhileCreatingSuccessfull.getErrorwhileCreatingSuccessfull());
			logger.info("Adding Failed Srid's In Sr Error List ",errorwhileCreatingSuccessfull.getErrorwhileCreatingSuccessfull());}
		logger.info("Setting the Data in The Bulk Transcript Bean ",bulkTranscriptBean);
		bulkTranscriptBean.setSRErrorList(SRErrorList);
		bulkTranscriptBean.setSRSuccessList(srSuccessList);
		bulkTranscriptBean.setSuccessList(successList);
		bulkTranscriptBean.setBarcodePDFFilePathList(errorwhileCreatingSuccessfull.getBarcodePDFFilePathList());
		bulkTranscriptBean.setMergedFileName(errorwhileCreatingSuccessfull.getMergedFileName());
		logger.info("Successfully Setting the Data In bulkTranscriptBean",bulkTranscriptBean);
		return bulkTranscriptBean;
	}
	public String generateSingleTranscript(StudentExamBean bean) throws Exception {
		TranscriptPDFCreator pdfCreator = new TranscriptPDFCreator();
		String sapid = bean.getSapid();
		
			ArrayList<PassFailExamBean> passList = getPassRecordsForStudentSelfTranscript(sapid);
			if(CollectionUtils.isEmpty(passList)) {
				logger.info("  PassList Data Not Found for Sapid {} ",sapid );
				throw new Exception("pass Record Not Found Data ");
				}
			StudentExamBean singleStudentData =getSingleStudentsData(sapid);
			logger.info(singleStudentData.getSapid()+"  PassList in Single Transcript  {}",passList);

			double lastWrittenAttemptOrder = getLastWrittenPassExamMonthYear(sapid);
			logger.info(singleStudentData.getSapid()+  "  lastWrittenAttemptOrder  {}",lastWrittenAttemptOrder);

			double lastAssignmentAttemptOrder = getLastAssignmentPassExamMonthYear(sapid);
			logger.info(singleStudentData.getSapid()+  "  lastAssignmentAttemptOrder  {}",lastWrittenAttemptOrder);
//		Added to find out last Exam month year of student to be printed on transcript pdf
			String lastExamMonthYear = findLastYearMonthOfStudent(lastWrittenAttemptOrder,lastAssignmentAttemptOrder,sapid);
			 logger.info(singleStudentData.getSapid()+  "  lastExamMonthYear  {}",lastExamMonthYear);
			String logoRequired = bean.getLogoRequired();
			
//		ArrayList<StudentExamBean> lateralStudentList = new ArrayList<StudentExamBean>();
			
			
			/*Added by Steffi to consider lateral students.
		Check if student is lateral student and has done program with NGASCE in past. In this case his passed subjects will become waived off subjects
		Course Waiver is not applicable for Jul2009 program Structure Students*/			
			HashMap<String,ArrayList<String>> semWiseSubjectMap = getSemWiseSubjectsMap(singleStudentData.getProgram(),singleStudentData.getPrgmStructApplicable());
			 logger.info(singleStudentData.getSapid()+  "  semWiseSubjectMap  {}",semWiseSubjectMap);
			 studentService.mgetWaivedOffSubjects(singleStudentData);
			checkIfForLatral(singleStudentData);
			checkNullForPassList(passList);
			singleStudentData.setLogoRequired(logoRequired);
			ArrayList<StudentExamBean> lateralStudentList = lateralStudentList(singleStudentData);
			 logger.info(singleStudentData.getSapid()+  "  lateralStudentList  {}",lateralStudentList);
			HashMap<String, String> lateralSapidLastExamMonthYearMap=getlateralSapidLastExamMonthYearMap(singleStudentData,semWiseSubjectMap,passList,lateralStudentList);
			 logger.info(singleStudentData.getSapid()+  "  lateralSapidLastExamMonthYearMap  {}",lateralSapidLastExamMonthYearMap);
			 if (this.checkLateralStudentFromPGToMBAProgram(singleStudentData.getIsLateral(), singleStudentData.getProgram(), singleStudentData.getOldProgram())){
				 	//passList =removeLateralDuplicateSubjectsPG_MBAPolicy(passList,singleStudentData.getProgram(), semWiseSubjectMap);
					passList = PassFailSubjectHelper.removeLateralDuplicateSubjectsPG_MBAPolicy_updated(passList);
				}
			 
			 //set admin login
			 singleStudentData.setFromAdmin(bean.getFromAdmin());
			 
			//Get Subject Credit Details
			Map<String, MDMSubjectCodeMappingBean> mapPssDetail = new HashMap<>();
			Map<Integer, MDMSubjectCodeMappingBean> mapSubjectCredit = new HashMap<>();
			if(CREDIT_ENROLL_YEAR <= Integer.parseInt(singleStudentData.getEnrollmentYear()))
			{
				try
				{
					mapPssDetail = creditService.getMappedPssDetail();
					mapSubjectCredit = creditService.getMappedSubjectCredit();
				}
				catch(Exception e)
				{
					throw new Exception(e);
				}
			}
			 
			String certificateNumberAndFilePath = pdfCreator.createTrascript(passList, singleStudentData,lateralStudentList, getAllProgramMap(), HALLTICKET_PATH, lastExamMonthYear, dao.getExamOrderMap(),semWiseSubjectMap ,getAllProgramMap(), dao,false,lateralSapidLastExamMonthYearMap,mapPssDetail,mapSubjectCredit);
			 logger.info(singleStudentData.getSapid()+  "  certificateNumberAndFilePath  {}",certificateNumberAndFilePath);
			return certificateNumberAndFilePath ;
		
		//service should trow errors
		//add loggers
		
	}
	public BulkTranscriptGenerationBean createTranscript(List<MarksheetBean> studentForSRList,StudentExamBean examBean)  {
		BulkTranscriptGenerationBean transcriptData = new  BulkTranscriptGenerationBean();
		TranscriptPDFCreator pdfCreator = new TranscriptPDFCreator();
		PDFMergerUtility merge = new PDFMergerUtility();
		String mergedFileName = HALLTICKET_PATH+"Transcript_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		transcriptData.setMergedFileName(mergedFileName);
		List<String> barcodePDFFilePathList = new ArrayList<String>();
		List<String> errorWhileGeneratingTranscript =new ArrayList<String>();
		for (MarksheetBean student : studentForSRList) {
			try {
				StudentExamBean singleStudentData =getSingleStudentsData(student.getSapid());
				singleStudentData.setLogoRequired(examBean.getLogoRequired());
				singleStudentData.setFromAdmin(examBean.getFromAdmin());
				if(singleStudentData.getIsLateral().equalsIgnoreCase("Y")){
					if(StringUtils.isEmpty(singleStudentData.getPreviousStudentId())) {
						throw new Exception("Previous Sapid Not Found ");
						}
					}
				logger.info("SingleStudentData {}",singleStudentData);

			String fileName=generateSingleTranscript(singleStudentData);
			logger.info(" Generated File Name {}",fileName);
				//move below code to a single method which takes sapid and logo require argument method name generatetranscript details
//				String sapid = student.getSapid();
//				ArrayList<PassFailExamBean> passList = getPassRecordsForStudentSelfTranscript(sapid);
//				double lastWrittenAttemptOrder = getLastWrittenPassExamMonthYear(sapid);
//				double lastAssignmentAttemptOrder = getLastAssignmentPassExamMonthYear(sapid);
//				
////				Added to find out last Exam month year of student to be printed on transcript pdf
//				String lastExamMonthYear = findLastYearMonthOfStudent(lastWrittenAttemptOrder,lastAssignmentAttemptOrder,sapid);
//				String logoRequired = Exambean.getLogoRequired();
//				StudentExamBean singleStudentData =getSingleStudentsData(sapid);
//				
////				ArrayList<StudentExamBean> lateralStudentList = new ArrayList<StudentExamBean>();
//				
//				
//				/*Added by Steffi to consider lateral students.
//				Check if student is lateral student and has done program with NGASCE in past. In this case his passed subjects will become waived off subjects
//				Course Waiver is not applicable for Jul2009 program Structure Students*/			
//				HashMap<String,ArrayList<String>> semWiseSubjectMap = getSemWiseSubjectsMap(singleStudentData.getProgram(),singleStudentData.getPrgmStructApplicable());
//				studentService.mgetWaivedOffSubjects(singleStudentData);
//				checkIfForLatral(singleStudentData);
//				checkNullForPassList(passList);
//				singleStudentData.setLogoRequired(logoRequired);
//				ArrayList<StudentExamBean> lateralStudentList = lateralStudentList(singleStudentData);
//				HashMap<String, String> lateralSapidLastExamMonthYearMap=getlateralSapidLastExamMonthYearMap(singleStudentData,semWiseSubjectMap,passList,lateralStudentList);
//				String certificateNumberAndFilePath = pdfCreator.createTrascript(passList, singleStudentData,lateralStudentList, getAllProgramMap(), TRANSCRIPT_PATH, lastExamMonthYear, dao.getExamOrderMap(),semWiseSubjectMap ,getAllProgramMap(), dao,false,lateralSapidLastExamMonthYearMap);

			logger.info("Merged File Name {}",mergedFileName);
	
			merge.setDestinationFileName(mergedFileName); // Setting the destination file path
			String transcript = HALLTICKET_PATH.replaceAll("\\\\", "/");
			logger.info("Full File Name for single file  {}",transcript + fileName);

			merge.addSource(transcript + fileName); // Add all source files, to be merged 
			barcodePDFFilePathList.add(fileName);
			logger.info("barcodePDFFilePathList {}",barcodePDFFilePathList);
			transcriptData.setBarcodePDFFilePathList(barcodePDFFilePathList);
	
		}catch (Exception e) {
			errorWhileGeneratingTranscript.add(student.getServiceRequestId());
//			e.printStackTrace();
			 
			logger.info("Adding Error Srid's in the error list {}  ",errorWhileGeneratingTranscript);
			transcriptData.setErrorwhileCreatingSuccessfull(errorWhileGeneratingTranscript);
		}
			
		}
		mergeDocuments(merge);
		logger.info("Merging the documents Sucessfully");
		return transcriptData;
		
	}
	private void mergeDocuments(PDFMergerUtility merge) {
		try {
			logger.info("Merging the documents");
			merge.mergeDocuments();
		} catch (COSVisitorException | IOException e) {
			
			e.printStackTrace();
		}
	}
	public List<MarksheetBean> checkDataOnSrId(String successList) throws Exception {
		logger.info("SuccessList  {}",successList);
		if (StringUtils.isEmpty(successList)){
				throw new Exception("Error In Generating Transcript For All Sr Ids  ");
				}
		PassFailExamBean passBean = new PassFailExamBean();
		passBean.setServiceRequestIdList(successList);
		List<MarksheetBean> studentForSRList=passFailDao.getStudentsForSR(passBean);
		if (CollectionUtils.isEmpty(studentForSRList)) {
			throw new Exception("No Record Found");
		}
		return studentForSRList;
		
	}
	public List<String> convertCommaSepratedToList(String commaSeprated) {
		logger.info("Converting CommaSeprated List {} into the List Of Strings ",commaSeprated);
		String commaSepratedList = generateCommaSeparatedList(commaSeprated);
		commaSepratedList=commaSepratedList.replaceAll("^,", "");
		commaSepratedList.trim(); 
		return Stream.of(commaSepratedList.split(",")).map(String::trim).collect(Collectors.toList());
	}
	public List<String> addingSuccessSridsInList(List<String> listOfSrId) throws Exception{
		String srid=generateCommaSepratedListFromList(listOfSrId);
		List<String >successList= new  ArrayList<String>();
		successList= serviceRequestDao.checkingForTranscript(srid);
		 logger.info( "  successList  {}",successList);
		 if(CollectionUtils.isEmpty(successList))
			 throw new Exception("No ServiceRequest id's are Matching");
		return successList;
	}
	public String generateCommaSepratedErrorList(List<String> allList,List<String> errorList){
		logger.info("Generating ErrorList Srid in CommaSeprated "); 
		for(String success :errorList) {
			if ((allList.contains(success))) {
				allList.remove(success);
				
			}
		}
		return generateCommaSepratedListFromList(allList);
	}
	private String generateCommaSepratedListFromList(List<String> successList) {
		String successForTranscript= String.join(",", successList);
		return successForTranscript;
		
	}
	private String generateCommaSeparatedList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+", ",");
		if(commaSeparatedList.endsWith(",")){
			commaSeparatedList = commaSeparatedList.substring(0,  commaSeparatedList.length()-1);
		}
		return commaSeparatedList;
	}
	public HashMap<String, ProgramExamBean> getAllProgramMap(){
		if(CollectionUtils.isEmpty(this.programDetailsMap )){
			this.programDetailsMap = dao.getProgramMap();
		}
		return programDetailsMap;
	}
	
	public ArrayList<PassFailExamBean> removeLateralDuplicateSubjectsPG_MBAPolicy(final ArrayList<PassFailExamBean> passList,
			final String currentProgram, final HashMap<String, ArrayList<String>> semWiseMBASubjectMap) {

		return (ArrayList<PassFailExamBean>) passList.stream()
				.filter(e -> !(semWiseMBASubjectMap.entrySet().stream()
						.filter(f -> f.getKey().equals("3") || f.getKey().equals("4")).map(Map.Entry::getValue)
						.collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList())
						.contains(e.getSubject()) && !e.getProgram().equals(currentProgram)))
				.collect(Collectors.toList());
	}

	public boolean checkLateralStudentFromPGToMBAProgram(final String isLateral, final String currentProgram, final String old_program) {
		/* && old_program.startsWith("PG") - Should have old program check but DB has incorrect values where in old program and new Program are same */
		
		if (isLateral.equalsIgnoreCase("Y") && currentProgram.startsWith("MBA") ) {
			return true;
		}
		return false;
	}
	

}