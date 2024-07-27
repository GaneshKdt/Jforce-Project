<!DOCTYPE html>
<html lang="en">

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Display Student Details" name="title" />
</jsp:include>

<style>
	.sz-main-content {
		min-height: 80%;
	}
	.panel-default {
		margin-top: 1em;
	}
	.panel-heading {
		padding: 1em 1.6em;
	}
	.pageHead {
		padding-bottom: 2em;
	}
	[role=tab] {
		border-left: 0.5em solid #9fc9c5 !important;
	}
	.panel-body {
		padding-left: 2em;
		padding-right: 2em;
	}
	.formLabel {
		font-size: 1em;
	}
	h2 {
		margin: 0.5em;
		font-size: 1.2rem;
	}
	input {
		max-width: 20em;
	}
	.panel-group {
		margin-bottom: 0.5em;
	}
	.titleIcon {
		float: right;
		color: #26a9e0;
    	font-size: 1em;
	}
	
	/*html table css properties*/
	.table {
		min-width: 25em;
		width: auto;
	}
	.table thead {
		background: linear-gradient(#49708f, #293f50);
	}
	.fieldName {
		color: #395870;
		display: block;
	}
	.table > thead > tr > th {
		color: #fff;
    	padding: 0.6em;
    }
    .table > thead > tr:hover {
		background: #395870 !important;
    }
	.table > tbody > tr > td {
		padding: 0.5em;
	}
</style>
<body>
	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
   			<div class="container-fluid">
       			<ul class="sz-breadcrumbs">
	        		<li><a href="/studentportal/home">Search</a></li> 
	        		<li><a href="/exam/admin/searchStudentsForm">Search Students</a></li>
	        		<li><a href="/exam/admin/editStudent?sapid=${studentSapid}&sem=${studentSem}" class="encodedHref">Edit Student Details</a></li>
	        		<li><a href="/exam/admin/viewStudent/${studentSapid}/${studentSem}" class="encodedHref">Display Student Details</a></li>
		        </ul>
          	</div>
        </div>
	</div>

	<div class="sz-main-content menu-closed">
		<div class="sz-main-content-inner">
			<jsp:include page="adminCommon/left-sidebar.jsp">
				<jsp:param value="" name="activeMenu" />
			</jsp:include>
			

			<div class="sz-content-wrapper examsPage">
				<%@ include file="adminCommon/adminInfoBar.jsp"%>
				<div class="sz-content">
				
					<div class="panel panel-default">
						<div class="panel-heading pageHead">
							<h2 class="panel-title red text-capitalize">Student Details</h2>
						</div>

						<div class="panel-body">
							<c:if test="${not empty statusMessage}">
							    <div class="alert alert-${status} alert-dismissible" role="alert">
									<button type="button" class="close" data-dismiss="alert" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<strong>${statusMessage}</strong>
							    </div>
							</c:if><!-- Displays the success/error message -->
							
							<div class="row">
								<div class="col-md-4">
									<label class="formLabel" for="sapid">Student No</label>
									<input type="text" id="sapid" name="sapid" disabled 
   										class="form-control" value="${studentSapid}" />
								</div>
								
								<div class="col-md-4">
									<label class="formLabel" for="sem">Enrolled Sem</label>
									<input type="text" id="sem" name="sem" disabled 
   										class="form-control" value="${studentSem}" />
								</div>
							</div>
							
							<div class="row">
								<div class="col-md-8">
									<!--Accordion wrapper-->
									<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
									  <!-- Accordion card -->
									  <div class="panel panel-default">
									    <!-- Accordion Card header -->
									    <div class="panel-heading" role="tab" id="personalInformationHeading">
									      <h4 class="panel-title">
									        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
									          Personal Information<i class="fa fa-arrow-down titleIcon" aria-hidden="true"></i>
									        </a>
									      </h4>
									    </div>
									    <!-- Accordion Card body -->
									    <div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="personalInformationHeading">
									      <div class="panel-body table-responsive">
									        <table class="table table-bordered table-hover">
									          <thead>
												  <tr>
												    <th scope="col">Field Name</th>
												    <th scope="col">Field Value</th>
												  </tr>
											  </thead>
											  <tbody>
												  <tr>
												    <td>
												    	<strong class="fieldName">First Name</strong>
												    </td>
												    <td>${studentDetails.firstName}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Middle Name</strong>
												    </td>
												    <td>${studentDetails.middleName}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Last Name</strong>
												    </td>
												    <td>${studentDetails.lastName}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Father First Name</strong>
												    </td>
												    <td>${studentDetails.fatherName}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Mother First Name</strong>
												    </td>
												    <td>${studentDetails.motherName}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Spouse Name</strong>
												    </td>
												    <td>${studentDetails.spouseName}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Gender</strong>
												    </td>
												    <td>${studentDetails.gender}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Date Of Birth</strong>
												    </td>
												    <td>${studentDetails.dob}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Age</strong>
												    </td>
												    <td>${studentDetails.age}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Student Image URL</strong>
												    </td>
												    <td>${studentDetails.imageUrl}</td>
												  </tr>
											  </tbody>
											</table>
									      </div>
									    </div>
									  </div>
									  <!-- Accordion card -->
									  <div class="panel panel-default">
									    <!-- Accordion Card header -->
									    <div class="panel-heading" role="tab" id="contactInformationHeading">
									      <h4 class="panel-title">
									        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
									          Contact Information<i class="fa fa-arrow-down titleIcon" aria-hidden="true"></i>
									        </a>
									      </h4>
									    </div>
									    <!-- Accordion Card body -->
									    <div id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="contactInformationHeading">
									      <div class="panel-body table-responsive">
									        <table class="table table-bordered table-hover">
											  <thead>
												  <tr>
												    <th scope="col">Field Name</th>
												    <th scope="col">Field Value</th>
												  </tr>
											  </thead>
											  <tbody>
												  <tr>
												    <td>
												    	<strong class="fieldName">Email Address</strong>
												    </td>
												    <td>${studentDetails.emailId}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Mobile Number</strong>
												    </td>
												    <td>${studentDetails.mobile}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Alt Phone Number</strong>
												    </td>
												    <td>${studentDetails.altPhone}</td>
												  </tr>
											  </tbody>
											 </table>
									      </div>
									    </div>
									  </div>
									  <!-- Accordion card -->
									  <div class="panel panel-default">
									    <!-- Accordion Card header -->
									    <div class="panel-heading" role="tab" id="addressInformationHeading">
									      <h4 class="panel-title">
									        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseThree" aria-expanded="false" aria-controls="collapseThree">
									          Address Information<i class="fa fa-arrow-down titleIcon" aria-hidden="true"></i>
									        </a>
									      </h4>
									    </div>
									    <!-- Accordion Card body -->
									    <div id="collapseThree" class="panel-collapse collapse" role="tabpanel" aria-labelledby="addressInformationHeading">
									      <div class="panel-body table-responsive">
									        <table class="table table-bordered table-hover">
											  <thead>
												  <tr>
												    <th scope="col">Field Name</th>
												    <th scope="col">Field Value</th>
												  </tr>
											  </thead>
											  <tbody>
												  <tr>
												    <td>
												    	<strong class="fieldName">Address Line 1 (House No. Name)</strong>
												    </td>
												    <td>${studentDetails.addressLine1}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Address Line 2 (Street)</strong>
												    </td>
												    <td>${studentDetails.addressLine2}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">LandMark</strong>
												    </td>
												    <td>${studentDetails.landMark}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Address Line 3 (Locality)</strong>
												    </td>
												    <td>${studentDetails.addressLine3}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Pin Code</strong>
												    </td>
												    <td>${studentDetails.pin}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">City</strong>
												    </td>
												    <td>${studentDetails.city}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">State</strong>
												    </td>
												    <td>${studentDetails.state}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Country</strong>
												    </td>
												    <td>${studentDetails.country}</td>
												  </tr>
											  </tbody>
											 </table>
									      </div>
									    </div>
									  </div>
									  <!-- Accordion card -->
									  <div class="panel panel-default">
									    <!-- Accordion Card header -->
									    <div class="panel-heading" role="tab" id="centerDetailsHeading">
									      <h4 class="panel-title">
									        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFour" aria-expanded="true" aria-controls="collapseFour">
									          Center Details<i class="fa fa-arrow-down titleIcon" aria-hidden="true"></i>
									        </a>
									      </h4>
									    </div>
									    <!-- Accordion Card body -->
									    <div id="collapseFour" class="panel-collapse collapse" role="tabpanel" aria-labelledby="centerDetailsHeading">
									      <div class="panel-body table-responsive">
									        <table class="table table-bordered table-hover">
											  <thead>
												  <tr>
												    <th scope="col">Field Name</th>
												    <th scope="col">Field Value</th>
												  </tr>
											  </thead>
											  <tbody>
												  <tr>
												    <td>
												    	<strong class="fieldName">Center Code</strong>
												    </td>
												    <td>${studentDetails.centerCode}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Center Name</strong>
												    </td>
												    <td>${studentDetails.centerName}</td>
												  </tr>
											  </tbody>
											 </table>
									      </div>
									    </div>
									  </div>
									  <!-- Accordion card -->
									  <div class="panel panel-default">
									    <!-- Accordion Card header -->
									    <div class="panel-heading" role="tab" id="enrollValidityInformationHeading">
									      <h4 class="panel-title">
									        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseFive" aria-expanded="false" aria-controls="collapseFive">
									          Enrollment / Validity Information<i class="fa fa-arrow-down titleIcon" aria-hidden="true"></i>
									        </a>
									      </h4>
									    </div>
									    <!-- Accordion Card body -->
									    <div id="collapseFive" class="panel-collapse collapse" role="tabpanel" aria-labelledby="enrollValidityInformationHeading">
									      <div class="panel-body table-responsive">
									        <table class="table table-bordered table-hover">
											  <thead>
												  <tr>
												    <th scope="col">Field Name</th>
												    <th scope="col">Field Value</th>
												  </tr>
											  </thead>
											  <tbody>
												  <tr>
												    <td>
												    	<strong class="fieldName">Enrollment Month</strong>
												    </td>
												    <td>${studentDetails.enrollmentMonth}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Enrollment Year</strong>
												    </td>
												    <td>${studentDetails.enrollmentYear}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Validity End Month</strong>
												    </td>
												    <td>${studentDetails.validityEndMonth}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Validity End Year</strong>
												    </td>
												    <td>${studentDetails.validityEndYear}</td>
												  </tr>
											  </tbody>
											 </table>
									      </div>
									    </div>
									  </div>
									  <!-- Accordion card -->
									  <div class="panel panel-default">
									    <!-- Accordion Card header -->
									    <div class="panel-heading" role="tab" id="programDetailsHeading">
									      <h4 class="panel-title">
									        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSix" aria-expanded="false" aria-controls="collapseSix">
									          Program Details<i class="fa fa-arrow-down titleIcon" aria-hidden="true"></i>
									        </a>
									      </h4>
									    </div>
									    <!-- Accordion Card body -->
									    <div id="collapseSix" class="panel-collapse collapse" role="tabpanel" aria-labelledby="programDetailsHeading">
									      <div class="panel-body table-responsive">
									        <table class="table table-bordered table-hover">
											  <thead>
												  <tr>
												    <th scope="col">Field Name</th>
												    <th scope="col">Field Value</th>
												  </tr>
											  </thead>
											  <tbody>
												  <tr>
												    <td>
												    	<strong class="fieldName">Program</strong>
												    </td>
												    <td>${studentDetails.program}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Program Structure</strong>
												    </td>
												    <td>${studentDetails.programStructure}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Program Changed</strong>
												    </td>
												    <td>${studentDetails.programChanged}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Old Program</strong>
												    </td>
												    <td>${studentDetails.oldProgram}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Program Cleared</strong>
												    </td>
												    <td>${studentDetails.programCleared}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Program Status</strong>
												    </td>
												    <td>${studentDetails.programStatus}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Program Remarks</strong>
												    </td>
												    <td>${studentDetails.programRemarks}</td>
												  </tr>
											  </tbody>
											 </table>
									      </div>
									    </div>
									  </div>
									  <!-- Accordion card -->
									  <div class="panel panel-default">
									    <!-- Accordion Card header -->
									    <div class="panel-heading" role="tab" id="workEduInformationHeading">
									      <h4 class="panel-title">
									        <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseSeven" aria-expanded="false" aria-controls="collapseSeven">
									          Work / Education Information<i class="fa fa-arrow-down titleIcon" aria-hidden="true"></i>
									        </a>
									      </h4>
									    </div>
									    <!-- Accordion Card body -->
									    <div id="collapseSeven" class="panel-collapse collapse" role="tabpanel" aria-labelledby="workEduInformationHeading">
									      <div class="panel-body table-responsive">
									        <table class="table table-bordered table-hover">
											  <thead>
												  <tr>
												    <th scope="col">Field Name</th>
												    <th scope="col">Field Value</th>
												  </tr>
											  </thead>
											  <tbody>
												  <tr>
												    <td>
												    	<strong class="fieldName">Highest Qualification</strong>
												    </td>
												    <td>${studentDetails.highestQualification}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Industry</strong>
												    </td>
												    <td>${studentDetails.industry}</td>
												  </tr>
												  <tr>
												    <td>
												    	<strong class="fieldName">Designation</strong>
												    </td>
												    <td>${studentDetails.designation}</td>
												  </tr>
											  </tbody>
											 </table>
									      </div>
									    </div>
									  </div>
									</div>
								</div>
							</div>
							<div class="row col-md-6">
								<a id="editButton" href="/exam/admin/editStudent?sapid=${studentSapid}&sem=${studentSem}"
									class="btn btn-danger">Edit Student Details</a>
								<a id="homeBtn" href="${pageContext.request.contextPath}/home"
									class="btn btn-large btn-primary">Go To Home</a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="adminCommon/footer.jsp" />

<script type="text/javascript">
	/*
	After the DOM is loaded the function executes
	*/
	document.addEventListener("DOMContentLoaded", function() {
		programDetailsTableValuesConversion();
	});

	/*
		Convert the abbreviated values present in the Program Details block into full-form texts
	*/
	function programDetailsTableValuesConversion() {
		const programDetailsDiv = document.getElementById("collapseSix");
		const programDetailsTable = programDetailsDiv.getElementsByTagName("table")[0];
		const tableBody = programDetailsTable.getElementsByTagName("tbody")[0];

		programChangedValueConversion(tableBody.rows[2].cells[1]);		//Cell of the Program Changed field value
		programClearedValueConversion(tableBody.rows[4].cells[1]);		//Cell of the Program Cleared field value
		programStatusValueConversion(tableBody.rows[5].cells[1]);		//Cell of the Program Status field value
	}

	/*
		The abbreviated option values of the Program Changed select tag are modified to full-forms
	*/
	function programChangedValueConversion(cellValue) {
		const programChanged = cellValue.textContent;
		if(programChanged === "Y")
			cellValue.textContent = "Yes";
		else
			cellValue.textContent = "No";
	}

	/*
		The abbreviated option values of the Program Cleared select tag are modified to full-forms
	*/
	function programClearedValueConversion(cellValue) {
		const programCleared = cellValue.textContent;
		if(programCleared === "Y")
			cellValue.textContent = "Yes";
		else if(programCleared === "N")
			cellValue.textContent = "No";
	}

	/*
		The option values of the Program Status select tag are modified with a clearer status
	*/
	function programStatusValueConversion(cellValue) {
		const programStatus = cellValue.textContent;
		switch(programStatus) {
			case "Program Suspension":
				cellValue.textContent = "Program Suspension";
				break;
			case "Program Terminated":
				cellValue.textContent = "Program Terminated (Admission Cancelled)";
				break;
			case "Program Withdrawal":
				cellValue.textContent = "Program Withdrawal (De-Registered)";
				break;
			default:
				cellValue.textContent = "Program Active (Confirmed)";
		}
	}
</script>
</body>
</html>
