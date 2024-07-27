
  <%@taglib prefix="spring" uri="http://www.springframework.org/tags"  %>
<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" var= "BASE_URL_ACADS_STATIC_RESOURCES" />

 <%@page import="com.nmims.beans.UserAuthorizationBean"%>
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.StudentBean"%>
 <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
  
 		<nav class="navbar navbar-inverse navbar-fixed-top customNavbar" role="navigation" style="min-height:40px;">
        <div class="container-fluid">
        
            <!-- <div class="social-col">
                <ul class="headerSocialLinks">
                    <li><a href="https://www.facebook.com/NMIMSSCE" target="_blank" class="facebook"><i class="fa fa-facebook"></i></a></li>
                    <li><a href="https://twitter.com/NMIMS_SCE" target="_blank" class="twitter"><i class="fa fa-twitter"></i></a></li>
                    <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" target="_blank" class="google-plus"><i class="fa fa-google-plus"></i></a></li>
                    <li><a href="#" target="_blank" class="youtube"><i class="fa fa-youtube"></i></a></li>
                </ul>
            </div> -->
                
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                </button>                
            </div>
            
            <div id="navbar" class="navbar-collapse collapse">
            	<div class="col-md-18 no-padding">
            	
            	
            	<%
		            String userId = (String)session.getAttribute("userId");
            		PersonAcads user = (PersonAcads)session.getAttribute("user");
            		StudentBean studentBean = (StudentBean)session.getAttribute("student");
            		String name = "";
            		String roles = "";
            		String program = "";
            		String studentPhotoUrl = "";
            		String userEmail = "";
            		String userMobile = "";
            		String pStructure = "";
            		
            		if(user != null){
            			roles = user.getRoles();
            			name = user.getFirstName() + " " + user.getLastName();
            			program = user.getProgram();
            			userEmail = user.getEmail();
            			userMobile = user.getContactNo();
            		}
            		if(studentBean != null && studentBean.getImageUrl() != null){
            			studentPhotoUrl = studentBean.getImageUrl().trim();
            			name = studentBean.getFirstName() + " " + studentBean.getLastName();
            			pStructure = studentBean.getPrgmStructApplicable();
            			userEmail = studentBean.getEmailId();
            			program = studentBean.getProgram();
            		}
            		
            		UserAuthorizationBean userAuthorization = (UserAuthorizationBean)session.getAttribute("userAuthorization");
            		if(userAuthorization != null){
            			roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
            		}
            		
		            if(userId != null) { 
		            	if(userId.startsWith("77") || userId.startsWith("79")){
		            %>
		            
		            
		            		<ul class="headerLinks" >
	                    
	                    
	                    	<li><a href="/studentportal/home">Home</a></li>
							
							<li>
	                        	<a href="#">Exam</a>
	                            <ul class="subMenu">
	                            	<li><a href="/exam/student/viewNotice">Results</a></li>
	                            	<li><a href="/exam/student/getMostRecentAssignmentResults">Assignment Marks</a></li>
									<li><a href="/exam/getAStudentMarks">Marks History</a></li>
									<li><a href="/exam/studentSelfMarksheetForm">Marksheet</a></li>
									<li><a href="/exam/studentTimeTable">Exam Timetable</a></li>
									<!-- <li><a href="#" onclick="window.alert('Hall Ticket will be available for download after Exam Registration')">Hall Ticket</a></li>  -->
									<li><a href="/exam/student/downloadHallTicket" >Hall Ticket</a></li>
									<li><a href="/exam/student/viewAssignmentsForm">Assignments</a></li>
									<li><a href="/exam/verifyInformation">Exam Registration</a></li>
									<!-- <li><a href="#" onclick="window.alert('Exam Registration is NOT active.')">Exam Registration</a></li> -->
									<%if("Jul2014".equalsIgnoreCase(pStructure)){ %>
									<li><a href="/exam/selectResitSubjectsForm">Re-sit Exam Registration</a>
									<%} %>
									<li><a href="/exam/student/printBookingStatus">Exam Registration Receipt</a></li>
									<%if(program.startsWith("PG")){ %>
									<li><a href="/exam/student/viewProject?subject=Project">Project Submission</a>
									<%} %>
	                            </ul>
							</li>
							
							
							<li>
	                        	<a href="#">Academics</a>
	                            <ul class="subMenu">
	                            	<li><a href="/acads/admin/viewTimeTable">Sessions Calendar</a></li>
									<li><a href="/acads/student/viewApplicableSubjectsForm">Learning Resources</a></li>
									<li><a href="/studentportal/gotoEZProxy" target="_blank">Digital Library</a></li>
									<li><a href="/acads/student/selectPCPSubjectsForm">PCP/VC Registration</a></li>
									<li><a href="/acads/student/downloadPCPRegistrationReceipt">PCP/VC Registration Receipt</a></li>
	                            </ul>
							</li>
							
							<li>
	                        	<a href="#">General</a>
	                            <ul class="subMenu">
	                            	<li><a href="/studentportal/student/getAllAnnouncementDetails">Announcements</a></li>
									<li><a href="http://ngasce.desk.com">Student Support System</a></li>
	                            </ul>
							</li>
							
			                <li><a href="/studentportal/changeUserPassword">Change Password</a></li>
			                <li><a href="/studentportal/student/updateProfile">Update Profile</a></li>
			                <li><a href="/studentportal/logout">Logout</a></li>
			                
	                    </ul>
		            
		            <%}else{ %>
		            
		            	<ul class="headerLinks">
	                    
	                    
	                    	<li><a href="/studentportal/home" title="Home"><i class="fa fa-home fa-lg" ></i></a></li>

							<jsp:include page="/views/studentPortalHeader.jsp">
								<jsp:param value="<%=roles %>" name="roles" />
							</jsp:include>
							
							<jsp:include page="/views/examHeader.jsp">
								<jsp:param value="<%=roles %>" name="roles" />
							</jsp:include>
							
							<jsp:include page="/views/acadsHeader.jsp">
								<jsp:param value="<%=roles %>" name="roles" />
							</jsp:include>
			                <li><a href="/studentportal/changeUserPassword"  title="Change Password"><i class="fa fa-key fa-lg" ></i></a></li>
			                <li><a href="/studentportal/student/updateProfile" title="Update Profile"><i class="fa fa-user fa-lg"></i></a></li>
			                <li><a href="/studentportal/logout" title="Logout"><i class="fa fa-power-off fa-lg"></i></a></li>  
			                
	                    </ul>
		            
		           	 <%} %>
		            
		            
	                    
	                 <%} %>
				</div>
                
                                
            </div>
        </div>
    </nav>
    
    <header class="customHeader">
    	<div class="logoWrapper">
        	<img src="${BASE_URL_ACADS_STATIC_RESOURCES}resources_2015/images/logo.jpg" width="100%" alt="Logo"/>
   	    </div>
        
        <div class="rightHeadWrapper">
        	<h1>Welcome to NGASCE Student Zone</h1>
            
            <%if(userId != null) { %>
            <div class="userContainer">
                <div class="userImg">
                    <%if(!"".equals(studentPhotoUrl)) {%>
                    	<img src="<%=studentPhotoUrl%>" alt="Student Photo" class="img-responsive" style="height:100%;"/>
                    <%}else{ %>
                    	<img src="resources_2015/images/userImg.jpg" alt="Student Photo"  class="img-responsive"/>
                    <%} %>
                </div>
                <div class="detailWrapper">
                    <h2><%=name.toUpperCase() %></h2>
                    <p>User ID: <%=userId %>, Program: <%=program %></p>
                    <p><a href="/studentportal/student/updateProfile" style="color: white"><%=userEmail%>/<%=userMobile %></a></p>
                </div>        
            </div>
            
            <%} %>
        </div>
        
    </header>
    
  
 
 	<div class="clearfix">
 	<%--  <c:import url="https://studentzone-ngasce.nmims.edu/maintenance.html" /> --%> 
 	</div>

		
