package com.nmims.controllers;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SearchBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.HttpDownloadUtilityHelper;
import com.nmims.helpers.TextTrackImplLine;
import com.nmims.helpers.VimeoManager;
import com.nmims.helpers.WebVttParser;
import com.nmims.listeners.SessionRecordingScheduler;
import com.nmims.services.ContentService;
import com.nmims.services.StudentService;
import com.nmims.services.VideoContentService;

@Controller 
@RequestMapping("/admin")
public class VideoContentController extends BaseController {
	@Autowired(required = false)
	ApplicationContext act;
	private final int pageSize = 10;
	private ArrayList<String> subjectList = null;
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;
	Map<String, List<String>> checkForMasterkeyMap = null;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST;
	
	@Autowired
	private SessionQueryAnswerDAO sessionQueryAnswerDAO;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Autowired
	private VideoContentService videoContentService;
	
	@Autowired
	private ContentService contentService;
	
	@Autowired
	private StudentService studentService;
	
	@Autowired
	private SessionRecordingScheduler sessionRecordingScheduler;
	
	private List<SearchBean> acadCycleList = null;
			
	public static final String default_format = "yyyy-MM-dd";
	
	private static final Logger logger = LoggerFactory.getLogger(VideoContentController.class);
	
	private Map<String, String> subjectCodeMapWithId = new HashMap<>();
	
	public Map<String, String> getsubjectCodeMapWithId() {		
		List<SessionDayTimeAcadsBean> subjectCodeList = new ArrayList<>();
		if (this.subjectCodeMapWithId.isEmpty()) {			
			TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
			subjectCodeList = dao.getsubjectCodeMapWithId();
			
			subjectCodeList.stream().map((element) -> {				
				this.subjectCodeMapWithId.put(element.getId(), element.getSubjectCode() + " ( " + element.getSubject() + " )");					
				return element;				
			}).collect(Collectors.toList());
		}
		return this.subjectCodeMapWithId;
	}
	
	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			this.subjectList = dao.getActiveSubjects();
		}

		return subjectList;
	}
	
	@ModelAttribute("acadCycleList")
	public List<SearchBean> acadCycleList() {
		if (this.acadCycleList == null)
			this.acadCycleList = videoContentService.fetchAcademicCycle();

		return acadCycleList;
	}

	@RequestMapping(value = "/uploadVideoContentForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView uploadVideoContentForm(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		modelAndView.addObject("fileBean", new FileAcadsBean());

		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> VideoContentsList = dao.getAllVideoContentList();
	//	modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(dao,VideoContentsList));
		modelAndView.addObject("VideoContentsList", VideoContentsList);
		modelAndView.addObject("videoContent", new VideoContentAcadsBean());
		modelAndView.addObject("action", "Add VideoContent");

		return modelAndView;
	}
	
	@RequestMapping(value = "/uploadVideoContentForLeadsForm", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView uploadVideoContentForLeadsForm(HttpServletRequest request, HttpServletResponse response) {

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		modelAndView.addObject("fileBean", new FileAcadsBean());

		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> VideoContentsList = dao.getAllVideoContentListForLeads();
		modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(dao,VideoContentsList));
		modelAndView.addObject("videoContent", new VideoContentAcadsBean());
		modelAndView.addObject("action", "Add VideoContent");

		return modelAndView;
	}
	
	@RequestMapping(value = "/uploadSessionWiseVideoContentForm", method = RequestMethod.GET)
	public ModelAndView uploadSessionWiseVideoContentForm(@RequestParam("id") Integer sessionId,
														  HttpServletRequest request,
														  HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}

		TimeTableDAO sessionDao = (TimeTableDAO)act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session = sessionDao.findScheduledSessionById(sessionId.toString());
		
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		
		modelAndView.addObject("fileBean", new FileAcadsBean());
		modelAndView.addObject("sessionId", sessionId);
		modelAndView.addObject("sessionDetails", "Add Video Contents for "+ session.getSessionName()+" of "+session.getSubject() );

		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> VideoContentsList = dao.getAllVideoContentList();
		modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(dao,VideoContentsList));
		modelAndView.addObject("videoContent", new VideoContentAcadsBean());
		modelAndView.addObject("action", "Add VideoContent");

		return modelAndView;
	}
/*
 * Conmmented and kept as will require in future by PS 28May
 * 	@RequestMapping(value = "/uploadVideoContentFiles", method = { RequestMethod.POST })
	public ModelAndView uploadVideoContentFiles(HttpServletRequest request,	
												HttpServletResponse response,
												@ModelAttribute FileBean fileBean) {

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		modelAndView.addObject("fileBean", fileBean);

		String userId = (String) request.getSession().getAttribute("userId_acads");
		VideoContentDAO vcdao = (VideoContentDAO) act.getBean("videoContentDAO");
		
		MultipartFile file = fileBean.getFileData();
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");

			List<VideoContentBean> VideoContentsList = vcdao.getAllVideoContentList();
			modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(vcdao,VideoContentsList));
			modelAndView.addObject("videoContent", new VideoContentBean());
			modelAndView.addObject("action", "Add VideoContent");

			return modelAndView;
		}
		 TimeTableDAO tdao = (TimeTableDAO) act.getBean("timeTableDAO");
		 SessionDayTimeBean session=null;

		
		try {
			
			
			 
			 Integer sessionId = fileBean.getFileId();
			 
			 session = tdao.findScheduledSessionById(sessionId .toString());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
			session=null;
			
			 
		}

		try {
			ExcelHelper excelHelper = new ExcelHelper();

			ArrayList<List> resultList = excelHelper.readVideoContentExcel(fileBean, subjectList, userId, session);
			ArrayList<VideoContentBean> videoContentList = (ArrayList<VideoContentBean>) resultList.get(0);
			ArrayList<VideoContentBean> errorBeanList = (ArrayList<VideoContentBean>) resultList.get(1);

			
			if (errorBeanList.size() > 0) {
				
				request.setAttribute("error", "true");
				String errorMessage="Error while uploading data caused due to bad data of rows with topic name : ";
				for(VideoContentBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n"+errorBean.getFileName()+" "+errorBean.getErrorMessage()+"<br>";
					
				}
				
				request.setAttribute("errorMessage", errorMessage);
				request.setAttribute("errorBeanList", errorBeanList);

				modelAndView.addObject("fileBean", fileBean);
				List<VideoContentBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(vcdao,VideoContentsList));
				modelAndView.addObject("videoContent", new VideoContentBean());
				modelAndView.addObject("action", "Add VideoContent");

				return modelAndView;
			}

			//ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			List<List> erroListAndIdListOfErrorContent = batchUpdateVideoContent(videoContentList);
			List<String> errorList = (List<String>)erroListAndIdListOfErrorContent.get(0);
			List<Long> idsOfAddedContent = (List<Long>) erroListAndIdListOfErrorContent.get(1);
			
			if (errorList.size() == 0) {
				

				List<Long> mappingCreatedIds= new ArrayList<>();
				for(VideoContentBean v : videoContentList) {
					boolean createMappings = createMappingsByVideoContentIdAndSessionId(vcdao, v.getId(), v.getSessionId());
					if(createMappings) {
						mappingCreatedIds.add(v.getId());
					}else {
						String deleteMappingRows = vcdao.deleteVideoContentIdAndMasterKeyMappingByListOfIds(mappingCreatedIds);
						
						String deleteRows = vcdao.deleteVideoContentByListOfIds(idsOfAddedContent);
						
						throw new Exception("Error in creating master key mappings.");
					}
				}
				
				
				
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", videoContentList.size() + " rows out of "
						+ videoContentList.size() + " inserted successfully.");

				List<VideoContentBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(vcdao,VideoContentsList));
				modelAndView.addObject("videoContent", new VideoContentBean());
				modelAndView.addObject("action", "Add VideoContent");

			} else {
				String deleteRows = vcdao.deleteVideoContentByListOfIds(idsOfAddedContent);
				
				
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList);

				List<VideoContentBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(vcdao,VideoContentsList));
				modelAndView.addObject("videoContent", new VideoContentBean());
				modelAndView.addObject("action", "Add VideoContent");

			}
		} catch (Exception e) {
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");

			List<VideoContentBean> VideoContentsList = vcdao.getAllVideoContentList();
			modelAndView.addObject("VideoContentsList", VideoContentsList);
			modelAndView.addObject("videoContent", new VideoContentBean());
			modelAndView.addObject("action", "Add VideoContent");

		}
		return modelAndView;
	}*/
	
	//uploadContentFiles Old start

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/uploadVideoContentFiles", method = { RequestMethod.POST })
	public ModelAndView uploadVideoContentFiles(HttpServletRequest request,	
												HttpServletResponse response,
												@ModelAttribute FileAcadsBean fileBean) {

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		modelAndView.addObject("fileBean", fileBean);

		String userId = (String) request.getSession().getAttribute("userId_acads");
		VideoContentDAO vcdao = (VideoContentDAO) act.getBean("videoContentDAO");

		MultipartFile file = fileBean.getFileData();
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");

			List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
			modelAndView.addObject("VideoContentsList", VideoContentsList);
			modelAndView.addObject("videoContent", new VideoContentAcadsBean());
			modelAndView.addObject("action", "Add VideoContent");

			return modelAndView;
		}
		 TimeTableDAO tdao = (TimeTableDAO) act.getBean("timeTableDAO");
		 SessionDayTimeAcadsBean session=null;


		try {

			
			 
			 Integer sessionId = fileBean.getFileId();
			 session = tdao.findScheduledSessionById(sessionId .toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
			session=null;
			 
		}

		try {
			ExcelHelper excelHelper = new ExcelHelper();

			ArrayList<List> resultList = excelHelper.readVideoContentExcel(fileBean, subjectList, userId, session);
			ArrayList<VideoContentAcadsBean> videoContentList = (ArrayList<VideoContentAcadsBean>) resultList.get(0);
			ArrayList<VideoContentAcadsBean> errorBeanList = (ArrayList<VideoContentAcadsBean>) resultList.get(1);

			if (errorBeanList.size() > 0) {
				
				request.setAttribute("error", "true");
				String errorMessage="Error while uploading data caused due to bad data of rows with topic name : ";
				for(VideoContentAcadsBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n"+errorBean.getFileName()+" "+errorBean.getErrorMessage()+"<br>";
				}
				
				request.setAttribute("errorMessage", errorMessage);
				request.setAttribute("errorBeanList", errorBeanList);

				modelAndView.addObject("fileBean", fileBean);
				List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", VideoContentsList);
				modelAndView.addObject("videoContent", new VideoContentAcadsBean());
				modelAndView.addObject("action", "Add VideoContent");

				return modelAndView;
			}

			ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			ArrayList<String> errorList = dao.batchUpdateVideoContent(videoContentList);

			if (errorList.size() == 0) {
				

				request.setAttribute("success", "true");
				request.setAttribute("successMessage", videoContentList.size() + " rows out of "
						+ videoContentList.size() + " inserted successfully.");

				List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", VideoContentsList);
				modelAndView.addObject("videoContent", new VideoContentAcadsBean());
				modelAndView.addObject("action", "Add VideoContent");

			} else {

				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList);

				List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", VideoContentsList);
				modelAndView.addObject("videoContent", new VideoContentAcadsBean());
				modelAndView.addObject("action", "Add VideoContent");

			}
		} catch (Exception e) {
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");

			List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
			modelAndView.addObject("VideoContentsList", VideoContentsList);
			modelAndView.addObject("videoContent", new VideoContentAcadsBean());
			modelAndView.addObject("action", "Add VideoContent");

		}
		return modelAndView;
	}
	
	//uploadContentFiles Old end
	
	
	//Sessions Batch upload Start
