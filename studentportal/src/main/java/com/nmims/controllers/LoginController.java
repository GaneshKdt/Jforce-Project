package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.CenterStudentPortalBean;
import com.nmims.beans.LeadStudentPortalBean;
import com.nmims.beans.UserAuthorizationStudentPortalBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.PortalDao;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.MapUtils;
import com.nmims.helpers.SMSSender;
import com.nmims.services.AuthorizationService;
import com.nmims.services.LDAPService;
import com.nmims.services.LoginLogService;
import com.nmims.views.UserAuthorizationExcelView;

/**
 * Handles requests for the application login page.
 */
@Controller
public class LoginController extends BaseController{
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	SMSSender smsSender;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	LeadDAO leadDAO;
	
	@Autowired
	LoginLogService loginLogService;
	
	@Autowired
	UserAuthorizationExcelView userAuthorizationExcelView;
	
	@Autowired
	AuthorizationService authorizationService;
	
	@Autowired
	LDAPService lDAPService;
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private static final String nelson = "nelson";


	public HashMap<String, String> getCenterCodeNameMap(){
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<CenterStudentPortalBean> centers = pDao.getAllCenters();
		HashMap<String, String> centerCodeNameMap = new HashMap<>();
		for (int i = 0; i < centers.size(); i++) {
			CenterStudentPortalBean cBean = centers.get(i);
			centerCodeNameMap.put(cBean.getCenterCode(), cBean.getCenterName());
		}
		return centerCodeNameMap;
	}
	
	public ArrayList<String> getLCList(){
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		ArrayList<String> lcs = pDao.getAllLCs();
		return lcs;
	}

	public ArrayList<String> getAllRolesList(){
		ArrayList<String> allRoles = new ArrayList<>(Arrays.asList("Exam Admin", "TEE Admin", "Marksheet Admin", "Assignment Admin",
																   "Read Admin", "Portal Admin", "Acads Admin","Finance","SR Admin",
																   "Faculty", "Learning Center", "Information Center",
																   "Corporate Center", "Student Support","Watch Videos Access",
																   "Career Services Admin", "Career Services Products Admin", 
																   "Career Services Sessions Admin", "Career Services Sessions Speaker", 
																   "Externally Affiliated","MBA-WX Admin"
				   					));
	   return allRoles;
	}
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String home(HttpServletRequest request, HttpServletResponse respnse) {
		logger.info("Login Page");
		request.getSession().setAttribute("userId", null);
//		return "login";
		return "redirect:" + SERVER_PATH + "logout";
	}
	
		// added by Ritesh
		@RequestMapping(value = "/admin/viewAuthorization", method ={RequestMethod.GET,RequestMethod.POST})
		public ModelAndView viewAuthorization(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute UserAuthorizationStudentPortalBean userAuthorizationBean) {
 
			ModelAndView modelnView = new ModelAndView("jsp/updateAuthorization");
			String loggedinUserId = (String)request.getSession().getAttribute("userId");
			
			List<String>  availableRolesList = new ArrayList<>();
			List<String>  selectedRolesList = new ArrayList<>();
			
   			try {
 
				//dao.findPerson(loggedinUserId);	
				lDAPService.findPerson(loggedinUserId);
				
				if (userAuthorizationBean.getUserId() == null || userAuthorizationBean.getUserId().equals("")) {
					userAuthorizationBean = authorizationService.getUserAuthorization(loggedinUserId);
 				} else {
					userAuthorizationBean = authorizationService.getUserAuthorization(userAuthorizationBean.getUserId());
 				}

				String currentRoles = userAuthorizationBean.getRoles() == null ? "" : userAuthorizationBean.getRoles();

				ArrayList<String> allRolesList = getAllRolesList();

				 availableRolesList = allRolesList.stream()
				        .filter(role -> !StringUtils.isBlank(role) && !currentRoles.contains(role))
				        .collect(Collectors.toList());

				 selectedRolesList = allRolesList.stream()
				        .filter(role -> !StringUtils.isBlank(role) && currentRoles.contains(role))
				        .collect(Collectors.toList());
				
				modelnView.addObject("userFound", "true");

			}catch (Exception  e) {
				setError(request , "User Not Found");
			}
   			modelnView.addObject("user", userAuthorizationBean);
			modelnView.addObject("userAuthorizationBean", userAuthorizationBean);
			modelnView.addObject("availableRolesList", availableRolesList);
			modelnView.addObject("selectedRolesList", selectedRolesList);
			return modelnView;
		}
		
