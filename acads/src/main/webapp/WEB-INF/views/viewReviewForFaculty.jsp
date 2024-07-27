<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Faculty Review" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Faculty Review (${rowCount} Assigned)</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<c:choose>
				<c:when test="${rowCount > 0}">

					<div class="table-responsive">
						<table class="table table-striped" style="font-size: 12px">
							<thead>
								<tr>
									<th>Sr. No.</th>
									<th>Session Date</th>
									<th>Subject</th>
									<th>Session Name</th>
									<th>Faculty Name</th>
									<th>Corporate Name</th>
									<th>Review Status</th>
									<th>Actions</th>
								</tr>
							</thead>
							<tbody>
								<%try{ %>
								<c:forEach var="review" items="${reviewFacultyList}"
									varStatus="status">
									<c:url value="${SERVER_PATH}acads/admin/reviewFacultyForm" var="reviewFacultyFormUrl">
										<c:param name="reviewId" value="${review.id}" />
										<c:param name="action" value="${action}" />
									</c:url>
									<tr>
										<td><c:out value="${status.count}" /></td>
										<td><c:out value="${review.date}" /></td>
										<td><c:out value="${review.subject}" /></td>
										<td><c:out value="${review.sessionName}" /></td>
										<td><c:out value="${review.firstName} ${review.lastName}" /></td>
										<td><c:out value="${review.corporateName}" /></td>
										<td><c:out value="${review.reviewed}" /></td>
										<td><a href="${reviewFacultyFormUrl}" title="Evaluate"><i
												class="fa fa-check-square-o fa-lg"></i></a></td>
									</tr>
								</c:forEach>
							 <%}catch(Exception e){ 
							 
							 e.printStackTrace();}%>
							 

							</tbody>
						</table>
					</div>
					<br>

				</c:when>
				<c:otherwise>
					<h1>No Faculty alllocated for review</h1>
				</c:otherwise>
			</c:choose>


		</div>


	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
