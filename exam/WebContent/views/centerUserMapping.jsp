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
	<jsp:param value="Upload Time Table" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Center User Mapping</legend></div>
			
				<%@ include file="messages.jsp"%>
				<%@ include file="uploadTTExcelErrorMessages.jsp"%>
				
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="mapping">
					<div class="panel-body">
					<div class="col-md-6 column">
						
					<div class="form-group">
						<form:label for="fileData" path="fileData">Select file</form:label>
						<form:input path="fileData" type="file" />
					</div>
					
					<div class="form-group">
						<form:label for="year" path="year">Select Year</form:label>
						<form:select id="year" path="year" placeholder="State" class="form-control" required="required" >
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
					
					
					<div class="form-group">
						<form:label for="month" path="year">Select Month</form:label>
						<form:select id="month" path="month" placeholder="State" class="form-control" required="required" >
							<form:option value="">Select Month</form:option>
							<form:options items="${monthList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="centerUserMapping">Upload</button>
					</div>
					
			</div>
			
			
			<div class="col-md-12 column">
			<b>Format of Center User Mapping: </b><br>
			SapId	| Center Id <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Center_User_MappingTemplate.xlsx" target="_blank">Download a Sample Template for Center Using Mapping</a>
			
			</div>
			
			
			
			</div>
			<br>
			
			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>