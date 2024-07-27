<!DOCTYPE html>
<html lang="en">

<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Change in Contact Details" name="title" />
</jsp:include>
<style>
	input {
		color: black;
	}
	#otpbox{
		border: 1px solid;
		border-color: black;

	}	
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
<jsp:include page="../common/header.jsp" />
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
   			<div class="container-fluid">
       			<ul class="sz-breadcrumbs">
	        		<li><a href="/studentportal/home">Student Zone</a></li> 
	        		<li><a href="selectSRForm">Select Service Request</a></li>
	        		<li><a href="changeInContactDetailsSRForm">Change in Contact Details SR</a></li>
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
								<h2 class="panel-title red text-capitalize">Change in Contact Details Service Request</h2>
							</div>
							
							<div class="panel-body">
								<jsp:include page="../common/messages.jsp" />
								<form name="changeContactDetailsForm" action="changeInContactDetailsSR" method="post">
									
									<input type="hidden" id="sapid" name="sapid" value="${studentSapid}" />
									<div class="row">
										<div class="col-md-4 form-group">
											<label for="detailType">Choose the Contact Detail you want to Update</label>
											<select class="form-control" id="detailType" name="detailType" 
												onchange="changeFormElementAttr(this.value)" disabled>
												<option value="">Select an Option</option>
												<option value="emailId">Change Email Id</option>
												<option value="mobile">Change Mobile No</option>
											</select>
										</div>
									</div>
									
									<div class="row">
										<div class="col-md-8 form-group hidden" id="currentValueBlock">
											<label for="currentValue"></label>
		  									<input class="form-control" type="text" readonly id="currentValue" name="currentValue" />
		  								</div>
	 								</div>
	 									
 									<div class="row">
 										<div class="col-md-8 form-group hidden" id="updateValueBlock">
		  									<label for="updateValue"></label>
		  									<input class="form-control" type="text" id="updateValue" name="updateValue"
		  										onkeydown="validateKey(event)" onpaste="validatePaste(event)" />
		  								</div>
	  								</div>
	 									
	 								<div class="row">
										<div class="col-md-6 form-group">
											<button id="formSubmission" name="formSubmission" disabled
												class="btn btn-large btn-primary">Verify</button>
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
										<i id="errorIcon" class="fa-solid fa-exclamation" aria-hidden="true"></i>
										<i id="successIcon" class="fa-solid fa-check" aria-hidden="true"></i>
										<h4 id="modalTitle" class="modal-title"></h4>
									</div>
									<div class="modal-body">
										<p class="modal-body-content"></p>
										<input id="otpbox" type="text" name="textbox" maxlength="4" 
											onkeypress='return event.charCode >= 48 && event.charCode <= 57'
											required />
									</div>
									<div class="modal-footer">
										<button type="button" id="modalSave" class="modal-button btn btn-large btn-primary"
											onclick="verifyOtp()">Proceed</button>
										<a class="modal-button btn btn-danger" data-dismiss="modal" onclick="ClearFields()">Close</a>
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
	let studentEmailMobileDetailsObject, helpBlockTimeout;
	const studentSapid = document.getElementById("sapid").value;

	const emailKeyRegex = /[a-z0-9_\+&\*\-\.@]/i;
	const mobileKeyRegex = /[0-9\+\-,\(\)\.\s]/i;

	/* OWASP Email validation regex */
	const emailAddressRegex = /^[a-z0-9_\+&\*\-]+(?:\.[a-z0-9_\+&\*\-]+)*@(?:[a-z0-9\-]+\.)+[a-z]{2,7}$/i;
	
	/* Generic validation for worldwide Mobile Phone numbers
	1. Every mobile number uses only 0-9 and sometimes space/dash/comma/period/round brackets.
	2. Minimum 7 characters and a Maximum of 34 characters
	3. Allow one space/dash/comma/period/round brackets character at a time, no repetition
	4. Can begin with a plus sign or two leading zeros. */
	const mobileNoRegex = /^(\+|00)?([0-9][\-,\(\)\.\s]?){6,31}[0-9\)]$/i;

	/*
		After the DOM is loaded the async function executes,
		and fetches student emailId and mobileNo details and stores in an Object.
		The values from the Object are then later on added as form attributes, based on user action.
	*/
	document.addEventListener("DOMContentLoaded", async function() {
		try {
			const response = await fetch(`/studentportal/m/student/getCurrentEmailIdMobileNo/${studentSapid}`);
			if(response.ok) {
				const responseObj = await response.json();
				studentEmailMobileDetailsObject = responseObj.details;
				document.getElementById("detailType").removeAttribute("disabled");
			}
			else {
				let errorText = await response.text();
				console.error("Error while fetching Student details: ", errorText);
				populateMessageModalText(true, "Error while fetching details.\nPlease refresh page and try again.", true, true);
			}
		}
		catch(err) {
			console.error("Unable to fetch student EmailId and MobileNo details due to Network Error: ", err);
			populateMessageModalText(true, "Network Error while fetching details.\nPlease refresh page and try again.", true, true);
		}
	});

	/*
		Depending on the option selected by the user from the dropdown, the form is updated, 
		the values in the form attributes are also updated accordingly
	*/
	function ClearFields() {

	     document.getElementById("otpbox").value = "";
	     
	}
	function changeFormElementAttr(selection) {
		const currentValueElement = document.getElementById("currentValueBlock");
		const updateValueElement = document.getElementById("updateValueBlock");
		const submitBtn = document.getElementById("formSubmission");
		const resetBtn = document.getElementById("reset");
		let currentValue = "";
		switch(selection) {
			case "emailId":
				currentValueElement.firstElementChild.textContent = "Current Email ID: ";
				updateValueElement.firstElementChild.textContent = "Enter new Email ID: ";
				updateValueElement.lastElementChild.setAttribute("placeholder", "Enter Email ID here");
				currentValue = studentEmailMobileDetailsObject[selection];
				break;
			case "mobile":
				currentValueElement.firstElementChild.textContent = "Current Mobile No: ";
				updateValueElement.firstElementChild.textContent = "Enter new Mobile No: ";
				updateValueElement.lastElementChild.setAttribute("placeholder", "Enter Mobile Number here");
				currentValue = studentEmailMobileDetailsObject[selection];
				break;
			default:
				currentValueElement.firstElementChild.textContent = selection;
				currentValueElement.lastElementChild.value = currentValue;
				updateValueElement.firstElementChild.textContent = selection;
				updateValueElement.lastElementChild.value = currentValue;
				customFormReset();
				currentValueElement.classList.add("hidden");
				updateValueElement.classList.add("hidden");
				submitBtn.setAttribute("disabled", "disabled");
				resetBtn.setAttribute("disabled", "disabled");
				return;											//return to exit the function
		}
		currentValueElement.lastElementChild.value = currentValue;
		currentValueElement.classList.remove("hidden");
		updateValueElement.lastElementChild.value = "";
		updateValueElement.classList.remove("hidden");
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
											method: "GET",
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
													.then(text => { throw new Error(text) });
								})
								.then(respText => respText === "true")
								.catch(err => {
										console.error("Error while checking student SR Eligibility: ", err);
										populateMessageModalText(true, "Error while checking Eligibility of student" + 
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
		updateValueElement.value = updateValueElement.defaultValue;
	}

	/*
		On clicking the Submit button, eligibility and validation checks are done,
		when these checks are successfully completed, the confirmation modal is shown to the user.
	*/
	document.changeContactDetailsForm.addEventListener("submit", async function(event) {
		event.preventDefault();			//Initially disabling the form submit
	    const updateValueText = this.querySelector("input[name=updateValue]").value;		// this - object of form
	    const currentValueText = this.querySelector("input[name=currentValue]").value;
	    const otpTextBox = document.getElementById("otpbox");
	    const select = document.getElementById("detailType");
		const selectedValue = select.options[select.selectedIndex].value;
		const valueName = (selectedValue === "emailId") ? "Email address" : "Mobile ";		//to display the selected value to the user in proper format

		//Checks if the update value field is empty
		if(document.getElementById("updateValue").value.length === 0) {
			displayErrorMessage(document.getElementById("updateValue"), "Value required, cannot be empty!");
			return;
		}
		
	    let isEligible = await srEligibilityCheck(studentSapid, selectedValue);
		if(!isEligible){
			otpTextBox.classList.add("hidden");
			populateMessageModalText(true, "Not Eligible to raise Service Request," +
					"\nRequest to Change " + valueName + " already In Progress." +
					"\nContact Support for any further help.", true, true);
			}
			
		else if(updateValueText === currentValueText){
			otpTextBox.classList.add("hidden");
			populateMessageModalText(true, "Entered " + valueName + " cannot be the same as Current " + valueName +
				". Please check and try again.", true, true);
		}
		else if(!charSeqRegexCheck(selectedValue, updateValueText)){
			otpTextBox.classList.add("hidden");
			populateMessageModalText(true, "Invalid " + valueName + " entered!" + 
					"\nPlease check and try again.", true, true);
			}
			
		
	    
	    else{
	    	otpTextBox.classList.remove("hidden");
	    	sendOtp(updateValueText,selectedValue,studentSapid,otpTextBox);
	    	populateMessageModalText(false, "Please Wait While We Are Sending Otp To Your New "+valueName, "static", false);}
	    	
	});


	/*
		On keyDown event, the entered key is validated.
	*/
	function validateKey(e) {
		const select = document.getElementById("detailType");
		const selectedValue = select.options[select.selectedIndex].value;
		const valueName = (selectedValue === "emailId") ? "Email address" : "Mobile number";		//to display the selected value to the user in proper format

		//Check to bypass Cut, Copy, Paste Keyboard Shortcuts
		if(e.ctrlKey || e.metaKey || e.key.toLowerCase() === "c" || e.key.toLowerCase() === "v" || e.key.toLowerCase() === "x")			
			return true;

		switch(e.keyCode) {
			case 8:
			case 9:
			case 13:
			case 16:
			case 37:
			case 39:		//Key Codes: backspace - 8, tab - 9, enter - 13, shift - 16, left arrow - 37, right arrow - 39
				break;
			default:
			    const key = e.key;
			    if(!checkIfKeyValid(selectedValue, key)) {			//error logged and displayed to the user, and action prevented
			    	console.error("Entered key: ", key, " is invalid!");
			    	displayErrorMessage(document.getElementById("updateValue"), "Entered text: " + key + " is invalid for the " + valueName + " field.");
			    	e.preventDefault();
			    	return false;
				}
			    break;
		}
	}

	/*
		Checks if the entered key is valid or not.
		The regex used to validate depends on the option selected in dropdown.
	*/
	function checkIfKeyValid(elementValue, key) {
		let valid = true;
		if(elementValue === "emailId")
			valid = emailKeyRegex.test(key);
		else if(elementValue === "mobile")
			valid = mobileKeyRegex.test(key);

		return valid;
	}

	/*
		On paste event, the character set is validated.
	*/
	function validatePaste(e) {
		let text = e.clipboardData.getData('text');
		const select = document.getElementById("detailType");
		const selectedValue = select.options[select.selectedIndex].value;
		const valueName = (selectedValue === "emailId") ? "Email address" : "Mobile number";	//to display the selected value to the user in proper format
		
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
	function charSeqRegexCheck(selectedElementValue, charSeq) {
		if(selectedElementValue === "emailId" && !emailAddressRegex.test(charSeq)) 
			return false;
		else if(selectedElementValue === "mobile" && !mobileNoRegex.test(charSeq)) 
			return false;
		else
			return true;
	}

	/*
		Method which displays the provided error message for the passed element.
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
	function sendOtp(updateValueText,selectedValue,studentSapid,otpTextBox){
		console.log("This Is UpdateValueText   "+ updateValueText +  "   This Is SelectedValue "+ selectedValue +"  This Is StudentSapid "+ studentSapid);
		let otpDetails ={};
		

		if(selectedValue.localeCompare('emailId')){
			otpDetails =
				{
					'subType':selectedValue,
					'mobile':updateValueText,
					'sapid':studentSapid,
					}
		}
		else{
			otpDetails = {
				'subType':selectedValue,
				'emailId':updateValueText,
				'sapid':studentSapid,
				}
		}
				
		

		$.ajax({
			type : "POST",
			url : "/studentportal/m/requestForOTP",
			contentType : "application/json",
		    dataType: 'json',
		    contentType : 'application/json',
		    data : JSON.stringify(otpDetails),
		    cache: false,
		    complete: function (xhr, status) {
			    const otpTextBox = document.getElementById("otpbox");
				console.log('xhr: '+xhr)
			
				
		        if (status === 'error' || !xhr.responseText) {console.log("In The Function Of the RequestOtp");
		        	otpTextBox.classList.add("hidden");
		        	populateMessageModalText(false, "Error while sending Otp.\nPlease try again.", true, true);
		        }
		        else {
					if (xhr.status == 200) {
						otpTextBox.classList.remove("hidden");
						populateMessageModalText(false, "OTP Has Been Sent To Your New "+selectedValue, "static", false);
						}
						
					else{
						otpTextBox.classList.add("hidden");
						populateMessageModalText(true, "Something Went Wrong", true, true);
					}
		        }
				
				}
		});
	}

	function verifyOtp(){
		const otpTextBox = document.getElementById("otpbox");
		var otp = $("#otpbox").val();
		var sapid = document.getElementById("sapid").value;
		const select = document.getElementById("detailType");
		const selectedValue = select.options[select.selectedIndex].value;
		const valueName = (selectedValue === "emailId") ? "Email address" : "Mobile No ";
		let otpDetails ={'otp':otp,
				'sapid':sapid,
				'subType':selectedValue};
					
		$.ajax({
			type : "POST",
			url : "/studentportal/m/verifyForOTP",
			contentType : "application/json",
		    dataType: 'json',
		    contentType : 'application/json',
		    data : JSON.stringify(otpDetails),
		    cache: false,
		    complete: function (xhr, status) {
				console.log('xhr: '+xhr)
		        if (status === 'error' || !xhr.responseText) {
		        	populateMessageModalText(false, "Please Check Your Otp Or "+valueName+"  .\nPlease try again.","static", true);	
		        }
		        else {
					if (xhr.status == 200) {
						populateMessageModalText(false, "OTP Verified Successfully For "+valueName, "static", false);
						srCheckConfirmation();
						}
						
					else{
						
						populateMessageModalText(true, "Something Went Wrong", true, true);
					}
		        }
		}
		});
	}
	
	/*
		Once the checks are completed, the form is submitted.
	*/
	function srCheckConfirmation() {
		document.forms['changeContactDetailsForm'].submit();
	}
</script>
</body>
</html>