<%@page import="java.net.URLEncoder"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	String sessionclass="";
	String accordsession="";
	String accordsessioncollpase="";
	String resourceclass="";
	String accordresource="";
	String accordresourcecollpase="";
	String quizclass="";
	String accordquiz="";
	String accordquizcollpase="";
	String finalassessquizclass="";
	String accordfinalassquiz="";
	String accordfinalassquizcollpase="";
	
	String activeMenu = request.getParameter("activeMenu"); 
	boolean finalAssessQuizStatus=activeMenu.equalsIgnoreCase("finalAssessQuiz");
	try{
		if(activeMenu.equalsIgnoreCase("session")){
			sessionclass="active";
			accordsession="true";
			accordsessioncollpase=" in ";
		}else if(activeMenu.equalsIgnoreCase("resource")){
			resourceclass="active";
			accordresource="true";
			accordresourcecollpase=" in ";
		}else if(activeMenu.equalsIgnoreCase("quiz")){
			quizclass="active";
			accordquiz="true";
			accordquizcollpase=" in ";
		}
		else if(activeMenu.equalsIgnoreCase("finalAssessQuiz")){
			finalassessquizclass="active";
			accordfinalassquiz="true";
			accordfinalassquizcollpase=" in ";
		}
		else{
			sessionclass="active";
		}
	}catch(Exception e){
		sessionclass="active";
	}
%>

<!DOCTYPE html>
<html>
<head>
	<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
	<link href="assets/css/courses.css" rel="stylesheet"> 
	<jsp:include page="common/jscss.jsp">
		<jsp:param value="Module View" name="title"/>
	</jsp:include>

</head>
<body>
	<jsp:include page="common/header.jsp" />

	<div class="sz-main-content-wrapper">
       	<div class="sz-breadcrumb-wrapper">
   			<div class="container-fluid">
       			<ul class="sz-breadcrumbs">
	        		<li><a href="/studentportal/home">Student Zone</a></li> 
	        		<li><a href="student/getFreeCoursesList">My Courses</a></li>
	        		<li><a href="student/getProgramSubjectList?id=${cpsId}">Subjects</a></li>
	        		<li><a href="/studentportal/viewModuleDetails?pssId=${pssId}" class="encodedHref">${subject}</a></li>
		        </ul>
          	</div>
        </div>
	</div>
	
	<div class="sz-main-content menu-closed">
		<div class="sz-main-content-inner">
              				
			<jsp:include page="common/left-sidebar.jsp">
				<jsp:param value="Get Certified" name="activeMenu"/>
			</jsp:include>
              				
              	<div class="sz-content-wrapper dashBoard myCoursesPage">
              		<%@ include file="common/studentInfoBar.jsp" %>
              		
              		<div class="sz-content" style="padding-top: 35px!important; "> 
              			<div class="clearfix"></div>
              			<div class="tab-grp1 tabbable-panel" style="overflow: hidden">
							<div class="tabbable-line">
								<ul class="nav nav-tabs">
									<% if(!finalAssessQuizStatus){ %> 	
									<li class="<%=sessionclass%>">
										<a data-toggle="tab" href="#home">
											<div class="text-center">
												<i class="material-icons sessionplan-icon">play_circle_outline</i><p>Videos</p>
											</div>
										</a>
									</li>
									
									<li class="<%=resourceclass%>">
										<a data-toggle="tab" href="#menu1">
											<div class="text-center">
												<i class="material-icons sessionplan-icon">library_books</i><p>RESOURCES</p>
											</div> 
										</a>
									</li>
									<% }%>
									<li <%if(!finalAssessQuizStatus) {%> class="<%=quizclass%><%} else {%> class="<%=finalassessquizclass%> <%} %>">
										<a data-toggle="tab" href="#menu2">
											<div class="text-center">
												<i class="material-icons sessionplan-icon">assessment</i><p>Quiz</p>
											</div>
										</a>
									</li>
								</ul>
								<div class="tab-content">
								<% if(!activeMenu.equalsIgnoreCase("finalAssessQuiz")){ %> 
									<div id="home" class="tab-pane fade in <%=sessionclass%>">
										<%@include file = "moduleVideoContentView.jsp" %>
									</div>
									
									<div id="menu1" class="tab-pane fade in <%=resourceclass%>">
										<%@include file = "moduleResourceContentView.jsp" %>
									</div>
								<%}%>
									<div id="menu2" <%if(!finalAssessQuizStatus) {%> class="tab-pane fade in <%=quizclass%><%} else {%> class="tab-pane fade in <%=finalassessquizclass%><%} %>">
										<%@include file = "moduleQuizView.jsp" %>
									</div>
								</div>
							</div>
						</div>
              		</div>
              	</div>
     	</div>
     </div>
      <jsp:include page="common/footer.jsp"/>
</body>
</html>