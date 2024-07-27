package com.nmims.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nmims.beans.PageAcads;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SearchBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.LeadDAO;
import com.nmims.daos.SessionQueryAnswerDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.helpers.HttpDownloadUtilityHelper;
import com.nmims.helpers.TextTrackImplLine;
import com.nmims.helpers.VimeoManager;
import com.nmims.helpers.WebVttParser;
import com.nmims.listeners.SessionRecordingScheduler;
import com.nmims.services.ContentService;
import com.nmims.services.StudentService;
import com.nmims.services.VideoContentService;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("m")
public class VideoContentRestController extends BaseController {
	
	@Autowired(required = false)
	ApplicationContext act;
	private final int pageSize = 10;
	private ArrayList<String> subjectList = null;
	private ArrayList<ProgramSubjectMappingAcadsBean> programSubjectMappingList = null;
	Map<String, List<String>> checkForMasterkeyMap = null;

	@Value("#{'${ACAD_YEAR_LIST}'.split(',')}")
	private List<String> ACAD_YEAR_LIST;
	
	@Value("#{'${ACAD_MONTH_LIST}'.split(',')}") 
	private List<String> ACAD_MONTH_LIST;
	
	@Value("${CURRENT_ACAD_MONTH}")
	private String CURRENT_ACAD_MONTH;

	@Value("${CURRENT_ACAD_YEAR}")
	private String CURRENT_ACAD_YEAR;
	
	@Autowired
	private SessionRecordingScheduler sessionRecordingScheduler;
	
	@Autowired
	private StudentService studentService;
	
	@PostMapping(value = "/videosHome")
	public ResponseEntity<PageAcads<VideoContentAcadsBean>> getVideoHomeContent(@RequestBody StudentAcadsBean student ,
																	  @Context HttpServletRequest request) {
		
		ArrayList<String> allsubjects = applicableSubjectsForStudentForRestApi(student);
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");

		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean studentBeanFromDB = cdao.getSingleStudentsData(student.getSapid());
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
		
		PageAcads<VideoContentAcadsBean> page = new PageAcads<VideoContentAcadsBean>();
		
		if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
			page= dao.getVideoContentPage(pageNo, pageSize,allsubjects,academicCycleListForDb,studentBeanFromDB);
		}
		
