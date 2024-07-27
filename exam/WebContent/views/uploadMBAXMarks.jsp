<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload MBAX Marks List" name="title" />
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
thead tr td{
	font-weight:bold;
}
.modal-dialog{
    overflow-y: initial !important
}
.modal-body{
    max-height: calc(100vh - 300px);
    overflow-y: auto;
}
</style>
</head>
<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload MBAX Marks List</legend></div>
			
				<%@ include file="messages.jsp"%>
				<%if("true".equals( (String)request.getAttribute("error_list_flag"))) { %>
					<table style="color: #a94442; background-color: #f2dede; border-color: #ebccd1;" class="table table-border table-striped ">
						<thead>
							<td>Email</td>
							<td>Name</td>
							<td>Score</td>
							<td>Max Score</td>
						</thead>
						<tbody>
							<c:forEach var="error_list" items="${error_lists}">
								<tr>
									<td>${error_list.email}</td>
									<td>${error_list.student_name}</td>
									<td>${error_list.score}</td>
									<td>${error_list.max_score}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				<%} %>
				<%@ include file="uploadExcelErrorMessages.jsp"%>
												
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadExamFeeExempt">
					
					
					
					
					<div class="panel-body">
					<div class="row">
					<div class="col-md-4">
							<div class="form-group">
							  <label for="sel1">Select Batch:</label>
							  <select class="form-control" id="batches" required>
							  	<option disabled selected value="">-- select batch --</option>
							    <%-- <c:forEach var="batch" items="${batches}">
			 						<option value="<c:out value="${batch.id}"/>"><c:out value="${batch.name}"/></option>
			 					</c:forEach> --%>
			 					 <c:forEach var="batch" items="${batches}">
								 						<option value="<c:out value="${batch.value}"/>">${batch.key}</option>
								 					</c:forEach>
							  </select>
							</div>
						</div>
						
						<div class="col-md-4">
							<div class="form-group">
							  <label for="sel1">Select Subject:</label>
							  <select name="timebound_id" class="form-control mbax_subject" id="subject" required>
							    <option disabled selected value="">-- select subject --</option>
							  </select>
							</div>
						</div>
						<input type="hidden" id="mbax_prgm_sem_subj_id" name="prgm_sem_subj_id" />
						
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
						
						
						
						
					<div class="col-md-4 ">
						<!--   -->
					<div class="form-group">
						<form:label for="fileData" path="fileData">Select file</form:label>
						<form:input id="mbax_marks_file" path="fileData" type="file" required="required"/>
					</div>	
					
						
					</div>
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			Name | Email | Score | Max score | Report Link  <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/mbaxMarks_Template.xlsx" target="_blank">Download a Sample Template</a> <br>
			</div>
			
			</div>
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button data-toggle="modal" data-target="#myModal" type="button" id="preview_btn" name="submit" class="btn btn-large btn-danger"
						formaction="uploadMBAXMarks">Preview ExcelSheet Marks To Be Upload</button>
					<button  name="submit" class="btn btn-large btn-primary"
						formaction="uploadMBAXMarks">Upload</button>
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
		 				<td><b>Score</b></td>
		 				<td><b>Max Score</b></td>
		 			</thead>
		 			<tbody>
		 				<c:forEach var="mark" items="${mbax_marks}">
		 					<tr>
		 						<td><c:out value="${mark.sapid}"/></td>
		 						<td><c:out value="${mark.student_name}"/></td>
		 						<td><c:out value="${mark.subject}"/></td>
		 						<td><c:out value="${mark.score}"/></td>
		 						<td><c:out value="${mark.max_score}"/></td>
		 					</tr>
		 				</c:forEach>
		 			</tbody>
		 		</table>
		 		</div>
		</div><br/><br/>
	</section>
	
	<div id="myModal" class="modal fade" role="dialog">
	  <div class="modal-dialog modal-lg">
	
	    <!-- Modal content-->
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal">&times;</button>
	        <h4 class="modal-title">MBAX Marks Excel Preview</h4>
	      </div>
	      <div class="modal-body" id="model_body">
	     
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	      </div>
	    </div>
	
	  </div>
	</div>

	<jsp:include page="footer.jsp" />
	<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	    <script>
	    $(document).ready( function () {
	        $('#dataTable').DataTable();
	        
	    } );
	    </script>
	    
	    <script>
		$(document).ready(function(){
			
			$('#preview_btn').click(function(e){
				var dataResult = '<center> <h4>Loading Preview</h4> </center>';
		        $('#model_body').html(dataResult);
				var jform = new FormData();
				jform.append('fileData',$('#mbax_marks_file')[0].files[0]);
				$.ajax({
				    url: 'previewMBAXMarks',
				    type: 'POST',
				    data: jform,
				    dataType: 'json',
				    mimeType: 'multipart/form-data', // this too
				    contentType: false,
				    cache: false,
				    processData: false,
				    success: function(data, status, jqXHR){
				
				        console.log(data);
				        if(data.status == "success"){
					        var dataResult = '<div class="row"> <div class="col-sm-6"> <h4>Total Rows: <b>'+ data.totalRows +'</b></h4> </div> <div class="col-sm-6"> <h4>Total Columns: <b>'+ data.totalColumns +'</b></h4> </div> </div><br/> <div class="table-responsive"> <table class="table table-bordered"> <thead style="background-color:#c72027;color:white"> <tr> <td>Name</td> <td>Email</td> <td>Score</td> <td>Max Score</td> <td>Report link</td> </tr> </thead> <tbody> ';
					        var dataLength = data.mbaxMarksBean.length;
					        /* if(dataLength > 6){
					        	dataLength = 6;
					        } */
					        for(var i=0; i < dataLength;i++){
					        	var marksBean = data.mbaxMarksBean[i];
					        	dataResult = dataResult + '<tr><td>'+ marksBean.student_name +'</td><td>'+ marksBean.email +'</td><td>'+ marksBean.score +'</td><td>'+ marksBean.max_score +'</td><td>'+ marksBean.report_link +'</td></tr>';
					        }
					        //var dataResultEnd = "</tbody> </table> </div> <div> <p>Note: Preview Show only first six records</p> </div>";
					        $('#model_body').html(dataResult);
				        }
				        else{
				        	var dataResult = '<center> <h4 style="color:red">'+ data.message +'</h4> </center>';
					        $('#model_body').html(dataResult);
				        }
				        

				    },
				    error: function(jqXHR,status,error){
				        // Hopefully we should never reach here
				       
				        console.log(jqXHR);
				        console.log(status);
				        console.log(error);
				        var dataResult = '<center> <h4 style="color:red">Something went wrong, we are working on it</h4> </center>';
				        $('#model_body').html(dataResult);
				    }
				});
			});
			
			$(document).on('click','.process_btn',function(){
				
			});
			
			$(document).on('change','.mbax_subject',function(){
				$('#mbax_prgm_sem_subj_id').val($(this).find(':selected').attr('data-prgm_sem_subj_id'));
			});
			
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
					url:"getMBAXScheduleListByAssessment?id=" + assessment+"&timeid=" + subject,
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
					url:"getMBAXSubjectListByBatchId?id=" + batch,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select subject --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option data-prgm_sem_subj_id="'+ response[i].prgm_sem_subj_id +'" value="'+ response[i].id +'">'+ response[i].subject +'</option>';
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
					url:"getMBAXAssessmentListByTimeBoundId?id=" + subject,
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
