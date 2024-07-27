<!DOCTYPE html>

<%@page import="com.nmims.beans.ProjectTitle"%>
<%@page import="com.nmims.beans.MBACentersBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Add Project Titles" name="title" />
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
			<fieldset class="row"><legend>Project Title list </legend></fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="projectTitle" method="post"	enctype="multipart/form-data">
				<div class="panel-body">
					<div class="col-md-6">
						<div class="form-group">
							<label>Subject</label>
							<form:select id="subject" path="subject" type="text" placeholder="Subject" class="form-control" required="required" itemValue="${filesSet.examMonth}">
								<form:option value="">Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
						</div>
					</div>
					
					<div class="col-md-6">
						<div class="form-group">
							<label >Consumer Type</label>
							<select data-id="consumerTypeDataId" id="consumerTypeId" name="consumerTypeId"  class="selectConsumerType form-control"  required="required">
								<option disabled selected value="">Select Consumer Type</option>
								<c:forEach var="consumerType" items="${consumerType}">
									<c:choose>
										<c:when test="${consumerType.id == searchBean.consumerTypeId}">
											<option selected value="<c:out value="${consumerType.id}"/>">
							                  <c:out value="${consumerType.name}"/>
							                </option>
										</c:when>
										<c:otherwise>
											<option value="<c:out value="${consumerType.id}"/>">
							                  <c:out value="${consumerType.name}"/>
							                </option>
										</c:otherwise>
									</c:choose>
	
					            </c:forEach>
							</select>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label >Program Structure</label>
							<select id="programStructureId" name="programStructureId"  class="selectProgramStructure form-control"  required="required">
								<option disabled selected value="">Select Program Structure</option>
							</select>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label >Program</label>
							<select id="programId" name="programId"  class="selectProgram form-control"  required="required">
								<option disabled selected value="">Select Program</option>
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
						<br/> Title <br>
						<a href="resources_2015/templates/Project_Titles_Template.xlsx" target="_blank">Download a Sample Template</a> <br>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addProjectTitles">
							Upload
						</button>
					</div>
				</div>
			</form:form>
			
			<div class="panel-group" style="margin-top : 30px" id="accordion" role="tablist" aria-multiselectable="true">
				<c:if test="${ successList != null && successList.size() > 0 }">

					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="successList">
							<h4 class="panel-title">
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
												<th>Title</th>
												<th>Program</th>
												<th>ProgramStructure</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="title" items="${successList}">
												<tr>
													<td><c:out value="${title.subject}"/></td>
													<td><c:out value="${title.title}"/></td>
													<td><c:out value="${title.programCode}"/></td>
													<td><c:out value="${title.programStructure}"/></td>
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
							<h4 class="panel-title">
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
												<th>Title</th>
												<th>Program</th>
												<th>Program Structure</th>
												<th>Error </th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="title" items="${errorList}">
												<tr>
													<td><c:out value="${title.subject}"/></td>
													<td><c:out value="${title.title}"/></td>
													<td><c:out value="${title.programCode}"/></td>
													<td><c:out value="${title.programStructure}"/></td>
													<td><c:out value="${title.error}"/></td>
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
						<h4 class="panel-title">
							<a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#centers-list" aria-expanded="true" aria-controls="centers-list">
								Title List
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
											<th>Title</th>
											<th>Program</th>
											<th>Program Structure</th>
											<th>Active</th>
											<th>Edit</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="title" items="${titleList}">
											<tr>
												<td><c:out value="${title.subject}"/></td>
												<td><c:out value="${title.title}"/></td>
												<td><c:out value="${title.programCode}"/></td>
												<td><c:out value="${title.programStructure}"/></td>
												<td>
													<a 
														href="javascript:void(0)" 
														titleId="${title.id}" 
														active="${title.active }"
														class="toggleActiveProjectTitle" 
														style="color:#c72127;"
													>
														${ title.active }
													</a>
												</td>
												<td>
													<a 
														href="updateProjectTitleForm?id=${title.id}" 
														style="color:#c72127;"
													>
														Edit
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
			$(document).on('click',".toggleActiveProjectTitle",function(){
				let prompt = confirm("Are you sure you want to toggle this?");
				if(!prompt){
					return false;
				}
				let titleId = $(this).attr("titleId");
				let active = $(this).attr("active");
				var activeToggledValue = '';
				if(active == 'Y') {
					activeToggledValue='N'
				} else if (active == 'N') {
					activeToggledValue='Y'
				} else {
					alert("Error invalid active flag found!");
					return
				}
				$.ajax({
					url:"toggleProjectTitleActive?id=" + titleId + "&active="+activeToggledValue,
					method:"POST",
					success:function(response){
						if(response.status == "success"){
							alert(response.message);
							window.location.href = "addProjectTitlesForm";
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
