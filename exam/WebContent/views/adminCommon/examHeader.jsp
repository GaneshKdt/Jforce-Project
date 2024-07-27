<%

String roles = request.getParameter("roles");

%>
			<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Assign</a>
                      <ul class="sz-sub-menu">
                      	<li><a href="/exam/admin/uploadAssignmentMarksForm">Mass Upload Assgn Marks</a></li>
	                    <li><a href="/exam/uploadAssignmentStatusForm">Upload Assgn Status</a></li>
	                    <li><a href="/exam/admin/uploadAssignmentFilesForm">Upload Assgn Questions</a></li>
	                    <li><a href="/exam/admin/makeAssignmentLiveForm">Make Assgn Live</a></li>
	                    <li><a href="/exam/admin/searchAssignmentSubmissionForm">Check Assgn Status</a></li>
	                    <li><a href="/exam/admin/copyCaseCheckForm">Check Copy Cases</a></li>
	                    <li><a href="/exam/admin/getCopyCaseReportForm">Copy Case Report Download</a></li>
	                    <li><a href="/exam/admin/assignmentDetailedThresholdCCForm">Detailed Threshold Copy Cases</a></li>
	                    <li><a href="/exam/admin/markCopyCasesForm">Mark & UnMark Copy Cases</a></li>
	                    <li><a href="/exam/admin/allocateAssignmentEvaluationForm?level=1">Allocate Assignments Evaluation Level 1</a></li>
	                    <!-- <li><a href="/exam/admin/allocateAssignmentEvaluationForm?level=2">Allocate Assignments Evaluation Level 2</a></li> 
	                    <li><a href="/exam/admin/allocateAssignmentEvaluationForm?level=3">Allocate Assignments Evaluation Level 3</a></li> -->
	                    <li><a href="/exam/admin/allocateAssignmentEvaluationForm?level=4">Reval Allocation</a></li>
	                    <li><a href="/exam/admin/allocateAssignmentEvaluationForm?level=2">Reval Allocation - Second Level</a></li>
	                    <li><a href="/exam/admin/searchAssignmentToEvaluateForm">Assignments Evaluation Status</a></li>
	                    <li><a href="/exam/admin/resetFacultyAssignmentAllocationForm?level=1">Reset Faculty Assignment Allocation Level 1</a></li>
	                    <li><a href="/exam/admin/extendedAssignmentSubmission">Extend Assignment/Project Submission Time</a></li>
	                    <li><a href="/exam/admin/removalOfFacultyFromRevaluationForm">Remove Faculty From Reval Stages</a></li>     
	                    <li><a href="/exam/admin/webCopyCaseCheckForm">Check Web Copy Cases</a></li>               
	                    
	                    <!-- <li><a href="/exam/admin/resetFacultyAssignmentAllocationForm?level=2">Reset Faculty Assignment Allocation Level 2</a></li>
	                    <li><a href="/exam/admin/resetFacultyAssignmentAllocationForm?level=3">Reset Faculty Assignment Allocation Level 3</a></li> -->                        
                      </ul>
				</li>
               <%} %>
            <%if(roles.indexOf("SR Admin") != -1){%>
            	<li class="has-sub-menu">
                  	<a href="#">Assign</a>
                      <ul class="sz-sub-menu">
                      	<li><a href="/exam/admin/searchAssignmentSubmissionForm">Check Assgn Status</a></li>
                      </ul>
                      </li>
            <%} %>
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Assign Results</a>
                      <ul class="sz-sub-menu">
                    <li><a href="/exam/admin/insertANSRecordsForm">Insert ANS records in Marks table(Regular Cycle Only)</a></li>
                    <li><a href="/exam/admin/downloadNormalizedScoreForm">Download Normalized Score</a></li>
                    <li><a href="/exam/admin/moveNormalizedScoreToMarksTableForm">Move Assignment Marks for Pass Fail trigger</a></li>
                      </ul>
				</li>
               <%} %>
                <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Case Study</a>
                      <ul class="sz-sub-menu">
	                      <li><a href="/exam/admin/uploadCaseStudyFilesForm">Upload Case Study Question</a></li>
	                      <li><a href="/exam/admin/searchSubmittedCaseStudyFilesForm">Search Submitted Case Study</a></li>
	                      <li><a href="/exam/admin/assignSubmittedCaseStudyToFacultyForm">Assign Case Study To Faculty</a></li>
	                      <li><a href="/exam/admin/searchEvaluatedCaseStudyForm">Search Evaluated Case Study</a></li>
                      </ul>
				</li>
               <%} %>
            
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#">Project</a>
                      <ul class="sz-sub-menu">

                    <li><a href="/exam/admin/searchProjectSubmissionForm">Search Project Submissions</a></li>
                    <li><a href="/exam/admin/allocateProjectEvaluationForm">Allocate Project Evaluations</a></li>
                    <li><a href="/exam/admin/allocateProjectRevaluationForm">Allocate Project Re-valuations</a></li>
                    <li><a href="/exam/admin/reallocateProjectEvaluationForm">Reallocate Project Evaluations</a></li>
                    <li><a href="/exam/admin/projectCopyCaseCheckForm">Check Copy Cases</a></li>
                    <li><a href="/exam/admin/getProjectCopyCaseReportForm">Copy Case Report Download</a></li>
					<li><a href="/exam/admin/projectDetailedThresholdCCForm">Detailed Threshold Copy Cases</a></li>
                    <li><a href="/exam/admin/projectMarkCopyCasesForm">Mark Copy Cases</a></li> 
                    <li><a href="/exam/admin/searchProjectToEvaluateForm">Evaluate Projects</a></li>
                    <li><a href="/exam/admin/projectConfigurationChecklistForm">Project Configuration Check List</a></li>
                    <li><a href="/exam/admin/projectPendingReportForm">Project Submission Pending Report</a></li>  
                      </ul>
				</li>
               <%} %>
            <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
				     <li class="has-sub-menu">
				     	<a href="#">MASS DOWNLOADS</a>
				  		<ul class="sz-sub-menu">
						   <li><a href="/exam/admin/massHallTicketDownloadForm">Mass HallTicket</a></li>
					       <li><a href="/exam/admin/massFeeReceiptDownloadForm">Mass FeeReceipt</a></li>
					       <li><a href="/acads/admin/massPCPBookingDownloadForm">Mass PCPBooking</a></li>
				      </ul>
				    </li>  
				    <%} %>
        
        <%if(roles.indexOf("Faculty") != -1 || roles.indexOf("Insofe") != -1 ) {%>
        
                 <li class="has-sub-menu">
                  	<a href="#">Evaluation</a>
                      <ul class="sz-sub-menu">
                      	<li><a href="/exam/admin/searchAssignmentToEvaluateForm">Evaluate Assignments</a></li>
                      	<li><a href="/exam/admin/searchProjectToEvaluateForm">Evaluate Projects</a></li>
                      	<li><a href="/exam/admin/searchAssignedCaseStudyFilesForm">Evaluate Case Study</a></li>
                      	<li><a href="/exam/admin/viewSubmittedSOP">View Project SOP</a></li>
                      	<li><a href="/exam/admin/viewSubmittedSynopsis">View Project Synopsis</a></li>
                      </ul>
				</li>
           
             <%} %>
			<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#">TEE</a>
                      <ul class="sz-sub-menu">
                      		<li><a href="/exam/admin/addStudentMarksForm">Single Entry</a></li>
							<li><a href="/exam/admin/uploadWrittenMarksForm">Mass Upload Offline Exam Marks</a></li>
						 	<li><a href="/exam/admin/uploadOnlineExamMarksForm">Mass Upload Online Exam Marks</a></li>
							<li><a href="/exam/admin/uploadOnlineWrittenRevalMarksForm">Mass Upload Online Exam Reval Marks</a></li>
							<li><a href="/exam/admin/evaluationSheetForm">Evaluation Sheet</a></li>
 							<li><a href="/exam/admin/insertABRecordsForm">Online Exam Absent Students List</a></li> 
							<li><a href="/exam/insertExecutiveABRecordsForm">Executive Exam Absent Students List</a></li>
							<li><a href="/exam/admin/markCheckSheetForm">TEE Marks Check Sheet</a></li>
							<li><a href="/exam/admin/examResultProcessingChecklist">Result Processing CheckList</a></li>
							<!-- <li><a href="/exam/examResultsProcessingChecklistTCS">Result Processing CheckList New</a></li> -->
							<li><a href="/exam/admin/executiveResultProcessingChecklist">Executive Result Processing CheckList</a></li>
 							<li><a href="/exam/admin/passFailTriggerSearchForm">Pass Fail Trigger</a></li>
							<li><a href="/exam/admin/getGraceEligibleForm">Grace</a></li>
							<li><a href="/exam/admin/makeResultsLiveForm">Make Results Live</a></li>
							<li><a href="/studentportal/admin/demoExamICLC">Demo Exams</a></li>
							<li><a href="/exam/admin/uploadTEEMarksForm">Upload MBA-WX TEE Marks</a></li>
							<li><a href="/exam/uploadTEEMarksForm">Upload MBA-WX TEE Marks</a></li>
							<!-- <li><a href="/exam/embaExamResultProcessingChecklist">EMBA Result Processing CheckList</a></li> -->
							<!-- <li><a href="/exam/readSifyDataForm">Pull Sify Results</a></li>
							<li><a href="/exam/transferSifyResultsToOnlineMarks">Transfer Sify Results to Marks Table</a></li>
							<li><a href="/exam/admin/insertABRecordsForm">Online Exam Absent Students List</a></li>
							<li><a href="/exam/admin/uploadWrittenMarksForm">Upload Student Marks for UMC </a></li>
	       					<li><a href="/exam/viewSifyMarks">View Sify Results</a></li> -->
					   </ul>
					</li>
               <%} %>
         
         
        	 <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#">MBA-WX TEE</a>
                      <ul class="sz-sub-menu">
                     		 <li><a href="/exam/admin/embaExamResultProcessingChecklist">EMBA Result Processing CheckList</a></li>
							<!-- <li><a href="/exam/uploadTEEMarksForm">Upload MBA-WX TEE Marks</a></li>
							<li><a href="/exam/examAssessmentPanelForm">Add Assessment</a></li> -->
							<li><a href="/exam/admin/embaPassFailReportForm">EMBA PassFail Report</a></li> 
							<li><a href="/exam/admin/dissertationQ7ExamResultCheckList">Dissertation Q7 Result Processing</a></li>
							<li><a href="/exam/admin/dissertationQ8ExamResultCheckList">Dissertation Q8 Result Processing</a></li>
							<li><a href="/exam/admin/mbaWxExamEmailLinkForm" target="_blank">MBA-WX Exam Email Link Dashboard</a></li>
					   </ul>
					</li>
					<li><a href="/exam/admin/mettlStatusDashboardForm">MBA-WX Exam Status Dashboard</a></li>
               <%} %>

               <%if(roles.indexOf("TEE Admin") != -1){ %>
           		<li class="has-sub-menu">
                  	<a href="#">MBA X TEE</a>
                      <ul class="sz-sub-menu">
                      	 <li><a href="/exam/admin/validateTestDetailsForm">Verify Test Data for UPGRAD</a></li>
                      </ul>
				</li>
               <%} %>  

               
                <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#">MBA-X TEE</a>
                      <ul class="sz-sub-menu">
                     		 <li><a href="/exam/admin/upgradExamResultsProcessingChecklist">MBAX Result Processing CheckList</a></li>
							<!-- <li><a href="/exam/uploadTEEMarksForm">Upload MBA-WX TEE Marks</a></li>
							<li><a href="/exam/examAssessmentPanelForm">Add Assessment</a></li> -->
							<!-- <li><a href="/exam/embaPassFailReportForm">EMBA PassFail Report</a></li>  -->
					   </ul>
					</li>
               <%} %>


         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Exam Results"><i class="fa-solid fa-trophy  fa-lg"></i></a>
                      <ul class="sz-sub-menu">
                      	<li><a href="/exam/admin/passFailTriggerSearchForm">Pass Fail Trigger</a></li>
						<li><a href="/exam/admin/getGraceEligibleForm">Grace</a></li>
						<li><a href="/exam/admin/makeResultsLiveForm">Make Results Live</a></li>
						<li><a href="/exam/marksheetForm">Marksheet</a></li>
						<li><a href="/exam/admin/singleStudentMarksheetForm">Single Marksheet</a></li>
						<li><a href="/exam/marksheetFromSRForm">Marksheet from SR</a></li>
						<li><a href="/exam/admin/generateNonGradedMarksheetFromSRForm">Non-Graded Marksheet from SR</a></li>
						<li><a href="/exam/marksheetFromSRFormForMBAWX">Marksheet from SR For MBA-WX</a></li>
						<li><a href="/exam/certificateFromSRForm">Certificate From SR</a></li>
						<li><a href="/exam/mbawxCertificateFromSRForm">MBAWX Certificate From SR</a></li>  
						<li><a href="/exam/admin/singleStudentCertificateForm">Single Certificate</a></li>
						<li><a href="/exam/executiveCertificatesForBatchForm">Executive Bulk Certificates </a></li>
						<li><a href="/exam/downloadExecutiveMarksheetForm">Executive Bulk Marksheet</a></li>
						<li><a href="/exam/admin/transcriptForm">Transcript</a></li>
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
                  	<a href="#" title="Review Question File"><i class="fa-solid fa-circle-question fa-lg" ></i></a>
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
                    	<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
                    	<li><a href="/exam/centerUserMappingForm" class="">Upload Center User Mapping</a></li>
                    	  <%} %>
	                    <li><a href="/exam/examExecutiveRegistrationChecklist" class="">Executive Exam Registration Checklist</a></li>
	                      <li><a href="/exam/examRegularRegistrationChecklist">Regular Exam Reg Checklist</a></li>
	                      	<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
	                    <li><a href="/exam/addExamCenterForm" class="">Add Exam Center</a></li>
	                     <li><a href="/exam/excelUploadForExamCentersForm" class="">Upload Mass Exam Center</a></li>
	                     <%} %>
	                 <!--   <li><a href="/exam/addExecutiveExamCenterFormMassUpload" class="">Upload Executive Mass Exam Center</a></li> -->
	                   <!--  <li><a href="/exam//exam/addExecutiveExamCenterForm" class="">Add Executive Exam Center</a></li> -->
	                	<li><a href="/exam/uploadExamFeeExemptForm" class="">Upload No Exam Fee Students</a></li>
	                	<li><a href="/exam/uploadExamFeeExemptSubjectsForm" class="">Upload No Exam Fee Students-Subjects</a></li>
						<li><a href="/exam/searchDDsToApproveForm">Approve DD</a></li>
						<li><a href="/exam/queryTransactionStatusForm">Query Transaction</a></li>
						<li><a href="/exam/admin/searchBookingsToReleaseForm">Release Seats</a></li>
						<li><a href="/exam/admin/uploadSeatRealeseFileForm">Mass Release Seats</a></li>
						<li><a href="/exam/searchExamBookingTOChangeCenterForm">Change Center</a></li>
						<li><a href="/exam/queryFeesPaidForm">Refund</a></li>
						<li><a href="/exam/admin/findOnlineTranConflictForm">Transaction Mismatch</a></li>
						<li><a href="/exam/admin/examBookingDashboardForm">Booking Dashboard</a></li>
						<li><a href="/exam/admin/attendanceSheetForm">Attendance Sheet</a></li>
						<li><a href="/exam/admin/downloadHallTicketForm">Hall Ticket</a></li>
						<li><a href="/exam/downloadExecutiveHallTicketForm">Executive Hall Ticket</a></li>
						<!-- <li><a href="/exam/admin/onlineExamPasswordReportForm">Online Exam Passwords</a></li> -->
						<li><a href="/exam/insertExamBookingDataFormTCS">Upload Exam Booking Data To TCS</a></li>	
						<li><a href="/exam/examBookingRefundRequestReport">Exam Booking Refund Request Report</a></li>
						<li><a href="/exam/admin/demoExamDashBoard">Demo Exam DashBoard</a></li>		
						<li><a href="/exam/updateExamBookingDataFormTCS">Reschedule Exam Booking</a></li>				
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
               } %>
               
          	  <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 <li class="has-sub-menu">
                   <a href="#">Exam Reg MBA-WX</a>
                      <ul class="sz-sub-menu">
