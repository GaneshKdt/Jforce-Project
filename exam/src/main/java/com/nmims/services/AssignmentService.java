package com.nmims.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.google.common.base.Throwables;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.google.common.collect.Lists;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentLiveSetting;
import com.nmims.beans.ExamAssignmentResponseBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ResultDomain;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.CopyCaseHelper;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.helpers.MailSender;

@Service("asgService")
public class AssignmentService {
	@Autowired
	StudentMarksDAO sMarksDao;

	@Autowired
	ApplicationContext act;

	@Autowired
	ExamBookingDAO eDao;
	
	@Autowired
	StudentService studentService;
	
	@Autowired
	@Qualifier("asignmentsDAO")
	AssignmentsDAO asgdao;
	
	@Autowired
	AmazonS3Helper amazonS3Helper;
	
	@Autowired
	FileUploadHelper fileUploadHelper;
	
	@Autowired
	CopyCaseHelper  qpHelper;

	@Value("${ASSIGNMENT_SUBMISSIONS_ATTEMPTS}")
	private String ASSIGNMENT_SUBMISSIONS_ATTEMPTS;
	
	@Value("${ASSIGNMENT_FILES_PATH}")
	private String ASSIGNMENT_FILES_PATH;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	
	private ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = null;
	private ArrayList<String> subjectList = null;
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList;
	
	@Value("#{'${REMARK_GRADES_PROGRAMS}'.split(',')}")
	private ArrayList<String> REMARK_GRADES_PROGRAMS;
	
	private final long MAX_FILE_SIZE = 5242880;
	private static final Long MAX_FILE_SIZE_LIMIT = 6291456L;				//6 MB in bytes (6 * 1024 * 1024)
	@Value("${SUBMITTED_ASSIGNMENT_FILES_PATH}")
	private String SUBMITTED_ASSIGNMENT_FILES_PATH;

	private static final Logger logger = LoggerFactory.getLogger(AssignmentService.class);
	private static final Logger passFailLogger = LoggerFactory.getLogger("pg-passfail-process");
	private static final Logger assg_logger = LoggerFactory.getLogger("assignmentSubmission");
	private static final Logger assg_logger_mobile = LoggerFactory.getLogger("assignmentSubmissionMobile");

	
	final String[] electiveMasterkeys = {"128"};
	
	final String[] electiveSem = {"5","6"};
	
