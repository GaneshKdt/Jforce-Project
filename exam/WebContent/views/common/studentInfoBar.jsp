<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.nmims.beans.ExamAnnouncementBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>

<%@page import="com.nmims.beans.UserAuthorizationExamBean"%>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%
	BaseController examHeadCon = new BaseController();
	String userId = (String)session.getAttribute("userId");
	Person user = (Person)session.getAttribute("user");
	StudentExamBean studentBean = (StudentExamBean)session.getAttribute("studentExam");
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
	
	UserAuthorizationExamBean userAuthorization = (UserAuthorizationExamBean)session.getAttribute("userAuthorization");
	if(userAuthorization != null){
		roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
	}

	ArrayList<ExamAnnouncementBean> announcementsInProfile = (ArrayList<ExamAnnouncementBean>)session.getAttribute("announcementsExam");
	int noOfAnnouncemntsInProfile = announcementsInProfile != null ? announcementsInProfile.size() : 0;

%>



<!--Top Student Profile bar -->
 <div id="main-content-info-bar" class="student-info-bar">
   <div class="mobile-menu visible-xs">
<div class="row d-flex justify-content-evenly">

	  <div class="col-8 col-sm-3">
	    <a href="#" id="mobileToggleNav"><span class="glyphicon glyphicon-menu-hamburger"></span></a>
	  </div>
	  
  
	  <div class="col-4 col-sm-8">
		  <div class="row">
			  <div class="col-12 col-sm-6">
				   <div class="row">
					    <div class="col-6">
					     	<a href="/studentportal/student/updateProfile"><span class="fa-regular fa-user"></span></a>
					    </div>
					   <div class="col-6">
					  	  <a href="/logout"><span class="icon-logout"></span></a>
					   </div>
				   </div>
			 
			  </div>
			  <div class="col-12 col-sm-6"></div>
				   <a href="#mobile-announcements" id="mobileNotification">
				     <div class="row ">
				     	<div class="col-6">
				     	   <span class="icon-no-announcements"></span>
				     	</div>
				   		 <div class="col-6">
				   		  <span class="count"><%=noOfAnnouncemntsInProfile %></span>
				     	</div>
				     </div>
			   		 </a>
		 	 </div>
	  </div>
  
  
</div>


   </div>
   <div  class="mt-2">
   <span class="student-image" style="background-image:url(<%=studentPhotoUrl%>);"></span>
   <h3 class="student-name"><%=name.toUpperCase() %></h3>
   <ul class="student-info-list">
     <li class="hidden-xs"><%=userEmail%></li>
     <li class="hidden-xs"><%=userMobile %></li>
     <li class="hidden-xs"><%=program %></li>
     <% if(!examHeadCon.checkLead(request, response)){ %>
			<li><%=userId %></li>
		<% } %>
   </ul>
   </div>
   <!-- <a href="#" class="customize-btn hidden-xs">CUSTOMIZE</a> -->
   <div class="clearfix"></div>
 </div>
 
 

 
 