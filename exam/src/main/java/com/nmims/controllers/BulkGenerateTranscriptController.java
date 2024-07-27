
package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.BulkTranscriptGenerationBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ServiceRequestDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.helpers.TranscriptPDFCreator;
import com.nmims.services.CertificateService;
import com.nmims.services.MarksheetService;
import com.nmims.services.StudentService;
import com.nmims.services.impl.TranscriptGenerationServiceInterface;

@Controller
public class BulkGenerateTranscriptController {
	private static final Logger logger = LoggerFactory.getLogger(BulkGenerateTranscriptController.class);

	@Autowired
	PassFailDAO passFailDao;
	@Autowired
	TranscriptGenerationServiceInterface transcriptService;
	
	@Value("${STUDENT_PHOTOS_PATH}") // added to get student photo to certificate
	private String STUDENT_PHOTOS_PATH;
	
	@Autowired
	TranscriptController transcriptController ;
	
	@Autowired
	MarksheetService mservice;
	
	@Autowired
	MarksheetController marksheetController;
	
	
	@Autowired
	ApplicationContext act;
	
//	@Autowired
//	ServiceRequestDAO serviceRequestDao;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Autowired
    CertificateService certificateService;
	
	@Autowired
	FileUploadHelper fileUploadHelper;
	
	@Value( "${TRANSCRIPT_PATH}" )
	private String TRANSCRIPT_PATH;
	
	
//	@Value("${CERTIFICATES_PATH}")
//	private String CERTIFICATES_PATH;
	
	
	@RequestMapping(value = "/admin/bulkTranscriptForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String transcriptForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		StudentExamBean studentExam = new StudentExamBean();
		m.addAttribute("student",studentExam);

		return "bulkTranscriptSheet";
	}
	@RequestMapping(value = "/admin/generateBulkTranscriptFromSR", method = RequestMethod.POST)
	public ModelAndView generateBulkCertificateFromSR(HttpServletRequest request,
			HttpServletResponse response, @ModelAttribute("student") StudentExamBean bean) throws Exception {
		ModelAndView modelnView = new ModelAndView("bulkTranscriptSheet");
		try {
			//set admin login
			bean.setFromAdmin("Y");
			
			BulkTranscriptGenerationBean generatedTranscript= transcriptService.generateBulkTranscript(bean);
			logger.info("Generated Trancript :- {}",generatedTranscript);
			// 1) move below logic into service 
			//2) create a method in service which returns bean which has all the attributes that will be added to model and view 
			//validate service request id's
//		bulkTranscript.checkForServiceRequestIdPresent(bean.getServiceRequestIdList());
		//remove this 
//		bulkTranscript.setBeanData(bean);
		
		//List<String> items =bulkTranscript.convertCommaSepratedToList(bean.getServiceRequestIdList());
		//List<String> SRErrorList =bulkTranscript.addingErrorSridsInList(items);
//		String  successList= bulkTranscript.generateCommaSepratedSuccessfullList(items,SRErrorList );
//		List<String> SRSuccessList= bulkTranscript.convertCommaSepratedToList(successList);
		//get transcript data by sr id
//		List<MarksheetBean> studentForSRList = bulkTranscript.checkDataOnSrId(successList);
		//create bean for instead of getter and setters
//		List<String> errorwhileCreatingSuccessfull = bulkTranscript.createTranscript(studentForSRList);
		String listString= String.join(", ",generatedTranscript.getSRErrorList());
		if(generatedTranscript.getSRErrorList().isEmpty()) {
			logger.info("Srid Error List {}",generatedTranscript.getSRErrorList());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error in generating Transcripts for Service Request ID - \""+ listString+"\", Please raise a case with details.");
		}
		if(!generatedTranscript.getSRSuccessList().isEmpty()) {
		request.getSession().setAttribute("BulkfileName", generatedTranscript.getMergedFileName());
		request.setAttribute("success", "true");
		request.setAttribute(
				"successMessage",
				"Transcript generated successfully. Please click Download link below to get file.");
		}
		 if (CollectionUtils.isEmpty(generatedTranscript.getBarcodePDFFilePathList())) {
			 throw new Exception("Transcript Not Applicable ");
			
		}
		for (String barcodePDFFilePath : generatedTranscript.getBarcodePDFFilePathList()) {
			fileUploadHelper.deleteFileFromLocal(barcodePDFFilePath);
		}

		} catch (Exception e) {
			// TODO Auto-generated catch block
		e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating Transcript." + e.getMessage());
		}
		return modelnView;
	}
	@RequestMapping(value = "/admin/bulkdownload", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void download(HttpServletRequest request,
			HttpServletResponse response, Model m) {
		try {
			String fileName = (String) request.getSession().getAttribute(
					"BulkfileName");


			File fileToDownload = new File(fileName);
			InputStream inputStream = new FileInputStream(fileToDownload);
			response.setContentType("application/pdf");
			response.setHeader(
					"Content-Disposition",
					"attachment; filename="
							+ fileName.substring(fileName.lastIndexOf("/") + 1,
									fileName.length()));
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();

		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Unable to download file.");
		}
	}
//	public HashMap<String, ProgramExamBean> getAllProgramMap(){
//		if(this.programDetailsMap == null || this.programDetailsMap.size() == 0){
//			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
//			this.programDetailsMap = dao.getProgramMap();
//		}
//		return programDetailsMap;
//	}
//	private String generateCommaSeparatedList(String sapIdList) {
//		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n)+", ",");
//		if(commaSeparatedList.endsWith(",")){
//			commaSeparatedList = commaSeparatedList.substring(0,  commaSeparatedList.length()-1);
//		}
//		return commaSeparatedList;
//	}
//	public ArrayList<StudentExamBean> getLateralStudentsData(ExamBookingDAO eDao, StudentExamBean lateralStudent){
//		ArrayList<StudentExamBean> lateralStudentList = new ArrayList<StudentExamBean>();
//		StudentExamBean prevLateralStudent = eDao.getSingleStudentsData(lateralStudent.getPreviousStudentId());
//		lateralStudentList.add(prevLateralStudent);
//		if(prevLateralStudent.getIsLateral().equalsIgnoreCase("Y") ) { // check again if lateral student is again lateral or not
//			List<StudentExamBean> lateralDataList2 = getLateralStudentsData(eDao,prevLateralStudent); // if yes then again repeat the method
//			lateralStudentList.addAll(lateralDataList2);
//		}
//		return lateralStudentList;
//	}
	
}

