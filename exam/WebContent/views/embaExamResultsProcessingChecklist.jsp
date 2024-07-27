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
				<legend> EMBA Exam Results Processing Checklist </legend>
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
												<td>1. <a href="/exam/admin/examAssessmentPanelForm"
													target="_blank">Add TEE Mettl Assessment</a> / 
													<a href="/exam/admin/examAssessmentDetails"
													target="_blank">Extend Exam Assessment End Time</a>
												</td>
												<td>Click Link To Add Assessment</td>
											</tr>
											<tr>
												<td>
												2. <a href="/exam/admin/scheduleCreationForm" target="_blank">Add TEE Mettl Schedule And Register Students</a>
												</td>
												<td>Click Link to Add Schedule and Register Students</td>
											</tr>
											<tr>
												<td>
												3.  <a href="/exam/admin/uploadRegistrationFailedMettlForm" target="_blank">Upload Registration Failed Students</a>
												</td>
												<td>Click Link to Upload registration failed students</td>
											</tr>
											<tr>
												<td>4.
													<a href="/exam/admin/readMettlMarksFromAPIForm" target="_blank">Pull Mettl Results</a>
													/ 
													<a href="/exam/admin/uploadProjectMarksFormMBAWX" target="_blank">Upload Project Marks</a>
													/ 
													<a href="/exam/admin/uploadCapstoneProjectMarksForm" target="_blank">Upload Capstone Project Marks</a>
												</td>
												<td>Click Link To Retrive Mettl Results. / Upload Project Marks</td>
											</tr>
											<tr>
												<!-- href="/exam/uploadTEEMarksForm" -->
												<td>5. <a href="/exam/admin/markRIANVCasesEmbaForm"
													target="_blank">Mark RIA/NV Cases</a></td>
												<td>Click Link to Mark RIA/NV</td>
											</tr>
											<tr>
												<td>6. <a href="/exam/admin/viewMettlScoresForm"
													target="_blank">View TEE Marks</a></td>
												<td>Click Link to View and Download Mettl Scores</td>
											</tr>
											<tr>
												<td>7. 
													<a href="/exam/admin/insertABRecordsFormMBAWX" target="_blank">Upload TEE Exam Absent Students List</a>
												</td>
												<td>Click Link to Upload TEE Exam Absent Students List</td>
											</tr>
											<tr>
												<td>8. 
													<a href="/exam/admin/embaPassFailTriggerForm" target="_blank">Pass Fail Trigger</a>
												</td>
												<td>Click Link to Pass Fail Trigger</td>
											</tr>
											<tr>
												<td>9.
													<a href="/exam/admin/embaGraceMarksForm" target="_blank">Generate Grace (2-marks) </a>
													/
													<a href="/exam/admin/embaProjectGraceMarksForm" target="_blank">Generate Grace For Project (2-marks) </a>
												</td>
												<td>Click Link to Generate Grace (2-marks) Report and Apply Grace</td>
											</tr>
											<tr>
												<td>10. <a href="/exam/admin/embaAbsoluteGradingForm"
													target="_blank">Generate Absolute Grading</a></td>
												<td>Click Link to Generate Absolute Grading</td>
											</tr>
											<tr>
												<td>11. 
													<a href="/exam/admin/embaPassFailMakeLiveForm" target="_blank">Make Live</a>
													/
													<a href="/exam/admin/embaProjectPassFailMakeLiveForm" target="_blank">Make Live For Project</a>
												</td>
												<td>Activate Results Live Flag</td>
											</tr>
											<tr>
												<td>12. <a href="/studentportal/loginAsForm"
													target="_blank">Login As Student</a></td>
												<td>Click Link to Login As Student to Verify Results</td>
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
