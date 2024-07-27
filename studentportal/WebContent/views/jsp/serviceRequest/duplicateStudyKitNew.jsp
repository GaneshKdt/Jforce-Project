
<!DOCTYPE html>

<html lang="en">


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Enter Service Request Information" name="title" />
</jsp:include>

<%
	boolean isCertificate = (Boolean) session.getAttribute("isCertificate");
%>


<body>

	<%@ include file="../common/headerDemo.jsp"%>



	<div class="sz-main-content-wrapper">

		<%@ include file="../common/breadcrum.jsp"%>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<div id="sticky-sidebar">
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Service Request" name="activeMenu" />
					</jsp:include>
				</div>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h5 style="color: #d2232a; font-weight: bold;">${sr.serviceRequestType }</h5>
						<div class="clearfix"></div>
						<div class="card card-body">

							<%@ include file="../common/messageDemo.jsp"%>
							<p>Dear Student, You have chosen below Service Request.
								Please fill in required information below before proceeding for
								Payment.</p>

							<form:form id="form1" action="saveDuplicateStudyKit"
								method="post" modelAttribute="sr" enctype="multipart/form-data">
								<fieldset>

									<!-- payment gateway option  -->
<!-- 									<input type="hidden" id="paymentOption" name="paymentOption"
										value="paytm" />
 -->
									<div class="col-md-6 column">
										<div class="form-group">
											<form:label class = "fw-bold" path="serviceRequestType"
												for="serviceRequestType">Service Request Type:</form:label>
											<p>${sr.serviceRequestType }</p>
											<form:hidden path="serviceRequestType" />
										</div>

										<div class="form-group">
											<label class= "fw-bold">Charges:</label>
											<div id="showValue" style="color: red; size: 20px;"></div>
											<form:hidden path="amount" value="3000" />
										</div>
										</br>
										<div class="form-group">
											<select name="semester" required="required"
												class="form-select">
												<option value="">Select Semester for Study Kit</option>
												<option value="1">1</option>
												<option value="2">2</option>
												<option value="3">3</option>
												<option value="4">4</option>
											</select>
										</div>

										<div class="form-group">
											<div class="controls">
												<a id="submit" name="submit" class="btn btn-danger me-2"
													formaction="saveDuplicateStudyKit"> Save & Proceed to
													Payment </a> <a id="backToSR" name="BacktoNewServiceRequest"
													class="btn btn-secondary" formaction="selectSRForm"
													formnovalidate="formnovalidate"> Back to New Service
													Request </a>
											</div>
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


	<jsp:include page="../common/footerDemo.jsp" />
<%-- 	<%@ include file="./paymentOption.jsp"%> --%>


</body>
<script type="text/javascript">
	$(document)
			.ready(
					function() {
						function isEmptyData(data) {
							if (data.trim() == "" || data == null) {
								return true;
							}
							return false;
						}
						function ChangeFormAction(action) {
							$('#form1').attr('action', action);
							$('#form1').submit();
						}
						$("#backToSR").click(function() {
							ChangeFormAction('selectSRForm');
						});

						$('#submit')
								.click(
										function() {
											let sem = $(
													"select[name='semester']")
													.val();
											if (!isEmptyData(sem)) {
												if (!confirm('Are you sure you want to submit this Service Request?'))
													return false;
											/* 	var myModal = document.getElementById('paymentGatewayOptions');
												var modal = new bootstrap.Modal(myModal) ;
												modal.show(); */
											ChangeFormAction('saveDuplicateStudyKit');
												return false;
											}
											alert("please select semester");

										});

/* 						$(document)
								.on(
										'click',
										'.selectPaymentGateway',
										function() {
											$("#paymentOption")
													.attr(
															"value",
															$(this)
																	.attr(
																			'data-paymentGateway'));
										});
 */
						/* $(document).on('click','#selectPaytm',function(){
							$("#paymentOption").attr("value","paytm");
							ChangeFormAction('saveDuplicateStudyKit');
						});
						
						$(document).on('click','#selectHdfc',function(){
							$("#paymentOption").attr("value","hdfc");
							ChangeFormAction('saveDuplicateStudyKit');
						});
						
						$(document).on('click','#selectBilldesk',function(){
							$("#paymentOption").attr("value","billdesk");
							ChangeFormAction('saveDuplicateStudyKit');
						});
						
						$(document).on('click','#selectPayu',function(){
							$("#paymentOption").attr("value","payu");
							ChangeFormAction('saveDuplicateStudyKit');
						}); */
					});

	var isCertificate =
<%=isCertificate%>
	var totalCost = 3000;
	if (isCertificate == true) {
		totalCost = totalCost + parseFloat(0.18 * totalCost);
	}
	document.getElementById("amount").value = totalCost;
	document.getElementById("showValue").innerHTML = totalCost;
</script>
</html>