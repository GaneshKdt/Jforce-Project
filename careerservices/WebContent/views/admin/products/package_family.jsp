<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value=" ${ title } " name="title" />
</jsp:include>

<style>
	.customTheme li{
		list-style: inherit;
	}
</style>

<body class="inside">

<%@ include file="/views/adminCommon/header.jsp"%>
	<%
		boolean isEdit = "true".equals((String)request.getAttribute("edit"));
	%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row"> <legend> ${ title } </legend></div>
			<%@ include file="/views/common/messages.jsp"%>
				<form style="padding:15px" class="form px-3" onsubmit="return false;">
				<input type="text" readonly hidden="hidden" name="familyId" value="${ Family.familyId }">
				<div class="clearfix"></div>
				<div class="col-12">				
					<div class="form-group col-12">
						<label for="familyName">Family Name</label>
						<input type="text" class="form-control pl-2" id="familyName" name="familyName">
					</div>
				</div>
				<div class="col-12">				
					<div class="form-group col-12">
						<label for="descriptionShort">Short Description</label>
						<textarea class="form-control pl-2" id="descriptionShort" name="descriptionShort">
							${ Family.descriptionShort }
						</textarea>
					</div>
				</div>
				<div class="col-12">				
					<div class="form-group col-12">
						<label for="description">Description</label>
						<textarea class="form-control pl-2" id="description" name="description">
							${ Family.description }
						</textarea>
					</div>
				</div>
				<div class="col-12">				
					<div class="form-group col-12">
						<label for="keyHighlights">Key Highlights</label>
						<textarea class="form-control pl-2" id="keyHighlights" name="keyHighlights">
							${ Family.keyHighlights }
						</textarea>
					</div>
				</div>
				<div class="col-12">				
					<div class="form-group col-12">
						<label for="eligibilityCriteria">Eligibility Criteria</label>
						<textarea class="form-control pl-2" id="eligibilityCriteria" name="eligibilityCriteria">
							${ Family.eligibilityCriteria }
						</textarea>
					</div>
				</div>
				<div class="col-12">				
					<div class="form-group col-12">
						<label for="componentEligibilityCriteria">Component Eligibility Criteria and Timeline</label>
						<textarea class="form-control pl-2" id="componentEligibilityCriteria" name="componentEligibilityCriteria">
							${ Family.componentEligibilityCriteria }
						</textarea>
					</div>
				</div>
				<button type="submit" class="btn btn-primary">Submit</button>
			</form>
		</div>
		<div class="container-fluid customTheme">	
			<legend> ${ tableTitle }</legend>
			<table id="table" class="table table-striped table-bordered" style="width:100%">
				<thead>
					<tr>
						<th>Sr.No</th>
						<th>Family Id</th>
						<th>Family Name</th>
						<th>Total Packages</th>
						<th>Links</th>
					</tr>
				</thead>
				<tbody>
				<% int i = 0; %>
					<c:forEach items="${AllFamilyData}" var="thisFamily">	
						<tr>
							<% i++; %>
							<td> <%= i %></td>
							<td>${ thisFamily.familyId }</td>
							<td><a href="updatePackageFamily?familyId=${ thisFamily.familyId }">${ thisFamily.familyName }</a></td>
							<td><a href="addPackage">${ thisFamily.numberOfPackages}</a></td>
							<td><a href="updatePackageFamily?familyId=${ thisFamily.familyId }"><i class="fa fa-edit"></i> Edit</a> | <a onclick="confirmDelete(`packageFamily`, `deletePackageFamily?familyId=${ thisFamily.familyId } `, '${ thisFamily.familyId }');"><i class="fa fa-trash"></i> Delete</a></td>
							
						</tr>
						
					</c:forEach>
					
				</tbody>
			</table>
		</div>
	</section>
	<jsp:include page="/views/adminCommon/footer.jsp" />
	<script src="/assets/tinymce/tinymce.min.js"></script>

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
		$( document ).ready(function() {
			$("#familyName").val("${ Family.familyName }");
			
			
			$( 'form' ).submit(function ( e ) {
				e.preventDefault();
				tinyMCE.triggerSave();
				var ddata = formDataToJSON();
				$.ajax({
					url: '${url}',
					type: 'post',
					dataType : 'json',
					contentType: 'application/json',
					data: ddata, 
					success : function(result) {
									if (result.status == "success") {
										successMessage("Success");
										window.location.reload();
									} else {
										errorMessage(result.message);
									}
									return false;
								},
					error: function(xhr, resp, text) {
						console.log(xhr, resp, text);
						errorMessage("Error. Please check all fields and retry!");
					}
				});
			});
		});
	</script>
</body>
	<script>
		$(document).ready(function() {
			<jsp:include page="/views/adminCommon/datatables.jsp" />
		});
		
	</script>

</html>