<script src="assets/js/jquery-1.11.3.min.js"></script>

<%
String roles = request.getParameter("roles");
%>
          
<%if(roles.indexOf("Portal Admin") != -1 || roles.indexOf("Information Center") != -1){ 	%>
							
	<li class="has-sub-menu"> <a href="#" title="Manage Users"><i class="fa-solid fa-users fa-lg" aria-hidden="true"></i></a>
	   <ul class="sz-sub-menu">
	     <li><a href="/studentportal/changeUserPassword">Change User Password</a></li>
	      <%if(roles.indexOf("Portal Admin") != -1){ 	%>
		<li><a href="/studentportal/admin/createUserForm">Create Users in LDAP</a></li>
		<li><a href="/studentportal/admin/createSingleUserForm">Create Single User</a></li>
		<li><a href="/studentportal/admin/searchUserAuthorizationForm">User Authorization</a></li>
		<li><a href="/studentportal/admin/viewAuthorization">Update User Authorization</a></li>
		
	   	<%} %>
	   </ul>
	 </li>
<%} %>
<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ 	%>
<li class="has-sub-menu"><a href="#" title="Policy">Policy</a>
	<ul class="sz-sub-menu">
		<li><a href="/knowyourpolicy/admin/knowYourPolicy">Policy's</a></li>
		<li><a href="/knowyourpolicy/admin/knowYourPolicyEntry ">Policy Entry</a></li>
		<li><a href="/knowyourpolicy/admin/knowYourPolicyCategory">Policy Category</a></li>
		<li><a href="/knowyourpolicy/admin/knowYourPolicySubCategory">Policy Sub
				Category</a></li>
	</ul></li>
<%} %>
<% if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1  || roles.indexOf("TEE Admin") != -1 ) { %>	
	<li class="has-sub-menu">
		<a href="#" title="Announcements">
			<i class="fa-solid fa-bullhorn fa-lg"></i>
		</a>
		<ul class="sz-sub-menu">
			<li><a href="/announcement/admin/addProgramAnnouncementForm">Create Program Announcements</a></li>
				<li><a href="/announcement/admin/addSubjectAnnouncementForm">Create Subject Announcements</a></li>
				<li><a href="/announcement/admin/addBatchAnnouncementForm">Create Batch Announcements</a></li>
				<li><a href="/announcement/admin/viewAllAnnouncements?type=program">View All Announcements</a></li>
		</ul>
	</li>
<% } %>	

<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ 	%>
	<li class="has-sub-menu">
     	<a href="#" title="Email Notifications"><i class="fa-solid fa-paper-plane fa-lg" aria-hidden="true"></i></a>
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
                	 <li><a href="/exam/admin/consumerTypeForm">Consumer Type Entries</a></li>
                	 <li><a href="/exam/admin/programStructureForm">Program Structure Entries</a></li>
                	 <li><a href="/exam/admin/programTypeForm">Program Type Entries</a></li>
                	 <li><a href="/exam/admin/specializationTypeForm">Specialization Entries</a></li>
                	 <li><a href="/exam/admin/programForm">Program Entries</a></li>
                	 <li><a href="/exam/admin/consumerProgramStructureMappingForm">Consumer Program Structure Mapping</a></li>  
                	 <li><a href="/exam/admin/programDetailsForm">Program Details Entries</a></li>
                	 <li><a href="/exam/admin/mdmSubjectCodeForm">Subject Data Entries</a></li>
                 	 <li><a href="/exam/admin/mdmSubjectCodeMappingForm">Program Subject Entries</a></li>
                 	 <li><a href="/exam/addBatchDetailsForm">Add Batch Details</a></li>
                 	 <li><a href="/exam/admin/addSubjectDateForm">Add TimeBound Date/Student/Faculty</a></li>
                 	 <li><a href="/exam/addTimeBoundStudentMappingForm">Add Student Mapping</a></li>
                 	 <li><a href="/exam/moveStagingToTimeBoundTableForm">Move TimeBound Staging</a></li>
                 	 <li><a href="/exam/addStudentCourseMappingForm">Add Student Course Mapping</a></li>
                 	 <li><a href="/exam/admin/currencyDetailsForm">Add Currency Details</a></li> 
                 	 <li><a href="/exam/admin/exitSrCertificateMappingForm">Exit Certificate Entries</a></li>   
					 <li><a href="/exam/admin/failedSubjectCriteriaForm">Failed Subject Criteria Entries</a></li>   
                 	 <li><a href="/exam/addSpecializationMapping">Specialization Mapping</a></li>
                 	 <li><a href="/exam/admin/customCourseWaiverForm">Custom Course Waiver</a></li>
                 </ul>
	</li>

