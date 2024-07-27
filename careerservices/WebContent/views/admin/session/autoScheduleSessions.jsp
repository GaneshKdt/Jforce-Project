<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PageCareerservicesBean"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value="Auto Schedule Sessions" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="/views/adminCommon/header.jsp"%>

	<section class="content-container">
		<div class="container-fluid customTheme"></div>
	<%@ include file="/views/adminCommon/messages.jsp"%>
		<br>
		
		<div class="panel-body">
		<div class="row">
			<div class="col-md-6 column">
				<legend><font>&nbsp;Sessions yet to be created in Zoom : ${pendingConferenceCount}</font></legend>
			</div>
				<form role="form" id="passFailForm" action="bookTrainingSessions" method="post">
					<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="bookTrainingSessions">Create Zoom Training</button>
					<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
				</form>
		</div>
		<br>
		
		<c:choose>
		<c:when test="${pendingConferenceCount > 0}">
		<div class="table-responsive">
		<table class="table table-striped" style="font-size:12px">
				<thead>
				<tr>
					<th>Sr. No.</th>
					<th>Session</th>
					<th>Date</th>
					<th>Day</th>
					<th>Start Time</th>
					<th>Faculty ID</th>
				</tr>
				</thead>
				<tbody>
				
				<c:forEach var="bean" items="${pendingConferenceList}" varStatus="status">
			        <tr>
			            <td><c:out value="${status.count}" /></td>
			            <td><c:out value="${bean.sessionName}" /></td>
			            <td><c:out value="${bean.date}" /></td>
						<td><c:out value="${bean.day}" /></td>
						<td><c:out value="${bean.startTime}" /></td>
						<td><c:out value="${bean.facultyId}" /></td>
			        </tr>   
			    </c:forEach>
					
				</tbody>
			</table>
		</div>
		<br>
	
		</c:when>
		</c:choose>
		</div>
		
	</section>

	<jsp:include page="/views/adminCommon/footer.jsp" />

</body>
</html>
