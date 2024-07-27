<!DOCTYPE html>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AssignmentStudentPortalFileBean"%>
<%@page import="com.nmims.beans.ConsumerProgramStructureStudentPortal"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%try{ %>


<%
		ArrayList<String> subjects = (ArrayList<String>)session.getAttribute("studentCourses_studentportal");
		//	int noOfSubjects = subjects != null ? subjects.size() : 0;
		String selectedSubject = "";
		
		HashMap<String,String> programSemSubjectIdWithSubject = (HashMap<String,String>)request.getSession().
				 getAttribute("programSemSubjectIdWithSubjects_studentportal");
		int noOfSubjects = programSemSubjectIdWithSubject != null ? programSemSubjectIdWithSubject.size() : 0;
		
		
		AssignmentStudentPortalFileBean assignment1 = new AssignmentStudentPortalFileBean();
		StudentStudentPortalBean sbean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		String sessionclass="";
		String resourceclass="";
		String assignclass="";
		String resultclass=""; 
		String qnaclass="";
		String forumclass="";
		String flowclass="";
		String toolsclass="";
		String accordsession="";
		String accordsessioncollpase="";
		String accordtools="";
		String accordtoolscollpase="";
		String accordresource="";
		String accordresourcecollpase="";
		String accordassign="";
		String accordassigncollpase="";
		String accordresult="";
		String accordresultcollpase="";
		String accordqna="";
		String accordqnacollpase="";
		String accordforum="";
		String accordforumcollpase="";
		String accordflow="";
		String accordflowcollpase="";
		String activeMenu = request.getParameter("activeMenu"); 
		String mostRecentExamResultPeriod = (String)session.getAttribute("mostRecentResultPeriod_studentportal");

		try{
			if(activeMenu.equalsIgnoreCase("session")){
				sessionclass="active";
				accordsession="true";
				accordsessioncollpase=" in ";
			}else if(activeMenu.equalsIgnoreCase("tools")){
				toolsclass="active";
				accordtools="true";
				accordtoolscollpase=" in ";
			}else if(activeMenu.equalsIgnoreCase("resource")){
				resourceclass="active";
				accordresource="true";
				accordresourcecollpase=" in ";
			}else if(activeMenu.equalsIgnoreCase("assign")){
				assignclass="active";
				accordassign="true";
				accordassigncollpase=" in ";
			}else if(activeMenu.equalsIgnoreCase("result")){
				resultclass="active";
				accordresult="true";
				accordresultcollpase=" in ";
			}else if(activeMenu.equalsIgnoreCase("qna")){
				qnaclass="active";
				accordqna="true";
				accordqnacollpase=" in ";
			}else if(activeMenu.equalsIgnoreCase("forum")){
				forumclass="active";
				accordforum="true";
				accordforumcollpase=" in ";  
			}else if(activeMenu.equalsIgnoreCase("flow") || activeMenu.equalsIgnoreCase("flowVideo") || activeMenu.equalsIgnoreCase("flowQuiz")){
				flowclass="active";
				accordflow="true";
				accordflowcollpase=" in ";  
			}else{
				sessionclass="active";
			}
			
		}catch(Exception e){
			sessionclass="active";
			activeMenu = "";
		}
		
%>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<html lang="en">  
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />  
    <jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
	</jsp:include>
    <body> 
		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
    	
    	<jsp:include page="../common/headerDemo.jsp" />
        
        <div class="sz-main-content-wrapper">
        	<div class="sz-breadcrumb-wrapper">
    			<div class="container-fluid">
        			<ul class="sz-breadcrumbs">
		        		<li><a href="/studentportal/home">Student Zone</a></li>

		        		<li><a href="/studentportal/student/viewCourseHomePage">My Courses</a></li>

		        		<!--   <li><a href="/studentportal/viewCourseDetails?subject=${subject}" class="encodedHref">${subject}</a></li>-->
		        		<li><a href="/studentportal/student/viewCourseDetails?programSemSubjectId=${programSemSubjectId}" class="encodedHref">${subject}</a></li>
			        </ul>
           		</div>
         	</div>
        </div>
	<div class="sz-main-content menu-closed">
		<div class="sz-main-content-inner" style="min-height: calc(140vh - 106px);">
			<div id="sticky-sidebar">
	            <jsp:include page="../common/left-sidebar.jsp">
	                <jsp:param value="My Courses" name="activeMenu" />
	            </jsp:include>
            </div>
			<div class="sz-content-wrapper dashBoard myCoursesPage">
				<%@ include file="../common/studentInfoBar.jsp"%>


				<div class="sz-content" style="padding-top: 50px !important;">

					<%if(noOfSubjects == 0 || (boolean)request.getAttribute("subjectNotAvail")){ %>
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



					<!------------------------------------------tabble panel starts  here----------------------------------------------->

					<!-- 		<div class="tab-grp1 tabbable-panel"> 
			
		       <div class="tabbable-line">  -->
		       
		<div class="container-fluid mt-lg-1  mt-5 pt-5 pt-md-0 pt-lg-0 pt-xl-0">
					<%if(!"Project".equals(selectedSubject) && !"Module 4 - Project".equals(selectedSubject)){ %>

					
						<ul class="nav nav-tabs nav-justified  mt-4 mt-lg-4 mt-md-4" id="myTab" role="tablist">

							<li class="nav-item" role="presentation"><a
								class="nav-link <%=sessionclass%> " type="button" id="sessions-tab"
								data-bs-toggle="tab" data-bs-target="#sessions-tab-pane"
								role="tab" aria-controls="sessions-tab-pane"
								aria-selected="true">
									<div class="text-center  mt-3">
										<i class="fa-regular fa-circle-play fa-xl "></i>
									</div>
									<h6 class="mt-3">SESSIONS</h6>
							</a></li>

							<%if(("MPDV".equalsIgnoreCase(sbean.getProgram()) && "Visual Analytics".equalsIgnoreCase(selectedSubject)) || ("EPBM".equalsIgnoreCase(sbean.getProgram()) && ("Enterprise Guide".equalsIgnoreCase(selectedSubject) || "Enterprise Miner".equalsIgnoreCase(selectedSubject)))){ %>
							<li class="nav-item " role="presentation">
							<a class="nav-link <%=resourceclass%>" type="button" data-bs-toggle="tab"
								data-bs-target="#tool-tab-pane" role="tab"
								aria-controls="tool-tab-pane" aria-selected="true">
									<div class="text-center mt-3">
										<i class="fa-solid fa-clone"></i>
									</div>
									<h6 class="mt-2">TOOL ACCESS</h6>
							</a></li>
							<%} %>


							<li class="nav-item" role="presentation"><a
								class="nav-link <%=resourceclass%>" type="button" data-bs-toggle="tab"
								id="resource-tab" data-bs-target="#resources-tab-pane"
								role="tab" aria-controls="resources-tab-pane"
								aria-selected="true">
									<div class="text-center mt-3">
										<i class="fa-solid fa-clone fa-xl"></i>
									</div>
									<h6 class="mt-3">RESOURCES</h6>

							</a></li>




							<li class="nav-item " role="presentation"><a
								class="nav-link <%=assignclass%>" type="button" data-bs-toggle="tab"
								data-bs-target="#assignment-tab-pane" role="tab"
								aria-controls="assignment-tab-pane" aria-selected="true">
									<div class="text-center mt-3">
										<i class="fa-solid fa-chart-simple fa-xl"></i>
									</div>
									<h6 class="mt-3">ASSIGNMENTS</h6>
							</a></li>


							<li class="nav-item " role="presentation"><a
								class="nav-link <%=resultclass%>" type="button" data-bs-toggle="tab"
								data-bs-target="#results-tab-pane" role="tab"
								aria-controls="results-tab-pane" aria-selected="true">
									<div class="text-center mt-3">
										<i class="fa-solid fa-clipboard-list fa-xl"></i>
									</div>
									<h6 class="mt-3">RESULTS</h6>
							</a></li>


							<li class="nav-item " role="presentation"><a
								class="nav-link <%=qnaclass%>" type="button" data-bs-toggle="tab"
								data-bs-target="#querise-tab-pane" role="tab"
								aria-controls="querise-tab-pane" aria-selected="true">
									<div class="text-center mt-3">
										<i class="fa-solid fa-circle-question fa-xl"></i>
									</div>
									<h6 class="mt-3">QUERIES</h6>
							</a></li>


							<li class="nav-item " role="presentation"><a
								class="nav-link <%=forumclass%>" type="button" data-bs-toggle="tab"
								data-bs-target="#fourm-tab-pane" role="tab"
								aria-controls="fourm-tab-pane" aria-selected="true">
									<div class="text-center mt-3">
										<i class="fa-solid fa-message fa-xl"></i>
									</div>
									<h6 class="mt-3">FORUM</h6>
							</a></li>




							<%if("EPBM".equalsIgnoreCase(sbean.getProgram()) && "Enterprise Miner".equalsIgnoreCase(selectedSubject)){ %>
							<li class="nav-item " role="presentation"><a
								class="nav-link <%=forumclass%>" type="button" data-bs-toggle="tab"
								data-bs-target="#casestudy-tab-pane" href="#menu6" role="tab"
								aria-controls="casestudy-tab-pane" aria-selected="true">
									<div class="text-center">
										<i class="material-icons sessionplan-icon">book</i>
										<p>CASE STUDY</p>
									</div>
							</a></li>
							<%}%>
							
							<li class="nav-item " role="presentation"><a
								class="nav-link <%=flowclass%>" type="button" data-bs-toggle="tab"
								data-bs-target="#flow-tab-pane" role="tab"
								aria-controls="flow-tab-pane" aria-selected="true">
									<div class="text-center mt-3">
										<i class="fa fa-arrows fa-xl"></i>
									</div>
									<h6 class="mt-3">FLOW</h6>
							</a></li>
							
						</ul>
					


					<!-- -----------------------------------------Tab Content starts here------------------------- -------------- -->


					
						<div class="tab-content" id="myTabContent">

							<div id="sessions-tab-pane"
								class=" tab-pane fade show <%=sessionclass%> "
								role="tabpanel" aria-labelledby="sessions-tab" tabindex="0">
								<%try {%>
								<%@ include file="academicCalendarDemo.jsp"%>
								<%} catch (Exception e) {}%>
							</div>


							<%if(("MPDV".equalsIgnoreCase(sbean.getProgram()) && "Visual Analytics".equalsIgnoreCase(selectedSubject)) || ("EPBM".equalsIgnoreCase(sbean.getProgram()) && ("Enterprise Guide".equalsIgnoreCase(selectedSubject) || "Enterprise Miner".equalsIgnoreCase(selectedSubject)))){ %>
							<div id="menu0" class="  tab-pane fade show ">
								<%@ include file="ToolAccess.jsp"%>
							</div>
							<%} %>


							<div id="resources-tab-pane"
								class=" tab-pane fade show <%=resourceclass%>"
								role="tabpanel" aria-labelledby="resource-tab" tabindex="0">
								<%@ include file="learningResourceOnLoadResourcesDemo.jsp"%>
							</div>


							<div id="assignment-tab-pane"
								class="tab-pane fade show <%=assignclass%>"
								role="tabpanel" aria-labelledby="assignment-tab" tabindex="0">

								<%if(!sbean.getPrgmStructApplicable().equalsIgnoreCase("Jan2018")){ %>
								<%@ include file="assignmentDemo.jsp"%>
								<%} %>
							</div>

							<div id="results-tab-pane"
								class=" tab-pane fade show <%=resultclass%>"
								role="tabpanel" aria-labelledby="results-tab" tabindex="0">

								<% if(!sbean.getProgram().equalsIgnoreCase("MPDV") ){ %>
								<%@ include file="resultDemo.jsp"%>
								<%}%>
							</div>

							<div id="querise-tab-pane"
								class=" tab-pane fade show <%=qnaclass%>"
								role="tabpanel" aria-labelledby="querise-tab" tabindex="0">
								<%@ include file="queriesTest.jsp"%>
							</div>

							<div id="fourm-tab-pane"
								class=" tab-pane fade show <%=forumclass%> ps-0 pe-0"
								role="tabpanel" aria-labelledby="fourm-tab" tabindex="0">
								<%@ include file="forumTest.jsp"%>
							</div>
							
							<div id="flow-tab-pane"
								class=" tab-pane fade show <%=flowclass%> ps-0 pe-0 d-none"
								role="tabpanel" aria-labelledby="flow-tab" tabindex="0">	
									<%@ include file="flow.jsp"%>
							</div>
							
							<div id="flowVideo-tab-pane"
								class=" tab-pane fade show <%=flowclass%> ps-0 pe-0 d-none"
								role="tabpanel" aria-labelledby="flow-tab" tabindex="0">	
									<%@ include file="flowVideos.jsp"%>
							</div>
							
							<div id="flowQuiz-tab-pane"
								class=" tab-pane fade show <%=flowclass%> ps-0 pe-0 d-none"
								role="tabpanel" aria-labelledby="flow-tab" tabindex="0">	
									<%@ include file="flowquiz.jsp"%>
							</div>
							

							<%if("EPBM".equalsIgnoreCase(sbean.getProgram()) && "Enterprise Miner".equalsIgnoreCase(selectedSubject)){ %>

							<div id="casestudy-tab-pane"
								class=" tab-pane fade show <%=forumclass%>"
								role="tabpanel" aria-labelledby="casestudy-tab" tabindex="0">

								<%@ include file="caseStudy.jsp"%>
							</div>
							<%}%>
					
							<%}else{ 
							 if(!activeMenu.equalsIgnoreCase("resource")){ 
									resultclass="active";
								}
						     %>

					<ul class="nav nav-tabs nav-justified  mt-4 mt-lg-4 mt-md-4" id="myTab" role="tablist">
						<li class="nav-item" role="presentation">
							<a class="nav-link <%=resultclass%>" id="RESULTSFOR-tab" data-bs-toggle="tab" href="#menu3" data-bs-toggle="tab" data-bs-target="#menu3"
							role="tab" aria-controls="RESULTSFOR-tab-pane" aria-selected="true">
							<div class="text-center text-dark mt-3">
								<i class="fa-solid fa-clipboard-list fa-xl"></i>
							</div>					
							<h6 class="mt-3 text-dark">RESULTS</h6>
							</a>
						</li>
						
						<li class="nav-item" role="presentation">
							<a class="nav-link <%=resourceclass%> " id="LEARNINGRESOURCES-tab" data-bs-toggle="tab"  data-bs-toggle="tab" data-bs-target="#menu1" 
							href="#menu1"  role="tab" aria-controls="LEARNING RESOURCES-tab-pane" aria-selected="true">
								<div class="text-center text-dark mt-3">
									<i class="fa-solid fa-clone fa-xl"></i>
								</div>
								<h6 class="mt-3 text-dark">RESOURCES</h6>
							</a>
					   </li>
					</ul>


					<div class="tab-content" id="myTabContent">
						<div id="menu3" class="tab-pane fade show <%=resultclass%>" aria-labelledby="RESULTSFOR-tab"
						 	role="tabpanel" tabindex="0" >
							<%@ include file="resultDemo.jsp"%>
						</div>
						<div id="menu1" class="tab-pane fade show <%=resourceclass%>" aria-labelledby="LEARNINGRESOURCES-tab"
							 role="tabpanel" tabindex="0" >
							<jsp:include page="learningResourceOnLoadResourcesDemo.jsp" />
						</div>
					</div>
							
							<%} %>
						</div>
						<%} %>		  
					</div>	
				 </div>				 
		 	  </div>			
		   </div>
		  <div class="footer">
		 <jsp:include page="../common/footerDemo.jsp"/> 
	</div>
	</div>

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/jquery.plugin.min.js"></script>
		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/jquery.countdown.js"></script>
		
			<script>
			var activeMenuClass = '<%=activeMenu%>';
				$(document).ready(function(){
					$('.table-result').DataTable({
				        "order": [[ 0, "desc" ],[ 1, "desc" ]]
				    });
				});
				<%if(assignment1 != null){%>
					$('#assignmentTimer1').countdown({until: new Date('<%=assignment1.getEndDate()%>'), format: 'dHMS'});
					$('#assignmentTimer1').countdown('toggle');
					
					$('#assignmentTimer2').countdown({until: new Date('<%=assignment1.getEndDate()%>'), format: 'dHMS'});
					$('#assignmentTimer2').countdown('toggle');
				<%}%>
				$("#myCoursesList").val(decodeURIComponent("<%=selectedSubject.trim()%>"));
				
			     
			    	
				

				$(function() {
					// bind change event to select
					$('#myCoursesList')
							.on(
									'change',
									function() {
										var subject = $(this).val(); // get selected value
										window.location = '/studentportal/student/viewCourseHomePage?subject='
												+ encodeURIComponent(subject); // redirect
										return false;
									});
				});
			</script>
			
			<script>
			var programSemSubjectId = '${programSemSubjectId}';
			var consumerProgramStructureId = '${consumerProgramStructureId}';
			var userId = '${userId}';
			var sessionPlanId = '${sessionPlanPgBean.id}'
			$(document).on('ready', function() {
			
				$('.load-more-table a').on('click', function() {
					$("#courseHomeLearningResources").addClass('showAllEntries');
					$(this).hide();
				});
			
			});
	      	$('.encodedHref').each(function() {
	      		var url = $(this).attr("href");    
	      		var urlbeforesubject= url.split('=')[0];
	      		var subject = url.split('=')[1];
	      		$(this).attr("href",urlbeforesubject+encodeURIComponent(subject));  
	      	});
	        </script>
			<script type="text/javascript" src="${pageContext.request.contextPath}/assets/js/flow/flow.js"></script>
		 <%}catch(Exception e){}%>
      
    </body>
</html>