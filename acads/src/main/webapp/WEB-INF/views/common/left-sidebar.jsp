<%-- <%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.StudentBean"%>
<%@page import="com.nmims.beans.AnnouncementBean"%>
<%@page import="java.util.ArrayList"%>

<%
	 StudentBean studentInSideBar = (StudentBean) session .getAttribute("student");

	/*StudentBean studentInSideBar = (StudentBean)application.getAttribute("student");*/
	String activeMenu = request.getParameter("activeMenu");
	String dashboardMenuClass = "";
	String academicCalendarMenuClass = "";
	String myCoursesMenuClass = "";
	String examMenuClass = "";
	String examResultsMenuClass = "";
	String examAssignmentResultsMenuClass = "";
	String examMarksHistoryMenuClass = "";
	String examTimetableMenuClass = "";
	String examHallTicketMenuClass = "";
	String examMarksheetMenuClass = "";
	String examAssignmentsMenuClass = "";
	String examRegistrationMenuClass = "";
	String examResitRegistrationMenuClass = "";
	String examProjetSubmissionMenuClass = "";
	//String examExamFeeReceiptMenuClass = "";
	String studentSupportMenuClass = "";
	String studentSupportServiceRequestMenuClass = "";
	String studentSupportOverviewMenuClass = "";
	String studentSupportFAQMenuClass = "";
	String studentSupportContactMenuClass = "";
	String studentSupportFeedback = "";
	String quickLinksMenuClass = "";
	String quickLinksPCPFeeReceiptMenuClass = "";
	String quickLinksChangePasswordMenuClass = "";
	String quickLinksPCPRegistrationMenuClass = "";
	String quickLinksExamFeeReceiptMenuClass = "";
	String quickLinksRegistrationFeeReceipt = "";
	String quickLinksDispatchOrder = "";
	String quickLinksMyDocuments = "";
	String quickLinkAnnouncementArchival = "";
	String validityExpired = (String) session
			.getAttribute("validityExpired");
	String earlyAccess = (String) session.getAttribute("earlyAccess");

	String walletMenuClass = "";

	if (activeMenu != null) {
		if ("Dashboard".equals(activeMenu)) {
			dashboardMenuClass = "active";
		} else if ("Academic Calendar".equals(activeMenu)) {
			academicCalendarMenuClass = "active";
		} else if ("My Courses".equals(activeMenu)) {
			myCoursesMenuClass = "active";
		} else if ("My Wallet".equals(activeMenu)) {
			walletMenuClass = "active";
		} else if ("Exam Results".equals(activeMenu)) {
			examMenuClass = "active";
			examResultsMenuClass = "active";
		} else if ("Assignment Marks".equals(activeMenu)) {
			examMenuClass = "active";
			examAssignmentResultsMenuClass = "active";
		} else if ("Marks History".equals(activeMenu)) {
			examMenuClass = "active";
			examMarksHistoryMenuClass = "active";
		} else if ("Exam Time Table".equals(activeMenu)) {
			examMenuClass = "active";
			examTimetableMenuClass = "active";
		} else if ("Hall Ticket".equals(activeMenu)) {
			examMenuClass = "active";
			examHallTicketMenuClass = "active";
		} else if ("Marksheet".equals(activeMenu)) {
			examMenuClass = "active";
			examMarksheetMenuClass = "active";
		} else if ("Assignment".equals(activeMenu)) {
			examMenuClass = "active";
			examAssignmentsMenuClass = "active";
		} else if ("Exam Registration".equals(activeMenu)) {
			examMenuClass = "active";
			examRegistrationMenuClass = "active";
		} else if ("Resit-Exam Registration".equals(activeMenu)) {
			examMenuClass = "active";
			examResitRegistrationMenuClass = "active";
		} else if ("Project Submission".equals(activeMenu)) {
			examMenuClass = "active";
			examProjetSubmissionMenuClass = "active";
		} else if ("Service Request".equals(activeMenu)) {
			studentSupportMenuClass = "active";
			studentSupportServiceRequestMenuClass = "active";
		} else if ("Overview".equals(activeMenu)) {
			studentSupportMenuClass = "active";
			studentSupportOverviewMenuClass = "active";
		} else if ("FAQs".equals(activeMenu)) {
			studentSupportMenuClass = "active";
			studentSupportFAQMenuClass = "active";
		} else if ("Contact Us".equals(activeMenu)) {
			studentSupportMenuClass = "active";
			studentSupportContactMenuClass = "active";
		} else if ("FeedBack".equals(activeMenu)) {
			studentSupportMenuClass = "active";
			studentSupportFeedback = "active";
		} else if ("PCP/VC Registration Receipt".equals(activeMenu)) {
			quickLinksMenuClass = "active";
			quickLinksPCPFeeReceiptMenuClass = "active";
		} else if ("Change Password".equals(activeMenu)) {
			quickLinksMenuClass = "active";
			quickLinksChangePasswordMenuClass = "active";
		} else if ("PCP/VC Registration".equals(activeMenu)) {
			quickLinksMenuClass = "active";
			quickLinksPCPRegistrationMenuClass = "active";
		} else if ("Exam Fee Receipt".equals(activeMenu)) {
			quickLinksMenuClass = "active";
			quickLinksExamFeeReceiptMenuClass = "active";
		} else if ("Registration Fee Receipt".equals(activeMenu)) {
			quickLinksMenuClass = "active";
			quickLinksRegistrationFeeReceipt = "active";
		} else if ("Dispatch Orders".equals(activeMenu)) {
			quickLinksMenuClass = "active";
			quickLinksDispatchOrder = "active";
		} else if ("My Documents".equals(activeMenu)) {
			quickLinksMenuClass = "active";
			quickLinksMyDocuments = "active";
		} else if ("Announcement Archival".equals(activeMenu)) {
			quickLinksMenuClass = "active";
			quickLinkAnnouncementArchival = "active";
		}
	}

	ArrayList<AnnouncementBean> announcementsInSideBar = (ArrayList<AnnouncementBean>) session
			.getAttribute("announcements");
	int noOfAnnouncemntsInSidebar = announcementsInSideBar != null ? announcementsInSideBar
			.size() : 0;
	SimpleDateFormat formatterSidebar = new SimpleDateFormat(
			"yyyy-MM-dd");
	SimpleDateFormat dateFormatterSidebar = new SimpleDateFormat(
			"dd-MMM-yyyy");
