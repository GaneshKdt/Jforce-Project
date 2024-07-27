<!DOCTYPE html>

<%@page import="ch.qos.logback.core.recovery.ResilientSyslogOutputStream"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.ConsumerProgramStructureStudentPortal"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AssignmentStudentPortalFileBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="java.util.Calendar"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>
<%try{ %>

<%
String cycledata = String.valueOf(request.getAttribute("cycledata"));


HashMap<String, String> programSemSubjectIdWithSubject = (HashMap<String,String>)request.getSession().getAttribute("programSemSubjectIdWithSubject");

ArrayList<String> subjects = (ArrayList<String>)session.getAttribute("studentCourses_studentportal");
int noOfSubjects = subjects != null ? subjects.size() : 0;
String selectedSubject = "";
AssignmentStudentPortalFileBean assignment1 = new AssignmentStudentPortalFileBean();
StudentStudentPortalBean sbean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
ArrayList<Integer> liveSessionPssIdsList = (ArrayList<Integer>) request.getSession().getAttribute("liveSessionPssIdAccess_studentportal");
String pssIdsCommaSeparated = "";
if (liveSessionPssIdsList != null && liveSessionPssIdsList.size() > 0) {
	pssIdsCommaSeparated = org.apache.commons.lang3.StringUtils.join(liveSessionPssIdsList, ",");
}


%>

<html lang="en">
    
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	
    
    <jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
	</jsp:include>
    
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">   
	<link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/resource-card.css" rel="stylesheet">  

    
    <body> 
    
    	<%@ include file="../common/headerDemo.jsp" %>

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
								<div class="container-fluid" >				
									<select class="form-select form-select-md mb-3" aria-label=".form-select-lg example" id="choice" onchange="handleDropdown(this)">
									  <option selected="selected">Select Subject Types</option>
									  <option value="all">ALL</option>
									  <option value="ongoing">ON GOING</option>
									  <option value="backlog">BACKLOG</option>
									</select>
								</div>
							<%
								if (programSemSubjectIdWithSubject.size() == 0) {
							%>
	
							<div class="panel-body " >
								<div class="card card-body text-center ">
									
										<span class="fa-solid fa-book-bookmark "></span> No Subjects to show
									
								</div>
							</div>
	
							<%
								} 
									
									
							%>
							

								<div class="row">
	
									<c:forEach var="pssId" items="<%=programSemSubjectIdWithSubject%>">
	
									<div class="col-md-6 col-sm-6 col-lg-6 col-xl-3  py-2" >
										
										<div class="card card-body h-100">
											<c:choose>
												<c:when test="${pssId.value == 'Project'}">
												
													
														<a data-pssId="${pssId.key }" class="encodedHref"> 
															<b> ${pssId.value } </b>
														</a>
													
													<hr>

											<div class="row">
											<div class="col text-center">
														<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="resource"> 
															<span class=" text-muted" >
																<i class="material-icons sessionplan-icon">library_books</i>
																<br>
																Resources
															</span>
														</a> 
													</div>
													<div class="col text-center">
													<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="result"> 
																<span class=" text-muted" >
																    <i class="material-icons sessionplan-icon">assignment</i>
																    <br> 
																	Results
																</span>
															</a>
													</div>
												</div>	
															
													
												</c:when>
												<c:otherwise>
												
												
											<div class="row ">
											<div class="col-10">
														<a data-pssId="${pssId.key }" class=" d-block text-truncate encodedHref" data-bs-target="tooltip" title=" ${pssId.value }"> 
															<b> ${pssId.value } </b>
														</a>
														</div>
														<div class="col ">
															<c:set var="pssIdsCommaSeparated" value="<%=pssIdsCommaSeparated %>"/>
													
															<c:if test = "${fn:contains(pssIdsCommaSeparated, pssId.key )}">
															 	<span title="Live Subject" class="float-end"><i class="fa fa-eye"></i></span>
															 </c:if>
															 <c:if test = "${!fn:contains(pssIdsCommaSeparated, pssId.key )}">
															 	<span title="Recorded Subject" class="float-end"><i class="fa fa-video-camera"></i></span>
															 </c:if>
														</div>
												</div>
													
													<hr>
													<div class="row ">												
														<div class="col-md-6 text-center " >
														<div class="row d-flex justify-content-center align-items-center">
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="session"> 
																<span class=" text-muted text-truncate" >
																	<i class="material-icons sessionplan-icon">play_circle_outline</i>
																	<br>															
																	Session
																</span>
															</a> 
															</div>
															
															<div class="row">
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="assign"> 
																<span class=" text-muted text-truncate" >
																	<i class="material-icons sessionplan-icon">assessment</i>
																	<br> 
																	Assessment
																</span>
															</a> 
															</div>
															
															<div class="row">
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="qna"> 
																<span class=" text-muted" >
																	<i class="material-icons sessionplan-icon">help</i>
																	<br> 
																	Q&amp;A
																</span>
															</a>
															</div>
															
															<div class="row">
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="flow"> 
																<span class=" text-muted" >
																	<i class="fa fa-arrows fa-xl"></i>
																	<br> 
																	Flow
																</span>
															</a>
															</div>
															
														</div>
														<div class="col-md-6 text-center " >
														
														<div class="row">
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="resource"> 
																<span class=" text-muted" >
																	<i class="material-icons sessionplan-icon">library_books</i>
																	<br>
																	Resources
																</span>
															</a> 
															</div>
															
															<div class="row">
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="result"> 
																<span class=" text-muted" >
																    <i class="material-icons sessionplan-icon">assignment</i>
																    <br> 
																	Results
																</span>
															</a> 
															</div>
															
															<div class="row">
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="forum"> 
																<span class=" text-muted" >
																	<i class="material-icons sessionplan-icon">question_answer</i>
																	<br> 
																	Forum
																</span>
															</a>
															</div>
															
														</div>
													</div>
													
												</c:otherwise>
												
											</c:choose>
											</div>
										
									</div>

								</c:forEach> 

								</div>
							
							
						</div>
					</div>
				</div>
			</div>
		</div>

		 <jsp:include page="../common/footerDemo.jsp"/> 
            
		 <%}catch(Exception e){}%>
	      	
	      	<script>
	      	
	      	var val ="${cycledata}";
	      
	      	if(val=="all")
	      		{
	      		document.getElementById("choice").selectedIndex = "1";
	      		}
	      	else if(val=="ongoing")
	      	    {
	      		document.getElementById("choice").selectedIndex = "2";
	      	    }
	      	else if(val=="backlog")
	      		{
	      		document.getElementById("choice").selectedIndex = "3";
	      		}
	      	
	      	
	      	
// 	      	console.log("test");
	      	
// 	      	
// 	      	console.log("aa "+val);
	      	
// 	      	var $option = $('#choice').children('option[value="'+ val +'"]');
// 	      	$option.attr('selected', true);​​
	      	
	      	function handleDropdown(evt) {
				var data1 = evt.options[evt.selectedIndex].value;
				window.location = "viewCourseHomePage?cycle=" + data1;
			}
		      	$('.encodedHref').each(function() {
		      		var pssId = $(this).attr("data-pssId"); 
		      		var param = $(this).attr("data-param");
		      		
		   	
		      		//$(this).attr("href","viewCourseDetails?subject="+encodeURIComponent(subject)+"&activeMenu="+param); 
		      		$(this).attr("href","/studentportal/student/viewCourseDetails?programSemSubjectId="+encodeURIComponent(pssId)+"&activeMenu="+param); 
		      	}); 

		      	$( document ).ready(function(){

		      		let height = $('.student-info-bar').height();
			      	console.log('height: '+height);
			      	if( height > 50 && height <= 80)
				      	$('.sz-content').css("margin-top","80px");
					if( height >= 80 )
				      	$('.sz-content').css("margin-top","100px");
			    });
	      	</script>
    </body>
</html>