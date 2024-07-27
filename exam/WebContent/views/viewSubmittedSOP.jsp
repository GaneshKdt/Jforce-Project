<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Student Guide Mapping" name="title" />
</jsp:include>
<link rel="stylesheet" href="//cdn.datatables.net/1.10.20/css/jquery.dataTables.min.css"/>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Student SOP Submission</legend></div>
        <%@ include file="messages.jsp"%>
			<div class="clearfix" style="background-color:white;padding:10px;">
				<%-- <div>
					<table class="table" id="table">
						<thead>
							<th>Sapid</th>
							<th>year</th>
							<th>month</th>
							<th>submitted date</th>
							<th>action</th>
						</thead>
						<tbody>
							<c:forEach var="uploadProjectSOPBean" items="${ uploadProjectSOPBeanList }">
								<tr>
									<td>${ uploadProjectSOPBean.sapId }</td>
									<td>${ uploadProjectSOPBean.year }</td>
									<td>${ uploadProjectSOPBean.month }</td>
									<td>${ uploadProjectSOPBean.updated_at }</td>
									<td>
									    <form method="POST" action="viewStudentSubmittedSOP">
											<input type="hidden" name="sapId" value="${ uploadProjectSOPBean.sapId }" />
											<input type="hidden" name="year" value="${ uploadProjectSOPBean.year }" />
											<input type="hidden" name="month" value="${ uploadProjectSOPBean.month }" />		
										<button class="btn btn-sm btn-primary">review</button>   
									    </form> 
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div> --%>
				<ul class="nav nav-tabs">
				  <li class="active"><a data-toggle="tab" href="#NotEvaluated">Not Evaluated</a></li>
				  <li><a data-toggle="tab" href="#Evaluated">Evaluated</a></li>
				</ul>
				
				<div class="tab-content">
				  <div id="NotEvaluated" class="tab-pane fade in active">
					<div>
						<table class="table" id="table">
							<thead>
								<th>Sapid</th>
								<th>year</th>
								<th>month</th>
								<th>submitted date</th>
								<th>action</th>
							</thead>
							<tbody>
								<c:forEach var="uploadProjectSOPBean" items="${ uploadProjectSOPBeanList }">
									<c:if test="${ uploadProjectSOPBean.status == 'Submitted'}">
									<tr>
										<td>${ uploadProjectSOPBean.sapId }</td>
										<td>${ uploadProjectSOPBean.year }</td>
										<td>${ uploadProjectSOPBean.month }</td>
										<td>${ uploadProjectSOPBean.created_at }</td>
										<td>
											<form method="POST" action="viewStudentSubmittedSOP">
											<input type="hidden" name="sapId" value="${ uploadProjectSOPBean.sapId }" />
											<input type="hidden" name="year" value="${ uploadProjectSOPBean.year }" />
											<input type="hidden" name="month" value="${ uploadProjectSOPBean.month }" />		
										<button class="btn btn-sm btn-primary">review</button>   
									    </form>
										</td>
									</tr>
									</c:if>
								</c:forEach>
							</tbody>
						</table>
					</div>
				  </div>
				  <div id="Evaluated" class="tab-pane fade">
				    <div>
						<table class="table" id="table2">
							<thead>
								<th>Sapid</th>
								<th>year</th>
								<th>month</th>
								<th>submitted date</th>
								<th>action</th>
							</thead>
							<tbody>
								<c:forEach var="uploadProjectSOPBean" items="${ uploadProjectSOPBeanList }">
									<c:if test="${ uploadProjectSOPBean.status == 'Approved' || uploadProjectSOPBean.status == 'Rejected' }">
									<tr>
										<td>${ uploadProjectSOPBean.sapId }</td>
										<td>${ uploadProjectSOPBean.year }</td>
										<td>${ uploadProjectSOPBean.month }</td>
										<td>${ uploadProjectSOPBean.created_at }</td>
										<td>
											<form method="POST" action="viewStudentSubmittedSOP">
											<input type="hidden" name="sapId" value="${ uploadProjectSOPBean.sapId }" />
											<input type="hidden" name="year" value="${ uploadProjectSOPBean.year }" />
											<input type="hidden" name="month" value="${ uploadProjectSOPBean.month }" />		
										<button class="btn btn-sm btn-primary">review</button>   
									    </form>
										</td>
									</tr>
									</c:if>
								</c:forEach>
							</tbody>
						</table>
					</div>
				  </div>
				</div>
			</div>
		</div><br/>
	</section>
	<jsp:include page="footer.jsp" />
	<script src="//cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js">
	</script>
	<script>
	$(document).ready(function(){
		$('#table').DataTable();
	});
	</script>
</body>
</html>
		