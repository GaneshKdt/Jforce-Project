<!DOCTYPE html>

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.TestExamBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Search IA for Evaluation" name="title" />
</jsp:include>

<link rel="stylesheet"
	href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css">

<body class="inside">

	<%@ include file="header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Search IA for Evaluation</legend>
			</div>
			<%@ include file="messages.jsp"%>
			<div class="panel-body clearfix">
				<form:form action="searchIaToEvaluate" method="post"
					modelAttribute="testBean" role="form">
					<fieldset>
						<div class="col-md-6 column">

							<div class="form-group">
								<form:select id="year" path="year" class="form-control"
									required="required" itemValue="${testBean.year}">
									<form:option value="">Select Exam Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>

							<div class="form-group">
								<form:select id="month" path="month" type="text"
									placeholder="Month" class="form-control" required="required"
									itemValue="${testBean.month}">
									<form:option value="">Select Exam Month</form:option>

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

							<div class="form-group" style="overflow: visible;">
								<form:select id="subject" path="subject"
									class="combobox form-control" itemValue="${testBean.subject}">
									<form:option value="" selected="selected">Type OR Select Subject</form:option>
									<form:options items="${subjectList}" />
								</form:select>
							</div>

							<div class="form-group">
								<form:input id="facultyId" path="facultyId" type="text"
									placeholder="Faculty ID" class="form-control"
									value="${testBean.facultyId}" />
							</div>

							<div class="form-group">
								<form:input id="sapid" path="sapid" type="text"
									placeholder="SAP ID" class="form-control"
									value="${testBean.sapid}" />
							</div>

							<div class="control-group">
								<div class="form-group">
									<button id="submit" name="submit" class="btn btn-primary"
										formaction="searchIaToEvaluate">Search IA Evaluation
										Report</button>

									<button id="cancel" name="cancel" class="btn btn-danger "
										formaction="home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
						</div>
					</fieldset>
				</form:form>
			</div>

			<hr
				style="background-color: rgb(228, 209, 209); height: 1px; border: 0;">
			<c:choose>
				<c:when test="${rowCount > 0}">

					<h2>
						&nbsp;IA Evaluates Details <font size="2px"> (${rowCount}
							Records Found)&nbsp; &nbsp; <%if(roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1) { %>
							<a class="btn btn-sm btn-primary"
							href="/exam/downloadIaToEvaluate">Download IA Evaluates Excel</a>
							<%} %>
						</font>
					</h2>

					<div class="panel-body table-responsive">
						<table class="table table-striped table-hover" id="dataTable"
							style="font-size: 12px">
							<thead>
								<tr>
									<th>Sr. No.</th>
									<th>Subject</th>
									<th>Teaching Faculty Name</th>
									<th>Teaching Faculty Id</th>
									<th>Evaluator Faculty Name</th>
									<th>Evaluator Faculty Id</th>
									<th>Session No.</th>
									<th>Question Type</th>
									<th>Total No. of Questions</th>
									<th>Test Start Date / Time</th>
									<th>SAP ID</th>
									<th>First Name</th>
									<th>Last Name</th>
									<th>Evaluated (Y / N)</th>
									<th>Awarded Score</th>
									<th>Max Score</th>
									<th>Batch</th>
									<th>Acad Year</th>
									<th>Acad Month</th>
									<th>Exam Year</th>
									<th>Exam Month</th>
									<th>Attempt Status</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach var="iaList" items="${iaList}" varStatus="status">
									<tr>
										<td><c:out value="${status.count}" /></td>
										<td><c:out value="${iaList.subject }" /></td>
										<td><c:out value="${iaList.facultyName }" /></td>
										<td><c:out value="${iaList.facultyId }" /></td>
										<td><c:out value="${iaList.evalFacultyName }" /></td>
										<td><c:out value="${iaList.evalFacultyId }" /></td>
										<td><c:out value="${iaList.sessionName }" /></td>
										<td><c:out value="${iaList.questionType }" /></td>
										<td><c:out value="${iaList.noOfQuestions }" /></td>
										<td><c:out value="${iaList.startDate }" /></td>
										<td><c:out value="${iaList.sapid }" /></td>
										<td><c:out value="${iaList.firstName }" /></td>
										<td><c:out value="${iaList.lastName }" /></td>
										<td><c:out value="${iaList.evaluated }" /></td>
										<td><c:out value="${iaList.score }" /></td>
										<td><c:out value="${iaList.maxScore }" /></td>
										<td><c:out value="${iaList.batch }" /></td>
										<td><c:out value="${iaList.acadYear }" /></td>
										<td><c:out value="${iaList.acadMonth }" /></td>
										<td><c:out value="${iaList.year }" /></td>
										<td><c:out value="${iaList.month }" /></td>
										<td><c:out value="${iaList.attemptStatus }" /></td>

									</tr>
								</c:forEach>

							</tbody>
						</table>
					</div>
					<br>

				</c:when>
			</c:choose>
	</section>

	<jsp:include page="footer.jsp" />
</body>

<script
	src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script
	src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script
	src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.buttons.min.js"></script>

<script type="text/javascript">

$(document).ready (function(){
	$('#dataTable').DataTable();

});
</script>
<script>
$(document).ready(function() {
    //toggle `popup` / `inline` mode
    $.fn.editable.defaults.mode = 'inline';     
    $('.editable').each(function() {
        $(this).editable();
    });
});
</script>
</html>