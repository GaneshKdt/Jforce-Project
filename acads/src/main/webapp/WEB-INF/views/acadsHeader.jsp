<%

String roles = request.getParameter("roles");

%>

<%if(roles.indexOf("Acads Admin") != -1 ){ %>
     <li>
     	<a href="#">Session Set Up</a>
  	<ul class="subMenu">
       <li><a href="/acads/admin/uploadFacultyAvailabilityForm">Upload Faculty Availability</a></li>
       <li><a href="/acads/admin/searchFacultyAvailabilityForm">Search Faculty Availability</a></li>
       <li><a href="/acads/admin/uploadCourseFacultyMappingForm">Upload Course Faculty Mapping</a></li>
       <li><a href="/acads/admin/searchCourseFacultyMappingForm">Search Course Faculty Mapping</a></li>
       <li><a href="/acads/admin/uploadSessionDayTimeForm">Upload Session Day/Time</a></li>
       <li><a href="/acads/admin/searchSessionDayTimeForm">Search Session Day/Time</a></li>
       <li><a href="/acads/admin/addScheduledSessionForm">Direct Session Creation</a></li>
       <li><a href="/acads/admin/autoScheduleForm">Generate Schedule Zoom</a></li>
      <li><a href="/acads/admin/searchScheduledSessionForm">Search Generated Schedule</a></li>
      <li><a href="/acads/admin/setSessionCalenderDatesForm">Set Session Calender Dates</a></li>
      <li><a href="/acads/admin/uploadSessionReviewFacultyMappingForm">Upload Session Review Faculty Mapping </a></li>
      <!-- <li><a href="/acads/searchFacultyReviewForm">Search Session Review Faculty </a></li> -->
       <li><a href="/acads/admin/batchSessionSchedulingForm">Upload Session By Location</a></li>
      </ul>
    </li>  
    <%} %>  
        <%if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Student Support") != -1 ){ %>
     <li>
     	<a href="#">Session Set Up</a>
  	<ul class="subMenu">
     	
	       
	      <li><a href="/acads/admin/searchScheduledSessionForm">Search Generated Schedule</a></li>
	      
      </ul>
    </li>  
    <%} %>
     
  
  <li class="">
   	<a href="#" title="Academic Calendar"><i class="fa fa-calendar fa-lg" ></i></a>
 	<ul class="subMenu">
 	
  	<%if(roles.indexOf("Acads Admin") != -1 ){ %>
      <li><a href="/acads/admin/viewTimeTable">Academic Calendar</a></li>
      <%} %> 
      
      <%if(roles.indexOf("Acads Coordinator") != -1 || roles.indexOf("Learning Center") != -1 
      || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
      <li><a href="/acads/admin/viewCompleteTimeTable">Academic Calendar</a></li>
      <%} %> 
      
      <%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1){ %> 
      <li><a href="/acads/admin/viewFacultyTimeTable">Faculty Calendar</a></li>
      <%} %> 
      
    <%--   <%if(!(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Coordinator") != -1)){%> 
      <li><a href="/acads/viewStudentTimeTable">Student Academic Calendar</a></li>
      <%} %>  --%>
     
     </ul>
   </li> 
     

   	<%if(roles.indexOf("Watch Videos Access") != -1 || roles.indexOf("Acads Admin") != -1  || roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>    
	 <li  class="">
   		<a href="#">Session Videos </a>
   		<ul class="subMenu">
 			<li>
 				<a href="/acads/admin/uploadVideoContentForm">Upload Session Videos </a>
 			</li>
 			<li>
 				<a href="/acads/admin/videosHome?pageNo=1&academicCycle=All">Watch All Videos </a>
 			</li>
 			<li>
 				<a href="/acads/admin/sessionRecordingPanel">Recording Status</a>
 			</li>
 			<li>
 				<a href="/acads/admin/searchSessionRecordingForm">Search Session Video Report</a>
 			</li>
 		</ul>
 	</li>
     <%} %>
     
     <%if(roles.indexOf("Acads Admin") != -1  || roles.indexOf("Exam Admin") != -1  || roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>    
	 <li  class="">
   		<a href="#">Session Plan </a>
   		<ul class="subMenu">
 			
 			<li>
 				<a href="/acads/admin/manageSessionPlan">Manage Session Plan </a>
 			</li>
 			
 		</ul>
 	</li>
     <%} %>
     
<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1  || roles.indexOf("Learning Center") != -1 ){ %>    
    <li>
    <a href="#" title="Content"><i class="fa fa-book fa-lg"></i></a>
  	<ul class="subMenu">
  	<%if(roles.indexOf("Acads Admin") != -1 ){ %>
	   <li><a href="/acads/admin/makeContentLiveForm">Make Content Live By Program Config</a></li>
       <li><a href="/acads/admin/uploadContentForm">Upload Content</a></li>
       <li><a href="/acads/admin/viewAllSubjectsForContent">View All Content</a></li>
       <li><a href="/acads/admin/transferContentForm">Transfer Content</a></li>
       <li><a href="/acads/createForumThreadForm">Create Forum</a></li>
	   <li><a href="/acads/searchForumThreadForm">Search Forum</a></li>
	   <li><a href="/studentportal/uploadLearningResourcesExcelForm">Manage Learning Resources</a></li>
	   <li><a href="/acads/admin/searchContentForm">Search/Edit Content</a></li>
	   <li><a  href="/acads/admin/adhoc-upload-file-form">Upload File & Generate Link</a></li>
	       
    <%} %>
    
    
    
   <%if(roles.indexOf("Faculty") != -1 ){ %>
       <li><a href="/acads/admin/viewApplicableSubjectsForFacultyForm">Learning Resources</a></li>
       <li><a href="/acads/admin/viewReviewForFacultyForm?action=edit">Review Faculty For Session</a></li>
	   <li><a href="/acads/admin/viewReviewForFacultyForm?action=view">My Session Review</a></li>
       <li><a href="/acads/admin/createForumThreadForm">Create Forum</a></li>
	   <li><a href="/acads/admin/searchForumThreadForm">Search Forum</a></li>
	   
	       <li><a href="/studentportal/uploadLearningResourcesExcelForm">Manage Learning Resources</a></li>
		   <li><a href="/studentportal/gotoEZProxy" target="_blank">Digital Library</a></li>
	       
   <%} %>
   
   <%if(roles.indexOf("Learning Center") != -1){ %>
       <li><a href="/acads/admin/viewAllSubjectsForContent">Learning Resources</a></li>
   <%} %>
     
      </ul>
    </li> 
         
<%} %>
    
   <%if(roles.indexOf("Acads Admin") != -1 ){ %>
    <li>
    <a href="#" >Faculty</a>
  	<ul class="subMenu">
      <li><a href="/acads/admin/addFacultyForm">Add Faculty</a></li>
      <li><a href="/acads/admin/addFacultyBulkUploadForm">Add Faculty Bulk Upload</a></li>
      <li><a href="/acads/admin/searchFacultyForm">Search Faculty</a></li>
      <li><a href="/acads/admin/uploadFacultyCourseForm">Faculty Learning Resource Access</a></li>
      
      </ul>
    </li> 
   <%} %> 
    
