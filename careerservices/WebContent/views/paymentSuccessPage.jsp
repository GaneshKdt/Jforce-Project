<!DOCTYPE html5>


<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
	
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	
	
	
	<jsp:include page="common/jscss.jsp">
		<jsp:param value="Payment Successfull" name="title"/>
	</jsp:include>
	<body>
		<%@ include file="common/header.jsp" %>
		<div class="sz-main-content-wrapper">
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">	
					<% if(session.getAttribute("userId") != null){ %>
						<jsp:include page="common/breadcrum.jsp">
							<jsp:param value="Career Services;Payment Successful" name="breadcrumItems" />
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
									Transaction Successful!
								</h2>
								<p class="col-12" style="font-size: 1rem"> 
									Your payment tracking id is: <%= request.getParameter("paymentTrackId") %>
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
			<jsp:include page="common/footer.jsp" />
			<%			
				String examAppRefreshStudentBean = (String)pageContext.getAttribute("server_path") + "exam/refreshStudentDetails";
				String acadsAppRefreshStudentBean  = (String)pageContext.getAttribute("server_path") + "acads/refreshStudentDetails";
				String csAppRefreshStudentBean  = (String)pageContext.getAttribute("server_path") + "studentportal/refreshStudentDetails";
			%>
			 <script>
				$( "#examApp" ).load( "<%=examAppRefreshStudentBean%>" );
				$( "#acadsApp" ).load( "<%=acadsAppRefreshStudentBean %>" );
				$( "#csApp" ).load( "<%=csAppRefreshStudentBean %>" );
			</script>
		</div>
	</body>
</html>