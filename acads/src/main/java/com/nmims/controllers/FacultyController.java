package com.nmims.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;


import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FacultyCourseBean;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.TimeTableDAO;

import com.nmims.helpers.ExcelHelper;

import com.nmims.interfaces.FacultyServiceInterface;
import com.nmims.helpers.AWSHelper;

@Controller
@CrossOrigin(origins="*", allowedHeaders="*")
public class FacultyController extends BaseController{

	@Autowired
	ApplicationContext act;

	@Value( "${FACULTY_FILES_PATH}" )
	private String FACULTY_FILES_PATH;

	@Autowired
	private AWSHelper awsHelper;

	@Value( "${FACULTY_FILES_PREVIEW_PATH}" )
	private String FACULTY_FILES_PREVIEW_PATH;
	
	@Value( "${EC_APPROVAL_PROOF_EXTENSION}" )
	private String EC_APPROVAL_PROOF_EXTENSION;
	
	@Value( "${EC_APPROVAL_PROOF_S3_URL}" )
	private String EC_APPROVAL_PROOF_S3_URL;
	
	@Value( "${EC_APPROVAL_PROOF_BUCKET}" )
	private String EC_APPROVAL_PROOF_BUCKET;
	
	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST; 
	
	@Autowired
	FacultyServiceInterface facultyService;

	private static final Logger logger = LoggerFactory.getLogger(FacultyController.class);

	private ArrayList<String> facultyList = null;
	private ArrayList<String> activeFacultyNameAndIdList = null;
	private ArrayList<String> subjectList = null;
	private ArrayList<String> feedbackResponseList = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7"));

	private final int pageSize = 10;

	private ArrayList<String> rolesForAllocation = new ArrayList<String>(Arrays.asList("Assignment Question Preparation","Assignment Evaluation","TEE Question Preparation","TEE Evaluation","Lecture Delivery"));
	private ArrayList<String> yearList = new ArrayList<String>(
			Arrays.asList("2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017"));
	private ArrayList<String> examMonthList = new ArrayList<String>(Arrays.asList("Apr","Jun","Sep","Dec"));
	private ArrayList<String> acadMonthList = new ArrayList<String>(Arrays.asList("Jan","Jul"));

	private ArrayList<String> facultyName = null;
	private static final boolean NoOption = false;
	@ModelAttribute("activeFacultyNameAndIdList")
	public ArrayList<String> getAllFacultiesListNameAndId(){
		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		this.activeFacultyNameAndIdList = fDao.getAllFacultiesListNameAndId();
		return this.activeFacultyNameAndIdList;
	}


	public FacultyController(){
	}

	public ArrayList<String> getFacultyList(){
		//if(this.facultyList == null){
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		this.facultyList = dao.getAllFaculties();
		//}
		return facultyList;
	}

	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}


	@ModelAttribute("facultyName")
	public ArrayList<String> getFacultyNameList(){

		if(this.facultyName  == null){
			FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
			this.facultyName = dao.getFacultyNameList();
		}
		return facultyName;

	}

	@RequestMapping(value = "/admin/addFacultyForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String addFacultyForm(HttpServletRequest request, HttpServletResponse respnse,Model m) {
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		FacultyAcadsBean faculty = new FacultyAcadsBean();

		if (!checkSession(request, respnse)) {
			return "studentPortalRediret";
		}
		
		
		List<String> programTypes = dao.getProgramTypes();
		List<String> programNames = dao.getAllProgramNames();
		m.addAttribute("faculty",faculty);
		m.addAttribute("programTypes",programTypes);
		m.addAttribute("programNames",programNames);
		m.addAttribute("ecApprovalProofExt", EC_APPROVAL_PROOF_EXTENSION);
		m.addAttribute("ecApprovalProofPath", EC_APPROVAL_PROOF_S3_URL);
		return "addFaculty";
	}

	@RequestMapping(value="/admin/getProgramNames/{type}", method = RequestMethod.GET)
	public ResponseEntity<List<String>> getProgramNames(HttpServletRequest request, @PathVariable String type){
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");

		List<String> programNames = dao.getProgramNames(type);
		return new ResponseEntity(programNames, HttpStatus.OK);
	}
 
	@RequestMapping(value = "/admin/addFaculty", method = RequestMethod.POST)
	public ModelAndView addFaculty(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyAcadsBean faculty){
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		
		ModelAndView modelnView = new ModelAndView("faculty");
		
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		
		
		String userId = (String)request.getSession().getAttribute("userId_acads");
		
		String fileName = faculty.getFacultyImageFileData().getOriginalFilename(); 
		String consentFormFileName = faculty.getFacultyConsentFormData().getOriginalFilename();
		
		//--- EC Approval Proof multipart filename and original filename ---//
		MultipartFile ecApprovalProofURL = faculty.getEcApprovalProof();
		String ecApprovalProofOriginalFileName = faculty.getEcApprovalProof().getOriginalFilename();
		String ecApprovalProofFilename = "EC_Approval_Proof_" + faculty.getFacultyId() + "_" + RandomStringUtils.randomAlphanumeric(6) + "." + FilenameUtils.getExtension(ecApprovalProofOriginalFileName);
		
		modelnView.addObject("filename",fileName);
		
		faculty.setCreatedBy(userId);
		List<String> programTypes = dao.getProgramTypes();
		List<String> programNames = dao.getAllProgramNames();

		
		//Checking duplicate email in database
		String emailId= faculty.getEmail();
		String facultyId = faculty.getFacultyId();
		int emailpresent= dao.checkIfEmailPresent(emailId,facultyId);
		int mobilepresent = dao.checkIfDuplicateMobilePresent(faculty.getMobile(), facultyId);
		
		if(emailpresent>0)
		{
			return addFacultyErrorPage(request, response, faculty, programNames, programTypes, "Duplicate Email Id not allowed");
		}
		
		
		if(faculty.getTitle().equals("Grader")) {
		if(mobilepresent>0)
		{
			return addFacultyErrorPage(request, response, faculty, programNames, programTypes, "Duplicate Mobile Number not allowed");
		}
		}
		
		
		
		
		if(!faculty.getTitle().equals("Grader")) {
		
		
		if (fileName != null && fileName.endsWith(".jpg")) {
			String filePath = uploadFile(faculty, "Photo", faculty.getFacultyImageFileData());
			if (filePath != null) {
				faculty.setImgUrl(filePath);
			}else{
				return addFacultyErrorPage(request, response, faculty, programNames, programTypes, "Error in uploading profile picture");
			}
		}else{
			return addFacultyErrorPage(request, response, faculty, programNames, programTypes, "File type not supported. Please upload .jpg file.");
		}
		
		}
		if (consentFormFileName != null && consentFormFileName.endsWith(".pdf")) {
			String filePath = uploadFile(faculty, "ConsentForm", faculty.getFacultyConsentFormData());
			if (filePath != null) {
				faculty.setConsentFormUrl(filePath);
			}else{
				return addFacultyErrorPage(request, response, faculty, programNames, programTypes, "Error in uploading Consent Form");
			}
		}

		if(getFacultyList().contains(faculty.getFacultyId())){
			return addFacultyErrorPage(request, response, faculty, programNames, programTypes, "Faculty ID is already in use. Please use a different faculty ID");
		}
		
		//--- get original filename extension ---//
		String extension = "."+FilenameUtils.getExtension(ecApprovalProofFilename);
		if(!EC_APPROVAL_PROOF_EXTENSION.contains(extension)) {
			return addFacultyErrorPage(request, response, faculty, programNames, programTypes, "EC approval proof file type not supported. Please upload another file.");
		}
				
		//--- add new ec approval proof ---//
		if(!StringUtils.isBlank(ecApprovalProofFilename) && !StringUtils.isBlank(FilenameUtils.getExtension(ecApprovalProofOriginalFileName))) {
			awsHelper.uploadFile(ecApprovalProofURL, "Faculty/EC_Approval_Proof/", EC_APPROVAL_PROOF_BUCKET, "Faculty/EC_Approval_Proof/"+ecApprovalProofFilename);
			faculty.setEcApprovalProofUrl(ecApprovalProofFilename);
		}
				
		if(StringUtils.isBlank(faculty.getEcApprovalDate())) {
			faculty.setEcApprovalDate(null);
		}

		if (ENVIRONMENT.equalsIgnoreCase("PROD")){
			
			LDAPDao ldapDao = (LDAPDao)act.getBean("ldapdao");
			PersonAcads p = mapFacultyToPerson(faculty);
			ldapDao.createUser(p, "Faculty");

			if(p.isErrorRecord()){
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", p.getErrorMessage());
				return modelnView;
			}
		}
		
		String message =  dao.insertFaculty(faculty);
		if ("success".equalsIgnoreCase(message)) {
			modelnView.addObject("faculty", faculty);
            modelnView.addObject("selectedProgGroups",faculty.getProgramGroup());
			modelnView.addObject("selectedProgNames",faculty.getProgramName());
			modelnView.addObject("programTypes",programTypes);
			modelnView.addObject("programNames",programNames);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Faculty details saved successfully & User created in LDAP with ID: "
					+ faculty.getFacultyId() + " and Password: "+faculty.getPassword());
			
		}else{
			
			modelnView.addObject("faculty", faculty);
            modelnView.addObject("selectedProgGroups",faculty.getProgramGroup());
			modelnView.addObject("selectedProgNames",faculty.getProgramName());
			modelnView.addObject("programTypes",programTypes);
			modelnView.addObject("programNames",programNames);
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Error while updating data. "+message);
		}
		
		return modelnView;
	}

