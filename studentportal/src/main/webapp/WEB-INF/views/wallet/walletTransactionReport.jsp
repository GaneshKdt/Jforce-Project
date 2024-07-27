<!DOCTYPE html>
<html lang="en">



<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Generate Wallet Transaction Report" name="title" />
</jsp:include>



<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="StudentPortal;Generate Wallet Transaction Report"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Generate Wallet Transaction
							Report</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>
							<form:form action="downloadWalletTransactionReport" method="post"
								modelAttribute="wallet">
								<div class="col-md-4">

									<div class="form-group">
										<label for="startDate">Enter SAPID</label>
										<form:input id="sapid" path="sapid" type="text"
											placeholder="Enter Student Number" class="form-control" />
									</div>

									<div class="form-group">
										<label for="startDate">Enter Start Date</label>
										<form:input id="startDate" path="startDate" type="date"
											placeholder="Start Date" class="form-control" />
									</div>

									<div class="form-group">
										<label for="startDate">Enter End Date</label>
										<form:input id="endDate" path="endDate" type="date"
											placeholder="End Date" class="form-control" />
									</div>

									<div class="form-group">
										<form:select id="tranStatus" path="transactionType"
											class="form-control">
											<form:option value="">Select Transaction Type</form:option>
											<form:option value="CREDIT">CREDIT</form:option>
											<form:option value="DEBIT">DEBIT</form:option>
										</form:select>
									</div>

									<button class="btn btn-primary" type="submit"
										formaction="downloadWalletTransactionReport" id="submit">Generate</button>
								</div>

							</form:form>
						</div>
						<c:choose>
							<c:when test="${rowCount > 0}">
								<h2 style="margin-left: 50px;">
									&nbsp;&nbsp;Wallet Transcations<font size="2px">
										(${rowCount} Records Found)&nbsp;<a
										href="downloadWalletTransactionReport">Download to Excel</a>
									</font>
								</h2>
								<div class="clearfix"></div>
								<div class="panel-content-wrapper">
									<div class="table-responsive">
										<table class="table table-striped table-hover"
											style="font-size: 12px">
											<thead>
												<tr>
													<th>Sr. No.</th>
													<th>User Id</th>
													<th>Transaction Type</th>
													<th>Amount</th>
													<th>Wallet ID</th>
													<th>Transcation ID</th>
												</tr>
											</thead>
											<tbody>

												<c:forEach var="wallet" items="${listOFWalletTransactions}"
													varStatus="status">
													<tr>
														<td><c:out value="${wallet.count}" /></td>
														<td><c:out value="${wallet.userId}" /></td>
														<td><c:out value="${wallet.transactionType}" /></td>
														<td><c:out value="${wallet.amount}" /></td>
														<td><c:out value="${wallet.walletId}" /></td>
														<td><c:out value="${wallet.tid}" /></td>



													</tr>
												</c:forEach>

											</tbody>
										</table>
									</div>
								</div>
								<br>
							</c:when>
						</c:choose>

					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>
</html>