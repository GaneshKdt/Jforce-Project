<!DOCTYPE html>



<html lang="en">
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.ServiceRequestStudentPortal"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%
		ArrayList<ServiceRequestStudentPortal> marksheetDetailAndAmountToBePaidList = (ArrayList<ServiceRequestStudentPortal>)request.getSession().getAttribute("marksheetDetailAndAmountToBePaidList");
		int sizeOfMarksheetDetailAndAmountToBePaidList = marksheetDetailAndAmountToBePaidList.size();
	    String courierAmount = marksheetDetailAndAmountToBePaidList.get(0).getCourierAmount();
	%>



<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>


<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Home" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<div id="sticky-sidebar"> 
					<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu"/>
					</jsp:include>
				</div>  
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>
					<%try{ %>
					<div class="sz-content">

						<h2 class="red text-capitalize">Marksheet Summary</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">


							<%@ include file="../common/messages.jsp"%>
							<form id="summary" method="post" action="addSRForm">

								<div class="table-responsive">
									<table class="table table-bordered">
										<thead>
											<tr>
												<th>Sr No</th>
												<th>Marksheet Details</th>
												<th>Charges</th>
											</tr>
										</thead>

										<tbody>
											<%
										   for(int i=0;i<sizeOfMarksheetDetailAndAmountToBePaidList;i++){
											   ServiceRequestStudentPortal serviceBean = marksheetDetailAndAmountToBePaidList.get(i);
										   %>
											<tr>
												<td><%=i+1 %></td>
												<td><%=serviceBean.getDescriptionToBeShownInMarksheetSummary()%></td>
												<td><%=serviceBean.getAmountToBeDisplayedForMarksheetSummary()%>
											</tr>
											<%} %>
											<tr>
												<td></td>
												<td>Amount To Be Paid For Courier</td>
												<td><%=courierAmount %></td>
											</tr>
										</tbody>
									</table>
									<div class="form-group">
										<div class="controls">
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="confirmMarksheetRequest" class="form-control">Proceed</button>

												<button id="backToSR" name="BacktoNewServiceRequest"
													class="btn btn-danger" formaction="selectSRForm"
													formnovalidate="formnovalidate">Back to New
													Service Request</button>
											</div>
										</div>
									</div>
							</form>
						</div>

					</div>

				</div>
			</div>
			<%}catch(Exception e){}%>


		</div>
	</div>
	</div>


	<jsp:include page="../common/footer.jsp" />


</body>
</html>