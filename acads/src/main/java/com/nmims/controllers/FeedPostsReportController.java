package com.nmims.controllers;

import java.sql.Timestamp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;



import com.nmims.beans.EndPointBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FeedPostsBean;
import com.nmims.beans.FileAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.PersonAcads;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SessionAttendanceFeedbackAcads;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.SessionReviewBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.TestAcadsBean;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.FeedPostsDAO;
import com.nmims.daos.LDAPDao;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.SessionReviewDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.helpers.AESencrp;
import com.nmims.helpers.ConferenceBookingClient;
import com.nmims.helpers.ExcelHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.SalesforceHelper;
import com.nmims.helpers.WebExMeetingManager;



@Controller
public class FeedPostsReportController {
	

	@Autowired(required=false)
	ApplicationContext act;
	
	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	

	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}")
	private List<String> ACAD_MONTH_LIST;
	

	
	@RequestMapping(value = "/admin/feedPostsReportForm", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView feedPostsReportForm(HttpServletRequest request, HttpServletResponse respnse) {
		ModelAndView mav = new ModelAndView("feedPostsReportForm");
		FeedPostsBean bean = new FeedPostsBean();
		mav.addObject("feedPostsBean", bean);
		mav.addObject("feedPostsList", null);		
		request.getSession().setAttribute("feedPostsList", null);
		mav.addObject("acadYearList", ACAD_YEAR_LIST);		
		mav.addObject("acadMonthList", ACAD_MONTH_LIST);		

		mav.addObject("feedPostsListSize", 0);
		
		
		return mav;
		
	}
	
	@RequestMapping(value = "/admin/feedPostsReport", method = {RequestMethod.POST})
	public ModelAndView feedPostsReport(HttpServletRequest request, HttpServletResponse respnse,@ModelAttribute FeedPostsBean bean) {		
		
		ModelAndView mav = new ModelAndView("feedPostsReportForm");		
		FeedPostsDAO feedPostsDAO = (FeedPostsDAO)act.getBean("feedPostsDAO");

		List<FeedPostsBean> feedPostsList = feedPostsDAO.getAllFeedPosts(bean.getAcadYear(), bean.getAcadMonth());
		List<FeedPostsBean> feedCommentsList = feedPostsDAO.getAllFeedComments(bean.getAcadYear(), bean.getAcadMonth());
		
		
		mav.addObject("feedPostsBean", bean);		
		mav.addObject("feedPostsList", feedPostsList);			
		request.getSession().setAttribute("feedPostsList", feedPostsList);
		request.getSession().setAttribute("feedCommentsList", feedCommentsList);
		
		mav.addObject("acadYearList", ACAD_YEAR_LIST);	
		mav.addObject("acadMonthList", ACAD_MONTH_LIST);			
		mav.addObject("feedPostsListSize", feedPostsList.size());
		return mav;
		
	}
	@RequestMapping(value = "/admin/downloadFeedPostsReport", method =  RequestMethod.GET)
	public ModelAndView downloadFeedPostsReport(HttpServletRequest request,
						   HttpServletResponse response ) {		
	
		List<FeedPostsBean> feedPostsList = new ArrayList<FeedPostsBean>();
		List<FeedPostsBean> feedCommentsList = new ArrayList<FeedPostsBean>();

		try {
			feedPostsList = (List<FeedPostsBean>) request.getSession().getAttribute("feedPostsList");
			feedCommentsList = (List<FeedPostsBean>) request.getSession().getAttribute("feedCommentsList");

		}catch(Exception e) {
			  
		}
		
		request.getSession().setAttribute("feedCommentsListForExcel", feedCommentsList);

		
		return new ModelAndView("FeedPostsExcelView","feedPostsList",feedPostsList);

	}
	
	@RequestMapping(value = "/admin/getCommentsByPostId", method =  RequestMethod.POST)
	public ResponseEntity<ArrayList<FeedPostsBean>>  getCommentsByPostId(@RequestBody FeedPostsBean feedPostsBean ) {		
		FeedPostsDAO feedPostsDAO = (FeedPostsDAO)act.getBean("feedPostsDAO");

		String post_id = feedPostsBean.getPost_id();
		
		return new ResponseEntity<ArrayList<FeedPostsBean>>(feedPostsDAO.getCommentsByPostId(post_id),HttpStatus.OK);

	}
	
}

	