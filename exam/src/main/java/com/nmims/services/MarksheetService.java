package com.nmims.services;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.nmims.beans.CenterExamBean;
import com.nmims.beans.EmbaMarksheetBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExamResultsBean;
import com.nmims.beans.MBAWXExamResultForSubject;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.controllers.MarksheetController;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.exceptions.NoRecordFoundException;
import com.nmims.helpers.CreatePDF;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.helpers.PassFailSubjectHelper;
import com.nmims.interfaces.DissertationGradesheet_TranscriptService;
import com.nmims.util.StringUtility;

@Service("msService")
public class MarksheetService {
	@Autowired
	ApplicationContext act;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private PassFailDAO passFailDAO;

	@Value("${MARKSHEETS_PATH}")
	private String MARKSHEETS_PATH;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Autowired
	FileUploadHelper fileUploadHelper;
	
	@Autowired
	private ExamsAssessmentsDAO examsAssessmentsDAO;
	
	@Autowired
	private StudentMarksDAO studentMarksDAO;
	
	@Autowired
	DissertationGradesheet_TranscriptService dissertationService;

	
	@Autowired
	ModeOfDeliveryService modService;
	
	private ArrayList<CenterExamBean> centers = null;
	private HashMap<String, CenterExamBean> centersMap = null;
	private HashMap<String, String> programCodeNameMap = null;
	private HashMap<String, ProgramExamBean> programMap = null;
	private Map<String,ExamResultsBean> nonGradedProgramsResultDatesMap;
	private Map<String, ProgramExamBean> modProgramMap = null;
	

	
	@Value( "${MARKSHEET_BUCKENAME}" )
	private String 	MARKSHEET_BUCKENAME;
	
	private final static String base_MarksheetPath = "Marksheets/";
	
	private final static int  IA_MASTER_DISSERTATION_MASTER_KEY = 131;
	
	private final static int IA_MASTER_DISSERTATION_Q7_SEM = 7;
	
	private final static int IA_MASTER_DISSERTATION_Q8_SEM = 8;
	
	private static final Logger dissertationLogs = (Logger) LoggerFactory.getLogger("dissertationResultProcess");
	
	public static final Logger logger = LoggerFactory.getLogger(MarksheetService.class);
	
	//Non-Graded Certificate programs consumerProgramStructureIds list. 
	public static final List<Integer> NON_GRADED_MASTER_KEY_LIST = (List<Integer>) Arrays.asList(142,143,144,145,146,147,148,149,155);

