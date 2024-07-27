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
                     © 2015 NMIMS. All Rights Reserved.
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


<%@page import="com.nmims.beans.StudentCareerservicesBean"%>

<%

boolean logout = false;
StudentCareerservicesBean student = null;
if(request.getSession().getAttribute("student_careerservices") != null){
	student = (StudentCareerservicesBean)request.getSession().getAttribute("student_careerservices");
}
if(request.getSession().getAttribute("logout") != null){
	logout = (boolean)request.getSession().getAttribute("logout");
}
%>

<style>
		
.icon-bar {
  position: fixed;
  LEFT: 95%;
  BOTTOM:5%;
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



</style>




<!--Footer-->
<footer>
	<div class="container-fluid" style="background-color: inherit">
		<div class="row col-12">
				<div class="col-md-3 col-sm-6">
					<div class="footer-info-wrapper">
						<h4>Reach Us:</h4>
						<h3>1800-1025-136 (Toll Free)</h3>
						<p>ngasce@nmims.edu</p>
					</div>
				</div>
				<div class="col-md-3 col-sm-6">
					<div class="footer-info-wrapper">
						<h4>Visit Us:</h4>
						<p>
							V.L.Mehta Road, Vile Parle (W)<br>Mumbai, Maharashtra -
							400056
						</p>
					</div>
				</div>
				<div class="col-md-3 offset-md-3 offset-sm-3 col-sm-6">
					<div class="social-wrapper">
						<h4>Connect with us:</h4>
						<ul>
							<li><a href="https://www.facebook.com/NMIMSSCE"
								class="icon-facebook" target="_blank"></a></li>
							<li><a href="https://twitter.com/NMIMS_SCE"
								class="icon-twitter" target="_blank"></a></li>
							<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-linkedin" target="_blank"></a></li> -->
						</ul>
					</div>
				</div>
			<%if(logout != false){%>
				<div class="col-md-3 col-sm-6">
					<div class="icon-bar" > 
						<a id="liveagent_button_online_57390000000H2q5"
								href="javascript://Chat"
								style="display: none; float: right;"
								onclick="liveagent.startChat('57390000000H2q5')">
								  <i class="fa fa-lg fa-comments" ></i>
						</a>        
						<div id="liveagent_button_offline_57390000000H2q5"
						style="display: none; float: right;">
						</div>
							
					</div>
				</div> 
			<%}else{ %>	
				<!-- <div class="col-md-3 col-sm-6">
					<div class="social-wrapper">
						<h4>Chat With US:</h4>
						<ul>
							<li><a id="liveagent_button_online_57390000000H2q5"
								href="javascript://Chat"
								style="display: none; float: right;"
								onclick="liveagent.startChat('57390000000H2q5')">
								<i class="fa fa-lg fa-comments"></i></a>        
								<div id="liveagent_button_offline_57390000000H2q5"
								style="display: none; float: right;"></div>
							</li>
						</ul>
						     
					</div>
				</div>  -->
				<div class="icon-bar"> 
					<a id="liveagent_button_online_57390000000H2q5" href="javascript://Chat" style="float: right;" onclick="liveagent.startChat('57390000000H2q5')">
							  <i class="fa fa-lg fa-comments"></i>
					</a> &nbsp; &nbsp; &nbsp; &nbsp;
					<div id="liveagent_button_offline_57390000000H2q5" style="display: none; float: right;">
					</div>
				</div>
			<%} %>
			<div class="clearfix"></div>
		</div>
		<p>&copy; 2016 NMIMS. All Rights Reserved.</p>
	</div>

	<% double random = Math.random(); %>

	<img alt=""
		src="/studentportal/resources_2015/images/singlePixel.gif?id=<%=random%>">
	<img alt=""
		src="/acads/resources_2015/images/singlePixel.gif?id=<%=random%>">
	<img alt=""
		src="/exam/resources_2015/images/singlePixel.gif?id=<%=random%>">

</footer>


<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="assets/js/jquery-1.11.3.min.js"></script>

<%	
	//This snippet checks if the server should reset sessions and does so if it equals true
	//requires jquery and is a higher priority function so happens right after jquery is loaded
	if(request.getParameter("resetSessions") != null && request.getParameter("resetSessions").equals("true")){ %>
		<span id="studentportalSessionResetApp"></span>
		<span id="examSessionResetApp"></span>
		<span id="acadsSessionResetApp"></span>
		<span id="csSessionResetApp"></span>
		<script>
			var server_path = location.protocol + '//' + location.host;
			var pageName = "/refreshStudentDetails";
			$("#studentportalSessionResetApp").load(server_path + "/studentportal" + pageName);
			$("#examSessionResetApp").load(server_path + "/exam" + pageName);
			$("#acadsSessionResetApp").load(server_path + "/acads" + pageName);
			$("#csSessionResetApp").load(server_path + "/careerservices" + pageName);
		</script>
<% 	} %>

<script src="assets/js/bootstrap.js"></script>

<!-- Custom Included JavaScript Files -->
<script src="assets/js/moment.min.js"></script>
<script src="assets/js/fullcalendar.min.js"></script>
<script src="assets/js/jquery.plugin.min.js"></script>

<script src="assets/js/jquery.countdown.js"></script>
<script src="assets/js/main.js"></script>
<script src="resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="resources_2015/js/dependent-dropdown.js"></script>

<script src="assets/jquery-confirm/jquery-confirm.min.js">	</script>
<script type="text/javascript" src="assets/dataTable/datatables.min.js"></script>

<!-- Live Agent button code snippet -->
<!--  
<script type="text/javascript">
if (!window._laq) { window._laq = []; }
window._laq.push(function(){liveagent.showWhenOnline('57390000000H2q5', document.getElementById('liveagent_button_online_57390000000H2q5'));
liveagent.showWhenOffline('57390000000H2q5', document.getElementById('liveagent_button_offline_57390000000H2q5'));
});
</script>
 
<script type='text/javascript' src='https://c.la1-c1-ukb.salesforceliveagent.com/content/g/js/42.0/deployment.js'></script>
<script type='text/javascript'>
liveagent.init('https://d.la1-c1-ukb.salesforceliveagent.com/chat', '57290000000H2JL', '00D90000000s6BL');
</script> -->


<script>

	$(".wysiwyg-generated p").removeAttr("style");
	$(".wysiwyg-generated span").removeAttr("style");
	
	//add active to the li on navigation menus depending on the url
	var path = window.location.pathname.split("/")[1] + "/" + window.location.pathname.split("/")[2];
	
	$('li>a[href*="' + path + '"]').parent().addClass('active');
	$('li>a[href*="' + path + '"]').parent().parent().parent().addClass('active');
	if(window.location.pathname.split("/")[1] == "careerservices"){
		$('#csSidebarIcon').addClass('active');
	}
</script>
<script type="text/javascript">
if (!window._laq) { window._laq = []; }
window._laq.push(function(){liveagent.showWhenOnline('57390000000H2q5', document.getElementById('liveagent_button_online_57390000000H2q5'));
liveagent.showWhenOffline('57390000000H2q5', document.getElementById('liveagent_button_offline_57390000000H2q5'));
});
</script>
<script type='text/javascript' src='https://c.la1-c1-ukb.salesforceliveagent.com/content/g/js/42.0/deployment.js'></script>
<script type='text/javascript'>
liveagent.init('https://d.la1-c1-ukb.salesforceliveagent.com/chat', '57290000000H2JL', '00D90000000s6BL');
<%
if(logout != false){%>
var fullName = '<%=student.getFirstName().trim()%>'+' '+'<%=student.getLastName().trim()%>';
var city = '<%=student.getCity().trim()%>';
var email = '<%=student.getEmailId().trim()%>';
//console.log(fullName + city + email);
 liveagent.addCustomDetail('fullName', fullName ); 

 liveagent.addCustomDetail('mobile', <%=student.getMobile().trim()%> );

 liveagent.addCustomDetail('email',  email);

 liveagent.addCustomDetail('city', city); 
 <%}%>
</script>