<%} %>	
	
		
<%if(roles.indexOf("Admin") != -1 || roles.indexOf("SR Admin") != -1){ 	%>

	<li class="has-sub-menu">
                   	<a href="#">SR</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/admin/searchSRForm">Search Service Request</a></li>
                       	<li><a href="/studentportal/admin/massUpdateSRStatusForm">Mass Update SR Status</a></li>
                       	<li><a href="/studentportal/admin/searchSRHistoryForm">Search Service Request History</a></li>
                       	<li><a href="/studentportal/admin/changeConfigurationForm">Set up Service Request Dates</a></li>
                       	<li><a href="/studentportal/admin/massUploadTrackingSRForm">Mass Upload Tracking SR</a></li>
                       	<li><a href="/studentportal/admin/searchTrackingSRForm">Search SR Tracking Records</a></li>
                       	<%if(roles.indexOf("Admin") != -1 || roles.indexOf("SR Admin") != -1 || roles.indexOf("Exam Admin") != -1){ 	%>
                       	<li><a href="/studentportal/admin/addStudentExtendedSRTimeForm">Extend Service Request Time </a></li>
                       	<li><a href="/studentportal/admin/teeRevaluationReportForm">TeeRevaluation Report</a> </li>
                       	<li><a href="/studentportal/admin/printWithdrawalForm">Print Mass Withdrawal Form</a></li>
                       	<li><a href="/studentportal/SearchSrTypesForm">Search SR Types</a></li>
                       	

                       	<%} %>
                       </ul>
	</li>

<%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1
 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ %>
	<li class="has-sub-menu">
                   	<a href="#">Service Request</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/admin/searchSRForm">Search Service Request</a></li>
                       	<li><a href="/studentportal/admin/searchSRHistoryForm">Search Service Request History</a></li>
                       </ul>
	</li>
<%}else if(roles.indexOf("Finance") != -1){%>
	<li class="has-sub-menu">
                   	<a href="#">Refund Amount</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/admin/refundPaymentForm">Refund Payments</a></li>
                       	<li><a href="/studentportal/admin/walletTransactionReportForm">Generate Wallet Report</a></li>
                       	<li><a href="/studentportal/admin/downloadAdHocPaymentReportForm">Adhoc Payment Report</li>
               
                       </ul>
	</li>
	<li class="has-sub-menu">
                   	<a href="#">Service Request</a>
                       <ul class="sz-sub-menu">

                       	<li><a href="/studentportal/admin/searchSRForm">Search Service Request</a></li>

                       	<li><a href="/studentportal/admin/searchSRDetailsByTrackId">Search TrackId</li>
                       </ul>
	</li>
<%}else if(roles.indexOf("Finance") != -1){ %>
<li class="has-sub-menu">
                   	<a href="#">Refund Amount</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/admin/refundSRForm">Refund SR Amount</a></li>
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
                       	  	  	  	<li><a href="/studentportal/faqEntryPage">FAQ Entry</a></li>
                       	  	  	  	<li><a href="/studentportal/faqCategoryEntryPage">FAQ Category Entry</a></li>
                       	  	  	  	<li><a href="/studentportal/faqSubCategoryEntryPage">FAQ SubCategory Entry</a></li>
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
                  	<a href="#" title="Settings"><i class="fa-solid fa-gear fa-lg" aria-hidden="true"></i></a>
                      <ul class="sz-sub-menu">
                     	
                     	 <li><a href="/studentportal/admin/makeSurveyLiveForm">Make Survey Live Settings</a></li>
                     	
						                     
                      </ul>
				</li>
               <%} %>  
               
<%--           <%if( roles.indexOf("Insofe") == -1 ){ %>      --%>
	          <li class=""> 
	          	<a href="/ltidemo/FacultyTimeline">
	          	<p>EMBA</p>
	            </a>
	          </li>
<%--           <%} %> --%>

            <%if(roles.indexOf("MBA-WX Admin") != -1 || roles.indexOf("Learning Center") != -1){ %>
        		<li class="has-sub-menu">
                  	<a href="#" title="EMBA Reports">EMBA Reports</a>
                   	<ul class="sz-sub-menu">
                    	<li><a href="/acads/admin/searchAttendanceFeedbackForMbaWxForm">Session Attendance For Mba Wx Report</a></li>
						<li><a href="/exam/admin/embaPassFailReportForm">EMBA PassFail Report</a></li>
						<li><a href="/exam/consolidatedStudentsWiseReportsForm">Consolidated Students Wise Reports</a></li>
                    	<li><a href="/internal-assessment/searchIaToEvaluateForm">IA Evaluates Reports</a></li>
				   	</ul>
				</li>
			  <%} %>
		 <%if(roles.indexOf("Faculty") ==  -1 && roles.indexOf("Insofe") ==  -1 ){ %>
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
			  <%} %>
			  <li><a href="/reportingtool/list-folder-form" target="_blank">Reporting Tool</a></li>
	<script>	
 $(document).ready(function () {
	
	 $("#sas").hide();
    $("#main").click(function(){
      $("#sas").toggle();
    });

}); 
</script>
           