package com.nmims.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.Chunk;

import com.itextpdf.text.DocumentException;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.EmbaMarksheetBean;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;

import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.LinkedInAddCertToProfileExamBean;
import com.nmims.beans.MBAWXExamResultForSubject;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.Specialisation;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.SpecialisationDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.CertificatePDFCreator;
import com.nmims.interfaces.DissertationGradesheet_TranscriptService;
import com.nmims.services.MarksheetService;
import com.nmims.services.SpecialisationService;
import com.nmims.util.SubjectsCerditsConfiguration;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class MarksheetRESTController { 
	
	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	MarksheetService msService;
	
	@Autowired
	ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Autowired
	CertificatePDFCreator pdfCreator;
	
	@Autowired
	SpecialisationService specialisationService;
	
	@Autowired
	DissertationGradesheet_TranscriptService dissertationService;
	
	private static final Logger logger = LoggerFactory.getLogger(MarksheetController.class);
	
	private String examBookingErrorURLWeb = "timeline/sRPaymentFailure";
//	private String examBookingFailURLWeb = "timeline/examBookingError";
	private String examBookingSuccessURLWeb = "timeline/examBookingSuccess";
	
	private String examBookingErrorURLMobile = "embaPaymentError";
	private String examBookingFailURLMobile = "embaExamBookingPaymentFailure";
	private String examBookingSuccessURLMobile = "embaExamBookingPaymentSuccess";
	
	private final static int  IA_MASTER_DISSERTATION_MASTER_KEY = 131;
	
	private final static int IA_MASTER_DISSERTATION_Q7_SEM = 7;
	
	private final static int IA_MASTER_DISSERTATION_Q8_SEM = 8;
	
	private final static String MSCAIML = "M.Sc. (AI & ML Ops)";
	
	private static final Logger finalCertificateLogger = LoggerFactory.getLogger("finalCertificate");
	
	@Value("${MARKSHEETS_PATH}")
	private String MARKSHEETS_PATH;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${STUDENT_PHOTOS_PATH}") // added to get student photo to certificate
	private String STUDENT_PHOTOS_PATH;
	
	@Value("${CERTIFICATES_PATH}")
	private String CERTIFICATES_PATH;
	
	private ArrayList<CenterExamBean> centers = null;
	private HashMap<String, ProgramExamBean> programMap = null;
	private HashMap<String, CenterExamBean> centersMap = null;
	private ArrayList<String> programList = null;
	private ArrayList<String> subjectList = null;
	private HashMap<String, String> programCodeNameMap = null;
	
	public String RefreshCache() {
		subjectList = null;
		getSubjectList();
		
		programList = null;
		getProgramList();
		
		programCodeNameMap = null;
		getProgramMap();
		
		programMap = null;
		getProgramAllDetailsMap();
		
		return null;
	}
	
	public HashMap<String, String> getProgramMap() {
		if (this.programCodeNameMap == null
				|| this.programCodeNameMap.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act
					.getBean("studentMarksDAO");
			this.programCodeNameMap = dao.getProgramDetails();
		}
		return programCodeNameMap;
	}
	
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null || this.subjectList.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act
					.getBean("studentMarksDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}

	public ArrayList<String> getProgramList() {
		if (this.programList == null || this.programList.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act
					.getBean("studentMarksDAO");
			this.programList = dao.getAllPrograms();
		}
		return programList;
	}
	public HashMap<String, ProgramExamBean> getProgramAllDetailsMap() {
		if (this.programMap == null || this.programMap.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act
					.getBean("studentMarksDAO");
			this.programMap = dao.getProgramMap();
		}
		return programMap;
	}
	
	public HashMap<String, CenterExamBean> getCentersMap() {
		// if(this.centers == null || this.centers.size() == 0){
		StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		this.centers = dao.getAllCenters();
		// }
		centersMap = new HashMap<String, CenterExamBean>();
		for (int i = 0; i < centers.size(); i++) {
			CenterExamBean bean = centers.get(i);
			centersMap.put(bean.getCenterCode(), bean);
		}
		return centersMap;
	}
	
	
	@PostMapping(value = "/generateMarksheetPreviewFromSR_MBAWX")
	public ResponseEntity<HashMap<String, List>> generateMarksheetPreviewFromSR_MBAWX(
		  @RequestBody EmbaPassFailBean bean) throws UnsupportedEncodingException {
		
		
		HashMap<String, List> response = new  HashMap<String, List>();
		List error = 	new ArrayList<>();
		List errorMessage = 	new ArrayList<>();
		List credits = 	new ArrayList<>();
		
		boolean hasAppearedForExamForGivenSemMonthYear = false;
		
		//to stop to throw error of you have not appeared for Q8 as it is not in mba.passFail table
		if(IA_MASTER_DISSERTATION_Q8_SEM  == Integer.parseInt(bean.getSem())) {
			hasAppearedForExamForGivenSemMonthYear = dissertationService.hasAppearedForQ8(bean.getSapid());
		}else {
		 hasAppearedForExamForGivenSemMonthYear = examsAssessmentsDAO
				.hasAppearedForExamForGivenSemMonthYearPreviewMarksheet_MBAWX(bean);
		}
		
		if (hasAppearedForExamForGivenSemMonthYear == false) {
			error.add("true");
			errorMessage.add("You have not appeared for  sem "+bean.getSem()+"- "+bean.getMonth()+"- "+bean.getYear());
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		
		List<EmbaPassFailBean> passFailDataList = new ArrayList<EmbaPassFailBean>();
		List<EmbaPassFailBean> passFailDataListAllSem = new ArrayList<EmbaPassFailBean>();


		try {
			passFailDataListAllSem = examsAssessmentsDAO.getEmbaPassFailBySapidTermMonthYearAllSem(bean.getSapid(), bean.getSem(), bean.getMonth(), bean.getYear());
			
			//Getting Q7 result
			if(IA_MASTER_DISSERTATION_Q7_SEM == Integer.parseInt(bean.getSem())) {
				EmbaPassFailBean  passFailBean = dissertationService.getPassFailForQ7(bean.getSapid());
				if(null!=passFailBean && !StringUtils.isEmpty(passFailBean.getGrade())) {
					passFailBean.setConsumerProgramStructureId(IA_MASTER_DISSERTATION_MASTER_KEY);
					passFailBean.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q7_SEM));
					passFailDataListAllSem.add(passFailBean);
				}
			}
			//Getting Q8 result
			if(IA_MASTER_DISSERTATION_Q8_SEM == Integer.parseInt(bean.getSem())) {
				//getting Q7 result in Q8 for CGPA calculation
				EmbaPassFailBean  passFailBeanforQ7 = dissertationService.getPassFailForQ7(bean.getSapid());
				EmbaPassFailBean passFailBean = dissertationService.getPassFailForQ8(bean.getSapid());
				if(null!=passFailBean && !StringUtils.isEmpty(passFailBean.getGrade()) && null!=passFailBeanforQ7) {
					passFailBeanforQ7.setConsumerProgramStructureId(IA_MASTER_DISSERTATION_MASTER_KEY);
					passFailBeanforQ7.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q7_SEM));
					passFailBean.setConsumerProgramStructureId(IA_MASTER_DISSERTATION_MASTER_KEY);
					passFailBean.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q8_SEM));
					passFailDataListAllSem.add(passFailBean);
					passFailDataListAllSem.add(passFailBeanforQ7);
				}
			}
		}catch(Exception e) {
			
		}
	
		
		try {
			passFailDataList = examsAssessmentsDAO.getEmbaPassFailBySapidTermMonthYear(bean.getSapid(), bean.getSem(), bean.getMonth(),bean.getYear());
			//Getting Q7 result
				if(IA_MASTER_DISSERTATION_Q7_SEM == Integer.parseInt(bean.getSem())) {
					EmbaPassFailBean  passFailBean = dissertationService.getPassFailForQ7(bean.getSapid());
					if(null!=passFailBean && !StringUtils.isEmpty(passFailBean.getGrade())) {
						passFailBean.setConsumerProgramStructureId(IA_MASTER_DISSERTATION_MASTER_KEY);
						passFailBean.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q7_SEM));			
						passFailDataList.add(passFailBean);
					}
				}
				if(IA_MASTER_DISSERTATION_Q8_SEM == Integer.parseInt(bean.getSem())) {
					//getting Q8 result
					EmbaPassFailBean passFailBean = dissertationService.getPassFailForQ8(bean.getSapid());
					if(null!=passFailBean && !StringUtils.isEmpty(passFailBean.getGrade())) {
						
						passFailBean.setConsumerProgramStructureId(IA_MASTER_DISSERTATION_MASTER_KEY);
						passFailBean.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q8_SEM));
						passFailDataList.add(passFailBean);
						
					}
				}
			

			//passFailDataList = examsAssessmentsDAO.getEmbaPassFailBySapid(bean.getSapid(), bean.getSem());
			int count = 1;
			double gradePoint = 0.0;
			double gradeCalculation = 0.0;
			double total = 0.0;
			int passCount = 0;
			double sumOftotalCredits = 0.0;
			for (EmbaPassFailBean e : passFailDataList) {	
				if(e.getTimeboundId() != null) {
					gradePoint = Float.parseFloat(e.getPoints());
					
					if(e.getPrgm_sem_subj_id() == 1883) { // Added by Abhay for MBA - WX Subject
						sumOftotalCredits = sumOftotalCredits + 20;
						gradeCalculation = gradePoint * 20;
						e.setCredits(20);
					}else if(e.getPrgm_sem_subj_id() == 2351) { // Added by Abhay for New Program Structure of MBA - WX Capstone Project
						sumOftotalCredits = sumOftotalCredits + 12;
						gradeCalculation = gradePoint * 12;
						e.setCredits(12);
					}
					/*Commented by Siddheshwar_Khanse as subject credits getting dynamically 
					 else if(e.getPrgm_sem_subj_id() == 1959) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
						sumOftotalCredits = sumOftotalCredits + 5;
						gradeCalculation = gradePoint * 5;
						e.setCredits(5);
					}else if(e.getPrgm_sem_subj_id() == 1961) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
						sumOftotalCredits = sumOftotalCredits + 3;
						gradeCalculation = gradePoint * 3;
						e.setCredits(3);
					}*/  
					else if(e.getConsumerProgramStructureId()==131) {
						double subjectCredits = SubjectsCerditsConfiguration.getMSCAIOpsSubjectCredits(e.getSubject());
						sumOftotalCredits = sumOftotalCredits + subjectCredits;
						gradeCalculation = gradePoint * subjectCredits;
						e.setCredits(subjectCredits);
					}
					else if(e.getConsumerProgramStructureId()==154 || e.getConsumerProgramStructureId()==155 || e.getConsumerProgramStructureId()==158) {
						double subjectCredits = SubjectsCerditsConfiguration.getMSCAISubjectCredits(e.getSubject());
						sumOftotalCredits = sumOftotalCredits + subjectCredits;
						gradeCalculation = gradePoint * subjectCredits;
						e.setCredits(subjectCredits);
					}
					else if(e.getConsumerProgramStructureId()==160) {
						double subjectCredits = SubjectsCerditsConfiguration.getMBAWXSubjectCredits(e.getPrgm_sem_subj_id());
						sumOftotalCredits = sumOftotalCredits + subjectCredits;
						gradeCalculation = gradePoint * subjectCredits;
						e.setCredits(subjectCredits);
					}
					else {
						sumOftotalCredits = sumOftotalCredits + 4;
						gradeCalculation = gradePoint * 4;
						e.setCredits(4);
					}
					
					total = total + gradeCalculation;			
					if(e.getIsPass().equals("Y")) {
						passCount++;
					}
					
					count++;
				}
				
			}	 
			
