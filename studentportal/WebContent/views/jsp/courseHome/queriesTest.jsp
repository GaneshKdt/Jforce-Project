<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.SessionQueryAnswerStudentPortal"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%
ArrayList<SessionQueryAnswerStudentPortal> myQueries =  (ArrayList<SessionQueryAnswerStudentPortal>)session.getAttribute("myQueries");
int noOfQueries = myQueries != null ? myQueries.size() : 0;
String subject =(String)request.getAttribute("subject");
ArrayList<SessionQueryAnswerStudentPortal> publicQueries =  (ArrayList<SessionQueryAnswerStudentPortal>)session.getAttribute("publicQueries");
int noOfpublicQueries = publicQueries != null ? publicQueries.size() : 0;
BaseController queryCon = new BaseController();
String queryLink="";
String hideSubmitQuery = "";
if(queryCon.checkLead(request, response)){
	queryLink = "disabled";
	hideSubmitQuery = "none";
}
%>
 

<c:url value="acads/student/courseQueryForm" var="courseQueryFormUrl">
	<c:param name="subject" value="<%=subject%>" />
</c:url>
<style>
.nodata {
	vertical-align: middle;
	color: #a6a8ab;
	font: 1.00em "Open Sans";
	text-align: center;
	margin: 0;
}


.panel-heading .accordion-toggle:after {
	/* symbol for "opening" panels */
	font-family: 'Glyphicons Halflings';
	content: "\e114";
	float: right;
	color: grey;
}

.panel-heading .accordion-toggle.collapsed:after {
	content: "\e080";
}

.disabled {
	pointer-events: none;
	cursor: default;
	
	
}
.btn-circle.btn-xl {
    width: 60px;
    height: 60px;
    margin-left: 10px; 
    padding: 10px 16px;
    border-radius: 35px;
    font-size: 24px;
    line-height: 1.33;
    position: fixed;
    bottom: 38px;
    right: 20px;
    z-index: 999;
}

.modal-body ::-webkit-scrollbar{
     display: none;
  }

#fabbtn{
background-color : #bb0000;
}

@media only screen and (max-width: 768px) {
  .card {
    max-width: 100%;
    display: flex;
    flex-wrap: nowrap;
    overflow-y : scroll;
  }
}


.click.collapsed .icon { 
  transform: rotate(180deg);
  transition: .3s ease-in-out;
}

</style>

<div class="container-fluid bg-white rounded">
		<div class="row">
		<div class="col-12  fs-3">
	
			<h4 class="text-danger text-uppercase mt-3">Queries</h4>
		

			<ul class="topRightLinks list-inline">
				<li><a href="/${courseQueryFormUrl}" class="<%= queryLink %>"><h3 class="green"></h3></a></li> 
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseFive" aria-expanded="true"></a></li>
			</ul>
	   </div>
	</div>
	
			<%@ include file="../messages.jsp"%>			
			 <div id="accordion">
   				 <div class="card"> 
   				  <a data-bs-toggle="collapse" href="#collapseOne">    					 
      				<div class="card-header bg-white pt-3 pb-3">        						 
         			 <i class="fa-solid fa-chevron-down float-end text-dark"></i>
         	          <h6 class="mt-0 mb-0 text-black text-uppercase"> My Queries </h6>
        			
      			    </div>
      			    </a>
      			
      	
      			<div id="collapseOne" class="collapse" data-bs-parent="#accordion">
					<div class="card-body" style="background-color: white;" >
					
							
							<%
								if (noOfQueries == 0) {
							%>
							<div class="no-data-wrapper">

								<h6 class="no-data nodata fw-bold">
									<span class="fa-solid fa-circle-question"></span> No queries raised
									by you
								</h6>
							</div>

							<%
								} else {
							%>

							<!-- <div class=" data-content panel-body"> -->
								<div class="col-md-12 " style="padding-bottom: 20px;">

									<i class="icon-icon-view-submissions"></i> <span ><%=noOfQueries%></span>
									queries raised by you

									<div class="clearfix"></div>
								</div>

							<!-- 	<div class="tbl-wrapper"> -->
									<table id="example" class="table table-responsive table-striped ">
										<thead>
											<tr>
												<th>SI</th>
												<th>Query Type</th>
												<th style="min-width: 9rem;">Date & Time of Query</th>
												<th>Query Details</th>
												<th>Answered On</th>
												<th>Answer</th>
												<th>Status</th>

											</tr>
										</thead>
										<tbody>
											<%
												int sessionCount = 0;
													String queryStatusCss = "cou-red";
													for (SessionQueryAnswerStudentPortal query : myQueries) {
														if ("Answered".equals(query.getStatus())) {
															queryStatusCss = "cou-green";
														} else {
															queryStatusCss = "cou-red";
														}
														String answer = (query.getAnswer() != null) ? query.getAnswer() : "Not Answered";
											%>
											<tr data-bs-toggle="modal"
												data-bs-target="#myQuery<%=sessionCount%>">
												<td><%=++sessionCount%></td>
												<td><%=query.getQueryType()%></td>
												<td><%=query.getCreatedDate()%></td>
												<td><%=query.getQuery()%></td>
												<td>
													<%
														if (query.getIsAnswered() == "Y") {
													%> <%=query.getLastModifiedDate()%> <%
												 	} else {
												 %> --- <%
												 	}
												 %>
												</td>
												<td><%=answer%></td>
												<td class="<%=queryStatusCss%>"><%=query.getStatus()%></td>

											</tr>
											<%
												}
											%>

										</tbody>
									</table>
									<%
										if (noOfQueries > 5) {
									%>
									<div class="load-more-table">
										<a>+<%=(noOfQueries - 5)%> More Queries <span
											class="icon-accordion-closed"></span></a>
									</div>
									<%
										}
									%>
						
							<%
								}
							%>


						
					</div>
				</div>
		 </div>