/*
 * commented and kept as will require in future by PS 28May
 * 	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/uploadVideoContentFilesBatch", method = { RequestMethod.POST })
	public ModelAndView uploadVideoContentFilesBatch(HttpServletRequest request,	
												HttpServletResponse response,
												@ModelAttribute FileBean fileBean) {

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		modelAndView.addObject("fileBean", fileBean);

		String userId = (String) request.getSession().getAttribute("userId_acads");
		VideoContentDAO vcdao = (VideoContentDAO) act.getBean("videoContentDAO");
		
		MultipartFile file = fileBean.getFileData();
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");

			List<VideoContentBean> VideoContentsList = vcdao.getAllVideoContentList();
			modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(vcdao,VideoContentsList));
			modelAndView.addObject("videoContent", new VideoContentBean());
			modelAndView.addObject("action", "Add VideoContent");

			return modelAndView;
		}
		 TimeTableDAO tdao = (TimeTableDAO) act.getBean("timeTableDAO");
		 ArrayList<String> sessionIdList= new ArrayList<String>(); 
		try {
			
			
			 
			 sessionIdList = tdao.getAllSessions();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
			
			 
		}

		try {
			ExcelHelper excelHelper = new ExcelHelper();
			HashMap<String,SessionDayTimeBean> sessionsMap = vcdao.getAllSessionsDetails();
			ArrayList<List> resultList = excelHelper.readVideoContentBatchExcel(fileBean, subjectList, userId, sessionIdList,sessionsMap);
			ArrayList<VideoContentBean> videoContentList = (ArrayList<VideoContentBean>) resultList.get(0);
			ArrayList<VideoContentBean> errorBeanList = (ArrayList<VideoContentBean>) resultList.get(1);

			
			if (errorBeanList.size() > 0) {
				
				request.setAttribute("error", "true");
				String errorMessage="Error while uploading data caused due to bad data of rows with topic name : ";
				for(VideoContentBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n"+errorBean.getFileName()+" "+errorBean.getErrorMessage()+"<br>";	
					
				}
				
				request.setAttribute("errorMessage", errorMessage);
				request.setAttribute("errorBeanList", errorBeanList);

				modelAndView.addObject("fileBean", fileBean);
				List<VideoContentBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(vcdao,VideoContentsList));
				modelAndView.addObject("videoContent", new VideoContentBean());
				modelAndView.addObject("action", "Add VideoContent");

				return modelAndView;
			}

			//ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			List<List> erroListAndIdListOfErrorContent = batchUpdateVideoContent(videoContentList);
			List<String> errorList = (List<String>)erroListAndIdListOfErrorContent.get(0);
			List<Long> idsOfAddedContent = (List<Long>) erroListAndIdListOfErrorContent.get(1);
			
			if (errorList.size() == 0) {
				
				List<Long> mappingCreatedIds= new ArrayList<>();
				for(VideoContentBean v : videoContentList) {
					boolean createMappings = createMappingsByVideoContentIdAndSessionId(vcdao, v.getId(), v.getSessionId());
					if(createMappings) {
						mappingCreatedIds.add(v.getId());
					}else {
						String deleteMappingRows = vcdao.deleteVideoContentIdAndMasterKeyMappingByListOfIds(mappingCreatedIds);
						
						String deleteRows = vcdao.deleteVideoContentByListOfIds(idsOfAddedContent);
						
						throw new Exception("Error in creating master key mappings.");
					}
				}
				
				
				
				
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", videoContentList.size() + " rows out of "
						+ videoContentList.size() + " inserted successfully.");

				List<VideoContentBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(vcdao,VideoContentsList));
				modelAndView.addObject("videoContent", new VideoContentBean());
				modelAndView.addObject("action", "Add VideoContent");

			} else {
				
				String deleteRows = vcdao.deleteVideoContentByListOfIds(idsOfAddedContent);
				
				
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList+". <b>Kindly reupload the whole file.</b> ");

				List<VideoContentBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(vcdao,VideoContentsList));
				modelAndView.addObject("videoContent", new VideoContentBean());
				modelAndView.addObject("action", "Add VideoContent");

			}
		} catch (Exception e) {
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "<b>Kindly reupload the whole file.</b>  Error in inserting rows. Error : "+e.getMessage());

			List<VideoContentBean> VideoContentsList = vcdao.getAllVideoContentList();
			modelAndView.addObject("VideoContentsList", setCountOfProgramsApplicableToEachVideoContent(vcdao,VideoContentsList));
			modelAndView.addObject("videoContent", new VideoContentBean());
			modelAndView.addObject("action", "Add VideoContent");

		}
		return modelAndView;
	}*/

	//Sessions Batch upload Old Start
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/uploadVideoContentFilesBatch", method = { RequestMethod.POST })
	public ModelAndView uploadVideoContentFilesBatch(HttpServletRequest request,	
												HttpServletResponse response,
												@ModelAttribute FileAcadsBean fileBean) {

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		modelAndView.addObject("fileBean", fileBean);

		String userId = (String) request.getSession().getAttribute("userId_acads");
		VideoContentDAO vcdao = (VideoContentDAO) act.getBean("videoContentDAO");
		MultipartFile file = fileBean.getFileData();
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");

			List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
			modelAndView.addObject("VideoContentsList", VideoContentsList);
			modelAndView.addObject("videoContent", new VideoContentAcadsBean());
			modelAndView.addObject("action", "Add VideoContent");

			return modelAndView;
		}
		 TimeTableDAO tdao = (TimeTableDAO) act.getBean("timeTableDAO");
		 ArrayList<String> sessionIdList= new ArrayList<String>(); 
		try {
			
			 
			 sessionIdList = tdao.getAllSessions();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			  

			 
		}

		try {
			ExcelHelper excelHelper = new ExcelHelper();
			HashMap<String,SessionDayTimeAcadsBean> sessionsMap = vcdao.getAllSessionsDetails();
			ArrayList<List> resultList = excelHelper.readVideoContentBatchExcel(fileBean, subjectList, userId, sessionIdList,sessionsMap);
			ArrayList<VideoContentAcadsBean> videoContentList = (ArrayList<VideoContentAcadsBean>) resultList.get(0);
			ArrayList<VideoContentAcadsBean> errorBeanList = (ArrayList<VideoContentAcadsBean>) resultList.get(1);

			
			if (errorBeanList.size() > 0) {
				
				request.setAttribute("error", "true");
				String errorMessage="Error while uploading data caused due to bad data of rows with topic name : ";
				for(VideoContentAcadsBean errorBean : errorBeanList) {
					errorMessage = errorMessage +"\n"+errorBean.getFileName()+" "+errorBean.getErrorMessage()+"<br>";	

				}
				
				request.setAttribute("errorMessage", errorMessage);
				request.setAttribute("errorBeanList", errorBeanList);

				modelAndView.addObject("fileBean", fileBean);
				List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", VideoContentsList);
				modelAndView.addObject("videoContent", new VideoContentAcadsBean());
				modelAndView.addObject("action", "Add VideoContent");

				return modelAndView;
			}

			ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			ArrayList<String> errorList = dao.batchUpdateVideoContent(videoContentList);

			if (errorList.size() == 0) {
			// below method call fix vimeo duration null value via excel upload 	
				videoContentService.updateVimeoDurationService(CURRENT_ACAD_YEAR,CURRENT_ACAD_MONTH);

				request.setAttribute("success", "true");
				request.setAttribute("successMessage", videoContentList.size() + " rows out of "
						+ videoContentList.size() + " inserted successfully.");

				List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", VideoContentsList);
				modelAndView.addObject("videoContent", new VideoContentAcadsBean());
				modelAndView.addObject("action", "Add VideoContent");

			} else {
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList);

				List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", VideoContentsList);
				modelAndView.addObject("videoContent", new VideoContentAcadsBean());
				modelAndView.addObject("action", "Add VideoContent");

			}
		} catch (Exception e) {
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");

			List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
			modelAndView.addObject("VideoContentsList", VideoContentsList);
			modelAndView.addObject("videoContent", new VideoContentAcadsBean());
			modelAndView.addObject("action", "Add VideoContent");

		}
		return modelAndView;
	}
	
	//Sessions Batch upload Old method End
	private List<List> batchUpdateVideoContent(ArrayList<VideoContentAcadsBean> videoContentList) {

		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		int i = 0;
		List<List> erroListAndIdListOfErrorContent = new ArrayList<>();
		List<String> errorList = new ArrayList<>();
		List<Long> idsOfAddedContent = new ArrayList<>();
		if(videoContentList != null) {
		}
		for (i = 0; i < videoContentList.size(); i++) {
			try{
				VideoContentAcadsBean bean = videoContentList.get(i);
				long key = dao.saveVideoContent(bean);
				bean.setId(key);
				if(key == 0) {
					errorList.add(i+"");
				}else {
					idsOfAddedContent.add(key);
				}

			}catch(Exception e){
				  
				errorList.add(i+"");
			}
		}
		erroListAndIdListOfErrorContent.add(errorList);
		erroListAndIdListOfErrorContent.add(idsOfAddedContent);
		return erroListAndIdListOfErrorContent;

	}

	//Sessions Batch upload End
	
	
	
	
	
	//Code to upload video topic data from Excel Start
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/uploadVideoTopicFiles", method = { RequestMethod.POST })
	public ModelAndView uploadVideoTopicFiles(HttpServletRequest request, HttpServletResponse response,
											  @ModelAttribute FileAcadsBean fileBean) {

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelAndView = new ModelAndView("editVideoContentDetails");
		modelAndView.addObject("fileBean", fileBean);

		String userId = (String) request.getSession().getAttribute("userId_acads");
		VideoContentDAO vcdao = (VideoContentDAO) act.getBean("videoContentDAO");
		VideoContentAcadsBean mainVideo = vcdao.getVideoContentById((int)(long)fileBean.getId()); 
	
		MultipartFile file = fileBean.getFileData();
		if (file.isEmpty()) {// Check if File was attached
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Please Select File to Upload...");

	
			
			List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
			modelAndView.addObject("VideoContentsList", VideoContentsList);
			modelAndView.addObject("videoContent", mainVideo);
			modelAndView.addObject("action", "Add VideoContent");
			modelAndView.addObject("fileBean", new FileAcadsBean());
			modelAndView.addObject("videoSubTopic", new VideoContentAcadsBean());

			return modelAndView;
		}

		try {
			ExcelHelper excelHelper = new ExcelHelper();

			ArrayList<List> resultList = excelHelper.readVideoTopicsExcel(fileBean, subjectList, userId, mainVideo);
			ArrayList<VideoContentAcadsBean> videoContentList = (ArrayList<VideoContentAcadsBean>) resultList.get(0);
			ArrayList<VideoContentAcadsBean> errorBeanList = (ArrayList<VideoContentAcadsBean>) resultList.get(1);

			if (errorBeanList.size() > 0) {
				request.setAttribute("errorBeanList", errorBeanList);

				List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", VideoContentsList);
				modelAndView.addObject("videoContent", mainVideo);
				modelAndView.addObject("action", "Add VideoContent");
				modelAndView.addObject("fileBean", new FileAcadsBean());
				modelAndView.addObject("videoSubTopic", new VideoContentAcadsBean());

				return modelAndView;
			}

			ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			ArrayList<String> errorList = dao.batchUpdateVideoTopic(videoContentList);

			if (errorList.size() == 0) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", videoContentList.size() + " rows out of "
						+ videoContentList.size() + " inserted successfully.");

				List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", VideoContentsList);
				modelAndView.addObject("videoContent", mainVideo);
				modelAndView.addObject("videoSubTopic", new VideoContentAcadsBean());
				modelAndView.addObject("action", "Edit VideoContent");
				modelAndView.addObject("fileBean", new FileAcadsBean());

			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size()
						+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
						+ errorList);


				modelAndView.addObject("videoSubTopic", new VideoContentAcadsBean());
				
				List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
				modelAndView.addObject("VideoContentsList", VideoContentsList);
				modelAndView.addObject("videoContent", mainVideo);
				modelAndView.addObject("action", "Edit VideoContent");
				modelAndView.addObject("fileBean", new FileAcadsBean());
				modelAndView.addObject("videoSubTopic", new VideoContentAcadsBean());

			}
		} catch (Exception e) {
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");

			modelAndView.addObject("videoSubTopic", new VideoContentAcadsBean());
			List<VideoContentAcadsBean> VideoContentsList = vcdao.getAllVideoContentList();
			modelAndView.addObject("VideoContentsList", VideoContentsList);
			modelAndView.addObject("videoContent", mainVideo);
			modelAndView.addObject("action", "Add VideoContent");
			modelAndView.addObject("fileBean", new FileAcadsBean());
			modelAndView.addObject("videoSubTopic", new VideoContentAcadsBean());

		}
		List<VideoContentAcadsBean> videoSubTopicsList = vcdao.getAllVideoSubTopicsList(mainVideo.getId());
		modelAndView.addObject("videoSubTopicsList", videoSubTopicsList);
		request.setAttribute("videoSubTopicsList", videoSubTopicsList);
	
		return modelAndView;
	}
	//Code to upload video topic data from Excel End
	
	@RequestMapping(value = "/videosHome", method = RequestMethod.GET) 
	public ModelAndView videosHomeAdmin(HttpServletRequest request,
										 HttpServletResponse response,
										 @RequestParam("academicCycle") String academicCycle
										 ) {
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		PersonAcads person = (PersonAcads) request.getSession().getAttribute("user_acads");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		String roles = person.getRoles();
		
		ArrayList<String> allsubjects = new ArrayList<String>();
		if(!roles.contains("Faculty")) {
		allsubjects = subjectList;
		allsubjects.add("Orientation");
		allsubjects.add("Assignment");
		allsubjects.add("Project Preparation Session");
		allsubjects.add("Guest Session: GST by CA. Bimal Jain");
		}else {
			allsubjects=dao.getFacultySubjectListForSessions(userId);
		}
		ModelAndView modelAndView = new ModelAndView("videoHomeAdmin");
		int pageNo;
		try {
			pageNo = Integer.parseInt(request.getParameter("pageNo"));
			if (pageNo < 1) {
				pageNo = 1;
			}
		} catch (NumberFormatException e) {
			pageNo = 1;
			  
		}
		

		
		ArrayList<String> academicCycleList =new ArrayList<String>();
		academicCycleList=dao.getAcademicCycleList();
		ArrayList<String> academicCycleListForDb =new ArrayList<String>();
		
		if(academicCycle==null || "".equals(academicCycle) || "All".equals(academicCycle)){
			academicCycle="All";
			academicCycleListForDb.addAll(academicCycleList);
		} else{
			academicCycleListForDb.add(academicCycle);
		}
		PageAcads<VideoContentAcadsBean> page = new PageAcads<VideoContentAcadsBean>();
		if(!roles.contains("Faculty")) {
			page = dao.getVideoContentPage(pageNo, pageSize,allsubjects,academicCycleListForDb,null);
		}else {
			page = dao.getVideoContentPageForFaculty(pageNo, pageSize, allsubjects, academicCycleListForDb, null, userId);
		}
		List<VideoContentAcadsBean> videoContentListPage = page.getPageItems();
		
		modelAndView.addObject("academicCycleList", academicCycleList);
		request.getSession().setAttribute("academicCycleList", academicCycleList);
		
		modelAndView.addObject("academicCycle", academicCycle);
		modelAndView.addObject("page", page);
		modelAndView.addObject("rowCount", page.getRowCount());
		modelAndView.addObject("allsubjects", allsubjects);
		request.getSession().setAttribute("allsubjects", allsubjects);
		modelAndView.addObject("roles",roles);
		modelAndView.addObject("selectedSubject", "All");

		//for batch track filter
		List<String> tracks=dao.getBatchTracks();

		modelAndView.addObject("allBatchTracks", tracks);
		request.getSession().setAttribute("allBatchTracks", tracks);
		modelAndView.addObject("selectedBatch", "All");
		
		List<FacultyAcadsBean> facultyList = new ArrayList<FacultyAcadsBean>();
		if(!roles.contains("Faculty")) {
			facultyList = dao.getFacultiesForSubjects(allsubjects);
		}else {
			facultyList = dao.getFacultYByFacultyId(userId);
		}
		modelAndView.addObject("facultyList",facultyList);
		request.getSession().setAttribute("facultyList", facultyList);
		
		if(!roles.contains("Faculty")) {
		FacultyAcadsBean tempFaculty=new FacultyAcadsBean();
		tempFaculty.setFacultyId("All");
		modelAndView.addObject("selectedFaculty",tempFaculty);
		}

		
		modelAndView.addObject("searchItem","");
		
		modelAndView.addObject("showPagination", true);
		
		modelAndView.addObject("VideoContentsList", videoContentListPage);
		modelAndView.addObject("subjectVideosMap", null);
		request.setAttribute("VideoContentsList", videoContentListPage);
		request.setAttribute("subjectVideosMap", null);
		if(!roles.contains("Faculty")) {
		request.setAttribute("subjectList", getSubjectList());
		}else {
		request.setAttribute("subjectList", allsubjects);
		}
		
		return modelAndView;
	}
	
	public ArrayList<String> applicableSubjectsForStudent(HttpServletRequest request) {
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingAcadsBean> allsubjects = new ArrayList<>();
		
		ArrayList<ProgramSubjectMappingAcadsBean> unAttemptedSubjectsBeans = new ArrayList<>();
		
		String sapId = (String)request.getSession().getAttribute("userId_acads");
		//So admins/faculty would see Videos Page with all videos 
		if(!sapId.startsWith("7")) {
			request.getSession().setAttribute("applicableSubjects", subjectList);
			return subjectList;
		}
		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		StudentAcadsBean studentRegistrationData;
		
		String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
		// If isEarlyAccess then registration will not be available of this drive.
		if("Yes".equalsIgnoreCase(earlyAccess)) {
			studentRegistrationData= student;
		}else if(student.getProgram().equalsIgnoreCase("EPBM") || student.getProgram().equalsIgnoreCase("MPDV") ) {
			
			studentRegistrationData = cdao.getStudentMaxSemRegistrationData(sapId);
		}else{
			studentRegistrationData = cdao.getStudentRegistrationData(sapId);
		}

		if(studentRegistrationData == null){
			//Get fail subjects content if studnet does not have registration for current sem.
			failSubjectsBeans = getFailSubjects(student);

			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				allsubjects.addAll(failSubjectsBeans);
			}

			
		}else{
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			//student.setPrgmStructApplicable(studentRegistrationData.getPrgmStructApplicable());
			//student.setWaivedOffSubjects(studentRegistrationData.getWaivedOffSubjects());
			ArrayList<ProgramSubjectMappingAcadsBean> currentSemSubjects = getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
				request.getSession().setAttribute("currentSemSubjects", currentSemSubjects);
			}

			
			//If current sem is 1, then there will be no failed subjects. Get failed subjects only when he is in higher semesters
			if(!"1".equals(studentRegistrationData.getSem())){
				failSubjectsBeans = getFailSubjects(student);

				if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
					allsubjects.addAll(failSubjectsBeans);
				}
			}

		}
		
		//Get subjects never attempted or results not declared
		unAttemptedSubjectsBeans = cdao.getUnAttemptedSubjects(sapId);
		if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			allsubjects.addAll(unAttemptedSubjectsBeans);
		}


		//Sort all subjects semester wise.
		Collections.sort(allsubjects);
		
		if(allsubjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you."); 
		}
		ArrayList<String> applicableSubjects=new ArrayList<>();
		for(ProgramSubjectMappingAcadsBean psmb:allsubjects){

			if(!student.getWaivedOffSubjects().contains(psmb.getSubject())) {
				applicableSubjects.add(psmb.getSubject());
			}
		}
		applicableSubjects.add("Guest Session: GST by CA. Bimal Jain");
		
		//To add orientation subject only for Sem 1 student  
		VideoContentDAO vDao = (VideoContentDAO) act.getBean("videoContentDAO");
		StudentAcadsBean semCheck = vDao.getStudentsMostRecentRegistrationData(sapId);
		try{
		if("1".equals(semCheck.getSem())){
			applicableSubjects.add("Orientation");
		}
		//end
		

		applicableSubjects.add("Assignment");
		
		//Commented by Siddheshwar_K because 'Project' gets added in subjects list with PSS
		/*if("4".equals(semCheck.getSem())){
				applicableSubjects.add("Project Preparation Session");
		}*/
		}catch(Exception e){
		}
		
		//Remove orientation,Project n assignment subject for sas student
		try {

			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram()) ){
				applicableSubjects.remove("Assignment");
				
				//We are not adding 'Project Preparation Session' in applicableSubjects list so no need of remove
				/*if(applicableSubjects.contains("Project Preparation Session")) {
					applicableSubjects.remove("Project Preparation Session");
				}*/
				if(applicableSubjects.contains("Orientation")) {
					applicableSubjects.remove("Orientation");
				
					if("1".equals(semCheck.getSem())){
					applicableSubjects.add("Executive Program Orientation");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		//end
		
		if(student.getWaivedInSubjects() != null) {
			for (String subject : student.getWaivedInSubjects()) {
				if(!applicableSubjects.contains(subject)) {
					applicableSubjects.add(subject);
				}
			}
		}
		
		request.getSession().setAttribute("failSubjectsBeans", failSubjectsBeans);
		request.getSession().setAttribute("applicableSubjects", applicableSubjects);
		return applicableSubjects;
				
	}
	private ArrayList<ProgramSubjectMappingAcadsBean> getFailSubjects(StudentAcadsBean student) {
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectList;
		try {
			failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		} catch (Exception e) {
			failSubjectList=new ArrayList<ProgramSubjectMappingAcadsBean>();
			  
		}
		return failSubjectList;
	}
	private ArrayList<ProgramSubjectMappingAcadsBean> getSubjectsForStudent(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<ProgramSubjectMappingAcadsBean> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingAcadsBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				subjects.add(bean);

			}
		}
		return subjects;
	}
	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	} 


	@RequestMapping(value = "/addVideoContentForm", method = RequestMethod.GET)
	public ModelAndView addVideoContentForm(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> VideoContentsList = dao.getAllVideoContentList();
		modelAndView.addObject("VideoContentsList", VideoContentsList);
		modelAndView.addObject("videoContent", new VideoContentAcadsBean());
		modelAndView.addObject("action", "Add VideoContent");
		return modelAndView;
	}

	@RequestMapping(value = "/postVideoContents", method = RequestMethod.POST)
	public ModelAndView postVideoContents(@ModelAttribute VideoContentAcadsBean VideoContent, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		try {
			if (0 == VideoContent.getId()) {
				VideoContent.setCreatedBy(userId);
				Date date = new Date();
				SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
				String strDate = formatter.format(date);
				VideoContent.setAddedOn(strDate);

				long key = 0L;
				try {
					key = dao.saveVideoContent(VideoContent);
					setSuccess(request, "VideoContent has been saved successfully");
				}catch (Exception e) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error Occured while adding the Video Content.");
					  
				}

				boolean createMappings = createMappingsByVideoContentIdAndSessionId(dao,key,VideoContent.getSessionId());
				if(!createMappings) {
					setError(request,"Error in createMappings  ");
					int deletedRow = 0;
					try {
						deletedRow = dao.deleteVideoContent((int)key);
					} catch (Exception e) {
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error Occured while deleting the Video Content.");
						  
					}
					if (deletedRow == 0) {
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error Occured while deleting the Video Content.");
					} else {
						setSuccess(request, "VideoContent has been deleted successfully");
					}
				}
			} else {
				VideoContent.setLastModifiedBy(userId);
				TimeTableDAO sessionDao = (TimeTableDAO)act.getBean("timeTableDAO");
				
				try {
					SessionDayTimeAcadsBean session = sessionDao.findScheduledSessionById(VideoContent.getSessionId().toString());
					VideoContent.setSessionDate(session.getDate());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					  
					VideoContent.setSessionDate(" ");
				}
				boolean checkIfSessionIdIsChnaged = checkIfSessionIdIsChnaged(dao,VideoContent);
				
				boolean VideoContentUpdated = Boolean.FALSE;
				try {
					VideoContentUpdated = dao.updateVideoContent(VideoContent);
				}catch (Exception e) {
					  
				}
				
				if (VideoContentUpdated) {
					setSuccess(request, "VideoContent has been updated successfully");
					if(checkIfSessionIdIsChnaged) {
						
						String deleteMappingsErrorMessage = dao.deleteVideoContentAndMasterKeyMappingsById(VideoContent.getId());
						if(!StringUtils.isBlank(deleteMappingsErrorMessage)) {
							setError(request,"Error in deleting mappings,Error : "+deleteMappingsErrorMessage+". Please try again ");
						}
						boolean createMappings = createMappingsByVideoContentIdAndSessionId(dao,(long)VideoContent.getId(),VideoContent.getSessionId());
						if(!createMappings) {
							setError(request,"Error in createMappings, Please try again ");
						}
					}
				}else {
					setError(request,"Error in updateVideoContent  ");
					throw new Exception("Error in updateVideoContent");
				}
				
			}
		} catch (Exception e) {
			  
			setError(request,"Error : "+e.getMessage());
		}
		
		modelAndView.addObject("fileBean", new FileAcadsBean());

		List<VideoContentAcadsBean> VideoContentsList = dao.getAllVideoContentList();
		modelAndView.addObject("VideoContentsList", VideoContentsList);
		modelAndView.addObject("videoContent", new VideoContentAcadsBean());
		modelAndView.addObject("action", "Add VideoContent");
		modelAndView.addObject("fileBean", new FileAcadsBean());
		return modelAndView;
	}
	
	/*
	 * returns true if old session id doesnt match with updated session id 
	 * */
	 private boolean checkIfSessionIdIsChnaged(VideoContentDAO dao, VideoContentAcadsBean videoContent) {
		
		 VideoContentAcadsBean oldVideoContent = dao.getVideoContentById(videoContent.getId().intValue());
		 if(oldVideoContent.getSessionId().equals(videoContent.getSessionId())) {
			 return false;
		 }else {
			 return true;
		 }
		 
	}

	private boolean createMappingsByVideoContentIdAndSessionId(VideoContentDAO dao, long key, Integer sessionId) {
		
			TimeTableDAO tDao = (TimeTableDAO)act.getBean("timeTableDAO");
		 List<VideoContentAcadsBean> videoContentIdAndMasterKeyList = new ArrayList<>();
			//get  video content
		 	VideoContentAcadsBean video = dao.getVideoContentById((int)key);
			List<VideoContentAcadsBean> videoContentList = new ArrayList<>();
			videoContentList.add(video);
			
			//get  session details mapped with videocontentid
			SessionDayTimeAcadsBean session = tDao.findScheduledSessionById(sessionId.toString());
			List<SessionDayTimeAcadsBean> sessionsList = new ArrayList<>();
			sessionsList.add(session);
			
			//get map of session id n masterkey
			Map<String,List<String>> sessionidAndMasterkeyMap = getSessionidAndMasterkeyMap(dao,sessionsList); 
			
			//create list of videocontentid and masterkey 
			for(VideoContentAcadsBean v : videoContentList) {
				List<String> masterKeysForVideoContentId = new ArrayList<>();
				if(sessionidAndMasterkeyMap.containsKey(v.getSessionId().toString())) {
					masterKeysForVideoContentId = sessionidAndMasterkeyMap.get(v.getSessionId().toString());
				}
				for(String s : masterKeysForVideoContentId) {
					VideoContentAcadsBean tempMappingBean = new VideoContentAcadsBean();
					tempMappingBean.setId(v.getId());
					tempMappingBean.setConsumerProgramStructureId(s);
					videoContentIdAndMasterKeyList.add(tempMappingBean);
				}
			}
			
			//batch update the list
			String batchUpdateErrorMessage = dao.batchInsertVideoCententIdAndMasterKeyMappings(videoContentIdAndMasterKeyList);
			
			if(StringUtils.isBlank(batchUpdateErrorMessage)) {

				 return true;
			}else {

				 return false;
			}

	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/watchVideos", method = RequestMethod.GET)
	public ModelAndView watchVideo(@RequestParam("id") String idString, HttpServletRequest request, HttpServletResponse response) {
			if(!checkSession(request, response)){
				return new ModelAndView("studentPortalRediret");
			}

		 ModelAndView modelAndView = new ModelAndView();
		 
		 PersonAcads person = (PersonAcads) request.getSession().getAttribute("user_acads");
		 String roles = person.getRoles();
		 String userId = (String) request.getSession().getAttribute("userId_acads");
		 StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		 StudentAcadsBean studentRegData = (StudentAcadsBean)request.getSession().getAttribute("studentRegData");

		 HttpDownloadUtilityHelper hdHelper = new HttpDownloadUtilityHelper();
		 List <TextTrackImplLine> trackDataList = new ArrayList <TextTrackImplLine>();
		 List<VideoContentAcadsBean> videoSubTopicsList = new ArrayList<VideoContentAcadsBean>();
		 String transcriptContent = null;
			
		 /**
		  * This check written to prevent query feature for the lead students
		  */
		 
		 if(userId.startsWith("7")) {
				modelAndView.setViewName("videoPage");
			}else {
				modelAndView.setViewName("videoPageAdmin");
			}
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		int id;
		VideoContentAcadsBean videoContent;
		try {
			id = Integer.parseInt(idString);

			 if(checkLead(request, response)) {
				 
				 	modelAndView.addObject("isLeadStudent",1);
					videoContent = dao.getVideoContentForLeadById(id);
					
			 }else {

					videoContent = dao.getVideoContentById(id);
				 
			 }
			 
			//this.getVideoTranscriptUrl(videoContent);
			String acadYear =  videoContent.getYear();
			String acadMonth = videoContent.getMonth();
			String currentYearMonthArr[] = contentService.getRecordedvsLiveCurrentYearMonthForSapid(studentRegData);
			
			String currentAcadYear = currentYearMonthArr[0];
			String currentAcadMonth = currentYearMonthArr[1];
			
			if(currentAcadYear.equalsIgnoreCase(acadYear) && currentAcadMonth.equalsIgnoreCase(acadMonth) ) {
				request.setAttribute("AskFaculty", "block");
				request.setAttribute("RedirectLink", "none");
			}else {
				request.setAttribute("AskFaculty", "none");
				request.setAttribute("RedirectLink", "block");
				
			}
			
		} catch (NumberFormatException e) {
			  
			return new ModelAndView("studentPortalRediret");
		}
		
		
		/*
		 * Code for View count commented
		 * videoContent.setViewCount(videoContent.getViewCount() +1); boolean
		 * VideoContentUpdated=dao.updateVideoContent(videoContent);
		 * if(!VideoContentUpdated) { request.setAttribute("error", "true");
		 * request.setAttribute("errorMessage", "Unable to update view count...");
		 * 
		 * }
		 */
		try {
			ArrayList<String> templist=new ArrayList<>();
			List<VideoContentAcadsBean> relatedVideos = new ArrayList<VideoContentAcadsBean>();
			templist.add(videoContent.getSubject());
			if(userId.startsWith("7")) {
				/*if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
					relatedVideos = dao.getRelatedVideoContentList("",videoContent.getSubject(),templist,student);
				}*/
				Integer subjectCodeId = 0;
				try {
					//Fetch subject code id of a session id.
					subjectCodeId = videoContentService.fetchSubjectCodeId(videoContent.getSessionId());
				}catch(Exception e){
					  
				}
				
				if(subjectCodeId != 0) {
					//Fetch related session videos based on subject code id
					relatedVideos = videoContentService.fetchSessionVideosForSubject(subjectCodeId, acadCycleList);
				}else {
					List<String> commonSubjectList = (List<String>) request.getSession().getAttribute("commonSubjects");

					if(commonSubjectList == null || commonSubjectList.size() < 1)
						commonSubjectList = this.getCommonSubjects(request);

					//Fetch related session videos for a common subject
					relatedVideos = videoContentService.fetchSessionVideosBySearch(videoContent.getSubject(),
							null, commonSubjectList, student.getProgram());
				}
				
			}else {
				if(!roles.contains("Faculty")) {
				relatedVideos = dao.getRelatedVideoContentList("",videoContent.getSubject(),templist,student);
				}else {
					relatedVideos = dao.getRelatedVideoContentListForFaculty("",videoContent.getSubject(),templist,userId);
				}
			}
			
			request.setAttribute("videoContent", videoContent);
			request.setAttribute("relatedVideos", relatedVideos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		
		String transcriptDwnldUrl = videoContent.getVideoTranscriptUrl();		//for Subtopics
		String videoURL = videoContent.getVideoLink();
		if(transcriptDwnldUrl != null) {	
			try {					
				transcriptContent = hdHelper.getContentFromTranscriptUrl(transcriptDwnldUrl);// get content from transcript download url		
				if(transcriptContent != null) {
					Reader transcriptContentReader = new StringReader(transcriptContent);
					BufferedReader transcriptContentReaderBr = new BufferedReader(transcriptContentReader);			
					trackDataList =  WebVttParser.parse(transcriptContentReaderBr);	//extract transcriptContent in list		
					
					if(trackDataList != null) {
						 for (TextTrackImplLine track: trackDataList) {		
							 VideoContentAcadsBean vcb = new VideoContentAcadsBean();
							 String videoLink = getVideoURL(videoURL,track.getStartTime());		
							 vcb.setStartTime(convertTime(track.getStartTime()));
							 vcb.setEndTime(convertTime(track.getEndTime()));
							 vcb.setDuration(getDuration(track.getStartTime(),track.getEndTime()));
							 vcb.setFileName(track.getText());		
							 vcb.setKeywords(track.getText());
							 vcb.setStartTimeInSeconds(convertTimeInSeconds(track.getStartTime()));
							 vcb.setEndTimeInSeconds(convertTimeInSeconds(track.getEndTime()));
							 vcb.setVideoLink(videoLink);
							 
							 videoSubTopicsList.add(vcb);
						 }					
					}
				}
			}catch(Exception e) {
				  
			}
		}
		
		
		
		
		
		

//		List<VideoContentBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(new Long(id));
		request.setAttribute("videoSubTopicsList", videoSubTopicsList);
		
		try {
			//Get all queries by student of subject
			String session_id = videoContent.getSessionId().toString();
			String programSemSubjectId=request.getParameter("pssId");
			request.setAttribute("programSemSubjectId", programSemSubjectId);
			getCourseQueriesMap(request,videoContent.getSubject(),session_id);
		  
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		request.setAttribute("subject", videoContent.getSubject());
		request.setAttribute("sessionId", videoContent.getSessionId());
		
		return modelAndView;
	}
	
		public List<SessionQueryAnswer> getMyQA( String session_id,String sapId
				) throws Exception {

			
			ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
			
			List <SessionQueryAnswer> myQuestions = cDao.getStudentQAforSession( session_id,sapId);
			
			return myQuestions;
		}
		public List<SessionQueryAnswer> getPublicQA( String session_id,String sapId
				) throws Exception {

			
			ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
			 
		   List <SessionQueryAnswer>publicQAs = cDao.getPublicQAsforSession( session_id,sapId);
			
		// Send Back to Session QA Page
			return publicQAs;
		}
		
		private void getCourseQueriesMap(HttpServletRequest request, String subject,String session_id) {
			StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			String userId = (String) request.getSession().getAttribute("userId_acads");
			//Added sessionId removed myCourseQueries list by Saurabh
			List<SessionQueryAnswer> myQueries = dao.getQueriesForSessionByStudent(student.getSapid(), session_id);
			//Commented as no need to show Course Queries where session Id not available
//			List<SessionQueryAnswer> myCourseQueries = dao.getQueriesForCourseByStudent(subject, student.getSapid());
//			myQueries.addAll(myCourseQueries);
			List<SessionQueryAnswer> mySessionQuestions;
			
			try {
				mySessionQuestions = getMyQA(session_id, student.getSapid());
				
				myQueries.addAll(mySessionQuestions);
				

			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
			}
			
			List<SessionQueryAnswer> answeredPublicQueriesForCourse  = new ArrayList<SessionQueryAnswer>();
			List<SessionQueryAnswer> publicQuestions = new ArrayList<SessionQueryAnswer>();
			
			if(userId.startsWith("7")) {
				try {
						//getting publicQueries for specific sessionId
						answeredPublicQueriesForCourse = sessionQueryAnswerDAO.getPublicQueriesForCourseV2(student.getSapid(), session_id);
						publicQuestions = getPublicQA(session_id, student.getSapid());
						answeredPublicQueriesForCourse.addAll(publicQuestions);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						  
					}
				
			}
			
			request.getSession().setAttribute("myQueries", myQueries);
			request.getSession().setAttribute("answeredPublicQueriesForCourse", answeredPublicQueriesForCourse);

		}
		
	 @RequestMapping(value = "/watchVideoTopic", method = RequestMethod.GET)
		public ModelAndView watch(@RequestParam("id") String idString, HttpServletRequest request, HttpServletResponse response) {
		 if(!checkSession(request, response)){
				return new ModelAndView("studentPortalRediret");
			}
		 	ModelAndView modelAndView = new ModelAndView();
		 	String userId = (String) request.getSession().getAttribute("userId_acads");
		 	StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
			
		 	if(userId.startsWith("7")) {
				modelAndView.setViewName("videoPage");
			}else {
				modelAndView.setViewName("videoPageAdmin");
			}
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			int id = Integer.parseInt(idString);
			VideoContentAcadsBean videoContentTopic = dao.getVideoSubTopicById(new Long(id));
			VideoContentAcadsBean videoContent = dao.getVideoContentById((int)(long)videoContentTopic.getParentVideoId());
			ArrayList<String> templist=new ArrayList<>();
			templist.add(videoContent.getSubject());
			List<VideoContentAcadsBean> relatedVideos = dao.getRelatedVideoContentList("",videoContent.getSubject(), templist,student);
			request.setAttribute("videoContent", videoContentTopic);
			request.setAttribute("relatedVideos", relatedVideos);

			List<VideoContentAcadsBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(videoContent.getId());
			request.setAttribute("videoSubTopicsList", videoSubTopicsList);
			try {
				//Get all queries by student of subject
				getCourseQueriesMap(request,videoContent.getSubject(),null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
			}
			request.setAttribute("subject", videoContent.getSubject());
			
			
			return modelAndView;
		}
	@RequestMapping(value = "/editVideoContents", method = RequestMethod.GET)
	public ModelAndView editVideoContent(@RequestParam("id") int id, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("editVideoContentDetails");
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");

		VideoContentAcadsBean VideoContent = getVideoContentWithConfigIdsById(dao,id);
		if (VideoContent == null) {
			modelAndView.addObject("fileBean", new FileAcadsBean());
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Video Content Not Found");
			modelAndView.addObject("VideoContent", new VideoContentAcadsBean());
			modelAndView.addObject("action", "Add VideoContent");
			modelAndView.addObject("fileBean", new FileAcadsBean());
		} else {
			

				if(VideoContent.getConsumerTypeId() != null) {
					if(VideoContent.getConsumerTypeId().split(",").length > 1) {
						VideoContent.setAllowedToUpdate("false");
					}else {
						VideoContent.setAllowedToUpdate("true");
					}
				}else {
					VideoContent.setAllowedToUpdate("false");
				}
				
			
			
			
			List<VideoContentAcadsBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(new Long(id));
			modelAndView.addObject("videoSubTopicsList", videoSubTopicsList);
			request.setAttribute("videoSubTopicsList", videoSubTopicsList);
			modelAndView.addObject("fileBean", new FileAcadsBean());
			modelAndView.addObject("videoContent", VideoContent);
			modelAndView.addObject("action", "Edit VideoContent");
			modelAndView.addObject("fileBean", new FileAcadsBean());
		}
		
		modelAndView.addObject("consumerType", getConsumerTypeList());
		modelAndView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
		modelAndView.addObject("programIdNameMap", getProgramIdNameMap());
		
		modelAndView.addObject("videoSubTopic", new VideoContentAcadsBean());
		List<VideoContentAcadsBean> VideoContentsList = dao.getAllVideoContentList();
		modelAndView.addObject("VideoContentsList", VideoContentsList);
		modelAndView.addObject("acadYearList",ACAD_YEAR_LIST);
		return modelAndView;
	}
	

	private VideoContentAcadsBean getVideoContentWithConfigIdsById(VideoContentDAO dao,  int id) {

		VideoContentAcadsBean content = dao.getVideoContentById(id);
		
		List<VideoContentAcadsBean> configDetails= dao.getProgramsListForCommonVideoContent(content.getId());

		 String consumerTypeId = "";
		 String programStructureId = "";
		 String programId = "";
		 int consumerTypeIdCounter = 0;
		 int programStructureIdCounter = 0;
		 int programIdCounter = 0;
		Map<String,String> checkForConsumerTypeId = new HashMap<>();
		Map<String,String> checkForProgramId = new HashMap<>();
		Map<String,String> checkForProgramStructureId = new HashMap<>();
		
		for(VideoContentAcadsBean c :configDetails) {
			if(!checkForConsumerTypeId.containsKey(c.getConsumerTypeId())) {
				if(consumerTypeIdCounter==0) {
					consumerTypeId += c.getConsumerTypeId();
					consumerTypeIdCounter++;
				}else {
					consumerTypeId += ","+c.getConsumerTypeId();
					consumerTypeIdCounter++;
				}
				checkForConsumerTypeId.put(c.getConsumerTypeId(), "");
			}
			
			if(!checkForProgramStructureId.containsKey(c.getProgramStructureId())) {
				if(programStructureIdCounter==0) {
					programStructureId += c.getProgramStructureId();
					programStructureIdCounter++;
				}else {
					programStructureId += ","+c.getProgramStructureId();
					programStructureIdCounter++;
				}
				checkForProgramStructureId.put(c.getProgramStructureId(), "");
			}

			if(!checkForProgramId.containsKey(c.getProgramId())) {
				if(programIdCounter==0) {
					programId += c.getProgramId();
					programIdCounter++;
				}else {
					programId += ","+c.getProgramId();
					programIdCounter++;
				}
				checkForProgramId.put(c.getProgramId(), "");
			}
		}
		
		
		content.setConsumerTypeId(consumerTypeId);
		content.setProgramStructureId(programStructureId);
		content.setProgramId(programId);
		
		return content;
	}

	
	@RequestMapping(value = "/deleteVideoContents", method = RequestMethod.GET)
	public ModelAndView deleteVideoContent(@RequestParam("id") int id, HttpServletRequest request) {
		
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");

		int deletedRow = 0;
		try {
			//Delete video content entry 
			deletedRow = dao.deleteVideoContent(id);
		} catch (Exception e) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Occured while deleting the Video Content.");
			  
		}
		if (deletedRow == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Occured while deleting the Video Content.");
		} else {
			setSuccess(request, "VideoContent has been deleted successfully");
		}
	
		modelAndView.addObject("fileBean", new FileAcadsBean());
		List<VideoContentAcadsBean> VideoContentsList = dao.getAllVideoContentList();
		modelAndView.addObject("VideoContentsList", VideoContentsList);
		modelAndView.addObject("videoContent", new VideoContentAcadsBean());
		modelAndView.addObject("action", "Add VideoContent");
		return modelAndView;
	}
	
	@RequestMapping(value = "/deleteSingleVideoContentFromCommonSetup", method = RequestMethod.GET)
	public ModelAndView deleteSingleVideoContentFromCommonSetup(@RequestParam("id") Long id, 
														   @RequestParam("consumerProgramStructureId") String consumerProgramStructureId, 
														   HttpServletRequest request,
			                                               HttpServletResponse response) {
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		
		
		int countOfProgramsContentApplicableTo = dao.getCountOfProgramsContentApplicableToByVideoId(id);

		if(countOfProgramsContentApplicableTo > 0) {
			int deletedRows = dao.deleteVideoContentIdAndMasterKeyMapping(id,consumerProgramStructureId);
			
			if(deletedRows > 0) {
				if(countOfProgramsContentApplicableTo == 1) {
					int deletedContentRows;
					try {
						deletedContentRows = dao.deleteVideoContent(id.intValue());
			
					if(deletedContentRows < 1){
							request.setAttribute("error", "true");
							request.setAttribute("errorMessage", "Error in deleting Content Record.");
							return uploadVideoContentForm(request, response);
						}
					} catch (Exception e) {
						  
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error in deleting Content Record.");
						return uploadVideoContentForm(request, response);
					}
				}
			}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in deleting Mapping Record.");
			return uploadVideoContentForm(request, response);
			}
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Record deleted successfully");
		}else {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in getting countOfProgramsContentApplicableTo.");
			return uploadVideoContentForm(request, response);
		}
	
		
		
		
		return uploadVideoContentForm(request, response);
	}
	

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/searchVideosOld", method = RequestMethod.POST)
	public ModelAndView searchVideo(@ModelAttribute VideoContentAcadsBean VideoContent, HttpServletRequest request,
			HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		ArrayList<String> applicableSubjects = new ArrayList<String>();
		ModelAndView modelAndView = new ModelAndView();
		String userId = (String) request.getSession().getAttribute("userId_acads");
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		
		if(userId.startsWith("7")) {
			modelAndView.setViewName("videoHome");
			 applicableSubjects = (ArrayList<String>)request.getSession().getAttribute("applicableSubjects"); 
			
		}else {
			modelAndView.setViewName("videoHomeAdmin");

			applicableSubjects = subjectList;
			applicableSubjects.add("Orientation");
			applicableSubjects.add("Executive Program Orientation");
			
		}
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		String searchItem = request.getParameter("searchItem");
		String subject = request.getParameter("subject");
		if(("".equals(searchItem) && "".equals(subject)) || (subject == null && searchItem==null) || applicableSubjects==null) {
				setSuccess(request, "Search Keyword field is required..."); 
				//Change academicCycle adding later
				PageAcads<VideoContentAcadsBean> page = new PageAcads<VideoContentAcadsBean>();
				if(userId.startsWith("7")) {
					if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
						page = dao.getVideoContentPage(1, pageSize,null,null,student);
					}
				}else {
					page = dao.getVideoContentPage(1, pageSize,null,null,student);
				}
				
				List<VideoContentAcadsBean> videoContentListPage = page.getPageItems();

				modelAndView.addObject("page", page);
				modelAndView.addObject("rowCount", page.getRowCount());

				modelAndView.addObject("VideoContentsList", videoContentListPage);
				modelAndView.addObject("subjectVideosMap", null);
				request.setAttribute("VideoContentsList", videoContentListPage);
				request.setAttribute("subjectVideosMap", null);
				request.setAttribute("subjectList", getSubjectList());
				return modelAndView;
		}
		List<VideoContentAcadsBean> VideoContentsList = new ArrayList<VideoContentAcadsBean>();
		if(userId.startsWith("7")) {
			if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
				VideoContentsList = dao.getRelatedVideoContentList(searchItem, subject,applicableSubjects,student);
			}
		}else {
			VideoContentsList = dao.getRelatedVideoContentList(searchItem, subject,applicableSubjects,student);
		}
		
		int size = VideoContentsList != null ? VideoContentsList.size() : 0;
		
		
		if (size > 0) {
			setSuccess(request, "Found " + size + " videos matching for \""+searchItem+"\"" );
		} else {
			setSuccess(request, "No videos matching your search for "+ searchItem +". Try some more specific keywords.");
		} 
		request.setAttribute("VideoContentsList", VideoContentsList);
		
		modelAndView.addObject("academicCycle", "All");
		modelAndView.addObject("selectedSubject", "All"); 
		FacultyAcadsBean tempFaculty=new FacultyAcadsBean();
		tempFaculty.setFacultyId("All");
		modelAndView.addObject("selectedFaculty",tempFaculty);
		
		modelAndView.addObject("searchItem", searchItem);

		modelAndView.addObject("showPagination", false);

		//for batch track
		modelAndView.addObject("selectedBatch","All");
		
		return modelAndView;
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/searchVideos")
	public ModelAndView searchVideos(@ModelAttribute VideoContentAcadsBean vdoCntBean, HttpServletRequest request,
			HttpServletResponse response) {
		
		//Check session is expired or not
		if(!checkSession(request, response)) 
			return new ModelAndView("studentPortalRediret");
				
		ModelAndView modelAndView = new ModelAndView();
		List<ConsumerProgramStructureAcads> PSSIdWithSubject = new ArrayList<ConsumerProgramStructureAcads>();
		List<String> PSSIdList = new ArrayList<String>();
		List<String> commonSubjectList = new ArrayList<String>();
		List<VideoContentAcadsBean> sessionVideosList = new ArrayList<VideoContentAcadsBean>();
		
		PersonAcads person = (PersonAcads) request.getSession().getAttribute("user_acads");
		String roles = person.getRoles();
		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		String searchKeyword = request.getParameter("searchItem");
		
		if(userId.startsWith("7")) {
			modelAndView.setViewName("videoHome");
			//Read programSemSubjectId's With Subject and common subject's from session
			PSSIdWithSubject = (List<ConsumerProgramStructureAcads>) request.getSession().getAttribute("programSemSubjectIdWithSubject");
			commonSubjectList = (List<String>) request.getSession().getAttribute("commonSubjects");
			
			//Prepare program sem subject list from the PSSIdWithSubject 
			PSSIdWithSubject.forEach((conPrgStrBean)->{
				PSSIdList.add(conPrgStrBean.getProgramSemSubjectId());
			});
			
			try {
				//Fetch session videos for specific keyword given by end user.
				sessionVideosList = videoContentService.fetchSessionVideosBySearch(searchKeyword, PSSIdList, 
						commonSubjectList, student.getProgram());
			} catch (Exception e) {
				  
			}
		}//if
		else {
			modelAndView.setViewName("videoHomeAdmin");	
			try {
				//Fetch session videos for specific keyword given by end user.
				if(!roles.contains("Faculty")) {
				sessionVideosList = videoContentService.fetchSessionVideosBySearch(searchKeyword);
				}else {
					sessionVideosList = videoContentService.fetchSessionVideosBySearchAndFaculty(searchKeyword, userId);
				}
			} catch (Exception e) {
				  
			}
		}//else
		
		//Assign zero in the size if videoContentList is null else size of list.
		int size = sessionVideosList != null ? sessionVideosList.size() : 0;
		
		if (size > 0) {
			setSuccess(request, "Found " + size + " videos matching for \""+searchKeyword+"\"" );
		} else {
			setSuccess(request, "No videos matching your search for "+ searchKeyword +". Try some more specific keywords.");
		} 
		request.setAttribute("VideoContentsList", sessionVideosList);
		
		modelAndView.addObject("academicCycle", "All");
		modelAndView.addObject("selectedSubject", "All"); 
		
		FacultyAcadsBean tempFaculty=new FacultyAcadsBean();
		tempFaculty.setFacultyId("All");
		modelAndView.addObject("selectedFaculty",tempFaculty);
		
		modelAndView.addObject("searchItem", searchKeyword);
		modelAndView.addObject("showPagination", false);
		modelAndView.addObject("roles",roles);

		//for batch track
		modelAndView.addObject("selectedBatch","All");
		
		return modelAndView;
	}
	
			@RequestMapping(value = "/videosForSubject", method = RequestMethod.GET)
			public ModelAndView videosForSubject( HttpServletRequest request,
					HttpServletResponse response) {
				if(!checkSession(request, response)){
					return new ModelAndView("studentPortalRediret");
				}
				ModelAndView modelAndView = new ModelAndView();
				String userId = (String) request.getSession().getAttribute("userId_acads");
				StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
				if(userId.startsWith("7")) {
					modelAndView.setViewName("videoHome");
				}else {
					modelAndView.setViewName("videoHomeAdmin");
				}
				VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
				String subject = request.getParameter("subject");
				if("".equals(subject) || subject == null) {
						setSuccess(request, "Subject Not Found..."); 
						//change academicCycle adding later
						PageAcads<VideoContentAcadsBean> page = new PageAcads<VideoContentAcadsBean>();
						List<VideoContentAcadsBean> videoContentListPage = new ArrayList<VideoContentAcadsBean>();
						
						if(userId.startsWith("7")) {
							if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
								page = dao.getVideoContentPage(1, pageSize,null,null,null);
								videoContentListPage = page.getPageItems();
							}
						}else {
							page = dao.getVideoContentPage(1, pageSize,null,null,null);
							videoContentListPage = page.getPageItems();
						}

						modelAndView.addObject("page", page);
						modelAndView.addObject("rowCount", page.getRowCount());

						modelAndView.addObject("VideoContentsList", videoContentListPage);
						modelAndView.addObject("subjectVideosMap", null);
						request.setAttribute("VideoContentsList", videoContentListPage);
						request.setAttribute("subjectVideosMap", null);
						request.setAttribute("subjectList", getSubjectList());
						return modelAndView;
				}
				ArrayList<String> templist=new ArrayList<>();
				
				List<VideoContentAcadsBean> VideoContentsList = dao.getRelatedVideoContentList("", subject,templist,student);
				int size = VideoContentsList != null ? VideoContentsList.size() : 0;
				
				
				if (size > 0) {
					setSuccess(request, "Found " + size + " videos matching for \""+subject+"\"" );
				} else {

					setSuccess(request, "No videos matching your search for "+ subject +". Try some more specific keywords.");
				} 
				request.setAttribute("VideoContentsList", VideoContentsList);
				
				modelAndView.addObject("academicCycle", "All");
				modelAndView.addObject("selectedSubject", "All"); 
				FacultyAcadsBean tempFaculty=new FacultyAcadsBean();
				tempFaculty.setFacultyId("All");
				modelAndView.addObject("selectedFaculty",tempFaculty);
				
				modelAndView.addObject("searchItem", "");

				modelAndView.addObject("showPagination", false);

				//for batch track
				modelAndView.addObject("selectedBatch","All");
			
				return modelAndView;
			}
			
			// '/acads/searchByFilter?searchInput='+searchInput+'&faculty='+faculty+'&subject='+subject+'&cycle='+cycle
			// searchByFilter start
			@SuppressWarnings("unchecked")
			@RequestMapping(value="/searchByFilter",method=RequestMethod.GET)
			public ModelAndView searchByFilter(HttpServletRequest request,
									   HttpServletResponse response,
									   @RequestParam("searchInput") String searchItem,
									   @RequestParam("faculty") String faculty,
									   @RequestParam("subject") String subject,
									   @RequestParam("cycle") String cycle, @RequestParam("batch") String batch) {
				if(!checkSession(request, response)){
					return new ModelAndView("studentPortalRediret");
				}
				
				PersonAcads person = (PersonAcads) request.getSession().getAttribute("user_acads");
				String roles = person.getRoles();
				
				StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
				ArrayList<String> applicableSubjects = new ArrayList<String>();
				ModelAndView modelAndView = new ModelAndView();
				String userId = (String) request.getSession().getAttribute("userId_acads");
				
				VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
				
				if(userId.startsWith("7")) {
					modelAndView.setViewName("videoHome");
					 applicableSubjects = (ArrayList<String>)request.getSession().getAttribute("applicableSubjects"); 
					
				}else {
					modelAndView.setViewName("videoHomeAdmin");
					if(!roles.contains("Faculty")) {
					applicableSubjects = subjectList;
					applicableSubjects.add("Orientation");
					applicableSubjects.add("Executive Program Orientation");
					}else {
						applicableSubjects=dao.getFacultySubjectListForSessions(userId);
					}
				}
				List<String> academicCycleList= (List<String>) request.getSession().getAttribute("academicCycleList");

				List<FacultyAcadsBean> facultyList= (List<FacultyAcadsBean>) request.getSession().getAttribute("facultyList");
				
				ArrayList<String> tempSubject = new ArrayList<String>();
				
				if("All".equalsIgnoreCase(subject)) {
					tempSubject.addAll(applicableSubjects);
				}else {
					tempSubject.add(subject);
				}
				
				FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
				List<VideoContentAcadsBean> VideoContentsList = new ArrayList<VideoContentAcadsBean>();
				if(userId.startsWith("7")) {
					if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
						VideoContentsList= dao.getVideoForSearchByFilter(searchItem, faculty, tempSubject, cycle, batch, student);
					}
				}else {
					VideoContentsList= dao.getVideoForSearchByFilter(searchItem, faculty, tempSubject, cycle, batch, student);
				}
				
				int size = VideoContentsList != null ? VideoContentsList.size() : 0;
				if(!roles.contains("Faculty")) {
				FacultyAcadsBean tempFaculty=new FacultyAcadsBean();
				if(!"All".equalsIgnoreCase(faculty)) {
					tempFaculty= fDao.findfacultyByFacultyId(faculty);
					faculty="Prof. "+tempFaculty.getFirstName()+" "+tempFaculty.getLastName();
					modelAndView.addObject("selectedFaculty",tempFaculty);
				}else {

					tempFaculty.setFacultyId("All");
					modelAndView.addObject("selectedFaculty",tempFaculty);
				}
				}
				if (size > 0) {
					if("".equals(searchItem)) {
						setSuccess(request, "Found " + size + " videos matching for Subject : "+subject+" and Faculty : "+faculty+" and Academic Cycle : "+cycle+" and Batch Track : "+batch);
							
					}else {

						setSuccess(request, "Found " + size + " videos matching for \""+searchItem+"\" and Subject : "+subject+" and Faculty : "+faculty+" and Academic Cycle : "+cycle+" and Batch Track : "+batch );
					}
				} else {

					setSuccess(request, "No videos matching your search for "+ searchItem +"  and Subject : "+subject+" and Faculty : "+faculty+" and Academic Cycle : "+cycle+" and Batch Track : "+batch+". Try some more specific keywords.");
				} 
				request.setAttribute("VideoContentsList", VideoContentsList);
				
				modelAndView.addObject("academicCycleList", academicCycleList);
				
				modelAndView.addObject("academicCycle", cycle); 
				
				modelAndView.addObject("selectedSubject", subject);
				
				modelAndView.addObject("facultyList",facultyList);
				
				modelAndView.addObject("searchItem", searchItem);
				
				modelAndView.addObject("roles",roles);
				if(!roles.contains("Faculty")) {
				request.setAttribute("subjectList", getSubjectList());
				}else {
					request.setAttribute("subjectList", applicableSubjects);
				}
				//for batch track filter
				modelAndView.addObject("selectedBatch",batch);
				modelAndView.addObject("allBatchTracks",dao.getBatchTracks());

				return modelAndView;
			}
			// searchByFilter End
	
	/* CRUD for video Subtopics Start */
	
	@RequestMapping(value = "/postVideoSubTopic", method = RequestMethod.POST)
	public ModelAndView postVideoSubTopic(@ModelAttribute VideoContentAcadsBean VideoContent, 
										  HttpServletRequest request,
										  HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("editVideoContentDetails");
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		VideoContentAcadsBean parentVideoContent = dao.getVideoContentById((int) (long)VideoContent.getParentVideoId()); 
		VideoContent.setSubject(parentVideoContent.getSubject());
		VideoContent.setThumbnailUrl(parentVideoContent.getThumbnailUrl());
		VideoContent.setSessionId(parentVideoContent.getSessionId());
		try {
			//Code to set custom video link for topic Start
			String[] timeArray=	VideoContent.getStartTime().split(":",-1);
			String timeStamp="#t=";
			int count=0;
			for(String t : timeArray){
				if(count==0){
				timeStamp=timeStamp+t+"h";
				}
				if(count==1){
					timeStamp=timeStamp+t+"m";
				}
				if(count==2){
					timeStamp=timeStamp+t+"s";
				}
				count++;
			}
			String tempLink = parentVideoContent.getVideoLink()+timeStamp;
			VideoContent.setVideoLink(tempLink);
			//Code to set custom video link for topic end
			
			
			if (VideoContent.getId()==null) {
				long key = dao.saveVideoSubTopic(VideoContent);
				setSuccess(request, "Video Sub Topic has been saved successfully");
			} else {
				boolean VideoContentUpdated = dao.updateVideoSubTopic(VideoContent);
				if (VideoContentUpdated) {
					setSuccess(request, "VideoContent has been updated successfully");
				}
			}
		} catch (Exception e) {
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in saving sub topic details.");
		} 
		modelAndView.addObject("videoContent", parentVideoContent);

		List<VideoContentAcadsBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(VideoContent.getParentVideoId());
		modelAndView.addObject("videoSubTopicsList", videoSubTopicsList);
		request.setAttribute("videoSubTopicsList", videoSubTopicsList);

		modelAndView.addObject("videoSubTopic", new VideoContentAcadsBean());
		modelAndView.addObject("action", "Edit VideoContent");
		modelAndView.addObject("fileBean", new FileAcadsBean());
		return modelAndView;
	}

	@RequestMapping(value = "/deleteVideoSubTopic", method = RequestMethod.GET)
	public ModelAndView deleteVideoSubTopic(@RequestParam("id") int id, HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView("videoContentDetails");
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");

		int deletedRow = dao.deleteVideoSubTopic(new Long(id));
		if (deletedRow == 0) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Occured while deleting the Video Content.");
		} else {
			setSuccess(request, "VideoContent has been deleted successfully");
		}
		modelAndView.addObject("fileBean", new FileAcadsBean());

		List<VideoContentAcadsBean> VideoContentsList = dao.getAllVideoContentList();
		modelAndView.addObject("VideoContentsList", VideoContentsList);
		modelAndView.addObject("videoContent", new VideoContentAcadsBean());
		modelAndView.addObject("action", "Add VideoContent");
		modelAndView.addObject("fileBean", new FileAcadsBean());
		return modelAndView;
	}


	
	/* CRUD for video Subtopics End*/
	

	/* RESTful mobile api Start */
	
