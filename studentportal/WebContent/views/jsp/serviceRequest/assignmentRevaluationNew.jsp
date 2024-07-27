
<!DOCTYPE html>

<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Assignment Revaluation" name="title" />
</jsp:include>
<!-- <style>
td {
	padding: 10px;
}

.selectCheckBox {
	width: 30px; /*Desired width*/
	height: 30px; /*Desired height*/
}

.red {
	color: red;
	font-size: 14px;
}

[type="checkbox"]:not (:checked ), [type="checkbox"]:checked {
	position: relative;
	left: 0px;
	opacity: 1;
}
</style> -->




<body>
	<jsp:include page="../common/headerDemo.jsp" />
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/studentportal/home">Student Zone</a></li>
					<li><a href="selectSRForm">Select Service Request</a></li>
				</ul>
			</div>
		</div>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">

				<div id="sticky-sidebar">
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Service Request" name="activeMenu" />
					</jsp:include>
				</div>

				<div class="sz-content-wrapper examsPage">
					<jsp:include page="../common/studentInfoBar.jsp" />

					<%-- <%@ include file="../common/studentInfoBar.jsp"%> --%>
					<div class="sz-content">
						<h2 class="text-danger text-capitalize">Assignment
							Revaluation</h2>
						<div class="clearfix"></div>
						<div class="card card-body">
							<jsp:include page="../common/messageDemo.jsp" />
							<form:form action="assignmentRevaluationConfirmation"
								method="post" modelAttribute="sr">
								<fieldset>
									<c:if test="${size > 0 }">
										<p>Dear Student, You have chosen below Service Request.
											Please select the subject/s for Assignment Revaluation before
											proceeding for Payment.</p>
										<br>
										<div class="col-md-18 column">
											<div class="form-group">
												<form:label class="fw-bold" path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }for${mostRecentResultPeriod }
													Exams</p>
												<form:hidden path="serviceRequestType" />
											</div>

											<div class="form-group">
												<label class="fw-bold">Charges:</label>
												<p>INR. 1000/- per Subject</p>
											</div>


											<div class="table-responsive">
												<table class="table table-striped" style="font-size: 12px">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th>Select For Revaluation</th>
															<th style="text-align: left;">Subject</th>
															<th>Sem</th>
															<th>Assignment Marks</th>
															<th>Low Score Reason</th>
														</tr>
													</thead>
													<tbody>

														<c:forEach var="studentMarks" items="${studentMarksList}"
															varStatus="status">
															<tr>
																<td><c:out value="${status.count}" /></td>
																<td>
																	<%-- <c:choose>   
																	 <c:when test="${fn:containsIgnoreCase({'Soft Skills for Managers','Employability Skills - II Tally','Start your Start up','Design Thinking'},studentMarks.subject )}"> 
																	Can not Apply  
																	</c:when> --%> <%--<c:otherwise>--%> <c:if
																		test="${studentMarks.markedForRevaluation == 'N' && studentMarks.toBeEvaluated == 'Y'}">
																		<form:checkbox path="revaluationSubjects"
																			value="${fn:replace(studentMarks.subject,',','~')}" />
																	</c:if> <c:if test="${studentMarks.toBeEvaluated == 'N'}">
																			Can not Apply
																		</c:if> <c:if
																		test="${studentMarks.markedForRevaluation == 'Y' }">
																			Applied for Revaluation 
																		</c:if> <%--</c:otherwise>			
																					
																	
																	</c:choose>--%>
																</td>
																<td nowrap="nowrap" style="text-align: left;"><c:out
																		value="${studentMarks.subject}" /></td>
																<td><c:out value="${studentMarks.sem}" /></td>
																<td><c:out value="${studentMarks.assignmentscore}" /></td>
																<td><c:out value="${studentMarks.reason}" /></td>
															</tr>
														</c:forEach>

													</tbody>
												</table>
											</div>
											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submit" name="submit" class="btn btn-danger"
														formaction="assignmentRevaluationConfirmation">Proceed</button>
													<button id="cancel" name="cancel" class="btn btn-dark"
														formaction="${pageContext.request.contextPath}/home"
														formnovalidate="formnovalidate">Cancel</button>
												</div>
											</div>


										</div>
									</c:if>

									<c:if test="${size == 0 }">
										<p>Dear Student, You have 0 records for Assignments for
											${mostRecentResultPeriod } Exams.</p>

										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit" class="btn btn-danger"
													formaction="selectSRForm">Select Another Service
													Request</button>
												<button id="cancel" name="cancel" class="btn btn-dark"
													formaction="${pageContext.request.contextPath}/home"
													formnovalidate="formnovalidate">Cancel</button>
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
	<jsp:include page="../common/footerDemo.jsp" />


</body>
</html>