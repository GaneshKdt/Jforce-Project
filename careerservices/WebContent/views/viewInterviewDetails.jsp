<!DOCTYPE html>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.UserAuthorizationBean"%>
<%@page import="com.nmims.beans.InterviewFeedbackBean"%>

<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:useBean id="now" class="java.util.Date"/>  
<jsp:include page="/views/common/jscss.jsp">
<jsp:param value="Session Details" name="title" />
</jsp:include>

<style>
	ul {
		padding-left: 20px;
	}
	
	li {
		list-style: initial;
	}
	
	.meetingButtons {
		margin: 5px;
	}
	
	.noHover {
		pointer-events: none;
	}
</style>

<script src='https://cdn.plot.ly/plotly-latest.min.js'></script>
<script type="text/javascript">
	function showAlert() {
		alert('Session not yet started. Please revisit this page 1 hour before start Date/time.');
		return false;
	}

	function showLRAlert() {
		alert('Session recordings will be available within 48 hours. You can visit Course Home Page and find recordings under Learning Resources Section.');
		return false;
	}
</script>

<body>
	<%@ include file="messages.jsp"%>
	<section class="content-container login" style="height:550px;background-color:#fff;">
		
		<div class="panel panel-primary">
			<div class="panel-heading">
				<h3 class="panel-title">Interview</h3>
			</div>
				
			<fmt:parseDate value="${session.date}" var="parsedDate" pattern="yyyy-MM-dd" />
		
			<div class="panel-body">
				 
				<table class="table table-striped ">
					<tbody>
						<tr style="padding: 10px;">
					   		<td>Session Name </td>
					   		<td colspan="2">Practice Interview</td>
					   	</tr>
					   	<tr>
					   		<td>${ joinTime }</td>
					   		<td>${ interview.date } ${interview.startTime } </td>
					   	</tr>
						<tr style="padding: 10px;">
					   		<td>Date </td>
					   		<td colspan="2"><fmt:formatDate  pattern="dd-MMM-yyyy"  value="${parsedDate}"/> ${interview.date}</td>
					   	</tr>
					   	<tr style="padding: 10px;">
					   		<td >Start Time </td><td><fmt:formatDate  pattern="dd-MMM-yyyy"  value="${parsedDate}"/> ${interview.startTime}</td>
					   	</tr>
					   	<tr style="padding: 10px;">
					   		<td >End Time </td><td><fmt:formatDate  pattern="dd-MMM-yyyy"  value="${parsedDate}"/> ${interview.endTime}</td>
					   	</tr>
					   	
					 	<!-- ___________________________________________________ Extra details for faculty ___________________________________________ -->
					   	
					   	<c:if test="${ userId = '' }">
					   	
						   	<tr style="padding: 10px;">
						   		<td >Faculty </td>
						   		<td>${interview.facultyName}</td>
						   	</tr>
						   	<tr style="padding: 10px;">
						   		<td >Original Meeting Host Id/Password </td>
						   		<td>${interview.hostId} / 'password'</td>
						   	</tr>
						   	<tr style="padding: 10px;">
						   		<td >Original Meeting Faculty Id/Name </td>
						   		<td>${interview.facultyId} / ${interview.facultyName}</td>
						   	</tr>
						   	
					   	</c:if>
					   	
						<!-- ___________________________________________________ Feedback if provided ___________________________________________ -->
						
						<tr class="noHover" id="feedbackRow" style="padding: 10px;" hidden>
							<td colspan=2>
								<div>
									<div>Interview Feedback</div>
									<div style="pointer-events: none;" id="feedbackBlock"></div>
									<div style="margin: 10px;">
										<p style="white-space: nowrap;">
											<b>PN </b>: 'Preparedness'<br> 
											<b>C&C</b>: 'Communication & Confidence'<br> 
											<b>LS </b>: 'Listening Skills' <br> 
											<b>BL </b>: 'Body Language '<br>
											<b>COT</b>: 'Clarity of Thought'<br> 
											<b>CE </b>: 'Connect/Engage'<br> 
											<b>E </b>: 'Examples'
										</p>
										<p>
											<strong>Area of Strength:</strong> ${ feedback.strength }
										</p>
										<p>
											<strong>Areas of Improvement:</strong> ${ feedback.improvements }
										</p>
										<p>
											<strong>Feedback on CV tweaking as per the role
											requirements and interviewing techniques:</strong> ${ feedback.cvtweaking }
										</p>
										<p>
											<strong>Alternate career choice:</strong> ${ feedback.careerchoice }
										</p>
									</div>
								</div>
							</td>
						</tr>
					</tbody>
				</table>
				
				<!-- ___________________________________________________ Instructions for interview  ___________________________________________ -->
				
				<c:if test="${not empty studentDetails }">
						<div class="bullets">
							<a data-toggle="collapse" href="#instructions" aria-expanded="false" aria-controls="instructions"><i class="fa fa-plus-square-o" aria-hidden="true"></i> Instructions to attend session:</a>
							<div id="instructions" class="collapse">
								<ul >
								  <li >This is a Practice Interview under your purchased package <b> ${studentDetails.packageName} </b>.</li>
								  <li >You currently have <b> ${studentDetails.activationsLeft} /${studentDetails.totalActivations} </b> activations left.</li>
								  <li >You can join meeting 15 minutes before start time.</li>
								  <li >Please keep your Headset ready to attend the session over Zoom.</li>
								  <li >You can post queries related to session after session is over.</li>
								  <li >Please use Google Chrome OR Mozilla Firefox OR Safari browser preferably.</li>
								  <li >Please contact Technical Support Desk +1-888-799-9666 for any Technical Assistance in joining Zoom Webinar </li>
								  <li ><a href="#" class="" onClick="window.open('resources_2015/LiveSessionGuide.pdf')">Download User Guide to Attend Session</a></li>
								</ul>
							</div>
						</div>
				</c:if>
					
			</div>
			
			<!-- ___________________________________________________ If faculty show feedback button  ___________________________________________ -->
				
			<c:choose>
			
				<c:when test="${ role = faculty }">
				
					<form action="" method="post">
						<div class="control-group" align="left">
							<div class="controls"
								style="padding-bottom: 5px; padding-left: 15px;">
								
								<fmt:formatDate value="${date}" var="formatedDate" pattern="yyyy-MM-dd" />	
								
								<c:if test="${interview.isCancelled ne 'Y' and formatedDate <= interview.date }">
									<div class="meetingButtons">
										<a class="btn btn-primary btn-sm" href="${interview.hostUrl}"
											target="_blank"><i class="fa fa-headphones fa-lg"></i>
											Attend Interview-For Students</a> &nbsp;
									</div>
								</c:if>
								<c:if test="${interview.isCancelled eq 'Y' }">
									<div class="form-group"
										style="color: #d2232a; font-weight: bold;">Session
										Cancelled</div>
									<div class="form-group" style="color: #d2232a;">Remarks :
										${session.reasonForCancellation}</div>
								</c:if>
		
								<div class="meetingButtons">
									<c:choose>
										<c:when test="${ interview.feedback == 'N' }">
											<a class="btn btn-primary btn-sm "
											href="interviewFeedbackForm?interviewId=${ interview.interviewId }"
											target="_blank"><i class="fa fa-comments-o"></i> Feedback </a>
										</c:when>
										<c:otherwise>
											<div class="form-group" style="margin: 20px;">
												<a class="btn btn-primary btn-sm " id="viewFeedback"><i
													class="fa fa-headphones fa-lg"></i> View Feedback </a>
											</div>
										</c:otherwise>
									</c:choose>
								</div>

								<br>
		
							</div>

						</div>
					</form>
							
				</c:when>
				<c:otherwise>
						
					<c:choose>
						<c:when test="${ joinTime > date && interview.attended == 'N'}" >
							<div class="form-group" style="margin: 20px;">
								<a class="btn btn-primary btn-sm " onclick="showAlert('interview')"><i
									class="fa fa-headphones fa-lg"></i> Attend Interview </a>
							</div>
						</c:when>
						<c:when test="${ interview.attended == 'Y' && interview.feedback == 'N'}" >
							<div class="form-group" style="margin: 20px;">
								<a class="btn btn-primary btn-sm " onclick="showAlert('feedback')"><i
									class="fa fa-headphones fa-lg"></i> View Feedback </a>
							</div>
						</c:when>
						<c:when test="${ interview.attended == 'Y' && interview.feedback == 'Y'}" >
							<div class="form-group" style="margin: 20px;">
								<a class="btn btn-primary btn-sm " id="viewFeedback"><i
									class="fa fa-headphones fa-lg"></i> View Feedback </a>
							</div>
						</c:when>
						<c:when  test="${ joinTime == date && interview.attended == 'N'}" >
								<div class="form-group" style="margin: 20px;">
									<a class="btn btn-primary btn-sm " href="${ interview.joinUrl }"
										target="_blank"  onclick="updateAttendance()"><i class="fa fa-headphones fa-lg"></i> Attend
										Interview </a>
								</div>
						</c:when>
					</c:choose>
					
				</c:otherwise>
			</c:choose>
		
		</div>
	</section>


