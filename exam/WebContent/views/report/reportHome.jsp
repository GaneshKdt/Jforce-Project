 <!DOCTYPE html>
<html lang="en">
<%@page import="com.nmims.beans.Person"%>

    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Reports" name="title"/>
    </jsp:include>
    <%try{ %>
    <%
    Person p = (Person)session.getAttribute("user");
    
    String firstName = "";
	String lastName = "";
	String displayName = "";
	String email = "";
	String lastLogon = "";
    
    if(p != null){
    	displayName = p.getDisplayName();
    	lastLogon = p.getLastLogon();
    }
    %>
    <style>
    .module-box {
	margin-top:10px;
	background-color:#3D9198;
	padding:15px;
	-webkit-box-sizing: border-box; /* Safari/Chrome, other WebKit */
	-moz-box-sizing: border-box;    /* Firefox, other Gecko */
	box-sizing: border-box;         /* Opera/IE 8+ */	
	-moz-box-shadow:    0px 0px 5px 6px #eee;
	-webkit-box-shadow: 0px 0px 5px 6px #eee;
	box-shadow:         0px 0px 5px 6px #eee;	
}
.module-box a{
 color: #ffffff;
    font-size: 16px;
}
.panel-default>.panel-heading {
    background: linear-gradient(to right, #f1b1ac, #de4c52);
}
.panel-heading {
  padding: 0;
	border:0;
}
.panel-title>a, .panel-title>a:active{
	display:block;
	padding:15px;
  color:#555;
  font-size:16px;
  font-weight:bold;
      font-family: initial;
	text-transform:uppercase;
	letter-spacing:1px;
  word-spacing:3px;
	text-decoration:none;
}
.panel-heading  a:before {
   font-family: 'Glyphicons Halflings';
   content: "\e114";
   float: right;
   transition: all 0.5s;
}
.panel-heading.active a:before {
	-webkit-transform: rotate(180deg);
	-moz-transform: rotate(180deg);
	transform: rotate(180deg);
} 
    </style>
    <body>
    
   
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Reports" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              					<div class="sz-content">
								
									<h2 class="red text-capitalize">Reports</h2>
									<div class="clearfix"></div>

 <%-- <div class="wrapper center-block">
  <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
  <div class="panel panel-default">
    <div class="panel-heading active" role="tab" id="headingOne">
      <h4 class="panel-title">
        <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
          Exam Reports
        </a>
      </h4>
    </div>
    <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
      <div class="panel-body">
     <div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
						<th>Links</th>
						<th>Description</th>
						</tr>
						<tr>
							<th><a href="/exam/admin/expectedExamRegistrationReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/expectedExamRegistrationReportForm
							</a> </th>
							<td>Expected Exam Registration for Center Capacity Report </td>
						</tr>
						<tr>
							<th><a href="/exam/admin/examCenterCapacityReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/examCenterCapacityReportForm
							</a> </th>
							<td>Exam Center Capacity Report </td>
						</tr>
						<tr>
							<th><a href="/admin/reRegistrationReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/reRegistrationReportForm
							</a> </th>
							<td>Re-Registration Report </td>
						</tr>
						</thead>
					</table>
				</div>
     
      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingTwo">
      <h4 class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
         Executive  Reports
        </a>
      </h4>
    </div>
    <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
      <div class="panel-body">
 <div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
						<th>Links</th>
						<th>Description</th>
						</tr>
						
						<tr>
							<th><a href="executiveExamBookingReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/executiveExamBookingReportForm
							</a>
							<td>Executive ExamBooking Report </td>
						</tr>
						<tr>
							<th><a href="executiveExamCenterSlotCapacityReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/executiveExamCenterSlotCapacityReportForm
							</a></th>
							<td>Executive Exam Center Slot Capacity Report </td>
						</tr>
						</thead>
					</table>
				</div>


      </div>
    </div>
  </div>
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingThree">
      <h4 class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
          Students Reports
        </a>
      </h4>
    </div>
    <div id="collapseThree" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingThree">
      <div class="panel-body">
        <div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
						<th>Links</th>
						<th>Description</th>
						</tr>
						<tr>
							<th><a href="programCompleteReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/programCompleteReportForm
							</a> </th>
							<td>Program Complete Report</td>
						</tr>
						
						
						<tr>
							<th><a href="/exam/admin/activeStudentReport" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/activeStudentReport
							</a> </th>
							<td>Active Student Report</td>
						</tr>
						
						
						<tr>
							<th><a href="/exam/admin/graceToCompleteProgramReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/graceToCompleteProgramReportForm
							</a> </th>
							<td>Grace To Complete Program Report</td>
						</tr>
						
						<tr>
							<th><a href="/exam/admin/studentClearingSubjectsReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/studentClearingSubjectsReportForm
							</a> </th>
							<td>Student Clearing Given Number of Subjects Report</td>
						</tr>
						<tr>
							<th><a href="/acads/searchStudentZoneLoginsForm" class="">
							https://studentzone-ngasce.nmims.edu/acads/searchStudentZoneLoginsForm
							</a> </th>
							<td>
							Student Logins Report
							</td>
						</tr>
						
						
						
						</thead>
					</table>
				</div>
      </div>
    </div>
  </div>
  
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingFour">
      <h4 class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFour" aria-expanded="false" aria-controls="collapseFour">
          Bookings Reports
        </a>
      </h4>
    </div>
    <div id="collapseFour" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingFour">
      <div class="panel-body">
       <div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
						<th>Links</th>
						<th>Description</th>
						</tr>
						<tr>
							<th><a href="/exam/admin/bookingSummaryReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/bookingSummaryReportForm</a>
							 </th>
							<td>Booking Summary Report</td>
						</tr>
						
						<%if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1
						|| roles.indexOf("Admin")!=-1 ){ %>
						<tr>
							<th><a href="/exam/admin/examBookingReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/examBookingReportForm</a>
							 </th>
							<td>Exam Booking Report</td>
						</tr>
						<tr>
							<th><a href="/exam/admin/examBookingPendingReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/examBookingPendingReportForm</a>
							 </th>
							<td>Exam Booking Pending Report</td>
						</tr>
						<%} %>
						<tr>
							<th><a href="/exam/admin/examBookingStudentCountReportForm" class="">
							
							https://studentzone-ngasce.nmims.edu/exam/admin/examBookingStudentCountReportForm</a>
							 </th>
							<td>Exam Bookings Counts (Sem, Program, City) Report </td>
						</tr>
								<tr>
							<th>	<a href="/exam/admin/releasedButNotBookedReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/releasedButNotBookedReportForm
							</a>
							 </th>
							<td>Exam Bookings Released But Not Booked Report</td>
						</tr>													
						
						</thead>
					</table>
				</div>
      </div>
    </div>
  </div>
  
  
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingFive">
      <h4 class="panel-title">
        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFive" aria-expanded="false" aria-controls="collapseFive">
          Other Reports
        </a>
      </h4>
    </div>
    <div id="collapseFive" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingFive">
      <div class="panel-body">
       <div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
						<th>Links</th>
						<th>Description</th>
						</tr>
						
						<tr>
							<th><a href="onlineExamMarksReport" class="">
							https://studentzone-ngasce.nmims.edu/exam/onlineExamMarksReport</a>
							
							 </th>
							<td>Online Exam Marks Report</td>
						</tr>
						
						
						<tr>
							<th><a href="/exam/admin/cumulativeFinanceReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/cumulativeFinanceReportForm</a>
							
							 </th>
							<td>Date Wise Online Collections Report</td>
						</tr>
						
						
						<tr>
							<th><a href="/exam/admin/questionPaperCountReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/questionPaperCountReportForm</a>
							 </th>
							<td>Question Paper Count Report</td>
						</tr>
						<tr>
							<th><a href="/exam/admin/semWiseSubjetsClearedReportForm" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/semWiseSubjetsClearedReportForm</a>
							 </th>
							<td>SemWise Subjects Cleared Report</td>
						</tr>
						<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1  
        		 					 || roles.indexOf("TEE Admin") != -1){ %>
						
						<tr>
							<th><a href="/exam/admin/toppersReportForm	" class="">
							https://studentzone-ngasce.nmims.edu/exam/admin/toppersReportForm	</a>
							 </th>
							<td>Toppers Report</td>
						</tr>
						<%} %>
						
						<%if(roles.indexOf("SR Admin") != -1 || roles.indexOf("Admin")!=-1){ %>
							<tr>
							<th><a href="/studentportal/downloadAdHocPaymentReport" class="">
https://studentzone-ngasce.nmims.edu/studentportal/downloadAdHocPaymentReport</a>							 </th>
							<td>Download Adhoc Payment Report</td>
						</tr>
						<tr>
							<th><a href="/exam/admin/assignmentPaymentReportForm" class="">
https://studentzone-ngasce.nmims.edu/exam/admin/assignmentPaymentReportForm</a>							 </th>
							<td>Assignment Payment Report</td>
						</tr>
						<%} %>
						
						</thead>
					</table>
				</div>
      </div>
    </div>
  </div>
  
