package com.nmims.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.AnnouncementStudentPortalBean;
import com.nmims.beans.PageStudentPortal;
import com.nmims.interfaces.AnnouncementServiceInterface;

@Controller
public class AnnouncementStudentController extends BaseController
{
	

	@Autowired
	AnnouncementServiceInterface announcementService;
	
	private final int pageSize = 20;
	
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
		PageStudentPortal<AnnouncementStudentPortalBean> page = announcementService.getAllAnnouncementByUserId(userId,pageNo,pageSize);
		List<AnnouncementStudentPortalBean> announcements = page.getPageItems();
		
		
		modelnView.addObject("page", page);
		modelnView.addObject("rowCount", page.getRowCount());
		
		if(announcements == null || announcements.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No Announcements Found.");
		}
		
		modelnView.addObject("announcementsPortal", announcements);
		return modelnView;
	}

}
