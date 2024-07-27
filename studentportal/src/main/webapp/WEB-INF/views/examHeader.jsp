<%

String roles = request.getParameter("roles");

%>
<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                <li>
                  	<a href="#">Assign</a>
                      <ul class="subMenu">
                      	<li><a href="/exam/admin/uploadAssignmentMarksForm">Mass Upload Assgn Marks</a></li>
                    <li><a href="/exam/uploadAssignmentStatusForm">Upload Assgn Status</a></li>
                    <li><a href="/exam/uploadAssignmentFilesForm">Upload Assgn Questions</a></li>
                    <li><a href="/exam/searchAssignmentSubmissionForm">Check Assgn Status</a></li>
                    <li><a href="/exam/copyCaseCheckForm">Check Copy Cases</a></li>
                    <li><a href="/exam/admin/getCopyCaseReportForm">Copy Case Report Download</a></li>
                    <li><a href="/exam/admin/detailedThresholdCopyCasesForm">Detailed Threshold Copy Cases</a></li>
                    <li><a href="/exam/markCopyCasesForm">Mark Copy Cases</a></li>
                    <li><a href="/exam/allocateAssignmentEvaluationForm?level=1">Allocate Assignments Evaluation Level 1</a></li>
                    <!-- <li><a href="/exam/allocateAssignmentEvaluationForm?level=2">Allocate Assignments Evaluation Level 2</a></li> 
                    <li><a href="/exam/allocateAssignmentEvaluationForm?level=3">Allocate Assignments Evaluation Level 3</a></li> -->
                    <li><a href="/exam/allocateAssignmentEvaluationForm?level=4">Reval Allocation</a></li>
                    <li><a href="/exam/allocateAssignmentEvaluationForm?level=2">Reval Allocation - Second Level</a></li>
                    <li><a href="/exam/searchAssignmentToEvaluateForm">Assignments Evaluation Status</a></li>
                    <li><a href="/exam/resetFacultyAssignmentAllocationForm?level=1">Reset Faculty Assignment Allocation Level 1</a></li>
                    <!-- <li><a href="/exam/resetFacultyAssignmentAllocationForm?level=2">Reset Faculty Assignment Allocation Level 2</a></li>
                    <li><a href="/exam/resetFacultyAssignmentAllocationForm?level=3">Reset Faculty Assignment Allocation Level 3</a></li> -->
                   
                      </ul>
</li>
               <%} %>
            
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                <li>
                  	<a href="#">Assign Results</a>
                      <ul class="subMenu">
                    <li><a href="/exam/insertANSRecordsForm">Insert ANS records in Marks table(Regular Cycle Only)</a></li>
                    <li><a href="/exam/downloadNormalizedScoreForm">Download Normalized Score</a></li>
                    <li><a href="/exam/moveNormalizedScoreToMarksTableForm">Move Assignment Marks for Pass Fail trigger</a></li>
                      </ul>
</li>
               <%} %>
            
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                <li>
                  	<a href="#">Project</a>
                      <ul class="subMenu">
                    <li><a href="/exam/searchProjectSubmissionForm">Search Project Submissions</a></li>
                    <li><a href="/exam/allocateProjectEvaluationForm">Allocate Project Evaluations</a></li>
                    <li><a href="/exam/allocateProjectRevaluationForm">Allocate Project Re-valuations</a></li>
                    <li><a href="/exam/searchProjectToEvaluateForm">Evaluate Projects</a></li>
                      </ul>
</li>
               <%} %>
            
            <%if(roles.indexOf("Faculty") != -1 ){ %>
                 <li>
                  	<a href="#">Evaluation</a>
                      <ul class="subMenu">
                      	<li><a href="/exam/searchAssignmentToEvaluateForm">Evaluate Assignments</a></li>
                      	<li><a href="/exam/searchProjectToEvaluateForm">Evaluate Projects</a></li>
                      </ul>
</li>
        		<li>
					  	<a href="/acads/assignedCourseQueries">Queries for Me</a>
                
				</li>
               <%} %>
             
