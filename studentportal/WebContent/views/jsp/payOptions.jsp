<!DOCTYPE html>

<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page import="com.nmims.beans.WalletBean"%>

<%try{ %>
<jsp:include page="common/jscss.jsp">
	<jsp:param value="Payment Options" name="title" />
</jsp:include>
<body>
	<%@ include file="common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<%@ include file="common/breadcrum.jsp"%>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="common/left-sidebar.jsp">
					<jsp:param value="Wallet" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="common/studentInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Payment Options</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="common/messages.jsp"%>
							<div class="panel-body">
								<form id="payUsingForm" method="POST"
									action="makeWalletTransaction">

									<input type="hidden" value="${requestId}" name="requestId" />

									<div class="controls">


										<c:if test="${balanceAmountToBePaid > 0}">
											<button id="partialPay" name="submit"
												class="btn btn-large btn-primary"
												formaction="sendToGatewayForShortfallPayment"
												onClick="return confirm('Are you sure you want to pay using wallet and CREDIT/DEBIT Card ? ');">PAY
												BY NMWALLET + ${balanceAmountToBePaid}</button>
										</c:if>
										<button id="payByWallet" name="submit"
											class="btn btn-large btn-primary"
											formaction="makePaymentUsingNMWallet"
											onClick="return confirm('Are you sure you want to pay using your wallet ? ');">PAY
											USING NMWALLET</button>

										<button id="payUsingCard" name="payUsingCard"
											class="btn btn-large btn-primary"
											formaction="payUsingHDFCGateway"
											onClick="return confirm('Are you sure you want to proceed to Payment?');">Credit
											Card/Debit Card</button>

										<button id="cancel" name="cancel" class="btn btn-danger"
											formaction="cancelPayRequest" formnovalidate="formnovalidate"
											onClick="return confirm('Are you sure you want to cancel ?');">Cancel</button>


									</div>
									<ul>
										<li>Current Wallet Balance : ${walletRecord.balance}</li>
										<li>Transaction Amount : ${transactionAmount}</li>

										<c:if test="${walletRecord.balance >= transactionAmount}">
											<li>Wallet Balance After Transaction :
												${walletRecord.balance - transactionAmount}</li>
										</c:if>
										<c:if
											test="${walletRecord.balance < transactionAmount && walletRecord.balance > 0}">
											<li>Wallet Shortfall : ${balanceAmountToBePaid}</li>
										</c:if>
									</ul>


								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<%}catch(Exception e){}%>

	<jsp:include page="common/footer.jsp" />

</body>
<script>
 $(document).ready(function(){
    	
    	var walletBalance = ${walletRecord.balance}
    	console.log(walletBalance);
    	if(walletBalance == 0){
    		$("#payByWallet").attr('disabled',true);
    	}
    	
		var walletBalanceAfterTransaction = ${walletRecord.balance - transactionAmount}
    	console.log(walletBalanceAfterTransaction);
    	if(walletBalanceAfterTransaction < 0 && walletBalance!=0){
    		$("#payByWallet").css('display','none');
    	}
    
    	

    	
    	
    });
    
    </script>
</html>