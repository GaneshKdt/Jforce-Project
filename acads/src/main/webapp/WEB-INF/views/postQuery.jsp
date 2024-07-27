<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Post a Query" name="title" />
</jsp:include>

<body class="inside">

<style>

.ui-state-hover{
	background:#DED9DA;
}

</style>

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <c:if test="${action == 'postQueries' }">
        	<div class="row"><legend>Post a Query for ${session.subject }-${session.sessionName }</legend></div>
        </c:if>
        
        <c:if test="${action == 'viewQueries' }">
        	<div class="row"><legend>Queries for ${session.subject }-${session.sessionName }</legend></div>
        </c:if>
        <%@ include file="messages.jsp"%>
		
		
		<c:if test="${action == 'postQueries' }">
			<div class="panel-body">
			<form:form  action="postQuery" method="post" modelAttribute="sessionQuery">
				<fieldset>
				<div class="col-md-18 column">
					<h2>Post a Query</h2>
					<form:hidden path="sessionId" value="${session.id }"/>
					<div class="form-group">
							<form:textarea path="query" maxlength="500" class="form-control" placeholder="Please note that only Course Content and/or Session related queries have to be posted in Post Query section which will be replied within 48 working hours. Enter query here..." cols="50"/>
					</div>
	
					<div class=" form-group controls">
						<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="postQuery" onClick="return confirm('Are you sure you want to submit this query?');">Post Query</button>
					</div>
	
				</div>
				</fieldset>
			</form:form>
			</div>
			
			
			<div class="panel-body">
				<div class="col-md-18 column">
					<h2>Note:</h2>
					<p>Please note that only Course Content and/or Session related queries have to be posted in Post Query section which will be replied within 48 working hours. For all academic administrative queries and support 
					please approach the academic coordinators. Details of the same are as follows:</p>
					<div id="accordion3" class="table-responsive">
					<h3 style="font-weight:normal;color:#000">Contact Details of Academic Coordinators. Click Here</h3>
						<table class="table table-striped" style="font-size:12px">
							<thead>
								<tr>
									<th>University Regional Office</th>
									<th>Contact Person</th>
									<th>Email ID</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>Mumbai</td>
									<td>Academic Coordinator</td>
									<td>ac_mumbai@nmims.edu</td>
								</tr>
								
								<tr>
									<td>New Delhi</td>
									<td>Academic Coordinator</td>
									<td>ac_newdelhi@nmims.edu</td>
								</tr>
								
								<tr>
									<td>Kolkata</td>
									<td>Academic Coordinator</td>
									<td>ac_kolkata@nmims.edu</td>
								</tr>
								
								<tr>
									<td>Ahmedabad</td>
									<td>Academic Coordinator</td>
									<td>ac_ahmedabad@nmims.edu</td>
								</tr>
								
								<tr>
									<td>Pune</td>
									<td>Academic Coordinator</td>
									<td>ac_pune@nmims.edu</td>
								</tr>
								
								<tr>
									<td>Bangalore</td>
									<td>Academic Coordinator</td>
									<td>ac_banglore@nmims.edu</td>
								</tr>
								
								<tr>
									<td>Hyderabad</td>
									<td>Academic Coordinator</td>
									<td>ac_hyderabad@nmims.edu</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</c:if>
		<!-- My Queries section -->
		<c:if test="${not empty myQueries}">
			<div class="panel-body">
				<h2>My Queries</h2>
				<div id="accordion1">
				
				<c:forEach var="queryAnswer" items="${myQueries}" varStatus="status">
					<h3 style="font-weight:normal;color:#000">Q. ${queryAnswer.query}
					<c:if test="${queryAnswer.isAnswered == 'Y' }"><i class="fa fa-check-square"></i></c:if>
					<c:if test="${queryAnswer.isAnswered == 'N' }"><i class="fa fa-hourglass-start"></i></c:if>
					
					<span class="pull-right" style="font-size: 11px;">
					Posted on ${queryAnswer.createdDate}
					<c:if test="${queryAnswer.isAnswered == 'Y' }">, Answered on ${queryAnswer.lastModifiedDate}</c:if>
					</span>
					</h3>
					<div style="background:#fff">Answer: ${queryAnswer.answer}</div>
				</c:forEach>
				
				</div>
			</div>
		</c:if>
		
		
		<!-- Public Queries section -->
		<c:if test="${not empty publicQueries}">
			<div class="panel-body">
				<h2>Queries by other Students</h2>
				<div id="accordion2">
				
				<c:forEach var="queryAnswer" items="${publicQueries}" varStatus="status">
					<h3 style="font-weight:normal;color:#000">Q. ${queryAnswer.query}
					
					<span class="pull-right"  style="font-size: 11px;">
					Posted on ${queryAnswer.createdDate}
					<c:if test="${queryAnswer.isAnswered == 'Y' }">, Answered on ${queryAnswer.lastModifiedDate}</c:if>
					</span>
					
					</h3>
					<div style="background:#fff">Answer: ${queryAnswer.answer}</div>
				</c:forEach>
				
				</div>
			</div>
		</c:if>
		
		
	</div>
	
	</section>

	<jsp:include page="footer.jsp" />
	<script>
	  $(function() {
	    $( "#accordion1" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	  });
	  
	  $(function() {
	    $( "#accordion2" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	  });
	  
	  $(function() {
	    $( "#accordion3" ).accordion({
	      collapsible: true,
	      heightStyle: "content",
	      active:false
	    });
	  });
	  </script>
	  

</body>
</html>
 --%>
 
 
 
 <!DOCTYPE html>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html lang="en">
    

	
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Post a Query" name="title"/>
    </jsp:include>
    
    
    
    <body>
    
    	<%@ include file="common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<%@ include file="common/breadcrum.jsp" %>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="common/left-sidebar.jsp">
								<jsp:param value="Academic Calendar" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										<h2 class="red text-capitalize">
											<c:if test="${action == 'postQueries' }">
									        	Post a Query for ${session.subject }-${session.sessionName }
									        </c:if>
									        
									        <c:if test="${action == 'viewQueries' }">
									        	Queries for ${session.subject }-${session.sessionName }
									        </c:if>
										</h2>
										<div class="clearfix"></div>
										
										<%@ include file="common/messages.jsp" %>
										
										
										<c:if test="${action == 'postQueries' }">
											<div class="panel-content-wrapper">
											<form:form  action="postQuery" method="post" modelAttribute="sessionQuery">
												<fieldset>
													<h2 style="margin-top:0px;">Post a Query</h2>
													<form:hidden path="sessionId" value="${session.id }"/>
													<div class="clearfix"></div>
													<div class="form-group">
															<form:select path="queryType" class="form-control" required="required" id="queryType" onchange="askToSelectQueryType()">
															 	<form:option value="">Select Query Category</form:option>
															 	<form:option value="Academic">Academic - Faculty </form:option>
															 	<form:option value="Technical">Technical - Student Support</form:option>
															</form:select>
													</div>
													
													<div class="form-group">
															<form:textarea path="query" id="query" maxlength="500" class="form-control" onchange="askToEnterQuery()" placeholder="Please note that only Course Content and/or Session related queries have to be posted in Post Query section which will be replied within 48 working hours. Enter query here..." cols="50"/>
													</div>
													
													<div class=" form-group controls">
														<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="postQuery" onClick="return confirm('Are you sure you want to submit this query?');">Post Query</button>
													</div>
									
												</fieldset>
											</form:form>
											</div>
											
											
											<div class="panel-content-wrapper">
													<h2 style="margin-top:0px;">Note:</h2>
													<div class="clearfix"></div>
													<p>Please note that only Course Content and/or Session related queries have to be posted in Post Query section which will be replied within 48 working hours. For all academic administrative queries and support 
													please approach the academic coordinators. Details of the same are as follows:</p>
													<div id="accordion3" class="table-responsive">
													<h4>Contact Details of Academic Coordinators. Click Here <i class="fa fa-plus-square-o"></i></h4>
														<table class="table table-striped" style="font-size:12px">
															<thead>
																<tr>
																	<th>University Regional Office</th>
																	<th>Contact Person</th>
																	<th>Email ID</th>
																</tr>
															</thead>
															<tbody>
																<tr>
																	<td>Mumbai</td>
																	<td>Academic Coordinator</td>
																	<td>ac_mumbai@nmims.edu</td>
																</tr>
																
																<tr>
																	<td>New Delhi</td>
																	<td>Academic Coordinator</td>
																	<td>ac_newdelhi@nmims.edu</td>
																</tr>
																
																<tr>
																	<td>Kolkata</td>
																	<td>Academic Coordinator</td>
																	<td>ac_kolkata@nmims.edu</td>
																</tr>
																
																<tr>
																	<td>Ahmedabad</td>
																	<td>Academic Coordinator</td>
																	<td>ac_ahmedabad@nmims.edu</td>
																</tr>
																
																<tr>
																	<td>Pune</td>
																	<td>Academic Coordinator</td>
																	<td>ac_pune@nmims.edu</td>
																</tr>
																
																<tr>
																	<td>Bangalore</td>
																	<td>Academic Coordinator</td>
																	<td>ac_banglore@nmims.edu</td>
																</tr>
																
																<tr>
																	<td>Hyderabad</td>
																	<td>Academic Coordinator</td>
																	<td>ac_hyderabad@nmims.edu</td>
																</tr>
															</tbody>
														</table>
													</div>
											</div>
										</c:if>
              							
              							
              							<!-- My Queries section -->
										<c:if test="${not empty myQueries}">
											<div class="panel-content-wrapper">
												<h2 style="margin-top:0px;">My Queries</h2>
												<div class="clearfix"></div>
												<div id="accordion1">
												
												<c:forEach var="queryAnswer" items="${myQueries}" varStatus="status">
													<h4 style="color:#000">Q. <c:if test="${not empty queryAnswer.queryType }"> ${queryAnswer.queryType}</c:if>  ${queryAnswer.query}
													<c:if test="${queryAnswer.isAnswered == 'Y' }"><i class="fa fa-check-square"></i></c:if>
													<c:if test="${queryAnswer.isAnswered == 'N' }"><i class="fa fa-hourglass-start"></i></c:if>
													
													<span class="pull-right" style="font-size: 11px;">
													Posted on ${queryAnswer.createdDate}
													<c:if test="${queryAnswer.isAnswered == 'Y' }">, Answered on ${queryAnswer.lastModifiedDate}</c:if>
													</span>
													</h4>
													<div style="background:#fff">Answer: ${queryAnswer.answer}</div>
												</c:forEach>
												
												</div>
											</div>
										</c:if>
										
										
										<!-- Public Queries section -->
										<c:if test="${not empty publicQueries}">
											<div class="panel-content-wrapper">
												<h2 style="margin-top:0px;">Queries by other Students</h2>
												<div class="clearfix"></div>
												<div id="accordion2">
												
												<c:forEach var="queryAnswer" items="${publicQueries}" varStatus="status">
													<h4 style="font-weight:normal;color:#000">Q. ${queryAnswer.query}
													
													<span class="pull-right"  style="font-size: 11px;">
													Posted on ${queryAnswer.createdDate}
													<c:if test="${queryAnswer.isAnswered == 'Y' }">, Answered on ${queryAnswer.lastModifiedDate}</c:if>
													</span>
													
													</h4>
													<div style="background:#fff">Answer: ${queryAnswer.answer}</div>
												</c:forEach>
												
												</div>
											</div>
										</c:if>
              							
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="common/footer.jsp"/>
        
        <script>
        $(document).ready(function(){
        	 var select_id = document.getElementById("queryType");

    		 var response =  select_id.options[select_id.selectedIndex].value;
    		 
    		 if(response.trim() == ""){
    			 document.getElementById("query").disabled = true;
    			 document.getElementById("submit").disabled = true;
    		 }
        });
        
        function askToSelectQueryType()
        {
        	 var select_id = document.getElementById("queryType");

    		 var response =  select_id.options[select_id.selectedIndex].value;
    		 
    		 if(response.trim() == ""){
    			 alert("Please select Query Category");
    			 document.getElementById("query").disabled = true;
    		 }else{
    			 document.getElementById("query").disabled = false;
    		 }
        }
        
        function askToEnterQuery()
        {
        	var query =  document.getElementById("query").value;
        	 if(query.trim() != ""){
    			 document.getElementById("submit").disabled = false;
    		 }
        }
        
		  $(function() {
		    $( "#accordion1" ).accordion({
		      collapsible: true,
		      heightStyle: "content",
		      active:false
		    });
		  });
		  
		  $(function() {
		    $( "#accordion2" ).accordion({
		      collapsible: true,
		      heightStyle: "content",
		      active:false
		    });
		  });
		  
		  $(function() {
		    $( "#accordion3" ).accordion({
		      collapsible: true,
		      heightStyle: "content",
		      active:false
		    });
		  });
		  </script>
		
    </body>
</html>