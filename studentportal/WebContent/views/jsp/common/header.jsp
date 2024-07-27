
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="org.jsoup.Jsoup"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.UserAuthorizationStudentPortalBean"%>
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="com.nmims.beans.AnnouncementStudentPortalBean"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%-- 		<nav class="navbar navbar-inverse navbar-fixed-top customNavbar" role="navigation" style="min-height:40px;">
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
            		Person user = (Person)session.getAttribute("user_studentportal");
            		StudentBean studentBean = (StudentBean)session.getAttribute("student_studentportal");
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
            		
            		UserAuthorizationBean userAuthorization = (UserAuthorizationBean)session.getAttribute("userAuthorization_studentportal");
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
									<li><a href="/exam/downloadHallTicket" >Hall Ticket</a></li>
									<li><a href="/exam/viewAssignmentsForm">Assignments</a></li>
									<li><a href="/exam/verifyInformation">Exam Registration</a></li>
									<!-- <li><a href="#" onclick="window.alert('Exam Registration is NOT active.')">Exam Registration</a></li> -->
									<%if("Jul2014".equalsIgnoreCase(pStructure)){ %>
									<li><a href="/exam/selectResitSubjectsForm">Re-sit Exam Registration</a>
									<%} %>
									<li><a href="/exam/printBookingStatus">Exam Registration Receipt</a></li>
									<%if(program.startsWith("PG")){ %>
									<li><a href="/exam/viewProject?subject=Project">Project Submission</a>
									<%} %>
	                            </ul>
							</li>
							
							
							<li>
	                        	<a href="#">Academics</a>
	                            <ul class="subMenu">
	                            	<li><a href="/acads/viewTimeTable">Sessions Calendar</a></li>
									<li><a href="/acads/viewApplicableSubjectsForm">Learning Resources</a></li>
									<li><a href="/studentportal/gotoEZProxy" target="_blank">Digital Library</a></li>
									<li><a href="/acads/selectPCPSubjectsForm">PCP/VC Registration</a></li>
									<li><a href="/acads/downloadPCPRegistrationReceipt">PCP/VC Registration Receipt</a></li>
	                            </ul>
							</li>
							
							<li>
	                        	<a href="#">General</a>
	                            <ul class="subMenu">
	                            	<li><a href="/studentportal/getAllAnnouncementDetails">Announcements</a></li>
									<li><a href="http://ngasce.desk.com">Student Support System</a></li>
	                            </ul>
							</li>
							
			                <li><a href="/studentportal/changePassword">Change Password</a></li>
			                <li><a href="/studentportal/student/updateProfile">Update Profile</a></li>
			                <li><a href="/logout">Logout</a></li>
			                
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
			                <li><a href="/studentportal/changePassword"  title="Change Password"><i class="fa fa-key fa-lg" ></i></a></li>
			                <li><a href="/studentportal/student/updateProfile" title="Update Profile"><i class="fa fa-user fa-lg"></i></a></li>
			                <li><a href="/logout" title="Logout"><i class="fa fa-power-off fa-lg"></i></a></li>  
			                
	                    </ul>
		            
		           	 <%} %>
		            
		            
	                    
	                 <%} %>
				</div>
                
                                
            </div>
        </div>
    </nav>
    
    <header class="customHeader">
    	<div class="logoWrapper">
        	<img src="resources_2015/images/logo.jpg" width="100%" alt="Logo"/>
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
 	 <c:import url="https://studentzone-ngasce.nmims.edu/maintenance.html" /> 
 	</div>
--%>


