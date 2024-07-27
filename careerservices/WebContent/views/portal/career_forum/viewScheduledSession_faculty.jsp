<!DOCTYPE html>
<!--[if lt IE 7]>	<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>		<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>		<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.UserAuthorizationBean"%>
<html class="no-js">
<!--<![endif]-->
<%@page import="com.nmims.beans.PersonCareerservicesBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<jsp:useBean id="now" class="java.util.Date" />
<jsp:include page="/views/common/jscss.jsp">
	<jsp:param value="Session Details" name="title" />
</jsp:include>


<style>
	ul {
		padding-left: 20px;
	}

	a {
	    color: #c72127;
	}

	li {
		list-style: initial;
	}

	.meetingButtons {
		margin: 5px;
	}
	
	.btn-sm, .btn-group-sm > .btn {
	    padding: 5px 10px;
	    font-size: 12px;
	    line-height: 1.5;
	    border-radius: 3px;
	    margin: 0;
	}
	.card-primary {
	    border-color: #428bca;
	}
	.card-primary > .card-heading {
	    color: #ffffff;
	    background-color: #428bca;
	    border-color: #428bca;
	}
	.card-title {
	    margin-top: 0;
	    margin-bottom: 0;
	    font-size: 16px;
	    color: inherit;
	}
	h1, h2, h3, h4, h5, h6, .h1, .h2, .h3, .h4, .h5, .h6 {
	    font-family: inherit;
	    font-weight: 500;
	    line-height: 1.1;
	    color: inherit;
	}
	.card-heading {
	    padding: 10px 15px;
	    border-bottom: 1px solid transparent;
	    border-top-right-radius: 3px;
	    border-top-left-radius: 3px;
	}
	h3 {
	    display: block;
	    font-size: 1.17em;
	    margin-block-start: 1em;
	    margin-block-end: 1em;
	    margin-inline-start: 0px;
	    margin-inline-end: 0px;
	    font-weight: bold;
	    float:left;
	}
	
	.control-group {
	    float: left;
	    margin: 1.5rem 0 1rem 0;
	    margin-top: 1.5rem;
	    margin-right: 0px;
	    margin-bottom: 1rem;
	    margin-left: 0px;
	}
</style>

<script type="text/javascript">
	function showAlert() {
		alert('Session not yet started. Please revisit this page 1 hour before start Date/time.');
		return false;
	}

	function showLRAlert() {
		alert(
			'Session recordings will be available within 48 hours. You can visit Course Home Page and find recordings under Learning Resources Section.');
		return false;
	}
</script>

<%
String userId = (String)session.getAttribute("userId");
	PersonCareerservicesBean user = (PersonCareerservicesBean)session.getAttribute("user_careerservices");
	String roles = "";
	if(user != null){
		roles = user.getRoles();
		if(roles == null){
	roles = "";
		}
	}else{
		userId = "";
	}
	
	HashMap<String,Integer> getMapOfFacultyIdAndNumberOfAttendee = (HashMap<String,Integer>)request.getAttribute("getMapOfFacultyIdAndNumberOfAttendee");
	UserAuthorizationBean userAuthorization = (UserAuthorizationBean)session.getAttribute("userAuthorization");
	if(userAuthorization != null){
		roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
	}
