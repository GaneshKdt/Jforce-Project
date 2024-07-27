<!DOCTYPE html>

<html lang="en">
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.WalletBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="../common/jscss.jsp">
	<jsp:param value="My Wallet" name="title" />
</jsp:include>
<%
    WalletBean walletRecord = (WalletBean)request.getAttribute("wallet");
    
    %>


<body>


	<%@ include file="../common/header.jsp"%>


	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;My Wallet" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">

				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="My Wallet" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">

					<div class="sz-content">

						<h2 class="red text-capitalize">My Wallet</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../messages.jsp"%>
							<%try{ %>
							<div class="clearfix"></div>
							<%if(walletRecord!=null){ %>

							<fieldset>
								<div class="col-md-6 column">

									<div class="form-group">
										<label for="fullName">Full Name :-</label>
										<c:out value="${student.firstName} ${student.lastName}" />
									</div>



									<div class="form-group">
										<label for="balanceID">Current Wallet Balance :- </label>
										<c:out value="${wallet.balance}" />
									</div>

									<button type="button" class="btn btn-primary"
										data-toggle="collapse" data-target="#transactions">Get
										All Transactions</button>
									<div id="transactions" class="collapse">
										<c:choose>
											<c:when test="${rowCount > 0}">

												<div class="table-responsive">
													<table class="table courses-sessions">
														<thead>
															<tr>
																<th>SR.NO</th>
																<th>Transaction Date Time</th>
																<th>Amount</th>
																<th>Transaction Type</th>
																<th>Transaction Description</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach items="${walletTransactionList}"
																var="walletRecord" varStatus="status">
																<tr>
																	<td><c:out value="${status.count}" /></td>
																	<td><c:out value="${walletRecord.createdDate}" /></td>
																	<td><c:out value="${walletRecord.amount}" /></td>
																	<td><c:out value="${walletRecord.transactionType}" /></td>
																	<th><c:out value="${walletRecord.description}" /></th>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>

											</c:when>
											<c:otherwise>
												<h2>No Transactions Made</h2>
											</c:otherwise>
										</c:choose>
									</div>
									<div class="clearfix"></div>
									<div class="form-group">
										<label class="control-label" for="submit"></label>
										<div class="controls">
											<button id="backToHome" name="backToHome"
												class="btn btn-danger" formaction="/studentportal/home"
												formnovalidate="formnovalidate">Cancel</button>
										</div>
									</div>
									<div class="clearfix"></div>

								</div>
							</fieldset>

							<%} %>
						</div>

					</div>
				</div>


				<%}catch(Exception e){}%>

			</div>
		</div>
	</div>
	<jsp:include page="../common/footer.jsp" />


</body>

</html>