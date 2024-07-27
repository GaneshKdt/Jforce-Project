<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%
    ArrayList<ExamBookingTransactionBean> listOfHallTicketsBasedOnSapid = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("listOfHallTicketsBasedOnSapid");
    int srNumber = 0;
    int numberOfHallTickets = (listOfHallTicketsBasedOnSapid.size()>0 && listOfHallTicketsBasedOnSapid!=null) ? listOfHallTicketsBasedOnSapid.size():0;
    
    %>
    <%if(numberOfHallTickets > 0){ %>
    	<div class="panel-heading" role="tab" id="">
			<h2>My Hall Tickets Generated </h2>
			<!---TOP TABS-->
	
			<div class="clearfix"></div>
		</div>
		
				<div class="table-responsive">
							<table class="table table-striped" style="font-size: 12px" id="myFeeReceiptTable">
								<thead>
									<tr>
										<th>Sr. No.</th>
										<th>SAPID</th>
										<th>YEAR</th>
										<th>MONTH</th>
										<th>DOWNLOAD HALL TICKET</th>
										
									</tr>
								</thead>
								<tbody>
								<%for(ExamBookingTransactionBean tranBean : listOfHallTicketsBasedOnSapid) {
									srNumber ++;
								%>
								<tr>
								<td><%=srNumber %></td>
								<td><%=tranBean.getSapid() %></td>
								<td><%=tranBean.getYear() %></td>
								<td><%=tranBean.getMonth() %></td>
								<td><a href="/acads/downloadFile?filePath=<%=tranBean.getFilePath()%>">DOWNLOAD</a></td>
								</tr>
								<%} %>
								</tbody>
							</table>
							</div>
							<%}%>