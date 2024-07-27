
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
    <jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="View Assignment Files" name="title"/>
    </jsp:include>
    
    <style>
a.disabled {
	pointer-events: none;
	cursor: default;
	color: gray;
}
</style>
    
    <body>
    
    	<%@ include file="../common/headerDemo.jsp" %>
    	
    	
        
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
										<h5 class="text-danger fw-bolder text-capitalize mt-3 mb-3" >Assignments (${currentSemAssignmentFilesList.size()+failSubjectsAssignmentFilesList.size()}) 
										
										<span class="pull-right"> (Assignment Preparation Video
<!-- 										<a target="_blank" href="https://nmims.webex.com/nmims/lsr.php?RCID=14a8748aec8987875051cb011cfefc20">Streaming Link</a> |  -->
<!-- 										<a target="_blank" href="https://nmims.webex.com/nmims/ldr.php?RCID=52275717165fde71a7dc03c93f5ba472">Download Link</a>)  -->

											<a class="<%= linkDisableForLead %>" target="_blank" href="${SERVER_PATH}acads/student/watchVideos?id=4588">Streaming Link</a>)

										</span>
										</h5>
										<div class="clearfix"></div>
										<%@ include file="../common/messages.jsp" %>
										
										<!-- Prashant 28-04-2023
										If current subject or resit subject assignemnts size are 0 then show previous submission tab || Card-16615 -->
										<c:if test="${currentSemAssignmentFilesList.size()+failSubjectsAssignmentFilesList.size() == 0}">
										<%-- <c:if test="${quickAssignments.size()==0}">	 --%>
											
											<div class="container-fluid pt-4 pb-4 h-100 bg-light rounded" > 
												
												<div class="col-md-12  ">
													<ul class="list-group list-group-horizontal-sm d-flex justify-content-evenly row" id="spaceBetween">

														
														<a class="<%= linkDisableForLead %> text-dark mt-2 col-12 col-xl-3 col-lg-3 col-md-3" href="/exam/student/modelAnswers" target="_blank" >
														<li class="list-group-item text-center h-100 pe-4 ps-4 pt-4 pb-4 rounded">
														<span class="fa-solid fa-file-pdf fs-1">
														</span>
														<h6 class="mt-3 text-black ">Download Sample Answers</h6>
														</li>
														</a>
														
														
														<a class="<%= linkDisableForLead %> text-dark mt-2 col-12 col-xl-3 col-lg-3 col-md-3" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/InternalAssignmentPreparationGuidelines.pdf" target="_blank" >
														<li class="list-group-item text-center h-100 pe-4 ps-4 pt-4 pb-4 rounded">
														<span class="fa-solid fa-file-pdf fs-1"></span>
														<h6 class="mt-3 text-black ">Download Assignment Guidelines</h6>
														</li>
														</a>

														
														<%if("Online".equals(student.getExamMode())){ %>
															<%if("CPBM".equals(student.getProgram())){ %>
																<a class="<%= linkDisableForLead %> text-dark mt-2 col-12 col-xl-3 col-lg-3 col-md-3" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank">
																<li class="list-group-item text-center h-100 pe-4 ps-4 pt-4 pb-4 rounded">
																<span class="fa-solid fa-file-pdf fs-1"></span>
																<h6 class="mt-3 text-black ">Download Assignment Submission Steps</h6>
																</li></a>
															<%}else{ %>
																<a class="<%= linkDisableForLead %> text-dark mt-2 col-12 col-xl-3 col-lg-3 col-md-3" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Online.pdf" target="_blank">
																<li class="list-group-item text-center h-100 pe-4 ps-4 pt-4 pb-4 rounded">
																<span class="fa-solid fa-file-pdf fs-1"></span>
																<h6 class="mt-3 text-black ">Download Submission & Fee Payment Steps</h6>
																</li></a>
															<%} %>
														<%}else{ %>
															<a class="<%= linkDisableForLead %> text-dark mt-2 col-12 col-xl-3 col-lg-3 col-md-3" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank">
															<li class="list-group-item text-center h-100 pe-4 ps-4 pt-4 pb-4 rounded">
															<span class="fa-solid fa-file-pdf fs-1"></span>
															<h6 class="mt-3 text-black ">Download Assignment Submission Steps</h6>
															</li></a>
														<%} %>
							
														<a class="<%= linkDisableForLead %> text-dark mt-2 col-12 col-xl-3 col-lg-3 col-md-3" href="viewPreviousAssignments" target="_blank">
														<li class="list-group-item text-center h-100 pe-4 ps-4 pt-4 pb-4 rounded">
														<span class="fa-solid fa-book-bookmark fs-1"></span>
														<h6 class="mt-3 text-black ">View Previous Session Submissions</h6>
														</li></a>
													  </ul>
												</div>
										
											</div>
										</c:if>
										<c:if test="${currentSemAssignmentFilesList.size() > 0}">
							<div class="row">
								 <div class="col-xl-5 col-lg-12 col-md-12" id="timeContainer">
									<div class="card">
										<div class="card-body">
											<div class="row">
												<div class="col-lg-2 fs-1 d-flex">
													<span class="icon fa-regular fa-hourglass text-secondary" style="font-size: 5rem;" id="timerIcon"></span>
												</div>
											<div class="col-lg-10">
											<div class="row">
											<div class="col-lg-12">
											<p class="text-secondary">TIME LEFT FOR SUBMISSION</p>
											</div>
											<div class="col-lg-12 fs-3">
											<!-- <div id="currentSubjectTimer" class="exam-assg-timer"></div> -->
											<fmt:parseDate value="${currentSemEndDateTime}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
											<c:if test="${endDate gt now}">
												<div id="currentSubjectTimer" class="exam-assg-timer"></div>
											</c:if>
											<c:if test="${endDate lt now}">
												<div id="ExpiredErrorMessages">
													<h5 class="text-danger fw-bolder text-capitalize">Assignment Submission
														Deadline has Passed.</h5>
												</div>
											</c:if>
											</div>
											</div>
											
											</div>
											</div>
										</div>
									</div>
								</div> 
								
								
								<div class="col-xl-7 col-lg-12 col-md-12" id="ulList">
									<ul class="list-group list-group-horizontal-sm">

										<a class="<%=linkDisableForLead%> text-dark" href="/exam/student/modelAnswers" target="_blank">
											<li class="list-group-item text-center h-100">
												<span class="fa-solid fa-file-pdf fs-1 "></span>
												<h6 class="mt-3 text-black ">Download Model Answers</h6>
											</li>
										 </a>


										<a class="<%=linkDisableForLead%> text-dark"
											href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/InternalAssignmentPreparationGuidelines.pdf"
											target="_blank">
											<li class="list-group-item text-center h-100">
											<span class="fa-solid fa-file-pdf fs-1 "></span>
											     <h6 class="mt-3 text-black ">Download Assignment Guidelines</h6>
											 </li>
										</a>
										<%
											if ("Online".equals(student.getExamMode())) {
										%>
										<%
											if ("CPBM".equals(student.getProgram())) {
										%>
										
										<a class="<%=linkDisableForLead%> text-dark" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf"
											target="_blank">
											<li class="list-group-item text-center h-100">
												<span class="fa-solid fa-file-pdf fs-1"></span>
												<h6 class="mt-3 text-black ">Download Assignment Submission Steps</h6>
											</li>
										</a>
												
										<%
											} else {
										%>
										
											<a class="<%=linkDisableForLead%> text-dark"
												href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Online.pdf"
												target="_blank">
												<li class="list-group-item text-center h-100">
													<span class="fa-solid fa-file-pdf fs-1"></span>
													 <h6 class="mt-3 text-black">Download Submission & Fee Payment Steps</h6>
												</li>
											</a>
									
										<%
											}
										%>
										<%
											} else {
										%>
										
										<a class="<%=linkDisableForLead%> text-dark" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf"
											target="_blank">
											<li class="list-group-item text-center h-100">
											<span class="fa-solid fa-file-pdf fs-1"></span>
											<h6 class="mt-3 text-black">Download Assignment Submission Steps</h6>
											</li></a>
										<%
											}
										%>

										
										<a class="<%=linkDisableForLead%> text-dark" href="viewPreviousAssignments" target="_blank">
										<li class="list-group-item text-center h-100 ">
											<span class="fa-solid fa-book-bookmark fs-1"></span>
											<h6 class="mt-3 text-black">View Previous Session Submissions</h6>
											</li>
										</a>
										
									</ul>
								</div>
							</div>
						</c:if>
										<div class="clearfix"></div>
										
										
										<c:if test="${currentSemAssignmentFilesList.size() > 0}">
										
										<fmt:parseDate value="${currentSemEndDateTime}" var="currentSemEndDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
										
										<h5 class="text-capitalize text-danger fw-bolder mt-3 mb-3">Current Sem Subjects Assignments: (${currentSemAssignmentFilesList.size() - currentSemSubmissionCount} Assignments Submission Pending)</h5>
										
										<div class="clearfix"></div>
										
										<form:form  action="" method="post" >
										<div class="table-responsive container-fluid bg-light rounded ">
										<table class="table  table-striped mt-3 mb-3 ml-2 mr-2 rounded">
															<thead>
																<tr> 
																	<th>Sr. No.</th>
																	<th>Exam Year</th>
																	<th>Exam Month</th>
																	<th class="text-left">Subject</th>
																	<th class="text-center">Sem</th>
																	<c:if test="${currentSemEndDate gt now}">
																		<th>Assignment Question File</th>
																	</c:if>
																	<th>Click to Submit Assignment</th>
																	<th>Status</th>
																	<th class="text-center">Submission Attempts left</th>
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
																		<td class="text-nowrap text-left"><a class="<%= linkDisableForLead %>" href="/studentportal/${courseUrl}" title="Navigate to Course Home Page"><c:out value="${assignmentFile.subject}"/></a></td>
																		<td class="text-center"><c:out value="${assignmentFile.sem}"/></td>
																		
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
																		<%-- <td class="text-nowrap"
																			<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149; color:#fff;"</c:if>
																			<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;"</c:if>
																			
																		>
																			<c:out value="${assignmentFile.status}"/>
																		</td> --%>

																		<c:choose>
																			<c:when test="${assignmentFile.status == 'Submitted'}">
																				<td class="text-nowrap bg-success">
																					<p class="text-white text-center mt-2">
																						<c:out value="${assignmentFile.status}" />
																					</p>
																				</td>
																			</c:when>
																			<c:when test="${assignmentFile.status != 'Submitted'}">
																				<td class="text-nowrap bg-danger">
																					<p class="text-white text-center mt-2">
																						<c:out value="${assignmentFile.status}" />
																					</p>
																				</td>
				
																			</c:when>
																		</c:choose>


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
									<div class="row">
										
										<div class="col-xl-5 col-lg-12 col-md-12" id="timeContainer">
			
												<div class="card">
												<div class="card-body">
												<div class="row">
												<div class="col-lg-2 fs-1">
													<span class="icon fa-regular fa-hourglass text-secondary" style="font-size: 5rem;" id="timerIcon"></span>
												</div>
												<div class="col-lg-10">
												<div class="row">
													<div class="col-lg-12">
													<p class="text-secondary">TIME LEFT FOR SUBMISSION</p>
													</div>
													<div class="col-lg-12 fs-3">
												
												<!-- <div id="failedSubjectTimer" class="exam-assg-timer"></div> -->
												<fmt:parseDate value="${failSubjectsEndDateTime}" var="endDate"
														pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
													<c:if test="${endDate gt now}">
														<div id="failedSubjectTimer" class="exam-assg-timer"></div>
													</c:if>
													<c:if test="${endDate lt now}">
														<div id="ExpiredErrorMessages">
															<h5 class="text-danger fw-bolder text-capitalize">Assignment Submission Deadline has Passed.</h5>
														</div>
													</c:if>
												<div class="clearfix"></div>
												</div>
												</div>
											</div>
											</div>
											</div>
											</div>
										</div>
			
										<div class="col-xl-7 col-lg-12 col-md-12">
											<ul class="list-group list-group-horizontal-sm">

												
												<a class="<%= linkDisableForLead %> text-dark" href="/exam/student/modelAnswers" target="_blank" >
												<li class="list-group-item text-center h-100">
												<span class="fa-solid fa-file-pdf fs-1"></span>
												<h6 class="mt-3 text-black ">Download Model Answers</h6>
												</li>
												</a>
												
												
												<a class="<%= linkDisableForLead %> text-dark" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/InternalAssignmentPreparationGuidelines.pdf" target="_blank" >
												<li class="list-group-item text-center h-100" >
												<span class="fa-solid fa-file-pdf fs-1"></span>
												<h6 class="mt-3 text-black ">Download Assignment Guidelines</h6>
												</li>
												</a>
												
												<%if("Online".equals(student.getExamMode())){ %>
													<%if("CPBM".equals(student.getProgram())){ %>
														
														<a class="<%= linkDisableForLead %> text-dark" class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank">
														<li class="list-group-item text-center h-100">
														<span class="fa-solid fa-file-pdf fs-1"></span>
														<h6 class="mt-3 text-black ">Download Assignment Submission Steps</h6>
														</li>
														</a>
													<%}else{ %>
														
														<a class="<%= linkDisableForLead %> text-dark"  class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Online.pdf" target="_blank">
														<li class="list-group-item text-center h-100">
														<span class="fa-solid fa-file-pdf fs-1"></span>
														<h6 class="mt-3 text-black ">Download Submission & Fee Payment Steps</h6>
														</li>
														</a>
													<%} %>
												<%}else{ %>
													
													<a class="<%= linkDisableForLead %> text-dark" class="<%= linkDisableForLead %>" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank">
													<li class="list-group-item text-center h-100">
													<span class="fa-solid fa-file-pdf fs-1"></span>
													<h6 class="mt-3 text-black ">Download Assignment Submission Steps</h6>
													</li>
													</a>
												<%} %>
					
												
												<a class="<%= linkDisableForLead %> text-dark" class="<%= linkDisableForLead %>" href="viewPreviousAssignments" target="_blank">
												<li class="list-group-item text-center h-100">
												<span class="fa-solid fa-book-bookmark fs-1"></span>
												<h6 class="mt-3 text-black ">View Previous Session Submissions</h6>
												</li>
												</a>
											  </ul>
										</div>
										</div>
										<fmt:parseDate value="${failSubjectsEndDateTime}" var="failSubjectEndDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
										<%if("ACBM".equals(student.getProgram())){ %>
										     <h2 class="text-capitalize">ANS/Applicable Subject Assignments &nbsp; </h2>
										<%}else { %>
										    <h5 class="text-capitalize text-danger fw-bolder mt-3 mb-3">ANS/Result Awaited/Failed Subjects Assignments : (${failSubjectsAssignmentFilesList.size() - failSubjectSubmissionCount} Assignments Submission Pending)&nbsp; </h5>
										<%} %>
											
										<div class="clearfix"></div>
										<form:form  action="" method="post" >
										<div class="table-responsive container-fluid bg-light rounded ">
										<table class="table  table-striped mt-3 mb-3 ml-2 mr-2 rounded">
															<thead>
																<tr> 
																	<th>Sr. No.</th>
																	<th>Exam Year</th>
																	<th>Exam Month</th> 
																	<th class="text-left">Subject</th>
																	<th class="text-center">Sem</th>
																	<c:if test="${failSubjectEndDate gt now}">
																		<th>Assignment Question File</th>
																	</c:if>
																	<th>Action</th>
																	<th>Status</th>
																	<th class="text-center">Submission Attempts left</th>
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
																		<td class="text-left text-nowrap"><a href="/studentportal/${courseUrl}" title="Navigate to Course Home Page"><c:out value="${assignmentFile.subject}"/></a></td>
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
																							<!-- <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#disclaimer">
																							  Open modal
																							</button> -->
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
																        	<td class="bg-primary text-light">
														         		 	<b>Results Awaited</b>
														         		 	</td>
														         		</c:when>
														         		<c:otherwise>
														         			<c:choose>
																	        <c:when test="${assignmentFile.paymentApplicable == 'Y' && assignmentFile.paymentDone == 'N' && aEndDate gt now }">
																			         <td class="bg-danger text-light">
																			         Assignment Submission Fees Pending
																			         </td>
																	        </c:when>
																	        <%-- <c:otherwise>
																	         		<td class="text-nowrap" 
																						<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149;color:#fff;"</c:if>
																						<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;"</c:if>
																						
																					><c:out value="${assignmentFile.status}"/></td>
																	        </c:otherwise> --%>
																	        
																	        <c:otherwise>
																	        <c:choose>
																			<c:when test="${assignmentFile.status == 'Submitted'}">
																				<td class="text-nowrap bg-success">
																					<p class="text-white text-center mt-2">
																						<c:out value="${assignmentFile.status}" />
																					</p>
																				</td>
																			</c:when>
																			<c:when test="${assignmentFile.status != 'Submitted'}">
																				<td class="text-nowrap bg-danger">
																					<p class="text-white text-center mt-2">
																						<c:out value="${assignmentFile.status}" />
																					</p>
																				</td>
				
																			</c:when>
																			</c:choose>
																			</c:otherwise>
																	        
																	        </c:choose>
														         		</c:otherwise>
																		</c:choose>
																		<td class="text-center"><c:out value="${assignmentFile.attemptsLeft}"/></td>
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
        
        	
					
					
					<div class="modal" id="disclaimer">
                      <div class="modal-dialog">
                        <div class="modal-content">


                          <div class="modal-header">
                            <h4 class="modal-title text-danger fw-bolder">Disclaimer</h4>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                          </div>


                          <div class="modal-body">
                            <p class="text-dark fw-bolder">Before proceeding towards assignment fee payment, 
                                please check whether you have submitted assignment for the same subject/s in the 
                                previous exam cycle and wish to re-submit again the assignment/s for the same. 
                                Kindly check the exam cycle month and year before proceeding.
                           </p>
                          </div>

                          <input type="hidden" id="selectAssignmentPaymentSubjectsFormParameter"/>
                          
                          <div class="modal-footer ">
                              <div class="col-2">
                                  <form style="display: hidden" action="/the/url" method="POST" id="form">
                                      <input type="hidden" id="var1" name="var1" value=""/>
                                      <input type="hidden" id="var2" name="var2" value=""/>
                                      </form>
                              </div>

                             <div class=" col-10">
                                  <div class="row ">
                                      <div class="col-8 col-xl-9 col-lg-9 col-md-9">
                                          <a id="agree" class="btn btn-danger rounded" >Agreed</a>
                                      </div>
                                      <div class="col-4 col-xl-3 col-lg-3 col-md-3">
                                          <a  class="btn btn-dark rounded" data-bs-dismiss="modal">Close</a>
                                          
                                     </div>
                                </div>
                             </div>
                        </div>
                        
                      </div>
                    </div> </div>
		
		
            
  	
        <jsp:include page="../common/footerDemo.jsp"/>
            
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
			console.log("ajsas");
			//var subj = $(this).attr("subject").replace("\'","%27");
			subject = encodeURIComponent($(this).attr("subject"))
			//console.log("------------------------------------------------------->subject")
			//console.log(subject)
			//alert($(this).attr("subject") + $(this).attr("year") + $(this).attr("month") + $(this).attr("status") +  $(this).attr("startDate") +  $(this).attr("endDate"));
			var url = "selectAssignmentPaymentSubjectsForm?subject="+ subject +"&year="+$(this).attr("year")+"&month="+$(this).attr("month")+"&status="+ $(this).attr("status")+"&startDate="+$(this).attr("startDate")+"&endDate="+$(this).attr("endDate");
			//console.log(url);
			showDisclaimer(url);
		});
		function showDisclaimer(redirectUrl){
			
			var myModal = new bootstrap.Modal(document.getElementById('disclaimer'))
			console.log('myModal',myModal);
			  myModal.show();
			 
			document.getElementById("selectAssignmentPaymentSubjectsFormParameter").value = redirectUrl;
			$("#agree").attr("href", redirectUrl)
			
		//	$("#disclaimer").modal('show');
			  
			  $("#closeModal").click(function(){
			$("#selectAssignmentPaymentSubjectsFormParameter").val('');
		});
			

		}
		
		
			
		
		
		
	
		</script>
		
		
	
    </body>
</html>