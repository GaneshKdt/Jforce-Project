<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
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
</head>
<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload TEE Marks List</legend></div>
			
				<%@ include file="messages.jsp"%>
				<%@ include file="uploadExcelErrorMessages.jsp"%>
												
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadExamFeeExempt">
					<div class="panel-body">
					<div class="row">
						<div class="col-md-4">
							<div class="form-group">
							  <label for="sel1">Select Batch:</label>
							  <select class="form-control" id="batches" required>
							  	<option disabled selected value="">-- select batch --</option>
							    <c:forEach var="batch" items="${batches}">
			 						<option value="<c:out value="${batch}"/>">Batch<c:out value="${batch}"/></option>
			 					</c:forEach>
							  </select>
							</div>
						</div>
						
						<div class="col-md-4">
							<div class="form-group">
							  <label for="sel1">Select Subject:</label>
							  <select name="timebound_id" class="form-control" id="subject" required>
							    <option disabled selected value="">-- select subject --</option>
							  </select>
							</div>
						</div>
						
						<div class="col-md-4">
							<div class="form-group">
							  <label for="sel1">Select Assessment:</label>
							  <select class="form-control" id="assessment" required>
							  	<option disabled selected value="">-- select assessment --</option>
							  </select>
							</div>
						</div>
						
						<div class="col-md-4">
							<div class="form-group">
							  <label for="sel1">Select Schedule:</label>
							  <select name="schedule_id" class="form-control" id="schedule" required>
							    <option disabled selected value="">-- select schedule --</option>
							  </select>
							</div>
						</div>
					</div>
					
					<div class="col-md-6 ">
						<!--   -->
					<div class="form-group">
						<form:label for="fileData" path="fileData">Select file</form:label>
						<form:input path="fileData" type="file" />
					</div>		
			</div>
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			First Name | Last Name | Sap ID | Score | Max score <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/TEE_MARKS_SAMPLE.xlsx" target="_blank">Download a Sample Template</a> <br>
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadTEEMarks">Upload</button>
				</div>

				
			</div>
			</form:form>
		
		<br/><br/>
		<div class="table-responsive">
		 		<table id="dataTable" class="table table-striped ">
		 			<thead>
		 				<td><b>Sapid</b></td>
		 				<td><b>Student Name</b></td>
		 				<td><b>Subject</b></td>
		 				<td><b>Batch</b></td>
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
			$(document).on('change','#assessment',function(){
				var assessment = $(this).val();
				if(assessment == ""){
					return false;
				}
				var subject = $('#subject').val();
				if(subject == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getScheduleListByAssessment?id=" + assessment+"&timeid=" + subject,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select schedule --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].schedule_id +'">'+ response[i].schedule_name +'</option>';
						}
						$('#schedule').html(optionsList);
						console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#schedule').html(optionsList);
			});
			
			
			$(document).on('change','#batches',function(){
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
	</script>
	
</body>
</html>
