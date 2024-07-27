package com.nmims.controllers;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.ExamBookingTransactionStudentPortalBean;
import com.nmims.beans.FacultyCourseFeedBackBean;
import com.nmims.beans.FaqCategoryBean;
import com.nmims.beans.FaqProgramGroupBean;
import com.nmims.beans.FaqQuestionAnswerTableBean;
import com.nmims.beans.FaqSubCategoryBean;
import com.nmims.beans.FeedbackBean;
import com.nmims.beans.LoginLogBean;
import com.nmims.beans.MBAWXPortalExamResultForSubject;
import com.nmims.beans.MailStudentPortalBean;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ReRegProbabilityBean;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.SessionDayTimeStudentPortal;
import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.StudentsTestDetailsStudentPortalBean;
import com.nmims.beans.TimeboundExamBookingBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.FaqDao;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.StudentDAO;
import com.nmims.exam.client.ITimeboundExamResultsClient;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.DateTimeHelper;
import com.nmims.helpers.PersonStudentPortalBean;
import com.nmims.interfaces.StudentSRForSFDCInterface;
import com.nmims.services.FaqServices;
import com.nmims.services.LoginLogService;
import com.nmims.services.PortalService;
import com.nmims.services.StudentService;
import com.nmims.services.SupportService;
import com.nmims.views.FeedbackExcelView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SupportController extends BaseController {

	@Autowired
	ApplicationContext act;
	
	@Autowired
	StudentService studentService;

	@Autowired
	FaqDao faqdao;
	
	@Autowired
	StudentDAO studentDao;
	
	@Autowired
	private HomeController homeController;
	
	@Autowired
	StudentSRForSFDCInterface studentSRForSFDCInterface;
	
	

	private static final Logger logger = LoggerFactory.getLogger(SupportController.class);
	private final int pageSize = 10;
	private static final int BUFFER_SIZE = 4096;
	public boolean debug = false;
	public boolean debugCurrent = true;

	@Autowired
	private ServiceRequestDao serviceRequestDao;

	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; // secret key;
	@Value("${ACCOUNT_ID}")
	private String ACCOUNT_ID;
	@Value("${V3URL}")
	private String V3URL;
	@Value("${SR_RETURN_URL}")
	private String SR_RETURN_URL;
	@Value("${SERVICE_REQUEST_FILES_PATH}")
	private String SERVICE_REQUEST_FILES_PATH;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;

	@Autowired
	FeedbackExcelView feedBackExcelView;

	@Autowired
	LoginLogService loginLogService;
	
	private final int  REVALUATION_FEE_PER_SUBJECT = 1000;
	private final int  PHOTOCOPY_FEE_PER_SUBJECT = 500;
	private final long MAX_FILE_SIZE = 5242880;
	public static final String LIVE_SESSION_ACCESS_DATE = "01/Jul/2021";

	public static final String programNotIncluded = "Bachelor Programs";// This Programs are not allowed to view SRB

	public static final String default_format = "yyyy-MM-dd";
	
	@Autowired
	SupportService supportService;
	
	@Autowired
	private ITimeboundExamResultsClient timeboundExamResultsClient;
	
	@Autowired
	FaqServices faqServices;
	
	@Autowired
	PortalService portalService;

	/*
	 * ArrayList<String> requestTypes = new ArrayList<>();
	 * 
	 * @ModelAttribute("requestTypes") public ArrayList<String>
	 * getRequestTypes(HttpServletRequest request) { String sapid =
	 * (String)request.getSession().getAttribute("userId"); this.requestTypes =
	 * serviceRequestDao.getActiveSRTypes();
	 * 
	 * StudentBean student = serviceRequestDao.getSingleStudentsData(sapid);
	 * if(student != null){ if("Jul2014".equals(student.getPrgmStructApplicable())){
	 * requestTypes.remove(ServiceRequest.OFFLINE_TEE_REVALUATION);
	 * requestTypes.remove(ServiceRequest.PHOTOCOPY_OF_ANSWERBOOK); }else{
	 * requestTypes.remove(ServiceRequest.TEE_REVALUATION); } } return
	 * this.requestTypes; }
	 * 
	 * public ArrayList<String> getAllRequestTypes() { //if(this.requestTypes.size()
	 * == 0){ this.requestTypes = serviceRequestDao.getAllSRTypes(); //} return
	 * this.requestTypes; }
	 */
	@RequestMapping(value = "/saveFacultyCourseFeedBack", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView saveFacultyCourseFeedBack(@ModelAttribute FacultyCourseFeedBackBean facultyCourseFeedBack,
			HttpServletRequest request, HttpServletResponse respnse) {
		PortalDao pDao = (PortalDao) act.getBean("portalDAO");
		// pDao.insertFacultyFeedBack(facultyCourseBean);
		return null;
	}

	@RequestMapping(value = "/studentAttendence", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView studentAttendence(HttpServletRequest request, HttpServletResponse respnse) {
		ModelAndView sModelnView = new ModelAndView("jsp/studentAttendence");
		String sapid = (String) request.getSession().getAttribute("userId");
		// System.out.println("studentAttendence method called with sapid"+sapid);
		PortalDao pDao = (PortalDao) act.getBean("portalDAO");
		AssignmentsDAO aDao = (AssignmentsDAO) act.getBean("asignmentsDAO");
		StudentStudentPortalBean student = (StudentStudentPortalBean) pDao.getSingleStudentsData(sapid);
		// System.out.println("Student Bean :- "+student);

		ReRegProbabilityBean reRegProbability = new ReRegProbabilityBean();

		if (student != null) {
			HashMap<String, Integer> applicableSubjects = studentDao.getapplicableSubjectsForStudent(sapid);
			reRegProbability.setNumberOfSubjectsApplicable(applicableSubjects.size());

			getAcademicDetails(student, sapid, aDao, pDao, request, reRegProbability);
		}

		return sModelnView;
	}

	@RequestMapping(value = "/viewStudentDetailsDashBoard", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView viewStudentDetailsDashBoard(HttpServletRequest request, HttpServletResponse respnse, Model m) {
//			throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
//			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, UnsupportedEncodingException {
		ModelAndView modelnView = new ModelAndView("jsp/support/studentDetails");
 
		try {
			String sapid = (String) request.getParameter("sapId");
			String token = (String) request.getParameter("token");
			
			/* Commented by Siddheshwar_Khanse on Dec 13, 2022 because this code shifted to com.nmims.services.SupportService.validateStudentMatrixSalesforceToken(String, String)
			token = java.net.URLDecoder.decode(token, "UTF-8");
			token = token.replaceAll(" ", "+"); 
	
			// if token not present
			if (StringUtils.isBlank(token)) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "You are not Authorised to view this information");
				return modelnView;
			}
	
			// System.out.println("viewStudentDetailsDashBoard method called with
			// sapid"+sapid);
			// System.out.println("token :"+token);
			// Checking token with in 100 seconds or not
			AESencrp encrp = new AESencrp();
			String key = "VDVlOGVadFNXJXJUKUswIw==";// setting key
	
			String salesforceDecryptToken = encrp.decryptSalesforce(token, key);
			// System.out.println("salesforceDecryptToken :"+salesforceDecryptToken);
			
			if (StringUtils.isBlank(salesforceDecryptToken)) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "You are not Authorised to view this information");
				return modelnView;
			}
	
			String timeUrlHit = salesforceDecryptToken.split("~")[1];
			String encryptSapid = salesforceDecryptToken.split("~")[0];
	
			//// System.out.println("timeUrlHit :"+timeUrlHit);
			
			long currentTimeInMiliseconds = System.currentTimeMillis();
			
			long diff = currentTimeInMiliseconds - Long.valueOf(timeUrlHit);
			
			// System.out.println("Time Difference :"+diff);
			
			if (diff > 100000 || (!encryptSapid.equalsIgnoreCase(sapid))) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "You are not Authorised to view this information");
				return modelnView;
			}*/
			
			if(!supportService.validateStudentMatrixSalesforceToken(sapid, token)) {
				request.setAttribute("error", "true"); 
				request.setAttribute("errorMessage", "You are not Authorised to view this information");
				return modelnView;
			}
	
			PortalDao pDao = (PortalDao) act.getBean("portalDAO");
			AssignmentsDAO aDao = (AssignmentsDAO) act.getBean("asignmentsDAO");
			ServiceRequestDao sDao = (ServiceRequestDao) act.getBean("serviceRequestDao");
			StudentStudentPortalBean student = (StudentStudentPortalBean) pDao.getSingleStudentsData(sapid);
 	
			ReRegProbabilityBean reRegProbability = new ReRegProbabilityBean();
			
			List<SessionQueryAnswerStudentPortal> studentQueries = null;
 
				
				if(student!=null){
		
					
					try {
						
					      studentQueries = portalService.getStudentQueries(sapid); // added by Ritesh
					}
					catch(Exception e) {
					//	e.printStackTrace();
					}
				
				HashMap<String, Integer> applicableSubjects = studentService.getapplicableSubjectsForStudent(sapid);
				reRegProbability.setNumberOfSubjectsApplicable(applicableSubjects.size());
				request.setAttribute("applicableSubjectsForStudent", applicableSubjects);
	
				if(homeController.isTimeboundWiseByConsumerProgramStructureId(student.getConsumerProgramStructureId())) {
					logger.info("Fetching ExamDetails of timebound student: {} for Student Metrics Result Details.", student.getSapid());
					getExamDetails(student,request,reRegProbability);

					getIADetails(student,request);

					logger.info("\'timeboundStudentMatrix\' logical view name set for sapid:"+student.getSapid());
					modelnView = new ModelAndView("jsp/support/timeboundStudentMatrix");
				}
				else
					getExamDetails(sapid, pDao, aDao, request, reRegProbability);
	
				getStudentPortalDetails(student, sDao, pDao, aDao, request);
	
				getAcademicDetails(student, sapid, aDao, pDao, request, reRegProbability);
	
				getMyCommunicationDetails(student, pDao, request);
	
				// System.out.println(reRegProbability);
				LoginLogBean loginDetails = loginLogService.getLoginLogs(sapid);
				List<ServiceRequestStudentPortal> allServiceRequestFromSapIDForStudentDetailDashBoard = studentSRForSFDCInterface.getAllServiceRequestFromSapIDForStudentDetailDashBoard(
						student.getSapid(), sDao);
				Map<String, String> mapOfActiveSRTypesAndTAT = studentSRForSFDCInterface.mapOfActiveSRTypesAndTAT(sDao);
				List<ServiceRequestStudentPortal> listofsr = studentSRForSFDCInterface.getStudentSR(allServiceRequestFromSapIDForStudentDetailDashBoard,mapOfActiveSRTypesAndTAT);
				modelnView.addObject("getAllServiceRequestFromSapIDForStudentDetailDashBoard", listofsr);
				modelnView.addObject("studentDetails", student);
				modelnView.addObject("reRegProbability", reRegProbability);
				modelnView.addObject("loginDetails", loginDetails);
				request.setAttribute("studentQueries", studentQueries);

			} 
			else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Student Does not Exist on Student Portal");
			}
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error occured while trying to display Student Metrics Details!");
			logger.error("Error occured while trying to display Student Metrics Details, Exception thrown: " + ex.toString());
		}
		return modelnView;
	}

	private void getIADetails(StudentStudentPortalBean student, HttpServletRequest request) {
		try {
			List<StudentsTestDetailsStudentPortalBean> allAttemptedIADetails = supportService.fetchAllAttemptedIADetails(student.getSapid());

			request.setAttribute("allAttemptedIADetails", allAttemptedIADetails);
			request.setAttribute("allAttemptedIADetailsSize", allAttemptedIADetails.size());
			
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error occured while trying to display IA attempts of Student Metrics Details!");
			logger.error("Error occured while trying to display IA attempts of Student Metrics Details, Exception thrown: " + e.toString());
		}
	}
	
	private void getExamDetails(StudentStudentPortalBean student, HttpServletRequest request, ReRegProbabilityBean reRegProbability) {
		logger.info("SupportController.getExamDetails() - START");
		try {
			//Get Student Marks Records
			List<MBAWXPortalExamResultForSubject> markRecordsList = supportService.getSubjectWiseStudentMarksRecord(student.getSapid());
			
			//Get Student Pass-Fail Records
			List<MBAWXPortalExamResultForSubject> passFailList = supportService.getStudentPassFailRecords(student.getSapid());
			
			//Get Student Exam Booking details.
			List<TimeboundExamBookingBean> examBookingsList = supportService.fetchTimeboundExamBookindRecords(student.getSapid());
			
			request.setAttribute("markRecordsList", markRecordsList);
			request.setAttribute("passFailList", passFailList);
			request.setAttribute("examBookingsList", examBookingsList);
			request.setAttribute("examBookingsListSize", examBookingsList.size());
			
			logger.info("Marks records list for student: {} of size: {} is : {}",student.getSapid(),markRecordsList.size(),markRecordsList);
			logger.info("Pass-Fail list for student: {} of size : {} is : {}",student.getSapid(),passFailList.size(),passFailList);
			logger.info("Exam Booking list for student: {} of size : {} is : {}",student.getSapid(),examBookingsList.size(),examBookingsList);
			
			int numberOfPassSubjects = (int)passFailList.stream().filter(passFailBean -> "Y".equals(passFailBean.getIsPass())).count();
			
			logger.info("No of passed subject by student: {} is :{}",student.getSapid(),numberOfPassSubjects);
			
			reRegProbability.setNumberOfWrittenAttempts(markRecordsList.size());
			reRegProbability.setNumberOfSubjectsPassed(numberOfPassSubjects);
			reRegProbability.setNumberOfSubjectsFailed(passFailList.size() - numberOfPassSubjects);
			reRegProbability.setNumberOfExamBookings(examBookingsList.size());
			reRegProbability.setNumberOfSubjectsExceptCurrentCycle(passFailList.size());
			reRegProbability.setNumberOfANS(0);
			reRegProbability.setNumberOfAssignmentsSubmitted(0);
	
			request.setAttribute("numberOfMarksSubjects", markRecordsList.size());
			request.setAttribute("numberOfPassFailSubjects", passFailList.size());
			request.setAttribute("numberOfFailSubjects", passFailList.size() - numberOfPassSubjects);
			request.setAttribute("numberOfPassSubjects", numberOfPassSubjects);
			logger.info("Successfully fetched ExamDetails of student: {} for Student Metrics Details.", student.getSapid());
		}
		catch(Exception ex) {
			logger.error("Error while fetching ExamDetails of student: {} for Student Metrics Details, Exception thrown: {}", student.getSapid(), ex.toString());
		}
		logger.info("SupportController.getExamDetails() - END");
	}

	/*
	 * ACode private void getStudentPortalDetails(StudentBean
	 * student,ServiceRequestDao sDao,PortalDao pDao,AssignmentsDAO
	 * aDao,HttpServletRequest request){ ArrayList<ServiceRequest>
	 * getAllServiceRequestFromSapIDForStudentDetailDashBoard =
	 * sDao.getStudentsSR(student.getSapid()); ArrayList<StudentMarksBean>
	 * getAllRegistrationsFromSapIdForStudentDetailDashBoard =
	 * pDao.getAllRegistrationsFromSAPID(student.getSapid());
	 * ArrayList<PassFailBean> passFailList =
	 * aDao.listOfPassFailRecordsFromSapid(student);
	 * 
	 * 
	 * for(int j = 0; j<
	 * getAllRegistrationsFromSapIdForStudentDetailDashBoard.size(); j++) {
	 * if(debugCurrent)//System.out.println("227 :: j = "+j);
	 * 
	 * try { for (int i = 0; i < passFailList.size(); i++) {
	 * if(debugCurrent)//System.out.
	 * println("227 :: getAllRegistrationsFromSapIdForStudentDetailDashBoard.get("
	 * +j+").getSem() = "+getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(
	 * j).getSem()); if(debugCurrent)//System.out.println("227 :: passFailList.get("
	 * +j+").getSem() = "+passFailList.get(i).getSem()); if(debugCurrent)
	 * //System.out.
	 * println("227 :: getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).getSem().equals(passFailList.get(i).getSem())"
	 * +
	 * getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).getSem().equals(
	 * passFailList.get(i).getSem()));
	 * if(getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).getSem().
	 * equals(passFailList.get(i).getSem())) {
	 * if(debugCurrent)//System.out.println("227 :: "+
	 * passFailList.get(i).getIsPass()); if(
	 * !"Y".equals(passFailList.get(i).getIsPass()) ) {
	 * getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).
	 * setPassFailStatus("No"); if(debugCurrent)//System.out.
	 * println("getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).getPassFailStatus() "
	 * +getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).
	 * getPassFailStatus()); }else {
	 * getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).
	 * setPassFailStatus("Yes"); if(debugCurrent)//System.out.
	 * println("getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).getPassFailStatus() "
	 * +getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).
	 * getPassFailStatus()); } } if(debug)
	 * //System.out.println("227:: passFailList.get("+i+").getSem() = "+passFailList
	 * .get(i).getSem()); if(debug)
	 * //System.out.println("227 :: passFailList.get("+i+").getIsPass( ) =  "+
	 * passFailList.get(i).getIsPass());
	 * 
	 * } if(debugCurrent) //System.out.
	 * println("227 :: getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).getPassFailStatus"
	 * + getAllRegistrationsFromSapIdForStudentDetailDashBoard.get(j).
	 * getPassFailStatus());
	 * 
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * request.setAttribute("getAllRegistrationsFromSapIdForStudentDetailDashBoard",
	 * getAllRegistrationsFromSapIdForStudentDetailDashBoard); request.setAttribute(
	 * "getAllServiceRequestFromSapIDForStudentDetailDashBoard",
	 * getAllServiceRequestFromSapIDForStudentDetailDashBoard); }
	 */

	private void getStudentPortalDetails(StudentStudentPortalBean student, ServiceRequestDao sDao, PortalDao pDao, 
			AssignmentsDAO aDao, HttpServletRequest request) {
		try {
//			ArrayList<ServiceRequestStudentPortal> getAllServiceRequestFromSapIDForStudentDetailDashBoard = sDao.getStudentsSR(student.getSapid());
			ArrayList<StudentMarksBean> getAllRegistrationsFromSapIdForStudentDetailDashBoard = pDao.getAllRegistrationsFromSAPID(student.getSapid());
			ArrayList<PassFailBean> passFailList = aDao.listOfPassFailRecordsFromSapid(student);
			Map<String, String> semWiseNumberOfSubjectsToClear = pDao.getSemWiseNumberOfSubjectsToClear(student);
			List<String> subjectsCleared = new ArrayList<>();
			int sem1SubjectsCount = 0;
			int sem2SubjectsCount = 0;
			int sem3SubjectsCount = 0;
			int sem4SubjectsCount = 0;
			try {
				for (PassFailBean bean : passFailList) {
					if ("1".equalsIgnoreCase(bean.getSem()) && "Y".equalsIgnoreCase(bean.getIsPass())) {
						sem1SubjectsCount++;
						subjectsCleared.add(bean.getSubject());
					} else if ("2".equalsIgnoreCase(bean.getSem()) && "Y".equalsIgnoreCase(bean.getIsPass())) {
						sem2SubjectsCount++;
						subjectsCleared.add(bean.getSubject());
					} else if ("3".equalsIgnoreCase(bean.getSem()) && "Y".equalsIgnoreCase(bean.getIsPass())) {
						sem3SubjectsCount++;
						subjectsCleared.add(bean.getSubject());
					} else if ("4".equalsIgnoreCase(bean.getSem()) && "Y".equalsIgnoreCase(bean.getIsPass())) {
						sem4SubjectsCount++;
						subjectsCleared.add(bean.getSubject());
					}
				}
				for (StudentMarksBean bean : getAllRegistrationsFromSapIdForStudentDetailDashBoard) {
					if ("1".equalsIgnoreCase(bean.getSem())
							&& sem1SubjectsCount == Integer.valueOf(semWiseNumberOfSubjectsToClear.get("1"))) {
						bean.setPassFailStatus("Yes");
					} else if ("1".equalsIgnoreCase(bean.getSem()) && "Y".equalsIgnoreCase(student.getIsLateral())
							&& sem1SubjectsCount >= (Integer.valueOf(semWiseNumberOfSubjectsToClear.get("1"))) / 2) {
						bean.setPassFailStatus("Yes");
					} else if ("2".equalsIgnoreCase(bean.getSem())
							&& sem2SubjectsCount == Integer.valueOf(semWiseNumberOfSubjectsToClear.get("2"))) {
						bean.setPassFailStatus("Yes");
					} else if ("2".equalsIgnoreCase(bean.getSem()) && "Y".equalsIgnoreCase(student.getIsLateral())
							&& sem2SubjectsCount >= (Integer.valueOf(semWiseNumberOfSubjectsToClear.get("2"))) / 2) {
						bean.setPassFailStatus("Yes");
					} else if ("3".equalsIgnoreCase(bean.getSem())
							&& sem3SubjectsCount == Integer.valueOf(semWiseNumberOfSubjectsToClear.get("3"))) {
						bean.setPassFailStatus("Yes");
					} else if ("3".equalsIgnoreCase(bean.getSem()) && "Y".equalsIgnoreCase(student.getIsLateral())
							&& sem3SubjectsCount >= (Integer.valueOf(semWiseNumberOfSubjectsToClear.get("3"))) / 2) {
						bean.setPassFailStatus("Yes");
					} else if ("4".equalsIgnoreCase(bean.getSem())
							&& sem4SubjectsCount == Integer.valueOf(semWiseNumberOfSubjectsToClear.get("4"))) {
						bean.setPassFailStatus("Yes");
					} else if ("4".equalsIgnoreCase(bean.getSem()) && "Y".equalsIgnoreCase(student.getIsLateral())
							&& sem4SubjectsCount >= (Integer.valueOf(semWiseNumberOfSubjectsToClear.get("4"))) / 2) {
						bean.setPassFailStatus("Yes");
					} else {
						bean.setPassFailStatus("No");
					}
				}
	
			} catch (Exception e) {
//				e.printStackTrace();
				logger.error("Error while fetching student' passfail status via subject count, Exception thrown: {}", e.toString());
			}
			request.setAttribute("getAllRegistrationsFromSapIdForStudentDetailDashBoard",
					getAllRegistrationsFromSapIdForStudentDetailDashBoard);
//			request.setAttribute("getAllServiceRequestFromSapIDForStudentDetailDashBoard",
//					getAllServiceRequestFromSapIDForStudentDetailDashBoard);
			request.setAttribute("subjectsClearedByStudent", subjectsCleared);
			logger.info("Successfully fetched StudentPortalDetails of student: {} for Student Metrics Details.", student.getSapid());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while fetching StudentPortalDetails of student: {} for Student Metrics Details, Exception thrown: {}", student.getSapid(), ex.toString());
		}
	}

	private void getMyCommunicationDetails(StudentStudentPortalBean student, PortalDao pDao, HttpServletRequest request) {
		try {
			ArrayList<MailStudentPortalBean> getAllCommunicationsMadeToTheStudent = pDao
					.getEmailCommunicationMadeToStudent(student.getSapid());
	
			request.setAttribute("getAllCommunicationsMadeToTheStudent", getAllCommunicationsMadeToTheStudent);
			logger.info("Successfully fetched CommunicationDetails of student: {} for Student Metrics Details.", student.getSapid());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while fetching CommunicationDetails of student: {} for Student Metrics Details, Exception thrown: {}", student.getSapid(), ex.toString());
		}
	}

	/*
	 * Acode private void getAcademicDetails(StudentBean student,String
	 * sapid,AssignmentsDAO aDao,PortalDao pDao,HttpServletRequest request,
	 * ReRegProbabilityBean reRegProbability) { ArrayList<SessionDayTimeBean>
	 * scheduledSessionListForStudentDetailDashBoard = new ArrayList<>();
	 * ArrayList<SessionDayTimeBean> attendedSessionListForStudentDetailDashBoard =
	 * new ArrayList<>();
	 * 
	 * int pcpBookingCountForStudentDetailDashBoard =
	 * aDao.getPCPBookingCountFromSAPID(sapid); int numberOfSessionsAttended =
	 * aDao.getTotalNumberOfSessionsAttended(sapid); int
	 * totalNumberOfSessionsAvailableForStudent =
	 * pDao.getSessionsAttendedBySingleStudent(sapid);
	 * 
	 * ArrayList<String> getSubjectsForStudent =
	 * getSubjectsForStudent(student,pDao);
	 * scheduledSessionListForStudentDetailDashBoard =
	 * pDao.getScheduledSessionForStudents(getSubjectsForStudent,student);
	 * ArrayList<SessionQueryAnswer> sessionAttendedDetailList =
	 * aDao.getSessionAttendedDetailList(sapid);
	 * attendedSessionListForStudentDetailDashBoard =
	 * pDao.getAttendedSessionsForStudent(getSubjectsForStudent,student); float
	 * percentage = ((float) numberOfSessionsAttended) /
	 * totalNumberOfSessionsAvailableForStudent; if(debug)//System.out.println(
	 * "---------------------------Displaypercentage----------------------------");
	 * percentage = percentage*100;
	 * 
	 * String displayPercentage = String.format("%2.02f", percentage);
	 * if(debug)//System.out.println(displayPercentage);
	 * 
	 * //System.out.println("scheduledSessionListForStudentDetailDashBoard"+
	 * scheduledSessionListForStudentDetailDashBoard);
	 * request.setAttribute("scheduledSessionListForStudentDetailDashBoard",
	 * scheduledSessionListForStudentDetailDashBoard);
	 * request.setAttribute("attendedSessionListForStudentDetailDashBoard",
	 * attendedSessionListForStudentDetailDashBoard);
	 * request.setAttribute("pcpBookingCountForStudentDetailDashBoard",
	 * pcpBookingCountForStudentDetailDashBoard);
	 * request.setAttribute("numberOfSessionsAttended",numberOfSessionsAttended);
	 * request.setAttribute("sessionAttendedDetailList",sessionAttendedDetailList);
	 * 
	 * request.setAttribute("totalNumberOfSessionsAvailableForStudent",
	 * totalNumberOfSessionsAvailableForStudent);
	 * request.setAttribute("displayPercentage", displayPercentage);
	 * 
	 * reRegProbability.setNumberOfLecturesAttended(numberOfSessionsAttended); }
	 */

	private void getAcademicDetails(StudentStudentPortalBean student, String sapid, AssignmentsDAO aDao, PortalDao pDao,
			HttpServletRequest request, ReRegProbabilityBean reRegProbability) {
		try {
			ArrayList<SessionDayTimeStudentPortal> scheduledSessionListForStudentDetailDashBoard = new ArrayList<>();
			ArrayList<SessionDayTimeStudentPortal> attendedSessionListForStudentDetailDashBoard = new ArrayList<>();
//			ArrayList<String> getSubjectsForStudent = getSubjectsForStudent(student, pDao);
//			ArrayList<String> getSubjectsForStudent = (ArrayList<String>) request.getSession().getAttribute("currentSemSubjects_studentportal");	//Not present in session
			ArrayList<String> getSubjectsForStudent = getCurrentApplicableSubjectsForStudent(request, student);
			
			ArrayList<SessionQueryAnswerStudentPortal> sessionAttendedDetailList = aDao.getSessionAttendedDetailList(sapid);
			// ArrayList<StudentMarksBean>
			// getAllRegistrationsFromSapIdForStudentDetailDashBoard =
			// (ArrayList<StudentMarksBean>)request.getAttribute("getAllRegistrationsFromSapIdForStudentDetailDashBoard");
			ArrayList<StudentMarksBean> getAllRegistrationsFromSapIdForStudentDetailDashBoard = pDao
					.getAllRegistrationsFromSAPID(student.getSapid());
	
			int pcpBookingCountForStudentDetailDashBoard = aDao.getPCPBookingCountFromSAPID(sapid);
			int numberOfSessionsAttended = aDao.getTotalNumberOfSessionsAttended(sapid);
			int totalNumberOfSessionsApplicableForStudent = 0;
	
			for (StudentMarksBean bean : getAllRegistrationsFromSapIdForStudentDetailDashBoard) {
				// System.out.println("semester of reg : "+bean.getSem());
				String year = bean.getYear();
				String month = bean.getMonth();
				totalNumberOfSessionsApplicableForStudent += pDao.getSessionsApplicableForSingleStudent(year, month, bean);
	
			}
	
			scheduledSessionListForStudentDetailDashBoard = pDao.getScheduledSessionForStudents(getSubjectsForStudent,
					student);
			attendedSessionListForStudentDetailDashBoard = pDao.getAttendedSessionsForStudent(getSubjectsForStudent,
					student);
			// System.out.println("numberOfSessionsAttended");
			// System.out.println(numberOfSessionsAttended +" ");
			// System.out.println("totalNumberOfSessionsApplicableForStudent");
			// System.out.println(totalNumberOfSessionsApplicableForStudent +" ");
			double percentage = 0;
			try {
				percentage = (numberOfSessionsAttended * 100) / totalNumberOfSessionsApplicableForStudent;
			} catch (Exception e) {
//				e.printStackTrace();
			}
	
			// System.out.println("percentage : "+percentage);
			String displayPercentage = "" + percentage;
	
			// System.out.println("scheduledSessionListForStudentDetailDashBoard"+scheduledSessionListForStudentDetailDashBoard);
			request.setAttribute("scheduledSessionListForStudentDetailDashBoard",
					scheduledSessionListForStudentDetailDashBoard);
			request.setAttribute("attendedSessionListForStudentDetailDashBoard",
					attendedSessionListForStudentDetailDashBoard);
			request.setAttribute("pcpBookingCountForStudentDetailDashBoard", pcpBookingCountForStudentDetailDashBoard);
			request.setAttribute("numberOfSessionsAttended", numberOfSessionsAttended);
			request.setAttribute("sessionAttendedDetailList", sessionAttendedDetailList);
			request.setAttribute("displayPercentage", displayPercentage);
	
			reRegProbability.setNumberOfLecturesAttended(numberOfSessionsAttended);
			logger.info("Successfully fetched AcademicDetails of student: {} for Student Metrics Details.", sapid);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while fetching AcademicDetails of student: {} for Student Metrics Details, Exception thrown: {}", sapid, ex.toString());
		}
	}

	private void getExamDetails(String sapid, PortalDao pDao, AssignmentsDAO aDao, HttpServletRequest request, 
			ReRegProbabilityBean reRegProbability) {
		try {
			StudentStudentPortalBean student = pDao.getSingleStudentsData(sapid);
			ArrayList<StudentMarksBean> markRecordsList = aDao.listOfMarkRecordsFromSapid(student);
			ArrayList<PassFailBean> passFailList = aDao.listOfPassFailRecordsFromSapid(student);
			ArrayList<ExamBookingTransactionStudentPortalBean> examBookingsList = aDao.getAllConfirmedBookingsFromSapId(sapid);
			ArrayList<AssignmentStudentPortalFileBean> ansList = aDao.getAllANSRecordsForSAPId(sapid);
	
			request.setAttribute("examBookingsList", examBookingsList);
			request.setAttribute("markRecordsList", markRecordsList);
			request.setAttribute("passFailList", passFailList);
			request.setAttribute("ansList", ansList);
	
			int numberOfWrittenAttempts = 0;
			int numberOfPassSubjects = 0;
			int numberOfFailSubjects = 0;
			for (PassFailBean bean : passFailList) {
				String writtenScore = bean.getWrittenscore();
				if (!StringUtils.isBlank(writtenScore)) {
					numberOfWrittenAttempts++;
				}
	
				if ("Y".equals(bean.getIsPass())) {
					numberOfPassSubjects++;
				} else {
					numberOfFailSubjects++;
				}
			}
	
			reRegProbability.setNumberOfWrittenAttempts(numberOfWrittenAttempts);
			reRegProbability.setNumberOfSubjectsPassed(numberOfPassSubjects);
			reRegProbability.setNumberOfSubjectsFailed(numberOfFailSubjects);
			reRegProbability.setNumberOfExamBookings(examBookingsList.size());
			reRegProbability.setNumberOfANS(ansList.size());
			reRegProbability
					.setNumberOfAssignmentsSubmitted(reRegProbability.getNumberOfSubjectsApplicable() - ansList.size());
			reRegProbability.setNumberOfSubjectsExceptCurrentCycle(passFailList.size());
	
			request.setAttribute("numberOfFailSubjects", numberOfFailSubjects);
			request.setAttribute("numberOfPassSubjects", numberOfPassSubjects);
			logger.info("Successfully fetched ExamDetails of student: {} for Student Metrics Details.", sapid);
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error while fetching ExamDetails of student: {} for Student Metrics Details, Exception thrown: {}", sapid, ex.toString());
		}
	}

	private ArrayList<String> getSubjectsForStudent(StudentStudentPortalBean student, PortalDao pDao) {

		ArrayList<ProgramSubjectMappingStudentPortalBean> programSubjectMappingList = pDao.getProgramSubjectMappingList();
		ArrayList<String> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingStudentPortalBean bean = programSubjectMappingList.get(i);

			if (bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable())
					&& bean.getProgram().equals(student.getProgram()) && bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())// Subjects has not already cleared it
			) {
				// System.out.println("bean.getSubject() after filtering
				// -->"+bean.getSubject());
				subjects.add(bean.getSubject());
			}
		}
		return subjects;
	}
	
	/**
	 * Applicable Subjects of the current Semester for Student
	 * @param request - HttpServletRequest containing the subjects and clearedSubjects List
	 * @param student - studentBean containing details of the respective student
	 * @return - ArrayList containing the Current Semester Applicable Subjects
	 */
	private ArrayList<String> getCurrentApplicableSubjectsForStudent(HttpServletRequest request, StudentStudentPortalBean student) {
		@SuppressWarnings("unchecked")
		Map<String, Integer> applicableSubjectsForStudent = (HashMap<String, Integer>) request.getAttribute("applicableSubjectsForStudent");		//Applicable subjects list
		@SuppressWarnings("unchecked")
		List<String> studentSubjectsCleared = (ArrayList<String>) request.getAttribute("subjectsClearedByStudent");			//List of Subjects cleared by the student
//		List<StudentMarksBean> studentRegistrations = (ArrayList<StudentMarksBean>) request.getAttribute("getAllRegistrationsFromSapIdForStudentDetailDashBoard");
		
		try {
			//Get the latest sem of student from applicableSubjectsForStudent HashMap
			int currentSem = applicableSubjectsForStudent.values()				//Working with only the values from the HashMap Collection
														 .stream()
														 .mapToInt(sem -> sem)			//Values passed from the stream are converted into IntStream
														 .max()					//If the stream is not empty, the maximum value is retured
														 .orElseThrow(NoSuchElementException::new);				//NoSuchElementException thrown for empty stream
			
			//Get the latest sem subjects of student from applicableSubjectsForStudent HashMap
			List<String> currentSemSubjects = applicableSubjectsForStudent.entrySet()		//Returns a Set of key-value pairs
															              .stream()
															              .filter(entry -> currentSem == entry.getValue())		//Filtering subjects matching the current sem
															              .map(Map.Entry::getKey)		//return a stream of key (subjects) filtered
															              .collect(Collectors.toList());
			
			//If the student is a lateral student, get the list of subjects cleared in previous program(s) to waive off from the current program
			List<String> waivedOffSubjects = studentService.mgetWaivedOffSubjects(student);
			logger.info("Total {} subjects waivedOff for Student: {}", waivedOffSubjects.size(), student.getSapid());
			
			//Adding lists to a Set, to remove duplicate subjects
			Set<String> clearedSubjectsSet = new HashSet<>();
			clearedSubjectsSet.addAll(studentSubjectsCleared);
			clearedSubjectsSet.addAll(waivedOffSubjects);
			logger.info("Total no of subjects cleared by Student: {}, in current & previous programs: {}", student.getSapid(), clearedSubjectsSet.size());
			
			//Filtering out subjects that haven't been cleared by the student
			List<String> currentApplicableSubjects = applicableSubjectsForStudent.keySet()		//Working with a Set containing only keys from the HashMap
																				 .stream()
																				 .filter(subject -> !clearedSubjectsSet.contains(subject))		//check if subject not cleared
																				 .collect(Collectors.toList());
			logger.info("For student: {} Current Applicable Subjects: {}", student.getSapid(), currentApplicableSubjects.toString());
			
			
			//Student registration operations
//			int currentSem = studentRegistrations.stream().map(StudentMarksBean::getSem).mapToInt(Integer::parseInt).max().orElseThrow(NoSuchElementException::new);
//			StudentMarksBean latestRegistration = studentRegistrations.stream()
//																	  .max(Comparator.comparingInt(registration -> Integer.parseInt(registration.getSem())))
//																	  .get();
			
			//List of current semester subjects yet to be cleared
			ArrayList<String> currentSemApplicableSubjects = currentSemSubjects.stream()
																			   .filter(subject -> currentApplicableSubjects.contains(subject))	//checking if subject present
																			   .collect(Collectors.toCollection(ArrayList::new));		//Collect values in ArrayList
			logger.info("For Student: {}, current Sem {} Applicable Subjects: {}", student.getSapid(), currentSem, currentSemApplicableSubjects.toString());
			return currentSemApplicableSubjects;
		}
		catch(NoSuchElementException ex) {
//			ex.printStackTrace();
			logger.error("Error occured while trying to get the current semester for Student: {}, Exception thrown: {}", student.getSapid(), ex.toString());
		}
		catch(Exception ex) {
//			ex.printStackTrace();
			logger.error("Error occured while trying to get Current Semester Applicable Subjects for Student: {}, Exception thrown: {}", student.getSapid(), ex.toString());
		}
		
		//If Exception caught above then return All Applicable subjects for the student
		ArrayList<String> applicableSubjects = applicableSubjectsForStudent.keySet()			//Working with a Set containing only keys from the HashMap
																		   .stream()
																		   .filter(subject -> !studentSubjectsCleared.contains(subject))	//check if subject not cleared
																		   .collect(Collectors.toCollection(ArrayList::new));			//Collect values in ArrayList
		logger.info("Applicable subjects for student {}: {}", student.getSapid(), applicableSubjects.toString());
		return applicableSubjects;
	}

	@RequestMapping(value = "/facultyCourseFeedBackForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView facultyCourseFeedBackForm(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("jsp/facultyCourseFeedBack");
		modelAndView.addObject("facultyCourseFeedBack", new FacultyCourseFeedBackBean());
		return modelAndView;
	}

	// Newly added by Vikas for a download excel provision 03/08/2016//
	@RequestMapping(value = "/downloadFeedBacks", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView downloadExcelFeedBack(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("jsp/downloadfeedback");
		List<FeedbackBean> listOfComments = serviceRequestDao.queryAllFeedBacks();
		if (listOfComments != null && listOfComments.size() > 0) {
			return new ModelAndView(feedBackExcelView, "feedbackList", listOfComments);
		} else {
			setError(request, "No FeedBack Comments");
			return mv;
		}

	}

	// End//
	@RequestMapping(value = "/faq", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView faq(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView();
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		String faqGroupTypeId = faqdao.getFaqGroupType(student.getConsumerProgramStructureId());
		request.getSession().setAttribute("faqGroupTypeId", faqGroupTypeId);
		//System.out.println(student);
		boolean checkDate = Boolean.FALSE;
		// Check Whether Student's Enrollment Month and year is before JUl 2021
		String enrollDate = ("01/" + student.getEnrollmentMonth() + "/" + student.getEnrollmentYear());	
		
		List<FaqSubCategoryBean> listOfSubCategories = new ArrayList<FaqSubCategoryBean>();
		List<FaqCategoryBean> listofFaqCategories = new ArrayList<FaqCategoryBean>();
		Map<Integer, List<FaqSubCategoryBean>> mapOfSubcatagery = new HashMap<Integer, List<FaqSubCategoryBean>>();
		Map<Integer, List<FaqQuestionAnswerTableBean>> mapOfQnACategory = new HashMap<Integer, List<FaqQuestionAnswerTableBean>>();
		Map<String, ArrayList<FaqQuestionAnswerTableBean>> mapOfQnASubCategory = new HashMap<String, ArrayList<FaqQuestionAnswerTableBean>>();
		
		try {
			listofFaqCategories = faqServices.getFaqCategories();
			List<Integer> categoryIds = listofFaqCategories.stream().map(x->x.getId()).collect(Collectors.toList());
			listOfSubCategories=faqServices.getFaqSubCategories();
			List<Integer> subCategoryIds = listOfSubCategories.stream().map(x->x.getId()).collect(Collectors.toList());
			mapOfSubcatagery = faqServices.getMapOfSubCatageory(subCategoryIds);
			mapOfQnACategory = faqServices.getMapOfFaqQuestionAnswer(faqGroupTypeId, categoryIds);
			mapOfQnASubCategory = faqServices.getMapOfQnASubCategory(faqGroupTypeId, categoryIds, subCategoryIds);
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * try { checkDate = DateTimeHelper.checkDate(DateTimeHelper.FORMAT_ddMMMyyyy,
		 * enrollDate, DateTimeHelper.FORMAT_ddMMMyyyy, LIVE_SESSION_ACCESS_DATE); }
		 * catch (ParseException e) { e.printStackTrace(); }
		 */

		/* if (checkDate) { */
		modelnView.setViewName("templates/faq");
		modelnView.addObject("category", listofFaqCategories);
		modelnView.addObject("subcatagery", mapOfSubcatagery);
		modelnView.addObject("questionanswerlist",mapOfQnACategory);
		modelnView.addObject("listOfQnASubCategory",mapOfQnASubCategory);
		/*
		 * } else { modelnView.setViewName("support/faq_distance"); }
		 */
		return modelnView;
	}

	@RequestMapping(value = "/supportOverview", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView supportOverview(HttpServletRequest request, HttpServletResponse respnse, Model m)
			throws ParseException {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		PortalDao pDao = (PortalDao) act.getBean("portalDAO");
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");

		// Check Whether Student's Enrollment Month and year is before JUl 2021
		String enrollDate = ("01/" + student.getEnrollmentMonth() + "/" + student.getEnrollmentYear());
		boolean checkDate = DateTimeHelper.checkDate(DateTimeHelper.FORMAT_ddMMMyyyy, enrollDate,
				DateTimeHelper.FORMAT_ddMMMyyyy, LIVE_SESSION_ACCESS_DATE);

		request.getSession().setAttribute("updatedSRB", checkDate);
		request.getSession().setAttribute("programType",
				pDao.getProgramTypeFromCode(student.getProgram(), student.getConsumerProgramStructureId()));
		request.getSession().setAttribute("programNotIncluded", programNotIncluded);

		ModelAndView modelnView = new ModelAndView("jsp/support/overview");
		return modelnView;
	}

	@RequestMapping(value = "/contactUs", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView contactUs(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/support/contactUs");
		String caseRaised = request.getParameter("caseRaised");
		if ("true".equals(caseRaised)) {
			setSuccess(request,
					"Your Query/Case is received successfully. We will get back to you shortly. Please check your email for Case Reference Number.");
		}
		return modelnView;
	}

	/*--------------------------------------------------------------------------------------------------
	 * added on Nov 10 2017
	 * 	*/
	@RequestMapping(value = "/trainingDocsICLC", method = { RequestMethod.GET })

	public ModelAndView trainingDocsICLC(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/importantDocumentsICLC/trainingDocumentsICLC");
		return modelnView;
	}

	@RequestMapping(value = "/modelAssignmentsICLC", method = { RequestMethod.GET })

	public ModelAndView modelAssignmentsICLC(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/importantDocumentsICLC/modelAssignmentsICLC");
		return modelnView;
	}

	@RequestMapping(value = "/admin/demoExamICLC", method = { RequestMethod.GET })

	public ModelAndView demoExamICLC(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/importantDocumentsICLC/demoExamICLC");
		return modelnView;
	}

	@RequestMapping(value = "/demoLecturesICLC", method = { RequestMethod.GET })

	public ModelAndView lectureVideosICLC(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/importantDocumentsICLC/lectureVideosICLC");
		return modelnView;
	}

	@RequestMapping(value = "/mockCallsICLC", method = { RequestMethod.GET })

	public ModelAndView mockCallsICLC(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/importantDocumentsICLC/mockCallsICLC");
		return modelnView;
	}

	@RequestMapping(value = "/faqICLC", method = { RequestMethod.GET })

	public ModelAndView faqICLC(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/importantDocumentsICLC/faqICLC");
		return modelnView;
	}

	@RequestMapping(value = "/faqSAS", method = { RequestMethod.GET })

	public ModelAndView faqSAS(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = null;
		String userId = (String) request.getSession().getAttribute("userId");
		if (userId.startsWith("77") || userId.startsWith("79")) {
			modelnView = new ModelAndView("jsp/faqSAS");
		} else {
			modelnView = new ModelAndView("jsp/importantDocumentsICLC/faqSAS");
		}
		return modelnView;
	}

	@RequestMapping(value = "/callRecordingsICLC", method = { RequestMethod.GET })

	public ModelAndView callRecordingsICLC(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/importantDocumentsICLC/recordedCallsSAS");
		return modelnView;
	}

	@RequestMapping(value = "/webinar", method = { RequestMethod.GET })

	public ModelAndView webinar(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if (!checkSession(request, respnse)) {
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/importantDocumentsICLC/webinar");
		return modelnView;
	}

	@RequestMapping(value = "/reRegistrationPage", method = { RequestMethod.GET })
	public RedirectView reRegistrationPage(HttpServletRequest request, HttpServletResponse respnse)
			throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
//		if (!checkSession(request, respnse)) {
//			return new ModelAndView("jsp/login");
//		}
		StudentStudentPortalBean student = (StudentStudentPortalBean) request.getSession().getAttribute("student_studentportal");
		String student_No  = student.getSapid();
		
		/**
		 * commented as it is not needed due to re-registration url change
		 */
		//String student_No = (String) request.getSession().getAttribute("userId");
		//long currentTimeInMiliseconds = System.currentTimeMillis();
		//String data = student_No + "~" + currentTimeInMiliseconds;
		//String token = AESencrp.encryptSalesforce(data);
	
		String dob = "";
		
		try {
			dob = DateTimeHelper.getDateInFormat(default_format,student.getDob());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		String redirectUrl = "https://ngasce.secure.force.com/nmLogin_new?studentNo=" + student_No
				+ "&dob=" + dob + "&type=reregistration";
		
		RedirectView view = new RedirectView();
		view.setUrl(redirectUrl);
		
		return view;

	}

	@RequestMapping(value = "/generateRegistrationLinks", method = { RequestMethod.GET })
	public ModelAndView generateRegistrationLinks(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute PersonStudentPortalBean person) {
		ModelAndView mav = new ModelAndView("jsp/generateRegistrationLinks");
		String passwordEnc;
		String passEnc = null;
		try {
			// System.out.println("type of registration "+person.getRegistrationType());
			if (person.getUserId() != null) {
				// System.out.println("person user id "+person.getUserId());
				passwordEnc = AESencrp.encrypt(person.getUserId());
				// System.out.println("Encrypted Text : " + passwordEnc);

				passEnc = passwordEnc.replace("+", "%2B");
				// System.out.println("Encrypted Text after replacement : " + passEnc);

				mav.addObject("passEnc", passEnc);
			} else {
				// System.out.println("User Id null");
			}
		} catch (Exception e) {
			// TODO catch block
//			e.printStackTrace();
		}

		mav.addObject("person", person);
		return mav;
	}

	@RequestMapping(value = "/faqEntryPage", method = { RequestMethod.GET })
	public ModelAndView faqEntryPageRedirect(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		ArrayList<FaqProgramGroupBean> faqgroup = null;
		ArrayList<FaqCategoryBean> faqcategory = null;
		ArrayList<FaqSubCategoryBean> faqsubcategory = null;
		ArrayList<FaqQuestionAnswerTableBean> faqlist = null;
		HashMap<Integer, String> faqgroupIdNameMap = new HashMap<Integer, String>();
		HashMap<Integer, String> faqcategoryIdNameMap = new HashMap<Integer, String>();
		HashMap<Integer, String> faqsubCategoryIdNameMap = new HashMap<Integer, String>();

		String userId = (String) request.getSession().getAttribute("userId");
		try {
			faqlist = faqdao.getAllFaqQuestionAnswerForAdminSide();
			faqgroup = faqdao.getFAQProgramGroups();
			faqcategory = faqdao.getFAQCategories();
			faqsubcategory = faqdao.getAllFAQSubCategories();
		} catch (Exception e) {
		}

		// hash map for group

		for (FaqProgramGroupBean group : faqgroup) {
			faqgroupIdNameMap.put(group.getId(), group.getGroupname());
		}

		// hash map for category

		for (FaqCategoryBean category : faqcategory) {
			faqcategoryIdNameMap.put(category.getId(), category.getCategoryname());
		}

		// hash map for subcategory

		for (FaqSubCategoryBean subcategory : faqsubcategory) {
			faqsubCategoryIdNameMap.put(subcategory.getId(), subcategory.getSubCategoryName());
		}

		request.getSession().setAttribute("faqGroupIdName", faqgroupIdNameMap);
		request.getSession().setAttribute("faqCategoryIdName", faqcategoryIdNameMap);
		request.getSession().setAttribute("faqSubCategoryIdName", faqsubCategoryIdNameMap);

		//System.out.println("check " + faqsubCategoryIdNameMap.get(1));

		//System.out.println("sub cat " + faqsubCategoryIdNameMap);

		ModelAndView modelnView = new ModelAndView("jsp/faqEntryPage");
		modelnView.addObject("faqlist", faqlist);
		return modelnView;

	}

	@RequestMapping(value = "/faqCategoryEntryPage", method = { RequestMethod.GET })
	public ModelAndView faqCategoryEntryPageRedirect(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}

		ArrayList<FaqCategoryBean> categorylist = null;

		String userId = (String) request.getSession().getAttribute("userId");
		try {
			categorylist = faqdao.getFAQCategories();
		} catch (Exception e) {
		}

		ModelAndView modelnView = new ModelAndView("jsp/support/faqCategoryEntryPage");
		modelnView.addObject("categorylist", categorylist);
		return modelnView;

	}
	
	@RequestMapping(value = "/faqSubCategoryEntryPage", method = { RequestMethod.GET })
	public ModelAndView faqSubCategoryEntryPageRedirect(HttpServletRequest request, HttpServletResponse response) {

		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}

		ArrayList<FaqSubCategoryBean> subcategorylist = null;

		String userId = (String) request.getSession().getAttribute("userId");
		try {
			subcategorylist = faqdao.getAllFAQSubCategories();
		} catch (Exception e) {
		}

		//System.out.println("subcategory "+subcategorylist);
		ModelAndView modelnView = new ModelAndView("jsp/support/faqSubCategoryEntryPage");
		modelnView.addObject("subcategorylist", subcategorylist);
		return modelnView;

	}

	@RequestMapping(value = "/deletefaq", method = { RequestMethod.POST })
	public ResponseEntity<Map<String, String>> faqDeleteRequest(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, String> res = new HashMap<String, String>();

		if (!checkSession(request, response)) {
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
		}

		int id = Integer.parseInt(request.getParameter("id"));
		String message = faqdao.deleteFaqEntry(id);
		res.put("message", message);

		if (message.equals(null)) {
			res.put("status", "fail");
		} else {
			res.put("status", "success");
		}

		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);

	}

	@RequestMapping(value = "/deletefaqcategory", method = { RequestMethod.POST })
	public ResponseEntity<Map<String, String>> faqCategoryDeleteRequest(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, String> res = new HashMap<String, String>();

		if (!checkSession(request, response)) {
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
		}

	
		int id = Integer.parseInt(request.getParameter("id"));
		
		try
		{
		// delete record from category table
		String message = faqdao.deleteFaqCategoryEntry(id);
		
		
		
		res.put("message", message);
        
		if (message.equals(null)) {
			res.put("status", "fail");
		} else {
			res.put("status", "success");
			
		// delete record from refererence question_answer table for category
			faqdao.deleteRecordsFromFaqQAT(id,"category");
		}
		}
		catch(Exception e){
			res.put("status", "fail");
		}

		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/deletefaqsubcategory", method = { RequestMethod.POST })
	public ResponseEntity<Map<String, String>> faqSubCategoryDeleteRequest(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, String> res = new HashMap<String, String>();

		if (!checkSession(request, response)) {
			res.put("status", "fail");
			res.put("message", "Log in to continue!!");
			return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);
		}

	
		int id = Integer.parseInt(request.getParameter("id"));
		
		try
		{
		// delete record from category table
		String message = faqdao.deleteFaqSubCategoryEntry(id);
		
		
		
		res.put("message", message);
        
		if (message.equals(null)) {
			res.put("status", "fail");
		} else {
			res.put("status", "success");
			
		// delete record from refererence question_answer table for sub category
			faqdao.deleteRecordsFromFaqQAT(id,"subcategory");
		}
		}
		catch(Exception e){
			res.put("status", "fail");
		}

		return new ResponseEntity<Map<String, String>>(res, HttpStatus.OK);

	}
	

	@ResponseBody
	@RequestMapping(value = "/getFAQProgramGroupList", method = { RequestMethod.GET })
	public ArrayList<FaqProgramGroupBean> programList(HttpServletRequest request, HttpServletResponse response) {
		//System.out.println("inside");
		ArrayList<FaqProgramGroupBean> groups = faqdao.getFAQProgramGroups();
		//System.out.println(groups);
		return groups;
	}

	@ResponseBody
	@RequestMapping(value = "/getFaqCategoryList", method = { RequestMethod.GET })
	public ResponseEntity<ArrayList<FaqCategoryBean>> faqCategoryList(HttpServletRequest request,
			HttpServletResponse response) {
		ArrayList<FaqCategoryBean> categories = new ArrayList<FaqCategoryBean>();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		try {
			categories = faqdao.getFAQCategories();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		//System.out.println(categories);

		return new ResponseEntity<>(categories, headers, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/getSubFaqCategoryList", method = { RequestMethod.GET })
	public ResponseEntity<ArrayList<FaqSubCategoryBean>> faqSubCategoryList(HttpServletRequest request,
			HttpServletResponse response) {
		String categoryId = request.getParameter("categoryid");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ArrayList<FaqSubCategoryBean> subcategorylist = new ArrayList<FaqSubCategoryBean>();
		try {
			subcategorylist = faqdao.getFAQSubCategories(categoryId);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return new ResponseEntity<>(subcategorylist, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/addFaqEntry", method = { RequestMethod.POST })
	public ModelAndView addFAQEntry(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String faqGroup = request.getParameter("faqgroup");
		String faqCategory = request.getParameter("category");
		String faqSubCategory = request.getParameter("subcategory");
		String question = request.getParameter("question");
		String answer = request.getParameter("answer");
		ModelAndView modelnView = new ModelAndView("jsp/faqEntryPage");
		if (!faqdao.isAbleToAddThisQuestion(faqGroup, faqCategory, faqSubCategory, question, answer)) {
			modelnView.addObject("succes", "false");
			return modelnView;
		}

		ArrayList<FaqQuestionAnswerTableBean> faqlist = null;

		/*
		 * String arr[] = request.getSession().getValueNames();
		 * 
		 * for (int i = 0; i < arr.length; i++) { System.out.println(arr[i] + ": " +
		 * request.getSession().getAttribute(arr[i])); }
		 */

		int a = faqdao.addFaqEntry(question, answer, faqGroup, faqCategory, faqSubCategory);
		//System.out.println("a is " + a);
		if (a == 0) {
			modelnView.addObject("succes", "false");
			return modelnView;
		}
		try {
			faqlist = faqdao.getAllFaqQuestionAnswerForAdminSide();

		} catch (Exception e) {
			faqlist = null;
		}
		modelnView.addObject("faqlist", faqlist);

		modelnView.addObject("succes", "true");
		return modelnView;

	}

	@RequestMapping(value = "/addFaqCategoryEntry", method = { RequestMethod.POST })
	public ModelAndView addFAQCategoryEntry(HttpServletRequest request, HttpServletResponse response)
			throws SQLException {

		String faqCategory = request.getParameter("category");
		ModelAndView modelnView = new ModelAndView("jsp/support/faqCategoryEntryPage");
		ArrayList<FaqCategoryBean> categorylist = null;

		if (!faqdao.isAbleToAddThisCategory(faqCategory)) {
			modelnView.addObject("succes", "false");
			try {
				categorylist = faqdao.getFAQCategories();

			} catch (Exception e) {
				categorylist = null;
			}
			modelnView.addObject("categorylist", categorylist);
			return modelnView;
		}

		/*
		 * String arr[] = request.getSession().getValueNames();
		 * 
		 * for (int i = 0; i < arr.length; i++) { System.out.println(arr[i] + ": " +
		 * request.getSession().getAttribute(arr[i])); }
		 */

		int a = faqdao.addFaqCategoryEntry(faqCategory);
		//System.out.println("a is " + a);
		if (a == 0) {
			modelnView.addObject("succes", "false");
		} else {
			modelnView.addObject("succes", "true");
		}
		try {
			categorylist = faqdao.getFAQCategories();

		} catch (Exception e) {
			categorylist = null;
		}
		modelnView.addObject("categorylist", categorylist);

		return modelnView;

	}
	
	@RequestMapping(value = "/addFaqSubCategoryEntry", method = { RequestMethod.POST })
	public ModelAndView addFAQSubCategoryEntry(HttpServletRequest request, HttpServletResponse response)
			throws SQLException {

		String categoryid = request.getParameter("category");
		String subcategory=request.getParameter("subcategory");
		ModelAndView modelnView = new ModelAndView("jsp/support/faqSubCategoryEntryPage");
		ArrayList<FaqSubCategoryBean> subcategorylist = null;

		if (!faqdao.isAbleToAddThisSubCategory(subcategory,categoryid)) {
			modelnView.addObject("succes", "false");
			try {
				subcategorylist = faqdao.getAllFAQSubCategories();

			} catch (Exception e) {
				subcategorylist = null;
			}
			modelnView.addObject("subcategorylist", subcategorylist);
			return modelnView;
		}

		/*
		 * String arr[] = request.getSession().getValueNames();
		 * 
		 * for (int i = 0; i < arr.length; i++) { System.out.println(arr[i] + ": " +
		 * request.getSession().getAttribute(arr[i])); }
		 */

		int a = faqdao.addFaqSubCategoryEntry(subcategory,categoryid);
		//System.out.println("a is " + a);
		if (a == 0) {
			modelnView.addObject("succes", "false");
		} else {
			modelnView.addObject("succes", "true");
		}
		try {
			subcategorylist = faqdao.getAllFAQSubCategories();

		} catch (Exception e) {
			subcategorylist = null;
		}
		modelnView.addObject("subcategorylist", subcategorylist);

		return modelnView;

	}

	

	@RequestMapping(value = "/updateFaqEntry", method = { RequestMethod.POST })
	public ModelAndView updateFaqEntry(HttpServletRequest request, HttpServletResponse response) throws SQLException {
		String faqid = request.getParameter("faqid");
		String faqGroup = request.getParameter("faqgroup");
		String faqCategory = request.getParameter("category");
		String faqSubCategory = request.getParameter("subcategory");
		String question = request.getParameter("question");
		String answer = request.getParameter("answer");
		HashMap<Integer, String> faqsubCategorylistMap = new HashMap<Integer, String>();
		ModelAndView modelnView = new ModelAndView("jsp/support/faqUpdatePage");

		//ArrayList<FaqQuestionAnswerTableBean> faq = null;
		FaqQuestionAnswerTableBean faq = new FaqQuestionAnswerTableBean();
		List<Integer> subCategoryIdListForCategoryId = new ArrayList<>();
		
		int count = 0;
		if (faqid != null || !faqid.equals("")) {
			try {

				// Faq ipdate dao call
				count = faqdao.updatefaqData(Integer.parseInt(faqid), faqGroup, faqCategory, faqSubCategory, question,
						answer);

			} catch (Exception e) {

				modelnView.addObject("succes", "false");
				return modelnView;
			}
			if (count > 0) {
				modelnView.addObject("succes", "true");

			} else {
				modelnView.addObject("succes", "false");
			}
			try {
				// updated FAQ dao record fetch
				faq = faqdao.getFaqById(Integer.parseInt(faqid));
				subCategoryIdListForCategoryId = faqdao.getSubCategoryIdsFromCategoryId(faq.getCategoryId());
			} catch (Exception e) {
//				e.printStackTrace();

			}
		} else {
			modelnView.addObject("succes", "false");
		}


		//System.out.println("faq" + faq);
		modelnView.addObject("faq", faq);
		modelnView.addObject("subCatArr", subCategoryIdListForCategoryId);
		return modelnView;

	}

	@RequestMapping(value = "/updateFaqCategory", method = { RequestMethod.POST })
	public ResponseEntity<HashMap<String, String>> updateFaqCategoryEntry(@RequestBody FaqCategoryBean category ,HttpServletRequest request,
			HttpServletResponse response) throws SQLException {

		String faqCategory = category.getCategoryname();
		String categoryid =String.valueOf(category.getId());

		//System.out.println(faqCategory);
		//System.out.println(categoryid);
		
		
		HashMap<String, String> res = new HashMap<String, String>();

		if (!checkSession(request, response)) {
			res.put("status", "fail");
			return new ResponseEntity<HashMap<String, String>>(res, HttpStatus.OK);
		}

		int count = 0;
		if (categoryid != null || !categoryid.equals("")) {

			try {

				// category update dao call
				//System.out.println("test1");
				count = faqdao.updatefaqCategoryData(Integer.parseInt(categoryid), faqCategory);
				//System.out.println("test2");

			} catch (Exception e) {

				res.put("status", "fail");

			}
			if (count > 0) {
				res.put("status", "success");

			} else {
				res.put("status", "fail");
			}

		} else {
			res.put("status", "fail");
		}

		return new ResponseEntity<HashMap<String, String>>(res, HttpStatus.OK);

		
		/*
		 * String faqCategory = request.getParameter("category"); String categoryid =
		 * request.getParameter("categoryid");
		 * 
		 * if (!checkSession(request, response)) { return new ModelAndView("jsp/login"); }
		 * 
		 * ArrayList<FaqCategoryBean> categorylist = null;
		 * 
		 * ModelAndView modelnView = new ModelAndView("jsp/support/faqCategoryEntryPage");
		 * 
		 * System.out.println("name "+faqCategory); System.out.println(" id "+
		 * categoryid); int count = 0; if (categoryid != null || !categoryid.equals(""))
		 * { try {
		 * 
		 * // category update dao call System.out.println("test1"); count =
		 * faqdao.updatefaqCategoryData(Integer.parseInt(categoryid), faqCategory);
		 * System.out.println("test2");
		 * 
		 * } catch (Exception e) {
		 * 
		 * modelnView.addObject("updatesucces", "false"); return modelnView; } if (count
		 * >0) { modelnView.addObject("updatesucces", "true");
		 * 
		 * } else { modelnView.addObject("updatesucces", "false"); }
		 * 
		 * 
		 * } else { modelnView.addObject("updatesucces", "false"); } try { categorylist
		 * = faqdao.getFAQCategories();
		 * 
		 * } catch (Exception e) { categorylist = null; }
		 * modelnView.addObject("categorylist", categorylist);
		 * 
		 * return modelnView;
		 */

	}
	
	
	@RequestMapping(value = "/updateFaqSubCategory", method = { RequestMethod.POST })
	public ResponseEntity<HashMap<String, String>> updateFaqSubCategoryEntry(@RequestBody FaqSubCategoryBean subcategory ,HttpServletRequest request,
			HttpServletResponse response) throws SQLException {

		String subcategoryName=subcategory.getSubCategoryName();
		String subcategoryid =String.valueOf(subcategory.getId());
		

		//System.out.println(subcategoryName);
		//System.out.println(subcategoryid);
		
		
		HashMap<String, String> res = new HashMap<String, String>();

		if (!checkSession(request, response)) {
			res.put("status", "fail");
			return new ResponseEntity<HashMap<String, String>>(res, HttpStatus.OK);
		}

		int count = 0;
		if (subcategoryid != null || !subcategoryid.equals("")) {

			try {

				// category update dao call
				//System.out.println("test1");
				count = faqdao.updatefaqSubCategoryData(subcategoryName,Integer.parseInt(subcategoryid));
				//System.out.println("test2");

			} catch (Exception e) {

				res.put("status", "fail");

			}
			if (count > 0) {
				res.put("status", "success");

			} else {
				res.put("status", "fail");
			}

		} else {
			res.put("status", "fail");
		}

		return new ResponseEntity<HashMap<String, String>>(res, HttpStatus.OK);
	}

	@RequestMapping(value = "/m/allfaqlist", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<FaqQuestionAnswerTableBean>> allFaqList(HttpServletRequest request,
			HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		String faqid = request.getParameter("faqid");
		// System.out.println("faqid " + faqid);
		ArrayList<FaqQuestionAnswerTableBean> faqlist = new ArrayList<FaqQuestionAnswerTableBean>();
		// System.out.println("faqlist "+faqlist);
		try {
			faqlist = faqdao.getAllFaqQuestionAnswer(faqid);
			// System.out.println("faqlist " + faqlist);
		} catch (Exception e) {
//			e.printStackTrace();
		}

		return new ResponseEntity<>(faqlist, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/m/filterfaqlist", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<FaqQuestionAnswerTableBean>> getFilteredFaqlist(HttpServletRequest request,
			HttpServletResponse response) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		String groupid = request.getParameter("groupid");
		String catid = request.getParameter("catid");
		String subcatid = request.getParameter("subcatid");
		ArrayList<FaqQuestionAnswerTableBean> faqlist = new ArrayList<FaqQuestionAnswerTableBean>();

		try {
			faqlist = faqdao.getListOfFaqQuestionAnswer(groupid, catid, subcatid);

		} catch (Exception e) {
//			e.printStackTrace();
		}

		return new ResponseEntity<>(faqlist, headers, HttpStatus.OK);
	}

	@RequestMapping(value = "/updatefaqentrypage")
	public ModelAndView updateFaqEntryPage(HttpServletRequest request, HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}

		ModelAndView mv = new ModelAndView("jsp/support/faqUpdatePage");
		String id = request.getParameter("id"); 

		//ArrayList<FaqQuestionAnswerTableBean> faq = null;
		//to get subcategory list(Prashant)
		FaqQuestionAnswerTableBean faq = new FaqQuestionAnswerTableBean();			
		List<Integer> subCategoryIdListForCategoryId = new ArrayList<>();

		if (id != null) {
			try {
				faq = faqdao.getFaqById(Integer.parseInt(id));
				subCategoryIdListForCategoryId = faqdao.getSubCategoryIdsFromCategoryId(faq.getCategoryId());
			} catch (Exception e) {
//				e.printStackTrace();
				request.setAttribute("error", "true");
				mv.addObject("errorMessage", "Error retrieving FAQ!");
			}
		} else {
			mv.addObject("errorMessage", "No id!");
		}
		
		mv.addObject("faq", faq);
		mv.addObject("subCatArr", subCategoryIdListForCategoryId);
		return mv;
	}
	
	@RequestMapping(value = "/ugConsent2", method = RequestMethod.POST)
	public ModelAndView ugConsent(HttpServletRequest request, HttpServletResponse response,@RequestParam("optionId") String optionId,@RequestParam("sapid") String sapid) {
		if (!checkSession(request, response)) {
			return new ModelAndView("jsp/login");
		}
		try {
		supportService.insertUgConsentForm(optionId, sapid);
		}catch(Exception e) {
			setError(request, "Error in Submitting Form. Error:- "+e.getMessage()+" . Please try again login.");
			return new ModelAndView("jsp/home");
		}
		setSuccess(request, "Form Submitted successfully. ");
		return new ModelAndView("jsp/home");
	}
	
	@GetMapping(value="/student/connectWithUs")
	public ModelAndView connectWithUs(HttpServletRequest request,HttpServletResponse response,Model m) {
		if(!checkSession(request,response)) {
			return new ModelAndView("jsp/login");
		}
		m.addAttribute("userId",request.getSession().getAttribute("userId"));
		return new ModelAndView("jsp/support/connectWithUs");
	}
	
	@RequestMapping(value = "/m/connectMyCases", method = RequestMethod.GET,produces = "application/json")
    public RedirectView connectMyCases(HttpServletRequest request,HttpServletResponse response){
//		if (!checkSession(request, response)) {
//			return "redirect:"+SERVER_PATH+"logout";
//		}
    Date date = new Date();
    String student_No = String.valueOf(request.getParameter("userId"));
    long currentTimeInMiliseconds = date.getTime();
    String data = student_No + "~" + currentTimeInMiliseconds;
    String token ="";
    try {
        token = AESencrp.encryptSalesforce(data);
    } catch (InvalidKeyException e) {
      //  e.printStackTrace();
    } catch (InvalidAlgorithmParameterException e) {
      //  e.printStackTrace();
    } catch (IllegalBlockSizeException e) {
      //  e.printStackTrace();
    } catch (BadPaddingException e) {
       // e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
       // e.printStackTrace();
    } catch (NoSuchPaddingException e) {
        //e.printStackTrace();
    }
    
    String url = "";
    
    
    if (ENVIRONMENT.equalsIgnoreCase("PROD")) {
    	url = "https://ngasce.secure.force.com/apex/MyTickets?token="+token;
    }else {
    	url = "https://ngasce--sandbox.sandbox.my.salesforce-sites.com/apex/MyCases?token="+token;
    }
    
    RedirectView view  = new RedirectView();
    view.setUrl(url);
    return view;
}
}
