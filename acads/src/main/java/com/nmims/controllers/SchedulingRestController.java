package com.nmims.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.ParallelSessionBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.TimeTableDAO;

@RestController
@CrossOrigin(origins="*", allowedHeaders="*")
@RequestMapping("m")
public class SchedulingRestController extends BaseController {
	
	@Autowired(required = false)
	ApplicationContext act;
	
	private HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord = null;
	
	/*@PostMapping(value = "/viewScheduledSession", consumes = "application/json", produces = "application/json")
	public ResponseEntity<List<ParallelSessionBean>> mviewScheduledSession(@RequestParam("id") String idString,
			@RequestBody StudentAcadsBean student) throws Exception {
//		ModelAndView modelnView = new ModelAndView("viewScheduledSession");
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		String userId = student.getSapid();
		ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");

		String id = idString;
		/* String classFullMessage = request.getParameter("classFullMessage"); */
	/*	TimeTableDAO dao = (TimeTableDAO) act.getBean("timeTableDAO");
		SessionDayTimeAcadsBean session = dao.findScheduledSessionById(id);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date sessiondate = df.parse(session.getDate());
		Date currdate = new Date();
		if (sessiondate.before(currdate)) {
			session.setTimeboundId(dao.getTimeboundIdByModuleID(session.getModuleId()));
			// session.setVideosOfSession(vdao.getVideosForSession(session.getId()));
		}
//		HashMap<String, Integer> facultyIdAndRemSeatsMap = dao.getMapOfFacultyIdAndRemainingSeats(id, session);
		HashMap<String, Integer> facultyIdAndRemSeatsMap = dao.getMapOfFacultyIdAndRemainingSeatsV2(session);
		List<ParallelSessionBean> response = new ArrayList<>();
		HashMap<String, FacultyAcadsBean> listOfAllFaculties = mapOfFacultyIdAndFacultyRecord();
		
		Iterator it = facultyIdAndRemSeatsMap.entrySet().iterator();
		int i = 1;
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			ParallelSessionBean parallelSessionBean = new ParallelSessionBean();
			parallelSessionBean.setFacultyId(pair.getKey().toString());
			parallelSessionBean.setSeats(pair.getValue().toString());
			FacultyAcadsBean facultyBean = listOfAllFaculties.get(pair.getKey());
			parallelSessionBean.setFirstName(facultyBean.getFirstName());
			parallelSessionBean.setLastName(facultyBean.getLastName());
			parallelSessionBean.setEmail(facultyBean.getEmail());
			parallelSessionBean.setActive(facultyBean.getActive());
			parallelSessionBean.setMobile(facultyBean.getMobile());
			parallelSessionBean.setCreatedBy(facultyBean.getCreatedBy());
			parallelSessionBean.setCreatedDate(facultyBean.getCreatedDate());
			parallelSessionBean.setLastModifiedBy(facultyBean.getLastModifiedBy());
			parallelSessionBean.setLastModifiedDate(facultyBean.getLastModifiedDate());
			parallelSessionBean.setSessionBean(session);
			
			if(session.getFacultyId().equalsIgnoreCase(pair.getKey().toString())) {
				parallelSessionBean.setJoinFor("HOST");
			}else if(session.getAltFacultyId().equalsIgnoreCase(pair.getKey().toString())) {
				parallelSessionBean.setJoinFor("ALTFACULTYID");
			}else if(session.getAltFacultyId2().equalsIgnoreCase(pair.getKey().toString())) {
				parallelSessionBean.setJoinFor("ALTFACULTYID2");
			}else if(session.getAltFacultyId3().equalsIgnoreCase(pair.getKey().toString())) {
				parallelSessionBean.setJoinFor("ALTFACULTYID3");
			}
			
			response.add(parallelSessionBean);

			it.remove(); // avoids a ConcurrentModificationException

		}

//		String sessionDate = session.getDate();
//		String sessionTime = session.getStartTime();
//
//		Date sessionDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sessionDate + " " + sessionTime);
//		long minutesToSession = getDateDiff(new Date(), sessionDateTime, TimeUnit.MINUTES);
//		long minutesAfterSession = getDateDiff(sessionDateTime, new Date(), TimeUnit.MINUTES);


//for(int i = 0; i < facultyIdAndRemSeatsMap_list.size(); i++) {
//	 String s = listOfAllFaculties.get(facultyIdAndRemSeatsMap_list.get[i]);
//
//
//}

//		if(minutesToSession < 60 && minutesToSession > -120){
//			modelnView.addObject("enableAttendButton", "true");
//
//			response.put("enableAttendButton", session_list);
//			response.put("showQueryButton", session_list);
//			response.put("sessionOver", session_list);
//		}
//
//
//		if(minutesAfterSession > 120){
//			if(!"Guest Lecture".equalsIgnoreCase(session.getSessionName())){// added Temporary To hide Post My Query button for Guest Lecture
//				modelnView.addObject("showQueryButton", "true");
//			}
//			modelnView.addObject("sessionOver", "true");
//
//		}
//
//		String joinUrl = "";
//		String name = "Coordinator";
//		String email = "notavailable@mail.com";
//		String mobile = "0000000";
//		if(student != null){
//			name = student.getFirstName() + " "+ student.getLastName();
//			email = student.getEmailId() != null ? student.getEmailId() : "notavailable@mail.com";
//			mobile = student.getMobile() != null ? student.getMobile() : "0000000";
//		}
//
//		joinUrl = WEB_EX_API_URL + "?AT=JM&MK="+session.getMeetingKey()
//				+"&AN="+URLEncoder.encode(name, "UTF-8")
//				+"&AE="+URLEncoder.encode(email, "UTF-8")
//				+"&CO="+URLEncoder.encode(mobile, "UTF-8")
//				+"&PW="+session.getMeetingPwd();

//		modelnView.addObject("joinUrl", joinUrl);
//
//		String hostUrl = WEB_EX_LOGIN_API_URL+ "?AT=LI&WID="+session.getHostId()+"&PW="+session.getHostPassword()+"&BU="+WEB_EX_API_URL+"?MK="+session.getMeetingKey()+"%26AT=HM%26Rnd="+Math.random();
//		modelnView.addObject("hostUrl", hostUrl);
//		modelnView.addObject("SERVER_PATH", SERVER_PATH);
//
		return new ResponseEntity<List<ParallelSessionBean>>(response, headers, HttpStatus.OK);
	}*/
	
