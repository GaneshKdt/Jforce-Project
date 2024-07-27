<!DOCTYPE html>

<%@page import="com.nmims.beans.MBATimeTableBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

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
			<fieldset class="row"><legend>Upload MBA-X Live Settings </legend></fieldset>
			<%@ include file="../../messages.jsp"%>

			<div class="panel-group" style="margin-top : 30px" id="accordion" role="tablist" aria-multiselectable="true">
				<div class="panel panel-default">
					<div class="panel-heading" role="tab" id="add">
						<h4 class="panel-title">
							<a role="button" data-toggle="collapse" data-parent="#accordion" href="#upload-form" aria-controls="upload-form">
								Upload Form
							</a>
						</h4>
					</div>
					<div id="upload-form" class="panel-collapse collapse" role="tabpanel" aria-labelledby="uploadForm">
						<div class="panel-body">
							<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data">
								<div>
									<div class="col-md-6 ">
										<div class="form-group">
											<form:label for="fileData" path="fileData">Select file</form:label>
											<form:input path="fileData" type="file" />
										</div>		
									</div>
									<div class="col-md-6 ">
										<div class="form-group">
											<form:label for="fileData" path="fileData">Select file</form:label>
											<form:select path="type" items="${ liveSettingTypes }" />
										</div>		
									</div>
									<div class="col-md-12 column">
										<b>Format of Upload: </b>
										<br/>
										PROGRAM_STRUCTURE | ACADS_YEAR | ACADS_MONTH | EXAM_YEAR | EXAM_MONTH | START_DATE | START_TIME | END_DATE | END_TIME
										 <br>
										<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/MBAX_LIVE_SETTINGS_SAMPLE.xlsx" target="_blank">Download a Sample Template</a> <br>
									</div>
								</div>
								<br>
								<div class="row">
									<div class="col-md-6 column">
										<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="uploadLiveSettings_MBAX">
											Upload
										</button>
									</div>
								</div>
							</form:form>
						</div>
					</div>
				</div>
			
				<div class="panel panel-default">
					<div class="panel-heading" role="tab" id="add">
						<h4 class="panel-title">
							<a role="button" data-toggle="collapse" data-parent="#accordion" href="#add-form" aria-controls="add-form">
								Add Form
							</a>
						</h4>
					</div>
					<div id="add-form" class="panel-collapse collapse" role="tabpanel" aria-labelledby="addForm">
						<div class="panel-body">
							<form:form modelAttribute="fileBean" method="post">
								<div>
									<form:hidden path="id" id="id" />
									<div class="col-md-18">
										<div class="col-md-6 column">
											<div class="form-group">
												<label for="programStructure">Program Structure</label>
												<form:select path="programStructure" id="programStructure" items="${ programStructureList }"/>
											</div>
										</div>
										<div class="col-md-6 column">
											<div class="form-group">
												<label for="examYear">Exam Year</label>
												<form:select path="examYear" id="examYear" items="${ examYearList }"/>
											</div>
										</div>
										
										<div class="col-md-6 column">
											<div class="form-group">
												<label for="examMonth">Exam Month</label>
												<form:select path="examMonth" id="examMonth"  items="${ examMonthList }"/>
											</div>
										</div>
										
										<div class="col-md-6 column">
											<div class="form-group">
												<label for="acadsYear">Acad Year</label>
												<form:select path="acadsYear" id="acadsYear"  items="${ acadYearList }"/>
											</div>
										</div>
										<div class="col-md-6 column">
											<div class="form-group">
												<label for="acadsMonth">Acad Month</label>
												<form:select path="acadsMonth" id="acadsMonth"  items="${ acadMonthList }"/>
											</div>
										</div>
										
										<div class="col-md-6 column">
											<div class="form-group">
												<label for="type">Type</label>
												<form:select path="type" items="${ liveSettingTypes }" />
											</div>
										</div>
									</div>
									
									<div class="col-md-18">
										<div class="col-md-6 column">
											<div class="form-group">
												<label for=startDateStr>Start Date</label>
												<form:input type="date" path="startDateStr" id="startDateStr"/>
											</div>
										</div>
										<div class="col-md-6 column">
											<div class="form-group">
												<label for="startTimeStr">Start Time</label>
												<form:input type="time" path="startTimeStr" id="startTimeStr"/>
											</div>
										</div>
										
										<div class="col-md-6 column">
											<div class="form-group">
												<label for=endDateStr>End Date</label>
												<form:input type="date" path="endDateStr" id="endDateStr"/>
											</div>
										</div>
										<div class="col-md-6 column">
											<div class="form-group">
												<label for="endTimeStr">End Time</label>
												<form:input type="time" path="endTimeStr" id="endTimeStr"/>
											</div>
										</div>
									</div>
								</div>
								<br>
								<div class="row">
									<div class="col-md-6 column">
										<a class="btn btn-large btn-danger" href="uploadLiveSettingsForm_MBAX">
											Cancel
										</a>
										<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addLiveSettings_MBAX">
											Update
										</button>
									</div>
								</div>
							</form:form>
						</div>
					</div>
				</div>
			</div>
			
			
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
												<th>Type</th>
												<th>Program Structure</th>
												<th>Acad Month</th>
												<th>Acad Year</th>
												<th>Exam Month</th>
												<th>Exam Year</th>
												<th>Start Date</th>
												<th>Start Time</th>
												<th>End Date</th>
												<th>End Time</th>
							 				</tr>
							 			</thead>
							 			<tbody>
							 				<c:forEach var="liveSetting" items="${successList}" varStatus="loop">
							 					<tr>
													<td><c:out value="${liveSetting.type}"/></td>
													<td><c:out value="${liveSetting.programStructure}"/></td>
													<td><c:out value="${liveSetting.acadsMonth}"/></td>
													<td><c:out value="${liveSetting.acadsYear}"/></td>
													<td><c:out value="${liveSetting.examMonth}"/></td>
													<td><c:out value="${liveSetting.examYear}"/></td>
													<td><c:out value="${liveSetting.startDateStr}"/></td>
													<td><c:out value="${liveSetting.startTimeStr}"/></td>
													<td><c:out value="${liveSetting.endDateStr}"/></td>
													<td><c:out value="${liveSetting.endTimeStr}"/></td>
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
												<th>Type</th>
												<th>Program Structure</th>
												<th>Acad Month</th>
												<th>Acad Year</th>
												<th>Exam Month</th>
												<th>Exam Year</th>
												<th>Start Date</th>
												<th>Start Time</th>
												<th>End Date</th>
												<th>End Time</th>
								 				<th>Error</th>
							 				</tr>
							 			</thead>
							 			<tbody>
							 				<c:forEach var="liveSetting" items="${errorList}" varStatus="loop">
							 					<tr>
													<td><c:out value="${liveSetting.type}"/></td>
													<td><c:out value="${liveSetting.programStructure}"/></td>
													<td><c:out value="${liveSetting.acadsMonth}"/></td>
													<td><c:out value="${liveSetting.acadsYear}"/></td>
													<td><c:out value="${liveSetting.examMonth}"/></td>
													<td><c:out value="${liveSetting.examYear}"/></td>
													<td><c:out value="${liveSetting.startDateStr}"/></td>
													<td><c:out value="${liveSetting.startTimeStr}"/></td>
													<td><c:out value="${liveSetting.endDateStr}"/></td>
													<td><c:out value="${liveSetting.endTimeStr}"/></td>
													<td><c:out value="${liveSetting.error}"/></td>
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
											<th>#</th>
											<th>Type</th>
											<th>Program Structure</th>
											<th>Acad Month</th>
											<th>Acad Year</th>
											<th>Exam Month</th>
											<th>Exam Year</th>
											<th>Start Date</th>
											<th>Start Time</th>
											<th>End Date</th>
											<th>End Time</th>
							 				<th>Action</th>
						 				</tr>
						 			</thead>
						 			<tbody>
						 				<c:forEach var="liveSetting" items="${liveSettingsList}" varStatus="loop">
						 					<tr>
						 						<td><c:out value="${loop.index + 1}"/></td>
												<td><c:out value="${liveSetting.type}"/></td>
												<td><c:out value="${liveSetting.programStructure}"/></td>
												<td><c:out value="${liveSetting.acadsMonth}"/></td>
												<td><c:out value="${liveSetting.acadsYear}"/></td>
												<td><c:out value="${liveSetting.examMonth}"/></td>
												<td><c:out value="${liveSetting.examYear}"/></td>
												<td><c:out value="${liveSetting.startDateStr}"/></td>
												<td><c:out value="${liveSetting.startTimeStr}"/></td>
												<td><c:out value="${liveSetting.endDateStr}"/></td>
												<td><c:out value="${liveSetting.endTimeStr}"/></td>
						 						<td>
						 							<a 
							 							href="updateLiveSettingForm_MBAX?Id=${liveSetting.id}" 
							 							style="color:#c72127;"
						 							>
						 								Update
					 								</a>
						 							<a 
							 							href="javascript:void(0)" 
							 							id="${liveSetting.id}" 
							 							class="deleteLiveSettingFromList_MBAX" 
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
			$(document).on('click',".deleteLiveSettingFromList_MBAX",function(){
				let prompt = confirm("Are you sure you want to delete?");
				if(!prompt){
					return false;
				}
				let id = $(this).attr("id");
				$.ajax({
					url:"deleteLiveSetting_MBAX?id=" + id,
					method:"POST",
					success:function(response){
						if(response.status == "success"){
							alert(response.message);
							window.location.href = "uploadLiveSettingsForm_MBAX";
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
