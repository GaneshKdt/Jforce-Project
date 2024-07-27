<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@page import="com.nmims.beans.PageStudentPortal"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.ServiceRequestStudentPortal"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Initiate Refund For SR" name="title" />
</jsp:include>
<%try{ %>
<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Check Transactions</legend>
			</div>
			<%@ include file="messages.jsp"%>
			<div class="row clearfix">
				<form:form action="refundSR" method="post" modelAttribute="sr">
					<fieldset>
						<div class="col-md-6 column">

							<div class="form-group">
								<label class="control-label" for="sapid">Enter Merchant
									Number</label>
								<form:input id="trackId" path="merchantRefNo" type="text"
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


							<!-- Button (Double) -->
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<div class="controls">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary" formaction="refundSR">Get
										Transactions</button>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>



						</div>



					</fieldset>
				</form:form>

			</div>
			<%
			ArrayList<ServiceRequestStudentPortal> transactionResponseList = (ArrayList<ServiceRequestStudentPortal>)request.getAttribute("transactionResponseList");
				if(transactionResponseList != null &&  transactionResponseList.size() > 0){
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

								</tr>
							</thead>
							<tbody>

								<%
										int count = 0;
											for(int i = 0; i < transactionResponseList.size(); i++) {
												count++;
												ServiceRequestStudentPortal bean = transactionResponseList.get(i);
												String sapid = bean.getSapId();
												String trackId = bean.getTrackId();
												String transactionType = bean.getTransactionType();
												String amount = bean.getAmount();
												String tranStatus = bean.getTranStatus();
									%>

								<tr>
									<td><c:out value="<%=count %>" /></td>
									<td><c:out value="<%=sapid %>" /></td>
									<td><c:out value="<%=trackId %>" /></td>
									<td><c:out value="<%=tranStatus %>" /></td>
									<td><c:out value="<%=transactionType %>" /></td>
									<td><c:out value="<%=amount %>" /></td>
									<td><c:url value="refundSRAmount" var="refundSRAmount">
											<c:param name="trackId" value="<%=trackId %>" />
											<c:param name="refundAmount" value="${refundAmount}" />
										</c:url>


										<button>
											<a href="${refundSRAmount}" title="Initiate Refund"
												onClick="return confirm('Are you sure you want to refund this amount?')">Refund
												Amount</a>
										</button></td>

								</tr>
								<%}//End of for loop %>

							</tbody>
						</table>
					</div>

				</div>
			</div>

			<%
				
			}//End of If for size %>


		</div>

		}


	</section>
	<%}catch(Exception e){}
%>
	<jsp:include page="footer.jsp" />


</body>
</html>
