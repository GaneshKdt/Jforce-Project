package com.nmims.controllers;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.FacultyFilterBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SearchBean;
import com.nmims.beans.SessionQueryAnswer;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.FacultyDAO;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.helpers.HttpDownloadUtilityHelper;
import com.nmims.helpers.TextTrackImplLine;
import com.nmims.helpers.WebVttParser;
import com.nmims.services.ContentService;
import com.nmims.services.StudentCourseMappingService;
import com.nmims.services.VideoContentService;
import com.nmims.util.ContentUtil;

@Controller
@RequestMapping("/student")
public class VideoContentStudentController  extends BaseController  {
	
	@Autowired
	private VideoContentService videoContentService;
	
	@Autowired
	private SessionQueryAnswerDAO sessionQueryAnswerDAO;
	
	@Autowired
	private ContentService contentService;
	
	private ArrayList<String> subjectList = null;
	
	private final int pageSize = 10;
	
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;
	
	private List<SearchBean> acadCycleList = null;
	
	@Autowired
	StudentCourseMappingService studentCourseMappingService;
	
	private final String allField = "All";
	
	@ModelAttribute("acadCycleList")
	public List<SearchBean> acadCycleList() {
		if (this.acadCycleList == null)
			this.acadCycleList = videoContentService.fetchAcademicCycle();

		return acadCycleList;
	}
	
	@RequestMapping(value = "/videosHome", method = RequestMethod.GET)
	public ModelAndView getVideoHomePageNew(HttpServletRequest request,HttpServletResponse response, @RequestParam("academicCycle") String academicCycle) {
	
		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}
		
		ModelAndView modelAndView = new ModelAndView("videoHomeDemo");
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		
		ArrayList<VideoContentAcadsBean> sessionForLeads = new ArrayList<VideoContentAcadsBean>();
		
		if(checkLead(request, response)) {
			LeadDAO leadDAO = (LeadDAO) act.getBean("leadDAO");
			sessionForLeads = leadDAO.getSessionForLeads();
		}
	
		//ArrayList<String> allsubjects = applicableSubjectsForStudent(request);
		ArrayList<String> allsubjects = studentCourseMappingService.applicableSubjectsForStudentForWeb(request);
		
		
		ArrayList<ConsumerProgramStructureAcads> programSemSubjectIdWithSubject = dao.getProgramSemSubjectId(allsubjects, student.getConsumerProgramStructureId());
		ArrayList<String> programSemSubjectIds = new ArrayList<String>();
		for (ConsumerProgramStructureAcads bean : programSemSubjectIdWithSubject) {
			programSemSubjectIds.add(bean.getProgramSemSubjectId());
		}
		
		//Commented By Siddheshwar_Khanse
		//Get subject code id's based on the applicable subject of a student  
		/*List<ConsumerProgramStructure> subjectCodeIdWithSubject=videoContentService.fetchSubjectCodeIdsByApplicableSub(allsubjects, 
																				student.getConsumerProgramStructureId());*/
		
		//Preparing List of subjectCodeIds
		/*List<String> subjectCodeIds = new ArrayList<String>();
		for (ConsumerProgramStructure bean : subjectCodeIdWithSubject) {
			subjectCodeIds.add(bean.getSubjectCodeId());
		}*/
		
		int pageNo;
		
		try {
			pageNo = Integer.parseInt(request.getParameter("pageNo"));
			if (pageNo < 1) {
				pageNo = 1;
			}
		} catch (NumberFormatException e) {
			pageNo = 1;
			  
		}
		
		/*ArrayList<String> academicCycleList =new ArrayList<String>();
		academicCycleList=dao.getAcademicCycleList();
		ArrayList<String> academicCycleListForDb =new ArrayList<String>();
		
		if(academicCycle==null || "".equals(academicCycle) || "All".equals(academicCycle)){
			academicCycle="All";
			academicCycleListForDb.addAll(academicCycleList);
		} else{
			academicCycleListForDb.add(academicCycle);
		}*/
		
		List<SearchBean> academicCycleList = new ArrayList<SearchBean>();
		academicCycleList = videoContentService.fetchAcademicCycle();
		
		String acadCycleDateFormat = "";
		if(academicCycle==null || "".equals(academicCycle) || "All".equals(academicCycle)){
			academicCycle="All";
			//List<SearchBean> academicCycleList = new ArrayList<SearchBean>();
			//academicCycleList = videoContentService.fetchAcademicCycle();
			//Prepare all academic cycle into a single string 
			acadCycleDateFormat =ContentUtil.pepareAcadDateFormat(academicCycleList);
		} else{
			acadCycleDateFormat = ContentUtil.prepareAcadDateFormat(academicCycle);
		}
		
		List<String> commonSubjects =  getCommonSubjects(request);
		
		//Commented By Siddheshwar_Khanse
	    /*Page<VideoContentBean> page = dao.getVideoContentPageNew(pageNo, pageSize, programSemSubjectIds, academicCycleListForDb, student, commonSubjects);
		List<VideoContentBean> videoContentListPage = page.getPageItems();*/
		
		//Fetching applicable Session Recordings based on subjectCodeIDs,cycle,program and commonSubjects 
		/*Page<VideoContentBean> page = videoContentService.fetchSessionVideoBySubjectCodeId(pageNo, pageSize, subjectCodeIds, 
																	academicCycleListForDb, student.getProgram(), commonSubjects);
		List<VideoContentBean> videoContentListPage = page.getPageItems();*/
		
		PageAcads<VideoContentAcadsBean> page = videoContentService.fetchSessionVideos(pageNo, pageSize, programSemSubjectIds, 
														acadCycleDateFormat, commonSubjects);
		List<VideoContentAcadsBean> videoContentListPage = page.getPageItems();
		
		String PSSIds = "";
		PSSIds = ContentUtil.frameINClauseString(programSemSubjectIds);
				
		//Fetch applicable drop down values like subjects, tracks, faculties and acad cycles
		Map<String,List> filterValueMap = videoContentService.fetchSessionVideoDropdownValues(PSSIds, academicCycle, "All", "All");
		modelAndView.addObject("academicCycleList",videoContentService.getSessionVideosByAcadCycle(allField,programSemSubjectIds,allField,commonSubjects,student.getConsumerProgramStructureId(),academicCycle));
		

		//modelAndView.addObject("academicCycleList", academicCycleList);
		//request.getSession().setAttribute("academicCycleList", academicCycleList);
		
		modelAndView.addObject("academicCycle", academicCycle);
		modelAndView.addObject("page", page);
		modelAndView.addObject("rowCount", page.getRowCount());
		
		//modelAndView.addObject("allsubjects", allsubjects);
		//request.getSession().setAttribute("allsubjects", allsubjects);
		
	   // modelAndView.addObject("programSemSubjectIdWithSubject", programSemSubjectIdWithSubject);
	    modelAndView.addObject("programSemSubjectIdWithSubject",  programSemSubjectIdWithSubject);
	    request.getSession().setAttribute("programSemSubjectIdWithSubject", programSemSubjectIdWithSubject);
		
	    //Commented By Siddheshwar_Khanse
		//Set subjectCodeIdWithSubject in session and modelAndView
	    /*modelAndView.addObject("subjectCodeIdWithSubject", subjectCodeIdWithSubject);
		request.getSession().setAttribute("subjectCodeIdWithSubject", subjectCodeIdWithSubject);*/
		
		modelAndView.addObject("selectedSubject", "All");
		
		modelAndView.addObject("commonSubjects", commonSubjects);
		request.getSession().setAttribute("commonSubjects", commonSubjects);
		
