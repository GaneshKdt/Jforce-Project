<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value="${ title }" name="title" />
</jsp:include>


<body class="inside">

	<%@ include file="/views/adminCommon/header.jsp"%>

	<section class="content-container login">

	<div class="container-fluid customTheme">	
			<div class="row">
				<legend>${ title }</legend>
			</div>
			<%@ include file="/views/common/messages.jsp"%>
			<div style=" overflow: auto;">
				<table  id="table"  class="table table-striped table-bordered">
					<thead class="text-center">
						<tr>
							<th rowspan="3">Sr.No</th>
							<th colspan="12">Student</th>
							<th colspan="5">Package</th>
						</tr>
						
						<tr>
							<th colspan="5">Details</th>
							<th colspan="7">Qualifications</th>
							<th rowspan="2">Start Date</th>
							<th rowspan="2">End Date</th>
							<th rowspan="2">Package ID (SFDC)</th>
							<th rowspan="2">Package Name</th>
							<th rowspan="2">Family Name</th>
						</tr>
						
						<tr>
							<th>Student Number</th>
							<th>Name</th>
							<th>Program</th>
							<th>Email Id</th>
							<th>Contact No.</th>
							
							<th>UG Qualification</th>
							<th>Highest Qualification</th>
							<th>Industry</th>
							<th>Company Name</th>
							<th>Designation</th>
							<th>Total Experience</th>
							<th>Annual Salary</th>
						</tr>
					</thead>
					<tbody>
						<% int i = 0; %>
						<c:forEach items="${AllStudentData}" var="StudentPackageData">
							<tr>
								<% i++; %>
								<td><%= i %></td>
								
								<td>${ StudentPackageData.sapid }</td>
								<td>${ StudentPackageData.student.firstName }  ${ StudentPackageData.student.middleName } ${ StudentPackageData.student.lastName }</td>
								<td>${ StudentPackageData.student.program }</td>
								<td>${ StudentPackageData.student.emailId }</td>
								<td>${ StudentPackageData.student.mobile }</td>
								
								<td>${ StudentPackageData.student.ugQualification }</td>
								<td>${ StudentPackageData.student.highestQualification }</td>
								<td>${ StudentPackageData.student.industry }</td>
								<td>${ StudentPackageData.student.companyName }</td>
								<td>${ StudentPackageData.student.designation }</td>
								<td>${ StudentPackageData.student.totalExperience }</td>
								<td>${ StudentPackageData.student.annualSalary }</td>
								
								<td>${ StudentPackageData.startDate}</td>
								<td>${ StudentPackageData.endDate }</td>
								<td>${ StudentPackageData.salesForceUID }</td>
								<td>${ StudentPackageData.packageName }</td>
								<td>${ StudentPackageData.familyName }</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<br><br><br><br>
	</section>

	<jsp:include page="/views/adminCommon/footer.jsp" />
</body>
<script>
	$(document).ready(function () {
		$('th').css({"text-align": "center", "vertical-align": "middle"});
		$("#featureId").val("${ Feature.featureId }");
		$("#featureName").val("${ Feature.featureName }");
		$("#featureDescription").val("${ Feature.featureDescription }");
		$("#validityFast").val("${ Feature.validityFast }");
		$("#validityNormal").val("${ Feature.validityNormal }");
		$("#validitySlow").val("${ Feature.validitySlow }");

		<jsp:include page="/views/adminCommon/datatables.jsp" />
	});
</script>

</html>