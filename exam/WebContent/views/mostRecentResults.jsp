<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Exam Data Management" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="limitedAccessHeader.jsp"%>
	
    <section class="content-container">


					<legend>&nbsp;Most Recent Marks <font size="2px">(${size} Records Found)</font></legend>
					
					<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								
								<th>GR No.</th>
								<th>SAP ID</th>
								<th>Student Name</th>
								<th>Program</th>
								<th>Sem</th>
								<th>Subject</th>
								<th>Written</th>
								<th>Assign.</th>
								<th>Grace</th>
								<th>Total</th>
								<th>Attempt</th>
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="studentMarks" items="${studentMarksList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
								<td><c:out value="${studentMarks.year}"/></td>
								<td><c:out value="${studentMarks.month}"/></td>
								
								<td><c:out value="${studentMarks.grno}"/></td>
								<td><c:out value="${studentMarks.sapid}"/></td>
								<td nowrap="nowrap"><c:out value="${studentMarks.studentname}"/></td>
								<td><c:out value="${studentMarks.program}"/></td>
								<td><c:out value="${studentMarks.sem}"/></td>
								<td nowrap="nowrap"><c:out value="${studentMarks.subject}"/></td>
								<td><c:out value="${studentMarks.writenscore}"/></td>
								<td><c:out value="${studentMarks.assignmentscore}"/></td>
								<td><c:out value="${studentMarks.gracemarks}"/></td>
								<td><c:out value="${studentMarks.total}"/></td>
								<td><c:out value="${studentMarks.attempt}"/></td>
								<!-- <td><c:out value="${studentMarks.source}"/></td>
								<td><c:out value="${studentMarks.location}"/></td>
								<td><c:out value="${studentMarks.centercode}"/></td>
								<td><c:out value="${studentMarks.remarks}"/></td>  
   
					            <td> 
						            <c:url value="editStudentMarks" var="editurl">
									  <c:param name="id" value="${studentMarks.id}" />
									</c:url>
									<c:url value="deleteStudentMarks" var="deleteurl">
									  <c:param name="id" value="${studentMarks.id}" />
									</c:url>
									<c:url value="viewStudentMarksDetails" var="studentMarksDetailsUrl">
									  <c:param name="id" value="${studentMarks.id}" />
									</c:url>
									<a class="glyphicon glyphicon-info-sign" href="${studentMarksDetailsUrl}" title="Details"></a>
									<a class="glyphicon glyphicon-pencil" href="${editurl}" title="Edit"></a>
									<a class="glyphicon glyphicon-trash" href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')"></a>
					            </td>-->
					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>

</section>

		


  <jsp:include page="footer.jsp" />

</body>
</html>
