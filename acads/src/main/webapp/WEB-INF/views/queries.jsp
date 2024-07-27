<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="sun.util.calendar.BaseCalendar"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.SessionQueryAnswer"%>

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
<style>
	.fa-chevron-down{ 
		font-size: 15px !important;
	}
	
	.disable{
		pointer-events: none;
		color: gray;
		cursor: default;
	}
	
</style>  

	<c:url value="acads/student/courseQueryForm" var="courseQueryFormUrl">
		<c:param name="subject" value="<%= subject %>" />
	</c:url>
												
<div class="">
	<div class="panel panel-default">
		<div class="panel-heading"  id="">
		<div class="row">
			<div class="col-md-10" style="">
				<h2>Queries
				</h2>
				
			</div>
			<div class="col-md-2">
			<%-- <a href="/${courseQueryFormUrl}"  >
					<h3 class="green" >Ask Faculty </h3>
			</a> --%>
			<a href="postQueryForm?id=${sessionId}&action=postQueries" class="<%= disable %>" style ="display:<%= request.getAttribute("AskFaculty") %>" onclick="${isLeadStudent eq 1 ? 'preventLeadUser(event)' : ''}">
					<h3 class="green" >Ask Faculty </h3>
			</a>
			<a href="/${courseQueryFormUrl}"  style ="display:<%= request.getAttribute("RedirectLink") %>" onclick="${isLeadStudent eq 1 ? 'preventLeadUser(event)' : ''}">
					<h3 class="green" >Posting Session Queries are disabled Please raise Course Query From Here</h3>
			</a>
			</div>
			 
			<div class="col-md-12" style="">
					<!-- Code for queries start -->
					<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
					  <div class="panel panel-default">
					    <div class="panel-heading">
					      <h4 class="panel-title">
					     	<%if(noOfQueries == 0){ %>
							<a href="#">
								&nbsp;&nbsp; No Queries By You
							</a>
						<%}else{ %>
					        <a data-toggle="collapse" aria-expanded="false"  data-parent="#accordion" href="#collapse1">
								&nbsp;&nbsp;  My Queries &nbsp;&nbsp; (<b><%=noOfQueries%></b>)  
							</a>
						<%} %>
						  </h4>
					    </div>
					    <div id="collapse1" class="panel-collapse collapse out">
					      <div class="panel-body">
								<div>
									<div  class=" table-responsive">
										<table class="table table-striped ">
											<thead>
												<tr>
													<th>SR.</th>
													<th>Date & Time of Query</th>
													<th>Query Details</th>
													<th>Status</th>
												</tr>
											</thead>
											<tbody>
												<%	int sessionCount = 0;
													String queryStatusCss = "cou-red";
													for(SessionQueryAnswer query :myQueries){
														if(query.getStatus().equalsIgnoreCase("Answered")){
															queryStatusCss = "cou-green";
														}else{
															queryStatusCss = "cou-red";
														}
														
														%>
														<tr data-toggle="modal" data-target="#myQuery<%=sessionCount%>">
														<td ><%=++sessionCount %></td>
														<td><%=query.getCreatedDate() %></td>
														<td><%=query.getQuery() %></td>
														<td class="<%=queryStatusCss%>"><%=query.getStatus() %></td>
													</tr>
												<%} %>
												
											</tbody>
										</table> 
									</div>
									</div>					      
					      </div>
					  </div>
					  <div class="panel panel-default">
					    <div class="panel-heading">
					      <h4 class="panel-title">
					    	<%if(noOfPublicQueries == 0){ %>
							<a href="#">
								&nbsp;&nbsp; No Queries By Other Students On Same Subject
							</a>
						<%}else{ %>
					        <a data-toggle="collapse" data-parent="#accordion" href="#collapse2">
								&nbsp;&nbsp;  Queries By Other Students On Same Subject &nbsp;&nbsp; (<b><%=noOfPublicQueries%></b>)  
							</a>
						<%} %>  
						</h4>
					    </div>
					    <div id="collapse2" class="panel-collapse collapse">
					      <div class="panel-body">
							<% for(SessionQueryAnswer ansQuery :answeredPublicQueriesForCourse){ %>	
						  		<div class="well-sm">
						  			<p>
						  				<b>Q: </b> <%=ansQuery.getQuery() %>
						  				
						  			</p>
						  			<p>
						  				<b>A: </b> <%=ansQuery.getAnswer() %>
						  			</p>
						  		</div>
							<% } %>
						  </div>
					    </div>
					  </div> 
					</div>
					<!-- Code for queries end -->
				</div>
		</div>
		<div class="clearfix"></div>

				</div> 
				 
				
			</div> 
		</div>
	
<%	int sessionCountTemp = 0;
	String queryStatusCssTemp = "cou-red";
	for(SessionQueryAnswer query :myQueries){
		
%>

<!-- My Query modals-->
<div id="myQuery<%=sessionCountTemp %>" class="modal fade" role="dialog">
	<div class="modal-dialog"> 
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">MY QUERY</h4>
			</div>
			<div class="modal-body">
				<h5>MY QUERY</h5>
				<p><%=query.getQuery() %></p>
				<p>&nbsp;</p>
				
				<%if("Answered".equals(query.getStatus())){ %>
				<h5 class="red">RESPONSE RECEIVED ON <%=query.getDateModified() %></h5>
				<%-- <p><strong>FROM:</strong> Prof. <%=query.getFacultyId() %></p> --%>
				<p><%=query.getAnswer() %></p>
					
				<%} %>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
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
	   
		$(document).ready(function(){
			console.log('Loaded queries.jsp');
			
		});

	</script>
	