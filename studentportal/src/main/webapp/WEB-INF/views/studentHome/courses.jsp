<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
System.out.println("courses");
ArrayList<String> subjects = (ArrayList<String>)session.getAttribute("studentCourses_studentportal");
int noOfSubjects = subjects.size();

%>

<div class="courses">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">COURSES</h4>
			<ul class="topRightLinks list-inline">
				<li><a href="courses-sessions.html" data-toggle="modal"
					data-target="#coursesModal">SEE ALL</a></li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" data-parent="#accordion" href="#collapseTwo"
					aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div id="collapseTwo"
			class="panel-collapse collapse in courses-panel-collapse"
			role="tabpanel">
			<!--panel body-->
			<div class="panel-body no-border">

				<div class="p-closed">
					<div class="no-data-wrapper no-border">
						<p class="no-data">
							<span class="icon-my-courses iconCourse"></span>
							<%=noOfSubjects %>
							<a href="#">Courses</a>
						</p>
					</div>
				</div>


				<%
         int count = 0;
         for(String subject : subjects){
        	 count++;
         %>

				<c:url value="viewCourseHomePage" var="courseUrl">
					<c:param name="subject" value="<%=subject %>" />
				</c:url>

				<div class="media">
					<div class="media-left">
						<span><%=count %></span>
					</div>
					<div class="media-body">
						<a href="${courseUrl}"><%=subject %></a>
					</div>
				</div>

				<%
	         if(count == 6){
	        	 break;//SHow only 6 subjects
	         }
         
         } %>


				<%
         if(noOfSubjects > 6){
         %>
				<a href="#" data-toggle="modal" data-target="#coursesModal"
					class="modalBtn media"><%=(noOfSubjects-6) %> More...</a>
				<%} %>


				<div class="clearfix"></div>
			</div>

		</div>
	</div>
</div>





<!--MODAL FOR COURSES-->
<div class="courses modal fade" id="coursesModal" tabindex="-1"
	role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">COURSES</h4>
			</div>
			<div class="modal-body">

				<%
         count = 0;
         for(String subject : subjects){
        	 count++;
         %>

				<c:url value="viewCourseHomePage" var="courseUrl">
					<c:param name="subject" value="<%=subject %>" />
				</c:url>

				<div class="media">
					<div class="media-left">
						<span><%=count %></span>
					</div>
					<div class="media-body">
						<a href="${courseUrl }"><%=subject %></a>
					</div>
				</div>

				<%} %>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>