		@RequestMapping(value = "/admin/updateAuthorization", method ={RequestMethod.POST})
		public ModelAndView updateAuthorization(HttpServletRequest request, HttpServletResponse respnse, @ModelAttribute UserAuthorizationStudentPortalBean userAuthorizationBean) {
 
 			ModelAndView modelnView = new ModelAndView("jsp/updateAuthorization");
			
			ArrayList<String> availableRolesList = new ArrayList<>();
			ArrayList<String> selectedRolesList = new ArrayList<>();
			
			try {
				
				String loggedinUserId = (String)request.getSession().getAttribute("userId");
				
 				if(!loggedinUserId.equals(nelson)) {
 					setError(request, "You can't update the roles");
					return modelnView;
				}
					
 				if(!userAuthorizationBean.getUserId().equals(nelson)) {
  					authorizationService.updateRolesInLdap(userAuthorizationBean.getUserId(), userAuthorizationBean.getRoles());
 					authorizationService.updateRolesInAuthorizationTable(userAuthorizationBean.getUserId(), userAuthorizationBean.getRoles());
 					setSuccess(request, "Authorization update successfully");
 				}else {
 					userAuthorizationBean = authorizationService.getUserAuthorization(userAuthorizationBean.getUserId());
 					setError(request, "You can't update your own roles");
 				}

 				String currentRoles = userAuthorizationBean.getRoles() == null ? "" : userAuthorizationBean.getRoles();
			
 				ArrayList<String> allRolesList = getAllRolesList();
			
 				availableRolesList = (ArrayList<String>)allRolesList.stream()
				        .filter(role -> !StringUtils.isBlank(role) && !currentRoles.contains(role))
				        .collect(Collectors.toList());

 				selectedRolesList = (ArrayList<String>)allRolesList.stream()
				        .filter(role -> !StringUtils.isBlank(role) && currentRoles.contains(role))
				        .collect(Collectors.toList());

 				modelnView.addObject("userFound", "true");
			
			}catch (Exception  e) {
				setError(request, "Error in update authorization");
 			}	
			
			modelnView.addObject("user", userAuthorizationBean);
			modelnView.addObject("userAuthorizationBean", userAuthorizationBean);
 			modelnView.addObject("availableRolesList",availableRolesList);
			modelnView.addObject("selectedRolesList", selectedRolesList);
			
			return modelnView;
	 	}
// *****
		
	@RequestMapping(value = "/admin/searchUserAuthorizationForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchUserAuthorizationForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		UserAuthorizationStudentPortalBean user = new UserAuthorizationStudentPortalBean();
		m.addAttribute("user",user);

		return "jsp/userAuthorization";
	}


