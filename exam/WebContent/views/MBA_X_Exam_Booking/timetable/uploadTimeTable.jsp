<!DOCTYPE html>

<%@page import="com.nmims.beans.MBATimeTableBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Upload MBA-X Time Table list" name="title" />
</jsp:include>

<%
	List<MBATimeTableBean> successTimeTableBeansList = (List<MBATimeTableBean>)request.getAttribute("successList"); 
	List<MBATimeTableBean> failTimeTableBeansList = (List<MBATimeTableBean>)request.getAttribute("errorList"); 

%>
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
	<%@ include file="../../header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<fieldset class="row"><legend>Upload MBA-X Time Table list </legend></fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data">
				<div class="panel-body">
					<div class="col-md-6 ">
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" />
						</div>		
					</div>
					<div class="col-md-12 column">
						<b>Format of Upload: </b>
						<br/>
						SUBJECT_NAME | TERM | EXAM_YEAR | EXAM_MONTH | EXAM_START_DATE_TIME | EXAM_END_DATE_TIME <br>
						<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/MBAX_TIME_TABLE_SAMPLE.xlsx" target="_blank">Download a Sample Template</a> <br>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="uploadTimeTable_MBAX">
							Upload
						</button>
						<c:if test = "${ showApproveButton }">
							<a href = "approveUploadTimeTable_MBAX" id="approve" class="btn btn-large btn-primary">
								Approve and Save
							</a>
						</c:if>
					</div>
				</div>
			</form:form>
			
			
			<div class="panel-group" style="margin-top : 30px" id="accordion" role="tablist" aria-multiselectable="true">
				<c:if test="${fn:length(successList) gt 0}">
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="successList">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#success-list" aria-controls="success-list">
									Time Tables for verification
								</a>
							</h4>
						</div>
						<div id="success-list" class="panel-collapse collapse" role="tabpanel" aria-labelledby="successList">
							<div class="panel-body">
								<fieldset class="row">
									<legend>
										<c:choose>
											<c:when test="${isApprove}">
												These rows will be uploaded.
										  	</c:when>
											<c:otherwise>
												These rows were successfully uploaded.
										  	</c:otherwise>
									  	</c:choose>
									</legend>
								</fieldset>
								<h5></h5>
								<div class="table-responsive">
							 		<table id="success-list-table" class="table table-striped" style="width: 100% !important;">
							 			<thead>
							 				<tr>
												<th>Subject</th>
												<th>Term</th>
												<th>Exam Year</th>
												<th>Exam Month</th>
												<th>Date</th>
												<th>Start Time</th>
												<th>End Time</th>
							 				</tr>
							 			</thead>
							 			<tbody>
							 				<c:forEach var="timeTable" items="${successList}">
							 					<tr>
													<td><c:out value="${timeTable.subjectName}"/></td>
													<td><c:out value="${timeTable.term}"/></td>
													<td><c:out value="${timeTable.examYear}"/></td>
													<td><c:out value="${timeTable.examMonth}"/></td>
													<td><c:out value="${timeTable.examDate}"/></td>
													<td><c:out value="${timeTable.examStartTime}"/></td>
													<td><c:out value="${timeTable.examEndTime}"/></td>
							 					</tr>
							 				</c:forEach>
							 			</tbody>
							 		</table>
							 	</div>
							</div>
						</div>
					</div>
				</c:if>
				<c:if test="${fn:length(errorList) gt 0}">
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="errorList">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#error-list" aria-controls="error-list">
									Time Tables with errors
								</a>
							</h4>
						</div>
						<div id="error-list" class="panel-collapse collapse" role="tabpanel" aria-labelledby="errorList">
							<div class="panel-body">
								<fieldset class="row">
									<legend>
										<c:choose>
											<c:when test="${isApprove}">
												These rows will not be uploaded.
										  	</c:when>
											<c:otherwise>
												These rows were not uploaded.
										  	</c:otherwise>
									  	</c:choose>
									</legend>
								</fieldset>
								<div class="table-responsive">
							 		<table id="error-list-table" class="table table-striped " style="width: 100% !important;">
							 			<thead>
							 				<tr>
												<th>Subject</th>
												<th>Term</th>
												<th>Exam Year</th>
												<th>Exam Month</th>
												<th>Date</th>
												<th>Start Time</th>
												<th>End Time</th>
												<th>Error</th>
							 				</tr>
							 			</thead>
							 			<tbody>
							 				<c:forEach var="timeTable" items="${errorList}">
							 					<tr>
													<td><c:out value="${timeTable.subjectName}"/></td>
													<td><c:out value="${timeTable.term}"/></td>
													<td><c:out value="${timeTable.examYear}"/></td>
													<td><c:out value="${timeTable.examMonth}"/></td>
													<td><c:out value="${timeTable.examDate}"/></td>
													<td><c:out value="${timeTable.examStartTime}"/></td>
													<td><c:out value="${timeTable.examEndTime}"/></td>
													<td><c:out value="${timeTable.error}"/></td>
							 					</tr>
							 				</c:forEach>
							 			</tbody>
							 		</table>
							 	</div>
							</div>
						</div>
					</div>
				</c:if>
				<div class="panel panel-default">
					<div class="panel-heading" role="tab" id="timeTableList">
						<h4 class="panel-title">
							<a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#time-table-list" aria-expanded="true" aria-controls="time-table-list">
								Time Tables List
							</a>
						</h4>
					</div>
					<div id="time-table-list" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="timeTableList">
						<div class="panel-body">
							<div class="table-responsive">
						 		<table id="time-table-list-table" class="table table-striped " style="width: 100% !important;">
						 			<thead>
						 				<tr>
											<th>Subject</th>
											<th>Term</th>
											<th>Exam Year</th>
											<th>Exam Month</th>
											<th>Date</th>
											<th>Start Time</th>
											<th>End Time</th>
							 				<th>Action</th>
						 				</tr>
						 			</thead>
						 			<tbody>
						 				<c:forEach var="timeTable" items="${timeTableList}">
						 					<tr>
												<td><c:out value="${timeTable.subjectName}"/></td>
												<td><c:out value="${timeTable.term}"/></td>
												<td><c:out value="${timeTable.examYear}"/></td>
												<td><c:out value="${timeTable.examMonth}"/></td>
												<td><c:out value="${timeTable.examDate}"/></td>
												<td><c:out value="${timeTable.examStartTime}"/></td>
												<td><c:out value="${timeTable.examEndTime}"/></td>
						 						<td>
						 							<a 
							 							href="updateTimeTableForm_MBAX?timeTableId=${timeTable.timeTableId}" 
							 							style="color:#c72127;"
						 							>
						 								Edit
					 								</a>
						 							<a 
							 							href="javascript:void(0)" 
							 							timeTableId="${timeTable.timeTableId}" 
							 							class="deleteTimeTableFromList_MBAX" 
							 							style="color:#c72127;"
						 							>
						 								Delete
				 									</a>
						 						</td>
						 					</tr>
						 				</c:forEach>
						 			</tbody>
						 		</table>
					 		</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<br/><br/>
	</section>

	<jsp:include page="../../footer.jsp" />
	<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	<script>
		$(document).ready( function () {
			$('#success-list-table').DataTable({
			    "autoWidth": false
			});
			$('#time-table-list-table').DataTable({
			    "autoWidth": false
			});
			$('#error-list-table').DataTable({
			    "autoWidth": false
			});
			$(document).on('click',".deleteTimeTableFromList_MBAX",function(){
				let prompt = confirm("Are you sure you want to delete?");
				if(!prompt){
					return false;
				}
				let timeTableId = $(this).attr("timeTableId");
				$.ajax({
					url:"deleteTimeTable_MBAX?timeTableId=" + timeTableId,
					method:"POST",
					success:function(response){
						if(response.status == "success"){
							alert(response.message);
							window.location.href = "uploadTimeTableForm_MBAX";
						}else{
							alert(response.message);
						}
					},
					error:function(error){
						alert("Failed to delete record");
						console.log(error);
					}
				});
			})
			
		});
		
	</script>
</body>
</html>