		//for batch track filter
		//List<String> all_tracks=dao.getBatchTracksWithSubjectNames(allsubjects);
		
		List<String> tracks =  filterValueMap.get("TRACKS");
		modelAndView.addObject("allBatchTracks", tracks);
		request.getSession().setAttribute("allBatchTracks", tracks);
		modelAndView.addObject("selectedBatch", "All");
		
		//Faculty List
		//List<FacultyAcadsBean> all_facultyList= dao.getFacultiesForSubjects(allsubjects);
		List<FacultyAcadsBean> facultyList = filterValueMap.get("FACULTIES");
		modelAndView.addObject("facultyList",facultyList);
		request.getSession().setAttribute("facultyList", facultyList);
		FacultyAcadsBean tempFaculty=new FacultyAcadsBean();
		tempFaculty.setFacultyId("All");
		modelAndView.addObject("selectedFaculty",tempFaculty);
		
		modelAndView.addObject("searchItem","");
		modelAndView.addObject("showPagination", true);
		modelAndView.addObject("VideoContentsList", videoContentListPage);
		modelAndView.addObject("subjectVideosMap", null);
		
		request.setAttribute("VideoContentsList", videoContentListPage);
		request.setAttribute("subjectVideosMap", null);
		request.setAttribute("subjectList", getSubjectList());
		request.setAttribute("sessionForLeads", sessionForLeads);
		
		request.getSession().setAttribute("all_commonSubjectList", commonSubjects);
		modelAndView.addObject("defaultAcademicCycle",academicCycle);
		request.getSession().setAttribute("defaultAcademicCycle", academicCycle);
		request.getSession().setAttribute("pssIds", programSemSubjectIds);
	//	request.getSession().setAttribute("all_facultyList", all_facultyList);
	//	request.getSession().setAttribute("all_BatchTracks", all_tracks);
	//	request.getSession().setAttribute("all_subjectList", programSemSubjectIdWithSubject);
		request.getSession().setAttribute("all_cycleList", academicCycleList);
		
