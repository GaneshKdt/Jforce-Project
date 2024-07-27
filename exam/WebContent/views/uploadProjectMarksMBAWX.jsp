<!DOCTYPE html>
<!--[if lt IE 7]>		<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>		 <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>		 <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">
<!--<![endif]-->

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

			<div class="row"><legend>Upload TEE Marks List</legend></div>
			
			<%@ include file="messages.jsp"%>
			<%@ include file="uploadExcelErrorMessages.jsp"%>
												
			<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadProjectMarksMBAWX">
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
								<select name="acadYear" id="acadYear" class="form-control" >
									<option value="">-- Acad Year --</option>
									<c:forEach items="${acadYear}" var="year">
									<option value="${year}">${year}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						
						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Program Type:</label> <select
									name="programType" class="form-control programType">
									<option value="">-- select Program Type --</option>
									<option value="MBA - WX">MBA - WX</option>
									<option value="M.Sc. (AI)">M.Sc. (AI)</option>
									<option value="M.Sc. (AI & ML Ops)">M.Sc. (AI & ML Ops)</option>
								</select>
							</div>
						</div>

						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Batch:</label> <select name="batchId"
									class="form-control batches">
									<option disabled selected value="">-- select batch --</option>

								</select>
							</div>
						</div>

						<div class="col-md-3">
							<div class="form-group">
								<label for="sel1">Select Subject:</label>
								<select name="timebound_id" class="form-control" id="subject">
									<option disabled selected value="">-- select subject --</option>
								</select>
							</div>
						</div>
						<div class="col-md-6 ">
							<div class="form-group">
								<form:label for="fileData" path="fileData">Select file</form:label>
								<form:input path="fileData" type="file" />
							</div>		
						</div>
				
						<div class="col-md-12 column">
							<b>Format of Upload: </b><br>
							Sap ID | Score | Max score <br>
							<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/TEE_MARKS_SAMPLE.xlsx" target="_blank">Download a Sample Template</a> <br>
						</div>
					
					
					</div>
					<br>
					<div class="row">
						<div class="col-md-6 column">
							<button id="submit" name="submit" class="btn btn-large btn-primary"
								formaction="uploadProjectMarksMBAWX">Upload</button>
						</div>
						
						<c:if test="${studentsListEligibleForPassFailSize gt 0}">
							<div class="col-md-4 ">
								<!--   -->
								<div class="form-group">
									<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="embaPassFailTriggerForProject">Run Pass Fail</button>
								</div>	
							</div>
						</c:if>
					</div>
				</div>
			</form:form>
		
		<br/><br/>

		<c:if test="${finalListforPassFailSize gt 0}">
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
									<th>Total</th>
									<th>Is Pass</th>
									<th>Status</th>
									<th>Processed</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="passFailBean" items="${finalListforPassFail}">
									<tr>
										<td><c:out value="${passFailBean.sapid}" /></td>
										<td><c:out value="${passFailBean.teeScore}" /></td>
										<td><c:out value="${passFailBean.iaScore}" /></td>
										<td><c:out value="${passFailBean.teeScore + passFailBean.iaScore}" /></td>
										<td><c:out value="${passFailBean.isPass}" /></td>
										<td><c:out value="${passFailBean.status}" /></td>
										<td><c:out value="${passFailBean.processed}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</c:if>
		
		<c:if test="${unsuccessfulPassFailSize gt 0}">
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
									<th>Status</th>
									<th>Processed</th>
									<th>Fail Reason</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="passFailBean" items="${unsuccessfulPassFail}">
									<tr>
										<td><c:out value="${passFailBean.sapid}" /></td>
										<td><c:out value="${passFailBean.teeScore}" /></td>
										<td><c:out value="${passFailBean.iaScore}" /></td>
										<td><c:out value="${passFailBean.status}" /></td>
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
		<div class="table-responsive">
	 		<table id="dataTable" class="table table-striped ">
	 			<thead>
	 				<td><b>Sapid</b></td>
	 				<td><b>Student Name</b></td>
	 				<td><b>Subject</b></td>
	 				<td><b>Batch Id</b></td>
	 				<td><b>Score</b></td>
	 				<td><b>Max Score</b></td>
	 			</thead>
	 			<tbody>
	 				<c:forEach var="tee_mark" items="${tee_marks}">
	 					<tr>
	 						<td><c:out value="${tee_mark.sapid}"/></td>
	 						<td><c:out value="${tee_mark.student_name}"/></td>
	 						<td><c:out value="${tee_mark.subject}"/></td>
	 						<td><c:out value="${tee_mark.batchId}"/></td>
	 						<td><c:out value="${tee_mark.score}"/></td>
	 						<td><c:out value="${tee_mark.max_score}"/></td>
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
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getSubjectListByBatchId?id=" + batch,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select subject --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].id +'">'+ response[i].subject +'</option>';
						}
						$('#subject').html(optionsList);
						console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#subject').html(optionsList);
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
							optionsList = optionsList + '<option value="'+ response[i].id +'">'+ response[i].customAssessmentName +'</option>';
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

		function getBatchList(programType,acadMonth,acadYear){
	    	  let optionsList = '<option value="" disabled selected>loading</option>';
	    	  $('.batchList').html(optionsList);
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
						batchList = batchList + '<option value="' + response[i].id +'" >'+ response[i].name + '</option>';
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
