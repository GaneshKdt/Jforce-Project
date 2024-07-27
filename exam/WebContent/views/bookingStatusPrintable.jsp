<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Exam Booking Status" name="title" />
</jsp:include>



<body class="inside">


	<section class="content-container login">
		<div class="container-fluid customTheme">

			

			<%@ include file="messages.jsp"%>
			<%
				String ddPaid = (String)session.getAttribute("ddPaid");
					List<ExamBookingTransactionBean> examBookings = (List<ExamBookingTransactionBean>)session.getAttribute("examBookings");
					StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
			%>
				
			<div class="panel-body">
			
			<div class="col-sm-6">
			<legend>Exam Booking Status</legend>
				<div class="row">
						<b>Student Name : </b><%=student.getFirstName()%> <%=student.getLastName()%>
				</div>
				<div class="row">
						<b>SAP ID : </b><%=student.getSapid()%>
				</div>
				<div class="row">
						<b>Program : </b><%=student.getProgram()%>
				</div>
			</div>
	
			</div>
			<div class="panel-body">
			
			<div class="col-sm-12">
					<form:form  action="" method="post" modelAttribute="examCenter">
					<fieldset>
					<div class="table-responsive">
					<table class="table table-striped" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Subject</th>
								<th>Sem</th>
								<th>Transaction Status</th>
								<th>Booking Status</th>
								<th>Exam Center Booked</th>
							</tr>
						</thead>
						<tbody>
						
						
						<%
								Map<String, String> examCenterIdNameMap = (HashMap<String, String>)session.getAttribute("examCenterIdNameMap");
											int count = 1;
											for(int i = 0; i < examBookings.size(); i++){
												ExamBookingTransactionBean bean = (ExamBookingTransactionBean)examBookings.get(i);
												SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
												SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy");
												Date formattedDate = formatter.parse(bean.getExamDate());
												String formattedDateString = dateFormatter.format(formattedDate);
												String booked = bean.getBooked();
												String bookingStatus = null;
												if("Y".equals(booked)){
													bookingStatus = "Booked";
												}else{
													bookingStatus = "Not Booked";
												}
												String examCenterName = examCenterIdNameMap.get(bean.getCenterId());
												
												String subject = bean.getSubject();
							%>
					        <tr>
					            <td><%= count++%></td>
								<td><%= subject%></td>
								<td><%= bean.getSem()%></td>
								
								<%if("Project".equals(subject) || "Module 4 - Project".equals(subject)){ %>
									<td><%= bean.getTranStatus()%></td>
									<td><%= bookingStatus%></td>
									<td>NA</td>
								<%}else{ %>
									<td><%= bean.getTranStatus()%></td>
									<td><%= bookingStatus%></td>
									<td><%= examCenterName %></td>
								<%} %>
																
					        </tr> 
					        <%} 
					        %>  
						</tbody>
					</table>
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<button id="cancel" name="cancel" class="btn btn-primary" onClick="window.print()">Print</button>
							
							
						</div>
					</div>
					 
				</div>
				</fieldset>
				</form:form>
				</div>
				</div>
		
		</div>
	</section>

    
</body>
</html>