	public MarksheetBean mStudentSelfMarksheet(PassFailExamBean studentMarks) throws SQLException {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		MarksheetBean marksheetBean = new MarksheetBean();
		MarksheetController msController = new MarksheetController();
		String sapId = studentMarks.getSapid();

		boolean hasAppearedForExamForGivenSemMonthYear = dao
				.hasAppearedForExamForGivenSemMonthYear(studentMarks,studentMarks.getExamMode());
		
		
		if (hasAppearedForExamForGivenSemMonthYear == false) {
			marksheetBean.setError(true);
			marksheetBean.setMessage(
					"You have not appeared for Semester "
							+ studentMarks.getSem() + " in Year "
							+ studentMarks.getWrittenYear() + " and month "
							+ studentMarks.getWrittenMonth());
			
			return marksheetBean;
		}
		List<StudentMarksBean> studentMarksBean = getStudentMarksHistory(studentMarks, marksheetBean);



		// Check if results are live

		marksheetBean = dao.getSingleStudentsData(studentMarks);
		String programStructure = marksheetBean.getPrgmStructApplicable();
		String resultDeclarationDate = "";
		ExamOrderExamBean exam = dao.getExamDetails(studentMarks.getWrittenMonth(),
				studentMarks.getWrittenYear());
		if (exam == null) {
			marksheetBean.setError(true);
			marksheetBean.setMessage(
					"No Exam Bookings found for "
							+ studentMarks.getWrittenMonth() + "-"
							+ studentMarks.getWrittenYear()
							+ " Exam, Semester " + studentMarks.getSem());
			return marksheetBean;
		}
		String resultLive = "N";

		if ("Online".equals(studentMarks.getExamMode())) {
			resultDeclarationDate = dao.getOnlineExamDeclarationDate(
					studentMarks.getWrittenMonth(),
					studentMarks.getWrittenYear());
			resultLive = exam.getLive();
		} else {
			resultDeclarationDate = dao.getOfflineExamDeclarationDate(
					studentMarks.getWrittenMonth(),
					studentMarks.getWrittenYear());
			resultLive = exam.getOflineResultslive();
		}
		marksheetBean.setResultDeclarationDate(resultDeclarationDate);
		if (!"Y".equalsIgnoreCase(resultLive)) {
			marksheetBean.setError(true);
			marksheetBean.setMessage(
					"Results are not yet announced for "
							+ studentMarks.getWrittenMonth() + "-"
							+ studentMarks.getWrittenYear() + " Exam cycle.");
			return marksheetBean;
		}
		HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudent(
				studentMarks.getSem(), studentMarks.getSapid(),
				studentMarks.getWrittenMonth(), studentMarks.getWrittenYear());
		if (keysMap == null || keysMap.size() == 0) {
			marksheetBean.setError(true);
			marksheetBean.setMessage( "No records found.");
			return marksheetBean;
		}

		// ArrayList<PassFailBean> passFailStudentList = dao.process(keysMap);
		ArrayList<PassFailExamBean> passFailStudentList = null;

//		if("ACBM".equalsIgnoreCase(studentMarks.getProgram())){
		if("Bajaj".equalsIgnoreCase(studentMarks.getConsumerType())){
//			passFailStudentList = dao.processACBM(keysMap);
			passFailStudentList = dao.processBajaj(keysMap);
		} else {
			passFailStudentList = dao.processNew(keysMap);// Use new pass fail
															// logic
		}
		Boolean isProjectApplicable =  sDao.isProjectApplicable(marksheetBean.getProgram(),studentMarks.getSem(),marksheetBean.getPrgmStructApplicable(),marksheetBean.getSapid());
		Boolean isModuleProjectApplicable =  sDao.isModuleProjectApplicable(marksheetBean.getProgram(),studentMarks.getSem(),marksheetBean.getPrgmStructApplicable(),marksheetBean.getSapid());
		List<PassFailExamBean> studentMarksList = new ArrayList<>();
		// Take only those records where he has written or assignment year/month
		// is same one as selected on form.
		if (passFailStudentList != null && !passFailStudentList.isEmpty()) {
			// if("4".equalsIgnoreCase(studentData.getSem())) {
			if(isProjectApplicable.equals(true)) {
				  if(!keysMap.containsKey(marksheetBean.getSapid().trim()+"Project")) {
				  PassFailExamBean passFailBean = new PassFailExamBean();
				  passFailBean.setSapid(marksheetBean.getSapid());
				  passFailBean.setSubject("Project"); passFailBean.setSem(studentMarks.getSem());
				  passFailBean.setProgram(marksheetBean.getProgram());
				  passFailBean.setName(marksheetBean.getFirstName()+" "+marksheetBean.getLastName());
				  passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
				  passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
				  passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
				  passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
				  }
				  
				  }
			if(isModuleProjectApplicable.equals(true)) {
				if(!keysMap.containsKey(marksheetBean.getSapid().trim()+"Module 4 - Project")) {
					PassFailExamBean passFailBean = new PassFailExamBean();
					passFailBean.setSapid(marksheetBean.getSapid());
					passFailBean.setSubject("Module 4 - Project"); passFailBean.setSem(studentMarks.getSem());
					passFailBean.setProgram(marksheetBean.getProgram());
					passFailBean.setName(marksheetBean.getFirstName()+" "+marksheetBean.getLastName());
					passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
					passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
					passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
					passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
				}
				
			}
			studentMarksList.addAll(passFailStudentList);
			
			studentMarksList = msController.handleGraceMarksLogic(studentMarksList);
			
			
		}

		HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
		String examMonth = studentMarks.getWrittenMonth();
		String examYear = studentMarks.getWrittenYear();

		CreatePDF helper = new CreatePDF();
		helper.generateSingleStudentMarksheet(studentMarksList, marksheetBean,
				examMonth, examYear, examOrderMap, getCentersMap());
		List<MarksheetBean> marksheetList = new ArrayList<MarksheetBean>();
		marksheetList.add(marksheetBean);
		try {
			marksheetBean = helper.mCreateStudentSelfMarksheetPDF(marksheetList,
					resultDeclarationDate, marksheetBean, getProgramAllDetailsMap(),
					getCentersMap(), MARKSHEETS_PATH);
			marksheetBean.setSuccess(true);
			marksheetBean.setMessage( "Marksheet generated successfully. Please click Download link below to get file.");
			
		} catch (Exception e) {
			
			marksheetBean.setError(true);
			marksheetBean.setMessage("Error in generating marksheet." + e.getMessage());
		}
		return marksheetBean;
	}
	

