<!DOCTYPE html>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">




<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Dispatch Orders" name="title" />
</jsp:include>



<%try{ %>

<body>

	<%@ include file="../common/header.jsp"%>
	<%HashMap<String,String> dispatchMapper = (HashMap<String,String>)request.getAttribute("dispatchOrders");
					boolean showRecords = true;
					if(dispatchMapper.isEmpty()){
						showRecords = false;
					}
															
														
			%>


	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Dispatch Orders" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="DispatchOrders" name="activeMenu" />
				</jsp:include>
			</div>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">Dispatch Orders</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<div class="clearfix"></div>
							<%if(showRecords){ %>
							<div class="panel-content-wrapper">

								<div class="table-responsive">
									<table class="table table-striped table-hover"
										style="font-size: 12px">
										<thead>

											<th>Tracking Number</th>
											<th>Dispatch Status</th>
											<th>Delivery Date</th>
											<th>Semester</th>
											<th>Track</th>
										</thead>
										<tbody>

											<tr>
												<td><%=dispatchMapper.get("trackingNumber") %></td>
												<td><%=dispatchMapper.get("currentTrackDetails") %></td>
												<td><%=dispatchMapper.get("deliveryTime") %>
												<td><%=dispatchMapper.get("semester") %></td>
												<td><a href="<%=dispatchMapper.get("trackingLink") %>"
													target="_blank">TRACKING LINK</a></td>
											</tr>

										</tbody>

									</table>


								</div>
								<%} %>
							</div>




						</div>

					</div>
				</div>


			</div>
		</div>
	</div>
	<%}catch(Exception e){}%>


	<jsp:include page="../common/footer.jsp" />


</body>
</html>