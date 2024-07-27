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
<spring:eval
	expression="@propertyConfigurer.getProperty('UMF_ACCESS_URL')"
	var="UMF_ACCESS_URL" />
<jsp:include page="../jscss.jsp">
	<jsp:param value="UFM Incident Report " name="title" />
</jsp:include>

<style>
.customTheme .table>tbody>tr>th, .customTheme .table>tbody>tr>td {
	padding-left: 1.3em;
}
</style>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend>UFM Student Incident Report </legend>
			</div>

			<div class="panel-body">
				<%@ include file="../messages.jsp"%>
				<form:form modelAttribute="fileBean" method="post"
					action="ufmIncidentReport">
					<div class="row">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="year">Exam Year</label>
								<form:select path="year" id="year" required="required">
									<form:option value="">Select Year</form:option>
									<form:options items="${ yearList }" />
								</form:select>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="month">Exam Month</label>
								<form:select path="month" id="month" required="required">
									<form:option value="">Select Month</form:option>
									<form:options items="${ monthList }" />
								</form:select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="category">Category</label>
								<form:select path="category" id="category" required="required">
									<form:option value="">Select Category</form:option>
									<form:option value="UFM">UFM</form:option>
									<form:option value="COC">COC</form:option>
									<%-- 	<form:option value="DisconnectAbove15Min">DisconnectAbove15Min</form:option> --%>
									<!--<form:option value="DisconnectBelow15Min">DisconnectBelow15Min</form:option> -->
								</form:select>
							</div>
						</div>

					</div>
					<br>
					<div class="row">
						<div class="col-md-6 column">
							<button id="submit" name="submit"
								class="btn btn-large btn-primary" formaction="ufmIncidentReport">Submit</button>
						</div>
					</div>
				</form:form>
			</div>

		</div>
	</section>




	<c:if test="${ incidentDetails != null && incidentDetails.size() > 0 }">

		<section class="content-container login">
			<div class="container-fluid customTheme">
				<div class="row">
					<legend>UFM Student Incident Report</legend>
									(${incidentDetails.size()} Records Found) <a href="UFMIncidentReport">Download UFM Student Incident Report</a></font>
							
				</div>
				<div class="panel-body">
					<div class="table-responsive">
						<table id="success-table" class="table table-striped ">
							<thead>
								<tr>
									<th>Sapid</th>
									<th>Subject</th>
									<th>Exam Month</th>
									<th>Exam Year</th>
									<th>Category</th>
									<th>Notice Url</th>
									<th>Incidents</th>
								
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${ incidentDetails }" var="bean"
									varStatus="status">
									<tr>
										<td>${ bean.sapid }</td>
										<td>${ bean.subject }</td>
										<td>${ bean.month }</td>
										<td>${ bean.year }</td>
										<td>${ bean.category}</td>
										<td>${ bean.showCauseNoticeURL}</td>
										<td>
										<c:forEach items="${ bean.incidentBean }" var="incidentBean">
										[Incident : ${ incidentBean.incident}- Timestamp : ${ incidentBean.time_Stamp}- VidoeNumber : ${ incidentBean.video_Number}]
									
									
									</c:forEach>
									</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</section>
	</c:if>


	<jsp:include page="../footer.jsp" />
	
	<!-- Calling Datatable JS jQuery -->
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script
		src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	<script>
		$(document).ready(function() {
			$('table').DataTable();
		});
	</script>


</body>
</html>
