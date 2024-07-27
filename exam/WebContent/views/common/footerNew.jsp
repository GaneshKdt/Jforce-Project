
<%@page import="com.nmims.controllers.BaseController"%>


<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "spring" %>

<%@page import="com.nmims.beans.StudentExamBean"%>



<% String validity =(String)request.getSession().getAttribute("validityExpired");%>

<%
try{

	BaseController footerCon = new BaseController();
	/* StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal"); */
	StudentExamBean student=(StudentExamBean)request.getSession().getAttribute("studentExam");
	Boolean logout = (Boolean)request.getSession().getAttribute("logout");
%>
<jsp:include page="nyrachatbot.jsp" flush="true" />


<!--Footer-->
<footer >
	<div class="container-fluid ">
		<div class="row">
		<%if (!validity .equalsIgnoreCase("Yes")) {%>
		<div class="col-lg-1 col-md-2 "></div>
		<div class="col-lg-2  col-md-2 d-flex justify-content-center align-items-center text-center text-lg-start text-md-start text-sm-start">
					<ul class="list-unstyled">
						<h4><small class="fs-6 text-white">Quick Links:</small></h4>
						<li><a href="/exam/student/myDocuments" class="text-white "><small>My Documents</small></a></li>
							<li><a href="/exam/student/viewModelQuestionForm" class="text-white "><small>Demo Exam</small></a></li>
							<li><a href="/studentportal/supportOverview" class="text-white "><small>Support Overview</small></a></li>
							<li><a href="/exam/selectSubjectsForm" class="text-white"><small>Register for Exam</small></a></li>
	              			<li><a href="/studentportal/reRegistrationPage" class="text-white" target="_blank"><small>Re-Registration</small></a></li>
				</ul>
				</div>
				<%} %>
		<div class="col-lg-3  col-md-3 d-flex justify-content-center  text-center text-lg-start text-md-start text-sm-start">
		<ul class="list-unstyled">
		<h4><small class="fs-6 text-white">Reach Us:</small></h4>
						<h6><small class="fs-6 text-white">1800-1025-136 (Toll Free)</small></h6>
						<a href="https://studentzone-ngasce.nmims.edu/studentportal/student/connectWithUs">
							<h6><small class="fs-6 text-white">Connect With Us</small></h6>
						</a>
						</ul>
						</div>
						
		<div class="col-lg-3  col-md-2  d-flex justify-content-center text-white text-center text-lg-start text-md-start text-sm-start">
		<ul class="list-unstyled">
						<h6><small class="fs-6 text-white">Visit Us:</small></h6>
						<h6><small class=" text-white">
							V.L.Mehta Road, Vile Parle (W)<br>Mumbai, Maharashtra -
							400056</small>
						</h6>
						</ul>
		</div>
		<div class="col-lg-3 col-md-3 text-center">
		<h6><small class="text-white fs-6">Connect with us:</small></h6>
					<a href="https://www.instagram.com/nmimsglobal/"
								class="fa-brands fa-instagram  text-white fs-4 me-2"  target="_blank"></a>
									<a href="https://www.youtube.com/@NMIMSGlobal"
								 class="fa-brands fa-youtube text-white fs-4 me-2"  target="_blank"></a>
							<a href="https://www.facebook.com/NMIMSSCE"
								class="fa-brands fa-facebook-f text-white fs-4 me-2"  target="_blank"></a>
							<a href="https://twitter.com/NMIMSGlobal"
								 class="fa-brands fa-twitter text-white fs-4" target="_blank"></a>
								
								
								<div class=" mx-auto">
								 <a href="https://play.google.com/store/apps/details?id=com.ngasce.jforce&hl=en_IN" target="_blank"><img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/playstore.png"  class="img-fluid mt-3" width="140px" ></a></div>
								
								 <a href="https://apps.apple.com/in/app/ngasce/id1386634999" target="_blank"><img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/appstore.png" class="img-fluid mt-2" width="140px" ></a>
								 </div>
								  
							<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-linkedin" target="_blank"></a></li> -->
		</div>
		<%if(logout != null){%>   
				 <div class="row">
					<!-- <div class="col-md-3 ">
						<div class="icon-bar">
							<a id="liveagent_button_online_57390000000H2q5"
								href="javascript://Chat" style="display: none; float: right;"
								onclick="liveagent.startChat('57390000000H2q5')"> <i
								class=" fa-solid fa-comment fa-lg"></i>
							</a>
							<div id="liveagent_button_offline_57390000000H2q5"
								style="display: none; float: right;"></div>
	
						</div>
					</div> -->
					<!--  
					<% if(footerCon.checkLead(request, response)){ %>
					<div class="col-md-1 registerNow regButtonPosition">
						<button class="btn btn-primary regButton" style="outline: none;"> Register Now</button>
					</div>
					<% } %>
					-->
				</div> 
				<%}else{ %>
				<div class="col-md-3 text-center ">
					<div class="social-wrapper">
						<h4>Chat With US:</h4>
						<ul>
							<li><a id="liveagent_button_online_57390000000H2q5" href="javascript://Chat" style="display: none; float: right;"
								onclick="liveagent.startChat('57390000000H2q5')">
								<i class=" fa-solid fa-comment fa-lg"></i>
								</a>
								<div id="liveagent_button_offline_57390000000H2q5" style="display: none; float: right;"></div>
							</li>
						</ul>
					</div>
				</div>
				<%} %>
				<div class="clearfix"></div>

		</div>
		<p>&copy; 2023 NMIMS. All Rights Reserved.</p>
</div>

</footer>
<script src="https://d3q78eohsdsot3.cloudfront.net/jquery-1.11.3.min.js"></script>
<jsp:include page="analytics.jsp"/>

<!-- Custom Included JavaScript Files -->
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/moment.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/fullcalendar.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.plugin.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.countdown.js"></script>

<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-combobox.js"></script>


 <!-- datatable js -->
 <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/datatable/jquery-3.5.1.js"></script> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/datatable/jquery.dataTables.min.js"></script> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/datatable/dataTables.bootstrap5.min.js"></script> 
 


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/datatable/bootstrap.bundle.min.js"></script>


<script type="text/javascript">
      $(document).ready(function(){
        $('.combobox').combobox();
      });
    </script>
<script type="text/javascript">
if (!window._laq) { window._laq = []; }
window._laq.push(function(){liveagent.showWhenOnline('57390000000H2q5', document.getElementById('liveagent_button_online_57390000000H2q5'));
liveagent.showWhenOffline('57390000000H2q5', document.getElementById('liveagent_button_offline_57390000000H2q5'));
});
</script>
<script>
$(document).scroll(function() {
	var y = $(this).scrollTop();
	if (y > 600) {
		$('.registerNow').fadeIn();
	}else {
	    $('.registerNow').fadeOut();
	 }
});
</script>
<script type='text/javascript'
	src='https://c.la1-c1-ukb.salesforceliveagent.com/content/g/js/42.0/deployment.js'></script>
	<script type='text/javascript'>
liveagent.init('https://d.la1-c1-ukb.salesforceliveagent.com/chat', '57290000000H2JL', '00D90000000s6BL');
<%
	if(logout != null){
		if(footerCon.checkLead(request, response)){
%>
var fullName = "<%=student.getFirstName().trim()%>";
<% }else{%>
var fullName = "<%=student.getFirstName().trim()%>"+' '+"<%=student.getLastName().trim()%>";
<% } %>
var email = '<%=student.getEmailId().trim()%>'; 
 <%if(student.getCity() != null){%>
var city = '<%=student.getCity().trim()%>';
if(city !== null && city !== '') {
	 liveagent.addCustomDetail('city', city); 
	 console.log(fullName + city + email);
}
<%}%> 
 liveagent.addCustomDetail('fullName', fullName ); 
 liveagent.addCustomDetail('email',  email);
 liveagent.addCustomDetail('mobile', <%=student.getMobile().trim()%> ); 
 <%}%>
</script>
<%
}
catch(Exception e){
e.printStackTrace();	
%>

    
<%	
}
%>

<!-- added below script for datable -->
<script>
		$(document).ready(function () {
		    $('#datatab').DataTable();
		});
				
		</script>
		

