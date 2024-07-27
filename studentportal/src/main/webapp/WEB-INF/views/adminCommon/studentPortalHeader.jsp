<script src="assets/js/jquery-1.11.3.min.js"></script>

<%
String roles = request.getParameter("roles");
%>
          
<%if(roles.indexOf("Portal Admin") != -1 || roles.indexOf("Information Center") != -1){ 	%>
							
	<li class="has-sub-menu"> <a href="#" title="Manage Users"><i class="fa fa-users fa-lg" aria-hidden="true"></i></a>
	   <ul class="sz-sub-menu">
	     <li><a href="/studentportal/changeUserPassword">Change User Password</a></li>
	      <%if(roles.indexOf("Portal Admin") != -1){ 	%>
		<li><a href="/studentportal/createUserForm">Create Users in LDAP</a></li>
		<li><a href="/studentportal/createSingleUserForm">Create Single User</a></li>
		<li><a href="/studentportal/searchUserAuthorizationForm">User Authorization</a></li>
	   	<%} %>
	   </ul>
	 </li>
<%} %>

<% if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1 ) { %>	
	<li class="has-sub-menu">
		<a href="#" title="Announcements">
			<i class="fa fa-bullhorn fa-lg"></i>
		</a>
		<ul class="sz-sub-menu">
			<li><a href="/studentportal/addAnnouncementForm">Create Announcements</a></li>
			<li><a href="/studentportal/getAllAnnouncements">View All Announcements</a></li>
		</ul>
	</li>
<% } %>	

<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ 	%>
	<li class="has-sub-menu">
     	<a href="#" title="Email Notifications"><i class="fa fa-paper-plane fa-lg" aria-hidden="true"></i></a>
         <ul class="sz-sub-menu">
         	<li><a href="/studentportal/emailTemplate">Email Template </a></li>
         	<li><a href="/studentportal/sendEmailFromExcelForm">Send Emails to Excel List</a></li>
         	<li><a href="/studentportal/sendEmailToStudentGroupForm">Send Emails based on Groups</a></li>
         	<li><a href="/studentportal/downloadFeedBacks">Download FeedBack Report</a></li><!-- Newly added by Vikas for download excel -->
         </ul>
	</li>


