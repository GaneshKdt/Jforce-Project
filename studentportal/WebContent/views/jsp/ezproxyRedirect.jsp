<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<html class="no-js">
<!--<![endif]-->
<jsp:include page="jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>

<script> 

		    var tenth = ''; 
		 
		    function ninth() { 
		        if (document.all) { 
		            (tenth); 
		            alert("Right Click Disable"); 
		            return false; 
		        } 
		    } 
		 
		    function twelfth(e) { 
		        if (document.layers || (document.getElementById && !document.all)) { 
		            if (e.which == 2 || e.which == 3) { 
		                (tenth); 
		                return false; 
		            } 
		        } 
		    } 
		    if (document.layers) { 
		        document.captureEvents(Event.MOUSEDOWN); 
		        document.onmousedown = twelfth; 
		    } else { 
		        document.onmouseup = twelfth; 
		        document.oncontextmenu = ninth; 
		    } 
		    document.oncontextmenu = new Function('alert("Right Click Disable"); return false') 
		</script>


<body class="inside">

	<%@ include file="header.jsp"%>


	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<div class="col-xs-18">
					<h2>Redirecting to Digital Library. Please wait...</h2>
					<img alt="Redirecting" src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/images/redirecting4.gif"
						align="middle" class="img-responsive displayed">

				<%-- 	<form action="http://ezproxy.svkm.ac.in:2048/login" method="POST"
						name="login" id="ezform">

						<input type="hidden" name="user" value="${userId}" /> <input
							type="hidden" name="pass" value="${password}" /> <input
							type="hidden" name="url" value="^U" />

					</form>
					 --%>
					<form action="https://svkm.mapmyaccess.com/mma-auth/ldap-login/620240066c5e80ac5e9cdea6?redirect=false" method="POST"
						name="login" id="ezform"  >
						

						<input type="hidden" id="UserName-popup" name="username"  required="" value="${userId}@svkmgrp.com" > 
						<input type="hidden" id="Password-popup" name="password"  value="${password}"  >
							

					</form>
					
				</div>
				<!-- /col-xs-18 -->
			</div>
			<!-- /row -->



		</div>
		<!-- /container -->
	</section>

	<script>
		document.getElementById('ezform').submit();
	</script>

	<jsp:include page="footer.jsp" />



</body>
</html>
