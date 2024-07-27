package com.nmims.controllers;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AssignmentFileBean;
import com.nmims.beans.AssignmentPaymentBean;
import com.nmims.beans.AssignmentStatusBean;
import com.nmims.beans.CaseStudyExamBean;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.ConsumerProgramStructureExam;
import com.nmims.beans.ExamBookingCancelBean;
import com.nmims.beans.ExamBookingExamBean;
import com.nmims.beans.ExamBookingMetricsBean;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.ExamCenterBean;
import com.nmims.beans.ExamCenterSlotMappingBean;
import com.nmims.beans.FileBean;
import com.nmims.beans.OperationsRevenueBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.Person;
import com.nmims.beans.ProgramCompleteReportBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentLearningMetricsBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.TcsOnlineExamBean;
import com.nmims.beans.UGConsentExcelReportBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.CaseStudyDao;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.ExamCenterDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.ProjectSubmissionDAO;
import com.nmims.daos.ReportsDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.EmailHelper;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.services.IReportsService;
import com.nmims.services.ProjectStudentEligibilityService;
import com.nmims.services.ReportService;
import com.nmims.views.ExamBookingCancellationReportExcel;
import com.nmims.views.ExamBookingPendingReportExcelView;
import com.nmims.views.ExpectedExamRegistrationReportExcelView;

@Controller
public class ReportsController extends BaseController{

	@Autowired(required=false)
	ApplicationContext act;

	@Autowired
	EmailHelper emailHelper;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;
	
	@Value("${ICLCRESTRICTED_USER_LIST}")
	private List<String> ICLCRESTRICTED_USER_LIST;

	/*@Autowired
	AssignmentsDAO asignmentsDAO;*/

	@Autowired
	ReportsDAO reportsDAO;
	
	@Autowired
	private IReportsService reportsService;

	@Autowired
	ProjectStudentEligibilityService eligibilityService;
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	@Autowired
	ExamBookingCancellationReportExcel ExamBookingCancellationReportExcel;

	//added for executive students
	@Value("#{'${SAS_EXAM_MONTH_LIST}'.split(',')}") 
	private List<String> SAS_EXAM_MONTH_LIST; 

