
<!DOCTYPE html>

<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<jsp:include page="common/jscss.jsp">
	<jsp:param value="Online Pending Payment" name="title" />
</jsp:include>
<body>

	<div class="sz-main-content-wrapper">
		<%@ include file="common/breadcrum.jsp"%>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">



				<div class="sz-content-wrapper examsPage">

					<div class="sz-content">

						<h2 class="red text-capitalize">Online Pending Payment
							Summary</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="messages.jsp"%>
							<form:form action="adhocPaymentForm" method="post"
								modelAttribute="adhocPaymentBean">
								<fieldset>
									<div class="panel-body">
										<div class="col-md-12 column">

											<div class="form-group">
												<form:label path="paymentType" for="paymentType">Payment Type:</form:label>
												<p>Exam Registration</p>
											</div>

											<div class="form-group">
												<form:label path="paymentType" for="paymentType"> Description:</form:label>
												<p>${adhocPaymentBean.description}</p>
											</div>

											<div class="form-group">
												<p>
													Please quote Merchant Reference Number <b>${adhocPaymentBean.trackId}</b>
													for any future communications with Institute.
												</p>
											</div>

											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<!-- <button id="submit" name="submit"
													class="btn btn-large btn-primary" formaction="pendingPaymentForm">OK</button> -->
													<button id="cancel" name="cancel" class="btn btn-danger"
														formaction="logout" formnovalidate="formnovalidate">OK</button>
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
	<jsp:include page="common/footer.jsp" />
</body>
</html>

