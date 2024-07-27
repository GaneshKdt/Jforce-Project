package com.nmims.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.LevelBasedProjectBean;
import com.nmims.beans.LevelBasedSOPConfigBean;
import com.nmims.beans.LevelBasedSynopsisConfigBean;
import com.nmims.beans.PaymentGatewayTransactionBean;
import com.nmims.beans.ProgramSubjectMappingExamBean;
import com.nmims.beans.ProjectConfiguration;
import com.nmims.beans.ProjectModuleExtensionBean;
import com.nmims.beans.ProjectModuleStatusBean;
import com.nmims.beans.ProjectStudentStatus;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.UploadProjectSOPBean;
import com.nmims.beans.UploadProjectSynopsisBean;
import com.nmims.beans.VivaSlotBean;
import com.nmims.beans.VivaSlotBookingConfigBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.LevelBasedProjectDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.daos.ProjectTitleDAO;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.LevelBasedProjectHelper;
import com.nmims.services.LevelBasedProjectService;
import com.nmims.services.ProjectStudentEligibilityService;
import com.nmims.views.PDWMProjectEligibleStudentsRequestExcelView;
import com.nmims.views.SOPSubmittedListExcelView;
import com.nmims.views.SynopsisSubmittedListExcelView;

@Controller
public class LevelBasedProjectController extends BaseController {
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	LevelBasedProjectHelper levelBasedProjectHelper;
	
	@Autowired
	LevelBasedProjectService levelBasedProjectService;
	
	@Autowired
	ProjectStudentEligibilityService eligibilityService;
	@Autowired
	LevelBasedProjectDAO levelBasedProjectDAO ;
	
	@Autowired
	PDWMProjectEligibleStudentsRequestExcelView PDWMProjectEligibleStudentsRequestExcelView;
	
	@Autowired
	SynopsisSubmittedListExcelView SynopsisSubmittedListExcelView;
	
	@Autowired
	AmazonS3Helper amazonS3Helper;
	
	@Value( "${SUBMITTED_SOP_FILES_PATH}" )
	private String SUBMITTED_SOP_FILES_PATH;
	
	@Value( "${SOP_PAYMENT_RETURN_URL}" )
	private String SOP_PAYMENT_RETURN_URL;
	
	@Value( "${SYNOPSIS_PAYMENT_RETURN_URL}" )
	private String SYNOPSIS_PAYMENT_RETURN_URL;
	
	@Value( "${SUBMITTED_SYNOPSIS_FILES_PATH}" )
	private String SUBMITTED_SYNOPSIS_FILES_PATH;
	
