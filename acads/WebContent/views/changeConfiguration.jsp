<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Set Dates for Various Exam Configuration" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row"><legend>&nbsp;Set Dates</legend></div>
		<div class="panel-body">
		<%@ include file="messages.jsp"%>
		<div class="col-md-6 column">
			<legend>&nbsp;Set Dates for Configuration</legend>
			<form:form action="makeResultsLive" method="post" modelAttribute="configuration">
				<fieldset>
						<div class="form-group">
							<form:select id="configurationType" path="configurationType"  class="form-control" itemValue="${configuration.configurationType}" required="required">
								<form:option value="">Select Configuration</form:option>
								<form:options items="${configurationList}" />
							</form:select>
						</div>

						<div class="form-group">
							<label for="startDate">Start Date</label>
							<form:input path="startTime" id="startDate" type="datetime-local" />
						</div>
						
						<div class="form-group">
							<label for="endDate">End Date</label>
							<form:input path="endTime" id="endDate" type="datetime-local" />
						</div>


						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-large btn-primary"	formaction="changeConfiguration">Save Configuration</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				
				
				
			</div>
			<div class="col-md-1 column">
			</div>
			<div class="col-md-11 column">
			<legend>&nbsp;Current Configuration</legend>
				<table class="table table-striped" style="font-size: 12px">
				<thead>
					<tr>
						<th>Sr. No.</th>
						<th>Configuration</th>
						<th>Start Date Time</th>
						<th>End Date Time</th>
	
	
					</tr>
				</thead>
				<tbody>
	
					<c:forEach var="conf" items="${currentConfList}" varStatus="status">
						<tr>
							<td><c:out value="${status.count}" /></td>
							<td nowrap="nowrap"><c:out value="${conf.configurationType}" /></td>
							<td><c:out value="${conf.startTime}" /></td>
							<td><c:out value="${conf.endTime}" /></td>
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
</html>
