<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%
    ArrayList<ExamBookingTransactionBean> listOfSrFeeReceiptsBasedOnSapid = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("listOfSrFeeReceiptsBasedOnSapid");
    int srNumSR = 0;
    int numberOfSRReceipts = (listOfSrFeeReceiptsBasedOnSapid!=null && listOfSrFeeReceiptsBasedOnSapid.size()>0) ? listOfSrFeeReceiptsBasedOnSapid.size():0;
    %>
    <%if(numberOfSRReceipts >0){ %>
    	<div class="panel-heading" role="tab" id="">
			<h2>My SR Fee Receipts Generated</h2>
			<!---TOP TABS-->
	
			<div class="clearfix"></div>
		</div>
		
				<div class="table-responsive">
							<table class="table table-striped" style="font-size: 12px" id="myFeeReceiptTable">
								<thead>
									<tr>
										<th>Sr. No.</th>
										<th>SAPID</th>
										<th>SR TYPE</th>
										<th>YEAR</th>
										<th>MONTH</th>
										<th>DOWNLOAD FEE RECEIPT</th>
										
									</tr>
								</thead>
								<tbody>
								<%for(ExamBookingTransactionBean examBean : listOfSrFeeReceiptsBasedOnSapid) {
									srNumSR ++;
								%>
								<tr>
								<td><%=srNumSR %></td>
								<td><%=examBean.getSapid() %></td>
								<td><%=examBean.getServiceRequestType() %></td>
								<td><%=examBean.getYear() %></td>
								<td><%=examBean.getMonth() %></td>
								<td><a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('FEERECEIPTS_S3_PATH')" /><%=examBean.getFilePath()%>')" />Download</a></td>
								</tr>
								<%} %>
								</tbody>
							</table>
							</div>
							<%}%>