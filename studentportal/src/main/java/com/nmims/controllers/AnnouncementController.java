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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.beans.AnnouncementMasterBean;
import com.nmims.beans.AssignmentStudentPortalFileBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.beans.TestStudentPortalBean;
import com.nmims.daos.AssignmentsDAO;
import com.nmims.daos.PortalDao;
import com.nmims.interfaces.AnnouncementServiceInterface;


/**
 * Handles requests for the application home page.
 */
@Controller
public class AnnouncementController extends BaseController{
	
	@Autowired
	ApplicationContext act;
	
	
	@Value( "${CONTENT_PATH}" )
	private String CONTENT_PATH;

	private static final Logger logger = LoggerFactory.getLogger(AnnouncementController.class);
	private final int pageSize = 20;
	
	private ArrayList<String> monthList = new ArrayList<String>(
			Arrays.asList("Jan","Feb","March","April","May","June","Jul","August","September","October","November","December")); 
	private ArrayList<String> programsList = new ArrayList<String>();
	private ArrayList<String> programStructureList = new ArrayList<String>();
	
	@Autowired
	AnnouncementServiceInterface announcementService;
	
	/**
	 * Refresh Cache function to refresh cache
	 * @param 
	 * none
	 * @return 
	 * none
	 * */
	public String RefreshCache() {
		
		programsList = null;
		getAllActivePrograms();
		
		programStructureList = null;
		getAllActiveProgramStructure();
		
		return null;
	}
	
	
	public AnnouncementController(){
	}
	
	public String getMonthNumber(String MonthName)
	{
		HashMap<String,String> mapOfMonthNameAndValue =new HashMap<String,String>();
		for(int i=0;i<monthList.size();i++)
		{
			mapOfMonthNameAndValue.put(monthList.get(i),String.valueOf(i+1));
		}
	   return mapOfMonthNameAndValue.get(MonthName);
	}
	 
	public ArrayList<String> getAllActivePrograms(){
		if(programsList == null || programsList.isEmpty()){
			
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			programsList = pDao.getAllActivePrograms();  
			return programsList;
		}
		return programsList;
	}
	public ArrayList<String> getAllActiveProgramStructure(){
		if(programStructureList == null || programStructureList.isEmpty()){
			
			PortalDao pDao = (PortalDao)act.getBean("portalDAO");
			programStructureList = pDao.getAllActiveProgramStructure();
			return programStructureList;
		}
		return programStructureList;
	}
	
	@RequestMapping(value = "/admin/addAnnouncementForm", method = {RequestMethod.GET, RequestMethod.POST})
	public String addAnnouncement(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		if(!checkSession(request, respnse)){
			return "login";
		}

		AnnouncementStudentPortalBean bean = new AnnouncementStudentPortalBean();
		//PortalDao dao = (PortalDao)act.getBean("portalDAO");

		List<AnnouncementMasterBean> masterbean = new ArrayList<AnnouncementMasterBean>();
		try {
		masterbean = announcementService.getConsumerProgramStructureData();
		}catch(Exception e) {
			
		}
		m.addAttribute("masterannouncement", masterbean);
		m.addAttribute("announcement",bean);
		m.addAttribute("program",getAllActivePrograms());
		m.addAttribute("programStructure",getAllActiveProgramStructure());

		return "jsp/addAnnouncement";
	}
	