<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
   <li>
   <a href="#" title="Acads Reports"><i class="fa fa-bar-chart fa-lg" ></i></a>
 	<ul class="subMenu">
 	 <%if(roles.indexOf("Acads Admin") != -1 ){ %>
 	 	<li><a href="/acads/admin/searchCommonSessionForm">Common Session Report</a></li>
      	<li><a href="/acads/admin/searchAttendanceFeedbackForm">Attendance, Feedback Report</a></li>
       	<li><a href="/acads/admin/searchAttendanceFeedbackForMbaWxForm">Session Attendance For Mba Wx Report</a></li>  
		    
      	<li><a href="/acads/admin/searchQueriesForm">Query Report</a></li>
       	<li><a href="/acads/searchStudentZoneLoginsForm">StudentZone Login Report</a></li>
       	<li><a href="/acads/admin/searchFacultyReviewForm">Faculty Review Report</a></li>
      <%} %>
       <%if(roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
      	<li><a href="/acads/admin/searchAttendanceFeedbackForm">Attendance, Feedback Report</a></li>
       <li><a href="/acads/admin/searchAttendanceFeedbackForMbaWxForm">Session Attendance For Mba Wx Report</a></li>  
		    
      <%} %>
      <li><a href="/acads/admin/pcpRegistrationReportForm">PCP Bookings Report</a></li>
     </ul>
   </li> 
<%} %> 

<%if(roles.indexOf("MBA-WX Admin") != -1 || roles.indexOf("Learning Center") != -1){ %>
        		<li>
                  	<a href="#" title="EMBA Reports">EMBA Reports</a>
                   	<ul class="subMenu">
                    	<li><a href="/acads/admin/searchAttendanceFeedbackForMbaWxForm">Session Attendance For Mba Wx Report</a></li>
						<li><a href="/exam/admin/embaPassFailReportForm">EMBA PassFail Report</a></li>
						<li><a href="/exam/consolidatedStudentsWiseReportsForm">Consolidated Students Wise Reports</a></li>
                    	<li><a href="/exam/searchIaToEvaluateForm">IA Evaluates Reports</a></li> 
				   	</ul>
				</li>
			  <%} %>   
 
<%if(roles.indexOf("Acads Admin") != -1 ){ %>
             	  <li>
                <a href="#" title="Settings"><i class="fa fa-cog fa-lg" aria-hidden="true"></i></a>
              	<ul class="subMenu">
              	<li><a href="/acads/admin/changeConfigurationForm">Set up Dates</a></li>
				<li><a href="/acads/loginAsForm">Acads: Log In As</a></li>
				<li><a href="/acads/admin/eventForm">Set Key Events</a></li>
				<li><a href="/studentportal/generateRegistrationLinks">Generate Post Registrations Links</a></li>
				<li><a href="/exam/examExecutiveRegistrationChecklist">Executive Exam Reg CheckList</a></li>
				  <li><a href="/exam/examRegularRegistrationChecklist">Regular Exam Reg Checklist</a></li>
                  </ul>
                </li>  
            <%} %> 
            
            <%if(roles.indexOf("Faculty") != -1){%>
          <li  class="has-sub-menu">
   				<a href="#">Session Videos </a>
   				<ul class="sz-sub-menu">
   					<li>
 						<a href="/acads/admin/videosHome?pageNo=1&academicCycle=All">Watch All Videos </a>
 					</li>
   				
   				</ul>
 		 </li>
          <%}%>

