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
			<%@ include file="messages.jsp"%>
			
			
			<div class="row"><legend>Upload Profile Image</legend></div>
			
				
				<form:form  method="post"	enctype="multipart/form-data" action="uploadPhoto">

					<input type="hidden" name="id" value="${id}"/>
					<input type="hidden" name="type" value="${type}"/>
					<input type="hidden" name='accountId' value="${ accountId }">
								
					<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group" align="left" >	
							<label>Select Photo File</label>
							<input id="fileData" type="file" name="file" > 
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
						
						
						<div class="form-group">
							<button id="submit" name="submit" class="btn btn-large btn-primary"  style="margin: 20px 0px 20px 0px"
							formaction="uploadPhoto">Upload</button>
						</div>
					</div>

			</div>
			</form:form>
			
		</div>
	</section>

	<%@ include file="footer.jsp"%>

</body>
</html>