//	to be deleted, api shifted to rest controller
//	@RequestMapping(value = "/m/videosHome", method = RequestMethod.POST)
//	public ResponseEntity<Page<VideoContentBean>> getVideoHomeContent(@RequestBody StudentBean student ,
//																	  @Context HttpServletRequest request) {
//		
//		ArrayList<String> allsubjects = applicableSubjectsForStudentForRestApi(student);
//		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//
//		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
//		StudentBean studentBeanFromDB = cdao.getSingleStudentsData(student.getSapid());
//		int pageNo;
//		try {
//			pageNo = Integer.parseInt(request.getParameter("pageNo"));
//			if (pageNo < 1) {
//				pageNo = 1;
//			}
//		} catch (NumberFormatException e) {
//			pageNo = 1;
//			  
//		}
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		//change academicCycle adding later
//		ArrayList<String> academicCycleList =new ArrayList<String>();
//		academicCycleList=dao.getAcademicCycleList();
//		ArrayList<String> academicCycleListForDb =new ArrayList<String>();
//		String academicCycle = "All"; 
//		if(academicCycle==null || "".equals(academicCycle) || "All".equals(academicCycle)){
//			academicCycle="All";
//			academicCycleListForDb.addAll(academicCycleList);
//		} else{
//			academicCycleListForDb.add(academicCycle);
//		}
//		
//		Page<VideoContentBean> page = new Page<VideoContentBean>();
//		
//		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//			page= dao.getVideoContentPage(pageNo, pageSize,allsubjects,academicCycleListForDb,studentBeanFromDB);
//		}
//		
//		return new ResponseEntity<Page<VideoContentBean>>(page,headers,HttpStatus.OK);
//	}
	private ArrayList<String> applicableSubjectsForStudentForRestApi(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingAcadsBean> allsubjects = new ArrayList<>();
		
		ArrayList<ProgramSubjectMappingAcadsBean> unAttemptedSubjectsBeans = new ArrayList<>();
		
		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean studentRegistrationData = cdao.getStudentRegistrationData(student.getSapid());
		StudentAcadsBean studentBeanFromDB = cdao.getSingleStudentsData(student.getSapid());
		studentService.mgetWaivedInSubjects(studentBeanFromDB);
		studentService.mgetWaivedOffSubjects(studentBeanFromDB);
		student.setWaivedOffSubjects(studentBeanFromDB.getWaivedOffSubjects());
		student.setWaivedInSubjectSemMapping(studentBeanFromDB.getWaivedInSubjectSemMapping());
		student.setWaivedInSubjects(studentBeanFromDB.getWaivedInSubjects());
		if(studentRegistrationData == null){
			//Get fail subjects content if studnet does not have registration for current sem.
			failSubjectsBeans = getFailSubjects(student);

			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				allsubjects.addAll(failSubjectsBeans);
			}
			
			
		}else{
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			ArrayList<ProgramSubjectMappingAcadsBean> currentSemSubjects = getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
			}
			//If current sem is 1, then there will be no failed subjects. Get failed subjects only when he is in higher semesters
			if(!"1".equals(studentRegistrationData.getSem())){
				failSubjectsBeans = getFailSubjects(student);

				if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
					allsubjects.addAll(failSubjectsBeans);
				}
			}
		}
		
		//Get subjects never attempted or results not declared
		unAttemptedSubjectsBeans = cdao.getUnAttemptedSubjects(student.getSapid());
		if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			allsubjects.addAll(unAttemptedSubjectsBeans);
		}


		//Sort all subjects semester wise.
		Collections.sort(allsubjects);
		
		
		ArrayList<String> applicableSubjects=new ArrayList<>();
		for(ProgramSubjectMappingAcadsBean psmb:allsubjects){
			if(!student.getWaivedOffSubjects().contains(psmb.getSubject())) {
				applicableSubjects.add(psmb.getSubject());
			}
		}
		
		if(student.getWaivedInSubjects() != null) {
			for(String subject: student.getWaivedInSubjects()) {
				if(!applicableSubjects.contains(subject)) {
					applicableSubjects.add(subject);
				}
			}
		}
		
		return applicableSubjects;
				
	}
	
	private void getVideoTranscriptUrl(VideoContentAcadsBean videoContent) {
		try {
			if(videoContent.getVideoTranscriptUrl() != null && videoContent.getVideoTranscriptUrl().indexOf("ngasce.zoom.us") != -1) {
				return;
			}
			VimeoManager vimeoManager = new VimeoManager();
			URI uri = new URI(videoContent.getVideoLink());
			String vimeoId = uri.getPath();
			vimeoId = vimeoId.substring(vimeoId.lastIndexOf('/') + 1);
			String textTrackUrl = "https://api.vimeo.com/videos/"+ vimeoId +"/texttracks";
			videoContent.setVideoTranscriptUrl(vimeoManager.getTranscriptLinkByUploadLinkUrl(textTrackUrl));
		}catch (Exception e) {
			// TODO: handle exception
			  
		}
	}

