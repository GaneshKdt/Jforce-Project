<!DOCTYPE html>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value="Upload Sessions" name="title" />
</jsp:include>

<script src="/assets/js/jquery-1.11.3.min.js"></script> 


<body class="inside">

	<%@ include file="/views/adminCommon/header.jsp"%>
	<br>
	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Schedule Session By Excel </legend></div>
			
				<%@ include file="/views/adminCommon/messages.jsp"%>
				<%@ 
					
					include file="ScheduleSessionErrorMessages.jsp"
				%>
				
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="batchSessionScheduling">
					<div class="panel-body">
					<div class="row">
						<div class="col-md-6 column">
							<div class="form-group">
								<form:label for="fileData" path="fileData">Select file</form:label>
								<form:input path="fileData" type="file" />
							</div>
							
						</div>
						
						<div class="col-md-12 column">
							<b>Format of Upload: </b><br>
							DATE (DD/MM/YYYY) | STARTTIME (HH:MM:SS 24 Hour format) | ENDTIME (HH:MM:SS 24 Hour format) | Session Name | Faculty ID | Faculty Location | Description <br>
							<a href="resources_2015/templates/batchSessionSchedulingTemplateForCS.xlsx" target="_blank">Download a Sample Template</a>
							
						</div>
						</div>
					</div>
					
					<br>
					
					<div class="row">
						<div class="col-md-6 column">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="batchSessionScheduling">Upload</button>
						</div>
					</div>
				
				</form:form>
		</div>
	</section>
	
	<jsp:include page="/views/adminCommon/footer.jsp" />
	
</body>
</html>