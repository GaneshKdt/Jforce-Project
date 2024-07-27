<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Make results live" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row clearfix">
		<%@ include file="messages.jsp"%>
		<div class="col-md-6 column">
			<legend>&nbsp;Make Executive Exam Timetable Live</legend>
			<form:form action="makeExecutiveTimetableLive" method="post" modelAttribute="exam">
				<fieldset>
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
						
						<div class="form-group">
							<form:select id="acadYear" path="acadYear"  placeholder="acadYear" class="form-control" itemValue="${exam.acadYear}" required="required">
								<form:option value="">Select Student Batch Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
						
						<div class="form-group">
							<form:select id="acadMonth" path="acadMonth"  placeholder="acadMonth" class="form-control" itemValue="${exam.acadMonth}" required="required">
								<form:option value="">Select Student Batch Month</form:option>
								<form:options items="${acadMonthList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="timeTableLive" path="timeTableLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.timeTableLive}" required="required">
								<form:option value="">Select to make live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeExecutiveTimetableLive">Make Executive Timetable Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				
				
				
			</div>
			
			<div class="col-md-12 column">
			<legend>&nbsp;Current Status</legend>
				<table class="table table-striped" style="font-size: 12px">
				<thead>
					<tr>
						<th>Sr. No.</th>
						<th>Exam</th>
						<th>Batch</th>
						<th>Timetable Live</th>
	
	
					</tr>
				</thead>
				<tbody>
	
					<c:forEach var="exam" items="${examsList}" varStatus="status">
						<tr>
							<td><c:out value="${status.count}" /></td>
							<td nowrap="nowrap"><c:out value="${exam.month}-${exam.year}" /></td>
							<td nowrap="nowrap"><c:out value="${exam.acadMonth}-${exam.acadYear}" /></td>
							<td><c:out value="${exam.timeTableLive}" /></td>
							
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
