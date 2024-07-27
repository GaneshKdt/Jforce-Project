<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value=" ${ title } " name="title" />
</jsp:include>


<body class="inside">

	<%@ include file="/views/adminCommon/header.jsp"%>
	<section class="content-container login">

		<div class="container-fluid customTheme">
			<div class="row">
				<legend>${ title }</legend>
			</div>
			<%@ include file="/views/common/messages.jsp"%>
		</div>

		<form class="form px-3" onsubmit="return false;">

			<div class="row">
				<div class="form-group">
					<input type="hidden" class="form-control" id="id" name="id">
				</div>
				
				<div class="form-group">
					<input type="hidden" class="form-control" id="entitlementId" name="entitlementId">
				</div>
				
				<div class="form-group">
					<% if(request.getAttribute("Update") != null && (boolean)request.getAttribute("Update")){ %>
						<input type="hidden" class="form-control" id="dependsOnFeatureId" name="dependsOnFeatureId">
					<% }else{ %>
						<div class="col-md-4 mb-3">
							<label for="FeatureId">Feature Id</label>
							<select class="form-control" id="dependsOnFeatureId" name="dependsOnFeatureId">
								<c:forEach items="${FeaturesNotDependedUpon}" var="feature">
									<option value="${ feature.featureId }">${ feature.featureName }</option>
								</c:forEach>
							</select>
						</div>
					<% } %>
				</div>
				
				<div class="col-md-4 mb-3">
					<div class="form-group">
						<label for="requiresCompletion">Requires Completion</label>
						<select class="form-control pl-2" id="requiresCompletion" name="requiresCompletion">
							<option value="false">No</option>
							<option value="true">Yes</option>
						</select>
					</div>
				</div>
	
				<div class="col-md-4 mb-3">
					<div class="form-group">
						<label for="monthsAfterCompletion">Months After Completion</label>
						<input type="number" class="form-control" id="monthsAfterCompletion" name="monthsAfterCompletion">
					</div>
				</div>
	
				<div class="col-md-4 mb-3">
					<div class="form-group">
						<label for="requiresActivationOnly">Requires Activation Only</label>
						<select class="form-control pl-2" id="requiresActivationOnly" name="requiresActivationOnly">
							<option value="false">No</option>
							<option value="true">Yes</option>
						</select>
					</div>
				</div>
				<div class="clearfix"></div>
				<div class="col-md-4 mb-3">
					<div class="form-group">
						<label for="monthsAfterActivation">Months After Activation</label>
						<input type="number" class="form-control" id="monthsAfterActivation" name="monthsAfterActivation">
					</div>
				</div>
	
				<div class="col-md-4 mb-3">
					<div class="form-group">
						<label for="activationsMinimumRequired">Activations Minimum Required</label>
						<input type="number" class="form-control" id="activationsMinimumRequired"
							name="activationsMinimumRequired">
					</div>
				</div>
				</div>

			<div class="clearfix"></div>
			<br>
			<div class="row">
				<div class="col-md-4">
					<div class="form-group">
						<button class="btn btn-primary" type="submit">Submit</button>
					</div>
				</div>
			</div>
			<br>
		</form> <br><br>

		<div class="clearfix"></div>
	<div class="container-fluid customTheme">	
		<legend> ${ tableTitle }</legend>
			<table id="dataTable" class="table table-striped table-bordered" style="width:100%">
				<thead>
					<tr>
						<th>Sr.No</th>
						<th>Feature Name </th>
						<th>Package Name </th>
						<th>Duration Type </th>
						<th>Requires Completion</th>
						<th>Months After Completion</th>
						<th>Requires Activation Only</th>
						<th>Months After Activation</th>
						<th>Activations Minimum Required</th>
						<th>Links</th>
					</tr>
				</thead>
				<tbody>
					<% int i = 0; %>
					<c:forEach items="${AllEntitlementDependencies}" var="dependency">
						<% i++; %>
						<tr>
							<td><%= i %></td>
							<td>${ dependency.featureName }</td>
							<td>${ dependency.packageName }</td>
							<td>${ dependency.durationType }</td>
							<td>${ dependency.requiresCompletion }</td>
							<td>${ dependency.monthsAfterCompletion }</td>
							<td>${ dependency.requiresActivationOnly }</td>
							<td>${ dependency.monthsAfterActivation }</td>
							<td>${ dependency.activationsMinimumRequired }</td>
							<td>
								<a href="updateEntitlementDependency?entitlementId=${ dependency.entitlementId }&dependencyId=${ dependency.id }">
									<i class="fa fa-edit"></i> Configure
								</a>
								<a href="deleteEntitlementDependency?entitlementId=${ dependency.entitlementId }&dependencyId=${ dependency.id }">
									<i class="fa fa-trash"></i> Delete
								</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

		</div>

	</section>
	<jsp:include page="/views/adminCommon/footer.jsp" />

	<script>
		$(document).ready(function() {
			<jsp:include page="/views/adminCommon/datatables.jsp" />
		});

		$(document).ready(function () {
			$("#id").val("${ Dependency.id }");
			$("#entitlementId").val("${ Dependency.entitlementId }");
			$("#dependsOnFeatureId").val("${ Dependency.dependsOnFeatureId }");
			$("#requiresCompletion").val("${ Dependency.requiresCompletion }");
			$("#monthsAfterCompletion").val("${ Dependency.monthsAfterCompletion }");
			$("#monthsAfterActivation").val("${ Dependency.monthsAfterActivation }");
			$("#activationsMinimumRequired").val("${ Dependency.activationsMinimumRequired }");

			$('#dataTable').DataTable();
		});

		$(document).ready(function () {

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
						if (result.status == "success") {
							successMessage("Success");
							window.location.reload();
						} else {
							errorMessage(result.message);
						}
						return false;
					},
					error: function (xhr, resp, text) {
						console.log(xhr, resp, text);
						errorMessage("Error. Please check all fields and retry!");
					}
				});
			});
		});
	</script>
</body>

</html>