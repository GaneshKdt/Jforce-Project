<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../../jscss.jsp">
<jsp:param value="Student Guide Mapping" name="title" />
</jsp:include>
<link rel="stylesheet" href="//cdn.datatables.net/1.10.20/css/jquery.dataTables.min.css"/>

<body class="inside">

<%@ include file="../../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Student Guide Mapping</legend></div>
        <%@ include file="../../messages.jsp"%>
			<div class="clearfix" style="background-color:white;padding:10px;">
				<form:form class="guideMappingForm" method="POST" modelAttribute="inputBean" action="studentAndGuidMapping" enctype="multipart/form-data">
				<div class="panel-body">
				
					<div class="col-md-6">
						<div class="form-group">
							<label>Month</label>
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${filesSet.month}">
								<form:option value="">Select Month</form:option>
								<form:options items="${monthList}" />
							</form:select>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label>Year</label>
							<form:select id="year" path="year" type="text" placeholder="Acad Month" class="form-control" required="required" itemValue="${filesSet.acadMonth}">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
					</div>
					
					<div class="col-md-6">
						<div class="form-group">
							<label>Subject</label>
							<form:select id="subject" path="subject" type="text" placeholder="Subject" class="form-control" required="required" itemValue="${filesSet.examMonth}">
								<form:option value="">Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
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
						<br/> Sapid | Faculty Id <br>
						<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Project_Guide_Mapping_Template.xlsx" target="_blank">Download a Sample Template</a> <br>
					</div>
				</div>
				<input type="button" class="btn btn-primary btn-sm submitExcel" value="Submit" />
				</form:form>
			
				<br/>
				<div>
					<table class="table" id="table">
						<thead>
							<tr>
								<th>Sapid</th>
								<th>FacultyId</th>
								<th>Faculty</th>
								<th>Year</th>
								<th>Month</th>
								<th>Action</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="levelBasedProjectBean" items="${ levelBasedProjectBeans }">
								<tr>
									<td>${ levelBasedProjectBean.sapId }</td>
									<td>${ levelBasedProjectBean.facultyId }</td>
									<td>${ levelBasedProjectBean.faculty }</td>
									<td>${ levelBasedProjectBean.year }</td>
									<td>${ levelBasedProjectBean.month }</td>
									<td><a 
														href="javascript:void(0)" 
														month="${levelBasedProjectBean.month}" 
														year="${levelBasedProjectBean.year}"
														sapId="${levelBasedProjectBean.sapId}"
														facultyId="${levelBasedProjectBean.facultyId}"
														class="delete_student_guide_mapping" 
														style="color:#c72127;"
													>
														Delete
													</a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div><br/>
		
		<c:if test="${ levelBasedProjectErrorList != null && levelBasedProjectErrorList.size() > 0 }">
			<div class="panel panel-default">
				<div class="panel-heading" role="tab" id="errorList">
					<h4 class="panel-title">
						<a role="button" data-toggle="collapse" data-parent="#accordion" href="#error-list" aria-controls="error-list">
							Errors
						</a>
					</h4>
				</div>
				<div id="error-list" class="panel-collapse collapse" role="tabpanel" aria-labelledby="errorList">
					<div class="panel-body">
						<fieldset class="row">
							<legend>
								Error List
							</legend>
						</fieldset>
						<div class="table-responsive">
							<table class="table" id="table">
								<thead>
									<tr>
										<th>Sapid</th>
										<th>FacultyId</th>
										<th>Faculty</th>
										<th>Year</th>
										<th>Month</th>
										<th>Error</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="levelBasedProjectBean" items="${ levelBasedProjectErrorList }">
										<tr>
											<td>${ levelBasedProjectBean.sapId }</td>
											<td>${ levelBasedProjectBean.facultyId }</td>
											<td>${ levelBasedProjectBean.faculty }</td>
											<td>${ levelBasedProjectBean.year }</td>
											<td>${ levelBasedProjectBean.month }</td>
											<td>${ levelBasedProjectBean.error }</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
			</c:if>
	</section>
	<jsp:include page="../../footer.jsp" />
	<script src="//cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js">
	</script>
	<script>
	$(document).ready(function(){
		$('#table').DataTable();
	});
	
	$(document).on('click',".submitExcel",function(){
		let prompt = confirm("Sapid,Faculty, Exam Year-Month are unique. If entry already exists this action will update the entry. Are you sure you want to upload?");
		if(!prompt){
			return false;
		}
		$(".guideMappingForm").submit();  
	});
	$(document).on('click',".delete_student_guide_mapping",function(){
		var tr = $(this).closest("tr"); 
		let prompt = confirm("Are you sure you want to delete?");
		if(!prompt){
			return false;
		}
		let month = $(this).attr("month");
		let year = $(this).attr("year");
		let sapId = $(this).attr("sapId");
		let facultyId = $(this).attr("facultyId");  
		var data = {
				month:month,
				year: year,
				sapId:sapId,
				facultyId:facultyId
				};  
		let body = JSON.stringify(data);
 
		$.ajax({
			url:"deleteStudentGuideMapping",
			contentType : "application/json",
			data:body, 
			method:"POST",  
			success:function(response){
				console.log(response); 
				if(response==true){  
					tr.remove(); 
				}    else{ 
					alert("error");
					}
			},  
			error:function(error){
				alert("Failed to delete record");
				console.log(error);
			}
		});         
	});
	</script>
</body>
</html>
		