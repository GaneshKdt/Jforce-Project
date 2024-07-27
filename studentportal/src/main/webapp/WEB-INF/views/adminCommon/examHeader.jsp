<%

String roles = request.getParameter("roles");

%>
			<%if(roles.indexOf("Faculty") == -1){ %>
			<li><a href="/exam/updateExamBookingDataFormTCS">Reschedule <br> Exam Booking</a></li>
			<%} %>
			<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Assign</a>
                      <ul class="sz-sub-menu">
                      	<li><a href="/exam/admin/uploadAssignmentMarksForm">Mass Upload Assgn Marks</a></li>
	                    <li><a href="/exam/uploadAssignmentStatusForm">Upload Assgn Status</a></li>
	                    <li><a href="/exam/uploadAssignmentFilesForm">Upload Assgn Questions</a></li>
	                    <li><a href="/exam/makeAssignmentLiveForm">Make Assgn Live</a></li>
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
	                    <li><a href="/exam/extendedAssignmentSubmission">Extend Assignment/Project Submission Time</a></li>

	                    <!-- <li><a href="/exam/resetFacultyAssignmentAllocationForm?level=2">Reset Faculty Assignment Allocation Level 2</a></li>
	                    <li><a href="/exam/resetFacultyAssignmentAllocationForm?level=3">Reset Faculty Assignment Allocation Level 3</a></li> -->
                   
                      </ul>
				</li>
               <%} %>
            <%if(roles.indexOf("SR Admin") != -1){%>
            	<li class="has-sub-menu">
                  	<a href="#">Assign</a>
                      <ul class="sz-sub-menu">
                      	<li><a href="/exam/searchAssignmentSubmissionForm">Check Assgn Status</a></li>
                      </ul>
                      </li>
            <%} %>
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Assign Results</a>
                      <ul class="sz-sub-menu">
                    <li><a href="/exam/insertANSRecordsForm">Insert ANS records in Marks table(Regular Cycle Only)</a></li>
                    <li><a href="/exam/downloadNormalizedScoreForm">Download Normalized Score</a></li>
                    <li><a href="/exam/moveNormalizedScoreToMarksTableForm">Move Assignment Marks for Pass Fail trigger</a></li>
                      </ul>
				</li>
               <%} %>
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1  || roles.indexOf("TEE Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Case Study</a>
                      <ul class="sz-sub-menu">
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
                      <ul class="sz-sub-menu">
                    <li><a href="/exam/searchProjectSubmissionForm">Search Project Submissions</a></li>
                    <li><a href="/exam/allocateProjectEvaluationForm">Allocate Project Evaluations</a></li>
                    <li><a href="/exam/allocateProjectRevaluationForm">Allocate Project Re-valuations</a></li>
                    <li><a href="/exam/projectCopyCaseCheckForm">Check Copy Cases</a></li> 
                    <li><a href="/exam/projectMarkCopyCasesForm">Mark Copy Cases</a></li>  
                    <li><a href="/exam/searchProjectToEvaluateForm">Evaluate Projects</a></li>
                      </ul>
				</li>
               <%} %>
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
				     <li class="has-sub-menu">
				     	<a href="#">MASS DOWNLOADS</a>
				  		<ul class="sz-sub-menu">
					       <li><a href="/exam/massHallTicketDownloadForm">Mass HallTicket</a></li>
					       <li><a href="/exam/massFeeReceiptDownloadForm">Mass FeeReceipt</a></li>
					       <li><a href="/acads/massPCPBookingDownloadForm">Mass PCPBooking</a></li>
				      </ul>
				    </li>  
				    <%} %>
           <%if(roles.indexOf("Faculty") != -1 ){ %>
                 <li class="has-sub-menu">
                  	<a href="#">Evaluation</a>
                      <ul class="sz-sub-menu">
                      	<li><a href="/exam/searchAssignmentToEvaluateForm">Evaluate Assignments</a></li>
                      	<li><a href="/exam/searchProjectToEvaluateForm">Evaluate Projects</a></li>
                      	<li><a href="/exam/searchAssignedCaseStudyFilesForm">Evaluate Case Study</a></li>
                      </ul>
				</li>
				
               	<li>
					  	<a href="#">Queries for Me</a>
					  	<ul class="sz-sub-menu">
                      	<li><a href="/acads/assignedCourseQueries">Course Queries</a></li>
                      	<li><a href="/acads/gotoFacultySessionList">Live Zoom session Queries</a></li>
                      </ul>
                </li>
               <%} %>
             
                <%if(roles.indexOf("Faculty") != -1 ){ %>
                <li class="menu">
                  	<a href="/acads/updateFacultyProfileForm"  title="My Profile"><i class="fa fa fa-user fa-lg" ></i></a>
           </li>
               <%}%>
               
			<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#">TEE</a>
                  	
                  	  <ul class="sz-sub-menu">
                     		<li><a href="/studentportal/admin/demoExamICLC">Demo Exams</a></li>
                      		<li><a href="/exam/admin/addStudentMarksForm">Single Entry</a></li>
							<li><a href="/exam/admin/uploadWrittenMarksForm">Mass Upload Offline Exam Marks</a></li>
							<li><a href="/exam/admin/uploadOnlineExamMarksForm">Mass Upload Online Exam Marks</a></li> 
							<li><a href="/exam/admin/uploadOnlineWrittenRevalMarksForm">Mass Upload Online Exam Reval Marks</a></li>
							<li><a href="/exam/admin/evaluationSheetForm">Evaluation Sheet</a></li>
							<li><a href="/exam/admin/insertABRecordsForm">Online Exam Absent Students List</a></li> 
							<li><a href="/exam/insertExecutiveABRecordsForm">Executive Exam Absent Students List</a></li>
							<li><a href="/exam/admin/markCheckSheetForm">TEE Marks Check Sheet</a></li>
							<li><a href="/exam/admin/examResultProcessingChecklist">Result Processing CheckList</a></li>
							<li><a href="/exam/admin/executiveResultProcessingChecklist">Executive Result Processing CheckList</a></li>
							<!-- <li><a href="/exam/readSifyDataForm">Pull Sify Results</a></li>
							<li><a href="/exam/viewSifyMarks">View Sify Results</a></li>
							<li><a href="/exam/transferSifyResultsToOnlineMarks">Transfer Sify Results to Marks Table</a></li> -->
							<li><a href="/exam/admin/insertABRecordsForm">Upload Online Exam Absent Students List</a></li>
							 <li><a href="/exam/admin/uploadWrittenMarksForm">Upload Student Marks for RIA/NV Cases </a></li> 
							<li><a href="/exam/admin/passFailTriggerSearchForm">Pass Fail Trigger</a></li>
	       					<li><a href="/exam/admin/getGraceEligibleForm">Grace</a></li>
							<li><a href="/exam/admin/makeResultsLiveForm">Make Results Live</a></li>
							<!-- <li><a href="/exam/embaExamResultProcessingChecklist">EMBA Result Processing CheckList</a></li> -->
							
					   </ul>
					</li>
					<li class="has-sub-menu">
                  		<a href="/exam/ufmCheckListForm">UFM</a>
                 	</li>
               <%} %>
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#">MBA-WX TEE</a>
                      <ul class="sz-sub-menu">
                      		 <li><a href="/exam/embaExamResultProcessingChecklist">EMBA Result Processing CheckList</a></li>
							<!-- <li><a href="/exam/uploadTEEMarksForm">Upload MBA-WX TEE Marks</a></li>
							<li><a href="/exam/examAssessmentPanelForm">Add Assessment</a></li> -->
							<li><a href="/exam/embaPassFailReportForm">EMBA PassFail Report</a></li> 
					   </ul>
					</li>
               <%} %>
               
               <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#">MBA-X TEE</a>
                      <ul class="sz-sub-menu">
                     		 <li><a href="/exam/upgradExamResultsProcessingChecklist">MBAX Result Processing CheckList</a></li>
							<!-- <li><a href="/exam/uploadTEEMarksForm">Upload MBA-WX TEE Marks</a></li>
							<li><a href="/exam/examAssessmentPanelForm">Add Assessment</a></li> -->

							<!-- <li><a href="/exam/embaPassFailReportForm">EMBA PassFail Report</a></li> -->  
					   </ul>
					</li>
               <%} %>
               
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Exam Results"><i class="fa fa-trophy fa-lg"></i></a>
                      <ul class="sz-sub-menu">
                      	<li><a href="/exam/admin/passFailTriggerSearchForm">Pass Fail Trigger</a></li>
						<li><a href="/exam/admin/getGraceEligibleForm">Grace</a></li>
						<li><a href="/exam/admin/makeResultsLiveForm">Make Results Live</a></li>
						<li><a href="/exam/marksheetForm">Marksheet</a></li>
						<li><a href="/exam/admin/singleStudentMarksheetForm">Single Marksheet</a></li>
						<li><a href="/exam/marksheetFromSRForm">Marksheet from SR</a></li>
						<li><a href="/exam/marksheetFromSRFormForMBAWX">Gradesheet from SR For MBA-WX</a></li>
						<li><a href="/exam/marksheetFromSRFormForMBAX">Gradesheet from SR For MBA-X</a></li>
						<li><a href="/exam/certificateFromSRForm">Certificate From SR</a></li>
						<li><a href="/exam/mbawxCertificateFromSRForm">Mbawx Certificate From SR</a></li>
						<li><a href="/exam/singleStudentCertificateForm">Single Certificate</a></li>
						<li><a href="/exam/downloadExecutiveMarksheetForm">Executive Bulk Marksheet</a></li>
                     	<li><a href="/exam/executiveCertificatesForBatchForm">Executive Bulk Certificates </a></li>
						<li><a href="/exam/certificateFromSapIdForm">Certificate From SapId</a></li>
						<li><a href="/exam/transcriptForm">Transcript</a></li>
						<li><a href="/exam/transcriptFormMBAWX">Transcript - MBA WX</a></li>
                      </ul>
				</li>

               <%} %>
               
              <% if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Assignment Admin") != -1) { %>
               <li class="has-sub-menu">
                 	<a href="#" title="Remarks Grade">Remarks Grade</a>
                     <ul class="sz-sub-menu">
                     	<li><a href="/exam/admin/stepRG0">Remarks Grade Checklist</a></li>
                     </ul>
			</li>
              <% } %>
            
            <%if(roles.indexOf("Exam Admin") != -1 ){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Review Question File"><i class="fa fa-question-circle fa-lg" ></i></a>
                      <ul class="sz-sub-menu">
                      	<li><a href="/exam/uploadQuestionFileForm">Review Question File</a></li>
                      </ul>
				</li>

               <%} %>
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Read Admin") != -1
             		|| roles.indexOf("TEE Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Exam Registration">Exam Reg</a>
                    <ul class="sz-sub-menu">
                    	<li><a href="/exam/centerUserMappingForm" class="">Upload Center User Mapping</a></li>
	                    <li><a href="/exam/examExecutiveRegistrationChecklist" class="">Executive Exam Registration Checklist</a></li>
	                      <li><a href="/exam/examRegularRegistrationChecklist">Regular Exam Reg Checklist</a></li>
	                    <li><a href="/exam/addExamCenterForm" class="">Add Exam Center</a></li>
	                     <li><a href="/exam/excelUploadForExamCentersForm" class="">Upload Mass Exam Center</a></li>
	                  <!--   <li><a href="/exam/addExecutiveExamCenterFormMassUpload" class="">Upload Executive Mass Exam Center</a></li> -->
	                    <!-- <li><a href="/exam/addExecutiveExamCenterForm" class="">Add Executive Exam Center</a></li> -->
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
						<li><a href="/exam/downloadExecutiveHallTicketForm">Executive Hall Ticket</a></li>
						<!-- <li><a href="/exam/admin/onlineExamPasswordReportForm">Online Exam Passwords</a></li> -->
						<li><a href="/exam/insertExamBookingDataFormTCS">Upload Exam Booking Data To TCS</a></li>	
						<li><a href="/exam/examBookingRefundRequestReport">Exam Booking Refund Request Report</a></li>
						<li><a href="/exam/admin/demoExamDashBoard">Demo Exam DashBoard</a></li>					
                    </ul>
					</li>
               <%} %>
               <%if(roles.indexOf("Consultant") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Exam Registration">Exam Reg</a>
                    <ul class="sz-sub-menu">
						<li><a href="/exam/admin/examBookingDashboardForm">Booking Dashboard</a></li>
                    </ul>
					</li>
               <%} %>
               
               <% if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Read Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Exam Registration">Finance Reports</a>
                    <ul class="sz-sub-menu">
						<li><a href="/exam/admin/examBookingDashboardForm">Booking Dashboard</a></li>
						<li><a href="/exam/admin/cumulativeFinanceReportForm">Date wise Exam Registration Revenue</a></li>
						<li><a href="/exam/admin/operationsRevenueForm">All Operations Revenue</a></li>
                    </ul>
					</li>
               <%} %>
               
           	  <%
           	  	boolean isAdmin = roles.indexOf("Acads Admin") != -1 || roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1;
           	  	boolean isFinance = roles.indexOf("Finance") != -1;
           	  	if( isAdmin || isFinance ){ %>
				<li class="has-sub-menu">
					<a href="#">Exam Reg MBA-WX</a>
					<ul class="sz-sub-menu">
						<% 
							if(isAdmin) {
						%>
							<li><a href="/exam/uploadCentersForm_MBAWX">Upload Centers</a></li>
		                    <li><a href="/exam/uploadTimeTableForm_MBAWX">Upload TimeTable</a></li>
		                    <li><a href="/exam/slotsList_MBAWX">View Slots</a></li>
							<li><a href="/exam/examCenterCapacityReportForm_MBAWX">Center Capacity Report</a></li>
							<li><a href="/exam/uploadLiveSettingsForm_MBAWX">Live Settings</a></li>
						<% 
							}
							if(isAdmin || isFinance) {
						%>
							<li><a href="/exam/examBookingReportForm_MBAWX">Exam Booking Report</a></li>
							<li><a href="/exam/reExamEligibilityReportForm">Re-Exam Eligible Students Report</a></li>
							<li><a href="/exam/queryTransactionStatusFormMBAWX">Query Transaction</a></li>
						<% 
							}
						%>
					</ul>
				</li>
                <%} %>
           	  <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 <li class="has-sub-menu">

                   <a href="#">Exam Reg MBA-X</a>
                      <ul class="sz-sub-menu">
	                      <li><a href="/exam/uploadCentersForm_MBAX">Upload Centers</a></li>
	                      <li><a href="/exam/uploadTimeTableForm_MBAX">Upload TimeTable</a></li>
	                      <li><a href="/exam/queryTransactionStatusForm_MBAX">Query Transaction</a></li>
	                      <!-- <li><a href="/exam/refundPaymentFormMBAX">Query Refund Transaction</a></li> -->
	                      <li><a href="/exam/uploadLiveSettingsForm_MBAX">Live Settings</a></li>
	                      <li><a href="/exam/examBookingReportForm_MBAX">Download Exam Bookings</a></li>  
	                      <li><a href="/exam/examCenterCapacityReportForm_MBAX">Exam Center Available Capacity</a></li>   
						  <li><a href="/exam/reExamEligibilityReportForm">Re-Exam Eligible Students Report</a></li>
         			  </ul>


      			 </li>
                <%} %>
                
                <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 <li class="has-sub-menu">
                   <a href="#">MBA-X IA</a>
                   <ul class="sz-sub-menu">
	                      <li><a href="/exam/validateTestDetailsForm">Verify Test Data For UPGRAD</a></li>
	                      <li><a href="/exam/liveSettingMBAX">Search Test</a>
         			  </ul>

      			 </li>
                <%} %>
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1
            		|| roles.indexOf("Read Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>   
               <li class="has-sub-menu">
                  	<a href="#" title="Search"><i class="fa fa-search" ></i></a>
	                    <ul class="sz-sub-menu">
							<li><a href="/exam/admin/searchStudentMarksForm">Search Marks</a></li>
							<li><a href="/exam/admin/searchRegisteredStudentMarksForm">Search Exam Registered Student Marks</a></li>
							<li><a href="/exam/admin/searchOnlineMarksForm" >Search Online Marks</a></li>
							<li><a href="/exam/admin/searchPassFailForm">Search Pass Fail</a></li>
							<li><a href="/exam/searchStudentsForm">Search Students</a></li>
		              		<li><a href="/exam/searchStudentsRegistrationsForm">Search Student Registrations</a></li>
		              		<li><a href="/exam/searchAssignmentStatusForm">Search Assgn Status</a></li>
							<li><a href="/exam/searchAssignmentFilesForm">Search Assgn Questions</a></li>
							<li><a href="/exam/searchExamCenterForm">Search Exam Centers </a></li>
							<li><a href="/exam/searchExecutiveExamCenter">Search Executive Exam Centers </a></li>
							<li><a href="/exam/searchExamBookingConflictForm">Search Exam Booking Conflict </a></li>
							<li><a href="/exam/searchStudentBatchMappingForm">Search Student Batch Mapping</a>
						</ul>
					</li>
                <%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
                
                <li class="has-sub-menu">
                  	<a href="#" title="Search"><i class="fa fa-search" ></i></a>
                      <ul class="sz-sub-menu">
	             	<li><a href="/exam/admin/searchStudentMarksForm">Search Marks</a></li> 
	             	<li><a href="/exam/admin/searchPassFailForm">Search Pass Fail</a></li>
	             	<li><a href="/exam/searchStudentsForm">Search Students</a></li>
	           		<li><a href="/exam/searchStudentsRegistrationsForm">Search Student Registrations</a></li>
	           		<li><a href="/exam/searchAssignmentStatusForm">Search Assgn Status</a></li>
	           		
	           		</ul>
				</li>
             <% }%>

		<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>

		      <li class="has-sub-menu">
		        <a href="#" title="Enrollment"><i class="fa fa-user-plus fa-lg"></i></a>
		        <ul class="sz-sub-menu">
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
                      <ul class="sz-sub-menu">
                      <li><a href="/exam/uploadTimetableForm">Upload Timetable</a></li>
						<li><a href="/exam/adminTimeTableForm">View Timetable</a></li>
						<li><a href="/exam/admin/makeResultsLiveForm">Make Timetable Live for Exam Registration</a></li>
						<!-- <li><a href="/exam/uploadSASTimetableForm">Upload Executive Timetable</a></li> -->
						<!-- <li><a href="/exam/makeExecutiveTimetableLiveForm">Make Live Executive TimeTable</a></li> -->
						<li><a href="/exam/adminExecutiveTimeTableForm">View Executive Timetable</a></li>
                      </ul>
				</li>
               <%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
               	 <li class="has-sub-menu">
                  	<a href="#" title="Exam Time Table"><i class="fa fa-table fa-lg"></i></a>
                      <ul class="sz-sub-menu">
							<li><a href="/exam/adminTimeTableForm">View Timetable</a></li>
                      </ul>
				</li>
              	<% }%>  
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1
         || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("SR Admin") != -1
         || roles.indexOf("Consultant") != -1 || roles.indexOf("Finance") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#" title="Exam Reports"><i class="fa fa-bar-chart fa-lg" ></i></a>
                      <ul class="sz-sub-menu">
                     	 <li><a href="/exam/admin/reportHome">Exam Reports</a></li>
                     	 <li><a href="/studentportal/syncPassedSemList">Salesforce Exam Report</a></li>
                     	 <li><a href="/studentportal/schedulerApisDashboard">Scheduler Apis Dashboard</a></li>
						 <li><a href="/exam/admin/demoExamDashBoard">Demo Exam Dashboard</a></li>
                      </ul>
				</li>
               <%} %>  
            
           <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Settings"><i class="fa fa-cog fa-lg" aria-hidden="true"></i></a>
                      <ul class="sz-sub-menu">
                     	 <li><a href="/studentportal/loginAsForm">Exam:Log In As</a></li>
                     	 <li><a href="/exam/admin/makeResultsLiveForm">Make Live Settings</a></li>
                     	 <li><a href="/exam/admin/changeConfigurationForm">Set up dates</a></li>
                     	  <!-- <li><a href="/exam/makeExecutiveRegistrationLiveForm">Make Live Executive  </a></li> -->
						                     
                      </ul>
				</li>
               <%} %>  
               
             <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
             <li class="has-sub-menu">
                  	<a href="#" title="Internal Assessment"><i class="fa fa-flask fa-lg" aria-hidden="true"></i></a>
                      <ul class="sz-sub-menu">
                      	 <li><a href="/exam/makeTestLiveForm">Make Tests Live</a></li>
                     	 <li><a href="/exam/addTestForm">Add Test</a></li>
                     	 <li><a href="/exam/viewAllTests">Search Tests</a></li>
                     	 <li><a href="/exam/consolidatedStudentsWiseReportsForm">Consolidated Students Wise Reports</a></li>
                     	 <li><a href="/exam/searchIaToEvaluateForm">IA Evaluates Reports</a></li>
                     	 <li><a href="/exam/admin/studentTestAuditTrailAnalysisForm">Student Test Audit Trail</a></li>
                     	 <li><a href="/exam/unfairMeansDetials">Lost Focus Details</a></li>
                      </ul>
				</li>
               <%} %>

               
               
               
            