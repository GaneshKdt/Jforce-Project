
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Lost Focus Details" name="title" />
</jsp:include>


<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">

<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.min.css">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>


<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.6/css/selectize.bootstrap3.min.css">


<style>
	input[type=datetime-local]::-webkit-inner-spin-button {
		-webkit-appearance: none;
		display: none;
	}
	
	.actionModal {
	  display: none; 
	  position: fixed; 
	  z-index: 3; 
	  padding-top: 150px; 
	  left: 0;
	  top: 0;
	  width: 100%; 
	  height: 100%; 
	  overflow: auto; 
	  background-color: rgb(0,0,0); 
	  background-color: rgba(0,0,0,0.4); 
	}
	
	.actionModal-content {
		font-family: "Open Sans";
		font-weight: 400;
		background-color: #fefefe;
		margin: auto;
		padding: 20px;
		border: 1px solid #888;
		max-height: calc(100vh - 250px);
		overflow: auto; 
		border-radius: 4px;
		font-size: 1.2em;
	}
	
	.actionModal-content b{
		font-weight: 700;
	}

	.loader {
	  border: 5px solid #B8B8B8;
	  border-radius: 50%;
	  border-top: 5px solid black;
	  width: 20px;
	  height: 20px;
	  -webkit-animation: spin 2s linear infinite; /* Safari */
	  animation: spin 1.5s linear infinite;
	}
	
	/* Safari */
	@-webkit-keyframes spin {
	  0% { -webkit-transform: rotate(0deg); }
	  100% { -webkit-transform: rotate(360deg); }
	}
	
	@keyframes spin {
	  0% { transform: rotate(0deg); }
	  100% { transform: rotate(360deg); }
	}
	
	input[type=date]::-webkit-inner-spin-button {
    -webkit-appearance: none;
    display: none;
	}
	
	.count {
		padding:5px;
	    border-radius: 20%;
	    background:  #d2232a;
	    color: white;
	    text-align: center;
	}
</style>


