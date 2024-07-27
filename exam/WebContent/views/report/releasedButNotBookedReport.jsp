<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
<jsp:param value="Report for Released but Not Booked" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Report for Released but Not Booked</legend></div>
        <%@ include file="../messages.jsp"%>
		<div class="row clearfix">
		<form:form  action="releasedButNotBookedReport" method="post" modelAttribute="searchBean">
			<fieldset>
				<div class="col-md-6 column">
				
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${searchBean.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
							<form:option value="2017">2017</form:option>
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${searchBean.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="subject" path="subject" type="text" placeholder="Subject" class="form-control" required="required" itemValue="${searchBean.month}">
							<form:option value="">Select Subject</form:option>
							<form:option value="Project">Project</form:option>
							<form:option value="Module 4 - Project">Module 4 - Project</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-primary" formaction="releasedButNotBookedReport">Generate Report</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
			</div>

		</fieldset>
		</form:form>
		
		</div>
	
	
		<c:if test="${rowCount > 0}">
		
			<legend>&nbsp;Released But Not Booked<font size="2px"> (${rowCount} Records Found)&nbsp;</font></legend>
			<div class="table-responsive">
			<table class="table table-striped table-hover" style="font-size:12px">
								<thead>
									<tr> 
										<th>Sr. No.</th>
										<th>Exam Year</th>
										<th>Exam Month</th>
										<th>Subject</th>
										<th>Student ID</th>
										<th>Release Reason</th>
									</tr>
								</thead>
								<tbody>
								
								<c:forEach var="booking" items="${examBookingList}" varStatus="status">
							        <tr>
							            <td><c:out value="${status.count}"/></td>
										<td><c:out value="${booking.year}"/></td>
										<td><c:out value="${booking.month}"/></td>
										<td><c:out value="${booking.subject}"/></td>
										<td><c:out value="${booking.sapid}"/></td>
										<td><c:out value="${booking.releaseReason}"/></td>
							        </tr>   
							    </c:forEach>
									
								</tbody>
							</table>
			</div>
			<br>
		
		</c:if>


	</div>

	</section>

	<jsp:include page="../footer.jsp" />


</body>
</html>
