
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
	
	.actionModal-content b{
		font-weight: 700;
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
					<li><a href="#">StudentTestAuditTrailAnalysisForm</a></li>

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

						<h2 class="red text-capitalize">Student's Test Audit Trail
							Analysis Form</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">

							<%@ include file="../adminCommon/messages.jsp"%>
							<!-- Code For Page Goes in Here Start -->

							<%try{ %>
							
							
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
							
							<div class="well" style="">
								--- OR ---
							</div>
							
							
							
							<div class="well" style="">
								<div class="form-group">
									<label for="sapId">Enter Sapid:</label> <input type="text"
										class="form-control" id="sapId">
								</div>
								<div class="form-group">
									<button id="searchTestsBySapidButton" type="submit"
										class="btn btn-default">Go</button>
								</div>
							</div>


							<div class="well" style="">
								<div id="testsSelectBoxContainer" class="form-group"></div>

							</div>


							<div class="well" style="">
								<div id="auditTrailContainer" class="form-group"></div>

							</div>

							<%}catch(Exception e){} %>

							<!-- Code For Page Goes in Here End -->
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

$(document).ready (function(){

	//Search student by name start
	$('#studentName').selectize({
    	create: false,
    	sortField: 'text'
	});

	$('#studentName').on('change', function(){
		console.log(' #studentName on change : ',$('#studentName').val());
		$('#sapId').val($('#studentName').val());
	});
	//Search student by name end


	/////////////
	
	
	$("#searchTestsBySapidButton").click(function(e){
		e.preventDefault();
		let sapId = $("#sapId").val();
		//alert(sapId);
		if(sapId){
			//api call start
				
	let selectBoxHtml = "<div class='form-group'><label for='tests'>Select Test:</label> <select id='tests' class='form-control' >	    <option value=''>loading...</option></select>	</div>";
	
	
	let options = "<option>Loading... </option>";
	$('#tests').html(options);
	$('#testsSelectBoxContainer').html(selectBoxHtml);
	
	 
	var data = {
			sapid:sapId
	}
	
	console.log("===================> data  : " + data);
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam/m/getTestsBySapid",   
		data : JSON.stringify(data),
		success : function(data) {
			console.log("SUCCESS getTestsBySapid : ");
			console.log(data);
			let testsData = data.tests;
			
			options = "";
			let allOption = "";
			
			//Data Insert For testsData
			//Start
			for(let i=0;i < testsData.length;i++){
				allOption = allOption + ""+ testsData[i].id +",";
				options = options + "<option value='" + testsData[i].id + "'> " + testsData[i].testName + "-" + testsData[i].startDate + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			console.log("==========> options\n" + options);
			$('#tests').html(
					 "<option value=''>Select Test</option>" + options
			);
			//End
			
			
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
			display(e);
		}
	});
	
	
			//api call end
		}else{
			alert('Enter Sapid To Search For Test');
		}
	});
	
	//Test select changed start
	$(document).on('change', '#tests', function(){
		let testId = $('#tests').val();
		let sapId = $("#sapId").val();
		
		//alert(testId);
		
		if(testId == ''){

			alert("Select a test!");
		}
		
		$('#auditTrailContainer').html("<p>Loading...</p>");
		
		//api call start 
	
	var details = {
			sapid: sapId,
			testId: testId
	}
	
	console.log("===================> data  : " + details);
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam/m/getStudentTestAuditTrailDataBySapidTestId",   
		data : JSON.stringify(details),
		success : function(data) {
			console.log("SUCCESS getStudentTestAuditTrailDataBySapidTestId : ");
			console.log(data);
						
			try{
				let testData = data.test;
				let studentTestDetailsData = data.studentTestDetails;
				let answersData = data.answers;
				let errorAnalyticsData = data.errorAnalytics;
				let networkLogsData = data.networkLogs;
				let pageVisitsData = data.pageVisits;
				
				let editTestDetailsHtmlContainer = "";
				let testDetailsHtmlContainer = "";
				let studentTestDetailsHtmlContainer = "";
				let answerDetailsHtmlContainer = "";
				let networkLogsHtmlContainer = "";
				let errorAnalysisHtmlContainer = "";
				let pageVisitsHtmlContainer = "";
				let errorAnalyticsHtmlContainer = "";
				
				//Start of edit start time
				
				
				if(studentTestDetailsData){

					console.log('studentTestDetailsData: '+JSON.stringify(studentTestDetailsData))
					let	editTestDetailsHtmlDiv = "<div> <p>Sapid : <b>"+studentTestDetailsData.sapid+"</b> </p>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <p  id='startDateValue'>TestStartedOn <b>"+studentTestDetailsData.testStartedOn+"</b> </p>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <div id='startDateEdit' style='display: none;' class='row' value='test'> <div class='col-md-4'><span> <b>Student's Start DateTime :</b> </span>"+
					"<input id='startDateInput' type='datetime-local' class='form-control ' name='startDate'> "+ 
					"<button type='submit' class='btn btn-primary' id='saveButton' onclick=updateStudentDateTime("+details.sapid+','+details.testId+") > Save</button>"+
					"<button type='submit' class='btn btn-primary' id='cancelButton' onclick=cancleRequest() style='margin-left:10px;'> Cancel</button></div></div>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <p>TestEndedOn : <b>"+studentTestDetailsData.testEndedOn+"</b> </p>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <p>TestCompleted : <b>"+studentTestDetailsData.testCompleted+"</b> </p>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <p>testQuestions : <b>"+studentTestDetailsData.testQuestions+"</b> </p>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <p>currentQuestion : <b>"+studentTestDetailsData.currentQuestion+"</b> </p>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <p>noOfQuestionsAttempted : <b>"+studentTestDetailsData.noOfQuestionsAttempted+"</b> </p>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <p>Score : <b>"+studentTestDetailsData.score+"</b> </p>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <p>showResult : <b>"+studentTestDetailsData.showResult+"</b> </p>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <div id='refreshCountEdit' style='display: none;' class='row' value='test'> <div class='col-md-4'><span> <b>Student's Refresh Count :</b> </span>"+
					"<input id='refreshCount' type='number' class='form-control ' name='refreshCount' placeholder='Enter Refresh Count'> "+ 
					"<button type='submit' class='btn btn-primary' id='updateRefreshCount' required='' onclick=updateRefreshCount("+details.sapid+','+details.testId+','+studentTestDetailsData.attempt+") > Save</button>"+
					"<button type='submit' class='btn btn-primary' id='cancelButton' onclick=cancleRequest() style='margin-left:10px;'> Cancel</button></div></div>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + " <p><button type='submit' class='btn btn-primary' id='editButton' onclick='hideShow(`block`,`noAction`,`editAction`)'> Edit Start Time </button>"+
					" <button type='button' class='btn btn-primary'  onclick='hideShow(`block`,`noAction`,`editRefreshCount`)' id='editRefreshCountButton'> Edit Refresh Count </button></p>";
					editTestDetailsHtmlDiv = editTestDetailsHtmlDiv + "</div>";
				
					editTestDetailsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo' data-toggle='collapse'><b>Edit Student's Test Details</b></a><div id='demo' class='container collapse in'>"+editTestDetailsHtmlDiv+"</div> </div>";
				
				
				}else{
					editTestDetailsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo' data-toggle='collapse'>Student's Test Details</a><div id='demo' class='container collapse in'>Student Did Not Attempt the test.</div> </div>";
					
				}
										
				//End of edit start time 
				
				//Show test Details start
				if(testData){
						
					let testDetailsHtmlDiv = "<div> <p>Test Name : <b>"+testData.testName+"</b> </p>";
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>StartDate : <b>"+testData.startDate+"</b> </p>";
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>EndDate : <b>"+testData.endDate+"</b> </p>";
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>MaxQuestnToShowToStudents : <b>"+testData.maxQuestnToShow+"</b> </p>";
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>MaxScore : <b>"+testData.maxScore+"</b> </p>";
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Duration : <b>"+testData.duration+"</b> </p>";
					testDetailsHtmlDiv = testDetailsHtmlDiv + " <p>Duration : <b>"+testData.duration+"</b> </p>";
					testDetailsHtmlDiv = testDetailsHtmlDiv + "</div>";
					
					testDetailsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo1' data-toggle='collapse'>Test Details</a><div id='demo1' class='container collapse in'>"+testDetailsHtmlDiv+"</div> </div>";
					
				}else{
					alert("RefreshPage! Unable to get test data for id: "+testId);
				}
				//Show test Details end
				
				//Show students test Details start
				if(studentTestDetailsData){
					
				let	studentTestDetailsHtmlDiv = "<div> <p>Sapid : <b>"+studentTestDetailsData.sapid+"</b> </p>";
					studentTestDetailsHtmlDiv = studentTestDetailsHtmlDiv + " <p>TestStartedOn : <b>"+studentTestDetailsData.testStartedOn+"</b> </p>";
					studentTestDetailsHtmlDiv = studentTestDetailsHtmlDiv + " <p>TestEndedOn : <b>"+studentTestDetailsData.testEndedOn+"</b> </p>";
					studentTestDetailsHtmlDiv = studentTestDetailsHtmlDiv + " <p>TestCompleted : <b>"+studentTestDetailsData.testCompleted+"</b> </p>";
					studentTestDetailsHtmlDiv = studentTestDetailsHtmlDiv + " <p>testQuestions : <b>"+studentTestDetailsData.testQuestions+"</b> </p>";
					studentTestDetailsHtmlDiv = studentTestDetailsHtmlDiv + " <p>currentQuestion : <b>"+studentTestDetailsData.currentQuestion+"</b> </p>";
					studentTestDetailsHtmlDiv = studentTestDetailsHtmlDiv + " <p>noOfQuestionsAttempted : <b>"+studentTestDetailsData.noOfQuestionsAttempted+"</b> </p>";
					studentTestDetailsHtmlDiv = studentTestDetailsHtmlDiv + " <p>Score : <b>"+studentTestDetailsData.score+"</b> </p>";
					studentTestDetailsHtmlDiv = studentTestDetailsHtmlDiv + " <p>showResult : <b>"+studentTestDetailsData.showResult+"</b> </p>";
					studentTestDetailsHtmlDiv = studentTestDetailsHtmlDiv + "</div>";
				
				 studentTestDetailsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo2' data-toggle='collapse'>Student's Test Details</a><div id='demo2' class='container collapse in'>"+studentTestDetailsHtmlDiv+"</div> </div>";
				
				
				}else{
					studentTestDetailsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo2' data-toggle='collapse'>Student's Test Details</a><div id='demo2' class='container collapse in'>Student Did Not Attempt the test.</div> </div>";
					
				}
				//Show students test Details end
				

				//Show students answers Details start
				if(answersData){
					
				let	answerDetailsHtmlDiv = "<div class='table-responsive'> <table class='table table-striped table-hover'>   <thead> <tr> <th>QuestionId</th> <th>Answer</th>   <th>CreatedBy</th>  <th>CreatedAt</th>   <th>LastModifiedBy</th>  <th>LastModifiedAt</th>   </tr></thead>";
				answerDetailsHtmlDiv = answerDetailsHtmlDiv + " <tbody>";
					for(let i=0;i < answersData.length;i++){
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<tr>";

						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+answersData[i].questionId+"</td>";
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+answersData[i].answer+"</td>";
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+answersData[i].createdBy+"</td>";
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+answersData[i].createdDate+"</td>";
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+answersData[i].lastModifiedBy+"</td>";
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+answersData[i].lastModifiedDate+"</td>";
						/*
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+answersData[i].marks+"</td>";
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+answersData[i].isChecked+"</td>";
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "<td>"+answersData[i].remarks+"</td>";
						*/
						answerDetailsHtmlDiv = answerDetailsHtmlDiv + "</tr>";
					}
					answerDetailsHtmlDiv = answerDetailsHtmlDiv + "</tbody></table> </div>";
					
					answerDetailsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo3' data-toggle='collapse'>Student's Answers</a><div id='demo3' class='container collapse in'>"+answerDetailsHtmlDiv+"</div> </div>";
				
				
				}else{
					answerDetailsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo3' data-toggle='collapse'>Student's Test Details</a><div id='demo3' class='container collapse in'>Student Did Not Attempt the test.</div> </div>";
					
				}
				//Show students answer Details end
				
				//show networklog start
				if(networkLogsData){
					
					let date = new Date();
					let	networkLogsHtmlDiv = "<div class='table-responsive'> <table class='table table-striped table-hover' id='networkLogsTable'>   <thead> <tr> <th>SAPID</th> <th>Duration</th>   <th>Status</th>  <th>Created At</th>  <th>Network Info</th>  <th>API Name</th>  <th>Error Message</th>  </tr></thead>";
					networkLogsHtmlDiv = networkLogsHtmlDiv + " <tbody>";
						for(let i=0;i < networkLogsData.length;i++){
							date.setTime(networkLogsData[i].created_at);
							networkLogsHtmlDiv = networkLogsHtmlDiv + "<tr>";
							networkLogsHtmlDiv = networkLogsHtmlDiv + "<td>"+networkLogsData[i].sapid+"</td>";
							networkLogsHtmlDiv = networkLogsHtmlDiv + "<td>"+networkLogsData[i].duration+"</td>";
							networkLogsHtmlDiv = networkLogsHtmlDiv + "<td>"+networkLogsData[i].status+"</td>";
							networkLogsHtmlDiv = networkLogsHtmlDiv + "<td>"+date.toLocaleString()+"</td>";
							
							if(networkLogsData[i].networkInfo === "" || !networkLogsData[i].networkInfo ){
								networkLogsHtmlDiv = networkLogsHtmlDiv + "<td> - </td>";
							}else{
								networkLogsHtmlDiv = networkLogsHtmlDiv + "<td>"+networkLogsData[i].networkInfo+"</td>";
							}
							
							networkLogsHtmlDiv = networkLogsHtmlDiv + "<td>"+networkLogsData[i].name+"</td>";
							
							if(networkLogsData[i].error_message === "" || !networkLogsData[i].error_message ){
								networkLogsHtmlDiv = networkLogsHtmlDiv + "<td> - </td>";
							}else{
								networkLogsHtmlDiv = networkLogsHtmlDiv + "<td>"+networkLogsData[i].error_message+"</td>";
							}
							
							
							networkLogsHtmlDiv = networkLogsHtmlDiv + "</tr>";
						}
						networkLogsHtmlDiv = networkLogsHtmlDiv + "</tbody></table> </div>" ;
						
						networkLogsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo4' data-toggle='collapse'>Network Logs</a><div id='demo4' class='container collapse in'>"+networkLogsHtmlDiv+"</div> </div>";
					
					
					}else{
						networkLogsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo4' data-toggle='collapse'>Network Logs</a><div id='demo4' class='container collapse in'>Student Did Not Visite Website.</div> </div>";
						
					}
				//show newtorklog ends
				
				
				//show mobile page visits 
				if(pageVisitsData){
					let date = new Date();
					let	pageVisitsHtmlDiv = "<div class='table-responsive'> <table class='table table-striped table-hover' id='pageVisitsTable'>   <thead> <tr> <th>Page</th> <th>Type</th> <th>Visited Date</th> <th>Timespent</th>  <th>Device Details</th>   <th>ipAddress</th> </tr></thead>";
					pageVisitsHtmlDiv = pageVisitsHtmlDiv + " <tbody>";
						for(let i=0; i < pageVisitsData.length; i++){
							date.setTime(pageVisitsData[i].visiteddate)
							
							pageVisitsHtmlDiv = pageVisitsHtmlDiv + "<tr>";
							pageVisitsHtmlDiv = pageVisitsHtmlDiv + "<td>"+pageVisitsData[i].path+"</td>";
							pageVisitsHtmlDiv = pageVisitsHtmlDiv + "<td>"+pageVisitsData[i].applicationType+"</td>";
							pageVisitsHtmlDiv = pageVisitsHtmlDiv + "<td>"+date.toLocaleString()+"</td>";
							pageVisitsHtmlDiv = pageVisitsHtmlDiv + "<td>"+pageVisitsData[i].timespent+"</td>";
							pageVisitsHtmlDiv = pageVisitsHtmlDiv + "<td> Device Name: "+pageVisitsData[i].deviceName+" Device OS: "+pageVisitsData[i].deviceOS+" <br>System Version: "+pageVisitsData[i].deviceSystemVersion+"</td>";
							pageVisitsHtmlDiv = pageVisitsHtmlDiv + "<td>"+pageVisitsData[i].ipAddress+"</td>";
							pageVisitsHtmlDiv = pageVisitsHtmlDiv + "</tr>";
						}
						pageVisitsHtmlDiv = pageVisitsHtmlDiv + "</tbody></table> </div>" ;
						
						pageVisitsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo6' data-toggle='collapse'>Page Visits From Mobile</a><div id='demo6' class='container collapse in'>"+pageVisitsHtmlDiv+"</div> </div>";
					
					
					}else{
						pageVisitsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo6' data-toggle='collapse'>Page Visits From Mobile</a><div id='demo6' class='container collapse in'>Student Did Not Visite Website.</div> </div>";
						
					}
				//show mobile page vistis ends
				
				//show errorAnalyticsHtmlContainer errors from server start
				
				if(errorAnalyticsData){
					//id, sapid, module, fixed, ipAddress, userAgent, stackTrace, createdBy, createdOn, updatedBy, updatedOn
					let	errorAnalyticsHtmlDiv = "<div class='table-responsive'> <table class='table table-striped table-hover'>   <thead> <tr> <th>IpAddress</th> <th>userAgent</th>   <th>stackTrace</th>  <th>createdOn</th>  </tr></thead>";
					errorAnalyticsHtmlDiv = errorAnalyticsHtmlDiv + " <tbody>";
						for(let i=0;i < errorAnalyticsData.length;i++){
							errorAnalyticsHtmlDiv = errorAnalyticsHtmlDiv + "<tr>";

							errorAnalyticsHtmlDiv = errorAnalyticsHtmlDiv + "<td>"+errorAnalyticsData[i].ipAddress+"</td>";
							errorAnalyticsHtmlDiv = errorAnalyticsHtmlDiv + "<td>"+errorAnalyticsData[i].userAgent+"</td>";
							errorAnalyticsHtmlDiv = errorAnalyticsHtmlDiv + "<td>"+errorAnalyticsData[i].stackTrace+"</td>";
							errorAnalyticsHtmlDiv = errorAnalyticsHtmlDiv + "<td>"+errorAnalyticsData[i].createdOn+"</td>";
							
							errorAnalyticsHtmlDiv = errorAnalyticsHtmlDiv + "</tr>";
						}
						errorAnalyticsHtmlDiv = errorAnalyticsHtmlDiv + "</tbody></table> </div>" ;
						
						errorAnalyticsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo7' data-toggle='collapse'>Error's From Server</a><div id='demo7' class='container collapse in'>"+errorAnalyticsHtmlDiv+"</div> </div>";
					
					
					}else{
						errorAnalyticsHtmlContainer = "<div class='panel panel-primary container'> <a href='#demo7' data-toggle='collapse'>Page Visits From Web</a><div id='demo7' class='container collapse in'>No errors.</div> </div>";
						
					}
				
				//show errors from server end
				
				
				let htmlForAuditTrailDetails = editTestDetailsHtmlContainer+testDetailsHtmlContainer + studentTestDetailsHtmlContainer + answerDetailsHtmlContainer + errorAnalyticsHtmlContainer + networkLogsHtmlContainer + pageVisitsHtmlContainer;
				
				$('#auditTrailContainer').html(htmlForAuditTrailDetails);
				
				
				
				
			}catch(err){
				console.log("Got error, Refresh Page! ");
				console.log(err);
			}
			
		},
		error : function(e) {

			console.log("ERROR: ", e);
			display(e);
			alert("Please Refresh The Page.")
			
		}
	});
	
		//api call end
		
	});
	//Test select changed end 
	
	$(document).ready(function() {
		    $('#networkLogsTable').DataTable();
		    $('#pageVisitsWebTable').DataTable();
		    $('#pageVisitsTable').DataTable();
		} );	
	
	/////////////
	$('.commonLinkbtn').on('click',function(){
		let testId = $(this).attr('data-testId');
		let modalBody = "<center><h4>Loading...</h4></center>";
		let data = {
			'id':testId
		};
		$.ajax({
			   type : "POST",
			   contentType : "application/json",
			   url : "/exam/getProgramsListForCommonTest",   
			   data : JSON.stringify(data),
			   success : function(data) {
				   modalBody = '<div class="table-responsive"> <table class="table"> <thead><td>Exam year</td><td>Exam month</td> <td>Consumer Type</td> <td>Program Structure</td> <td>Program</td> <td>Subject</td> <td>Action</td> </thead><tbody>';
				   for(let i=0;i < data.length;i++){
					   modalBody = modalBody + '<tr><td>'
					   							+ data[i].year 
					   							+'</td><td>'
					   							+ data[i].month 
					   							+'</td><td>'
					   							+ data[i].consumerType 
					   							+'</td><td>'
					   							+ data[i].programStructure 
					   							+'</td><td>'
					   							+ data[i].program 
					   							+'</td><td>'
					   							+ data[i].subject 
					   							+'</td><td> '
					   							+' <a href="editTestFromSearchTestsForm?testId='+ data[i].id 
					   									+'&consumerProgramStructureId='+ data[i].consumerProgramStructureId
					   									+'&testConfigIds='+ data[i].consumerTypeId
					   										+'~'+data[i].programStructureId
					   										+'~'+data[i].programId
					   										+'~'+data[i].subject.replace(/\&/g, 'and')
					   									+'"  title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a></td></tr>';
				   }
				   
				   modalBody = modalBody + '<tbody></table></div>';
				   $('.modalBody').html(modalBody);
			   },
			   error : function(e) {
				   alert("Please Refresh The Page.")
			   }
		});
		$('.modalBody').html(modalBody);
		//modal-body
		$('#myModal').modal('show');
	});


