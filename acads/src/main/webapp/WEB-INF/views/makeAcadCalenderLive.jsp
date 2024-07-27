<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AcadsCalenderBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Make Academic Calender Live" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row"><legend>Set Session Calender Dates</legend></div>
		<div class="panel-body clearfix">
		
		<%@ include file="messages.jsp"%>
		<div class="col-md-6 column">
			<h2>&nbsp;Make Academic Calender Live</h2>
			<form:form action="makeCalenderLive" method="post" modelAttribute="sessionCalenderBean">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control"  required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Jan">Jan</form:option>
								<form:option value="Jul">Jul</form:option>
							</form:select>
						</div>

						

						<div class="form-group">
						<form:input id="date" path="startDate" type="date" placeholder="Start Date" class="form-control" required="required"/>
					</div>
					
					<div class="form-group">
						<form:input id="date" path="endDate" type="date" placeholder="End Date" class="form-control" required="required"/>
					</div>

						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="setSessionCalenderDates">Set Session Calender Dates</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>	
			</div>
			
			<div class="col-md-12 column">
			<h2>&nbsp;Current Status</h2>
				<table class="table table-striped" style="font-size: 12px">
				<thead>
					<tr>
						<th>Sr. No.</th>
						<th>Academic Calender Year</th>
						<th>Academic Calender Month</th>
						<th>Academic Calender Start Date</th>
						<th>Academic Calender End Date</th>
						
	
	
					</tr>
				</thead>
				<tbody>
	
					<c:forEach var="calender" items="${calenderList}" varStatus="status">
						<tr>
							<td><c:out value="${status.count}" /></td>
							<td><c:out value="${calender.year}" /></td>
							<td><c:out value="${calender.month}" /></td>
							<td><c:out value="${calender.startDate}" /></td>
							<td><c:out value="${calender.endDate}" /></td>
							
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
