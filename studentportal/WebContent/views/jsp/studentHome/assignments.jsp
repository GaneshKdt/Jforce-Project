<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page import="com.nmims.beans.AssignmentStudentPortalFileBean"%>
<%@page import="java.util.ArrayList"%>

<%
	String collpaseAssignmentSection = "";
	ArrayList<AssignmentStudentPortalFileBean> allAssignmentFilesList = (ArrayList<AssignmentStudentPortalFileBean>)session.getAttribute("quickAssignments_studentportal");
	//ArrayList<String> subjectsNotAllowedToSubmitDashboard = (ArrayList<String>) request.getSession().getAttribute("subjectsNotAllowedToSubmitDashboard");
	//System.out.println("subjectsNotAllowedToSubmitDashboard in jsp "+subjectsNotAllowedToSubmitDashboard);
 	BaseController bcon = new BaseController();
	ArrayList<AssignmentStudentPortalFileBean> leadAssignmet = null;
	if(bcon.checkLead(request, response)){
		allAssignmentFilesList =  leadAssignmet;
	}
	String leadAssignment = ""; 
	String styleForDate = "";
	int noOfAssignments =0;
	if(allAssignmentFilesList!=null){
		noOfAssignments = allAssignmentFilesList.size(); 
	}
	else {
		noOfAssignments = 0;
	}
	
	if(noOfAssignments > 0){
		collpaseAssignmentSection = "in";//Adding "in" class will expand Assignment section. Expand only when assignments are present
	}
%>

<div class="assignments open">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">ASSIGNMENTS</h4>
			<ul class="topRightLinks list-inline">
				<li><a href="#" data-toggle="modal"
					data-target="#assignmentsModal">SEE ALL</a></li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" data-parent="#accordion"
					href="#collapseThree" aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div id="collapseThree"
			class="panel-collapse collapse <%=collpaseAssignmentSection %> courses-panel-collapse"
			role="tabpanel">
			<div class="panel-body">

				<%if(noOfAssignments == 0){ %>

				<div class="no-data-wrapper leftBorder">
					<p class="no-data">
<!-- 					<span class="icon-my-courses"></span>No Assignment Found! -->
						<span class="fa-solid fa-book-bookmark"></span>
						<a href="/exam/student/viewAssignmentsForm">View Assignments</a>
					</p>
				</div>

				<%}else{ %>
				<!--Assignments Sections-->
				<section class="red p-closed">

					<div class="no-data-wrapper" >
						<p class="no-data">
							<span class="fa-solid fa-book-bookmark"></span>
							<%=noOfAssignments %> Assignments
						</p>
					</div>
				</section>

		<% 
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		int assignmentCount = 0;
		for(AssignmentStudentPortalFileBean assignment :allAssignmentFilesList){ 
			assignmentCount++;
			String status = assignment.getStatus();
			String assignmentStatusClass = "red";
			String assignmentAction = "Pending";
			
			if("Submitted".equalsIgnoreCase(status)){
				assignmentStatusClass = "green";
				assignmentAction = "Preview";
			}
			
			if("Result Awaited".equalsIgnoreCase(status)){
				status = "Results Awaited";
				assignmentStatusClass = "yellow";
				assignmentAction = "Results Awaited";
			}  
			
			if(bcon.checkLead(request, response)){
				assignmentStatusClass = "black";
				assignmentAction = "Preview";
				leadAssignment = "background-color: rgba(210,210,210,0.5)";
				assignmentAction = "Status";
				status = "Preview";
				styleForDate = "padding-right: 12px;";
			}
			Date formattedDate = formatter.parse(assignment.getEndDate());
			String formattedDateString = dateFormatter.format(formattedDate);
			
		%>

				<section class="<%=assignmentStatusClass%>" style="<%= leadAssignment %>">
					<h4>
						<a href="<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />exam/student/viewAssignmentsForm"><%=assignment.getSubject() %>
							<span>semester <%=assignment.getSem() %></span>
						</a>
						
					</h4>
					<h5><%=status %></h5>
					<span class="submitBtns"> 
					<% if(bcon.checkLead(request, response)) { %>
						<a  style="background: black;" href="#" class="assignmentBtn" onClick="notifyLeads()">
							<%=assignmentAction %>
						</a>
					<% }else if("Submitted".equalsIgnoreCase(status)){%>
						<a href="#" class="assignmentBtn" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" /><%=assignment.getPreviewPath()%>', 'Preview', 200, 200);">
							<%=assignmentAction %>
						</a>
					<%}else{ %> 
						<a href="#" class="assignmentBtn" onClick="return false;" style="cursor: default;">
							<%=assignmentAction %>
						</a>
						<%} %>

						<% 
							if(bcon.checkLead(request, response)) 
								formattedDateString = "Date";
						%>
						<h5 style="<%= styleForDate %>"> Due <%=formattedDateString%></h5>
					</span>
					<div class="clearfix"></div>
				</section>

				<%
        		if(assignmentCount == 6){
        			break;
        		}
		} %>


				<%} %>
			</div>
		</div>
	</div>
</div>


<!--MODAL FOR ASSIGNMENTS -->
<%if(noOfAssignments > 0){ %>
<div class="modal fade assignments" id="assignmentsModal" tabindex="-1" role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">ASSIGNMENTS</h4>
			</div>
			<div class="modal-body">

				<% 
      	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		for(AssignmentStudentPortalFileBean assignment :allAssignmentFilesList){ 
			String status = assignment.getStatus();
			String assignmentStatusClass = "red";
			String assignmentAction = "Pending";
			
			if("Submitted".equalsIgnoreCase(status)){
				assignmentStatusClass = "green";
				assignmentAction = "Preview";
			}
			
			if("Results Awaited".equalsIgnoreCase(status)){
				 status = "Results Awaited";
				assignmentStatusClass = "yellow";
				assignmentAction = "Results Awaited";
			}  
			
			  if(bcon.checkLead(request, response)){
					assignmentStatusClass = "black";
					assignmentAction = "Preview";
				}
			  
			Date formattedDate = formatter.parse(assignment.getEndDate());
			String formattedDateString = dateFormatter.format(formattedDate);
			
		%>


				<section class="<%=assignmentStatusClass%>">
					<a
						href="<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />exam/student/viewAssignmentsForm"><h4><%=assignment.getSubject() %>
							<span>semester <%=assignment.getSem() %></span>
						</h4></a>
					<h5><%=status %></h5>
					<span class="submitBtns"> <% if("Submitted".equalsIgnoreCase(status)){%>
						<a href="#" class="assignmentBtn"
						onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />/<%=assignment.getPreviewPath()%>', 'Preview', 200, 200);"><%=assignmentAction %></a>
						<%}else{ %> <a href="#" class="assignmentBtn"
						onClick="return false;" style="cursor: default;"><%=assignmentAction %></a>
						<%} %>
						<h5>
							Due
							<%=formattedDateString%></h5>
					</span>
					<div class="clearfix"></div>
				</section>

				<%
		} %>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>
<% } %>


<script>
	function notifyLeads(){
		alert("Please enrole for the complete course...");
	}
</script>