		return new ResponseEntity<PageAcads<VideoContentAcadsBean>>(page,headers,HttpStatus.OK);
	}
	
	
	private ArrayList<String> applicableSubjectsForStudentForRestApi(StudentAcadsBean student) {
		ArrayList<ProgramSubjectMappingAcadsBean> failSubjectsBeans = new ArrayList<>();
		ArrayList<ProgramSubjectMappingAcadsBean> allsubjects = new ArrayList<>();
		
		ArrayList<ProgramSubjectMappingAcadsBean> unAttemptedSubjectsBeans = new ArrayList<>();
		
		ContentDAO cdao = (ContentDAO)act.getBean("contentDAO");
		StudentAcadsBean studentRegistrationData = cdao.getStudentRegistrationData(student.getSapid());
		StudentAcadsBean studentBeanFromDB = cdao.getSingleStudentsData(student.getSapid());
		studentService.mgetWaivedInSubjects(studentBeanFromDB);
		studentService.mgetWaivedOffSubjects(studentBeanFromDB);
		student.setWaivedOffSubjects(studentBeanFromDB.getWaivedOffSubjects());
		student.setWaivedInSubjectSemMapping(studentBeanFromDB.getWaivedInSubjectSemMapping());
		student.setWaivedInSubjects(studentBeanFromDB.getWaivedInSubjects());
		if(studentRegistrationData == null){
			//Get fail subjects content if studnet does not have registration for current sem.
			failSubjectsBeans = getFailSubjects(student);

			if(failSubjectsBeans != null && failSubjectsBeans.size() > 0){
				allsubjects.addAll(failSubjectsBeans);
			}
			
			
		}else{
			//Take program from Registration data and not Student data. 
			student.setProgram(studentRegistrationData.getProgram());
			student.setSem(studentRegistrationData.getSem());
			ArrayList<ProgramSubjectMappingAcadsBean> currentSemSubjects = getSubjectsForStudent(student);
			if(currentSemSubjects != null && currentSemSubjects.size() > 0){
				allsubjects.addAll(currentSemSubjects);
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
			for(String subject: student.getWaivedInSubjects()) {
				if(!applicableSubjects.contains(subject)) {
					applicableSubjects.add(subject);
				}
			}
		}
		
		return applicableSubjects;
				
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

	
		
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	@GetMapping(value = "/watchVideos",  produces="application/json")
	public ResponseEntity<HashMap<String, List<VideoContentAcadsBean>>> watchVideoForMobileApi(@RequestParam("id") String idString, HttpServletRequest request) {
			

		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> newVideoSubTopicsList = new ArrayList<VideoContentAcadsBean>();

		int id = 0;
		VideoContentAcadsBean videoContent = null;
		try {
			id = Integer.parseInt(idString);
			videoContent = dao.getVideoContentById(id);
			//this.getVideoTranscriptUrl(videoContent);
		} catch (NumberFormatException e) {
			  
			
		} 
		List<VideoContentAcadsBean> mainVideo= new ArrayList<VideoContentAcadsBean>();
		mainVideo.add(videoContent);

		
	
//		List<VideoContentBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(new Long(id));
		
		HashMap<String, List<VideoContentAcadsBean>> response = new HashMap<String, List<VideoContentAcadsBean>>();
		response.put("mainVideo",mainVideo);
		response.put("videoSubTopicsList",newVideoSubTopicsList);
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		
		return new ResponseEntity<HashMap<String, List<VideoContentAcadsBean>>>(response,headers,HttpStatus.OK);
	}
	 @GetMapping(value = "/watchVideoTopic")
		public ResponseEntity<HashMap<String, List<VideoContentAcadsBean>>> watchForMobileApi(@RequestParam("id") String idString, HttpServletRequest request) {
		 
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			int id = Integer.parseInt(idString);
			VideoContentAcadsBean videoContentTopic = dao.getVideoSubTopicById(new Long(id));
			VideoContentAcadsBean videoContent = dao.getVideoContentById((int)(long)videoContentTopic.getParentVideoId());
			ArrayList<String> templist=new ArrayList<>();
			templist.add(videoContent.getSubject());
			//List<VideoContentBean> relatedVideos = dao.getRelatedVideoContentList(videoContent.getSubject(),videoContent.getSubject(), templist);

			List<VideoContentAcadsBean> mainVideo= new ArrayList<VideoContentAcadsBean>();
			mainVideo.add(videoContentTopic);

			List<VideoContentAcadsBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(videoContent.getId());
			
			HashMap<String, List<VideoContentAcadsBean>> response = new HashMap<String, List<VideoContentAcadsBean>>();
			response.put("mainVideo",mainVideo);
			response.put("videoSubTopicsList",videoSubTopicsList);
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json"); 
			
			return new ResponseEntity<HashMap<String, List<VideoContentAcadsBean>>>(response,headers,HttpStatus.OK);
		}
	 
		@PostMapping(value = "/subjectsForVideos", produces="application/json", consumes="application/json")
		public ResponseEntity<List<String>> getSubjectsForVideos(@RequestBody StudentAcadsBean student) {
			
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			ArrayList<String> allsubjects=null;
			List<String> subjectsWithVideos=null;
			try {
				allsubjects = applicableSubjectsForStudentForRestApi(student);
				subjectsWithVideos=dao.getSubjectsWithVideos(allsubjects);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
			}
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json"); 
			
			return new ResponseEntity<List<String>>(subjectsWithVideos,headers,HttpStatus.OK);
		}
		
		//get lastcyle videos start
		@PostMapping(value = "/lastCycleVideos", produces="application/json", consumes="application/json")
		public ResponseEntity<ArrayList<VideoContentAcadsBean>> lastCycleVideos(@RequestBody StudentAcadsBean student,
															@RequestParam("month") String month,
															@RequestParam("year") Integer year 
															) {
			
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			ArrayList<VideoContentAcadsBean> lastCycleVideos = new ArrayList<VideoContentAcadsBean>();
//			ArrayList<VideoContentBean> finalLastCycleVideos = new ArrayList<VideoContentBean>();
			try {
//				lastCycleVideos=dao.getVideoContentForSubjectYearMonth(student.getSubject(),year,month);
				if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
					lastCycleVideos=dao.getVideoContentForSubjectYearMonthAndSapId(student.getSubject(),year,month,student.getSapid());
				}
//				for(VideoContentBean videoContentBean : lastCycleVideos){
//					if(dao.checkIfBookmarked(student.getSapid(),videoContentBean.getId().toString())){
//						videoContentBean.setBookmarked("Y");
//					}
//					finalLastCycleVideos.add(videoContentBean);
//				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
			}
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json"); 
			
			return new ResponseEntity<ArrayList<VideoContentAcadsBean>>(lastCycleVideos,headers,HttpStatus.OK);
		}
		//end
	 
		@GetMapping(value = "/videosForSubject")
		public ResponseEntity<List<VideoContentAcadsBean>> videosForSubjectForMobile( HttpServletRequest request) {
			 
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			String subject = request.getParameter("subject");
			String sapid = request.getParameter("sapid");
			ContentDAO cDao = (ContentDAO)act.getBean("contentDAO");
			
			ArrayList<String> templist=new ArrayList<>();
//			List<VideoContentBean> finalResponse = new ArrayList<VideoContentBean>();
			List<VideoContentAcadsBean> videoContentsList=new ArrayList<VideoContentAcadsBean>();
			try {

				StudentAcadsBean student = cDao.getSingleStudentsData(sapid);
//				if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){
					if(!StringUtils.isBlank(sapid)) {
						videoContentsList = dao.getRelatedVideoContentListBySapId("", subject,templist,student);
					} else {
						videoContentsList = dao.getRelatedVideoContentList("", subject,templist, student);
					}
//				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				  
			}
//			for(VideoContentBean contentBean : videoContentsList){
//				if(dao.checkIfBookmarked(sapid,String.valueOf(contentBean.getId()))){
//					contentBean.setBookmarked("Y");
//				}
//				finalResponse.add(contentBean);
//			}
			
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json"); 
			

			return new ResponseEntity<List<VideoContentAcadsBean>>(videoContentsList,headers,HttpStatus.OK);
		}

		@GetMapping(value = "/videosForSession", produces="application/json")
		public ResponseEntity<List<VideoContentAcadsBean>> m_courseDetailsQueries(
				@RequestParam("sessionId") String sessionId, 
				@RequestParam(required = false, name = "sapid") String sapid) throws Exception {
			HttpHeaders headers = new HttpHeaders();
		    headers.add("Content-Type", "application/json"); 
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
//			List<VideoContentBean> finalResponse = new ArrayList<VideoContentBean>();
			List<VideoContentAcadsBean> response= new ArrayList<VideoContentAcadsBean>();

			if(!StringUtils.isBlank(sapid)) {
				 response= dao.getVideosForSessionBySapId(sessionId, sapid);
			} else {
				 response= dao.getVideosForSession(sessionId);
			}
			
			
//				for(VideoContentBean contentBean : response){
//					if(dao.checkIfBookmarked(sapid,String.valueOf(contentBean.getId()))){
//						contentBean.setBookmarked("Y");
//					}
//					finalResponse.add(contentBean);
//				}
			return new ResponseEntity<List<VideoContentAcadsBean>>(response, headers,  HttpStatus.OK);
	}
	
	@GetMapping(value = "/getVideoRecordingDetailById", produces="application/json")
	public ResponseEntity<List<VideoContentAcadsBean>> getVideoRecordingDetailById(@RequestParam("sessionId") int VideoId) throws Exception {
		HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Type", "application/json"); 
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> response = new ArrayList<VideoContentAcadsBean>();
		response.add(dao.getVideoContentById(VideoId));
		return new ResponseEntity<List<VideoContentAcadsBean>>(response, headers,  HttpStatus.OK);
	}
	
	
	/*getPostIdForVideo Start*/
	@PostMapping(value = "/getPostIdByVideoId", produces="application/json")
	public ResponseEntity<VideoContentAcadsBean> getPostIdByVideoId(
			@RequestBody VideoContentAcadsBean bean, HttpServletRequest request) {
			
		VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
		VideoContentAcadsBean videoContent = new VideoContentAcadsBean();
		try {
			Long post_id = dao.getPostIdByVideoId(bean.getId());

			if(StringUtils.isNumeric(post_id+"")){
				videoContent.setPost_id(post_id);
			}
		} catch (NumberFormatException e) {
			  
			
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json"); 
		
		return new ResponseEntity<VideoContentAcadsBean>(videoContent,headers,HttpStatus.OK);
	}
	/*getPostIdForVideo end*/
		
					
			@PostMapping(value = "/getSessionsForLead", consumes = "application/json", produces = "application/json")
			public ResponseEntity<ArrayList<VideoContentAcadsBean>> getSessionsForLead(HttpServletRequest request, @RequestBody VideoContentAcadsBean bean) throws Exception {

				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json");
				LeadDAO leadDAO = (LeadDAO) act.getBean("leadDAO");
				ArrayList<VideoContentAcadsBean> response = new ArrayList<>();
				
				if(!StringUtils.isBlank(bean.getSubject())) {
					response = leadDAO.getSubejctViseSessionForLead(bean);
				}else {
					response = leadDAO.getAllSessionForLead();
				}

				return new ResponseEntity<>(response,headers, HttpStatus.OK);

			}
			
			@CrossOrigin(origins = "*", allowedHeaders = "*")
			@GetMapping(value = "/zoomBatchjon", produces="application/json ; charset=UTF-8")
			public ResponseEntity<HashMap<String, String>> zoomBatchjon() {
				HashMap<String, String> response = new HashMap<String, String>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json"); 
				try {
					sessionRecordingScheduler.synchronizeRecordingUpload();
					response.put("status", "success");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					  
					response.put("status", "error");
					response.put("message", e.getMessage());
				}
				return new ResponseEntity<HashMap<String, String>>(response,headers,HttpStatus.OK);
			}
			
			@CrossOrigin(origins = "*", allowedHeaders = "*")
			@GetMapping(value = "/vimeoBatchJob", produces="application/json ; charset=UTF-8")
			public ResponseEntity<HashMap<String, String>> vimeoBatchJob() {
				HashMap<String, String> response = new HashMap<String, String>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json"); 
				try {
					sessionRecordingScheduler.vimeoStatusCheck();
					response.put("status", "success");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					  
					response.put("status", "error");
					response.put("message", e.getMessage());
				}
				return new ResponseEntity<HashMap<String, String>>(response,headers,HttpStatus.OK);
			}
			
			@CrossOrigin(origins = "*", allowedHeaders = "*")
			@GetMapping(value = "/removeVideoFileFromZoom", produces="application/json ; charset=UTF-8")
			public ResponseEntity<HashMap<String, String>> removeVideoFileFromZoom() {
				HashMap<String, String> response = new HashMap<String, String>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json"); 
				sessionRecordingScheduler.removeVideoFileFromZoom();
				response.put("status", "success");
				return new ResponseEntity<HashMap<String, String>>(response,headers,HttpStatus.OK);
			}
			
			@CrossOrigin(origins = "*", allowedHeaders = "*")
			@GetMapping(value = "/updateZoomAudioLinkWithAWSs3", produces="application/json ; charset=UTF-8")
			public ResponseEntity<HashMap<String, String>> updateZoomAudioLinkWithAWSs3() {
				HashMap<String, String> response = new HashMap<String, String>();
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json"); 
				sessionRecordingScheduler.updateZoomAudioLinkWithAWSs3();
				response.put("status", "success");
				return new ResponseEntity<HashMap<String, String>>(response,headers,HttpStatus.OK);
			}
			
			@CrossOrigin(origins = "*", allowedHeaders = "*")
			@GetMapping(value = "/getVideoSubTopics", produces="application/json ; charset=UTF-8")
			public ResponseEntity<HashMap<String, List<VideoContentAcadsBean>>> getVideoSubTopics(@RequestParam("id") String idString,@RequestParam(value = "pageNo", required=false) Integer pageNo, HttpServletRequest request) {
				VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
				HttpDownloadUtilityHelper hdHelper = new HttpDownloadUtilityHelper();
				List <TextTrackImplLine> trackDataList = new ArrayList <TextTrackImplLine>();
				List<VideoContentAcadsBean> videoSubTopicsList = new ArrayList<VideoContentAcadsBean>();
				List<VideoContentAcadsBean> newVideoSubTopicsList = new ArrayList<VideoContentAcadsBean>();
				String transcriptContent = null;
				int id = 0;
				VideoContentAcadsBean videoContent = null;
				int sizePerPage=20;
				if(pageNo == null) {
					pageNo = 0;				
				}
				
				try {
					id = Integer.parseInt(idString);
					videoContent = dao.getVideoContentById(id);
				} catch (NumberFormatException e) {
					  
					
				}
				if(videoContent != null) {
				String transcriptDwnldUrl = videoContent.getVideoTranscriptUrl();		//for Subtopics
				String videoURL = videoContent.getVideoLink();
				if(transcriptDwnldUrl != null) {	
					try {					
						transcriptContent = hdHelper.getContentFromTranscriptUrl(transcriptDwnldUrl);// get content from transcript download url		
						if(transcriptContent != null) {
							Reader transcriptContentReader = new StringReader(transcriptContent);
							BufferedReader transcriptContentReaderBr = new BufferedReader(transcriptContentReader);			
							trackDataList =  WebVttParser.parse(transcriptContentReaderBr);	//extract transcriptContent in list		
							
							if(trackDataList != null) {
								 for (TextTrackImplLine track: trackDataList) {		
									 VideoContentAcadsBean vcb = new VideoContentAcadsBean();
									 String videoLink = getVideoURL(videoURL,track.getStartTime());		
									 vcb.setStartTime(convertTime(track.getStartTime()));
									 vcb.setEndTime(convertTime(track.getEndTime()));
									 vcb.setDuration(getDuration(track.getStartTime(),track.getEndTime()));
									 vcb.setFileName(track.getText());		
									 vcb.setKeywords(track.getText());
									 vcb.setStartTimeInSeconds(convertTimeInSeconds(track.getStartTime()));
									 vcb.setEndTimeInSeconds(convertTimeInSeconds(track.getEndTime()));
									 vcb.setVideoLink(videoLink);
									 videoSubTopicsList.add(vcb);
								 }					
							}
						}
					}catch(Exception e) {
						  
					}
				}
				
			
//				List<VideoContentBean> videoSubTopicsList = dao.getAllVideoSubTopicsList(new Long(id));
				
				int videoSubTopicsListSize = videoSubTopicsList.size();	
				int from = 0;
				int to = videoSubTopicsList.size();			
				if(pageNo != 0) {
					from = Math.max(0,(pageNo-1)*sizePerPage); 		
					if(from > videoSubTopicsListSize) {
						from = videoSubTopicsListSize;
					}
					to =  Math.min(videoSubTopicsList.size(),pageNo*sizePerPage);
					
				}			
				newVideoSubTopicsList = videoSubTopicsList.subList(from,to);	 
				}
				HashMap<String, List<VideoContentAcadsBean>> response = new HashMap<String, List<VideoContentAcadsBean>>();

				response.put("videoSubTopicsList",newVideoSubTopicsList);
				
				HttpHeaders headers = new HttpHeaders();
				headers.add("Content-Type", "application/json"); 
				
				return new ResponseEntity<HashMap<String, List<VideoContentAcadsBean>>>(response,headers,HttpStatus.OK);
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
			      try{
			    	 date= df.parse(input);	   
			    	 output = outputformat.format(date);	  
			    	 
			      }catch(Exception pe){
			           
			       }
				return output;
			}
			
			public String getDuration(String from,String to) {
				
				SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
			    DateFormat df = new SimpleDateFormat("SSS");	     
			    Date date = null;
			    String output = null;
				Date d1 = null;
				Date d2 = null;
				try {
					d1 = format.parse(from);
					d2 = format.parse(to);
					//in milliseconds
					long diff = d2.getTime() - d1.getTime();			
					 date= df.parse(Long.toString(diff));			   
			    	 output = format.format(date);
			    	 
				}catch(Exception e) {
					  
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
					
					  try{
						 date= df.parse(from);	   
						 hhoutput = hhformat.format(date);	  
						 mmoutput = mmformat.format(date);	  
					     ssoutput = ssformat.format(date);	  
						 
					  }catch(Exception pe){
					       
					   }
					
						String videoLink = vimeoLink + "#t="+hhoutput+"h"+mmoutput+"m"+ssoutput+"s";
					
						return videoLink;
						
				}
			
}
