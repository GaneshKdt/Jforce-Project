package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.helpers.EmailHelper;
import com.nmims.beans.LoginBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.daos.AttendanceFeedbackDAO;
import com.nmims.daos.ReportsDAO;

/**
 * Handles requests for the application login page.
 */
@Controller
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private final int pageSize = 10;
	private ArrayList<String> yearList = new ArrayList<String>(Arrays.asList( 
			"2015","2016","2017")); 

	
	@Autowired(required=false)
	ApplicationContext act;
	@Autowired
	private EmailHelper emailHelper;
	
	@Autowired
	private AttendanceFeedbackDAO attendanceFeedbackDAO;
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String home(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("Login Page"); 
		
		request.getSession().setAttribute("userId_acads", null);
		/*sendEmail();*/
		return "login";
	}
	
	
	/*public void sendEmail(){
		try {
			//EmailHelper helper = new EmailHelper();
			ArrayList<String> emailids = new ArrayList<>(Arrays.asList("sanketpanaskar@gmail.com", "jforcesolutions@gmail.com"));
			emailHelper.sendMassEmail(emailids, "sanketpanaskar@gmail.com", "NMIMS Global Access SCE", "Test Email", "This is a test email");

		} catch (Exception e) {
			  
		}
	}	*/
	@RequestMapping(value="/searchStudentZoneLoginsForm",method={RequestMethod.GET,RequestMethod.POST})
	public String searchStudentZoneLoginsForm(HttpServletRequest request, HttpServletResponse respnse,Model m){
		LoginBean searchBean = new LoginBean();
		
		m.addAttribute("searchBean", searchBean);
		
		return "searchStudentZoneLogins";
	}
	@RequestMapping(value="/searchStudentZoneLogins",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView searchStudentZoneLogins(HttpServletRequest request, HttpServletResponse response,@ModelAttribute LoginBean searchBean){
		ModelAndView modelAndView = new ModelAndView("searchStudentZoneLogins");
		AttendanceFeedbackDAO dao = (AttendanceFeedbackDAO)act.getBean("attendanceFeedbackDAO");
	
		PageAcads<LoginBean> page = attendanceFeedbackDAO.getStudentLogins(1, Integer.MAX_VALUE,searchBean);
		List<LoginBean> listOfStudents = page.getPageItems();
		request.getSession().setAttribute("searchBean_acads", searchBean);
		modelAndView.addObject("page", page);
		modelAndView.addObject("rowCount", page.getRowCount());
		
		modelAndView.addObject("searchBean", searchBean);

		if(listOfStudents == null || listOfStudents.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Attendance Records Found.");
		}
		
		modelAndView.addObject("loginList", listOfStudents);
		
		return modelAndView;
		
	}
	@RequestMapping(value="/searchLoginPage",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView searchLoginPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView("searchStudentZoneLogins");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		LoginBean search = (LoginBean)request.getSession().getAttribute("searchBean_acads");
		PageAcads<LoginBean> page = attendanceFeedbackDAO.getStudentLogins(pageNo, Integer.MAX_VALUE,search);
		List<LoginBean> listOfStudents = page.getPageItems();
		modelAndView.addObject("page", page);
		modelAndView.addObject("rowCount", page.getRowCount());
		modelAndView.addObject("student",search);
		if(listOfStudents == null || listOfStudents.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Records found.");
		}
		return modelAndView;
		
	}

	public void sendMassEmail(ArrayList<String> toEmailIds, String fromEmailId, String fromName, String subject, String htmlBody) {
		PoolingHttpClientConnectionManager poolingConnManager = new PoolingHttpClientConnectionManager();
		try {
			poolingConnManager.setDefaultMaxPerRoute(50);
			poolingConnManager.setMaxTotal(200);

			//CloseableHttpClient client = HttpClients.custom().setConnectionManager(poolingConnManager).build();

			//Setting the HttpClient to Check for Stale Connections
			CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(
					RequestConfig.custom().setStaleConnectionCheckEnabled(true).build()).
					setConnectionManager(poolingConnManager).build();
			//EmailHelper helper = new EmailHelper();
			if(toEmailIds != null && toEmailIds.size() > 0){
				for (String toEmailId : toEmailIds) {
					emailHelper.sendEmailUsingTNSAPI(client, toEmailId, fromEmailId, fromName, subject, htmlBody);
					//emailHelper.testMethod();
				}
			}
			//client.close();

		} catch (Exception e) {
			  
		}finally{
			/*poolingConnManager.close();
			poolingConnManager.shutdown();*/
		}
	}
}
