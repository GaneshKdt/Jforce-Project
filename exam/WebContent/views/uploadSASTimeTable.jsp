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

			<div class="row">
			    <legend>Upload SAS Time Table</legend>
			</div>
			
				<%@ include file="messages.jsp"%>
				<%@ include file="uploadExecutiveTTExcelErrorMessages.jsp"%>
				
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadTimeTable">
					<div class="panel-body">
					<div class="col-md-6 column">
						
					<div class="form-group">
						<form:label for="fileData" path="fileData">Select file</form:label>
						<form:input path="fileData" type="file" />
					</div>
					
						<div class="form-group">
						<form:select id="enrollmentYear" path="enrollmentYear"  class="form-control" required="required"  itemValue="${fileBean.year}">
							<form:option value="">Select Enrollment Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="enrollmentMonth" path="enrollmentMonth"  class="form-control" required="required" itemValue="${fileBean.month}">
							<form:option value="">Select Enrollment Month</form:option>
							<form:options items="${monthList}" />
						</form:select>
					</div>
						
					<div class="form-group">
						<form:select id="year" path="year"  class="form-control" required="required"  itemValue="${fileBean.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month"  class="form-control" required="required" itemValue="${fileBean.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:options items="${monthList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="prgmStructApplicable" path="prgmStructApplicable" class="form-control" required="required" itemValue="${fileBean.prgmStructApplicable}">
							<form:option value="">Select Program Structure</form:option>
							<form:options items="${programStructureList}" />
							
						</form:select>
					</div>
				
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="uploadSASTimeTable">Upload</button>
					</div>
					
			</div>
			
			
			<div class="col-md-12 column">
			<b>Format of Upload SAS Exam : </b><br>
			Date (DD-MMM-YYYY) | StartTime (HH:MM 24 Hour format) | EndTime (HH:MM 24 Hour format) <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/SAS_Timetable_Upload_Template.xlsx" target="_blank">Download a Sample Template for Regular Exam</a>
			</div>
			
			
			
			</div>
			<br>
			
			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
