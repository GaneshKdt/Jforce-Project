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

<jsp:include page="../jscss.jsp">
	<jsp:param value="UFM Checklist" name="title" />
</jsp:include>

<body>
	<%@ include file="../header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> UFM Checklist </legend>
			</div>

			<%@ include file="../messages.jsp"%>
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
												<td>1. <a href="/exam/admin/uploadUFMShowCauseFileForm"
													target="_blank">Upload Show Cause</a>
												</td>
												<td>Upload Show cause students file</td>
											</tr>
											<tr>
												<td>2. <a href="/exam/admin/uploadUFMActionFileForm"
													target="_blank">Upload Decision File</a></td>
												<td>Upload Action to be taken for students file.</td>
											</tr>
											 <tr>
												<td>3. <a href="/exam/admin/listOfStudentsMarkedForUFMForm"
													target="_blank">View Students Marked for UFM</a></td>
												<td>View and Download Status for students.</td>
											</tr>
											 <tr>
												<td>4. <a href="/exam/admin/ufmIncidentReportForm"
													target="_blank">View Students Incidents Report</a></td>
												<td>View and Download Incidents Report.</td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</section>	
	<jsp:include page="../footer.jsp" />
</body>
</html>
