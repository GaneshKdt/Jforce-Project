<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Add Key Event" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row"><legend>&nbsp;Add Event</legend></div>
		<div class="panel-body">
		<%@ include file="messages.jsp"%>
		
    <%try{ %>
    		<div class="sz-content">
								
										<h2 class="red text-capitalize">
											<c:if test="${action == 'Add Event' }">
									        	Create a new Event
											</c:if>
									        
									        <c:if test="${action == 'Edit Event' }">
									        	Edit Event
									        </c:if>
										</h2>
										<div class="clearfix"></div>
											
											<div class="container-fluid row" style="color:black; padding-top:20px;">
											<div class=" col-md-5">
											<form:form  action="postEvent" method="post" modelAttribute="event">
												<fieldset> 
													<form:hidden path="id" value="${event.id }"/>
													<form:hidden path="createdBy" value="${event.createdBy}"/>
													<div class="clearfix"></div>
													<div class="form-group">
														<form:input path="eventName" value="${event.eventName}" required="required"  placeholder="Event Name" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
															<form:textarea path="description" id="description" maxlength="500" class="form-control" value='${event.description}' placeholder="Add description for the Event..." cols="50"/>
													</div>
													
													<div class="form-group">
														<label for="startDateTime">Event Start Date Time :&nbsp;&nbsp;</label>
														<form:input type="datetime-local" required="required" path="startDateTime" value="${event.startDateTime}" id="startDateTime" style="color:black;"/>
													</div>
													<div class="form-group">
														<label for="endDateTime">Event End Date Time :&nbsp;&nbsp;&nbsp;&nbsp;</label>
														<form:input type="datetime-local" required="required" path="endDateTime" value="${event.endDateTime}" id="endDateTime" style="color:black;"/>
													</div>
													
													<div class=" form-group controls">
														<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="postEvent" onClick="return confirm('Are you sure you want to add this Event ?');">${action}</button>
													</div>
									
												</fieldset>
											</form:form>
											</div>
											</div>
											
											<div class="container-fluid" style="text-align:center;">
												<c:if test="${not empty eventsList}">
												<h2 align="center" class="red text-capitalize" style="padding-top: 20px;"> Events : </h2>
												<table class="table table-striped table-hover" style="font-size:12px">
													<thead>
														<tr>
															<th>Sr No.</th>
															<th>Event</th>
															<th>Description</th>
															<th>Start Date Time</th>
															<th>End Date Time</th>
															<th>Created By</th>
															<th>Created </th>
															<th>Last Modified By</th>
															<th>Last Modified </th>
															<th colspan="2">Action </th>
														</tr>
													</thead>
												<tbody>
												<c:forEach var="eventL" items="${eventsList}" varStatus="status">
													<tr>
														<td>${status.count}</td>
														<td>${eventL.eventName }</td>
														<td>${eventL.description }</td>
														<td>${eventL.startDateTime }</td>
														<td>${eventL.endDateTime }</td>
														<td>${eventL.createdBy }</td>
														<td>${eventL.createdDateTime }</td>
														<td>${eventL.lastModifiedBy }</td>
														<td>${eventL.lastModifiedDateTime } </td>
														<td>
															<a href="/acads/admin/editEvent?id=${eventL.id }" title="Edit Event" class="btn btn-primary">
																Edit
															</a>
														</td>
														<td>
															<a href="/acads/admin/deleteEvent?id=${eventL.id }" title="Delete Event" class="btn btn-danger">
																Delete
															</a>
														</td>
													</tr>
												</c:forEach>
												</tbody>
												</table>
												</c:if>
											</div>
									</div>
              				</div>
              		</div>
         
        <%}catch(Exception e){
        	e.printStackTrace();
        } %>
	</section>

	<jsp:include page="footer.jsp" />

</body>
</html>
