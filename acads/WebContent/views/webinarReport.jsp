<!DOCTYPE html>
<html lang="en">
<%@page import="java.util.Map"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Webinar Participants Report" name="title" />
</jsp:include>
<link rel="stylesheet" type="text/css"
	href="https://cdn.datatables.net/1.10.24/css/jquery.dataTables.css">
<link rel="stylesheet" type="text/css" href="/DataTables/datatables.css">
<style>
#fullPageLoading {
	position: fixed;
	height: 100%;
	width: 100%;
	z-index: 10;
	display: flex;
}

#loader-container {
	margin-top: auto;
	margin-bottom: auto;
	margin-left: auto;
	margin-right: auto;
	background-color: white;
	padding: 20px;
	border-radius: 5px;
	z-index: 11111;
	text-align: center;
}

#loader {
	border: 16px solid #f3f3f3; /* Light grey */
	border-top: 16px solid #d2232a; /* Blue */
	border-radius: 50%;
	width: 120px;
	height: 120px;
	animation: spin 2s linear infinite;
}

.input-group .form-control{
	z-index: 0;
}

@
keyframes spin { 0% {
	transform: rotate(0deg);
}
100%
{
transform




:


 


rotate




(360
deg


);
}
}
</style>
<body>
	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Academics;Webinar Participants Report"
				name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Webinar Participants Report</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="viewSessionParticipantsReport" method="post"
								modelAttribute="searchBean">
								<fieldset>
									<div class="panel-body">

										<div class="col-md-6 column">
											<div class="form-group">
												<form:select id="year" path="year" type="text"
													placeholder="Year" class="combobox form-control"
													required="true" itemValue="${searchBean.year}">
													<form:option value="">Select Academic Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>

											<div class="form-group">
												<form:select id="month" path="month" type="text"
													placeholder="Month" class="combobox form-control"
													required="true" itemValue="${searchBean.month}">
													<form:option value="">Select Academic Month</form:option>
													<form:option value="Jan">Jan</form:option>
													<form:option value="Jul">Jul</form:option>
													<form:option value="Oct">Oct</form:option>
													<form:option value="Apr">Apr</form:option>
												</form:select>
											</div>

											<div class="form-group" style="overflow: visible;">
												<form:select id="subjectCode" path="subjectCodeId"
													class="combobox form-control"
													itemValue="${session.subjectCode}">
													<form:option value="">Select Subject Code</form:option>
													<c:forEach items="${subjectCodeMap}" var="subjectMap">
														<form:option value="${subjectMap.key}">${subjectMap.value}</form:option>
													</c:forEach>
												</form:select>
											</div>


											<%-- <div class="form-group">
											<form:select id="facultyFullName" path="facultyId" class="combobox form-control">
												<form:option value="">Type OR Select Faculty</form:option>
												<c:forEach items="${facultyIdMap}" var="facultyMap">
													<form:option value="${facultyMap.key}">${facultyMap.value}</form:option>
												</c:forEach>
											</form:select>
										</div> --%>

											<div class="form-group">
												<label for="dateTo">Date From : </label><br>
												<form:input id="dateFrom" path="dateFrom" type="date"
													placeholder="Session Date" class="form-control"
													value="${searchBean.dateFrom}" />
											</div>

											<div class="form-group">
												<label for="dateTo">Date To : </label><br>
												<form:input id="dateTo" path="dateTo" type="date"
													placeholder="Session Date" class="form-control"
													value="${searchBean.dateTo}" />
											</div>
											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<button id="submit" name="submit"
													class="btn btn-large btn-primary">Search</button>
												<!-- <button id="submitAll" name="submitAll" class="btn btn-large btn-primary" formaction="searchScheduledSession?searchType=all">Search All</button> -->
												<button type="reset" id="reset" name="reset" class="btn btn-danger">RESET</button>
											</div>
										</div>
									</div>
								</fieldset>
							</form:form>
							<div class="panel-body">
								<c:choose>
									<c:when test="${rowCount >= 0}">
										<div class="container-fluid customTheme">
											<legend>
												&nbsp;Scheduled Sessions<font size="2px">
													(${rowCount} Records Found) &nbsp; <a href="exportexcel">Download
														to Excel</a>
												</font>
											</legend>
										</div>
										<div class="table-responsive" id="detailSecond">
											<table id="reportTable" class="dataTable"
												style="font-size: 12px; padding: 20px">
												<thead>
													<tr>
														<th>Student Id</th>
														<th>Student Name</th>
														<th>Subject Name</th>
														<th>Semester No</th>
														<th>Session No</th>
														<th>Start Time</th>
														<th>End Time</th>
														<th>Duration in single login</th>
														<th>Total duration in all logins</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="bean" items="${sessionAttendanceList}">
														<tr>
															<td><c:out value="${bean.getSapId() }"></c:out></td>
															<td><c:out value="${bean.getName() }"></c:out></td>
															<td><c:out value="${bean.getSubject() }"></c:out></td>
															<td><c:out value="${bean.getSem() }"></c:out></td>
															<td><c:out value="${bean.getSessionName() }"></c:out></td>
															<td><c:out value="${bean.getJoin_time() }"></c:out></td>
															<td><c:out value="${bean.getLeave_time() }"></c:out></td>
															<td><c:out value="${bean.getDuration() }"></c:out></td>
															<td><c:out value="${bean.getTotalDuration() }"></c:out></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</c:when>
									<c:otherwise>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="adminCommon/footer.jsp" />
	<script
		src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$('.dataTable').DataTable();
		});
		
		$(document).click(function(event) {
	        if (!($(event.target).is("span"))) {
	            $('.caret').css('display','block');
	            $('.glyphicon-remove').css('display','none');
	        } 
	    });
	</script>
</body>
</html>