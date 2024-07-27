<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%
    ArrayList<ExamBookingTransactionBean> listOfProjectFeeReceiptsBasedOnSapid = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("listOfProjectFeeReceiptsBasedOnSapid");
    int srNumProject = 0;
    int numberOfProjectPayments = (listOfProjectFeeReceiptsBasedOnSapid!=null && listOfProjectFeeReceiptsBasedOnSapid.size()>0) ? listOfProjectFeeReceiptsBasedOnSapid.size():0;
    %>
    <%if(numberOfProjectPayments >0){ %>
    	<div class="panel-heading" role="tab" id="">
			<h2>My Project Fee Receipts Generated</h2>
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
										<th>DOWNLOAD FEE RECEIPT</th>
										
									</tr>
								</thead>
								<tbody>
								<%for(ExamBookingTransactionBean examBean : listOfProjectFeeReceiptsBasedOnSapid) {
									srNumProject ++;
								%>
								<tr>
								<td><%=srNumProject %></td>
								<td><%=examBean.getSapid() %></td>
								<td><%=examBean.getYear() %></td>
								<td><%=examBean.getMonth() %></td>
								<td><a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('PROJECT_FEERECEIPTS_S3_PATH')" /><%=examBean.getFilePath()%>')" />Download</a></td>
								</tr>
								<%} %>
								</tbody>
							</table>
							</div>
							<%}%>