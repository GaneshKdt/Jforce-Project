package com.nmims.services;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.nmims.beans.ConsumerProgramStructureAcads;
import com.nmims.beans.PageAcads;
import com.nmims.beans.ProgramSubjectMappingAcadsBean;
import com.nmims.beans.SearchBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.helpers.AWSHelper;
import com.nmims.helpers.VimeoManager;
import com.nmims.util.ContentUtil;

import com.nmims.beans.FacultyFilterBean;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class VideoContentService {
	
	@Autowired
	private VideoContentDAO videoContentDAO;
	
	@Autowired
	private AWSHelper awsHelper;
	
	@Value("${AWS_UPLOAD_URL}")
	private String AWS_UPLOAD_URL;
	
	@Value("${AWS_UPLOAD_URL_LOCAL}")
	private String AWS_UPLOAD_URL_LOCAL;
	
	@Autowired(required = false)
	ApplicationContext act;
	
	private Logger quickVideoContentForNewMasterKey_logger = LoggerFactory.getLogger("quickVideoContentForNewMasterKey");
	
	public static final String DEFAULT_FORMAT = "yyyy-MM-dd";
	public static final String TEMP_SEARCH_TYPE = "distinct";
	public static final String HISTORY_DATE = "2020-07-01";
	private HashMap<String, VideoContentAcadsBean> mapOfSessionIdAndVideoContentRecord = null;
	
	public void updateVimeoDurationService(String acadYear, String acadMonth) {
		VimeoManager vimeo = new VimeoManager(); 
		try {
			ArrayList<VideoContentAcadsBean> List = videoContentDAO.getVimeoId(acadYear, acadMonth);
			for(VideoContentAcadsBean videoContentBean : List ) {
				JsonObject vimeoResponse = vimeo.checkUploadVideoStatus(videoContentBean.getVimeoId() );
				videoContentDAO.updateVimeoDuration(vimeoResponse.get("duration").getAsString(),videoContentBean.getId());
			}
		}catch(Exception e){
			e.getMessage();
		} 
	}
	
	public List<ConsumerProgramStructureAcads> fetchSubjectCodeIdsByApplicableSub(List<String> subjects,
																			String consumerProgramStructureId) {
		List<ConsumerProgramStructureAcads> subjectCodeIdList = null;
		String applicableSubjects = null;
		
		//Preparing all applicable subject to into one string for IN clause
		applicableSubjects = ContentUtil.frameINClauseString(subjects);
		
		//Get applicable subjects code id
		subjectCodeIdList = videoContentDAO.getSubjectCodeIdsByApplicableSub(applicableSubjects, consumerProgramStructureId);
		
		//return subject code id's list
		return subjectCodeIdList;
	}
	
	public PageAcads<VideoContentAcadsBean> fetchSessionVideoBySubjectCodeId(int pageNo, int pageSize, 
																	List<String> subjectCodeIdList,
																	List<String> academicCycleList, 
																	String program, List<String> commonSubjectList ){
		PageAcads<VideoContentAcadsBean> page = null;
		String subjectCodeIds = null,academicCycles=null,commonSubjects=null;
		
		//Preparing subjectCodeId's into one string for IN clause
		subjectCodeIds = ContentUtil.frameINClauseString(subjectCodeIdList);
				
		//Preparing academicCycle's into one string for IN clause
		academicCycles = ContentUtil.frameINClauseString(academicCycleList);
				
		//Preparing commonSubject's into one string for IN clause
		commonSubjects = ContentUtil.frameINClauseString(commonSubjectList);
		
		//Get session video details in Pagination
		//page = videoContentDAO.getSessionVideoBySubjectCodeId(pageNo, pageSize, subjectCodeIds, 
		//														academicCycles, program, commonSubjects);
		
		//Get session video details in Pagination
		page = videoContentDAO.getSessionsVideoBySubjectCodeId(pageNo, pageSize, subjectCodeIds, 
															    academicCycles, program, commonSubjects);
		
		//return page having the Session Videos Details
		return page;
	}
	
	public PageAcads<VideoContentAcadsBean> fetchSessionVideos(int pageNo, int pageSize,
																List<String> programSemSubjectIdList, String acadCycleDateFormat, 
																List<String> commonSubjectList) {
		PageAcads<VideoContentAcadsBean> page = null;
		String programSemSubjectIds = null, commonSubjects = null;

		//Preparing programSemSubjectIds into one string for IN clause
		programSemSubjectIds = ContentUtil.frameINClauseString(programSemSubjectIdList);

		//Preparing commonSubject's into one string for IN clause
		commonSubjects = ContentUtil.frameINClauseString(commonSubjectList);
		
		//Get session video details in Pagination
		page = videoContentDAO.getSessionVideos(pageNo, pageSize, programSemSubjectIds, acadCycleDateFormat,commonSubjects);

		//return page having the Session Videos Details
		return page;
	}
	
	public List<VideoContentAcadsBean> fetchVideoForSearchByFilterForCommonNew(String searchItem, String faculty,
			List<String> programSemSubjectIds, String acadCycleDateFormat, String batch, StudentAcadsBean student) {
		List<VideoContentAcadsBean> sessionVideosList = null;
		
		//Get common session recordings a particular filter
		sessionVideosList =videoContentDAO.getVideoForSearchByFilterForCommonNew(searchItem, faculty, programSemSubjectIds, 
																acadCycleDateFormat, batch, student); 
		
		//Sort the VideoContentBean in descending order based on the video content id
		sessionVideosList = ContentUtil.sortInDesc(sessionVideosList);
		
		//return common session video's list based on filter values 
		return sessionVideosList;
	}
	
	public List<VideoContentAcadsBean> fetchCommonSessionSubjectVideos(String searchItem, String faculty,List<String> programSemSubjectIds,
														String acadCycleDateFormat, String batch, StudentAcadsBean student) {
		List<VideoContentAcadsBean> sessionVideosList = null;
		
		//Get common session recordings a particular filter
		sessionVideosList =videoContentDAO.getCommonSessionSubjectVideos(searchItem, faculty, programSemSubjectIds, 
																		acadCycleDateFormat, batch, student); 
				
		//return common session video's list based on filter values 
		return sessionVideosList;
	}
	
	
	public List<VideoContentAcadsBean> fetchSessionVideosForFilter(String searchItem, String faculty, List<String> programSemSubjectIdList, 
		String cycle, String batch,List<String> commonSubjectList, String program, List<SearchBean> academicCycleList){
		
		List<VideoContentAcadsBean> sessionVideosList = null;
		List<VideoContentAcadsBean> commonSubVdoContentList = null;
		String programSemSubjectIds = null,commonSubjects = null;
		String acadCycleDateFormat = "";
		
		commonSubVdoContentList = new ArrayList<VideoContentAcadsBean>();
		
		//Preparing subjectCodeId's into one string for IN clause
		programSemSubjectIds = ContentUtil.frameINClauseString(programSemSubjectIdList);
		
		if("All".equals(cycle))
			acadCycleDateFormat = ContentUtil.pepareAcadDateFormat(academicCycleList);
		else
			acadCycleDateFormat = ContentUtil.prepareAcadDateFormat(cycle);
				
		if(commonSubjectList.size()>0) {
			//Preparing commonSubject's into one string for IN clause
			commonSubjects = ContentUtil.frameINClauseString(commonSubjectList);
			
			//Fetch Session Recording for common subjects from temp table
			/*List<VideoContentBean> commonSubVdoContentList = this.getCommonSubjectSessionRecording(commonSubjects, acadDateFormat,
																					batch, faculty);*/
			//Fetch Session Recording for common subjects by old logic
			commonSubVdoContentList=videoContentDAO.getCommonSubjectVideos(commonSubjects, faculty, cycle, batch, program);
		}
		
		//Get session recordings for current cycle or previous cycle and particular filter
		/*sessionVideosList = videoContentDAO.getSessionVideosForFilter(searchItem, faculty, programSemSubjectIds, cycle, 
																		batch, program, commonSubjects);*/
		
		//Get session recordings for current cycle or previous cycle and particular filter
		sessionVideosList = videoContentDAO.getSessionVideosByFilter(searchItem, faculty, programSemSubjectIds, 
											acadCycleDateFormat,batch);
		
		//Merge common subject session recordings with applicable PSS session recordings
		sessionVideosList.addAll(commonSubVdoContentList);

		//Sort the VideoContentBean in descending order based on the video content id
		sessionVideosList = ContentUtil.sortInDesc(sessionVideosList);
		
		//return session video's list based on filter values
		return sessionVideosList;
	}
	
	public List<VideoContentAcadsBean> fetchOldCycleSessionVideosForFilter(String searchItem, String faculty, 
															List<String> subjectCodeIdList,String cycle, 
															String batch,String program, List<String> commonSubjectList){
		List<VideoContentAcadsBean> sessionVideosList = null;
		String subjectCodeIds = null,commonSubjects = null;
		
		//Preparing subjectCodeId's into one string for IN clause
		subjectCodeIds = ContentUtil.frameINClauseString(subjectCodeIdList);
				
		//Preparing commonSubject's into one string for IN clause
		commonSubjects = ContentUtil.frameINClauseString(commonSubjectList);
		
		//Get session recordings for old cycle and particular filter
		sessionVideosList = videoContentDAO.getOldCycleSessionVideosForFilter(searchItem, faculty, subjectCodeIds, cycle, 
																				batch, program, commonSubjects);
		
		//return session video's list based on filter values 
		return sessionVideosList;
	}
	
	public String fetchSubjectBySubjectCodeId(String subjectCodeId) {
		String subject = "";
		
		//Get subject name of a particular subject code Id
		subject = videoContentDAO.getSubjectBySubjectCodeId(subjectCodeId);	
		
		//return subject name
		return subject;
	}
	
	/**
	 * @param 	year
	 * @param	month
	 * @return	String	return success or failure message
	 * @throws 	Exception *
	 * */
	@Transactional(propagation = Propagation.REQUIRED , rollbackFor = Exception.class)
	 public String dataTransferForNrmlStudent(String year, String month) throws Exception {
		 String result = null;
		 int count = 0;
		
		 //Transfer data from video content table to de-normalized table based on Year and Month
		 count = videoContentDAO.transferData(year,  month);
		 
		 //prepare success or failure message based on count
		 if(count > 0) 
			 result = count+" record are transfered successfully.";
		 else
			 result ="Failed to transfer. please try again." ;
		 
		 //return success or failure message
		 return result;
	 }
	
	public List<SearchBean> fetchAcademicCycle(){
		List<SearchBean> acadCycleList = null;
		
		//Get all unique academic cycle for PG 
		acadCycleList = videoContentDAO.getAcademicCycle();
		
		//return academic cycle
		return acadCycleList;
	}
	
	/**
	 * Fetch subject name by given program seme subject id.
	 * @param programSemSubjectId - particular subject PSS id.
	 * @return String - subject name.
	 */
	public String fetchSubjectByPSSId(String programSemSubjectId) {
		String subject = "";
		
		//Get subject name of a particular subject code Id
		subject = videoContentDAO.getSubjectByProgramSemSubjectId(programSemSubjectId);	
		
		//return subject name
		return subject;
	}
	
	public void uploadSessionRecordingAudio(VideoContentAcadsBean videoContentBean) throws IOException {
		String fileName = videoContentBean.getSessionId() + "_" + RandomStringUtils.randomAlphanumeric(20) + ".m4a";
		if(videoContentBean.getAudioFile().indexOf("%3A") != -1 || videoContentBean.getAudioFile().indexOf("%25") != -1) {
			for(int i=0;i < 15;i++) {
				if(videoContentBean.getAudioFile().indexOf("https://ngasce.zoom.us") != -1) {
					break;
				}
				try {
					videoContentBean.setAudioFile(java.net.URLDecoder.decode(videoContentBean.getAudioFile(),"UTF-8"));
				}catch(Exception exception){
					exception.getStackTrace();
					break;
				}
			}
		}
		String audioFileWithEncode = videoContentBean.getAudioFile();
		try {
			audioFileWithEncode = URLEncoder.encode(videoContentBean.getAudioFile(),StandardCharsets.UTF_8.toString());
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		String url = AWS_UPLOAD_URL_LOCAL + "uploadAudioFile?public=true&filePath=" + audioFileWithEncode + "&keyName=" + fileName + "&bucketName=sessionaudiorecording";
		String result = awsHelper.uploadOnAWS(url);
		//String result = null;
		if(result != null) {
			videoContentBean.setAudioUrl_status("success");
			videoContentBean.setOldaudioUrl(videoContentBean.getAudioFile());
			videoContentBean.setAudioFile(result);
		}else {
			videoContentBean.setAudioUrl_status("failed");
			videoContentBean.setAudioUrl_retry(videoContentBean.getAudioUrl_retry() + 1);
		}
		videoContentDAO.updateVideoContentAudioDetails(videoContentBean);
	}

	/**
	 * Get all session videos matching to the search keyword.
	 * @param searchKeyword - search keyword given by user.
	 * @return List - returns the session videos matched with search keyword.
	 * @throws Exception If any exception occurs while executing the logic 
	 */
	public List<VideoContentAcadsBean> fetchSessionVideosBySearch(String searchKeyword) throws Exception{
		List<VideoContentAcadsBean> sessionVideoList = null;
		
		//Get matched session video for a specific keyword searched by user.
		sessionVideoList = videoContentDAO.getSessionVideosBySearch(searchKeyword);
		
		//Sort the session videos in descending order.
		sessionVideoList = ContentUtil.sortInDesc(sessionVideoList);
		
		//return the sorted session video list.
		return sessionVideoList;
	}
	
	/**
	 * Get all applicable session videos based on PSSId's and matching for a search keyword.
	 * @param searchKeyword - search keyword given by user.
	 * @param PSSIdList - program sem subject id's list.
	 * @param commonSubjectList - common subjects list.
	 * @param program - contains the program of a student.
	 * @return List return the list of session videos matching for a given search keyword.
	 * @throws java.lang.Exception If any exception occurs while executing the logic.
	 */
	public List<VideoContentAcadsBean> fetchSessionVideosBySearch(String searchKeyword, List<String> PSSIdList, 
			List<String> commonSubjectList, String program) throws Exception{
		List<VideoContentAcadsBean> sessionVideoList = null;
		String PSSIds = null;
		String commonSubjects = null;
		
		//Prepare PPSId's list into a single string
		PSSIds = ContentUtil.frameINClauseString(PSSIdList);
		
		//Prepare common subject list into a single string
		commonSubjects = ContentUtil.frameINClauseString(commonSubjectList);
		
		//Get matched applicable session video for a specific keyword searched by user.
		sessionVideoList = videoContentDAO.getSessionVideosBySearch(searchKeyword, PSSIds, commonSubjects, program);
		
		//Sort the session videos in descending order.
		sessionVideoList = ContentUtil.sortInDesc(sessionVideoList);
		
		//return the sorted session video list.
		return sessionVideoList;
	}
	
	/**
	 * This is used for retrieve the session video's based on the subject code Id and academic cycle list. 
	 * @param subjectCodeId - Contains subjectCodeiId of particular subject.
	 * @param academicCycleList - Contains all applicable academic cycle list. 
	 * @return List - return the session videos for subject code Id and academic cycle list.
	 * @throws Exception throws an exception if and only if any exception occurs in logic.
	 */
	public List<VideoContentAcadsBean> fetchSessionVideosForSubject(Integer subjectCodeId, 
			List<SearchBean> academicCycleList) throws Exception{
		List<VideoContentAcadsBean> sessionVideoList = null;
		String acadDateFormat = null;
		
		//Prepare academic date format for given academic cycle list into a single string. 
		acadDateFormat = ContentUtil.pepareAcadDateFormat(academicCycleList);
		
		//Get session session videos based on the subject code Id for applicable cycle list.
		sessionVideoList = videoContentDAO.getSessionVideosForSubject(subjectCodeId, acadDateFormat);
		
		//Sort the session videos in descending order.
		sessionVideoList = ContentUtil.sortInDesc(sessionVideoList);
		
		//return the sorted session video list.
		return sessionVideoList;
	}
	
	/**
	 * Get the subject code if for a given session id.
	 * @param sessionId - session id.
	 * @return Integer - returns a subject code id.
	 * @throws Exception if any exception occurs.
	 */
	public Integer fetchSubjectCodeId(Integer sessionId) throws Exception{
		Integer subjectCodeId = 0;

		//Get subject code id for a particular session
		subjectCodeId = videoContentDAO.getSubjectCodeId(sessionId);

		//return subject code id.
		return subjectCodeId;
	}
	
	/**
	 * Fetch the all session video details having the mobileUrlHd, mobileUrlSd1, mobileUrlSd2 is null 
	 * that is blank download links. 
	 * @return List of session recording details.
	 * @throws Exception If any exception raised while fetching the details.
	 */
	public List<VideoContentAcadsBean> fetchBlankDownloadLinksVideoDeatils() throws Exception{
		List<VideoContentAcadsBean> blanksDownloadLinksVideoDetails = null;
		
		//get all blanks download links video details.
		blanksDownloadLinksVideoDetails = videoContentDAO.getBlankLinksVideoDetails();
		
		//return list of video details.
		return blanksDownloadLinksVideoDetails;
	}
	
	/**
	 * Update session videos download links 
	 * @param sessionVideoDetails - session video bean having the urls and videoContentId
	 * @return return success message with updated count.
	 * @throws Exception If any exception raised or no record found for update.
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String updateVideoDownloadUrls(VideoContentAcadsBean sessionVideoDetails) throws Exception{
		int updatedVCCount = 0, updateQuickCount=0;
		String resultMessage = "";
		
		//Update video download urls in video content table.
		updatedVCCount = videoContentDAO.updateVideoDownloadUrls(sessionVideoDetails, "acads.video_content");
		
		//Update video download urls in quick video content table only when session video found for PG
		if(sessionVideoDetails.getSessionPlanModuleId() ==null || sessionVideoDetails.getSessionPlanModuleId()== 0 )
			updateQuickCount = videoContentDAO.updateVideoDownloadUrls(sessionVideoDetails, "acads.quick_video_content");
		
		//If updated count is grater than zero then return success message else throw exception. 
		if(updatedVCCount > 0)
			resultMessage = updatedVCCount+" row has updated in video content and "+updateQuickCount+ " rows updated in quick video content.";
		else 
			throw new Exception("Session video record found for update.");
		
		//return updated count details.
		return resultMessage;
	}
	
	/**
	 * Update session videos download links 
	 * @param sessionVideoDetails - session video bean having the urls and videoContentId
	 * @return return success message with updated count.
	 * @throws Exception If any exception raised or no record found for update.
	 */
	public String createVideoContentForNewMasterKey(String oldConsumerProgramStructureId, String newConsumerProgramStructureId, String acadYear, String acadMonth) throws Exception{
		String resultMessage = "";
		
		quickVideoContentForNewMasterKey_logger.info("Request MasterKey >>"+oldConsumerProgramStructureId);
		
		//list of videoContentId's for old masterkey
		ArrayList<VideoContentAcadsBean> listOfBean = videoContentDAO.getVideoContentEntriesForMasterKey(oldConsumerProgramStructureId, acadYear, acadMonth);
		quickVideoContentForNewMasterKey_logger.info("listOfBean >>"+listOfBean.size());
		HashMap<String, String> mapOfMasterKeySubjectAndPssId =new HashMap<String, String>();
		for (VideoContentAcadsBean bean : listOfBean) {
			try {
				quickVideoContentForNewMasterKey_logger.info("Subject >>" + bean.getSubject() + " new Masterkey >>"+ newConsumerProgramStructureId + " videoContentId>>" + bean.getId());
				if (!mapOfMasterKeySubjectAndPssId.containsKey(newConsumerProgramStructureId + "-" + bean.getSubject())) {
					// get pssId for new ConsumerProgramStructureId and subject name
					quickVideoContentForNewMasterKey_logger.info("Fetching pssId from DB!");
					String pssId = videoContentDAO.getPssIdForMasterKeyAndSubject(newConsumerProgramStructureId,bean.getSubject());
					mapOfMasterKeySubjectAndPssId.put(newConsumerProgramStructureId + "-" + bean.getSubject(), pssId);
					bean.setProgramSemSubjectId(pssId);
				} else {
					quickVideoContentForNewMasterKey_logger.info("Fetching pssId from map!");
					String pssId = mapOfMasterKeySubjectAndPssId.get(newConsumerProgramStructureId + "-" + bean.getSubject());
					bean.setProgramSemSubjectId(pssId);
				}

				quickVideoContentForNewMasterKey_logger.info("pssId >>" + bean.getProgramSemSubjectId());
				videoContentDAO.insertQuickVideoContentForNewMasterKey(bean);
			} catch (Exception e) {
				// TODO: handle exception
				quickVideoContentForNewMasterKey_logger.error("Error while inserting entry for subject >>"
						+ bean.getSubject() + " : error :" + e.getMessage());
			}

		}
		resultMessage="Entries added successfully for new masterKey - "+newConsumerProgramStructureId;
		return resultMessage;
	}
	
	/**
	  * Fetch all program semester subjects id's along with the subjects.
	  * @param allsubjects - All applicable subject list to the current user.
	  * @param consumerProgramStructureId - Master key of the current user.
	  * @return List having the PSSIds with the subject.
	  */
	 public List<ConsumerProgramStructureAcads> getProgramSemSubjectId(ArrayList<String> allsubjects,
	 		String consumerProgramStructureId) {
	 	List<ConsumerProgramStructureAcads> PSSIdsList = null;
	 	
	 	//Get list of program sem subject ids list
	 	PSSIdsList = videoContentDAO.getProgramSemSubjectId(allsubjects, consumerProgramStructureId);
	 	
	 	//return list
	 	return PSSIdsList;
	 }//getProgramSemSubjectId(-,-)
	 
	 public List<SearchBean> getApplicableCycles(List<String> programSemSubjectIdsList) {
	 	List<SearchBean> applicableAcadCycle = null;
	 	
	 	String programSemSubjectIds = ContentUtil.frameINClauseString(programSemSubjectIdsList);
	 	
	 	applicableAcadCycle = videoContentDAO.getApplicableCycles(programSemSubjectIds);
	 	
	 	return applicableAcadCycle;
	 }
	 
	 /**
		 * To get the applicable drop-down values for the session videos page like tracks,faculties,subjects and academic cycles.  
		 * @param PSSIds - applicable program sem subjects id's in single string.
		 * @param acadDateFormat - selected or default academic cycle. 
		 * @param facultyId - selected or default faculty ID.
		 * @param track - selected or default track.
		 * @return Map return the map of PSSWithSubjectList, FacultiesList, TrackList and AcadCycleList.
		 */
		@SuppressWarnings({"rawtypes"})
		public Map<String,List> fetchSessionVideoDropdownValues(String PSSIds,String acadDateFormat,String facultyId,String track) {
			
			//Get drop-down values based on the selected input by user.
			 List<VideoContentAcadsBean> filterValuesList = videoContentDAO.getSessionVideoDropdownValues(PSSIds, acadDateFormat, facultyId, track);
			 return setDynamicFilterValue(filterValuesList);
		}
		
		public Map<String,List> setDynamicFilterValue(List<VideoContentAcadsBean> filterValuesList){
			Map<String,List> filterValuesMap = new HashMap<String,List>();
			//Create 4 Set Faculty,Track,Subject and Academic Cycle for drop-down to store data.
			Set<FacultyFilterBean> facultySet = new HashSet<FacultyFilterBean>();
			Set<SearchBean> cycleSet = new HashSet<SearchBean>();
			Set<String> trackSet = new HashSet<String>();
			Set<ConsumerProgramStructureAcads> pssIdAndSubjSet = new HashSet<ConsumerProgramStructureAcads>();
			Set<String> commonSubject = new HashSet<String>();
						
			//Filter unique Faculty,Track,Subject and Academic Cycle and store into particular Set.
			filterValuesList.forEach((vdoCntBean)->{
				SearchBean sb = new SearchBean();
				FacultyFilterBean facultyFltrBean = new FacultyFilterBean();
				ConsumerProgramStructureAcads conmrPrgStructure = new ConsumerProgramStructureAcads();

				//Academic Cycle
				sb.setMonth(vdoCntBean.getMonth());
				sb.setYear(vdoCntBean.getYear());

				//Faculty
				facultyFltrBean.setFacultyId(vdoCntBean.getFacultyId());
				facultyFltrBean.setFirstName(vdoCntBean.getFirstName());
				facultyFltrBean.setLastName(vdoCntBean.getLastName());

				//PSSId's with Subject
				
				if(!StringUtils.isBlank(vdoCntBean.getProgramSemSubjectId())) {	
					
				conmrPrgStructure.setProgramSemSubjectId(vdoCntBean.getProgramSemSubjectId());
				conmrPrgStructure.setSubject(vdoCntBean.getSubject().trim());
				pssIdAndSubjSet.add(conmrPrgStructure);
				
				}else {
					commonSubject.add(vdoCntBean.getSubject());
				}
				//Track 
				trackSet.add(vdoCntBean.getTrack());
			
				//Add to particular Set
				cycleSet.add(sb);
				facultySet.add(facultyFltrBean);
				
				
				});
			
			//Remove null and empty entries from set for tracks.
			trackSet.remove(null);
			trackSet.remove("");

			//Convert all set into the list and add to map
			filterValuesMap.put("FACULTIES", facultySet.stream()
					.collect(Collectors.toList()));
			filterValuesMap.put("ACADCYCLES",cycleSet.stream()
					.collect(Collectors.toList()));
			filterValuesMap.put("PSSIdWITHSUBJECT",pssIdAndSubjSet.stream()
					.collect(Collectors.toList()));
			filterValuesMap.put("TRACKS",trackSet.stream()
					.collect(Collectors.toList()));
			filterValuesMap.put("COMMONSUBJECTS",commonSubject.stream()
					.collect(Collectors.toList()));
			filterValuesMap.put("allVideoList",filterValuesList);
			//Return applicable values map.
			return filterValuesMap;
		}
		
		public List<FacultyFilterBean> getSessionVideosByFaculty(String searchItem, String faculty,
				List<String> programSemSubjectIds, 
				String cycle, String batch,List<SearchBean> academicCycleList,String consumerProgramStructureId){
			List<FacultyFilterBean> videoContentList = null;
			String programSemSubjectId = ContentUtil.frameINClauseString(programSemSubjectIds);
		
			String acadCycleDateFormat = "";
		
			if("All".equals(cycle))
				acadCycleDateFormat = ContentUtil.pepareAcadDateFormat(academicCycleList);
			else
				acadCycleDateFormat = ContentUtil.prepareAcadDateFormat(cycle);
			
			videoContentList = videoContentDAO.getSessionVideosByFaculty(searchItem, faculty, programSemSubjectId, 
					acadCycleDateFormat,batch);
			
			videoContentList.addAll(videoContentDAO.getCommonSessionVideosByFaculty(faculty, programSemSubjectId, 
					cycle,batch,consumerProgramStructureId));
			HashSet hs = new HashSet(); //remove all the duplicates from the list
			hs.addAll(videoContentList); 
			videoContentList.clear();
			videoContentList.addAll(hs);
			
			return videoContentList;
		}
		
		public List<ConsumerProgramStructureAcads> getSessionVideosBySubject(String searchItem, String faculty,
				List<String> programSemSubjectIds, 
				String cycle, String batch,List<SearchBean> academicCycleList){
			String programSemSubjectId = ContentUtil.frameINClauseString(programSemSubjectIds);
			String acadCycleDateFormat = "";
			if("All".equals(cycle))
				acadCycleDateFormat = ContentUtil.pepareAcadDateFormat(academicCycleList);
			else
				acadCycleDateFormat = ContentUtil.prepareAcadDateFormat(cycle);
			
			return videoContentDAO.getSessionVideosBySubject(searchItem, faculty, programSemSubjectId, 
					acadCycleDateFormat,batch);
		}
		
		public List<String> getSessionVideosByTrack(String searchItem, String faculty,
				List<String> programSemSubjectIds, 
				String cycle, String batch,List<SearchBean> academicCycleList){
			String programSemSubjectId = ContentUtil.frameINClauseString(programSemSubjectIds);
			String acadCycleDateFormat = "";
			if("All".equals(cycle))
				acadCycleDateFormat = ContentUtil.pepareAcadDateFormat(academicCycleList);
			else
				acadCycleDateFormat = ContentUtil.prepareAcadDateFormat(cycle);
			
			return videoContentDAO.getSessionVideosByTrack(searchItem, faculty, programSemSubjectId, 
					acadCycleDateFormat,batch);
		}
		
		public List<String> getSessionVideosByCommon(List<String> commonSubjectList, String faculty,String cycle,String batch,String program){
			//Preparing commonSubject's into one string for IN clause
			String commonSubjects = ContentUtil.frameINClauseString(commonSubjectList);
						//Fetch Session Recording for common subjects by old logic
			return videoContentDAO.getCommonSubjectVideosList(commonSubjects, faculty, cycle, batch, program);

		}


	public HashMap<String, VideoContentAcadsBean> getMapOfSessionIdAndVideoContentRecord(){		
		try {
			VideoContentDAO dao = (VideoContentDAO) act.getBean("videoContentDAO");
			List<VideoContentAcadsBean> listOfAllVideoContent = dao.getAllVideoContentList();
			this.mapOfSessionIdAndVideoContentRecord = new HashMap<String, VideoContentAcadsBean>();
			for (VideoContentAcadsBean video : listOfAllVideoContent) {
				this.mapOfSessionIdAndVideoContentRecord.put(String.valueOf(video.getSessionId()), video);
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return mapOfSessionIdAndVideoContentRecord;
	}
	
	
	public ArrayList<VideoContentAcadsBean> getAllSessionRecording(VideoContentAcadsBean videoContentBean) {

		quickVideoContentForNewMasterKey_logger.info("Entering getAllSessionRecording() method of VideoContentService");

		VideoContentDAO videoContentDAO = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> sessionRecordingList = Collections.synchronizedList(new ArrayList<>());
		List<VideoContentAcadsBean> acadsSessionsList = Collections.synchronizedList(new ArrayList<>());
		List<VideoContentAcadsBean> acadsFacultyList = Collections.synchronizedList(new ArrayList<>());

		try {
			sessionRecordingList = videoContentDAO.getAllSessionRecording(videoContentBean);
			acadsSessionsList = videoContentDAO.getAcadsSessionsList(sessionRecordingList, videoContentBean);

			acadsFacultyList = videoContentDAO.getAcadsFacultyList(sessionRecordingList, videoContentBean);

			sessionRecordingList.stream().map((element) -> {
				element.setTimeBound(element.getSessionPlanModuleId() == null ? "N" : "Y");
				return element;
			}).collect(Collectors.toList());

			TimeTableDAO timeTableDAO = (TimeTableDAO) act.getBean("timeTableDAO");

			ArrayList<ProgramSubjectMappingAcadsBean> subjectProgramList = new ArrayList<ProgramSubjectMappingAcadsBean>();

			SessionDayTimeAcadsBean searchBean = new SessionDayTimeAcadsBean();
			searchBean.setYear(videoContentBean.getYear());
			searchBean.setMonth(videoContentBean.getMonth());
			searchBean.setDate(videoContentBean.getSessionDate());

			String acadDateFormat = ContentUtil.prepareAcadDateFormat(searchBean.getMonth(), searchBean.getYear());
			SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_FORMAT);
			Date date = formatter.parse(acadDateFormat);
			Date historyDate = formatter.parse(HISTORY_DATE);

			for (VideoContentAcadsBean session : sessionRecordingList) {
				int group = 1;
				String id = Integer.toString(session.getSessionId());
				if (date.compareTo(historyDate) >= 0) {

					subjectProgramList = timeTableDAO.getSubjectProgramListBySessionId(id);
				} else {
					subjectProgramList = timeTableDAO.getSubjectProgramListBySessionIdFromHistory(id);
				}

				for (ProgramSubjectMappingAcadsBean programSubjectMappingBean : subjectProgramList) {
					if (group == 1) {
						session.setGroup1Sem(programSubjectMappingBean.getSem());
						session.setGroup1Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
					} else if (group == 2) {
						session.setGroup2Sem(programSubjectMappingBean.getSem());
						session.setGroup2Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
					} else if (group == 3) {
						session.setGroup3Sem(programSubjectMappingBean.getSem());
						session.setGroup3Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
					} else if (group == 4) {
						session.setGroup4Sem(programSubjectMappingBean.getSem());
						session.setGroup4Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
					} else if (group == 5) {
						session.setGroup5Sem(programSubjectMappingBean.getSem());
						session.setGroup5Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
					} else if (group == 6) {
						session.setGroup6Sem(programSubjectMappingBean.getSem());
						session.setGroup6Program(programSubjectMappingBean.getPrgmStructApplicable() + ": "
								+ programSubjectMappingBean.getProgram());
						group++;
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		quickVideoContentForNewMasterKey_logger.info("Exiting getAllSessionRecording() method of VideoContentService");
		return (ArrayList<VideoContentAcadsBean>) sessionRecordingList;
	}	
	
	public List<SearchBean> getSessionVideosByAcadCycle(String faculty,
			List<String> programSemSubjectIds, String batch,List<String> commonSubjects,String consumerProgramStructureId,String academicCycle){
		
		List<SearchBean> acadCycleList = null;
		SearchBean bean = new SearchBean();
		bean.setMonth(academicCycle.substring(0,3));
		bean.setYear(academicCycle.substring(3, academicCycle.length()));
		String programSemSubjectId = ContentUtil.frameINClauseString(programSemSubjectIds);
		String commonSubject = ContentUtil.frameINClauseString(commonSubjects);
	
		acadCycleList = videoContentDAO.getSessionVideosByAcadCycle(faculty,programSemSubjectId,batch);
		
		acadCycleList.addAll(videoContentDAO.getAcadCycleOfCommonSession(commonSubject, faculty, batch, 
				consumerProgramStructureId));
	
		HashSet hs = new HashSet(); //remove all the duplicates from the list
		hs.addAll(acadCycleList); 
		hs.add(bean);
		acadCycleList.clear();
		acadCycleList.addAll(hs);
		
		Collections.sort(acadCycleList, new Comparator<SearchBean>() {
            public int compare(final SearchBean object1, final SearchBean object2) {
                return object1.getYear().toLowerCase().compareTo(object2.getYear().toLowerCase());
            }
        });
		//return academic cycle
		return acadCycleList;
	}
	
	public List<VideoContentAcadsBean> fetchSessionVideosBySearchAndFaculty(String searchKeyword, String facultyId) throws Exception{
		List<VideoContentAcadsBean> sessionVideoList = null;
		
		//Get matched session video for a specific keyword searched by user.
		sessionVideoList = videoContentDAO.getSessionVideosBySearchAndFaculty(searchKeyword,facultyId);
		
		//Sort the session videos in descending order.
		sessionVideoList = ContentUtil.sortInDesc(sessionVideoList);
		
		//return the sorted session video list.
		return sessionVideoList;
	}

	public List<VideoContentAcadsBean> getManualRecordingUploadReport(VideoContentAcadsBean videoContentBean, Map<String, String> subjectCodeMapWithId) {
		
		List<VideoContentAcadsBean> manualRecordingUploadListWithSubjectCode = new ArrayList<>();		
		List<VideoContentAcadsBean> manualRecordingReportList = new ArrayList<>();		
		List<Integer> sessionIdList = new ArrayList<>();
		List<Integer> sessionIdDistinctList = new ArrayList<>();

		manualRecordingReportList = videoContentDAO.getAcadsVideoContent(videoContentBean.getYear(),
				videoContentBean.getMonth(), videoContentBean.getFromSessionDate(),
				videoContentBean.getToSessionDate(), videoContentBean.getFacultyId());
	
		if (videoContentBean.getSubjectCodeId() != null) {
			if (!manualRecordingReportList.isEmpty()) {
				manualRecordingReportList.stream().map((element) -> {					
					sessionIdList.add(element.getSessionId());
					return element;
				}).collect(Collectors.toList());
			}
			sessionIdDistinctList = videoContentDAO.getAcadsVideoContentWithSubjectCode(sessionIdList, String.valueOf(videoContentBean.getSubjectCodeId()));
			if (!sessionIdDistinctList.isEmpty()) {
				for (VideoContentAcadsBean element : manualRecordingReportList) {
					for(Integer sessionElement: sessionIdDistinctList) {
						if (element.getSessionId().equals(sessionElement)) {
							manualRecordingUploadListWithSubjectCode.add(element);
						}
					}
				}
			}
			return manualRecordingUploadListWithSubjectCode;
		} else {
			return manualRecordingReportList;
		}		
	}
}