	@Value("${SYNOPSIS_FILES_PATH}")
	private String SYNOPSIS_FILES_PATH;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	
	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> EXAM_YEAR_LIST;

	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}")
	private List<String> EXAM_MONTH_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;
	
	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private List<String> CURRENT_YEAR_LIST;
	
	@Autowired
	ProjectTitleDAO projectTitleDAO;
	
	private final String subjectName = "Level Based Project";
	
	private final int MAX_FILE_SIZE = 5242880;
	private static final Logger logger = LoggerFactory.getLogger(LevelBasedProjectController.class);
	private static final Logger projectSubmissionLogger = LoggerFactory.getLogger("projectSubmission");
	private final List<String> SOP_STATUS = Arrays.asList("Payment pending","Payment failed","Submitted","Rejected","Approved");
	private final List<String> SOP_TRANSACTION_STATUS = Arrays.asList("Initiated","Payment Successfull","Payment failed","Expired");
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH_URL;
	
	@RequestMapping(value = "/student/guidedProjectSubmissionSummary", method = {RequestMethod.GET})
	public ModelAndView guidedProjectSubmissionSummary(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean inputBean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("project/student/guidedProjectSubmissionSummary");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");
		projectSubmissionLogger.info("Pg visit /guidedProjectSubmissionSummary Sapid - {}",student.getSapid());
		if(student.getExamMode().equalsIgnoreCase("Offline")){
			setError(request, "You are not eligible for Project Submission!");
			return mv;
		}
		
		PassFailDAO passFailDAO = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<String> passedSubjects = passFailDAO.getPassSubjectsNamesForAStudent(student.getSapid());
		if(passedSubjects.contains(inputBean.getSubject())) {
			setError(request, "Subject already passed!,You are not eligible for Project Submission!");
			return mv;
		}
		
		String examMonth = null;
		String examYear = null;
//		LevelBasedProjectBean recentMapping = null;
//		recentMapping = levelBasedProjectService.getRecentStudentGuideMapping(student.getSapid());
//		if(recentMapping != null) {
//			examMonth = recentMapping.getMonth();
//			examYear = recentMapping.getYear();
//		}else {
			String method = "guidedProjectSubmissionSummary()";
			// Set Student Applicable Exam Mont Year
			AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(student.getSapid(), inputBean.getSubject(), method);
			examMonth = examMonthYearBean.getMonth();
			examYear = examMonthYearBean.getYear();
//		}
		String pssId = levelBasedProjectService.getProgramSemSubjectIdForStudent(student.getSapid(), inputBean.getSubject());
		ProjectConfiguration projectConfiguration = null;
		try {
			projectConfiguration = levelBasedProjectService.getSingleLevelBasedProjectConfiguration(examYear, examMonth, pssId);
			request.getSession().setAttribute("projectConfiguration", projectConfiguration);
		}catch (Exception e) {
			// TODO: handle exception
			setError(request, "Project Configuration not found for given subject");
			return mv;
		}
		
		ProjectStudentStatus status = new ProjectStudentStatus();
		status.setSapid(student.getSapid());
		status.setSubject(inputBean.getSubject());
		status.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		status.setProgramSemSubjId(pssId);
		status.setExamMonth(examMonth);
		status.setExamYear(examYear);
		
		//eligibilityService.getPaymentInfoForStudent(student, status);
		
		/*if(status.getCanSubmit().equals("N")) {
			setError(request, status.getCantSubmitError());
			return mv;
		}*/
		try {
			//ProjectConfiguration config = levelBasedProjectService.getProjectConfigurationForSubjectId(examMonth, examYear, pssId);
			status.setHasSOP(projectConfiguration.getHasSOP());
			status.setHasSynopsis(projectConfiguration.getHasSynopsis());
			status.setHasSubmission(projectConfiguration.getHasSubmission());
			status.setHasTitle(projectConfiguration.getHasTitle());
			status.setHasViva(projectConfiguration.getHasViva());
			
			List<ProjectModuleExtensionBean> extensions = levelBasedProjectService.getProjectExtensionsListForStudent(examMonth, examYear, student.getSapid(), pssId);
			if("Y".equals(projectConfiguration.getHasSOP())) {
				ProjectModuleStatusBean sopStatus = eligibilityService.getSOPInfoForStudent(student, pssId, inputBean.getSubject(), examYear, examMonth, extensions);	
				status.setSopStatus(sopStatus);	
			} else {
				ProjectModuleStatusBean sopStatus = new ProjectModuleStatusBean();
				sopStatus.setError("Not Eligible!");
				status.setSopStatus(sopStatus);	
			}
			
			if("Y".equals(projectConfiguration.getHasSynopsis())) {
				ProjectModuleStatusBean synopsisStatus = eligibilityService.getSynopsisInfoForStudent(student, pssId, examYear, examMonth, extensions);	
				status.setSynopsisStatus(synopsisStatus);
			} else {
				ProjectModuleStatusBean synopsisStatus = new ProjectModuleStatusBean();
				synopsisStatus.setError("Not Eligible!");
				status.setSynopsisStatus(synopsisStatus);
			}
			
			if("Y".equalsIgnoreCase(projectConfiguration.getHasSubmission())) {
				ProjectModuleStatusBean projectSubmissionStatus = new ProjectModuleStatusBean();
				projectSubmissionStatus.setAllowSubmission(false);
				
				if("Y".equals(projectConfiguration.getHasSynopsis())) {
					AssignmentFileBean assignmentFileBean = new AssignmentFileBean();
					assignmentFileBean.setSubject(inputBean.getSubject());
					assignmentFileBean.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
					assignmentFileBean.setMonth(status.getExamMonth());
					assignmentFileBean.setYear(status.getExamYear());
					ArrayList<String> applicableStudentIdSubjectList =  projectSubmissionDAO.getProjectApplicableStudentList(examYear,examMonth);
					if(applicableStudentIdSubjectList.contains(student.getSapid() + "" + inputBean.getSubject())) {
						//AssignmentFileBean assignmentFileBean2 = projectSubmissionDAO.findProjectGuidelinesForApplicableCycle(assignmentFileBean);
						AssignmentFileBean assignmentFileBean2 = projectSubmissionDAO.findPDWMById(assignmentFileBean);
						if(assignmentFileBean2 != null) {
							assignmentFileBean2.setEndDate(assignmentFileBean2.getEndDate().replaceFirst("T", " "));
							projectSubmissionStatus.setEndDate(assignmentFileBean2.getEndDate());
							projectSubmissionStatus.setAllowSubmission(true);	
						}	
					}
				}else {
					response.sendRedirect(SERVER_PATH_URL+"exam/student/viewProject?subject="+ inputBean.getSubject());
				}
				
				status.setSubmissionStatus(projectSubmissionStatus);
			}else {
				ProjectModuleStatusBean projectSubmissionStatus = new ProjectModuleStatusBean();
				projectSubmissionStatus.setAllowSubmission(false);
				status.setSubmissionStatus(projectSubmissionStatus);
			}
			
			// Viva and title submission are currently kept inactive.
			if("Y".equals(projectConfiguration.getHasViva())) {
				ProjectModuleStatusBean vivaStatus = eligibilityService.getVivaInfoForStudent(student, pssId, examYear, examMonth, extensions);	
				status.setVivaStatus(vivaStatus);
				/*
				 * ProjectModuleStatusBean vivaStatus = new ProjectModuleStatusBean();
				 * vivaStatus.
				 * setError("Invalid Configuration detected! Kindly contact Administrator!");
				 * status.setVivaStatus(vivaStatus);
				 */
			} else {
				ProjectModuleStatusBean vivaStatus = new ProjectModuleStatusBean();
				vivaStatus.setError("Not Eligible!");
				status.setVivaStatus(vivaStatus);
			}

			if("Y".equals(projectConfiguration.getHasTitle())) {
				ProjectModuleStatusBean titleStatus = new ProjectModuleStatusBean();
				titleStatus.setError("Invalid Configuration detected! Kindly contact Administrator!");
				status.setTitleStatus(titleStatus);
			} else {
				ProjectModuleStatusBean titleStatus = new ProjectModuleStatusBean();
				titleStatus.setError("Not Eligible!");
				status.setTitleStatus(titleStatus);
			}
		}catch (Exception e) {
			projectSubmissionLogger.info("Exception Error guidedProjectSubmissionSummary() Sapid:{} Error:{}", student.getSapid(), e);
			mv.addObject("status", status);
			mv.addObject("error", "true");
			mv.addObject("errorMessage", "Error getting Project configuration");
			return mv;
		}
		mv.addObject("status", status);
		return mv;
	}
	
	
	@RequestMapping(value = "/student/uploadProjectSynopsisForm", method = {RequestMethod.GET})
	public ModelAndView uploadProjectSynopsisForm(HttpServletRequest request,@RequestParam String subject) {
		UploadProjectSynopsisBean uploadSynopsisBean = new UploadProjectSynopsisBean();
		ModelAndView mv = new ModelAndView("uploadProjectSynopsis");
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		
		LevelBasedProjectBean recentMapping = levelBasedProjectService.getRecentStudentGuideMapping(student.getSapid());
//		StudentExamBean resitMonthYear = levelBasedProjectService.getProjectConfigurationMonthYear();
//		String projectConfigMonth = resitMonthYear.getMonth();
//		String examYear = recentMapping.getYear();
//		String examMonth = recentMapping.getMonth();
//		if(levelBasedProjectService.isResitCycleMonth(projectConfigMonth)) {
//			examYear = resitMonthYear.getYear();
//			examMonth = resitMonthYear.getMonth();
//		}
		String examMonth = null;
		String examYear = null;
		if(recentMapping != null) {
			examMonth = recentMapping.getMonth();
			examYear = recentMapping.getYear();
		}else {
			String method = "uploadProjectSynopsisForm()";
			// Set Student Applicable Exam Mont Year
			AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(student.getSapid(), "Module 4 - Project" , method);
			examMonth = examMonthYearBean.getMonth();
			examYear = examMonthYearBean.getYear();
		}
		int pss_id = levelBasedProjectDAO.getProgramSemSubjectId(subject, student.getConsumerProgramStructureId());
		if(pss_id <= 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "PSS id not found");
			return mv;
		}
		LevelBasedSynopsisConfigBean levelBasedSynopsisConfigBean = levelBasedProjectDAO.getSynopsisConfigBeanBasedOnMasterKey(pss_id + "",examYear,examMonth);
		mv.addObject("levelBasedSynopsisConfigBean", levelBasedSynopsisConfigBean);
		if(levelBasedSynopsisConfigBean == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Level Based project synopsis submission is not live");
			return mv;
		}
		 
		levelBasedSynopsisConfigBean.setSubject(uploadSynopsisBean.getSubject());
		uploadSynopsisBean.setYear(examYear);
		uploadSynopsisBean.setMonth(examMonth);
		uploadSynopsisBean.setSapid(student.getSapid());
		uploadSynopsisBean.setSubject(subject);
		
		
		String facultyName = levelBasedProjectService.getSOPGuideNameForStudent(examYear, examMonth, student.getSapid(), uploadSynopsisBean.getSubject());
		
		try {
			boolean hasSubmittedPreviously = levelBasedProjectDAO.checkSynopsisSubmissionCount(uploadSynopsisBean, SOP_STATUS);
			if(!hasSubmittedPreviously) {
				UploadProjectSynopsisBean resultUploadProjectBean = new UploadProjectSynopsisBean();
				resultUploadProjectBean.setFacultyName(facultyName);
				resultUploadProjectBean.setSubject(uploadSynopsisBean.getSubject());
				mv.addObject("canSubmit", true);
				mv.addObject("resultUploadProjectBean", resultUploadProjectBean);
			} else {
				UploadProjectSynopsisBean resultUploadProjectBean = levelBasedProjectDAO.getLastSubmittedSynopsis(uploadSynopsisBean, SOP_STATUS);
				resultUploadProjectBean.setFacultyName(facultyName);
				resultUploadProjectBean.setSubject(uploadSynopsisBean.getSubject());
				if(SOP_STATUS.get(2).equalsIgnoreCase(resultUploadProjectBean.getStatus())) {
					mv.addObject("submitted", true);
				}else {
					mv.addObject("submitted", false);
				}
				if(resultUploadProjectBean.getAttempt() >= levelBasedSynopsisConfigBean.getMax_attempt()) {
					mv.addObject("canSubmit", false);
				}else {
					mv.addObject("canSubmit", true);
				}
				mv.addObject("resultUploadProjectBean", resultUploadProjectBean);
			}
		}catch (Exception e) {
			logger.info("Error getting submission info!"+e);
			setError(request, "Error getting submission info!");
			mv.addObject("submitted", false);
		}
		
		return mv;
	}
	
	@RequestMapping(value = "/student/uploadProjectSynopsis", method = {RequestMethod.POST},headers = "content-type=multipart/form-data")
	public ModelAndView uploadProjectSynopsis(HttpServletRequest request,@ModelAttribute UploadProjectSynopsisBean uploadProjectSynopsisBean) {
		ModelAndView mv = new ModelAndView("uploadProjectSynopsis");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		String subject = uploadProjectSynopsisBean.getSubject();
		try {
			ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
			LevelBasedProjectBean recentMapping = levelBasedProjectService.getRecentStudentGuideMapping(student.getSapid());
			String examMonth = null;
			String examYear = null;
			if(recentMapping != null) {
				examMonth = recentMapping.getMonth();
				examYear = recentMapping.getYear();
			}else {
				String method = "uploadProjectSynopsis()";
				// Set Student Applicable Exam Mont Year
				AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(student.getSapid(), "Module 4 - Project" , method);
				examMonth = examMonthYearBean.getMonth();
				examYear = examMonthYearBean.getYear();
			}
//			StudentExamBean resitMonthYear = levelBasedProjectService.getProjectConfigurationMonthYear();
//			String projectConfigMonth = resitMonthYear.getMonth();
//			String examYear = recentMapping.getYear();
//			String examMonth = recentMapping.getMonth();
//			if(levelBasedProjectService.isResitCycleMonth(projectConfigMonth)) {
//				examYear = resitMonthYear.getYear();
//				examMonth = resitMonthYear.getMonth();
//			}
			uploadProjectSynopsisBean.setYear(examYear);
			uploadProjectSynopsisBean.setMonth(examMonth);
			uploadProjectSynopsisBean.setSapid(student.getSapid());
			uploadProjectSynopsisBean.setCreated_by(student.getSapid());
			uploadProjectSynopsisBean.setUpdated_by(student.getSapid());
			String pssId = levelBasedProjectService.getProgramSemSubjectIdForStudent(student.getSapid(), uploadProjectSynopsisBean.getSubject());
			LevelBasedSynopsisConfigBean levelBasedSOPConfigBean = levelBasedProjectService.getSynopsisConfiguration(examYear, examMonth, pssId);
			List<ProjectModuleExtensionBean> extensions = levelBasedProjectService.getProjectExtensionsListForStudent(examMonth, examYear, student.getSapid(), pssId);
			if(extensions.size()>0) {
			for (ProjectModuleExtensionBean projectModuleExtensionBean : extensions) {
				if("Synopsis".equals(projectModuleExtensionBean.getModuleType())) {
					levelBasedSOPConfigBean.setEnd_date(projectModuleExtensionBean.getEndDate());
				}
			}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date();
			Date endDate;
			try {
				endDate = sdf.parse(levelBasedSOPConfigBean.getEnd_date());
				if("N".equals(levelBasedSOPConfigBean.getLive()) || endDate.before(curDate)) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error Submitting Synopsis : Submission Window Inactive!");
					uploadProjectSynopsisBean.setSubject(subject);
					return uploadProjectSynopsisForm(request, subject);
				}
			} catch (ParseException e1) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error : " + e1.getMessage());
				uploadProjectSynopsisBean.setSubject(subject);
				return uploadProjectSynopsisForm(request, subject);
			}
			
			String trackId = student.getSapid() + System.currentTimeMillis();
			uploadProjectSynopsisBean.setTrack_id(trackId);
			if(uploadProjectSynopsisBean == null || uploadProjectSynopsisBean.getFileData() == null) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload Inner: No File Selected");
				return mv;
			}
			
			long fileSizeInBytes = uploadProjectSynopsisBean.getFileData().getSize();
			if(fileSizeInBytes > MAX_FILE_SIZE){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "file is larger than excepted, please upload less than " + MAX_FILE_SIZE + " size");
				return mv;
			}
			CommonsMultipartFile file = uploadProjectSynopsisBean.getFileData();
			String fileName = file.getOriginalFilename();
			
			if(!(fileName.toUpperCase().endsWith(".PDF") ) ){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Only PDF file allowed,please convert your file to PDF file format");
				return mv;
			}
			/*InputStream inputStream = null;   
			OutputStream outputStream = null; 
			InputStream tempInputStream = file.getInputStream();
			byte[] initialbytes = new byte[4];   
			tempInputStream.read(initialbytes);
			tempInputStream.close();
			inputStream = file.getInputStream();
			
			String filePath = SUBMITTED_SYNOPSIS_FILES_PATH + uploadProjectSynopsisBean.getMonth() + uploadProjectSynopsisBean.getYear() + "/" + fileName;
			
			File folderPath = new File(SUBMITTED_SYNOPSIS_FILES_PATH  + uploadProjectSynopsisBean.getMonth() + uploadProjectSynopsisBean.getYear());
			if (!folderPath.exists()) {
				folderPath.mkdirs();
			}
			File newFile = new File(filePath);   
			
			
			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   
			
			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}
			
			outputStream.close();
			inputStream.close();*/
			
			String year = uploadProjectSynopsisBean.getYear();
			String month= uploadProjectSynopsisBean.getMonth();
			fileName = uploadProjectSynopsisBean.getSapid() + "_synopsis_" + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
			String folderPath ="Synopsis/submissions/"+month + year + "/";
			uploadProjectSynopsisBean.setFilePath(month + year + "/" + fileName);

			HashMap<String,String> s3_response = amazonS3Helper.uploadFiles(file,folderPath,"guidedprojectcontent",folderPath+fileName);
			
			if(!s3_response.get("status").equalsIgnoreCase("success")) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Error in uploading file "+s3_response.get("fileUrl"));
				return mv;
			}
			
			
			LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
			
			
			String previewPath = uploadProjectSynopsisBean.getMonth() + uploadProjectSynopsisBean.getYear() + "/" + fileName;
			uploadProjectSynopsisBean.setPreviewPath(previewPath);			
			LevelBasedSynopsisConfigBean levelBasedSynopsisConfigBean = levelBasedProjectDAO.getSynopsisConfigBeanBasedOnMasterKey(pssId,examYear,examMonth);

			UploadProjectSynopsisBean resultUploadProjectBean;
			boolean hasSubmittedPreviously = levelBasedProjectDAO.checkSynopsisSubmissionCount(uploadProjectSynopsisBean, SOP_STATUS);
			if(!hasSubmittedPreviously) {
				resultUploadProjectBean = new UploadProjectSynopsisBean();
				resultUploadProjectBean.setAttempt(0);
				mv.addObject("canSubmit", true);
				mv.addObject("resultUploadProjectBean", resultUploadProjectBean);
			} else {
				resultUploadProjectBean = levelBasedProjectDAO.getLastSubmittedSynopsis(uploadProjectSynopsisBean, SOP_STATUS);
			}
			
			if(resultUploadProjectBean.getAttempt() >= levelBasedSynopsisConfigBean.getMax_attempt()) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Max limit extended");
				uploadProjectSynopsisBean.setSubject(subject);
				return uploadProjectSynopsisForm(request, subject);
			}

			LevelBasedProjectBean levelBasedProjectBean = levelBasedProjectDAO.getStudentGuide(student.getSapid(),year,month);
			uploadProjectSynopsisBean.setFacultyId(levelBasedProjectBean.getFacultyId());
			if(levelBasedSynopsisConfigBean.getPayment_applicable().equals("Y")) {
				//payment form
//				uploadProjectSynopsisBean.setPayment_status(SOP_TRANSACTION_STATUS.get(0));
//				uploadProjectSynopsisBean.setStatus(SOP_STATUS.get(0));
//				
//				if(levelBasedProjectDAO.insertSynopsisRecord(uploadProjectSynopsisBean)) {
//					ModelAndView payment_mv = new ModelAndView("payment");
//					
//					request.getSession().setAttribute("track_id", trackId);
//					payment_mv.addObject("track_id", trackId);
//					payment_mv.addObject("sapid", student.getSapid());
//					payment_mv.addObject("type", "Synopsis");
//					payment_mv.addObject("amount", levelBasedSynopsisConfigBean.getPayment_amount());
//					payment_mv.addObject("description", "Synopsis Fees for " + student.getSapid());
//					payment_mv.addObject("portal_return_url",SYNOPSIS_PAYMENT_RETURN_URL );
//					payment_mv.addObject("created_by", student.getSapid());
//					payment_mv.addObject("updated_by", student.getSapid());
//					payment_mv.addObject("mobile", student.getMobile());
//					payment_mv.addObject("email_id", student.getEmailId());
//					payment_mv.addObject("first_name", student.getFirstName());
//					payment_mv.addObject("source", "web");
//					return payment_mv;
//				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Failed to initiate payment");
					uploadProjectSynopsisBean.setSubject(subject);
					return uploadProjectSynopsisForm(request, subject);
//				}
				
			}else {
				uploadProjectSynopsisBean.setPayment_status(SOP_TRANSACTION_STATUS.get(1));
				uploadProjectSynopsisBean.setStatus(SOP_STATUS.get(2));
				try {
					uploadProjectSynopsisBean.setAttempt(uploadProjectSynopsisBean.getAttempt() + 1);
					levelBasedProjectDAO.insertSynopsisRecordToHistory(uploadProjectSynopsisBean);
					levelBasedProjectDAO.insertSynopsisRecord(uploadProjectSynopsisBean);
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Successfully submitted Synopsis");
				} catch (Exception e) {
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Failed to submit Synopsis file");
				}
				uploadProjectSynopsisBean.setSubject(subject);
				return uploadProjectSynopsisForm(request, subject);
			}
		} catch (Exception e) {
			// TODO: handle exception
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error : " + e.getMessage());

			uploadProjectSynopsisBean.setSubject(subject);
			return uploadProjectSynopsisForm(request, subject);
		}
	}
	
	
	
	@RequestMapping(value = "/student/synopsisPaymentResponse", method = {RequestMethod.GET})
	public ModelAndView synopsisPaymentResponse(HttpServletRequest request,@ModelAttribute UploadProjectSynopsisBean uploadProjectSynopsisBean) {
		ModelAndView mv = new ModelAndView("uploadProjectSynopsis");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		String examMonth = null;
		String examYear = null;
		
		LevelBasedProjectBean recentMapping = levelBasedProjectService.getRecentStudentGuideMapping(student.getSapid());
//		StudentExamBean resitMonthYear = levelBasedProjectService.getProjectConfigurationMonthYear();
		if(recentMapping != null) {
			examMonth = recentMapping.getMonth();
			examYear = recentMapping.getYear();
		}else {
			String method = "synopsisPaymentResponse()";
			// Set Student Applicable Exam Mont Year
			AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(student.getSapid(), "Module 4 - Project" , method);
			examMonth = examMonthYearBean.getMonth();
			examYear = examMonthYearBean.getYear();
		}
//		String projectConfigMonth = resitMonthYear.getMonth();
//		if(levelBasedProjectService.isResitCycleMonth(projectConfigMonth)) {
//			examYear = resitMonthYear.getYear();
//			examMonth = resitMonthYear.getMonth();
//		}
		uploadProjectSynopsisBean.setSapid(student.getSapid());
		uploadProjectSynopsisBean.setYear(examYear);
		uploadProjectSynopsisBean.setMonth(examMonth);
		uploadProjectSynopsisBean.setTrack_id((String) request.getSession().getAttribute("track_id"));
		String trackId = request.getParameter("trackId");
		String status = request.getParameter("status");
		String message = request.getParameter("message");
		if("success".equalsIgnoreCase(status) && trackId.equalsIgnoreCase(uploadProjectSynopsisBean.getTrack_id())) {
			uploadProjectSynopsisBean.setStatus(SOP_STATUS.get(2));
			uploadProjectSynopsisBean.setPayment_status(SOP_TRANSACTION_STATUS.get(1));
			String updateStatus = levelBasedProjectDAO.updateSynopsisTransactionStatus(uploadProjectSynopsisBean);
			if("true".equalsIgnoreCase(updateStatus)) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Successfully payment completed");
			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",updateStatus);
			}
			
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to complete payment process, Error: " + message);
		}
		return uploadProjectSynopsisForm(request, uploadProjectSynopsisBean.getSubject());
	}
	
	@RequestMapping(value="/admin/levelBasedSynopsisConfigDelete", method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json; charset=UTF-8")
	public ResponseEntity<Map<String, String>> levelBasedSynopsisConfigDelete(HttpServletRequest request,HttpServletResponse response,@ModelAttribute LevelBasedSynopsisConfigBean levelBasedSynopsisConfigBean) {

		Map<String, String> res = new HashMap<String, String>();
		
		if(!checkSession(request, response)){
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
		}
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
		String result = levelBasedProjectDAO.deleteSynopsisByYearMonthAndPssId(levelBasedSynopsisConfigBean);

		res.put("message", result);
		res.put("status", StringUtils.isBlank(result) ? "success" : "fail");

		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/levelBasedSynopsisConfigForm", method = {RequestMethod.GET})
	public ModelAndView levelBasedSynopsisConfigForm(HttpServletRequest request,HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");
		ModelAndView mv = new ModelAndView("project/synopsis/level_based_synopsis_config");
		mv.addObject("EXAM_MONTH_LIST", EXAM_MONTH_LIST);
		mv.addObject("EXAM_YEAR_LIST", EXAM_YEAR_LIST);
		mv.addObject("consumerType", dao.getConsumerTypeList());
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
		mv.addObject("levelBasedSynopsisConfigBeansList", levelBasedProjectDAO.getLevelBasedSynopsisConfigList());
		
		return mv;
	}
	
	@RequestMapping(value = "/admin/levelBasedSynopsisConfig", method = {RequestMethod.POST},headers = "content-type=multipart/form-data")
	public ModelAndView levelBasedSynopsisConfig(HttpServletRequest request,HttpServletResponse response,@ModelAttribute LevelBasedSynopsisConfigBean levelBasedSynopsisConfigBean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String errorMessage = null;
		try {
			if(levelBasedSynopsisConfigBean == null || levelBasedSynopsisConfigBean.getFileData() == null) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload: No File Selected");
				levelBasedSynopsisConfigForm(request,response);
			}
			CommonsMultipartFile file = levelBasedSynopsisConfigBean.getFileData();
			String fileName = file.getOriginalFilename();
			if(!(fileName.toUpperCase().endsWith(".PDF") ) ){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Only PDF file allowed,please convert your file to PDF file format");
				return levelBasedSynopsisConfigForm(request,response);
			}
			
			fileName = levelBasedSynopsisConfigBean.getSubject() + "_synopsis_" + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
			String month=levelBasedSynopsisConfigBean.getMonth();
			String year= levelBasedSynopsisConfigBean.getYear() ;
			String folderPath = "Synopsis/"+month + year + "/";
			
			levelBasedSynopsisConfigBean.setQuestion_filePath(month + year + "/" + fileName);
			levelBasedSynopsisConfigBean.setQuestion_previewPath(month + year + "/" + fileName);
			
			HashMap<String,String> s3_response = amazonS3Helper.uploadFiles(file,folderPath,"guidedprojectcontent",folderPath+fileName);
			if(!s3_response.get("status").equalsIgnoreCase("success")) {
			errorMessage =  "Error in uploading file "+s3_response.get("fileUrl"); 
			}
			if(errorMessage!=null) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",errorMessage); 
				return levelBasedSynopsisConfigForm(request,response);
			}
				
			LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
			
			String userId = (String)request.getSession().getAttribute("userId");
			
			ArrayList<Integer> programSemSubjectIds = levelBasedProjectDAO.getProgramStructureIdBasedOnMasterKeyAndSubject(levelBasedSynopsisConfigBean.getProgram(),levelBasedSynopsisConfigBean.getProgram_structure(),levelBasedSynopsisConfigBean.getConsumer_type(),levelBasedSynopsisConfigBean.getSubject());
			levelBasedSynopsisConfigBean.setCreated_by(userId);
			levelBasedSynopsisConfigBean.setUpdated_by(userId);
			if(programSemSubjectIds.size() <= 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Zero pssId found please select valid field");
				return levelBasedSynopsisConfigForm(request,response);
			}
			HashMap<String,ArrayList<LevelBasedSynopsisConfigBean>> responseHashMap = levelBasedProjectDAO.insertIntoSynopsisConfig(programSemSubjectIds, levelBasedSynopsisConfigBean);
			if(responseHashMap == null) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error: Failed to insert into database");
				return levelBasedSynopsisConfigForm(request,response);
			}
			if(responseHashMap.get("error").size() > 0) {
				request.setAttribute("error","true");
				request.setAttribute("errorMessage","Error Record: " + responseHashMap.get("error").size() + " | Total record: " + programSemSubjectIds.size());
				request.setAttribute("errorTableFlag", "true");
				request.setAttribute("errorTable", responseHashMap.get("error"));
			}
			if(responseHashMap.get("success").size() > 0) {
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Successfully inserted record: " + responseHashMap.get("success").size() + " | Total record: " + programSemSubjectIds.size());
			}
			if(responseHashMap.get("success").size() == 0 && responseHashMap.get("error").size() == 0) {
				request.setAttribute("error","true");
				request.setAttribute("errorMessage","Error: No success and No error record found");
			}
			return levelBasedSynopsisConfigForm(request,response);
		}
		catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error: " + e.getMessage());
			
			return levelBasedSynopsisConfigForm(request,response);
		}
	}
	
	@RequestMapping(value = "/student/vivaSlotBookingForm", method = {RequestMethod.GET})
	public ModelAndView vivaSlotBookingForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("bookSlotVIVA");
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		VivaSlotBean vivaSlotExist = levelBasedProjectDAO.vivaSlotAlreadyBooked(student.getSapid(), eDao.getLiveProjectExamYear(), eDao.getLiveProjectExamMonth());
		String enableAttendButton = "false";
		if(vivaSlotExist == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to check slot booking");
			return mv;
		}
		if(vivaSlotExist.getViva_slots_id() != null) {
			request.setAttribute("alreadyBooked", "true");
			mv.addObject("alreadyBookedBean", vivaSlotExist);
			try {
				Date sessionDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(vivaSlotExist.getStart_time());
				long minutesToSession = getDateDiff(new Date(), sessionDateTime, TimeUnit.MINUTES);
				if (minutesToSession < 30 && minutesToSession > -60) {
					enableAttendButton = "true";
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				
			}
			
			mv.addObject("enableAttendButton", enableAttendButton);
			
			return mv;
		} 
		String subjectName="Module 4 - Project";  
		int pss_id = levelBasedProjectDAO.getProgramSemSubjectId(subjectName, student.getConsumerProgramStructureId());
		if(pss_id <= 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "PSS id not found");
			return mv;
		}
		VivaSlotBookingConfigBean vivaSlotBookingConfigBean = levelBasedProjectDAO.getVivaSlotBookingConfig(pss_id + "", eDao.getLiveProjectExamYear(), eDao.getLiveProjectExamMonth());
		if(vivaSlotBookingConfigBean == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Viva booking is not live");
			return mv;
		}
		String trackId = student.getSapid() + System.currentTimeMillis();	//generate trackId
		List<VivaSlotBean> remainingSlotsDate = levelBasedProjectDAO.getVivaSlots(eDao.getLiveProjectExamYear(), eDao.getLiveProjectExamMonth());
		mv.addObject("remainingSlotsDate", remainingSlotsDate);
		mv.addObject("trackId", trackId);
		return mv;
	}
	
	
	@RequestMapping(value = "/student/vivaSlotBooking", method = {RequestMethod.POST})
	public ModelAndView vivaSlotBooking(HttpServletRequest request,HttpServletResponse response,VivaSlotBean vivaSlotBean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		VivaSlotBean vivaSlotBean_tmp = levelBasedProjectDAO.getVivaSlotDateTimeById(vivaSlotBean.getViva_slots_id());
		if(vivaSlotBean_tmp == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to check slot availability");
			return vivaSlotBookingForm(request,response);
		}
		if(Float.parseFloat(vivaSlotBean_tmp.getRemaining()) <= 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Slot is not available to booked, seat full");
			return vivaSlotBookingForm(request,response);
		}
		vivaSlotBean.setBooked("Y");
		vivaSlotBean.setPayment_status("Free");
		vivaSlotBean.setYear(eDao.getLiveProjectExamYear());
		vivaSlotBean.setMonth(eDao.getLiveProjectExamMonth());
		vivaSlotBean.setSapid(student.getSapid());
		vivaSlotBean.setCreated_by(student.getSapid());
		vivaSlotBean.setUpdated_by(student.getSapid());
		if(!levelBasedProjectDAO.createVivaSlotBooking(vivaSlotBean)) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to booked slot, please try again do not refresh current page");
			return vivaSlotBookingForm(request,response);
		}
		request.setAttribute("success", "true");
		request.setAttribute("successMessage", "Successfully viva slot booked");
		return vivaSlotBookingForm(request,response);
	}
	
	
	@RequestMapping(value = "/m/getSlotTimeByDate", method = RequestMethod.POST, consumes="application/json", produces="application/json")
	public ResponseEntity<List<VivaSlotBean>> getSlotTimeByDate(@RequestBody VivaSlotBean vivaSlotBean){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(vivaSlotBean.getDate() == null) {
			return new ResponseEntity<List<VivaSlotBean>>(new ArrayList<VivaSlotBean>(), HttpStatus.OK);
		}
		return new ResponseEntity<List<VivaSlotBean>>(levelBasedProjectService.getSlotDateTimeByDate(vivaSlotBean.getDate()), HttpStatus.OK);
	}


	@RequestMapping(value = "/admin/addVivaSlotsFormForm", method = {RequestMethod.GET})
	public ModelAndView addVivaSlotsFormForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("project/vivaSlots");
		
		mv.addObject("inputBean", new VivaSlotBean());
		return mv;
	}

	@RequestMapping(value = "/admin/projectConfigurationChecklistForm", method = {RequestMethod.GET})
	public ModelAndView projectConfigurationChecklistForm(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		return new ModelAndView("project/levelBasedProjectCheckList");
	}
	

	@RequestMapping(value = "/admin/viewSubmittedSynopsis", method = {RequestMethod.GET})
	public ModelAndView viewSubmittedSynopsis(HttpServletRequest request, HttpServletResponse response, @ModelAttribute UploadProjectSynopsisBean uploadProjectSOPBean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		String userId = (String) request.getSession().getAttribute("userId");
		uploadProjectSOPBean.setFacultyId(userId);
		ModelAndView mv = new ModelAndView("viewSubmittedSynopsis");
		try {
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
		List<UploadProjectSynopsisBean> uploadProjectSynopsisResultBean = levelBasedProjectDAO.getSubmittedSynopsisWithGuidId(uploadProjectSOPBean.getFacultyId());
		if(uploadProjectSynopsisResultBean == null) {
			mv.addObject("uploadProjectSynopsisBeanList",new ArrayList<UploadProjectSynopsisBean>());
		}else {
			mv.addObject("uploadProjectSynopsisBeanList", uploadProjectSynopsisResultBean);
		}
		}catch (Exception e) {
			logger.info("Error! Failed to view Submitted Synopsis "+e);
			// TODO: handle exception
		}
		return mv;
	}
	

	@RequestMapping(value = "/admin/viewStudentSubmittedSynopsis", method = {RequestMethod.GET})
	public ModelAndView viewStudentSubmittedSynopsis(HttpServletRequest request,@ModelAttribute UploadProjectSynopsisBean uploadProjectSOPBean) {
		ModelAndView mv = new ModelAndView("viewStudentSubmittedSynopsis");
		//set for redirected url after form submit
		if(uploadProjectSOPBean.getSapid() == null || uploadProjectSOPBean.getMonth() == null || uploadProjectSOPBean.getYear() == null) {
			String sapid = request.getParameter("sapid");
			uploadProjectSOPBean.setSapid(sapid);
			String month = request.getParameter("month");
			uploadProjectSOPBean.setMonth(sapid);
			String year = request.getParameter("year");
			uploadProjectSOPBean.setYear(sapid);
		}
		try {
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
		UploadProjectSynopsisBean uploadProjectSynopsisResultBean =  levelBasedProjectDAO.viewSubmittedSynopsis(uploadProjectSOPBean);		
		if(uploadProjectSynopsisResultBean == null) {
			mv.addObject("errorFlag", "true");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! no result found,Invalid submission");
		}else {
			mv.addObject("uploadProjectSynopsisResultBean", uploadProjectSynopsisResultBean);
		}
		}catch (Exception e) {
			logger.info("Error! Failed to view Student Submitted Synopsis "+e);
			// TODO: handle exception
		}
		return mv;
	}
	
	
	@RequestMapping(value = "/admin/updateStudentSubmittedSynopsisStatus", method = {RequestMethod.POST})
	public ModelAndView updateStudentSubmittedSOPStatus(HttpServletRequest request, HttpServletResponse response, @ModelAttribute UploadProjectSynopsisBean bean) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		String userId = (String) request.getSession().getAttribute("userId");
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO)act.getBean("levelBasedProjectDAO");
		try {
			SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			UploadProjectSynopsisBean uploadProjectSynopsisResultBean =  levelBasedProjectDAO.viewSubmittedSynopsis(bean);
			uploadProjectSynopsisResultBean.setUpdated_by(userId);
			uploadProjectSynopsisResultBean.setScore(bean.getScore());
			uploadProjectSynopsisResultBean.setReason(bean.getReason());
			uploadProjectSynopsisResultBean.setEvaluated("Y");
			int count = uploadProjectSynopsisResultBean.getEvaluationCount() == null ? 1 : Integer.parseInt(uploadProjectSynopsisResultBean.getEvaluationCount()) + 1;  
			uploadProjectSynopsisResultBean.setEvaluationCount(count + "");
			uploadProjectSynopsisResultBean.setEvaluationDate(sdf3.format(timestamp));
			levelBasedProjectDAO.updateSubmittedSynopsis(uploadProjectSynopsisResultBean);
			request.setAttribute("success","true");
			request.setAttribute("successMessage", "Successfully updated SOP status");
		}catch (Exception e) {
			logger.info("Failed to updated SOP status "+e);
			request.setAttribute("error","true");
			request.setAttribute("errorMessage", "Failed to updated SOP status");	
		}
		return viewStudentSubmittedSynopsis(request, bean);
	}
	
	//Download Eligible List for PD-WM Module 4 report
	//Method execution time: 528 milliseconds.
	@RequestMapping(value = "/admin/downloadEligibleStudentsExcelReport", method = {RequestMethod.GET})
	public ModelAndView downloadEligibleStudentsForm(HttpServletRequest request, HttpServletResponse response)  {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("PDWMProjectEligibleStudentsRequestExcelView");
		String CPSID = "112";
		String sem = "2";
		String subject="Module 4 - Project"; 
		String isPass="Y";
		
		List<StudentExamBean> eligiblelist = new ArrayList<StudentExamBean>();
		List<String> resitCycleMonth =new ArrayList(Arrays.asList("Apr","Sep"));
		try 
		{
			List<StudentExamBean> studentDetailList = levelBasedProjectService.getProjectApplicableStudents(CPSID,sem);
			StudentExamBean monthyear = levelBasedProjectService.getProjectConfigurationMonthYear();
			if(studentDetailList.size() > 0) {
				for (StudentExamBean list: studentDetailList) {
					boolean isValid = levelBasedProjectService.isStudentValid(list.getSapid(),list.getValidityEndMonth(),list.getValidityEndYear());
					if(isValid){
					//check if student is pass in Module 4 - Project 
						boolean studentPassProject = levelBasedProjectService.checkIfStudentPassProject(list.getSapid(),subject,isPass);
						if(!studentPassProject) {
							eligiblelist.add(list);
						}
						
						String projectConfigMonth =monthyear.getMonth();
						if(resitCycleMonth.contains(projectConfigMonth)) {
							if(list.getMonth().equalsIgnoreCase(CURRENT_ACAD_MONTH) && list.getYear().equalsIgnoreCase(CURRENT_ACAD_YEAR) ) {
								eligiblelist.remove(list);
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! Failed to generate report.");
			// TODO: handle exception
		}
		if(eligiblelist == null || eligiblelist.size() == 0) {
			mv.addObject("errorFlag", "true");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! No records found.");
			return new ModelAndView("project/levelBasedProjectCheckList");
		}else {
			return new ModelAndView(PDWMProjectEligibleStudentsRequestExcelView, "eligiblelist", eligiblelist);
		}
	}
	//Download Eligible List for PD-WM Module 4 report
	
	//Download SOP & Synopsis Submission Report Form
	
	private void getMonthYearList(ModelAndView mv) {
		mv.addObject("inputBean", new LevelBasedProjectBean());
		mv.addObject("yearList", CURRENT_YEAR_LIST);
		mv.addObject("monthList", ACAD_MONTH_LIST);
	}
	
	//Method execution time : 2 milliseconds
	@RequestMapping(value = "/admin/downloadSOPorSynopsisSubmitted&Transaction_or_SynopsisScoreReportForm", method = {RequestMethod.GET})
	public ModelAndView downloadSOPAndSynopsisSubmittedandTransactionReportForm(HttpServletRequest request, HttpServletResponse response)  {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("project/downloadSOPAndSynopsisSubmissionReportForm");
		getMonthYearList(mv);
		return mv;
	}
	
	//Search SOP Submitted and Transaction List 
	//Method execution time : 71 milliseconds
	@RequestMapping(value = "/admin/searchSOPSubmittedandTransactionList", method = {RequestMethod.POST})
	public ModelAndView searchSOPSubmittedandTransactionList(@ModelAttribute("inputBean") LevelBasedProjectBean inputBean,HttpServletRequest request, HttpServletResponse response)  {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("project/downloadSOPAndSynopsisSubmissionReportForm");
		List<UploadProjectSOPBean> SOPSubmissionList = new ArrayList<UploadProjectSOPBean>();
		List<PaymentGatewayTransactionBean> SOPTransactionList = new ArrayList<PaymentGatewayTransactionBean>();
		List<String> trackIDList = new ArrayList<String>();
		getMonthYearList(mv);
		mv.addObject("inputBean",inputBean);
		try 
		{
			SOPSubmissionList = levelBasedProjectService.getSOPSubmissionList(inputBean.getMonth(),inputBean.getYear());
			if(SOPSubmissionList.size() > 0) {
				mv.addObject("SOPCount", SOPSubmissionList.size());
				request.getSession().setAttribute("SOPSubmissionList", SOPSubmissionList);
			}
			for(UploadProjectSOPBean SOPTrackID : SOPSubmissionList) {
				trackIDList.add(SOPTrackID.getTrack_id());
			}
			SOPTransactionList = levelBasedProjectService.getSOPorSynopsisTransactionList(trackIDList);
			if(SOPTransactionList.size() > 0) {
				mv.addObject("SOPTransactionCount", SOPTransactionList.size());
				request.getSession().setAttribute("SOPTransactionList", SOPTransactionList);
			}
		}
		catch (Exception e) {
			logger.info("Error! Failed to generate SOPSubmittedandTransactionList "+e);
			mv.addObject("errorFlag", "true");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! Failed to generate report.");
			// TODO: handle exception
		}
		if(SOPSubmissionList == null || SOPSubmissionList.size() == 0) {
			mv.addObject("errorFlag", "true");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! No records found for SOP Submitted list.");
			return mv;
		} 
		if(SOPTransactionList == null || SOPTransactionList.size() == 0) {
			  mv.addObject("errorFlag", "true"); request.setAttribute("error", "true");
			  request.setAttribute("errorMessage","Error! No records found for SOP Transaction list."); 
			  return mv;
		}
		return mv;
	}
	
	//Download SOP Submitted and Transaction List Excel Report 
	//Method execution time : 1 milliseconds
	@RequestMapping(value = "/admin/downloadSOPSubmittedandTransactionExcelReport", method = {RequestMethod.GET})
	public ModelAndView downloadSOPSubmittedandTransactionListExcelReport(HttpServletRequest request, HttpServletResponse response)  {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("project/downloadSOPAndSynopsisSubmissionReportForm");
		ModelAndView MV = new ModelAndView("SOPSubmittedListExcelView");
		getMonthYearList(mv);
		try 
		{
			List<UploadProjectSOPBean> SOPSubmissionList = (List<UploadProjectSOPBean>)request.getSession().getAttribute("SOPSubmissionList");
			List<PaymentGatewayTransactionBean> SOPTransactionList = (List<PaymentGatewayTransactionBean>)request.getSession().getAttribute("SOPTransactionList");
			MV.addObject("SOPSubmissionList", SOPSubmissionList);
			MV.addObject("SOPTransactionList", SOPTransactionList);
			return MV;
		  //return new ModelAndView(SOPSubmittedListExcelView, "SOPSubmissionList", SOPSubmissionList);
		}
		catch (Exception e) {
			logger.info("Error! Failed to generate SOPSubmittedandTransactionExcelReport. "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! Failed to generate report.");
			return mv;
			// TODO: handle exception
		}
	}
	
	//search Synopsis Submitted and Transaction List 
	//Method execution time : 50 milliseconds.
	@RequestMapping(value = "/admin/searchSynopsisSubmittedandTransactionList", method = {RequestMethod.POST})
	public ModelAndView searchSynopsisSubmittedandTransactionList(@ModelAttribute("inputBean") LevelBasedProjectBean inputBean,HttpServletRequest request, HttpServletResponse response)  {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("project/downloadSOPAndSynopsisSubmissionReportForm");
		List<UploadProjectSynopsisBean> SynopsisSubmissionList = new ArrayList<UploadProjectSynopsisBean>();
		List<PaymentGatewayTransactionBean> SynopsisTransactionList = new ArrayList<PaymentGatewayTransactionBean>();
		List<String> trackIDList = new ArrayList<String>();
		getMonthYearList(mv);
		mv.addObject("inputBean",inputBean);
		try 
		{
			SynopsisSubmissionList = levelBasedProjectService.getSynopsisSubmissionList(inputBean.getMonth(),inputBean.getYear());
			if(SynopsisSubmissionList.size() > 0) {
				mv.addObject("SynopsisCount", SynopsisSubmissionList.size());
				request.getSession().setAttribute("SynopsisSubmissionList", SynopsisSubmissionList);
			}
			for(UploadProjectSynopsisBean SynopsisTrackID : SynopsisSubmissionList) {
				trackIDList.add(SynopsisTrackID.getTrack_id());
			}
			SynopsisTransactionList = levelBasedProjectService.getSOPorSynopsisTransactionList(trackIDList);
			if(SynopsisTransactionList.size() > 0) {
				mv.addObject("SynopsisTransactionCount", SynopsisTransactionList.size());
				request.getSession().setAttribute("SynopsisTransactionList", SynopsisTransactionList);
			}
		}
		catch (Exception e) {
			logger.info("Error! Failed to generate SubmittedandTransactionList. "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! Failed to generate report.");
			// TODO: handle exception
		}
		if(SynopsisSubmissionList == null || SynopsisSubmissionList.size() == 0) {
			mv.addObject("errorFlag", "true");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! No records found.");
			return mv;
		}/*if(SynopsisTransactionList == null || SynopsisTransactionList.size() == 0) {
			  mv.addObject("errorFlag", "true"); request.setAttribute("error", "true");
			  request.setAttribute("errorMessage","Error! No records found for Synsopsis Transaction list."); 
			  return mv; 
		}*/ 
		return mv;
	}
	
	//Download Synopsis Submitted and Transaction List Excel Report
	//Method execution time : 1 milliseconds
		@RequestMapping(value = "/admin/downloadSynopsisSubmittedandTransactionExcelReport", method = {RequestMethod.GET})
		public ModelAndView downloadSynopsisSubmittedandTransactionListExcelReport(HttpServletRequest request, HttpServletResponse response)  {
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mv = new ModelAndView("project/downloadSOPAndSynopsisSubmissionReportForm");
			ModelAndView MV = new ModelAndView("SynopsisSubmittedListExcelView");
			getMonthYearList(mv);
			try 
			{
				List<UploadProjectSynopsisBean> SynopsisSubmissionList = (List<UploadProjectSynopsisBean>)request.getSession().getAttribute("SynopsisSubmissionList");
				List<PaymentGatewayTransactionBean> SynopsisTransactionList = (List<PaymentGatewayTransactionBean>)request.getSession().getAttribute("SynopsisTransactionList");
				MV.addObject("SynopsisSubmissionList", SynopsisSubmissionList);
				MV.addObject("SynopsisTransactionList", SynopsisTransactionList);
				return MV;				
			}
			catch (Exception e) {
				logger.info("Error! Failed to generate SynopsisSubmittedandTransactionExcelReport. "+e);
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error! Failed to generate report.");
				return mv;
				// TODO: handle exception
			}
		}
		
	//search Synopsis Evaluated Score List Report 
	//Method execution time : 11 milliseconds
	@RequestMapping(value = "/admin/searchSynopsisEvaluatedScoreReport", method = {RequestMethod.POST})
	public ModelAndView searchSynopsisEvaluatedScoreReport(@ModelAttribute("inputBean") LevelBasedProjectBean inputBean,HttpServletRequest request, HttpServletResponse response)  {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("project/downloadSOPAndSynopsisSubmissionReportForm");
		List<UploadProjectSynopsisBean> SynopsisEvaluatedScoreList = new ArrayList<UploadProjectSynopsisBean>();
		getMonthYearList(mv);
		mv.addObject("inputBean",inputBean);
		String evaluated="Y";
		try 
		{
			SynopsisEvaluatedScoreList = levelBasedProjectService.getSynopsisEvaluatedScoreList(inputBean.getMonth(),inputBean.getYear(),evaluated);
			if(SynopsisEvaluatedScoreList.size() > 0) {
				mv.addObject("SynopsisScoreReportCount", SynopsisEvaluatedScoreList.size());
				request.getSession().setAttribute("SynopsisEvaluatedScoreList", SynopsisEvaluatedScoreList);
			}
		}
		catch (Exception e) {
			logger.info("Error! Failed to generate SynopsisEvaluatedScoreReport. "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! Failed to generate report.");
			// TODO: handle exception
		}
		if(SynopsisEvaluatedScoreList == null || SynopsisEvaluatedScoreList.size() == 0) {
			mv.addObject("errorFlag", "true");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error! No records found.");
			return mv;
		}else {
			return mv;
		}
	}
	
	//Download Synopsis Score List Excel Report
	//Method execution time : 2 milliseconds
		@RequestMapping(value = "/admin/downloadSynopsisEvaluatedScoreExcelReport", method = {RequestMethod.GET})
		public ModelAndView downloadSynopsisEvaluatedScoreExcelReport(HttpServletRequest request, HttpServletResponse response)  {
			if(!checkSession(request, response)){
				redirectToPortalApp(response);
				return null;
			}
			ModelAndView mv = new ModelAndView("project/downloadSOPAndSynopsisSubmissionReportForm");
			getMonthYearList(mv);
			try 
			{
				List<UploadProjectSynopsisBean> SynopsisEvaluatedScoreList = (List<UploadProjectSynopsisBean>)request.getSession().getAttribute("SynopsisEvaluatedScoreList");
				return new ModelAndView(SynopsisSubmittedListExcelView, "SynopsisEvaluatedScoreList", SynopsisEvaluatedScoreList);
			}
			catch (Exception e) {
				logger.info("Error! Failed to generate SynopsisEvaluatedScoreExcelReport "+e);
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error! Failed to generate report.");
				return new ModelAndView("project/downloadSOPAndSynopsisSubmissionReportForm");
				// TODO: handle exception
			}
		}
		public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
			long diffInMillies = date2.getTime() - date1.getTime();
			return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
		}
}
