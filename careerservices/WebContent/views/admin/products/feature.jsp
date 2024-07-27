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
			<% if(!(request.getAttribute("showFeatureTableOnly") != null && (boolean)request.getAttribute("showFeatureTableOnly"))){ %>
				<form style="padding:15px" class="form px-3" onsubmit="return false;">
					<div class="form-group col-12">
						<input type="hidden" class="form-control" id="featureId" name="featureId" required>
					</div>
		
					<div class="form-group col-12">
						<label for="featureName">Feature Name</label>
						<input type="text" class="form-control" id="featureName" name="featureName" required>
					</div>
		
					<div class="form-group col-12">
						<label for="featureDescription">Description</label>
						<textarea class="form-control" id="featureDescription" name="featureDescription" required>
						</textarea>
					</div>
		
					<div class="form-group col-12">
						<label for="validitySlow">Validity in Slow packages </label>
						<input type="number" class="form-control" id="validitySlow" name="validitySlow" required>
					</div>
		
					<div class="form-group col-12">
						<label for="validityNormal">Validity in Normal packages </label>
						<input type="number" class="form-control" id="validityNormal" name="validityNormal" required>
					</div>
		
					<div class="form-group col-12">
						<label for="validityFast">Validity in Fast packages </label>
						<input type="number" class="form-control" id="validityFast" name="validityFast" required>
					</div>
		
					<button class="btn btn-primary" type="submit">Submit form</button>
				</form>
				<div class="clearfix"></div>
			<% } %>
		</div>
		<legend>&nbsp; ${ tableHeader } </legend>

	<div class="container-fluid customTheme">	
		<legend> ${ tableTitle }</legend>
			<table id="dataTable" class="table table-striped table-bordered" style="width:100%">
				<thead>
					<tr>
						<th>Sr.No</th>
						<th>Feature Id</th>
						<th>Feature Name</th>
						<th>Description</th>
						<th>Validity Fast</th>
						<th>Validity Normal</th>
						<th>Validity Slow</th>
						<th>Links</th>
					</tr>
				</thead>
				<tbody>
					<% int i = 0; %>
					<c:forEach items="${AllFeatures}" var="feature">
						<tr>
							<% i++; %>
							<td><%= i %></td>
							<td>${ feature.featureId }</td>
							<td>${ feature.featureName }</td>
							<td>${ feature.featureDescription }</td>
							<td>${ feature.validityFast }</td>
							<td>${ feature.validityNormal }</td>
							<td>${ feature.validitySlow }</td>
							<td>
								<a href="updateFeature?featureId=${ feature.featureId }">
									<i class="fa fa-edit"></i> Edit
								</a>
								<br>
								<a
									onclick="confirmDelete(`Package Family Upgrade Path Relation`, `deleteFeature?featureId=${ feature.featureId } `);">
									<i class="fa fa-trash"></i> Delete
								</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		<br><br><br><br>
	</section>

	<jsp:include page="/views/adminCommon/footer.jsp" />

	<script src="assets/tinymce/js/tinymce/tinymce.min.js"></script>
	
	<script>tinymce.init({selector:'textarea'});</script>
</body>
<script>
	$(document).ready(function () {

		$("#featureId").val("${ Feature.featureId }");
		$("#featureName").val("${ Feature.featureName }");
		$("#featureDescription").val("${ Feature.featureDescription }");
		$("#validityFast").val("${ Feature.validityFast }");
		$("#validityNormal").val("${ Feature.validityNormal }");
		$("#validitySlow").val("${ Feature.validitySlow }");
		
		$(document).ready(function() {
			<jsp:include page="/views/adminCommon/datatables.jsp" />
		});
		
		$( 'form' ).submit(function ( e ) {
			var ddata = formDataToJSON();
			e.preventDefault();
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
				}
			});
		});
	});
</script>

</html>