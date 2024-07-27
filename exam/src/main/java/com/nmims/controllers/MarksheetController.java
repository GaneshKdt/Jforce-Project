package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.DocumentException;
import com.nmims.beans.CenterExamBean;
import com.nmims.beans.EmbaMarksheetBean;
import com.nmims.beans.EmbaPassFailBean;
import com.nmims.beans.ExamOrderExamBean;
import com.nmims.beans.ExecutiveExamOrderBean;
import com.nmims.beans.MarksheetBean;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.ProgramExamBean;
import com.nmims.beans.Specialisation;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.ExamsAssessmentsDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.SpecialisationDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.daos.TestDAOForRedis;
import com.nmims.helpers.CertificatePDFCreator;
import com.nmims.helpers.CreatePDF;
import com.nmims.helpers.DateHelper;
import com.nmims.helpers.ResultsFromRedisHelper;
import com.nmims.interfaces.DissertationGradesheet_TranscriptService;
import com.nmims.services.MarksheetService;
import com.nmims.services.ModeOfDeliveryService;
import com.nmims.services.SpecialisationService;

/**
 * Handles requests for the application home page.
 */
@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MarksheetController extends BaseController {

	@Autowired(required = false)
	ApplicationContext act;
	
	@Autowired
	MarksheetService msService;
	
	@Autowired
	SpecialisationService specialisationService;
	
	@Autowired
	DissertationGradesheet_TranscriptService gradSheetService;
	
	@Autowired
	ModeOfDeliveryService modService;
	
	@Autowired
	CertificatePDFCreator pdfCreators;
	
	@Value("${MARKSHEETS_PATH}")
	private String MARKSHEETS_PATH;

	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${STUDENT_PHOTOS_PATH}") // added to get student photo to certificate
	private String STUDENT_PHOTOS_PATH;
	
//Added for SAS	
	@Value("#{'${ACAD_YEAR_SAS_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_SAS_LIST ;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST ;
	
	@Value("#{'${PDDM_PROGRAMS_LIST}'.split(',')}")
	private List<String> PDDM_PROGRAMS_LIST ;
	
	@Value("#{'${SAS_EXAM_MONTH_LIST}'.split(',')}")
	private List<String> SAS_EXAM_MONTH_LIST ;
	
	@Value("${CERTIFICATES_PATH}")
	private String CERTIFICATES_PATH;

	private final int pageSize = 20;
	
	private final static int IA_MASTER_DISSERTATION_Q7_SEM = 7;
	
	private final static int IA_MASTER_DISSERTATION_Q8_SEM = 8;
	
	private final static int IA_MASTER_DISSERTATION_MASTER_KEY = 131;
	
	private final static String MSCAIML = "M.Sc. (AI & ML Ops)";
	
	private static final Logger finalCertificateLogger = LoggerFactory.getLogger("finalCertificate");

	private static final Logger logger = LoggerFactory
			.getLogger(MarksheetController.class);
	private ArrayList<String> programList = null;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}") 
	private List<String> yearList; 
	
	private ArrayList<String> graceYearList = new ArrayList<String>(
			Arrays.asList("2013", "2014", "2015", "2016", "2017"));

	private ArrayList<String> subjectList = null;
	private HashMap<String, String> programCodeNameMap = null;
	private HashMap<String, ProgramExamBean> programMap = null;

	private ArrayList<CenterExamBean> centers = null;
	private HashMap<String, CenterExamBean> centersMap = null;
	
	private ArrayList<String> applicableSubjectList = null;
	
	private Map<String, ProgramExamBean> modProgramMap = null;
	
	private String examBookingErrorURLWeb = SERVER_PATH + "timeline/sRPaymentFailure";
//	private String examBookingFailURLWeb = "timeline/examBookingError";
	private String examBookingSuccessURLWeb = SERVER_PATH + "timeline/sRPaymentSuccess";
	
	private String examBookingErrorURLMobile = "embaPaymentError";
	private String examBookingFailURLMobile = "embaExamBookingPaymentFailure";
	private String examBookingSuccessURLMobile = "embaExamBookingPaymentSuccess";
	
	private static final List<String> marksheetPreview_REDIS = new ArrayList<String>(Arrays.asList(":"));  

	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
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
	
	public ArrayList<CenterExamBean> getCentersList() {
		// if(this.centers == null){
		StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		this.centers = dao.getAllCenters();
		// }
		return centers;
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
	
	public ArrayList<String> getAllApplicableSubjects(String program,String prgmStructApp){
		
		if (this.applicableSubjectList == null || this.applicableSubjectList.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			this.applicableSubjectList = dao.getAllApplicableSubjects(program,prgmStructApp);
		}
		return applicableSubjectList;
	}

	private void getStudentMarksHistory(HttpServletRequest request,
			HttpServletResponse response) {
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute(
				"studentExam");
		StudentMarksBean bean = new StudentMarksBean();
		StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		bean.setSapid(student.getSapid());
		List<StudentMarksBean> studentMarksListForMarksHistory = null;
		if ("Online".equals(student.getExamMode())) {
			boolean readFromCache = Boolean.FALSE;
			try {
				readFromCache = this.fetchRedisHelper().readFromCache();
				if (readFromCache) {
					studentMarksListForMarksHistory = this.fetchOnlyMarksHistory(student.getSapid());
					request.setAttribute("resultSource1","REDIS");//REDIS is :, indicates from where marksheetHistory displayed on jsp page.
				}
			} catch (Exception ex) {
				logger.error("MarksheetController: getStudentMarksHistory : error : "+ ex.getMessage());

				//if REDIS stopped - exception caught - page loading continued -Vilpesh on 2021-11-19  
				readFromCache = Boolean.FALSE;
			} finally {
				if (!readFromCache) {
					studentMarksListForMarksHistory = dao.getAStudentsMarksForOnline(bean);
					request.setAttribute("resultSource1","DB");//DB is space,indicates from where marksheetHistory displayed on jsp page
				}
			}
		} else {
			studentMarksListForMarksHistory = dao
					.getAStudentsMarksForOffline(bean);
			request.setAttribute("resultSource1","DB");//DB is space,indicates from where marksheetHistory displayed on jsp page
		}

		request.setAttribute("studentMarksListForMarksHistory",
				studentMarksListForMarksHistory);
	}

	private void getStudentMarksHistory(final Map<String, Object> simpleMap, HttpServletRequest request,
			HttpServletResponse response) {
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute(
				"studentExam");
		StudentMarksBean bean = new StudentMarksBean();
		StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		bean.setSapid(student.getSapid());
		List<StudentMarksBean> studentMarksListForMarksHistory = null;
		if ("Online".equals(student.getExamMode())) {
			boolean dbFetch = Boolean.FALSE;
			try {
				if(null != simpleMap) {
					studentMarksListForMarksHistory = this.fetchRedisHelper().fetchOnlyMarksHistory(simpleMap, ResultsFromRedisHelper.EXAM_STAGE_TEE, student.getSapid());
					request.setAttribute("resultSource1","REDIS");//REDIS is :, indicates from where marksheetHistory displayed on jsp page.
				} else {
					dbFetch = Boolean.TRUE;
				}
			} catch (Exception ex) {
				logger.error("MarksheetController: getStudentMarksHistory : error : "+ ex.getMessage());
				dbFetch = Boolean.TRUE;
			} finally {
				if (dbFetch) {
					studentMarksListForMarksHistory = dao.getAStudentsMarksForOnline(bean);
					request.setAttribute("resultSource1","DB");//DB is space,indicates from where marksheetHistory displayed on jsp page
				}
			}
		} else {
			studentMarksListForMarksHistory = dao
					.getAStudentsMarksForOffline(bean);
			request.setAttribute("resultSource1","DB");//DB is space,indicates from where marksheetHistory displayed on jsp page
		}

		request.setAttribute("studentMarksListForMarksHistory",
				studentMarksListForMarksHistory);
	}
	
	protected ResultsFromRedisHelper fetchRedisHelper() {
		ResultsFromRedisHelper resultsFromRedisHelper = null;
		resultsFromRedisHelper = (ResultsFromRedisHelper) act.getBean("resultsFromRedisHelper"); 
		return resultsFromRedisHelper;
	}
	
	public List<StudentMarksBean> fetchOnlyMarksHistory(String sapId) {
		List<StudentMarksBean> listStudentMarksBean2 = null;
		Map<String, Object> destinationMap = null;
		
		logger.info("MarksheetController : fetchOnlyMarksHistory : sapId : "+sapId);
		destinationMap = this.fetchRedisHelper().fetchOnlyMarksHistory(ResultsFromRedisHelper.EXAM_STAGE_TEE,
				sapId);
		
		if (null != destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY)
				&& destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY) instanceof List) {
			listStudentMarksBean2 = (List<StudentMarksBean>) destinationMap.get(ResultsFromRedisHelper.KEY_STUDENT_MARKS_HISTORY);
			logger.info("MarksheetController : fetchOnlyMarksHistory : Size of MarksHistory : "+ listStudentMarksBean2.size());
		}
		
		return listStudentMarksBean2;
	}
	
	/**
	 * Map with Value containing Passfail data list. Contains ONLY 1 key-value pair.
	 * @param sapId
	 * @return
	 */
	/*@Deprecated
	protected Map<String, Object> fetchOnlyPassfail(final String sapId) {
		Map<String, Object> simpleMap = null;
		simpleMap = this.fetchRedisHelper().fetchOnlyPassfail(ResultsFromRedisHelper.EXAM_STAGE_TEE, sapId);
		return simpleMap;
	}*/
	
	/**
	 * Map with Value containing Passfail data list. Contains ONLY 1 key-value pair.
	 * @param simpleMap Map with result details
	 * @param sapId
	 * @return
	 */
	protected Map<String, Object> fetchOnlyPassfail(final Map<String, Object> simpleMap, final String sapId) {
		Map<String, Object> tempMap = null;
		tempMap = this.fetchRedisHelper().fetchOnlyPassfail(simpleMap, ResultsFromRedisHelper.EXAM_STAGE_TEE, sapId);
		return tempMap;
	}
	
	protected String fetchLastAttemptYearMonthForSem(final String sem, final String sapId, Map<String, Object> dataMap) {
		String temp_highestYearMonth = null;
		String highestYearMonth = null;
		Integer subjPassed = null;
		Map<Integer, Set<String>> yearMonthMap = null;
		Set<String> yearMonthSet = null;
		Set<Integer> keySet = null;
		//logger.info("MarksheetController : fetchLastAttemptYearMonthForSem : (Sem, SapId) : (" + sem + ", " + sapId + ")");
		yearMonthMap = this.fetchRedisHelper().fetchYearMonthFromPassfailForSemBoth(sem, dataMap);

		if (null != yearMonthMap) {
			keySet = yearMonthMap.keySet();
			for (Integer key : keySet) {
				subjPassed = key;
				yearMonthSet = yearMonthMap.get(subjPassed);
				if (null != yearMonthSet && !yearMonthSet.isEmpty()) {
					temp_highestYearMonth = DateHelper.findHighestYearMonth(yearMonthSet,
							DateHelper.FORMAT_YEAR_DASH_MONTH);// yyyy-MMM
				} else if (null != yearMonthSet && yearMonthSet.isEmpty()) {
					logger.info("MarksheetController : fetchLastAttemptYearMonthForSem : (no subjects found passed for) Sem : " + sem);
				}
			}

			if (null != temp_highestYearMonth) {
				highestYearMonth = temp_highestYearMonth.substring(0, 4);// extract 2020 from 2020-Dec
				highestYearMonth = highestYearMonth + temp_highestYearMonth.substring(5);// extract Dec from 2020-Dec
			}
		}
		return highestYearMonth;
	}
	
	public List<PassFailExamBean> searchPassfail(final Map<String, Object> simpleMap, final String sem,
			final String sapId, final String examMonth, final String examYear) {
		String examYearExamMonth = null;
		String lastAttemptYearMonth = null;
		Map<String, Object> dataMap = null;
		List<PassFailExamBean> list = null;
		List<PassFailExamBean> pFList = null;
		//boolean readFrom = Boolean.FALSE;
		try {
			//readFrom = this.fetchRedisHelper().readFromCache();
			if (null != simpleMap) {
				dataMap = this.fetchOnlyPassfail(simpleMap, sapId);
				if (null != dataMap && dataMap.size() == 1) {
					Object val = null;
					Set<String> keySet = dataMap.keySet();
					for (String key : keySet) {
						val = dataMap.get(key);//remove the passfail list
						if (null != val) {
							if (val instanceof List && !((List) val).isEmpty()) {
								//Find the last attempt Year month for a Semester, from Assgnment and/or Exam Year month.
								lastAttemptYearMonth = this.fetchLastAttemptYearMonthForSem(sem, sapId, dataMap);
								examYearExamMonth = examYear + examMonth;
								logger.info(
										"searchPassfail : Comparing in Sem for (Sem, ExamYearExamMonth, lastAttemptYearMonth) : ("
												+ sem + "," + examYearExamMonth + "," + lastAttemptYearMonth + ")");

								if (null != lastAttemptYearMonth && lastAttemptYearMonth.equals(examYearExamMonth)) {
									pFList = (List) val;
									if (null != pFList && !pFList.isEmpty()) {
										list = new ArrayList<PassFailExamBean>();
										for (PassFailExamBean bean : pFList) {

											if (null != bean.getSem() && null != bean.getSapid()
													&& (sem.equalsIgnoreCase(bean.getSem())
															&& sapId.equalsIgnoreCase(bean.getSapid()))) {
												list.add(bean);
											}
										}
										pFList = list;
										logger.info("searchPassfail : Found(REDIS) Passfail (SapId, Sem, Subjects) ("
												+ sapId + "," + sem + "," + pFList.size() + ")");
									} else if (null == pFList || (null != pFList && pFList.isEmpty())) {
										pFList = null;
										logger.info("searchPassfail : Empty/Absent(REDIS) Passfail (SapId, Sem) (" + sapId
												+ "," + sem + ")");
									}
								} else {
									logger.info(
											"searchPassfail : Wrong Sem/Wrong ExamYearExamMonth in REDIS, db fetch done (SapId, Sem) ("
													+ sapId + "," + sem + ")");
								}
								examYearExamMonth = null;
							} else if (val instanceof List && ((List) val).isEmpty()) {
								logger.info("searchPassfail : Empty in REDIS, db fetch done (SapId, Sem) (" + sapId + ","
										+ sem + ")");
							}
						} else {
							logger.info(
									"searchPassfail : Not in REDIS, db fetch done (SapId, Sem) (" + sapId + "," + sem + ")");
						}
					}
				} else {
					logger.info("searchPassfail : Wrong way Data stored in REDIS, db fetch (SapId, Sem) (" + sapId + ","
							+ sem + ")");
				}
			} else {
				logger.info("searchPassfail : db fetch continued, (SapId, Sem) (" + sapId + "," + sem + ")");
			}
		} catch (Exception ex) {
			pFList = null;
			logger.error("MarksheetController: searchPassfail : db fetch done as getting error : " + ex.getMessage());
		}
		return pFList;
	}
	
	// getStudentMarksHistoryForExecutive Start
	private void getStudentMarksHistoryForExecutive(HttpServletRequest request,
			HttpServletResponse response) {
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute(
				"studentExam");
		StudentMarksBean bean = new StudentMarksBean();
		StudentMarksDAO dao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		bean.setSapid(student.getSapid());
		List<StudentMarksBean> studentMarksListForMarksHistory = null;
		studentMarksListForMarksHistory = dao.getAStudentsMarksForExecutive(bean);
		

		request.setAttribute("studentMarksListForMarksHistory",
				studentMarksListForMarksHistory);
	}
	//getStudentMarksHistoryForExecutive End

	public HashMap<String, String> getProgramMap() {
		if (this.programCodeNameMap == null
				|| this.programCodeNameMap.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act
					.getBean("studentMarksDAO");
			this.programCodeNameMap = dao.getProgramDetails();
		}
		return programCodeNameMap;
	}

	public HashMap<String, ProgramExamBean> getProgramAllDetailsMap() {
		if (this.programMap == null || this.programMap.size() == 0) {
			StudentMarksDAO dao = (StudentMarksDAO) act
					.getBean("studentMarksDAO");
			this.programMap = dao.getProgramMap();
		}
		return programMap;
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
	//Added to get mapped Mode of Learning by program code - by shivam.pandey.EXT 
	public Map<String, ProgramExamBean> getModProgramMap()throws Exception {
		this.modProgramMap = modService.getModProgramMap();
		return modProgramMap;
	}

	@RequestMapping(value = "/admin/marksheetForm", method = { RequestMethod.GET,
			RequestMethod.POST })
	public String marksheetForm(HttpServletRequest request,
			HttpServletResponse response, Model m) {
		logger.info("Add New Company Page");

		PassFailExamBean bean = new PassFailExamBean();
		m.addAttribute("studentMarks", bean);
		m.addAttribute("programList", getProgramList());
		m.addAttribute("yearList", yearList);
		m.addAttribute("subjectList", getSubjectList());
		request.getSession().setAttribute("passFailSearchBean", bean);

		return "marksheet";
	}

	@RequestMapping(value = "/admin/singleStudentMarksheetForm", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String singleStudentMarksheetForm(HttpServletRequest request,
			HttpServletResponse response, Model m) {
		logger.info("Add New Company Page");

		PassFailExamBean bean = new PassFailExamBean();
		m.addAttribute("studentMarks", bean);
		m.addAttribute("yearList", yearList);
		request.getSession().setAttribute("passFailSearchBean", bean);

		return "singleStudentMarksheet";
	}

	@RequestMapping(value = "/admin/download", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void download(HttpServletRequest request,
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

	@RequestMapping(value = "/admin/getMarksheet", method = RequestMethod.POST)
	public ModelAndView getMarksheet(HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute PassFailExamBean studentMarks) {
		ModelAndView modelnView = new ModelAndView("marksheet");
		request.getSession().setAttribute("passFailSearchBean", studentMarks);

		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		// Page<PassFailBean> page = dao.getPassFailPage(1, pageSize,
		// studentMarks);
		List<PassFailExamBean> studentMarksList = dao
				.getRecordsForMarksheet(studentMarks);

		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());

		if (studentMarksList == null || studentMarksList.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}

		HashMap<String, MarksheetBean> studentMap = dao
				.getStudentsData(studentMarks);

		ArrayList<String> errorList = checkIfStudentsPresentInMasterDB(
				studentMarksList, studentMap);
		if (errorList.size() > 0) {

			// Converting ArrayList to HashSet to remove duplicates
			HashSet<String> listToSet = new HashSet<String>(errorList);

			// Creating Arraylist without duplicate values
			List<String> listWithoutDuplicates = new ArrayList<String>(
					listToSet);

			request.setAttribute("error", "true");
			request.setAttribute(
					"errorMessage",
					"SAPIDs given here not found in Students Master Database. Please contact academic team to add these."
							+ " Without students master data marksheet cannot be generated. "
							+ listWithoutDuplicates);
			return modelnView;
		}

		String resultDeclarationDate = "";
		if ("Online".equalsIgnoreCase(studentMarks.getExamMode())) {
			resultDeclarationDate = dao.getOnlineExamDeclarationDate(
					studentMarks.getWrittenMonth(),
					studentMarks.getWrittenYear());
		} else {
			resultDeclarationDate = dao.getOfflineExamDeclarationDate(
					studentMarks.getWrittenMonth(),
					studentMarks.getWrittenYear());
		}
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
		String examMonth = studentMarks.getWrittenMonth();
		String examYear = studentMarks.getWrittenYear();

		CreatePDF helper = new CreatePDF();
		List<MarksheetBean> marksheetList = helper.generateMarksheetList(
				studentMarksList, studentMap, examMonth, examYear,
				examOrderMap, resultDeclarationDate, getCentersMap());
		Collections.sort(marksheetList);
		try {
			helper.createPDF(marksheetList, resultDeclarationDate, request,
					getProgramMap(), getCentersMap(), MARKSHEETS_PATH, getModProgramMap());
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Marksheet generated successfully. Please click Download link below to get file.");

		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			logger.error("Error in generating marksheet of getMarksheet: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error in generating marksheet of getMarksheet: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		}

		return modelnView;
	}

	@RequestMapping(value = "/admin/getSingleStudentMarksheet", method = RequestMethod.POST)
	public ModelAndView getSingleStudentMarksheet(HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute PassFailExamBean studentMarks) throws SQLException {
		ModelAndView modelnView = new ModelAndView("singleStudentMarksheet");
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());

		request.getSession().setAttribute("passFailSearchBean", studentMarks);

		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");

		// Check if result is live
		MarksheetBean studentData = dao.getSingleStudentsData(studentMarks);
		Boolean isProjectApplicable =  sDao.isProjectApplicable(studentData.getProgram(),studentMarks.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
		Boolean isModuleProjectApplicable =  sDao.isModuleProjectApplicable(studentData.getProgram(),studentMarks.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
		Boolean isMimicProApplicable =  sDao.isMimicProApplicable(studentData.getProgram(),studentMarks.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());

		// ArrayList<String> errorList =
		// checkIfStudentsPresentInMasterDB(studentMarksList, studentMap);
		if (studentData == null) {
			request.setAttribute("error", "true");
			request.setAttribute(
					"errorMessage",
					"Student not found in Students Master Database. Please contact academic team to add these."
							+ " Without students master data marksheet cannot be generated. ");
			return modelnView;
		}

		String programStructure = studentData.getPrgmStructApplicable();
		String resultDeclarationDate = "";
		ExamOrderExamBean exam = dao.getExamDetails(studentMarks.getWrittenMonth(),
				studentMarks.getWrittenYear());
		String resultLive = "N";

		if ("Online".equals(studentData.getExamMode())) {
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
		studentData.setResultDeclarationDate(resultDeclarationDate);
		// Generate marksheet records
		HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudent(
				studentMarks.getSem(), studentMarks.getSapid(),
				studentMarks.getWrittenMonth(), studentMarks.getWrittenYear());
		if (keysMap == null || keysMap.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}
		String sapId = studentData.getSapid().trim();
		ArrayList<String> softSkillSubjects= new ArrayList<String>(Arrays.asList(sapId+"Soft Skills for Managers",sapId+"Employability Skills - II Tally",sapId+"Start your Start up",sapId+"Design Thinking"));
		
		for(String subject : softSkillSubjects) {
			 if(keysMap.containsKey(subject)) {
				 keysMap.remove(subject);
			 }
		}
		// ArrayList<PassFailBean> passFailStudentList =
		// dao.processNew(keysMap);
		
		
		ArrayList<PassFailExamBean> passFailStudentList = null;

// 		if("ACBM".equalsIgnoreCase(studentData.getProgram())){
		boolean isBajaj = "Bajaj".equalsIgnoreCase(studentData.getConsumerType());
		boolean isDBMJul2014 = "Jul2014".equalsIgnoreCase(studentData.getPrgmStructApplicable()) && "DBM".equalsIgnoreCase(studentData.getProgram());
		if(isBajaj && !isDBMJul2014){
//			passFailStudentList = dao.processACBM(keysMap);
			passFailStudentList = dao.processBajaj(keysMap);
		} else {
			passFailStudentList = dao.processNew(keysMap);// Use new pass fail
															// logic
		}
		
	
		
		List<PassFailExamBean> studentMarksList = new ArrayList<>();
		// Take only those records where he has written or assignment year/month
		// is same one as selected on form.
		if (passFailStudentList != null && !passFailStudentList.isEmpty()) {
			if(isProjectApplicable.equals(true)) {
			  //if("4".equalsIgnoreCase(studentMarks.getSem())) {
			  if(!keysMap.containsKey(studentMarks.getSapid().trim()+"Project")) {
			  PassFailExamBean passFailBean = new PassFailExamBean();
			  passFailBean.setSapid(studentMarks.getSapid());
			  passFailBean.setSubject("Project"); passFailBean.setSem(studentMarks.getSem());
			  passFailBean.setProgram(studentData.getProgram());
			  passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
			  passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
			  passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
			  passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
			  passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
			  }
			}
			if(isModuleProjectApplicable.equals(true)) {
				//if("4".equalsIgnoreCase(studentMarks.getSem())) {
				if(!keysMap.containsKey(studentMarks.getSapid().trim()+"Module 4 - Project")) {
					PassFailExamBean passFailBean = new PassFailExamBean();
					passFailBean.setSapid(studentMarks.getSapid());
					passFailBean.setSubject("Module 4 - Project"); passFailBean.setSem(studentMarks.getSem());
					passFailBean.setProgram(studentData.getProgram());
					passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
					passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
					passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
					passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
					passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
				}
				  
			}
			
			if(isMimicProApplicable.equals(true)) {
				//if("4".equalsIgnoreCase(studentMarks.getSem())) {
				if(!keysMap.containsKey(studentMarks.getSapid().trim()+"Simulation: Mimic Pro")) {
					PassFailExamBean passFailBean = new PassFailExamBean();
					passFailBean.setSapid(studentMarks.getSapid());
					passFailBean.setSubject("Simulation: Mimic Pro"); 
					passFailBean.setSem(studentMarks.getSem());
					passFailBean.setProgram(studentData.getProgram());
					passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
					passFailBean.setWrittenscore(""); 
					passFailBean.setAssignmentscore("");
					passFailBean.setWrittenYear(""); 
					passFailBean.setWrittenMonth("");
					passFailBean.setAssignmentYear(""); 
					passFailBean.setAssignmentMonth("");
					passFailBean.setTotal("--"); 
					passFailStudentList.add(passFailBean); 
				}

				if(!keysMap.containsKey(studentMarks.getSapid().trim()+"Simulation: Mimic Social")) {
					PassFailExamBean passFailBean = new PassFailExamBean();
					passFailBean.setSapid(studentMarks.getSapid());
					passFailBean.setSubject("Simulation: Mimic Social"); 
					passFailBean.setSem(studentMarks.getSem());
					passFailBean.setProgram(studentData.getProgram());
					passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
					passFailBean.setWrittenscore(""); 
					passFailBean.setAssignmentscore("");
					passFailBean.setWrittenYear(""); 
					passFailBean.setWrittenMonth("");
					passFailBean.setAssignmentYear(""); 
					passFailBean.setAssignmentMonth("");
					passFailBean.setTotal("--"); 
					passFailStudentList.add(passFailBean); 
				}
			}
			studentMarksList.addAll(passFailStudentList);
		}


		HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
		String examMonth = studentMarks.getWrittenMonth();
		String examYear = studentMarks.getWrittenYear();

		CreatePDF helper = new CreatePDF();
		helper.generateSingleStudentMarksheet(studentMarksList, studentData,
				examMonth, examYear, examOrderMap, getCentersMap());
		ArrayList<MarksheetBean> marksheetList = new ArrayList<>();
		marksheetList.add(studentData);

		try {
			helper.createPDF(marksheetList, resultDeclarationDate, request,
					getProgramMap(), getCentersMap(), MARKSHEETS_PATH, getModProgramMap());
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Marksheet generated successfully. Please click Download link below to get file.");

		} catch (DocumentException | IOException e) {
			logger.error("Error in generating marksheet of getSingleStudentMarksheet: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error in generating marksheet of getSingleStudentMarksheet: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		}

		return modelnView;
	}

	private ArrayList<String> checkIfStudentsPresentInMasterDB(
			List<PassFailExamBean> studentMarksList,
			HashMap<String, MarksheetBean> studentMap) {
		ArrayList<String> errorList = new ArrayList<>();
		for (int i = 0; i < studentMarksList.size(); i++) {
			String sapId = studentMarksList.get(i).getSapid().trim();
			if ((!studentMap.containsKey(sapId))
					&& (!"Not Available".equals(sapId))) {
				errorList.add(sapId);
			}

		}
		return errorList;
	}

	@RequestMapping(value = "/admin/singleStudentCertificateForm", method = {
			RequestMethod.GET, RequestMethod.POST })
	public ModelAndView singleStudentCertificateForm(
			@ModelAttribute PassFailExamBean passFailBean,
			HttpServletRequest request, HttpServletResponse response, Model m) {
		ModelAndView modelnView = new ModelAndView("singleStudentCertificate");

		m.addAttribute("bean", passFailBean);
		return modelnView;
	}

	@RequestMapping(value = "/admin/getSingleStudentCertificate", method = {
			RequestMethod.GET, RequestMethod.POST })
	public ModelAndView getSingleStudentCertificate(
			@ModelAttribute PassFailExamBean passFailBean,
			HttpServletRequest request, HttpServletResponse response, Model m) throws Exception {
		ModelAndView modelnView = new ModelAndView("singleStudentCertificate");
		CertificatePDFCreator pdfCreator = new CertificatePDFCreator();
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		MarksheetBean marksheetBean = dao.getSingleStudentForSR(passFailBean);
		String declareDate = dao
				.getResultDeclareDateForSingleStudent(marksheetBean);
		try {
			String certificateNumberGenerated = pdfCreators
					.generateCertificateAndReturnCertificateNumberForSingleStudent(
							marksheetBean, declareDate, request,
							getProgramAllDetailsMap(), getCentersMap(),
							CERTIFICATES_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH);
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Certificate generated successfully. Please click Download link below to get file.");
			dao.updateSRWithCertificateNumberAndCurrentDateForSingleStudent(
					passFailBean, certificateNumberGenerated);

		} catch (DocumentException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating certificate." + e.getMessage());
		}

		modelnView.addObject("bean", passFailBean);
		return modelnView;
	}

	@RequestMapping(value = "/student/studentSelfMarksheetForm", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String studentSelfMarksheetForm(HttpServletRequest request,
			HttpServletResponse response, Model m) {
		
		
		TestDAOForRedis daoForRedis = (TestDAOForRedis)act.getBean("testDaoForRedis");
		if(daoForRedis.checkForFlagValueInCache("movingResultsToCache","Y") ) {
			return "noDataAvailable";
		}
		PassFailExamBean bean = new PassFailExamBean();
		m.addAttribute("studentMarks", bean);
		m.addAttribute("yearList", yearList);
		getStudentMarksHistory(request, response);
		request.getSession().setAttribute("passFailSearchBean", bean);

		return "studentSelfMarksheet";
	}

	@RequestMapping(value = "/student/studentSelfMarksheet", method = { RequestMethod.POST })
	public ModelAndView studentSelfMarksheet(HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute PassFailExamBean studentMarks) throws SQLException {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		ModelAndView modelnView = new ModelAndView("studentSelfMarksheet");
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());
		String sapId = (String) request.getSession().getAttribute("userId");
		StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");
		studentMarks.setSapid(sapId);
		
		logger.info("/studentSelfMarksheet : (SapId, Sem, M, Y ) : (" + sapId + ", " + studentMarks.getSem() + ", "
				+ studentMarks.getWrittenMonth() + "," + studentMarks.getWrittenYear() + ")");
		
		//One time fetch from REDIS, Vilpesh 20220701
		boolean hasAppearedForExamForGivenSemMonthYear;
		Map<String, Object> simpleMap = null;
		simpleMap = this.fetchResultsFromRedis(sapId);

		//To check if Student appeared for Exam from REDIS, Vilpesh on 20220623
		Boolean attempted = this.checkMarksHistoryForStudent(simpleMap, studentMarks.getSem(), sapId,
				studentMarks.getWrittenMonth(), studentMarks.getWrittenYear());
		if(null != attempted) {
			hasAppearedForExamForGivenSemMonthYear = attempted.booleanValue();
		} else {
			hasAppearedForExamForGivenSemMonthYear = dao
				.hasAppearedForExamForGivenSemMonthYear(studentMarks,
						student.getExamMode());
		}
		if (hasAppearedForExamForGivenSemMonthYear == false) {
			request.setAttribute("error", "true");
			request.setAttribute(
					"errorMessage",
					"You have not appeared for Semester "
							+ studentMarks.getSem() + " in Year "
							+ studentMarks.getWrittenYear() + " and month "
							+ studentMarks.getWrittenMonth());
			modelnView.addObject("studentMarks", studentMarks);
			return modelnView;
		}

		//getStudentMarksHistory(request, response);
		getStudentMarksHistory(simpleMap, request, response);//new method reuse from REDIS data in simpleMap, -Vilpesh 20220701

		request.getSession().setAttribute("passFailSearchBean", studentMarks);

		// Check if results are live
		MarksheetBean studentData = dao.getSingleStudentsData(studentMarks);

		String programStructure = studentData.getPrgmStructApplicable();
		String resultDeclarationDate = "";
		ExamOrderExamBean exam = dao.getExamDetails(studentMarks.getWrittenMonth(),
				studentMarks.getWrittenYear());
		if (exam == null) {
			request.setAttribute("error", "true");
			request.setAttribute(
					"errorMessage",
					"No Exam Bookings found for "
							+ studentMarks.getWrittenMonth() + "-"
							+ studentMarks.getWrittenYear()
							+ " Exam, Semester " + studentMarks.getSem());
			return modelnView;
		}
		String resultLive = "N";

		if ("Online".equals(student.getExamMode())) {
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
		studentData.setResultDeclarationDate(resultDeclarationDate);
		if (!"Y".equalsIgnoreCase(resultLive)) {
			request.setAttribute("error", "true");
			request.setAttribute(
					"errorMessage",
					"Results are not yet announced for "
							+ studentMarks.getWrittenMonth() + "-"
							+ studentMarks.getWrittenYear() + " Exam cycle.");
			return modelnView;
		}
		
		HashMap<String, ArrayList> keysMap = null;
		//To create Map(SapIdSubject, list of marks) from REDIS, Vilpesh on 20220624
		keysMap = this.fetchMarksHistoryOnSem(simpleMap, studentMarks.getSem(), studentMarks.getSapid(),
				studentMarks.getWrittenMonth(), studentMarks.getWrittenYear(), studentData.getConsumerType());
		if(null == keysMap) {
			keysMap = dao.getMarksRecordsForStudent(
					studentMarks.getSem(), studentMarks.getSapid(),
					studentMarks.getWrittenMonth(), studentMarks.getWrittenYear());
		}
		if (keysMap == null || keysMap.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}

		ArrayList<PassFailExamBean> pFList = null; //holds passfail from REDIS -Vilpesh 20220628
		Boolean runGrace = Boolean.TRUE; //false for REDIS, true for db -Vilpesh 20220628
		
		// ArrayList<PassFailBean> passFailStudentList = dao.process(keysMap);
		ArrayList<PassFailExamBean> passFailStudentList = null;

//		if("ACBM".equalsIgnoreCase(studentData.getProgram())){
		boolean isBajaj = "Bajaj".equalsIgnoreCase(studentData.getConsumerType());
		boolean isDBMJul2014 = "Jul2014".equalsIgnoreCase(studentData.getPrgmStructApplicable()) && "DBM".equalsIgnoreCase(studentData.getProgram());
		if(isBajaj && !isDBMJul2014){
//			passFailStudentList = dao.processACBM(keysMap);
			passFailStudentList = dao.processBajaj(keysMap);
		} else {
			try {
				//fetch passfail from REDIS, Vilpesh on 20220630
				pFList = this.findPassfail(simpleMap, studentMarks.getSem(), studentMarks.getSapid(),
						studentMarks.getWrittenMonth(), studentMarks.getWrittenYear());
				if(null != pFList && pFList.isEmpty()) {
					pFList = null;
				}
			} catch(Exception ex) {
				logger.error("MarksheetController: findPassfail : db fetch continued : "+ ex.getMessage());
				pFList = null;
			}

			if (null == pFList) {
				passFailStudentList = dao.processNew(keysMap);// Use new pass fail logic
			} else {
				runGrace = Boolean.FALSE;
				passFailStudentList = pFList;
				logger.info("MarksheetController: Marksheet From(REDIS) for (SapId, Sem, WrittenMonth, WrittenYear) ("
						+ studentMarks.getSapid() + "," + studentMarks.getSem() + "," + studentMarks.getWrittenMonth()
						+ "," + studentMarks.getWrittenYear() + ")");
			}
			
		}
		Boolean isProjectApplicable =  sDao.isProjectApplicable(studentData.getProgram(),studentMarks.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
		Boolean isModuleProjectApplicable = sDao.isModuleProjectApplicable(studentData.getProgram(),studentMarks.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
		Boolean isMimicProApplicable =  sDao.isMimicProApplicable(studentData.getProgram(),studentMarks.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
		Boolean isMimicSocialApplicable =  sDao.isMimicSocialApplicable(studentData.getProgram(),studentMarks.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());

		List<PassFailExamBean> studentMarksList = new ArrayList<>();
		// Take only those records where he has written or assignment year/month
		// is same one as selected on form.
		if (passFailStudentList != null && !passFailStudentList.isEmpty()) {
			// if("4".equalsIgnoreCase(studentData.getSem())) {
			if(isProjectApplicable.equals(true)) {
				  if(!keysMap.containsKey(studentData.getSapid().trim()+"Project")) {
				  PassFailExamBean passFailBean = new PassFailExamBean();
				  passFailBean.setSapid(studentData.getSapid());
				  passFailBean.setSubject("Project"); passFailBean.setSem(studentMarks.getSem());
				  passFailBean.setProgram(studentData.getProgram());
				  passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
				  passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
				  passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
				  passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
				  passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
				  }
				  
				  }

			if(isModuleProjectApplicable.equals(true)) {
				//if("4".equalsIgnoreCase(studentMarks.getSem())) {
				if(!keysMap.containsKey(studentMarks.getSapid().trim()+"Module 4 - Project")) {
					PassFailExamBean passFailBean = new PassFailExamBean();
					passFailBean.setSapid(studentMarks.getSapid());
					passFailBean.setSubject("Module 4 - Project"); passFailBean.setSem(studentMarks.getSem());
					passFailBean.setProgram(studentData.getProgram());
					passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
					passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
					passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
					passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
					passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
				}
				  
			}
			if(isMimicProApplicable.equals(true)) {
				  if(!keysMap.containsKey(studentData.getSapid().trim()+"Simulation: Mimic Pro")) {
				  PassFailExamBean passFailBean = new PassFailExamBean();
				  passFailBean.setSapid(studentData.getSapid());
				  passFailBean.setSubject("Simulation: Mimic Pro"); passFailBean.setSem(studentMarks.getSem());
				  passFailBean.setProgram(studentData.getProgram());
				  passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
				  passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
				  passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
				  passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
				  passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
				  }
				  
			}
			if(isMimicSocialApplicable.equals(true)) {
				  if(!keysMap.containsKey(studentData.getSapid().trim()+"Simulation: Mimic Social")) {
				  PassFailExamBean passFailBean = new PassFailExamBean();
				  passFailBean.setSapid(studentData.getSapid());
				  passFailBean.setSubject("Simulation: Mimic Social"); passFailBean.setSem(studentMarks.getSem());
				  passFailBean.setProgram(studentData.getProgram());
				  passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
				  passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
				  passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
				  passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
				  passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
				  }
				  
			}
			
			studentMarksList.addAll(passFailStudentList);
			//if added to not do Grace, if passfail from REDIS, -Vilpesh 20220701
			if(runGrace) {
				studentMarksList = handleGraceMarksLogic(studentMarksList);
			}
			
		}


		HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
		String examMonth = studentMarks.getWrittenMonth();
		String examYear = studentMarks.getWrittenYear();

		CreatePDF helper = new CreatePDF();
		helper.generateSingleStudentMarksheet(studentMarksList, studentData,
				examMonth, examYear, examOrderMap, getCentersMap());
		ArrayList<MarksheetBean> marksheetList = new ArrayList<>();
		marksheetList.add(studentData);

		try {
			helper.createStudentSelfMarksheetPDF(marksheetList,
					resultDeclarationDate, request, getProgramAllDetailsMap(),
					getCentersMap(), MARKSHEETS_PATH, getModProgramMap());
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Marksheet generated successfully. Please click Download link below to get file.");

		} catch (Exception e) {
			logger.error("Error in generating marksheet of studentSelfMarksheet: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet." + e.getMessage());
		}

		simpleMap = null;
		
		return modelnView;
	}
	
	//keep this method readFromCache(), -Vilpesh 20220701
	protected boolean readFromCache() {
		boolean readFrom = Boolean.FALSE;
		readFrom = this.fetchRedisHelper().readFromCache();
		return readFrom;
	}
	
	//keep this method readFromCache(), -Vilpesh 20220701
	public Map<String, Object> fetchResultsFromRedis(final String sapId) {
		Map<String, Object> simpleMap = null;
		boolean readFrom = Boolean.FALSE;
		try {
			readFrom = this.readFromCache();
			if (readFrom) {
				simpleMap = this.fetchRedisHelper().fetchResultsFromRedis(ResultsFromRedisHelper.EXAM_STAGE_TEE, sapId);
			} else {
				logger.info("MarksheetController: fetchResultsFromRedis : db fetch done.");
			}
		} catch (Exception ex) {
			simpleMap = null;
			logger.error(
					"MarksheetController: fetchResultsFromRedis : db fetch done as getting error : " + ex.getMessage());
		}
		return simpleMap;
	}
	
	// keep this method , do not delete - Vilpesh 20220623
	/**
	 * In MarksHistory pulled from REDIS, checks that row exists.
	 * @param simpleMap 
	 * @param sem
	 * @param sapId
	 * @param writtenMonth
	 * @param writtenYear
	 * @return null if any exception or disallowed read from REDIS, true/false if
	 *         found
	 */
	public Boolean checkMarksHistoryForStudent(final Map<String, Object> simpleMap, String sem, String sapId,
			String writtenMonth, String writtenYear) {
		Boolean attempted = Boolean.FALSE;
		try {
			if(null != simpleMap) {
				attempted = this.fetchRedisHelper().checkMarksHistoryForStudent(simpleMap,
						ResultsFromRedisHelper.EXAM_STAGE_TEE, sem, sapId, writtenMonth, writtenYear);
			} else {
				attempted = null;
				logger.info("MarksheetController: checkMarksHistoryForStudent : db fetch to be done.");
			}
		} catch (Exception ex) {
			logger.error("MarksheetController: checkMarksHistoryForStudent : db fetch done as getting error : "
					+ ex.getMessage());
			attempted = null;
		}
		return attempted;
	}
	
	// keep this method fetchMarksHistoryOnSem, do not delete - Vilpesh 20220624
	/**
	 * In MarksHistory pulled from REDIS, create Map(SapIdSubject, list of marks).
	 *
	 * @param simpleMap
	 * @param sem
	 * @param sapId
	 * @param writtenMonth
	 * @param writtenYear
	 * @param studentType
	 * @return null if any exception or disallowed read from REDIS, Map if found.
	 */
	public HashMap<String, ArrayList> fetchMarksHistoryOnSem(final Map<String, Object> simpleMap, String sem,
			String sapId, String writtenMonth, String writtenYear, String studentType) {
		HashMap<String, ArrayList> keysMap = null;
		List<StudentMarksBean> datalist = null;
		ArrayList<StudentMarksBean> list = null;
		StudentMarksBean bean = null;
		DateHelper dateHelper = null;
		Boolean isLessOrEqualExamOrder;
		int size;
		try {
			if (null != simpleMap) {
				dateHelper = new DateHelper();

				datalist = this.fetchRedisHelper().fetchMarksHistoryOnSem(simpleMap,
						ResultsFromRedisHelper.EXAM_STAGE_TEE, sem, sapId, writtenMonth, writtenYear);
				if (null != datalist && !datalist.isEmpty()) {
					keysMap = new HashMap<>();
					size = datalist.size();
					for (int i = 0; i < size; i++) {
						bean = (StudentMarksBean) datalist.get(i);

						isLessOrEqualExamOrder = dateHelper.compareYearMonth(writtenYear, writtenMonth, bean.getYear(),
								bean.getMonth());
						if (isLessOrEqualExamOrder) {
							bean.setStudentType(studentType);// Needed in PF processing.
							String key = bean.getSapid().trim() + bean.getSubject().trim();
							// added by Swarup Jul 2023, to add marks records so DB fetch can be done in
							// case redis fails
							keysMap.computeIfAbsent(key, k -> new ArrayList<>()).add(bean);
//							if (!keysMap.containsKey(key)) {
//								list = new ArrayList<>();
//								list.add(bean);
//								keysMap.put(key, list);
//							} else {
//								list = (ArrayList) keysMap.get(key);
//							}
						} else {
							logger.info(
									"fetchMarksHistoryOnSem : Not Added (SapId, Sem, Subject, writtenYear, writtenMonth, beanYear, beanMonth) ("
											+ sapId + "," + sem + "," + bean.getSubject() + "," + writtenYear + ","
											+ writtenMonth + "," + bean.getYear() + "," + bean.getMonth() + ")");
						}
					}
					logger.info("fetchMarksHistoryOnSem : keysMap (SapIdSubject) : " + keysMap.keySet().size());
				} else {
					logger.info(
							"fetchMarksHistoryOnSem : REDIS empty, db fetch continued, (sapId, sem, writtenMonth, writtenYear, studentType) ("
									+ sapId + "," + sem + "," + writtenMonth + "," + writtenYear + "," + studentType
									+ ")");
					keysMap = null;
				}
			} else {
				keysMap = null;
				logger.info("MarksheetController: fetchMarksHistoryOnSem : db fetch to be done.");
			}
		} catch (Exception ex) {
			logger.error("MarksheetController: fetchMarksHistoryOnSem : db fetch done as getting error : "
					+ ex.getMessage());
			keysMap = null;
			dateHelper = null;
		}
		return keysMap;
	}
	
	// keep this method findPassfail, do not delete - Vilpesh 20220628
	public ArrayList<PassFailExamBean> findPassfail(final Map<String, Object> simpleMap, final String sem,
			final String sapId, final String examMonth, final String examYear) {
		String examYearExamMonth = null;
		List<PassFailExamBean> onlyList = null;
		ArrayList<PassFailExamBean> list = null;
		List<PassFailExamBean> pFList = null;
		String lastAttemptYearMonth = null;
		try {
			if (null != simpleMap) {
				onlyList = this.fetchRedisHelper().fetchOnlyPassfailList(simpleMap,
						ResultsFromRedisHelper.EXAM_STAGE_TEE, sapId);
				if (null != onlyList) {
					if (onlyList instanceof List && !onlyList.isEmpty()) {
						// Find the last attempt Year month for a Semester, from Assignment and/or Exam
						// Year month.
						lastAttemptYearMonth = this.fetchLastAttemptYearMonthForSem(sem, sapId, onlyList);
						examYearExamMonth = examYear + examMonth;
						logger.info(
								"findPassfail : Comparing in Sem for (Sem, ExamYearExamMonth, lastAttemptYearMonth) : ("
										+ sem + "," + examYearExamMonth + "," + lastAttemptYearMonth + ")");
						if (null != lastAttemptYearMonth && lastAttemptYearMonth.equals(examYearExamMonth)) {
							pFList = onlyList;
							if (null != pFList && !pFList.isEmpty()) {
								list = new ArrayList<PassFailExamBean>();
								for (PassFailExamBean bean : pFList) {
									if (null != bean.getSem() && null != bean.getSapid()
											&& (sem.equalsIgnoreCase(bean.getSem())
													&& sapId.equalsIgnoreCase(bean.getSapid()))) {
										list.add(bean);
									}
								}
								logger.info("findPassfail : Found(REDIS) Passfail (SapId, Sem, Subjects) (" + sapId
										+ "," + sem + "," + list.size() + ")");
							} else if (null == pFList || (null != pFList && pFList.isEmpty())) {
								list = null;
								logger.info("findPassfail : Empty/Absent(REDIS) Passfail (SapId, Sem) (" + sapId + ","
										+ sem + ")");
							}
						} else {
							logger.info(
									"findPassfail : Wrong Sem/Wrong ExamYearExamMonth in REDIS, db fetch done (SapId, Sem) ("
											+ sapId + "," + sem + ")");
						}
						examYearExamMonth = null;
					} else if (onlyList instanceof List && onlyList.isEmpty()) {
						logger.info("findPassfail : Empty in REDIS, db fetch done (SapId, Sem) (" + sapId + "," + sem
								+ ")");
					}
				} else {
					logger.info("findPassfail : Not in REDIS, db fetch done (SapId, Sem) (" + sapId + "," + sem + ")");
				}
			} else {
				list = null;
				logger.info("MarksheetController: findPassfail : db fetch to be done.");
			}
		} catch (Exception ex) {
			list = null;
			logger.error("MarksheetController: findPassfail : db fetch done as getting error : " + ex.getMessage());
		}
		return list;
	}

	// keep this method fetchLastAttemptYearMonthForSem, do not delete -Vilpesh
	// 20220628
	/**
	 * Overloaded method.
	 * 
	 * @param sem
	 * @param sapId
	 * @param list  List of PassFailExamBean(s)
	 * @return
	 */
	protected String fetchLastAttemptYearMonthForSem(final String sem, final String sapId,
			final List<PassFailExamBean> list) {
		String temp_highestYearMonth = null;
		String highestYearMonth = null;
		Map<Integer, Set<String>> yearMonthMap = null;
		Set<String> yearMonthSet = null;
		Set<Integer> keySet = null;
		// logger.info("MarksheetController - fetchLastAttemptYearMonthForSem - (Sem,
		// SapId) - (" + sem + ", " + sapId + ")");
		yearMonthMap = this.fetchRedisHelper().fetchYearMonthFromPassfailForSemBoth(sem, list);
		if (null != yearMonthMap) {
			keySet = yearMonthMap.keySet();
			for (Integer key : keySet) {
				yearMonthSet = yearMonthMap.get(key);
				if (null != yearMonthSet && !yearMonthSet.isEmpty()) {
					temp_highestYearMonth = DateHelper.findHighestYearMonth(yearMonthSet,
							DateHelper.FORMAT_YEAR_DASH_MONTH);// yyyy-MMM
				} else if (null != yearMonthSet && yearMonthSet.isEmpty()) {
					logger.info(
							"MarksheetController - fetchLastAttemptYearMonthForSem - (no subjects found passed/failed for) Sem - "
									+ sem);
				}
			}
			if (null != temp_highestYearMonth) {
				highestYearMonth = temp_highestYearMonth.substring(0, 4);// extract 2020 from 2020-Dec
				highestYearMonth = highestYearMonth + temp_highestYearMonth.substring(5);// extract Dec from 2020-Dec
			}
		}
		return highestYearMonth;
	}

	@RequestMapping(value = "/admin/marksheetFromSRForm", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String marksheetFromSRForm(HttpServletRequest request,
			HttpServletResponse response, Model m) {

		PassFailExamBean bean = new PassFailExamBean();
		request.getSession().setAttribute("bean", bean);

		return "marksheetFromSR";
	}
	

	@RequestMapping(value = "/admin/generateMarksheetFromSR", method = RequestMethod.POST)
	public ModelAndView generateMarksheetFromSR(HttpServletRequest request,
			HttpServletResponse response, @ModelAttribute PassFailExamBean bean) {
		ModelAndView modelnView = new ModelAndView("marksheetFromSR");
		request.getSession().setAttribute("passFailSearchBean", bean);

		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		
		List<MarksheetBean> studentForSRList = dao.getStudentsForSR(bean);		
		logger.info("studentForSRList size:"+studentForSRList.size());
		if (studentForSRList == null || studentForSRList.size() == 0) {
			logger.error("Marksheet records are empty.");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}
		
		ArrayList<MarksheetBean> marksheetList = new ArrayList<>();
		CreatePDF helper = new CreatePDF();
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		//Storing Error SRIDs. 
		List<String> SRErrorList=new ArrayList<String>();
		for (MarksheetBean studentData : studentForSRList) {
			try {
			Boolean isProjectApplicable =  sDao.isProjectApplicable(studentData.getProgram(),studentData.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
			Boolean isModuleProjectApplicable =  sDao.isModuleProjectApplicable(studentData.getProgram(),studentData.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
			
			//added to solve case where Mimic Social not displayed. by Vilpesh 2022-06-11 
			Boolean isMimicProApplicable =  sDao.isMimicProApplicable(studentData.getProgram(),studentData.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
			Boolean isMimicSocialApplicable =  sDao.isMimicSocialApplicable(studentData.getProgram(),studentData.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
			
			String programStructure = studentData.getPrgmStructApplicable();
			String resultDeclarationDate = "";
			ExamOrderExamBean exam = dao.getExamDetails(studentData.getMonth(),
					studentData.getYear());
			String resultLive = "N";

			if ("Online".equals(studentData.getExamMode())) {
				
				resultDeclarationDate = dao.getOnlineExamDeclarationDate(
						studentData.getMonth(), studentData.getYear());
				resultLive = exam.getLive();
				
			} else {
				resultDeclarationDate = dao.getOfflineExamDeclarationDate(
						studentData.getMonth(), studentData.getYear());
				resultLive = exam.getOflineResultslive();
			}
			studentData.setResultDeclarationDate(resultDeclarationDate);

			// Generate marksheet records
			HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudent(
					studentData.getSem(), studentData.getSapid(),
					studentData.getMonth(), studentData.getYear());
			if (keysMap == null || keysMap.size() == 0) {
				SRErrorList.add(studentData.getServiceRequestId());
//				request.setAttribute("error", "true");`
//				request.setAttribute("errorMessage", "No records found.");
//				return modelnView;
			}

			// ArrayList<PassFailBean> passFailStudentList =
			// dao.processNew(keysMap);
			ArrayList<PassFailExamBean> passFailStudentList = null;

//			if("ACBM".equalsIgnoreCase(studentData.getProgram())){
			
			boolean isBajaj = "Bajaj".equalsIgnoreCase(studentData.getConsumerType());
			boolean isDBMJul2014 = "Jul2014".equalsIgnoreCase(studentData.getPrgmStructApplicable()) && "DBM".equalsIgnoreCase(studentData.getProgram());
			if(isBajaj && !isDBMJul2014){
//				passFailStudentList = dao.processACBM(keysMap);
				passFailStudentList = dao.processBajaj(keysMap);
			} else {
				passFailStudentList = dao.processNew(keysMap);// Use new pass
																// fail logic
			}

			List<PassFailExamBean> studentMarksList = new ArrayList<>();
			// Take only those records where he has written or assignment
			// year/month is same one as selected on form.
			if (passFailStudentList != null && !passFailStudentList.isEmpty()) {
				if(isProjectApplicable.equals(true)) {
				 //if("4".equalsIgnoreCase(studentData.getSem())) {
					  if(!keysMap.containsKey(studentData.getSapid().trim()+"Project")) {
					  PassFailExamBean passFailBean = new PassFailExamBean();
					  passFailBean.setSapid(studentData.getSapid());
					  passFailBean.setSubject("Project"); passFailBean.setSem(studentData.getSem());
					  passFailBean.setProgram(studentData.getProgram());
					  passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
					  passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
					  passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
					  passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
					  passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
					  }
					  
				  }
				if(isModuleProjectApplicable.equals(true)) {
					if(!keysMap.containsKey(studentData.getSapid().trim()+"Module 4 - Project")) {
						PassFailExamBean passFailBean = new PassFailExamBean();
						passFailBean.setSapid(studentData.getSapid());
						passFailBean.setSubject("Module 4 - Project"); passFailBean.setSem(studentData.getSem());
						passFailBean.setProgram(studentData.getProgram());
						passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
						passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
						passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
						passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
						passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
					} 
				}

				//added to solve case where Mimic Social not displayed. by Vilpesh 2022-06-11
				if(isMimicProApplicable.equals(true)) {
					if (!keysMap.containsKey(studentData.getSapid().trim() + "Simulation: Mimic Pro")) {
						PassFailExamBean passFailBean = new PassFailExamBean();
						passFailBean.setSapid(studentData.getSapid());
						passFailBean.setSubject("Simulation: Mimic Pro");
						passFailBean.setSem(studentData.getSem());
						passFailBean.setProgram(studentData.getProgram());
						passFailBean.setName(studentData.getFirstName() + " " + studentData.getLastName());
						passFailBean.setWrittenscore("");
						passFailBean.setAssignmentscore("");
						passFailBean.setWrittenYear("");
						passFailBean.setWrittenMonth("");
						passFailBean.setAssignmentYear("");
						passFailBean.setAssignmentMonth("");
						passFailBean.setTotal("--");
						passFailStudentList.add(passFailBean);
					}
				}
				
				if(isMimicSocialApplicable.equals(true)) {
					if (!keysMap.containsKey(studentData.getSapid().trim() + "Simulation: Mimic Social")) {
						PassFailExamBean passFailBean = new PassFailExamBean();
						passFailBean.setSapid(studentData.getSapid());
						passFailBean.setSubject("Simulation: Mimic Social");
						passFailBean.setSem(studentData.getSem());
						passFailBean.setProgram(studentData.getProgram());
						passFailBean.setName(studentData.getFirstName() + " " + studentData.getLastName());
						passFailBean.setWrittenscore("");
						passFailBean.setAssignmentscore("");
						passFailBean.setWrittenYear("");
						passFailBean.setWrittenMonth("");
						passFailBean.setAssignmentYear("");
						passFailBean.setAssignmentMonth("");
						passFailBean.setTotal("--");
						passFailStudentList.add(passFailBean);
					} 
				}
				
				//Prepare a bean to get waived-off subjects
				StudentExamBean stdExmBean =  new StudentExamBean();
				
				//Set required properties
				stdExmBean.setSapid(studentData.getSapid());
				stdExmBean.setSem(studentData.getSem());
				stdExmBean.setConsumerType(studentData.getConsumerType());
				stdExmBean.setPrgmStructApplicable(studentData.getPrgmStructApplicable());
				stdExmBean.setIsLateral(studentData.getIsLateral());
				stdExmBean.setPreviousStudentId(studentData.getPreviousStudentId());
				stdExmBean.setProgram(studentData.getProgram());
				stdExmBean.setFirstName(studentData.getFirstName());
				stdExmBean.setLastName(studentData.getLastName());
				stdExmBean.setOldProgram(studentData.getOldProgram());
					//Get waived-Off subjects list and add to passFailStudent's List only if current student is lateral.
					if (stdExmBean.getIsLateral().equalsIgnoreCase("Y"))
						passFailStudentList.addAll(msService.getWaivedOffSubjectsToAddonMarksheet(stdExmBean));
				studentMarksList.addAll(passFailStudentList);
				
				studentMarksList = handleGraceMarksLogic(studentMarksList);
				
			}


			HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
			String examMonth = studentData.getMonth();
			String examYear = studentData.getYear();

			helper.generateSingleStudentMarksheet(studentMarksList,
					studentData, examMonth, examYear, examOrderMap,
					getCentersMap());

			marksheetList.add(studentData);
			}catch (Exception e) {
				
				SRErrorList.add(studentData.getServiceRequestId());
				logger.error("error: "+e.getMessage());
			}
		}
		
		
		try {
			request.setAttribute("logoRequired", bean.getLogoRequired());
			helper.createPDF(marksheetList, null, request, getProgramMap(),
					getCentersMap(), MARKSHEETS_PATH, getModProgramMap());
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Marksheet generated successfully. Please click Download link below to get file.");
			/* dao.updateSRStatus(bean, "In Progress"); */
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error in generating marksheet of generateMarksheetFromSR:"+e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		}
		
		//Converting Error list to string for printing error message on front-end.
		String errorSRString = String.join(", ", SRErrorList);
		String errorMessage="Error in generating marksheet for following SRID:";
		if(!SRErrorList.isEmpty()) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", errorMessage+"\n"+errorSRString);
		}
		//Collections.sort(marksheetList);
		return modelnView;
	}
	@RequestMapping(value = "/admin/marksheetFromSRFormForMBAWX", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String marksheetFromSRFormForMBAWX(HttpServletRequest request,
			HttpServletResponse response, Model m) {

		EmbaPassFailBean bean =  new EmbaPassFailBean();
		request.getSession().setAttribute("bean", bean);

		return "marksheetFromSRForMBAWX";
	}
	
	@RequestMapping(value = "/admin/generateMarksheetFromSRForMBAWX", method = RequestMethod.POST)
	public ModelAndView generateMarksheetFromSRForMBAWX(HttpServletRequest request,
			HttpServletResponse response, @ModelAttribute EmbaPassFailBean bean) {

		ModelAndView modelnView = new ModelAndView("marksheetFromSRForMBAWX");
		request.getSession().setAttribute("passFailSearchBean", bean);
		request.setAttribute("logoRequired", bean.getLogoRequired());
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		ArrayList<EmbaMarksheetBean> marksheetList = new ArrayList<>();
		EmbaMarksheetBean marksheet = new EmbaMarksheetBean();
		List<EmbaPassFailBean> passFailDataListAllSapidsAllSems = new ArrayList<EmbaPassFailBean>();
		List<List<EmbaPassFailBean>> passFailCollectiveData = new ArrayList<List<EmbaPassFailBean>>();
		CreatePDF helper = new CreatePDF();
		String fileName = "";
		String commaSeparatedList = "";
		try {
			List<EmbaMarksheetBean> studentForSRList = examsAssessmentsDAO.getStudentsForSRForMBAWX(bean);
			//Map<String, Set<Integer>> dissertationMap = new HashMap<String, Set<Integer>>();
			Set<String> dissertationSem = new HashSet<>();
			if (studentForSRList == null || studentForSRList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
				return modelnView; 
			}
			// variable created for check the data should go to find for Master Dissertation or not
			boolean dissertationExist= false;
			
			for (EmbaMarksheetBean studentData : studentForSRList) {
				commaSeparatedList = commaSeparatedList + studentData.getSapid() + ","; 
				
				//Based on master key we are going to collect sapid and sem 
				if (IA_MASTER_DISSERTATION_MASTER_KEY == studentData.getConsumerProgramStructureId()) {
					dissertationExist =  true;
					dissertationSem.add(studentData.getSapid()+"-"+studentData.getSem());
				}
				bean.setSapid(studentData.getSapid());
				marksheet = msService.generateMarksheetFromSRForMBAWX(bean,studentData,getCentersMap());
				//passFailDataList = examsAssessmentsDAO.getEmbaPassFailBySapid(studentData.getSapid(), studentData.getSem());
//				passFailCollectiveData.add(passFailDataList);
				marksheetList.add(marksheet);
			}
			//Collections.sort(marksheetList);
			passFailDataListAllSapidsAllSems = examsAssessmentsDAO.getEmbaPassFailByAllSapids(commaSeparatedList);
			
			//this to check whether the student is pass in sem 7 for the data coming from mba.passFail;
			boolean semFlag = passFailDataListAllSapidsAllSems.stream()
					.filter(passFail -> IA_MASTER_DISSERTATION_Q7_SEM == Integer.parseInt(passFail.getSem()))
					.findFirst().isPresent();
			
			//created to get sapid and sem for those result is not live
			Set<String> errorList = new HashSet<>();

			//if sapids for master key  = 131 that is MasterDissertation is exists then it will go to find the passFailDetails
			if (dissertationExist) {
				List<EmbaPassFailBean> passFailSapidForDissertation = getResultForDissertation(dissertationSem, semFlag,
						errorList);

				//if isResult is not live for any sapid and sem all the data came from mba.passFail will be removed for that sapid and sem
				if(!errorList.isEmpty()) {
					for(String student:errorList) {
						String[] studentDetails=student.split("-");
						String sapid = studentDetails[0];
						String sem = studentDetails[1];
						passFailDataListAllSapidsAllSems.removeIf(passfail -> passfail.getSapid().equalsIgnoreCase(sapid)
								&& passfail.getSem().equalsIgnoreCase(sem));
					};
				
					
				}
				
				//If data retrive from master dissertaion passFail table is not empty the data will be added with mba.passFail data
				if (!passFailSapidForDissertation.isEmpty()) {
					passFailDataListAllSapidsAllSems.addAll(passFailSapidForDissertation);
				}
			}
			try {
				
					marksheetBean = helper.generateMarksheetPDFForMBAWX(marksheetList,	
						 marksheetBean,getProgramMap(),
							getCentersMap(),
						 MARKSHEETS_PATH,passFailDataListAllSapidsAllSems,request,getModProgramMap());
				marksheetBean.setSuccess(true);
				if(fileName == "") {
					fileName = marksheetBean.getFileName();
				}
				
//				marksheetBean.setMessage( "Marksheet generated successfully. Please click Download link below to get file.");
				
							

			} catch (Exception e) {
				logger.error("Error in generating gradesheet of generateMarksheetFromSRForMBAWX: "+e);
			}
			
			request.getSession().setAttribute("fileName", marksheetBean.getFileName());
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Marksheet generated successfully. Please click Download link below to get file.");
			
			
			
		}
		catch(Exception e) {
			
			marksheetBean.setError(true);
			marksheetBean.setMessage("Error in generating marksheet." + e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		}
		
		

//		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
//
//		List<MarksheetBean> studentForSRList = dao.getStudentsForSR(bean);
//
//		if (studentForSRList == null || studentForSRList.size() == 0) {
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", "No records found.");
//			return modelnView;
//		}
//
//		ArrayList<MarksheetBean> marksheetList = new ArrayList<>();
//		CreatePDF helper = new CreatePDF();
//		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
//		for (MarksheetBean studentData : studentForSRList) {
//			Boolean isProjectApplicable =  sDao.isProjectApplicable(studentData.getProgram(),studentData.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
//			String programStructure = studentData.getPrgmStructApplicable();
//			String resultDeclarationDate = "";
//			ExamOrderBean exam = dao.getExamDetails(studentData.getMonth(),
//					studentData.getYear());
//			String resultLive = "N";
//
//			if ("Online".equals(studentData.getExamMode())) {
//				resultDeclarationDate = dao.getOnlineExamDeclarationDate(
//						studentData.getMonth(), studentData.getYear());
//				resultLive = exam.getLive();
//			} else {
//				resultDeclarationDate = dao.getOfflineExamDeclarationDate(
//						studentData.getMonth(), studentData.getYear());
//				resultLive = exam.getOflineResultslive();
//			}
//			studentData.setResultDeclarationDate(resultDeclarationDate);
//
//			// Generate marksheet records
//			HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudent(
//					studentData.getSem(), studentData.getSapid(),
//					studentData.getMonth(), studentData.getYear());
//			if (keysMap == null || keysMap.size() == 0) {
//				request.setAttribute("error", "true");
//				request.setAttribute("errorMessage", "No records found.");
//				return modelnView;
//			}
//
//			// ArrayList<PassFailBean> passFailStudentList =
//			// dao.processNew(keysMap);
//			ArrayList<PassFailBean> passFailStudentList = null;
//
//			if ("ACBM".equals(studentData.getProgram())) {
//				passFailStudentList = dao.processACBM(keysMap);
//			} else {
//				passFailStudentList = dao.processNew(keysMap);// Use new pass
//																// fail logic
//			}
//
//			List<PassFailBean> studentMarksList = new ArrayList<>();
//			// Take only those records where he has written or assignment
//			// year/month is same one as selected on form.
//			if (passFailStudentList != null && !passFailStudentList.isEmpty()) {
//				if(isProjectApplicable.equals(true)) {
//				 //if("4".equalsIgnoreCase(studentData.getSem())) {
//					  if(!keysMap.containsKey(studentData.getSapid().trim()+"Project")) {
//					  PassFailBean passFailBean = new PassFailBean();
//					  passFailBean.setSapid(studentData.getSapid());
//					  passFailBean.setSubject("Project"); passFailBean.setSem(studentData.getSem());
//					  passFailBean.setProgram(studentData.getProgram());
//					  passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
//					  passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
//					  passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
//					  passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
//					  passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
//					  }
//					  
//					  }
//				studentMarksList.addAll(passFailStudentList);
//				studentMarksList = handleGraceMarksLogic(studentMarksList);
//				
//			}
//
//
//			HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
//			String examMonth = studentData.getMonth();
//			String examYear = studentData.getYear();
//
//			helper.generateSingleStudentMarksheet(studentMarksList,
//					studentData, examMonth, examYear, examOrderMap,
//					getCentersMap());
//
//			marksheetList.add(studentData);
//		}
//		Collections.sort(marksheetList);
//
//		try {
//			helper.createPDF(marksheetList, null, request, getProgramMap(),
//					getCentersMap(), MARKSHEETS_PATH);
//			request.setAttribute("success", "true");
//			request.setAttribute(
//					"successMessage",
//					"Marksheet generated successfully. Please click Download link below to get file.");
//			/* dao.updateSRStatus(bean, "In Progress"); */
//		} catch (DocumentException | IOException e) {
//			// TODO Auto-generated catch block
//			
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage",
//					"Error in generating marksheet.");
//		}

		return modelnView;
	}
	
	@GetMapping("/admin/generateNonGradedMarksheetFromSRForm")
	public String generateNonGradedMarksheetFromSRForm(HttpServletRequest request,
			HttpServletResponse response, @ModelAttribute EmbaPassFailBean bean) {
		//return logical view name
		return "nonGradedMarksheetFromSR";
	}
	
	@PostMapping("/admin/generateNonGradedMarksheetFromSR")
	public ModelAndView generateNonGradedMarksheetFromSR(HttpServletRequest request,
			HttpServletResponse response, @ModelAttribute EmbaPassFailBean bean) {
		
		logger.info("MarksheetController.generateNonGradedMarksheetFromSR() - START");
		//Create MOdelAndView with logical view name.
		ModelAndView modelnView = new ModelAndView("nonGradedMarksheetFromSR");
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();
		
		//add model attribute and value in modelAndView.
		modelnView.addObject("bean", bean);
		
		try {
			//Invoke mark sheet generation for non graded programs.  
			marksheetBean = msService.generateNonGradedMarksheet(bean);
			
			//set generated file path in the session for download.
			request.getSession().setAttribute("fileName", marksheetBean.getFileName());
			
			//set success message to the request scope.
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Marksheet generated successfully. Please click Download link below to get file.");
			
		}
		catch(Exception e) {
			//Set error status and message to bean
			marksheetBean.setError(true);
			marksheetBean.setMessage("Error in generating marksheet." + e.getMessage());
			
			//Set error message to request scope
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet Error Message:"+e.getStackTrace());
			logger.error("Error in generating marksheet Error Message:"+e.getMessage()+"\nStack Trace is:"+e.getStackTrace());
		}
		
		logger.info("MarksheetController.generateNonGradedMarksheetFromSR() - END");
		
		//return ModelAndView having logical view name.
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/marksheetFromSRFormForMBAX", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String marksheetFromSRFormForMBAX(HttpServletRequest request,
			HttpServletResponse response, Model m) {

		EmbaPassFailBean bean =  new EmbaPassFailBean();
		request.getSession().setAttribute("bean", bean);

		return "marksheetFromSRForMBAX";
	}
	
	
	@RequestMapping(value = "/admin/generateMarksheetFromSRForMBAX", method = RequestMethod.POST)
	public ModelAndView generateMarksheetFromSRForMBAX(HttpServletRequest request,
			HttpServletResponse response, @ModelAttribute EmbaPassFailBean bean) {
		ModelAndView modelnView = new ModelAndView("marksheetFromSRForMBAX");
		request.getSession().setAttribute("passFailSearchBean", bean);
		request.setAttribute("logoRequired", bean.getLogoRequired());
		EmbaMarksheetBean marksheetBean = new EmbaMarksheetBean();
		ExamsAssessmentsDAO examsAssessmentsDAO = (ExamsAssessmentsDAO)act.getBean("examsAssessmentsDAO");
		ArrayList<EmbaMarksheetBean> marksheetList = new ArrayList<>();
		EmbaMarksheetBean marksheet = new EmbaMarksheetBean();
		List<EmbaPassFailBean> passFailDataListAllSapidsAllSems = new ArrayList<EmbaPassFailBean>();
		List<List<EmbaPassFailBean>> passFailCollectiveData = new ArrayList<List<EmbaPassFailBean>>();
		CreatePDF helper = new CreatePDF();
		String fileName = "";
		List<String> sapidList = new ArrayList<>();
		try {
			List<EmbaMarksheetBean> studentForSRList = examsAssessmentsDAO.getStudentsForSRForMBAX(bean);
			
			if (studentForSRList == null || studentForSRList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
				return modelnView; 
			}
			for (EmbaMarksheetBean studentData : studentForSRList) {
				if(!sapidList.contains(studentData.getSapid())) {
					sapidList.add(studentData.getSapid());
				}
				bean.setSapid(studentData.getSapid());
				marksheet = msService.generateMarksheetFromSRForMBAX(bean,studentData,getCentersMap());
				//passFailDataList = examsAssessmentsDAO.getMBAXPassFailBySapid(studentData.getSapid(), studentData.getSem());
//				passFailCollectiveData.add(passFailDataList);
				marksheetList.add(marksheet);
			}
			//Collections.sort(marksheetList);
			

			
			for(String sapid : sapidList) {
				List<EmbaPassFailBean> temp = new ArrayList<EmbaPassFailBean>();
				temp = examsAssessmentsDAO.getMBAXPassFailByAllSapidsForStructureChangeStudent(sapid) ;
				if(temp.size() == 0) {
					temp = examsAssessmentsDAO.getMBAXPassFailByAllSapids(sapid);
				}
				passFailDataListAllSapidsAllSems.addAll(temp);
			}
			try {
				marksheetBean = helper.generateMarksheetPDFForMBAX(marksheetList,	
						 marksheetBean,getProgramMap(),
							getCentersMap(),
						 MARKSHEETS_PATH,passFailDataListAllSapidsAllSems,request,getModProgramMap());
				marksheetBean.setSuccess(true);
				if(fileName == "") {
					fileName = marksheetBean.getFileName();
				}
//				marksheetBean.setMessage( "Marksheet generated successfully. Please click Download link below to get file.");
				
							

			} catch (Exception e) {
				logger.error("Error in generating gradesheet of generateMarksheetFromSRForMBAX: "+e);
				
			}
			
			request.getSession().setAttribute("fileName", marksheetBean.getFileName());
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Marksheet generated successfully. Please click Download link below to get file.");
			
			
			
		}
		catch(Exception e) {
			
			marksheetBean.setError(true);
			marksheetBean.setMessage("Error in generating marksheet." + e.getMessage());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		}
		
		

//		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
//
//		List<MarksheetBean> studentForSRList = dao.getStudentsForSR(bean);
//
//		if (studentForSRList == null || studentForSRList.size() == 0) {
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage", "No records found.");
//			return modelnView;
//		}
//
//		ArrayList<MarksheetBean> marksheetList = new ArrayList<>();
//		CreatePDF helper = new CreatePDF();
//		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
//		for (MarksheetBean studentData : studentForSRList) {
//			Boolean isProjectApplicable =  sDao.isProjectApplicable(studentData.getProgram(),studentData.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
//			String programStructure = studentData.getPrgmStructApplicable();
//			String resultDeclarationDate = "";
//			ExamOrderBean exam = dao.getExamDetails(studentData.getMonth(),
//					studentData.getYear());
//			String resultLive = "N";
//
//			if ("Online".equals(studentData.getExamMode())) {
//				resultDeclarationDate = dao.getOnlineExamDeclarationDate(
//						studentData.getMonth(), studentData.getYear());
//				resultLive = exam.getLive();
//			} else {
//				resultDeclarationDate = dao.getOfflineExamDeclarationDate(
//						studentData.getMonth(), studentData.getYear());
//				resultLive = exam.getOflineResultslive();
//			}
//			studentData.setResultDeclarationDate(resultDeclarationDate);
//
//			// Generate marksheet records
//			HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudent(
//					studentData.getSem(), studentData.getSapid(),
//					studentData.getMonth(), studentData.getYear());
//			if (keysMap == null || keysMap.size() == 0) {
//				request.setAttribute("error", "true");
//				request.setAttribute("errorMessage", "No records found.");
//				return modelnView;
//			}
//
//			// ArrayList<PassFailBean> passFailStudentList =
//			// dao.processNew(keysMap);
//			ArrayList<PassFailBean> passFailStudentList = null;
//
//			if ("ACBM".equals(studentData.getProgram())) {
//				passFailStudentList = dao.processACBM(keysMap);
//			} else {
//				passFailStudentList = dao.processNew(keysMap);// Use new pass
//																// fail logic
//			}
//
//			List<PassFailBean> studentMarksList = new ArrayList<>();
//			// Take only those records where he has written or assignment
//			// year/month is same one as selected on form.
//			if (passFailStudentList != null && !passFailStudentList.isEmpty()) {
//				if(isProjectApplicable.equals(true)) {
//				 //if("4".equalsIgnoreCase(studentData.getSem())) {
//					  if(!keysMap.containsKey(studentData.getSapid().trim()+"Project")) {
//					  PassFailBean passFailBean = new PassFailBean();
//					  passFailBean.setSapid(studentData.getSapid());
//					  passFailBean.setSubject("Project"); passFailBean.setSem(studentData.getSem());
//					  passFailBean.setProgram(studentData.getProgram());
//					  passFailBean.setName(studentData.getFirstName()+" "+studentData.getLastName());
//					  passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
//					  passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
//					  passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
//					  passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
//					  }
//					  
//					  }
//				studentMarksList.addAll(passFailStudentList);
//				studentMarksList = handleGraceMarksLogic(studentMarksList);
//				
//			}
//
//
//			HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
//			String examMonth = studentData.getMonth();
//			String examYear = studentData.getYear();
//
//			helper.generateSingleStudentMarksheet(studentMarksList,
//					studentData, examMonth, examYear, examOrderMap,
//					getCentersMap());
//
//			marksheetList.add(studentData);
//		}
//		Collections.sort(marksheetList);
//
//		try {
//			helper.createPDF(marksheetList, null, request, getProgramMap(),
//					getCentersMap(), MARKSHEETS_PATH);
//			request.setAttribute("success", "true");
//			request.setAttribute(
//					"successMessage",
//					"Marksheet generated successfully. Please click Download link below to get file.");
//			/* dao.updateSRStatus(bean, "In Progress"); */
//		} catch (DocumentException | IOException e) {
//			// TODO Auto-generated catch block
//			
//			request.setAttribute("error", "true");
//			request.setAttribute("errorMessage",
//					"Error in generating marksheet.");
//		}

		return modelnView;
	}
	public List<PassFailExamBean> handleGraceMarksLogic(List<PassFailExamBean> studentMarksList){
		
		for (PassFailExamBean passFailBean : studentMarksList) {
			//Check if grace was applied after the year-month of requested marksheet
			//In such case, written marks and total should be subtracted with grace, which is coming from future exam wrt exam year-month of SR
			//E.g. Student request marksheet for Jun-15, and was given grace in Dec-15, then it should not appear in Jun-15 marksheet
			if("N".equalsIgnoreCase(passFailBean.getIsPass()) && StringUtils.isNotBlank(passFailBean.getGracemarks()) && !passFailBean.getWrittenscore().equalsIgnoreCase("AB")) {
				int writtenscore = Integer.parseInt(passFailBean.getWrittenscore());
				int gracemarks = Integer.parseInt(passFailBean.getGracemarks());
				int total = 0;
				try{
					total = Integer.parseInt(passFailBean.getTotal());
					passFailBean.setTotal((total - gracemarks ) + "");
				}catch(Exception e){
					passFailBean.setTotal("");
				}
				passFailBean.setWrittenscore((writtenscore - gracemarks ) + "");
				//passFailBean.setTotal((total - gracemarks ) + "");
				passFailBean.setGracemarks(null);
			}
		}
		return studentMarksList;
	}

	@RequestMapping(value = "/admin/certificateFromSapIdForm", method = {
			RequestMethod.GET, RequestMethod.POST })
	public ModelAndView certificateFromSapIdForm(Model m) {
		return new ModelAndView("certificateFromSapId");
	}

	@RequestMapping(value = "/admin/certificateFromSapId", method = {
			RequestMethod.GET, RequestMethod.POST })
	public ModelAndView certificateFromSapId(
			@RequestParam Map<String, String> inputs, Model m,HttpServletRequest request) {
		List<MarksheetBean> listOfStudents = new ArrayList<MarksheetBean>();
		HashMap<String, String> mapOfSapIDAndResultDate = new HashMap<String, String>();
		CertificatePDFCreator pdfCreator = new CertificatePDFCreator();
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		String sapidList = inputs.get("serviceRequestIdList");
		String isPassStudent = inputs.get("isPassStudent");
		String certificateNumberGenerated = "";
		try {
			listOfStudents = sDao
					.findStudentsFromCommaSeperatedSapid(sapidList);
			mapOfSapIDAndResultDate = dao
					.getMapOfSapIdAndResultDeclareDate(listOfStudents);
			
				certificateNumberGenerated = pdfCreators
						.generateParticipationCertificateAndReturnCertificateNumber(
								listOfStudents, mapOfSapIDAndResultDate, request,
								getProgramAllDetailsMap(), getCentersMap(),
								MARKSHEETS_PATH, SERVER_PATH,isPassStudent,STUDENT_PHOTOS_PATH);
		
			
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Certificate generated successfully. Please click Download link below to get file.");
			return certificateFromSapIdForm(m);
		} catch (Exception e) {
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating certificate." + e.getMessage());
			return certificateFromSapIdForm(m);
		}
		
	}

	@RequestMapping(value = "/admin/certificateFromSRForm", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String certificateFromSRForm(HttpServletRequest request,
			HttpServletResponse response, Model m) {

		PassFailExamBean bean = new PassFailExamBean();
		request.getSession().setAttribute("bean", bean);

		return "certificateFromSR";
	}
	@RequestMapping(value = "/admin/mbawxCertificateFromSRForm", method = {
			RequestMethod.GET})
	public String generateMbawxCertificateFromSRForm(HttpServletRequest request,
			HttpServletResponse response, Model m) {
		PassFailExamBean bean = new PassFailExamBean();
		request.getSession().setAttribute("bean", bean);

		return "mbawxCertificateFromSR"; 
	}
	
	
	
	@RequestMapping(value = "/admin/generateMbawxCertificateFromSR", method = RequestMethod.POST)
	public ModelAndView generateMbawxCertificateFromSR(HttpServletRequest request,
			HttpServletResponse response, @ModelAttribute PassFailExamBean bean) throws Exception {
		ModelAndView modelnView = new ModelAndView("mbawxCertificateFromSR");
		request.getSession().setAttribute("passFailSearchBean", bean);
        if(bean.getServiceRequestIdList()==null || bean.getServiceRequestIdList().equalsIgnoreCase("")) {
        	request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please add atleast 1 Service Request Id.");
			return modelnView;
        } 
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");

		List<MarksheetBean> studentForSRList = dao.getStudentsForSR(bean);
		HashMap<String, String> mapOfSapIDAndResultDate =null;
        if(bean.getProgram().equalsIgnoreCase("MBA - WX") || bean.getProgram().equalsIgnoreCase("PDDM")) {
		mapOfSapIDAndResultDate = dao
				.getMapOfSapIdAndResultDeclareDateForMbawx(studentForSRList);
        }else if(bean.getProgram().equalsIgnoreCase("MBA - X")) {
        mapOfSapIDAndResultDate = dao
    			.getMapOfSapIdAndResultDeclareDateForMbax(studentForSRList);	
        }
        else if(MSCAIML.equalsIgnoreCase(bean.getProgram()))
	    {
	       	List<String> sapidList = studentForSRList.stream().map(MarksheetBean::getSapid).collect(Collectors.toList());
	       	List<MarksheetBean> q8PassFailDetailList = dao.getResultDeclareDateForMSCAIStudents(sapidList);
	       	Map<String, String> hashOfSapIDAndResultDate = q8PassFailDetailList.stream().collect(Collectors.toMap(key -> key.getSapid(), value -> value.getResultDeclarationDate()));
	       	mapOfSapIDAndResultDate = new HashMap<>(hashOfSapIDAndResultDate);
	    }

		if (studentForSRList == null || studentForSRList.size() == 0 || mapOfSapIDAndResultDate.values()==null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}
		request.setAttribute("logoRequired", bean.getLogoRequired());
		CertificatePDFCreator pdfCreator = new CertificatePDFCreator();
		//find specialisations of student 
		SpecialisationDAO spDao = (SpecialisationDAO) act.getBean("specialisationDao");
		for(MarksheetBean srBean: studentForSRList) {
			if(mapOfSapIDAndResultDate.get(srBean.getSapid())==null) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found for student "+srBean.getSapid());
				return modelnView;
			}
			if(bean.getProgram().equalsIgnoreCase("PDDM")) {
				//currently PDDM not having Specialisation
			}else if(MSCAIML.equalsIgnoreCase(bean.getProgram())) {
				//currently M.Sc. (AI & ML Ops) not having Specialisation
			}else {
				String specialisation="";
				try {
					/*
					 * commented by harsh
					 * specialization was not ordered alphabetically, needed fix 
					 * based on card 9897
					Specialisation s = spDao.getSpecializationsOfStudent(srBean.getSapid());
					//single specialisation
					if(s.getSpecialisation1()!=null) {
						specialisation=s.getSpecialisation1();
					}
					//dual specialisation
					if(s.getSpecialisation2()!=null) {
						specialisation=specialisation+" & "+s.getSpecialisation2();
					} 
					*/
					specialisation = specialisationService.getSpecilizationNameBasedOnAlphabeticalOrder(srBean.getSapid());
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
				
				srBean.setSpecialisation(specialisation);
			}
		}
		try {
			String certificateNumberGenerated;
			if(bean.getProgram().equalsIgnoreCase("PDDM")) {
				certificateNumberGenerated = pdfCreators
						.generatePDDMCertificateAndReturnCertificateNumber(
								studentForSRList, mapOfSapIDAndResultDate, request,
								getProgramAllDetailsMap(), getCentersMap(),
								MARKSHEETS_PATH, PDDM_PROGRAMS_LIST,STUDENT_PHOTOS_PATH);
			}else if(MSCAIML.equalsIgnoreCase(bean.getProgram())){
				certificateNumberGenerated = pdfCreators
						.generateCertificateAndReturnCertificateNumberForMSCAI(
							studentForSRList, mapOfSapIDAndResultDate, request,
							getProgramAllDetailsMap(), getCentersMap(),
							MARKSHEETS_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH);
			}else {
				certificateNumberGenerated = pdfCreators
						.generateCertificateAndReturnCertificateNumber(
							studentForSRList, mapOfSapIDAndResultDate, request,
							getProgramAllDetailsMap(), getCentersMap(),
							MARKSHEETS_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH);
			}
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Certificate generated successfully. Please click Download link below to get file.");
			dao.updateSRWithCertificateNumberAndCurrentDate(bean,
					certificateNumberGenerated);

		} catch (DocumentException | IOException | ParseException e) {
			// TODO Auto-generated catch block
			finalCertificateLogger.error("Error in generating certificate for generateMbawxCertificateFromSR: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating certificate." + e.getMessage());
		}

		return modelnView;
	}
	
	
//	to be deleted
//	@RequestMapping(value = "/m/getSingleStudentCertificateMBAWX", method = { RequestMethod.POST }, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<LinkedInAddCertToProfileBean> getSingleStudentCertificateMBAWX(
//			@RequestBody PassFailBean passFailBean) throws Exception {
//		
//		CertificatePDFCreator pdfCreator = new CertificatePDFCreator();
//		
//		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
//		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
//		Format formatterMonth = new SimpleDateFormat("MM");
//		Format formatterYear = new SimpleDateFormat("yyyy");
//	    Format formatterDay = new SimpleDateFormat("dd");
//		MarksheetBean marksheetBean = dao.getSingleStudentsData(passFailBean);
//		
//		
//		String declareDate = null;
//		
//		try {
//			declareDate = dao
//					.getResultDeclareDateForSingleStudentMBAWX(marksheetBean);
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			
//		}
//		SpecialisationDAO spDao = (SpecialisationDAO) act.getBean("specialisationDao");
//
//		Specialisation s = spDao.getSpecializationsOfStudent(marksheetBean.getSapid());
//
//		String specialisation="";
//		//single specialisation
//		if(s.getSpecialisation1()!=null) {
//			specialisation=s.getSpecialisation1();
//		}
//		//dual specialisation
//		if(s.getSpecialisation2()!=null) {
//			specialisation=specialisation+" And "+s.getSpecialisation2();
//		} 
//		marksheetBean.setSpecialisation(specialisation);
//		
//		
//		
//		try {
//			String certificateNumberGenerated = pdfCreator
//					.generateCertificateAndReturnCertificateNumberForSingleStudentSelfMBWX(
//							marksheetBean, declareDate,
//							getProgramAllDetailsMap(), getCentersMap(),
//							CERTIFICATES_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH);
//			//request.setAttribute("success", "true");
//			//request.setAttribute(
//					//"successMessage",
//					//"Certificate generated successfully. Please click Download link below to get file.");
////			dao.updateSRWithCertificateNumberAndCurrentDateForSingleStudent(
////					passFailBean, certificateNumberGenerated);
//			
//			LinkedInAddCertToProfileBean response = new LinkedInAddCertToProfileBean();
//			response.setCertUrl(certificateNumberGenerated);
//			
//
//			response.setIssueMonth(formatterMonth.format(sd.parse(declareDate)));
//			response.setIssueYear(formatterYear.format(sd.parse(declareDate)));
//			response.setConsumerProgramStructureId(marksheetBean.getConsumerProgramStructureId());
//			String key = marksheetBean.getProgram()+"-"+marksheetBean.getPrgmStructApplicable();
//			ProgramBean programDetails = getProgramAllDetailsMap().get(key);
//			
//			String programname = programDetails.getProgramname();
//			String programType = programDetails.getProgramType();
//
//			if(programname.equals("MBA (WX) for Working Executives")) {
//				programname = "MASTER OF BUSINESS ADMINISTRATION (WORKING EXECUTIVES)";
//			
//				}else if (programname.equals("MBA (Executive) with specialisation in Business Analytics")){
//					programname = "MBA (EXECUTIVE) WITH SPECIALISATION IN BUSINESS ANALYTICS";				
//
//				}
//			if(programDetails.getProgram().equalsIgnoreCase("MBA - WX")) {
//				programType="MBA (Wx)";
//			}
//			response.setName(programname);
//			return new ResponseEntity<LinkedInAddCertToProfileBean>(  response , HttpStatus.ACCEPTED);
//
//		} catch (DocumentException | IOException | ParseException e) {
//			// TODO Auto-generated catch block
//			
//			//request.setAttribute("error", "true");
//			//request.setAttribute("errorMessage",
//				//	"Error in generating certificate." + e.getMessage());
//			return null;
//		}
//
//	//	modelnView.addObject("bean", passFailBean);
//		//return modelnView;
//	}
	
	
//	to be deleted
//	@RequestMapping(value = "/m/getSingleStudentCertificate", method = { RequestMethod.POST }, consumes = "application/json", produces = "application/json")
//	public ResponseEntity<LinkedInAddCertToProfileBean> getSingleStudentCertificate(
//			@RequestBody PassFailBean passFailBean) throws Exception {
//		
//		CertificatePDFCreator pdfCreator = new CertificatePDFCreator();
//		
//		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
//		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
//		Format formatterMonth = new SimpleDateFormat("MM");
//		Format formatterYear = new SimpleDateFormat("yyyy");
//	    Format formatterDay = new SimpleDateFormat("dd");
//		MarksheetBean marksheetBean = dao.getSingleStudentsData(passFailBean);
//		
//		
//		String declareDate = null;
//		
//		try {
//			declareDate = dao
//					.getResultDeclareDateForSingleStudent(marksheetBean);
//		} catch (Exception e1) {
//			
//		}		
//		
//		try {
//			String certificateNumberGenerated = pdfCreator
//					.generateCertificateAndReturnCertificateNumberForSingleStudentSelfMBWX(
//							marksheetBean, declareDate,
//							getProgramAllDetailsMap(), getCentersMap(),
//							CERTIFICATES_PATH, SERVER_PATH,STUDENT_PHOTOS_PATH);
//			//request.setAttribute("success", "true");
//			//request.setAttribute(
//					//"successMessage",
//					//"Certificate generated successfully. Please click Download link below to get file.");
////			dao.updateSRWithCertificateNumberAndCurrentDateForSingleStudent(
////					passFailBean, certificateNumberGenerated);
//			
//			LinkedInAddCertToProfileBean response = new LinkedInAddCertToProfileBean();
//			response.setCertUrl(certificateNumberGenerated);
//			
//
//			response.setIssueMonth(formatterMonth.format(sd.parse(declareDate)));
//			response.setIssueYear(formatterYear.format(sd.parse(declareDate)));
//			response.setConsumerProgramStructureId(marksheetBean.getConsumerProgramStructureId());
//			String key = marksheetBean.getProgram()+"-"+marksheetBean.getPrgmStructApplicable();
//			ProgramBean programDetails = getProgramAllDetailsMap().get(key);
//			
//			String programname = programDetails.getProgramname();
//			String programType = programDetails.getProgramType();
//			response.setName(programname);
//			return new ResponseEntity<LinkedInAddCertToProfileBean>(  response , HttpStatus.ACCEPTED);
//
//		} catch (DocumentException | IOException | ParseException e) {
//			// TODO Auto-generated catch block
//			
//			//request.setAttribute("error", "true");
//			//request.setAttribute("errorMessage",
//				//	"Error in generating certificate." + e.getMessage());
//			return null;
//		}
//
//	//	modelnView.addObject("bean", passFailBean);
//		//return modelnView;
//	}
	
//	Added for SAS
	@RequestMapping(value = "/student/studentSelfSASMarksheetForm", method = RequestMethod.GET)
	public String studentSelfSASMarksheetForm(HttpServletRequest request,
			HttpServletResponse response, Model m) {
		    StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		    getStudentMarksHistoryForExecutive(request, response);
		    PassFailExamBean bean = new PassFailExamBean();
			m.addAttribute("studentMarks", bean);
			m.addAttribute("subjectList", getAllApplicableSubjects(student.getProgram(),student.getPrgmStructApplicable()));
			m.addAttribute("yearList", ACAD_YEAR_SAS_LIST);
			m.addAttribute("monthList", SAS_EXAM_MONTH_LIST);
		return "studentSASSelfMarksheet";
	}
	
	@RequestMapping(value = "/student/studentSASSelfMarksheet", method = { RequestMethod.POST })
	public ModelAndView studentSASSelfMarksheet(HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute PassFailExamBean studentMarks) throws SQLException {
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		StudentExamBean student = (StudentExamBean)request.getSession().getAttribute("studentExam");
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		ModelAndView modelnView = new ModelAndView("studentSASSelfMarksheet");
		
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", ACAD_YEAR_SAS_LIST);
		modelnView.addObject("monthList", SAS_EXAM_MONTH_LIST);
		modelnView.addObject("subjectList", getSubjectList());
		String sapId = (String) request.getSession().getAttribute("userId");
		studentMarks.setSapid(sapId);

		boolean hasAppearedForExamForGivenSemMonthYear = dao
				.hasAppearedForExamForGivenSemMonthYearForExecutive(studentMarks);
		if (hasAppearedForExamForGivenSemMonthYear == false) {
			request.setAttribute("error", "true");
			request.setAttribute(
					"errorMessage",
					"You have not appeared for Semester "
							+ studentMarks.getSem() + " in Year "
							+ studentMarks.getWrittenYear() + " and month "
							+ studentMarks.getWrittenMonth());
			modelnView.addObject("studentMarks", studentMarks);
			return modelnView;
		}

		getStudentMarksHistoryForExecutive(request, response);

		request.getSession().setAttribute("passFailSearchBean", studentMarks);

		// Check if results are live
		MarksheetBean studentData = dao.getSingleStudentsData(studentMarks);

		String programStructure = studentData.getPrgmStructApplicable();
		String resultDeclarationDate = "";
		ExecutiveExamOrderBean exam = dao.getExamDetailsForExecutive(studentMarks.getWrittenMonth(),
				studentMarks.getWrittenYear(),studentData.getEnrollmentMonth(),studentData.getEnrollmentYear());
		if (exam == null) {
			request.setAttribute("error", "true");
			request.setAttribute(
					"errorMessage",
					"No Exam Bookings found for "
							+ studentMarks.getWrittenMonth() + "-"
							+ studentMarks.getWrittenYear()
							+ " Exam, Semester " + studentMarks.getSem());
			return modelnView;
		}
		String resultLive = exam.getResultLive();

			resultDeclarationDate = dao.getOnlineExamDeclarationDateForExecutive(
					studentMarks.getWrittenMonth(),
					studentMarks.getWrittenYear(),
					studentData.getEnrollmentMonth(),
					studentData.getEnrollmentYear());
			
		studentData.setResultDeclarationDate(resultDeclarationDate);
		if (!"Y".equalsIgnoreCase(resultLive)) {
			request.setAttribute("error", "true");
			request.setAttribute(
					"errorMessage",
					"Results are not yet announced for "
							+ studentMarks.getWrittenMonth() + "-"
							+ studentMarks.getWrittenYear() + " Exam cycle.");
			return modelnView;
		}

		HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudentForExecutive(
				studentMarks.getSem(), studentMarks.getSapid(),
				studentMarks.getWrittenMonth(), studentMarks.getWrittenYear());
		if (keysMap == null || keysMap.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}

		// ArrayList<PassFailBean> passFailStudentList = dao.process(keysMap);
		ArrayList<PassFailExamBean> passFailStudentList = null;

		passFailStudentList = dao.processNewForExecutive(keysMap);// discuss with sir 23may PS
		

		List<PassFailExamBean> studentMarksList = new ArrayList<>();
		// Take only those records where he has written or assignment year/month
		// is same one as selected on form.
		if (passFailStudentList != null && !passFailStudentList.isEmpty()) {
			studentMarksList.addAll(passFailStudentList);
		}


		HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
		String examMonth = studentMarks.getWrittenMonth();
		String examYear = studentMarks.getWrittenYear();

		CreatePDF helper = new CreatePDF();
		helper.generateSingleStudentMarksheetForExecutive(studentMarksList, studentData,
				examMonth, examYear, examOrderMap, getCentersMap());
		ArrayList<MarksheetBean> marksheetList = new ArrayList<>();
		marksheetList.add(studentData);

		try {
			helper.createStudentSelfMarksheetPDFForExecutive(marksheetList,
					resultDeclarationDate, request, getProgramMap(),
					getCentersMap(), MARKSHEETS_PATH, getModProgramMap());
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Marksheet generated successfully. Please click Download link below to get file.");

		} catch (Exception e) {
			logger.error("Error in generating marksheet of studentSASSelfMarksheet: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet." + e.getMessage());
		}
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("subjectList", getAllApplicableSubjects(student.getProgram(),student.getPrgmStructApplicable()));
		modelnView.addObject("yearList", ACAD_YEAR_SAS_LIST);
		return modelnView;
	}
	
	@RequestMapping(value = {"/student/generateMarksheetPreviewFromSR","/generateMarksheetPreviewFromSR"}, method = RequestMethod.POST, consumes= "application/json", produces = "application/json")
	public ResponseEntity<HashMap<String, List>> generateMarksheetPreviewFromSR(
		  @RequestBody MarksheetBean bean) throws UnsupportedEncodingException {
		
		
		HashMap<String, List> response = new  HashMap<String, List>();
		List error = 	new ArrayList<>();
		List errorMessage = 	new ArrayList<>();
		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		PassFailExamBean pbean = new PassFailExamBean();
		pbean.setSapid(bean.getSapid());
		ExamOrderExamBean exam = dao.getExamDetails(bean.getExamMonth(),
				bean.getExamYear());
		String resultLive = "N";
		if(exam!=null){
			if ("Online".equals(bean.getExamMode())) {
				resultLive = exam.getLive();
			}else{
				resultLive = exam.getOflineResultslive();
			}
		}else{
			error.add("true");
			errorMessage.add("No exam details found for "+bean.getExamMonth()+"- "+bean.getExamYear());
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		/*//sDao, studentData, isProjectApplicable, isModuleProjectApplicable code shifted inside if. Vilpesh on 2022-04-30
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		MarksheetBean studentData = dao.getSingleStudentsData(pbean);
		Boolean isProjectApplicable =  sDao.isProjectApplicable(bean.getProgram(),bean.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
		Boolean isModuleProjectApplicable =  sDao.isModuleProjectApplicable(bean.getProgram(),bean.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
		*/ //above 4 commented lines moved inside -if check of resultLive
		
		//One time fetch from REDIS, Vilpesh 20220722
		boolean hasAppearedForExamForGivenSemMonthYear;
		Map<String, Object> simpleMap = null;
		
		if("Y".equalsIgnoreCase(resultLive)){
			
			//shifted inside, One time fetch from REDIS, Vilpesh 20220722
			simpleMap = this.fetchResultsFromRedis(bean.getSapid());
			
			// To check if Student appeared for Exam from REDIS, Vilpesh on 20220722
			Boolean attempted = this.checkMarksHistoryForStudent(simpleMap, bean.getSem(), bean.getSapid(),
					bean.getExamMonth(), bean.getExamYear());
			
			if(null != attempted) {
				hasAppearedForExamForGivenSemMonthYear = attempted.booleanValue();
			} else {
				// Generate marksheet records
				hasAppearedForExamForGivenSemMonthYear = dao
						.hasAppearedForExamForGivenSemMonthYearPreviewMarksheet(bean,
								bean.getExamMode());
			}
			if (hasAppearedForExamForGivenSemMonthYear == false) {
				error.add("true");
				errorMessage.add("You have not appeared for  sem "+bean.getSem()+"- "+bean.getExamMonth()+"- "+bean.getExamYear());
				response.put("error", error);
				response.put("errorMessage", errorMessage);
				return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
			}
			
			List<PassFailExamBean> pFList = this.searchPassfail(simpleMap, bean.getSem(), bean.getSapid(),
					bean.getExamMonth(), bean.getExamYear());
			if (null != pFList && !pFList.isEmpty()) {
				logger.info("generateMarksheetPreviewFromSR : Found(REDIS) Passfail (SapId, Sem) (" + bean.getSapid()
						+ "," + bean.getSem() + ")");
				response.put("marks", pFList);
				response.put("resultSourcee", marksheetPreview_REDIS);//REDIS is :, indicates from where marksheet preview displayed.
				error.add("false");
				response.put("error", error);
				return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
			}
			
			HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudent(
							bean.getSem(), bean.getSapid(),
							bean.getExamMonth(), bean.getExamYear());
			
			if (keysMap == null || keysMap.size() == 0) {
				error.add("true");
				errorMessage.add("No marks records found for "+bean.getExamMonth()+"- "+bean.getExamYear());
				response.put("error", error);
				response.put("errorMessage", errorMessage);
				return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
			}
			
			StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
			MarksheetBean studentData = dao.getSingleStudentsData(pbean);
			Boolean isProjectApplicable =  sDao.isProjectApplicable(bean.getProgram(),bean.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
			Boolean isModuleProjectApplicable =  sDao.isModuleProjectApplicable(bean.getProgram(),bean.getSem(),studentData.getPrgmStructApplicable(),studentData.getSapid());
			
			ArrayList<PassFailExamBean> passFailStudentList = null;
//			if("ACBM".equalsIgnoreCase(studentData.getProgram())){
			boolean isBajaj = "Bajaj".equalsIgnoreCase(studentData.getConsumerType());
			boolean isDBMJul2014 = "Jul2014".equalsIgnoreCase(studentData.getPrgmStructApplicable()) && "DBM".equalsIgnoreCase(studentData.getProgram());
			if(isBajaj && !isDBMJul2014){
//				passFailStudentList = dao.processACBM(keysMap);
				passFailStudentList = dao.processBajaj(keysMap);
			} else {
				passFailStudentList = dao.processNew(keysMap);// Use new pass
			}
			List<PassFailExamBean> studentMarksList = new ArrayList<>();
			// Take only those records where he has written or assignment
			// year/month is same one as selected on form.
			if (passFailStudentList != null && !passFailStudentList.isEmpty()) {

				 if(isProjectApplicable.equals(true)) {
					  if(!keysMap.containsKey(bean.getSapid().trim()+"Project")) {
					  PassFailExamBean passFailBean = new PassFailExamBean();
					  passFailBean.setSapid(bean.getSapid());
					  passFailBean.setSubject("Project"); passFailBean.setSem(bean.getSem());
					  passFailBean.setProgram(bean.getProgram());
					  passFailBean.setName(bean.getFirstName()+" "+bean.getLastName());
					  passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
					  passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
					  passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
					  passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
					  }
					  
				  }
				 if(isModuleProjectApplicable.equals(true)) {
					  if(!keysMap.containsKey(bean.getSapid().trim()+"Module 4 - Project")) {
					  PassFailExamBean passFailBean = new PassFailExamBean();
					  passFailBean.setSapid(bean.getSapid());
					  passFailBean.setSubject("Module 4 - Project"); passFailBean.setSem(bean.getSem());
					  passFailBean.setProgram(bean.getProgram());
					  passFailBean.setName(bean.getFirstName()+" "+bean.getLastName());
					  passFailBean.setWrittenscore(""); passFailBean.setAssignmentscore("");
					  passFailBean.setWrittenYear(""); passFailBean.setWrittenMonth("");
					  passFailBean.setAssignmentYear(""); passFailBean.setAssignmentMonth("");
					  passFailBean.setTotal("--"); passFailStudentList.add(passFailBean); 
					  }
					  
				  }
				 
				studentMarksList.addAll(passFailStudentList);
				studentMarksList = handleGraceMarksLogic(studentMarksList);
			}
			
			//By Vilpesh on 2022-04-11, order Subject names. 
			if(null != studentMarksList) {
				List<PassFailExamBean> subjectListOrdered = this.orderSubject(studentMarksList);
				studentMarksList.clear();
				studentMarksList.addAll(subjectListOrdered);
			}
			
			response.put("marks", studentMarksList);
			error.add("false");
			response.put("error", error);
			
		}else{
			error.add("true");
			errorMessage.add("Result not live for "+bean.getExamMonth()+"- "+bean.getExamYear());
			response.put("error", error);
			response.put("errorMessage", errorMessage);
			return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
		}
		
		//Vilpesh on 20220722
		if(null != simpleMap) {
			simpleMap.clear();
			simpleMap = null;
		}
		return new ResponseEntity<HashMap<String, List>>(response, HttpStatus.OK);
	}

	/**
	 * Reorders subject names in alphabetically order.
	 * @param listBean
	 * @return
	 */
	protected List<PassFailExamBean> orderSubject(List<PassFailExamBean> listBean) {
		Set<String> setSubject;
		List<PassFailExamBean> listOrdered = null;
		List<PassFailExamBean> listOrderedSubjectWise = null;
		Map<String, List<PassFailExamBean>> mapSubjectResult = null;
		
		if(null != listBean && !listBean.isEmpty()) {
			//Distinct Subjects
			setSubject = listBean.stream().map(g -> g.getSubject()).collect(Collectors.toCollection(TreeSet::new));
			//logger.info("orderSubject : Distinct Subject(s): "+ setSubject.size());
			if(null != setSubject && !setSubject.isEmpty()) {
				//Create Map of each Subject and its list of beans
				mapSubjectResult = listBean.stream().collect(Collectors.groupingBy(h -> h.getSubject()));
				
				listOrderedSubjectWise = new LinkedList<PassFailExamBean>();
				for(String subject : setSubject) {
					listOrdered = mapSubjectResult.get(subject);
					listOrderedSubjectWise.addAll(listOrdered);
				}
				
				if(null != mapSubjectResult) {
					mapSubjectResult.clear();
				}
				setSubject.clear();
			}
		} else {
			listOrderedSubjectWise = listBean;
		}
		return listOrderedSubjectWise;
	}

	//code for custom marksheet for best of tee passed start
	@RequestMapping(value = "/admin/singleStudentCustomMarksheetForBestOfTeePassedForm", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String singleStudentCustomMarksheetForBestOfTeePassedForm(HttpServletRequest request,
			HttpServletResponse response, Model m) {
		logger.info("Add New Company Page");

		PassFailExamBean bean = new PassFailExamBean();
		m.addAttribute("studentMarks", bean);
		m.addAttribute("yearList", yearList);
		request.getSession().setAttribute("passFailSearchBean", bean);

		return "singleStudentCustomMarksheetForBestOfTeePassedForm";
	}
	

	@RequestMapping(value = "/admin/getSingleStudentCustomMarksheetForBestOfTeePassed", method = RequestMethod.POST)
	public ModelAndView getSingleStudentCustomMarksheetForBestOfTeePassed(HttpServletRequest request,
			HttpServletResponse response,
			@ModelAttribute PassFailExamBean studentMarks) throws SQLException {
		ModelAndView modelnView = new ModelAndView("singleStudentCustomMarksheetForBestOfTeePassedForm");
		modelnView.addObject("studentMarks", studentMarks);
		modelnView.addObject("programList", getProgramList());
		modelnView.addObject("yearList", yearList);
		modelnView.addObject("subjectList", getSubjectList());

		request.getSession().setAttribute("passFailSearchBean", studentMarks);

		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");

		// Check if result is live
		MarksheetBean studentData = dao.getSingleStudentsData(studentMarks);

		// ArrayList<String> errorList =
		// checkIfStudentsPresentInMasterDB(studentMarksList, studentMap);
		if (studentData == null) {
			request.setAttribute("error", "true");
			request.setAttribute(
					"errorMessage",
					"Student not found in Students Master Database. Please contact academic team to add these."
							+ " Without students master data marksheet cannot be generated. ");
			return modelnView;
		}

		String programStructure = studentData.getPrgmStructApplicable();
		String resultDeclarationDate = "";
		ExamOrderExamBean exam = dao.getExamDetails(studentMarks.getWrittenMonth(),
				studentMarks.getWrittenYear());
		String resultLive = "N";

		if ("Online".equals(studentData.getExamMode())) {
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
		studentData.setResultDeclarationDate(resultDeclarationDate);
		// Generate marksheet records
		HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudent(
				studentMarks.getSem(), studentMarks.getSapid(),
				studentMarks.getWrittenMonth(), studentMarks.getWrittenYear());
		if (keysMap == null || keysMap.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}
		
		try {
			keysMap = addTempBlankMarksBeanOfDec18(dao, keysMap, studentMarks.getSapid());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", e1.getMessage());
			return modelnView;
		}
		
		// ArrayList<PassFailBean> passFailStudentList =
		// dao.processNew(keysMap);

		ArrayList<PassFailExamBean> passFailStudentList = null;

//		if("ACBM".equalsIgnoreCase(studentData.getProgram())){
		boolean isBajaj = "Bajaj".equalsIgnoreCase(studentData.getConsumerType());
		boolean isDBMJul2014 = "Jul2014".equalsIgnoreCase(studentData.getPrgmStructApplicable()) && "DBM".equalsIgnoreCase(studentData.getProgram());
		if(isBajaj && !isDBMJul2014){
//			passFailStudentList = dao.processACBM(keysMap);
			passFailStudentList = dao.processBajaj(keysMap);
		} else {
			passFailStudentList = dao.processNew(keysMap);// Use new pass fail
															// logic
		}

		List<PassFailExamBean> studentMarksList = new ArrayList<>();
		// Take only those records where he has written or assignment year/month
		// is same one as selected on form.
		if (passFailStudentList != null && !passFailStudentList.isEmpty()) {

			studentMarksList.addAll(passFailStudentList);
		}


		HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
		String examMonth = studentMarks.getWrittenMonth();
		String examYear = studentMarks.getWrittenYear();

		CreatePDF helper = new CreatePDF();
		helper.generateSingleStudentMarksheet(studentMarksList, studentData,
				examMonth, examYear, examOrderMap, getCentersMap());
		ArrayList<MarksheetBean> marksheetList = new ArrayList<>();
		marksheetList.add(studentData);

		try {
			helper.createPDF(marksheetList, resultDeclarationDate, request,
					getProgramMap(), getCentersMap(), MARKSHEETS_PATH, getModProgramMap());
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Marksheet generated successfully. Please click Download link below to get file.");

		} catch (DocumentException | IOException e) {
			logger.error("Error in generating marksheet of getSingleStudentCustomMarksheetForBestOfTeePassed: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error in generating marksheet of getSingleStudentCustomMarksheetForBestOfTeePassed: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		}

		return modelnView;
	}
	
	private HashMap<String, ArrayList> addTempBlankMarksBeanOfDec18(PassFailDAO dao,HashMap<String, ArrayList> keysMap,String sapid) throws Exception{
		
		Map<String,ArrayList<PassFailExamBean>> passfailRecords =  dao.getPassFailRecordsWhereIsPassIsYAndOldIsPassStatusIsNBySapid(sapid);
		Iterator entries = keysMap.entrySet().iterator();
		for(int k = 0; k < keysMap.size(); k++){
			Entry thisEntry = (Entry) entries.next();
			String key = (String)thisEntry.getKey();
			ArrayList<StudentMarksBean> currentList = (ArrayList)thisEntry.getValue();
			boolean hasDec2018entry = false;
			
			if(passfailRecords.containsKey(key)) {
				for(StudentMarksBean b : currentList) {
					if("Dec2018".equalsIgnoreCase(b.getMonth()+b.getYear())) {
						hasDec2018entry = true;
						throw new Exception("There is record found for Dec 2018 subject :"+b.getSubject()+" sapid : "+b.getSapid()+". <br> "
											+ " Please generate marksheet from usual modules. ");
						
					}
				}

				if(!hasDec2018entry) {
					StudentMarksBean tempBean = new StudentMarksBean();
					tempBean.setSapid(currentList.get(0).getSapid());
					tempBean.setSubject(currentList.get(0).getSubject());
					tempBean.setYear("2018");
					tempBean.setMonth("Dec");
					tempBean.setWritenscore("");
					tempBean.setExamorder("21.00");
					currentList.add(tempBean);
					keysMap.put(key, currentList);
				}
			}
			
		}
		
		
		return keysMap;
	}
	
	@RequestMapping(value = "/admin/customMarksheetFromSRForBestOfTeePassedForm", method = {
			RequestMethod.GET, RequestMethod.POST })
	public String customMarksheetFromSRForBestOfTeePassedForm(HttpServletRequest request,
			HttpServletResponse response, Model m) {

		PassFailExamBean bean = new PassFailExamBean();
		request.getSession().setAttribute("bean", bean);

		return "customMarksheetFromSRForBestOfTeePassed";
	}

	@RequestMapping(value = "/admin/generateCustomMarksheetFromSRForBestOfTeePassed", method = RequestMethod.POST)
	public ModelAndView generateCustomMarksheetFromSRForBestOfTeePassed(HttpServletRequest request,
			HttpServletResponse response, @ModelAttribute PassFailExamBean bean) {
		ModelAndView modelnView = new ModelAndView("customMarksheetFromSRForBestOfTeePassed");
		request.getSession().setAttribute("passFailSearchBean", bean);

		PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");

		List<MarksheetBean> studentForSRList = dao.getStudentsForSR(bean);

		if (studentForSRList == null || studentForSRList.size() == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
			return modelnView;
		}

		ArrayList<MarksheetBean> marksheetList = new ArrayList<>();
		CreatePDF helper = new CreatePDF();
		StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
		for (MarksheetBean studentData : studentForSRList) {

			String programStructure = studentData.getPrgmStructApplicable();
			String resultDeclarationDate = "";
			ExamOrderExamBean exam = dao.getExamDetails(studentData.getMonth(),
					studentData.getYear());
			String resultLive = "N";

			if ("Online".equals(studentData.getExamMode())) {
				resultDeclarationDate = dao.getOnlineExamDeclarationDate(
						studentData.getMonth(), studentData.getYear());
				resultLive = exam.getLive();
			} else {
				resultDeclarationDate = dao.getOfflineExamDeclarationDate(
						studentData.getMonth(), studentData.getYear());
				resultLive = exam.getOflineResultslive();
			}
			studentData.setResultDeclarationDate(resultDeclarationDate);

			// Generate marksheet records
			HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudent(
					studentData.getSem(), studentData.getSapid(),
					studentData.getMonth(), studentData.getYear());
			if (keysMap == null || keysMap.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
				return modelnView;
			}

			try {
				keysMap = addTempBlankMarksBeanOfDec18(dao, keysMap, studentData.getSapid());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", e1.getMessage());
				return modelnView;
			}
			// ArrayList<PassFailBean> passFailStudentList =
			// dao.processNew(keysMap);
			ArrayList<PassFailExamBean> passFailStudentList = null;

//			if("ACBM".equalsIgnoreCase(studentData.getProgram())){
			boolean isBajaj = "Bajaj".equalsIgnoreCase(studentData.getConsumerType());
			boolean isDBMJul2014 = "Jul2014".equalsIgnoreCase(studentData.getPrgmStructApplicable()) && "DBM".equalsIgnoreCase(studentData.getProgram());
			if(isBajaj && !isDBMJul2014){
//				passFailStudentList = dao.processACBM(keysMap);
				passFailStudentList = dao.processBajaj(keysMap);
			} else {
				passFailStudentList = dao.processNew(keysMap);// Use new pass
																// fail logic
			}

			List<PassFailExamBean> studentMarksList = new ArrayList<>();
			// Take only those records where he has written or assignment
			// year/month is same one as selected on form.
			if (passFailStudentList != null && !passFailStudentList.isEmpty()) {

				studentMarksList.addAll(passFailStudentList);
				studentMarksList = handleGraceMarksLogic(studentMarksList);
				
			}


			HashMap<String, BigDecimal> examOrderMap = sDao.getExamOrderMap();
			String examMonth = studentData.getMonth();
			String examYear = studentData.getYear();

			helper.generateSingleStudentMarksheet(studentMarksList,
					studentData, examMonth, examYear, examOrderMap,
					getCentersMap());

			marksheetList.add(studentData);
		}
		Collections.sort(marksheetList);

		try {
			helper.createPDF(marksheetList, null, request, getProgramMap(),
					getCentersMap(), MARKSHEETS_PATH, getModProgramMap());
			request.setAttribute("success", "true");
			request.setAttribute(
					"successMessage",
					"Marksheet generated successfully. Please click Download link below to get file.");
			/* dao.updateSRStatus(bean, "In Progress"); */
		} catch (DocumentException | IOException e) {
			// TODO Auto-generated catch block
			logger.error("Error in generating marksheet of generateCustomMarksheetFromSRForBestOfTeePassed: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error in generating marksheet of generateCustomMarksheetFromSRForBestOfTeePassed: "+e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage",
					"Error in generating marksheet.");
		}

		return modelnView;
	}
	//code for custom marksheet for best of tee passed end
	
	
	//Start
		@RequestMapping(value = "/admin/executiveCertificatesForBatchForm", method = {
				RequestMethod.GET, RequestMethod.POST })
		public String executiveCertificatesForBatchForm(HttpServletRequest request,
				HttpServletResponse response, Model m) {
	
			PassFailExamBean bean = new PassFailExamBean();
			m.addAttribute("bean", bean);
	
			m.addAttribute("yearList", yearList);
			m.addAttribute("monthList", SAS_EXAM_MONTH_LIST);
			return "executiveCertificatesForBatchForm";
		}
	
		@RequestMapping(value = "/admin/executiveCertificatesForBatch", method = RequestMethod.POST)
		public ModelAndView executiveCertificatesForBatch(HttpServletRequest request,
				HttpServletResponse response, @ModelAttribute PassFailExamBean bean) {
			ModelAndView modelnView = new ModelAndView("executiveCertificatesForBatchForm");
			request.getSession().setAttribute("passFailSearchBean", bean);
			modelnView.addObject("yearList", yearList);
			modelnView.addObject("monthList", SAS_EXAM_MONTH_LIST);
			modelnView.addObject("bean", bean);
	
			PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
			
			// Getting student detalis as per year-month-program in bean
			List<MarksheetBean> studentForSRList = dao.getStudentsDetailsByYearMonthProgramForExecutive(bean);
			
			if (studentForSRList == null || studentForSRList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
				return modelnView;
			}
			
			studentForSRList = checkIsPassForExecutive(studentForSRList);
			
			HashMap<String, String> mapOfSapIDAndResultDate = dao
					.getMapOfSapIdAndResultDeclareDate(studentForSRList);
			if (studentForSRList == null || studentForSRList.size() == 0) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "No records found.");
				return modelnView;
			}
	
			CertificatePDFCreator pdfCreator = new CertificatePDFCreator();
	
			try {
				String certificateNumberGenerated = pdfCreators
						.generateCertificateAndReturnCertificateNumber(
								studentForSRList, mapOfSapIDAndResultDate, request,
								getProgramAllDetailsMap(), getCentersMap(),
								MARKSHEETS_PATH, SERVER_PATH, STUDENT_PHOTOS_PATH);
				request.setAttribute("success", "true");
				request.setAttribute(
						"successMessage",
						"Certificate generated successfully. Please click Download link below to get file.");
				//dao.updateSRWithCertificateNumberAndCurrentDate(bean,
			//		certificateNumberGenerated);
	
			//} catch (DocumentException | IOException | ParseException  e) {
			} catch ( Exception e) {
				// TODO Auto-generated catch block
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",
						"Error in generating certificate." + e.getMessage());
		}
			return modelnView;
		}
		
		public List<MarksheetBean> checkIsPassForExecutive(List<MarksheetBean> studentForSRList) {
			PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
			
			for(MarksheetBean bean : studentForSRList) {
				
				//check if not passed in normal subjects 
				boolean isPassedInNormalSubjects = dao.checkIsPassedInNormalSubjects(bean) ; // return true if passed in all subjects
				
				//check if not passed in case study
				boolean isPassedInCaseStudy;
				
				if("EPBM".equalsIgnoreCase(bean.getProgram())) {
					isPassedInCaseStudy = dao.checkIsPassedInCaseStudy(bean) ; // return true if passed in all subjects
				}else {
					isPassedInCaseStudy = true; // will be true as MPDV does not have casestudy
				}
				
				if(isPassedInNormalSubjects && isPassedInCaseStudy) {
					bean.setPassForExceutive(true);
				}else {
					bean.setPassForExceutive(false);
				}
			}
			
			return studentForSRList;
		}
		
		//End
		
		
		//SAS final Marksheet start
			@RequestMapping(value = "/admin/marksheetFromSRForSASForm", method = 	RequestMethod.GET)
			public String marksheetFromSRForSASForm(HttpServletRequest request,
					HttpServletResponse response, Model m) {
		
				PassFailExamBean bean = new PassFailExamBean();
				request.getSession().setAttribute("bean", bean);
		
				return "marksheetFromSRForSAS";
			}
		
			@RequestMapping(value = "/admin/generateMarksheetFromSRForSAS", method = RequestMethod.POST)
			public ModelAndView generateMarksheetFromSRForSAS(HttpServletRequest request,
				HttpServletResponse response, @ModelAttribute PassFailExamBean bean) {
				ModelAndView modelnView = new ModelAndView("marksheetFromSR");
				request.getSession().setAttribute("passFailSearchBean", bean);
		
			PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
		
				List<MarksheetBean> studentForSRList = dao.getStudentsForSR(bean);
		
				if (studentForSRList == null || studentForSRList.size() == 0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No records found.");
					return modelnView;
				}
		
				ArrayList<MarksheetBean> marksheetList = new ArrayList<>();
				CreatePDF helper = new CreatePDF();
				StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
				for (MarksheetBean studentData : studentForSRList) {
					if(!"EPBM".equalsIgnoreCase(studentData.getProgram()) && !"MPDV".equalsIgnoreCase(studentData.getProgram()) ) {
						continue;
					}
					String programStructure = studentData.getPrgmStructApplicable();
					String resultDeclarationDate = "";
					ExecutiveExamOrderBean exam = dao.getExamDetailsForExecutive(studentData.getMonth(),
																				 studentData.getYear(),
																				 studentData.getEnrollmentMonth(),
																				 studentData.getEnrollmentYear());
					if (exam == null) {
						request.setAttribute("error", "true");
						request.setAttribute(
											"errorMessage",
											"No Exam Details Found for "
											+ " Month/Year "+studentData.getMonth()+"/"+studentData.getYear()+" "
											+ "and Acads Month/Year"+studentData.getEnrollmentMonth()+"/"+studentData.getEnrollmentYear()+""
											);
						return modelnView;
					}String resultLive = "N";
		
					if ("Online".equals(studentData.getExamMode())) {
						resultDeclarationDate = dao.getOnlineExamDeclarationDateForExecutive(
								studentData.getMonth(),
								studentData.getYear(),
								studentData.getEnrollmentMonth(),
								studentData.getEnrollmentYear());
						
						resultLive = exam.getResultLive();
					} else {
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Student cannot be Offline.");
						return modelnView;
					}
					studentData.setResultDeclarationDate(resultDeclarationDate);
		
					// Generate marksheet records
					HashMap<String, ArrayList> keysMap = dao.getMarksRecordsForStudentForExecutive(
							studentData.getSem(), studentData.getSapid(),
							studentData.getMonth(), studentData.getYear());
					if (keysMap == null || keysMap.size() == 0) {
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "No records found.");
						return modelnView;
					}
		
					// ArrayList<PassFailBean> passFailStudentList =
					// dao.processNew(keysMap);
					ArrayList<PassFailExamBean> passFailStudentList = null;
		
					passFailStudentList = dao.processNewForExecutive(keysMap);
						
		
					List<PassFailExamBean> studentMarksList = new ArrayList<>();
					// Take only those records where he has written or assignment
					// year/month is same one as selected on form.
					if (passFailStudentList != null && !passFailStudentList.isEmpty()) {
		
						studentMarksList.addAll(passFailStudentList);
						
						//commmented as sas will not have grace 
						//studentMarksList = handleGraceMarksLogic(studentMarksList);
						
					}
		
		
					HashMap<String, BigDecimal> examOrderMap = sDao.getExecutiveExamOrderMap();
					String examMonth = studentData.getMonth();
					String examYear = studentData.getYear();
		
					studentData = helper.generateSingleStudentMarksheetForExecutive(studentMarksList,
							studentData, examMonth, examYear, examOrderMap,
							getCentersMap());
		
					marksheetList.add(studentData);
				}
				Collections.sort(marksheetList);
		
				try {
					helper.createMarksheetPDFForSAS(marksheetList, null, request, getProgramMap(),
							getCentersMap(), MARKSHEETS_PATH, getModProgramMap());
					request.setAttribute("success", "true");
					request.setAttribute(
							"successMessage",
							"Marksheet generated successfully. Please click Download link below to get file.");
					/* dao.updateSRStatus(bean, "In Progress"); */
				} catch (DocumentException | IOException e) {
					// TODO Auto-generated catch block
					logger.error("Error in generating marksheet of generateMarksheetFromSRForSAS: "+e);
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage",
							"Error in generating marksheet.");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Error in generating marksheet of generateMarksheetFromSRForSAS: "+e);
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage",
							"Error in generating marksheet.");
				}
		
				return modelnView;
			}
			//SAS final Marksheet end
			
			//Executive Bulk Marks Sheet //
				
				@RequestMapping(value = "/admin/downloadExecutiveMarksheetForm", method = RequestMethod.GET)
				public ModelAndView downloadExecutiveMarksheetForm(HttpServletRequest request,
						HttpServletResponse response,@ModelAttribute PassFailExamBean bean) {
			
					ModelAndView modelAndView = new ModelAndView("executiveMarksheet");
				modelAndView.addObject("bean", bean);
					modelAndView.addObject("yearList", yearList);
					modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
					return modelAndView;
				}
				
				@RequestMapping(value = "/admin/downloadExecutiveMarksheet", method = RequestMethod.POST)
				public ModelAndView downloadExecutiveMarksheet(HttpServletRequest request,
						HttpServletResponse response,@ModelAttribute PassFailExamBean bean) {
					
					
					ModelAndView modelAndView = new ModelAndView("executiveMarksheet");
					modelAndView.addObject("bean", bean);
					modelAndView.addObject("yearList", yearList);
					modelAndView.addObject("monthList", SAS_EXAM_MONTH_LIST);
					
					PassFailDAO dao = (PassFailDAO) act.getBean("passFailDAO");
					StudentMarksDAO sDao = (StudentMarksDAO) act.getBean("studentMarksDAO");
							CreatePDF helper = new CreatePDF();
							HashMap<String, BigDecimal> examOrderMap = sDao.getExecutiveExamOrderMap();
							ArrayList<MarksheetBean> marksheetList = new ArrayList<>();
					HashMap<String,ArrayList<PassFailExamBean>> studentMarksMap = dao.getExecutiveStudentsPassFailMarks(bean.getProgram(),bean.getBatchMonth(),bean.getBatchYear());
					if("EPBM".equalsIgnoreCase(bean.getProgram())){
						for(Entry<String,ArrayList<PassFailExamBean>> e : studentMarksMap.entrySet()){
							String sapid = e.getKey();
							//if student not attempted for eminer then display -- on marksheet
							ArrayList<String> subjectsAttemptedByStudent = new ArrayList<String>();
							ArrayList<PassFailExamBean> pfbeanList = e.getValue();
							for(PassFailExamBean pf : pfbeanList){
								if(!subjectsAttemptedByStudent.contains(pf.getSubject())) {
									subjectsAttemptedByStudent.add(pf.getSubject());
								}
							}
							PassFailExamBean eMinerBean = new PassFailExamBean();
							if(!subjectsAttemptedByStudent.contains("Enterprise Miner")){
								eMinerBean.setSapid(sapid);
								eMinerBean.setTotal("-");
								eMinerBean.setSubject("Enterprise Miner");
								eMinerBean.setWrittenscore("-");
								eMinerBean.setAssignmentscore("-"); 
								eMinerBean.setGracemarks("-"); 
								eMinerBean.setWrittenMonth("-");
								eMinerBean.setWrittenYear("-");
								eMinerBean.setProgram(bean.getProgram());
								eMinerBean.setSem(e.getValue().get(0).getSem());
								eMinerBean.setIsPass("N");
								studentMarksMap.get(sapid).add(eMinerBean);
							}
							
							
							
							PassFailExamBean caseStudyMarksBean = dao.getSingleExecutiveStudentsCaseStudyMarks(sapid);
							if(caseStudyMarksBean == null || StringUtils.isBlank(caseStudyMarksBean.getTotal())){
								caseStudyMarksBean.setSapid(sapid);
									caseStudyMarksBean.setTotal("-");
									caseStudyMarksBean.setSubject("Case Study");
									caseStudyMarksBean.setWrittenscore("-");
									caseStudyMarksBean.setAssignmentscore("-"); 
									caseStudyMarksBean.setGracemarks("-"); 
									caseStudyMarksBean.setWrittenMonth("-");
									caseStudyMarksBean.setWrittenYear("-");
									caseStudyMarksBean.setProgram(bean.getProgram());
									caseStudyMarksBean.setSem(e.getValue().get(0).getSem());
									caseStudyMarksBean.setIsPass("N");
							}else{
								caseStudyMarksBean.setSubject("Case Study");
								caseStudyMarksBean.setWrittenscore("-");
								caseStudyMarksBean.setAssignmentscore("-"); 
								caseStudyMarksBean.setGracemarks("-"); 
								caseStudyMarksBean.setWrittenMonth("-");
								caseStudyMarksBean.setWrittenYear("-");
								caseStudyMarksBean.setProgram(bean.getProgram());
								caseStudyMarksBean.setSem(e.getValue().get(0).getSem());
								if(Integer.parseInt(caseStudyMarksBean.getTotal()) >= 50){
									caseStudyMarksBean.setIsPass("Y");
								}else{
									caseStudyMarksBean.setIsPass("N");
								}
							}
							studentMarksMap.get(sapid).add(caseStudyMarksBean);
						}
						
						
					}
					for(Entry<String,ArrayList<PassFailExamBean>> b : studentMarksMap.entrySet()){
									String sapid = b.getKey();
									MarksheetBean studentBean = dao.getExecutiveStudentForMarksheet(sapid);
									if("MPDV".equalsIgnoreCase(bean.getProgram())){
														String resultDeclarationDate = "";
														ExecutiveExamOrderBean exam = dao.getExamDetailsForExecutive(bean.getMonth(),
																bean.getYear(),
																bean.getBatchMonth(),
																bean.getBatchYear());
														if (exam == null) {
															request.setAttribute("error", "true");
															request.setAttribute(
																				"errorMessage",
																				"No Exam Details Found for "
																				+ " Month/Year "+bean.getMonth()+"/"+bean.getYear()+" "
																				+ "and Acads Month/Year"+bean.getBatchMonth()+"/"+bean.getBatchYear()+""
																				);
															return modelAndView;
														}String resultLive = "N";
										
															resultDeclarationDate = dao.getOnlineExamDeclarationDateForExecutive(
																	bean.getMonth(),
																	bean.getYear(),
																	bean.getBatchMonth(),
																	bean.getBatchYear());
															
															resultLive = exam.getResultLive();
														
															studentBean.setResultDeclarationDate(resultDeclarationDate);
										
													}
									if("EPBM".equalsIgnoreCase(bean.getProgram())){
											studentBean.setResultDeclarationDate("28-May-2019");
									}
									studentBean = helper.generateSingleStudentMarksheetForExecutive(b.getValue(),
											studentBean,bean.getMonth(), bean.getYear(), examOrderMap,
											getCentersMap());
									marksheetList.add(studentBean);
								}
								
								try {
									helper.createMarksheetPDFForSAS(marksheetList, null, request, getProgramMap(),
											getCentersMap(), MARKSHEETS_PATH, getModProgramMap());
									request.setAttribute("success", "true");
									request.setAttribute(
											"successMessage",
											"Marksheet generated successfully. Please click Download link below to get file.");
									/* dao.updateSRStatus(bean, "In Progress"); */
								} catch (DocumentException | IOException e) {
									// TODO Auto-generated catch block
									logger.error("Error in generating marksheet of downloadExecutiveMarksheet: "+e);
									request.setAttribute("error", "true");
									request.setAttribute("errorMessage",
											"Error in generating marksheet.");
								} catch (Exception e) {
									// TODO Auto-generated catch block
									logger.error("Error in generating marksheet of downloadExecutiveMarksheet: "+e);
									request.setAttribute("error", "true");
									request.setAttribute("errorMessage",
											"Error in generating marksheet.");
								}
					return modelAndView;
				}
				
				
				//SAS final Marksheet end
				
	/* MBA WX Marksheets start */
	
	 /*
	  * Commented by Ashutosh on 22-11-2019
	  * Use the m/studentSelfMarksheetForSemMonthYear API for marksheet downloads.
	  	@RequestMapping(value = "/m/studentSelfMarksheet", method = { RequestMethod.POST }) 
		public ResponseEntity<EmbaMarksheetBean> mStudentSelfMarksheet(HttpServletRequest request, @RequestBody EmbaPassFailBean studentMarks) throws SQLException { 
			 HttpHeaders headers = new HttpHeaders(); headers.add("Content-Type", "application/json");
			 EmbaMarksheetBean response = new EmbaMarksheetBean(); 
			 try { 
				 response = msService.mStudentSelfMarksheetForMbaWx(studentMarks);
				 return new ResponseEntity<EmbaMarksheetBean>(response, headers, HttpStatus.OK); 
			 } catch(Exception e) {
				 return new ResponseEntity<EmbaMarksheetBean>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR); 
			 }
		 }
	  */
	 
				

//	to be deleted
//	@RequestMapping(value = "/m/studentSelfMarksheetForSemMonthYear", method = { RequestMethod.POST })
//	public ResponseEntity<EmbaMarksheetBean> mstudentSelfMarksheetForSemMonthYear(
//			HttpServletRequest request, @RequestBody MBAWXExamResultForSubject studentMarks
//	) throws SQLException {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		EmbaMarksheetBean response = new EmbaMarksheetBean();
//		try {
//			response = msService.mStudentSelfMarksheetForTermMonthYearForMbaWx(studentMarks);
//
//						return new ResponseEntity<EmbaMarksheetBean>(response, headers, HttpStatus.OK);
//					}
//					catch(Exception e) {
//						return new ResponseEntity<EmbaMarksheetBean>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
//					}
//					
//				}
				
				
//				to be deleted
//				@RequestMapping(value = "/m/sRPaymentSuccess", method = { RequestMethod.POST })
//				public String mbaExamBookingCallback(HttpServletRequest request, Model model, Model m) {
//
//					//if(request.getSession().getAttribute("embaBookingRequest") == null) {
//					//	model.addAttribute("status", "fail");
//					//	model.addAttribute("message", "Invlid or empty request!");
//						return "redirect:" + examBookingSuccessURLWeb;
////					} else {
////						MBAWXExamBookingRequest bookingRequest = (MBAWXExamBookingRequest) request.getSession().getAttribute("embaBookingRequest");
////						if(bookingRequest.getSource().equalsIgnoreCase("webapp")) {
////							if(bookingRequest.getPaymentOption() != null) { 
////								model.addAttribute("paymentOption", bookingRequest.getPaymentOption());
////							}
////							if(bookingRequest.getSelectedSubjects() != null) {
////								model.addAttribute("timeboundIds", bookingRequest.getSelectedSubjects());
////							}
////							if(bookingRequest.getSapid() != null) {
////								model.addAttribute("sapid", bookingRequest.getSapid());
////							}
////							if(bookingRequest.getTrackId() != null) {
////								model.addAttribute("trackId", bookingRequest.getTrackId());
////							}
////						}
////						return parseExamBookingResponse(request, model, bookingRequest);
////					}
//				}
				
				
//				to be deleted
//				@RequestMapping(value = "/m/sRStatusFailure", method = { RequestMethod.POST })
//				public String mbaExamBookingCallbackFail(HttpServletRequest request, Model model, Model m) {
//
//					//if(request.getSession().getAttribute("embaBookingRequest") == null) {
//					//	model.addAttribute("status", "fail");
//					//	model.addAttribute("message", "Invlid or empty request!");
//						return "redirect:" + examBookingErrorURLWeb	;
////					} else {
////						MBAWXExamBookingRequest bookingRequest = (MBAWXExamBookingRequest) request.getSession().getAttribute("embaBookingRequest");
////						if(bookingRequest.getSource().equalsIgnoreCase("webapp")) {
////							if(bookingRequest.getPaymentOption() != null) { 
////								model.addAttribute("paymentOption", bookingRequest.getPaymentOption());
////							}
////							if(bookingRequest.getSelectedSubjects() != null) {
////								model.addAttribute("timeboundIds", bookingRequest.getSelectedSubjects());
////							}
////							if(bookingRequest.getSapid() != null) {
////								model.addAttribute("sapid", bookingRequest.getSapid());
////							}
////							if(bookingRequest.getTrackId() != null) {
////								model.addAttribute("trackId", bookingRequest.getTrackId());
////							}
////						}
////						return parseExamBookingResponse(request, model, bookingRequest);
////					}
//				}
				

				
	/* MBA WX Marksheets End */
//	to be deleted
//	@RequestMapping(value = "/m/mbaXstudentSelfMarksheetForSemMonthYear", method = { RequestMethod.POST })
//	public ResponseEntity<EmbaMarksheetBean> mbaXstudentSelfMarksheetForSemMonthYear(
//			HttpServletRequest request, @RequestBody MBAWXExamResultForSubject studentMarks
//	) throws SQLException {
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		EmbaMarksheetBean response = new EmbaMarksheetBean();
//		try {
//			response = msService.mStudentSelfMarksheetForTermMonthYearForMbaX(studentMarks);
//
//			return new ResponseEntity<EmbaMarksheetBean>(response, headers, HttpStatus.OK);
//		} catch(Exception e) {			
//			
//			response.setSubjects(null);
//			response.setError(true);
//			return new ResponseEntity<EmbaMarksheetBean>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		
//	}
				
	/* MBA X Marksheets End */
				
	public List<EmbaPassFailBean> getResultForDissertation(Set<String> dissertationSem, boolean semFlag, Set<String> errorList){
		List<EmbaPassFailBean> passFailSapidForDissertation  =  new ArrayList<EmbaPassFailBean>();
		
		//list of sapid and sem will iterate
		dissertationSem.stream().map(student -> student.split("-")).forEach(studentData->{
			String dissertationSapid = studentData[0];
			int sem = Integer.parseInt(studentData[1]);
		
			if (IA_MASTER_DISSERTATION_Q7_SEM == sem) {
				//check added for that sapid and sem that data is already fetch or not 
				boolean dublicateSapid = passFailSapidForDissertation.stream()
						.filter(studentDetails -> dissertationSapid.equalsIgnoreCase(studentDetails.getSapid())
								&& sem == Integer.parseInt(studentDetails.getSem())).findFirst().isPresent();
				
				//semFlag is for the student result is live for all the subject in sem 7 rather than master disstertaton
				if (semFlag && !dublicateSapid) {
					EmbaPassFailBean passFailForQ7 = gradSheetService.getPassFailForQ7(dissertationSapid);
					if (null != passFailForQ7 && "Y".equalsIgnoreCase(passFailForQ7.getIsResultLive())
							&& !StringUtils.isEmpty(passFailForQ7.getGrade())
							&& !StringUtils.isEmpty(passFailForQ7.getPoints())) {
						passFailForQ7.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q7_SEM));
						passFailSapidForDissertation.add(passFailForQ7);
					}else {
						errorList.add(dissertationSapid+"-"+sem);
					}
				}else {
					return;
				}
			}
			
			if (IA_MASTER_DISSERTATION_Q8_SEM == sem) {
				
				//check added for that sapid and sem that data is already fetch or not 
				boolean dublicateSapid = passFailSapidForDissertation.stream()
				.filter(studentDetails -> dissertationSapid.equalsIgnoreCase(studentDetails.getSapid())
						&& IA_MASTER_DISSERTATION_Q7_SEM == Integer.parseInt(studentDetails.getSem())).findFirst().isPresent();
				
				//semFlag is for the student result is live for all the subject in sem 7 rather than master disstertaton
				if (semFlag && !dublicateSapid) {
					//getting Q7 Result is retreiving in Q8 because of calculation of CGPA
					EmbaPassFailBean passFailForQ7 = gradSheetService.getPassFailForQ7(dissertationSapid);
					if (null != passFailForQ7 && "Y".equalsIgnoreCase(passFailForQ7.getIsResultLive())
							&& !StringUtils.isEmpty(passFailForQ7.getGrade())
							&& !StringUtils.isEmpty(passFailForQ7.getPoints())) {
						passFailForQ7.setSem(String.valueOf(IA_MASTER_DISSERTATION_Q7_SEM));

						passFailSapidForDissertation.add(passFailForQ7);
					}
				}
				//getting Q8 Result
				EmbaPassFailBean passFailForQ8 = gradSheetService.getPassFailForQ8(dissertationSapid);
				if (null != passFailForQ8 && "Y".equalsIgnoreCase(passFailForQ8.getIsResultLive())

						&& !StringUtils.isEmpty(passFailForQ8.getGrade())
						&& !StringUtils.isEmpty(passFailForQ8.getPoints())) {

					passFailSapidForDissertation.add(passFailForQ8);
				}else {
					errorList.add(dissertationSapid+"-"+sem);
				}
			
			}else {
				return;
			}

		});
		
		return passFailSapidForDissertation;
	}
				
}




