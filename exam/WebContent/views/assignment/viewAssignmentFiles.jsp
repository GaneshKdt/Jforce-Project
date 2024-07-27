<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="com.nmims.beans.StudentBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="View Assignment Files" name="title" />
</jsp:include>

<body class="inside">

<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_END_DATE')" var="ASSIGNMENT_END_DATE"/>
<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_END_TIME')" var="ASSIGNMENT_END_TIME"/>


<%
	String sapId = (String)session.getAttribute("userId");
	StudentBean student = (StudentBean)session.getAttribute("student");
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
%>
	
	
<%@ include file="../header.jsp"%>
	
	<jsp:useBean id="now" class="java.util.Date" />
	
    <section class="content-container login" >
        <div class="container-fluid customTheme">
			
			<div class="row">
				<legend>Assignments (${currentSemSubjectsCount+failSubjectsCount}) : 
				<font size="2pt" color="white">
					<a style="color:white" href="resources_2015/InternalAssignmentPreparationGuidelines.pdf" target="_blank" ><b><i class="fa fa-download fa-lg"></i> Download Assignment Guidelines</b></a>
					<span style="width:40px;color:#c72027">|</span>
					
					<%if("Jul2014".equals(student.getPrgmStructApplicable())){ %>
						<a style="color:white" href="resources_2015/AssignmentSubmissionSteps_Online.pdf" target="_blank" ><b><i class="fa fa-download fa-lg"></i> Download Assignment Submission & Fee Payment Steps</b></a>
					<%}else{ %>
						<a style="color:white" href="resources_2015/AssignmentSubmissionSteps.pdf" target="_blank" ><b><i class="fa fa-download fa-lg"></i> Download Assignment Submission Steps</b></a>
					<%} %>
					
					
					<span style="width:40px;color:#c72027">|</span>
					<a style="color:white" href="viewPreviousAssignments"><b>View Previous Session Submissions</b></a>
					
				</font>
				</legend>
			</div>

        <%@ include file="../messages.jsp"%>
	
	
	
	
	<!-- <div class ="panel-body">
	<a href="viewPreviousAssignments" class="btn btn-primary">View Previous Session Submissions</a>
	</div> -->
	
	<!-- Table for Current Sem Subjects -->
	<c:choose>
	<c:when test="${currentSemSubjectsCount > 0}">
	
	<div id="DateCountdown1" data-date="${currentSemEndDateTime}" style="width: 100%; height:120px;"></div>
	<fmt:parseDate value="${currentSemEndDateTime}" var="currentSemEndDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
	
	<!-- <p class="bg-primary" style="padding: 15px 15px">Current Sem Subjects Assignments. </p> -->
	<h2>Current Sem Subjects Assignments: (${currentSemSubjectsCount - currentSemSubmissionCount} Assignments Submission Pending)</h2>
	<form:form  action="" method="post" >
	<div class="table-responsive panel-body" >
	<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th style="text-align:left;">Subject</th>
								<th style="text-align:center;">Sem</th>
								<c:if test="${currentSemEndDate gt now}">
									<th>Assignment Question File</th>
								</c:if>
								<th>Click to Submit Assignment</th>
								<th>Status</th>
								<th style="text-align:center;">Submission Attempts left</th>
								<th>Student Assignment Submitted Date & Time</th>
								
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="assignmentFile" items="${currentSemAssignmentFilesList}" varStatus="status">
						
						<c:url value="editAssignmentFileForm" var="editurl">
						  <c:param name="year" value="${assignmentFile.year}" />
						  <c:param name="month" value="${assignmentFile.month}" />
						  <c:param name="subject" value="${assignmentFile.subject}" />
						</c:url>
						
						<c:url value="viewSingleAssignment" var="detailsUrl">
						  <c:param name="year" value="${assignmentFile.year}" />
						  <c:param name="month" value="${assignmentFile.month}" />
						  <c:param name="subject" value="${assignmentFile.subject}" />
						  <c:param name="status" value="${assignmentFile.status}" />
						  <c:param name="startDate" value="${assignmentFile.startDate}" />
						  <c:param name="endDate" value="${assignmentFile.endDate}" />
						</c:url>
									
						<fmt:parseDate value="${assignmentFile.startDate}" var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
						<fmt:parseDate value="${assignmentFile.endDate}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
						<fmt:parseDate value="${assignmentFile.lastModifiedDate}" var="submissionDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
							<c:if test="${assignmentFile.subject != 'Project'}">
						        <tr>
						            <td><c:out value="${status.count}"/></td>
									<td><c:out value="${assignmentFile.year}"/></td>
									<td><c:out value="${assignmentFile.month}"/></td>
									<td nowrap="nowrap" style="text-align:left;"><c:out value="${assignmentFile.subject}"/></td>
									<td style="text-align:center;"><c:out value="${assignmentFile.sem}"/></td>
									
									<c:if test="${currentSemEndDate gt now}">
										<td>
										<a href="downloadStudentAssignmentFile?filePath=${assignmentFile.filePath}&subject=${assignmentFile.subject}">Download</a>
										</td>
									</c:if>
										<td> 
										<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
										 <a class="glyphicon glyphicon-pencil" href="${editurl}" title="Edit"></a> 
										<%}else{ %>
										<a href="${detailsUrl}" >
										<b>
										
											<c:if test="${assignmentFile.status == 'Submitted'}">
												Review Submitted Assignment
											</c:if>
											<c:if test="${assignmentFile.status != 'Submitted'}">
												Submit Assignment
											</c:if>
										
										</b></a>
										<%} %>
							         </td>
									<td nowrap="nowrap" 
										<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149; color:#fff;"</c:if>
										<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;"</c:if>
										
									>
										<c:out value="${assignmentFile.status}"/>
									</td>
									<td style="text-align:center;"><c:out value="${assignmentFile.attemptsLeft}"/></td>
									<td><fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${submissionDate}" timeStyle="full"/></td>
	 
						        </tr>   
						    </c:if>
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	</form:form>
	<br>
	</c:when>
	</c:choose>



	<!-- Table for Fail Subjects -->
	<c:choose>
	<c:when test="${failSubjectsCount > 0}">
	<div id="DateCountdown2" data-date="${failSubjectsEndDateTime}" style="width: 100%; height:120px;"></div>
	<fmt:parseDate value="${failSubjectsEndDateTime}" var="failSubjectEndDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
	
	<!-- <p class="bg-danger" style="padding: 15px 15px">Failed Subjects Assignments.&nbsp; (Please note, if you submit assignments for failed subjects, then it is mandatory to attend Term End Exam again for that subject.)</p> -->
	<h2>ANS/Failed Subjects Assignments : (${failSubjectsCount - failSubjectSubmissionCount} Assignments Submission Pending)&nbsp; </h2>
	<form:form  action="" method="post" >
	<div class="table-responsive  panel-body">
	<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th> 
								<th style="text-align:left;">Subject</th>
								<th style="text-align:center;">Sem</th>
								<c:if test="${failSubjectEndDate gt now}">
									<th>Assignment Question File</th>
								</c:if>
								<th>Action</th>
								<th>Status</th>
								<th style="text-align:center;">Submission Attempts left</th>
								<th>Student Assignment Submitted Date & Time</th>
								
								
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="assignmentFile" items="${failSubjectsAssignmentFilesList}" varStatus="status">
						
						<c:url value="editAssignmentFileForm" var="editurl">
						  <c:param name="year" value="${assignmentFile.year}" />
						  <c:param name="month" value="${assignmentFile.month}" />
						  <c:param name="subject" value="${assignmentFile.subject}" />
						</c:url>
						
						<c:url value="viewSingleAssignment" var="detailsUrl">
						  <c:param name="year" value="${assignmentFile.year}" />
						  <c:param name="month" value="${assignmentFile.month}" />
						  <c:param name="subject" value="${assignmentFile.subject}" />
						  <c:param name="status" value="${assignmentFile.status}" />
						  <c:param name="startDate" value="${assignmentFile.startDate}" />
						  <c:param name="endDate" value="${assignmentFile.endDate}" />
						</c:url>
									
						<fmt:parseDate value="${assignmentFile.startDate}" var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
						<fmt:parseDate value="${assignmentFile.endDate}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
						<fmt:parseDate value="${assignmentFile.lastModifiedDate}" var="submissionDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
							<c:if test="${assignmentFile.subject != 'Project'}">
						        <tr>
						            <td><c:out value="${status.count}"/></td>
									<td><c:out value="${assignmentFile.year}"/></td>
									<td><c:out value="${assignmentFile.month}"/></td>
									<td nowrap="nowrap" style="text-align:left;"><c:out value="${assignmentFile.subject}"/></td>
									<td style="text-align:center;"><c:out value="${assignmentFile.sem}"/></td>
									<c:if test="${failSubjectEndDate gt now}">
										<td>
										<a href="downloadStudentAssignmentFile?filePath=${assignmentFile.filePath}&subject=${assignmentFile.subject}">Download</a>
										</td>
									</c:if>
									
									<c:choose>
							         <c:when test="${assignmentFile.paymentApplicable == 'Yes' && assignmentFile.paymentDone == 'No'}">
									         <td> 
												<a href="/exam/selectAssignmentPaymentSubjectsForm" >
													<b>
														Proceed to Payment
													</b>
												</a>
									         </td>
							         </c:when>
							         <c:otherwise>
							         		<td> 
												<a href="${detailsUrl}" >
													<b>
														<c:if test="${assignmentFile.status == 'Submitted'}">
															Review Submitted Assignment
														</c:if>
														<c:if test="${assignmentFile.status != 'Submitted'}">
															Submit Assignment
														</c:if>
													</b>
												</a>
									         </td>
							         </c:otherwise>
							         </c:choose>
									
									
							         
							         
							         <c:choose>
							         <c:when test="${assignmentFile.paymentApplicable == 'Yes' && assignmentFile.paymentDone == 'No'}">
									         <td style="background-color: #DF3818;color:#fff;">
									         Assignment Submission Fees Pending
									         </td>
							         </c:when>
							         <c:otherwise>
							         		<td nowrap="nowrap" 
												<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149;color:#fff;"</c:if>
												<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;"</c:if>
												
											>
							         </c:otherwise>
							         </c:choose>
							         
							         
							         
									
									<c:out value="${assignmentFile.status}"/></td>
									<td style="text-align:center;"><c:out value="${assignmentFile.attemptsLeft}"/></td>
									<td><fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${submissionDate}" timeStyle="full"/></td>
	 
						        </tr>   
					        </c:if>
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	</form:form>

	</c:when>
	</c:choose>

	
	</div>
	</section>

	<jsp:include page="../footer.jsp" />
	<script>
           $("#DateCountdown1").TimeCircles({
			count_past_zero: false,
			"animation": "ticks",
			"bg_width": 0.9,
			"fg_width": 0.09,
			"circle_bg_color": "#60686F",
			"time": {
				"Days": {
					"text": "Days",
					"color": "#FFCC66",
					"show": true
				},
				"Hours": {
					"text": "Hours",
					"color": "#99CCFF",
					"show": true
				},
				"Minutes": {
					"text": "Minutes",
					"color": "#70B670",
					"show": true
				},
				"Seconds": {
					"text": "Seconds",
					"color": "#FF9999",
					"show": true
				}
			}
		});
           
           $("#DateCountdown2").TimeCircles({
   			count_past_zero: false,
   			"animation": "ticks",
   			"bg_width": 0.9,
   			"fg_width": 0.09,
   			"circle_bg_color": "#60686F",
   			"time": {
   				"Days": {
   					"text": "Days",
   					"color": "#FFCC66",
   					"show": true
   				},
   				"Hours": {
   					"text": "Hours",
   					"color": "#99CCFF",
   					"show": true
   				},
   				"Minutes": {
   					"text": "Minutes",
   					"color": "#70B670",
   					"show": true
   				},
   				"Seconds": {
   					"text": "Seconds",
   					"color": "#FF9999",
   					"show": true
   				}
   			}
   		});

        </script> 

