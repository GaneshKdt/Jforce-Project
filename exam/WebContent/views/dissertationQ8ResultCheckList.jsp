<!DOCTYPE html>
<%@page import="java.util.*"%>

<html lang="en">

<head>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
</head>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Exam Results Processing Checklist" name="title" />
</jsp:include>

<body>
	<%@ include file="header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> Master Dissertation Q8 Results Processing Checklist </legend>
			</div>

			<%@ include file="messages.jsp"%>
						<div class="panel-body">
							
							<div class="col-md-18 column">

								<div class="table-responsive">
									<table class="table table-striped" style="font-size: 12px">
										<thead>
											<tr>
												<th>Link</th>
												<th>Description</th>
											</tr>
										</thead>
										<tbody>
											<tr>											
												<td>1. <a href="/exam/admin/dissertationQ8InsertMarksForm"
													target="_blank">Insert Marks</a> 
												</td>
												<td>Click Link To Insert Marks </td>
											</tr>
											<tr>
												<td>
												2. <a href="/exam/admin/dissertationQ8StagingForm" target="_blank">Pass Fail Trigger</a>
												</td>
												<td>Click Link To Transfer Marks To Staging </td>
											</tr>
											<tr>
												<td>
												3.  <a href="/exam/admin/disseratationGraceForm" target="_blank">Apply Grace</a>
												</td>
												<td>Click Link to Apply Grace</td>
											</tr>
											<tr>
												<td>
												4.  <a href="/exam/admin/disseratationQ8GradeForm" target="_blank">Calculate Grade</a>
												</td>
												<td>Click Calculate Grade</td>
											</tr>
											<tr>
											<tr>
												<td>
												5.  <a href="/exam/admin/dissertationQ8PassFailForm" target="_blank">Transfer Staging to PassFail</a>
												</td>
												<td>Click Link to transfer marks from Staging to Passfail</td>
											</tr>
											<tr>
											
												<td>
												6.  <a href="/exam/admin/dissertationQ8MakeLiveForm" target="_blank">Make Live</a>
												</td>
												<td>Click Link to Make Result live</td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</section>	
	<jsp:include page="footer.jsp" />
</body>
</html>
