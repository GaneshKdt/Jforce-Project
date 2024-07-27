<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Announcement" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Announcement</legend>
			</div>
			<div class="panel-body">
				<div class="col-md-18 column">

					<form action="editAnnouncement" method="post">


						<h4>Subject: ${announcement.subject}</h4>
						<h4>Description: ${announcement.descriptionForDisplay}</h4>
						<h4>Start Date: ${announcement.startDate}</h4>
						<h4>End Date: ${announcement.endDate}</h4>
						<h4>Active: ${announcement.active}</h4>
						<h4>Category: ${announcement.category}</h4>


						<c:url value="/admin/editAnnouncement" var="editurl">
							<c:param name="id" value="${announcement.id}" />
						</c:url>
						<c:url value="/admin/deleteAnnouncement" var="deleteurl">
							<c:param name="id" value="${announcement.id}" />
						</c:url>


						<!-- Button (Double) -->
						<div class="control-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">

								<%if(roles.indexOf("Portal Admin") != -1){ %>
								<button id="edit" name="edit" class="btn btn-success"
									formaction="${editurl}">Edit</button>
								<button id="delete" name="delete" class="btn btn-danger"
									formaction="${deleteurl}"
									onclick="return confirm('Are you sure you want to delete this job?')">Delete</button>


								<%} %>
								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Back
									to Home</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</section>


	<jsp:include page="footer.jsp" />

</body>
</html>
