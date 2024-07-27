<!DOCTYPE html>


<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AssignmentStudentPortalFileBean"%>
<%@page import="java.util.Calendar"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%try{ %>


<%
	ArrayList<String> subjects = (ArrayList<String>)session.getAttribute("studentCourses_studentportal");
	int noOfSubjects = subjects != null ? subjects.size() : 0;
	String selectedSubject = "";
	AssignmentStudentPortalFileBean assignment1 = new AssignmentStudentPortalFileBean();

%>

<html lang="en">

<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>

<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;My Courses" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
    	 	<div id="sticky-sidebar"> 
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="My Courses" name="activeMenu" />
					</jsp:include>
                </div>

				<div class="sz-content-wrapper dashBoard myCoursesPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<%if(noOfSubjects == 0){ %>
						<div class="alert alert-danger alert-dismissible">
							<button type="button" class="close" data-dismiss="alert"
								aria-hidden="true">&times;</button>
							<%=((String)request.getAttribute("errorMessage"))%>
						</div>
						<%}else{ 
              								
              									selectedSubject = (String)request.getAttribute("subject");
              									HashMap<String, AssignmentStudentPortalFileBean> courseAssignmentsMap1 = (HashMap<String, AssignmentStudentPortalFileBean>)session.getAttribute("courseAssignmentsMap");
              									if(courseAssignmentsMap1!=null){
              										assignment1 = courseAssignmentsMap1.get(selectedSubject);
              									}
              									
              								
              								%>

						<h2>Select Subject</h2>
						<div class="clearfix"></div>
						<div class="courses-menu-wrapper">
							<div class="select-wrapper custom">
								<select class="form-control" id="myCoursesList">

									<%
										         for(String subject : subjects){
										         %>
									<option value="<%=subject%>"><%=subject%></option>
									<%}%>
								</select>
							</div>
						</div>
						<div class="clearfix"></div>

						<div class="panel-group has-no-result" id="accordion"
							role="tablist" aria-multiselectable="true">

							<%if(!"Project".equals(selectedSubject) && !"Module 4 - Project".equals(selectedSubject)){ %>
							<% try{ %>
							<%@ include file="academicCalendar.jsp"%>
							<%}
													         catch(Exception e){} 
													      %>
							<%@ include file="learningResourcesDemo.jsp"%>
							<%@ include file="assignment.jsp"%>

							<%@ include file="result.jsp"%>

							<%@ include file="queries.jsp"%>
							<%@ include file="forum.jsp"%>


							<%}else{ %>

							<%@ include file="result.jsp"%>
							<%@ include file="learningResourcesDemo.jsp"%>
							<%} %>




						</div>
						<div class="clearfix"></div>
						<%} %>
					</div>						
				</div>
			    		 <jsp:include page="../common/footerDemo.jsp" /> 
			</div>		
		</div>
	</div>

	


	<script>
			
				<%if(assignment1 != null){%>
					$('#assignmentTimer1').countdown({until: new Date('<%=assignment1.getEndDate()%>'), format: 'dHMS'});
					$('#assignmentTimer1').countdown('toggle');
					
					$('#assignmentTimer2').countdown({until: new Date('<%=assignment1.getEndDate()%>'), format: 'dHMS'});
					$('#assignmentTimer2').countdown('toggle');
				<%}%>
				$("#myCoursesList").val(decodeURIComponent("<%=selectedSubject.trim()%>"));
				
			     
			    	
				
				
				
				 $(function(){
			      // bind change event to select
			      $('#myCoursesList').on('change', function () {
			          var subject = $(this).val(); // get selected value
			          window.location = '/studentportal/viewCourseHomePageDemo?subject='+encodeURIComponent(subject); // redirect
			          return false;
			      });
			    });
			</script>

	<script>
			
			$(document).on('ready', function() {
			
				$('.load-more-table a').on('click', function() {
					$("#courseHomeLearningResources").addClass('showAllEntries');
					$(this).hide();
				});
			
			});
			</script>
	<%}catch(Exception e){}%>
</body>
</html>
