package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.ForumAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.ForumDAO;
import com.nmims.helpers.MailSender;

@Controller
@RequestMapping("/admin")
public class ForumController extends BaseController{

	private ArrayList<String> subjectList = null;
	private final int pageSize = 10;
	@Autowired
	ApplicationContext act;
	
	
	@RequestMapping(value = "/createForumThreadForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView createForumThreadForm(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		
		ModelAndView modelnView = new ModelAndView("forum/createThread");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");

		
		ExamOrderAcadsBean forumLiveBean = dao.getForumCurrentlyLive();

		if(forumLiveBean != null){
			modelnView.addObject("forumLiveSession", forumLiveBean.getAcadMonth() + "-" + forumLiveBean.getYear());
		}else{
			modelnView.addObject("forumLiveSession", "Forum not live");
		}
		
		
		//ArrayList<String> facultySubjects = contentDao.getFacultySubjectList((String)request.getSession().getAttribute("userId_acads"));
		
		//fetching faculty subjects from sessions table
		ArrayList<String> facultySubjects = dao.getFacultySubjectListForSessions((String)request.getSession().getAttribute("userId_acads"), forumLiveBean.getYear(), forumLiveBean.getMonth());
		
		modelnView.addObject("subjectList",facultySubjects);
		modelnView.addObject("forumBean", new ForumAcadsBean());
		
		return modelnView;
	}
	//Newly Added by Vikas to search thread//
	@RequestMapping(value="/searchForumThreadForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView searchForumThreadForm(HttpServletRequest request, HttpServletResponse response){
		ModelAndView modelAndView = new ModelAndView("forum/searchForumThread");
		
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		//ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
		
		ExamOrderAcadsBean forumLiveBean = dao.getForumCurrentlyLive();
		//ArrayList<String> facultySubjects = contentDao.getFacultySubjectList((String)request.getSession().getAttribute("userId_acads"));
		
		//fetching faculty subjects from sessions table
		ArrayList<String> facultySubjects = dao.getFacultySubjectListForSessions((String)request.getSession().getAttribute("userId_acads"), forumLiveBean.getYear(), forumLiveBean.getMonth());
		
		modelAndView.addObject("subjectList",facultySubjects);
		modelAndView.addObject("forumBean", new ForumAcadsBean());
		return modelAndView;
	}
	