	public ExamAssignmentResponseBean getAssignments(String userId) {

		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();// response bean

		StudentMarksDAO sMarksDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
		StudentExamBean student = eDao.getSingleStudentsData(userId);
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		PassFailDAO pdao = (PassFailDAO) act.getBean("passFailDAO");
		
		//set liveAssignment Flag in BaseDAO
				int hasAssignment = dao.checkHasAssignment(student.getConsumerProgramStructureId());
				if(hasAssignment > 0) {
					AssignmentLiveSetting assignmentLiveSettingRegular = dao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(), "Regular");
					if(assignmentLiveSettingRegular != null) {
						dao.setLiveAssignmentYear(assignmentLiveSettingRegular.getAcadsYear());
						dao.setLiveAssignmentMonth(assignmentLiveSettingRegular.getAcadsMonth());
					}
					AssignmentLiveSetting assignmentLiveSettingResit = dao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(), "Resit");
					if(assignmentLiveSettingResit != null) {
						dao.setLiveResitAssignmentYear(assignmentLiveSettingResit.getAcadsYear());
						dao.setLiveResitAssignmentMonth(assignmentLiveSettingResit.getAcadsMonth());
					}		
				}
		
		
		ArrayList<String> currentSemSubjects = new ArrayList<>();
		ArrayList<String> failSubjects = new ArrayList<>();
		ArrayList<String> applicableSubjects = new ArrayList<>();
		ArrayList<String> ANSSubjects = new ArrayList<>();

		List<AssignmentFileBean> failSubjectsAssignmentFilesList = new ArrayList<>();
		List<AssignmentFileBean> currentSemAssignmentFilesList = new ArrayList<>();

		HashMap<String, String> subjectSemMap = new HashMap<>();
		int currentSemSubmissionCount = 0;
		int failSubjectSubmissionCount = 0;
		String currentSem = null;

		StudentExamBean studentRegistrationData = dao.getStudentRegistrationData(userId);

		// tab~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);

		ArrayList<String> getSubjects = getSubjectList();
		boolean isOnline = isOnline(student);

		if (student == null) {
			response.setError("Student Enrollment not found. Please contact Administrator.");
			// return new
			// ResponseEntity<>(response,headers,HttpStatus.UNPROCESSABLE_ENTITY);
		} else {
			studentService.mgetWaivedOffSubjects(student);
		}

		if (studentRegistrationData != null) {
			// Take program from Registration data and not Student data.
			// "+studentRegistrationData.getSem());
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			currentSem = studentRegistrationData.getSem();
			currentSemSubjects = getSubjectsForStudent(student, subjectSemMap);
		}
		
		//added waivedIn subject mapping
		if(currentSemSubjects != null) {
			ArrayList<String> waivedInSubjects = studentService.mgetWaivedInSubjects(student);
			for(String subject : waivedInSubjects) {
				if(!currentSemSubjects.contains(subject)) {
					currentSemSubjects.add(subject);
				}
			}
		}else {
			currentSemSubjects = studentService.mgetWaivedInSubjects(student);
		}
		subjectSemMap.putAll(student.getWaivedInSubjectSemMapping());

		try {
			ArrayList<String> passSubjectsList = getPassSubjects(student, pdao);
			if (!passSubjectsList.isEmpty() && passSubjectsList != null) {
				for (String subject : passSubjectsList) {
					if (currentSemSubjects.contains(subject)) {
						currentSemSubjects.remove(subject);
					}
				}
			}
		} catch (Exception e) {

			
			// response.setError("unable to get pass subjects");
		}

		failSubjects = new ArrayList<>();
		// if((currentSem != null && (!"1".equals(currentSem))) ||
		// studentRegistrationData == null){
		// If current semester is 1, then there cannot be any failed subjects

		try {
			ArrayList<AssignmentFileBean> failSubjectsBeans = getFailSubjects(student);
			if (failSubjectsBeans != null && failSubjectsBeans.size() > 0) {

				for (AssignmentFileBean bean : failSubjectsBeans) {
					String subject = bean.getSubject();
					String sem = bean.getSem();
					failSubjects.add(bean.getSubject());
					subjectSemMap.put(subject, sem);

					if ("ANS".equalsIgnoreCase(bean.getAssignmentscore())) {
						ANSSubjects.add(subject);
					}
				}
			}
		} catch (Exception e) {
			
		}

		try {
			ArrayList<AssignmentFileBean> failANSSubjectsBeans = getANSNotProcessed(student);
			if (failANSSubjectsBeans != null && failANSSubjectsBeans.size() > 0) {

				for (int i = 0; i < failANSSubjectsBeans.size(); i++) {
					String subject = failANSSubjectsBeans.get(i).getSubject();
					String sem = failANSSubjectsBeans.get(i).getSem();
					failSubjects.add(failANSSubjectsBeans.get(i).getSubject());
					subjectSemMap.put(subject, sem);

					ANSSubjects.add(subject);
				}
			}
		} catch (Exception e) {
			
		}
		ArrayList<AssignmentFileBean> currentSemResultAwaitedSubjectsList = new ArrayList<AssignmentFileBean>();

		// Check if result is live for last submission cycle
		boolean isResultLiveForLastSubmissionCycle = sMarksDao.isResultLiveForLastAssignmentSubmissionCycle();
		ArrayList<String> subjectsNotAllowedToSubmit = new ArrayList<String>();
		if (!isResultLiveForLastSubmissionCycle) {
			/*
			 * Commented so that current sem subjects should not show in ANS/Results
			 * Awaited/Failed Subjects Table currentSemResultAwaitedSubjectsList =
			 * dao.getResultAwaitedAssignmentSubmittedSubjectsList(student.getSapid()); for
			 * (AssignmentFileBean assignmentFileBean :
			 * currentSemResultAwaitedSubjectsList){ String subject =
			 * assignmentFileBean.getSubject();
			 * if(!failSubjects.contains(subject)){ failSubjects.add(subject); } }
			 */
			ArrayList<String> subjectsSubmittedInLastCycle = dao.getFailedSubjectsSubmittedInLastCycle(userId,
					failSubjects);
			ArrayList<String> subjectsExamBookedInLastCycle = dao.getFailedSubjectsExamBookedInLastCycle(userId,
					failSubjects);

			for (String subject : subjectsSubmittedInLastCycle) {
				ANSSubjects.remove(subject);
			}
			if (subjectsSubmittedInLastCycle.size() > 0 || subjectsExamBookedInLastCycle.size() > 0) {
				// There are failed subjects submitted in last submission cycle

				// If result is not live then subjects submitted in last cycle cannot be
				// submitted till results are live
				subjectsNotAllowedToSubmit.addAll(subjectsSubmittedInLastCycle);
				subjectsNotAllowedToSubmit.addAll(subjectsExamBookedInLastCycle);
			}


		}

		for (String failedSubject : failSubjects) {
			// For ANS cases, where result is not declared, failed subject will also be
			// present in Current sem subject.
			// Give preference to it as Failed, so that assignment can be submitted and
			// remove from Current list
			if (currentSemSubjects.contains(failedSubject)) {
				currentSemSubjects.remove(failedSubject);
			}
		}

		currentSemSubjects.remove("Project");
		currentSemSubjects.remove("Module 4 - Project");
		failSubjects.remove("Project");
		failSubjects.remove("Module 4 - Project");
		applicableSubjects.addAll(currentSemSubjects);
		applicableSubjects.addAll(failSubjects);
		applicableSubjects.remove("Project");
		applicableSubjects.remove("Module 4 - Project");
		response.setApplicableSubjects(applicableSubjects);
		// request.getSession().setAttribute("subjectsForStudent", applicableSubjects);
		List<AssignmentFileBean> allAssignmentFilesList = new ArrayList<>();
		/*
		 * Commented by Steffi to allow offline students to submit assignments in
		 * APR/SEP if(!isOnline){ allAssignmentFilesList =
		 * dao.getAssignmentsForSubjects(applicableSubjects, student); }else{
		 */
		List<AssignmentFileBean> currentSemFiles = null;
		List<AssignmentFileBean> failSubjectFiles = null;
		if (currentSemSubjects != null && currentSemSubjects.size() > 0) {
			currentSemFiles = dao.getAssignmentsForSubjects(currentSemSubjects, student);
		}
		if (failSubjects != null && failSubjects.size() > 0) {
			failSubjectFiles = dao.getResitAssignmentsForSubjects(failSubjects, student);
		}

		if (currentSemFiles != null) {
			allAssignmentFilesList.addAll(currentSemFiles);
		}

		if (failSubjectFiles != null) {

			allAssignmentFilesList.addAll(failSubjectFiles);
		}
		if (allAssignmentFilesList != null) {

			HashMap<String, AssignmentFileBean> subjectSubmissionMap = new HashMap<>();
			/*
			 * Commented by Steffi to allow offline students to submit assignments in
			 * APR/SEP if(!isOnline){ subjectSubmissionMap =
			 * dao.getSubmissionStatus(applicableSubjects, sapId);//Assignments from Jun,
			 * Dec cycle }else{
			 */
			// For online, resit i.e. fail subjects paper change after resit date is
			// over.//Applicable cycle is all 4 i.e. Apr, Jun, Sep, Dec
			HashMap<String, AssignmentFileBean> currentSemSubjectSubmissionMap = dao
					.getSubmissionStatus(currentSemSubjects, userId);
			HashMap<String, AssignmentFileBean> failSubjectSubmissionMap = dao.getResitSubmissionStatus(failSubjects,
					student);

			if (currentSemSubjectSubmissionMap != null) {
				subjectSubmissionMap.putAll(currentSemSubjectSubmissionMap);
			}

			if (failSubjectSubmissionMap != null) {
				subjectSubmissionMap.putAll(failSubjectSubmissionMap);
			}
			// }
			for (AssignmentFileBean assignment : allAssignmentFilesList) {
				String subject = assignment.getSubject();
				String status = "Not Submitted";
				String attempts = "0";
				String lastModifiedDate = "";

				String pastCycleAssignmentDetails = "";

				AssignmentFileBean studentSubmissionStatus = subjectSubmissionMap.get(subject);
				if (studentSubmissionStatus != null) {
					status = studentSubmissionStatus.getStatus();
					attempts = studentSubmissionStatus.getAttempts();
					lastModifiedDate = studentSubmissionStatus.getLastModifiedDate();
					lastModifiedDate = lastModifiedDate.replaceAll("T", " ");
					lastModifiedDate = lastModifiedDate.substring(0, 19);
				}

				assignment.setStatus(status);
				assignment.setAttempts(attempts);
				assignment.setAttemptsLeft((maxAttempts - Integer.parseInt(attempts)) + "");
				assignment.setSem(subjectSemMap.get(subject));
				assignment.setLastModifiedDate(lastModifiedDate);

				if (failSubjects.contains(subject)) {
					if ("Submitted".equals(status)) {
						assignment.setPreviewPath(studentSubmissionStatus.getPreviewPath());
						failSubjectSubmissionCount++;
					}
					failSubjectsAssignmentFilesList.add(assignment);
					
				} else {
					if ("Submitted".equals(status)) {
						assignment.setPreviewPath(studentSubmissionStatus.getPreviewPath());
						currentSemSubmissionCount++;
					}
					currentSemAssignmentFilesList.add(assignment);
					
				}
			}
		}

		// String yearMonth = dao.getMostRecentAssignmentSubmissionPeriod();
		String yearMonth = dao.getLiveAssignmentMonth() + "-" + dao.getLiveAssignmentYear();
		response.setYearMonth(yearMonth);

		String currentSemEndDateTime = "";
		String failSubjectsEndDateTime = "";

		response.setCurrentSemAssignmentFilesList(currentSemAssignmentFilesList);

		int currentSemSubjectsCount = (currentSemAssignmentFilesList == null ? 0
				: currentSemAssignmentFilesList.size());

		response.setCurrentSemSubjectsCount(currentSemSubjectsCount);
		response.setCurrentSemSubmissionCount(currentSemSubmissionCount);

		response.setFailSubjectsAssignmentFilesList(failSubjectsAssignmentFilesList);
		int failSubjectsCount = (failSubjectsAssignmentFilesList == null ? 0 : failSubjectsAssignmentFilesList.size());

		response.setFailSubjectsCount(failSubjectsCount);
		response.setFailSubjectSubmissionCount(failSubjectSubmissionCount);

		if (currentSemSubjectsCount > 0) {
			currentSemEndDateTime = currentSemAssignmentFilesList.get(0).getEndDate().substring(0, 19);
		}
		if (failSubjectsCount > 0) {

			for (AssignmentFileBean assignmentFileBean : failSubjectsAssignmentFilesList) {
				if (ANSSubjects.contains(assignmentFileBean.getSubject())) {
					// ANS cases will always be allowed to Submit
					assignmentFileBean.setSubmissionAllowed(true);
					subjectsNotAllowedToSubmit.remove(assignmentFileBean.getSubject());
				} else if (subjectsNotAllowedToSubmit.contains(assignmentFileBean.getSubject())) {
					assignmentFileBean.setSubmissionAllowed(false);
				} else {
					assignmentFileBean.setSubmissionAllowed(true);
				}

				int pastCycleAssignmentAttempts = dao.getPastCycleAssignmentAttempts(assignmentFileBean.getSubject(),
						userId);
				if (pastCycleAssignmentAttempts >= 2 && !"Submitted".equals(assignmentFileBean.getStatus())) {
					assignmentFileBean.setPaymentApplicable("Yes");
					boolean hasPaidForAssignment = dao.checkIfAssignmentFeesPaid(assignmentFileBean.getSubject(),
							userId); //// check if Assignment Fee Paid for Current drive
					if (!hasPaidForAssignment) {
						assignmentFileBean.setPaymentDone("No");
					} else {
						assignmentFileBean.setPaymentDone("Yes");
					}
				}

			}

			// request.getSession().setAttribute("subjectsNotAllowedToSubmit",
			// subjectsNotAllowedToSubmit);
			response.setSubjectsNotAllowedToSubmit(subjectsNotAllowedToSubmit);
			failSubjectsEndDateTime = failSubjectsAssignmentFilesList.get(0).getEndDate().substring(0, 19);
			// Offline Students dont have limits on their attempts//
			if ("Offline".equals(student.getExamMode())) {
				for (AssignmentFileBean assignmentFileBean : failSubjectsAssignmentFilesList) {

					assignmentFileBean.setPaymentDone("Yes");
				}
			}
		}

		response.setCurrentSemEndDateTime(currentSemEndDateTime);
		response.setFailSubjectsEndDateTime(failSubjectsEndDateTime);

		if ((currentSemSubjectsCount + failSubjectsCount) == 0) {
			// setError(request, "No Assignments allocated to you.");
		}

		ArrayList<String> timeExtendedStudentIdSubjectList = dao.assignmentExtendedSubmissionTime();
		response.setTimeExtendedStudentIdSubjectList(timeExtendedStudentIdSubjectList);

		// request.getSession().setAttribute("timeExtendedStudentIdSubjectList",timeExtendedStudentIdSubjectList);

		// return new ResponseEntity<>(response,headers,HttpStatus.OK);
		return response;
	}

	public ExamAssignmentResponseBean submitAssignment(AssignmentFileBean assignmentFile) {
		ExamAssignmentResponseBean result = new ExamAssignmentResponseBean();
		try {
			String sapId = assignmentFile.getSapId();
			AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
			StudentExamBean student = dao.getSingleStudentsData(sapId);
			boolean isOnline = isOnline(student);
			ArrayList<String> timeExtendedStudentIdSubjectList = dao.assignmentExtendedSubmissionTime();
			int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
			int attempts = 0;
			AssignmentFileBean submissionStatus = null;
			
			submissionStatus = dao.getQuickAssignmentsForSingleStudent(sapId, assignmentFile.getSubject(),
					assignmentFile.getYear(), assignmentFile.getMonth()).get(0);
	
 
			if (submissionStatus != null) {
				attempts = Integer.parseInt(submissionStatus.getAttempts());
				assignmentFile.setEndDate(submissionStatus.getEndDate()); // by - Prashant Set end date from prev dao call for email purpose due to commented latest dao call for /submit api.
			}
			
			assignmentFile.setAttempts(attempts + "");
			
			result.setMaxAttempts(maxAttempts);
		//	result.setCurrentYearList(currentYearList);
		//	result.setSubjectList(getSubjectList());
			//result.setAssignmentFile(assignmentFile);
			result.setSubject(assignmentFile.getSubject());

			if (attempts >= maxAttempts) {

				result.setError("true");
				result.setErrorMessage("Maximum Attempts reached to submit assignment for " + assignmentFile.getSubject());
				result.setAssignmentFile(assignmentFile);

				return result;
			}
			
			//Check if end date is expired or not
			if (!timeExtendedStudentIdSubjectList.contains(sapId + assignmentFile.getSubject())) {
				boolean isEndDateExpired = false;
				isEndDateExpired = fileUploadHelper.checkIsEndDateExpired(assignmentFile.getEndDate());
				if(isEndDateExpired) {
					result.setError("true");
					result.setErrorMessage("Unable to submit assignment for \"+assignmentFile.getSubject()+\" subject as deadline is over.");
					result.setAssignmentFile(assignmentFile);
	
					return result;
				}
			}

			if (assignmentFile == null || assignmentFile.getFileData() == null) {
				result.setError("true");
				result.setErrorMessage("Error in file Upload: No File Selected");
				result.setAssignmentFile(assignmentFile);

				return result;
			}

			String fileName = assignmentFile.getFileData().getOriginalFilename();
			if (fileName == null || "".equals(fileName.trim())) {
				result.setError("true");
				result.setErrorMessage("Error in file Upload Inner: No File Selected");
				result.setAssignmentFile(assignmentFile);
				
				return result;
			}

			String errorMessage = uploadAssignmentSubmissionFile(assignmentFile, assignmentFile.getYear(),
					assignmentFile.getMonth(), sapId);

			// Check if file saved to Disk successfully & Antivirus check. Do not delete
			// below code
			if (errorMessage == null) {
				// Below is the code for Virus scan. Please do not delete it. It is commented,
				// since antivirus check is disabled as of now.
				// String scanStatus = "CLEAN";
				/*
				 * String scanStatus = scanFile(assignmentFile);
				 * if("ERROR_INPUT_STREAM_OPEN".equals(scanStatus)){ //Unable to open file. Try
				 * one more time, and it should works with Symantec Engine. This is a bug with
				 * Symantec Engine scanStatus = scanFile(assignmentFile); }
				 * 
				 * if("ERROR_INPUT_STREAM_OPEN".equalsIgnoreCase(scanStatus)){ //Ask student to
				 * retry. It will work assignmentFile.setPreviewPath(null);
				 * request.setAttribute("error", "true"); request.setAttribute("errorMessage",
				 * "A temporary error occured while scanning file "+
				 * assignmentFile.getFileData().getOriginalFilename() +". Error: "+scanStatus +
				 * ". Please try uploading file again.");
				 * modelnView.addObject("assignmentFile",assignmentFile);
				 * 
				 * return modelnView; }else if(!"CLEAN".equalsIgnoreCase(scanStatus)){
				 * assignmentFile.setPreviewPath(null); request.setAttribute("error", "true");
				 * request.setAttribute("errorMessage",
				 * "Virus Found in File "+assignmentFile.getFileData().getOriginalFilename()
				 * +". File not Saved. Error: "+scanStatus);
				 * modelnView.addObject("assignmentFile",assignmentFile);
				 * 
				 * return modelnView; }else{
				 */
				maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
				attempts = Integer.parseInt(assignmentFile.getAttempts()) + 1;
				assignmentFile.setAttempts(attempts + "");
				assignmentFile.setStatus("Submitted");
				assignmentFile.setCreatedBy(sapId);
				assignmentFile.setLastModifiedBy(sapId);
				try {
					dao.saveAssignmentSubmissionDetails(assignmentFile, maxAttempts, student);
				} catch (Exception e) {
					assg_logger_mobile.error("Exception Error saveAssignmentSubmissionDetails() : Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - Error = {}",
							sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),assignmentFile.getAttempts(),e);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				try {
					dao.saveQuickAssignmentSubmissionDetails(assignmentFile, student);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					assg_logger_mobile.error("Exception Error saveQuickAssignmentSubmissionDetails() : Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - StudentFilePath = {} - Error = {}",
							sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),assignmentFile.getAttempts(),assignmentFile.getStudentFilePath(),e);
				}
				assg_logger_mobile.info("Saved student assignment data in all table : Sapid = "+sapId +
						" - Month = "+ assignmentFile.getMonth()+
						" - Year = "+ assignmentFile.getYear()+
						" - Subject = "+ assignmentFile.getSubject()+
						" - StudentFilePath = "+ assignmentFile.getStudentFilePath()+
						" - Attempt = "+ assignmentFile.getAttempts()+
						" - From Mobile");
				// }

			}
			else if("corruptFile".equalsIgnoreCase(errorMessage)) {

				assg_logger_mobile.error("Corrupt/Blank file detected while uploading assignment : Sapid = "+sapId +
						" - Month = "+ assignmentFile.getMonth()+
						" - Year = "+ assignmentFile.getYear()+
						" - Subject = "+ assignmentFile.getSubject()+
						" - ErrorMessage = "+ errorMessage);
				result.setError("true");
				result.setErrorMessage("Unable to read PDF, the uploaded file must be corrupt or blank. Please verify and submit an updated file.");
				result.setAssignmentFile(assignmentFile);
				
				return result;
			}else {
				result.setError("true");
				result.setErrorMessage("Error in file Upload Outer: " + errorMessage);
				result.setAssignmentFile(assignmentFile);
				assg_logger_mobile.error("Error in file Upload Outer while uploading assignment : Sapid = "+sapId +
						" - Month = "+ assignmentFile.getMonth()+
						" - Year = "+ assignmentFile.getYear()+
						" - Subject = "+ assignmentFile.getSubject()+
						" - ErrorMessage = "+ errorMessage);
				
				return result;
			}

			try {
				dao.upsertAssignmentStatus(assignmentFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				assg_logger_mobile.error("Exception Error upsertAssignmentStatus() : Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - Error = {}",
						sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),assignmentFile.getAttempts(),e);
			}
			
//			assignmentFile = dao.getQuickAssignmentsForSingleStudent(sapId, assignmentFile.getSubject(),
//					assignmentFile.getYear(), assignmentFile.getMonth()).get(0);

			/*
			 * Commented by Steffi to allow offline to submit assignment in APR/SEP
			 * if(!isOnline){ assignmentFile =
			 * dao.getAssignmentDetailsForStudent(assignmentFile, student); }else{
			 */
//			if (failSubjectList != null && failSubjectList.contains(assignmentFile.getSubject())) {
//				assignmentFile = dao.getResitAssignmentDetailsForStudent(assignmentFile, student);// Fail subject
//																									// details
//			} else {
//				assignmentFile = dao.getAssignmentDetailsForStudent(assignmentFile, student);// Current sem subject
//																								// details
//			}
			/* } */  
			result.setSuccess("true");

			//int usedAttempts = Integer.parseInt(assignmentFile.getAttempts()); by Prashant - to avoid getting value from dao call for /submit api
			int usedAttempts = attempts;
			String successMessage = "Files Uploaded successfully. Please cross verify the Preview of the uploaded assignment file. "
					+ "Take Printscreen for your records. Attempt " + usedAttempts + " Exhausted.   ";

			if (usedAttempts < maxAttempts) {
				successMessage = successMessage
						+ "Incase of incorrect/incomplete file: you can use the remaining attempts and be cautious while resubmitting the assignment file.";
			} else {
				successMessage = successMessage + "You have exhausted all submission attempts of the subject";
			}
			result.setSuccessMessage(successMessage);
			result.setAssignmentFile(assignmentFile);


			// Send Email to student

			MailSender mailSender = (MailSender) act.getBean("mailer");
			try {
				mailSender.sendAssignmentReceivedEmail(student, assignmentFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				assg_logger_mobile.error("Exception Error sendAssignmentReceivedEmail() : Sapid = {} - Month Year = {}/{} - Subject = {} - Attempt = {} - StudentFilePath = {} - Error = {}",
						sapId,assignmentFile.getMonth(),assignmentFile.getYear(),assignmentFile.getSubject(),assignmentFile.getAttempts(),assignmentFile.getStudentFilePath(),e);
			}
			assignmentFile.setFileData(null);
			assg_logger_mobile.info("Successfully uploaded assignment and recieved mail details : Sapid = "+sapId +
					" - Month = "+ assignmentFile.getMonth()+
					" - Year = "+ assignmentFile.getYear()+
					" - Subject = "+ assignmentFile.getSubject()+
					" - StudentFilePath = "+ assignmentFile.getStudentFilePath()+
					" - Attempt = "+ assignmentFile.getAttempts()+
					" - From Mobile");

		} catch (Exception e) {
			
			result.setError("true");
			result.setErrorMessage("Error 1:" + e.getMessage());
			assignmentFile.setFileData(null);
			result.setAssignmentFile(assignmentFile);
			assg_logger_mobile.error("Try catch error while uploading assignment : Sapid = "+assignmentFile.getSapId() +
					" - Month = "+ assignmentFile.getMonth()+
					" - Year = "+ assignmentFile.getYear()+
					" - Subject = "+ assignmentFile.getSubject()+
					" - ErrorMessage = "+ e.getMessage());
			return result;
		}
		return result;
	}

	public boolean isOnline(StudentExamBean student) {

		boolean isOnline = false;
		try {
			String programStucture = student.getPrgmStructApplicable();

			if ("Online".equals(student.getExamMode())) {
				// New batch students and certificate program students will be considered online
				// and with 4 attempts for assginmnet submission
				isOnline = true;
			}
			return isOnline;
		} catch (Exception e) {
			
			return false;
		}

	}

	private ArrayList<String> getSubjectsForStudent(StudentExamBean student, HashMap<String, String> subjectSemMap) {

		ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = getProgramSubjectMappingList();

		ArrayList<String> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

			if (bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable())
					&& bean.getProgram().equals(student.getProgram()) && bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())// Subjects has not already cleared it
			) {
				subjects.add(bean.getSubject());
				subjectSemMap.put(bean.getSubject(), bean.getSem());

			}

			// Below code is for creating map of subject and sem
			if (bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable())
					&& bean.getProgram().equals(student.getProgram())) {
				subjectSemMap.put(bean.getSubject(), bean.getSem());

			}
		}

		return subjects;
	}
	
	

	public ArrayList<ProgramSubjectMappingExamBean> getProgramSubjectMappingList() {
		if (this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0) {
			ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
			this.programSubjectMappingList = eDao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	}

	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
	}

	private ArrayList<String> getPassSubjects(StudentExamBean student, PassFailDAO dao) {
		ArrayList<String> passSubjectList = dao.getPassSubjectsNamesForSingleStudent(student.getSapid());
		return passSubjectList;
	}
	
	private ArrayList<String> getSubjectwithPassFailEntry(StudentExamBean student, PassFailDAO dao) {
		ArrayList<String> passSubjectList = dao.getSubjectwithPassFailEntry(student.getSapid());
		return passSubjectList;
	}

	private ArrayList<AssignmentFileBean> getFailSubjects(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		ArrayList<AssignmentFileBean> failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		return failSubjectList;
	}

	private ArrayList<AssignmentFileBean> getANSNotProcessed(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		ArrayList<AssignmentFileBean> failSubjectList = dao.getANSNotProcessed(student.getSapid());
		return failSubjectList;
	}

	private ArrayList<String> getFailSubjectsNames(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		ArrayList<String> failSubjectList = null;
		failSubjectList = dao.getFailSubjectsNamesForAStudent(student.getSapid());
		return failSubjectList;
	}

	private String uploadAssignmentSubmissionFile(AssignmentFileBean bean, String year, String month, String sapId) {

		String errorMessage = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;

		CommonsMultipartFile file = bean.getFileData();
		String fileName = file.getOriginalFilename();

		long fileSizeInBytes = bean.getFileData().getSize();
		if (fileSizeInBytes > MAX_FILE_SIZE_LIMIT) {
			errorMessage = "File size exceeds 5 MB. Please upload a file with size less than 5 MB";
			return errorMessage;
		}
		
		if(!FileUploadHelper.checkPdfFileContentType(file.getContentType())) {		//File Header Validation added as per card: 12152
			assg_logger.error("Error Invalid file with Content-Type: {} for subject: {} by student: {}", file.getContentType(), bean.getSubject(), bean.getSapId());
			return "File type not supported. Please upload a PDF file!";
		}


		// Replace special characters in file name
		String subject = bean.getSubject();
		subject = subject.replaceAll("'", "_");
		subject = subject.replaceAll(",", "_");
		subject = subject.replaceAll("&", "and");
		subject = subject.replaceAll(" ", "_");
		subject = subject.replaceAll(":", "");


		if (!(fileName.toUpperCase().endsWith(".PDF"))) {
			errorMessage = "File type not supported. Please upload .pdf file.";
			return errorMessage;
		}
		String folderPath =  "Submissions/"+ month + year + "/" + subject + "/";
		// Add Random number to avoid student guessing names of other student's
		// assignment files
		fileName = sapId + "_" + subject + "_" + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
		bean.setStudentFilePath(month + year + "/" + subject + "/"+fileName);
		bean.setPreviewPath(month + year + "/" + subject + "/"+fileName);

		/*try {
			// PDF stores first 4 letters as %PDF, which can be used to check if a file is
			// actually a pdf file and not just going by extension
			InputStream tempInputStream = file.getInputStream();
			;
			byte[] initialbytes = new byte[4];
			tempInputStream.read(initialbytes);

			tempInputStream.close();
			String fileType = new String(initialbytes);

			if (!"%PDF".equalsIgnoreCase(fileType)) {
				errorMessage = "File is not a PDF file. Please upload .pdf file.";
				return errorMessage;
			}

			inputStream = file.getInputStream();
			String filePath = SUBMITTED_ASSIGNMENT_FILES_PATH + month + year + "/" + subject + "/" + fileName;
			String previewPath = month + year + "/" + subject + "/" + fileName;
			// Check if Folder exists which is one folder per Exam (Jun2015, Dec2015 etc.)
			File folderPath = new File(SUBMITTED_ASSIGNMENT_FILES_PATH + month + year + "/" + subject);
			if (!folderPath.exists()) {
				boolean created = folderPath.mkdirs();
			}

			File newFile = new File(filePath);

			outputStream = new FileOutputStream(newFile);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			bean.setStudentFilePath(filePath);
			bean.setPreviewPath(previewPath);
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			errorMessage = "Error in uploading file for " + bean.getSubject() + " : " + e.getMessage();
			
		}*/
		HashMap<String,String> s3_response = amazonS3Helper.uploadFiles(file,folderPath,"assignment-files",folderPath+fileName);
		if(!s3_response.get("status").equalsIgnoreCase("success")) {
			return "Unable to upload file: " + s3_response.get("url");
		}

		
		boolean isFileCorupted = fileUploadHelper.isFileCorrupted(s3_response.get("url"));
		if(isFileCorupted) {
			assg_logger.error("Error Corrupted/Unreadable File Sapid:{} Subject:{} FilePath URL:{}", sapId, bean.getSubject(), s3_response.get("url"));
			return "Unable to read PDF, the uploaded file must be corrupt or blank. Please verify and submit an updated file.";
		}
		
		boolean isQPFileUploaded = fileUploadHelper.isQPFileUploaded(s3_response.get("url"),ASSIGNMENT_FILES_PATH+ bean.getQuestionFilePreviewPath(),null,SERVER_PATH);
		if(isQPFileUploaded) {
			assg_logger.error("Error QP File Uploaded Sapid:{} Month/Year:{}/{} Subject:{} On Attempt:{} URL/filepath:{}",
					sapId, month, year, bean.getSubject(), Integer.parseInt(bean.getAttempts()) + 1, s3_response.get("url"));
			errorMessage = "Invalid Submission, the uploaded file is the same as a question paper file. Please upload a valid file with answer/s.";
		}

		return errorMessage;
	}
	
	public void updateConsumerPrgStructIdForProgChange() {
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		dao.updateConsumerPrgStructIdForProgChange(); 
	} 
	private ArrayList<AssignmentFileBean> getUGFailSubjects(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<AssignmentFileBean> failSubjectList = dao.getUGFailSubjectsForAStudent(student.getSapid());
		return failSubjectList;
	}
	
	private List<String> getUGPassSubjects(StudentExamBean student) {
		PassFailDAO dao = (PassFailDAO)act.getBean("passFailDAO");
		return dao.getUGPassSubjectsForAStudent(student.getSapid());
	}
	public ExamAssignmentResponseBean getAssignments(String userId, String liveType) {
		
		StudentMarksDAO sMarksDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		PassFailDAO pdao = (PassFailDAO) act.getBean("passFailDAO");
		
		ExamAssignmentResponseBean response = new ExamAssignmentResponseBean();
		HashMap<String, String> subjectSemMap = new HashMap<>();   
		List<AssignmentFileBean> allAssignmentFilesList = new ArrayList<>();
		HashMap<String, AssignmentFileBean> subjectSubmissionMap = new HashMap<>();
		ArrayList<String> failSubjects = new ArrayList<>(); 
		List<AssignmentFileBean> failSubjectsAssignmentFilesList = new ArrayList<>();
		List<AssignmentFileBean> currentSemAssignmentFilesList = new ArrayList<>();
		ArrayList<String> ANSSubjects = new ArrayList<>();
		ArrayList<String> subjectsNotAllowedToSubmit = new ArrayList<String>();
		ArrayList<String> currentSemSubjects = new ArrayList<>();
		
		String currentSem = null; 
		int maxAttempts = Integer.parseInt(ASSIGNMENT_SUBMISSIONS_ATTEMPTS);
		StudentExamBean student = eDao.getSingleStudentsData(userId);
		
		
		if (student == null) {
			response.setError("Student Enrollment not found. Please contact Administrator.");
			
		} else {
			//set waived off subjects in student bean
			studentService.mgetWaivedOffSubjects(student);
		}
		
				
		int hasAssignment = dao.checkHasAssignment(student.getConsumerProgramStructureId());
		
		if(hasAssignment > 0) {
			
			//**starts regular**
			
			//refreshing cache with live assignment year and month
			AssignmentLiveSetting assignmentLiveSettingRegular = dao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(), "Regular");
			
			if(liveType.equalsIgnoreCase("Regular")&& assignmentLiveSettingRegular != null) {
		        dao.setLiveAssignmentYear(assignmentLiveSettingRegular.getAcadsYear());
				dao.setLiveAssignmentMonth(assignmentLiveSettingRegular.getAcadsMonth());
				
				//get current registration of student for live assignment cycle
				StudentExamBean studentRegistrationData = dao.getStudentRegistrationData(userId);
				
				// Take program from current Registration.
				if (studentRegistrationData != null) { 
					student.setProgram(studentRegistrationData.getProgram());
					student.setSem(studentRegistrationData.getSem());
					currentSem = studentRegistrationData.getSem(); 
				}
				
			    //this code is to get current sem subjects 
				//include waivedIn subjects if any
				if (studentRegistrationData != null) {
					currentSemSubjects = getSubjectsForStudent(student, subjectSemMap);
				}
				ArrayList<String> waivedInSubjects = studentService.mgetWaivedInSubjects(student);
				if(currentSemSubjects != null) {
					for(String subject : waivedInSubjects) {
						if(!currentSemSubjects.contains(subject)) {
							currentSemSubjects.add(subject);
						}
					}
				}else {
					currentSemSubjects = waivedInSubjects;
				}
				subjectSemMap.putAll(student.getWaivedInSubjectSemMapping());
				
				//find pass subjects and
				//remove this from current sem subjects 
			    try { 
					ArrayList<String> passFailSubjectsList = getSubjectwithPassFailEntry(student, pdao);
					if (!passFailSubjectsList.isEmpty() && passFailSubjectsList != null) {
						for (String subject : passFailSubjectsList) {
							if (currentSemSubjects.contains(subject)) {
								currentSemSubjects.remove(subject);
							}
						}
					}
				} catch (Exception e) {

					
					// response.setError("unable to get pass subjects");
				}
			    
			    
			    //get pass subjects for BBA. for UG, pass subjects is in remark db
			    //remove this from current sem subjects
//				if(student.getProgram().equals("BBA") || student.getProgram().equals("B.Com")) {
				if(REMARK_GRADES_PROGRAMS.contains(student.getProgram())) {
					List<String> ugPassSubjectsList = getUGPassSubjects(student);
					for(String subject:ugPassSubjectsList){
						if(currentSemSubjects.contains(subject)){
							currentSemSubjects.remove(subject);
						}
					}
					
					//Added By Prashant 9-11-2022 - BBA sem 5,6 electives subjects logic check 
					try {
						if(Arrays.asList(electiveMasterkeys).contains(studentRegistrationData.getConsumerProgramStructureId()) && Arrays.asList(electiveSem).contains(student.getSem())) {
							List<String> electiveSubjectsList = getCurrentCycleSubjects(student.getSapid(),studentRegistrationData.getYear(),studentRegistrationData.getMonth());
							Iterator<String> itr = currentSemSubjects.iterator();            
							while(itr.hasNext()){
								String subject = itr.next();
									if(!electiveSubjectsList.contains(subject)){
										itr.remove();
									}
							}
						}
					}catch (Exception e) {
						logger.error("Error in service side getCurrentCycleSubjects while make live assignment (Regular) for sapid - "+userId);
						logger.error("Error occurs - "+e);

					}
				}
				
				//remove project related subjects from assignments. 
				//As make project live is a different module 
				currentSemSubjects.remove("Project");
				currentSemSubjects.remove("Module 4 - Project");
				
				//get assignment question files 
				List<AssignmentFileBean> currentSemFiles = null;
				if (currentSemSubjects != null && currentSemSubjects.size() > 0) {
					currentSemFiles = dao.getAssignmentsForSubjects(currentSemSubjects, student);
				}
				if (currentSemFiles != null) {
					allAssignmentFilesList.addAll(currentSemFiles);
				}
				
				//get assignment submission
				HashMap<String, AssignmentFileBean> currentSemSubjectSubmissionMap = dao
						.getSubmissionStatus(currentSemSubjects, userId);
				
				if (currentSemSubjectSubmissionMap != null) {
					subjectSubmissionMap.putAll(currentSemSubjectSubmissionMap);
				}
				
			}
			//***end's regular***
			
			//***starts resit***
			AssignmentLiveSetting assignmentLiveSettingResit = dao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(), "Resit");
			if(liveType.equalsIgnoreCase("Resit") && assignmentLiveSettingResit != null) {
				dao.setLiveResitAssignmentYear(assignmentLiveSettingResit.getAcadsYear());
				dao.setLiveResitAssignmentMonth(assignmentLiveSettingResit.getAcadsMonth());
			    
				failSubjects = new ArrayList<>();
				
				try {
					ArrayList<AssignmentFileBean> failSubjectsBeans = getFailSubjects(student);
//					if(student.getProgram().equals("BBA") || student.getProgram().equals("B.Com")) {
					if(REMARK_GRADES_PROGRAMS.contains(student.getProgram())) {
						ArrayList<AssignmentFileBean> softSkillsFailSubjectsBeans = getUGFailSubjects(student);
						if(softSkillsFailSubjectsBeans != null && softSkillsFailSubjectsBeans.size() > 0){
							
							for (AssignmentFileBean bean : softSkillsFailSubjectsBeans) {
								String subject = bean.getSubject();
								String sem = bean.getSem();
								failSubjects.add(bean.getSubject());
								subjectSemMap.put(subject, sem);
								
								if("ANS".equalsIgnoreCase(bean.getRemarks())){
									ANSSubjects.add(subject);
								}
							}
						}
						
					}
					if (failSubjectsBeans != null && failSubjectsBeans.size() > 0) {

						for (AssignmentFileBean bean : failSubjectsBeans) {
							String subject = bean.getSubject();
							String sem = bean.getSem();
							
							subjectSemMap.put(subject, sem);
							//adding vilpesh's check here for softskill pass subjects
//							if(student.getProgram().equals("BBA") || student.getProgram().equals("B.Com")) {
							if(REMARK_GRADES_PROGRAMS.contains(student.getProgram())) {
								List<String> ugPassSubjectsList  = getUGPassSubjects(student);
								if(!ugPassSubjectsList.contains(subject)){
									failSubjects.add(bean.getSubject());
								}
							}else {//for normal students old logic
								failSubjects.add(bean.getSubject());
							}
							
							if ("ANS".equalsIgnoreCase(bean.getAssignmentscore())) {
								ANSSubjects.add(subject);
							}
						}
					}
				} catch (Exception e) {
					
				}
				try {
					ArrayList<AssignmentFileBean> failANSSubjectsBeans = getANSNotProcessed(student);
					if (failANSSubjectsBeans != null && failANSSubjectsBeans.size() > 0) {

						for (int i = 0; i < failANSSubjectsBeans.size(); i++) {
							String subject = failANSSubjectsBeans.get(i).getSubject();
							String sem = failANSSubjectsBeans.get(i).getSem();
							failSubjects.add(failANSSubjectsBeans.get(i).getSubject());
							subjectSemMap.put(subject, sem);

							ANSSubjects.add(subject);
						}
					}
				} catch (Exception e) {
					
				}
				boolean isResultLiveForLastSubmissionCycle = sMarksDao.isResultLiveForLastAssignmentSubmissionCycle();
				if (!isResultLiveForLastSubmissionCycle) {
					/*
					 * Commented so that current sem subjects should not show in ANS/Results
					 * Awaited/Failed Subjects Table currentSemResultAwaitedSubjectsList =
					 * dao.getResultAwaitedAssignmentSubmittedSubjectsList(student.getSapid()); for
					 * (AssignmentFileBean assignmentFileBean :
					 * currentSemResultAwaitedSubjectsList){ String subject =
					 * assignmentFileBean.getSubject();
					 * if(!failSubjects.contains(subject)){ failSubjects.add(subject); } }
					 */
					ArrayList<String> subjectsSubmittedInLastCycle = dao.getFailedSubjectsSubmittedInLastCycle(userId,
							failSubjects);
					ArrayList<String> subjectsExamBookedInLastCycle = dao.getFailedSubjectsExamBookedInLastCycle(userId,
							failSubjects);

					for (String subject : subjectsSubmittedInLastCycle) {
						ANSSubjects.remove(subject);
					}
					if (subjectsSubmittedInLastCycle.size() > 0 || subjectsExamBookedInLastCycle.size() > 0) {
						// There are failed subjects submitted in last submission cycle

						// If result is not live then subjects submitted in last cycle cannot be
						// submitted till results are live
						subjectsNotAllowedToSubmit.addAll(subjectsSubmittedInLastCycle);
						subjectsNotAllowedToSubmit.addAll(subjectsExamBookedInLastCycle);
					}


				}
				failSubjects.remove("Project");
				failSubjects.remove("Module 4 - Project");
				
				List<AssignmentFileBean> failSubjectFiles = null;
				
				if (failSubjects != null && failSubjects.size() > 0) {
					failSubjectFiles = dao.getResitAssignmentsForSubjects(failSubjects, student);
				}
				if (failSubjectFiles != null) {

					allAssignmentFilesList.addAll(failSubjectFiles);
				}
				HashMap<String, AssignmentFileBean> failSubjectSubmissionMap = dao.getResitSubmissionStatus(failSubjects,
						student);
				
				if (failSubjectSubmissionMap != null) {
					subjectSubmissionMap.putAll(failSubjectSubmissionMap);
				}
			}	
			//***ends resit***
		 }
		
		 if (allAssignmentFilesList != null) {

			for (AssignmentFileBean assignment : allAssignmentFilesList) {
				String subject = assignment.getSubject();
				String status = "Not Submitted";
				String attempts = "0";
				String lastModifiedDate = null;

				AssignmentFileBean studentSubmissionStatus = subjectSubmissionMap.get(subject);
				if (studentSubmissionStatus != null) {
					status = studentSubmissionStatus.getStatus();
					attempts = studentSubmissionStatus.getAttempts();
					lastModifiedDate = studentSubmissionStatus.getLastModifiedDate();
					lastModifiedDate = lastModifiedDate.replaceAll("T", " ");
					lastModifiedDate = lastModifiedDate.substring(0, 19);
				}

				assignment.setStatus(status);
				assignment.setAttempts(attempts);
				assignment.setAttemptsLeft((maxAttempts - Integer.parseInt(attempts)) + "");
				assignment.setSem(subjectSemMap.get(subject));
				assignment.setSubmissionDate(lastModifiedDate);
				if (failSubjects.contains(subject)) {
					if ("Submitted".equals(status)) {
						assignment.setPreviewPath(studentSubmissionStatus.getPreviewPath());
						assignment.setStudentFilePath(studentSubmissionStatus.getStudentFilePath());
					}
					failSubjectsAssignmentFilesList.add(assignment);
					
				} else {
					if ("Submitted".equals(status)) {
						assignment.setPreviewPath(studentSubmissionStatus.getPreviewPath());
						assignment.setStudentFilePath(studentSubmissionStatus.getStudentFilePath());
					}
					assignment.setCurrentSemSubject("Y");
					currentSemAssignmentFilesList.add(assignment);
					
				}
			}
		}
 
		if (failSubjectsAssignmentFilesList.size() > 0) {

			for (AssignmentFileBean assignmentFileBean : failSubjectsAssignmentFilesList) {
				if (ANSSubjects.contains(assignmentFileBean.getSubject())) {
					// ANS cases will always be allowed to Submit
					assignmentFileBean.setSubmissionAllowed(true);
					subjectsNotAllowedToSubmit.remove(assignmentFileBean.getSubject());
				} else if (subjectsNotAllowedToSubmit.contains(assignmentFileBean.getSubject())) {
					assignmentFileBean.setSubmissionAllowed(false);
				} else {
					assignmentFileBean.setSubmissionAllowed(true);
				}

				int pastCycleAssignmentAttempts = dao.getPastCycleAssignmentAttempts(assignmentFileBean.getSubject(),
						userId);
				if (pastCycleAssignmentAttempts >= 2 && !"Submitted".equals(assignmentFileBean.getStatus())) {
					assignmentFileBean.setPaymentApplicable("Y");
					boolean hasPaidForAssignment = dao.checkIfAssignmentFeesPaid(assignmentFileBean.getSubject(),
							userId); //// check if Assignment Fee Paid for Current drive
					if (!hasPaidForAssignment) {
						assignmentFileBean.setPaymentDone("N");
					} else {
						assignmentFileBean.setPaymentDone("Y");
					}
				}

			}
			response.setSubjectsNotAllowedToSubmit(subjectsNotAllowedToSubmit);
			// Offline Students dont have limits on their attempts//
			if ("Offline".equals(student.getExamMode())) {
				for (AssignmentFileBean assignmentFileBean : failSubjectsAssignmentFilesList) {

					assignmentFileBean.setPaymentDone("Y");
				}
			}
		} 
        response.setAllAssignmentFilesList(allAssignmentFilesList);
		
		return response;
	}
	
	/**
	*Note:make changes in the assignment temp table for current cycle
	*when previous cycle's result is live. Need to change status of all result awaited subjects.
	*steps: 1) check if entry in assg for asg resit live cycle and status 'Results Awaited'
	*2) if student is pass, delete entry from qck table
	*3) if student fail, change result awaited status to not submitted and submissionAllow= "Y"
	*/
	public void updateQuickAssgTableOnPassfailProcess(ArrayList<PassFailExamBean> passFailStudentList) {
		 
		passFailLogger.info("------------------------ STARTED ASSIGNMENT TEMP TABLE UPDATE IN BATCH------------------------");
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		ArrayList<AssignmentFileBean> asgstoDelete = new ArrayList<AssignmentFileBean>();
		ArrayList<AssignmentFileBean> asgstoUpdate = new ArrayList<AssignmentFileBean>();
		
		passFailLogger.info("passFailStudentList size : "+passFailStudentList.size());
		for (PassFailExamBean passfailBean : passFailStudentList ) {
			 
			try {
				String sapid = passfailBean.getSapid();
				String subject = passfailBean.getSubject();
				
				StudentExamBean student = eDao.getSingleStudentsData(sapid);
				AssignmentLiveSetting resitLive = dao.getCurrentLiveAssignment(student.getConsumerProgramStructureId(),"Resit");
				
				passFailLogger.info("{} {} : current assignment resit cycle: {} {}",sapid, subject, resitLive.getExamYear(),resitLive.getExamMonth());
				
				String resitLiveYear = resitLive.getExamYear();
				String resitLiveMonth = resitLive.getExamMonth();
				
				AssignmentFileBean  resultAwaitedAsg = dao.getResultAwaitedAssgForSingleStudent(sapid,subject,resitLiveYear,resitLiveMonth);
				
				passFailLogger.info("{} {} : result Awaited Assignment: "+resultAwaitedAsg, sapid,subject);
				if(resultAwaitedAsg != null) {
					if(passfailBean.getIsPass().equalsIgnoreCase("Y") ) {
						asgstoDelete.add(resultAwaitedAsg);
						passFailLogger.info("{} {} : added to delete list", sapid, subject);
					}else {
						asgstoUpdate.add(resultAwaitedAsg);
						passFailLogger.info("{} {} : added to update list",sapid, subject);
					}
						
				}
			} catch (Exception e) { 
				passFailLogger.info("Error: while updating quick assignment table : "+Throwables.getStackTraceAsString(e));
			}
				
		}
		passFailLogger.info("---------------------------------- STARTED BATCH UPDATE AND DELETE ----------------------------------");
		dao.batchDeleteAssignmentFromQuickTable(asgstoDelete);
		dao.batchUpdateResultAwaitedAssgs(asgstoUpdate);
		passFailLogger.info("---------------------------------- FINISHED BATCH UPDATE AND DELETE ----------------------------------");
		passFailLogger.info("------------------------ FINISHED ASSIGNMENT TEMP TABLE UPDATE IN BATCH------------------------");

	}

	public void updateQuickAssgTableOnApplyGraceMark(List<PassFailExamBean> studentMarksList) {
		ArrayList<PassFailExamBean> passFailStudentList= new ArrayList<PassFailExamBean>();
		for (PassFailExamBean passfailBean : studentMarksList ) {
			passfailBean.setIsPass("Y");
			passFailStudentList.add(passfailBean);
		}
		updateQuickAssgTableOnPassfailProcess(passFailStudentList);
	} 
	
	
	public String getSingleMonthYear(List<String> list) {
		
		 String monthyear = null;
		 if(list.size() == 1 ){
			 
		    for (int i = 0; i < list.size(); i++) {
		    	monthyear = list.get(i); //take the ith object out of list
		        while (list.contains(monthyear)) {
		        	list.remove(monthyear); //remove all matching entries
		        }
		        list.add(monthyear); //at last add one entry
		    }
		 }
		//String monthyear = setToReturn.toString();
	return monthyear;
	}
	
	public List<AssignmentFileBean> getMonthYearForEvaluateAssignment(AssignmentFileBean searchBean) {
//		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		return asgdao.getMonthYearForEvaluateAssignment(searchBean);	 
	}
	
	public Page<AssignmentFileBean> getAssignmentsSubmissionForFaculty1(int i, int maxValue, AssignmentFileBean searchBean) {
		return asgdao.getAssignmentsSubmissionForFaculty1(i, Integer.MAX_VALUE, searchBean);
	}

	public boolean getAssignmentsForFaculty1Count(String facultyId, String month, String year) {
		return asgdao.getAssignmentsForFaculty1Count(facultyId, month, year);
	}

	public boolean getAssignmentsForFaculty2Count(String facultyId, String month, String year) {
		return asgdao.getAssignmentsForFaculty2Count(facultyId, month, year) ;
	}

	public Page<AssignmentFileBean> getAssignmentsSubmissionForFaculty2(int i, int maxValue,AssignmentFileBean searchBean) {
		return asgdao.getAssignmentsSubmissionForFaculty2(i, Integer.MAX_VALUE, searchBean);
	}

	public boolean getAssignmentsForFaculty3Count(String facultyId, String month, String year) {	
		return asgdao.getAssignmentsForFaculty3Count(facultyId, month, year);
	}

	public Page<AssignmentFileBean> getAssignmentsSubmissionForFaculty3(int i, int maxValue,AssignmentFileBean searchBean) {
		return asgdao.getAssignmentsSubmissionForFaculty3(i, Integer.MAX_VALUE, searchBean);
	}

	public boolean getAssignmentsForRevalCount(String facultyId, String month, String year) {	
		return asgdao.getAssignmentsForRevalCount(facultyId, month, year);
	}

	public Page<AssignmentFileBean> getAssignmentsSubmissionForReval(int i, int maxValue,AssignmentFileBean searchBean) {	
		return asgdao.getAssignmentsSubmissionForReval(i, Integer.MAX_VALUE, searchBean);
	}
	
	public ArrayList<String> getCurrentCycleSubjects(String sapid,String year,String month) {
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		return dao.getCurrentCycleSubjects(sapid, year, month);
	}

	public List<ResultDomain> getCopyCaseReport(AssignmentFileBean searchBean, String tableName) {
		return asgdao.getCopyCaseReport(searchBean,tableName);
	}

	public List<ResultDomain> getDetailedThreshold1CC(final String month, final String year, final String subject, final String sapId1) throws NullPointerException,SQLException {
		return asgdao.getDetailedThreshold1CC(month, year, subject, sapId1);
	}
	

	/*
	 * this method is for load the form page and return the matching text data
	 *  with last five cycle
	 */
	public List<ResultDomain> checkQPCopyCase(String subject, MultipartFile file,List<String> masterkey) throws Exception {

	
		ExamOrderExamBean currentCycle = null;
		List<ExamOrderExamBean> lstFiveCycleList = null;
		

		// extract uploaded pdf and store all questions in to list of String
		List<String> userQlist = qpHelper.readAllContaint(file);
		//get current live cycle
		currentCycle = asgdao.getCurrentLiveCycle();
		//calculate last cycle exam order
		Double currentOrder = Double.parseDouble(currentCycle.getOrder());
		Double lastFithOrder = Double.parseDouble(currentCycle.getOrder()) - 2.00;
		//get all last five cycles
		lstFiveCycleList = asgdao.getLastFiveCycle(lastFithOrder, currentOrder);
		// get all previous 5 cycles QP
		Set<String> qpfiles = new HashSet<String>();
		
		StringJoiner joiner = new StringJoiner(",");
		for (String value : masterkey) {
			joiner.add("'" + value + "'");
		}
		String masterkeyList = joiner.toString();

		List<String> subjectcodId=asgdao.getSubjectCidId(masterkeyList, subject,  currentCycle.getYear(),currentCycle.getMonth());
		
		Set<String> commanQueations=new LinkedHashSet<String>();
		
		List<ResultDomain> resultDomainBeanList=new ArrayList<ResultDomain>();

		//get QP files path's and compare and get matching percentage 
		for (ExamOrderExamBean examOrderExamBean : lstFiveCycleList) {
			
			ResultDomain result=new ResultDomain();
			double percentage=0.00;
			String qpfile=null;
			String subjecrcode=null;
			String secondFile=null;
			Set<String> matchingQuestipons=new HashSet<String>();
			for(String subCode:subjectcodId) {
			List<String> list = asgdao.getQPS(examOrderExamBean.getMonth(), examOrderExamBean.getYear(), subject,subCode);
			List<String> cycleQuestionsList=new ArrayList<String>();
			for (String Qpfilepath : list) {
				// extracting all questions from QP
				List<String> queations = qpHelper.readAllContaint(ASSIGNMENT_FILES_PATH + Qpfilepath);
				qpfile=ASSIGNMENT_FILES_PATH + Qpfilepath;
				cycleQuestionsList.addAll(queations);

			}
			ArrayList<List<String>> allFileContentsList=new ArrayList<List<String>>();
			allFileContentsList.add(userQlist);
			allFileContentsList.add(cycleQuestionsList);
			double matchingPercentage=qpHelper.compareQPCopyCase(0, allFileContentsList,examOrderExamBean.getMonth(),examOrderExamBean.getYear());
//			subjecrcode=subCode;
//			secondFile=qpfile;
			if(percentage<=matchingPercentage) {
				percentage=matchingPercentage;
				subjecrcode=subCode;
				secondFile=qpfile;
			}
			
			
			//get common text
			cycleQuestionsList.retainAll(userQlist);
			matchingQuestipons.addAll(cycleQuestionsList);
			commanQueations.addAll(cycleQuestionsList);
		
		}
			result.setSubject(subject);
			result.setMatching(percentage);
			result.setYear(examOrderExamBean.getYear());
			result.setMonth(examOrderExamBean.getMonth());
			result.setSecondFile(secondFile);
			result.setSubjectCode(subjecrcode);
			result.setCommonText(matchingQuestipons);
			resultDomainBeanList.add(result);
		}
		
		

			return resultDomainBeanList;

	}

	public String deleteUploadedQPFile(final String filepath) throws Exception{
		// TODO Auto-generated method stub
		return asgdao.deleteAssigmentfile(filepath);
	}

	
	
