<!DOCTYPE html>

<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>

<html>

<style>

.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar .student-image
	{
	border: 2px solid #000;
}

.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar ul.student-info-list li
	{
	color: #333;
}

.action-modal {
	position: fixed; /* Stay in place */
	z-index: 2; /* Sit on top */
	padding-top: 100px; /* Location of the box */
	left: 0;
	top: 0;
	width: 100%; /* Full width */
	height: 100%; /* Full height */
	overflow: auto; /* Enable scroll if needed */
	background-color: rgb(0, 0, 0); /* Fallback color */
	background-color: rgba(0, 0, 0, 0.4); /* Black w/ opacity */
}

/* Modal Content */
.action-modal-content {
	font-family: "Open Sans";
	font-weight: 400;
	background-color: #fefefe;
	margin: auto;
	padding: 20px;
	border: 1px solid #888;
	max-height: calc(100vh - 250px);
	overflow: auto;
	border-radius: 4px;
	font-size: 1.2em;
}

.actionModal-content b {
	font-weight: 700;
}

#fullPageLoading {
	position: fixed;
	height: 100%;
	width: 100%;
	z-index: 10;
	display: flex;
}

@keyframes spin {
	0% { transform: rotate(0deg); }
	100% { transform: rotate(360deg); }
}

.highlight{
	border-color: #d2232a;
}

.highlight:focus {
	border-color: #d2232a;
}

</style>

<body>
	
	<div class="sz-main-content-wrapper complete-profile-warpper">

		<div id="showContent" class="action-modal">
			<div class="action-modal-content" id="actionModel" style="max-width: 500px; text-align: center;">
				<div id="content" style="text-align: center;">
					<p>Multiple account found, please select the one you want to log in with.</p>
					<table class="table table-borderless">
						<tbody>
							<c:forEach items="${ leadDetails }" var="details">
								<tr>
									<td>${ details.registrationId }</td>
									<td>${ details.program }</td>
									<td><a class='btn btn-primary' href='/studentportal/loginForMultipleLeadAccount?userId=${ details.registrationId }'>Login</a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>
			
	</div>
	
	<script src="assets/js/bootstrap.js"></script>
	<script src="resources_2015/js/vendor/bootstrap-combobox.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	
	<script>

	</script>

</body>
</html>