<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.nmims.beans.LeadStudentPortalBean"%>
<%@page import="com.nmims.daos.LeadDAO"%>
<%@page import="com.nmims.beans.AnnouncementStudentPortalBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>

<%@page import="com.nmims.beans.UserAuthorizationStudentPortalBean"%>
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>

<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	BaseController infoCon = new BaseController();
	String userId = (String)session.getAttribute("userId");
	PersonStudentPortalBean user = (PersonStudentPortalBean)session.getAttribute("user_studentportal");
	StudentStudentPortalBean studentBean = (StudentStudentPortalBean)session.getAttribute("student_studentportal");

	//System.out.println("studentBean :"+studentBean);
	//System.out.println("user :"+user);
	String name = "";
	String roles = "";
	String program = "";
	String studentPhotoUrl = "";
	String userEmail = "";
	String userMobile = "";
	String pStructure = "";
	String validity= "";
	
	if(user != null){
		roles = user.getRoles();
		name = user.getFirstName() + " " + user.getLastName();
		//program = user.getProgram();
		userEmail = user.getEmail();
		userMobile = user.getContactNo();
	}
	if(studentBean != null){
		
		name = studentBean.getFirstName() + " " + studentBean.getLastName();
		pStructure = studentBean.getPrgmStructApplicable();
		userEmail = studentBean.getEmailId();
		program = studentBean.getProgram(); //Since the program was showing null for the student//
		userMobile = studentBean.getMobile();
		validity = studentBean.getValidityEndMonth()+" "+studentBean.getValidityEndYear();
		
	}
	if(studentBean != null && studentBean.getImageUrl() != null){
		studentPhotoUrl = studentBean.getImageUrl().trim();
		if(infoCon.checkLead(request, response)){
			studentPhotoUrl = "../resources_2015/images/userImg.jpg";
		}
	}
	
	UserAuthorizationStudentPortalBean userAuthorization = (UserAuthorizationStudentPortalBean)session.getAttribute("userAuthorization_studentportal");
	if(userAuthorization != null){
		roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
	}

	ArrayList<AnnouncementStudentPortalBean> announcementsInProfile = (ArrayList<AnnouncementStudentPortalBean>)session.getAttribute("announcements");
	int noOfAnnouncemntsInProfile = announcementsInProfile != null ? announcementsInProfile.size() : 0;

%>



<!--Top Student Profile bar -->
<div class="student-info-bar">
	<div class="mobile-menu visible-xs">
		<div class="row">
			<div class="col-xs-4">
				<a href="#" id="mobileToggleNav"><span
					class="glyphicon glyphicon-menu-hamburger"></span></a>
			</div>
			<div class="col-xs-8 text-right">
				<a href="/studentportal/updateProfile"><span
					class="icon-my-profile"></span></a> <a href="/studentportal/logout"><span
					class="icon-logout"></span></a> <a href="#mobile-announcements"
					id="mobileNotification"><span class="icon-no-announcements"></span><span
					class="count"><%=noOfAnnouncemntsInProfile %></span></a>
			</div>
		</div>
	</div>
	<span class="student-image"
		style="background-image:url(<%= studentPhotoUrl %>);"></span>
	<h3 class="student-name"><%=name.toUpperCase() %></h3>
	<ul class="student-info-list">
		<li class="hidden-xs"><%=userEmail%></li>
		<li class="hidden-xs"><%=userMobile %></li>
		<li class="hidden-xs"><%=program %></li>	
		<% if(!infoCon.checkLead(request, response)){ %>
			<li><%=userId %></li>
			<fmt:parseDate value="${validityEndDate}" var="validityEndDate" pattern="yyyy/MM/dd" />
			<li>Validity End: <fmt:formatDate pattern="dd-MMM-yyyy" value="${validityEndDate}" />
		<% } else{%>
		<li>Validity End: <%=validity %></li>
		<% } %>
	</ul>
	<!-- <a href="#" class="customize-btn hidden-xs">CUSTOMIZE</a> -->
	<div class="clearfix"></div>
</div>