</div>
</div> --%>							
								
									<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1 
        		 					 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1 || roles.indexOf("Learning Center") != -1){ %>
									 <div class="row">
											<div class="col-sm-6">
											  <div class="module-box ">
												<a href="/exam/admin/programCompleteReportForm" class="">Students Clearing Program</a>
											  </div> 
											</div> 
										  <div class="col-sm-6" <% if(roles.indexOf("Learning Center") != -1){ %>style="display:none;" <%} %>>
											  <div class="module-box ">
												<a href="/exam/admin/graceToCompleteProgramReportForm" class="">Student needing < 10 Grace </a>
											  </div> 
											</div> 
										</div>

										<%-- <div class="row">
											<div class="col-sm-6" <% if(roles.indexOf("Learning Center") != -1){ %>style="display:none;" <%} %>>
											  <div class="module-box ">
												<a href="/exam/admin/bookingSummaryReport" class="">Bookings Number</a>
											  </div> 
											</div>
											</div>  --%>
											
									<% } %>
									<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1 
        		 					 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1 || roles.indexOf("Learning Center") != -1
        		 					 || roles.indexOf("Finance") != -1 ){ %>
									 <div class="row">
											<div class="col-sm-6">
											  <div class="module-box ">
												<a href="/exam/admin/examBookingReportForm" class="">Confirmed Bookings</a>
											  </div> 
											</div>
											<div class="col-sm-6">
											  <div class="module-box ">
												<a href="/exam/admin/teeAttendanceReportForm" class="">TEE Attendance Report</a>
											  </div> 
											</div>
										</div>		
									<% } %>
									
									<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 
        		 					 || roles.indexOf("Learning Center") != -1){ %>
									 <div class="row">
											<div class="col-sm-6">
											  <div class="module-box ">
												<a href="/exam/admin/projectPendingReportForm" class="">Project Submission Pending Report</a>
											  </div> 
											</div>
										</div>		
									<% } %>
									
									<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){
									if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1 
        		 					 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1 || roles.indexOf("Learning Center") != -1 
        		 					|| roles.indexOf("Student Support") != -1){ %>
									 
										<div class="row">
																
												<div class="col-sm-6">
												  <div class="module-box ">
													<a href="/exam/admin/examCenterCapacityReportForm" class="">Exam Center Available Capacity</a>
												  </div> 
												</div> 
										</div>
									<% }} %>					
									
									<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1 
        		 					 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1 || roles.indexOf("Learning Center") != -1){ %>
									 
										<div class="row">
												<div class="col-sm-6">
												  <div class="module-box ">
													<a href="/exam/admin/examBookingPendingReportForm" class="">Pending Bookings</a>
												  </div> 
												</div> 

										</div>
												<div class="row">
														<% if(roles.indexOf("Admin") != -1){ %>
														<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/cumulativeFinanceReportForm" class="">Date Wise Online Collections</a>
														  </div> 
														</div> 
														
														<% } %>
														<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
														 <div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/questionPaperCountReportForm" class="">Question Paper Count</a>
														  </div> 
														</div> 
														<% } %>

											</div>
											<div class="row">
											<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
												<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/examBookingStudentCountReportForm" class="">Exam Bookings Counts (Sem, Program, City)</a>
														  </div> 
														</div> 
														  <%} %>
														<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/studentClearingSubjectsReportForm" class="">Student Clearing Given Number of Subjects</a>
														  </div> 
														</div> 
														
													</div>
													<div class="row">
													<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
													<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/expectedExamRegistrationReportForm" class="">Expected Exam Registration for Center Capacity</a>
														  </div> 
														</div> 
														<% } %>
													   <div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/semWiseSubjetsClearedReportForm" class="">Semwise Pass Report</a>
														  </div> 
														</div> 
													</div>
													<div class="row">
													<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1  
        		 					 || roles.indexOf("TEE Admin") != -1){ %>
													<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/toppersReportForm" class="">Toppers Report </a>
														  </div> 
													</div> 
														<%} %>
														   <div <% if(roles.indexOf("Learning Center") != -1){ %>style="display:none;" <%} %> class="col-sm-6">
														  <!-- <div class="module-box ">
															<a href="onlineExamMarksReport" class="">Online Exam Marks Report</a>
														  </div>  -->
														</div> 
													</div>
													<div class="row">
													<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/releasedButNotBookedReportForm" class="">Exam Bookings Released but Not booked</a>
														  </div> 
														</div> 
														
														<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/examBookingCanceledReportForm" class="">Exam Bookings Canceled</a>
														  </div> 
														</div>
														
														 <div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/assignmentPaymentReportForm" class="">Assignment Payment Report</a>
														  </div> 
														</div> 
														
														<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/studentportal/admin/downloadAdHocPaymentReport" class="">AdHoc Payments</a>
														  </div> 
														</div> 
														
														<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/reRegistrationReportForm" class="">Re-Registration Report</a>
														  </div> 
														</div> 
														
													</div>
													<%} %>
													<%if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
													<div class="row">
														<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/examBookingReportForm" class="">Confirmed Bookings</a>
														  </div> 
														</div> 
														
														<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/examBookingPendingReportForm" class="">Pending Bookings</a>
														  </div> 
														</div> 
														 
													</div>
													<%} %>
													
													<div class="row">
						<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1
				         || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("SR Admin") != -1
				         || roles.indexOf("Consultant") != -1 || roles.indexOf("Finance") != -1   ){ %>
      							
													
													<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/activeStudentReport" class="">Active Student Report</a>
														  </div> 
													</div> 
						<%} %>
													
													<%if (roles.indexOf("Exam Admin") != -1) { %>
													<div class="col-sm-6">
														<div class="module-box ">
															<a href="/exam/admin/passedInRevalYetRegisteredReportForm" class="">Student Passed In Reval Yet Registered Report</a>
														</div>
													</div>
													<%} %>
							 


													
													</div>
						<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1
				         || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("SR Admin") != -1
				         || roles.indexOf("Consultant") != -1 || roles.indexOf("Finance") != -1   ){ %>
      				
													<div class="row">
													<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/acads/searchStudentZoneLoginsForm" class="">Student Login Report</a>
														  </div> 
														</div> 
													</div>
						<%} %>					
													<%if(roles.indexOf("SR Admin") != -1 || roles.indexOf("Consultant") != -1){ %>
													<div class="row">
													<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/studentportal/admin/downloadAdHocPaymentReport" class="">AdHoc Payments</a>
														  </div> 
												    </div> 
												    </div>
												    <div class="row">
												    <div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/assignmentPaymentReportForm" class="">Assignment Payment Report</a>
														  </div> 
														</div>
													</div>
													<div class="row">
													<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/programCompleteReportForm" class="">Students Clearing Program</a>
														  </div> 
													</div> 
													</div>
													<div class="row">
													<div class="col-sm-6">
														  <div class="module-box ">
															<a href="/exam/admin/semWiseSubjetsClearedReportForm" class="">Semwise Pass Report</a>
														  </div> 
													</div> 
													</div> 
													<div class="row">
													<div class="col-sm-6">
													 	<div class="module-box ">
															<a href="/exam/admin/examBookingReportForm" class="">Confirmed Bookings</a>
														  </div> 
												    </div> 
													</div>	
												    <%} %>
												    
												    <%if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1
												    	 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("Exam Admin") != -1
												    	 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 ){ %>
													<div class="row">
														<div class="col-sm-6">
														  <div class="module-box ">
															<a href="#demo" data-toggle="collapse" style="padding-bottom:5px">Executive Program's Reports</a>

															<div id="demo" class="collapse" >
															<div class="module-box ">
															 	<a href="executiveExamBookingReportForm" class="">Confirmed Exam Bookings Report</a>
														 	</div>
														 	<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
														 	<div class="module-box ">
																<a href="executiveExamCenterSlotCapacityReportForm" class="">Exam Center Capacity Report</a>
														 	</div>
														 	<%} %>
														  <div class="module-box ">
															<a href="/exam/admin/examBookingPendingReportExecutiveForm" class="">Executive Exam Booking Pending Report</a>
														  </div>
														  
														   <div class="module-box ">
															<a href="/exam/admin/caseStudySubmissionReportForm" class="">Case Study Submission Report </a>
														  </div>
														 	
															</div>
														  </div> 
														</div> 
														
														 
													</div> 
													 <%} %> 
													 
													 <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1  
        		 					 || roles.indexOf("TEE Admin") != -1){ %>
        		 					 			<div class="row">
        		 					 				<div class="col-sm-6">
												  		<div class="module-box ">
															<a href="/exam/pgReExamEligibleStudentReport" class="">PG Re-Exam Eligible Students Report</a>
												  		</div> 
													</div> 
        		 					 			</div>	 
												<div class="row">
												<div class="col-sm-6">
												  <div class="module-box ">
													<a href="/exam/admin/projectStatusReportForm" class="">Project Status Report</a>
												  </div> 
												</div> 
													 <%} %> 
												<!-- Added to show a report of payments which were recieved twice from student
												<div class="col-sm-6">
												  <div class="module-box ">
													<a href="assignmentPaymentReportDoublePaymentsRecievedForm" class="">Assignment Payment Report for Double Payments Recieved</a>
												  </div>  
												</div> -->

											</div> 
              					
              			<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1
				         || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("SR Admin") != -1
				         || roles.indexOf("Consultant") != -1 || roles.indexOf("Finance") != -1   ){ %>					 
										<div class="row">
												<div class="col-sm-6">
												  <div class="module-box ">
													<a href="/exam/admin/pendingSubjectsForLateralEntriesReportForm" class="">Pending Subjects For Lateral Entries Report</a>
												  </div> 
												</div> 

											</div> 
										
										<div class="row">
												<div class="col-sm-6">
												  <div class="module-box ">
														<a href="assignmentEvalReportWithMarksBifurcationNQuestionRemarksForm" class="">Assignment Evaluation Report With Marks Bifurcation n Question Wise Remarks</a>
													</div> 
												</div> 

											</div> 
						<% } %> 
						<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1
				         || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("SR Admin") != -1
				         || roles.indexOf("Consultant") != -1 || roles.indexOf("Finance") != -1   ){ %>					 
										<div class="row">
												<div class="col-sm-6">
												  <div class="module-box ">
													<a href="/exam/admin/aisheugcReportForm" class="">Aisheugc Report</a>
												  </div> 
												</div> 

											</div> 
										 
						<% } %> 		
											
											
									</div>
								 </div>
								</div>
							</div>
							</div>
						<jsp:include page="../adminCommon/footer.jsp"/>
						<%}catch(Exception e){
							}	%>		
						
						
								
							</body>
							
							<script>
							 $('.panel-collapse').on('show.bs.collapse', function () {
								    $(this).siblings('.panel-heading').addClass('active');
								  });

								  $('.panel-collapse').on('hide.bs.collapse', function () {
								    $(this).siblings('.panel-heading').removeClass('active');
								  });
							
							
							</script>
						</html>