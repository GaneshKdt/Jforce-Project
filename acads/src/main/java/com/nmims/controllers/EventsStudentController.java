package com.nmims.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.EventBean;
import com.nmims.daos.EventsDao;

@Controller
public class EventsStudentController extends BaseController
{
	
	@Autowired(required=false)
	ApplicationContext act;



	
	@RequestMapping(value="/student/keyEvents", method=RequestMethod.GET)
	public ModelAndView getKeyEvents(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelAndView =new ModelAndView("keyEvents");
		EventsDao dao = (EventsDao)act.getBean("eventsDao");
		//String userId= (String)request.getSession().getAttribute("sapId");
		List<EventBean> eventsList=dao.getAllEvents();
		request.getSession().setAttribute("eventsList", eventsList);
		 
		return modelAndView;
	}
}