<%
StudentStudentPortalBean lead = (StudentStudentPortalBean)session.getAttribute("student_studentportal");
BaseController headerCon = new BaseController();
// ArrayList<AnnouncementStudentPortalBean> announcementsInHeader = (ArrayList<AnnouncementStudentPortalBean>)session.getAttribute("announcementsPortal");
// int noOfAnnouncemntsInHeader = announcementsInHeader != null ? announcementsInHeader.size() : 0;
SimpleDateFormat formatterHeader = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat dateFormatterHeader = new SimpleDateFormat("dd-MMM-yyyy");
String hideProfileLink = (String)request.getAttribute("hideProfileLink");
String mobileVisibleHeaderClass = "";//Use this class to show header on login page only
String link = "";
String profileHref= "/studentportal/student/updateProfile";
String communicationLink = "/studentportal/student/myEmailCommunicationsForm";
String linkedin = "https://blog.linkedin.com/2020/march/26/resources-to-help-you-navigate-the-challenges-of-todays-job-market?trk=lilblog_03-30-20_LiL-free-resources_learning"; 
boolean showProfile = true;
String perspective="";
if(session.getAttribute("userId") == null){
	//Login page
	mobileVisibleHeaderClass = "visibleHeader";
}

try{
	
if(!StringUtils.isBlank(request.getSession().getAttribute("isLoginAsLead").toString())){
	if(headerCon.checkLead(request, response)){ 
		link = "disabled";
		profileHref = "#";
		communicationLink = "/studentportal/student/emailCommunicationsForm";
		showProfile = false;
		perspective=lead.getPerspective();%>
		<c:set var = "perspective" value = '<%=perspective%>'/>
		<%
	}
}
}catch(Exception e){}

%>

<head>
	<link rel="icon" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/tablogo.png">
	<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/skip-navigation-header.css">
</head>

<style>
    @media ( max-width : 768px) {  
	.sz-header-menu{  
display: contents!important; 
}
.sz-header .sz-header-menu li{
padding:0px!important;
}
}

.disabled {
	pointer-events: none;
	cursor: default;
}
.student-info-bar{
left: <%=(perspective=="free" )?"0px!important":"100px"%>;
width: <%=(perspective=="free")?"100%!important":"93%"%>   
}      
.sz-content-wrapper{
padding-left: <%=(perspective=="free")?"0px!important":"100px"%> ; 
}
.sz-breadcrumb-wrapper{
left: <%=(perspective=="free")?"0px!important":"100px"%>;
width: <%=(perspective=="free")?"100%!important":""%>
}  
#globalSearch{
 	height:70px !important;
} 
#globalSearch[placeholder]{
 	font-size :22px !important;
}
#globalSearch::placeholder {
  	font-size: 17px;
}

#searchResult .list-group .list-group-item {
	font-weight: normal;
}

#searchResult .list-group .list-group-item:hover {
	background-color: aliceblue;
}

#searchResult .list-group .list-group-item::before:hover {
	background-color: black;
}