	private List<StudentMarksBean> getStudentMarksHistory(PassFailExamBean psBean, MarksheetBean msBean) {
		StudentMarksBean bean = new StudentMarksBean();
		StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		bean.setSapid(psBean.getSapid());
		List<StudentMarksBean> studentMarksListForMarksHistory = null;
		if ("Online".equals(psBean.getExamMode())) {
			studentMarksListForMarksHistory = dao
					.getAStudentsMarksForOnline(bean);
		} else {
			studentMarksListForMarksHistory = dao
					.getAStudentsMarksForOffline(bean);
		}
	
		
		return studentMarksListForMarksHistory;
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

	public HashMap<String, String> getProgramMap() {
		if (this.programCodeNameMap == null
				|| this.programCodeNameMap.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.programCodeNameMap = dao.getProgramDetails();
		}
		return programCodeNameMap;
	}
	
	public Map<String, ExamResultsBean> getNonGradedProgramsResultDatesMap() throws SQLException {
		if (this.nonGradedProgramsResultDatesMap == null
				|| this.nonGradedProgramsResultDatesMap.size() == 0) {
			this.nonGradedProgramsResultDatesMap = examsAssessmentsDAO.getResultDeclarationsMap();
		}
		return nonGradedProgramsResultDatesMap;
	}
	
	private String getMonthName(String examMonth) {
		switch(examMonth) {
			case "Jan" : return "January";
			case "Feb" : return "February";
			case "Mar" : return "March";
			case "Apr" : return "April";
			case "May" : return "May";
			case "Jun" : return "June";
			case "Jul" : return "July";
			case "Aug" : return "August";
			case "Sep" : return "September";
			case "Oct" : return "October";
			case "Nov" : return "November";
			case "Dec" : return "December";
			default : return null;
		}
	}

	//Added to get mapped Mode of Learning by program code - by shivam.pandey.EXT
	public Map<String, ProgramExamBean> getModProgramMap()throws Exception {
		this.modProgramMap = modService.getModProgramMap();
		return modProgramMap;
	}

	public EmbaMarksheetBean mStudentSelfMarksheetForMbaWx(EmbaPassFailBean studentMarks) throws SQLException {
		
		//PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO"); 
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();

		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");	

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		StudentExamBean student = sapIdStudentsMap.get(studentMarks.getSapid());		
		String term = studentMarks.getSem();

		CreatePDF helper = new CreatePDF();
		
		marksheetBean.setFirstName(student.getFirstName());
		marksheetBean.setLastName(student.getLastName());
		marksheetBean.setSapid(student.getSapid());
		marksheetBean.setFatherName(student.getFatherName());
		marksheetBean.setMotherName(student.getMotherName());
		marksheetBean.setExamMonth("October");
		marksheetBean.setExamYear("2019");
		marksheetBean.setProgram(student.getProgram());
		//marksheetBean.setEnrollmentMonth(student.getEnrollmentMonth());
		//marksheetBean.setEnrollmentYear(student.getEnrollmentYear());
		marksheetBean.setEnrollmentMonth("July");
		marksheetBean.setEnrollmentYear("2019");
		
		marksheetBean.setSem(term);

		List<EmbaMarksheetBean> marksheetList = new ArrayList<EmbaMarksheetBean>();
		
		marksheetList.add(marksheetBean);
		
		List<EmbaPassFailBean> passFailDataList = new ArrayList<EmbaPassFailBean>();
		try {
			passFailDataList = examsAssessmentsDAO.getEmbaPassFailBySapid(student.getSapid(),student.getSem());
		}catch(Exception e) {
			
		}
		List<EmbaPassFailBean> passFailDataListAllSem = new ArrayList<EmbaPassFailBean>();
		try {
			//passFailDataListAllSem = examsAssessmentsDAO.getEmbaPassFailBySapidTermMonthYearAllSem(sapid, term, acadsMonth, acadsYear);
		}catch(Exception e) {
			
		}
		
		try {
			marksheetBean = helper.mCreateStudentSelfMarksheetPDFforMbaWX(marksheetList,	
					 marksheetBean,
					 MARKSHEETS_PATH,passFailDataList,				passFailDataListAllSem, student.getConsumerProgramStructureId(),getModProgramMap()
);
			marksheetBean.setSuccess(true);
			marksheetBean.setMessage( "Marksheet generated successfully. Please click Download link below to get file.");
						

		} catch (Exception e) {
			logger.error("Error in generating marksheet of mStudentSelfMarksheetForMbaWx: "+e);
			marksheetBean.setError(true);
			marksheetBean.setMessage("Error in generating marksheet." + e.getMessage());
		}
		return marksheetBean;
	}



	public EmbaMarksheetBean mStudentSelfMarksheetForTermMonthYearForMbaWx(MBAWXExamResultForSubject studentMarks) throws SQLException {
	
		//PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();

		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");	

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentExamBean student = dao.getStudentDetails(studentMarks.getSapid());		
		
		if(NON_GRADED_MASTER_KEY_LIST.contains(Integer.parseInt(student.getConsumerProgramStructureId()))) {
			this.mGenerateNonGradedStudentSelfMarksheet(studentMarks);
		}
		
		CreatePDF helper = new CreatePDF();
 
		String sapid = studentMarks.getSapid();
		String term = studentMarks.getTerm();
		String examMonth = studentMarks.getExamMonth();
		String examYear = studentMarks.getExamYear();
		String acadsMonth = studentMarks.getAcadsMonth();
		String acadsYear = studentMarks.getAcadsYear();
		
		marksheetBean.setFirstName(student.getFirstName());
		marksheetBean.setLastName(student.getLastName());
		marksheetBean.setSapid(student.getSapid());
		marksheetBean.setFatherName(student.getFatherName());
		marksheetBean.setMotherName(student.getMotherName());
		marksheetBean.setExamMonth(getMonthName(examMonth));
		marksheetBean.setExamYear(examYear);
		marksheetBean.setProgram(student.getProgram());
		marksheetBean.setProgramname(student.getProgramForHeader());
		marksheetBean.setEnrollmentMonth(getMonthName(acadsMonth));
		marksheetBean.setEnrollmentYear(acadsYear);
		marksheetBean.setSem(term);
		
		
		List<EmbaMarksheetBean> marksheetList = new ArrayList<EmbaMarksheetBean>();
		
		marksheetList.add(marksheetBean);
		
		List<EmbaPassFailBean> passFailDataList = new ArrayList<EmbaPassFailBean>();
		List<EmbaPassFailBean> passFailDataListAllSem = new ArrayList<EmbaPassFailBean>();

		try {
			passFailDataList = examsAssessmentsDAO.getEmbaPassFailBySapidTermMonthYear(sapid, term, acadsMonth, acadsYear);
			
			if(IA_MASTER_DISSERTATION_MASTER_KEY == Integer.parseInt(student.getConsumerProgramStructureId())) {
				if(IA_MASTER_DISSERTATION_Q7_SEM == Integer.parseInt(term)) {
					EmbaPassFailBean  passFailBean = dissertationService.getPassFailForQ7(sapid);
					if(null!=passFailBean && !StringUtils.isEmpty(passFailBean.getGrade())) {
						passFailBean.setConsumerProgramStructureId(IA_MASTER_DISSERTATION_MASTER_KEY);
						passFailBean.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q7_SEM));			
						passFailDataList.add(passFailBean);
					}
				}
				if(IA_MASTER_DISSERTATION_Q8_SEM == Integer.parseInt(term)) {
					EmbaPassFailBean passFailBean = dissertationService.getPassFailForQ8(sapid);
					if(null!=passFailBean  && !StringUtils.isEmpty(passFailBean.getGrade())) {
						passFailBean.setConsumerProgramStructureId(IA_MASTER_DISSERTATION_MASTER_KEY);
						passFailDataList.add(passFailBean);
					}
				}
			}
		}catch(Exception e) {
			dissertationLogs.info("Error Found while mStudentSelfMarksheetForTermMonthYearForMbaWx  "+sapid+" "+e);
		}
		
