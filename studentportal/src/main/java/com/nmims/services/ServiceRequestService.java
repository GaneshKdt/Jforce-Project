package com.nmims.services;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.nmims.abstracts.ServiceRequestAbstract;
import com.nmims.assembler.ObjectConverter;
import com.nmims.assembler.ObjectDifferenceCopier;
import com.nmims.beans.EBonafidePDFContentRequestBean;
import com.nmims.beans.PassFailBean;
import com.nmims.beans.ProgramSubjectMappingStudentPortalBean;
import com.nmims.beans.ProgramsStudentPortalBean;
import com.nmims.beans.ReRegistrationStudentPortalBean;
import com.nmims.beans.ResponseStudentPortalBean;
import com.nmims.beans.ServiceRequestCustomPDFContentBean;
import com.nmims.beans.ServiceRequestDocumentBean;
import com.nmims.beans.ServiceRequestResponse;
import com.nmims.beans.ServiceRequestStudentPortal;
import com.nmims.beans.ServiceRequestType;
import com.nmims.beans.StudentMarksBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.controllers.HomeController;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.MassUploadTrackingSRDAO;
import com.nmims.daos.PortalDao;
import com.nmims.daos.ServiceRequestDao;
import com.nmims.daos.StudentDAO;
import com.nmims.dto.ChangeDetailsSRDto;
import com.nmims.dto.SrAdminUpdateDto;
import com.nmims.dto.StudentSrDTO;
import com.nmims.enums.ProfileDetailEnum;
import com.nmims.enums.ServiceRequestTypeEnum;
import com.nmims.exception.RecordNotExistException;
import com.nmims.factory.ChangeDetailsSRStrategyFactory;
import com.nmims.factory.ExitSrApplicableFactory;
import com.nmims.factory.ServiceRequestFactory;
import com.nmims.factory.SubjectRepeatSRFactory;
import com.nmims.helpers.AWSHelper;
import com.nmims.helpers.CertificatePDFCreator;
import com.nmims.helpers.CreatePDF;
import com.nmims.helpers.DateTimeHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.ResultsFromRedisHelper;
import com.nmims.helpers.SFConnection;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.interfaces.ExitSrApplicableInterface;
import com.nmims.interfaces.SubjectRepeatSR;
import com.nmims.interfaces.VerifyContactDetailsInterface;
import com.nmims.publisher.IdCardEventPublisher;
import com.nmims.repository.ServiceRequestRepository;
import com.nmims.stratergies.ChangeDetailsSRStrategyInterface;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

@Service("servReqServ")
public class ServiceRequestService {
	@Autowired
	private VerifyContactDetailsInterface verifyOtp;
	
	@Autowired
	private ServiceRequestFactory serviceRequestFactory;
	
	@Autowired
	private ChangeDetailsSRStrategyFactory changeDetailsSRStrategyFactory;
	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	private ServiceRequestDao serviceRequestDao;
	
	@Autowired
	private StudentDAO studentDAO;
	
	@Autowired
	private CreatePDF createPdfHelper;
	@Autowired
	private ContentDAO cDao;
	
	@Autowired
	private MailSender mailer;

	@Autowired
	private PortalDao portalDao;
	
	@Autowired
	private AWSHelper awsHelper;
	
	@Autowired
	ExitSrApplicableFactory  exitSrApplicableFactory;

	@Autowired
	MassUploadTrackingSRDAO massUploadTrackingSRDAO;
	
	@Autowired
	HomeController homeController;
	
	@Autowired
	IdCardEventPublisher eventPublisher;
	
	@Autowired
	IdCardService idCardService;
	

	
	@Value("${SECURE_SECRET}")
	private String SECURE_SECRET; // secret key;
	@Value("${ACCOUNT_ID}")
	private String ACCOUNT_ID;
	@Value("${V3URL}") 
	private String V3URL;
	@Value("${SR_RETURN_URL}")
	private String SR_RETURN_URL;
	@Value("${SR_RETURN_URL_MOBILE}")
	private String SR_RETURN_URL_MOBILE;
	@Value("${ADHOC_PAYMENT_RETURN_URL}") // AdhocpaymentReturn Url
	private String ADHOC_PAYMENT_RETURN_URL;
	@Value("${SERVICE_REQUEST_FILES_PATH}")
	private String SERVICE_REQUEST_FILES_PATH;
	@Value("${MBAWX_TERM_REPEAT_SR_CHARGES}")
	private String MBAWX_TERM_REPEAT_SR_CHARGES;

	@Value("${SR_TRACKING_LIST}")
	private List<String> SR_TRACKING_LIST;

	@Value("${AWS_SR_FILES_BUCKET}")
	private String srBucket;
	
	@Value("${CURRENT_ACAD_YEAR}")
	private String ACAD_YEAR;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String ACAD_MONTH;
	
	private ArrayList<String> yearList = new ArrayList<String>(
			Arrays.asList("2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019"));
	
	private final String[] srTypeInSrHistory = {"Subject Repeat M.Sc. AI and ML Ops", "Term De-Registration", "Exit Program", "Subject Repeat MBA - WX", 
			"Program Withdrawal","Issuance of Gradesheet", "Issuance of Transcript", "Issuance of Final Certificate", "Issuance of Marksheet"};
	
	private static final List<String> serviceRequestStatusList = Arrays.asList("Cancelled", "Closed", "In Progress", "Payment Failed", "Payment Pending", "Submitted");
	
	private static final String ISSUANCE_OF_BONAFIDE_FOLDER = "IssuanceOfBonafide/";
	private static final String SR_FILES_TEST_FOLDER = "ServiceRequestFilesTest/";
	private static final String EBONAFIDE_SR_DOCUMENT_TYPE = "SR E-Bonafide";
	
	public static final String RECORD_PURPOSE = "Record Purpose";
	public static final String OFFICIAL_PURPOSE = "Official Purpose";
	public static final String SCHOLARSHIP_PURPOSE = "Scholarship Purpose";
	public static final String LOAN_PURPOSE = "Loan Purpose";
	public static final String VISA_PURPOSE = "VISA Purpose";
	public static final String OTHER_PURPOSE = "Others : ";
	
	private static final String ScribeResume = "ScribeResume/";
	
	private static final String PARAGRAPH_ONE = "paragraphOne";
	private static final String PARAGRAPH_TWO = "paragraphTwo";
	
	private final int SECOND_MARKSHEET_FEE_PER_SUBJECT = 500;
	private final long MAX_FILE_SIZE = 5242880;
	private final int SECOND_DIPLOMA_FEE = 1000;

	private static final Logger logger = LoggerFactory.getLogger(ServiceRequestService.class);
	private static final Logger exitSrClosed = LoggerFactory.getLogger("exitSrClosed");
	
	public static final String SOFTSKILLS_PROGRAM_BBA = "BBA";
	public static final String SOFTSKILLS_PROGRAM_BCOM = "B.Com";
	public static final String SOFTSKILLS_PROGRAM_BBA_BA = "BBA-BA";
	private static final String MedicalCertificate = "SpecialNeedSR/";
	@Value("${CURRENT_MBAWX_ACAD_MONTH}")
	private String CURRENT_MBAWX_ACAD_MONTH;
	
	@Value("${CURRENT_MBAWX_ACAD_YEAR}")
	private String CURRENT_MBAWX_ACAD_YEAR;
	
	@Value( "${SFDC_USERID}" )
	private String SFDC_USERID;
	
	@Value( "${SFDC_PASSWORD_TOKEN}" )
	private String SFDC_PASSWORD_TOKEN;
	
	private PartnerConnection connection;
	
	@Value("${CURRENT_MBAX_ACAD_YEAR}")
	private String CURRENT_MBAX_ACAD_YEAR;
	
	@Value("${CURRENT_MBAX_ACAD_MONTH}")
	private String CURRENT_MBAX_ACAD_MONTH;
	
	@Value("${ENVIRONMENT}")
	private String ENVIRONMENT;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	@Value("${AWS_SR_FILES_BUCKET}")
	private String AWS_SR_FILES_BUCKET;
	
	@Value("${SR_FILES_S3_PATH}")
	private String SR_FILES_S3_PATH;
	
	@Value("${BBA_ELECTIVES_SEMESTER}")
	private List<String> BBA_ELECTIVES_SEMESTER;
	
	@Autowired
	SFConnection sfc;
	@Autowired
	ApplicationContext act;
	
	@Autowired
	SalesforceHelper salesforceHelper;
	
	@Autowired
	SubjectRepeatSRFactory subjectRepeatSRFactory;
	
	private final static int IA_MASTER_DISSERTATION_MASTER_KEY = 131;
	
	private final static int IA_MASTER_DISSERTATION_SEM_FOR_Q8 = 8;
	
	//Certificate programs consumerProgramStructureIds list. 
	public static final List<Integer> MASTER_KEY_LIST = (List<Integer>) Arrays.asList(142,143,144,145,146,147,148,149);
    
	private HashMap<String,StudentStudentPortalBean> getNewProgramMapped=null;
	
	private HashMap<String,StudentStudentPortalBean> getNewProgramMapped(){	
		
		if(this.getNewProgramMapped==null) {
			HashMap<String,StudentStudentPortalBean> mappedNewProgram=new HashMap<String,StudentStudentPortalBean>();
			ArrayList<StudentStudentPortalBean>getListOfProgramMapped=serviceRequestDao.getMappedNewMasterKey();
			for(StudentStudentPortalBean bean:getListOfProgramMapped) {
				mappedNewProgram.put(bean.getConsumerProgramStructureId(), bean);
			}
			this.getNewProgramMapped=mappedNewProgram;
		}	
		return getNewProgramMapped;
	}
	
	public ServiceRequestService() {  
		//init(); 
	}
	public void init(){
		SFConnection sf= new SFConnection(SFDC_USERID,SFDC_PASSWORD_TOKEN);
		this.connection = SFConnection.getConnection();
	}
	private ArrayList<String> yearListForMBAWX = new ArrayList<String>(
			Arrays.asList("2019", "2020"));
	
	
	public ArrayList<PassFailBean> getPassedYearMonthList(StudentStudentPortalBean student){
		
		//check student's program for  sems & subject count per sems
		ArrayList<ProgramSubjectMappingStudentPortalBean> SemSubjectCountMap = serviceRequestDao.getSemSubjectCountMapping(student);
		
		ArrayList<PassFailBean> passedSems = new ArrayList();

		boolean readFromCache = Boolean.FALSE;
		Map<String, Object> dataMap = null;
		try {
			readFromCache = this.fetchRedisHelper().readFromCache();
			if (readFromCache) {
				dataMap = this.fetchOnlyPassfail(student.getSapid());
				if (null != dataMap && dataMap.size() == 1) {
					Object val = null;
					Set<String> keySet = dataMap.keySet();
					for (String key : keySet) {
						val = dataMap.get(key);
						if (null == val) {
							readFromCache = Boolean.FALSE;// Not in REDIS, so refetch from DB 
							logger.info("ServiceRequestService: getPassedYearMonthList : SapId : " + student.getSapid()
									+ ", Not in REDIS, so refetch from DB continued.");
						} else if (null != val && (val instanceof List && ((List) val).isEmpty())) {
							readFromCache = Boolean.FALSE;// Empty in REDIS, so refetch from DB
							logger.info("ServiceRequestService: getPassedYearMonthList : SapId : " + student.getSapid()
									+ ", Empty in REDIS, so refetch from DB continued.");
						}
					}
				} else {
					readFromCache = Boolean.FALSE;
					logger.info("ServiceRequestService: getPassedYearMonthList : SapId : " + student.getSapid()
							+ ", Wrong way Data stored in REDIS, so refetch from DB continued.");
				}
			} else {
				logger.info("ServiceRequestService: getPassedYearMonthList : SapId : " + student.getSapid()
						+ ", fetch from DB continued.");
			}
		} catch(Exception ex) {
			logger.error("ServiceRequestService: getPassedYearMonthList : error : "+ ex.getMessage());
			//if REDIS stopped - exception caught - page loading continued -Vilpesh on 2022-04-23
			readFromCache = Boolean.FALSE;
		}
		
		//check passfail table to find if all subjects cleared 
		//for each sem
		for (ProgramSubjectMappingStudentPortalBean programBean : SemSubjectCountMap) {
			PassFailBean passedSubjectsBean = null;
			if(readFromCache) {
				passedSubjectsBean = this.fetchExamYearForSem(programBean.getSem(),student.getSapid(), dataMap);
			} else {
				passedSubjectsBean = serviceRequestDao.getPassedSubjectCount(programBean.getSem(),student.getSapid());
			}
			logger.info(
					"ServiceRequestService : getPassedYearMonthList : (Sem, Subjects to Pass, Subjects Passed, Program) : ("
							+ programBean.getSem() + ", " + programBean.getSubjectsCount() + ", "
							+ passedSubjectsBean.getCount() + ", " + student.getProgram() + ")");
			
			//if number of subjects in a sem=passed subjects
			if(passedSubjectsBean.getCount() == Integer.parseInt(programBean.getSubjectsCount()) ) {
				//student cleared this sem
				passedSems.add(passedSubjectsBean);
			} else if ((null != student.getProgram())
					&& (student.getProgram().equals(SOFTSKILLS_PROGRAM_BBA)
							|| student.getProgram().equals(SOFTSKILLS_PROGRAM_BCOM)
							|| student.getProgram().equals(SOFTSKILLS_PROGRAM_BBA_BA))
					&& (passedSubjectsBean.getCount() + 1 == Integer.parseInt(programBean.getSubjectsCount()))
					&& (programBean.getSem().equals("1") || programBean.getSem().equals("2")
							|| programBean.getSem().equals("3"))) {
				// for SoftSkill Subjects, 1 subject is in remarkpassfail.passfail, so we will
				// always get 1 less subject() irrespective of student passed or failed
				// non-softskill subjects. So if Student has passed all non-softskill
				// subjects, his year-month must come in dropdown. Vilpesh 20220719 
				passedSems.add(passedSubjectsBean);
				logger.info(
						"ServiceRequestService : getPassedYearMonthList : Added as Program(BBA,B.Com,BBA-BA) and passed all non Softskill subjects");
			}else if (student.getProgram().equals(SOFTSKILLS_PROGRAM_BBA)
					&& (programBean.getSem().equals("5") || programBean.getSem().equals("6"))
					&& passedSubjectsBean.getCount() + 2 == Integer.parseInt(programBean.getSubjectsCount())){
				passedSems.add(passedSubjectsBean);
				logger.info(
						"ServiceRequestService : getPassedYearMonthList : Added as Program(BBA,B.Com,BBA-BA) and passed all subjects for semester 5,6");
			}
		}
		
		if(readFromCache) {
			if(null != dataMap) {
				dataMap.clear();
			}
		}
		
		return passedSems;
	}	
	
	protected ResultsFromRedisHelper fetchRedisHelper() {
		ResultsFromRedisHelper resultsFromRedisHelper = null;
		resultsFromRedisHelper = (ResultsFromRedisHelper) act.getBean("resultsFromRedisHelper");
		return resultsFromRedisHelper;
	}

	protected Map<String, Object> fetchOnlyPassfail(final String sapId) {
		Map<String, Object> simpleMap = null;
		simpleMap = this.fetchRedisHelper().fetchOnlyPassfail(ResultsFromRedisHelper.EXAM_STAGE_TEE, sapId);
		return simpleMap;
	}
	
	protected PassFailBean fetchExamYearForSem(final String sem, final String sapId, Map<String, Object> dataMap) {
		String highestYearMonth = null;
		Integer subjPassed = null;
		Map<Integer, Set<String>> yearMonthMap = null;
		Set<String> yearMonthSet = null;
		Set<Integer> keySet = null;
		PassFailBean bean = null;
		//logger.info("ServiceRequestService : fetchExamYearForSem : (Sem, SapId) : (" + sem + ", " + sapId + ")");
		yearMonthMap = this.fetchRedisHelper().fetchYearMonthFromPassfailForSem(sem, dataMap);

		bean = new PassFailBean();
		bean.setSem(sem);
		if (null != yearMonthMap) {
			keySet = yearMonthMap.keySet();
			for (Integer key : keySet) {
				subjPassed = key;
				yearMonthSet = yearMonthMap.get(subjPassed);
				if (null != yearMonthSet && !yearMonthSet.isEmpty()) {
					highestYearMonth = DateTimeHelper.findHighestYearMonth(yearMonthSet,
							DateTimeHelper.FORMAT_YEAR_DASH_MONTH);// yyyy-MMM
				} else if (null != yearMonthSet && yearMonthSet.isEmpty()) {
					logger.info("ServiceRequestService : fetchExamYearForSem : (no subjects found passed for) Sem : " + sem);
				}
			}

			if (null != highestYearMonth) {
				bean.setCount(subjPassed);
				bean.setWrittenYear(highestYearMonth.substring(0, 4));// extract 2020 from 2020-Dec
				bean.setWrittenMonth(highestYearMonth.substring(5));// extract Dec from 2020-Dec
			} else {
				bean.setCount(0);
			}
		} else {
			bean.setCount(0);
		}
		return bean;
	}
	
