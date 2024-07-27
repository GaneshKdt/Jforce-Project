<%@page import="com.nmims.beans.AnnouncementAcadsBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>

<%@page import="com.nmims.beans.UserAuthorizationBean"%>
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.StudentAcadsBean"%>

<%

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
		program = studentBean.getProgram(); //Since the program was showing null for the student//
	}
	
	UserAuthorizationBean userAuthorization = (UserAuthorizationBean)session.getAttribute("userAuthorization");
	if(userAuthorization != null){
		roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
	}

	ArrayList<AnnouncementAcadsBean> announcementsInProfile = (ArrayList<AnnouncementAcadsBean>)session.getAttribute("announcements");
	int noOfAnnouncemntsInProfile = announcementsInProfile != null ? announcementsInProfile.size() : 0;

%>



<!--Top Student Profile bar -->
 <div class="student-info-bar">
   <div class="mobile-menu visible-xs">
     <div class="row">
       <div class="col-xs-4"><a href="#" id="mobileToggleNav"><span class="glyphicon glyphicon-menu-hamburger"></span></a></div>
       <div class="col-xs-8 text-right">
       <a href="/studentportal/student/updateProfile"><span class="icon-my-profile"></span></a>
       <a href="/studentportal/logout"><span class="icon-logout"></span></a>
       <a href="#mobile-announcements" id="mobileNotification"><span class="icon-no-announcements"></span><span class="count"><%=noOfAnnouncemntsInProfile %></span></a>
       </div>
     </div>
   </div>
   <span class="student-image" style="background-image:url(<%=studentPhotoUrl%>);"></span>
   <h3 class="student-name"><%=name.toUpperCase() %></h3>
   <ul class="student-info-list">
     <li class="hidden-xs"><%=userEmail%></li>
     <li class="hidden-xs"><%=userMobile %></li>
     <li class="hidden-xs"><%=program %></li>
     <li><%=userId %></li>
   </ul>
   <!-- <a href="#" class="customize-btn hidden-xs">CUSTOMIZE</a> -->
   <div class="clearfix"></div>
 </div>
 
 

 
 