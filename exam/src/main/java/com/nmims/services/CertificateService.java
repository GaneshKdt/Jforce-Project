package com.nmims.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.DocumentException;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.ServiceRequestBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ServiceRequestDAO;
import com.nmims.daos.StudentDAO;
import com.nmims.helpers.AmazonS3Helper;
import com.nmims.helpers.CertificatePDFCreator;

@Service
public class CertificateService {
	
	@Value("${AWS_UPLOAD_URL}")
	private String AWS_UPLOAD_URL;
	
	@Value("${AWS_SR_FILES_BUCKET}")
	private String srBucket;
	
	@Value("${SR_FILES_S3_PATH}")
	private String SR_FILES_S3_PATH;
	
	@Autowired
	AmazonS3Helper awsHelper;
	
	@Autowired
	StudentDAO studentDao;
	
	@Autowired
	ServiceRequestDAO serviceRequestDao;

//	@Autowired(required = false)
//	ApplicationContext act;
	
	private static final Logger logger = LoggerFactory.getLogger(CertificateService.class);
		
	private static final String IssuanceOfBonafide = "IssuanceOfBonafide/";
	
	private static final String EBONAFIDE_SR_DOCUMENT_TYPE = "SR E-Bonafide";
	
	public HashMap<String, String> generateBonafideCertificate(HttpServletRequest request,
			Integer srId, String sapId, HashMap<String, ProgramExamBean> programAllDetailsMap, String mARKSHEETS_PATH) throws Exception {
		HashMap<String, String> map = new HashMap<>();
		HashMap<String, String> certificateMap = new HashMap<>();
		
//		String userId = (String) request.getSession().getAttribute("userId"); 		//getting userId null
		StudentExamBean studentInfo = studentDao.getStudentInfo(sapId);
		ServiceRequestBean serviceRequest = serviceRequestDao.getServiceRequestForSR(srId);
		String exitWithdrawalStatus = serviceRequestDao.getExitWithdrawalStatusForSR(sapId);
		CertificatePDFCreator pdfCreator = new CertificatePDFCreator();
		try {
			certificateMap = pdfCreator.generateSRCertificateAndReturnCertificateNumber(studentInfo,serviceRequest,
					programAllDetailsMap, mARKSHEETS_PATH, exitWithdrawalStatus,true);
			String certificateNumberGenerated = (String) certificateMap.get("certificateNumber");
			serviceRequestDao.updateSRWithCertificateNumberAndCurrentDate(srId, certificateNumberGenerated);
			String fullPath = certificateMap.get("filePath") + certificateMap.get("fileName");
			certificateMap.put("fullPath", fullPath);
			String fileName = certificateMap.get("fileName");
			String filePath=null;
				HashMap<String, String> srUpload = awsHelper.uploadLocalFile(fullPath, IssuanceOfBonafide + fileName,
						srBucket, IssuanceOfBonafide);
				if ("success".equals(srUpload.get("status"))) {
					filePath = srUpload.get("url");
					String filePathName = filePath.replaceAll(SR_FILES_S3_PATH, "");
					serviceRequestDao.insertSrGeneratedDocumentFilePath(sapId, EBONAFIDE_SR_DOCUMENT_TYPE, filePathName, "SR Admin");
				} else {
					logger.error("failed to upload bonafide certificate pdf to AWS " + srUpload.get("url"));
					throw new RuntimeException(
							"failed to upload bonafide certificate pdf to AWS" + srUpload.get("url"));
			} 
				map.put("awsPath", filePath);
				map.put("localPath", fullPath);
				return map;
		} catch (DocumentException | IOException | ParseException e) {
//			e.printStackTrace();
			logger.error("Error in generating Bonafide certificate" + e.getMessage());
			throw new RuntimeException("Error in generating certificate." + e.getMessage());
		}
	}

	public ModelAndView generateBonafideCertificateForSR(HttpServletRequest request,Integer srId, String sapId
			, HashMap<String, ProgramExamBean> programAllDetailsMap, String mARKSHEETS_PATH) throws Exception {

		HashMap<String, String> certificateMap = new HashMap<>();
		String fullPath = null;
		ModelAndView modelnView = new ModelAndView("certificateFromSR");
//		request.getSession().setAttribute("passFailSearchBean", bean);
//		request.getSession().getAttribute("sr");
		if (srId == null ) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please add atleast 1 Service Request Id.");
			return modelnView;
		}
		
		String userId = (String) request.getSession().getAttribute("userId");
		StudentExamBean studentInfo = studentDao.getStudentInfo(sapId);
		ServiceRequestBean serviceRequest = serviceRequestDao.getServiceRequestForSR(srId);
		String exitWithdrawalStatus = serviceRequestDao.getExitWithdrawalStatusForSR(sapId);
		request.setAttribute("logoRequired", "Y");
		CertificatePDFCreator pdfCreator = new CertificatePDFCreator();

		try {
			certificateMap = pdfCreator.generateSRCertificateAndReturnCertificateNumber(studentInfo,serviceRequest,
					programAllDetailsMap, mARKSHEETS_PATH, exitWithdrawalStatus,true);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage",
					"Certificate generated successfully. Please click Download link below to get file.");
			String certificateNumberGenerated = (String) certificateMap.get("certificateNumber");
			try {
				serviceRequestDao.updateSRWithCertificateNumberAndCurrentDate(srId, certificateNumberGenerated);
			} catch (Exception e) {
				logger.error(
						"failed to update CertificateNumberAndCurrentDate for id " + srId);
			}
			String documentFor = "Document for Issuance Of Bonafide";
			fullPath = certificateMap.get("filePath") + certificateMap.get("fileName");
			certificateMap.put("fullPath", fullPath);
			request.getSession().setAttribute("fileName",fullPath);
			serviceRequestDao.insertSrGeneratedDocumentFilePath(String.valueOf(srId), EBONAFIDE_SR_DOCUMENT_TYPE, fullPath, userId);
			logger.info("Description of document " + documentFor);
		} catch (DocumentException | IOException | ParseException e) {
//			e.printStackTrace();
			logger.error("Error in generating Bonafide certificate" + e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating certificate." + e.getMessage());
		}
		return modelnView;
	};
	
	public String getStudentUserIDCourseCompletionBadge(String sapid) {
		return serviceRequestDao.getStudentUserIDCourseCompletionBadge(sapid);
	}
	
	public String getStudentUniquehashCourseCompletionBadge(String userId) {
		return serviceRequestDao.getStudentUniquehashCourseCompletionBadge(userId);
	}
	
	public boolean insertAWSFilePath(String sapid,String Uniquehash,String fileType,String S3FilePath) {
		return serviceRequestDao.insertAWSFilePath(sapid, Uniquehash, fileType, S3FilePath);
	}
	
	public List<String> getProgramApplicableForProgramCompletion() throws Exception{
		return serviceRequestDao.getProgramApplicableForProgramCompletion();
	}
}