///////////////////////////////////////////////////////////////////
	
	
	$('.selectConsumerType').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programStructureId').html(options);
	$('#programId').html(options);
	$('.selectSubject').html(options);
	
	 
	var data = {
			id:this.value
	}
console.log(this.value)
	
	console.log("===================> data id : " + id);
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByConsumerType",   
		data : JSON.stringify(data),
		success : function(data) {
			console.log("SUCCESS Program Structure: ", data.programStructureData);
			console.log("SUCCESS Program: ", data.programData);
			console.log("SUCCESS Subject: ", data.subjectsData);
			var programData = data.programData;
			var programStructureData = data.programStructureData;
			var subjectsData = data.subjectsData;
			
			options = "";
			let allOption = "";
			
			//Data Insert For Program List
			//Start
			for(let i=0;i < programData.length;i++){
				allOption = allOption + ""+ programData[i].id +",";
				options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			console.log("==========> options\n" + options);
			$('#programId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			options = ""; 
			allOption = "";
			//Data Insert For Program Structure List
			//Start
			for(let i=0;i < programStructureData.length;i++){
				allOption = allOption + ""+ programStructureData[i].id +",";
				options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			console.log("==========> options\n" + options);
			$('#programStructureId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			
			options = ""; 
			allOption = "";
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
			);
			//End
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});
	
	///////////////////////////////////////////////////////
	
	
		$('.selectProgramStructure').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programId').html(options);
	$('.selectSubject').html(options);
	
	 
	var data = {
			programStructureId:this.value,
			consumerTypeId:$('#consumerTypeId').val()
	}
	console.log(this.value)
	
	console.log("===================> data id : " + $('#consumerTypeId').val());
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByProgramStructure",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data.programData);
			var programData = data.programData;
			var subjectsData = data.subjectsData;
			
			options = "";
			let allOption = "";
			
			//Data Insert For Program List
			//Start
			for(let i=0;i < programData.length;i++){
				allOption = allOption + ""+ programData[i].id +",";
				options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			console.log("==========> options\n" + options);
			$('#programId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			
			options = ""; 
			allOption = "";
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
			);
			//End
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});


/////////////////////////////////////////////////////////////

	
		$('.selectProgram').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('.selectSubject').html(options);
	
	 
	var data = {
			programId:this.value,
			consumerTypeId:$('#consumerTypeId').val(),
			programStructureId:$('#programStructureId').val()
	}
	console.log(this.value)
	
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByProgram",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data.subjectsData);
			
			var subjectsData = data.subjectsData;
			
			
			
			
			options = ""; 
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
			);
			//End
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});

