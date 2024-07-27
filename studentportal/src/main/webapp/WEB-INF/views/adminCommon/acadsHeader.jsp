<%

String roles = request.getParameter("roles");
String isStukentLtiRoles = (String)request.getSession().getAttribute("isStukentLtiRoles_studentportal");
String isStukentLtiProviderName = (String)request.getSession().getAttribute("isStukentLtiProviderName_studentportal");
int isStukentLtiUserIdCount = (int)request.getSession().getAttribute("isStukentLtiUserIdCount_studentportal");

String isHarvardLtiRoles = (String)request.getSession().getAttribute("isHarvardLtiRoles");
String isHarvardLtiProviderName = (String)request.getSession().getAttribute("isHarvardLtiProviderName");
int isHarvardLtiUserIdCount = (int)request.getSession().getAttribute("isHarvardLtiUserIdCount");


%>


	<%if(roles.indexOf("Acads Admin") != -1 ){ %>
     <li class="has-sub-menu">
     	<a href="#">Session Set Up</a>
  		<ul class="sz-sub-menu">
	       <li><a href="/acads/uploadFacultyAvailabilityForm">Upload Faculty Availability</a></li>
	       <li><a href="/acads/searchFacultyAvailabilityForm">Search Faculty Availability</a></li>
	       <li><a href="/acads/uploadCourseFacultyMappingForm">Upload Course Faculty Mapping</a></li>
	       <li><a href="/acads/searchCourseFacultyMappingForm">Search Course Faculty Mapping</a></li>
	       <li><a href="/acads/uploadSessionDayTimeForm">Upload Session Day/Time</a></li>
	       <li><a href="/acads/searchSessionDayTimeForm">Search Session Day/Time</a></li>
	       <li><a href="/acads/addCommonSessionForm">Common Session Creation</a>
	       <li><a href="/acads/addScheduledSessionForm">Direct Session Creation</a></li>
	       <li><a href="/acads/autoScheduleForm">Create Webinar Links</a></li>
	      <li><a href="/acads/searchScheduledSessionForm">Search Generated Schedule</a></li>
	      <li><a href="/acads/setSessionCalenderDatesForm">Set Session Calender Dates</a></li>
	      <li><a href="/acads/uploadScheduleSessionForCorporateForm">Upload Schedule Session For Corporate</a></li>
	      <li><a href="/acads/admin/uploadSessionReviewFacultyMappingForm">Upload Session Review Faculty Mapping </a></li>
	      <li><a href="/acads/uploadScheduleSessionForSASForm">Upload Schedule Session For SAS</a></li>
	      <li><a href="/acads/batchSessionSchedulingForm">Upload Session By Location</a></li>
	      <li><a href="/acads/schedulePcpBooking">Schedule Bridge Booking</a></li> 
      </ul>
    </li>  
    <%} %>  
    <%if(roles.indexOf("Watch Videos Access") != -1){ %>    
     <li class="has-sub-menu">
     	<a href="#">Session Set Up</a>
  		<ul class="sz-sub-menu">
	      <li><a href="/acads/searchScheduledSessionForm">Search Generated Schedule</a></li>
	      <li><a href="/acads/searchCommonSessionForm">Search Common Session</a></li>
      </ul>
    </li>  
    <%} %> 
    <%if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Student Support") != -1 ){ %>
     <li class="has-sub-menu">
     	<a href="#">Session Set Up</a>
  		<ul class="sz-sub-menu">
	       
	      <li><a href="/acads/searchScheduledSessionForm">Search Generated Schedule</a></li>
	      
      </ul>
    </li>  
    <%} %> 
  
  <li  class="has-sub-menu">
   	<a href="#" title="Academic Calendar"><i class="fa fa-calendar fa-lg" ></i></a>
 	<ul class="sz-sub-menu">
 	
  	<%if(roles.indexOf("Acads Admin") != -1 ){ %>
      <li><a href="/acads/viewTimeTable">Academic Calendar</a></li>
      <%} %> 
      
      <%if(roles.indexOf("Acads Coordinator") != -1 || roles.indexOf("Learning Center") != -1 
      || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
      <li><a href="/acads/viewCompleteTimeTable">Academic Calendar</a></li>
      <%} %> 
      
      <%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Coordinator") != -1){ %> 
      <li><a href="/acads/viewFacultyTimeTable">Faculty Calendar</a></li>
      <%} %> 
      
    <%--   <%if(!(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Coordinator") != -1)){%> 
      <li><a href="/acads/viewStudentTimeTable">Student Academic Calendar</a></li>
      <%} %>  --%>
     
     </ul>
   </li> 
   	
   	<%if(roles.indexOf("Watch Videos Access") != -1 || roles.indexOf("Acads Admin") != -1 || roles.indexOf("Assignment Admin") != -1  || roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>    
	 <li  class="has-sub-menu">
   		<a href="#">Session Videos </a>
   		<ul class="sz-sub-menu">
 			<%if(roles.indexOf("Watch Videos Access") == -1){ %>
 			<li>
 				<a href="/acads/uploadVideoContentForm">Upload Session Videos </a>
 			</li>
 		     <%} %>
 			<li>
 				<a href="/acads/videosHomeAdmin?pageNo=1&academicCycle=All">Watch All Videos </a>
 			</li>
 			<li>
 				<a href="/acads/sessionRecordingPanel">Recording Status</a>
 			</li>
 			<li>
 				<a href="/acads/searchSessionRecordingForm">Search Session Video Report</a>
 			</li>
 		</ul>
 	</li>
     <%} %>
     
   	<%if(roles.indexOf("Assignment Admin") != -1  || roles.indexOf("Acads Admin") != -1  || roles.indexOf("Exam Admin") != -1  || roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>
  	 <li  class="has-sub-menu"> 
   		<a href="#">Session Plan </a>
   		<ul class="sz-sub-menu">
 			
 			<li>
 				<a href="/acads/admin/manageSessionPlan">Manage Session Plan </a>
 			</li>
 			
 		</ul>
 	</li>
     <%} %>
	<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1  || roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>    
	    <li class="has-sub-menu">
	    <a href="#" title="Content"><i class="fa fa-book fa-lg"></i></a>
	  	<ul class="sz-sub-menu">
	  	<%if(roles.indexOf("Acads Admin") != -1 ){ %>
	       <li><a href="/acads/uploadContentForm">Upload Content</a></li>
	       <li><a href="/acads/uploadContentFormForLeads">Upload Content For Leads</a></li>
	       <li><a href="/acads/viewAllSubjectsForContent">View All Content</a></li>
	       <li><a href="/acads/transferContentForm">Transfer Content</a></li>
	       
	       <li><a href="/acads/createForumThreadForm">Create Forum</a></li>
	       <li><a href="/acads/searchForumThreadForm">Search Forum</a></li>
	       
	       <li><a href="/studentportal/uploadLearningResourcesExcelForm">Manage Learning Resources</a></li>
	        
	        
	    <%} %>
	    
	    <%if(roles.indexOf("Acads Coordinator") != -1){ %>
	    <li><a href="/acads/viewAllSubjectsForContent">View All Content</a></li>
	    <%} %>
	    
	   <%if(roles.indexOf("Faculty") != -1 ){ %>
	       <li><a href="/acads/viewApplicableSubjectsForFacultyForm">Learning Resources</a></li>
	       <li><a href="/acads/viewReviewForFacultyForm?action=edit">Review Faculty For Session</a></li>
	       <li><a href="/acads/viewReviewForFacultyForm?action=view">My Session Review</a></li>
	       <li><a href="/acads/createForumThreadForm">Create Forum</a></li>
	       <li><a href="/acads/searchForumThreadForm">Search Forum</a></li>
	       
	       <li><a href="/studentportal/uploadLearningResourcesExcelForm">Manage Learning Resources</a></li>
	      
		   <li><a href="/studentportal/gotoEZProxy" target="_blank">Digital Library</a></li>
	     <!--    <li><a href="/acads/uploadContentForm">Upload Content</a></li>-->
	        
	   <%} %>
	   
	   <%if(roles.indexOf("Learning Center") != -1){ %>
	       <li><a href="/acads/viewAllSubjectsForContent">Learning Resources</a></li>
	   <%} %>
	   
	   
	     
	      </ul>
	    </li> 
         
	<%} %>
	
	
   	<%if(  roles.indexOf("Faculty") != -1 ){ %>    
	 <li  class="has-sub-menu">
   		<a href="#">Internal Assignemnt </a>
   		<ul class="sz-sub-menu">
 			<li>
 				<a href="/exam/viewTestsForFaculty">Allocated Assignments </a>
 			</li>
 			
 		</ul>
 	</li>
     <%} %>
     
      <%if(isStukentLtiUserIdCount != 0 || isHarvardLtiUserIdCount != 0 ){ %>  
		 <li  class="has-sub-menu">
	   		<a href="#">E-Learn </a>
	   		<ul class="sz-sub-menu">
	 			<%if(roles.indexOf("Faculty") != -1 && isStukentLtiUserIdCount != 0) {%>
	 			<li>
	 				<a href="/acads/viewELearnResources?roles=<%= isStukentLtiRoles %>&p_name=<%= isStukentLtiProviderName %>">Stukent Books</a>
	 			</li>
	 			<%} %>
	 			<% if((roles.indexOf("Faculty") != -1  || roles.indexOf("Acads Admin") != -1) && isHarvardLtiUserIdCount != 0 ){ %>	
	 			<li>
	 				<a href="/acads/viewELearnResources?roles=<%= isHarvardLtiRoles %>&p_name=<%= isHarvardLtiProviderName %>">Harvard/Capsim Books</a>
	 			</li>
	 			<%} %>
	 		</ul>
	 	</li>
     <%} %>
    
   <%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
    <li class="has-sub-menu">
    <a href="#" >Faculty</a>
  	<ul class="sz-sub-menu">
      <li><a href="/acads/addFacultyForm">Add Faculty</a></li>
      <li><a href="/acads/searchFacultyForm">Search Faculty</a></li>
      <li><a href="/acads/uploadFacultyCourseForm">Faculty Learning Resource Access</a></li>
      </ul>
    </li> 
   <%} %> 
    
		<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ %>
		   <li class="has-sub-menu">
		   <a href="#" title="Acads Reports"><i class="fa fa-bar-chart fa-lg" ></i></a>
		 	<ul class="sz-sub-menu">
		 	 <%if(roles.indexOf("Acads Admin") != -1){ %> 
		 	 	<li><a href="/acads/searchCommonSessionForm">Common Session Report</a></li>
		 	 <%} %>
		 	 <%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ %>
		      <li><a href="/acads/searchAttendanceFeedbackForm">Attendance, Feedback Report</a></li>
		           <li><a href="/acads/searchAttendanceFeedbackForMbaWxForm">Session Attendance For Mba Wx Report</a></li>  
