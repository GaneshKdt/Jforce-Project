<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Auto Schedule Sessions" name="title" />
</jsp:include>
<link rel="stylesheet" href="https://cdn.datatables.net/1.10.22/css/jquery.dataTables.min.css" />
<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">
		<div class="row"><div class="container-fluid customTheme"></div>
		<div class="container-fluid">
		<%@ include file="messages.jsp"%>
		</div>
		<br>
		<div class="panel-body">
			<div class="col-md-9 column">
				<legend>&nbsp;Sessions pending to be scheduled : ${pendingRecordsCount}. </legend>
			</div>
			<div class="col-md-9 column">
				<form role="form" id="passFailForm" action="processPassFail" method="post">
					<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="autoSchedule">Schedule Sessions</button>
					<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
				</form>
			</div>
		
		</div>
		

		

		<br>
		<br>
		
		
		<form:form method="post" action="bookTrainingSessionsNew" modelAttribute="sessionDayTimeAcadsBean">	
		<div class="panel-body">
			<div class="col-md-9 column">
				<legend><font>&nbsp;Sessions yet to be created in Zoom : ${pendingConferenceCount} </font></legend>
			</div>
			<div class="col-md-9 column">
				<%-- <form role="form" id="passFailForm" action="bookTrainingSessions" method="post">
					<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="bookTrainingSessions">Create WebEx Trainings</button>
					<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
				</form> --%>	
 					<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="bookTrainingSessionsNew">Create Webinar Links</button>
			</div>
		
		</div>
		
		
		<c:choose>
		<c:when test="${pendingConferenceCount > 0}">
		<div class="table-responsive">
		<div class="panel-body">
		<table class="table table-striped" style="font-size:12px">
				<thead>
				<tr>
					<th style="width: 80px;"> <input class="checkAll" type="checkbox" id="selectAll" style="width: 15px; height: 30px;"/>&nbsp;All</th>
					<th>Sr. No.</th>
					<th>Year</th>
					<th>Month</th>
					<th>Subject</th>
					<th>Session</th>
					<th>Date</th>
					<th>Day</th>
					<th>Start Time</th>
					<th>Faculty ID</th>
					<th>Session Type</th>
				</tr>
				</thead>
				<tbody>
				
				<c:forEach var="bean" items="${pendingConferenceList}" varStatus="status">
			        <tr>
			        	<tr class="myDiv"  id="${status.index}">
			        	<td><input class="checkBox" type="checkbox"  onclick="setCheckAll()" name="sessionList" value="${bean.id}" style="width: 15px; height: 30px;"/></td>
			            <td><c:out value="${status.count}" /></td>
			            <td><c:out value="${bean.year}" /></td>
			            <td><c:out value="${bean.month}" /></td>
			            <td><c:out value="${bean.subject}" /></td>
			            <td><c:out value="${bean.sessionName}" /></td>
			            <td><c:out value="${bean.date}" /></td>
						<td><c:out value="${bean.day}" /></td>
						<td><c:out value="${bean.startTime}" /></td>
						<td><c:out value="${bean.facultyId}" /></td>
						<c:set var = "sessiontypes" value = "${bean.sessionType}"/>
						<td><c:out value="${fn:replace(fn:replace(sessiontypes, '1', 'Webinar'), '2', 'Meeting')}" /></td>
			        </tr>   
			    </c:forEach>
					
				</tbody>
			</table>
			</div>
		</div>
		<br>
	
		</c:when>
		</c:choose>
		
		</form:form>
		</div>
	</section>




	<jsp:include page="footer.jsp" />
	
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
	<script type="text/javascript" src="https://code.jquery.com/jquery-3.5.1.js"></script>
	<script type="text/javascript" src="https://cdn.datatables.net/1.10.22/js/jquery.dataTables.min.js"></script>
	<script src="${pageContext.request.contextPath}/assets/js/autoScheduleSessions.js" type="text/javascript"></script>
	

</body>
</html>
