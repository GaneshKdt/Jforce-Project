/**
 * 
 */
package com.nmims.beans;

import java.io.Serializable;

/**
 * @author vil_m
 *
 */
public class MettlScheduleAPIBean  implements Serializable  {
	public static final String SINGLE = "SINGLE";
	public static final String RANGE = "RANGE";
	
	public static final String ALWAYSON = "AlwaysOn";
	public static final String FIXED = "Fixed";
	public static final String OPENFORALL = "OpenForAll";
	public static final String BYINVITATION = "ByInvitation";
	public static final String EXACTTIME = "ExactTime";
	public static final String SLOTWISE = "Slotwise";
	public static final String OFF = "OFF";
	public static final String PHOTO = "PHOTO";
	public static final String VIDEO = "VIDEO";
	public static final String LOCATION_KOLKATA = "Asia/Kolkata";
	public static final String UTC_KOLKATA = "UTC+05:30";
	//Two constants for new API
	public static final String DURATION = "DURATION";
	public static final String SCHEDULED = "SCHEDULED";
	
	private String assessmentId;//Mandatory
	private String name;//Mandatory – Name of the Schedule
	private String scheduleType = ALWAYSON;//Mandatory – Shown as Access Time in your online account - ”AlwaysOn” OR ”Fixed”
	private ScheduleWindow scheduleWindow = null;//new ScheduleWindow();//null since ”AlwaysOn”
	private String testLinkType;//test link type : duration or scheduled
	private TestLinkSettings testLinkSettings = new TestLinkSettings();//newly added JSON body for SCHEDULED type tests
	private WebProctoring webProctoring = new WebProctoring();
	private VisualProctoring visualProctoring = new VisualProctoring();
	private Access access = new Access(); //new Access();
	private IPAccessRestriction ipAccessRestriction = new IPAccessRestriction();
	private TestGradeNotification testGradeNotification = new TestGradeNotification();
	private String sourceApp = "NGASCE";//Mandatory - Name of your application
	private String testStartNotificationUrl;
	private String testFinishNotificationUrl;
	private String testGradedNotificationUrl;
	private String testResumeEnabledForExpiredTestURL;

	public class WebProctoring {
		private boolean enabled = Boolean.TRUE;//default
		private int count = 0;//default
		private boolean showRemainingCounts = Boolean.FALSE;//default

		public WebProctoring() {
			super();
			// TODO Auto-generated constructor stub
		}

		public boolean getEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public int getCount() {
			return count;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public boolean isShowRemainingCounts() {
			return showRemainingCounts;
		}

		public void setShowRemainingCounts(boolean showRemainingCounts) {
			this.showRemainingCounts = showRemainingCounts;
		}
	}
	
	public class ScheduleWindow {
		private String fixedAccessOption; //= EXACTTIME;//"SlotWise";//default
		private String startsOnDate; //null;//"Thu, 27 Jun 2021";//default
		private String endsOnDate; //"Thu, 27 Jun 2021";//default
		private String startsOnTime; //"17:30:00";//default
		private String endsOnTime; //"18:00:00";//default
		private String locationtimeZone = LOCATION_KOLKATA;//default
		private String timeZone = UTC_KOLKATA;//default
		
		public ScheduleWindow() {
			super();
			// TODO Auto-generated constructor stub
		}
		public String getFixedAccessOption() {
			return fixedAccessOption;
		}
		public void setFixedAccessOption(String fixedAccessOption) {
			this.fixedAccessOption = fixedAccessOption;
		}
		public String getStartsOnDate() {
			return startsOnDate;
		}
		public void setStartsOnDate(String startsOnDate) {
			this.startsOnDate = startsOnDate;
		}
		public String getEndsOnDate() {
			return endsOnDate;
		}
		public void setEndsOnDate(String endsOnDate) {
			this.endsOnDate = endsOnDate;
		}
		public String getStartsOnTime() {
			return startsOnTime;
		}
		public void setStartsOnTime(String startsOnTime) {
			this.startsOnTime = startsOnTime;
		}
		public String getEndsOnTime() {
			return endsOnTime;
		}
		public void setEndsOnTime(String endsOnTime) {
			this.endsOnTime = endsOnTime;
		}
		public String getTimeZone() {
			return timeZone;
		}
		public void setTimeZone(String timeZone) {
			this.timeZone = timeZone;
		}
		public String getLocationtimeZone() {
			return locationtimeZone;
		}
		public void setLocationtimeZone(String locationtimeZone) {
			this.locationtimeZone = locationtimeZone;
		}
	}
	
