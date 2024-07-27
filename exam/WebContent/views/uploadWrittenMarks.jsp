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
	<jsp:param value="Upload Student Marks" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Student Written Marks</legend></div>
			
				<%@ include file="messages.jsp"%>
				<%@ include file="uploadExcelErrorMessages.jsp"%>
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadWrittenMarks">
					<div class="row">
					<div class="col-md-6 column">
				
					<div class="form-group">
						<form:label for="fileData" path="fileData">Select file</form:label>
						<form:input path="fileData" type="file" />
					</div>

					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${studentMarks.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${studentMarks.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Feb">Feb</form:option>
							<form:option value="Mar">Mar</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="May">May</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Jul">Jul</form:option>
							<form:option value="Aug">Aug</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Oct">Oct</form:option>
							<form:option value="Nov">Nov</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="uploadWrittenMarks">Upload</button>
						</div>
					</div>
					
					<div class="col-md-12 column">
						<b>Format of Upload: </b><br>
						SR.NO. | NAME OF THE STUDENT | STUDENT NO.	| PROGRAM	| SEMESTER	| SUBJECT  NAME	| TEE SCORE | REMARKS<br><br>
						<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Written_Marks_Input_Template.xlsx" target="_blank">Download a Sample Template</a> <br>
						<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Written_Marks_Input_SAS_Template.xlsx" target="_blank">Download a Sample Template for SAS</a>
					</div>
			
			</div>

			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
