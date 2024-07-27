<!DOCTYPE html>


<%@page import="org.jsoup.Jsoup"%>
<%@page
	import="org.springframework.web.servlet.support.RequestContextUtils"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="java.util.List"%>
<%@page import="com.nmims.daos.PortalDao"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
<%
		
		//String SERVER_PATH = (String)request.getAttribute("SERVER_PATH");
		String examAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "exam/logoutforSSO";
		String acadsAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "acads/logoutforSSO";
		String ltiAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "ltidemo/logoutforSSO";
		String csAppLogoutUrl = (String)pageContext.getAttribute("server_path") + "careerservices/logoutforSSO";
		
	%>



<html lang="en">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />


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
			<jsp:param value="Student Zone;Login" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">


			<div class="sz-content-wrapper dashBoard withBgImage loginPage">

				<div class="container">
					<div class="contentMainWraper">
						<%@ include file="common/messages.jsp"%>
						<div class="login-cont">
							<h4>LOGIN</h4>
							<form class="profileForm" id="leadLogin" action="loginForLeads" method="post">
								<div class="form-group">
									<label for="name">EMAIL ID / MOBILE / REGISTATION NUMBER</label> 
									<select id='loginTypeSelect' class="form-control">
										<option value="NA" selected="selected">Select Login Type</option>
										<option value="Mobile_No__c">Mobile Number</option>
										<option value="Email">Email Id</option>
										<option value="nm_RegistrationNo__c">Registration Id</option>
									</select> <br>
									<input type="text" class="form-control" placeholder="Select Login Type" id="userId" name="userId">
								</div>
								<div class="form-group">
										<a href="student/registerForLeadsForm">Not Registered? Register Now!</a>
								</div>
								<button type="submit" id="loginBtn" class="btn  btn-default">Login</button>
							</form>
						</div>

						<div class="row">
							<div class="col-md-12">
								<div class="panel-content-wrapper latestNews">
									<!-- <h2 class="text-capitalize">Welcome to NGASCE Student Zone</h2> -->
									<div class="clearfix"></div>
									<img src="assets/images/overview.jpg" class="img-responsive" />
									<p align="justify">Welcome to NMIMS Global Access - School
										for Continuing Education (NGASCE)! You are about to log in to
										the world of Online Learning at NGASCE, a world made possible
										due to a combination of 30 years of legacy of best in class
										education and state of the art learning technology!</p>
									<p align="justify">As you log in using the credentials
										given to you by the University, please take time to go through
										your profile and update your contact information. The details
										mentioned there are your details as per the current University
										Student Database. In case there is any change or any error in
										these details, it will hamper the University to stay in touch
										with you.</p>
									<p align="justify">With this Portal, we hope to provide you
										all the support you need during your enrolment with the
										Program offered by the University. It will be our endevour to
										keep improving your experience with this Portal as we go
										along.</p>
									<p>Happy Learning!</p>
									<p>
										<b>Team NGASCE</b>
									</p>
								</div>
							</div>
						</div>
															
						<div id="modal" class="action-modal">
							<div id='action-modal-content' class="action-modal-content" style="max-width: 500px;">
								
								<div id='modal-heading'>
								</div>
								
								<div id='modal-text' style="text-align: center;">
								</div>
								
								<div class="wrapper" style="text-align: center;">
									<button type='submit' class='btn btn-primary' id='triggerLogin' onclick="verifyDetails()">Submit</button>
									<button type='submit' class='btn btn-primary' id='closeButton'>close</button>
								</div>
							</div>
						</div>
						
					</div>
				</div>
			</div>


		</div>
	</div>
	<div id="examApp"></div>
	<div id="acadsApp"></div>
	<div id="ltiApp"></div>
	<div id="csApp"></div>
	<jsp:include page="common/footer.jsp" />

	<script>

		let mobile;
		let otpsent = false;
		
		$(document).ready(function(e) {
			
			$('#loginBtn').prop('disabled', false);
			
			$('#loginTypeSelect').on('change', function(event){

				var selectedText = $(this).find("option:selected").text();
				if( selectedText == "Select Login Type" )
					alert('Please select a login type');
				$('#userId').attr("placeholder", selectedText);
				
			});
			
			$('#loginBtn').click(function(event){

				event.preventDefault();

				$.ajax({
		             type: 'POST',
		             url: '/studentportal/login',
		             data: {
		                 userId: "77999999999",
		                  password: "Roger@321"
		             },
		             success: function (data) {
		                 $(".error_msg").text(data);
		             }
		         }); 
				
			    $.ajax({
		             type: 'POST',
		             url: '/acads/login',
		             data: {
		                 userId: "77999999999",
		                  password: "Roger@321"
		             },
		             success: function (data) {
		                 $(".error_msg").text(data);
		             }
		         }); 

		        $.ajax({
		             type: 'POST',
		             url: '/exam/login',
		             data: {
		                 userId: "77999999999",
		                  password: "Roger@321"
		             },
		             success: function (data) {
		                 $(".error_msg").text(data);
		             }
		         });

				let userId = $('#userId').val();
				let loginType = $('#loginTypeSelect').val();
				let selectedText = $('#loginTypeSelect').find("option:selected").text();

				if( validate() ){
					
					let body = {
								'userId' : userId,
								'loginType' : loginType
							};

					getLeadDetailsAndSendOTP(body)
					
				}
				
				
			});

			$('#closeButton').on('click', function(){

				$('#modal').hide();

			});
			
			//////Enter Method for submitting form//////
		    $(document).keypress(function (e) {
		    	var key = e.which;
		    	
		        if(key == 13){
		        	
		        	if( otpsent ){
		        		verifyDetails( e );
		        		return;
		        	}
		        	if( validate() ){
		        		
		        		let body = {
								'userId' : userId,
								'loginType' : loginType
							};
		        		
		        		getLeadDetailsAndSendOTP(body);
			        	
		        	}
		        	
		            return false;
		        	
		        }
		    	
		     });
		     document.getElementById("userId").focus();
		});

		function validate(){
			
			let userId = $('#userId').val();
			let loginType = $('#loginTypeSelect').val();
			let selectedText = $('#loginTypeSelect').find("option:selected").text();

			if ( loginType == "NA"){

				$('#triggerLogin').hide();
				$('#modal-heading').html( '<p style="text-align: center; font-size: 1em"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> <b> Warning </b></p>' );
				$('#modal-text').html('<p>Please select a login type.</p>');
				$('#modal').show();
				return false;
			}else if( userId==null || userId=="" || typeof userId == "undefined" ){
				$('#triggerLogin').hide();
				$('#modal-heading').html( '<p style="text-align: center; font-size: 1em"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> <b> Warning </b></p>' );
				$('#modal-text').html('<p>Please enter your '+selectedText+'.</p>');
				$('#modal').show();
				return false;
			}

			$('#modal-heading').html( '<p style="text-align: center; font-size: 1em"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i> <b> Error </b></p>' );
			
			if( loginType == "Email" ){

				if( !isEmail(userId) ){
					$('#triggerLogin').hide();
					$('#modal-text').html('<p>Incorrect email id, please enter a valid email id.</p>');
					$('#modal').show();
					return false;
				}
			}else if( loginType == "Mobile_No__c" ){

				if( !Number(userId) || userId.length < 10 || userId.length > 10 ){
					$('#triggerLogin').hide();
					$('#modal-text').html('<p>Incorrect mobile number, please enter a valid mobile number.</p>');
					$('#modal').show();
					return false;
				}
			}else if( loginType == "nm_RegistrationNo__c" ) {

				if( !Number(userId) ){
					$('#triggerLogin').hide();
					$('#modal-text').html('<p>Incorrect registration number, please enter a valid registration number.</p>');
					$('#modal').show();
					return false;
				}
			}
			
			return true;
			
		}
		
		function getLeadDetailsAndSendOTP( body ){
			
			$.ajax({
				type : "POST",
				url : "/studentportal/getLoginDetailsForLeads",
				contentType : "application/json",
				data : JSON.stringify(body),
				dataType: 'json',
				success: function(data) {              

					mobile = data.mobile;
					sendOtp(data.mobile, data.emailId);
					otpsent = true;
					return;
				    	
			     },
				error: function(error){

					let loginType;
					let response = JSON.parse(error.responseText);
			        $('#triggerLogin').hide();
			        
			        switch( response.loginType ){
			        	case "nm_RegistrationNo__c":
			        		loginType = 'registration number.';
				        	break;
			        	case "Mobile_No__c":
			        		loginType = 'mobile number.';
				        	break;
			        	case "Email":
			        		loginType = 'email id.';
				        	break;
			        }
			        
					$('#modal-text').html('<p>'+response.errorMessage+' for ' + loginType + '</p>');
					$('#modal').show();
			            
			   	}
			});
			
		}
		
		function verifyDetails(event){

			let otp = $('#otp').val();

			let verifyDetails = {
				'mobile' : mobile,
				'otp' : otp
			}
			
			$.ajax({
				type : 'POST',
				url : '/studentportal/verifyRequestOTP',
				data : JSON.stringify(verifyDetails),
				contentType : "application/json;",
				complete: function (xhr, status) {

			        if (status === 'error' || !xhr.responseText) {

						$('#triggerLogin').show();
						$('#modal-heading').html( "<p style='text-align: center; font-size: 1em'><i class='fa fa-key'></i> <b>Enter Code</b></p>" );
			        	$('#modal-text').html('<p>Incorrect OTP, please try again.</p> <br> <input type=text id="otp" placeholder=Enter OTP  class="form-control"  required=required/>');
						$('#modal').show();
						$('#otp').focus();
						return;
						
			        }
			        else {

						$('#leadLogin').submit();
						$('body').prepend(' <div id="fullPageLoading"> <div class="modal-backdrop fade in"></div> <div id="loader-container"> <div id="loader"></div> <div> Please wait... </div> </div> </div>')
								
			        }
				} 
			});

		}

		function sendOtp(mobile_number, email){

			let otpDetails = {
				'mobile' : mobile_number,
				'emailId' : email,
				'deviceType' : 'ios'
			}

			$.ajax({
				type : "POST",
				url : "/studentportal/requestOTP",
				contentType : "application/json",
				data : JSON.stringify(otpDetails),
			    dataType: 'json',
			    cache: false,
			    complete: function (xhr, status) {

			        if (status === 'error' || !xhr.responseText) {
						alert("An error occured while registering, please try again later.")
			        }
			        else {
				        
						if (xhr.status == 200) {
							
							$('#triggerLogin').show();
							$('#modal-heading').html( '<p style="text-align: center; font-size: 1em"><i class="fa fa-key"></i> <b>Enter Code</b></p>' );
							$('#modal-text') .html('<p>We have sent you a SMS on mobile number '+ mobile_number+ ' with a 4 digit verification code</p> <br> <input type=text id="otp" placeholder=Enter OTP  class="form-control"  required=required/>');
							$('#modal').show();
							$('#otp').focus();
						}else{

							$('#modal-heading').html( '<p style="text-align: center; font-size: 1em"><i class="fa fa-key"></i> <b>Enter Code</b></p>' );
							$('#modal-text') .html('<p>We have encountered an error, please try again.')
							$('#modal').show();
						}
			        }
				} 
			});

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


	<script>
		//Logout users from all apps when they visit login page
		$( "#examApp" ).load( "<%=examAppLogoutUrl%>" );
		$( "#acadsApp" ).load( "<%=acadsAppLogoutUrl%>" );
		$( "#ltiApp" ).load( "<%=ltiAppLogoutUrl%>" );
		$( "#csApp" ).load( "<%=csAppLogoutUrl%>" );
	</script>
</body>
</html>