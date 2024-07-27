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
	<jsp:param value="Upload Student Master" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Student Master</legend></div>
			
		<%@ include file="messages.jsp"%>
		<%@ include file="uploadStudentMasterErrorMessages.jsp"%>
		<form:form modelAttribute="fileBean" method="post"
			enctype="multipart/form-data" action="uploadStudentsMaster">
			<div class="row">
			<div class="col-md-6 column">
				<!--   -->

				<form:label for="fileData" path="fileData">Select file</form:label>
				<form:input path="fileData" type="file" />
			</div>
			
			<div class="col-md-12 column">
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Exam_Student_Master_Upload_Template_10.xlsx" target="_blank">Download a Sample Template</a>
			
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadStudentsMaster">Upload</button>
				</div>

				
			</div>
			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
