<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
	<jsp:param value="Search Sessions Scheduled" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
	<section class="content-container login">
    	<div class="container-fluid customTheme">
        	<div class="row"><legend>Search Common Sessions</legend></div>
        	<%@ include file="messages.jsp"%>
		
			<form:form action="searchScheduledSession" method="post" modelAttribute="searchBean">
				<form:input type="hidden" path="isCommon" value="Y"/>
				<fieldset>
					<div class="panel-body">
						<div class="col-md-6 column">
						
							<div class="form-group">
								<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="true" 
									itemValue="${searchBean.year}">
									<form:option value="">Select Academic Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>
						
							<div class="form-group">
								<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="true" itemValue="${searchBean.month}">
									<form:option value="">Select Academic Month</form:option>
									<form:option value="Jan">Jan</form:option>
									<form:option value="Jul">Jul</form:option>
								</form:select>
							</div>
							
							<div class="form-group" style="overflow:visible;">
								<form:select id="subject" path="subject" class="combobox form-control"
									 itemValue="${searchBean.subject}" > 
									<form:option value="">Type OR Select Subject</form:option>
									<form:option value="Assignment">Assignment</form:option>
									<form:option value="project">Project</form:option>
									<form:option value="Module 4 - Project">Module 4 - Project</form:option>
									<form:option value="Orientation">Orientation</form:option>
									<form:option value="SASOrientation">SAS-Orientation</form:option>
								</form:select>
							</div>
							
							<div class="form-group">
								<form:input id="date" path="date" type="date" placeholder="Session Date" class="form-control" value="${searchBean.date}" />
							</div>
							
							<div class="form-group">
									<form:input id="facultyId" path="facultyId" type="text" placeholder="Faculty ID" class="form-control" value="${searchBean.facultyId}"/>
							</div>
							
							<div class="form-group" >
								<form:select id="facultyLocation" path="facultyLocation" type="text" placeholder="facultyLocation" class="form-control" itemValue="${searchBean.facultyLocation}" > 
									<form:option value="">Select Faculty Location</form:option>
									<form:options items="${locationList}" />
								</form:select>
							</div>
							
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<button name="submit" class="btn btn-large btn-primary" formaction="searchCommonSession">Search Session</button>
								<button name="submitAll" class="btn btn-large btn-primary" formaction="searchAttendanceFeedback">Search Attendance</button>
								<button name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
							
						</div>
					</div>
				</fieldset>
			</form:form>
			
			<c:choose>
				<c:when test="${rowCount > 0}">
					<legend>&nbsp;Scheduled Sessions<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadCommonSessions">Download to Excel</a> </font></legend>
					<div class="table-responsive">
						<table class="table table-striped" style="font-size:12px">
							<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Year</th>
								<th>Month</th>
								<th>Subject</th>
								<th>Session Name</th>
								<th>Sem</th>
								<th>Date</th>
								<th>Day</th>
								<th>Start Time</th>
								<th>Faculty ID</th>
								<th>Faculty Name</th>
								<th>Faculty1 Location</th>
								<th>Actions</th>
								<th>Video Details</th>
							</tr>
							</thead>
							<tbody>
								<c:forEach var="bean" items="${scheduledSessionList}" varStatus="status">
						        	<tr>
							            <td><c:out value="${status.count}" /></td>
							            <td><c:out value="${bean.year}" /></td>
							            <td><c:out value="${bean.month}" /></td>
							            <td nowrap="nowrap"><c:out value="${bean.subject}" /></td>
							            <td><c:out value="${bean.sessionName}" /></td>
							            <td><c:out value="${bean.sem}" /></td>
							            <td><c:out value="${bean.date}" /></td>
										<td><c:out value="${bean.day}" /></td>
										<td><c:out value="${bean.startTime}" /></td>
										<td><c:out value="${bean.facultyId}" /></td>
										<td>${mapOfFacultyIdAndFacultyRecord[bean.facultyId].fullName} </td>
										<td><c:out value="${bean.facultyLocation}" /></td>
										<td> 
											<c:url value="deleteCommonSession" var="deleteurl">
											  <c:param name="id" value="${bean.id}" />
											</c:url>
											
											 <a href="${deleteurl}" title="Delete" onclick="return confirm('Are you sure you want to delete this record?')">
											 	<i class="fa fa-trash-o fa-lg"></i>
											 </a> 
							            </td>
							            <td>
			            					<a href="/acads/admin/uploadSessionWiseVideoContentForm?id=${bean.id}" target="_blank">
			            						<b><i class="fa fa-upload"></i></b>
			            					</a>
			            				</td>
						            </tr>
			            		</c:forEach>
							</tbody>
						</table>
					</div>
				</c:when>
			</c:choose>
		</div>
	</section>
		
	<jsp:include page="footer.jsp" />
	
</body>
</html>