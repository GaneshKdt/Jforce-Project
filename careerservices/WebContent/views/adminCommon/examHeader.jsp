<%

String roles = request.getParameter("roles");

%>
			<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Assign</a>
                      <ul class="subMenu">
                      	<li><a href="/exam/uploadAssignmentMarksForm">Mass Upload Assgn Marks</a></li>
	                    <li><a href="/exam/uploadAssignmentStatusForm">Upload Assgn Status</a></li>
	                    <li><a href="/exam/uploadAssignmentFilesForm">Upload Assgn Questions</a></li>
	                    <li><a href="/exam/searchAssignmentSubmissionForm">Check Assgn Status</a></li>
	                    <li><a href="/exam/copyCaseCheckForm">Check Copy Cases</a></li>
	                    <li><a href="/exam/markCopyCasesForm">Mark Copy Cases</a></li>
	                    <li><a href="/exam/allocateAssignmentEvaluationForm?level=1">Allocate Assignments Evaluation Level 1</a></li>
	                    <!-- <li><a href="/exam/allocateAssignmentEvaluationForm?level=2">Allocate Assignments Evaluation Level 2</a></li> 
	                    <li><a href="/exam/allocateAssignmentEvaluationForm?level=3">Allocate Assignments Evaluation Level 3</a></li> -->
	                    <li><a href="/exam/allocateAssignmentEvaluationForm?level=4">Reval Allocation</a></li>
	                    <li><a href="/exam/allocateAssignmentEvaluationForm?level=2">Reval Allocation - Second Level</a></li>
	                    <li><a href="/exam/searchAssignmentToEvaluateForm">Assignments Evaluation Status</a></li>
	                    <li><a href="/exam/resetFacultyAssignmentAllocationForm?level=1">Reset Faculty Assignment Allocation Level 1</a></li>
	                    <li><a href="/exam/extendedAssignmentSubmission">Extend Assignment/Project Submission Time</a></li>
	                    <!-- <li><a href="/exam/resetFacultyAssignmentAllocationForm?level=2">Reset Faculty Assignment Allocation Level 2</a></li>
	                    <li><a href="/exam/resetFacultyAssignmentAllocationForm?level=3">Reset Faculty Assignment Allocation Level 3</a></li> -->
                   
                      </ul>
				</li>
               <%} %>
            <%if(roles.indexOf("SR Admin") != -1){%>
            	<li class="has-sub-menu">
                  	<a href="#">Assign</a>
                      <ul class="subMenu">
                      	<li><a href="/exam/searchAssignmentSubmissionForm">Check Assgn Status</a></li>
                      </ul>
                      </li>
            <%} %>
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Assign Results</a>
                      <ul class="subMenu">
                    <li><a href="/exam/insertANSRecordsForm">Insert ANS records in Marks table(Regular Cycle Only)</a></li>
                    <li><a href="/exam/downloadNormalizedScoreForm">Download Normalized Score</a></li>
                    <li><a href="/exam/moveNormalizedScoreToMarksTableForm">Move Assignment Marks for Pass Fail trigger</a></li>
                      </ul>
				</li>
               <%} %>
                <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Case Study</a>
                      <ul class="subMenu">
	                      <li><a href="/exam/uploadCaseStudyFilesForm">Upload Case Study Question</a></li>
	                      <li><a href="/exam/searchSubmittedCaseStudyFilesForm">Search Submitted Case Study</a></li>
	                      <li><a href="/exam/assignSubmittedCaseStudyToFacultyForm">Assign Case Study To Faculty</a></li>
	                      <li><a href="/exam/searchEvaluatedCaseStudyForm">Search Evaluated Case Study</a></li>
                      </ul>
				</li>
               <%} %>
            
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Project</a>
                      <ul class="subMenu">
                    <li><a href="/exam/searchProjectSubmissionForm">Search Project Submissions</a></li>
                    <li><a href="/exam/allocateProjectEvaluationForm">Allocate Project Evaluations</a></li>
                    <li><a href="/exam/allocateProjectRevaluationForm">Allocate Project Re-valuations</a></li>
                    <li><a href="/exam/searchProjectToEvaluateForm">Evaluate Projects</a></li>
                      </ul>
				</li>
               <%} %>
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
				     <li class="has-sub-menu">
				     	<a href="#">MASS DOWNLOADS</a>
				  		<ul class="subMenu">
					       <li><a href="/exam/massHallTicketDownloadForm">Mass HallTicket</a></li>
					       <li><a href="/exam/massFeeReceiptDownloadForm">Mass FeeReceipt</a></li>
					       <li><a href="/acads/massPCPBookingDownloadForm">Mass PCPBooking</a></li>
				      </ul>
				    </li>  
				    <%} %>
        
        <%if(roles.indexOf("Faculty") != -1) {%>
        
                 <li class="has-sub-menu">
                  	<a href="#">Evaluation</a>
                      <ul class="subMenu">
                      	<li><a href="/exam/searchAssignmentToEvaluateForm">Evaluate Assignments</a></li>
                      	<li><a href="/exam/searchProjectToEvaluateForm">Evaluate Projects</a></li>
                      		<li><a href="/exam/searchAssignedCaseStudyFilesForm">Evaluate Case Study</a></li>
                      </ul>
				</li>
           
             <%} %>
			<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#">TEE</a>
                      <ul class="subMenu">
                      	<li><a href="/exam/addStudentMarksForm">Single Entry</a></li>
							<li><a href="/exam/uploadWrittenMarksForm">Mass Upload Offline Exam Marks</a></li>
						 	<li><a href="/exam/uploadOnlineExamMarksForm">Mass Upload Online Exam Marks</a></li>
							<li><a href="/exam/uploadOnlineWrittenRevalMarksForm">Mass Upload Online Exam Reval Marks</a></li>
							<li><a href="/exam/evaluationSheetForm">Evaluation Sheet</a></li>
 							<li><a href="/exam/insertABRecordsForm">Online Exam Absent Students List</a></li> 
							<li><a href="/exam/insertExecutiveABRecordsForm">Executive Exam Absent Students List</a></li>
							<li><a href="/exam/markCheckSheetForm">TEE Marks Check Sheet</a></li>
							<li><a href="/exam/examResultProcessingChecklist">Result Processing CheckList</a></li>
							<li><a href="/exam/executiveResultProcessingChecklist">Executive Result Processing CheckList</a></li>
 							<li><a href="/exam/passFailTriggerSearchForm">Pass Fail Trigger</a></li>
							<li><a href="/exam/getGraceEligibleForm">Grace</a></li>
							<li><a href="/exam/makeResultsLiveForm">Make Results Live</a></li>
							<li><a href="/studentportal/demoExamICLC">Demo Exams</a></li>
							<!-- <li><a href="/exam/readSifyDataForm">Pull Sify Results</a></li>
							<li><a href="/exam/transferSifyResultsToOnlineMarks">Transfer Sify Results to Marks Table</a></li>
							<li><a href="/exam/insertABRecordsForm">Online Exam Absent Students List</a></li>
							<li><a href="/exam/uploadWrittenMarksForm">Upload Student Marks for UMC </a></li>
	       					<li><a href="/exam/viewSifyMarks">View Sify Results</a></li> -->
					   </ul>
					</li>
               <%} %>
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Exam Results"><i class="fa fa-trophy fa-lg"></i></a>
                      <ul class="subMenu">
                      	<li><a href="/exam/passFailTriggerSearchForm">Pass Fail Trigger</a></li>
						<li><a href="/exam/getGraceEligibleForm">Grace</a></li>
						<li><a href="/exam/makeResultsLiveForm">Make Results Live</a></li>
						<li><a href="/exam/marksheetForm">Marksheet</a></li>
						<li><a href="/exam/singleStudentMarksheetForm">Single Marksheet</a></li>
						<li><a href="/exam/marksheetFromSRForm">Marksheet from SR</a></li>
						<li><a href="/exam/certificateFromSRForm">Certificate From SR</a></li>
						<li><a href="/exam/singleStudentCertificateForm">Single Certificate</a></li>
						<li><a href="/exam/transcriptForm">Transcript</a></li>
                      </ul>
				</li>

               <%} %>
            
            <%if(roles.indexOf("Exam Admin") != -1 ){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Review Question File"><i class="fa fa-question-circle fa-lg" ></i></a>
                      <ul class="subMenu">
                      	<li><a href="/exam/uploadQuestionFileForm">Review Question File</a></li>
                      </ul>
				</li>

               <%} %>
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Read Admin") != -1
             		|| roles.indexOf("TEE Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Exam Registration">Exam Reg</a>
                    <ul class="subMenu">
                    	<li><a href="/exam/centerUserMappingForm" class="">Upload Center User Mapping</a></li>
	                    <li><a href="/exam/examRegistrationChecklist" class="">Executive Exam Registration Checklist</a></li>
	                    <li><a href="/exam/addExamCenterForm" class="">Add Exam Center</a></li>
	                     <li><a href="/exam/excelUploadForExamCentersForm" class="">Upload Mass Exam Center</a></li>
	                 <!--   <li><a href="/exam/addExecutiveExamCenterFormMassUpload" class="">Upload Executive Mass Exam Center</a></li> -->
	                   <!--  <li><a href="/exam//exam/addExecutiveExamCenterForm" class="">Add Executive Exam Center</a></li> -->
	                	<li><a href="/exam/uploadExamFeeExemptForm" class="">Upload No Exam Fee Students</a></li>
	                	<li><a href="/exam/uploadExamFeeExemptSubjectsForm" class="">Upload No Exam Fee Students-Subjects</a></li>
						<li><a href="/exam/searchDDsToApproveForm">Approve DD</a></li>
						<li><a href="/exam/queryTransactionStatusForm">Query Transaction</a></li>
						<li><a href="/exam/searchBookingsToReleaseForm">Release Seats</a></li>
						<li><a href="/exam/uploadSeatRealeseFileForm">Mass Release Seats</a></li>
						<li><a href="/exam/searchExamBookingTOChangeCenterForm">Change Center</a></li>
						<li><a href="/exam/queryFeesPaidForm">Refund</a></li>
						<li><a href="/exam/findOnlineTranConflictForm">Transaction Mismatch</a></li>
						<li><a href="/exam/examBookingDashboardForm">Booking Dashboard</a></li>
						<li><a href="/exam/attendanceSheetForm">Attendance Sheet</a></li>
						<li><a href="/exam/downloadHallTicketForm">Hall Ticket</a></li>
						<li><a href="/exam/downloadExecutiveHallTicketForm">Executive Hall Ticket</a></li>
						<!-- <li><a href="/exam/onlineExamPasswordReportForm">Online Exam Passwords</a></li> -->
                    </ul>
					</li>
               <%} %>
               
               <%if(roles.indexOf("Consultant") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Exam Registration">Exam Reg</a>
                    <ul class="subMenu">
						<li><a href="/exam/examBookingDashboardForm">Booking Dashboard</a></li>
                    </ul>
					</li>
               <%} %>
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1
            		|| roles.indexOf("Read Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>   
               <li class="has-sub-menu">
                  	<a href="#" title="Search"><i class="fa fa-search" ></i></a>
	                    <ul class="subMenu">
							<li><a href="/exam/searchStudentMarksForm">Search Marks</a></li>
							<li><a href="/exam/searchRegisteredStudentMarksForm">Search Exam Registered Student Marks</a></li>
							<li><a href="/exam/searchOnlineMarksForm" >Search Online Marks</a></li>
							<li><a href="/exam/searchPassFailForm">Search Pass Fail</a></li>
							<li><a href="/exam/searchStudentsForm">Search Students</a></li>
		              		<li><a href="/exam/searchStudentsRegistrationsForm">Search Student Registrations</a></li>
		              		<li><a href="/exam/searchAssignmentStatusForm">Search Assgn Status</a></li>
							<li><a href="/exam/searchAssignmentFilesForm">Search Assgn Questions</a></li>
							<li><a href="/exam/searchExamCenterForm">Search Exam Centers </a></li>
							<li><a href="/exam/searchExecutiveExamCenter">Search Executive Exam Centers </a></li>
							<li><a href="/exam/searchExamBookingConflictForm">Search Exam Booking Conflict </a></li>
						</ul>
					</li>
                <%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
                
                <li class="has-sub-menu">
                  	<a href="#" title="Search"><i class="fa fa-search" ></i></a>
                      <ul class="subMenu">
	             	<li><a href="/exam/searchStudentMarksForm">Search Marks</a></li> 
	             	<li><a href="/exam/searchPassFailForm">Search Pass Fail</a></li>
	             	<li><a href="/exam/searchStudentsForm">Search Students</a></li>
	           		<li><a href="/exam/searchStudentsRegistrationsForm">Search Student Registrations</a></li>
	           		<li><a href="/exam/searchAssignmentStatusForm">Search Assgn Status</a></li>
	           		
	           		</ul>
				</li>
             <% }%>

		<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>

		      <li class="has-sub-menu">
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
                 
                 <li class="has-sub-menu">
                  	<a href="#" title="Exam Time Table"><i class="fa fa-table fa-lg" ></i></a>
                      <ul class="subMenu">
                      <li><a href="/exam/uploadTimetableForm">Upload Timetable</a></li>
						<li><a href="/exam/adminTimeTableForm">View Timetable</a></li>
						<li><a href="/exam/makeResultsLiveForm">Make Timetable Live for Exam Registration</a></li>
                     	<!-- <li><a href="/exam/uploadSASTimetableForm">Upload Executive Timetable</a></li> -->
                     	<!-- <li><a href="/exam/makeExecutiveTimetableLiveForm">Make Live Executive TimeTable</a></li> -->
                     	<li><a href="/exam/adminExecutiveTimeTableForm">View Executive Timetable</a></li>
                      </ul>
				</li>
               <%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
               	 <li class="has-sub-menu">
                  	<a href="#" title="Exam Time Table"><i class="fa fa-table fa-lg"></i></a>
                      <ul class="subMenu">
							<li><a href="/exam/adminTimeTableForm">View Timetable</a></li>
                      </ul>
				</li>
              	<% }%>  
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1
         || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("SR Admin") != -1 || roles.indexOf("Consultant") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#" title="Exam Reports"><i class="fa fa-bar-chart fa-lg" ></i></a>
                      <ul class="subMenu">
                     	 <li><a href="/exam/reportHome">Exam Reports</a></li>
                      </ul>
				</li>
               <%} %>  
            
           <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Settings"><i class="fa fa-cog fa-lg" aria-hidden="true"></i></a>
                      <ul class="subMenu">
                     	 <li><a href="/studentportal/loginAsForm">Exam:Log In As</a></li>
                     	 <li><a href="/exam/makeResultsLiveForm">Make Live Settings</a></li>
                     	 <li><a href="/exam/changeConfigurationForm">Set up dates</a></li>
                     	 <!-- <li><a href="/exam/makeExecutiveRegistrationLiveForm">Make Live Executive</a></li> -->
                      	 
                      </ul>
				</li>
               <%} %>  
               
                <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
           <li class="has-sub-menu">
                  	<a href="#" title="Internal Assessment"><i class="fa fa-flask fa-lg" aria-hidden="true"></i></a>
                      <ul class="subMenu">
                     	 <li><a href="/exam/addTestForm">Add Test</a></li>
                     	 <li><a href="/exam/viewAllTests">View All Tests</a></li>
                      </ul>
				</li>
               <%} %>  
               
               