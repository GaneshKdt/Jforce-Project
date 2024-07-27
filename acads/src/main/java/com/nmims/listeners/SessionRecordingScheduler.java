package com.nmims.listeners;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletConfig;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.ServletConfigAware;
import org.zeroturnaround.zip.ZipUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nmims.beans.ParticipantReportBean;
import com.nmims.beans.RecordingStatus;
import com.nmims.beans.SessionBean;
import com.nmims.beans.SessionDayTimeAcadsBean;
import com.nmims.beans.StudentAcadsBean;
import com.nmims.beans.VideoContentAcadsBean;
import com.nmims.daos.ContentDAO;
import com.nmims.daos.NotificationDAO;
import com.nmims.daos.ReportsDAO;
import com.nmims.daos.TimeTableDAO;
import com.nmims.daos.VideoContentDAO;
import com.nmims.helpers.AWSHelper;
import com.nmims.helpers.HttpDownloadUtilityHelper;
import com.nmims.helpers.MailSender;
import com.nmims.helpers.MobileNotificationHelper;
import com.nmims.helpers.TextTrackImplLine;
import com.nmims.helpers.VimeoManager;
import com.nmims.helpers.WebVttParser;
import com.nmims.helpers.ZoomManager;
import com.nmims.services.AttendnaceReportService;

import com.nmims.services.StudentService;
import com.nmims.services.VideoContentService;
import com.nmims.util.ContentUtil;

public class SessionRecordingScheduler implements ApplicationContextAware, ServletConfigAware {
	private static final Logger logger = LoggerFactory.getLogger(SessionRecordingScheduler.class);
	private Logger recording_upload_logger = LoggerFactory.getLogger("session_recording_upload");
	private Logger video_Downloads_Urls_logger = LoggerFactory.getLogger("video_Downloads_Urls");
	private static final Logger sessionAttendance = LoggerFactory.getLogger("sessionAttendanceReport");
	
	@Value( "${SERVER}" )
	private String SERVER;

	@Value( "${ENVIRONMENT}" )
	private String ENVIRONMENT;
	
	@Value( "${ZOOM_VIDEO_TARGET_FOLDER}" )
	private String ZOOM_VIDEO_TARGET_FOLDER;
	
	@Value("${TRANSCRIPT_PATH}")
	private String TRANSCRIPT_PATH;
	
	/*@Value("${TRANSCRIPT_SERVER_PATH}")
	private String TRANSCRIPT_SERVER_PATH;*/
	
	@Value("${AWS_UPLOAD_URL}")
	private String AWS_UPLOAD_URL;
	
	@Value("${AWS_UPLOAD_URL_LOCAL}")
	private String AWS_UPLOAD_URL_LOCAL;
	
	@Value("${TIMEBOUND_PORTAL_LIST}")
	private List<String> TIMEBOUND_PORTAL_LIST;
	
	@Autowired
	private VideoContentService videoContentService;
	
	@Autowired
	private AWSHelper awsHelper;
	
	@Autowired
	StudentService studentService;
	
	private static ApplicationContext act = null;
	private static ServletConfig sc = null;
	
	@Autowired
	private NotificationDAO notificationDAO;
	
	@Autowired
	private MobileNotificationHelper mobileNotificationHelper;
	
	@Autowired
	AttendnaceReportService attendanceService;
	
	private ArrayList<String> nonPG_ProgramList = new ArrayList<String>(Arrays.asList("BBA", "B.Com", "PD - WM","PD - DM",
			"M.Sc. (App. Fin.)", "CP-WL", "CP-ME", "BBA-BA"));
	
	@Override
	public void setServletConfig(ServletConfig config) {
		sc = config;
	}
 
	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		act = context;
	}
	