//	@CrossOrigin(origins = "*", allowedHeaders = "*")
//	@RequestMapping(value = "/m/watchVideos", method = RequestMethod.GET, produces="application/json")
//	public ResponseEntity<HashMap<String, List<VideoContentBean>>> watchVideoForMobileApi(@RequestParam("id") String idString, HttpServletRequest request) {
//			
//
//		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//		List<VideoContentBean> newVideoSubTopicsList = new ArrayList<VideoContentBean>();
//
//		int id = 0;
//		VideoContentBean videoContent = null;
//		try {
//			id = Integer.parseInt(idString);
//			videoContent = dao.getVideoContentById(id);
//			//this.getVideoTranscriptUrl(videoContent);
//		} catch (NumberFormatException e) {
//			  
//			
//		} 
//		List<VideoContentBean> mainVideo= new ArrayList<VideoContentBean>();
//		mainVideo.add(videoContent);
//
//		
//	
////		List<VideoContentBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(new Long(id));
//		
//		HashMap<String, List<VideoContentBean>> response = new HashMap<String, List<VideoContentBean>>();
//		response.put("mainVideo",mainVideo);
//		response.put("videoSubTopicsList",newVideoSubTopicsList);
//		
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json"); 
//		
//		return new ResponseEntity<HashMap<String, List<VideoContentBean>>>(response,headers,HttpStatus.OK);
//	}
//	 @RequestMapping(value = "/m/watchVideoTopic", method = RequestMethod.GET)
//		public ResponseEntity<HashMap<String, List<VideoContentBean>>> watchForMobileApi(@RequestParam("id") String idString, HttpServletRequest request) {
//		 
//			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//			int id = Integer.parseInt(idString);
//			VideoContentBean videoContentTopic = dao.getVideoSubTopicById(new Long(id));
//			VideoContentBean videoContent = dao.getVideoContentById((int)(long)videoContentTopic.getParentVideoId());
//			ArrayList<String> templist=new ArrayList<>();
//			templist.add(videoContent.getSubject());
//			//List<VideoContentBean> relatedVideos = dao.getRelatedVideoContentList(videoContent.getSubject(),videoContent.getSubject(), templist);
//
//			List<VideoContentBean> mainVideo= new ArrayList<VideoContentBean>();
//			mainVideo.add(videoContentTopic);
//
//			List<VideoContentBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(videoContent.getId());
//			
//			HashMap<String, List<VideoContentBean>> response = new HashMap<String, List<VideoContentBean>>();
//			response.put("mainVideo",mainVideo);
//			response.put("videoSubTopicsList",videoSubTopicsList);
//			
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json"); 
//			
//			return new ResponseEntity<HashMap<String, List<VideoContentBean>>>(response,headers,HttpStatus.OK);
//		}
//	 
//		@RequestMapping(value = "/m/subjectsForVideos", method = RequestMethod.POST, produces="application/json", consumes="application/json")
//		public ResponseEntity<List<String>> getSubjectsForVideos(@RequestBody StudentBean student) {
//			
//			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//			ArrayList<String> allsubjects=null;
//			List<String> subjectsWithVideos=null;
//			try {
//				allsubjects = applicableSubjectsForStudentForRestApi(student);
//				subjectsWithVideos=dao.getSubjectsWithVideos(allsubjects);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				  
//			}
//			
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json"); 
//			
//			return new ResponseEntity<List<String>>(subjectsWithVideos,headers,HttpStatus.OK);
//		}
//		
//		//get lastcyle videos start
//		@RequestMapping(value = "/m/lastCycleVideos", method = RequestMethod.POST, produces="application/json", consumes="application/json")
//		public ResponseEntity<ArrayList<VideoContentBean>> lastCycleVideos(@RequestBody StudentBean student,
//															@RequestParam("month") String month,
//															@RequestParam("year") Integer year 
//															) {
//			
//			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//			ArrayList<VideoContentBean> lastCycleVideos = new ArrayList<VideoContentBean>();
////			ArrayList<VideoContentBean> finalLastCycleVideos = new ArrayList<VideoContentBean>();
//			try {
////				lastCycleVideos=dao.getVideoContentForSubjectYearMonth(student.getSubject(),year,month);
//				if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//					lastCycleVideos=dao.getVideoContentForSubjectYearMonthAndSapId(student.getSubject(),year,month,student.getSapid());
//				}
////				for(VideoContentBean videoContentBean : lastCycleVideos){
////					if(dao.checkIfBookmarked(student.getSapid(),videoContentBean.getId().toString())){
////						videoContentBean.setBookmarked("Y");
////					}
////					finalLastCycleVideos.add(videoContentBean);
////				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				  
//			}
//			
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json"); 
//			
//			return new ResponseEntity<ArrayList<VideoContentBean>>(lastCycleVideos,headers,HttpStatus.OK);
//		}
//		//end
//	 
//		@RequestMapping(value = "/m/videosForSubject", method = RequestMethod.GET)
//		public ResponseEntity<List<VideoContentBean>> videosForSubjectForMobile( HttpServletRequest request) {
//			 
//			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//			String subject = request.getParameter("subject");
//			String sapid = request.getParameter("sapid");
//			ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
//			
//			ArrayList<String> templist=new ArrayList<>();
////			List<VideoContentBean> finalResponse = new ArrayList<VideoContentBean>();
//			List<VideoContentBean> videoContentsList=new ArrayList<VideoContentBean>();
//			try {
//
//				StudentBean student = cDao.getSingleStudentsData(sapid);
////				if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
//					if(!StringUtils.isBlank(sapid)) {
//						videoContentsList = dao.getRelatedVideoContentListBySapId("", subject,templist,student);
//					} else {
//						videoContentsList = dao.getRelatedVideoContentList("", subject,templist, student);
//					}
////				}
//				
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				  
//			}
////			for(VideoContentBean contentBean : videoContentsList){
////				if(dao.checkIfBookmarked(sapid,String.valueOf(contentBean.getId()))){
////					contentBean.setBookmarked("Y");
////				}
////				finalResponse.add(contentBean);
////			}
//			
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json"); 
//			
//
//			return new ResponseEntity<List<VideoContentBean>>(videoContentsList,headers,HttpStatus.OK);
//		}
//
//		@RequestMapping(value = "/m/videosForSession", method = RequestMethod.GET, produces="application/json")
//		public ResponseEntity<List<VideoContentBean>> m_courseDetailsQueries(
//				@RequestParam("sessionId") String sessionId, 
//				@RequestParam(required = false, name = "sapid") String sapid) throws Exception {
//			HttpHeaders headers = new HttpHeaders();
//		    headers.add("Content-Type", "application/json"); 
//			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
////			List<VideoContentBean> finalResponse = new ArrayList<VideoContentBean>();
//			List<VideoContentBean> response= new ArrayList<VideoContentBean>();
//
//			if(!StringUtils.isBlank(sapid)) {
//				 response= dao.getVideosForSessionBySapId(sessionId, sapid);
//			} else {
//				 response= dao.getVideosForSession(sessionId);
//			}
//			
//			
////				for(VideoContentBean contentBean : response){
////					if(dao.checkIfBookmarked(sapid,String.valueOf(contentBean.getId()))){
////						contentBean.setBookmarked("Y");
////					}
////					finalResponse.add(contentBean);
////				}
//			return new ResponseEntity<List<VideoContentBean>>(response, headers,  HttpStatus.OK);
//	}
//	
//	@RequestMapping(value = "/m/getVideoRecordingDetailById", method = RequestMethod.GET, produces="application/json")
//	public ResponseEntity<List<VideoContentBean>> getVideoRecordingDetailById(@RequestParam("sessionId") int VideoId) throws Exception {
//		HttpHeaders headers = new HttpHeaders();
//	    headers.add("Content-Type", "application/json"); 
//		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//		List<VideoContentBean> response = new ArrayList<VideoContentBean>();
//		response.add(dao.getVideoContentById(VideoId));
//		return new ResponseEntity<List<VideoContentBean>>(response, headers,  HttpStatus.OK);
//	}
		
		/* RESTful mobile api End */
		
		//Hit this link to update sessionDates in video_content table if any are missing
		@RequestMapping(value="/updateAllVideoSessionDates",method=RequestMethod.GET)
		public void updateAllVideoSessionDates(){
			
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO"); 
			
			List<VideoContentAcadsBean> videoContentsList=null;
			boolean updated=false;
			TimeTableDAO sessionDao = (TimeTableDAO)act.getBean("timeTableDAO");
			SessionDayTimeAcadsBean session = null;
			try {
				videoContentsList = dao.getAllVideoContentList();
				for(VideoContentAcadsBean video : videoContentsList){
					try {
						session = sessionDao.findScheduledSessionById(video.getSessionId().toString());
						video.setSessionDate(session.getDate());
						updated=dao.updateVideoContent(video);
						
						if(!updated){
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//  
					}
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
			} 
		}

		//update thumbnail where there were default ones start
		@RequestMapping(value = "/updateDefaultThumbnails", method = RequestMethod.GET)
		public ModelAndView updateDefaultThumbnails(HttpServletRequest request,
													HttpServletResponse response) {
			

			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			
			
			List<VideoContentAcadsBean> videosWithDefaultThumbnails= dao.getVideosWithDefaultThumbnails();
			String returnMessage="";
			for(VideoContentAcadsBean video : videosWithDefaultThumbnails) {
				returnMessage=returnMessage+getThumbnailImage(video);
			}
			setSuccess(request, returnMessage);
			
			return new ModelAndView("studentPortalRediret");
		}
		//end
		public String getThumbnailImage(VideoContentAcadsBean video) {
			ExcelHelper excelHelper = new ExcelHelper();
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			
			String mobileUrlHd= video.getMobileUrlHd();
			//Code to generate videolink for website  from mobileUrlHd Start
			String vimeoId="";
			if("".equals(mobileUrlHd) || mobileUrlHd==null){
				return " <br> No mobileUrlHd for video "+video.getFileName()+" subject : "+video.getSubject();
			}else {
				if(mobileUrlHd.contains("progressive_redirect/playback")) {
					vimeoId = mobileUrlHd.split("/playback/")[1].split("/rendition/")[0];
				}
				else {
					String[] linkSplitArray1 = mobileUrlHd.split("/external/", -1);
					String[] linkSplitArray2 = linkSplitArray1[1].split(".hd.", -1);
					vimeoId = linkSplitArray2[0];
				}
			}
			//Code to generate videolink for website  from mobileUrlHd End
			
			//Code to get thumbnail URL start
			String thumbnailUrl="";
			try {
				thumbnailUrl = excelHelper.getVideoXml(vimeoId);
				video.setThumbnailUrl(thumbnailUrl);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
				return " <br> Error in api request "+video.getFileName()+" subject : "+video.getSubject();
			}
			//Code to get thumbnail URL end
			boolean videoContentUpdated;
			try {
				videoContentUpdated = dao.updateVideoContent(video);
			if (!videoContentUpdated) {
				return " <br> Error in updating "+video.getFileName()+" subject : "+video.getSubject();
			}
			} catch (Exception e) {
				  
				return " <br> Error in updating "+video.getFileName()+" subject : "+video.getSubject();
			}
			
			List<VideoContentAcadsBean> relatedTopics = dao.getAllVideoSubTopicsList(video.getId());
			String topicErrors="";
			for(VideoContentAcadsBean topic : relatedTopics) {
				topic.setThumbnailUrl(video.getThumbnailUrl());
				
				boolean topicUpdated;
				try {
					topicUpdated = dao.updateVideoContent(video);
				if (!topicUpdated) {
					topicErrors=topicErrors+" <br> Error in updating topic : "+topic.getFileName()+" of video "+video.getFileName()+" subject : "+video.getSubject();
				}
				} catch (Exception e) {
					  
					topicErrors=topicErrors+" <br> Error in updating topic : "+topic.getFileName()+" of video "+video.getFileName()+" subject : "+video.getSubject();
				}
			}
			
			return "Success"+relatedTopics;
		}
		
		/*Code for Making Video Content configurable start*/
		@RequestMapping(value = "/createMappingsOfVideoCententIdAndMasterKey", method = RequestMethod.GET)
		public String createMappingsOfVideoCententIdAndMasterKey(HttpServletRequest request, HttpServletResponse respnse, Model m) {


			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			TimeTableDAO tDao = (TimeTableDAO)act.getBean("timeTableDAO");

			List<VideoContentAcadsBean> videoContentIdAndMasterKeyList = new ArrayList<>();
			//get all video contents
			List<VideoContentAcadsBean> videoContentList = dao.getAllVideoContentList();
			
			//get all sessions details mapped with videocontentid
			List<SessionDayTimeAcadsBean> sessionsList = tDao.getAllSessionsMappedWithVideoContent();
			
			//get map of session id n masterkey
			Map<String,List<String>> sessionidAndMasterkeyMap = getSessionidAndMasterkeyMap(dao,sessionsList); 
			
			//create list of videocontentid and masterkey 
			for(VideoContentAcadsBean v : videoContentList) {
				List<String> masterKeysForVideoContentId = new ArrayList<>();
				if(sessionidAndMasterkeyMap.containsKey(v.getSessionId().toString())) {
					masterKeysForVideoContentId = sessionidAndMasterkeyMap.get(v.getSessionId().toString());
				}
				for(String s : masterKeysForVideoContentId) {
					VideoContentAcadsBean tempMappingBean = new VideoContentAcadsBean();
					tempMappingBean.setId(v.getId());
					tempMappingBean.setConsumerProgramStructureId(s);
					videoContentIdAndMasterKeyList.add(tempMappingBean);
				}
			}
			
			//batch update the list
			String batchUpdateErrorMessage = dao.batchInsertVideoCententIdAndMasterKeyMappings(videoContentIdAndMasterKeyList);
			
			if(StringUtils.isBlank(batchUpdateErrorMessage)) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Successfully created mapings");

			}else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Erro in creating mappings...");

			}
			
			return "studentPortalRediret";
		}
		
		private Map<String, List<String>> getSessionidAndMasterkeyMap(VideoContentDAO dao,
				List<SessionDayTimeAcadsBean> sessionsList) {
			checkForMasterkeyMap = new HashMap<>();
			Map<String, List<String>> sessionidAndMasterkeyMap = new HashMap<>();
			
			for(SessionDayTimeAcadsBean s : sessionsList) {
				List<String> masterKeys = new ArrayList<>();
				String corporateName = s.getCorporateName();
				String hasModuleId = s.getHasModuleId();
				String subject = s.getSubject();
				if("All".equalsIgnoreCase(corporateName) || corporateName == null) { //getCorporateName is null or All
					masterKeys.addAll(getMasterKeysToSessionIdBySubjectAndCorporateName(dao,subject,"Retail"));
					masterKeys.addAll(getMasterKeysToSessionIdBySubjectAndCorporateName(dao,subject,"Verizon"));
					masterKeys.addAll(getMasterKeysToSessionIdBySubjectAndCorporateName(dao,subject,"CIPLA"));
				}else if("".equalsIgnoreCase(corporateName.trim())) { //getCorporateName is blank ie Retail
					masterKeys.addAll(getMasterKeysToSessionIdBySubjectAndCorporateName(dao,subject,"Retail"));
				}else { //getCorporateName is Verizon,SAS or Diageo
					masterKeys.addAll(getMasterKeysToSessionIdBySubjectAndCorporateName(dao,subject,corporateName));
				}
				
				sessionidAndMasterkeyMap.put(s.getId(),masterKeys);
			}
			
			checkForMasterkeyMap = null;
			
			return sessionidAndMasterkeyMap;
		}

		private List<String> getMasterKeysToSessionIdBySubjectAndCorporateName(VideoContentDAO dao, String subject,String corporateName) {
			String checkKey = subject+"_"+corporateName;
			List<String> tempMasterKeys = new ArrayList<>();
			
			if(!checkForMasterkeyMap.containsKey(checkKey)) {
				tempMasterKeys = dao.getMasterKeysToSessionIdBySubjectAndCorporateName(subject,corporateName);
				checkForMasterkeyMap.put(checkKey,tempMasterKeys);
			}else {
				tempMasterKeys = checkForMasterkeyMap.get(checkKey);
			}
			
			return tempMasterKeys;
		}
		

		private List<VideoContentAcadsBean> setCountOfProgramsApplicableToEachVideoContent(VideoContentDAO dao, List<VideoContentAcadsBean> list) {
			try {
				int size = 0, i = 1;
				String contentIds = "";
				
				if(list != null ) {
					size = list.size();
				}
				
				for(VideoContentAcadsBean v : list) {
					if(i == size) {
						contentIds += v.getId()+"";
					}else {
						contentIds += v.getId()+",";
					}
					i++;
				}
				
				Map<Long,Integer> videoContentIdNCountOfProgramsApplicableToMap =
						dao.getVideoContentIdNCountOfProgramsApplicableToMap(contentIds);
				
				for(VideoContentAcadsBean t : list) {
					t.setCountOfProgramsApplicableTo(videoContentIdNCountOfProgramsApplicableToMap.get(t.getId()));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
			}
			
			return list;
		}

		@RequestMapping(value = "/getProgramsListForCommonVideoContent", method = {RequestMethod.POST})
		public ResponseEntity<List<VideoContentAcadsBean>> getProgramsListForCommonVideoContent(@RequestBody VideoContentAcadsBean bean) {
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			
			return new ResponseEntity<List<VideoContentAcadsBean>>(dao.getProgramsListForCommonVideoContent(bean.getId()),HttpStatus.OK);
		}
		
		@RequestMapping(value = "/editSingleVideoContentFromCommonSetup", method = RequestMethod.GET)
		public ModelAndView editSingleVideoContentFromCommonSetup(HttpServletRequest request,
															 HttpServletResponse respnse,
															 @ModelAttribute VideoContentAcadsBean content,
															 @RequestParam("contentId") Long contentId,
															 @RequestParam("consumerTypeId") String consumerTypeId,
															 @RequestParam("programStructureId") String programStructureId,
															 @RequestParam("programId") String programId,
															 @RequestParam("consumerProgramStructureId") String consumerProgramStructureId
															 
															 ) {
			ModelAndView modelnView = new ModelAndView("editVideoContentDetails");

			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			content = dao.getVideoContentById(contentId.intValue());

			content.setConsumerTypeId(consumerTypeId);
			content.setProgramStructureId(programStructureId);
			content.setProgramId(programId);
			content.setConsumerProgramStructureId(consumerProgramStructureId);;
			content.setEditSingleContentFromCommonSetup("true");
			
			
					
			
			modelnView.addObject("content",content);
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("subjectList", getSubjectList());
			modelnView.addObject("edit", "true");
			modelnView.addObject("consumerType", getConsumerTypeList());
			modelnView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
			modelnView.addObject("programIdNameMap", getProgramIdNameMap());
			
				

					if(content.getConsumerTypeId() != null) {
						if(content.getConsumerTypeId().split(",").length > 1) {
							content.setAllowedToUpdate("false");
						}else {
							content.setAllowedToUpdate("true");
						}
					}else {
						content.setAllowedToUpdate("false");
					}
					
				
				
				
				List<VideoContentAcadsBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(new Long(contentId));
				modelnView.addObject("videoSubTopicsList", videoSubTopicsList);
				request.setAttribute("videoSubTopicsList", videoSubTopicsList);
				modelnView.addObject("fileBean", new FileAcadsBean());
				modelnView.addObject("videoContent", content);
				modelnView.addObject("action", "Edit VideoContent");
				modelnView.addObject("fileBean", new FileAcadsBean());
			
			
			modelnView.addObject("consumerType", getConsumerTypeList());
			modelnView.addObject("programStructureIdNameMap", getProgramStructureIdNameMap());
			modelnView.addObject("programIdNameMap", getProgramIdNameMap());
			
			modelnView.addObject("videoSubTopic", new VideoContentAcadsBean());
			List<VideoContentAcadsBean> VideoContentsList = dao.getAllVideoContentList();
			modelnView.addObject("VideoContentsList", VideoContentsList);
			return modelnView;
		
		}
		

		@RequestMapping(value = "/updateSingleVideoContentFromCommonSetup", method = RequestMethod.POST)
		public ModelAndView updateSingleVideoContentFromCommonSetup(@ModelAttribute VideoContentAcadsBean videoContent, 
											  HttpServletRequest request,
											  HttpServletResponse response) {
			
			Long contentId = videoContent.getId();
			String consumerTypeId = videoContent.getConsumerTypeId();
			String programStructureId = videoContent.getProgramStructureId();
			String programId = videoContent.getConsumerTypeId();
			String consumerProgramStructureId = videoContent.getConsumerProgramStructureId();
			
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			String userId = (String) request.getSession().getAttribute("userId_acads");
			

			videoContent.setCreatedBy(userId);
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
			String strDate = formatter.format(date);
			videoContent.setAddedOn(strDate);
			Long oldId = videoContent.getId();

			long key=0;
			try {
				key = dao.saveVideoContent(videoContent);

				videoContent.setId(key);
			
			if(key == 0) {
				setError(request,"Error in saveVideoContent  ");
				return editSingleVideoContentFromCommonSetup(request, response, videoContent, contentId, consumerTypeId, programStructureId, programId, consumerProgramStructureId);
			}
			} catch (Exception e) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error Occured while saving the Video Content.");
				  
				return editSingleVideoContentFromCommonSetup(request, response, videoContent, contentId, consumerTypeId, programStructureId, programId, consumerProgramStructureId);
			}
			
			setSuccess(request, "VideoContent has been saved successfully");
			
			List<VideoContentAcadsBean> videoContentIdAndMasterKeyList = new ArrayList<>();
			videoContentIdAndMasterKeyList.add(videoContent);
			String createMapping = dao.batchInsertVideoCententIdAndMasterKeyMappings(videoContentIdAndMasterKeyList);
			
			if(!StringUtils.isBlank(createMapping)) {
				setError(request,"Error in createMappings  ");
				int deletedRow;
				try {
					deletedRow = dao.deleteVideoContent((int)key);
				
				if (deletedRow == 0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error Occured while deleting the Video Content.");
				}
				} catch (Exception e) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error Occured while deleting the Video Content.");
					  
				}
				return editSingleVideoContentFromCommonSetup(request, response, videoContent, contentId, consumerTypeId, programStructureId, programId, consumerProgramStructureId);
			}
			
			int deleteOldRows = dao.deleteIdNConsumerProgramStructureIdMapping(oldId,consumerProgramStructureId);
			if(deleteOldRows == 0) {
				setError(request,"Error in deleting old mapping  ");
				int deletedRow;
				try {
					deletedRow = dao.deleteVideoContent((int)key);
				
				if (deletedRow == 0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error Occured while deleting the Video Content.");
				}
				} catch (Exception e) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error Occured while deleting the Video Content.");
					  
				}
				return editSingleVideoContentFromCommonSetup(request, response, videoContent, contentId, consumerTypeId, programStructureId, programId, consumerProgramStructureId);
			}
			
			return editSingleVideoContentFromCommonSetup(request, response, videoContent, contentId, consumerTypeId, programStructureId, programId, consumerProgramStructureId);
				
		}
		/*Code for Making Video Content configurable End*/
		
