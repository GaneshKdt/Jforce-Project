package com.nmims.controllers;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


import com.nmims.beans.DemoExamAttendanceBean;
import com.nmims.beans.DemoExamBean;
import com.nmims.beans.MbaWxDemoExamScheduleDetailBean;
import com.nmims.beans.StudentExamBean;
import com.nmims.daos.DemoExamDAO;
import com.nmims.daos.ExamBookingDAO;
import com.nmims.helpers.DemoExamHelper;
import com.nmims.helpers.TeeSSOHelper;
import com.nmims.services.DemoExamServices;

@Controller
public class DemoExamController extends BaseController {
	@Autowired
	ApplicationContext act;
	
	@Autowired
	DemoExamHelper demoExamHelper; 
	
	@Autowired
	DemoExamDAO demoExamDAO;
	
	@Autowired
	ExamBookingDAO examBookingDAO;
	
	@Autowired
	DemoExamServices demoExamService;
	
	@Value( "${SERVER_PATH}" )
	private String SERVER_PATH;
	
	@Value("${ICLCRESTRICTED_USER_LIST}")
	private List<String> ICLCRESTRICTED_USER_LIST;
	
	@RequestMapping(value = "/admin/viewDemoExamList", method = RequestMethod.GET)
	public ModelAndView viewDemoExamList(HttpServletRequest request,HttpServletResponse response) {
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("demoexam");
		DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
		List<DemoExamBean> demoBeansList = demoExamDAO.getDemoExamList();
		List<String> subjectList = demoExamDAO.getSubjectList();
		mv.addObject("demoExamList", demoBeansList);
		mv.addObject("subjectList", subjectList);
		return mv;
	}
	
	@RequestMapping(value = "/admin/demoExamSubjectLinkMapping", method = RequestMethod.POST)
	public ModelAndView demoExamSubjectLinkMapping(HttpServletRequest request, @ModelAttribute DemoExamBean demoExamBean){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		String userId = (String)request.getSession().getAttribute("userId");
		DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
		List<DemoExamBean> demoExamBeanList =  demoExamHelper.readExcelSheet(request, demoExamBean);
		List<DemoExamBean> demoExamBeanErrorList = new ArrayList<DemoExamBean>();
		int successfullyInserted = 0;
		for(int i=0;i < demoExamBeanList.size();i++) {
			demoExamBeanList.get(i).setLastmodified_by(userId);
			if(demoExamDAO.checkInsideProgramSemSubject(demoExamBeanList.get(i))) {
				if(!demoExamDAO.insertDemoExam(demoExamBeanList.get(i))) {
					demoExamBeanErrorList.add(demoExamBeanList.get(i));
				}else {
					successfullyInserted++;
				}
			}else {
				demoExamBeanErrorList.add(demoExamBeanList.get(i));
			}
		}
		ModelAndView mv = new ModelAndView("demoexam");
		List<DemoExamBean> demoBeansList = demoExamDAO.getDemoExamList();
		List<String> subjectList = demoExamDAO.getSubjectList();
		mv.addObject("demoExamList", demoBeansList);
		mv.addObject("subjectList", subjectList);
		if(successfullyInserted > 0) {
			request.setAttribute("success", "true");
			request.setAttribute("successMessage", "Successfully inserted record : " + successfullyInserted);
		}
		if(demoExamBeanErrorList.size() > 0) {
			mv.addObject("errorFlag","true");
			mv.addObject("demoExamBeanErrorList", demoExamBeanErrorList);
		}
		return mv;  
	}
	
