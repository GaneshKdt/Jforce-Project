package com.nmims.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
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
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.LevelBasedProjectBean;
import com.nmims.beans.LevelBasedSOPConfigBean;
import com.nmims.beans.LevelBasedSynopsisConfigBean;
import com.nmims.beans.ProjectModuleExtensionBean;
import com.nmims.beans.ProjectStudentStatus;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.UploadProjectSOPBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.LevelBasedProjectDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.LevelBasedProjectHelper;
import com.nmims.services.LevelBasedProjectService;
import com.nmims.services.ProjectStudentEligibilityService;

@Controller
public class ProjectSOPController extends BaseController {

	@Autowired
	ApplicationContext act;

	@Autowired
	LevelBasedProjectHelper levelBasedProjectHelper;

	@Autowired
	LevelBasedProjectService levelBasedProjectService;

	@Autowired
	ProjectStudentEligibilityService eligibilityService;

	@Autowired
	LevelBasedProjectDAO levelBasedProjectDAO;
	
	@Autowired
	AmazonS3Helper amazonS3Helper;

	@Value("${SUBMITTED_SOP_FILES_PATH}")
	private String SUBMITTED_SOP_FILES_PATH;

	@Value("${SOP_PAYMENT_RETURN_URL}")
	private String SOP_PAYMENT_RETURN_URL;

	@Value("${SYNOPSIS_PAYMENT_RETURN_URL}")
	private String SYNOPSIS_PAYMENT_RETURN_URL;

	@Value("${SUBMITTED_SYNOPSIS_FILES_PATH}")
	private String SUBMITTED_SYNOPSIS_FILES_PATH;

	@Value("${SYNOPSIS_FILES_PATH}")
	private String SYNOPSIS_FILES_PATH;

	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;

	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private List<String> CURRENT_YEAR_LIST;

	private final int MAX_FILE_SIZE = 5242880;

	private final List<String> SOP_STATUS = Arrays.asList("Payment pending", "Payment failed", "Submitted", "Rejected",
			"Approved");
	private final List<String> SOP_TRANSACTION_STATUS = Arrays.asList("Initiated", "Payment Successfull",
			"Payment failed", "Expired", "No Charges");

