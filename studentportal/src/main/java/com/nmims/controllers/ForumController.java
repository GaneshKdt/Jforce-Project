package com.nmims.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ExamOrderStudentPortalBean;
import com.nmims.beans.ForumStudentPortalBean;
import com.nmims.beans.SessionQueryAnswerStudentPortal;
import com.nmims.beans.StudentStudentPortalBean;
import com.nmims.daos.ForumDAO;
import com.nmims.daos.PortalDao;
import com.nmims.helpers.EmailHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.PersonStudentPortalBean;

@Controller
@RequestMapping("/student")
public class ForumController extends BaseController{

	private ArrayList<String> subjectList = null;
	
	@Autowired
	ApplicationContext act;
	
	@Autowired
	EmailHelper emailHelper;
	/*public ArrayList<String> getSubjectList(){
		if(this.subjectList == null){
			TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
			this.subjectList = dao.getAllSubjects();
		}
		return subjectList;
	}*/
	
	/*@RequestMapping(value = "/createForumThreadForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView createForumThreadForm(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ModelAndView modelnView = new ModelAndView("jsp/forum/createThread");
		
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		ExamOrderBean forumLiveBean = dao.getForumCurrentlyLive();
		if(forumLiveBean != null){
			modelnView.addObject("forumLiveSession", forumLiveBean.getAcadMonth() + "-" + forumLiveBean.getYear());
		}else{
			modelnView.addObject("forumLiveSession", "Forum not live");
		}
		modelnView.addObject("subjectList", getSubjectList());
		modelnView.addObject("forumBean", new ForumBean());
		
		return modelnView;
	}
	
	@RequestMapping(value = "/createForumThread",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView createForumThread(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ForumBean forumBean){
		ModelAndView modelnView = new ModelAndView("jsp/forum/createThread");
		//ModelAndView modelnView = new ModelAndView("jsp/forum/thread");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		ExamOrderBean forumLiveBean = dao.getForumCurrentlyLive();
		if(forumLiveBean != null){
			modelnView.addObject("forumLiveSession", forumLiveBean.getAcadMonth() + "-" + forumLiveBean.getYear());
		}else{
			modelnView.addObject("forumLiveSession", "Forum not live");
		}
		
		
		forumBean.setMonth(forumLiveBean.getAcadMonth());
		forumBean.setYear(forumLiveBean.getYear());
		
		forumBean.setCreatedBy((String)request.getSession().getAttribute("userId"));
		
		dao.createThread(forumBean);
		setSuccess(request, "Thread Created Successfully with status as " + forumBean.getStatus());
		
		return modelnView;
	}*/
	
