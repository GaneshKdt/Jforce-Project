<%@page import="com.nmims.beans.ServiceRequestStudentPortal"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>

<%try{ %>
<%
String collapseSRSection = "";
ArrayList<ServiceRequestStudentPortal> srList = (ArrayList<ServiceRequestStudentPortal>)session.getAttribute("srList");
HashMap<String,String> mapOfSRTypesAndTAT = (HashMap<String,String>)session.getAttribute("mapOfSRTypesAndTAT");
int noOfSR = srList.size();

if(noOfSR > 0){
	collapseSRSection = "in";//Adding "in" class will expand SR gnment section. Expand only when SR are present
}

%>

<div class="service_request open">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">SERVICE REQUESTS</h4>
			<ul class="topRightLinks list-inline">
				<li><a href="/studentportal/selectSRForm">CREATE NEW</a></li>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" data-parent="#accordion" href="#collapseSix"
					aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div id="collapseSix"
			class="panel-collapse collapse <%=collapseSRSection %> courses-panel-collapse"
			role="tabpanel">
			<div class="panel-body">
				<%if(noOfSR == 0){ %>
				<div class="no-data-wrapper">
					<p class="no-data">
						<span class="icon-student-support"></span>No Service Requests
					</p>
				</div>
				<%}else{ %>


				<!--Service content -->
				<div class="table p-closed">
					<div class="no-data-wrapper no-border">
						<p class="no-data">
							<span class="icon-student-support"></span><%=noOfSR %>
							Service Requests
						</p>
					</div>

				</div>


				<table class="table" cellpadding="10">
					<tbody>
					<thead>
						<th>SR TYPE</th>
						<th>SR ID</th>
						<th>STATUS</th>
						<th>CREATED DATE</th>
						<th>CLOSED DATE</th>
						<th>EXPECTED CLOSED DATE</th>

					</thead>
					<%
                    int srCount = 0;
                    for(ServiceRequestStudentPortal serviceRequest: srList){ 
                    	srCount++;
                    	String srStatusClass = "red";
                    	String requestedClosedDate ="";
                    	String expectedClosedDate ="";
                    	String CreatedDates ="";
                    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
                    	
                    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    	
                    	/* if(!"".equals(serviceRequest.getCreatedDate()))
                    	{
                    		Date createdDate =dateFormat.parse(serviceRequest.getCreatedDate());
                        	CreatedDates =sdf.format(createdDate);
                    	}
                    	
                    	if(!"".equals(serviceRequest.getRequestClosedDate()))
                    	{
                    		Date requestedClosed =dateFormat.parse(serviceRequest.getRequestClosedDate());
                            requestedClosedDate =sdf.format(requestedClosed);
                    	}
                    	 */
                    	Date d = dateFormat.parse(serviceRequest.getCreatedDate());
                    	Calendar c = Calendar.getInstance();
                    	c.setTime(d);
                    	String tat = mapOfSRTypesAndTAT.get(serviceRequest.getServiceRequestType());
                    	/* if(!"".equals(tat))
                    	{
                    		c.add(Calendar.DATE, Integer.parseInt(tat));
                            expectedClosedDate = sdf.format(c.getTime()); 
                    	} */
                        c.add(Calendar.DATE, Integer.parseInt(tat));
                        expectedClosedDate = dateFormat.format(c.getTime()); 
                    	 
                    	if(serviceRequest.getRequestClosedDate()==null){
                    		serviceRequest.setRequestClosedDate("");
                    	}
                    	if("Closed".equals(serviceRequest.getStatus())){
                    		srStatusClass = "green";
                    	}else if("In Progress".equals(serviceRequest.getStatus())){
                    		srStatusClass = "yellow";
                    	}
                    %>

					<tr>

						<td><%=srCount %>. <%=serviceRequest.getServiceRequestType() %></td>
						<td>Service Request ID <%=serviceRequest.getId() %></td>
						<td class="<%=srStatusClass %> text-right"><%=serviceRequest.getRequestStatus() %></td>
						<td class="text-right"><%=serviceRequest.getCreatedDate() %></td>
						<td class="text-right"><%=serviceRequest.getRequestClosedDate()%></td>
						<%if("Assignment Revaluation".equals(serviceRequest.getServiceRequestType()) || "Revaluation of Term End Exam Marks".equals(serviceRequest.getServiceRequestType()) || "Revaluation of Written Exam Answer Books".equals(serviceRequest.getServiceRequestType()) || "Photocopy of Written Exam Answer Books".equals(serviceRequest.getServiceRequestType())) {%>
						<!-- Commented temporarily  -->
						<td class="text-right">
							<%}else{ %>
						
						<td class="text-right"><%=expectedClosedDate %></td>
						<%} %>
					</tr>

					<%} %>


					</tbody>
				</table>

				<%} %>

				<%}catch(Exception e){
                	e.printStackTrace();}%>

			</div>
		</div>
	</div>
</div>