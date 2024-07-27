<!DOCTYPE html>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload TEE Marks List" name="title" />
</jsp:include>
<head>
	<link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
	<style>
		.dataTables_filter > label > input{
			float:right !important;
		}
		.toggleListWell{
		cursor: pointer !important;
			margin-bottom:0px !important;
		}
		.toggleWell{
			background-color:white !important;
		}
		input[type="radio"]{
			width:auto !important;
			height:auto !important;
			
		}
		.optionsWell{
			padding:0px 10px;
		}
	</style>
	
	<style>
		.accordion {
		  background-color: #eee;
		  color: #444;
		  cursor: pointer;
		  padding: 18px;
		  width: 100%;
		  border: none;
		  text-align: left;
		  outline: none;
		  font-size: 15px;
		  transition: 0.4s;
		}
		
		.active, .accordion:hover {
		  background-color: #ccc;
		}
		
		.accordion:after {
		  content: '\002B';
		  color: #777;
		  font-weight: bold;
		  float: right;
		  margin-left: 5px;
		}
		
		.active:after {
		  content: "\2212";
		}
		
		.panel {
		  padding: 0 18px;
		  background-color: white;
		  max-height: 0;
		  overflow: hidden;
		  transition: max-height 0.2s ease-out;
		}
	</style>