<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">


		<!-- Custom breadcrumbs as requirement is diff. Start -->
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/exam/">Exam</a></li>
					<li><a href="/exam/viewAllTests">Tests</a></li>
					<li><a href="/exam/viewTestDetails?id=${testId}">Test Details</a></li>
					<li><a href="#">Lost Focus Details</a></li>
				</ul>
				<ul class="sz-social-icons">
					<li><a href="https://www.facebook.com/NMIMSSCE"
						class="icon-facebook" target="_blank"></a></li>
					<li><a href="https://twitter.com/NMIMS_SCE"
						class="icon-twitter" target="_blank"></a></li>
					<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->

				</ul>
			</div>
		</div>
		<!-- Custom breadcrumbs as requirement is diff. End -->



		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">

				<%try{ %>

				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>

				<%}catch(Exception e){} %>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Lost Focus Details</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">

							<%@ include file="../adminCommon/messages.jsp"%>
							<!-- Code For Page Goes in Here Start -->

							<%try{ %>

							<div>

								<div class='row'>
									<div class="col-md-3 form-group" style="margin: 20px;">
										<h3 style="float: none; color: #d2232a;">Search by Test</h3>
										<label style="font-size: 1em; margin-top: 10px">Test:
										</label> <select id='test' class="form-control">
											<option value='0' disabled selected="selected">Select
												Test</option>
											<c:forEach var="test" items="${ testList }">
												<option value="${ test.testId }" subject='${ test.subject }'
													startDate="${ test.testStartDate }"
													batch="${ test.batchName }">${ test.testName }</option>
											</c:forEach>
										</select>
									</div>
								</div>

								<hr>

								<div>
									<h3 style="float: none; margin-left: 20px; color: #d2232a">Search
										by Subject-Date</h3>

									<div style="margin: 20px;">
										<div class="row">
											<div class="col-md-3 form-group">
												<label style="font-size: 1em;">Subject: </label> <select
													id='subject' class="form-control">
													<option value='0' disabled selected="selected">Select
														Subject</option>
													<c:forEach var="subjectList" items="${ subjectList }">
														<option value="${ subjectList.program_sem_subject_id }">${ subjectList.subject }</option>
													</c:forEach>
												</select>
											</div>

											<div class="col-md-3 form-group">
												<label style="font-size: 1em">Start Date</label> <input
													type="date" id="startDate" class="form-control">
											</div>

											<div class="col-md-3 form-group">
												<label style="font-size: 1em">End Date </label> <input
													type="date" id="endDate" class="form-control">
											</div>
										</div>

										<div class="row form-group">
											<div class="col-md-3 form-group">
												<button id='lostFocusDetailsForDuration'>Submit</button>
											</div>
										</div>
									</div>
								</div>

								<hr>

								<div id='recentLogs' style="display: none;">
									<div>
										<h3 style="float: none; margin-left: 20px; color: #d2232a">Recent
											Test Details</h3>
									</div>

									<div class="row form-group" style="float: none; margin: 10px;">
										<div class="col-md-3">
											<label style="font-size: 1em">Duration: </label> <select
												id='filterDuration' class="form-control">
												<option value='0'>Select Duration</option>
											</select>
										</div>
										<div class='col-md-3'>
											<label style="font-size: 1em">Total Record : </label> <br>
											<p id="totalCount" style="font-size: 1em"></p>
										</div>
										<div class='col-md-3'>
											<label style="font-size: 1em">Total Instances : </label>
											<p id="totalInstances" style="font-size: 1em"></p>
										</div>
									</div>
								</div>

								<div id='lostFocusDetails' style="margin:20px 0px 20px 0px;">
									<ul class="nav nav-tabs">
										<li class="active"><a data-toggle="tab" href="#attempted" style="font-size: 1em">To Be Marked <span class='count' id='attemptedCount'>0</span></a></li>
										<li><a data-toggle="tab" href="#copyCase" style="font-size: 1em">Marked for Copy Case <span class='count' id='copyCaseCount'>0</span></a></li>
									</ul>

									<div class="tab-content">
										<div id="attempted" class="tab-pane fade in active">
										</div>
										<div id="copyCase" class="tab-pane fade">
										</div>
									</div>
								</div>

								<div id="confirm" class="actionModal">
									<div class="actionModal-content" id="actionModel"
										style="max-width: 500px; text-align: center;">
										<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm
											Action</b> <br>
										<div id='modal-text'></div>
										<div id='modal-action'></div>
									</div>
								</div>
							</div>

							<%}catch(Exception e){} %>

						</div>

					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../adminCommon/footer.jsp" />


	<script src="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.jquery.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/selectize.js/0.12.6/js/standalone/selectize.min.js"></script>

	
	<script type="text/javascript">

	let studentDetails = [];
	let lostFocusDetails; 
	let selectSize = 0;
	let recentLostFocusData = [];
	let selectedStudentToMarkCopyCase = [];
	let selectedStudentToUnmarkCopyCase = [];
	let attemptDataTable='';
	let copyCaseDataTable='';
	
	$(document).ready (function(){

		$('#attempted').html('<center><div style="margin: auto; padding-top:50px"> Loading  <div class="loader"></div> </div></center>');
		$('#copyCase').html('<center><div style="margin: auto; padding-top:50px"> Loading  <div class="loader"></div> </div></center>');
		
		$.ajax({
			type : "GET",
			contentType : "application/json",
			url : "/exam/m/getRecentTest",
			success : function(data) {

				if(data.length == 0){
					attemptedDivContainer = "<div style='margin-top:20px; text-align:center'>No recent tests were found.</div>";
					copyCaseDivContainer = "<div style='margin-top:20px; text-align:center'>No recent tests were found.</div>";

					$('#attemptedCount').html('0');
					$('#copyCaseCount').html('0');
					$('#attempted').html(attemptedDivContainer);
					$('#copyCase').html(copyCaseDivContainer);
				}else{

					getRecentLostFocusDetails(data)
				}
				
			},
			error : function(e) {
				console.log("ERROR: ", e);
				alert("Please Refresh The Page.")
				
			}
		});


		$('#lostFocusDetailsForDuration').click(function() {

			$('#recentLogs').hide();
			$('#attempted').html('<center><div style="margin: auto; padding-top:50px"> Loading  <div class="loader"></div> </div></center>');
			$('#copyCase').html('<center><div style="margin: auto; padding-top:50px"> Loading  <div class="loader"></div> </div></center>');
			let startDate = $('#startDate').val();
			let endDate = $('#endDate').val();
			let program_sem_subject_id = $('#subject').val();
			
			console.debug('pss: '+program_sem_subject_id)
			console.debug('startDate: '+startDate)
			console.debug('endDate: '+endDate)
			
			if( program_sem_subject_id == null || typeof program_sem_subject_id == "undefined" || program_sem_subject_id == "" ){
				
				$('#confirm').show();
				$('#modal-text').html('<p><br>Please select a subject to search.</p>')
				$('#modal-action').html("<button type='button' id='close' class='btn btn-primary'  onclick='closeModal()' style='margin: 10px;'>Close</button>")
				
			}else if ( startDate == null || typeof startDate == "undefined" || startDate == ""){
			
				$('#confirm').show();
				$('#modal-text').html('<p><br>Please select start date for the search.</p>')
				$('#modal-action').html("<button type='button' id='close' class='btn btn-primary'  onclick='closeModal()' style='margin: 10px;'>Close</button>")
				
			}else if ( endDate == null || typeof endDate == "undefined" || endDate == "" ){
			
				$('#confirm').show();
				$('#modal-text').html('<p><br>Please select end date for the search.</p>')
				$('#modal-action').html("<button type='button' id='close' class='btn btn-primary'  onclick='closeModal()' style='margin: 10px;'>Close</button>")
				
			}
			
			let body = {
					'program_sem_subject_id':program_sem_subject_id,
					'testStartDate':startDate,
					'testEndDate':endDate
					}
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/m/getTestForSubjectAndDuration",
				data : JSON.stringify(body),
				success : function(data) {

					console.debug('inSuccessgetTestForSubjectAndDuration')
					console.debug('data: '+JSON.stringify(data))
					getRecentLostFocusDetails(data)
					
				},
				error : function(e) {
					console.log("ERROR: ", e);
					alert("Please Refresh The Page.")
					
				}
			});
		});

		
		$('#test').on('change',function(){

			$('#recentLogs').hide();
			$("#startDate").val("");
			$("#endDate").val("");
			$('#attempted').html('<center><div style="margin: auto; padding-top:50px"> Loading  <div class="loader"></div> </div></center>');
			$('#copyCase').html('<center><div style="margin: auto; padding-top:50px"> Loading  <div class="loader"></div> </div></center>');
			let details = [];
			let testName = $('#test option:selected').text();
			let subject = $('#test option:selected').attr('subject');
			let testStartDate = $('#test option:selected').attr('startDate');
			let batchName = $('#test option:selected').attr('batch');
			let testId = $('#test').val();
			selectedStudentToMarkCopyCase = [];
			selectedStudentToUnarkCopyCase = [];
			let body = {
					'testId':testId
					};

			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "https://ngasce-content.nmims.edu/ltidemo/api/getTestLostFocusDetails", 
				data : JSON.stringify(body),
				success : function(data) {

					for(var j in data){
						details.push({
							'count':data[j].count,
							'createdDate':data[j].createdDate,
							'emailId':data[j].emailId,
							'ipAddress':data[j].ipAddress,
							'lastModifiedDate':data[j].lastModifiedDate,
							'sapid':data[j].sapid,
							'testId':data[j].testId,
							'totalTimeAwayInSecs':data[j].totalTimeAwayInSecs,
							'testName':testName,
							'testStartDate':testStartDate,
							'subject':subject,
							'batchName':batchName
						});
					}

					getStudentDetails(details)
					
				},
				error : function(e) {
					console.log("ERROR: ", e);
					alert("Please Refresh The Page.")
					
				}
			});

		});


		$('#filterDuration').on('change',function(){

			let duration = $('#filterDuration').val();
			let subject = $('#subject').val();
			let lostFocusDivContainer;
			let studentList = [];
			let totalInstances = 0;
			selectedStudentToMarkCopyCase = [];
			selectedStudentToUnmarkCopyCase = [];
			let attemptedCount = 0;
			let copyCaseCount = 0;
			
			if(lostFocusDetails && lostFocusDetails.length != 0){

				for(let i=0;i < lostFocusDetails.length;i++){

					console.debug('check: '+duration < (lostFocusDetails[i].timeAway/60))
					if(duration < (lostFocusDetails[i].timeAway/60)){

						studentList.push({
							'testId':lostFocusDetails[i].testId,
							'timeAway':lostFocusDetails[i].timeAway,
							'count':lostFocusDetails[i].count,
							'sapid':lostFocusDetails[i].sapid,
							'ipAddress':lostFocusDetails[i].ipAddress,
							'sapid':lostFocusDetails[i].sapid,
							'name' : lostFocusDetails[i].name,
							'testName' :lostFocusDetails[i].testName,
							'testStartDate':lostFocusDetails[i].testStartDate,
							'subject':lostFocusDetails[i].subject,
							'batchName':lostFocusDetails[i].batchName,
							'attemptStatus':lostFocusDetails[i].attemptStatus,
							'reason':lostFocusDetails[i].reason
						});

						populateTable(studentList);
						
					}else if(duration == 0){

						studentList.push({
							'testId':lostFocusDetails[i].testId,
							'timeAway':lostFocusDetails[i].timeAway,
							'count':lostFocusDetails[i].count,
							'sapid':lostFocusDetails[i].sapid,
							'ipAddress':lostFocusDetails[i].ipAddress,
							'sapid':lostFocusDetails[i].sapid,
							'name' : lostFocusDetails[i].name,
							'testName' :lostFocusDetails[i].testName,
							'testStartDate':lostFocusDetails[i].testStartDate,
							'subject':lostFocusDetails[i].subject,
							'batchName':lostFocusDetails[i].batchName,
							'attemptStatus':lostFocusDetails[i].attemptStatus,
							'reason':lostFocusDetails[i].reason
						});

						populateTable(studentList);

					}
				}

			}else{
					
				attemptedDivContainer = "<div style='margin-top:20px; text-align:center'>No students attempted to switch tabs.</div>";
				copyCaseDivContainer = "<div style='margin-top:20px; text-align:center'>No students attempted to switch tabs.</div>";
			}

		});

	});

	
	function getRecentLostFocusDetails(recentTestDetails){

		let testName;
		let testStartDate;
		let subject;
		let batchName;

		if(!recentTestDetails.length == 0){

			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "https://ngasce-content.nmims.edu/ltidemo/api/getRecentTestLostFocusDetails",
				data : JSON.stringify(recentTestDetails),
				success : function(data) {

					console.debug('data: '+data)
					recentLostFocusData = [];
					
					for (var i in data){
						let list = data[i];
						for(var j in list){

							for ( var i in recentTestDetails){
								if(recentTestDetails[i].testId == list[j].testId){
									testName = recentTestDetails[i].testName;
									testStartDate = recentTestDetails[i].testStartDate;
									subject = recentTestDetails[i].subject;
									batchName = recentTestDetails[i].batchName;
								}
							}
							
							recentLostFocusData.push({
								'count':list[j].count,
								'createdDate':list[j].createdDate,
								'emailId':list[j].emailId,
								'ipAddress':list[j].ipAddress,
								'lastModifiedDate':list[j].lastModifiedDate,
								'sapid':list[j].sapid,
								'testId':list[j].testId,
								'totalTimeAwayInSecs':list[j].totalTimeAwayInSecs,
								'testName':testName,
								'testStartDate':testStartDate,
								'subject':subject,
								'batchName':batchName
							});
						}
					}

					getStudentDetails(recentLostFocusData);
					
				},
				error : function(e) {
					console.debug("ERROR: ", e);
					alert("Please Refresh The Page.")
					
				}
			});
			 
		}else{

			attemptedDivContainer = "<div style='margin-top:20px; text-align:center'>No students attempted to switch tabs.</div>";
			copyCaseDivContainer = "<div style='margin-top:20px; text-align:center'>No students attempted to switch tabs.</div>";

			$('#attemptedCount').html('0');
			$('#copyCaseCount').html('0');
			$('#attempted').html(attemptedDivContainer);
			$('#copyCase').html(copyCaseDivContainer);
			
		}
	}

	function getStudentDetails(lostFocusData){

		let body = [];
		let details = [];
		
		for(let i=0;i < lostFocusData.length;i++){
			body.push({
				'sapid':lostFocusData[i].sapid,
				'testId':lostFocusData[i].testId
			});
		}

		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "/exam/m/getStudentLostFocusDetails",
			data : JSON.stringify(body),
			success : function(data) {

				for(let i=0;i < lostFocusData.length;i++){
					details.push({
						'testId':lostFocusData[i].testId,
 						'timeAway':lostFocusData[i].totalTimeAwayInSecs,
						'count':lostFocusData[i].count,
						'sapid':lostFocusData[i].sapid,
						'ipAddress':lostFocusData[i].ipAddress,
						'sapid':lostFocusData[i].sapid,
						'name' : data[i].firstName + " " + data[i].lastName,
						'testName' :lostFocusData[i].testName,
						'testStartDate':lostFocusData[i].testStartDate,
						'subject':lostFocusData[i].subject,
						'batchName':lostFocusData[i].batchName,
						'attemptStatus':data[i].attemptStatus,
						'reason':data[i].reason
					});
				}

				lostFocusDetails = details;
				
				console.debug('detailsTesting: '+details)
				populateTable(details);

			},
			error : function(e) {
				$('#attempted').html('An error occured, please try again.');
				$('#copyCaseCount').html('An error occured, please try again.');
				alert("Please Refresh The Page.")
				
			}
		});
	}

	function populateTable(details){

		let selectSizeList = [];
		let totalInstances = 0;
		let attemptedCount = 0;
		let copyCaseCount = 0;
		let	attemptedDiv = "<div class='table-responsive' style='overflow: hidden; padding:10px;'> <table class='table table-striped table-hover' id='attemptedTable' style='padding:10px;'> ";
		attemptedDiv = attemptedDiv + "<thead> <tr> <th>Test Name</th> <th>Batch</th> <th>Subject</th> <th>Start Date</th> <th>Instances</th> <th>SAPID</th> <th>Name</th> <th>Duration (in min)</th> ";
		attemptedDiv = attemptedDiv + "<th>Reason</th><th>IP Address</th> <th style=text-align:center;vertical-align: middle;>Select <input type='checkbox' class='form-check-input' id='selectall_mark' onchange='selectAllStudents(`mark`,this.id)'></th> <th>Attempt Status</th> </tr></thead> <tbody>";
		let	copyCaseDiv = "<div class='table-responsive' style='overflow: hidden; padding:10px;'> <table class='table table-striped table-hover' id='copyCaseTable' style='padding:10px;'> ";
		copyCaseDiv = copyCaseDiv + "<thead> <tr> <th>Test Name</th> <th>Batch</th> <th>Subject</th> <th>Start Date</th> <th>Instances</th> <th>SAPID</th> <th>Name</th> <th>Duration (in min)</th> ";
		copyCaseDiv = copyCaseDiv + "<th>Reason</th><th>IP Address</th> <th style=text-align:center;vertical-align: middle;>Select <input type='checkbox' class='form-check-input' id='selectall_unmark' onchange='selectAllStudents(`unmark`,this.id)'></th> <th>Attempt Status</th> </tr></thead> <tbody>";
		
		try{
			if(details && details.length != 0){

				for(let i=0;i < details.length;i++){

					if(details[i].attemptStatus == 'Attempted'){

						attemptedDiv = attemptedDiv + "<tr>";
						attemptedDiv = attemptedDiv + "<td style='word-wrap: break-word; max-width: 150px;'>"+details[i].testName+"</td>";
						attemptedDiv = attemptedDiv + "<td>"+details[i].batchName+"</td>";
						attemptedDiv = attemptedDiv + "<td>"+details[i].subject+"</td>";
						attemptedDiv = attemptedDiv + "<td>"+details[i].testStartDate+"</td>";
						attemptedDiv = attemptedDiv + "<td>"+details[i].count+"</td>";
						attemptedDiv = attemptedDiv + "<td>"+details[i].sapid+"</td>";
						attemptedDiv = attemptedDiv + "<td>"+details[i].name +"</td>";
						attemptedDiv = attemptedDiv + "<td>"+moment({}).seconds(details[i].timeAway).format("mm:ss")+ "</td>";
						if( typeof details[i].reason === 'undefined' ){
							if( details[i].timeAway < 5 )
								attemptedDiv = attemptedDiv + "<td> Lost focus duration less than 5 sec </td>";
							else
								attemptedDiv = attemptedDiv + "<td> Reason Not Provided </td>";
						}else{
							attemptedDiv = attemptedDiv + "<td>"+details[i].reason+"</td>";
						}
						attemptedDiv = attemptedDiv + "<td>"+details[i].ipAddress+"</td>";
						attemptedDiv = attemptedDiv + "<td style=text-align:center><input type='checkbox' class='form-check-input' id=entry_"+i+" value='"+JSON.stringify(details[i])+"' onclick='storeDetails(this.id, `mark`)'></td>";
						attemptedDiv = attemptedDiv + "<td>"+details[i].attemptStatus+"</td>";
						attemptedDiv = attemptedDiv + "</tr>";
						attemptedCount++;

						selectSizeList.push(Math.round(details[i].timeAway/60));
						
					}else{

						copyCaseDiv = copyCaseDiv + "<tr>";
						copyCaseDiv = copyCaseDiv + "<td style='word-wrap: break-word; max-width: 150px;'>"+details[i].testName+"</td>";
						copyCaseDiv = copyCaseDiv + "<td>"+details[i].batchName+"</td>";
						copyCaseDiv = copyCaseDiv + "<td>"+details[i].subject+"</td>";
						copyCaseDiv = copyCaseDiv + "<td>"+details[i].testStartDate+"</td>";
						copyCaseDiv = copyCaseDiv + "<td>"+details[i].count+"</td>";
						copyCaseDiv = copyCaseDiv + "<td>"+details[i].sapid+"</td>";
						copyCaseDiv = copyCaseDiv + "<td>"+details[i].name +"</td>";
						copyCaseDiv = copyCaseDiv + "<td>"+moment({}).seconds(details[i].timeAway).format("mm:ss") + "</td>";
						if( typeof details[i].reason === 'undefined' ){
							if( details[i].timeAway < 5 )
								copyCaseDiv = copyCaseDiv + "<td> Lost focus duration less than 5 sec </td>";
							else
								copyCaseDiv = copyCaseDiv + "<td> Reason Not Provided </td>";
						}else{
							copyCaseDiv = copyCaseDiv + "<td>"+details[i].reason+"</td>";
						}
						copyCaseDiv = copyCaseDiv + "<td>"+details[i].ipAddress+"</td>";
						copyCaseDiv = copyCaseDiv + "<td style=text-align:center><input type='checkbox' class='form-check-input' id=entry_"+i+" value='"+JSON.stringify(details[i])+"' onclick='storeDetails(this.id, `unmark`)'></td>";
						copyCaseDiv = copyCaseDiv + "<td>"+details[i].attemptStatus+"</td>";
						copyCaseDiv = copyCaseDiv + "</tr>";
						copyCaseCount++;
						
						}
						
						selectSizeList.push(Math.round(details[i].timeAway/60));
						
					}

					attemptedDiv = attemptedDiv + "</tbody></table> <button type='button' id='sendEmail' class='btn btn-primary' style='margin:20px 10px 10px 0px;' onclick='sendEmail("+JSON.stringify(details)+")'>Send Email</button>";
					attemptedDiv = attemptedDiv + "<a id='downalodExcel' class='btn btn-primary' style='margin:20px 10px 10px 0px;' onclick='downloadExcel("+JSON.stringify(details)+")'>Download Excel </a>";
					attemptedDiv = attemptedDiv + "<button type=button id='markCopyCase' class='btn btn-primary' style='margin:20px 10px 10px 0px;' onclick='alertUser(`mark`)'>Mark Copy Case</button>";
					attemptedDivContainer = "<div style='margin:20px'>"+attemptedDiv+"</div>";

					copyCaseDiv = copyCaseDiv + "</tbody></table> <button type=button id='unmarkCopyCase' class='btn btn-primary' style='margin:20px 10px 10px 0px;' onclick='alertUser(`unmark`)'>Unmark Copy Case</button></div>" ;
					copyCaseDivContainer = "<div style='margin:20px'>"+copyCaseDiv+"</div>";
					
					selectSize = Math.max.apply(null, selectSizeList); 

				}else{

					attemptedDivContainer = "<div style='margin-top:20px; text-align:center'>No students attempted to switch tabs.</div>";
					copyCaseDivContainer = "<div style='margin-top:20px; text-align:center'>No students attempted to switch tabs.</div>";
					
				}

			let selectOption = { 0: "Select Duration", 0.30: "0.30 minutes" };

			$('#filterDuration').empty();
			
			$.each(selectOption, function(key, value) {   
			     $('#filterDuration')
			         .append($("<option></option>")
			                    .attr("value", key)
			                    .text(value)); 
			});

			for(let i = 1; i<=selectSize; i++){
				
				$('#filterDuration').append($('<option>', {
				    value: i,
				    text: i+' minutes'
				}));
			}

			totalInstances = attemptedCount + copyCaseCount;
			$('#totalCount').html(details.length+' entries');
			$('#totalInstances').html(totalInstances+' instances');
			$('#attemptedCount').html(attemptedCount);
			$('#copyCaseCount').html(copyCaseCount);
			$('#attempted').html(attemptedDivContainer);
			$('#copyCase').html(copyCaseDivContainer);
			
			attemptDataTable = $('#attemptedTable').DataTable({
		        initComplete: function () {
		            this.api().columns().every( function () {
		                var column = this;
		                var headerText = $(column.header()).text();

		                if(headerText == "Subject"){
		                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
		                    .appendTo( $(column.header()) )
		                    .on( 'change', function () {
		                        var val = $.fn.dataTable.util.escapeRegex(
		                            $(this).val()
		                        );
		 
		                        column
		                            .search( val ? '^'+val+'$' : '', true, false )
		                            .draw();
		                    });
		 
		                	column.data().unique().sort().each( function ( d, j ) {
		                    select.append( '<option value="'+d+'">'+d+'</option>' )
		                	});
		              	}
		              	
		            });
		        }
		    });

			copyCaseDataTable = $('#copyCaseTable').DataTable({
		        initComplete: function () {
		            this.api().columns().every( function () {
		                var column = this;
		                var headerText = $(column.header()).text();

		                if(headerText == "Subject")
		                {
		                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
		                    .appendTo( $(column.header()) )
		                    .on( 'change', function () {
		                        var val = $.fn.dataTable.util.escapeRegex(
		                            $(this).val()
		                        );
		 
		                        column
		                            .search( val ? '^'+val+'$' : '', true, false )
		                            .draw();
		                    } );
		 
		                column.data().unique().sort().each( function ( d, j ) {
		                    select.append( '<option value="'+d+'">'+d+'</option>' )
		                } );
		              }
		            } );
		        }
		    });
		    
			if(details.length > 0){
				$('#recentLogs').show();
			}

			if(selectedStudentToMarkCopyCase.length == 0){
				 $('#markCopyCase').prop('disabled', true);
			}
			if(selectedStudentToUnmarkCopyCase.length == 0){
				 $('#unmarkCopyCase').prop('disabled', true);
			}
			
		}catch(err){
			console.debug("Got error, Refresh Page! ");
			console.debug(err);
		}
		
	}

	function sendEmail(list){
		
		let action = confirm("Are you sure you want to send unfair means mail to the following students?");
		if(action){
			let body = list;
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/m/sendEmailForUnfairMeans",   
				data : JSON.stringify(body),
				success : function(data) {
					if(data.success){
						alert(data.successMessage)
					}else{
						alert("An error occured while sending mails.")
					}
					
				},
				error : function(e) {
					console.log("ERROR: ", e);
					alert("Please Refresh The Page.")
					
				}
			});
		}else{
			console.log("in else")
		}
		
	}
	
	function downloadExcel(data){

		let body = data;
		
		console.debug('inDownloadExcel got data: '+JSON.stringify(body))
			
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "/exam/admin/getDataForLostFocusReport",   
			data : JSON.stringify(body),
			success : function(data) {
				window.location.href = "/exam/Exam_Lost_Focus_Report";
			},
			error : function(e) {
				console.log("ERROR: ", e);
				alert("Please Refresh The Page.")
				
			}
		});
	}

	function storeDetails(id, status){

		let details = JSON.parse($('#'+id).val());
		
		
		if(status == 'mark'){

			if($('#' + id).is(":checked")){ 
				if(!selectedStudentToMarkCopyCase.includes(details)){
					selectedStudentToMarkCopyCase.push(details);
				}
			}else{
				
				for(let i=0;i < selectedStudentToMarkCopyCase.length;i++){

					if(selectedStudentToMarkCopyCase[i].sapid == details.sapid){
						const index = selectedStudentToMarkCopyCase.indexOf(selectedStudentToMarkCopyCase[i]);
						if (index > -1) {
							selectedStudentToMarkCopyCase.splice(index, 1);
						}
					}
					
				}
			}
				
		}else if (status == 'unmark'){

			if($('#' + id).is(":checked")){ 
				if(!selectedStudentToUnmarkCopyCase.includes(details)){
					selectedStudentToUnmarkCopyCase.push(details);
				}
			}else{

				for(let i=0;i < selectedStudentToUnmarkCopyCase.length;i++){

					if(selectedStudentToUnmarkCopyCase[i].sapid == details.sapid){
						const index = selectedStudentToUnmarkCopyCase.indexOf(selectedStudentToUnmarkCopyCase[i]);
						if (index > -1) {
							selectedStudentToUnmarkCopyCase.splice(index, 1);
						}
					}
					
				}

			}
				
		}	
		
		if(selectedStudentToMarkCopyCase.length > 0){
			$('#markCopyCase').prop('disabled', false);
		}else{
			$('#markCopyCase').prop('disabled', true);
		}

		if(selectedStudentToUnmarkCopyCase.length > 0){
			$('#unmarkCopyCase').prop('disabled', false);
		}else{
			$('#unmarkCopyCase').prop('disabled', true);
		}

	}

	function selectAllStudents(status, id){

		let attemptedTable =  $('#attemptedTable');
		let copyCaseTable = $('#copyCaseTable');
		let checkboxId = '';
		selectedStudentToMarkCopyCase = [];
		
		if(status == 'mark'){

			var allPages = attemptDataTable.rows().nodes();
			
			if($('#selectall_mark').is(":checked")){

				$('input:checkbox', copyCaseTable).not(this).prop('checked', false);
				$('input:checkbox', allPages).prop('checked', true);

				attemptDataTable.rows().every( function ( rowIdx, tableLoop, rowLoop ) {
				    var data = this.data();
				    for(let i=0;i < lostFocusDetails.length;i++){
						if(data[5] == lostFocusDetails[i].sapid){
							if(!selectedStudentToMarkCopyCase.includes(lostFocusDetails[i])){
								selectedStudentToMarkCopyCase.push(lostFocusDetails[i]);
							}
						}
					}
				});
				
			}else{

				$('input:checkbox', allPages).prop('checked', false);
				attemptDataTable.rows().every( function ( rowIdx, tableLoop, rowLoop ) {
				    var data = this.data();

				    for(let i=0;i < lostFocusDetails.length;i++){
				    	if(data[5] == lostFocusDetails[i].sapid){
					    	if(selectedStudentToMarkCopyCase.includes(lostFocusDetails[i])){
								const index = selectedStudentToMarkCopyCase.indexOf(lostFocusDetails[i]);
								if (index > -1) {
									selectedStudentToMarkCopyCase.splice(index, 1);
								}
							}
				    	}
					}
				});
				
		 	}
		}else if(status == 'unmark'){

			var allPages = copyCaseDataTable.rows().nodes();
			
			if($('#selectall_unmark').is(":checked")){

				$('input:checkbox', attemptedTable).not(this).prop('checked', false);
				$('input:checkbox', allPages).prop('checked', true);

				copyCaseDataTable.rows().every( function ( rowIdx, tableLoop, rowLoop ) {
				    var data = this.data();
				    for(let i=0;i < lostFocusDetails.length;i++){
						if(data[5] == lostFocusDetails[i].sapid){
							if(!selectedStudentToUnmarkCopyCase.includes(lostFocusDetails[i])){
								selectedStudentToUnmarkCopyCase.push(lostFocusDetails[i]);
							}
						}
					}
				});

			}else{

				$('input:checkbox', allPages).prop('checked', false);
				
				copyCaseDataTable.rows().every( function ( rowIdx, tableLoop, rowLoop ) {
				    var data = this.data();

				    for(let i in lostFocusDetails){
				    	if(data[5] == lostFocusDetails[i].sapid){
					    	if(selectedStudentToUnmarkCopyCase.includes(lostFocusDetails[i])){
								const index = selectedStudentToUnmarkCopyCase.indexOf(lostFocusDetails[i]);
								if (index > -1) {
									selectedStudentToUnmarkCopyCase.splice(index, 1);
								}
							}
				    	}
					}
				});
		
		 	}
		 	
		}

		if(selectedStudentToMarkCopyCase.length > 0){
			$('#markCopyCase').prop('disabled', false);
		}else{
			$('#markCopyCase').prop('disabled', true);
		}

		if(selectedStudentToUnmarkCopyCase.length > 0){
			$('#unmarkCopyCase').prop('disabled', false);
		}else{
			$('#unmarkCopyCase').prop('disabled', true);
		}

	}
	
	function closeModal(task){

		$('#confirm').hide();
		if(task == 'reloadPage'){
			location.reload();
		}
		
	}
	
	function markCopyCase(){
        
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "/exam/m/markCopyCase",   
			data : JSON.stringify(selectedStudentToMarkCopyCase),
			success : function(data) {
				
				if(data.success){
					$('#modal-text').html(' <b>The selected students have been marked for copy case.</b> ');
					$('#modal-action').html("<button type='button' id='close' class='btn btn-primary'  onclick='closeModal(`reloadPage`)' style='margin: 10px;'>Close</button>")
				}
				
			},
			error : function(e) {
				console.log("ERROR: ", e);
				alert("Please Refresh The Page.")
				
			}
		});

	}
	
	function unmarkCopyCase(){
		
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "/exam/m/unmarkCopyCase",   
			data : JSON.stringify(selectedStudentToUnmarkCopyCase),
			success : function(data) {
				if(data.success){
					$('#modal-text').html(' <b>The selected students have been unmarked for copy case.</b> ');
					$('#modal-action').html("<button type='button' id='close' class='btn btn-primary'  onclick='closeModal(`reloadPage`)' style='margin: 10px;'>Close</button>")
					
				}
			},
			error : function(e) {
				console.log("ERROR: ", e);
				alert("Please Refresh The Page.")
				
			}
		});

	}
	
	
	function alertUser(action){

		let duration = $('#filterDuration').val();
		let functionToCall;
		let alertMessage = '';
		if(action == 'mark'){
			functionToCall = 'markCopyCase()';
			alertMessage = 'Are you sure you want to '+action+' copy case for the selected student ('+selectedStudentToMarkCopyCase.length+' students) for the duration of above '+duration+' minutes.';
		}else{
			functionToCall = 'unmarkCopyCase()';
			alertMessage = 'Are you sure you want to '+action+' copy case for the selected student ('+selectedStudentToUnmarkCopyCase.length+' students) for the duration of above '+duration+' minutes.';
		}
		
		$('#modal-text').html(alertMessage);
		$('#modal-action').html("<button type='button' class='btn btn-primary id='proceed'  onclick='"+functionToCall+"' style='margin: 10px;' >Proceed</button><button type='button' id='close' class='btn btn-primary' onclick='closeModal(`closeModal`)' style='margin: 10px;'>Close</button>")
		$('#confirm').show();
		
	}

	</script>
	
</body>
</html>