//		to be deleted, api shifted to rest controller
		/*getPostIdForVideo Start*/
//		@CrossOrigin(origins = "*", allowedHeaders = "*")
//		@RequestMapping(value = "/m/getPostIdByVideoId", method = RequestMethod.POST, produces="application/json")
//		public ResponseEntity<VideoContentBean> getPostIdByVideoId(
//				@RequestBody VideoContentBean bean, HttpServletRequest request) {
//				
//			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//			VideoContentBean videoContent = new VideoContentBean();
//			try {
//				Long post_id = dao.getPostIdByVideoId(bean.getId());
//
//				if(StringUtils.isNumeric(post_id+"")){
//					videoContent.setPost_id(post_id);
//				}
//			} catch (NumberFormatException e) {
//				  
//				
//			}
//			
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json"); 
//			
//			return new ResponseEntity<VideoContentBean>(videoContent,headers,HttpStatus.OK);
//		}
		/*getPostIdForVideo end*/
		
//		@RequestMapping(value = "/startSessionRecordingScheduler", method = RequestMethod.GET, produces="application/json")
//		public void downloadVideoFromURL() throws IOException {			
//			SessionRecordingScheduler srscheduler = new SessionRecordingScheduler();		
//			srscheduler.synchronizeRecordingUpload();
//		}		
//
//		@RequestMapping(value = "/startvimeoStatusCheckScheduler", method = RequestMethod.GET, produces="application/json")
//		public void vimeoStatusCheck() throws IOException {			
//			SessionRecordingScheduler srscheduler = new SessionRecordingScheduler();		
//			srscheduler.vimeoStatusCheck();
//		}
		
		public Long convertTimeInSeconds(String time) throws Exception {
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		    DateFormat sdf = new SimpleDateFormat("ss");	   
		    DateFormat mdf = new SimpleDateFormat("mm");	 
		    DateFormat HHdf = new SimpleDateFormat("HH");	     


		    Long seconds = null;
		    Long minutes = null;
		    Long hours = null;
		    Long totalSeconds = null;
			Date d1 = null;
			
			d1 = format.parse(time);
			seconds = Long.parseLong(sdf.format(d1));	
			minutes = Long.parseLong(mdf.format(d1));	
			hours = Long.parseLong(HHdf.format(d1));	
			totalSeconds = seconds + (60 * minutes) + (3600 * hours);
			return totalSeconds;
		}		

		public String convertTime(String input) {
			
		      DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		     
		      DateFormat outputformat = new SimpleDateFormat("HH:mm:ss");
		      Date date = null;
		      String output = null;
		      try{
		    	 date= df.parse(input);	   
		    	 output = outputformat.format(date);	  
		    	 
		      }catch(Exception pe){
		           
		       }
			return output;
		}
		
		public String getDuration(String from,String to) {
			
			SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		    DateFormat df = new SimpleDateFormat("SSS");	     
		    Date date = null;
		    String output = null;
			Date d1 = null;
			Date d2 = null;
			try {
				d1 = format.parse(from);
				d2 = format.parse(to);
				//in milliseconds
				long diff = d2.getTime() - d1.getTime();			
				 date= df.parse(Long.toString(diff));			   
		    	 output = format.format(date);
		    	 
			}catch(Exception e) {
				  
			}
			return output;
		}
		private String getVideoURL(String vimeoLink, String from) {
			
			
			    DateFormat df = new SimpleDateFormat("HH:mm:ss");
				  DateFormat hhformat = new SimpleDateFormat("HH");
				  DateFormat mmformat = new SimpleDateFormat("mm");
				  DateFormat ssformat = new SimpleDateFormat("ss");
				
				  Date date = null;
				  String hhoutput = null;
				  String mmoutput = null;
				  String ssoutput = null;
				
				  try{
					 date= df.parse(from);	   
					 hhoutput = hhformat.format(date);	  
					 mmoutput = mmformat.format(date);	  
				     ssoutput = ssformat.format(date);	  
					 
				  }catch(Exception pe){
				       
				   }
				
					String videoLink = vimeoLink + "#t="+hhoutput+"h"+mmoutput+"m"+ssoutput+"s";
				
					return videoLink;
					
			}
		
