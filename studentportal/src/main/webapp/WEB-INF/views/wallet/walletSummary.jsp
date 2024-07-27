<!DOCTYPE html>

<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nmims.beans.WalletBean"%>


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Wallet Transaction Summary" name="title" />
</jsp:include>
<body>
	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<%@ include file="../common/breadcrum.jsp"%>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Wallet" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Wallet Transaction Summary</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>


							<div class="panel-body">
								<form id="wallet" method="POST"
									action="/studentportal/myWalletForm">
									<fieldset>
										<div class="table-responsive">
											<table class="table table-bordered">
												<thead>
													<tr>
														<th>Description</th>
														<th>Amount</th>
														<th>Current Balance</th>
													</tr>
												</thead>
												<tbody>
													<tr>
														<td>${walletRecord.description}</td>
														<td>${walletRecord.amount}</td>
														<td>${walletRecord.balance}</td>
													</tr>
												</tbody>

											</table>
										</div>
										<input type="hidden" value="${requestId}" name="requestId" />
									</fieldset>
									<div class="form-group">
										<label class="control-label" for="submit"></label>
										<div class="controls">

											<button id="submit" name="submit"
												class="btn btn-large btn-primary">OK</button>

											<button id="cancel" name="cancel" class="btn btn-danger"
												formaction="home" formnovalidate="formnovalidate">Cancel</button>
										</div>
									</div>
								</form>
							</div>




						</div>
					</div>
				</div>
			</div>
		</div>
	</div>



	<jsp:include page="../common/footer.jsp" />

</body>
</html>