	public ServiceRequestStudentPortal checkMarksheetHistory(ServiceRequestStudentPortal sr, HttpServletRequest request) {
		String serviceRequestType = sr.getServiceRequestType();
		//boolean paymentRequired = false;
		String sapId = sr.getSapId();
		
		//System.out.println("getWantAtAddress-->"+sr.getWantAtAddress());
		if("Yes".equals(sr.getWantAtAddress()) && sr.getWantAtAddress()!=null){
			//System.out.println("if of want at add---------");
			sr.setIssued("Y");
			sr.setModeOfDispatch("Courier");
			String postalAddressForShipment = sr.getPostalAddress();
			sr.setPostalAddress(postalAddressForShipment);
			sr.setCourierAmount(100+"");
		}else{
			//System.out.println("else of want at add---------");
			sr.setIssued("N");
			sr.setModeOfDispatch("LC");
			sr.setCourierAmount(0+"");
		}
		//System.out.println("Mode Of Dispatch--->"+sr.getModeOfDispatch());
		//System.out.println("page parameter" + request.getParameter("postalAddress"));
		//System.out.println("sapId"+sapId);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapId);
		//System.out.println("student"+student);
		//boolean isCertificate = isStudentOfCertificate(student.getProgram());
		boolean isCertificate = student.isCertificateStudent();
		
		//modelnView.addObject("student", student);
		String resultDeclareDateOnline, resultDeclareDateOffline;
		String examMode = "";
		if ("Online".equalsIgnoreCase(student.getExamMode())) {
			examMode = "Online";

		} else {
			examMode = "Offline";

		}
		//System.out.println("PARAMETERS FROM PAGE.......");
		//System.out.println(sr.getMarksheetDetailRecord1());
		//System.out.println(sr.getMarksheetDetailRecord2());
		//System.out.println(sr.getMarksheetDetailRecord3());
		//System.out.println(sr.getMarksheetDetailRecord4());
		//System.out.println("PARAMETERS FROM PAGE END.......");
		
		ArrayList<String> messageToBeShownForSemesterAppeared = serviceRequestDao.getSubjectsAppearedForSemesterMessageList(sr, examMode);//This will show an error message if the student has not appeared for a sem for given year and month//
		int subjectsAppeared = serviceRequestDao.getSubjectsAppeared(sr, examMode);//If he has given any exam//
		ArrayList<String> messageToShowForResultDeclareDate = serviceRequestDao.resultDeclaredMessage(sr);//Check if the result has been declared for the particular year,month and sem//
		ArrayList<String> marksheetsPrinted = serviceRequestDao.getMarksheetPrintedCount(sr);//Check number of marksheets issued and printed//
		
		//System.out.println("subjectsAppeared = " + subjectsAppeared);
		//System.out.println("messageToBeShownForSemesterAppeared"+messageToBeShownForSemesterAppeared.size());
		//System.out.println("messageToShowForResultDeclareDate"+messageToShowForResultDeclareDate.size());
		
		//Check if result is declared.
		if (messageToShowForResultDeclareDate!=null && messageToShowForResultDeclareDate.size()>0) {
			String messageForResultDeclaration = "";
			for(String message : messageToShowForResultDeclareDate){
				if(!"Yes".equals(message)){
					messageForResultDeclaration = message + "\n";
				}
			}
			sr.setError(messageForResultDeclaration+"Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
			//System.out.println("1st if:::::::::::::::::::::");
			sr.setYearList(yearList);
			return sr;
		}
		//Check if marksheets are already printed and kept at LC//
		if(marksheetsPrinted!=null && marksheetsPrinted.size()>0){
			String messageForPrintedMarksheets = "";
			for(String message : marksheetsPrinted){
				
				messageForPrintedMarksheets = message + "\n";
				}
			sr.setError(messageForPrintedMarksheets+" :- This marksheet is already printed, kindly connect with your Academic co-ordinator, if you wish to have this couriered then email us at <a href=\"#\"> ngasce@nmims.edu</a>. Please re-raise marksheet for other semesters by clicking on this <a href=\"addSRForm\"> LINK</a> ?");
			sr.setYearList(yearList);
			//System.out.println("2nd if:::::::::::::::::::::");
			return sr;
			}
			
		
		//Will check if any subjects were appeared by student//
		if (subjectsAppeared == 0) {
			sr.setError("There are no marks entries with respect to your SAPID"
					+ " Exam for you OR Results not yet declared.Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
			
			sr.setYearList(yearList);
			//System.out.println("3rd if:::::::::::::::::::::");
			return sr;
		}
		//Print the message for the semester appeared or not appeared//
		if(messageToBeShownForSemesterAppeared!=null && messageToBeShownForSemesterAppeared.size()>0){
			String messageToBeShown = "";
			for(String message : messageToBeShownForSemesterAppeared){
				messageToBeShown = message +"\n";  
			}
			sr.setError(messageToBeShown+"Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
			sr.setYearList(yearList);
			//System.out.println("4th if:::::::::::::::::::::");
			return sr;
		}
		
		String courierAmount = isCertificate ? generateAmountBasedOnCriteria(sr.getCourierAmount(),"GST") : sr.getCourierAmount();
		ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaid(sr,SECOND_MARKSHEET_FEE_PER_SUBJECT,request,isCertificate);//Map the service bean with semester and set in session.Returns a map which will show the description on marksheet summary.//
		sr.setMarksheetDetailAndAmountToBePaidList(marksheetDetailAndAmountToBePaidList);
		
		int  amountToBeDisplayedForMarksheetSummary= 0 ;
		for(int i =0;i<marksheetDetailAndAmountToBePaidList.size();i++){
			ServiceRequestStudentPortal serviceBean = marksheetDetailAndAmountToBePaidList.get(i);
			
			amountToBeDisplayedForMarksheetSummary = amountToBeDisplayedForMarksheetSummary+
					Integer.parseInt(serviceBean.getAmountToBeDisplayedForMarksheetSummary() );
			
		}
		amountToBeDisplayedForMarksheetSummary =amountToBeDisplayedForMarksheetSummary+ Integer.parseInt(sr.getCourierAmount());
		////System.out.println("AMOUNT FROM SESSION-->"+amountToBeDisplayedForMarksheetSummary);
		sr.setTotalAmountToBePayed(Integer.toString(amountToBeDisplayedForMarksheetSummary));
		return sr;
	}
	
	public ServiceRequestStudentPortal confirmMarksheetRequest(ServiceRequestStudentPortal sr) {
		boolean isDuplicate = false;
		List<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = sr.getMarksheetDetailAndAmountToBePaidList();
		for(int i =0;i<marksheetDetailAndAmountToBePaidList.size();i++){
			ServiceRequestStudentPortal serviceBean = marksheetDetailAndAmountToBePaidList.get(i);
			
			if("Duplicate".equals(serviceBean.getAdditionalInfo1())){
				isDuplicate = true; //Set to duplicate for showing doucument on marksheet Confirmation page//
			}
		}
		
		sr.setDuplicateMarksheet(isDuplicate);
		return sr;
	}
	public ServiceRequestStudentPortal saveMarksheetAndPayment(ServiceRequestStudentPortal sr, MultipartFile firCopy, MultipartFile indemnityBond) {
		logger.info("Inside saveMarksheetAndPayment");
		String sapid = sr.getSapId();
		////System.out.println("sapid::::::"+sapid);
		//StudentBean student = serviceRequestDao.getSingleStudentsData(sapid);
		//student.setAddress(studentAddress);// Setting address since it was
											// failing for case 77114000467
		
		List<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = sr.getMarksheetDetailAndAmountToBePaidList();
		String trackIdForMultipleMarksheets = sapid+System.currentTimeMillis(); //Since if we set this value in populateServicebean in the loop,the trackId does not remain unique since the loop runs and some time is lost//
		//set trackid in session (remaining)
		String totalFeesForMarksheetPayment = sr.getTotalAmountToBePayed();
		////System.out.println("totalFeesForMarksheetPayment::::::"+totalFeesForMarksheetPayment);
		ArrayList<ServiceRequestStudentPortal> listOfServiceRequests = new ArrayList<ServiceRequestStudentPortal>();
		//String CourierAmount = sr.getCourierAmount();
	
		for(int i =0;i<marksheetDetailAndAmountToBePaidList.size();i++){
			ServiceRequestStudentPortal serviceBean = marksheetDetailAndAmountToBePaidList.get(i);
			////System.out.println(serviceBean.toString());
			serviceBean.setAmount(totalFeesForMarksheetPayment);
			serviceBean.setPaymentOption(sr.getPaymentOption());
			serviceBean.setDevice(sr.getDevice());
			insertMarksheetForPayment(serviceBean,sapid,trackIdForMultipleMarksheets,marksheetDetailAndAmountToBePaidList.size());
			if("Duplicate".equals(serviceBean.getAdditionalInfo1())){
				String documentInsertMessage = insertServiceRequestDocumentAndReturnMessage(firCopy,indemnityBond,serviceBean,sapid);
				if(!"Success".equals(documentInsertMessage)){
					return confirmMarksheetRequest(sr);
				}
			}
			listOfServiceRequests.add(serviceBean);
		}
		ServiceRequestStudentPortal service = listOfServiceRequests.get(0);
		service.setPostalAddress(listOfServiceRequests.get(0).getPostalAddress());
		service.setAmount(totalFeesForMarksheetPayment);
		service.setTrackId(trackIdForMultipleMarksheets);  
		
		return service;
	}
	public ServiceRequestStudentPortal saveMarksheetAndPaymentForMBAWX(ServiceRequestStudentPortal sr, HttpServletRequest request) {

		String sapid = sr.getSapId();
		//System.out.println("sapid::::::"+sapid);
	
		String trackIdForMultipleMarksheets = sapid+System.currentTimeMillis(); //Since if we set this value in populateServicebean in the loop,the trackId does not remain unique since the loop runs and some time is lost//
		
		String totalFeesForMarksheetPayment = sr.getTotalAmountToBePayed();
		//System.out.println("totalFeesForMarksheetPayment::::::"+totalFeesForMarksheetPayment);

		sr.setAmount(totalFeesForMarksheetPayment);
		sr.setTrackId(trackIdForMultipleMarksheets); 
		return sr;
	}
	
	public ServiceRequestStudentPortal saveMarksheetAndPaymentDocsForMBAWX(ServiceRequestStudentPortal sr, HttpServletRequest request, 
			MultipartFile firCopy, MultipartFile indemnityBond, int listSize ) {

		String sapid = sr.getSapId();
		if(("").equalsIgnoreCase(sr.getTrackId()) || sr.getTrackId()==null) {
			String trackIdForMultipleMarksheets = sapid+System.currentTimeMillis(); 
			sr.setTrackId(trackIdForMultipleMarksheets);			
		}
		//System.out.println("saveMarksheetAndPaymentDocs----sr--"+sr);
	
		ServiceRequestStudentPortal serviceBean = sr;
		serviceBean.setAmount(sr.getAmount());
		insertMarksheetForPayment(serviceBean,sapid,sr.getTrackId(),listSize);
		if("Duplicate".equals(serviceBean.getAdditionalInfo1())){
			String documentInsertMessage = insertServiceRequestDocumentAndReturnMessage(firCopy,indemnityBond,serviceBean,sapid);
			if(!"Success".equals(documentInsertMessage)){
				sr.setDuplicateMarksheet(true);
			}
		}

		return sr;
	}
//	public ServiceRequest saveMarksheetRequest(ServiceRequest sr, HttpServletRequest request) {
//		String sapid = sr.getSapId();
//		//System.out.println("sapid:::::::::::::::::::::::"+sapid);
//		StudentBean student = serviceRequestDao.getSingleStudentsData(sapid);
//		//System.out.println("student::::::::"+student);
//		ArrayList<ServiceRequest> listOfServiceRequestInserted = new ArrayList<ServiceRequest>();
//		ArrayList<ServiceRequest> marksheetDetailAndAmountToBePaidList;
//		if (checkSession(request)) {
//			 marksheetDetailAndAmountToBePaidList = (ArrayList<ServiceRequest>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
//			sr.setMarksheetDetailAndAmountToBePaidList(marksheetDetailAndAmountToBePaidList);
//		}else {
//			boolean isCertificate = student.isCertificateStudent();
//			
//			marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaid(sr,SECOND_MARKSHEET_FEE_PER_SUBJECT,request,isCertificate);//Map the service bean with semester and set in session.Returns a map which will show the description on marksheet summary.//
//			//System.out.println("marksheetDetailAndAmountToBePaidList"+marksheetDetailAndAmountToBePaidList);
//			
//		}
//		
//		for (ServiceRequest serviceRequest : marksheetDetailAndAmountToBePaidList) {
//        
//			int countOfRecords = serviceRequestDao.getMarksheetIssuedCount(serviceRequest);
//			//System.out.println("countOfRecords:::::"+countOfRecords);		
//			if (countOfRecords > 0) {
//				
//				sr.setError("Free Marksheet request already received for " + serviceRequest.getMonth() + "-"
//						+ serviceRequest.getYear() + " : Sem : " + serviceRequest.getSem());
//				return sr;
//			}
//		}
//		for (int i = 0; i < marksheetDetailAndAmountToBePaidList.size(); i++) {
//		
//			ServiceRequest service = new ServiceRequest();
//			service = insertMarksheetServiceRequest(marksheetDetailAndAmountToBePaidList.get(i), request,
//					marksheetDetailAndAmountToBePaidList.size());// Passing size to method to flag marksheet only if the
//																	// size is greater than 1//
//			listOfServiceRequestInserted.add(service);
//
//		}
//         //System.out.println("listOfServiceRequestInserted::::::"+listOfServiceRequestInserted);
//		if (listOfServiceRequestInserted != null && listOfServiceRequestInserted.size() > 0) {
//			//System.out.println("inserted");
//			//ServiceRequest srBean = new ServiceRequest();
//			sr.setServiceRequestType(listOfServiceRequestInserted.get(0).getServiceRequestType());
//			StringBuilder serviceDescription = new StringBuilder();
//			StringBuilder serviceRequestId = new StringBuilder();
//
//			if (listOfServiceRequestInserted.size() > 1) {
//				for (ServiceRequest service : listOfServiceRequestInserted) {
//					serviceDescription.append(service.getDescription()).append(" ,");
//					serviceRequestId.append(service.getId()).append(" ,");
//				}
//				sr.setDescriptionList(
//						serviceDescription.toString().substring(0, serviceDescription.toString().length() - 1));
//				sr.setSrIdList(serviceRequestId.toString().substring(0, serviceRequestId.toString().length() - 1));
//
//			} else {
//				//System.out.println("not inserted");
//				for (ServiceRequest service : listOfServiceRequestInserted) {
//					serviceDescription.append(service.getDescription());
//					serviceRequestId.append(String.valueOf(service.getId()));
//				}
//				sr.setDescription(serviceDescription.toString());
//				sr.setId(Long.parseLong(serviceRequestId.toString()));
//			}
//			
//		}
//
//		MailSender mailer = (MailSender) act.getBean("mailer");
//
//		
//		//ServiceRequest response = servReqServ.saveMarksheetRequest(sr);
//		return sr;
//	}
	
	public ServiceRequestStudentPortal saveMarksheetRequest(ServiceRequestStudentPortal sr, HttpServletRequest request) {
		
		try {
		
		String sapid = sr.getSapId();
		System.out.println("sapid:::::::::::::::::::::::"+sapid);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		System.out.println("student::::::::"+student);
		ArrayList<ServiceRequestStudentPortal> listOfServiceRequestInserted = new ArrayList<ServiceRequestStudentPortal>();
		ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList;
		System.out.println("sr.getMarksheetDetailAndAmountToBePaidList()--"+sr.getMarksheetDetailAndAmountToBePaidList().get(0));
		
		
		
		if (sr.getMarksheetDetailAndAmountToBePaidList() == null) {
			sr.setErrorMessage("MarksheetDetailAndAmountToBePaidList not found.");
			return sr;
//			marksheetDetailAndAmountToBePaidList = (ArrayList<ServiceRequest>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
//			sr.setMarksheetDetailAndAmountToBePaidList(marksheetDetailAndAmountToBePaidList);
		}else {
			boolean isCertificate = student.isCertificateStudent();
			marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaid(sr,SECOND_MARKSHEET_FEE_PER_SUBJECT,request,isCertificate);//Map the service bean with semester and set in session.Returns a map which will show the description on marksheet summary.//
		
		}
		
		for (ServiceRequestStudentPortal serviceRequest : marksheetDetailAndAmountToBePaidList) {
        
			int countOfRecords = serviceRequestDao.getMarksheetIssuedCount(serviceRequest);
			
			if (countOfRecords > 0) {
				
				sr.setError("Free Marksheet request already received for " + serviceRequest.getMonth() + "-"
						+ serviceRequest.getYear() + " : Sem : " + serviceRequest.getSem());
				return sr;
			}
		}
		for (int i = 0; i < marksheetDetailAndAmountToBePaidList.size(); i++) {
		
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			service = insertMarksheetServiceRequest(marksheetDetailAndAmountToBePaidList.get(i), request,
					marksheetDetailAndAmountToBePaidList.size());// Passing size to method to flag marksheet only if the
																	// size is greater than 1//
			sr.getMarksheetDetailAndAmountToBePaidList().get(i).setId(service.getId());
			listOfServiceRequestInserted.add(service);

		}
         System.out.println("listOfServiceRequestInserted::::::"+listOfServiceRequestInserted);
		if (listOfServiceRequestInserted != null && listOfServiceRequestInserted.size() > 0) {
			System.out.println("inserted");
			ServiceRequestStudentPortal srBean = new ServiceRequestStudentPortal();
			sr.setServiceRequestType(listOfServiceRequestInserted.get(0).getServiceRequestType());
			StringBuilder serviceDescription = new StringBuilder();
			StringBuilder serviceRequestId = new StringBuilder();

			if (listOfServiceRequestInserted.size() > 1) {
				for (ServiceRequestStudentPortal service : listOfServiceRequestInserted) {
					serviceDescription.append(service.getDescription()).append(" ,");
					serviceRequestId.append(service.getId()).append(" ,");
				}
				sr.setDescriptionList(
						serviceDescription.toString().substring(0, serviceDescription.toString().length() - 1));
				sr.setSrIdList(serviceRequestId.toString().substring(0, serviceRequestId.toString().length() - 1));

			} else {
				//System.out.println("not inserted");
				for (ServiceRequestStudentPortal service : listOfServiceRequestInserted) {
					serviceDescription.append(service.getDescription());
					serviceRequestId.append(String.valueOf(service.getId()));
				}
				sr.setDescription(serviceDescription.toString());
				sr.setId(Long.parseLong(serviceRequestId.toString()));
			}
		}

		mailer.sendSREmail(sr, student);
		}catch(Exception e) {
			//e.printStackTrace();
		}
		//ServiceRequest response = servReqServ.saveMarksheetRequest(sr);
		return sr;
	}
	private String insertServiceRequestDocumentAndReturnMessage(MultipartFile firCopy,
			MultipartFile indemnityBond,ServiceRequestStudentPortal sr,String sapid) {
		if (firCopy != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, firCopy, sapid + "_FIR");

			if (document.getErrorMessage() == null) {
				//System.out.println("FIR Copy uploaded.");
				document.setDocumentName("FIR for Duplicate Marksheet");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			} else {
				sr.setError( "Error in uploading document " + document.getErrorMessage());
				return "Error";
			}
		}

		if (indemnityBond != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, indemnityBond, sapid + "_Indemnity_Bond");

			if (document.getErrorMessage() == null) {
				//System.out.println("Indemnity Bond uploaded.");
				document.setDocumentName("Indemnity Bond for Duplicate Marksheet");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
				
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
				return "Error";
			}
		}
		return "Success";
		
	}
	
	public ServiceRequestStudentPortal saveSpecialNeedSR(ServiceRequestStudentPortal sr, MultipartFile medical,StudentStudentPortalBean student)  {
			String sapId= sr.getSapId();
			String filename="";
			String filepath="";
			try
			{
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			filename=uploadSRFile(document, medical, sapId + "_MEDICAL_CERTIFICATE");
			filepath=SERVICE_REQUEST_FILES_PATH+filename;
			filename=MedicalCertificate+filename;
			//medicalCertificate.UploadMedicalCertificate(sr.getSapId(), filename,filepath,MedicalCertificate);
			HashMap<String, String> srUpload=awsHelper.uploadLocalFile(filepath, filename, srBucket, MedicalCertificate); 
			if(srUpload.get("status").equalsIgnoreCase("error")){
				logger.error("failed to upload Medical certificate pdf to AWS " + " For Sapid : "+sapId);
				throw new RuntimeException("Error while creating Special Needs SR");
			}
			sr.setDescription(sr.getServiceRequestType() + " for student " + sapId );
			sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
			sr.setCategory("Exam");
			sr.setSapId(sapId);
			sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
			sr.setCreatedBy(sapId);
			sr.setLastModifiedBy(sapId);
			sr.setIssued("N");
			sr.setHasDocuments("Y");
			sr.setSrAttribute("");
			serviceRequestDao.insertServiceRequest(sr);
			if(sr.getId()!=null) {
			document.setFilePath(filename);
			document.setDocumentName("Student medical proof for special need");
			document.setServiceRequestId(sr.getId());
			serviceRequestDao.insertServiceRequestDocument(document);
			serviceRequestDao.updateDocumentStatus(sr);
			
			ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
			serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			mailer.sendSREmail(sr, student);
			deleteFile(filepath);
			}
			}
			catch(Exception e){
				logger.error("failed to upload Medical certificate pdf to AWS " + " For Sapid : "+sapId);
				throw new RuntimeException("Error while creating Special Needs SR");
			}
		return sr;
	}
	
	public void deleteFile(String filePath)
	{
		File f= new File(filePath);           //file to be delete  
		f.delete();
	}
	public void uploadFile(ServiceRequestDocumentBean document, MultipartFile file, String newFileName)
	{
		String errorMessage = null;
		InputStream inputStream = null;
		OutputStream outputStream = null;

		String fileName = file.getOriginalFilename();

		long fileSizeInBytes = file.getSize();
		if (fileSizeInBytes >MAX_FILE_SIZE ) {
			errorMessage = "File size exceeds 5MB. Please upload a file with size less than 5MB";
			document.setErrorMessage(errorMessage);
		}
		fileName = newFileName + "_" + RandomStringUtils.randomAlphanumeric(10)
				+ fileName.substring(fileName.lastIndexOf("."), fileName.length());
		try {

			inputStream = file.getInputStream();
			String filePath = SERVICE_REQUEST_FILES_PATH + fileName;
			// Check if Folder exists which is one folder per Exam (Jun2015,
			// Dec2015 etc.)
			File folderPath = new File(SERVICE_REQUEST_FILES_PATH);
			if (!folderPath.exists()) {
				//System.out.println("Making Folder");
				boolean created = folderPath.mkdirs();
				//System.out.println("created = " + created);
			}

			File newFile = new File(filePath);
			
			outputStream = new FileOutputStream(newFile);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = inputStream.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			document.setFilePath(filePath);
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			//e.printStackTrace();
			document.setErrorMessage(e.getMessage());
		}
	}
	
	public String uploadSRFile(ServiceRequestDocumentBean document, MultipartFile file, String newFileName) {
			String errorMessage = null;
			InputStream inputStream = null;
			OutputStream outputStream = null;
			String fileName = file.getOriginalFilename();
			long fileSizeInBytes = file.getSize();
			if (fileSizeInBytes > MAX_FILE_SIZE) {
				errorMessage = "File size exceeds 5MB. Please upload a file with size less than 5MB";
				document.setErrorMessage(errorMessage);
				}
			fileName = newFileName + "_" + RandomStringUtils.randomAlphanumeric(10)+ fileName.substring(fileName.lastIndexOf("."), fileName.length());
			try {
				inputStream = file.getInputStream();
				String filePath = SERVICE_REQUEST_FILES_PATH + fileName;
				// Check if Folder exists which is one folder per Exam (Jun2015,
				// Dec2015 etc.)
				File folderPath = new File(SERVICE_REQUEST_FILES_PATH);
				if (!folderPath.exists()) {
					//System.out.println("Making Folder");
					boolean created = folderPath.mkdirs();
					//System.out.println("created = " + created);
				}
				File newFile = new File(filePath);
				outputStream = new FileOutputStream(newFile);
				int read = 0;
				byte[] bytes = new byte[1024];
				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				document.setFilePath(filePath);
				outputStream.close();
				inputStream.close();
			} catch (IOException e) {
				//e.printStackTrace();
				document.setErrorMessage(e.getMessage());
			}
			return fileName;
	}
	
	private void insertMarksheetForPayment(ServiceRequestStudentPortal serviceBean,
			String sapid,String trackIdForMultipleMarksheets,int numberOfMarksheetRequests) {
		serviceBean.setDescription(serviceBean.getServiceRequestType() + " for student " + sapid + " for Exam " + serviceBean.getMonth() + "-"
				+ serviceBean.getYear() + " & Sem : " + serviceBean.getSem());
		//System.out.println("serviceBean.getDescription()");

		//System.out.println(serviceBean.getDescription());
		serviceBean.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		serviceBean.setCategory("Exam");
		serviceBean.setCreatedBy(sapid);
		serviceBean.setLastModifiedBy(sapid);
		serviceBean.setSrAttribute("Multiple Marksheet");
		if(numberOfMarksheetRequests > 1){
			serviceBean.setMultipleMarksheet("Y");
		}else{
			serviceBean.setMultipleMarksheet("N");
		}
		if(!serviceRequestDao.checkServiceRequestExist(
				serviceBean.getTrackId(), 
				serviceBean.getSapId(), 
				serviceBean.getYear(), 
				serviceBean.getMonth(), 
				serviceBean.getSem())) {
		serviceBean.setInformationForPostPayment(sapid + "~" + serviceBean.getYear() + "~" + serviceBean.getMonth() + "~" + serviceBean.getSem());// So
		populateServiceRequestObjectForMultipleMarksheets(serviceBean,trackIdForMultipleMarksheets);
		serviceRequestDao.insertServiceRequest(serviceBean);
		}else {
			//System.out.println("inside else condition double entry of marksheet sr request");
		}
		
	}
	private void populateServiceRequestObjectForMultipleMarksheets(ServiceRequestStudentPortal sr, String trackIdForMultipleMarksheets) {

		String sapid = sr.getSapId();
		
		
		sr.setSapId(sapid);
		sr.setTrackId(trackIdForMultipleMarksheets);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_PENDING);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		
	}

	public ServiceRequestStudentPortal insertMarksheetServiceRequest(ServiceRequestStudentPortal sr,HttpServletRequest request,int numberOfMarksheetRequests){
		//System.out.println("Address = " + sr.getPostalAddress());
		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Exam " + sr.getMonth() + "-"
				+ sr.getYear() + " & Sem : " + sr.getSem());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Exam");
		sr.setSapId(sapid);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("Multiple Marksheet");
		
		if(sr.getModeOfDispatch() ==null || "NO".equalsIgnoreCase(sr.getModeOfDispatch()))
		{
			sr.setModeOfDispatch("LC");
		}
		serviceRequestDao.insertServiceRequest(sr);
		serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
		return sr;
	}

	public boolean checkSession(HttpServletRequest request){
		String userId = (String)request.getSession().getAttribute("userId");
		if(userId != null){
			return true;
		}else{
			return false;
		}
		
		/*return true;*/

	}




	
	

	
	
	/*public ServiceRequest saveTranscriptRequest(ServiceRequest sr) {
		
	return null;

	}*/
	

