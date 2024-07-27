<%
String roles = request.getParameter("roles");
%>
          
<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Portal Admin") != -1 || roles.indexOf("Information Center") != -1){ 	%>
							
	<li class="has-sub-menu"> <a href="#" title="Manage Users"><i class="fa-solid fa-users fa-lg" aria-hidden="true"></i></a>
	   <ul class="sz-sub-menu">
	     <li><a href="/studentportal/changeUserPassword">Change User Password</a></li>
	      <%if(roles.indexOf("Portal Admin") != -1){ 	%>
		<li><a href="/studentportal/admin/createUserForm">Create Users in LDAP</a></li>
		<li><a href="/studentportal/admin/createSingleUserForm">Create Single User</a></li>
		<li><a href="/studentportal/searchUserAuthorizationForm">User Authorization</a></li>
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
<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ 	%>
	<li class="has-sub-menu">
             	<a href="#" title="Announcements"><i class="fa-solid fa-bullhorn fa-lg"></i></a>
                 <ul class="sz-sub-menu">
                 	<li><a href="/studentportal/admin/addAnnouncementForm">Create Announcements</a></li>
					<li><a href="/studentportal/admin/getAllAnnouncements">View All Announcements</a></li>
                 </ul>
	</li>
	
	<li class="has-sub-menu">
     	<a href="#" title="Email Notifications"><i class="fa-solid fa-paper-plane fa-lg" aria-hidden="true"></i></a>
         <ul class="sz-sub-menu">
         	<li><a href="/studentportal/admin/sendEmailFromExcelForm">Send Emails to Excel List</a></li>
         	<li><a href="/studentportal/admin/sendEmailToStudentGroupForm">Send Emails based on Groups</a></li>
         	<li><a href="/studentportal/admin/sendEmailToLeadsForm">Send Emails to Leads</a></li>
         	<li><a href="/studentportal/downloadFeedBacks">Download FeedBack Report</a></li><!-- Newly added by Vikas for download excel -->
         </ul>
	</li>

<%} %>
		
<%if(roles.indexOf("Admin") != -1 || roles.indexOf("SR Admin") != -1){ 	%>

	<li class="has-sub-menu">
                   	<a href="#">SR</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/searchSRForm">Search Service Request</a></li>
                       	<li><a href="/studentportal/searchSRHistoryForm">Search Service Request History</a></li>
                       	<li><a href="/studentportal/changeConfigurationForm">Set up Service Request Dates</a></li>
                       	<%if(roles.indexOf("Admin") != -1 || roles.indexOf("SR Admin") != -1 || roles.indexOf("Exam Admin") != -1){ 	%>
                       	<li><a href="/studentportal/addStudentExtendedSRTimeForm">Extend Service Request Time</a></li>
                       	<%} %>
                       </ul>
	</li>

<%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1
 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ %>
	<li class="has-sub-menu">
                   	<a href="#">Service Request</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/searchSRForm">Search Service Request</a></li>
                       </ul>
	</li>
<%}else if(roles.indexOf("Finance") != -1){%>
	<li class="has-sub-menu">
                   	<a href="#">Refund Amount</a>
                       <ul class="sz-sub-menu">
                       	<li><a href="/studentportal/refundPaymentForm">Refund Payments</a></li>
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
	<%if(roles.indexOf("Faculty") == -1 ){%>
	<li class="has-sub-menu">
   		<a href="#" title="EMBA Reports">Leads</a>
          	<ul class="sz-sub-menu">
          		<li><a href="/exam/admin/makeTestLiveFormForLeads">Lead Make Test Live</a></li>
				<li><a href="/exam/admin/viewAllTestsForLeads">Lead Search Test</a></li>
				<li><a href="/exam/admin/addTestForLeadsForm">Lead Add Test</a></li>
          		<li><a href="/studentportal/admin/leadReport">Lead Report</a></li>
			</ul>
	</li>
	<%} %>
	<script>	
 $(document).ready(function () {
	
	 $("#sas").hide();
    $("#main").click(function(){
      $("#sas").toggle();
    });

}); 
</script>

           