.buttonContrast{
	background-color:blue !important;
	border-color:blue !important;
	border-style: solid;
	color:white !important;
}
.linkContrast {
  color: blue !important;
}
.inputBorder{
	border : 1px solid blue !important;
}
</style>
<input type="hidden" id="subjectCodeIds" value="${sessionScope.subjectCodeId_studentportal}" />
<div class="sz-header <%=mobileVisibleHeaderClass%>">
	<div class="container-fluid">
		<a class="skip-navigation" href="#main-content-info-bar">Skip Navigation</a><!-- Adding Skip Navigation link for keyboard navigation accessibility, card: 17484 -->
		
		<div class="sz-logo">
			<img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/logo.png" class="img-responsive" alt="" />

		</div>

 
		<%if(session.getAttribute("userId") != null){ %>
		<ul class="sz-header-menu">
		
			<!--	Remove Linkedin link -->
			<%-- <%if(!"true".equals(hideProfileLink)){ %>
					<li><a href="<%= linkedin %>" target="_blank"><span class="fa fa-linkedin"></span> LinkedIn Job Search</a></li>
			<%} %> --%>
			
			<!-- Card 10453 disable Portal Experience for Leads and redirect directly to freeCourses page -->
			<%-- <% if(headerCon.checkLead(request, response)){ %>
			<li class="toggle-view header-toggle-li" ><a>   
			<c:if test="${perspective eq 'free'}">
			<c:set var="checked" value="checked"/>
			<c:set var="label" value="Free Course"/>
			<c:set var="title" value="Switch to Experienced"/>
			</c:if>
			<c:if test="${perspective ne 'free'}">
			<c:set var="checked" value=""/> 
			<c:set var="title" value="Switch to Free Course"/>
			</c:if>
			<span class="toggle-label"> Experience Student Portal &nbsp</span>
			<label class="switch header-switch" title="${title }">
			  <input type="checkbox" ${checked }>
			  <span class="slider round"></span>
			</label> 
			<span class="toggle-label">&nbsp Free Courses</span></a></li> 
 			<% } %> --%>
 			<c:choose>
	 			<c:when test="${isLoginAsLead eq true}"> 
	 				<li>
	 					<a href="getFreeCoursesList"><span class="fa fa-home" aria-hidden="true"></span>Home</a>
	 				</li>
					<li>
						<a href="https://ngasce.secure.force.com/nmcompleteFormRevised?id=<%= lead.getLeadId() %>" target="_blank">
							<span class="fa fa-registered"></span>Register Now</a>
					</li>
					<!-- <li>
						<a href="/studentportal/student/emailCommunicationsForm">
							<span class="glyphicon glyphicon-envelope"></span>My Communications</a>
					</li> -->
	 			</c:when>
	 			<c:otherwise>
	 			
					<%if(!"true".equals(hideProfileLink)){ 
						 if(showProfile){ %>
							<li><a href="#" onClick="focusSearchBar()"  data-toggle="modal" data-target="#globalsearchmodel" >
						<span class="fa-solid fa-magnifying-glass"></span>Search</a>
					</li>
					<%}} %>
					<!-- Modal -->
					<div class="modal fade" id="globalsearchmodel" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
					  <div class="modal-dialog">
					    <div class="modal-content" style="border-radius: 5px; width: 450px;">
					      <div class="modal-header" style="border-color: lightgray;">
					        <h1 class="modal-title fs-5 w-100" id="exampleModalLabel">
					        	<!-- search bar -->
						        <form class="d-flex" role="search" action="/studentportal/student/searchResultPage" >
							      <input class="form-control me-2" type="search" placeholder="Search here" id="globalSearch" aria-label="Search" name="search">
							    </form>
							</h1>
					      </div>
					      <div class="modal-body">
							<a href="#" data-toggle="modal" data-dismiss="modal" data-target="#SearchModal"></a><br>
							<div id="searchResult"></div>			
					      </div>
					      <div class="modal-footer" style="border-top: 1px solid lightgray;">
					        <a  class="btn btn-secondary" id="allSearch" style="margin-right: 10px; background: gray;" >Search</a>
					      </div>
					    </div>
					  </div>
					</div>	
					
				<%Boolean courseraAccess=(Boolean)request.getSession().getAttribute("courseraAccess");%>	
	 				<% if(courseraAccess){%>
					<% if(!"true".equals(hideProfileLink)) { %>
						<li><a href="/acads/student/showCourseraProducts">
							<span class="fa-regular fa-lightbulb"></span>Skillset</a>
						</li>
		     		 <%} %>
				  <%} %>	
					
					<%
						boolean consumerProgramStructureHasCSAccess = false;
						if(session.getAttribute("consumerProgramStructureHasCSAccess") != null){
							consumerProgramStructureHasCSAccess = (boolean) session.getAttribute("consumerProgramStructureHasCSAccess");
						}
						boolean csActiveInHeader = false;
						if(session.getAttribute("student_studentportal") != null){
							csActiveInHeader = ((StudentStudentPortalBean) session.getAttribute("student_studentportal")).isPurchasedOtherPackages();
						}
						if(!"true".equals(hideProfileLink) && !csActiveInHeader && consumerProgramStructureHasCSAccess){ %>
					<li><a href="/careerservices/showAllProducts"><span
							class="fa fa-briefcase"></span>Career Services</a></li>
					<% } %>
					
					<%if(!"true".equals(hideProfileLink)){ %>
					<li><a href="<%= communicationLink %>"><span
							class="glyphicon glyphicon-envelope"></span>My Communications</a></li>
					<%} %>
					<%--
					<li>
						<a href="<%= communicationLink %>"><span class="glyphicon glyphicon-envelope"></span>My Communications</a>
					</li>--%>
					<%if(!"true".equals(hideProfileLink)){ %>
						<li class="notification-link">
							<a href="" data-toggle="modal" data-keyboard="true" data-target="#announcements"> 
								<span class="icon-notification" id="announcementCounter">
									 <div class="notification-count"></div>
								</span>
							</a>
						</li>
						<%} %>

					<%if(!"true".equals(hideProfileLink)){ 
						 if(showProfile){ %>
							<li><a href="<%= profileHref %>" class=" <%= link %> "><span class="icon-my-profile"></span>My Profile</a></li>
					<%}} %>
				</c:otherwise>
			</c:choose>  
			<li>
				<a href="/logout"><span class="icon-logout"></span>Logout</a>
			</li>
		</ul>
<%-- 		<%if(noOfAnnouncemntsInHeader > 0){ %> --%>
		<!-- Announcements Modal -->
	<%-- 	<div id="new-announcements" class="modal fade" role="dialog"
			tabindex="-1">
			<div class="modal-dialog modal-md">
				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title">Announcements</h4>
					</div>
					<div class="modal-body">
						<ul class="announcement-list">

							<%
					                		  int count = 0;
									          for(AnnouncementStudentPortalBean announcement : announcementsInHeader){
									        	  count++;
									        	  String announcementWithoutHtmlCharacgters = Jsoup.parse(announcement.getDescription()).text();
									        	  String announcemntBrief = announcementWithoutHtmlCharacgters.length() > 150 ? announcementWithoutHtmlCharacgters.substring(0, 149)+"..."  : announcementWithoutHtmlCharacgters;
									        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
									  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
									          %>

							<li><a href="#" data-toggle="modal" data-dismiss="modal"
								data-target="#announcementModal<%=count%>"> <!-- <li> <a href="#" onclick="return false;" style="cursor:default;"> -->

									<h3><%=announcement.getSubject() %></h3>
									<p><%=announcemntBrief %></p>
									<h4><%=formattedDateString %>
										by <span><%=announcement.getCategory() %></span>
									</h4>
							</a></li>
							<%
										          if(count == 4){
										        	  break;
										          }
									          } %>
						</ul>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal"
							data-toggle="modal" data-target="#announcementModalAll">See
							All</button>

						<!-- <button type="button" class="btn btn-default" onclick="window.location.href='/studentportal/getAllAnnouncementDetails'">See All</button> -->
					</div>
				</div>
			</div>
		</div>

		<%} %>
		 --%>
		<%}else{ %>
		<div class="pull-right hidden-xs">
			<h2>Welcome to NGASCE Student Zone</h2>
		</div>
		<%} %>

		<div class="clearfix"></div>
	</div>
