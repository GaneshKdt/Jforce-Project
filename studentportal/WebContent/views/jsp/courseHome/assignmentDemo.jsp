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
	


	
	<!-------------------------------------Assignment header card----------------------------------->
<div class="container-fluid bg-white rounded"  >
		<div class="row">
		<div class="col-lg-12">
		  <h4 class="text-danger text-uppercase mt-3 mb-5"> Assignments  <span>
					<%if(!"BBA".equalsIgnoreCase(student.getProgram()) && !"B.Com".equalsIgnoreCase(student.getProgram())){ %>
						(Assignment Preparation Video <a target="_blank" class=" text-capitalize<%= disable %>"
		
						href="https://studentzone-ngasce.nmims.edu/acads/student/watchVideos?id=4588">Streaming Link</a>)
		
					<% } %>
					<span class="text-success text-capitalize">Status : <%=assignmentStatus%></span>
				    </span>
		   </h4>
		
			
			<ul  class="topRightLinks list-unstyled">
				 <li>
				 <a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseTwo" aria-expanded="true">
			      </a>
			     </li>		
	           </ul>
		</div>	
		</div>
		
		<!----------------------------------shows only when there is no assignments to show------------------------------------>
	
		<%if(assignment == null){ %>
		<div id="collapseTwo" class=" collapse   "role="tabpanel">
			
				
					<div class="no-data-wrapper">

						<h6 class="no-data nodata fw-bold"><span class="fa-solid fa-chart-simple"></span> No Assignments to show</h6>
					</div>
				
		  </div>

		 		
		
		<% }
		if(assignment != null){ %>
	<div id="collapseTwo" class=" collapse    <%=hasAssignmentClass %>" role="tabpanel"></div>
			<%}else{%>
			<div id="collapseTwo"class=" collapse    <%=hasAssignmentClass %>"role="tabpanel"></div>
				<%} %>
				
					<%if(assignment == null){ %>
								
						<h6 class="no-data nodata fw-bold"><i class="fa-solid fa-chart-simple me-2" ></i>No Assignments Scheduled for
							<%=assignmentSubject %></h6>
				
				    <br><br>
					<%}else{ 
				
				Date formattedDate = formatter.parse(assignment.getEndDate());
				String formattedDateString = dateFormatter.format(formattedDate);
			
				%>
          
    
     


	
     <!----------------------------------------------------------------Main card starts here ----------------------------------------------------------------->
		 
						     	
					    
					     	   <div class="row mb-4">
					     		 
								        <div class="col-lg-5 col-md-12 col-sm-3"> 
									    
										    <div class="timer-wrapper">
											<span class="icon fa-solid fa-hourglass"></span>
											<h3>TIME LEFT FOR SUBMISSION</h3>
											<div class="exam-assg-timer" id="assignmentTimer2"></div>											
										</div>
								
								
								</div>
								
								
					
								<div class="col-lg-7 mt-md-3 mt-sm-3">
								
								
									<ul class="list-group list-group-horizontal-sm text-center">									
										
										  <li class="list-group-item">
											 <a href="/exam/student/modelAnswers" target="_blank" class="<%= disable %>">
											 <i class="fa-solid fa-file-pdf fa-2xl text-black">
											  </i>
												<h6 class="mt-3 text-black">Download Model Answers</h6>
												</a>
											</li>
                                            
								
											
										
											  <li class="list-group-item">
											<a class="<%= disable %>"
												href="/exam/resources_2015/InternalAssignmentPreparationGuidelines.pdf"
												target="_blank"><i class="fa-solid fa-file-pdf fa-2xl text-black" class="<%= disable %>"></i>
												<h6 class="mt-3 text-black">Download Assignment Guidelines</h6></a>
											</li>
										
											<%if("Online".equals(student.getExamMode())){ %>
												<%if("CPBM".equals(student.getProgram())){ %>
													
													  <li class="list-group-item">
														<a class="<%= disable %> " href="/exam/resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank">
															<i class="fa-solid fa-file-pdf fa-2xl text-black"></i>
															<h6 class="mt-3 text-black">Download Assignment Submission Steps</h6>
														</a>
												   </li>												
												
												<%}else{ %>
													
													  <li class="list-group-item">
														<a class="<%= disable %> " href="/exam/resources_2015/AssignmentSubmissionSteps_Online.pdf" target="_blank"  w>
															<i class="fa-solid fa-file-pdf fa-2xl text-black"></i>
															<h6 class="mb-0 mt-3 text-black">Download Submission & </h6>
															<h6 class="mt-0 text-black"> Fee Payment Steps</h6>
														</a>
													</li>
												
												<%} %>
											<%}else{ %>
												
												  <li class="list-group-item">
													<a class="<%= disable %> " href="/exam/resources_2015/AssignmentSubmissionSteps_Offline.pdf" target="_blank">
														<i class="fa-solid fa-file-pdf fa-2xl text-black"></i>
														<h6 class="mt-3 text-black">Download Assignment Submission Steps</h6>
													</a>
												 </li>
											
											<%} %>
									
							
									      <li class="list-group-item">
											<a class="<%= disable %> " href="/exam/student/viewPreviousAssignments">
											<i class="fa-solid fa-file-pdf fa-2xl text-black"></i>
											<h6 class="mt-3 text-black">View all Session Submission</h6>
											</a>
										  </li>								 
									     </ul>
								 
							   
							 </div>		
				       	</div>
				
			          	<div class="row  mb-4"> 
			          	
							    	<div class="col-lg-7">
							    	<div class="card card-body">
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
								
							</div>	
								
								<div class="col-lg-5 mt-3">
									<div class="row text-center" >
										<div class="col-md-5 col-sm-6 ">
											
												
													<a target="_blank" class="<%= disable %>"
														href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FILES_PATH')"/><%=assignment.getQuestionFilePreviewPath()%>"> 
														<i class="fa-solid fa-download fs-1 text-black"></i>
														<h6 class="mt-3 text-black">Download Question File</h6>
													</a>

										
										</div>
										<div class="col">
										<div class="vr h-100"></div>
										</div> 
										<div class="col-md-5 col-sm-6  ">
											<%if(!assignment.getStatus().equals("Results Awaited")){ %>
											
													<a href="/exam/${assignmentSubmissionUrl }" class="<%= disable %>"><i
														class="fa-solid fa-upload fs-1 text-black"></i><h6 class="mt-3 text-black">Upload Assignment</h6></a>
											<% }%>
										</div>
									</div>
								</div>
							  </div>
			
			
		           
						
                            	<div class="table-responsive">  
								<table class="table table-result"style="width:100%;" >

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
								   	<br>
					</div>					   	
					<%} %>
				
		</div>
		
