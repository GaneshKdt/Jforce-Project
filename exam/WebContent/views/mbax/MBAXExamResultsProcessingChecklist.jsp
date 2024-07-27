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
	<jsp:param value="MBA-X Results Processing Checklist" name="title" />
</jsp:include>

<body>
	<%@ include file="../header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> MBA-X Exam Results Processing Checklist </legend>
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
												<td>1. <a href="/exam/admin/mbaxExamAssessmentsPanelForm"
													target="_blank">Add TEE Mettl Assessment</a> / 
													<a href="/exam/admin/mbaxExamAssessmentsDetails" target="_blank">Extend Exam Assessment End Time</a>
												</td>
												<td>Click Link To Add Assessment</td>
											</tr>
											<tr>
												<td>2.<a href="/exam/admin/mbaxExamScheduleCreationForm" target="_blank">Click Link to Add Schedule and Register Students</a>
												</td>
												<td>Click Link To Add Assessment</td>
											</tr>
											 <tr>
												<td>3.<a href="/exam/admin/uploadMBAXRegistrationFailedForm" target="_blank">Upload Registration Failed Student</a>
												</td>
												<td>Click Link to Upload registration failed students</td>
											</tr> 
											<tr>
												<td>3. 
													<a href="/exam/admin/readMettlMarksFromAPIFormMBAX" target="_blank">Pull Mettl Results</a>
													/ <a href="/exam/admin/uploadProjectMarksFormMBAX" target="_blank">Upload Project Marks</a>
												</td>
												<td>Click Link To Retrive Mettl Results.</td>
											</tr>
											 <!-- <tr>
												<td>1. <a href="/exam/uploadMBAXMarksForm"
													target="_blank">Upload MBAX TEE Scores</a>
												</td>
												<td>TEE scores are uploaded schedule wise.</td>
											</tr> -->
										<!--	<tr>
												<td>2. <a href="/exam/readMBAXMettlMarksFromAPIForm"
													target="_blank">Pull Upgrad TEE Results</a></td>
												<td>...</td>
											</tr> -->
											 <tr>
												<td>4. <a href="/exam/admin/viewMettlScoresMBAXForm"
													target="_blank">View Scores</a></td>
												<td>View and Download TEE scores.</td>
											</tr>
											<tr>
												
												<td>5. <a href="/exam/admin/markRIANVCasesMBAXForm"
													target="_blank">Mark RIA/NV Cases</a></td>
												<td>Student wise mark RIA/NV.</td>
											</tr>
											 
											<tr>
												<td>6. <a href="/exam/admin/uploadUpgradAbsentListForm"
													target="_blank">Upload TEE Exam Absent Students List</a></td>
												<td>Absent list is generated based on subject and batch selected.</td>
											</tr>
											<tr>
												<td>7. <a href="/exam/admin/mbaxPassFailTriggerForm"
													target="_blank">Pass Fail Trigger</a></td>
												<td>Run passfail trigger for every schedule.</td>
											</tr>
											<tr>
												<td>8. 
													<a href="/exam/admin/mbaxGraceMarksForm" target="_blank">Generate Grace (2-marks) </a>
													/ <a href="/exam/admin/projectGraceMarksFormMBAX" target="_blank">Generate Grace For Project (2-marks) </a>
												</td>
												<td>Mark Grace for every schedule. Do not run passfail trigger after grace is applied.</td>
											</tr>
											<tr>
												<td>9. <a href="/exam/admin/mbaxAbsoluteGradingForm"
													target="_blank">Generate Absolute Grading</a></td>
												<td>...</td>
											</tr>
											<tr>
												<td>10. 
													<a href="/exam/admin/mbaxPassFailMakeLiveForm" target="_blank">Make Live</a>
													/ <a href="/exam/admin/projectPassFailMakeLiveMBAXForm" target="_blank">Make Live For Project</a>
												</td>
												<td>Results are made live schedule wise.</td>
											</tr>

											 <tr>
												<td>11. <a href="/exam/admin/mbaxPassFailReportForm"
													target="_blank"><b>Pass Fail Report</b></a></td>

												<td>...</td>
											</tr>
											 <tr>
												<td>12. 
													<a data-toggle="modal" data-target="#myModal" href="#">Log In As Student</a>
												
													<div class="modal fade" id="myModal" role="dialog">
														<div class="modal-dialog">
												
															<!-- Modal content-->
															<div class="modal-content">
																<div class="modal-header">
																	<button type="button" class="close" data-dismiss="modal">&times;</button>
																	<h4 class="modal-title">Log In As Student</h4>
																</div>
																<div class="modal-body">
																	<div class="panel-content-wrapper">
																		<div>
																			
																			<form id="loginAsStudent">
																				<div class="row">
																					<div class="col-sm-18">
																						<div class="form-group">
																							<label for="studentId">Student Id:</label>
																							<input id="studentId" type="text" class="form-control" placeholder = "Student Number" />
																						</div>
																					</div>
																				</div>
																				<button id="submitBtn" class="btn btn-primary" type="submit">Log in as student</button>
																			</form>
																		</div>
																	</div>
																</div>
																<div class="modal-footer">
																	<button type="button" class="btn btn-default"
																		data-dismiss="modal">Close</button>
																</div>
															</div>
												
														</div>
													</div>
												</td>

												<td>...</td>
											</tr>								
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</section>	
	<jsp:include page="../footer.jsp" />
	
	<script>
		$('#loginAsStudent').on('submit', function(e) {
			e.preventDefault();
			let studentId = $('#studentId').val();
			if(studentId.length != 11) {
				alert('Invalid student number! Please try again!')
				return;
			}
			window.open('/ssoservices/mbax/login?sapid=' + studentId, '_blank').focus();
	    });
	</script>
</body>
</html>