	@RequestMapping(value = "/admin/addAnnouncement", method = RequestMethod.POST)
	public ModelAndView addAnnouncement(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AnnouncementStudentPortalBean bean,
			@RequestParam(required=false) MultipartFile file1,
			@RequestParam(required=false) MultipartFile file2,
			@RequestParam(required=false) MultipartFile file3) throws Exception{
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		
		
		 String userId = (String)request.getSession().getAttribute("userId");
		bean.setCreatedBy(userId);
		bean.setLastModifiedBy(userId);
		ModelAndView modelnView = new ModelAndView("jsp/announcement");

		PortalDao dao = (PortalDao)act.getBean("portalDAO");
	
		List<AnnouncementMasterBean> masterbean = announcementService.getConsumerProgramStructureData();
		 List<String> programList = new ArrayList<String>();
		 


		try {
		 programList = Arrays.asList(bean.getProgram().split(","));
		
		}catch(Exception e) {
			
		}		
   
		try {
		
		if(file1 != null && file1.getOriginalFilename() != null && file1.getSize() > 0){
			String filePath = announcementService.uploadAnnouncementFile(file1);
			if(filePath == null){
				modelnView = new ModelAndView("jsp/addAnnouncement");
				setError(request, "Error in uploading file");
				modelnView.addObject("announcement", bean);
				modelnView.addObject("masterannouncement", masterbean);
				modelnView.addObject("program",getAllActivePrograms());
				modelnView.addObject("programStructure",getAllActiveProgramStructure());
				return modelnView;
			}else{
				bean.setAttachmentFile1Path(filePath);
			}
		}
		
		if(file2 != null && file2.getOriginalFilename() != null && file2.getSize() > 0){
			String filePath = announcementService.uploadAnnouncementFile(file2);
			if(filePath == null){
				modelnView = new ModelAndView("jsp/addAnnouncement");
				setError(request, "Error in uploading file");
				modelnView.addObject("announcement", bean);
				modelnView.addObject("program",getAllActivePrograms());
				modelnView.addObject("programStructure",getAllActiveProgramStructure());
				modelnView.addObject("masterannouncement", masterbean);
				return modelnView;
			}else{
				bean.setAttachmentFile2Path(filePath);
			}
		}
		
		if(file3 != null  && file3.getOriginalFilename() != null && file3.getSize() > 0){
			String filePath = announcementService.uploadAnnouncementFile(file3);
			if(filePath == null){
				modelnView = new ModelAndView("jsp/addAnnouncement");
				setError(request, "Error in uploading file");
				modelnView.addObject("announcement", bean);
				modelnView.addObject("masterannouncement", masterbean);
				modelnView.addObject("program",getAllActivePrograms());
				modelnView.addObject("programStructure",getAllActiveProgramStructure());
				return modelnView;
			}else{
				bean.setAttachmentFile3Path(filePath);
			}
		}
		int id = 0;

	
		id = dao.insertAnnouncement(programList , bean);
		
		bean.setId(id+"");
		modelnView.addObject("announcement", bean);
		modelnView.addObject("program",getAllActivePrograms());
		modelnView.addObject("programStructure",getAllActiveProgramStructure());
		request.getSession().setAttribute("announcement", bean);
		
		}
		catch(Exception e)
		{
			

			logger.info("Error in inserting announcement table,post table, announcement-mapping table and announcement new table. Method Name:-addAnnouncement ");
			logger.error("Error in inserting announcement ",e.getMessage());

			setError(request, "Error in inserting file.Please Add announcement again ");
			modelnView = new ModelAndView("jsp/addAnnouncement");
			modelnView.addObject("masterannouncement", masterbean);
			modelnView.addObject("announcement", bean);
			modelnView.addObject("program",getAllActivePrograms());
			modelnView.addObject("programStructure",getAllActiveProgramStructure());
			return modelnView;
		}
		
		
		return modelnView;
		
	}
	
	/* Commented By Riya method is shifted in service 
	/*
	  private String uploadAnnouncementFile(MultipartFile file)
	  { 
	   InputStream inputStream = null; 
	   OutputStream outputStream = null;
	  
	  String fileName = file.getOriginalFilename();
	 
	  
	  
	  
	 
	  String newFileName = fileName.replaceAll(" ", "_"); 
	  newFileName = newFileName.replaceAll("&","_");
	 
	  String todayAsString = new SimpleDateFormat("ddMMyyyy").format(new Date());
	  fileName = newFileName + "_" + todayAsString +
	  fileName.substring(fileName.lastIndexOf("."), fileName.length()); 
	  try {
	 
	  inputStream = file.getInputStream(); 
	  String filePath = CONTENT_PATH +"Announcements/"+ fileName; //Check if Folder exists which is one folder per
	  Exam (Jun2015, Dec2015 etc.) 
	  File folderPath = new File(CONTENT_PATH +"Announcements/"); 
	  if (!folderPath.exists()) {
	  
	  	boolean created = folderPath.mkdirs();
	 
	  }
	  
	  File newFile = new File(filePath);
	  
	  outputStream = new FileOutputStream(newFile); 
	  int read = 0; byte[] bytes = new byte[1024];
	  
	  while ((read = inputStream.read(bytes)) != -1) 
	  { 
	  	outputStream.write(bytes, 0,read); 
	  } 
	  outputStream.close(); inputStream.close();
	
	  
	  return "Announcements/"+ fileName; 
	  } catch (IOException e) {
	   
	  return null; 
	  }
	  
	  
	 }
	 */


