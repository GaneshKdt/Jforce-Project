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
	<div class="assignments">
		<div class="d-flex align-items-center text-wrap">
			<span class="fw-bold me-3"><small class="fs-5">ASSIGNMENTS</small></span>
				<div class=" d-flex ms-auto">
					<a href="#" class="text-dark me-2 " data-bs-toggle="modal"
					data-bs-target="#assignmentsModal"><small class="text-nowrap">SEE ALL</small></a> 
					<a href="#" data-bs-toggle="collapse" data-bs-target="#collapseFive" role="button" aria-expanded="true" aria-controls="collapseFive" id="collapseCard" class="text-muted">
					<i class="fa-solid fa-square-minus"></i></a>
				</div>
		</div>
	
	<%if(noOfAssignments == 0){ %>
			<div class="card card-body  text-center ">
			
				<h6><i class="fa-solid fa-book-bookmark"></i>
				<a href="/exam/student/viewAssignmentsForm"><small class="text-dark">View Assignments</small></a></h6>
			</div>
	
		
	<%} else {%>
		<div class="collapse" id="collapseFive">
			<div class="card card-body text-center " >
			<h6><i class="fa-solid fa-book-bookmark"></i>
				<small> <%=noOfAssignments %> Assignments</small></h6>
			</div>
		</div> 
	
		<div class="collapse show" id="collapseFive" >
			<ul class="list-group ">
			<% 
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
			int assignmentCount = 0;
			for(AssignmentStudentPortalFileBean assignment :allAssignmentFilesList){ 
				assignmentCount++;
				String status = assignment.getStatus();
				String assignmentStatusClass = "red";
				String assignmentAction = "Pending";
				String statusColor="bg-danger";
				String textColor="text-danger";
			
				
				if("Submitted".equalsIgnoreCase(status)){
					assignmentStatusClass = "green";
					assignmentAction = "Preview";
					statusColor="bg-success";
					textColor="text-success";
					
				}
				
				if("Result Awaited".equalsIgnoreCase(status)){
					status = "Results Awaited";
					assignmentStatusClass = "yellow";
					assignmentAction = "Results Awaited";
					statusColor="bg-warning";
					textColor="text-warning";
				
				}  
				
				if(bcon.checkLead(request, response)){
					assignmentStatusClass = "black";
					assignmentAction = "Preview";
					leadAssignment = "background-color: rgba(210,210,210,0.5)";
					assignmentAction = "Status";
					status = "Preview";
					styleForDate = "padding-right: 12px;";
					statusColor="bg-dark";
					textColor="text-dark";
				
				}
				Date formattedDate = formatter.parse(assignment.getEndDate());
				String formattedDateString = dateFormatter.format(formattedDate);
				
			%>
		
		
  				<li class="list-group-item">
   					<div class="row">
   						<div class="col-sm-4 col-lg-6 ">
   							<a href="<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />exam/student/viewAssignmentsForm" >
      						<span class=" text-dark "><%=assignment.getSubject() %></span> <span class="text-uppercase text-muted fw-bold">semester <%=assignment.getSem() %></span></a>
      						<h6 class="<%=textColor%> mt-2"><%=status %></h6>
      					</div>
      					<div class="col-sm-8 col-lg-6 ">
      						<span class="badge rounded-pill float-lg-end float-md-end <%=statusColor%>">
								<% if(bcon.checkLead(request, response)) 
										formattedDateString = "Date";
								%>
								<% if(bcon.checkLead(request, response)) { %>
										<a  href="#"  class="assignmentBtn text-white "  id="notifyLeads"><%=assignmentAction %></a>
							<% }else if("Submitted".equalsIgnoreCase(status)){%>
								<a href="#"  class="assignmentBtn text-white  " onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" /><%=assignment.getPreviewPath()%>', 'Preview', 200, 200);">
									<%=assignmentAction %></a>
							<%}else{ %> 
								<a href="#"  class="assignmentBtn text-white" onClick="return false;" style="cursor: default;"><%=assignmentAction %></a>
								<%} %>
						</span>
      					<br />
	      					<% if(bcon.checkLead(request, response)) 
							formattedDateString = "Date";
							%>
      							<span style="<%= styleForDate %>" class="float-lg-end float-md-end "> Due <%=formattedDateString%></span>
     				</div>
     			</div>
 		 	</li>
		
				<%if(assignmentCount == 6){
        			break;
        		} } %>
        		</ul>
		</div>
				<%} %>		
	</div>

	<!--MODAL FOR ASSIGNMENTS -->
<%if(noOfAssignments > 0){ %>
<div class="modal fade assignments" id="assignmentsModal" tabindex="-1" role="dialog">
	<div class="modal-dialog" >
		<div class="modal-content">
			<div class="modal-header">
				<h1 class="modal-title fs-5" id="modalLabel">ASSIGNMENTS</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			</div>
			
	
			<div class="modal-body">

				<% 
      	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		for(AssignmentStudentPortalFileBean assignment :allAssignmentFilesList){ 
			String status = assignment.getStatus();
			String assignmentStatusClass = "text-danger";
			String assignmentAction = "Pending";
		String statusColor="bg-danger";
			
			if("Submitted".equalsIgnoreCase(status)){
				assignmentStatusClass = "text-success";
				assignmentAction = "Preview";
				statusColor="bg-success";
			}
			
			if("Results Awaited".equalsIgnoreCase(status)){
				 status = "Results Awaited";
				assignmentStatusClass = "text-warning";
				assignmentAction = "Results Awaited";
				statusColor="bg-warning";
			}  
			
			  if(bcon.checkLead(request, response)){
					assignmentStatusClass = "text-dark";
					assignmentAction = "Preview";
					statusColor="bg-dark";
				}
			  
			Date formattedDate = formatter.parse(assignment.getEndDate());
			String formattedDateString = dateFormatter.format(formattedDate);
			
		%>
<div class="card card-body mb-2">
  				<li class="list-group-item">
   					<div class="row">
   						<div class=" col-lg-7 ">
   							<a href="<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />exam/student/viewAssignmentsForm" >
      						<small><span class=" text-dark"><%=assignment.getSubject() %></span> <span class="text-uppercase text-muted fw-bold">semester <%=assignment.getSem() %></span></a>
      						<h6 class="<%=assignmentStatusClass%> mt-2"><%=status %></h6></small>
      					</div>
      					<div class=" col-lg-5 ">
      				<small><span class="badge rounded-pill float-lg-end float-md-end <%=statusColor%> ">
					<% if("Submitted".equalsIgnoreCase(status)){%>
						<a href="#" class="assignmentBtn text-white" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />/<%=assignment.getPreviewPath()%>', 'Preview', 200, 200);"><%=assignmentAction %></a>
						<%}else{ %> 
						<a href="#" class="assignmentBtn text-white"
						onClick="return false;" ><%=assignmentAction %></a>
						<%} %>
						</span></small>
      					<br />
      					<span class="float-lg-end float-md-end "><small> Due <%=formattedDateString%></small></span>
     				</div>
     			</div>
 		 	</li>
</div>
				<%
		} %>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-bs-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>
<% } %>


<!-- <script>
	function notifyLeads(){
		alert("Please enrole for the complete course...");
	}
</script> -->
		<script type="text/javascript" src="${pageContext.request.contextPath }/assets/js/assignment.js"></script>
