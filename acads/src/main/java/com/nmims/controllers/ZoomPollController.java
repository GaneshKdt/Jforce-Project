package com.nmims.controllers;



import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionPollReportBean;
import com.nmims.beans.WebinarPollsBean;
import com.nmims.beans.WebinarPollsResultsBean;
import com.nmims.daos.LDAPDao;
import com.nmims.helpers.AESencrp;
import com.nmims.interfaces.PollServiceInterface;
import com.nmims.views.SessionPollReportExcelView;



@Controller
public class ZoomPollController extends BaseController
{
	
	
	@Autowired(required = false)
	ApplicationContext act;
	

	@Autowired
	SessionPollReportExcelView sessionPollReportExcelView;
	
	@Autowired
	PollServiceInterface zoomPollService;
	
	@Value( "${CURRENT_ACAD_MONTH}" )
	private String CURRENT_ACAD_MONTH;

	@Value( "${CURRENT_ACAD_YEAR}" )
	private String CURRENT_ACAD_YEAR;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;

	
	private static final Logger logger = Logger.getLogger(ZoomPollController.class);
	
	private void loginFaculty(String userIdFromURL, HttpServletRequest request) {
		request.getSession().setAttribute("userId_acads", userIdFromURL);

		LDAPDao dao = (LDAPDao) act.getBean("ldapdao");
//		System.out.println("userIdFromURL-->" + userIdFromURL);
		PersonAcads person = dao.findPerson(userIdFromURL);
//		System.out.println("PERSON FROM LDAP" + person);
		// Person person = new Person();
		person.setUserId(userIdFromURL);
		request.getSession().setAttribute("user_acads", person);
		request.getSession().setAttribute("userId_acads", userIdFromURL);

	}
	
	
	//To View Polls Results of that particular webinarId i.e MeetingKey
	
