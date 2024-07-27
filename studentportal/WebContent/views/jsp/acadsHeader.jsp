
<%

String roles = request.getParameter("roles");

%>

<%if(roles.indexOf("Acads Admin") != -1 ){ %>
<li><a href="#">Session Set Up</a>
	<ul class="subMenu">
		<li><a href="/acads/admin/uploadFacultyAvailabilityForm">Upload Faculty Availability</a></li>
		<li><a href="/acads/admin/searchFacultyAvailabilityForm">Search Faculty Availability</a></li>
		<li><a href="/acads/admin/uploadCourseFacultyMappingForm">Upload Course Faculty Mapping</a></li>
		<li><a href="/acads/admin/searchCourseFacultyMappingForm">Search Course Faculty Mapping</a></li>
		<li><a href="/acads/admin/uploadSessionDayTimeForm">Upload Session Day/Time</a></li>
		<li><a href="/acads/admin/searchSessionDayTimeForm">Search Session Day/Time</a></li>
		<li><a href="/acads/admin/addScheduledSessionForm">Direct Session Creation</a></li>
		<li><a href="/acads/admin/autoScheduleForm">Create Webinar Links</a></li>
		<li><a href="/acads/admin/searchScheduledSessionForm">Search Generated Schedule</a></li>
		<li><a href="/acads/admin/setSessionCalenderDatesForm">Set Session Calendar Dates</a></li>
	</ul>
</li>
<%} %>


<li class=""><a href="#" title="Academic Calendar"><i
		class="fa fa-calendar fa-lg"></i></a>
	<ul class="subMenu">

		<%if(roles.indexOf("Acads Admin") != -1 ){ %>
		<li><a href="/acads/admin/viewTimeTable">Academic Calendar</a></li>
		<%} %>

		<%if(roles.indexOf("Acads Coordinator") != -1 || roles.indexOf("Learning Center") != -1 
      || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
		<li><a href="/acads/admin/viewCompleteTimeTable">Academic Calendar</a></li>
		<%} %>


		<%if( roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1 || roles.indexOf("Insofe") != -1 ){ %>
		<li><a href="/acads/admin/viewFacultyTimeTable">Faculty Calendar</a></li>

		<%} %>

		<%--   <%if(!(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Coordinator") != -1)){%> 
      <li><a href="/acads/viewStudentTimeTable">Student Academic Calendar</a></li>
      <%} %>  --%>

	</ul></li>


<%if(roles.indexOf("Watch Videos Access") != -1 || roles.indexOf("Acads Admin") != -1   || roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>
<li class=""><a href="#">Session Videos </a>
	<ul class="subMenu">
		<%if(roles.indexOf("Watch Videos Access") == -1){ %>
		<li><a href="/acads/admin/uploadVideoContentForm">Upload Session
				Videos </a></li>
		<%} %>
		<li><a href="/acads/admin/videosHome?pageNo=1&academicCycle=All">Watch
				All Videos </a></li> 

	</ul></li>
<%} %>

<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>
<li class=""><a href="#">Session Plan </a>
	<ul class="subMenu">
			<li><a href="/acads/admin/uploadSyllabusForm">Upload Syllabus </a></li>
 			<li><a href="/acads/admin/syllabus">View Syllabus</a></li>
		<li><a href="/acads/admin/manageSessionPlan">Manage Session Plan </a></li>
<li><a href="/acads/admin/managePGSessionPlan">Manage PG Session Plan </a></li>
	</ul></li>
<%} %>

<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>
<li class=""><a href="#">MBAX-Session Plan </a>
    <ul class="subMenu">

        <li><a href="/acads/mbax/sp/a/admin/manageSessionPlan">MBAX - Manage Session Plan </a></li>

    </ul></li>
<%} %>

