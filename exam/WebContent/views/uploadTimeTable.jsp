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

			<div class="row"><legend>Upload Time Table</legend></div>
			
				<%@ include file="messages.jsp"%>
				<%@ include file="uploadTTExcelErrorMessages.jsp"%>
				
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadTimeTable">
					<div class="panel-body">
					<div class="col-md-6 column">
						
					<div class="form-group">
						<form:label for="fileData" path="fileData">Select file</form:label>
						<form:input path="fileData" type="file" />
					</div>
					
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${fileBean.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${fileBean.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="prgmStructApplicable" path="prgmStructApplicable" type="text" placeholder="Program Structure" class="form-control" required="required" itemValue="${fileBean.prgmStructApplicable}">
							<form:option value="">Select Program Structure</form:option>
							<form:option value="Jul2009">Jul2009 (Offline Exam)</form:option>
							<form:option value="Jul2013">Jul2013 (Offline Exam)</form:option>
							<form:option value="Jul2014">Jul2014 (Online Exam)</form:option>
							<form:option value="Jul2017">Jul2017 (Online Exam)</form:option>
							<form:option value="Jul2018">Jul2018 (Online Exam)</form:option>
							<form:option value="Jan2019">Jan2019 (Online Exam)</form:option>
							<form:option value="Jul2019">Jul2019 (Online Exam)</form:option>
							<form:option value="Jul2020">Jul2020 (Online Exam)</form:option>
							<form:option value="Jan2022">Jan2022 (Online Exam)</form:option>
							<form:option value="Jul2022">Jul2022 (Online Exam)</form:option>
						</form:select>
					</div> 
					
					<div class="form-group">
						<form:select id="ic" path="ic" type="text" placeholder="Corporate Center" class="form-control" required="required">
							<form:option value="">Select Corporate Center</form:option>
							<form:option value="All">All</form:option>
							<form:options items="${corporateCenterList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="uploadTimeTable">Upload</button>
					</div>
					
			</div>
			
			
			<div class="col-md-12 column">
			<b>Format of Upload Regular Exam (Offline Exam): </b><br>
			Program	| Sem | Subject | Date (DD-MMM-YYYY) | StartTime (HH:MM 24 Hour format) | EndTime (HH:MM 24 Hour format) | Mode(Online/Offline) <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Timetable_Upload_Template.xlsx" target="_blank">Download a Sample Template for Regular Exam</a>
			
			<br><br>
			
			<b>Format of Upload Regular Exam (Online Exam): </b><br>
			Program	| Sem | Subject | Date (DD-MMM-YYYY) | StartTime (HH:MM 24 Hour format) | EndTime (HH:MM 24 Hour format) | Mode(Online/Offline) <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Timetable_Upload_Template_Resit.xlsx" target="_blank">Download a Sample Template for Regular Exam</a>
			
			<br><br>
			
			<b>Format of Upload Resit Exam: </b><br>
			Program	| Sem | Subject | Date (DD-MMM-YYYY) | StartTime (HH:MM 24 Hour format) | EndTime (HH:MM 24 Hour format) | Mode(Online) <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Timetable_Upload_Template_Resit.xlsx" target="_blank">Download a Sample Template for Resit Exam</a>
			
			<br><br>
			
			<b>Format of Upload Corporate Batch: </b><br>
			Program	| Sem | Subject | Date (DD-MMM-YYYY) | StartTime (HH:MM 24 Hour format) | EndTime (HH:MM 24 Hour format) | Mode(Online) <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Timetable_Upload_Template_Resit.xlsx" target="_blank">Download a Sample Template for Corporate Batch</a>
			
			</div>
			
			
			
			</div>
			<br>
			
			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
