package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.DocumentException;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ServiceRequestDAO;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.CertificatePDFCreator;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.services.CertificateService;

@Controller
public class CertificateController {
	
	@Autowired
	MarksheetController marksheetController;
	
	@Autowired
    CertificateService certificateService;
	
	@Autowired
	ServiceRequestDAO serviceRequestDao;
	
	@Autowired
	PassFailDAO passFailDao;
	
	@Autowired
	FileUploadHelper fileUploadHelper;
	
	@Autowired
	CertificatePDFCreator pdfCreators;
	
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(MarksheetController.class);
	
	@Value("${MARKSHEETS_PATH}")
	private String MARKSHEETS_PATH;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${STUDENT_PHOTOS_PATH}") // added to get student photo to certificate
	private String STUDENT_PHOTOS_PATH;
	
	@Value("${CERTIFICATES_PATH}")
	private String CERTIFICATES_PATH;
	
	@RequestMapping(value = "/admin/generateCertificateFromSR", method = RequestMethod.POST)
	public ModelAndView generateCertificateFromSR(HttpServletRequest request,
			HttpServletResponse response, @ModelAttribute PassFailExamBean bean) throws Exception {
		ModelAndView modelnView = new ModelAndView("certificateFromSR");
		request.getSession().setAttribute("passFailSearchBean", bean);
		try {
        if(bean.getServiceRequestIdList()==null || bean.getServiceRequestIdList().equalsIgnoreCase("")) {
        	request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please add atleast 1 Service Request Id.");
			return modelnView;
        } 

		List<MarksheetBean> studentForSRList = passFailDao.getStudentsForSR(bean);
		HashMap<String, String> mapOfSapIDAndResultDate = passFailDao
				.getMapOfSapIdAndResultDeclareDate(studentForSRList);
		
		if (studentForSRList == null || studentForSRList.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}
		request.setAttribute("logoRequired", bean.getLogoRequired());
		CertificatePDFCreator pdfCreator = new CertificatePDFCreator();

		//Commented By Prashant for creating new logic QR code on FC based on applicable programs
		/*try {
			String certificateNumberGenerated = pdfCreator.generateCertificateAndReturnCertificateNumber(
							studentForSRList, mapOfSapIDAndResultDate, request,
							marksheetController.getProgramAllDetailsMap(), marksheetController.getCentersMap(),
							MARKSHEETS_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH);
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Certificate generated successfully. Please click Download link below to get file.");
			serviceRequestDao.updateSRWithCertificateNumberAndCurrentDate(bean, certificateNumberGenerated);*/
		PDFMergerUtility merge = new PDFMergerUtility();
		String mergedFileName = CERTIFICATES_PATH+"Certificate_" + RandomStringUtils.randomAlphanumeric(5) + ".pdf";
		List<String> barcodePDFFilePathList = new ArrayList<String>();
		String certificateNumberGenerated = null;
		List<String> programCompletionProgramApplicableList = new ArrayList<String>();
		try {
			
		//Method execution time: 41 milliseconds.
		//get list of Programs who are applicable for Course Completion Badge
		programCompletionProgramApplicableList = certificateService.getProgramApplicableForProgramCompletion();
		logger.info("programCompletionProgramApplicableList size : "+programCompletionProgramApplicableList.size());
		}catch (Exception e) {
			logger.error(e.getMessage());
			
			//e.printStackTrace();
			// TODO: handle exception
		}
		List<String> SRErrorList=new ArrayList<String>();
		List<String> SRSuccessList=new ArrayList<String>();
		for (MarksheetBean student : studentForSRList) {
			try { 
			String courseCompletionBadgeUrl = "";
			boolean isQRCodeAppliable = true;
			String key = student.getProgram() + "-" + student.getPrgmStructApplicable();
			ProgramExamBean programDetails = marksheetController.getProgramAllDetailsMap().get(key);
			String program = programDetails.getProgram();
			
			if(programCompletionProgramApplicableList.contains(program)) { //watermark FC will only upload for course completion badge eligible programs student
				
				//Method execution time: 1 milliseconds.
				String userId = certificateService.getStudentUserIDCourseCompletionBadge(student.getSapid()); // Get User ID of sapid for uniquehash details
//				if(StringUtils.isBlank(userId)) {
//					request.setAttribute("error", "true");
//					request.setAttribute("errorMessage",
//						"Error in generating certificate : Error in creating UserId for program completion badge for Service Request ID - "+ student.getServiceRequestId()+", Please raise a case with details." );
//					return modelnView;
//				}
				userId = userId.trim();
				
				//Method execution time: 4 milliseconds.
				String Uniquehash = certificateService.getStudentUniquehashCourseCompletionBadge(userId);
//				if(StringUtils.isBlank(Uniquehash)) {
//					request.setAttribute("error", "true");
//					request.setAttribute("errorMessage",
//						"Error in generating certificate : Error in creating program completion badge for Service Request ID - "+ student.getServiceRequestId()+", Please raise a case with details." );
//					return modelnView;
//				}
				
				Uniquehash = Uniquehash.trim();
				
				String fileType = "Final Certificate";
				String resultDeclareDate = mapOfSapIDAndResultDate.get(student.getSapid());

				String S3FilePath = pdfCreators.generateCertificateAndReturnCertificateNumberForSingleStudentSelfMBWX(
						student, resultDeclareDate,marksheetController.getProgramAllDetailsMap(), marksheetController.getCentersMap(),
						CERTIFICATES_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH); // Generating watermark pdf and upload on S3
				
				// Save records of uploaded watermark FC pdf path and sapid, uniquehash for credential url
				//Method execution time: 2 milliseconds.
				certificateService.insertAWSFilePath(student.getSapid(), Uniquehash, fileType, S3FilePath.substring(50));

				courseCompletionBadgeUrl = SERVER_PATH+"studentportal/credentials/public/badgedetails/"+Uniquehash;
			}else {
				isQRCodeAppliable = false;
			}
			String certificateNumberAndFilePath = pdfCreators.generatePGMBACertificateAndReturnCertificateNumber(
						student, mapOfSapIDAndResultDate, request,
						programDetails, marksheetController.getCentersMap(),
						CERTIFICATES_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH,courseCompletionBadgeUrl,isQRCodeAppliable); // Generating barcode pdf
			certificateNumberGenerated = certificateNumberAndFilePath.substring(0,20); // Get only certificateNumber
			
			String barcodePDFFilePath = certificateNumberAndFilePath.substring(20); // Get only filePath of barcode pdf
			File file = new File(barcodePDFFilePath); // Loading all the pdf files we wish to merge
			merge.setDestinationFileName(mergedFileName); // Setting the destination file path
			merge.addSource(file); // Add all source files, to be merged
			barcodePDFFilePathList.add(barcodePDFFilePath);
			
			//Method execution time: 2 milliseconds.
			serviceRequestDao.updateSRWithCertificateNumberAndCurrentDate(student.getServiceRequestId(),certificateNumberGenerated);
		SRSuccessList.add(student.getServiceRequestId());
		}catch (Exception e) {
			SRErrorList.add(student.getServiceRequestId());
			//e.printStackTrace();
			logger.error(e.getMessage());
		}
		}
		logger.info("SR List size: "+studentForSRList.size());
		logger.info("SRErrorLsit Size: "+ SRErrorList.size());
		logger.info("SRSuccessList Size: "+SRSuccessList.size());
		String listString= String.join(", ",SRErrorList);
		if(!SRErrorList.isEmpty()) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error in generating certificate for Service Request ID - \""+ listString+"\", Please raise a case with details.");
		}
		if(!SRSuccessList.isEmpty()) {
		merge.mergeDocuments(); // Merge all pdf file
		request.getSession().setAttribute("fileName", mergedFileName);
		request.setAttribute("success", "true");
		request.setAttribute(
				"successMessage",
				"Certificate generated successfully. Please click Download link below to get file.");
		}
		for (String barcodePDFFilePath : barcodePDFFilePathList) {
			fileUploadHelper.deleteFileFromLocal(barcodePDFFilePath);
		}

		} catch (Exception e) {
//			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error(e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating certificate." + e.getMessage());
		}

		return modelnView;
	}

	 /** 
	  * Genarates the certificates for Service Request raised by user
	  * @param request HttpServletRequest
	  * @param response HttpServletResponse
	  * @param bean PassFailExamBean 
	  */
		@RequestMapping(value = "/admin/generateSrCertificate", method = { RequestMethod.GET,
				RequestMethod.POST })
		public @ResponseBody ResponseEntity<HashMap> generateSrCertificate(HttpServletRequest request, 
			   @NotBlank(message = "serviceRequestId cannot be blank") @RequestParam Integer srId, @RequestParam String sapId) throws Exception {
			try {
				HashMap<String, String> map = certificateService.generateBonafideCertificate(request,srId,sapId,
						marksheetController.getProgramAllDetailsMap(), MARKSHEETS_PATH);
				return new ResponseEntity<HashMap>(map, HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<HashMap>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		/** 
		  * Downloads the pdf for SR stored locally
		  * @param request HttpServletRequest
		  * @param response HttpServletResponse
		  * @param m Model 
		  */
		@RequestMapping(value = "/student/download", method = { RequestMethod.GET,
				RequestMethod.POST })
		public void downloadX(HttpServletRequest request,
				HttpServletResponse response, Model m) {
			logger.info("Add New Company Page");

			try {
				String fileName = (String) request.getSession().getAttribute(
						"fileName");


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

}