	@RequestMapping(value = "/admin/editAnnouncement", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editAnnouncement(HttpServletRequest request, HttpServletResponse response)
	{
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
	
		ModelAndView modelnView = new ModelAndView("jsp/addAnnouncement");
		//List<AnnouncementMasterBean> announcementprogramList = new ArrayList<AnnouncementMasterBean>();
		try {
		String id = request.getParameter("id");
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		
		AnnouncementStudentPortalBean bean = announcementService.findById(id);
		modelnView.addObject("announcement", bean);
		//announcementprogramList = getProgramListByAnnouncement(bean);
		
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-mm-dd");
		
		/* if the announcement is present in history table, then set announ_hist = true*/
		Date date = f.parse(bean.getLastModifiedDate());
		Date date2 = f.parse("2020-12-31");
			if(date.compareTo(date2) < 0) 
				request.setAttribute("announ_hist", "true");
			else
				request.setAttribute("announ_hist", "false");
		
		
		request.setAttribute("announcement", bean);
			
		}catch(Exception e)
		{
			
		}
		
		//Commented By Riya as it was not needed
		/*modelnView.addObject("program",getAllActivePrograms());
		modelnView.addObject("programStructure",getAllActiveProgramStructure());*/
		
		//request.setAttribute("announcementprograms", announcementprogramList);
		request.setAttribute("edit", "true");
		
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/updateAnnouncement", method = RequestMethod.POST)
	public ModelAndView updateAnnouncement(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AnnouncementStudentPortalBean bean,
			@RequestParam(required=false) MultipartFile file1,
			@RequestParam(required=false) MultipartFile file2,
			@RequestParam(required=false) MultipartFile file3,
			@RequestParam("announ_hist") String announ_hist) {
		
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/announcement");
		
		
			
		String userId = (String)request.getSession().getAttribute("userId");
		bean.setCreatedBy(userId);
		bean.setLastModifiedBy(userId);
		List<AnnouncementMasterBean> announcementprogramList = getProgramListByAnnouncement(bean);
		
		
		try {
			
		List<String> programList = new ArrayList<String>();
		for(AnnouncementMasterBean masterKey : announcementprogramList)
			programList.add(Integer.toString(masterKey.getId()));
		
		bean.setCount(programList.size());
		
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
	
		if(file1 != null && file1.getOriginalFilename() != null && file1.getSize() > 0){
			String filePath = announcementService.uploadAnnouncementFile(file1);
			if(filePath == null){
				modelnView = new ModelAndView("jsp/addAnnouncement");
				setError(request, "Error in uploading file");
				request.setAttribute("announcementprograms", announcementprogramList);
				modelnView.addObject("announcement", bean);
				modelnView.addObject("program",getAllActivePrograms());
				modelnView.addObject("programStructure",getAllActiveProgramStructure());
				request.setAttribute("edit", "true");
				return modelnView;
			}else{
				bean.setAttachmentFile1Path(filePath);
			}
		}else{
			bean.setAttachmentFile1Path(bean.getAttachment1());
		}
		
		if(file2 != null && file2.getOriginalFilename() != null && file2.getSize() > 0){
			String filePath = announcementService.uploadAnnouncementFile(file2);
			if(filePath == null){
				modelnView = new ModelAndView("jsp/addAnnouncement");
				setError(request, "Error in uploading file");
				request.setAttribute("announcementprograms", announcementprogramList);
				modelnView.addObject("announcement", bean);
				modelnView.addObject("program",getAllActivePrograms());
				modelnView.addObject("programStructure",getAllActiveProgramStructure());
				request.setAttribute("edit", "true");
				return modelnView;
			}else{
				bean.setAttachmentFile2Path(filePath);
			}
		}else{
			bean.setAttachmentFile2Path(bean.getAttachment2());
		}
		
		if(file3 != null  && file3.getOriginalFilename() != null && file3.getSize() > 0){
			String filePath = announcementService.uploadAnnouncementFile(file3);
			if(filePath == null){
				modelnView = new ModelAndView("jsp/addAnnouncement");
				setError(request, "Error in uploading file");
				request.setAttribute("announcementprograms", announcementprogramList);
				modelnView.addObject("announcement", bean);
				modelnView.addObject("program",getAllActivePrograms());
				modelnView.addObject("programStructure",getAllActiveProgramStructure());
				request.setAttribute("edit", "true");
				return modelnView;
			}else{
				bean.setAttachmentFile3Path(filePath);
			}
		}else{
			//Modified by Steffi on 29th Dec to include existing attachment cases.
			bean.setAttachmentFile3Path(bean.getAttachment3()); 
		}
		
			
			/* If the updated announcement is from history table,*/
			if(announ_hist.equals("true")) {
				
				dao.updateAnnouncementInHistory(programList,bean);
			}else
				dao.updateAnnouncement(programList,bean); //Current table
			
			
			modelnView.addObject("announcement", bean);
			modelnView.addObject("program",getAllActivePrograms());
			modelnView.addObject("programStructure",getAllActiveProgramStructure());
			request.getSession().setAttribute("announcement", bean);
			
		}catch(Exception e){
			
			logger.info("Error in updating in announcement,announcement-mapping,post,announcement-new table. Method name:- updateAnnouncement ");
			logger.error("Error in updating in announcement,announcement-mapping,post,announcement-new table. Method name:- updateAnnouncement ",e);
			setError(request, "Error in updating Announcement.Please update announcement again ");
			modelnView = new ModelAndView("jsp/addAnnouncement");
			modelnView.addObject("announcement", bean);
			modelnView.addObject("program",getAllActivePrograms());
			modelnView.addObject("programStructure",getAllActiveProgramStructure());
			request.setAttribute("announcementprograms", announcementprogramList);
			request.setAttribute("edit", "true");
			return modelnView;
		}
		
	
		return modelnView;
		}
		
	
	@RequestMapping(value = "/admin/deleteAnnouncement", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteAnnouncement(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		String id = request.getParameter("id");
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		try {
			dao.deleteAnnouncement(id);;
		}catch(Exception e)
		{
			
			logger.info("Error in deleting in announcement-mapping table, announcement-new table. Method Name :- deleteAnnouncement");
			logger.error("Error in deleting in announcement-mapping table, announcement-new table. Method Name :- deleteAnnouncement "+e);
			setError(request, "Error in deleting announcement.Please delete announcement again ");
			return getAllAnnouncements(request,response);
				
		}
		

		return getAllAnnouncements(request,response);
	}
	
	
	@RequestMapping(value = "/admin/getAllAnnouncements",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getAllAnnouncements(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		ModelAndView modelnView = new ModelAndView("jsp/announcementsList");
		//PortalDao dao = (PortalDao)act.getBean("portalDAO");
		
		//List<Job> jobs = dao.getAllJobs();
	
		//Page<AnnouncementBean> page = dao.getAnnouncementsPage(1, pageSize);
		
		/*------Get All Announcement Service Called--------*/
		PageStudentPortal<AnnouncementStudentPortalBean> page = announcementService.getAllAnnouncements(1, pageSize);
		List<AnnouncementStudentPortalBean> announcements = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		
		if(announcements == null || announcements.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Announcements Found.");
		}
		
		modelnView.addObject("announcements", announcements);
		return modelnView;
	}
	
	/*
	 * Shifted in AnnouncementStudentController by Riya 
	 
	@RequestMapping(value = "/student/getAllStudentAnnouncements", method ={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getAllStudentAnnouncements(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		String userId = (String)request.getSession().getAttribute("userId");
		int pageNo;
		try { 
		pageNo = Integer.parseInt(request.getParameter("pageNo"));
		}catch(Exception e) {
			pageNo = 1;
		}
		
		ModelAndView modelnView = new ModelAndView("jsp/announcementsListForStudent");
		
		/*------Get All Announcement By userId Service Called------*/
		/*Page<AnnouncementBean> page = announcementService.getAllAnnouncementByUserId(userId,pageNo,pageSize);
		List<AnnouncementBean> announcements = page.getPageItems();
		
		
		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		
		if(announcements == null || announcements.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Announcements Found.");
		}
		
		modelnView.addObject("announcements", announcements);
		return modelnView;
	}
	
	
	@RequestMapping(value = "/m/getAllStudentAnnouncements", method =RequestMethod.POST)
	public ResponseEntity<Page<AnnouncementBean>> m_getAllStudentAnnouncements(HttpServletRequest request, HttpServletResponse response){
		
		
		String userId = request.getParameter("userId");
		
		int pageNo;
		int pageSize;
		try { 
		pageNo = Integer.parseInt(request.getParameter("pageNo"));
		pageSize = Integer.parseInt(request.getParameter("pageSize"));
		}catch(Exception e) {
			pageNo = 1;
			pageSize = 20;
		}
		
		/*------Get All Announcement By userId Service Called------*/
		//Page<AnnouncementBean> page = announcementService.getAllAnnouncementByUserId(userId,pageNo,pageSize);
		//List<AnnouncementBean> announcements = page.getPageItems();

		//if(announcements == null || announcements.size() == 0){
		//	return new ResponseEntity<List<AnnouncementBean>>(HttpStatus.NO_CONTENT);
		//}
		/*return new ResponseEntity<Page<AnnouncementBean>>(page,HttpStatus.OK);
	}*/
	
	/* Commented By Riya as method is shifted in service layer
	
	 public Page<AnnouncementBean> getAllAnnouncementByUserId(String userId,int pageNo,int pageSize) 
	 { 
	 	PortalDao dao = (PortalDao)act.getBean("portalDAO");
	 	
	 	StudentBean student = dao.getSingleStudentsData(userId); 
	 	String Month = getMonthNumber(student.getEnrollmentMonth());
	    String startDate = student.getEnrollmentYear()+"-"+Month+"-01"; 
	    String  consumerProgramStructureId = student.getConsumerProgramStructureId();
	 //Page<AnnouncementBean> page  = dao.getAllAnnouncementForSingleStudent(startDate,1, pageSize); //Changed
	   student.getPrgmStructApplicable() into consumerProgramStructureId
	   Page<AnnouncementBean> page = dao.getAllAnnouncementForSingleStudent(student.getProgram(),consumerProgramStructureId,startDate,pageNo, pageSize); //
	   Page<AnnouncementBean> page =  dao.getAllAnnouncementForSingleStudent(student.getProgram(),student.getPrgmStructApplicable(),startDate,1, pageSize);
	  
	  return page; 
	  }
	 */
	 
	
	@RequestMapping(value = "/getAllAnnouncementsOfStudentPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getAllAnnouncementsOfStudentPage(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/announcementsListForStudent");
		int pageNo;
		try {
		pageNo = Integer.parseInt(request.getParameter("pageNo"));
		}catch(Exception e) {
			pageNo =1;
		}
		//PortalDao dao = (PortalDao)act.getBean("portalDAO");
		
		
		//Page<AnnouncementBean> page = dao.getAnnouncementsPage(pageNo, pageSize);
		
		/* Get All Announcement Service Called */
		PageStudentPortal<AnnouncementStudentPortalBean> page = announcementService.getAllAnnouncements(pageNo, pageSize);
		List<AnnouncementStudentPortalBean> announcements = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		
		if(announcements == null || announcements.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Announcements Found.");
		}
		
		modelnView.addObject("announcements", announcements);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/getAllAnnouncementsPage",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView getAllAnnouncementsPage(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/announcementsList");
		
		int pageNo = Integer.parseInt(request.getParameter("pageNo"));
		//PortalDao dao = (PortalDao)act.getBean("portalDAO");
	
		//Page<AnnouncementBean> page = dao.getAnnouncementsPage(pageNo, pageSize);
		
		PageStudentPortal<AnnouncementStudentPortalBean> page = announcementService.getAllAnnouncements(pageNo, pageSize);
		List<AnnouncementStudentPortalBean> announcements = page.getPageItems();

		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		
		if(announcements == null || announcements.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Announcements Found.");
		}
		
		modelnView.addObject("announcements", announcements);
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/viewAnnouncementDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView viewAnnouncementDetails(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		ModelAndView modelnView = new ModelAndView("jsp/announcement");
		
		String id = request.getParameter("id");
		//PortalDao dao = (PortalDao)act.getBean("portalDAO");
		/*----Find Announcement By Id Service Called-------*/
		AnnouncementStudentPortalBean bean = announcementService.findById(id);
		modelnView.addObject("announcement", bean);
		//request.setAttribute("announcement", bean);
		return modelnView;
	}
	
	
	@RequestMapping(value = "/admin/insertExistingAnnouncementDataIntoMasterKey", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity insertExistingAnnouncementDataIntoMasterKey(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		//if(!checkSession(request, respnse)){
		//	return "login";
		//}

		AnnouncementStudentPortalBean bean = new AnnouncementStudentPortalBean();
		PortalDao dao = (PortalDao)act.getBean("portalDAO");

		List<String> cpsIdList = new ArrayList<String>();
		try {
			cpsIdList = dao.getAllConsumerProgramStructureId();
		}catch(Exception e) {
			
		}
		
		List<Integer> announcementIdsList = new ArrayList<Integer>();
		try {
			announcementIdsList = dao.getAllAnnouncementIdByProgram();
		}catch(Exception e) {
			
		}
		
		   for(Integer id:announcementIdsList){  
			    
				dao.insertAnnouncementMasterKey(cpsIdList,id);

			     
			   }  		


			return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/insertExistingNonAllProgramAnnouncementDataIntoMasterKey", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity insertExistingNonAllProgramAnnouncementDataIntoMasterKey(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		

		AnnouncementStudentPortalBean bean = new AnnouncementStudentPortalBean();
		PortalDao dao = (PortalDao)act.getBean("portalDAO");

		List<AnnouncementStudentPortalBean> announcementIdsList = new ArrayList<AnnouncementStudentPortalBean>();
		try {
			announcementIdsList = dao.getAllAnnouncementIdOfSingleProgram();
			
		}catch(Exception e) {
			
		}
		
		   for(AnnouncementStudentPortalBean abean:announcementIdsList){  
			   
			     int programId = dao.getProgramIdByCode(abean.getProgram());
			     int programStructureId = dao.getProgramStructureIdByProgramStructure(abean.getProgramStructure());
			    
			  
			     
			     if(programId != 0 || programStructureId != 0) {
			     int cpsId = dao.getCpsIdByProgramAndProgramStucture(programId,programStructureId);
			   
			    if(cpsId != 0 ) {
			    	int masterinsertedid = dao.insertAnnouncementSingleMasterKey(abean.getId(), cpsId);
			    	if(masterinsertedid == 0 ) {
			    		
			    		
			    	}
			    }
			     }
			     
			   }	 		

			return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/admin/insertExistingAllProgramStructureAnnouncementData", method = {RequestMethod.GET, RequestMethod.POST})
	public ResponseEntity insertExistingAllProgramStructureAnnouncementData(HttpServletRequest request, HttpServletResponse respnse, Model m) {
	//	if(!checkSession(request, respnse)){
	//		return "login";
	//	}

		AnnouncementStudentPortalBean bean = new AnnouncementStudentPortalBean();
		PortalDao dao = (PortalDao)act.getBean("portalDAO");

		List<AnnouncementStudentPortalBean> announcementIdsList = new ArrayList<AnnouncementStudentPortalBean>();
		try {
			announcementIdsList = dao.getAllAnnouncementIdOfAllProgramStructure(); 
			
		}catch(Exception e) {
			
		}
		
		   for(AnnouncementStudentPortalBean abean:announcementIdsList){  
			   
			     int programId = dao.getProgramIdByCode(abean.getProgram());
			    
			   
			    
			     
			     if(programId != 0) {
			    	 

			 		List<String> cpsIdList = new ArrayList<String>();
			 		try {
			 			cpsIdList =  dao.getAllConsumerProgramStructureIdByProgram(programId);
			 		}catch(Exception e) {
			 			
			 		}
			 		
			 		int annId = Integer.parseInt(abean.getId());
			 		if(cpsIdList.size() > 0) { 
			     	dao.insertAnnouncementMasterKey(cpsIdList , annId);
			    	
			    
			     }
			     }
			   }	 		
			return new ResponseEntity<>(HttpStatus.OK);

		
	}

	
	
	
	@RequestMapping(value = "/admin/getCommonAnnouncementProgramsList", method = {RequestMethod.POST})
	public ResponseEntity<ArrayList<AnnouncementMasterBean>> getCommonAnnouncementProgramsList(@RequestBody AnnouncementStudentPortalBean announcementbean) {
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		
		
		ArrayList<AnnouncementMasterBean> consumerProgramStructureList = new ArrayList<AnnouncementMasterBean>();
		List<String> masterKeyIds = new ArrayList<String>();
		try {
			masterKeyIds = dao.getMasterKeyByAnnouncementId(announcementbean.getId());
			String commaSeperatedIdsList = String.join(",", masterKeyIds);
			
			consumerProgramStructureList = dao.getConsumerProgramStructureDataById(commaSeperatedIdsList);
		}catch(Exception e) {
			
		}
		//return new ResponseEntity<ArrayList<AnnouncementMasterBean>>(dao.getMasterKeyByAnnouncementId(),HttpStatus.OK);
	
		
		
		/*StringBuilder sb = new StringBuilder();

		if(masterKeyIds.size() > 0) {
		for(AnnouncementMasterBean am : masterKeyIds ) {
			
			 sb.append(am.getMaster_key() ).append(",");
			 
		}
		commaSeperatedIdsList = sb.deleteCharAt(sb.length() - 1).toString();
	}else {
		commaSeperatedIdsList = "0";
	}*/
		
		
		
		return new ResponseEntity<ArrayList<AnnouncementMasterBean>>(consumerProgramStructureList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/getProgramListByAnnouncement", method = {RequestMethod.POST})
	public List<AnnouncementMasterBean> getProgramListByAnnouncement(@RequestBody AnnouncementStudentPortalBean announcementbean) {
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		
		
		List<AnnouncementMasterBean> programList = new ArrayList<AnnouncementMasterBean>();
		List<String> masterKeyIds = new ArrayList<String>();
		try {
			masterKeyIds = dao.getMasterKeyByAnnouncementId(announcementbean.getId());
			
			String commaSeperatedIdsList = String.join(",", masterKeyIds);
			
			programList = dao.getConsumerProgramStructureDataById(commaSeperatedIdsList);
		}catch(Exception e) {
			
		}
		//return new ResponseEntity<ArrayList<AnnouncementMasterBean>>(dao.getMasterKeyByAnnouncementId(),HttpStatus.OK);
	
		
		/*StringBuilder sb = new StringBuilder();

		if(masterKeyIds.size() > 0) {
		for(AnnouncementMasterBean am : masterKeyIds ) {
			
			 sb.append(am.getMaster_key() ).append(",");
			 
		}
		commaSeperatedIdsList = sb.deleteCharAt(sb.length() - 1).toString();
	}else {
		commaSeperatedIdsList = "0";
	}
		*/
		
		
		
		
		
		return programList;
	
	}
	
	
	@RequestMapping(value = "/admin/editAnnouncementProgram", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView editAnnouncementProgram(HttpServletRequest request, HttpServletResponse response) throws Exception{
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
			}
		
		ModelAndView modelnView = new ModelAndView("jsp/addAnnouncement");
		//Added By Riya
		AnnouncementStudentPortalBean bean = new AnnouncementStudentPortalBean();
		
		
		String masterKey = request.getParameter("masterKey");
		String announcementId = request.getParameter("announcementId");
		//PortalDao dao = (PortalDao)act.getBean("portalDAO");
		
		//Commented By Riya 
		//AnnouncementBean bean = dao.findByIdAndMasterKey(announcementId,masterKey);
	//	request.setAttribute("announcementId", announcementId);
		//request.setAttribute("masterId", masterId);
		try {
			/*-----Edit Single Announcement Program---------*/
			bean = announcementService.editAnnouncementProgram(announcementId,masterKey);
		}catch(Exception e)
		{
			
        }
		
		
		
		modelnView.addObject("announcementId", announcementId);
		modelnView.addObject("masterKey",masterKey);
		modelnView.addObject("masterId", bean.getMasterId());
		modelnView.addObject("announcement", bean);
		
		/*Commented By Riya as it was not needed */
		//modelnView.addObject("program",getAllActivePrograms());
		//modelnView.addObject("programStructure",getAllActiveProgramStructure());
		request.setAttribute("announcement", bean);
		request.setAttribute("editAnnouncementProgram", "true");	
		return modelnView;
	}
	
	@RequestMapping(value = "/admin/addAnotherAnnouncement", method = RequestMethod.POST)
	public ModelAndView addAnotherAnnouncement(HttpServletRequest request, HttpServletResponse response, @ModelAttribute AnnouncementStudentPortalBean bean,
			@RequestParam(required=false) MultipartFile file1,
			@RequestParam(required=false) MultipartFile file2,
			@RequestParam(required=false) MultipartFile file3,
			@RequestParam("oldAnnouncementId") String oldAnnouncementId) throws Exception{
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		ModelAndView modelnView = new ModelAndView("jsp/announcement");
		
		try {
		 String userId = (String)request.getSession().getAttribute("userId");
	
		
		 
		 String masterId = bean.getMasterId(); 

		bean.setCreatedBy(userId);
		bean.setLastModifiedBy(userId);
	

		PortalDao dao = (PortalDao)act.getBean("portalDAO");
	
		if(file1 != null && file1.getOriginalFilename() != null && file1.getSize() > 0){
			String filePath = announcementService.uploadAnnouncementFile(file1);
			if(filePath == null){
				modelnView = new ModelAndView("jsp/addAnnouncement");
				setError(request, "Error in uploading file");
				modelnView.addObject("announcement", bean);
				modelnView.addObject("program",getAllActivePrograms());
				modelnView.addObject("programStructure",getAllActiveProgramStructure());
				return modelnView;
			}else{
				bean.setAttachmentFile1Path(filePath);
			}
		}
		
		if(file2 != null && file2.getOriginalFilename() != null && file2.getSize() > 0){
			String filePath = announcementService.uploadAnnouncementFile(file2);
			if(filePath == null){
				modelnView = new ModelAndView("jsp/addAnnouncement");
				setError(request, "Error in uploading file");
				modelnView.addObject("announcement", bean);
				modelnView.addObject("program",getAllActivePrograms());
				modelnView.addObject("programStructure",getAllActiveProgramStructure());
				return modelnView;
			}else{
				bean.setAttachmentFile2Path(filePath);
			}
		}
		
		if(file3 != null  && file3.getOriginalFilename() != null && file3.getSize() > 0){
			String filePath = announcementService.uploadAnnouncementFile(file3);
			if(filePath == null){
				modelnView = new ModelAndView("jsp/addAnnouncement");
				setError(request, "Error in uploading file");
				modelnView.addObject("announcement", bean);
				modelnView.addObject("program",getAllActivePrograms());
				modelnView.addObject("programStructure",getAllActiveProgramStructure());
				return modelnView;
			}else{
				bean.setAttachmentFile3Path(filePath);
			}
		}
		
		//Commented By Riya (as extra argument need to be passed
		//int id = dao.insertAnotherAnnouncement(bean,masterId);
		int id = 0;
	
		id = dao.insertAnotherAnnouncement(bean,masterId,oldAnnouncementId);
		
		bean.setId(id+"");
		modelnView.addObject("announcement", bean);
		modelnView.addObject("program",getAllActivePrograms());
		modelnView.addObject("programStructure",getAllActiveProgramStructure());
		request.getSession().setAttribute("announcement", bean);
		
		}catch(Exception e)
		{
			
			logger.info("Error in inserting another announcement in announcement table,announcement-mapping table, announcement-new table, post table : Method Name :- addAnotherAnnouncement");
			logger.error("Error in inserting another announcement in announcement table,announcement-mapping table, announcement-new table, post table : Method Name :- addAnotherAnnouncement "+e);
			setError(request, "Error in inserting Announcement.Please Update Announcement again. ");
			modelnView = new ModelAndView("jsp/addAnnouncement");
			request.setAttribute("editAnnouncementProgram", "true");
			modelnView.addObject("announcement", bean);
			modelnView.addObject("program",getAllActivePrograms());
			modelnView.addObject("programStructure",getAllActiveProgramStructure());
			return modelnView;
		}
	
		return modelnView;
		
	}
	
	@RequestMapping(value = "/admin/deleteAnnouncementProgram", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView deleteAnnouncementProgram(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
			}
		
		
		String masterKey = request.getParameter("masterKey");
		String announcementId = request.getParameter("announcementId");
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		
		try {
		dao.deleteAnnouncementProgram(masterKey,announcementId);
		}catch(Exception e)
		{
			
			logger.info("Error in deleting in announcement-mapping table, announcement-new table : Method Name :- deleteAnnouncementProgram");
			logger.error("Error in deleting in announcement-mapping table, announcement-new table : Method Name :- deleteAnnouncementProgram "+e);
			setError(request, "Error in deleting announcement.Please delete announcement again ");
			return getAllAnnouncements(request,response);
			
		}
	
		 
		return getAllAnnouncements(request,response);
	}
	 


 

}


