<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Search Session Day and Time" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Session Day and Time</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchSessionDayTime" method="post" modelAttribute="searchBean">
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
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchSessionDayTime">Search</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
					
			</div>
			
			</div>
			</fieldset>
		</form:form>
		
	<c:choose>
	<c:when test="${rowCount > 0}">

	<legend>&nbsp;Session Days and Time<font size="2px"> (${rowCount} Records Found) &nbsp; </font></legend>
	<div class="table-responsive">
	<table class="table table-striped" style="font-size:12px">
			<thead>
			<tr>
				<th>Sr. No.</th>
				<th>Year</th>
				<th>Month</th>
				<th>Day</th>
				<th>Session Start Time</th>
				<th>Actions</th>
			</tr>
			</thead>
			<tbody>
			
			<c:forEach var="bean" items="${sessionDayTimeList}" varStatus="status">
		        <tr>
		            <td><c:out value="${status.count}" /></td>
		            <td><c:out value="${bean.year}" /></td>
		            <td><c:out value="${bean.month}" /></td>
					<td><c:out value="${bean.day}" /></td>
					<td><c:out value="${bean.startTime}" /></td>
					<td> 
			            <c:url value="editSessionDayTime" var="editurl">
						  <c:param name="id" value="${bean.id}" />
						</c:url>
						<c:url value="deleteSessionDayTime" var="deleteurl">
						  <c:param name="id" value="${bean.id}" />
						</c:url>
						<%if(roles.indexOf("Acads Admin") != -1 ){ %>
						<a href="${editurl}" title="Edit"><i class="fa-solid fa-pen-to-square fa-lg"></i></a>&nbsp;
						 <a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"><i class="fa-regular fa-trash-can fa-lg"></i></a>
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
	
	
	</div>
	

	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
