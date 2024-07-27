<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PageCareerservicesBean"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
<jsp:param value="Search Sessions Scheduled" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="/views/adminCommon/header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Sessions Scheduled</legend></div>
		<%@ include file="/views/adminCommon/messages.jsp"%>
		
		<form:form  action="searchScheduledSession" method="post" modelAttribute="searchBean">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-6 column">
					
					<%-- <div class="form-group" style="overflow:visible;">
							<form:select id="subject" path="subject"  class="combobox form-control"   itemValue="${searchBean.subject}">
								<form:option value="">Type OR Select Subject</form:option>
								<form:options items="${subjectList}" />
								<form:option value="Assignment">Assignment</form:option>
							</form:select>
					</div> --%>
					
					<div class="form-group" >
							<form:select id="facultyLocation" path="facultyLocation"  class="form-control"  
								 itemValue="${session.facultyLocation}"> 
								<form:option value="">Select Faculty Location</form:option>
								<form:options items="${locationList}" />
							</form:select>
					</div> 
					
					<div class="form-group">
						<form:input id="date" path="date" type="date" placeholder="Session Date" class="form-control" value="${searchBean.date}" />
					</div>
					
			
			
			
	
			<div class="form-group">
				<form:select id="day" path="day" type="text" placeholder="Day" class="form-control"  itemValue="${searchBean.day}">
					<form:option value="">Select Day</form:option>
					<form:option value="Monday">Monday</form:option>
					<form:option value="Tuesday">Tuesday</form:option>
					<form:option value="Wednesday">Wednesday</form:option>
					<form:option value="Thursday">Thursday</form:option>
					<form:option value="Friday">Friday</form:option>
					<form:option value="Saturday">Saturday</form:option>
					<form:option value="Sunday">Sunday</form:option>
				</form:select>
			</div>
			
			<div class="form-group">
					<form:input id="sessionName" path="sessionName" type="text" placeholder="Session Name" class="form-control"  value="${searchBean.sessionName}"/>
			</div>
			
			<div class="form-group">
					<form:input id="facultyId" path="facultyId" type="text" placeholder="Faculty ID" class="form-control"  value="${searchBean.facultyId}"/>
			</div>
			
								
			<div class="form-group">
				<label class="control-label" for="submit"></label>
				<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchScheduledSession">Search</button>
				<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
			</div>
					
			
			</div>
			</div>
			</fieldset>
		</form:form>
		
	<c:choose>
	<c:when test="${rowCount > 0}">

	<legend>&nbsp;Scheduled Sessions<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadScheduledSessions">Download to Excel</a> </font></legend>
	<div class="table-responsive">
	<table class="table table-striped" style="font-size:12px">
			<thead>
			<tr>
				<th>Sr. No.</th>
				<th>Session</th>
				<th>Date</th>
				<th>Day</th>
				<th>Start Time</th>
				<th>Faculty ID</th>
				<th>Faculty1 Location</th>
				<th>Actions</th>
				<th>Video Details</th>
			</tr>
			</thead>
			<tbody>
			
			<c:forEach var="bean" items="${scheduledSessionList}" varStatus="status">
		        <tr>
		            <td><c:out value="${status.count}" /></td>
		            <td><c:out value="${bean.sessionName}" /></td>
		            <td><c:out value="${bean.date}" /></td>
					<td><c:out value="${bean.day}" /></td>
					<td><c:out value="${bean.startTime}" /></td>
					<td><c:out value="${bean.facultyId}" /></td>
					<td><c:out value="${bean.facultyLocation}" /></td>
					<td> 
			            <c:url value="editScheduledSession" var="editurl">
						  <c:param name="id" value="${bean.id}" />
						</c:url>
						<c:url value="deleteScheduledSession" var="deleteurl">
						  <c:param name="id" value="${bean.id}" />
						</c:url>
						
						<c:url value="sessionCancellationForm" var="sessionCancellation">
						  <c:param name="id" value="${bean.id}" />
						</c:url>
						
						<%-- <%if(roles.indexOf("Acads Admin") != -1  || roles.indexOf("Portal Admin") != -1){ %> --%>
						 <a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>&nbsp;
						 <a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa fa-trash-o fa-lg"></i></a> 
						 <a href="${sessionCancellation}" title="Cancel Session"><i class="glyphicon glyphicon-remove"></i></a>
						<%-- <%} %> --%>
						
		            </td>
		            <td>
		            	<a href="/acads/uploadSessionWiseVideoContentForm?id=${bean.id}" target="_blank">
		            		<b>
		            			<i class="fa fa-upload"></i>
		            		</b>
		            	</a>
		            </td>
		            
		            
		        </tr>   
		    </c:forEach>
				
			</tbody>
		</table>
	</div>
	<br>

	</c:when>
	</c:choose>

	<c:url var="firstUrl" value="searchScheduledSessionPage?pageNo=1" />
	<c:url var="lastUrl" value="searchScheduledSessionPage?pageNo=${page.totalPages}" />
	<c:url var="prevUrl" value="searchScheduledSessionPage?pageNo=${page.currentIndex - 1}" />
	<c:url var="nextUrl" value="searchScheduledSessionPage?pageNo=${page.currentIndex + 1}" />
	
	
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
	            <c:url var="pageUrl" value="searchScheduledSessionPage?pageNo=${i}" />
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

	<jsp:include page="/views/adminCommon/footer.jsp"/>

</body>
</html>