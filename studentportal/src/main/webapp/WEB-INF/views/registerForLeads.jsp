<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    
<!DOCTYPE html>
<html lang="en">

<jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" />
</jsp:include>

<style>
.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar .student-image
	{
	border: 2px solid #000;
}

.complete-profile-warpper .sz-content-wrapper.withBgImage .student-info-bar ul.student-info-list li
	{
	color: #333;
}

.action-modal {
	display: none; /* Hidden by default */
	position: fixed; /* Stay in place */
	z-index: 2; /* Sit on top */
	padding-top: 100px; /* Location of the box */
	left: 0;
	top: 0;
	width: 100%; /* Full width */
	height: 100%; /* Full height */
	overflow: auto; /* Enable scroll if needed */
	background-color: rgb(0, 0, 0); /* Fallback color */
	background-color: rgba(0, 0, 0, 0.4); /* Black w/ opacity */
}

/* Modal Content */
.action-modal-content {
	font-family: "Open Sans";
	font-weight: 400;
	background-color: #fefefe;
	margin: auto;
	padding: 20px;
	border: 1px solid #888;
	max-height: calc(100vh - 250px);
	overflow: auto;
	border-radius: 4px;
	font-size: 1.2em;
}

.actionModal-content b {
	font-weight: 700;
}

#fullPageLoading {
	position: fixed;
	height: 100%;
	width: 100%;
	z-index: 10;
	display: flex;
	top: 0;
}

#loader-container {
	margin-top: auto;
	margin-bottom: auto;
	margin-left: auto;
	margin-right: auto;
	background-color: white;
	padding: 20px;
	border-radius: 5px;
	z-index: 11111;
	text-align: center;
}

#loader {
	border: 16px solid #f3f3f3; /* Light grey */
	border-top: 16px solid #d2232a; /* Blue */
	border-radius: 50%;
	width: 120px;
	height: 120px;
	animation: spin 2s linear infinite;
}

@keyframes spin {
	0% { transform: rotate(0deg); }
	100% { transform: rotate(360deg); }
}

.highlight{
	border-color: #d2232a;
}

.highlight:focus {
	border-color: #d2232a;
}
</style>

<body>

	<%@ include file="common/header.jsp"%>

	<div class="sz-main-content-wrapper complete-profile-warpper">
		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Lead Registration" name="breadcrumItems" />
		</jsp:include>

		<div class="sz-content-wrapper dashBoard withBgImage">
			<div class="sz-main-content ">
				<div class="container">


					<h2 class="text-capitalize red" style="margin-top: 50px;">Register Now</h2>
					<div class="clearfix"></div>
					<div class="panel-content-wrapper row">


						<%@ include file="common/messages.jsp"%>

						<div class="col-md-6 form-group" style="margin: 20px">
							<form id="registerLead" >
									<label for='salutation'>Salutation</label> 
									<select id='salutation' class="form-control" style="min-width: 100%" >
										<option disabled="disabled" selected="selected" value='0'>Select
											Salutation</option>
										<option value="Mr.">Mr.</option>
										<option value="Ms.">Ms.</option>
										<option value="Mrs.">Mrs.</option>
										<option value="Miss">Miss</option>
									</select> 
									<label for='firstName'>First Name</label> 
									<input id='firstName' class="form-control" placeholder="Enter First Name"/>
									<label for='lastName'>Last Name</label> 
									<input id='lastName' class="form-control" placeholder="Enter Last Name"/> 
									<label for='email'>Email </label> 
									<input id='email' class="form-control" placeholder="Enter Email Id" /> 
									<label for='number'>Mobile Number</label> 
									<input id='number' class="form-control" placeholder="Enter Mobile Number"/>
									<label for='currentLocation'>Current Location</label> 
									<select id='currentLocation' class="form-control" style="min-width: 100%">
										<option disabled="disabled" selected="selected" value='0'>Select
											Current Location</option>
										<option value="Mumbai">Mumbai</option>
										<option value="Pune">Pune</option>
										<option value="Delhi">Delhi</option>
										<option value="Bangalore">Bangalore</option>
										<option value="Kolkata">Kolkata</option>
										<option value="Hyderabad ">Hyderabad </option>
										<option value="Indore">Indore</option>
										<option value="Chandigarh">Chandigarh</option>
										<option value="Chennai">Chennai</option>
									</select> 
									
									<input type="button" class="btn btn-primary" id='requestOtp' value="Register" />
							</form>
						</div>
						
						<div id="modal" class="action-modal">
							<div id='action-modal-content' class="action-modal-content" style="max-width: 500px;">
								
								<div id="modal-heading"></div>
								
								<div id='modal-text' style="text-align: center;">
								</div>
								
								<div class="wrapper" style="text-align: center;">
									<button type='submit' class='btn btn-primary' id='triggerLogin'>Submit</button>
									<button type='submit' class='btn btn-primary' id='closeModal'>Close</button>
								</div>
							</div>
						</div>

						<div id="fullPageLoading" style="display: none;">
							<div class="modal-backdrop fade in"></div>
							<div id="loader-container">
								<div id="loader"></div>
								<div>Please wait...</div>
							</div>
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="common/footer.jsp" />
	