//SaveDob
//SaveChangeInName
//SavePhograph
//SaveFinalCertificateRequest

	public ServiceRequestStudentPortal saveDOB(ServiceRequestStudentPortal sr,MultipartFile sscMarksheet){
		String sapId= sr.getSapId();
		//System.out.println(sapId);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapId);
		String dob = sr.getDob();
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapId + " : New DOB (YYYY-MM-DD) " + dob);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Admission");
		sr.setSapId(sapId);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapId);
		sr.setLastModifiedBy(sapId);
		sr.setIssued("N");
		sr.setSrAttribute("");
		
		serviceRequestDao.insertServiceRequest(sr);
		
		if (sscMarksheet != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, sscMarksheet, sapId + "_SSC_Marksheet");

			if (document.getErrorMessage() == null) {
				//System.out.println("SSC Marksheet uploaded.");
				document.setDocumentName("Student SSC Marksheet for correct Date of Birth");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");

				mailer.sendSREmail(sr, student);
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
			}
		}
		return sr;
	}
	
	public ServiceRequestStudentPortal saveChangeInName(ServiceRequestStudentPortal sr, MultipartFile changeInNameDoc) {
		String sapId = sr.getSapId();
		String firstName =sr.getFirstName();
		String lastName = sr.getLastName();
//		String middleName = sr.getMiddleName();
		
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapId);
//		sr.setDescription(sr.getServiceRequestType() + " for student " + sapId + ": First Name:" + firstName
//				+ ", Middle Name: " + middleName + ", Last Name: " + lastName);
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapId + ": First Name: " + firstName + ", Last Name: " + lastName);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Admission");
		sr.setSapId(sapId);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapId);
		sr.setLastModifiedBy(sapId);
		sr.setSrAttribute("");
		serviceRequestDao.insertServiceRequest(sr);
		
		if (changeInNameDoc != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, changeInNameDoc, sapId + "_Name_Change_Document");

			if (document.getErrorMessage() == null) {
				//System.out.println("SSC Marksheet uploaded.");
				document.setDocumentName("Document for Name Change");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");

				mailer.sendSREmail(sr, student);
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
			}
		}
		
		//fillPaymentParametersInMap(model, student, sr);
		return sr;
	}
	
	public ServiceRequestStudentPortal savePhograph(ServiceRequestStudentPortal sr, MultipartFile changeInPhotographDoc,
			MultipartFile changeInPhotographProofDoc) {
		
		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		sr.setIssued("N");
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Admission");
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("");
		serviceRequestDao.insertServiceRequest(sr);
		//request.getSession().setAttribute("sr", sr);

		if (changeInPhotographDoc != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			//System.out.println("changeInPhotographDoc"+changeInPhotographDoc);
			//System.out.println("sapid"+sapid);
			uploadFile(document, changeInPhotographDoc, sapid + "_Photo_Change_Document");

			if (document.getErrorMessage() == null) {
				//System.out.println("Photograph uploaded.");
				document.setDocumentName("Document for Photo Change");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			} else {
				//setError(m, "Error in uploading document " + document.getErrorMessage());
				sr.setError("Error in uploading document " + document.getErrorMessage());
				return sr;
			}
		}
		
		if (changeInPhotographProofDoc != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			//System.out.println("changeInPhotographProofDoc"+changeInPhotographProofDoc);
			uploadFile(document, changeInPhotographProofDoc, sapid + "_Photo_Change_Document_IDProof");

			if (document.getErrorMessage() == null) {
				//System.out.println("Photograph ID Proof uploaded.");
				document.setDocumentName("ID Proof Document for Photo Change");
				serviceRequestDao.insertServiceRequestDocument(document);
				mailer.sendSREmail(sr, student);
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
				return sr;
			}
		}
		//fillPaymentParametersInMap(model, student, sr);
		return sr;
	}


	public ServiceRequestStudentPortal saveFinalCertificateRequest(ServiceRequestStudentPortal sr, MultipartFile nameOnCertificateDoc) {
		
		Calendar cal = Calendar.getInstance();
		sr.setIssued("N");
		sr.setModeOfDispatch("LC");
		////System.out.println("Name Of Certifcate Document-->" + nameOnCertificateDoc.getOriginalFilename());
		////System.out.println("Address = " + sr.getPostalAddress());
		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Exam");
		sr.setSapId(sapid);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("");
		sr.setMonth(new SimpleDateFormat("MMM").format(cal.getTime()));
		sr.setYear(new SimpleDateFormat("YYYY").format(cal.getTime()));
		ArrayList<ServiceRequestStudentPortal> listOfServiceRequest = new ArrayList<ServiceRequestStudentPortal>();
		listOfServiceRequest.add(sr);
		if (nameOnCertificateDoc != null && "Yes".equals(sr.getAdditionalInfo1())) {
			sr.setAdditionalInfo1("Spouse");
		}
		serviceRequestDao.insertServiceRequest(sr);
		serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
		
		// Newly Added by Vikas 09/08/2016//
		//System.out.println("Radio button Value-->" + sr.getAdditionalInfo1());
		if (nameOnCertificateDoc != null && "Yes".equals(sr.getAdditionalInfo1())) {
			////System.out.println("Name Of Certifcate Document-->" + nameOnCertificateDoc.getOriginalFilename());
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, nameOnCertificateDoc, sapid + "_NAME_ON_CERTIFICATE");

			if (document.getErrorMessage() == null) {
				//System.out.println("Marriage Certificate Uploaded");
				document.setDocumentName("Marriage Certificate For Issuance Of Final Certificate");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
				return checkFinalCertificateEligibilitynew(sr);
			}
		}
		mailer.sendSREmail(sr, student);
		
		
		return sr;
	}
	
	public String generateAmountBasedOnCriteria(String amount,String criteria){
		double calculatedAmount = 0.0;
		switch(criteria){
		case "GST":
			calculatedAmount = Double.parseDouble(amount) + (0.18 * Double.parseDouble(amount));
			break;
		}
		//System.out.println("generateAmountBasedOnCriteria"+calculatedAmount);
		return String.valueOf(calculatedAmount);
		
	}
	
	private void populateServiceRequestObject(ServiceRequestStudentPortal sr) {
		String trackId = sr.getSapId() + System.currentTimeMillis();
		sr.setTrackId(trackId);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_PENDING);	
	}
	
	public void fillPaymentParametersInMap(ModelMap model, StudentStudentPortalBean student, ServiceRequestStudentPortal sr) {

		String address = student.getAddress();
		//System.out.println("Address Before Substring-->" + address);

		if (address == null || address.trim().length() == 0) {
			address = "Not Available";
		} else if (address.length() > 200) {
			address = address.substring(0, 200);
		}
		//System.out.println("Address after Substring-->" + address);
		String city = student.getCity();
		if (city == null || city.trim().length() == 0) {
			city = "Not Available";
		}

		String pin = student.getPin();
		if (pin == null || pin.trim().length() == 0) {
			pin = "400000";
		}

		String mobile = student.getMobile();
		if (mobile == null || mobile.trim().length() == 0) {
			mobile = "0000000000";
		}

		String emailId = student.getEmailId();
		if (emailId == null || emailId.trim().length() == 0) {
			emailId = "notavailable@email.com";
		}
		
		model.addAttribute("channel", "10");
		model.addAttribute("account_id", ACCOUNT_ID);
		model.addAttribute("reference_no", sr.getTrackId());
		model.addAttribute("amount",sr.getAmount());
		model.addAttribute("mode", "LIVE");
		model.addAttribute("currency", "INR");
		model.addAttribute("currency_code", "INR");
		model.addAttribute("description", sr.getServiceRequestType() + ":" + student.getSapid());// This
		model.addAttribute("orderId",sr.getOrderId());																							// should
																									// be
																									// used
																									// in
																									// response
		if(sr.getIsMobile()) {
			model.addAttribute("return_url", SR_RETURN_URL_MOBILE);
		}else {
			model.addAttribute("return_url", SR_RETURN_URL);	
		}
		model.addAttribute("name", student.getFirstName() + " " + student.getLastName());
		model.addAttribute("address", address);
		model.addAttribute("city", city);
		model.addAttribute("country", "IND");
		model.addAttribute("postal_code", pin);
		model.addAttribute("phone", mobile);
		model.addAttribute("email", emailId);
		model.addAttribute("algo", "MD5");
		model.addAttribute("V3URL", V3URL);
		model.addAttribute("studentNumber", sr.getSapId());
	}
	
