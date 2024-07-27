<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Make Executive Exam Details Live" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row clearfix">
		<%@ include file="messages.jsp"%>
		<div class= "panel">
		<div class="row">
			<legend>&nbsp;Make Executive Exam Details Live</legend>
			<form:form action="makeExecutiveRegistrationLive" method="post" modelAttribute="exam">
				<fieldset>
				<div class="col-md-9">
					<div class="form-group">
					<label style="color:darkblue">Select Exam Year-Month</label>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Exam Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${exam.month}" required="required">
								<form:option value="">Select Exam Month</form:option>
								<form:options items="${monthList}" />
							</form:select>
						</div>
						</div>
						<div class="form-group">
						<label style="color:darkblue">Select Student Enrollment Year-Month</label>
						<div class="form-group">
							<form:select id="year" path="acadYear" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Student Enrollment Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
						
						<div class="form-group">
							<form:select id="month" path="acadMonth" type="text" placeholder="Month" class="form-control" itemValue="${exam.month}" required="required">
								<form:option value="">Select Student Enrollment Month</form:option>
								<form:options items="${enrolMonthList}" />
							</form:select>
						</div>
						</div>
						
						
						<div class="form-group">
						<label style="color:darkblue">Select Exam Registration Window</label>
						<div class="form-group">
							<label for="registrationStartDate">Start Date-Time</label>
							<form:input path="registrationStartDate" id="startDate" type="datetime-local" />
						</div>
						
						<div class="form-group">
							<label for="registrationEndDate">End Date-Time</label>
							<form:input path="registrationEndDate" id="endDate" type="datetime-local" />
						</div>
						</div>
						

						
						</div>
						<div class="col-md-9">
						
					<%-- 
					    <div class="form-group">
						<label style="color:darkblue">Select Exam Registration Live Flag</label>
							<form:select id="live" path="live" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.live}" required="required">
								<form:option value="">Select to make live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div> --%>
						
						<div class="form-group">
						<label style="color:darkblue">Select Exam HallTicket Window</label>
						<div class="form-group">
							<label for="hallTicketStartDate">Start Date-Time</label>
							<form:input path="hallTicketStartDate" id="startDate" type="datetime-local" />
						</div>
						
						<div class="form-group">
							<label for="hallTicketEndDate">End Date-Time</label>
							<form:input path="hallTicketEndDate" id="endDate" type="datetime-local" />
						</div>
						</div>
						
						<!-- make sas result live start -->
						<div class="form-group">
						<label style="color:darkblue">Select Executive Result Live Flag</label>
							<form:select id="resultLive" path="resultLive" type="text"	placeholder="Make result Live" class="form-control" itemValue="${exam.resultLive}" required="required">
								<form:option value="">Select to make result live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>
						
						<div class="form-group">
						<label style="color:darkblue">Select Executive Result Declaration Date</label>
						<div class="form-group">
							<label for="resultDeclareDate">Start Date-Time</label>
							<form:input path="resultDeclareDate" id="resultDeclareDate" type="datetime-local" />
						</div> 
						</div>
						<!-- make sas result live end -->
						
						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeExecutiveRegistrationLive">Update Executive Registration Status!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
						
					</div>
				</fieldset>
				</form:form>
				
				
				
				</div>
			</div>
			
			<div class="row-md-18">
			<legend>&nbsp;Current Status</legend>
				<table class="table table-striped" style="font-size: 12px">
				<thead>
					<tr>
						<th>Sr.No.</th>
						<th>Enroll(Month-Year)</th>
						<th>Exam(Month-Year)</th>
						<th>Timetable Live</th>
						<th>Registration Start</th>
						<th>Registration End</th> 
						<th>HallTicket Start</th> 
						<th>HallTicket End</th> 
						<th>Result Live</th> 
						<th>Result Date</th> 
					</tr>
				</thead>
				<tbody>
	
					<c:forEach var="exam" items="${examsList}" varStatus="status">
						<tr>
							<td><c:out value="${status.count}" /></td>
							<td nowrap="nowrap"><c:out value="${exam.acadMonth}-${exam.acadYear}" /></td>
							<td nowrap="nowrap"><c:out value="${exam.month}-${exam.year}" /></td>
							<td><c:out value="${exam.timeTableLive}" /></td>
							<td><c:out value="${exam.registrationStartDate}" /></td>
							<td><c:out value="${exam.registrationEndDate}" /></td>
							<td><c:out value="${exam.hallTicketStartDate}" /></td>
							<td><c:out value="${exam.hallTicketEndDate}" /></td>
							<td><c:out value="${exam.resultLive}" /></td>
							<td><c:out value="${exam.resultDeclareDate}" /></td>
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