//			int sum = passFailDataList.size();		
//			int sumOftotalCredits = sum * 4;
			double gpa = total/sumOftotalCredits;
			double cgpa = 0.0;

			if("1".equalsIgnoreCase(bean.getSem())) {
				cgpa = gpa;
				credits.add(String.format("%,.2f", gpa));
				credits.add(String.format("%,.2f", cgpa));
				response.put("credits", credits);
			} else {
			double cgradeCalculation = 0.0;
			double ctotal = 0.0;
			double cgradePoint = 0.0;
//			int csum = passFailDataListAllSem.size();		
			double csumOftotalCredits = 0.0;
			
			
			for (EmbaPassFailBean e : passFailDataListAllSem) {		
				cgradePoint = Float.parseFloat(e.getPoints());
				
				/*
				 * if("Capstone Project".equalsIgnoreCase(e.getSubject())) { cgradeCalculation =
				 * cgradePoint * 20; } else { cgradeCalculation = cgradePoint * 4; }
				 */		
				
				 if(e.getPrgm_sem_subj_id() == 1883) { // Added by Abhay for MBA - WX Subject
					    csumOftotalCredits = csumOftotalCredits + 20;
						cgradeCalculation = cgradePoint * 20;
					}else if(e.getPrgm_sem_subj_id() == 2351) { // Added by Abhay for New Program Structure of MBA - WX Capstone Project
						csumOftotalCredits = csumOftotalCredits + 12;
						cgradeCalculation = cgradePoint * 12;
					}
					/*Commented by Siddheshwar_Khanse as subject credits getting dynamically
					 else if(e.getPrgm_sem_subj_id() == 1959) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
						csumOftotalCredits = csumOftotalCredits + 5;
						cgradeCalculation = cgradePoint * 5;
					}else if(e.getPrgm_sem_subj_id() == 1961) { // Added by Abhay for M.Sc. (AI & ML Ops) Subject
						csumOftotalCredits = csumOftotalCredits + 3;
						cgradeCalculation = cgradePoint * 3;
					}*/
					else if(e.getConsumerProgramStructureId()==131) {
						double subjectCredits = SubjectsCerditsConfiguration.getMSCAIOpsSubjectCredits(e.getSubject());
						csumOftotalCredits = csumOftotalCredits + subjectCredits;
						cgradeCalculation = cgradePoint * subjectCredits;
					}
					else if(e.getConsumerProgramStructureId()==154 || e.getConsumerProgramStructureId()==155 || e.getConsumerProgramStructureId()==158) {
						double subjectCredits = SubjectsCerditsConfiguration.getMSCAISubjectCredits(e.getSubject());
						csumOftotalCredits = csumOftotalCredits + subjectCredits;
						cgradeCalculation = cgradePoint * subjectCredits;
					}
					else if(e.getConsumerProgramStructureId()==160) {
						double subjectCredits = SubjectsCerditsConfiguration.getMBAWXSubjectCredits(e.getPrgm_sem_subj_id());
						csumOftotalCredits = csumOftotalCredits + subjectCredits;
						cgradeCalculation = cgradePoint * subjectCredits;
					}
					else {
						csumOftotalCredits = csumOftotalCredits + 4;
						cgradeCalculation = cgradePoint * 4;
					}
				 
				ctotal = ctotal + cgradeCalculation;			
//				if(e.getIsPass().equals("Y")) {
//					passCount++;
//				}
//				
//				count++;
			}		
			cgpa = ctotal/csumOftotalCredits;
			
			
			credits.add(String.format("%,.2f", gpa));
			credits.add(String.format("%,.2f", cgpa));
			response.put("credits", credits);
	
			}
     
		}catch(Exception e) {
			
			error.add("true");
			errorMessage.add("Error in getting marks details.");
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		if(passFailDataList == null){
			error.add("true");
			errorMessage.add("No exam details found for "+bean.getMonth()+"- "+bean.getYear());
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		
		
		
		
		response.put("marks", passFailDataList);
	
		error.add("false");
		response.put("error", error);
		
		return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
	}
	
	@PostMapping(value = "/generateNonGradedMarksheetPreviewFromSR")
	public ResponseEntity<HashMap<String, List>> generateMarksheetPreviewFromSR(
		  @RequestBody EmbaPassFailBean bean) throws UnsupportedEncodingException {

		HashMap<String, List> response = new  HashMap<String, List>();
		List error = 	new ArrayList<>();
		List errorMessage = 	new ArrayList<>();

		boolean hasAppearedForExamForGivenSemMonthYear = examsAssessmentsDAO
				.hasAppearedForExamForGivenSemMonthYearPreviewMarksheet_MBAWX(bean);
		
		if (hasAppearedForExamForGivenSemMonthYear == false) {
			error.add("true");
			errorMessage.add("You have not appeared for  sem "+bean.getSem()+"- "+bean.getMonth()+"- "+bean.getYear());
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		
		List<EmbaPassFailBean> passFailDataList = new ArrayList<EmbaPassFailBean>();
	
		try {
			passFailDataList = examsAssessmentsDAO.getEmbaPassFailBySapidTermMonthYear(bean.getSapid(), bean.getSem());
     
		}catch(Exception e) {
			
			error.add("true");
			errorMessage.add("Error in getting marks details.");
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		if(passFailDataList == null){
			error.add("true");
			errorMessage.add("No exam details found for "+bean.getMonth()+"- "+bean.getYear());
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		
		response.put("marks", passFailDataList);
	
		error.add("false");
		response.put("error", error);
		
		return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
	}
	
	@PostMapping(value = "/generateMarksheetPreviewFromSR_MBAX")
	public ResponseEntity<HashMap<String, List>> generateMarksheetPreviewFromSR_MBAX(
		  @RequestBody EmbaPassFailBean bean) throws UnsupportedEncodingException {
		
		
		HashMap<String, List> response = new  HashMap<String, List>();
		List error = 	new ArrayList<>();
		List errorMessage = 	new ArrayList<>();
		List credits = 	new ArrayList<>();

		boolean hasAppearedForExamForGivenSemMonthYear = examsAssessmentsDAO
				.hasAppearedForExamForGivenSemMonthYearPreviewMarksheet_MBAX(bean);
		if (hasAppearedForExamForGivenSemMonthYear == false) {
			error.add("true");
			errorMessage.add("You have not appeared for  sem "+bean.getSem()+"- "+bean.getMonth()+"- "+bean.getYear());
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		
		List<EmbaPassFailBean> passFailDataList = new ArrayList<EmbaPassFailBean>();
		List<EmbaPassFailBean> passFailDataListAllSem = new ArrayList<EmbaPassFailBean>();

		passFailDataListAllSem = examsAssessmentsDAO.getMbaXPassFailAllSemForStructureChangeStudent(bean.getSapid(), bean.getSem());
		
		if(passFailDataListAllSem.size() == 0) {
			try {
				passFailDataListAllSem = examsAssessmentsDAO.getMbaXPassFailBySapidTermMonthYearAllSem(bean.getSapid(), bean.getSem(), bean.getMonth(), bean.getYear());
			}catch(Exception e) {
				
			}
		}
	
		
		try {
			
//			passFailDataList = examsAssessmentsDAO.getEmbaPassFailBySapidTermMonthYear(bean.getSapid(), bean.getSem(), bean.getMonth(),bean.getYear());
//			getMbaXPassFailBySapidTermMonthYear
			passFailDataList = examsAssessmentsDAO.getMbaXPassFailForStructureChangeStudent(bean.getSapid(), bean.getSem());
			if(passFailDataList.size() == 0) {
				passFailDataList = examsAssessmentsDAO.getMbaXPassFailBySapidTermMonthYear(bean.getSapid(), bean.getSem(), bean.getMonth(), bean.getYear());
			}
			int count = 1;
			float gradePoint = 0;
			float gradeCalculation = 0;
			float total = 0;
			int passCount = 0;
			int sumOftotalCredits = 0;
			for (EmbaPassFailBean e : passFailDataList) {	
				if(e.getTimeboundId() != null) {
					gradePoint = Float.parseFloat(e.getPoints());
					
					if(1958 == e.getPrgm_sem_subj_id() || 1806 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Capstone Project Subject
						e.setCredits(20);
						sumOftotalCredits = sumOftotalCredits + 20;
						gradeCalculation = gradePoint * 20;
					}else if(1789 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Basics of Python Subject 
						e.setCredits(2);
						sumOftotalCredits = sumOftotalCredits + 2;	
						gradeCalculation = gradePoint * 2;
					}  else {
						sumOftotalCredits = sumOftotalCredits + 4;
						gradeCalculation = gradePoint * 4;
					}
					
					total = total + gradeCalculation;	
				}
				
			}	 
			
			float gpa = total/sumOftotalCredits;
			float cgpa = 0;
			
			if("1".equalsIgnoreCase(bean.getSem())) {
				credits.add( String.format("%,.2f", gpa));
				credits.add(String.format("%,.2f", gpa));
				response.put("credits", credits);
			} else {
				
				float cgradeCalculation = 0;
				float ctotal = 0;
				float cgradePoint = 0;
				int csumOftotalCredits = 0;
				
				for (EmbaPassFailBean e : passFailDataListAllSem) {
					cgradePoint = Float.parseFloat(e.getPoints());

				 	if(1958 == e.getPrgm_sem_subj_id() || 1806 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Capstone Project Subject
						csumOftotalCredits = csumOftotalCredits + 20;
						cgradeCalculation = cgradePoint * 20;
					}else if(1789 == e.getPrgm_sem_subj_id() ) { // Added by Abhay for Basics of Python Subject 
						csumOftotalCredits = csumOftotalCredits + 2;	
						cgradeCalculation = cgradePoint * 2;
					}  else {
						csumOftotalCredits = csumOftotalCredits + 4;
						cgradeCalculation = cgradePoint * 4;
					}	
					
					ctotal = ctotal + cgradeCalculation;			
//					if(e.getIsPass().equals("Y")) {
//						passCount++;
//					}
//					
//					count++;
				}	
				cgpa = ctotal/csumOftotalCredits;
				credits.add(String.format("%,.2f", gpa));
				credits.add(String.format("%,.2f", cgpa));
				response.put("credits", credits);
			}	
	
		}catch(Exception e) {
			
			error.add("true");
			errorMessage.add("Error in getting marks details.");
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		if(passFailDataList == null){
			error.add("true");
			errorMessage.add("No exam details found for "+bean.getMonth()+"- "+bean.getYear());
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		response.put("marks", passFailDataList);
	
		error.add("false");
		response.put("error", error);
		
		return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/getSingleStudentCertificateMBAWX", consumes = "application/json", produces = "application/json")
	public ResponseEntity<LinkedInAddCertToProfileExamBean> getSingleStudentCertificateMBAWX(
			@RequestBody PassFailExamBean passFailBean) throws Exception {
		
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		Format formatterMonth = new SimpleDateFormat("MM");
		Format formatterYear = new SimpleDateFormat("yyyy");
	    Format formatterDay = new SimpleDateFormat("dd");
		MarksheetBean marksheetBean = dao.getSingleStudentsData(passFailBean);
		
		
		String declareDate = null;
		
		try {
			if(MSCAIML.equalsIgnoreCase(marksheetBean.getProgram()))
				declareDate = dao.getResultDeclareDateForMSCAISingleStudent(marksheetBean);
			else if("MBA - X".equals(marksheetBean.getProgram()))
				declareDate = dao.getResultDeclareDateForSingleStudentMBAX(marksheetBean);
			else
				declareDate = dao.getResultDeclareDateForSingleStudentMBAWX(marksheetBean);
		} catch (Exception e1) {
			finalCertificateLogger.error("Error in getting declare date for getSingleStudentCertificateMBAWX: "+e1);
		}
		SpecialisationDAO spDao = (SpecialisationDAO) act.getBean("specialisationDao");

		Specialisation s = spDao.getSpecializationsOfStudent(marksheetBean.getSapid());

		String specialisation="";
		/*
		 * commented by harsh
		 * specialization was not ordered alphabetically, needed fix 
		 * based on card 9897
		//single specialisation
		if(s.getSpecialisation1()!=null) {
			specialisation=s.getSpecialisation1();
		}
		//dual specialisation
		if(s.getSpecialisation2()!=null) {
			specialisation=specialisation+" And "+s.getSpecialisation2();
		} 
		*/
		
		try {
			specialisation = specialisationService.getSpecilizationNameBasedOnAlphabeticalOrder(marksheetBean.getSapid());
		}catch (Exception e) {
			finalCertificateLogger.error("Error in getting specialisation for getSingleStudentCertificateMBAWX: "+e);
		}
		
		marksheetBean.setSpecialisation(specialisation);
		
		try {
			String certificateNumberGenerated = null;			
			if(MSCAIML.equalsIgnoreCase(marksheetBean.getProgram())) {
				certificateNumberGenerated = pdfCreator
						.generateCertificateAndReturnCertificateNumberForSingleStudentSelfMSCAI(
								marksheetBean, declareDate,
								getProgramAllDetailsMap(), getCentersMap(),
								CERTIFICATES_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH);
			}else {
				certificateNumberGenerated = pdfCreator
				.generateCertificateAndReturnCertificateNumberForSingleStudentSelfMBWX(
						marksheetBean, declareDate,
						getProgramAllDetailsMap(), getCentersMap(),
						CERTIFICATES_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH);
			}
			//request.setAttribute("success", "true");
			//request.setAttribute(
					//"successMessage",
					//"Certificate generated successfully. Please click Download link below to get file.");
//			dao.updateSRWithCertificateNumberAndCurrentDateForSingleStudent(
//					passFailBean, certificateNumberGenerated);
			
			LinkedInAddCertToProfileExamBean response = new LinkedInAddCertToProfileExamBean();
			response.setCertUrl(certificateNumberGenerated);
			

			response.setIssueMonth(formatterMonth.format(sd.parse(declareDate)));
			response.setIssueYear(formatterYear.format(sd.parse(declareDate)));
			response.setConsumerProgramStructureId(marksheetBean.getConsumerProgramStructureId());
			String key = marksheetBean.getProgram()+"-"+marksheetBean.getPrgmStructApplicable();
			ProgramExamBean programDetails = getProgramAllDetailsMap().get(key);
			
			String programname = programDetails.getProgramname();
			String programType = programDetails.getProgramType();

			if(programname.equals("MBA (WX) for Working Executives")) {
				programname = "MASTER OF BUSINESS ADMINISTRATION (WORKING EXECUTIVES)";
			
				}else if (programname.equals("MBA (Executive) with specialisation in Business Analytics")){
					programname = "MBA (EXECUTIVE) WITH SPECIALISATION IN BUSINESS ANALYTICS";				

				}
			if(programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {
				programType="MBA (Wx)";
			}
			response.setName(programname);
			return new ResponseEntity<LinkedInAddCertToProfileExamBean>(  response , HttpStatus.ACCEPTED);

		} catch (DocumentException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			finalCertificateLogger.error("Error in generating certificate for getSingleStudentCertificateMBAWX: "+e);
			//request.setAttribute("error", "true");
			//request.setAttribute("errorMessage",
				//	"Error in generating certificate." + e.getMessage());
			return null;
		}
		catch(Exception e)
		{
			finalCertificateLogger.error("Error in generating certificate for getSingleStudentCertificateMBAWX: "+e);
			return null;
		}

	//	modelnView.addObject("bean", passFailBean);
		//return modelnView;
	}
	
	
	@PostMapping(value = "/getSingleStudentCertificate", consumes = "application/json", produces = "application/json")
	public ResponseEntity<LinkedInAddCertToProfileExamBean> getSingleStudentCertificate(
			@RequestBody PassFailExamBean passFailBean) throws Exception {
		
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		Format formatterMonth = new SimpleDateFormat("MM");
		Format formatterYear = new SimpleDateFormat("yyyy");
	    Format formatterDay = new SimpleDateFormat("dd");
		MarksheetBean marksheetBean = dao.getSingleStudentsData(passFailBean);
		
		
		String declareDate = null;
		
		try {
			declareDate = dao
					.getResultDeclareDateForSingleStudent(marksheetBean);
		} catch (Exception e1) {
			
		}		
		
		try {
			String certificateNumberGenerated = pdfCreator
					.generateCertificateAndReturnCertificateNumberForSingleStudentSelfMBWX(
							marksheetBean, declareDate,
							getProgramAllDetailsMap(), getCentersMap(),
							CERTIFICATES_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH);
			//request.setAttribute("success", "true");
			//request.setAttribute(
					//"successMessage",
					//"Certificate generated successfully. Please click Download link below to get file.");
//			dao.updateSRWithCertificateNumberAndCurrentDateForSingleStudent(
//					passFailBean, certificateNumberGenerated);
			
			LinkedInAddCertToProfileExamBean response = new LinkedInAddCertToProfileExamBean();
			response.setCertUrl(certificateNumberGenerated);
			

			response.setIssueMonth(formatterMonth.format(sd.parse(declareDate)));
			response.setIssueYear(formatterYear.format(sd.parse(declareDate)));
			response.setConsumerProgramStructureId(marksheetBean.getConsumerProgramStructureId());
			String key = marksheetBean.getProgram()+"-"+marksheetBean.getPrgmStructApplicable();
			ProgramExamBean programDetails = getProgramAllDetailsMap().get(key);
			
			String programname = programDetails.getProgramname();
			String programType = programDetails.getProgramType();
			response.setName(programname);
			return new ResponseEntity<LinkedInAddCertToProfileExamBean>(  response , HttpStatus.ACCEPTED);

		} catch (DocumentException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			
			//request.setAttribute("error", "true");
			//request.setAttribute("errorMessage",
				//	"Error in generating certificate." + e.getMessage());
			return null;
		}

	//	modelnView.addObject("bean", passFailBean);
		//return modelnView;
	}

	
	@PostMapping(value = "/studentSelfMarksheetForSemMonthYear")
	public ResponseEntity<EmbaMarksheetBean> mstudentSelfMarksheetForSemMonthYear(
			HttpServletRequest request, @RequestBody MBAWXExamResultForSubject studentMarks
	) throws SQLException {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		EmbaMarksheetBean response = new EmbaMarksheetBean();
		try {
			response = msService.mStudentSelfMarksheetForTermMonthYearForMbaWx(studentMarks);

						return new ResponseEntity<EmbaMarksheetBean>(response, headers, HttpStatus.OK);
					}
					catch(Exception e) {
						logger.error("Error in generating gradesheet mbawx from student side: "+e);
						return new ResponseEntity<EmbaMarksheetBean>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
					}
					
				}
	
	@PostMapping(value = "/sRPaymentSuccess")
	public String mbaExamBookingCallback(HttpServletRequest request, Model model, Model m) {

		//if(request.getSession().getAttribute("embaBookingRequest") == null) {
		//	model.addAttribute("status", "fail");
		//	model.addAttribute("message", "Invlid or empty request!");
			return "redirect:" + examBookingSuccessURLWeb;
//		} else {
//			MBAWXExamBookingRequest bookingRequest = (MBAWXExamBookingRequest) request.getSession().getAttribute("embaBookingRequest");
//			if(bookingRequest.getSource().equalsIgnoreCase("webapp")) {
//				if(bookingRequest.getPaymentOption() != null) { 
//					model.addAttribute("paymentOption", bookingRequest.getPaymentOption());
//				}
//				if(bookingRequest.getSelectedSubjects() != null) {
//					model.addAttribute("timeboundIds", bookingRequest.getSelectedSubjects());
//				}
//				if(bookingRequest.getSapid() != null) {
//					model.addAttribute("sapid", bookingRequest.getSapid());
//				}
//				if(bookingRequest.getTrackId() != null) {
//					model.addAttribute("trackId", bookingRequest.getTrackId());
//				}
//			}
//			return parseExamBookingResponse(request, model, bookingRequest);
//		}
	}
	
	@PostMapping(value = "/sRStatusFailure")
	public String mbaExamBookingCallbackFail(HttpServletRequest request, Model model, Model m) {

		//if(request.getSession().getAttribute("embaBookingRequest") == null) {
		//	model.addAttribute("status", "fail");
		//	model.addAttribute("message", "Invlid or empty request!");
			return "redirect:" + examBookingErrorURLWeb	;
//		} else { 
//			MBAWXExamBookingRequest bookingRequest = (MBAWXExamBookingRequest) request.getSession().getAttribute("embaBookingRequest");
//			if(bookingRequest.getSource().equalsIgnoreCase("webapp")) {
//				if(bookingRequest.getPaymentOption() != null) { 
//					model.addAttribute("paymentOption", bookingRequest.getPaymentOption());
//				}
//				if(bookingRequest.getSelectedSubjects() != null) {
//					model.addAttribute("timeboundIds", bookingRequest.getSelectedSubjects());
//				}
//				if(bookingRequest.getSapid() != null) {
//					model.addAttribute("sapid", bookingRequest.getSapid());
//				}
//				if(bookingRequest.getTrackId() != null) {
//					model.addAttribute("trackId", bookingRequest.getTrackId());
//				}
//			}
//			return parseExamBookingResponse(request, model, bookingRequest);
//		}
	}
	
	@PostMapping(value = "/mbaXstudentSelfMarksheetForSemMonthYear")
	public ResponseEntity<EmbaMarksheetBean> mbaXstudentSelfMarksheetForSemMonthYear(
			HttpServletRequest request, @RequestBody MBAWXExamResultForSubject studentMarks
	) throws SQLException {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		EmbaMarksheetBean response = new EmbaMarksheetBean();
		try {
			response = msService.mStudentSelfMarksheetForTermMonthYearForMbaX(studentMarks);

			return new ResponseEntity<EmbaMarksheetBean>(response, headers, HttpStatus.OK);
		} catch(Exception e) {			
			logger.error("Error in generating gradesheet mbax from student side: "+e);
			response.setSubjects(null);
			response.setError(true);
			return new ResponseEntity<EmbaMarksheetBean>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