</div>
<!--header ends-->

<div class="modal" id="announcements" aria-labelledby="exampleModalLabel">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
         <h4 class="modal-title " id="exampleModalLabel">ALL ANNOUNCEMENTS</h4>
      </div>
      	 <div id="loader" style="display: none; text-align: center; margin-top: 2rem;">
		  <img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/gifs/loading-29.gif" alt="Loading..." style="height:40px" />
		  <p style="color: red; font-weight: bold; margin-top: 0.5rem;">Loading...</p>
		</div>
		<div class="modal-body">

			<ul>
				<div id="announcementbody"></div>
			</ul>
		</div>
		<div class="modal-footer">
          	<button type="button" class="btn btn-default" id="archive">View Archive</button></div>
		</div>
	</div>
  </div>
  
  <div id="announcementInsideModal"></div>
<%if(lead!=null){ %>
	<input type="hidden" id="checkTextToSpeech" value="<%= lead.getTextToSpeech() %>">
	<input type="hidden" id="checkHighContrast" value="<%= lead.getHighContrast() %>">
<%} %>
<%-- <%if(noOfAnnouncemntsInHeader > 0){ %>
<!--MODAL FOR ALL ANNOUNCEMENT-->

<div class="modal fade announcementModalTop" id="allAnnoucements"
	tabindex="-1" role="dialog">
	<div class="modal-dialog modal-md" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">ALL ANNOUNCEMENTS</h4>
			</div>
			<div class="modal-body">
				<ul>

					<%
        	 int count = 0;
	          for(AnnouncementStudentPortalBean announcement : announcementsInHeader){
	        	  count++;
	        	  String announcementWithoutHtmlCharacgters = Jsoup.parse(announcement.getDescription()).text();
	        	  String announcemntBrief = announcementWithoutHtmlCharacgters.length() > 150 ? announcementWithoutHtmlCharacgters.substring(0, 149)+"..."  : announcementWithoutHtmlCharacgters;
	        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
  				  String formattedDateString = dateFormatterHeader.format(formattedDate);
	          %>

					<li><a href="#" data-toggle="modal" data-dismiss="modal"
						data-target="#announcementModal<%=count%>"> <!-- <li> <a href="#" onclick="return false;" style="cursor:default;"> -->
							<h3><%=announcement.getSubject() %></h3>
							<p><%=announcemntBrief %></p>

							<h4><%=formattedDateString%>
								by <span><%=announcement.getCategory()%></span>
							</h4>
					</a></li>

					<%
	          
	          } %>


				</ul>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" id="archive">View Archive</button>
			</div>
		</div>
	</div>
</div>

<%}%>