//		to be deleted, api shifted to rest controller
//		@CrossOrigin(origins = "*", allowedHeaders = "*")
//		@RequestMapping(value = "/m/getVideoSubTopics", method = RequestMethod.GET, produces="application/json ; charset=UTF-8")
//		public ResponseEntity<HashMap<String, List<VideoContentBean>>> getVideoSubTopics(@RequestParam("id") String idString,@RequestParam(value = "pageNo", required=false) Integer pageNo, HttpServletRequest request) {
//			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//			HttpDownloadUtilityHelper hdHelper = new HttpDownloadUtilityHelper();
//			List <TextTrackImplLine> trackDataList = new ArrayList <TextTrackImplLine>();
//			List<VideoContentBean> videoSubTopicsList = new ArrayList<VideoContentBean>();
//			List<VideoContentBean> newVideoSubTopicsList = new ArrayList<VideoContentBean>();
//			String transcriptContent = null;
//			int id = 0;
//			VideoContentBean videoContent = null;
//			int sizePerPage=20;
//			if(pageNo == null) {
//				pageNo = 0;				
//			}
//			
//			try {
//				id = Integer.parseInt(idString);
//				videoContent = dao.getVideoContentById(id);
//			} catch (NumberFormatException e) {
//				  
//				
//			}
//			if(videoContent != null) {
//			String transcriptDwnldUrl = videoContent.getVideoTranscriptUrl();		//for Subtopics
//			String videoURL = videoContent.getVideoLink();
//			if(transcriptDwnldUrl != null) {	
//				try {					
//					transcriptContent = hdHelper.getContentFromTranscriptUrl(transcriptDwnldUrl);// get content from transcript download url		
//					if(transcriptContent != null) {
//						Reader transcriptContentReader = new StringReader(transcriptContent);
//						BufferedReader transcriptContentReaderBr = new BufferedReader(transcriptContentReader);			
//						trackDataList =  WebVttParser.parse(transcriptContentReaderBr);	//extract transcriptContent in list		
//						
//						if(trackDataList != null) {
//							 for (TextTrackImplLine track: trackDataList) {		
//								 VideoContentBean vcb = new VideoContentBean();
//								 String videoLink = getVideoURL(videoURL,track.getStartTime());		
//								 vcb.setStartTime(convertTime(track.getStartTime()));
//								 vcb.setEndTime(convertTime(track.getEndTime()));
//								 vcb.setDuration(getDuration(track.getStartTime(),track.getEndTime()));
//								 vcb.setFileName(track.getText());		
//								 vcb.setKeywords(track.getText());
//								 vcb.setStartTimeInSeconds(convertTimeInSeconds(track.getStartTime()));
//								 vcb.setEndTimeInSeconds(convertTimeInSeconds(track.getEndTime()));
//								 vcb.setVideoLink(videoLink);
//								 videoSubTopicsList.add(vcb);
//							 }					
//						}
//					}
//				}catch(Exception e) {
//					  
//				}
//			}
//			
//		
////			List<VideoContentBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(new Long(id));
//			
//			int videoSubTopicsListSize = videoSubTopicsList.size();	
//			int from = 0;
//			int to = videoSubTopicsList.size();			
//			if(pageNo != 0) {
//				from = Math.max(0,(pageNo-1)*sizePerPage); 		
//				if(from > videoSubTopicsListSize) {
//					from = videoSubTopicsListSize;
//				}
//				to =  Math.min(videoSubTopicsList.size(),pageNo*sizePerPage);
//				
//			}			
//			newVideoSubTopicsList = videoSubTopicsList.subList(from,to);	 
//			}
//			HashMap<String, List<VideoContentBean>> response = new HashMap<String, List<VideoContentBean>>();
//
//			response.put("videoSubTopicsList",newVideoSubTopicsList);
//			
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json"); 
//			
//			return new ResponseEntity<HashMap<String, List<VideoContentBean>>>(response,headers,HttpStatus.OK);
//		}
		
		@RequestMapping(value = "/searchSessionRecordingForm", method = RequestMethod.GET)
		public String searchSessionRecordingForm(Model m) {
			
			VideoContentAcadsBean videoContentBean = new VideoContentAcadsBean();
			m.addAttribute("videoContentBean", videoContentBean);
			m.addAttribute("yearList", ACAD_YEAR_LIST);
			m.addAttribute("monthList", ACAD_MONTH_LIST);
			
			return "searchSessionRecording";
		}
		
		
		@RequestMapping(value = "/searchSessionRecording", method = RequestMethod.POST)
		public ModelAndView searchSessionRecording(HttpServletRequest request, HttpServletResponse response, @ModelAttribute VideoContentAcadsBean videoContentBean) {
			
			logger.info("Entering searchSessionRecording() method of Video Content Controller");
			
			if(!checkSession(request, response)){
				return new ModelAndView("studentPortalRediret");
			}
			
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			ModelAndView modelnView = new ModelAndView("searchSessionRecording");
			
			try {
//				ArrayList<VideoContentAcadsBean> sessionRecordingList = dao.getAllSessionRecording(videoContentBean);
				List<VideoContentAcadsBean> sessionRecordingList = videoContentService.getAllSessionRecording(videoContentBean);
				if (sessionRecordingList.size() > 0) {
					modelnView.addObject("recordingListSize",sessionRecordingList.size());
					request.setAttribute("success","true");
					request.setAttribute("successMessage","Report generated successfully. Please click link below to download excel.");
				}else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No records found.");
				}
				request.getSession().setAttribute("sessionRecordingList", sessionRecordingList);
				
			} catch (Exception e) {
				  
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in generating Report.");
			}
			modelnView.addObject("videoContentBean", videoContentBean);
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("monthList", ACAD_MONTH_LIST);
			
			logger.info("Exiting searchSessionRecording() method of Video Content Controller");
			
			return modelnView;
		}
		
		@SuppressWarnings("unchecked")
		@RequestMapping(value = "/downloadSessionVideoReport", method = RequestMethod.GET)
		public ModelAndView downloadSessionVideoReport(HttpServletRequest request, HttpServletResponse response) {
			
			logger.info("Entering downloadSessionVideoReport() method of Video Content Controller");
			
			if(!checkSession(request, response)){
				return new ModelAndView("studentPortalRediret");
			}
			
			ArrayList<VideoContentAcadsBean> sessionRecordingList = (ArrayList<VideoContentAcadsBean>) request.getSession().getAttribute("sessionRecordingList");
			
			logger.info("Exiting downloadSessionVideoReport() method of Video Content Controller");
			
			return new ModelAndView("sessionVideoReportExcelView", "sessionRecordingList",sessionRecordingList);
		}
		
		@RequestMapping(value = "/updateVimeoDuration")
		public void updateVimeoDuration(@RequestParam("year") String year, @RequestParam("month") String month) {
			videoContentService.updateVimeoDurationService(year,month);
		}
		
