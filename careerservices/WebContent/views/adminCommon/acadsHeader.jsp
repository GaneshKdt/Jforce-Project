<%

String roles = request.getParameter("roles");
%>

<%if(roles.indexOf("Acads Admin") != -1 ){ %>
     <li>
     	<a href="#">Session Set Up</a>
  	<ul class="subMenu">
       <li><a href="/acads/uploadFacultyAvailabilityForm">Upload Faculty Availability</a></li>
       <li><a href="/acads/searchFacultyAvailabilityForm">Search Faculty Availability</a></li>
       <li><a href="/acads/uploadCourseFacultyMappingForm">Upload Course Faculty Mapping</a></li>
       <li><a href="/acads/searchCourseFacultyMappingForm">Search Course Faculty Mapping</a></li>
       <li><a href="/acads/uploadSessionDayTimeForm">Upload Session Day/Time</a></li>
       <li><a href="/acads/searchSessionDayTimeForm">Search Session Day/Time</a></li>
       <li><a href="/acads/addScheduledSessionForm">Direct Session Creation</a></li>
       <li><a href="/acads/autoScheduleForm">Generate Schedule/WebEx</a></li>
      <li><a href="/acads/searchScheduledSessionForm">Search Generated Schedule</a></li>
      <li><a href="/acads/setSessionCalenderDatesForm">Set Session Calender Dates</a></li>
      </ul>
    </li>  
    <%} %>  
     <style>
      	.navbar-collapse.collapse:not(.in) {
	        display: none !important;
	    }
	    .collapse:not(.show) {
		    display: block;
		}
     </style>
  
  <li class="">
   	<a href="#" title="Academic Calendar"><i class="fa fa-calendar fa-lg" ></i></a>
 	<ul class="subMenu">
 	
  	<%if(roles.indexOf("Acads Admin") != -1 ){ %>
      <li><a href="/acads/viewTimeTable">Academic Calendar</a></li>
      <%} %> 
      
      <%if(roles.indexOf("Acads Coordinator") != -1 || roles.indexOf("Learning Center") != -1 
      || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
      <li><a href="/acads/viewCompleteTimeTable">Academic Calendar</a></li>
      <%} %> 
      
      <%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1){ %> 
      <li><a href="/acads/viewFacultyTimeTable">Faculty Calendar</a></li>
      <%} %> 
      
    <%--   <%if(!(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Coordinator") != -1)){%> 
      <li><a href="/acads/viewStudentTimeTable">Student Academic Calendar</a></li>
      <%} %>  --%>
     
     </ul>
   </li> 
     
	
   	<%if(roles.indexOf("Watch Videos Access") != -1 || roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1  || roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>    
	 <li  class="">
   		<a href="#">Session Videos </a>
   		<ul class="subMenu">
 			<%if(roles.indexOf("Watch Videos Access") == -1){ %>
 			<li>
 				<a href="/acads/uploadVideoContentForm">Upload Session Videos </a>
 			</li>
 		     <%} %>
 			<li>
 				<a href="/acads/videosHomeAdmin?pageNo=1&academicCycle=All">Watch All Videos </a>
 			</li>
 			
 		</ul>
 	</li>
     <%} %>
    
<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1  || roles.indexOf("Learning Center") != -1 ){ %>    
    <li>
    <a href="#" title="Content"><i class="fa fa-book fa-lg"></i></a>
  	<ul class="subMenu">
  	<%if(roles.indexOf("Acads Admin") != -1 ){ %>
       <li><a href="/acads/uploadContentForm">Upload Content</a></li>
       <li><a href="/acads/viewAllSubjectsForContent">View All Content</a></li>
       <li><a href="/acads/transferContentForm">Transfer Content</a></li>
    <%} %>
    
   <%if(roles.indexOf("Faculty") != -1 ){ %>
       <li><a href="/acads/viewApplicableSubjectsForFacultyForm">Learning Resources</a></li>
   <%} %>
   
   <%if(roles.indexOf("Learning Center") != -1){ %>
       <li><a href="/acads/viewAllSubjectsForContent">Learning Resources</a></li>
   <%} %>
     
      </ul>
    </li> 
         
<%} %>
    
   <%if(roles.indexOf("Acads Admin") != -1 ){ %>
    <li>
    <a href="#" >Faculty</a>
  	<ul class="subMenu">
      <li><a href="/acads/addFacultyForm">Add Faculty</a></li>
      <li><a href="/acads/searchFacultyForm">Search Faculty</a></li>
      <li><a href="/acads/uploadFacultyCourseForm">Faculty Learning Resource Access</a></li>
      </ul>
    </li> 
   <%} %> 
    
<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
   <li>
   <a href="#" title="Acads Reports"><i class="fa fa-bar-chart fa-lg" ></i></a>
 	<ul class="subMenu">
 	 <%if(roles.indexOf("Acads Admin") != -1 ){ %>
      <li><a href="/acads/searchAttendanceFeedbackForm">Attendance, Feedback Report</a></li>
      <li><a href="/acads/searchQueriesForm">Query Report</a></li>
      <%} %>
   	  <%if(roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
      	<li><a href="/acads/searchAttendanceFeedbackForm">Attendance, Feedback Report</a></li>
      <%} %>
      <li><a href="/acads/pcpRegistrationReportForm">PCP Bookings Report</a></li>
      
     </ul>
   </li> 
<%} %> 
 
<%if(roles.indexOf("Acads Admin") != -1 ){ %>
             	  <li>
                <a href="#" title="Settings"><i class="fa fa-cog fa-lg" aria-hidden="true"></i></a>
              	<ul class="subMenu">
              	<li><a href="/acads/changeConfigurationForm">Set up Dates</a></li>
				<li><a href="/acads/loginAsForm">Acads: Log In As</a></li>
				<li><a href="/acads/eventForm">Set Key Events</a></li>
				<li><a href="/studentportal/generateRegistrationLinks">Generate Post Registrations Links</a></li>
                 <li><a href="/exam/examRegistrationChecklist">Executive Exam Reg CheckList</a></li>
                  </ul>
                </li>  
            <%} %> 