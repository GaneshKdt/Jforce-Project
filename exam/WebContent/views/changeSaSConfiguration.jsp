<!DOCTYPE html>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Set Dates for Various Executive Programs Exam Configuration" name="title" />
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
			<form:form action="makeSapResultsLive" method="post" modelAttribute="configuration">
				<fieldset>
						<div class="form-group">
							<form:select id="configurationType" path="configurationType"  class="form-control" >
								<form:option value="">Select Configuration</form:option>
								<form:options items="${sasConfigurationList}" />
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
						
						<div class="form-group">
							<form:select path="prgrmStructApplicable" id="prgrmStructApplicable" class="form-control" >
							    <form:option value="">Program Structure Applicable</form:option>
							    <form:options items="${programStructureList}" />
							</form:select>
						</div>
						
					   <%-- <div class="form-group">
							<form:select path="program" id="program" class="form-control">
							    <form:option value="">Select Program</form:option>
							    <form:options items="${programList}" />
							</form:select>
						</div> --%>
						
					 	<div class="form-group" >
							<form:select id="programList" path="program" type="text"  class="form-control" required="required" 
								 multiple="true" > 
								<form:option value="">Select Program</form:option>
								<form:option value="All">All</form:option>
								<c:forEach items="${programList}" var="prog">
								<form:option value="${prog}">${prog}</form:option>
								</c:forEach>
							</form:select>
						</div>
						<div class="form-group" >
							<form:select id="subjectList" path="subject" type="text"  class="form-control" required="required" 
								 multiple="true" > 
								<form:option value="">Select Subject</form:option>
								<form:option value="All">All</form:option>
								<c:forEach items="${subjectList}" var="sub">
								<form:option value="${sub}">${sub}</form:option>
								</c:forEach>
							</form:select>
						</div>
						<%-- <div class="form-group">
							<form:select path="subject" id="subject" class="form-control">
							    <form:option value="">Select Subject</form:option>
							    <form:options items="${subjectList}" />
							</form:select>
						</div> --%>


						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-large btn-primary"	formaction="changeSaSConfiguration">Save Configuration</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
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
						<!-- <th>Program</th>
						<th>Subject</th> -->
	
	
					</tr>
				</thead>
				<tbody>
	
					<c:forEach var="conf" items="${currentConfList}" varStatus="status">
						<tr>
							<td><c:out value="${status.count}" /></td>
							<td nowrap="nowrap"><c:out value="${conf.configurationType}" /></td>
							<td><c:out value="${conf.startTime}" /></td>
							<td><c:out value="${conf.endTime}" /></td>
						<%-- 	<td><c:out value="${conf.program}" /></td>
							<td><c:out value="${conf.subject}" /></td> --%>
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