	@RequestMapping(value = "/admin/searchUserAuthorization",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchUserAuthorization(HttpServletRequest request, HttpServletResponse response, @ModelAttribute UserAuthorizationStudentPortalBean user){
		ModelAndView modelnView = new ModelAndView("jsp/userAuthorization");
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
		modelnView.addObject("user", user);
		try {
		/*	boolean userExists = dao.checkUserExists(user.getUserId());
			if(!userExists){
				setError(request, "User does not exist. Please create user before setting up Authorization");
				return modelnView;
			}*/
			dao.findPerson(user.getUserId());			
			ContentDAO contentDao=(ContentDAO)act.getBean("contentDAO");
			boolean userExists = contentDao.checkUserExists(user.getUserId());
//			if(userExists){
//				setError(request, "User Entry Already Present ");
//				return modelnView;
//			}
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			UserAuthorizationStudentPortalBean userAuthorizationBean = pDao.getUserAuthorization(user.getUserId());
			modelnView.addObject("userFound", "true");
			if(userAuthorizationBean == null){
				userAuthorizationBean = new UserAuthorizationStudentPortalBean();
				userAuthorizationBean.setUserId(user.getUserId());
			}
			
			String currentRoles = userAuthorizationBean.getRoles() == null ? "" : userAuthorizationBean.getRoles();
			String currentLCs = userAuthorizationBean.getAuthorizedLC() == null ? "" : userAuthorizationBean.getAuthorizedLC();
			String currentICs = userAuthorizationBean.getAuthorizedCenters() == null ? "" : userAuthorizationBean.getAuthorizedCenters();
			
			HashMap<String, String> centerCodeNameMap = getCenterCodeNameMap();
			HashMap<String, String> availableCenterCodeNameMap = new HashMap<>();
			HashMap<String, String> selectedCenterCodeNameMap = new HashMap<>();
			
			for (String centerCode : centerCodeNameMap.keySet()) {
			    String centerName = centerCodeNameMap.get(centerCode);
			    if(StringUtils.isBlank(centerCode)){
			    	continue;
			    }
			    if(currentICs.contains(centerCode)){
			    	selectedCenterCodeNameMap.put(centerCode, centerName);
			    }else{
			    	availableCenterCodeNameMap.put(centerCode, centerName);
			    }
			}

			ArrayList<String> allLCList = getLCList();
			ArrayList<String> availableLCList = new ArrayList<>();
			ArrayList<String> selectedLCList = new ArrayList<>();
			for (String lc : allLCList) {
				if(StringUtils.isBlank(lc)){
			    	continue;
			    }
				
				if(currentLCs.contains(lc)){
					selectedLCList.add(lc);
			    }else{
			    	availableLCList.add(lc);
			    }
			}
			
			ArrayList<String> allRolesList = getAllRolesList();
			ArrayList<String> availableRolesList = new ArrayList<>();
			ArrayList<String> selectedRolesList = new ArrayList<>();
			for (String role : allRolesList) {
				if(StringUtils.isBlank(role)){
			    	continue;
			    }
				
				if(currentRoles.contains(role)){
					selectedRolesList.add(role);
			    }else{
			    	availableRolesList.add(role);
			    }
			}
			
			
			modelnView.addObject("userAuthorizationBean", userAuthorizationBean);
			modelnView.addObject("availableLCList", availableLCList);
			modelnView.addObject("selectedLCList", selectedLCList);
			modelnView.addObject("availableCenterCodeNameMap", MapUtils.sortByValue(availableCenterCodeNameMap));
			modelnView.addObject("selectedCenterCodeNameMap", MapUtils.sortByValue(selectedCenterCodeNameMap));
			modelnView.addObject("availableRolesList", availableRolesList);
			modelnView.addObject("selectedRolesList", selectedRolesList);
			

		}catch(NameNotFoundException n) {
			setError(request , "User Not Found In LDap");
		}
		catch (Exception  e) {
			setError(request, e.getMessage());
		}
		
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/setAuthorization",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView setAuthorization(HttpServletRequest request, HttpServletResponse response, @ModelAttribute UserAuthorizationStudentPortalBean userAuthorizationBean) throws NameNotFoundException{
		LDAPDao dao = (LDAPDao)act.getBean("ldapdao");
 		ModelAndView modelnView = new ModelAndView("jsp/userAuthorization");
		try {
			
			String loggedinUserId = (String)request.getSession().getAttribute("userId");	
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");	
		
			if(!loggedinUserId.equals(nelson)) {
					setError(request, "You can't update the roles");
				return modelnView;
			}
			if(!userAuthorizationBean.getUserId().equals(nelson)) {
				
			dao.updateRolesLdapAttribute(userAuthorizationBean.getUserId(), userAuthorizationBean.getRoles());
			pDao.saveAuthorization(userAuthorizationBean, loggedinUserId);
			setSuccess(request, "Authorization set successfully");
		
			}
			else {
				userAuthorizationBean = authorizationService.getUserAuthorization(userAuthorizationBean.getUserId());
				setError(request, "You can't update your own roles");
			}

		    String currentRoles = userAuthorizationBean.getRoles() == null ? "" : userAuthorizationBean.getRoles();
			String currentLCs = userAuthorizationBean.getAuthorizedLC() == null ? "" : userAuthorizationBean.getAuthorizedLC();
			String currentICs = userAuthorizationBean.getAuthorizedCenters() == null ? "" : userAuthorizationBean.getAuthorizedCenters();

			
			HashMap<String, String> centerCodeNameMap = getCenterCodeNameMap();

			Map<String, String> availableCenterCodeNameMap = centerCodeNameMap.entrySet().stream()
			        .filter(entry -> !StringUtils.isBlank(entry.getKey()) && !currentICs.contains(entry.getKey()))
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			Map<String, String> selectedCenterCodeNameMap = centerCodeNameMap.entrySet().stream()
			        .filter(entry -> !StringUtils.isBlank(entry.getKey()) && currentICs.contains(entry.getKey()))
			        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

 			ArrayList<String> allLCList = getLCList();
	 		
			ArrayList<String> availableLCList = (ArrayList<String>)allLCList.stream()
			        .filter(lc -> !StringUtils.isBlank(lc) && !currentLCs.contains(lc))
			        .collect(Collectors.toList());

			ArrayList<String> selectedLCList = (ArrayList<String>)allLCList.stream()
			        .filter(lc -> !StringUtils.isBlank(lc) && currentLCs.contains(lc))
			        .collect(Collectors.toList());
			
 			ArrayList<String> allRolesList = getAllRolesList();
	 		
	 		ArrayList<String> availableRolesList = (ArrayList<String>)allRolesList.stream()
			        .filter(role -> !StringUtils.isBlank(role) && !currentRoles.contains(role))
			        .collect(Collectors.toList());

	 		ArrayList<String> selectedRolesList = (ArrayList<String>)allRolesList.stream()
			        .filter(role -> !StringUtils.isBlank(role) && currentRoles.contains(role))
			        .collect(Collectors.toList());
	 		

			modelnView.addObject("userFound", "true");
			modelnView.addObject("userAuthorizationBean", userAuthorizationBean);
			modelnView.addObject("availableLCList", availableLCList);
			modelnView.addObject("selectedLCList", selectedLCList);
			modelnView.addObject("availableCenterCodeNameMap", MapUtils.sortByValue(availableCenterCodeNameMap));
			modelnView.addObject("selectedCenterCodeNameMap", MapUtils.sortByValue(selectedCenterCodeNameMap));
			modelnView.addObject("availableRolesList", availableRolesList);
			modelnView.addObject("selectedRolesList", selectedRolesList);
			
			modelnView.addObject("lcList", getLCList());
			modelnView.addObject("centerList", getCenterCodeNameMap());

		
		}catch (Exception  e) {
			setError(request, "Error in set authorization");
 
		}
 		modelnView.addObject("user", userAuthorizationBean);
		return modelnView;
 	}
	
	@RequestMapping(value="/requestOTP", method= RequestMethod.POST,consumes ="application/json",produces = "application/json")
	public ResponseEntity<String>  requestOTP(@RequestBody LeadStudentPortalBean leadBean,HttpServletRequest request,HttpServletResponse response) {
		String otp = generateRandomOTP();

		HttpHeaders header = new HttpHeaders();
		leadBean.setOtp(otp);
		if(leadDAO.insertIntoLead(leadBean)) {
			
			smsSender.sendRequestOTP(leadBean);
//			mailSender.sendOtpForLeadLogin(leadBean);
			

			return new ResponseEntity<String>("Successfully otp send",header,HttpStatus.OK);
		}
		return new ResponseEntity<String>("Failed to re-send OTP",header,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(value="/verifyRequestOTP", method= RequestMethod.POST,consumes ="application/json",produces = "application/json")
	public ResponseEntity<String> verifyRequestOTP(@RequestBody LeadStudentPortalBean leadBean,HttpServletRequest request,HttpServletResponse response) {
		LeadStudentPortalBean leadBean2 = leadDAO.verifyOTP(leadBean);
		HttpHeaders header = new HttpHeaders();
		if(leadBean2 != null) {

			if(leadDAO.updateOTP(leadBean2)) {
				try {
					leadBean.setUserId(leadBean.getMobile());
					LeadStudentPortalBean leanBean2 = leadDAO.getLeadDetailsLocallyForMobile(leadBean);
					loginLogService.insertLoginDetails(leanBean2.getLeadId(), request);
				}catch(Exception exception) {
					logger.error(exception.getMessage());
				}
				
				return new ResponseEntity<String>("Successfully update verify code", header,HttpStatus.OK);
			}
			return new ResponseEntity<String>("Failed to update verify code", header,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>("Invalid otp found", header,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(value="/m/resendRequestOTP", method= RequestMethod.POST,consumes ="application/json",produces = "application/json")
	public ResponseEntity<String> resendRequestOTP(@RequestBody LeadStudentPortalBean leadBean,HttpServletRequest request,HttpServletResponse response) {
		String otp = generateRandomOTP();
		HttpHeaders header = new HttpHeaders();
		leadBean.setOtp(otp);
		if(leadDAO.updateOTP(leadBean)) {
			smsSender.sendRequestOTP(leadBean);
			return new ResponseEntity<String>("Successfully otp re-send",header,HttpStatus.OK);
		}
		return new ResponseEntity<String>("Faild to re-send OTP",header,HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	private String generateRandomOTP() {
		String numbers = "1234567890";
		Random random = new Random();
		String otp = "";
		for(int i = 0; i< 4 ; i++) {
			otp = otp + numbers.charAt(random.nextInt(numbers.length()));
	    }
		return otp;
	}
	
	@GetMapping("/admin/searchUserAuthorizationFormNew")
	public ModelAndView searchUserAuthorizationFormNew() {
		ModelAndView modelnView = new ModelAndView("jsp/searchUserAuthorizationFormNew");
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		UserAuthorizationStudentPortalBean userAuthorizationBean = pDao.getUserAuthorization("nelson");
		modelnView.addObject("userAuthorizationBean", userAuthorizationBean);
		List<String> allRoles = Arrays.asList("Examiner/Grader","Course Co-ordinator","Programme Co-ordinator","Course Mentor","Counsellor");
		List<String>allocated = Arrays.asList("Examiner/Grader","Course Co-ordinator","Programme Co-ordinator");
		List<String>available = new ArrayList<>();
		
		available = allRoles.stream().filter(l-> allocated.stream().noneMatch(m->m.equalsIgnoreCase(l))).collect(Collectors.toList());
		modelnView.addObject("available", available);
		modelnView.addObject("allocated", allocated);
		return modelnView;
	}
	
	 @RequestMapping(value = "/admin/downloadAuthorizationReport", method = { RequestMethod.GET, RequestMethod.POST })
		public ModelAndView downloadReport(HttpServletRequest request, HttpServletResponse response) {	
 			List<UserAuthorizationStudentPortalBean>listOfUserAuthorization = new ArrayList<UserAuthorizationStudentPortalBean>();
			try {
				listOfUserAuthorization = authorizationService.getAllUserAuthorization();
			} catch (Exception e) {
				// e.printStackTrace();
			}
 			
		  return new ModelAndView(userAuthorizationExcelView, "userAuthorizationList", listOfUserAuthorization);
		}
}
