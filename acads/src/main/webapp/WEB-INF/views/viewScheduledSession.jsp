<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.UserAuthorizationBean"%>
<html class="no-js"> <!--<![endif]-->
 <%@page import="com.nmims.beans.PersonAcads"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:useBean id="now" class="java.util.Date"/>  
<jsp:include page="jscss.jsp">
<jsp:param value="Session Details" name="title" />
</jsp:include>
<style>
ul {
    padding-left:20px;
}

li {
    list-style: initial;
}

.meetingButtons{
	margin:5px;
}
a.disabled{
	pointer-events: none;
  	cursor: default;
}
</style>

<script type="text/javascript">

function showAlert(){
	alert('Session not yet started. Please revisit this page 1 hour before start Date/time.');
	return false;
}

function showLRAlert(){
	alert('Session recordings will be available within 48 hours. You can visit Course Home Page and find recordings under Learning Resources Section.');
	return false;
}

</script>
<script src="https://use.fontawesome.com/7e8fb4977a.js"></script>
<%
	String userId = (String)session.getAttribute("userId");
	Person user = (Person)session.getAttribute("user");
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
	BaseController sessionCon = new BaseController();
	String disableLink = "";
	try{
		if(sessionCon.checkLead(request, response)){
			disableLink = "disabled";
		}
	}catch(Exception e){}
	
%>