%>
<body>
	<%@ include file="/views/common/messages.jsp"%>
	<section class="content-container login" style="height:550px;background-color:#fff;">
		<div class="card card-primary">
			<div class="card-heading">
				<h3 class="card-title">${session.sessionName}</h3>
			</div>
			<fmt:parseDate value="${session.date}" var="parsedDate" pattern="yyyy-MM-dd" />
			<div class="card-body">
				<table class="table table-striped table-hover">
					<tbody>
						<tr>
							<td>Session Name</td>
							<td>${session.sessionName}</td>
						</tr>
						<tr>
							<td>Date / Time</td>
							<td>
								<fmt:formatDate pattern="dd-MMM-yyyy" value="${parsedDate}" />
							</td>
						</tr>
						<tr>
							<td>Start Time</td>
							<td>${session.startTime}</td>
						</tr>
						<tr>
							<td>End Time</td>
							<td>${session.endTime}</td>
						</tr>
						<tr>
							<td>Faculty</td>
							<td>${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}</td>
						</tr>
						
						<%if(roles.indexOf("CSFaculty") != -1 || roles.indexOf("Career Services Admin") != -1){ %>
							<tr>
								<td>Corporate Name</td>
								<td>Retail</td>
							</tr>
							<tr>
								<td>Original Meeting Host Id/Password</td>
								<td>${session.hostKey}/${session.hostPassword}</td>
							</tr>
							<tr>
								<td>Original Meeting Faculty Id/Name</td>
								<td>${session.facultyId} / ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}
								</td>
							</tr>
						<%} %>
					</tbody>
				</table>
				
				<br>
				
				<h5 style="font-size: larger;">Description</h5>
				<div>
					${session.description}
				</div>
					
				<br>
					
				<div class="bullets">
					<a data-toggle="collapse" href="#instructions" aria-expanded="false" aria-controls="instructions"><i
							class="fa fa-plus-square-o" aria-hidden="true"></i> Instructions to attend session:</a>
					<div id="instructions" class="collapse">
						<ul>
							<li>Attend Session Button will be enabled 1 hour before session.</li>
							<li>You can join meeting 15 minutes before start time.</li>
							<li>Please keep your Headset ready to attend the session over Zoom.</li>
							<li>You can post queries related to session after session is over.</li>
							<li>Please use Google Chrome OR Mozilla Firefox OR Safari browser preferably.</li>
							<li>Please contact Technical Support Desk +1-888-799-9666 for any Technical Assistance in
								joining Zoom Webinar </li>
							<li><a href="#" class=""
									onClick="window.open('resources_2015/LiveSessionGuide.pdf')">Download User Guide to
									Attend Session</a></li>
						</ul>
					</div>
					<div style="display: none;" id="guestLecture">
						<p>To join the training session</p>
						<ul>
							<li> 
								<a href="https://acecloud.webex.com/acecloud/k2/j.php?MTID=t7e4b4fab4ba5a32842c6762ec57caa5c"
									target="_blank">click here</a> 
							</li>
							<li>Enter your name and email address . </li>
							<li>Enter the session password: NMIMSNMIMS. </li>
							<li>Click "Join Now".</li>
						</ul>
					</div>
				</div>
			</div>

			<!-- 		Added for zoom -->

			<c:url value="attendScheduledSession" var="joinUrl">
				<c:param name="id" value="${session.id}" />
				<%-- <c:param name="facultyId" value="${session.facultyId}" /> --%>
				<c:param name="joinFor" value="Admin" />
			</c:url>

			<c:url value="attendScheduledSession" var="attendUrl">
				<c:param name="id" value="${session.id}" />
				<c:param name="joinFor" value="ANY" />
			</c:url>

			<c:url value="loginIntoWebEx" var="startMeetingUrl">
				<c:param name="id" value="${session.id}" />
			</c:url>

			<c:url value="loginIntoZoom" var="startZoomMeetingUrl">
				<c:param name="id" value="${session.id}" />
			</c:url>
			<c:url value="postQueryForm" var="postQueryUrl">
				<c:param name="id" value="${session.id}" />
				<c:param name="action" value="postQueries" />
			</c:url>

			<c:url value="postQueryForm" var="myQueriesUrl">
				<c:param name="id" value="${session.id}" />
				<c:param name="action" value="viewQueries" />
			</c:url>

			<c:url value="refreshSession" var="refreshurl">
				<c:param name="id" value="${session.id}" />
			</c:url>

			<c:url value="createParallelSession" var="createParallelSessionUrl">
				<c:param name="id" value="${session.id}" />
			</c:url>

			<c:url value="viewFacultyFeedback" var="facultyFeedbackUrl">
				<c:param name="id" value="${session.id}" />
			</c:url>

			<c:url value="viewQueryForm" var="viewQueryUrl">
				<c:param name="id" value="${session.id}" />
			</c:url>
			<form action="" method="post">
				<div class="control-group" align="left">
					<div class="controls" style="padding-bottom: 5px; padding-left: 15px;">
						
						<%if(roles.indexOf("Career Services Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
						<c:if test="${session.isCancelled ne 'Y' }">
							<div class="meetingButtons">
								<a class="btn btn-primary btn-sm" href="${joinUrl}" target="_blank"><i
										class="fa fa-headphones fa-lg"></i> Attend Session-For Students</a> &nbsp;
							</div>
						</c:if>
						<c:if test="${session.isCancelled eq 'Y' }">
							<div class="form-group" style="color:#d2232a ;font-weight:bold;">
								Session Cancelled
							</div>
							<div class="form-group" style="color:#d2232a;">
								Remarks : ${session.reasonForCancellation}
							</div>
						</c:if>
						<%} %>

						<%if(roles.indexOf("Career Services Sessions Speaker") != -1 || roles.indexOf("Career Services Admin") != -1 || roles.indexOf("Career Services Sessions Admin") != -1){ %>
						<div class="meetingButtons">
							<a class="btn btn-danger btn-sm" href="${facultyFeedbackUrl}" target="_blank"><i
									class="fa fa-eye fa-lg"></i> View Feedback</a>
						</div>
						<div class="meetingButtons">
							<a class="btn btn-danger btn-sm" href="${viewQueryUrl}" target="_blank"><i
									class="fa fa-question-circle fa-lg"></i> View Queries</a>
						</div>
						<%} %>
						<br>

					</div>
					<div class="controls" style="padding-bottom: 5px; padding-left: 15px;">
						<%if(roles.indexOf("Career Services Sessions Speaker") != -1 || roles.indexOf("Career Services Admin") != -1){ %>
						<c:if test="${session.isCancelled ne 'Y'}">
							<br />
							<!-- Start Meeting Section Admin login -->
							<%if(roles.indexOf("Career Services Admin") != -1){ %>

								<c:if test="${not empty session.meetingKey }">
									<div class="meetingButtons">
										<a class="btn btn-success btn-sm" href="${startZoomMeetingUrl}" target="_blank"><i
												class="fa fa-video-camera fa-lg"></i> Start Original Meeting for
											${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}</a>
									</div>
								</c:if>

							<%}else if(roles.indexOf("Career Services Sessions Speaker") != -1){ %>
							<!-- Start Meeting Section by Respective Faculty login -->
							<fmt:formatDate var="formattedDate" value="${now}" pattern="yyyy-MM-dd" />
							<c:if test="${session.date eq formattedDate }">
								<c:if
									test="${not empty session.meetingKey && fn:trim(fn:toUpperCase (session.facultyId)) eq fn:trim (fn:toUpperCase(userId))}">
									<div class="meetingButtons">
										<a class="btn btn-success btn-sm" href="${startZoomMeetingUrl}"
											target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Original
											Meeting for
											${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}</a>
									</div>
								</c:if>
							</c:if>
							<%} %>
							<br />

							<%if(roles.indexOf("Career Services Admin") != -1){ %>
								<!-- Refresh Meeting Section by Admin -->
								<c:if test="${not empty session.meetingKey}">
									<div class="meetingButtons">
										<a class="btn btn-success btn-sm" href="${refreshurl}&type=0"
											onclick="return confirm('Are you sure you want to re-create meeting? A new meeting will be created.')"><i
												class="fa fa-video-camera fa-lg"></i> Re-create Original Meeting for
											${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}</a>
									</div>
								</c:if>
							<%} %>


						</c:if>
						<%if(roles.indexOf("Career Services Sessions Speaker") != -1 ){ %>
						<c:if test="${session.isCancelled eq 'Y'}">
							<div class="meetingButtons" style="color:#d2232a ;font-weight:bold;">
								Session Cancelled
							</div>
							<div class="meetingButtons" style="color:#d2232a;">
								Remarks : ${session.reasonForCancellation}
							</div>
						</c:if>
						<%} %>
						
						<c:url value="addVideoContent" var="addVideoContentUrl">
							<c:param name="sessionId" value="${session.id}" />
						</c:url>

						<br />
						<div class="meetingButtons">
							<a class="btn btn-primary btn-sm " href="${addVideoContentUrl}"
								target="_blank">
								<i class="fa fa-play-circle-o fa-lg"></i> &nbsp;
								Upload Session Video&nbsp;
							</a>
						</div>
						<%} %>
					</div>
				</div>
			</form>
			<br>

		</div>
	</section>


	<%-- <jsp:include page="footer.jsp" /> --%>


	<script src="resources_2015/js/vendor/jquery-1.11.2.min.js"></script>
	<!-- jQuery Editable element Plugin -->
	<script src="assets/js/bootstrap.js"></script>
	<script src="resources_2015/js/vendor/bootstrap-editable.min.js"></script>

	<script>
		$(document).ready(function () {
			$('#guestLecture').hide();
			//toggle `popup` / `inline` mode
			$.fn.editable.defaults.mode = 'inline';

			/* //make username editable
			$('#score').editable();
			/* //make username editable
	$('#score').editable();
	
	//make username editable
	$('#remarks').editable(); */
			//make username editable

			$('.editable').each(function () {
				$(this).editable();
			});

		});

		function showMessage() {
			$('#guestLecture').show();
		}
		
		var activationsPossible = 0;
		var totalActivations = 0;
		var nextActivationPossible = 0;
		var activationsLeft = 0;
		
		<%
			if((userId.startsWith("77") || userId.startsWith("79"))){//Student %>
			
				$(document).ready(function () {
					getCareerForumActivationInfo();
				}
				function getCareerForumActivationInfo(){
		
					var sapid = '{"sapid": "<%= (String)request.getSession().getAttribute("userId") %>"}';
					$.ajax({
					    type: 'POST',
					    url: '/careerservices/m/getCareerForumActivationInfo',
					    data: sapid,
					    contentType: "application/json;",  
					    dataType: "json",
					    success: function(data, textStatus ){
					    	console.log(data);
					    	var nextActivationDateStr = data.response.nextActivationAvailableDate;
					    	var nextActivationDate = new Date(nextActivationDateStr);
					    	nextActivationPossible = nextActivationDate.toDateString();
					    	totalActivations = data.response.totalActivations;
					    	activationsPossible = data.response.activationsPossible;
					    	activationsLeft = data.response.activationsLeft;
		
					    	$("#totalActivations").html(totalActivations);
					    	$("#activationsPossible").html(activationsPossible);
					    	$("#nextActivationPossible").html(nextActivationPossible);
					    	$("#activationsLeft").html(activationsLeft);
					    }
					});
				}
		<%	}
		%>
	</script>

</body>

</html>