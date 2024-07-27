

<!DOCTYPE html>


<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.ConsumerProgramStructureStudentPortal"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AssignmentStudentPortalFileBean"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%try{ %>


<%
	ArrayList<String> subjects = (ArrayList<String>)session.getAttribute("studentCourses_studentportal");
	//int noOfSubjects = subjects != null ? subjects.size() : 0;
	String selectedSubject = "";
	AssignmentStudentPortalFileBean assignment1 = new AssignmentStudentPortalFileBean();
	StudentStudentPortalBean sbean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
	HashMap<String,String> programSemSubjectIdWithSubject = (HashMap<String ,String>)request.getSession().getAttribute("programSemSubjectIdWithSubjects_studentportal");
	int noOfSubjects = programSemSubjectIdWithSubject != null ? programSemSubjectIdWithSubject.size() : 0;
	
%>

<html lang="en">
    
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
	</jsp:include>
    
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">   
	<link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/resource-card.css" rel="stylesheet">  
    <style>
	   .whitebg {
	    background-color: white;
	    }
    </style>
    <body> 
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;My Courses" name="breadcrumItems"/>
			</jsp:include>
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="My Courses" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper dashBoard myCoursesPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
              								
              								<%if(noOfSubjects == 0){ %>
              								
												<div class="panel-body whitebg" >
													<div class="no-data-wrapper">
														<p class="no-data">
															<span class="fa-solid fa-book-bookmark"></span> No Subjects to show
														</p>
													</div>
												</div>
												<%}else{ 
              									
              								%>
              								
              								<div class="course-learning-resources-m-wrapper">   
													
													<div id="collapseThree" class="panel-collapse collapse in academic-schedule courses-panel-collapse panel-content-wrapper" role="tabpanel">
														<div class="col-12">

															<div class="row mr-5 mb-3 sessionplan-block ">
														
												         <c:forEach var="pssId" items="${programSemSubjectIdWithSubjects}">
																<div class="mt-3 col-xl-4 col-lg-3 col-md-6 col-sm-6 col-12 "> 
																	<div class="sessionplan-resourcecard card">
																		<a data-pssId="${pssId.key }"  class="encodedHref">     
																			<div class="card-title sessionPlanHomeCardTitle ">
																				<b>${pssId.value } </b>  
																				<!-- <br>
																				<span style="font-size: 14px;">Prof. Rithu Pandey -->  
																				</span> 
																			</div> 
																		</a>
																		<div class="text-dark pt-0 pb-0 sessionplan-card-body card-body">
																			<div class="pt-3 row">
																				<div class="col-xl-7 col-lg-7 col-md-6 col-sm-6 " >  
																					<a class="btnn font12 hoverblue encodedHref" data-pssId="${pssId.key }"  data-param="session">
																						<i class="material-icons sessionplan-icon">play_circle_outline</i></br>      
																						<span> Session</span>
																					</a> 
																					<a class="btnn font12 hoverblue encodedHref" data-pssId="${pssId.key }" data-param="assign">
																						<i class="material-icons sessionplan-icon">assessment</i></br>
																						<span>Assessment</span>
																					</a>
																					<a class="btnn font12 hoverblue encodedHref" data-pssId="${pssId.key }" data-param="qna">
																						<i class="material-icons sessionplan-icon">help</i></br>
																						<span> Q&amp;A</span> 
																					</a>
																				</div> 
																				<div class="col-xl-5 col-lg-5 col-md-6 col-sm-6 ">  
																					<a class="btnn font12 hoverblue encodedHref" data-pssId="${pssId.key }" data-param="resource">
																						<i class="material-icons sessionplan-icon">library_books</i> </br>
																						<span>Resources</span> 
																					</a>
																					<a class="btnn font12 hoverblue encodedHref" data-pssId="${pssId.key }" data-param="result">
																						<i class="material-icons sessionplan-icon">assignment</i></br>   
																						<span>Results</span> 
																					</a>
																					
																					<a class="btnn font12 hoverblue encodedHref" data-pssId="${pssId.key }" data-param="forum">
																						<i class="material-icons sessionplan-icon">question_answer</i> </br> 
																						<span> Forum</span> 
																					</a>
																				</div>
																			 </div>  
																		</div>
																	</div> 
																</div> 
																</c:forEach>  
																
															</div>

														</div> 
													</div>  
											</div>    
              								
										<%} %>
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
      
            <jsp:include page="../common/footer.jsp"/>
            
		 <%}catch(Exception e){}%>
	      	<script>
	      	$('.encodedHref').each(function() {
	      		var pssId = $(this).attr("data-pssId"); 
	      		var param = $(this).attr("data-param");
	      		
	   	
	      		//$(this).attr("href","viewCourseDetails?subject="+encodeURIComponent(subject)+"&activeMenu="+param); 

	      		$(this).attr("href","student/viewCourseDetails?programSemSubjectId="+encodeURIComponent(pssId)+"&activeMenu="+param); 

	      	}); 
	      	
	      	</script>
    </body>
</html>