//////////////////////////////////////////////


///////////////////////////////////////////////


$('#applicableType').on('change', function(){

let applicableType = this.value;
console.log('IN applicableType on change event : applicableType = ');
console.log(applicableType);

var data = {
programId:$('.selectProgram').val(),
consumerTypeId:$('#consumerTypeId').val(),
programStructureId:$('#programStructureId').val(),
subject:$('.selectSubject').val(),
acadYear:$('#acadYear').val(),
acadMonth:$('#acadMonth').val()
};
console.log("data : ");
console.log(data);

if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === '' || data.subject === ''
|| data.acadYear === ''  || data.acadMonth === ''){
alert('Please Select All Consumer, Program, ProgramStructureId, subject, acadMonth and acadYear');
return;
}

if( data.programId.split(',').length > 1 ||
data.consumerTypeId.split(',').length > 1 || 
data.programStructureId.split(',').length > 1 ){

document.getElementById("applicableType").options[3].selected = 'selected';

return;
}

if(applicableType === 'batch'){
//make api call to get batch details 

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getBatchDataByMasterKeyConfig",   
data : JSON.stringify(data),
success : function(data) {

console.log("SUCCESS: dataForReferenceId :");
console.log(data.dataForReferenceId);


var dataForReferenceId = data.dataForReferenceId;

if( !dataForReferenceId && dataForReferenceId.length === 0){
alert("Unable to get batches for selected program config, Kindly update selections and try again.");
return;
}

var labelForReferenceId = "<label for='refrenceId'>Applicable Batch</label>";
var selectElementTopPart = "<select  id='referenceId'  name='referenceId' readonly='readonly' placeholder='referenceId' type='text' class='form-control' required='required'>" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForReferenceId.length;i++){

options = options + "<option value='" + dataForReferenceId[i].id + "'> "
			+ dataForReferenceId[i].name + " </option>";
}


console.log("==========> options\n" + options);

var htmlToAppend = labelForReferenceId 
		+ selectElementTopPart 
		+ " <option disabled selected value=''> Select Option </option> " + options 
		+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#referenceIdDiv').html( htmlToAppend );
//End




},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});
//make api call to get batch details end

}else if(applicableType === 'module'){
//make api call to get module details

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getModuleDataByMasterKeyConfig",   
data : JSON.stringify(data),
success : function(data) {

console.log("SUCCESS: dataForReferenceId :");
console.log(data.dataForReferenceId);


var dataForReferenceId = data.dataForReferenceId;

if( !dataForReferenceId && dataForReferenceId.length === 0){
alert("Unable to get Modules for selected program config, Kindly update selections and try again.");
return;
}

var labelForReferenceId = "<label for='refrenceId'>Applicable Modules</label>";
var selectElementTopPart = "<select  id='referenceId'  name='referenceId' readonly='readonly' placeholder='referenceId' type='text' class='form-control' required='required'>" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForReferenceId.length;i++){

options = options + "<option value='" + dataForReferenceId[i].id + "'> " 
			+ dataForReferenceId[i].topic + " </option>";
}


console.log("==========> options\n" + options);

var htmlToAppend = labelForReferenceId 
		+ selectElementTopPart 
		+ " <option disabled selected value=''> Select Option </option> " + options 
		+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#referenceIdDiv').html( htmlToAppend );
//End




},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});


