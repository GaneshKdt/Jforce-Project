<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Find Payments NOT Captured" name="title" />
</jsp:include>

<body class="inside">

<%try{ %>
	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Find Payments NOT Captured</legend></div>
			
				<%@ include file="messages.jsp"%>

				<%
					ArrayList<ExamBookingTransactionBean> unpaidTransactionsList = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("unpaidTransactionsList");
				ArrayList<ExamBookingTransactionBean> unRecordedTransactionsListInDB = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("unRecordedTransactionsListInDB");
				String missingTrans =(String)request.getAttribute("missingTrans");
				%>
								
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="/exam/admin/findOnlineTranConflict">
					<div class="row">
					<div class="col-md-6 column">
						<!--   -->

						<form:label for="fileData" path="fileData">Select HDFC Transaction file</form:label>
						<form:input path="fileData" type="file" required="required" />
					
					<br/>
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${fileBean.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${fileBean.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					
			</div>
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			PaymentID | Date Created | AccountID | PaymentMethod | Merchant Ref. No | Cusomer | Email | Txn Amount | Status<br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/HDFC_Transaction_Upload_Template.xlsx" target="_blank">Download a Sample Template</a>
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="/exam/admin/findOnlineTranConflict">Upload HDFC File</button>
						<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="/exam/admin/findOnlineTranConflict?findmissing=true">Find Missing Transaction</button>
				</div>

				
			</div>
			</form:form>
			
			<%
				if((unpaidTransactionsList != null && unpaidTransactionsList.size() > 0) || (unRecordedTransactionsListInDB !=null && unRecordedTransactionsListInDB.size()>0)) {
			%>
			
			<div class="row">
				<div class="col-md-18 column">
					<div class="table-responsive">
					<%if(!"true".equals(missingTrans)){ %>
						<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Student ID</th>
								<th>First Name</th>
								<th>Last Name</th>
								<th>Email</th>
								<th>Mobile</th>
								<th>Alt Phone</th>
								<th>Transaction Initiation Time</th>
								<th>Transaction Completion Time</th>
								<th>Time Difference</th>
								<th>Amount in Exam Portal</th>
								<th>Amount at HDFC</th>
							</tr>
						</thead>
						<tbody>
						
						<%
													DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
																
																for(int i = 0; i < unpaidTransactionsList.size(); i++){
																	ExamBookingTransactionBean bean = unpaidTransactionsList.get(i);
																	String sapId = bean.getSapid();
																	String firstName = bean.getFirstName();
																	String lastName = bean.getLastName();
																	String email = bean.getEmailId();
																	String mobile = bean.getMobile();
																	String altPhone = bean.getAltPhone();
																	String tranTime = bean.getTranDateTime();
																	String respTranTime = bean.getRespTranDateTime();
																	String amount = bean.getAmount();
																	String respAmount = bean.getRespAmount();
																	
																	Date startTime = df.parse(tranTime.substring(0, 19));
																	Date endTime = df.parse(respTranTime);
																	
																	long diff = endTime.getTime() - startTime.getTime();
																	long diffSeconds = diff / 1000;         
																	long diffMinutes = diff / (60 * 1000);  
																	diffSeconds = diffSeconds - (diffMinutes * 60);
																	String timeDiff = diffMinutes +":"+diffSeconds;
												%>
						
							<tr>
								<td><%=(i+1)%></td>
						    	<td><%=sapId %></td>
						    	<td><%=firstName %></td>
						    	<td><%=lastName %></td>
						    	<td><%=email %></td>
						    	<td><%=mobile %></td>
						    	<td><%=altPhone %></td>
						    	<td nowrap="nowrap"><%=tranTime %></td>
						    	<td nowrap="nowrap"><%=respTranTime %></td>
						    	<td><%=timeDiff%></td>
						    	<td><%=amount %></td>
						    	<td><%=respAmount %></td>
						    </tr>
						
						<%} %>
						
						</tbody>
					</table>
					<%}else if("true".equals(missingTrans)){ %>
					<a href="#" onclick="downloadToExcel();">Download to Excel</a>
						<table class="table table-striped" style="font-size:12px" id = "conflictTable">
						<thead>
							<tr>
								<th>Sr. No.</th>
								<th>First Name</th>
								<th>Email</th>
								<th>Track Id</th>
								<th>Transaction Completion Time</th>
								<th>Amount at HDFC</th>
							</tr>
						</thead>
						<tbody>
						
						<%
													
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
																for(int i = 0; i < unRecordedTransactionsListInDB.size(); i++){
																	ExamBookingTransactionBean bean = unRecordedTransactionsListInDB.get(i);
																	String firstName = bean.getFirstName();
																	String email = bean.getEmailId();
																	String respTranTime = bean.getRespTranDateTime();
																	String respAmount = bean.getRespAmount();
																	
												%>
						
							<tr>
								<td><%=(i+1)%></td>
						    	<td><%=firstName %></td>
						    	<td><%=email %></td>
						    	<td>'<%=bean.getMerchantRefNo() %>'</td>
						    	<td nowrap="nowrap"><%=respTranTime %></td>
						    	<td><%=respAmount %></td>
						    </tr>
						
						<%} %>
						
						</tbody>
					</table>
					<%} %>
					</div>
				</div>

				
			</div>
			<%} %>
		</div>
	</section>
<%}catch(Exception e){}%>
	<jsp:include page="footer.jsp" />

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.table2excel.js"></script>

<script>
function downloadToExcel(){
	$("#conflictTable").table2excel({
		exclude: ".noExl",
		name: "Excel Document Name"
	});
	
	
	
}

</script>

</body>
</html>
