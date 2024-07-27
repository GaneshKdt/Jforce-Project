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
</style>
<div class="course-queries-m-wrapper">
	<div class="panel-courses-page">
		
		<div class="panel-heading" role="tab" id="">
			<h2>Queries</h2>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<!-- <li> <a data-toggle="modal" data-target="#postNewQuery" class="new-post">NEW QUERY</a> </li> -->

				<li><a href="/${courseQueryFormUrl}" class="<%= queryLink %>"><h3 class="green">

				<%-- <li><a href="/${courseQueryFormUrl}"><h3 class="green">
>>>>>>> branch 'development' of https://ngasce@bitbucket.org/ngasce/studentportal.git
							Ask Faculty <span class="newFlag"> NEW! </span>
						</h3></a></li> --%>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseFive" aria-expanded="true"></a></li>
				<div class="clearfix"></div>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div class="clearfix"></div>

		<%-- 		<%if(noOfQueries == 0){ %>
			<div id="collapseFive" class="panel-collapse collapse academic-schedule courses-panel-collapse panel-content-wrapper" role="tabpanel">
		<%}else{ %> --%>
		<div id="collapseFive"
			class=" collapse in academic-schedule courses-panel-collapse panel-content-wrapper accordion-has-content"
			role="tabpanel">
			<%-- 		<%} %> --%>
			<%@ include file="../messages.jsp"%>
			<div class="panel panel-default">
				<div class="panel-heading">
					<h4 class="panel-title">
						<a class="accordion-toggle" data-toggle="collapse"
							data-parent="#accordion" href="#collapse1"> My Queries </a>
					</h4>
				</div>
				<div id="collapse1" class="panel-collapse collapse in">
					<div class="panel-body">
						<div class="panel-body" style="padding: 20px;">
							<%-- Commented by PS
				<a href="/acads/courseQueryForm?subject=<%=subject %>" title="New Query" class="btn btn-sm btn-primary"> Course related New Query </a>
				 --%>
							<%
								if (noOfQueries == 0) {
							%>
							<div class="no-data-wrapper">

								<h6 class="no-data nodata">
									<span class="fa-regular fa-calendar-days"></span>No queries raised
									by you
								</h6>
							</div>

							<%
								} else {
							%>

							<div class=" data-content panel-body">
								<div class="col-md-12 " style="padding-bottom: 20px;">

									<i class="icon-icon-view-submissions"></i> <span><%=noOfQueries%></span>
									queries raised by you

									<div class="clearfix"></div>
								</div>

								<div class="tbl-wrapper">
									<table id="example" class="table table-striped ">
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
											<tr data-toggle="modal"
												data-target="#myQuery<%=sessionCount%>">
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
								</div>

							</div>
							<%
								}
							%>


						</div>
					</div>
				</div>
			</div>

			<div class="panel panel-default">
				<div class="panel-heading">
					<h4 class="panel-title">
						<a class="accordion-toggle" data-toggle="collapse"
							data-parent="#accordion" href="#collapse2"> Public Queries </a>
					</h4>
				</div>
				<div id="collapse2" class="panel-collapse collapse in">
					<div class="panel-body">
						<div class="panel-body">
							<div class="panel-body" style="padding: 20px;">
								<%-- Commented by PS
				<a href="/acads/courseQueryForm?subject=<%=subject %>" title="New Query" class="btn btn-sm btn-primary"> Course related New Query </a>
				 --%>
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

												<tr data-toggle="modal"
													data-target="#publicQuery<%=sessionCount%>">
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
										<%-- <%if(noOfpublicQueries > 5){ %>
							<div class="load-more-table">
								<a>+<%=(noOfpublicQueries-5) %> More Queries <span
									class="icon-accordion-closed"></span></a>
							</div>
							<%} %> --%>
									</div>

								</div>
								<%
									}
								%>


							</div>
						</div>
					</div>
				</div>
			</div>










		</div>
	</div>
</div>


<%
	int sessionCount = 0;
	String queryStatusCss = "cou-red";
	for (SessionQueryAnswerStudentPortal query : myQueries) {
%>
<!-- My Query modals-->
<div id="myQuery<%=sessionCount%>" class="modal fade" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">MY QUERY</h4>
			</div>
			<div class="modal-body">
				<h5>MY QUERY</h5>
				<p><%=query.getQuery()%></p>
				<p>&nbsp;</p>

				<%
					if ("Answered".equals(query.getStatus())) {
				%>
				<h5 class="red">
					RESPONSE RECEIVED ON
					<%=query.getLastModifiedDate()%></h5>
				<%-- <p><strong>FROM:</strong> Prof. <%=query.getFacultyId() %></p> --%>
				<p><%=query.getAnswer()%></p>

				<%
					}
				%>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
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
<!-- My Query modals-->
<div id="publicQuery<%=sessionCount%>" class="modal fade" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">PUBLIC QUERY</h4>
			</div>
			<div class="modal-body">
				<h5>PUBLIC QUERY</h5>
				<p><%=query.getQuery()%></p>
				<p>&nbsp;</p>

				<%
					if ("Answered".equals(query.getStatus())) {
				%>
				<h5 class="red">
					RESPONSE RECEIVED ON
					<%=query.getLastModifiedDate()%></h5>
				<%-- <p><strong>FROM:</strong> Prof. <%=query.getFacultyId() %></p> --%>
				<p><%=query.getAnswer()%></p>

				<%
					}
				%>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
			</div>
		</div>
	</div>
</div>


<%
	sessionCount++;
	}
