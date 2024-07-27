<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search Faculty Unavailability Dates" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Faculty Unavailability Dates</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchFacultyUnavailability" method="post" modelAttribute="searchBean">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="true" itemValue="${searchBean.year}">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" itemValue="${searchBean.month}">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="facultyId" path="facultyId" type="text" placeholder="Faculty ID" class="form-control"  value="${searchBean.facultyId}"/>
					</div>
					
					<div class="form-group">
							<form:input id="unavailabilityDate" path="unavailabilityDate" type="date" placeholder="Date" 
							class="form-control"  value="${searchBean.unavailabilityDate}"/>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchFacultyUnavailability">Search</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
					
			</div>
			
			</div>
			</fieldset>
		</form:form>
		
		<c:choose>
	<c:when test="${rowCount > 0}">

	<legend>&nbsp;Faculty Unavailability Dates<font size="2px"> (${rowCount} Records Found) &nbsp; </font></legend>
	<div class="table-responsive">
	<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Year</th>
							<th>Month</th>
							<th>Faculty ID</th>
							<th>Unavailable Date</th>
							<th>Actions</th>
						</tr>
					</thead>
						<tbody>
						
						<c:forEach var="bean" items="${facultyUnavailabilityList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}" /></td>
					            <td><c:out value="${bean.year}" /></td>
					            <td><c:out value="${bean.month}" /></td>
								<td><c:out value="${bean.facultyId}" /></td>
								<td><c:out value="${bean.unavailabilityDate}" /></td>
								<td> 
						            <c:url value="editFacultyUnavailability" var="editurl">
									  <c:param name="id" value="${bean.id}" />
									  <c:param name="facultyId" value="${bean.facultyId}" />
									  <c:param name="unavailabilityDate" value="${bean.unavailabilityDate}" />
									</c:url>
									<c:url value="deleteFacultyUnavailability" var="deleteurl">
									  <c:param name="id" value="${bean.id}" />
									</c:url>
									<%if(roles.indexOf("Acads Admin") != -1 ){ %>
									<a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a>&nbsp;
						 			<a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa fa-trash-o fa-lg"></i></a> 
									<%} %>
									
					            </td>
					            
					            
					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	<br>

	</c:when>
	</c:choose>
	
	<c:url var="firstUrl" value="searchFacultyUnavailabilityPage?pageNo=1" />
	<c:url var="lastUrl" value="searchFacultyUnavailabilityPage?pageNo=${page.totalPages}" />
	<c:url var="prevUrl" value="searchFacultyUnavailabilityPage?pageNo=${page.currentIndex - 1}" />
	<c:url var="nextUrl" value="searchFacultyUnavailabilityPage?pageNo=${page.currentIndex + 1}" />
	
	
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
	            <c:url var="pageUrl" value="searchFacultyUnavailabilityPage?pageNo=${i}" />
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
