package com.nmims.services;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.FacultyExamBean;
import com.nmims.beans.LevelBasedSOPConfigBean;
import com.nmims.beans.LevelBasedSynopsisConfigBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectModuleExtensionBean;
import com.nmims.beans.ProjectModuleStatusBean;
import com.nmims.beans.ProjectStudentStatus;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.UploadProjectSOPBean;
import com.nmims.beans.UploadProjectSynopsisBean;
import com.nmims.beans.VivaSlotBookingConfigBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.LevelBasedProjectDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.daos.ProjectTitleDAO;
import com.nmims.helpers.LevelBasedProjectHelper;

@Service
public class ProjectStudentEligibilityService {


	@Autowired
	ApplicationContext act;

	
	@Autowired
	LevelBasedProjectHelper levelBasedProjectHelper;
	
	@Autowired
	LevelBasedProjectService levelBasedProjectService;
	
	@Autowired
	ProjectTitleDAO projectTitleDAO;
	
	@Autowired
	LevelBasedProjectDAO levelBasedProjectDAO;
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	@Autowired
	ExamBookingDAO examBookingDAO;
	
	@Value("#{'${SEM_5_PROJECT_APPLICABLE_PROGRAM_LIST}'.split(',')}")
	private List<String> SEM_5_PROJECT_APPLICABLE_PROGRAM_LIST;
	
	@Value("#{'${SEM_5_PROJECT_APPLICABLE_PROGRAM_LIST}'.split(',')}")
	private List<String> SEM_6_PROJECT_APPLICABLE_PROGRAM_LIST;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	private static final Logger projectSubmissionLogger = LoggerFactory.getLogger("projectSubmission");
	