//		to be deleted, api shifted to rest controller
//		@RequestMapping(value = "/m/getSessionsForLead", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//		public ResponseEntity<ArrayList<VideoContentBean>> getSessionsForLead(HttpServletRequest request, @RequestBody VideoContentBean bean) throws Exception {
//
//			HttpHeaders headers = new HttpHeaders();
//			headers.add("Content-Type", "application/json");
//			LeadDAO leadDAO = (LeadDAO) act.getBean("leadDAO");
//			ArrayList<VideoContentBean> response = new ArrayList<>();
//			
//			if(!StringUtils.isBlank(bean.getSubject())) {
//				response = leadDAO.getSubejctViseSessionForLead(bean);
//			}else {
//				response = leadDAO.getAllSessionForLead();
//			}
//
//			return new ResponseEntity<>(response,headers, HttpStatus.OK);
//
//		}
	
//		@RequestMapping(value = "/student/videosHome", method = RequestMethod.GET)
//		public ModelAndView getVideoHomePageNew(HttpServletRequest request,HttpServletResponse response, @RequestParam("academicCycle") String academicCycle) {
//		
//			if(!checkSession(request, response)){
//				return new ModelAndView("studentPortalRediret");
//			}
//			
//			ModelAndView modelAndView = new ModelAndView("videoHome");
//			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//			StudentBean student = (StudentBean)request.getSession().getAttribute("student_acads");
//			
//			LeadDAO leadDAO = (LeadDAO) act.getBean("leadDAO");
//			ArrayList<VideoContentBean> sessionForLeads = leadDAO.getSessionForLeads();
//			
//			ArrayList<String> allsubjects = applicableSubjectsForStudent(request);
//			
//			//Commented By Siddheshwar_Khanse
//			/*ArrayList<ConsumerProgramStructure> programSemSubjectIdWithSubject = dao.getProgramSemSubjectId(allsubjects, student.getConsumerProgramStructureId());
//			ArrayList<String> programSemSubjectIds = new ArrayList<String>();
//			for (ConsumerProgramStructure bean : programSemSubjectIdWithSubject) {
//				programSemSubjectIds.add(bean.getProgramSemSubjectId());
//			}*/
//			
//			//Get subject code id's based on the applicable subject of a student  
//			List<ConsumerProgramStructure> subjectCodeIdWithSubject=videoContentService.fetchSubjectCodeIdsByApplicableSub(allsubjects, 
//																					student.getConsumerProgramStructureId());
//			
//			//Preparing List of subjectCodeIds
//			List<String> subjectCodeIds = new ArrayList<String>();
//			for (ConsumerProgramStructure bean : subjectCodeIdWithSubject) {
//				subjectCodeIds.add(bean.getSubjectCodeId());
//			}
//			
//			int pageNo;
//			
//			try {
//				pageNo = Integer.parseInt(request.getParameter("pageNo"));
//				if (pageNo < 1) {
//					pageNo = 1;
//				}
//			} catch (NumberFormatException e) {
//				pageNo = 1;
//				  
//			}
//			
//			ArrayList<String> academicCycleList =new ArrayList<String>();
//			academicCycleList=dao.getAcademicCycleList();
//			ArrayList<String> academicCycleListForDb =new ArrayList<String>();
//			
//			if(academicCycle==null || "".equals(academicCycle) || "All".equals(academicCycle)){
//				academicCycle="All";
//				academicCycleListForDb.addAll(academicCycleList);
//			} else{
//				academicCycleListForDb.add(academicCycle);
//			}
//			
//			List<String> commonSubjects =  getCommonSubjects(request);
//			
//			//Commented By Siddheshwar_Khanse
//		    /*Page<VideoContentBean> page = dao.getVideoContentPageNew(pageNo, pageSize, programSemSubjectIds, academicCycleListForDb, student, commonSubjects);
//			List<VideoContentBean> videoContentListPage = page.getPageItems();*/
//			
//			//Fetching applicable Session Recordings based on subjectCodeIDs,cycle,program and commonSubjects 
//			Page<VideoContentBean> page = videoContentService.fetchSessionVideoBySubjectCodeId(pageNo, pageSize, subjectCodeIds, 
//																		academicCycleListForDb, student.getProgram(), commonSubjects);
//			List<VideoContentBean> videoContentListPage = page.getPageItems();
//			
//			modelAndView.addObject("academicCycleList", academicCycleList);
//			request.getSession().setAttribute("academicCycleList", academicCycleList);
//			
//			modelAndView.addObject("academicCycle", academicCycle);
//			modelAndView.addObject("page", page);
//			modelAndView.addObject("rowCount", page.getRowCount());
//			
//			modelAndView.addObject("allsubjects", allsubjects);
//			request.getSession().setAttribute("allsubjects", allsubjects);
//			
//			//Commented By Siddheshwar_Khanse
//		    /*modelAndView.addObject("programSemSubjectIdWithSubject", programSemSubjectIdWithSubject);
//		    request.getSession().setAttribute("programSemSubjectIdWithSubject", programSemSubjectIdWithSubject);*/
//			
//			//Set subjectCodeIdWithSubject in session and modelAndView
//			modelAndView.addObject("subjectCodeIdWithSubject", subjectCodeIdWithSubject);
//			request.getSession().setAttribute("subjectCodeIdWithSubject", subjectCodeIdWithSubject);
//			
//			modelAndView.addObject("selectedSubject", "All");
//			
//			modelAndView.addObject("commonSubjects", getCommonSubjects(request));
//			request.getSession().setAttribute("commonSubjects", getCommonSubjects(request));
//			
//			//for batch track filter
//			List<String> tracks=dao.getBatchTracks();
//			modelAndView.addObject("allBatchTracks", tracks);
//			request.getSession().setAttribute("allBatchTracks", tracks);
//			modelAndView.addObject("selectedBatch", "All");
//			
//			//Faculty List
//			List<FacultyBean> facultyList= dao.getFacultiesForSubjects(allsubjects);
//			modelAndView.addObject("facultyList",facultyList);
//			request.getSession().setAttribute("facultyList", facultyList);
//			FacultyBean tempFaculty=new FacultyBean();
//			tempFaculty.setFacultyId("All");
//			modelAndView.addObject("selectedFaculty",tempFaculty);
//			
//			modelAndView.addObject("searchItem","");
//			modelAndView.addObject("showPagination", true);
//			modelAndView.addObject("VideoContentsList", videoContentListPage);
//			modelAndView.addObject("subjectVideosMap", null);
//			
//			request.setAttribute("VideoContentsList", videoContentListPage);
//			request.setAttribute("subjectVideosMap", null);
//			request.setAttribute("subjectList", getSubjectList());
//			request.setAttribute("sessionForLeads", sessionForLeads);
//			return modelAndView;
//		
//		}
		
		public List<String> getCommonSubjects(HttpServletRequest request) {
					
			String sapId = (String)request.getSession().getAttribute("userId_acads");
			VideoContentDAO vDao = (VideoContentDAO) act.getBean("videoContentDAO");
			StudentAcadsBean studentRegistrationData = vDao.getStudentsMostRecentRegistrationData(sapId);
			
			List<String> commonSubjectList = new ArrayList<String>();
			commonSubjectList.add("Guest Session: GST by CA. Bimal Jain");
			commonSubjectList.add("Assignment");
			
			if("1".equals(studentRegistrationData.getSem())){
				commonSubjectList.add("Orientation");
			}
			
			//Commented by Siddheshwar_K because 'Project' gets added in subjects list with PSS
			/*if("4".equals(studentRegistrationData.getSem())){
				commonSubjectList.add("Project Preparation Session");
			}*/
			return commonSubjectList;
		}
		
		@GetMapping("/transferData")
		@Produces("application/json")
		public ResponseEntity<String> transferData() {
			String year = "2021";
			String month = "Jan";
			String result = null;
			
			try {
				//Transferring Jul2020 cycle data
				result = videoContentService.dataTransferForNrmlStudent(year, month) ;
				
			} catch (ParseException pe) {
				
				result = pe.getMessage();
			}
			catch (Exception e) {
				  
				result = e.getMessage();
			}
			//return the result
			return new ResponseEntity<String>(result,HttpStatus.OK);
		}
		
		
		@GetMapping("/updateDownloadVideoUrls")
		public ResponseEntity<String> updateBlankDownloadVideoUrls() {
			String result = null;
			
			try {
				//Update download video urls.
				result = sessionRecordingScheduler.updateBlankDownloadVideoUrls();
			}
			catch (Exception e) {
				result = e.getMessage();
			}
			//return the result
			return new ResponseEntity<String>(result,HttpStatus.OK);
		}
		
		@GetMapping("/createVideoContentEntryForMasterKey")
		public ResponseEntity<String> updateVideoContentForNewMasterKey(@RequestParam("oldConsumerProgramStructureId") String oldConsumerProgramStructureId,
				@RequestParam("newConsumerProgramStructureId") String newConsumerProgramStructureId,
				@RequestParam("acadYear") String acadYear,
				@RequestParam("acadMonth") String acadMonth
				) {
			String result = null;
			
			try {
				result = videoContentService.createVideoContentForNewMasterKey(oldConsumerProgramStructureId,newConsumerProgramStructureId,acadYear, acadMonth);
			}
			catch (Exception e) {
				result = e.getMessage();
			}
			//return the result
			return new ResponseEntity<String>(result,HttpStatus.OK);
		}
		
		@RequestMapping(value = "/manualRecordingUploadReportForm", method = { RequestMethod.GET, RequestMethod.POST })
		public String manualRecordingUploadReportForm(HttpServletRequest request, HttpServletResponse response, Model m) {

			try {
				Map<String, String> subjectCodeMapWithId = new HashMap<>();
				subjectCodeMapWithId = getsubjectCodeMapWithId();
				
				VideoContentAcadsBean videoContentBean = new VideoContentAcadsBean();
				m.addAttribute("videoContentBean", videoContentBean);
				m.addAttribute("yearList", ACAD_YEAR_LIST);
				m.addAttribute("monthList", ACAD_MONTH_LIST);
				m.addAttribute("subjectCodeMapWithId", subjectCodeMapWithId);
				
				return "manualRecordingUploadReport";
			} catch (Exception e) {
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
			return null;
		}
		
		@RequestMapping(value = "/manualRecordingUploadReport", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView manualRecordingUploadReport(HttpServletRequest request, HttpServletResponse response, @ModelAttribute VideoContentAcadsBean videoContentBean) {

			List<VideoContentAcadsBean> manualRecordingReportList = new ArrayList<>();			
			ModelAndView modelnView = new ModelAndView("manualRecordingUploadReport");
			
			Map<String, String> subjectCodeMapWithId = new HashMap<>();
			subjectCodeMapWithId = getsubjectCodeMapWithId();
						
			try {
				manualRecordingReportList = videoContentService.getManualRecordingUploadReport(videoContentBean, subjectCodeMapWithId);
				
				if(!manualRecordingReportList.isEmpty()) {
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", "Report generated successfully. Please click link below to download excel.");
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No records found.");
				}
				
				modelnView.addObject("manualRecordingUploadReportListSize", manualRecordingReportList.size());
				request.getSession().setAttribute("manualRecordingUploadReportList", manualRecordingReportList);
				modelnView.addObject("manualRecordingUploadReportList", manualRecordingReportList);

			} catch (Exception e) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in generating Report.");
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
			
			modelnView.addObject("videoContentBean", videoContentBean);
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("monthList", ACAD_MONTH_LIST);
			modelnView.addObject("subjectCodeMapWithId", subjectCodeMapWithId);
			
			return modelnView;
		}
		
		@SuppressWarnings("unchecked")
		@RequestMapping(value = "/downloadManualRecordingExcel", method = RequestMethod.GET)
		public ModelAndView downloadManualRecordingExcel(HttpServletRequest request, HttpServletResponse response) {			
			
			List<VideoContentAcadsBean> videoContentAcadsBean = new ArrayList<>();
			try {			
				videoContentAcadsBean = (List<VideoContentAcadsBean>) request.getSession().getAttribute("manualRecordingUploadReportList");
			} catch (Exception e) {
				logger.error(ExceptionUtils.getFullStackTrace(e));
			}
			return new ModelAndView("manualRecordingUploadReportExcelView", "manualRecordingUploadReport", videoContentAcadsBean);
		}
}