		try {
			passFailDataListAllSem = examsAssessmentsDAO.getEmbaPassFailBySapidTermMonthYearAllSem(sapid, term, acadsMonth, acadsYear);
			
			if(IA_MASTER_DISSERTATION_MASTER_KEY == Integer.parseInt(student.getConsumerProgramStructureId())) {
				
				if(IA_MASTER_DISSERTATION_Q7_SEM == Integer.parseInt(term)) {
					EmbaPassFailBean  passFailBean = dissertationService.getPassFailForQ7(sapid);
					if(null!=passFailBean) {
						passFailBean.setConsumerProgramStructureId(IA_MASTER_DISSERTATION_MASTER_KEY);
						passFailBean.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q7_SEM));
						passFailDataListAllSem.add(passFailBean);
					}
				}
				if(IA_MASTER_DISSERTATION_Q8_SEM == Integer.parseInt(term)) {
					EmbaPassFailBean passFailBean = dissertationService.getPassFailForQ8(sapid);
					EmbaPassFailBean  passFailBeanForQ7 = dissertationService.getPassFailForQ7(sapid);
					if(null!=passFailBean) {
						passFailBeanForQ7.setConsumerProgramStructureId(IA_MASTER_DISSERTATION_MASTER_KEY);
						passFailBeanForQ7.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q7_SEM));
						passFailBean.setConsumerProgramStructureId(IA_MASTER_DISSERTATION_MASTER_KEY);
						passFailBean.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q8_SEM));
						passFailDataListAllSem.add(passFailBeanForQ7);
						passFailDataListAllSem.add(passFailBean);
					}
				}	
			}
		}catch(Exception e) {
			dissertationLogs.info("Error Found while mStudentSelfMarksheetForTermMonthYearForMbaWx  "+sapid+" "+e);
		}

		try {
			marksheetBean = helper.mCreateStudentSelfMarksheetPDFforMbaWX(marksheetList,	
					marksheetBean,
					MARKSHEETS_PATH,
					passFailDataList,
					passFailDataListAllSem, student.getConsumerProgramStructureId(), getModProgramMap()
				);

			//Upload File On S3 And delete
			String marksheet_url = fileUploadHelper.uploadDocument(marksheetBean.getFileName(), base_MarksheetPath, MARKSHEET_BUCKENAME);
			
			/*
			 * Commented by Riya as file will be serve from s3
			 */
			// Added by Ashutosh. 
			// Dont expose file system path to user.
           // String downloadPath = marksheetBean.getFileName().split(":/")[1];        
           // marksheetBean.setDownloadPath(downloadPath);
			marksheetBean.setDownloadPath(marksheet_url);
            marksheetBean.setFileName(null);
            
			marksheetBean.setSuccess(true);
			marksheetBean.setMessage( "Marksheet generated successfully. Please click Download link below to get file.");
						

		} catch (Exception e) {
			logger.error("Error in generating marksheet of mStudentSelfMarksheetForTermMonthYearForMbaWx: "+e);
			marksheetBean.setError(true);
			marksheetBean.setMessage("Error in generating marksheet." + e.getMessage());
		}
		return marksheetBean;
	}
	
	public EmbaMarksheetBean mGenerateNonGradedStudentSelfMarksheet(MBAWXExamResultForSubject stdExamResult) {
		List<EmbaPassFailBean> passFailDataList = new ArrayList<EmbaPassFailBean>();
		List<EmbaPassFailBean> passFailDataListAllSem = new ArrayList<EmbaPassFailBean>();
		EmbaMarksheetBean studentData = new EmbaMarksheetBean();
		
		studentData.setSapid(stdExamResult.getSapid());
		studentData.setSem(stdExamResult.getTerm());
		studentData.setYear(stdExamResult.getAcadsYear());
		studentData.setMonth(stdExamResult.getAcadsMonth());
		studentData.setExamYear(stdExamResult.getExamYear());
		studentData.setExamMonth(stdExamResult.getExamMonth());
		
		try {
			//Get result declaration date's map 
			Map<String,ExamResultsBean> resultDatesMap = this.getNonGradedProgramsResultDatesMap();
			
			//Prepare student mark sheet bean
			studentData = this.prepareMarksheetBean(studentData, getCentersMap());
		
			//Set result declaration date 
			this.setResultDeclarationDate(resultDatesMap,studentData);
			
			List<EmbaMarksheetBean> marksheetList = new ArrayList<EmbaMarksheetBean>();
			
			marksheetList.add(studentData);

			try {
				passFailDataList = examsAssessmentsDAO.getEmbaPassFailBySapidTermMonthYear(studentData.getSapid(), studentData.getSem());
			}catch(Exception e) {
				
			}
			
			try {
				passFailDataListAllSem = examsAssessmentsDAO.getEmbaPassFailBySapidTermMonthYearAllSem(studentData.getSapid(), studentData.getSem());
			}catch(Exception e) {
				
			}
			studentData.setError(true);
			studentData.setMessage("Implementation error in generating marksheet.");
		
		} catch (SQLException e) {
			studentData.setError(true);
			studentData.setMessage("Error in generating marksheet." + e.getMessage());
		}
		
		return studentData;
	}
	
	public EmbaMarksheetBean mStudentSelfMarksheetForTermMonthYearForMbaX(MBAWXExamResultForSubject studentMarks) throws SQLException {
		
		//PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();

		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");	

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		StudentExamBean student = sapIdStudentsMap.get(studentMarks.getSapid());		
		
		CreatePDF helper = new CreatePDF();

		String sapid = studentMarks.getSapid();
		String term = studentMarks.getTerm();
		String examMonth = studentMarks.getExamMonth();
		String examYear = studentMarks.getExamYear();
		String acadsMonth = studentMarks.getAcadsMonth();
		String acadsYear = studentMarks.getAcadsYear();
		
		marksheetBean.setFirstName(student.getFirstName());
		marksheetBean.setLastName(student.getLastName());
		marksheetBean.setSapid(student.getSapid());
		marksheetBean.setFatherName(student.getFatherName());
		marksheetBean.setMotherName(student.getMotherName());
		marksheetBean.setExamMonth(getMonthName(examMonth));
		marksheetBean.setExamYear(examYear);
		marksheetBean.setProgram(student.getProgram());
		marksheetBean.setEnrollmentMonth(getMonthName(acadsMonth));
		marksheetBean.setEnrollmentYear(acadsYear);
		marksheetBean.setSem(term);

		
		List<EmbaMarksheetBean> marksheetList = new ArrayList<EmbaMarksheetBean>();
		List<EmbaPassFailBean> passFailDataListAllSem = new ArrayList<EmbaPassFailBean>();

		marksheetList.add(marksheetBean);
		
		List<EmbaPassFailBean> passFailDataList = new ArrayList<EmbaPassFailBean>();
		passFailDataList = examsAssessmentsDAO.getMbaXPassFailForStructureChangeStudent(sapid, term);
		if(passFailDataList.size() == 0) {
			try {
				passFailDataList = examsAssessmentsDAO.getMbaXPassFailBySapidTermMonthYear(sapid, term, acadsMonth, acadsYear);
			}catch(Exception e) {
				
			}
		}
		
		passFailDataListAllSem = examsAssessmentsDAO.getMbaXPassFailAllSemForStructureChangeStudent(sapid, term );
		
		if(passFailDataListAllSem.size() == 0) {
			try {
				passFailDataListAllSem = examsAssessmentsDAO.getMbaXPassFailBySapidTermMonthYearAllSem(sapid, term, acadsMonth, acadsYear);
	
			}catch(Exception e) {
				
			}
		}
		
		
		int passCount = 0;
		int sum = passFailDataList.size();	

	 	StringBuilder sb = new StringBuilder();		
		String commaSeparatedSubjects = "";
		for (EmbaPassFailBean e : passFailDataList) {
			if(e.getIsPass().equals("Y") && !StringUtils.isBlank(e.getGrade())) {
				passCount++;
			}else {
				 sb.append(e.getSubject()).append(",");				
			}
		}
		if(sb.length() > 0) {			
		commaSeparatedSubjects = sb.deleteCharAt(sb.length() - 1).toString();
		}
		
		if(sum == passCount || examsAssessmentsDAO.isResitResultLive(sapid, term)){				//not to return marksheet for failed subjects 
			
			try {
				marksheetBean = helper.mCreateStudentSelfMarksheetPDFforMbaX(marksheetList,	
						marksheetBean,
						MARKSHEETS_PATH,
						passFailDataList,
						passFailDataListAllSem,
						getModProgramMap()
					);

//				marksheetBean = helper.mCreateStudentSelfMarksheetPDFforMbaWX(marksheetList,	
//					marksheetBean,
//					MARKSHEETS_PATH,
//					passFailDataList,examsAssessmentsDAO,studentMarks,
//					passFailDataListAllSem
//
//				);
				
				//Upload File On S3 And delete
				String marksheet_url = fileUploadHelper.uploadDocument(marksheetBean.getFileName(), base_MarksheetPath, MARKSHEET_BUCKENAME);
				
				/*
				 * Commented by Riya as file will be serve from s3
				 */
	
				// Added by Ashutosh. 
				// Dont expose file system path to user.
	           // String downloadPath = SERVER_PATH+marksheetBean.getFileName().split(":/")[1];
	            //marksheetBean.setDownloadPath(downloadPath);
				
				marksheetBean.setDownloadPath(marksheet_url);
	            marksheetBean.setFileName(null);            
				marksheetBean.setSuccess(true);
				marksheetBean.setSubjects(null);
				marksheetBean.setMessage( "Gradesheet generated successfully. Please click Download link below to get file.");						
	
			} catch (Exception e) {
				logger.error("Error in generating gradesheet of mStudentSelfMarksheetForTermMonthYearForMbaX: "+e);
				marksheetBean.setError(true);
				marksheetBean.setMessage("Error in generating Gradesheet." + e.getMessage());
				marksheetBean.setSubjects(null);
			}		}else {
					marksheetBean.setError(true);
					marksheetBean.setMessage("Gradesheet is not Available since the student has failed in subject : "+commaSeparatedSubjects+". Will be availabe after Resit Results are declared.");
					marksheetBean.setSubjects(null);
		}
		return marksheetBean;
		}

	public EmbaMarksheetBean generateMarksheetFromSRForMBAWX(EmbaPassFailBean studentMarks,EmbaMarksheetBean studentData,HashMap<String, CenterExamBean> centersMap) throws SQLException {
		
		//PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();

		//ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");	

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		StudentExamBean student = dao.getStudentDetails(studentMarks.getSapid());		

		//CreatePDF helper = new CreatePDF();
		marksheetBean.setSem(studentData.getSem());
		marksheetBean.setFirstName(student.getFirstName());
		marksheetBean.setLastName(student.getLastName());
		marksheetBean.setSapid(student.getSapid());
		marksheetBean.setFatherName(student.getFatherName());
		marksheetBean.setMotherName(student.getMotherName());
		marksheetBean.setProgram(student.getProgram());
		marksheetBean.setProgramname(student.getProgramForHeader());
		//marksheetBean.setEnrollmentMonth(student.getEnrollmentMonth());
		//marksheetBean.setEnrollmentYear(student.getEnrollmentYear());
		String examMonth = studentData.getExamMonth();
		String examYear = studentData.getExamYear();
		marksheetBean.setEnrollmentMonth(examMonth);
		marksheetBean.setEnrollmentYear(examYear);
		marksheetBean.setMonth(studentData.getMonth());
		marksheetBean.setYear(studentData.getYear());
		marksheetBean.setExamMonth(examMonth);
		marksheetBean.setExamYear(examYear);
		marksheetBean.setConsumerProgramStructureId(Integer.parseInt(student.getConsumerProgramStructureId()));

		marksheetBean.setCenterCode(studentData.getCenterCode());
		CenterExamBean center = centersMap.get(studentData.getCenterCode());
		if(center != null){
			marksheetBean.setLc(center.getLc());
		}
		
		
//		List<EmbaMarksheetBean> marksheetList = new ArrayList<EmbaMarksheetBean>();
//		
//		marksheetList.add(marksheetBean);
		
//		List<EmbaPassFailBean> passFailDataList = new ArrayList<EmbaPassFailBean>();
//		try {
//			passFailDataList = examsAssessmentsDAO.getEmbaPassFailBySapid(student.getSapid());
//		}catch(Exception e) {
//			
//		}
//		try {
//			marksheetBean = helper.mCreateStudentSelfMarksheetPDFforMbaWX(marksheetList,	
//					 marksheetBean,
//					 MARKSHEETS_PATH,passFailDataList);
//			marksheetBean.setSuccess(true);
//			marksheetBean.setMessage( "Marksheet generated successfully. Please click Download link below to get file.");
//						
//
//		} catch (Exception e) {
//			
//			marksheetBean.setError(true);
//			marksheetBean.setMessage("Error in generating marksheet." + e.getMessage());
//		}
		return marksheetBean;
	}
	