	public void getPaymentInfoForStudentV2(StudentExamBean student, ProjectStudentStatus status) {
		if(student.getExamMode().equalsIgnoreCase("Offline")){
			status.setCanSubmit("N");
			status.setCantSubmitError("Offine students are not applicable to submit project!");
			return;
		}
		PassFailDAO passFailDAO = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<String> passedSubjects = passFailDAO.getPassSubjectsNamesForAStudent(status.getSapid());
		if(passedSubjects.contains(status.getSubject())) {
			status.setCanSubmit("N");
			status.setCantSubmitError("Subject already passed!");
			return;
		}
		ProjectConfiguration projectConfiguration = null;
		try {
			projectConfiguration = projectTitleDAO.getSingleLevelBasedProjectConfiguration(status.getExamYear(), status.getExamMonth(), status.getProgramSemSubjId());
		}catch (Exception e) {
			// TODO: handle exception
			status.setCanSubmit("N");
			status.setCantSubmitError("Project Configuration not found for given subject");
			return;
		}
		if(!"Y".equalsIgnoreCase(projectConfiguration.getHasSOP())) {
			status.setCanSubmit("N");
			status.setCantSubmitError("SOP is not applicable");
			return;
		}
		LevelBasedSOPConfigBean levelBasedSOPConfigBean = null;
		try {
			levelBasedSOPConfigBean = projectTitleDAO.getSOPConfiguration(status.getExamYear(), status.getExamMonth(), status.getProgramSemSubjId());
		}catch (Exception e) {
			// TODO: handle exception
			status.setCanSubmit("N");
			status.setCantSubmitError("SOP Configuration missing,SOP not setup yet. please contact admin for more information");
			return;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date endDate = null;
		Date startDate = null;
		Date curDate = new Date();
		try {
			endDate = sdf.parse(levelBasedSOPConfigBean.getEnd_date());
			startDate = sdf.parse(levelBasedSOPConfigBean.getStart_date());
		}catch (Exception e) {
			// TODO: handle exception
			status.setCanSubmit("N");
			status.setCantSubmitError("Invalid Date format in configuration, please contact admin");
			return;
		}
		
		if(!"Y".equalsIgnoreCase(levelBasedSOPConfigBean.getLive())) {
			status.setCanSubmit("N");
			status.setCantSubmitError("SOP Submission flag is not live");
			return;
		}
		
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		status.setCanSubmit("Y");
		status.setHasSOP("Y");
		ProjectModuleStatusBean projectModuleStatusBean = new ProjectModuleStatusBean();
		projectModuleStatusBean.setLive(levelBasedSOPConfigBean.getLive());
		projectModuleStatusBean.setMaxSubmissions(levelBasedSOPConfigBean.getMax_attempt());
		projectModuleStatusBean.setStartDate(levelBasedSOPConfigBean.getStart_date());
		projectModuleStatusBean.setEndDate(levelBasedSOPConfigBean.getEnd_date());
		projectModuleStatusBean.setPayment_applicable(levelBasedSOPConfigBean.getPayment_applicable());
		projectModuleStatusBean.setPayment_amount(levelBasedSOPConfigBean.getPayment_amount());
		
		try {
			FacultyExamBean faculty = getSOPStudentGuideInfo(status.getExamYear(), status.getExamMonth(), student.getSapid(), status.getSubject());
			projectModuleStatusBean.setFacultyId(faculty.getFacultyId());
			projectModuleStatusBean.setFacultyName(faculty.getFirstName() + " " + faculty.getLastName());
		}catch (Exception e) {
			// TODO: handle exception
			status.setCanSubmit("N");
			status.setCantSubmitError("Faculty mapping missing for these student, please contact admin");
			return;
		}
		
		
		
		//extension to student
		List<ProjectModuleExtensionBean> extensions = levelBasedProjectService.getProjectExtensionsListForStudent(status.getExamMonth(), status.getExamYear(), student.getSapid(), status.getProgramSemSubjId());
		for (ProjectModuleExtensionBean projectModuleExtensionBean : extensions) {
			if("SOP".equals(projectModuleExtensionBean.getModuleType())) {
				projectModuleStatusBean.setEndDate(projectModuleExtensionBean.getEndDate()); 
				try {
					endDate = sdf.parse(projectModuleStatusBean.getEndDate());
				} catch (ParseException e) {  
					
				}
				
			}
		}
		
		UploadProjectSOPBean uploadProjectSOPBean = levelBasedProjectDAO.getStudentSOPSubmissionBySapId(status.getExamMonth(), status.getExamYear(), student.getSapid());
		if(uploadProjectSOPBean != null && uploadProjectSOPBean.getAttempt() >= levelBasedSOPConfigBean.getMax_attempt()) {
			projectModuleStatusBean.setAllowSubmission(false);
			status.setCantSubmitError("Max attempt limit reached, You can attempt max: " + levelBasedSOPConfigBean.getMax_attempt());
		}else {
			projectModuleStatusBean.setAllowSubmission(true);
		}
		
		if(uploadProjectSOPBean != null) {
			projectModuleStatusBean.setSubmissionsMade(uploadProjectSOPBean.getAttempt());
			projectModuleStatusBean.setReason(uploadProjectSOPBean.getReason());
			projectModuleStatusBean.setSubmittedFilePath(uploadProjectSOPBean.getPreviewPath());
			projectModuleStatusBean.setStatus(uploadProjectSOPBean.getStatus());
		}else {
			projectModuleStatusBean.setSubmissionsMade(0);
			projectModuleStatusBean.setReason(null);
		}
		
		if(projectModuleStatusBean.isAllowSubmission() && curDate.before(endDate) && curDate.after(startDate)) {
			// check freesubject applicable
			ArrayList<String> freeSubjects = getFreeSubjects(student, eDao);
			ArrayList<String> individualFreeSubjects = eDao.getStudentFreeProjectMap().get(student.getSapid());
			if(individualFreeSubjects != null && individualFreeSubjects.size() > 0){
				freeSubjects.addAll(individualFreeSubjects);
			}
			if(freeSubjects.contains(status.getSubject())) {
				status.setPaymentPending("N");
			}else {
				if(uploadProjectSOPBean == null) {
					status.setPaymentPending("Y");
				}else {
					status.setPaymentPending("N");
				}
			}
			projectModuleStatusBean.setAllowSubmission(true);
		} else if(status.getCantSubmitError() == null || "".equalsIgnoreCase(status.getCantSubmitError())) {
			projectModuleStatusBean.setAllowSubmission(false);
			status.setCantSubmitError("SOP submission window is closed");
		}
		status.setSopStatus(projectModuleStatusBean);
	}
	
	public void getPaymentInfoForStudent(StudentExamBean student, ProjectStudentStatus status) {
		PassFailDAO passFailDAO = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<String> passedSubjects = passFailDAO.getPassSubjectsNamesForAStudent(status.getSapid());
		if(passedSubjects.contains(status.getSubject())) {
			status.setCanSubmit("false");
			status.setCantSubmitError("Subject already passed!");
		}

		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		
		ArrayList<String> freeSubjects = getFreeSubjects(student, eDao);
		ArrayList<String> individualFreeSubjects = eDao.getStudentFreeProjectMap().get(student.getSapid());
		if(individualFreeSubjects != null && individualFreeSubjects.size() > 0){
			freeSubjects.addAll(individualFreeSubjects);
		}

		if(!eDao.isResultLiveForLastProjectSubmissionCycle()){
			ArrayList<AssignmentFileBean> projectSubmittedButNotProcessed = dao.getResultAwaitedProjectSubmittedList(student.getSapid());
//			ArrayList<ExamBookingExamBean> projectExamBookedInLastCycleNotSubmitted = dao.getProjectExamBookedInLastCycleButNotSubmitted(student.getSapid());
//			ArrayList<ExamBookingExamBean> projectExamBookedInLastCycle = dao.getProjectExamBookedInLastCycle(student.getSapid());
//			if(projectSubmittedButNotProcessed.size()>0 && projectExamBookedInLastCycle.size()>0){
			if(projectSubmittedButNotProcessed.size()>0){
				status.setCanSubmit("N");
				status.setCantSubmitError("Results not live for the previous exam cycle!");
				return;
			}
		}

		int lastSem = -1;
		List<StudentMarksBean> registrationList = eDao.getRegistrations(student.getSapid());
		for (int i = 0; i < registrationList.size(); i++) {
			StudentMarksBean bean = registrationList.get(i);
			int sem = Integer.parseInt(bean.getSem());
			if(sem >= lastSem){
				lastSem = sem;
			}
		}
		
		boolean isPgStudent = student.getProgram().startsWith("PG") && !"Diageo".equalsIgnoreCase(student.getConsumerType()); //Only sem 4 students allowed to submit
		boolean pgSemCheck = lastSem >= 4 && lastSem != -1;
		boolean isPdWmStudent = "PD - WM".equalsIgnoreCase(student.getProgram()) && "Retail".equalsIgnoreCase(student.getConsumerType());
		boolean pdWmSemCheck = lastSem > 1 && lastSem != -1;
		
		if(!isPgStudent && !isPdWmStudent) {
			status.setCantSubmitError("Project Submission not applicable for current registration!");
			status.setCanSubmit("N");
			return;
		}else if(isPgStudent && !pgSemCheck){ 
			status.setCantSubmitError("Project Submission is applicable for Sem 4 Registration only");
			status.setCanSubmit("N");
			return;
		}else if(isPdWmStudent && !pdWmSemCheck){ 
			status.setCantSubmitError("Project Submission is applicable for Sem 2 Registration only");
			status.setCanSubmit("N");
			return;
		}
		
		if(student.getExamMode().equalsIgnoreCase("Offline")){
			status.setCanSubmit("N");
			status.setCantSubmitError("Offine students are not applicable to submit project!");
			return;
		}
		
		status.setCanSubmit("Y");
		ArrayList<String>  examBookedSubjets = eDao.getProjectBookingforCurrentLiveExam(student.getSapid()); //Check if project payment done for current live project submission cycle.
		if(examBookedSubjets.contains(status.getSubject()) || freeSubjects.contains(status.getSubject())){
			status.setPaymentPending("N");
			return;
		} else {
			status.setPaymentPending("Y");
			return;
		}
	}

	public FacultyExamBean getSOPStudentGuideInfo(String examYear, String examMonth, String sapid, String subject) {
		return projectTitleDAO.getSOPGuideNameForStudent(examYear, examMonth, sapid, subject);
	}
	
	public FacultyExamBean getSynopsisStudentGuideInfo(String examYear, String examMonth, String sapid, String subject) {
		return projectTitleDAO.getSynopsisGuideNameForStudent(examYear, examMonth, sapid, subject);
	}
	
	private ArrayList<String> getFreeSubjects(StudentExamBean student, ExamBookingDAO eDao) {
		ArrayList<String> freeSubjects = new ArrayList<>();
		ArrayList<StudentExamBean> exemptStudentList = eDao.getProjectExemptStudentList();
		HashMap<String, String> exemptSAPids = new HashMap<>();
		for (int i = 0; i < exemptStudentList.size(); i++) {
			StudentExamBean bean = exemptStudentList.get(i);
			exemptSAPids.put(bean.getSapid(), bean.getSem());
		}
		if(exemptSAPids.containsKey(student.getSapid())){
			String exemptSem = exemptSAPids.get(student.getSapid());
			ArrayList<ProgramSubjectMappingExamBean> programSubjectMappingList = eDao.getProgramSubjectMappingList();
			for (int i = 0; i < programSubjectMappingList.size(); i++) {
				ProgramSubjectMappingExamBean bean = programSubjectMappingList.get(i);

				if(bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) && bean.getProgram().equals(student.getProgram())
						&& bean.getSem().equals(exemptSem)){
					freeSubjects.add(bean.getSubject());
				}
			}
		}
		return freeSubjects;
	}