	@RequestMapping(value="/searchForumThread",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView searchForumThread(HttpServletRequest request, HttpServletResponse response,@ModelAttribute ForumAcadsBean forumBean){
		ModelAndView modelAndView = new ModelAndView("forum/searchForumThread");
		ForumDAO forumDao = (ForumDAO)act.getBean("forumDAO");
		//ContentDAO contentDao = (ContentDAO)act.getBean("contentDAO");
		ExamOrderAcadsBean forumLiveBean = forumDao.getForumCurrentlyLive();
		
		//ArrayList<String> subjectList = contentDao.getFacultySubjectList((String)request.getSession().getAttribute("userId_acads"));
		
		//fetching faculty subjects from sessions table
		ArrayList<String> subjectList = forumDao.getFacultySubjectListForSessions((String)request.getSession().getAttribute("userId_acads"), forumLiveBean.getYear(), forumLiveBean.getMonth());
		

		PageAcads<ForumAcadsBean> page = forumDao.getThreadsRelatedToSubjectPage(1, 100, forumBean);
		List<ForumAcadsBean> threadListRelatedToSubject = page.getPageItems();

		modelAndView.addObject("page", page);
		modelAndView.addObject("rowCount",page.getRowCount());
		modelAndView.addObject("subjectList",subjectList);
		modelAndView.addObject("forumBean", new ForumAcadsBean());
		if(threadListRelatedToSubject!=null){
			modelAndView.addObject("forumsRelatedToSubject", threadListRelatedToSubject);
		}else{
			setError(request,"No Forums Related To this subject");
		}
		
		return modelAndView;
	}
	@RequestMapping(value="/facultyReplyForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView facultyReplyForm(HttpServletRequest request, HttpServletResponse response){
		
		ModelAndView modelnview = new ModelAndView("forum/facultyReplyForm");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");

		String parentReplyId = request.getParameter("parentReplyId");
		
		ForumAcadsBean forumThreadBean = dao.getForumReplyById(parentReplyId);
		
		modelnview.addObject("originalPostId", forumThreadBean.getParentPostId());
		
	
		forumThreadBean.setParentReplyId(parentReplyId);
		modelnview.addObject("forumBean", forumThreadBean);
		
		return modelnview;
	}
	
	@RequestMapping(value="/editReplyForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView editReplyForm(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelnView = new ModelAndView("forum/editReply");
		
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		String id = request.getParameter("id");
		ForumAcadsBean forumThreadBeanReply = dao.getForumReplyById(id);
		String userId = (String)request.getSession().getAttribute("userId_acads");
		
		
		
		modelnView.addObject("forumBean",forumThreadBeanReply);
		
		return modelnView;
	}
	@RequestMapping(value="/editReply",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView editReply(HttpServletRequest request, HttpServletResponse response,@ModelAttribute ForumAcadsBean forumBean){
		
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		ModelAndView modelnView = new ModelAndView("forum/selfClose");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		dao.updateReply(forumBean);
		modelnView.addObject("originalPostId", forumBean.getParentPostId());
		setSuccess(request, "Reply Edited Successfully");
		
		return modelnView;
	}
	@RequestMapping(value="/facultyReply",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView facultyReply(HttpServletRequest request, HttpServletResponse response,@ModelAttribute ForumAcadsBean forumBean){
		
		
		ModelAndView modelnView = new ModelAndView("forum/selfClose");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
			
		forumBean.setCreatedBy((String)request.getSession().getAttribute("userId_acads"));
		forumBean.setLastModifiedBy((String)request.getSession().getAttribute("userId_acads"));
		modelnView.addObject("originalPostId", forumBean.getParentPostId());
		dao.createThreadReply(forumBean);
		
		
		return modelnView;
	}
	@RequestMapping(value="/viewForumResponse",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView viewForumResponse(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}

		ModelAndView modelnView = new ModelAndView("forum/viewForumResponse");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");

		String id = request.getParameter("id");
		String reply = request.getParameter("reply");
		ForumAcadsBean forumThreadBean = dao.getForumById(id);//This queries the main forum//
		forumThreadBean.setParentPostId(id);
		forumThreadBean.setId(Long.valueOf(id));
		ArrayList<ForumAcadsBean> threadReplies = new ArrayList<ForumAcadsBean>();
		//for sorting replies
		if("".equals(request.getParameter("orderBy")) || request.getParameter("orderBy")== null){
			String orderBy = "oldest";
			forumThreadBean.setOrderBy(orderBy);
		}else{
			String orderBy = (String)request.getParameter("orderBy");
			forumThreadBean.setOrderBy(orderBy);
		}
		if("oldest".equals(forumThreadBean.getOrderBy()) ){
			threadReplies = dao.getThreadRepliesOfMainThreadAsc(id);//This queries the reply's corresponding to main forum

			 Collections.sort(threadReplies, new Comparator<ForumAcadsBean>() {
			        @Override
			        public int compare(ForumAcadsBean object1, ForumAcadsBean object2) {
			        	Date o1=object1.getCreatedDate();
			        	Date o2=object2.getCreatedDate();
			            return  (o1).compareTo(o2);
			            
			        }
			    }); 
		}else if("newest".equals(forumThreadBean.getOrderBy())){
			
			threadReplies = dao.getThreadRepliesOfMainThreadOrderDsc(id);

			 Collections.sort(threadReplies, new Comparator<ForumAcadsBean>() {
			        @Override
			        public int compare(ForumAcadsBean object1, ForumAcadsBean object2) {
			        	Date o1=object1.getCreatedDate();
			        	Date o2=object2.getCreatedDate();
			            return  (o2).compareTo(o1);
			            
			        }
			    });
		}

		//end
		forumThreadBean.setThreadReplies(threadReplies);
		ArrayList<ForumAcadsBean> childThreadReplies = null;

		if(threadReplies != null && threadReplies.size() > 0 ){
			for (ForumAcadsBean forumBean : threadReplies) {

				childThreadReplies =dao.getThreadRepliesOfParentReply(forumBean.getId()+"");//This queries level 2 replies, i.e. reply to existing reply

				forumBean.setThreadReplies(childThreadReplies);

			}
		}
		if("true".equals(reply)){
			setSuccess(request,"Reply Posted Successfully!!");
		}

		modelnView.addObject("forumBean", forumThreadBean);
		modelnView.addObject("newest","viewForumResponse?id="+id+"&orderBy=newest");
		modelnView.addObject("oldest","viewForumResponse?id="+id+"&orderBy=oldest");

		return modelnView;
	}
	//Editable callouts//
	@RequestMapping(value="/editForumSubject",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String editForumSubject(@RequestParam String value,@RequestParam String pk, HttpServletRequest request){
		try{
			ForumDAO forumDao = (ForumDAO)act.getBean("forumDAO");
			forumDao.updateTitleOfForumThread(pk,value);
			return "{\"status\": \"success\", \"msg\": \"Status saved successfully!\"}";
		}catch(Exception e){
			  
			return "{\"status\": \"error\", \"msg\": \"Error in saving Status!\"}";
		}
	}
	
	@RequestMapping(value="/saveThreadReplyStatus",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String saveThreadReplyStatus(@RequestParam String value, @RequestParam String pk, HttpServletRequest request) {
		
		try{
			ForumDAO forumDao = (ForumDAO)act.getBean("forumDAO");
			forumDao.updateStatusOfThreadReply(pk,value);
			return "{\"status\": \"success\", \"msg\": \"Status saved successfully!\"}";
		}catch(Exception e){
			  
			return "{\"status\": \"error\", \"msg\": \"Error in saving Status!\"}";
		}
		
	}
	
	
	@RequestMapping(value="/saveForumStatus",method={RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String saveRequestStatus(@RequestParam String value, @RequestParam String pk, HttpServletRequest request) {
		
		try{
			ForumDAO forumDao = (ForumDAO)act.getBean("forumDAO");
			forumDao.updateStatusOfThread(pk,value);
			return "{\"status\": \"success\", \"msg\": \"Status saved successfully!\"}";
		}catch(Exception e){
			  
			return "{\"status\": \"error\", \"msg\": \"Error in saving Status!\"}";
		}
		
	}
	//end//
	@RequestMapping(value = "/createForumThread",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView createForumThread(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ForumAcadsBean forumBean){
		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		
		ModelAndView modelnView = new ModelAndView("forum/createThread");
		//ModelAndView modelnView = new ModelAndView("forum/thread");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		ExamOrderAcadsBean forumLiveBean = dao.getForumCurrentlyLive();
		if(forumLiveBean != null){
			modelnView.addObject("forumLiveSession", forumLiveBean.getAcadMonth() + "-" + forumLiveBean.getYear());
		}else{
			modelnView.addObject("forumLiveSession", "Forum not live");
		}
		
		
		forumBean.setMonth(forumLiveBean.getAcadMonth());
		forumBean.setYear(forumLiveBean.getYear());
		
		forumBean.setCreatedBy((String)request.getSession().getAttribute("userId_acads"));
		
		dao.createThread(forumBean);
		setSuccess(request, "Thread Created Successfully with status as " + forumBean.getStatus());
		
		//fetching faculty subjects from sessions table
		ArrayList<String> facultySubjects = dao.getFacultySubjectListForSessions((String)request.getSession().getAttribute("userId_acads"), forumLiveBean.getYear(), forumLiveBean.getMonth());
				
		modelnView.addObject("subjectList",facultySubjects);
		modelnView.addObject("forumBean", new ForumAcadsBean());
		
		return modelnView;
	}
	@RequestMapping(value = "/replyToForumThread",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView replyToForumThread(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ForumAcadsBean forumBean){

		if(!checkSession(request, response)){
			return new ModelAndView("login");
		}
		
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		forumBean.setCreatedBy((String)request.getSession().getAttribute("userId_acads"));
		forumBean.setLastModifiedBy((String)request.getSession().getAttribute("userId_acads"));
		dao.createThreadReply(forumBean);
		setSuccess(request, "Reply posted Successfully");
		
		return viewForumResponse(request, response);
	}
	
	//Report Abuse module//
	@RequestMapping(value="/reportAbuse",method={RequestMethod.GET,RequestMethod.POST})
	public String reportAbuse(HttpServletRequest request, HttpServletResponse response){
		String replyId = request.getParameter("id");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		ForumAcadsBean threadReplyBean = dao.getForumReplyById(replyId);
		
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		String reportee = "";
		if(student == null){
			PersonAcads loggedInAdmin = (PersonAcads)request.getSession().getAttribute("user_acads");
			reportee = loggedInAdmin.getFirstName() + " " + loggedInAdmin.getLastName();
		}else{
			reportee = student.getFirstName() + " " + student.getLastName() + " ( " +student.getSapid()+ ")";
		}
		
		String isLevel2Reply = request.getParameter("isLevel2Reply");
		ForumAcadsBean originalForumThreadBean = null;
		
		if("true".equals(isLevel2Reply)){
			String parentPostId = dao.getForumReplyById(threadReplyBean.getParentPostId()).getParentPostId();//Take parent of Level 2 reply
			originalForumThreadBean = dao.getForumById(parentPostId);//Find original post for subject
			originalForumThreadBean.setDescription(threadReplyBean.getDescription());//Abusive text comes from reply and not from original post
		}else{
			originalForumThreadBean = dao.getForumById(threadReplyBean.getParentPostId());//For finding subject
			originalForumThreadBean.setDescription(threadReplyBean.getDescription());//Abusive text comes from reply and not from original post
		}
		
		
		
		String reportedOn = "";
		ContentDAO pDao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean postedBy = pDao.getSingleStudentsData(threadReplyBean.getCreatedBy());
		if(postedBy != null){
			reportedOn = postedBy.getFirstName() + " " + postedBy.getLastName() + " ( " +postedBy.getSapid()+ ")";
		}
				
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.sendForumAbuseEmail(originalForumThreadBean, reportee, reportedOn);
		
		setSuccess(request, "Abuse Reported with Authority");
		return "forward:/admin/viewForumResponse?id="+originalForumThreadBean.getId()+"";
		
	}
	
	//end//
}
