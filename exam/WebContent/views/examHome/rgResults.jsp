<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">
<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Employability Skills Results" name="title" />
</jsp:include>

<style>
</style>

<body>

	<%@ include file="../common/header.jsp"%>

	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Employability Skills Results"
				name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Employability Skills Results" name="activeMenu" />
				</jsp:include>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>

					<div class="sz-content">
						<!-- Code for page starts -->

						<h2 class="red text-capitalize">Employability Skills Results</h2>

						<div class="clearfix"></div>

						<div class="panel-content-wrapper">
							<%--<%@ include file="../common/messages.jsp" %>  --%><%--@ include
								file="../adminCommon/newmessages.jsp"--%>

							<div class="table-responsive">
								<!-- div class="row">  -->
								<c:choose>
									<c:when test="${rowCount <= 0}">
										<h2 class="red text-capitalize">Results Unavailable.</h2>
									</c:when>
									<c:when test="${rowCount > 0}">
										<table class="table courses-sessions">
											<thead>
												<tr>
													<th>Sr. No</th>
													<th>Exam Year</th>
													<th>Exam Month</th>
													<th>Subject</th>
													<th>Sem</th>
													<th>Marks</th>
													<th>Grade</th>
													<th>Remarks</th>
													<%-- th>Reason</th>  --%>
												</tr>
											</thead>
											<tbody>
												<c:forEach var="d" items="${dataList}" varStatus="statusOld">
													<tr>
														<td><c:out value="${statusOld.count}" /></td>
														<td><c:out value="${d.year}" /></td>
														<td><c:out value="${d.month}" /></td>
														<td><c:out value="${d.subject}" /></td>
														<td><c:out value="${d.sem}" /></td>
														<td><c:out value="${d.scoreTotal}" /></td>
														<td><c:out value="${d.grade}" /></td>
														<td><c:if test="${d.remarks == null}">&nbsp;</c:if><c:if test="${d.remarks != null}"><c:out value="${d.remarks}" /></c:if></td>
														<%--td><c:out value="${d.failReason}" /></td>  --%>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</c:when>
								</c:choose>
								<!-- /div>  -->

							</div>

						</div>
						<div class="clearfix"></div>
						<div class="table courses-sessions">
							<div class="panel-heading">
								<h2 class="panel-title">Note : Subjects that are
									under Employability skills are only graded as SATISFACTORY and
									NOT SATISFACTORY.</h2>
							</div>
							<div class="panel-heading">
								<ol>
									<li class="panel-title">Passing criteria is 50% (15 marks
										out of 30 marks) to be awarded SATISFACTORY. Student can
										submit the assignment in the next exam cycle as per the latest
										question paper to clear the subject.</li>
									<li class="panel-title">Students who have not cleared the
										subject, have an option to opt for revaluation, details of
										which will be communicated on email shortly.</li>
									<li class="panel-title">For any additional remark's
										students can write to ngasce@nmims.edu</li>
									<li class="panel-title">For any other queries please call
										18001025136 [Mon-Sat] 9am-7pm</li>
								</ol>
							</div>
						</div>
						<!-- Code for page ends -->
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="../common/footer.jsp" />

</body>

</html>
