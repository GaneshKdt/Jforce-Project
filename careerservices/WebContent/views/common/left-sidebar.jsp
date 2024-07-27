
<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.StudentCareerservicesBean"%>
<%@page import="com.nmims.beans.AnnouncementCareerservicesBean"%>
<%@page import="java.util.ArrayList"%>

<%

	if(request.getSession().getAttribute("student_careerservices") == null){
		
		response.sendRedirect("../studentPortal/home");
		String scr = "<script>window.location='/studentportal/home'</script>";
		response.getWriter().write(scr);
	}else{

		String hasAssignmentFlag = (String) request.getSession().getAttribute("hasAssignmentFlag");
		String hasTestFlag = (String) request.getSession().getAttribute("hasTestFlag");
	
		StudentCareerservicesBean studentInSideBar = (StudentCareerservicesBean)session.getAttribute("student_careerservices");
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
		String careerServicesClass ="";
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
		String quickLinksDispatchOrder ="";
		String quickLinksMyDocuments = "";
		String quickLinkAnnouncementArchival ="";
		String validityExpired = (String)session.getAttribute("validityExpired");
		String earlyAccess = (String)session.getAttribute("earlyAccess");
	

		
		ArrayList<AnnouncementCareerservicesBean> announcementsInSideBar = (ArrayList<AnnouncementCareerservicesBean>)session.getAttribute("announcements");
		int noOfAnnouncemntsInSidebar = announcementsInSideBar != null ? announcementsInSideBar.size() : 0;
		SimpleDateFormat formatterSidebar = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormatterSidebar = new SimpleDateFormat("dd-MMM-yyyy");

%>

<div class="sz-main-navigation">
		<div class="mobile-logo visible-xs d-none d-md-none d-xs-block "><a href="#"><img src="${pageContext.request.contextPath}/assets/images/logo.png" class="img-responsive" alt=""/></a></div>
		<ul class="sz-nav">
		<%if("No".equals(validityExpired)) {%>
			<li id="toggle-nav"><a href="#"><span class="icon-arrow-right"></span></a></li>
			<li><a href="/studentportal/home"><span class="icon-dashboard"></span>
			<p>Dashboard</p>
			</a></li>
			<li class="<%=academicCalendarMenuClass%>"> <a href="/acads/viewStudentTimeTable"><span class="icon-academic-calendar"></span>
			<p>Academic Calendar</p>
			</a></li>
			<li class="<%=myCoursesMenuClass%>"> <a href="/studentportal/student/viewCourseHomePage"><span class="icon-my-courses"></span>
			<p>My Courses</p>
			</a></li>
		
			<li class="">				
				<a href="/studentportal/lastCycleContent">
					<span>
						<i class="fa fa-book" aria-hidden="true"></i>
					</span>
					<p>Last Cycle Content</p>
				</a>
			</li>		
			<li class="">				
				<a href="/acads/videosHomeNew?pageNo=1&academicCycle=All">
					<span>
						<i class="far fa-play-circle" aria-hidden="true"></i>
					</span>
					<p>Session Videos</p>
				</a>
			</li>
		
			<%//if("No".equalsIgnoreCase(earlyAccess)){ %>
		
			<li class="has-sub-menu">			
				<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
					<a href="/exam/getMostRecentResultsSaS"><span class="icon-exams"></span>
						<p>Exam</p>
					</a>
				<%} else {%>
					<a href="/exam/getMostRecentResults">
						<span class="icon-exams"></span>
						<p>Exams</p>				
					</a>		
				<%}%>
			<ul class="sz-sub-menu">
				<li class="<%=examResultsMenuClass%>">
					<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
						<a href="/exam/getMostRecentResultsSaS">
							<p>Exam Results</p>
						</a>
					<%} else {%>
						<a href="/exam/viewNotice">
							<p>Exam Results</p>
						</a>
					<%} %>
				</li>
				<%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()))){%>
					<li class="<%=examAssignmentResultsMenuClass%>">
						<a href="/exam/getMostRecentAssignmentResults">
						<p>Assignment Marks</p>
					</a>
					</li>
				<%} %>
				<li>
					<a href="/exam/viewModelQuestionForm" >
						<p>Demo Exam</p>
					</a>
				</li>
				<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
					<li class="<%=examTimetableMenuClass%>">
							<a href="/exam/executiveStudentTimeTable">
							<p>Exam Calendar</p>
						</a>
					</li>
				<%} else {%>
				<li class="<%=examTimetableMenuClass%>">
						<a href="/exam/studentTimeTable">
						<p>Exam Calendar</p>
					</a>
				</li>
				<%} %>
				<li class="<%=examHallTicketMenuClass%>">
					<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
				
							<a href="/exam/downloadExecutiveHallTicket">
							<p>Hall Ticket</p>
						</a>
					<%} else {%>
							<a href="/exam/downloadHallTicket">
							<p>Hall Ticket</p>
						</a>
					<%} %>
				</li>
				<li	class="<%=examMarksheetMenuClass%>">
					<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
				
							<a href="/exam/studentSelfSASMarksheetForm">
							<p>Marksheet</p>
						</a>
					<%} else {%>
						<a href="/exam/studentSelfMarksheetForm">
							<p>Marksheet</p>
						</a>
					<%} %>
				</li>
			<%
				if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()))){
			%>
			<li class="<%=examAssignmentsMenuClass%>" >
					<a href="/exam/viewAssignmentsForm">
					<p> Assignment </p>
				</a>
			</li>
			<%-- <li class="<%=examAssignmentsMenuClass%>" >
					<a href="/exam/viewTestsForStudent">
					<p>Internal Assignment (MCQ&rsquo;s)</p>
				</a>
			</li> --%>			<%
				}
			%>
					
			<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
		
			<%if("No".equals(earlyAccess)) {%>
			<li class="<%=examRegistrationMenuClass%>" >
					<a href="/exam/executiveRegistrationForm">
					<p>Exam Registration</p>
				</a>
			</li>
			<%} %>
			<%} else{%>
		
			<%if("No".equals(earlyAccess)) {%>
			<li class="<%=examRegistrationMenuClass%>" >
					<a href="/exam/selectSubjectsForm">
					<p>Exam Registration</p>
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
		
			<%if(studentInSideBar.getProgram().startsWith("PG")){ %>
			<li class="<%=examProjetSubmissionMenuClass%>">
					<a href="/exam/viewProject?subject=Project">
					<p>Project Submission</p>
				</a>
			</li>
		<%} }%>
		
		<%if("EPBM".equals(studentInSideBar.getProgram())){ %>
			<li>
					<a href="/exam/caseStudy">
					<p>Case Study</p>
				</a>
			</li>
		<%} %>
		
			</ul>
			</li>
			<%} %>
			<%//} %>
			<%@ include file="/views/common/CSStudentSidebar.jsp"%>
			<li class="	has-sub-menu <%=studentSupportMenuClass%>">			
				<a href="/studentportal/supportOverview">
					<span class="icon-student-support"></span>
					<p>Student Support</p>
				</a>
				<ul class="sz-sub-menu">
					<li class="<%=studentSupportOverviewMenuClass%>" onclick="href='/studentportal/supportOverview'">					
						<a href="/studentportal/supportOverview">
							<p>Overview</p>
						</a>				
					</li>
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
					<li class="<%=studentSupportServiceRequestMenuClass%>">					
						<a href="/studentportal/selectSRForm">
							<p>Service Request</p>
						</a>				
					</li>
					<li class="<%=studentSupportContactMenuClass%>">					
						<a href="/studentportal/contactUs">
							<p>Contact Us</p>
						</a>				
					</li>
					<li class="<%=studentSupportFeedback%>">					
						<a href="/studentportal/feedback">
							<p>Feedback & Suggestions</p>
						</a>				
					</li>
				</ul>
			</li>
		
		
			
		
			<%if("No".equals(validityExpired)) {%>
			<li class="has-sub-menu <%=quickLinksMenuClass%>"> <a href="#"><span class="icon-quick-links"></span>
			<p>Quick Links</p>
			</a>
			<ul class="sz-sub-menu">
				<li> <a href="/studentportal/gotoEZProxy"	target="_blank">
				<p>Digital Library</p>
				</a> </li>
			
				<%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()) || "Verizon".equals(studentInSideBar.getCenterName()) || "PGDGM".equals(studentInSideBar.getProgram()))){%>
				<li class="<%=quickLinksPCPRegistrationMenuClass%>">					<a href="/acads/selectPCPSubjectsForm">
					<p>PCP/VC Registration</p>
				</a>				</li>
			<%}%>
			
			<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
		
			<%if("No".equals(earlyAccess)) {%>
			<li class="<%=examRegistrationMenuClass%>" >
					<a href="/exam/executiveRegistrationForm">
					<p>Exam Registration</p>
				</a>
			</li>
			<%} %>
		
			<%} else{%>
			
				<%if("No".equalsIgnoreCase(earlyAccess)){ %>
				<li> <a href="/exam/selectSubjectsForm">
				<p>Exam Registration</p>
				</a> </li>
				<%} %>
				<%} %>
		
		
			<li class="<%=examHallTicketMenuClass%>">
			<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
		
					<a href="/exam/downloadExecutiveHallTicket">
					<p>Hall Ticket</p>
				</a>
			<%} else {%>
					<a href="/exam/downloadHallTicket">
					<p>Hall Ticket</p>
				</a>
			<%} %>
			</li>
		
		
			<li	class="<%=examMarksheetMenuClass%>">
			<%if("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram())){%>
					<a href="/exam/studentSelfSASMarksheetForm">
					<p>Marksheet</p>
				</a>
			<%}else{%>
				<a href="/exam/studentSelfMarksheetForm">
					<p>Marksheet</p>
				</a>
			<%} %>
			</li>
		
			<li class="<%=quickLinksExamFeeReceiptMenuClass%>">
			<%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()) || "Verizon".equals(studentInSideBar.getCenterName()) || "PGDGM".equals(studentInSideBar.getProgram()))){%>
				<a href="/exam/printBookingStatus">
				<p>Exam Fee Receipt</p>
				</a>			<%}else{%>
				<a href="/exam/printExecutiveBookingStatusQuickLinks">
					<p>Exam Fee Receipt</p>
				</a>
		
			<%} %>
				</li>
			
				<%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()) || "Verizon".equals(studentInSideBar.getCenterName()) || "PGDGM".equals(studentInSideBar.getProgram()))){%>
				<li class="<%=quickLinksPCPFeeReceiptMenuClass%>">					<a href="/acads/downloadPCPRegistrationReceipt">
						<p>PCP/VC Registration Receipt</p>
				</a>				</li>
			<%} %>			<li class="<%=quickLinksRegistrationFeeReceipt%>"> <a href="/studentportal/viewFeeReceipt">
				<p>Fee Receipt</p>
				</a> </li>
				<li class="<%=quickLinksDispatchOrder%>"> <a href="/studentportal/getDispatches">
				<p>Study Kit Dispatch Orders</p>
				</a> </li>
				<li class="<%=quickLinksMyDocuments%>"> <a href="/exam/myDocuments">
				<p>My Documents</p>
				</a></li>
					<li class="<%=quickLinkAnnouncementArchival%>"> <a href="/studentportal/getAllStudentAnnouncements">
				<p>Announcement Archival </p>
				</a> </li>
				<li class="<%=quickLinksChangePasswordMenuClass%>"> 
					<a href="/studentportal/changePassword">
						<p>Change Password</p>
					</a> 
				</li>
			
				<li class="<%=quickLinksChangePasswordMenuClass%>"> 
					<a href="/studentportal/studentAttendence">
						<p> Attendence </p>
					</a>
				</li>
			
				<li class="<%=quickLinksChangePasswordMenuClass%>">					
					<a href="/acads/keyEvents">
						<p> Key Events </p>
					</a>
				</li>
		
			<%if(!("MPDV".equals(studentInSideBar.getProgram()) || "EPBM".equals(studentInSideBar.getProgram()))){%>
				<li><a href="/studentportal/reRegistrationPage" target="_blank">
						<p>Re-Registration</p>
				</a>
				</li>
			<%} %>			
				<!--	<li><a href="http://www.thepracticetest.in/NMIMS/" target="_blank">
				<p>Demo Exam</p>
				</a></li> -->
			</ul>
			</li>
			<!-- 30jan
			-->
		
		<%} %>
		
		</ul>
	
	
		</div>

	
 <!-- Mobile notification contents -->
 <%if(noOfAnnouncemntsInSidebar > 0){ %>
	<div class="mobile-notification-wrapper	d-none d-md-none d-xs-block " id="mobile-announcements">
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
				for(AnnouncementCareerservicesBean announcement : announcementsInSideBar){
					count++;
					Date formattedDate = formatterSidebar.parse(announcement.getStartDate());
					String formattedDateString = dateFormatterSidebar.format(formattedDate);
			%>
						
			<li> 
				<a href="#" data-toggle="modal" data-dismiss="modal" data-target="#announcementModal<%=count%>">
					<h3><%=announcement.getSubject() %></h3>
					<p><%=announcement.getDescription() %></p>
					<h4><%=formattedDateString%> by <span><%=announcement.getCategory() %></span> </h4>
				</a> 
			</li>
			
			<% 
				}
			%>
				
		</ul>
	</div>
		<%
		} 
	} %>
	
