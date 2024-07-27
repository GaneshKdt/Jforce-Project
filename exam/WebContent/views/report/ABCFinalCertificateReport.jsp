<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Report for ABC Final Certificate" name="title" />
</jsp:include>
<style>
.button {
	display: inline-block;
	padding: 10px 20px;
	background-color: #48428E;
	color: white;
	text-decoration: none;
	border: none;
	border-radius: 4px;
	cursor: pointer;
}
</style>
<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Report for ABC Final Certificate </legend>
			</div>
			<%@ include file="../messages.jsp"%>
			<div class="row clearfix">
				<form:form action="getFinalCertificateABC" method="post"
					modelAttribute="ABCreportbean">
					<fieldset>
						<div class="col-md-6 column">

							<div class="form-group">
								<form:select id="year" path="examYear" type="text"
									placeholder="Year" class="form-control" 
									itemValue="${ABCreportbean.examYear}">
									<form:option value="">Select Year</form:option>
									<form:options items="${examYearList}" />
								</form:select>
							</div>

							<div class="form-group">
								<form:select id="month" path="examMonth" type="text"
									placeholder="Month" class="form-control" 
									itemValue="${ABCreportbean.examMonth}">
									<form:option value="">Select Month</form:option>
									<form:options items="${examMonthList}" />
								</form:select>
							</div>

							<div class="form-group">
								<button id="submit" name="submit" class="btn btn-primary"
									formaction="getFinalCertificateABC">Generate Report</button>
								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="/studentportal/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>

					</fieldset>
				</form:form>

			</div>


			<c:if test="${finalCertificateABCreport.size() > 0}">

				<legend>
					&nbsp;ABC Final Certificate Report<font size="2px">
						(${finalCertificateABCreport.size()} Records Found)&nbsp; 
						<a href="getFinalCertificateReportForABC" class="button">Download Final Certificate ABC report to Excel</a>

					</font>
				</legend>
				<%-- <div class="table-responsive">
					<table class="table table-striped table-hover"
						style="font-size: 12px">
						<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Program Name</th>
								<th>Enrollment Month</th>
								<th>Enrollment Year</th>
								<th>Sapid</th>
								<th>StudentName</th>
								<th>Gender</th>
								<th>DOB</th>
								<th>Mobile</th>
								<th>Email</th>
								<th>Father Name</th>
								<th>Mother Name</th>
								<th>Pass Fail</th>
								<th>Passing Year</th>
								<th>Passing Month</th>
								<th>DOI</th>
								<th>Certificate Number</th>
							</tr>
						</thead>
						<tbody>

							<c:forEach var="report" items="${finalCertificateABCreport}"
								varStatus="status">
								<tr>
									<td><c:out value="${status.count}" /></td>
									<td><c:out value="${report.programName}" /></td>
									<td><c:out value="${report.enrollmentMonth}" /></td>
									<td><c:out value="${report.enrollmentYear}" /></td>
									<td><c:out value="${report.sapid}" /></td>
									<td><c:out value="${report.studentName}" /></td>
									<td><c:out value="${report.gender}" /></td>
									<td><c:out value="${report.dateOfBirth}" /></td>
									<td><c:out value="${report.mobile}" /></td>
									<td><c:out value="${report.email}" /></td>
									<td><c:out value="${report.fatherName}" /></td>
									<td><c:out value="${report.motherName}" /></td>
									<td><c:out value="${report.result}" /></td>
									<td><c:out value="${report.passingYear}" /></td>
									<td><c:out value="${report.passingMonth}" /></td>
									<td><c:out value="${report.declareDate}" /></td>
									<td><c:out value="${report.certificateNumber}" /></td>
								</tr>
							</c:forEach>

						</tbody>
					</table>
				</div> --%>
				<br>

			</c:if>


		</div>

	</section>

	<jsp:include page="../footer.jsp" />


</body>
</html>