<!--       					<li><a href="/exam/uploadMBAWXCentersForm">Upload Centers</a></li> -->
<!--       					<li><a href="/exam/addMbaWxTimeTableForm">Upload TimeTable</a></li> -->
							<li><a href="/exam/queryTransactionStatusFormMBAWX">Query Transaction</a></li>
         			  </ul>
      			 </li>
                <%} %>
                <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 <li class="has-sub-menu">
                   <a href="#">Exam Reg MBA-X</a>
                      <ul class="sz-sub-menu">
                      	<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){ %>
	                      <li><a href="/exam/admin/uploadCentersForm_MBAX">Upload Centers</a></li>
	                        <%} %>
	                      <li><a href="/exam/addTimeTableForm_MBAX">Upload TimeTable</a></li>
	                      <li><a href="/exam/admin/queryTransactionStatusForm_MBAX">Query Transaction</a></li>
	                      <!-- <li><a href="/exam/refundPaymentFormMBAX">Query Refund Transaction</a></li> -->
	                      <li><a href="/exam/admin/liveSettingMBAX">Live Settings</a></li>
	                      <li><a href="/exam/examBookingsdownloadMBAX">Download Exam Bookings</a></li>  
	                      <%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){%>
	                      <li><a href="/exam/examCenterCapacityReportFormMBAX">Exam Center Available Capacity</a></li>  
	                      <%} %> 
         			  </ul>
      			 </li>
                <%} %>
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1
            		|| roles.indexOf("Read Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>   
               <li class="has-sub-menu">
                  	<a href="#" title="Search"><i class="fa-solid fa-magnifying-glass" ></i></a>
	                    <ul class="sz-sub-menu">
							<li><a href="/exam/admin/searchStudentMarksForm">Search Marks</a></li>
							<li><a href="/exam/admin/searchRegisteredStudentMarksForm">Search Exam Registered Student Marks</a></li>
							<li><a href="/exam/admin/searchOnlineMarksForm" >Search Online Marks</a></li>
							<li><a href="/exam/admin/searchPassFailForm">Search Pass Fail</a></li>
							<li><a href="/exam/admin/searchStudentsForm">Search Students</a></li>
		              		<li><a href="/exam/admin/searchStudentsRegistrationsForm">Search Student Registrations</a></li>
		              		<li><a href="/exam/admin/searchAssignmentStatusForm">Search Assgn Status</a></li>
							<li><a href="/exam/admin/searchAssignmentFilesForm">Search Assgn Questions</a></li>
							<%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){%>
							<li><a href="/exam/searchExamCenterForm">Search Exam Centers </a></li>
							<li><a href="/exam/searchExecutiveExamCenter">Search Executive Exam Centers </a></li>
							<%} %>
							<li><a href="/exam/searchExamBookingConflictForm">Search Exam Booking Conflict </a></li>
							<li><a href="/exam/admin/searchStudentBatchMappingForm">Search Student Batch Mapping</a>   
	           				<li><a href="/exam/admin/displayLiveSessionReport">Search Recorded/Live Session Report</a></li>
	           				<li><a href="/acads/admin/dummyUsersForm">Search LoginAsForm Users</a>
						</ul>
					</li>
                <%}else if(roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
                
                <li class="has-sub-menu">
                  	<a href="#" title="Search"><i class="fa-solid fa-magnifying-glass" ></i></a>
                    <ul class="sz-sub-menu">
	             	<li><a href="/exam/admin/searchStudentMarksForm">Search Marks</a></li> 
	             	<li><a href="/exam/admin/searchPassFailForm">Search Pass Fail</a></li>
	             	<li><a href="/exam/admin/searchStudentsForm">Search Students</a></li>
	           		<li><a href="/exam/admin/searchStudentsRegistrationsForm">Search Student Registrations</a></li>
	           		<li><a href="/exam/admin/searchAssignmentStatusForm">Search Assgn Status</a></li>
	           		<li><a href="/exam/admin/displayLiveSessionReport">Search Recorded/Live Session Report</a></li>
	           		</ul>
				</li>
              <% } else if (roles.indexOf("Student Support") != -1){%>
              	<li class="has-sub-menu">
                  	<a href="#" title="Search"><i class="fa-solid fa-magnifying-glass" ></i></a>
                    <ul class="sz-sub-menu">
	           			<li><a href="/acads/admin/dummyUsersForm">Search LoginAsForm Users</a> 
	           		</ul>
				</li>
              <% }%>

		<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Acads Admin") != -1){ %>

		      <li class="has-sub-menu">
		        <a href="#" title="Enrollment"><i class="fa-solid fa-user-plus fa-lg"></i></a>
		        <ul class="sz-sub-menu">
		        <%if(!"academic.admin".equalsIgnoreCase((String)session.getAttribute("userId"))){%>
		         <li><a href="/exam/admin/uploadCentersForm">Upload Information Centers</a></li>
		         <%} %>
				<li><a href="/exam/admin/uploadStudentsMasterForm">Upload Student Master</a></li>
				<li><a href="/exam/admin/uploadRegistrationForm">Upload Registrations</a></li>
				<!-- <li><a href="/exam/admin/uploadStudentsImageForm">Upload Student Photo URLs</a></li> -->
                      </ul>
				</li>
               <%} %>  
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 
                 <li class="has-sub-menu">
                  	<a href="#" title="Exam Time Table"><i class="fa-solid fa-table fa-lg" ></i></a>
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
                  	<a href="#" title="Exam Time Table"><i class="fa-solid fa-table fa-lg"></i></a>
                      <ul class="sz-sub-menu">
							<li><a href="/exam/adminTimeTableForm">View Timetable</a></li>
                      </ul>
				</li>
              	<% }%>  
         
         <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1
         || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 || roles.indexOf("SR Admin") != -1 
         || roles.indexOf("Consultant") != -1 || roles.indexOf("Student Support") != -1){ %>
                <li class="has-sub-menu">
                  	<a href="#" title="Exam Reports"><i class="fa-solid fa-chart-column fa-lg" ></i></a>
                      <ul class="sz-sub-menu">
                       <%if( roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1
                    	         || roles.indexOf("Learning Center") != -1 || roles.indexOf("Information Center") != -1 || roles.indexOf("Corporate Center") != -1 
                    	         || roles.indexOf("SR Admin") != -1 || roles.indexOf("Consultant") != -1 ){ %>
                     	 <li><a href="/exam/admin/reportHome">Exam Reports</a></li>
                     	 <li><a href="/exam/downloadDemoExamReport">Download Demo Exam Report</a></li>
                     <%}else if(roles.indexOf("Student Support") != -1) { %>
                       	 <li><a href="/exam/admin/reportHome">Exam Reports</a></li>
					 <% } %>	  
                      </ul>
				</li>
               <%} %>  
            
           <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
                 <li class="has-sub-menu">
                  	<a href="#" title="Settings"><i class="fa-solid fa-gear fa-lg" aria-hidden="true"></i></a>
                      <ul class="sz-sub-menu">
                     	 <li><a href="/studentportal/loginAsForm">Exam:Log In As</a></li>
                     	 <li><a href="/exam/admin/makeResultsLiveForm">Make Live Settings</a></li>
                     	 <li><a href="/exam/admin/changeConfigurationForm">Set up dates</a></li>
                     	 <!-- <li><a href="/exam/makeExecutiveRegistrationLiveForm">Make Live Executive</a></li> -->
                      	 
                      </ul>
				</li>
               <%} %>  
               
                <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Read Admin") != -1){ %>
           		<li class="has-sub-menu">
                  	<a href="#" title="Internal Assessment"><i class="fa-solid fa-flask fa-lg" aria-hidden="true"></i></a>
                      <ul class="sz-sub-menu">
                     	<li><a href="/exam/makeTestLiveForm">Make Tests Live</a></li>
                    	<li><a href="/exam/addTestForm">Add Test</a></li>
                    	<li><a href="/exam/viewAllTests">Search Tests</a></li>
                      	<li><a href="/exam/consolidatedStudentsWiseReportsForm">Consolidated Students Wise Reports</a></li>
                      	<li><a href="/internal-assessment/searchIaToEvaluateForm">IA Evaluates Reports</a></li>
                      	<li><a href="/exam/admin/iaVerifyParametersReportForm">IA Verify Parameters Report</a></li>
                      	<li><a href="/exam/admin/studentTestAuditTrailAnalysisForm">Student Test Audit Trail</a></li>
                      	<li><a href="/exam/createNewTemplateForm">Manage Question Templates</a></li> 
                      	<li><a href="/exam/admin/unfairMeansDetials">Lost Focus Details</a></li>
                      </ul> 
				</li>
               <%} %>

				<%
					if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1
							|| roles.indexOf("Read Admin") != -1) {
				%>
					<li class="has-sub-menu"><a href="#"
						title="MBA-X Internal Assessment">MBA-X IA<i
							class="fa-solid fa-flask fa-lg" aria-hidden="true"></i></a>
						<ul class="sz-sub-menu">
							<li><a href="/exam/mbax/ia/a/makeTestLiveForm">Make Tests
									Live</a></li>
							<li><a href="/exam/mbax/ia/a/viewAllTests">Search Tests</a></li>
							<li><a href="/exam/mbax/ia/a/searchIaToEvaluateForm">IA
									Evaluates Reports</a></li>
							<li><a href="/exam/mbax/ia/a/studentTestAuditTrailAnalysisForm">Student
									Test Audit Trail</a></li>
							<li><a href="/exam/mbax/ia/a/unfairMeansDetials">Lost Focus
									Details</a></li>
						</ul>
						</li>
				<%
				}
				%>


<%
if (roles.indexOf("MBA-WX Admin") != -1 || roles.indexOf("Learning Center") != -1) {
%>
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
               
              <%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Student Support") != -1 || roles.indexOf("TEE Admin") != -1){ %>
              	<li class="has-sub-menu">
                	<a href="#">Block Center</a>
                    <ul class="sz-sub-menu">

		                  <li><a href="/exam/admin/centerNotAllowForm">Block Students Center</a></li>
		                  <li><a href="/exam/admin/searchBlockStudentsCenterForm">Search Blocked Students Center</a></li>

                    </ul>
				</li>
             <%} %>
             
               
               