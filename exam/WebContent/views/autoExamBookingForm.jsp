<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="java.util.HashMap"%>
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
	<jsp:param value="Auto Exam Booking" name="title" />
</jsp:include>

<body class="inside">

<%try{ %>
<%
   ArrayList<ExamBookingTransactionBean> successfulExamBookings = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("successfulExamBookings");
   //int count =successfulExamBookings.size();
   HashMap<String,StudentExamBean> mapOfSapIdAndStudent =(HashMap<String,StudentExamBean>)request.getAttribute("getAllStudents");
   
%>
	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Auto Exam Booking</legend></div>
			
				<%@ include file="messages.jsp"%>

				
								
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="/exam/admin/saveAutoExamBooking">
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
						formaction="/exam/admin/saveAutoExamBooking">Update Exam Bookings</button>
						
				</div>

				
			</div>
			</form:form>
			
			<c:if test="${rowCount >0 }">
			
			<div class="row">
				<div class="col-md-18 column">
					<div class="table-responsive">
					<a href="#" onclick="downloadToExcel();">Download to Excel</a>
						<table class="table table-striped" style="font-size:12px" id = "conflictTable">
						<thead>
							<tr>
								<th>Sr. No.</th>
								<th>First Name</th>
								<th>Email</th>
								<th>Track Id</th>
								<th>Booking Status</th>
								<!-- <th>Transaction Completion Time</th> -->
								<th>Amount at HDFC</th>
							</tr>
						</thead>
						<tbody>
						
						<%
						for(int i = 0; i < successfulExamBookings.size(); i++){
							ExamBookingTransactionBean bean = successfulExamBookings.get(i);
							StudentExamBean student =mapOfSapIdAndStudent.get(bean.getSapid());
							String firstName = student.getFirstName();
							String email = student.getEmailId();
							String respTranTime = bean.getRespTranDateTime();
							String amount = bean.getAmount();
							String booked =bean.getBooked();
																	
				         %>
						
							<tr>
								<td><%=(i+1)%></td>
						    	<td><%=firstName %></td>
						    	<td><%=email %></td>
						    	<td><%=bean.getTrackId() %></td>
						    	<td><%=bean.getBooked() %></td>
						    	<%-- <td nowrap="nowrap"><%=respTranTime %></td> --%>
						    	<td><%=amount %></td>
						    </tr>
						
						<%} %>
						</tbody>
					</table>
					
					</div>
				</div>

				
			</div>
			</c:if>
		</div>
	</section>
<%}catch(Exception e){}%>
	<jsp:include page="footer.jsp" />

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.table2excel.js"></script>

<script>
function downloadToExcel(){
	$("#conflictTable").table2excel({
		exclude: ".noExl",
		name: "Exam Booing Excel"
	});
	
	
	
}

</script>

</body>
</html>
