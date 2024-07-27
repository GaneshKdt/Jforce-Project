
<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%try{ %>

<%
	ArrayList<StudentStudentPortalBean> feeReceiptDetailer = (ArrayList<StudentStudentPortalBean>)request.getAttribute("listOfPaymentsMade");
	String sapid = (String)request.getAttribute("sapid");
	int numberOfFeeReceipts = feeReceiptDetailer.size();

	int srNo = 0;
%>

<html lang="en">


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="" name="title" />
</jsp:include>



<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Registration Fee Receipt"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Registration Fee Receipt" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">View Fee Receipt</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<div class="table-responsive panel-body">
								<%if(numberOfFeeReceipts>0){ %>
								<table class="table table-striped table-hover"
									style="font-size: 12px">
									<thead>
										<tr>
											<th>SR No</th>

											<th>Month</th>
											<th>Year</th>
											<th>Semester</th>
											<th>Download</th>
										</tr>
									</thead>
									<tbody>
										<%
										
										for(StudentStudentPortalBean bean : feeReceiptDetailer){ 
											srNo++;%>
										<tr>
											<th><%=srNo%></th>
											<th>&nbsp;&nbsp;<%=bean.getAcadMonth()%></th>
											<th><%=bean.getAcadYear()%></th>
											<th>Semester <%=bean.getSem()%></th>
											<th><a
												href="http://ngasce.force.com/StudentZoneFeeReceipt?StudentNo=<%=sapid%>&Sem=<%=bean.getSem()%>"
												target="_blank">&nbsp;&nbsp;&nbsp;<span
													class="icon-icon-download-file"></span></a></th>
										</tr>
										<%} %>

									</tbody>
								</table>
								<%} %>
							</div>
						</div>


					</div>
				</div>


			</div>
		</div>
	</div>

	<%}catch(Exception e){
  		e.printStackTrace();}%>

	<jsp:include page="../common/footer.jsp" />


</body>
</html>


