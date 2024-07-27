<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Exam Results Dahboard" name="title" />
</jsp:include>

<body>

	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param
				value="Exam Results Processing ; Checklist ; Results Dashboard"
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

						<h2 class="red text-capitalize">Search For Results Base
							Records :</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="getExamResultsChecklistCount" method="post"
								modelAttribute="bean">
								<fieldset>
									<div class="row">
										<div class="col-md-3">
											<div class="form-group">
												<form:select id="enrollmentYear" path="year" type="text"
													placeholder="Year" class="form-control"
													itemValue="${bean.year}">
													<form:option value="">Select Result Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>
										</div>
									</div>

									<div class="row">
										<div class="col-md-3">
											<div class="form-group">
												<form:select id="enrollmentMonth" path="month"
													class="form-control" itemValue="${bean.month}">
													<form:option value="">Select  Result Month</form:option>
													<form:options items="${monthList}" />
												</form:select>
											</div>
										</div>
									</div>

									<div class="row">
										<div class="col-md-3">
											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<button id="submit" name="submit"
													class="btn btn-large btn-primary">Search</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home"
													formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
									</div>
									<c:if test="${showCount == 'true'}">
										<div class="row">
											<div class="col-md-3">
												<div class="table-responsive">
													<table class="table table-striped" style="font-size: 12px"
														id="passFailProcess" border="1px">
														<caption>
															<h4>Expected Data for exam year and month
																${yearMonth}</h4>
														</caption>
														<thead>
															<tr>
																<th>Category</th>
																<th>Count</th>
															</tr>
														</thead>
														<tbody>
															<tr>
																<td>Confirmed Bookings Count</td>
																<td>${confirmBookingsCount }</td>
															</tr>
															<tr>
																<td>Confirmed Project Bookings Count</td>
																<td>${confirmProjectBookingsCount }</td>
															</tr>
															<tr>
																<td>Project Not Booked Count</td>
																<td>${projectNotBookedCount }</td>
															</tr>
															<tr>
																<td>Assignment Not Submitted Count</td>
																<td>${assignmentNotSubmittedCount }</td>
															</tr>
															<tr>
																<td>Assignment Submitted But TEE Not booked Count</td>
																<td>${assignmentSubmittedButTEENotBookedCount }</td>
															</tr>
															<tr>
																<td>Total :</td>
																<td>${totalCount }</td>
															</tr>
															
															<tr>
																<td>Records Present in checklist for : ${yearMonth}</td>
																<td>${presentRecords }</td>
															</tr>
														</tbody>
													</table>
												</div>
											</div>
										</div>
										<div class="row">
											<div class="col-md-3">
												<button id="generateChecklistData" class="btn btn-primary"
													formaction="populateResultChecklist">Generate
													Base Data</button>
											</div>
										</div>
									</c:if>
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="adminCommon/footer.jsp" />


</body>
</html>