<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1  || roles.indexOf("Learning Center") != -1
	|| roles.indexOf("Insofe") != -1 ){ %>
<li><a href="#" title="Content"><i class="fa fa-book fa-lg"></i></a>
	<ul class="subMenu">
		<%if(roles.indexOf("Acads Admin") != -1 ){ %>
		<li><a href="/acads/admin/uploadContentForm">Upload Content</a></li>
		<li><a href="/acads/admin/viewAllSubjectsForContent">View All
				Content</a></li>

		<li><a href="/acads/admin/transferContentForm">Transfer Content</a></li>
		<li><a href="/forum/admin/createForumThreadForm">Create Forum</a></li>
		<li><a href="/forum/admin/searchForumThreadForm">Search Forum</a></li>


		<li><a href="/studentportal/uploadLearningResourcesExcelForm">Manage
				Learning Resources</a></li>
		<li><a  href="/acads/admin/adhoc-upload-file-form">Upload File & Generate Link</a></li>
		<%} %>


		<%if(roles.indexOf("Faculty") != -1  || roles.indexOf("Insofe") != -1 ){ %>
		<li><a href="/acads/admin/viewApplicableSubjectsForFacultyForm">Learning

				Resources</a></li>
		<li><a href="/acads/admin/viewReviewForFacultyForm">Review Faculty
				For Session</a></li>
		<li><a href="/forum/admin/createForumThreadForm">Create Forum</a></li>
		<li><a href="/forum/admin/searchForumThreadForm">Search Forum</a></li>

		<li><a href="/studentportal/uploadLearningResourcesExcelForm">Manage
				Learning Resources</a></li>
		<li><a href="/studentportal/gotoEZProxy" target="_blank">Digital
				Library</a></li>

		<%} %>

		<%if(roles.indexOf("Learning Center") != -1){ %>
		<li><a href="/acads/admin/viewAllSubjectsForContent">Learning
				Resources</a></li>
		<%} %>

	</ul></li>

<%} %>

<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
<li><a href="#">Faculty</a>
	<ul class="subMenu">  
		<li><a href="/acads/admin/addFacultyForm">Add Faculty</a></li>
		<li><a href="/acads/admin/searchFacultyForm">Search Faculty</a></li>
		<li><a href="/acads/admin/uploadFacultyCourseForm">Faculty Learning
				Resource Access</a></li>
	</ul></li>
<%} %>

<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
<li><a href="#" title="Acads Reports"><i
		class="fa fa-bar-chart fa-lg"></i></a>
	<ul class="subMenu">
		<%if(roles.indexOf("Acads Admin") != -1 ){ %>
		<li><a href="/acads/admin/searchAttendanceFeedbackForm">Attendance,
				Feedback Report</a></li>
		<li><a href="/acads/admin/searchAttendanceFeedbackForMbaWxForm">Session
				Attendance For Mba Wx Report</a></li>
		<li><a href="/acads/admin/searchQueriesForm">Query Report</a></li>
		<li><a href="/acads/searchStudentZoneLoginsForm">StudentZone
				Login Report</a></li>
		<%} %>
		<%if(roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
		<li><a href="/acads/admin/searchAttendanceFeedbackForm">Attendance,
				Feedback Report</a></li>
		<li><a href="/acads/admin/searchAttendanceFeedbackForMbaWxForm">Session
				Attendance For Mba Wx Report</a></li>
		<%} %>
		<li><a href="/acads/admin/pcpRegistrationReportForm">PCP Bookings
				Report</a></li>
		<%if(roles.indexOf("Information Center")!=-1||roles.indexOf("Learning Center")!=-1||roles.indexOf("Student Support")!=-1){ %>
			  <li><a href="/studentportal/admin/louReportForm">LOU Report</a></li>
			  <%} %>
	</ul></li>
<%} %>

<%if(roles.indexOf("Acads Admin") != -1 ){ %>
<li><a href="#" title="Settings"><i class="fa fa-cog fa-lg"
		aria-hidden="true"></i></a>
	<ul class="subMenu">
		<li><a href="/acads/admin/changeConfigurationForm">Set up Dates</a></li>
		<li><a href="/acads/loginAsForm">Acads: Log In As</a></li>
		<li><a href="/acads/admin/eventForm">Set Key Events</a></li>
		<li><a href="/studentportal/generateRegistrationLinks">Generate
				Post Registrations Links</a></li>
		<li><a href="/exam/examExecutiveRegistrationChecklist">Executive Exam
				Reg CheckList</a></li>
				  <li><a href="/exam/examRegularRegistrationChecklist">Regular Exam Reg Checklist</a></li>
	</ul></li>
<%} %>

<%if(roles.indexOf("Acads Admin") != -1 ){ %>
<li><a href="#" title="Internal Assessment"><i
		class="fa fa-flask fa-lg" aria-hidden="true"></i></a>
	<ul class="subMenu">
		<li><a href="/exam/addTest">Add Test</a></li>
		<li><a href="/exam/admin/makeResultsLiveForm">View All Tests</a></li>
	</ul></li>
<%} %>

<%if(roles.indexOf("Faculty") != -1 || roles.indexOf("Insofe") != -1 ){%>
          <li>
   				<a href="#">Session Videos </a>
   				<ul class="subMenu">
   					<li>
 						<a href="/acads/admin/videosHome?pageNo=1&academicCycle=All">Watch All Videos </a>
 					</li>
   				
   				</ul>
 		 </li>
          <%}%>
