<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html> 
<html>
<jsp:include page="common/jscss.jsp">
	<jsp:param value="Ug Consent Form" name="title" />
</jsp:include>

<style>
#consentForm{
    	margin-right:100px;
}
input[type=submit]{
  background-color: black;  
}
</style>

<body>
	<!-- Code for Page here Starts -->

	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="#"><img
					src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/logo.png" class="img-responsive" alt="" /></a>
			</div>
			<ul class="nav navbar-nav navbar-right">
				<li class="active"><a class="btn btn-danger navbar-btn"
					href="/logout"><span class="icon-logout"></span>
						Logout</a></li>
			</ul>
				
		</div>
	</nav>

	<div class="container">
										<h2 class="text-capitalize">Consent Form</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper" id="consentForm">
											<p>Due to an administrative directive from the regulatory authority, we are advised to change the nomenclature of the program you have been enrolled in.<br>
											The ${program} program that you are enrolled in currently has now been changed to a ${course}.
											 (Duration: 6 months)<br></p>
											<p><b>Do Note:</b></p>
											<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-The curriculum inclusions and evaluation pedagogy for the ${course} shall be exactly the same as Semester 1 of the ${program} program.</p>
											<p>As students who expected to be enrolled in ${program}, this change may alarm you. However, we have solutions for you which will allow you to meet your end goal without any issue.</p>
											<p><b>Way Forward.</b><br>
											You may choose any one of the following options:<br><br>
											By checking the box, the undersigned confirms that they are willing to proceed with the chosen option as the way forward to their admission for the July/Aug 2022 admission cycle of the ${program} program. </p>
											<br>
											<form method="POST" action="ugConsent"  name="theForm">
											<INPUT TYPE="radio" name="option" value="1"/>&nbsp;&nbsp;<b>Option 1</b>
											<p>
											Opt for the 6-month ${course}. At the end of the 6-month cycle, opt for lateral admission/program upgrade to Semester 2 of ${program} in January 2023 academic cycle. This option will ensure continuity and overall duration of the program remains as is.</p>
											<INPUT TYPE="radio" name="option" value="2"/>&nbsp;&nbsp;<b>Option 2</b>
							 				<p>
											Opt out of the current academic cycle completely & request for transfer of admission to the next admission intake (January 2023) for ${program} program. This means your program start date will be January 2023.</p>
											
											<INPUT TYPE="radio" name="option" value="3"/>&nbsp;&nbsp;<b>Option 3</b>
											<p>
											Opt for 6-month ${course} only until its completion and then have the freedom of decision for your next steps.</p>
											
											<INPUT TYPE="radio" name="option" value="4"/>&nbsp;&nbsp;<b>Option 4</b>
											<p>Admission Cancellation <br>You may cancel your admission and get a full refund of the fee paid. </p>
											 
											<input type="hidden" id="optionId" name="optionId" value="0">
											<input type="hidden" id="sapid" name="sapid" value="<%=request.getSession().getAttribute("userId")%>">
											<INPUT TYPE="submit" VALUE="SUBMIT" onclick="return checkValidation()"/><br><br>
											
											<p>To allow for a smooth transition in your education, we request you to complete the steps given below:<br><br>
											<b>Step 1:</b><br>Submit your response on the Student Portal here<br><br>
											<b>Step 2:</b><br>Download the consent form template sent to your registered email id from <b>connect@nmims.edu</b><br>
											Fill in your details <br>Scan the copy & email it to <b>connect@nmims.edu</b><br><br>
										
											
											
											<b>Important Note:</b><br>
												-	Completing both Step 1 & Step 2 is mandatory<br>
												-	We request you to share your chosen option with us or before 20th November, 2022. In absence of a response, your admission to the ${program} program by NGASCE, shall stand cancelled and your fee shall be refunded. 
												<br>-	This directive is applicable only for the July 2022 ${program} batch 
												<br>-	Enrollment for January 2023 ${program} has already begun </p>
											</form>
											</div>
		<!-- Code for Page here Ends -->
	</div>
</body>
<script>
    function checkValidation(){
    	var radios = document.getElementsByName('option');
    	for (var i = 0, length = radios.length; i < length; i++) {
    	  if (radios[i].checked) {
    	    document.getElementById("optionId").value = radios[i].value;
    	    break;
    	  }
    	}
    	if(document.getElementById("optionId").value == "0"){
    		alert("Please Select Any one option.");
    		return false;	
    	}
    }  
    </script>
</html>
