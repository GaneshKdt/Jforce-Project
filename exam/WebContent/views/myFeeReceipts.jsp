<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
    <%
    ArrayList<ExamBookingTransactionBean> listOfFeeReceiptsBasedOnSapid = (ArrayList<ExamBookingTransactionBean>)request.getAttribute("listOfFeeReceiptsBasedOnSapid");
    int srNo = 0;
    int numberOfFeeReceipts = (listOfFeeReceiptsBasedOnSapid.size()>0 && listOfFeeReceiptsBasedOnSapid!=null) ? listOfFeeReceiptsBasedOnSapid.size():0;   
    %>
    <%if(numberOfFeeReceipts > 0){ %>
    	<div class="panel-heading" role="tab" id="">
			<h2>My Exam Fee Receipts Generated</h2>
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
								<%for(ExamBookingTransactionBean tranBean : listOfFeeReceiptsBasedOnSapid) {
									srNo ++;
								%>
								<tr>
								<td><%=srNo %></td>
								<td><%=tranBean.getSapid() %></td>
								<td><%=tranBean.getYear() %></td>
								<td><%=tranBean.getMonth() %></td>
								<%-- <td><a href="/acads/downloadFile?filePath=<%=tranBean.getFilePath()%>">DOWNLOAD</a></td> --%>
								<td><a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('EXAM_FEERECEIPT_S3_PATH')" /><%=tranBean.getFilePath()%>')" />Download</a></td>
								</tr>
								<%} %>
								</tbody>
							</table>
							</div>
							<%}%>