</body>
</html>
 --%>
 
 
 <!DOCTYPE html>
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@page import="com.nmims.beans.AssignmentFileBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_END_DATE')" var="ASSIGNMENT_END_DATE"/>
<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_END_TIME')" var="ASSIGNMENT_END_TIME"/>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="SERVER_PATH"/>
<%
	String sapId = (String)session.getAttribute("userId");
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String studentName = "";
	BaseController assignmentCon = new BaseController();
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
	String linkDisableForLead="";
	if(assignmentCon.checkLead(request, response)){
		linkDisableForLead = "disabled";
	}
%>

<jsp:useBean id="now" class="java.util.Date" />

<html lang="en">
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="View Assignment Files" name="title"/>
    </jsp:include>
    
    <style>
    	a.disabled{
	    		pointer-events: none;
	  			cursor: default;
	  			color: gray;
    	} 
    </style>
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Assignment" name="breadcrumItems"/>
			</jsp:include>
        	
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                             <div id="sticky-sidebar">  
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Assignment" name="activeMenu"/>
							</jsp:include>
              				</div>                           				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %> 
              						
              						
              						<div class="sz-content">
										<h2 class="red text-capitalize" style="width:100%">Assignments (${currentSemAssignmentFilesList.size()+failSubjectsAssignmentFilesList.size()}) 
										
										<span class="pull-right"> (Assignment Preparation Video
<!-- 										<a target="_blank" href="https://nmims.webex.com/nmims/lsr.php?RCID=14a8748aec8987875051cb011cfefc20">Streaming Link</a> |  -->
<!-- 										<a target="_blank" href="https://nmims.webex.com/nmims/ldr.php?RCID=52275717165fde71a7dc03c93f5ba472">Download Link</a>)  -->

											<a class="<%= linkDisableForLead %>" target="_blank" href="${SERVER_PATH}acads/student/watchVideos?id=4588">Streaming Link</a>)

										</span>
										</h2>
										<div class="clearfix"></div>
										<%@ include file="../common/messages.jsp" %>
										
										<!-- Prashant 28-04-2023
										If current subject or resit subject assignemnts size are 0 then show previous submission tab || Card-16615 -->
										<c:if test="${currentSemAssignmentFilesList.size()+failSubjectsAssignmentFilesList.size() == 0}">
										<%-- <c:if test="${quickAssignments.size()==0}">	 --%>
											<div class="panel-content-wrapper" style="min-height:220px;"> 
												
												<div class="col-md-12">
													<ul class="extra-assignment-action">

														<li><a class="<%= linkDisableForLead %>" href="/exam/student/modelAnswers" target="_blank" ><span class="fa-solid fa-file-pdf"></span>Download Sample Answers</a></li>
														<li><a class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/InternalAssignmentPreparationGuidelines.pdf" target="_blank" ><span class="fa-solid fa-file-pdf"></span>Download Assignment Guidelines</a></li>

														
														<%if("Online".equals(student.getExamMode())){ %>
															<%if("CPBM".equals(student.getProgram())){ %>
																<li><a class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank"><span class="fa-solid fa-file-pdf"></span>Download Assignment Submission Steps</a></li>
															<%}else{ %>
																<li><a class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Online.pdf" target="_blank"><span class="fa-solid fa-file-pdf"></span>Download Submission & Fee Payment Steps</a></li>
															<%} %>
														<%}else{ %>
															<li><a class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank"><span class="fa-solid fa-file-pdf"></span>Download Assignment Submission Steps</a></li>
														<%} %>
							
														<li><a class="<%= linkDisableForLead %>" href="viewPreviousAssignments" target="_blank"><span class="fa-solid fa-book-bookmark"></span>View Previous Session Submissions</a></li>
													  </ul>
												</div>
										
											</div>
										</c:if>
										
										<c:if test="${currentSemAssignmentFilesList.size() > 0}">
											<div class="col-lg-5">
												<div class="row">
													<div class="timer-wrapper panel-content-wrapper">
														<span class="icon fa-regular fa-hourglass"></span>
													
														<h3>TIME LEFT FOR SUBMISSION</h3>
														<!-- <div id="currentSubjectTimer" class="exam-assg-timer"></div> -->
										<fmt:parseDate value="${currentSemEndDateTime}" var="endDate"
											pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
										<c:if test="${endDate gt now}">
											<div id="currentSubjectTimer" class="exam-assg-timer"></div>
										</c:if>
										<c:if test="${endDate lt now}">
											<div id="ExpiredErrorMessages">
												<h2 class="red text-capitalize">Assignment Submission Deadline has Passed.</h2>
											</div>
										</c:if>
										<div class="clearfix"></div>
													</div>
												</div>
											</div>
										
										<div class="col-lg-7">
											<ul class="extra-assignment-action">

												<li><a class="<%= linkDisableForLead %>" href="/exam/student/modelAnswers" target="_blank" ><span class="fa-solid fa-file-pdf"></span>Download Model Answers</a></li>
												<li><a class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/InternalAssignmentPreparationGuidelines.pdf" target="_blank" ><span class="fa-solid fa-file-pdf"></span>Download Assignment Guidelines</a></li>
												
												<%if("Online".equals(student.getExamMode())){ %>
													<%if("CPBM".equals(student.getProgram())){ %>
														<li><a class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank"><span class="fa-solid fa-file-pdf"></span>Download Assignment Submission Steps</a></li>
													<%}else{ %>
														<li><a class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Online.pdf" target="_blank"><span class="fa-solid fa-file-pdf"></span>Download Submission & Fee Payment Steps</a></li>
													<%} %>	
												<%}else{ %>
													<li><a class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank"><span class="fa-solid fa-file-pdf"></span>Download Assignment Submission Steps</a></li>
												<%} %>
					
												<li><a class="<%= linkDisableForLead %>" href="viewPreviousAssignments" target="_blank"><span class="fa-solid fa-book-bookmark"></span>View Previous Session Submissions</a></li>
											  </ul>
										</div>
										</c:if>
										
										<div class="clearfix"></div>
										
										
										<c:if test="${currentSemAssignmentFilesList.size() > 0}">
										
										<fmt:parseDate value="${currentSemEndDateTime}" var="currentSemEndDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
										
										<h2 class="text-capitalize">Current Sem Subjects Assignments: (${currentSemAssignmentFilesList.size() - currentSemSubmissionCount} Assignments Submission Pending)</h2>
										
										<div class="clearfix"></div>
										
										<form:form  action="" method="post" >
										<div class="table-responsive panel-content-wrapper" >
										<table class="table table-striped" style="font-size:12px">
															<thead>
																<tr> 
																	<th>Sr. No.</th>
																	<th>Exam Year</th>
																	<th>Exam Month</th>
																	<th style="text-align:left;">Subject</th>
																	<th style="text-align:center;">Sem</th>
																	<c:if test="${currentSemEndDate gt now}">
																		<th>Assignment Question File</th>
																	</c:if>
																	<th>Click to Submit Assignment</th>
																	<th>Status</th>
																	<th style="text-align:center;">Submission Attempts left</th>
																	<th>Student Assignment Submitted Date & Time</th>
																	
																</tr>
															</thead>
															<tbody>
															
															<c:forEach var="assignmentFile" items="${currentSemAssignmentFilesList}" varStatus="status">
															
															<c:url value="editAssignmentFileForm" var="editurl">
															  <c:param name="year" value="${assignmentFile.year}" />
															  <c:param name="month" value="${assignmentFile.month}" />
															  <c:param name="subject" value="${assignmentFile.subject}" />
															</c:url>
															
															<c:url value="student/viewCourseHomePage" var="courseUrl">
																  <%-- <c:param name="subject" value="${assignmentFile.subject}" /> --%>
															</c:url>
															
															<c:url value="viewSingleAssignment" var="detailsUrl">
															  <c:param name="year" value="${assignmentFile.year}" />
															  <c:param name="month" value="${assignmentFile.month}" />
															  <c:param name="subject" value="${assignmentFile.subject}" />
															  <c:param name="status" value="${assignmentFile.status}" />
															  <c:param name="startDate" value="${assignmentFile.startDate}" />
															  <c:param name="endDate" value="${assignmentFile.endDate}" />
															</c:url>
																		
															<fmt:parseDate value="${assignmentFile.startDate}" var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
															<fmt:parseDate value="${assignmentFile.endDate}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
															<fmt:parseDate value="${assignmentFile.submissionDate}" var="submissionDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
																<c:if test="${assignmentFile.subject != 'Project' && assignmentFile.subject != 'Module 4 - Project'}">
															        <tr>
															            <td><c:out value="${status.count}"/></td>
																		<td><c:out value="${assignmentFile.year}"/></td>
																		<td><c:out value="${assignmentFile.month}"/></td>
																		<td nowrap="nowrap" style="text-align:left;"><a class="<%= linkDisableForLead %>" href="/studentportal/${courseUrl}" title="Navigate to Course Home Page"><c:out value="${assignmentFile.subject}"/></a></td>
																		<td style="text-align:center;"><c:out value="${assignmentFile.sem}"/></td>
																		
																		<c:if test="${currentSemEndDate gt now}">
																			<td>
																			<a class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FILES_PATH')" />${assignmentFile.questionFilePreviewPath}">${assignmentFile.month}-${assignmentFile.year} Question File</a>
																			</td> 
																		</c:if>
																			<td> 
																			<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
																			 <a class="<%= linkDisableForLead %>" class="fa-solid fa-pencil" href="${editurl}" title="Edit"></a> 
																			<%}else{ %>
																			<a class="<%= linkDisableForLead %>" href="${detailsUrl}" >
																			
																			<b>
																			
																				<c:if test="${assignmentFile.status == 'Submitted'}">
																					Review Submitted Assignment
																				</c:if>
																				<c:if test="${assignmentFile.status != 'Submitted'}">
																					Submit Assignment
																				</c:if>
																			
																			</b></a>
																			<%} %>
																         </td>
																		<td nowrap="nowrap" 
																			<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149; color:#fff;"</c:if>
																			<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;"</c:if>
																			
																		>
																			<c:out value="${assignmentFile.status}"/>
																		</td>
																		<td style="text-align:center;"><c:out value="${3-assignmentFile.attempts}"/></td>
																		<td><fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${submissionDate}" timeStyle="full"/></td>
										 
															        </tr>   
															    </c:if>
														    </c:forEach>
																
																
															</tbody>
														</table>
										</div>
										</form:form>
										<br>
										</c:if>
										
										
										
										<c:if test="${failSubjectsAssignmentFilesList.size() > 0}">
										
										<div class="col-lg-5">
											<div class="row">
												<div class="timer-wrapper panel-content-wrapper">
													<span class="icon fa-regular fa-hourglass"></span>
													<h3>TIME LEFT FOR SUBMISSION</h3>
													<!-- <div id="failedSubjectTimer" class="exam-assg-timer"></div> -->
										<fmt:parseDate value="${failSubjectsEndDateTime}" var="endDate"
											pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
										<c:if test="${endDate gt now}">
											<div id="failedSubjectTimer" class="exam-assg-timer"></div>
										</c:if>
										<c:if test="${endDate lt now}">
											<div id="ExpiredErrorMessages">
												<h2 class="red text-capitalize">Assignment Submission Deadline has Passed.</h2>
											</div>
										</c:if>
										<div class="clearfix"></div>
												</div>
											</div>
										</div>
										
										<div class="col-lg-7">
											<ul class="extra-assignment-action">

												<li><a class="<%= linkDisableForLead %>" href="/exam/student/modelAnswers" target="_blank" ><span class="fa-solid fa-file-pdf"></span>Download Model Answers</a></li>
												<li><a class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/InternalAssignmentPreparationGuidelines.pdf" target="_blank" ><span class="fa-solid fa-file-pdf"></span>Download Assignment Guidelines</a></li>
												
												<%if("Online".equals(student.getExamMode())){ %>
													<%if("CPBM".equals(student.getProgram())){ %>
														<li><a class="<%= linkDisableForLead %>" class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank"><span class="fa-solid fa-file-pdf"></span>Download Assignment Submission Steps</a></li>
													<%}else{ %>
														<li><a class="<%= linkDisableForLead %>" class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Online.pdf" target="_blank"><span class="fa-solid fa-file-pdf"></span>Download Submission & Fee Payment Steps</a></li>
													<%} %>
												<%}else{ %>
													<li><a class="<%= linkDisableForLead %>" class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank"><span class="fa-solid fa-file-pdf"></span>Download Assignment Submission Steps</a></li>
												<%} %>
					
												<li><a class="<%= linkDisableForLead %>" class="<%= linkDisableForLead %>" href="viewPreviousAssignments" target="_blank"><span class="fa-solid fa-book-bookmark"></span>View Previous Session Submissions</a></li>
											  </ul>
										</div>
										
										<fmt:parseDate value="${failSubjectsEndDateTime}" var="failSubjectEndDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
										<%if("ACBM".equals(student.getProgram())){ %>
										     <h2 class="text-capitalize">ANS/Applicable Subject Assignments &nbsp; </h2>
										<%}else { %>
										    <h2 class="text-capitalize">ANS/Result Awaited/Failed Subjects Assignments : (${failSubjectsAssignmentFilesList.size() - failSubjectSubmissionCount} Assignments Submission Pending)&nbsp; </h2>
										<%} %>
											
										<div class="clearfix"></div>
										<form:form  action="" method="post" >
										<div class="table-responsive  panel-content-wrapper">
										<table class="table table-striped" style="font-size:12px">
															<thead>
																<tr> 
																	<th>Sr. No.</th>
																	<th>Exam Year</th>
																	<th>Exam Month</th> 
																	<th style="text-align:left;">Subject</th>
																	<th style="text-align:center;">Sem</th>
																	<c:if test="${failSubjectEndDate gt now}">
																		<th>Assignment Question File</th>
																	</c:if>
																	<th>Action</th>
																	<th>Status</th>
																	<th style="text-align:center;">Submission Attempts left</th>
																	<th>Student Assignment Submitted Date & Time</th>
																	
																	
																</tr>
															</thead>
															<tbody>
															
															<c:forEach var="assignmentFile" items="${failSubjectsAssignmentFilesList}" varStatus="status">
															
															<c:url value="editAssignmentFileForm" var="editurl">
															  <c:param name="year" value="${assignmentFile.year}" />
															  <c:param name="month" value="${assignmentFile.month}" />
															  <c:param name="subject" value="${assignmentFile.subject}" />
															</c:url>
															
															<c:url value="student/viewCourseHomePage" var="courseUrl">
																  <%-- <c:param name="subject" value="${assignmentFile.subject}" /> --%>
															</c:url>
															
															<c:url value="viewSingleAssignment" var="detailsUrl">
															  <c:param name="year" value="${assignmentFile.year}" />
															  <c:param name="month" value="${assignmentFile.month}" />
															  <c:param name="subject" value="${assignmentFile.subject}" />
															  <c:param name="status" value="${assignmentFile.status}" />
															  <c:param name="startDate" value="${assignmentFile.startDate}" />
															  <c:param name="endDate" value="${assignmentFile.endDate}" />
															</c:url>
															
															<c:url value="selectAssignmentPaymentSubjectsForm" var="selectAssignmentPaymentSubjectsFormUrl">
															  <c:param name="year" value="${assignmentFile.year}" />
															  <c:param name="month" value="${assignmentFile.month}" />
															  <c:param name="subject" value="${assignmentFile.subject}" />
															  <c:param name="status" value="${assignmentFile.status}" />
															  <c:param name="startDate" value="${assignmentFile.startDate}" />
															  <c:param name="endDate" value="${assignmentFile.endDate}" />
															</c:url>
																		
															<fmt:parseDate value="${assignmentFile.startDate}" var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
															<fmt:parseDate value="${assignmentFile.endDate}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
															<fmt:parseDate value="${assignmentFile.submissionDate}" var="submissionDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
																<c:if test="${assignmentFile.subject != 'Project'}">
															        <tr>
															            <td><c:out value="${status.count}"/></td>
																		<td><c:out value="${assignmentFile.year}"/></td>
																		<td><c:out value="${assignmentFile.month}"/></td>
																		<td nowrap="nowrap" style="text-align:left;"><a href="/studentportal/${courseUrl}" title="Navigate to Course Home Page"><c:out value="${assignmentFile.subject}"/></a></td>
																		<td style="text-align:center;"><c:out value="${assignmentFile.sem}"/></td>
																		<c:if test="${failSubjectEndDate gt now}">
																			<td>
																			<a class="<%= linkDisableForLead %>" class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FILES_PATH')" />${assignmentFile.questionFilePreviewPath}">${assignmentFile.month}-${assignmentFile.year} Question File</a>
																			</td>     
																		</c:if>
																		
																		<td> 
																		<fmt:parseDate value="${assignmentFile.endDate}" pattern="yyyy-MM-dd HH:mm:ss" var="aEndDate"/>
   
																			<c:choose>
																			<c:when test="${assignmentFile.submissionAllow eq 'N'}">      
															         		 	<a class="<%= linkDisableForLead %>" class="<%= linkDisableForLead %>" href="#" onclick="alert('You can submit assignment for this subject, once results for previous cycle submission are declared.');">
															         		 		<b>Results Awaited</b> 
															         		 	</a>  
															         		</c:when>
															         		<c:otherwise>
															         			<c:choose>
															         				
																		        	<c:when test="${assignmentFile.paymentApplicable eq 'Y' && assignmentFile.paymentDone eq 'N' && aEndDate > now}">
																							<a class="<%= linkDisableForLead %> proceedToPayment" subject="${assignmentFile.subject}" year = "${assignmentFile.year}" month =" ${assignmentFile.month}"  status="${assignmentFile.status}"  startDate="${assignmentFile.startDate}" endDate="${assignmentFile.endDate}">
																								<b>Proceed to Payment</b>
																							</a>
																		         	</c:when>
																		        <c:otherwise>
																							<a class="<%= linkDisableForLead %>" href="${detailsUrl}" >
																								<b>
																									<c:if test="${assignmentFile.status == 'Submitted'}">
																										Review Submitted Assignment
																									</c:if>
																									<c:if test="${assignmentFile.status != 'Submitted'}">
																										Submit Assignment
																									</c:if>
																								</b>
																							</a>
																		        </c:otherwise>
																		        </c:choose>
															         		</c:otherwise>
															         		</c:choose>
															         	</td>
																		
																        <c:choose>
																        <c:when test="${assignmentFile.status eq 'Results Awaited'}">
																        	<td style="background-color: #26a9e0;color:#fff;">
														         		 	<b>Results Awaited</b>
														         		 	</td>
														         		</c:when>
														         		<c:otherwise>
														         			<c:choose>
																	        <c:when test="${assignmentFile.paymentApplicable == 'Y' && assignmentFile.paymentDone == 'N' && aEndDate gt now }">
																			         <td style="background-color: #DF3818;color:#fff;">
																			         Assignment Submission Fees Pending
																			         </td>
																	        </c:when>
																	        <c:otherwise>
																	         		<td nowrap="nowrap" 
																						<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149;color:#fff;"</c:if>
																						<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;"</c:if>
																						
																					><c:out value="${assignmentFile.status}"/></td>
																	        </c:otherwise>
																	        </c:choose>
														         		</c:otherwise>
																		</c:choose>
																		<td style="text-align:center;"><c:out value="${assignmentFile.attemptsLeft}"/></td>
																		<td><fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${submissionDate}" timeStyle="full"/></td>
										 
															        </tr>   
														        </c:if>
														    </c:forEach>
																
																
															</tbody>
														</table>
										</div>
										</form:form>
									
										</c:if>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
        
        <div id="disclaimer" class="modal fade" role="dialog">
		  <div class="modal-dialog">
		
		    <!-- Modal content-->
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal">&times;</button>
		        <h4 class="modal-title">Disclaimer</h4>
		      </div>
		      <div class="modal-body">
		        <p>Before proceeding towards assignment fee payment, 
		        please check whether you have submitted assignment for the same subject/s in the 
		        previous exam cycle and wish to re-submit again the assignment/s for the same. 
		        Kindly check the exam cycle month and year before proceeding.
 				</p>
		      </div>
		      <input type="hidden" id="selectAssignmentPaymentSubjectsFormParameter"/>
		      <div class="modal-footer">
		      <form style="display: hidden" action="/the/url" method="POST" id="form">
  					<input type="hidden" id="var1" name="var1" value=""/>
  					<input type="hidden" id="var2" name="var2" value=""/>
			  </form>
			  <div class=" row text-center">
			  
		      <div class="btn-group"  >
		      	<a id="agree" class="btn btn-success" style="background-color:red;">Agreed</a>
		        <a  class="btn btn-info" id="closeModal" data-dismiss="modal">Close</a>
		     
		      </div>
		      </div>
		      </div>
		    </div>
		  </div>
		</div>
		
		
            
  	
        <jsp:include page="../common/footer.jsp"/>
            
		<script>
		$(document).ready(function(){
			$("#selectAssignmentPaymentSubjectsFormParameter").val('');
			var currentSemEndDateTime = '${currentSemEndDateTime}'
			if(currentSemEndDateTime) {
				var currentSemEndDate = moment(currentSemEndDateTime, 'YYYY-MM-DD HH:mm:ss').toDate();
				$('#currentSubjectTimer').countdown('destroy');
				$('#currentSubjectTimer').countdown({until: currentSemEndDate});
				$('#currentSubjectTimer').countdown('toggle');
			}
			var failSubjectsEndDateTime = '${failSubjectsEndDateTime}'
			if(failSubjectsEndDateTime) {
				var failSubjectsEndDate = moment(failSubjectsEndDateTime, 'YYYY-MM-DD HH:mm:ss').toDate();
				$('#failedSubjectTimer').countdown('destroy');
				$('#failedSubjectTimer').countdown({until: failSubjectsEndDate});
				$('#failedSubjectTimer').countdown('toggle');	
			}
		});

		$('.proceedToPayment').on('click', function(){
			//var subj = $(this).attr("subject").replace("\'","%27");
			subject = encodeURIComponent($(this).attr("subject"))
			//console.log("------------------------------------------------------->subject")
			//console.log(subject)
			//alert($(this).attr("subject") + $(this).attr("year") + $(this).attr("month") + $(this).attr("status") +  $(this).attr("startDate") +  $(this).attr("endDate"));
			var url = "selectAssignmentPaymentSubjectsForm?subject="+ subject +"&year="+$(this).attr("year")+"&month="+$(this).attr("month")+"&status="+ $(this).attr("status")+"&startDate="+$(this).attr("startDate")+"&endDate="+$(this).attr("endDate");
			//console.log(url);
			showDisclaimer(url);
		});
		function showDisclaimer(x){
			console.log(x);
			document.getElementById("selectAssignmentPaymentSubjectsFormParameter").value = x;
			$("#agree").attr("href", x)

			$("#disclaimer").modal('show');

		}
		
		
			
		$("#closeModal").click(function(){
			$("#selectAssignmentPaymentSubjectsFormParameter").val('');
		});
		
		
	
		</script>
    </body>
</html>