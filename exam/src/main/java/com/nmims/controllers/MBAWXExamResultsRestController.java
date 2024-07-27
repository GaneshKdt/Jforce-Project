package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.MBATranscriptBean;
import com.nmims.beans.MBAWXExamMarksheetGenerationChecks;
import com.nmims.beans.MBAWXExamResultForSubject;
import com.nmims.beans.MBAWXExamResultsBean;
import com.nmims.beans.MBAWXPassFailStatus;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentSubjectConfigExamBean;
import com.nmims.beans.StudentsTestDetailsExamBean;
import com.nmims.daos.MBAWXExamResultsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TestDAO;
import com.nmims.daos.UpgradResultProcessingDao;
import com.nmims.helpers.MBAMarksheetHelper;
import com.nmims.helpers.MBAWXResultsProcessingHelper;
import com.nmims.helpers.MBAWXTranscriptPDFCreator;
import com.nmims.interfaces.DissertationGradesheet_TranscriptService;
import com.nmims.interfaces.DissertationQ8ResultService;
import com.nmims.interfaces.DissertationResultProcessingService;


@RestController
@RequestMapping("m")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MBAWXExamResultsRestController {
	
	@Value("${HALLTICKET_PATH}")
	private String HALLTICKET_PATH;

	@Autowired
	ApplicationContext act;
	
	private HashMap<String, ProgramExamBean> programDetailsMap = null;
	
	@Autowired
	DissertationResultProcessingService dissertationServiceQ7;
	
	@Autowired
	DissertationQ8ResultService dissertationServiceQ8;
	
	@Autowired
	UpgradResultProcessingDao upgradResultProcessingDao;

	@Value("${CURRENT_MBAX_ACAD_MONTH}")
	private String CURRENT_MBAX_ACAD_MONTH; 
	
	@Value("${CURRENT_MBAX_ACAD_YEAR}")
	private String CURRENT_MBAX_ACAD_YEAR;
	
	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH; 
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR;

	@Autowired
	MBAWXExamResultsDAO mbawxExamResultsDAO;
	
	@Autowired
	MBAWXResultsProcessingHelper resultsProcessingHelper;

	@Autowired
	TestDAO testDAO;

	@Autowired
	MBAMarksheetHelper marksheetHelper;
	
	@Autowired
	DissertationGradesheet_TranscriptService dissertationGradeService;
	
	// The constants used 
	private String IA_MAX_SCORE = "70";
	private int TEE_PASS_SCORE = 12;
	private int RESIT_PASS_SCORE = 50;
	private String PROJECT_EXAM_TEE_SCORE = "60";
	private String PROJECT_EXAM_TEE_SCORE_OCT = "80";
	private String PROJECT_IA_MAX_SCORE = "40";
	private String PROJECT_IA_MAX_SCORE_OCT = "20";
	private String RESIT_EXAM_TEE_SCORE = "100";
	private String TOTAL_SCORE_MAX = "100";	
	private String MBAX_IA_MAX_SCORE = "60";
	private String CAPSTONE_PROJECT_MAX_SCORE = "80";
	private String CAPSTONE_PROJECT_IA_MAX_SCORE = "20";

	private String MBAX_PROJECT_EXAM_TEE_SCORE = "75";
	private String MBAX_PROJECT_IA_MAX_SCORE = "25";

	private int MSC_TEE_PASS_SCORE = 16;
	private String MSC_TEE_SCORE = "40";
	private String MSC_IA_MAX_SCORE = "60";
	private String PDDM_TEE_MAX_SCORE = "70";
	private String IA_ONLY_MAX_SCORE = "100";
	private String IA_MASTER_DESSERTATION_Q7_MAX_SCORE = "60";
	
	private final static int IA_MASTER_DISSERTATION_Q7_SEM = 7;
	
	private final static int IA_MASTER_DISSERTATION_Q8_SEM = 8;
	
	private final static int IA_MASTER_DISSERTATION_Q7_SUBJECT_ID = 1990;
	
	private final static int IA_MASTER_DISSERTATION_Q8_SUBJECT_ID = 1991;
	
	//Non-Graded Certificate programs consumerProgramStructureIds list. 
	public static final List<Integer> NON_GRADED_MASTER_KEY_LIST = (List<Integer>) Arrays.asList(142,143,144,145,146,147,148,149);
	
	
	@PostMapping(path = "/getCurrentSubjectResultsForStudent", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<MBAWXExamResultsBean> getCurrentSubjectResultsForStudent(@RequestBody StudentExamBean student){
		MBAWXExamResultsBean responseBean = new MBAWXExamResultsBean();

		String sapid = student.getSapid();
		if(sapid == null) {
			responseBean.setStatus("fail");
			responseBean.setErrorMessage("No sapid");
			return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
		} else {
			try {
				List<MBAWXExamResultForSubject> currentSubjects = getCurrentResultsForStudent(sapid);
				sortExamResultsByStartDate(currentSubjects);
				responseBean.setData(currentSubjects);
				responseBean.setStatus("success");
				responseBean.setErrorMessage(null);
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}catch (Exception e) {
				responseBean.setStatus("fail");
				responseBean.setErrorMessage("Internal Server Error");
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}
		}
	}
	
	@PostMapping(path = "/getCurrentSubjectResultsForMbaXStudent", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<MBAWXExamResultsBean> getCurrentSubjectResultsForMbaXStudent(@RequestBody StudentExamBean student){
		MBAWXExamResultsBean responseBean = new MBAWXExamResultsBean();

		String sapid = student.getSapid();
		if(sapid == null) {
			responseBean.setStatus("fail");
			responseBean.setErrorMessage("No sapid");
			return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
		} else {
			try {
				List<MBAWXExamResultForSubject> currentSubjects = getCurrentResultsForMbaXStudent(sapid);
				sortExamResultsByStartDate(currentSubjects);
				responseBean.setData(currentSubjects);
				responseBean.setStatus("success");
				responseBean.setErrorMessage(null);
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}catch (Exception e) {
				responseBean.setStatus("fail");
				responseBean.setErrorMessage("Internal Server Error");
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}
		}
	}
	
	@PostMapping(path = "/getPassFailStatusForStudent", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<MBAWXExamResultsBean> getPassFailStatusForStudent(@RequestBody StudentExamBean student){
		MBAWXExamResultsBean responseBean = new MBAWXExamResultsBean();

		String sapid = student.getSapid();
		if(sapid == null) {
			responseBean.setStatus("fail");
			responseBean.setErrorMessage("No sapid");
			return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
		} else {
			try {
				List<MBAWXExamResultForSubject> passFailStatus = getPassFailStatusForStudent(sapid);
				sortExamResultsByExamStartDateDesc(passFailStatus);
				responseBean.setData(passFailStatus);
				responseBean.setStatus("success");
				responseBean.setErrorMessage(null);
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}catch (Exception e) {
				responseBean.setStatus("fail");
				responseBean.setErrorMessage("Internal Server Error");
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}
		}
	}
	
	@PostMapping(path = "/getMbaXPassFailStatusForStudent", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<MBAWXExamResultsBean> getMbaXPassFailStatusForStudent(@RequestBody StudentExamBean student){
		MBAWXExamResultsBean responseBean = new MBAWXExamResultsBean();

		String sapid = student.getSapid();
		if(sapid == null) {
			responseBean.setStatus("fail");
			responseBean.setErrorMessage("No sapid");
			return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
		} else {
			try {
				List<MBAWXExamResultForSubject> passFailStatus = getMbaXPassFailStatusForStudent(sapid);
				sortExamResultsByExamStartDateDesc(passFailStatus);
				responseBean.setData(passFailStatus);
				responseBean.setStatus("success");
				responseBean.setErrorMessage(null);
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}catch (Exception e) {
				responseBean.setStatus("fail");
				responseBean.setErrorMessage("Internal Server Error");
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}
		}
	}

	@PostMapping(path = "/getMarksHistoryForStudentNew", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<MBAWXExamResultsBean> getMarksHistoryForStudentNew(@RequestBody StudentExamBean student){
		MBAWXExamResultsBean responseBean = new MBAWXExamResultsBean();

		String sapid = student.getSapid();
		if(sapid == null) {
			responseBean.setStatus("fail");
			responseBean.setErrorMessage("No sapid");
			return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
		} else {
			try {
				List<MBAWXExamResultForSubject> marksHistory = getMarksHistoryForStudent(sapid);
				sortExamResultsByExamStartDateDesc(marksHistory);
				responseBean.setData(marksHistory);
				responseBean.setStatus("success");
				responseBean.setErrorMessage(null);
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}catch (Exception e) {
				
				responseBean.setStatus("fail");
				responseBean.setErrorMessage("Internal Server Error");
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}
		}
	}

	@PostMapping(path = "/getAvailableMarksheetsForStudent", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<MBAWXExamResultsBean> getAvailableMarksheetsForStudent(@RequestBody StudentExamBean student){
		MBAWXExamResultsBean responseBean = new MBAWXExamResultsBean();


		String sapid = student.getSapid();
		if(sapid == null) {
			responseBean.setStatus("fail");
			responseBean.setErrorMessage("No sapid");
			return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
		} else {
			try {
				List<MBAWXExamResultForSubject> marksheetDownloads = getAvailableMarksheetDownloadsForStudent(sapid);
//				sortExamResultsByStartDate(marksheetDownloads);
				responseBean.setData(marksheetDownloads);
				responseBean.setStatus("success");
				responseBean.setErrorMessage(null);
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}catch (Exception e) {
				responseBean.setStatus("fail");
				responseBean.setErrorMessage("Internal Server Error");
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}
		}
	}
	
	@PostMapping(path = "/getAvailableMarksheetsForMbaXStudent", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<MBAWXExamResultsBean> getAvailableMarksheetsForMbaXStudent(@RequestBody StudentExamBean student){
		MBAWXExamResultsBean responseBean = new MBAWXExamResultsBean();


		String sapid = student.getSapid();
		if(sapid == null) {
			responseBean.setStatus("fail");
			responseBean.setErrorMessage("No sapid");
			return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
		} else {
			try {
				List<MBAWXExamResultForSubject> marksheetDownloads = getAvailableMarksheetDownloadsForMbaXStudent(sapid);
				
			//	sortExamResultsByStartDate(marksheetDownloads);
				responseBean.setData(marksheetDownloads);
				responseBean.setStatus("success");
				responseBean.setErrorMessage(null);
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}catch (Exception e) {
				
				responseBean.setStatus("fail");
				responseBean.setErrorMessage("Internal Server Error");
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}
		}
	}

	
	@PostMapping(path = "/getMarksHistoryForStudent", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<MBAWXExamResultsBean> getMarksHistoryForStudent(@RequestBody StudentExamBean student){
		MBAWXExamResultsBean responseBean = new MBAWXExamResultsBean();


		String sapid = student.getSapid();
		if(sapid == null) {
			responseBean.setStatus("fail");
			responseBean.setErrorMessage("No sapid");
			return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
		} else {
			try {
				List<MBAWXExamResultForSubject> marksHistory = getTEEMarksHistoryForStudent(sapid);
				sortExamResultsByExamStartDateDesc(marksHistory);
				responseBean.setData(marksHistory);
				
				responseBean.setStatus("success");
				responseBean.setErrorMessage(null);
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}catch (Exception e) {
				
				responseBean.setStatus("fail");
				responseBean.setErrorMessage("Internal Server Error");
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}
		}
	}
	
	@PostMapping(path = "/getMarksHistoryForMbaXStudent", consumes = "application/json", produces = "application/json; charset=UTF-8")
	public ResponseEntity<MBAWXExamResultsBean> getMarksHistoryForMbaXStudent(@RequestBody StudentExamBean student){
		MBAWXExamResultsBean responseBean = new MBAWXExamResultsBean();


		String sapid = student.getSapid();
		if(sapid == null) {
			responseBean.setStatus("fail");
			responseBean.setErrorMessage("No sapid");
			return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
		} else {
			try {
				List<MBAWXExamResultForSubject> marksHistory = getTEEMarksHistoryForMbaXStudent(sapid);
				sortExamResultsByExamStartDateDesc(marksHistory);
				responseBean.setData(marksHistory);
				
				responseBean.setStatus("success");
				responseBean.setErrorMessage(null);
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}catch (Exception e) {
				
				responseBean.setStatus("fail");
				responseBean.setErrorMessage("Internal Server Error");
				return new ResponseEntity<MBAWXExamResultsBean>(responseBean ,HttpStatus.OK);
			}
		}
	}
	
	
	@RequestMapping(value = "/testMarksheet")
	public ResponseEntity<MBATranscriptBean> testMarksheet(HttpServletRequest request){
		String sapid = (String) request.getParameter("sapid");
		
		MBATranscriptBean transcriptBean = marksheetHelper.getTranscriptBeanForSapid(sapid);
		
		try {
			MBAWXTranscriptPDFCreator transcriptHelper = new MBAWXTranscriptPDFCreator();
			transcriptHelper.createTrascript(transcriptBean, HALLTICKET_PATH, getAllProgramMap());
		} catch (Exception e) {
			
		}
		return new ResponseEntity<MBATranscriptBean>(transcriptBean, HttpStatus.OK);
	}
	
	
	
	private List<MBAWXExamResultForSubject> getAvailableMarksheetDownloadsForStudent(String sapid) {

		List<MBAWXExamResultForSubject> completedSemestersList = new ArrayList<MBAWXExamResultForSubject>();
		List<MBAWXExamMarksheetGenerationChecks> semMonthYearResultsMap ;
		
		StudentExamBean studentBean = mbawxExamResultsDAO.getLatestRegistrationForStudent(sapid);

		if(NON_GRADED_MASTER_KEY_LIST.contains(Integer.parseInt(studentBean.getConsumerProgramStructureId())))
			semMonthYearResultsMap = getNonGradedSemMonthYearPassFailMapForStudent(sapid);
		else
			semMonthYearResultsMap = getSemMonthYearPassFailMapForStudent(sapid);
		
		Map<String, Integer> semTotalSubjectsMap = getSemAndSubjectMapForStudent(sapid);
		
		for (MBAWXExamMarksheetGenerationChecks semMonthYearResultCount : semMonthYearResultsMap) {
			// All timebound ids of the same Month/Year should have the same semester
			String term = semMonthYearResultCount.getTerm();

			// list of subjects in this sem.
			int totalSubjectsInThisSem = semTotalSubjectsMap.get(term);
			int totalSubjectResultsInCycle = semMonthYearResultCount.getNumberOfPassFailEntries();
			boolean allSubjectsPresent = totalSubjectResultsInCycle == totalSubjectsInThisSem;

			if(allSubjectsPresent) {
				MBAWXExamResultForSubject semesterDetails = new MBAWXExamResultForSubject();
				semesterDetails.setSapid(sapid);
				semesterDetails.setTerm(term);
				semesterDetails.setAcadsMonth(semMonthYearResultCount.getAcadsMonth());
				semesterDetails.setAcadsYear(semMonthYearResultCount.getAcadsYear());
				semesterDetails.setExamMonth(semMonthYearResultCount.getExamMonth());
				semesterDetails.setExamYear(semMonthYearResultCount.getExamYear());
				completedSemestersList.add(semesterDetails);
			}
		}
		return completedSemestersList;
	}
	
	private List<MBAWXExamResultForSubject> getAvailableMarksheetDownloadsForMbaXStudent(String sapid) {

		List<MBAWXExamResultForSubject> completedSemestersList = new ArrayList<MBAWXExamResultForSubject>();
		List<MBAWXExamMarksheetGenerationChecks> semMonthYearResultsMap = getSemMonthYearPassFailMapForMbaXStudent(sapid);
		Map<String, Integer> semTotalSubjectsMap = getSemAndSubjectMapForStudent(sapid);

		for (MBAWXExamMarksheetGenerationChecks semMonthYearResultCount : semMonthYearResultsMap) {
			// All timebound ids of the same Month/Year should have the same semester
			String term = semMonthYearResultCount.getTerm();

			// list of subjects in this sem.
			int totalSubjectsInThisSem = semTotalSubjectsMap.get(term);
			int totalSubjectResultsInCycle = semMonthYearResultCount.getNumberOfPassFailEntries();
			boolean allSubjectsPresent = totalSubjectResultsInCycle == totalSubjectsInThisSem;

			if(allSubjectsPresent) {
				MBAWXExamResultForSubject semesterDetails = new MBAWXExamResultForSubject();
				semesterDetails.setSapid(sapid);
				semesterDetails.setTerm(term);
				semesterDetails.setAcadsMonth(semMonthYearResultCount.getAcadsMonth());
				semesterDetails.setAcadsYear(semMonthYearResultCount.getAcadsYear());
				semesterDetails.setExamMonth(semMonthYearResultCount.getExamMonth());
				semesterDetails.setExamYear(semMonthYearResultCount.getExamYear());
				semesterDetails.setTimeboundId(semMonthYearResultCount.getTimeboundId());
				//semesterDetails.setStartDate(semMonthYearResultCount.get);
				
				completedSemestersList.add(semesterDetails);
			}
		}
		return completedSemestersList;
	}

	private List<MBAWXExamMarksheetGenerationChecks> getNonGradedSemMonthYearPassFailMapForStudent(String sapid) {
		List<StudentSubjectConfigExamBean> timeboundIdsForStudent = mbawxExamResultsDAO.getNonGradedAllApplicableSubjectDetailsForStudent(sapid);
		Map<String, StudentSubjectConfigExamBean> termYearMonthMap = new HashMap<String, StudentSubjectConfigExamBean>();

		// first sort all timebound ids by MonYear. this is done to group all timebound ids
		for (StudentSubjectConfigExamBean timeboundIdDetails : timeboundIdsForStudent) {
			String termYearMonth = timeboundIdDetails.getSem() + timeboundIdDetails.getAcadYear() + timeboundIdDetails.getAcadMonth();
			termYearMonthMap.put(termYearMonth, timeboundIdDetails);
		}

		// use year/month/sem to form a map of <sem, <monthYear, no of subjects with exams>> map
		List<MBAWXExamMarksheetGenerationChecks> semYearMonthPassFailList = new ArrayList<MBAWXExamMarksheetGenerationChecks>();

		for (Map.Entry<String, StudentSubjectConfigExamBean> semYearMonthEntry : termYearMonthMap.entrySet()) {
			StudentSubjectConfigExamBean timeboundIdDetails = semYearMonthEntry.getValue();
			String sem = timeboundIdDetails.getSem();
			String year = timeboundIdDetails.getAcadYear();
			String month = timeboundIdDetails.getAcadMonth();
			
			int numberOfPassFailEntriesForMonthYear = mbawxExamResultsDAO.getNonGradedNumberOfPassFailForTermYearMonth(sapid, sem);
			
			MBAWXExamMarksheetGenerationChecks semYearMonthPassFail = new MBAWXExamMarksheetGenerationChecks();
			semYearMonthPassFail.setNumberOfPassFailEntries(numberOfPassFailEntriesForMonthYear);
			semYearMonthPassFail.setAcadsMonth(month);
			semYearMonthPassFail.setAcadsYear(year);
			semYearMonthPassFail.setExamMonth(timeboundIdDetails.getExamMonth());
			semYearMonthPassFail.setExamYear(timeboundIdDetails.getExamYear());
			semYearMonthPassFail.setSapid(sapid);
			semYearMonthPassFail.setTerm(sem);
			semYearMonthPassFailList.add(semYearMonthPassFail);
		}
		return semYearMonthPassFailList;
	}

	
	private List<MBAWXExamMarksheetGenerationChecks> getSemMonthYearPassFailMapForStudent( String sapid ) {

		List<StudentSubjectConfigExamBean> timeboundIdsForStudent = mbawxExamResultsDAO.getAllApplicableSubjectDetailsForStudent(sapid);
		Map<String, StudentSubjectConfigExamBean> termYearMonthMap = new HashMap<String, StudentSubjectConfigExamBean>();
		
		boolean dissertationQ8Flag = timeboundIdsForStudent.stream().filter(sem -> IA_MASTER_DISSERTATION_Q7_SEM == (Integer.parseInt(sem.getSem()))).findFirst().isPresent();

		if(dissertationQ8Flag) {
			StudentSubjectConfigExamBean student = dissertationGradeService.getPassFailForQ8Timebound(sapid);
			if(null!=student) {
				timeboundIdsForStudent.add(student);
			}
		}
		
		// first sort all timebound ids by MonYear. this is done to group all timebound ids
		for (StudentSubjectConfigExamBean timeboundIdDetails : timeboundIdsForStudent) {
			String termYearMonth = timeboundIdDetails.getSem() + timeboundIdDetails.getAcadYear() + timeboundIdDetails.getAcadMonth();
			termYearMonthMap.put(termYearMonth, timeboundIdDetails);
		}

		// use year/month/sem to form a map of <sem, <monthYear, no of subjects with exams>> map
		List<MBAWXExamMarksheetGenerationChecks> semYearMonthPassFailList = new ArrayList<MBAWXExamMarksheetGenerationChecks>();

		for (Map.Entry<String, StudentSubjectConfigExamBean> semYearMonthEntry : termYearMonthMap.entrySet()) {
			StudentSubjectConfigExamBean timeboundIdDetails = semYearMonthEntry.getValue();
			String sem = timeboundIdDetails.getSem();
			String year = timeboundIdDetails.getAcadYear();
			String month = timeboundIdDetails.getAcadMonth();
			
			int numberOfPassFailEntriesForMonthYear = mbawxExamResultsDAO.getNumberOfPassFailForTermYearMonth(
				sapid, year, month, sem
				
				
			);
			
			if(IA_MASTER_DISSERTATION_Q7_SEM == Integer.parseInt(sem)) {
				int count = dissertationGradeService.checkSapidExistForQ7(sapid);
				numberOfPassFailEntriesForMonthYear +=  count;
			}
			
			if(IA_MASTER_DISSERTATION_Q8_SEM == Integer.parseInt(sem)) {
				int count = dissertationGradeService.checkSapidExistForQ8(sapid);
				numberOfPassFailEntriesForMonthYear +=  count;
			}
			
	
			MBAWXExamMarksheetGenerationChecks semYearMonthPassFail = new MBAWXExamMarksheetGenerationChecks();
			semYearMonthPassFail.setNumberOfPassFailEntries(numberOfPassFailEntriesForMonthYear);
			semYearMonthPassFail.setAcadsMonth(month);
			semYearMonthPassFail.setAcadsYear(year);
			semYearMonthPassFail.setExamMonth(timeboundIdDetails.getExamMonth());
			semYearMonthPassFail.setExamYear(timeboundIdDetails.getExamYear());
			semYearMonthPassFail.setSapid(sapid);
			semYearMonthPassFail.setTerm(sem);
			semYearMonthPassFailList.add(semYearMonthPassFail);
		}
		return semYearMonthPassFailList;
	}
	
	private List<MBAWXExamMarksheetGenerationChecks> getSemMonthYearPassFailMapForMbaXStudent( String sapid ) {
		List<StudentSubjectConfigExamBean> timeboundIdsForStudent  = new  ArrayList<StudentSubjectConfigExamBean>(); 
		timeboundIdsForStudent = mbawxExamResultsDAO.getAllSemOfAcadYearMonthForStructureChangeStudent(sapid);
		if(timeboundIdsForStudent.size() == 0) {
			timeboundIdsForStudent = mbawxExamResultsDAO.getAllApplicableSubjectDetailsForStudentMBAX(sapid);
		}
		Map<String, StudentSubjectConfigExamBean> termYearMonthMap = new HashMap<String, StudentSubjectConfigExamBean>();

		// first sort all timebound ids by MonYear. this is done to group all timebound ids
		for (StudentSubjectConfigExamBean timeboundIdDetails : timeboundIdsForStudent) {
			String termYearMonth = timeboundIdDetails.getSem() + timeboundIdDetails.getAcadYear() + timeboundIdDetails.getAcadMonth();

			termYearMonthMap.put(termYearMonth, timeboundIdDetails);
		}

		// use year/month/sem to form a map of <sem, <monthYear, no of subjects with exams>> map
		List<MBAWXExamMarksheetGenerationChecks> semYearMonthPassFailList = new ArrayList<MBAWXExamMarksheetGenerationChecks>();
		
		for (Map.Entry<String, StudentSubjectConfigExamBean> semYearMonthEntry : termYearMonthMap.entrySet()) {
			
			StudentSubjectConfigExamBean timeboundIdDetails = semYearMonthEntry.getValue();
			String sem = timeboundIdDetails.getSem();
			String year = timeboundIdDetails.getAcadYear();
			String month = timeboundIdDetails.getAcadMonth();
			int numberOfPassFailEntriesForMonthYear = 0;
			numberOfPassFailEntriesForMonthYear = mbawxExamResultsDAO.getNumberOfMbaXPassFailForStructureChangeStudent(sapid, sem);
			if(numberOfPassFailEntriesForMonthYear == 0 ) {
				numberOfPassFailEntriesForMonthYear = mbawxExamResultsDAO.getNumberOfMbaXPassFailForTermYearMonth(
					sapid, year, month, sem
				);
			}
			MBAWXExamMarksheetGenerationChecks semYearMonthPassFail = new MBAWXExamMarksheetGenerationChecks();
			semYearMonthPassFail.setNumberOfPassFailEntries(numberOfPassFailEntriesForMonthYear);
			semYearMonthPassFail.setAcadsMonth(month);
			semYearMonthPassFail.setAcadsYear(year);
			semYearMonthPassFail.setExamMonth(timeboundIdDetails.getExamMonth());
			semYearMonthPassFail.setExamYear(timeboundIdDetails.getExamYear());
			semYearMonthPassFail.setSapid(sapid);
			semYearMonthPassFail.setTerm(sem);
			semYearMonthPassFailList.add(semYearMonthPassFail);
		}
		return semYearMonthPassFailList;
	}
	
	private Map<String, Integer> getSemAndSubjectMapForStudent(String sapid) {
		Map<String, Integer> semSubjectCountMap = new HashMap<String, Integer>();
		// get all subjects and sort them by sem
		List<ProgramSubjectMappingExamBean> subjectMappingBeans = new ArrayList<ProgramSubjectMappingExamBean>();
		subjectMappingBeans = mbawxExamResultsDAO.getAllProgramSemSubjectsForStructureChangeStudent(sapid);
		if(subjectMappingBeans.size() == 0) {
			subjectMappingBeans = mbawxExamResultsDAO.getAllProgramSemSubjectsStudent(sapid);	
		}

		for (ProgramSubjectMappingExamBean programSubjectMappingBean : subjectMappingBeans) {
			String sem = programSubjectMappingBean.getSem();
		
			if(("3".equalsIgnoreCase(sem) || "4".equalsIgnoreCase(sem) ) && 
			   ("111".equals(programSubjectMappingBean.getConsumerProgramStructureId()) || "151".equals(programSubjectMappingBean.getConsumerProgramStructureId()))	
			  ) {
				semSubjectCountMap.put(sem, 5);
			}else if ("5".equalsIgnoreCase(sem) && "151".equals(programSubjectMappingBean.getConsumerProgramStructureId())) {
				semSubjectCountMap.put(sem, 3);
			}else if("4".equalsIgnoreCase(sem) && "131".equals(programSubjectMappingBean.getConsumerProgramStructureId()) ) {
				semSubjectCountMap.put(sem, 3);
			}else if("5".equalsIgnoreCase(sem) && ("131".equals(programSubjectMappingBean.getConsumerProgramStructureId()) ||
					"158".equals(programSubjectMappingBean.getConsumerProgramStructureId()))) {
				semSubjectCountMap.put(sem, 4);
			}else if (("3".equalsIgnoreCase(sem) && 
					("160".equals(programSubjectMappingBean.getConsumerProgramStructureId())))) {
				semSubjectCountMap.put(sem, 4);
			}else if (("4".equalsIgnoreCase(sem) || "5".equalsIgnoreCase(sem)) && 
					("160".equals(programSubjectMappingBean.getConsumerProgramStructureId()))) {
				semSubjectCountMap.put(sem, 5);
			}else if (("6".equalsIgnoreCase(sem) &&  
					("160".equals(programSubjectMappingBean.getConsumerProgramStructureId())))) {
				semSubjectCountMap.put(sem, 1);
			}else {
				int totalSubjectsForSem = semSubjectCountMap.containsKey(sem) ? semSubjectCountMap.get(sem) : 0;
				totalSubjectsForSem ++;
				semSubjectCountMap.put(sem, totalSubjectsForSem);
			}
			
		}
	return semSubjectCountMap;
	}
	
	private void sortExamResultsByStartDate(List<MBAWXExamResultForSubject> list) {
		
		Collections.sort(list, new Comparator<MBAWXExamResultForSubject>() {
			@Override
			public int compare(MBAWXExamResultForSubject o1, MBAWXExamResultForSubject o2) {
				return o1.getStartDate().compareTo( o2.getStartDate() );
			}
		}); 
	}


	private void sortExamResultsByExamStartDateDesc(List<MBAWXExamResultForSubject> list) {

		Collections.sort(list, new Comparator<MBAWXExamResultForSubject>() {
			@Override
			public int compare(MBAWXExamResultForSubject o1, MBAWXExamResultForSubject o2) {
				if(o1.getExamDate() != null && o2.getExamDate() == null) {
					return -1;
				} else if(o1.getExamDate() == null && o2.getExamDate() != null) {
					return 1;
				} else if(o1.getExamDate() == null && o2.getExamDate() == null) {
					return 0;
				}
				return o2.getExamDate().compareTo( o1.getExamDate() );
			}
		}); 
	}
	
	private List<MBAWXExamResultForSubject> getCurrentResultsForStudent(String sapid) {

		List<StudentSubjectConfigExamBean> timeboundIdsForStudent = mbawxExamResultsDAO.getCurrentTimeboundSubjectDetailsForStudent(sapid, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR);
		if (timeboundIdsForStudent != null) {
			List<MBAWXExamResultForSubject> currentSubjects = new ArrayList<MBAWXExamResultForSubject>();
			
			for (StudentSubjectConfigExamBean timeboundSubject : timeboundIdsForStudent) {
				MBAWXPassFailStatus passFailStatus = getMBAWXPassFailStatus(sapid, timeboundSubject.getId(), timeboundSubject.getPrgm_sem_subj_id(), timeboundSubject.getHasIA(),  timeboundSubject.getHasTEE());
				checkPassFailBean(passFailStatus);
				MBAWXExamResultForSubject subjectResult = getSubjectResult(sapid, timeboundSubject, passFailStatus);
				currentSubjects.add(subjectResult);
			}
			return currentSubjects;
		}
		return new ArrayList<MBAWXExamResultForSubject>();
	}
	
//	private List<MBAWXExamResultForSubject> getCurrentResultsForStudent(String sapid) {
//
//		List<StudentSubjectConfigExamBean> timeboundIdsForStudent = mbawxExamResultsDAO.getCurrentTimeboundSubjectDetailsForStudent(sapid, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR);
//		if (timeboundIdsForStudent != null) {
//			List<MBAWXExamResultForSubject> currentSubjects = new ArrayList<MBAWXExamResultForSubject>();
//			
//			for (StudentSubjectConfigExamBean timeboundSubject : timeboundIdsForStudent) {
//				MBAWXPassFailStatus passFailStatus = getMBAWXPassFailStatus(sapid, timeboundSubject.getId(), timeboundSubject.getPrgm_sem_subj_id(), timeboundSubject.getHasIA(),  timeboundSubject.getHasTEE());
//				if(null!=passFailStatus) {
//				if (IA_MASTER_DISSERTATION_Q7_SUBJECT_ID == (Integer.parseInt(timeboundSubject.getPrgm_sem_subj_id()))) {
//					MBAWXExamResultForSubject dissertationResult =	dissertationServiceQ7.getMastersDissertationResult( sapid,  timeboundSubject,  passFailStatus, timeboundSubject.getHasIA(),  timeboundSubject.getHasTEE());
//					if(null!=dissertationResult) {
//						currentSubjects.add(dissertationResult);
//					}
//				}else if(IA_MASTER_DISSERTATION_Q8_SUBJECT_ID == Integer.parseInt(timeboundSubject.getPrgm_sem_subj_id())) {
//					MBAWXExamResultForSubject dissertationResult =	dissertationServiceQ8.getMastersDissertationResult( sapid,  timeboundSubject,  passFailStatus, timeboundSubject.getHasIA(),  timeboundSubject.getHasTEE());
//					if(null!=dissertationResult) {
//						currentSubjects.add(dissertationResult);
//					}
//				}
//				else {
//				checkPassFailBean(passFailStatus);
//				MBAWXExamResultForSubject subjectResult = getSubjectResult(sapid, timeboundSubject, passFailStatus);
//				currentSubjects.add(subjectResult);
//				}
//				}
//			}
//			return currentSubjects;
//		}
//		return new ArrayList<MBAWXExamResultForSubject>();
//	}
	

	private List<MBAWXExamResultForSubject> getCurrentResultsForMbaXStudent(String sapid) {

		List<StudentSubjectConfigExamBean> timeboundIdsForStudent = mbawxExamResultsDAO.getCurrentTimeboundSubjectDetailsForStudent(sapid, CURRENT_MBAX_ACAD_MONTH, CURRENT_MBAX_ACAD_YEAR);
		if (timeboundIdsForStudent != null) {
			List<MBAWXExamResultForSubject> currentSubjects = new ArrayList<MBAWXExamResultForSubject>();
			
			for (StudentSubjectConfigExamBean timeboundSubject : timeboundIdsForStudent) {
				MBAWXPassFailStatus passFailStatus = getMBAXPassFailStatus(sapid, timeboundSubject.getId(), 
						timeboundSubject.getPrgm_sem_subj_id()
						);
				checkPassFailBean(passFailStatus);
				MBAWXExamResultForSubject subjectResult = getMbaXSubjectResult(sapid, timeboundSubject, passFailStatus);
				currentSubjects.add(subjectResult);
			}
			return currentSubjects;
		}
		return new ArrayList<MBAWXExamResultForSubject>();
	}
	
	private List<MBAWXExamResultForSubject> getPassFailStatusForStudent(String sapid) {

		List<MBAWXExamResultForSubject> passFailResults = new ArrayList<MBAWXExamResultForSubject>();
		List<StudentSubjectConfigExamBean> timeboundIdsForStudent = mbawxExamResultsDAO.getAllApplicableSubjectDetailsForStudentPassfail(sapid);
		for (StudentSubjectConfigExamBean timeboundSubject : timeboundIdsForStudent) {
			MBAWXPassFailStatus passFailStatus = getMBAWXPassFailStatus(sapid, timeboundSubject.getId(), timeboundSubject.getPrgm_sem_subj_id(), timeboundSubject.getHasIA(),  timeboundSubject.getHasTEE());
	
			
				if (IA_MASTER_DISSERTATION_Q7_SUBJECT_ID == Integer.parseInt(timeboundSubject.getPrgm_sem_subj_id())) {
					MBAWXExamResultForSubject dissertationResult =	dissertationServiceQ7.getMastersDissertationResult( sapid,  timeboundSubject,  passFailStatus, timeboundSubject.getHasIA(),  timeboundSubject.getHasTEE());
					if(null!=dissertationResult) {
						passFailResults.add(dissertationResult);
					}
				}else if(IA_MASTER_DISSERTATION_Q8_SUBJECT_ID == Integer.parseInt(timeboundSubject.getPrgm_sem_subj_id())) {
					MBAWXExamResultForSubject dissertationResult =	dissertationServiceQ8.getMastersDissertationResult( sapid,  timeboundSubject,  passFailStatus, timeboundSubject.getHasIA(),  timeboundSubject.getHasTEE());
					if(null!=dissertationResult) {
						passFailResults.add(dissertationResult);
					}
				}
				else {
					if(null!=passFailStatus) {
						checkPassFailBean(passFailStatus);
						MBAWXExamResultForSubject subjectResult = getSubjectResult(sapid, timeboundSubject, passFailStatus);
						passFailResults.add(subjectResult);
					}
				}
			
		}

		return passFailResults;
	}
	
	private List<MBAWXExamResultForSubject> getMbaXPassFailStatusForStudent(String sapid) {

		List<MBAWXExamResultForSubject> passFailResults = new ArrayList<MBAWXExamResultForSubject>();
		List<StudentSubjectConfigExamBean> timeboundIdsForStudent  = new ArrayList<StudentSubjectConfigExamBean>();
		timeboundIdsForStudent = mbawxExamResultsDAO.getAllApplicableSubjectDetailsForStructureChangeStudent(sapid);
		if(timeboundIdsForStudent.size() == 0) {
			timeboundIdsForStudent = mbawxExamResultsDAO.getAllApplicableSubjectDetailsForStudentPassfail(sapid);
		}
		for (StudentSubjectConfigExamBean timeboundSubject : timeboundIdsForStudent) {
			MBAWXPassFailStatus passFailStatus = getMBAXPassFailStatus(sapid, timeboundSubject.getId(),
					timeboundSubject.getPrgm_sem_subj_id());
			if(passFailStatus != null) {
				checkPassFailBean(passFailStatus);
				MBAWXExamResultForSubject subjectResult = getMbaXSubjectResult(sapid, timeboundSubject, passFailStatus);
				passFailResults.add(subjectResult);
			}
		}

		return passFailResults;
	}

	private List<MBAWXExamResultForSubject> getMarksHistoryForStudent(String sapid) {

		List<MBAWXExamResultForSubject> marksHistory = new ArrayList<MBAWXExamResultForSubject>();
		
		// For each timebound id mapping for this student
		List<StudentSubjectConfigExamBean> timeboundIdsForStudent = mbawxExamResultsDAO.getAllTimeboundSubjectDetailsForStudent(sapid);
		
		for (StudentSubjectConfigExamBean studentSubjectConfig : timeboundIdsForStudent) {
			String timeboundId = studentSubjectConfig.getId();
			
			// Get all TEE marks entries. This will ideally return a maximum of 2 (1 tee, 1 resit).
			// Only results with isResultLive for the respective Schedules are returned.
			List<MBAWXPassFailStatus> allTeeMarksForSubject = mbawxExamResultsDAO.getAllTeeMarksForSubject(sapid, timeboundId);

			if (allTeeMarksForSubject.size() > 0) {
				// Get the subject result bean
				MBAWXExamResultForSubject subjectResult = createSubjectResultObjectFromTimeboundSubject(studentSubjectConfig);
				for (MBAWXPassFailStatus teeMarks : allTeeMarksForSubject) {
					// Set Status as Marks.
					checkPassFailBean(teeMarks);
					subjectResult.setTerm(teeMarks.getSem());
					// If the entry is for Resit(100 marks) exam, add it to the resitScore of the bean
					if(RESIT_EXAM_TEE_SCORE.equals(teeMarks.getMax_score())) {
						subjectResult.setResitScore(teeMarks.getTeeScore());
						subjectResult.setResitScoreMax(teeMarks.getMax_score());
					} else {
						subjectResult.setTeeScore(teeMarks.getTeeScore());
						subjectResult.setTeeScoreMax(teeMarks.getMax_score());
					}
					
					subjectResult.setExamDate(teeMarks.getExamStartTime());
				}

				// Get IA for this subject
				getIAScoreForSubjectResults(sapid, Long.parseLong(timeboundId), subjectResult);
				

				// Get Grace and Total if applicable.
				resultsProcessingHelper.setGraceAndTotal(subjectResult);
				
				// Check if student is pass in TEE and ReSit
				resultsProcessingHelper.setIsPass(subjectResult);
				
				subjectResult.setShowResults("Y");
				marksHistory.add(subjectResult);
			}
		}
		return marksHistory;
	}
	
	private MBAWXPassFailStatus getMBAWXPassFailStatus(String sapid, String timeboundId, String prgm_sem_subj_id, String hasIA, String hasTEE) {
		if("1883".equals(prgm_sem_subj_id) || "2351".equals(prgm_sem_subj_id) || "2482".equals(prgm_sem_subj_id)
				|| "2414".equals(prgm_sem_subj_id) || "2406".equals(prgm_sem_subj_id) || "1988".equals(prgm_sem_subj_id) 
				|| "2492".equals(prgm_sem_subj_id) || "2423".equals(prgm_sem_subj_id)
				) {
			boolean passFailStatusExists = mbawxExamResultsDAO.checkIfProjectPassFailResultsBySapidTimeboundId(sapid, timeboundId);
			
			if(!passFailStatusExists)
				passFailStatusExists = mbawxExamResultsDAO.checkIfComponectProjectPassFailResultsBySapidTimeboundId(sapid, timeboundId);
			
			MBAWXPassFailStatus passFailStatus = null;
			if(passFailStatusExists) {
				passFailStatus = mbawxExamResultsDAO.getProjectPassFailResultsBySapidTimeboundId(sapid, timeboundId);
				
				if(passFailStatus==null) {
					passFailStatus = mbawxExamResultsDAO.getComponentProjectPassFailResultsBySapidTimeboundId(sapid, timeboundId);
				}
			}
			checkPassFailBean(passFailStatus);
			return passFailStatus;
		}  else if("Y".equalsIgnoreCase(hasIA) &&  "N".equalsIgnoreCase(hasTEE) ) {
			boolean passFailStatusExists = mbawxExamResultsDAO.checkIfProjectPassFailResultsBySapidTimeboundId(sapid, timeboundId);
			MBAWXPassFailStatus passFailStatus = null;
			MBAWXPassFailStatus dissertation = null;
			
			//if loop iterating for Q7 or Q8 pssId
			if(IA_MASTER_DISSERTATION_Q7_SUBJECT_ID == (Integer.parseInt(prgm_sem_subj_id))) {
				
				dissertation = dissertationServiceQ7.mapPassFailBean(sapid,timeboundId);
				if(null!=dissertation && !StringUtils.isEmpty(dissertation.getIsResultLive()) 
						&& "Y".equalsIgnoreCase(dissertation.getIsResultLive())) {
					//it will set the data only if data found and result is live
					passFailStatus = dissertation;
				}
				
			}else if(IA_MASTER_DISSERTATION_Q8_SUBJECT_ID == Integer.parseInt(prgm_sem_subj_id)){	
				dissertation = dissertationServiceQ8.mapPassFailBean(sapid,timeboundId);	
				if(null!=dissertation && !StringUtils.isEmpty(dissertation.getIsResultLive()) 
						&& "Y".equalsIgnoreCase(dissertation.getIsResultLive())) {
					//it will set the data only if data found and result is live
					passFailStatus = dissertation;
				}
			}
			
			if(passFailStatusExists) {
				passFailStatus = mbawxExamResultsDAO.getIAOnlyComponentPassFailResult(sapid, timeboundId);
				if("1990".equals(passFailStatus.getPrgm_sem_subj_id()))
					passFailStatus.setMax_score(IA_MASTER_DESSERTATION_Q7_MAX_SCORE);
				else
					passFailStatus.setMax_score(IA_ONLY_MAX_SCORE);
			}
			checkPassFailBean(passFailStatus);
			return passFailStatus;
		}else {
			boolean passFailStatusExists = mbawxExamResultsDAO.checkIfPassFailResultsBySapidTimeboundId(sapid, timeboundId);
			MBAWXPassFailStatus passFailStatus = null;
			if(passFailStatusExists) {
				passFailStatus = mbawxExamResultsDAO.getPassFailResultsBySapidTimeboundId(sapid, timeboundId);
			}
			checkPassFailBean(passFailStatus);
			return passFailStatus;
		}
	}
	
	private MBAWXPassFailStatus getMBAXPassFailStatus(String sapid, String timeboundId, String pssId) {
		//added pssid check for Capstone Project
		if("1958".equals(pssId) ||  "1806".equals(pssId)) {
			boolean passFailStatusExists = mbawxExamResultsDAO.checkIfMbaXProjectPassFailResultsBySapidTimeboundId(sapid, timeboundId);
			MBAWXPassFailStatus passFailStatus = null;
			if(passFailStatusExists) {
				passFailStatus = mbawxExamResultsDAO.getMbaXProjectPassFailResultsBySapidTimeboundId(sapid, timeboundId);
			}
			checkPassFailBean(passFailStatus);
			return passFailStatus;
		}else if("1789".equals(pssId)) {
			boolean passFailStatusExists = mbawxExamResultsDAO.checkIfMbaXProjectPassFailResultsBySapidTimeboundId(sapid, timeboundId);
			MBAWXPassFailStatus passFailStatus = null;
			if(passFailStatusExists) {
				passFailStatus = mbawxExamResultsDAO.getBopProjectPassFailResult(sapid, timeboundId);
			}
			checkPassFailBean(passFailStatus);
			return passFailStatus;
		}  else {
			boolean passFailStatusExists = mbawxExamResultsDAO.checkIfMbaXPassFailResultsBySapidTimeboundId(sapid, timeboundId);
			MBAWXPassFailStatus passFailStatus = null;		
	
			if(passFailStatusExists) {
				passFailStatus = mbawxExamResultsDAO.getMbaXPassFailResultsBySapidTimeboundId(sapid, timeboundId);
			}
			checkPassFailBean(passFailStatus);
			return passFailStatus;
		}
	}
	
	private void checkPassFailBean(MBAWXPassFailStatus passFailStatus) {
		if(passFailStatus != null) {
			checkTEEMarksStatus(passFailStatus);
			setTotalScore(passFailStatus);
		}
	}
	
	private void checkTEEMarksStatus(MBAWXPassFailStatus passFailStatus) {
		String status = passFailStatus.getStatus();
		if(!StringUtils.isBlank(status) && !"Attempted".equalsIgnoreCase(status)) {
			passFailStatus.setTeeScore(passFailStatus.getStatus());
		}
	}

	private MBAWXExamResultForSubject getSubjectResult(String sapid, StudentSubjectConfigExamBean timeboundSubject, MBAWXPassFailStatus passFailStatus) {
		Long timeboundId = Long.parseLong(timeboundSubject.getId());
		MBAWXExamResultForSubject subjectResult = createSubjectResultObjectFromTimeboundSubject(timeboundSubject);
		subjectResult.setSapid(sapid);
		subjectResult.setShowResultsForIA("Y");
		subjectResult.setPrgm_sem_subj_id(timeboundSubject.getPrgm_sem_subj_id());
		
		
		if (passFailStatus != null && "Y".equals(passFailStatus.getIsResultLive())) {
			subjectResult.setExamDate(passFailStatus.getExamStartTime());
			subjectResult.setShowResults("Y");
			subjectResult.setTotal(passFailStatus.getTotal());
			subjectResult.setTotalMax(TOTAL_SCORE_MAX);
			subjectResult.setGraceMarks(passFailStatus.getGraceMarks());
			subjectResult.setIsPass(passFailStatus.getIsPass());
			
			if(!StringUtils.isEmpty(passFailStatus.getSem())) {
				subjectResult.setTerm(passFailStatus.getSem());
			}
			
			if(IA_ONLY_MAX_SCORE.equals(passFailStatus.getMax_score()) && "Y".equals(timeboundSubject.getHasIA()) && "N".equals(timeboundSubject.getHasTEE()) ) {
				
				if ( passFailStatus.getIaScore() == null ) {
					subjectResult.setIaScore("0");
				}else {
					subjectResult.setIaScore(passFailStatus.getIaScore());
				}
				subjectResult.setIaScoreMax(IA_ONLY_MAX_SCORE);
				
			} else if(RESIT_EXAM_TEE_SCORE.equals(passFailStatus.getMax_score())) {
				
				// For 100 marks tee exam (reSit) we need to calculate IA Scores.
				getIAScoreForSubjectResults(sapid, timeboundId, subjectResult);
				
				// resit score for reSit
				subjectResult.setResitScore(passFailStatus.getTeeScore());
				subjectResult.setResitScoreMax(passFailStatus.getMax_score());

				// Get the 30 Mark TEE exam score from marks history.
				setTeeScore(subjectResult);

			} else if(PROJECT_EXAM_TEE_SCORE.equals(passFailStatus.getMax_score())) {
				
				// For 30 marks tee exam (normal) we dont need ia Scores. all scores are returned from the DB
				subjectResult.setTeeScore(passFailStatus.getTeeScore());
				subjectResult.setTeeScoreMax(passFailStatus.getMax_score());
				if ( passFailStatus.getIaScore() == null ) {
					subjectResult.setIaScore("0");
				}else {
					subjectResult.setIaScore(passFailStatus.getIaScore());
				}
				subjectResult.setIaScoreMax(PROJECT_IA_MAX_SCORE);
				
			} else if(PROJECT_EXAM_TEE_SCORE_OCT.equals(passFailStatus.getMax_score())) {
				
				// For 30 marks tee exam (normal) we dont need ia Scores. all scores are returned from the DB
				subjectResult.setTeeScore(passFailStatus.getTeeScore());
				subjectResult.setTeeScoreMax(passFailStatus.getMax_score());
				if ( passFailStatus.getIaScore() == null ) {
					subjectResult.setIaScore("0");
				}else {
					subjectResult.setIaScore(passFailStatus.getIaScore());
				}
				subjectResult.setIaScoreMax(PROJECT_IA_MAX_SCORE_OCT);
				
			} else if(MSC_TEE_SCORE.equals(passFailStatus.getMax_score())) {
				// For 40 marks tee exam (normal) we dont need ia Scores. all scores are returned from the DB
				subjectResult.setTeeScore(passFailStatus.getTeeScore());
				subjectResult.setTeeScoreMax(passFailStatus.getMax_score());
				if ( passFailStatus.getIaScore() == null ) {
					subjectResult.setIaScore("0");
				}else {
					subjectResult.setIaScore(passFailStatus.getIaScore());
				}
				subjectResult.setIaScoreMax(MSC_IA_MAX_SCORE);
			} else if(PDDM_TEE_MAX_SCORE.equals(passFailStatus.getMax_score())) {
				subjectResult.setTeeScore(passFailStatus.getTeeScore());
				subjectResult.setTeeScoreMax(passFailStatus.getMax_score());
				if ( passFailStatus.getIaScore() == null ) {
					subjectResult.setIaScore("0");
				}else {
					subjectResult.setIaScore(passFailStatus.getIaScore());
				}
				subjectResult.setIaScoreMax("30");
			} else if("50".equals(passFailStatus.getMax_score())) {
				
				subjectResult.setTeeScore(passFailStatus.getTeeScore());
				subjectResult.setTeeScoreMax(passFailStatus.getMax_score());
				if ( passFailStatus.getIaScore() == null ) {
					subjectResult.setIaScore("0");
				}else {
					subjectResult.setIaScore(passFailStatus.getIaScore());
				}
				subjectResult.setIaScoreMax("50");
				
			} else if((passFailStatus.getSimulation_max_score()!=null && passFailStatus.getCompXM_max_score()!=null) 
					&& CAPSTONE_PROJECT_MAX_SCORE.equals(String.valueOf((passFailStatus.getSimulation_max_score() + passFailStatus.getCompXM_max_score())))) {
				// For 30 marks tee exam (normal) we dont need ia Scores. all scores are returned from the DB
				subjectResult.setTeeScore(passFailStatus.getTeeScore());
				subjectResult.setTeeScoreMax(CAPSTONE_PROJECT_MAX_SCORE);
				subjectResult.setSimulation_score(passFailStatus.getSimulation_score());
				subjectResult.setCompXM_score(passFailStatus.getCompXM_score());
				subjectResult.setSimulation_max_score(passFailStatus.getSimulation_max_score());
				subjectResult.setCompXM_max_score(passFailStatus.getCompXM_max_score());
				subjectResult.setIaScore(passFailStatus.getIaScore());
				subjectResult.setIaScoreMax(CAPSTONE_PROJECT_IA_MAX_SCORE);
				
			} else {
				// For 30 marks tee exam (normal) we dont need ia Scores. all scores are returned from the DB
				subjectResult.setTeeScore(passFailStatus.getTeeScore());
				subjectResult.setTeeScoreMax(passFailStatus.getMax_score());
				if ( passFailStatus.getIaScore() == null ) {
					subjectResult.setIaScore("0");
				}else {
					subjectResult.setIaScore(passFailStatus.getIaScore());
				}
				subjectResult.setIaScoreMax(IA_MAX_SCORE);
			}
			
			resultsProcessingHelper.setIsPassForResitAndTee(subjectResult);
		} else {
			subjectResult.setShowResults("N");
			
			// get and add IA scores for thisZ
			getIAScoreForSubjectResults(sapid, timeboundId, subjectResult);
		}
		
		return subjectResult;
	}

	private void setTeeScore(MBAWXExamResultForSubject subjectResult) {

		// Get the 30 marks TEE exam score
		MBAWXPassFailStatus teeMarks = mbawxExamResultsDAO.getTEERecordForStudent(subjectResult.getSapid(), subjectResult.getTimeboundId());
		checkTEEMarksStatus(teeMarks);

		// Set the score
		subjectResult.setTeeScore(teeMarks.getTeeScore());		
		subjectResult.setTeeScoreMax(teeMarks.getMax_score());
	}
	
	private void setTeeScoreForMbaX(MBAWXExamResultForSubject subjectResult) {

		// Get the 40 marks TEE exam score
		MBAWXPassFailStatus teeMarks = mbawxExamResultsDAO.getTEERecordForMbaXStudent(subjectResult.getSapid(), subjectResult.getTimeboundId());
		checkTEEMarksStatus(teeMarks);

		// Set the score			
		subjectResult.setTeeScore(teeMarks.getTeeScore());		
		subjectResult.setTeeScoreMax(teeMarks.getMax_score());
	}

	
	private MBAWXExamResultForSubject createSubjectResultObjectFromTimeboundSubject(StudentSubjectConfigExamBean timeboundSubject) {
		MBAWXExamResultForSubject subjectResult = new MBAWXExamResultForSubject();
		subjectResult.setTimeboundId(timeboundSubject.getId());
		subjectResult.setAcadsMonth(timeboundSubject.getAcadMonth());
		subjectResult.setAcadsYear(timeboundSubject.getAcadYear());
		subjectResult.setExamMonth(timeboundSubject.getExamMonth());
		subjectResult.setExamYear(timeboundSubject.getExamYear());
		subjectResult.setSubject(timeboundSubject.getSubject());
		subjectResult.setTerm(timeboundSubject.getSem());
		subjectResult.setStartDate(timeboundSubject.getStartDate());
		return subjectResult;
	}
	
	private void setTotalScore(MBAWXPassFailStatus passFailStatus) {
		int teeScore = parseIfNumericScore(passFailStatus.getTeeScore());
		int iaScore = parseIfNumericScore(passFailStatus.getIaScore());
		int total = teeScore + iaScore;
		passFailStatus.setTotal(Integer.toString(total));
	}
	
	private int parseIfNumericScore(String score) {
		if (!StringUtils.isBlank(score) && StringUtils.isNumeric(score)) {
			return Integer.parseInt(score);
		}
		return 0;
	}

	private void getIAScoreForSubjectResults(String sapid, Long timeboundId, MBAWXExamResultForSubject subjectResult) {

		List<StudentsTestDetailsExamBean> attemptedTestsBySapidNSubject = resultsProcessingHelper.getIAForSubjectWithBestOf7Marked(timeboundId, sapid);
		
		if (attemptedTestsBySapidNSubject.size() > 0) {

			int score = 0;
			double scoreInDecimal = 0.0;
			int maxScore = 0;
			int numberOfSelectedAttempts = 0;
			for (StudentsTestDetailsExamBean b : attemptedTestsBySapidNSubject) {
				// Calculate total score and add subject to selected subjects array
				if(b.isScoreSelectedForBestOf7()) {
					scoreInDecimal = scoreInDecimal + b.getScore();
					maxScore = maxScore + b.getMaxScore();
					numberOfSelectedAttempts++;
				}
			}
			score = roundDoubleToInt(scoreInDecimal);
			
			subjectResult.setIaScore(Integer.toString(score));
			subjectResult.setIaScoreMax(Integer.toString(maxScore));
			subjectResult.setShowResultsForIA("Y");
		} else {
			// dont show results if no ia is present for this subject
			subjectResult.setShowResultsForIA("N");
		}
	}
	
	private void getIAScoreForSubjectResultsMBAX(String sapid, Long timeboundId, MBAWXExamResultForSubject subjectResult, int bestOfSubjectCount) {

		EmbaPassFailBean studentFinalMarks = new EmbaPassFailBean();		
		int project=0;
		double projectInDecimal=0.0;
		ArrayList<StudentsTestDetailsExamBean> iaMarks = upgradResultProcessingDao.getMBAXIAScoresForStudentSubject(sapid,
				timeboundId.toString(),"Assignment");  
		ArrayList<StudentsTestDetailsExamBean> projectScore = upgradResultProcessingDao.getMBAXIAScoresForStudentSubject(sapid,
				timeboundId.toString(),"Project");  
		
		if(projectScore.size()>0) {
			projectInDecimal=projectScore.get(0).getScore();
		}
		project = roundDoubleToInt(projectInDecimal);
		studentFinalMarks.setProject(project);
		boolean calculateIA =true;
		
		if(iaMarks.size() > 0) { 
			for(StudentsTestDetailsExamBean test : iaMarks) {
				if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
					studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
					studentFinalMarks.setStatus("NA");
					calculateIA=false;
				}
			}
		}
		
		if(calculateIA) {
			if(iaMarks.size() <= bestOfSubjectCount && iaMarks.size() > 0) { //for score list having less than 5 entires add all scores to get IA
				int scoreFor3OrBelowTests=0;
				double scoreFor3OrBelowTestsInDecimal=0.0;
				for(StudentsTestDetailsExamBean test : iaMarks) {
					if(test.getShowResult().equalsIgnoreCase("N") || test.getShowResultsToStudents().equalsIgnoreCase("N") ) {
						//studentFinalMarks.setFailReason("ShowResult/ShowResultsToStudents flag not live");
						//unsuccessfulPassFail.add(studentFinalMarks);
						//studentFinalMarks.setStatus("NA");
						break;
					}else {
						scoreFor3OrBelowTestsInDecimal = scoreFor3OrBelowTestsInDecimal + test.getScore();
					}
				}
				scoreFor3OrBelowTests = roundDoubleToInt(scoreFor3OrBelowTestsInDecimal);
				int finalIAScore= scoreFor3OrBelowTests+project;
				studentFinalMarks.setIaScore(""+finalIAScore);
				//logger.info("IA Score = "+finalIAScore);
			}else if(iaMarks.size() > bestOfSubjectCount) {
				String iaScore = calculateBestOfScoresMBAX(iaMarks,bestOfSubjectCount,project); // //for score list having more than 3 entires  consider best 3  for IA
				studentFinalMarks.setIaScore(iaScore);
				//logger.info("Best Of 3 IA Score = "+iaScore);
			}else if(iaMarks.size() == 0) { // if no ia is given then consider only project score.
				int finalIAScore= project;
				studentFinalMarks.setIaScore(""+finalIAScore);
			}
		}

		subjectResult.setIaScore(studentFinalMarks.getIaScore());
		subjectResult.setIaScoreMax(Integer.toString(bestOfSubjectCount * 10));			
		subjectResult.setShowResultsForIA("Y");

	}

	private String calculateBestOfScoresMBAX(ArrayList<StudentsTestDetailsExamBean> iaMarks, int limit, int project) {
		String iaScore ="0";
		List<StudentsTestDetailsExamBean>  descScoreSortedList = new LinkedList<>();
		descScoreSortedList.addAll(iaMarks);
		Comparator<StudentsTestDetailsExamBean> compareByScore = new Comparator<StudentsTestDetailsExamBean>() {
			@Override
			public int compare(StudentsTestDetailsExamBean o1, StudentsTestDetailsExamBean o2) {
				return o1.getScoreInInteger().compareTo(o2.getScoreInInteger());
			}
		};
		Collections.sort(descScoreSortedList, compareByScore.reversed());
		if(descScoreSortedList.size() > limit) {
			List<StudentsTestDetailsExamBean> bestAttempts = descScoreSortedList.subList(0, limit);
			int score = 0;
			double scoreInDecimal = 0.0;
			for(StudentsTestDetailsExamBean b : bestAttempts) {
				scoreInDecimal = scoreInDecimal + b.getScore();
			}
			score = roundDoubleToInt(scoreInDecimal);
			iaScore=String.valueOf(score+project); // added project score to IA
		}
		return iaScore;
	}

	private void getMbaXIAScoreForSubjectResults(String sapid, Long timeboundId, MBAWXExamResultForSubject subjectResult) {

		List<StudentsTestDetailsExamBean> attemptedTestsBySapidNSubject = resultsProcessingHelper.getMbaXIAForSubjectWithBestOf7Marked(timeboundId, sapid);
		
		if (attemptedTestsBySapidNSubject.size() > 0) {

			int score = 0;
			double scoreInDecimal = 0.0;
			int numberOfSelectedAttempts = 0;
			for (StudentsTestDetailsExamBean b : attemptedTestsBySapidNSubject) {
				// Calculate total score and add subject to selected subjects array
				if(b.isScoreSelectedForBestOf7()) {
					scoreInDecimal = scoreInDecimal + b.getScore();
					numberOfSelectedAttempts++;
				}
			}
			score = roundDoubleToInt(scoreInDecimal);
			subjectResult.setIaScore(Integer.toString(score));
			subjectResult.setIaScoreMax(Integer.toString(numberOfSelectedAttempts * 10));
			subjectResult.setShowResultsForIA("Y");
		} else {
			// dont show results if no ia is present for this subject
			subjectResult.setShowResultsForIA("N");
		}
	}
	
	private List<MBAWXExamResultForSubject> getTEEMarksHistoryForStudent(String sapid) {

		List<MBAWXExamResultForSubject> marksHistory = new ArrayList<MBAWXExamResultForSubject>();
		List<MBAWXPassFailStatus> allTeeMarks = mbawxExamResultsDAO.getAllTeeMarksForStudent(sapid);
		
		for (MBAWXPassFailStatus teeMarks : allTeeMarks) {

			checkPassFailBean(teeMarks);
			StudentSubjectConfigExamBean timeboundSubject = mbawxExamResultsDAO.getTimeboundSubjectDetailsForTimeboundId(teeMarks.getTimeboundId());
			timeboundSubject.setSem(teeMarks.getSem());
			MBAWXExamResultForSubject subjectResult = createSubjectResultObjectFromTimeboundSubject(timeboundSubject);
			

			subjectResult.setExamDate(teeMarks.getExamStartTime());
			subjectResult.setTeeScore(teeMarks.getTeeScore());
			subjectResult.setTeeScoreMax(teeMarks.getMax_score());
			
			
			int teeScore = parseIfNumericScore(subjectResult.getTeeScore());
			
			if(RESIT_EXAM_TEE_SCORE.equals(subjectResult.getTeeScoreMax())) {
				subjectResult.setTeeIsPass(teeScore >= RESIT_PASS_SCORE ? "Y" : "N");
			} else {
				subjectResult.setTeeIsPass(teeScore >= TEE_PASS_SCORE ? "Y" : "N");
			}
			marksHistory.add(subjectResult);
		}
		
		return marksHistory;
	}
	
	private List<MBAWXExamResultForSubject> getTEEMarksHistoryForMbaXStudent(String sapid) {

		List<MBAWXExamResultForSubject> marksHistory = new ArrayList<MBAWXExamResultForSubject>();
		List<MBAWXPassFailStatus> allTeeMarks = mbawxExamResultsDAO.getAllTeeMarksForMbaXStudent(sapid);		
		
		for (MBAWXPassFailStatus teeMarks : allTeeMarks) {

			checkPassFailBean(teeMarks);
			StudentSubjectConfigExamBean timeboundSubject = mbawxExamResultsDAO.getTimeboundSubjectDetailsForTimeboundId(teeMarks.getTimeboundId());
			
			MBAWXExamResultForSubject subjectResult = createSubjectResultObjectFromTimeboundSubject(timeboundSubject);
			

			subjectResult.setExamDate(teeMarks.getExamStartTime());
			subjectResult.setTeeScore(teeMarks.getTeeScore());
			subjectResult.setTeeScoreMax(teeMarks.getMax_score());
			
			
			int teeScore = parseIfNumericScore(subjectResult.getTeeScore());
			
			if(RESIT_EXAM_TEE_SCORE.equals(subjectResult.getTeeScoreMax())) {
				subjectResult.setTeeIsPass(teeScore >= RESIT_PASS_SCORE ? "Y" : "N");
			} else {
				subjectResult.setTeeIsPass(teeScore >= TEE_PASS_SCORE ? "Y" : "N");
			}
			marksHistory.add(subjectResult);
		}
		
		return marksHistory;
	}
	
	private MBAWXExamResultForSubject getMbaXSubjectResult(String sapid, StudentSubjectConfigExamBean timeboundSubject, MBAWXPassFailStatus passFailStatus) {
		Long timeboundId = Long.parseLong(timeboundSubject.getId());
		MBAWXExamResultForSubject subjectResult = createSubjectResultObjectFromTimeboundSubject(timeboundSubject);
		subjectResult.setSapid(sapid);
		subjectResult.setShowResultsForIA("Y");

		int numberOfIAApplicableForBestSelection = 4;
		String yearMonth = timeboundSubject.getExamMonth() + timeboundSubject.getExamYear();
		if("Dec2019".equals(yearMonth) || "Mar2020".equals(yearMonth)) {
			numberOfIAApplicableForBestSelection = 6;
		}


		List<String> basicsOfPythonReExamSchedules = Arrays.asList("2466003", "2904767", "4983152");
		
		boolean isBasicsOfPythonSubject = false;
		if("1789".equals(timeboundSubject.getPrgm_sem_subj_id())) {
			isBasicsOfPythonSubject = true;
		}

		
		if (passFailStatus != null && "Y".equals(passFailStatus.getIsResultLive())) {
			subjectResult.setExamDate(passFailStatus.getExamStartTime());
			subjectResult.setShowResults("Y");
			subjectResult.setTotal(passFailStatus.getTotal());
			
			if(isBasicsOfPythonSubject) {
				numberOfIAApplicableForBestSelection = 5;
				if(!"100".equals(passFailStatus.getMax_score())) {
					subjectResult.setTotalMax("50");
				}
			} else {
				subjectResult.setTotalMax(TOTAL_SCORE_MAX);
			}
			subjectResult.setGraceMarks(passFailStatus.getGraceMarks());
			subjectResult.setIsPass(passFailStatus.getIsPass());
			
			if(RESIT_EXAM_TEE_SCORE.equals(passFailStatus.getMax_score())) {
				// For 100 marks tee exam (reSit) we need to calculate IA Scores.
				getIAScoreForSubjectResultsMBAX(sapid, timeboundId, subjectResult, numberOfIAApplicableForBestSelection);
				
				// resit score for reSit
				subjectResult.setResitScore(passFailStatus.getTeeScore());
				subjectResult.setResitScoreMax(passFailStatus.getMax_score());

				// Get the 40 Mark TEE exam score from marks history.

				setTeeScoreForMbaX(subjectResult);								
			} else if(MBAX_PROJECT_EXAM_TEE_SCORE.equals(passFailStatus.getMax_score())) {
						
				// For 30 marks tee exam (normal) we dont need ia Scores. all scores are returned from the DB
				subjectResult.setTeeScore(passFailStatus.getTeeScore());
				subjectResult.setTeeScoreMax(passFailStatus.getMax_score());
				if ( passFailStatus.getIaScore() == null ) {
					subjectResult.setIaScore("0");
				}else {
					subjectResult.setIaScore(passFailStatus.getIaScore());
				}
				subjectResult.setIaScoreMax(MBAX_PROJECT_IA_MAX_SCORE);
				
					
			} else if("60".equals(passFailStatus.getMax_score()) && "1806".equals(timeboundSubject.getPrgm_sem_subj_id())) {
				
				// update the weightage for Capstone Subject max marks 40 in Mini Project from Dec21 acad cycle
				subjectResult.setTeeScore(passFailStatus.getTeeScore());
				subjectResult.setTeeScoreMax(passFailStatus.getMax_score());
				if ( passFailStatus.getIaScore() == null ) {
					subjectResult.setIaScore("0");
				}else {
					subjectResult.setIaScore(passFailStatus.getIaScore());
				}
				subjectResult.setIaScoreMax("40");
				
					
			} else {
				// For 30 marks tee exam (normal) we dont need ia Scores. all scores are returned from the DB
				subjectResult.setTeeScore(passFailStatus.getTeeScore());
				subjectResult.setTeeScoreMax(passFailStatus.getMax_score());
				if ( passFailStatus.getIaScore() == null ) {
					subjectResult.setIaScore("0");
				}else {
					subjectResult.setIaScore(passFailStatus.getIaScore());
				}
				subjectResult.setIaScoreMax(Integer.toString(numberOfIAApplicableForBestSelection * 10));
			}			
			resultsProcessingHelper.setIsPassForResitAndTee(subjectResult);

			boolean isBasicsOfPythonReExamSchedule = basicsOfPythonReExamSchedules.contains(passFailStatus.getSchedule_id());
			
			boolean isBasicsOfPythonReExam = isBasicsOfPythonSubject && isBasicsOfPythonReExamSchedule; 
			if("50".equals(passFailStatus.getMax_score()) && isBasicsOfPythonReExam) {
				// This whole block is particularly for MBAX Basics Of Python(sem 1 project). 
				// TODO: Remove the hardcode, add a check in schedules table for resit exam(?)
				getIAScoreForSubjectResultsMBAX(sapid, timeboundId, subjectResult, numberOfIAApplicableForBestSelection);
				
				// resit score for reSit
				subjectResult.setResitScore(passFailStatus.getTeeScore());
				subjectResult.setResitScoreMax(passFailStatus.getMax_score());
				subjectResult.setResitIsPass(passFailStatus.getIsPass());

				setTeeScoreForMbaX(subjectResult);
			}
		} else {
			subjectResult.setShowResults("N");
			// get and add IA scores for thisZ
			getIAScoreForSubjectResultsMBAX(sapid, timeboundId, subjectResult, numberOfIAApplicableForBestSelection);
			//added pssid check for Capstone Project
			// added timeboundid check for Capstone Subject 40% Mini Project from Dec21 acad cycle
			if("1958".equals(timeboundSubject.getPrgm_sem_subj_id()) ||  ("1806".equals(timeboundSubject.getPrgm_sem_subj_id()) && timeboundId < 746 ) ) {
				subjectResult.setIaScoreMax(MBAX_PROJECT_IA_MAX_SCORE);
			}else if("1806".equals(timeboundSubject.getPrgm_sem_subj_id()) && timeboundId >= 746 )  {
				subjectResult.setIaScoreMax("40");
			}
		}
		
		return subjectResult;
	}
	
	public HashMap<String, ProgramExamBean> getAllProgramMap(){
		if(this.programDetailsMap == null || this.programDetailsMap.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			this.programDetailsMap = dao.getProgramMap();
		}
		return programDetailsMap;
	}
	
	private int roundDoubleToInt(double doubleValue) {
		return (int) Math.round(doubleValue);
	}

}