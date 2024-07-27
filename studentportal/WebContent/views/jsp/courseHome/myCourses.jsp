<%-- <!DOCTYPE html>

<%@page import="ch.qos.logback.core.recovery.ResilientSyslogOutputStream"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.ConsumerProgramStructure"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AssignmentFileBean"%>
<%@page import="java.util.Calendar"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix = "fn" uri = "http://java.sun.com/jsp/jstl/functions" %>

<%try{ %>

<%

String cycledata = String.valueOf(request.getAttribute("cycledata"));
ArrayList<String> subjects = (ArrayList<String>) session.getAttribute("studentCourses_studentportal");
//int noOfSubjects = subjects != null ? subjects.size() : 0;
String selectedSubject = "";
HashMap<String, String> programSemSubjectIdWithSubject = null;
AssignmentFileBean assignment1 = new AssignmentFileBean();
StudentBean sbean = (StudentBean) request.getSession().getAttribute("student_studentportal");
int noOfSubjects = 0;
String flag = "";
if (cycledata == "null" || cycledata.equals("")) {
	System.out.println(" 26 "+cycledata);
	programSemSubjectIdWithSubject = (HashMap<String, String>) request.getSession()
	.getAttribute("programSemSubjectIdWithSubjects_studentportal");
	flag = "test";
	noOfSubjects = programSemSubjectIdWithSubject != null ? programSemSubjectIdWithSubject.size() : 0;
} else if (cycledata.equals("all")) {
	System.out.println(" 32 "+cycledata);
	programSemSubjectIdWithSubject = (HashMap<String, String>) request.getSession()
	.getAttribute("programSemSubjectIdWithSubjects_studentportal");

	noOfSubjects = programSemSubjectIdWithSubject != null ? programSemSubjectIdWithSubject.size() : 0;

	flag = "all";
} else if (cycledata.equals("ongoing")) {
	System.out.println(" 40 "+cycledata);
	programSemSubjectIdWithSubject = (HashMap<String, String>) request.getSession()
	.getAttribute("programSemSubjectIdWithSubjectForCurrentsem");

	noOfSubjects = programSemSubjectIdWithSubject != null ? programSemSubjectIdWithSubject.size() : 0;
	flag = "on going";
} else if (cycledata.equals("backlog")) {
	System.out.println(" 47 "+cycledata);
	programSemSubjectIdWithSubject = (HashMap<String, String>) request.getSession()
	.getAttribute("programSemSubjectIdWithSubjectForBacklog");

	
	noOfSubjects = programSemSubjectIdWithSubject != null ? programSemSubjectIdWithSubject.size() : 0;
	flag = "backlog";
} else {
	System.out.println(" 55 "+cycledata);
	programSemSubjectIdWithSubject = (HashMap<String, String>) request.getSession()
	.getAttribute("programSemSubjectIdWithSubjects_studentportal");

	noOfSubjects = programSemSubjectIdWithSubject != null ? programSemSubjectIdWithSubject.size() : 0;
	flag = "testttt";
}

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
	    
	    .course{
	    	background-color: white;
	    	box-shadow: 1px 2px 5px #CBCBCB;
	    	padding: 20px;
	    	overflow: auto;
	    	min-height: 320px;
	    }

	    .course::-webkit-scrollbar {
		  width: 10px;
		  border-radius: 2px;
		}
		
		/* Track */
		.course::-webkit-scrollbar-track {
		  background: #FAFAFA; 
		}
		 
		/* Handle */
		.course::-webkit-scrollbar-thumb {
		  background: #D8D8D8;
		  border-radius: 2px;
		}
		
		/* Handle on hover */
		.course::-webkit-scrollbar-thumb:hover {
		  background: #B8B8B8; 
		}
		
		.font12{
			font-size: 10px;
			font-weight: 500;
			color: #888888;
		}

    </style>
    
    <body> 
    
    	<%@ include file="../common/header.jsp" %>

		<div class="sz-main-content-wrapper">
	
			<jsp:include page="../common/breadcrum.jsp">
				<jsp:param value="Student Zone;My Courses" name="breadcrumItems" />
			</jsp:include>
	
			<div class="sz-main-content menu-closed">
	
				<div class="sz-main-content-inner">
	
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="My Courses" name="activeMenu" />
					</jsp:include>
	
					<div class="sz-content-wrapper dashBoard myCoursesPage">
	
						<%@ include file="../common/studentInfoBar.jsp"%>
	
						<div class="sz-content">
								<div class="container-fluid" >
									<select id="choice" class="form-control form-control-sm"
										onchange="handleDropdown(this)">
										<option value="" selected="selected">Select Subject
											Types</option>
										<option value="all">ALL</option>
										<option value="ongoing">ON GOING</option>
										<option value="backlog">BACKLOG</option>
									</select>
								</div>
							<%
								if (noOfSubjects == 0) {
							%>
	
							<div class="panel-body whitebg">
								<div class="no-data-wrapper">
									<p class="no-data">
										<span class="icon-my-courses"></span> No Subjects to show
									</p>
								</div>
							</div>
	
							<%
								} else {
							%>
	
							<div class="col-12">
								<div class="row">
	
									<c:forEach var="pssId" items="<%=programSemSubjectIdWithSubject%>">
	
									<div class="mt-3 col-xl-4 col-lg-3 col-md-6 col-sm-6 col-12">
										<div class="text-dark pt-0 pb-0 course" style="color: #6C6C6C;">
										
											<c:choose>
												<c:when test="${pssId.value == 'Project'}">
												
													<div style="min-height: 45px"> 
														<a data-pssId="${pssId.key }" class="encodedHref" style="font-size: 1em;"> 
															<b> ${pssId.value } </b>
														</a>
													</div>
													<hr>
													<div class="col-xs-6" style="display: grid; text-align: center;">
														
														<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="resource"> 
															<span class="hoverblue font12" >
																<i class="material-icons sessionplan-icon">library_books</i>
																<br>
																Resources
															</span>
														</a> 
														
													</div>
														
													<div class="pt-3 row">
													
														<div class="col-xs-6" style="display: grid; text-align: center;">
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="result"> 
																<span class="hoverblue font12" >
																    <i class="material-icons sessionplan-icon">assignment</i>
																    <br> 
																	Results
																</span>
															</a>  
														</div>
														
													</div>
													
												</c:when>
												<c:otherwise>
												
													<div style="min-height: 45px">
														<a data-pssId="${pssId.key }" class="encodedHref" style="font-size: 1em;"> 
															<b> ${pssId.value } </b>
														</a>
														<c:set var="pssIdsCommaSeparated" value="<%=pssIdsCommaSeparated %>"/>
														<div style="float: right; margin-top: -10px;">
															<c:if test = "${fn:contains(pssIdsCommaSeparated, pssId.key )}">
															 	<span title="Live Subject" style="font-size: 35px;"><i class="fa fa-eye"></i></span>
															 </c:if>
															 <c:if test = "${!fn:contains(pssIdsCommaSeparated, pssId.key )}">
															 	<span title="Recorded Subject" style="font-size: 35px;"><i class="fa fa-video-camera"></i></span>
															 </c:if>
														</div>
													</div>
													<hr>
													<div class="pt-3 row">
														<div class="col-xs-6" style="display: grid; text-align: center;">
														
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="session"> 
																<span class="hoverblue font12" >
																	<i class="material-icons sessionplan-icon">play_circle_outline</i>
																	<br>
																	Session
																</span>
															</a> 
															
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="assign"> 
																<span class="hoverblue font12" >
																	<i class="material-icons sessionplan-icon">assessment</i>
																	<br> 
																	Assessment
																</span>
															</a> 
															
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="qna"> 
																<span class="hoverblue font12" >
																	<i class="material-icons sessionplan-icon">help</i>
																	<br> 
																	Q&amp;A
																</span>
															</a>
															
														</div>
														<div class="col-xs-6" style="display: grid; text-align: center;">
														
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="resource"> 
																<span class="hoverblue font12" >
																	<i class="material-icons sessionplan-icon">library_books</i>
																	<br>
																	Resources
																</span>
															</a> 
															
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="result"> 
																<span class="hoverblue font12" >
																    <i class="material-icons sessionplan-icon">assignment</i>
																    <br> 
																	Results
																</span>
															</a> 
															
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="forum"> 
																<span class="hoverblue font12" >
																	<i class="material-icons sessionplan-icon">question_answer</i>
																	<br> 
																	Forum
																</span>
															</a>
															
														</div>
													</div>
													
												</c:otherwise>
												
											</c:choose>
											
										</div>
									</div>

								</c:forEach> 

								</div>
							</div>
							<% } %>
						</div>
					</div>
				</div>
			</div>
		</div>

		<jsp:include page="../common/footer.jsp"/>
            
		 <%}catch(Exception e){}%>
	      	
	      	<script>
	      	
	      	var val ="${cycledata}";
	      	alert("300 "+val);
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
		      		$(this).attr("href","viewCourseDetails?programSemSubjectId="+encodeURIComponent(pssId)+"&activeMenu="+param); 
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
</html> --%>

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
	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
	</jsp:include>
    
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">   
	<link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/resource-card.css" rel="stylesheet">  
	
    <style>
    
	   .whitebg {
	    	background-color: white;
	    }
	    
	    .course{
	    	background-color: white;
	    	box-shadow: 1px 2px 5px #CBCBCB;
	    	padding: 20px;
	    	overflow: auto;
	    	min-height: 320px;
	    }

	    .course::-webkit-scrollbar {
		  width: 10px;
		  border-radius: 2px;
		}
		
		/* Track */
		.course::-webkit-scrollbar-track {
		  background: #FAFAFA; 
		}
		 
		/* Handle */
		.course::-webkit-scrollbar-thumb {
		  background: #D8D8D8;
		  border-radius: 2px;
		}
		
		/* Handle on hover */
		.course::-webkit-scrollbar-thumb:hover {
		  background: #B8B8B8; 
		}
		
		.font12{
			font-size: 10px;
			font-weight: 500;
			color: #888888;
		}

    </style>
    
    <body> 
    
    	<%@ include file="../common/header.jsp" %>

		<div class="sz-main-content-wrapper">
	
			<jsp:include page="../common/breadcrum.jsp">
				<jsp:param value="Student Zone;My Courses" name="breadcrumItems" />
			</jsp:include>
	
			<div class="sz-main-content menu-closed">
	
				<div class="sz-main-content-inner">
	
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="My Courses" name="activeMenu" />
					</jsp:include>
	
					<div class="sz-content-wrapper dashBoard myCoursesPage">
	
						<%@ include file="../common/studentInfoBar.jsp"%>
	
						<div class="sz-content">
								<div class="container-fluid" >
									<select id="choice" class="form-control form-control-sm"
										onchange="handleDropdown(this)">
										<option value="" selected="selected">Select Subject
											Types</option>
										<option value="all">ALL</option>
										<option value="ongoing">ON GOING</option>
										<option value="backlog">BACKLOG</option>
									</select>
								</div>
							<%
								if (programSemSubjectIdWithSubject.size() == 0) {
							%>
	
							<div class="panel-body whitebg" style="margin-top:30px;">
								<div class="no-data-wrapper">
									<p class="no-data">
										<span class="fa-solid fa-book-bookmark"></span> No Subjects to show
									</p>
								</div>
							</div>
	
							<%
								} 
									
									
							%>
							
	
							<div class="col-12">
								<div class="row">
	
									<c:forEach var="pssId" items="<%=programSemSubjectIdWithSubject%>">
	
									<div class="mt-3 col-xl-4 col-lg-3 col-md-6 col-sm-6 col-12">
										<div class="text-dark pt-0 pb-0 course" style="color: #6C6C6C;">
										
											<c:choose>
												<c:when test="${pssId.value == 'Project'}">
												
													<div style="min-height: 45px"> 
														<a data-pssId="${pssId.key }" class="encodedHref" style="font-size: 1em;"> 
															<b> ${pssId.value } </b>
														</a>
													</div>
													<hr>
													<div class="col-xs-6" style="display: grid; text-align: center;">
														
														<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="resource"> 
															<span class="hoverblue font12" >
																<i class="material-icons sessionplan-icon">library_books</i>
																<br>
																Resources
															</span>
														</a> 
														
													</div>
														
													<div class="pt-3 row">
													
														<div class="col-xs-6" style="display: grid; text-align: center;">
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="result"> 
																<span class="hoverblue font12" >
																    <i class="material-icons sessionplan-icon">assignment</i>
																    <br> 
																	Results
																</span>
															</a>  
														</div>
														
													</div>
													
												</c:when>
												<c:otherwise>
												
												
												
													<div style="min-height: 45px">
														<a data-pssId="${pssId.key }" class="encodedHref" style="font-size: 1em;"> 
															<b> ${pssId.value } </b>
														</a>
														
															<c:set var="pssIdsCommaSeparated" value="<%=pssIdsCommaSeparated %>"/>
														<div style="float: right; margin-top: -10px;">
															<c:if test = "${fn:contains(pssIdsCommaSeparated, pssId.key )}">
															 	<span title="Live Subject" style="font-size: 35px;"><i class="fa fa-eye"></i></span>
															 </c:if>
															 <c:if test = "${!fn:contains(pssIdsCommaSeparated, pssId.key )}">
															 	<span title="Recorded Subject" style="font-size: 35px;"><i class="fa fa-video-camera"></i></span>
															 </c:if>
														</div>
														
													</div>
													<hr>
													<div class="pt-3 row">
														<div class="col-xs-6" style="display: grid; text-align: center;">
														
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="session"> 
																<span class="hoverblue font12" >
																	<i class="material-icons sessionplan-icon">play_circle_outline</i>
																	<br>															
																	Session
																</span>
															</a> 
															
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="assign"> 
																<span class="hoverblue font12" >
																	<i class="material-icons sessionplan-icon">assessment</i>
																	<br> 
																	Assessment
																</span>
															</a> 
															
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="qna"> 
																<span class="hoverblue font12" >
																	<i class="material-icons sessionplan-icon">help</i>
																	<br> 
																	Q&amp;A
																</span>
															</a>
															
														</div>
														<div class="col-xs-6" style="display: grid; text-align: center;">
														
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="resource"> 
																<span class="hoverblue font12" >
																	<i class="material-icons sessionplan-icon">library_books</i>
																	<br>
																	Resources
																</span>
															</a> 
															
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="result"> 
																<span class="hoverblue font12" >
																    <i class="material-icons sessionplan-icon">assignment</i>
																    <br> 
																	Results
																</span>
															</a> 
															
															<a class="btnn encodedHref" data-pssId="${pssId.key }" data-param="forum"> 
																<span class="hoverblue font12" >
																	<i class="material-icons sessionplan-icon">question_answer</i>
																	<br> 
																	Forum
																</span>
															</a>
															
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
		</div>

		<jsp:include page="../common/footer.jsp"/>
            
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