<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                 <li>
                  	<a href="#">TEE</a>
                      <ul class="subMenu">
                      		<li><a href="/exam/addStudentMarksForm">Single Entry</a></li>
							<li><a href="/exam/admin/uploadWrittenMarksForm">Mass Upload Offline Exam Marks</a></li>
							<li><a href="/exam/admin/uploadOnlineExamMarksForm">Mass Upload Online Exam Marks</a></li>
							<li><a href="/exam/admin/uploadOnlineWrittenRevalMarksForm">Mass Upload Online Exam Reval Marks</a></li>
							<li><a href="/exam/evaluationSheetForm">Evaluation Sheet</a></li>
							<li><a href="/exam/admin/insertABRecordsForm">Online Exam Absent Students List</a></li>
							<li><a href="/exam/insertExecutiveABRecordsForm">Executive Exam Absent Students List</a></li>
							<li><a href="/exam/markCheckSheetForm">TEE Marks Check Sheet</a></li>
							<li><a href="/exam/admin/passFailTriggerSearchForm">Pass Fail Trigger</a></li>
							<li><a href="/exam/admin/getGraceEligibleForm">Grace</a></li>
							<li><a href="/exam/admin/makeResultsLiveForm">Make Results Live</a></li>
					   </ul>
					</li>
               <%} %>
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                 <li>
                  	<a href="#" title="Exam Results"><i class="fa fa-trophy fa-lg"></i></a>
                      <ul class="subMenu">
                      	<li><a href="/exam/admin/passFailTriggerSearchForm">Pass Fail Trigger</a></li>
						<li><a href="/exam/admin/getGraceEligibleForm">Grace</a></li>
						<li><a href="/exam/admin/makeResultsLiveForm">Make Results Live</a></li>
						<li><a href="/exam/marksheetForm">Marksheet</a></li>
						<li><a href="/exam/admin/singleStudentMarksheetForm">Single Marksheet</a></li>
						<li><a href="/exam/marksheetFromSRForm">Marksheet from SR</a></li>
						<li><a href="/exam/certificateFromSRForm">Certificate From SR</a></li>
						<li><a href="/exam/singleStudentCertificateForm">Single Certificate</a></li>
						<li><a href="/exam/downloadExecutiveMarksheetForm">Executive Bulk Marksheet</a></li>
                     	<li><a href="/exam/executiveCertificatesForBatchForm">Executive Bulk Certificates </a></li>
						<li><a href="/exam/transcriptForm">Transcript</a></li>
                      </ul>
				</li>

               <%} %>
            
            <%if(roles.indexOf("Exam Admin") != -1 ){ %>
                 <li>
                  	<a href="#" title="Review Question File"><i class="fa fa-question-circle fa-lg" ></i></a>
                      <ul class="subMenu">
                      	<li><a href="/exam/uploadQuestionFileForm">Review Question File</a></li>
                      </ul>
</li>

               <%} %>
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Read Admin") != -1
             		|| roles.indexOf("TEE Admin") != -1){ %>
                 <li>
                  	<a href="#" title="Exam Registration">Exam Reg</a>
                      <ul class="subMenu">
                      	<li><a href="/exam/addExamCenterForm" class="">Add Exam Center</a></li>
                      	 <li><a href="/exam/excelUploadForExamCentersForm" class="">Upload Mass Exam Center</a></li>
                      	<!-- <li><a href="/exam/addExecutiveExamCenterFormMassUpload" class="">Upload Executive Mass Exam Center</a></li> -->
                      	<li><a href="/exam/examExecutiveRegistrationChecklist" class="">Exam Registration Checklist</a></li>
                      	  <li><a href="/exam/examRegularRegistrationChecklist">Regular Exam Reg Checklist</a></li>
                	<li><a href="/exam/uploadExamFeeExemptForm" class="">Upload No Exam Fee Students</a></li>
                	<li><a href="/exam/uploadExamFeeExemptSubjectsForm" class="">Upload No Exam Fee Students-Subjects</a></li>
					<li><a href="/exam/searchDDsToApproveForm">Approve DD</a></li>
					<li><a href="/exam/queryTransactionStatusForm">Query Transaction</a></li>
					<li><a href="/exam/searchBookingsToReleaseForm">Release Seats</a></li>
					<li><a href="/exam/uploadSeatRealeseFileForm">Mass Release Seats</a></li>
					<li><a href="/exam/searchExamBookingTOChangeCenterForm">Change Center</a></li>
					<li><a href="/exam/queryFeesPaidForm">Refund</a></li>
					<li><a href="/exam/admin/findOnlineTranConflictForm">Transaction Mismatch</a></li>
					<li><a href="/exam/admin/examBookingDashboardForm">Booking Dashboard</a></li>
					<li><a href="/exam/attendanceSheetForm">Attendance Sheet</a></li>
					<li><a href="/exam/downloadHallTicketForm">Hall Ticket</a></li>
					<!-- <li><a href="/exam/admin/onlineExamPasswordReportForm">Online Exam Passwords</a></li> -->
					<li><a href="/exam/insertExamBookingDataFormTCS">Upload Exam Booking Data To TCS</a></li>
					<li><a href="/exam/admin/demoExamDashBoard">Demo Exam DashBoard</a></li>
                      </ul>
</li>
               <%} %>
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1
            		|| roles.indexOf("Read Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>   
               <li>
                  	<a href="#" title="Search"><i class="fa fa-search" ></i></a>
                      <ul class="subMenu">
               	
	
					<li><a href="/exam/admin/searchStudentMarksForm">Search Marks</a></li>
					<li><a href="/exam/admin/searchRegisteredStudentMarksForm">Search Exam Registered Student Marks</a></li>
					<li><a href="/exam/admin/searchOnlineMarksForm" >Search Online Marks</a></li>
					<li><a href="/exam/admin/searchPassFailForm">Search Pass Fail</a></li>
					<li><a href="/exam/searchStudentsForm">Search Students</a></li>
              		<li><a href="/exam/searchStudentsRegistrationsForm">Search Student Registrations</a></li>
              		<li><a href="/exam/searchAssignmentStatusForm">Search Assgn Status</a></li>
					<li><a href="/exam/searchAssignmentFilesForm">Search Assgn Questions</a></li>
					<li><a href="/exam/searchExamCenterForm">Search Exam Centers </a></li>
					<li><a href="/exam/searchStudentBatchMappingForm">Search Student Batch Mapping</a>
               		
	</ul>
</li>
                <%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
                
                <li>
                  	<a href="#" title="Search"><i class="fa fa-search" ></i></a>
                      <ul class="subMenu">
             	<li><a href="/exam/admin/searchStudentMarksForm">Search Marks</a></li> 
             	<li><a href="/exam/admin/searchPassFailForm">Search Pass Fail</a></li>
             	<li><a href="/exam/searchStudentsForm">Search Students</a></li>
           		<li><a href="/exam/searchStudentsRegistrationsForm">Search Student Registrations</a></li>
           		<li><a href="/exam/searchAssignmentStatusForm">Search Assgn Status</a></li>
           		
           		</ul>
</li>
             <% }%>