public EmbaMarksheetBean prepareMarksheetBean(EmbaMarksheetBean studentData,HashMap<String, CenterExamBean> centersMap) throws SQLException {
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();
		
		StudentExamBean student = studentMarksDAO.getStudentDetails(studentData.getSapid());		

		marksheetBean.setSem(studentData.getSem());
		marksheetBean.setFirstName(student.getFirstName());
		marksheetBean.setLastName(student.getLastName());
		marksheetBean.setSapid(student.getSapid());
		marksheetBean.setFatherName(student.getFatherName());
		marksheetBean.setMotherName(student.getMotherName());
		marksheetBean.setProgram(student.getProgram());
		marksheetBean.setProgramname(student.getProgramForHeader());
	
		marksheetBean.setEnrollmentMonth(student.getEnrollmentMonth());
		marksheetBean.setEnrollmentYear(student.getEnrollmentYear());
		marksheetBean.setValidityEndMonth(student.getValidityEndMonth());
		marksheetBean.setValidityEndYear(student.getValidityEndYear());
		
		marksheetBean.setMonth(studentData.getMonth());
		marksheetBean.setYear(studentData.getYear());
		marksheetBean.setExamMonth(studentData.getExamMonth());
		marksheetBean.setExamYear(studentData.getExamYear());

		marksheetBean.setCenterCode(student.getCenterCode());
		CenterExamBean center = centersMap.get(student.getCenterCode());
		if(center != null){
			marksheetBean.setLc(center.getLc());
		}	
		return marksheetBean;
	}
	
	
