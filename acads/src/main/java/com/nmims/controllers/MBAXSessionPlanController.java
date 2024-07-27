package com.nmims.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AnnouncementAcadsBean;
import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.ContentFilesSetbean;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.Post;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionPlanAPIResponseBean;
import com.nmims.beans.SessionPlanBean;
import com.nmims.beans.SessionPlanGetLRAPIResponseBean;
import com.nmims.beans.SessionPlanGetVideosAPIResponseBean;
import com.nmims.beans.SessionPlanModuleBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.SyllabusBean;
import com.nmims.beans.TestAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.MBAXSessionPlanDAO;
import com.nmims.daos.SyllabusDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.FileUploadHelper;
import com.nmims.services.VideoContentService;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mbax/sp/a/admin")
public class MBAXSessionPlanController extends BaseController {

	@Value( "${CONTENT_PATH}" )
	private String CONTENT_PATH;
	@Autowired(required = false)
	ApplicationContext act;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST; 
	private ArrayList<String> subjectList = null;

	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Autowired
	SyllabusDAO syllabusDao;
	
	@Autowired
	private VideoContentService videoContentService;	
	
	@Autowired
	FileUploadHelper fileUploadHelper;

	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			this.subjectList = dao.getActiveSubjects();
		}

		return subjectList;
	}

	
	@RequestMapping(value = "/manageSessionPlan", method = RequestMethod.GET)
	public String manageSessionPlan(HttpServletRequest request, 
									HttpServletResponse response,
									Model m) {

		/*if(!checkSession(request, response)){
			return "studentPortalRediret";
		}*/
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		
		List<SessionPlanBean> sessionPlanList = dao.getAllSessionPlans();

		m.addAttribute("sessionPlanList", sessionPlanList);

		m.addAttribute("formBean", new SessionPlanBean());
		m.addAttribute("acadsYearList", ACAD_YEAR_LIST);
		m.addAttribute("acadsMonthList", ACAD_MONTH_LIST);
		m.addAttribute("consumerType", dao.getConsumerTypeBeanList());
		return "mbaxManageSessionPlan";
		
	}
	
	@RequestMapping(value = "/saveSessionPlan", method = RequestMethod.POST)
	public String saveSessionPlan(HttpServletRequest request, 
									HttpServletResponse response,
									Model m,
									@ModelAttribute SessionPlanBean formBean) {
		
		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}
		String userId = (String) request.getSession().getAttribute("userId_acads");
		
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		
		formBean.setCreatedBy(userId);
		formBean.setLastModifiedBy(userId);
		
		if(formBean.getId() == null) {
			//Save Session plan
			String saveSessionPlanMessage =  dao.saveSessionPlan(formBean);
			
			if(!StringUtils.isNumeric(saveSessionPlanMessage)) {

				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",saveSessionPlanMessage);
				return manageSessionPlan(request,response,m);
			}
			
			formBean.setId(Long.parseLong(saveSessionPlanMessage));
			
			//create mappings for program sem subject ids and session plan id 

			String createMappings = create_SessionPlanId_TimeBoundId_Mappings(dao, formBean);
			
			if(!StringUtils.isBlank(createMappings)) {
				
				dao.deleteSessionPlanById(formBean.getId());
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",createMappings);
				return manageSessionPlan(request,response,m);
			}
			
			String createGenericModuleErrorMessage = createGenericModule(dao,Long.parseLong(saveSessionPlanMessage));
			
			if(!StringUtils.isNumeric(createGenericModuleErrorMessage)) {

				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Error in creating Generic Module for the session plan, Kindly create a generic module manually. Error : "+createGenericModuleErrorMessage);
			}
			
			request.setAttribute("success", "true");
			request.setAttribute("successMessage","Session Plan Saved Successfully!");
			
		}else {
			
			// check if program config have been changed
			String isProgramChanged = checkIsProgramChanged(dao,formBean);
			
			if(!"true".equalsIgnoreCase(isProgramChanged) && !"false".equalsIgnoreCase(isProgramChanged)) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Unable to update session plan, "+isProgramChanged);
				return editSessionPlan(request,response,m,formBean.getId());
			}
			
			// Update session plan
			String updated = dao.updateSessionPlan(formBean); 
			if(!StringUtils.isBlank(updated)) {
				
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Unable to update session plan, "+updated);
				return editSessionPlan(request,response,m,formBean.getId());
			}
			
			if("true".equalsIgnoreCase(isProgramChanged) ) {
				
				//delete old mapings
				int deleted = dao.deleteSessionPlanIdTimeboundIdMappingBySessionPlanId(formBean.getId());
				if(deleted < 1) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage","Unable to update session plan,Try Again "+deleted);
					return editSessionPlan(request,response,m,formBean.getId());
				}
				
				//create new mapings
				String createMappings = create_SessionPlanId_TimeBoundId_Mappings(dao, formBean);
				
				if(!StringUtils.isBlank(createMappings)) {
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage",createMappings);
					return editSessionPlan(request,response,m,formBean.getId());
				}
			}
			

			request.setAttribute("success", "true");
			request.setAttribute("successMessage","Session Plan Updated Successfully!");
			return editSessionPlan(request,response,m,formBean.getId());
			
		}
		
		return manageSessionPlan(request,response,m);
	}
	
	
	private String createGenericModule(MBAXSessionPlanDAO dao, long sessionPlanId) {
		
		SessionPlanModuleBean bean = new SessionPlanModuleBean();
		bean.setSessionPlanId(sessionPlanId);
		bean.setSessionModuleNo( (long)0 );
		bean.setTopic("Generic Module For Session Plan ");
		bean.setOutcomes("Contains general data applicable for whole session plan. ");
		bean.setChapter("NA");
		bean.setPedagogicalTool("NA");
		bean.setCreatedBy("Automated");
		bean.setLastModifiedBy("Automated");
		
		return dao.saveSessionPlanModule(bean);
	}


	private String checkIsProgramChanged(MBAXSessionPlanDAO dao, SessionPlanBean formBean) {
		
		try {
			SessionPlanBean fromDB = dao.getSessionPlanById(formBean.getId());
			if( fromDB.getConsumerTypeId().equalsIgnoreCase(formBean.getConsumerTypeId())
				&& fromDB.getProgramStructureId().equalsIgnoreCase(formBean.getProgramStructureId())
				&& fromDB.getProgramId().equalsIgnoreCase(formBean.getProgramId())
				&& fromDB.getSubject().equalsIgnoreCase(formBean.getSubject())
			){
				return "false";
			}else {
				return "true";
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
			return e.getMessage();
		}
		
		
	}
	
	private String create_SessionPlanId_ProgramSemSubjectId_Mappings(MBAXSessionPlanDAO dao,
																	 SessionPlanBean bean) {
		
		ArrayList<String> programSemSubjectIds =
					dao.getProgramSemSubjectIdsBySubjectNProgramConfig(bean.getProgramId()
																 	   ,bean.getProgramStructureId()
																 	   ,bean.getConsumerTypeId()
																 	   ,bean.getSubject());
			if(programSemSubjectIds.isEmpty()) {
				return "Error in getting programSemSubjectIds, try again.";
			}
			
			
			
			return dao.batchInsertSessionPlanIdProgramSemSubjectIdMappings(bean,programSemSubjectIds); 
	}
	
	private String create_SessionPlanId_TimeBoundId_Mappings(MBAXSessionPlanDAO dao,
			 												SessionPlanBean bean) {

		//1. Get programSemSubjectIds
		ArrayList<String> programSemSubjectIds =
		dao.getProgramSemSubjectIdsBySubjectNProgramConfig(bean.getProgramId()
				 	   ,bean.getProgramStructureId()
				 	   ,bean.getConsumerTypeId()
				 	   ,bean.getSubject());
		if(programSemSubjectIds.isEmpty()) {
		return "Error in getting programSemSubjectIds, try again.";
		}
		
		//2. get timeboundIds by programSemSubjectIds
		List<Long> timeboundIds = dao.getTimeboundIdsByProgramSemSubjectIdsMBAX(programSemSubjectIds,bean.getReferenceId());
		if(timeboundIds.isEmpty()) {
			return "Error in getting timeboundIds,  try again .";
			}
		
		//3. insert mappings in table.
		return dao.batchInsertSessionPlanIdTimeBoundIdMappings(bean,timeboundIds); 
		}
	
	@RequestMapping(value = "/editSessionPlan", method = RequestMethod.GET)
	public String editSessionPlan(HttpServletRequest request, 
									HttpServletResponse response,
									Model m,
									@RequestParam Long id ) {

		/*if(!checkSession(request, response)){
			return "studentPortalRediret";
		}*/

		SessionDayTimeAcadsBean session = new SessionDayTimeAcadsBean();

		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		
		SessionPlanBean bean = dao.getSessionPlanById(id);
		
		if(bean == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error in getting SessionPlan Details.");
			return manageSessionPlan(request,response,m);
		
		}
		
		List<SessionPlanModuleBean> sessionPlanModulesList = dao.getAllSessionPlanModulesListBySessionPlanId(id);
		m.addAttribute("sessionPlanModulesList", sessionPlanModulesList);
		
		Map<String,String> programStructureIdNameMap = dao.getProgramStructureIdNameMap();
		m.addAttribute("programStructureIdNameMap", programStructureIdNameMap);
		
		Map<String,String> programIdNameMap = dao.getProgramIdNameMap();
		m.addAttribute("programIdNameMap", programIdNameMap);
		 
		ArrayList<SyllabusBean> syllabus = new ArrayList<SyllabusBean>();
		
		try {
			//syllabus = syllabusDao.getSyllabusForSessionPlanId(id);
		} catch (Exception e) {
			  
		}
			
		m.addAttribute("formBean", bean);
		m.addAttribute("module", new SessionPlanModuleBean());
		m.addAttribute("acadsYearList", ACAD_YEAR_LIST);
		m.addAttribute("acadsMonthList", ACAD_MONTH_LIST);
		m.addAttribute("consumerType", dao.getConsumerTypeList());
		m.addAttribute("session", session);
		m.addAttribute("testBean", new TestAcadsBean());
		m.addAttribute("syllabus", syllabus);
		
		return "editMBAXSessionPlan";
		
	}

	
	@RequestMapping(value = "/deleteSessionPlan", method = RequestMethod.GET)
	public String deleteSessionPlan(HttpServletRequest request, 
									HttpServletResponse response,
									Model m,
									@RequestParam Long id ) {

		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}
		
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		
		int deleted = dao.deleteSessionPlanById(id);
		if(deleted < 1) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Unable to delete session plan,Try Again "+deleted);
		}else {
		request.setAttribute("success", "true");
		request.setAttribute("successMessage"," Deleted session plan successfully.");
		}
		return manageSessionPlan(request, response, m);
	}
	
	@RequestMapping(value = "/deleteSessionPlanModule", method = RequestMethod.GET)
	public String deleteSessionPlanModule(HttpServletRequest request, 
									HttpServletResponse response,
									Model m,

									@RequestParam Long id
									) {

		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}
		
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		//ArrayList<String> sessionIdList = dao.selectSessionIdByModuleId(id);
		//int sessiondeleted = dao.deleteSessionByModuleId(id);
		int deleted = dao.deleteSessionPlanModuleById(id);
		if(deleted < 1) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Unable to delete session plan,Try Again "+deleted);
		}else {
		request.setAttribute("success", "true");
		request.setAttribute("successMessage","Session Plan Module Deleted Successfully!");
		}
		return editSessionPlan(request,response,m,id);
	}

	@RequestMapping(value = "/saveSessionPlanModule", method = RequestMethod.POST)
	public String saveSessionPlanModule(HttpServletRequest request, 
									HttpServletResponse response,
									Model m,
									@ModelAttribute SessionPlanModuleBean module) {
		
		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}
		String userId = (String) request.getSession().getAttribute("userId_acads");
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		
		module.setCreatedBy(userId);
		module.setLastModifiedBy(userId);
		
		if(module.getId() == null) {
			//Save Session plan module
			String saveSessionPlanModuleMessage =  dao.saveSessionPlanModule(module);
			
			if(!StringUtils.isNumeric(saveSessionPlanModuleMessage)) {

				request.setAttribute("error", "true");
				request.setAttribute("errorMessage",saveSessionPlanModuleMessage);
				return editSessionPlan(request,response,m,module.getSessionPlanId());
			}
			

			module.setId(Long.parseLong(saveSessionPlanModuleMessage));		//sessionPlanModuleId
			ArrayList<String> chapterList = module.getChapters();
 			
			for(int i = 0; i<chapterList.size(); i++) {
				
				try {
					module.setSyllabusId(Long.parseLong(chapterList.get(i))); 	//syllabusId
					syllabusDao.saveSessionPlanModuleMapping(module);
				}catch (Exception e) {
					  
				}
			}
			
			request.setAttribute("success", "true");
			request.setAttribute("successMessage","Session Plan Module Saved Successfully!");
			
		}else {
			
			if(module.isCloneSyllabus()) {

				long id = module.getId();
				String primaryKey = "";
				ArrayList<SyllabusBean> syllabus = new ArrayList<SyllabusBean>();
				long  count = 1;
				try {
					syllabus = syllabusDao.getSyllabusForSessionPlanId(module.getId());
					for(SyllabusBean bean:syllabus) {
						
						module.setSessionPlanId(id);
						module.setSessionModuleNo(count);
						module.setChapter(bean.getChapter());
						module.setTopic(bean.getTopic());
						module.setOutcomes(bean.getOutcomes());
						module.setPedagogicalTool(bean.getPedagogicalTool());
						primaryKey =  dao.saveSessionPlanModule(module);
						module.setId(Long.parseLong(primaryKey));		//sessionPlanModuleId
						module.setSyllabusId(bean.getId());				//syllabusId
						module.setSubjectCodeId(syllabusDao.getSubjectCodeForMapping(module));
						syllabusDao.saveSessionPlanModuleMapping(module);
						count++;
					}
				} catch (Exception e) {
					  
				}
			}else {
				// Update session plan
				String updated = dao.updateSessionPlanModule(module); 
				
				if(!StringUtils.isBlank(updated)) {
					
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage","Unable to update session plan Module , "+updated);
					return manageSessionPlanModuleById(request, response, m, module.getId(),"edit");
				}else {

				try {
					syllabusDao.updateSyllabusSessionPlanModuleMapping(module);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					  
				}
				request.setAttribute("success", "true");
				request.setAttribute("successMessage","Session Plan Module Updated Successfully!");
				return manageSessionPlanModuleById(request, response, m, module.getId(),"edit");
				}
			}
		}

		return editSessionPlan(request,response,m,module.getSessionPlanId());
	}
	

	@RequestMapping(value = "/manageSessionPlanModuleById", method = RequestMethod.GET)
	public String manageSessionPlanModuleById(HttpServletRequest request, 
									HttpServletResponse response,
									Model m,
									@RequestParam Long id,
									@RequestParam String action) {

		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}
		
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		
		SessionPlanModuleBean bean = dao.getSessionPlanModuleById(id);
		ArrayList<SyllabusBean> syllabus = new ArrayList<SyllabusBean>();
		
		try {
			syllabus = syllabusDao.getSyllabusForSessionPlanModuleId(id);
		} catch (Exception e) {
			  
		}
		
		if(bean == null) {
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage","Error in getting SessionPlan Module Details.");
			return manageSessionPlan(request,response,m);
		}
		m.addAttribute("bean",bean);
		m.addAttribute("action",action);
		m.addAttribute("syllabus",syllabus);
		m.addAttribute("module",new SessionPlanModuleBean());
		
		return "manageSessionPlanModuleById";
		
	}
	@RequestMapping(value = "/uploadVideoContentForSessionPlanModuleForm", method = RequestMethod.GET)
	public String uploadVideoContentForSessionPlanModuleForm(@RequestParam("sessionPlanModuleId") Long id,
														  HttpServletRequest request,
														  HttpServletResponse response,
														  Model m) {
		if(!checkSession(request, response)){
			return "studentPortalRediret";
		}
		TimeTableDAO sessionDao = (TimeTableDAO)act.getBean("timeTableDAO");
		MBAXSessionPlanDAO spDao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		SessionPlanModuleBean bean = spDao.getSessionPlanModuleById(id);
		
		m.addAttribute("fileBean", new FileAcadsBean());
		m.addAttribute("sessionDetails", "Add Session Recording for Session Plan Module  " );

		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> videoContentsList = spDao.getAllVideoContentListBySesionPlanModuleId(id);
		
		m.addAttribute("videoContentsList", videoContentsList);
		m.addAttribute("videoContent", new VideoContentAcadsBean());
		m.addAttribute("action", "Add VideoContent");
		m.addAttribute("bean", bean);

		return "uploadVideoContentForSessionPlanModuleForm";
	}
		
		//Sessions Batch upload Start for sessionplanmodule
		@RequestMapping(value = "/uploadVideoContentForSessionPlanModule", method = { RequestMethod.POST })
		public String uploadVideoContentForSessionPlanModule(HttpServletRequest request,	
													HttpServletResponse response,
													@ModelAttribute FileAcadsBean fileBean,
													Model m) {

			if(!checkSession(request, response)){
				return "studentPortalRediret";
			}
			ModelAndView modelAndView = new ModelAndView("videoContentDetails");
			m.addAttribute("fileBean", fileBean);

			String userId = (String) request.getSession().getAttribute("userId_acads");
			VideoContentDAO vcdao = (VideoContentDAO) act.getBean("videoContentDAO");
			Long sessionPlanModuleId = Long.parseLong(fileBean.getFileId()+"");
			MultipartFile file = fileBean.getFileData();
			if (file.isEmpty()) {// Check if File was attached
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Please Select File to Upload...");
				
				return uploadVideoContentForSessionPlanModuleForm(sessionPlanModuleId,
																  request,response,m);
				
			}
			
			if(!StringUtils.isNumeric(sessionPlanModuleId+"")) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Please Select SessionPlan Module to Upload...");
				
				return manageSessionPlan(request, response, m);
			}
			
			 TimeTableDAO tdao = (TimeTableDAO) act.getBean("timeTableDAO");
			 ArrayList<String> sessionIdList= new ArrayList<String>(); 
			try {
				 sessionIdList = tdao.getAllSessions();
			} catch (Exception e) {
				  
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
					return uploadVideoContentForSessionPlanModuleForm(sessionPlanModuleId, request,response,m);
					
				}

				ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
				
				for(VideoContentAcadsBean v :  videoContentList) {
					v.setSessionPlanModuleId(sessionPlanModuleId);
					v.setSubject(v.getSubject()+"_SesssionPlan_Video");
				}
				
				ArrayList<String> errorList = dao.batchUpdateVideoContent(videoContentList);

				if (errorList.size() == 0) {
					videoContentService.updateVimeoDurationService(CURRENT_ACAD_YEAR,CURRENT_ACAD_MONTH);
					request.setAttribute("success", "true");
					request.setAttribute("successMessage", videoContentList.size() + " rows out of "
							+ videoContentList.size() + " inserted successfully.");
					return uploadVideoContentForSessionPlanModuleForm(sessionPlanModuleId,
							  request,response,m);


					
				} else {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size()
							+ " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "
							+ errorList);
					return uploadVideoContentForSessionPlanModuleForm(sessionPlanModuleId,
							  request,response,m);


					

				}
			} catch (Exception e) {
				  
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in inserting rows.");
				return uploadVideoContentForSessionPlanModuleForm(sessionPlanModuleId,
						  request,response,m);


			}
		}
		
		//Sessions Batch upload End

	
	

		@RequestMapping(value = "/uploadLRContentForSessionPlanModule", method = RequestMethod.GET)
		public String uploadLRContentForSessionPlanModule(HttpServletRequest request, 
										HttpServletResponse response,
										Model m,
										@RequestParam Long id ) {

			if(!checkSession(request, response)){
				return "studentPortalRediret";
			}
			
			MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
			SessionPlanModuleBean bean = dao.getSessionPlanModuleById(id);
			
			if(bean == null) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Error in getting SessionPlan Module Details.");
				return manageSessionPlan(request,response,m);
			}

			SessionPlanBean sessionPlan = dao.getSessionPlanById(bean.getSessionPlanId());
			if(sessionPlan == null) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Error in getting SessionPlan Details.");
				return manageSessionPlan(request,response,m);
			}
			
			m.addAttribute("bean",bean);
			m.addAttribute("sessionPlan",sessionPlan);

			m.addAttribute("subjectList", getSubjectList());
			
			ContentFilesSetbean  filesSet = new ContentFilesSetbean();
			m.addAttribute("filesSet",filesSet);
			
			List<ContentAcadsBean> contentList = dao.getContentsForSessionPlanModule(id);

			m.addAttribute("contentList",contentList);
			
			return "uploadLRContentForSessionPlanModule";
		
		}
		

		@RequestMapping(value = "/uploadContentFilesForSessionModule",  method = {RequestMethod.POST})
		public String uploadContentFilesForSessionModule(HttpServletRequest request, 
				HttpServletResponse response, 
				@ModelAttribute ContentFilesSetbean filesSet,
				Model m){
			m.addAttribute("filesSet",filesSet);
			
			
			List<ContentAcadsBean> contentFiles = filesSet.getContentFiles();
			
			Long sessionPlanModuleId = filesSet.getId();
			
			if(!StringUtils.isNumeric(sessionPlanModuleId+"")) {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage","Kindly select Session Plan Module to  proceed.");
				return manageSessionPlan(request,response,m);
			}	
				
				
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			int successCount = 0;
			String fileNames = "";
			for (int i = 0; i < contentFiles.size(); i++) {

				ContentAcadsBean bean = contentFiles.get(i);

				String fileName = bean.getFileData().getOriginalFilename();  
				String contentName = bean.getName();
				String errorMessage = null;
				if(contentName == null || "".equals(contentName.trim())  ){
					//If no name mentioned for Content, then do not store in Database
					continue;
				}

				if(fileName == null || "".equals(fileName.trim()) ){
					//If no file is selected, do not upload any file
					continue;
				}else{
					//errorMessage = uploadContentFile(bean, filesSet.getSubject());
					errorMessage = fileUploadHelper.uploadContentFileOnS3(bean, filesSet.getSubject());
					
					if(!errorMessage.equals("success"))
						errorMessage = uploadContentFile(bean, filesSet.getSubject());
				}

				//Check if file saved to Disk successfully
				if(errorMessage.equals("success")){
					String userId = (String)request.getSession().getAttribute("userId_acads");
					bean.setCreatedBy(userId);
					bean.setLastModifiedBy(userId);
					bean.setSessionPlanModuleId(filesSet.getId());
					if(filesSet.getId()!=null) {
						MBAXSessionPlanDAO sDao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
						String moduleName = sDao.getSessionPlanModuleById(bean.getSessionPlanModuleId()).getTopic();
						bean.setSessionPlanModuleName(moduleName);
					}
					long contentId = dao.saveContentFileDetails(bean, filesSet.getSubject(), filesSet.getYear(), filesSet.getMonth());
					
					if(contentId < 1 ) {
						request.setAttribute("error", "true");
						request.setAttribute("errorMessage", "Error in file saving file details to DB, FileName : "+bean.getName()
											+ "<br> Files Uploaded successfully : File Names : "+fileNames);
						return uploadLRContentForSessionPlanModule(request, response, m, sessionPlanModuleId);
						
					}
					
					successCount++;
					fileNames = fileNames + " : " +bean.getName() ;

				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "Error in file Upload: "+errorMessage);
					return uploadLRContentForSessionPlanModule(request, response, m, sessionPlanModuleId);
				}
			}

			request.setAttribute("success","true");
			request.setAttribute("successMessage",successCount + " Files Uploaded successfully : File Names : "+fileNames);

			filesSet = new ContentFilesSetbean();
			m.addAttribute("filesSet",filesSet);
			
			return uploadLRContentForSessionPlanModule(request, response, m, sessionPlanModuleId);
		}

		private String uploadContentFile(ContentAcadsBean bean, String subject) {

			String errorMessage = "success";
			InputStream inputStream = null;   
			OutputStream outputStream = null;   

			CommonsMultipartFile file = bean.getFileData(); 
			String fileName = file.getOriginalFilename();   

			//Replace special characters in file
			fileName = fileName.replaceAll("'", "_");
			fileName = fileName.replaceAll(",", "_");
			fileName = fileName.replaceAll("&", "and");
			fileName = fileName.replaceAll(" ", "_");
			fileName = fileName.replaceAll(":", "_");
			fileName = fileName.replaceAll("'", "_");
			
			subject = subject.replaceAll(":", "_");
			subject = subject.replaceAll("'", "_");

			fileName = fileName.substring(0, fileName.lastIndexOf(".")) + "_" + RandomStringUtils.randomAlphanumeric(10) + fileName.substring(fileName.lastIndexOf("."), fileName.length());
			String tempFileNameLowerCase = fileName.toLowerCase();

			if(!(tempFileNameLowerCase.endsWith(".pdf")  || tempFileNameLowerCase.endsWith(".zip")  || tempFileNameLowerCase.endsWith(".rar")  || tempFileNameLowerCase.endsWith(".xls") || tempFileNameLowerCase.endsWith(".xlsx") || 
					tempFileNameLowerCase.endsWith(".ppt") || tempFileNameLowerCase.endsWith(".pptx") || tempFileNameLowerCase.endsWith(".doc") || tempFileNameLowerCase.endsWith(".docx") 
					|| tempFileNameLowerCase.endsWith(".flv") || tempFileNameLowerCase.endsWith(".mov") || tempFileNameLowerCase.endsWith(".mpeg") || tempFileNameLowerCase.endsWith(".mov") || tempFileNameLowerCase.endsWith(".avi") ) ){
				errorMessage = "File type not supported.";
				return errorMessage;
			}

			try {   
				inputStream = file.getInputStream();
				String extension = "."+FilenameUtils.getExtension(file.getOriginalFilename());
				long currentUnixTime = System.currentTimeMillis() / 1000L;
				fileName = currentUnixTime+RandomStringUtils.randomAlphanumeric(5)+extension;
				
				String filePath = CONTENT_PATH + subject + "/" +fileName;
				String previewPath = subject + "/" + fileName;
				//Check if Folder exists which is one folder per Subject 
				File folderPath = new File(CONTENT_PATH  + subject );
				if (!folderPath.exists()) {   
					folderPath.mkdirs();   
				}   

				File newFile = new File(filePath);   


				outputStream = new FileOutputStream(newFile);   
				int read = 0;   
				byte[] bytes = new byte[1024];   

				while ((read = inputStream.read(bytes)) != -1) {   
					outputStream.write(bytes, 0, read);   
				}
				bean.setFilePath(filePath);
				bean.setPreviewPath(previewPath);
				outputStream.close();
				inputStream.close();
			} catch (IOException e) {   
				errorMessage = "Error in uploading file for "+bean.getSubject() + " : "+ e.getMessage();
				     
			}   

			return errorMessage;
		}



}
