<%@page import="com.nmims.beans.PackageRequirementsMasterMappingData"%>
<%@page import="java.util.List"%>
<%@page import="com.nmims.beans.PackageRequirementsMasterMapping"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="/views/adminCommon/jscss.jsp">
	<jsp:param value=" ${ title } " name="title" />
</jsp:include>

<style>
	.ms-drop input{
		width:auto
	}
	.ms-drop{
		width:auto;
		position: initial !important;
	}
	label {
		display: inline-block;
		max-width: 100%;
		margin-bottom: 5px;
		font-weight: 700;
	}

	.ms-drop input[type=checkbox] {
		margin: 4px 0 0;
		margin-top: 1px\9;
		line-height: normal;
	}

	.ms-drop input[type=checkbox] {
		-webkit-box-sizing: border-box;
		-moz-box-sizing: border-box;
		box-sizing: border-box;
		padding: 0;
	}
	.ms-drop button,.ms-drop input,.ms-drop select{
		font-family: inherit;
		font-size: inherit;
		line-height: inherit;
	}
	.ms-drop input {
		line-height: normal;
			height: auto;
	}
	.ms-drop button,.ms-drop input,.ms-drop optgroup, select {
		margin: 0;
		font: inherit;
		color: inherit;
	}
</style>
<link rel="stylesheet" href="resources_2015/multiple-select-wenzhixin/multiple-select.css">


<body class="inside">