//	public ServiceRequest checkFinalCertificateEligibility(ServiceRequest sr) {
//		sr.setWantAtAddress("No");
//		
//		String sapid = sr.getSapId();
//		sr.setSapId(sapid);
//		StudentBean student = serviceRequestDao.getSingleStudentsData(sapid);
//		sr.setFinalName(student.getFirstName().trim()+" "+student.getMiddleName().trim()+" "+student.getLastName().trim());
//		List<String> waivedOffSubjects = student.getWaivedOffSubjects();
//		ArrayList<String> applicableSubjects = new ArrayList<>();
//		applicableSubjects = serviceRequestDao.getApplicableSubject(student.getPrgmStructApplicable(), student.getProgram());
//		try{
//		applicableSubjects.removeAll(waivedOffSubjects);
//		}catch(Exception e){
//			//Do nothing
//		}
//		String program = student.getProgram();
//		String programStructure = student.getPrgmStructApplicable();
//		String isLateral = student.getIsLateral();
//		/*String enrollmentYear = student.getEnrollmentYear();
//		String enrollmentMonth = student.getEnrollmentMonth();*/
//		int noOfSubjectsToClearProgram = 0;
//		boolean lateralStudent = false;
//		
//		HashMap<String,ProgramsBean> programInfoList = serviceRequestDao.getProgramDetails();
//		//System.out.println("programInfoList-------------> "+programInfoList);
//		ProgramsBean bean = programInfoList.get(program+"-"+programStructure);
//		//System.out.println(" EXAM MODE "+student.getExamMode());
//	
//		noOfSubjectsToClearProgram = Integer.parseInt(bean.getNoOfSubjectsToClear().trim());	
//		int numberOfsubjectsCleared = serviceRequestDao.getNumberOfsubjectsCleared(sapid, lateralStudent);
//		if ("Y".equals(isLateral)) {
//			lateralStudent = true;
//			//noOfSubjectsToClearProgram = noOfSubjectsToClearProgram / 2;
//			noOfSubjectsToClearProgram =  Integer.parseInt(bean.getNoOfSubjectsToClearLateral().trim());
//		}
//
//		if (numberOfsubjectsCleared != noOfSubjectsToClearProgram) {
//			noOfSubjectsToClearProgram = applicableSubjects.size();
//		}
//		
//		if (numberOfsubjectsCleared != noOfSubjectsToClearProgram) {
//			
//			sr.setError("true");
//			sr.setErrorMessage("You have not yet cleared all subjects!");
//			//return new ModelAndView("serviceRequest/selectSR");
//		} else {
//			
//			int diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
//			//System.out.println("diplomaIssuedCount = " + diplomaIssuedCount);
//			if (diplomaIssuedCount >= 1) {
//				sr.setCharges( Integer.toString(SECOND_DIPLOMA_FEE)  );
//				sr.setDuplicateDiploma("true");
//			} else {
//				sr.setCharges("0");
//			}
//
//			return sr;
//		}
//		return sr;
//	}
	
	public ServiceRequestStudentPortal saveFinalCertificateAndPayment(ServiceRequestStudentPortal sr, MultipartFile indemnityBond,
			MultipartFile firCopy, MultipartFile affidavit) {
		String sapid = sr.getSapId();
		String studentAddress = sr.getPostalAddress();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		student.setAddress(studentAddress);// Setting address since it was failing for case 77114000467
		//boolean isCertificate = isStudentOfCertificate(student.getProgram());
		boolean isCertificate = student.isCertificateStudent();
		if("Yes".equals(sr.getWantAtAddress())){
			sr.setIssued("Y");
			sr.setModeOfDispatch("Courier");
			sr.setCourierAmount(100+"");
		}else{
			sr.setIssued("N");
			sr.setModeOfDispatch("LC");
			sr.setCourierAmount(0+"");
		}
		
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Exam");
		sr.setCreatedBy(sapid);
		sr.setSrAttribute("");
		sr.setLastModifiedBy(sapid);
		sr.setInformationForPostPayment(sapid);// So that it is used post
		
		// payment for follow up steps
		populateServiceRequestObject(sr);
		if(affidavit != null) {
			sr.setAdditionalInfo1("Spouse");
		}
		serviceRequestDao.insertServiceRequest(sr);
		String courierAmount = isCertificate ? generateAmountBasedOnCriteria(sr.getCourierAmount(),"GST") : sr.getCourierAmount();
		sr.setCourierAmount(courierAmount);
		// be used in post payment
		
		if (firCopy != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, firCopy, sapid + "_FIR");

			if (document.getErrorMessage() == null) {
				//System.out.println("FIR Copy uploaded.");
				document.setDocumentName("FIR for Duplicate Final Certificate");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
				//return checkFinalCertificateEligibility(sr);
				return checkFinalCertificateEligibilitynew(sr);
			}
		}

		if (indemnityBond != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, indemnityBond, sapid + "_Indemnity_Bond");

			if (document.getErrorMessage() == null) {
				//System.out.println("Indemnity Bond uploaded.");
				document.setDocumentName("Indemnity Bond for Duplicate Final Certificate");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
				//return checkFinalCertificateEligibility(sr);
				return checkFinalCertificateEligibilitynew(sr);
			}
		}
		
		if (affidavit != null) {
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, affidavit, sapid + "_Affidavit");
			
			if (document.getErrorMessage() == null) {
				document.setDocumentName("Affidavit/Marriage Certificate for Duplicate Final Certificate");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
			return checkFinalCertificateEligibilitynew(sr);
			}
		}
		//System.out.println("Student address-->" + student.getAddress());
		
		/*return proceedToPayOptions(model,requestId,ra);*/
		//return new ModelAndView(new RedirectView("pay"), model);
		return sr;
		
	}
	


	public ServiceRequestStudentPortal saveNewICard(ServiceRequestStudentPortal sr, MultipartFile changeInIDDoc, HttpServletRequest request) {
		// TODO Auto-generated method stub
		String sapId= sr.getSapId();
		//ServiceRequest response = servReqServ.saveNewICard(sr,mapOfInputs,changeInIDDoc,request);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapId);
		
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Admission");
		populateServiceRequestObject(sr);
		sr.setIssued("N");
		sr.setSrAttribute("");
		serviceRequestDao.insertServiceRequest(sr);
		//request.getSession().setAttribute("sr", sr);
		//m.addAttribute("sr", sr);

		if (changeInIDDoc != null) {
			//System.out.println("Document uploaded.");
			ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
			document.setServiceRequestId(sr.getId());
			uploadFile(document, changeInIDDoc, sapId + "_Change_In_ID_Document");

			if (document.getErrorMessage() == null) {
				document.setDocumentName("Document for Change in ID");
				serviceRequestDao.insertServiceRequestDocument(document);
				sr.setHasDocuments("Y");
				serviceRequestDao.updateDocumentStatus(sr);
				
				ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
				serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
				mailer.sendSREmail(sr, student);
			} else {
				sr.setError("Error in uploading document " + document.getErrorMessage());
				//setError(m, "Error in uploading document " + document.getErrorMessage());
				//return new ModelAndView("serviceRequest/changeInID");
			}
		}	
		return sr;
	}
	public ServiceRequestStudentPortal issueBonafide(ServiceRequestStudentPortal sr, HttpServletRequest request) {
		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		int numberOfBonafiedCopiesIssued = 0;
		//Take only cases where the student has actually done payments or has made service requests. Avoid initiated status//
		ArrayList<ServiceRequestStudentPortal> getBonafideIssuedCertificateBySapid = serviceRequestDao.getBonafideIssuedCertificateBySapid(sapid);
		if(getBonafideIssuedCertificateBySapid!=null && getBonafideIssuedCertificateBySapid.size()>0){
			for(ServiceRequestStudentPortal service : getBonafideIssuedCertificateBySapid){
				numberOfBonafiedCopiesIssued = numberOfBonafiedCopiesIssued + Integer.parseInt(service.getNoOfCopies());
			}
			sr.setNoOfCopies(Integer.toString(numberOfBonafiedCopiesIssued));
			
		}else{
			sr.setNoOfCopies("0");
		}
		
		sr.setPostalAddress(student.getAddress());
		//System.out.println("numberOfBonafiedCopiesIssued"+numberOfBonafiedCopiesIssued);
		sr.setCharges("0");
		
		return sr;
	}
	
	public ServiceRequestStudentPortal saveBonafideRequest(ServiceRequestStudentPortal sr, String purpose) {
		//System.out.println("Address = " + sr.getPostalAddress());
		
		String sapid = sr.getSapId();
		//System.out.println("Purpose"+purpose);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Exam " + sr.getMonth() + "-"
				+ sr.getYear() + " & Sem : " + sr.getSem());
		
		sr.setCategory("Exam");
		sr.setSapId(sapid);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("");
		sr.setAdditionalInfo1(purpose);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		//System.out.println("sr::::::::::::::::"+sr);
		serviceRequestDao.insertServiceRequest(sr);
		serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
		// how many times
		// same request was
		// made so that next
		// time they can be
		// charged
		
		mailer.sendSREmail(sr, student);
		
		return sr;
	}

	public ServiceRequestStudentPortal SaveBonafideRequestAndProceedToPay(ServiceRequestStudentPortal sr, HttpServletRequest request) {
		//String paymentType = sr.getPaymentType();
		//System.out.println("SR parameters verification");
		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		
		sr.setDescription(sr.getServiceRequestType() + " for student " + student.getSapid());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
		sr.setCategory("Exam");
		
		if("Yes".equals(sr.getWantAtAddress())){
			sr.setIssued("Y");
			sr.setModeOfDispatch("Courier");
		}else{
			sr.setIssued("N");
			sr.setModeOfDispatch("LC");
		}
		
		sr.setCreatedBy(student.getSapid());
		sr.setLastModifiedBy(student.getSapid());
		sr.setSrAttribute("");
		sr.setInformationForPostPayment(student.getSapid());// So that it is used post
		// payment for follow up steps
		populateServiceRequestObject(sr);
		serviceRequestDao.insertServiceRequest(sr);
		return sr;
	}
	
	public ServiceRequestStudentPortal checkFinalCertificateEligibility(ServiceRequestStudentPortal sr) {
		sr.setWantAtAddress("No");
		
		String sapid = sr.getSapId();
		sr.setSapId(sapid);
		//System.out.println("sapid:::::::::"+sapid);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		//System.out.println("student:::::::::::::"+student);
		
		sr.setFinalName(student.getFirstName().trim()+" "+
				denull(student.getMiddleName()).trim()+
				denull(student.getLastName()).trim());
		sr.setFirstName(student.getFirstName());
		sr.setLastName(student.getLastName());
		sr.setFatherName(student.getFatherName());
		sr.setMotherName(student.getMotherName());
		
		sr.setPostalAddress(student.getAddress());
		List getIfFinalCertificateIssued = serviceRequestDao.getIfFinalCertificateIssued(sapid);
		int count = getIfFinalCertificateIssued.size();
		if(count >0) {
			sr.setIssued("Yes");
			
		}
			
		//System.out.println("count::::::::::::"+count);
		//		sr.setNoOfCopies(Integer.toString(numberOfBonafiedCopiesIssued));
		List<String> waivedOffSubjects = student.getWaivedOffSubjects();
		ArrayList<String> applicableSubjects = new ArrayList<>();
		applicableSubjects = serviceRequestDao.getApplicableSubject(student.getPrgmStructApplicable(), student.getProgram());
		try{
		applicableSubjects.removeAll(waivedOffSubjects);
		}catch(Exception e){
			//Do nothing
		}
		String program = student.getProgram();
		String programStructure = student.getPrgmStructApplicable();
		String isLateral = student.getIsLateral();
		/*String enrollmentYear = student.getEnrollmentYear();
		String enrollmentMonth = student.getEnrollmentMonth();*/
		int noOfSubjectsToClearProgram = 0;
		boolean lateralStudent = false;
		
		HashMap<String,ProgramsStudentPortalBean> programInfoList = serviceRequestDao.getProgramDetails();
		//System.out.println("programInfoList-------------> "+programInfoList);
//		ProgramsBean bean = programInfoList.get(program+"-"+programStructure);
		String consumerProgStructId = student.getConsumerProgramStructureId();
		ProgramsStudentPortalBean bean = programInfoList.get(consumerProgStructId);
		//System.out.println(" EXAM MODE "+student.getExamMode());
	
		noOfSubjectsToClearProgram = Integer.parseInt(bean.getNoOfSubjectsToClear().trim());	
		int numberOfsubjectsCleared = serviceRequestDao.getNumberOfsubjectsCleared(sapid, lateralStudent);
		if ("Y".equals(isLateral)) {
			lateralStudent = true;
			//noOfSubjectsToClearProgram = noOfSubjectsToClearProgram / 2;
			noOfSubjectsToClearProgram =  Integer.parseInt(bean.getNoOfSubjectsToClearLateral().trim());
		}

		if (numberOfsubjectsCleared != noOfSubjectsToClearProgram) {
			noOfSubjectsToClearProgram = applicableSubjects.size();
		}
		
		if (numberOfsubjectsCleared != noOfSubjectsToClearProgram) {
			
			sr.setError("true");
			sr.setErrorMessage("You have not yet cleared all subjects!");
			//return new ModelAndView("serviceRequest/selectSR");
		} else {
			
			int diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
			//System.out.println("diplomaIssuedCount = " + diplomaIssuedCount);
			if (diplomaIssuedCount >= 1) {
				sr.setCharges( Integer.toString(SECOND_DIPLOMA_FEE)  );
				sr.setDuplicateDiploma("true");
			} else {
				sr.setCharges("0");
			}

			return sr;
		}
		//System.out.println("sr");
		//System.out.println(sr);
		return sr;
	}

	private String denull(String str) {
		str = (str== null ? "" : str+" ");

		return str;
	}

	
	

	
	public ServiceRequestStudentPortal saveProgramDeRegistration(ServiceRequestStudentPortal sr) {
	
		int numOfRowsUpdated = 0;
		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);		
		
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Program " +sr.getProgram() + "," + sr.getMonth() + "-"
				+ sr.getYear() + " & Sem : " + sr.getSem());
		sr.setSapId(sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Admission");
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		//System.out.println("sr::::::::::::::::"+sr);
		try {
			
			//System.out.println(" rows deleted from registration--"+numOfRowsUpdated);
			
//			5. update status in SFDC
			boolean success = updateDeregisterStatusInSFDC(sr);

			if(!success) {
				sr.setError("Error in updating records.");
			}else {
				serviceRequestDao.insertServiceRequest(sr);
				serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
				// how many times
				// same request was
				// made so that next
				// time they can be
				// charged
//				4. Dereg auto-success
				numOfRowsUpdated = serviceRequestDao.deRegisterUser(sr);
				mailer.sendSREmail(sr, student);				
			}
//			
			
		}catch(Exception e) {
			sr.setError("Error in Program De-Registration.");
			sr.setErrorMessage(e.getMessage());
		}
		
		return sr;
	}
	
	
	public ReRegistrationStudentPortalBean getActiveReRegistrationFromSalesForce() throws ConnectionException {
		
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 */  
	
		QueryResult qResult = new QueryResult();
		ReRegistrationStudentPortalBean bean = new ReRegistrationStudentPortalBean();
			try {
				this.connection = SFConnection.getConnection();
				
				if( connection == null ) {
					SFConnection sfConnection = new SFConnection(SFDC_USERID, SFDC_PASSWORD_TOKEN);
					connection = SFConnection.getConnection();					
				}
				
				String sql = "select id , Session_End_Date__c, Session_Start_Date__c, Category__c,nm_Type__c, nm_Semester__c,Specialisation__c,Year__c,Session__c  "
						+ "from Calender__c where Category__c ='Re-Registration' and nm_Type__c='Post Graduate Diploma Programs' and Session_End_Date__c >Today";
				qResult = connection.query(sql);
				SObject[] records = qResult.getRecords();
				SObject s = (SObject) records[0]; 
					if(records.length>0) {
						bean.setError(false); 
					}
					bean.setAcadYear(StringUtils.left((String)s.getField("Year__c"), 4));
					bean.setAcadMonth(StringUtils.left((String)s.getField("Session__c"), 3));
					bean.setStartTime((String)s.getField("Session_Start_Date__c"));
					bean.setEndTime((String)s.getField("Session_End_Date__c"));
					return bean;
			} catch (Exception e) { 
				//init(); 
				//getActiveReRegistrationFromSalesForce();
				
				bean.setError(true);
				return bean;
			}
			
	}	
	public boolean updateDeregisterStatusInSFDC(ServiceRequestStudentPortal sr) {
		
		//System.out.println("inside----1----updateDeregisterStatusInSFDC---");
		ArrayList<String> saleforceUpdationErrorList = new ArrayList<String>();
		boolean isValid = false;
		boolean done = false;
		/*
		 * ConnectorConfig config = new ConnectorConfig();
		 * config.setUsername(SFDC_USERID); config.setPassword(SFDC_PASSWORD_TOKEN);
		 */
		//System.out.println("Updating records back in SFDC");
		//System.out.println("SFDC_USERID = "+SFDC_USERID);
		//System.out.println("SFDC_PASSWORD_TOKEN = "+SFDC_PASSWORD_TOKEN);
		QueryResult qResult = new QueryResult();
		try {
			
			
			//connection = Connector.newConnection(config);
			String sql = "select id,Sem__c,Student_Number__c,StageName, "
					+ " nm_Session__c,nm_Year__c  from opportunity  "
					+ " where  nm_Session__c like '" + sr.getMonth() + "%' and nm_Year__c=" + sr.getYear()
					+ " and StageName ='Closed Won' and Student_Number__c ='"+ sr.getSapId()+"' ORDER BY Sem__c desc limit 1";
			qResult = connection.query(sql);
			
			
			//System.out.println("");

			SObject[] recordsToBeSent = new SObject[qResult.getSize()];
			
			ArrayList<SObject> recordsToBeUpdated = new ArrayList<SObject>();
			
			
			//System.out.println("inside----2----updateDeregisterStatusInSFDC----"+qResult);
			if (qResult.getSize() > 0) {
				//System.out.println("inside----3--updateDeregisterStatusInSFDC-----"+qResult.getSize());
				SObject[] records = qResult.getRecords();
				//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+records.length);
				while (!done) {
					for (int i = 0; i < records.length; i++) {
						SObject s = (SObject) records[i];
						//System.out.println("s---"+s);
						String id = (String)s.getField("Id");
						
							SObject opportunityObject = new SObject();
							opportunityObject.setType("Opportunity");
							opportunityObject.setField("Id",id);
							opportunityObject.setField("StageName","De-Registered");
							opportunityObject.setField("Term_Cleared__c","No");
							//System.out.println("opportunityObject--"+opportunityObject);
	
							recordsToBeUpdated.add(opportunityObject);
							recordsToBeSent = recordsToBeUpdated.toArray(recordsToBeSent);
							
							//System.out.println("recordsToBeSent size--"+recordsToBeSent.length);
							// update the records in Salesforce.com
							SaveResult[] saveResults = connection.update(recordsToBeSent);
							// check the returned results for any errors
							for (int k=0; k< saveResults.length; k++) {
								if (saveResults[k].isSuccess()) {
									//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
									//System.out.println(k+". Successfully updated record - Id: " + saveResults[k].getId());
									isValid = true;
								}else {
									Error[] errors = saveResults[k].getErrors();
									String errorMessage = "";
									for (int j=0; j< errors.length; j++) {
										//System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
										
										//System.out.println("ERROR updating record: " + records[k].getId() + " : "+ errors[j].getMessage() );
										errorMessage +=errors[j].getMessage();
									}
									isValid = false;
									saleforceUpdationErrorList.add(records[k].getId()+":"+errorMessage);
									//System.out.println("saleforceUpdationErrorList--"+saleforceUpdationErrorList);
								}   
							}
						}
						if (qResult.isDone()) {
							//System.out.println("in isdone true---");
							done = true;
						} else {
							//System.out.println("Querying more.....");
							qResult = connection.queryMore(qResult.getQueryLocator());
						}
					}
					
				}
			
		}
		catch(Exception e) {
			//init(); 
			//updateDeregisterStatusInSFDC(sr);
			//System.out.println(e.getMessage());
		}
		return isValid;
	}  
	
	
	public ServiceRequestStudentPortal checkMarksheetHistoryForMBAWX(ServiceRequestStudentPortal sr, HttpServletRequest request) {
		String serviceRequestType = sr.getServiceRequestType();
	
		//boolean paymentRequired = false;
		String sapId = sr.getSapId();
		
		//Check for Courier Amount
		//System.out.println("getWantAtAddress-->"+sr.getWantAtAddress());
		if("Yes".equals(sr.getWantAtAddress()) && sr.getWantAtAddress()!=null){
			//System.out.println("if of want at add---------");
			sr.setIssued("Y");
			sr.setModeOfDispatch("Courier");
			String postalAddressForShipment = sr.getPostalAddress();
			sr.setPostalAddress(postalAddressForShipment);
			sr.setCourierAmount(100+"");
		}else{
			//System.out.println("else of want at add---------");
			sr.setIssued("N");
			sr.setModeOfDispatch("LC");
			sr.setCourierAmount(0+"");
		}
		//System.out.println("Mode Of Dispatch--->"+sr.getModeOfDispatch());
		//System.out.println("page parameter" + request.getParameter("postalAddress"));
		//System.out.println("sapId"+sapId);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapId);
		//System.out.println("student"+student);
		//boolean isCertificate = isStudentOfCertificate(student.getProgram());
		boolean isCertificate = student.isCertificateStudent();
		
		//System.out.println("PARAMETERS FROM PAGE.......");
		//System.out.println(sr.getMarksheetDetailRecord1());
		//System.out.println(sr.getMarksheetDetailRecord2());
		//System.out.println(sr.getMarksheetDetailRecord3());
		//System.out.println(sr.getMarksheetDetailRecord4());
		//System.out.println("PARAMETERS FROM PAGE END.......");
		
		ArrayList<String> messageToBeShownForSemesterAppeared = new ArrayList<>();
				
		//if sem is equal to 8 and master key  =  131 that is Msc AI for Master Dissertation Part II then only the sapid will check that it exists or not
		if (IA_MASTER_DISSERTATION_SEM_FOR_Q8 == Integer.parseInt(student.getSem())
				&& IA_MASTER_DISSERTATION_MASTER_KEY == Integer.parseInt(student.getConsumerProgramStructureId())) {
			if (messageToBeShownForSemesterAppeared.isEmpty()) {
				int count = serviceRequestDao.checkSapidExistForQ8(sr.getSapId());
					if (count == 0) {
						String semesterAppearedMessage = "";
						messageToBeShownForSemesterAppeared.add(semesterAppearedMessage);
					}
			}
		}else {
			messageToBeShownForSemesterAppeared =serviceRequestDao.getSubjectsAppearedForSemesterMessageListForMBAWX(sr);//This will show an error message if the student has not appeared for a sem for given year and month//
		}

		ArrayList<Integer> subjectsAppeared = new ArrayList<>();
		
		if (IA_MASTER_DISSERTATION_SEM_FOR_Q8 == Integer.parseInt(student.getSem())
				&& IA_MASTER_DISSERTATION_MASTER_KEY == Integer.parseInt(student.getConsumerProgramStructureId())) {
					int count = serviceRequestDao.checkSapidExistForQ8(sr.getSapId());
					if (count != 0) {
							subjectsAppeared.add(count);
						}
			}else {
				subjectsAppeared = serviceRequestDao.getSubjectsAppearedForSemesterForMBAWX(sr); 
				
			}
		//If student has given any exam//
		ArrayList<String> messageToShowForResultDeclareDate = serviceRequestDao.resultDeclaredMessageForMBAWX(sr);//Check if the result has been declared for the particular year,month and sem//
		ArrayList<String> marksheetsPrinted = serviceRequestDao.getMarksheetPrintedCount(sr);//Check number of marksheets issued and printed//
		
		//System.out.println("subjectsAppeared = " + subjectsAppeared.size() + "--"+ subjectsAppeared);
		//System.out.println("messageToBeShownForSemesterAppeared"+messageToBeShownForSemesterAppeared.size());
		//System.out.println("messageToShowForResultDeclareDate"+messageToShowForResultDeclareDate.size());
		
		//Check if result is declared.
		if (messageToShowForResultDeclareDate!=null && messageToShowForResultDeclareDate.size()>0) {
			String messageForResultDeclaration = "";
			for(String message : messageToShowForResultDeclareDate){
				if(!"Yes".equals(message)){
					messageForResultDeclaration = message + "\n";
				}
			}
			sr.setError(messageForResultDeclaration+"Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
			//System.out.println("1st if:::::::::::::::::::::");
			sr.setYearList(yearListForMBAWX);
			return sr;
		}
		//Check if marksheets are already printed and kept at LC//
		if(marksheetsPrinted!=null && marksheetsPrinted.size()>0){
			String messageForPrintedMarksheets = "";
			for(String message : marksheetsPrinted){
				
				messageForPrintedMarksheets = message + "\n";
				}
			sr.setError(messageForPrintedMarksheets+" :- This marksheet is already printed, kindly connect with your Academic co-ordinator, if you wish to have this couriered then email us at <a href=\"#\"> ngasce@nmims.edu</a>. Please re-raise marksheet for other semesters by clicking on this <a href=\"addSRForm\"> LINK</a> ?");
			sr.setYearList(yearListForMBAWX);
			//System.out.println("2nd if:::::::::::::::::::::");
			return sr;
			}
			
		
		//Will check if any subjects were appeared by student//
		if (subjectsAppeared.size() > 0 && subjectsAppeared.get(0) == 0) {
			sr.setError("There are no marks entries with respect to your SAPID"
					+ " Exam for you OR Results not yet declared.Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
			
			sr.setYearList(yearListForMBAWX);
			//System.out.println("3rd if:::::::::::::::::::::");
			return sr;
		}
		//Print the message for the semester appeared or not appeared//
		if(messageToBeShownForSemesterAppeared!=null && messageToBeShownForSemesterAppeared.size()>0){
			String messageToBeShown = "";
			for(String message : messageToBeShownForSemesterAppeared){
				messageToBeShown = message +"\n";  
			}
			sr.setError(messageToBeShown+"Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
			sr.setYearList(yearList);
			//System.out.println("4th if:::::::::::::::::::::");
			return sr;
		}
		
		String courierAmount = isCertificate ? generateAmountBasedOnCriteria(sr.getCourierAmount(),"GST") : sr.getCourierAmount();
		ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList=new ArrayList<>();
		
		// Map the service bean with semester and set in session.Returns a map which will show the description on mark sheet summary.
		if(MASTER_KEY_LIST.contains(Integer.parseInt(student.getConsumerProgramStructureId())))
			marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaid(sr,
					SECOND_MARKSHEET_FEE_PER_SUBJECT, request, isCertificate);
		else	
			marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaidMBAWX(sr,
					SECOND_MARKSHEET_FEE_PER_SUBJECT, request, isCertificate);
	
		sr.setMarksheetDetailAndAmountToBePaidList(marksheetDetailAndAmountToBePaidList);
		
		int  amountToBeDisplayedForMarksheetSummary= 0 ;
		for(int i =0;i<marksheetDetailAndAmountToBePaidList.size();i++){
			ServiceRequestStudentPortal serviceBean = marksheetDetailAndAmountToBePaidList.get(i);
			
			amountToBeDisplayedForMarksheetSummary = amountToBeDisplayedForMarksheetSummary+
					Integer.parseInt(serviceBean.getAmountToBeDisplayedForMarksheetSummary() );
			
		}
		amountToBeDisplayedForMarksheetSummary =amountToBeDisplayedForMarksheetSummary+ Integer.parseInt(sr.getCourierAmount());
		////System.out.println("AMOUNT FROM SESSION-->"+amountToBeDisplayedForMarksheetSummary);
		sr.setTotalAmountToBePayed(Integer.toString(amountToBeDisplayedForMarksheetSummary));
		
		return sr;
	}

	public ServiceRequestStudentPortal checkMarksheetHistoryForMBAX(ServiceRequestStudentPortal sr, HttpServletRequest request) {
		String serviceRequestType = sr.getServiceRequestType();
		//boolean paymentRequired = false;
		String sapId = sr.getSapId();
		
		//Check for Courier Amount
		//System.out.println("getWantAtAddress-->"+sr.getWantAtAddress());
		if("Yes".equals(sr.getWantAtAddress()) && sr.getWantAtAddress()!=null){
			//System.out.println("if of want at add---------");
			sr.setIssued("Y");
			sr.setModeOfDispatch("Courier");
			String postalAddressForShipment = sr.getPostalAddress();
			sr.setPostalAddress(postalAddressForShipment);
			sr.setCourierAmount(100+"");
		}else{
			//System.out.println("else of want at add---------");
			sr.setIssued("N");
			sr.setModeOfDispatch("LC");
			sr.setCourierAmount(0+"");
		}
		//System.out.println("Mode Of Dispatch--->"+sr.getModeOfDispatch());
		//System.out.println("page parameter" + request.getParameter("postalAddress"));
		//System.out.println("sapId"+sapId);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapId);
		//System.out.println("student"+student);
		//boolean isCertificate = isStudentOfCertificate(student.getProgram());
		boolean isCertificate = student.isCertificateStudent();
		
		//System.out.println("PARAMETERS FROM PAGE.......");
		//System.out.println(sr.getMarksheetDetailRecord1());
		//System.out.println(sr.getMarksheetDetailRecord2());
		//System.out.println(sr.getMarksheetDetailRecord3());
		//System.out.println(sr.getMarksheetDetailRecord4());
		//System.out.println("PARAMETERS FROM PAGE END.......");
		
		ArrayList<String> messageToBeShownForSemesterAppeared = serviceRequestDao.getSubjectsAppearedForSemesterMessageListForMBAX(sr);//This will show an error message if the student has not appeared for a sem for given year and month//
		ArrayList<Integer> subjectsAppeared = serviceRequestDao.getSubjectsAppearedForSemesterForMBAX(sr); //If student has given any exam//
		ArrayList<String> messageToShowForResultDeclareDate = serviceRequestDao.resultDeclaredMessageForMBAX(sr);//Check if the result has been declared for the particular year,month and sem//
		ArrayList<String> marksheetsPrinted = serviceRequestDao.getMarksheetPrintedCount(sr);//Check number of marksheets issued and printed//
		
		//System.out.println("subjectsAppeared = " + subjectsAppeared.size() + "--"+ subjectsAppeared);
		//System.out.println("messageToBeShownForSemesterAppeared"+messageToBeShownForSemesterAppeared.size());
		//System.out.println("messageToShowForResultDeclareDate"+messageToShowForResultDeclareDate.size());
		
		//Check if result is declared.
		if (messageToShowForResultDeclareDate!=null && messageToShowForResultDeclareDate.size()>0) {
			String messageForResultDeclaration = "";
			for(String message : messageToShowForResultDeclareDate){
				if(!"Yes".equals(message)){
					messageForResultDeclaration = message + "\n";
				}
			}
			sr.setError(messageForResultDeclaration+"Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
			//System.out.println("1st if:::::::::::::::::::::");
			sr.setYearList(yearListForMBAWX);
			return sr;
		}
		//Check if marksheets are already printed and kept at LC//
		if(marksheetsPrinted!=null && marksheetsPrinted.size()>0){
			String messageForPrintedMarksheets = "";
			for(String message : marksheetsPrinted){
				
				messageForPrintedMarksheets = message + "\n";
				}
			sr.setError(messageForPrintedMarksheets+" :- This marksheet is already printed, kindly connect with your Academic co-ordinator, if you wish to have this couriered then email us at <a href=\"#\"> ngasce@nmims.edu</a>. Please re-raise marksheet for other semesters by clicking on this <a href=\"addSRForm\"> LINK</a> ?");
			sr.setYearList(yearListForMBAWX);
			//System.out.println("2nd if:::::::::::::::::::::");
			return sr;
			}
			
		
		//Will check if any subjects were appeared by student//
		if (subjectsAppeared.size() > 0 && subjectsAppeared.get(0) == 0) {
			sr.setError("There are no marks entries with respect to your SAPID"
					+ " Exam for you OR Results not yet declared.Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
			
			sr.setYearList(yearListForMBAWX);
			//System.out.println("3rd if:::::::::::::::::::::");
			return sr;
		}
		//Print the message for the semester appeared or not appeared//
		if(messageToBeShownForSemesterAppeared!=null && messageToBeShownForSemesterAppeared.size()>0){
			String messageToBeShown = "";
			for(String message : messageToBeShownForSemesterAppeared){
				messageToBeShown = message +"\n";  
			}
			sr.setError(messageToBeShown+"Kindly click <a href=\"addSRForm\"> HERE </a> for raising service request.");
			sr.setYearList(yearList);
			//System.out.println("4th if:::::::::::::::::::::");
			return sr;
		}
		
		String courierAmount = isCertificate ? generateAmountBasedOnCriteria(sr.getCourierAmount(),"GST") : sr.getCourierAmount();
		ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaidMBAWX(sr,SECOND_MARKSHEET_FEE_PER_SUBJECT,request,isCertificate);//Map the service bean with semester and set in session.Returns a map which will show the description on marksheet summary.//
		sr.setMarksheetDetailAndAmountToBePaidList(marksheetDetailAndAmountToBePaidList);
		
		int  amountToBeDisplayedForMarksheetSummary= 0 ;
		for(int i =0;i<marksheetDetailAndAmountToBePaidList.size();i++){
			ServiceRequestStudentPortal serviceBean = marksheetDetailAndAmountToBePaidList.get(i);
			
			amountToBeDisplayedForMarksheetSummary = amountToBeDisplayedForMarksheetSummary+
					Integer.parseInt(serviceBean.getAmountToBeDisplayedForMarksheetSummary() );
			
		}
		amountToBeDisplayedForMarksheetSummary =amountToBeDisplayedForMarksheetSummary+ Integer.parseInt(sr.getCourierAmount());
		////System.out.println("AMOUNT FROM SESSION-->"+amountToBeDisplayedForMarksheetSummary);
		sr.setTotalAmountToBePayed(Integer.toString(amountToBeDisplayedForMarksheetSummary));
		return sr;
	}

	public ServiceRequestStudentPortal checkFinalCertificateEligibilitynew(ServiceRequestStudentPortal sr) {
		sr.setWantAtAddress("No");
		String sapid = sr.getSapId();
		sr.setSapId(sapid);
		//System.out.println("sapid:::::::::"+sapid);
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		//System.out.println("student:::::::::::::"+student);
		
		sr.setFinalName(student.getFirstName().trim()+" "+
				denull(student.getMiddleName()).trim()+
				denull(student.getLastName()).trim());
		sr.setFirstName(student.getFirstName());
		sr.setLastName(student.getLastName());
		sr.setFatherName(student.getFatherName());
		sr.setMotherName(student.getMotherName());
		sr.setHusbandName(student.getHusbandName());
		
		sr.setPostalAddress(student.getAddress());
		List getIfFinalCertificateIssued = serviceRequestDao.getIfFinalCertificateIssued(sapid);
		int count = getIfFinalCertificateIssued.size();
		if(count >0) {
			sr.setIssued("Yes");
			
		}
			
		//System.out.println("count::::::::::::"+count);
		//		sr.setNoOfCopies(Integer.toString(numberOfBonafiedCopiesIssued));
		String isLateral ="";
		String cpsi="";
		String previousStudentId="";
	try {
		 isLateral = student.getIsLateral();
		 cpsi = student.getConsumerProgramStructureId();
		 previousStudentId = student.getPreviousStudentId();
		 if(student.getConsumerProgramStructureId().isEmpty() || student.getConsumerProgramStructureId()==null) {
			 	sr.setError("true");
				sr.setErrorMessage("Unable to find masterKey.");
				return sr;
			}
		 if(student.getIsLateral().isEmpty() || student.getIsLateral()==null) {
			 isLateral="N";
			}
		}catch(Exception e) {
			
			sr.setError("true");
			sr.setErrorMessage("Unable to find previous student master key/islateral details.");
			return sr;
		}
	ArrayList<String> applicableSubjects = new ArrayList<>();
	applicableSubjects = serviceRequestDao.getApplicableSubjectNew(cpsi);
	//System.out.println("Applicable subjects = "+applicableSubjects);
	if(applicableSubjects.size() ==  0 ) {
		sr.setError("true");
		sr.setErrorMessage("No Applicable subjects found");
		return sr;
		}
	List<String> subjectsCleared = serviceRequestDao.getSubjectsClearedCurrentProgramNew(sapid, isLateral,cpsi);
	int numberOfsubjectsCleared =subjectsCleared.size();
	if("Y".equalsIgnoreCase(isLateral)) {
		if(student.getPreviousStudentId().isEmpty() || student.getPreviousStudentId()==null) {
			sr.setError("true");
			sr.setErrorMessage("Unable to find previous student Id.");
			return sr;
		}
		List<String> waivedOffSubjects = student.getWaivedOffSubjects(); //contains all the passed subjects of previous program
		//System.out.println("Waived subjects = "+waivedOffSubjects.size());
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
	//System.out.println("programInfoList-------------> "+programInfoList);
	ProgramsStudentPortalBean bean = programInfoList.get(student.getConsumerProgramStructureId());
	//System.out.println(" EXAM MODE "+student.getExamMode());
	noOfSubjectsToClearProgram = Integer.parseInt(bean.getNoOfSubjectsToClear().trim());	
	if (numberOfsubjectsCleared != noOfSubjectsToClearProgram) {
		sr.setError("true");
		sr.setErrorMessage("You have not yet cleared all subjects!");
		
	}else {
			
			int diplomaIssuedCount = serviceRequestDao.getDiplomaIssuedCount(sr);
			//System.out.println("diplomaIssuedCount = " + diplomaIssuedCount);
			if (diplomaIssuedCount >= 1) {
				sr.setCharges( Integer.toString(SECOND_DIPLOMA_FEE)  );
				sr.setDuplicateDiploma("true");
			} else {
				sr.setCharges("0");
			}

			return sr;
		}
		return sr;
}
	public ServiceRequestStudentPortal saveProgramWithdrawal(ServiceRequestStudentPortal sr) {
		
		sr.setServiceRequestType("Program Withdrawal");
		ArrayList<ServiceRequestStudentPortal> srList = serviceRequestDao.getServiceRequestHistoryList(sr,"");
		
		if(srList.size()>0) {
			sr.setError("true"); 
			sr.setErrorMessage("Already raised Service Request for Program Withdrawal.");
			return sr;
		}

		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		  
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Program " +student.getProgram() ); 
		sr.setSapId(sapid); 
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Admission");
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setAmount("0"); 
		try {
			serviceRequestDao.insertServiceRequest(sr);
			serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track

		}catch(Exception e) {
			sr.setError("true"); 
			sr.setErrorMessage("Couldn't save Program Withdrawal.");
		} 
		
		return sr;
	}
 
	public ServiceRequestStudentPortal saveExitProgram(ServiceRequestStudentPortal sr) {
 
		sr.setServiceRequestType("Exit Program");
		ArrayList<ServiceRequestStudentPortal> srList = serviceRequestDao.getServiceRequestHistoryList(sr,"");
		
		if(srList.size()>0) {
			sr.setError("true"); 
			sr.setErrorMessage("Already raised Service Request for Exit Program.");
			return sr;
		}

		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		  
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Program " +student.getProgram() ); 
		sr.setSapId(sapid); 
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Admission");
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		//sr.setAmount("0"); 
		
		Calendar cal = Calendar.getInstance();
		sr.setMonth(new SimpleDateFormat("MMM").format(cal.getTime()));
		sr.setYear(new SimpleDateFormat("YYYY").format(cal.getTime()));

		if(sr.getHasDocuments()==null) {
			//System.out.println("sr.getHasDocuments() is null");
    		sr.setHasDocuments("N");
    	}
		try {
			serviceRequestDao.insertServiceRequest(sr);
			if(sr.getTranStatus().equalsIgnoreCase("Free")) {
				serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
			}
		}catch(Exception e) {
			sr.setError("true"); 
			sr.setErrorMessage("Couldn't save Program Withdrawal.");
		} 
		/*
		 * if (nameOnCertificateDoc != null && "Y".equals(sr.getHasDocuments())) {
		 * 
		 * ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
		 * document.setServiceRequestId(sr.getId()); uploadFile(document,
		 * nameOnCertificateDoc, sapid + "_NAME_ON_CERTIFICATE");
		 * 
		 * if (document.getErrorMessage() == null) { document.
		 * setDocumentName("Marriage Certificate For Issuance Of Final Certificate");
		 * document.setServiceRequestId(sr.getId());
		 * serviceRequestDao.insertServiceRequestDocument(document);
		 * serviceRequestDao.updateDocumentStatus(sr); } else {
		 * sr.setError("Error in uploading document " + document.getErrorMessage()); } }
		 */
		
		return sr;
	}
	public int updateStudentProgramStatus(String sapid) {
		int count = serviceRequestDao.updateStudentProgramStatus(sapid);
		return count;
	}

	public boolean checkIfStudentApplicableForWithdrawal(StudentStudentPortalBean student) {

		try {
			StudentStudentPortalBean bean = serviceRequestDao.getRecentRegisterationByStudent(student.getSapid());
			
			boolean checkAlreadyRaised = checkAlreadyRaisedForExit(student.getSapid());
			if(checkAlreadyRaised) {
				return false;
			}
			
			String enrollmentDate = student.getEnrollmentYear()+" "+student.getEnrollmentMonth();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM");
			
			if(sdf.parse(enrollmentDate).after(sdf.parse("2019 Jan")) || sdf.parse(enrollmentDate).equals(sdf.parse("2019 Jan"))) {
				
				//System.out.println("new scheme...");
				if(bean!=null) {
		        	if( Integer.parseInt(bean.getSem())  < 2) {
		    			return true;
		    		}
		        }
			}else {
				
				//System.out.println("old scheme...");
				if(bean!=null) {
		        	if( Integer.parseInt(bean.getSem())  < 3) {
		    			return true;
		    		}
		        }
			}
			
	        
		} catch (Exception e) {}
		return false;
	}
	public ServiceRequestStudentPortal checkIfStudentApplicableForExitProgram(StudentStudentPortalBean student) {
		ServiceRequestStudentPortal rs = new ServiceRequestStudentPortal();
		try {
			StudentStudentPortalBean bean = serviceRequestDao.getRecentRegisterationByStudent(student.getSapid());

			ArrayList<ProgramSubjectMappingStudentPortalBean> passfailInfo = new  ArrayList<ProgramSubjectMappingStudentPortalBean>();
			if(student.getIsLateral().equalsIgnoreCase("Y")) {
				passfailInfo = cDao.getFailSubjectsForaLateralStudent(student);
			}else {
				passfailInfo= cDao.getFailSubjectsForAStudent(student.getSapid());
			}
			//System.out.println("passfailInfo"+passfailInfo);
			ArrayList<String> failedSemList = new ArrayList<String>();
			for(ProgramSubjectMappingStudentPortalBean passfailBean : passfailInfo) {
				failedSemList.add(passfailBean.getSem());
			}
			
			String enrollmentDate = student.getEnrollmentYear()+" "+student.getEnrollmentMonth();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM");
			
			if(sdf.parse(enrollmentDate).after(sdf.parse("2019 Jan")) || sdf.parse(enrollmentDate).equals(sdf.parse("2019 Jan"))) {
				System.out.println("new scheme...");
				boolean IfEntryInPassfailForSem1=false ;
				boolean IfEntryInPassfailForSem2=false ;
				if(bean!=null) {
					if(student.getIsLateral().equalsIgnoreCase("Y")) {
						IfEntryInPassfailForSem1 =cDao.checkIfEntryInPassfailLateral(student.getSapid(),student.getPreviousStudentId(),1);
						IfEntryInPassfailForSem2 =cDao.checkIfEntryInPassfailLateral(student.getSapid(),student.getPreviousStudentId(),2);
					}else {
						IfEntryInPassfailForSem1 =cDao.checkIfEntryInPassfail(student.getSapid(),1);
						IfEntryInPassfailForSem2 =cDao.checkIfEntryInPassfail(student.getSapid(),2);
					    	
					}
					
				    //if sem 1 failed not not yet attempted
				    if(!IfEntryInPassfailForSem1 || failedSemList.contains("1")) {
						rs.setIsCertificate(false);
						 return rs;
					//if sem 1 pass,sem 2 failed and not yet attempted	 
					}else if(!IfEntryInPassfailForSem2 || failedSemList.contains("2")) {
						rs.setCertificationType("Certificate In Business Management");
						rs.setIsCertificate(true);return rs;
					//if sem 1 and sem 2 passed.
					}else {
						rs.setCertificationType("Diploma In Business Management");
						rs.setIsCertificate(true);return rs;
					}
		        }
			}else {
				System.out.println("failedSemList "+failedSemList);
				//System.out.println("old scheme...");
				if(bean!=null) {
					boolean IfEntryInPassfailForSem1 =cDao.checkIfEntryInPassfail(student.getSapid(),1);
				    boolean IfEntryInPassfailForSem2 =cDao.checkIfEntryInPassfail(student.getSapid(),2);
					//if sem1 or sem2 failed
		        	if( (!IfEntryInPassfailForSem1 || failedSemList.contains("1"))
		        			||
		        		(!IfEntryInPassfailForSem2 || failedSemList.contains("2"))
		        			) { 
		        		
		        		rs.setIsCertificate(false);
						return rs;
					//if sem 1 and sem 2 pass 
					//dgm
		    		}else {
		    			rs.setCertificationType("Diploma In General Management");
						rs.setIsCertificate(true);
	        			return rs;
		    		}
		        }
			}
			
	        
		} catch (Exception e) {}
		rs.setIsCertificate(false);
		return rs;
	}
	public ResponseStudentPortalBean printFormForWithdrawal(String sapid) throws Exception {
		ResponseStudentPortalBean response = new ResponseStudentPortalBean();
		
		if(sapid.isEmpty()) {
			response.setMessage("Error!! Invalid sapid");
			response.setState(false);
			return response;
		}
		
		
		Document document = new Document(PageSize.A4);
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(SERVICE_REQUEST_FILES_PATH+"Withdrawal_Form.pdf"));
		document.open();
		
		List<String> sapidList  = Arrays.asList(sapid.split(","));
		int i=0;
		for(String sapId:sapidList) {
			document.setMargins(80, 50, 50, 30);
			ServiceRequestStudentPortal sr;
			StudentStudentPortalBean student;
			try {
				sr = serviceRequestDao.findSRBySapIdAndType(sapId,"Program Withdrawal");	
				student = serviceRequestDao.getSingleStudentsData(sapId);
				
				createPdfHelper.generateHeaderTable(document);
				createPdfHelper.generateStudentInfoTable(document, student);
				createPdfHelper.generateWithdrawalReasonTable(document,sr);
				createPdfHelper.generateDeclaration(document);
				createPdfHelper.generateApprovalFied(document);
				document.newPage();
			} catch (Exception e) {
				response.setMessage("No SR registered by student"+sapId);
				response.setState(false);
				return response;
			}
			i++;
		}
		document.close();
		response.setState(true);
		response.setMessage("Withdrawal form generated for "+i+" students");
		return response;
	}
	

	public ServiceRequestStudentPortal getSubjectRepeatStatusForStudent(String sapid) throws Exception{
		
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		SubjectRepeatSR subjectRepeatSR = subjectRepeatSRFactory.getProductType(student.getProgram());
		return subjectRepeatSR.getSubjectRepeatStatusForStudent(sapid);
		
//		ServiceRequestStudentPortal sr = new ServiceRequestStudentPortal();
//		try {
//
//			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
//			
//			String srProgramType = "131".equals(student.getConsumerProgramStructureId()) ?  ServiceRequestStudentPortal.SUBJECT_REPEAT_MSC_AI_ML : ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX;
//			
//			boolean isRegistrationLiveForStudent = serviceRequestDao.getReRegLiveMBAWX(sapid, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR, srProgramType);
////
////			List<String> listOfSapidsToExtendFor = new ArrayList<String>(Arrays.asList("77420407626", "77119507385"));
////			if(!listOfSapidsToExtendFor.contains(sapid)) {
////				isRegistrationLiveForStudent = false;
////			}
//			
//
//			String charges = MBAWX_TERM_REPEAT_SR_CHARGES;
//			
//			if(!StringUtils.isBlank(student.getEnrollmentMonth()) && !StringUtils.isBlank(student.getEnrollmentYear())) {
//				String monthYear = student.getEnrollmentMonth() + student.getEnrollmentYear();
//				if("Jul2019".equals(monthYear) || "Oct2019".equals(monthYear)) {
//					charges = "6000";
//				}
//			}
//
//			List<ServiceRequestStudentPortal> failedSubjectsList =  serviceRequestDao.getFailedSubjectsForStudentMBAWX(sapid, charges);
//
//			for (ServiceRequestStudentPortal serviceRequest : failedSubjectsList) {
//				if ("5".equals(serviceRequest.getSem())) {
//					charges = "10000";
//					serviceRequest.setAmount("10000");
//				}
//				if(student.getProgram().equals("M.Sc. (AI & ML Ops)")) {
//					int percreditCharges = 8000;
//					if("Foundations of Probability and Statistics for Data Science".equals(serviceRequest.getSubject())) {
//						int costForSubject = 5 * percreditCharges;
//						charges = Integer.toString(costForSubject);
//						serviceRequest.setAmount(Integer.toString(costForSubject));
//					}else if("Data Structures and Algorithms".equals(serviceRequest.getSubject())) {
//						int costForSubject = 4 * percreditCharges;
//						charges = Integer.toString(costForSubject);
//						serviceRequest.setAmount(Integer.toString(costForSubject));
//					}else if("Advanced Mathematical Analysis for Data Science".equals(serviceRequest.getSubject())) {
//						int costForSubject = 3 * percreditCharges;
//						charges = Integer.toString(costForSubject);
//						serviceRequest.setAmount(Integer.toString(costForSubject));
//					}else if("Statistics and Probability in Decision Modeling -1".equalsIgnoreCase(serviceRequest.getSubject())) {
//						int costForSubject = 3 * percreditCharges;
//						charges = Integer.toString(costForSubject);
//						serviceRequest.setAmount(Integer.toString(costForSubject));
//					}else if("Statistics and Probability in Decision Modeling -2".equalsIgnoreCase(serviceRequest.getSubject())) {
//						int costForSubject = 3 * percreditCharges;
//						charges = Integer.toString(costForSubject);
//						serviceRequest.setAmount(Integer.toString(costForSubject));
//					}else if("Advanced Data Structures and Algorithms".equalsIgnoreCase(serviceRequest.getSubject())) {
//						int costForSubject = 2 * percreditCharges;
//						charges = Integer.toString(costForSubject);
//						serviceRequest.setAmount(Integer.toString(costForSubject));
//					}
//				}
//			}
//			
//			List<ServiceRequestStudentPortal> repeatSubjectsApplied =  serviceRequestDao.getRepeatAppliedSubjectsMBAWX(sapid, CURRENT_MBAWX_ACAD_MONTH, CURRENT_MBAWX_ACAD_YEAR, srProgramType);
//			
//
//			
//			if(isRegistrationLiveForStudent) {
//				// TODO : Add a validity check
//								
//				if(failedSubjectsList.size() > 0) {
//					sr.setRepeatSubjects(failedSubjectsList);
//					sr.setRepeatSubjectsApplied(repeatSubjectsApplied);
//					sr.setError("false");
//				} else {
//					sr.setError("true");
//					sr.setErrorMessage("No Subjects available!");
//				}
//				
//			} else {
//				sr.setError("true");
//				sr.setErrorMessage("Subject Repeat not live at the moment.");
//			}
//		} catch(Exception e) {
//			sr.setError("Error checking service request live status.");
//			sr.setErrorMessage(e.getMessage());
//		}
//		return sr;
	}

	public ServiceRequestStudentPortal saveMarksheetRequest1(ServiceRequestStudentPortal sr, HttpServletRequest request) {
		String sapid = sr.getSapId();

		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);

		ArrayList<ServiceRequestStudentPortal> listOfServiceRequestInserted = new ArrayList<ServiceRequestStudentPortal>();
		ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList;
		//System.out.println("sr---"+sr);
		//System.out.println("sr.getMarksheetDetailAndAmountToBePaidList()--"+sr.getMarksheetDetailAndAmountToBePaidList().get(0));
		if (sr.getMarksheetDetailAndAmountToBePaidList() == null) {
			sr.setErrorMessage("MarksheetDetailAndAmountToBePaidList not found.");
			return sr;
//			marksheetDetailAndAmountToBePaidList = (ArrayList<ServiceRequest>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
//			sr.setMarksheetDetailAndAmountToBePaidList(marksheetDetailAndAmountToBePaidList);
		}else {
			boolean isCertificate = student.isCertificateStudent();
			marksheetDetailAndAmountToBePaidList = serviceRequestDao.listOfMarksheetDetailsAndAmountToBePaid(sr,SECOND_MARKSHEET_FEE_PER_SUBJECT,request,isCertificate);//Map the service bean with semester and set in session.Returns a map which will show the description on marksheet summary.//
			//System.out.println("marksheetDetailAndAmountToBePaidList"+marksheetDetailAndAmountToBePaidList);
		}
		
		for (ServiceRequestStudentPortal serviceRequest : marksheetDetailAndAmountToBePaidList) {
        
			int countOfRecords = serviceRequestDao.getMarksheetIssuedCount(serviceRequest);
			//System.out.println("countOfRecords:::::"+countOfRecords);		
			if (countOfRecords > 0) {
				
				sr.setError("Free Marksheet request already received for " + serviceRequest.getMonth() + "-"
						+ serviceRequest.getYear() + " : Sem : " + serviceRequest.getSem());
				return sr;
			}
		}
		for (int i = 0; i < marksheetDetailAndAmountToBePaidList.size(); i++) {
		
			ServiceRequestStudentPortal service = new ServiceRequestStudentPortal();
			service = insertMarksheetServiceRequest(marksheetDetailAndAmountToBePaidList.get(i), request,
					marksheetDetailAndAmountToBePaidList.size());// Passing size to method to flag marksheet only if the
																	// size is greater than 1//
			sr.getMarksheetDetailAndAmountToBePaidList().get(i).setId(service.getId());

		}

		return sr;
	}
	
	public ServiceRequestStudentPortal saveSubjectRegistrationSRPaymentForMBAWX(ServiceRequestStudentPortal sr) throws Exception {

		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sr.getSapId());
		SubjectRepeatSR subjectRepeatSR = subjectRepeatSRFactory.getProductType(student.getProgram());
		return subjectRepeatSR.saveSubjectRegistrationSRPayment(sr);
		
//		String sapid = sr.getSapId();
//		
//		String srProgramType = ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX;
//		String charges = MBAWX_TERM_REPEAT_SR_CHARGES;
//		boolean isMsc = false;
//		if(sapid != null) {
//
//			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
//			
//			srProgramType = "131".equals(student.getConsumerProgramStructureId()) ?  ServiceRequestStudentPortal.SUBJECT_REPEAT_MSC_AI_ML : ServiceRequestStudentPortal.SUBJECT_REPEAT_MBAWX;
//			
//
//			if(!StringUtils.isBlank(student.getEnrollmentMonth()) && !StringUtils.isBlank(student.getEnrollmentYear())) {
//				String monthYear = student.getEnrollmentMonth() + student.getEnrollmentYear();
//				if("Jul2019".equals(monthYear) || "Oct2019".equals(monthYear)) {
//					charges = "6000";
//				}
//
//				if(student.getProgram().equals("M.Sc. (AI & ML Ops)")) {
//					isMsc = true;
//				}
//			}
//		}
//
//		// charges for capstone subject repeat is 4000.
//		List<ServiceRequestStudentPortal> subjects = sr.getRepeatSubjects();
//		Integer mscCharges = 0 ;
//		for (ServiceRequestStudentPortal subjectInfo : subjects) {
//			if(subjectInfo.getSem().equals("5")) {
//				charges = "10000";
//			}
//			
//
//			if(isMsc) {
//				int percreditCharges = 8000;
//				if("Foundations of Probability and Statistics for Data Science".equals(subjectInfo.getSubject())) {
//					mscCharges = mscCharges +  (5 * percreditCharges);
//				}else if("Data Structures and Algorithms".equals(subjectInfo.getSubject())) {
//					mscCharges = mscCharges + (4 * percreditCharges);
//				}else if("Advanced Mathematical Analysis for Data Science".equals(subjectInfo.getSubject())) {
//					mscCharges = mscCharges + (3 * percreditCharges);
//				}else if("Statistics and Probability in Decision Modeling -1".equalsIgnoreCase(subjectInfo.getSubject())) {
//					mscCharges = mscCharges + (3 * percreditCharges);
//				}else if("Statistics and Probability in Decision Modeling -2".equalsIgnoreCase(subjectInfo.getSubject())) {
//					mscCharges = mscCharges + (3 * percreditCharges);
//				}else if("Advanced Data Structures and Algorithms".equalsIgnoreCase(subjectInfo.getSubject())) {
//					mscCharges = mscCharges + (2 * percreditCharges);
//				}
//			}
//		}
//		
//		
//		String trackIdForMultipleMarksheets = sapid+System.currentTimeMillis(); //Since if we set this value in populateServicebean in the loop,the trackId does not remain unique since the loop runs and some time is lost//
//		String totalAmount ;
//		if(isMsc) {
//			totalAmount =  Integer.toString(mscCharges);
//		}else {
//			int baseCharge = Integer.parseInt(charges);
//			totalAmount  = Integer.toString(baseCharge * subjects.size());
//		}
//		sr.setAmount(totalAmount);
//		String desc ="";
//		for (ServiceRequestStudentPortal subjectInfo : subjects) {
//			
//			ServiceRequestStudentPortal srToInsert = new ServiceRequestStudentPortal();
//			srToInsert.setServiceRequestType(srProgramType);
//			srToInsert.setSapId(sapid);
//			srToInsert.setSem(subjectInfo.getSem());
//			srToInsert.setTrackId(trackIdForMultipleMarksheets);
//
//			srToInsert.setDescription(srProgramType + " for student " + sapid + " for Sem : " + subjectInfo.getSem() + " for Subject : " + subjectInfo.getSubject());
//			srToInsert.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
//			srToInsert.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_PAYMENT_PENDING);			
//
//			srToInsert.setYear(CURRENT_MBAWX_ACAD_YEAR);
//			srToInsert.setMonth(CURRENT_MBAWX_ACAD_MONTH);
//			
//			srToInsert.setAmount(totalAmount);
//			srToInsert.setInformationForPostPayment(subjectInfo.getSubject());
//			srToInsert.setPaymentOption(sr.getPaymentOption());
//			srToInsert.setDevice(sr.getDevice());
//
//			serviceRequestDao.insertServiceRequest(srToInsert);
//			serviceRequestDao.insertServiceRequestHistory(srToInsert);
//			sr.setId(srToInsert.getId());
//		
//				desc +="\n>> for Sem : " + subjectInfo.getSem() + ", Subject : " + subjectInfo.getSubject();
//
//		}
//		sr.setDescription(srProgramType + " for student " + sapid + " "+desc);
//		sr.setServiceRequestType(srProgramType);
//		String totalFeesForMarksheetPayment = sr.getTotalAmountToBePayed();
//		//System.out.println("totalFeesForMarksheetPayment::::::"+totalFeesForMarksheetPayment);
//
////		sr.setAmount(totalFeesForMarksheetPayment);
//		sr.setTrackId(trackIdForMultipleMarksheets); 
//		
//		String paymentUrl = "proceedToPaymentGatewaySr?sapId="+sapid+"&paymentOptionName="+sr.getPaymentOption()+"&serviceRequestId="+sr.getId()+ "&productType=" + sr.getProductType();
//		sr.setPaymentUrl(paymentUrl);
//		return sr;
	}
	

	public ServiceRequestStudentPortal insertTermRegistrationServiceRequestMBAWX(ServiceRequestStudentPortal sr){

		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Sem : " + sr.getSem());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Exam");
		sr.setSapId(sapid);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("Multiple Marksheet");
		
		if(sr.getModeOfDispatch() ==null || "NO".equalsIgnoreCase(sr.getModeOfDispatch()))
		{
			sr.setModeOfDispatch("LC");
		}
		serviceRequestDao.insertServiceRequest(sr);
		serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
		return sr;
	}
	

	public ServiceRequestStudentPortal getListOfServiceRequestByTrackId(ServiceRequestStudentPortal sr){

		String sapid = sr.getSapId();
		StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Sem : " + sr.getSem());
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Exam");
		sr.setSapId(sapid);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("Multiple Marksheet");
		
		if(sr.getModeOfDispatch() ==null || "NO".equalsIgnoreCase(sr.getModeOfDispatch()))
		{
			sr.setModeOfDispatch("LC");
		}
		serviceRequestDao.insertServiceRequest(sr);
		serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
		return sr;
	}
	
	public ServiceRequestResponse getSRStatus(ServiceRequestStudentPortal sr) {
		ServiceRequestResponse response = new ServiceRequestResponse();
		
		try {
			List<ServiceRequestStudentPortal> listOfSR = serviceRequestDao.findSRByTrackId(sr.getTrackId(), sr.getSapId());
			if(listOfSR.size() == 0) {
				response.setError("true");
				response.setErrorMessage("Service Request Not Found");
			} else {
				response.setResponse(listOfSR);
				response.setTrackId(sr.getTrackId());
				response.setSapid(sr.getSapId());
				response.setPaymentResponse(listOfSR.get(0).getTranStatus());
				response.setError("false");
			}
		}catch (Exception e) {
			response.setError("true");
			response.setErrorMessage("Error Fetching Service Request!");
		}
		
		return response;
		
	}
	
	@Deprecated			//use massUpdateSR() method
	public ServiceRequestStudentPortal massUploadSR(ServiceRequestStudentPortal bean) throws Exception {
		  
		try {
			
			int srToUpdateCount = bean.getServiceRequestIdList().split("\\r?\\n").length; 
			bean = serviceRequestDao.massUpdateSRStatus(bean);
			ServiceRequestStudentPortal srBean = serviceRequestDao.getServiceRequestBySrId(bean.getId());
			serviceRequestDao.insertServiceRequestStatusHistory(srBean, "Update");
			String[] idArray = bean.getServiceRequestIdList().split("\\r?\\n"); 
			
			for(String id : idArray) {
				if ("Closed".equalsIgnoreCase(bean.getStatus())) {
					serviceRequestDao.setClosedDateForServiceRequest(bean.getStatus(),Long.parseLong(id), bean.getUserId());// Added
					
					ServiceRequestStudentPortal sr = serviceRequestDao.findById(id);
					serviceRequestDao.insertServiceRequestStatusHistory(sr, "Update");
					if(ServiceRequestStudentPortal.EXIT_PROGRAM.equals(sr.getServiceRequestType()) ){
						StudentStudentPortalBean newMappedProgram=getNewMappedPrograms(sr.getSapId());
						serviceRequestRepository.updateProgramStatusForDeregisteredStudent(Long.valueOf(sr.getSapId()), bean.getUserId(),newMappedProgram);
					}
					StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sr.getSapId());
					mailer.sendSRClosureEmail(sr, student.getFirstName(), student.getEmailId());
				} else if ("Cancelled".equalsIgnoreCase(bean.getStatus())) {
					serviceRequestDao.deleteSRHistoryForSR(id);
					ServiceRequestStudentPortal sr = serviceRequestDao.findById(id);
					if(ServiceRequestStudentPortal.ASSIGNMENT_REVALUATION.equals(sr.getServiceRequestType()))
					{
						UnMarkAssignmentRevaluationSubjectsForCancelled(sr);
					}else if(ServiceRequestStudentPortal.TEE_REVALUATION.equals(sr.getServiceRequestType())){
						UnmarkForTeeRevaluationSubjectsForCancelled(sr); 
					}
					else if(ServiceRequestStudentPortal.EXIT_PROGRAM.equals(sr.getServiceRequestType()) || ServiceRequestStudentPortal.DE_REGISTERED.equals(sr.getServiceRequestType()) || ServiceRequestStudentPortal.PROGRAM_WITHDRAWAL.equals(sr.getServiceRequestType())){
						serviceRequestRepository.updateProgramStatusAsActiveForDeregistrationCancellation(Long.valueOf(sr.getSapId()), bean.getUserId());
					}
				}
			}
			bean.setSuccessCount((srToUpdateCount-bean.getErrorList().size())); 
			return bean;
		} catch (Exception e) {   
			throw new Exception(e.getMessage());  
		}  
	}
	
	//Deprecated, use unmarkAssignmentRevaluationForCancelledSubjects() method instead for reference massUploadSR(), as used in updateServiceRequestStatusAndReason()
	private void UnMarkAssignmentRevaluationSubjectsForCancelled(ServiceRequestStudentPortal sr) {
		String sapid = sr.getSapId();
		//System.out.println(sr.getInformationForPostPayment());
		ArrayList<String> revaluationSubjects = new ArrayList<String>(
				Arrays.asList(sr.getInformationForPostPayment().split("~")));
		//System.out.println("revaluationSubjects = " + revaluationSubjects);

		for (String subject : revaluationSubjects) {
			//System.out.println("UnMarking reval for " + subject);
			serviceRequestDao.unmarkForRevaluation(sapid, subject);
		}
	}
	
	/**
	 * Unmark the Assignments (markedForRevaluation) as 'N' for a student in the assignmentsubmission table
	 * @param sapid - student sapid
	 * @param informationForPostPayment - string containing the subjects for Assignment Revaluation (separated using ~)
	 * @return - count of subjects unmarked
	 */
	private int unmarkAssignmentRevaluationForCancelledSubjects(String sapid, String informationForPostPayment) {
		ArrayList<String> revaluationSubjects = new ArrayList<>(Arrays.asList(informationForPostPayment.split("~")));

		int countOfAssignUnmarkedForReval = 0;
		for(String subject: revaluationSubjects) {
			countOfAssignUnmarkedForReval += serviceRequestDao.unmarkForRevaluation(sapid, subject);
		}
		
		return countOfAssignUnmarkedForReval;
	}
	
	//Deprecated, use unmarkTeeRevaluationForCancelledSubjects() method instead for reference massUploadSR(), as used in updateServiceRequestStatusAndReason()
	private void UnmarkForTeeRevaluationSubjectsForCancelled(ServiceRequestStudentPortal sr) {
		String sapid = sr.getSapId();
		//System.out.println(sr.getInformationForPostPayment());
		ArrayList<String> revaluationSubjects = new ArrayList<String>(
				Arrays.asList(sr.getInformationForPostPayment().split("~")));
		//System.out.println("revaluationSubjects = " + revaluationSubjects);

		for (String subject : revaluationSubjects) {
			//System.out.println("UnMarking reval tee for " + subject);
			serviceRequestDao.unmarkForTeeRevaluation(sapid, subject);
		}
	}
	
	/**
	 * Unmark the TEE (markedForRevaluation) as 'N' for a student in the marks table
	 * @param sapid - student sapid
	 * @param informationForPostPayment - string containing the subjects for TEE Revaluation (separated using ~)
	 * @return - count of subjects unmarked
	 */
	private int unmarkTeeRevaluationForCancelledSubjects(String sapid, String informationForPostPayment) {
		ArrayList<String> revaluationSubjects = new ArrayList<>(Arrays.asList(informationForPostPayment.split("~")));
		
		int countOfTeeUnmarkedForReval = 0;
		for (String subject: revaluationSubjects) {
			countOfTeeUnmarkedForReval += serviceRequestDao.unmarkForTeeRevaluation(sapid, subject);
		}
		
		return countOfTeeUnmarkedForReval;
	}
	
	/**
	 * Updates the Request Status of Service Request in service_request_history table if present.
	 * Or Inserts the Service Request record if missing from the history table. 
	 * @param sr - bean containing the ServiceRequest fields
	 * @param userId - id of the Admin user
	 */
	private void updateOrInsertSrHistoryReqStatus(ServiceRequestStudentPortal sr, String userId) {
		String serviceRequestId = String.valueOf(sr.getId());
		
		if(serviceRequestDao.countOfSrHistoryBySrId(serviceRequestId) > 0) {
			//Updates the Request Status of Service Request in service_request_history table
			int countOfSrHistoryRowsUpdated = serviceRequestDao.updateSrHistoryReqStatus(serviceRequestId, sr.getRequestStatus(), userId);
			logger.info(countOfSrHistoryRowsUpdated + " records of requestStatus in SR History table updated by " + userId + " for SR with id: " + serviceRequestId);
		}
		else {
			//Inserts the Service Request record in the service_request_history table
			int countOfSrHistoryRowsInserted = serviceRequestDao.insertSrHistoryReqStatus(sr.getServiceRequestType(), sr.getSapId(), sr.getYear(), sr.getMonth(), 
																		sr.getCreatedDate(), sr.getCreatedBy(), userId, sr.getSem(), serviceRequestId, sr.getRequestStatus());
			logger.info(countOfSrHistoryRowsInserted + " records inserted in SR History table by " + userId + " for SR with id: " + serviceRequestId);
		}
	}
	
	/**
	 * Update the service_request table for Closed Or Cancelled SR
	 * @param serviceRequest - bean containing the Service Request fields
	 * @param userId - id of the Admin user
	 * @throws Exception 
	 */
	private void updateSrForClosedOrCancelStatus(ServiceRequestStudentPortal serviceRequest, String userId) throws Exception {
		//Get the firstName and emailAddress of the student to whom the Service Request belongs
		Map<String, Object> studentDetails = studentDAO.getStudentFirstNameAndEmail(serviceRequest.getSapId());
		String studentFirstName = (String) studentDetails.get("firstName");
		String studentEmailId = (String) studentDetails.get("emailId");
		String consumerProgramStructureId = String.valueOf(studentDetails.get("consumerProgramStructureId"));
		
		//Change detail of student according to the Change Detail Service Request raised and the status of SR
		if(ServiceRequestTypeEnum.CHANGE_FATHER_MOTHER_SPOUSE_NAME.getValue().equals(serviceRequest.getServiceRequestType())
			|| ServiceRequestTypeEnum.CHANGE_IN_CONTACT_DETAILS.getValue().equals(serviceRequest.getServiceRequestType())) {
			Optional<String> changedEmailAddress = changeDetailsSrApproveRejectActions(Long.valueOf(serviceRequest.getSapId()), serviceRequest.getDescription(), 
																						serviceRequest.getRequestStatus(), userId);		//Store Email Address of student if changed
			
			if(changedEmailAddress.isPresent())		//if a value is present in the Optional Object, update emailId of student
				studentEmailId = changedEmailAddress.get();
		}
		
		if(serviceRequest.getRequestStatus().equals("Closed")) {
			
			if(SR_TRACKING_LIST.contains(serviceRequest.getServiceRequestType())) {
				
				if((!massUploadTrackingSRDAO.isSrIdExist(serviceRequest.getId())) && !"Courier".equals(serviceRequest.getModeOfDispatch())) {
					throw new RecordNotExistException("Record Not Exist");
				}
			}
			//Updates the Closed Date of SR in service_request table
			int countOfSrUpdatedToClosed = serviceRequestDao.updateClosedDateForSR(serviceRequest.getId(), userId);
			
			ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(serviceRequest.getId());
			serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
			logger.info(countOfSrUpdatedToClosed + " records of SR Closed Date updated for SR with id: " + serviceRequest.getId());
			
			if (ServiceRequestStudentPortal.ISSUEANCE_OF_BONAFIDE.equals(serviceRequest.getServiceRequestType())) {
				// Allow EBonaFide for MBA-WX also - commented by shivam.pandey.EXT
				//if(!homeController.isTimeboundWiseByConsumerProgramStructureId(consumerProgramStructureId)) {
					Map<String, String> certificateFilePathMap = getEBonafideCertificate(serviceRequest.getId(), serviceRequest.getSapId(), userId);
					logger.info("Issuance of Bonafide certificate generated for student {} and stored in filepath: {}", serviceRequest.getSapId(), certificateFilePathMap);
					
					mailer.sendSRClosureEmailWithAttachment(serviceRequest, studentFirstName, studentEmailId, certificateFilePathMap.get("localFilePath"), certificateFilePathMap.get("awsPath")); 
				//}
			}
			else if(ServiceRequestStudentPortal.EXIT_PROGRAM.equals(serviceRequest.getServiceRequestType())) {
				StudentStudentPortalBean newMappedProgram=getNewMappedPrograms(serviceRequest.getSapId());
				
				if(!StringUtils.isBlank(newMappedProgram.getConsumerProgramStructureId())) {
			    	 int countOfRowsUpdated = serviceRequestRepository.updateProgramStatusForDeregisteredStudent(Long.valueOf(serviceRequest.getSapId()), userId,newMappedProgram);
			    	 exitSrClosed.info(serviceRequest.getSapId() + " SuccessFully Closed and Mapped with "+newMappedProgram.getConsumerProgramStructureId()+" Program by userId = "+ userId);
			    	 mailer.sendSRClosureEmail(serviceRequest, studentFirstName, studentEmailId);
					 logger.info("{} records of ProgramStatus updated to 'Program Withdrawal' by {} for Student: {}", countOfRowsUpdated, userId, serviceRequest.getSapId());
				 }else {
				     int count=serviceRequestDao.updateSrReqStatusCancelReason(serviceRequest.getId(),"Submitted", "", userId);
				     throw new Exception("Not Updated");					
				 }
			
			}
			else {
				mailer.sendSRClosureEmail(serviceRequest, studentFirstName, studentEmailId);
			}
		}
		else {		//For serviceRequest.getRequestStatus().equals("Cancelled")
			if(ServiceRequestStudentPortal.ASSIGNMENT_REVALUATION.equals(serviceRequest.getServiceRequestType())) {
				int countOfAssignUnmarkedForReval = unmarkAssignmentRevaluationForCancelledSubjects(serviceRequest.getSapId(), serviceRequest.getInformationForPostPayment());
				logger.info(countOfAssignUnmarkedForReval + " records of Assignments unmarked for Revaluation by " + userId + " for student: " + serviceRequest.getSapId());
			}
			else if(ServiceRequestStudentPortal.TEE_REVALUATION.equals(serviceRequest.getServiceRequestType())) {
				int countOfTeeUnmarkedForReval = unmarkTeeRevaluationForCancelledSubjects(serviceRequest.getSapId(), serviceRequest.getInformationForPostPayment());
				logger.info(countOfTeeUnmarkedForReval + " records of TEE unmarked for Revaluation by " + userId + " for student: " + serviceRequest.getSapId());
			}
			else if(ServiceRequestStudentPortal.EXIT_PROGRAM.equals(serviceRequest.getServiceRequestType()) 
					|| ServiceRequestStudentPortal.DE_REGISTERED.equals(serviceRequest.getServiceRequestType()) 
					|| ServiceRequestStudentPortal.PROGRAM_WITHDRAWAL.equals(serviceRequest.getServiceRequestType())) {
				int countOfRowsUpdated = serviceRequestRepository.updateProgramStatusAsActiveForDeregistrationCancellation(Long.valueOf(serviceRequest.getSapId()), userId);
				logger.info("{} records of ProgramStatus updated to 'null' by {} for Student: {}", countOfRowsUpdated, userId, serviceRequest.getSapId());
			}
			
			mailer.sendSRCancellationEmail(serviceRequest, studentFirstName, studentEmailId);
		}
	}
	
	/**
	 * Update the Request Status And Cancellation Reason in service_request & service_request_history table
	 * @param srAdminUpdateDto - DTO which contains the Service Request details
	 * @param userId - id of the Admin user prompting these changes
	 * @throws Exception
	 */
	@Transactional
	public void updateServiceRequestStatusAndReason(SrAdminUpdateDto srAdminUpdateDto, String userId) throws Exception {
		ServiceRequestStudentPortal serviceRequest = ObjectConverter.convertObjToXXX(srAdminUpdateDto, new TypeReference<ServiceRequestStudentPortal>() {});
		Long srId = serviceRequest.getId();
		
		//Updates the Request Status And Cancellation Reason in service_request table
		int countOfSrRowsUpdated = serviceRequestDao.updateSrReqStatusCancelReason(srId, serviceRequest.getRequestStatus(), serviceRequest.getCancellationReason(), userId);
		logger.info(countOfSrRowsUpdated + " records of SR Status & CancellationReason updated by " + userId + " for SR with id: " + srId);
		ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(srId);
		serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
		
		boolean srBeanRepopulatedFlag = false;			//flag - used to denote if the SR Bean is re-populated with additional details
		if(Arrays.asList(srTypeInSrHistory).contains(serviceRequest.getServiceRequestType())) {
			if(serviceRequest.getRequestStatus().equals("Cancelled")) {
				//Deletes the Service Request record in service_request_history table
				int countOfSrHistoryRowsDeleted = serviceRequestDao.deleteSrHistoryBySrId(srId);
				logger.info(countOfSrHistoryRowsDeleted + " records of SR Status in History table deleted by " + userId + " for SR with id: " + srId);
			}
			else {
				ObjectDifferenceCopier.copyObjectExtendingSuperClassDifference(serviceRequest, serviceRequestDao.findSrById(srId));
				srBeanRepopulatedFlag = true;
				
				//Updates the Request Status in service_request_history table
				updateOrInsertSrHistoryReqStatus(serviceRequest, userId);
			}
		}
		
		if(serviceRequest.getRequestStatus().equals("Closed") || serviceRequest.getRequestStatus().equals("Cancelled")) {
			if(!srBeanRepopulatedFlag)
				ObjectDifferenceCopier.copyObjectExtendingSuperClassDifference(serviceRequest, serviceRequestDao.findSrById(srId));
			
			//Updates the service_request table for Closed And Cancelled SR
			updateSrForClosedOrCancelStatus(serviceRequest, userId);
		}
	}

	public ArrayList<String> getSrRequestTypes(String sapid) {
  		// don't use session inside theses function,because mobile app using these function
   
    StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
   
    ArrayList<String> requestTypes=new ArrayList<String>(); 
    if(student != null) {
    requestTypes.addAll(serviceRequestDao.getSRTypesForExtendedTimeStudents(sapid)) ;
    requestTypes = serviceRequestDao.getSRTypesForConsumerProgramStructureId(student.getConsumerProgramStructureId());
    
    if ("Online".equals(student.getExamMode())) {
         requestTypes.remove(ServiceRequestStudentPortal.OFFLINE_TEE_REVALUATION);
         requestTypes.remove(ServiceRequestStudentPortal.PHOTOCOPY_OF_ANSWERBOOK);
    }
   
    ServiceRequestType type=(ServiceRequestType)serviceRequestDao.getSRType("Scribe for Term End Exam");
    Long current_timestamp=Timestamp.from(Instant.now()).getTime();
    Long scribe_start_timestamp=Timestamp.valueOf(type.getStartTime()).getTime();
    Long scribe_end_timestamp=Timestamp.valueOf(type.getEndTime()).getTime();
	if(!(scribe_start_timestamp<=current_timestamp&&current_timestamp<=scribe_end_timestamp)){
		requestTypes.remove(ServiceRequestStudentPortal.SCRIBE_FOR_TERM_END_EXAM);
	}
    }
 
    return requestTypes;
}

	


	public ServiceRequestStudentPortal saveDuplicateStudyKit(ServiceRequestStudentPortal sr) {
			String sapid = sr.getSapId();
			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
			String sem = sr.getSem();
			sr.setDescription(sr.getServiceRequestType() + " for Semester " + sem + " for student " + sapid
					+ " for Program " + student.getProgram());
			sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
			sr.setCategory("Academics");
			populateServiceRequestObject(sr);
			sr.setIsFlagged("N");
			sr.setSrAttribute("");
			try {
				//sr.setLandMark(student.getLandMark());
				sr.setPin(student.getPin());
				sr.setLocality(student.getLocality());
				sr.setStreet(student.getStreet());
				sr.setHouseNoName(student.getHouseNoName());
				sr.setCity(student.getCity());
				sr.setState(student.getState());
				sr.setCountry(student.getCountry());
				}catch(Exception e ) {
					//System.out.println("Failed to set shipping address fields ");
				}
			serviceRequestDao.insertServiceRequest(sr);
			
			return sr;
			
		}

	public ServiceRequestStudentPortal saveSingleBook(ServiceRequestStudentPortal sr) {
			String sapid = sr.getSapId();
			String program ="";
			StudentStudentPortalBean student = serviceRequestDao.getSingleStudentsData(sapid);
			
			String subject = sr.getSubject();
			sr.setDescription(sr.getServiceRequestType() + " for Subject " + subject + " for student " + sapid
					+ " for Program " + student.getProgram());
			sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_INITIATED);
			sr.setCategory("Academics");
			try {
				//sr.setLandMark(student.getLandMark());
				sr.setPin(student.getPin());
				sr.setLocality(student.getLocality());
				sr.setStreet(student.getStreet());
				sr.setHouseNoName(student.getHouseNoName());
				sr.setCity(student.getCity());
				sr.setState(student.getState());
				sr.setCountry(student.getCountry());
				}catch(Exception e ) {
					//System.out.println("Failed to set shipping address fields ");
				}
			populateServiceRequestObject(sr);
			sr.setIssued("N");
			sr.setSrAttribute("");
			serviceRequestDao.insertServiceRequest(sr);
			return sr;
			
		}
	public HashMap<String, String> getDispatches(String sapid) {
		// TODO Auto-generated method stub
		HashMap<String, String> mapOfDispatchParameters = salesforceHelper.getMapOfDispatchParameterAndValues(sapid);
		return mapOfDispatchParameters;
	}
	
	/**
	 * Generating Issuance of Bonafide certificate pdf and uploading filepath to AWS and updating records in db
	 * @param srId - serviceRequestId
	 * @param sapId - student sapId
	 * @param userId - user
	 * @return awsFilePath and localFilePath 
	 */
	public Map<String, String> getEBonafideCertificate(Long srId, String sapId, String userId) {
		StudentStudentPortalBean studentInfo = serviceRequestDao.getSingleStudentsData(sapId);
		String specifiedReason = serviceRequestDao.getSRAdditionalInfo(srId);
		boolean isExitStudent = serviceRequestDao.checkExitStudent(sapId);
		HashMap<String, ProgramsStudentPortalBean> programMap = serviceRequestDao.getProgramMap();
		
		Map<String, String> certificateMap = new HashMap<>();
		Map<String, String> eBonafidePDFContent = new HashMap<String, String>();
		try {
			if(eBonafidePDFContentExist(srId)) 
				eBonafidePDFContent = getEBonafidePDFContentMap(srId);
			else 
				eBonafidePDFContent = generateEBonafidePDFContent(srId, studentInfo, specifiedReason, programMap, isExitStudent, true);
			
			String purpose = getEBonafidePurposeCategory(specifiedReason);
			certificateMap = CertificatePDFCreator.generateEBonafideCertificate(studentInfo, SERVICE_REQUEST_FILES_PATH, 
			isExitStudent, true, eBonafidePDFContent, purpose, srId);
		} 
		catch(Exception e) {
			logger.info("failed to generate E-Bonafide certificate pdf, due to "+e.toString());
			throw new RuntimeException("failed to generate E-Bonafide certificate pdf, due to "+e.getMessage());
		}
		
		int countOfRowsUpdated = serviceRequestDao.updateSRWithCertificateNumberAndCurrentDate(srId, certificateMap.get("certificateNumber"));
		ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(srId);
		serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
		
		logger.info("certificate number and current date updated for {} records", countOfRowsUpdated);
		
		String localFilePath = certificateMap.get("filePath") + certificateMap.get("fileName");
		String bucketFolderName = ("PROD".equals(ENVIRONMENT)) ? ISSUANCE_OF_BONAFIDE_FOLDER : SR_FILES_TEST_FOLDER;
		String awsFilePath =  bucketFolderName + certificateMap.get("fileName");
		HashMap<String, String> awsUploadResponseMap = awsHelper.uploadLocalFile(localFilePath, awsFilePath, AWS_SR_FILES_BUCKET, bucketFolderName);
		
		if ("success".equals(awsUploadResponseMap.get("status"))) {
			String awsPath = awsUploadResponseMap.get("url");
			String filePathName = awsPath;
			filePathName = filePathName.replaceAll(SR_FILES_S3_PATH, "");
			int countOfRowsInserted = serviceRequestDao.insertSrGeneratedDocumentFilePath(sapId, EBONAFIDE_SR_DOCUMENT_TYPE, filePathName, userId);
			logger.info("{} records inserted in receipt_hallticket table for file: {}", countOfRowsInserted, awsPath);
			
			Map<String, String> map = new HashMap<>();
			map.put("awsPath", awsPath);
			map.put("localFilePath", localFilePath);
			return map;
		} 
		else {
			logger.error("failed to upload bonafide certificate pdf to AWS, pdf generated locally " + awsUploadResponseMap.get("url"));
			throw new RuntimeException("failed to upload bonafide certificate pdf to AWS" + awsUploadResponseMap.get("url"));
		} 
	}
	
	/** 
	 * Getting SR details of Issuance of Bonafide for showing on Preview button
	 * @param sap
	 * @param reason
	 * @param srid
	 * @return SR details for Issuance of Bonafide
	 */
	public StudentSrDTO eBonafidePDFDetails(String sapId, String reason, Long srId) {
		StudentSrDTO srDto = new StudentSrDTO();
		String filePath = getFilePathBySRId(sapId);

		if (StringUtils.isNotBlank(filePath))
			srDto.setFilePath(filePath);
		
		else if(eBonafidePDFContentExist(srId)){
			 Map<String, String> eBonafidePDFContent = getEBonafidePDFContentMap(srId);
			 srDto.setCustomPDFContent(eBonafidePDFContent);
		}
		else 
			srDto = getEBonafidePDFDetails(sapId, reason, srId);
		
		return srDto;
	}
	
	public StudentSrDTO getEBonafidePDFDetails(String sapId, String reason, Long srId) {
		
		StudentSrDTO studentDetails = serviceRequestDao.getStudentDetailsBySapId(sapId);
		
		StudentSrDTO programDetails = serviceRequestDao.getProgramDetailsByProgram(studentDetails.getProgram());
		
		return mapEBonafidePDFDetails(studentDetails, programDetails, sapId, reason);
	}
	
	private StudentSrDTO mapEBonafidePDFDetails(StudentSrDTO studentDetails, StudentSrDTO programDetails,
		String sapId, String reason) {
		StudentSrDTO tempBean = new StudentSrDTO();
		
		tempBean.setSapId(sapId);
		tempBean.setAdditionalInfo1(reason);
		tempBean.setFirstName(studentDetails.getFirstName());
		tempBean.setLastName(studentDetails.getLastName());
		tempBean.setEnrollmentMonth(studentDetails.getEnrollmentMonth());
		tempBean.setEnrollmentYear(studentDetails.getEnrollmentYear());
		tempBean.setSem(studentDetails.getSem());
		tempBean.setProgramStatus(studentDetails.getProgramStatus());
		tempBean.setGender(studentDetails.getGender());
		tempBean.setProgram(studentDetails.getProgram());
		tempBean.setValidityEndMonth(studentDetails.getValidityEndMonth());
		tempBean.setValidityEndYear(studentDetails.getValidityEndYear());
		tempBean.setProgramDuration(programDetails.getProgramDuration());
		tempBean.setProgramDurationUnit(programDetails.getProgramDurationUnit());
		tempBean.setProgramName(programDetails.getProgramName()+" "+"("+studentDetails.getProgram()+")");
		
		return tempBean;
	}
	
	/**
	 * Getting filePath of certificate PDF 
	 * @param sapid
	 * @return String of filePath
	 */
	public String getFilePathBySRId(String sapId) {
		String filePath = new String();
		try {
			filePath = serviceRequestDao.getFilePathBySrId(sapId);
			
			if (StringUtils.isNotBlank(filePath))
				return SR_FILES_S3_PATH + filePath;
			
		} catch (Exception e) {
			logger.error("failed to get filepath of certificate pdf" + e.getMessage());
//			throw new RuntimeException("failed to get filepath of certificate PDF" + e.getMessage());
		}
		return filePath;
	}
	
	public void saveBonafideRequest(ServiceRequestStudentPortal sr,HttpServletRequest request) {
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		String sapid = student.getSapid();
//		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid + " for Exam " + sr.getMonth() + "-"
//				+ sr.getYear() + " & Sem : " + sr.getSem());
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapid);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Exam");
		sr.setSapId(sapid);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapid);
		sr.setLastModifiedBy(sapid);
		sr.setSrAttribute("");
		
		if(sr.getAdditionalInfo1().equals("") || sr.getAdditionalInfo1().equals(null))
			sr.setAdditionalInfo1(sr.getPurpose());
		else 
			sr.setAdditionalInfo1("Others : "+removeSpacesAndSymbols(sr.getAdditionalInfo1()));
		
		serviceRequestDao.insertServiceRequest(sr);
		serviceRequestDao.insertServiceRequestHistory(sr);// For keeping track
		// how many times
		// same request was
		// made so that next
		// time they can be
		// charged
