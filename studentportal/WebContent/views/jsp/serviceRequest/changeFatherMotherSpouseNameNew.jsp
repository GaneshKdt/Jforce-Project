<!DOCTYPE html>
<html lang="en">

<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Change Father/Mother/Spouse Name SR" name="title" />
</jsp:include>
<style>
.panel-default {
	margin-top: 1em;
}

.panel-heading {
	padding: 1em 1.6em 2em;
}

.panel-title {
	font-size: 1.05em;
}

.panel-body {
	padding-top: 1.2em;
	padding-left: 2em;
}

label {
	font-size: 0.85em;
}

em {
	color: #d2232a;
}

#supportingDocType {
	margin: 0.8em 0 0.4em 0;
}

#supportingDocType>ul {
	margin-bottom: 0;
}

.has-error {
	margin-bottom: 0.75em;
}

.help-block {
	font-size: 15px;
	font-weight: 600;
	margin-top: 0.4em;
	margin-bottom: 0;
}
/* Modal Box */
.modal-content {
	width: 80%;
	margin: auto;
}

.modal-header {
	padding: 1em;
}

.modal-header>.fa {
	float: left;
	margin-right: 0.25em;
	padding-top: 0.15em;
}

.modal-body {
	text-align: center;
	padding: 1em 0.5em 0.7em;
	overflow: hidden;
}

.modal-body-content {
	font-size: medium !important;
}

.modal-footer {
	display: flex;
	justify-content: center;
	border-top: hidden;
	padding: 0.4em;
}

.modal-footer>.modal-button {
	width: 20%;
	margin: 0.2em 0.5em 0.35em !important;
	font-size: 0.8rem;
	border-radius: 0.2em;
	padding: 0.5em 0;
}
</style>
<body>
	<jsp:include page="../common/headerDemo.jsp" />
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/studentportal/home">Student Zone</a></li>
					<li><a href="selectSRForm">Select Service Request</a></li>
					<li><a href="changeFatherMotherSpouseNameSRForm">Change
							Father/Mother/Spouse Name SR</a></li>
				</ul>
			</div>
		</div>
		<!-- breadcrumbs -->

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<div id="sticky-sidebar">
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu" />
					</jsp:include>
				</div>

				<div class="sz-content-wrapper examsPage">
					<jsp:include page="../common/studentInfoBar.jsp" />
					<div class="sz-content">

						<div class="card card-body">
							<div class="card-header">
								<h5 class="card-title fw-bold" style="color: #d2232a;">Change
									Father/Mother/Spouse Name Service Request</h5>
							</div>
							<div class="card-body">
								<jsp:include page="../common/messageDemo.jsp" />
								<form name="changeFaMoSpoNameForm"
									action="changeFatherMotherSpouseNameSR" method="post"
									enctype="multipart/form-data">

									<input type="hidden" id="sapid" name="sapid"
										value="${studentSapid}" />
									<div class="row mb-2">
										<div class="col-md-4 form-group">
											<label for="detailType" class="fw-bold">Choose the
												Detail you want to Update</label> <select class="form-select"
												id="detailType" name="detailType"
												onchange="changeFormElementAttr(this.value)" disabled>
												<option value="">Select an Option</option>
												<option value="fatherName">Change Father Name</option>
												<option value="motherName">Change Mother Name</option>
												<option value="husbandName">Change Spouse Name</option>
											</select>
										</div>
									</div>

									<div class="row">
										<div class="col-md-8 mb-2 form-group d-none "
											id="currentValueBlock">
											<label for="currentValue" class="fw-bold"></label> <input
												class="form-control " type="text" readonly id="currentValue"
												name="currentValue">
										</div>
									</div>

									<div class="row">
										<div class="col-md-8 mb-3 form-group d-none"
											id="updateValueBlock">
											<label for="updateValue" class="fw-bold"></label> <input
												class="form-control" type="text" id="updateValue"
												name="updateValue" onkeydown="validateKey(event)"
												onpaste="validatePaste(event)">
										</div>
									</div>

									<div class="row">
										<div class="col-md-6 mb-3 d-none" id="supportingDocBlock">
											<label for="supportingDocument" class="fw-bold">Upload
												Supporting Document: </label> <input class="form-control"
												type="file" id="supportingDocument"
												accept="image/jpeg, image/png, image/svg+xml, application/pdf, application/msword, 
		  												application/vnd.openxmlformats-officedocument.wordprocessingml.document, 
		  												application/zip, application/vnd.rar"
												name="supportingDocument" onchange="checkFile(event)">
										</div>
									</div>

									<div class="row col-md-6">
										<div class="form-label">
										<!--  data-bs-toggle="modal"
												data-bs-target="#messageModal" -->
											<button id="formSubmission" name="formSubmission"
												disabled class="btn btn-large btn-danger">Submit</button>
											<button id="reset" onclick="customFormReset(); return false;"
												class="btn btn-dark" disabled>Reset</button>
											<a id="cancel" href="selectSRForm" class="btn btn-dark">Cancel</a>
										</div>
									</div>
									<input type="hidden" id="device" name="device" value="WebApp" />
								</form>
							</div>
						</div>
					<div class="modal fade" id="messageModal" tabindex="-1"
							 aria-labelledby="messageModal">
							<div class="modal-dialog" role="document">
								<div class="modal-content">
									<div class="modal-header">
										
										<i id="errorIcon" class="fa-solid fa-exclamation"
											aria-hidden="true"></i> <i id="successIcon"
											class="fa-solid fa-check" aria-hidden="true"></i>
										<h4 id="modalTitle" class="modal-title"></h4>
										<button type="button" class="btn-close float-right" data-bs-dismiss="modal"  aria-label="Close"></button>
									</div>
									<div class="modal-body">
										<p class="modal-body-content"></p>
									</div>
									<div class="modal-footer">
										<button type="button" id="modalSave"
											class="modal-button btn btn-large btn-primary"
											onclick="srCheckConfirmation()">Proceed</button>
										<a class="modal-button btn btn-danger" data-bs-dismiss="modal">Close</a>
									</div>
								</div>
								
							</div>
						</div>  
				
						<!-- /.modal -->
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../common/footerDemo.jsp" />

	<script type="text/javascript"
		src="${pageContext.request.contextPath}/assets/js/serviceRequest/changeInFatherMotherSpouseName.js"></script>
</body>
</html>