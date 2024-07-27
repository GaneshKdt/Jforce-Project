<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<%@page import="com.nmims.beans.ReRegProbabilityBean"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.SessionDayTimeStudentPortal"%>
<%@page import="com.nmims.beans.PassFailBean"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
 <%@page import="com.nmims.beans.SessionQueryAnswerStudentPortal"%>


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Student Details" name="title" />
</jsp:include>


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Sessions Calendar" name="title" />
</jsp:include>

<%
List<SessionQueryAnswerStudentPortal> studentQueries = (ArrayList<SessionQueryAnswerStudentPortal>)request.getAttribute("studentQueries");

StudentStudentPortalBean studentDetails = (StudentStudentPortalBean)request.getAttribute("studentDetails");

ArrayList<PassFailBean> passFailList = (ArrayList<PassFailBean>)request.getAttribute("passFailList");
ArrayList<SessionDayTimeStudentPortal> scheduledSessionListForStudentDetailDashBoard = (ArrayList<SessionDayTimeStudentPortal>)request.getAttribute("scheduledSessionListForStudentDetailDashBoard");
ArrayList<StudentMarksBean> markRecordsList = (ArrayList<StudentMarksBean>)request.getAttribute("markRecordsList");
ArrayList<StudentMarksBean> getAllRegistrationsFromSapIdForStudentDetailDashBoard = (ArrayList<StudentMarksBean>)request.getAttribute("getAllRegistrationsFromSapIdForStudentDetailDashBoard");

Calendar cal = Calendar.getInstance();
String monthYear = new SimpleDateFormat("MMM YYYY").format(cal.getTime());
ReRegProbabilityBean reRegProbability = (ReRegProbabilityBean)request.getAttribute("reRegProbability");

String probabilityColor = "";
if(reRegProbability !=null){
	double probability = reRegProbability.getReRegProbability();
	if(probability >= 40.0){
		probabilityColor = "lightgreen";
	}else if(probability > 10.0 && probability < 40.0){
		probabilityColor = "yellow";
	}else{
		probabilityColor = "red";
}
}

%>

