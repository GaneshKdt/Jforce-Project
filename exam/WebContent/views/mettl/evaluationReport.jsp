<!DOCTYPE html>
<!--[if lt IE 7]>	<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>		<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>		<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.MettlPGResponseBean"%>
<%@page import="java.util.List"%>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.MBAExamBookingRequest"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Pull Results from Mettl" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
		
	<section class="content-container login">
		<div class="container-fluid customTheme">
		<div class="row"><legend>Evaluation Info Form</legend></div>
		<%@ include file="../messages.jsp"%>
		<div class="clearfix"></div>
			<div class="row">
				<form:form action="getMettlStudentEvaluationReport" method="post" modelAttribute="inputBean" enctype="multipart/form-data">
					<div class="panel-body">
						<div class="row">
							<div class="col-md-6 column">
								<div class="form-group">
									<label for="examYear">Exam Year</label>
									<form:select path="examYear" id="examYear" required="required">
										<option value=""></option>
										<option value="2020">2020</option>
										<option value="2021">2021</option>
										<option value="2022">2022</option>
									</form:select>
								</div>
							</div>
	
							<div class="col-md-6 column">
								<div class="form-group">
									<label for="examMonth">Exam Month</label>
									<form:select path="examMonth" id="examMonth" required="required">
										<option value=""></option>
										<option value="Jan">Jan</option>
										<option value="Feb">Feb</option>
										<option value="Mar">May</option>
										<option value="Apr">Apr</option>
										<option value="May">May</option>
										<option value="Jun">Jun</option>
										<option value="Jul">Jul</option>
										<option value="Aug">Aug</option>
										<option value="Sep">Sep</option>
										<option value="Oct">Oct</option>
										<option value="Nov">Nov</option>
										<option value="Dec">Dec</option>
									</form:select>
								</div>
							</div>
	
							<div class="col-md-12 column">
								<div class="form-group">
									<form:label for="fileData" path="fileData">Upload list of students/subjects to get information for.</form:label>
									<form:input path="fileData" type="file" required="required"/>(FORMAT. Sapid, Subject)
								</div>		
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-6 column">
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="getMettlStudentEvaluationReport">
									Pull Results
								</button>
							</div>
						</div>
					</div>
				</form:form>
			</div>
		</div>
	</section>
	
	<c:if test="${fn:length(failureResponse) gt 0}">
		<section class="content-container login">
			<div class="container-fluid customTheme">
			<div class="row"><legend>Error Records</legend></div>
			<div class="clearfix"></div>
				<div class="row">
					<table id="error-list-table" class="table table-striped " style="width: 100% !important;">
			 			<thead>
			 				<tr>
								<th>Sapid</th>
								<th>Sify Subject Code</th>
								<th>Subject</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Access Key</th>
								<th>Error</th>
			 				</tr>
			 			</thead>
			 			<tbody>
			 				<c:forEach var="bean" items="${failureResponse}">
			 					<tr>
									<td><c:out value="${bean.sapid}"/></td>
									<td><c:out value="${bean.sifySubjectCode}"/></td>
									<td><c:out value="${bean.subject}"/></td>
									<td><c:out value="${bean.year}"/></td>
									<td><c:out value="${bean.month}"/></td>
									<td><c:out value="${bean.schedule_accessKey}"/></td>
									<td><c:out value="${bean.error}"/></td>
			 					</tr>
			 				</c:forEach>
			 			</tbody>
			 		</table>
				</div>
			</div>
		</section>
	</c:if>
	<jsp:include page="../footer.jsp" />
	
	<script>
		
		$(document).ready(function() {
			$('#programId, #programStructureId, #consumerTypeId').change(function() {
				var input = {}
				if($('#programId').val() != 'All') {
					input.programId = $('#programId').val()
				}

				if($('#consumerTypeId').val() != 'All') {
					input.consumerTypeId = $('#consumerTypeId').val()
				}

				if($('#programStructureId').val() != 'All') {
					input.programStructureId = $('#programStructureId').val()
				}
				
				console.log(input)
				$.ajax({
					url: "getListOfSubjectsForMettl",
					data : JSON.stringify(input),
					type: "POST",
					contentType: "application/json",
					dataType: 'json',
					
					success: function(response){
						$('#sifySubjectCode').empty()
						
					    $('#sifySubjectCode').append('<option value="All">All</option>')
						response.forEach(function(data) {
						    $('#sifySubjectCode').append('<option value="' + data.sifySubjectCode + '">' + data.subject + '</option>')
						})
					}
				});
			})
		})
		
	</script>
</body>
</html>
