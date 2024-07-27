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
<style>
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

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>${sr.serviceRequestType }</legend>
			</div>
			<form:form id="form1" action="saveTEERevaluation" method="post"
				modelAttribute="sr">
				<fieldset>

					<%@ include file="../messages.jsp"%>
					<div class="panel-body">
						<div>Please confirm Subjects selected and Amount to be paid
							before proceeding for Payment.</div>
						<br>

						<div class="col-md-18 column">

							<div class="form-group">
								<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
								${sr.serviceRequestType } for ${mostRecentResultPeriod } Exams
								<form:hidden path="serviceRequestType" />
							</div>

							<div class="form-group">
								<label>Charges:</label> INR. ${sr.amount }/-
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
										class="btn btn-large btn-primary"
										formaction="saveTEERevaluation">Proceed to Payment</a>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>


						</div>




					</div>
				</fieldset>
			</form:form>
		</div>

	</section>

	<jsp:include page="../footer.jsp" />

	<%-- <%@ include file="./paymentOption.jsp"%> --%>
	<script>
		function ChangeFormAction(action){
			$('#form1').attr('action',action);
			$('#form1').submit();
		}
		$(document).ready(function(){
			$('#saveTEERevaluationAndPay').click(function(){
				if (!confirm('Are you sure you want to submit this Service Request?')) return false;
				/* $("#paymentGatewayOptions").modal(); */
				ChangeFormAction('saveTEERevaluation');
			});
			
			/* $(document).on('click','.selectPaymentGateway',function(){
				$("#paymentOption").attr("value",$(this).attr('data-paymentGateway'));
				
			}); */
		});
	</script>
</body>
</html>
