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
	<jsp:param value="Exam Booking Refund Request Report" name="title" />
</jsp:include>
<style>
</style>
<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend>Exam Booking Refund Request Report</legend>
			</div>

			<%@ include file="messages.jsp"%>

			<div class="panel-content-wrapper panel-body">
				<div class="" style="float: right;">
					<a href="downloadExamBookingRefundRequestReport"><i
						class="fa-solid fa-download"
						style="font-size: 2rem;border: 1px solid lightgray;padding: .5rem;border-radius: .5rem;"></i></a>
				</div>
				<br />
				<br />
				<br />
				<div class="table-responsive">
					<table class="table table-striped table-hover tables"
						style="font-size: 12px">
						<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Student Number</th>
								<th>Name</th>
								<th>Email ID</th>
								<th>Mobile Number</th>
								<th>Track ID</th>
								<th>Description</th>
								<th>Total Fees</th>
								<th>Options</th>
								<th>Submission (Date and Time)</th>
							</tr>
						</thead>
						<tbody>

							<c:forEach var="reportRequest" items="${reportData}"
								varStatus="status">
								<tr>
									<td><c:out value="${status.count}" /></td>
									<td><c:out value="${reportRequest.sapid}" /></td>
									<td><c:out value="${reportRequest.name}" /></td>
									<td><c:out value="${reportRequest.emailId}" /></td>
									<td><c:out value="${reportRequest.mobile}" /></td>
									<td><c:out value="${reportRequest.trackId}" /></td>
									<td><c:out value="${reportRequest.description}" /></td>
									<td><c:out value="${reportRequest.amount}" /></td>
									<td><c:out value="${reportRequest.options}" /></td>
									<td><c:out value="${reportRequest.submissionDate}" /></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>

<script>
	$(document)
			.ready(
					function() {

						$('.tables')
								.DataTable(
										{

											initComplete : function() {
												this
														.api()
														.columns()
														.every(
																function() {
																	var column = this;
																	var headerText = $(
																			column
																					.header())
																			.text();
																	console
																			.log("header :"
																					+ headerText);

																	column
																			.data()
																			.unique()
																			.sort()
																			.each(
																					function(
																							d,
																							j) {
																						select
																								.append('<option value="'+d+'">'
																										+ d
																										+ '</option>')
																					});

																});
											}
										});
					});
</script>
</html>