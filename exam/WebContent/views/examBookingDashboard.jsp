<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<%@page import="java.util.TreeMap"%>
<%@page import="com.nmims.beans.ExamBookingMetricsBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.math.BigDecimal"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Exam Bookings Dashboard" name="title" />
</jsp:include>
<meta http-equiv="refresh" content="300" >

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Exam Bookings Dashboard
			<%if("true".equals((String)request.getAttribute("resultsPage"))) {%>
			<font size="3">(Refreshed at: <%=new java.util.Date()%>)</font>
			<%} %>
			</legend></div>
			
			<%@ include file="messages.jsp"%>

			<%if(!"true".equals((String)request.getAttribute("resultsPage"))) {%>
				<div class="row clearfix">
				<form:form  action="/exam/admin/examBookingReport" method="get" modelAttribute="studentMarks">
				<fieldset>
				<div class="col-md-6 column">
	
						<div class="form-group">
							<form:select id="writtenYear" path="year" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.year}">
								<form:option value="">Select Exam Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
						
						<div class="form-group">
							<form:select id="writtenMonth" path="month" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.month}">
								<form:option value="">Select Exam Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>
						
						<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="/exam/admin/examBookingDashboard">View Dashboard</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
	
					</div>
					
					</fieldset>
					</form:form>
					
			</div>
		<%} %>
		<%
		Integer totalSubjectsBooked = (Integer)request.getAttribute("totalSubjectsBooked");
		Integer onlineSubjectsBooked = (Integer)request.getAttribute("onlineSubjectsBooked");
		Integer offlineSubjectsBooked = (Integer)request.getAttribute("offlineSubjectsBooked");
		Integer onlineStudentsBooked = (Integer)request.getAttribute("onlineStudentsBooked");
		Integer OfflineStundetsBooked = (Integer)request.getAttribute("OfflineStundetsBooked");
		Integer OnlineSubjectsPayments = (Integer)request.getAttribute("OnlineSubjectsPayments");
		Integer ddSubjectsPayments = (Integer)request.getAttribute("ddSubjectsPayments");
		Integer OnlineTransactions = (Integer)request.getAttribute("OnlineTransactions");
		Integer OnlineRefundTransactions = (Integer)request.getAttribute("OnlineRefundTransactions");
		Integer ddTransactions = (Integer)request.getAttribute("ddTransactions");
		Integer noOfDDApprovalPending = (Integer)request.getAttribute("noOfDDApprovalPending");
		Integer noOfDDsRejected = (Integer)request.getAttribute("noOfDDsRejected");
		Integer noOfDDsApprovedButNotBooked = (Integer)request.getAttribute("noOfDDsApprovedButNotBooked");
		Integer noOfSubjectsPendingToBeBoked = (Integer)request.getAttribute("noOfSubjectsPendingToBeBoked");
		Integer noOfStudentsPendingToBeBoked = (Integer)request.getAttribute("noOfStudentsPendingToBeBoked");
		
		Integer noOfFailedSubjectsPendingToBeBoked = (Integer)request.getAttribute("noOfFailedSubjectsPendingToBeBoked");
		Integer noOfFailedStudentsPendingToBeBoked = (Integer)request.getAttribute("noOfFailedStudentsPendingToBeBoked");
		
		TreeMap<String, ExamBookingMetricsBean> icLcMetricsMap = (TreeMap<String, ExamBookingMetricsBean>)request.getAttribute("icLcMetricsMap");
		
		Integer freeStudentsBooked = (Integer)request.getAttribute("freeStudentsBooked");
		Double onlineAmount = (Double)request.getAttribute("onlineAmount");
		Double refundAmount = (Double)request.getAttribute("refundAmount");
		Double bulkPayment = (Double)request.getAttribute("bulkPayment");
		Double conflictAmount = 0.0;
		Integer noOfActiveUsers = 0;
		Integer noOfConflictTransactions = 0;
		Double refundDueAmount = 0.0;
		Integer noOfPendingRefunds = 0;
		
		if(application.getAttribute("conflictAmount") != null){
			conflictAmount = (Double)application.getAttribute("conflictAmount");
		}
		if(application.getAttribute("noOfActiveUsers") != null){
			noOfActiveUsers = (Integer)application.getAttribute("noOfActiveUsers");
		}
		if(application.getAttribute("noOfConflictTransactions") != null){
			noOfConflictTransactions = (Integer)application.getAttribute("noOfConflictTransactions");
		}
		if(application.getAttribute("refundDueAmount") != null){
			refundDueAmount = (Double)application.getAttribute("refundDueAmount");
		}
		if(application.getAttribute("noOfPendingRefunds") != null){
			noOfPendingRefunds = (Integer)application.getAttribute("noOfPendingRefunds");
		}
		
	
		
		if(conflictAmount == null){
			conflictAmount = 0.0;
		}
		
		%>
		
		
		<%if("true".equals((String)request.getAttribute("resultsPage"))) {%>
		<div class="row">
			<div class="col-md-6 column">
				<span class="label label-success"># of Total Subjects Booked: <%=totalSubjectsBooked.intValue() %></span><br>
				<span class="label label-success"># of Subjects Booked for Online Exam: <%=onlineSubjectsBooked.intValue() %></span><br>
				<span class="label label-success"># of Subjects Booked for Offline Exam: <%=offlineSubjectsBooked.intValue() %></span><br>
				<span class="label label-success"># of Students who booked Online Exam: <%=onlineStudentsBooked.intValue() %></span><br>
				<span class="label label-success"># of Students who booked Offline Exam: <%=OfflineStundetsBooked.intValue() %></span><br>
				<span class="label label-success"># of Students who booked Free: <%=freeStudentsBooked.intValue() %></span><br>
				<%-- <span class="label label-success"># of Users Logged in: <%=noOfActiveUsers.intValue() %></span><br> --%>
				<span class="label label-success"># of Refunds Initiated/Completed: <%=OnlineRefundTransactions.intValue() %></span><br>
				<span class="label label-success">Amount Refunded: <%=refundAmount%></span><br>
			</div>
			<div class="col-md-6 column">
				<span class="label label-primary"># of subjects booked by Online Payment: <%=OnlineSubjectsPayments.intValue() %></span><br>
				<%-- <span class="label label-primary"># of subjects booked by DD: <%=ddSubjectsPayments.intValue() %></span><br> --%>
				<span class="label label-primary"># of Online Transactions: <%=(OnlineTransactions.intValue() + noOfConflictTransactions.intValue()) %></span><br>
				<%-- <span class="label label-primary"># of DD Approved & Booked Transactions: <%=ddTransactions.intValue() %></span><br> --%>
				<span class="label label-primary">Amount collected Online: <%=BigDecimal.valueOf((onlineAmount.doubleValue() + conflictAmount.doubleValue())).toPlainString()%></span><br>
				<span class="label label-primary">Bulk Payments: <%=bulkPayment%></span><br>
			</div>
			<div class="col-md-6 column">
				<%-- <span class="label label-danger"># of DD Approvals Pending: <%=noOfDDApprovalPending.intValue() %></span><br>
				<span class="label label-danger"># of DD Rejected: <%=noOfDDsRejected.intValue() %></span><br>
				<span class="label label-danger"># of DD Approved but Not Booked: <%=noOfDDsApprovedButNotBooked.intValue() %></span><br> --%>
				<span class="label label-danger"># of Current Drive Subjects Pending to be Booked: <%=noOfSubjectsPendingToBeBoked.intValue() %></span><br>
				<span class="label label-danger"># of Current Drive Students yet to start Booking: <%=noOfStudentsPendingToBeBoked.intValue() %></span><br>
				<span class="label label-danger"># of Failed Subjects Pending to be Booked: <%=noOfFailedSubjectsPendingToBeBoked.intValue() %></span><br>
				<span class="label label-danger"># of Failed Students yet to start Booking: <%=noOfFailedStudentsPendingToBeBoked.intValue() %></span><br>
				<span class="label label-danger"># of Conflict Transactions: <%=noOfConflictTransactions.intValue() %></span><br>
				<span class="label label-danger">Online Transaction Conflict Amount: <%=conflictAmount.doubleValue() %></span><br>
				<span class="label label-danger"># of Refunds Due: <%=noOfPendingRefunds.intValue() %></span><br>
				<span class="label label-danger">Refund Due Amount: <%=refundDueAmount.doubleValue() %></span><br>
			</div>
		</div>
		<br>
		
		<div>
		<a href="/exam/admin/cumulativeFinanceReportForm" class="btn btn-sm btn-primary" target="_blank">Datewise Exam Registration Revenue</a>&nbsp;
		<a href="/exam/admin/operationsRevenueForm" class="btn btn-sm btn-primary" target="_blank">View All Operations Revenue</a>
		</div>
		<br>
		
		<h2>IC/LC Wise Statistics</h2>
		<div class="panel-body table-responsive">
		<table class="table table-striped" style="font-size:12px">
							<thead>
							<tr>
								<th>Sr. No.</th>
								<th>LC</th>
								<th>IC</th>
								<th># Of Current Drive Pending Students</th>
								<th># Of Current Drive Pending Subjects</th>
								<th># Of Failed Pending Students</th>
								<th># Of FailedPending Students</th>
							</tr>
						</thead>
							<tbody>
							
							<%
							int count = 0;
							for (Map.Entry<String, ExamBookingMetricsBean> entry : icLcMetricsMap.entrySet()){
								ExamBookingMetricsBean bean = entry.getValue();
								count++;
							%>
							
							
							
							
						        <tr>
						            <td><%=count %></td>
						            <td><%=bean.getLc() %></td>
						            <td><%=bean.getCenterName() %></td>
						            <td><%=bean.getNoOfCurrentDrivePendingStudents() %></td>
						            <td><%=bean.getNoOfCurrentDrivePendingSubjects() %></td>
						            <td><%=bean.getNoOfFailedPendingStudents() %></td>
						            <td><%=bean.getNoOfFailedPendingSubejcts() %></td>
						            
						        </tr>   
						    <% }%>
								
								
							</tbody>
						</table>
		</div>
		
		
		
		
		<%} %>
		
	</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
