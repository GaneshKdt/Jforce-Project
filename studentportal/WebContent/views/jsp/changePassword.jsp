<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<html class="no-js"> <!--<![endif]-->
	
	<jsp:include page="jscss.jsp">
		<jsp:param value="Change Password" name="title" />
	</jsp:include>
	
	<body class="inside">
	
   <%@ include file="header.jsp"%>
    
    <section class="content-container login">
        <div class="container-fluid customTheme">
          <div class="row">
             <legend>Change Password</legend>
          </div> <!-- /row -->
           
          <%@ include file="messages.jsp" %>
		  
		  <div class="">          
            <div class="col-xs-18 panel-body">
	          <div class="bullets">
                <h4>Password Policy</h4>
                <p>Please adhere to the below password policy while you set your new password</p>
                <ul class="policy">
                  <li><i class="fa fa-arrow-circle-right"></i> Passwords cannot contain the user's account name or parts of the user's full name that exceed two consecutive characters.</li>
                  <li><i class="fa fa-arrow-circle-right"></i> Passwords must be at least eight characters in length.</li>
                  <li><i class="fa fa-arrow-circle-right"></i> Passwords must contain characters from at least three of the following four categories: </li>
                  <li><i class="fa fa-arrow-circle-right"></i> English uppercase alphabet characters (A-Z)</li>
				  <li><i class="fa fa-arrow-circle-right"></i> English lowercase alphabet characters (a-z)</li>
				  <li><i class="fa fa-arrow-circle-right"></i> Numbers (0-9)</li>
				  <li><i class="fa fa-arrow-circle-right"></i> Non-alphanumeric characters (for example, !$#%)</li>
                </ul>
              </div>
			  <br>
              <div class="panel-body">
              
                <form role="form" id="change-password" action="savePassword" method="post">
                <div class="row">          
	              <div class="col-sm-9">
                    <div class="form-group">
                      <label for="password">Enter New Password</label>
                      <input type="password" class="form-control" id="password" name="password" placeholder="Enter Password">
                    </div>
                    <div class="form-group">
                      <label for="confirm_password">Confirm Password</label>
                      <input type="password" class="form-control" id="confirm_password" name="confirm_password" placeholder="Confirm Password">
                    </div>
                  </div>
	              <div class="col-sm-9 bottom-pass-button">
                    <button type="submit" class="btn btn-primary">Change Password</button>
                  </div>
                </div>  
                </form>
                
              </div> <!--/module-box-->
            </div> <!-- /col-xs-6 -->
          </div> <!-- /row -->
          
        </div> <!-- /container -->
    </section>
    
    <jsp:include page="footer.jsp" />
    
	
    
	<script>
		$(function() {
		  $( "#change-password" ).validate({
			rules: {
			  password: {
					required: true,
					minlength: 8},
			  confirm_password: {
					equalTo: "#password"
			}
		  }
			  
		  });
		});
	</script>
    
    
  </body>
</html>
 --%>

<!DOCTYPE html>

<html lang="en">




<jsp:include page="common/jscss.jsp">
	<jsp:param value="Change Password" name="title" />
</jsp:include>


<style>
	.has-error {
		margin-bottom: 1em;
	}
	.help-block {
	    font-size: 15px;
	    font-weight: 600;
	    margin-top: 0.65em;
	}
</style>
<body>

	<%@ include file="common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Change Password" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar">  
				<jsp:include page="common/left-sidebar.jsp">
					<jsp:param value="Change Password" name="activeMenu" />
				</jsp:include>
			</div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">Change Password</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="common/messages.jsp"%>

							<p>Please adhere to the below password policy while you set
								your new password</p>
							<ul>
								<li><p>Passwords cannot contain the user's account name
										or parts of the user's full name that exceed two consecutive
										characters.</p></li>
								<li><p>Passwords must be at least eight characters in
										length.</p></li>
								<li><p>Passwords must contain characters from the following 
										three categories:</p></li>

								<ul>
									<li><p>English alphabet characters (A-Z/a-z)</p></li>
									<li><p>Numbers (0-9)</p></li>
									<li><p>Non-alphanumeric characters (for example, !$#%)</p></li>
								</ul>

							</ul>

							<form role="form" id="change-password" action="savePassword"
								onsubmit="return validatePassword()" method="post">
								<div class="row">
									<div class="col-md-6">
										<div class="form-group">
											<label for="password">Enter New Password</label> <input
												type="password" class="form-control" id="password"
												name="password" placeholder="Enter Password"
												required="required">
										</div>
										<div class="form-group">
											<label for="confirm_password">Confirm Password</label> <input
												type="password" class="form-control" id="confirm_password"
												name="confirm_password" placeholder="Confirm Password"
												required="required">
										</div>
									</div>
									<div class="col-sm-9 bottom-pass-button">
										<button type="submit" class="btn btn-primary">Change
											Password</button>
									</div>
								</div>
							</form>
						</div>

					</div>
				</div>


			</div>
		</div>
	</div>


	<jsp:include page="common/footer.jsp" />
	<!-- <script>
		$(function() {
		  $( "#change-password" ).validate({
			rules: {
			  password: {
					required: true,
					minlength: 8},
			  confirm_password: {
					equalTo: "#password"
			}
		  }
			  
		  });
		});
	</script>  -->

<script>
	/* 
	*	Password policy: Minimum eight characters, maximum 20 characters, at least one letter, one number and one special character.
	*	?= is a Positive lookahead (lookaround), they are zero-length assertions. lookaround only matches characters and returns true or false.
	*	.* after the ?= signifies any digit (.) zero or more times (*), followed by the character to match.
	*	{n,m} is a Quantifier which matches the preceding element at least n times, but no more than m times.
	*/
	const passwordValidationRegex = /^(?=.*[a-z])(?=.*\d)(?=.*[@\$!%\*#\?&])[a-z\d@\$!%\*#\?&]{8,20}$/i;

	/*
	*	Method executed when the form is submitted.
	*	The user entered password is matched with the validation regex, an error message is displayed if not matched.
	*	The password and confirm password fields are matched for equality, an error message is shown if not equal.
	*/
	function validatePassword() {
		const password = document.getElementById("password");
		const confirmPassword = document.getElementById("confirm_password");

		const passwordParentNode = password.parentNode;			//Parent Node of the password element
		const confirmPasswordParentNode = confirmPassword.parentNode;		//Parent Node of the confirm_password element
		removeHelpBlockFromElement(passwordParentNode);			//remove the span help-block if it already exists
		removeHelpBlockFromElement(confirmPasswordParentNode);	//remove the span help-block if it already exists

		const passwordValue = password.value;			//Password element value
		const confirmPasswordValue = confirmPassword.value;		//Confirm Password element value

		if(!passwordValidationRegex.test(passwordValue)) {
			displayErrorMessage(passwordParentNode, "Entered Password does not match the Password policy.");
			return false;
		}
		else if(passwordValue !== confirmPasswordValue) {
			displayErrorMessage(confirmPasswordParentNode, "Confirm Password does not match the entered Password.");
			return false;
		}
		else
			return true;
	}

	/*
	*	Method which displays an error message for the passed element.
	*/
	function displayErrorMessage(element, errorMessage) {
		const helpNode = document.createElement("span");
		helpNode.classList.add("help-block");
		helpNode.textContent = errorMessage;
	
		element.classList.add("has-error");
		element.appendChild(helpNode);
	}
	
	/*
	*	Removes the span help-block child element if present in the passed parent element.
	*	Also removes it's has-error class if present.
	*/
	function removeHelpBlockFromElement(element) {
		element.querySelectorAll("span.help-block")
				.forEach(childElement => element.removeChild(childElement));
		element.classList.remove("has-error");
	}
</script>

</body>
</html>