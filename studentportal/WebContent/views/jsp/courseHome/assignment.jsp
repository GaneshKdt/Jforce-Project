<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.AssignmentStudentPortalFileBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%

String assignmentSubject = (String)request.getAttribute("subject");
List<AssignmentStudentPortalFileBean> studentAssignmentMarksBeanList = (List<AssignmentStudentPortalFileBean>)request.getSession().getAttribute("studentAssignmentMarksBeanList");
HashMap<String, AssignmentStudentPortalFileBean> courseAssignmentsMap = (HashMap<String, AssignmentStudentPortalFileBean>)session.getAttribute("courseAssignmentsMap");
AssignmentStudentPortalFileBean assignment = null;

if(courseAssignmentsMap!=null){
	assignment = courseAssignmentsMap.get(assignmentSubject);
}
StudentStudentPortalBean student = (StudentStudentPortalBean)session.getAttribute("student_studentportal");

int attemptsLeft = 0;
String assignmentStatus = "NA";
String hasAssignmentClass= "";
if(assignment != null){
	attemptsLeft = 3-Integer.parseInt(assignment.getAttempts());
	assignmentStatus = assignment.getStatus();
	if(!assignment.isSubmissionAllowed()) {
		assignmentStatus = "Results Awaited";
	}
	hasAssignmentClass = "accordion-has-content";
}

SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

String disable = "";
String href="";
BaseController assCon = new BaseController();
if(assCon.checkLead(request, response)){
	disable = "disable";
}

%>
<c:url value="student/viewSingleAssignment" var="assignmentSubmissionUrl">
	<c:param name="fromCourseHomePage" value="true" />
	<%if(assignment != null){%>
		<c:param name="year" value="<%=assignment.getYear() %>" />
		<c:param name="month" value="<%=assignment.getMonth() %>" />
		<c:param name="status" value="<%=assignment.getStatus() %>" />
		<c:param name="startDate" value="<%=assignment.getStartDate() %>" />
		<c:param name="endDate" value="<%=assignment.getEndDate() %>" />
	<%}%>
	<c:param name="subject" value="<%=assignmentSubject %>" />
</c:url>

<style>
.disable{
	pointer-events: none !important;
    cursor: default;
    color:Gray;
}
</style>

