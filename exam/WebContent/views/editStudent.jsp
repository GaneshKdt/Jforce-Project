<!DOCTYPE html>
<html lang="en">

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Edit Student Details" name="title" />
</jsp:include>
<style>
	.panel-default {
		margin-top: 1em;
	}
	.panel-heading {
		padding: 1em 1.6em 2em;
	}
	.panel-body {
		padding-left: 2em;
	}
	.formLabel {
		font-size: 1em;
	}
	h2 {
		margin: 0.5em;
	}
	input, select, textarea {
		max-width: 25em;		/*As max width is set to 300px for Select tags in bootstrap, assigning same width to all tags used in form for consistency*/
	}
	select[readonly] {
    	pointer-events: none;	/*Pointer events none on select tag with readonly attribute to disallow user from accessing the select tag options*/
	}
	.disableLink {
	  pointer-events: none;
	  cursor: default;
	  opacity: 0.5;
	}
	@media only screen and (min-width: 992px) {
		.sapidDiv {
			margin-left: 2.5em;
		}
	}
	.help-block {
		display: none;
		padding-left: 15px;
	    margin-bottom: 20px;
	    font-weight: 600;
	}
	#studentImage { 
	    font-family: "FontAwesome";
	    font-size: 4em;
	    width: 2em;
	    height: 2em;
	    display: block;
	    border: 3px solid #ccc;
	    border-radius: 50%;
	    margin: 0 auto;
	    text-align: center;
	    line-height: 1.8em;
	    background: #eee;
    	object-fit: none;
    	opacity: 1;
    	transition: .5s ease;
		backface-visibility: hidden;
	}
	.imageView {
		display: none;
		transition: .5s ease;
		opacity: 0;
		position: absolute;
		top: 55%;
		left: 50%;
		transform: translate(-50%, -50%);
		-ms-transform: translate(-50%, -50%);
		text-align: center;
	}
	.hoverBlock:hover #studentImage {
		opacity: 0.3;
		border: 3px solid #242424;
	}
	.hoverBlock:hover .imageView {
		opacity: 1;
	}
	.view {
		font-size: 2.5em;
	}
	/* Image View Modal */
	.modal-header {
	    border: 0;
    	padding: 0.6em 0.8em;
	}
	.modal-body {
		margin: 0 0.6em;
		border: 5px solid #404041;
	    padding: 0;
    	-ms-overflow-style: none;		/* Hide scrollbar (but keep functionality) IE and Edge */
		scrollbar-width: none;		/* Hide scrollbar (but keep functionality) Firefox */
	}
	.modal-body::-webkit-scrollbar {
		display: none;		/* Hide scrollbar (but keep functionality) for Chrome, Safari and Opera */
	}
	.modal-body-content {
		font-family: "FontAwesome";
		height: 100%;
		width: 100%;
	}
	.modal-footer {
	    display: flex;
	    justify-content: center;
	    border-top: hidden;
	    padding: 0.4em;
	}
 	.modal-footer > .modal-button { 
	    width: 20%;
	    margin: 0.2em 0.5em !important;
	    font-size: 0.8rem;
	    border-radius: 0.2em;
	    padding: 0.5em 0;
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
		        </ul>
          	</div>
        </div>
	</div><!-- breadcrumbs -->

	<div class="sz-main-content menu-closed">
		<div class="sz-main-content-inner">
			<jsp:include page="adminCommon/left-sidebar.jsp">
				<jsp:param value="" name="activeMenu" />
			</jsp:include>


			<div class="sz-content-wrapper examsPage">
				<%@ include file="adminCommon/adminInfoBar.jsp"%>
				<div class="sz-content">

					<div class="panel panel-default">
						<div class="panel-heading">
							<h2 class="panel-title red text-capitalize">Edit Student Details</h2>
						</div>
					
						<div class="panel-body">
							<%@ include file="adminCommon/messages.jsp"%>
							<form id="editStudentDetailsForm" action="updateStudent" onsubmit="return validateForm()" method="post">
								
								<div class="row">	
									<div class="col-md-2 form-group" id="studentImageDiv">
										<label class="formLabel" for="studentImage">Student Image</label>
										<img id="studentImage" src="../resources_2015/images/userImg.jpg" alt="&#xf1c5;">
										<input type="hidden" id="imageUrl" name="imageUrl" value="" />
										<div class="imageView" title="View Student Image">
											<span class="view" onclick="showImageModal()"><i class="fa fa-eye" aria-hidden="true"></i></span>
										</div>
									</div>
									
									<div class="col-md-4 form-group sapidDiv">
										<label class="formLabel" for="sapid">Student No</label>
										<input type="text" id="sapid" name="sapid" readonly 
	   										class="form-control" value="${studentSapid}" required />
									</div>
									
									<div class="col-md-4 form-group">
										<label class="formLabel" for="sem">Enrolled Sem</label>
										<input type="text" id="sem" name="sem" readonly 
	   										class="form-control" value="${studentSem}" required />
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="firstName">First Name</label>
										<input type="text" id="firstName" name="firstName" disabled 
	   										class="form-control" placeholder="Enter First Name" required /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="middleName">Middle Name</label>
										<input type="text" id="middleName" name="middleName" disabled 
	   										class="form-control" placeholder="Enter Middle Name" /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="lastName">Last Name</label>
										<input type="text" id="lastName" name="lastName" disabled 
	   										class="form-control" placeholder="Enter Last Name" /> 
									</div>
								</div>
								
								<div class="row">
									<div class="col-md-3 form-group">
										<label class="formLabel" for="fatherName">Father Name</label>
										<input type="text" id="fatherName" name="fatherName" disabled 
	   										class="form-control" placeholder="Enter Father Name" required /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="motherName">Mother Name</label>
										<input type="text" id="motherName" name="motherName" disabled 
	   										class="form-control" placeholder="Enter Mother Name" required /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="spouseName">Spouse Name</label>
										<input type="text" id="spouseName" name="spouseName" disabled 
	   										class="form-control" placeholder="Enter Spouse Name" /> 
									</div>
								</div>
								
								<div class="row">
									<div class="col-md-3 form-group">
										<label class="formLabel" for="gender">Gender</label>
										<select id="gender" name="gender" disabled 
	   										class="form-control" required>
	   										<option value="">Select Gender</option>
	   										<option value="Female">Female</option>
	   										<option value="Male">Male</option>
	   									</select> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="dob">Date of Birth</label>
										<input type="date" id="dob" name="dob" disabled required 
											class="form-control" onchange="calculateCurrentAge(this.value)" />
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="currentAge">Current Age</label>
										<input type="text" id="currentAge" name="currentAge" readonly 
	   										class="form-control" placeholder="Enter Age" /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="age">Age (At Admission)</label>
										<input type="text" id="age" name="age" readonly 
	   										class="form-control" placeholder="Enter Age (At Admission)" /> 
									</div>
								</div>
								
								<div class="row">
									<div class="col-md-3 form-group">
										<label class="formLabel" for="emailId">Email Address</label>
										<input type="text" id="emailId" name="emailId" disabled required 
	   										class="form-control" placeholder="Enter Email Address" /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="mobile">Mobile Number</label>
										<input type="text" id="mobile" name="mobile" disabled required 
	   										class="form-control" placeholder="Enter Mobile Number" /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="altPhone">Alt Phone Number</label>
										<input type="text" id="altPhone" name="altPhone" disabled 
	   										class="form-control" placeholder="Enter Alt Phone Number" /> 
									</div>
									
									<span id="helpBlockContact" class="help-block"></span>
								</div>
								
								<div class="row">
									<div class="col-md-3 form-group">
										<label class="formLabel" for="addressLine1">Address Line 1 (House No. Name)</label>
										<input type="text" id="addressLine1" name="addressLine1" disabled 
	   										class="form-control" placeholder="Enter House No. Name" required /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="addressLine2">Address Line 2 (Street)</label>
										<input type="text" id="addressLine2" name="addressLine2" disabled 
	   										class="form-control" placeholder="Enter Street" required /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="landMark">LandMark</label>
										<input type="text" id="landMark" name="landMark" disabled 
	   										class="form-control" placeholder="Enter Nearest LandMark" /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="addressLine3">Address Line 3 (Locality)</label>
										<input type="text" id="addressLine3" name="addressLine3" disabled 
	   										class="form-control" placeholder="Enter Locality" required /> 
									</div>
								</div>
								
								<div class="row">
									<div class="col-md-3 form-group">
										<label class="formLabel" for="pin">Pin Code</label>
										<input type="text" id="pin" name="pin" onblur="addressDetailsByPin(this)" disabled required 
	   										class="form-control" placeholder="Enter Pin Code" aria-describedby="helpBlockPin" />
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="city">City</label>
										<input type="text" id="city" name="city" disabled required 
	   										class="form-control" placeholder="Enter City" /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="state">State</label>
										<input type="text" id="state" name="state" disabled required 
	   										class="form-control" placeholder="Enter State" /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="country">Country</label>
										<input type="text" id="country" name="country" disabled required 
	   										class="form-control" placeholder="Enter Country" /> 
									</div>
									
									<span id="helpBlockPostalAddr" class="help-block"></span>
								</div>
								
								<div class="row">
									<div class="col-md-3 form-group">
										<label class="formLabel" for="centerCode">Center Code</label>
										<select id="centerCode" name="centerCode" class="form-control"
	   										readonly onclick="return false;" onkeydown="return false;"> 
	   										<option value="">Select Center Code</option>
	   									</select>
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="centerName">Center Name</label>
										<select id="centerName" name="centerName" class="form-control"
	   										readonly onclick="return false;" onkeydown="return false;"> 
	   										<option value="">Select Center Name</option>
	   									</select>
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="program">Program</label>
										<select id="program" name="program" class="form-control"
	   										readonly onclick="return false;" onkeydown="return false;"> 
	   										<option value="">Select Program</option>
	   									</select>
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="programStructure">Program Structure</label>
										<select id="programStructure" name="programStructure" class="form-control"
	   										readonly onclick="return false;" onkeydown="return false;"> 
	   										<option value="">Select Program Structure</option>
	   									</select>
									</div>
								</div>
								
								<div class="row">
									<div class="col-md-3 form-group">
										<label class="formLabel" for="enrollmentMonth">Enrollment Month</label>
										<select id="enrollmentMonth" name="enrollmentMonth" class="form-control"
	   										readonly onclick="return false;" onkeydown="return false;"> 
	   										<option value="">Select Enrollment Month</option>
	   									</select>
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="enrollmentYear">Enrollment Year</label>
										<input type="text" id="enrollmentYear" name="enrollmentYear" readonly 
	   										class="form-control" placeholder="Enter Enrollment Year" /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="validityEndMonth">Validity End Month</label>
										<select id="validityEndMonth" name="validityEndMonth" class="form-control"
	   										readonly onclick="return false;" onkeydown="return false;"> 
	   										<option value="">Select Validity End Month</option>
	   									</select>
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="validityEndYear">Validity End Year</label>
										<input type="text" id="validityEndYear" name="validityEndYear" readonly 
	   										class="form-control" placeholder="Enter Validity End Year" /> 
									</div>
								</div>
								
								<div class="row">
									<div class="col-md-3 form-group">
										<label class="formLabel" for="programChanged">Program Changed</label>
										<select id="programChanged" name="programChanged" class="form-control"
	   										readonly onclick="return false;" onkeydown="return false;"> 
	   										<option value="">No</option>
	   										<option value="Y">Yes</option>
	   									</select>
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="oldProgram">Old Program</label>
										<select id="oldProgram" name="oldProgram" class="form-control"
	   										readonly onclick="return false;" onkeydown="return false;"> 
	   										<option value="">Select Old Program</option>
	   									</select>
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="programCleared">Program Cleared</label>
										<select id="programCleared" name="programCleared" disabled 
	   										class="form-control" required> 
	   										<option value="N" selected>No</option>
	   										<option value="Y">Yes</option>
	   									</select>
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="programStatus">Program Status</label>
										<select id="programStatus" name="programStatus" disabled 
	   										class="form-control" > 
	   										<option value="">Program Active (Confirmed)</option>
	   										<option value="Program Suspension">Program Suspension</option>
	   										<option value="Program Terminated">Program Terminated (Admission Cancelled)</option>
	   										<option value="Program Withdrawal">Program Withdrawal (De-Registered)</option>
	   									</select>
									</div>
								</div>
								
								<div class="row">
									<div class="col-md-3 form-group">
										<label class="formLabel" for="programRemarks">Program Remarks</label>
										<textarea type="text" id="programRemarks" name="programRemarks" 
	   										class="form-control" rows="4" cols="50" disabled></textarea>
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="highestQualification">Highest Qualification</label>
										<input type="text" id="highestQualification" name="highestQualification" readonly 
	   										class="form-control" placeholder="Select Highest Qualification" /> 
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="industry">Industry</label>
										<select id="industry" name="industry" disabled 
	   										class="form-control"> 
	   										<option value="">Select Industry</option>
	   										<option value="Agriculture">Agriculture</option>
	   										<option value="Apparel">Apparel</option>
	   										<option value="Banking">Banking</option>
	   										<option value="Biotechnology">Biotechnology</option>
	   										<option value="Chemicals">Chemicals</option>
	   										<option value="Communication">Communication</option>
	   										<option value="Construction">Construction</option>
	   										<option value="Consulting">Consulting</option>
	   										<option value="Education">Education</option>
	   										<option value="Electronics">Electronics</option>
	   										<option value="Energy">Energy</option>
	   										<option value="Engineering">Engineering</option>
	   										<option value="Entertainment">Entertainment</option>
	   										<option value="Environment">Environment</option>
	   										<option value="Finance">Finance</option>
	   										<option value="Food & Bevarage">Food & Beverage</option>
	   										<option value="Government">Government</option>
	   										<option value="Healthcare">Healthcare</option>
	   										<option value="Hospitality">Hospitality</option>
	   										<option value="Insurance">Insurance</option>
	   										<option value="Machinery">Machinery</option>
	   										<option value="Manufacturing">Manufacturing</option>
	   										<option value="Media">Media</option>
	   										<option value="Not For Profit">Not For Profit</option>
	   										<option value="Others">Others</option>
	   										<option value="Recreation">Recreation</option>
	   										<option value="Retail">Retail</option>
	   										<option value="Shipping">Shipping</option>
	   										<option value="Technology">Technology</option>
	   										<option value="Telecommunications">Telecommunications</option>
	   										<option value="Transportation">Transportation</option>
	   										<option value="Utilities">Utilities</option>
	   									</select>
									</div>
									
									<div class="col-md-3 form-group">
										<label class="formLabel" for="designation">Designation</label>
										<select id="designation" name="designation" disabled 
	   										class="form-control"> 
	   										<option value="">Select Designation</option>
	   										<option value="Assistant">Assistant</option>
	   										<option value="Assistant Manager">Assistant Manager</option>
	   										<option value="Associate">Associate</option>
	   										<option value="Asst. Manager">Asst. Manager</option>
	   										<option value="CEO">CEO</option>
	   										<option value="CFO">CFO</option>
	   										<option value="Chairman">Chairman</option>
	   										<option value="CXO">CXO</option>
	   										<option value="Deputy General Manager">Deputy General Manager</option>
	   										<option value="Executive / Officer">Executive / Officer</option>
	   										<option value="General Manager">General Manager</option>
	   										<option value="Jr. Associate">Jr. Associate</option>
	   										<option value="Jr. Officer">Jr. Officer</option>
	   										<option value="Manager">Manager</option>
	   										<option value="Managing Director">Managing Director</option>
	   										<option value="Officer">Officer</option>
	   										<option value="Others">Others</option>
	   										<option value="Self Employed">Self Employed</option>
	   										<option value="Sr. Associate">Sr. Associate</option>
	   										<option value="Sr. Manager">Sr. Manager</option>
	   										<option value="Sr. Officer">Sr. Officer</option>
	   										<option value="Sr. Vice president">Sr. Vice president</option>
	   										<option value="Supervisor">Supervisor</option>
	   										<option value="Vice Chairman">Vice Chairman</option>
	   										<option value="Vice President">Vice President</option>
	   									</select>
									</div>
								</div>
								
								<div class="row col-md-6">
									<div class="form-group">
										<button id="submit" name="submit"
											class="btn btn-large btn-primary">Update</button>
										<a id="resetBtn" class="btn btn-danger disableLink"
											onclick="resetFormAction()">Reset</a>
										<button id="cancel" name="cancel" class="btn btn-danger" formnovalidate
											formaction="${pageContext.request.contextPath}/home">Cancel</button>
									</div>
								</div>
							</form>
						</div>
					</div>
					<div class="modal fade" id="imageViewModal" tabindex="-1" role="dialog" aria-labelledby="imageViewModal">
						<div class="modal-dialog" role="document">
							<div class="modal-content">
								<div class="modal-header">
									<button type="button" class="close" data-dismiss="modal"
										aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
									<h4 id="modalTitle" class="modal-title">Student Image</h4>
								</div>
								<div class="modal-body">
									<img id="modalImage" class="modal-body-content" alt="&#xf1c5;">
								</div>
								<div class="modal-footer">
									<a class="modal-button btn btn-danger" data-dismiss="modal">Close</a>
								</div>
							</div><!-- /.modal-content -->
						</div><!-- /.modal-dialog -->
					</div><!-- /.modal -->
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="adminCommon/footer.jsp" />

<script type="text/javascript">
	let studentProfileDetailsObject;
	const getDetailsUrl = "/exam/m/admin/getStudentProfileDetails?";

	/* OWASP Email validation regex */
	const emailAddressRegex = /^[a-z0-9_\+&\*-]+(?:\.[a-z0-9_\+&\*-]+)*@(?:[a-z0-9-]+\.)+[a-z]{2,7}$/i;
	
	/* Generic validation for worldwide Mobile Phone numbers
	1. Every mobile number uses only 0-9 and sometimes space/dash/comma/period/round brackets.
	2. Minimum 7 charcters and a Maximum of 34 characters
	3. Allow one space/dash/comma/period/round brackets character at a time, no repetition
	4. Can begin with a plus sign or two leading zeros. */
	const mobileNoRegex = /^(\+|00)?([0-9][-,\(\)\.\s]?){6,31}[0-9\)]$/i;
	
	/* Generic validation for worldwide pincodes
	1. Every postal code system uses only A-Z and/or 0-9 and sometimes space/dash.
	2. Minimum 2 charcters and a Maximum of 12 characters.
	3. Allow one space or dash character at a time, no repetition.
	4. Should not begin or end with space or dash. */
	const pinCodeRegex = /^[a-z0-9]([a-z0-9][-\s]?){0,10}[a-z0-9]$/i;

	/*
		After the DOM is loaded the async function executes
		and fetches student profile details data and stores as an Object.
		The values from the Object are then added as the form attributes
	*/
	document.addEventListener("DOMContentLoaded", async function() {
		let request = new Request(getDetailsUrl + new URLSearchParams({
																		sapid: document.getElementById("sapid").value,
																		sem: document.getElementById("sem").value,
																	}), {
																			method: "GET",
																			headers: new Headers({
																			    "Content-Type": "application/json; charset=UTF-8",
																				"Accepts": "application/json"
																			})
																		 });
		let response = await fetch(request);
		if(response.ok) {
			studentProfileDetailsObject = await response.json();
			insertEnableFormFields(studentProfileDetailsObject);
			document.getElementById("resetBtn").classList.remove("disableLink");		//removing the disableLink class from the resetButton which enables the button for the user
		}
		else {
			let errorText = await response.text();
			console.error("Error while fetching Student profile details: ", errorText);
			alert("Error while fetching Student profile details, please reload the page.");
		}
	});

	/*
		Values from the Object are added as form attributes
	*/
	function insertEnableFormFields(studentDetailsObj) {
		let fatherNameEl = document.getElementById("fatherName");
		fatherNameEl.value = studentDetailsObj.fatherName;
		fatherNameEl.removeAttribute("disabled");
		let motherNameEl = document.getElementById("motherName");
		motherNameEl.value = studentDetailsObj.motherName;
		motherNameEl.removeAttribute("disabled");
		let firstNameEl = document.getElementById("firstName");
		firstNameEl.value = studentDetailsObj.firstName;
		firstNameEl.removeAttribute("disabled");
		let middleNameEl = document.getElementById("middleName");
		middleNameEl.value = studentDetailsObj.middleName;
		middleNameEl.removeAttribute("disabled");
		let lastNameEl = document.getElementById("lastName");
		lastNameEl.value = studentDetailsObj.lastName;
		lastNameEl.removeAttribute("disabled");
		let spouseNameEl = document.getElementById("spouseName");
		spouseNameEl.value = studentDetailsObj.spouseName;
		spouseNameEl.removeAttribute("disabled");

		let genderNameEl = document.getElementById("gender");
		selectOptionOrCreate(genderNameEl, studentDetailsObj.gender);
		genderNameEl.removeAttribute("disabled");
		let dobEl = document.getElementById("dob");
		dobEl.value = studentDetailsObj.dob;
		dobEl.removeAttribute("disabled");
		calculateCurrentAge(dobEl.value);		//display the current age
		let ageEl = document.getElementById("age");		//age kept as disabled
		ageEl.value = studentDetailsObj.age;
		insertStudentImage(studentDetailsObj.imageUrl);			//adding the student image in form

		let emailIdEl = document.getElementById("emailId");
		emailIdEl.value = studentDetailsObj.emailId;
		emailIdEl.removeAttribute("disabled");
		let mobileEl = document.getElementById("mobile");
		mobileEl.value = studentDetailsObj.mobile;
		mobileEl.removeAttribute("disabled");
		let altPhoneEl = document.getElementById("altPhone");
		altPhoneEl.value = studentDetailsObj.altPhone;
		altPhoneEl.removeAttribute("disabled");

		let addressLine1El = document.getElementById("addressLine1");
		addressLine1El.value = studentDetailsObj.houseNoName;
		addressLine1El.removeAttribute("disabled");
		let addressLine2El = document.getElementById("addressLine2");
		addressLine2El.value = studentDetailsObj.street;
		addressLine2El.removeAttribute("disabled");
		let landMarkEl = document.getElementById("landMark");
		landMarkEl.value = studentDetailsObj.landMark;
		landMarkEl.removeAttribute("disabled");
		let addressLine3El = document.getElementById("addressLine3");
		addressLine3El.value = studentDetailsObj.addressLine3;
		addressLine3El.removeAttribute("disabled");
		let pinEl = document.getElementById("pin");
		pinEl.value = studentDetailsObj.pin;
		pinEl.removeAttribute("disabled");
		let cityEl = document.getElementById("city");
		cityEl.value = studentDetailsObj.city;
		cityEl.removeAttribute("disabled");
		let stateEl = document.getElementById("state");
		stateEl.value = studentDetailsObj.state;
		stateEl.removeAttribute("disabled");
		let countryEl = document.getElementById("country");
		countryEl.value = studentDetailsObj.country;
		countryEl.removeAttribute("disabled");

		selectOptionOrCreate(document.getElementById("centerCode"), studentDetailsObj.centerCode);		//centerCode kept as disabled
		selectOptionOrCreate(document.getElementById("centerName"), studentDetailsObj.centerName);		//centerName kept as disabled
		selectOptionOrCreate(document.getElementById("program"), studentDetailsObj.program);		//program kept as disabled
		selectOptionOrCreate(document.getElementById("programStructure"), studentDetailsObj.programStructure);		//programStructure kept as disabled

		selectOptionOrCreate(document.getElementById("enrollmentMonth"), studentDetailsObj.enrollmentMonth);		//enrollmentMonth kept as disabled
		document.getElementById("enrollmentYear").value = studentDetailsObj.enrollmentYear;		//enrollmentYear kept as disabled
		selectOptionOrCreate(document.getElementById("validityEndMonth"), studentDetailsObj.validityEndMonth);		//validityEndMonth kept as disabled
		document.getElementById("validityEndYear").value = studentDetailsObj.validityEndYear;		//validityEndYear kept as disabled
		
		selectOptionOrCreate(document.getElementById("programChanged"), studentDetailsObj.programChanged);		//programChanged kept as disabled
		selectOptionOrCreate(document.getElementById("oldProgram"), studentDetailsObj.oldProgram);		//oldProgram kept as disabled
		let programClearedEl = document.getElementById("programCleared");
		selectOptionOrCreate(programClearedEl, studentDetailsObj.programCleared);
		programClearedEl.removeAttribute("disabled");
		let programStatusEl = document.getElementById("programStatus");
		selectOptionOrCreate(programStatusEl, studentDetailsObj.programStatus);
		programStatusEl.removeAttribute("disabled");
		let programRemarksEl = document.getElementById("programRemarks");
		programRemarksEl.value = studentDetailsObj.programRemarks;
		programRemarksEl.removeAttribute("disabled");

		document.getElementById("highestQualification").value = studentDetailsObj.highestQualification;			//highestQualification kept as disabled
		let industryEl = document.getElementById("industry");
		selectOptionOrCreate(industryEl, studentDetailsObj.industry);
		industryEl.removeAttribute("disabled");
		let designationEl = document.getElementById("designation");
		selectOptionOrCreate(designationEl, studentDetailsObj.designation);
		designationEl.removeAttribute("disabled");
	}

	/*
		If the value passed is present as an option in the selectElement, then it is selected.
		Or a new option is created and selected.
	*/
	function selectOptionOrCreate(selectElement, newValue) {
		const selectOptions = Array.from(selectElement.options).map(e => e.value);
		
		if(typeof newValue !== "undefined" 
			&& newValue !== null
			&& newValue.trim().length > 0) {		//will evaluate to true if value is not: null, undefined, empty string ("")
			
			if(selectOptions.indexOf(newValue) > -1) 
				selectElement.value = newValue;
			else 
				createNewOption(selectElement, newValue, newValue);
		}
	}

	/*
		A new select option is created and selected
	*/
	function createNewOption(element, text, inputValue) {
		let option = document.createElement("option");
		option.text = text;
		option.value = inputValue;
		option.selected = "selected";
		element.add(option);
	}

	/*
		On clicking the reset button the form attributes are reverted to their initial values.
	*/
	function resetFormAction() {
		insertEnableFormFields(studentProfileDetailsObject);
	}

	/*
		Calculate Current Age from the DOB
	*/
	function calculateCurrentAge(dob) { 
	    const today = new Date();
	    const birthDate = new Date(dob);
	    let age = today.getFullYear() - birthDate.getFullYear();
	    
	    const monthDiff = today.getMonth() - birthDate.getMonth();
	    if(monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) 		//for day and month precision
	        age--;

        if(age < 0) {		//Check to invalidate future Date of Birth selections
        	alert("Please select a valid Date of Birth!");
			document.getElementById("dob").value = "";
			document.getElementById("currentAge").value = 0;
        }
        else
	    	document.getElementById("currentAge").value = age;
	}

	/*
		If the imageUrl passed in parameter is valid, attach the url as a source to the student img
	*/
	function insertStudentImage(imageUrl) {
		if(typeof imageUrl !== "undefined" 
			&& imageUrl !== null
			&& imageUrl.trim().length > 0) {

			document.getElementById("studentImage").setAttribute("src", imageUrl);
			document.getElementById("studentImage").style.objectFit = "cover";
			document.getElementById("imageUrl").setAttribute("value", imageUrl);
			document.getElementById("modalImage").setAttribute("src", imageUrl);
			document.querySelector(".imageView").style.display = "block";
			document.getElementById("studentImageDiv").classList.add("hoverBlock");
		}
	}

	/*
		Fetch the City, State and Country details from the provided Pin Code
	*/
	async function addressDetailsByPin(pin) {
		const pincode = pin.value;
		if(pincode.length !== 6) {
			console.error("Unable to automatically fetch City, State & Country details. Pincode length not equal to 6");
			displayPinDetailsMessage(false);
			return;
		}

		const request = new Request("/studentportal/getAddressDetailsFromPinCode", {
																					    method: "POST",
																					    headers: {
																					      "Accept": "application/json",
																					      "Content-Type": "application/json"
																					    },
																					    body: JSON.stringify({"pin": pincode})
																					});
		try {
			let response = await fetch(request);
			if (!response.ok) 
				throw new Error(response.status + " " + response.statusText);
			
			let addressDetailsObj = await response.json();
			document.getElementById("city").value = addressDetailsObj.city;
			document.getElementById("state").value = addressDetailsObj.state;
			document.getElementById("country").value = addressDetailsObj.country;

			displayPinDetailsMessage(true);
		}
		catch(err) {
			console.error("Unable to automatically fetch City, State & Country details! Error: ", err);
			displayPinDetailsMessage(false);
		}
	}

	/*
		Displays the image model on call
	*/
	function showImageModal() {
		$("#imageViewModal").modal('show');
	}

	/*
		Method which displays a response message for PinCode attribute depending on the passed status.
	*/
	function displayPinDetailsMessage(success) {
		let validationClassName = "";
		let timerSeconds = 0;
		const pinElParent = document.getElementById("pin").parentNode;
		const helpBlock = document.getElementById("helpBlockPostalAddr");
		helpBlock.style.display = "block";

		if(success) {
			validationClassName = "has-success";
			helpBlock.textContent = "Successfully updated City, State & Country details from the entered Pin Code.";
			helpBlock.style.color = "#4BA355";
			timerSeconds = 5000;
		}
		else {
			validationClassName = "has-warning";
			helpBlock.textContent = "Unable to automatically fetch City, State & Country details from the entered PinCode. Please enter manually.";
			helpBlock.style.color = "#9DAD53";
			timerSeconds = 8000;
		}
		pinElParent.classList.add(validationClassName);
		document.getElementById("city").parentNode.classList.add(validationClassName);
		document.getElementById("state").parentNode.classList.add(validationClassName);
		document.getElementById("country").parentNode.classList.add(validationClassName);

		const myTimeout = setTimeout(function() {
										helpBlock.style.display = "none";
										helpBlock.textContent = "";
										
										pinElParent.classList.remove(validationClassName);
										document.getElementById("city").parentNode.classList.remove(validationClassName);
										document.getElementById("state").parentNode.classList.remove(validationClassName);
										document.getElementById("country").parentNode.classList.remove(validationClassName);
									}, timerSeconds);
	}

	/*
		Validate form fields before submit using regular expression, 
		if validation fails, cancel form submit.
	*/
	function validateForm() {
		const emailId = document.getElementById("emailId");
		const mobile = document.getElementById("mobile");
		const altPhone = document.getElementById("altPhone");
		const pin = document.getElementById("pin");

		if(!emailAddressRegex.test(emailId.value)) 
			return displayValidationError(emailId, "Invalid Email Address! Allowed characters: A(a)-Z(z), digits: 0-9, " +
											"special characters: hyphen, underscore, plus(+), ampersand(&), asterisk(*)", "helpBlockContact");
		
		if(!mobileNoRegex.test(mobile.value)) 
			return displayValidationError(mobile, "Invalid Mobile Number! Allowed digits: 0-9, " +
											"special characters: hyphen, space key, round brackets & period", "helpBlockContact");
		
		if(altPhone.value.length > 0 && !mobileNoRegex.test(altPhone.value)) 
			return displayValidationError(altPhone, "Invalid Alt Phone! Allowed digits: 0-9, " +
											"special characters: hyphen, space key, round brackets & period", "helpBlockContact");
		
		if(!pinCodeRegex.test(pin.value)) 
			return displayValidationError(pin, "Invalid Pin Code! Allowed characters: A(a)-Z(z), digits: 0-9, " +
											"special characters: hyphen & space key", "helpBlockPostalAddr");

		return true;
	}

	/*
		Validation error message is displayed to the user for 8 seconds and logged in console.
	*/
	function displayValidationError(element, errorMessage, helpBlockId){
		const elParent = element.parentNode;
		elParent.classList.add("has-error");
		
		const helpBlock = document.getElementById(helpBlockId);
		helpBlock.style.display = "block";
		helpBlock.style.color = "#d2232a";
		helpBlock.textContent = errorMessage;
		console.error("Validation Error: ", helpBlock.textContent);
		
		const myTimeout = setTimeout(function() {
													helpBlock.style.display = "none";
													helpBlock.textContent = "";
													elParent.classList.remove("has-error");
												}, 8000);

		element.focus();
		return false;
	}
</script>
</body>
</html>