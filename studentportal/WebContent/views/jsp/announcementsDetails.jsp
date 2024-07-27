TYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<html class="no-js">
<!--<![endif]-->

<jsp:include page="jscss.jsp">
	<jsp:param value="View Announcements" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<%
	PersonStudentPortalBean p = (PersonStudentPortalBean)session.getAttribute("user_studentportal");

	String email = "";
	String mobile = "";
	String altMobile = "";
	String postalAddress = "";
    
    if(p != null){
    	email = p.getEmail();
    	mobile = p.getContactNo();
    	postalAddress = p.getPostalAddress();
    	altMobile = p.getAltContactNo();
    }
    %>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Announcements</legend>
			</div>
			<!-- /row -->

			<%@ include file="messages.jsp"%>
			<c:forEach var="announcement" items="${announcements}"
				varStatus="status">

				<div class="panel-body">
					<div class="panel panel-danger">
						<div class="panel-heading">
							<fmt:parseDate var="dateObj" value="${announcement.startDate}"
								type="DATE" pattern="yyyy-MM-dd" />

							<h4 class="panel-title">${announcement.category}
								: ${announcement.subject},
								<fmt:formatDate type="date" value="${dateObj}"
									pattern="dd-MMM-yyyy" />
							</h4>
						</div>
						<div class="panel-body">${announcement.descriptionForDisplay}

							<br />


							<c:if test="${not empty  announcement.attachmentFile1Name}">
								<a target="_blank"
									href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" />${announcement.attachment1}">${announcement.attachmentFile1Name}</a>
							</c:if>
							<br />

							<c:if test="${not empty  announcement.attachmentFile2Name}">
								<a target="_blank"
									href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" />${announcement.attachment2}">${announcement.attachmentFile2Name}</a>
							</c:if>
							<br />

							<c:if test="${not empty  announcement.attachmentFile3Name}">
								<a target="_blank"
									href="<spring:eval expression="@propertyConfigurer.getProperty('ANNOUNCEMENT_PREVIEW_PATH')" />${announcement.attachment3}">${announcement.attachmentFile3Name}</a>
							</c:if>
							<br />

						</div>
					</div>
				</div>
				<!-- /row -->
			</c:forEach>
		</div>
		<!-- /container -->
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
