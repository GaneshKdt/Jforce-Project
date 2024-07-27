<!DOCTYPE html>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Project Module Vise Extension" name="title"/>
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
			<fieldset class="row"><legend>Project Module Vise Extension</legend></fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="inputBean" method="post"	enctype="multipart/form-data">
				<div class="panel-body">
				
					<div class="col-md-6">
						<div class="form-group">
							<label>Exam Month</label>
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
							<label >Sapid</label>
							<form:input id="sapid" path="sapid" type="text" placeholder="sapid" class="form-control" required="required"/>
						</div>
					</div>
					
					<div class="col-md-6">
						<div class="form-group">
							<label>Extension Type</label>
							<form:select id="moduleType" path="moduleType" class="form-control" required="required">
								<form:option value="">Select Extension Type</form:option>
								<form:option value="Title Selection">Title Selection</form:option>
								<form:option value="SOP">SOP</form:option>
								<form:option value="Project Submission">Project Submission</form:option>
								<form:option value="Synopsis">Synopsis</form:option>
								<form:option value="Viva">Viva</form:option>
							</form:select>
						</div>
					</div>
					
					<div class="col-md-6">
						<div class="form-group">
							<label>Subject</label>
							<form:select id="subject" path="subject" class="form-control" required="required">
								<form:option value="">Select Subject</form:option>
								<form:option value="Project">Project</form:option>
								<form:option value="Module 4 - Project">Module 4 - Project</form:option>
							</form:select>
						</div>
					</div>
					
					<div class="col-md-6">
						<div class="form-group">
							<label >Extension End Date</label>
							<form:input id="endDate" path="endDate" type="datetime-local" placeholder="endDate" class="form-control" required="required"/>
						</div>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addProjectModuleExtended">
							Add Extension
						</button>
					</div>
				</div>
			</form:form>
			
			<div class="panel-group" style="margin-top : 30px" id="accordion" role="tablist" aria-multiselectable="true">
				<div class="panel panel-default">
					<div class="panel-heading" role="tab" id="centersList">
						<h4 class="panel-configuration">
							<a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#centers-list" aria-expanded="true" aria-controls="centers-list">
								Extended Students List
							</a>
						</h4>
					</div>
					<div id="centers-list" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="centersList">
						<div class="panel-body">
							<div class="table-responsive">
								<table id="centers-list-table" class="table table-striped " style="width: 100% !important;">
									<thead>
										<tr>
											<th>Sapid</th>
											<th>Student Name</th>
											<th>Subject</th>
											<th>Exam Year</th>
											<th>Exam Month</th>
											<th>Module Type</th>
											<th>End Date</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="bean" items="${extendedList}">
											<tr>
												<td>${bean.sapid}</td>
												<td>${bean.firstname} ${bean.lastname}</td>
												<td>${bean.subject}</td>
												<td>${bean.examYear}</td>
												<td>${bean.examMonth}</td>
												<td>${bean.moduleType}</td>
												<td>${bean.endDate}</td>
												<td>
													<a 
														href="updateProjectExtensionForm?id=${bean.id}" 
														style="color:#c72127;"
													>
														Edit
													</a>
													<a 
														href="javascript:void(0)" 
														extensionId="${bean.id}" 
														class="deleteModuleExtension" 
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
			$(document).on('click',".deleteModuleExtension",function(){
				let prompt = confirm("Are you sure you want to delete?");
				if(!prompt){
					return false;
				}
				let extensionId = $(this).attr("extensionId");
				$.ajax({
					url:"deleteModuleExtension?id=" + extensionId,
					method:"POST",
					success:function(response){
						if(response.status == "success"){
							alert(response.message);
							window.location.href = "projectModuleExtendedForm";
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
