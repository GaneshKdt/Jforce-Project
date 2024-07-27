<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="TEE Attendance Report" name="title" />
</jsp:include>

<style>
.customTheme .table>tbody>tr>th, .customTheme .table>tbody>tr>td{
	padding-left: 1.3em;
}
</style>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>TEE Attendance Report</legend></div>
			
			<div class="panel-body">
				<%@ include file="messages.jsp"%>
				<form:form modelAttribute="teeAttendance" method="post" action="teeAttendanceReport">
					<div class="row">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="year">Exam Year</label>
								<form:select path="year" id="year" required="required">
									<form:option value="">Select Year</form:option>
									<c:forEach var="year" items="${yearList}">
										<form:option value="${year}">${year}</form:option>
									</c:forEach>
								</form:select>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="month">Exam Month</label>
								<form:select path="month" id="month" required="required">
									<form:option value="">Select Month</form:option>
									<form:option value="Apr">Apr</form:option>
									<form:option value="Jun">Jun</form:option>
									<form:option value="Sep">Sep</form:option>
									<form:option value="Dec">Dec</form:option>
								</form:select>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group" style="overflow:visible;">
								<label for="program">Program(Optional)</label>
								<form:select id="program" path="programCode" class="combobox form-control">
									<option value="" disabled selected>Select Or Type Program</option>
									<c:forEach var="program" items="${programList}">
								    	<form:option value="${program.programCode}">${program.programCode} - ${program.programName}</form:option>
									</c:forEach>
								</form:select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="sem">Sem(Optional)</label>
								<form:select path="sem" id="sem">
									<form:option value="">Select Sem</form:option>
									<form:option value="1">1</form:option>
									<form:option value="2">2</form:option>
									<form:option value="3">3</form:option>
									<form:option value="4">4</form:option>
									<form:option value="5">5</form:option>
									<form:option value="6">6</form:option>
								</form:select>
							</div>
						</div>
					</div>
					<br>
					<div class="row">
						<div class="col-md-6 column">
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="teeAttendanceReport">Generate Report</button>
							<button id="cancel" name="cancel" class="btn btn-large btn-danger" formaction="/studentportal/home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>
				</form:form>
			</div>
					
			<c:if test="${ teeAttendanceReport != null}">
				<div class="row"><legend>TEE Attendance Report( Number Of Record ${teeAttendanceReport.size()}) <small style="font-size: 16px"><a href="/exam/admin/TEEAttendanceReport">Download TEE Attendance Report</a></small></legend></div>
			</c:if>
			
		</div>
	</section>
	
	<jsp:include page="footer.jsp" />

	<!-- Calling Datatable JS jQuery -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

	<script>
		$(document).ready( function () {
			$('#attendance-table').DataTable();
			$('.combobox').attr('placeholder','Select or Type Program');
		});
	</script>
	
</body>
</html>
