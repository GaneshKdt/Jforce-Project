<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('UMF_ACCESS_URL')" var="UMF_ACCESS_URL"/>
<jsp:include page="../jscss.jsp">
	<jsp:param value="Upload UFM Show Cause" name="title" />
</jsp:include>

<style>
.customTheme .table>tbody>tr>th, .customTheme .table>tbody>tr>td{
	padding-left: 1.3em;
}
</style>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload UFM Show Cause</legend></div>
			
			<div class="panel-body">
				<%@ include file="../messages.jsp"%>
				<form:form modelAttribute="fileBean" method="post" enctype="multipart/form-data" action="uploadUFMShowCauseFile">
					<div class="row">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="showCauseDeadline">Deadline</label>
								<form:input type="datetime-local" path="showCauseDeadline" id="showCauseDeadline" required="required"/>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="year">Exam Year</label>
								<form:select path="year" id="year" required="required">
									<form:option value="">Select Year</form:option>
									<form:options items="${ yearList }"/>
								</form:select>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="month">Exam Month</label>
								<form:select path="month" id="month" required="required">
									<form:option value="">Select Month</form:option>
									<form:options items="${ monthList }"/>
								</form:select>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="category">Category</label>
								<form:select path="category" id="category" required="required">
									<form:option value="">Select Category</form:option>
									<form:option value="UFM">UFM</form:option>
									<form:option value="COC">COC</form:option>
									<form:option value="DisconnectAbove15Min">DisconnectAbove15Min</form:option>
									<!--<form:option value="DisconnectBelow15Min">DisconnectBelow15Min</form:option> -->
								</form:select>
							</div>
						</div>
						
						<div class="col-md-6 column">
							<form:label for="fileData" path="fileData">Select file Show Cause File</form:label>
							(Format : Sapid, Subject, Exam Date, Exam Time, Reason)
							<form:input path="fileData" type="file" accepts=".xls,.xlsx" required="required"/>
							
							<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/UFMShowCauseTemplate.xlsx" target="_blank">Download a Sample Template</a> <br>
						</div>
						
						<div class="col-md-6 column">
							<form:label for="incidentUploadFileData" path="incidentUploadFileData">Select file Incident File</form:label>
							(Format : sapid,subject,Incident,TimeStamp,Video Number)
							<form:input path="incidentUploadFileData" type="file" accepts=".xls,.xlsx" />
							
							<a href="${pageContext.request.contextPath}/resources_2015/templates/UFMIncidentSampleTemplate.xlsx" target="_blank">Download Template</a> <br>
						</div>
					</div>
					<br>
					<div class="row">
						<div class="col-md-6 column">
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="uploadUFMShowCauseFile">Upload</button>
						</div>
					</div>
				</form:form>
			</div>			
			
		</div>
	</section>
	
	
	<c:if test="${ errorList != null && errorList.size() > 0 }">
	
		<section class="content-container login">
			<div class="container-fluid customTheme">
				<div class="row"><legend>Error List</legend></div>
				<div class="panel-body">
					<div class="table-responsive">
				 		<table id="error-table" class="table table-striped table-hover dataTables">
							<thead>
								<tr>
									<th>Sapid</th>
									<th>Subject</th>
									<th>Exam Date</th>
									<th>Exam Time</th>
									<th>Reason</th>
									<th>Error</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach items = "${ errorList }" var = "bean">
								<tr>
									<td>${ bean.sapid }</td>
									<td>${ bean.subject }</td>
									<td>${ bean.examDate }</td>
									<td>${ bean.examTime }</td>
									<td>${ bean.ufmMarkReason }</td>
									<td>${ bean.error }</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</section>
	</c:if>
	
	<c:if test="${ successList != null && successList.size() > 0 }">
	
		<section class="content-container login">
			<div class="container-fluid customTheme">
				<div class="row"><legend>Success List</legend></div>
				<div class="panel-body">
					<div class="table-responsive">
				 		<table id="success-table" class="table table-striped ">
							<thead>
								<tr>
									<th>Sapid</th>
									<th>Subject</th>
									<th>Exam Date</th>
									<th>Exam Time</th>
									<th>Reason</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach items = "${ successList }" var = "bean">
								<tr>
									<td>${ bean.sapid }</td>
									<td>${ bean.subject }</td>
									<td>${ bean.examDate }</td>
									<td>${ bean.examTime }</td>
									<td>${ bean.ufmMarkReason }</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</section>
	</c:if>
	
	<c:if test="${ successListDocuments != null && successListDocuments.size() > 0 }">
	
		<section class="content-container login">
			<div class="container-fluid customTheme">
				<div class="row"><legend>Document Success List</legend></div>
				<div class="panel-body">
					<div class="table-responsive">
				 		<table id="success-doc-table" class="table table-striped">
							<thead>
								<tr>
									<th>Sapid</th>
									<th>Subjects</th>
									<th>Document URL</th>
									<th>Reason</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach items = "${ successListDocuments }" var = "bean">
								<tr>
									<td>${ bean.sapid }</td>
									<td>
										<c:forEach items = "${ bean.subjectsList }" var = "subjectsList">
											${ subjectsList.subject }
										</c:forEach>
									</td>
									<td><a href = "${UMF_ACCESS_URL}/${bean.showCauseNoticeURL}">URL</a></td>
									<td>${ bean.ufmMarkReason }</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</section>
	</c:if>
	<jsp:include page="../footer.jsp" />

	<!-- Calling Datatable JS jQuery -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

	<script>
		$(document).ready( function () {
			$('#success-table').DataTable();
			$('#error-table').DataTable();
			$('#success-doc-table').DataTable();
		})
	</script>
	
</body>
</html>