%>

<div class="sz-main-navigation">
	<div class="mobile-logo visible-xs">
		<a href="#"><img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/images/logo.png"
			class="img-responsive" alt="" /></a>
	</div>
	<ul class="sz-nav">
		<%
			if ("No".equals(validityExpired)) {
		%>
		<li id="toggle-nav"><a href="#"><span
				class="icon-arrow-right"></span></a></li>
		<li class="<%=dashboardMenuClass%>"><a href="/studentportal/home"><span
				class="icon-dashboard"></span>
				<p>Dashboard</p> </a></li>
		<li class="<%=academicCalendarMenuClass%>"><a
			href="/acads/viewStudentTimeTable"><span
				class="icon-academic-calendar"></span>
				<p>Academic Calendar</p> </a></li>
		<li class="<%=myCoursesMenuClass%>"><a
			href="/studentportal/viewCourseHomePage"><span
				class="icon-my-courses"></span>
				<p>My Courses</p> </a></li>
				<li class=""> 
          	<a href="/acads/videosHome?pageNo=1&academicCycle=All">
          		<span>
          			<i class="fa fa-play-circle-o" aria-hidden="true"></i>
          		</span>
            <p>Session Videos</p>
            </a>
          </li> 
				
		<%
			//if("No".equalsIgnoreCase(earlyAccess)){
		%>

		<li class="has-sub-menu <%=examMenuClass%>"><a
			href="/exam/student/getMostRecentResults"><span class="icon-exams"></span>
				<p>Exams</p> </a>
			<ul class="sz-sub-menu">
				<li class="<%=examResultsMenuClass%>"><a
					href="/exam/student/viewNotice">
						<p>Exam Results</p>
				</a></li>
				<li class="<%=examAssignmentResultsMenuClass%>"><a
					href="/exam/student/getMostRecentAssignmentResults">
						<p>Assignment Marks</p>
				</a></li>
				<li class="<%=examMarksHistoryMenuClass%>">
			  	<a href="/exam/getAStudentMarks">
					<p>Marks History</p>
				</a>
			  </li>
				<li class="<%=examTimetableMenuClass%>"><a
					href="/exam/studentTimeTable">
						<p>Exam Calendar</p>
				</a></li>
				<li class="<%=examHallTicketMenuClass%>"><a
					href="/exam/downloadHallTicket">
						<p>Hall Ticket</p>
				</a></li>
				<li class="<%=examMarksheetMenuClass%>"><a
					href="/exam/studentSelfMarksheetForm">
						<p>Marksheet</p>
				</a></li>
				<li class="<%=examAssignmentsMenuClass%>"><a
					href="/exam/viewAssignmentsForm">
						<p>Assignment</p>
				</a></li>

				<li><a href="/exam/viewModelQuestionForm">
						<p>Demo Exam</p>
				</a></li>
				<%
					if ("No".equals(earlyAccess)) {
				%>
				<li class="<%=examRegistrationMenuClass%>"><a
					href="/exam/selectSubjectsForm">
						<p>Exam Registration</p>
				</a></li>
				<%
					}
				%>
				<%
					//if("Jul2014".equalsIgnoreCase(studentInSideBar.getPrgmStructApplicable()) || "Jul2013".equalsIgnoreCase(studentInSideBar.getPrgmStructApplicable())){
				%>
				<!-- <li class="<%=examResitRegistrationMenuClass%>" >
			  	<a href="/exam/selectResitSubjectsForm"><p>Resit-Exam Registration</p></a>
					
				
			</li> -->
				<%
					//}
				%>
				<%
					if (studentInSideBar.getProgram().startsWith("PG")) {
				%>
				<li class="<%=examProjetSubmissionMenuClass%>"><a
					href="/exam/viewProject?subject=Project">
						<p>Project Submission</p>
				</a></li>
				<%
					}
				%>

			</ul></li>
		<%
			}
		%>
		<%
			//}
		%>
		<li class="has-sub-menu <%=studentSupportMenuClass%>"><a
			href="/studentportal/supportOverview"><span
				class="icon-student-support"></span>
				<p>Student Support</p> </a>
			<ul class="sz-sub-menu">

				<li class="<%=studentSupportOverviewMenuClass%>"><a
					href="/studentportal/supportOverview">
						<p>Overview</p>
				</a></li>
				<li class="<%=studentSupportFAQMenuClass%>"><a
					href="/studentportal/faq">
						<p>FAQs</p>
				</a></li>
				<li class="<%=studentSupportServiceRequestMenuClass%>"><a
					href="/studentportal/selectSRForm">
						<p>Service Request</p>
				</a></li>
				<li class="<%=studentSupportContactMenuClass%>"><a
					href="/studentportal/contactUs">
						<p>Contact Us</p>
				</a></li>
				<li class="<%=studentSupportFeedback%>"><a
					href="/studentportal/feedback">
						<p>Feedback & Suggestions</p>
				</a></li>
			</ul></li>
		<%
			if ("No".equals(validityExpired)) {
		%>
		<li class="has-sub-menu <%=quickLinksMenuClass%>"><a href="#"><span
				class="icon-quick-links"></span>
				<p>Quick Links</p> </a>
			<ul class="sz-sub-menu">
				<li><a href="/studentportal/gotoEZProxy" target="_blank">
						<p>Digital Library</p>
				</a></li>
				<li class="<%=quickLinksPCPRegistrationMenuClass%>"><a
					href="/acads/selectPCPSubjectsForm">
						<p>PCP Registration</p>
				</a></li>
				<%
					//if("No".equalsIgnoreCase(earlyAccess)){
				%>
				<li><a href="/exam/selectSubjectsForm">
						<p>Exam Registration</p>
				</a></li>
				<%
					//}
				%>
				<li><a href="/exam/downloadHallTicket">
						<p>Hall Ticket</p>
				</a></li>
				<li><a href="/exam/studentSelfMarksheetForm">
						<p>Marksheet</p>
				</a></li>
				<li class="<%=quickLinksExamFeeReceiptMenuClass%>"><a
					href="/exam/printBookingStatus">
						<p>Exam Fee Receipt</p>
				</a></li>
				<li class="<%=quickLinksPCPFeeReceiptMenuClass%>"><a
					href="/acads/downloadPCPRegistrationReceipt">
						<p>PCP Registration Receipt</p>
				</a></li>
				<li class="<%=quickLinksRegistrationFeeReceipt%>"><a
					href="/studentportal/viewFeeReceipt">
						<p>Fee Receipt</p>
				</a></li>
				<li class="<%=quickLinksDispatchOrder%>"><a
					href="/studentportal/getDispatches">
						<p>Study Kit Dispatch Orders</p>
				</a></li>
				<li class="<%=quickLinksMyDocuments%>"><a
					href="/exam/myDocuments">
						<p>My Documents</p>
				</a></li>
				<li class="<%=quickLinkAnnouncementArchival%>"><a
					href="/studentportal/getAllStudentAnnouncements">
						<p>Announcement Archival</p>
				</a></li>
				<li class="<%=quickLinksChangePasswordMenuClass%>"><a
					href="/studentportal/changePassword">
						<p>Change Password</p>
				</a></li>

				<li class="<%=quickLinksChangePasswordMenuClass%>"><a
					href="/studentportal/studentAttendence">
						<p>Attendance</p>
				</a></li>

				<li class="<%=quickLinksChangePasswordMenuClass%>"><a
					href="/acads/keyEvents">
						<p>Key Events</p>
				</a></li>

				<!-- <li><a href="http://www.thepracticetest.in/NMIMS/" target="_blank">
                <p>Demo Exam</p>
                </a></li> -->

				<li><a href="/studentportal/reRegistrationPage" target="_blank">
						<p>Re-Registration</p>
				</a></li>
			</ul></li>

		<li class="has-sub-menu <%=walletMenuClass%>"> <a href="/studentportal/myWalletForm"><span class="glyphicon glyphicon-credit-card"></span>
            <p>My Wallet</p>
            </a>
          </li>
          <!-- 30jan 
            -->

		<%
			}
		%>
	</ul>