<%@ include file="/views/adminCommon/header.jsp"%>

	<%
		boolean isEdit = "true".equals((String)request.getAttribute("edit"));
	%>
	<section class="content-container login">

		<div class="container-fluid customTheme">
				<div class="row">
					<legend> ${ title } </legend>
				</div>
			<%@ include file="/views/common/messages.jsp"%>

			<form style="padding:15px" class="form px-3" onsubmit="return false;">
				<input type="text" readonly hidden="hidden" name="requirementsId" id = "requirementsId">
				<input type="text" readonly hidden="hidden" name="packageId" id = "packageId">
				<div class="">

					<%@ include file="/views/adminCommon/consumerProgramStructure.jsp"%>

					<div class="col-12 row my-3">
						<div class="form-group">
							<div class="col-md-4">
								<label for="availableForAlumni">Available For Alumni?</label>
								<select class="form-control pl-2" id="availableForAlumni" name="availableForAlumni">
									<option value="false">No</option>
									<option value="true">Yes</option>
								</select>
							</div>
							<div class="col-md-4">
								<label for="availableForAlumni">Available To Alumni Only?</label>
								<select class="form-control pl-2" id="availableForAlumniOnly" name="availableForAlumniOnly">
									<option value="false">No</option>
									<option value="true">Yes</option>
								</select>
							</div>
							<div class="col-md-4">
								<label for="alumniMaxMonthsAfterLastRegistration">Till how many years after graduation can alumni register for this package</label>
								<input type="number" min="0" class="form-control pl-2" id="alumniMaxMonthsAfterLastRegistration" name="alumniMaxMonthsAfterLastRegistration">
							</div>
						</div>
					</div>
					<div class="col-12 row my-3">
						<div class="form-group col-md-6">
							<label for="requiredSemMin">Minimum semester required</label>
							<input type="number" min="0" max="4" class="form-control pl-2" id="requiredSemMin" name="requiredSemMin">
						</div>
						<div class="form-group col-md-6">
							<label for="requiredSemMax">Maximum semester required</label>
							<input type="number" min="0" max="4" class="form-control pl-2" id="requiredSemMax" name="requiredSemMax">
						</div>
					</div>
					<div class="col-12 row my-3">
						<div class="form-group col-md-6">
							<label for="minSubjectsClearedPerSem">Minimum number of subjects cleared(per semester)</label>
							<input type="number" min="0" class="form-control pl-2" id="minSubjectsClearedPerSem" name="minSubjectsClearedPerSem">
						</div>
						<div class="form-group col-md-6">
							<label for="minSubjectsClearedTotal">Minimum number of subjects cleared(total)</label>
							<input type="number" min="0" class="form-control pl-2" id="minSubjectsClearedTotal" name="minSubjectsClearedTotal">
						</div>
					</div>
					<button type="submit" class="btn btn-primary">Submit</button>
				</div>
			</form>
		</div>

		<div class="container-fluid customTheme">
			<legend> ${ tableTitle }</legend>

			<div style="overflow-x:scroll">

				<table id="table" class="table table-striped table-bordered display compact" style="width:100%">
					<thead>
						<tr>
							<th>Sr.No</th>
							<th>Requirements Id</th>
							<th>Package Name</th>
							<th>Master Data Id</th>
							<th>Program Code</th>
							<th>Program Name</th>
							<th>Program Structure</th>
							<th>Consumer Type</th>
							<th>Available for alumni</th>
							<th>Available for alumni Only</th>
							<th>Alumni max years after graduation</th>
							<th>Minimum Semester</th>
							<th>Maximum Semester</th>
							<th>Minimum Subjects Cleared Per Semester</th>
							<th>Minimum Subjects Cleared Total</th>
							<th>Links</th>
						</tr>
					</thead>
					<tbody>
						<% int i = 0; %>
						<c:forEach items="${AllPackageRequirements}" var="thisPackageRequirements">
							<tr>
								<% i++; %>
								<td> <%= i %> </td>
								<td>${ thisPackageRequirements.requirementsId } </td>
								<td><a href="updatePackage?packageId=${ thisPackageRequirements.packageId }">${ thisPackageRequirements.packageName }</a> </td>
								<td>${ thisPackageRequirements.consumerProgramStructureId } </td>
								<td>${ thisPackageRequirements.programCode } </td>
								<td>${ thisPackageRequirements.programName } </td>
								<td>${ thisPackageRequirements.programStructureName } </td>
								<td>${ thisPackageRequirements.consumerType } </td>
								<td>${ thisPackageRequirements.availableForAlumni } </td>
								<td>${ thisPackageRequirements.availableForAlumniOnly } </td>
								<td>${ thisPackageRequirements.alumniMaxMonthsAfterLastRegistration } </td>
								<td>${ thisPackageRequirements.requiredSemMin } </td>
								<td>${ thisPackageRequirements.requiredSemMax } </td>
								<td>${ thisPackageRequirements.minSubjectsClearedPerSem } </td>
								<td>${ thisPackageRequirements.minSubjectsClearedTotal } </td>
								<td>
									<a href="updatePackageRequirements?requirementsId=${ thisPackageRequirements.requirementsId }">
										<i class="fa fa-edit"></i> Edit
									</a><br>
									<a href="addPackageRequirements?packageId=${ thisPackageRequirements.packageId }">
										<i class="fa fa-plus"></i> Add more requirements for this id
									</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</section>
	<jsp:include page="/views/adminCommon/footer.jsp" />
	<jsp:include page="/views/adminCommon/consumerProgramStructureJs.jsp" />
	<script>
		$( document ).ready(function() {
			$("#requirementsId").val("${ Requirements.requirementsId }");
			$("#packageId").val("${ Requirements.packageId }");
			$("#requiredSemMin").val("${ Requirements.requiredSemMin }");
			$("#requiredSemMax").val("${ Requirements.requiredSemMax }");
			$("#minSubjectsClearedTotal").val("${ Requirements.minSubjectsClearedTotal }");
			$("#minSubjectsClearedPerSem").val("${ Requirements.minSubjectsClearedPerSem }");
			$("#availableForAlumni").val("${ Requirements.availableForAlumni }");
			$("#availableForAlumniOnly").val("${ Requirements.availableForAlumniOnly }");
			$("#alumniMaxMonthsAfterLastRegistration").val("${ Requirements.alumniMaxMonthsAfterLastRegistration }");

			$( 'form' ).submit(function ( e ) {
				e.preventDefault();
				if($("#requiredSemMin").val() > $("#requiredSemMax").val()){
					errorMessage("Minimum semester cant be greated than maximum semester!");
					return false;
				}

				//Prepare data for posting
				var myFormData = $('form').serializeArray();

				//convert to boolean
				for(var k in myFormData){
					if(myFormData[k].value == "true"){
						myFormData[k].value = true;
					}
					if(myFormData[k].value == "false"){
						myFormData[k].value = false;
					}
				}
				var programIds = [];
				var data = {};

				//loop through and create an array
				myFormData.map(function(x){
					//for every programId[] add it to a seperate array
					//this is done so request mapping can directly consume this array as a "consumerProgramStructureId" List
					if(x.name=="programId[]"){
						programIds.push(x.value);
					}else{
						//every other element is directly added to the data array
						data[x.name] = x.value;
					}
				});
				data["consumerProgramStructureId"] = programIds

				//convert data array into json for post
				var ddata = JSON.stringify(data);


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
										errorMessage(result.error);
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
	var consumerProgramStructureDetails = ${ ConsumerProgramStructureDetails };
	var consumerTypeDetails = ${ ConsumerTypes };
	var programStructureDetails = ${ ProgramStructures };
	<%
		PackageRequirementsMasterMapping masterMapping = (PackageRequirementsMasterMapping)request.getAttribute("Requirements");

		String listOfStrings = "'";

		if(masterMapping.getConsumerProgramStructureMappingData() != null && masterMapping.getConsumerProgramStructureMappingData().size() > 0 ){
			List<PackageRequirementsMasterMappingData> requirementsMasterMappingDatas = masterMapping.getConsumerProgramStructureMappingData();
			for(PackageRequirementsMasterMappingData data: requirementsMasterMappingDatas){
				listOfStrings += data.getConsumerProgramStructureId() + "','";
			}
		}

		listOfStrings += "'";
	%>
	var initiallySelectedItems = [ <%= listOfStrings %> ];

	$(document).ready(function() {
		<jsp:include page="/views/adminCommon/datatables.jsp" />
	});

	function confirmDelete(name,url){
		if (confirm('Are you sure you want to delete this' + name + '?')) {
		// Save it!
		} else {
			window.location.href=url;
		}

	}
</script>

</html>