<body>
<%@ include file="messages.jsp"%>
	
   <section class="content-container login" style="height:550px;background-color:#fff;">
				 
				 <div class="panel panel-primary">
				  <div class="panel-heading">
				     <c:choose>
					    <c:when test="${session.sessionName=='Orientation Session'}">
						<h3 class="panel-title">Orientation</h3>
					    </c:when>    
					    <c:otherwise>
						<h3 class="panel-title">${session.subject}</h3>
					    </c:otherwise>
					</c:choose>

				  </div>
				  <fmt:parseDate value="${session.date}" var="parsedDate" pattern="yyyy-MM-dd" />
				  
				  <div class="panel-body">
				   
				   	<table class="table table-striped table-hover">
				   		<tbody>
				   			<tr>
				   				<td>Session Name</td>
				   				<td>${session.sessionName}</td>
				   			</tr>
				   			
				   			<c:if test="${session.track != null && session.track != ''}">
				   			<tr>
				   				<td>Track/Group</td>
				   				<td>${session.track}</td>
				   			</tr>
				   			</c:if>
				   			
				   			<tr>
				   				<td>Date / Time</td>
				   				<td><fmt:formatDate  pattern="dd-MMM-yyyy"  value="${parsedDate}"/>, ${session.startTime}</td>
				   			</tr>
				   			
				   			<%if(roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Admin") != -1){ %>
				   			
				   			<c:choose>
				   				<c:when test="${session.corporateName != null && session.corporateName != ''}">
				   					<tr>
						   				<td>Corporate Name</td>
						   				<td>${session.corporateName}</td>
						   			</tr>
				   				</c:when>
				   				<c:otherwise>
				   					<tr>
						   				<td>Corporate Name</td>
						   				<td>Retail</td>
						   			</tr>
				   				</c:otherwise>
				   			</c:choose>
				   			
				   			<tr>
				   				<td>Original Meeting Number/Password </td>
				   				<td>${session.meetingKey}/${session.meetingPwd}</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Original Meeting Host Id/Password</td>
				   				<td>${session.hostKey}/${session.hostPassword}</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Original Meeting Faculty Id/Name</td>
				   				<td><a href="/studentportal/facultyProfile?facultyId=${session.facultyId}" target="_blank">${session.facultyId} / ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}</a></td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Original Meeting Faculty Location</td>
				   				<td>
				   					<a href="#" class="editable" id="addFacultyLocation" 
				   					data-type="text" data-pk="${session.id}" 
				   					data-url="/acads/admin/addFacultyLocation?AF=0" 
				   					data-title="Add Faculty Location">${session.facultyLocation}</a> 
				   					
				   				
				   				</td>
				   			
				   			</tr>
				   			
				   			
				   			<tr>
				   				<td>Parallel Meeting 1 Number/Password </td>
				   				<td>${session.altMeetingKey}/${session.altMeetingPwd}</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Parallel Meeting 1 Host Id/Password</td>
				   				<td>${session.altHostKey}/${session.altHostPassword}</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Parallel Meeting 1 Faculty Id/Name</td>
				   				<td>
				   					<a href="#" class="editable" id="addAltFacultyId" 
				   					data-type="text" data-pk="${session.id}" 
				   					data-url="/acads/admin/addAltFacultyId?AF=1" 
				   					data-title="Add Alternate Faculty">${session.altFacultyId}</a> 
				   					
				   					/ ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId].fullName}
				   				
				   				</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Parallel Meeting 1 Faculty Location</td>
				   				<td>
				   					<a href="#" class="editable" id="addAltFacultyLocation" 
				   					data-type="text" data-pk="${session.id}" 
				   					data-url="/acads/admin/addFacultyLocation?AF=1" 
				   					data-title="Add Faculty 1 Location">${session.altFacultyLocation}</a> 
				   					
				   				
				   				</td>
				   			
				   			</tr>
				   			
				   			
				   			<tr>
				   				<td>Parallel Meeting 2 Number/Password </td>
				   				<td>${session.altMeetingKey2}/${session.altMeetingPwd2}</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Parallel Meeting 2 Host Id/Password</td>
				   				<td>${session.altHostKey2}/${session.altHostPassword2}</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Parallel Meeting 2 Faculty Id/Name</td>
				   				<td><a href="#" class="editable" id="addAltFacultyId" 
				   					data-type="text" data-pk="${session.id}" 
				   					data-url="/acads/admin/addAltFacultyId?AF=2" 
				   					data-title="Add Alternate Faculty">${session.altFacultyId2}</a> 
				   					
				   					 / ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId2].fullName}</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Parallel Meeting 2 Faculty Location</td>
				   				<td>
				   					<a href="#" class="editable" id="addAltFaculty2Location" 
				   					data-type="text" data-pk="${session.id}" 
				   					data-url="/acads/admin/addFacultyLocation?AF=2" 
				   					data-title="Add Faculty 2 Location">${session.altFaculty2Location}</a> 
				   					
				   				
				   				</td>
				   			
				   			</tr>
				   			
				   			
				   			<tr>
				   				<td>Parallel Meeting 3 Number/Password </td>
				   				<td>${session.altMeetingKey3}/${session.altMeetingPwd3}</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Parallel Meeting 3 Host Id/Password</td>
				   				<td>${session.altHostKey3}/${session.altHostPassword3}</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Parallel Meeting 3 Faculty Id/Name</td>
				   				<td><a href="#" class="editable" id="addAltFacultyId" 
				   					data-type="text" data-pk="${session.id}" 
				   					data-url="/acads/addAltFacultyId?AF=3" 
				   					data-title="Add Alternate Faculty">${session.altFacultyId3}</a> 
				   					
				   					 / ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId3].fullName}</td>
				   			</tr>
				   			
				   			<tr>
				   				<td>Parallel Meeting 3 Faculty Location</td>
				   				<td>
				   					<a href="#" class="editable" id="addAltFaculty3Location" 
				   					data-type="text" data-pk="${session.id}" 
				   					data-url="/acads/admin/addFacultyLocation?AF=3" 
				   					data-title="Add Faculty 3 Location">${session.altFaculty3Location}</a> 
				   					
				   				
				   				</td>
				   			
				   			</tr>
				   			
				   			<%} %>
				   		</tbody>
				   	</table>
				   
				   <%if(roles.indexOf("Acads Admin") != -1){ %>
					   <h4 style="color: #c72127;">Program List</h4>
					   <table class="table table-striped table-hover">
			   				<thead>
			   					<tr>
			   						<td>Program</td>
			   						<td>Sem</td>
			   						<td>prgmStructApplicable</td>
			   					</tr>
			   				</thead>
			   				<tbody>
			   					<c:forEach items="${subjectProgramList }" var="program">
			   						<tr>
			   							<td><c:out value="${program.program}" /></td>
			   							<td><c:out value="${program.sem}" /></td>
			   							<td><c:out value="${program.prgmStructApplicable}" /></td>
			   						</tr>
			   					</c:forEach>
			   				</tbody>
		   				</table>
	   				<% } %>
				   
				   	<%-- <table>
				   	<tbody>
				   		<tr>
						   	<td><b>Session Name:</b> ${session.sessionName}</td>
						   	<td width="10px">&nbsp;</td>
						   	<td><b>Faculty:</b> ${session.firstName} ${session.lastName}</td>
					   	</tr>
					   	
					   	<tr>
						   	<td><fmt:parseDate value="${session.date}" var="parsedDate" pattern="yyyy-MM-dd" />
								<h5 align="justify"><b>Date:</b> <fmt:formatDate  pattern="dd-MMM-yyyy"  value="${parsedDate}"/></td>
							<td width="10px">&nbsp;</td>
						   	<td><b>Start Time:</b> 
									<c:choose>
									    <c:when test="${session.sessionName=='Orientation Session'}">
									        <c:choose>
										        <c:when test="${session.corporateName=='Verizon'}">
												     <c:out value="16:30:00"/>
												</c:when>
												<c:otherwise>
											        <c:out value="17:00:00"/>
										          </c:otherwise> 
											</c:choose>
										 </c:when>    
									<c:otherwise>
										${session.startTime}
									    </c:otherwise>
									</c:choose>
							</td>
					   	</tr>
					   	<%if(roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Admin") != -1){ %>
					   	<tr>
						   	<td><b>Meeting Number:</b> ${session.meetingKey}</td>
						   	<td width="30px">&nbsp;</td>
						   	<td><b>Meeting Password:</b> ${session.meetingPwd}</td>
					   	</tr>
					   	
					   	
					   	<tr>
					   		<td><b>Room:</b> ${session.room}</td>
					   		<td width="30px">&nbsp;</td>
					   		<td><b>Cisco Host ID:</b> ${session.hostId}</td>
					   	</tr>
					   	
					   	
					   	<tr>
					   	<td><b>Alt Meeting Number:</b> 
							<a href="#" class="editable" id="addAltMeetingKey" data-type="text" data-pk="${session.id}" data-url="/acads/addAltMeetingKey?MN=1" data-title="Change Evaluation Count">${session.altMeetingKey}</a>
					   	</td>
					   	<td width="30px">&nbsp;</td>
					   	<td><b>Alt Meeting Password:</b> 
							<a href="#" class="editable" id="addAltMeetingPwd" data-type="text" data-pk="${session.id}" data-url="/acads/addAltMeetingPwd?MP=1" data-title="Change Evaluation Count">${session.altMeetingPwd}</a>
					   	</td>
					   	</tr>
					   	
					   	<tr>
					   	<td><b>Alt Meeting Number 2:</b> 
							<a href="#" class="editable" id="addAltMeetingKey" data-type="text" data-pk="${session.id}" data-url="/acads/addAltMeetingKey?MN=2" data-title="Change Evaluation Count">${session.altMeetingKey2}</a>
					   	</td>
					   	<td width="30px">&nbsp;</td>
					   	<td><b>Alt Meeting Password 2:</b> 
							<a href="#" class="editable" id="addAltMeetingPwd" data-type="text" data-pk="${session.id}" data-url="/acads/addAltMeetingPwd?MP=2" data-title="Change Evaluation Count">${session.altMeetingPwd2}</a>
					   	</td>
					   	</tr>
					   	
					   	
					   	<tr>
					   	<td><b>Alt Meeting Number 3:</b> 
							<a href="#" class="editable" id="addAltMeetingKey" data-type="text" data-pk="${session.id}" data-url="/acads/addAltMeetingKey?MN=3" data-title="Change Evaluation Count">${session.altMeetingKey3}</a>
					   	</td>
					   	<td width="30px">&nbsp;</td>
					   	<td><b>Alt Meeting Password 3:</b> 
							<a href="#" class="editable" id="addAltMeetingPwd" data-type="text" data-pk="${session.id}" data-url="/acads/addAltMeetingPwd?MP=3" data-title="Change Evaluation Count">${session.altMeetingPwd3}</a>
					   	</td>
					   	</tr>
					   	
					   	
					
					   	
					   	
					   	<tr>
					   	<td><b>Alt Meeting Faculty ID:</b> 
							<a href="#" class="editable" id="addAltFacultyId" data-type="text" data-pk="${session.id}" data-url="/acads/addAltFacultyId?AF=1" data-title="Add Alternate Faculty">${session.altFacultyId}</a>
					   	</td>
					   	<td width="30px">&nbsp;</td>
					   	<td><b>Alt Meeting Faculty ID 2:</b> 
							<a href="#" class="editable" id="addAltFacultyId" data-type="text" data-pk="${session.id}" data-url="/acads/addAltFacultyId?AF=2" data-title="Add Alternate Faculty">${session.altFacultyId2}</a>
					   	</td>
					   	<td width="30px">&nbsp;</td>
					   	<td><b>Alt Meeting Faculty ID 3:</b> 
							<a href="#" class="editable" id="addAltFacultyId" data-type="text" data-pk="${session.id}" data-url="/acads/addAltFacultyId?AF=3" data-title="Add Alternate Faculty">${session.altFacultyId3}</a>
					   	</td>
					   	</tr>
					   	
					   	
					   	
					   	<!-- //Newly added 29/08/2016// -->
					   	
					   	<%} %>
						   	
						   	
				   	</tbody>
				   	</table> --%>
				   	
				   	
					<div class="bullets">
					
					<a data-toggle="collapse" href="#instructions" aria-expanded="false" aria-controls="instructions"><i class="fa fa-plus-square-o" aria-hidden="true"></i> Instructions to attend session:</a>
					<div id="instructions" class="collapse">
						<ul >
						  <li >Attend Session Button will be enabled 1 hour before session.</li>
						  <li >You can join meeting 15 minutes before start time.</li>
						  <li >Please keep your Headset ready to attend the session over Zoom.</li>
						  <li >You can post queries related to session after session is over.</li>
						 <!--  <li >If you are unable to join session using 'Attend Session' button above, then please use direct URL 
							<a href="https://nmims.webex.com/mw0401lsp12/mywebex/default.do?siteurl=nmims&service=7" target=_blank">https://nmims.webex.com/mw0401lsp12/mywebex/default.do?siteurl=nmims&service=7</a>
							along with Meeting Number and Meeting Password given above.</li> -->
						  <li >Please use Google Chrome OR Mozilla Firefox OR Safari browser preferably.</li>
						  <li >Please contact Technical Support Desk +1-888-799-9666 for any Technical Assistance in joining Zoom Webinar </li>
						  <li ><a href="#" class="<%= disableLink %>" onClick="window.open('resources_2015/LiveSessionGuide.pdf')">Download User Guide to Attend Session</a></li>
						</ul>
					</div>
				   
				   <div style="display: none;" id="guestLecture">
					    <p>To join the training session</p>
					   	<ul>
					   		<li> <a href="https://acecloud.webex.com/acecloud/k2/j.php?MTID=t7e4b4fab4ba5a32842c6762ec57caa5c" target="_blank">click here</a> </li>
					   		<li>Enter your name and email address . </li>
					   		<li>Enter the session password: NMIMSNMIMS. </li>
					   		<li>Click "Join Now".</li>
					   	</ul>
				   </div>
				</div>
				

				</div>
				
				<!-- 		Added for zoom  -->
				
				<c:url value="https://ngasce.secure.force.com/AdditionalLiveLectures" var="sfdcUrl">
					<c:param name="id" value="${userId}" />
					<c:param name="dob" value="${dob}" />
				</c:url>
				
				<c:url value="attendScheduledSession" var="joinUrl">
				  <c:param name="id" value="${session.id}" />
				  <c:param name="joinFor" value="HOST" />
				  <c:param name="pssId" value="${pssId }" />
				</c:url>
				
				<c:url value="attendScheduledSession" var="altJoinUrl">
				  <c:param name="id" value="${session.id}" />
				  <c:param name="joinFor" value="ALTFACULTYID" />
				  <c:param name="pssId" value="${pssId }" />
				</c:url>
			
				<c:url value="attendScheduledSession" var="alt2JoinUrl">
				  <c:param name="id" value="${session.id}" />
				   <c:param name="joinFor" value="ALTFACULTYID2" />
				   <c:param name="pssId" value="${pssId }" />
				</c:url>
			
				<c:url value="attendScheduledSession" var="alt3JoinUrl">
				  <c:param name="id" value="${session.id}" />
				   <c:param name="joinFor" value="ALTFACULTYID3" />
				   <c:param name="pssId" value="${pssId }" />
				</c:url>
				
				<c:url value="attendScheduledSession" var="attendUrl">
				  <c:param name="id" value="${session.id}" />
				  <c:param name="joinFor" value="ANY" />
				  <c:param name="pssId" value="${pssId }" />
				</c:url>
				
				
				<!-- Commented By Somesh For Zoom Integration -->
				<%-- <c:url value="attendScheduledSession" var="attendUrlForHostFaculty">
				  <c:param name="id" value="${session.id}" />
				  <c:param name="facultyId" value="${session.facultyId}" />
				  <c:param name="joinFor" value="HOST" />
				</c:url>
			
				<c:url value="attendScheduledSession" var="attendUrlForAltFaculty">
				  <c:param name="id" value="${session.id}" />
				  <c:param name="facultyId" value="${session.altFacultyId}" />
				  <c:param name="joinFor" value="ALTFACULTYID" />
				</c:url>
			
				<c:url v		alue="attendScheduledSession" var="attendUrlForAltFaculty2">
				  <c:param name="id" value="${session.id}" />
				   <c:param name="facultyId" value="${session.altFacultyId2}" />
				   <c:param name="joinFor" value="ALTFACULTYID2" />
				</c:url>
			
				<c:url value="attendScheduledSession" var="attendUrlForAltFaculty3">
				  <c:param name="id" value="${session.id}" />
				   <c:param name="facultyId" value="${session.altFacultyId3}" />
				   <c:param name="joinFor" value="ALTFACULTYID3" />
				</c:url>
				
				<c:url value="attendScheduledSession" var="attendUrl">
				  <c:param name="id" value="${session.id}" />
				  <c:param name="joinFor" value="ANY" />
				</c:url> --%>
				
				<c:url value="loginIntoWebEx" var="startMeetingUrl">
				  <c:param name="id" value="${session.id}" />
				</c:url>
				
				<c:url value="loginIntoZoom" var="startZoomMeetingUrl">
					<c:param name="id" value="${session.id}" />
				</c:url>				
				<c:url value="${SERVER_PATH}acads/student/postQueryForm" var="postQueryUrl">
				  <c:param name="id" value="${session.id}" />
				  <c:param name="action" value="postQueries" />
				</c:url>
				
				<c:url value="${SERVER_PATH}acads/student/postQueryForm" var="myQueriesUrl">
				  <c:param name="id" value="${session.id}" />
				  <c:param name="action" value="viewQueries" />
				</c:url>
				
				<c:url value="${SERVER_PATH}acads/student/watchVideos" var="learningResourcesUrl">
				  <c:param name="id" value="${videoId}" />
				</c:url>
				
				<c:url value="${SERVER_PATH}acads/student/watchVideos" var="learningResourcesUrlAlt">
				  <c:param name="id" value="${altVideoId}" />
				</c:url>
				
				<c:url value="${SERVER_PATH}acads/student/watchVideos" var="learningResourcesUrlAlt2">
				  <c:param name="id" value="${alt2VideoId}" />
				</c:url>
				
				<c:url value="${SERVER_PATH}acads/student/watchVideos" var="learningResourcesUrlAlt3">
				  <c:param name="id" value="${alt3VideoId}" />
				</c:url>
				
				<c:url value="refreshSession" var="refreshurl">
				  <c:param name="id" value="${session.id}" />
				</c:url>
				
				<c:url value="createParallelSession" var="createParallelSessionUrl">
					  <c:param name="id" value="${session.id}" /> 
	 			</c:url>
	 			
	 			<c:url value="deleteParallelSession" var="deleteParallelSessionUrl">
					  <c:param name="id" value="${session.id}" /> 
	 			</c:url> 

				<c:url value="viewFacultyFeedback" var="facultyFeedbackUrl">
				  <c:param name="id" value="${session.id}" />
				</c:url>
				
				<c:url value="${SERVER_PATH}acads/admin/viewQueryForm" var="viewQueryUrl">
				  <c:param name="id" value="${session.id}" />
				</c:url>
				
				<c:url value="viewAddSessionPollsForm" var="viewAddSessionPollsUrl">
				  <c:param name="id" value="${session.id}" />
				</c:url>

				<form  action="" method="post">
				<div class="control-group" align="left">
					<div class="controls" style="padding-bottom: 5px; padding-left: 15px;">
					
						<%if((userId.startsWith("77") || userId.startsWith("79"))/*  && !"77215000851".equals(userId)  */){//Student %>
							<c:if test="${enableAttendButton == 'true' && ( empty session.isCancelled || session.isCancelled eq 'N') }">
									
									<!-- Student can join Webinar from here -->
									
									<div class="form-group">
										<c:choose>
											<c:when test="${isSessionAccess == 'true'}">
												<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${joinUrl}" target="_blank">
													<i class="fa fa-headphones fa-lg"></i> 
													Attend Session by ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}  
													(Remaining Seats : ${facultyIdAndRemSeatsMap[session.facultyId]})
												</a> &nbsp;
											</c:when>
											<c:otherwise>
												<a class="btn btn-primary btn-sm <%= disableLink %>" href="${sfdcUrl}" target="_blank">
													Subscribe Now
												</a>
											</c:otherwise>
										</c:choose>
										
									</div>
									
									<c:if test="${not empty session.altFacultyId && not empty session.altMeetingKey }">
										<div class="form-group">
											<c:choose>
												<c:when test="${isSessionAccess == 'true'}">
													<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${altJoinUrl}" target="_blank">
														<i class="fa fa-headphones fa-lg"></i> 
														Attend Session by ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId].fullName} 
														(Remaining Seats : ${facultyIdAndRemSeatsMap[session.altFacultyId]})
													</a> &nbsp;
												</c:when>
												<c:otherwise>
													<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${sfdcUrl}" target="_blank">
														Subscribe Now
													</a>
												</c:otherwise>
											</c:choose>
										</div>
									</c:if>
									
									<c:if test="${not empty session.altFacultyId2 && not empty session.altMeetingKey2 }">
									   <div class="form-group">
									   		<c:choose>
												<c:when test="${isSessionAccess == 'true'}">
													<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${alt2JoinUrl}" target="_blank">
														<i class="fa fa-headphones fa-lg"></i> 
														Attend Session by ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId2].fullName} 
														(Remaining Seats : ${facultyIdAndRemSeatsMap[session.altFacultyId2]}) 
													</a> &nbsp;
												</c:when>
												<c:otherwise>
													<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${sfdcUrl}" target="_blank">
														Subscribe Now
													</a>
												</c:otherwise>
											</c:choose>
										</div>
									</c:if>
									
									<c:if test="${not empty session.altFacultyId3 && not empty session.altMeetingKey3 }">
										<div class="form-group">
											<c:choose>
												<c:when test="${isSessionAccess == 'true'}">
													<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${alt3JoinUrl}" target="_blank">
														<i class="fa fa-headphones fa-lg"></i> 
														Attend Session by ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId3].fullName} 
														(Remaining Seats  : ${facultyIdAndRemSeatsMap[session.altFacultyId3]}) 
													</a> &nbsp;
												</c:when>
												<c:otherwise>
													<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${sfdcUrl}" target="_blank">
														Subscribe Now
													</a>
												</c:otherwise>
											</c:choose>
										</div>
									</c:if>
									
									<!-- Commented By Somesh For Zoom Integration -->
									<%-- <div class="form-group">
										<a  class="btn btn-primary btn-sm " href="${attendUrlForHostFaculty}" target="_blank"><i class="fa fa-headphones fa-lg"></i> Attend Session by ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName} (Remaining Seats : ${facultyIdAndRemSeatsMap[session.facultyId]})</a> &nbsp;
									</div>
									
									<c:if test="${not empty session.altFacultyId}">
										<div class="form-group">
											<a  class="btn btn-primary btn-sm " href="${attendUrlForAltFaculty}" target="_blank"><i class="fa fa-headphones fa-lg"></i> Attend Session by ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId].fullName} (Remaining Seats : ${facultyIdAndRemSeatsMap[session.altFacultyId]})</a> &nbsp;
										</div>
									</c:if>
									<c:if test="${not empty session.altFacultyId2}">
									   <div class="form-group">
											<a  class="btn btn-primary btn-sm " href="${attendUrlForAltFaculty2}" target="_blank"><i class="fa fa-headphones fa-lg"></i> Attend Session by ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId2].fullName} (Remaining Seats : ${facultyIdAndRemSeatsMap[session.altFacultyId2]})</a> &nbsp;
										</div>
									</c:if>
									<c:if test="${not empty session.altFacultyId3}">
										<div class="form-group">
											<a  class="btn btn-primary btn-sm " href="${attendUrlForAltFaculty3}" target="_blank"><i class="fa fa-headphones fa-lg"></i> Attend Session by ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId3].fullName} (Remaining Seats  : ${facultyIdAndRemSeatsMap[session.altFacultyId3]})</a> &nbsp;
										</div>
									</c:if> --%>
									
								<c:if test="${isSessionAccess == 'true'}">
									<c:if test="${session.noOfParallelSession > 1}">
										<div class="form-group">
										    <c:if test="${session.sessionName ne 'Guest Lecture'}">
												<a  class="btn btn-primary btn-sm " href="${attendUrl}" target="_blank"><i class="fa fa-headphones fa-lg"></i> Attend Any Available Session</a>&nbsp;
											</c:if>
											<c:if test="${session.sessionName eq 'Guest Lecture'}">
												<a  class="btn btn-primary btn-sm " href="#" onClick="return showMessage();"><i class="fa fa-headphones fa-lg"></i> Attend Any Available Session</a>&nbsp;
											</c:if>
										</div>
									</c:if>
								</c:if>
							</c:if>
							
							<c:if test="${session.isCancelled eq 'Y' }">
							   <div class="form-group" style="color:#d2232a ;font-weight:bold;">
									Session Cancelled
							   </div>
							   <div class="form-group" style="color:#d2232a;">
									Remarks : ${session.reasonForCancellation} 
								</div>
							</c:if>
							
							<c:if test="${enableAttendButton != 'true' && sessionOver != 'true'}">
								<c:choose>
									<c:when test="${isSessionAccess == 'true'}">
										<div class="meetingButtons">
											<a  class="btn btn-primary btn-sm <%= disableLink %>" href="#" onClick="return showAlert();"><i class="fa fa-headphones fa-lg"></i> Attend Session</a> &nbsp;
										</div>
									</c:when>
									<c:otherwise>
										<div class="meetingButtons">
											<a class="btn btn-primary btn-sm <%= disableLink %>" href="${sfdcUrl}" target="_blank">
												Subscribe Now
											</a>
										</div>
									</c:otherwise>
								</c:choose>
								
								<div class="meetingButtons">
									<a  class="btn btn-primary btn-sm <%= disableLink %>" href="#" onClick="return showLRAlert();" target="_blank"><i class="fa fa-play-circle-o fa-lg"></i> View Session Recordings</a> &nbsp;
								</div>
							</c:if>
							
							<c:if test="${showQueryButton == 'true' && session.isCancelled ne 'Y' }">
								<div class="meetingButtons">
									<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${postQueryUrl}" target="_blank"><i class="fa fa-question-circle fa-lg"></i> Post A Query</a> &nbsp;
								</div>
								<div class="meetingButtons">
									<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${myQueriesUrl}" target="_blank"><i class="fa fa-question-circle fa-lg"></i> My Queries</a> &nbsp;
								</div>
								
								<!-- 	Added Session recoding link -->
								<c:if test="${not empty session.facultyId}">
									<c:choose>
										<c:when test="${videoId ne 0 }">
											<div class="meetingButtons">
												<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${learningResourcesUrl}" target="_blank"><i class="fa fa-play-circle-o fa-lg"></i> 
												View Session Recordings by Prof. ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName} </a> &nbsp;
											</div>
										</c:when>
										
										<c:otherwise>
											<div class="meetingButtons">
												<a  class="btn btn-primary btn-sm disabled  <%= disableLink %>" href="${learningResourcesUrl}" target="_blank"><i class="fa fa-play-circle-o fa-lg"></i> 
												View Session Recordings by Prof. ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName} </a> &nbsp;
												(The Session recording is not available)
											</div>
										</c:otherwise>
									</c:choose>
								</c:if>
								
								<!-- 	AltMeeting 		-->
								<c:if test="${not empty session.altFacultyId}">
									<c:choose>
										<c:when test="${altVideoId ne 0 }">
											<div class="meetingButtons">
												<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${learningResourcesUrlAlt}" target="_blank"><i class="fa fa-play-circle-o fa-lg"></i> 
												View Session Recordings by Prof. ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId].fullName} </a> &nbsp;
											</div>
										</c:when>
										<c:otherwise>
											<div class="meetingButtons">
												<a  class="btn btn-primary btn-sm disabled  <%= disableLink %>" href="${learningResourcesUrlAlt}" target="_blank"><i class="fa fa-play-circle-o fa-lg"></i> 
												View Session Recordings by Prof. ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId].fullName} </a> &nbsp;
												(The Session recording is not available)
											</div>
										</c:otherwise>
									</c:choose>
								</c:if>
								
								<!-- 	Alt 2 Meeting 		-->
								<c:if test="${not empty session.altFacultyId2}">
									<c:choose>
										<c:when test="${alt2VideoId ne 0 }">
											<div class="meetingButtons">
												<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${learningResourcesUrlAlt2}" target="_blank"><i class="fa fa-play-circle-o fa-lg"></i> 
												View Session Recordings by Prof. ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId2].fullName} </a> &nbsp;
											</div>
										</c:when>
										<c:otherwise>
											<div class="meetingButtons">
												<a  class="btn btn-primary btn-sm disabled  <%= disableLink %>" href="${learningResourcesUrlAlt2}" target="_blank"><i class="fa fa-play-circle-o fa-lg"></i> 
												View Session Recordings by Prof. ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId2].fullName}</a> &nbsp;
												(The Session recording is not available)
											</div>
										</c:otherwise>
									</c:choose>
								</c:if>
								
								<!-- 	Alt 3 Meeting 		-->
								<c:if test="${not empty session.altFacultyId3}">
									<c:choose>
										<c:when test="${alt3videoId ne 0 }">
											<div class="meetingButtons">
												<a  class="btn btn-primary btn-sm <%= disableLink %>" href="${learningResourcesUrlAlt3}" target="_blank"><i class="fa fa-play-circle-o fa-lg"></i> 
												View Session Recordings by Prof. ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId3].fullName} </a> &nbsp;
											</div>
										</c:when>
										<c:otherwise>
											<div class="meetingButtons">
												<a  class="btn btn-primary btn-sm disabled  <%= disableLink %>" href="${learningResourcesUrlAlt3}" target="_blank"><i class="fa fa-play-circle-o fa-lg"></i> 
												View Session Recordings by Prof. ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId3].fullName} </a> &nbsp;
												(The Session recording is not available)
											</div>
										</c:otherwise>
									</c:choose>
								</c:if>
							</c:if>
						
						<%} %>
						
						<br>
						
						<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Learning Center") != -1 || roles.indexOf("Corporate Center") != -1){ %>
							<c:if test="${session.isCancelled ne 'Y' }">
								<div class="meetingButtons">
									<a  class="btn btn-primary btn-sm" href="${joinUrl}"  target="_blank"><i class="fa fa-headphones fa-lg"></i> Attend Session-For Students</a> &nbsp;
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
						
						<%if(roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Admin") != -1 || roles.indexOf("Acads Coordinator") != -1){ %>
								<div class="meetingButtons">
									<a  class="btn btn-danger btn-sm" href="${facultyFeedbackUrl}"  target="_blank"><i class="fa fa-eye fa-lg"></i> View Feedback</a>	
								</div>
								<div class="meetingButtons">
									<a  class="btn btn-danger btn-sm" href="${viewQueryUrl}"  target="_blank"><i class="fa fa-question-circle fa-lg"></i> View Queries</a>
								</div>
								<div class="meetingButtons">
									<a  class="btn btn-danger btn-sm" href="${viewAddSessionPollsUrl}"  target="_blank"><i class="glyphicon glyphicon-align-left"></i> Session Polls</a>
								</div>
						<%} %>
						<br>
						
						</div>
						<div class="controls" style="padding-bottom: 5px; padding-left: 15px;">
							<%if(roles.indexOf("Faculty") != -1 || roles.indexOf("Acads Admin") != -1){ %>
								<c:if test="${session.ciscoStatus ne 'B' }">
									This session is not created in WebEx yet. 
								</c:if>
								<c:if test="${session.isCancelled ne 'Y'}">
									<br/>
									<!--  Start Meeting Section  Admin login -->
									<%if(roles.indexOf("Acads Admin") != -1){ %>
									
									<c:if test="${not empty session.meetingKey }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${startZoomMeetingUrl}&type=0" target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Original Meeting for ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}</a>
										</div>
									</c:if>
									
									<c:if test="${not empty session.altMeetingKey }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${startZoomMeetingUrl}&type=1" target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 1 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId].fullName}</a>
										</div>
									</c:if>
									<c:if test="${not empty session.altMeetingKey2 }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${startZoomMeetingUrl}&type=2" target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 2 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId2].fullName}</a>
										</div>
									</c:if>
									<c:if test="${not empty session.altMeetingKey3 }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${startZoomMeetingUrl}&type=3" target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 3 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId3].fullName}</a>
										</div>
									</c:if>
									
									<!-- Commented By Somesh For Zoom Integration -->
									<%-- <c:if test="${not empty session.meetingKey }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${startMeetingUrl}&type=0"  target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Original Meeting for ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}</a>
										</div>
									</c:if> 
									
									<c:if test="${not empty session.altMeetingKey }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${startMeetingUrl}&type=1"  target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 1 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId].fullName}</a>
										</div>
									</c:if>
									<c:if test="${not empty session.altMeetingKey2 }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${startMeetingUrl}&type=2"  target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 2 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId2].fullName}</a>
										</div>
									</c:if>
									<c:if test="${not empty session.altMeetingKey3 }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${startMeetingUrl}&type=3"  target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 3 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId3].fullName}</a>
										</div>
									</c:if>
									
									--%>
									
									<%}else if(roles.indexOf("Faculty") != -1){ %>
									<!--  Start Meeting Section  by Respective Faculty login -->
									<fmt:formatDate var="formattedDate" value="${now}" pattern="yyyy-MM-dd"  />
									<c:if test="${session.date eq formattedDate }">
										<c:if test="${not empty session.meetingKey && fn:trim(fn:toUpperCase (session.facultyId)) eq fn:trim (fn:toUpperCase(userId))}">
											<div class="meetingButtons">
												<a  class="btn btn-success btn-sm" href="${startZoomMeetingUrl}&type=0" target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Original Meeting for ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}</a>
											</div>
										</c:if>
									</c:if>
									
									<c:if test="${session.date eq formattedDate }">
										<c:if test="${not empty session.altMeetingKey && fn:trim(fn:toUpperCase(session.altFacultyId)) eq fn:trim (fn:toUpperCase(userId)) }">
											<div class="meetingButtons">
												<a  class="btn btn-success btn-sm" href="${startZoomMeetingUrl}&type=1" target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 1 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId].fullName}</a>
											</div>
										</c:if>
									</c:if>
									
									<c:if test="${session.date eq formattedDate }">
										<c:if test="${not empty session.altMeetingKey2 && fn:trim(fn:toUpperCase(session.altFacultyId2)) eq fn:trim(fn:toUpperCase(userId)) }">
											<div class="meetingButtons">
												<a  class="btn btn-success btn-sm" href="${startZoomMeetingUrl}&type=2" target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 2 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId2].fullName}</a>
											</div>
										</c:if>
									</c:if>
									
									<c:if test="${session.date eq formattedDate }">
										<c:if test="${not empty session.altMeetingKey3 && fn:trim(fn:toUpperCase(session.altFacultyId3)) eq fn:trim(fn:toUpperCase(userId)) }">
											<div class="meetingButtons">
												<a  class="btn btn-success btn-sm" href="${startZoomMeetingUrl}&type=3" target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 3 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId3].fullName}</a>
											</div>
										</c:if>
									</c:if>
										
										<!-- Commented By Somesh For Zoom Integration -->
										<%-- <c:if test="${not empty session.meetingKey && session.facultyId eq userId}">
											<div class="meetingButtons">
												<a  class="btn btn-success btn-sm" href="${startMeetingUrl}&type=0"  target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Original Meeting for ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}</a>
											</div>
										</c:if> 
										</c:if>
										<c:if test="${not empty session.altMeetingKey && session.altFacultyId eq userId }">
											<div class="meetingButtons">
												<a  class="btn btn-success btn-sm" href="${startMeetingUrl}&type=1"  target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 1 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId].fullName}</a>
											</div>
										</c:if>
										<c:if test="${not empty session.altMeetingKey2 && session.altFacultyId2 eq userId }">
											<div class="meetingButtons">
												<a  class="btn btn-success btn-sm" href="${startMeetingUrl}&type=2"  target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 2 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId2].fullName}</a>
											</div>
										</c:if>
										<c:if test="${not empty session.altMeetingKey3 && session.altFacultyId3 eq userId }">
											<div class="meetingButtons">
												<a  class="btn btn-success btn-sm" href="${startMeetingUrl}&type=3"  target="_blank"><i class="fa fa-video-camera fa-lg"></i> Start Parallel Session 3 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId3].fullName}</a>
											</div>
										</c:if> --%>
													
										
									<%} %>
									<br/>
									
									<!-- Commented Re-Create session -->
									<%-- 
									
									<%if(roles.indexOf("Acads Admin") != -1){ %>
									<!--  Refresh Meeting Section  by Admin -->
									<c:if test="${not empty session.meetingKey}">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${refreshurl}&type=0"  onclick="return confirm('Are you sure you want to re-create meeting? A new meeting will be created.')"  ><i class="fa fa-video-camera fa-lg"></i> Re-create Original Meeting for ${mapOfFacultyIdAndFacultyRecord[session.facultyId].fullName}</a>
										</div>
									</c:if>
									<c:if test="${not empty session.altMeetingKey }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${refreshurl}&type=1"  onclick="return confirm('Are you sure you want to re-create meeting? A new meeting will be created.')"  ><i class="fa fa-video-camera fa-lg"></i> Re-create Parallel Session 1 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId].fullName}</a>
										</div>
									</c:if>
									<c:if test="${not empty session.altMeetingKey2 }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${refreshurl}&type=2"  onclick="return confirm('Are you sure you want to re-create meeting? A new meeting will be created.')"  ><i class="fa fa-video-camera fa-lg"></i> Re-create Parallel Session 2 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId2].fullName}</a>
										</div>
									</c:if>
									<c:if test="${not empty session.altMeetingKey3 }">
										<div class="meetingButtons">
											<a  class="btn btn-success btn-sm" href="${refreshurl}&type=3"  onclick="return confirm('Are you sure you want to re-create meeting? A new meeting will be created.')"  ><i class="fa fa-video-camera fa-lg"></i> Re-create Parallel Session 3 for ${mapOfFacultyIdAndFacultyRecord[session.altFacultyId3].fullName}</a>
										</div>
									</c:if>
									<%} %> 
									
									--%>
									
									
								</c:if>
								<%if(roles.indexOf("Faculty") != -1 ){ %>
								<c:if test="${session.isCancelled eq 'Y'}">
								   <div class="meetingButtons" style="color:#d2232a ;font-weight:bold;">
										Session Cancelled
								   </div>
								   <div class="meetingButtons" style="color:#d2232a;">
										Remarks : ${session.reasonForCancellation} 
									</div>
								</c:if>
								<%} %>
								
								<%if(roles.indexOf("Acads Admin") != -1){ %>
									<br/>
									<div class="meetingButtons">
										<a  class="btn btn-info btn-sm" href="${createParallelSessionUrl}&type=1"  ><i class="fa fa-video-camera fa-lg"></i> Create Parallel Session 1</a>
									</div>
									<div class="meetingButtons">
										<a  class="btn btn-info btn-sm" href="${createParallelSessionUrl}&type=2"  ><i class="fa fa-video-camera fa-lg"></i> Create Parallel Session 2</a>
									</div>
									<div class="meetingButtons">
										<a  class="btn btn-info btn-sm" href="${createParallelSessionUrl}&type=3"  ><i class="fa fa-video-camera fa-lg"></i> Create Parallel Session 3</a>
									</div>
							<!-- 	For Delete Parallel Session -->
									<c:if test="${not empty session.altMeetingKey || not empty session.altFacultyId || not empty session.altHostKey}">
										<div class="meetingButtons">
											<a class="btn btn-danger btn-sm" href="${deleteParallelSessionUrl}&type=1" onClick="return confirm('Are you sure you want delete Parallel Session 1 ?')" >
												<i class="fa fa-trash fa-lg"></i> Delete Parallel Session 1
											</a>
										</div>
									</c:if>
									
									<c:if test="${not empty session.altMeetingKey2 || not empty session.altFacultyId2 || not empty session.altHostKey2}">
										<div class="meetingButtons">
											<a class="btn btn-danger btn-sm" href="${deleteParallelSessionUrl}&type=2" onClick="return confirm('Are you sure you want delete Parallel Session 2 ?')" >
												<i class="fa fa-trash fa-lg"></i> Delete Parallel Session 2
											</a>
										</div>
									</c:if>
									
									<c:if test="${not empty session.altMeetingKey3 || not empty session.altFacultyId3 || not empty session.altHostKey3}">
										<div class="meetingButtons">
											<a class="btn btn-danger btn-sm" href="${deleteParallelSessionUrl}&type=3" onClick="return confirm('Are you sure you want delete Parallel Session 3 ?')" >
												<i class="fa fa-trash fa-lg"></i> Delete Parallel Session 3
											</a>
										</div>
									</c:if>
									
								<%} %>
											
								<c:url value="uploadSessionWiseVideoContentForm" var="uploadSessionWiseVideoContentFormUrl">
								  <c:param name="id" value="${session.id}" />
								</c:url>
								
								<br/>
									<div class="meetingButtons">
										<a  class="btn btn-primary btn-sm " href="${uploadSessionWiseVideoContentFormUrl}" target="_blank"> 
											<i class="fa fa-play-circle-o fa-lg"></i> &nbsp; 
											Upload Session Video Details
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

    
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-1.11.2.min.js"></script>
<!-- jQuery Editable element Plugin -->
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.min.js" ></script>

<script>
$(document).ready(function() {
	$('#guestLecture').hide();
    //toggle `popup` / `inline` mode
    $.fn.editable.defaults.mode = 'inline';     
    
    /* //make username editable
    $('#score').editable();
    
    //make username editable
    $('#remarks').editable(); */
    
    $('.editable').each(function() {
        $(this).editable({
	       	success: function(response, newValue) {
	       		console.log(response)
	       		console.log(response.message)
	       		if(response.status == 'Fail') return response.message;
	       	}
        });
    });
});

function showMessage()
{
	$('#guestLecture').show();
}
</script>

</body>
</html>