<%if(noOfAnnouncemntsInHeader > 0){ %>
<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
<%
        	 int count = 0;
	          for(AnnouncementStudentPortalBean announcement : announcementsInHeader){
	        	  count++;
	        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
	  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
	          %>

<div class="modal fade announcement" id="announcementModal<%=count %>"
	tabindex="-1" role="dialog">
	<div class="modal-dialog" role="document">
		<div class="modal-content modal-md">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">ANNOUNCEMENTS</h4>
			</div>
			<div class="modal-body">
				<h6><%=announcement.getSubject() %></h6>
				<p><%=announcement.getDescription() %></p>

				<%if(announcement.getAttachment1() != null){ %>
				<a target="_blank"
					href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" /><%=announcement.getAttachment1()%>"><%=announcement.getAttachmentFile1Name() %></a><br />
				<%} %>
				<%if(announcement.getAttachment2() != null){ %>
				<a target="_blank"
					href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" /><%=announcement.getAttachment2()%>"><%=announcement.getAttachmentFile2Name() %></a><br />
				<%} %>
				<%if(announcement.getAttachment3() != null){ %>
				<a target="_blank"
					href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" /><%=announcement.getAttachment3()%>"><%=announcement.getAttachmentFile3Name() %></a><br />
				<%} %>


				<h4 class="small"><%=formattedDateString%>
					<span>by</span><a href="#"> <%=announcement.getCategory() %></a>
				</h4>

			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">DONE</button>
			</div>
		</div>
	</div>
</div>
<% }//End of For loop %>

<%}%> --%>