<%-- <jsp:include page="footer.jsp" /> --%>


	<script src="resources_2015/js/vendor/jquery-1.11.2.min.js"></script>
	<!-- jQuery Editable element Plugin -->
	<script src="assets/js/bootstrap.js"></script>
	<script src="resources_2015/js/vendor/bootstrap-editable.min.js"></script>
	
	<script>
			var parameters = {
				type: 'bar',
				x : ['PN', 'C&C', 'LS', 'BL', 'COT', 'CE', 'E' ],
				    
				y : [ ${feedback.getPreparedness() }, ${ feedback.getCommunication() }, ${ feedback.getListeningSkills() }, ${ feedback.getBodyLanguage() },
						${ feedback.getClarityOfThought() }, ${ feedback.getConnect() }, ${ feedback.getExamples() } ],

				text: [ 'PN: Preparedness', 
						'C&C: Communication & Confidence',
						'LS: Listening Skills',
						'BL: Body Language ',
						'COT:  Clarity of Thought',
						'CE: Connect/Engage',
						'E: Examples' ],
				mode: 'text',
				textposition: 'bottom',
				marker : {
						color : '#C8A2C8',
						line : {
							width : 1
						}
					}
			};

			var data = [ parameters ];

			var layout = {
				title : 'Interview Feedback',
				font : {
					size : 14
				},
				showlegend: false,
				hoverinfo: 'none'
			};

			var config = {
				responsive : true
			}

			Plotly.newPlot('feedbackBlock', data, layout, {displayModeBar: false});	

	</script>
</body>
</html>
