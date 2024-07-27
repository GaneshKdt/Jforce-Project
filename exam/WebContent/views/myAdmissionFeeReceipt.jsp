
<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%
	ArrayList<StudentExamBean> lstOfAdmissionPaymentReceipt = (ArrayList<StudentExamBean>) request.getAttribute("lstOfAdmissionPaymentReceipt");
	String sapid = (String) request.getAttribute("sapid");
	int numberOfAdmissionFeeReceipts = (lstOfAdmissionPaymentReceipt.size() > 0 && lstOfAdmissionPaymentReceipt != null) ? lstOfAdmissionPaymentReceipt
			.size() : 0;

	int srNoAdmissionFee = 0;
%>

<%
	if (numberOfAdmissionFeeReceipts > 0) {
%>
<div class="panel-heading" role="tab" id="">
	<h2>My Admission Fee Receipts Generated</h2>
	<!---TOP TABS-->

	<div class="clearfix"></div>
</div>

<div class="table-responsive">
	<table class="table table-striped" style="font-size: 12px"
		id="myAdmissionFeeReceiptTable">
		<thead>
			<tr>
				<th>SR No</th>

				<th>Month</th>
				<th>Year</th>
				<th>Semester</th>
				<th>Registered</th>
				<th>Download</th>
			</tr>
		</thead>
		<tbody>
			<%
				for (StudentExamBean bean : lstOfAdmissionPaymentReceipt) {
					srNoAdmissionFee++;
			%>
			<tr>
				<th><%=srNoAdmissionFee%></th>
				<th>&nbsp;&nbsp;<%=bean.getAcadMonth()%></th>
				<th><%=bean.getAcadYear()%></th>
				<th>Semester <%=bean.getSem()%></th>
				<th><%=bean.getRegistered()%></th>
				<th><a
					href="http://ngasce.force.com/StudentZoneFeeReceipt?StudentNo=<%=bean.getSapid()%>&Sem=<%=bean.getSem()%>"
					target="_blank">&nbsp;&nbsp;&nbsp;<span
						class="icon-icon-download-file"></span></a></th>
			</tr>
			<%
				}
			%>

		</tbody>
	</table>
</div>
<%
	}
%>