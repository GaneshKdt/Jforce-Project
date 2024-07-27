package com.nmims.controllers;

import java.net.URLDecoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.LinkedInAddCertToProfileBean;
import com.nmims.beans.OpenBadgesIssuedBean;
import com.nmims.beans.OpenBadgesUsersBean;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.services.OpenBadgesService;
import com.nmims.factory.CertificateFactory;

@Controller
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OpenBadgesController extends BaseController {
	
	@Autowired
	private OpenBadgesService openBadgesService;
	
	@Autowired
	CertificateFactory certificateFactory;
	
	@Value( "${SERVER}" )
	private String SERVER;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value("${LINKED_IN_SHARE_IMAGES_PATH}")
	private String LINKED_IN_SHARE_IMAGES_PATH;
	
	@Value("${LINKED_IN_CLIENT_ID}")
	private String LINKED_IN_CLIENT_ID;
	
	@Value("${LINKED_IN_CLIENT_SECRET}")
	private String LINKED_IN_CLIENT_SECRET;
	
	@Value("${LINKED_IN_SCOPE}")
	private String LINKED_IN_SCOPE;
	
	@Value("${LINKED_IN_CODE}")
	private String LINKED_IN_CODE;
	
	@Value("${LINKED_IN_REDIRECT_URI}")
	private String LINKED_IN_REDIRECT_URI;

	@Value("${LINKED_IN_RANK_REDIRECT_URI}")
	private String LINKED_IN_RANK_REDIRECT_URI;
	
	@Value("${LINKED_IN_IMAGE_POST_REDIRECT_URI}")
	private String LINKED_IN_IMAGE_POST_REDIRECT_URI;
	
	private static final Logger logger = LoggerFactory.getLogger(OpenBadgesController.class);
	
	@GetMapping(value = "/student/myBadges")
	public ModelAndView getBadgesWeb(HttpServletRequest request, HttpServletResponse response) {
				
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		OpenBadgesUsersBean usersBean = new OpenBadgesUsersBean ();
//		ModelAndView modelnView = new ModelAndView("jsp/badges/myBadges");
		ModelAndView modelnView = new ModelAndView("templates/badges/myBadges");
		try {
			String sapid = (String)request.getSession().getAttribute("userId");
			StudentStudentPortalBean studentBean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
			if( "MBA - WX".equals(studentBean.getProgram()) || "MBA - X".equals(studentBean.getProgram()) 
	            	||	"DBSM".equals(studentBean.getProgram()) || "KTN".equals(studentBean.getProgram()) 
	            	||	"IBS".equals(studentBean.getProgram()) || "SSMM".equals(studentBean.getProgram()) 
	            	||	"MFB".equals(studentBean.getProgram()) || "EPBM".equals(studentBean.getProgram()) 
	            	||	"MPDV".equals(studentBean.getProgram())  
				) {
				return new ModelAndView("jsp/login");
			}
			
			usersBean = openBadgesService.getMyBadgeList(sapid, Integer.parseInt(studentBean.getConsumerProgramStructureId()));
			
			modelnView.addObject("mybadges", usersBean);
			modelnView.addObject("status", "success");
//			logger.error("myBadges: {}", modelnView);
			return modelnView;
			
		} catch(Exception e) {
//			e.printStackTrace();
			modelnView.addObject("mybadges", usersBean);
			modelnView.addObject("status", "error");
			return modelnView;
		}
	}
	
	@GetMapping(value = "/student/badgeDetails")
	public ModelAndView getBadgesDetailstWeb(HttpServletRequest request, HttpServletResponse response) {
		
		final String URL = "studentportal/credentials/public/badgedetails/";
		
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		LinkedInAddCertToProfileBean linkedInCredentials = new LinkedInAddCertToProfileBean();
		
		linkedInCredentials.setCode(LINKED_IN_CODE);
		linkedInCredentials.setClient_id(LINKED_IN_CLIENT_ID);
		linkedInCredentials.setClient_secret(LINKED_IN_CLIENT_SECRET);
		linkedInCredentials.setScope(LINKED_IN_SCOPE);
		linkedInCredentials.setRedirect_uri(SERVER_PATH + LINKED_IN_IMAGE_POST_REDIRECT_URI);
		
		OpenBadgesIssuedBean badgesDetails =  new OpenBadgesIssuedBean();
//		ModelAndView modelnView = new ModelAndView("jsp/badges/badgeDetails");
		ModelAndView modelnView = new ModelAndView("templates/badges/badgeDetails");
		try {
			
			String sapid = (String)request.getSession().getAttribute("userId");
			StudentStudentPortalBean studentBean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
			
			if("MBA - WX".equals(studentBean.getProgram()) || "MBA - X".equals(studentBean.getProgram()) 
	            	||	"DBSM".equals(studentBean.getProgram()) || "KTN".equals(studentBean.getProgram()) 
	            	||	"IBS".equals(studentBean.getProgram()) || "SSMM".equals(studentBean.getProgram()) 
	            	||	"MFB".equals(studentBean.getProgram()) || "EPBM".equals(studentBean.getProgram()) 
	            	||	"MPDV".equals(studentBean.getProgram()) 
				) {
				return new ModelAndView("jsp/login");
			}
			
			String uniquehash = URLDecoder.decode(request.getParameter("uniquehash"), "UTF-8");
			Integer badgeId = Integer.parseInt(request.getParameter("badgeId"));

			String awardedAt = URLDecoder.decode(request.getParameter("awardedAt"), "UTF-8");
			badgesDetails = openBadgesService.getBadgesDetails(uniquehash, badgeId,sapid, awardedAt);
		
			badgesDetails.getEvidenceBeanList().stream().map((element) -> {				
				element.setEvidenceValue(Jsoup.clean(element.getEvidenceValue(), Whitelist.none()));
				return element;
			}).collect(Collectors.toList());

			badgesDetails.setUrl(SERVER_PATH + URL);
			badgesDetails.setProductType("PG");

			OpenBadgesIssuedBean badgeLocation =  new OpenBadgesIssuedBean();
			badgeLocation = openBadgesService.getPublicBadgesDetails(uniquehash);
			modelnView.addObject("badgesDetails", badgesDetails);
			modelnView.addObject("awardedAt", awardedAt);
//			request.getSession().setAttribute("badgesDetails", badgesDetails);
//			request.getSession().setAttribute("studentBean", studentBean);
			modelnView.addObject("studentBean",studentBean);
			modelnView.addObject("badgeLocation", badgeLocation);
			modelnView.addObject("linkedinCredentials", linkedInCredentials);
			modelnView.addObject("serverPath", SERVER_PATH);
			return modelnView;
			
		} catch(Exception e) {
			modelnView.addObject("badgesDetails", badgesDetails);
			modelnView.addObject("status", "error");
			return modelnView;
		}
	}
	
	@GetMapping(value = "/public/badgedetails/{uniquehash}")
	public ModelAndView getShareableBadgesDetailstWeb(HttpServletRequest request, HttpServletResponse response, 
			@PathVariable String uniquehash) {
		OpenBadgesUsersBean badgesDetails =  new OpenBadgesUsersBean();
		ModelAndView modelnView = new ModelAndView("redirect:/credentials/public/badgedetails/"+uniquehash);
		try {			
//			badgesDetails = openBadgesService.getPublicBadgesDetails(uniquehash);
//			modelnView.addObject("uniquehash", uniquehash);
			return modelnView;
			
		}catch(Exception e) {
//			e.printStackTrace();
			modelnView.addObject("badgesDetails", badgesDetails);
			modelnView.addObject("status", "error");
			return modelnView;
		}
	}

	@GetMapping(value = "/credentials/public/badgedetails/{uniquehash}")
	public ModelAndView getShareableBadgesDetailstWebV2(HttpServletRequest request, HttpServletResponse response, 
			@PathVariable String uniquehash) {
		
		OpenBadgesUsersBean badgesDetails =  new OpenBadgesUsersBean();
				
//		ModelAndView modelnView = new ModelAndView("jsp/badges/publicBadgeDetails");
		ModelAndView modelnView = new ModelAndView("templates/badges/publicBadgeDetails");
		try {

			badgesDetails = openBadgesService.getPublicBadgesDetails(uniquehash);

			badgesDetails.getEvidenceBeanList().stream().map((element) -> {				
				element.setEvidenceValue(Jsoup.clean(element.getEvidenceValue(), Whitelist.none()));
				return element;
			}).collect(Collectors.toList());

			modelnView.addObject("badgesDetails", badgesDetails);

			String filepath = openBadgesService.getCertificatePath(uniquehash);			
			if(filepath != null || !filepath.isEmpty()) {
				String isLinkGeneratedFromFC = "Y";
				modelnView.addObject("isLinkGeneratedFromFC", isLinkGeneratedFromFC);
				modelnView.addObject("filepath", filepath);
			}
			return modelnView;

		}catch(Exception e) {
			//e.printStackTrace();
			modelnView.addObject("badgesDetails", badgesDetails);
			modelnView.addObject("status", "error");
			return modelnView;
		}
	}
	
//	Scheduler API START
	@GetMapping(value = "/runBadgeUserEntryScheduler")
	public ResponseEntity<HashMap<String, String>> runBadgeUserEntryScheduler() {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
//		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
//			response.put("result", "Not running runBagdeUserEntryScheduler since this is not tomcat4. This is "+SERVER);
//			return new ResponseEntity<>(response, headers, HttpStatus.OK);
//		}
		String result = openBadgesService.createBadgeUserEntry();
		response.put("result", result);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}

	@GetMapping(value = "/runAskQueryBadgeScheduler")
	public ResponseEntity<HashMap<String, String>> runAskQueryBadgeScheduler() {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runAskQueryBagdeScheduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		String result = openBadgesService.callAskQueryProcForAllStudent();
		response.put("result", result);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}

	@GetMapping(value = "/runSubmitAssignmentBadgeScheduler")
	public ResponseEntity<HashMap<String, String>> runSubmitAssignmentBadgeScheduler() {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runSubmitAssignmentBadgeScheduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		String result = openBadgesService.callSubmitAssignmentProcForAllStudent();
		response.put("result", result);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@GetMapping(value = "/runProgramCompletionBadgeScheduler")
	public ResponseEntity<HashMap<String, String>> runProgramCompletionBadgeScheduler() {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runProgramCompletionBadgeScheduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		String result = openBadgesService.callProgramCompletionBadgeForAllStudent();
		response.put("result", result);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}

	@GetMapping(value = "/runTopInAssignmentBadgeScheduler")
	public ResponseEntity<HashMap<String, String>> runAssignmentBadgeScheduler(
			@RequestParam(name = "examMonth") String examMonth,
			@RequestParam(name = "examYear") Integer examYear
			) {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runProgramCompletionBadgeScheduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		String result = openBadgesService.callTopInAssignmentBadgeForAllStudent(
				examMonth, examYear
				);
		response.put("result", result);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@GetMapping(value = "/runTopInAssignmentBadgeForAllCycle")
	public ResponseEntity<HashMap<String, String>> runAssignmentBadgeForAllCycle() {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runTopInAssignmentBadge since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		String result = openBadgesService.callTopInAssignmentBadgeForAllMonthYear();
		response.put("result", result);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}

	@GetMapping(value = "/runTopInSemesterBadgeScheduler")
	public ResponseEntity<HashMap<String, String>> runTopInSemesterBadgeScheduler(
			@RequestParam(name = "examMonth") String examMonth,
			@RequestParam(name = "examYear") Integer examYear
			) {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runProgramCompletionBadgeScheduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		String result = openBadgesService.callTopInSemesterBadgeForAllStudent(
				 examMonth, examYear );
		response.put("result", result);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);

	}
	
	@RequestMapping(value="/runReRegistrationBadgeSchduler",method={RequestMethod.GET})
	public ResponseEntity<HashMap<String, String>>  generateRegistrationLinks(){
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runReRegistrationBadgeSchduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		response =  openBadgesService.callReRegistrationForSemBadgeForAllStudent();
		
		
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}


	@GetMapping(value = "/runNgasceAlumniBadgeScheduler")
	public ResponseEntity<HashMap<String, String>> runNgasceAlumniBadgeScheduler() {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runProgramCompletionBadgeScheduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		HashMap<String,String> result = openBadgesService.runNgasceAlumniBadgeScheduler();
		
		return new ResponseEntity<>(result, headers, HttpStatus.OK);
	}
	
	@GetMapping(value = "/runTopInProgramBadgeSchduler")
	public ResponseEntity<HashMap<String, String>> runTopInProgramBadgeSchduler(@RequestParam(name = "examMonth") String examMonth,
			@RequestParam(name = "examYear") Integer examYear) {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
//		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
//			response.put("result", "Not running runProgramCompletionBadgeScheduler since this is not tomcat4. This is "+SERVER);
//			return new ResponseEntity<>(response, headers, HttpStatus.OK);
//		}
		response = openBadgesService.callTopInProgramBadgeForAllStudent(examMonth,examYear);
		
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value="/topInSubjectBadgeScheduler",method={RequestMethod.GET})
	public ResponseEntity<HashMap<String, String>>  generrateTopLinks(){
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runTopiInSubjectSchduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		response =  openBadgesService.callTopnInSubjectBadgeForAllStudent();
		
		
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value="/topInTEEBadgeScheduler",method={RequestMethod.GET})
	public ResponseEntity<HashMap<String, String>>  topInTEEBadgeScheduler( @RequestParam(required = true) String examMonth, @RequestParam(required = true) Integer examYear) {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runtopInTEEBadgeScheduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		response =  openBadgesService.callTopnInTEEBadgeForAllStudent(examMonth, examYear);

		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}

	/**
	 * Badge scheduler to assign 'Appeared for TEE' badge
	 * 
	 * @return Response Entity of updated data
	 * @author Swarup Singh Rajpurohit
	 */
	@RequestMapping(value = "/appearedForTEEBadgeScheduler", method = { RequestMethod.GET })
	public ResponseEntity<Map<String, String>> appearedForTEEBadgeScheduler(@RequestParam Integer acadYear,
			@RequestParam String acadMonth, @RequestParam Integer examYear, @RequestParam String examMonth) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, String> response = new HashMap<>();

		if (!"tomcat4".equalsIgnoreCase(SERVER)) {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("result",
					"Not running appearedForTEEBadgeScheduler since this is not tomcat4. This is " + SERVER);
			return new ResponseEntity<>(errorMap, headers, HttpStatus.OK);
		}

		response = openBadgesService.callAppearedTEEBadgeForAllStudent(acadMonth, acadYear, examMonth, examYear);

		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}

	@GetMapping(value = "/appearedForTEEBadgeSchedulerForAll")
	public ResponseEntity<HashMap<String, String>> appearedForTEEBadgeSchedulerForAll() {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if (!"tomcat4".equalsIgnoreCase(SERVER)) {
			response.put("result", "Not running runTopInAssignmentBadge since this is not tomcat4. This is " + SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		String result = openBadgesService.callAppearedInTEEBadgeForAllMonthYear();
		response.put("result", result);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	/**
	 * Badge scheduler to assign 'Portal Visit Streak' badge
	 * @param month
	 * @param year
	 * @return
	 * @author anilkumar.prajapati
	 * @throws ParseException 
	 */
	@RequestMapping(value="/portalVisitStreakBadgeScheduler",method={RequestMethod.GET})
	public ResponseEntity<HashMap<String, String>>  portalVisitStreakScheduler(@RequestParam String month, @RequestParam Integer year, 
		@RequestParam String tableNames) throws ParseException {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runPortalVisitStreakBadgeSchduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		response =  openBadgesService.callStreakForAllStudent(month, year, tableNames);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@RequestMapping(value="/portalVisitStreakBadge",method={RequestMethod.GET})
	public ResponseEntity<HashMap<String, String>> portalVisitStreak(@RequestParam String month, @RequestParam Integer year, 
		@RequestParam String sapId) throws ParseException {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runPortalVisitStreakBadgeSchduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		response =  openBadgesService.callStreakForStudent(month, year, sapId);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}
	
	@GetMapping(value = "/m/runLectureAttendanceBadgeScheduler")
	public ResponseEntity<HashMap<String, String>> runLectureAttendanceBadgeBadgeScheduler(@RequestParam(value="month") String month,@RequestParam(value="year") String year) {
		HashMap<String, String> response = new HashMap<String, String>();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");  
		if(!"tomcat4".equalsIgnoreCase(SERVER) ){
			response.put("result", "Not running runProgramCompletionBadgeScheduler since this is not tomcat4. This is "+SERVER);
			return new ResponseEntity<>(response, headers, HttpStatus.OK);
		}
		String result = openBadgesService.callLectureAttendanceBadgeForAllStudent(year, month);
		response.put("result", result);
		return new ResponseEntity<>(response, headers, HttpStatus.OK);
	}

}
