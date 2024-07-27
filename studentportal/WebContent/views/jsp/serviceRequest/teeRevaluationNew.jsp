<!DOCTYPE html>

<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="${sr.serviceRequestType }" name="title" />
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
	.table {
		font-size: 12px;
	}
	#copyCaseNote {
		color: #d2232a;
		margin-bottom: 0;
	}
	@media only screen and (min-width: 1024px) {
		#copyCaseTable {
			width: 60%;
		}
	}
</style> -->

<body>
<jsp:include page="../common/headerDemo.jsp"/>
	<%-- <%@ include file="../common/headerDemo.jsp"% --%>>
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/studentportal/home">Student Zone</a></li>
					<li><a href="selectSRForm">Select Service Request</a></li>
				</ul>
			</div>
		</div>
<%-- 		<%@ include file="../common/breadcrum.jsp"%>
 --%>		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
				<jsp:include page="../common/studentInfoBar.jsp"/>
					<%-- <%@ include file="../common/studentInfoBar.jsp"%> --%>
					<div class="sz-content">
						<h2 class="text-danger text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="card card-body">
						<jsp:include page="../common/messageDemo.jsp"/>
							<%-- <%@ include file="../common/messages.jsp"%> --%>
							<form:form action="teeRevaluationConfirmation" method="post"
								modelAttribute="sr">
								<fieldset>
									<c:if test="${size > 0 }">
										<p>Dear Student, You have chosen below Service Request.
											Please select the subject/s before proceeding for Payment.</p>
										<br>

										<div class="col-md-18 column">

											<div class="form-group">
												<form:label  class="fw-bold" path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }for ${mostRecentResultPeriod }
													Exams</p>
												<form:hidden path="serviceRequestType" />
											</div>

											<div class="form-group">
												<label class="fw-bold" >Charges:</label>
												<p>INR. ${charges }/- per Subject</p>
											</div>


											<div class="table-responsive">
												<table class="table table-striped">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th>Select</th>
															<th style="text-align: left;">Subject</th>
															<th>Sem</th>
															<th>Marks</th>
														</tr>
													</thead>
													<tbody>

														<c:forEach var="studentMarks" items="${studentMarksList}"
															varStatus="status">
															<c:if test="${not empty studentMarks.writenscore}">
																<c:choose>
																	<c:when test="${studentMarks.writenscore == 'NV' || studentMarks.writenscore == 'RIA'}">
																		<tr>
																			<td><c:out value="${status.count}" /></td>
																			<td><b>You cannot apply for the revaluation of this subject</b>
																			</td>
																			<td nowrap="nowrap" style="text-align: left;"><c:out
																					value="${studentMarks.subject}" /></td>
																			<td><c:out value="${studentMarks.sem}" /></td>
																			<td><c:out value="${studentMarks.writenscore}" /></td>
																		</tr>
																	</c:when>
																	<%-- #Commented as per Card 11453, Copy Case Subjects shown in a different table and student unable to select those subjects for revaluation
																	<c:when test="${(studentMarks.subject == 'Project' || studentMarks.subject == 'Module 4 - Project') && fn:containsIgnoreCase(studentMarks.remarks, 'Copy Case')}">
																		<tr>
																			<td><c:out value="${status.count}" /></td>
																			<td><b>You cannot apply for the revaluation of this subject</b>
																			<td nowrap="nowrap" style="text-align: left;"><c:out
																					value="${studentMarks.subject}" /></td>
																			<td><c:out value="${studentMarks.sem}" /></td>
																			<td><c:out value="${studentMarks.writenscore}" /></td>
																		</tr>
																	</c:when> --%>
																	<c:otherwise>
																		<tr>
																			<td><c:out value="${status.count}" /></td>
																			<td><c:if test="${studentMarks.markedForRevaluation == 'N' }">
																					<form:checkbox path="revaluationSubjects" class="subjectCheck"
																						value="${fn:replace(studentMarks.subject,',','|')}" onclick="checkSelectedSubjects()"/>
																				</c:if> 
																				<c:if test="${studentMarks.markedForRevaluation == 'Y' }">
																					Applied for Revaluation
																				</c:if>
																			</td>
																			<td nowrap="nowrap" style="text-align: left;"><c:out
																					value="${studentMarks.subject}" /></td>
																			<td><c:out value="${studentMarks.sem}" /></td>
																			<td><c:out value="${studentMarks.writenscore}" /></td>
																		</tr>
																	</c:otherwise>
																</c:choose>
															</c:if>
														</c:forEach>

													</tbody>
												</table>
											</div>


											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submit" name="submit"
														class="btn btn-danger" disabled
														formaction="teeRevaluationConfirmation">Proceed</button>
													<button id="cancel" name="cancel" class="btn btn-dark"
														formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
												</div>
											</div>
										</div>
									</c:if>
									<c:if test="${size == 0 }">
										<p>Dear Student, You have 0 records for
											${mostRecentResultPeriod } Term End Exams.</p>
										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary" formaction="selectSRForm">Select
													Another Service Request</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
									</c:if>
									<c:if test="${copyCaseProjectSubjectsList.size() gt 0}">
										<br/>
										<div class="form-group">
											<label class="fw-bold" for="copyCaseNote">Copy Case Subjects:</label>
											<p id="copyCaseNote">Please Note:- Subjects marked under Copy Case cannot be selected for revaluation.</p>
										</div>
										<div id="copyCaseTable" class="table-responsive">
											<table class="table table-striped table-bordered">
												<thead>
													<tr>
														<th>Sr. No.</th>
														<th>Subject</th>
														<th>Sem</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="copyCaseSubjects" items="${copyCaseProjectSubjectsList}" varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><c:out value="${copyCaseSubjects.subject}" /></td>
															<td><c:out value="${copyCaseSubjects.sem}" /></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
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
	
<script type="text/javascript"
		src="${pageContext.request.contextPath}/assets/js/serviceRequest/teeRevaluation.js"></script>
</body>
</html>