<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Student Marks" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>

<section class="content-container login">
	<div class="container-fluid customTheme">
		<%@ include file="messages.jsp"%>
		<div class="row clearfix">
		<legend>Student Marks</legend>
			<div class="col-md-6 column">
				<form  action="editStudentMarks" method="post">


				<h4>Year: ${studentMarks.year}</h4>
				<h4>Month: ${studentMarks.month}</h4>
				
				<h4>GR No.: ${studentMarks.grno}</h4>
				<h4>SAP ID: ${studentMarks.sapid}</h4>
				
				<h4>Student Name: ${studentMarks.studentname}</h4>
				<h4>Program: ${studentMarks.program}</h4>
				<h4>Semester: ${studentMarks.sem}</h4>
				<h4>Subject: ${company.subject}</h4>
				<h4>Written Score: ${studentMarks.writenscore}</h4>
				
				<h4>Assignment Score: ${studentMarks.assignmentscore}</h4>
				<h4>Grace Marks: ${studentMarks.gracemarks}</h4>
				<h4>Attempt: ${studentMarks.attempt}</h4>
				<h4>Source: ${studentMarks.source}</h4>
				<h4>Location: ${studentMarks.location}</h4>
				<h4>Center Code: ${studentMarks.centercode}</h4>
				<h4>Remarks: ${studentMarks.remarks}</h4>
				<h4>Created By: ${studentMarks.createdBy}</h4>
				<h4>Created Date: ${studentMarks.createdDate}</h4>
				<h4>Last Modified By: ${studentMarks.lastModifiedBy}</h4>
				<h4>Last Modified Date: ${studentMarks.lastModifiedDate}</h4>
				
				<c:url value="editStudentMarks" var="editurl">
				  <c:param name="id" value="${studentMarks.id}" />
				</c:url>
				<c:url value="deleteStudentMarks" var="deleteurl">
				  <c:param name="id" value="${studentMarks.id}" />
				</c:url>
				

				<!-- Button (Double) -->
				<div class="control-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="edit" name="edit" class="btn btn-success" formaction="${editurl}">Edit</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
				</form>
			</div>
		</div>
	</div>
</section>

  <jsp:include page="footer.jsp" />


</body>
</html>
