
 <!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>



<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Query TAt Report" name="title"/>
    </jsp:include>
    
    
    <%
    List<SessionQueryAnswer> unansweredQueries =(List<SessionQueryAnswer>)request.getAttribute("unansweredQueries");
    List<SessionQueryAnswer> answeredQueries =(List<SessionQueryAnswer>)request.getAttribute("answeredQueries");
    %>
    <body>
    <%try{ %>
    	<%@ include file="adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Query TAT Report" name="breadcrumItems"/>
			</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Query TAT report</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper" style="min-height:450px;">
											<%@ include file="adminCommon/messages.jsp" %>
									<form:form  action="queryTATReport" method="post" modelAttribute="searchBean">
											<fieldset>
											<div class="col-md-4 column">

													<div class="form-group">
														<form:select id="writtenYear" path="year"  required="required"	class="form-control"   itemValue="${searchBean.year}">
															<form:option value="">Select Academic Year</form:option>
															<form:options items="${yearList}" />
														</form:select>
													</div>
													
													<div class="form-group">
														<form:select id="writtenMonth" path="month"  required="required"  class="form-control"  itemValue="${searchBean.month}">
															<form:option value="">Select Academic Month</form:option>
															<form:option value="Jan">Jan</form:option>
															<form:option value="Jul">Jul</form:option>
														</form:select>
													</div>
													
												</div>
												
												
												
												<div class="col-md-4 column">

													<div class="form-group" style="overflow:visible;">
															<form:select id="subject" path="subject"  class="combobox form-control"  itemValue="${searchBean.subject}" > 
																<form:option value="">Type OR Select Subject</form:option>
																<form:options items="${subjectList}" />
															</form:select>
													</div>
													
													<div class="form-group">
														<form:select id="isAnswered" path="isAnswered"  class="form-control"  itemValue="${searchBean.isAnswered}">
															<form:option value="">Select Status</form:option>
															<form:option value="Y">Answered</form:option>
															<form:option value="N">Not Answered</form:option>
														</form:select>
													</div>
													
																							
										<div class="form-group"> 
												<button id="submit" name="submit" class="btn  btn-primary" formaction="queryTATReport">Generate</button>
												<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
										</div>

												</div>
										</fieldset>
								</form:form>
							</div> 
						
					<!-- Unanswered Queries section -->
		<div class="">
			<c:if test="${not empty unansweredQueries}">
			<div class="row">
			<div class="col-md-8">
			
			<a href="#unAnsweredDiv" data-toggle="collapse" >
			<h2>Unanswered Queries (${unansweredQueriesSize }) <i class="fa-solid fa-chevron-up" aria-hidden="true" style="color:grey !important;"></i></h2>    
			</a>
			</div>
			<div class="col-md-4">
				<a href="/acads/admin/UnAnsweredQueryReport" class="btn btn-large btn-primary" title="Download UnAnswered Query Report">Download Excel</a>
			</div>
			</div>
			<div id="unAnsweredDiv"  class="collapse in"> 
			<table class="table table-striped table-hover example" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>SapId</th>
								<th>Query</th>
								<th>Created Date</th>
								<th>Time Since Query Posted</th>
								<th>Batch Name</th>
								<th>Assigned Faculty Id</th>
							</tr>
						</thead>					
						
						<tbody>
						    
							<%-- <c:forEach var="queryAnswer" items="${unansweredQueries}" varStatus="status"> --%>
							<%
							int srCount = 0;
							for(SessionQueryAnswer queryAnswer : unansweredQueries){ %>
							<tr>
									<td> <%= ++srCount %> </td>
									<td><%=queryAnswer.getSapId() %></td>
									<td><%=queryAnswer.getQuery() %></td>
									<td><%=queryAnswer.getCreatedDate()%></td>
									<td>
									<%
										String createdDate= queryAnswer.getCreatedDate();
									 	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
									    Date d1 = new Date();
									    Date d2 = new Date();
									    String currentDate = format.format(d2);
									   	
									    d1 = format.parse(createdDate);
										d2 = format.parse(currentDate);

										//in milliseconds
										long diff = d2.getTime() - d1.getTime();

										long diffSeconds = diff / 1000 % 60;
										long diffMinutes = diff / (60 * 1000) % 60;
										long diffHours = diff / (60 * 60 * 1000) % 24;
										long diffDays = diff / (24 * 60 * 60 * 1000);

									    
									   %>
									<%=diffDays %> Days
									</td>
									<% if(queryAnswer.getBatchName() != null) {%>
									
									<td><%= queryAnswer.getBatchName() %></td>
									  
									<%}else{ %>
									
									<td></td>
									<% } %>
									<td><%=queryAnswer.getAssignedToFacultyId() %></td>
							</tr>
							<%} %>
							<%-- </c:forEach> --%>			
							
						</tbody>
					
					</table>
			</div>
			
		
			</c:if>
		</div>
		
		<!-- Answered Queries section -->
		<div class="">
			<c:if test="${not empty answeredQueries}">
				
			<div class="row">
			<div class="col-md-8">
			<a href="#answeredDiv" data-toggle="collapse" >
				<h2>
				Answered Queries (${answeredQueriesSize }) <i class="fa-solid fa-chevron-up" aria-hidden="true" style="color:grey !important;"></i>    
				</h2>
			</a>
			</div>
			<div>
				<a href="/acads/admin/AnsweredQueryReport" class="btn btn-large btn-primary" title="Download UnAnswered Query Report">Download Excel</a>
			</div>
			</div>
			<div id="answeredDiv">
						<table id="example" class="table table-striped table-hover example" style="font-size:12px; ">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>SapId</th>
								<th>Query</th>
								<th>Faculty Answer</th>
								<th>Has Attachment</th>
								<th>Created Date</th>
								<th>Answered On </th>
								<th>Time Taken To Answer Query</th>
								<th>Batch Name</th>
								<th>Answered Faculty's Id</th>
							</tr>
						</thead>					
						
						<tbody>
							<%
							int srCount = 0;
							for(SessionQueryAnswer queryAnswer : answeredQueries){ %>
							<tr>
									<td> <%= ++srCount %> </td>
									<td><%=queryAnswer.getSapId() %></td>
									<td><%=queryAnswer.getQuery() %></td>
									<td style="word-break: break-all" ><%=queryAnswer.getAnswer() %></td>
									<td><%=queryAnswer.getHasAttachment()%></td>
									<td><%=queryAnswer.getCreatedDate()%></td>
									<td><%=queryAnswer.getLastModifiedDate()%></td>
									<td>
									<%
										String createdDate= queryAnswer.getCreatedDate();
										String answeredDate= queryAnswer.getLastModifiedDate();
									 	DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
									    Date d1 = new Date();
									    Date d2 = new Date();
									    
									    d1 = format.parse(createdDate);
										d2 = format.parse(answeredDate);

										//in milliseconds
										long diff = d2.getTime() - d1.getTime();

										long diffSeconds = diff / 1000 % 60;
										long diffMinutes = diff / (60 * 1000) % 60;
										long diffHours = diff / (60 * 60 * 1000) % 24;
										long diffDays = diff / (24 * 60 * 60 * 1000);

									    
									   %>
									<%=diffDays %> Days
									</td>
									
									<% if(queryAnswer.getBatchName() != null) {%>
									
									<td><%= queryAnswer.getBatchName() %></td>
									
									<%}else{ %>
									
									<td></td>
									<% } %>
									
									<td><%=queryAnswer.getAssignedToFacultyId() %></td>
							</tr>
							<%} %>
	
						</tbody>
					</table>
				
			</div>		
			</c:if>
		</div>
		
		
						
						
						
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
        <jsp:include page="adminCommon/footer.jsp"/>
        <%}catch(Exception e){  }%>
        
       <script type="text/javascript">
       $(document).ready(function() {
    	    $('.example').DataTable( );
    	} );
    			
       </script>
		
    </body>
</html>