%>

<!-- Code for Ask Faculty Fab Button  :start-->

<div class="menu pmd-floating-action" role="navigation"
	style="z-index: 9;">
	<!--  <a href="javascript:void(0);" class="pmd-floating-action-btn btn btn-sm pmd-btn-fab pmd-btn-raised pmd-ripple-effect btn-default" data-title="Post">
                <span class="pmd-floating-hidden">Post</span>
                <i class="material-icons">edit</i>
            </a>
            <a href="javascript:void(0);" class="pmd-floating-action-btn btn btn-sm pmd-btn-fab pmd-btn-raised pmd-ripple-effect btn-default" data-title="Chat">
                <span class="pmd-floating-hidden">Chat</span>
                <i class="material-icons">chat</i>
            </a>
            <a href="javascript:void(0);" data-toggle="modal" data-target=".bd-example-modal-lg" class="pmd-floating-action-btn btn btn-sm pmd-btn-fab pmd-btn-raised pmd-ripple-effect btn-default" data-title="Audio Call">
                <span class="pmd-floating-hidden">Audio Call</span>
                <i class="material-icons">call</i>
            </a>
            <a href="javascript:void(0);" data-toggle="modal" data-target=".bd-example-modal-lg" class="pmd-floating-action-btn btn btn-sm pmd-btn-fab pmd-btn-raised pmd-ripple-effect btn-default" data-title="Video Call">
                <span class="pmd-floating-hidden">Video Call</span>
                <i class="material-icons">video_call</i>
            </a> -->
	<a href="javascript:void(0);" data-toggle="modal"
		data-target="#askFaculty"
		class="pmd-floating-action-btn btn pmd-btn-fab pmd-btn-raised pmd-ripple-effect"
		style="background-color: #d2232a; display: <%= hideSubmitQuery %>"
		onMouseOver="this.style.color='white';" data-title="Post a Query">
		<span class="pmd-floating-hidden">Primary</span> <i
		class="material-icons pmd-sm">add</i>
	</a>
</div>

<!-- Code for Ask Faculty Fab Button  :end-->

<!-- Code for Ask Faculty Fab Button Model :start-->

<div id="askFaculty" class="modal fade" role="dialog">
	<div class="modal-dialog">
		<!-- Modal content-->
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal">&times;</button>
				<h4 class="modal-title">Post A Query </h4>
			</div>
			<div class="modal-body">
				<div class="panel-content-wrapper">

					<form:form action="postCourseQuery" method="post"
						modelAttribute="sessionQuery">
						<fieldset>
							<!-- <h2 style="margin-top: 0px;">Post a Query</h2> -->
							<div class="clearfix"></div>

							<div class="form-group">
								<div class="row">
									<!-- 													<div class="col-xs-2"> -->
									<!-- 													<label for="faculty" s> Select Faculty : <span style="color: red"> * </span> </label> -->
									<!-- 													</div> -->
									<div class="col-xs-8">
										<form:select path="facultyId" id="facultyId"
											class="form-control" required="required">
											<form:option value=""> Select Faculty</form:option>
											<c:forEach var="faculty" items="${facultyForQuery}">

												<form:option value="${faculty.facultyId }"> Prof. ${faculty.firstName } ${faculty.lastName } </form:option>
											</c:forEach>
										</form:select>
									</div>
								</div>
							</div>

							<div class="clearfix"></div>
							<form:hidden path="subject" value="${subject}" />
							<form:hidden path="programSemSubjectId" value="${programSemSubjectId}" />
							<div class="form-group">
								<form:textarea path="query" id="query" maxlength="500"
									class="form-control" minlength="5" required="required"
									placeholder="Please post queries related to Course Content and/or Session only in this section.The response for the query will be provided within 48 working hours."
									cols="50" rows="3" />
							</div>

							<div class=" form-group controls">
								<button id="submit" name="submit" class="btn btn-sm"
									onmouseover="this.style.color='white';"
									formaction="postCourseQuery"
									onClick="return confirm('Are you sure you want to submit this query?');">Post
									Query</button>
							</div>

						</fieldset>
					</form:form>
				</div>
			</div>

			<!-- <div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
			</div> -->

		</div>
	</div>
</div>

<!--  Code for Ask Faculty FAb Button Model :end-->
<script>
	$(document).ready(function() {
		$('#example').DataTable();
	});
	$(document).ready(function() {
		$('#example2').DataTable();
	});
</script>
