package com.nmims.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.nmims.beans.ConfigurationExam;
import com.nmims.beans.ExamBookingTransactionBean;
import com.nmims.beans.Page;
import com.nmims.beans.PassFailExamBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.beans.StudentMarksBean;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.daos.PassFailDAO;
import com.nmims.daos.StudentMarksDAO;
import com.nmims.helpers.MailSender;

/**
 * Handles requests for the application home page.
 */
@Controller
public class SupportController extends BaseController{

	@Autowired
	ApplicationContext act;

	private static final Logger logger = LoggerFactory.getLogger(SupportController.class);
	private final int pageSize = 10;
	private static final int BUFFER_SIZE = 4096;
	
	@Autowired
	PassFailDAO passFailDao;
	
	@Autowired
	StudentMarksDAO studentMarksDAO;
	
	@Autowired
	ExamBookingDAO examBookingDAO;
	
	/*@Autowired
	ExamBookingDAO examBookingDAO;*/
	
	@RequestMapping(value = "/viewStudentDetails", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView makeResultsLiveForm(HttpServletRequest request, HttpServletResponse respnse, Model m) {
		ModelAndView modelnView = new ModelAndView("support/studentDetails");
		String sapId = request.getParameter("sapId");
		
		ArrayList<PassFailExamBean> passFailResults = passFailDao.getPassFailRecords(sapId);
		int passFailRowCount = passFailResults != null ? passFailResults.size() : 0;
		m.addAttribute("passFailRowCount", passFailRowCount);
		m.addAttribute("passFailResults", passFailResults);
		
		ArrayList<StudentMarksBean> marksResults = studentMarksDAO.getStudentMarks(sapId);
		int marksRowCount = marksResults != null ? marksResults.size() : 0;
		m.addAttribute("marksRowCount", marksRowCount);
		m.addAttribute("marksResults", marksResults);
		
		ArrayList<ExamBookingTransactionBean> examBookingResults = examBookingDAO.getAllConfirmedBookingPastToPresent(sapId);
		int examBookingsRowCount = examBookingResults != null ? examBookingResults.size() : 0;
		m.addAttribute("examBookingsRowCount", examBookingsRowCount);
		m.addAttribute("examBookingResults", examBookingResults);
		
		return modelnView;
	}
	
}

