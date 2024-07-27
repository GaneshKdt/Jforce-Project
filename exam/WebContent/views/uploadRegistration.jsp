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

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Registrations" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

		<div class="row"><legend>Upload Registrations</legend></div>
			
		<%@ include file="messages.jsp"%>
		<%@ include file="uploadExcelErrorMessages.jsp"%>
		<form:form modelAttribute="fileBean" method="post"
			enctype="multipart/form-data" action="uploadRegistration">
			<div class="panel-body">
			<div class="col-md-6 column">
				<div class="form-group">
					<form:label for="fileData" path="fileData">Select file</form:label>
					<form:input path="fileData" type="file" />
				</div>
				
				<div class="form-group">
				<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="uploadRegistration">Upload</button>
				</div>
			</div>
			
			<div class="col-md-12 column">
			
				Registration records will store information about students session registrations for every semester. 
				This data can be pulled from ready report available in Salesforce<br>
				<b>Format of Upload: </b><br>
				Student No	| Program	| Semester (1/2/3/4) | Session (Jul/Jan) | Year | IC Record ID | IC Name <br><br>
				<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Registration_Upload_Template.xlsx" target="_blank">Download a Sample Template</a>
			
			</div>
			
			</div>
			<br>

			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