</div>


<!-- Mobile notification contents -->

<%
	if (noOfAnnouncemntsInSidebar > 0) {
%>
<div class="mobile-notification-wrapper visible-xs"
	id="mobile-announcements">
	<div class="mobile-notification-header">
		<a href="#" class="hide-link">Hide <span
			class="glyphicon glyphicon-menu-right"></span></a>
		<div class="clearfix"></div>
		<h4>Announcements</h4>
		<!-- <div class="btn-group" role="group">
			  <a href="#" class="btn btn-default active">Show </a>
			  <a href="#" class="btn btn-default">All Announcements <span class="glyphicon glyphicon-menu-down"></span></a>
		  </div> -->
	</div>
	<ul class="announcement-list">

		<%
			int count = 0;
				for (AnnouncementBean announcement : announcementsInSideBar) {
					count++;
					Date formattedDate = formatterSidebar.parse(announcement
							.getStartDate());
					String formattedDateString = dateFormatterSidebar
							.format(formattedDate);
		%>

		<li><a href="#" data-toggle="modal" data-dismiss="modal"
			data-target="#announcementModal<%=count%>">
				<h3><%=announcement.getSubject()%></h3>
				<p><%=announcement.getDescription()%></p>
				<h4><%=formattedDateString%>
					by <span><%=announcement.getCategory()%></span>
				</h4>
		</a></li>

		<%
			}
		%>

	</ul>
</div>
<%
	}
%>  --%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.nmims.beans.LeadBean"%>
<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.StudentBean"%>
<%@page import="com.nmims.beans.AnnouncementBean"%>
<%@page import="java.util.ArrayList"%>

<%

String hasAssignmentFlag = (String) request.getSession().getAttribute("hasAssignmentFlag");
String hasTestFlag = (String) request.getSession().getAttribute("hasTestFlag");
BaseController lsCon = new BaseController();

