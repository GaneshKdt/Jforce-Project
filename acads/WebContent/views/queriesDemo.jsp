<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="sun.util.calendar.BaseCalendar"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.SessionQueryAnswer"%>
<%@page import="org.apache.commons.lang3.StringUtils" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%

ArrayList<SessionQueryAnswer> myQueries =  (ArrayList<SessionQueryAnswer>)session.getAttribute("myQueries");
int noOfQueries = myQueries != null ? myQueries.size() : 0;
String subject =(String)request.getAttribute("subject");
int sessionId =(int)request.getAttribute("sessionId");
ArrayList<SessionQueryAnswer> answeredPublicQueriesForCourse =  (ArrayList<SessionQueryAnswer>)session.getAttribute("answeredPublicQueriesForCourse");
int noOfPublicQueries = answeredPublicQueriesForCourse != null ? answeredPublicQueriesForCourse.size() : 0;
BaseController queryCon = new BaseController();
String  disable="";
if(queryCon.checkLead(request, response)){
	disable= "disable";
}
 
%>
<c:url value="acads/student/courseQueryForm" var="courseQueryFormUrl">
 <c:param name="subject" value="<%= subject %>" />
</c:url>

<div class="bg-white rounded border border-1 mt-4">
		<div class="row">
		
		<div class="col-6 ">
				<h4 class="text-dark text-uppercase mt-3 ps-2">Queries</h4>
		</div>
		<div class="col-6  d-flex justify-content-end pt-2 pe-4">
				<a href="postQueryForm?id=${sessionId}&pssId=${programSemSubjectId }&action=postQueries" class="<%= disable %>" style ="display:<%= request.getAttribute("AskFaculty") %>" onclick="${isLeadStudent eq 1 ? 'preventLeadUser(event)' : ''}">
						<h4 class="text-success" >Post A Query</h4>
				</a>
				<a href="/${courseQueryFormUrl}&pssId=${programSemSubjectId }"  style ="display:<%= request.getAttribute("RedirectLink") %>" onclick="${isLeadStudent eq 1 ? 'preventLeadUser(event)' : ''}">
						<h4 class="text-success" >Posting Session Queries Are Disabled Please Raise Course Query From Here</h4>
				</a>
		  </div>
		</div>
				
		            <div id="accordion">
		   				 <div class="card rounded-0 border-start-0 border-end-0" id="myQuery"> 
		   				  <a data-bs-toggle="collapse" href="#collapseOne">    					 
		      				<div class="card-header bg-white pt-3 pb-3">        						 
		         			 <i class="fa-solid fa-chevron-down float-end"></i>
		         	          <h6 class="mt-0 mb-0 text-black text-uppercase"> My Queries </h6>
        			
			      			    </div>
			      			  </a>
			      			  <div id="collapseOne" class="collapse" data-bs-parent="#accordion">
					          <div class="card-body bg-white" >
					              <%
								if (noOfQueries == 0) {
							%>
							<div class="no-data-wrapper">

								<h6 class=" d-flex justify-content-center fs-4 text-muted ">
									<span class="fa-regular fa-calendar-days"></span>No queries raised
									by you
								</h6>
							</div>

							<%
								} else {
							%>
					          <div class="col-md-12 pb-2 text-muted mt-0">
                 				<i class="icon-icon-view-submissions"></i> <span ><%=noOfQueries%></span>
									queries raised by you
                    				<div class="clearfix"></div>
								</div>
								<table class="table table-striped ">
												
													<tr id="collapse1">
														<th>SR.</th>
														<th>Date & Time of Query</th>
														<th>Query Details</th>
														<th>Status</th>
													</tr>
												
												<tbody>
													<%	int sessionCount = 0;
														for(SessionQueryAnswer query :myQueries){%>
															<tr data-bs-toggle="modal" data-bs-target="#myQuery<%=sessionCount%>">
															<td ><%=++sessionCount %></td>
															<td><%=query.getCreatedDate() %></td>
															<td><%=query.getQuery() %></td>
															<td><%=query.getStatus() %></td>
														</tr>
													<%} %>
													
												</tbody>
											</table> 
											<%} %>
									</div>
								</div>
						</div>		
								
								<!-- public Queries Start-->
								<div class="card rounded-bottom border border-0" id="publicQuery">
						      		<a   data-bs-toggle="collapse"  href="#collapse2" >
						      			<div class="card-header rounded-bottom bg-white pt-3 pb-3 border border-0">			
						         				  <i class="fa-solid fa-chevron-down float-end"></i>
						         				 <h6 class="mt-0 mb-0 text-black text-uppercase">Public Queries </h6> 
						      			</div>
						             </a>
						        <div id="collapse2" class="collapse" data-bs-parent="#accordion">
					              <div class="card-body bg-light" >
                                    <%
									if (noOfPublicQueries == 0) {
							     	%>
							     	<div class="no-data-wrapper">

										<h6 class="d-flex justify-content-center fs-4 text-muted ">
											<span class="fa-regular fa-calendar-days"></span>No queries raised
										</h6>
								   </div>
								    <%
									} else {
								    %>
									    <div class="col-md-12 pb-2 text-muted" >
	 										<i class="icon-icon-view-submissions"></i> <span><%=noOfPublicQueries%></span>
											queries raised
											<div class="clearfix"></div>
										</div>
								          <div class="card card-body">
			                              	 <%int count=1; %>
												<% for(SessionQueryAnswer ansQuery :answeredPublicQueriesForCourse){ %>	
											  		<div class="mt-1 mb-1">
											  			<p class="mt-0 mb-0">
											  				<b>Q<%=count++ %>: </b> <%=ansQuery.getQuery() %>
											  				
											  			</p>
											  			<p class="mt-0 mb-0">
											  				<b>A: </b> <%=ansQuery.getAnswer() %>
											  			</p>
											  		</div>
												<% } %>
						  
							  			</div>
											
								   <%} %>
								   </div>
								   </div>
								   <div>
								<!-- Public Queries End -->
				  </div>
			      
		
			</div>
	
</div>		
</div>			 

<%	int sessionCountTemp = 0;
	for(SessionQueryAnswer query :myQueries){
%>

<!-- My Query modals-->
<div class="modal fade" id="myQuery<%=sessionCountTemp%>" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
	<div class="modal-dialog"> 
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
			 <h1 class="modal-title fs-5" id="myQuery<%=sessionCountTemp%>">MY QUERY</h1>
				<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
			</div>
			<div class="modal-body">
				<p>Q.<%=query.getQuery() %></p>
					
				<%if("Answered".equals(query.getStatus())){ %>
				
				<%if(!StringUtils.isBlank(query.getLastModifiedDate())){ %>
				 <p class="text-muted">RESPONSE RECEIVED ON <%=query.getLastModifiedDate() %></p>
                <%} %>
                
                <%if(!StringUtils.isBlank(query.getDateModified())) {%>
                   <p class="text-muted">RESPONSE RECEIVED ON <%=query.getDateModified() %></p>
                <%} %>
     			<p>A.<%=query.getAnswer() %></p>
					
				<%} %>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-danger" data-bs-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>

<%
sessionCountTemp++;
	}%>
	
	<script>
	   function preventLeadUser(e){
			e.preventDefault();
			alert("You do not have access to this feature");
		}
		
		$('#myQuery').click(function() {
			$(this).find('i').toggleClass('fa-solid fa-chevron-down  fa-solid  fa-chevron-up');
		});

		$('#publicQuery').click(function() {
			$(this).find('i').toggleClass('fa-solid fa-chevron-up fa-solid  fa-chevron-down ');
		}); 
	   
	</script> 