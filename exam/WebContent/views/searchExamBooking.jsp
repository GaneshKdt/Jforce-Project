<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search Released Exam Bookings" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Search Released Exam Bookings</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchExamBooking" method="post" modelAttribute="examBooking">
			<fieldset>
			<div class="row clearfix">
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  itemValue="${examBooking.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${examBooking.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="sapid" path="sapid" type="text" placeholder="Student Number" class="form-control" value="${examBooking.sapid}"/>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchExamBookingTOChangeCenter">Search</button>
						<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
			</div>
			</div>
			</fieldset>
		</form:form>
		
		
	
	
	<c:choose>
	<c:when test="${rowCount > 0}">
	 <form:form  action="changeCenterForStudents" method="post" modelAttribute="examBooking">
	 <form:hidden path="month"/>
	 <form:hidden path="year"/>
	 <form:hidden path="sapid"/>
	<%
	StudentExamBean student = (StudentExamBean)request.getAttribute("studentExam");
		String programStructureApplicable = student.getPrgmStructApplicable();
		Map<String, List<ExamCenterBean>> subjectAvailableCentersMap = (HashMap<String, List<ExamCenterBean>>)session.getAttribute("subjectAvailableCentersMap");
		
	%>
	
	<legend>&nbsp;Released Exam Bookings<font size="2px"> (${rowCount} Records Found) &nbsp; </font></legend>
	<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Year</th>
							<th>Month</th>
							<th>Student ID</th>
							<th>Student Name</th>
							<th>Subject</th>
							<th>Exam Date</th>
							<th>Exam Start Time</th>
							<th>Select Exam Center</th>
						</tr>
						</thead>
						<tbody>
						
						<c:forEach var="examBookingVar" items="${releasedBookingsList}" varStatus="status">
						
							<%
							ExamBookingTransactionBean bean = (ExamBookingTransactionBean)pageContext.getAttribute("examBookingVar");
							List<ExamCenterBean> examCenters = subjectAvailableCentersMap.get(bean.getSubject() + bean.getExamTime());
							
							
							%>
					        <tr>
					       
					            <td><c:out value="${status.count}" /></td>
					            <td><c:out value="${examBookingVar.year}" /></td>
					            <td><c:out value="${examBookingVar.month}" /></td>
								<td><c:out value="${examBookingVar.sapid}" /></td>
								<td><c:out value="${examBookingVar.firstName} ${examBookingVar.lastName}" /></td>
								<td><c:out value="${examBookingVar.subject}" /></td>
								<td><c:out value="${examBookingVar.examDate}" /></td>
								<td><c:out value="${examBookingVar.examTime}" /></td>
								<td>
								<%if(examCenters.size() > 0){ 
								%>
								
								<select name="selectedCenters" required="required">
								<option value="">Please Select Exam Center</option>
									<% for(int j = 0; j < examCenters.size(); j++){ 
										ExamCenterBean center = examCenters.get(j);
										String centerId = center.getCenterId();
										String centerName = center.getExamCenterName();
										String locality = center.getLocality();
										String city = center.getCity();
										int available = center.getAvailable();
										String capacity = center.getCapacity();
									%>
									
									<option value="<%=bean.getSubject()%>|<%=centerId%>|${examBookingVar.examTime}|<%=city%>">
										<%=city %> : <%=centerName %>, <%=locality %>
										<%if("Jul2014".equals(programStructureApplicable)) {%>
											(<%=available %>/<%=capacity %>)
										<%} %>
									</option>
									
									
									<%}%>
									</select>
									<%}else{%>
										No Exam Center Available
									<%} %>
								</td>
								
							
					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	<br>
	<div class="form-group">
		<label class="control-label" for="submit"></label>
		<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="changeCenterForStudents" onClick="return confirm('Are you sure you want to change Exam centers?')">Change Center</button>
		<button id="reset" type="reset" class="btn btn-danger" type="reset">Reset</button>
		<button id="cancel" name="cancel" class="btn btn-danger" formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
	</div>
	
	</form:form>
</c:when>
</c:choose>

<c:url var="firstUrl" value="searchExamBookingPage?pageNo=1" />
<c:url var="lastUrl" value="searchExamBookingPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchExamBookingPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchExamBookingPage?pageNo=${page.currentIndex + 1}" />


<c:choose>
<c:when test="${page.totalPages > 1}">
<div align="center">
    <ul class="pagination">
        <c:choose>
            <c:when test="${page.currentIndex == 1}">
                <li class="disabled"><a href="#">&lt;&lt;</a></li>
                <li class="disabled"><a href="#">&lt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${firstUrl}">&lt;&lt;</a></li>
                <li><a href="${prevUrl}">&lt;</a></li>
            </c:otherwise>
        </c:choose>
        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
            <c:url var="pageUrl" value="searchExamBookingPage?pageNo=${i}" />
            <c:choose>
                <c:when test="${i == page.currentIndex}">
                    <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <c:choose>
            <c:when test="${page.currentIndex == page.totalPages}">
                <li class="disabled"><a href="#">&gt;</a></li>
                <li class="disabled"><a href="#">&gt;&gt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${nextUrl}">&gt;</a></li>
                <li><a href="${lastUrl}">&gt;&gt;</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</div>
</c:when>
</c:choose>

</div>
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
