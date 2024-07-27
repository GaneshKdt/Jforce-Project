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
s<jsp:include page="../jscss.jsp">
<jsp:param value="Pending Subjects For Lateral Entries Report" name="title" />
</jsp:include>

        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css"> 


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Pending Subjects For Lateral Entries Report</legend></div>
        <%@ include file="../messages.jsp"%>
		<div class="row clearfix">
		<form:form  action="/exam/admin/pendingSubjectsForLateralEntriesReport" method="post" modelAttribute="studentBean">
			<fieldset>
				<div class="col-md-6 column">
				<div class="form-group">
				<textarea name="sapIdList" cols="50" rows="7" placeholder="Enter different Sapid Ids in new lines">${bean.serviceRequestIdList}</textarea>
				</div>
				
				<div class="form-group">
					<button id="submit" name="submit" class="btn btn-primary" formaction="/exam/admin/pendingSubjectsForLateralEntriesReport">Generate Report</button>
					<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
				</div>
			</div>
		</fieldset>
		</form:form>
		
		</div>
	
	
		<c:if test="${rowCount > 0}">
		
			<legend>&nbsp;Pending Subjects For Lateral Entries<font size="2px"> (${rowCount} Records Found)  &nbsp;&nbsp;&nbsp;
						<a style="color:#0e133b;font-size:20px;" href="/exam/admin/downloadpendingSubjectsForLateralEntriesReport">
						<button style="background-color:#fcfcfc; font-size: 17px;">Download to Excel</button></a>&nbsp; &nbsp; &nbsp; 		
						
			</font>				
			</legend>
			<div class="table-responsive">
			<table class="table table-striped table-hover" id="dataTable" style="font-size:12px">
								<thead>
									<tr> 
										<th>Sapid</th>
										<th>First Name</th>
										<th>Last Name</th>
										<th>Subject</th>
										<th>Sem</th>
										<th>Previous Student Id</th>
										<th>Program</th>
										<th>Program Structure Applicable</th>
										<th>Old Program</th>
										<th>Previous Program Structure Applicable</th> 
									</tr>
								</thead>
								<tbody>
								
								<c:forEach var="student" items="${studentsList}" varStatus="status">
							        <tr>
							            <td><c:out value="${student.sapid}"/></td>
										<td><c:out value="${student.firstName}"/></td>
										<td><c:out value="${student.lastName}"/></td>
										<td><c:out value="${student.subject}"/></td>
										<td><c:out value="${student.sem}"/></td>
										<td><c:out value="${student.previousStudentId}"/></td>
										<td><c:out value="${student.program}"/></td>
										<td><c:out value="${student.prgmStructApplicable }"/></td>
										<td><c:out value="${student.oldProgram }"/></td>
										<td><c:out value="${student.previousPrgmStructApplicable }"/></td>
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

       <script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
       <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
       <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.buttons.min.js"></script>

<script type="text/javascript">

$(document).ready (function(){
       $('#dataTable').DataTable();

});
</script>

</html>
