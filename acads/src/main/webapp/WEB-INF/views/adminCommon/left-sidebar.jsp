<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.StudentAcadsBean"%>
<%@page import="com.nmims.beans.AnnouncementAcadsBean"%>
<%@page import="java.util.ArrayList"%>

 <%@page import="com.nmims.beans.UserAuthorizationBean"%>
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.StudentAcadsBean"%>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
 
<%

ArrayList<AnnouncementAcadsBean> announcementsInSideBar = (ArrayList<AnnouncementAcadsBean>)session.getAttribute("announcements");
int noOfAnnouncemntsInSidebar = announcementsInSideBar != null ? announcementsInSideBar.size() : 0;
SimpleDateFormat formatterSidebar = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat dateFormatterSidebar = new SimpleDateFormat("dd-MMM-yyyy");



String userId = (String)session.getAttribute("userId");
PersonAcads user = (PersonAcads)session.getAttribute("user");
StudentAcadsBean studentBean = (StudentAcadsBean)session.getAttribute("student");
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



%>

<style>
.sz-main-content.menu-closed .sz-main-content-inner .sz-main-navigation ul.sz-nav > li {
    padding: 0.35em 0;
}



</style>

<div class="sz-main-navigation">
        <div class="mobile-logo visible-xs"><a href="#"><img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/images/logo.png" class="img-responsive" alt=""/></a></div>
        <ul class="sz-nav">
          <!-- <li id="toggle-nav"><a href="#"><span class="icon-arrow-right"></span></a></li> -->
          <li ><a href="/studentportal/home" title="Home"><i class="fa fa-home fa-lg" ></i></a>
          </li>
          
          <jsp:include page="/views/adminCommon/studentPortalHeader.jsp">
			<jsp:param value="<%=roles %>" name="roles" />
		</jsp:include>
		
		<jsp:include page="/views/adminCommon/examHeader.jsp">
			<jsp:param value="<%=roles %>" name="roles" />
		</jsp:include>
		
		<jsp:include page="/views/adminCommon/acadsHeader.jsp">
			<jsp:param value="<%=roles %>" name="roles" />
		</jsp:include>
          
          
          
        </ul>
		
		
      </div>
      
      
 <!-- Mobile notification contents -->
 
 <%if(noOfAnnouncemntsInSidebar > 0){ %>
      <div class="mobile-notification-wrapper visible-xs" id="mobile-announcements">
        <div class="mobile-notification-header"><a href="#" class="hide-link">Hide <span class="glyphicon glyphicon-menu-right"></span></a>
          <div class="clearfix"></div>
          <h4>Announcements</h4>
          <!-- <div class="btn-group" role="group">
			  <a href="#" class="btn btn-default active">Show </a>
			  <a href="#" class="btn btn-default">All Announcements <span class="glyphicon glyphicon-menu-down"></span></a>
		  </div> -->
        </div>
        <ul class="announcement-list">
        
        	<%
        	int count = 0;
	          for(AnnouncementAcadsBean announcement : announcementsInSideBar){
	        	  count++;
	        	  Date formattedDate = formatterSidebar.parse(announcement.getStartDate());
	  			  String formattedDateString = dateFormatterSidebar.format(formattedDate);
	          %>
				          
			            <li> <a href="#" data-toggle="modal" data-dismiss="modal" data-target="#announcementModal<%=count%>">
			            <h3><%=announcement.getSubject() %></h3>
			            <p><%=announcement.getDescription() %></p>
			            <h4><%=formattedDateString%> by <span><%=announcement.getCategory() %></span> </h4>
			            </a> </li>
	          
	          <%
	          
	          } %>
         
        </ul>
      </div>
<%}%>