	public ProjectModuleStatusBean getSOPInfoForStudent(StudentExamBean student, String pssId, String subject, String examYear, String examMonth, List<ProjectModuleExtensionBean> extensions) throws ParseException {
		ProjectModuleStatusBean bean = new ProjectModuleStatusBean();
		try {
			LevelBasedSOPConfigBean sopConfig = levelBasedProjectService.getSOPConfiguration(examYear, examMonth, pssId);
			bean.setLive(sopConfig.getLive());
			bean.setMaxSubmissions(sopConfig.getMax_attempt());
			bean.setStartDate(sopConfig.getStart_date());
			bean.setEndDate(sopConfig.getEnd_date());
			
			FacultyExamBean faculty = getSOPStudentGuideInfo(examYear, examMonth, student.getSapid(), subject);
			bean.setFacultyId(faculty.getFacultyId());
			bean.setFacultyName(faculty.getFirstName() + " " + faculty.getLastName());
			
			for (ProjectModuleExtensionBean projectModuleExtensionBean : extensions) {
				if("SOP".equals(projectModuleExtensionBean.getModuleType())) {
					bean.setEndDate(projectModuleExtensionBean.getEndDate());
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date();
			Date endDate = sdf.parse(bean.getEndDate());
			Date startDate = sdf.parse(bean.getStartDate());
			boolean hasSubmitted = levelBasedProjectService.getSOPSubmissionStatusForStudentV2(examYear, examMonth, student.getSapid());
			
			if(hasSubmitted) {
				UploadProjectSOPBean sopSubmission = levelBasedProjectService.getSOPSubmissionForStudent(examYear, examMonth, student.getSapid());
				bean.setSubmissionsMade(sopSubmission.getAttempt());
				bean.setReason(sopSubmission.getReason());
				bean.setSubmittedFilePath(sopSubmission.getFilePath());
				bean.setStatus(sopSubmission.getStatus());
			} else {
				bean.setSubmissionsMade(0);
				bean.setReason(null);
			}

			if("Y".equals(bean.getLive()) && curDate.before(endDate) && curDate.after(startDate) && bean.getSubmissionsMade() < bean.getMaxSubmissions()) {
				bean.setAllowSubmission(true);
			} else {
				bean.setAllowSubmission(false);
			}
		}catch (Exception e) {
			
			bean.setError("Error getting SOP details!");
		}
		return bean;
	}
	
	public ProjectModuleStatusBean getSynopsisInfoForStudent(StudentExamBean student, String programSemSubjId, String examYear, String examMonth, List<ProjectModuleExtensionBean> extensions) throws ParseException  {
		ProjectModuleStatusBean bean = new ProjectModuleStatusBean();
		try {
			LevelBasedSynopsisConfigBean synopsisConfig = levelBasedProjectService.getSynopsisConfiguration(examYear, examMonth, programSemSubjId);
			bean.setLive(synopsisConfig.getLive());
			bean.setMaxSubmissions(synopsisConfig.getMax_attempt());
			bean.setStartDate(synopsisConfig.getStart_date());
			bean.setEndDate(synopsisConfig.getEnd_date());
			for (ProjectModuleExtensionBean projectModuleExtensionBean : extensions) {
				if("Synopsis".equals(projectModuleExtensionBean.getModuleType())) {
					bean.setEndDate(projectModuleExtensionBean.getEndDate());
				}
			}

			boolean hasSubmittedSynopsis = levelBasedProjectService.getSynopsisSubmissionStatusForStudent(examYear, examMonth, student.getSapid());
			if(hasSubmittedSynopsis) {
				UploadProjectSynopsisBean synopsisSubmission = levelBasedProjectService.getSynopsisSubmissionForStudent(examYear, examMonth, student.getSapid());
				bean.setSubmissionsMade(synopsisSubmission.getAttempt());
				bean.setReason(synopsisSubmission.getReason());
				bean.setSubmittedFilePath(synopsisSubmission.getFilePath());
				bean.setStatus(synopsisSubmission.getStatus()); 
				bean.setScore(synopsisSubmission.getScore());
			} else {
				bean.setSubmissionsMade(0);
				bean.setReason(null);
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date();
			Date endDate = sdf.parse(bean.getEndDate());
			Date startDate = sdf.parse(bean.getStartDate());
			if("Y".equals(bean.getLive()) && curDate.before(endDate) && curDate.after(startDate) && bean.getSubmissionsMade() < bean.getMaxSubmissions()) {
				bean.setAllowSubmission(true);
			} else {
				bean.setAllowSubmission(false);
			}
		}catch (Exception e) {
			bean.setError("Error getting Synopsis details!");
		}
		return bean;
	}

	public ProjectModuleStatusBean getVivaInfoForStudent(StudentExamBean student, String pssId, String examYear,
			String examMonth, List<ProjectModuleExtensionBean> extensions) {
		ProjectModuleStatusBean bean = new ProjectModuleStatusBean();
		try {
			VivaSlotBookingConfigBean vivaConfig = levelBasedProjectService.getVivaConfiguration(examYear, examMonth, pssId);
			if(vivaConfig!=null ) {
				bean.setLive("Y"); 
				bean.setStartDate(vivaConfig.getStart_date());
				bean.setEndDate(vivaConfig.getEnd_date());
			}
			
			for (ProjectModuleExtensionBean projectModuleExtensionBean : extensions) {
				if("Viva".equals(projectModuleExtensionBean.getModuleType())) {
					bean.setEndDate(projectModuleExtensionBean.getEndDate());
				}
			}
 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date();
			Date endDate = sdf.parse(bean.getEndDate());
			Date startDate = sdf.parse(bean.getStartDate());  
		}catch (Exception e) {
			bean.setError("Error getting Viva details!");
		}
		return bean;
	}
	
	public String getProjectApplicableProgramSem(String program) {
		String sem = null;
		if("CPBM".equalsIgnoreCase(program) || "PD - WM".equalsIgnoreCase(program)) {
			sem = "2";
		}else if(SEM_5_PROJECT_APPLICABLE_PROGRAM_LIST.contains(program)) {
			sem = "5";
		}else if(SEM_6_PROJECT_APPLICABLE_PROGRAM_LIST.contains(program)) {
			sem = "6";
		}else {
			sem = "4";
		}
		return sem;
	}

	public AssignmentFileBean getStudentApplicableExamMonthYear(String sapId, String subject, String method) {
		AssignmentFileBean bean = new AssignmentFileBean();
		StudentExamBean registration = projectSubmissionDAO.getRecentRegisterationByStudent(sapId);
		boolean freshStudent = false;
		if(registration.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR) && registration.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH)) {
			freshStudent=true;
		}
		boolean isFail = false;
		isFail = levelBasedProjectService.checkIfStudentPassProject(sapId,subject,"N");
//		if(freshStudent && !isFail) {
//				bean.setYear(projectSubmissionDAO.getLiveProjectExamYear());
//				bean.setMonth(projectSubmissionDAO.getLiveProjectExamMonth());
//		}else {
//			try {
//				ExamOrderExamBean examOrderBean = getLastCycleProjectLiveDetail();
//				bean.setYear(examOrderBean.getYear());
//				bean.setMonth(examOrderBean.getMonth());
//			} catch (Exception e) {
//				// if exception occur then set current project submission live exam month
//				bean.setYear(projectSubmissionDAO.getLiveProjectExamYear());
//				bean.setMonth(projectSubmissionDAO.getLiveProjectExamMonth());
//				projectSubmissionLogger.info("Exception Error getLastCycleProjectLiveDetail() Sapid:{} Error:{}", sapId, e);
//				//e.printStackTrace();
//			}
//		}
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ArrayList<String> timeExtendedStudentIdSubjectList =  dao.assignmentExtendedSubmissionTime();
		// For temporary set current live project submission exam cycle for normal as well as extended students
//		if(timeExtendedStudentIdSubjectList.contains(sapId+subject)) {
//			ExamOrderExamBean examOrderBean = getLastCycleProjectLiveDetail();
//			bean.setYear(examOrderBean.getYear());
//			bean.setMonth(examOrderBean.getMonth());
//		}else {
			bean.setYear(projectSubmissionDAO.getLiveProjectExamYear());
			bean.setMonth(projectSubmissionDAO.getLiveProjectExamMonth());
//		}
		
		projectSubmissionLogger.info(" Sapid:{} isFreshStudent >>{} isFail>>{} Exam cycle:{}-{} Called By Method:{}", sapId, freshStudent, isFail, bean.getMonth(), bean.getYear(), method);
		return bean;
	}
	
	public ExamOrderExamBean getLastCycleProjectLiveDetail() {
		// TODO Auto-generated method stub
		return projectSubmissionDAO.getLastCycleProjectLiveDetail();
	}

	public List<ExamBookingTransactionBean> getConfirmedProjectBookingApplicableCycle(String sapid, String month,String year) {
		return examBookingDAO.getConfirmedProjectBookingApplicableCycle(sapid, month, year);
	}

	public List<AssignmentFileBean> getProjectSubmission(final String subject, final String month, final String year, final String sapId) throws NullPointerException, SQLException{
		return projectSubmissionDAO.getProjectSubmission(month, year , subject, sapId);
	}
}