<div class="course-assignments-m-wrapper">
	<div class=" panel-courses-page">
		<div class="panel-heading" role="tab" id="">
		<h2> Assignments &nbsp;&nbsp; <span class="pull-right red">
			<%if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){ %>
				(Assignment Preparation Video <a target="_blank" class="<%= disable %>"

				href="https://studentzone-ngasce.nmims.edu/acads/student/watchVideos?id=4588">Streaming Link</a>)

			<% } %>
		</span>
		</h2>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li>
					<h3 class="green">
						<span>Status:</span>
						<%=assignmentStatus%></h3>
				</li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseTwo" aria-expanded="true"></a></li>
				<div class="clearfix"></div>
			</ul>

			<div class="clearfix"></div>
		</div>
		<div class="clearfix"></div>
		<%if(assignment == null){ %>
		<div id="collapseTwo"
			class=" collapse in academic-schedule courses-panel-collapse panel-content-wrapper accordion-has-content "
			role="tabpanel">
			<div class="panel-body" style="padding: 20px;"> 
				
					<div class="no-data-wrapper">

						<h6 class="no-data nodata"><span class="icon-exams"></span> No Assignments to show</h6>
					</div>

				

				</div>
		</div>
		<% }
		if(assignment != null){ %>
		<div id="collapseTwo"
			class="panel-collapse collapse in academic-schedule courses-panel-collapse panel-content-wrapper <%=hasAssignmentClass %>"
			role="tabpanel">
			<%}else{%>
			<div id="collapseTwo"
				class="panel-collapse collapse academic-schedule courses-panel-collapse panel-content-wrapper <%=hasAssignmentClass %>"
				role="tabpanel">
				<%} %>
				<div class="panel-body">
					<%if(assignment == null){ %>
					<div class="no-data-wrapper">
						<p class="no-data">
							<span class="fa-solid fa-book-bookmark"></span>No Assignments Scheduled for
							<%=assignmentSubject %></p>
					</div>
					<%}else{ 
				
				Date formattedDate = formatter.parse(assignment.getEndDate());
				String formattedDateString = dateFormatter.format(formattedDate);
			
				%>

				<div class="data-content" >
<!-- 					<div class="col-md-12 p-closed">
						<div class="timer-wrapper">
							<h3>TIME LEFT FOR SUBMISSION</h3>
							<i class="icon-clock"></i>
							<div class="exam-assg-timer" id="assignmentTimer1"></div>
							<div class="clearfix"></div>
						</div>

					</div> -->
						<div class="col-md-12 margin-bottom">
							<div class="row row-eq-height">
								<div class="col-lg-5">
									<div class="row">
										<div class="timer-wrapper">
											<span class="icon fa-solid fa-hourglass"></span>
											<h3>TIME LEFT FOR SUBMISSION</h3>
											<div class="exam-assg-timer" id="assignmentTimer2"></div>
											<div class="clearfix"></div>
										</div>
									</div>
								</div>
								<div class="col-lg-7">
									<div class="row">
										<ul class="extra-assignment-action ">
											<li><a href="/exam/student/modelAnswers" target="_blank" class="<%= disable %>"><span
													class="fa-solid fa-file-pdf"></span>Download Model Answers</a></li>
											<li><a class="<%= disable %>"
												href="/exam/resources_2015/InternalAssignmentPreparationGuidelines.pdf"
												target="_blank"><span class="fa-solid fa-file-pdf" class="<%= disable %>"></span>Download
													Assignment Guidelines</a></li>
											<%if("Online".equals(student.getExamMode())){ %>
												<%if("CPBM".equals(student.getProgram())){ %>
													<li>
														<a class="<%= disable %>" href="/exam/resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank">
															<span class="fa-solid fa-file-pdf"></span>
															Download Assignment Submission Steps
														</a>
													</li>
												<%}else{ %>
													<li>
														<a class="<%= disable %>" href="/exam/resources_2015/AssignmentSubmissionSteps_Online.pdf" target="_blank">
															<span class="fa-solid fa-file-pdf"></span>
															Download Submission & Fee Payment Steps
														</a>
													</li>
												<%} %>
											<%}else{ %>
												<li>
													<a class="<%= disable %>" href="/exam/resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank">
														<span class="fa-solid fa-file-pdf"></span>
														Download Assignment Submission Steps
													</a>
												</li>
											<%} %>

											<li><a class="<%= disable %>" href="/exam/student/viewPreviousAssignments"><span
													class="fa-solid fa-file-pdf"></span>View all Session Submission</a></li>
											<div class="clearfix"></div>
										</ul>
									</div>
								</div>
							</div>
						</div>
						<div class="clearfix"></div>
						<div class="col-md-12">
							<div class="row row-eq-height">
								<div class="col-lg-7">
									<div class="row">
										<div class="col-md-8">
											<h3 class="assignment-heading"><%=assignmentSubject %></h3>
											<p class="assignment-content">Please read assignment
												guidelines before submission</p>
										</div>
										<div class="col-md-4">
											<h3 class="assignment-heading">Month & Year</h3>
											<p class="assignment-content"><%=assignment.getMonth()%>-<%=assignment.getYear()%></p>
										</div>
										<div class="col-md-2">
											<h3 class="assignment-heading">Semester</h3>
											<p class="assignment-content assignment-figures"><%=assignment.getSem() %></p>
										</div>
										<div class="col-md-6">
											<h3 class="assignment-heading">Due By</h3>
											<p class="assignment-content assignment-figures"><%=formattedDateString%></p>
										</div>
										<div class="col-md-4">
											<h3 class="assignment-heading">Attempts Left</h3>
											<p class="assignment-content assignment-figures"><%=attemptsLeft%></p>
										</div>
										<div class="clearfix"></div>
									</div>
								</div>
								<div class="col-lg-5">
									<div class="row full-height">
										<div class="col-md-6 col-sm-6 full-height">
											<div class="row full-height">
												<div class="assignment-action borderRight">
													<a target="_blank" class="<%= disable %>"
														href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FILES_PATH')"/><%=assignment.getQuestionFilePreviewPath()%>"> 
														<span class="fa-solid fa-download"></span>Download Question File
													</a>

												</div>
											</div>
										</div>
										<div class="col-md-6 col-sm-6 full-height">
											<div class="row full-height">
												<div class="assignment-action">
													<a href="/exam/${assignmentSubmissionUrl }" class="<%= disable %>"><span
														class="fa-solid fa-upload"></span>Upload Assignment</a>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div><br/>
							<div>
								<table class="table table-result" style="width:100%">
									<thead>
										<td>Year</td>
										<td>Month</td>
										<td>Assignmentscore</td>
										<td>Remarks</td>
									</thead>
									<tbody>
										<% try{ %>
											<% for(int i=0;i < studentAssignmentMarksBeanList.size();i++){ %>
												<tr>
													<td><%=studentAssignmentMarksBeanList.get(i).getYear() %></td>
													<td><%=studentAssignmentMarksBeanList.get(i).getMonth() %></td>
													<td><%=studentAssignmentMarksBeanList.get(i).getScore() %></td>
													<td><%=studentAssignmentMarksBeanList.get(i).getRemarks() %></td>
												</tr>
											<% } %>
										<% }catch(Exception e) { } %>
									</tbody>
								</table>
							</div>
						</div>
						<div class="clearfix"></div>
					</div>

					<%} %>
				</div>
			</div>
		</div>
	</div>	