//	private String uploadFacultyProfilePic(FacultyBean bean, String facultyId){
//		
//		String errorMessage = null;
//		InputStream inputStream = null;   
//		OutputStream outputStream = null;   
//		
//		CommonsMultipartFile file = bean.getFileData3();
//		String fileName = file.getOriginalFilename();   
//		
//		
//		String todayAsString = new SimpleDateFormat("ddMMyyyy").format(new Date());
//		fileName = facultyId +  "_" + todayAsString + ".jpg";
//		
//		
//		try {
//			inputStream = file.getInputStream();   
//			String filePath = FACULTY_PHOTOS_PATH+fileName;
//			File folderPath = new File(FACULTY_PHOTOS_PATH);
//			
//			if (!folderPath.exists()) {
//				boolean created = folderPath.mkdirs();
//			}   
//			
//			File newFile = new File(filePath);   
//
//			outputStream = new FileOutputStream(newFile);   
//			int read = 0;   
//			byte[] bytes = new byte[1024];   
//
//			while ((read = inputStream.read(bytes)) != -1) {   
//				outputStream.write(bytes, 0, read);   
//			}
//			outputStream.close();
//			inputStream.close();
//			
//			return filePath;
//
//		} catch (Exception e) {
//			errorMessage = "Error in uploading file";
//			  
//			return null;
//		}
//	}

	private PersonAcads mapFacultyToPerson(FacultyAcadsBean faculty) {
		PersonAcads p = new PersonAcads();
		p.setFirstName(faculty.getFirstName());
		p.setLastName(faculty.getLastName());
		p.setUserId(faculty.getFacultyId());
		p.setPassword(faculty.getPassword());
		p.setEmail(faculty.getEmail());
		p.setProgram("NA");
		p.setContactNo("9999999999");
		p.setAltContactNo("9999999999");
		p.setPostalAddress("NA");

		return p;
	}

	@RequestMapping(value = "/admin/editFaculty", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editFaculty(HttpServletRequest request, HttpServletResponse response, Model m){
		ModelAndView modelnView = new ModelAndView("addFaculty");

		String id = request.getParameter("id");
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		FacultyAcadsBean faculty = dao.findByName(id);
		
		List<String> programTypes = dao.getProgramTypes();
		List<String> programNames = dao.getAllProgramNames();
		
		modelnView.addObject("faculty", faculty);
		modelnView.addObject("selectedProgGroups",faculty.getProgramGroup());
		modelnView.addObject("selectedProgNames",faculty.getProgramName());
		modelnView.addObject("programTypes",programTypes);
		modelnView.addObject("programNames",programNames);
		modelnView.addObject("ecApprovalProofExt", EC_APPROVAL_PROOF_EXTENSION);
		modelnView.addObject("ecApprovalProofPath", EC_APPROVAL_PROOF_S3_URL);
		m.addAttribute("faculty", faculty);
		request.setAttribute("edit", "true");
		return modelnView;
	}

	@RequestMapping(value = "/admin/updateFaculty", method = RequestMethod.POST)
	public ModelAndView updateFaculty(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyAcadsBean faculty_Bean){
		ModelAndView modelnView = new ModelAndView("faculty");
		String userId = (String)request.getSession().getAttribute("userId_acads");
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		
		FacultyAcadsBean oldFaculty_Bean = dao.findfacultyByFacultyId(faculty_Bean.getFacultyId());;
		List<String> programTypes = dao.getProgramTypes();
		List<String> programNames = dao.getAllProgramNames();
		
		try {
		//do not update duplicate email id
		String emailId= faculty_Bean.getEmail();
		String oldFacultyId = faculty_Bean.getFacultyId();
		int emailpresent= dao.checkIfEmailPresent(emailId,oldFacultyId);
		int mobilepresent= dao.checkIfDuplicateMobilePresent(faculty_Bean.getMobile(),oldFacultyId);
		
		String fileName = faculty_Bean.getFacultyImageFileData().getOriginalFilename();
		
		//--- EC Approval Proof multipart filename and original filename ---//
		MultipartFile ecApprovalProofURL = faculty_Bean.getEcApprovalProof();
		String ecApprovalProofOriginalFileName = faculty_Bean.getEcApprovalProof().getOriginalFilename();
		String ecApprovalProofFilename = "EC_Approval_Proof_" + faculty_Bean.getFacultyId() + "_" + RandomStringUtils.randomAlphanumeric(6) + "." + FilenameUtils.getExtension(ecApprovalProofOriginalFileName);
		
		if(emailpresent > 0)
		{
			return updateFacultyErrorPage(request, response, faculty_Bean, programNames, programTypes, "Duplicate Email Id not allowed");
		}
		
		if(!StringUtils.isBlank(fileName)) {
			if (fileName != null && fileName.endsWith(".jpg")) {
				String filePath = uploadFile(faculty_Bean, "Photo", faculty_Bean.getFacultyImageFileData());
				if (filePath != null) {
					faculty_Bean.setImgUrl(filePath);
				}else{
					return updateFacultyErrorPage(request, response, faculty_Bean, programNames, programTypes, "Error in uploading profile picture");
				}
			}else{
				return updateFacultyErrorPage(request, response, faculty_Bean, programNames, programTypes, "File type not supported. Please upload .jpg file.");
			}
		}
		
		if(faculty_Bean.getTitle().equals("Grader")) {
			
		if(mobilepresent >0)
		{
			return updateFacultyErrorPage(request, response, faculty_Bean, programNames, programTypes, "Duplicate Mobile No. not allowed");
		}
		
		}
		
		
				///
		if(!faculty_Bean.getTitle().equals("Grader")) {
			
		if (!faculty_Bean.getFacultyImageFileData().isEmpty()) {
//			if (StringUtils.isNullOrEmpty(oldFaculty_Bean.getImgUrl())) {
				String filePath = uploadFile(faculty_Bean, "Photo", faculty_Bean.getFacultyImageFileData());
				if (filePath != null) {
					faculty_Bean.setImgUrl(filePath);
				} else{
					return updateFacultyErrorPage(request, response, faculty_Bean, programNames, programTypes, "Error in uploading profile picture");
				}
//			}
		}else{
			faculty_Bean.setImgUrl(oldFaculty_Bean.getImgUrl());
		}
		
		}

		if (!faculty_Bean.getFacultyConsentFormData().isEmpty()) {
//			if (StringUtils.isNullOrEmpty(oldFaculty_Bean.getConsentFormUrl())) {
				String filePath = uploadFile(faculty_Bean, "ConsentForm", faculty_Bean.getFacultyConsentFormData());
				if (filePath != null) {
					faculty_Bean.setConsentFormUrl(filePath);
				} else{
					return updateFacultyErrorPage(request, response, faculty_Bean, programNames, programTypes, "Error in uploading Consent Form");
				}
//			}
		}else{
			faculty_Bean.setConsentFormUrl(oldFaculty_Bean.getConsentFormUrl());
		}
		
		faculty_Bean.setLastModifiedBy(userId);
		
		//--- get original filename extension ---//
		String extension = "."+FilenameUtils.getExtension(ecApprovalProofFilename);
		if(!EC_APPROVAL_PROOF_EXTENSION.contains(extension)) {
					
			return updateFacultyErrorPage(request, response, faculty_Bean, programNames, programTypes, "EC approval proof file type not supported. Please upload another file.");
		}
		
		//--- add new ec approval proof ---//
		if(!StringUtils.isBlank(ecApprovalProofFilename) && !StringUtils.isBlank(FilenameUtils.getExtension(ecApprovalProofOriginalFileName))) {
			awsHelper.uploadFile(ecApprovalProofURL, "Faculty/EC_Approval_Proof/", EC_APPROVAL_PROOF_BUCKET, "Faculty/EC_Approval_Proof/"+ecApprovalProofFilename);
			faculty_Bean.setEcApprovalProofUrl(ecApprovalProofFilename);
		}
		
		if(StringUtils.isBlank(faculty_Bean.getEcApprovalDate())) {
			faculty_Bean.setEcApprovalDate(null);
		}
			
		
			dao.updateFaculty(faculty_Bean);
	
			faculty_Bean = dao.findByName(faculty_Bean.getId());
			modelnView.addObject("faculty", faculty_Bean);
	        modelnView.addObject("selectedProgGroups",faculty_Bean.getProgramGroup());
			modelnView.addObject("selectedProgNames",faculty_Bean.getProgramName());
			modelnView.addObject("programTypes",programTypes);
			modelnView.addObject("programNames",programNames);
			request.setAttribute("success","true");
			request.setAttribute("successMessage","Faculty details updated successfully");
		}catch(Exception e) {
			return updateFacultyErrorPage(request, response, faculty_Bean, programNames, programTypes, "Error while updating data.");
		}

		return modelnView;
	}

	@RequestMapping(value = "/admin/deleteFaculty", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteFaculty(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyAcadsBean faculty){
		ModelAndView modelnView = new ModelAndView("faculty");

		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		dao.deactivateFaculty(faculty);

		faculty = dao.findByName(faculty.getId());
		modelnView.addObject("faculty", faculty);

		request.setAttribute("success","true");
		request.setAttribute("successMessage","Faculty De-Activated successfully");

		return modelnView;
	}

	@RequestMapping(value = "/admin/viewFacultyDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewStudentDetails(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("faculty");
		String id = request.getParameter("id");

		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		FacultyAcadsBean faculty = dao.findByName(id);

		modelnView.addObject("faculty", faculty);

		return modelnView;
	}

	@RequestMapping(value = "/admin/searchFacultyForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String searchFacultyForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FacultyAcadsBean searchBean = new FacultyAcadsBean();
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		Map<String,String> facultyIdMap = dao.getFacultyMap();
		request.getSession().setAttribute("facultyIdMap", facultyIdMap);
		m.addAttribute("searchBean",searchBean);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("feedbackResponseList", feedbackResponseList);
		m.addAttribute("facultyIdMap", facultyIdMap);

		return "searchFaculty";
	}
	//Newly Added//
	@RequestMapping(value="/admin/allocateFacultyToRoleForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView allocateFacultyToRoleForm(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("allocateFacultyToRole");
		modelnView.addObject("facultyBean",new FacultyAcadsBean());
		ArrayList<String> getFacultyAllocatedList = new ArrayList<String>();

		modelnView.addObject("rolesForAllocationList", rolesForAllocation);
		modelnView.addObject("examMonthList", examMonthList);
		modelnView.addObject("acadMonthList", acadMonthList);
		modelnView.addObject("yearList",yearList);
		modelnView.addObject("getFacultyAllocatedList",getFacultyAllocatedList);

		return modelnView;
	}

	@RequestMapping(value="/admin/allocateFacultyToRole",method={RequestMethod.POST})
	public ModelAndView allocateFacultyToRole(@ModelAttribute FacultyAcadsBean facultyBean,HttpServletRequest request, HttpServletResponse response){

		ModelAndView modelAndView = new ModelAndView("allocateFacultyToRole");
		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		ArrayList<FacultyAcadsBean> facultyRecordsForBatchInsert = null;
		ArrayList<String> getFacultyAllocatedList = new ArrayList<String>();

		modelAndView.addObject("rolesForAllocationList", rolesForAllocation);
		modelAndView.addObject("examMonthList", examMonthList);
		modelAndView.addObject("acadMonthList", acadMonthList);
		modelAndView.addObject("yearList",yearList);
		modelAndView.addObject("getFacultyAllocatedList",getFacultyAllocatedList);

		if(facultyBean.getFacultyAllocated() == null){
			setError(request,"Please Allot Faculties");
			modelAndView.addObject("facultyBean", new FacultyAcadsBean());
			return modelAndView;
		}else{
			facultyRecordsForBatchInsert = generateBatchInsertListForFaculty(facultyBean);
		}

		try{
			fDao.batchInsertRecordFacultyRole(facultyRecordsForBatchInsert);
			setSuccess(request,"Successfully Allocated Faculty Role");
			modelAndView.addObject("facultyBean", new FacultyAcadsBean());
			return modelAndView;
		}catch(Exception e){
			  
			setError(request,"Error in Updating Faculty Role");
			modelAndView.addObject("facultyBean", new FacultyAcadsBean());
			return modelAndView;
		}

	}
	@RequestMapping(value="/admin/searchFacultyAllocationForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView searchFacultyAllocationForm(HttpServletRequest request, HttpServletResponse response){
		/*if(!checkSession(request, response)){
		return new ModelAndView("studentPortalRediret");
	    }*/
		ModelAndView modelAndView = new ModelAndView("searchFacultyAllocation");
		modelAndView.addObject("faculty",new FacultyAcadsBean());
		modelAndView.addObject("rolesForAllocationList", rolesForAllocation);
		modelAndView.addObject("examMonthList", examMonthList);
		modelAndView.addObject("acadMonthList", acadMonthList);
		modelAndView.addObject("yearList",yearList);
		return modelAndView;
	}
	@RequestMapping(value="/admin/saveFacultyRating",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String saveFacultyRating(@RequestParam String value, @RequestParam Long pk,HttpServletRequest request){

		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		String userId = (String)request.getSession().getAttribute("userId_acads");
		try {
			fDao.saveFacultyRating(value,userId,pk);
			return "{\"status\": \"success\", \"msg\": \"Status saved successfully!\"}";
		} catch (Exception e) {
			  
			return "{\"status\": \"error\", \"msg\": \"Error in saving Status!\"}";
		}

	}
	@RequestMapping(value="/admin/searchFacultyAllocation",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView searchFacultyAllocation(@ModelAttribute FacultyAcadsBean faculty,HttpServletRequest request, HttpServletResponse response){
		/*if(!checkSession(request, response)){
		return new ModelAndView("studentPortalRediret");
	    }*/
		ModelAndView modelAndView = new ModelAndView("searchFacultyAllocation");
		modelAndView.addObject("rolesForAllocationList", rolesForAllocation);
		modelAndView.addObject("examMonthList", examMonthList);
		modelAndView.addObject("acadMonthList", acadMonthList);
		modelAndView.addObject("yearList",yearList);
		modelAndView.addObject("faculty",faculty);
		request.getSession().setAttribute("faculty", faculty);

		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		PageAcads<FacultyAcadsBean> page = fDao.getFacultyAllocationPage(1, pageSize, faculty);
		List<FacultyAcadsBean> facultyAllocationList = page.getPageItems();

		if(facultyAllocationList ==null || facultyAllocationList.isEmpty())
		{
			setError(request,"No Records Found ...!!");
			return modelAndView;
		}

		modelAndView.addObject("rowCount", page.getRowCount());
		modelAndView.addObject("facultyAllocationList", facultyAllocationList);
		return modelAndView;
	}

	@RequestMapping(value = "/admin/searchFacultyAllocationPage", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchFacultyAllocationPage(HttpServletRequest request, HttpServletResponse response){
		/*if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}*/
		ModelAndView modelnView = new ModelAndView("searchStudentRegistrations");
		FacultyAcadsBean faculty = (FacultyAcadsBean)request.getSession().getAttribute("faculty");

		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		PageAcads<FacultyAcadsBean> page = fDao.getFacultyAllocationPage(pageNo, pageSize, faculty);
		List<FacultyAcadsBean> facultyAllocationList = page.getPageItems();

		modelnView.addObject("facultyAllocationList", facultyAllocationList);
		modelnView.addObject("page", page);

		modelnView.addObject("facultyAllocationList", facultyAllocationList);
		modelnView.addObject("rowCount", page.getRowCount());

		if(facultyAllocationList ==null || facultyAllocationList.isEmpty()){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No records found.");
		}
		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadFacultyAllocationReport", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadFacultyAllocationReport(HttpServletRequest request, HttpServletResponse response){
		/*if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}*/
		FacultyAcadsBean faculty = (FacultyAcadsBean)request.getSession().getAttribute("faculty");

		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		PageAcads<FacultyAcadsBean> page = fDao.getFacultyAllocationPage(1, Integer.MAX_VALUE, faculty);
		List<FacultyAcadsBean> facultyAllocationList = page.getPageItems();

		return new ModelAndView("FacultyAllocationExcelView","facultyAllocationList",facultyAllocationList);
	}

	public ArrayList<FacultyAcadsBean> generateBatchInsertListForFaculty(FacultyAcadsBean facultyBean){
		ArrayList<FacultyAcadsBean> listOfFaculty = new ArrayList<FacultyAcadsBean>();
		List<String> listOfFacultyId = Arrays.asList(facultyBean.getFacultyAllocated().split("\\s*,\\s*"));
		for(String facultyId : listOfFacultyId){
			FacultyAcadsBean faculty = new FacultyAcadsBean();
			faculty.setFacultyAllocated(facultyId);
			faculty.setRoleForAllocation(facultyBean.getRoleForAllocation());
			faculty.setAcadMonth(facultyBean.getAcadMonth());
			faculty.setExamMonth(facultyBean.getExamMonth());
			faculty.setAcadYear(facultyBean.getAcadYear());
			faculty.setExamYear(facultyBean.getExamYear());
			listOfFaculty.add(faculty);
		}
		return listOfFaculty;

	}


	//End//


	//Edited on 12/9/2017 ----Start
	@RequestMapping(value = "/admin/searchFaculty",  method = {RequestMethod.POST})
	public ModelAndView searchFaculty(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyAcadsBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchFaculty");

		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		request.getSession().setAttribute("searchBean_acads", searchBean);
		Map<String,String> facultyIdMap =(Map<String,String>)request.getSession().getAttribute("facultyIdMap");

		/*Page<FacultyBean> page = dao.getFacultyPage(1, pageSize, searchBean);
			List<FacultyBean> facultyList = page.getPageItems();*/
		List<FacultyAcadsBean> facultyList = dao.getFacultyPage(searchBean);
		modelnView.addObject("facultyIdMap",facultyIdMap);
		//modelnView.addObject("page", page);
		//modelnView.addObject("rowCount", page.getRowCount());
		int rowCount = facultyList.size();
		modelnView.addObject("rowCount",rowCount );
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("feedbackResponseList",feedbackResponseList );
		modelnView.addObject("ecApprovalProofPath", EC_APPROVAL_PROOF_S3_URL);
		if(facultyList == null || facultyList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Faculty Records Found.");
		}
		modelnView.addObject("facultyList", facultyList);
		modelnView.addObject("downloadFaculties", "downloadFaculties");
		return modelnView;
	}

	@RequestMapping(value = "/admin/searchFacultyWithPeerRatings",  method = {RequestMethod.POST})
	public ModelAndView searchFacultyWithPeerRatings(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyAcadsBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchFaculty");

		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		request.getSession().setAttribute("searchBean_acads", searchBean);
		Map<String,String> facultyIdMap =(Map<String,String>)request.getSession().getAttribute("facultyIdMap");

		/*		Page<FacultyBean> page = dao.getFacultyPageWithPeerRatings(1, pageSize, searchBean);
			List<FacultyBean> facultyList = page.getPageItems();*/
		List<FacultyAcadsBean> facultyList = dao.getFacultyPageWithPeerRatings(searchBean);
		modelnView.addObject("facultyIdMap",facultyIdMap);
		//modelnView.addObject("page", page);
		//modelnView.addObject("rowCount", page.getRowCount());
		int rowCount = facultyList.size();
		modelnView.addObject("rowCount", rowCount);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("feedbackResponseList",feedbackResponseList );
		if(facultyList == null || facultyList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Faculty Records Found.");
		}
		modelnView.addObject("facultyList", facultyList);
		modelnView.addObject("downloadFacultyPeerRating", "downloadFacultiesWithPeerReviewRatings"); 
		//request.getSession().setAttribute("downloadFacultyPeerRating", "downloadFacultiesWithPeerReviewRatings");
		return modelnView;
	}

	@RequestMapping(value = "/admin/searchFacultyWithStudentRatings",  method = {RequestMethod.POST})
	public ModelAndView searchFacultyWithStudentRatings(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyAcadsBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchFaculty");

		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		request.getSession().setAttribute("searchBean_acads", searchBean);
		Map<String,String> facultyIdMap =(Map<String,String>)request.getSession().getAttribute("facultyIdMap");

		/*Page<FacultyBean> page = dao.getFacultyPageWithStudentRatings(1, pageSize, searchBean);
			List<FacultyBean> facultyList = page.getPageItems();*/
		List<FacultyAcadsBean> facultyList = dao.getFacultyPageWithStudentRatings(searchBean);
		modelnView.addObject("facultyIdMap",facultyIdMap);
		//modelnView.addObject("page", page);
		//modelnView.addObject("rowCount", page.getRowCount());
		int rowCount = facultyList.size();
		modelnView.addObject("rowCount", rowCount);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("feedbackResponseList",feedbackResponseList );
		if(facultyList == null || facultyList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Faculty Records Found.");
		}
		modelnView.addObject("facultyList", facultyList);
		modelnView.addObject("downloadFacultiesStudentRatings", "downloadFacultiesWithStudentReviewRatings");
		return modelnView;
	}

	@RequestMapping(value = "/admin/searchFacultyWithBothRatings",  method = {RequestMethod.POST})
	public ModelAndView searchFacultyWithBothRatings(HttpServletRequest request, HttpServletResponse response, @ModelAttribute FacultyAcadsBean searchBean){
		ModelAndView modelnView = new ModelAndView("searchFaculty");

		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		request.getSession().setAttribute("searchBean_acads", searchBean);
		Map<String,String> facultyIdMap =(Map<String,String>)request.getSession().getAttribute("facultyIdMap");

		//Page<FacultyBean> page = dao.getFacultyPageWithBothRatings(1, pageSize, searchBean);
		//List<FacultyBean> facultyList = page.getPageItems();
		List<FacultyAcadsBean> facultyList = dao.getFacultyPageWithBothRatings(searchBean);
		modelnView.addObject("facultyIdMap",facultyIdMap);
		//modelnView.addObject("page", page);
		//modelnView.addObject("rowCount", page.getRowCount());
		int rowCount = facultyList.size();
		modelnView.addObject("rowCount", rowCount);
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("feedbackResponseList",feedbackResponseList );
		if(facultyList == null || facultyList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Faculty Records Found.");
		}
		modelnView.addObject("facultyList", facultyList);
		modelnView.addObject("downloadFacultiesBothRatings", "downloadFacultiesWithBothRatings");
		return modelnView;
	}

	//Edited on 12/9/2017 ------End

	@RequestMapping(value = "/admin/searchFacultyPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchFacultyPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchFaculty");

		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		FacultyAcadsBean searchBean = (FacultyAcadsBean)request.getSession().getAttribute("searchBean_acads");
		/*int pageNo = Integer.parseInt(request.getParameter("pageNo"));

			Page<FacultyBean> page = dao.getFacultyPage(pageNo, pageSize, searchBean);*/
		List<FacultyAcadsBean> facultyList = dao.getFacultyPage(searchBean);
		int rowCount = facultyList.size();

		//modelnView.addObject("page", page);
		modelnView.addObject("rowCount",rowCount );
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("feedbackResponseList", feedbackResponseList);

		if(facultyList == null || facultyList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Faculty Records Found.");
		}


		modelnView.addObject("facultyList", facultyList);
		return modelnView;
	}

	@RequestMapping(value = "/admin/downloadFaculties", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadFaculties(HttpServletRequest request, HttpServletResponse response) {
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		FacultyAcadsBean searchBean = (FacultyAcadsBean)request.getSession().getAttribute("searchBean_acads");

		/*Page<FacultyBean> page = dao.getFacultyPage(1, Integer.MAX_VALUE, searchBean);
			List<FacultyBean> facultyList = page.getPageItems();*/

		List<FacultyAcadsBean> facultyList = dao.getFacultyPage(searchBean);

		return new ModelAndView("facultyExcelView","facultyList",facultyList);
	}

	@RequestMapping(value = "/admin/downloadFacultiesWithPeerReviewRatings", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadFacultiesWithPeerReviewRatings(HttpServletRequest request, HttpServletResponse response) {
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		FacultyAcadsBean searchBean = (FacultyAcadsBean)request.getSession().getAttribute("searchBean_acads");

		/*Page<FacultyBean> page = dao.getFacultyPage(1, Integer.MAX_VALUE, searchBean);
			List<FacultyBean> facultyList = page.getPageItems();*/

		List<FacultyAcadsBean> facultyList = dao.getFacultyPageWithPeerRatings(searchBean);

		return new ModelAndView("facultyExcelView","facultyList",facultyList);
	}

	@RequestMapping(value = "/admin/downloadFacultiesWithStudentReviewRatings", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadFacultiesWithStudentReviewRatings(HttpServletRequest request, HttpServletResponse response) {
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		FacultyAcadsBean searchBean = (FacultyAcadsBean)request.getSession().getAttribute("searchBean_acads");

		/*Page<FacultyBean> page = dao.getFacultyPage(1, Integer.MAX_VALUE, searchBean);
			List<FacultyBean> facultyList = page.getPageItems();*/

		List<FacultyAcadsBean> facultyList = dao.getFacultyPageWithStudentRatings(searchBean);

		return new ModelAndView("facultyExcelView","facultyList",facultyList);
	}

	@RequestMapping(value = "/admin/downloadFacultiesWithBothRatings", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadFacultiesWithBothRatings(HttpServletRequest request, HttpServletResponse response) {
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		FacultyAcadsBean searchBean = (FacultyAcadsBean)request.getSession().getAttribute("searchBean_acads");

		/*Page<FacultyBean> page = dao.getFacultyPage(1, Integer.MAX_VALUE, searchBean);
			List<FacultyBean> facultyList = page.getPageItems();*/

		List<FacultyAcadsBean> facultyList = dao.getFacultyPageWithBothRatings(searchBean);

		return new ModelAndView("facultyExcelView","facultyList",facultyList);
	}

	/*	@RequestMapping(value = "/searchFacultyPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView searchFacultyPage(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("searchFaculty");

		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		FacultyBean searchBean = (FacultyBean)request.getSession().getAttribute("searchBean_acads");
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));

		Page<FacultyBean> page = dao.getFacultyPage(pageNo, pageSize, searchBean);
		List<FacultyBean> facultyList = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		modelnView.addObject("searchBean", searchBean);
		modelnView.addObject("feedbackResponseList", feedbackResponseList);

		if(facultyList == null || facultyList.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Faculty Records Found.");
		}


		modelnView.addObject("facultyList", facultyList);
		return modelnView;
	}*/

	/*	@RequestMapping(value = "/downloadFaculties", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downloadFaculties(HttpServletRequest request, HttpServletResponse response) {
		FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
		FacultyBean searchBean = (FacultyBean)request.getSession().getAttribute("searchBean_acads");

		Page<FacultyBean> page = dao.getFacultyPage(1, Integer.MAX_VALUE, searchBean);
		List<FacultyBean> facultyList = page.getPageItems();

		return new ModelAndView("facultyExcelView","facultyList",facultyList);
	}*/


	@RequestMapping(value = "/admin/updateFacultyProfileForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView updateFacultyProfileForm(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelnView = new ModelAndView("updateFacultyProfile");	

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}

		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		String userId = (String)request.getSession().getAttribute("userId_acads");

		FacultyAcadsBean faculty=  fDao.findfacultyByFacultyId(userId);
		List<String> programTypes = fDao.getProgramTypes();
		List<String> programNames = fDao.getAllProgramNames();
		
		
		modelnView.addObject("faculty", faculty);
		modelnView.addObject("selectedProgGroups",faculty.getProgramGroup());
		modelnView.addObject("selectedProgNames",faculty.getProgramName());
		modelnView.addObject("programTypes",programTypes);
		modelnView.addObject("programNames",programNames);
		modelnView.addObject("subjectList", getSubjectList());

		return modelnView;
	}

	@RequestMapping(value = "/admin/saveFacultyProfile", method = {RequestMethod.POST})
	public ModelAndView saveFacultyProfile(HttpServletRequest request, HttpServletResponse response , @ModelAttribute FacultyAcadsBean faculty){
		ModelAndView modelnView = new ModelAndView("updateFacultyProfile");	

		FacultyDAO fDao = (FacultyDAO)act.getBean("facultyDAO");
		String userId = (String)request.getSession().getAttribute("userId_acads");

		String cvMessage = "";
		String photoMessage = "";

		if(!faculty.getCvFileData().isEmpty() || !faculty.getFacultyImageFileData().isEmpty() ){

			if(!faculty.getCvFileData().isEmpty() ){
				cvMessage = uploadFile(faculty,"CV",faculty.getCvFileData());
			}
			if(!faculty.getFacultyImageFileData().isEmpty() ){
				photoMessage = uploadFile(faculty,"Photo",faculty.getFacultyImageFileData());
			}

			if(StringUtils.isBlank(photoMessage) || StringUtils.isBlank(cvMessage))
			{
				setError(request,"Error while updating Faculty Details "+photoMessage+" "+cvMessage);
				return modelnView;
			}
		}
		faculty.setLastModifiedBy(userId);

        List<String> programTypes = fDao.getProgramTypes();
		List<String> programNames = fDao.getAllProgramNames();
        
		modelnView.addObject("faculty", faculty);
		modelnView.addObject("selectedProgGroups",faculty.getProgramGroup());
		modelnView.addObject("selectedProgNames",faculty.getProgramName());
		modelnView.addObject("programTypes",programTypes);
		modelnView.addObject("programNames",programNames);

		fDao.saveFacultyProfile(faculty);
		setSuccess(request, "Profile Updated Successfully ..");
		return modelnView;
	}

	private String uploadFile(FacultyAcadsBean bean,String fileType,CommonsMultipartFile fileToUpload) {

		InputStream inputStream = null;   
		OutputStream outputStream = null;
		String filePathForDB = null;

		CommonsMultipartFile file = fileToUpload; 
		String fileName = file.getOriginalFilename();   

		//Replace special characters in file
		fileName = fileName.replaceAll("'", "_");
		fileName = fileName.replaceAll(",", "_");
		fileName = fileName.replaceAll("&", "and");
		fileName = fileName.replaceAll(" ", "_");

//		fileName = fileName.substring(0, fileName.lastIndexOf(".")) +"_"+ fileType + fileName.substring(fileName.lastIndexOf("."), fileName.length());


		String todayAsString = new SimpleDateFormat("dd_MM_yyyy").format(new Date());

		//returns /NMSCE1234/<FileName>_<FileType>_<DATE>.<TYPE>
		fileName =  fileType +  "_" + todayAsString + fileName.substring(fileName.lastIndexOf('.'), fileName.length());
		


		try {   
			inputStream = file.getInputStream();   
			
			//returns /Faculty/NMSCE1234/<FileName>_<FileType>_<DATE>.<TYPE>
			String filePath = FACULTY_FILES_PATH + "/" + bean.getFacultyId() + "/" + fileName;
			filePathForDB = FACULTY_FILES_PREVIEW_PATH + bean.getFacultyId() + "/" + fileName;
			//returns E:/Faculty/NMSCE1234/<FileName>_<FileType>_<DATE>.<TYPE>
			//Just to store the file on the filesystem
			
			File folderPath = new File(FACULTY_FILES_PATH + "/" + bean.getFacultyId());
			
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

			if(fileType.equalsIgnoreCase("CV")){
				bean.setCvUrl(filePathForDB);
			}else if(fileType.equalsIgnoreCase("ConsentForm")){
				bean.setConsentFormUrl(filePathForDB);
			}else if(fileType.equalsIgnoreCase("Photo")){
				bean.setImgUrl(filePathForDB);
			}

			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
			  
			return "Error in uploading file for "+bean.getFacultyFullName() + " : "+ e.getMessage();
		}   catch(Exception e){
			  
			return "Error in uploading file for "+bean.getFacultyFullName() + " : "+ e.getMessage();
		}

		return filePathForDB;
	}

	public MultiValueMap<Double,String> getFilterMapBasedUponAverage(MultiValueMap<Double,String> valueMap ,double Avg,String avgForValue)
	{
		MultiValueMap filterMap = new MultiValueMap();

		for (Object key : valueMap.keySet()) {
			if("greaterThan".equalsIgnoreCase(avgForValue)){
				if ((Double)key > Avg) {
					if (valueMap.get(key) instanceof List) {

						List<String> value = (List<String>) valueMap.get(key);
						for (String v : value) {
							filterMap.put(key, v);
						}
					} else{
						filterMap.put(key, valueMap.get(key));
					}
				}
			}else if("lessThan".equalsIgnoreCase(avgForValue)){
				if ((Double)key < Avg) {
					if (valueMap.get(key) instanceof List) {

						List<String> value = (List<String>) valueMap.get(key);
						for (String v : value) {
							filterMap.put(key, v);
						}
					} else{
						filterMap.put(key, valueMap.get(key));
					}
				}
			}
		}
		return filterMap;
	}

	@RequestMapping(value = "/admin/addFacultyBulkUploadForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadFacultyAvailabilityForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FacultyAcadsBean facultyBean = new FacultyAcadsBean();
		m.addAttribute("facultyBean",facultyBean);

		return "uploadFaculty";
	}

	@RequestMapping(value = "/admin/uploadFaculty", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadFacultyUnavailability(FacultyAcadsBean facultyBean, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadFaculty");
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readFacultyExcel(facultyBean, userId);

			List<FacultyAcadsBean> facultyList = (ArrayList<FacultyAcadsBean>)resultList.get(0);
			List<FacultyAcadsBean> errorBeanList = (ArrayList<FacultyAcadsBean>)resultList.get(1);

			facultyBean = new FacultyAcadsBean();
			m.addAttribute("facultyBean",facultyBean);

			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				return modelnView;
			}

			FacultyDAO dao = (FacultyDAO)act.getBean("facultyDAO");
			ArrayList<String> errorList = dao.batchUpdateFaculty(facultyList);

			if(errorList.size() == 0){
				request.setAttribute("success","true");
				request.setAttribute("successMessage",facultyList.size() +" rows out of "+ facultyList.size()+" inserted successfully.");
			}else{
				request.setAttribute("error", "true");
				request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
			}

		}catch(Exception e){
			  
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");

		}

		return modelnView;
	}
	
	//-- common add faculty page method ---//
	public ModelAndView addFacultyErrorPage(HttpServletRequest request, HttpServletResponse response, FacultyAcadsBean faculty, List<String> programNames, List<String> programTypes, String msg) {
		ModelAndView modelnView = new ModelAndView("addFaculty");
		modelnView.addObject("faculty", faculty);
		modelnView.addObject("selectedProgGroups",faculty.getProgramGroup());
		modelnView.addObject("selectedProgNames",faculty.getProgramName());
		modelnView.addObject("programTypes",programTypes);
		modelnView.addObject("programNames",programNames);
		modelnView.addObject("ecApprovalProofExt", EC_APPROVAL_PROOF_EXTENSION);
		modelnView.addObject("ecApprovalProofPath", EC_APPROVAL_PROOF_S3_URL);
		setError(request, msg);
		return modelnView;
	}
	
	public ModelAndView updateFacultyErrorPage(HttpServletRequest request, HttpServletResponse response, FacultyAcadsBean faculty_Bean, List<String> programNames, List<String> programTypes, String msg) {
		ModelAndView modelnView = new ModelAndView("addFaculty");
		modelnView.addObject("faculty", faculty_Bean);
		request.setAttribute("edit", "true");
		modelnView.addObject("selectedProgGroups",faculty_Bean.getProgramGroup());
		modelnView.addObject("selectedProgNames",faculty_Bean.getProgramName());
		modelnView.addObject("programTypes",programTypes);
		modelnView.addObject("programNames",programNames);
		modelnView.addObject("ecApprovalProofExt", EC_APPROVAL_PROOF_EXTENSION);
		modelnView.addObject("ecApprovalProofPath", EC_APPROVAL_PROOF_S3_URL);
		setError(request, msg);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/uploadFacultyCourseForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String uploadFacultyCourseForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {

		FileAcadsBean  facultyCourse = new FileAcadsBean();
		m.addAttribute("facultyCourse",facultyCourse);
		m.addAttribute("subjectList", getSubjectList());
		m.addAttribute("yearList", ACAD_YEAR_LIST);
		List<FacultyCourseBean> faculty_List = facultyService.getallFacultyCourseList(NoOption);
		m.addAttribute("row_count",faculty_List.size());
		request.getSession().setAttribute("faculty_List", faculty_List);
		return "uploadFacultyCourseMapping";
	}
	
	@RequestMapping(value = "/admin/uploadFacultyCourseMapping", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView uploadFacultyCourseMapping(@ModelAttribute FileAcadsBean facultyCourse, BindingResult result,HttpServletRequest request, Model m){
		ModelAndView modelnView = new ModelAndView("uploadFacultyCourseMapping");
		List<FacultyCourseBean> faculty_List = null;
		try{
			String userId = (String)request.getSession().getAttribute("userId_acads");
			m.addAttribute("facultyCourse",facultyCourse);
			m.addAttribute("yearList", ACAD_YEAR_LIST);
			ExcelHelper excelHelper = new ExcelHelper();
			ArrayList<List> resultList = excelHelper.readFacultyCourseAccessExcel(facultyCourse, getSubjectList(), getFacultyList(), userId,getsubjectCodeMap(),getSubjectCodesList());
		
			List<FacultyCourseBean> facultyCourseList = (ArrayList<FacultyCourseBean>)resultList.get(0);
			List<FacultyCourseBean> errorBeanList = (ArrayList<FacultyCourseBean>)resultList.get(1);
			
			if(errorBeanList.size() > 0){
				request.setAttribute("errorBeanList", errorBeanList);
				
			}else {
		
				TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
				ArrayList<String> errorList = dao.batchUpdateFacultyCourse(facultyCourseList);
		
				if(errorList.size() == 0){
					request.setAttribute("success","true");
					request.setAttribute("successMessage",facultyCourseList.size() +" rows out of "+ facultyCourseList.size()+" inserted successfully.");
				}else{
					request.setAttribute("error", "true");
					request.setAttribute("errorMessage", errorList.size() + " records were NOT inserted. Please see row number of rows not inserted. Error row numbers "+errorList);
				}
			}
		
			faculty_List = facultyService.getallFacultyCourseList(NoOption);
		}catch(Exception e){
	
			e.printStackTrace();
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error in inserting rows.");
		}
	
		modelnView.addObject("row_count",faculty_List.size());
		request.getSession().setAttribute("faculty_List", faculty_List);
	
		return modelnView;
	}
	
	
	
	@RequestMapping(value = "/admin/deleteFacultyCourse", method = {RequestMethod.POST})
	public ResponseEntity<Integer> deleteFacultyCourse(@RequestBody FacultyCourseBean faculty) {
		int result = 0;
		try {
		result = facultyService.deleteFacultyCourse(faculty.getFacultyId(),faculty.getYear(),faculty.getMonth(),faculty.getSubjectcode());
		}catch(Exception e) {
			
		}
		return new ResponseEntity<Integer>(result,HttpStatus.OK);
	}
	
	@GetMapping(value = "/m/populateSubjectNameWithSubjectCode")
	public ResponseEntity<List<FacultyCourseBean>> populateSubjectNameWithSubjectCode() {
		
		return new ResponseEntity<List<FacultyCourseBean>>( facultyService.populateFacultySubjectCodeInCourseTable(),HttpStatus.OK);
	}
}
