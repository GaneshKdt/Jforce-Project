<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html class="no-js"> <!--<![endif]-->
   	
    <jsp:include page="jscss.jsp">
	<jsp:param value="Welcome to Academics Portal" name="title" />
		
	</jsp:include>
	<head>
   		 <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
   		 <style>
   		 	.btn-danger {
			    color: #fff !important;
			    background-color: #d9534f !important;
			    border-color: #d43f3a !important;
			}
   		 </style>
   	</head>
    <body class="inside">
	
    <%@ include file="header.jsp"%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
          	<div class="jumbotron" style="padding:10px;margin-top:20px;">
		 		<br/><h2>Session Recording Status</h2> <br/>
		 		<div class="table-responsive">
		 		<table id="dataTable" class="table table-striped ">
		 			<thead>
		 				<td><b>Meeting Id</b></td>
		 				<td><b>Session Id</b></td>
		 				<td><b>Subject</b></td>
		 				<td><b>Date</b></td>
		 				<td><b>Start Time</b></td>
		 				<td><b>End Time</b></td>
		 				<td><b>Status</b></td>
		 				<td><b>Error</b></td>
		 				<td><b>Vimeo Status</b></td>
		 			</thead>
		 			<tbody>
		 				<c:forEach var="session" items="${sessions}">
		 					<tr>
		 						<td><c:out value="${session.meetingId}"/></td>
		 						<td><c:out value="${session.sessionId}"/></td>
		 						<td><c:out value="${session.subject}"/></td>
		 						<td><c:out value="${session.date}"/></td>
		 						<td><c:out value="${session.startTime}"/></td>
		 						<td><c:out value="${session.endTime}"/></td>
		 						<c:if test="${session.status eq 'pending'}">
									<td><span class="btn-warning" style="padding:5px 8px;border-radius:5px"><c:out value="${session.status}"/></span></td>
								</c:if>
								<c:if test="${session.status eq 'initiated'}">
									<td><span class="btn-info" style="padding:5px 8px;border-radius:5px"><c:out value="${session.status}"/></span></td>
								</c:if>
								<c:if test="${session.status eq 'success'}">
									<td><span class="btn-success" style="padding:5px 8px;border-radius:5px"><c:out value="${session.status}"/></span></td>
								</c:if>
								<c:if test="${session.status eq 'failed'}">
									<td><span class="btn-danger" style="padding:5px 8px;border-radius:5px"><c:out value="${session.status}"/></span></td>
								</c:if>
								<td><c:out value="${session.error}"/></td>
								<td><c:out value="${session.vimeoStatus}"/></td>
		 					</tr>
		 				</c:forEach>
		 			</tbody>
		 		</table>
		 		</div>
		 	</div>
        </div> <!-- /container -->
    </section>
    
    
    <jsp:include page="footer.jsp" />
    <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
    <script>
    $(document).ready( function () {
        $('#dataTable').DataTable();
    } );
    </script>
  </body>
</html>
