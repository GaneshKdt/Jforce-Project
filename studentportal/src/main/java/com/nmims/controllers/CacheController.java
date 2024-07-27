package com.nmims.controllers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.CacheRefreshStudentPortalBean;

@Controller
public class CacheController extends BaseController {


	@Autowired
	ApplicationContext act;

	@Value("${SERVER_PORT}")
	private int[] ports;
	/*@Autowired
	AssignmentsDAO assignmentsDAO;
	
	@Autowired
	DashboardDAO dashboardDAO;
	
	@Autowired
	ExamBookingDAO examBookingDAO;
	
	@Autowired
	ExamCenterDAO examCenterDAO;
	
	@Autowired
	FacultyDAO facultyDAO;
	
	@Autowired
	PassFailDAO passFailDAO;
	
	@Autowired
	ProjectSubmissionDAO projectSubmissionDAO;
	
	@Autowired
	ReportsDAO reportsDAO;
	
	@Autowired
	ResitExamBookingDAO resitExamBookingDAO;
	
	@Autowired
	StudentMarksDAO studentMarksDAO;*/
	
	
	//Make an entry in this method for every DAO that is made
	@RequestMapping(value = "/admin/refreshCache", method = {RequestMethod.GET})
	public ModelAndView refreshCache(HttpServletRequest request, HttpServletResponse respnse) {
		/*assignmentsDAO.refreshLiveFlagSettings();
		dashboardDAO.refreshLiveFlagSettings();
		examBookingDAO.refreshLiveFlagSettings();
		examCenterDAO.refreshLiveFlagSettings();
		facultyDAO.refreshLiveFlagSettings();
		passFailDAO.refreshLiveFlagSettings();
		projectSubmissionDAO.refreshLiveFlagSettings();
		reportsDAO.refreshLiveFlagSettings();
		resitExamBookingDAO.refreshLiveFlagSettings();
		studentMarksDAO.refreshLiveFlagSettings();*/
		
		return null;
	}
	
	@RequestMapping(value = "/admin/cacheRefreshToOnlyServer", method = {RequestMethod.GET})
	public ModelAndView CacheRefreshToOnlyServer() {
		
		AnnouncementController announcementController = act.getBean(AnnouncementController.class);
		announcementController.RefreshCache();
		EmailSMSController emailSMSController = act.getBean(EmailSMSController.class);
		emailSMSController.RefreshCache();
		HomeController homeController = act.getBean(HomeController.class);
		homeController.RefreshCache();
		
		return new ModelAndView("jsp/RequestTesting");
	}
	
	@RequestMapping(value = "/admin/cacheRefreshToAllServer", method = {RequestMethod.GET})
	public @ResponseBody CacheRefreshStudentPortalBean CacheRefreshToAllServer() throws IOException {
		
		ModelAndView mv = new ModelAndView("jsp/RequestTesting");
		String error = this.TryRefreshCacheToAllServer(0);
		CacheRefreshStudentPortalBean response = new CacheRefreshStudentPortalBean();
		response.setMessage("failed : " + error);
		if(error.isEmpty()) {
			response.setStatus("success");
		}else {
			response.setStatus("error");
		}
		return response;
	}
	
	@RequestMapping(value = "/admin/cacheRefreshPanel", method = {RequestMethod.GET})
	public String CacheRefreshPanel(HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		if(!checkSession(request, response)){
			return "redirect:/login";
		}
		return "jsp/CacheRefreshPanel";
	}
	

	@RequestMapping(value = "/admin/pingAllServerInstances", method = {RequestMethod.GET})
	public ResponseEntity<String> pingAllServerInstances() {
		String singlePixel = "resources_2015/images/singlePixel.gif";
		String favicon = "favicon.png";
		String logo = "assets/images/logo.jpg";
		
		List<String >apps = new ArrayList<String>();
		apps.add("/studentportal/" + singlePixel);
		apps.add("/acads/" + singlePixel);
		apps.add("/careerservices/" + singlePixel);
		apps.add("/exam/" + singlePixel);
		apps.add("/salesforce/" + singlePixel);
		apps.add("/ltidemo/" + logo);
		apps.add("/timeline/" + favicon);
		apps.add("/ssoservices/" + favicon);
		
		Map<String, String> response = new HashMap<String, String>();
		
		String responseData = "";
		for(int port : ports) {
			responseData += "<br/> --------------------------------------- <br/>";
			for(String app:apps) {
				responseData += "<br/>";
				String path = "http://localhost:" + port + app;
				try {

					URL obj = new URL(path);
					HttpURLConnection  con = (HttpURLConnection ) obj.openConnection();
					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", "Mozilla/5.0");
					int responseCode = con.getResponseCode();
					
					if (responseCode == HttpURLConnection.HTTP_OK) { // success
						responseData += "<span style='color : green'>" + path + ": Success" + "</span>";
						response.put(path, "Success");
					} else {
						responseData += "<span style='color : red'>" + path + ": Fail - " + responseCode + "</span>";
						response.put(path, "FAIL : " + responseCode);
					}
					
				}catch (Exception e) {
					responseData +=  "<span style='color : orange'>" + path + ": Fail - " + e + "</span>";
					response.put(path, "Error : " + e);
				}
			}
		}
		
		return new ResponseEntity<String>(responseData, HttpStatus.OK);
	}
}
