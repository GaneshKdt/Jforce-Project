/*
 * JS file which contains the method which is called when the forgotPassword form is submitted.
 * Consists of a Fetch API call, and a function which creates an Element from scratch to display the response message received from the server.
 * @author: Raynal Dcunha
 */

const url = "studentportal/m/forgotPassword?";
let responsePopup = document.getElementById("responsePopup");
let responsePopupStackCount = 0, endTime = 0;
let successResponse = false;		//a flag to denote whether the success message is being displayed

//Creating elements with attributes, which are used while displaying the success/error message 
let buttonInner = document.createElement("button");
buttonInner.setAttribute("class", "btn-close");
buttonInner.setAttribute("data-bs-dismiss", "alert");
buttonInner.setAttribute("aria-hidden","true");

let countBadge = document.createElement("span");
countBadge.setAttribute("id", "countBadge");
countBadge.setAttribute("class", "badge bg-secondary ms-2");
countBadge.setAttribute("aria-hidden","true");

let faIcon = document.createElement("i");
faIcon.setAttribute("id", "faIcon");
faIcon.setAttribute("aria-hidden","true");

//async 
function submitForm(e, form) {
	e.preventDefault();
	document.getElementById("getPassBtn").classList.add("no-click");		//disabling the submit button to avoid multiple form submits 

	if(!successResponse) {
		let request = new Request(url + new URLSearchParams({
																userId: form.userId.value
															}), 
									{
										method: 'GET',
										headers: new Headers({
										    'Content-Type': 'application/json; charset=UTF-8'
										})
									});
		
		try {
			fetch(request)
			 .then(function(response) {
				 if (response.ok)
					 return response.text();
				 else {
					 if(response.status != 400) 
						 throw new Error("Something went wrong! Please try again.");
					 
					 return response.text().then(function(text) { throw new Error(text) });			//throws an Exception, if the response status is not 200 OK
				 }
			 })
			 .then(function(response) {
				 successResponse = true;					//the successResponse flag is set to true, to denote a success message is shown to the user
				 displayResponse(response, "success");
				 endTime = new Date().getTime() + 2 * 60 * 1000 + 1000;			//specifying the end time, which is used in the displayCountdown() function
				 setTimeout(function() { successResponse = false }, 2 * 60 * 1000);			//flag is reset after 2 minutes
			 })
			 .catch(function(error) {
				 displayResponse(error, "error");
			 })
			 .finally(function() {
				 resetFormAndEnableButton(form);
			 });
		}
		catch(error) {
			displayResponse("Error: Something went wrong! Please try again.","error");
			resetFormAndEnableButton(form);
		}
		
		//Fetch API call using Async Await [commented to extend support to IE11]
//		try {
//			let response = await fetch(request);
//			try {
//				let responseStatus = await new Promise(async function(resolve, reject) {
//					if (response.ok)
//						return resolve(response.text());
//					else {
//						if(response.status != 400) 
//							return reject("Something went wrong! Please try again.");
//						
//						let errorText = await response.text();
//						return reject(errorText);
//					}
//				});
//				
//				successResponse = true;
//				displayResponse(responseStatus, "success");
//				endTime = new Date().getTime() + 2 * 60 * 1000 + 1000;
//				setTimeout(function() { successResponse = false }, 2 * 60 * 1000);
//			}
//			catch(error) {
//				displayResponse("Error: " + error, "error");
//			}
//		}
//		catch(error) {
//			displayResponse("Error: Something went wrong! Please try again.","error");
//		}
	} 
	else {
		displayResponse("Password is sent on your registered Email Address, please wait before sending another request!", "warning");
		resetFormAndEnableButton(form);
	}
}

//function which creates a div, adds child Elements to it and displays the success/error/warning message
//parameters: 	text - Message to be displayed
//			  	messageType - type which denotes whether the message is a success, error or warning message
function displayResponse(text, messageType) {
	let responseText = document.getElementById("responseText");
	countBadge.classList.remove("timerBadge");
	
	if(responseText == null) {			//checks if responseText element already exists
		responseText = document.createElement("div");
		responseText.setAttribute("id", "responseText");
		responseText.setAttribute("role", "alert");
		setResponsePopupAttrs(responseText, text, messageType);
		
		responsePopup.appendChild(responseText);
	}
	else {
		let responseTextContent = responseText.childNodes[1].nodeValue;
		
		if(text == responseTextContent) {
			if(messageType !== "warning") {
				//increments the count (shown in countBadge element) if the same message is being displayed
				responsePopupStackCount++;
				
				countBadge.textContent = responsePopupStackCount;
				responseText.appendChild(countBadge);
			}
			else {
				//To highlight the countBadge element for 1200 ms
				countBadge.setAttribute("style","box-shadow: 0 0 0 0.25rem rgba(130, 138, 145, 0.5)");
				setTimeout(function() { countBadge.removeAttribute("style") }, 1200);
			}
		}
		else {
			setResponsePopupAttrs(responseText, text, messageType);
		}
	}
	
	if(messageType === "warning") {
		countBadge.classList.add("timerBadge");		//adding the timerBadge class (for css) which makes the countBadge element expand in size 
		displayCountdown();			//displayCountdown() fn is called before the timeInterval, so that a valid time is shown at the start of the displayed response message 
		let timeInterval = setInterval(function() {
				displayCountdown();
				
				if(successResponse === false)		//clear the timeInterval when the flag is reset
					clearInterval(timeInterval);
			}, 500);
		responseText.appendChild(countBadge);
	}
}

//function which appends attributes to the responseText element depending of the messageType specified
function setResponsePopupAttrs(responseText, text, messageType) {
	responsePopupStackCount = 0;		//the count is reset to zero, since a new message is being displayed
	
	if(messageType === "success") {
		responseText.setAttribute("class", "alert alert-success alert-dismissible");
		faIcon.setAttribute("class","fas fa-check-circle me-2");
	}
	else if(messageType === "error") {
		responseText.setAttribute("class", "alert alert-danger alert-dismissible");
		faIcon.setAttribute("class","fas fa-times-circle me-2");
	}
	else if(messageType === "warning") {
		responseText.setAttribute("class", "alert alert-warning alert-dismissible");
		faIcon.setAttribute("class","fas fa-exclamation-triangle me-2");
	}
	responseText.textContent = text;
	
	responseText.insertBefore(faIcon, responseText.childNodes[0]); 
	responseText.appendChild(buttonInner);
	responsePopupStackCount++;
}

//function which acts as a countDown timer, after the countDown is finished the successResponse flag is reset
function displayCountdown() {
	let time = endTime - new Date().getTime();
	
	let minutes = Math.floor(time / (1000 * 60));
	let seconds = Math.floor((time % (1000 * 60)) / 1000);
	let timerClock = "0" + minutes + "m : " + ((seconds < 10) ? "0" + seconds : seconds) + "s";
	countBadge.textContent = timerClock;
		
	if(time < 1000) 
		successResponse = false;
}

//function which resets the form field values and re-enables the form submit button
function resetFormAndEnableButton(form) {
	form.reset();
	document.getElementById("getPassBtn").classList.remove("no-click");
}
