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


<jsp:include page="../jscss.jsp">
<jsp:param value="Search Project Submissions" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Project Submissions</legend></div>
        <%@ include file="../messages.jsp"%>
		<div class="row clearfix">
		<form:form  action="searchProjectSubmission" method="post" modelAttribute="searchBean">
			<fieldset>
				<div class="col-md-6 column">
				
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${searchBean.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${searchBean.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Apr">April</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="sapId" path="sapId" type="text" placeholder="SAP ID" class="form-control" value="${searchBean.sapId}"/>
					</div>	
					
					<!-- added to enable selection in case of module 4 - project -->
					<form:select id="subject" path="subject" class="form-control" itemValue="${searchBean.subject}">
						<form:option value="">Select Subject</form:option>
						<form:option value="Project">Project</form:option>
						<form:option value="Module 4 - Project">Module 4 - Project</form:option>
					</form:select>
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-primary" formaction="searchProjectSubmission">Search Submissions</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
			</div>

		</fieldset>
		</form:form>
		
		</div>
	
	
	<c:choose>
<c:when test="${rowCount > 0}">

	<legend>&nbsp;Project Details & Files<font size="2px"> (${rowCount} Records Found)&nbsp; <a href="downloadProjectSubmittedExcel">Download Submission Excel</a></font></legend>
	<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Student ID</th>
								<th>File</th>
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="assignmentFile" items="${assignmentFilesList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
								<td><c:out value="${assignmentFile.year}"/></td>
								<td><c:out value="${assignmentFile.month}"/></td>
								<td><c:out value="${assignmentFile.sapId}"/></td>
								<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}">Download</a></td>
								<td></td>
					        </tr>   
					    </c:forEach>
							
						</tbody>
					</table>
	</div>
	<br>

</c:when>
</c:choose>

<c:url var="firstUrl" value="searchProjectSubmissionPage?pageNo=1" />
<c:url var="lastUrl" value="searchProjectSubmissionPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchProjectSubmissionPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchProjectSubmissionPage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="searchProjectSubmissionPage?pageNo=${i}" />
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

	  <jsp:include page="../footer.jsp" />


</body>
</html>
