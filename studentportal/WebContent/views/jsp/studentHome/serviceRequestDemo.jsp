<%@page import="com.nmims.beans.ServiceRequestStudentPortal"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>


<%
String collapseSRSection = "";
ArrayList<ServiceRequestStudentPortal> srList = (ArrayList<ServiceRequestStudentPortal>)session.getAttribute("newSrList");
int noOfSR = srList.size();

if(noOfSR > 0){
	collapseSRSection = "in";//Adding "in" class will expand SR gnment section. Expand only when SR are present
}
%>
<% if(noOfSR > 0){ %>
<div class=" col-md-6 mb-2"> 
<div class="service_request open">
	<div class="d-flex align-items-center text-wrap ">
		<span class="fw-bold me-3"><small class="fs-5">SERVICE REQUESTS</small></span>
		<div class="ms-auto text-nowrap">
			<a href="/studentportal/student/selectSRForm" class="text-dark me-1"><small >CREATE NEW</small></a>
			 <a type="button" data-bs-toggle="collapse" data-bs-target="#collapseSeven" class="text-muted"
				role="button" aria-expanded="true" aria-controls="collapseSeven"
				id="collapseCard"> <i class="fa-solid fa-square-minus"></i></a>
			</ul>
		</div>
		
	</div>
	
			<%-- <div class="table p-closed">
					<div class="no-data-wrapper no-border">
						<p class="no-data">
							<span class="icon-student-support"></span><%=noOfSR %>
							Service Requests
						</p>
					</div>

				</div> --%>
	
					<div id="collapseSeven" class="collapse">
						<div class="card card-body text-center text-dark">
						<h6 ><i class="fa-regular fa-life-ring"></i>
							<small class="text-dark"><%=noOfSR %> Service Requests</small></h6>
						</div>
					</div>
				<!--Service content -->
				<div id="collapseSeven" class="collapse show">
				<div class="table-responsive">
					<table class=" table ">
  						<thead>
    					<tr>
    						<th scope="col">SR NO</th>
     						<th scope="col">SR TYPE</th>
							<th scope="col" class="text-center">SR ID</th>
							<th scope="col" class="text-center">STATUS</th>
							<th scope="col" class="text-center">CREATED DATE</th>
							<th scope="col" class="text-center">CLOSED DATE</th>
						<th scope="col">EXPECTED CLOSED DATE</th>
					</tr>
					</thead>
					<tbody>
					<%
                    int srCount = 0;
                    for(ServiceRequestStudentPortal serviceRequest: srList){ 
                    	srCount++;
                    	
                    %>
					<tr>
						<td scope="row" class="text-center"><%=srCount %></td>
						<td> <%=serviceRequest.getServiceRequestType() %></td>
						<td class="text-center"><%=serviceRequest.getId() %></td>
						<td class="text-center"><%=serviceRequest.getRequestStatus() %></td>
						<td class="text-center"><%=serviceRequest.getCreatedDate() %></td>
						<td class="text-center"><%=serviceRequest.getRequestClosedDate()%></td>
						<td class="text-center"><%=serviceRequest.getExpectedClosedDate()%></td>
					</tr>

					<%} %>


					</tbody>
				</table>
				</div>
			
</div>

</div>
</div>
<%}%>