StudentBean studentInSideBar = (StudentBean)session.getAttribute("student");
String activeMenu = request.getParameter("activeMenu"); 
String dashboardMenuClass = "";
String myGetCertifiedMenuClass = "";
String academicCalendarMenuClass = "";
String myCoursesMenuClass = "";
String bookmarksMenuClass = "";
String examMenuClass = "";
String examResultsMenuClass = "";
String examAssignmentResultsMenuClass = "";
String examMarksHistoryMenuClass = "";
String examTimetableMenuClass = "";
String examHallTicketMenuClass = "";
String examMarksheetMenuClass = "";
String examTranscriptMenuClass = "";
String examAssignmentsMenuClass = "";
String examRegistrationMenuClass = "";
String examResitRegistrationMenuClass = "";
String examProjetSubmissionMenuClass = "";
//String examExamFeeReceiptMenuClass = "";
String studentSupportMenuClass = "";
String studentSupportServiceRequestMenuClass = "";
String studentSupportOverviewMenuClass = "";
String studentSupportFAQMenuClass = "";
String studentSupportContactMenuClass = "";
String studentSupportFeedback = "";
String quickLinksMenuClass = "";
String quickLinksPCPFeeReceiptMenuClass = "";
String quickLinksChangePasswordMenuClass = "";
String quickLinksPCPRegistrationMenuClass = "";
String quickLinksUFMDashboardMenuClass = "";
String quickLinksExamFeeReceiptMenuClass = "";
String quickLinksRegistrationFeeReceipt = "";
String quickLinksDispatchOrder ="";
String quickLinksMyDocuments = "";
String quickLinkAnnouncementArchival ="";
String lastCycle="";
String validityExpired = (String)session.getAttribute("validityExpired");
String earlyAccess = (String)session.getAttribute("earlyAccess");

//boolean ifReRegistrationActive = (boolean)session.getAttribute("ifReRegistrationActive");
String walletMenuClass = "";
String isLead = (String)request.getSession().getAttribute("isLoginAsLead");
String diableLink = "", color = "", notify="", lastCycleContentLink = "/studentportal/student/lastCycleContent";
if(isLead.equals("true")){
	diableLink = "disabled";
	color = "color: #b9b9b9;";
	notify = "notifyLead()";
	lastCycleContentLink = "#";
}
 

if(activeMenu != null){
	if("Dashboard".equals(activeMenu)){
		dashboardMenuClass = "active";
	}else if("Get Certified".equals(activeMenu)){
		myGetCertifiedMenuClass = "active";
	}else if("Academic Calendar".equals(activeMenu)){
		academicCalendarMenuClass = "active";
	}else if("My Courses".equals(activeMenu)){
		myCoursesMenuClass = "active";
	}else if("Bookmarks".equals(activeMenu)){
		bookmarksMenuClass = "active";
	}else if ("My Wallet".equals(activeMenu)) {
		walletMenuClass = "active";
	} else if("Exam Results".equals(activeMenu)){
		examMenuClass = "active";
		examResultsMenuClass = "active";
	}else if("Assignment Marks".equals(activeMenu)){
		examMenuClass = "active";
		examAssignmentResultsMenuClass = "active";
	}else if("Marks History".equals(activeMenu)){
		examMenuClass = "active";
		examMarksHistoryMenuClass = "active";
	}else if("Exam Time Table".equals(activeMenu)){
		examMenuClass = "active";
		examTimetableMenuClass = "active";
	}else if("Hall Ticket".equals(activeMenu)){
		examMenuClass = "active";
		examHallTicketMenuClass = "active";
	}else if("Marksheet".equals(activeMenu)){
		examMenuClass = "active";
		examMarksheetMenuClass = "active";
	}else if("Transcript".equals(activeMenu)){
		examMenuClass = "active";
		examTranscriptMenuClass = "active";
	}else if("Assignment".equals(activeMenu)){
		examMenuClass = "active";
		examAssignmentsMenuClass = "active";
	}else if("Exam Registration".equals(activeMenu)){
		examMenuClass = "active";
		examRegistrationMenuClass = "active";
	}else if("Resit-Exam Registration".equals(activeMenu)){
		examMenuClass = "active";
		examResitRegistrationMenuClass = "active";
	}else if("Project Submission".equals(activeMenu)){
		examMenuClass = "active";
		examProjetSubmissionMenuClass = "active";
	}else if("Service Request".equals(activeMenu)){
		studentSupportMenuClass = "active";
		studentSupportServiceRequestMenuClass = "active";
	}else if("Overview".equals(activeMenu)){
		studentSupportMenuClass = "active";
		studentSupportOverviewMenuClass = "active";
	}else if("FAQs".equals(activeMenu)){
		studentSupportMenuClass = "active";
		studentSupportFAQMenuClass = "active";
	}else if("Contact Us".equals(activeMenu)){
		studentSupportMenuClass = "active";
		studentSupportContactMenuClass = "active";
	}else if("FeedBack".equals(activeMenu)){
		studentSupportMenuClass = "active";
		studentSupportFeedback = "active";
	}else if("PCP/VC Registration Receipt".equals(activeMenu)){
		quickLinksMenuClass = "active";
		quickLinksPCPFeeReceiptMenuClass = "active";
	}else if("Change Password".equals(activeMenu)){
		quickLinksMenuClass = "active";
		quickLinksChangePasswordMenuClass = "active";
	}else if("PCP/VC Registration".equals(activeMenu)){
		quickLinksMenuClass = "active";
		quickLinksPCPRegistrationMenuClass = "active";
	}else if("Exam Fee Receipt".equals(activeMenu)){
		quickLinksMenuClass = "active";
		quickLinksExamFeeReceiptMenuClass = "active";
	}else if("Registration Fee Receipt".equals(activeMenu)){
		quickLinksMenuClass = "active";
		quickLinksRegistrationFeeReceipt = "active";
	}else if("Dispatch Orders".equals(activeMenu)){
		quickLinksMenuClass = "active";
		quickLinksDispatchOrder ="active";
	}else if("My Documents".equals(activeMenu)){
		quickLinksMenuClass = "active";
		quickLinksMyDocuments = "active";
	}else if("lastCycle".equals(activeMenu)){
		lastCycle = "active";
	}else if("UFMDashboard".equals(activeMenu)){
		quickLinksMenuClass = "active";
		quickLinksUFMDashboardMenuClass = "active";
	}
	
	else if("Announcement Archival".equals(activeMenu))
	{
		quickLinksMenuClass = "active";
		quickLinkAnnouncementArchival ="active";
	}
}
ArrayList<AnnouncementBean> announcementsInSideBar = (ArrayList<AnnouncementBean>)session.getAttribute("announcements");
int noOfAnnouncemntsInSidebar = announcementsInSideBar != null ? announcementsInSideBar.size() : 0;
SimpleDateFormat formatterSidebar = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat dateFormatterSidebar = new SimpleDateFormat("dd-MMM-yyyy");