	@RequestMapping(value = "/viewForumThreadChain", method = {RequestMethod.GET, RequestMethod.POST})
public ModelAndView viewForumThreadChain(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		ModelAndView modelnView = new ModelAndView("jsp/forum/threadChain");
		
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		String id = request.getParameter("id");
		
		String reply = request.getParameter("reply");
		
		ForumStudentPortalBean forumThreadBean = dao.getForumById(id);//This queries the main forum//
		forumThreadBean.setParentPostId(id);
		forumThreadBean.setId(Long.valueOf(id));

		//for sorting replies
		if("".equals(request.getParameter("orderBy")) || request.getParameter("orderBy")== null){
			String orderBy = "oldest";
			forumThreadBean.setOrderBy(orderBy);
		}else{
			String orderBy = (String)request.getParameter("orderBy");
			forumThreadBean.setOrderBy(orderBy);
		}
	
		ArrayList<ForumStudentPortalBean> threadReplies = new ArrayList<ForumStudentPortalBean>();
		
		if("oldest".equals(forumThreadBean.getOrderBy()) ){
			threadReplies = dao.getThreadRepliesOfMainThread(id);//This queries the reply's corresponding to main forum
			 Collections.sort(threadReplies, new Comparator<ForumStudentPortalBean>() {
			        @Override
			        public int compare(ForumStudentPortalBean object1, ForumStudentPortalBean object2) {
			        	String o1=object1.getCreatedDate();
			        	String o2=object2.getCreatedDate();
			            return  (o1).compareTo(o2);
			            
			        }
			    }); 
		}else if("newest".equals(forumThreadBean.getOrderBy())){
			threadReplies = dao.getThreadRepliesOfMainThreadOrderAsc(id);

			 Collections.sort(threadReplies, new Comparator<ForumStudentPortalBean>() {
			        @Override
			        public int compare(ForumStudentPortalBean object1, ForumStudentPortalBean object2) {
			        	String o1=object1.getCreatedDate();
			        	String o2=object2.getCreatedDate();
			            return  (o2).compareTo(o1);
			            
			        }
			    });
		}
		//end
		forumThreadBean.setThreadReplies(threadReplies);

		
		ArrayList<ForumStudentPortalBean> childThreadReplies = null;
		if(threadReplies != null && threadReplies.size() > 0 ){
			for (ForumStudentPortalBean forumBean : threadReplies) {
				
				childThreadReplies =dao.getThreadRepliesOfParentReply(forumBean.getId()+"");//This queries level 2 replies, i.e. reply to existing reply
				forumBean.setThreadReplies(childThreadReplies);
			}
		}
		
		if("true".equals(reply)){
			setSuccess(request,"Reply Posted Successfully!!");
		}
		
		modelnView.addObject("forumBean", forumThreadBean);
		modelnView.addObject("newest","viewForumThreadChain?id="+id+ "&orderBy=newest");
		modelnView.addObject("oldest","viewForumThreadChain?id="+id+ "&orderBy=oldest");

		return modelnView;
	}
	
