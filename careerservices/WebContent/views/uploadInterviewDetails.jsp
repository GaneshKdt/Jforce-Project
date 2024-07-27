<html class="no-js">


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@page import="com.nmims.beans.InterviewBean"%>
<%@page import="java.util.ArrayList"%>

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Upload Interview Details" name="title" />
</jsp:include>	

<body class="inside">

	<%@ include file="header.jsp"%>


	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Interview Details</legend></div>

			<jsp:include page="/views/messages.jsp" />
				
				<form:form modelAttribute="interviewBean" method="post" enctype="multipart/form-data" action="uploadInterviewDetails">
					<div class="panel-body">
						<div>
							<div class="col-md-6 column">
								<div class="form-group">
									<form:label for="fileData" path="fileData">Select file</form:label>
									<form:input path="fileData" type="file" required="required"/>
								</div>
							</div>
					
							<div class="col-md-12 column">
							<b>Format of Upload: </b><br>
							Faculty ID | Date (DD-MMM-YYYY) | Start Time (HH:MM 24 Hour format) | End Time (HH:MM 24 Hour format) <br>
							<a href="resources_2015/templates/InterviewDetails.xlsx" target="_blank">Download a Sample Template</a>
							</div>
						</div>
						
						<c:if test="${not empty errorList}">
							<div style="display:inline-block; position: relative; width: 100%;">
						
							<div style="margin: 20px; text-align: center;">
								<legend> Successfully Uploaded Data</legend>
							</div> 
							
							<div style="margin: 20px;">
								<table id="successTable" class="table table-striped">
									<thead>
										<tr>
											<th>Sr No.</th>
											<th>Faculty Id</th>
											<th>Date</th>
											<th>Start Time</th>
											<th>End Time</th>
										</tr>
									</thead> 
									<tbody>
									<c:choose>
										<c:when test="${ empty successList }">
											<td colspan="5" style="text-align: center; ">No record were uploaded</td>
										</c:when>
										<c:otherwise>
											<c:set var = "countSuccess" value = "${0}"/>
											<c:forEach items="${ successList }" var="success">
												<tr>
													<td>${ countSuccess = countSuccess+1 }</td>
													<td>${ success.facultyId }</td>
													<td>${ success.date }</td>
													<td>${ success.startTime }</td>
													<td>${ success.endTime }</td>
												</tr>		
											</c:forEach>
										</c:otherwise>
									</c:choose>
										
										
									</tbody>
								</table>
							</div>
						
							<div style="margin: 20px; text-align: center;">
								<legend> Error In Data</legend>
							</div> 
							
							<div style="margin: 20px;">
								<table id="errorTable" class="table table-striped">
									<thead>
										<tr>
											<th>Sr No.</th>
											<th>Faculty Id</th>
											<th>Date</th>
											<th>Start Time</th>
											<th>End Time</th>
											<th>Error</th>
										</tr>
									</thead> 
									<tbody>
										<c:set var = "countError" value = "${0}"/>
										<c:forEach items="${ errorList }" var="error">
											<tr>
												<td>${ countError = countError+1 }</td>
												<td>${ error.facultyId }</td>
												<td>${ error.date }</td>
												<td>${ error.startTime }</td>
												<td>${ error.endTime }</td>
												<td>${ error.errorMessage }</td>
											</tr>		
										</c:forEach>
									</tbody>
								</table>
							</div>
									
							<div style="margin: 20px; text-align: center;">
								<legend>Already Existing Data </legend>
							</div>
							<div style="margin: 20px;">
									<table id="dbData" class="table table-striped">
										<thead>
											<tr>
												<th>Sr No.</th>
												<th>Faculty Id</th>
												<th>Date</th>
												<th>Start Time</th>
												<th>End Time</th>
											</tr>
										</thead>
										<tbody>
										<c:set var = "countData" value = "${0}"/>
										<c:forEach items="${ allInterview }" var="interview">
											<tr>
												<td>${ countData = countData+1 }</td>
												<td>${ interview.facultyId }</td>
												<td>${ interview.date }</td>
												<td>${ interview.startTime }</td>
												<td>${ interview.endTime }</td>
											</tr>
										</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</c:if>
					</div>
					
					
					
					<div class="row">
						<div class="col-md-6 column">
							<button id="submit" name="submit" class="btn btn-large btn-primary"
								formaction="uploadInterviewDetails">Upload</button>
						</div>
					</div>
					
				</form:form>
				
		</div>
		
	</section>

	<jsp:include page="adminCommon/footer.jsp" />
	
	<script>
		 $(document).ready(function() {
			$('#successTable').DataTable();
		 	$('#errorTable').DataTable(); 
		 	$('#dbData').DataTable(); 
		 } );
	</script>

</body>
</html>