//	@Scheduled(fixedDelay=10*60*1000)
//	public void loadZoomAudioFiles() {
//		if(!"tomcat4".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
//			System.out.println("Not running synchronizeRecordingUpload: scheduler since this is not tomcat4. This is "+SERVER);
//			return;
//		}
//		System.out.println("---------------->>>>>>> loadZoomAudioFiles");
//		try {
//			TimeTableDAO timetableDAO = (TimeTableDAO)act.getBean("timeTableDAO");
//			List<VideoContentBean> videoContentBeansList = timetableDAO.getVideoContentTop50();
//			System.out.println("------------->>>>>>> videoContentBeansList.size() : " + videoContentBeansList.size());
//			for(VideoContentBean videoContentBean : videoContentBeansList) {
//				try {
//					if(videoContentBean.getMeetingKey() != null) {
//						HashMap<String, String> result = getURLByMeetingId(videoContentBean.getMeetingKey());
//						System.out.println("--------->>>>>>> result.get(\"m4ADwnldUrl\") : " + result.get("m4ADwnldUrl"));
//						if(result.get("m4ADwnldUrl") != null) {
//							System.out.println("----------->>>>> music");
//							timetableDAO.markAsProcess(videoContentBean.getId(), result.get("m4ADwnldUrl"));
//						}else {
//							System.out.println("----------->>>>> file not found");
//							timetableDAO.markAsProcessFailed(videoContentBean.getId(), "File not found");
//						}
//					}else {
//						System.out.println("----------->>>>> meeting id not found");
//						timetableDAO.markAsProcessFailed(videoContentBean.getId(), "meetingKey not found");
//					}
//				}
//				catch (Exception e) {
//					// TODO: handle exception
//					  
//				}
//			}
//		}
//		catch (Exception e) {
//			// TODO: handle exception
//			  
//		}
//	}
	
	
	@Scheduled(fixedDelay=5*60*1000)
	public void updateZoomAudioLinkWithAWSs3() {
		if(!"tomcat16".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running synchronizeRecordingUpload: scheduler since this is not tomcat16. This is "+SERVER);
			return;
		}
		VideoContentDAO videoContentDAO = (VideoContentDAO) act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> videoContentBeanList = videoContentDAO.getListOfVideoContentWithZoomAudioLink();
		if(videoContentBeanList == null) {
			return;
		}
		for (VideoContentAcadsBean videoContentBean : videoContentBeanList) {
			try {
				if(videoContentBean.getAudioFile() != null && videoContentBean.getAudioFile().contains("ngasce.zoom.us")) {
					System.out.println("----->>>>> started videoContent: " + videoContentBean.getAudioFile());
					videoContentService.uploadSessionRecordingAudio(videoContentBean);
					System.out.println("----->>>>> completed videoContent: " + videoContentBean.getAudioFile());
				}
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	

	@Scheduled(fixedDelay=10*60*1000)
	public void updateZoomMeeting() {
		if(!"tomcat16".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running synchronizeRecordingUpload: scheduler since this is not tomcat16. This is "+SERVER);
			return;
		} 
		System.out.println("---------------->>>>>>> loadZoomAudioFiles");
		try {
			TimeTableDAO timetableDAO = (TimeTableDAO)act.getBean("timeTableDAO");
			List<SessionDayTimeAcadsBean> sessionList = timetableDAO.getVideoContentFromTodayTop100();
			System.out.println("------------->>>>>>> sessionList.size() : " + sessionList.size());
			for(SessionDayTimeAcadsBean sessionBean : sessionList) {
				try {
					if(sessionBean.getMeetingKey() != null) {
						ZoomManager zoomManager = new ZoomManager();
						zoomManager.updateWebinar(sessionBean);
						if(!sessionBean.isErrorRecord()) {
							timetableDAO.updateSessionPasswordAndProccessFlag(sessionBean);
						}
					}else {
						System.out.println("----------->>>>> meeting id not found");
						timetableDAO.updateSessionProccessFlagFalse(sessionBean);
					}
				}
				catch (Exception e) {
					// TODO: handle exception
					  
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			  
		}
	}
	
	
	
	// tmp remove from prod because already running on uat6
	@Scheduled(fixedDelay=30*60*1000)
	public void removeVideoFileFromZoom() {
		if(!"tomcat17".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running synchronizeRecordingUpload: scheduler since this is not tomcat17. This is "+SERVER);
			return;
		}
		System.out.println("---------------->>>>>>> load removeVideoFileFromZoom");
		try {
			FileUtils.cleanDirectory(new File(ZOOM_VIDEO_TARGET_FOLDER + "/"));		//delete all files and folder from lists
			TimeTableDAO timetableDAO = (TimeTableDAO)act.getBean("timeTableDAO");
			timetableDAO.deleteFailedRecording();
			//List<SessionDayTimeBean> sessionDayTimeBeans = timetableDAO.getLastToLastMonthVideo();
			List<SessionDayTimeAcadsBean> sessionDayTimeBeans = new ArrayList<SessionDayTimeAcadsBean>();
			List<SessionDayTimeAcadsBean> sessionDayTimeBeans1 = timetableDAO.getLastMonthVideoUploadedRecording();
			List<SessionDayTimeAcadsBean> sessionDayTimeBeans2 = timetableDAO.getLastMonthVideoUploadedAltRecording();
			List<SessionDayTimeAcadsBean> sessionDayTimeBeans3 = timetableDAO.getLastMonthVideoUploadedAlt2Recording();
			List<SessionDayTimeAcadsBean> sessionDayTimeBeans4 = timetableDAO.getLastMonthVideoUploadedAlt3Recording();
			List<String> deletedRecording = timetableDAO.getDeletedMeetingList();
			for (SessionDayTimeAcadsBean sessionDayTimeBean_tmp : sessionDayTimeBeans1) {
				
				if(!deletedRecording.contains(sessionDayTimeBean_tmp.getId()+ "" + sessionDayTimeBean_tmp.getMeetingKey())) {
					sessionDayTimeBeans.add(sessionDayTimeBean_tmp);
				}
			}
			for (SessionDayTimeAcadsBean sessionDayTimeBean_tmp : sessionDayTimeBeans2) {
				if(!deletedRecording.contains(sessionDayTimeBean_tmp.getId() + "" + sessionDayTimeBean_tmp.getMeetingKey())) {
					sessionDayTimeBeans.add(sessionDayTimeBean_tmp);
				}
			}
			for (SessionDayTimeAcadsBean sessionDayTimeBean_tmp : sessionDayTimeBeans3) {
				if(!deletedRecording.contains(sessionDayTimeBean_tmp.getId() + "" + sessionDayTimeBean_tmp.getMeetingKey())) {
					sessionDayTimeBeans.add(sessionDayTimeBean_tmp);
				}
			}
			for (SessionDayTimeAcadsBean sessionDayTimeBean_tmp : sessionDayTimeBeans4) {
				if(!deletedRecording.contains(sessionDayTimeBean_tmp.getId() + "" + sessionDayTimeBean_tmp.getMeetingKey())) {
					sessionDayTimeBeans.add(sessionDayTimeBean_tmp);
				}
			}
			System.out.println("------------->>>>>>> videoContentBeansList.size() : " + sessionDayTimeBeans.size());
			for(SessionDayTimeAcadsBean sessionDayTimeBean : sessionDayTimeBeans) {
				String meetingId =  sessionDayTimeBean.getMeetingKey(); //"98640484205";
				String sessionId = sessionDayTimeBean.getId();
				String sessionDate = sessionDayTimeBean.getDate();
				try {
					if(meetingId != null) {
						getVideoRecordingAndDownload(sessionId,meetingId,sessionDate);
					}else {
						System.out.println("----------->>>>> meeting id not found");
						timetableDAO.markAsDelete(sessionId,meetingId, "Failed null meetingId",sessionDate);
					}
				}
				catch (Exception e) {
					// TODO: handle exception
					timetableDAO.markAsDelete(sessionId,meetingId, e.getMessage(),sessionDate);
					  
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			  
		}
	}
	
	
	@Scheduled(fixedDelay=15*60*1000)
	public void synchronizeRecordingUpload() throws IOException {
		if(!"tomcat16".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running synchronizeRecordingUpload: scheduler since this is not tomcat16. This is "+SERVER);
			return;
		}
		     
		try { 
			System.out.println("--------------> call the synchronizeRecordingUpload"); 
//			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//			DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
//			LocalDateTime now = LocalDateTime.now();
//			LocalDateTime nowTime = now.minusMinutes(30);
//			String date = dtf.format(now);
//			String time = timeFormat.format(nowTime);
//			//date = "2019-07-10";
//			System.out.println("===========>>>>>> date : " + date + " || time : " + time);
			TimeTableDAO recordingStatusDAO = (TimeTableDAO)act.getBean("timeTableDAO");
//			LocalTime time1 = LocalTime.parse("07:00:00");
//			LocalTime time2 = LocalTime.parse("19:00:00");
//			LocalTime nowUtcTime = LocalTime.now(Clock.systemUTC());
			List<SessionDayTimeAcadsBean> sessionList = null;	//get session data from session table, required for zoom api
//			if (nowUtcTime.isAfter(time1) && nowUtcTime.isBefore(time2)){
//				sessionList = recordingStatusDAO.getSessionsByDate("PG");
//				if(sessionList == null){
//					sessionList = recordingStatusDAO.getSessionsByDate("MBA-WX");
//				}
//			}else {
//				sessionList = recordingStatusDAO.getSessionsByDate("MBA-WX");
//				if(sessionList == null){
//					sessionList = recordingStatusDAO.getSessionsByDate("PG");
//				}
//			}
			sessionList = recordingStatusDAO.getSessionsByDate("MBA-WX");
			List<SessionDayTimeAcadsBean> sessionList2 = recordingStatusDAO.getSessionsByDate("PG");
			if(sessionList == null) {
				sessionList = new ArrayList<SessionDayTimeAcadsBean>();
			}
			sessionList.addAll(sessionList2);
			ZoomManager zoom = new ZoomManager();
			int i =0;
			System.out.println("##################################get length of sessionList : " + sessionList.size());
			for (SessionDayTimeAcadsBean attendList : sessionList) {
				System.out.println("--------------> session list id : " + attendList.getHostId());
				System.out.println("------> alt host id1: " + attendList.getAltHostId() + " | alt host id2: " + attendList.getAltHostId2() + " | alt host id3 : " + attendList.getAltHostId3());
				System.out.println("\n\ninside primary host id 0/" + i);
				if(!recordingStatusDAO.createRecordingStatusEntry(attendList.getMeetingKey(),attendList.getId())) {	// initiate record entry
					System.out.println("-------------> Error while creating record inside recording table");
					continue;
				}
				this.getFromZoomAndUploadToVimeo(zoom,recordingStatusDAO,attendList);
				/*String vimeoId = recordingStatusDAO.getVimeoIdFromMeetingId(attendList.getMeetingKey());
				if(vimeoId == null || "false".equalsIgnoreCase(vimeoId)) {
					this.getFromZoomAndUploadToVimeo(zoom,recordingStatusDAO,attendList);
				}else {
					recordingStatusDAO.pendingRecordingStatus(attendList.getMeetingKey(), vimeoId);
				}*/
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			  
		}
	}
	
	@Scheduled(fixedDelay=15*60*1000)
	public void vimeoStatusCheck() throws IOException {
		
		if(!"tomcat16".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running synchronizeRecordingUpload: scheduler since this is not tomcat16. This is "+SERVER);
			return;
		}
		recording_upload_logger.debug("START");
		
		VimeoManager vimeo = new VimeoManager(); 
		TimeTableDAO recordingStatusDAO = (TimeTableDAO)act.getBean("timeTableDAO");
		List<RecordingStatus> recordingStatusList = recordingStatusDAO.getPendingRecordingData();
		
		recording_upload_logger.info(recordingStatusList.size()+" SessionRecordingScheduler.vimeoStatusCheck() - Total session recodings entry has to be made.");
		int i=0;
		for (RecordingStatus recordingStatus : recordingStatusList) {
			try {
				recording_upload_logger.info("Inside for loop uploading : "+i);
				if(recordingStatus.getVimeoId() == null) {
					continue;
				}
				JsonObject vimeoResponse = vimeo.checkUploadVideoStatus(recordingStatus.getVimeoId());
				
				recording_upload_logger.info("Inserting started for SessionId :"+recordingStatus.getSessionId() +" and MeetingId :"+recordingStatus.getMeetingId() );
				insertIntoVideoContent(vimeoResponse, recordingStatus);
				i++;
			}
			catch (Exception e) {
				recording_upload_logger.error("While making entries into video content table - Error Message :"+e.getMessage());
			}
		}
		
		/*List<SessionDayTimeBean> overTimeRecordingStatusList = recordingStatusDAO.getOverTimeRecordingList();
		if(overTimeRecordingStatusList != null) {
			
			for(SessionDayTimeBean recording : overTimeRecordingStatusList) {
				try {
					vimeo.sendErrorNotification(act,recording, "Something went wrong session recording take longer time to upload");
				}
				catch (Exception e) {
					// TODO: handle exception
				}
			}
		}*/
		recording_upload_logger.info("Total vimeo status checked : " + i);
		recording_upload_logger.debug("END");
	}
	
	@Scheduled(fixedDelay=15*60*1000)
	public void updateTranscriptToVimeo() {
		if(!"tomcat16".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running synchronizeRecordingUpload: scheduler since this is not tomcat16. This is "+SERVER);
			return;
		}
		VideoContentDAO videoContentDAO = (VideoContentDAO)act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> videoContentBeansList = videoContentDAO.getVideoContentWithOutVimeoUploadedTranscript();
		for (VideoContentAcadsBean videoContentBean : videoContentBeansList) {
			try {
				String vimeoId = videoContentBean.getVideoLink();
				String transcriptUrl = videoContentBean.getVideoTranscriptUrl();
				if(vimeoId == null) {
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "Video link is empty in content");
					continue;
				}
				String[] vimeolink_array_tmp = vimeoId.split("/");
				vimeoId = vimeolink_array_tmp[vimeolink_array_tmp.length - 1];
				System.out.println("vimeoId: " + vimeoId);
				videoContentBean.setVimeoId(vimeoId);
				HttpDownloadUtilityHelper hdHelper = new HttpDownloadUtilityHelper();
				VimeoManager vimeo = new VimeoManager();
				String vttName = "Video Subtitle";
				
				String transcriptContent = hdHelper.getContentFromTranscriptUrlWithHeader(transcriptUrl);
				JsonObject vimeoResponse = vimeo.checkUploadVideoStatus(videoContentBean.getVimeoId());				// get vimeo video data
				if(vimeoResponse == null) {
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "step1 error: text track url is null");
					continue;
				}
				JsonObject metadataObject = vimeoResponse.get("metadata").getAsJsonObject();
				JsonObject connectionObject = metadataObject.get("connections").getAsJsonObject();
				JsonObject texttracksObject = connectionObject.get("texttracks").getAsJsonObject();		
				String uri = texttracksObject.get("uri").getAsString();							// got texttracks uri
				System.out.println("uri: " + uri);
				if(uri == null) {
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "step1 error: text track url-> uri is null");
					continue;
				}
				
				String getUploadLinkUrl = "https://api.vimeo.com"+uri;
				
				String uploadLink = vimeo.getUploadLinkfromUri(getUploadLinkUrl,vttName);		// get uploading link for uploading vtt file
				if(uploadLink != null) {
					if(vimeo.putSubtitleinVideo(uploadLink,transcriptContent)) {						//uploading subtitle
						String setTextTrackActive = "https://api.vimeo.com"+uri;
						System.out.println("--------->>>>> setTextTrackActive: " + setTextTrackActive);
						if(vimeo.setTextTrackActive(setTextTrackActive)) { 
							System.out.println("Successfully set active subtitle");
							videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "uploaded");
							continue;
						}else {
							videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "step4 error: failed to set active");
							continue;
						}
					}else {
						videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "step3 error: failed to put transcript content");
						continue;
					}
				}else {
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "step2 error: upload link is null");
					continue;
				}
			}
			catch (Exception e) {
				// TODO: handle exception
				  
				videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "Exception: " + e.getMessage());
			}
		}
	}
	
	@Scheduled(fixedDelay=15*60*1000)
	public void newUpdateTranscriptUrl() {
		
		if(!"tomcat16".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running synchronizeRecordingUpload: scheduler since this is not tomcat16. This is "+SERVER);
			return;
		}
		
		 
		VideoContentDAO videoContentDAO = (VideoContentDAO)act.getBean("videoContentDAO");
		List<VideoContentAcadsBean> videoContentBeansList = videoContentDAO.getVideoContentTranscriptNotFoundData();
		if(videoContentBeansList.size() <= 50) {
			List<VideoContentAcadsBean> videoContentBeansList_tmp = videoContentDAO.getVideoContentTranscriptNotFoundData2();
			videoContentBeansList.addAll(videoContentBeansList_tmp);
		}
		if(videoContentBeansList.size() <= 50) {
			List<VideoContentAcadsBean> videoContentBeansList_tmp = videoContentDAO.getVideoContentTranscriptNotFoundData3();
			videoContentBeansList.addAll(videoContentBeansList_tmp);
		}
		if(videoContentBeansList.size() <= 50) {
			List<VideoContentAcadsBean> videoContentBeansList_tmp = videoContentDAO.getVideoContentTranscriptNotFoundData4();
			videoContentBeansList.addAll(videoContentBeansList_tmp);
		}
		System.out.println("---------->>>>>> videoContentBeanList: " + videoContentBeansList.size());
		for (VideoContentAcadsBean videoContentBean : videoContentBeansList) {
			System.out.println("--->>>> videoContentId: " + videoContentBean.getId());
			String filePathWithOutEncode = null;
			try {
				HashMap<String, String> downloadUrlMap = getURLByMeetingId(videoContentBean.getMeetingKey());
				if(downloadUrlMap == null) {
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "download map is empty");
					continue;
				}
				String transcriptUrl = downloadUrlMap.get("transcriptDwnldUrl");
				if(transcriptUrl == null) {
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "transcript not found on zoom");
					continue;
				}
				System.out.println("-------->>>>> transcriptUrl: " + transcriptUrl);
				HttpDownloadUtilityHelper hdHelper = new HttpDownloadUtilityHelper();
				Random rand = new Random();
				String fileName = rand.nextInt(10000) + "_" + rand.nextInt(1000) + "_" + videoContentBean.getSessionId() + "_" + videoContentBean.getId() + "_" + System.currentTimeMillis() / 1000L + "_" + "transcriptFile.vtt";
				String filePath = hdHelper.downloadFileAndSave(transcriptUrl,TRANSCRIPT_PATH,fileName);
				if(filePath == null) {
					System.out.println("---------->>>>>> transcriptContent is empty");
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "transcriptContent saveFilePath is null");
					continue;
				}
				filePathWithOutEncode = filePath;
				try {
					filePath = URLEncoder.encode(filePath,StandardCharsets.UTF_8.toString());
				}	
				catch (Exception e) {
					// TODO: handle exception
				}
				String url = AWS_UPLOAD_URL_LOCAL + "upload?public=true&filePath="+ filePath +"&keyName="+ fileName +"&bucketName=sessiontranscriptrecording";
				System.out.println("--------<<<<< url >>>> : " + url);
				String fileUrl = awsHelper.uploadOnAWS(url);
				System.out.println("------>>>>> fileUrl: " + fileUrl);
				if(fileUrl != null) {
					videoContentDAO.updateVideoTranscriptionUrl(videoContentBean.getId() + "", fileUrl);
				}else {
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "failed to upload file on aws");
				}
			}
			catch (Exception e) {
				// TODO: handle exception
				  
				logger.error(e.getMessage());
				videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "transcriptContent error: " + e.getMessage());
			}
			System.out.println("--->>>> delete filePath: " + filePathWithOutEncode);
			if(filePathWithOutEncode != null) {
				new File(filePathWithOutEncode).delete();
			} 
		}
	}
	
	/*@Deprecated
	@Scheduled(fixedDelay=15*60*1000)
	public void updateTranscriptUrl() throws IOException {
		
		if(!"tomcat6".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running synchronizeRecordingUpload: scheduler since this is not tomcat4. This is "+SERVER);
			return;
		}
		
		VimeoManager vimeo = new VimeoManager(); 
		VideoContentDAO videoContentDAO = (VideoContentDAO)act.getBean("videoContentDAO");
		List<VideoContentBean> videoContentBeansList = videoContentDAO.getVideoContentTranscriptNotFound();
		System.out.println("---------->>>>>> videoContentBeanList: " + videoContentBeansList.size());
		for (VideoContentBean videoContentBean : videoContentBeansList) {
			try {
			String textTrackUrl = "https://api.vimeo.com/videos/"+ videoContentBean.getVimeoId() +"/texttracks";
			System.out.println("------->>>>> textTrackUrl : " + textTrackUrl);
			String transcriptionLink = vimeo.getTranscriptLinkByUploadLinkUrl(textTrackUrl);
			if(transcriptionLink != null) {
				System.out.println("------------>>>>>>> inside if condition");
				videoContentDAO.updateVideoTranscriptionUrl(videoContentBean.getId() + "", transcriptionLink);
			}else {
				System.out.println("------------>>>>>>> inside else condition");
				HashMap<String, String> downloadUrlMap = getURLByMeetingId(videoContentBean.getMeetingKey());
				if(downloadUrlMap == null) {
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "download map is empty");
					System.out.println("--------->>>>>> downloadURLMap is empty");
					continue;
				}
				String transcriptUrl = downloadUrlMap.get("transcriptDwnldUrl");
				System.out.println("-------->>>>> transcriptUrl: " + transcriptUrl);
				String vttName = "Video Subtitle";
				HttpDownloadUtilityHelper hdHelper = new HttpDownloadUtilityHelper();
				String transcriptContent = null;
				try {
					transcriptContent = hdHelper.getContentFromTranscriptUrl(transcriptUrl);// get content from transcript download url
				} catch (Exception e) {
					// TODO Auto-generated catch block
					  
				}	
				
				if(transcriptContent ==null) {
					System.out.println("---------->>>>>> transcriptContent is empty");
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "transcriptContent is empty");
					continue;
				}
				JsonObject vimeoResponse = vimeo.checkUploadVideoStatus(videoContentBean.getVimeoId());				// get vimeo video data  
				JsonObject metadataObject = vimeoResponse.get("metadata").getAsJsonObject();
				JsonObject connectionObject = metadataObject.get("connections").getAsJsonObject();
				JsonObject texttracksObject = connectionObject.get("texttracks").getAsJsonObject();		
				String uri = texttracksObject.get("uri").getAsString();							// got texttracks uri
				String mainUri = vimeoResponse.get("uri").getAsString();
				
				String getUploadLinkUrl = "https://api.vimeo.com"+uri;
				
				String uploadLink = vimeo.getUploadLinkfromUri(getUploadLinkUrl,vttName);		// get uploading link for uploading vtt file
				if(uploadLink != null) {
					vimeo.putSubtitleinVideo(uploadLink,transcriptContent);						//uploading subtitle
					String setTextTrackActive = "https://api.vimeo.com"+uri;
					System.out.println("--------->>>>> setTextTrackActive: " + setTextTrackActive);
					if(vimeo.setTextTrackActive(setTextTrackActive)) { 
						transcriptionLink = vimeo.getTranscriptLinkByUploadLinkUrl(getUploadLinkUrl);
						if(transcriptionLink != null) {
							System.out.println("---------->>>>> id: " + videoContentBean.getId() + " | transcriptionLink : " + transcriptionLink);
							videoContentDAO.updateVideoTranscriptionUrl(videoContentBean.getId() + "", transcriptionLink);
						}else {
							videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "Transcript url is empty");
							System.out.println("-------->>>>>>> transcript Url is empty");
						}
					}else {
						transcriptionLink = vimeo.getTranscriptLinkByUploadLinkUrl(getUploadLinkUrl);
						videoContentDAO.updateVideoTranscriptionUrl(videoContentBean.getId() + "", transcriptionLink);
						System.out.println("----------->>>>>>> Failed to set status Y");
					}
				}else {
					videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", "uploadLink in empty or null");
					System.out.println("----------->>>>>>> uplaodLink in empty or null");
				}
			}
			}
			catch (Exception e) {
				// TODO: handle exception
				videoContentDAO.updateVideoTranscriptionUrlFlag(videoContentBean.getId() + "", e.getMessage());
			}
		}
		
	}*/
	
		
	
	private boolean downloadRecordingUsingUrl(String downloadUrl,String filePathAndName) throws IOException {
		try {
			URL url = new URL(downloadUrl);
			Path targetPath = new File(ZOOM_VIDEO_TARGET_FOLDER + File.separator + filePathAndName).toPath();
			Files.copy(url.openStream(),targetPath,StandardCopyOption.REPLACE_EXISTING);
			return true;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			  
			return false;
		}
		
	}
	
	
	private void markAsDeletedSessionFromZoom(String sessionId,String webinarId) {
		TimeTableDAO timeTableDAO = (TimeTableDAO)act.getBean("timeTableDAO");
		timeTableDAO.markAsDeleted(sessionId,webinarId);
	}
	
	private boolean getVideoRecordingAndDownload(String sessionId,String webinarId,String sessionDate) {
		TimeTableDAO timeTableDAO = (TimeTableDAO)act.getBean("timeTableDAO");
		String folderPath = ZOOM_VIDEO_TARGET_FOLDER + "/" + webinarId;
		String folderPathOnly = folderPath;
		ResponseEntity<String> zoomResponse;
		JsonObject jsonObj;
		ZoomManager zoom = new ZoomManager();
		HttpDownloadUtilityHelper hdHelper = new HttpDownloadUtilityHelper();
		try {
			zoomResponse = zoom.getZoomRecordingList2(timeTableDAO,webinarId,sessionId,sessionDate);
			if(zoomResponse == null) {
				System.out.println("---------->>>>>>> recording not found deleted from zoom");
				//timeTableDAO.markAsDelete(sessionId,webinarId, "zoom null response",sessionDate);
				return false;	// error from zoom api, error save in recording_status table
			}
			jsonObj = new JsonParser().parse(zoomResponse.getBody()).getAsJsonObject();
			if ("200".equalsIgnoreCase(zoomResponse.getStatusCode().toString())) {
				
				String uuid=jsonObj.get("uuid").getAsString();
				
				if(jsonObj.get("recording_files") == null) {
					System.out.println("---------->>>>>>> recording not found");
					timeTableDAO.markAsDelete(sessionId,webinarId, "No recording files1",sessionDate);
					return false;
				}
				if(jsonObj.get("recording_count").getAsInt() <= 0) {
					System.out.println("---------->>>>>>> recording not found, count zero");
					timeTableDAO.markAsDelete(sessionId,webinarId, "No recording files2",sessionDate);
					return false;
				}else {
					boolean downloadVideoFlag = false;
					ArrayList<String> recordingListTmp = new ArrayList<String>(); 
					System.out.println("---->>>> recording found");
					for (JsonElement recordings : jsonObj.get("recording_files").getAsJsonArray()) {
						System.out.println("----------->>>>>> Loop started");
						JsonObject recording = recordings.getAsJsonObject();
						if(recording.get("file_type").getAsString().equals("MP4")) {
							String downloadUrl = hdHelper.getRedirectUrl(recording.get("download_url").getAsString() + "?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6Imp3dCJ9.eyJpc3MiOiJrczFPX1BCUFJmTzdxN1duSGE5UWl3IiwiZXhwIjoiMTQ5NjA5MTk2NDAwMCJ9.CvrMLpU36wK1m_M3R7yqtG9I7hXQZ6EC3FQ1eZUSwcU");
							//delete recording and download to content server
							File dir = new File(ZOOM_VIDEO_TARGET_FOLDER + "/" + webinarId);
							if (!dir.exists()) dir.mkdirs();
							String filePathAndName = webinarId + "/" + RandomStringUtils.randomAlphanumeric(20) + ".mp4";
							System.out.println(filePathAndName);
							System.out.println("------------>>>>>>> before if");
							System.out.println(ZOOM_VIDEO_TARGET_FOLDER + "/" + filePathAndName);
							if(this.downloadRecordingUsingUrl(downloadUrl, filePathAndName)) {
								System.out.println(ZOOM_VIDEO_TARGET_FOLDER + "/" + filePathAndName);
								File file = new File(ZOOM_VIDEO_TARGET_FOLDER + "/" + filePathAndName);
								if(file.exists()) {
									downloadVideoFlag = true;
									//markAsDeletedSessionFromZoom(sessionId,webinarId);
									//System.out.println("------------>>>>>> successfully download video to content server");
									System.out.println(" fileId : " + recording.get("id").getAsString());
									recordingListTmp.add(recording.get("id").getAsString());
									/*if(zoom.deleteRecordingFile(webinarId, recording.get("id").getAsString())) {
										markAsDeletedSessionFromZoom(sessionId,webinarId);
										System.out.println("------------>>>>>> successfully download video to content server");
									}
									else{
										timeTableDAO.markAsDelete(sessionId,webinarId, "Failed to delete",sessionDate);
									}*/
								}else {
									System.out.println("------>>>>> file creation error");
									timeTableDAO.markAsDelete(sessionId,webinarId, "File creation error",sessionDate);
								}
							}else {
								System.out.println("============>>>>>>> Error while downloading video to content server");
								timeTableDAO.markAsDelete(sessionId,webinarId, "Error while download file",sessionDate);
								return false;
							}
						}else {
							System.out.println("------>>>>>>> recording.get(\"file_type\") : " + recording.get("file_type"));
						}
					}
					System.out.println("---------->>>>>>>>> outside loop");
					if(!downloadVideoFlag) {
						System.out.println("---------->>>> no mp4 found");
						timeTableDAO.markAsDelete(sessionId,webinarId, "No Mp4 found",sessionDate);
						if(new File(folderPathOnly).exists()) {
							FileUtils.deleteDirectory(new File(folderPathOnly));
							//new File(folderPathOnly).delete();	//delete folder
						}
						return false;
					}
					
					System.out.println("folderPath: " + folderPath);
					ZipUtil.pack(new File(folderPath), new File(folderPath + ".zip"));
					System.out.println("Zip completed");
					String folderPathWithOutEncode = folderPath + ".zip";
					folderPath = URLEncoder.encode(folderPathWithOutEncode,StandardCharsets.UTF_8.toString());
					System.out.println("------>>>> encoded foldpath: " + folderPath);
					String url = AWS_UPLOAD_URL_LOCAL + "upload?public=false&filePath=" + folderPath + "&keyName=" + sessionId + "-" + webinarId + "-" + sessionDate + ".zip&bucketName=sessionvideorecording";
					String result = awsHelper.uploadOnAWS(url);
					if(result == null) {
						System.out.println("-----<<<<<< failed to upload on aws");
						timeTableDAO.markAsDelete(sessionId,webinarId, "Failed to upload on aws",sessionDate);
						return false;
					}
					System.out.println("==========>>>>>> upload completed");
					for (String id : recordingListTmp) {
						System.out.println("---->>>> loop started for delete");
						System.out.println("------>>>> id: " + id + " | webinarId: " + webinarId + " | uuid: "+uuid);
						if(zoom.deleteRecordingFile(uuid,id)) {
							if(folderPathWithOutEncode != null) {
								FileUtils.deleteDirectory(new File(folderPathOnly));	//delete folder
								new File(folderPathWithOutEncode).delete();	//delete zip file
								System.out.println("----->>>> file and folder deleted");
							}
							markAsDeletedSessionFromZoom(sessionId,webinarId);
						}
						else{
							timeTableDAO.markAsDelete(sessionId,webinarId, "Failed to delete",sessionDate);
						}
					}
				}
			}else {
				System.out.println("------>>>>>> invalid response");
				timeTableDAO.markAsDelete(sessionId,webinarId, "Invalid response",sessionDate);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			  
			timeTableDAO.markAsDelete(sessionId,webinarId, e.getMessage(),sessionDate);
		}
		return false;
	}
	
	
	private boolean getFromZoomAndUploadToVimeo(ZoomManager zoom,TimeTableDAO recordingStatusDAO,SessionDayTimeAcadsBean sessionsData) throws IOException {
		ResponseEntity<String> zoomResponse;
		JsonObject jsonObj;
		VimeoManager vimeo = new VimeoManager();
		HttpDownloadUtilityHelper hdHelper = new HttpDownloadUtilityHelper();
		try {
			zoomResponse = zoom.getZoomRecordingList(recordingStatusDAO,sessionsData.getMeetingKey(),true);	//get zoom webinar recording list.
			if(zoomResponse == null) {
				return false;	// error from zoom api, error save in recording_status table
			}
			jsonObj = new JsonParser().parse(zoomResponse.getBody()).getAsJsonObject();
			if ("200".equalsIgnoreCase(zoomResponse.getStatusCode().toString())) {
				if(jsonObj.get("recording_files") == null) {
					// error no recording found.
					//sendErrorNotification(sessionsData,jsonObj.get("message").getAsString());
					recordingStatusDAO.errorRecordingStatus(sessionsData.getMeetingKey(), jsonObj.get("message").getAsString());
					return false;
				}
				 
				if(jsonObj.get("recording_count").getAsInt() <= 0) {
					vimeo.sendErrorNotification(act,sessionsData,"No cloud recording found");
					recordingStatusDAO.errorRecordingStatus(sessionsData.getMeetingKey(), "No cloud recording found");	// error no cloud recording found.
				}else {
					int recordingCount = 0;
					boolean mp4flag = false;
					String downloadLink = null;
					int fileSize = 0;
					for (JsonElement recordings : jsonObj.get("recording_files").getAsJsonArray()) {
						//System.out.println("----------> zoom meetings : " + recordings.toString());
						JsonObject recording = recordings.getAsJsonObject();
						if(recording.get("file_type").getAsString().equals("MP4")) {
							mp4flag = true;
							recordingCount++;
							System.out.println("---------------> download link : " + recording.get("download_url").getAsString());
							//pass download url to vimeo to upload video on it.
							if(recording.get("file_size").getAsLong() > fileSize) {
								fileSize = recording.get("file_size").getAsInt();						
								try {								
									downloadLink = hdHelper.getRedirectUrl(recording.get("download_url").getAsString() + "?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6Imp3dCJ9.eyJpc3MiOiJrczFPX1BCUFJmTzdxN1duSGE5UWl3IiwiZXhwIjoiMTQ5NjA5MTk2NDAwMCJ9.CvrMLpU36wK1m_M3R7yqtG9I7hXQZ6EC3FQ1eZUSwcU");									
								} catch (Exception ex) {
									
								}								
							}
							//vimeo.uploadVideo(videoType,recording.get("download_url").getAsString(), "Testing auto upload zoom video", date, meeting.get("id").getAsString(), sessions);
							//return true;
							//send to video download link
						}
					}
					System.out.println("--------=====>>>>> download_url : " + downloadLink);
					if(recordingCount > 1) {
						vimeo.sendErrorNotification(act,sessionsData,"Multiple recording found");
						recordingStatusDAO.errorRecordingStatus(sessionsData.getMeetingKey(), "Multiple recording found");	//error multiple cloud recording found.
					}
					if(mp4flag && downloadLink != null) {
						vimeo.uploadVideo(recordingStatusDAO, sessionsData, downloadLink);
					}
					else{
						vimeo.sendErrorNotification(act,sessionsData,"No Mp4 zoom cloud recording file found");
						recordingStatusDAO.errorRecordingStatus(sessionsData.getMeetingKey(), "No Mp4 zoom cloud recording file found");
					}
				}
			
			}else {
				//sendErrorNotification(sessionsData,"Invalid repsonse from zoom,Status:" + zoomResponse.getStatusCode().toString());
				recordingStatusDAO.errorRecordingStatus(sessionsData.getMeetingKey(), "Invalid repsonse from zoom,Status:" + zoomResponse.getStatusCode().toString());	//response is error from zoom
			}
			return false;
		}
		catch(Exception e) {
			  
			recordingStatusDAO.errorRecordingStatus(sessionsData.getMeetingKey(), "Error : " + e.getMessage());
			System.out.println("------------------------>Error: " + e.getMessage());
			return false;
		}
	}
	
	
	
	private void insertIntoVideoContent(JsonObject vimeoResponse,RecordingStatus recordingStatus) throws IOException {
		recording_upload_logger.debug("START");
		
		//Checking condition whether video is available on vimeo or not
		TimeTableDAO recordingStatusDAO = (TimeTableDAO)act.getBean("timeTableDAO");
		VimeoManager vimeo = new VimeoManager();
		if(vimeoResponse == null) {
			recordingStatusDAO.updateVimeoStatus(recordingStatus.getMeetingId(), "Null response Vimeo");
			recording_upload_logger.error("Null Vimeo response received for MeetingKey :"+recordingStatus.getMeetingId());
			return;
		}
		System.out.println("=======>>>>>>>>>> vimeoResponse.get(\"status\").getAsString() : " + vimeoResponse.get("status").getAsString());
		if("uploading_error".equalsIgnoreCase(vimeoResponse.get("status").getAsString())) {
			SessionDayTimeAcadsBean sessionsData = new SessionDayTimeAcadsBean();
			sessionsData.setSessionId(recordingStatus.getSessionId());
			sessionsData.setMeetingKey(recordingStatus.getMeetingId());
			//vimeo.sendErrorNotification(act,sessionsData, "Error while uploading video");
			recordingStatusDAO.errorRecordingStatus(recordingStatus.getMeetingId(), "Error while uploading video");
			recording_upload_logger.error("Error while uploading video for MeetingKey :"+recordingStatus.getMeetingId());
			//recordingStatusDAO.updateVimeoStatus(recordingStatus.getMeetingId(), "Vimeo recording still not available");
			return;
		}
		if(!"available".equalsIgnoreCase(vimeoResponse.get("status").getAsString())) {
			recordingStatusDAO.updateVimeoStatus(recordingStatus.getMeetingId(), "Vimeo recording still not available");
			recording_upload_logger.error("Vimeo recording still not available for MeetingKey :"+recordingStatus.getMeetingId());
			return;
		}
		VideoContentAcadsBean videoContentBean = new VideoContentAcadsBean();
		//VideoContentDAO videoContentDAO = (VideoContentDAO) act.getBean("videoContentDAO");
		recording_upload_logger.info( "Session Id :  " + Integer.parseInt(recordingStatus.getSessionId()));
		
		SessionBean sessionBean = recordingStatusDAO.getSessionDataById(Integer.parseInt(recordingStatus.getSessionId()));
		if(sessionBean == null) {
			recording_upload_logger.error("NULL SessionBean found.");
			return;
		}
		String contentOwner = "System";
		
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		String currentDate = dateFormat.format(date);
		
		if(recordingStatus.getMeetingId().equalsIgnoreCase(sessionBean.getAltMeetingKey())) {
			sessionBean.setFacultyId(sessionBean.getAltFacultyId());
		}
		if(recordingStatus.getMeetingId().equalsIgnoreCase(sessionBean.getAltMeetingKey2())) {
			sessionBean.setFacultyId(sessionBean.getAltFacultyId2());
		}
		if(recordingStatus.getMeetingId().equalsIgnoreCase(sessionBean.getAltMeetingKey3())) {
			sessionBean.setFacultyId(sessionBean.getAltFacultyId3());
		}
		videoContentBean.setSessionId(Integer.parseInt(recordingStatus.getSessionId()));
		videoContentBean.setFileName(sessionBean.getSubject() + " " + sessionBean.getSessionName());
		videoContentBean.setFacultyId(sessionBean.getFacultyId());
		videoContentBean.setKeywords(sessionBean.getSubject() + " " + sessionBean.getSessionName());
		videoContentBean.setDescription(sessionBean.getSubject() + " " + sessionBean.getSessionName());
		if(sessionBean.getModuleId() != null && sessionBean.getModuleId() != 0) {
			videoContentBean.setSubject(sessionBean.getSubject() + "_SesssionPlan_Video");
		}else {
			videoContentBean.setSubject(sessionBean.getSubject());
		}
		HashMap<String, String> zoomResponse = getURLByMeetingId(recordingStatus.getMeetingId());
		videoContentBean.setSessionDate(sessionBean.getDate());
		videoContentBean.setAddedOn(currentDate);
		videoContentBean.setAddedBy(contentOwner);
		videoContentBean.setYear(sessionBean.getYear());
		videoContentBean.setMonth(sessionBean.getMonth());
		videoContentBean.setCreatedBy(contentOwner);
		videoContentBean.setLastModifiedBy(contentOwner);
		videoContentBean.setSessionPlanModuleId(sessionBean.getModuleId());
		videoContentBean.setDuration(vimeoResponse.get("duration").getAsString()); 
		//videoContentBean.setVideoTranscriptUrl(zoomResponse.get("transcriptDwnldUrl"));
		videoContentBean.setAudioFile(zoomResponse.get("m4ADwnldUrl"));
		//videoContentBean.setVideoTranscriptUrl(getTranscriptDownloadURLByVimeoId(recordingStatus.getVimeoId()));
		
		//get videos from vimeo response
		for(JsonElement files : vimeoResponse.get("files").getAsJsonArray() ) {
			JsonObject file = files.getAsJsonObject();
			if(file.get("link").getAsString().indexOf("profile_id=174") != -1) {
				videoContentBean.setMobileUrlHd(file.get("link").getAsString());
			}
			else if(file.get("link").getAsString().indexOf("profile_id=165") != -1) {
				videoContentBean.setMobileUrlSd1(file.get("link").getAsString());
			}
			else if(file.get("link").getAsString().indexOf("profile_id=164") != -1) {
				videoContentBean.setMobileUrlSd2(file.get("link").getAsString());
			}
		}
		
		try {
			/*This check is temporary purpose, will make logic permanent which is inside this check after some days of observation.*/
			if(videoContentBean.getMobileUrl() ==null && videoContentBean.getMobileUrlSd1() == null && videoContentBean.getMobileUrlSd2()==null) {
				recording_upload_logger.info("Mobile all three url's found null so setting updated new url's to VideoContentBean.");
				for(JsonElement files : vimeoResponse.get("files").getAsJsonArray() ) {
					JsonObject file = files.getAsJsonObject();
					if(file.get("public_name").getAsString().equalsIgnoreCase("720p") && file.get("quality").getAsString().equalsIgnoreCase("hd")) {
						videoContentBean.setMobileUrlHd(file.get("link").getAsString());
					}
					else if(file.get("public_name").getAsString().equalsIgnoreCase("540p") && file.get("quality").getAsString().equalsIgnoreCase("sd")) {
						videoContentBean.setMobileUrlSd1(file.get("link").getAsString());
					}
					else if(file.get("public_name").getAsString().equalsIgnoreCase("360p") && file.get("quality").getAsString().equalsIgnoreCase("sd")) {
						videoContentBean.setMobileUrlSd2(file.get("link").getAsString());
					}
				}
			}
		}catch(Exception e) {
			recording_upload_logger.error("Error occured while setting the mobile URL's Error Message:"+e.getMessage());
		}
		
		if(videoContentBean.getMobileUrlSd1() == null && videoContentBean.getMobileUrlSd2() != null) {
			String tmp_link = videoContentBean.getMobileUrlSd2();
			videoContentBean.setMobileUrlSd1(tmp_link.replace("profile_id=164", "profile_id=165"));
		}
		
		//get thumbnail from vimeo response
		JsonObject pictures = vimeoResponse.get("pictures").getAsJsonObject();
		for(JsonElement sizes : pictures.get("sizes").getAsJsonArray()) {
			JsonObject size = sizes.getAsJsonObject();
			if(size.get("width").getAsInt() == 1920) {
				videoContentBean.setThumbnailUrl(size.get("link").getAsString());
				break;
			}
			else if(size.get("width").getAsInt() == 1280) {
				videoContentBean.setThumbnailUrl(size.get("link").getAsString());
				break;
			}
		}
		String[] uri = vimeoResponse.get("uri").getAsString().split("/");
	
		videoContentBean.setVideoLink("https://player.vimeo.com/video/" + uri[uri.length - 1]);
		System.out.println("=============>>>>>>>>>>> inside 1");
		/*if(videoContentBean.getMobileUrlHd() == null) {
			//no video found with size 174,
			recording_status.updateVimeoStatus(recordingStatus.getMeetingId(), "Error in vimeo hdVideoUrl,getting value hdVideoUrl null");
			return false;
		}
		if(videoContentBean.getMobileUrlSd1() == null) {
			//no video found with size 165,
			recording_status.updateVimeoStatus(recordingStatus.getMeetingId(), "Error in vimeo SD1VideoUrl,getting value SD1VideoUrl null");
			return false;
		}
		if(videoContentBean.getMobileUrlSd2() == null) {
			//no video found with size 164,
			recording_status.updateVimeoStatus(recordingStatus.getMeetingId(), "Error in vimeo SD2VideoUrl,getting value SD2VideoUrl null");
			return false;
		}*/
		if(videoContentBean.getThumbnailUrl() == null) {
			//no thumbnail found
			videoContentBean.setThumbnailUrl("https://studentzone-ngasce.nmims.edu/acads/resources_2015/images/thumbnailLogo.png");
		}
		ContentDAO dao = (ContentDAO) act.getBean("contentDAO");
		
		recording_upload_logger.info("Adding session recording entry for :"+videoContentBean);
		
		/***
		 * duplicate session check
		 * */
		ArrayList<SessionBean> sessionList=dao.getSessionListForMeetingKey(recordingStatus.getMeetingId());
		recording_upload_logger.info("session list size is :"+sessionList.size());
		for(SessionBean sessionBean2:sessionList) {
			VideoContentAcadsBean videoContetBeanTemp=new VideoContentAcadsBean();
			
			BeanUtils.copyProperties(videoContentBean, videoContetBeanTemp);
			
			videoContetBeanTemp.setSessionId(Integer.parseInt(sessionBean2.getId()));
			videoContetBeanTemp.setSubject(sessionBean2.getSubject());
			videoContetBeanTemp.setFileName(sessionBean2.getSubject()+" "+sessionBean2.getSessionName());
			
		if("success".equalsIgnoreCase(dao.addIntoVideoContent(videoContetBeanTemp))) {
			recordingStatusDAO.successRecordingStatus(recordingStatus.getMeetingId());
			/*try {
			uploadZoomSubtitleOnVimeo(recordingStatus.getMeetingId(),recordingStatus.getVimeoId(),recordingStatus.getSessionId(),videoContentBean);		//for uploading subtitle of video
			}catch(Exception e) {
				  
			}*/
			try {
				this.sendRecordingUplodedNotification(videoContetBeanTemp);
			} catch (Exception e) {
				recording_upload_logger.error("Error while sending the recording uploaded notification Error Message :"+e.getMessage()+" for sessionId : "+sessionBean2.getId());
			}
			
			recording_upload_logger.debug("END");
			continue;
		}
		else {
			// failed to insert data in video_content
			recordingStatusDAO.updateVimeoStatus(recordingStatus.getMeetingId(), "Error while inserting data into video_content table");
			recording_upload_logger.error("Error while inserting data into video_content table for MeetingKey :"+recordingStatus.getMeetingId() +" and sessionId : "+sessionBean2.getId());
			recording_upload_logger.debug("END");
			continue;
		} 
	}
	return;
}

	

	private String getTranscriptDownloadURLByVimeoId(String vimeoId) throws IOException {

		VimeoManager vimeo = new VimeoManager(); 
		
		JsonObject vimeoResponse = vimeo.checkUploadVideoStatus(vimeoId);				// get vimeo video data  
		JsonObject metadataObject = vimeoResponse.get("metadata").getAsJsonObject();
		JsonObject connectionObject = metadataObject.get("connections").getAsJsonObject();
		JsonObject texttracksObject = connectionObject.get("texttracks").getAsJsonObject();		
		String uri = texttracksObject.get("uri").getAsString();							// got texttracks uri
		String getUploadLinkUrl = "https://api.vimeo.com"+uri;
		
		String transcriptLink = vimeo.getTranscriptLinkByUploadLinkUrl(getUploadLinkUrl);		
		
		// TODO Auto-generated method stub
		return transcriptLink;
	}

	private void uploadZoomSubtitleOnVimeo(String meetingId,String vimeoId,String sessionId,VideoContentAcadsBean videoContentBean) throws Exception {		


		VimeoManager vimeo = new VimeoManager(); 
		VideoContentDAO videoContentDAO = (VideoContentDAO) act.getBean("videoContentDAO");
		String vttName = "Video Subtitle";
		HttpDownloadUtilityHelper hdHelper = new HttpDownloadUtilityHelper();
		String transcriptContent = null;
		String transcriptDwnldUrl = getURLByMeetingId(meetingId).get("transcriptDwnldUrl") ;
		try {
			transcriptContent = hdHelper.getContentFromTranscriptUrl(transcriptDwnldUrl);// get content from transcript download url
		} catch (Exception e) {
			// TODO Auto-generated catch block
			  
		}		
		if(transcriptContent != null) {
				
				JsonObject vimeoResponse = vimeo.checkUploadVideoStatus(vimeoId);				// get vimeo video data  
				JsonObject metadataObject = vimeoResponse.get("metadata").getAsJsonObject();
				JsonObject connectionObject = metadataObject.get("connections").getAsJsonObject();
				JsonObject texttracksObject = connectionObject.get("texttracks").getAsJsonObject();		
				String uri = texttracksObject.get("uri").getAsString();							// got texttracks uri
				
				String getUploadLinkUrl = "https://api.vimeo.com"+uri;
				
				String uploadLink = vimeo.getUploadLinkfromUri(getUploadLinkUrl,vttName);		// get uploading link for uploading vtt file
				if(uploadLink != null) {
					vimeo.putSubtitleinVideo(uploadLink,transcriptContent);						//uploading subtitle
				}
				
		}
	}

	private HashMap<String, String> getURLByMeetingId(String meetingId) {		
		// TODO Auto-generated method stub
		ZoomManager zoom = new ZoomManager();
		TimeTableDAO recordingStatusDAO = (TimeTableDAO)act.getBean("timeTableDAO");
		ResponseEntity<String> zoomResponse = null;
		JsonObject jsonObj;
		String transcriptDwnldUrl = null;
		HashMap<String, String> urlLink = new HashMap<String, String>();
//		String vttName = null;
		try {
		zoomResponse = zoom.getZoomRecordingList(recordingStatusDAO,meetingId,true);	//get zoom webinar recording list.
		
		jsonObj = new JsonParser().parse(zoomResponse.getBody()).getAsJsonObject();
		System.out.println("---------->>>>>>> zoom meeting id : " + meetingId);
			if ("200".equalsIgnoreCase(zoomResponse.getStatusCode().toString())) {
			System.out.println("--------->>>>>> inside zoom success ");
//			vttName = jsonObj.get("topic").getAsString();
				
			for (JsonElement recordings : jsonObj.get("recording_files").getAsJsonArray()) {
				JsonObject recording = recordings.getAsJsonObject();
				System.out.println("---------->>>>>> type : " + recording.get("file_type").getAsString());
				if("TRANSCRIPT".equalsIgnoreCase(recording.get("file_type").getAsString())) {
				 transcriptDwnldUrl = recording.get("download_url").getAsString();									
				 urlLink.put("transcriptDwnldUrl", transcriptDwnldUrl);
				}
				if("M4A".equalsIgnoreCase(recording.get("file_type").getAsString())) {
					 urlLink.put("m4ADwnldUrl", recording.get("download_url").getAsString());
				}
			}
			}
			
		}catch(Exception e) {
			  
		}
		return urlLink;
	}


	private void sendRecordingUplodedNotification(VideoContentAcadsBean videoContentBean) {
		String title = "Session Recording Available";
		
		String body = videoContentBean.getDescription()+" recording is available for viewing.";
		
		JsonObject notification = new JsonObject();
		notification.addProperty("body", body);
		notification.addProperty("title", title);
		notification.addProperty("sessionId", videoContentBean.getSessionId().toString());
		notification.addProperty("subject", videoContentBean.getSubject());
		notification.addProperty("type", "recording");
		notification.addProperty("content_available", true);
		notification.addProperty("priority", "high");
		notification.addProperty("showWhenInForeground", true);
		
		List<StudentAcadsBean> studentListToSendNotification = new ArrayList<StudentAcadsBean>();
		List<StudentAcadsBean> studentListAfterJul = new ArrayList<StudentAcadsBean>();
		List<StudentAcadsBean> studentListBeforeJul = new ArrayList<StudentAcadsBean>();
				
		studentListAfterJul = notificationDAO.getRegisteredPGStudentForSessionAfterJul21(videoContentBean.getSessionId().toString());
		studentListBeforeJul = notificationDAO.getRegisteredPGStudentForSessionBeforeJul21(videoContentBean.getSessionId().toString());
		
		List<StudentAcadsBean> nonPGProgramStudentsList = notificationDAO.getRegisteredNonPGStudentForSession(videoContentBean.getSessionId().toString(), studentService.getListOfLiveSessionAccessMasterKeys(TIMEBOUND_PORTAL_LIST));
		List<StudentAcadsBean> mbawxProgarmStudentsList = notificationDAO.getRegisteredTimeBoundStudentForSession(videoContentBean.getSessionId().toString());
		
		studentListToSendNotification.addAll(studentListBeforeJul); 
		studentListToSendNotification.addAll(studentListAfterJul);
		studentListToSendNotification.addAll(nonPGProgramStudentsList);
		studentListToSendNotification.addAll(mbawxProgarmStudentsList);
		
		mobileNotificationHelper.sendSessionNotification(notification, studentListToSendNotification);
		
	}//sendRecordingUplodedNotification
	
	@Scheduled(fixedDelay=3*60*60*1000)
	public void updateBlankDownloadVideoUrlsJob() {
		if(!"tomcat16".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running updateBlankDownloadVideoUrlsJob: since this is not tomcat16. This is "+SERVER);
			return;
		}
		video_Downloads_Urls_logger.trace("Download URLs update batch job START");
		
		//Execute updateBlankDownloadVideoUrls
		String updatedStatus = this.updateBlankDownloadVideoUrls();
		
		video_Downloads_Urls_logger.info("updatedStatus::"+updatedStatus);
		video_Downloads_Urls_logger.trace("Download URLs update batch job END");
	}
	
	
	public String updateBlankDownloadVideoUrls() {
		List<VideoContentAcadsBean> sessionVideoDeatilsList = null;
		
		video_Downloads_Urls_logger.trace("Video download urls updation started.");
		
		int failedCount = 0;
		try {
			//Step 01] Fetch the blank download links session videos list
			sessionVideoDeatilsList = videoContentService.fetchBlankDownloadLinksVideoDeatils();
			
		}catch(Exception e) {
			video_Downloads_Urls_logger.error("Exception occure while fetching the blank download urls session videos. Error Messsage:"+e.getMessage());
			return "Falied: Error Message:"+sessionVideoDeatilsList;
		}
		
		try {
			//Count total no of session video has to be process.
			int totalSessionCount = sessionVideoDeatilsList.size();
		
			video_Downloads_Urls_logger.info(totalSessionCount+" sessions videos found to update urls.");
			
		}catch (NullPointerException npe) {
			video_Downloads_Urls_logger.error("Session videos not found having the blanks download urls. Error Message:"+npe.getMessage());
			return "Falied: Error Message: Session videos not fund to update.";
		}
		
		int count=0;
		//Step 02] Process updating the downloads urls for each video.
		for(VideoContentAcadsBean sessionVideoDetails : sessionVideoDeatilsList){
			VimeoManager vimeo = new VimeoManager();
			
			try {
				video_Downloads_Urls_logger.info("Started updating process for:"+(++count)+" video with VimeoId:"+sessionVideoDetails.getVimeoId() 
					+" and videoContentId:"+sessionVideoDetails.getId());
				
				// Step 02(a)] Get the session recording details uploaded on the Vimeo platform using vimeoId  
				JsonObject vimeoResponse = vimeo.checkUploadVideoStatus(sessionVideoDetails.getVimeoId());
				
				if(vimeoResponse == null) {
					video_Downloads_Urls_logger.error("Null Vimeo response received for VimeoId:"+sessionVideoDetails.getVimeoId() +" and videoContentId:"+sessionVideoDetails.getId());
					failedCount++;
					continue;	
				}else {
					//Step 02(b)] Read video url files from vimeo response and set to the bean
					//Here the link contains profile_id 174 means MobileHDUrl, 165 means MobileSD1rl and 164 means MobileSD2Url
					try {
						for(JsonElement files : vimeoResponse.get("files").getAsJsonArray() ) {
							JsonObject file = files.getAsJsonObject();
							
							if(file.get("link").getAsString().indexOf("profile_id=174") != -1) {
								sessionVideoDetails.setMobileUrlHd(file.get("link").getAsString());
							}
							else if(file.get("link").getAsString().indexOf("profile_id=165") != -1) {
								sessionVideoDetails.setMobileUrlSd1(file.get("link").getAsString());
							}
							else if(file.get("link").getAsString().indexOf("profile_id=164") != -1) {
								sessionVideoDetails.setMobileUrlSd2(file.get("link").getAsString());
							}
						}

						if(sessionVideoDetails.getMobileUrlSd1() == null && sessionVideoDetails.getMobileUrlSd2() != null) {
							String tmp_link = sessionVideoDetails.getMobileUrlSd2();
							sessionVideoDetails.setMobileUrlSd1(tmp_link.replace("profile_id=164", "profile_id=165"));
						}
					}catch(Exception e) {
						video_Downloads_Urls_logger.error("Error occured while setting the profile_id based mobile URL's Error Message:"+e.getMessage());
					}
					
					try {
						/*This check is temporary purpose, will make logic permanent which is inside this check after some days of observation.*/
						if(sessionVideoDetails.getMobileUrl() ==null && sessionVideoDetails.getMobileUrlSd1() == null && sessionVideoDetails.getMobileUrlSd2()==null) {
							video_Downloads_Urls_logger.info("Mobile all three url's found null so setting updated new url's to VideoContentBean.");
							
							for(JsonElement files : vimeoResponse.get("files").getAsJsonArray() ) {
								JsonObject file = files.getAsJsonObject();
								
								if((file.get("public_name").getAsString().equalsIgnoreCase("720p") && file.get("quality").getAsString().equalsIgnoreCase("hd"))
										|| file.get("link").getAsString().contains("720p")) {
									sessionVideoDetails.setMobileUrlHd(file.get("link").getAsString());
								}
								else if((file.get("public_name").getAsString().equalsIgnoreCase("540p") && file.get("quality").getAsString().equalsIgnoreCase("sd"))
										|| file.get("link").getAsString().contains("540p")) {
									sessionVideoDetails.setMobileUrlSd1(file.get("link").getAsString());
								}
								else if((file.get("public_name").getAsString().equalsIgnoreCase("360p") && file.get("quality").getAsString().equalsIgnoreCase("sd"))
										|| file.get("link").getAsString().contains("360p")) {
									sessionVideoDetails.setMobileUrlSd2(file.get("link").getAsString());
								}
							}
						}
					}catch(Exception e) {
						video_Downloads_Urls_logger.error("Error occured while setting the mobile URL's Error Message:"+e.getMessage());
					}
					
					
					try {
						//Step 02(c)] Update the entry in video content table.
						video_Downloads_Urls_logger.info("MobileUrlHd:"+sessionVideoDetails.getMobileUrlHd());
						video_Downloads_Urls_logger.info("MobileUrlSd1:"+sessionVideoDetails.getMobileUrlSd1());
						video_Downloads_Urls_logger.info("MobileUrlSd2:"+sessionVideoDetails.getMobileUrlSd2());
						
						String resultMessage = videoContentService.updateVideoDownloadUrls(sessionVideoDetails);
						video_Downloads_Urls_logger.info("resultMessage::"+resultMessage);
						
					}catch (Exception e) {
						failedCount++;
						video_Downloads_Urls_logger.error("Exception occured while updating the video content entry. Error Message:"+e.getMessage());
					}
				}//else
				
			} catch (IOException e) {
				video_Downloads_Urls_logger.error("Exception occured while getting the uploaded recording on vimeo. Error Message:"+e.getMessage());
				failedCount++;
				continue;
			}
		}//for()
		
		video_Downloads_Urls_logger.info((sessionVideoDeatilsList.size()-failedCount)+" successfully updated.");
		video_Downloads_Urls_logger.info(failedCount+" failed to updated.");
		video_Downloads_Urls_logger.trace("Video download urls updation end.");
		
		//return updation status.
		return (sessionVideoDeatilsList.size()-failedCount)+" successfully updated download links.";
	}
	
	@Scheduled(cron="0 0 3 * * ?")
	public void fetchZoomParticipantDetailsForSessions() {
		if(!"tomcat17".equalsIgnoreCase(SERVER) || !"PROD".equalsIgnoreCase(ENVIRONMENT)){
			System.out.println("Not running updateBlankDownloadVideoUrlsJob: since this is not tomcat16. This is "+SERVER);
			return;
		}
		System.out.println("fetchZoomParticipantDetailsForSessions called!");
		sessionAttendance.info("fetchZoomParticipantDetails BatchJob START>>>>");
		attendanceService.fetchSessionAttendnaceFromZoomAndUpdateToDBBatchJob();
		sessionAttendance.info("fetchZoomParticipantDetails BatchJob END>>>>");
	}

}
