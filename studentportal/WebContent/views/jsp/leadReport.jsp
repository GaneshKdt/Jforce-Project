<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Send Email" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Lead Report</legend>
			</div>

			<%@ include file="messages.jsp"%>

			<div class="panel-body">
				<div>
					<table class='table table-striped table-hover' id='leadReport' style='padding:10px;'>
						 <thead> 
							 <tr> 
							 	<th>Sr No</th>
							 	<th>Location</th>
							 	<th>Lead Id</th>
							 	<th>Registration Id</th>
							 	<th>Email Id</th>
							 	<th>First Name</th>
							 	<th>Last Name</th>
							 	<th>Mobile Number</th>
							 	<th>Program</th>
							 	<th>Created Date</th>
							 </tr>
						 </thead>
						 <tbody>
							<c:set var="count" value="1"></c:set>
							<c:forEach var='lead' items="${ leadList }">
								<tr>
							 		<td><c:out value="${ count }"></c:out></td>
							 		<td>${ lead.location }</td>
							 		<td>${ lead.leadId }</td>
							 		<td>${ lead.registrationId }</td>
							 		<td>${ lead.emailId }</td>
							 		<td>${ lead.firstName }</td>
							 		<td>${ lead.lastName }</td>
							 		<td>${ lead.mobile }</td>
							 		<td>${ lead.program }</td>
							 		<td>${ lead.createdDate }</td>
							 	</tr>
							<c:set var="count" value="${ count+1 }"></c:set>
							</c:forEach>
						 </tbody>
					 </table>
				</div>
				
				<div>
					<a class='btn btn-primary' href="/studentportal/Lead_Report">Download Report</a>
				</div>
				 
			</div>

		</div>

	</section>

	<jsp:include page="footer.jsp" />
	
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.21/css/jquery.dataTables.css">
	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.10.21/js/jquery.dataTables.js"></script>
	<script type="text/javascript">
		$('#leadReport').DataTable();
	</script>
</body>
</html>
