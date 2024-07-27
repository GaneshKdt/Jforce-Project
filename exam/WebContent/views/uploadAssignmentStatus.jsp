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
	<jsp:param value="Upload Assignment Status" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload Assignment Status</legend></div>
			
				<%@ include file="messages.jsp"%>
				
				<%

					List<AssignmentStatusBean> errorBeanList = (ArrayList<AssignmentStatusBean>)request.getAttribute("errorBeanList"); 
					List<AssignmentStatusBean> subjectMappingErrorBeanList = (ArrayList<AssignmentStatusBean>)request.getAttribute("subjectMappingErrorBeanList"); 
					List<AssignmentStatusBean> subjectMappingSuccessBeanList = (ArrayList<AssignmentStatusBean>)session.getAttribute("subjectMappingSuccessBeanList");
					if(errorBeanList != null && errorBeanList.size() > 0){ 
				%>
								
						<div class="alert alert-danger">
				<%	
						for(int i = 0 ; i < errorBeanList.size() ; i++ ){
							AssignmentStatusBean bean = (AssignmentStatusBean)errorBeanList.get(i);
							out.println(bean.getErrorMessage());
							out.println("<br/>");
						}//End of for
						out.println("</div>");
					}//End of if
				
				%>

				<%
					if(subjectMappingErrorBeanList != null && subjectMappingErrorBeanList.size() > 0){ 
				
				%>
				
				<div class="alert alert-danger"> <%= subjectMappingErrorBeanList.size()%> errors found in mapping. Click <a href="downloadAsignmentUploadErrorReport">here to download Error Report in Excel</a></div>
				
				
				<%} %>
				
				
				<%
					if(subjectMappingSuccessBeanList != null && subjectMappingSuccessBeanList.size() > 0){ 
				
				%>
				
				<div class="alert alert-success"> <%= subjectMappingSuccessBeanList.size()%> correct Record found in file. Click <a href="downloadAsignmentUploadSuccessReport">here to download Error Report in Excel</a></div>
				
				
				<%} %>
								
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadAssignmentStatus">
					<div class="row">
					<div class="col-md-6 column">
						<!--   -->

						<form:label for="fileData" path="fileData">Select file</form:label>
						<form:input path="fileData" type="file" />
					
					<br/>
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${fileBean.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${fileBean.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					
			</div>
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			SAP ID	| Subject | Assignment Uploaded (Y)<br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/Assignment_Status_Upload_Template.xlsx" target="_blank">Download a Sample Template</a>
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadAssignmentStatus">Upload</button>
				</div>

				
			</div>
			</form:form>
		</div>
		</div>
	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
