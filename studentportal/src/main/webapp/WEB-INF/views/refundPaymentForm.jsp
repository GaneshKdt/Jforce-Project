<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AdhocPaymentStudentPortalBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Initiate Refund For SR" name="title" />
</jsp:include>
<%
try {
%>
<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
	<div class="container-fluid customTheme">
		<div class="row">
			<legend>Check Transactions</legend>
		</div>
		<%@ include file="messages.jsp"%>
		<div class="row clearfix">
			<form:form action="queryRefundPayment" method="post"
				modelAttribute="adhocPaymentBean">
				<fieldset>
					<div class="col-md-6 column">

						<div class="form-group">
							<form:select id="feesType" path="feesType" required="required"
								class="form-control">
								<form:option value="">Select Payment Type</form:option>
								<form:options items="${refundPaymentTypeList}" />
							</form:select>
						</div>

						<div class="form-group">
							<label class="control-label" for="sapid">Enter SAP ID</label>
							<form:input id="sapid" path="sapId" type="text"
								placeholder="Enter SAP ID" class="form-control"
								required="required" />
						</div>

						<div class="form-group">
							<label class="control-label" for="merchantRefNo">Enter
								Merchant Number</label>
							<form:input id="merchantRefNo" path="merchantRefNo" type="text"
								placeholder="Merchant Number" class="form-control"
								required="required" />
						</div>

						<div class="form-group">
							<label class="control-label" for="refundAmount">Enter
								Amount to Refund</label>
							<form:input id="refundAmount" path="refundAmount" type="text"
								placeholder="Refund Amount" class="form-control"
								required="required" />
						</div>

						<div class="form-group">
							<label for="description">Description</label>
							<form:input path="description" id="description"
								required="required" class="form-control"
								placeholder="Description" />
						</div>

						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="queryRefundPayment">Get Transactions</button>
								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					</div>

				</fieldset>
			</form:form>

		</div>

		<%
		ArrayList<AdhocPaymentStudentPortalBean> transactionResponseList = (ArrayList<AdhocPaymentStudentPortalBean>) request
						.getAttribute("listOfFailedPayments");
				if (transactionResponseList != null
						&& transactionResponseList.size() > 0) {
		%>
		<div class="row">
			<div class="col-md-15 column">

				<div class="table-responsive">
					<table class="table table-striped" style="font-size: 12px">
						<thead>
							<tr>
								<th>Sr. No.</th>
								<th>SAPID</th>
								<th>Track ID</th>
								<th>Transaction Status Of ServiceRequest</th>


								<th>Transaction Type</th>

								<th>Amount</th>

								<th>Actions</th>

								<th>Refund to Student</th>

							</tr>
						</thead>
						<tbody>

							<%
							int count = 0;
															for (int i = 0; i < transactionResponseList.size(); i++) {
																count++;
																AdhocPaymentStudentPortalBean bean = transactionResponseList.get(i);
																String sapid = bean.getSapId();
																String trackId = bean.getTrackId();
																String transactionType = bean.getFeesType();
																String amount = bean.getRefundAmount();
																String tranStatus = bean.getTranStatus();
							%>

							<tr>
								<td><c:out value="<%=count%>" /></td>
								<td><c:out value="<%=sapid%>" /></td>
								<td><c:out value="<%=trackId%>" /></td>
								<td><c:out value="<%=tranStatus%>" /></td>
								<td><c:out value="<%=transactionType%>" /></td>
								<td><c:out value="<%=amount %>" /></td>
								<td><c:set var="sapid" scope="session" value="<%=sapid%>" />

									<c:url value="refundPayment" var="refundPayment">
										<c:param name="trackId" value="<%=trackId%>" />
										<c:param name="refundAmount" value="${refundAmount}" />
									</c:url>



									<button>
										<a href="${refundPayment}" title="Initiate Refund"
											onClick="return confirm('Are you sure you want to refund this amount?')">Refund
											To Student Account</a>

									</button></td>

								<td><c:url value="refundPayment"
										var="refundPaymentToWallet">
										<c:param name="trackId" value="<%=trackId%>" />
										<c:param name="refundAmount" value="${refundAmount}" />
										<c:param name="typeOfRefund" value="Wallet" />
									</c:url></td>
							</tr>
							<%
								}//End of for loop
							%>

						</tbody>
					</table>
				</div>

			</div>
		</div>

		<%
			}//End of If for size
		%>

	</div>

	</section>
	<%}catch(Exception e){
		e.printStackTrace();}%>

</body>
</html>