</head>
<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Capstone Project Marks</legend></div>
			
			<%@ include file="messages.jsp"%>
			<%@ include file="uploadExcelErrorMessages.jsp"%>
												
			<form:form modelAttribute="teeResultBean" method="post" 	enctype="multipart/form-data" action="uploadCapstoneProjectMarks">
				<div class="panel-body">
					<div class="row">
						
						<div class="col-md-3">
							<div class="form-group">
							<label for="sel1">Acad Month</label>
								<select name="acadMonth" id="acadMonth" class="form-control">
									<option value="">-- Acad Month --</option>
									<c:forEach items="${acadMonth}" var="month">
									<option value="${month}">${month}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="col-md-3">
							<div class="form-group">
							<label for="sel1">Acad Year</label>
								<select name="acadYear" id="acadYear" class="form-control">
									<option value="">-- Acad Year --</option>
									<c:forEach items="${acadYear}" var="year">
									<option value="${year}">${year}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Program Type:</label> 
								<form:select
									path="programType" class="form-control programType"  value="${ teeResultBean.programType }">
									<option disabled selected value="">Select Consumer Type</option>
									<c:forEach var="productType" items="${productTypes}">
										<c:choose>
											<c:when
												test="${productType == teeResultBean.programType}">
												<option selected value="<c:out value="${productType}"/>">
													<c:out value="${productType}" />
												</option>
											</c:when>
											<c:otherwise>
												<option value="<c:out value="${productType}"/>">
													<c:out value="${productType}" />
												</option>
											</c:otherwise>
										</c:choose>

									</c:forEach>
									</form:select>
							</div>
						</div>

						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Batch:</label> <form:select path="batchId"
									class="form-control batches"  value="${ teeResultBean.batchId }">
									<option disabled selected value="">-- select batch --</option>
								</form:select>
							</div>
						</div>

						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Subject:</label>
								<form:select path="timebound_id" class="form-control" id="subject"  value="${ teeResultBean.timebound_id }">
									<option disabled selected value="">-- select subject --</option>
								</form:select>
							</div>
						</div>
						<div class="col-md-6 ">
							<div class="form-group">
								<form:label for="fileData" path="fileData">Select file</form:label>
								<form:input path="fileData" type="file" value="${teeResultBean.fileData}"/>
							</div>		
						</div>
				
						<div class="col-md-12 column">
							<b>Format of Upload: </b><br>
							Sap ID | Simulation Score | Simulation Max Score | CompXM Score | CompXM Max Score <br>
							<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/CAPSTONE_PROJECT_MARKS_TEMPLATE.xlsx" target="_blank">Download a Sample Template</a> <br>
						</div>
					
					
					</div>
					<br>
					<div class="row">
						<div class="col-md-6 column">
							<button id="submit" name="submit" class="btn btn-large btn-primary"
								formaction="uploadCapstoneProjectMarks">Upload</button>
						</div>
						
						<c:if test="${studentsListEligibleForPassFailSize gt 0}">
							<div class="col-md-4 ">
								<!--   -->
								<div class="form-group">
									<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="triggerTimeboundProjectPassFail">Run Pass Fail</button>
								</div>	
							</div>
						</c:if>
					</div>
				</div>
			</form:form>

		<c:if test="${successfullyProcessedPassFailSize gt 0}">
			<button class="accordion">Table 1</button>
			<div class="panel">
				<div class="column">
					<h5> <b>Students ready for pass-fail </b></h5>
					<div class="table-responsive">
						<table id ="dataTable" class="table table-striped table-hover tables">
							<thead>
								<tr>
									<th>SapId</th>
									<th>TEE Score</th>
									<th>IA Score</th>
									<td>Simulation Status</td>
									<td>Simulation Score</td>
	 								<td>Simulation Max Score</td>
	 								<td>CompXM Status</td>
					 				<td>CompXM Score</td>
					 				<td>CompXM Max Score</td>
									<th>Processed</th>
									<th>Fail Reason</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="passFailBean" items="${successfullyProcessedPassFailList}">
									<tr>
										<td><c:out value="${passFailBean.sapid}" /></td>
										<td><c:out value="${passFailBean.teeScore}" /></td>
										<td><c:out value="${passFailBean.iaScore}" /></td>
										<td><c:out value="${passFailBean.simulation_status}" /></td>
										<td><c:out value="${passFailBean.simulation_score}" /></td>
										<td><c:out value="${passFailBean.simulation_max_score}" /></td>
										<td><c:out value="${passFailBean.compXM_status}" /></td>
										<td><c:out value="${passFailBean.compXM_score}" /></td>
										<td><c:out value="${passFailBean.compXM_max_score}" /></td>
										<td><c:out value="${passFailBean.processed}" /></td>
										<td><c:out value="${passFailBean.failReason}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</c:if>
		
		<c:if test="${failedPassFailProcessingSize gt 0}">
			<button class="accordion">Table 2</button>
			<div class="panel">
				<div class="column">
					<h5><b> Students having insufficient data </b></h5>
					<div class="table-responsive">
						<table id ="dataTable1" class="table table-striped table-hover tables">
							<thead>
								<tr>
									<th>SapId</th>
									<th>TEE Score</th>
									<th>IA Score</th>
									<td>Simulation Status</td>
									<td>Simulation Score</td>
	 								<td>Simulation Max Score</td>
	 								<td>CompXM Status</td>
					 				<td>CompXM Score</td>
					 				<td>CompXM Max Score</td>
									<th>Processed</th>
									<th>Fail Reason</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="passFailBean" items="${failedPassFailProcessingList}">
									<tr>
										<td><c:out value="${passFailBean.sapid}" /></td>
										<td><c:out value="${passFailBean.teeScore}" /></td>
										<td><c:out value="${passFailBean.iaScore}" /></td>
										<td><c:out value="${passFailBean.simulation_status}" /></td>
										<td><c:out value="${passFailBean.simulation_score}" /></td>
										<td><c:out value="${passFailBean.simulation_max_score}" /></td>
										<td><c:out value="${passFailBean.compXM_status}" /></td>
										<td><c:out value="${passFailBean.compXM_score}" /></td>
										<td><c:out value="${passFailBean.compXM_max_score}" /></td>
										<td><c:out value="${passFailBean.processed}" /></td>
										<td><c:out value="${passFailBean.failReason}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</c:if>

			<c:if test="${capstone_project_marks_size > 0}">
				<h4 style="color: red;">&nbsp;Capstone Project Marks Report 
					<font size="2px">(${capstone_project_marks_size}
						Records Found) &nbsp; <a href="/exam/admin/downloadTimeboundProjectMarksReport" style="color: blue;">Download to Excel</a>
					</font>
				</h4>
				<div class="clearfix"></div>
			</c:if>

			<div class="table-responsive">
	 		<table id="dataTable" class="table table-striped ">
	 			<thead>
	 			<tr>
	 				<th><b>Sap ID</b></th>
	 				<th><b>Student Name</b></th>
	 				<th><b>Subject</b></th>
	 				<th><b>Batch Id</b></th>
	 				<th><b>Simulation Status</b></th>
	 				<th><b>Simulation Score</b></th>
	 				<th><b>Simulation Max Score</b></th>
	 				<th><b>CompXM Status</b></th>
	 				<th><b>CompXM Score</b></th>
	 				<th><b>CompXM Max Score</b></th>
	 			</tr>
	 			</thead>
	 			<tbody>
	 				<c:forEach var="project_marks" items="${capstone_project_marks}">
	 					<tr>
	 						<td><c:out value="${project_marks.sapid}"/></td>
	 						<td><c:out value="${project_marks.student_name}"/></td>
	 						<td><c:out value="${project_marks.subject}"/></td>
	 						<td><c:out value="${project_marks.batchId}"/></td>
	 						<td><c:out value="${project_marks.simulation_status}"/></td>
	 						<td><c:out value="${project_marks.simulation_score}"/></td>
	 						<td><c:out value="${project_marks.simulation_max_score}"/></td>
	 						<td><c:out value="${project_marks.compXM_status}"/></td>
	 						<td><c:out value="${project_marks.compXM_score}"/></td>
	 						<td><c:out value="${project_marks.compXM_max_score}"/></td>
	 					</tr>
	 				</c:forEach>
	 			</tbody>
	 		</table>
 		</div>
		</div><br/><br/>
	</section>

	<jsp:include page="footer.jsp" />
	<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
		<script>
		$(document).ready( function () {
			$('#dataTable').DataTable();
			
		} );
		</script>
	<script>
		$(document).ready(function(){

			$(document).on('click','#acadMonth,#acadYear',function(){
				var programName= $('.programType').val(); 
				if(programName != ""){
					$('.programType').val("");
				}
			});
			
			$(document).on('change','.programType',function(){
				var programType = $(this).val();
				var acadMonth=$('#acadMonth').val();
				var acadYear=$('#acadYear').val();
				if(programType == "" || acadMonth == "" || acadYear == ""){
					return false;
				}
				getBatchList(programType,acadMonth,acadYear);
			});
			
			$(document).on('change','.batches',function(){
				var batch = $(this).val();
				if(batch == ""){
					return false;
				}
				getSubjectsWithTimeboundList(batch);
			});
			
			$(document).on('change','#subject',function(){
				var subject = $(this).val();
				if(subject == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getAssessmentListByTimeBoundId?id=" + subject,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select assessment --</option>';
						for(let i=0;i < response.length;i++){
							
							if(response[i].id ==  $('#subject').val()){
							    optionsList = optionsList + '<option selected value="'+ response[i].id +'">'+ response[i].customAssessmentName +'</option>';
							 }else{
								 optionsList = optionsList + '<option value="'+ response[i].id +'">'+ response[i].customAssessmentName +'</option>';
							}
						}
						$('#assessment').html(optionsList);
						console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#assessment').html(optionsList);
			});
			
		});

		function getSubjectsWithTimeboundList(batch){
			let optionsList = '<option value="" disabled selected>loading</option>';
			$.ajax({
				url:"getSubjectListByBatchId?id=" + batch,
				method:"GET",
				success:function(response){
					optionsList = '<option disabled selected value="">-- select subject --</option>';
					for(let i=0;i < response.length;i++){
						 if((response[i].id !=0) &&  (response[i].id == '${ teeResultBean.timebound_id }')){
						    optionsList = optionsList + '<option selected value="'+ response[i].id +'">'+ response[i].subject +'</option>';
						 }else{
					   	 	optionsList = optionsList + '<option value="'+ response[i].id +'">'+ response[i].subject +'</option>';
						}
					}
					$('#subject').html(optionsList);
				},
				error:function(error){
					alert("Error while getting schedule data");
				}
			});
			$('#subject').html(optionsList);
		}

		function getBatchList(programType,acadMonth,acadYear){
	    	let optionsList = '<option value="" disabled selected>loading</option>';  
			$.ajax({
	    	 	url : '/exam/m/getActiveBatchList',
			    type : 'POST',
				contentType : "application/json",
				data : JSON.stringify({ 
					programType : programType,
					acadMonth : acadMonth,
					acadYear : acadYear
					}),
				dataType : "json",
		      success : function(response){
					let batchList = '<option disabled selected value="">-- select batch --</option>';
					for(let i=0;i < response.length;i++){
						if((response[i].id !=0) && (response[i].id == '${teeResultBean.batchId}')){
						    batchList = batchList + '<option selected value="' + response[i].id +'" >'+ response[i].name + '</option>';
						 }else{
							 batchList = batchList + '<option value="' + response[i].id +'" >'+ response[i].name + '</option>';
						}
					}
					$('.batches').html(batchList);
		      },error: function (result, status, err) {
			          alert("Unable to fetach Batch List !");
			          let optionsList = '<option value="" disabled selected>Unable to fetach Batch List !</option>';
			    	  $('.batches').html(optionsList);        
			  }
		    });
		  }

	</script>
	
	<script>
	addEventListener("load", function() {
		var programType = $('.programType').val();
		if (programType != null && typeof(programType) != "undefined" ){
			getBatchList(programType);
		}

		setTimeout(function(){
			getSubjects();
	  	},400);
	});

	function getSubjects(){
		var batchId = $('.batches').val();
		if (batchId != null && typeof(batchId) != "undefined" ){
			getSubjectsWithTimeboundList(batchId);
		}
	}
	</script>
	
   <script>
		var acc = document.getElementsByClassName("accordion");
		var i;
		
		for (i = 0; i < acc.length; i++) {
			acc[i].addEventListener("click", function() {
			    this.classList.toggle("active");
			    var panel = this.nextElementSibling;
			    if (panel.style.maxHeight) {
			      panel.style.maxHeight = null;
			    } else {
			      panel.style.maxHeight = panel.scrollHeight + "px";
			    } 
			});
		}
	</script>
</body>
</html>
