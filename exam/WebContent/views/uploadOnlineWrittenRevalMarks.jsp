<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Online Exam Marks" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Online Exam Marks</legend></div>
			
				<%@ include file="messages.jsp"%>
				<%@ include file="uploadOnlineMarksErrorMessages.jsp"%>
												
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadOnlineExamMarks">
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
									<%-- 
									<form:option value="2017">2017</form:option>
									 --%>
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
									<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control"   itemValue="${fileBean.subject}">
										<form:option value="">Select Subject</form:option>
										<form:options items="${subjectList}" />
									</form:select>
							</div>
							
							<%-- <div class="form-group">
									<form:password id="filePassword" path="filePassword" placeholder="Password" class="form-control"  value="${fileBean.filePassword}"/>
							</div> --%>
					
					
					</div>
					
					<div class="col-md-12 column">
					<b>Format of Upload: </b><br>
					Student ID	| Section 4 score | Remarks <br>
					  <a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Online_Marks_Template.xlsx" target="_blank">Download a Sample Template</a> 
					</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadOnlineWrittenRevalMarks">Upload</button>
				</div>

			</div>
			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
