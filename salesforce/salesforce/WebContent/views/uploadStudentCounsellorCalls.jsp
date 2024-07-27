<!DOCTYPE html>

<%@ page contentType="text/html; charset=UTF-8"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.ArrayList"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Documents" name="title" />
</jsp:include>

<head>
	<style type="text/css">
		.formDiv{
			margin: 20px;
		    background: white;
		    border-radius: 4px;
		    padding: 20px;
		}
	</style>
</head>

<body class="inside">

	<section class="content-container login">
		<div class="container-fluid customTheme">
		
			<div class="row">
				<legend>Select Files to Upload</legend>
			</div>

			<%@ include file="messages.jsp"%>

			<form:form modelAttribute="fileset" method="post" id="callUploadForm"
				enctype="multipart/form-data" action="uploadStudentCounsellorCall">
				
				<div class="formDiv">
					
					<form:input path="userId" value="${ userId }" hidden="true"/>
					
					<label style="margin: 10px; font-size: 1em;">Name</label>
					<form:input path="name" type="text" required="true"/>
					
					<label style="margin: 10px; font-size: 1em;">Function</label>
					<form:input path="function" type="text" required="true"/>
					
					<label style="margin: 10px; font-size: 1em;">Category</label>
					<form:input path="category" type="text" required="true"/>
					
					<label style="margin: 10px; font-size: 1em;">Select File</label>
					<form:input id="file" path="files" type="file" required="true" multiple="true" />
					
					<button id="upload" name="submit" class="btn btn-primary" style="border: none; padding: 10px 25px; border-radius: 4px;"
						formaction="uploadStudentCounsellorCall">Upload</button>
						
				</div>
				
			</form:form>

		</div>
		
	</section>

	<div class="container-fluid customTheme">
	
		<div class="row">
			<legend>Uploaded files</legend>
		</div>
	
		<div class="formDiv">
			<table class="table table-striped" id="uploadedCalls">
				<thead>
					<tr>
						<th>Sr No</th>
						<th>Name</th>
						<th>URL</th>
						<th>Function</th>
						<th>Category</th>
					</tr>
				</thead>
				<tbody>
					<c:set var="count" value="0"></c:set>
					<c:forEach var="calls" items="${ callList }">
						<c:set var="count" value="${count+1}"></c:set>
						<tr>
							<td>${ count }</td>
							<td>${ calls.name }</td>
							<td>
							<audio controls controlsList="nodownload" preload="auto" id="audio_player">
								<source src='${ calls.url }' type="audio/mpeg">
							</audio>
							</td>
							<td>${ calls.function }</td>
							<td>${ calls.category }</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		
	</div>
	
	<%@ include file="footer.jsp"%>

	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>

	<script>
	
		try{
			
			$('#file').bind('change', function( event ) {

				let totalSize = 0
				
			  	event.preventDefault();
			  	
			  	for( i = 0; i< this.files.length; i++ ){
			  		
			  		totalSize += this.files[i].size
			  		
			  	}
			  	
			  	if( totalSize > 40000000 ){
			  		
			  		alert("Total size of the selected files are exceeding the configured maximum ( 40 MB ). Please reduce the files and try again.");
			  		$('#upload').prop('disabled', true);
			  		
			  	}else{

			  		$('#upload').prop('disabled', false)

			  	}
			  	
			});
			
		}catch ( error ){
			console.debug(error)
		}
		
		$('#uploadedCalls').DataTable();
		
	</script>

</body>
</html>
