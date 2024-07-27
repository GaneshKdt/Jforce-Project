
<!DOCTYPE html>


<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AssignmentStudentPortalFileBean"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%try{ %>


<%
		ArrayList<String> subjects = (ArrayList<String>)session.getAttribute("studentCourses_studentportal");
		int noOfSubjects = subjects != null ? subjects.size() : 0;
		String selectedSubject = "";
		AssignmentStudentPortalFileBean assignment1 = new AssignmentStudentPortalFileBean();
		StudentStudentPortalBean sbean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
		String sessionclass="";
		String resourceclass="";
		String assignclass="";
		String resultclass=""; 
		String qnaclass="";
		String forumclass="";
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
			}else{
				sessionclass="active";
			}
			
		}catch(Exception e){
			sessionclass="active";
		}
		
%>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<html lang="en">
    
    <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
	</jsp:include>
    
    <link href="assets/css/courses.css" rel="stylesheet"> 

        <!-- Custom CSS for fabButton -->
          <link type="text/css" href="assets/css/propellerButton.css" rel="stylesheet">
          <link type="text/css" href="assets/css/fabButton.css" rel="stylesheet">

    
    <body>
    
		<script src="assets/js/jquery-1.11.3.min.js"></script>
    	
    	<jsp:include page="../common/header.jsp" />
    	
    	
        
        <div class="sz-main-content-wrapper">
        	<div class="sz-breadcrumb-wrapper">
    			<div class="container-fluid">
        			<ul class="sz-breadcrumbs">
		        		<li><a href="/studentportal/home">Student Zone</a></li>
		        		<li><a href="viewCourseHomePage">My Courses</a></li>
		        		<li><a href="/studentportal/viewCourseDetails?subject=${subject}" class="encodedHref">${subject}</a></li>
			        </ul>
           		</div>
         	</div>
        </div>
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="My Courses" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper dashBoard myCoursesPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content" style="padding-top: 35px!important; "> 
              								
              								<%if(noOfSubjects == 0){ %>
              									<div class="alert alert-danger alert-dismissible">
													<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
													<%=((String)request.getAttribute("errorMessage"))%>
												</div>
              								<%}else{ 
              								
              									selectedSubject = (String)request.getAttribute("subject");
              									HashMap<String, AssignmentStudentPortalFileBean> courseAssignmentsMap1 = (HashMap<String, AssignmentStudentPortalFileBean>)session.getAttribute("courseAssignmentsMap");
              									if(courseAssignmentsMap1!=null){
              										assignment1 = courseAssignmentsMap1.get(selectedSubject);
              									}
              									
              								%>
              								
											<div class="clearfix"></div>
											
					<div class="tab-grp1 tabbable-panel">
						<div class="tabbable-line">
						<%if(!"Project".equals(selectedSubject) && !"Module 4 - Project".equals(selectedSubject)){ %>
							<ul class="nav nav-tabs ">
								<li class="<%=sessionclass%>">
								<a data-toggle="tab" href="#home">
								<div class="text-center"><i class="material-icons sessionplan-icon">play_circle_outline</i><p>SESSIONS </p></div> 
								</a>
								</li>
								
								<%if(("MPDV".equalsIgnoreCase(sbean.getProgram()) && "Visual Analytics".equalsIgnoreCase(selectedSubject)) || ("EPBM".equalsIgnoreCase(sbean.getProgram()) && ("Enterprise Guide".equalsIgnoreCase(selectedSubject) || "Enterprise Miner".equalsIgnoreCase(selectedSubject)))){ %>
										<li class="<%=resourceclass%>">
										<a data-toggle="tab" href="#menu0">
										<div class="text-center"><i class="material-icons sessionplan-icon">library_books</i><p>TOOL ACCESS </p></div> 
										</a>
										</li>	
								<%} %>			
								<li class="<%=resourceclass%>">
									<a data-toggle="tab" href="#menu1">
									<div class="text-center"><i class="material-icons sessionplan-icon">library_books</i><p>RESOURCES</p></div> 
									</a>
								</li>
								<li class="<%=assignclass%>">
									<a data-toggle="tab" href="#menu2">
										<div class="text-center"><i class="material-icons sessionplan-icon">assessment</i><p>ASSIGNMENTS </p></div>
									</a>
								</li>
								<li class="<%=resultclass%>">
								<a data-toggle="tab" href="#menu3">
								<div class="text-center"><i class="material-icons sessionplan-icon">assignment</i><p>RESULTS </p></div>
								 
								</a></li>
								<li class="<%=qnaclass%>">
								<a data-toggle="tab" href="#menu4">
								<div class="text-center"><i class="material-icons sessionplan-icon">help</i><p>QUERIES </p></div> 
								</a>
								</li>
								<li class="<%=forumclass%>">
								<a data-toggle="tab" href="#menu5">
								<div class="text-center"><i class="material-icons sessionplan-icon">question_answer</i><p>FORUM </p></div> 
								 
								</a>
								</li>
								<%if("EPBM".equalsIgnoreCase(sbean.getProgram()) && "Enterprise Miner".equalsIgnoreCase(selectedSubject)){ %>
									<li class="<%=forumclass%>">
									<a data-toggle="tab" href="#menu6">
									<div class="text-center"><i class="material-icons sessionplan-icon">book</i><p>CASE STUDY </p></div>
									</a>
									</li>
								<%}%> 
							</ul>

							<div class="tab-content">
								<div id="home" class="tab-pane fade in <%=sessionclass%>">

									<%try {%>
									<%@ include file="academicCalendar.jsp"%>
									<%} catch (Exception e) {
									}%>
								</div>
								
								<%if(("MPDV".equalsIgnoreCase(sbean.getProgram()) && "Visual Analytics".equalsIgnoreCase(selectedSubject)) || ("EPBM".equalsIgnoreCase(sbean.getProgram()) && ("Enterprise Guide".equalsIgnoreCase(selectedSubject) || "Enterprise Miner".equalsIgnoreCase(selectedSubject)))){ %>
									<div id="menu0" class="tab-pane fade in ">
										<%@ include file="ToolAccess.jsp" %>
									</div>
								<%} %>
								<div id="menu1" class="tab-pane fade in <%=resourceclass%>">

									<%@ include file="learningResourceOnLoadResources.jsp" %>
								</div>
								<div id="menu2" class="tab-pane fade in <%=assignclass%>">

									<%if(!sbean.getPrgmStructApplicable().equalsIgnoreCase("Jan2018")){ %>
										<%@ include file="assignment.jsp" %>
									<%} %>
								</div>
								<div id="menu3" class="tab-pane fade in <%=resultclass%>">

									<% if(!sbean.getProgram().equalsIgnoreCase("MPDV") ){ %>
										<%@ include file="result.jsp" %>
									<%}%>
								</div>
								<div id="menu4" class="tab-pane fade in <%=qnaclass%>">

									<%@ include file="queries.jsp" %>
								</div>
								<div id="menu5" class="tab-pane fade in <%=forumclass%>">

									<%@ include file="forum.jsp" %>
								</div>
								<%if("EPBM".equalsIgnoreCase(sbean.getProgram()) && "Enterprise Miner".equalsIgnoreCase(selectedSubject)){ %>
									<div id="menu6" class="tab-pane fade in <%=forumclass%>">
									<%@ include file="caseStudy.jsp" %>
								</div>
								<%}%>
							</div>
						 <%}else{ 
							 if(!activeMenu.equalsIgnoreCase("resource")){ 
									resultclass="active";
								}
						 %>
						 <ul class="nav nav-tabs ">
						 		<li class="<%=resultclass%>"><a data-toggle="tab" href="#menu3">RESULTS FOR <%=mostRecentExamResultPeriod%></a></li>
								<li class="<%=resourceclass%>"><a data-toggle="tab" href="#menu1">LEARNING RESOURCES </a></li>
							</ul>

							<div class="tab-content">
								
								<div id="menu3" class="tab-pane fade in <%=resultclass%>">

									<%@ include file="result.jsp" %>
								</div>
								<div id="menu1" class="tab-pane fade in <%=resourceclass%>">
 									<jsp:include page="learningResourceOnLoadResources.jsp" />
								</div>
								
							</div>
						 <%} %>
						</div>
					</div>
					
<!-- 				accordion view for small size screen	 -->
				<%-- 		
					<div class="accord-grp accordion" >
						<div class="">
						<%if(!"Project".equals(selectedSubject)){ %>
							<div class="tab-grp">
							
								<div class="<%=sessionclass%> single-tab">
								<a data-toggle="collapse" href="#home-acrd" role="button" aria-expanded="<%=accordsession %>"  aria-controls="collapseExample">
								<div class="text-center"><i class="material-icons sessionplan-icon">play_circle_outline</i><p>SESSIONS </p></div> 
								</a>
								</div>
								
									<div id="home-acrd" class="collapse <%=accordsessioncollpase %> " >
	
										<%try {%>
										<%@ include file="academicCalendar.jsp"%>
										<%} catch (Exception e) {
										}%>
									</div>
								
								
								<%if(("MPDV".equalsIgnoreCase(sbean.getProgram()) && "Visual Analytics".equalsIgnoreCase(selectedSubject)) || ("EPBM".equalsIgnoreCase(sbean.getProgram()) && ("Enterprise Guide".equalsIgnoreCase(selectedSubject) || "Enterprise Miner".equalsIgnoreCase(selectedSubject)))){ %>
										<div class="<%=resourceclass%> single-tab">
										<a data-toggle="collapse" href="#menu0-acrd" role="button" aria-expanded="<%=accordtools %>" aria-controls="collapseExample">
										<div class="text-center"><i class="material-icons sessionplan-icon">library_books</i><p>TOOL ACCESS </p></div> 
										</a>
										</div>	
										
											<div id="menu0-acrd" class="collapse <%=accordtoolscollpase %>">
											<%@ include file="ToolAccess.jsp" %>
											</div>
										
								<%} %>			
								<div class="<%=resourceclass%> single-tab">
									<a data-toggle="collapse" href="#menu1-acrd" role="button" aria-expanded="<%=accordresource %>" aria-controls="collapseExample">
									<div class="text-center"><i class="material-icons sessionplan-icon">library_books</i><p>RESOURCES</p></div> 
									</a>
								</div>
										<div id="menu1-acrd" class="collapse <%=accordresourcecollpase %>">
		
											<%@ include file="learningResourceOnLoadResources.jsp" %>
										</div>
								
								
								
								<div class="<%=assignclass%> single-tab" >
									<a data-toggle="collapse" href="#menu2-acrd" role="button" aria-expanded="<%=accordassign %>" aria-controls="collapseExample">
										<div class="text-center"><i class="material-icons sessionplan-icon">assessment</i><p>ASSIGNMENTS </p></div>
									</a>
								</div>
										<div id="menu2-acrd" class="collapse <%=accordassigncollpase%>">
		
											<%if(!sbean.getPrgmStructApplicable().equalsIgnoreCase("Jan2018")){ %>
												<%@ include file="assignment.jsp" %>
											<%} %>
										</div>
								
								
								<div class="<%=resultclass%> single-tab">
								<a data-toggle="collapse" href="#menu3-acrd" role="button" aria-expanded="<%=accordresult %>" aria-controls="collapseExample">
								<div class="text-center"><i class="material-icons sessionplan-icon">assignment</i><p>RESULTS </p></div>
								 
								</a></div>
								
										<div id="menu3-acrd" class="collapse <%=accordresultcollpase %>">
		
											<% if(!sbean.getProgram().equalsIgnoreCase("MPDV") ){ %>
												<%@ include file="result.jsp" %>
											<%}%>
										</div>
								
								
								<div class="<%=qnaclass%> single-tab">
								<a data-toggle="collapse" href="#menu4-acrd" role="button" aria-expanded="<%=accordqna %>" aria-controls="collapseExample">
								<div class="text-center"><i class="material-icons sessionplan-icon">help</i><p>QUERIES </p></div> 
								</a>
								</div>
										<div id="menu4-acrd" class="collapse <%=accordqnacollpase%>">
		
											<%@ include file="queries.jsp" %>
										</div>
								
								
								
								<div class="<%=forumclass%> single-tab">
								<a data-toggle="collapse" href="#menu5-acrd" role="button" aria-expanded="<%=accordforum %>" aria-controls="collapseExample">
								<div class="text-center"><i class="material-icons sessionplan-icon">question_answer</i><p>FORUM </p></div> 
								 
								</a>
								</div>
								
										<div id="menu5-acrd" class="collapse <%=accordforumcollpase%>">
		
											<%@ include file="forum.jsp" %>
										</div>
								
								
								<%if("EPBM".equalsIgnoreCase(sbean.getProgram()) && "Enterprise Miner".equalsIgnoreCase(selectedSubject)){ %>
									<div class="<%=forumclass%> single-tab">
									<a data-toggle="collapse" href="#menu6-acrd" role="button" aria-expanded="false" aria-controls="collapseExample">
									<div class="text-center"><i class="material-icons sessionplan-icon">book</i><p>CASE STUDY </p></div>
									</a>
									</div>
									
									
										<div id="menu6-acrd" class="collapse <%=forumclass%>">
										<%@ include file="caseStudy.jsp" %>
										</div>
								<%}%> 
							</div>

							
								
						 <%}else{ 
						  if(!activeMenu.equalsIgnoreCase("resource")){ 
							    resultclass="active";
								accordresultcollpase=" in ";
								}
						 %>
						 <div class="tab-grp">
							<div class="<%=resultclass%> single-tab">
								<a data-toggle="collapse" href="#menu6-acrd" role="button"
									aria-expanded="false" aria-controls="collapseExample">
									<div class="text-center">
										<i class="material-icons sessionplan-icon">assignment</i>
										<p><%=mostRecentExamResultPeriod%></p> 
									</div>
								</a>
							</div>


							<div id="menu6-acrd" class="collapse <%=accordresultcollpase%>">
								<%@ include file="result.jsp"%>
							</div>
							<div class="<%=resourceclass%> single-tab">
								<a data-toggle="collapse" href="#menu7-acrd" role="button"
									aria-expanded="false" aria-controls="collapseExample">
									<div class="text-center">
										<i class="material-icons sessionplan-icon">book</i>
										<p>LEARNING RESOURCES</p>
									</div>
								</a>
							</div>


							<div id="menu7-acrd" class="collapse <%=accordresourcecollpase%>">
								<%@ include file="learningResourceOnLoadResources.jsp"%>
							</div>
						</div>



							<%} %>
						</div>
					</div> --%>
							
										
										<%} %>
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
      
            


			<script src="assets/js/jquery.plugin.min.js"></script>
			<script src="assets/js/jquery.countdown.js"></script>
			<script>
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
				
			     
			    	
				
				
				
				 $(function(){
			      // bind change event to select
			      $('#myCoursesList').on('change', function () {
			          var subject = $(this).val(); // get selected value
			          window.location = '/studentportal/viewCourseHomePage?subject='+encodeURIComponent(subject); // redirect
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
	      	$('.encodedHref').each(function() {
	      		var url = $(this).attr("href");    
	      		var urlbeforesubject= url.split('=')[0];
	      		var subject = url.split('=')[1];
	      		$(this).attr("href",urlbeforesubject+encodeURIComponent(subject));  
	      	}); 
			</script>
		 <%}catch(Exception e){
	      	}%>
           <jsp:include page="../common/footer.jsp"/>
    </body>
</html>