%>

<style>
	a.disabled {
	  	pointer-events: none;
	  	cursor: default;
	}
</style>
<script>
	function notifyLead() {
	  alert("Please enrole for the complete course...");
	}
</script>
<div class="sz-main-navigation">
        <div class="mobile-logo visible-xs"><a href="#"><img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/images/logo.png" class="img-responsive" alt=""/></a></div>
        <ul class="sz-nav">
         <%if("No".equals(validityExpired)) {%>
          <li id="toggle-nav"><a href="#"><span class="icon-arrow-right"></span></a></li>
          <li class="<%=dashboardMenuClass%>"><a href="/studentportal/home"><span class="icon-dashboard"></span>
            <p>Dashboard</p>
            </a></li>
            
            <%if("true".equals(isLead)){%>
           	<li class="<%=myGetCertifiedMenuClass%>"> 
           		<a href="/studentportal/getFreeCoursesList">
           			<span class="material-icons"> card_membership </span>
					<p>Get Certified</p>
				</a>
			</li>
           <% }%>
            
            
          <li class="<%=academicCalendarMenuClass%>"> <a href="/acads/student/viewStudentTimeTable"><span class="icon-academic-calendar"></span>
            <p>Academic Calendar</p>
            </a></li>
          <li class="<%=myCoursesMenuClass%>"> <a href="/studentportal/student/viewCourseHomePage"><span class="icon-my-courses"></span>
            <p>My Courses</p>
            </a></li>

           <%if("PD - DM".equals(studentInSideBar.getProgram())){%>
          
              <li class=""> 
          	<a href="/acads/viewELearnResources">
          		<span>
          			<i class="fa fa-graduation-cap" aria-hidden="true"></i>
          		</span>
            <p>E-Learn</p>
            </a> 
          </li>
          
         <%}%>
          	
         <li class=""> 
          	<a href="/acads/student/videosHome?pageNo=1&academicCycle=${currentSessionCycle}">
          		<span>
          			<i class="fa fa-play-circle-o" aria-hidden="true"></i>
          		</span>
            <p>Session Videos</p>
            </a>
          </li>
          
          <li class="<%=bookmarksMenuClass%>"><a
			href="/studentportal/student/bookmarks"> <span> <i
					class="fa fa-bookmark" aria-hidden="true"></i>
			</span>
				<p>Bookmarks</p>
		  </a></li>
            
            <%//if("No".equalsIgnoreCase(earlyAccess)){ %>
            
          <!-- _______________________________________________________ Exam _____________________________________________________ -->
            
          <li class="has-sub-menu <%=examMenuClass%>"> 
		  	 <%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
			     <a class="<%= diableLink %>" href="/exam/student/getMostRecentResultsSaS"><span class="icon-exams"></span>
					<p>Exam</p>
				 </a>
			 <%} else {%>
			    <a class="<%= diableLink %> " href="/exam/student/getMostRecentResults"><span class="icon-exams"></span>
					<p>Exams</p> 
				</a> 
			<%}%>
			<ul class="sz-sub-menu">
				<% if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()))){ %>
					<li class="<%=examAssignmentsMenuClass%>" >
				  		<a href="/exam/student/viewAssignmentsForm">
							<p> Assignment </p>
						</a>
					</li>
				 <%-- <li class="<%=examAssignmentsMenuClass%>" >
				  	<a href="/exam/viewTestsForStudent">
						<p>Internal Assignment (MCQ&rsquo;s)</p>
					</a>
				</li> --%> 
			   	<% } %>
				
				<%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()))){%>
			  		<li class="<%=examAssignmentResultsMenuClass%>">
			  			<a href="/exam/student/getMostRecentAssignmentResults">
							<p>Assignment Marks</p>
						</a>
			  		</li>
				<%} %>
			
			
			  <li class="<%=examResultsMenuClass%>">
			 <%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
			     <a href="/exam/student/getMostRecentResultsSaS">
					<p>Exam Results</p>
				 </a>
			 <%} else {%>
			          <a  href="/exam/student/viewNotice">
					     <p>Exam Results</p>
				      </a>
			 <%} %>
			  </li>
			
			<% if ("BBA".equals(studentInSideBar.getProgram()) || "B.Com".equals(studentInSideBar.getProgram())) {
			%>
			<li><a href="/exam/stepRGResults">
					<p>Employability Skills Results</p>
			</a>
			</li>
			<% }
			%>  
			<li>	
			  <a href="/exam/viewModelQuestionForm" >
                    <p>Demo Exam</p>
              </a>
            </li>
			
			  
			 <%--  <li class="<%=examMarksHistoryMenuClass%>">
			  	<a href="/exam/getAStudentMarks">
					<p>Marks History</p>
				</a>
			  </li> --%>
			<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
			<li class="<%=examTimetableMenuClass%>">
			  	<a class="<%= diableLink %>" href="/exam/executiveStudentTimeTable">
					<p style="<%= color %>">Exam Calendar</p>
				</a>
			</li>
			<%} else {%>
			<li class="<%=examTimetableMenuClass%>">
			  	<a class="<%= diableLink %>" href="/exam/studentTimeTable">
					<p style="<%= color %>">Exam Calendar</p>
				</a>
			</li>
			<%} %>
			
			<li class="<%=examHallTicketMenuClass%>">
			<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
			
			  	<a class="<%= diableLink %>" href="/exam/downloadExecutiveHallTicket">
					<p style="<%= color %>">Hall Ticket</p>
				</a>
			<%} else {%>
			  	<!-- <a href="/exam/downloadHallTicket"> -->
			  	<a class="<%= diableLink %>" href="/exam/student/previewHallTicket">
					<p style="<%= color %>">Hall Ticket</p>
				</a>
			<%} %>
			</li>
			
			<li  class="<%=examMarksheetMenuClass%>">
		    <%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
			
			  	<a class="<%= diableLink %>" href="/exam/studentSelfSASMarksheetForm">
					<p style="<%= color %>">Marksheet</p>
				</a>
			<%} else {%>
				<a class="<%= diableLink %>" href="/exam/studentSelfMarksheetForm">
					<p style="<%= color %>">Marksheet</p>
				</a>
			<%} %>
			</li>
			
			<%if(!"MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
				<li  class="<%=examTranscriptMenuClass%>">
					<a class="<%= diableLink %>" href="/exam/student/generateStudentSelfTranscriptForm">
						<p style="<%= color %>">Transcript</p>
					</a>
				</li>
			<%} %>
			
		    
			
		<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
			
			<%if("No".equals(earlyAccess)) {%>
			<li class="<%=examRegistrationMenuClass%>" >
			  	<a class="<%= diableLink %>" href="/exam/executiveRegistrationForm">
					<p style="<%= color %>">Exam Registration</p>
				</a>
			</li>
			<%} %>
			<%} else{%>
			
			<%if("No".equals(earlyAccess)) {%>
			<li class="<%=examRegistrationMenuClass%>" >
			  	<a class="<%= diableLink %>" href="/exam/selectSubjectsForm">
					<p style="<%= color %>">Exam Registration</p>
				</a>
			</li>
			<%} %>
			<%} %>
			<%//if("Jul2014".equalsIgnoreCase(studentInSideBar.getPrgmStructApplicable()) || "Jul2013".equalsIgnoreCase(studentInSideBar.getPrgmStructApplicable())){ %>
			<!-- <li class="<%=examResitRegistrationMenuClass%>" >
			  	<a href="/exam/selectResitSubjectsForm"><p>Resit-Exam Registration</p></a>
					
				
			</li> -->
			<%//} %>
			
		<%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()))){%>
			
			<%if((studentInSideBar.getProgram().startsWith("PG") || studentInSideBar.getConsumerType().equalsIgnoreCase("Diageo"))){ %>
				<li class="<%=examProjetSubmissionMenuClass%>">
				  	<a class="<%= diableLink %>" href="/exam/student/viewProject?subject=Project">
						<p style="<%= color %>">Project Submission</p>
					</a>
				</li>  
			<%} else if((studentInSideBar.getProgram().equalsIgnoreCase("PD - WM") && studentInSideBar.getConsumerType().equalsIgnoreCase("Retail"))) {%>
			
				<li class="<%=examProjetSubmissionMenuClass%>">
				  	<a class="<%= diableLink %>" href="/exam/student/viewProject?subject=Module 4 - Project">
						<p style="<%= color %>">Project Submission</p>
					</a>
				</li>  
			<% } %>
		<%}%>
			
		<%if("EPBM".equals(studentInSideBar.getProgram())){ %>
			<li>
			  	<a href="/exam/student/caseStudy">
					<p>Case Study</p>
				</a>
			</li>
		<%} %>	
		
			</ul>
			</li>
			<%} %>
			 <!-- _______________________________________________________ CareerServices _____________________________________________________ -->
			 
			<%if(!lsCon.checkLead(request, response)){ %>
				<%@include file="/views/common/CSStudentSidebar.jsp"%>
			<%} %>
			
			<!-- ___________________________________________________________ Student Support ________________________________________________ -->
			
			<li class="has-sub-menu <%=studentSupportMenuClass%>"> <a href="/studentportal/supportOverview"><span class="icon-student-support"></span>
            <p>Student Support</p>
            </a>
            <ul class="sz-sub-menu">
              
              <li class="<%=studentSupportOverviewMenuClass%>"> <a href="/studentportal/supportOverview">
                <p>Overview</p>
                </a> </li>
             <%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){ %>
              <li class="<%=studentSupportFAQMenuClass%>"> 
                <a href="/studentportal/faqSAS">
                   <p>FAQs</p>
                </a> 
              </li>
             <%} else {%>
              	 <li class="<%=studentSupportFAQMenuClass%>"> 
                	<a href="/studentportal/faq">
                   		<p>FAQs</p>
                	</a> 
              	</li>
             <%} %>
              <li class="class="<%=studentSupportServiceRequestMenuClass%>"> <a class="<%= diableLink %>" href="/studentportal/selectSRForm">
                <p style="<%= color %>">Service Request</p>
                </a> </li>
              <li class="<%=studentSupportContactMenuClass%>"> <a href="/studentportal/contactUs">
                <p>Contact Us</p>
                </a> </li>
                <li class="<%=studentSupportFeedback%>"> <a class="<%= diableLink %>" href="/studentportal/feedback">
                <p style="<%= color %>">Feedback & Suggestions</p>
                </a> </li>
            </ul>
            </li>

            
            <%if("Yes".equals(validityExpired)) {%>
            
	          <li class="has-sub-menu <%=examMenuClass%>"> 
			  	 <%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
				     <a href="/exam/student/getMostRecentResultsSaS"><span class="icon-exams"></span>
						<p>Exam</p>
					 </a>
				 <%} else {%>
				    <a href="/exam/student/getMostRecentResults"><span class="icon-exams"></span>
						<p>Exams</p> 
					</a> 
				<%}%>
					<ul class="sz-sub-menu">
					  <li class="<%=examResultsMenuClass%>">
					 <%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
					     <a href="/exam/student/getMostRecentResultsSaS">
							<p>Exam Results</p>
						 </a>
					 <%} else {%>
					          <a href="/exam/student/viewNotice">
							     <p>Exam Results</p>
						      </a>
					 <%} %>
					  </li>
					  
					<%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()))){%>
					  <li class="<%=examAssignmentResultsMenuClass%>">
					  	<a href="/exam/student/getMostRecentAssignmentResults">
							<p>Assignment Marks</p>
						</a>
					  </li>
					  <li  class="<%=examMarksheetMenuClass%>">
					    <%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
						
						  	<a class="<%= diableLink %>" href="/exam/studentSelfSASMarksheetForm">
								<p style="<%= color %>">Marksheet</p>
							</a>
						<%} else {%>
							<a class="<%= diableLink %>" href="/exam/studentSelfMarksheetForm">
								<p style="<%= color %>">Marksheet</p>
							</a>
						<%} %>
					  </li>
					  
					<%} %>
					</ul>
				</li>
			
               
            <%} %>
            
           <% if(!lsCon.checkLead(request, response)){ %>
           		<li class="has-sub-menu re_reg_li">
		            <a href="/studentportal/reRegistrationPage" target="_blank">
		            <span class="icon-reregister"></span>
	                <p>Re-Registration</p></a> 
                </li>   
            <% } %> 
           

