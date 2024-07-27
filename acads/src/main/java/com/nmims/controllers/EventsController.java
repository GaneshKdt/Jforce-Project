package com.nmims.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.EventBean;
import com.nmims.daos.EventsDao;

@Controller
public class EventsController extends BaseController{
	@Autowired(required=false)
	ApplicationContext act;

	//Commented By Riya as mapping is shifted in EventsStudentController
	
	/*@RequestMapping(value="/keyEvents", method=RequestMethod.GET)
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
	}*/
	
	@RequestMapping(value="/admin/eventForm", method=RequestMethod.GET)
	public ModelAndView getEventForm(HttpServletRequest request, HttpServletResponse response) {
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelAndView =new ModelAndView("event");
		EventsDao dao = (EventsDao)act.getBean("eventsDao");
		//String userId= (String)request.getSession().getAttribute("userId_acads");
		List<EventBean> eventsList=dao.getAllEvents();
		modelAndView.addObject("eventsList", eventsList);
		 
		modelAndView.addObject("event", new EventBean());
		modelAndView.addObject("action", "Add Event");
		return modelAndView;
	}
	
	@RequestMapping(value="/admin/postEvent", method=RequestMethod.POST)
	public ModelAndView postEvent(@ModelAttribute EventBean event, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelAndView =new ModelAndView("event");
		EventsDao dao = (EventsDao)act.getBean("eventsDao");
		String userId= (String)request.getSession().getAttribute("userId_acads");
		
		DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			if(0==event.getId()) {
				event.setStartDateTime(event.getStartDateTime().replaceAll("T", " "));
				event.setEndDateTime(event.getEndDateTime().replaceAll("T", " "));
				event.setCreatedBy(userId);
				event.setCreatedDateTime(df.format(new Date()));
				event.setLastModifiedBy(userId);
				event.setLastModifiedDateTime(df.format(new Date()));
			long key =dao.saveEvent(event);

			setSuccess(request, "Event has been saved successfully");
			}else {
				if(event.getLastModifiedDateTime() !=null) {
					event.setLastModifiedBy(userId);
					event.setLastModifiedDateTime(df.format(new Date()));
				}
				event.setStartDateTime(event.getStartDateTime().replaceAll("T", " "));
				event.setEndDateTime(event.getEndDateTime().replaceAll("T", " "));
				
				boolean eventUpdated=dao.updateEvent(event);
				if(eventUpdated) {
					setSuccess(request, "Event has been updated successfully");
				}
			}
		} catch (Exception e) {
			  
		}
		
		List<EventBean> eventsList=dao.getAllEvents();
		modelAndView.addObject("eventsList", eventsList);
		modelAndView.addObject("event", new EventBean());
		modelAndView.addObject("action", "Add Event");
		return modelAndView;
	}
	
	@RequestMapping(value="/admin/editEvent", method=RequestMethod.GET)
	public ModelAndView editEvent(@RequestParam("id") int id,
								  HttpServletRequest request,
								  HttpServletResponse response ) {
		ModelAndView modelAndView =new ModelAndView("event");
		EventsDao dao = (EventsDao)act.getBean("eventsDao");
		
		EventBean event = dao.getEventById(id);
		if(event == null){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Event Not Found");
			modelAndView.addObject("event", new EventBean());
			modelAndView.addObject("action", "Add Event");
		}else {
			try {
				event.setStartDateTime(event.getStartDateTime().replaceAll(" ", "T"));
				event.setEndDateTime(event.getEndDateTime().replaceAll(" ", "T"));
			} catch (Exception e) {
				  
			}
			modelAndView.addObject("event", event);
			modelAndView.addObject("action", "Edit Event");
		}
		List<EventBean> eventsList=dao.getAllEvents();
		modelAndView.addObject("eventsList", eventsList);
		return modelAndView;
	}
	@RequestMapping(value="/admin/deleteEvent", method=RequestMethod.GET)
	public ModelAndView deleteEvent(@RequestParam("id") int id,
								  HttpServletRequest request ) {
		ModelAndView modelAndView =new ModelAndView("event");
		EventsDao dao = (EventsDao)act.getBean("eventsDao");
		
		int deletedRow=dao.deleteEvent(id);
		if(deletedRow==0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "Error Occured while deleting the event.");
			modelAndView.addObject("event", new EventBean());
			modelAndView.addObject("action", "Add Event");
		}else {
			modelAndView.addObject("event", new EventBean());
			modelAndView.addObject("action", "Add Event");
			setSuccess(request, "Event has been deleted successfully");
		}
		List<EventBean> eventsList=dao.getAllEvents();
		modelAndView.addObject("eventsList", eventsList);
		return modelAndView;
	}
}