	public class TestLinkSettings{
		
		private String testDate;
		private String timeZone = UTC_KOLKATA;
		private String locationTimeZone = LOCATION_KOLKATA;
		private String testStartTime;
		private String testEndTime;
		private String reportingStartTime;
		private String reportingFinishTime;
		
		
		public TestLinkSettings() {
			super();
			// TODO Auto-generated constructor stub
		}
		public String getTestDate() {
			return testDate;
		}
		public void setTestDate(String testDate) {
			this.testDate = testDate;
		}
		public String getTimeZone() {
			return timeZone;
		}
		public void setTimeZone(String timeZone) {
			this.timeZone = timeZone;
		}
		public String getLocationTimeZone() {
			return locationTimeZone;
		}
		public void setLocationTimeZone(String locationTimeZone) {
			this.locationTimeZone = locationTimeZone;
		}
		public String getTestStartTime() {
			return testStartTime;
		}
		public void setTestStartTime(String testStartTime) {
			this.testStartTime = testStartTime;
		}
		public String getTestEndTime() {
			return testEndTime;
		}
		public void setTestEndTime(String testEndTime) {
			this.testEndTime = testEndTime;
		}
		public String getReportingStartTime() {
			return reportingStartTime;
		}
		public void setReportingStartTime(String reportingStartTime) {
			this.reportingStartTime = reportingStartTime;
		}
		public String getReportingFinishTime() {
			return reportingFinishTime;
		}
		public void setReportingFinishTime(String reportingFinishTime) {
			this.reportingFinishTime = reportingFinishTime;
		}
		
	}
	
	public class Access {
		private String type = OPENFORALL;//default
		private Candidates[] candidates; // = null;//default
		private boolean sendEmail = Boolean.FALSE;//default
		
		public Access() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		public Access(String type, int size) {
			this.type = type;
			this.candidates = new Candidates[size];
		}
		
		public void addCandidateInfo(int index, String name, String email) {
			this.candidates[index] = new Candidates();
			this.candidates[index].setName(name);
			this.candidates[index].setEmail(email);
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
		
		public class Candidates {
			private String name;
			private String email;
			
			public Candidates() {
				super();
				// TODO Auto-generated constructor stub
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}

			public String getEmail() {
				return email;
			}

			public void setEmail(String email) {
				this.email = email;
			}
		}

		public Candidates[] getCandidates() {
			return candidates;
		}

		public void setCandidates(Candidates[] candidates) {
			this.candidates = candidates;
		}

		public boolean isSendEmail() {
			return sendEmail;
		}

		public void setSendEmail(boolean sendEmail) {
			this.sendEmail = sendEmail;
		}
	}

	public MettlScheduleAPIBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getAssessmentId() {
		return assessmentId;
	}

	public void setAssessmentId(String assessmentId) {
		this.assessmentId = assessmentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	public ScheduleWindow getScheduleWindow() {
		return scheduleWindow;
	}

	public void setScheduleWindow(ScheduleWindow scheduleWindow) {
		this.scheduleWindow = scheduleWindow;
	}

	public String getSourceApp() {
		return sourceApp;
	}

	public void setSourceApp(String sourceApp) {
		this.sourceApp = sourceApp;
	}

	public String getTestStartNotificationUrl() {
		return testStartNotificationUrl;
	}

	public void setTestStartNotificationUrl(String testStartNotificationUrl) {
		this.testStartNotificationUrl = testStartNotificationUrl;
	}

	public String getTestFinishNotificationUrl() {
		return testFinishNotificationUrl;
	}

	public void setTestFinishNotificationUrl(String testFinishNotificationUrl) {
		this.testFinishNotificationUrl = testFinishNotificationUrl;
	}

	public String getTestGradedNotificationUrl() {
		return testGradedNotificationUrl;
	}

	public void setTestGradedNotificationUrl(String testGradedNotificationUrl) {
		this.testGradedNotificationUrl = testGradedNotificationUrl;
	}

	public String getTestResumeEnabledForExpiredTestURL() {
		return testResumeEnabledForExpiredTestURL;
	}

	public void setTestResumeEnabledForExpiredTestURL(String testResumeEnabledForExpiredTestURL) {
		this.testResumeEnabledForExpiredTestURL = testResumeEnabledForExpiredTestURL;
	}

	public WebProctoring getWebProctoring() {
		return webProctoring;
	}

	public void setWebProctoring(WebProctoring webProctoring) {
		this.webProctoring = webProctoring;
	}

	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}
	
	
	public TestLinkSettings getTestLinkSettings() {
		return testLinkSettings;
	}