public EmbaMarksheetBean generateMarksheetFromSRForMBAX(EmbaPassFailBean studentMarks,EmbaMarksheetBean studentData,HashMap<String, CenterExamBean> centersMap) throws SQLException {
		
		//PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();

		//ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");	

		StudentMarksDAO dao = (StudentMarksDAO)act.getBean("studentMarksDAO");
		HashMap<String, StudentExamBean> sapIdStudentsMap = dao.getAllStudents();
		StudentExamBean student = sapIdStudentsMap.get(studentMarks.getSapid());		

		//CreatePDF helper = new CreatePDF();
		marksheetBean.setSem(studentData.getSem());
		marksheetBean.setFirstName(student.getFirstName());
		marksheetBean.setLastName(student.getLastName());
		marksheetBean.setSapid(student.getSapid());
		marksheetBean.setFatherName(student.getFatherName());
		marksheetBean.setMotherName(student.getMotherName());
		marksheetBean.setProgram(student.getProgram());
		//marksheetBean.setEnrollmentMonth(student.getEnrollmentMonth());
		//marksheetBean.setEnrollmentYear(student.getEnrollmentYear());
		String examMonth = studentData.getExamMonth();
		String examYear = studentData.getExamYear();
		marksheetBean.setEnrollmentMonth(examMonth);
		marksheetBean.setEnrollmentYear(examYear);
		marksheetBean.setMonth(studentData.getMonth());
		marksheetBean.setYear(studentData.getYear());
		marksheetBean.setExamMonth(examMonth);
		marksheetBean.setExamYear(examYear);

		marksheetBean.setCenterCode(studentData.getCenterCode());
		CenterExamBean center = centersMap.get(studentData.getCenterCode());
		if(center != null){
			marksheetBean.setLc(center.getLc());
		}
		
		
//		List<EmbaMarksheetBean> marksheetList = new ArrayList<EmbaMarksheetBean>();
//		
//		marksheetList.add(marksheetBean);
		
//		List<EmbaPassFailBean> passFailDataList = new ArrayList<EmbaPassFailBean>();
//		try {
//			passFailDataList = examsAssessmentsDAO.getEmbaPassFailBySapid(student.getSapid());
//		}catch(Exception e) {
//			
//		}
//		try {
//			marksheetBean = helper.mCreateStudentSelfMarksheetPDFforMbaWX(marksheetList,	
//					 marksheetBean,
//					 MARKSHEETS_PATH,passFailDataList);
//			marksheetBean.setSuccess(true);
//			marksheetBean.setMessage( "Marksheet generated successfully. Please click Download link below to get file.");
//						
//
//		} catch (Exception e) {
//			
//			marksheetBean.setError(true);
//			marksheetBean.setMessage("Error in generating marksheet." + e.getMessage());
//		}
		return marksheetBean;
	}
	public HashMap<String, ProgramExamBean> getProgramAllDetailsMap() {
		if (this.programMap == null || this.programMap.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act
					.getBean("studentMarksDAO");
			this.programMap = dao.getProgramMap();
		}
		return programMap;
	}
	
	public ArrayList<PassFailExamBean> getLateralPasslist(StudentExamBean student, ArrayList<StudentExamBean> lateralStudentList, HashMap<String,ArrayList<String>> semWiseSubjectMap, PassFailDAO passFailDao ){
		ArrayList<PassFailExamBean> lateralPassList = new ArrayList<PassFailExamBean>();
		for(StudentExamBean lateral : lateralStudentList) {
			ArrayList<PassFailExamBean> previousPassList = passFailDao.getPassRecordsForStudentSelfTranscript(lateral.getSapid());
			for(String key:semWiseSubjectMap.keySet()) {
				for(String subject :semWiseSubjectMap.get(key)) {
					if(student.getWaivedOffSubjects().contains(subject)) {  
						//if previous pass list contain WaivedOff subjects then add in passlist 
						for(PassFailExamBean prev:previousPassList) { 
							if(subject.contains(prev.getSubject())
								|| (subject.contains("Decision Science") && prev.getSubject().equalsIgnoreCase("Business Statistics"))
								|| (subject.contains("Business Communication") && prev.getSubject().equalsIgnoreCase("Business Communication and Etiquette"))
								) {
								prev.setSem(key); //update previous subject sem to current subject sem
								lateralPassList.add(prev);
							}
						} 
					}
				}
			}
		}
		return lateralPassList;
	}
	
	/**
	 * Generate waived-off subjects PassFailBeans list.
	 * @param stdExmBean - Contains student basic details like sapId,program,semester, lateral etc.. 
	 * @return List - Return list of PassFailExamBean having waived-off subjects.
	 * @throws Exception If any exception occurs while processing waived-off subjects pass fail logic.
	 */
	public List<PassFailExamBean> getWaivedOffSubjectsToAddonMarksheet(StudentExamBean stdExmBean) throws Exception{
		ArrayList<PassFailExamBean> passFailStudentList = new ArrayList<>();

		// Pass StudentExamBean to get waived-off subjects.
		studentService.mgetWaivedOffSubjects(stdExmBean);
		
		
		//Remove 'Project' and 'Module 4 - Project' from the waived-Off subjects list.
		stdExmBean.getWaivedOffSubjects().remove("Project"); 
		stdExmBean.getWaivedOffSubjects().remove("Module 4 - Project");

		// If student is of Jul2019 structure and in his waived-off subjects contains
		// 'Business Statistics' then add 'Decision Science' subject as well.
		if ("Jul2019".equalsIgnoreCase(stdExmBean.getPrgmStructApplicable())
				&& stdExmBean.getWaivedOffSubjects().contains("Business Statistics")) {
			stdExmBean.getWaivedOffSubjects().add("Decision Science");
		}

		//Get Semester wise subjects map.
		HashMap<String, ArrayList<String>> semWiseSubjectMap = passFailDAO.getSemWiseSubjectsMap(stdExmBean.getProgram(),
				stdExmBean.getPrgmStructApplicable());
		
		if (stdExmBean.getIsLateral().equalsIgnoreCase("Y")) {
			//Get subjects from the semester subjects map for the semester which student has applied mark sheet.   
			for (String subject : semWiseSubjectMap.get(stdExmBean.getSem())) {
				// If semester wise subject contains in the waived-off subjects list then only prepare passFailBean and add to list. 
				if (stdExmBean.getWaivedOffSubjects().contains(subject)) {
					//Create PassFailBean object 
					PassFailExamBean passFailBean = new PassFailExamBean();
					
					//Prepare passFailBean.
					passFailBean.setWaivedOff(true);
					passFailBean.setSapid(stdExmBean.getSapid());
					passFailBean.setSubject(subject);
					passFailBean.setSem(stdExmBean.getSem());
					passFailBean.setProgram(stdExmBean.getProgram());
					passFailBean.setOldProgram(stdExmBean.getOldProgram());
					passFailBean.setName(stdExmBean.getFirstName() + " " + stdExmBean.getLastName());
					
					//Marked score as empty because we are not showing any score on mark sheet for waived-off subjects.
					//Instead of marks we are showing '##'
					passFailBean.setWrittenscore("");
					passFailBean.setAssignmentscore("");
					passFailBean.setWrittenYear("");
					passFailBean.setWrittenMonth("");
					passFailBean.setAssignmentYear("");
					passFailBean.setAssignmentMonth("");
					passFailBean.setTotal("");
					
					//Add passFailBean to list.
					passFailStudentList.add(passFailBean);
				} // inner if
			} // for
		} // outer if
		
		if(checkLateralStudentFromPGToMBAProgram(stdExmBean.getIsLateral(), stdExmBean.getProgram())) {
			//passFailStudentList  = (removeLateralDuplicateSubjectsPG_MBAPolicy(passFailStudentList,stdExmBean.getProgram(),semWiseSubjectMap));
			passFailStudentList = 	 PassFailSubjectHelper.removeLateralDuplicateSubjectsPG_MBAPolicy_updated(passFailStudentList);
		}
		
	//return waived-off subjects pass fail beans as a list. 
	return passFailStudentList;	
	
	}// getWaivedOffSubjectsToAddonMarksheet(-)
	
	public ArrayList<PassFailExamBean> removeLateralDuplicateSubjectsPG_MBAPolicy(final ArrayList<PassFailExamBean> passList,
			final String currentProgram, final HashMap<String, ArrayList<String>> semWiseMBASubjectMap) {

		return (ArrayList<PassFailExamBean>) passList.stream()
				.filter(e -> !(semWiseMBASubjectMap.entrySet().stream()
						.filter(f -> f.getKey().equals("3") || f.getKey().equals("4")).map(Map.Entry::getValue)
						.collect(Collectors.toList()).stream().flatMap(List::stream).collect(Collectors.toList())
						.contains(e.getSubject()) && !e.getOldProgram().equals(currentProgram)
						
						))
				.collect(Collectors.toList());
	}

	public boolean checkLateralStudentFromPGToMBAProgram(final String isLateral, final String currentProgram) {
		/* && old_program.startsWith("PG") - Should have old program check but DB has incorrect values where in old program and new Program are same */
		
		if (isLateral.equalsIgnoreCase("Y") && currentProgram.startsWith("MBA") ) {
			return true;
		}
		return false;
	}
	
	/**
	 * Generate the marksheet's for the non-graded certificate students based on the service request id's.
	 * @param embaPassFailBean having the service request id's.
	 * @return EmbaMarksheetBean having the generated marksheet's file path.
	 * @throws NoRecordFoundException If student service request details not found based on the service request id's.
	 * @throws SQLException If any exceptions raises while executing the SQL queries.
	 * @throws Exception If any exceptions raises while generating the marksheet's PDF file. 
	 */
	public EmbaMarksheetBean generateNonGradedMarksheet(EmbaPassFailBean embaPassFailBean) throws NoRecordFoundException, SQLException, Exception {
		logger.info("MarksheetService.generateNonGradedMarksheet() - START");
		List<EmbaMarksheetBean> marksheetList = new ArrayList<>();
		EmbaMarksheetBean marksheet = new EmbaMarksheetBean();
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();
		List<EmbaPassFailBean> passFailDataListAllSapidsAllSems = new ArrayList<EmbaPassFailBean>();
		CreatePDF helper = new CreatePDF();
		List<String> sapidList = new ArrayList<>();
		
		if(embaPassFailBean==null || embaPassFailBean.getServiceRequestIdList()==null || "".equalsIgnoreCase(embaPassFailBean.getServiceRequestIdList()))
			throw new NullPointerException("No service request id's found for marksheet generation, please enter service request id's.");
		
		//Generate list of service request id's based on the serviceRequestIds string having carriage return or new line as a separator.
		List<Integer> serviceRequestIdList = Stream.of(StringUtility
				.generateCommaSeparatedList(embaPassFailBean.getServiceRequestIdList()).split(",", -1))
				.map(s -> Integer.parseInt(s)).collect(Collectors.toList());
		
		logger.info("Service Request Ids List:"+serviceRequestIdList);

		//Get students and service request details based on the service request id's.
		List<EmbaMarksheetBean> studentForSRList = examsAssessmentsDAO.getStudentsServiceRequestDetails(serviceRequestIdList, NON_GRADED_MASTER_KEY_LIST);
		
		if(studentForSRList==null || studentForSRList.isEmpty())
			throw new NoRecordFoundException("No student records found for given service request ids.");
		
		//Get result declaration date's map 
		Map<String,ExamResultsBean> resultDatesMap = this.getNonGradedProgramsResultDatesMap();
				
		//Process the studentSR details list and prepare the resultant marksheetList.
		for (EmbaMarksheetBean studentData : studentForSRList) {
			sapidList.add(studentData.getSapid()); 
			embaPassFailBean.setSapid(studentData.getSapid());
			
			marksheet = this.prepareMarksheetBean(studentData,getCentersMap());
			
		 	this.setResultDeclarationDate(resultDatesMap,marksheet);
		 	
			marksheetList.add(marksheet);
		}
		
		logger.info("comma separated student sapid's:"+sapidList);
		
		//Get the students pass fail status based on the sapId's. 
		passFailDataListAllSapidsAllSems=examsAssessmentsDAO.getNonGradedEmbaPassFailByAllSapids(sapidList);

		//Generate marksheet's
		marksheetBean= helper.generateNonGradedMarksheetPDF(marksheetList, this.getProgramMap(),
				this.getCentersMap(),MARKSHEETS_PATH, passFailDataListAllSapidsAllSems,embaPassFailBean.getLogoRequired());

		marksheetBean.setSuccess(true);

		logger.info("Marksheet's generated file path:"+marksheetBean.getFileName());
		logger.info("MarksheetService.generateNonGradedMarksheet() - END");
		
		//return mark sheet bean having the generated marksheet's file path.
		return marksheetBean;
	}//generateNonGradedMarksheet(-)


	private void setResultDeclarationDate(Map<String,ExamResultsBean> resultDatesMap, EmbaMarksheetBean marksheet) {
		ExamResultsBean resultBean = resultDatesMap
				.get(new StringBuilder(marksheet.getMonth()).append(marksheet.getYear()).append("-")
						.append(marksheet.getExamMonth()).append(marksheet.getExamYear()).toString());
			 	
			 	String resultDate="";
			 	try {
			 	if(resultBean != null) 
			 		resultDate= new SimpleDateFormat("dd-MMM-yyyy").format(new SimpleDateFormat("yyyy-MM-dd").parse(resultBean.getResultDeclareDate()));
			 	}catch (Exception e) {
					logger.error("Error occured in parsing of declaration date for sapid '"+marksheet.getSapid()+"'. Error Message:"+e.getMessage());
				}
				
				marksheet.setResultDeclarationDate(resultDate);
	}//setResultDeclarationDate(-,-)
	
}

