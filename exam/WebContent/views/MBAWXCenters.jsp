<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload TEE Marks List" name="title" />
</jsp:include>
<head>
   		 <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
   		 <style>
.dataTables_filter > label > input{
	float:right !important;
}
.toggleListWell{
cursor: pointer !important;
	margin-bottom:0px !important;
}
.toggleWell{
	background-color:white !important;
}
input[type="radio"]{
	width:auto !important;
	height:auto !important;
	
}
.optionsWell{
	padding:0px 10px;
}
</style>
</head>
<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row"><legend>Upload MBA-WX Center list </legend></div>
			
				<%@ include file="messages.jsp"%>
				<c:if test="${ status == 'error' }">
				<table class="table table-striped " style="background-color:rgb(255,244,244);color:rgb(194,57,52)">
		 			<thead>
		 				<td><b>name</b></td>
		 				<td><b>city</b></td>
		 				<td><b>state</b></td>
		 				<td><b>capacity</b></td>
		 				<td><b>address</b></td>
		 				<td><b>googleMapUrl</b></td>
		 				<td><b>locality</b></td>
		 			</thead>
		 			<tbody>
		 				<c:forEach var="errorMBAWXCentersBean" items="${errorMBAWXCentersBeanList}">
		 					<tr>
		 						<td><c:out value="${errorMBAWXCentersBean.name}"/></td>
		 						<td><c:out value="${errorMBAWXCentersBean.city}"/></td>
		 						<td><c:out value="${errorMBAWXCentersBean.state}"/></td>
		 						<td><c:out value="${errorMBAWXCentersBean.capacity}"/></td>
		 						<td><c:out value="${errorMBAWXCentersBean.address}"/></td>
		 						<td><c:out value="${errorMBAWXCentersBean.googleMapUrl}"/></td>
		 						<td><c:out value="${errorMBAWXCentersBean.locality}"/></td>
		 					</tr>
		 				</c:forEach>
		 			</tbody>
		 		</table>
		 		</c:if>
				<%@ include file="uploadExcelErrorMessages.jsp"%>
												
				<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data">
					<div class="panel-body">
					
					
					<div class="col-md-6 ">
						<!--   -->
					<div class="form-group">
						<form:label for="fileData" path="fileData">Select file</form:label>
						<form:input path="fileData" type="file" />
					</div>		
			</div>
			
			<div class="col-md-12 column">
			<b>Format of Upload: </b><br>
			Name | City | State | Capacity | Address | GoogleMapUrl | locality <br>
			<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/sampleCenterMBAWX.xlsx" target="_blank">Download a Sample Template</a> <br>
			</div>
			
			
			</div>
			<br>
			<div class="row">
				<div class="col-md-6 column">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadMBAWXCenters">Upload</button>
				</div>

				
			</div>
			</form:form>
		
		<br/><br/>
		<div class="table-responsive">
		 		<table id="dataTable" class="table table-striped ">
		 			<thead>
		 				<td><b>centerId</b></td>
		 				<td><b>name</b></td>
		 				<td><b>city</b></td>
		 				<td><b>state</b></td>
		 				<td><b>capacity</b></td>
		 				<td><b>address</b></td>
		 				<td><b>googleMapUrl</b></td>
		 				<td><b>locality</b></td>
		 				<td><b>action</b></td>
		 			</thead>
		 			<tbody>
		 				<c:forEach var="mbawxCenter" items="${mbawxCentersList}">
		 					<tr>
		 						<td><c:out value="${mbawxCenter.centerId}"/></td>
		 						<td><c:out value="${mbawxCenter.name}"/></td>
		 						<td><c:out value="${mbawxCenter.city}"/></td>
		 						<td><c:out value="${mbawxCenter.state}"/></td>
		 						<td><c:out value="${mbawxCenter.capacity}"/></td>
		 						<td><c:out value="${mbawxCenter.address}"/></td>
		 						<td><c:out value="${mbawxCenter.googleMapUrl}"/></td>
		 						<td><c:out value="${mbawxCenter.locality}"/></td>
		 						<td><a href="javascript:void(0)" centerId="${mbawxCenter.centerId}" class="deleteCenterFromList" style="color:#c72127;">Delete</a></td>
		 					</tr>
		 				</c:forEach>
		 			</tbody>
		 		</table>
		 		</div>
		</div><br/><br/>
	</section>

	<jsp:include page="footer.jsp" />
	<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	    <script>
	    $(document).ready( function () {
	        $('#dataTable').DataTable();
	        $(document).on('click',".deleteCenterFromList",function(){
	        	let prompt = confirm("Are you sure you want to delete?");
	        	if(!prompt){
	        		return false;
	        	}
	        	let centerId = $(this).attr("centerId");
	        	$.ajax({
	        		url:"deleteCenterFromList?centerId=" + centerId,
	        		method:"POST",
	        		success:function(response){
	        			console.log("--->>>> response");
	        			if(response.status == "success"){
	        				alert(response.message);
	        				window.location.href = "uploadMBAWXCentersForm";
	        			}else{
	        				alert(response.message);
	        			}
	        		},
	        		error:function(error){
	        			alert("Failed to delete record");
	        			console.log("--->>>> error");
	        			console.log(error);
	        		}
	        	});
	        })
	    } );
	    </script>
</body>
</html>
