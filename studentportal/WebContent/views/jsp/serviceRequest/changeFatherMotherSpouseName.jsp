<!DOCTYPE html>
<html lang="en">

<jsp:include page="../adminCommon/jscss.jsp">
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
	#supportingDocType > ul {
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
	.modal-header > .fa {
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
 	.modal-footer > .modal-button { 
	    width: 20%;
	    margin: 0.2em 0.5em 0.35em !important;
	    font-size: 0.8rem;
	    border-radius: 0.2em;
	    padding: 0.5em 0;
	}
</style>
<body>
<jsp:include page="../common/header.jsp"/>
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
	     		<ul class="sz-breadcrumbs">
		       		<li><a href="/studentportal/home">Student Zone</a></li> 
		       		<li><a href="selectSRForm">Select Service Request</a></li>
		       		<li><a href="changeFatherMotherSpouseNameSRForm">Change Father/Mother/Spouse Name SR</a></li>
	        	</ul>
	        </div>
	  	</div><!-- breadcrumbs -->

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

						<div class="panel panel-default">
							<div class="panel-heading">
								<h2 class="panel-title red text-capitalize">Change Father/Mother/Spouse Name Service Request</h2>
							</div>
							<div class="panel-body">
								<jsp:include page="../common/messages.jsp" />
								<form name="changeFaMoSpoNameForm" action="changeFatherMotherSpouseNameSR" 
									method="post" enctype="multipart/form-data">
									
									<input type="hidden" id="sapid" name="sapid" value="${studentSapid}" />
									<div class="row">
										<div class="col-md-4 form-group">
											<label for="detailType">Choose the Detail you want to Update</label>
											<select class="form-control" id="detailType" name="detailType" 
												onchange="changeFormElementAttr(this.value)" disabled>
											  <option value="">Select an Option</option>
											  <option value="fatherName">Change Father Name</option>
											  <option value="motherName">Change Mother Name</option>
											  <option value="husbandName">Change Spouse Name</option>
											</select>
										</div>
									</div>
									
									<div class="row">
										<div class="col-md-8 form-group hidden" id="currentValueBlock">
											<label for="currentValue"></label>
		  									<input class="form-control" type="text" readonly id="currentValue" name="currentValue">
		  								</div>
  									</div>
  									
  									<div class="row">
  										<div class="col-md-8 form-group hidden" id="updateValueBlock">
		  									<label for="updateValue"></label>
		  									<input class="form-control" type="text" id="updateValue" name="updateValue"
		  										onkeydown="validateKey(event)" onpaste="validatePaste(event)">
		  								</div>
	  								</div>
	  								
	  								<div class="row">
  										<div class="col-md-6 form-group hidden" id="supportingDocBlock">
		  									<label for="supportingDocument">Upload Supporting Document: </label>
		  									<input class="form-control" type="file" id="supportingDocument" 
		  										accept="image/jpeg, image/png, image/svg+xml, application/pdf, application/msword, 
		  												application/vnd.openxmlformats-officedocument.wordprocessingml.document, 
		  												application/zip, application/vnd.rar"
		  										name="supportingDocument" onchange="checkFile(event)">
		  								</div>
	  								</div>
  									
  									<div class="row col-md-6">
										<div class="form-group">
											<button id="formSubmission" name="formSubmission" disabled
												class="btn btn-large btn-primary">Submit</button>
											<button id="reset" onclick="customFormReset(); return false;"
												class="btn btn-danger" disabled>Reset</button>
											<a id="cancel" href="selectSRForm" 
												class="btn btn-danger">Cancel</a>
										</div>
									</div>
									<input type="hidden" id="device" name="device" value="WebApp" />
								</form>
							</div>
						</div>
						<div class="modal fade" id="messageModal" tabindex="-1" role="dialog" aria-labelledby="messageModal">
							<div class="modal-dialog" role="document">
								<div class="modal-content">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal"
											aria-label="Close">
											<span aria-hidden="true">&times;</span>
										</button>
										<i id="errorIcon"  class="fa-solid fa-exclamation" aria-hidden="true"></i>
										<i id="successIcon" class="fa-solid fa-check" aria-hidden="true"></i>
										<h4 id="modalTitle" class="modal-title"></h4>
									</div>
									<div class="modal-body">
										<p class="modal-body-content"></p>
									</div>
									<div class="modal-footer">
										<button type="button" id="modalSave" class="modal-button btn btn-large btn-primary"
											onclick="srCheckConfirmation()">Proceed</button>
										<a class="modal-button btn btn-danger" data-dismiss="modal">Close</a>
									</div>
								</div><!-- /.modal-content -->
							</div><!-- /.modal-dialog -->
						</div><!-- /.modal -->
					</div>
				</div>
			</div>
		</div>
	</div>
<jsp:include page="../common/footer.jsp" />

<script>
	let studentFaMoSpoNameObject, helpBlockTimeout;
	const studentSapid = document.getElementById("sapid").value;
	
	const keyRegex = /[a-z\.\-'\xC0-\uFFFF]/i;
	const charSeqRegex = /^[a-z\.\-'\xC0-\uFFFF]+$/i;
	const charSeqSpaceRegex = /^[a-z\s\.\-'\xC0-\uFFFF]+$/i;

	const expectedFileTypes = ["image/jpeg", "image/png", "image/svg+xml", "application/pdf", 
								"application/zip", "application/x-zip-compressed", "application/vnd.rar", "application/x-rar-compressed", 
								"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"];

	const fatherDocTypeArray = ["PAN Card", "Passport", "Aadhaar Card (if Father name is mentioned)", 
								"Driving Licence (if Father name is mentioned)", "10th Marksheet (if Father name is mentioned)"];
	const motherDocTypeArray = ["Passport", "10th Marksheet (if Mother name is mentioned)"];
	const spouseDocTypeArray = ["Passport", "Marriage Certificate"];

	//Executes when the DOM is loaded
	document.addEventListener("DOMContentLoaded", async function() {
		try {
			const response = await fetch(`/studentportal/m/student/getCurrentFatherMotherHusbandName/${studentSapid}`);
			if(response.ok) {
				const responseObj = await response.json();
				studentFaMoSpoNameObject = responseObj.details;
				document.getElementById("detailType").removeAttribute("disabled");
			}
			else {
				let errorText = await response.text();
				console.error("Error while fetching Student details: ", errorText);
				populateMessageModalText(true, "Error fetching student details.\nPlease refresh page and try again.", true, true);
			}
		}
		catch(err) {
			console.error("Unable to fetch student FatherName, MotherName and SpouseName details due to Network Error: ", err);
			populateMessageModalText(true, "Network Error while fetching details.\nPlease refresh page and try again.", true, true);
		}
	});

	/*
		Depending on the option selected by the user from the dropdown, the form is updated, 
		the values in the form attributes are also updated accordingly.
	*/
	function changeFormElementAttr(selection) {
		const currentValueElement = document.getElementById("currentValueBlock");
		const updateValueElement = document.getElementById("updateValueBlock");
		const supportingDocElement = document.getElementById("supportingDocBlock");
		const submitBtn = document.getElementById("formSubmission");
		const resetBtn = document.getElementById("reset");
		let currentValue = "";
		switch(selection) {
			case "fatherName":
				currentValueElement.firstElementChild.textContent = "Current Father Name: ";
				updateValueElement.firstElementChild.innerHTML = "Enter new Father Name: <em>(First Name only)</em>";
				updateValueElement.lastElementChild.setAttribute("placeholder", "Enter Father First Name here");
				currentValue = studentFaMoSpoNameObject[selection];
				createUnorderedDocumentList(fatherDocTypeArray);
				break;
			case "motherName":
				currentValueElement.firstElementChild.textContent = "Current Mother Name: ";
				updateValueElement.firstElementChild.innerHTML = "Enter new Mother Name: <em>(First Name only)</em>";
				updateValueElement.lastElementChild.setAttribute("placeholder", "Enter Mother First Name here");
				currentValue = studentFaMoSpoNameObject[selection];
				createUnorderedDocumentList(motherDocTypeArray);
				break;
			case "husbandName":
				currentValueElement.firstElementChild.textContent = "Current Spouse Name: ";
				updateValueElement.firstElementChild.textContent = "Enter new Spouse Name: ";
				updateValueElement.lastElementChild.setAttribute("placeholder", "Enter Spouse Name here");
				currentValue = studentFaMoSpoNameObject[selection];
				createUnorderedDocumentList(spouseDocTypeArray);
				break;
			default:
				currentValueElement.firstElementChild.textContent = selection;
				currentValueElement.lastElementChild.value = currentValue;			//resetting the value to empty
				updateValueElement.firstElementChild.textContent = selection;
				updateValueElement.lastElementChild.value = currentValue;			//resetting the value to empty
				supportingDocElement.removeChild(document.getElementById("supportingDocType"));
				supportingDocElement.lastElementChild.value = null;				//resetting the file value to empty
				customFormReset();
				currentValueElement.classList.add("hidden");
				updateValueElement.classList.add("hidden");
				supportingDocElement.classList.add("hidden");
				submitBtn.setAttribute("disabled", "disabled");
				resetBtn.setAttribute("disabled", "disabled");
				return;											//return to exit the function
		}

		currentValueElement.lastElementChild.value = currentValue;
		currentValueElement.classList.remove("hidden");
		updateValueElement.lastElementChild.value = "";			//resetting the value to empty
		updateValueElement.classList.remove("hidden");
		supportingDocElement.lastElementChild.value = null;				//resetting the file value to empty
		supportingDocElement.classList.remove("hidden");

		submitBtn.removeAttribute("disabled");
		resetBtn.removeAttribute("disabled");
	}

	/*
		An API is called using fetch which return a boolean value to denote if the student is eligible to raise the select Service Request
	*/
	function srEligibilityCheck(studentNo, selectedDetail) {
		const url = "/studentportal/m/student/checkEligibilityChangeDetailsSR?" + new URLSearchParams({ 
																										sapid: studentNo,
																										detailType: selectedDetail
																										});
		const request = new Request(url, {
											method: 'GET',
											headers: new Headers({
											    "Content-Type": "application/json; charset=UTF-8",
												"Accepts": "application/json"
											})
										});

		const eligibility = fetch(request)
								.then(response => {
										if(response.ok)
											return response.text();
										else
											return response.text()
													.then(text => {
															throw new Error(text)
													});
								})
								.then(respText => respText === "true")
								.catch(err => {
										console.error("Error while checking student SR Eligibility: ", err);
										populateMessageModalText(true, "Error while checking Eligibility of student." + 
																"\nPlease refresh the page and try again!", true, true);
										return false;
								});
		return eligibility;
	}

	/*
		On clicking the reset button, only the update value element is resetted to it's default value.
	*/
	function customFormReset() {
		const updateValueElement = document.getElementById("updateValue");
		const supportingDocElement = document.getElementById("supportingDocument");

		updateValueElement.value = updateValueElement.defaultValue;
		supportingDocElement.value = supportingDocElement.defaultValue;
	}

	/*
		On clicking the Submit button, eligibility and validation checks are done,
		when these checks are successfully completed, the confirmation modal is shown to the user.
	*/
	document.changeFaMoSpoNameForm.addEventListener("submit", async function(event) {
		event.preventDefault();						//Initially disabling the form submit
	    const updateValueText = this.querySelector("input[name=updateValue]").value;		// this - object of form
	    const currentValueText = this.querySelector("input[name=currentValue]").value;
	    const supportingDocFile = this.querySelector("input[name=supportingDocument]").files[0];

	    const select = document.getElementById("detailType");
		const selectedValue = select.options[select.selectedIndex].value;

		if(checkForEmptyValues())		//Check if the values are empty
			return;

		//Check if student is eligible to raise the Service Request
	    let isEligible = await srEligibilityCheck(studentSapid, selectedValue);
		if(!isEligible) {
			const typeName = selectedValue === "husbandName" ? "spouseName" : selectedValue;
			populateMessageModalText(true, "Not Eligible to raise Service Request," + 
									"\nRequest to Change " + typeName + " already In Progress." + 
									"\nContact Support for any further help.", true, true);
		}
		else if(!charSeqRegexCheck(selectedValue, updateValueText))
			populateMessageModalText(true, "Invalid Name entered." + 
									"\nPlease check and try again.", true, true);
	    else if(updateValueText === currentValueText)
	    	populateMessageModalText(true, "Entered Name cannot be the same as Current Name." + 
	    	    					"\nPlease Check and try again.", true, true);
	    else if(!validateFileType(supportingDocFile))
	    	populateMessageModalText(true, "Error uploading file, selected file is not accepted." + 
	    	    					"\nPlease upload file with a valid file type.", true, true);
	    else if(!validateFileSize(supportingDocFile))
	    	populateMessageModalText(true, "Error uploading file, selected file is too big." + 
	    	    					"\nPlease upload a file below 5 MB.", true, true);
	    else
	    	populateMessageModalText(false, "Are you sure you want to proceed?", "static", false);
	});

	/*
		On keyDown event, the entered key is validated.
	*/
	function validateKey(e) {
		const select = document.getElementById("detailType");
		const selectedValue = select.options[select.selectedIndex].value;

		//Check to bypass Ctrl and Command Key for Cut, Copy, Paste Keyboard Shortcuts
		if(e.ctrlKey || e.metaKey)
			return true;
		
		switch(e.keyCode) {
			case 8:
			case 9:
			case 13:
			case 37:
			case 39:		//Key Codes: backspace - 8, tab - 9, enter - 13, left arrow - 37, right arrow - 39
				break;
			default:
			    const key = e.key;
			    if(!keyRegex.test(key)) {			//error logged and displayed to the user, and action prevented
				   	if(selectedValue === "husbandName") {
						if(/\s/.test(key))
						    return true;
				   	}
				   	console.error("Entered key: ", key, " is invalid!");
				   	displayErrorMessage(document.getElementById("updateValue"), "Entered text: " + key + " is invalid for the " + selectedValue + " field.");
			    	e.preventDefault();
			    	return false;
			    }
			    break;
		}
	}

	/*
		On paste event, the character set is validated.
	*/
	function validatePaste(e) {
		let text = e.clipboardData.getData('text');
		const select = document.getElementById("detailType");
		const selectedValue = select.options[select.selectedIndex].value;

		const isValid = charSeqRegexCheck(selectedValue, text);
		if(!isValid) {			//error logged and displayed to the user, and action prevented
			console.error("Entered text: ", text, " is invalid!");
			displayErrorMessage(document.getElementById("updateValue"), "Entered text: " + text + " is invalid! Please enter a valid " + valueName + ".");
			e.preventDefault();
		}

		return isValid;
	}

	/*
		Checks if the entered character sequence is valid or not.
		The regex used to validate depends on the option selected in dropdown.
	*/
	function charSeqRegexCheck(selectedValue, charSeq) {
		if(selectedValue === "husbandName" && !charSeqSpaceRegex.test(charSeq)) 
			return false;
		else if(selectedValue !== "husbandName" && !charSeqRegex.test(charSeq)) 
			return false;
		else
			return true;
	}

	/*
		Checks if the file selected by the user for upload is of valid type and size or not.
	*/
	function checkFile(e) {
		const fileUpload = e.target.files[0];

		if(!validateFileType(fileUpload)) {
			displayErrorMessage(document.getElementById("supportingDocument"), "Selected file type not accepted. Please upload a valid file.");
			e.target.value = "";
		}
		else if(!validateFileSize(fileUpload)) {
			displayErrorMessage(document.getElementById("supportingDocument"), "Selected file size too big. Please upload a file below 5 MB.");
			e.target.value = "";
		}
	}

	/*
		Checks if file type matches the accepted file types.
	*/
	function validateFileType(file) {
		for(const fileType of expectedFileTypes) {
			if(file.type == fileType)				//check if file uploaded matches the accepted file type
				return true;
		}

		return false;
	}

	/*
		Checks if the file size is 5 MB or below.
	*/
	function validateFileSize(file) {
		return (file.size <= 5 * 1024 * 1024) ? true : false;		//check if file size is larger than 5MB
	}

	/*
		Create a list specifying the supporting documents for the selected detail type.
	*/
	function createUnorderedDocumentList(docTypeArray) {
		let supportingDocElement = document.getElementById("supportingDocBlock");
		const supportingDocType = document.getElementById("supportingDocType");
		if(supportingDocType != null)
			supportingDocElement.removeChild(supportingDocType);
		
		let unorderedList = document.createElement('ul');
		for(const docType of docTypeArray) {
			let item = document.createElement('li');
	        item.appendChild(document.createTextNode(docType));			// Setting content of the item:
	        
	        unorderedList.appendChild(item);			// Add item to the list:
		}

		let spanElement = document.createElement("span");
		spanElement.textContent = "Please Upload one of the following Documents as Proof (required)";

		let pElement = document.createElement("p");
		pElement.setAttribute("id","supportingDocType");
		pElement.appendChild(spanElement);
		pElement.appendChild(unorderedList);

		supportingDocElement.prepend(pElement);
	}

	/*
		Check if the updated value and supporting documents fields are empty.
	*/
	function checkForEmptyValues() {
		if(document.getElementById("updateValue").value.length === 0) {
			displayErrorMessage(document.getElementById("updateValue"), "Name required, cannot be empty!");
			return true;
		}
		else if(document.getElementById("supportingDocument").files.length === 0) {
			displayErrorMessage(document.getElementById("supportingDocument"), "File required, cannot be empty!");
			return true;
		}

		return false;
	}

	/*
		Method which displays an error message for the passed element.
	*/
	function displayErrorMessage(element, errorMessage) {
		const elParent = element.parentNode;
		removeHelpBlockFromElement(elParent);		//remove the span help-block if it already exists
		clearTimeout(helpBlockTimeout);				//clearTimeout of previous block if present
		
		const helpNode = document.createElement("span");
		helpNode.classList.add("help-block");
		helpNode.textContent = errorMessage;

		elParent.classList.add("has-error");
		elParent.appendChild(helpNode);
		
		helpBlockTimeout = setTimeout(removeHelpBlockFromElement, 6000, elParent);		//execute function to remove help-block after 6 seconds
	}

	/*
		Removes the span help-block child element if present in the passed parent element.
		Also removes it's has-error class if present.
	*/
	function removeHelpBlockFromElement(element) {
		element.querySelectorAll("span.help-block")
				.forEach(childElement => element.removeChild(childElement));
		element.classList.remove("has-error");
	}

	/*
		The modal is populated with text as per the required message.
	*/
	function populateMessageModalText(isError, body, backdropClick, keyboardEscape) {
		if(isError) {
			document.getElementById("modalTitle").textContent = "Error";
			document.getElementById("errorIcon").classList.remove("hidden");
			document.getElementById("successIcon").classList.add("hidden");
			document.getElementById("modalSave").classList.add("hidden");
		}
		else {
			document.getElementById("modalTitle").textContent = "Confirmation";
			document.getElementById("errorIcon").classList.add("hidden");
			document.getElementById("successIcon").classList.remove("hidden");
			document.getElementById("modalSave").classList.remove("hidden");
		}

		document.querySelector(".modal-body-content").innerText = body;
			
		$("#messageModal").modal({
			show: true,
			backdrop: backdropClick,
			keyboard: keyboardEscape
		});
	}

	/*
		Once the checks are completed, the form is submitted.
	*/
	function srCheckConfirmation() {
		document.forms['changeFaMoSpoNameForm'].submit();
	}
</script>
</body>
</html>