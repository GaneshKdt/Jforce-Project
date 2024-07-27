package com.nmims.stratergies.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.services.ServiceRequestService;
import com.nmims.services.StudentService;
import com.nmims.stratergies.CheckFinalCertificateEligibilityInterface;

@Service("checkFinalCertificateEligibility")
public class CheckFinalCertificateEligibility  implements CheckFinalCertificateEligibilityInterface {
	
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired 
	private PortalDao portalDAO;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	ServiceRequestService servReqServ;
	
	private final int SECOND_DIPLOMA_FEE = 1000;
	
	private static final Logger logger = LoggerFactory.getLogger(CheckFinalCertificateEligibility.class);

	@Override
	public ServiceRequestStudentPortal checkFinalCertificateEligibility(ServiceRequestStudentPortal sr) throws Exception {
		// TODO Auto-generated method stub
		sr.setWantAtAddress("No");
		
		String sapid = sr.getSapId();
		sr.setSapId(sapid);
		logger.info("sapid: "+sapid);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		String isLateral ="";
		String cpsi="";
		String previousStudentId="";
		logger.info("student: "+student.toString());
		try {
			 isLateral = student.getIsLateral();
			 cpsi = student.getConsumerProgramStructureId();
			 previousStudentId = student.getPreviousStudentId();
			 logger.info("Previous Students Id "+previousStudentId);
			 if(student.getConsumerProgramStructureId().isEmpty() || student.getConsumerProgramStructureId()==null) {
				 throw new Exception("Unable to find masterKey.");
				}
			 if(student.getIsLateral().isEmpty() || student.getIsLateral()==null) {
				 isLateral="N";
				}
			
		} catch(Exception e) {
			
			
			
			
			
		
			//setError(request, "Unable to find previous student master key/islateral details");
			 //response.put("error", "Unable to find masterKey.");	
		
			 throw new Exception("Unable to find previous student master key/islateral details.");

			//return new ModelAndView("serviceRequest/selectSR");
		
		}

		ArrayList<String> applicableSubjects = new ArrayList<>();
		applicableSubjects = serviceRequestDao.getApplicableSubjectNew(cpsi);
		logger.info("Applicable subjects = "+applicableSubjects.size());
		if(applicableSubjects.size() ==  0 ) {
			throw new Exception("No Applicable subjects found!");
			}
		List<String> subjectsCleared = serviceRequestDao.getSubjectsClearedCurrentProgramNew(sapid, isLateral,cpsi);
		
		int numberOfsubjectsCleared =subjectsCleared.size();
		if("Y".equalsIgnoreCase(isLateral)) {
			if(student.getPreviousStudentId().isEmpty() || student.getPreviousStudentId()==null) {
				throw new Exception("Unable to find previous student Id.");

			}
			List<String> waivedOffSubjectsbefore =  studentService.mgetWaivedOffSubjects(student);
			List<String> waivedOffSubjects =new ArrayList<String>();
			if(waivedOffSubjectsbefore!=null) {
	            if(student.getPrgmStructApplicable().equalsIgnoreCase("Jul2019") && waivedOffSubjectsbefore.contains("Strategic Management")) {
	            	waivedOffSubjectsbefore.add("Decision Science");
	            }
	           
	            for(String subject : waivedOffSubjectsbefore) {
	                if(!waivedOffSubjects.contains(subject)){ 
	                    waivedOffSubjects.add(subject); 
	                }
	            }
			}
			logger.info("Waived subjects = "+waivedOffSubjects.size());
			int waivedOffSubjectsCount = 0;
			for(String str:waivedOffSubjects) {
				if(applicableSubjects.contains(str) && !subjectsCleared.contains(str)) {
					waivedOffSubjectsCount++;
				}
			}
			numberOfsubjectsCleared+=waivedOffSubjectsCount;
		}
		
		
	int noOfSubjectsToClearProgram = 0;
		
		HashMap<String,ProgramsStudentPortalBean> programInfoList = serviceRequestDao.getProgramDetails();
		logger.info("programInfoList-------------> "+programInfoList);
		ProgramsStudentPortalBean bean = programInfoList.get(student.getConsumerProgramStructureId());
		logger.info(" EXAM MODE "+student.getExamMode());
		noOfSubjectsToClearProgram = Integer.parseInt(bean.getNoOfSubjectsToClear().trim());
		logger.info("numberOfsubjectsCleared = "+numberOfsubjectsCleared);
		logger.info("noOfSubjectsToClearProgram = "+noOfSubjectsToClearProgram);
		
		//for exit student
		if(student.getProgramStatus()!=null && student.getProgramStatus().equals("Program Withdrawal")) {
			boolean closedSr = serviceRequestDao.getStudentsClosedExitSR(sapid);
			if(!closedSr) {
				throw new Exception( "Please raise Exit Program Service Request first. If you already raised please wait for Approval");
			}else {
				numberOfsubjectsCleared = noOfSubjectsToClearProgram;
				}
//			ServiceRequestStudentPortal serReq = servReqServ.checkIfStudentApplicableForExitProgram(student);
//			if(serReq.getIsCertificate()) {
//					numberOfsubjectsCleared = noOfSubjectsToClearProgram;
//			} -->Commented By Shailesh B'coz there is no required to check again exit Program eligibility after closed by admin.
		}
		if (numberOfsubjectsCleared != noOfSubjectsToClearProgram) {
			throw new Exception("You have not yet cleared all subjects!");
		} else {
			ModelAndView modelnView = new ModelAndView("serviceRequest/diplomaConfirmation");
			modelnView.addObject("sr", sr);
			modelnView.addObject("usersapId",sr.getSapId());
			int diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
			logger.info("diplomaIssuedCount = " + diplomaIssuedCount);
			if (diplomaIssuedCount >= 1) {
				modelnView.addObject("charges", SECOND_DIPLOMA_FEE);
				modelnView.addObject("duplicateDiploma", "true");
			} else {
				modelnView.addObject("charges", 0);
				modelnView.addObject("duplicateDiploma", "false");
			}

			return sr;
		}	
	}

	private List<String> checkIfWaivedOff(StudentStudentPortalBean student, PortalDao pDao, HttpServletRequest request) {
		//Check if student is lateral student and has done program with NGASCE in past. In this case his passed subjects will become waived off subjects
		//Course Waiver is not applicable for Jul2009 program Structure Students
//		ArrayList<String> waivedOffSubjects = new ArrayList<String>();
//		if(student.getPreviousStudentId() != null && !"".equals(student.getPreviousStudentId()) && !"Jul2009".equals(student.getPrgmStructApplicable()) && student.getIsLateral().equalsIgnoreCase("Y")){
//
//			waivedOffSubjects = pDao.getAllPreviousSubjectNamesForSapid(student.getPreviousStudentId());
//			if(waivedOffSubjects.contains("Business Statistics")){
//				if("EPBM".equalsIgnoreCase(student.getProgram())){
//					waivedOffSubjects.remove("Business Statistics");
//					waivedOffSubjects.add("Business Statistics- EP");
//				}
//				if("MPDV".equalsIgnoreCase(student.getProgram())){
//					waivedOffSubjects.remove("Business Statistics");
//					waivedOffSubjects.add("Business Statistics- MP");
//				}
//			}
//
//			if(waivedOffSubjects.contains("Business Communication and Etiquette")){
//				waivedOffSubjects.add("Business Communication");
//			}
//			
//			return  waivedOffSubjects;
//		}
		return studentService.mgetWaivedOffSubjects(student);
	}
	
	
}
