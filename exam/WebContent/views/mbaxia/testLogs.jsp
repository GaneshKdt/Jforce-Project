	
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="StudentTestAuditTrailAnalysisForm" name="title" />
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
	
	.actionModal-content::-webkit-scrollbar {
	  width: 10px;
	}
	
	/* Track */
	.actionModal-content::-webkit-scrollbar-track {
	  background: #f1f1f1; 
	  border-radius: 4px;
	}
	 
	/* Handle */
	.actionModal-content::-webkit-scrollbar-thumb {
	  background: #888; 
	  border-radius: 4px;
	}
	
	/* Handle on hover */
	.actionModal-content::-webkit-scrollbar-thumb:hover {
	  background: #555; 
	}
	
	
	.actionModal-content b{
		font-weight: 700;
	}

	.details{
		width: 100%; 
		margin:10px;
	}

	.logs{
		 max-height: 555px; 
		 overflow-y: scroll; 
		 width: 100%; 
		 margin: 40px auto 40px auto; 
		 background: white; 
		 border-radius:4px; 
		 box-shadow: 2px 4px 8px #CBCBCB
	}
	
	.logs::-webkit-scrollbar {
	  width: 10px;
	}
	
	/* Track */
	.logs::-webkit-scrollbar-track {
	  background: #f1f1f1; 
	  border-radius: 4px;
	}
	 
	/* Handle */
	.logs::-webkit-scrollbar-thumb {
	  background: #888; 
	  border-radius: 4px;
	}
	
	/* Handle on hover */
	.logs::-webkit-scrollbar-thumb:hover {
	  background: #555; 
	}

	.expand{
		padding:5px;
		pointer-events: auto;
		color:#00D1FF;
	}
	
	.expand:hover {
		cursor: pointer;
	}
	
	.table-responsive::-webkit-scrollbar {
		width: 4px;
	}
	
	/* Track */
	.table-responsive::-webkit-scrollbar-track {
	  background: #f1f1f1; 
	  border-radius: 10px;
	}
	 
	/* Handle */
	.table-responsive::-webkit-scrollbar-thumb {
	  background: #888; 
	  border-radius: 10px;
	}
	
	/* Handle on hover */
	.table-responsive::-webkit-scrollbar-thumb:hover {
	  background: #555; 
	}
	
	.showMore{
		margin: 20px;
		padding: 10px;
	}
	
	.showMore:hover{
		cursor: pointer;
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
	
	.tabs{
		color: black;
	}
	
	.snackbar{
	    background: #5b5a5a;
	    width: 30%;
	    padding: 20px;
	    margin: 25px;
	    text-align: center;
	    border-radius: 4px;
   	 	position: fixed;
    	z-index: 2;
	    left: 50%;
	    transform: translate(-50%, 0);
	    display: none;
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
					<li><a href="#">MAB-X Internal Assessment Audit Trails</a></li>

				</ul>
				<ul class="sz-social-icons">
					<li><a href="https://www.facebook.com/NMIMSSCE"
						class="icon-facebook" target="_blank"></a></li>
					<li><a href="https://twitter.com/NMIMS_SCE"
						class="icon-twitter" target="_blank"></a></li>
					
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

						<h2 class="red text-capitalize">MAB-X Internal Assessment Audit Trails</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">

							<%@ include file="../adminCommon/messages.jsp"%>
							<!-- Code For Page Goes in Here Start -->

							<%try{ %>
							
							<div id='snackbar' class='snackbar'>
								<p id='snackbarDetails' style="color: white;"></p>
							</div>	
							
							<div class="well" style="">
								<div class="form-group">
									<label for="studentName">Search By Students Name:</label>
									 <select class="form-control" id="studentName">
									    <option value="">Select Student</option>
									    
										<c:forEach var="studentForSelect" items="${studentsListForSelect}"
											varStatus="status">
									    	<option value="${studentForSelect.sapid}">${studentForSelect.firstName} ${studentForSelect.lastName} - ${studentForSelect.sapid}</option>
									    </c:forEach>
									    
									  </select>
								</div>
							</div>
							
							<div class="well" hidden="">
								--- OR ---
							</div>
							
							
							
							<div class="well" hidden="">
								<div class="form-group">
									<label for="sapId">Enter Sapid:</label> 
									<input type="text" class="form-control" id="sapId">
								</div>
								<div class="form-group">
									<button id="searchTestsBySapidButton" type="submit" class="btn btn-default">Go</button>
								</div>
							</div>
							
					<!-- ____________________________________________ test select div ____________________________________________ -->
					
							<div class="well" id='testSelectBox' style="display:none;">
								<div id="testsSelectBoxContainer" class="form-group"></div>
								<div id="testJoinLinkContainer" style="display:none;">
									<input type='hidden' id='shareLinkField'>
								</div>
							</div>
							
					<!-- _________________________________________________ Loader _________________________________________________ -->	
						
							<div id='loaderMain' class="well" hidden="">
								<div style="margin: 20px auto 20px auto; text-align: center;"> 
									<div>Loading</div>
									<div style='left: 49%; position: relative;' class="loader"></div> 
								</div>
							</div>
							
					<!-- ____________________ edit test details div for students who havent started the test ____________________ -->
					
							<div class="well" id='editTestDetailsBox' style="display:none;">
							
								<div class='container logs' style='overflow: hidden;'> 
									<h3 style='padding:10px; float: none'>Test Details</h3>
									<div id="editTestDetailsBoxContainer" class="form-group"></div>
								</div>
								
							</div>
							
					<!-- ____________________________________ student test details and logs ____________________________________ -->
							
							<div class="well" id='studentTestDetialsBox' style="padding: 10px; display:none;">
							
								<div class='logs'> 
									<h3 style='padding:20px 10px 0px 20px; float: none'>Student Test Details</h3>
									<div id="studentTestDetials"></div>
								</div>

							</div>
							
					<!-- _________________________________________________ logs  _________________________________________________-->
			
								<div class="well" id='logDetails' style="padding: 10px; display: none;">
								
									<div class="container logs" style="padding: 10px;">
										<h3 style='padding:10px 10px 20px 10px; float: none'>Answers & Logs</h3>
										<div style="padding: 10px;">
											<ul class="nav nav-tabs" id='tabs'>
											    <li id='recentLogTab' class="active"><a class='tabs' data-toggle="tab" href="#recentLogContainer" style="font-weight: 1em">Recent Logs</a></li>
											    <li id='testLogTab'><a class='tabs' data-toggle="tab" href="#flaggedLogsContainer" style="font-weight: 1em">Test Logs</a></li>
											    <li id='studentAnswerTab'><a class='tabs' data-toggle="tab" href="#answerContainer" style="font-weight: 1em">Student Answers</a></li>
											    <li id='detailedLogsTab'><a class='tabs' data-toggle="tab" href="#testLogContainer" style="font-weight: 1em">Detailed Logs</a></li>
											    <li id='logFileTab'><a class='tabs' data-toggle="tab" href="#logFileDetailsContainer" style="font-weight: 1em">Log File Details</a></li>
											</ul>
													
											<div class="tab-content">
												<div class='tab-pane fade in active'id="recentLogContainer" > 
													<div id="recentLog"></div>
												</div>
												
												<div class='tab-pane fade' id='flaggedLogsContainer'>
													<div id="flaggedLogs"></div>
												</div>
												
												<div class='tab-pane fade' id='answerContainer'> 
													<div id="answerDetials"></div>
												</div>
															
												<div class='tab-pane fade' id='testLogContainer'>
													<label for='typeFilter' style='font-size: 15px; margin: 10px;'>Select Type</label><br>
													<select class='form-control' id='typeFilter' onclick='typeChange()' style='margin-left: 10px;'>
														<option value='all'>Select All</option>
													</select>
													<div id="testLogDetails"></div>
												</div>
												
												<div class='tab-pane fade' style='overflow: auto;' id="logFileDetailsContainer">
													<p style="margin-top: 20px; font-size: 15px;"><strong><i class="fa-solid fa-triangle-exclamation" aria-hidden="true"></i>
														Warning: Not To Be Used While IA's are going on</strong></p>
													<button class='btn btn-primary' onclick='populateLogFileDetails()' id='showLogButton'>Show Log Details</button>
													<div id="logFileDetails" style='margin: 10px;' class="form-group"></div>
												</div>
											</div>
										</div>
									</div>		
									
								</div>

							<%}catch(Exception e){} %>

							<!-- Code For Page Goes in Here End -->

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

	let testLogDetails ;
	let test;
	let logsLoaded = false;
	let supportProvided = false;
	
	$(document).ready (function(){
	
		//Search student by name start
		$('#studentName').selectize({
	    	create: false,
	    	sortField: 'text'
		});
	
		$('#studentName').on('change', function(){
			$('#sapId').val($('#studentName').val());
		});
		//Search student by name end
	
		
		$("#studentName").on('change', function(e){
			e.preventDefault();

			$('#editTestDetailsBox').hide();
			$('#studentTestDetialsBox').hide();
			$('#logDetails').hide();
			$('#testSelectBox').show();
			$('#testsSelectBoxContainer').html('<center><div style="margin: auto; padding:50px"> Loading  <div class="loader"></div> </div></center>');		
			let sapId = $("#sapId").val();

			//added to copy text to clipboard
			
			var $temp = $("<input>");
			$("body").append($temp);
			$temp.val(sapId).select();
		
		  	try {
		    	var successful = document.execCommand("copy");
		    	var msg = successful ? 'successful' : 'unsuccessful';
		    	if( msg == 'successful')
		    		$('#snackbar').fadeIn(10);
  			} catch (err) {
    			console.log(err);
 			}

		    $temp.remove();
		    $('#snackbarDetails').html('Copied sapid to clipboard')
    		$('#snackbar').fadeOut(1000);
			
			if(sapId){
				//api call start
					
				let selectBoxHtml = "<div class='form-group'><label for='tests'>Select Test:</label> <select id='tests' class='form-control' onchange='handleTestChange()'><option value=''>loading...</option></select></div>";
		
		
				let options = "<option>Loading... </option>";
				$('#tests').html(options);
				$('#testsSelectBoxContainer').html(selectBoxHtml);
				
				var data = {
						sapid:sapId
					}
	
				$.ajax({
					
					type : "POST",
					contentType : "application/json",
					url : "/exam/mbax/ia/student/m/getTestsBySapid",   
					data : JSON.stringify(data),
					success : function(data) {
						
						let testsData = data.tests;
						options = "";
						let allOption = "";
						
						for(let i=0;i < testsData.length;i++){
							
							allOption = allOption + ""+ testsData[i].id +",";
							if(testsData[i].testDetailsId){
								options = options + "<option value='" + testsData[i].id + "' date='"+testsData[i].startDate+"' duration="+testsData[i].duration+" > " + 
								testsData[i].testName + "-" + testsData[i].startDate + " (Attempted) </option>";
							}else{
								options = options + "<option value='" + testsData[i].id + "' date='"+testsData[i].startDate+"' duration="+testsData[i].duration+" > " + 
								testsData[i].testName + "-" + testsData[i].startDate + " (Not Attempted) </option>";
							}
						}
						allOption = allOption.substring(0,allOption.length-1);
						
						$('#tests').html(
								 "<option value=''>Select Test</option>" + options
						);
						
						try{
			
							$("#tests").chosen({
								width: '100%'
							});
							
						}catch(err){
							console.log("Got error, Refresh Page! ");
							console.log(err);
						}
						
					},
					error : function(e) {
						
						alert("Please Refresh The Page.")
						console.log("ERROR: ", e);
						
					}
				});

				//api call end
			}else{
				$('#testSelectBox').hide();
				console.log('Enter Sapid To Search For Test');
			}
		});
		
	});
	//Test select changed end 


		
	function handleTestChange(){

		$('#editTestDetailsBox').hide();
		$('#studentTestDetialsBox').hide();
		$('#logDetails').hide();
		$('#loaderMain').show();
		$('#logFileDetails').empty();
		$('#showLogButton').show();
		
		let testDetails;
		let testId = $('#tests').val();
		let sapId = $("#sapId").val();
		supportProvided = false;
		logsLoaded = false;
			
		if(testId == ''){
			alert("Select a test!");
		}
		
		var details = {
			sapid: sapId,
			testId: testId
		}
	
			//api call for student test Details
		
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "/exam/mbax/ia/student/m/getStudentTestDetails",   
			data : JSON.stringify(details),
			success : function(data) {
				
				testDetails = data.studentTestDetails;	
				test = data.test;

				$('#shareLinkField').val(test.testJoinURL);
				
				try{

					if(testDetails.id != null){
						
						$('#editTestDetailsBox').hide(); $('#studentTestDetialsBox').show(); $('#loaderMain').hide();
						$('#studentTestDetials').html('<center><div style="margin: auto; padding:50px"> Loading  <div class="loader"></div> </div></center>');
						populateStudentTestDetails(data);
	
					}else{
						
						
						$('#editTestDetailsBox').show(); $('#studentTestDetialsBox').hide(); $('#loaderMain').hide();
						populateEditStudentTestDetails( test );
								
					}
	
				}catch(err){
					console.log("Got error, Refresh Page! ");
					console.log(err);
				}	
				
			},
			error : function(e) {
				console.log("ERROR: ", e);
				alert("Please Refresh The Page.")
			}
		});

	}


	</script>

	<div id="extendTestDuration" class="actionModal">
		<div class="actionModal-content" id="actionModal" style="max-width: 500px; text-align: center;">
			<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>
			<br> <b>Are you sure you want to edit the start time for the
				student? </b>
			<div style='width: 100%; margin: auto;'>
				<button type='button' class='btn btn-primary ' style='margin: 10px;' onclick="hideShow('none', 'editDateTime', 'extendTestDuration')">Proceed</button>
				<button type='button' class='btn btn-primary' onclick="hideShow('none', 'noAction', 'extendTestDuration')" style='margin: 10px;'>Close</button>
			</div>
		</div>
	</div>
	
	<div id="editRefreshCount" class="actionModal">
		<div class="actionModal-content" id="actionModal" style="max-width: 500px; text-align: center;">
			<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>
			<br> <b>Are you sure you want to edit the refresh count for the student? </b>
			<div style='width: 100%; margin: auto;'>
				<button type='button' class='btn btn-primary ' style='margin: 10px;' onclick="hideShow('none', 'editRefreshCount', 'editRefreshCount')">Proceed</button>
				<button type='button' class='btn btn-primary' onclick="hideShow('none', 'noAction','editRefreshCount')" style='margin: 10px;'>Close</button>
			</div>
		</div>
	</div>

	<div id="extendTestWindow" class="actionModal">
		<div class="actionModal-content" id="actionModal" style="max-width: 500px; text-align: center;">
			<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>
			<br> <b>Are you sure you want to edit the start time for the
				student? </b>
			<div style='width: 100%; margin: auto;'>
				<button type='button' class='btn btn-primary ' style='margin: 10px;' onclick="hideShow('none', 'extendTestWindow', 'extendTestWindow')">Proceed</button>
				<button type='button' class='btn btn-primary' onclick="hideShow('none', 'noAction', 'extendTestWindow')" style='margin: 10px;'>Close</button>
			</div>
		</div>
	</div>
	
	<div id="showResponse" class="actionModal">
		<div class="actionModal-content" id="responseModal" style="max-width: 500px; text-align: center;">
		</div>
	</div>
	
	<div id="showContent" class="actionModal">
		<div class="actionModal-content" id="actionModel" style="max-width: 1000px; text-align: center;">
			<div id="content" style="text-align: justify;"></div>
			<div style='width: 100%; margin: auto;'>
				<button type='button' class='btn btn-primary' onclick="hideShow('none', 'noAction','showContent')" style='margin: 10px;'>Close</button>
			</div>
		</div>
	</div>
	
	<script>

		let typeList = [];
		let isFiltered = false;
		let testEndedOn;
		let testStartedOn;
		
		function hideShow(action, toDo, name){
			
			var Modal = document.getElementById(name);
			try{
				Modal.style.display = action;
			}catch(error){ }

			if(toDo === "editDateTime"){
				
				document.getElementById('startDateEdit').style.display = "block";
				document.getElementById('startDateEdit').focus();
				document.getElementById('extendStartTimeButton').style.display = "none";
				document.getElementById('saveButton').style.display = "inline-block";
				document.getElementById('cancelButton').style.display = "inline-block";
				$('#editRefreshCountButton').hide();
				$('#showAnswerAndLogButton').hide();
				$('.testJoinLink').hide();
				$('#otherIssuesButton').hide();
				
			}else if(toDo === "editRefreshCount"){

				$('#refreshCountEdit').show();
				$('#cancelButton').show();
				$('#cancelButton').show();
				$('#extendStartTimeButton').hide();
				$('#startDateEdit').hide();
				$('#saveButton').hide();
				$('#editRefreshCountButton').hide();
				$('#extendStartTimeButton').hide();
				$('#showAnswerAndLogButton').hide();
				$('.testJoinLink').hide();
				$('#otherIssuesButton').hide();
				
			}else if(toDo =="saveOtherIssues"){

				$('#otherIssuesDiv').show();
				$('#extendStartTimeButton').hide();
				$('#editRefreshCountButton').hide();
				$('#showAnswerAndLogButton').hide();
				$('#getTestJoinLink').hide();
				$('#otherIssuesButton').hide();
				
			}else if(toDo === "extendTestWindow"){

				$('#extendTestWindowDiv').show();
				$('#showLogsButton').hide();
				$('#extendTestWindowButton').hide();
				$('.testJoinLink').hide();

			}
			
		} 
		
		function cancleRequest(){
			
			$('#otherIssuesDiv').hide(); $('#startDateEdit').hide(); $('#refreshCountEdit').hide(); $('#saveButton').hide(); 
			$('#cancelButton').hide(); $('#testStartDateEdit').hide(); $('#extendTestWindowDiv').hide(); 
			$('#editRefreshCountButton').show(); $('#extendStartTimeButton').show();
			$('#testStartDateValue').show(); $('#extendTestWindowButton').show();
			$('.testJoinLink').show(); $('#showLogsButton').show(); $('#showAnswerAndLogButton').show();
			$('#otherIssuesButton').show();
			
		}

		function getLogDetailsForAttemptedStudents(){

			logsLoaded = true;
			$('#detailedLogsTab').show(); $('#logFileTab').show(); $('#studentAnswerTab').show(); 
			$("#tabs li").removeClass("active");$(".tab-content div").removeClass("active in");
			$('#answerDetials').empty(),$('#testLogDetails').empty(),$('#flaggedLogs').empty(),$('#logFileDetails').empty();
			$('#recentLogTab').hide(); $('#testLogTab').show(); $('#logDetails').show();
			$('#testLogTab').addClass('active'); $('#flaggedLogsContainer').addClass('in active');
			$('#flaggedLogs').html('<center><div style="margin: auto; padding:50px"> Loading  <div class="loader"></div> </div></center>');
			$('#answerDetials').html('<center><div style="margin: auto; padding:50px"> Loading  <div class="loader"></div> </div></center>');	
			$('#testLogDetails').html('<center><div style="margin: auto; padding:50px"> Loading  <div class="loader"></div> </div></center>');
			$('#logFileDetailsBox').html('<center><div style="margin: auto; padding:50px"> Loading  <div class="loader"></div> </div></center>');

			let testId = $('#tests').val();
			let sapId = $("#sapId").val();
			let date = $('#tests option:selected').attr('date');
			let duration = $('#tests option:selected').attr('duration');

			console.debug('date: '+date+' duration: '+duration)
			
			var details = {
					sapid: sapId,
					testId: testId,
					startDate : date,
					duration: duration
				}
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/mbax/ia/student/m/getLogDetailsForAttemptedStudents",   
				data : JSON.stringify(details),
				success : function(data) {
					
					testLogDetails = data.testLogs;	
					answersData = data.answers;
					
					try{

						populateAnswerDetails(answersData);
						populateDetailedTestLogs(testLogDetails);
						populateTestLogs(testLogDetails);
						
					}catch(err){
						console.log("Got error, Refresh Page! ");
						console.log(err);
					}
						
				},
				error : function(e) {
			
					console.log("ERROR: ", e);
					alert("Please Refresh The Page.")
					
				}
			});
			
		}

		function getLogDetailsForNotAttemptedStudents(){

			logsLoaded = true;
			$("#tabs li").removeClass("active");$(".tab-content div").removeClass("active in");
			$('#recentLog').html('<center><div style="margin: auto; padding:50px"> Loading  <div class="loader"></div> </div></center>');
			$('#logDetails').show();$('#recentLogTab').addClass('active');$('#recentLogContainer').addClass('in active');
			$('#recentLogTab').show(); $('#testLogTab').hide(); $('#studentAnswerTab').hide(); $('#detailedLogsTab').hide();
			
			let testId = $('#tests').val();
			let sapId = $("#sapId").val();
			let date = $('#tests option:selected').attr('date');
			let duration = $('#tests option:selected').attr('duration');

			console.debug('date: '+date+' duration: '+duration)
			
			var details = {
					sapid: sapId,
					testId: testId,
					startDate : date,
					duration: duration
				}
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/mbax/ia/student/m/getLogDetailsForNotAttemptedStudents",   
				data : JSON.stringify(details),
				success : function(data) {
						
					try{
							populateRecentLogs(data);
								
					}catch(err){
						console.log("Got error, Refresh Page! ");
						console.log(err);
					}
								
				},
				error : function(e) {
				
					console.log("ERROR: ", e);
					alert("Please Refresh The Page.")
					
				}
			});

		}
		
		function populateStudentTestDetails( data ){

			let testId = $('#tests').val();
			let sapid = $("#sapId").val();
			let testData = data.test;
			let studentTestDetailsData = data.studentTestDetails;
	
			let editTestDetailsHtmlContainer = "";
			let testDetailsHtmlContainer = "";
			let studentTestDetailsHtmlContainer = "";
			let answerDetailsHtmlContainer = "";
	
			//Show test Details to edit start time and refresh count

			if(studentTestDetailsData){

				let testDetailsHtmlDiv = "<div class='row'> <div class='col-md-6'><p>Test Name : <b>"+testData.testName+"("+testData.id+")</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Test Start Date : <b>"+testData.startDate+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Test End Date : <b>"+testData.endDate+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Maximum Questions to Show to Students : <b>"+testData.maxQuestnToShow+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Maximum Score : <b>"+testData.maxScore+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Duration : <b>"+testData.duration+"</b> </p>";
				if(testData.proctoringEnabled == 'Y')
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Proctoring : <b>On</b> </p>";
				else
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Proctoring : <b>Off</b> </p>"
					
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<p>Sapid : <b>"+studentTestDetailsData.sapid+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<p>Attempt Status : <b>"+studentTestDetailsData.attemptStatus+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<p>Test Started On : <b>"+studentTestDetailsData.testStartedOn+"</b> </p>";
				if(studentTestDetailsData.testStartedOn != null)
					testDetailsHtmlDiv = testDetailsHtmlDiv + "<p>Test Ended On : <b>"+studentTestDetailsData.testEndedOn+"</b> </p>";
				else
					testDetailsHtmlDiv = testDetailsHtmlDiv + "<p>Test Ended On : <b>NA</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "</div>";
				
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div class='col-md-6'>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Test Completed : <b>"+studentTestDetailsData.testCompleted+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Test Completed Status : <b>"+studentTestDetailsData.testEndedStatus+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Test Questions : <b>"+studentTestDetailsData.testQuestions+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Current Question : <b>"+studentTestDetailsData.currentQuestion+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Number Of Questions Attempted : <b>"+studentTestDetailsData.noOfQuestionsAttempted+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Numner Of Refresh : <b>"+studentTestDetailsData.countOfRefreshPage+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Score : <b>"+studentTestDetailsData.score+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Show Result : <b>"+studentTestDetailsData.showResult+"</b> </p> ";
				
				if(studentTestDetailsData.resultDeclaredOn != null)
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Result Declared On : <b>"+studentTestDetailsData.resultDeclaredOn+"</b> </p>";	
				else
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Result Declared On : <b> NA </b> </p>";	

				if(studentTestDetailsData.contactedSupport != null && studentTestDetailsData.contactedSupport !== "" )
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Contacted Support : <b>"+studentTestDetailsData.contactedSupport+"</b> </p> ";
				else
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Contacted Support : <b> N </b> </p>"; 

				if(studentTestDetailsData.reason != null && studentTestDetailsData.reason !== "" )
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Reason : <b>"+studentTestDetailsData.reason+"</b> </p>";
				else
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Reason : <b> NA </b> </p>"; 

				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Moved Answer From Cache To DB : <b>"+studentTestDetailsData.answersMovedFromCacheToDB+"</b> </p> </div>"; 

				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div class='col-md-12'>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div id='refreshCountEdit' style='display: none;' class='row' value='test'> <div class='col-md-6'><span> <b>Student's Refresh Count :</b> </span>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<select class='form-control ' required='' id='refreshCount'>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + '<option value="0" selected="selected">Select Refresh Count</option>';
				testDetailsHtmlDiv = testDetailsHtmlDiv + '<option value="-3">03</option>';
				testDetailsHtmlDiv = testDetailsHtmlDiv + '<option value="-5">05</option>';
				testDetailsHtmlDiv = testDetailsHtmlDiv + '<option value="-8">08</option>';
				testDetailsHtmlDiv = testDetailsHtmlDiv + '<option value="-10">10</option>';
				testDetailsHtmlDiv = testDetailsHtmlDiv + "</select>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div><lable for='refreshCountReason' style='font-weight: 700;'>Reason: </lable> ";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<input id='refreshCountReason' style='width: 300px' class='form-control reason' placeholder='Enter reason for increasing time'/></div>"+
				"<button type='submit' class='btn btn-primary' id='updateRefreshCount' onclick=updateRefreshCount("+sapid+','+testId+','+studentTestDetailsData.attempt+") > Save</button>"+
				"<button type='submit' class='btn btn-primary' id='cancelButton' onclick=cancleRequest() style='margin-left:10px;'> Cancel</button></div></div>";

				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div id='startDateEdit' style='display: none;' class='row' value='test'><div class='col-md-12'>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<lable for='duration' style='font-weight: 700;'>Extend Student's Start Time: </lable>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<select id='duration' class='form-control ' name='startDate'> ";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<option value='0' testDuration='"+testData.duration+"'>Select Duration</option> ";
				for (let index = 1; index <= testData.duration; index++){
					if( index%5 == 0 )
						testDetailsHtmlDiv = testDetailsHtmlDiv + "<option value='"+index+"' testDuration='"+testData.duration+"'>"+index+" Minutes</option> ";
				}
				testDetailsHtmlDiv = testDetailsHtmlDiv + "</select></div> ";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div class='col-md-12'><lable for='extendTestDurationReason' style='font-weight: 700;'>Reason: </lable> ";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<input id='extendTestDurationReason' style='width: 300px' class='form-control reason' placeholder='Enter reason for extending time'/></div>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div class='col-md-8' style='margin-top:25px'><button type='submit' class='btn btn-primary' id='saveButton' onclick=extendTestDuration("+sapid+','+testId+','+studentTestDetailsData.attempt+") > Save</button>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<button type='submit' class='btn btn-primary' id='cancelButton' onclick=cancleRequest() style='margin-left:10px;'> Cancel</button></div></div>";

				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div id='otherIssuesDiv' style='display: none;' class='row' value='test'><div class='col-md-12'>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<lable for='otherIssuesReason' style='font-weight: 700;'>Reason: </lable> ";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<input id='otherIssuesReason' style='width: 300px' class='form-control reason' placeholder='Enter reason for contacting support'/></div>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div class='col-md-8' style='margin-top:25px'><button type='submit' class='btn btn-primary' id='saveButton' onclick=saveOtherIssues("+sapid+','+testId+','+studentTestDetailsData.attempt+") > Save</button>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<button type='submit' class='btn btn-primary' id='cancelButton' onclick=cancleRequest() style='margin-left:10px;'> Cancel</button></div></div>";
				
				testDetailsHtmlDiv = testDetailsHtmlDiv + " <p><button type='submit' class='btn btn-primary' id='extendStartTimeButton' onclick='hideShow(`block`,`noAction`,`extendTestDuration`)'> Extend Start Time </button>"+
				" <button type='button' style='margin-left:10px;' class='btn btn-primary'  onclick='hideShow(`block`,`noAction`,`editRefreshCount`)' id='editRefreshCountButton'> Increase Refresh Count </button>"+
				" <button type='button' style='margin-left:10px;' class='btn btn-primary'  onclick='hideShow(`block`,`saveOtherIssues`,`saveOtherIssues`)' id='otherIssuesButton'> Other Issues </button>"+
				" <button type='button' style='margin-left:10px;' class='btn btn-primary'  onclick='getLogDetailsForAttemptedStudents()' id='showAnswerAndLogButton'> Show Answers and Logs </button>"+
				" <button type='button' style='margin-left:10px;' class='btn btn-primary testJoinLink'  onclick='copyTestJoinLink()' id='getTestJoinLink'> Get Test Join Link </button></p></div>";

				
				testDetailsHtmlContainer = "<div style='overflow: hidden; padding:20px; '> "+testDetailsHtmlDiv+"</div>";
			
			}else{
				testDetailsHtmlContainer = "<div style='overflow: hidden; padding:20px; '> <div>Student Did Not Attempt the test.</div> </div>";
				
			}
			
			$( '#loaderMain' ).hide();
			$( '#studentTestDetials' ).html(testDetailsHtmlContainer);
	
		}

		function populateAnswerDetails( data ){

			if(data){
				
				let	answerDetailsHtmlDiv = "<div class='table-responsive'> <table class='table table-striped table-hover'>   <thead> <tr>";
				answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<th style='min-width: 300px; word-wrap: break-word;'>Question</th> <th>Type</th> <th>Answer</th> <th>CreatedAt</th> ";
				answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<th>LastModifiedAt</th> <th>Is Checked</th> <th>Marks</th> <th>Remark</th> </tr></thead>";
				answerDetailsHtmlDiv = answerDetailsHtmlDiv + " <tbody>";

				for(let i=0;i < data.length;i++){
					
					answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<tr>";
					answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>( "+ data[i].questionId +" ) "+extractContent(data[i].question)+"</td>";
					answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+ data[i].typeString +"</td>";
					if(data[i].type == 4 || data[i].type == 8)
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+data[i].answer+"</td>";
					else
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+data[i].optionData+"</td>";
					answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+data[i].createdDate+"</td>";
					answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+data[i].lastModifiedDate+"</td>";
					if(data[i].isChecked)
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>Yes</td>";
					else
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>No</td>";
					answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+data[i].marks+"</td>";
					
					if(typeof data[i].remark == "undefined")
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>NA</td>";
					else
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+data[i].remark+"</td>";
					answerDetailsHtmlDiv = answerDetailsHtmlDiv + "</tr>";
				}
				
				answerDetailsHtmlDiv = answerDetailsHtmlDiv + "</tbody></table> </div>";
				answerDetailsHtmlContainer = "<div style='padding:10px; '>"+answerDetailsHtmlDiv+"</div>";
				
			}else{
				answerDetailsHtmlContainer = "<div style='padding:10px; '> Student Did Not Attempt any Question for the test.</div> </div>";		
			}

			$( '#answerDetials' ).html(answerDetailsHtmlContainer);
		}
		
		function populateEditStudentTestDetails( data ){

			let testId = $('#tests').val();
			let sapid = $("#sapId").val();
			let testData = data;
			
			let testDetailsHtmlDiv = "";
	
			if(testData){

				testDetailsHtmlDiv = "<div class='details'> <p>Sapid : <b>"+sapid+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<p>Test Name : <b>"+testData.testName+"("+testId+")</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<p id='testStartDateValue'>StartDate : <b>"+testData.startDate+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div id='extendTestWindowDiv' style='display: none;' class='row'>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<div class='col-md-4'> <span> <b>Extend Start Time</b> </span> <br>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<label for='testWindowExtendStartTime'> Extended Test Window Start Time</label>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<input id='testWindowExtendStartTime' type='datetime-local' class='form-control ' name='startDate'> ";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<label for='testWindowExtendEndTime'> Extended Test Window End Time </label>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<input id='testWindowExtendEndTime' type='datetime-local' class='form-control ' name='endDate'> ";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<label for='testWindowExtendReason'> Reason </label>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<input id='testWindowExtendReason' type='text' class='form-control ' name='reason' placeholder='Enter reason for extending test window'> ";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<button type='submit' class='btn btn-primary' id='testTimeSaveButton' onclick=extendStudentsTestWindow("+sapid+','+testId+") > Save</button>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<button type='submit' class='btn btn-primary' id='testTimeCancelButton' onclick=cancleRequest() style='margin-left:10px;'> Cancel</button></div></div>";
				
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<p>EndDate : <b>"+testData.endDate+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<p>MaxQuestnToShowToStudents : <b>"+testData.maxQuestnToShow+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<p>MaxScore : <b>"+testData.maxScore+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<p>Duration : <b>"+testData.duration+"</b> </p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<p><button type='submit' class='btn btn-primary' id='extendTestWindowButton' onclick='hideShow(`block`,`noAction`,`extendTestWindow`)'> Extend Test Join Window </button>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<button type='submit' style='margin-left:10px;' class='btn btn-primary' id='showLogsButton' onclick='getLogDetailsForNotAttemptedStudents()'> Show Logs </button>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "<button type='button' style='margin-left:10px;' class='btn btn-primary testJoinLink'  onclick='copyTestJoinLink()' id='getTestJoinLink'> Get Test Join Link </button></p>";
				testDetailsHtmlDiv = testDetailsHtmlDiv + "</div>";

			}else{
				alert("RefreshPage! Unable to get test data for id: "+testId);
			}

			$('#loaderMain').hide();
			$('#editTestDetailsBoxContainer').html(testDetailsHtmlDiv);	

		}

		function copyTestJoinLink(){

			testJoinLink = $('#shareLinkField').val();

			var $temp = $("<input>");
			$("body").append($temp);
			$temp.val(testJoinLink).select();
		
		  	try {
		    	var successful = document.execCommand("copy");
		    	var msg = successful ? 'successful' : 'unsuccessful';
		    	if( msg == 'successful')
		    		$('#snackbar').fadeIn(10);
  			} catch (err) {
    			console.log(err);
 			}

		    $temp.remove();
		    $('#snackbar').html();
		    $('#snackbarDetails').html('Copied test join link to clipboard')
    		$('#snackbar').fadeOut(1000);
			
		}
		
		function populateRecentLogs( data ){

			let logDetials = data.testLogs;
			let logDetialsDiv = "";
			let test_duration = $('#tests option:selected').attr('duration');
			
			logDetialsDiv = "<div class='table-responsive details' style='margin:auto;'>";
			logDetialsDiv = logDetialsDiv + "<table class='table table-striped table-hover' id='testLogTable' style='margin:auto'> ";
			logDetialsDiv = logDetialsDiv + "<thead> <tr> <th>Visited Date</th> <th>Type</th> <th>Timespent</th> <th style='max-width: 180px; word-wrap: break-word;'>Page</th> <th>Status</th> <th>Details</th> ";
			logDetialsDiv = logDetialsDiv + "<th>Network Information</th></tr></thead> ";
			logDetialsDiv = logDetialsDiv + "<tbody>";

			logDetials.sort( function compare( a, b ) {
				if (a.visitedDate < b.visitedDate)    	return -1;
				else if(a.visitedDate > b.visitedDate)	return  1;
				else                      				return  0;
			});
			
			if( logDetials.length != 0){

				let alert;
				
				try {
		
					for(let i=0;i < logDetials.length;i++){


						if(logDetials[i].type == 'Error Analytics')
							alert = '#FCEEEE';
						else if(logDetials[i].type == 'Network Log')
							if(logDetials[i].status == "error")
								alert = '#FCEEEE';
							else
								alert="";
						else
							alert="";
						
						if(logDetials[i].type != 'Lost Focus'){
							
							logDetialsDiv = logDetialsDiv + "<tr style='background:"+alert+"'>";
							
							if(!Date.parse(logDetials[i].visitedDate)){
								
								if(test_duration < 120){
									logDetialsDiv = logDetialsDiv + "<td>"+ moment(parseInt(logDetials[i].visitedDate)).format("hh:mm:ss a")+"</td>";
								}else{
									logDetialsDiv = logDetialsDiv + "<td>"+ moment(parseInt(logDetials[i].visitedDate)).format("MMM Do YYYY, hh:mm:ss a")+"</td>";
								}
							}else{
								logDetialsDiv = logDetialsDiv + "<td>"+moment(logDetials[i].visitedDate).format("MMM Do YYYY, hh:mm:ss a")+"</td>";
							}
								
							logDetialsDiv = logDetialsDiv + "<td>"+logDetials[i].type+"</td>";
							
							if(!logDetials[i].timespent == "")
								logDetialsDiv = logDetialsDiv + "<td>"+moment({}).seconds(logDetials[i].timespent).format("H:mm:ss")+"</td>";
							else
								logDetialsDiv = logDetialsDiv + "<td>-</td>";
								
							logDetialsDiv = logDetialsDiv + "<td style='max-width: 180px; word-wrap: break-word;'>"+logDetials[i].page+"</td>";
							logDetialsDiv = logDetialsDiv + "<td>"+logDetials[i].status+"</td>";
	
							if(logDetials[i].details != "" && typeof logDetials[i].details != 'undefined' && logDetials[i].details != null )
								logDetialsDiv = logDetialsDiv + '<td>'+logDetials[i].details.substr(0, 30)+'...<span class="expand" onclick=expand(`details`,'+i+')>See More<span> '+
								' <div id="detailsContent_'+i+'" style="display:none;">'+logDetials[i].details+'</div></td>';
							else
								logDetialsDiv = logDetialsDiv + "<td>-</td>";
	
								
							logDetialsDiv = logDetialsDiv + "<td>"+logDetials[i].network+"</td>";
							logDetialsDiv = logDetialsDiv + "</tr>";
						}
					}

				} catch(err) {
					console.log(err);
					logDetialsDiv = logDetialsDiv + "<tr><td colspan='8'>Please refresh the page.</td></tr>"; 
				}
				
			}else{
				logDetialsDiv = logDetialsDiv + "<tr><td colspan='8' style='text-align: center;'>No records found</td></tr>";
			}
			
			logDetialsDiv = logDetialsDiv + "</tbody></table> </div>" ;
			$('#loaderMain').hide();
			$('#recentLogContainer').addClass('in active');
			$('#recentLog').html(logDetialsDiv);	
	
		}
	
		function populateDetailedTestLogs(testLogs){

			let test_duration = $('#tests option:selected').attr('duration');
			let	testLogsDiv = "<div class='table-responsive details'> <div>";
			testLogsDiv = testLogsDiv + "<table class='table table-striped table-hover' id='testLogTable' style='margin:auto'> ";
			testLogsDiv = testLogsDiv + "<thead> <tr> <th>Visited Date</th> <th>Type</th> <th>Timespent</th> <th style='max-width: 180px; word-wrap: break-word;'>Page</th> <th>Status</th> <th>Details</th> ";
			testLogsDiv = testLogsDiv + "<th>Network Information</th> <th>Error Message</th></tr></thead> ";
			testLogsDiv = testLogsDiv + "<tbody>";
	
			testLogs.sort( function compare( a, b ) {
				if (a.visitedDate < b.visitedDate)    	return -1;
				else if(a.visitedDate > b.visitedDate)	return  1;
				else                      				return  0;
			});

			if(testLogs.length != 0){
				
				try {
		
					let isLostFocus = '';
					let duration = '';
					let show = 'parent';
					let call ='';

					for(let i=0;i < testLogs.length;i++){
		
						let alert = '';
						let action = '';
						let expand = '';
						
						if(testLogs[i].type == 'Lost Focus'){
							expand = 'padding-right:5px; display:inline; font-size:12px'
							isLostFocus = true;
							duration = parseInt(testLogs[i].visitedDate) + (parseInt(testLogs[i].timespent)*1000);
							show = 'parent';
							call='showChild'
						}else{
							expand = 'display:none;';
						}
						
						if(isLostFocus && !(testLogs[i].type == 'Lost Focus')){
							if(parseInt(duration) > parseInt(testLogs[i].visitedDate)){
								action='collapse';
								show = 'child';
							}
							else {
								isLostFous = false;
								show = 'parent';
							}
						}

						if(testLogs[i].type == 'Error Analytics' || testLogs[i].type == 'Lost Focus')
							alert = '#FCEEEE';
						else if(testLogs[i].type == 'Network Log')
							if(testLogs[i].status == "error" || testLogs[i].status == "Error")
								alert = '#FCEEEE';
							else
								alert="";
						else
							alert="";
		
						if(!typeList.includes(testLogs[i].type))
							typeList.push(testLogs[i].type);
						
						testLogsDiv = testLogsDiv + "<tr class='"+show+"' id='row_"+i+"' onclick='"+call+"("+i+")' style='background:"+alert+"; visibility:"+action+"'>";
						
						if( !Date.parse(testLogs[i].visitedDate) ){

							if(test_duration < 120){
								testLogsDiv = testLogsDiv + "<td><i class='fa-solid fa-circle-plus' aria-hidden='true' style='"+expand+"'></i>"+
								moment(parseInt(testLogs[i].visitedDate)).format("hh:mm:ss a")+"</td>";
							}else{
								testLogsDiv = testLogsDiv + "<td><i class='fa-solid fa-circle-plus' aria-hidden='true' style='"+expand+"'></i>"+
								moment(parseInt(testLogs[i].visitedDate)).format("MMM Do YYYY, hh:mm:ss a")+"</td>";
							}
							
						}else{
							testLogsDiv = testLogsDiv + "<td><i class='fa-solid fa-circle-plus' aria-hidden='true' style='"+expand+"'></i>"+
							moment(testLogs[i].visitedDate).format("MMM Do YYYY, hh:mm:ss a")+"</td>";
						}
							
						testLogsDiv = testLogsDiv + "<td>"+testLogs[i].type+"</td>";
						
						if(!testLogs[i].timespent == ""){

							testLogsDiv = testLogsDiv + "<td> "+moment({}).seconds(testLogs[i].timespent).format("H:mm:ss")+"</td>";
		
						}else
							testLogsDiv = testLogsDiv + "<td>-</td>";
							
						testLogsDiv = testLogsDiv + "<td style='max-width: 180px; word-wrap: break-word;'>"+testLogs[i].page+"</td>";
						testLogsDiv = testLogsDiv + "<td>"+testLogs[i].status+"</td>";
						
						if(testLogs[i].type == 'Lost Focus'){
							testLogsDiv = testLogsDiv + "<td>"+testLogs[i].details+"</td>";
						}else if(testLogs[i].type == 'Network Log'){
							if( testLogs[i].details != "" && typeof testLogs[i].details != "undefined" && testLogs[i].details != null )
								testLogsDiv = testLogsDiv + '<td>'+testLogs[i].details.substr(0, 30)+'...<span class="expand" onclick=expand(`details`,'+i+')>See More<span> '+
								' <div id="detailsContent_'+i+'" style="display:none;">'+testLogs[i].details+'</div></td>';
							else
								testLogsDiv = testLogsDiv + "<td>-</td>";
						}else{
		
							if(testLogs[i].details != "")
								testLogsDiv = testLogsDiv + '<td>'+testLogs[i].details.substr(0, 30)+'...<span class="expand" onclick=expand(`details`,'+i+')>See More<span> '+
								' <div id="detailsContent_'+i+'" style="display:none;">'+testLogs[i].details+'</div></td>';
							else
								testLogsDiv = testLogsDiv + "<td>-</td>";
		
						}
							
						testLogsDiv = testLogsDiv + "<td>"+testLogs[i].network+"</td>";
						
						if(!testLogs[i].errorMessage == "")
							testLogsDiv = testLogsDiv + "<td>"+testLogs[i].errorMessage.substr(0, 30)+'...<br><div id="errorMessage_'+i+'" style="display:none;">'+
							testLogs[i].errorMessage+'</div><span class="expand" onclick=expand(`errorMessage`,'+i+')>See More<span></td>'
						else
							testLogsDiv = testLogsDiv + "<td>-</td>";
							
						testLogsDiv = testLogsDiv + "</tr>";
							
					}

				} catch(err) {
					console.log(err);
				}
				
			}else{
				testLogsDiv = testLogsDiv + "<tr><td colspan='8'>No records found</td></tr>";
			}
				
			testLogsDiv = testLogsDiv + "</tbody></table> </div>" ;
			$('#loaderMain').hide();
			$('#testLogDetails').html(testLogsDiv);

			if(!isFiltered){
				$('#typeFilter').empty().append('<option selected="selected" value="all">Select All</option>');
				for(let i = 0; i<typeList.length; i++){
					$('#typeFilter').append($("<option></option>").attr("value", typeList[i])
						.text(typeList[i])); 
				}
			}
		}

		function populateTestLogs(flaggedLogs){

			let questionCount = 0;
			let background = "";
			let	flaggedLogsDiv = "<div class='table-responsive details'> <div>";
			flaggedLogsDiv = flaggedLogsDiv + "<table class='table table-striped table-hover' id='flaggedLogs' style='margin:auto'> ";
			flaggedLogsDiv = flaggedLogsDiv + "<thead> <tr> <th>Visited Date</th> <th>Status</th> ";
			flaggedLogsDiv = flaggedLogsDiv + "</tr></thead> <tbody>";
	
			flaggedLogs.sort( function compare( a, b ) {
				if (a.visitedDate < b.visitedDate)    	return -1;
				else if(a.visitedDate > b.visitedDate)	return  1;
				else                      				return  0;
			});

			if(flaggedLogs.length != 0){
				
				try {
					
					for(let i=0;i < flaggedLogs.length;i++){

						if( flaggedLogs[i].type == 'Question - Answer')
							questionCount++;
						
						if( flaggedLogs[i].type == 'Lost Focus' || flaggedLogs[i].type == 'Error Analytics' )
							background = '#FCEEEE';
						else
							background = '';

						if( typeof flaggedLogs[i].description !== 'undefined' && flaggedLogs[i].description !== 'NA' &&
								flaggedLogs[i].description !== 'null'){
							
							if( !(flaggedLogs[i].type == 'Lost Focus' && flaggedLogs[i].timespent <5) ){
								
								flaggedLogsDiv = flaggedLogsDiv + "<tr>";
								
								if(!Date.parse(flaggedLogs[i].visitedDate)){
									flaggedLogsDiv = flaggedLogsDiv + "<td style=background:"+background+" >"+
									moment(parseInt(flaggedLogs[i].visitedDate)).format("h:mm:ss a")+"</td>";
								}else{
									flaggedLogsDiv = flaggedLogsDiv + "<td style=background:"+background+">"+
									moment(flaggedLogs[i].visitedDate).format("MMM Do YYYY, h:mm:ss a")+"</td>";
								}
									
								if(flaggedLogs[i].type == 'Lost Focus'){
									flaggedLogsDiv = flaggedLogsDiv + "<td style=background:"+background+">Student moved away from test.(Away for "+
											flaggedLogs[i].timespent+" sec)</td>";
								}else if(flaggedLogs[i].type == 'Test Log'){
									flaggedLogsDiv = flaggedLogsDiv + "<td>"+flaggedLogs[i].status+"</td>";
								}else{
				
									flaggedLogsDiv = flaggedLogsDiv + "<td style=background:"+background+">"+flaggedLogs[i].description+"</td>";
				
								}
									
								flaggedLogsDiv = flaggedLogsDiv + "</tr>";
								
							}
							
						}
					}
		
				} catch(err) {
					flaggedLogsDiv = flaggedLogsDiv + "<tr><td colspan='8'>Please refresh the page.</td></tr>"; 
				}
				
			}else{
				flaggedLogsDiv = flaggedLogsDiv + "<tr><td colspan='8'>No records found</td></tr>";
			}
				
			flaggedLogsDiv = flaggedLogsDiv + "</tbody></table> </div>" ;
			$('#loaderMain').hide();
			$('#flaggedLogs').html(flaggedLogsDiv);

		}
	
		function extendTestDuration(sapid, testId, attempt){
			
			let testExtendDuration = $('#duration').val();
			let reason = $('#extendTestDurationReason').val();
			let testDuration = $('#duration').find('option:selected').attr('testDuration');
			let userId ="${userId}";

			if(testExtendDuration == 0){

				let option = "<option value='0' testDuration='"+testDuration+"' selected=''>Please Select A Duration </option> ";
				for (let index = 1; index <= testDuration; index++){
					if( index%5 == 0 )
						option = option + "<option value='"+index+"' testDuration='"+testDuration+"'>"+index+" Minutes</option> ";
				}
				
				$('#duration').focus();
				$('#duration').find('option').remove();
				$('#duration').append(option)
				$('#duration').css('border-color', 'red');
				return;
				
			}else if(reason == ""){
				
				$('#extendTestDurationReason').focus();
				$('#extendTestDurationReason').attr("placeholder", "Please enter the reason for extending time.");
				$('#extendTestDurationReason').css('border-color', 'red');
				return;
			}
			
			var data = {
					'testExtendDuration':testExtendDuration,            //duration by which the test is to be extended
					'duration':testDuration,							//test duration
					'sapid': sapid,
					'testId': testId,
					'noOfRefreshAllowed': -1,
					'attempt': attempt,
					'contactedSupport':'Y',
					'reason':reason,
					'userId':userId
			    }; 

			$.ajax({
				type: 'POST',
				url: '/exam/mbax/ia/student/m/updateStartDateTime',
				data : JSON.stringify(data),
				contentType: "application/json;", 
				dataType: "json",
				success: function(data, textStatus ){

					document.getElementById("responseModal").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
					"<br> The start date and time for the student with Sapid <b>"+data.sapid+" </b>"+
					"and Test Id: <b>"+data.testId+"</b> been updated successfully to <b>"+data.testStartedOn+"</b>"+
					"<div style='width: 100%; margin: auto;'>"+
					"<button type='button' class='btn btn-primary' onclick='handleEventClose()' style='margin: 10px;'>Close</button>"+
					"</div>";

					hideShow("block", "nothing", 'showResponse');
					return;
	
				},
				error : function(request,error) {

					let response = JSON.parse(request.responseText)
			        document.getElementById("responseModal").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
					"<br> <b>"+response.errorMessage+"</b>"+
					"<div style='width: 100%; margin: auto;'>"+
					"<button type='button' class='btn btn-primary' onclick='hideShow(`none`, `nothing`, `showResponse`)' style='margin: 10px;'>Close</button>"+
					"</div>";

					hideShow("block", "nothing", 'showResponse');
					return;
					
			    }
			});
		}

		function updateRefreshCount(sapid, testId, attempt){

			let refreshCount = $('#refreshCount').val();
			let reason = $('#refreshCountReason').val();
			let userId ="${userId}";
			
			if(refreshCount == "0"){
				
				$('#refreshCount').focus();
				let option = '<option value="0" selected="selected">Please Select A Refresh Count</option>';
					option = option + '<option value="-3">3</option>';
					option = option + '<option value="-5">5</option>';
					option = option + '<option value="-8">8</option>';
					option = option + '<option value="-10">10</option>';
				
				$('#refreshCount').find('option').remove();
				$('#refreshCount').append(option)
				$('#refreshCount').css('border-color', 'red');
				return;
				
			}else if(reason == ""){
				
				$('#refreshCountReason').focus();
				$('#refreshCountReason').attr("placeholder", "Please enter the reason for increasing refresh count.");
				$('#refreshCountReason').css('border-color', 'red');
				return;
				
			}
			
			var data = {
					'sapid': sapid,
					'testId': testId,
					'noOfRefreshAllowed':refreshCount,
					'attempt': attempt,
					'contactedSupport':'Y',
					'reason':reason,
					'userId':userId
			    }; 
			
			$.ajax({
				type: 'POST',
				url: '/exam/mbax/ia/student/m/updateRefreshCount',
				data : JSON.stringify(data),
				contentType: "application/json;", 
				dataType: "json",
				success: function(data, textStatus ){
					
					document.getElementById("responseModal").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
					"<br> The refresh count for the student with Sapid <b>"+sapid+" </b>"+
					"and Test Id: <b>"+testId+"</b> been updated successfully to <b>"+refreshCount+"</b>"+
					"<div style='width: 100%; margin: auto;'>"+
					"<button type='button' class='btn btn-primary' onclick='handleEventClose()' style='margin: 10px;'>Close</button>"+
					"</div>";

					hideShow("block", "nothing", 'showResponse');
					return;
						
				},
				error : function(request,error) {

					let response = JSON.parse(request.responseText)
					document.getElementById("responseModal").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
					"<br> <b>"+response.errorMessage+"</b>"+
					"<div style='width: 100%; margin: auto;'>"+
					"<button type='button' class='btn btn-primary' onclick='hideShow(`none`, `nothing`, `showResponse`)' style='margin: 10px;'>Close</button>"+
					"</div>";

					hideShow("block", "nothing", 'showResponse');
					return;
					
			    }
			});
		}

		function handleEventClose(){

			hideShow("none", "nothing", 'showResponse');
			handleTestChange();
		}
		
		function expand(type, index){

			let content;
			if(type == 'errorMessage' )
				content = $('#errorMessage_'+index).html();
			else
				content = $('#detailsContent_'+index).html();

			$('#content').html(content);
			hideShow('block', 'noAction','showContent');
				
		}

		let isClicked = false;
		
		function showChild(index) {

			if(!isClicked){
				$( '#row_'+index ).nextUntil( "tr:has(.child)" ).css("visibility", "visible");
				$( ".child" ).css("background-color", "#FCEEEE");
				$( ".parent" ).css("visibility", "visible");
				isClicked = true;
			}else{
				$( '#row_'+index ).nextUntil( "tr:has(.child)" ).css("visibility", "collapse");
				$( ".parent" ).css("visibility", "visible");
				isClicked = false;
			}
		}

		function typeChange(){

			isFiltered = true;
			let type = $( "#typeFilter" ).val();
			let filteredLogs = [];

			if(type != 'all'){
				for(let i = 0; i<testLogDetails.length; i++){
					if(testLogDetails[i].type == type){
	
						filteredLogs.push(testLogDetails[i]);
					
					}
				}
			}else{
				for(let i = 0; i<testLogDetails.length; i++){
					
					filteredLogs.push(testLogDetails[i]);
						
				}
			}
				
			populateDetailedTestLogs(filteredLogs);
		}

		function extendStudentsTestWindow(sapid, testId){

			let extendedStartTime = $('#testWindowExtendStartTime').val();
			let extendedEndTime =  $('#testWindowExtendEndTime').val();
			let userId ="${userId}";
			let reason = $('#testWindowExtendReason').val();

			if(reason == ""){
				
				$('#testWindowExtendReason').focus();
				$('#testWindowExtendReason').attr("placeholder", "Please enter reason for extending test window.");
				$('#testWindowExtendReason').css('border-color', 'red');
				return;
				
			}
				
			var data = {
					'sapid': sapid,
					'testId': testId,
					'extendedStartTime': extendedStartTime,
					'extendedEndTime': extendedEndTime,
					'contactedSupport':'Y',
					'reason':reason,
					'userId':userId
			    }; 

			$.ajax({
				type: 'POST',
				url: '/exam/mbax/ia/student/m/extendTestWindow',
				data : JSON.stringify(data),
				contentType: "application/json;", 
				dataType: "json",
				success: function(data, textStatus ){
					if(data.errorRecord){

						document.getElementById("responseModal").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
						"<br> <b>"+data.errorMessage+"</b>"+
						"<div style='width: 100%; margin: auto;'>"+
						"<button type='button' class='btn btn-primary' onclick='hideShow(`none`, `nothing`, `extendTestDuration`)' style='margin: 10px;'>Close</button>"+
						"</div>";

						hideShow("block", "nothing", 'showResponse');
						return;
						
					}else{

						document.getElementById("responseModal").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
						"<br> The start datetime and end datetime for the student with Sapid <b>"+sapid+" </b>"+
						"and Test Id: <b>"+testId+"</b> been updated successfully to <b>"+extendedStartTime+" and " + extendedEndTime +"</b>"+
						"<div style='width: 100%; margin: auto;'>"+
						"<button type='button' class='btn btn-primary' onclick='handleEventClose()' style='margin: 10px;'>Close</button>"+
						"</div>";

						hideShow("block", "nothing", 'showResponse');
						return;
						
					}
				}
			});
			
		}

		function extractContent(content) {
			var span = document.createElement('span');
			span.innerHTML = content;
			return span.textContent;
		};		


		function populateLogFileDetails(){

			$('#showLogButton').hide();
			$('#logFileDetails').html('<center><div style="margin: auto; padding:50px"> Loading  <div class="loader"></div> </div></center>');		
			
			let testId = $('#tests').val();
			let sapId = $("#sapId").val();
			let dateSelected = $("#tests option:selected").attr('date').split(" ");
			let date = dateSelected[0];
			let content="";
			
			let body = {
				"sapid":sapId,
				"testId":testId,
				"date":date
			}
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "/exam/mbax/ia/student/m/getStudentLogFileDetails",   
				data : JSON.stringify(body),
				success : function(data) {
					try{

						if(data && data.length != 0){
							
							let	logFileDetailsDiv = "<div>";
							for(let i=0;i < data.length;i++){
								content = extractContent(data[i]);
								logFileDetailsDiv = logFileDetailsDiv + content + "<br><br>";
							}
							logFileDetailsDiv += "</div>";
							$('#logFileDetails').html(logFileDetailsDiv);
							
						}else{
							$('#logFileDetails').html("There is no log file details for the student."); 
						}
						
						
					}catch(err){
						console.log("Got error, Refresh Page! ");
						console.log(err);
					}
					
				},
				error : function(e) {
					console.log("ERROR: ", e);
					alert("Please Refresh The Page.")
					
				}
			});

		}

		function saveOtherIssues(sapid, testId, attempt){
			
			let reason = $('#otherIssuesReason').val();
			let userId ="${userId}";
			
			if(reason == ""){
				
				$('#otherIssuesReason').focus();
				$('#otherIssuesReason').attr("placeholder", "Please enter the reason for contacting support.");
				$('#otherIssuesReason').css('border-color', 'red');
				return;
				
			}
			
			var data = {
					'sapid': sapid,
					'testId': testId,
					'attempt': attempt,
					'contactedSupport':'Y',
					'reason':reason,
					'userId':userId
			    }; 
			
			$.ajax({
				type: 'POST',
				url: '/exam/mbax/ia/student/m/saveSupportForOtherIssues',
				data : JSON.stringify(data),
				contentType: "application/json;", 
				dataType: "json",
				success: function(data, textStatus ){
					
					document.getElementById("responseModal").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
					"<br> Entry has been successfully made for <b>"+data.sapid+" </b>"+
					"and Test Id: <b>"+data.testId+"</b> for contacting support. Reason: <b>"+data.reason+"</b>"+
					"<div style='width: 100%; margin: auto;'>"+
					"<button type='button' class='btn btn-primary' onclick='handleEventClose()' style='margin: 10px;'>Close</button>"+
					"</div>";

					hideShow("block", "nothing", 'showResponse');
					return;
						
				},
				error : function(request,error) {

					let response = JSON.parse(request.responseText)
					document.getElementById("responseModal").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
					"<br> <b>"+response.errorMessage+"</b>"+
					"<div style='width: 100%; margin: auto;'>"+
					"<button type='button' class='btn btn-primary' onclick='hideShow(`none`, `nothing`, `showResponse`)' style='margin: 10px;'>Close</button>"+
					"</div>";

					hideShow("block", "nothing", 'showResponse');
					return;
					
			    }
			});
		}
	
		window.onresize = function(event) {
			let width = $('#duration').width();
			$('.reason').width(width);
		};
	</script>
</body>
</html>