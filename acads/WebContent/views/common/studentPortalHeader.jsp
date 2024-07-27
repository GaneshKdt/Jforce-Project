<%

String roles = request.getParameter("roles");

%>
<%if(roles.indexOf("Portal Admin") != -1){ 	%>
							
	<li>
           	<a href="#" title="Manage Users"><i class="fa-solid fa-user fa-lg" aria-hidden="true"></i></a>
               <ul class="subMenu">
               	<li><a href="/studentportal/changeUserPassword">Change Other User Password</a></li>
				<li><a href="/studentportal/admin/createUserForm">Create Users in LDAP</a></li>
				<li><a href="/studentportal/admin/createSingleUserForm">Create Single User</a></li>
				<li><a href="/studentportal/searchUserAuthorizationForm">User Authorization</a></li>
              </ul>
	</li>
<%} %>
<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("Consultant") != -1){ 	%>
<li><a href="#" title="Policy">Policy</a>
	<ul class="subMenu">
		<li><a href="/knowyourpolicy/admin/knowYourPolicy">Policy's</a></li>
		<li><a href="/knowyourpolicy/admin/knowYourPolicyEntry ">Policy Entry</a></li>
		<li><a href="/knowyourpolicy/admin/knowYourPolicyCategory">Policy Category</a></li>
		<li><a href="/knowyourpolicy/admin/knowYourPolicySubCategory">Policy Sub
				Category</a></li>
	</ul></li>
<%} %>

<%if(roles.indexOf("Portal Admin") != -1 || roles.indexOf("Student Support") != -1){ 	%>
	<li>
             	<a href="#" title="Announcements"><i class="fa-solid fa-bullhorn fa-lg"></i></a>
                 <ul class="subMenu">
                 	<li><a href="/studentportal/admin/addAnnouncementForm">Create Announcements</a></li>
					<li><a href="/studentportal/admin/getAllAnnouncements">View All Announcements</a></li>
                 </ul>
	</li>
	
	<li>
                       <ul class="subMenu">
                       	<li><a href="/studentportal/admin/sendEmailFromExcelForm">Send Emails to Excel List</a></li>
                       	<li><a href="/studentportal/admin/sendEmailToStudentGroupForm">Send Emails based on Groups</a></li>
                       </ul>
	</li>


<%} %>

<%if(roles.indexOf("Admin") != -1 || roles.indexOf("SR Admin") != -1){ 	%>

	<li>
                   	<a href="#">SR</a>
                       <ul class="subMenu">
                       	<li><a href="/studentportal/searchSRForm">Search Service Request</a></li>
                       	<li><a href="/studentportal/changeConfigurationForm">Set up Service Request Dates</a></li>
                       </ul>
	</li>

<%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1
 || roles.indexOf("Student Support") != -1){ %>
	<li>
                   	<a href="#">Service Request</a>
                       <ul class="subMenu">
                       	<li><a href="/studentportal/searchSRForm">Search Service Request</a></li>
                       </ul>
	</li>
<%} %>
           