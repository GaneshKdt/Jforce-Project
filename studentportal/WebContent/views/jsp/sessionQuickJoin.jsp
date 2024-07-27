<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.nmims.helpers.AESencrp"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
<html>

<meta charset="ISO-8859-1">
<jsp:include page="common/jscss.jsp">
	<jsp:param value="Session Info" name="title" />
</jsp:include>

<style>
	.student-info-list li{
		color: #404041 !important;
	}
</style>
<body>



	<div class="sz-main-content-wrapper complete-profile-warpper">

		<div class="sz-breadcrumb-wrapper">
   			<div class="container-fluid">
       			<ul class="sz-breadcrumbs">
	        		<li><a href="#">Student Zone</a></li>
	        		<li><a href="#">Start Session</a></li>
		        </ul>
          	</div>
        </div>

		<br><br>
		<div class="sz-main-content menu-closed">
			<div class="container">
				<div class="row">
				<div class="col-md-7">
				<div class="panel-content-wrapper">
					<%@ include file="common/messages.jsp"%>
						<jsp:useBean id="now" class="java.util.Date"/>
						
						<div class="panel-content-wrapper">
							<div class="table-responsive">
								
								<table class="table table-striped table-hover" style="font-size:12px">
									<thead>
										<tr>
											<th>Subject</th>
											<th>Session Name</th>
											<th>Track</th>
											<th>Date</th>
											<th>Start Time</th>
											<th>Action</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${sessionList}" var="session">
											<tr>
												<input type="hidden" class="myField" value="${session.date}T${session.startTime}+05:30" />
												<td><c:out value="${session.subject }"></c:out></td>
												<td><c:out value="${session.sessionName }"></c:out></td>
												<td>
												<c:choose>
													<c:when test="${empty session.track}">
														<c:out value="Common For All Batches"></c:out>
													</c:when>
													<c:otherwise>
														<c:out value="${session.track}"></c:out>
													</c:otherwise>
												</c:choose>
												</td>
												<td class="myDate"><c:out value="${session.date }"></c:out></td>
												<td class="myTime"><c:out value="${session.startTime }"></c:out></td>
												<td><button type="button" id="sessionId" class="btn btn-primary joinSession" 
														value = "${session.id}~${session.prgmSemSubId}">Join Session</button></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
							
						<div class="clearfix"></div>
						</div>
						
						<a class="btn btn-primary" title="goHome" id="goToPortal" >Go To Home</a>
				</div>
				</div>
				
				<div class="col-md-5">
					<iframe id="sessionFrame" src="" width="100%" seamless="seamless" height="550" frameborder="0"></iframe>
				</div>
				</div>
			</div>
		</div>
	</div>
<%-- 	<jsp:include page="common/footer.jsp" /> --%>
		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
 		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/moment.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath }/assets/js/sessionQuickJoin.js"></script>
		 <script>
		
			<%
				String encryptedSapId =  "";
				encryptedSapId = URLEncoder.encode(AESencrp.encrypt((String)session.getAttribute("userId"))); 
				String acadsAppSSOUrl = (String)pageContext.getAttribute("server_path") + "acads/loginforSSO?uid="+encryptedSapId;
			%>
		
		</script>
		
		<div id="acadsApp"></div>
		
		<script>
		$(document).ready(function(){
			$( "#acadsApp" ).load('<%=acadsAppSSOUrl%>', function() {
// 				alert('Test')
			});
		});
		</script>
		
		
	
</body>
</html>