	@RequestMapping(value = "/admin/createDemoExamRecord", method = RequestMethod.POST)
	public ResponseEntity<DemoExamBean> createDemoExamRecord(HttpServletRequest request, @RequestBody DemoExamBean demoExamBean){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		String userId = (String)request.getSession().getAttribute("userId");
		DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
		demoExamBean.setLastmodified_by(userId);
		DemoExamBean responseData = new DemoExamBean();
		if(demoExamDAO.insertDemoExam(demoExamBean)) {
			responseData.setStatus("success");
			responseData.setMessage("Successfully demo exam record created");
		}else {
			responseData.setStatus("error");
			responseData.setMessage("Failed to create exam record");
		}
		return new ResponseEntity<DemoExamBean>(responseData,headers, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/admin/deleteDemoExamRecord", method = RequestMethod.POST)
	public ResponseEntity<DemoExamBean> deleteDemoExamRecord(@RequestBody DemoExamBean demoExamBean){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
		DemoExamBean responseData = new DemoExamBean();
		if(demoExamDAO.deleteDemoExam(demoExamBean)) {
			responseData.setStatus("success");
			responseData.setMessage("Successfully demo exam record deleted");
		}else {
			responseData.setStatus("error");
			responseData.setMessage("Failed to delete exam record");
		}
		return new ResponseEntity<DemoExamBean>(responseData,headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/updateDemoExamRecord", method = RequestMethod.POST)
	public ResponseEntity<DemoExamBean> updateDemoExamRecord(HttpServletRequest request,@RequestBody DemoExamBean demoExamBean){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
		String userId = (String)request.getSession().getAttribute("userId");
		demoExamBean.setLastmodified_by(userId);
		DemoExamBean responseData = new DemoExamBean();
		if(demoExamDAO.updateDemoExam(demoExamBean)) {
			responseData.setStatus("success");
			responseData.setMessage("Successfully demo exam record updated");
		}else {
			responseData.setStatus("error");
			responseData.setMessage("Failed to update exam record");
		}
		return new ResponseEntity<DemoExamBean>(responseData,headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/admin/demoExamDashBoard", method = RequestMethod.GET)
	public ModelAndView demoExamDashBoard(HttpServletRequest request,HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("demoExamDashBoard");
		DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		//List<StudentBean> demoExamAttendanceBeans = demoExamDAO.getAttendStudent(getAuthorizedCodes(request));
		int countOfNotAttendStudent = 0;
		int countOfAttendStudent = 0;
		List<StudentExamBean> examBookingList = demoExamDAO.getCurrentCycleExamBookedStudentList(getAuthorizedCodes(request), eDao.getLiveExamYear(), eDao.getLiveExamMonth());
		List<String> attendedSapidList = demoExamDAO.getAttendStudentDemoExamAttendanceSapids();
		List<StudentExamBean> demoExamAttendedList = new ArrayList<StudentExamBean>();
		for (StudentExamBean studentBean : examBookingList) {
			if(!attendedSapidList.contains(studentBean.getSapid())) {
				countOfNotAttendStudent++;
			}else {
				int totalAttempt = 0;
				for (String sapid : attendedSapidList) {
					if(sapid.equalsIgnoreCase(studentBean.getSapid())) {
						totalAttempt++;
					}
				}
				studentBean.setTotal(totalAttempt + "");
				demoExamAttendedList.add(studentBean);
				countOfAttendStudent++;
			}
		}
		mv.addObject("attendStudentCount", countOfAttendStudent);
		mv.addObject("attendStudentList", demoExamAttendedList);
		mv.addObject("notAttendStudentCount", countOfNotAttendStudent);
		return mv;
	}
	
	@RequestMapping(value = "/admin/demoExamOutGivenDashBoard", method = RequestMethod.GET)
	public ModelAndView demoExamOutGivenDashBoard(HttpServletRequest request,HttpServletResponse response){
		if(!checkSession(request, response)){
			redirectToPortalApp(response);
			return null;
		}
		ModelAndView mv = new ModelAndView("demoExamNotGivenDashBoard");
		DemoExamDAO demoExamDAO = (DemoExamDAO) act.getBean("demoExamDAO");
		ExamBookingDAO eDao = (ExamBookingDAO)act.getBean("examBookingDAO");
		//List<StudentBean> demoExamAttendanceBeans = demoExamDAO.getAttendStudent(getAuthorizedCodes(request));
		int countOfNotAttendStudent = 0;
		int countOfAttendStudent = 0;
		List<StudentExamBean> examBookingList = demoExamDAO.getCurrentCycleExamBookedStudentList(getAuthorizedCodes(request), eDao.getLiveExamYear(), eDao.getLiveExamMonth());
		List<String> attendedSapidList = demoExamDAO.getAttendStudentDemoExamAttendanceSapids();
		List<StudentExamBean> demoExamNotAttendedList = new ArrayList<StudentExamBean>();
		for (StudentExamBean studentBean : examBookingList) {
			if(!attendedSapidList.contains(studentBean.getSapid())) {
				countOfNotAttendStudent++;
				demoExamNotAttendedList.add(studentBean);
			}else {
				countOfAttendStudent++;
			}
		}
		mv.addObject("attendStudentCount", countOfAttendStudent);
		mv.addObject("notAttendStudentList", demoExamNotAttendedList);
		mv.addObject("notAttendStudentCount", countOfNotAttendStudent);
		return mv;
	}
	
	@RequestMapping(value = "/admin/downloadDemoExamReport", method = RequestMethod.GET)
	public ModelAndView downloadDemoExamReport(HttpServletRequest request, HttpServletResponse response)
			 {
		if (!checkSession(request, response)) {
			redirectToPortalApp(response);
			return null;
		}
		request.getSession().setAttribute("ICLCRESTRICTED_USER_LIST", ICLCRESTRICTED_USER_LIST);
		List<DemoExamBean> listOfDemoExamAttendaceReport = new ArrayList<DemoExamBean>();
		try {
		List<DemoExamBean> examBookingList = demoExamDAO.getCurrentCycleExamBookedStudentsList(getAuthorizedCodes(request), examBookingDAO.getLiveExamYear(), examBookingDAO.getLiveExamMonth());
		List<DemoExamBean> attendedSapidList = demoExamDAO.getAttendStudentDemoExamAttendanceSapidsAndLatestDateTime();
		
		Map<String, DemoExamBean> mapOfBeanBySapid = getMapOfBeanBySapid(attendedSapidList);
			for (DemoExamBean bean : examBookingList) {
				String	sapid = bean.getSapid();
				DemoExamBean attandence = mapOfBeanBySapid.get(sapid);
				if (attandence!=null) {

					bean.setCount(attandence.getCount());
					bean.setAttemptStatus("Y");

					bean.setLatestAttemptDateTime(attandence.getLatestAttemptDateTime());
					listOfDemoExamAttendaceReport.add(bean);
					continue;
				}

				bean.setCount("0");
				bean.setAttemptStatus("N");
				bean.setLatestAttemptDateTime("0");
				listOfDemoExamAttendaceReport.add(bean);
			}

		} catch (Exception e) {
		}
		
		return new ModelAndView("demoExamReportExcelView", "listOfDemoExamAttendaceReport",listOfDemoExamAttendaceReport);
	}
	
	
	public  Map<String, DemoExamBean> getMapOfBeanBySapid(List<DemoExamBean> attendedSapidList) throws Exception {
		
		Map<String, DemoExamBean> mapOfdemoExamBeanBysapid =attendedSapidList.stream().collect(Collectors.toMap(DemoExamBean::getSapid, demoExambean -> demoExambean));
		return mapOfdemoExamBeanBysapid;
	}
	
}

