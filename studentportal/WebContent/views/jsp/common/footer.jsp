<%-- <div class="container-fluid footerWrapper">
        <footer>
            <div class="col-md-6 footerSection" style="color:white;;margin-top:20px;">
                <h2>Social Connect</h2>
                <h3>Connect with us via Social Media and get all our latest news and upcoming events.</h3>
                
                <div class="row">
                    <div class="col-lg-18">
                        <ul class="footerSocialLinks">
                            <li><a href="https://www.facebook.com/NMIMSSCE" target="_blank" class="facebook"><i class="fa fa-facebook"></i></a></li>
                            <li><a href="https://twitter.com/NMIMS_SCE" target="_blank" class="twitter"><i class="fa fa-twitter"></i></a></li>
                            <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" target="_blank" class="google-plus"><i class="fa fa-google-plus"></i></a></li>
                            <li><a href="#" target="_blank" class="youtube"><i class="fa fa-youtube"></i></a></li>
                        </ul>
                    </div>  
                </div>
            </div>
            
            <div class="col-md-6 footerSection" style="color:white;;margin-top:20px;">
                <h2>Contact NGA SCE</h2>
                
                    Address:<br>
                    V.L.Mehta Road, Vile Parle (W), Mumbai, <br>
                    Maharashtra - 400056
                <br>
                    Email Address: ngasce@nmims.edu
                <br>
                    Tel. No: 7506283418 / 022 65265057
               <br>
                     � 2015 NMIMS. All Rights Reserved.
            </div>
            
            <!-- Session Synchronization -->
            
            <% double random = Math.random(); %>

            <img alt="" src="/exam/resources_2015/images/singlePixel.gif?id=<%=random%>">
            <img alt="" src="/acads/resources_2015/images/singlePixel.gif?id=<%=random%>">
            
        </footer>
	</div>
    
    
<script src="resources_2015/js/vendor/jquery-1.11.2.min.js"></script>
<script src="resources_2015/js/vendor/bootstrap.min.js"></script>
<script src="resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="resources_2015/js/vendor/jquery.validate.min.js"></script>
<script src="resources_2015/js/vendor/additional-methods.min.js"></script>
<script src="resources_2015/js/vendor/fileinput.min.js"></script>
<script src="resources_2015/js/vendor/bootstrap-datepicker.min.js"></script>
<script src="resources_2015/js/vendor/scripts.js"></script>
<script src="resources_2015/js/vendor/slick.js"></script>
<script src="resources_2015/js/main.js?id=2"></script>


 --%>
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>