	private ArrayList<String> subjectList = null;

	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.subjectList = dao.getActiveSubjects();
		}
		return subjectList;
	}

	@RequestMapping(value = "/admin/levelBasedSOPConfigForm", method = { RequestMethod.GET })
	public ModelAndView levelBasedSOPConfigForm(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("project/sop/config");
		mv.addObject("levelBasedSOPConfigBeansList", getLevelBasedSOPConfigList());
		mv.addObject("consumerType", getConsumerTypeList());
		mv.addObject("inputBean", new LevelBasedSOPConfigBean());
		mv.addObject("yearList", CURRENT_YEAR_LIST);
		mv.addObject("monthList", ACAD_MONTH_LIST);
		return mv;
	}

	@RequestMapping(value = "/admin/levelBasedSOPConfig", method = { RequestMethod.POST })
	public ModelAndView levelBasedSOPConfig(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute LevelBasedSOPConfigBean levelBasedSOPConfigBean) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String) request.getSession().getAttribute("userId");
		levelBasedSOPConfigBean.setLastModifiedBy(userId);
		levelBasedSOPConfigBean.setCreatedBy(userId);

		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		List<Integer> errorList = new ArrayList<Integer>();
		int successCount = 0;
		ArrayList<Integer> programSemSubjectIds = levelBasedProjectDAO.getProgramStructureIdBasedOnMasterKeyAndSubject(
				levelBasedSOPConfigBean.getProgram(), levelBasedSOPConfigBean.getProgram_structure(),
				levelBasedSOPConfigBean.getConsumer_type(), levelBasedSOPConfigBean.getSubject());

		for (Integer pssId : programSemSubjectIds) {
			levelBasedSOPConfigBean.setProgram_sem_subject_id(pssId);
			if (levelBasedSOPConfigBean.getPayment_amount() == null
					|| levelBasedSOPConfigBean.getPayment_amount() == "") {
				levelBasedSOPConfigBean.setPayment_amount("0");
			}
			try {
				levelBasedProjectDAO.insertIntoConfig(levelBasedSOPConfigBean);
			} catch (Exception e) {
				levelBasedSOPConfigBean.setError("Error Inserting Record : " + e.getMessage());
				errorList.add(pssId);
				
			}
		}
		if (successCount > 0) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Successfully created new record");
		}

		if (errorList.size() > 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to create some new records: " + errorList.size());
			request.setAttribute("errorList", errorList);
		}

		ModelAndView mv = new ModelAndView("project/sop/config");
		mv.addObject("levelBasedSOPConfigBeansList", getLevelBasedSOPConfigList());
		mv.addObject("consumerType", getConsumerTypeList());
		mv.addObject("inputBean", levelBasedSOPConfigBean);
		mv.addObject("yearList", CURRENT_YEAR_LIST);
		mv.addObject("monthList", ACAD_MONTH_LIST);
		return mv;
	}

	private List<LevelBasedSOPConfigBean> getLevelBasedSOPConfigList() {
		return levelBasedProjectDAO.getLiveSOPConfigurationList();
	}

	private ArrayList<ConsumerProgramStructureExam> getConsumerTypeList() {
		AssignmentsDAO dao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		return dao.getConsumerTypeList();
	}

	@RequestMapping(value = "/admin/studentAndGuidMappingForm", method = { RequestMethod.GET })
	public ModelAndView studentAndGuidMappingForm(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView mv = new ModelAndView();
		configureGuideMapingView(mv);
		return mv;
	}

	@RequestMapping(value = "/admin/studentAndGuidMapping", method = { RequestMethod.POST })
	public ModelAndView studentAndGuidMapping(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute LevelBasedProjectBean levelBasedProjectBean) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		String userId = (String) request.getSession().getAttribute("userId");
		levelBasedProjectBean.setCreatedBy(userId);
		levelBasedProjectBean.setLastModifiedBy(userId);

		ModelAndView mv = new ModelAndView();
		List<LevelBasedProjectBean> levelBasedProjectErrorList = new ArrayList<LevelBasedProjectBean>();
		List<LevelBasedProjectBean> levelBasedProjectBeanList = new ArrayList<LevelBasedProjectBean>();
		try {
			levelBasedProjectHelper.readStudentGuidMappingDataFromExcel(levelBasedProjectBean,
					levelBasedProjectBeanList, levelBasedProjectErrorList);
			int success = 0;
			for (LevelBasedProjectBean bean : levelBasedProjectBeanList) {
				try {
					levelBasedProjectDAO.upsertIntoStudentMapping(bean);
					success++;
				} catch (Exception e) {
					
					bean.setError("Error Adding Row to DB : " + e.getMessage());
					levelBasedProjectErrorList.add(bean);
					mv.addObject("errorFlag", "true");
				}
			}
			mv.addObject("levelBasedProjectErrorList", levelBasedProjectErrorList);

			if (success > 0) {
				mv.addObject("success", "true");
				mv.addObject("successMessage", "Successfully student mapping inserted : " + success);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			
			mv.addObject("error", "true");
			mv.addObject("errorMessage", e1.getMessage());
		}
		configureGuideMapingView(mv);
		mv.addObject("inputBean", levelBasedProjectBean);
		return mv;
	}

	private void configureGuideMapingView(ModelAndView mv) {
		mv.setViewName("project/sop/studentAndGuidMappingForm");
		mv.addObject("inputBean", new LevelBasedProjectBean());
		mv.addObject("levelBasedProjectBeans", levelBasedProjectDAO.getAllStudentGuideMapping());
		mv.addObject("subjectList", getSubjectList());
		mv.addObject("yearList", CURRENT_YEAR_LIST);
		mv.addObject("monthList", ACAD_MONTH_LIST);
	}

	@RequestMapping(value = "/student/uploadProjectSOPForm", method = { RequestMethod.GET })
	public ModelAndView uploadProjectSOPForm(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String subject) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("project/student/uploadProjectSOP");
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
		//String examMonth = eDao.getLiveProjectExamMonth();
		//String examYear = eDao.getLiveProjectExamYear();

		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");
		/*
		 * boolean isStudentEligibleForSubject =
		 * levelBasedProjectService.checkIfProgramSemSubjectIdApplicableForStudent(
		 * student.getSapid(),subject); if(!isStudentEligibleForSubject) {
		 * setError(request, "You are not eligible for Project Submission!"); return mv;
		 * }
		 */
		
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
		ProjectStudentStatus status = new ProjectStudentStatus();
		try {
			String pssId = levelBasedProjectService.getProgramSemSubjectIdForStudent(student.getSapid(), subject);
			status.setProgramSemSubjId(pssId);
		} catch (Exception e) {
			// TODO: handle exception
			setError(request, "You are not eligible for Project Submission!");
			return mv;
		}
		status.setSapid(student.getSapid());
		status.setSubject(subject);
		status.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		//status.setExamMonth(examMonth);
		//status.setExamYear(examYear);
		status.setExamMonth(examMonth);
		status.setExamYear(examYear);
		eligibilityService.getPaymentInfoForStudentV2(student, status);

		if (status.getCanSubmit().equals("N")) {
			setError(request, status.getCantSubmitError());
			return mv;
		}

		mv.addObject("projectStatus", status);
		return mv;

		/*
		 * if(status.getPaymentPending().equals("Y")) { setError(request,
		 * "Project Fee Payment Still Pending!"); return mv; }
		 */
		/*
		 * try { ProjectConfiguration config =
		 * levelBasedProjectService.getProjectConfigurationForSubjectId(examMonth,
		 * examYear, pssId); status.setHasSOP(config.getHasSOP());
		 * 
		 * List<ProjectModuleExtensionBean> extensions =
		 * levelBasedProjectService.getProjectExtensionsListForStudent(examMonth,
		 * examYear, student.getSapid(), pssId); if("Y".equals(config.getHasSOP())) {
		 * ProjectModuleStatusBean sopStatus =
		 * eligibilityService.getSOPInfoForStudent(student, pssId, subject, examYear,
		 * examMonth, extensions); status.setSopStatus(sopStatus);
		 * 
		 * if(!StringUtils.isBlank(sopStatus.getError())) { setError(request,
		 * sopStatus.getError()); return mv; } } else { setError(request,
		 * "Submission criteria not defined for current subject!"); return mv; } }catch
		 * (Exception e) {  setError(request,
		 * "Error getting SOP submission info!"); mv.addObject("submitted", false); }
		 * mv.addObject("projectStatus", status); return mv;
		 */
	}

	@RequestMapping(value = "/student/uploadProjectSOP", method = {
			RequestMethod.POST }, headers = "content-type=multipart/form-data")
	public ModelAndView uploadProjectSOP(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute UploadProjectSOPBean uploadProjectSOPBean) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("project/student/uploadProjectSOP");
		ExamBookingDAO eDao = (ExamBookingDAO) act.getBean("examBookingDAO");
		//String examMonth = eDao.getLiveProjectExamMonth();
		//String examYear = eDao.getLiveProjectExamYear();
		String errorMessage = "";
		//LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO) act.getBean("levelBasedProjectDAO");
		
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");

		ProjectStudentStatus status = new ProjectStudentStatus();
		
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
			String method = "uploadProjectSOP()";
			// Set Student Applicable Exam Mont Year
			AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(student.getSapid(), "Module 4 - Project" , method);
			examMonth = examMonthYearBean.getMonth();
			examYear = examMonthYearBean.getYear();
		}
		try {
			String pssId = levelBasedProjectService.getProgramSemSubjectIdForStudent(student.getSapid(),
					uploadProjectSOPBean.getSubject());
			status.setProgramSemSubjId(pssId);
		} catch (Exception e) {
			// TODO: handle exception
			setError(request, "You are not eligible for Project Submission!");
			return mv;
		}
		status.setSapid(student.getSapid());
		status.setSubject(uploadProjectSOPBean.getSubject());
		status.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
		status.setExamMonth(examMonth);
		status.setExamYear(examYear);

		eligibilityService.getPaymentInfoForStudentV2(student, status);

		if (status.getCanSubmit().equals("N")) {
			setError(request, status.getCantSubmitError());
			return mv;
		}
		mv.addObject("projectStatus", status);

		try {

			uploadProjectSOPBean.setYear(status.getExamYear());
			uploadProjectSOPBean.setMonth(status.getExamMonth());
			uploadProjectSOPBean.setSapId(student.getSapid());
			uploadProjectSOPBean.setCreated_by(student.getSapid());
			uploadProjectSOPBean.setUpdated_by(student.getSapid());

			String trackId = student.getSapid() + System.currentTimeMillis();
			uploadProjectSOPBean.setTrack_id(trackId);
			// live flag check and current year month assign.
			if (uploadProjectSOPBean == null || uploadProjectSOPBean.getFileData() == null) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload Inner: No File Selected");
				return mv;
			}

			long fileSizeInBytes = uploadProjectSOPBean.getFileData().getSize();
			if (fileSizeInBytes > MAX_FILE_SIZE) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",
						"file is larger than excepted, please upload less than " + MAX_FILE_SIZE + " size");
				return mv;
			}
			CommonsMultipartFile file = uploadProjectSOPBean.getFileData();
			String fileName = file.getOriginalFilename();

			if (!(fileName.toUpperCase().endsWith(".PDF"))) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",
						"Only PDF file allowed,please convert your file to PDF file format");
				return mv;
			}
			/*InputStream inputStream = null;
			OutputStream outputStream = null;
			
			InputStream tempInputStream = file.getInputStream();
			byte[] initialbytes = new byte[4];
			tempInputStream.read(initialbytes);

			tempInputStream.close();

			inputStream = file.getInputStream();
			
			String filePath = SUBMITTED_SOP_FILES_PATH + uploadProjectSOPBean.getMonth()
					+ uploadProjectSOPBean.getYear() + "/" + fileName;

			File folderPath = new File(
					SUBMITTED_SOP_FILES_PATH + uploadProjectSOPBean.getMonth() + uploadProjectSOPBean.getYear());
			if (!folderPath.exists()) {
				// "Making Folder";
				folderPath.mkdirs();
				// "created = "+created;
			}
			
			File newFile = new File(filePath);

			outputStream = new FileOutputStream(newFile);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			outputStream.close();
			inputStream.close();
			*/

			String year = uploadProjectSOPBean.getYear();
			String month= uploadProjectSOPBean.getMonth();
			
			fileName = uploadProjectSOPBean.getSapId() + "_" + RandomStringUtils.randomAlphanumeric(12) + ".pdf";
			String folderPath = "SOP/submissions/"+month + year + "/";
			uploadProjectSOPBean.setFilePath(month + year + "/" + fileName);

			HashMap<String,String> s3_response = amazonS3Helper.uploadFiles(file,folderPath,"guidedprojectcontent",folderPath+fileName);
			
			if(!s3_response.get("status").equalsIgnoreCase("success")) {
				errorMessage =  "Error in uploading file "+s3_response.get("fileUrl"); 
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",errorMessage);
				return mv;
			}

			//LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO) act.getBean("levelBasedProjectDAO");

			String previewPath = uploadProjectSOPBean.getMonth() + uploadProjectSOPBean.getYear() + "/" + fileName;
			uploadProjectSOPBean.setPreviewPath(previewPath);

			uploadProjectSOPBean.setFacultyId(status.getSopStatus().getFacultyId());

			if ("Y".equalsIgnoreCase(status.getSopStatus().getPayment_applicable())
					&& "Y".equalsIgnoreCase(status.getPaymentPending())) {
				// payment form
				// Transactions are temporarily disabled for the new cycle
				uploadProjectSOPBean.setPayment_status(SOP_TRANSACTION_STATUS.get(0));
				uploadProjectSOPBean.setStatus(SOP_STATUS.get(0));
				uploadProjectSOPBean.setAttempt(1);
				// levelBasedProjectDAO.insertSOPRecordToHistory(uploadProjectSOPBean);
				if (levelBasedProjectDAO.upsertPaymentSOPRecord(uploadProjectSOPBean)) {
					ModelAndView payment_mv = new ModelAndView("payment");
					request.getSession().setAttribute("track_id", trackId);
					request.getSession().setAttribute("project_module_base_subject", uploadProjectSOPBean.getSubject());
					payment_mv.addObject("track_id", trackId);
					payment_mv.addObject("sapid", student.getSapid());
					payment_mv.addObject("type", "SOP");
					payment_mv.addObject("amount", status.getSopStatus().getPayment_amount());
					payment_mv.addObject("description", "SOP Fees for " + student.getSapid());
					payment_mv.addObject("portal_return_url", SOP_PAYMENT_RETURN_URL);
					payment_mv.addObject("created_by", student.getSapid());
					payment_mv.addObject("updated_by", student.getSapid());
					payment_mv.addObject("mobile", student.getMobile());
					payment_mv.addObject("email_id", student.getEmailId());
					payment_mv.addObject("first_name", student.getFirstName());
					payment_mv.addObject("source", "web");
					// return new ModelAndView("redirect:/sopPaymentResponse?trackId=" + trackId +
					// "&status=success&message=Successfully complete"); //testing purpose
					return payment_mv;
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Failed to initiate payment");
					return uploadProjectSOPForm(request, response, uploadProjectSOPBean.getSubject());
				}
			} else {
				uploadProjectSOPBean.setPayment_status(SOP_TRANSACTION_STATUS.get(4));
				uploadProjectSOPBean.setStatus(SOP_STATUS.get(2));
				uploadProjectSOPBean.setAttempt(status.getSopStatus().getSubmissionsMade() + 1);
				try {
					levelBasedProjectDAO.insertSOPRecordToHistory(uploadProjectSOPBean);
					levelBasedProjectDAO.insertSOPRecord(uploadProjectSOPBean);
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Successfully project SOP submitted");
				} catch (Exception e) {
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Failed to submit SOP file");
				}
				return uploadProjectSOPForm(request, response, uploadProjectSOPBean.getSubject());
			}
		} catch (Exception e) {
			// TODO: handle exception
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error : " + e.getMessage());
			return uploadProjectSOPForm(request, response, uploadProjectSOPBean.getSubject());
		}
		
	}

	@RequestMapping(value = "/student/sopPaymentResponse", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView sopPaymentResponse(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute UploadProjectSOPBean uploadProjectSOPBean) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		// ModelAndView mv = new ModelAndView("uploadProjectSOP");
		UploadProjectSOPBean uploadProjectSOPBean2 = null;
		try {
			StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");
			LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO) act.getBean("levelBasedProjectDAO");
			uploadProjectSOPBean2 = levelBasedProjectDAO
					.getSOPByTrackId((String) request.getSession().getAttribute("track_id"));
			if (!student.getSapid().equalsIgnoreCase(uploadProjectSOPBean2.getSapId())) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Invalid trackId found");
				return uploadProjectSOPForm(request, response, uploadProjectSOPBean2.getSubject());
			}
			if (SOP_TRANSACTION_STATUS.get(1).equalsIgnoreCase(uploadProjectSOPBean2.getPayment_status())) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Invalid URl found");
				return uploadProjectSOPForm(request, response, uploadProjectSOPBean2.getSubject());
			}
			uploadProjectSOPBean2.setSubject((String) request.getSession().getAttribute("project_module_base_subject"));
			/*
			 * uploadProjectSOPBean.setSapId(student.getSapid());	
			 * uploadProjectSOPBean.setYear(eDao.getLiveExamYear());
			 * uploadProjectSOPBean.setMonth(eDao.getLiveExamMonth());
			 * uploadProjectSOPBean.setSubject("Module 4 - Project");
			 * uploadProjectSOPBean.setTrack_id((String)
			 * request.getSession().getAttribute("track_id"));
			 */
			String trackId = request.getParameter("track_id");
			String status = request.getParameter("transaction_status");
			String message = request.getParameter("error");
			if ("Payment Successfull".equalsIgnoreCase(status)
					&& trackId.equalsIgnoreCase((String) request.getSession().getAttribute("track_id"))) {
				uploadProjectSOPBean2.setStatus(SOP_STATUS.get(2));
				uploadProjectSOPBean2.setPayment_status(SOP_TRANSACTION_STATUS.get(1));
				levelBasedProjectDAO.insertSOPRecordToHistory(uploadProjectSOPBean2);
				String updateStatus = levelBasedProjectDAO.updateSOPTransactionStatus(uploadProjectSOPBean2);
				if ("true".equalsIgnoreCase(updateStatus)) {
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Successfully payment completed");
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", updateStatus);
				}
				
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Failed to complete payment process, Error: " + message);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uploadProjectSOPForm(request, response, uploadProjectSOPBean2.getSubject());
	}

	@RequestMapping(value = "/admin/viewSubmittedSOP", method = { RequestMethod.GET })
	public ModelAndView viewSubmittedSOP(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute UploadProjectSOPBean uploadProjectSOPBean) {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}

		String userId = (String) request.getSession().getAttribute("userId");
		uploadProjectSOPBean.setFacultyId(userId);
		ModelAndView mv = new ModelAndView("viewSubmittedSOP");
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO) act.getBean("levelBasedProjectDAO");
		List<UploadProjectSOPBean> uploadProjectSOPBeanList = levelBasedProjectDAO
				.getSubmittedSOPWithGuidId(uploadProjectSOPBean.getFacultyId());
		if (uploadProjectSOPBeanList == null) {
			mv.addObject("uploadProjectSOPBeanList", new ArrayList<UploadProjectSOPBean>());
		} else {
			mv.addObject("uploadProjectSOPBeanList", uploadProjectSOPBeanList);
		}
		return mv;
	}
	
	//Method execution time: 234 milliseconds
	@RequestMapping(value = "/admin/viewStudentSubmittedSOP", method = { RequestMethod.POST })
	public ModelAndView viewStudentSubmittedSOP(HttpServletRequest request,
			@ModelAttribute UploadProjectSOPBean uploadProjectSOPBean) {
		ModelAndView mv = new ModelAndView("viewStudentSubmittedSOP");
		
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO) act.getBean("levelBasedProjectDAO");
		UploadProjectSOPBean uploadProjectSOPResultBean = levelBasedProjectDAO.viewSubmittedSOP(uploadProjectSOPBean);

		String moduleType = "Synopsis";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date();
		Date synopsisEndDate;
		try {
			String CPSID = levelBasedProjectService.getCPSID(uploadProjectSOPBean.getSapId());
			List<String> pssIdList = levelBasedProjectService.getPSSIDList(CPSID);
			String commaSeparatedPSSID = String.join(",", pssIdList);
			String pssId = levelBasedProjectService.getProgramSemSubjectId(uploadProjectSOPBean.getMonth(),uploadProjectSOPBean.getYear(), commaSeparatedPSSID);
			LevelBasedSynopsisConfigBean levelBasedSynopsisConfigBean = levelBasedProjectService.getSynopsisConfiguration(uploadProjectSOPBean.getYear(), uploadProjectSOPBean.getMonth(), pssId);
			ProjectModuleExtensionBean extensions = levelBasedProjectService.getProjectExtensionsEndDateModuleType(uploadProjectSOPBean.getMonth(), uploadProjectSOPBean.getYear(), uploadProjectSOPBean.getSapId(), moduleType);
			
			//if student had extended synopsis end date set end date from ProjectModuleExtension end date
			if(extensions.getEndDate()==null || extensions.getEndDate() == "") {
				synopsisEndDate = sdf.parse(levelBasedSynopsisConfigBean.getEnd_date());
			}else {
				synopsisEndDate = sdf.parse(extensions.getEndDate());
			}
			/*
			 * if("N".equals(levelBasedSynopsisConfigBean.getLive()) ||
			 * levelBasedSynopsisEndDate.before(curDate) || moduleEndDate.before(curDate) )
			 * { request.setAttribute("error", "true"); request.setAttribute("errorMessage",
			 * "You can not update status : Synopsis Submission End Date has expired !");
			 * }else {
			 */
			if (uploadProjectSOPResultBean == null) {
				mv.addObject("errorFlag", "true");
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error! no result found,Invalid submission");
			} else {
				mv.addObject("uploadProjectSOPResultBean", uploadProjectSOPResultBean);
			}
			if ("N".equals(levelBasedSynopsisConfigBean.getLive()) || synopsisEndDate.before(curDate)) {
				String isEndDateExpired = "Y";
				mv.addObject("isEndDateExpired", isEndDateExpired);
				return mv;
			}
		} catch (Exception e1) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error : " + e1.getMessage());
		}
		return mv;
	}

	@RequestMapping(value = "/admin/updateStudentSubmittedSOPStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView updateStudentSubmittedSOPStatus(HttpServletRequest request,
			@ModelAttribute UploadProjectSOPBean uploadProjectSOPBean) {
		// update status
		LevelBasedProjectDAO levelBasedProjectDAO = (LevelBasedProjectDAO) act.getBean("levelBasedProjectDAO");
		if (levelBasedProjectDAO.updateSubmittedSOP(uploadProjectSOPBean)) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Successfully updated SOP status");
		} else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to updated SOP status");
		}
	return viewStudentSubmittedSOP(request,uploadProjectSOPBean);
	}

	@RequestMapping(value = "/admin/deleteStudentGuideMapping", method = {RequestMethod.POST},consumes = "application/json")
	public ResponseEntity<Boolean> deleteStudentGuideMapping(HttpServletRequest request,@RequestBody LevelBasedProjectBean bean) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HashMap<String,String> response = new HashMap<>();
		
		Boolean status =  levelBasedProjectDAO.deleteStudentGuideMappingForACycle(bean);
		if(status==true) {
			return new ResponseEntity<>(status,headers, HttpStatus.OK);
		} 
		return new ResponseEntity<>(status,headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
