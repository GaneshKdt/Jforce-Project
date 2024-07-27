<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.poi.util.SystemOutLogger"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.ArrayList"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Documents" name="title" />
</jsp:include>

<body class="inside">

	<%
	DocumentFileSet filesSet = (DocumentFileSet)request.getAttribute("filesSet");
	ArrayList<DocumentBean> documents = filesSet.getDocuments();
	int noOfDocuments = documents.size();
	%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<%@ include file="messages.jsp"%>
			
			<%if(noOfDocuments > 0){ %>
			
			<div class="row"><legend>Select Files for Documents to Upload</legend></div>
			
				
				<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="AEPOnboardingDocumentUpload">
				
					<form:hidden path="sfdcRecordId"/>
					<form:hidden path="recordType"/>
					<form:hidden path="aepid" value="${ filesSet.aepid }"/>
					<input type="hidden" name="type" value="${filesSet.recordType}"/>
					<input type="hidden" name="leadId" value="${filesSet.sfdcRecordId}"/>
					<input type="hidden" name="accountId" value="${filesSet.sfdcRecordId}"/>
					
					<div class="panel-body">
					<div class="col-md-18 column">
					
					<table class="table table-striped table-hover">
						<thead>
							<tr>
								<th>Document Name</th>
								<th>Document Status</th>
								<th>Select File</th>
							</tr>
						</thead>
					
					
					 
					<% 
						for(int i = 0 ; i < documents.size() ; i++) {
						DocumentBean document = documents.get(i);
					%>
						<input type="hidden" name="documents[<%=i%>].sfdcDocumentRecordId" value="<%=document.getSfdcDocumentRecordId()%>">
						<input type="hidden" name="documents[<%=i%>].documentStatus" value="<%=document.getDocumentStatus()%>">
						<input type="hidden" name="documents[<%=i%>].documentName" value="<%=document.getDocumentName()%>">
						<tr>
							<td><%=document.getDocumentName() %></td>
							<td><%= !StringUtils.isBlank(document.getDocumentStatus()) ? document.getDocumentStatus() : "Not Submited" %></td>
							<td>
								<div class="form-group" align="left" >
									<input id="fileData"  type="file" name="documents[<%=i%>].file"> 
								</div>
							</td>
						</tr>
					<% } %>
					
					</table>
					
					<%if(noOfDocuments > 0){ %>
						<div class="form-group">
							<button id="submit" name="submit" class="btn btn-large btn-primary" style="margin: 20px 0px 20px 0px"
							formaction="AEPOnboardingDocumentUpload">Upload</button>
						</div>
					<%} %>
					</div>
					
			</div>

			</form:form>
			
		<%} %>
		</div>
	</section>

	<%@ include file="footer.jsp"%>

</body>
</html>