		return modelAndView;
	
	}
	
	public ArrayList<String> applicableSubjectsForStudent(HttpServletRequest request) {
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingAcadsBean> allsubjects = new ArrayList<>();
		
		ArrayList<ProgramSubjectMappingAcadsBean> unAttemptedSubjectsBeans = new ArrayList<>();
		
		String sapId = (String)request.getSession().getAttribute("userId_acads");
		//So admins/faculty would see Videos Page with all videos 
		if(!sapId.startsWith("7")) {
			request.getSession().setAttribute("applicableSubjects", subjectList);
			return subjectList;
		}
		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		StudentAcadsBean studentRegistrationData;
		
		String earlyAccess = (String)request.getSession().getAttribute("earlyAccess");
		// If isEarlyAccess then registration will not be available of this drive.
		if("Yes".equalsIgnoreCase(earlyAccess)) {
			studentRegistrationData= student;
		}else if(student.getProgram().equalsIgnoreCase("EPBM") || student.getProgram().equalsIgnoreCase("MPDV") ) {
			
			studentRegistrationData = cdao.getStudentMaxSemRegistrationData(sapId);
		}else{
			studentRegistrationData = cdao.getStudentRegistrationData(sapId);
		}

		if(studentRegistrationData == null){
			//Get fail subjects content if studnet does not have registration for current sem.
			failSubjectsBeans = getFailSubjects(student);
			
			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				allsubjects.addAll(failSubjectsBeans);
			}
			/*videoForSubject(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				allsubjects.addAll(failSubjectsBeans);
			}*/
			
		}else{
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			//student.setPrgmStructApplicable(studentRegistrationData.getPrgmStructApplicable());
			//student.setWaivedOffSubjects(studentRegistrationData.getWaivedOffSubjects());
			ArrayList<ProgramSubjectMappingAcadsBean> currentSemSubjects = getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
				request.getSession().setAttribute("currentSemSubjects", currentSemSubjects);
			}
			
			//If current sem is 1, then there will be no failed subjects. Get failed subjects only when he is in higher semesters
			if(!"1".equals(studentRegistrationData.getSem())){
				failSubjectsBeans = getFailSubjects(student);

				if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
					allsubjects.addAll(failSubjectsBeans);
				}
			}
		}
		
		//Get subjects never attempted or results not declared
		//Commented by Siddheshwar_K for fix Project Preparation Recording.
		//unAttemptedSubjectsBeans = cdao.getUnAttemptedSubjects(sapId);
		
		unAttemptedSubjectsBeans = cdao.getNotPassedSubjectsBasedOnSapid(sapId);
		if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			allsubjects.addAll(unAttemptedSubjectsBeans);
		}

		//Sort all subjects semester wise.
		Collections.sort(allsubjects);
		
		if(allsubjects.size() == 0){
			request.setAttribute("error", "true");
			request.setAttribute("errorMessage", "No subjects found for you."); 
		}
		ArrayList<String> applicableSubjects=new ArrayList<>();
		for(ProgramSubjectMappingAcadsBean psmb:allsubjects){
			if(!student.getWaivedOffSubjects().contains(psmb.getSubject())) {
				applicableSubjects.add(psmb.getSubject());
			}
		}
		applicableSubjects.add("Guest Session: GST by CA. Bimal Jain");
		
		//To add orientation subject only for Sem 1 student  
		/*VideoContentDAO vDao = (VideoContentDAO) act.getBean("videoContentDAO");
		StudentBean semCheck = vDao.getStudentsMostRecentRegistrationData(sapId);*/
		
		String stdSem = (String) request.getSession().getAttribute("currentSem");
		
		try{
		if("1".equals(stdSem)){
			applicableSubjects.add("Orientation");
		}
		//end
		

		applicableSubjects.add("Assignment");
		
		//Commented by Siddheshwar_K because 'Project' gets added in subjects list with PSS
		/*if("4".equals(stdSem)){
				applicableSubjects.add("Project Preparation Session");
		}*/
		}catch(Exception e){
		}
		
		//Remove orientation,Project n assignment subject for sas student
		try {

			if("EPBM".equalsIgnoreCase(student.getProgram()) || "MPDV".equalsIgnoreCase(student.getProgram()) ){
				applicableSubjects.remove("Assignment");
				
				//We are not adding 'Project Preparation Session' in applicableSubjects list so no need of remove
				/*if(applicableSubjects.contains("Project Preparation Session")) {
					applicableSubjects.remove("Project Preparation Session");
				}*/
				if(applicableSubjects.contains("Orientation")) {
					applicableSubjects.remove("Orientation");
				
					if("1".equals(stdSem)){
					applicableSubjects.add("Executive Program Orientation");
					}
				}
			}
		} catch (Exception e) {
			  
		}
		//end
		
		if(student.getWaivedInSubjects() != null) {
			for (String subject : student.getWaivedInSubjects()) {
				if(!applicableSubjects.contains(subject)) {
					applicableSubjects.add(subject);
				}
			}
		}
		
		request.getSession().setAttribute("failSubjectsBeans", failSubjectsBeans);
		request.getSession().setAttribute("applicableSubjects", applicableSubjects);
		return applicableSubjects;
				
	}
	
	public List<String> getCommonSubjects(HttpServletRequest request) {
		
		String sapId = (String)request.getSession().getAttribute("userId_acads");
		VideoContentDAO vDao = (VideoContentDAO) act.getBean("videoContentDAO");
		StudentAcadsBean studentRegistrationData = vDao.getStudentsMostRecentRegistrationData(sapId);
		
		List<String> commonSubjectList = new ArrayList<String>();
		//commonSubjectList.add("Guest Session: GST by CA. Bimal Jain");
		commonSubjectList.add("Assignment");
		
		if("1".equals(studentRegistrationData.getSem())){
			commonSubjectList.add("Orientation");
		}
		
		//Commented by Siddheshwar_K because 'Project' gets added in subjects list with PSS
		/*if("4".equals(studentRegistrationData.getSem())){
			commonSubjectList.add("Project Preparation Session");
		}*/
		return commonSubjectList;
	}
	
	
	@ModelAttribute("subjectList")
	public ArrayList<String> getSubjectList() {
		if (this.subjectList == null) {
			ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
			this.subjectList = dao.getActiveSubjects();
		}

		return subjectList;
	}

	private ArrayList<ProgramSubjectMappingAcadsBean> getFailSubjects(StudentAcadsBean student) {
		ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectList;
		try {
			failSubjectList = dao.getFailSubjectsForAStudent(student.getSapid());
		} catch (Exception e) {
			failSubjectList=new ArrayList<ProgramSubjectMappingAcadsBean>();
			  
		}
		return failSubjectList;
	}

	private ArrayList<ProgramSubjectMappingAcadsBean> getSubjectsForStudent(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = getProgramSubjectMappingList();
		ArrayList<ProgramSubjectMappingAcadsBean> subjects = new ArrayList<>();
		for (int i = 0; i < programSubjectMappingList.size(); i++) {
			ProgramSubjectMappingAcadsBean bean = programSubjectMappingList.get(i);

			if(
					bean.getPrgmStructApplicable().equals(student.getPrgmStructApplicable()) 
					&& bean.getProgram().equals(student.getProgram())
					&& bean.getSem().equals(student.getSem())
					&& !student.getWaivedOffSubjects().contains(bean.getSubject())//Subjects has not already cleared it
					){
				subjects.add(bean);

			}
		}
		return subjects;
	}
	
	public ArrayList<ProgramSubjectMappingAcadsBean> getProgramSubjectMappingList(){
		if(this.programSubjectMappingList == null || this.programSubjectMappingList.size() == 0){
			ContentDAO dao = (ContentDAO)act.getBean("contentDAO");
			this.programSubjectMappingList = dao.getProgramSubjectMappingList();
		}
		return programSubjectMappingList;
	} 
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/searchByFilter_old", method = RequestMethod.GET)
	public ModelAndView searchByFilterOld(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("searchInput") String searchItem, @RequestParam("faculty") String faculty,
			@RequestParam("subject") String programSemSubjectIds, @RequestParam("cycle") String cycle,
			@RequestParam("batch") String batch) {

		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		ArrayList<String> applicableSubjects = new ArrayList<String>();
		ArrayList<ConsumerProgramStructureAcads> programSemSubjectIdWithSubject = new ArrayList<ConsumerProgramStructureAcads>();
		String subject = "";

		// List<ConsumerProgramStructure> subjectCodeIdWithSubject = new
		// ArrayList<ConsumerProgramStructure>();
		ModelAndView modelAndView = new ModelAndView();
		String userId = (String) request.getSession().getAttribute("userId_acads");

		if (userId.startsWith("7")) {
			modelAndView.setViewName("videoHome");
			applicableSubjects = (ArrayList<String>) request.getSession().getAttribute("applicableSubjects");
			programSemSubjectIdWithSubject = (ArrayList<ConsumerProgramStructureAcads>) request.getSession()
					.getAttribute("programSemSubjectIdWithSubject");
			// subjectCodeIdWithSubject = (List<ConsumerProgramStructure>)
			// request.getSession().getAttribute("subjectCodeIdWithSubject");

		} else {
			modelAndView.setViewName("videoHomeAdmin");
			applicableSubjects = subjectList;
			applicableSubjects.add("Orientation");
			applicableSubjects.add("Executive Program Orientation");
		}

		// List<String> academicCycleList= (List<String>)
		// request.getSession().getAttribute("academicCycleList");
		List<SearchBean> academicCycleList = (List<SearchBean>) request.getSession().getAttribute("academicCycleList");
		List<FacultyAcadsBean> facultyList = (List<FacultyAcadsBean>) request.getSession().getAttribute("facultyList");
		List<String> commonSubjectList = (List<String>) request.getSession().getAttribute("commonSubjects");
		List<String> allBatchTrackList = (List<String>) request.getSession().getAttribute("allBatchTracks");

		programSemSubjectIds = !StringUtils.isBlank(programSemSubjectIds) ? programSemSubjectIds : "All";
		List<String> commonSubjects = new ArrayList<String>();
		List<String> tempSubject = new ArrayList<String>();

		if ("All".equalsIgnoreCase(programSemSubjectIds)) {
			commonSubjects = commonSubjectList;
			for (ConsumerProgramStructureAcads bean : programSemSubjectIdWithSubject) {
				tempSubject.add(bean.getProgramSemSubjectId());
			}
		} else {
			tempSubject.add(programSemSubjectIds);
		}

		/*Commented By Siddheshwar_Khanse
		//Set subjectCodeIds as 'All' if it blank else existing
		subjectCodeIds = !StringUtils.isBlank(subjectCodeIds) ? subjectCodeIds : "All";
		
		//If subjectCodeIds contains 'All' then get common subjects and prepare subjectCodeIds list
		/*if("All".equalsIgnoreCase(subjectCodeIds)) {
			commonSubjects =  commonSubjectList;
			for (ConsumerProgramStructure bean : subjectCodeIdWithSubject) {
				tempSubject.add(bean.getSubjectCodeId());
			}
		}else {
			tempSubject.add(subjectCodeIds);
		}*/

		if (cycle == null || "".equals(cycle))
			cycle = "All";

		List<VideoContentAcadsBean> VideoContentsList = new ArrayList<VideoContentAcadsBean>();

		if (commonSubjectList.contains(programSemSubjectIds) && !"All".equalsIgnoreCase(programSemSubjectIds)) {
			VideoContentsList = videoContentService.fetchVideoForSearchByFilterForCommonNew(searchItem, faculty,
					tempSubject, cycle, batch, student);
			/*VideoContentsList = videoContentService.fetchCommonSessionSubjectVideos(searchItem, faculty, tempSubject, 
																acadCycleDateFormat, batch, student);*/
		} else {
			VideoContentsList = videoContentService.fetchSessionVideosForFilter(searchItem, faculty, tempSubject, cycle,
					batch, commonSubjects, student.getProgram(), academicCycleList);
			/*if("Jan2021".equalsIgnoreCase(cycle) || "Jul2020".equalsIgnoreCase(cycle) || "All".equalsIgnoreCase(cycle)) {
				//VideoContentsList = dao.getVideoForSearchByFilterNew(searchItem, faculty, tempSubject, cycle, batch, student, commonSubjects);
			}
			else {
				//VideoContentsList=dao.getOldCycleVideoForSearchByFilter(searchItem, faculty, tempSubject, cycle, batch, student, commonSubjects);
				VideoContentsList=videoContentService.fetchOldCycleSessionVideosForFilter(searchItem, faculty, tempSubject, 
																		cycle, batch, student.getProgram(), commonSubjects);
			}*/
		}

		// Commented By Siddheshwar_Khanse
		// Get Session Recordings Common Sessions
		/*if (commonSubjectList.contains(subjectCodeIds) && !"All".equalsIgnoreCase(subjectCodeIds)) {
			VideoContentsList = videoContentService.fetchVideoForSearchByFilterForCommonNew(searchItem, faculty, tempSubject, 
																							cycle, batch, student);
		}else {
			//Get Session Recordings for Current Cycle and Previous Cycle
			if("Jan2021".equalsIgnoreCase(cycle) || "Jul2020".equalsIgnoreCase(cycle) || "All".equalsIgnoreCase(cycle)) {
				VideoContentsList = videoContentService.fetchSessionVideosForFilter(searchItem, faculty, tempSubject, 
																			cycle, batch, student.getProgram(), commonSubjects);
			}
			else {
				//Get Session Recordings for Old Cycle
				VideoContentsList=videoContentService.fetchOldCycleSessionVideosForFilter(searchItem, faculty, tempSubject, 
																			cycle, batch, student.getProgram(), commonSubjects);
			}
		}*/

		int size = VideoContentsList != null ? VideoContentsList.size() : 0;

		FacultyAcadsBean tempFaculty = new FacultyAcadsBean();
		if (!"All".equalsIgnoreCase(faculty)) {
			tempFaculty = fDao.findfacultyByFacultyId(faculty);
			faculty = "Prof. " + tempFaculty.getFirstName() + " " + tempFaculty.getLastName();
		} else {
			tempFaculty.setFacultyId("All");
		}

		// Commented By Siddheshwar_Khanse
		// Replace subjectCodeId with Subject Name
		// String subject =
		// videoContentService.fetchSubjectBySubjectCodeId(subjectCodeIds);

		if ("All".equalsIgnoreCase(programSemSubjectIds) || commonSubjectList.contains(programSemSubjectIds)
				|| "".equals(programSemSubjectIds))
			subject = programSemSubjectIds;
		else
			// Get subject name by PSS Id
			subject = videoContentService.fetchSubjectByPSSId(programSemSubjectIds);

		if (size > 0) {
			if ("".equals(searchItem)) {
				setSuccess(request, "Found " + size + " videos matching for Subject : " + subject + " and Faculty : "
						+ faculty + " and Academic Cycle : " + cycle + " and Batch Track : " + batch);
			} else {
				setSuccess(request,
						"Found " + size + " videos matching for \"" + searchItem + "\" and Subject : " + subject
								+ " and Faculty : " + faculty + " and Academic Cycle : " + cycle + " and Batch Track : "
								+ batch);
			}
		} else {
			setSuccess(request,
					"No videos matching your search for " + searchItem + "  and Subject : " + subject
							+ " and Faculty : " + faculty + " and Academic Cycle : " + cycle + " and Batch Track : "
							+ batch + ". Try some more specific keywords.");
		}

		request.setAttribute("VideoContentsList", VideoContentsList);
		modelAndView.addObject("academicCycleList", academicCycleList);
		modelAndView.addObject("academicCycle", cycle);
		modelAndView.addObject("selectedSubject", subject);
		modelAndView.addObject("programSemSubjectIds", programSemSubjectIds);
		// modelAndView.addObject("subjectCodeIds", subjectCodeIds);
		modelAndView.addObject("facultyList", facultyList);
		modelAndView.addObject("selectedFaculty", tempFaculty);
		modelAndView.addObject("searchItem", searchItem);
		modelAndView.addObject("commonSubjects", commonSubjectList);

		request.getSession().setAttribute("commonSubjects", commonSubjectList);
		request.setAttribute("subjectList", getSubjectList());

		modelAndView.addObject("programSemSubjectIdWithSubject", programSemSubjectIdWithSubject);
		request.getSession().setAttribute("programSemSubjectIdWithSubject", programSemSubjectIdWithSubject);

		// modelAndView.addObject("subjectCodeIdWithSubject", subjectCodeIdWithSubject);
		// request.getSession().setAttribute("subjectCodeIdWithSubject",
		// subjectCodeIdWithSubject);

		// for batch track filter
		modelAndView.addObject("selectedBatch", batch);
		modelAndView.addObject("allBatchTracks", allBatchTrackList);

		return modelAndView;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping("/videoForSubject")
	public ModelAndView videoForSubject(@RequestParam("subjectCodeId") String subjectCodeId,
			@RequestParam("subject") String subject, HttpServletRequest request,HttpServletResponse response) {

		//Check session is expired or not
		if(!checkSession(request, response))
			return new ModelAndView("studentPortalRediret");

		ModelAndView modelAndView = new ModelAndView("videoHomeDemo");
		List<VideoContentAcadsBean> videoContentList = new ArrayList<VideoContentAcadsBean>();
		//List<SearchBean> academicCycleList = (List<SearchBean>) request.getSession().getAttribute("academicCycleList");
		//List<String> commonSubjectList = (List<String>) request.getSession().getAttribute("commonSubjects");
		List<SearchBean> academicCycleList = null;
		List<String> commonSubjectList = null;
		List<String> programSemSubjectIds = null;
		List<ConsumerProgramStructureAcads> programSemSubjectIdWithSubject = new ArrayList<ConsumerProgramStructureAcads>();
		programSemSubjectIdWithSubject = (List<ConsumerProgramStructureAcads>) request.getSession().getAttribute("programSemSubjectIdWithSubject");
		
		if((programSemSubjectIdWithSubject == null)) {
		try {
			//Step 01 - Get program semester subjects id's with the subject name.
			programSemSubjectIdWithSubject = this.getProgramSemSubjectIdWithSubject(request);
			} catch (Exception e) {
					System.out.println(e.getMessage());
			}
		}
		
		//Step 02 - Prepare PSSIds list.
		programSemSubjectIds = this.getPrgmSemSubjectIdsList(programSemSubjectIdWithSubject);
		
		//Step 03 - Set Dynamic drop down values. 
		this.setDynamicDropDownValues(request, modelAndView, programSemSubjectIds);
		
		//Step 04 - Get academic cycles list from the session
		academicCycleList = (List<SearchBean>) request.getSession().getAttribute("academicCycleList");
		
		//Get common subjects
		commonSubjectList = (List<String>) request.getSession().getAttribute("commonSubjects");
		
		try {
			//If subjectCodeId does not contain null and subject does not contain in the common subject list 
			//then fetch session videos based on the subject code id Else for specific common subject.
			if(!"null".equals(subjectCodeId) && !commonSubjectList.contains(subject)) 
				videoContentList = videoContentService.fetchSessionVideosForSubject(Integer.parseInt(subjectCodeId),
						academicCycleList);
			else {
				StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
				//Fetch session videos for common subject selected by user.
				videoContentList = videoContentService.fetchSessionVideosBySearch(subject, null, 
						commonSubjectList, student.getProgram());
			}
		} catch (Exception e) {
			  
		}
		
		//Assign zero in the size if videoContentList is null else size of list.
		int size = videoContentList != null ? videoContentList.size() : 0;

		if (size > 0) {
			setSuccess(request, "Found " + size + " videos matching for \""+subject+"\"" );
		} else {

			setSuccess(request, "No videos matching your search for "+ subject +". Try some more specific keywords.");
		} 
		request.setAttribute("VideoContentsList", videoContentList);
		
		modelAndView.addObject("academicCycle", "All");
		/*modelAndView.addObject("selectedSubject", "All"); 
		FacultyAcadsBean tempFaculty=new FacultyAcadsBean();
		tempFaculty.setFacultyId("All");
		modelAndView.addObject("selectedFaculty",tempFaculty);
		
		modelAndView.addObject("searchItem", "");*/

		modelAndView.addObject("showPagination", false);

		//for batch track
		//modelAndView.addObject("selectedBatch","All");
		modelAndView.addObject("selectedSubject", subject);
		return modelAndView;
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/searchVideos")
	public ModelAndView searchVideos(@ModelAttribute VideoContentAcadsBean vdoCntBean, HttpServletRequest request,
			HttpServletResponse response) {

		//Check session is expired or not
		if(!checkSession(request, response)) 
			return new ModelAndView("studentPortalRediret");

		ModelAndView modelAndView = new ModelAndView("videoHomeDemo");
		List<ConsumerProgramStructureAcads> PSSIdWithSubject = new ArrayList<ConsumerProgramStructureAcads>();
		List<String> PSSIdList = new ArrayList<String>();
		List<String> commonSubjectList = new ArrayList<String>();
		List<VideoContentAcadsBean> sessionVideosList = new ArrayList<VideoContentAcadsBean>();

		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		String searchKeyword = request.getParameter("searchItem");
		//Read programSemSubjectId's With Subject and common subject's from session
		PSSIdWithSubject = (List<ConsumerProgramStructureAcads>) request.getSession().getAttribute("programSemSubjectIdWithSubject");
		
		if((PSSIdWithSubject == null)) {
		try {
			//Step 01 - Get program semester subjects id's with the subject name.
			PSSIdWithSubject = this.getProgramSemSubjectIdWithSubject(request);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		}
		//Step 02 - Prepare PSSIds list.
		PSSIdList = this.getPrgmSemSubjectIdsList(PSSIdWithSubject);
		
		//Step 03 - Set Dynamic drop down values. 
		this.setDynamicDropDownValues(request, modelAndView, PSSIdList);
		
		//Get common subjects
		commonSubjectList = (List<String>) request.getSession().getAttribute("commonSubjects");
		
		/*if(userId.startsWith("7")) {
			modelAndView.setViewName("videoHome");
			//Read programSemSubjectId's With Subject and common subject's from session
			PSSIdWithSubject = (List<ConsumerProgramStructureAcads>) request.getSession().getAttribute("programSemSubjectIdWithSubject");
			commonSubjectList = (List<String>) request.getSession().getAttribute("commonSubjects");

			//Prepare program sem subject list from the PSSIdWithSubject 
			PSSIdWithSubject.forEach((conPrgStrBean)->{
				PSSIdList.add(conPrgStrBean.getProgramSemSubjectId());
			});*/

			try {
				//Fetch session videos for specific keyword given by end user.
				sessionVideosList = videoContentService.fetchSessionVideosBySearch(searchKeyword, PSSIdList, 
						commonSubjectList, student.getProgram());
			} catch (Exception e) {
				  
			}
		/*}//if
		else {
			modelAndView.setViewName("videoHomeAdmin");	
			try {
				//Fetch session videos for specific keyword given by end user.
				sessionVideosList = videoContentService.fetchSessionVideosBySearch(searchKeyword);
			} catch (Exception e) {
				  
			}
		}//else*/

		//Assign zero in the size if videoContentList is null else size of list.
		int size = sessionVideosList != null ? sessionVideosList.size() : 0;

		if (size > 0) {
			setSuccess(request, "Found " + size + " videos matching for \""+searchKeyword+"\"" );
		} else {
			setError(request, "No videos matching your search for <b>"+ searchKeyword +"</b>. Try some more specific keywords.");
		} 
		request.setAttribute("VideoContentsList", sessionVideosList);

		modelAndView.addObject("academicCycle", "All");
		//modelAndView.addObject("selectedSubject", "All"); 

		//FacultyAcadsBean tempFaculty=new FacultyAcadsBean();
		//tempFaculty.setFacultyId("All");
		//modelAndView.addObject("selectedFaculty",tempFaculty);

		modelAndView.addObject("searchItem", searchKeyword);
		modelAndView.addObject("showPagination", false);

		//for batch track
		//modelAndView.addObject("selectedBatch","All");

		return modelAndView;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/watchVideos", method = RequestMethod.GET)
	public ModelAndView watchVideo(@RequestParam("id") String idString, HttpServletRequest request,
			HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		ModelAndView modelAndView = new ModelAndView("videoPageDemo");
		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		StudentAcadsBean studentRegData = (StudentAcadsBean) request.getSession().getAttribute("studentRegData");

		HttpDownloadUtilityHelper hdHelper = new HttpDownloadUtilityHelper();
		List<TextTrackImplLine> trackDataList = new ArrayList<TextTrackImplLine>();
		List<VideoContentAcadsBean> videoSubTopicsList = new ArrayList<VideoContentAcadsBean>();
		String transcriptContent = null;

		/**
		 * This check written to prevent query feature for the lead students
		 */

		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		int id;
		VideoContentAcadsBean videoContent;

		try {
			id = Integer.parseInt(idString);

			if (checkLead(request, response)) {
				modelAndView.addObject("isLeadStudent", 1);
				videoContent = dao.getVideoContentForLeadById(id);
			} else {
				videoContent = dao.getVideoContentById(id);
			}

			// this.getVideoTranscriptUrl(videoContent);
			String acadYear = videoContent.getYear();
			String acadMonth = videoContent.getMonth();
			String currentYearMonthArr[] = contentService.getRecordedvsLiveCurrentYearMonthForSapid(studentRegData);

			String currentAcadYear = currentYearMonthArr[0];
			String currentAcadMonth = currentYearMonthArr[1];

			if (currentAcadYear.equalsIgnoreCase(acadYear) && currentAcadMonth.equalsIgnoreCase(acadMonth)) {
				request.setAttribute("AskFaculty", "block");
				request.setAttribute("RedirectLink", "none");
			} else {
				request.setAttribute("AskFaculty", "none");
				request.setAttribute("RedirectLink", "block");

			}

		} catch (NumberFormatException e) {
			  
			return new ModelAndView("studentPortalRediret");
		}

		/*
		 * Code for View count commented
		 * videoContent.setViewCount(videoContent.getViewCount() +1); boolean
		 * VideoContentUpdated=dao.updateVideoContent(videoContent);
		 * if(!VideoContentUpdated) { request.setAttribute("error", "true");
		 * request.setAttribute("errorMessage", "Unable to update view count...");
		 * 
		 * }
		 */
		Integer subjectCodeId = 0;
		try {
			subjectCodeId = videoContentService.fetchSubjectCodeId(videoContent.getSessionId());
		} catch (Exception e1) {
			
		}

		try {
			List<VideoContentAcadsBean> relatedVideos = new ArrayList<VideoContentAcadsBean>();

			if (subjectCodeId != 0 && subjectCodeId != null) {
				/*if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
					relatedVideos = dao.getRelatedVideoContentList("",videoContent.getSubject(),templist,student);
				}*/
				// Fetch related session videos based on subject code id
				relatedVideos = videoContentService.fetchSessionVideosForSubject(subjectCodeId, acadCycleList);
				
				videoContent.setSubjectCodeId(subjectCodeId);
			} else {
				List<String> commonSubjectList = (List<String>) request.getSession().getAttribute("commonSubjects");

				if (commonSubjectList == null || commonSubjectList.size() < 1)
					commonSubjectList = this.getCommonSubjects(request);

				// Fetch related session videos for a common subject
				relatedVideos = videoContentService.fetchSessionVideosBySearch(videoContent.getSubject(), null,
						commonSubjectList, student.getProgram());
			}

			request.setAttribute("videoContent", videoContent);
			request.setAttribute("relatedVideos", relatedVideos);
		} catch (Exception e) {
			  
		}

		String transcriptDwnldUrl = videoContent.getVideoTranscriptUrl(); // for Subtopics
		String videoURL = videoContent.getVideoLink();
		if (transcriptDwnldUrl != null) {
			try {
				transcriptContent = hdHelper.getContentFromTranscriptUrl(transcriptDwnldUrl);// get content from
																								// transcript download
																								// url
				if (transcriptContent != null) {
					Reader transcriptContentReader = new StringReader(transcriptContent);
					BufferedReader transcriptContentReaderBr = new BufferedReader(transcriptContentReader);
					trackDataList = WebVttParser.parse(transcriptContentReaderBr); // extract transcriptContent in list

					if (trackDataList != null) {
						for (TextTrackImplLine track : trackDataList) {
							VideoContentAcadsBean vcb = new VideoContentAcadsBean();
							String videoLink = getVideoURL(videoURL, track.getStartTime());
							vcb.setStartTime(convertTime(track.getStartTime()));
							vcb.setEndTime(convertTime(track.getEndTime()));
							vcb.setDuration(getDuration(track.getStartTime(), track.getEndTime()));
							vcb.setFileName(track.getText());
							vcb.setKeywords(track.getText());
							vcb.setStartTimeInSeconds(convertTimeInSeconds(track.getStartTime()));
							vcb.setEndTimeInSeconds(convertTimeInSeconds(track.getEndTime()));
							vcb.setVideoLink(videoLink);

							videoSubTopicsList.add(vcb);
						}
					}
				}
			} catch (Exception e) {
				  
			}
		}

		// List<VideoContentBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(new
		// Long(id));
		request.setAttribute("videoSubTopicsList", videoSubTopicsList);

		try {
			// Get all queries by student of subject
			String session_id = videoContent.getSessionId().toString();
			String programSemSubjectId=request.getParameter("pssId");
			request.setAttribute("programSemSubjectId", programSemSubjectId);
			getCourseQueriesMap(request, videoContent.getSubject(), session_id);
		} catch (Exception e) {
			  
		}

		request.setAttribute("subject", videoContent.getSubject());
		request.setAttribute("sessionId", videoContent.getSessionId());

		return modelAndView;
	}
	
	public List<SessionQueryAnswer> getMyQA(String session_id, String sapId) throws Exception {

		ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");

		List<SessionQueryAnswer> myQuestions = cDao.getStudentQAforSession(session_id, sapId);

		return myQuestions;
	}

	public List<SessionQueryAnswer> getPublicQA(String session_id, String sapId) throws Exception {

		ContentDAO cDao = (ContentDAO) act.getBean("contentDAO");

		List<SessionQueryAnswer> publicQAs = cDao.getPublicQAsforSession(session_id, sapId);

		// Send Back to Session QA Page
		return publicQAs;
	}
	
	private void getCourseQueriesMap(HttpServletRequest request, String subject, String session_id) {
		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		String userId = (String) request.getSession().getAttribute("userId_acads");
		List<SessionQueryAnswer> myQueries = dao.getQueriesForSessionByStudent(subject, student.getSapid());
		List<SessionQueryAnswer> myCourseQueries = dao.getQueriesForCourseByStudent(subject, student.getSapid());
		myQueries.addAll(myCourseQueries);

		List<SessionQueryAnswer> mySessionQuestions;

		try {
			mySessionQuestions = getMyQA(session_id, student.getSapid());
			myQueries.addAll(mySessionQuestions);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}

		List<SessionQueryAnswer> answeredPublicQueriesForCourse = new ArrayList<SessionQueryAnswer>();
		List<SessionQueryAnswer> publicQuestions = new ArrayList<SessionQueryAnswer>();

		if (userId.startsWith("7")) {
			try {

				answeredPublicQueriesForCourse = sessionQueryAnswerDAO.getPublicQueriesForCourse(student.getSapid(),
						subject, student.getConsumerProgramStructureId());
				publicQuestions = getPublicQA(session_id, student.getSapid());
				answeredPublicQueriesForCourse.addAll(publicQuestions);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
			}

		}

		request.getSession().setAttribute("myQueries", myQueries);
		request.getSession().setAttribute("answeredPublicQueriesForCourse", answeredPublicQueriesForCourse);

	}
	
	public Long convertTimeInSeconds(String time) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		DateFormat sdf = new SimpleDateFormat("ss");
		DateFormat mdf = new SimpleDateFormat("mm");
		DateFormat HHdf = new SimpleDateFormat("HH");

		Long seconds = null;
		Long minutes = null;
		Long hours = null;
		Long totalSeconds = null;
		Date d1 = null;

		d1 = format.parse(time);
		seconds = Long.parseLong(sdf.format(d1));
		minutes = Long.parseLong(mdf.format(d1));
		hours = Long.parseLong(HHdf.format(d1));
		totalSeconds = seconds + (60 * minutes) + (3600 * hours);
		return totalSeconds;
	}		

	public String convertTime(String input) {

		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");

		DateFormat outputformat = new SimpleDateFormat("HH:mm:ss");
		Date date = null;
		String output = null;
		try {
			date = df.parse(input);
			output = outputformat.format(date);

		} catch (Exception pe) {
			  
		}
		return output;
	}

	public String getDuration(String from, String to) {

		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		DateFormat df = new SimpleDateFormat("SSS");
		Date date = null;
		String output = null;
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format.parse(from);
			d2 = format.parse(to);
			// in milliseconds
			long diff = d2.getTime() - d1.getTime();
			date = df.parse(Long.toString(diff));
			output = format.format(date);

		} catch (Exception e) {
			  
		}
		return output;
	}
	
	private String getVideoURL(String vimeoLink, String from) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		DateFormat hhformat = new SimpleDateFormat("HH");
		DateFormat mmformat = new SimpleDateFormat("mm");
		DateFormat ssformat = new SimpleDateFormat("ss");

		Date date = null;
		String hhoutput = null;
		String mmoutput = null;
		String ssoutput = null;

		try {
			date = df.parse(from);
			hhoutput = hhformat.format(date);
			mmoutput = mmformat.format(date);
			ssoutput = ssformat.format(date);

		}catch (Exception pe) {
			
		}

		String videoLink = vimeoLink + "#t=" + hhoutput + "h" + mmoutput + "m" + ssoutput + "s";

		return videoLink;
	}
	
	@RequestMapping(value = "/watchVideoTopic", method = RequestMethod.GET)
	public ModelAndView watch(@RequestParam("id") String idString, HttpServletRequest request,
			HttpServletResponse response) {
		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}
		ModelAndView modelAndView = new ModelAndView();
		String userId = (String) request.getSession().getAttribute("userId_acads");
		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");

		if (userId.startsWith("7")) {
			modelAndView.setViewName("videoPage");
		} else {
			modelAndView.setViewName("videoPageAdmin");
		}
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		int id = Integer.parseInt(idString);
		VideoContentAcadsBean videoContentTopic = dao.getVideoSubTopicById(new Long(id));
		VideoContentAcadsBean videoContent = dao.getVideoContentById((int) (long) videoContentTopic.getParentVideoId());
		ArrayList<String> templist = new ArrayList<>();
		templist.add(videoContent.getSubject());
		List<VideoContentAcadsBean> relatedVideos = dao.getRelatedVideoContentList("", videoContent.getSubject(), templist,
				student);
		request.setAttribute("videoContent", videoContentTopic);
		request.setAttribute("relatedVideos", relatedVideos);

		List<VideoContentAcadsBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(videoContent.getId());
		request.setAttribute("videoSubTopicsList", videoSubTopicsList);
		try {
			// Get all queries by student of subject
			getCourseQueriesMap(request, videoContent.getSubject(), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}
		request.setAttribute("subject", videoContent.getSubject());

		return modelAndView;
	}
	
	//This mapping contains old logic so that this is not used anywhere in project
	@RequestMapping(value = "/videosHomeOld", method = RequestMethod.GET)
	@Deprecated
	public ModelAndView getVideoHomePage(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("academicCycle") String academicCycle) {

		if(!checkSession(request, response)){
			return new ModelAndView("studentPortalRediret");
		}

		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");
		ArrayList<String> allsubjects = applicableSubjectsForStudent(request);

		ModelAndView modelAndView = new ModelAndView("videoHome");
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		LeadDAO leadDAO = (LeadDAO) act.getBean("leadDAO");
		ArrayList<VideoContentAcadsBean> sessionForLeads = leadDAO.getSessionForLeads();
		int pageNo;

		try {
			pageNo = Integer.parseInt(request.getParameter("pageNo"));
			if (pageNo < 1) {
				pageNo = 1;
			}
		} catch (NumberFormatException e) {
			pageNo = 1;
			  
		}

		ArrayList<String> academicCycleList =new ArrayList<String>();
		academicCycleList=dao.getAcademicCycleList();
		ArrayList<String> academicCycleListForDb =new ArrayList<String>();

		if(academicCycle==null || "".equals(academicCycle) || "All".equals(academicCycle)){
			academicCycle="All";
			academicCycleListForDb.addAll(academicCycleList);
		} else{
			academicCycleListForDb.add(academicCycle);
		}


		PageAcads<VideoContentAcadsBean> page = new PageAcads<VideoContentAcadsBean>();

		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
			page = dao.getVideoContentPage(pageNo, pageSize,allsubjects,academicCycleListForDb,student);
		}

		List<VideoContentAcadsBean> videoContentListPage = page.getPageItems();
		modelAndView.addObject("academicCycleList", academicCycleList);
		request.getSession().setAttribute("academicCycleList", academicCycleList);

		modelAndView.addObject("academicCycle", academicCycle);
		modelAndView.addObject("page", page);
		modelAndView.addObject("rowCount", page.getRowCount());
		modelAndView.addObject("allsubjects", allsubjects);
		request.getSession().setAttribute("allsubjects", allsubjects);
		modelAndView.addObject("selectedSubject", "All");

		//for batch track filter
		List<String> tracks=dao.getBatchTracks();
		modelAndView.addObject("allBatchTracks", tracks);
		request.getSession().setAttribute("allBatchTracks", tracks);
		modelAndView.addObject("selectedBatch", "All");

		List<FacultyAcadsBean> facultyList= dao.getFacultiesForSubjects(allsubjects);
		modelAndView.addObject("facultyList",facultyList);
		request.getSession().setAttribute("facultyList", facultyList);
		FacultyAcadsBean tempFaculty=new FacultyAcadsBean();
		tempFaculty.setFacultyId("All");
		modelAndView.addObject("selectedFaculty",tempFaculty);

		modelAndView.addObject("searchItem","");

		modelAndView.addObject("showPagination", true);

		modelAndView.addObject("VideoContentsList", videoContentListPage);
		modelAndView.addObject("subjectVideosMap", null);
		request.setAttribute("VideoContentsList", videoContentListPage);
		request.setAttribute("subjectVideosMap", null);
		request.setAttribute("subjectList", getSubjectList());
		request.setAttribute("sessionForLeads", sessionForLeads);
		return modelAndView;
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/searchByFilter", method = RequestMethod.GET)
	public ModelAndView searchByFilterNew(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("searchInput") String searchItem, @RequestParam("faculty") String faculty,
			@RequestParam("subject") String programSemSubjectIds, @RequestParam("cycle") String cycle,
			@RequestParam("batch") String batch) {

		if (!checkSession(request, response)) {
			return new ModelAndView("studentPortalRediret");
		}

		FacultyDAO fDao = (FacultyDAO) act.getBean("facultyDAO");
		StudentAcadsBean student = (StudentAcadsBean) request.getSession().getAttribute("student_acads");
		ArrayList<String> applicableSubjects = new ArrayList<String>();
		ArrayList<ConsumerProgramStructureAcads> programSemSubjectIdWithSubject = new ArrayList<ConsumerProgramStructureAcads>();
		String subject = "";
	
		ModelAndView modelAndView = new ModelAndView();
		String userId = (String) request.getSession().getAttribute("userId_acads");

		if (userId.startsWith("7")) {
			modelAndView.setViewName("videoHomeDemo");
			applicableSubjects = (ArrayList<String>) request.getSession().getAttribute("applicableSubjects");
			programSemSubjectIdWithSubject = (ArrayList<ConsumerProgramStructureAcads>) request.getSession()
					.getAttribute("programSemSubjectIdWithSubject");
		} else {
			modelAndView.setViewName("videoHomeAdmin");
			applicableSubjects = subjectList;
			applicableSubjects.add("Orientation");
			applicableSubjects.add("Executive Program Orientation");
		}

		List<SearchBean> academicCycleList = (List<SearchBean>) request.getSession().getAttribute("academicCycleList");
		List<String> commonSubjectList = (List<String>) request.getSession().getAttribute("all_commonSubjectList");
		
		//List<ConsumerProgramStructureAcads> all_subjectList = (List<ConsumerProgramStructureAcads>) request.getSession().getAttribute("all_subjectList");
	//	List<String> all_batch = (List<String>) request.getSession().getAttribute("all_BatchTracks");
		//List<FacultyAcadsBean> all_faculty = (List<FacultyAcadsBean>) request.getSession().getAttribute("all_facultyList");
		List<String> all_cycle = (List<String>) request.getSession().getAttribute("all_cycleList");
		List<String> all_pssIds = (List<String>) request.getSession().getAttribute("pssIds");
		programSemSubjectIds = !StringUtils.isBlank(programSemSubjectIds) ? programSemSubjectIds : "All";
		
		List<String> tempSubject = new ArrayList<String>();
		List<String> commonSubjects = new ArrayList<String>();
		if ("All".equalsIgnoreCase(programSemSubjectIds)) {
			//commonSubjects = commonSubjectList;
			for (ConsumerProgramStructureAcads bean : programSemSubjectIdWithSubject) {
				tempSubject.add(bean.getProgramSemSubjectId());
			}
			tempSubject.addAll(commonSubjectList);
		} else {
			tempSubject.add(programSemSubjectIds);
		}

		if (cycle == null || "".equals(cycle))
			cycle = "All";

		if(academicCycleList==null && "All".equalsIgnoreCase(cycle)) {
			academicCycleList = videoContentService.getApplicableCycles(this.getPrgmSemSubjectIdsList(programSemSubjectIdWithSubject));
			request.getSession().setAttribute("academicCycleList", academicCycleList);
		}
		
		List<VideoContentAcadsBean> VideoContentsList = new ArrayList<VideoContentAcadsBean>();

		if (commonSubjectList.contains(programSemSubjectIds) && !"All".equalsIgnoreCase(programSemSubjectIds)) {
			VideoContentsList = videoContentService.fetchVideoForSearchByFilterForCommonNew(searchItem, faculty,
					tempSubject, cycle, batch, student);
		} else {
			VideoContentsList = videoContentService.fetchSessionVideosForFilter(searchItem, faculty, tempSubject, cycle,
					batch, commonSubjects, student.getProgram(), academicCycleList);
		}

		int size = VideoContentsList != null ? VideoContentsList.size() : 0;

		
	
		try {
				//Fetch applicable values of Academic cycle,Subject,Tracks and Faculties for drop-down.
			//	Map<String,List> filterValueMap = videoContentService.setDynamicFilterValue(VideoContentsList); 
			
				//modelAndView.addObject("academicCycleList", filterValueMap.get("ACADCYCLES"));
				//modelAndView.addObject("facultyList",filterValueMap.get("FACULTIES"));
				//modelAndView.addObject("programSemSubjectIdWithSubject", filterValueMap.get("PSSIdWITHSUBJECT"));
				//modelAndView.addObject("allBatchTracks",filterValueMap.get("TRACKS"));
			
				
					modelAndView.addObject("commonSubjects",videoContentService.getSessionVideosByCommon(commonSubjectList, faculty, cycle, batch, student.getProgram()));
					modelAndView.addObject("programSemSubjectIdWithSubject",videoContentService.getSessionVideosBySubject(searchItem, faculty, all_pssIds, 
							cycle,batch,academicCycleList));
					modelAndView.addObject("facultyList",videoContentService.getSessionVideosByFaculty(searchItem, faculty, tempSubject, 
							cycle,batch,academicCycleList,student.getConsumerProgramStructureId()));
				
					modelAndView.addObject("allBatchTracks",videoContentService.getSessionVideosByTrack(searchItem, faculty, tempSubject, 
							cycle,batch,academicCycleList));
				
			
					modelAndView.addObject("academicCycleList",videoContentService.getSessionVideosByAcadCycle(faculty, tempSubject,batch,commonSubjectList,student.getConsumerProgramStructureId(),cycle));
				
			//	modelAndView.addObject("academicCycleList",!cycle.equalsIgnoreCase("All") ? all_cycle : filterValueMap.get("ACADCYCLES"));
			//	modelAndView.addObject("facultyList",!faculty.equalsIgnoreCase("All") ? all_faculty : filterValueMap.get("FACULTIES")  );
			//	modelAndView.addObject("programSemSubjectIdWithSubject",!programSemSubjectIds.equalsIgnoreCase("All") ? all_subjectList : filterValueMap.get("PSSIdWITHSUBJECT")  );
			//	modelAndView.addObject("allBatchTracks",!batch.equalsIgnoreCase("All") ?  all_batch   : filterValueMap.get("TRACKS")  );
			//	modelAndView.addObject("commonSubjects",!programSemSubjectIds.equalsIgnoreCase("All") ?  commonSubjectList   : filterValueMap.get("COMMONSUBJECTS")  );
		
				
		}catch(Exception e) {
			e.printStackTrace();
		}
		FacultyAcadsBean FacultyAcadsBean =new FacultyAcadsBean();
		FacultyAcadsBean tempFaculty = new FacultyAcadsBean();

		if (!"All".equalsIgnoreCase(faculty)) {
			FacultyAcadsBean  = fDao.findfacultyByFacultyId(faculty);
			tempFaculty.setFacultyId(FacultyAcadsBean.getFacultyId());tempFaculty.setFirstName(FacultyAcadsBean.getFirstName());
			tempFaculty.setLastName(FacultyAcadsBean.getLastName());
			faculty = "Prof. " + tempFaculty.getFirstName() + " " + tempFaculty.getLastName();
		} else {
			tempFaculty.setFacultyId("All");
		}

		if ("All".equalsIgnoreCase(programSemSubjectIds) || commonSubjectList.contains(programSemSubjectIds)
				|| "".equals(programSemSubjectIds))
			subject = programSemSubjectIds;
		else
			// Get subject name by PSS Id
			subject = videoContentService.fetchSubjectByPSSId(programSemSubjectIds);

		if (size > 0) {
			if ("".equals(searchItem)) {
				setSuccess(request, "Found " + size + " videos matching for Subject : " + subject + " and Faculty : "
						+ faculty + " and Academic Cycle : " + cycle + " and Batch Track : " + batch);
			} else {
				setSuccess(request,
						"Found " + size + " videos matching for \"" + searchItem + "\" and Subject : " + subject
								+ " and Faculty : " + faculty + " and Academic Cycle : " + cycle + " and Batch Track : "
								+ batch);
			}
		} else {
			setSuccess(request,
					"No videos matching your search for " + searchItem + "  and Subject : " + subject
							+ " and Faculty : " + faculty + " and Academic Cycle : " + cycle + " and Batch Track : "
							+ batch + ". Try some more specific keywords.");
		}
		
		
		request.setAttribute("VideoContentsList", VideoContentsList);
	
		modelAndView.addObject("academicCycle", cycle);
		modelAndView.addObject("selectedSubject", subject);
		modelAndView.addObject("programSemSubjectIds", programSemSubjectIds);
	
		modelAndView.addObject("selectedFaculty", tempFaculty);
		modelAndView.addObject("searchItem", searchItem);
		
		request.getSession().setAttribute("commonSubjects", commonSubjectList);
		
		// for batch track filter
		modelAndView.addObject("selectedBatch", batch);
		modelAndView.addObject("defaultAcademicCycle",request.getSession().getAttribute("defaultAcademicCycle"));

		return modelAndView;
	}
	
	private List<ConsumerProgramStructureAcads> getProgramSemSubjectIdWithSubject(HttpServletRequest request) {
		ArrayList<String> allsubjects = applicableSubjectsForStudent(request);
		StudentAcadsBean student = (StudentAcadsBean)request.getSession().getAttribute("student_acads");

		List<ConsumerProgramStructureAcads> programSemSubjectIdWithSubject = videoContentService.getProgramSemSubjectId(allsubjects, student.getConsumerProgramStructureId());

		return programSemSubjectIdWithSubject;

	}
	
	private List<String> getPrgmSemSubjectIdsList(List<ConsumerProgramStructureAcads> programSemSubjectIdWithSubject) {
		List<String> programSemSubjectIds = new ArrayList<String>();

		for (ConsumerProgramStructureAcads bean : programSemSubjectIdWithSubject) {
			programSemSubjectIds.add(bean.getProgramSemSubjectId());
		}

		return programSemSubjectIds;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setDynamicDropDownValues(HttpServletRequest request, ModelAndView modelAndView, List<String> programSemSubjectIds) {
		String PSSIds = "";
		PSSIds = ContentUtil.frameINClauseString(programSemSubjectIds);

		//Fetch applicable drop down values like subjects, tracks, faculties and acad cycles
		Map<String,List> filterValueMap = videoContentService.fetchSessionVideoDropdownValues(PSSIds, 
				"All", "All", "All");

		List<String> commonSubjects = (List<String>) request.getSession().getAttribute("commonSubjects");
		
		if(commonSubjects == null)
			commonSubjects =  this.getCommonSubjects(request);

		modelAndView.addObject("commonSubjects", commonSubjects);
		request.getSession().setAttribute("commonSubjects", commonSubjects);

		modelAndView.addObject("academicCycleList", filterValueMap.get("ACADCYCLES"));
		request.getSession().setAttribute("academicCycleList", filterValueMap.get("ACADCYCLES"));

		modelAndView.addObject("programSemSubjectIdWithSubject", filterValueMap.get("PSSIdWITHSUBJECT"));
		request.getSession().setAttribute("programSemSubjectIdWithSubject", filterValueMap.get("PSSIdWITHSUBJECT"));

		modelAndView.addObject("allBatchTracks", filterValueMap.get("TRACKS"));
		request.getSession().setAttribute("allBatchTracks", filterValueMap.get("TRACKS"));

		List<FacultyAcadsBean> facultyList = filterValueMap.get("FACULTIES");
		modelAndView.addObject("facultyList",facultyList);

		FacultyAcadsBean tempFaculty=new FacultyAcadsBean();
		tempFaculty.setFacultyId("All");
		modelAndView.addObject("selectedFaculty",tempFaculty);

		modelAndView.addObject("selectedSubject", "All");
		modelAndView.addObject("selectedBatch", "All");
		modelAndView.addObject("searchItem","");
	}
	
}
