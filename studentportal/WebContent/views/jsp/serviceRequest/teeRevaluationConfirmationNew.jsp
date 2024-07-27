<!DOCTYPE html>


<html lang="en">


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
</style>
 -->
<body>

	<jsp:include page="../common/headerDemo.jsp" />

	<!-- <section class="content-container login"> -->
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

				<%-- 	<div class="container-fluid customTheme"> 
			<div class="row">
				<legend>${sr.serviceRequestType }</legend>
			</div> --%>
				<div class="sz-content-wrapper examsPage">
					<jsp:include page="../common/studentInfoBar.jsp" />
					<div class="sz-content">
						<h2 class="text-danger text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="card card-body">
							<jsp:include page="../common/messageDemo.jsp" />
							<form:form id="form1" action="saveTEERevaluation" method="post"
								modelAttribute="sr">
								<fieldset>

									<%-- <%@ include file="../messages.jsp"%> --%>
									<div>Please confirm Subjects selected and Amount to be
										paid before proceeding for Payment.</div>
									<br>

									<div class="col-md-18 column">

										<div class="form-group">
											<form:label class="fw-bold" path="serviceRequestType"
												for="serviceRequestType">Service Request Type:</form:label>
											<p>${sr.serviceRequestType } for ${mostRecentResultPeriod }
											Exams<p>
											<form:hidden path="serviceRequestType" />
										</div>

										<div class="form-group">
											<label class="fw-bold">Charges:</label> <p>INR. ${sr.amount }/-<p>
											<form:hidden path="amount" />
											<form:hidden path="revaluationSubjects" />
										</div>


										<div class="table-responsive">
											<table class="table table-striped" style="font-size: 12px">
												<thead>
													<tr>
														<th>Sr. No.</th>
														<th style="text-align: left;">Subject</th>
														<th>Sem</th>
														<th>Marks</th>
													</tr>
												</thead>
												<tbody>

													<c:forEach var="studentMarks" items="${revaluationList}"
														varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td nowrap="nowrap" style="text-align: left;"><c:out
																	value="${studentMarks.subject}" /></td>
															<td><c:out value="${studentMarks.sem}" /></td>
															<td><c:out value="${studentMarks.writenscore}" /></td>
														</tr>
													</c:forEach>

												</tbody>
											</table>
										</div>
										<!-- payment gateway option  -->
										<!-- <input type="hidden" id="paymentOption" name="paymentOption"
												value="paytm" /> -->

										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<a id="saveTEERevaluationAndPay" name="submit"
													class="btn btn-danger"
													formaction="saveTEERevaluation">Proceed to Payment</a>
												<button id="cancel" name="cancel" class="btn btn-dark"
													formaction="${pageContext.request.contextPath}/home"
													formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
									</div>
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- </section> -->

	<jsp:include page="../common/footerDemo.jsp" />

	<%-- <%@ include file="./paymentOption.jsp"%> --%>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/assets/js/serviceRequest/teeRevaluationConfirmation.js"></script>
</body>
</html>