<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>

      <li>
        <a href="#" title="Enrollment"><i class="fa fa-user-plus fa-lg"></i></a>
        <ul class="subMenu">
         <li><a href="/exam/uploadCentersForm">Upload Information Centers</a></li>
		<li><a href="/exam/uploadStudentsMasterForm">Upload Student Master</a></li>
		<li><a href="/exam/uploadRegistrationForm">Upload Registrations</a></li>
		<!-- <li><a href="/exam/uploadStudentsImageForm">Upload Student Photo URLs</a></li> -->
                      </ul>
</li>
               <%} %>  
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 
                 <li>
                  	<a href="#" title="Exam Time Table"><i class="fa fa-table fa-lg" ></i></a>
                      <ul class="subMenu">
                      <li><a href="/exam/uploadTimetableForm">Upload Timetable</a></li>
						<li><a href="/exam/adminTimeTableForm">View Timetable</a></li>
						<li><a href="/exam/admin/makeResultsLiveForm">Make Timetable Live for Exam Registration</a></li>
                      </ul>
				</li>
               <%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
               	 <li>
                  	<a href="#" title="Exam Time Table"><i class="fa fa-table fa-lg"></i></a>
                      <ul class="subMenu">
	<li><a href="/exam/adminTimeTableForm">View Timetable</a></li>
                      </ul>
</li>
              	<% }%>  
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1
         || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
                <li>
                  	<a href="#" title="Exam Reports"><i class="fa fa-bar-chart fa-lg" ></i></a>
                      <ul class="subMenu">
                     	 <li><a href="/Reports">Exam Reports</a></li>
                      </ul>
</li>
               <%} %>  
            
           <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 <li>
                  	<a href="#" title="Settings"><i class="fa fa-cog fa-lg" aria-hidden="true"></i></a>
                      <ul class="subMenu">
                     	 <li><a href="/studentportal/loginAsForm">Exam:Log In As</a></li>
                     	 <li><a href="/exam/admin/makeResultsLiveForm">Make Live Settings</a></li>
                     	 <li><a href="/exam/admin/changeConfigurationForm">Set up dates</a></li>
                      </ul>
</li>
               <%} %>  
               
                     <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 <li>
                  	<a href="#" title="Internal Assessment"><i class="fa fa-flask fa-lg" aria-hidden="true"></i></a>
                      <ul class="subMenu">
                      	 <li><a href="/exam/makeTestLiveForm">Make Tests Live</a></li>
                     	 <li><a href="/exam/addTestForm">Add Test</a></li>
                     	 <li><a href="/exam/viewAllTests">Search Tests</a></li>
                     	  <li><a href="/exam/consolidatedStudentsWiseReportsForm">Consolidated Students Wise Reports</a></li>
                     	  <li><a href="/exam/searchIaToEvaluateForm">IA Evaluates Reports</a></li>
                     	 
                      </ul>
				</li>
               <%} %>  