<%--             <%if(ifReRegistrationActive) {%> --%>
<!--             <li class="has-sub-menu"> -->
<!--             <a href="/studentportal/reRegistrationPage" target="_blank"> -->
<!--             <i class="fa fa-refresh" style="font-size: 1.25rem;" aria-hidden="true"></i> -->
<!--                 <p>Re-Registration</p>  -->
<!--                 </a> </li>   -->
<!--             </li>  -->
<%--             <%}%>    --%>

         	<%if("No".equals(validityExpired)) {%>
         	
         	<!-- ______________________________________________ QuickLink _______________________________________________ -->
         		
           <li class="has-sub-menu <%=quickLinksMenuClass%>"> <a href="#"><span class="icon-quick-links"></span>
            <p>Quick Links</p>
            </a>
            <ul class="sz-sub-menu">
              <li> <a class="<%= diableLink %>" href="/studentportal/gotoEZProxy"  target="_blank">
                <p  style="<%= color %>">Digital Library</p>
                </a> </li>
                
          	 <%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()) || "Verizon".equals(studentInSideBar.getCenterName()) || "PGDGM".equals(studentInSideBar.getProgram()))){%>
              <li class="<%=quickLinksPCPRegistrationMenuClass%>"> 
              	<a class="<%= diableLink %>" href="/acads/student/selectPCPSubjectsForm">
                	<p style="<%= color %>">PCP/VC Registration</p>
                </a> 
              </li>
             <%}%>   
                
            <li class="<%=quickLinksUFMDashboardMenuClass%>"> 
              	<a class="<%= diableLink %>" href="/exam/student/ufmStatus">
                	<p style="<%= color %>">UFM Dashboard</p>
                </a> 
            </li>
           <%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
			
			<%if("No".equals(earlyAccess)) {%>
			<li class="<%=examRegistrationMenuClass%>" >
			  	<a class="<%= diableLink %>" href="/exam/executiveRegistrationForm">
					<p style="<%= color %>">Exam Registration</p>
				</a>
			</li>
			<%} %>
			
			<%} else{%>
			
			<%if("No".equals(earlyAccess)) {%>
			<li class="<%=examRegistrationMenuClass%>" >
			  	<a class="<%= diableLink %>" href="/exam/selectSubjectsForm">
					<p style="<%= color %>">Exam Registration</p>
				</a>
			</li>
			
			<%} %>
			<%} %>
			
			
              <li class="<%=examHallTicketMenuClass%>">
			<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
			
			  	<a class="<%= diableLink %>" href="/exam/downloadExecutiveHallTicket">
					<p style="<%= color %>">Hall Ticket</p>
				</a>
			<%} else {%>
			  	<!-- <a href="/exam/downloadHallTicket"> -->
			  	<a class="<%= diableLink %>" href="/exam/student/previewHallTicket">
					<p style="<%= color %>">Hall Ticket</p>
				</a>
			<%} %>
			</li>
			
			
            <li  class="<%=examMarksheetMenuClass%>">
		    <%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
			  	<a class="<%= diableLink %>" href="/exam/studentSelfSASMarksheetForm">
					<p style="<%= color %>">Marksheet</p>
				</a>
			<%}else{%>
				<a class="<%= diableLink %>" href="/exam/studentSelfMarksheetForm">
					<p style="<%= color %>">Marksheet</p>
				</a>
			<%} %>
			</li>
			<%if(!"MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
				<li  class="<%=examTranscriptMenuClass%>">
					<a class="<%= diableLink %>" href="/exam/student/generateStudentSelfTranscriptForm">
						<p style="<%= color %>">Transcript</p>
					</a>
				</li>
			<%} %>
              <li class="<%=quickLinksExamFeeReceiptMenuClass%>"> 
             <%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
			  	<a class="<%= diableLink %>" href="/exam/printExecutiveBookingStatusQuickLinks">
					<p style="<%= color %>">Exam Fee Receipt</p>
				</a>
			<%}else{%>
				<a class="<%= diableLink %>" href="/exam/student/printBookingStatus">
					<p style="<%= color %>">Exam Fee Receipt</p>
				</a>
			<%} %>
                </li>
          	 <%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()) || "Verizon".equals(studentInSideBar.getCenterName()) || "PGDGM".equals(studentInSideBar.getProgram()))){%>
              <li class="<%=quickLinksPCPFeeReceiptMenuClass%>"> 
              	<a class="<%= diableLink %>" href="/acads/student/downloadPCPRegistrationReceipt">
               		 <p style="<%= color %>">PCP/VC Registration Receipt</p>
                </a> 
              </li>
             <%} %>
               <li class="<%=quickLinksRegistrationFeeReceipt%>"> <a class="<%= diableLink %>" href="/studentportal/viewFeeReceipt">
                <p style="<%= color %>">Fee Receipt</p>
                </a> </li>
	            <li class="<%=quickLinksDispatchOrder%>"> <a class="<%= diableLink %>" href="/studentportal/getDispatches">
                <p style="<%= color %>">Study Kit Dispatch Orders</p>
                </a> </li>
                <li class="<%=quickLinksMyDocuments%>"> <a class="<%= diableLink %>" href="/exam/student/myDocuments">
                <p style="<%= color %>">My Documents</p>
                </a></li>
                  <li class="<%=quickLinkAnnouncementArchival%>"> <a class="<%= diableLink %>" href="/studentportal/student/getAllStudentAnnouncements"> 
                <p style="<%= color %>">Announcement Archival 	 </p>
                </a> </li>
              <li class="<%=quickLinksChangePasswordMenuClass%>"> <a class="<%= diableLink %>" href="/studentportal/changePassword">
                <p style="<%= color %>">Change Password</p>
                </a> </li>
                

              <li class="<%=quickLinksChangePasswordMenuClass%>"> <a class="<%= diableLink %>" class="<%= diableLink %>" href="/studentportal/studentAttendence">
                <p style="<%= color %>"> Attendance </p>
                </a>
                 </li>
                 
              <li class="<%=quickLinksChangePasswordMenuClass%>"> 
              	<a class="<%= diableLink %>" href="/acads/student/keyEvents">
                	<p style="<%= color %>"> Key Events </p>
                </a>
              </li>
            
         	<%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()))){%>
              <li><a class="<%= diableLink %>" href="/studentportal/reRegistrationPage" target="_blank">
						<p style="<%= color %>">Re-Registration</p>
				 </a>
				</li>
             <%} %> 
              
              <!--   <li><a href="http://www.thepracticetest.in/NMIMS/" target="_blank">
                <p>Demo Exam</p>
                </a></li> -->
            </ul>
          </li>
     <%--      <%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()))){%>
          <li class="has-sub-menu <%=walletMenuClass%>"> <a href="/studentportal/myWalletForm"><span class="glyphicon glyphicon-credit-card"></span>
            <p>My Wallet</p>
            </a>
          </li>
          <%} %>  --%>
          <!-- 30jan 
            -->
          
         <%} %>
          
          <!-- <li class=""> 
          	<a href="/ltidemo/Timeline">
          		<span>
          			<i class="fa fa-play-circle-o" aria-hidden="true"></i>
          		</span>
            <p>EMBA</p>
            </a>
          </li> -->
          
        </ul>
		
		
      </div>
     
      
 <!-- Mobile notification contents -->
 
 <% if(noOfAnnouncemntsInSidebar > 0){ %>
      <div class="mobile-notification-wrapper visible-xs" id="mobile-announcements">
        <div class="mobile-notification-header"><a href="#" class="hide-link">Hide <span class="glyphicon glyphicon-menu-right"></span></a>
          <div class="clearfix"></div>
          <h4>Announcements</h4>
          <!-- <div class="btn-group" role="group">
			  <a href="#" class="btn btn-default active">Show </a>
			  <a href="#" class="btn btn-default">All Announcements <span class="glyphicon glyphicon-menu-down"></span></a>
		  </div> -->
        </div>
        <ul class="announcement-list">
        
        	<%
        	int count = 0;
	          for(AnnouncementBean announcement : announcementsInSideBar){
	        	  count++;
	        	  Date formattedDate = formatterSidebar.parse(announcement.getStartDate());
	  			  String formattedDateString = dateFormatterSidebar.format(formattedDate);
	          %>
				          
			            <li> <a href="#" data-toggle="modal" data-dismiss="modal" data-target="#announcementModal<%=count%>">
			            <h3><%=announcement.getSubject() %></h3>
			            <p><%=announcement.getDescription() %></p>
			            <h4><%=formattedDateString%> by <span><%= announcement.getCategory() %></span> </h4>
			            </a> </li>
	          
	          <%
	          
	          } %>
         
        </ul>
      </div>
<%}%>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script> 
 var sapid="<%=studentInSideBar.getSapid()%>"; 
let data = {
		'sapId':sapid
	};
$(".re_reg_li").css("display","none");
	/* $.ajax({
		   type : "POST",
		   contentType : "application/json",
		   url : "/studentportal/m/reRegForMobile",   
		   data : JSON.stringify(data),
		   success : function(data) {
			   if(data.success==true){
				   $(".re_reg_li").css("display","block");
			   }
		   } 
	}); */
</script>