<br>
      			
      		<div class="card">
      		<a   data-bs-toggle="collapse"  href="#collapse2">
      			<div class="card-header bg-white pt-3 pb-3">			
         				  <i class="fa-solid fa-chevron-down float-end text-dark"></i>
         				 <h6 class="mt-0 mb-0 text-black text-uppercase">Public Queries </h6> 
      			</div>
             </a>
			
				<div id="collapse2" class="collapse" data-bs-parent="#accordion">
					<div class="card-body" style="background-color: white;" >
				
								<%
									if (noOfpublicQueries == 0) {
								%>
								<div class="no-data-wrapper">

									<h6 class="no-data nodata">
										<span class="fa-regular fa-calendar-days"></span>No queries raised
									</h6>
								</div>

								<%
									} else {
								%>

								<div class=" data-content panel-body">
									<div class="col-md-12 " style="padding-bottom: 20px;">

										<i class="icon-icon-view-submissions"></i> <span><%=noOfpublicQueries%></span>
										queries raised

										<div class="clearfix"></div>
									</div>

									<div class="tbl-wrapper">
										<table id="example2" class="table table-striped ">
											<thead>
												<tr>
													<th>SI</th>
													<th>Query Type</th>
													<th style="min-width: 9rem;">Date & Time of Query</th>
													<th>Query Details</th>
													<th>Answered On</th>
													<th>Answer</th>													
												</tr>
											</thead>
											<tbody>
												<%
													int sessionCount = 0;
														String queryStatusCss = "cou-red";
														for (SessionQueryAnswerStudentPortal query : publicQueries) {
															if ("Answered".equals(query.getStatus())) {
																queryStatusCss = "cou-green";
															} else {
																queryStatusCss = "cou-red";
															}
															String answer = (query.getAnswer() != null) ? query.getAnswer() : "Not Answered";
												%>

												<tr data-bs-toggle="modal"
													data-bs-target="#publicQuery<%=sessionCount%>">
													<td><%=++sessionCount%></td>
													<td><%=query.getQueryType()%></td>
													<td><%=query.getCreatedDate()%></td>
													<td><%=query.getQuery()%></td>
													<td style="text-align:center;">
														<%
															if (query.getIsAnswered() == "Y") {
														%> <%=query.getLastModifiedDate()%> <%
												 	} else {
												 %> --- <%
												 	}
												 %>
													</td>
													<td><%=answer%></td>
												</tr>
												<%
													}
												%>

											</tbody>
										</table>
						
									</div>

								</div>
								<%
									}
								%>


							
						</div>
					</div>				
		        </div>
		   </div>
		  <br>
		 <br>
	</div>



<%
	int sessionCount = 0;
	String queryStatusCss = "cou-red";
	for (SessionQueryAnswerStudentPortal query : myQueries) {
%>
<!-- My Query PopUp opens only when we click on qurey -->

	<div class="modal fade" id="myQuery<%=sessionCount%>" tabindex="-1"  aria-labelledby="exampleModalLabel" aria-hidden="true">
	 <div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
			
			    <h1 class="modal-title fs-5"  id="exampleModalLabel">MY QUERY</h1>
				<button type="button" class="btn-close" data-bs-dismiss="modal"></button>
				
			</div>
			<div class="modal-body">
				<h6 class="text-bold">MY QUERY</h6>
				<p><%=query.getQuery()%></p>
				<p>&nbsp;</p>

				<%
					if ("Answered".equals(query.getStatus())) {
				%>
				<h6 class="text-bold">
					RESPONSE RECEIVED ON
					<%=query.getLastModifiedDate()%></h6>
				<%-- <p><strong>FROM:</strong> Prof. <%=query.getFacultyId() %></p> --%>
				<p><%=query.getAnswer()%></p>

				<%
					}
				%>
				<div class="form-group controls">
				<button type="button" class="btn btn-danger" data-bs-dismiss="modal">Done</button>
			   </div>
			   </div>
			    
				
			  
		   </div>
		</div>
	</div>



<%
	sessionCount++;
	}
