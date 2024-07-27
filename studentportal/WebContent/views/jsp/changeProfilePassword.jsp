<!DOCTYPE html>
<%@page import="com.nmims.helpers.PersonStudentPortalBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%
	PersonStudentPortalBean p = (PersonStudentPortalBean)session.getAttribute("user_studentportal");

	String email = "";
	String mobile = "";
	String altMobile = "";
	String postalAddress = "";
    
    if(p != null){
    	email = p.getEmail();
    	mobile = p.getContactNo();
    	postalAddress = p.getPostalAddress();
    	altMobile = p.getAltContactNo();
    }
    %>

<html lang="en">




<jsp:include page="common/jscss.jsp">
	<jsp:param value="Update Profile Password" name="title" />
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
</style>

<script type="text/javascript">
	
	function validateForm(){
		var newpassword = document.getElementById("n_Password").value;
		var confirmPassword = document.getElementById("c_Password").value;
		var oldpasswordpg = document.getElementById("o_Password").value;
		var oldpassword = "${password}";
		
	//	console.log("student  N_pwd :: " +newpassword);
	//	console.log("student  C_pwd :: " +confirmPassword);
	//	console.log("student  O_pwd :: " +oldpassword);
		var Exp = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/; //Minimum eight characters, at least one letter, one number and one special character:
		if(!newpassword.match(Exp)){
		alert("Your Password is not satisfying the password policy");
		return false;
		}
		if(confirmPassword != newpassword){
		alert("Your Password and Confirm Password do not match");
		return false;
		}
		if(oldpassword == newpassword){
			alert("Your New Password and Old Password are the same");
			return false;
			}
		if(oldpasswordpg != oldpassword){
			alert("Your Old Password is incorrect");
			return false;
			}
		return true;
	}
	
	</script>

<body>

	<%@ include file="common/header.jsp"%>

	<div class="sz-main-content-wrapper complete-profile-warpper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Update Information"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="container">

				<div class="sz-content-wrapper dashBoard withBgImage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<h2 class="red text-capitalize" style="margin-top: -20px;">Change
						Profile Password</h2>
					<div class="clearfix"></div>

					<div class="panel-content-wrapper">
						<%@ include file="common/messages.jsp"%>

						<c:if test="${passwordUpdate != true}">
							<h4>Password Policy</h4>
							<p>Please adhere to the below password policy while you set
								your new password</p>
							<ul class="policy">
								<li><i class="fa fa-arrow-circle-right"></i> Passwords
									cannot contain the user's account name or parts of the user's
									full name that exceed two consecutive characters.</li>
								<li><i class="fa fa-arrow-circle-right"></i> Passwords must
									be eight characters in length.</li>
								<li><i class="fa fa-arrow-circle-right"></i> Passwords must
									contain characters from the following categories:</li>
								<li><i class="fa fa-arrow-circle-right"></i> English
									alphabet characters (A-Z/a-z)</li>
								<li><i class="fa fa-arrow-circle-right"></i> Numbers (0-9)</li>
								<li><i class="fa fa-arrow-circle-right"></i>
									Non-alphanumeric characters (for example, !$#%)</li>
							</ul>
							<!-- <p>Please verify/update information below. Father and Mother name will be displayed on Marksheet and Certificate</p> -->
							<form id="updateFirstTimeForm"
								action="updateStudentProfilePassword" method="post"
								onSubmit="return validateForm();" modelAttribute="student">
								<fieldset>
									<div class="row">
										<div class="col-md-6">


											<div class="form-group">
												<label for="password">Enter Old Password(*)</label> <input
													type="password" class="form-control" id="o_Password"
													name="o_Password" placeholder="Enter Old Password"
													required="required">
											</div>
											<div class="form-group">
												<label for="n_Password">Enter New Password(*)</label> <input
													type="password" class="form-control" id="n_Password"
													name="n_Password" placeholder="Enter New Password"
													required="required">
											</div>
											<div class="form-group">
												<label for="c_Password">Confirm New Password(*)</label> <input
													type="password" class="form-control" id="c_Password"
													name="c_password" placeholder="Confirm New Password"
													required="required">
											</div>


											<div class="form-group">
												<button type="submit" class="btn btn-primary">Update
													Information</button>
											</div>


										</div>

									</div>
								</fieldset>
							</form>
						</c:if>
						<c:if test="${passwordUpdate}">
							<form>
								<div class="form-group">
									<button type="submit" class="btn btn-primary"
										formaction="/logout">Proceed to Login</button>
								</div>
							</form>

						</c:if>
					</div>

				</div>


			</div>
		</div>
	</div>


	<jsp:include page="common/footer.jsp" />


</body>
</html>