<!DOCTYPE html>

<%@page import="com.nmims.beans.MBACentersBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Upload MBA-WX Center list" name="title" />
</jsp:include>

<%
	List<MBACentersBean> successCentersBeansList = (List<MBACentersBean>)request.getAttribute("successCentersBeansList"); 
	List<MBACentersBean> failCentersBeansList = (List<MBACentersBean>)request.getAttribute("failCentersBeansList"); 

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
			<fieldset class="row"><legend>Upload MBA-WX Center list </legend></fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="fileBean" method="post"	enctype="multipart/form-data">
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
						Name | City | State | Capacity | Address | GoogleMapUrl | Locality | Active<br>
						<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/sampleCenterMBAWX.xlsx" target="_blank">Download a Sample Template</a> <br>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="uploadCenters_MBAWX">
							Upload
						</button>
						<a href = "autoGenerateSlots_MBAWX" class="btn btn-large btn-primary">
							Generate Slots
						</a>
						<c:if test = "${ showApproveButton }">
							<a href = "approveUploadCenters_MBAWX" id="approve" name="approve" class="btn btn-large btn-primary">
								Approve and Save Centers List
							</a>
						</c:if>
					</div>
				</div>
			</form:form>
			
			<div class="panel-group" style="margin-top : 30px" id="accordion" role="tablist" aria-multiselectable="true">
				<%
					if(successCentersBeansList != null && successCentersBeansList.size() > 0){
				%>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="successList">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#success-list" aria-controls="success-list">
									Centers for verification
								</a>
							</h4>
						</div>
						<div id="success-list" class="panel-collapse collapse" role="tabpanel" aria-labelledby="successList">
							<div class="panel-body">
								<fieldset class="row">
									<legend>
										<c:choose>
											<c:when test="${isApprove}">
												These centers will be uploaded.
											</c:when>
											<c:otherwise>
												These centers were successfully uploaded.
											</c:otherwise>
										</c:choose>
									</legend>
								</fieldset>
								<h5></h5>
								<div class="table-responsive">
									<table id="success-list-table" class="table table-striped" style="width: 100% !important;">
										<thead>
											<tr>
												<th>Name</th>
												<th>City</th>
												<th>State</th>
												<th>Capacity</th>
												<th>Address</th>
												<th>Google Map URL</th>
												<th>Locality</th>
												<th>Active</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="center" items="${successCentersBeansList}">
												<tr>
													<td><c:out value="${center.name}"/></td>
													<td><c:out value="${center.city}"/></td>
													<td><c:out value="${center.state}"/></td>
													<td><c:out value="${center.capacity}"/></td>
													<td><c:out value="${center.address}"/></td>
													<td><c:out value="${center.googleMapUrl}"/></td>
													<td><c:out value="${center.locality}"/></td>
													<td><c:out value="${center.active}"/></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				<% } %>
				<%
					if(failCentersBeansList != null && failCentersBeansList.size() > 0){
				%>
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="errorList">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#error-list" aria-controls="error-list">
									Centers with errors
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
												<th>Name</th>
												<th>City</th>
												<th>State</th>
												<th>Capacity</th>
												<th>Address</th>
												<th>Google Map URL</th>
												<th>Locality</th>
												<th>Active</th>
												<th>Error</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="center" items="${failCentersBeansList}">
												<tr>
													<td><c:out value="${center.name}"/></td>
													<td><c:out value="${center.city}"/></td>
													<td><c:out value="${center.state}"/></td>
													<td><c:out value="${center.capacity}"/></td>
													<td><c:out value="${center.address}"/></td>
													<td><c:out value="${center.googleMapUrl}"/></td>
													<td><c:out value="${center.locality}"/></td>
													<td><c:out value="${center.active}"/></td>
													<td><c:out value="${center.error}"/></td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				<% } %>
				<div class="panel panel-default">
					<div class="panel-heading" role="tab" id="centersList">
						<h4 class="panel-title">
							<a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#centers-list" aria-expanded="true" aria-controls="centers-list">
								Centers List
							</a>
						</h4>
					</div>
					<div id="centers-list" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="centersList">
						<div class="panel-body">
							<div class="table-responsive">
								<table id="centers-list-table" class="table table-striped " style="width: 100% !important;">
									<thead>
										<tr>
											<th>Name</th>
											<th>City</th>
											<th>State</th>
											<th>Capacity</th>
											<th>Address</th>
											<th>Google Map URL</th>
											<th>Locality</th>
											<th>Active</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="center" items="${centersList}">
											<tr>
												<td><c:out value="${center.name}"/></td>
												<td><c:out value="${center.city}"/></td>
												<td><c:out value="${center.state}"/></td>
												<td><c:out value="${center.capacity}"/></td>
												<td><c:out value="${center.address}"/></td>
												<td><c:out value="${center.googleMapUrl}"/></td>
												<td><c:out value="${center.locality}"/></td>
												<td>
													<a 
														href="javascript:void(0)" 
														centerId="${center.centerId}" 
														active="${center.active}" 
														class="toggleCenterActive_MBAWX" 
														style="color:#c72127;"
													>
														<c:out value="${center.active}"/>
													</a>
												</td>
												<td>
													<a 
														href="updateCenterForm_MBAWX?centerId=${center.centerId}" 
														style="color:#c72127;"
													>
														Edit
													</a>
													<a 
														href="javascript:void(0)" 
														centerId="${center.centerId}" 
														class="deleteCenterFromList_MBAWX" 
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
			$(document).on('click',".deleteCenterFromList_MBAWX",function(){
				let prompt = confirm("Are you sure you want to delete?");
				if(!prompt){
					return false;
				}
				let centerId = $(this).attr("centerId");
				$.ajax({
					url:"deleteCenter_MBAWX?centerId=" + centerId,
					method:"POST",
					success:function(response){
						if(response.status == "success"){
							alert(response.message);
							window.location.href = "uploadCentersForm_MBAWX";
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

			$(document).on('click',".toggleCenterActive_MBAWX",function(){
				let prompt = confirm("Are you sure you want to toggle this record?");
				if(!prompt){
					return false;
				}
				let centerId = $(this).attr("centerId");
				let active = $(this).attr("active") == "Y" ? "N" : "Y";
				
				$.ajax({
					url:"toggleCenterActive_MBAWX?centerId=" + centerId + "&active=" + active,
					method:"POST",
					success:function(response){
						if(response.status == "success"){
							alert(response.message);
							window.location.href = "uploadCentersForm_MBAWX";
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
</body>
</html>