	@RequestMapping(value="/reportAbuse",method={RequestMethod.GET,RequestMethod.POST})
	public String reportAbuse(HttpServletRequest request, HttpServletResponse response){
		String replyId = request.getParameter("id");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		ForumStudentPortalBean threadReplyBean = dao.getForumReplyById(replyId);
		
		StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		String reportee = "";
		if(student == null){
			PersonStudentPortalBean loggedInAdmin = (PersonStudentPortalBean)request.getSession().getAttribute("user_studentportal");
			reportee = loggedInAdmin.getFirstName() + " " + loggedInAdmin.getLastName();
		}else{
			reportee = student.getFirstName() + " " + student.getLastName() + " ( " +student.getSapid()+ ")";
		}
		
		String isLevel2Reply = request.getParameter("isLevel2Reply");
		ForumStudentPortalBean originalForumThreadBean = null;
		
		if("true".equals(isLevel2Reply)){
			String parentPostId = dao.getForumReplyById(threadReplyBean.getParentPostId()).getParentPostId();//Take parent of Level 2 reply
			originalForumThreadBean = dao.getForumById(parentPostId);//Find original post for subject
			originalForumThreadBean.setDescription(threadReplyBean.getDescription());//Abusive text comes from reply and not from original post
		}else{
			originalForumThreadBean = dao.getForumById(threadReplyBean.getParentPostId());//For finding subject
			originalForumThreadBean.setDescription(threadReplyBean.getDescription());//Abusive text comes from reply and not from original post
		}
		
		
		
		String reportedOn = "";
		PortalDao pDao = (PortalDao)act.getBean("portalDAO");
		StudentStudentPortalBean postedBy = pDao.getSingleStudentsData(threadReplyBean.getCreatedBy());
		if(postedBy != null){
			reportedOn = postedBy.getFirstName() + " " + postedBy.getLastName() + " ( " +postedBy.getSapid()+ ")";
		}
				
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.sendForumAbuseEmail(originalForumThreadBean, reportee, reportedOn);
		
		setSuccess(request, "Abuse Reported with Authority");
		
		return "forward:/student/viewForumThreadChain?id="+originalForumThreadBean.getId()+"";
		
	}
	@RequestMapping(value = "/deleteForumReply", method = {RequestMethod.GET, RequestMethod.POST})
	public String deleteForumReply(HttpServletRequest request, HttpServletResponse response){
		String replyId = request.getParameter("replyId");
		String parentPostId =request.getParameter("parentPostId");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		dao.deleteThreadReply(replyId);
		
		setSuccess(request, "Reply Deleted Successfully");
		
		return "forward:/student/viewForumThreadChain?id="+parentPostId+"";
	}
	//Newly Added for student to edit his own reply//
	@RequestMapping(value="/editReplyForm",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView editReplyForm(HttpServletRequest request, HttpServletResponse response){
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/forum/editReply");
		
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
		String id = request.getParameter("id");
		ForumStudentPortalBean forumThreadBeanReply = dao.getForumReplyById(id);
		String userId = (String)request.getSession().getAttribute("userId");
		
		if(!userId.equals(forumThreadBeanReply.getCreatedBy())){
			setError(request, "You are not authorized to edit this reply");
			return new ModelAndView("jsp/forward:/student/viewForumThreadChain?id="+forumThreadBeanReply.getParentPostId()+"");
		}
		
		modelnView.addObject("forumBean",forumThreadBeanReply);
		
		return modelnView;
	}
	
	@RequestMapping(value="/editReply",method={RequestMethod.GET,RequestMethod.POST})
	public ModelAndView editReply(HttpServletRequest request, HttpServletResponse response,@ModelAttribute ForumStudentPortalBean forumBean){
		
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/forum/selfClose");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
	
		dao.updateReply(forumBean);
		modelnView.addObject("originalPostId", forumBean.getParentPostId());
		setSuccess(request, "Reply Edited Successfully");
		
		return modelnView;
	}
	
	
	//end//
	@RequestMapping(value = "/postLevel2ReplyForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView postLevel2ReplyForm(HttpServletRequest request, HttpServletResponse response) {
		
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		ModelAndView modelnView = new ModelAndView("jsp/forum/level2Reply");
		
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");

		String parentReplyId = request.getParameter("parentReplyId");
		
		ForumStudentPortalBean forumThreadBean = dao.getForumReplyById(parentReplyId);
		
		modelnView.addObject("originalPostId", forumThreadBean.getParentPostId());
		
	
		forumThreadBean.setParentReplyId(parentReplyId);
		modelnView.addObject("forumBean", forumThreadBean);
		
		return modelnView;
	}
	
	@RequestMapping(value = "/postLevel2Reply", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView postLevel2Reply(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ForumStudentPortalBean forumBean) {
		
		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		ModelAndView modelnView = new ModelAndView("jsp/forum/selfClose");
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
			
		forumBean.setCreatedBy((String)request.getSession().getAttribute("userId"));
		forumBean.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
		
		dao.createThreadReply(forumBean);
		setSuccess(request, "Reply posted Successfully");
		
		
		ForumStudentPortalBean originalForumThreadBean = dao.getForumReplyById(forumBean.getParentReplyId());
		modelnView.addObject("originalPostId", originalForumThreadBean.getParentPostId());
		/*
		String emailOfFaculty = dao.getEmailIdOfFaculty(originalForumThreadBean.getCreatedBy());
		MailSender mailer = (MailSender)act.getBean("mailer");
		mailer.notifyFacultyForResponse(originalForumThreadBean);
		*/
	
		
		return modelnView;
	}

	@RequestMapping(value = "/replyToForumThread",  method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView replyToForumThread(HttpServletRequest request, HttpServletResponse response, @ModelAttribute ForumStudentPortalBean forumBean){

		if(!checkSession(request, response)){
			return new ModelAndView("jsp/login");
		}
		
		ForumDAO dao = (ForumDAO)act.getBean("forumDAO");
			
		forumBean.setCreatedBy((String)request.getSession().getAttribute("userId"));
		forumBean.setLastModifiedBy((String)request.getSession().getAttribute("userId"));
		/*
		ForumBean originalThread = dao.getForumById(forumBean.getParentPostId());
		String emailIdOfFaculty = dao.getEmailIdOfFaculty(originalThread.getCreatedBy());
		
		originalThread.setFacultyEmail(emailIdOfFaculty);
		mailer.notifyFacultyForResponse(originalThread);
		MailSender mailer = (MailSender)act.getBean("mailer");
		*/
		dao.createThreadReply(forumBean);
		
		setSuccess(request, "Reply posted Successfully");
		
		return viewForumThreadChain(request, response);
	}

}
