<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%
    ArrayList<ExamBookingTransactionBean> listOfAssignmentFeeReceiptsBasedOnSapid = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("listOfAssignmentFeeReceiptsBasedOnSapid");
    int srNumAsg = 0;
    int numberOfAssignmentPayments = (listOfAssignmentFeeReceiptsBasedOnSapid!=null && listOfAssignmentFeeReceiptsBasedOnSapid.size()>0) ? listOfAssignmentFeeReceiptsBasedOnSapid.size():0;
    %>
    <%if(numberOfAssignmentPayments >0){ %>
    	<div class="panel-heading" role="tab" id="">
			<h2>My Assignment Fee Receipts Generated</h2>
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
										<!-- <th>SUBJECT</th> -->
										<th>DOWNLOAD FEE RECEIPT</th>
										
									</tr>
								</thead>
								<tbody>
								<%for(ExamBookingTransactionBean examBean : listOfAssignmentFeeReceiptsBasedOnSapid) {
									srNumAsg ++;
								%>
								<tr>
								<td><%=srNumAsg %></td>
								<td><%=examBean.getSapid() %></td>
								<td><%=examBean.getYear() %></td>
								<td><%=examBean.getMonth() %></td>
								<%-- <td><%=examBean.getSubject() %></td> --%>
								<td><a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FEERECEIPTS_S3_PATH')" /><%=examBean.getFilePath()%>')" />Download</a></td>
 								
								</tr>
								<%} %>
								</tbody>
							</table>
							</div>
							<%}%>