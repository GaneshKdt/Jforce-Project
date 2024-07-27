<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

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
	System.out.println("filesSet = "+filesSet);
	System.out.println("documents = "+documents);
	
	 %>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<%@ include file="messages.jsp"%>
			
			<%if(noOfDocuments > 0){ %>
			
			<div class="row"><legend>Select Files for Documents to Upload</legend></div>
			
				
				<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="uploadDocuments">
				
					<form:hidden path="sfdcRecordId"/>
					<form:hidden path="recordType"/>
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
							<td><%=document.getDocumentStatus() != null ? document.getDocumentStatus() : ""%></td>
							<td>
								<div class="form-group" align="left" >	
									<input id="fileData"  type="file" name="documents[<%=i%>].file"> 
								</div>
							</td>
						</tr>
					<%} %>
					
					</table>
					
					<%if(noOfDocuments > 0){ %>
						<div class="form-group">
							<button id="submit" name="submit" class="btn btn-large btn-primary" style="margin: 20px 0px 20px 0px"
							formaction="uploadDocuments">Upload</button>
						</div>
					<%} %>
					</div>
					
					<c:if test="${ not empty studentPhotographURL }">
					<div>
						<h3>Preview Image</h3>
			            <img id="preview" src="${ studentPhotographURL }" height=500 width="400" alt="Preview Image" style="margin: 20px 0px 20px 0px" /><br>
				        <span> 
				        	<i class="fa fa-warning" style="color: #c72127"></i>
					        Please make sure that the orientation and the size of the image is correct, the same will be followed through out portal.
				        </span>
			        </div>
					</c:if>

			</div>

			</form:form>
			
		<%} %>
		</div>
	</section>

	<%@ include file="footer.jsp"%>
	
	<!-- 
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <script type="text/javascript">
	
    	function readURL(input) {
    	  	if (input.files && input.files[0]) {
    	    	var reader = new FileReader();
    	    
    	    	reader.onload = function(e) {
    	      	$('#blah').attr('src', e.target.result);
    	    	}
    	    
    	    	reader.readAsDataURL(input.files[0]); // convert to base64 string
    	  	}
    	}

    	$("#fileData").change(function() {
   			readURL(this);
   		});
	</script>
 	-->
</body>
</html>
