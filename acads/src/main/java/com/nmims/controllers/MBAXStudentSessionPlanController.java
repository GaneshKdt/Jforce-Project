package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nmims.beans.ContentAcadsBean;
import com.nmims.beans.Post;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionPlanAPIResponseBean;
import com.nmims.beans.SessionPlanBean;
import com.nmims.beans.SessionPlanGetLRAPIResponseBean;
import com.nmims.beans.SessionPlanGetVideosAPIResponseBean;
import com.nmims.beans.SessionPlanModuleBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.MBAXSessionPlanDAO;
import com.nmims.daos.TimeTableDAO;


@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mbax/sp/s")
public class MBAXStudentSessionPlanController {
	
	@Autowired(required = false)
	ApplicationContext act;
	
	@RequestMapping(value = "/api/getSessionPlanDetailsBySapidNMasterKey", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<SessionPlanAPIResponseBean> getSessionPlanDetailsBySapidNMasterKey(@RequestBody StudentAcadsBean student){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		SessionPlanAPIResponseBean responseBean = getSessionPlanDetailsResponseBeanBySapidNMasterKey(student.getSapid(),student.getSubject());
		
		return new ResponseEntity<>(responseBean,headers, HttpStatus.OK);	
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/api/getVideosBySessionPlanModuleId", method = RequestMethod.POST)
	public ResponseEntity<SessionPlanGetVideosAPIResponseBean> getVideoHomeContent(@RequestBody SessionPlanModuleBean bean ,
																	  @Context HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		SessionPlanGetVideosAPIResponseBean responseBean = getSessionPlanVideosByModuleId(bean.getId(), bean.getSapId());
		
		return new ResponseEntity<>(responseBean,headers, HttpStatus.OK);	
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/api/getLRBySessionPlanModuleId", method = RequestMethod.POST)
	public ResponseEntity<SessionPlanGetLRAPIResponseBean> getLRBySessionPlanModuleId(@RequestBody SessionPlanModuleBean bean ,
																	  @Context HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		SessionPlanGetLRAPIResponseBean responseBean = getSessionPlanLRByModuleId(bean.getId(), bean.getSapId());
		return new ResponseEntity<>(responseBean,headers, HttpStatus.OK);	
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/api/getSessionPlanDetailsByProgramSemSubjectId", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<SessionPlanAPIResponseBean> getSessionPlanDetailsByProgramSemSubjectId(@RequestBody SessionPlanBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		SessionPlanAPIResponseBean responseBean = getSessionPlanDetailsBeanByProgramSemSubjectId(bean.getProgramSemSubjectId());
		
		return new ResponseEntity<>(responseBean,headers, HttpStatus.OK);	
	}
	

	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/api/getSessionPlanDetailsByTimeboundId", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<SessionPlanAPIResponseBean> getSessionPlanDetailsByTimeboundId(@RequestBody SessionPlanBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		SessionPlanAPIResponseBean responseBean = getSessionPlanDetailsBeanByTimeboundId(bean.getTimeboundId());
		
		return new ResponseEntity<>(responseBean,headers, HttpStatus.OK);	
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/api/getSessionPlanDetailsBySapidAndTimeboundId", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<SessionPlanAPIResponseBean> getSessionPlanDetailsBySapidAndTimeboundId(@RequestBody SessionPlanBean bean){
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		SessionPlanAPIResponseBean responseBean = getSessionPlanDetailsBeanByTimeboundIdAndSapid(bean.getTimeboundId(), bean.getSapid());
		
		return new ResponseEntity<>(responseBean,headers, HttpStatus.OK);	
	}
	
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@RequestMapping(value = "/api/getPostDetailsByModuleId", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	public ResponseEntity<SessionPlanAPIResponseBean> getPostDetailsByModuleId(@RequestBody Post bean){
		
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		SessionPlanAPIResponseBean responseBean = new SessionPlanAPIResponseBean();
		List<Post> listOfPosts =  dao.getPostByModuleId(bean.getSession_plan_module_id());
		responseBean.setPosts(listOfPosts);
		
		return new ResponseEntity<>(responseBean,headers, HttpStatus.OK);	
	}
	
	private SessionPlanAPIResponseBean getSessionPlanDetailsResponseBeanBySapidNMasterKey(String sapid,
			String subject) {

		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		SessionPlanAPIResponseBean bean = new SessionPlanAPIResponseBean();
		SessionPlanBean sessionPlan = dao.getSessionPlanBySapidNMasterKey(sapid, subject);

		if (sessionPlan == null) {
			bean.setErrorMessage("Error getting session plan details, Try Again.");
			bean.setSessionPlan(new SessionPlanBean());
			return bean;
		} else {
			bean.setSessionPlan(sessionPlan);
		}

		List<SessionPlanModuleBean> modulesList = dao.getAllSessionPlanModulesBySessionPlanId(sessionPlan.getId());

		bean.setModules(modulesList);
		bean.setSuccessMessage("Success.");

		return bean;
	}
	
	private SessionPlanGetVideosAPIResponseBean getSessionPlanVideosByModuleId(Long id, String sapId) {
		
		SessionPlanGetVideosAPIResponseBean bean = new SessionPlanGetVideosAPIResponseBean();
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
					
//		List<VideoContentBean> sessionVideos = dao.getAllVideoContentListBySesionPlanModuleId(id);
		List<VideoContentAcadsBean> sessionVideos = dao.getAllVideoContentListBySesionPlanModuleIdAndSapId(id, sapId);
//		List<VideoContentBean> finalVideoContentList = new ArrayList<>();

//		for(VideoContentBean videoContentBean : sessionVideos){
//			if(dao.checkIfBookmarked(sapId,videoContentBean.getId().toString())){
//				videoContentBean.setBookmarked("Y");
//			}
//			finalVideoContentList.add(videoContentBean);
//		}

		bean.setSessionVideos(sessionVideos);
		
		return bean;
	}
	
	private SessionPlanGetLRAPIResponseBean getSessionPlanLRByModuleId(Long id, String sapId) {
		
		SessionPlanGetLRAPIResponseBean bean = new SessionPlanGetLRAPIResponseBean();
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
					
//		List<ContentBean> lRList = dao.getContentsForSessionPlanModule(id);
		List<ContentAcadsBean> lRList = dao.getContentsForSessionPlanModuleAndSapId(id, sapId);
//		List<ContentBean> finalLRList = new ArrayList<>();

//		for(ContentBean contentBean : lRList){
//			if(dao.checkIfBookmarked(sapId,contentBean.getId())){
//				contentBean.setBookmarked("Y");
//			}
//			finalLRList.add(contentBean);
//		}
		
		bean.setLearningResources(lRList);
		
		return bean;
	}
	
	private SessionPlanAPIResponseBean getSessionPlanDetailsBeanByProgramSemSubjectId(Long programSemSubjectId) {
		
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		SessionPlanAPIResponseBean bean = new SessionPlanAPIResponseBean();
		SessionPlanBean sessionPlan = dao.getSessionPlanDetailsBeanByProgramSemSubjectId(programSemSubjectId);
		
		if(sessionPlan == null) {
			bean.setErrorMessage("Error getting session plan details, Try Again.");
			bean.setSessionPlan(new SessionPlanBean());
			List<SessionPlanModuleBean> tempList=new ArrayList<>();
			bean.setModules(tempList);
		}else {
			bean.setSessionPlan(sessionPlan);

			List<SessionPlanModuleBean> modulesList = 
				dao.getAllSessionPlanModulesBySessionPlanId(sessionPlan.getId());
			
			bean.setModules(modulesList);
			bean.setSuccessMessage("Success.");
		}
		return bean;
		
	}
	
	private SessionPlanAPIResponseBean getSessionPlanDetailsBeanByTimeboundId(Long timeboundId) {
		
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		SessionPlanAPIResponseBean bean = new SessionPlanAPIResponseBean();
		SessionPlanBean sessionPlan = dao.getSessionPlanDetailsBeanByTimeboundId(timeboundId);
		
		if(sessionPlan == null) {
			bean.setErrorMessage("Error getting session plan details, Try Again.");
			bean.setSessionPlan(new SessionPlanBean());
			List<SessionPlanModuleBean> tempList=new ArrayList<>();
			bean.setModules(tempList);
		}else {
			bean.setSessionPlan(sessionPlan);

			List<SessionPlanModuleBean> modulesList = 
				dao.getAllSessionPlanModulesBySessionPlanId(sessionPlan.getId());
			
			bean.setModules(modulesList);
			bean.setSuccessMessage("Success.");
		}
		return bean;
		
	}
	
	private SessionPlanAPIResponseBean getSessionPlanDetailsBeanByTimeboundIdAndSapid(Long timeboundId, String sapid) {
		
		MBAXSessionPlanDAO dao = (MBAXSessionPlanDAO) act.getBean("mbaxSessionPlanDAO");
		SessionPlanAPIResponseBean bean = new SessionPlanAPIResponseBean();
		SessionPlanBean sessionPlan = dao.getSessionPlanDetailsBeanByTimeboundId(timeboundId);
		
		if(sessionPlan == null) {
			bean.setErrorMessage("Error getting session plan details, Try Again.");
			bean.setSessionPlan(new SessionPlanBean());
			List<SessionPlanModuleBean> tempList=new ArrayList<>();
			bean.setModules(tempList);
		}else {
			bean.setSessionPlan(sessionPlan);

			List<SessionPlanModuleBean> modulesList = 
				dao.getAllSessionPlanModulesBySessionPlanIdAndSapid(sessionPlan.getId(), sapid);

			List<SessionPlanModuleBean> modulesListToReturn = new ArrayList<SessionPlanModuleBean>(); 
			for (SessionPlanModuleBean sessionPlanModule : modulesList) {
				SessionPlanModuleBean newSessionPlanModule = sessionPlanModule;

				if(!StringUtils.isBlank(newSessionPlanModule.getSessionId())) {
					TimeTableDAO timeTableDAO = (TimeTableDAO)act.getBean("timeTableDAO");
					SessionDayTimeAcadsBean session = timeTableDAO.findScheduledSessionById(newSessionPlanModule.getSessionId());
					newSessionPlanModule.setSession(session);
				}
				
				modulesListToReturn.add(newSessionPlanModule);
			}
			bean.setModules(modulesList);
			bean.setSuccessMessage("Success.");
		}
		return bean;
		
	}
	
}