<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://www.springframework.org/tags" prefix = "spring" %>
<% StudentStudentPortalBean sbean = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");%>
<% String validity =(String) request.getSession().getAttribute("validityExpired");
%>
<%
try{

	BaseController footerCon = new BaseController();
	StudentStudentPortalBean student = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
	Boolean logout = (Boolean)request.getSession().getAttribute("logout");
	System.out.println("logout "+logout);
%>
<jsp:include page="nyrachatbot.jsp" flush="true" />
 <%-- 
<script id="fsChatWidget" data-view= "false" data-anime="bounce" src="https://cognitive.findabilityplatform.com/NMSTApplication-uat/resources/js/chatwidget.js?key=${encJson }">
		</script>
 --%>
 <style>
.icon-bar {
	position: fixed;
	LEFT: 95%;
	BOTTOM: 5%;
	transform: translateY(100%);
}

/* Style the icon bar links */
.icon-bar a {
	background: #bb0000;
	text-align: center;
	padding: 16px;
	transition: all 0.3s ease;
	color: white;
	font-size: 16px;
}

.icon-bar a:hover {
	background-color: #000;
}

.icon-bar h4 {
	text-align: center;
	padding: 2px;
	transition: all 0.3s ease;
	color: white;
	font-size: 16px;
}

.regButtonPosition{
	position: fixed; 
	bottom: -13px; 
	right: 60px; 
	display: none;
}

.regButton{
	padding: 15px; 
	background: #b3002d; 
}
#quickLink a
{
color: white;
}

</style>

<!--Footer-->
<footer>
	<div class="container-fluid" >
		<div class="row">
			<%if (!validity .equalsIgnoreCase("Yes")) {%>
		<div class="col-lg-1 col-md-2  col-sm-2 "></div>
			<div class="col-lg-2 col-md-2  col-sm-2 ">			
					<div class="footer-info-wrapper " id="quickLink">
						<ul class="list-unstyled  ">
							<h4>Quick Links:</h4>
							<li ><a href="/exam/student/myDocuments" >My Documents</a></li>
							<li><a href="/exam/student/viewModelQuestionForm">Demo Exam</a></li>
							<li><a href="/studentportal/supportOverview" >Support Overview</a></li>
							<li><a href="/exam/selectSubjectsForm" >Register for Exam</a></li>
	              			<li><a href="/studentportal/reRegistrationPage" target="_blank">Re-Registration</a></li>
						</ul>
					</div>
				</div>
				<%}else{ %>
				<div class="col-lg-2 col-md-2  col-sm-2 ">	</div>
				<%} %>
			
				<div class="col-lg-3 col-sm-3 col-md-3 ">
					<div class="footer-info-wrapper">
						<h4>Reach Us:</h4>
						<p>1800-1025-136 (Toll Free)</p>
						<p>ngasce@nmims.edu</p>
					</div>
				</div>
			
				<div class="col-lg-3 col-md-3 col-sm-2">
					<div class="footer-info-wrapper">
						<h4>Visit Us:</h4>
						<p>
							V.L.Mehta Road, Vile Parle (W)<br>Mumbai, Maharashtra -
							400056
						</p>
					</div>
				</div>
			
				 <div class="col-lg-3 col-md-2 col-sm-3 text-center">
                        <div class="footer-info-wrapper" id="quickLink">
						<h4>Connect with us:</h4>
							<a href="https://www.instagram.com/nmimsglobal/"
								class="fa-brands fa-instagram fa-xl"  style="color: white; margin-right: 5px" target="_blank"></a>
							<a href="https://www.youtube.com/@NMIMSGlobal"
								 class="fa-brands fa-youtube text-white fa-xl"  style="color: white; margin-right: 5px"  target="_blank"></a>
							<a href="https://www.facebook.com/NMIMSSCE"
								class="fa-brands fa-facebook-f text-white fa-xl"  style="color: white; margin-right: 5px"  target="_blank"></a>
							<a href="https://twitter.com/NMIMSGlobal"
								 class="fa-brands fa-twitter text-white fa-xl"  style="color: white; margin-right: 5px"  target="_blank"></a>
							<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-linkedin" target="_blank"></a></li> -->
					</div>

						<div>
								 <a href="https://play.google.com/store/apps/details?id=com.ngasce.jforce&hl=en_IN" target="_blank">
								 <img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/playstore.png"  class="img-fluid" width="140px" style="margin-top: 10px"></a>
						</div>
						<div>		
								 <a href="https://apps.apple.com/in/app/ngasce/id1386634999" target="_blank">
								 <img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/appstore.png" class="img-fluid" width="140px"  style="margin-top: 10px"></a>	
						</div>
                    </div>
 
				<%if(logout != null){%>
				
					<!-- <div class="col-md-3 col-sm-6">
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
			
				<%}else{ %>
				<div class="col-md-3 col-sm-6">
					<div class="social-wrapper">
						<h4>Chat With US:</h4>
						<ul>
							<li><a id="liveagent_button_online_57390000000H2q5"
								href="javascript://Chat" style="display: none; float: right;"
								onclick="liveagent.startChat('57390000000H2q5')"> <i
									class=" fa-solid fa-comment fa-lg"></i></a>
								<div id="liveagent_button_offline_57390000000H2q5"
									style="display: none; float: right;"></div></li>
						</ul>
					
					</div>
				</div>
				<%} %> 
				<div class="clearfix"></div>
			
		</div>
		<p>&copy; 2023 NMIMS. All Rights Reserved.</p>
	</div>

	<% double random = Math.random(); %>

<%-- 	<img alt=""
		src="/exam/resources_2015/images/singlePixel.gif?id=<%=random%>">
	<img alt=""
		src="/acads/resources_2015/images/singlePixel.gif?id=<%=random%>">
	<img alt=""
		src="/careerservices/resources_2015/images/singlePixel.gif?id=<%=random%>"> --%>

</footer>
<script src="https://d3q78eohsdsot3.cloudfront.net/jquery-1.11.3.min.js"></script>

<jsp:include page="analytics.jsp"/>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>

<!-- Custom Included JavaScript Files -->
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/moment.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/fullcalendar.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/jquery.plugin.min.js"></script>

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/jquery.countdown.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/main.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-combobox.js"></script>

<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>


<script type="text/javascript">
      $(document).ready(function(){
        $('.combobox').combobox();
      });
    </script>


<!-- Live Agent button code snippet -->
<script type="text/javascript">
if (!window._laq) { window._laq = []; }
window._laq.push(function(){liveagent.showWhenOnline('57390000000H2q5', document.getElementById('liveagent_button_online_57390000000H2q5'));
liveagent.showWhenOffline('57390000000H2q5', document.getElementById('liveagent_button_offline_57390000000H2q5'));
});
</script>
<script type='text/javascript' src='https://c.la1-c1-ukb.salesforceliveagent.com/content/g/js/42.0/deployment.js'></script>
<script type='text/javascript'>
liveagent.init('https://d.la1-c1-ukb.salesforceliveagent.com/chat', '57290000000H2JL', '00D90000000s6BL');
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
%>
    <c:redirect url = "/studentportal/login"/>
<%	
}
%>

<!-- Refersh session on body events -->
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/refreshSession-v1.js"></script>
