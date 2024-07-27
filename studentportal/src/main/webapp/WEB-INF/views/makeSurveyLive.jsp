<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<%@page import="com.nmims.beans.AcadCycleFeedback"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Make Survey live" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
			<div class="row clearfix">
				<%@ include file="messages.jsp"%>
				<div class="row">
					<div class="panel">
						<div class="col-md-6 column">
							<div class="panel">
								<legend>&nbsp;Make Survey Live</legend>
								<form:form action="makeSurveyLive" method="post"
									modelAttribute="survey">
									<fieldset>
										<div class="form-group">
											<form:select id="year" path="year" type="text"
												placeholder="Year" class="form-control" required="required">
												<form:option value="">Select Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="month" path="month" type="text"
												placeholder="Month" class="form-control" required="required">
												<form:option value="">Select Month</form:option>
												<form:options items="${monthList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="live" path="live" type="text"
												placeholder="Make Live" class="form-control"
												required="required">
												<form:option value="">Select to make live</form:option>
												<form:option value="Y">Yes</form:option>
												<form:option value="N">No</form:option>
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="type" path="type" type="text"
												placeholder="Make Live" class="form-control"
												required="required">
												<form:option value="">Select Survey Type</form:option>
												<form:option value="Academic Survey">Academic Survey</form:option>

											</form:select>
										</div>

										<!-- Button (Double) -->
										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-sm btn-primary" formaction="makeSurveyLive">Make
													Survey Live!</button>
												<button id="cancel" name="cancel"
													class="btn btn-danger btn-sm" formaction="home"
													formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>

									</fieldset>
								</form:form>
							</div>
						</div>
						<div class="col-md-12 column">
							<div class="panel">
								<legend>&nbsp;Current Status</legend>
								<table class="table table-striped" style="font-size: 12px">
									<thead>
										<tr>
											<th>Sr. No.</th>
											<th>Type</th>
											<th>Live</th>
											<th>AcadYear</th>
											<th>AcadMonth</th>

										</tr>
									</thead>
									<tbody>

										<c:forEach var="exam" items="${surveyConfList}"
											varStatus="status">
											<tr>
												<td><c:out value="${status.count}" /></td>
												<td><c:out value="${exam.type}" /></td>
												<td><c:out value="${exam.live}" /></td>
												<td><c:out value="${exam.year}" /></td>
												<td><c:out value="${exam.month}" /></td>

											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>

					</div>

				</div>
	</section>

	<jsp:include page="footer.jsp" />

</body>
</html>
