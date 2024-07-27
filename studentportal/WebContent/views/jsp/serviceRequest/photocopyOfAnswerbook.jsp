<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="${sr.serviceRequestType }" name="title" />
</jsp:include>


<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>${sr.serviceRequestType }</legend>
			</div>
			<form:form action="teeRevaluationConfirmation" method="post"
				modelAttribute="sr">
				<fieldset>

					<%@ include file="../messages.jsp"%>
					<div class="panel-body">


						<c:if test="${size > 0 }">

							<div>Dear Student, You have chosen below Service Request.
								Please select the subject/s before proceeding for Payment.</div>
							<br>

							<div class="col-md-18 column">

								<div class="form-group">
									<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
									${sr.serviceRequestType } for ${mostRecentResultPeriod } Exams
									<form:hidden path="serviceRequestType" />
								</div>

								<div class="form-group">
									<label>Charges:</label> INR. ${charges }/- per Subject
								</div>


								<div class="table-responsive">
									<table class="table table-striped" style="font-size: 12px">
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

													<tr>
														<td><c:out value="${status.count}" /></td>
														<td><c:if
																test="${studentMarks.markedForPhotocopy == 'N' }">
																<form:checkbox path="revaluationSubjects"
																	value="${studentMarks.subject}" />
															</c:if> <c:if test="${studentMarks.markedForPhotocopy == 'Y' }">
									Applied for Photocopy
								</c:if></td>
														<td nowrap="nowrap" style="text-align: left;"><c:out
																value="${studentMarks.subject}" /></td>
														<td><c:out value="${studentMarks.sem}" /></td>
														<td><c:out value="${studentMarks.writenscore}" /></td>
													</tr>
												</c:if>
											</c:forEach>

										</tbody>
									</table>
								</div>


								<div class="form-group">
									<label class="control-label" for="submit"></label>
									<div class="controls">
										<button id="submit" name="submit"
											class="btn btn-large btn-primary"
											formaction="teeRevaluationConfirmation">Proceed</button>
										<button id="cancel" name="cancel" class="btn btn-danger"
											formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
									</div>
								</div>


							</div>


						</c:if>

						<c:if test="${size == 0 }">
							<div>Dear Student, You have 0 records for
								${mostRecentResultPeriod } Term End Exams.</div>

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

					</div>
				</fieldset>


			</form:form>
		</div>

	</section>

	<jsp:include page="../footer.jsp" />

</body>
</html>