//	public List<String> getAllSubjects() throws Exception {
//
//		return asgdao.getSubjects();
//	}
//	
	

//	public void UpdateAssigemntTableFor() throws Exception{
//		
//		List<String> pssidtb=asgdao.getPssId("Written and Oral Communication","118");
//		System.out.println("pssid :: "+pssidtb.get(0));
//		List<String> subI= asgdao.getSubjectCidId(pssidtb.get(0));
//		System.out.println("sbject code id  :: "+subI.get(0));
//		
//		try
//		{
//		ArrayList<AssignmentFileBean> nullList=asgdao.getAllAssignmentWhoseSubjectCodeIsNull();
//		System.out.println("Total null :: "+nullList.size());
//		
//	
//			
//			for(AssignmentFileBean x:nullList) {	
//				System.out.println(x);
//					List<String> pssidl=asgdao.getPssId(x.getSubject(),x.getConsumerProgramStructureId());
//					String pssid=pssidl.get(0);
//					System.out.println(pssid);
//					List<String> subId= asgdao.getSubjectCidId(pssid);
//					String sugCodeId=subId.get(0);
//					System.out.println(sugCodeId);
//					logger.info("UPDATE exam.assignments SET subjectCodeId = '"+sugCodeId+"' WHERE (year = '"+x.getYear()+"') and (month = '"+x.getMonth()+"') and (subject = '"+x.getSubject()+"') and (consumerProgramStructureId = '"+x.getConsumerProgramStructureId()+"');" );
//					System.out.println("UPDATE exam.assignments SET subjectCodeId = '"+sugCodeId+"' WHERE (year = '"+x.getYear()+"') and (month = '"+x.getMonth()+"') and (subject = '"+x.getSubject()+"') and (consumerProgramStructureId = '"+x.getConsumerProgramStructureId()+"');" );
//					
//			
//		}
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//		}
//		
//	}

	public List<ResultDomain> getDetailedThreshold2CC(final String month, final String year, final String subject, final String sapId1) throws NullPointerException,SQLException {
		return asgdao.getDetailedThreshold2CC(month, year, subject, sapId1);
	}

	public List<ResultDomain> getUnique1CCList(final String month, final String year, final String subject, final String sapid) throws NullPointerException,SQLException{
		return asgdao.getUnique1CCList(month, year, subject, sapid);
	}
	
	public List<ResultDomain> getStudentAbove90CCList(final String month, final String year, final String subject, final String sapid) throws NullPointerException,SQLException{
		return asgdao.getStudentAbove90CCList(month, year, subject, sapid);
	}
	
	public List<ResultDomain> getUnique2CCList(final String month, final String year, final String subject, final String sapid) throws NullPointerException,SQLException{
		return asgdao.getUnique2CCList(month, year, subject, sapid);
	}

	public List<AssignmentFileBean> getMarkedCopyCases(final String month, final String year, final String subject, final String sapIdList) throws NullPointerException, SQLException {
		// TODO Auto-generated method stub
		return asgdao.getMarkedCopyCases(month, year, subject, sapIdList);
	}

	public boolean getResultLiveExamYear(final String examMonth, final String examYear) {
		return asgdao.getResultLiveExamYear(examMonth, examYear);
	}

	public void updateToUnprocessedInMarksTable(final List<AssignmentFileBean> unMarkCCList) {
		asgdao.updateToUnprocessedInMarksTable(unMarkCCList);
	}

	public void updateMarkedCCToUnmarkInTempTable(final List<AssignmentFileBean> otherReasonList) {
		asgdao.updateMarkedCCToUnmarkInTempTable(otherReasonList);
	}

	public void updateMarkedCCToUnmarkInSubmissionTable(final List<AssignmentFileBean> otherReasonList) {
		asgdao.updateMarkedCCToUnmarkInSubmissionTable(otherReasonList);
	}

	public void updateMarkedCCToAllocateInTempTable(final List<AssignmentFileBean> allocateList) {
		asgdao.updateMarkedCCToAllocateInTempTable(allocateList);
	}

	public void updateMarkedCCToAllocateInSubmissionTable(final List<AssignmentFileBean> allocateList) {
		asgdao.updateMarkedCCToAllocateInSubmissionTable(allocateList);
	}

	public boolean autoMarkCopyCase(final String month, final String year, final List<String> subjectList, final Logger CCLogger, final String lastModifiedBy) throws Exception{
		int subejctCount = 1;
		for(String subject: subjectList) {
			CCLogger.info("------------------ Started Auto Mark Copy Case Subject Count:{}/{} || Subject:{} || Month-Year:{}-{} -----------------",
						subejctCount, subjectList.size(), subject, month, year);
			try {
				List<ResultDomain> unqiue1SapidList = getUnique1CCList(month, year, subject, null);
				if(unqiue1SapidList != null && unqiue1SapidList.size()  > 0) {
					List<ResultDomain> unqiue2SapidList = getUnique2CCList(month, year, subject, null);
					if(unqiue2SapidList != null && unqiue2SapidList.size()  > 0) {
						unqiue1SapidList.addAll(unqiue2SapidList);
					}
					CCLogger.info("------------------ Started Auto Mark Copy Case Subject Count:{}/{} || Subject:{} || Month-Year:{}-{} || Students List Count:{} -----------------",
							subejctCount, subjectList.size(), subject, month, year, unqiue1SapidList.size());
					processAutoMarkCopyCase(month, year, subject, unqiue1SapidList, CCLogger, lastModifiedBy);
					CCLogger.info("------------------ Ended Auto Mark Copy Case Subject Count:{}/{} || Subject:{} || Month-Year:{}-{} || Students List Count:{} -----------------",
							subejctCount, subjectList.size(), subject, month, year, unqiue1SapidList.size());
				}else {
					CCLogger.error("Error: No records found for Auto Mark Copy Case Subject Count:{}/{} || Subject:{} || Month-Year:{}-{} -----------------",
							subejctCount, subjectList.size(), subject, month, year);
				}
			} catch (Exception e) {
				CCLogger.error("Exception Error processAutoMarkCopyCase() || Subject Count:{}/{} || Subject:{} || Month-Year:{}-{} || Error:{} -----------------",
						subejctCount, subjectList.size(), subject, month, year, e);
//				e.printStackTrace();
			}
			CCLogger.info("------------------ Ended Auto Mark Copy Case Subject Count:{}/{} || Subject:{} || Month-Year:{}-{} -----------------",
					subejctCount, subjectList.size(), subject, month, year);
			subejctCount++;
		}
		return true;
		
	}

	private void processAutoMarkCopyCase(final String month, final String year, final String subject, final List<ResultDomain> unqiue1SapidList, final Logger CCLogger, final String lastModifiedBy) throws InterruptedException, ExecutionException {
		// number of threads for executor service pool
		final int numberOfThreads = 8;
		final Executor executor = Executors.newFixedThreadPool(numberOfThreads);
		
		List<CompletableFuture<String>> pageContentFutures = 
													IntStream.range(0, unqiue1SapidList.size())
												    .mapToObj(index -> 
												    assignmentMarkCC(
																index,
																unqiue1SapidList,
																CCLogger,
																executor,
																lastModifiedBy)
											        		)
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
			
		    return pageContents.stream()
		            .filter(pageContent -> pageContent.contains("CompletableFuture"))
		            .count();
		});

		System.out.println("Number of Web Pages having CompletableFuture keyword - " + 
		        countFuture.get());

	}

	private CompletableFuture<String> assignmentMarkCC(final int index, final List<ResultDomain> unqiue1SapidList, final Logger CCLogger, final Executor executor, final String lastModifiedBy) {
		return CompletableFuture.supplyAsync(() -> {
			ResultDomain bean = new ResultDomain();
			bean = unqiue1SapidList.get(index);
			try {
				int count= 1;
				// Mark CC in assignmentsubmission table
				CCLogger.info("---------- Started markCCInSubmissionTable() || Month-Year:{}-{} || Subject :{} || Sapid:{} ----------",
								bean.getMonth(), bean.getYear(),
								bean.getSubject(), bean.getSapid());
				count = markCCInSubmissionTable(bean.getMonth(), bean.getYear(), bean.getSubject(), bean.getSapid(), lastModifiedBy);
				CCLogger.info("---------- Ended markCCInSubmissionTable() || Month-Year:{}-{} || Subject :{} || Sapid:{} || Updated Rows:{} ----------",
						bean.getMonth(), bean.getYear(),
						bean.getSubject(), bean.getSapid(), 
						count);
				
				// Mark CC in temp table
				CCLogger.info("---------- Started markCCInTempTable() || Month-Year:{}-{} || Subject :{} || Sapid:{} ----------",
						bean.getMonth(), bean.getYear(),
						bean.getSubject(), bean.getSapid());
				count = markCCInTempTable(bean.getMonth(), bean.getYear(), bean.getSubject(), bean.getSapid(), lastModifiedBy);
				CCLogger.info("---------- Ended markCCInTempTable() || Month-Year:{}-{} || Subject :{} || Sapid:{} || Updated Rows:{} ----------",
						bean.getMonth(), bean.getYear(),
						bean.getSubject(), bean.getSapid(), 
						count);
				
			} catch (Exception e) {
				CCLogger.error(
						"Exception Error assignmentMarkCC() || Month-Year:{}-{} || Subject :{} || Sapid:{} || Error:{}",
						bean.getMonth(), bean.getYear(),
						bean.getSubject(), bean.getSapid(),
						e);
//				e.printStackTrace();
			}finally {
				bean= null;
			}
			
			return "abc";
		}, executor
				);
	}

	private int markCCInTempTable(final String month, final String year, final String subject, final String sapid, final String lastModifiedBy) {
		return	asgdao.markCCInTempTable(month, year, subject, sapid, lastModifiedBy);
	}

	private int markCCInSubmissionTable(final String month, final String year, final String subject, final String sapid, final String lastModifiedBy) {
		return asgdao.markCCInSubmissionTable(month, year, subject, sapid, lastModifiedBy);
	}

	public List<AssignmentFileBean> getCCMarkStudentCountSubjectWise(final String month, final String year, final List<String> subjectList) throws NullPointerException, SQLException{
		return asgdao.getCCMarkStudentCountSubjectWise(month, year, subjectList);
	}
	
	

}
