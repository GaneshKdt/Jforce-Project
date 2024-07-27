<!DOCTYPE html5>


<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
	
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<jsp:include page="common/jscss.jsp">
		<jsp:param value="Payment Failed" name="title"/>
	</jsp:include>
	<body>
		<jsp:include page="/views/common/header.jsp"/>
		
		<div class="sz-main-content-wrapper">
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">	
					<% if(session.getAttribute("userId") != null){ %>
						<jsp:include page="common/breadcrum.jsp">
							<jsp:param value="Career Services;Payment Failed" name="breadcrumItems" />
						</jsp:include>
						<jsp:include page="common/left-sidebar.jsp">
							<jsp:param value="" name="activeMenu"/>
						</jsp:include>
					<% } %>
					
					<div class="sz-content-wrapper dashBoard myCoursesPage">
						<div class="sz-content">
							<div class="m-5 row">
							
								<div class="col-12">
									<jsp:include page="/views/common/messages.jsp"/>
								</div>
								<h2 class="col-12">
									Transaction Failed!
								</h2>
								<p class="col-12" style="font-size: 1rem"> 
									There was either an error processing your transaction or it was cancelled. Please contact the support team if there seems to be any issue with the same.
								</p>
								<div class="col-12" id="studentPortalLink">
									<button class="btn btn-primary" onclick="window.location.href='/studentportal/home'">
										Click here to return to the Student Portal
									</button>
								</div>
								
								<script>
									if(window.location.href.includes("Mobile")){
										var studentPortalLink = document.getElementById("studentPortalLink");
										studentPortalLink.style.display = "none";
									}
								</script>
							</div>
						</div>
					</div>	
				</div>
			</div>
			
			<jsp:include page="/views/common/footer.jsp" />
		
		</div>
		
		
	</body>
</html>