<body class="inside">
 
	<section class="content-container login">
		<div class="container-fluid customTheme">
		
	
			<div class="row">
				<legend>Student Details</legend>
			</div>
    
			<%@ include file="../messages.jsp"%>


			<%if(studentDetails !=null){ %>
			
		
			
			<div class="row">

				<div class="col-md-18 column">
					<div id="tabs">
						<ul>
							<li><a href="#tabs-1">Student Details</a></li>
							<li><a href="#tabs-2">Academics</a></li>
							<li><a href="#tabs-3">Exam</a></li>
							<li><a href="#tabs-4">Service Request</a></li>
							<li><a href="#tabs-5">Student Communications</a></li>
							<li><a href="#tabs-6">Login Logs</a></li>
							<li><a href="#tabs-7">Student Queries</a></li>
							
						</ul>
						<div id="tabs-1">
							<%if(studentDetails!=null){ %>
							<div class="col-md-5">
								<p>
									Name :
									<%=studentDetails.getFirstName()%>
									<%=studentDetails.getLastName() %></p>
								<p>
									Program Structure :
									<%=studentDetails.getPrgmStructApplicable() %></p>
								<p>
									Program :
									<%=studentDetails.getProgram()%></p>
							</div>
							<div class="col-md-4">
								<p>
									Validity End Month-Year :
									<%=studentDetails.getValidityEndMonth()%>-<%=studentDetails.getValidityEndYear()%></p>
								<p>
									Enrollment Month-Year :
									<%=studentDetails.getEnrollmentMonth()%>-<%=studentDetails.getEnrollmentYear()%></p>
								<p style="background-color:<%=probabilityColor%>">Re-Reg
									probability : ${reRegProbability.reRegProbability }%</p>
							</div>
							<%}else{ %>

							<p>Student Details Not Found</p>
							<%} %>
							<p></p>
							<div class="clearfix"></div>
							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" href="#collapseStudentReg">Student
												Registration Details</a> <span style="float: right;">Session
												Attendance Percentage : ${displayPercentage}% </span>
										</h4>
									</div>
									<div id="collapseStudentReg" class="panel-collapse collapse">
										<c:if test="${fn:length(getAllRegistrationsFromSapIdForStudentDetailDashBoard) > 0}">
											<table class="table table-hover">
												<thead>
													<tr>
														<th>Year</th>
														<th>Month</th>
														<th>Semester</th>
														<th>Program</th>
														<th>Registered Date</th>
														<th>Passed</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="registration"
														items="${getAllRegistrationsFromSapIdForStudentDetailDashBoard}">
														<tr>
															<td><c:out value="${registration.year}" /></td>
															<td><c:out value="${registration.month}" /></td>
															<td><c:out value="${registration.sem}" /></td>
															<td><c:out value="${registration.program}" /></td>
															<td><c:out value="${registration.createdDate}" /></td>
															<td><c:out value="${registration.passFailStatus}" /></td>
														</tr>
													</c:forEach>
												</tbody>

											</table>
										</c:if>
									</div>
								</div>
							</div>
						</div>

						<div id="tabs-2">
							<%-- <div class="panel-group">
					           <div class="panel panel-default">
								    <div class="panel-heading">
								      <h4 class="panel-title">
						  				PCP BOOKING COUNT :
						  				 <c:if test="${pcpBookingCount > 0}">
						  				 	 ${pcpBookingCount}
						  				 </c:if>
						  				 <c:if test="${pcpBookingCount eq 0 || pcpBookingCount eq null}">
						  				 	 0
						  				 </c:if>
						  			  </h4>
						  			 </div>
						  		</div>
						  </div> --%>

							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" href="#collapseScheduleSession">TOTAL
												NUMBER OF SESSIONS ATTENDED : <c:if
													test="${fn:length(attendedSessionListForStudentDetailDashBoard) > 0}">
						  				 	 ${fn:length(attendedSessionListForStudentDetailDashBoard)}
							  				 </c:if> <c:if
													test="${fn:length(attendedSessionListForStudentDetailDashBoard) eq 0}">
							  				 	 0
							  				 </c:if>
											</a>
											<%-- 
						 				TOTAL NUMBER OF SESSIONS ATTENDED : 
						 				<c:if test="${numberOfSessionsAttended > 0}">
						  				 	 ${numberOfSessionsAttended}
						  				 </c:if>
						  				 <c:if test="${numberOfSessionsAttended eq 0 || numberOfSessionsAttended eq null }">
						  				 	 0
						  				 </c:if> --%>
										</h4>
									</div>


									<div id="collapseScheduleSession"
										class="panel-collapse collapse">
										<c:if
											test="${fn:length(attendedSessionListForStudentDetailDashBoard) > 0}">
											<table class="table">
												<thead>
													<tr>
														<th>Subject Name</th>
														<th>Session Name</th>
														<th>Faculty ID</th>
														<th>Date</th>
														<th>Start Time</th>
														<th>End Time</th>
													</tr>
												</thead>
												<tbody>
													<tr>
														<c:forEach var="sessionBean"
															items="${attendedSessionListForStudentDetailDashBoard}">
															<tr>
																<td><c:out value="${sessionBean.subject}" /></td>
																<td><c:out value="${sessionBean.sessionName}" /></td>
																<td><c:out value="${sessionBean.facultyId}" /></td>
																<td><c:out value="${sessionBean.date}" /></td>
																<td><c:out value="${sessionBean.startTime}" /></td>
																<td><c:out value="${sessionBean.endTime}" /></td>
															</tr>
														</c:forEach>
													</tr>

												</tbody>
											</table>
										</c:if>
									</div>

								</div>
							</div>

							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" href="#collapseScheduleSession">SCHEDULED
												SESSIONS : <c:if
													test="${fn:length(scheduledSessionListForStudentDetailDashBoard) > 0}">
						  				 	 ${fn:length(scheduledSessionListForStudentDetailDashBoard)}
							  				 </c:if> <c:if
													test="${fn:length(scheduledSessionListForStudentDetailDashBoard) eq 0}">
							  				 	 0
							  				 </c:if>
											</a>
										</h4>
									</div>
									<div id="collapseScheduleSession"
										class="panel-collapse collapse">
										<c:if
											test="${fn:length(scheduledSessionListForStudentDetailDashBoard) > 0}">
											<table class="table">
												<thead>
													<tr>
														<th>Subject Name</th>
														<th>Session Name</th>
														<th>Faculty Full Name</th>
														<th>Date</th>
														<th>Start Time</th>
														<th>End Time</th>
													</tr>
												</thead>
												<tbody>
													<tr>
														<c:forEach var="sessionBean"
															items="${scheduledSessionListForStudentDetailDashBoard}">
															<tr>
																<td><c:out value="${sessionBean.subject}" /></td>
																<td><c:out value="${sessionBean.sessionName}" /></td>
																<td><c:out
																		value="${sessionBean.firstName}  ${sessionBean.lastName}" /></td>
																<td><c:out value="${sessionBean.date}" /></td>
																<td><c:out value="${sessionBean.startTime}" /></td>
																<td><c:out value="${sessionBean.endTime}" /></td>
															</tr>
														</c:forEach>
													</tr>

												</tbody>
											</table>
										</c:if>
									</div>
								</div>
							</div>

							<p></p>

							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" href="#collapseSessionAttendance">SESSION
												QUERY DETAILS : <c:if
													test="${fn:length(sessionAttendedDetailList) > 0}">
						  				 	 ${fn:length(sessionAttendedDetailList)}
						  				 </c:if> <c:if test="${fn:length(sessionAttendedDetailList) eq 0}">
						  				 	 0
						  				 </c:if>
											</a>
										</h4>
									</div>
									<div id="collapseSessionAttendance"
										class="panel-collapse collapse">
										<c:if test="${fn:length(sessionAttendedDetailList) > 0}">
											<table class="table">
												<thead>
													<tr>
														<th>Subject Name</th>
														<th>Query</th>
														<th>Answer</th>
														<th>Faculty ID</th>
														<th>Session ID</th>

													</tr>
												</thead>
												<tbody>
													<tr>
														<c:forEach var="sessionBean"
															items="${sessionAttendedDetailList}">
															<tr>
																<td><c:out value="${sessionBean.subject}" /></td>
																<td><c:out value="${sessionBean.query}" /></td>
																<td><c:out value="${sessionBean.answer}" /></td>
																<td><c:out value="${sessionBean.facultyId}" /></td>
																<td><c:out value="${sessionBean.sessionId}" /></td>

															</tr>
														</c:forEach>
													</tr>

												</tbody>
											</table>
										</c:if>
									</div>
								</div>
							</div>
						</div>

						<div id="tabs-3">
							<p>
								<b>EXAM DETAILS</b>
							</p>
							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" href="#collapse1">Mark Records
												: <c:if test="${fn:length(markRecordsList) > 0}">
						  				 	 ${fn:length(markRecordsList)}
							  				 </c:if> <c:if test="${fn:length(markRecordsList) eq 0}">
							  				 	 0
							  				 </c:if>
											</a>
										</h4>
									</div>
									<div id="collapse1" class="panel-collapse collapse">
										<div class="panel-body">
											<table class="table table-hover">
												<thead>
													<tr>
														<th>Year</th>
														<th>Month</th>
														<th>Subject</th>
														<th>Semester</th>
														<th>Written Score</th>
														<th>Asignment Score</th>
														<th>Total Score</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="marks" items="${markRecordsList}">
														<tr>
															<td><c:out value="${marks.year}" /></td>
															<td><c:out value="${marks.month}" /></td>
															<td><c:out value="${marks.subject}" /></td>
															<td><c:out value="${marks.sem}" /></td>
															<td><c:out value="${marks.writenscore}" /></td>
															<td><c:out value="${marks.assignmentscore}" /></td>
															<td><c:out value="${marks.total}" /></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>

							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" href="#collapse2">Pass Fail
												Records : <c:if test="${fn:length(passFailList) > 0}">
						  				 	 	${fn:length(passFailList)} (${numberOfPassSubjects} Passed, ${numberOfFailSubjects } Failed)
							  				 </c:if> <c:if test="${fn:length(passFailList) eq 0}">
							  				 	 0
							  				 </c:if>
											</a>
										</h4>
									</div>
									<div id="collapse2" class="panel-collapse collapse">
										<div class="panel-body">
											<table class="table table-hover">
												<thead>
													<tr>
														<th>Subject</th>
														<th>Semester</th>
														<th>Written Score</th>
														<th>Assignment Score</th>
														<th>Total Score</th>
														<th>Pass Fail Status</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="passFail" items="${passFailList}">
														<tr>
															<td><c:out value="${passFail.subject}" /></td>
															<td><c:out value="${passFail.sem}" /></td>
															<td><c:out value="${passFail.writtenscore}" /><sub>(${passFail.writtenMonth}-${passFail.writtenYear})</sub></td>
															<td><c:out value="${passFail.assignmentscore}" /><sub>(${passFail.assignmentMonth}-${passFail.assignmentYear})</sub></td>
															<td><c:out value="${passFail.total}" /></td>
															<td><c:out value="${passFail.isPass}" /></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>

							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" href="#collapse3">Exam Bookings
												: <c:if test="${fn:length(examBookingsList) > 0}">
						  				 	 	${fn:length(examBookingsList)}
							  				 </c:if> <c:if test="${fn:length(examBookingsList) eq 0}">
							  				 	 0
							  				 </c:if>
											</a>
										</h4>
									</div>
									<div id="collapse3" class="panel-collapse collapse">
										<div class="panel-body">
											<table class="table table-hover">
												<thead>
													<tr>
														<th>Exam Year</th>
														<th>Exam Month</th>
														<th>Subject</th>
														<th>Semester</th>
														<th>Amount</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="bookingBean" items="${examBookingsList}">
														<tr>
															<td><c:out value="${bookingBean.year}" /></td>
															<td><c:out value="${bookingBean.month}" /></td>
															<td><c:out value="${bookingBean.subject}" /></td>
															<td><c:out value="${bookingBean.sem}" /></td>
															<td><c:out value="${bookingBean.amount}" /></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>

							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" href="#collapse4">ANS Records :
												<c:if test="${fn:length(ansList) > 0}">
						  				 	 	${fn:length(ansList)}
							  				 </c:if> <c:if test="${fn:length(ansList) eq 0}">
							  				 	 0
							  				 </c:if>
											</a>
										</h4>
									</div>
									<div id="collapse4" class="panel-collapse collapse">
										<div class="panel-body">
											<table class="table table-hover">
												<thead>
													<tr>
														<th>Subject Name</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="assignment" items="${ansList}">
														<tr>
															<td><c:out value="${assignment.subject}" /></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>

						</div>
						<div id="tabs-4">
							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse" href="#collapseServiceRequest">SERVICE
												REQUESTS : <c:if
													test="${fn:length(getAllServiceRequestFromSapIDForStudentDetailDashBoard) > 0}">
						  				 	 	${fn:length(getAllServiceRequestFromSapIDForStudentDetailDashBoard)}
							  				 </c:if> <c:if
													test="${fn:length(getAllServiceRequestFromSapIDForStudentDetailDashBoard) eq 0}">
							  				 	 0
							  				 </c:if>
											</a>
										</h4>
									</div>
									<div id="collapseServiceRequest"
										class="panel-collapse collapse">
										<c:if
											test="${fn:length(getAllServiceRequestFromSapIDForStudentDetailDashBoard) > 0}">
											<table class="table table-hover">
												<thead>
												<c:set value="${mapOfActiveSRTypesAndTAT}"
													var="mapOfActiveSRTypesAndTAT" />
													<tr>
														<th>SR.No</th>
														<th>Service Request ID</th>
														<th>Service Request Type</th>
														<th>Service Request Status</th>
														<th>Created Date</th>
														<th>Service Request Closed Date</th>
														<th>Expected Closed Date</th>
														<th>Payment Status</th>
														<th>Amount</th>
														<th>Description</th>
														<th>Documents</th>
														<th>Track ID</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="sr"
														items="${getAllServiceRequestFromSapIDForStudentDetailDashBoard}"
														varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><c:out value="${sr.id}" /></td>
															<td><c:out value="${sr.serviceRequestType}" /></td>
															<td><c:out value="${sr.requestStatus}" /></td>
															<td><c:out value="${sr.createdDate}" /></td>
															<td><c:out value="${sr.requestClosedDate}" /></td>
															<td><c:out value="${sr.requestExpectedClosedDate}"></c:out></td>
															<td><c:out value="${sr.tranStatus}" /></td>
															<td><c:out value="${sr.respAmount}" /></td>
															<td width="30"><c:out value="${sr.description}" /></td>
															<td><c:if test="${sr.hasDocuments == 'Y' }">
																	<a
																		href="/studentportal/student/viewSRDocumentsForStudents?serviceRequestId=${sr.id}"
																		target="_blank">View</a>
																</c:if></td>
															<td><c:out value="${sr.trackId}" /></td>
															<td></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</c:if>
									</div>
								</div>
							</div>
						</div>

						<div id="tabs-5">
							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a data-toggle="collapse"
												href="#collapseStudentCommunication">Student
												Communication : <c:if
													test="${fn:length(getAllCommunicationsMadeToTheStudent) > 0}">
						  				 	 	${fn:length(getAllCommunicationsMadeToTheStudent)}
							  				 </c:if> <c:if
													test="${fn:length(getAllCommunicationsMadeToTheStudent) eq 0}">
							  				 	 0
							  				 </c:if>
											</a>
										</h4>
									</div>
									<div id="collapseStudentCommunication"
										class="panel-collapse collapse">
										<c:if
											test="${fn:length(getAllCommunicationsMadeToTheStudent) > 0}">
											<table class="table table-hover">
												<thead>
													<tr>
														<th>SR.No</th>
														<th>Subject</th>
														<th>From Email ID</th>
														<th>Date Of Communication</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="mail"
														items="${getAllCommunicationsMadeToTheStudent}"
														varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><c:out value="${mail.subject}" /></td>
															<td><c:out value="${mail.fromEmailId}" /></td>
															<td><c:out value="${mail.createdDate}" /></td>
															<td></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</c:if>
									</div>
								</div>
							</div>
						</div>
						
						<div id="tabs-6">
							<div class="panel-group">
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											Login Logs
										</h4>
									</div>
									<div>
										<table class="table table-hover">
											
											<thead>
												<tr>
													<th>Student Account Created On</th>
													<th>First Login</th>
													<th>Last Login</th>
												</tr>
											</thead>
											<tbody>
												<tr>
													<td>
														<c:choose>
															<c:when test="${empty loginDetails.mailerTriggredOn}">
																NA
															</c:when>
															<c:otherwise>
																${loginDetails.mailerTriggredOn}
															</c:otherwise>
														</c:choose>
													</td>
													<td>
														<c:choose>
															<c:when test="${empty loginDetails.firstLogin}">
																NA
															</c:when>
															<c:otherwise>
																${loginDetails.firstLogin}
															</c:otherwise>
														</c:choose>
													</td>
													<td>
														<c:choose>
															<c:when test="${empty loginDetails.lastLogin}">
																NA
															</c:when>
															<c:otherwise>
																${loginDetails.lastLogin}
															</c:otherwise>
														</c:choose>
													</td>
												</tr>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
						
					<div id="tabs-7">
						<div class="panel-group panel-overview">
							<div class="panel panel-default">
								<div class="panel-heading">
									<h4 class="panel-title">
										<h4 data-toggle="collapse" href="#collapse5">Student Queries : ${fn:length(studentQueries)}</h4>
									</h4>
								</div>
								<div id="collapse5" class="panel-collapse collapse">
									<div class="panel-body">
										<div class="table-responsive">
											<table class="table table-bordered table-hover">
												<tbody>
												<thead> 
													<tr>
														<th>Query Type</th>
														<th>Created Date</th>
 									     				<th>Query</th> 
 									     				<th>Answer On</th> 
 									     				<th>Answer</th> 
 									     				<th>Status</th> 
 									     				
													</tr>
												</thead>
									       <c:if test="${fn:length(studentQueries) > 0}">
												
												<c:forEach var="query" items="${studentQueries}">
												  <tr>
												    <td><c:out value="${query.queryType}" /></td>
												    <td><c:out value="${query.createdDate}" /></td>
												    <td><c:out value="${query.query}" /></td>
												       <c:choose>
												      <c:when test="${query.isAnswered eq 'Y'}">
												    <td><c:out value="${query.lastModifiedDate}" /></td>
												      </c:when>
												      <c:otherwise>
												        <td></td>
												      </c:otherwise>
												    </c:choose>
												    <td>${query.answer}</td>
												    <c:choose>
												      <c:when test="${query.isAnswered eq 'Y'}">
												        <td>Answered</td>
												      </c:when>
												      <c:otherwise>
												        <td>Not Answered</td>
												      </c:otherwise>
												    </c:choose>
												  </tr>
												</c:forEach>
									        </c:if>
												</tbody>
											</table>
										</div>
									</div>
								</div>
							</div>
					
					     </div>
						</div>
					</div>
				</div>


			</div>
			<%} %>

		</div>

	</section>

	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/jquery-1.11.2.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/jquery.validate.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/additional-methods.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/fileinput.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/bootstrap-datepicker.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/scripts.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/main.js?id=1"></script>
	<script src="${pageContext.request.contextPath}/assets/js/moment.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/fullcalendar.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/bootstrap-combobox.js"></script>
	<%
		 Date dt = new Date();
		 
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		 String today = sdf.format(dt);
		 %>

	<script>
   $(function() {
    $( "#tabs" ).tabs({
      beforeLoad: function( event, ui ) {
        ui.jqXHR.fail(function() {
          ui.panel.html(
            "Couldn't load this tab. We'll try to fix this as soon as possible. " +
            "If this wouldn't be a demo." );
        });
      }
    });
  });
  
  <%if(studentDetails!=null){ %>
  $('#calendar').fullCalendar({
		defaultDate: '<%=today%>',
		header: false,
		aspectRatio: 1.4,
		/* dayClick: function(date, jsEvent, view) {
			console.log('Clicked on: ' + date.format());
		}, */
		
		events: [
			<% 
			    for(int i = 0 ; i < scheduledSessionListForStudentDetailDashBoard.size(); i++){
				 SessionDayTimeStudentPortal bean = scheduledSessionListForStudentDetailDashBoard.get(i);
			%>    
			    
			    
			{
				title: '<%=bean.getSubject().replaceAll("'", "") + " - "+ bean.getSessionName()%>',
				start: '<%=bean.getDate()+"T"+ bean.getStartTime()%>',
				url: 'viewScheduledSession?id=<%=bean.getId()%>',
				className: 'blue-event'
			},
			
			<%}%>
			
		],
		eventClick: function(calEvent, jsEvent, view) {
			$(this).css('border-color', 'red');
			document.getElementById("sessionFrame").src=calEvent.url;
          return false;
	
		},
	});
	<%}%>
	function setCurrentTitle() {
		var view = $('#calendar').fullCalendar('getView');
		var start = $('#calendar').fullCalendar('getView').title;
		console.log(view,start);
		$('#month').html(start);
	}
	

	$('#showM').click(function() {
		$('#calendar').fullCalendar( 'changeView', 'month' );
		setCurrentTitle();
	});
	$('#showW').click(function() {
		$('#calendar').fullCalendar( 'changeView', 'agendaWeek' );
		setCurrentTitle();
	});
	$('#showD').click(function() {
		$('#calendar').fullCalendar( 'changeView', 'agendaDay' );
		setCurrentTitle();
	});
	$('#showT').click(function() {
		$('#calendar').fullCalendar( 'changeView', 'agendaDay' );
		setCurrentTitle();
		$('#calendar').fullCalendar('today');
	});
	
	$('#next').click(function() {
		$('#calendar').fullCalendar('next');
		setCurrentTitle();
	});
	$('#prev').click(function() {
		$('#calendar').fullCalendar('prev');
		setCurrentTitle();
	});
	$('.show-calendar-format button').on('click', function(){
		$('.show-calendar-format button').removeClass('active-button');
		$(this).addClass('active-button');
	});
	
  </script>
</body>
</html>