%>

<%
	sessionCount = 0;
	queryStatusCss = "cou-red";
	for (SessionQueryAnswerStudentPortal query : publicQueries) {
%>


<!-- Public Query PopUp opens only when we click on qurey -->


<div class="modal fade" id="publicQuery<%=sessionCount%>" tabindex="-1"  aria-labelledby="exampleModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
			
			<h1 class="modal-title fs-5"  id="exampleModalLabel">PUBLIC QUERY</h1>
		<button type="button" class="btn-close m-0" data-bs-dismiss="modal" ></button>
				
			</div>
			<div class="modal-body">
				<h6 class="text-bold">PUBLIC QUERY</h6>
				<p><%=query.getQuery()%></p>
				<p>&nbsp;</p>

				<%
					if ("Answered".equals(query.getStatus())) {
				%>
				<h6 class="text-bold">
					RESPONSE RECEIVED ON
					<%=query.getLastModifiedDate()%></h6>
			     <%--  <p><strong>FROM:</strong> Prof. <%=query.getFacultyId() %></p>  --%>
				<p><%=query.getAnswer()%></p>				

				<%
					}
				%>
			<div class="form-group controls">
				<button type="button" class="btn btn-danger" data-bs-dismiss="modal">Done</button>
			</div>
			</div>
			    
				
			
		</div>
	</div>
</div>


<%
	sessionCount++;
	}
%>

<div class="position-absolute bottom-0 end-0">
	<button  id="fabbtn" type="button" href="javascript:void(0);" data-bs-toggle="modal"
		data-bs-target="#exampleModal" class="btn btn-primary btn-circle btn-xl border-0"
		  display: <%= hideSubmitQuery %>">
       <i class="fa-solid fa-plus"></i>
	</button>
</div>


  <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h1 class="modal-title fs-5" id="exampleModalLabel">Post A Query</h1>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>       
      </div>
      <div class="modal-body">
          <form:form action="postCourseQuery" method="post"
						modelAttribute="sessionQuery">
						<fieldset>
							<div class="form-group">
								<div class="row">								
									<div class="col-xs-8">
										<form:select path="facultyId" id="facultyId"
											class="form-select form-select-md mb-3" required="required">
											<form:option value=""> Select Faculty</form:option>
											<c:forEach var="faculty" items="${facultyForQuery}">
									<form:option value="${faculty.facultyId }"> Prof. ${faculty.firstName } ${faculty.lastName } </form:option>
											</c:forEach>
										</form:select>
									</div>
								</div>
							</div>
							<br>						
							<form:hidden path="subject" value="${subject}" />
							<form:hidden path="programSemSubjectId" value="${programSemSubjectId}" />
							<div class="form-group">
								<form:textarea path="query" id="query"  maxlength="500"
									class="form-control rounded" minlength="5" required="required"
									placeholder="Please post queries related to Course Content and/or Session only in this section.The response for the query will be provided within 48 working hours."
									cols="50" rows="3" />
							 </div>
							 <br>
							<div class="form-group controls">
								<button id="submit" name="submit" class="btn btn-danger";									
									formaction="postCourseQuery"
									onClick="return confirm('Are you sure you want to submit this query?');">Post Query</button>
							  </div>
						</fieldset>
					
					</form:form>
                </div>
    </div>
  </div>
</div>
 

<!--  Code for Ask Faculty FAb Button Model :end-->



<!-- 
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script> -->
<script>

/* $(document).ready(function(){
    $('#collapseOne').click(function () {
        $("i").toggleClass("fa-solid fa-chevron-down");
    });

    $('#collapse2').click(function () {
        $("i").toggleClass("fa-solid fa-chevron-up");
    });
}); */



 $('.card').click(function() {
	$(this).find('i').toggleClass('fa-solid fa-chevron-down  fa-solid fa-chevron-up');
}); 

	$(document).ready(function() {
		$('#example').DataTable();
	});
	$(document).ready(function() {
		$('#example2').DataTable();
	});
</script>