<!-- 		      Commented By Somesh same report available in  Post Query Report tab -->		      
<!-- 		      <li><a href="/acads/searchQueriesForm">Query Report</a></li> -->
		      <li><a href="/studentportal/downloadAdHocPaymentReport">AdHoc Payments Report</a></li>
		      <li><a href="/acads/admin/searchFacultyReviewForm">Faculty Review Report</a></li>
		      <%} %>
		      <%if(roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("Learning Center") != -1){ %>
		      <li><a href="/acads/searchAttendanceFeedbackForm">Attendance, Feedback Report</a></li>
		      <li><a href="/acads/searchAttendanceFeedbackForMbaWxForm">Session Attendance For Mba Wx Report</a></li>  
		      <%} %>
		      <li><a href="/acads/pcpRegistrationReportForm">Bridge Bookings Report</a></li>
		     <!--  <li><a href="/acads/searchAttendanceFeedbackForm">Attendance, Feedback Report</a></li> -->
		      <li><a href="/acads/admin/queryTATReportPage">Post Query Report</a></li>
		      <li><a href="/acads/feedPostsReportForm">MBA WX Feeds/Comments Report</a></li>
		      <li><a href="/exam/electiveReportForm">MBA WX Electives Report</a></li>
		      
		      <!-- called- -->
		    <!--   <li><a href="/exam/generateExecutivePasswordForm">Executive Students Report</a></li> moved to exam reg checklist-->
		     <li><a href="/studentportal/downloadAcadFeedbackReportForm">Academic Survey Report</a></li>
		      
		     </ul>
		   </li> 
		<%} %> 
 
		<%if(roles.indexOf("Acads Admin") != -1 ){ %>
             <li class="has-sub-menu">
                <a href="#" title="Settings"><i class="fa fa-cog fa-lg" aria-hidden="true"></i></a>
              	<ul class="sz-sub-menu">
              	<li><a href="/acads/changeConfigurationForm">Set up Dates</a></li>
              	<li><a href="/exam/admin/makeAcadDetailsLiveForm">Make Live</a></li>
				<li><a href="/acads/loginAsForm">Acads: Log In As</a></li>
				<li><a href="/acads/eventForm">Set Key Events</a></li>
				<li><a href="/studentportal/generateRegistrationLinks">Generate Post Registrations Links</a></li>
                 <li><a href="/exam/examExecutiveRegistrationChecklist">Executive Exam Reg CheckList</a></li>
                   <li><a href="/exam/examRegularRegistrationChecklist">Regular Exam Reg Checklist</a></li>
                  </ul>
                </li>  
            <%} %> 
            
            <%if(roles.indexOf("Acads Admin") != -1 ){ %>
        <li class="has-sub-menu">
                  	<a href="#" title="Internal Assessment"><i class="fa fa-flask fa-lg" aria-hidden="true"></i></a>
                      <ul class="sz-sub-menu">
                      	 <li><a href="/exam/makeTestLiveForm">Make Tests Live</a></li>
                     	 <li><a href="/exam/addTestForm">Add Test</a></li>
                     	 <li><a href="/exam/viewAllTests">Search Tests</a></li>
                     	 <li><a href="/exam/consolidatedStudentsWiseReportsForm">Consolidated Students Wise Reports</a></li>
                     	 <li><a href="/exam/searchIaToEvaluateForm">IA Evaluates Reports</a></li>
                     	 <li><a href="/exam/admin/studentTestAuditTrailAnalysisForm">Student Test Audit Trail</a></li>
                      </ul>
				</li>
            <%} %> 
            
<!--    added by stef on 6-Nov  -->  
  <%--  
       
         <%  String userId = (String)session.getAttribute("userId");   %>
            
            <%if (roles.indexOf("Faculty")!= -1 || userId.startsWith("77") || userId.startsWith("79")) { %>
            	 <li class="menu">
               	<a href="/acads/studentAttendanceReport"  title="Student Attendance"><i class="fa fa-calendar-check-o fa-lg" ></i></a>
                 </li>
           <%  }%> --%>
		<%if(roles.indexOf("Faculty") != -1){%>
          <li  class="has-sub-menu">
   				<a href="#">Session Videos </a>
   				<ul class="sz-sub-menu">
   					<li>
 						<a href="/acads/videosHomeAdmin?pageNo=1&academicCycle=All">Watch All Videos </a>
 					</li>
   				
   				</ul>
 		 </li>
          <%}%>