//make api call to get module details end

}else if(applicableType === 'old'){ 
return; 
} else{
$('#referenceIdDiv').html( "<span></span>" );
alert('Please select an option.');
}
});

///////////////////////////////////////////////








	
	
	
});



</script>
	<div id="editAction" class="actionModal">
		<div class="actionModal-content" id="actionModel" style="max-width: 500px; text-align: center;">
			<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>
			<br> <b>Are you sure you want to edit the start time for the
				student? </b>
			<div style='width: 100%; margin: auto;'>
				<button type='button' class='btn btn-primary ' style='margin: 10px;' onclick="hideShow('none', 'editDateTime', 'editAction')">Proceed</button>
				<button type='button' class='btn btn-primary' onclick="hideShow('none', 'noAction', 'editAction')" style='margin: 10px;'>Close</button>
			</div>
		</div>
	</div>
	
	<div id="editRefreshCount" class="actionModal">
		<div class="actionModal-content" id="actionModel" style="max-width: 500px; text-align: center;">
			<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>
			<br> <b>Are you sure you want to edit the refresh count for the student? </b>
			<div style='width: 100%; margin: auto;'>
				<button type='button' class='btn btn-primary ' style='margin: 10px;' onclick="hideShow('none', 'editRefreshCount', 'editRefreshCount')">Proceed</button>
				<button type='button' class='btn btn-primary' onclick="hideShow('none', 'noAction','editRefreshCount')" style='margin: 10px;'>Close</button>
			</div>
		</div>
	</div>
	
	<script>

		function hideShow(action, toDo, name){
			console.log("in hideShow");
			var model = document.getElementById(name);
			model.style.display = action;

			if(toDo === "editDateTime"){
				document.getElementById('startDateValue').style.display = "none";
				document.getElementById('startDateEdit').style.display = "block";
				document.getElementById('startDateEdit').focus();
				document.getElementById('editButton').style.display = "none";
				document.getElementById('saveButton').style.display = "inline-block";
				document.getElementById('cancelButton').style.display = "inline-block";
				$('#editRefreshCountButton').hide();
			}else if(toDo === "editRefreshCount"){

				$('#refreshCountEdit').show();
				$('#editButton').hide();
				$('#startDateEdit').hide();
				$('#saveButton').hide();
				$('#cancelButton').hide();
				$('#editRefreshCountButton').hide();
				$('#editButton').hide();
				
			}
			
		} 
		
		function cancleRequest(){
			
			$('#editRefreshCountButton').show();
			$('#editButton').show();
			$('#startDateValue').show();
			$('#refreshCountEdit').hide();
			$('#startDateEdit').hide();
			$('#saveButton').hide();
			$('#cancelButton').hide();
			
		}
	</script>

	<script>
		function updateStudentDateTime(sapid, testId){
			let date = new Date(document.getElementById('startDateInput').value);

			//Updated by Pranit on 9thMay20 to fix single digit in dates issue start
			let monthToSetInDateTime = ( ((date.getMonth()+1) < 10) ? ( ( (""+(date.getMonth()+1)).length < 2 ? ("0"+(date.getMonth()+1)) : (date.getMonth()+1)) ) : (date.getMonth()+1)  )
			
			let dateToSetInDateTime = ( ((date.getDate()) < 10) ? ( ( (""+(date.getDate())).length < 2 ? ("0"+(date.getDate())) : (date.getDate())) ) : (date.getDate())  )
			
			let hoursToSetInDateTime = ( ((date.getHours()) < 10) ? ( ( (""+(date.getHours())).length < 2 ? ("0"+(date.getHours())) : (date.getHours())) ) : (date.getHours())  )
			
			let minsToSetInDateTime = ( ((date.getMinutes()) < 10) ? ( ( (""+(date.getMinutes())).length < 2 ? ("0"+(date.getMinutes())) : (date.getMinutes())) ) : (date.getMinutes())  )
			
			let dateTime = date.getFullYear()+"-"+monthToSetInDateTime+"-"+dateToSetInDateTime+" "+hoursToSetInDateTime+":"+minsToSetInDateTime+":00";
			//Updated by Pranit on 9thMay to fix single digit in dates issue end
			
			var data = {
					'sapid': sapid,
					'testId': testId,
					'testStartedOn': dateTime
			    }; 
			
			$.ajax({
				type: 'POST',
				url: '/exam/api/updateStartDateTime',
				data : JSON.stringify(data),
				contentType: "application/json;", 
				dataType: "json",
				success: function(data, textStatus ){
					if(data.errorRecord){
						console.log("In IF");
						console.log("In Else");
						document.getElementById("actionModel").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
						"<br> <b>"+data.errorMessage+"</b>"+
						"<div style='width: 100%; margin: auto;'>"+
						"<button type='button' class='btn btn-primary' onclick='hideShow(`none`, `nothing`, `editAction`)' style='margin: 10px;'>Close</button>"+
						"</div>";

						hideShow("block", "nothing", 'editAction');
						return;
					}else{
						console.log("In Else");
						document.getElementById("actionModel").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
						"<br> The start date and time for the student with Sapid <b>"+data.sapid+" </b>"+
						"and Test Id <b>"+data.testId+"</b> been updated successfully to <b>"+data.testStartedOn+"</b>"+
						"<div style='width: 100%; margin: auto;'>"+
						"<button type='button' class='btn btn-primary' onclick=window.location.reload() style='margin: 10px;'>Close</button>"+
						"</div>";

						hideShow("block", "nothing", 'editAction');
						return;
					}
				}
			});
		}

		function updateRefreshCount(sapid, testId, attempt){

			let refreshCount = $('#refreshCount').val();
			if(refreshCount == ""){
				$('#refreshCount').focus();
				$('#refreshCount').attr('placeholder','Please Enter the Refresh Count');
				$('#refreshCount').css('border-color', 'red');
				return;
			}
			
			var data = {
					'sapid': sapid,
					'testId': testId,
					'noOfRefreshAllowed':refreshCount,
					'attempt': attempt
			    }; 

			console.log('body: '+JSON.stringify(data));
			
			$.ajax({
				type: 'POST',
				url: '/exam//m/updateRefreshCount',
				data : JSON.stringify(data),
				contentType: "application/json;", 
				dataType: "json",
				success: function(data, textStatus ){
					if(data.errorRecord){
						document.getElementById("actionModel").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
						"<br> <b>"+data.errorMessage+"</b>"+
						"<div style='width: 100%; margin: auto;'>"+
						"<button type='button' class='btn btn-primary' onclick='hideShow(`none`, `nothing`, `editAction`)' style='margin: 10px;'>Close</button>"+
						"</div>";

						hideShow("block", "nothing", 'editAction');
						return;
					}else{
						document.getElementById("actionModel").innerHTML = "<i class='fa-solid fa-triangle-exclamation'></i> <b>Confirm Action</b> <br>"+
						"<br> The refresh count for the student with Sapid <b>"+sapid+" </b>"+
						"and Test Id <b>"+testId+"</b> been updated successfully to <b>"+refreshCount+"</b>"+
						"<div style='width: 100%; margin: auto;'>"+
						"<button type='button' class='btn btn-primary' onclick=window.location.reload() style='margin: 10px;'>Close</button>"+
						"</div>";

						hideShow("block", "nothing", 'editAction');
						return;
					}
				}
			});
		}
	</script>
</body>
</html>