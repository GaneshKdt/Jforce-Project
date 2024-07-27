package com.nmims.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.PortalDao;
import com.nmims.helpers.PersonStudentPortalBean;
import com.nmims.interfaces.AnnouncementServiceInterface;

@RestController
@RequestMapping("m")
public class AnnouncementStudentRESTController
{
	@Autowired
	AnnouncementServiceInterface announcementService;
	
	@RequestMapping(value = "/getAllStudentAnnouncements", method =RequestMethod.POST, produces="application/json", consumes="application/json")
	public ResponseEntity<List<AnnouncementStudentPortalBean>> getAllStudentAnnouncements(HttpServletRequest request, HttpServletResponse response , @RequestBody PersonStudentPortalBean input){
		
		
		HttpHeaders headers = new HttpHeaders(); 
		headers.add("Content-Type", "application/json"); 
		String userId = input.getUserId();

		
		/* Logic shifted in service layer
		PortalDao dao = (PortalDao)act.getBean("portalDAO");
		StudentBean student =dao.getSingleStudentsData(userId);


		Page<AnnouncementBean> announcementspage = new Page<AnnouncementBean>();
		String consumerProgramStructureId = student.getConsumerProgramStructureId();
		*/


		List<AnnouncementStudentPortalBean> announcements = announcementService.getAllStudentAnnouncements(userId);	
			

		/*
		//Added temp for hiding Announcements for new batch
		if(student.getEnrollmentMonth().equalsIgnoreCase("Oct")) {
			announcements = new ArrayList<AnnouncementBean>();
		}
		//int announcementSize = announcements != null ? announcements.size() : 0;
		*/
		return new ResponseEntity<List<AnnouncementStudentPortalBean>>(announcements, headers, HttpStatus.OK);


	}
}