	public void setTestLinkSettings(TestLinkSettings testLinkSettings) {
		this.testLinkSettings = testLinkSettings;
	}


	public class VisualProctoring {
		private String mode = VIDEO; //Before Apr22 was PHOTO //OFF;//default
		private Options options = new Options();//default
		
		public VisualProctoring() {
			super();
		}

		public class Options {
			private boolean candidateScreenCapture = Boolean.TRUE;//default
			private boolean candidateAuthorization = Boolean.FALSE;//default
			private boolean isAudioProctoring = Boolean.FALSE;//default
			
			public Options() {
				super();
				// TODO Auto-generated constructor stub
			}

			public boolean isCandidateScreenCapture() {
				return candidateScreenCapture;
			}

			public void setCandidateScreenCapture(boolean candidateScreenCapture) {
				this.candidateScreenCapture = candidateScreenCapture;
			}

			public boolean isCandidateAuthorization() {
				return candidateAuthorization;
			}

			public void setCandidateAuthorization(boolean candidateAuthorization) {
				this.candidateAuthorization = candidateAuthorization;
			}

			public boolean isAudioProctoring() {
				return isAudioProctoring;
			}

			public void setAudioProctoring(boolean isAudioProctoring) {
				this.isAudioProctoring = isAudioProctoring;
			}
			
		}

		public String getMode() {
			return mode;
		}

		public void setMode(String mode) {
			this.mode = mode;
		}

		public Options getOptions() {
			return options;
		}

		public void setOptions(Options options) {
			this.options = options;
		}
	}
	
	public class IPAccessRestriction {
		private boolean enabled = Boolean.FALSE;//default
		private String type;// = SINGLE;//default
		private String ip;//default
		private String ranges;//default
		
		public IPAccessRestriction() {
			super();
			// TODO Auto-generated constructor stub
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}

		public String getRanges() {
			return ranges;
		}

		public void setRanges(String ranges) {
			this.ranges = ranges;
		}
		
	}
	
	public class TestGradeNotification {
		private boolean enabled = Boolean.FALSE;//default //Before Apr22 exam it was TRUE
		private String[] recipients; //= {"jforcesolutions@gmail.com"};//default
		
		public TestGradeNotification() {
			super();
			// TODO Auto-generated constructor stub
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String[] getRecipients() {
			return recipients;
		}

		public void setRecipients(String[] recipients) {
			this.recipients = recipients;
		}
	}
	
	public VisualProctoring getVisualProctoring() {
		return visualProctoring;
	}

	public void setVisualProctoring(VisualProctoring visualProctoring) {
		this.visualProctoring = visualProctoring;
	}

	public IPAccessRestriction getIpAccessRestriction() {
		return ipAccessRestriction;
	}

	public void setIpAccessRestriction(IPAccessRestriction ipAccessRestriction) {
		this.ipAccessRestriction = ipAccessRestriction;
	}

	public TestGradeNotification getTestGradeNotification() {
		return testGradeNotification;
	}

	public void setTestGradeNotification(TestGradeNotification testGradeNotification) {
		this.testGradeNotification = testGradeNotification;
	}

	public String getTestLinkType() {
		return testLinkType;
	}

	public void setTestLinkType(String testLinkType) {
		this.testLinkType = testLinkType;
	}
	
}