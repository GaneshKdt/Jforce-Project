<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Faculty Availability Dates" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>


	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Faculty Availability Dates</legend></div>
			
				<%@ include file="messages.jsp"%>
				<%@ include file="uploadAvailabilityFacultyDatesErrorMessages.jsp"%>
				
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadFacultyAvailability">
					<div class="panel-body">
					<div class="col-md-6 column">
					<div class="form-group">
						<form:label for="fileData" path="fileData">Select file</form:label>
						<form:input path="fileData" type="file" />
					</div>
					<div class="form-group">
						<form:select id="year" path="year" class="form-control" required="required"  itemValue="${fileBean.year}">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month"  class="form-control" required="required" itemValue="${fileBean.month}">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
						</form:select>
					</div>
					
				
			</div>
			
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			Faculty ID |
<!-- 		Faculty ID | Date (DD-MMM-YYYY) | StartTime (HH:MM 24 Hour format) <br> -->
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/templates/FacultyAvailability.xlsx" target="_blank">Download a Sample Template</a>
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadFacultyAvailability">Upload</button>
				</div>

				
			</div>
			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
