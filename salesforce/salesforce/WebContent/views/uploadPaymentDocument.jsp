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
	<jsp:param value="Upload Payment Document" name="title" />
</jsp:include>

<body class="inside">

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<%@ include file="messages.jsp"%>
			
			
			<div class="row"><legend>Upload Payment Document</legend></div>
			
				
				<form:form  method="post"	enctype="multipart/form-data" action="uploadPaymentDocument">

					<input type="hidden" name="id" value="${id}"/>
					
								
					<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group" align="left" >	
							<label>Select Payment Document/DD</label>
							<input id="fileData" type="file" name="file" > 
						</div>
						
						<div class="form-group">
							<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="uploadPaymentDocument">Upload</button>
						</div>
					</div>

			</div>

			</form:form>
			
		</div>
	</section>

	<%@ include file="footer.jsp"%>

</body>
</html>