<script>

	window.addEventListener('load', function() {
		loadCount();
		$(".notification-link").click(function(e){
			e.preventDefault();
			loadAnnouncements();
		});
	});

	function loadCount(){
		var data = {
			sapid: "${sessionScope.userId}"
			};
	  
		$.ajax({
	    	url: '/announcement/m/getUnreadAnnouncementCount',
	      	type: 'POST',
	      	contentType: 'application/json',
	      	data: JSON.stringify(data),
	      	success: function(response) {
	    		var count = response;
	    	  	var announcementCount = '<div class="notification-count">'+count+'</div>';
	           	$('#announcementCounter').html(announcementCount);
			},
		  	error : function(xhr, status, error) {
				var announcementCount = '<div class="notification-count">0</div>';
				$('#announcementCounter').html(announcementCount);	  
			}
	  });
	}	

	function isReadStatusInHeader(id, userId) {
		var modal = document.getElementById("announcementModal");
		var data = {
			announcementId : id,
			userId : userId
		};
		
		var data1 = {
			sapid : userId
		};
		
		$.ajax({
			url : '/announcement/m/changeAnnouncementStatus',
			method : 'POST',
			contentType : 'application/json',
			data : JSON.stringify(data),
			success : function(response1) {

				//On update Reload the Announcements and Announcements Counter
				$.ajax({
					url : '/announcement/m/getUnreadAnnouncementCount',
					method : 'POST',
					data : JSON.stringify(data1),
					contentType : 'application/json',
					success : function(response2) {
						var count = response2;
						var updatedCounter = '<div class="notification-count">'+count+'</div>';
						$('#announcementCounter').html(updatedCounter);	
					},
					error : function(xhr, status, error) {
				 		console.error("Error while getting Announcements data :" ,error);
					}
				});
			},
			error : function(xhr, status, error) {
				console.error("Error while updating the status : " ,error);
			}
		});
	}
</script> 