	public HashMap<String, FacultyAcadsBean> mapOfFacultyIdAndFacultyRecord() {
		FacultyDAO facultyDao = (FacultyDAO) act.getBean("facultyDAO");
		ArrayList<FacultyAcadsBean> listOfAllFaculties = facultyDao.getAllFacultyRecords();
		if (this.mapOfFacultyIdAndFacultyRecord == null) {
			this.mapOfFacultyIdAndFacultyRecord = new HashMap<String, FacultyAcadsBean>();
			for (FacultyAcadsBean faculty : listOfAllFaculties) {
				this.mapOfFacultyIdAndFacultyRecord.put(faculty.getFacultyId(), faculty);
			}
		}
		return mapOfFacultyIdAndFacultyRecord;

	}
	
	@RequestMapping(value = "/viewScheduleSessionByPSSId", method = { RequestMethod.GET })
	public ModelAndView viewScheduleSessionByPSSId(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("viewScheduleSessionByPSSId");
		TimeTableDAO dao = (TimeTableDAO)act.getBean("timeTableDAO");
		try {
			String pssId = request.getParameter("pssId");
			String year = request.getParameter("year");
			String month = request.getParameter("month");
			String track = request.getParameter("track");
			List<SessionDayTimeAcadsBean> sessionDayTimeBeanList = dao.viewScheduleSessionByPSSId(pssId,year,month,track);
			mv.addObject("sessionDayTimeBeanList", sessionDayTimeBeanList);
			mv.addObject("year", year);
			mv.addObject("month", month);
			mv.addObject("subject", sessionDayTimeBeanList.get(0).getSubject());
		}
		catch (Exception e) {
			// TODO: handle exception
			request.setAttribute("error","true");
			request.setAttribute("errorMessage","Something went wrong with these link, please try again after sometime, Error: " + e.getMessage());
		}
		
		return mv;
	}

}
