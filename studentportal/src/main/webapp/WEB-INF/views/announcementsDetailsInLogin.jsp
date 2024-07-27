<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@page import="com.nmims.helpers.Person"%>
<html class="no-js">
<!--<![endif]-->

<jsp:include page="jscss.jsp">
	<jsp:param value="View Announcements" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<%
    Person p = (Person)session.getAttribute("user_studentportal");

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
						<fmt:parseDate var="dateObj" value="${announcement.startDate}" type="DATE" pattern="yyyy-MM-dd"/>
						
							<h4 class="panel-title">${announcement.category} : ${announcement.subject}, 
							<fmt:formatDate type="date" value="${dateObj}" pattern="dd-MMM-yyyy"/>
							</h4>
						</div>
						<div class="panel-body">${announcement.descriptionForDisplay}</div>
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
 --%>

<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html lang="en">




<jsp:include page="common/jscss.jsp">
	<jsp:param value="Announcements" name="title" />
</jsp:include>



<body>


	<div class="sz-main-content-wrapper">

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">


				<div class="sz-content-wrapper examsPage">


					<div class="sz-content" style="margin-top: -130px;">

						<h2 class="red text-capitalize">${announcementSize }
							Announcements</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="common/messages.jsp"%>

							<c:if test="${announcementSize > 0 }">
								<c:forEach var="announcement" items="${announcements}"
									varStatus="status">

									<div class="panel-body">
										<div class="panel panel-danger">
											<div class="panel-heading">
												<fmt:parseDate var="dateObj"
													value="${announcement.startDate}" type="DATE"
													pattern="yyyy-MM-dd" />

												<h4 class="panel-title">${announcement.category}
													: ${announcement.subject},
													<fmt:formatDate type="date" value="${dateObj}"
														pattern="dd-MMM-yyyy" />
												</h4>
											</div>
											<div class="panel-body">${announcement.descriptionForDisplay}</div>
										</div>
									</div>
									<!-- /row -->
								</c:forEach>
							</c:if>

						</div>

					</div>
				</div>


			</div>
		</div>
	</div>

</body>
</html>