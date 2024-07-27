<%@page import="com.nmims.beans.ExamAnnouncementBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>

<%@page import="com.nmims.beans.UserAuthorizationExamBean"%>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%

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
	
	UserAuthorizationExamBean userAuthorization = (UserAuthorizationExamBean)session.getAttribute("userAuthorization");
	if(userAuthorization != null){
		roles = (userAuthorization.getRoles() != null && !"".equals(userAuthorization.getRoles())) ? userAuthorization.getRoles() : roles;
	}

	ArrayList<ExamAnnouncementBean> announcementsInProfile = (ArrayList<ExamAnnouncementBean>)session.getAttribute("announcementsExam");
	int noOfAnnouncemntsInProfile = announcementsInProfile != null ? announcementsInProfile.size() : 0;

%>



<!--Top Student Profile bar -->
 <div class="student-info-bar">
   <div class="mobile-menu visible-xs">
     <div class="row">
       <div class="col-xs-4"><a href="#" id="mobileToggleNav"><span class="glyphicon glyphicon-menu-hamburger"></span></a></div>
       <div class="col-xs-8 text-right">
       <a href="/studentportal/student/updateProfile"><span class="fa-regular fa-user"></span></a>
       <a href="/logout"><span class="icon-logout"></span></a>
       <a href="#mobile-announcements" id="mobileNotification"><span class="icon-no-announcements"></span><span class="count"><%=noOfAnnouncemntsInProfile %></span></a>
       </div>
     </div>
   </div>
   <%-- <span class="student-image" style="background-image:url(<%=studentPhotoUrl%>);"></span> --%>	<!-- Commented to avoid twice page reload, if studentPhotoUrl value null/empty -->
   <c:set var="studentImage" value="<%=studentPhotoUrl%>"/>
   <c:if test="${not empty studentImage}">
   	 <span class="student-image" style="background-image:url(${studentImage});"></span>
   </c:if>
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
 
 

 
 