<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
<jsp:param value="View Assignment Files" name="title" />
</jsp:include>

<body class="inside">

<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_END_DATE')" var="ASSIGNMENT_END_DATE"/>
<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_END_TIME')" var="ASSIGNMENT_END_TIME"/>


<%
	String sapId = (String)session.getAttribute("userId");
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
%>
	
	
<%@ include file="../header.jsp"%>
	
    <section class="content-container login" >
        <div class="container-fluid customTheme">
			
			<div class="row">
				<legend>Assignments (${currentSemSubjectsCount+failSubjectsCount})</legend>
			</div>

        <%@ include file="../messages.jsp"%>
	
	
	
	<div class="row clearfix">
	<div class="col-md-11 column">
	
	<c:if test="${(currentSemSubjectsCount > 0)  || (failSubjectsCount > 0)}">
	
	<fmt:parseDate value="${endDateTime}" var="endDateTimeParsed" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
	
	
	
	<p class="panel-body" ><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/InternalAssignmentPreparationGuidelines.pdf" target="_blank" ><b><i class="fa-solid fa-downloadfa-lg"></i> Download Assignment Guidelines</b></a> 
	Please view ${yearMonth} Examination Internal Assignment Preparation Guidelines and go through it thoroughly before preparation and submission. <br>
	<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps.pdf" target="_blank" ><b><i class="fa-solid fa-downloadfa-lg"></i> Download Assignment Submission steps</b></a>
	Last Date of Internal Assignment Submission for ${yearMonth} examination is <fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${endDateTimeParsed}" timeStyle="full"/></p>
	</c:if>
	</div>
	
	<div class="col-md-7 column">
		<!-- <h2>Time left to submit Assignment</h2> -->
		<div id="DateCountdown" data-date="${endDateTime}" style="width: 100%; height:120px;"></div>
				
	</div>
	</div>
	
		
	<!-- Table for Fail Subjects -->
	<c:choose>
	<c:when test="${failSubjectsCount > 0}">
	<!-- <p class="bg-danger" style="padding: 15px 15px">Failed Subjects Assignments.&nbsp; (Please note, if you submit assignments for failed subjects, then it is mandatory to attend Term End Exam again for that subject.)</p> -->
	<h2>ANS/Failed Subjects Assignments : (${failSubjectsCount - failSubjectSubmissionCount} Assignments Submission Pending)&nbsp; <h5>(a) Previous Assignment marks in the related subject will be carried forward. However, Term End Examination is mandatory to pass the failed subject OR (b) Student can re-submit the assignment for the failed subject in case s/he so desires. However, Term End Examination is mandatory to pass the failed subject.</h5></h2>
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

								<th>Assignment Question File</th>
								<th>Click to Submit Assignment</th>
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
						
						<c:url value="viewSingleResitAssignment" var="detailsUrl">
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
							<c:if test="${assignmentFile.subject != 'Project' && assignmentFile.subject != 'Module 4 - Project'}">
						        <tr>
						            <td><c:out value="${status.count}"/></td>
									<td><c:out value="${assignmentFile.year}"/></td>
									<td><c:out value="${assignmentFile.month}"/></td>
									<td nowrap="nowrap" style="text-align:left;"><c:out value="${assignmentFile.subject}"/></td>
									<td style="text-align:center;"><c:out value="${assignmentFile.sem}"/></td>
	
									<td>
									<a href="downloadStudentAssignmentFile?filePath=${assignmentFile.filePath}&subject=${assignmentFile.subject}">Download</a>
									</td>
									<td> 
										<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
										 <a class="fa-solid fa-pencil" href="${editurl}" title="Edit"></a> 
										<%}else{ %>
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
										<%} %>
							         </td>
									<td nowrap="nowrap" 
										<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149;color:#fff;"</c:if>
										<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;"</c:if>
										
									>
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
           $("#DateCountdown").TimeCircles({
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
