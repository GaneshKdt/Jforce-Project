
 <%@page import="com.nmims.controllers.BaseController"%>
<%@page import="org.jsoup.Jsoup"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.UserAuthorizationBean"%>
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.StudentAcadsBean"%>
<%@page import="com.nmims.beans.AnnouncementAcadsBean"%>
<%@page import="java.util.ArrayList"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%
BaseController headerCon = new BaseController();
StudentAcadsBean lead = (StudentAcadsBean)session.getAttribute("student_acads");
ArrayList<AnnouncementAcadsBean> announcementsInHeader = (ArrayList<AnnouncementAcadsBean>)session.getAttribute("announcementsAcads");
int noOfAnnouncemntsInHeader = announcementsInHeader != null ? announcementsInHeader.size() : 0;
SimpleDateFormat formatterHeader = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat dateFormatterHeader = new SimpleDateFormat("dd-MMM-yyyy");
String hideProfileLink = (String)request.getAttribute("hideProfileLink");
String mobileVisibleHeaderClass = "";//Use this class to show header on login page only
String linkedin = "https://blog.linkedin.com/2020/march/26/resources-to-help-you-navigate-the-challenges-of-todays-job-market?trk=lilblog_03-30-20_LiL-free-resources_learning";
boolean showProfile = true;

if(session.getAttribute("userId_acads") == null){
	//Login page
	mobileVisibleHeaderClass = "visibleHeader";
}
	
	String link = "", profileHref= "/studentportal/student/updateProfile";
	if(headerCon.checkLead(request, response)){ 
		link = "disabled";
		profileHref = "#";
		showProfile = false;
	}
%>

<head>
	<link rel="icon" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/images/tablogo.png">
</head>
<div class="sz-header <%=mobileVisibleHeaderClass%>">
	<div class="container-fluid">
		<div class="sz-logo">
			<img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/images/logo.png" class="img-responsive" alt="" />

		</div>
		<ul class="sz-header-menu text-nowrap">
	
 			<c:choose>
	 			<c:when test="${isLoginAsLead eq true}"> 
	 				<li>
	 					<a href="/studentportal/student/getFreeCoursesList"><span class="fa-solid fa-house" aria-hidden="true"></span>Home</a>
	 				</li>
					<li>
						<a href="https://ngasce.secure.force.com/nmcompleteFormRevised?id=<%= lead.getLeadId() %>" target="_blank">
							<span class="fa-solid fa-registered"></span>Register Now</a>
					</li>
	 			</c:when>
	 			<c:otherwise>
					<% if(!"true".equals(hideProfileLink)) { %>
						<li><a href="/acads/student/showCourseraProducts">
							<span class="fa-regular fa-lightbulb"></span>Skillsets</a>
						</li>
					<% } %>
					
					<%
						boolean consumerProgramStructureHasCSAccess = false;
						if(session.getAttribute("consumerProgramStructureHasCSAccess") != null){
							consumerProgramStructureHasCSAccess = (boolean) session.getAttribute("consumerProgramStructureHasCSAccess");
						}
						boolean csActiveInHeader = false;
						if(session.getAttribute("student_acads") != null){
							csActiveInHeader = ((StudentAcadsBean) session.getAttribute("student_acads")).isPurchasedOtherPackages();
						}
						if(!"true".equals(hideProfileLink) && !csActiveInHeader && consumerProgramStructureHasCSAccess){ %>
					<li><a href="/careerservices/showAllProducts"><span
							class="fa-solid fa-briefcase"></span>Career Services</a></li>
					<% } %>
					
		                 <li>
							<a href="/studentportal/student/myEmailCommunicationsForm">
								<span class="fa-solid fa-envelope"></span>My Communications</a>
						 </li>
					<%--
						<a href="<%= communicationLink %>"><span class="glyphicon glyphicon-envelope"></span>My Communications</a>
					</li>--%>
					<li class="notification-link">
						<a href="" data-bs-toggle="modal" data-bs-keyboard="true" data-bs-target="#announcements"> 
							<span class="icon-notification"><div class="notification-count"><%=noOfAnnouncemntsInHeader %></div></span></a>
					</li>
					
					<%if(!"true".equals(hideProfileLink)){ 
						 if(showProfile){ %>
							<li><a href="<%= profileHref %>" class=" <%= link %> "><span class="fa-regular fa-user"></span>My Profile</a></li>
					<%}} %>
				</c:otherwise>
			</c:choose>  
			<li>
				<a href="/logout"><span class="icon-logout"></span>Logout</a>
			</li>
		</ul>
		<div class="clearfix"></div>
	</div>
</div>
<!--header ends-->


<%if(noOfAnnouncemntsInHeader > 0){ %>
<!--MODAL FOR ALL ANNOUNCEMENT-->


<div class="modal fade" id="announcements" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title " id="exampleModalLabel">ALL ANNOUNCEMENTS</h4>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
			<div class="modal-body">
				<ul>

				<%
        	 int count = 0;
	          for(AnnouncementAcadsBean announcement : announcementsInHeader){
	        	  count++;
	        	  String announcementWithoutHtmlCharacgters = Jsoup.parse(announcement.getDescription()).text();
	        	  String announcemntBrief = announcementWithoutHtmlCharacgters.length() > 150 ? announcementWithoutHtmlCharacgters.substring(0, 149)+"..."  : announcementWithoutHtmlCharacgters;
	        	  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
	  			  String formattedDateString = dateFormatterHeader.format(formattedDate);
	          %>

					<a href="#" data-bs-toggle="modal" data-bs-dismiss="modal"
						data-bs-target="#announcementModal<%=count%>"> <!-- <li> <a href="#" onclick="return false;" style="cursor:default;"> -->
							<p class="card-text text-dark fw-bold"><%=announcement.getSubject() %></p>
							<p class="card-text text-dark "><%=announcemntBrief %></p>

							<p class="card-text text-dark "><%=formattedDateString%>
								by <span class="card-text text-dark "><%=announcement.getCategory()%></span>
							</p>
					</a>
<hr>
					<%
	          
	          } %>


				</ul>
			</div>
			<div class="modal-footer">
               <button type="button" class="btn btn-default" id="archive">View Archive</button>			</div>
		</div>
	</div>
</div>

<%}%>


<%if(noOfAnnouncemntsInHeader > 0){ %>
<!-- MODAL FOR INDIVIDUAL ANNOUNCEMENTS-->
<%
			int count = 0;
			for(AnnouncementAcadsBean announcement : announcementsInHeader){
				  count++;
				  Date formattedDate = formatterHeader.parse(announcement.getStartDate());
				  String formattedDateString = dateFormatterHeader.format(formattedDate);
			%>


<div class="modal fade" id="announcementModal<%=count %>" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h1 class="modal-title fs-5" id="exampleModalLabel">ANNOUNCEMENTS</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
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
				<button type="button" class="btn btn-default" data-bs-dismiss="modal">DONE</button>
			</div>
		</div>
	</div>
</div>
<% }//End of For loop %>

<%}%>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
    document.getElementById("archive").onclick = function () {
        location.href = "/announcement/student/getAllStudentAnnouncements";
    };
</script>

