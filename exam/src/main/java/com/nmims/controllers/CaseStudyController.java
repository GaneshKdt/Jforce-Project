package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.log.SysoCounter;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.CaseStudyExamBean;
import com.nmims.beans.Page;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.CaseStudyDao;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.MailSender;

@Controller
@RequestMapping("/student")
public class CaseStudyController extends ExecutiveBaseController {
	@Autowired
	ApplicationContext act;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value( "${SUBMITTED_CASESTUDY_FILES_PATH}" )
	private String SUBMITTED_CASESTUDY_FILES_PATH;

	@Value( "${CASESTUDY_TOPICS}" )
	private  List<String> CASESTUDY_TOPICS;
	
	@Value( "${CASESTUDY_PREVIEW_PATH}" )
	private  List<String> CASESTUDY_PREVIEW_PATH;
	
	private static final int BUFFER_SIZE = 4096;
	private final long MAX_FILE_SIZE = 5242880;
	
	@RequestMapping(value = "/caseStudy", method = {RequestMethod.GET,RequestMethod.POST})
	public ModelAndView caseStudy(HttpServletRequest request, HttpServletResponse response,Model m) {
			ArrayList<String> TopicsSubmited = new ArrayList<String>();
			String sapId = (String)request.getSession().getAttribute("userId");
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
			CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
			if(student == null){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Student Enrollment not found. Please contact Administrator.");
				return new ModelAndView("caseStudyFiles/viewCaseStudy");
			}
			StudentExamBean studentRegistrationData = dao.getExecutiveStudentRegistrationData(sapId);
			if(studentRegistrationData != null){
				//Take program and sem from Registration data and not Student data. 
				student.setProgram(studentRegistrationData.getProgram());
				student.setSem(studentRegistrationData.getSem());
			}
			if(studentRegistrationData == null){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Student Registration not found. Please contact Administrator.");
				return new ModelAndView("caseStudyFiles/viewCaseStudy");
			}
			CaseStudyExamBean bean = new CaseStudyExamBean();
			CaseStudyDao caseDAO =  (CaseStudyDao)act.getBean("caseDAO");
			List<CaseStudyExamBean> caseStudyList = caseDAO.getCaseStudyList(studentRegistrationData.getYear(),studentRegistrationData.getMonth());
			Boolean isCaseStudyLive = false;
			if(caseStudyList != null && !caseStudyList.isEmpty()){
				isCaseStudyLive = true;
				m.addAttribute("isCaseStudyLive",isCaseStudyLive);
			}else{
				m.addAttribute("isCaseStudyLive",isCaseStudyLive);
				setError(request,"Case Study Not live Currently");
				return new ModelAndView("caseStudyFiles/viewCaseStudy");
			}
			String currentSemEndDateTime = null;
			String currentSemStartDateTime = null;
			//ArrayList<String> topics = caseDAO.getCaseStudyTopics(studentRegistrationData.getYear(),studentRegistrationData.getMonth());
			
			//HashMap<String,CaseStudyBean> topicSubmissionMap = new HashMap<>();
			if(!caseStudyList.isEmpty()){
				currentSemEndDateTime = caseStudyList.get(0).getEndDate().substring(0, 19);
				currentSemStartDateTime = caseStudyList.get(0).getStartDate().substring(0, 19);
			}
				CaseStudyExamBean submittedCaseStudy = caseDAO.getCaseStudySubmissionStatus(CASESTUDY_TOPICS, sapId,studentRegistrationData.getYear(),studentRegistrationData.getMonth());
				String status = "Not Submitted";
				String lastModifiedDate = "";
				if(submittedCaseStudy.getTopic() != null ){
						TopicsSubmited.add(submittedCaseStudy.getTopic());
						status = submittedCaseStudy.getStatus();
						lastModifiedDate = submittedCaseStudy.getLastModifiedDate();
						lastModifiedDate = lastModifiedDate.replaceAll("T", " ");
						lastModifiedDate = lastModifiedDate.substring(0,19);
					
				}else{
				}
		
			int noOfQuestionFiles = 0;
			int noOfCaseSubmitted = 0;
			if(caseStudyList != null){
				noOfQuestionFiles = caseStudyList.size(); 
			}
			m.addAttribute("status", status);
			m.addAttribute("submittedCaseStudy",submittedCaseStudy);
			if(TopicsSubmited.size() == 0) {
				m.addAttribute("submitedCaseName", null);
				m.addAttribute("noOfCaseSubmitted", noOfCaseSubmitted);
			}else {
				m.addAttribute("submitedCaseName", TopicsSubmited);
				m.addAttribute("noOfCaseSubmitted", TopicsSubmited.size());
			}
			m.addAttribute("yearList", ACAD_YEAR_LIST);
			m.addAttribute("noOfQuestionFiles",noOfQuestionFiles);
			m.addAttribute("batchMonth", studentRegistrationData.getMonth());
			m.addAttribute("batchYear", studentRegistrationData.getYear());
			m.addAttribute("caseStudyTopicList", CASESTUDY_TOPICS);
			m.addAttribute("currentSemEndDateTime", currentSemEndDateTime);
			m.addAttribute("currentSemStartDateTime", currentSemStartDateTime);
			request.getSession().setAttribute("caseStudyList", caseStudyList);
			request.getSession().setAttribute("currentSemEndDateTime", currentSemEndDateTime);
			request.getSession().setAttribute("currentSemStartDateTime", currentSemStartDateTime);
			request.getSession().setAttribute("isCaseStudyLive", isCaseStudyLive);
		return new ModelAndView("caseStudyFiles/viewCaseStudy");
	}	

