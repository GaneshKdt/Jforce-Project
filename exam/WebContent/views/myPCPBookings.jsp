<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%
    ArrayList<ExamBookingTransactionBean> listOfPCPBookingsBasedOnSapid = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("listOfPCPBookingsBasedOnSapid");
    int srNumPCP = 0;
    int numberOfPCPBookings = (listOfPCPBookingsBasedOnSapid.size()>0 && listOfPCPBookingsBasedOnSapid!=null) ? listOfPCPBookingsBasedOnSapid.size():0;
    %>
    <%if(numberOfPCPBookings >0){ %>
    	<div class="panel-heading" role="tab" id="">
			<h2>My PCP Fee Receipts Generated</h2>
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
								<%for(ExamBookingTransactionBean examBean : listOfPCPBookingsBasedOnSapid) {
									srNumPCP ++;
								%>
								<tr>
								<td><%=srNumPCP %></td>
								<td><%=examBean.getSapid() %></td>
								<td><%=examBean.getYear() %></td>
								<td><%=examBean.getMonth() %></td>
								<%-- <td><a href="/acads/downloadFile?filePath=<%=examBean.getFilePath()%>">DOWNLOAD</a></td> --%>
								<td><a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('MARKSHEET_PCP_S3_PATH')" /><%=examBean.getFilePath()%>')" />Download</a></td>
								</tr>
								<%} %>
								</tbody>
							</table>
							</div>
							<%}%>