<%@page import="com.nmims.beans.SessionDayTimeBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%

ArrayList<SessionDayTimeBean> myQuestions =  (ArrayList<SessionDayTimeBean>)session.getAttribute("myQuestions");
int noOfQuestions = myQuestions != null ? myQuestions.size() : 0;
String sessionSubject =(String)request.getAttribute("subject");
ArrayList<SessionDayTimeBean> answeredPublicQuestions =  (ArrayList<SessionDayTimeBean>)session.getAttribute("answeredPublicQuestions");
int noOfPublicQuestions = answeredPublicQuestions != null ? answeredPublicQuestions.size() : 0;

%>
<style>
	.fa-chevron-down{ 
		font-size: 15px !important;
	}
</style>  

	<c:url value="acads/student/courseQueryForm" var="courseQueryFormUrl">
		<c:param name="subject" value="<%= sessionSubject %>" />
	</c:url>
												
<div class="">
	<div class="panel panel-default">
		<div class="panel-heading"  id="">
		<div class="row">
			<div class="col-md-10" style="">
				<h2>Questions
				</h2>
				
			</div>
			<div class="col-md-2">
			<a href="/courseQueryFormUrl"  >
					<h3 class="green" >Ask Faculty </h3>
			</a>
			</div>
			 
			<div class="col-md-12" style="">
					<!-- Code for queries start -->
					<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
					  <div class="panel panel-default">
					    <div class="panel-heading">
					      <h4 class="panel-title">
					     	<%if(noOfQuestions == 0){ %>
							<a href="#">
								&nbsp;&nbsp; No Questions By You
							</a>
						<%}else{ %>
					        <a data-toggle="collapse" aria-expanded="false"  data-parent="#accordion" href="#collapse4">
								&nbsp;&nbsp;  My Questions &nbsp;&nbsp; (<b><%=noOfQuestions%></b>)  
							</a>
						<%} %>
						  </h4>
					    </div>
					    <div id="collapse4" class="panel-collapse collapse out">
					      <div class="panel-body">
								<div>
									<div  class=" table-responsive">
										<table class="table table-striped ">
											<thead>
												<tr>
													<th>SR.</th>
													<th>Date & Time of Question</th>
													<th>Question Details</th>
													
												</tr>
											</thead>
											<tbody>
												<%	int CountOfsession = 0;
													
													for(SessionDayTimeBean question :myQuestions){
														
														%>
														<tr data-toggle="modal" data-target="#myQuestion<%=CountOfsession%>">
														<td ><%=++CountOfsession %></td>
														<td><%=question.getCreatedDate() %></td>
														<td><%=question.getQuestion() %></td>
														
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
					    	<%if(noOfPublicQuestions == 0){ %>
							<a href="#">
								&nbsp;&nbsp; No Questions By Other Students On Same Subject
							</a>
						<%}else{ %>
					        <a data-toggle="collapse" data-parent="#accordion" href="#collapse3">
								&nbsp;&nbsp;  Questions By Other Students On Same Subject &nbsp;&nbsp; (<b><%=noOfPublicQuestions%></b>)  
							</a>
						<%} %>  
						</h4>
					    </div>
					    <div id="collapse3" class="panel-collapse collapse">
					      <div class="panel-body">
							<% for(SessionDayTimeBean ansQuestion :answeredPublicQuestions){ %>	
						  		<div class="well-sm">
						  			<p>
						  				<b>Q: </b> <%=ansQuestion.getQuestion() %>
						  				
						  			</p>
						  			<p>
						  				<b>A: </b> <%=ansQuestion.getAnswer() %>
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
	
<%	int sessionCounttemp = 0;
	
	for(SessionDayTimeBean question :myQuestions){
		
%>

<!-- My Question modals-->
<div id="myQuestion<%=sessionCounttemp %>" class="modal fade" role="dialog">
	<div class="modal-dialog"> 
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">MY Question</h4>
			</div>
			<div class="modal-body">
				<h5>MY Question</h5>
				<p><%=question.getQuestion() %></p>
				<p>&nbsp;</p>
				
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>

<%
sessionCounttemp++;
	}%>
	
	<script>
		$(document).ready(function(){
			console.log('Loaded questions.jsp');
			
		});
	</script>
	