<script> 

	let salutation;
	let firstName;
	let lastName;
	let email;
	let mobile;
	let agency
	let highestQualification;
	let admissionYear;
	let currentLocation;
	let courseIntrestedIn;
	let agencyPassword;
	let redirectURL;

	$(document).ready(function(){

		$('#closeModal').on('click', function(){
			$('#modal').hide();
		});
		
		
		$('#triggerLogin').click(function(event){

			let otp = $('#otp').val();
			number = $('#number').val();

			let verifyDetails = {
				'mobile' : number,
				'otp' : otp
			}

			console.log('verifyDetails: '+JSON.stringify(verifyDetails))
			
			$.ajax({
				type : 'POST',
				url : '/studentportal/verifyRequestOTP',
				data : JSON.stringify(verifyDetails),
				contentType : "application/json;",
				success: function( data ){
					
					$('#triggerLogin').show();
			        $('#modal-heading').html( '<p style="text-align: center; font-size: 1em"><i class="fa fa-key"></i> <b>Enter Code</b></p>');
		        	$('#modal-text') .html('<p>Incorrect OTP, please try again.</p> <br> <input type=text id="otp" placeholder=Enter OTP  class="form-control"  required=required/>');
					$('#modal').show();
					$('#otp').focus();
					return;
					
				},
				error: function( error ){
					
					$('#modal').hide();
		        	registerLead();
					$('#fullPageLoading').show();
							
				}
			});

		});

		$('#requestOtp').click(function(event){
			
			event.preventDefault();
			$('#fullPageLoading').show();
			
			if( validation() ){
				
			    number = $('#number').val();
				email = $('#email').val();
				
				let body = {
					'emailId' : email,
					'mobile' : number,
					'deviceType' : 'ios'
				}
				
				checkIfLeadPresentForEmailAndMobile( body );
		
			}
			
		});

	});


	function checkIfLeadPresentForEmailAndMobile( body ){
		
		$.ajax({
			type : "POST",
			url : "/studentportal/checkIfLeadPresentForEmailAndMobile",
			contentType : "application/json",
			data : JSON.stringify(body),
			dataType: 'json',
			success: function(data) {	

				console.log('body: '+JSON.stringify(body))
				requestOtp( body );
	
			},
			error: function(error){

				let response = JSON.parse(error.responseText);
				let count = 1;
				
				let modal_text = '<p>Account already present. Please try logging in using the following details </p>';
				modal_text = modal_text + '<table class="table table-borderless"><thead><tr><th style=text-align:center;>Sr No.</th>';
				modal_text = modal_text + '<th style=text-align:center;>Email Id</th><th style=text-align:center;>Mobile No.</th>';
				modal_text = modal_text + '<th style=text-align:center;>Registration Id</th><tr></thead> <tbody>';
					
				for( record in response ){
					modal_text = modal_text + '<tr><td>'+count+'</td><td>'+response[record].emailId+'</td>'+'<td>'+response[record].mobile+'</td>';
					modal_text = modal_text + '<td>'+response[record].registrationId+'</td></tr>'
					count++;
				}
				modal_text = modal_text + '</tbody></table>'
				
				$('#fullPageLoading').hide();
				$('#triggerLogin').hide();
			    $('#modal-heading').html( '<p style="text-align: center; font-size: 1em"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> <b>Error</b></p>');
				$('#modal-text') .html( modal_text);
				$('#modal').show();
			    	
			}
		});

	}
	
	function requestOtp( body ){

		$.ajax({
			type : "POST",
			url : "/studentportal/requestOTP",
			contentType : "application/json",
			data : JSON.stringify(body),
		    dataType: 'json',
		    cache: false,
		    success: function( data ){
		    	
				$('#fullPageLoading').hide();
				$('#triggerLogin').show();
		        $('#modal-heading').html( '<p style="text-align: center; font-size: 1em"><i class="fa fa-key"></i> <b>Enter Code</b></p>');
				$('#modal-text') .html('<p>We have sent you a SMS on '+ number+ ' with a 4 digit verification code</p> <br> <input type=text id="otp" placeholder=Enter OTP  class="form-control"  required=required/>');
				$('#modal').show();
				$('#otp').focus();
				
		    },
		    error: function( error ){

				$('#fullPageLoading').hide();
				$('#modal-text') .html('<p>We have encountered an error, please try again.')
				$('#modal').show();
				
				
		    }
		});
		
	}

	function registerLead(){

		$('#fullPageLoading').show();

		let salutation = $('#salutation').val();
		let firstName = $('#firstName').val();
		let lastName = $('#lastName').val();
		let email = $('#email').val();
		let number = $('#number').val();
		let currentLocation = $('#currentLocation').val();
	
		let body = {
					"salutation":salutation,
					"firstName":firstName,
					"lastName":lastName,
					"emailId":email,
					"mobile":number,
					"currentLocation":currentLocation,
				};

				
		$.ajax({
			type : "POST",
			url : "/studentportal/m/registerLeads",
			contentType : "application/json",
			data : JSON.stringify(body),
			dataType: 'json',
			success: function(data) {
				    
				if( data.isError ){
					$('#fullPageLoading').hide();
					$('#triggerLogin').hide();
				    $('#modal-heading').html( '<p style="text-align: center; font-size: 1em"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> <b>Error</b></p>');
					$('#modal-text') .html('<p>'+data.message+'</p>');
					$('#modal').show();
				}else{
			        	window.location = data.redirectURL;
				}
	
			},
			error: function(error){

				console.log('error: '+JSON.stringify(error))
			    let response = JSON.parse(error.responseText);
				$('#fullPageLoading').hide();
				$('#triggerLogin').hide();
			    $('#modal-heading').html( '<p style="text-align: center; font-size: 1em"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> <b>Error</b></p>');
				$('#modal-text') .html('<p>'+response.message+'</p>');
				$('#modal').show();
			    	
			}
		});

	}

	function validation(){

		let salutation = $('#salutation :selected').val()
		let firstName = $('#firstName').val();
		let lastName = $('#lastName').val();
		let email = $('#email').val();
		let number = $('#number').val();
		let currentLocation = $('#currentLocation :selected').val()
		
		if(salutation === '0'){

			selectValues = { "0": "Please Select Salutation", "Mr.": "Mr.", "Mrs.":"Mrs.", "Miss":"Miss" };
			$('#salutation').empty();
			$.each(selectValues, function(key, value) {
			     $('#salutation') .append($('<option>', { key : value }) .text(value));
			});
			
			$('#salutation').focus();
			$('#salutation').addClass('highlight');
			return false;
	
		}

		if( !firstName ){
			$('#salutation').removeClass('highlight');
			$('#firstName').attr("placeholder", "Please Enter Your First Name");	
			$('#firstName').focus();
			$('#firstName').addClass('highlight');
			return false;
		}
		
		if(!lastName){
			$('#salutation, #firstName').removeClass('highlight');
			$('#lastName').attr("placeholder", "Please Enter Your Last Name");	
			$('#lastName').focus();
			$('#lastName').addClass('highlight');
			return false;
		}

		if(!email){
			$('#salutation, #firstName, #lastName').removeClass('highlight');
			$('#email').val('');
			$('#email').attr("placeholder", "Please Enter Your Email Id");	
			$('#email').focus();
			$('#email').addClass('highlight');
			return false;
		}else if(!isEmail(email)){
			$('#salutation, #firstName, #lastName').removeClass('highlight');
			$('#email').val('');
			$('#email').attr("placeholder", "Please Enter a Proper Email Id");	
			$('#email').focus();
			$('#email').addClass('highlight');
			return false;
		}
		
		if(!number){
			$('#salutation, #firstName, #lastName, #email').removeClass('highlight');
			$('#number').val('');
			$('#number').attr("placeholder", "Please Enter Your Mobile Number");	
			$('#number').focus();
			$('#number').addClass('highlight');
			return false;
		}else if(number.length != 10){
			$('#salutation, #firstName, #lastName, #email').removeClass('highlight');
			$('#number').val('');
			$('#number').attr("placeholder", "Please Enter a Proper Mobile Number");	
			$('#number').focus();
			$('#number').addClass('highlight');
			return false;
		}else{
			if(!checkForMobile(number)){
				$('#salutation, #firstName, #lastName, #email').removeClass('highlight');
				$('#number').val('');
				$('#number').attr("placeholder", "Please Make Sure That The Mobile Number Starts With 7 or 8 or 9");	
				$('#number').focus();
				$('#number').addClass('highlight');
				return false;
			}
		}

		if(currentLocation === '0'){

			selectValues = { "0": "Please Select Salutation", "Mumbai": "Mumbai", "Pune":"Pune", "Delhi":"Delhi", "Bangalore":"Bangalore", "Kolkata":"Kolkata",
					 "Hyderabad":"Hyderabad", "Indore":"Indore", "Chandigarh":"Chandigarh", "Chennai":"Chennai" };
			$('#currentLocation').empty();
			$.each(selectValues, function(key, value) {
			     $('#currentLocation') .append($('<option>', { key : value }) .text(value));
			});
			
			$('#currentLocation').focus();
			$('#currentLocation').addClass('highlight');
			return false;
	
		}
		return true;
	}	
	
	function isEmail(emailId) {
		var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		return regex.test(emailId);
	}

	function checkForMobile(mobileNumber){
		index = mobileNumber.charAt(0);
		if(index == '7' || index == '8' || index == '9'){
			return true;
		}else{
			return false;
		}
	}

</script> 
</body>
</html>