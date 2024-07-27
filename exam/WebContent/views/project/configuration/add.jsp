<!DOCTYPE html>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Project Configuration" name="title"/>
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
	<%@ include file="../../header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<fieldset class="row"><legend>Project Configurations</legend></fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="projectConfiguration" method="post"	enctype="multipart/form-data">
				<div class="panel-body">
				
					<div class="col-md-6">
						<div class="form-group">
							<label >Exam Month</label>
							<form:select id="examMonth" path="examMonth" type="text" placeholder="Exam Month" class="form-control" required="required" itemValue="${filesSet.examMonth}">
								<form:option value="">Select Month</form:option>
								<form:option value="Jan">Jan</form:option>
								<form:option value="Feb">Feb</form:option>
								<form:option value="Mar">Mar</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="May">May</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Jul">Jul</form:option>
								<form:option value="Aug">Aug</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Oct">Oct</form:option>
								<form:option value="Nov">Nov</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label>Exam Year</label>
							<form:select id="examYear" path="examYear" type="text" placeholder="Exam Month" class="form-control" required="required" itemValue="${filesSet.examMonth}">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
					</div>
					
					<div class="col-md-6">
						<div class="form-group">
							<label >Consumer Type</label>
							<select data-id="consumerTypeDataId" id="consumerType" name="consumerType"  class="selectConsumerType form-control"  required="required">
								<option disabled selected value="">Select Consumer Type</option>
								<c:forEach var="consumerType" items="${consumerType}">
									<c:choose>
										<c:when test="${consumerType.id == searchBean.consumerTypeId}">
											<option selected value="<c:out value="${consumerType.name}"/>">
							                  <c:out value="${consumerType.name}"/>
							                </option>
										</c:when>
										<c:otherwise>
											<option value="<c:out value="${consumerType.name}"/>">
							                  <c:out value="${consumerType.name}"/>
							                </option>
										</c:otherwise>
									</c:choose>
	
					            </c:forEach>
							</select>
						</div>
					</div>
					<div class="col-md-12 ">
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" />
						</div>		
					</div>
					<div class="col-md-12 column">
						<b>Format of Upload: </b>
						<br/> PROGRAM | PROGRAM_STRUCTURE | SUBJECT | HAS_TITLE | HAS_SOP | HAS_VIVA | HAS_SYNOPSIS | HAS_SUBMISSION <br>
						<!-- <a href="resources_2015/templates/Project_Configuration_Template.xlsx" target="_blank">Download a Sample Template</a> <br> -->
						<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Project_Configuration_Template.xlsx" target="_blank">Download a Sample Template</a> <br>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="projectConfiguration">
							Upload
						</button>
					</div>
				</div>
			</form:form>
			
			<div class="panel-group" style="margin-top : 30px" id="accordion" role="tablist" aria-multiselectable="true">
				<c:if test="${ successList != null && successList.size() > 0 }">

					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="successList">
							<h4 class="panel-configuration">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#success-list" aria-controls="success-list">
									Data Uploaded
								</a>
							</h4>
						</div>
						<div id="success-list" class="panel-collapse collapse" role="tabpanel" aria-labelledby="successList">
							<div class="panel-body">
								<h5></h5>
								<div class="table-responsive">
									<table id="success-list-table" class="table table-striped" style="width: 100% !important;">
										<thead>
											<tr>
												<th>Subject</th>
												<th>Exam Year</th>
												<th>Exam Month</th>
												<th>Program</th>
												<th>ProgramStructure</th>
												<th>Has Title</th>
												<th>Has SOP</th>
												<th>Has Viva</th>
												<th>Has Synopsis</th>
												<th>Has Submission</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="configuration" items="${successList}">
												<tr>
													<td><c:out value="${configuration.subject}"/></td>
													<td><c:out value="${configuration.examYear}"/></td>
													<td><c:out value="${configuration.examMonth}"/></td>
													<td><c:out value="${configuration.programCode}"/></td>
													<td><c:out value="${configuration.programStructure}"/></td>
													<td><c:out value="${configuration.hasTitle}"/></td>
													<td><c:out value="${configuration.hasSOP}"/></td>
													<td><c:out value="${configuration.hasViva}"/></td>
													<td><c:out value="${configuration.hasSynopsis}"/></td>
													<td><c:out value="${configuration.hasSubmission}"/></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</c:if>
				<c:if test="${ errorList != null && errorList.size() > 0 }">
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="errorList">
							<h4 class="panel-configuration">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#error-list" aria-controls="error-list">
									Titles with errors
								</a>
							</h4>
						</div>
						<div id="error-list" class="panel-collapse collapse" role="tabpanel" aria-labelledby="errorList">
							<div class="panel-body">
								<fieldset class="row">
									<legend>
										<c:choose>
											<c:when test="${isApprove}">
												These centers will not be uploaded.
											</c:when>
											<c:otherwise>
												These centers were not uploaded.
											</c:otherwise>
										</c:choose>
									</legend>
								</fieldset>
								<div class="table-responsive">
									<table id="error-list-table" class="table table-striped " style="width: 100% !important;">
										<thead>
											<tr>
												<th>Subject</th>
												<th>Exam Year</th>
												<th>Exam Month</th>
												<th>Program</th>
												<th>Program Structure</th>
												<th>Has Title</th>
												<th>Has SOP</th>
												<th>Has Viva</th>
												<th>Has Synopsis</th>
												<th>Has Submission</th>
												<th>Error </th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="configuration" items="${errorList}">
												<tr>
													<td><c:out value="${configuration.subject}"/></td>
													<td><c:out value="${configuration.examYear}"/></td>
													<td><c:out value="${configuration.examMonth}"/></td>
													<td><c:out value="${configuration.programCode}"/></td>
													<td><c:out value="${configuration.programStructure}"/></td>
													<td><c:out value="${configuration.hasTitle}"/></td>
													<td><c:out value="${configuration.hasSOP}"/></td>
													<td><c:out value="${configuration.hasViva}"/></td>
													<td><c:out value="${configuration.hasSynopsis}"/></td>
													<td><c:out value="${configuration.hasSubmission}"/></td>
													<td><c:out value="${configuration.error}"/></td>
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
					<div class="panel-heading" role="tab" id="centersList">
						<h4 class="panel-configuration">
							<a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#centers-list" aria-expanded="true" aria-controls="centers-list">
								Configuration List
							</a>
						</h4>
					</div>
					<div id="centers-list" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="centersList">
						<div class="panel-body">
							<div class="table-responsive">
								<table id="centers-list-table" class="table table-striped " style="width: 100% !important;">
									<thead>
										<tr>
											<th>Subject</th>
											<th>Exam Year</th>
											<th>Exam Month</th>
											<th>Program</th>
											<th>Program Structure</th>
											<th>Has Title</th>
											<th>Has SOP</th>
											<th>Has Viva</th>
											<th>Has Synopsis</th>
											<th>Has Submission</th>
											<th>Active</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="configuration" items="${configurationList}">
											<tr>
												<td><c:out value="${configuration.subject}"/></td>
												<td><c:out value="${configuration.examYear}"/></td>
												<td><c:out value="${configuration.examMonth}"/></td>
												<td><c:out value="${configuration.programCode}"/></td>
												<td><c:out value="${configuration.programStructure}"/></td>
												<td><c:out value="${configuration.hasTitle}"/></td>
												<td><c:out value="${configuration.hasSOP}"/></td>
												<td><c:out value="${configuration.hasViva}"/></td>
												<td><c:out value="${configuration.hasSynopsis}"/></td>
												<td><c:out value="${configuration.hasSubmission}"/></td>
												<td>
													<a 
														href="updateProjectConfigurationForm?id=${configuration.id}" 
														style="color:#c72127;"
													>
														Edit
													</a>
													<a 
														href="javascript:void(0)" 
														configurationId="${configuration.id}" 
														class="deleteProjectConfiguration" 
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
			$('#centers-list-table').DataTable({
				"autoWidth": false
			});
			$('#error-list-table').DataTable({
				"autoWidth": false
			});
			$(document).on('click',".deleteProjectConfiguration",function(){
				let prompt = confirm("Are you sure you want to delete?");
				if(!prompt){
					return false;
				}
				let configurationId = $(this).attr("configurationId");
				$.ajax({
					url:"deleteProjectConfiguration?id=" + configurationId,
					method:"POST",
					success:function(response){
						if(response.status == "success"){
							alert(response.message);
							window.location.href = "projectConfigurationForm";
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
		} );
		
	</script>
	
	<script>

	 var consumerTypeId = '${ searchBean.consumerTypeId }';
	 var programStructureId = '${ searchBean.programStructureId }';
	 var programId = '${ searchBean.programId }';
	</script>
	<%@ include file="../../../views/common/consumerProgramStructure.jsp" %>
</body>
</html>
