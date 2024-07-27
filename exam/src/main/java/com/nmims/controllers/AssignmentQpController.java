package com.nmims.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentFilesSetbean;
import com.nmims.beans.AssignmentFilesSetbeanList;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.services.QpAdmin;
import com.nmims.services.QpFaculty;
import com.nmims.services.QpReviewer;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import com.nmims.beans.ResponseBean;

@Controller
public class AssignmentQpController extends BaseController{
	
	private ArrayList<String> currentYearList = new ArrayList<String>(Arrays.asList("2014", "2015","2016", "2017" , "2018" , "2019", "2020","2021","2022","2023" )); 
	
	@Value( "${ASSIGNMENT_FILES_PATH}" )
	private String ASSIGNMENT_FILES_PATH;
	
	@Autowired
	AssignmentsDAO asignmentsDAO;
	
	@Autowired 
	private QpAdmin qpAdmin; 
	
	@Autowired 
	private QpFaculty qpFaculty; 
	
	@Autowired 
	private QpReviewer qpReviewer; 
	
	private static final int BUFFER_SIZE = 4096;
	@RequestMapping(value = "/admin/assignmentQpCreation", method = {RequestMethod.GET})
	public String assignmentQpCreation(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			return "login";
		}
		AssignmentFilesSetbean  filesSet = new AssignmentFilesSetbean();
		m.addAttribute("filesSet",filesSet);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", qpAdmin.getSubjectList());
		m.addAttribute("consumerType", asignmentsDAO.getConsumerTypeList());
		m.addAttribute("facultyMap", qpAdmin.getFacultyList());
		return "assignment/assignment_qp_creation";
	}
	@RequestMapping(value = "/saveAssignmentFacultyQpMapping", method = {RequestMethod.POST})
	public String saveAssignmentFacultyQpMapping(HttpServletRequest request, HttpServletResponse respons,
			RedirectAttributes ra,@ModelAttribute AssignmentFilesSetbean formData,Model m) {
		if(!checkSession(request, respons)){
			return "login";
		}
		AssignmentFilesSetbean response = qpAdmin.createFacultyReviewerMappingFromForm(formData);
		setFlashMessage(ra,response);
		return "redirect:/assignmentQpCreation";
	}

	@RequestMapping(value = "/uploadFacultyQpMapping", method = {RequestMethod.POST})
	public String uploadFacultyQpMapping(HttpServletRequest request,HttpServletResponse respons, 
			RedirectAttributes ra,@ModelAttribute AssignmentFilesSetbean filesSet,Model m) {
		if(!checkSession(request, respons)){
			return "login";
		}
		AssignmentFilesSetbean response = qpAdmin.createFacultyReviewerMappingFromExcel(filesSet);
		setFlashMessage(ra,response);
		return "redirect:/assignmentQpCreation";
	}
	
	@RequestMapping(value="/facultyAssignmentUpload",method=RequestMethod.GET)
	public String facultyAssignmentUpload(HttpServletRequest request, HttpServletResponse response,
			HttpServletResponse respons,@ModelAttribute AssignmentFilesSetbean filesSet,Model m) {
		String facultyId = (String)request.getSession().getAttribute("userId");
		if(!checkSession(request, respons)){
			return "login";
		}
		List<AssignmentFilesSetbean> beanList = qpFaculty.getPendingUploadList(facultyId);
		
		m.addAttribute("beanList", beanList);
		m.addAttribute("filesSet",filesSet);
		return "assignment/assignmentUpload";
	}
	//for studentportal faculty dashboard
	@RequestMapping(value = "/countOfQpNotUploaded", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity<AssignmentFilesSetbean>  countOfQpNotUploaded(HttpServletRequest request, HttpServletResponse resp,
			 @RequestParam String facultyId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		AssignmentFilesSetbean response = qpFaculty.countOfQpNotUploaded(facultyId);
		
		return new ResponseEntity<AssignmentFilesSetbean>(response, HttpStatus.OK);
	}
	@RequestMapping(value="/facultyAssignmentQpReview",method= {RequestMethod.GET,RequestMethod.POST})
	public String facultyAssignmentQpReview(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute AssignmentFilesSetbean filesSet,Model m) {
		if(!checkSession(request, response)){
			return "login";
		}
		String facultyId = (String)request.getSession().getAttribute("userId");
		List<AssignmentFilesSetbean> beanList = qpReviewer.getPendingReviewList(facultyId);
		m.addAttribute("beanList", beanList);
		m.addAttribute("filesSet",filesSet);
		AssignmentFilesSetbean feedbackbean = new AssignmentFilesSetbean();
		m.addAttribute("feedbackbean",feedbackbean);
		return "assignment/assignmentReview";
	}
	@RequestMapping(value="/sendQpFeedback",method=RequestMethod.POST)
	public String sendQpFeedback(HttpServletRequest request,
			RedirectAttributes ra ,@ModelAttribute AssignmentFilesSetbean filesSet,Model m) {
		
		String facultyId = (String)request.getSession().getAttribute("userId");
		filesSet.setReviewer(facultyId); 
		AssignmentFilesSetbean response = qpReviewer.sendFeedback(filesSet);
		setFlashMessage(ra,response);
		return "redirect:/facultyAssignmentQpReview";
		
	}
	@RequestMapping(value="/sendOverallRemark",method=RequestMethod.POST)
	public String sendOverallRemark(HttpServletRequest request,
			RedirectAttributes ra ,@ModelAttribute AssignmentFilesSetbean filesSet,Model m) {
		 
		AssignmentFilesSetbean response = qpAdmin.addRemark(filesSet);
		setFlashMessage(ra,response);
		return "redirect:/pendingUploadAndReviewList?tabindex="+filesSet.getTabindex();
		
	}
	@RequestMapping(value = "/admin/pendingUploadAndReviewList", method = {RequestMethod.GET})
	public String pendingUploadAndReviewList(HttpServletRequest request, HttpServletResponse response,
			Model m,@RequestParam String tabindex) {
		if(!checkSession(request, response)){
			return "login";
		}
		AssignmentFilesSetbean fileSet = qpAdmin.checkPendingUploadAndReviewList();
		fileSet.setTabindex(tabindex);
		AssignmentFilesSetbean feedbackbean = new AssignmentFilesSetbean();
		m.addAttribute("feedbackbean",feedbackbean);
		m.addAttribute("fileSet",fileSet);
		return "assignment/pendinguploadAndReviewList";
	}
	@RequestMapping(value="/qpApproveOrReject",method=RequestMethod.POST)
	public String qpApproveOrReject(HttpServletRequest request,
			RedirectAttributes ra,@ModelAttribute AssignmentFilesSetbean filesSet,Model m) {
		String facultyId = (String)request.getSession().getAttribute("userId");
		
		filesSet.setReviewer(facultyId);
		AssignmentFilesSetbean response = qpReviewer.approveOrRejectFile(filesSet);
		
		setFlashMessage(ra,response);
		return "redirect:/facultyAssignmentQpReview";
		
	}
	@RequestMapping(value = "/adminApproveQp", method = RequestMethod.POST)
		    public ResponseEntity<ResponseBean> adminApproveQp(@RequestBody AssignmentFilesSetbeanList fileset )
		    		throws ParseException, IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ResponseBean response = new ResponseBean();
		try {
			response = qpAdmin.adminApproveQp(fileset.getFileset()); 
		} catch (Exception e) {
			
		} 
		return new ResponseEntity<ResponseBean>(response, HttpStatus.OK);
	}
	 
	@RequestMapping(value = "/searchQpForm", method = {RequestMethod.GET,RequestMethod.POST})
    public  String searchQpForm(HttpServletRequest request, HttpServletResponse response,ModelMap m)
    { 
		if(!checkSession(request, response)){
		return "login";
	    }
		AssignmentFilesSetbean  filesSet = new AssignmentFilesSetbean();
		m.addAttribute("filesSet",filesSet);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("subjectList", qpAdmin.getSubjectList());
		m.addAttribute("consumerType", asignmentsDAO.getConsumerTypeList());
		return "assignment/searchQp"; 
	}
	@RequestMapping(value = "/searchQp", method = {RequestMethod.GET,RequestMethod.POST})
    public  String searchQp(HttpServletRequest request, HttpServletResponse response,ModelMap m,
    		@ModelAttribute AssignmentFilesSetbean fileset,RedirectAttributes ra)
    { 
		List<AssignmentFilesSetbean> data = asignmentsDAO.searchQpById(fileset);
		ra.addFlashAttribute("filesSetList", data);
		return "redirect:/searchQpForm";
		 
	}
	@RequestMapping(value = "/updateAssignmentStartDateEndDate", method = {RequestMethod.GET,RequestMethod.POST})
    public  String updateAssignmentStartDateEndDate(HttpServletRequest request,ModelMap m,
    		RedirectAttributes ra,@ModelAttribute AssignmentFilesSetbean fileset)
    { 
		AssignmentFilesSetbean response = qpAdmin.updateAssignmentStartDateEndDate(fileset);
		setFlashMessage(ra,response);
		return "redirect:/searchQpForm";
	}
	@RequestMapping(value = "/saveAssignmentQuestionsInQpTable", method = {RequestMethod.GET,RequestMethod.POST})
    public  String saveAssignmentFileInStagingTable(HttpServletRequest request,ModelMap m,
    		RedirectAttributes ra,@ModelAttribute AssignmentFilesSetbean filesSet)
    {
		//AssignmentFilesSetbean response= qpAdmin.saveAssignmentFileInStagingTable(filesSet);
		AssignmentFilesSetbean response= qpAdmin.saveAssignmentQuestionsInQpTable(filesSet);
		setFlashMessage(ra,response);
		return "redirect:/facultyAssignmentUpload";
	}
	@RequestMapping(value = "/updateQpDateForStudent", method = RequestMethod.POST)
    public  ResponseEntity<AssignmentFilesSetbean> updateQpDateForStudent(@RequestBody AssignmentFilesSetbean item,RedirectAttributes ra)
		throws ParseException, IOException { 
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		
		AssignmentFilesSetbean bean= qpAdmin.setStartDateEndDateForStudent(item);  
		return new ResponseEntity<AssignmentFilesSetbean>(bean,headers, HttpStatus.OK); 
	}
	@RequestMapping(value = "/createQpForm", method = RequestMethod.POST)
    public String  createQpForm(@ModelAttribute AssignmentFilesSetbean bean,HttpServletRequest request, HttpServletResponse response, Model m)
	{ 
		if(bean.getQpId()!=null) {
			bean.setAssignmentFilesSet(qpFaculty.getQuestionsByQpId(bean.getQpId()));  
		} 
		m.addAttribute("questionsBean",bean);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");    
		return "assignment/createQpForm";
	}
	@RequestMapping(value = "/showAsgQns", method = RequestMethod.GET)
    public String  showAsgQns(@ModelAttribute AssignmentFilesSetbean bean,HttpServletRequest request, HttpServletResponse response, Model m)
	{ 
		String qpId = request.getParameter("qpId");
		String role = request.getParameter("role");
		if(qpId==null) {
			//error msg
		}
		List<AssignmentFilesSetbean> questions=  qpFaculty.getQuestionsByQpId(qpId) ;
		m.addAttribute("questionsBean",questions);
		m.addAttribute("role",role); 
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");    
		return "assignment/previewAssignmentQp";
	}
	public void setFlashMessage(RedirectAttributes ra,AssignmentFilesSetbean response) {
		if(response.getStatus().equalsIgnoreCase("success")) {
			ra.addFlashAttribute("success","true");
			ra.addFlashAttribute("successMessage",response.getMessage());
		}else {
			ra.addFlashAttribute("error","true");
			ra.addFlashAttribute("errorMessage",response.getMessage());
		}
	}
	@RequestMapping(value = "/downloadAssignmentQpFile", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadAssignmentQpFile(HttpServletRequest request, HttpServletResponse response,@ModelAttribute AssignmentFilesSetbean bean ){
		ModelAndView modelnView = new ModelAndView("downloadAssignmentFile");
		List<AssignmentFilesSetbean> data = asignmentsDAO.findQpFileFromStagingTable(bean.getYear(),bean.getMonth(),bean.getPss_id());
		try{
			String fullPath = data.get(0).getFilePath();
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
		AssignmentFileBean searchBean = (AssignmentFileBean)request.getSession().getAttribute("searchBean");
		modelnView.addObject("searchBean",searchBean);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}
}