	@Value("#{'${ACAD_YEAR_SAS_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_SAS_LIST; 

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> ACAD_YEAR_LIST; 


	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST; 

	@Value("#{'${EXAM_MONTH_LIST}'.split(',')}") 
	private List<String> EXAM_MONTH_LIST; 

	@Value("${PROGRAM_STRUCTURES}") 
	private List<String> PROGRAM_STRUCTURES; 


//	private ArrayList<String> programList = null;
//	private ArrayList<String> subjectList = null; 
//	private HashMap<String, ProgramExamBean> programDetailsMap = null;
//	private HashMap<String, ProgramExamBean> programProgramStructureDetailsMap = null;
	private final String DD_APPROVAL_PENDING = "DD Approval Pending";
	private final String DD_APPROVED = "DD Approved";
	private final String DD_REJECTED = "DD Rejected";

	private final int pageSize = 20;


	private static final Logger logger = LoggerFactory.getLogger(ReportsController.class);
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> yearList; 

	private ArrayList<String> executiveExamMonthList = new ArrayList<String>(Arrays.asList( 
			"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"));

	@Value("#{'${CURRENT_YEAR_LIST}'.split(',')}")
	private ArrayList<String> currentYearList; 
//	private ArrayList<CenterExamBean> centers = null; 
//	private ArrayList<String> centersList = null;
//	private HashMap<String, String> centerCodeNameMap = null; 
//	private HashMap<String, CenterExamBean> centersMap = null; 
//	private HashMap<String, StudentExamBean> studentsWithLCMap = null;
	
	@Autowired
	ReportService reportService;

	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {

//		subjectList = null;
		getSubjectList();

//		programList = null;
		getProgramList();

//		programDetailsMap = null;
		getAllProgramMap();



		return null;
	}


	public ArrayList<String> getCentersList(){
		 ArrayList<CenterExamBean> centers;
         ArrayList<String> centersList;
		//if(this.centers == null){
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		centers = dao.getAllCenters();

		centersList = new ArrayList<>();
		for (int i = 0; i < centers.size(); i++) {
			centersList.add(centers.get(i).getCenterCode());
		}
		//}
		return centersList;
	}

	public HashMap<String, CenterExamBean> getCentersMap(){
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		ArrayList<CenterExamBean> centers = dao.getAllCenters();
		HashMap<String, CenterExamBean>centersMap = new HashMap<>();
		for (int i = 0; i < centers.size(); i++) {
			CenterExamBean cBean = centers.get(i);
			centersMap.put(cBean.getCenterCode(), cBean);
		}
		return centersMap;
	}

	public HashMap<String, StudentExamBean> getStudentsWithLCMap(){
		HashMap<String, StudentExamBean> studentsWithLCMap;
//		if(this.studentsWithLCMap == null || this.studentsWithLCMap.size() == 0){
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			studentsWithLCMap = dao.getAllStudentsWithLC();
//		}

		return studentsWithLCMap;
	}


	public Map<String, String> getExamCenterIdNameMap(){

		ExamCenterDAO dao = (ExamCenterDAO)act.getBean("examCenterDAO");
		return dao.getExamCenterIdNameMap();

	}

	public HashMap<String, String> getCenterCodeNameMap(String authorizedCenterCodes){
		HashMap<String, String> centerCodeNameMap;
//		if(this.centerCodeNameMap == null || this.centerCodeNameMap.size() == 0){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			ArrayList<CenterExamBean> centers = dao.getAuthrorizedCenters(authorizedCenterCodes);


			centerCodeNameMap = new HashMap<>();
			for (CenterExamBean cBean : centers) {
				centerCodeNameMap.put(cBean.getCenterCode(), cBean.getCenterName());
			}
//		}

		return centerCodeNameMap;
	}

	public ArrayList<String> getSubjectList(){
		 ArrayList<String> subjectList;
//		if(this.subjectList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			subjectList = dao.getAllSubjects();
//		}
		return subjectList;
	}

	public ArrayList<String> getProgramList(){
		ArrayList<String> programList;
//		if(this.programList == null){
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			programList = dao.getAllPrograms();
//		}
		return programList;
	}

	public HashMap<String, ProgramExamBean> getAllProgramMap(){
		HashMap<String, ProgramExamBean> programDetailsMap;
		//	if(this.programDetailsMap == null || this.programDetailsMap.size() == 0){
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		programDetailsMap = dao.getProgramMap();
		//	}
		return programDetailsMap;
	}

	public HashMap<String, ProgramExamBean> getAllProgramProgramStructureMap(String programStructure){
		HashMap<String, ProgramExamBean> programProgramStructureDetailsMap;
		/*if(this.programProgramStructureDetailsMap == null || this.programProgramStructureDetailsMap.size() == 0){
		*/
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		programProgramStructureDetailsMap = dao.getProgramProgramStructureMap(programStructure);
		//}
		return programProgramStructureDetailsMap;
	}

	//Newly added//

	@RequestMapping(value="/admin/reRegistrationReportForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView reRegistrationReportForm(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("report/reRegistrationReport");
		ReportsDAO rDao = (ReportsDAO)act.getBean("reportsDAO");
		/*ArrayList<CenterBean> centerList = (ArrayList<CenterBean>)rDao.getCentersOnAuthorizedCenterCodes(getAuthorizedCodes(request), "LC");*/
		modelnView.addObject("studentBean", new StudentExamBean());
		modelnView.addObject("centerList",getCenterCodeNameMap(getAuthorizedCodes(request)));
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("rowCount",0);
		modelnView.addObject("listOfSapIdOfActiveStudents", new ArrayList<String>());

		modelnView.addObject("mapOfStudentNumberSemAndCountOfFailedSubjects", new HashMap<String,String>());

		modelnView.addObject("mapOfStudentNumberAndSemAndCountOfANSSubjects", new HashMap<String,String>());
		modelnView.addObject("mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects", new HashMap<String,String>());
		modelnView.addObject("mapOfStudentNumberAndSemAndCountOfABSubjects", new HashMap<String,String>());
		modelnView.addObject("mapOfStudentNumberAndSemAndGAPInReReg", new HashMap<String,String>());
		modelnView.addObject("mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade", new HashMap<String,String>());
		modelnView.addObject("mapOfStudentNumberAndSemAndPendingNumberOfExamBookings", new HashMap<String,String>());
		modelnView.addObject("mapOfStudentNumberAndSemAndCountOfSessionsAttended", new HashMap<String,String>());
		modelnView.addObject("mapOfStudentNumberAndSemAndDriveMonthYear", new HashMap<String,String>());

		return modelnView;
	}

	@RequestMapping(value="/admin/getSubjectNames",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String getSubjectNames(@RequestParam("type") String type,@RequestParam("sapid") String sapid,@RequestParam("sem") String semester,HttpServletRequest request, HttpServletResponse response){

		ReportsDAO rDao = (ReportsDAO)act.getBean("reportsDAO");
		try{
			String commaSeperatedSubjects = (String)rDao.commaSeperatedSubjectListBasedOnType(type,sapid,semester);
			return commaSeperatedSubjects;
		}catch(Exception e){
			
			return e.getMessage();
		}

	}

	@RequestMapping(value="/admin/reRegistrationReportPage",method={RequestMethod.GET})
	public ModelAndView reRegistrationReportPage(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		return reRegistrationReport(new StudentExamBean(),request,response);
	}

	@RequestMapping(value="/admin/reRegistrationReport",method={RequestMethod.POST})
	public ModelAndView reRegistrationReport(@ModelAttribute StudentExamBean studentBean,HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("report/reRegistrationReport");
		ReportsDAO rDao = (ReportsDAO)act.getBean("reportsDAO");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");

		ArrayList<String> listOfSapIdOfActiveStudents = new ArrayList<String>();
		Page<StudentExamBean> page = new Page<StudentExamBean>();
		HashMap<String,String> mapOfStudentNumberSemAndCountOfFailedSubjects = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfANSSubjects = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfABSubjects = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndGAPInReReg = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndPendingNumberOfExamBookings = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfSessionsAttended = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndDriveMonthYear = new HashMap<String,String>();
		HashMap<String, StudentExamBean> studentsMap = new HashMap<>();


		int rowCount = 0;
		List<StudentExamBean> studentList = null;

		String pageNo = request.getParameter("pageNo");

		if(pageNo == null){
			pageNo = "1";
			request.getSession().setAttribute("studentBean",studentBean);
		}else{
			studentBean = (StudentExamBean)request.getSession().getAttribute("studentBean");
		}


		page = dao.getStudentPage(Integer.parseInt(pageNo), pageSize, studentBean, getAuthorizedCodes(request));
		studentList = page.getPageItems();
		rowCount = page.getRowCount();




		for(StudentExamBean student:studentList){
			listOfSapIdOfActiveStudents.add(student.getSapid());
			studentsMap.put(student.getSapid(), student);
		}


		if(listOfSapIdOfActiveStudents.size()> 0 ){
			mapOfStudentNumberAndSemAndDriveMonthYear = rDao.getMapOfStudentNumberAndSemAndDriveMonthYear(StringUtils.join(listOfSapIdOfActiveStudents,","));

			mapOfStudentNumberSemAndCountOfFailedSubjects = rDao.getMapOfStudentNumberSemAndCountOfSubjectsFailedOrPassed(StringUtils.join(listOfSapIdOfActiveStudents,","),"FAIL");

			mapOfStudentNumberAndSemAndCountOfANSSubjects = rDao.getMapOfStudentNumberAndSemAndCountOfANSSubjects(StringUtils.join(listOfSapIdOfActiveStudents,","));

			mapOfStudentNumberAndSemAndCountOfABSubjects = rDao.getMapOfStudentNumberAndSemAndCountOfTEEMissingOrABSubjects(StringUtils.join(listOfSapIdOfActiveStudents,","),"AB");

			mapOfStudentNumberAndSemAndGAPInReReg = rDao.getMapOfStudentNumberAndSemAndGAPInReReg(StringUtils.join(listOfSapIdOfActiveStudents,","));

			mapOfStudentNumberAndSemAndPendingNumberOfExamBookings  = rDao.getMapOfStudentNumberAndSemAndPendingNumberOfExamBookings(StringUtils.join(listOfSapIdOfActiveStudents,","));

			mapOfStudentNumberAndSemAndCountOfSessionsAttended  = rDao.getMapOfStudentNumberAndSemAndCountOfSessionsAttended(StringUtils.join(listOfSapIdOfActiveStudents,","));



			modelnView.addObject("studentBean", studentBean);
			modelnView.addObject("rowCount",rowCount);
			modelnView.addObject("page",page);


			modelnView.addObject("listOfSapIdOfActiveStudents", listOfSapIdOfActiveStudents);
			modelnView.addObject("studentsMap", studentsMap);
			modelnView.addObject("mapOfStudentNumberSemAndCountOfFailedSubjects", mapOfStudentNumberSemAndCountOfFailedSubjects);
			modelnView.addObject("mapOfStudentNumberAndSemAndCountOfANSSubjects", mapOfStudentNumberAndSemAndCountOfANSSubjects);
			modelnView.addObject("mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects", mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects);
			modelnView.addObject("mapOfStudentNumberAndSemAndCountOfABSubjects", mapOfStudentNumberAndSemAndCountOfABSubjects);
			modelnView.addObject("mapOfStudentNumberAndSemAndGAPInReReg", mapOfStudentNumberAndSemAndGAPInReReg);
			modelnView.addObject("mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade", mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade);
			modelnView.addObject("mapOfStudentNumberAndSemAndPendingNumberOfExamBookings", mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade);
			modelnView.addObject("mapOfStudentNumberAndSemAndCountOfSessionsAttended", mapOfStudentNumberAndSemAndCountOfSessionsAttended);
			modelnView.addObject("mapOfStudentNumberAndSemAndDriveMonthYear", mapOfStudentNumberAndSemAndDriveMonthYear);




			modelnView.addObject("centerList",getCenterCodeNameMap(getAuthorizedCodes(request)));
			modelnView.addObject("programList", getProgramList());
			modelnView.addObject("yearList", yearList);
			modelnView.addObject("subjectList", getSubjectList());

			return modelnView;
		}else{
			setError(request,"No Records Found");
			return reRegistrationReportForm(request,response);
		}

	}

	/* old code
	 * 
	 * @RequestMapping(value="/studentLearningMetrics",method={RequestMethod.POST})
	public void studentLearningMetrics()
	{
		try{
		ReportsDAO rDao = (ReportsDAO)act.getBean("reportsDAO");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		ArrayList<String> listOfSapIdOfActiveStudents = new ArrayList<String>();
		Page<StudentBean> page = new Page<StudentBean>();
		HashMap<String,String> mapOfStudentNumberSemAndCountOfFailedSubjects = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberSemAndCountOfPassSubjects = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfANSSubjects = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfAssignmentSubmitted = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfABSubjects = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndGAPInReReg = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndPendingNumberOfExamBookings = new HashMap<String,String>();
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfSessionsAttended = new HashMap<String,String>();
		HashMap<String, Integer> mapOfStudentNumberAndNoOfSubjectsApplicable = new HashMap<>();

		HashMap<String, StudentBean> studentsMap = new HashMap<>();


		int rowCount = 0;
		List<StudentBean> studentList = null;

		StudentBean studentBean = new StudentBean();
		studentBean.setValidStudent(true);
		page = dao.getStudentPage(Integer.parseInt("1"), Integer.MAX_VALUE, studentBean,null);
		studentList = page.getPageItems();
		rowCount = page.getRowCount();

		for(StudentBean student:studentList){
			listOfSapIdOfActiveStudents.add(student.getSapid());
			studentsMap.put(student.getSapid(), student);
		}


		if(listOfSapIdOfActiveStudents.size()> 0 ){

			mapOfStudentNumberSemAndCountOfFailedSubjects = rDao.getMapOfStudentNumberSemAndCountOfSubjectsFailedOrPassed(StringUtils.join(listOfSapIdOfActiveStudents,","),"FAIL");
			mapOfStudentNumberSemAndCountOfPassSubjects = rDao.getMapOfStudentNumberSemAndCountOfSubjectsFailedOrPassed(StringUtils.join(listOfSapIdOfActiveStudents,","),"PASS");
			mapOfStudentNumberAndSemAndCountOfANSSubjects = rDao.getMapOfStudentNumberAndSemAndCountOfANSSubjects(StringUtils.join(listOfSapIdOfActiveStudents,","));
			mapOfStudentNumberAndSemAndCountOfAssignmentSubmitted = rDao.getMapOfStudentNumberAndSemAndCountOfAssignmentSubmitted(StringUtils.join(listOfSapIdOfActiveStudents,","));
			//mapOfStudentNumberAndSemAndCountOfABSubjects = rDao.getMapOfStudentNumberAndSemAndCountOfTEEMissingOrABSubjects(StringUtils.join(listOfSapIdOfActiveStudents,","),"AB");

			//mapOfStudentNumberAndSemAndGAPInReReg = rDao.getMapOfStudentNumberAndSemAndGAPInReReg(StringUtils.join(listOfSapIdOfActiveStudents,","));

			mapOfStudentNumberAndSemAndPendingNumberOfExamBookings  = rDao.getMapOfStudentNumberAndSemAndPendingNumberOfExamBookings(StringUtils.join(listOfSapIdOfActiveStudents,","));
			//mapOfStudentNumberAndSemAndCountOfSessionsAttended  = rDao.getMapOfStudentNumberAndSemAndCountOfSessionsAttended(StringUtils.join(listOfSapIdOfActiveStudents,","));

			mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade = rDao.getMapOfStudentNumberAndSemAndNumberOfExamsBookings(StringUtils.join(listOfSapIdOfActiveStudents,","), "Y");
			mapOfStudentNumberAndNoOfSubjectsApplicable = rDao.getMapOfStudentNumberAndNoOfSubjectsApplicable();


			ArrayList<StudentLearningMetricsBean> metricsList = new ArrayList<StudentLearningMetricsBean>();


			//int count=0;
			for(String sapId : listOfSapIdOfActiveStudents) {
				StudentLearningMetricsBean bean = new StudentLearningMetricsBean();
				bean.setSapid(sapId);
				mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberAndSemAndCountOfANSSubjects, "ANS", bean);
				mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberSemAndCountOfFailedSubjects, "FAIL", bean);
				mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberSemAndCountOfPassSubjects, "PASS", bean);
				mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberAndSemAndPendingNumberOfExamBookings, "Exam Booking Pending", bean);
				mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade, "Exam Booked", bean);
				mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberAndSemAndCountOfAssignmentSubmitted, "Assignment Submitted", bean);
				bean.setNumberOfSubjectsApplicable(mapOfStudentNumberAndNoOfSubjectsApplicable.containsKey(bean.getSapid()) ?mapOfStudentNumberAndNoOfSubjectsApplicable.get(bean.getSapid()) : 0);
				//count++;
				metricsList.add(bean);
			}
			if(metricsList.size() > 0){
				rDao.batchUpsertStudentMetrics(metricsList);
			}
		}
		}catch(Exception e){
			
		}
	}*/

	@RequestMapping(value="/api/studentLearningMetrics", method = RequestMethod.POST, produces="application/json", consumes="application/json")
	public ResponseEntity<HashMap<String,String>> studentLearningMetrics() throws Exception
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		HashMap<String,String> response = new HashMap<>();
		try{
			ReportsDAO rDao = (ReportsDAO)act.getBean("reportsDAO");
			StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
			ArrayList<String> listOfSapIdOfActiveStudents = new ArrayList<String>();
			Page<StudentExamBean> page = new Page<StudentExamBean>();
			HashMap<String,String> mapOfStudentNumberSemAndCountOfFailedSubjects = new HashMap<String,String>();
			HashMap<String,String> mapOfStudentNumberSemAndCountOfPassSubjects = new HashMap<String,String>();
			HashMap<String,String> mapOfStudentNumberAndSemAndCountOfANSSubjects = new HashMap<String,String>();
			HashMap<String,String> mapOfStudentNumberAndSemAndCountOfAssignmentSubmitted = new HashMap<String,String>();
			HashMap<String,String> mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects = new HashMap<String,String>();
			HashMap<String,String> mapOfStudentNumberAndSemAndCountOfABSubjects = new HashMap<String,String>();
			HashMap<String,String> mapOfStudentNumberAndSemAndGAPInReReg = new HashMap<String,String>();
			HashMap<String,String> mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade = new HashMap<String,String>();
			HashMap<String,String> mapOfStudentNumberAndSemAndPendingNumberOfExamBookings = new HashMap<String,String>();
			HashMap<String,String> mapOfStudentNumberAndSemAndCountOfSessionsAttended = new HashMap<String,String>();
			HashMap<String, Integer> mapOfStudentNumberAndNoOfSubjectsApplicable = new HashMap<>();

			HashMap<String, StudentExamBean> studentsMap = new HashMap<>();


			int rowCount = 0;
			List<StudentExamBean> studentList = null;

			StudentExamBean studentBean = new StudentExamBean();
			studentBean.setValidStudent(true);
			page = dao.getStudentPage(Integer.parseInt("1"), Integer.MAX_VALUE, studentBean,null);
			studentList = page.getPageItems();
			rowCount = page.getRowCount();

			for(StudentExamBean student:studentList){
				listOfSapIdOfActiveStudents.add(student.getSapid());
				studentsMap.put(student.getSapid(), student);
			}


			if(listOfSapIdOfActiveStudents.size()> 0 ){

				mapOfStudentNumberSemAndCountOfFailedSubjects = rDao.getMapOfStudentNumberSemAndCountOfSubjectsFailedOrPassed(StringUtils.join(listOfSapIdOfActiveStudents,","),"FAIL");
				mapOfStudentNumberSemAndCountOfPassSubjects = rDao.getMapOfStudentNumberSemAndCountOfSubjectsFailedOrPassed(StringUtils.join(listOfSapIdOfActiveStudents,","),"PASS");
				mapOfStudentNumberAndSemAndCountOfANSSubjects = rDao.getMapOfStudentNumberAndSemAndCountOfANSSubjects(StringUtils.join(listOfSapIdOfActiveStudents,","));
				mapOfStudentNumberAndSemAndCountOfAssignmentSubmitted = rDao.getMapOfStudentNumberAndSemAndCountOfAssignmentSubmitted(StringUtils.join(listOfSapIdOfActiveStudents,","));
				//mapOfStudentNumberAndSemAndCountOfABSubjects = rDao.getMapOfStudentNumberAndSemAndCountOfTEEMissingOrABSubjects(StringUtils.join(listOfSapIdOfActiveStudents,","),"AB");

				//mapOfStudentNumberAndSemAndGAPInReReg = rDao.getMapOfStudentNumberAndSemAndGAPInReReg(StringUtils.join(listOfSapIdOfActiveStudents,","));

				mapOfStudentNumberAndSemAndPendingNumberOfExamBookings  = rDao.getMapOfStudentNumberAndSemAndPendingNumberOfExamBookings(StringUtils.join(listOfSapIdOfActiveStudents,","));
				//mapOfStudentNumberAndSemAndCountOfSessionsAttended  = rDao.getMapOfStudentNumberAndSemAndCountOfSessionsAttended(StringUtils.join(listOfSapIdOfActiveStudents,","));

				mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade = rDao.getMapOfStudentNumberAndSemAndNumberOfExamsBookings(StringUtils.join(listOfSapIdOfActiveStudents,","), "Y");
				mapOfStudentNumberAndNoOfSubjectsApplicable = rDao.getMapOfStudentNumberAndNoOfSubjectsApplicable();


				ArrayList<StudentLearningMetricsBean> metricsList = new ArrayList<StudentLearningMetricsBean>();


				//int count=0;
				for(String sapId : listOfSapIdOfActiveStudents) {
					StudentLearningMetricsBean bean = new StudentLearningMetricsBean();
					bean.setSapid(sapId);
					mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberAndSemAndCountOfANSSubjects, "ANS", bean);
					mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberSemAndCountOfFailedSubjects, "FAIL", bean);
					mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberSemAndCountOfPassSubjects, "PASS", bean);
					mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberAndSemAndPendingNumberOfExamBookings, "Exam Booking Pending", bean);
					mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberAndSemAndNumberOfExamsBookingsMade, "Exam Booked", bean);
					mapstudentLearningMatricBeanWithMetricsDetails(mapOfStudentNumberAndSemAndCountOfAssignmentSubmitted, "Assignment Submitted", bean);
					bean.setNumberOfSubjectsApplicable(mapOfStudentNumberAndNoOfSubjectsApplicable.containsKey(bean.getSapid()) ?mapOfStudentNumberAndNoOfSubjectsApplicable.get(bean.getSapid()) : 0);
					//count++;
					metricsList.add(bean);
				}
				if(metricsList.size() > 0){
					rDao.batchUpsertStudentMetrics(metricsList);
				}
			}
		}catch(Exception e){
			
			response.put("Status", "error");
			response.put("Message", "Oops! Something went wrong.");
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		response.put("Status", "success");
		response.put("Message", "Student Metrics updated successfully.");
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}



	public void mapstudentLearningMatricBeanWithMetricsDetails(Map<String,String> studentMetricsMap,String metricType, StudentLearningMetricsBean bean)
	{

		if(!studentMetricsMap.isEmpty())
		{
			for(String keyOfSapidAndSemester : studentMetricsMap.keySet())
			{
				if("ANS".equalsIgnoreCase(metricType)){

					if(studentMetricsMap.containsKey(bean.getSapid() + "-1")){
						bean.setSem1NoOfANS(studentMetricsMap.get(bean.getSapid() + "-1"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-2")){
						bean.setSem2NoOfANS(studentMetricsMap.get(bean.getSapid() + "-2"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-3")){
						bean.setSem3NoOfANS(studentMetricsMap.get(bean.getSapid() + "-3"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-4")){
						bean.setSem4NoOfANS(studentMetricsMap.get(bean.getSapid() + "-4"));
					}

					/*int totalNoOfANS = Integer.valueOf(!StringUtils.isBlank(bean.getSem1NoOfANS()) ? bean.getSem1NoOfANS() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem2NoOfANS()) ? bean.getSem2NoOfANS() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem3NoOfANS()) ? bean.getSem3NoOfANS() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem4NoOfANS()) ? bean.getSem4NoOfANS() : "0");*/

					/* old code
					 * int totalNoOfANS = NumberUtils.toInt(bean.getSem1NoOfANS()) 
										+ NumberUtils.toInt(bean.getSem2NoOfANS()) 
										+ NumberUtils.toInt(bean.getSem3NoOfANS()) 
										+ NumberUtils.toInt(bean.getSem4NoOfANS());*/
					int totalNoOfANS = bean.getSem1NoOfANS()
							+ bean.getSem2NoOfANS()
							+ bean.getSem3NoOfANS()
							+ bean.getSem4NoOfANS();
					bean.setTotalNoOfANS(totalNoOfANS);

				}else if("Exam Booked".equalsIgnoreCase(metricType)){
					if(studentMetricsMap.containsKey(bean.getSapid() + "-1")){
						bean.setSem1NoOfBookedSubjects(studentMetricsMap.get(bean.getSapid() + "-1"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-2")){
						bean.setSem2NoOfBookedSubjects(studentMetricsMap.get(bean.getSapid() + "-2"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-3")){
						bean.setSem3NoOfBookedSubjects(studentMetricsMap.get(bean.getSapid() + "-3"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-4")){
						bean.setSem4NoOfBookedSubjects(studentMetricsMap.get(bean.getSapid() + "-4"));
					}

					/*int totalNoOfExamBooking = Integer.valueOf(!StringUtils.isBlank(bean.getSem1NoOfBookedSubjects()) ? bean.getSem1NoOfBookedSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem2NoOfBookedSubjects()) ? bean.getSem2NoOfBookedSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem3NoOfBookedSubjects()) ? bean.getSem3NoOfBookedSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem4NoOfBookedSubjects()) ? bean.getSem4NoOfBookedSubjects() : "0");
					 */
					/*Old code
					 * int totalNoOfExamBooking = NumberUtils.toInt(bean.getSem1NoOfBookedSubjects()) 
							+ NumberUtils.toInt(bean.getSem2NoOfBookedSubjects()) 
							+ NumberUtils.toInt(bean.getSem3NoOfBookedSubjects()) 
							+ NumberUtils.toInt(bean.getSem4NoOfBookedSubjects());*/
					int totalNoOfExamBooking = bean.getSem1NoOfBookedSubjects() 
							+ bean.getSem2NoOfBookedSubjects() 
							+ bean.getSem3NoOfBookedSubjects() 
							+ bean.getSem4NoOfBookedSubjects();
					bean.setTotalNoOfBookedSubjects(totalNoOfExamBooking);

				}else if("Exam Booking Pending".equalsIgnoreCase(metricType)){
					if(studentMetricsMap.containsKey(bean.getSapid() + "-1")){
						bean.setSem1NoOfBookingPendingSubjects(studentMetricsMap.get(bean.getSapid() + "-1"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-2")){
						bean.setSem2NoOfBookingPendingSubjects(studentMetricsMap.get(bean.getSapid() + "-2"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-3")){
						bean.setSem3NoOfBookingPendingSubjects(studentMetricsMap.get(bean.getSapid() + "-3"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-4")){
						bean.setSem4NoOfBookingPendingSubjects(studentMetricsMap.get(bean.getSapid() + "-4"));
					}

					/*int totalNoOfExamBookingPending = Integer.valueOf(!StringUtils.isBlank(bean.getSem1NoOfBookingPendingSubjects()) ? bean.getSem1NoOfBookingPendingSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem2NoOfBookingPendingSubjects()) ? bean.getSem2NoOfBookingPendingSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem3NoOfBookingPendingSubjects()) ? bean.getSem3NoOfBookingPendingSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem4NoOfBookingPendingSubjects()) ? bean.getSem4NoOfBookingPendingSubjects() : "0");
					 */
					/*old code
					 * int totalNoOfExamBookingPending = NumberUtils.toInt(bean.getSem1NoOfBookingPendingSubjects()) 
							+ NumberUtils.toInt(bean.getSem2NoOfBookingPendingSubjects()) 
							+ NumberUtils.toInt(bean.getSem3NoOfBookingPendingSubjects()) 
							+ NumberUtils.toInt(bean.getSem4NoOfBookingPendingSubjects());*/
					int totalNoOfExamBookingPending = bean.getSem1NoOfBookingPendingSubjects() 
							+ bean.getSem2NoOfBookingPendingSubjects() 
							+ bean.getSem3NoOfBookingPendingSubjects() 
							+ bean.getSem4NoOfBookingPendingSubjects();
					bean.setTotalNoOfBookingPendingSubjects(totalNoOfExamBookingPending);

				}else if("SessionAttendance".equalsIgnoreCase(metricType)){

					if(studentMetricsMap.containsKey(bean.getSapid() + "-1")){
						bean.setSem1NoOfLecturesAttended(studentMetricsMap.get(bean.getSapid() + "-1"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-2")){
						bean.setSem2NoOfLecturesAttended(studentMetricsMap.get(bean.getSapid() + "-2"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-3")){
						bean.setSem3NoOfLecturesAttended(studentMetricsMap.get(bean.getSapid() + "-3"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-4")){
						bean.setSem4NoOfLecturesAttended(studentMetricsMap.get(bean.getSapid() + "-4"));
					}

					/*old code
					 * int totalNoOfLecturedAttended = NumberUtils.toInt(bean.getSem1NoOfLecturesAttended()) 
							+ NumberUtils.toInt(bean.getSem2NoOfLecturesAttended()) 
							+ NumberUtils.toInt(bean.getSem3NoOfLecturesAttended()) 
							+ NumberUtils.toInt(bean.getSem4NoOfLecturesAttended());*/
					int totalNoOfLecturedAttended = bean.getSem1NoOfLecturesAttended() 
							+ bean.getSem2NoOfLecturesAttended()
							+ bean.getSem3NoOfLecturesAttended()
							+ bean.getSem4NoOfLecturesAttended();
					bean.setTotalNoOfLecturedAttended(totalNoOfLecturedAttended);

				}else if("FAIL".equalsIgnoreCase(metricType)){

					if(studentMetricsMap.containsKey(bean.getSapid() + "-1")){
						bean.setSem1NoOfFailedSubjects(studentMetricsMap.get(bean.getSapid() + "-1"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-2")){
						bean.setSem2NoOfFailedSubjects(studentMetricsMap.get(bean.getSapid() + "-2"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-3")){
						bean.setSem3NoOfFailedSubjects(studentMetricsMap.get(bean.getSapid() + "-3"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-4")){
						bean.setSem4NoOfFailedSubjects(studentMetricsMap.get(bean.getSapid() + "-4"));
					}

					/*int totalNoOfFailSubjects = Integer.valueOf(!StringUtils.isBlank(bean.getSem1NoOfFailedSubjects()) ? bean.getSem1NoOfFailedSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem2NoOfFailedSubjects()) ? bean.getSem2NoOfFailedSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem3NoOfFailedSubjects()) ? bean.getSem3NoOfFailedSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem4NoOfFailedSubjects()) ? bean.getSem4NoOfFailedSubjects() : "0");
					 */

					/*old code
					 * int totalNoOfFailSubjects = NumberUtils.toInt(bean.getSem1NoOfFailedSubjects()) 
							+ NumberUtils.toInt(bean.getSem2NoOfFailedSubjects()) 
							+ NumberUtils.toInt(bean.getSem3NoOfFailedSubjects()) 
							+ NumberUtils.toInt(bean.getSem4NoOfFailedSubjects());*/
					int totalNoOfFailSubjects =bean.getSem1NoOfFailedSubjects()
							+ bean.getSem2NoOfFailedSubjects()
							+ bean.getSem3NoOfFailedSubjects() 
							+ bean.getSem4NoOfFailedSubjects();
					bean.setTotalNoOfFailedSubjects(totalNoOfFailSubjects);
				}else if("PASS".equalsIgnoreCase(metricType)){

					if(studentMetricsMap.containsKey(bean.getSapid() + "-1")){
						bean.setSem1NoOfPassedSubjects(studentMetricsMap.get(bean.getSapid() + "-1"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-2")){
						bean.setSem2NoOfPassedSubjects(studentMetricsMap.get(bean.getSapid() + "-2"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-3")){
						bean.setSem3NoOfPassedSubjects(studentMetricsMap.get(bean.getSapid() + "-3"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-4")){
						bean.setSem4NoOfPassedSubjects(studentMetricsMap.get(bean.getSapid() + "-4"));
					}

					/*int totalNoOfPassSubjects = Integer.valueOf(!StringUtils.isBlank(bean.getSem1NoOfPassedSubjects()) ? bean.getSem1NoOfPassedSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem2NoOfPassedSubjects()) ? bean.getSem2NoOfPassedSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem3NoOfPassedSubjects()) ? bean.getSem3NoOfPassedSubjects() : "0") 
							+ Integer.valueOf(!StringUtils.isBlank(bean.getSem4NoOfPassedSubjects()) ? bean.getSem4NoOfPassedSubjects() : "0");
					 */

					/*old code
					 * int totalNoOfPassSubjects = NumberUtils.toInt(bean.getSem1NoOfPassedSubjects()) 
							+ NumberUtils.toInt(bean.getSem2NoOfPassedSubjects()) 
							+ NumberUtils.toInt(bean.getSem3NoOfPassedSubjects()) 
							+ NumberUtils.toInt(bean.getSem4NoOfPassedSubjects());*/
					int totalNoOfPassSubjects = bean.getSem1NoOfPassedSubjects() 
							+ bean.getSem2NoOfPassedSubjects() 
							+ bean.getSem3NoOfPassedSubjects() 
							+ bean.getSem4NoOfPassedSubjects();

					bean.setTotalNoOfPassedSubjects(totalNoOfPassSubjects);

				}else if("Assignment Submitted".equalsIgnoreCase(metricType)){

					if(studentMetricsMap.containsKey(bean.getSapid() + "-1")){
						bean.setSem1NoOfAssignSubmitted(studentMetricsMap.get(bean.getSapid() + "-1"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-2")){
						bean.setSem2NoOfAssignSubmitted(studentMetricsMap.get(bean.getSapid() + "-2"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-3")){
						bean.setSem3NoOfAssignSubmitted(studentMetricsMap.get(bean.getSapid() + "-3"));
					}
					if(studentMetricsMap.containsKey(bean.getSapid() + "-4")){
						bean.setSem4NoOfAssignSubmitted(studentMetricsMap.get(bean.getSapid() + "-4"));
					}

					/*int totalNoOfAssignmentSubmitted = Integer.valueOf(!IsNullOrEmpty(bean.getSem1NoOfAssignSubmitted())? bean.getSem1NoOfAssignSubmitted() : "0") 
							+ Integer.valueOf(!IsNullOrEmpty(bean.getSem2NoOfAssignSubmitted()) ? bean.getSem2NoOfAssignSubmitted() : "0") 
							+ Integer.valueOf(!IsNullOrEmpty(bean.getSem3NoOfAssignSubmitted()) ? bean.getSem3NoOfAssignSubmitted() : "0") 
							+ Integer.valueOf(!IsNullOrEmpty(bean.getSem4NoOfAssignSubmitted()) ? bean.getSem4NoOfAssignSubmitted() : "0");
					 */
					/*old code
					 * int totalNoOfAssignmentSubmitted = NumberUtils.toInt(bean.getSem1NoOfAssignSubmitted()) 
							+ NumberUtils.toInt(bean.getSem2NoOfAssignSubmitted()) 
							+ NumberUtils.toInt(bean.getSem3NoOfAssignSubmitted()) 
							+ NumberUtils.toInt(bean.getSem4NoOfAssignSubmitted());
					 */
					int totalNoOfAssignmentSubmitted = bean.getSem1NoOfAssignSubmitted() 
							+ bean.getSem2NoOfAssignSubmitted() 
							+ bean.getSem3NoOfAssignSubmitted()
							+ bean.getSem4NoOfAssignSubmitted();

					bean.setTotalNoOfAssignSubmitted(totalNoOfAssignmentSubmitted);
				}
			}
		}
	}

	public boolean IsNullOrEmpty(String stringToCheck)
	{
		if(stringToCheck =="" || stringToCheck ==null || StringUtils.isBlank(stringToCheck))
		{
			return true;
		}
		return false;
	}
	@RequestMapping(value="/admin/downloadToReRegistrationReportExcel",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView downloadToReRegistrationReportExcel(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		ReportsDAO rDao = (ReportsDAO)act.getBean("reportsDAO");
		StudentMarksDAO sDao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentExamBean studentBean = (StudentExamBean)request.getSession().getAttribute("studentBean");
		Page<StudentExamBean> allActiveStudentsPage = sDao.getStudentPage(1, Integer.MAX_VALUE, studentBean, getAuthorizedCodes(request));
		List<StudentExamBean> allActiveStudentsList = allActiveStudentsPage.getPageItems();
		ArrayList<String> listOfSapIdOfActiveStudents = new ArrayList<String>();
		HashMap<String, StudentExamBean> allStudentsMap = new HashMap<>();

		for(StudentExamBean student:allActiveStudentsList){
			listOfSapIdOfActiveStudents.add(student.getSapid());
			allStudentsMap.put(student.getSapid(), student);
		}

		HashMap<String,String> mapOfStudentNumberSemAndCountOfFailedSubjects = rDao.getMapOfStudentNumberSemAndCountOfSubjectsFailedOrPassed(StringUtils.join(listOfSapIdOfActiveStudents,","),"FAIL");
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfANSSubjects = rDao.getMapOfStudentNumberAndSemAndCountOfANSSubjects(StringUtils.join(listOfSapIdOfActiveStudents,","));
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfABSubjects = rDao.getMapOfStudentNumberAndSemAndCountOfTEEMissingOrABSubjects(StringUtils.join(listOfSapIdOfActiveStudents,","),"AB");
		HashMap<String,String> mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects = rDao.getMapOfStudentNumberAndSemAndCountOfTEEMissingOrABSubjects(StringUtils.join(listOfSapIdOfActiveStudents,","),"TEE");
		HashMap<String,String> mapOfStudentNumberAndSemAndGAPInReReg = rDao.getMapOfStudentNumberAndSemAndGAPInReReg(StringUtils.join(listOfSapIdOfActiveStudents,","));
		HashMap<String,String> mapOfStudentNumberAndSemAndPendingNumberOfExamBookings  = rDao.getMapOfStudentNumberAndSemAndPendingNumberOfExamBookings(StringUtils.join(listOfSapIdOfActiveStudents,","));
		HashMap<String,String>mapOfStudentNumberAndSemAndCountOfSessionsAttended  = rDao.getMapOfStudentNumberAndSemAndCountOfSessionsAttended(StringUtils.join(listOfSapIdOfActiveStudents,","));
		HashMap<String,String> mapOfStudentNumberAndSemAndDriveMonthYear = rDao.getMapOfStudentNumberAndSemAndDriveMonthYear(StringUtils.join(listOfSapIdOfActiveStudents,","));


		request.getSession().setAttribute("allStudentsMap", allStudentsMap);
		request.getSession().setAttribute("mapOfStudentNumberSemAndCountOfFailedSubjects", mapOfStudentNumberSemAndCountOfFailedSubjects);
		request.getSession().setAttribute("mapOfStudentNumberAndSemAndCountOfANSSubjects", mapOfStudentNumberAndSemAndCountOfANSSubjects);
		request.getSession().setAttribute("mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects", mapOfStudentNumberAndSemAndCountOfTEEMissingSubjects);
		request.getSession().setAttribute("mapOfStudentNumberAndSemAndCountOfABSubjects", mapOfStudentNumberAndSemAndCountOfABSubjects);
		request.getSession().setAttribute("mapOfStudentNumberAndSemAndGAPInReReg", mapOfStudentNumberAndSemAndGAPInReReg);
		request.getSession().setAttribute("mapOfStudentNumberAndSemAndPendingNumberOfExamBookings", mapOfStudentNumberAndSemAndPendingNumberOfExamBookings);
		request.getSession().setAttribute("mapOfStudentNumberAndSemAndCountOfSessionsAttended", mapOfStudentNumberAndSemAndCountOfSessionsAttended);
		request.getSession().setAttribute("mapOfStudentNumberAndSemAndDriveMonthYear", mapOfStudentNumberAndSemAndDriveMonthYear);

		return new ModelAndView("reRegistrationReportExcelView", "listOfActiveSapId", listOfSapIdOfActiveStudents);
	}

	@RequestMapping(value = "/admin/reportHome", method = {RequestMethod.GET, RequestMethod.POST})
	public String reportHome(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		return "report/reportHome";
	}

	@RequestMapping(value = "/admin/programCompleteReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String programCompleteReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", yearList);

		return "report/programCompleteReport";
	}
	//Active Student Report//
	@RequestMapping(value="/admin/activeStudentReport",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView activeStudentReport(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
		PassFailDAO pdao = (PassFailDAO)act.getBean("passFailDAO");
		ArrayList<StudentExamBean> activeStudentsWithRecentRegistrationsList = dao.getActiveStudentsWithRecentRegistrations(getAuthorizedCodes(request));
		//HashMap<String,ArrayList<PassFailBean>> mapOfSapIdAndSemAndPassFailList = pdao.getMapOfSapIdAndSemAndPassFailList();
		//request.getSession().setAttribute("mapOfSapIdAndSemAndPassFailList", mapOfSapIdAndSemAndPassFailList);
		return new ModelAndView("activeStudentSemWiseRegistrationExcelView","activeStudentsWithRecentRegistrationsList",activeStudentsWithRecentRegistrationsList);//BeanName,variableName,variable//

	}




	@RequestMapping(value="/admin/makeStudentListAlumni",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView makeStudentListAlumni(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelAndView = new ModelAndView("report/programCompleteReport");
		List<PassFailExamBean> studentMarksList = (List<PassFailExamBean>)request.getSession().getAttribute("programCompleteList");
		ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
		StudentMarksBean marks = new StudentMarksBean();
		modelAndView.addObject("studentMarks",marks);
		modelAndView.addObject("yearList", yearList);

		try{
			dao.setStudentListToAlumni(studentMarksList);
			setSuccess(request," Below list made set to Alumni. Record Count : "+studentMarksList.size());
		}catch(Exception e){
			setError(request,"Error in updating records.");
			
		}

		return modelAndView;
	}

	//start--Added to send email for Alumni Students.
	@RequestMapping(value="/admin/sendEmailToPCStudents",method={RequestMethod.POST})
	public ModelAndView sendEmailToPCStudents(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelAndView = new ModelAndView("report/programCompleteReport");
		String userId = (String)request.getSession().getAttribute("userId");
		List<PassFailExamBean> studentMarksList = (List<PassFailExamBean>)request.getSession().getAttribute("programCompleteList");
		try{

			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			StudentMarksBean marks = new StudentMarksBean();
			modelAndView.addObject("studentMarks",marks);
			modelAndView.addObject("yearList", yearList);
			List<String> toSapId = new ArrayList<>();
			for(PassFailExamBean bean : studentMarksList){
				toSapId.add(bean.getSapid());
			}
			List<String> studentEmailList= dao.getEmailofPCStudents(toSapId);
			if(studentEmailList.isEmpty() || studentEmailList == null){
				setError(request,"Mails not sent.Kindly Mark Students as Alumni first");
			}else{
				String subject = "Program Completion";
				String fromEmailId = "ngasce@nmims.edu";
				String mailBody = "Program Completion Satus Mail";
				MailSender mailSender = (MailSender)act.getBean("mailer");
				mailSender.sendProgramCompletedEmail(subject,mailBody,studentEmailList);
				dao.getSentEmailToPCStudents(studentMarksList);
				setSuccess(request," Email sent to Students. Record Count : "+studentMarksList.size());
			}
		}catch(Exception e){
			setError(request,"Mails not sent");
			MailSender mailSender = (MailSender)act.getBean("mailer");
			mailSender.mailStackTrace("Error in Sending Mail", e);
			//
		}
		return modelAndView;
	}
	//end

	@RequestMapping(value = "/admin/programCompleteReport", method = RequestMethod.POST)
	public ModelAndView programCompleteReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("report/programCompleteReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		List<PassFailExamBean> studentMarksList = new ArrayList<>();
		ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");

		Map<String, ProgramExamBean> programDetails = getAllProgramMap();
		for(Map.Entry<String,ProgramExamBean> entry : programDetails.entrySet()){
			if("MBA - WX".equalsIgnoreCase(entry.getValue().getProgram()) && "Y".equalsIgnoreCase(entry.getValue().getActive())) {
				studentMarksList.addAll(dao.getMBAWXProgramCompletedReport(Integer.parseInt(entry.getValue().getNoOfSubjectsToClear()), getAuthorizedCodes(request), entry.getValue().getProgramStructure(), studentMarks.getYear(), studentMarks.getMonth()));
				continue;
			}
			else if("MBA - X".equalsIgnoreCase(entry.getValue().getProgram()) && "Y".equalsIgnoreCase(entry.getValue().getActive())) {
				studentMarksList.addAll(dao.getMBAXProgramCompletedReport(Integer.parseInt(entry.getValue().getNoOfSubjectsToClear()), getAuthorizedCodes(request), entry.getValue().getProgramStructure(), studentMarks.getYear(),studentMarks.getMonth()));
				continue;
			}
			else {
				studentMarksList.addAll(dao.getProgramCompletedReport(studentMarks, entry.getValue().getProgram(),
						Integer.parseInt(entry.getValue().getNoOfSubjectsToClear()), 
						entry.getValue().getProgramStructure(), false,getAuthorizedCodes(request)) );
	
				
				studentMarksList.addAll(reportService.getStudentPassList(studentMarks, entry.getValue().getProgram(),
						Integer.parseInt(entry.getValue().getNoOfSubjectsToClear()), 
						entry.getValue().getProgramStructure(), true,getAuthorizedCodes(request)) );
			}
		}

		/*List<PassFailBean> oldCertificateStudentsList = new ArrayList<>();
		List<PassFailBean> oldDiploma1StudentsList = new ArrayList<>();
		List<PassFailBean> oldDiploma2StudentsList = new ArrayList<>();
		List<PassFailBean> oldPgDiplomaStudentsList = new ArrayList<>();
		List<PassFailBean> oldPgDiplomaLateralStudentsList = new ArrayList<>(); 
		List<PassFailBean> oldPDiplomaStudentsList = new ArrayList<>();

		List<PassFailBean> newCertificateStudentsList = new ArrayList<>();
		List<PassFailBean> newCertificateStudentsList2 = new ArrayList<>();
		List<PassFailBean> newCertificateStudentsList3 = new ArrayList<>();
		List<PassFailBean> newDiplomaStudentsList = new ArrayList<>();
		List<PassFailBean> newPgDiplomaStudentsList = new ArrayList<>();
		List<PassFailBean> newPgDiplomaLateralStudentsList = new ArrayList<>();

		if(studentMarks.getExamMode() != null && !"".equals(studentMarks.getExamMode())){
			if("Online".equalsIgnoreCase(studentMarks.getExamMode())){
				newDiplomaStudentsList = dao.getProgramCompletedReport(studentMarks, "D", 12, "Jul2014", false,getAuthorizedCodes(request)); 
				newPgDiplomaStudentsList = dao.getProgramCompletedReport(studentMarks, "P", 24, "Jul2014", false,getAuthorizedCodes(request));
				newPgDiplomaLateralStudentsList = dao.getProgramCompletedReport(studentMarks, "P", 12, "Jul2014", true,getAuthorizedCodes(request));//Lateral PG will need 12 subjects to pass
				oldCertificateStudentsList = dao.getProgramCompletedReport(studentMarks, "C", 5, "Jul2013", false,getAuthorizedCodes(request));
				newCertificateStudentsList = dao.getProgramCompletedReport(studentMarks, "C", 6, "Jul2017", false,getAuthorizedCodes(request));
				newCertificateStudentsList2 = dao.getProgramCompletedReport(studentMarks, "C", 5, "Jul2017", false,getAuthorizedCodes(request));
				newCertificateStudentsList3 = dao.getProgramCompletedReport(studentMarks, "C", 2, "Jul2017", false,getAuthorizedCodes(request));
			}else if("Offline".equalsIgnoreCase(studentMarks.getExamMode())){
				oldDiploma1StudentsList = dao.getProgramCompletedReport(studentMarks, "D", 10, "Jul2009", false,getAuthorizedCodes(request)); //Diploma launched in 2009
				oldDiploma2StudentsList = dao.getProgramCompletedReport(studentMarks, "D", 10, "Jul2013", false,getAuthorizedCodes(request)); //Diploma launched in 2013
				oldPgDiplomaStudentsList = dao.getProgramCompletedReport(studentMarks, "P", 20, "Jul2009", false,getAuthorizedCodes(request));
				oldPDiplomaStudentsList = dao.getProgramCompletedReport(studentMarks, "P", 20, "Jul2013", false,getAuthorizedCodes(request));//Added by Steffi for PG in Jul2013
				oldPgDiplomaLateralStudentsList = dao.getProgramCompletedReport(studentMarks, "P", 10, "Jul2009", true,getAuthorizedCodes(request));//Lateral PG will need 10 subjects to pass
			}
		}else{
			oldCertificateStudentsList = dao.getProgramCompletedReport(studentMarks, "C", 5, "Jul2013", false,getAuthorizedCodes(request));
			oldDiploma1StudentsList = dao.getProgramCompletedReport(studentMarks, "D", 10, "Jul2009", false,getAuthorizedCodes(request)); //Diploma launched in 2009
			oldDiploma2StudentsList = dao.getProgramCompletedReport(studentMarks, "D", 10, "Jul2013", false,getAuthorizedCodes(request)); //Diploma launched in 2013
			oldPgDiplomaStudentsList = dao.getProgramCompletedReport(studentMarks, "P", 20, "Jul2009", false,getAuthorizedCodes(request));
			oldPgDiplomaLateralStudentsList = dao.getProgramCompletedReport(studentMarks, "P", 10, "Jul2009", true,getAuthorizedCodes(request));//Lateral PG will need 10 subjects to pass
			oldPDiplomaStudentsList = dao.getProgramCompletedReport(studentMarks, "P", 20, "Jul2013", false,getAuthorizedCodes(request));//Added by Steffi for PG in Jul2013

			newDiplomaStudentsList = dao.getProgramCompletedReport(studentMarks, "D", 12, "Jul2014", false,getAuthorizedCodes(request)); 
			newPgDiplomaStudentsList = dao.getProgramCompletedReport(studentMarks, "P", 24, "Jul2014", false,getAuthorizedCodes(request));
			newPgDiplomaLateralStudentsList = dao.getProgramCompletedReport(studentMarks, "P", 12, "Jul2014", true,getAuthorizedCodes(request));//Lateral PG will need 12 subjects to pass
			newCertificateStudentsList = dao.getProgramCompletedReport(studentMarks, "C", 6, "Jul2017", false,getAuthorizedCodes(request));
			newCertificateStudentsList2 = dao.getProgramCompletedReport(studentMarks, "C", 5, "Jul2017", false,getAuthorizedCodes(request));
			newCertificateStudentsList3 = dao.getProgramCompletedReport(studentMarks, "C", 2, "Jul2017", false,getAuthorizedCodes(request));

		}

		studentMarksList.addAll(oldCertificateStudentsList);
		studentMarksList.addAll(oldDiploma1StudentsList);
		studentMarksList.addAll(oldDiploma2StudentsList);
		studentMarksList.addAll(oldPgDiplomaStudentsList);
		studentMarksList.addAll(oldPgDiplomaLateralStudentsList);
		studentMarksList.addAll(oldPDiplomaStudentsList);
		studentMarksList.addAll(newDiplomaStudentsList);
		studentMarksList.addAll(newPgDiplomaStudentsList);
		studentMarksList.addAll(newPgDiplomaLateralStudentsList);
		studentMarksList.addAll(newCertificateStudentsList);
		studentMarksList.addAll(newCertificateStudentsList2);
		studentMarksList.addAll(newCertificateStudentsList3);*/

		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", yearList);

		if(studentMarksList == null || studentMarksList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}
		modelnView.addObject("studentMarksList", studentMarksList);
		request.getSession().setAttribute("programCompleteList",studentMarksList);
		modelnView.addObject("rowCount",studentMarksList.size());
		return modelnView;
	}


	@RequestMapping(value = "/admin/downloadProgramCompleteReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadProgramCompleteReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		List<PassFailExamBean> programCompleteList = (ArrayList<PassFailExamBean>)request.getSession().getAttribute("programCompleteList");
		return new ModelAndView("programCompleteReportExcelView","programCompleteList",programCompleteList);
	}

	@RequestMapping(value = "/admin/downloadCertficateCSV", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadCertficateCSV(HttpServletRequest request, HttpServletResponse response) {
		/*if(!checkSession(request, response)){
			return new ModelAndView("login");
		}*/
		List<PassFailExamBean> programCompleteList = (ArrayList<PassFailExamBean>)request.getSession().getAttribute("programCompleteList");
		ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
		String year = request.getParameter("year");
		String month = request.getParameter("month");
		HashMap<String, StudentExamBean> mapOfSapIdAndStudent = dao.getAllStudentsWithCenter(getAuthorizedCodes(request));
		ArrayList<ProgramCompleteReportBean> programCompleteStudentsList =  dao.getProgramCompleteStudentsDetails(programCompleteList, year, month);

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=\"Certificate_Generation_"+month + "_"+year +".csv\"");
		try
		{
			OutputStream outputStream = response.getOutputStream();
			String columnHeaders = "Cert #,Certificate Profile,Student Number,Last Name,First Name,Father Name,Mother Name,School,Qualification Title,Specialization,Date of Qualification,Month of Qualification,Year of Qualification,Presentation Date,Roll No,Transcript Year,Session,Transcript Examination Month,Transcript Examination Year,Academic Year,Transcript Data,GPA,CGPA,Remark,Transcript Declaration Date,Transcript Issue Date,IsCertificate\n";
			StringBuffer fileContents = new StringBuffer();
			fileContents.append(columnHeaders);

			int counter = 1;
			SimpleDateFormat  parser = new SimpleDateFormat("yyyy-MM-dd");
			Format formatterMonth = new SimpleDateFormat("MMMM");
			Format formatterMonthForQualification = new SimpleDateFormat("MM");
			Format formatterYear = new SimpleDateFormat("yyyy");
			Format formatterDay = new SimpleDateFormat("dd");
			for (ProgramCompleteReportBean bean : programCompleteStudentsList) {


				StudentExamBean student = null;
				String qualificationMonth = "";
				String qualificationMonthForQualification = "";
				String qualificationYear = "";
				String qualificationDayOfMonth = "";
				student = mapOfSapIdAndStudent.get(bean.getSapid());

				if("Online".equalsIgnoreCase(student.getExamMode())){

					qualificationMonth = formatterMonth.format(parser.parse(bean.getDeclareDate()));
					qualificationMonthForQualification = formatterMonthForQualification.format(parser.parse(bean.getDeclareDate()));//Temporary Logic for getting only month number-Vikas//
					qualificationYear = formatterYear.format(parser.parse(bean.getDeclareDate()));
					qualificationDayOfMonth = formatterDay.format(parser.parse(bean.getDeclareDate()));
				}
				else{

					qualificationMonth = formatterMonth.format(parser.parse(bean.getOflineResultsDeclareDate()));
					qualificationMonthForQualification = formatterMonthForQualification.format(parser.parse(bean.getOflineResultsDeclareDate()));
					qualificationYear = formatterYear.format(parser.parse(bean.getOflineResultsDeclareDate()));
					qualificationDayOfMonth = formatterDay.format(parser.parse(bean.getOflineResultsDeclareDate()));
				}

				String lastName = bean.getLastName();
				String firstName = bean.getFirstName();
				String fatherName = (bean.getFatherName() != null && !"".equals(bean.getFatherName().trim())) ? bean.getFatherName() : "   ";
				String motherName = (bean.getMotherName() != null && !"".equals(bean.getMotherName().trim())) ? bean.getMotherName() : "   ";

				String row = (counter++) +","
						+ "SDL,"
						+ bean.getSapid()+ ","
						+ lastName.substring(0, 1).toUpperCase() + lastName.substring(1) +","
						+ firstName.substring(0, 1).toUpperCase() + firstName.substring(1) +","
						+ fatherName.substring(0, 1).toUpperCase() + fatherName.substring(1) +","
						+ motherName.substring(0, 1).toUpperCase() + motherName.substring(1) +","
						+ "(NMIMS Global Access - School for Continuing Education),"
						+ bean.getProgramname() +","
						+ ","
						+ qualificationDayOfMonth+"-"+qualificationMonthForQualification+"-"+qualificationYear +","
						//+ bean.getMonth() +","
						//+ bean.getYear() +","

						+ qualificationMonth +","
						+ qualificationYear +","


						+ qualificationMonth+" "+qualificationDayOfMonth+" "+qualificationYear +","
						+ ",,,,,,,,,,,,Yes\n";

				fileContents.append(row);
			}

			outputStream.write(fileContents.toString().getBytes());
			outputStream.flush();
			outputStream.close();
		}
		catch(Exception e)
		{
			
		}

		return null;
	}

	@RequestMapping(value = "/admin/expectedExamRegistrationReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String expectedExamRegistrationReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "report/expectedExamRegistrationReport";
	}

	@RequestMapping(value = "/admin/expectedExamRegistrationReport", method = RequestMethod.POST)
	public ModelAndView expectedExamRegistrationReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/expectedExamRegistrationReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<StudentExamBean> neverRegisteredStudentList = dao.getStudentsNeverRegisteredForExam(studentMarks);
			ArrayList<StudentExamBean> registeredFailedStudentList = dao.getStudentsRegisteredButFailed(studentMarks);
			ArrayList<StudentExamBean> newRegisteredStudentList = dao.getStudentsNewlyRegistered(studentMarks);

			ArrayList<String> validStudentList = dao.getStudentsWithValidity(studentMarks);

			ArrayList<StudentExamBean> allStudentsList = new ArrayList<>();
			HashMap<String, StudentExamBean> allStudentsMap = new HashMap<>();

			if(validStudentList == null){
				validStudentList = new ArrayList<>();
			}

			for(StudentExamBean student : neverRegisteredStudentList) {
				if(validStudentList.contains(student.getSapid()) && "Online".equals(student.getExamMode())){
					allStudentsMap.put(student.getSapid().trim()+"~"+student.getSubject().trim(), student);
				}

			}

			for(StudentExamBean student : registeredFailedStudentList) {
				if(validStudentList.contains(student.getSapid()) && "Online".equals(student.getExamMode()) ){
					allStudentsMap.put(student.getSapid().trim()+"~"+student.getSubject().trim(), student);
				}

			}

			for (StudentExamBean student : newRegisteredStudentList) {
				if("Online".equals(student.getExamMode())){
					allStudentsMap.put(student.getSapid().trim()+"~"+student.getSubject().trim(), student);
				}

			}


			allStudentsList.addAll(allStudentsMap.values());

			HashMap<String, Integer> subjectCityStudentCountMap = new HashMap<>();

			for (StudentExamBean studentBean : allStudentsList) {
				String subject = studentBean.getSubject();
				String city = studentBean.getCity();

				String key = subject + "~" + city;

				if(!subjectCityStudentCountMap.containsKey(key)){
					Integer count = new Integer(0);
					count++;
					subjectCityStudentCountMap.put(key, count);
				}else{
					Integer count = subjectCityStudentCountMap.get(key);
					count++;
					subjectCityStudentCountMap.put(key, count);
				}
			}

			TreeMap<String, Integer> sortedSubjectCityStudentCountMap = new TreeMap<String, Integer>(subjectCityStudentCountMap); 

			if(allStudentsMap != null && allStudentsMap.size() > 0){
				modelnView.addObject("rowCount",allStudentsMap.size());
				modelnView.addObject("centerCodeNameMap",getCenterCodeNameMap(""));
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
				modelnView.addObject("subjectCityStudentCountMap",subjectCityStudentCountMap);
				modelnView.addObject("sortedSubjectCityStudentCountMap",sortedSubjectCityStudentCountMap);

				request.getSession().setAttribute("subjectCityStudentCountMap", subjectCityStudentCountMap);
				request.getSession().setAttribute("sortedSubjectCityStudentCountMap", sortedSubjectCityStudentCountMap);
				request.getSession().setAttribute("allStudentsList", allStudentsList);
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}


	@RequestMapping(value = "/admin/expectedExamRegistrationReportAfterAssignmentSubmission", method = RequestMethod.POST)
	public ModelAndView expectedExamRegistrationReportAfterAssignmentSubmission(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/expectedExamRegistrationReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<StudentExamBean> registeredFailedStudentList = dao.getStudentsRegisteredButFailed(studentMarks);
			ArrayList<StudentExamBean> assignmentSubmittedStudentList = dao.getStudentsAssignmentSubmitted(studentMarks);

			ArrayList<String> validStudentList = dao.getStudentsWithValidity(studentMarks);

			ArrayList<StudentExamBean> allStudentsList = new ArrayList<>();
			HashMap<String, StudentExamBean> allStudentsMap = new HashMap<>();

			if(validStudentList == null){
				validStudentList = new ArrayList<>();
			}



			for(StudentExamBean student : registeredFailedStudentList) {
				if(validStudentList.contains(student.getSapid()) && "Online".equals(student.getExamMode())){
					allStudentsMap.put(student.getSapid().trim()+"~"+student.getSubject().trim(), student);
				}
			}

			for(StudentExamBean student : assignmentSubmittedStudentList) {
				if(validStudentList.contains(student.getSapid()) && "Online".equals(student.getExamMode())){
					allStudentsMap.put(student.getSapid().trim()+"~"+student.getSubject().trim(), student);
				}
			}


			allStudentsList.addAll(allStudentsMap.values());

			HashMap<String, Integer> subjectCityStudentCountMap = new HashMap<>();

			for (StudentExamBean studentBean : allStudentsList) {
				String subject = studentBean.getSubject();
				String city = studentBean.getCity();

				String key = subject + "~" + city;

				if(!subjectCityStudentCountMap.containsKey(key)){
					Integer count = new Integer(0);
					count++;
					subjectCityStudentCountMap.put(key, count);
				}else{
					Integer count = subjectCityStudentCountMap.get(key);
					count++;
					subjectCityStudentCountMap.put(key, count);
				}
			}

			TreeMap<String, Integer> sortedSubjectCityStudentCountMap = new TreeMap<String, Integer>(subjectCityStudentCountMap); 

			if(allStudentsMap != null && allStudentsMap.size() > 0){
				modelnView.addObject("rowCount",allStudentsMap.size());
				modelnView.addObject("centerCodeNameMap",getCenterCodeNameMap(""));
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
				modelnView.addObject("subjectCityStudentCountMap",subjectCityStudentCountMap);
				modelnView.addObject("sortedSubjectCityStudentCountMap",sortedSubjectCityStudentCountMap);

				request.getSession().setAttribute("subjectCityStudentCountMap", subjectCityStudentCountMap);
				request.getSession().setAttribute("sortedSubjectCityStudentCountMap", sortedSubjectCityStudentCountMap);
				request.getSession().setAttribute("allStudentsList", allStudentsList);
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}


	@RequestMapping(value = "/admin/downloadExpectedExamRegistrationReport", method = {RequestMethod.GET})
	public ModelAndView downloadExpectedExamRegistrationReport(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ArrayList<StudentExamBean> allStudentsList =  (ArrayList<StudentExamBean>)request.getSession().getAttribute("allStudentsList");
		TreeMap<String, Integer> sortedSubjectCityStudentCountMap = (TreeMap<String, Integer>)request.getSession().getAttribute("sortedSubjectCityStudentCountMap");
		ModelAndView modelnView = new ModelAndView("report/expectedExamRegistrationReport");
		StudentMarksBean bean = (StudentMarksBean)request.getSession().getAttribute("studentMarks");
		//ExcelHelper helper = new ExcelHelper();
		ExpectedExamRegistrationReportExcelView helper = new ExpectedExamRegistrationReportExcelView();
		ArrayList<Object> data = new ArrayList<>();
		data.add(allStudentsList);
		data.add(sortedSubjectCityStudentCountMap);
		try{
			//helper.buildExcelDocument(data,response);
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Report generated successfully");
			return new ModelAndView("expectedExamRegistrationReportExcelView","data",data);

		}catch(Exception e){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error generating report.");
				
		}
		modelnView.addObject("studentMarks", bean);
		return modelnView;
		//return new ModelAndView("expectedExamRegistrationReportExcelView","data",data);
	}
	//Newly Added-Start// 
	@RequestMapping(value="/admin/assignmentPaymentReportForm",method={RequestMethod.GET, RequestMethod.POST})
	public String assignmentPaymentReportForm(HttpServletRequest request, HttpServletResponse response,Model m){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "report/assignmentPaymentReport";
	}
	@RequestMapping(value="/admin/assignmentPaymentReport",method={RequestMethod.POST})
	public ModelAndView assignmentPaymentReport(HttpServletRequest request, HttpServletResponse response,@ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/assignmentPaymentReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<AssignmentPaymentBean> assignmentPaymentList = dao.getAssignmentPaymentsForGivenYearMonth(studentMarks);
			request.getSession().setAttribute("assignmentPaymentList",assignmentPaymentList);

			if(assignmentPaymentList != null && assignmentPaymentList.size() > 0){
				modelnView.addObject("rowCount",assignmentPaymentList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}
	@RequestMapping(value = "/admin/downloadAssignmentPaymentReport", method = {RequestMethod.GET})
	public ModelAndView downloadAssignmentPaymentReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<AssignmentPaymentBean> assignmentPaymentList = (ArrayList<AssignmentPaymentBean>)request.getSession().getAttribute("assignmentPaymentList");
		return new ModelAndView("assignmentPaymentReportExcelView","assignmentPaymentList",assignmentPaymentList);
	}
	//Ends here-End//
	@RequestMapping(value = "/admin/examBookingReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String examBookingReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", ACAD_YEAR_LIST);

		return "report/examBookingReport";
	}

	@RequestMapping(value = "/admin/examBookingReport", method = RequestMethod.POST)
	public ModelAndView examBookingReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("report/examBookingReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<ExamBookingTransactionBean> examBookingList = dao.getConfirmedBookingForGivenYearMonth(studentMarks, getAuthorizedCodes(request));
			ArrayList<ExamBookingTransactionBean> examBookingProjectFeeExemptList = dao.getConfirmedBookingForGivenYearMonthProjectFeeExemptList(studentMarks, getAuthorizedCodes(request));
			examBookingList.addAll(examBookingProjectFeeExemptList);

			request.getSession().setAttribute("examBookingList",examBookingList);


			if(examBookingList != null && examBookingList.size() > 0){
				modelnView.addObject("rowCount",examBookingList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadExamBookingReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExamBookingReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		List<ExamBookingTransactionBean> examBookingList = (ArrayList<ExamBookingTransactionBean>)request.getSession().getAttribute("examBookingList");

		return new ModelAndView("examBookingReportExcelView","examBookingList",examBookingList);
	}

	@RequestMapping(value = "/admin/toppersReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String toppersReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "report/toppersReport";
	}

	@RequestMapping(value = "/admin/toppersReport", method = RequestMethod.POST)
	public ModelAndView toppersReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/toppersReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<StudentExamBean> studentList = dao.getToppers(studentMarks, getAuthorizedCodes(request));


			if(studentList != null && studentList.size() > 0){
				modelnView.addObject("rowCount",studentList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");

				HashMap<String, CenterExamBean> centerCodeNameMap = getCentersMap();
				for (StudentExamBean studentBean : studentList) {
					String lc = centerCodeNameMap.get(studentBean.getCenterCode()) != null ? centerCodeNameMap.get(studentBean.getCenterCode()).getLc() : "No LC";
					studentBean.setLc(lc);
				}

			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
			request.getSession().setAttribute("studentList",studentList);
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadToppersReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadToppersReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		List<StudentExamBean> studentList = (ArrayList<StudentExamBean>)request.getSession().getAttribute("studentList");
		return new ModelAndView("toppersReportExcelView","studentList",studentList);
	}

	@RequestMapping(value = "/admin/semWiseSubjetsClearedReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String semWiseSubjetsClearedReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "report/semWiseSubjetsClearedReport";
	}

	@RequestMapping(value = "/admin/semWiseSubjetsClearedReport", method = RequestMethod.POST)
	public ModelAndView semWiseSubjetsClearedReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/semWiseSubjetsClearedReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<StudentExamBean> studentList = dao.getSemWiseSubjectsCleared(studentMarks, getAuthorizedCodes(request));


			if(studentList != null && studentList.size() > 0){
				modelnView.addObject("rowCount",studentList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");

				HashMap<String, CenterExamBean> centerCodeNameMap = getCentersMap();
				for (StudentExamBean studentBean : studentList) {
					String lc = centerCodeNameMap.get(studentBean.getCenterCode()) != null ? centerCodeNameMap.get(studentBean.getCenterCode()).getLc() : "No LC";
					studentBean.setLc(lc);
				}

			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
			request.getSession().setAttribute("studentList",studentList);
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/admin/studentClearingSubjectsReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String studentClearingCertainNoOfSubjectsReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "report/studentClearingCertainNoOfSubjectsReport";
	}

	@RequestMapping(value = "/admin/studentClearingSubjectsReport", method = RequestMethod.POST)
	public ModelAndView studentClearingSubjectsReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/studentClearingCertainNoOfSubjectsReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<StudentExamBean> studentList = dao.getStudentClearingCertainNoOfSubjects(studentMarks, getAuthorizedCodes(request));


			if(studentList != null && studentList.size() > 0){
				modelnView.addObject("rowCount",studentList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");

				HashMap<String, CenterExamBean> centerCodeNameMap = getCentersMap();
				for (StudentExamBean studentBean : studentList) {
					String lc = centerCodeNameMap.get(studentBean.getCenterCode()) != null ? centerCodeNameMap.get(studentBean.getCenterCode()).getLc() : "No LC";
					studentBean.setLc(lc);
				}

			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
			request.getSession().setAttribute("studentList",studentList);
		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadStudentClearingSubjectsReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadStudentClearingSubjectsReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		List<StudentExamBean> studentList = (ArrayList<StudentExamBean>)request.getSession().getAttribute("studentList");

		return new ModelAndView("studentClearingSubjectsReportExcelView","studentList",studentList);
	}

	@RequestMapping(value = "/admin/onlineExamPasswordReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String onlineExamPasswordReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("monthList", SAS_EXAM_MONTH_LIST);
		return "report/onlineExamPasswordReport";
	}


	/*@RequestMapping(value = "/onlineExamPasswordReport", method = RequestMethod.POST)
	public ModelAndView onlineExamPasswordReport(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute StudentMarksBean studentMarks){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/onlineExamPasswordReport");
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("monthList", SAS_EXAM_MONTH_LIST);
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<ExamBookingTransactionBean> examBookingList = dao.getConfirmedOnlineExamBookingForGivenYearMonth(studentMarks);

		//  Added on 8/2/2018 by Steffi
		  	HashMap<String,ExamBookingTransactionBean> examBookingSapidSubjectMap = new HashMap<String,ExamBookingTransactionBean>();
			int count = 0;
		  	if(examBookingList != null && !examBookingList.isEmpty()) {
				 for(ExamBookingTransactionBean e: examBookingList){
					 String key1 = e.getSapid()+"|"+e.getSubject();
					 String key2 = e.getSapid()+"|"+e.getExamDate()+"|"+e.getExamTime();

					 if(!examBookingSapidSubjectMap.containsKey(key1) && !examBookingSapidSubjectMap.containsKey(key2)){
						 examBookingSapidSubjectMap.put(key1, e);
						 examBookingSapidSubjectMap.put(key2, e);
						 count++;
					 }else{
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error in "+examBookingSapidSubjectMap.get(key1).getSapid()+examBookingSapidSubjectMap.get(key1).getSubject());
						return modelnView; 
					 }
				 }
			} 
			// Added on 8/2/2018 by Steffi end
			ArrayList<ExamBookingTransactionBean> listOfExecutiveExamBookings=dao.listOfExecutiveExamBookings(studentMarks);
			HashMap<String,ExamBookingTransactionBean> examBookingExecutiveSapidSubjectMap = new HashMap<String,ExamBookingTransactionBean>();
			// Added on 8/2/2018 by Steffi
			int count2 = 0;
			  if(listOfExecutiveExamBookings != null && !listOfExecutiveExamBookings.isEmpty()) {
					 for(ExamBookingTransactionBean e: listOfExecutiveExamBookings){
						 String key1 = e.getSapid()+"|"+e.getSubject();
						 String key2 = e.getSapid()+"|"+e.getExamDate()+"|"+e.getExamTime();

						 if(!examBookingExecutiveSapidSubjectMap.containsKey(key1) && !examBookingExecutiveSapidSubjectMap.containsKey(key2)){
							 examBookingExecutiveSapidSubjectMap.put(key1, e);
							 examBookingExecutiveSapidSubjectMap.put(key2, e);
							 count2++;
						 }else{
							 request.setAttribute("error", "true");
								request.setAttribute("errorMessage", "Error in  "+examBookingExecutiveSapidSubjectMap.get(key1).getSapid());
								return modelnView;
						 }
					 }
					 } 
				// Added on 8/2/2018 by Steffi end

			HashMap<String, String> studentPasswordMap = dao.getStudentExamPasswordsForGivenYearMonth(studentMarks);
			for (int i = 0; i < examBookingList.size(); i++) {
				ExamBookingTransactionBean bean = examBookingList.get(i);
				bean.setPassword(studentPasswordMap.get(bean.getSapid()));
			}

			//executive students
			String assignPass=null;
			ArrayList<StudentMarksBean> listOfAllExecutiveBookings=null;
			for(ExamBookingTransactionBean listOfBeans:listOfExecutiveExamBookings) {
				//generatePass();
                assignPass=generatePass();
                studentMarks.setPassword(assignPass);
                studentMarks.setSapid(listOfBeans.getSapid());
                studentMarks.setMonth(listOfBeans.getMonth());
                studentMarks.setYear(listOfBeans.getYear());
                studentMarks.setExamDate(listOfBeans.getExamDate());
                studentMarks.setExamTime(listOfBeans.getExamTime());
                studentMarks.setEmailId(listOfBeans.getEmailId());
                studentMarks.setProgramStructApplicable(listOfBeans.getProgramStructApplicable());
                studentMarks.setSifySubjectCode(listOfBeans.getSifySubjectCode());
                dao.assignPassword(studentMarks);
			}


			listOfAllExecutiveBookings=dao.listOfBookings(studentMarks);

			request.getSession().setAttribute("listOfAllExecutiveBookings",listOfAllExecutiveBookings);
			//end
			request.getSession().setAttribute("examBookingPasswordList",examBookingList);

			if(examBookingList != null && examBookingList.size() > 0 || 
					listOfAllExecutiveBookings!=null && listOfAllExecutiveBookings.size()>0){
				modelnView.addObject("rowCount",examBookingList.size());
				modelnView.addObject("rowCountSAS",listOfAllExecutiveBookings.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}

		return modelnView;
	}*/


	@RequestMapping(value = "/admin/onlineExamPasswordReport", method = RequestMethod.POST)
	public ModelAndView onlineExamPasswordReport(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute StudentMarksBean studentMarks){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/onlineExamPasswordReport");
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("monthList", SAS_EXAM_MONTH_LIST);
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<ExamBookingTransactionBean> examBookingList = dao.getConfirmedOnlineExamBookingForGivenYearMonth(studentMarks);

			//  Added on 8/2/2018 by Steffi for generating exam password start
			ArrayList<String> sapidList = dao.getStudentExamPasswordsBlank(studentMarks);
			HashMap<String, String> studentPasswordNotBlankMap = dao.getStudentExamPasswordsNotBlank(studentMarks);
			if(sapidList.size()>0){
				for(String sapid:sapidList){
					if(studentPasswordNotBlankMap.containsKey(sapid)){
						dao.assignPasswordRetailAndCorporate(sapid,studentPasswordNotBlankMap.get(sapid),studentMarks);
					}else{
						String password = generateRandomPass(sapid);
						dao.assignPasswordRetailAndCorporate(sapid,password,studentMarks);
					}
				}
			}

			//  Added on 8/2/2018 by Steffi for generating exam password end
			//  Added on 8/2/2018 by Steffi
			HashMap<String,ExamBookingTransactionBean> examBookingSapidSubjectMap = new HashMap<String,ExamBookingTransactionBean>();
			int count = 0;
			if(examBookingList != null && !examBookingList.isEmpty()) {
				for(ExamBookingTransactionBean e: examBookingList){
					String key1 = e.getSapid()+"|"+e.getSubject();
					String key2 = e.getSapid()+"|"+e.getExamDate()+"|"+e.getExamTime();

					if(!examBookingSapidSubjectMap.containsKey(key1) || !examBookingSapidSubjectMap.containsKey(key2)){
						examBookingSapidSubjectMap.put(key1, e);
						examBookingSapidSubjectMap.put(key2, e);
						count++;
					}else{
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error in "+examBookingSapidSubjectMap.get(key1).getSapid()+examBookingSapidSubjectMap.get(key1).getSubject());
						return modelnView; 
					}
				}
			} 
			// Added on 8/2/2018 by Steffi end
			ArrayList<ExamBookingTransactionBean> listOfExecutiveExamBookings=dao.listOfExecutiveExamBookings(studentMarks);
			HashMap<String,ExamBookingTransactionBean> examBookingExecutiveSapidSubjectMap = new HashMap<String,ExamBookingTransactionBean>();
			// Added on 8/2/2018 by Steffi
			int count2 = 0;
			if(listOfExecutiveExamBookings != null && !listOfExecutiveExamBookings.isEmpty()) {
				for(ExamBookingTransactionBean e: listOfExecutiveExamBookings){
					String key1 = e.getSapid()+"|"+e.getSubject();
					String key2 = e.getSapid()+"|"+e.getExamDate()+"|"+e.getExamTime();

					if(!examBookingExecutiveSapidSubjectMap.containsKey(key1) || !examBookingExecutiveSapidSubjectMap.containsKey(key2)){
						examBookingExecutiveSapidSubjectMap.put(key1, e);
						examBookingExecutiveSapidSubjectMap.put(key2, e);
						count2++;
					}else{
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error in  "+examBookingExecutiveSapidSubjectMap.get(key1).getSapid());
						return modelnView;
					}
				}
			} 
			// Added on 8/2/2018 by Steffi end

			HashMap<String, String> studentPasswordMap = dao.getStudentExamPasswordsForGivenYearMonth(studentMarks);
			for (int i = 0; i < examBookingList.size(); i++) {
				ExamBookingTransactionBean bean = examBookingList.get(i);
				bean.setPassword(studentPasswordMap.get(bean.getSapid()));
			}

			//executive students
			String assignPass=null;
			ArrayList<StudentMarksBean> listOfAllExecutiveBookings=null;
			for(ExamBookingTransactionBean listOfBeans:listOfExecutiveExamBookings) {
				//generatePass();
				assignPass=generatePass();
				studentMarks.setPassword(assignPass);
				studentMarks.setSapid(listOfBeans.getSapid());
				studentMarks.setMonth(listOfBeans.getMonth());
				studentMarks.setYear(listOfBeans.getYear());
				studentMarks.setExamDate(listOfBeans.getExamDate());
				studentMarks.setExamTime(listOfBeans.getExamTime());
				studentMarks.setEmailId(listOfBeans.getEmailId());
				studentMarks.setProgramStructApplicable(listOfBeans.getProgramStructApplicable());
				studentMarks.setSifySubjectCode(listOfBeans.getSifySubjectCode());
				dao.assignPassword(studentMarks);
			}


			listOfAllExecutiveBookings=dao.listOfBookings(studentMarks);

			request.getSession().setAttribute("listOfAllExecutiveBookings",listOfAllExecutiveBookings);
			//end
			request.getSession().setAttribute("examBookingPasswordList",examBookingList);

			if(examBookingList != null && examBookingList.size() > 0 || 
					listOfAllExecutiveBookings!=null && listOfAllExecutiveBookings.size()>0){
				modelnView.addObject("rowCount",examBookingList.size());
				modelnView.addObject("rowCountSAS",listOfAllExecutiveBookings.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}

		return modelnView;
	}



	@RequestMapping(value = "/admin/tcsOnlineExamPasswordReport", method = {RequestMethod.POST})
	public ModelAndView tcsOnlineExamPasswordReport(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute StudentMarksBean studentMarks){
		ModelAndView modelnView = new ModelAndView("report/onlineExamPasswordReport");
		ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");

		modelnView.addObject("studentMarks", new StudentMarksBean()); 	//for all field to be blank of exam year/month
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("monthList", SAS_EXAM_MONTH_LIST);
		ArrayList<TcsOnlineExamBean> tcsOnlineExamPasswordlist = dao.getListofTcsOnlineExamBooking(studentMarks);
		request.getSession().setAttribute("tcsOnlineExamPasswordlist",tcsOnlineExamPasswordlist);

		if(tcsOnlineExamPasswordlist != null && tcsOnlineExamPasswordlist.size() > 0 ){
			modelnView.addObject("rowCountTcs",tcsOnlineExamPasswordlist.size());
			request.setAttribute("tcsSuccess","true");
			request.setAttribute("tcsSuccessMessage","Report generated successfully. Please click link below to download excel.");
		}else{
			request.setAttribute("tcsError", "true");
			request.setAttribute("tcsErrorMessage", "No records found.");
		}

		return modelnView;			

	}

	@RequestMapping(value = "/admin/downloadtcsOnlineExamPasswordReport", method =  RequestMethod.GET)
	public ModelAndView downloadtcsOnlineExamPasswordReport(HttpServletRequest request,
			HttpServletResponse response ) {
		ArrayList<TcsOnlineExamBean> tcsOnlineExamPasswordlist  = new ArrayList<TcsOnlineExamBean>();
		try {
			tcsOnlineExamPasswordlist = (ArrayList<TcsOnlineExamBean>) request.getSession().getAttribute("tcsOnlineExamPasswordlist");
		}catch(Exception e) {
			
		}
		return new ModelAndView("tcsOnlineExamPasswordView","tcsOnlineExamPasswordlist",tcsOnlineExamPasswordlist);

	}

	@RequestMapping(value = "/admin/downloadExecutiveExamPassword", method = {RequestMethod.GET, RequestMethod.POST})
	public String downloadExecutiveExamPassword(HttpServletRequest request, HttpServletResponse response,
			Model m) throws IOException {


		ArrayList<StudentMarksBean> reportList = (ArrayList<StudentMarksBean>)request.getSession().getAttribute("listOfAllExecutiveBookings");
		m.addAttribute("reportList",reportList);
		return "executiveBookingExcelView";

	}




	@RequestMapping(value = "/admin/downloadOnlineExamPasswordReport", method = {RequestMethod.GET, RequestMethod.POST})
	public String downloadOnlineExamPasswordReport(HttpServletRequest request, HttpServletResponse response,
			Model m) throws IOException {
		List<ExamBookingTransactionBean> examBookingList = 
				(ArrayList<ExamBookingTransactionBean>)request.getSession().getAttribute("examBookingPasswordList");
		m.addAttribute("examBookingList",examBookingList);

		return "onlineExamPasswordReportExcelView";

	}

	@RequestMapping(value = "/admin/examBookingStudentCountReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String examBookingStudentCountReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "report/examBookingStudentCountReport";
	}

	@RequestMapping(value = "/admin/examBookingStudentCountReport", method = RequestMethod.POST)
	public ModelAndView examBookingStudentCountReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("report/examBookingStudentCountReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<ExamBookingTransactionBean> examBookingList = dao.getConfirmedBookingForGivenYearMonth(studentMarks, getAuthorizedCodes(request));
			request.getSession().setAttribute("examBookingList",examBookingList);


			if(examBookingList != null && examBookingList.size() > 0){
				modelnView.addObject("rowCount",examBookingList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	public String generatePass() {
		String generatedString =null;
		try {
			generatedString=  RandomStringUtils.randomNumeric(10);
		}
		catch(Exception e) {
			
			return generatedString;
		}

		return generatedString;
	}

	@RequestMapping(value = "/admin/downloadExamBookingStudentCountReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExamBookingStudentCountReport(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		List<ExamBookingTransactionBean> examBookingList = (ArrayList<ExamBookingTransactionBean>)request.getSession().getAttribute("examBookingList");

		return new ModelAndView("examBookingStudentCountReportExcelView","examBookingList",examBookingList);
	}

	@RequestMapping(value = "/admin/questionPaperCountReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String questionPaperCountReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "report/questionPaperCountReport";
	}

	@RequestMapping(value = "/admin/questionPaperCountReport", method = RequestMethod.POST)
	public ModelAndView questionPaperCountReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/questionPaperCountReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<ExamBookingTransactionBean> examBookingList = dao.getQuestionPaperCountForGivenYearMonth(studentMarks);
			request.getSession().setAttribute("examBookingList",examBookingList);


			if(examBookingList != null && examBookingList.size() > 0){
				modelnView.addObject("rowCount",examBookingList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadQuestionPaperCountReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadQuestionPaperCountReport(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<ExamBookingTransactionBean> examBookingList = (ArrayList<ExamBookingTransactionBean>)request.getSession().getAttribute("examBookingList");

		return new ModelAndView("questionPaperCountReportExcelView","examBookingList",examBookingList);
	}

	@RequestMapping(value = "/admin/cumulativeFinanceReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String cumulativeFinanceReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
			return null; 
		}
		//Added new filters ic/lc/paymentOptions
		ArrayList<String> lclist = reportsDAO.getListOfLCNames();
		ArrayList<String> iclist = reportsDAO.getListOfICNames();
		ArrayList<String> paymentOptions = reportsDAO.getListOfPaymentOptions();
		m.addAttribute("studentMarks",new OperationsRevenueBean());
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("lclist",lclist);
		m.addAttribute("iclist",iclist);
		m.addAttribute("paymentOptions",paymentOptions);
		request.getSession().setAttribute("lclist", lclist);
		request.getSession().setAttribute("iclist", iclist);
		request.getSession().setAttribute("paymentOptions", paymentOptions);
		return "cumulativeFinanceReport";
	} 

	@RequestMapping(value = "/admin/cumulativeFinanceReport", method = RequestMethod.POST)
	public ModelAndView cumulativeFinanceReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute OperationsRevenueBean operationsRevenue){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("cumulativeFinanceReport");
		request.getSession().setAttribute("studentMarks", operationsRevenue);
		try{
			ArrayList<ExamBookingTransactionBean> examBookingList = new ArrayList<ExamBookingTransactionBean>();
			//for PG/All program type filter get revenue of exam booking.
			if(!operationsRevenue.getProgramType().equalsIgnoreCase("MBA - X") && !operationsRevenue.getProgramType().equalsIgnoreCase("MBA - WX")) {
				examBookingList.addAll(reportsDAO.getDatewiseAmountPG(operationsRevenue));
			}
			//for MBAx/All program type filter get total revenue of exam booking.
			if(!operationsRevenue.getProgramType().equalsIgnoreCase("PG") && !operationsRevenue.getProgramType().equalsIgnoreCase("MBA - WX")) {
				examBookingList.addAll(reportsDAO.getDatewiseAmountMBAx(operationsRevenue));
			}
			//for MBAwx/All program type filter get total revenue of exam booking.
			if(!operationsRevenue.getProgramType().equalsIgnoreCase("MBA - X") && !operationsRevenue.getProgramType().equalsIgnoreCase("PG")) {
				examBookingList.addAll(reportsDAO.getDatewiseAmountMBAwx(operationsRevenue));
			}

			HashMap<String, Double> dateAmountMap = new HashMap<>();

			for (int i = 0; i < examBookingList.size(); i++) {
				ExamBookingTransactionBean bean = examBookingList.get(i);
				if(dateAmountMap.containsKey(bean.getRespTranDateTime())) {
					double amount = dateAmountMap.get(bean.getRespTranDateTime());
					amount=amount+Double.parseDouble(bean.getAmount());
					dateAmountMap.replace(bean.getRespTranDateTime(), amount);
				}else {
					dateAmountMap.put(bean.getRespTranDateTime(), Double.parseDouble(bean.getAmount()));
				}
			}

			ServletContext sc = request.getSession().getServletContext();

			ArrayList<ExamBookingTransactionBean> successfulButCenterNotAvailableExamBookings = (ArrayList<ExamBookingTransactionBean>)sc.getAttribute("successfulButCenterNotAvailableExamBookings");
			ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings = (ArrayList<ExamBookingTransactionBean>)sc.getAttribute("successfulButAlreadyBookedExamBookings");

			if(successfulButCenterNotAvailableExamBookings != null && successfulButCenterNotAvailableExamBookings.size() > 0){
				for(int i = 0; i < successfulButCenterNotAvailableExamBookings.size(); i++){

					ExamBookingTransactionBean bean = successfulButCenterNotAvailableExamBookings.get(i);
					String respTranDate = bean.getRespTranDateTime().substring(0,10);
					double currentAmount = dateAmountMap.get(respTranDate);
					double newAmount = currentAmount + Double.parseDouble(bean.getAmount());
					dateAmountMap.put(respTranDate,newAmount);
				}
			}

			if(successfulButAlreadyBookedExamBookings != null && successfulButAlreadyBookedExamBookings.size() > 0){
				for(int i = 0; i < successfulButAlreadyBookedExamBookings.size(); i++){

					ExamBookingTransactionBean bean = successfulButAlreadyBookedExamBookings.get(i);
					String respTranDate = bean.getRespTranDateTime().substring(0,10);
					double currentAmount = dateAmountMap.get(respTranDate);
					double newAmount = currentAmount + Double.parseDouble(bean.getAmount());
					dateAmountMap.put(respTranDate,newAmount);
				}
			}

			Map<String, Double> sortedDateAmountMap = new TreeMap<>(dateAmountMap);
			request.getSession().setAttribute("sortedDateAmountMap",sortedDateAmountMap);

			if(examBookingList != null && examBookingList.size() > 0){
				modelnView.addObject("rowCount",dateAmountMap.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating records.");
		}
		modelnView.addObject("studentMarks", operationsRevenue);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadCumulativeFinanceReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadCumulativeFinanceReport(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		Map<String, Double> sortedDateAmountMap = (Map<String, Double>)request.getSession().getAttribute("sortedDateAmountMap");

		return new ModelAndView("cumulativeFinanceReportExcelView","sortedDateAmountMap",sortedDateAmountMap);
	}

	@RequestMapping(value = "/admin/examBookingDashboardForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String examBookingDashboardForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");

		StudentMarksBean marks = new StudentMarksBean();
		marks.setMonth(dao.getLiveExamMonth());
		marks.setYear(dao.getLiveExamYear());
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "examBookingDashboard";
	}

	@RequestMapping(value = "/admin/examBookingDashboard", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView examBookingDashboard(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("examBookingDashboard");
		request.getSession().setAttribute("studentMarks", studentMarks);
		int totalSubjectsBooked = 0;
		int onlineSubjectsBooked = 0;
		int offlineSubjectsBooked = 0;
		int onlineStudentsBooked = 0;
		int OfflineStundetsBooked = 0;
		int freeStudentsBooked = 0;
		int OnlineSubjectsPayments = 0;
		int ddSubjectsPayments = 0;
		int OnlineTransactions = 0;
		int OnlineRefundTransactions = 0;
		int ddTransactions = 0;
		int noOfDDsRejected = 0;
		int noOfDDsApprovedButNotBooked = 0;
		int noOfDDApprovalPending = 0;
		int noOfSubjectsPendingToBeBoked = 0;
		int noOfStudentsPendingToBeBoked = 0;
		double bulkPayment = 0;
		double onlineAmount = 0;
		double amountPaid = 0;
		double refundAmount = 0;

		int noOfFailedSubjectsPendingToBeBoked = 0;
		int noOfFailedStudentsPendingToBeBoked = 0;

		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<ExamBookingTransactionBean> examBookingList = dao.getConfirmedOrRelesedBookingForGivenYearMonth(studentMarks);
			ArrayList<AssignmentStatusBean> studentsList = dao.getStudentsSubjectsForExamBooking(studentMarks, getAuthorizedCodes(request));
			bulkPayment = dao.getBulkPayments();

			HashMap<String,String> passStudentSubjectMap = new HashMap<>();
			HashMap<String,String> onlineStudentsMap = new HashMap<>();
			HashMap<String,String> offlineStudentsMap = new HashMap<>();
			HashMap<String,String> freeStudentsMap = new HashMap<>();
			HashMap<String,String> OnlineTransactionMap = new HashMap<>();
			HashMap<String,String> ddTransactionsMap = new HashMap<>();
			HashMap<String,String> bookingsCompleteStudentSubjectMap = new HashMap<>();
			HashMap<String,String> bookingsInCompleteStudentSubjectMap = new HashMap<>();
			HashMap<String,String> failedSubjectsBookingsInCompleteStudentSubjectMap = new HashMap<>();
			ArrayList<String> bookingPendingStudentsList = new ArrayList<>();
			ArrayList<String> failedStudentsBookingPendingStudentsList = new ArrayList<>();
			HashMap<String,String> OnlineRefundTransactionMap = new HashMap<>();


			ArrayList<AssignmentStatusBean> passStudentsList = dao.getPassRecords();
			for (AssignmentStatusBean bean : passStudentsList) {
				passStudentSubjectMap.put(bean.getSapid()+bean.getSubject(), null);
			}

			for (int i = 0; i < examBookingList.size(); i++) {
				ExamBookingTransactionBean bean = examBookingList.get(i);
				String studentId = bean.getSapid();
				String mode = bean.getExamMode();
				String paymentMode = bean.getPaymentMode();
				String trackId = bean.getTrackId();
				String amount = bean.getAmount();
				String booked = bean.getBooked();

				if("Y".equals(booked)){
					bookingsCompleteStudentSubjectMap.put(bean.getSapid()+bean.getSubject(), null);
				}

				if("Online".equalsIgnoreCase(mode) && "Y".equals(booked)){
					onlineSubjectsBooked++;
					if(!onlineStudentsMap.containsKey(studentId)){
						onlineStudentsMap.put(studentId, null);
						onlineStudentsBooked++;
					}
				}else if("Offline".equalsIgnoreCase(mode) && "Y".equals(booked)){
					offlineSubjectsBooked++;
					if(!offlineStudentsMap.containsKey(studentId)){
						offlineStudentsMap.put(studentId, null);
						OfflineStundetsBooked++;
					}
				}

				if("Online".equalsIgnoreCase(paymentMode)){
					if("Y".equals(booked)){
						OnlineSubjectsPayments++;
					}
					if(!OnlineTransactionMap.containsKey(trackId)){
						amountPaid = Double.parseDouble(amount);
						onlineAmount = onlineAmount + amountPaid ;
						OnlineTransactionMap.put(trackId, null);
						OnlineTransactions++;
					}
					if("RF".equals(booked) && (!OnlineRefundTransactionMap.containsKey(trackId))){
						amountPaid = Double.parseDouble(amount);
						refundAmount = refundAmount + amountPaid;
						OnlineRefundTransactionMap.put(trackId, null);
						OnlineRefundTransactions++;
					}
				}else if("DD".equalsIgnoreCase(paymentMode)){
					ddSubjectsPayments++;
					if(!ddTransactionsMap.containsKey(trackId)){
						ddTransactionsMap.put(trackId, null);
						ddTransactions++;
					}
				}else if("FREE".equalsIgnoreCase(paymentMode)){
					if(!freeStudentsMap.containsKey(studentId)){
						freeStudentsMap.put(studentId, null);
						freeStudentsBooked++;
					}
				}
			}


			HashMap<String,String> rejectedDDMap = new HashMap<>();
			HashMap<String,String> pendingDDMap = new HashMap<>();
			HashMap<String,String> approvedButNotBookedDDMap = new HashMap<>();
			ArrayList<ExamBookingTransactionBean> ddBookingList = dao.getDDBookings(studentMarks);

			for (int i = 0; i < ddBookingList.size(); i++) {
				ExamBookingTransactionBean bean = ddBookingList.get(i);

				String trackId = bean.getTrackId();
				String tranStatus = bean.getTranStatus();
				String booked = bean.getBooked();

				if(DD_APPROVAL_PENDING.equalsIgnoreCase(tranStatus)){
					if(!pendingDDMap.containsKey(trackId)){
						pendingDDMap.put(trackId, null);
						noOfDDApprovalPending++;
					}
				}else if(DD_REJECTED.equalsIgnoreCase(tranStatus)){
					if(!rejectedDDMap.containsKey(trackId)){
						rejectedDDMap.put(trackId, null);
						noOfDDsRejected++;
					}
				}else if(DD_APPROVED.equalsIgnoreCase(tranStatus) && 
						(!("Y".equals(booked) || "RL".equals(booked)))){
					if(!approvedButNotBookedDDMap.containsKey(trackId)){
						approvedButNotBookedDDMap.put(trackId, null);
						noOfDDsApprovedButNotBooked++;
					}
				}
			}

			for (int i = 0; i < studentsList.size(); i++) {
				AssignmentStatusBean bean = studentsList.get(i);
				if(!bookingsCompleteStudentSubjectMap.containsKey(bean.getSapid()+bean.getSubject()) && 
						!bookingsInCompleteStudentSubjectMap.containsKey(bean.getSapid()+bean.getSubject())&& 
						!passStudentSubjectMap.containsKey(bean.getSapid()+bean.getSubject())){
					bookingsInCompleteStudentSubjectMap.put(bean.getSapid()+bean.getSubject(), null);
					noOfSubjectsPendingToBeBoked++;
				}
				if((!bookingPendingStudentsList.contains(bean.getSapid())) && 
						(!onlineStudentsMap.containsKey(bean.getSapid())) &&
						(!offlineStudentsMap.containsKey(bean.getSapid())) ){
					bookingPendingStudentsList.add(bean.getSapid());
					noOfStudentsPendingToBeBoked++;
				}
			}


			String month = studentMarks.getMonth();
			String year = studentMarks.getYear();

			if("Sep".equalsIgnoreCase(month)){
				month = "Dec";
			}else if("Apr".equalsIgnoreCase(month)){
				month = "Jun";
			}


			ArrayList<AssignmentStatusBean> failStudentsList = dao.getValidStudentsFailRecordsWithoutANS(year, month, getAuthorizedCodes(request));

			for (int i = 0; i < failStudentsList.size(); i++) {
				AssignmentStatusBean bean = failStudentsList.get(i);
				if(!bookingsCompleteStudentSubjectMap.containsKey(bean.getSapid()+bean.getSubject()) && 
						!failedSubjectsBookingsInCompleteStudentSubjectMap.containsKey(bean.getSapid()+bean.getSubject())){
					failedSubjectsBookingsInCompleteStudentSubjectMap.put(bean.getSapid()+bean.getSubject(), null);
					noOfFailedSubjectsPendingToBeBoked++;
				}
				if((!failedStudentsBookingPendingStudentsList.contains(bean.getSapid())) && 
						(!onlineStudentsMap.containsKey(bean.getSapid())) &&
						(!offlineStudentsMap.containsKey(bean.getSapid())) ){
					failedStudentsBookingPendingStudentsList.add(bean.getSapid());
					noOfFailedStudentsPendingToBeBoked++;
				}
			}


			HashMap<String, StudentExamBean> studentsWithLCMap = getStudentsWithLCMap();
			HashMap<String, ExamBookingMetricsBean> icLcMetricsMap = new HashMap<String, ExamBookingMetricsBean>();

			//Gather metrics for current drive pending STUDENTS
			for (String sapid : bookingPendingStudentsList) {
				StudentExamBean student = studentsWithLCMap.get(sapid);
				if(student == null){
					continue;
				}
				String key = student.getLc() + student.getCenterCode();

				if(!icLcMetricsMap.containsKey(key)){
					ExamBookingMetricsBean bean = new ExamBookingMetricsBean();
					bean.setLc(student.getLc());
					bean.setCenterCode(student.getCenterCode());
					bean.setCenterName(student.getCenterName());
					bean.setNoOfCurrentDrivePendingStudents(1);
					bean.setNoOfCurrentDrivePendingSubjects(1);
					icLcMetricsMap.put(key, bean);
				}else{
					ExamBookingMetricsBean bean = icLcMetricsMap.get(key);
					bean.setNoOfCurrentDrivePendingStudents(bean.getNoOfCurrentDrivePendingStudents() + 1);
				}
			}


			//Get metrics for failed pending STUDENTS
			for (String sapid : failedStudentsBookingPendingStudentsList) {
				StudentExamBean student = studentsWithLCMap.get(sapid);
				if(student == null){
					continue;
				}
				String key = student.getLc() + student.getCenterCode();

				if(!icLcMetricsMap.containsKey(key)){
					ExamBookingMetricsBean bean = new ExamBookingMetricsBean();
					bean.setLc(student.getLc());
					bean.setCenterCode(student.getCenterCode());
					bean.setCenterName(student.getCenterName());
					bean.setNoOfFailedPendingStudents(1);
					bean.setNoOfFailedPendingSubejcts(1);
					icLcMetricsMap.put(key, bean);
				}else{
					ExamBookingMetricsBean bean = icLcMetricsMap.get(key);
					bean.setNoOfFailedPendingStudents(bean.getNoOfFailedPendingStudents() + 1);
				}
			}

			//Gather metrics for current drive pending SUBJECTS
			for (Map.Entry<String, String> entry : bookingsInCompleteStudentSubjectMap.entrySet())
			{
				String studentSubject = entry.getKey();
				String sapid = studentSubject.substring(0, 11);
				String subject = studentSubject.substring(11,studentSubject.length()-1);
				StudentExamBean student = studentsWithLCMap.get(sapid);
				if(student == null){
					continue;
				}

				String key = student.getLc() + student.getCenterCode();

				if(!icLcMetricsMap.containsKey(key)){
					ExamBookingMetricsBean bean = new ExamBookingMetricsBean();
					bean.setLc(student.getLc());
					bean.setCenterCode(student.getCenterCode());
					bean.setCenterName(student.getCenterName());
					bean.setNoOfCurrentDrivePendingStudents(1);
					bean.setNoOfCurrentDrivePendingSubjects(1);
					icLcMetricsMap.put(key, bean);
				}else{
					ExamBookingMetricsBean bean = icLcMetricsMap.get(key);
					bean.setNoOfCurrentDrivePendingSubjects(bean.getNoOfCurrentDrivePendingSubjects() + 1);
				}
			}

			//Gather metrics for failed pending SUBJECTS
			for (Map.Entry<String, String> entry : failedSubjectsBookingsInCompleteStudentSubjectMap.entrySet())
			{
				String studentSubject = entry.getKey();
				String sapid = studentSubject.substring(0, 11);
				String subject = studentSubject.substring(11,studentSubject.length()-1);
				StudentExamBean student = studentsWithLCMap.get(sapid);
				if(student == null){
					continue;
				}
				String key = student.getLc() + student.getCenterCode();

				if(!icLcMetricsMap.containsKey(key)){
					ExamBookingMetricsBean bean = new ExamBookingMetricsBean();
					bean.setLc(student.getLc());
					bean.setCenterCode(student.getCenterCode());
					bean.setCenterName(student.getCenterName());
					bean.setNoOfFailedPendingStudents(1);
					bean.setNoOfFailedPendingSubejcts(1);
					icLcMetricsMap.put(key, bean);
				}else{
					ExamBookingMetricsBean bean = icLcMetricsMap.get(key);
					bean.setNoOfFailedPendingSubejcts(bean.getNoOfFailedPendingSubejcts() + 1);
				}
			}

			modelnView.addObject("icLcMetricsMap",new TreeMap<String, ExamBookingMetricsBean>(icLcMetricsMap));

			onlineAmount = onlineAmount + bulkPayment;//Bulk payment to be appended to total value.
			modelnView.addObject("resultsPage","true");

			modelnView.addObject("totalSubjectsBooked",(onlineSubjectsBooked+offlineSubjectsBooked));
			modelnView.addObject("onlineSubjectsBooked",onlineSubjectsBooked);
			modelnView.addObject("offlineSubjectsBooked",offlineSubjectsBooked);
			modelnView.addObject("onlineStudentsBooked",onlineStudentsBooked);
			modelnView.addObject("OfflineStundetsBooked",OfflineStundetsBooked);
			modelnView.addObject("freeStudentsBooked",freeStudentsBooked);
			modelnView.addObject("OnlineSubjectsPayments",OnlineSubjectsPayments);
			modelnView.addObject("ddSubjectsPayments",ddSubjectsPayments);
			modelnView.addObject("OnlineTransactions",OnlineTransactions);
			modelnView.addObject("OnlineRefundTransactions",OnlineRefundTransactions);
			modelnView.addObject("ddTransactions",ddTransactions);
			modelnView.addObject("noOfDDApprovalPending",noOfDDApprovalPending);
			modelnView.addObject("noOfDDsRejected",noOfDDsRejected);
			modelnView.addObject("noOfSubjectsPendingToBeBoked",noOfSubjectsPendingToBeBoked);
			modelnView.addObject("noOfStudentsPendingToBeBoked",noOfStudentsPendingToBeBoked);

			modelnView.addObject("noOfFailedSubjectsPendingToBeBoked",noOfFailedSubjectsPendingToBeBoked);
			modelnView.addObject("noOfFailedStudentsPendingToBeBoked",noOfFailedStudentsPendingToBeBoked);

			modelnView.addObject("onlineAmount",onlineAmount);
			modelnView.addObject("refundAmount",refundAmount);
			modelnView.addObject("bulkPayment",bulkPayment);
			modelnView.addObject("noOfDDsApprovedButNotBooked",noOfDDsApprovedButNotBooked);


		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating dashboard.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/admin/examBookingPendingReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String examBookingPendingReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "examBookingPendingReport";
	}

	@RequestMapping(value = "/admin/examBookingPendingReport", method = RequestMethod.POST)
	public ModelAndView examBookingPendingReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}

		ModelAndView modelnView = new ModelAndView("examBookingPendingReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			String month = studentMarks.getMonth();
			String year = studentMarks.getYear();

			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM");  
			Date date1 = sdf.parse(year+"-"+month);  
			Date date2 = sdf.parse("2021-Dec");
			
			
			ArrayList<ExamBookingTransactionBean> examBookingList = dao.getConfirmedBookingForGivenYearMonth(studentMarks, getAuthorizedCodes(request));
			HashMap<String,String> bookingsCompleteStudentSubjectMap = new HashMap<>();
			HashMap<String,String> passStudentSubjectMap = new HashMap<>();

			HashMap<String,String> regIncompleteStudentSubjectMap = new HashMap<>();
			ArrayList<AssignmentStatusBean> regIncompleteStudetsList = new ArrayList<>();
			
			
			for (int i = 0; i < examBookingList.size(); i++) {
				ExamBookingTransactionBean bean = examBookingList.get(i);
				bookingsCompleteStudentSubjectMap.put(bean.getSapid()+bean.getSubject(), null);

			}
			
			
			if(date1.before(date2)) {
				
			ArrayList<AssignmentStatusBean> passStudentsList = dao.getPassRecords();
			for (AssignmentStatusBean bean : passStudentsList) {
				passStudentSubjectMap.put(bean.getSapid()+bean.getSubject(), null);
			}


			//ArrayList<AssignmentStatusBean> studentsList = dao.getAssignmentSubmittedList(studentMarks, getAuthorizedCodes(request));
			//Assignment is not compulsory for attempting exam. Ignoring offline students in logic, since not many remain in system.
			ArrayList<AssignmentStatusBean> studentsList = dao.getValidStudentSubjectList(year,month,getAuthorizedCodes(request));

			 

			for (int i = 0; i < studentsList.size(); i++) {
				AssignmentStatusBean bean = studentsList.get(i);
				String key = bean.getSapid()+bean.getSubject();
				if(!bookingsCompleteStudentSubjectMap.containsKey(key) 
						&& (!passStudentSubjectMap.containsKey(key)) 
						&& (!regIncompleteStudentSubjectMap.containsKey(key))){
					bean.setExamYear(studentMarks.getYear());
					bean.setExamMonth(studentMarks.getMonth());
					regIncompleteStudetsList.add(bean);
					regIncompleteStudentSubjectMap.put(key, null);
				}
			}
			
		}else {
			List<AssignmentStatusBean> studentsList = dao.getPendingExambookingStudentSubjectListFromTemp(year,month,getAuthorizedCodes(request));
			
			studentsList.addAll(dao.getProjectExambookingStudentSubjectList(year,month,getAuthorizedCodes(request)));
			
			
			for (AssignmentStatusBean bean : studentsList) {
				String key = bean.getSapid()+bean.getSubject();
				if(!bookingsCompleteStudentSubjectMap.containsKey(key) 
					&& (!regIncompleteStudentSubjectMap.containsKey(key))){
					bean.setExamYear(studentMarks.getYear());
					bean.setExamMonth(studentMarks.getMonth());
					regIncompleteStudetsList.add(bean);
					regIncompleteStudentSubjectMap.put(key, null);
				}
			}
		}



			if("Sep".equalsIgnoreCase(month)){//For Sep exam validity will be till Dec
				month = "Dec";
			}else if("Apr".equalsIgnoreCase(month)){//For Apr exam validity will be till Jun
				month = "Jun";
			}


			ArrayList<AssignmentStatusBean> failStudentsList = dao.getValidStudentsFailRecordsWithoutANS(year, month, getAuthorizedCodes(request));

			for (AssignmentStatusBean bean : failStudentsList) {
				String key = bean.getSapid()+bean.getSubject();
				if(!bookingsCompleteStudentSubjectMap.containsKey(key) && (!regIncompleteStudentSubjectMap.containsKey(key))){
					bean.setExamYear(studentMarks.getYear());
					bean.setExamMonth(studentMarks.getMonth());
					regIncompleteStudetsList.add(bean);
					regIncompleteStudentSubjectMap.put(key, null);
				}
			}

			HashMap<String, CenterExamBean> centerCodeNameMap = getCentersMap();
			ArrayList<AssignmentStatusBean> regIncompleteStudetsList_tmp = new ArrayList<>();
			for (AssignmentStatusBean assignmentFileBean : regIncompleteStudetsList) {
				String lc = centerCodeNameMap.get(assignmentFileBean.getCenterCode()) != null ? centerCodeNameMap.get(assignmentFileBean.getCenterCode()).getLc() : "No LC";
				assignmentFileBean.setLc(lc);
				if(!"MBA - WX".equalsIgnoreCase(assignmentFileBean.getProgram()) && !"MBA - X".equalsIgnoreCase(assignmentFileBean.getProgram()) ) {
					regIncompleteStudetsList_tmp.add(assignmentFileBean);
				}
			}
			
				

			request.getSession().setAttribute("regIncompleteStudetsList",regIncompleteStudetsList_tmp);

			if(regIncompleteStudetsList_tmp != null && regIncompleteStudetsList_tmp.size() > 0){
				modelnView.addObject("rowCount",regIncompleteStudetsList_tmp.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating records.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadExamBookingPendingReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExamBookingPendingReport(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		
		ArrayList<AssignmentStatusBean> regIncompleteStudetsList = (ArrayList<AssignmentStatusBean>)request.getSession().getAttribute("regIncompleteStudetsList");
		ModelAndView modelnView = new ModelAndView("examBookingPendingReport");
		StudentMarksBean studentMarks =(StudentMarksBean)request.getSession().getAttribute("studentMarks");
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		//ExcelHelper helper =new ExcelHelper();
		ExamBookingPendingReportExcelView helper = new ExamBookingPendingReportExcelView();
		try{
			modelnView.addObject("rowCount",regIncompleteStudetsList.size());
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Report Downloaded Successfully");
			//helper.buildExcelDocumentForExamBookingPending(regIncompleteStudetsList, response, dao);
			return new ModelAndView ("examBookingPendingReportExcelView","regIncompleteStudetsList",regIncompleteStudetsList);

		}catch(Exception e)
		{
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);
		return modelnView;
	}

	@RequestMapping(value = "/admin/examCenterCapacityReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String examCenterCapacityReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "examCenterCapacityReport";
	}

	@RequestMapping(value = "/admin/findOnlineTranConflictForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String findOnlineTranConflictForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		m.addAttribute("yearList", currentYearList);

		FileBean fileBean = new FileBean();
		m.addAttribute("fileBean",fileBean);

		return "onlineTranConflict";
	}


	@RequestMapping(value = "/admin/findOnlineTranConflict", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView findOnlineTranConflict(FileBean fileBean, BindingResult result,HttpServletRequest request,HttpServletResponse response, Model m){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("onlineTranConflict");
		String missingTrans =request.getParameter("findmissing");
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		try{
			String userId = (String)request.getSession().getAttribute("userId");

			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readHDFCTransactionExcel(fileBean, getProgramList(), getSubjectList(), userId);
			//List<StudentMarksBean> marksBeanList = excelHelper.readMarksExcel(fileBean, programList, subjectList);

			List<ExamBookingTransactionBean> transactionStatusList = (ArrayList<ExamBookingTransactionBean>)resultList.get(0);
			List<ExamBookingTransactionBean> errorBeanList = (ArrayList<ExamBookingTransactionBean>)resultList.get(1);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}
			ReportsDAO reportDao = (ReportsDAO)act.getBean("reportsDAO");

			ArrayList<String> allTrackIdList = new ArrayList<String>();

			ArrayList<String> examBookingsTrackIdList = reportDao.getConfirmedOrRelesedBookingTrackIdsForGivenYearMonth(fileBean);
			ArrayList<String> srTrackIdList = reportDao.getAllSRTrackId();
			ArrayList<String> pcpTrackIdList = reportDao.getAllPCPTrackId();
			ArrayList<String> adhocPaymentTrackIdList = reportDao.getAllAdhocPaymentTrackId();
			ArrayList<String> assigmentPaymentTrackIdList = reportDao.getAllAssignmentPaymentTrackId(fileBean);



			allTrackIdList.addAll(examBookingsTrackIdList);
			allTrackIdList.addAll(srTrackIdList);
			allTrackIdList.addAll(pcpTrackIdList);
			allTrackIdList.addAll(adhocPaymentTrackIdList);
			allTrackIdList.addAll(assigmentPaymentTrackIdList);

			ArrayList<ExamBookingTransactionBean> examExpiredBookingList = reportDao.getExpiredBookingsForGivenYearMonth(fileBean);

			HashMap<String, ExamBookingTransactionBean> unpaidTransactionsMap = new HashMap<>();
			HashMap<String, ExamBookingTransactionBean> feesPaidTransactionsMap = new HashMap<>();


			//ArrayList<String> paidTransactions = new ArrayList<>();
			for (int i = 0; i < examExpiredBookingList.size(); i++) {
				ExamBookingTransactionBean bean = examExpiredBookingList.get(i);
				unpaidTransactionsMap.put(bean.getTrackId().trim(), bean);
				//paidTransactions.add(bean.getTrackId().trim());
			}

			double differenceAmount = 0;
			double differenceAmountForUnRecordedTrans = 0;
			ArrayList<ExamBookingTransactionBean> unRecordedTransactionsListIn = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> unpaidTransactionsList = new ArrayList<>();

			for (int i = 0; i < transactionStatusList.size(); i++) {
				ExamBookingTransactionBean bean = transactionStatusList.get(i);
				if(unpaidTransactionsMap.containsKey(bean.getMerchantRefNo().trim()) && !"true".equals(missingTrans)){

					ExamBookingTransactionBean unPaidBean =unpaidTransactionsMap.get(bean.getMerchantRefNo().trim());
					String respTranDateTime = bean.getRespTranDateTime();
					String respAmount = bean.getRespAmount();

					unPaidBean.setRespTranDateTime(respTranDateTime);
					unPaidBean.setRespAmount(respAmount);

					double amountReceived = Double.parseDouble(bean.getRespAmount());
					differenceAmount = differenceAmount + amountReceived;
					unpaidTransactionsList.add(unPaidBean);
				} 
				if(!allTrackIdList.contains(bean.getMerchantRefNo().trim()))
				{
					unRecordedTransactionsListIn.add(bean);
				}
			}

			if(unpaidTransactionsList.size() > 0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Found mismatch of "+unpaidTransactionsList.size() + " Transactions & "+differenceAmount + "/- Rs.");
			} 
			if(unRecordedTransactionsListIn.size()>0){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Found mismatch of "+unRecordedTransactionsListIn.size() + " Transactions & "+differenceAmountForUnRecordedTrans + "/- Rs.");
			}else{
				request.setAttribute("success","true");
				request.setAttribute("successMessage","No mismatch found");
			}


			request.setAttribute("unpaidTransactionsList",unpaidTransactionsList);
			request.setAttribute("unRecordedTransactionsListInDB",unRecordedTransactionsListIn);

			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");

			for (ExamBookingTransactionBean bean : unRecordedTransactionsListIn) {
				String trackId = bean.getMerchantRefNo();
				String sapid = trackId.substring(0, 11);

				ArrayList<String> alreadyBookedList = dao.getSubjectsBookedForStudent(sapid);

				if(alreadyBookedList != null && alreadyBookedList.size() > 0){
					continue;
				}else{
					continue;
				}

				/*ArrayList<ExamBookingTransactionBean> subjectsCentersList = dao.getSubjectsCentersForTrackId(trackId);
					ArrayList<String> subjectsCenters = new ArrayList<>();
					ArrayList<String> subjects = new ArrayList<>();

					StudentBean student = dao.getSingleStudentWithValidity(sapid);

					for (int j = 0; j < subjectsCentersList.size(); j++) {

						subjectsCenters.add(subjectsCentersList.get(j).getSubject() 
								+"|"+subjectsCentersList.get(j).getCenterId() 
								+ "|" + subjectsCentersList.get(j).getExamDate() 
								+ "|" + subjectsCentersList.get(j).getExamTime());


						//subjectsCenters.add(subjectsCentersList.get(j).getSubject()+"|"+subjectsCentersList.get(j).getCenterId() +"|"+subjectsCentersList.get(j).getExamTime());
						subjects.add(subjectsCentersList.get(j).getSubject());
					}


					boolean centerStillAvailable = checkIfCenterStillAvailable(subjectsCenters, student);
					if(!centerStillAvailable){
						continue;
					}else{
						List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForConflictUsingSingleConnection(bean);
						MailSender mailSender = (MailSender)act.getBean("mailer");
						mailSender.sendBookingSummaryEmailForConflictBooking(student, examBookings, getExamCenterIdNameMap());
					}*/

			}



		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in reading file.");

		}
		m.addAttribute("fileBean",fileBean);

		m.addAttribute("missingTrans", missingTrans);
		m.addAttribute("yearList", currentYearList);
		return modelnView;
	}

	private boolean checkIfCenterStillAvailable( ArrayList<String> selectedCenters,StudentExamBean student) {
		ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
		boolean centerStillAvailable = true;
		if("Offline".equals(student.getExamMode())){ //Since offline has no centers
			return true;
		}

		List<ExamCenterBean> availableCenters = ecDao.getAvailableCentersForRegularOnlineExam(student.getSapid(), student.isCorporateExamCenterStudent(), student.getMappedCorporateExamCenterId());

		for (int i = 0; i < selectedCenters.size(); i++) {
			String subjectCenter = selectedCenters.get(i);

			String[] data = subjectCenter.split("\\|");

			String subject = data[0];
			String centerId = data[1];
			String examDate = data[2];
			String examStartTime = data[3];

			if("Project".equals(subject)){
				continue;//Center availability is not applicable for Project
			}
			if("Module 4 - Project".equals(subject)){
				continue;//Center availability is not applicable for Project
			}
			
			ArrayList<String> centerIdList = new ArrayList<>();
			for (ExamCenterBean center : availableCenters) {
				if(center.getDate().equals(examDate) && center.getStarttime().equals(examStartTime)){
					centerIdList.add(center.getCenterId());
				}
			}

			if(!centerIdList.contains(centerId)){
				centerStillAvailable = false;
				break;
			}
		}
		return centerStillAvailable;
	}



	@RequestMapping(value = "/admin/examCenterCapacityReport", method = RequestMethod.POST)
	public ModelAndView examCenterCapacityReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("examCenterCapacityReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		List<ExamCenterSlotMappingBean> allCapacityRecords = new ArrayList<ExamCenterSlotMappingBean>();
		try{
			
			allCapacityRecords = reportsService.getexamCenterCapacityReport(studentMarks);
		
			
			
			request.getSession().setAttribute("centerBookingsList",allCapacityRecords);
			
			if(allCapacityRecords != null && allCapacityRecords.size() > 0) {
				modelnView.addObject("rowCount",allCapacityRecords.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating records.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadExamCenterCapacityReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExamCenterCapacityReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<ExamCenterSlotMappingBean> centerBookingsList = (ArrayList<ExamCenterSlotMappingBean>)request.getSession().getAttribute("centerBookingsList");

		return new ModelAndView("examCenterCapacityReportExcelView","centerBookingsList",centerBookingsList);
	}

	@RequestMapping(value = "/admin/bookingSummaryReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String bookingSummaryReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {


		return "bookingSummaryReport";
	}


	@RequestMapping(value = "/admin/bookingSummaryReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView bookingSummaryReport(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
		ArrayList<ExamBookingTransactionBean> bookingList = dao.getAllConfirmedBookings(getAuthorizedCodes(request));
		int confirmedDDBookingsOffline = 0;
		int confirmedOnlineBookingsOffline = 0;
		int confirmedDDBookingsOnline = 0;
		int confirmedOnlineBookingsOnline = 0; 

		for (int i = 0; i < bookingList.size(); i++) {
			ExamBookingTransactionBean bean = bookingList.get(i);
			String examMode = bean.getExamMode();
			String paymentMode = bean.getPaymentMode();
			if("Online".equals(examMode)){
				if("DD".equals(paymentMode)){
					confirmedDDBookingsOnline++;
				}else if("Online".equals(paymentMode)){
					confirmedOnlineBookingsOnline++;
				}
			}else if("Offline".equals(examMode)){
				if("DD".equals(paymentMode)){
					confirmedDDBookingsOffline++;
				}else if("Online".equals(paymentMode)){
					confirmedOnlineBookingsOffline++;
				}
			}
		}

		request.setAttribute("confirmedDDBookingsOffline", confirmedDDBookingsOffline);
		request.setAttribute("confirmedOnlineBookingsOffline", confirmedOnlineBookingsOffline);
		request.setAttribute("confirmedDDBookingsOnline", confirmedDDBookingsOnline);
		request.setAttribute("confirmedOnlineBookingsOnline", confirmedOnlineBookingsOnline);

		ExamCenterSlotMappingBean slotsBooked = dao.getSlotsBooked();
		request.setAttribute("slotsBooked", slotsBooked);
		return new ModelAndView("bookingSummaryReport");
	}

	@RequestMapping(value = "/admin/releasedButNotBookedReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String releasedButNotBookedReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		ExamBookingTransactionBean searchBean = new ExamBookingTransactionBean();
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("yearList", currentYearList);

		return "report/releasedButNotBookedReport";
	}
	
	@RequestMapping(value="/admin/examBookingCanceledReportForm" ,method=RequestMethod.GET)
	public ModelAndView examBookingCanceledReportForm(HttpServletRequest request, HttpServletResponse response)
	{
		ModelAndView mv = new ModelAndView("report/examBookingCanceledReport");
		ExamBookingTransactionBean searchBean = new ExamBookingTransactionBean();
		mv.addObject("searchBean", searchBean);
		mv.addObject("yearList", currentYearList);
		return mv;
	}
	
	@RequestMapping(value="/admin/examBookingCanceledReport" ,method=RequestMethod.POST)
	public ModelAndView examBookingCanceledReport(HttpServletRequest request, HttpServletResponse response,  @ModelAttribute ExamBookingCancelBean searchBean)
	{
		ModelAndView mv = new ModelAndView("report/examBookingCanceledReport");
		Person person= new Person();
		List<ExamBookingCancelBean> canceledBookingList=new ArrayList<ExamBookingCancelBean>();
		try
		{
			person=(Person)request.getSession().getAttribute("user");		
			canceledBookingList=reportsService.getExamBookingCanceledListReport(searchBean);
			request.getSession().setAttribute("canceledBookingList",canceledBookingList);			
			if(canceledBookingList != null && canceledBookingList.size() > 0){
				mv.addObject("rowCount",canceledBookingList.size());
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}			
			
		}
		catch(Exception e){
			logger.error("Error in generating Report: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		mv.addObject("searchBean", searchBean);
		mv.addObject("yearList", currentYearList);
		mv.addObject("user",person);
		return mv;
	}

	
	@RequestMapping(value = "/admin/downloadExamBookingCanceledReport")
	public ModelAndView downloadExamBookingCanceledReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<ExamBookingCancelBean> canceledBookingList =new ArrayList<ExamBookingCancelBean>();
		try {
			canceledBookingList = (List<ExamBookingCancelBean>)request.getSession().getAttribute("canceledBookingList");			
		} catch (Exception e) {			
			logger.error(String.valueOf(e));
		}	
		return new ModelAndView(ExamBookingCancellationReportExcel,"canceledBookingList",canceledBookingList);
	}
	
	


	@RequestMapping(value = "/admin/releasedButNotBookedReport", method = RequestMethod.POST)
	public ModelAndView releasedButNotBookedReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingTransactionBean searchBean){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/releasedButNotBookedReport");
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<ExamBookingTransactionBean> examBookingList = dao.getReleasedButNotBooked(searchBean);
			request.getSession().setAttribute("examBookingList",examBookingList);


			if(examBookingList != null && examBookingList.size() > 0){
				modelnView.addObject("rowCount",examBookingList.size());
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/admin/autoExamBookingForm", method = {RequestMethod.GET , RequestMethod.POST})
	public ModelAndView autoExamBookingForm(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("autoExamBookingForm");
		modelnView.addObject("yearList", yearList);
		FileBean fileBean = new FileBean();
		modelnView.addObject("fileBean",fileBean);
		return modelnView;
	}


	/*Commented by Stef
	 * @RequestMapping(value = "/saveAutoExamBooking", method = {RequestMethod.GET, RequestMethod.POST})
		public ModelAndView saveAutoExamBooking(@ModelAttribute FileBean fileBean, BindingResult result,HttpServletRequest request,HttpServletResponse response, Model m){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
				ModelAndView modelnView = new ModelAndView("autoExamBookingForm");
				modelnView.addObject("yearList", yearList);
				modelnView.addObject("fileBean",fileBean);


				try{
					String userId = (String)request.getSession().getAttribute("userId");

					// read data from Excel File 
					ExcelHelper excelHelper = new ExcelHelper();
					ArrayList<List> resultList = excelHelper.readHDFCTransactionExcel(fileBean, getProgramList(), getSubjectList(), userId);

					List<ExamBookingTransactionBean> transactionStatusList = (ArrayList<ExamBookingTransactionBean>)resultList.get(0);
					List<ExamBookingTransactionBean> errorBeanList = (ArrayList<ExamBookingTransactionBean>)resultList.get(1);

					if(errorBeanList.size() > 0){
						request.setAttribute("errorBeanList", errorBeanList);
						return modelnView;
					}
					ReportsDAO reportDao = (ReportsDAO)act.getBean("reportsDAO");

					ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
					ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
					HashMap<String, StudentBean> getAllStudents = dao.getAllStudents();
					ArrayList<String> examBookingsTrackIdList = reportDao.getConfirmedOrRelesedBookingTrackIdsForGivenYearMonth(fileBean);

					ArrayList<ExamBookingTransactionBean> examExpiredBookingList = reportDao.getExpiredBookingsForGivenYearMonth(fileBean);

					// put all expired booking list into map for further processing as per center availability 
					HashMap<String, ExamBookingTransactionBean> doAutoBookingTransactionsMap = new HashMap<>();

					HashMap<String, ExamBookingTransactionBean> alreadyBookedTransactionMap =new HashMap<>();
					HashMap<String,String> corporateCenterUserMapping = ecDao.getCorporateCenterUserMapping();
					// already booking map of records need not to be process 
					for(int i=0;i<examBookingsTrackIdList.size();i++)
					{
						ExamBookingTransactionBean alreadyBookingBean = new ExamBookingTransactionBean();
						alreadyBookedTransactionMap.put(examBookingsTrackIdList.get(i).trim(),alreadyBookingBean);
					}

					// map of records need to be process for booking as Expired 
					for(int i=0;i<examExpiredBookingList.size();i++)
					{
						ExamBookingTransactionBean autoBookingBean =examExpiredBookingList.get(i);
						doAutoBookingTransactionsMap.put(autoBookingBean.getTrackId().trim(),autoBookingBean);
					}

					ArrayList<ExamBookingTransactionBean> successfulButCenterNotAvailableExamBookings = new ArrayList<>();
					ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings = new ArrayList<>();
					ArrayList<ExamBookingTransactionBean> successfulExamBookings = new ArrayList<>();
					int noOfConflictTransactions = 0;
					int noOfPendingRefunds = 0;
					double totalConflictAmount = 0;
					double refundDueAmount = 0;

					for (int i = 0; i < transactionStatusList.size(); i++) {
						ExamBookingTransactionBean bean = transactionStatusList.get(i);
						if(doAutoBookingTransactionsMap.containsKey(bean.getMerchantRefNo().trim()))
						{
							try
							{
								ExamBookingTransactionBean processbean =doAutoBookingTransactionsMap.get(bean.getMerchantRefNo().trim());

								boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Exam Registration");
								if(!isExamRegistraionLive){
									return modelnView;
								}

								String sapid = processbean.getSapid();
								String trackId = processbean.getTrackId();
								StudentBean student = dao.getSingleStudentWithValidity(sapid);
								if(corporateCenterUserMapping.containsKey(sapid)){
									student.setCorporateExamCenterStudent(true);
									student.setMappedCorporateExamCenterId(corporateCenterUserMapping.get(sapid));
								}
								String program = student.getProgram();
								String studentProgramStructure = student.getPrgmStructApplicable();
								ArrayList<ExamBookingTransactionBean> subjectsCentersList = dao.getSubjectsCentersForTrackId(trackId);
								ArrayList<String> subjectsCenters = new ArrayList<>();
								ArrayList<String> subjects = new ArrayList<>();

								for (int j = 0; j < subjectsCentersList.size(); j++) {

									subjectsCenters.add(subjectsCentersList.get(j).getSubject() 
											+"|"+subjectsCentersList.get(j).getCenterId() 
											+ "|" + subjectsCentersList.get(j).getExamDate() 
											+ "|" + subjectsCentersList.get(j).getExamTime());
									subjects.add(subjectsCentersList.get(j).getSubject());
								}


								ArrayList<String> alreadyBooked = dao.getSubjectsBookedForStudent(sapid);
								boolean subjectAlreadyBooked = false;
								for (int j = 0; j < subjectsCentersList.size(); j++) {
									if(alreadyBooked.contains(subjectsCentersList.get(j).getSubject())){
										subjectAlreadyBooked = true;
									}
								}
								if(subjectAlreadyBooked){
									successfulButAlreadyBookedExamBookings.add(processbean);
									noOfConflictTransactions++;
									continue;
								}

								boolean centerStillAvailable = checkIfCenterStillAvailable(subjectsCenters, student);
								if(!centerStillAvailable){
									successfulButCenterNotAvailableExamBookings.add(processbean);
									noOfConflictTransactions++;
									continue;
								}else{
									List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForConflictUsingSingleConnection(processbean);
									successfulExamBookings.addAll(examBookings);
									MailSender mailSender = (MailSender)act.getBean("mailer");
									mailSender.sendBookingSummaryEmailForConflictBooking(student, examBookings, getExamCenterIdNameMap());
								}

							}catch (Exception e) {
								
							}
							modelnView.addObject("successfulExamBookings",successfulExamBookings);
							modelnView.addObject("getAllStudents",getAllStudents);
							modelnView.addObject("rowCount",successfulExamBookings.size());
						}
					}

				}catch(Exception e){
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in reading file.");
				}
				return modelnView;
		}*/

	//Added by Stef
	@RequestMapping(value = "/admin/saveAutoExamBooking", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView saveAutoExamBooking(@ModelAttribute FileBean fileBean, BindingResult result,HttpServletRequest request,HttpServletResponse response, Model m){

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("autoExamBookingForm");
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("fileBean",fileBean);


		try{
			String userId = (String)request.getSession().getAttribute("userId");

			// read data from Excel File 
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readHDFCTransactionExcel(fileBean, getProgramList(), getSubjectList(), userId);

			List<ExamBookingTransactionBean> transactionStatusList = (ArrayList<ExamBookingTransactionBean>)resultList.get(0);
			List<ExamBookingTransactionBean> errorBeanList = (ArrayList<ExamBookingTransactionBean>)resultList.get(1);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}
			ReportsDAO reportDao = (ReportsDAO)act.getBean("reportsDAO");

			ExamBookingDAO dao = (ExamBookingDAO)act.getBean("examBookingDAO");
			ExamCenterDAO ecDao = (ExamCenterDAO)act.getBean("examCenterDAO");
			HashMap<String, StudentExamBean> getAllStudents = dao.getAllStudents();
			ArrayList<String> examBookingsTrackIdList = reportDao.getConfirmedOrRelesedBookingTrackIdsForGivenYearMonth(fileBean);

			ArrayList<ExamBookingTransactionBean> examExpiredBookingList = reportDao.getExpiredBookingsForGivenYearMonth(fileBean);

			// put all expired booking list into map for further processing as per center availability 
			HashMap<String, ExamBookingTransactionBean> doAutoBookingTransactionsMap = new HashMap<>();

			HashMap<String, ExamBookingTransactionBean> alreadyBookedTransactionMap =new HashMap<>();
			HashMap<String,String> corporateCenterUserMapping = ecDao.getCorporateCenterUserMapping();
			// already booking map of records need not to be process 
			for(int i=0;i<examBookingsTrackIdList.size();i++)
			{
				ExamBookingTransactionBean alreadyBookingBean = new ExamBookingTransactionBean();
				alreadyBookedTransactionMap.put(examBookingsTrackIdList.get(i).trim(),alreadyBookingBean);
			}

			// map of records need to be process for booking as Expired 
			for(int i=0;i<examExpiredBookingList.size();i++)
			{
				ExamBookingTransactionBean autoBookingBean =examExpiredBookingList.get(i);
				doAutoBookingTransactionsMap.put(autoBookingBean.getTrackId().trim(),autoBookingBean);
			}

			ArrayList<ExamBookingTransactionBean> successfulButCenterNotAvailableExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> successfulButAlreadyBookedExamBookings = new ArrayList<>();
			ArrayList<ExamBookingTransactionBean> successfulExamBookings = new ArrayList<>();
			int noOfConflictTransactions = 0;
			int noOfPendingRefunds = 0;
			double totalConflictAmount = 0;
			double refundDueAmount = 0;
			String projectLastDate = "";
			for (int i = 0; i < transactionStatusList.size(); i++) {
				ExamBookingTransactionBean bean = transactionStatusList.get(i);
				if(doAutoBookingTransactionsMap.containsKey(bean.getMerchantRefNo().trim()))
				{
					try
					{
						ExamBookingTransactionBean processbean =doAutoBookingTransactionsMap.get(bean.getMerchantRefNo().trim());

						ProjectSubmissionDAO pDao = (ProjectSubmissionDAO)act.getBean("projectSubmissionDAO");
						boolean isExamRegistraionLive = dao.isConfigurationLivePost1Day("Exam Registration");

						String sapid = processbean.getSapid();
						StudentExamBean student = dao.getSingleStudentWithValidity(sapid);
						String projectSubject = "Project";
						if("PD - WM".equals(student.getProgram())) {
							projectSubject = "Module 4 - Project";
						}
						
						boolean isProjectRegistraionLive = pDao.isProjectLive(projectSubject);//added new **************

						if(!isExamRegistraionLive && !isProjectRegistraionLive){
							return modelnView;
						}

						String trackId = processbean.getTrackId();
						if(corporateCenterUserMapping.containsKey(sapid)){
							student.setCorporateExamCenterStudent(true);
							student.setMappedCorporateExamCenterId(corporateCenterUserMapping.get(sapid));
						}
						String program = student.getProgram();
						String studentProgramStructure = student.getPrgmStructApplicable();
						ArrayList<ExamBookingTransactionBean> subjectsCentersList = dao.getSubjectsCentersForTrackId(trackId);
						ArrayList<String> subjectsCenters = new ArrayList<>();
						ArrayList<String> subjects = new ArrayList<>();

						for (int j = 0; j < subjectsCentersList.size(); j++) {

							subjectsCenters.add(subjectsCentersList.get(j).getSubject() 
									+"|"+subjectsCentersList.get(j).getCenterId() 
									+ "|" + subjectsCentersList.get(j).getExamDate() 
									+ "|" + subjectsCentersList.get(j).getExamTime());
							subjects.add(subjectsCentersList.get(j).getSubject());
						}


						ArrayList<String> alreadyBooked = new ArrayList<>();

						if(isExamRegistraionLive){
							alreadyBooked = dao.getSubjectsBookedForStudent(sapid);
						}
						if(isProjectRegistraionLive && !isExamRegistraionLive){
							AssignmentFileBean assignmnetFile = new AssignmentFileBean();
							if("PD - WM".equals(student.getProgram())) {
								alreadyBooked = dao.getModuleProjectBookedForStudent(sapid); //added new ***************************
								assignmnetFile.setSubject("Module 4 - Project");
							} else {
								alreadyBooked = dao.getProjectBookedForStudent(sapid); //added new ***************************
								assignmnetFile.setSubject("Project");
							}
//							assignmnetFile = pDao.findById(assignmnetFile); // Commented to make two cycle live
							String method = "saveAutoExamBooking()";
							AssignmentFileBean examMonthYearBean = eligibilityService.getStudentApplicableExamMonthYear(userId, assignmnetFile.getSubject(), method);
							assignmnetFile.setMonth(examMonthYearBean.getMonth());
							assignmnetFile.setYear(examMonthYearBean.getYear());
							assignmnetFile.setConsumerProgramStructureId(student.getConsumerProgramStructureId());
							assignmnetFile = projectSubmissionDAO.findProjectGuidelinesForApplicableCycle(assignmnetFile);

							String endDate=assignmnetFile.getEndDate();
							endDate = endDate.replaceAll("T", " ");
							projectLastDate = endDate.substring(0, 10);
						}
						boolean subjectAlreadyBooked = false;
						for (int j = 0; j < subjectsCentersList.size(); j++) {
							if(alreadyBooked.contains(subjectsCentersList.get(j).getSubject())){
								subjectAlreadyBooked = true;
							}
						}
						if(subjectAlreadyBooked){
							successfulButAlreadyBookedExamBookings.add(processbean);
							noOfConflictTransactions++;
							continue;
						}

						boolean centerStillAvailable = checkIfCenterStillAvailable(subjectsCenters, student);
						if(!centerStillAvailable){
							successfulButCenterNotAvailableExamBookings.add(processbean);
							noOfConflictTransactions++;
							continue;
						}else{
							List<ExamBookingTransactionBean> examBookings = dao.updateSeatsForConflictUsingSingleConnection(processbean);
							successfulExamBookings.addAll(examBookings);
							MailSender mailSender = (MailSender)act.getBean("mailer");
							if(isProjectRegistraionLive && !isExamRegistraionLive){
								mailSender.sendBookingSummaryEmailForProjectConflictBooking(student, examBookings, getExamCenterIdNameMap(),projectLastDate);
							}else if(!isProjectRegistraionLive && isExamRegistraionLive){
								mailSender.sendBookingSummaryEmailForConflictBooking(student, examBookings, getExamCenterIdNameMap(),ecDao);
							}else{
								mailSender.sendBookingSummaryEmailForConflictBooking(student, examBookings, getExamCenterIdNameMap(),ecDao);
								mailSender.sendBookingSummaryEmailForProjectConflictBooking(student, examBookings, getExamCenterIdNameMap(),projectLastDate);
							}							
						}

					}catch (Exception e) {
						
					}
					modelnView.addObject("successfulExamBookings",successfulExamBookings);
					modelnView.addObject("getAllStudents",getAllStudents);
					modelnView.addObject("rowCount",successfulExamBookings.size());
				}
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in reading file.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/admin/operationsRevenueForm", method = {RequestMethod.GET , RequestMethod.POST})
	public ModelAndView operationsRevenueForm(HttpServletRequest request, HttpServletResponse response) {
		//commented for testing
		if(!checkSession(request, response)){ 
			redirectToPortalApp(response); 
			return null; 
		}
		//Added new filters ic/lc/paymentOptions
		ArrayList<String> lclist = reportsDAO.getListOfLCNames();
		ArrayList<String> iclist = reportsDAO.getListOfICNames();
		ArrayList<String> paymentOptions = reportsDAO.getListOfPaymentOptions();
		ModelAndView modelnView = new ModelAndView("report/operationsRevenueReport");
		modelnView.addObject("operationsRevenue",new OperationsRevenueBean());
		modelnView.addObject("lclist",lclist);
		modelnView.addObject("iclist",iclist);
		modelnView.addObject("paymentOptions",paymentOptions);
		request.getSession().setAttribute("lclist", lclist);
		request.getSession().setAttribute("iclist", iclist);
		request.getSession().setAttribute("paymentOptions", paymentOptions);
		return modelnView;
	}

	/*old code
	 * @RequestMapping(value = "/operationsRevenue", method = {RequestMethod.POST})
	public ModelAndView operationsRevenue(HttpServletRequest request, HttpServletResponse response, @ModelAttribute OperationsRevenueBean operationsRevenue ) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ArrayList<OperationsRevenueBean> revenueList = new ArrayList<OperationsRevenueBean>();
		ReportsDAO reportDao = (ReportsDAO)act.getBean("reportsDAO");
		OperationsRevenueBean srRevenue = reportDao.getSRRevenue(operationsRevenue);
		OperationsRevenueBean adhocRevenue = reportDao.getAdhocPaymentRevenue(operationsRevenue);
		OperationsRevenueBean assignmentRevenue = reportDao.getAssignmentPaymentRevenue(operationsRevenue);
		OperationsRevenueBean examBookingRevenue = reportDao.getExamBookingsRevenue(operationsRevenue);
		OperationsRevenueBean pcpBookingRevenue = reportDao.getPCPBookingsRevenue(operationsRevenue);
	    OperationsRevenueBean adhocRefundRevenue = reportDao.getAdhocRefundPaymentRevenue(operationsRevenue);

		revenueList.add(srRevenue);
		revenueList.add(adhocRevenue);
		revenueList.add(assignmentRevenue);
		revenueList.add(examBookingRevenue);
		revenueList.add(pcpBookingRevenue);
		revenueList.add(adhocRefundRevenue);

		ModelAndView modelnView = new ModelAndView("report/operationsRevenueReport");
		request.getSession().setAttribute("operationsRevenue", operationsRevenue);
		request.getSession().setAttribute("revenueList", revenueList);

		modelnView.addObject("revenueList",revenueList);
		modelnView.addObject("operationsRevenue",operationsRevenue);
		modelnView.addObject("rowCount",revenueList.size());
		return modelnView;
	}*/


	@RequestMapping(value = "/admin/operationsRevenue", method = {RequestMethod.POST})
	public ModelAndView operationsRevenue(HttpServletRequest request, HttpServletResponse response, @ModelAttribute OperationsRevenueBean operationsRevenue ) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		//Added new filters ic/lc/paymentOptions
		ArrayList<String> lclist = (ArrayList<String>) request.getSession().getAttribute("lclist");
		ArrayList<String> iclist = (ArrayList<String>) request.getSession().getAttribute("iclist");
		ArrayList<String> paymentOptions = (ArrayList<String>) request.getSession().getAttribute("paymentOptions");

		ArrayList<OperationsRevenueBean> revenueTypeBasedList = new ArrayList<OperationsRevenueBean>();//list contains total revenue for every applicable revenue type SR/Assign/PCP etc.
		ArrayList<OperationsRevenueBean> revenueList = new ArrayList<OperationsRevenueBean>();
		
		ArrayList<OperationsRevenueBean> srRevenue = reportsDAO.getSRRevenueAmountList(operationsRevenue); //list contains SR Revenue based on SR Type.
		OperationsRevenueBean srbean = new OperationsRevenueBean();
		double totalSR = getTotalRevenueAmount(srRevenue);
		srbean.setRevenueSource("Service Request");
		srbean.setAmount(totalSR);
		revenueTypeBasedList.add(srbean);
		ArrayList<OperationsRevenueBean> srRevenueList = reportsDAO.getSRRevenueDownloadList(operationsRevenue);
		revenueList.addAll(srRevenueList);
		
		//total payment received from adhoc revenue type.
		ArrayList<OperationsRevenueBean> adhocRevenue = reportsDAO.getAdhocPaymentRevenueDownloadList(operationsRevenue);
		revenueList.addAll(adhocRevenue);
		OperationsRevenueBean adhocbean = new OperationsRevenueBean();
		double totalAdhoc = getTotalRevenueAmount(adhocRevenue);
		adhocbean.setRevenueSource("Adhoc Payment");
		adhocbean.setAmount(totalAdhoc);
		revenueTypeBasedList.add(adhocbean);

		//for PG/All program type filter get total revenue of assignment and exam booking.
		if(!operationsRevenue.getProgramType().equalsIgnoreCase("MBA - X") && !operationsRevenue.getProgramType().equalsIgnoreCase("MBA - WX")) {
			ArrayList<OperationsRevenueBean> assignmentRevenue = reportsDAO.getAssignmentPaymentRevenueDownloadList(operationsRevenue);
			revenueList.addAll(assignmentRevenue);
			OperationsRevenueBean assgnbean = new OperationsRevenueBean();
			double totalAssgn = getTotalRevenueAmount(assignmentRevenue);
			assgnbean.setRevenueSource("Assignment (PG/Diploma/Cert)");
			assgnbean.setAmount(totalAssgn);
			revenueTypeBasedList.add(assgnbean);
			ArrayList<OperationsRevenueBean> examBookingRevenuePG = reportsDAO.getExamBookingsRevenueDownloadPG(operationsRevenue);
			revenueList.addAll(examBookingRevenuePG);
			OperationsRevenueBean pgbean = new OperationsRevenueBean();
			double totalPG = getTotalRevenueAmount(examBookingRevenuePG);
			pgbean.setRevenueSource("Exam (PG/Diploma/Cert)");
			pgbean.setAmount(totalPG);
			revenueTypeBasedList.add(pgbean);
		}
		
		//for MBAx/All program type filter get total revenue of exam booking.
		if(!operationsRevenue.getProgramType().equalsIgnoreCase("PG") && !operationsRevenue.getProgramType().equalsIgnoreCase("MBA - WX")) {
			ArrayList<OperationsRevenueBean> examBookingRevenueMBAx = reportsDAO.getExamBookingsRevenueDownloadMBAx(operationsRevenue);
			revenueList.addAll(examBookingRevenueMBAx);
			OperationsRevenueBean mbaxbean = new OperationsRevenueBean();
			double totalMbax = getTotalRevenueAmount(examBookingRevenueMBAx);
			mbaxbean.setRevenueSource("Exam (MBAx)");
			mbaxbean.setAmount(totalMbax);
			revenueTypeBasedList.add(mbaxbean);
		}
		//for MBAwx/All program type filter get total revenue of exam booking.
		if(!operationsRevenue.getProgramType().equalsIgnoreCase("MBA - X") && !operationsRevenue.getProgramType().equalsIgnoreCase("PG")) {
			ArrayList<OperationsRevenueBean> examBookingRevenueMBAwx = reportsDAO.getExamBookingsRevenueDownloadMBAwx(operationsRevenue);
			revenueList.addAll(examBookingRevenueMBAwx);
			OperationsRevenueBean mbawxbean = new OperationsRevenueBean();
			double totalMbawx = getTotalRevenueAmount(examBookingRevenueMBAwx);
			mbawxbean.setRevenueSource("Exam (MBAwx)");
			mbawxbean.setAmount(totalMbawx);
			revenueTypeBasedList.add(mbawxbean);
		}
		ArrayList<OperationsRevenueBean> pcpBookingRevenue = reportsDAO.getPCPBookingsRevenueDownload(operationsRevenue);
		revenueList.addAll(pcpBookingRevenue);
		OperationsRevenueBean pcpbean = new OperationsRevenueBean();
		double totalPcp = getTotalRevenueAmount(pcpBookingRevenue);
		pcpbean.setRevenueSource("PCP Payment");
		pcpbean.setAmount(totalPcp);
		revenueTypeBasedList.add(pcpbean);
		//OperationsRevenueBean adhocRefundRevenue = reportDao.getAdhocRefundPaymentRevenue(operationsRevenue); not to count
		//revenueList.add(adhocRefundRevenue); not to count

		double total = getTotalRevenueAmount(revenueTypeBasedList);
		OperationsRevenueBean bean = new OperationsRevenueBean();
		bean.setRevenueSource("Total"); //sum of all the revenue types
		bean.setAmount(total);
		revenueTypeBasedList.add(bean);
		ModelAndView modelnView = new ModelAndView("report/operationsRevenueReport");
		request.getSession().setAttribute("operationsRevenue", operationsRevenue);
		request.getSession().setAttribute("revenueList", revenueList);
		modelnView.addObject("srRevenue",srRevenue);
		modelnView.addObject("revenueTypeBasedList",revenueTypeBasedList);
		modelnView.addObject("operationsRevenue",operationsRevenue);
		modelnView.addObject("rowCount",revenueTypeBasedList.size());
		modelnView.addObject("lclist",lclist);
		modelnView.addObject("paymentOptions",paymentOptions);
		modelnView.addObject("iclist",iclist);
		return modelnView;
	}
	public double getTotalRevenueAmount(ArrayList<OperationsRevenueBean> revenueList){
		double total = 0;
		for(OperationsRevenueBean bean:revenueList ){
			try{
				total += bean.getAmount();
			}catch(Exception e){
			}
		}
		return total;
	}

	@RequestMapping(value = "/admin/downloadOperationsRevenueReport", method = {RequestMethod.GET , RequestMethod.POST})
	public void downloadOperationsRevenueReport(HttpServletRequest request, HttpServletResponse response ) throws IOException {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
		}
		ArrayList<OperationsRevenueBean> revenueList = (ArrayList<OperationsRevenueBean>)request.getSession().getAttribute("revenueList");
		ExcelHelper helper = new ExcelHelper();
		helper.buildExcelDocumentForOperationRevenue(revenueList,response);

	}
	//Added on 2/4/2018 for Assignments double Payment Report.
	
	//Added to show a report of payments which were recieved twice from student	
	/*	@RequestMapping(value = "/assignmentPaymentReportDoublePaymentsRecievedForm", method = {RequestMethod.GET , RequestMethod.POST})
	public String assignmentPaymentReportDoublePaymentsRecievedForm(HttpServletRequest request, HttpServletResponse response,Model m){
		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);

		return "report/assignmentDoublePaymentReport";
	}

	@RequestMapping(value = "/assignmentPaymentReportDoublePaymentsRecieved", method = {RequestMethod.POST})
	public ModelAndView assignmentPaymentReportDoublePaymentsRecieved(HttpServletRequest request, HttpServletResponse response,@ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelnView = new ModelAndView("report/assignmentDoublePaymentReport");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<AssignmentPaymentBean> assignmentPaymentList = dao.getDoubleAssignmentPaymentsForGivenYearMonth(studentMarks);
			request.getSession().setAttribute("assignmentPaymentList",assignmentPaymentList);

			if(assignmentPaymentList != null && assignmentPaymentList.size() > 0){
				modelnView.addObject("rowCount",assignmentPaymentList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);

		return modelnView;
	}

	@RequestMapping(value = "/downloadAssignmentDoublePaymentReport", method = {RequestMethod.GET})
	public ModelAndView downloadAssignmentDoublePaymentReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		List<AssignmentPaymentBean> assignmentPaymentList = (ArrayList<AssignmentPaymentBean>)request.getSession().getAttribute("assignmentPaymentList");
		return new ModelAndView("assignmentDoublePaymentReportExcelView","assignmentPaymentList",assignmentPaymentList);
	}*/
	//End on 2/4/2018 for Assignments double Payment Report.

	public static void main(String[] args) throws ParseException {
		//Date d = new Date();
		SimpleDateFormat  parser = new SimpleDateFormat("yyyy-mm-dd");
		Date d = parser.parse("2015-02-10");

		Format formatter = new SimpleDateFormat("MMMM d, yyyy");

		formatter = new SimpleDateFormat("MMMM d, yyyy"); 
		String s = formatter.format(new Date());

	}




	//-------- Pending Bookings report for Executive Programs Start -------

	@RequestMapping(value = "/admin/examBookingPendingReportExecutiveForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String examBookingPendingReportExecutiveForm(HttpServletRequest request, HttpServletResponse response, Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		StudentMarksBean marks = new StudentMarksBean();
		m.addAttribute("studentMarks",marks);
		m.addAttribute("yearList", currentYearList);
		m.addAttribute("monthList", executiveExamMonthList);
		return "examBookingPendingReportExecutive";
	}

	@RequestMapping(value = "/admin/examBookingPendingReportExecutive", method = RequestMethod.POST)
	public ModelAndView examBookingPendingReportExecutive(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean studentMarks){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}

		ModelAndView modelnView = new ModelAndView("examBookingPendingReportExecutive");
		request.getSession().setAttribute("studentMarks", studentMarks);
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<ExamBookingTransactionBean> examBookingList = dao.getExecutiveConfirmedBookingForGivenYearMonth(studentMarks, getAuthorizedCodes(request));
			HashMap<String,String> bookingsCompleteStudentSubjectMap = new HashMap<>();
			HashMap<String,String> passStudentSubjectMap = new HashMap<>();
			HashMap<String,String> regIncompleteStudentSubjectMap = new HashMap<>();
			for (int i = 0; i < examBookingList.size(); i++) {
				ExamBookingTransactionBean bean = examBookingList.get(i);
				bookingsCompleteStudentSubjectMap.put(bean.getSapid()+bean.getSubject(), null);
			}

			ArrayList<AssignmentStatusBean> passStudentsList = dao.getPassRecordsExecutive();
			for (AssignmentStatusBean bean : passStudentsList) {
				passStudentSubjectMap.put(bean.getSapid()+bean.getSubject(), null);
			}

			ArrayList<String> examSubjectsList = dao.getSubjectsListWhoseExamHasBeenConducted(studentMarks);

			ArrayList<AssignmentStatusBean> studentsList = dao.getValidExecutiveStudentSubjectList(studentMarks, examSubjectsList,getAuthorizedCodes(request));
			ArrayList<AssignmentStatusBean> regIncompleteStudetsList = new ArrayList<>(); 

			for (int i = 0; i < studentsList.size(); i++) {
				AssignmentStatusBean bean = studentsList.get(i);
				String key = bean.getSapid()+bean.getSubject();
				if(!bookingsCompleteStudentSubjectMap.containsKey(key) 
						&& (!passStudentSubjectMap.containsKey(key)) 
						&& (!regIncompleteStudentSubjectMap.containsKey(key))){
					bean.setExamYear(studentMarks.getYear());
					bean.setExamMonth(studentMarks.getMonth());
					regIncompleteStudetsList.add(bean);
					regIncompleteStudentSubjectMap.put(key, null);
				}
			}

			ArrayList<AssignmentStatusBean> failStudentsList = dao.getValidExecutiveStudentsFailRecords(studentMarks,examSubjectsList,getAuthorizedCodes(request));

			for (AssignmentStatusBean bean : failStudentsList) {
				String key = bean.getSapid()+bean.getSubject();
				if(!bookingsCompleteStudentSubjectMap.containsKey(key) && (!regIncompleteStudentSubjectMap.containsKey(key))){
					bean.setExamYear(studentMarks.getYear());
					bean.setExamMonth(studentMarks.getMonth());
					regIncompleteStudetsList.add(bean);
					regIncompleteStudentSubjectMap.put(key, null);
				}
			}

			HashMap<String, CenterExamBean> centerCodeNameMap = getCentersMap();
			for (AssignmentStatusBean assignmentFileBean : regIncompleteStudetsList) {
				String lc = centerCodeNameMap.get(assignmentFileBean.getCenterCode()) != null ? centerCodeNameMap.get(assignmentFileBean.getCenterCode()).getLc() : "No LC";
				assignmentFileBean.setLc(lc);
			}

			request.getSession().setAttribute("regIncompleteStudetsList",regIncompleteStudetsList);

			if(regIncompleteStudetsList != null && regIncompleteStudetsList.size() > 0){
				modelnView.addObject("rowCount",regIncompleteStudetsList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating records.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("monthList", executiveExamMonthList);
		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadExecutiveExamBookingPendingReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadExecutiveExamBookingPendingReport(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		ArrayList<AssignmentStatusBean> regIncompleteStudetsList = (ArrayList<AssignmentStatusBean>)request.getSession().getAttribute("regIncompleteStudetsList");
		ModelAndView modelnView = new ModelAndView("examBookingPendingReport");
		StudentMarksBean studentMarks =(StudentMarksBean)request.getSession().getAttribute("studentMarks");
		//StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");

		//ExamBookingPendingReportExcelView helper = new ExamBookingPendingReportExcelView();
		try{
			modelnView.addObject("rowCount",regIncompleteStudetsList.size());
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Report Downloaded Successfully");
			return new ModelAndView ("examBookingExecutivePendingReportExcelView","regIncompleteStudetsList",regIncompleteStudetsList);

		}catch(Exception e)
		{
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading report.");
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("yearList", currentYearList);
		modelnView.addObject("monthList", executiveExamMonthList);
		return modelnView;
	}

	//-------- Pending Bookings report for Executive Programs End -------



	//---------- Report for Checking Project Submission Status ------
	@RequestMapping(value = "/admin/projectStatusReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String projectStatusReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		StudentMarksBean marksBean = new StudentMarksBean();
		m.addAttribute("month",ACAD_MONTH_LIST);
		m.addAttribute("year", ACAD_YEAR_LIST);
		m.addAttribute("marksBean", marksBean);

		return "report/projectStatusReport";
	}

	@RequestMapping(value = "/admin/projectStatusReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView projectStatusReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute StudentMarksBean marksBean ) {

		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		String year = "";
		String month = "";
		if(marksBean != null){
			year = marksBean.getYear();
			month = marksBean.getMonth(); 
		}


		ReportsDAO rd = (ReportsDAO)act.getBean("reportsDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		AssignmentsDAO dao = (AssignmentsDAO)act.getBean("asignmentsDAO");

		ArrayList<StudentExamBean> validStudentsNotBookedProject = new ArrayList<StudentExamBean>();
		ArrayList<PassFailExamBean> validStudentsBookedNotClearedProject = new ArrayList<PassFailExamBean>();

		ArrayList<PassFailExamBean> projectStatusReportExcelList = new ArrayList<PassFailExamBean>();
		validStudentsNotBookedProject = rd.getValidNotBookedProjectList(year,month);
		validStudentsBookedNotClearedProject = rd.getValidStudentsNotClearedProjectList(year,month);

		if(validStudentsBookedNotClearedProject !=null) {
			projectStatusReportExcelList.addAll(validStudentsBookedNotClearedProject);
		}
		if(validStudentsNotBookedProject !=null) {
			for(StudentExamBean sapid : validStudentsNotBookedProject){
				PassFailExamBean pb = new PassFailExamBean();
				pb.setSapid(sapid.getSapid());
				pb.setName(sapid.getFirstName()+" "+sapid.getLastName());
				pb.setProgram(sapid.getProgram());
				if("PD - WM".equals(sapid.getProgram())) {
					pb.setSubject("Module 4 - Project");
				} else {
					pb.setSubject("Project");
				}
				pb.setWrittenscore("NB");
				pb.setWrittenMonth("NA");
				pb.setWrittenYear("NA");
				projectStatusReportExcelList.add(pb);
			}
		}
		//Get student project submission for previous exam cycle start
		boolean isResultLiveForLastProjectSubmissionCycle = eDao.isResultLiveForLastProjectSubmissionCycle(); //check if result for last exam cycle is live
		ArrayList<PassFailExamBean> resultsAwaitedList = new ArrayList<PassFailExamBean>();
		if(!isResultLiveForLastProjectSubmissionCycle){
			ArrayList<String> projectBookedInLastCycle= dao.getAllResultAwaitedProjectList();
			if(projectBookedInLastCycle.size()>0 ){
				for(PassFailExamBean bean:projectStatusReportExcelList){
					String student = bean.getSapid();
					if(projectBookedInLastCycle.contains(student)){
						resultsAwaitedList.add(bean);
					}
				}
			}
		}//Get student project submission for previous exam cycle end

		if(resultsAwaitedList.size()>0 ){
			projectStatusReportExcelList.removeAll(resultsAwaitedList);
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		return new ModelAndView("projectStatusReportExcelView","projectStatusReport",projectStatusReportExcelList);
	}

	//Report for Passed in Reval yet registred for next cycle start

	@RequestMapping(value = "/admin/passedInRevalYetRegisteredReportForm", method = RequestMethod.GET)
	public String passedInRevalYetRegisteredReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

		ExamBookingExamBean searchBean = new ExamBookingExamBean();
		m.addAttribute("month",EXAM_MONTH_LIST);
		m.addAttribute("year", ACAD_YEAR_LIST);
		m.addAttribute("searchBean", searchBean);

		return "report/passedInRevalYetRegisteredReport";
	}

	@RequestMapping(value = "/admin/passedInRevalYetRegisteredReport", method = RequestMethod.POST)
	public String passedInRevalYetRegisteredReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ExamBookingExamBean searchBean, Model m ) {

		if(!checkSession(request, response))
		{
			redirectToPortalApp(response);
			return null;
		}

		m.addAttribute("month",EXAM_MONTH_LIST);
		m.addAttribute("year", ACAD_YEAR_LIST);
		m.addAttribute("searchBean",searchBean);
		int rowCount=0;

		ArrayList<ExamBookingExamBean> studentPassedInRevalAndRegisteredReport = new ArrayList<ExamBookingExamBean>();
		ReportsDAO rd = (ReportsDAO)act.getBean("reportsDAO");

		try {

			studentPassedInRevalAndRegisteredReport = rd.getStudentPassedInRevalAndRegistered(searchBean); 

			m.addAttribute("studentPassedInRevalAndRegisteredReport",studentPassedInRevalAndRegisteredReport);
			request.getSession().setAttribute("studentPassedInRevalAndRegisteredReport", studentPassedInRevalAndRegisteredReport);

			rowCount = studentPassedInRevalAndRegisteredReport != null ? studentPassedInRevalAndRegisteredReport.size() : 0;

			if (studentPassedInRevalAndRegisteredReport != null && studentPassedInRevalAndRegisteredReport.size() > 0) {
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating report.");
		}
		m.addAttribute("rowCount",rowCount);

		return "report/passedInRevalYetRegisteredReport";

	}


	@RequestMapping(value = "/admin/downloadPassedInRevalYetRegisteredReport" , method = RequestMethod.GET)
	public String downloadOperationsRevenueReport(HttpServletRequest request, HttpServletResponse response, Model m){

		List<ExamBookingExamBean> studentPassedInRevalAndRegisteredReport = (List<ExamBookingExamBean>) request.getSession().getAttribute("studentPassedInRevalAndRegisteredReport");
		m.addAttribute("studentPassedInRevalAndRegisteredReport",studentPassedInRevalAndRegisteredReport);
		return "studentPassedInRevalAndRegisteredReportExcelView";

	}
	//Report for Passed in Reval yet registred for next cycle end

	@RequestMapping(value = "/admin/caseStudySubmissionReportForm" , method = RequestMethod.GET)
	public ModelAndView caseStudySubmissionReportForm(HttpServletRequest request, HttpServletResponse response, Model m){
		ModelAndView mav = new ModelAndView("report/caseStudySubmissionReport");
		m.addAttribute("yearList",ACAD_YEAR_SAS_LIST);
		m.addAttribute("monthList",SAS_EXAM_MONTH_LIST);
		m.addAttribute("caseStudyBean", new CaseStudyExamBean());
		m.addAttribute("rowCount",0);
		return mav;

	}

	@RequestMapping(value = "/admin/caseStudySubmissionReport" , method = RequestMethod.POST)
	public ModelAndView caseStudySubmissionReport(HttpServletRequest request, HttpServletResponse response,@ModelAttribute CaseStudyExamBean caseStudyBean){
		ModelAndView mav = new ModelAndView("report/caseStudySubmissionReport");
		mav.addObject("yearList",ACAD_YEAR_SAS_LIST);
		mav.addObject("monthList",SAS_EXAM_MONTH_LIST);
		CaseStudyDao dao = (CaseStudyDao)act.getBean("caseDAO");
		List<CaseStudyExamBean> submissions = dao.getAllSubmittedCaseStudyDetails(caseStudyBean.getBatchYear(),caseStudyBean.getBatchMonth());
		mav.addObject("rowCount",submissions.size());
		request.getSession().setAttribute("submissions", submissions);
		return mav;

	}

	@RequestMapping(value = "/admin/downloadCaseStudySubmissionsReport" , method = RequestMethod.GET)
	public String downloadCaseStudySubmissionsReport(HttpServletRequest request, HttpServletResponse response, Model m){

		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
		
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		List<CaseStudyExamBean> submissions = (List<CaseStudyExamBean>) request.getSession().getAttribute("submissions");
		Map<String,StudentExamBean> studentDetails = new HashMap<String,StudentExamBean>();
		m.addAttribute("submissions",submissions);
		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, CenterExamBean> centers = dao.getICLCMap();
		for(CaseStudyExamBean cs : submissions){
			String sapid = cs.getSapid();
			StudentExamBean student = dao.getSingleStudentsData(sapid);
			student.setLc(centers.get(student.getCenterCode()).getLc());
			if(!studentDetails.containsKey(student.getSapid())){
				studentDetails.put(sapid,student);
			}
		}
		m.addAttribute("studentDetails",studentDetails);

		return "studentCaseStudySubmissionReportExcelView";

	}

	public String generateRandomPass(String sapid) {
		String generatedString =null;
		try {
			int randomNum = ThreadLocalRandom.current().nextInt(10, 99 + 1);
			String FirstString=  String.valueOf(randomNum);
			String SecondString = RandomStringUtils.randomNumeric(8);
			generatedString = FirstString+SecondString;
		}
		catch(Exception e) {
			
			return generatedString;
		}

		return generatedString;
	}
	@RequestMapping(value = "/admin/pendingSubjectsForLateralEntriesReportForm" , method = RequestMethod.GET)
	public ModelAndView pendingSubjectsForLateralEntriesReportForm(HttpServletRequest request, HttpServletResponse response, Model m){
		ModelAndView mav = new ModelAndView("report/pendingSubjectsForLateralEntriesReport");

		StudentExamBean studentBean = new StudentExamBean();
		m.addAttribute("studentBean", studentBean);        
		return mav;    	
	}

	@RequestMapping(value = "/admin/pendingSubjectsForLateralEntriesReport" , method = RequestMethod.POST)
	public ModelAndView pendingSubjectsForLateralEntriesReport(@ModelAttribute StudentExamBean studentBean,HttpServletRequest request, HttpServletResponse response, Model m){
		ModelAndView mav = new ModelAndView("report/pendingSubjectsForLateralEntriesReport");


		if(!checkSession(request, response)){
			//return new ModelAndView("studentPortalRediret");
			redirectToPortalApp(response);
			return null;
		}
		String commaSeparatedSapids = generateCommaSeparatedList(studentBean.getSapIdList()); 
		try{
			ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");
			ArrayList<StudentExamBean> studentsList = dao.getPendingSubjectsForLateralEntries(commaSeparatedSapids);			
			request.getSession().setAttribute("studentsList",studentsList);
			if(studentsList != null && studentsList.size() > 0){
				mav.addObject("rowCount",studentsList.size());
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}

		}catch(Exception e){
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		return mav;

	}



	@RequestMapping(value = "/admin/downloadpendingSubjectsForLateralEntriesReport" , method = RequestMethod.GET)
	public ModelAndView downloadIaToEvaluate(HttpServletRequest request,
			HttpServletResponse response ) {
		ArrayList<StudentExamBean> studentsList = new ArrayList<StudentExamBean>();	
		try {
			studentsList = (ArrayList<StudentExamBean>) request.getSession().getAttribute("studentsList");
		}catch(Exception e) {
			
		}
		return new ModelAndView("LateralEntriesStudentsExcelView","studentsList",studentsList);
	}

	public String generateCommaSeparatedList(String sapIdList) {
		String commaSeparatedList = sapIdList.replaceAll("(\\r|\\n|\\r\\n|\\s|\\p{C})+", ",");
		if(commaSeparatedList.endsWith(",")){
			commaSeparatedList = commaSeparatedList.substring(0,  commaSeparatedList.length()-1);
		}
		return commaSeparatedList;
	}
	
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/getPendingSubjectsForStudent" , method = RequestMethod.POST, produces="application/json", consumes="application/json")
//	public ResponseEntity<StudentPendingSubjectsResponseBean> getPendingSubjectsForStudent(HttpServletRequest request, HttpServletResponse response, @RequestBody StudentBean input) {
//
//		StudentPendingSubjectsResponseBean bean = new StudentPendingSubjectsResponseBean();
//		String sapid = input.getSapid();
//		
//		if(StringUtils.isBlank(sapid)) {
//			bean.setStatus("Fail");
//			bean.setMessage("Please enter student number!");
//			return ResponseEntity.ok(bean);
//		}
//		
//		ReportsDAO dao = (ReportsDAO)act.getBean("reportsDAO");	
//		StudentBean student;
//		try {
//			student = dao.getStudentBySapid(sapid);
//		}catch (Exception e) {
//			bean.setStatus("Fail");
//			bean.setMessage("Error fetching student info!");
//			return ResponseEntity.ok(bean);
//		}
//		String studentProgramType;
//		switch(student.getProgram()) {
//		case "MBA - WX" : 
//			studentProgramType = "MBAWX";
//			break;
//		case "MBA - X" : 
//			studentProgramType = "MBAX";
//			break;
//		default : 
//			studentProgramType = "PG";
//			break;
//		}
//		StudentPendingSubjectsEligibleFactory.ProductType productType = StudentPendingSubjectsEligibleFactory.ProductType.valueOf(studentProgramType);
//		StudentPendingSubjectsEligibleFactory factory = (StudentPendingSubjectsEligibleFactory) act.getBean("studentPendingSubjectsEligibleFactory");
//		try {
//			StudentPendingSubjectsEligibilityStrategyInterface strategy = factory.getProductType(productType);
//			List<StudentPendingSubjectBean> pendingSubjects = strategy.getPendingSubjectsForStudent(student);
//			bean.setStatus("Success");
//			bean.setPendingSubjects(pendingSubjects);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			
//			bean.setStatus("Fail");
//			bean.setMessage(e.getMessage());
//		}
//
//		return ResponseEntity.ok(bean);
//	}
	
	@RequestMapping(value = "/admin/ugConsentReportForm", method = RequestMethod.GET)
	public ModelAndView ugConsentReportForm(HttpServletRequest request, HttpServletResponse response,Model m) {
		if (!checkSession(request, response)) {
			return new ModelAndView("login");
		}
		UGConsentExcelReportBean ugStudent = new UGConsentExcelReportBean();
		m.addAttribute("ugStudent",ugStudent);
		return new ModelAndView("report/ugConsentReportForm");
	}
	
	@RequestMapping(value = "/admin/ugConsentReport", method = RequestMethod.POST)
	public ModelAndView ugConsentReport(HttpServletRequest request, HttpServletResponse response,@ModelAttribute UGConsentExcelReportBean ugStudent,Model m) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView modelnView = new ModelAndView("report/ugConsentReportForm");
		m.addAttribute("ugStudent",ugStudent);
		request.getSession().setAttribute("type",ugStudent.getType());
		try{
			ArrayList<UGConsentExcelReportBean> studentList = null;
			if(ugStudent.getType().equals("Pending")) 
				studentList = reportService.getUgConsentStudentPendingDetails(ugStudent,getAuthorizedCodes(request));
			else
				studentList = reportService.getUgConsentStudentSubmittedDetails(ugStudent,getAuthorizedCodes(request));
			
			if(studentList != null && studentList.size() > 0){
				modelnView.addObject("rowCount",studentList.size());
				request.setAttribute("success","true");
				request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
			}
			request.getSession().setAttribute("ugStudentList",studentList);
		
		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in generating Report.");
		}
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/downloadUGConsentReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadUGConsentReport(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		List<UGConsentExcelReportBean> studentList = (ArrayList<UGConsentExcelReportBean>)request.getSession().getAttribute("ugStudentList");
		return new ModelAndView("ugConsentReportExcelView","studentList",studentList);
	}
	
}
