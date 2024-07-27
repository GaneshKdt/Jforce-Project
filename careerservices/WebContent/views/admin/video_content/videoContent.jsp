<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value="${ title }" name="title" />
</jsp:include>


<body class="inside">

	<%@ include file="/views/adminCommon/header.jsp"%>
	<section class="content-container login">
	
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>${ title }</legend>
			</div>
			<%@ include file="/views/common/messages.jsp"%>
			<form style="padding:15px" class="form px-3" onsubmit="return false;">
				<div class="row" style="padding-top:15px">
					<div class="form-group">
						<input type="hidden" id="id" name="id">
						<input type="hidden" id="lastModifiedBy" name="lastModifiedBy" value="${ userId }">
						<input type="hidden" id="addedBy" name="addedBy" value="${ userId }">
						<input type="hidden" id="createdBy" name="createdBy" value="${ userId }">
						<input type="hidden" id="facultyId" name="facultyId">
					</div>
				</div>
				<div class="row" style="padding-top:15px">
					<div class="col-md-4 pb-3">
						<label for="videoTypeId">Video Type</label>
						
						<select class="form-control pl-2" id="videoTypeId" onchange="videoTypeChanged()" required name="videoTypeId">
							<option value=""></option>
							<option value="1">Orientation Video</option>
							<option value="2">Session Video</option>
						</select>
					</div>
					<div class="col-md-4">
						<label for="sessionId">Session</label>
						<select class="form-control pl-2" id="sessionId" name="sessionId" disabled>
							<option></option>
						</select>
					</div>
				</div>
				<div class="row" style="padding-top:15px">
					<div class="col-md-4 pb-3">
						<label for="fileName">File Name</label>
						<input type="text" class="form-control" id="fileName" name="fileName">
					</div>
					
					<div class="col-md-4 pb-3">
						<label for="videoTitle">Video Title</label>
						<input type="text" class="form-control" id="videoTitle" name="videoTitle">
					</div>
					<div class="col-md-4">
						<label for="keywords">Keywords</label>
						<input type="text" class="form-control" id="keywords" name="keywords">
					</div>
				</div>
				<div class="row" style="padding-top:15px">
					<div class="col-md-12">
						<label for="description">Description</label>
						<textarea class="form-control" id="description" name="description">
							
						</textarea>
					</div>
				</div>
				<div class="row" style="padding-top:15px">
					<div class="col-md-4">
						<label for="defaultVideo">Default Video</label>
						<input type="text" class="form-control" id="defaultVideo" name="defaultVideo">
					</div>

					<div class="col-md-4">
						<label for="duration">Duration ( As shown to user. )</label>
						<input type="text" class="form-control" id="duration" name="duration">
					</div>
					
					<div class="col-md-4">
						<label for="thumbnailUrl">Thumbnail URL</label>
						<input type="text" class="form-control" id="thumbnailUrl" name="thumbnailUrl">
					</div>
				</div>
				<div class="row" style="padding-top:15px">
					<div class="col-md-4">
						<label for="videoLink">Video Link</label>
						<input type="text" class="form-control" id="videoLink" name="videoLink">
					</div>

					<div class="col-md-4">
						<label for="mobileUrlHd">Mobile URL HD</label>
						<input type="text" class="form-control" id="mobileUrlHd" name="mobileUrlHd">
					</div>

					<div class="col-md-4">
						<label for="mobileUrlSd1">Mobile URL SD 1</label>
						<input type="text" class="form-control" id="mobileUrlSd1" name="mobileUrlSd1">
					</div>

					<div class="col-md-4">
						<label for="mobileUrlSd2">Mobile URL SD 2</label>
						<input type="text" class="form-control" id="mobileUrlSd2" name="mobileUrlSd2">
					</div>
				</div>
				<div class="row" style="padding-top:15px">
					<div class="col-md-4">
						<button class="btn btn-primary" type="submit">Submit</button>
					</div>
				</div>
			</form>
		</div>

		<br><br>

	<div class="container-fluid customTheme">	
		<legend> ${ tableTitle }</legend>
			<div class="clearfix"></div>
			<div>
		
				<table id="table" class="table table-striped table-bordered display compact" style="width:100%" style="overflow-x:scroll">
					<thead>
						<tr>
							<th>Sr.No</th>
							<th>Session Id</th>
							<th>Session Name </th>
							<th>Video Title </th>
							<th>File Name</th>
							<th>Faculty Name</th>
							<th>Keywords</th>
							<th>Description </th>
							<th>Default Video </th>
							<th>duration</th>
							<th>sessionDate</th>
							<th>addedOn</th>
							<th>addedBy</th>
							<th>createdBy</th>
							<th>createdDate</th>
							<th>lastModifiedDate</th>
							<th>lastModifiedBy</th>
							<th>videoLink</th>
							<th>thumbnailUrl</th>
							<th>mobileUrlHd</th>
							<th>mobileUrlSd1</th>
							<th>mobileUrlSd2</th>
							<th>Links</th>
						</tr>
					</thead>
					<tbody>
						<% int i = 0; %>
						<c:forEach items="${AllVideoContent}" var="thisVideoContent">	
							<tr>
								<% i++; %>
								<td> <%= i %> </td>
								<td>${ thisVideoContent.sessionId }</td>
								<td>${ thisVideoContent.sessionName }</td>
								<td>${ thisVideoContent.videoTitle }</td>
								<td>${ thisVideoContent.fileName }</td>
								<td>${ thisVideoContent.facultyName }</td>
								<td>${ thisVideoContent.keywords }</td>
								<td>${ thisVideoContent.description }</td>
								<td>${ thisVideoContent.defaultVideo }</td>
								<td>${ thisVideoContent.duration }</td>
								<td>${ thisVideoContent.sessionDate }</td>
								<td>${ thisVideoContent.addedOn }</td>
								<td>${ thisVideoContent.addedBy }</td>
								<td>${ thisVideoContent.createdBy }</td>
								<td>${ thisVideoContent.createdDate }</td>
								<td>${ thisVideoContent.lastModifiedDate }</td>
								<td>${ thisVideoContent.lastModifiedBy }</td>
								<td>${ thisVideoContent.videoLink }</td>
								<td>${ thisVideoContent.thumbnailUrl }</td>
								<td>${ thisVideoContent.mobileUrlHd }</td>
								<td>${ thisVideoContent.mobileUrlSd1 }</td>
								<td>${ thisVideoContent.mobileUrlSd2 }</td>
								<td>
									<a href="updateVideoContent?id=${ thisVideoContent.id }"> 
										<i class="fa fa-edit"></i>Update
									</a>
									<br>
									<a href="deleteVideoContent?id=${ thisVideoContent.id }"> 
										<i class="fa fa-trash"></i>Delete
									</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</section>
	<br><br><br><br>
	<jsp:include page="/views/adminCommon/footer.jsp" />

	<script src="assets/tinymce/tinymce.min.js"></script>
	
	
  	<script>
	  	tinymce.init({
	  		selector:'textarea', 
			height: 400,
			plugins: 'print preview fullpage searchreplace autolink directionality visualblocks visualchars fullscreen image link media template codesample table charmap hr pagebreak nonbreaking anchor insertdatetime advlist lists textcolor wordcount imagetools contextmenu colorpicker textpattern',
			toolbar: 'styleselect bold italic underline | forecolor backcolor | fontselect | fontsizeselect | alignleft aligncenter alignright | bullist numlist outdent indent | table ',
			fontsize_formats: "8pt 10pt 12pt 14pt 18pt 24pt 36pt"
	  	});
	  	
  	</script>
  	
	<script>
		function videoTypeChanged(){
			if( $("#videoTypeId").val() == "1" ){
				$("#sessionId").attr("disabled", true);
			}else{
				$("#sessionId").attr("disabled", false);
			}
		}
		$(document).ready(function () {
			var id = ${ id };
			if(id != 0){
				$("#id").val("${ id }");
				$.ajax({
					url: '/m/getVideoContent?id=' + id,
					type: 'get',
					dataType: 'json',
					contentType: 'application/json',
					success: function (result) {
						$.each(result, function(key, value){
							$('#' + key).val(value);
							if(tinyMCE.get(key) != null){
								tinyMCE.get(key).setContent(value);
							}
						});
					},
					error: function (xhr, resp, text) {
						console.log(xhr, resp, text);
					}
				});
			}
			
			
			$('#dataTable').DataTable();
	
			
			$.ajax({
				url: '/m/getAllSessionsWithoutVideoContent',
				type: 'get',
				dataType: 'json',
				contentType: 'application/json',
				success: function (result) {
					result.forEach(function(session){
						$('#sessionId').append('<option value="' + session.id + '">' + session.sessionName + ' | ' + session.facultyId + '</option>');
						
						$('#facultyId').val(session.facultyId);
					});
					<% if(request.getParameter("sessionId") != null){ %>
						$('#sessionId').val('<%= (String) request.getParameter("sessionId") %>');
					<% } %>
				},
				error: function (xhr, resp, text) {
					console.log(xhr, resp, text);
				}
			});
			
			$('form').submit(function (e) {
				e.preventDefault();
				var ddata = formDataToJSON();
	
				$.ajax({
					url: '${url}',
					type: 'post',
					dataType: 'json',
					contentType: 'application/json',
					data: ddata,
					success: function (result) {
						if (result.status == "success" || result.status == "1") {
							successMessage("Success");
							window.location.reload();
						} else {
							errorMessage(result.message);
						}
						return false;
					},
					error: function (xhr, resp, text) {
						console.log(xhr, resp, text);
					}
				});
			});
		});
	</script>
</body>


</html>