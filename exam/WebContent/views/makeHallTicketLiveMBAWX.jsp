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
		
			<legend>&nbsp;Make HallTicket Live</legend>
			<form:form action="makeHallTicketLiveMBAWX" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="examYear" type="text" placeholder="Year" class="form-control"  required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="examMonth" type="text" placeholder="Month" class="form-control"  required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Oct">Oct</form:option> 
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="live" path="live" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.live}" required="required">
								<form:option value="">Select to make live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>

						<div class="col-md-4 column">
						<div class="form-group">
							<label >Acads Year</label>
							<form:select id="acadsYear" path="acadsYear" type="text"	placeholder="Acads Year" class="form-control" required="required"  >
								<form:option value="">Select Acads Year</form:option>
								
				                 <form:options items="${acadsYearList}"/>
				                
				            
								
							</form:select>
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Acads Month</label>
							<form:select id="acadsMonth" path="acadsMonth" type="text" placeholder="Acads Month" class="form-control" required="required" >
								<form:option value="">Select Acads Month</form:option>
								 <form:options items="${acadsMonthList}"/>
							</form:select>
						</div>
						</div>

						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label> 
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeHallTicketLiveMBAWX">Change Live Setting!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				
			</div>
			
			<div class="col-md-12 column">
			<legend>Current Status</legend>
				<table class="table table-striped" style="font-size: 12px">
				<thead>
					<tr>
						<th>Sr. No.</th>
						<th>Exam</th>
						<th>HallTicket Live Status</th>
					</tr>
				</thead>
				<tbody>
	
					<c:forEach var="hallTicket" items="${hallTicketsList}" varStatus="status">
						<tr>
							<td><c:out value="${status.count}" /></td>
							<td nowrap="nowrap"><c:out value="${hallTicket.examMonth}-${hallTicket.examYear}" /></td>
							<td><c:out value="Live" /></td> 
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
