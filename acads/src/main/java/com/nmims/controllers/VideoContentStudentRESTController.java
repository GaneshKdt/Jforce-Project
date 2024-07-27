package com.nmims.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.ExamOrderAcadsBean;
import com.nmims.beans.FacultyAcadsBean;
import com.nmims.beans.PageAcads;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.dto.StudentSubjectCodeDto;
import com.nmims.services.ContentService;
import com.nmims.services.StudentCourseMappingService;
import com.nmims.services.StudentService;
import com.nmims.services.VideoContentService;
import com.nmims.util.ContentUtil;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class VideoContentStudentRESTController  extends BaseController  {

	private final int pageSize = 10;

	@Autowired
	private VideoContentService videoContentService;

	@Autowired
	private ContentService contentService;
	
	@Autowired
	private VideoContentDAO videoContentDAO;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;

	@Autowired
	StudentService studentService;
	
	@Autowired
	StudentCourseMappingService studentCourseMappingService;
	

	@RequestMapping(value = "/subjectsForVideosWithSubjectCode", method = RequestMethod.POST, produces="application/json", consumes="application/json")
	public ResponseEntity<List<ConsumerProgramStructureAcads>> getSubjectsForVideosWithSubjectCode(@RequestBody StudentSubjectCodeDto studentDto) {

		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		ArrayList<String> allsubjects=null;
		List<ConsumerProgramStructureAcads> subjectCodeIdWithSubject = new ArrayList<>();
		StudentAcadsBean student = new StudentAcadsBean();
		student.setSapid(studentDto.getSapid());
		List<String> subjectsWithVideos=null;

		try {
			student = cdao.getSingleStudentsData(student.getSapid());
			studentService.mgetWaivedOffSubjects(student);
			studentService.mgetWaivedInSubjects(student);
			//allsubjects = applicableSubjectsForStudentMobile(student);
			allsubjects = studentCourseMappingService.applicableSubjectsForStudentMobile(student);
			subjectsWithVideos=videoContentDAO.getSubjectsWithVideos(allsubjects);

			subjectCodeIdWithSubject = videoContentService.fetchSubjectCodeIdsByApplicableSub(subjectsWithVideos, 
					student.getConsumerProgramStructureId());

		} catch (Exception e) {
			  
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		return new ResponseEntity<List<ConsumerProgramStructureAcads>>(subjectCodeIdWithSubject,headers,HttpStatus.OK);
	}

	@RequestMapping(value = "/videosForSubjectNew", method = RequestMethod.POST)
	public ResponseEntity<List<VideoContentAcadsBean>> videosForSubjectForMobileNew(@RequestBody StudentSubjectCodeDto student) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");

		List<VideoContentAcadsBean> videoContentsList=new ArrayList<VideoContentAcadsBean>();
		try {
			if(student.getProgramSemSubjectId()==null) {
				if(student.getSubjectCodeId()==null) {
					StudentAcadsBean studentData = cDao.getSingleStudentsData(student.getSapid());
					int programSemSubjectId = cDao.getPssIdBySubject(student.getSubject(), studentData.getConsumerProgramStructureId());
					videoContentsList = videoContentDAO.getRelatedVideoContentNewForMobile(student.getSapid(), programSemSubjectId);				
				}else {
					videoContentsList = videoContentDAO.getRelatedVideoContentUsingSubjectCodeForMobile(student.getSubjectCodeId());
					//fetch and set bookmark status of all videoContentList
					videoContentsList = fetchAndInsertBookmarksInVideoContent(videoContentsList,student.getSapid());
				}	
			}else {
				videoContentsList = videoContentDAO.getVideosUsingPSSIdForMobile(student.getProgramSemSubjectId(),"All");
				videoContentsList = fetchAndInsertBookmarksInVideoContent(videoContentsList,student.getSapid());
			}

		} catch (Exception e) {
			  
		}

		return new ResponseEntity<List<VideoContentAcadsBean>>(videoContentsList,headers,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/videosHomeCount", method = RequestMethod.POST)
	public ResponseEntity<PageAcads> videosHomeCount(@RequestBody StudentSubjectCodeDto student) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");

		List<VideoContentAcadsBean> videoContentsList=new ArrayList<VideoContentAcadsBean>();
		PageAcads page = new PageAcads();
		try {
			String pssIdsList = ContentUtil.frameINClauseString(student.getProgramSemSubjectIdList());
			int videosCount = videoContentDAO.getVideosUsingPSSIdForMobileCount(pssIdsList,"All");
			page.setRowCount(videosCount);
		} catch (Exception e) {
			  
		}

		return new ResponseEntity<PageAcads>(page,headers,HttpStatus.OK);
	}

	@RequestMapping(value = "/videosHomeNew", method = RequestMethod.POST)
	public ResponseEntity<PageAcads<VideoContentAcadsBean>> getVideoHomeContentNew(@RequestBody StudentAcadsBean student ,
			@Context HttpServletRequest request) {
		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean studentBeanFromDB = cdao.getSingleStudentsData(student.getSapid());

		//ArrayList<String> allsubjects = applicableSubjectsForStudentMobile(studentBeanFromDB);
		ArrayList<String> allsubjects = studentCourseMappingService.applicableSubjectsForStudentMobile(studentBeanFromDB);
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");


		int pageNo;
		try {
			pageNo = Integer.parseInt(request.getParameter("pageNo"));
			if (pageNo < 1) {
				pageNo = 1;
			}
		} catch (NumberFormatException e) {
			pageNo = 1;
			  
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		//change academicCycle adding later
		ArrayList<String> academicCycleList =new ArrayList<String>();
		academicCycleList=dao.getAcademicCycleList();
		ArrayList<String> academicCycleListForDb =new ArrayList<String>();
		String academicCycle = "All";
		if(academicCycle==null || "".equals(academicCycle) || "All".equals(academicCycle)){
			academicCycle="All";
			academicCycleListForDb.addAll(academicCycleList);
		} else{
			academicCycleListForDb.add(academicCycle);
		}

		ArrayList<ConsumerProgramStructureAcads> programSemSubjectIdWithSubject = dao.getProgramSemSubjectId(allsubjects, studentBeanFromDB.getConsumerProgramStructureId());
		ArrayList<String> programSemSubjectIds = new ArrayList<String>();
		for (ConsumerProgramStructureAcads bean : programSemSubjectIdWithSubject) {
			programSemSubjectIds.add(bean.getProgramSemSubjectId());
		}

		List<String> commonSubjects =  contentService.getCommonSubjectsMobile(student.getSapid());
		PageAcads<VideoContentAcadsBean> page = dao.getVideoContentPageNew(pageNo, pageSize, programSemSubjectIds, academicCycleListForDb, student, commonSubjects);
		page.setPageItems(null);
		return new ResponseEntity<PageAcads<VideoContentAcadsBean>>(page,headers,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/videosForCycle", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<VideoContentAcadsBean>> videosForCycle(@RequestBody StudentAcadsBean student) {
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		
		ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		ArrayList<VideoContentAcadsBean> videosList = new ArrayList<VideoContentAcadsBean>();
//		ArrayList<VideoContentBean> finalLastCycleVideos = new ArrayList<VideoContentBean>();
		try {
			StudentAcadsBean studentRegData = dao.getStudentsMostRecentRegistrationData(student.getSapid());
			int programSemSubjectId = 0;
			if(student.getProgramSemSubjectId() == null) {
				programSemSubjectId = cDao.getPssIdBySubject(student.getSubject(), studentRegData.getConsumerProgramStructureId());	
			}else {
				programSemSubjectId = Integer.parseInt(student.getProgramSemSubjectId());	
			}
			
			
			List<ExamOrderAcadsBean> liveFlagList = cDao.getLiveFlagDetails();
			HashMap<String,BigDecimal> examOrderMap = this.generateExamOrderMap(liveFlagList);
			
			
			double acadSessionLiveOrder = this.getMaxOrderOfAcadSessionLive(liveFlagList);
			double reg_order =  examOrderMap.get(studentRegData.getMonth()+studentRegData.getYear()).doubleValue();
			double current_order =  examOrderMap.get(CURRENT_ACAD_MONTH+CURRENT_ACAD_YEAR).doubleValue();
			String acadCycleDateFormat = "";
			
			if (student.getAcadCycle().equalsIgnoreCase("current")) {
				//Get video content list for current cycle of a student
//				videosList = dao.getVideoContentForCurrentCycle(studentRegData, programSemSubjectId,
//						acadSessionLiveOrder,reg_order,current_order);
				if(acadSessionLiveOrder == reg_order) {
					acadCycleDateFormat = ContentUtil.prepareCurrentAcadDate(studentRegData.getYear(), studentRegData.getMonth());
				}else {
					acadCycleDateFormat = ContentUtil.prepareCurrentAcadDate(CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH);
				}
			}
			else if(student.getAcadCycle().equalsIgnoreCase("last")) {
				//Get video content list for last cycle of a student
				
				if(acadSessionLiveOrder == reg_order) {
					acadCycleDateFormat = ContentUtil.prepareLastAcadDate(studentRegData.getYear(), studentRegData.getMonth());
				}else {
					acadCycleDateFormat = ContentUtil.prepareLastAcadDate(CURRENT_ACAD_YEAR, CURRENT_ACAD_MONTH);
				}
//					videosList = dao.getVideoContentForLastCycle(studentRegData, programSemSubjectId,
//							acadSessionLiveOrder,reg_order,current_order);	
			} 
			videosList = (ArrayList<VideoContentAcadsBean>) videoContentDAO.getVideosUsingPSSIdForMobile(student.getProgramSemSubjectId(),acadCycleDateFormat);
			videosList = (ArrayList<VideoContentAcadsBean>) fetchAndInsertBookmarksInVideoContent(videosList,student.getSapid());


		} catch (Exception e) {
			  
		}
		
		return new ResponseEntity<ArrayList<VideoContentAcadsBean>>(videosList,headers,HttpStatus.OK);
	}
	
	private double getMaxOrderOfAcadSessionLive(List<ExamOrderAcadsBean> liveFlagList){
		double sessionLiveOrder = 0.0;
		for (ExamOrderAcadsBean bean : liveFlagList) {
			double currentOrder = Double.parseDouble(bean.getOrder());
			if("Y".equalsIgnoreCase(bean.getAcadSessionLive()) && currentOrder > sessionLiveOrder){
				sessionLiveOrder = currentOrder;
			}
		}
		return sessionLiveOrder;
	}
	
	private HashMap<String, BigDecimal> generateExamOrderMap(List<ExamOrderAcadsBean> liveFlagList) {
		HashMap<String, BigDecimal> orderMap = new HashMap<String, BigDecimal>();
		for (ExamOrderAcadsBean row : liveFlagList) {
			orderMap.put(row.getMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
			orderMap.put(row.getAcadMonth()+row.getYear(),BigDecimal.valueOf((long)Double.parseDouble( row.getOrder())));
		}
		return orderMap;
	}

	public List<VideoContentAcadsBean> fetchAndInsertBookmarksInVideoContent(List<VideoContentAcadsBean> videoContentsList, String sapid){
		String commaSaperatedVideoContentIds = null;
		commaSaperatedVideoContentIds =	videoContentsList.stream()
				.map(a -> String.valueOf(a.getId()))
				.collect(Collectors.joining(","));
		
		//get bookmark status of all videos
		List<VideoContentAcadsBean> videoContentBookarkIds = videoContentDAO.getBookmarksOfVideoContent(commaSaperatedVideoContentIds,sapid);
		
		//set bookmarks status in original videoContentList
		videoContentsList.forEach(myObject1 -> videoContentBookarkIds.stream()
	            .filter(myObject2 -> myObject1.getId().equals(myObject2.getId()))
	            .findAny().ifPresent(myObject2 -> myObject1.setBookmarked(myObject2.getBookmarked())));
		
		return videoContentsList;
	}

	public ArrayList<String> applicableSubjectsForStudentMobile(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingAcadsBean> allsubjects = new ArrayList<>();

		ArrayList<ProgramSubjectMappingAcadsBean> unAttemptedSubjectsBeans = new ArrayList<>();


		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean studentRegistrationData;

		String earlyAccess = contentService.checkEarlyAccess(student.getSapid());
		// If isEarlyAccess then registration will not be available of this drive.
		if("Yes".equalsIgnoreCase(earlyAccess)) {
			studentRegistrationData= student;
		}else if(student.getProgram().equalsIgnoreCase("EPBM") || student.getProgram().equalsIgnoreCase("MPDV") ) {

			studentRegistrationData = cdao.getStudentMaxSemRegistrationData(student.getSapid());
		}else{
			studentRegistrationData = cdao.getStudentRegistrationData(student.getSapid());
		}

		if(studentRegistrationData == null){
			//Get fail subjects content if studnet does not have registration for current sem.
			failSubjectsBeans = contentService.getFailSubjects(student);

			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				allsubjects.addAll(failSubjectsBeans);
			}

		}else{
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			ArrayList<ProgramSubjectMappingAcadsBean> currentSemSubjects = contentService.getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
			}

			//If current sem is 1, then there will be no failed subjects. Get failed subjects only when he is in higher semesters
			if(!"1".equals(studentRegistrationData.getSem())){
				failSubjectsBeans = contentService.getFailSubjects(student);

				if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
					allsubjects.addAll(failSubjectsBeans);
				}
			}
		}

		//Get subjects never attempted or results not declared
		unAttemptedSubjectsBeans = cdao.getUnAttemptedSubjects(student.getSapid());
		if(unAttemptedSubjectsBeans != null && unAttemptedSubjectsBeans.size() > 0){
			allsubjects.addAll(unAttemptedSubjectsBeans);
		}


		//Sort all subjects semester wise.
		Collections.sort(allsubjects);

		ArrayList<String> applicableSubjects=new ArrayList<>();
		for(ProgramSubjectMappingAcadsBean psmb:allsubjects){
			if(!student.getWaivedOffSubjects().contains(psmb.getSubject())) {
				applicableSubjects.add(psmb.getSubject());
			}
		}


		if(student.getWaivedInSubjects() != null) {
			for (String subject : student.getWaivedInSubjects()) {
				if(!applicableSubjects.contains(subject)) {
					applicableSubjects.add(subject);
				}
			}
		}

		return applicableSubjects;	
	}




}