	@RequestMapping(value = "/admin/viewSessionPollsResultsForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String getSessionPollsResultsForm(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String id, Model m) {

		String userIdEncrypted = request.getParameter("eid");
		
		String userIdFromURL = null;
		try {
			
			
			
			if (userIdEncrypted != null) {
				userIdFromURL = AESencrp.decrypt(userIdEncrypted);
			}
	

		if (userIdFromURL != null) {
//			System.out.println("userIdFromURL in view session polls result form " + userIdFromURL);
			loginFaculty(userIdFromURL, request);
		}

		if (!checkSession(request, response)) {
			return "studentPortalRediret";
		}
		String userId = (String) request.getSession().getAttribute("userId_acads");

		//Person user = (Person) request.getSession().getAttribute("user_acads"); Commented By Riya as it was not used anywhere
		
		
		SessionDayTimeAcadsBean session =zoomPollService.findScheduledSessionById(id);
		request.getSession().setAttribute("webinarId", session.getMeetingKey());
		request.getSession().setAttribute("acadSessionId", id);
		m.addAttribute("session", session);
		
		WebinarPollsResultsBean webinarPollsResultsBean=zoomPollService.getWebinarPollsResults(session.getMeetingKey(),userId);
		
		m.addAttribute("totalQuesions", webinarPollsResultsBean);
		m.addAttribute("totalQuesionsSize", webinarPollsResultsBean.getQuestions().size());
		
		}catch(Exception e)
		{
			
			logger.info("Error in getting the Poll Results. Method getSessionPollsResultsForm ",e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to create session poll!"+e.getMessage());
		
		}
	
		
		//request.getSession().setAttribute("webinarId", "525960232");
		

		return "sessionPollsResults";
	}
	
	
	//To add create Polls
	
	@RequestMapping(value = "/admin/createWebinarPolls", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView createWebinarPolls(HttpServletRequest request,HttpServletResponse response,
			@ModelAttribute("sessionPolls") WebinarPollsBean webinarPollsBean) throws IOException {
		String acadSessionId = null;
		ModelAndView modelAndView = null;
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		try {
			String userId = (String) request.getSession().getAttribute("userId_acads");
			String webinarId = (String) request.getSession().getAttribute("webinarId");
			
			acadSessionId = (String) request.getSession().getAttribute("acadSessionId");
			modelAndView = new ModelAndView("redirect:/admin/viewAddSessionPollsForm?id=" + acadSessionId);
			
			webinarPollsBean.setCreatedBy(userId);
			webinarPollsBean.setLastModifiedBy(userId);
			
//			System.out.println("Create Webinar  Poll Service Called--------- ");
			HashMap<String,String> create_response  = zoomPollService.createWebinarPoll(webinarId,webinarPollsBean);
				
				//-------------------
				if (create_response.get("success").equals("true")) {
										
					request.setAttribute("success", create_response.get("success"));
					request.setAttribute("successMessage",  create_response.get("successMessage"));
					modelAndView.setViewName(modelAndView.getViewName()
							+ "&pollError=n&pollMessage=Successfully Session Poll is created.");
					return modelAndView;

				} else {
					request.setAttribute("error",create_response.get("error"));
					request.setAttribute("errorMessage", create_response.get("errorMessage"));
					modelAndView.setViewName(
							modelAndView.getViewName() + "&pollError=y&pollMessage=Failed to create session poll!");
					return modelAndView;

				}

			

		} catch (Exception ex) {
			
//			System.out.println("Got exception while creating webinar polls.");
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to create session poll!");
			modelAndView.setViewName(
					modelAndView.getViewName() + "&pollError=y&pollMessage=Failed to create session poll!");
			return modelAndView;

		}

	}
	
	
	
	
	//To view created sessions Polls
	
	@RequestMapping(value = "/admin/viewAddSessionPollsForm", method = { RequestMethod.GET, RequestMethod.POST })
	public String getSessionPollsForm(HttpServletRequest request, HttpServletResponse response, @RequestParam String id,
			Model m) {

		String userIdEncrypted = request.getParameter("eid");
//		System.out.println("userIdEncrypted " + userIdEncrypted);
		String userIdFromURL = null;
		try {
			if (userIdEncrypted != null) {
				userIdFromURL = AESencrp.decrypt(userIdEncrypted);
			}
		
		if (userIdFromURL != null) {
//			System.out.println("userIdFromURL in view session polls form " + userIdFromURL);
			loginFaculty(userIdFromURL, request);
		}

		if (!checkSession(request, response)) {
			return "studentPortalRediret";
		}
		//String userId = (String) request.getSession().getAttribute("userId_acads");

		//Person user = (Person) request.getSession().getAttribute("user_acads"); Commented By Riya as it was not used anywhere

//		System.out.println(" debug 1");
		SessionDayTimeAcadsBean session = zoomPollService.findScheduledSessionById(id);

//		System.out.println(" debug 1 "+session);
		
		
		
		m.addAttribute("session", session);
		
		m.addAttribute("sessionPoll", new WebinarPollsBean());
		
		request.getSession().setAttribute("webinarId", session.getMeetingKey());
		request.getSession().setAttribute("acadSessionId", id);
		

		String error = (String) request.getParameter("pollError");
		String msg = (String) request.getParameter("pollMessage");
		
		
		
		if (!StringUtils.isBlank(error) && !StringUtils.isBlank(msg)) {
			if (error.equals("n")) {
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", msg);
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", msg);
			}
		}
		
		List<WebinarPollsBean> webinarPollsBeanList = zoomPollService.getSessionPolls(session.getMeetingKey());
//		System.out.println(" debug 1 "+webinarPollsBeanList.size());
		m.addAttribute("totalPolls", webinarPollsBeanList);
		m.addAttribute("totalPollsSize", webinarPollsBeanList.size());
		
		
		} catch (Exception e) {
			
			// TODO: handle exception
			logger.info("Error in getting the Poll Results. Method getSessionPollsForm ",e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to getting session poll!"+e.getMessage());
		}


		return "sessionPolls";
	}
	
	
	//To get a session Poll
	@RequestMapping(value = "/admin/getWebinarPoll", method = { RequestMethod.GET })
	public String getWebinarPoll(HttpServletRequest request, HttpServletResponse response, Model m) {
		if (!checkSession(request, response)) {
			return "studentPortalRediret";
		}

		try {
			String webinarId = (String) request.getParameter("webinarId");
			String pollId = (String) request.getParameter("pollId");
			String acadSessionId = (String) request.getSession().getAttribute("acadSessionId");
			
			
			if (acadSessionId != null && pollId != null && !pollId.isEmpty() && webinarId != null
					&& !webinarId.isEmpty()) {
				//SessionDayTimeBean session = zoomPollService.findScheduledSessionById(acadSessionId);
				
//				System.out.println("--------- Get All the Session Polls Service Called -----");
				List<WebinarPollsBean> webinarPollsBeanList = zoomPollService.getSessionPolls(webinarId);
				List<WebinarPollsBean> webinarPollBeanList =new ArrayList<WebinarPollsBean>();
							
				for (WebinarPollsBean webinarPollsBean : webinarPollsBeanList) {
					if (webinarPollsBean.getId().equals(pollId)) {
						m.addAttribute("sessionPoll", webinarPollsBean); 
						
					}
				}
				
				//m.addAttribute("session", session);
				m.addAttribute("totalPoll",webinarPollBeanList);
				m.addAttribute("totalPolls", webinarPollsBeanList);
				m.addAttribute("totalPollsSize", webinarPollsBeanList.size());
				m.addAttribute("formaction", "updateWebinarPolls");
				m.addAttribute("msg", "Scroll down to edit poll!");
				request.setAttribute("success", "true");
				request.setAttribute("successMessage", "Scroll down to edit poll!");
			} else {
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error while retrieving poll!");
			}
		} catch (Exception ex) {
	
			logger.info("Error in getting the Poll Results. Method getSessionPollsForm ",ex);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error while retrieving poll!"+ex.getMessage());
		}
		return "sessionPolls";
	}
	
	//To update your poll
	
	
	@RequestMapping(value = "/admin/updateWebinarPolls", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView updateWebinarPolls(HttpServletRequest request,
			@ModelAttribute("sessionPolls") WebinarPollsBean webinarPollsBean) throws IOException {
		String acadSessionId = null;
		
		ModelAndView modelAndView = null;
		try {
			String userId = (String) request.getSession().getAttribute("userId_acads");  
			String webinarId = (String) request.getSession().getAttribute("webinarId");
//			System.out.println("312 "+webinarId);
			acadSessionId = (String) request.getSession().getAttribute("acadSessionId");
			modelAndView = new ModelAndView("redirect:/admin/viewAddSessionPollsForm?id=" + acadSessionId);
			
			webinarPollsBean.setCreatedBy(userId);
			webinarPollsBean.setLastModifiedBy(userId);
			
			
//			System.out.println("----------- Update Poll Service Called-------");
			HashMap<String,String> update_response = zoomPollService.updateWebinarPoll(webinarId, webinarPollsBean);
				
				if (update_response.get("success").equals("true")) {
										
					request.setAttribute("success", update_response.get("success"));
					request.setAttribute("successMessage", update_response.get("successMessage"));
					modelAndView.setViewName(modelAndView.getViewName()
							+ "&pollError=n&pollMessage=Successfully Session Poll is updated.");
					return modelAndView;
				} else {
					request.setAttribute("error", update_response.get("error"));
					request.setAttribute("errorMessage", update_response.get("errorMessage"));
					modelAndView.setViewName(
							modelAndView.getViewName() + "&pollError=y&pollMessage=Failed to update session poll!");
					return modelAndView;
				}

			

		} catch (Exception ex) {
			
			logger.info("Error in updating polls . Method updateWebinarPolls ",ex);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Failed to update session poll!");
			modelAndView.setViewName(
					modelAndView.getViewName() + "&pollError=y&pollMessage=Failed to update session poll! "+ex.getMessage());
			return modelAndView;
		}
	}

	
	//To delete Poll 
	
	@RequestMapping(value = "/admin/deleteWebinarPoll", method = { RequestMethod.GET })
	public ModelAndView deleteWebinarPoll(HttpServletRequest request) {
		String webinarId = null;
		String pollId = null;
		String acadSessionId = (String) request.getSession().getAttribute("acadSessionId");
		HashMap<String,String>  deletePoll_response = new HashMap<String,String>();
		ModelAndView modelAndView = new ModelAndView("redirect:/admin/viewAddSessionPollsForm?id=" + acadSessionId);
		try {

			webinarId = (String) request.getParameter("webinarId");
			pollId = (String) request.getParameter("pollId");

//			System.out.println("webinarId : " + webinarId + " pollId : " + pollId);
			if (webinarId != null && !webinarId.isEmpty() && !pollId.isEmpty() && pollId != null) {
				
				//------------Delete Poll Service Called-----------"
				deletePoll_response = zoomPollService.deleteWebinarPoll(webinarId, pollId);
				
			}
			if (deletePoll_response.get("error").equals("true")) {
				modelAndView.setViewName(modelAndView.getViewName()
						+ "&pollError=y&pollMessage=Error while deleting webinar polls!");
				return modelAndView;
			}
			else {
				modelAndView.setViewName(
						modelAndView.getViewName() + "&pollError=n&pollMessage=Successfully webinar polls deleted!");
				return modelAndView;
				
			}
		} catch (Exception ex) {
		
			logger.info("Error in deleting polls . Method updateWebinarPolls ",ex);
			modelAndView.setViewName(
					modelAndView.getViewName() + "&pollError=y&pollMessage=Error while deleting webinar polls!"+ex.getMessage());
			return modelAndView;
		}
	}
	
	//downloadSessionPollReportForm
	
		@RequestMapping(value = "/admin/downloadSessionPollReportForm", method = { RequestMethod.GET, RequestMethod.POST })
		public String searchSessionPollReportForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
			
			String userId = (String) request.getSession().getAttribute("userId_acads");
			m.addAttribute("yearList", ACAD_YEAR_LIST);
			m.addAttribute("monthList", ACAD_MONTH_LIST);
			m.addAttribute("searchBean", new SessionPollReportBean());
			m.addAttribute("userId",userId);
			return "searchSessionPollReport";
		}

	
		@RequestMapping(value = "/admin/getSubjectNames",method = RequestMethod.POST , consumes="application/json", produces="application/json")
		public ResponseEntity viewFacultyIdBypssId(@RequestBody  SessionPollReportBean inputObject) {
				
				ArrayList<String> subjects = new ArrayList<String>();
				try {
					
					subjects = zoomPollService.getSubjectCodeLists(inputObject.getFacultyId(),inputObject.getMonth(), inputObject.getYear());
				
				}catch (Exception e)
				{
					
					logger.error("Getting error in subject codes (Method Name :- viewFacultyIdBypssId) ",e);
				}
				return new ResponseEntity(subjects,HttpStatus.OK); 
			}

		//To search poll by the given data from viewsessionPollReport
		
		
		@RequestMapping(value = "/admin/sessionPollReport", method = RequestMethod.POST)
		public ModelAndView sessionPollReport(HttpServletRequest request, HttpServletResponse response,
				@ModelAttribute SessionPollReportBean searchBean) {
			
			
			ModelAndView modelnView = new ModelAndView("searchSessionPollReport");
			try {
			request.getSession().setAttribute("searchBean_acads", searchBean);
			
			PageAcads<SessionPollReportBean> page = zoomPollService.getSessionPollReport(1, Integer.MAX_VALUE, searchBean);
			List<SessionPollReportBean> sessionPollList = page.getPageItems();
			
			
			
			modelnView.addObject("searchBean", searchBean);
			request.getSession().setAttribute("sessionPollList", sessionPollList);
			ArrayList<String> subjectList=(ArrayList<String>)request.getSession().getAttribute("subjectlist");
			modelnView.addObject("subjectList",subjectList);
			modelnView.addObject("yearList", ACAD_YEAR_LIST);
			modelnView.addObject("monthList", ACAD_MONTH_LIST);
			modelnView.addObject("row_count",sessionPollList.size());
			
			
				if (sessionPollList.size() == 0) {
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", "No Poll Records Found.");
				}
			}catch (Exception e)
			{
				
				logger.error("Getting error poll data Method :- sessionPollReport) ",e);
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", "Error in getting poll report. "+e.getMessage());
			}
			return modelnView;
		}

		
		@RequestMapping(value = "/admin/downloadPollReport", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView downloadSessionPollReport(HttpServletRequest request, HttpServletResponse response) {
			List<SessionPollReportBean> sessionPollList = new ArrayList<SessionPollReportBean>();
			
			try {
			if (!checkSession(request, response)) {
				return new ModelAndView("login");
			}
			/*SessionPollReportBean searchBean = (SessionPollReportBean) request.getSession().getAttribute("searchBean_acads");*/
			sessionPollList = (ArrayList<SessionPollReportBean>) request.getSession().getAttribute("sessionPollList");
			
		}catch (Exception e)
		{
			
			logger.error("Getting error downloading poll data Method :- downloadSessionPollReport) ",e);
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in downloading poll report. "+e.getMessage());
		}
			
			return new ModelAndView("sessionPollReportExcelView","sessionPollList", sessionPollList);
		}
			
		

	
	

	
 
}
