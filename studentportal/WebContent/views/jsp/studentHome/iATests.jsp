
<% try{ %>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@page import="com.nmims.beans.AssignmentStudentPortalFileBean"%>
<%@page import="com.nmims.beans.TestStudentPortalBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>


<%
	List<TestStudentPortalBean> testsForStudent = (List<TestStudentPortalBean>)request.getSession().getAttribute("testsForStudent");
	String collpaseTesttSection = "";

	int noOfTests =0;
	if(testsForStudent!=null){
		noOfTests = testsForStudent.size(); 
	}
	else {
		noOfTests = 0;
	}
	
	if(noOfTests > 0){
		collpaseTesttSection = "in";//Adding "in" class will expand Assignment section. Expand only when assignments are present
	}
%>

<div class="assignments open">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">Internal Assignments (MCQ&rsquo;s)</h4>
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
			class="panel-collapse collapse <%=collpaseTesttSection %> courses-panel-collapse"
			role="tabpanel">
			<div class="panel-body">

				<%if(noOfTests == 0){ %>

				<div class="no-data-wrapper leftBorder">
					<p class="no-data">
						<span class="fa-solid fa-book-bookmark"></span>No Tests Scheduled!
					</p>
				</div>

				<%}else{ %>
				<!--Assignments Sections-->
				<section class="red p-closed">

					<div class="no-data-wrapper">
						<p class="no-data">
							<span class="fa-solid fa-book-bookmark"></span>
							<%=noOfTests %>
							Tests
						</p>
					</div>
				</section>

				<% 
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		int assignmentCount = 0;
		for(TestStudentPortalBean test :testsForStudent){ 
			assignmentCount++;
			String status = "";
			String assignmentStatusClass = "red";
			String assignmentAction = "Pending";
			if(test.getMaxAttempt() > test.getAttempt() && test.getAttempt() == 0){
				status = "Pending";
			}
			if(test.getMaxAttempt() == test.getAttempt() ){
				status = "Submitted";
			}
			if(test.getMaxAttempt() > test.getAttempt() && test.getAttempt() > 0){
				status = "Give Test Again";
			}

			if("Submitted".equalsIgnoreCase(status)){
				assignmentStatusClass = "green";
				assignmentAction = "View Details";
			}

			if("Give Test Again".equalsIgnoreCase(status)){
				assignmentStatusClass = "yellow";
				assignmentAction = "Give Test Again";
			}   
			
			Date formattedDate = formatter.parse(test.getEndDate().replaceAll("T", " "));
			String formattedDateString = dateFormatter.format(formattedDate);
			
		%>


				<section class="<%=assignmentStatusClass%>">
					<h4>
						<a
							href="<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />exam/viewTestDetailsForStudents?id=<%= test.getId() %>&message=openTestDetails"><%=test.getTestName() %>
							<span> <%=test.getSubject() %></span></a>
					</h4>
					<h5><%=status %></h5>
					<span class="submitBtns"> <a class="assignmentBtn"
						href="<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />exam/viewTestDetailsForStudents?id=<%= test.getId() %>&message=openTestDetails"><%=assignmentAction %></a>

						<h5>
							Due
							<%=formattedDateString%></h5>
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

<%-- 
 <!--MODAL FOR ASSIGNMENTS -->
 	<%if(noOfTests > 0){ %>
<div class="modal fade assignments" id="assignmentsModal" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Tests</h4>
      </div>
      <div class="modal-body">
      
      	<% 
      	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		for(TestBean test :testsForStudent){ 
			String status = "";
			String assignmentStatusClass = "red";
			String assignmentAction = "Pending";
			
			if("Submitted".equalsIgnoreCase(status)){
				assignmentStatusClass = "green";
				assignmentAction = "Preview";
			}
			
			  /* if(subjectsNotAllowedToSubmitDashboard.contains(assignment.getSubject())){
				 status = "Results Awaited";
				assignmentStatusClass = "yellow";
				assignmentAction = "Results Awaited";
			}  */ 
			
			Date formattedDate = formatter.parse(test.getEndDate().replaceAll("T", " "));
			String formattedDateString = dateFormatter.format(formattedDate);
			
		%>
			
			
	        <section class="<%=assignmentStatusClass%>">
	          <a href="<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />exam/viewAssignmentsForm"><h4><%=test.getSubject() %> <span> <%=test.getTestName() %></span></h4></a>
	          <h5><%=status %></h5>
	          <span class="submitBtns"> 
	          <% if("Submitted".equalsIgnoreCase(status)){%>
	           		<a href="#" class="assignmentBtn" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />/<%=test.getTestName()%>', 'Preview', 200, 200);"><%=assignmentAction %></a>
	           <%}else{ %>
	           		<a href="#" class="assignmentBtn" onClick="return false;" style="cursor: default;"><%=assignmentAction %></a>
	           <%} %>
	          <h5>Due <%=formattedDateString%></h5>
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
<%} %> --%>
<%}catch(Exception e){} %>
