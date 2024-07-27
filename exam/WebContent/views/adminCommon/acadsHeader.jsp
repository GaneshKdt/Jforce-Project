<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%
	try{
%>
<%

String roles = request.getParameter("roles");

boolean isStukentApplicable = false;
try{
	 isStukentApplicable = (boolean)request.getSession().getAttribute("isStukentApplicable");
}catch(Exception e){
		isStukentApplicable = false;
}

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
	       <li><a href="/acads/addScheduledSessionForm">Direct Session Creation</a></li>
	       <li><a href="/acads/autoScheduleForm">Create Webinar Links</a></li>
	      <li><a href="/acads/searchScheduledSessionForm">Search Generated Schedule</a></li>
	      <li><a href="/acads/setSessionCalenderDatesForm">Set Session Calender Dates</a></li>
	      <li><a href="/acads/uploadScheduleSessionForCorporateForm">Upload Schedule Session For Corporate</a></li> 
	      <li><a href="/acads/uploadScheduleSessionForSASForm">Upload Schedule Session For SAS</a></li>
	      <li><a href="/acads/batchSessionSchedulingForm">Upload Session By Location</a></li>
      </ul>
    </li>  
    <%} %>  
    <%if(roles.indexOf("Watch Videos Access") != -1){ %>    
     <li class="has-sub-menu">
     	<a href="#">Session Set Up</a>
  		<ul class="sz-sub-menu">
	      <li><a href="/acads/searchScheduledSessionForm">Search Generated Schedule</a></li>
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

<%--    	<% if( roles.indexOf("Insofe") == -1 ) { %> --%>
   	<a href="#" title="Academic Calendar"><i class="fa-solid fa-calendar-days fa-lg" ></i></a>
<%--    	<% } %> --%>
 	<ul class="sz-sub-menu">
 	
  	<%if(roles.indexOf("Acads Admin") != -1 ){ %>
      <li><a href="/acads/admin/viewTimeTable">Academic Calendar</a></li>
      <%} %> 
      
      <%if(roles.indexOf("Acads Coordinator") != -1 || roles.indexOf("Learning Center") != -1 
      || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
      <li><a href="/acads/admin/viewCompleteTimeTable">Academic Calendar</a></li>
      <%} %> 
      
      <%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1  || roles.indexOf("Insofe") != -1 || roles.indexOf("Acads Coordinator") != -1){ %> 
      <li><a href="/acads/admin/viewFacultyTimeTable">Faculty Calendar</a></li>
      <%} %> 
      
    <%--   <%if(!(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Coordinator") != -1)){%> 
      <li><a href="/acads/viewStudentTimeTable">Student Academic Calendar</a></li>
      <%} %>  --%>
     
     </ul>
   </li> 
     
   	<%if(roles.indexOf("Watch Videos Access") != -1 || roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1  || roles.indexOf("Insofe") != -1 
   	|| roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>    
	 <li  class="has-sub-menu">
   		<a href="#">Session Videos </a>
   		<ul class="sz-sub-menu">
 			<%if(roles.indexOf("Acads Admin") != -1   || roles.indexOf("Exam Admin") != -1){ %>
 			<li>
 				<a href="/acads/admin/uploadVideoContentForm">Upload Session Videos </a>
 			</li>
 			<li>
 				<a href="/acads/admin/sessionRecordingPanel">Recording Status</a>
 			</li>
 		     <%} %>
 		     <%if(roles.indexOf("Exam Admin") == -1 ){ %>
 			<li>
 				<a href="/acads/admin/videosHome?pageNo=1&academicCycle=All">Watch All Videos </a>
 			</li>
 			<%} %>
 			
 		</ul>
 	</li>
     <%} %>
     
	<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1   || roles.indexOf("Insofe") != -1 
	|| roles.indexOf("Learning Center") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>    
	    <li class="has-sub-menu">
	    <a href="#" title="Content"><i class="fa-solid fa-book fa-lg"></i></a>
	  	<ul class="sz-sub-menu">
	  	<%if(roles.indexOf("Acads Admin") != -1 ){ %>
	       <li><a href="/acads/admin/uploadContentForm">Upload Content</a></li>
	       <li><a href="/acads/admin/uploadContentFormForLeads">Upload Content For Leads</a></li>
	       <li><a href="/acads/admin/viewAllSubjectsForContent">View All Content</a></li>
	       <li><a href="/acads/admin/transferContentForm">Transfer Content</a></li>
	       
	       <li><a href="/forum/admin/createForumThreadForm">Create Forum</a></li>
	       <li><a href="/forum/admin/searchForumThreadForm">Search Forum</a></li>
	       
	       <li><a href="/studentportal/uploadLearningResourcesExcelForm">Manage Learning Resources</a></li>
	         <li><a href="/exam/readSifyDataForm">Add Sify Data</a></li>
	       <li><a href="/exam/viewSifyMarks">View Sify Data</a></li>
	    <%} %>
	    
	    <%if(roles.indexOf("Acads Coordinator") != -1){ %>
	    <li><a href="/acads/admin/viewAllSubjectsForContent">View All Content</a></li>
	    <%} %>
	    

	   <%if(roles.indexOf("Faculty") != -1 || roles.indexOf("Insofe") != -1  ){ %>
	       <li><a href="/acads/admin/viewApplicableSubjectsForFacultyForm">Learning Resources</a></li>

	      
	       <li><a href="/forum/admin/createForumThreadForm">Create Forum</a></li>
	       <li><a href="/forum/admin/searchForumThreadForm">Search Forum</a></li>
	       
	       
	       <li><a href="/studentportal/uploadLearningResourcesExcelForm">Manage Learning Resources</a></li>
	     <!--   <li><a href="/acads/uploadContentForm">Upload Content</a></li>
	       <li><a href="/acads/uploadContentFormForLeads">Upload Content For Leads</a></li> -->
	       
	        
	   <%} %>
	   
	   <%if(roles.indexOf("Learning Center") != -1){ %>
	       <li><a href="/acads/admin/viewAllSubjectsForContent">Learning Resources</a></li>
	   <%} %>
	     
	      </ul>
	    </li> 
         
	<%} %>
    
   	<%if(  roles.indexOf("Faculty") != -1 || roles.indexOf("Insofe") != -1  ){ %>    
	 <li  class="has-sub-menu">
   		<a href="#">Internal Assignemnt </a>
   		<ul class="sz-sub-menu">
 			<li>
 				<a href="/exam/viewTestsForFaculty">Allocated Assignments </a>
 			</li>
 			
 		</ul>
 	</li>
     <%} %>
         
   	<%if(  roles.indexOf("Faculty") != -1 || roles.indexOf("Insofe") != -1  ){ %>    
	 <li  class="has-sub-menu">
   		<a href="#">MBA-X Internal Assignemnt </a>
   		<ul class="sz-sub-menu">
 			<li>
 				<a href="/exam/mbax/ia/a/viewTestsForFaculty">Allocated Assignments </a>
 			</li>
 			
 		</ul>
 	</li>
     <%} %>
     
     <%if(  roles.indexOf("Faculty") != -1 && isStukentApplicable == true){ %>  
		 <li  class="has-sub-menu">
	   		<a href="#">E-Learn </a>
	   		<ul class="sz-sub-menu">
	 			<li>
	 				<a href="/acads/viewELearnResources">Stukent Books</a>
	 			</li>
	 		</ul>
	 	</li>
     <%} %>
     
   <%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
    <li class="has-sub-menu">
    <a href="#" >Faculty</a>
  	<ul class="sz-sub-menu">
      <li><a href="/acads/admin/addFacultyForm">Add Faculty</a></li>
      <li><a href="/acads/admin/searchFacultyForm">Search Faculty</a></li>
      <li><a href="/acads/admin/uploadFacultyCourseForm">Faculty Learning Resource Access</a></li>
      </ul>
    </li> 
   <%} %> 
    
		<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ %>
		   <li class="has-sub-menu">
		   <a href="#" title="Acads Reports"><i class="fa-solid fa-chart-column fa-lg" ></i></a>
		 	<ul class="sz-sub-menu">
		 	 <%if(roles.indexOf("Acads Admin") != -1 ){ %>
		 	  <li><a href="/acads/searchCommonSessionForm">Common Session Report</a></li>
		      <li><a href="/acads/admin/searchAttendanceFeedbackForm">Attendance, Feedback Report</a></li>
		      <li><a href="/acads/admin/searchAttendanceFeedbackForMbaWxForm">Session Attendance For Mba Wx Report</a></li>  
		      
<!-- 		      Commented By Somesh same report available in  Post Query Report tab -->
<!-- 		      <li><a href="/acads/searchQueriesForm">Query Report</a></li> -->
		      <li><a href="/studentportal/admin/downloadAdHocPaymentReport">AdHoc Payments Report</a></li>
		       <li><a href="/acads/admin/searchFacultyReviewForm">Faculty Review Report</a></li>
		       
		      <%} %>
		      <%if(roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
		      <li><a href="/acads/admin/searchAttendanceFeedbackForm">Attendance, Feedback Report</a></li>
		       <li><a href="/acads/admin/searchAttendanceFeedbackForMbaWxForm">Session Attendance For Mba Wx Report</a></li>  
		      
		      <%} %>

		      <li><a href="/acads/admin/pcpRegistrationReportForm">PCP Bookings Report</a></li>
		      <li><a href="/acads/admin/queryTATReportPage">Post Query Report</a></li>
		      <li><a href="/acads/admin/feedPostsReportForm">MBA WX Feeds/Comments Report</a></li>
		      <li><a href="/exam/electiveReportForm">MBA WX Electives Report</a></li>
			  <%if(roles.indexOf("Information Center")!=-1||roles.indexOf("Learning Center")!=-1||roles.indexOf("Student Support")!=-1){ %>
			  <li><a href="/studentportal/admin/louReportForm">LOU Report</a></li>
			  <%} %>
		      <%if(roles.indexOf("Acads Admin") != -1){ %> 
		               <li><a href="/acads/admin/zoomTimeStampReport">Session Time Stamp Report</a></li>
		       <%} %>
		      <!--  -->
		     <%--  <li  <% if(roles.indexOf("Learning Center") != -1){ %>style="display:none;" <%} %>><a href="/exam/generateExecutivePasswordForm">Executive Students Report</a></li> --%>
		     </ul>
		   </li> 
		<%} %> 
 
		<%if(roles.indexOf("Acads Admin") != -1 ){ %>
             <li class="has-sub-menu">
                <a href="#" title="Settings"><i class="fa-solid fa-gear fa-lg" aria-hidden="true"></i></a>
              	<ul class="sz-sub-menu">

              	<li><a href="/acads/admin/changeConfigurationForm">Set up Dates</a></li> 
              	<li><a href="/exam/admin/makeAcadDetailsLiveForm">Make Live</a></li>
				<li><a href="/acads/loginAsForm">Acads: Log In As</a></li>
				<li><a href="/acads/admin/eventForm">Set Key Events</a></li>
				<li><a href="/studentportal/generateRegistrationLinks">Generate Post Registrations Links</a></li>
                <li><a href="/exam/examExecutiveRegistrationChecklist">Executive Exam Reg CheckList</a></li>
                <li><a href="/exam/examRegularRegistrationChecklist">Regular Exam Reg Checklist</a></li>
                
                </ul>
                </li>  
            <%} %> 
            
           <%if(roles.indexOf("Acads Admin") != -1 ){ %>
         <li class="has-sub-menu">
                  	<a href="#" title="Internal Assessment"><i class="fa-solid fa-flask fa-lg" aria-hidden="true"></i></a>
                      <ul class="sz-sub-menu">
                      	 <li><a href="/exam/makeTestLiveForm">Make Tests Live</a></li>
                     	 <li><a href="/exam/addTestForm">Add Test</a></li>
                     	 <li><a href="/exam/viewAllTests">Search Tests</a></li>
                     		<li><a href="/exam/consolidatedStudentsWiseReportsForm">Consolidated Students Wise Reports</a></li>
                     	<li><a href="/internal-assessment/searchIaToEvaluateForm">IA Evaluates Reports</a></li>
                     		
                     	 
                      </ul>
				</li>
            <%} %> 
            
 <%
	}catch(Exception e){
		
	}
%>