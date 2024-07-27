<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add/Edit Session" name="title" />
</jsp:include>


<script language="JavaScript">
	function validateForm() {
		var mode = document.getElementById('mode').value;
		var capacity = document.getElementById('capacity').value;
		
		if(mode == 'Online'){
			if(capacity == ''){
				alert('Capacity can not be blank if Exam center is set up for Online mode');
				return false;
			}
		}
		return true;
	}
</script>

<body class="inside">

<%@ include file="header.jsp"%>
	<%
	boolean isEdit = "true".equals((String)request.getAttribute("edit"));
	
	%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Add/Edit Session</legend></div>
		<%@ include file="messages.jsp"%>
		<form:form  action="addScheduledSession" method="post" modelAttribute="session" >
			<fieldset>
			<div class="panel-body">
			
			
			<div class="col-md-6 column">
				<%if(isEdit){ %>
				<form:input type="hidden" path="id" value="${session.id}"/>
				<%} %>
				<!-- Form Name -->
				
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"  required="required" 
							itemValue="${session.year}" disabled="disabled" readonly="readonly"> 
							<form:option value="">Select Acad Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" 
							itemValue="${session.month}" disabled="disabled" readonly="readonly">
							<form:option value="">Select Acad Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
				
					<div class="form-group" style="overflow:visible;">
							<form:select id="subject" path="subject" class="combobox form-control" required="required" 
								 itemValue="${session.subject}" disabled="disabled" readonly="readonly"> 
								<form:option value="">Type OR Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="sessionName" path="sessionName" type="text" placeholder="Session Name" class="form-control"  
							value="${session.sessionName}" disabled="disabled"/>
					</div>
					
					<div class="form-group">
							<form:input id="facultyId" path="facultyId" type="text" placeholder="Faculty ID" class="form-control"  
							value="${session.facultyId}"/>
					</div>
					
					<div class="form-group">
						<form:input id="date" path="date" type="date" placeholder="Session Date" class="form-control" value="${session.date}" />
					</div>
					
					<div class="form-group">
						<form:input id="startTime" path="startTime" type="time" placeholder="Time" class="form-control" value="${session.startTime}" />
					</div>
					
					<div class="form-group">
					<label class="control-label" for="submit"></label>
						<%if(isEdit){ %>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateScheduledSession">Update Details</button>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateSessionName">Update Session Name</button>
						<%}else	{%>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addScheduledSession">Add Session</button>
						<%} %>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="verifySessionFeasibility">Verify Feasibility</button>
						
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
					
					
				</div>
				
				
				<div class="col-md-12 column">
				<legend>&nbsp;Other Scheduled Sessions for Same Subject<font size="2px">  </font></legend>
				<div class="table-responsive">
				<table class="table table-striped" style="font-size:12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Subject</th>
							<th>Session</th>
							<th>Date</th>
							<th>Day</th>
							<th>Start Time</th>
							<th>Faculty ID</th>
							<th>Faculty Name</th>
						</tr>
						</thead>
						<tbody>
						
						<c:forEach var="bean" items="${scheduledSessionList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}" /></td>
					            <td><c:out value="${bean.subject}" /></td>
					            <td><c:out value="${bean.sessionName}" /></td>
					            <td><c:out value="${bean.date}" /></td>
								<td><c:out value="${bean.day}" /></td>
								<td><c:out value="${bean.startTime}" /></td>
								<td><c:out value="${bean.facultyId}" /></td>
								<td><c:out value="${bean.firstName} ${bean.lastName}" /></td>
					            
					        </tr>   
					    </c:forEach>
							
						</tbody>
					</table>
				</div>
				
				
				</div>
				
				
				</div>
				
				
			</fieldset>
		</form:form>

		</div>
		
	
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
