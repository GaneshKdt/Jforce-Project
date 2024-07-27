
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>


<html class="no-js">
<!--<![endif]-->
<%@page import="java.util.Map"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Search Faculty" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Search Faculty</legend>
			</div>
			<%@ include file="messages.jsp"%>

			<form:form method="post" modelAttribute="searchBean">
				<%
					try {
				%>
				<fieldset>
					<div class="panel-body">

						<div class="col-md-6 column">

							<div class="form-group">
								<form:input id="facultyId" path="facultyId" type="text"
									placeholder="Faculty ID" class="form-control"
									value="${searchBean.facultyId}" />
							</div>

							<div class="form-group" style="overflow: visible;">
								<form:select id="facultyFullName" path="facultyFullName"
									class="combobox form-control">
									<form:option value="">Type OR Select Faculty</form:option>
									<c:forEach items="${facultyIdMap}" var="facultyMap">
										<form:option value="${facultyMap.key}">${facultyMap.value}</form:option>
									</c:forEach>
								</form:select>
							</div>

							<div class="form-group">
								<form:input id="location" path="location" type="text"
									placeholder="Location" class="form-control"
									value="${searchBean.location}" />
							</div>
							
							<div class="form-group">
									<form:select id="approvedInSlab" path="approvedInSlab"
										class="form-control" itemValue="${searchBean.approvedInSlab}">
										<form:option value="">Select Approval In Slab</form:option>
										<form:option value="A (7500)">A (7500)</form:option>
										<form:option value="B (5000)">B (5000)</form:option>
										<form:option value="C (4000)">C (4000)</form:option>
										<form:option value="D (4500)">D (4500)</form:option>
										<form:option value="E (3000)">E (3000)</form:option>
										<form:option value="F (2000)">F (2000)</form:option>
										<form:option value="None">None</form:option>
									</form:select>
							</div>

						</div>
						<div class="col-md-6">

							<div class="form-group" style="overflow: visible;">
								<form:select id="subjectPref1" path="subjectPref1">
									<form:option value="">Select Subject Preference 1</form:option>
									<form:options items="${subjectList}" />
								</form:select>
							</div>

							<div class="form-group" style="overflow: visible;">
								<form:select id="subjectPref2" path="subjectPref2">
									<form:option value="">Select Subject Preference 2</form:option>
									<form:options items="${subjectList}" />
								</form:select>
							</div>

							<div class="form-group" style="overflow: visible;">
								<form:select id="subjectPref3" path="subjectPref3">
									<form:option value="">Select Subject Preference 3</form:option>
									<form:options items="${subjectList}" />
								</form:select>
							</div>

							<div class="form-group">
								<form:input id="ngasceExp" path="ngasceExp" type="text"
									placeholder="Enter Years of NGASCE Experience"
									class="form-control" value="${searchBean.ngasceExp}" />
							</div>
						</div>
						<div class="col-md-6 column">


							<div class="form-group" style="overflow: visible;">
								<form:select id="minPeerReviewAvg" path="minPeerReviewAvg">
									<form:option value="">Select Min Peer Review Average</form:option>
									<form:options items="${feedbackResponseList}" />
								</form:select>
							</div>


							<div class="form-group" style="overflow: visible;">
								<form:select id="minStudentReviewAvg" path="minStudentReviewAvg">
									<form:option value="">Select Min Student Review Average</form:option>
									<form:options items="${feedbackResponseList}" />
								</form:select>
							</div>


							<div class="form-group" style="overflow: visible;">
								<form:select id="maxPeerReviewAvg" path="maxPeerReviewAvg">
									<form:option value="">Select Max Peer Review Average</form:option>
									<form:options items="${feedbackResponseList}" />
								</form:select>
							</div>


							<div class="form-group" style="overflow: visible;">
								<form:select id="maxStudentReviewAvg" path="maxStudentReviewAvg">
									<form:option value="">Select Max Student Review Average</form:option>
									<form:options items="${feedbackResponseList}" />
								</form:select>
							</div>


						</div>
						<div class="clearfix"></div>
						<div class="row" style="margin-top:60px;margin-left:300px;">

							<div class="form-group">
								<button id="submit" name="submit"
									class="btn btn-medium btn-primary" formaction="searchFaculty">Search</button>
								<button id="submit" name="submit"
									class="btn btn-medium btn-primary"
									formaction="searchFacultyWithPeerRatings">Search With
									Peer Ratings</button>

								<button id="submit" name="submit"
									class="btn btn-medium btn-primary"
									formaction="searchFacultyWithStudentRatings">Search
									With Student Ratings</button>
								<button id="submit" name="submit"
									class="btn btn-medium btn-primary"
									formaction="searchFacultyWithBothRatings">Search With
									Both Ratings</button>

								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />home" formnovalidate="formnovalidate">Cancel</button>
							</div>


						</div>


					</div>
				</fieldset>
				<%
					} catch (Exception e) {
							e.printStackTrace();
						}
				%>
			</form:form>

			<%
				try {
			%>
			<c:choose>
				<c:when test="${rowCount > 0}">

					<legend>
						&nbsp;Faculty Details <font size="2px"> (${rowCount}
							Records Found) &nbsp; <c:if test="${downloadFaculties ne null}">
								<a id="excel" href="${downloadFaculties}">Download To Excel</a>
							</c:if> <c:if test="${downloadFacultyPeerRating ne null}">
								<a id="excel1" href="${downloadFacultyPeerRating}">Download
									To Excel</a>
							</c:if> <c:if test="${downloadFacultiesStudentRatings ne null}">
								<a id="excel2" href="${downloadFacultiesStudentRatings}">Download
									To Excel</a>
							</c:if> <c:if test="${downloadFacultiesBothRatings ne null}">
								<a id="excel3" href="${downloadFacultiesBothRatings}">Download
									To Excel</a>
							</c:if>
						</font>
					</legend>
					<div class="table-responsive">
						<table class="table table-striped table-hover tables"
							style="font-size: 12px">
							<thead>
								<tr>
									<th>Sr.No.</th>
									<th>Faculty ID</th>
									<th>First Name</th>
									<th>Last Name</th>
									<th>Email</th>
									<th>Mobile</th>
									<th>Active</th>
									<th>Average Student Feedback</th>
									<th>Average Peer Review</th>
									<th>Actions</th>
								</tr>
							</thead>
							<tbody>

								<c:forEach var="bean" items="${facultyList}" varStatus="status">
									<tr>
										<td><c:out value="${status.count}" /></td>
										<td><c:out value="${bean.facultyId}" /></td>
										<td><c:out value="${bean.firstName}" /></td>
										<td><c:out value="${bean.lastName}" /></td>
										<td><c:out value="${bean.email}" /></td>
										<td><c:out value="${bean.mobile}" /></td>
										<td><c:out value="${bean.active}" /></td>
										<td><c:out value="${bean.studentReviewAvg}"></c:out></td>
										<td><c:out value="${bean.peerReviewAvg}"></c:out></td>
										<td><c:url value="editFaculty" var="editurl">
												<c:param name="id" value="${bean.id}" />
											</c:url> <c:url value="deleteFaculty" var="deleteurl">
												<c:param name="id" value="${bean.id}" />
											</c:url> <%
 	if (roles.indexOf("Acads Admin") != -1) {
 %> <a href="${editurl}" title="Edit"><i
												class="fa fa-pencil-square-o fa-lg"></i></a>&nbsp; <a
											href="${deleteurl}" title="Delete"
											onclick="return confirm('Are you sure you want to Deactivate this Faculty?')"><i
												class="fa fa-trash-o fa-lg"></i></a> <%
 	}
 %></td>


									</tr>
								</c:forEach>


							</tbody>
						</table>
					</div>
					<br>

				</c:when>
			</c:choose>
			<%
				} catch (Exception e) {
					e.printStackTrace();
				}
			%>
		</div>
	</section>

	<jsp:include page="footer.jsp" />

	<script>
		$(document).ready(function() {

			$('.tables').DataTable({

				"searching" : false,
				"ordering" : false,
				initComplete : function() {
					this.api().columns().every(function() {
						var column = this;
						var headerText = $(column.header()).text();
						console.log("header :" + headerText);

					});
				}
			});
		});
		$(".numonly")
				.keypress(
						function(e) {
							return (e.which != 8 && e.which != 0 && (e.which > 57 || e.which < 48)) ? false
									: true;
						});
	</script>

</body>
</html>