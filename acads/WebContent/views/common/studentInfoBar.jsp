<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.nmims.beans.AnnouncementAcadsBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>

<%@page import="com.nmims.beans.UserAuthorizationBean"%>
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.StudentAcadsBean"%>

<%

	String userId = (String)session.getAttribute("userId_acads");
	PersonAcads user = (PersonAcads)session.getAttribute("user_acads");
	StudentAcadsBean studentBean = (StudentAcadsBean)session.getAttribute("student_acads");
	BaseController acadInfo = new BaseController();
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
		program = user.getProgram();
		userEmail = user.getEmail();
		userMobile = user.getContactNo();
	}
	if(studentBean != null){
		
		name = studentBean.getFirstName() + " " + studentBean.getLastName();
		pStructure = studentBean.getPrgmStructApplicable();
		userEmail = studentBean.getEmailId();
		program = studentBean.getProgramForHeader(); //Since the program was showing null for the student//
	}
	if(studentBean != null && studentBean.getImageUrl() != null){
		studentPhotoUrl = studentBean.getImageUrl().trim();
	}
	
	UserAuthorizationBean userAuthorization = (UserAuthorizationBean)session.getAttribute("userAuthorization");
	if(userAuthorization != null){
		roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
	}

	ArrayList<AnnouncementAcadsBean> announcementsInProfile = (ArrayList<AnnouncementAcadsBean>)session.getAttribute("announcementsAcads");
	int noOfAnnouncemntsInProfile = announcementsInProfile != null ? announcementsInProfile.size() : 0;
	
%>
<style>
@media ( min-width : 768px) {
	.student-info-bar .mobile-menu {
		display:none;
	}
	.student-info-list li{
     display: inline !important;
   }
}

</style>

<!--Top Student Profile bar --> 
 <div class="student-info-bar">
   <div class="mobile-menu visible-xs">
     <div class="row">
       <div class="col-4"><a href="#" id="mobileToggleNav"><i class="fa fa-bars" aria-hidden="true"></i></a></div>
       <div class="col-8 text-end">
	       <a href="/studentportal/student/updateProfile"><span class="fa-regular fa-user"></span></a>
	       <a href="/logout"><span class="icon-logout"></span></a>
	       <a href="#mobile-announcements" id="mobileNotification"><span class="icon-no-announcements"></span><span class="count"><%=noOfAnnouncemntsInProfile %></span></a>
       </div>
     </div>
   </div>
   <span class="student-image" style="background-image:url(<%=studentPhotoUrl%>);"></span>
   <h3 class="student-name"><%=name.toUpperCase() %></h3>
   <div class="text-lg-nowrap">
   <ul class="student-info-list ">
     <li class="d-none d-sm-block d-sm-none d-md-block"><%=userEmail%></li>
     <li class="d-none d-sm-block d-sm-none d-md-block"><%=userMobile %></li>
     <li class="d-none d-sm-block d-sm-none d-md-block"><%=program %></li>
     <% if(!acadInfo.checkLead(request, response)){ %>
			<li><%=userId %></li>
		<% }%>
		
   </ul>
   </div>
   <!-- <a href="#" class="customize-btn hidden-xs">CUSTOMIZE</a> -->
   <div class="clearfix"></div>
 </div>

 