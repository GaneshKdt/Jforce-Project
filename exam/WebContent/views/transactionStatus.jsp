<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Check Transactions" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Check Transactions</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="row clearfix">
		<form  action="queryTransactionStatus" method="post" >
			<fieldset>
			<div class="col-md-6 column">

					
					<div class="form-group">
							<input id="sapid" name="sapid" type="text" placeholder="User ID" class="form-control" required="required"/>
					</div>


				<!-- Button (Double) -->
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="queryTransactionStatus">Get Transactions</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>

			
			
			</div>
			
			
			
			</fieldset>
		</form>
		
		</div>
		<%
			ArrayList<ExamBookingTransactionBean> transactionResponseList = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("transactionResponseList");
				if(transactionResponseList != null &&  transactionResponseList.size() > 0){
		%>
		<div class="row">
			<div class="col-md-15 column">
			
			<div class="table-responsive">
					<table class="table table-striped" style="font-size:12px">
					<thead>
						<tr> 
							<th>Sr. No.</th>
							<th>SAPID</th>
							<th>Track ID</th>
							<th>Transaction Status in Exam DB</th>
							<th>Error</th>
							<th>Error Code</th>
							<th>Transaction Type</th>
							<th>Status</th>
							<th>Amount</th>
							<th>Is Flagged</th>
							<th>Actions</th>
				
						</tr>
					</thead>
					<tbody>
					
				<%
										int count = 0;
											for(int i = 0; i < transactionResponseList.size(); i++) {
												count++;
												ExamBookingTransactionBean bean = transactionResponseList.get(i);
												String sapid = bean.getSapid();
												String trakId = bean.getTrackId();
												String error = bean.getError();
												String errorCode = bean.getErrorCode();
												String transactionType = bean.getTransactionType();
												String status = bean.getStatus();
												String amount = bean.getRespAmount();
												String isFlagged = bean.getIsFlagged();
												String tranStatus = bean.getTranStatus();
									%>
				
				<tr>
		           	<td><c:out value="<%=count %>"/></td>
		            <td><c:out value="<%=sapid %>"/></td>
		            <td><c:out value="<%=trakId %>"/></td>
		            <td><c:out value="<%=tranStatus %>"/></td>
		            <td nowrap= "nowrap"><c:out value="<%=error %>"/></td>
		            <td><c:out value="<%=errorCode %>"/></td>
		            <td><c:out value="<%=transactionType %>"/></td>
		            <td><c:out value="<%=status %>"/></td>
		            <td><c:out value="<%=amount %>"/></td>
		            <td><c:out value="<%=isFlagged %>"/></td>

		            <td> 
		            <c:url value="approveTransactionsForTrackId" var="approveTransactions">
					  <c:param name="trackId" value="<%=trakId %>" />
					</c:url>
					
					<%//if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){
						if("Payment Successfull".equalsIgnoreCase(status)){%>
					<a class="glyphicon glyphicon-info-sign" href="${approveTransactions}" title="Approve Transactions" onClick="return confirm('Are you sure you want to approve this transaction?')"></a>
					<%} %>
					
					</td>
		            
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
	
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
