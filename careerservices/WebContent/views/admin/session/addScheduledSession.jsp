<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value="Add/Edit Session" name="title" />
</jsp:include>

<style>
.panel-title .glyphicon{
		font-size: 14px;
	}
</style>

<body class="inside">

<%@ include file="/views/adminCommon/header.jsp"%>
	<%
	boolean isEdit = "true".equals((String)request.getAttribute("edit"));
	%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row"> 
				<legend>Add / Edit Session</legend>
			</div>
			<%@ include file="/views/adminCommon/messages.jsp"%>
			
			<form:form action="addScheduledSession" method="post" modelAttribute="session">
				<fieldset>
					<div class="panel-body">
						<div class="col-md-4 column">
								
							<%if(isEdit){ %>
								<form:input type="hidden" path="id" id="id" value="${session.id}"/>
							<%} %>
								
							<div class="form-group" >
							</div>
						
						
							<div class="form-group" >
								<form:select id="facultyLocation" path="facultyLocation" type="text"	placeholder="facultyLocation" class="form-control" required="required" itemValue="${session.facultyLocation}" > 
									<form:option value="">Select Faculty Location</form:option>
									<form:options items="${locationList}" />
								</form:select>
							</div>
							
							<div class="form-group">
									<form:input id="sessionName" path="sessionName" type="text" placeholder="Session Name" class="form-control" value="${session.sessionName}" disabled="disabled"/>
							</div>
							
							<div class="form-group">
							
								<input type="text" list="facultyId" name="facultyId" placeholder="Faculty"/>
								<datalist id="facultyId" autocomplete="off">
									<c:forEach items="${FacultiesInCS}" var="facultyInCs">
										<option value="${ facultyInCs.facultyId }">${ facultyInCs.firstName } ${ facultyInCs.middleName } ${ facultyInCs.lastName } | ${ facultyInCs.facultyId }</option>
									</c:forEach>
								</datalist>
							</div>
							
							<div class="form-group">
								<form:input id="date" path="date" type="date" placeholder="Session Date" class="form-control" value="${session.date}" />
							</div>
							
							<div class="form-group">
								<form:input id="startTime" path="startTime" type="time" placeholder="Time" class="form-control" value="${session.startTime}" />
							</div>
							
							<div class="form-group">
								<form:textarea id ="description" path="description" class="form-control" rows="4" placeholder="Enter Description..." value="${session.description }"/>
							</div>
							
							
							<%if(!isEdit){ %>
								<form:select id="bypassAllChecks" path="bypassAllChecks" type="text" placeholder="Bypass All Checks" class="form-control" required="required"> 
									<form:option value="">Bypass All Checks</form:option>
									<form:option value="N">No</form:option>
									<form:option value="Y">Yes</form:option>
								</form:select>
							<%} %>
							
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<div class="form-group">
									
									<%if(isEdit){ %>
										<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateScheduledSession">Update Details</button>
									<%}else	{%>
										<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addScheduledSession">Add Session</button>
									<%} %>
									<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
						</div>
					</div>
				</fieldset>
			</form:form>
		</div>
	</section>
	<jsp:include page="/views/adminCommon/footer.jsp" />

	<script>
		$(document).ready(function () {
			$('facultyId').val("${session.facultyId}")
		});
	</script>
</body>
</html>