<script>
	
	function loadAnnouncements() {
		var data = {
					sapid: "${sessionScope.userId}"
			  		};
  	$.ajax({
    	url: '/announcement/m/getAllActiveAnnouncementsByStudentMapping',
      	type: 'POST',
      	contentType: 'application/json',
      	data: JSON.stringify(data),
      	beforeSend: function() {
    		$('#loader').css('display', 'block');
   	  	},
   	  	
      	success: function(response) {
           	var activeStatus = response.isActiveAnnouncement;
 if(activeStatus){
    		$('#loader').css('display', 'none');
          	var announcements = response.announcements;
          	var announcementCount = typeof response.announcementCounter === "undefined" ? 0 : response.announcementCounter;
          	var optionsList = '<div class="notification-count">'+announcementCount+'</div>';
          	$('#announcementCounter').html(optionsList);
          	var count=0;
          	
          	try {
        		var announcement='';
  		  	  	var individualAnnouncement ='';
              	for (let i = 0; i < announcements.length; i++) {
	              	singleannouncement = response.announcements[i];
		           	var createdDate = new Date(singleannouncement.createdDate);
					var options = { day: '2-digit', month: 'short', year: 'numeric' };
					var formattedDate = createdDate.toLocaleDateString('en-US', options);
	                count++;
	                
	               	var breifAnnouncement = announcements[i].description;
						function stripHtmlTags(breifAnnouncement) {
		                	return breifAnnouncement.replace(/<[^>]*>/g, '');
		                }
              	
						var announcementWithTags = announcements[i].description;
		              	var announcementWithoutTags  =stripHtmlTags(announcementWithTags);
		              	var announcemntBrief = "";        	
		              	if (announcementWithoutTags.length > 150) {
		              	  announcemntBrief = announcementWithoutTags.substring(0, 149) + "...";
		              	} else {
		              	  announcemntBrief = announcementWithoutTags;
		              	}

					if(announcements[i].readStatus){
						
				announcement 		+='<a class="bg-light" href="#" data-toggle="modal" data-dismiss="modal"'
									+'onClick="isReadStatusInHeader('+announcements[i].id+', ${sessionScope.userId})"'
									+'id="isRead" data-target="#announcementModalInheader'+announcements[i].id+'">';           								
				announcement		+='<div id="announcementId-'+announcements[i].id+'">'
									+'<p style="font-weight: bold; color:black;">'+announcements[i].subject+'</p>'
									+'</div>';
				announcement		+='<p style="font-weight: bold; color:black;">'+announcemntBrief+'</p>';
				announcement		+='<p style="font-weight: bold; color:black;">'+announcements[i].startDate+''
									+'by <span style="font-weight: bold; color:black;">'+announcements[i].category+'</span>'
									+'</p>'
									+'</a>';
				announcement   		+='<hr>';
									}
				
					if(!announcements[i].readStatus){
						
				announcement		+='<span style="background-color: #f6f6f6;">'   
							 		+'<a class="bg-light" href="#" data-toggle="modal" data-dismiss="modal"'
									+'onClick="isReadStatusInHeader('+announcements[i].id+',${sessionScope.userId})"'
									+'id="isRead" data-target="#announcementModalInheader'+announcements[i].id+'">';   
				announcement		+='<div id="announcementdetails">'
									+'<p class="text-secondary">'+announcements[i].subject+'</p>'
									+'</div>';
				announcement	    +='<p class="text-secondary">'+announcemntBrief+'</p>';
				announcement		+='<p class="text-secondary">'+announcements[i].startDate+''
									+'by <span class="text-secondary">'+announcements[i].category+'</span>'
									+'</p>'
									+'</a>'
									+'</span>';       				              		
				announcement		+='<hr>';
								    }

  			individualAnnouncement	+='<div class="modal" id="announcementModalInheader'+singleannouncement.id+'"  aria-labelledby="seprateModel">' 	
					   				+'<div class="modal-dialog">'
									+'<div class="modal-content">'
									+'<div class="modal-header">'
									+'<button type="button" class="close" data-dismiss="modal"aria-label="Close"><span aria-hidden="true">&times;</span></button>'
									+'<h4 class="modal-title fs-5" id="seprateModel">ANNOUNCEMENTS</h4>'
									+'</div>';
					
			individualAnnouncement	+='<div class="modal-body">'
									+'<h6>'+singleannouncement.subject+'</h6>'
									+'<p>'+singleannouncement.description+'</p>';	
				
									if(singleannouncement.attachment1 != null){
		    individualAnnouncement	+='<a target="_blank" href="https://announcementfiles.s3.ap-south-1.amazonaws.com/"/>'+singleannouncement.attachment1+singleannouncement.attachmentFile1Name+'</a><br/>';
									}
									if(singleannouncement.attachment2 != null){
			individualAnnouncement	+='<a target="_blank" href="https://announcementfiles.s3.ap-south-1.amazonaws.com/"/>'+singleannouncement.attachment2+singleannouncement.attachmentFile2Name+'</a><br/>';
									}
									if(singleannouncement.attachment3 != null){
		    individualAnnouncement	+='<a target="_blank" href="https://announcementfiles.s3.ap-south-1.amazonaws.com/"/>'+singleannouncement.attachment3+singleannouncement.attachmentFile3Name+'</a><br/>';
									}
																									
		    individualAnnouncement	+='<h4 class="small">' + formattedDate + '<span> by </span><a href="#">'+''+ singleannouncement.category + '</a></h4>';
	
			individualAnnouncement	+='</div>';
				
			individualAnnouncement	+='<div class="modal-footer">'
									+'<button data-dismiss="modal" type="button" class="btn btn-default">DONE</button>'
									+'</div>'
									+'</div>'
									+'</div>'
									+'</div>';	

				}
			}catch (err) {
				console.error(err);
			    }
			
				$('#announcementbody').html(announcement);
				$('#announcementInsideModal').html(individualAnnouncement);
			    }
 	else{	  	
				$('#loader').css('display', 'none');
				var msg = response.announcementMsg;
				var announcementError ='<div id="announcementbody"><p style="font-weight: bold; color:black; text-align:center;">'+msg+'</p></div>';
			 	$('#announcementbody').html(announcementError);
					}
			},
			error : function(xhr, status, error) {
				$('#loader').css('display', 'none');
				var announcementError ='<div id="announcementbody"><p style="font-weight: bold; color:black; text-align:center;">Unable to load announcements!</p></div>';
				$('#announcementbody').html(announcementError);
				console.error('Unable to load announcements!', error);
			}
		});
	}
</script>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/headerOldBootstrap.js"></script>

<!-- Card 10453 disable Portal Experience for Leads and redirect directly to freeCourses page
<script>
$(".header-switch").change(function() {    
	window.location = "/studentportal/student/setPerspectiveForLeads?perspective=toggle";
}); 
</script> -->