//to be deleted api transfer to announcemnt student rest controller



//package com.nmims.controllers;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.nmims.beans.AnnouncementBean;
//import com.nmims.beans.Page;
//import com.nmims.beans.StudentBean;
//import com.nmims.daos.PortalDao;
//
//@RestController
//@RequestMapping("m")
//public class AnnouncementRestController {
//	
//	
//	@Autowired
//	ApplicationContext act;
//	
//	private ArrayList<String> monthList = new ArrayList<String>(
//			Arrays.asList("Jan","Feb","March","April","May","June","Jul","August","September","October","November","December"));
//	
//	
//	@PostMapping(path = "/getAllStudentAnnouncements")
//	public ResponseEntity<Page<AnnouncementBean>> m_getAllStudentAnnouncements(HttpServletRequest request, HttpServletResponse response){
//		
//		
//		String userId = request.getParameter("userId");
//			
//		int pageNo;
//		int pageSize;
//		try { 
//		pageNo = Integer.parseInt(request.getParameter("pageNo"));
//		pageSize = Integer.parseInt(request.getParameter("pageSize"));
//		}catch(Exception e) {
//			pageNo = 1;
//			pageSize = 20;
//		}
//		
//		Page<AnnouncementBean> page = getAllAnnouncementByUserId(userId,pageNo,pageSize);
//		//List<AnnouncementBean> announcements = page.getPageItems();
//
//		//if(announcements == null || announcements.size() == 0){
//		//	return new ResponseEntity<List<AnnouncementBean>>(HttpStatus.NO_CONTENT);
//		//}
//		return new ResponseEntity<Page<AnnouncementBean>>(page,HttpStatus.OK);
//	}
//
//	
//	public Page<AnnouncementBean> getAllAnnouncementByUserId(String userId,int pageNo,int pageSize)
//	{
//		PortalDao dao = (PortalDao)act.getBean("portalDAO");
//		
//		StudentBean student =dao.getSingleStudentsData(userId);
//		String Month =getMonthNumber(student.getEnrollmentMonth());
//		String startDate =student.getEnrollmentYear()+"-"+Month+"-01";
//		String consumerProgramStructureId = student.getConsumerProgramStructureId();
//		//Page<AnnouncementBean> page = dao.getAllAnnouncementForSingleStudent(startDate,1, pageSize);
//		//Changed student.getPrgmStructApplicable() into consumerProgramStructureId
//		Page<AnnouncementBean> page = dao.getAllAnnouncementForSingleStudent(student.getProgram(),consumerProgramStructureId,startDate,pageNo, pageSize);
//	//	Page<AnnouncementBean> page = dao.getAllAnnouncementForSingleStudent(student.getProgram(),student.getPrgmStructApplicable(),startDate,1, pageSize);
//
//		return page;
//	}
//	
//	public String getMonthNumber(String MonthName)
//	{
//		HashMap<String,String> mapOfMonthNameAndValue =new HashMap<String,String>();
//		for(int i=0;i<monthList.size();i++)
//		{
//			mapOfMonthNameAndValue.put(monthList.get(i),String.valueOf(i+1));
//		}
//	   return mapOfMonthNameAndValue.get(MonthName);
//	}
//}
