
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Extended Internal Assessment StartTime EndTime"
		name="title" />
</jsp:include>


<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

<style>
/* The snackbar - position it at the bottom and in the middle of the screen */
#snackbar {
	visibility: hidden;
	/* Hidden by default. Visible on click */
	min-width: 250px;
	/* Set a default minimum width */
	margin-left: -125px;
	/* Divide value of min-width by 2 */
	background-color: #333;
	/* Black background color */
	color: #fff;
	/* White text color */
	text-align: center;
	/* Centered text */
	border-radius: 2px;
	/* Rounded borders */
	padding: 16px;
	/* Padding */
	position: fixed;
	/* Sit on top of the screen */
	z-index: 1;
	/* Add a z-index if needed */
	left: 50%;
	/* Center the snackbar */
	bottom: 30px;
	/* 30px from the bottom */
}

/* Show the snackbar when clicking on a button (class added with JavaScript) */
#snackbar.show {
	visibility: visible;
	/* Show the snackbar */
	/* Add animation: Take 0.5 seconds to fade in and out the snackbar. 
However, delay the fade out process for 2.5 seconds */
	-webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
	animation: fadein 0.5s, fadeout 0.5s 2.5s;
}

/* Animations to fade the snackbar in and out */
@
-webkit-keyframes fadein {from { bottom:0;
	opacity: 0;
}

to {
	bottom: 30px;
	opacity: 1;
}

}
@
keyframes fadein {from { bottom:0;
	opacity: 0;
}

to {
	bottom: 30px;
	opacity: 1;
}

}
@
-webkit-keyframes fadeout {from { bottom:30px;
	opacity: 1;
}

to {
	bottom: 0;
	opacity: 0;
}

}
@
keyframes fadeout {from { bottom:30px;
	opacity: 1;
}

to {
	bottom: 0;
	opacity: 0;
}

}
.redLeftBorder {
	padding-left: 10px;
	border-left: 5px solid #C72033;
}

.greenLeftBorder {
	padding-left: 10px;
	border-left: 5px solid #49a54e;
}

input[type=number] {
	color: black;
	width: 100%;
	padding: 12px 20px;
	margin: 8px 0;
	box-sizing: border-box;
	line-height: 20px;
}
</style>

<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<!-- Custom breadcrumbs as requirement is diff. Start -->
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/exam/">Exam</a></li>
					<li><a href="/exam/mbax/ia/a/viewAllTests">Internal
							Assessments</a></li>
					<li><a href="/exam/mbax/ia/a/viewTestDetails?id=${test.id}">Internal
							Assessment Details</a></li>
					<li><a href="#">Extended Internal Assessment StartEnd
							DateTime For Student</a></li>

				</ul>
				<ul class="sz-social-icons">
					<li><a href="https://www.facebook.com/NMIMSSCE"
						class="icon-facebook" target="_blank"></a></li>
					<li><a href="https://twitter.com/NMIMS_SCE"
						class="icon-twitter" target="_blank"></a></li>
					<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->
				</ul>
			</div>
		</div>
		<!-- Custom breadcrumbs as requirement is diff. End -->

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Extend StartEnd DateTime For
							Student Of ${test.testName }</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../adminCommon/messages.jsp"%>

							<!-- Code For Page Goes in Here Start -->
							<div class="row">
								<div class="well col-sm-12">
									<h5></h5>
									<div class="">
										<div class="col-sm-6">
											<form:form id="extendedTestsStartEndTimeBySapid"
												action="/exam/mbax/ia/a/extendedTestsStartEndTimeBySapid" method="post"
												modelAttribute="bean">
												<form:hidden path="id" value="${test.id }" />

												<div class="">
													<div class="form-group">
														<textarea name="sapidList" cols="50" rows="7"
															placeholder="Enter Comma separated Sapids">${bean.sapidList}</textarea>
													</div>
												</div>

												<div class="">
													<div class="form-group">
														<form:label path="extendedStartTime"
															for="extendedStartTime">Extended Start Date</form:label>

														<div class='input-group date' id=''>
															<form:input path="extendedStartTime"
																id="extendedStartTime" type="datetime-local"
																style="color:black;" value="${test.extendedStartTime}"
																required="required" />

														</div>

													</div>
												</div>

												<div class="">
													<div class="form-group">
														<form:label path="extendedEndTime" for="extendedEndTime">Extended End Date</form:label>

														<div class='input-group date' id='   '>
															<form:input path="extendedEndTime" id="extendedEndTime"
																type="datetime-local" style="color:black;"
																value="${test.extendedEndTime}" required="required" />

														</div>

													</div>
												</div>

												<div class="">
													<div class="form-group">
														<label class="control-label" for="submit"></label>

														<button id="submit" name="submit"
															class="btn btn-large btn-primary" formmethod="post"
															formaction="/exam/mbax/ia/a/extendedTestsStartEndTimeBySapid">Save</button>
														<button id="cancel" name="cancel" class="btn btn-danger"
															formaction="home" formnovalidate="formnovalidate">Cancel</button>

													</div>
												</div>

											</form:form>
										</div>
									</div>
								</div>
							</div>

							<div class="row">

								<div class="well col-sm-12">
									<div class="jumbotron">
										<h2>Extended StartDate EndDate List</h2>
									</div>
									<div class="table-responsiv">
										<table id="fTable" class="table table-hover table-striped"
											style="font-size: 12px">
											<thead>
												<tr>
													<th>Sr No.</th>
													<th>Sapid</th>
													<th>Extended Start DateTime</th>
													<th>Extended End DateTime</th>
													<th>Delete</th>
												</tr>
											</thead>
											<tbody>

												<c:forEach var="bean" items="${extendedList}"
													varStatus="status">
													<tr>
														<td>${status.count}</td>
														<td>${bean.sapid}</td>
														<td>${bean.extendedStartTime}</td>
														<td>${bean.extendedEndTime}</td>
														<td><a
															href="/exam/mbax/ia/a/deleteExtendedTestsTimeBySapidNTestId?sapid=${bean.sapid}&testId=${bean.testId}"
															class="btn btn-primary">Delete</a></td>

													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</div>
							<!-- Code For Page Goes in Here End -->
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../adminCommon/footer.jsp" />

</body>
</html>