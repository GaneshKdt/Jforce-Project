<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Removal Of Faculty From All Stages Of Revaluation "
		name="title" />
</jsp:include>
<link rel="stylesheet" type="text/css"
	href="https://cdn.datatables.net/1.12.1/css/jquery.dataTables.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css"
	integrity="sha512-xh6O/CkQoPOWDdYTDqeRdPCVd1SpvCA9XXcUnZS2FmJNp1coAFzvtCN9BmamE+4aHK8yyUHUSCcJHgXloTyT2A=="
	crossorigin="anonymous" referrerpolicy="no-referrer" />
<style>
#actionbtn {
	width: 60px;
	justify-content: space-around;
	justify-item: center; text-align-center;
	display: flex;
}

#actionbtn a .fa-trash {
	color: #d2232a;
}

#actionbtn a .fa-trash:hover {
	bgcolor: #bd422a;
}

#table_id_filter input {
	color: #404041;
}

.sz-content-wrapper {
	padding: 0px 15px 0px 15px;
}
</style>
<body>
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Student Portal;Removal Of Faculty Form"
				name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Removal Of Faculty From All
							Stages of Revaluation</h2>
						<div class="clearfix"></div>
						<!-- start -->
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form action="searchFacultyFromAllStage" method="post"
								modelAttribute="assignmentFileBean">
								<fieldset>
									<div class="col-md-4">
										<div class="form-group" style="overflow: visible; position: sticky">
											<form:select id="facultyId" path="facultyId"
												required="required" class="combobox form-control"
												itemValue="${assignmentFileBean.facultyId}">
												<form:option value="" selected="selected">Select FacultyId</form:option>
												<form:options items="${faculties}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="month" path="month" type="text"
												placeholder="Month" class="form-control" required="required"
												itemValue="${assignmentFileBean.month}">
												<form:option value="">Exam Month</form:option>
												<form:option value="Apr">Apr</form:option>
												<form:option value="Jun">Jun</form:option>
												<form:option value="Sep">Sep</form:option>
												<form:option value="Dec">Dec</form:option>
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="year" path="year" class="form-control"
												required="required" itemValue="${assignmentFileBean.year}">
												<form:option value="">Exam Year</form:option>
												<form:options items="${yearlist}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="subject" path="subject" class="form-control"
												itemValue="${assignmentFileBean.subject}">
												<form:option value="">Select Subject</form:option>
												<form:options items="${subjects}" />
											</form:select>
										</div>




										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary">Search</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home"
													formnovalidate="formnovalidate">Cancel</button>



											</div>
										</div>
										<c:if test="${facultyListFromAllStages.size() > 0}">
									<button id="remove" name="remove"
									formaction="removalOfFacultyFromRevaluation"
									class="btn btn-large btn-primary" id="remove">remove</button>
									</c:if>
									</div>
									
								</fieldset>
							</form:form>
						</div>
						<!-- end -->
					</div>
					<!-- start Dashboard -->
					<c:if test="${facultyListFromAllStages.size() > 0}">

						<div class="sz-content" style="margin-top: -80px">

							<h2 style="margin-left: 50px;">
								&nbsp;
								List of faculty to remove from all stages of revaluation <font
									size="2px"> (${facultyListFromAllStages.size()} Records
									Found) </font>
							</h2>
							<div class="clearfix"></div>
							<div class="panel-content-wrapper">
								<div class="success-msg-count"></div>
								<div class="table-responsive">
									<table id="RemovalOfFaculty"
										class="table table-striped table-hover"
										style="font-size: 12px">
										<thead>
											<tr>
												<th>Sapid</th>
												<th>Subject</th>
												<th>Year</th>
												<th>Month</th>
												<th>Faculty 2</th>
												<th>Faculty 3</th>
												<th>faculty Id Revaluation</th>

											</tr>
										</thead>
										<tbody>
											<c:forEach var="faculty" items="${facultyListFromAllStages}"
												varStatus="status">
												<tr>
													<td><c:out value="${faculty.sapId}" /></td>
													<td><c:out value="${faculty.subject}" /></td>
													<td><c:out value="${faculty.year}" /></td>
													<td><c:out value="${faculty.month}" /></td>
													<td><c:out value="${faculty.faculty2}" /></td>
													<td><c:out value="${faculty.faculty3}" /></td>
													<td><c:out value="${faculty.facultyIdRevaluation}" /></td>

												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</c:if>
				</div>
			</div>
			<!-- end Dashboard -->
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />

	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.js"></script>
	<script type=”text/javascript”
		src=”https://cdn.datatables.net/1.10.22/js/jquery.dataTables.min.js”></script>
	<script type="text/javascript" charset="utf8"
		src="https://cdn.datatables.net/1.12.1/js/jquery.dataTables.js"></script>
	<script
		src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#RemovalOfFaculty').DataTable();
		})
	</script>
</body>
</html>