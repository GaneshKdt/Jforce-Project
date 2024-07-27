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
	<jsp:param value="Upload Student Photo" name="title" />
</jsp:include>

<body class="inside">

	<section class="content-container login">
		<div class="container-fluid customTheme">
			
			<div class="row"><legend>Download Dispatch Order Document Merge</legend></div>
			<%@ include file="messages.jsp"%>
			
				<form:form  method="post" action="downloadDispatchOrderDocumentMerge" enctype="multipart/form-data">
				<fieldset>
					<div class="panel-body">
					 <div class="row">
						<div class="col-md-6 column">
							<div class="form-group">
								<label>Upload File</label>
								<input class="form-control" name="file" type="file" required>  
							</div>
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<button type="submit" id="submit" name="submit" class="btn btn-large btn-primary">Upload</button>
							</div>
						</div>
						</div>
					 <div class="row">
						<div class="col-md-6 column">
							<c:if test="${not empty mergedUrl}">
								<a href="${mergedUrl}">Download Merged File</a>
							</c:if>
						</div>
					</div>
					</div>
				</fieldset>
			   </form:form>
			   
		</div>
	</section>

	<%@ include file="footer.jsp"%>

</body>
</html>
