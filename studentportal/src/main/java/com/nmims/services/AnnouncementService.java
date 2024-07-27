package com.nmims.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.nmims.beans.AnnouncementStudentPortalBean;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.AnnouncementMasterBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.controllers.AnnouncementController;
import com.nmims.dto.AnnouncementDto;
import com.nmims.daos.PortalDao;
import com.nmims.helpers.AWSHelper;
import com.nmims.interfaces.AnnouncementServiceInterface;
import java.util.Arrays;
import java.util.HashMap;

@Service("announcementService")
public class AnnouncementService implements AnnouncementServiceInterface
{
	@Autowired
	ApplicationContext act;
	
	@Value( "${CONTENT_PATH}" )
	private String CONTENT_PATH;
	
	@Value("${SERVER_PATH}")
	private String SERVER_PATH;
	
	private ArrayList<String> monthList = new ArrayList<String>(
			Arrays.asList("Jan","Feb","March","April","May","June","Jul","August","September","October","November","December")); 
	
	@Autowired
	AWSHelper awshelper;
	
	final static String baseFolderPath = "Announcements/";
	
	final static String Announcement_Folder_Name = "announcementfiles";
	
	private static final Logger logger = LoggerFactory.getLogger(AnnouncementController.class);
	

	private HttpHeaders getHeaders() {
		HttpHeaders headers =  new HttpHeaders();
		headers.add("Accept", "application/json");
		headers.add("Content-Type", "application/json");
		return headers;
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

	@Override
	public List<AnnouncementMasterBean> getConsumerProgramStructureData() {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		return pDao.getConsumerProgramStructureData();
	}

	@Override
	public String uploadAnnouncementFile(MultipartFile file) {
		try {  
		//InputStream inputStream = null;   
		//OutputStream outputStream = null;   

		//String fileName = file.getOriginalFilename();   
		//System.out.println("fileName Size = "+file.getSize());

		

		//System.out.println("fileName = "+fileName);
		//String newFileName = fileName.replaceAll(" ", "_");
		//newFileName = newFileName.replaceAll("&", "_");

		String todayAsString = new SimpleDateFormat("ddMMyyyy").format(new Date());
		//fileName = newFileName +  "_" + todayAsString + fileName.substring(fileName.lastIndexOf("."), fileName.length());
		
		
		String fileName = todayAsString+"_"+RandomStringUtils.randomAlphanumeric(5)+"."+FilenameUtils.getExtension(file.getOriginalFilename());;
			
			/*
			inputStream = file.getInputStream();   
			String filePath = CONTENT_PATH + "Announcements/"+ fileName;
			//Check if Folder exists which is one folder per Exam (Jun2015, Dec2015 etc.) 
			File folderPath = new File(CONTENT_PATH + "Announcements/");
			if (!folderPath.exists()) {
				//System.out.println("Making Folder");
				boolean created = folderPath.mkdirs();
				//System.out.println("created = "+created);
			}   

			File newFile = new File(filePath);   

			outputStream = new FileOutputStream(newFile);   
			int read = 0;   
			byte[] bytes = new byte[1024];   

			while ((read = inputStream.read(bytes)) != -1) {   
				outputStream.write(bytes, 0, read);   
			}
			outputStream.close();
			inputStream.close();
			//System.out.println("Uploded File");
			*/
			

			String url = awshelper.uploadFile(file, baseFolderPath, Announcement_Folder_Name, baseFolderPath+fileName);
			
			
			if(!StringUtils.isBlank(url))
				return baseFolderPath+ fileName;
		} catch (Exception e) {   
			
		}   
		return null;
		
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	public int insertAnnouncement(List<String> consumerProgramIdsList, AnnouncementStudentPortalBean bean) throws Exception {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		int id = pDao.insertAnnouncement(consumerProgramIdsList , bean);
		return id;
		
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	public void updateAnnouncement(List<String> consumerProgramIdsList, AnnouncementStudentPortalBean bean) throws Exception {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		pDao.updateAnnouncement(consumerProgramIdsList , bean);
		
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	public int deleteAnnouncement(String id) throws Exception
	{
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		
			pDao.deleteAnnouncement(id);
			return 1;
		
	}

	@Override
	public PageStudentPortal<AnnouncementStudentPortalBean> getAllAnnouncements(int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		PageStudentPortal<AnnouncementStudentPortalBean> page = pDao.getAnnouncementsPage(pageNo, pageSize);
		return page;
	}

	@Override
	public PageStudentPortal<AnnouncementStudentPortalBean> getAllAnnouncementByUserId(String userId, int pageNo, int pageSize) {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean student =pDao.getSingleStudentsData(userId);
		String Month =getMonthNumber(student.getEnrollmentMonth());
		String startDate =student.getEnrollmentYear()+"-"+Month+"-01";
		String consumerProgramStructureId = student.getConsumerProgramStructureId();
		//Page<AnnouncementBean> page = dao.getAllAnnouncementForSingleStudent(startDate,1, pageSize);
		//Changed student.getPrgmStructApplicable() into consumerProgramStructureId
		PageStudentPortal<AnnouncementStudentPortalBean> page = pDao.getAllAnnouncementForSingleStudent(student.getProgram(),consumerProgramStructureId,startDate,pageNo, pageSize);
	//	Page<AnnouncementBean> page = dao.getAllAnnouncementForSingleStudent(student.getProgram(),student.getPrgmStructApplicable(),startDate,1, pageSize);

		return page;
	}

	@Override
	public AnnouncementStudentPortalBean AnnouncementDetailsfindById(String id) {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		AnnouncementStudentPortalBean bean = pDao.findById(id);
		return bean;
	}

	@Override
	public ArrayList<AnnouncementMasterBean> getCommonAnnouncementProgramsList(AnnouncementStudentPortalBean announcementbean) {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<String> masterKeyIds = new ArrayList<String>();
		try {
			masterKeyIds = pDao.getMasterKeyByAnnouncementId(announcementbean.getId());
		}catch(Exception e) {
			
		}
		
		String commaSeperatedIdsList = String.join(",", masterKeyIds);
		

		ArrayList<AnnouncementMasterBean> getConsumerPrgmlist = pDao.getConsumerProgramStructureDataById(commaSeperatedIdsList);
		return getConsumerPrgmlist;
	}

	@Override
	public AnnouncementStudentPortalBean editAnnouncementProgram(String announcementId, String masterKey) {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		AnnouncementStudentPortalBean bean = pDao.findByIdAndMasterKey(announcementId,masterKey);
		
		return bean;
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	public int addAnotherAnnouncement(AnnouncementStudentPortalBean bean, String masterId, String oldAnnouncementId)
			throws Exception {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		int id = pDao.insertAnotherAnnouncement(bean,masterId,oldAnnouncementId);
		return id;
	}

	@Override
	public List<AnnouncementStudentPortalBean> getAllActiveAnnouncements(String program, String consumerProgramStructureId) {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		List<AnnouncementStudentPortalBean> announcements = new ArrayList<AnnouncementStudentPortalBean>();
		 announcements = pDao.getAllActiveAnnouncements(program,consumerProgramStructureId);
		 return announcements;
	}

	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	public int deleteAnnouncementProgram(String masterKey, String announcementId) throws Exception {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		pDao.deleteAnnouncementProgram(masterKey,announcementId);
		return 1;
	}

	@Override
	public AnnouncementStudentPortalBean findById(String announcementId) {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		return pDao.findById(announcementId);
	}
	
	
	//commented By Riya same dao call like getAllAnnouncements
		/*public Page<AnnouncementBean> getAllAnnouncementsOfStudentPage(int pageNo, int pageSize)
		{
			PortalDao dao = (PortalDao)act.getBean("portalDAO");
			Page<AnnouncementBean> page = dao.getAnnouncementsPage(pageNo, pageSize);
			return page;
		}
		
		public Page<AnnouncementBean> getAllAnnouncementsPage(int pageNo, int pageSize)
		{
			PortalDao dao = (PortalDao)act.getBean("portalDAO");
			Page<AnnouncementBean> page = dao.getAnnouncementsPage(pageNo, pageSize);
			return page;
		}*/
	
	//same method logic as getCommonAnnouncementProgramsList
		/*public List<AnnouncementMasterBean> getProgramListByAnnouncement(AnnouncementBean announcementbean)
		{
			PortalDao dao = (PortalDao)act.getBean("portalDAO");
			//System.out.println("==========> id : " +announcementbean.getId() );	
			
			List<AnnouncementMasterBean> programList = new ArrayList<AnnouncementMasterBean>();
			List<AnnouncementMasterBean> masterKeyIds = new ArrayList<AnnouncementMasterBean>();
			try {
				masterKeyIds = dao.getMasterKeyByAnnouncementId(announcementbean);
			}catch(Exception e) {
				
			}
			//return new ResponseEntity<ArrayList<AnnouncementMasterBean>>(dao.getMasterKeyByAnnouncementId(),HttpStatus.OK);
		
			String commaSeperatedIdsList = "";
			StringBuilder sb = new StringBuilder();

			if(masterKeyIds.size() > 0) {
				for(AnnouncementMasterBean am : masterKeyIds ) {
				
					sb.append(am.getMaster_key() ).append(",");
				 
				}
				commaSeperatedIdsList = sb.deleteCharAt(sb.length() - 1).toString();
			}else {
			commaSeperatedIdsList = "0";
			}
			
			programList = dao.getConsumerProgramStructureDataById(commaSeperatedIdsList);
			
			return programList;
			
		}*/
	
	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
	public void updateAnnouncementInHistory(List<String> consumerProgramIdsList, AnnouncementStudentPortalBean bean) throws Exception {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		pDao.updateAnnouncementInHistory(consumerProgramIdsList , bean);
		
	}

	@Override
	public List<String> getMasterKeyByAnnouncementId(String announcementId) {
		// TODO Auto-generated method stub
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		return pDao.getMasterKeyByAnnouncementId(announcementId);
	}

	//For MOBILE AND MBA-WX
	
	@Override
	public List<AnnouncementStudentPortalBean> getAllStudentAnnouncements(String userId) {
		// TODO Auto-generated method stub
		

		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean student =dao.getSingleStudentsData(userId);
		
		String consumerProgramStructureId = student.getConsumerProgramStructureId();
		
		List<AnnouncementStudentPortalBean> announcements = null;

		try {
			
			announcements = dao.getAllActiveAnnouncements(student.getProgram(),consumerProgramStructureId);	
			

		}catch(Exception e) {
			
		}
		
		//Added temp for hiding Announcements for new batch
	/*	if(student.getEnrollmentMonth().equalsIgnoreCase("Oct")) {
			announcements = new ArrayList<AnnouncementStudentPortalBean>();
		}*/
		return announcements;
	}
	
	
	public List<AnnouncementStudentPortalBean> getActiveAnnouncementByRest(String masterkey,HashMap<String,String> pssIds) {
		List<AnnouncementStudentPortalBean> announcements = new ArrayList<AnnouncementStudentPortalBean>();
		AnnouncementDto announcement = new AnnouncementDto();
		ArrayList<String> pssId = new ArrayList<String>(pssIds.keySet());
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = SERVER_PATH + "announcement/m/getAllStudentActiveAnnouncements";
			announcement.setMasterkey(masterkey);
			announcement.setPssIds(pssId);
			
			ResponseEntity<String> response = restTemplate.postForEntity(url, announcement, String.class);	

			if (!("200".equalsIgnoreCase(response.getStatusCode().toString()))) {
				logger.info("Error:- Getting response code other than 200 ( student course  portal ) for masterkey "+masterkey+" And Response :- "+response.toString());
			}else {
			
				JsonObject 	jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
				announcement = new Gson().fromJson(jsonObject, AnnouncementDto.class);
				announcements.addAll(announcement.getAnnouncements());
			}
		}
		catch (Exception e) {
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			String stackTrace = writer.toString();
			logger.info("Error in calling api of student course in portal for masterkey "+masterkey,e);
			
		}
		return announcements;
	}
	
	public List<AnnouncementStudentPortalBean> getAllActiveAnnouncementByRest() {
		List<AnnouncementStudentPortalBean> announcements = new ArrayList<AnnouncementStudentPortalBean>();
		AnnouncementDto announcement = new AnnouncementDto();
		try {
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<String> requestEntity = new HttpEntity<>("", getHeaders());
			String url = SERVER_PATH + "announcement/m/getAllActiveAnnouncements";
			ResponseEntity<String> response = restTemplate.exchange(url,  HttpMethod.GET,requestEntity ,String.class);	

			if (!("200".equalsIgnoreCase(response.getStatusCode().toString()))) {
				logger.info("Error:- Getting response code other than 200 ( student course  portal ) for admin And Response :- "+response.toString());
			}else {
				JsonObject 	jsonObject = new JsonParser().parse(response.getBody()).getAsJsonObject();
				announcement = new Gson().fromJson(jsonObject, AnnouncementDto.class);
				announcements.addAll(announcement.getAnnouncements());
			}
		}
		catch (Exception e) { 
			logger.info("Error in calling api of student course in portal for admin ",e);
		}
		return announcements;
	}
}
 