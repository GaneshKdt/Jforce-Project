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
	<jsp:param value="Upload Sessions by location" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Schedule Session By Location </legend></div>
			
				<%@ include file="messages.jsp"%>
				<%@ include file="uploadCorporateScheduleSessionErrorMessages.jsp"%>
				
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="batchSessionScheduling">
					<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group">
							<form:label for="fileData" path="fileData">Select file</form:label>
							<form:input path="fileData" type="file" />
						</div>
						<%-- 
						<div class="form-group">
							<form:select id="corporateName" path="corporateName" type="text" placeholder="Corporate Name" class="form-control" required="required" itemValue="${fileBean.month}">
								<form:option value="">Select corporate Name</form:option>
								<form:option value="Verizon">Verizon</form:option>
							</form:select>
						</div> --%>
						
						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${fileBean.month}">
								<form:option value="">Select Academic Month</form:option>
								<form:option value="Jan">Jan</form:option>
								<form:option value="Jul">Jul</form:option>
							</form:select>
						</div>
					
						<div class="form-group">
							<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${fileBean.year}">
								<form:option value="">Select Academic Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
						<hr>
					
					<div class="form-group">
						<label for="startDate">Session Start Date : (For Download Report)&nbsp;&nbsp;</label>
						<form:input type="date" path="startDate" value="${fileBean.startDate}" id="startDate" style="color:black;"/>
					</div>
					<div class="form-group">
						<label for="endDate">Session End Date : (For Download Report)&nbsp;&nbsp;&nbsp;&nbsp;</label>
						<form:input type="date" path="endDate" value="${fileBean.endDate}" id="endDate" style="color:black;"/>
					</div>
					
			</div>
			
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			DATE (DD-MMM-YYYY) | STARTTIME (HH:MM 24 Hour format) | ENDTIME (HH:MM 24 Hour format)| SUBJECT | SUBJECT CODE | Session Name | Faculty ID | Faculty Location | 
			Parallel 1 FacultyId | Parallel 1 FacultyId | Parallel 2 FacultyId | Parallel 2 FacultyId | Parallel 3 FacultyId | Parallel 3 FacultyId | Corporate Name | Track | Session Type  <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/templates/batchSessionSchedulingTemplate.xlsx" target="_blank">Download a Sample Template</a>
			<br><hr>
			<c:if test="${allCancelledSessionSize > 0 }">
				<div class="panel-body">
					<h2>&nbsp;Session Report<font size="2px"> (${allCancelledSessionSize} Records Found) &nbsp; <a href="downloadCancelledSessionReport" style="color:blue;">Download to Excel</a></font></h2>
				</div>
			</c:if>
			
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="batchSessionScheduling">Upload</button>
					<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="cancelSessionReScheduling">Get Session</button>
				</div>
			</div>
			
			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