//		request.getSession().setAttribute("sr", sr);
//		m.addAttribute("sr", sr);
		
		//create a service layer for it
		saveEBonafideContent(sr.getId(), sapid, sr.getAdditionalInfo1().replaceFirst("Others : ", ""));
		
		MailSender mailer = (MailSender) act.getBean("mailer");
		mailer.sendSREmail(sr, student);
	}
	
	public ServiceRequestStudentPortal getServiceRequest(Long srId) {
		ServiceRequestStudentPortal sr = serviceRequestDao.getServiceRequestBySrId(srId);
		return sr;
	}
	
	/**
	 * Depending on the type of Change Father/Mother/Spouse Name Service Request raised by the student, 
	 * the details are validated and stored, a confirmation mail is sent to the student on success.
	 * @param srDto - DTO containing required details for the Change Father/Mother/Spouse Name Service Request
	 * @return - Bean containing the Service Request details
	 */
	@Transactional
	public ServiceRequestStudentPortal changeFatherMotherSpouseName(ChangeDetailsSRDto srDto) {
		ServiceRequestAbstract changeFaMoSpoSR = serviceRequestFactory.createServiceRequest(ServiceRequestTypeEnum.CHANGE_FATHER_MOTHER_SPOUSE_NAME);
		ChangeDetailsSRStrategyInterface changeDetailsSRStrategy = changeDetailsSRStrategyFactory.findStrategy(ProfileDetailEnum.getByValue(srDto.getDetailType()));
		
		Map<String, Object> studentDetailsMap = changeFaMoSpoSR.studentDetails(srDto.getSapid());
		String presentDetailValue = Objects.nonNull(studentDetailsMap.get(srDto.getDetailType())) ? String.valueOf(studentDetailsMap.get(srDto.getDetailType())) : "";		//Null stored as empty String
		logger.info("Retrieving the Student {} value already present in database: {}", srDto.getDetailType(), presentDetailValue);
		
		logger.info("Performing validations on the input submitted by the student for the {} Service Request.", changeDetailsSRStrategy.getDetailType().getValue());
		changeDetailsSRStrategy.performChangeDetailsSrChecks(presentDetailValue, srDto);			//Perform Checks for Change Details Service Request
		
		String description = changeDetailsSRStrategy.serviceRequestDescription(srDto.getSapid(), srDto.getUpdateValue(), srDto.getCurrentValue());
		logger.info("Description created for the Change Details Service Request for Change in {}: {}", srDto.getDetailType(), description);
		ServiceRequestStudentPortal serviceRequestBean = changeFaMoSpoSR.createServiceRequestBean(srDto.getSapid(), ServiceRequestTypeEnum.CHANGE_FATHER_MOTHER_SPOUSE_NAME,
																									description, srDto.getDevice());
		logger.info("Service Request bean created for the {} SR, inserting the Service Request record in database.", changeDetailsSRStrategy.getDetailType().getValue());
		
		long serviceRequestId = changeFaMoSpoSR.storeFreeServiceRequestRecord(serviceRequestBean);
		
		serviceRequestBean.setId(serviceRequestId);
		serviceRequestDao.insertServiceRequestStatusHistory(serviceRequestBean,"Create");
		logger.info("The {} Service Request record was successfully stored in database with an ID: {}", changeDetailsSRStrategy.getDetailType().getValue(), serviceRequestId);
		
		String bucketFolderName = (ENVIRONMENT.equals("PROD")) ? "ChangeFatherMotherSpouseName/" : "ServiceRequestDocumentsUploadTest/";
		ServiceRequestDocumentBean srDocumentBean = changeFaMoSpoSR.uploadServiceRequestDocument(srDto.getSapid(), serviceRequestId, "FatherMotherSpouseName_Change_Document", 
																								srDto.getSupportingDocument(), bucketFolderName);
		logger.info("Document successfully uploaded for the {} Service Request: {}", changeDetailsSRStrategy.getDetailType().getValue(), srDocumentBean.toString());
		
		srDocumentBean.setFilePath(changeFaMoSpoSR.removeStaticS3PathFromFilePath(srDocumentBean.getFilePath()));		//Replace the Static S3 path from the filePath of the Service Request document
		long srDocumentId = changeFaMoSpoSR.insertServiceRequestDocumentRecord(srDocumentBean);
		logger.info("Service Request documents record inserted for the {} SR with an ID: {}", changeDetailsSRStrategy.getDetailType().getValue(), srDocumentId);
		long noOfRowsUpdated = changeFaMoSpoSR.updateSrRecordDocumentStatus(serviceRequestId, true, String.valueOf(srDto.getSapid()));
		logger.info("HasDocuments flag changed to true for {} records of {} Service Request.", noOfRowsUpdated, changeDetailsSRStrategy.getDetailType().getValue());
		
		changeFaMoSpoSR.sendSuccessfulSrRequestMail(serviceRequestBean, String.valueOf(studentDetailsMap.get("sapid")), String.valueOf(studentDetailsMap.get("firstName")), 
													String.valueOf(studentDetailsMap.get("lastName")), String.valueOf(studentDetailsMap.get("emailId")));
		changeFaMoSpoSR.sendSuccessfulSrCreatedMail(serviceRequestBean, String.valueOf(studentDetailsMap.get("sapid")), 
													String.valueOf(studentDetailsMap.get("firstName")), String.valueOf(studentDetailsMap.get("lastName")));
		logger.info("Change in Contact Details Service Request Confirmation mails successfully sent to the Student: {} and the concerned team.", srDto.getSapid());
		
		return serviceRequestBean;
	}
	
	/**
	 * Depending on the type of Change in Contact Details Service Request raised by the student, 
	 * the details are validated and stored, a confirmation mail is sent to the student on success.
	 * @param srDto - DTO containing required details for the Change in Contact Details Service Request
	 * @return - Bean containing the Service Request details
	 * @throws Exception 
	 */
	@Transactional
	public ServiceRequestStudentPortal changeContactDetails(ChangeDetailsSRDto srDto) {
		ServiceRequestAbstract changeContactDetailsSR = serviceRequestFactory.createServiceRequest(ServiceRequestTypeEnum.CHANGE_IN_CONTACT_DETAILS);
		ChangeDetailsSRStrategyInterface changeDetailsSRStrategy = changeDetailsSRStrategyFactory.findStrategy(ProfileDetailEnum.getByValue(srDto.getDetailType()));
		
		Map<String, Object> studentDetailsMap = changeContactDetailsSR.studentDetails(srDto.getSapid());
		String presentDetailValue = Objects.nonNull(studentDetailsMap.get(srDto.getDetailType())) ? String.valueOf(studentDetailsMap.get(srDto.getDetailType())) : "";		//Null stored as empty String
		logger.info("Retrieving the Student {} value already present in database: {}", srDto.getDetailType(), presentDetailValue);
		
		logger.info("Performing validations on the input submitted by the student for the Change in Contact Details Service Request.");
		changeDetailsSRStrategy.performChangeDetailsSrChecks(presentDetailValue, srDto);			//Perform Checks for Change Details Service Request
		
		String description = changeDetailsSRStrategy.serviceRequestDescription(srDto.getSapid(), srDto.getUpdateValue(), srDto.getCurrentValue());
		logger.info("Description created for the Change Details Service Request for Change in {}: {}", srDto.getDetailType(), description);
		ServiceRequestStudentPortal serviceRequestBean = changeContactDetailsSR.createServiceRequestBean(srDto.getSapid(), ServiceRequestTypeEnum.CHANGE_IN_CONTACT_DETAILS, 
																										description, srDto.getDevice());
		logger.info("Service Request bean created for the Change in Contact Details SR, inserting the Service Request record in database.");
		
		long serviceRequestId = changeContactDetailsSR.storeFreeServiceRequestRecord(serviceRequestBean);
		serviceRequestBean.setId(serviceRequestId);
		serviceRequestDao.insertServiceRequestStatusHistory(serviceRequestBean,"Create");
		logger.info("The Change in Contact Details Service Request record was successfully stored in database with an ID: {}", serviceRequestId);
//		boolean updation= verifyOtp.updateContactDetails(srDto);
//		System.out.println("Updation Successfull  "+ updation);
		SrAdminUpdateDto dto = new SrAdminUpdateDto();
		dto.setId(serviceRequestId);
		dto.setRequestStatus("Closed");
		dto.setServiceRequestType("Change in Contact Details");
		dto.setSapid(srDto.getSapid().toString());
		System.out.println("This is the dto " +dto.toString());
		try {
			updateServiceRequestStatusAndReason(dto,srDto.getSapid().toString());
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		changeContactDetailsSR.sendSuccessfulSrRequestMail(serviceRequestBean, String.valueOf(studentDetailsMap.get("sapid")), String.valueOf(studentDetailsMap.get("firstName")), 
															String.valueOf(studentDetailsMap.get("lastName")), String.valueOf(studentDetailsMap.get("emailId")));
		changeContactDetailsSR.sendSuccessfulSrCreatedMail(serviceRequestBean, String.valueOf(studentDetailsMap.get("sapid")), 
															String.valueOf(studentDetailsMap.get("firstName")), String.valueOf(studentDetailsMap.get("lastName")));
		logger.info("Change in Contact Details Service Request Confirmation mails successfully sent to the Student: {} and the concerned team.", srDto.getSapid());
		
		return serviceRequestBean;
	}
	
	/**
	 * The detail type from the Service Request description is returned.
	 * @param description - Service Request description
	 * @return SR detail type as a String
	 */
	private String getDetailTypeFromSrDescription(String description) {
		Pattern pattern = Pattern.compile("\\[Change(.*?)\\]");		//(.*?) is to return a group (of any characters) containing of a non-greedy match 
		Matcher matcher = pattern.matcher(description);
		
		String detailType = "";
		while (matcher.find())
			detailType = matcher.group(1).trim();
		
		if("spouseName".equals(detailType))
			detailType = "husbandName";
		return detailType;
	}
	
	/**
	 * Check if the provided sapid is valid and fetch student' fatherName, motherName and spouseName details.
	 * @param sapid - studentNo of the student
	 * @return Map containing the student' details
	 */
	public Map<String, Object> studentFatherMotherHusbandName(Long sapid)  {
		try {
			if(Objects.isNull(sapid) || String.valueOf(sapid).length() != 11) {		//Check if sapid is valid
				logger.error("Provided StudentNo: {}, is either null or does not contain 11 characters.", sapid);
				throw new IllegalArgumentException("Illegal StudentNo provided!");
			}
			
			return serviceRequestRepository.studentFatherMotherHusbandName(sapid);
		}
		catch(EmptyResultDataAccessException ex) {
//			ex.printStackTrace();
			logger.error("Invalid StudentNo: {}, unable to fetch student details. Exception thrown: {}", sapid, ex.toString());
			throw new IllegalArgumentException("Invalid StudentNo! Unable to fetch Student details.");
		}
	}
	
	/**
	 * Check if the provided sapid is valid and fetch student' emailId and mobileNo details.
	 * @param sapid - studentNo of the student
	 * @return Map containing the student' details
	 */
	public Map<String, Object> studentEmailIdMobileNo(Long sapid)  {
		try {
			if(Objects.isNull(sapid) || String.valueOf(sapid).length() != 11) {		//Check if sapid is valid
				logger.error("Provided StudentNo: {}, is either null or does not contain 11 characters.", sapid);
				throw new IllegalArgumentException("Illegal StudentNo provided!");
			}
			
			return serviceRequestRepository.studentEmailIdMobileNo(sapid);
		}
		catch(EmptyResultDataAccessException ex) {
//			ex.printStackTrace();
			logger.error("Invalid StudentNo: {}, unable to fetch student details. Exception thrown: {}", sapid, ex.toString());
			throw new IllegalArgumentException("Invalid StudentNo! Unable to fetch Student details.");
		}
	}
	
	/**
	 * Checks if the student is eligible to raise the Change Details Service Request to update a particular detail.
	 * @param sapid - studentNo of the Student
	 * @param detailType - Change Details SR detail type that is to be updated
	 * @return - boolean value indicating if the student is eligible to raise the Change Details SR
	 */
	public boolean checkStudentContactDetailsSrEligibility(Long sapid, String detailType) {
		ChangeDetailsSRStrategyInterface changeDetailsSR = changeDetailsSRStrategyFactory.findStrategy(ProfileDetailEnum.getByValue(detailType));
		return changeDetailsSR.checkSrEligibility(sapid);
	}
	
	/**
	 * Change the profile detail of student according to the Change Details Service Request raised.
	 * If the Change Details request was for change in EmailId, the updated Email Address is returned, 
	 * otherwise an empty Optional is returned at the end of the method.
	 * @param studentNo - sapid of the Student
	 * @param srDescription - description of the Service Request
	 * @param srRequestStatus - request Status of the Service Request
	 * @param userId - id of the user
	 * @return Optional String containing the email address of the student if emailId is updated, otherwise an empty Optional
	 */
	private Optional<String> changeDetailsSrApproveRejectActions(Long studentNo, String srDescription, String srRequestStatus, String userId) {
		ProfileDetailEnum detailTypeEnum = ProfileDetailEnum.getByValue(getDetailTypeFromSrDescription(srDescription));
		ChangeDetailsSRStrategyInterface changeDetailsSR = changeDetailsSRStrategyFactory.findStrategy(detailTypeEnum);
		Map<String, String> srDescriptionValuesMap = changeDetailsSR.getValuesFromSrDescription(srDescription);
		logger.info("Values obtained from the Change Details Service Request description: {}", srDescriptionValuesMap.toString());
		
		if(!srDescriptionValuesMap.get("sapid").equals(String.valueOf(studentNo)))		//Checking if the sapid from SR Description matches the studentNo
			throw new IllegalArgumentException("StudentNo obtained from the Change Details ServiceRequest description does not match the sapid obtained from the Service Request record.");
		
		//If the Service Request status is Closed the new value is considered, if Cancelled the old value is considered
		String value = srRequestStatus.equals("Closed") ? srDescriptionValuesMap.get("updateValue") : srDescriptionValuesMap.get("currentValue");
		int updatedRows = changeDetailsSR.updateDetail(studentNo, value, userId);
		logger.info("{}: {} updated for Student: {}, no. of rows affected: {}", detailTypeEnum.getValue(), value, studentNo, updatedRows);
		
		if("PROD".equalsIgnoreCase(ENVIRONMENT)) {
			String currentLdapValue = "";					//Variable to store ldap attribute to make ldap updation transactional
			if(changeDetailsSR.isDetailPresentInLdap()) {
				currentLdapValue = changeDetailsSR.getStoredLdapAttributeValue(studentNo);		
				logger.info("{} attribute from the LDAP Object of Student {} stored: {}", detailTypeEnum.getValue(), studentNo, currentLdapValue);
				
				changeDetailsSR.updateLdapAttribute(studentNo, value);
				logger.info("Successfully updated {} attribute value in LDAP for Student: {}", detailTypeEnum.getValue(), studentNo);
			}
			
			try {
				//bypass student before Jul2014 as Student record not present in Salesforce
				if(changeDetailsSR.checkStudentEnrollmentAfterSalesforceDate(studentNo)) {
					String updatedSalesforceAccountId = changeDetailsSR.updateSalesforceField(studentNo, value);
					logger.info("{} updated successfully on Salesforce of Student with Account Id: {}", detailTypeEnum.getValue(), updatedSalesforceAccountId);
				}
				
				//added to update Id card after mobile number update
				if(ProfileDetailEnum.MOBILE_NO == detailTypeEnum){
					StudentStudentPortalBean student =studentDAO.getStudentDetailsBySapid(studentNo.toString());
					if("PROD".equalsIgnoreCase(ENVIRONMENT)){
					idCardService.updateIdCard(student);
					}
				}
				
			}
			catch(Exception ex) {
//				ex.printStackTrace();
				if(changeDetailsSR.isDetailPresentInLdap()) {
					//Reverting above stored student attribute in LDAP
					changeDetailsSR.updateLdapAttribute(studentNo, currentLdapValue);
					logger.error("{} attribute value reverted in LDAP for Student: {}", detailTypeEnum.getValue(), studentNo);
				}
				throw new RuntimeException("Unable to update Student details in Salesforce!");
			}
		}
		
		return (ProfileDetailEnum.EMAIL_ID == detailTypeEnum) ? Optional.of(value) : Optional.empty();
	}
	
	public ServiceRequestStudentPortal checkIfMScAIStudentApplicableForExitProgram(StudentStudentPortalBean student) {
		ServiceRequestStudentPortal servicerequest = new ServiceRequestStudentPortal();
		try {
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		    Date date = new Date();  
		    String currentDateTime = formatter.format(date);  
			    
		    //get latest Sem of Student
			String sem = serviceRequestDao.getLatestSemBySapId(student.getSapid());
			int latestSem = Integer.parseInt(sem); 
				
			if(latestSem==5 || latestSem==4) {
				int countOfSubject = 0;
				List<String> getfailedPssId = serviceRequestDao.getPssIdOfStudentBySapId(student.getSapid()); //get All failed PssId
				
				int getfailedCount = 0;
			    if(getfailedPssId.size()>0) {
			    	getfailedCount = serviceRequestDao.getSemByFailedPssId((ArrayList<String>) getfailedPssId);  //get Only failed count of sem 1 to 4 
			    }

			 	  if(latestSem == 4){
			 		  countOfSubject = serviceRequestDao.getCountOfTotalPassedMScSubjectBySapid(student.getSapid());
				       if(countOfSubject == 13) {
						  servicerequest.setIsCertificate(true);
					 	  servicerequest.setCertificationType("Professional Diploma in Data Science");
						  return servicerequest;
					  }else {
				    	 servicerequest.setError("You have not cleared all subjects of Term 1 to Term 4!");
				    	  servicerequest.setIsCertificate(false);
				    	  return servicerequest;
				      }
			 	  }else if(latestSem==5 && getfailedCount==0){
			 		  StudentStudentPortalBean bean = serviceRequestDao.getMonthAndYearOfSemV(student.getSapid());			 			    
			 		  ArrayList<String> getTimeBoundsubjectconfigId = (ArrayList<String>) serviceRequestDao.getTimeBoundsubjectconfigId(student.getSapid());		 			
			 		  String DateAndTime=serviceRequestDao.getTimeBoundStartedDateAndTime(getTimeBoundsubjectconfigId,bean.getYear(),bean.getMonth());
			 		  	if(!StringUtils.isBlank(DateAndTime)) {
			 		  		if(currentDateTime.compareTo(DateAndTime) > 0) {
								servicerequest.setIsCertificate(false);
							 	servicerequest.setError("The lectures have started for term 5!");
								return servicerequest;
			 		  		}else {
			 		  			servicerequest.setIsCertificate(true);
			 		  			servicerequest.setCertificationType("Professional Diploma in Data Science");
			 		  			return servicerequest;
						 	}
			 		  	}else {
			 		  		servicerequest.setIsCertificate(true);
			 		  		servicerequest.setCertificationType("Professional Diploma in Data Science");
			 		  		return servicerequest;
			 		  	}
			 	 }
			}else {
				servicerequest.setIsCertificate(false);
				servicerequest.setError("Exit SR will be applicable after Term 4!");
				return servicerequest;
			}
			
		}catch (Exception e) {
			logger.error("Error in checkIfMScAIStudentApplicableForExitProgram for SAPID: "+student.getSapid()+ " ERROR: "+e.getMessage());
		}
		servicerequest.setIsCertificate(false);
		return servicerequest;
		
	}
	public String returnmonthforResitExam()
	{
		String exammonth="";
		LocalDate currentdate = LocalDate.now();
		String month=String.valueOf(currentdate.getMonth());
		switch(month){
		case "JANUARY":
			exammonth="Apr";
			break;
		case "FEBRUARY":
			exammonth="Apr";
			break;
		case "MARCH":
			exammonth="Apr";
			break;
		case "APRIL":
			exammonth="Apr";
			break;
		case "MAY":
			exammonth="Jun";
			break;
		case "JUNE":
			exammonth="Jun";
			break;
		case "JULY":
			exammonth="Sep";
			break;
		case "AUGUST":
			exammonth="Sep";
			break;
		case "SEPTEMBER":
			exammonth="Sep";
			break;
		case "OCTOBER":
			exammonth="Dec";
			break;
		case "NOVEMBER":
			exammonth="Dec";
			break;
		case "DECEMBER":
			exammonth="Dec";
			break;
		}
		return exammonth;
	}
	public ServiceRequestStudentPortal saveScribeSR(ServiceRequestStudentPortal sr, MultipartFile resume,StudentStudentPortalBean student)  {
		String sapId= sr.getSapId();
		String filename="";
		String filepath="";
		String exammonth="";
		try {
		ServiceRequestDocumentBean document = new ServiceRequestDocumentBean();
		String examdetails=serviceRequestDao.getExamMonthByAcadMonth(ACAD_MONTH,ACAD_YEAR);
		int listofresitsubject=serviceRequestDao.getpassfailstatus(student.getSapid());
		if(listofresitsubject>0) {
			exammonth=returnmonthforResitExam();
		}else {
			exammonth=examdetails;
		}
		filename=uploadSRFile(document,resume, sapId + "_SCRIBE_RESUME");
		filepath=SERVICE_REQUEST_FILES_PATH+filename;
		filename=ScribeResume+filename;
		HashMap<String, String> srUpload=awsHelper.uploadLocalFile(filepath, filename, srBucket, ScribeResume); 
		if(srUpload.get("status").equalsIgnoreCase("error")){
			logger.error("failed to upload Scribe Resume pdf to AWS " + " For Sapid : "+sapId);
			throw new RuntimeException("Error while creating Scribe for Term End Exam SR");
		}
		sr.setDescription(sr.getServiceRequestType() + " for student " + sapId +" for Exam "+exammonth+"-"+ACAD_YEAR);
		sr.setTranStatus(ServiceRequestStudentPortal.TRAN_STATUS_FREE);
		sr.setCategory("Exam");
		sr.setSapId(sapId);
		sr.setRequestStatus(ServiceRequestStudentPortal.REQUEST_STATUS_SUBMITTED);
		sr.setCreatedBy(sapId);
		sr.setLastModifiedBy(sapId);
		sr.setIssued("N");
		sr.setHasDocuments("Y");
		sr.setSrAttribute("");
		sr.setMonth(exammonth);
		sr.setYear(ACAD_YEAR);
		serviceRequestDao.insertServiceRequest(sr);
		if(sr.getId()!=null) {
		document.setFilePath(filename);
		document.setDocumentName("Student Scribe Resume for Scribe for Term End Exam SR");
		document.setServiceRequestId(sr.getId());
		serviceRequestDao.insertServiceRequestDocument(document);
		serviceRequestDao.updateDocumentStatus(sr);
		
		ServiceRequestStudentPortal bean = serviceRequestDao.getServiceRequestBySrId(sr.getId());
		serviceRequestDao.insertServiceRequestStatusHistory(bean, "Update");
		mailer.sendSREmail(sr, student);
		deleteFile(filepath);
		}
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error("failed to upload Resume pdf to AWS " + " For Sapid : "+sapId);
			throw new RuntimeException("Error while creating Scribe for Term End Exam SR");
		}
	return sr;
}
	
	public  ServiceRequestStudentPortal checkIfStudentIsApplicableForExitPrograms(StudentStudentPortalBean student) {
		ServiceRequestStudentPortal serReq = new ServiceRequestStudentPortal();
		boolean alreadyRaised = checkIfAlreadyRaised(student.getSapid(), "Exit Program");
		if(alreadyRaised) {
			serReq.setIsCertificate(false);
			serReq.setError("Already raised Service Request for Exit Program.");
			return serReq;
		}
		ExitSrApplicableInterface exitApplicable = exitSrApplicableFactory.getProductType(student.getConsumerProgramStructureId());
        serReq = exitApplicable.checkIfStudentApplicableForExitProgram(student);
		return serReq;
	}
	
	public StudentStudentPortalBean getNewMappedPrograms(String sapId) {
		StudentStudentPortalBean student = serviceRequestDao.getStudentInfo(sapId);		
    	
		HashMap<String,StudentStudentPortalBean>newProgramMapped =getNewProgramMapped();
    	
		ExitSrApplicableInterface exitApplicable = exitSrApplicableFactory.getProductType(student.getConsumerProgramStructureId());
    	ServiceRequestStudentPortal serReq = exitApplicable.checkIfStudentApplicableForExitProgram(student);
    	
		return newProgramMapped.get(serReq.getConsumerProgramStructureId());
	}
	
	/**
	 * check E-Bonafide by purpose from service_request table
	 * @param sapId
	 * @param srId
	 * @param srType
	 * @param purpose
	 * @return
	 */
	public boolean canRaiseEBonafideByPurpose(String sapId, String srType, String purpose) {
		switch (purpose) {
		case RECORD_PURPOSE:
			return isCountOfPurposeOneOrLess(sapId, srType, purpose);
			
		case OFFICIAL_PURPOSE:
			return isCountOfPurposeOneOrLess(sapId, srType, purpose);		
			
		case SCHOLARSHIP_PURPOSE:
			return isCountOfPurposeOneOrLess(sapId, srType, purpose);
			
		case LOAN_PURPOSE:
			return isCountOfPurposeOneOrLess(sapId, srType, purpose);
			
		case VISA_PURPOSE:
			return isCountOfPurposeOneOrLess(sapId, srType, purpose);
			
		default:
			return isCountOfPurposeFiveOrLess(sapId, srType, OTHER_PURPOSE);
		}
	}
	
	//check E-Bonafide purpose count is one or less  
	private boolean isCountOfPurposeOneOrLess(String sapId, String srType, String purpose) {
		Integer eBonafidePurposeCount = serviceRequestDao.getEBonafidePurposeCount(sapId, srType, purpose);
	
		if(eBonafidePurposeCount < 1)
			return true;
		else
			return false;
	}
	
	//check E-Bonafide purpose count is one or less  
	private boolean isCountOfPurposeFiveOrLess(String sapId, String srType, String purpose) {
		Integer eBonafidePurposeCount = serviceRequestDao.getEBonafidePurposeCount(sapId, srType, purpose);
		
		if(eBonafidePurposeCount < 5)
			return true;
		else
			return false;
	}
	
	//remove spaces \n and special characters from the String
	public String removeSpacesAndSymbols(String purpose) {
		String text = purpose.replaceAll("\n", "");
		text = text.replaceAll("[^a-zA-Z0-9-().,'\"]", " ");  
		
		return text;
	}
	
	 public ArrayList<String> getApprovedStudentList(String serviceRequestType,String requestStatus){
		 return serviceRequestDao.getApprovedStudentList(serviceRequestType, requestStatus);
	 }

	 public boolean checkAlreadyRaisedForExit(String sapid) {	
		boolean checkAlreadyRaised = false;
		String getProgramStatus = serviceRequestDao.getProgramStatusOfStudent(sapid);	
		if(!StringUtils.isBlank(getProgramStatus) && getProgramStatus.equals("Program Withdrawal")) {
			checkAlreadyRaised = true;
		}
		return checkAlreadyRaised;
	}

	public void saveEBonafideContent(Long srId, String sapId, String specifiedReason) {
		
		Map<String, String> eBonafidePdfContentMap = new HashMap<String, String>();
		List<ServiceRequestCustomPDFContentBean> eBonafidePdfContentList = new ArrayList<ServiceRequestCustomPDFContentBean>();
		try {
			StudentStudentPortalBean studentInfo = serviceRequestDao.getSingleStudentsData(sapId);
			boolean isExitStudent = serviceRequestDao.checkExitStudent(sapId);
			HashMap<String, ProgramsStudentPortalBean> programMap = serviceRequestDao.getProgramMap();
			
			eBonafidePdfContentMap = generateEBonafidePDFContent(srId, studentInfo, specifiedReason, programMap, isExitStudent, true);
			
			eBonafidePdfContentList = getEBonafidePDFContentList(eBonafidePdfContentMap, srId);
			
			serviceRequestDao.saveEBonafideContent(eBonafidePdfContentList, sapId);
			
		} catch (Exception e) {
			logger.info("failed to save E-Bonafide pdf content for sapId {}, due to ", sapId, e);
		} 
	}
	
	private List<ServiceRequestCustomPDFContentBean> getEBonafidePDFContentList(
			Map<String, String> eBonafidePdfContentMap, Long srId) {
		List<ServiceRequestCustomPDFContentBean> eBonafidePdfContentList = new ArrayList<ServiceRequestCustomPDFContentBean>();
		
		ServiceRequestCustomPDFContentBean tempBean1 = new ServiceRequestCustomPDFContentBean();
		tempBean1.setContentPosition(PARAGRAPH_ONE);
		tempBean1.setContent(eBonafidePdfContentMap.get(PARAGRAPH_ONE));
		tempBean1.setServiceRequestId(srId);
		eBonafidePdfContentList.add(tempBean1);
		
		ServiceRequestCustomPDFContentBean tempBean2 = new ServiceRequestCustomPDFContentBean();
		tempBean2.setContentPosition(PARAGRAPH_TWO);
		tempBean2.setContent(eBonafidePdfContentMap.get(PARAGRAPH_TWO));
		tempBean2.setServiceRequestId(srId);
		eBonafidePdfContentList.add(tempBean2);
		
		return eBonafidePdfContentList;
	}
	public Map<String, String> generateEBonafidePDFContent(
		Long srId, StudentStudentPortalBean studentInfo, String specifiedReason,HashMap<String, ProgramsStudentPortalBean> programMap, 
		boolean isExitStudent,boolean isLogoRequired) {
		Map<String, String> customPdfContentMap = new HashMap<String, String>();
		
		String key = studentInfo.getProgram() + "-" + studentInfo.getPrgmStructApplicable();
		ProgramsStudentPortalBean programDetails = programMap.get(key);
		String programname = programDetails.getProgramname();
		String programDurationUnit = programDetails.getProgramDurationUnit();
		String programDuration = programDetails.getProgramDuration();
		String programCode = programDetails.getProgramcode();
		
		String genderPronoun = new String();
		String gender = new String();
		if("Male".equalsIgnoreCase(studentInfo.getGender())) {
			genderPronoun = "his";
			gender = "He";
		}
		else {
			genderPronoun = "her";
			gender = "She";
		}

		String paragraphOne = "This is to ceritfy that " + studentInfo.getFirstName() + " " + studentInfo.getLastName() + " (Student No."
					    + studentInfo.getSapid() + ") was a bonafide student of our " + programDuration + " "
						+ programDurationUnit + " " + "in " + programname + " (" + programCode + ")"
						+ " program of NMIMS Global Access - School for Continuing Education. " + gender + " was enrolled for "
						+ studentInfo.getEnrollmentMonth() + " " + studentInfo.getEnrollmentYear() + " batch";

		if (studentInfo.getProgramStatus() != null && studentInfo.getProgramStatus().equals("Program Withdrawal")
				&& isExitStudent) {
			if (studentInfo.getProgram().equals("CBM")) {
				paragraphOne += ". Student opted for Exit program after completing the " + studentInfo.getSem() + " semester"
								+ " and got " + programDetails.getProgramname() + " issued";
			} else if (studentInfo.getProgram().equals("DBM")) {
				paragraphOne += ". Student opted for Exit program after completing the "
						+ programDetails.getProgramDuration() + " " + programDetails.getProgramDurationUnit() + ""
						+ " and got " + programDetails.getProgramname() + " issued";
			}
		}
		paragraphOne += " and " + genderPronoun + " program validity was till " + studentInfo.getValidityEndMonth() + " " + studentInfo.getValidityEndYear() + ". ";

		String paragraphTwo = "This letter is issued on " + genderPronoun + " " + specifiedReason + " request for further studies. \n";
		
		customPdfContentMap.put(PARAGRAPH_ONE, paragraphOne);
		customPdfContentMap.put(PARAGRAPH_TWO, paragraphTwo);
		
		return customPdfContentMap;
	}
	
	public Map<String, String> getEBonafidePDFContentMap(Long srId) {
		
		List<ServiceRequestCustomPDFContentBean> eBonafidePDFContent = serviceRequestDao.getEBonafidePDFContent(srId);
		
		Map<String, String> eBonafidePDFContentMap = new HashMap<String, String>();
		
		eBonafidePDFContent.stream()
						   .forEach(bean -> eBonafidePDFContentMap.put(bean.getContentPosition(), bean.getContent()));
		return eBonafidePDFContentMap;
	}
	
	public boolean eBonafidePDFContentExist(Long srId) {
		Integer count = serviceRequestDao.checkEBonafidePDFContent(srId);
		
		if(count > 0)
			return true;
		else
			return false;
	}
	
	public StudentSrDTO eBonafidePDFContent(String sapId, String reason, Long srId) {
		StudentSrDTO srDto = new StudentSrDTO();
	
		if(eBonafidePDFContentExist(srId)){
			 Map<String, String> eBonafidePDFContent = getEBonafidePDFContentMap(srId);
			 srDto.setCustomPDFContent(eBonafidePDFContent);
		}
		else {
			srDto = getEBonafidePDFDetails(sapId, reason, srId);
		}
		return srDto;
	}
	
	public void saveEBonafideContent(EBonafidePDFContentRequestBean bean, String userId) {
		List<ServiceRequestCustomPDFContentBean> customPDFContent = bean.getCustomPDFContent();
		customPDFContent.stream()
		.forEach(bean1 -> bean1.setContent(removeSpacesAndSymbols(bean1.getContent())));
	
		serviceRequestDao.saveEBonafideContent(bean.getCustomPDFContent(), userId);
	}
	
	private String getEBonafidePurposeCategory(String specifiedReason) {
		String purpose = new String();
		
		if(RECORD_PURPOSE.equals(specifiedReason) || OFFICIAL_PURPOSE.equals(specifiedReason) || 
				SCHOLARSHIP_PURPOSE.equals(specifiedReason) || LOAN_PURPOSE.equals(specifiedReason) || VISA_PURPOSE.equals(specifiedReason))
			purpose = specifiedReason.trim().replace(" ","_");
		else
			purpose = "Others";
		
		return purpose;
	}
	
	public void removeNonApplicableSubject(List<StudentMarksBean> studentMarksList, List<String> applicableSubjectList) {
		
		studentMarksList.removeIf(bean -> !applicableSubjectList.contains(bean.getSubject()) && 
				BBA_ELECTIVES_SEMESTER.contains(bean.getSem()));
	}
	
	//Get remark grade subjects of student
	public List<StudentMarksBean> getAStudentsMostRecentRGAssignmentMarks(String sapid) {
		List<StudentMarksBean> studentRGMarksList = new ArrayList<StudentMarksBean>();
		studentRGMarksList = serviceRequestDao.getAStudentsMostRecentRGAssignmentMarks(sapid);
		return studentRGMarksList;	
	}
	 
	public boolean checkIfAlreadyRaised(String sapid, String serviceRequestType) {	
		boolean checkAlreadyRaised = false;
		int ServiceRequestCount = serviceRequestDao.getAlreadyRaisedForExitProgram(sapid, serviceRequestType);	
		if(ServiceRequestCount > 0) {
			checkAlreadyRaised = true;
		}
		return checkAlreadyRaised;
	}
	
//	/**
//	 * Mass update Service Request records with provided status and cancellation reason for the given Service Request IDs.
//	 * The provided fields are validated and each Service Request record is updated accordingly.
//	 * A response Map is returned which contains the amount of Service Request IDs updated and error message for invalid/failed Service Request IDs.
//	 * @param serviceRequestIdList - List of Service Request IDs
//	 * @param requestStatus - Service Request status to be applied
//	 * @param cancellationReason - cancellation reason (applicable for Cancelled Service Request records)
//	 * @param userId - id of the Admin user
//	 * @return Map containing the response message
//	 */
//	public Map<String, String> massUpdateSR(List<String> serviceRequestIdList, String requestStatus, String cancellationReason, String userId) {
//		int updatedRecordsCount = 0;											//Counter which increments on every Service Request record updated
//		StringBuilder errorMessage = new StringBuilder();
//		
//		//Fields of Mass Update Service Request validated
//		massUpdateSRFieldChecks(serviceRequestIdList, requestStatus, cancellationReason, userId);
//		logger.info("Mass update Service Request status: {} with cancellationReason: {}, initiated by user: {}, for {} Service Request IDs: {}",
//				requestStatus, cancellationReason, userId, serviceRequestIdList.size(), serviceRequestIdList);
//	
//		//Records of Service Request IDs fetched from database and stored in a Map of key (serviceRequestId) - value (corresponding Service Request record data) pairs
//		List<ServiceRequestStudentPortal> serviceRequestList = serviceRequestRepository.serviceRequestTypeSapidList(serviceRequestIdList);
//		System.out.println("serviceRequestList : "+ serviceRequestList);
//		Map<String, SrAdminUpdateDto> serviceRequestMap = serviceRequestList.stream()
//																			.map((serviceRequestBean) -> createSrAdminUpdateDto(serviceRequestBean.getId(), serviceRequestBean.getServiceRequestType(), 
//																																serviceRequestBean.getSapId(), requestStatus, cancellationReason))
//																			.collect(Collectors.toMap((serviceRequest) -> String.valueOf(serviceRequest.getId()), Function.identity()));
//		Set<String> validServiceRequestIdSet = serviceRequestMap.keySet();
//		logger.info("Service Request records fetched for IDs: {}", validServiceRequestIdSet);
//		
//		//Service Request IDs which are not present in the Set retrieved from database are stored as invalid Service Request IDs
//		Set<String> invalidServiceRequestIds = dataDifferenceSet(serviceRequestIdList, validServiceRequestIdSet);
//		logger.info("Invalid Service Request IDs provided by user {}: {}", userId, invalidServiceRequestIds);
//		if(!invalidServiceRequestIds.isEmpty())
//			errorMessage.append("Invalid Service Request IDs: " + invalidServiceRequestIds.toString() + "\n");
//		List<String> errorSrId = new ArrayList<String>();
//		//each Service Request ID is iterated and updateServiceRequestStatusAndReason() method is called
//		for(String serviceRequestId: validServiceRequestIdSet) {
//			try {
//				updateServiceRequestStatusAndReason(serviceRequestMap.get(serviceRequestId), userId);
//				updatedRecordsCount++;
//			}
//	        catch(Exception ex) {
//	        	ex.printStackTrace();
//				logger.error("Error while trying to update status: {} for Service Request ID: {}, Exception thrown: ", requestStatus, serviceRequestId, ex);
//				errorSrId.add(serviceRequestId);
//			}
//		}
//		if(!errorSrId.isEmpty())
//			errorMessage.append("Error updating Service Request ID: ").append(errorSrId).append("\n");
//		
//		//A response Map is created with required data
//		Map<String, String> responseMap = new HashMap<>();
//		responseMap.put("status", (invalidServiceRequestIds.isEmpty() && updatedRecordsCount == validServiceRequestIdSet.size()) ? "success" : "error");
//		responseMap.put("successCount", String.valueOf(updatedRecordsCount));			
//		responseMap.put("errorMessage", errorMessage.toString());
//		return responseMap;
//	}
	
//	/**
//	 * A SrAdminUpdateDto DTO is created with the provided fields.
//	 * @param serviceRequestId - ID of the Service Request
//	 * @param serviceRequestType - type of the Service Request
//	 * @param sapId - sapid of the student who raised the Service Request
//	 * @param requestStatus - status of the Service Request
//	 * @param cancellationReason - reason for the Service Request cancellation (applicable for Cancelled Service Request records)
//	 * @return DTO containing the provided fields
//	 */
//	private static SrAdminUpdateDto createSrAdminUpdateDto(Long serviceRequestId, String serviceRequestType, String sapId, String requestStatus, String cancellationReason) {
//		SrAdminUpdateDto srAdminUpdateDto = new SrAdminUpdateDto();
//		srAdminUpdateDto.setId(serviceRequestId);
//		srAdminUpdateDto.setServiceRequestType(serviceRequestType);
//		srAdminUpdateDto.setSapid(sapId);
//		srAdminUpdateDto.setRequestStatus(requestStatus);
//		srAdminUpdateDto.setCancellationReason(cancellationReason);
//		
//		return srAdminUpdateDto;
//	}
	
//	/**
//	 * List Data which is not present in Set is returned as a Set.
//	 * @param dataList - List containing the data which is to be evaluated
//	 * @param dataSet - Set containing data which is used as a Comparator
//	 * @return Set containing the non-matching data
//	 */
//	private static Set<String> dataDifferenceSet(List<String> dataList, Set<String> dataSet) {
//		return dataList.stream()
//						.filter(data -> !dataSet.contains(data))
//						.collect(Collectors.toSet());
//	}
//	
//	/**
//	 * The fields required for Mass Update Service Request is validated.
//	 * @param serviceRequestIdList - List containing Service Request IDs
//	 * @param requestStatus - Service Request status
//	 * @param cancellationReason - reason for Service Request cancellation
//	 * @param userId - id of the Admin user
//	 */
//	private void massUpdateSRFieldChecks(List<String> serviceRequestIdList, String requestStatus, String cancellationReason, String userId) {
//		if(CollectionUtils.isEmpty(serviceRequestIdList))
//			throw new IllegalArgumentException("No Service Request IDs provided!");
//		
//		if(!serviceRequestStatusList.contains(requestStatus))
//			throw new IllegalArgumentException("Invalid Status selected.");
//		
//		if("Cancelled".equals(requestStatus) && StringUtils.isBlank(cancellationReason))
//			throw new IllegalArgumentException("Cancellation Reason not provided.");
//		
//		if(StringUtils.isBlank(userId))
//			throw new IllegalArgumentException("Unable to detect User ID. Please try again!");
//	}
}
