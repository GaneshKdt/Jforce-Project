<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Date"%>
<%@page import="com.nmims.beans.*"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="View Exam Center Slots" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>View Exam Center Slots</legend></div>
        <%@ include file="messages.jsp"%>
	
	<%
	List<ExecutiveExamCenter> examCentersList = (List<ExecutiveExamCenter>)request.getAttribute("examCentersList");
	%>
	
	<c:choose>
	<c:when test="${rowCount > 0}">

	<h2>&nbsp;Executive Exam Centers<font size="2px"> (${rowCount} Records Found) &nbsp; </font></h2>
	<div class="panel-body">
	
		<div class="table-responsive">
		<form:form  action="updateExamCenterCapacity" method="post" modelAttribute="examCenter">
		<form:input path="year" type="hidden" value="${examCenter.year }"/>
		<form:input path="month" type="hidden" value="${examCenter.month }"/>
		<form:input path="centerId" type="hidden" value="${examCenter.centerId }"/>
		<form:input path="ic" type="hidden" value="${examCenter.ic }"/>
		<table class="table table-striped" style="font-size:12px">
							<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Year</th>
								<th>Month</th>
								<th>Exam Center Name</th>
								<th>Date</th>
								<th>Time</th>
								<th>Capacity</th>
								<th>onHold</th>
								<th>Booked</th>
								<th>Available</th>
							</tr>
						</thead>
							<tbody>
							
							<%
							int count = 1;
							for(int i = 0; i < examCentersList.size(); i++){
								ExecutiveExamCenter bean = (ExecutiveExamCenter)examCentersList.get(i);
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
								SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
								Date formattedDate = formatter.parse(bean.getDate());
								String formattedDateString = dateFormatter.format(formattedDate);
								
								String centerId = bean.getCenterId();
								String year = bean.getYear();
								String month = bean.getMonth();
								String examCenterName = bean.getExamCenterName();
								String startTime = bean.getStarttime();
								String capacity = bean.getCapacity();
								int onHold = bean.getOnHold();
								int booked = bean.getBooked();
								int available = bean.getAvailable();
	
							%>
							
							
							 <tr>
						            <td><c:out value="<%=count++ %>" /></td>
						            <td><c:out value="<%=year %>" /></td>
						            <td><c:out value="<%=month %>" /></td>
									<td><c:out value="<%=examCenterName %>" /></td>
									<td><c:out value="<%=formattedDateString %>" /></td>
									<td><c:out value="<%=startTime %>" /></td>
									<td>
									<input type="hidden" name="centerDetails" value="<%=bean.getDate()%>|<%=startTime%>"/>
									<input id="capacity" name="capacity" type="number" placeholder="Capacity" class="form-control"  value="<%=capacity %>"/>
									</td>
									<td><c:out value="<%=onHold %>" /></td>
									<td><c:out value="<%=booked %>" /></td>
									<td><c:out value="<%=available %>"/></td>
						            
						        </tr>   
														
								<%} %>
							</tbody>
						</table>
						
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateExecutiveExamCenterCapacity">Update Capacity</button>
							<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="searchExecutiveExamCenter" formnovalidate="formnovalidate">Back to Exam Center Search</button>
						</div>
						
						</form:form>
		</div>
	</div>
	<br>

</c:when>
</c:choose>


</div>
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