<%} %>
	<%if(roles.indexOf("Admin")!=-1) { %>
<li class="has-sub-menu">
             	<a href="#" title="MDM">MDM</a>
                 <ul class="sz-sub-menu">
                	 <li><a href="/exam/consumerTypeForm">Consumer Type Entries</a></li>
                	 <li><a href="/exam/programStructureForm">Program Structure Entries</a></li>
                	 <li><a href="/exam/programTypeForm">Program Type Entries</a></li>
                	 <li><a href="/exam/specializationTypeForm">Specialization Entries</a></li>
                	 <li><a href="/exam/programForm">Program Entries</a></li>
                	 <li><a href="/exam/consumerProgramStructureMappingForm">Consumer Program Structure Mapping</a></li>  
                	 <li><a href="/exam/programDetailsForm">Program Details Entries</a></li>
                	 <li><a href="/exam/mdmSubjectCodeForm">Subject Data Entries</a></li>
                 	 <li><a href="/exam/mdmSubjectCodeMappingForm">Program Subject Entries</a></li>
                 	 
                 	 <li><a href="/exam/addBatchDetailsForm">Add Batch Details</a></li>
                 	 <li><a href="/exam/addSubjectDateForm">Add TimeBound Date/Student/Faculty</a></li>
                 	 <li><a href="/exam/addTimeBoundStudentMappingForm">Add Student Mapping</a></li>
                 	 <li><a href="/exam/moveStagingToTimeBoundTableForm">Move TimeBound Staging</a></li>
                 	 <li><a href="/exam/addStudentCourseMappingForm">Add Student Course Mapping</a></li>
                 	 <li><a href="/exam/admin/failedSubjectCriteriaForm">Failed Subject Criteria Entries</a></li>                    
                 </ul>
	</li>

<%} %>	
	
		
<%if(roles.indexOf("Admin") != -1 || roles.indexOf("SR Admin") != -1){ 	%>

	<li class="has-sub-menu">
                   	<a href="#">SR</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/searchSRForm">Search Service Request</a></li>
                       	<li><a href="/studentportal/massUpdateSRStatusForm">Mass Update SR Status</a></li>
                       	<li><a href="/studentportal/searchSRHistoryForm">Search Service Request History</a></li>
                       	<li><a href="/studentportal/changeConfigurationForm">Set up Service Request Dates</a></li>
                       	
                       	<%if(roles.indexOf("Admin") != -1 || roles.indexOf("SR Admin") != -1 || roles.indexOf("Exam Admin") != -1){ 	%>
                       	<li><a href="/studentportal/addStudentExtendedSRTimeForm">Extend Service Request Time </a></li>
                       	
                       	<li><a href="/studentportal/printWithdrawalForm">Print Mass Withdrawal Form</a></li>
                       	<%} %>
                       </ul>
	</li>

<%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1
 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ %>
	<li class="has-sub-menu">
                   	<a href="#">Service Request</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/searchSRForm">Search Service Request</a></li>
                       	<li><a href="/studentportal/searchSRHistoryForm">Search Service Request History</a></li>
                       </ul>
	</li>
<%}else if(roles.indexOf("Finance") != -1){%>
	<li class="has-sub-menu">
                   	<a href="#">Refund Amount</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/refundPaymentForm">Refund Payments</a></li>
                       	<li><a href="/studentportal/walletTransactionReportForm">Generate Wallet Report</a></li>
                       </ul>
	</li>
	<li class="has-sub-menu">
                   	<a href="#">Service Request</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/searchSRForm">Search Service Request</a></li>
                       </ul>
	</li>
<%}else if(roles.indexOf("Finance") != -1){ %>
<li class="has-sub-menu">
                   	<a href="#">Refund Amount</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/refundSRForm">Refund SR Amount</a></li>
                       </ul>
	</li>
	<%} %>
	
	<%if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ %>
	<li class="has-sub-menu">
                   	<a href="#" title="Important Doc">Important Documents</a>
                   <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/trainingDocsICLC?viewer=All">Training Documents</a></li>
                       	<li><a href="/studentportal/modelAssignmentsICLC">Model Assignments</a></li>
                       	  	<li><a href="/studentportal/admin/demoExamICLC">Demo Exams</a></li>
                       	  	  	  	<li><a href="/studentportal/faqICLC?viewer=All">FAQ's</a></li>
                       	  	  	  		<%if(roles.indexOf("SAS Team") != -1 ){ %>
		<li class="has-sub-menu" ><a href="#" title="SAS" id="main">SAS</a>
			<ul class="sz-sub-menu" id ="sas">
				<li><a href="/studentportal/trainingDocsICLC?viewer=SAS">Training
						Documents</a></li>
				<li><a href="/studentportal/demoLecturesICLC">Demo
						Lectures</a></li>
				<li><a href="/studentportal/mockCallsICLC"> Mock calls</a></li>
				<li><a href="/studentportal/faqSAS">FAQ's</a></li>
				<li><a href="/studentportal/callRecordingsICLC?viewer=SAS">Call recordings</a></li>
				<li><a href="/studentportal/webinar">Webinar</a></li>
			</ul></li>
	<%} %>
                       	  	  	  	
                       </ul>    
	</li><%}%>
	
	
	 <%if(roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Settings"><i class="fa fa-cog fa-lg" aria-hidden="true"></i></a>
                      <ul class="sz-sub-menu">
                     	
                     	 <li><a href="/studentportal/admin/makeSurveyLiveForm">Make Survey Live Settings</a></li>
                     	
						                     
                      </ul>
				</li>
               <%} %>  
               
          <li class=""> 
          	<a href="/ltidemo/FacultyTimeline">
          	<p>EMBA</p>
            </a>
          </li>      
          
            <%if(roles.indexOf("MBA-WX Admin") != -1 || roles.indexOf("Learning Center") != -1){ %>
        		<li class="has-sub-menu">
                  	<a href="#" title="EMBA Reports">EMBA Reports</a>
                   	<ul class="sz-sub-menu">
                    	<li><a href="/acads/searchAttendanceFeedbackForMbaWxForm">Session Attendance For Mba Wx Report</a></li>
						<li><a href="/exam/embaPassFailReportForm">EMBA PassFail Report</a></li>
						<li><a href="/exam/consolidatedStudentsWiseReportsForm">Consolidated Students Wise Reports</a></li>
                    	<li><a href="/exam/searchIaToEvaluateForm">IA Evaluates Reports</a></li> 
				   	</ul>
				</li>
			  <%} %>
			 <%if(roles.indexOf("Faculty") == -1 ){ %>
			<li class="has-sub-menu">
          		<a href="#" title="EMBA Reports">Leads</a>
          			<ul class="sz-sub-menu">
	          			<%if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1){%>
				   			<li><a href="/studentportal/admin/leadReport">Lead Report </a></li>
	                    <% } %>
				   		<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support") != -1 || 
				   		roles.indexOf("Consultant") != -1 || roles.indexOf("MBA-WX Admin") != -1 ){ %>
				   		
				   			<li><a href="/studentportal/admin/leadReport">Lead Report </a></li>
				   			<li><a href="/exam/admin/viewAllTestsForLeads">Lead Search Test</a></li>
					   		<li><a href="/exam/admin/addTestForLeadsForm">Lead Add Test</a></li>
	                    	<li><a href="/exam/admin/makeTestLiveFormForLeads">Lead Make Test Live</a></li>
	                    	
				   		<% } %>
				   	</ul>
				</li>
			  <%}%>
	<script>	
 $(document).ready(function () {
	
	 $("#sas").hide();
    $("#main").click(function(){
      $("#sas").toggle();
    });

}); 
</script>
           