	@RequestMapping(value ="/viewSingleCaseStudy", method ={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView viewSingleCaseStudy(HttpServletRequest request, HttpServletResponse respnse, 
			@ModelAttribute CaseStudyExamBean caseStudyFile, Model m) {
		ModelAndView modelnView = new ModelAndView("caseStudyFiles/singleCaseStudy");
		String sapId = (String)request.getSession().getAttribute("userId");
		String currentSemEndDateTime = (String)request.getSession().getAttribute("currentSemEndDateTime");
		String currentSemStartDateTime = (String)request.getSession().getAttribute("currentSemStartDateTime");
		Boolean isCaseStudyLive = (Boolean)request.getSession().getAttribute("isCaseStudyLive");
		CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
		String status = request.getParameter("status");
		if(status.equalsIgnoreCase("Submitted")){
			CaseStudyExamBean caseStudyFileSubmitted = dao.getSubmittedCaseStudyDetails(sapId, caseStudyFile.getBatchYear(), caseStudyFile.getBatchMonth(),(String)request.getParameter("topic"));
			caseStudyFile.setStudentFilePath(caseStudyFileSubmitted.getStudentFilePath());
			caseStudyFile.setPreviewPath(caseStudyFileSubmitted.getPreviewPath());
			
		}
		if (isCaseStudyLive){
			modelnView.addObject("submissionAllowed","true");	
		}else{
			modelnView.addObject("submissionAllowed","false");
		}
		String startDate = currentSemStartDateTime;
		startDate = startDate.replaceAll("T", " ");
		caseStudyFile.setStartDate(startDate.substring(0,19));
	
		String endDate = currentSemEndDateTime;
		endDate = endDate.replaceAll("T", " ");
		caseStudyFile.setEndDate(endDate.substring(0,19));
		
		modelnView.addObject("status",status);
		modelnView.addObject("caseStudyFile",caseStudyFile);

		return modelnView;
	}
	
	
	@RequestMapping(value = "/submitCase",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView submitCase(HttpServletRequest request, HttpServletResponse response, @ModelAttribute CaseStudyExamBean caseStudyFile){

		ModelAndView modelnView = new ModelAndView("caseStudyFiles/singleCaseStudy");

		try {		
			String sapId = (String)request.getSession().getAttribute("userId");
			caseStudyFile.setSapid(sapId);
			CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
 
			StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		
			modelnView.addObject("caseStudyFile",caseStudyFile);
			modelnView.addObject("topic",(String)request.getParameter("topic"));

			if(caseStudyFile == null || caseStudyFile.getFileData() == null  ){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload: No File Selected");
				modelnView.addObject("caseStudyFile",caseStudyFile);

				return modelnView;
			}

			String fileName = caseStudyFile.getFileData().getOriginalFilename();  
			if(fileName == null || "".equals(fileName.trim()) ){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in file Upload Inner: No File Selected");
				modelnView.addObject("caseStudyFile",caseStudyFile);

				return modelnView;
			}

			String errorMessage = uploadCaseStudySubmissionFile(caseStudyFile, caseStudyFile.getBatchYear(), caseStudyFile.getBatchMonth(), sapId);

			//Check if file saved to Disk successfully & Antivirus check. Do not delete below code
			if(errorMessage == null){

				caseStudyFile.setStatus("Submitted");
				String userId = (String)request.getSession().getAttribute("userId");
				caseStudyFile.setLastModifiedBy(userId);
				caseStudyFile.setTopic((String)request.getParameter("topic"));
				caseStudyFile.setFilePath((String)request.getParameter("filePath"));
				/*caseStudyFile.setTitle((String)request.getParameter("title"));*/
				dao.saveCaseSubmissionDetails(caseStudyFile, student);
				CaseStudyExamBean bean = dao.getSubmittedCaseStudyDetails(student.getSapid(), caseStudyFile.getBatchYear(), caseStudyFile.getBatchMonth(), (String)request.getParameter("topic"));
				bean.setEndDate((String)request.getParameter("endDate"));
				bean.setStartDate((String)request.getParameter("startDate"));
				//Send Email to student
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendCaseStudyReceivedEmail(student, bean);

			}else{
				request.setAttribute("error", "true");
				//request.setAttribute("errorMessage", "Error in file Upload Outer: "+errorMessage);
				request.setAttribute("errorMessage", "Error in file Upload");
				modelnView.addObject("caseStudyFile",caseStudyFile);
				return modelnView;
			}

			request.setAttribute("success","true");
		
			String successMessage = "File Uploaded successfully. Please the cross verify the Preview of the uploaded case file. "
					+ "Take Printscreen for your records. <br>";


			request.setAttribute("successMessage",successMessage);
			modelnView.addObject("caseStudyFile",caseStudyFile);
			modelnView.addObject("status",caseStudyFile.getStatus());
			return modelnView;

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error 1:"+e.getMessage());
			modelnView.addObject("caseStudyFile",caseStudyFile);

			return modelnView;
		}
	}
	
	private String uploadCaseStudySubmissionFile(CaseStudyExamBean bean, String year, String month, String sapId) {

		String errorMessage = null;
		InputStream inputStream = null;   
		OutputStream outputStream = null;   

		CommonsMultipartFile file = bean.getFileData(); 
		String fileName = file.getOriginalFilename();   

		long fileSizeInBytes = bean.getFileData().getSize();
		if(fileSizeInBytes > MAX_FILE_SIZE){
			errorMessage = "File size exceeds 5MB. Please upload a file with size less than 1MB";
			return errorMessage;
		}

		//Replace special characters in file name
		String subject = bean.getTopic();
/*		
		subject = subject.replaceAll("'", "_");
		subject = subject.replaceAll(",", "_");
		subject = subject.replaceAll("&", "and");
		subject = subject.replaceAll(" ", "_");
		subject = subject.replaceAll(":", "");
*/

		if(!(fileName.toUpperCase().endsWith(".PDF") ) ){
			errorMessage = "File type not supported. Please upload .pdf file.";
			return errorMessage;
		}
		//Add Random number to avoid student guessing names of other student's assignment files
		fileName = sapId + "_" + subject + "_" + RandomStringUtils.randomAlphanumeric(12) + ".pdf";

		try {  
			//PDF stores first 4 letters as %PDF, which can be used to check if a file is actually a pdf file and not just going by extension
			InputStream tempInputStream = file.getInputStream();  ;
			byte[] initialbytes = new byte[4];   
			tempInputStream.read(initialbytes);

			tempInputStream.close();
			String fileType = new String(initialbytes);

			if(!"%PDF".equalsIgnoreCase(fileType)){
				errorMessage = "File is not a PDF file. Please upload .pdf file.";
				return errorMessage;
			}


			inputStream = file.getInputStream();   
			String filePath = SUBMITTED_CASESTUDY_FILES_PATH + month + year + "/" + subject + "/" + fileName;
			String previewPath = month + year + "/" + subject + "/" + fileName;
			//Check if Folder exists which is one folder per Exam (Jun2015, Dec2015 etc.) 
			File folderPath = new File(SUBMITTED_CASESTUDY_FILES_PATH  + month + year + "/" + subject);
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
			//errorMessage = "Error in uploading file for "+bean.getTopic() + " : "+ e.getMessage();
			errorMessage = "Error in uploading file for "+bean.getTopic();
			   
		}   

		return errorMessage;
	}
	
	
	
	@RequestMapping(value = "/downloadCaseStudyQuestionFile", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadStudentAssignmentFile(HttpServletRequest request, HttpServletResponse response , Model m){
		ModelAndView modelnView = new ModelAndView("downloadAssignmentFile");

		String fullPath = request.getParameter("filePath");
		//String topic = request.getParameter("subject");

		try{
			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();
			String appPath = context.getRealPath("");

			// construct the complete absolute path of the file
			//String fullPath = appPath + filePath;		
			File downloadFile = new File(fullPath);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			// get MIME type of the file
			String mimeType = context.getMimeType(fullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}

			// set content attributes for the response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			// write bytes read from the input stream into the output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
			outStream.close();
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
	
		return modelnView;
	}
	
	@RequestMapping(value = "/downloadStudentCaseStudyFile", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadStudentCaseStudyFile(HttpServletRequest request, HttpServletResponse response , Model m){
		ModelAndView modelnView = new ModelAndView("downloadAssignmentFile");

		String fullPath = request.getParameter("filePath");
		
		try{
			// get absolute path of the application
			ServletContext context = request.getSession().getServletContext();
		
			// construct the complete absolute path of the file
			//String fullPath = appPath + filePath;		
			File downloadFile = new File(fullPath);
			FileInputStream inputStream = new FileInputStream(downloadFile);

			// get MIME type of the file
			String mimeType = context.getMimeType(fullPath);
			if (mimeType == null) {
				// set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}

			// set content attributes for the response
			response.setContentType(mimeType);
			response.setContentLength((int) downloadFile.length());

			// set headers for the response
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					downloadFile.getName());
			response.setHeader(headerKey, headerValue);

			// get output stream of the response
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;

			// write bytes read from the input stream into the output stream
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			inputStream.close();
			outStream.close();
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading file.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/viewCaseStudyQuestionFiles", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewPreviousAssignments(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AssignmentFileBean searchBean){
		ModelAndView modelnView = new ModelAndView("caseStudyFiles/viewCaseStudyQuestionFiles");
		//Dummy comment
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String sapId = (String)request.getSession().getAttribute("userId");
		
		searchBean.setSapId(sapId);

		request.getSession().setAttribute("searchBean", searchBean);

		CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
		StudentExamBean studentRegistrationData = dao.getExecutiveStudentRegistrationData(sapId);
		String year = "";
		String month = "";
		if(studentRegistrationData != null){
			//Take year and month from Registration data and not Student data. 
			year = studentRegistrationData.getYear();
			month = studentRegistrationData.getMonth();
		}
		if(studentRegistrationData == null){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Student Registration not found. Please contact Administrator.");
			return new ModelAndView("caseStudyFiles/viewCaseStudy");
		}
		List<CaseStudyExamBean>  caseStudyList = dao.getCaseStudyList(year, month);

		modelnView.addObject("caseStudyList", caseStudyList);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("rowCount", caseStudyList.size());

		modelnView.addObject("yearList", ACAD_YEAR_LIST);

		if(caseStudyList == null || caseStudyList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Case Study Question Files found.");
		}
		return modelnView;
	}
	
	
}
