<!DOCTYPE html>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">




<jsp:include page="common/jscss.jsp">
	<jsp:param value="Share Feedback" name="title" />
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

<script>
    
	 function askReason(responseId,remarkId,remarkValue){
		 var select_id = document.getElementById(responseId);

		 var response = select_id.options[select_id.selectedIndex].value;
		 if(response == "N" ){
			 document.getElementById(remarkId).style.display = 'block';
		 }else{
			 document.getElementById(remarkId).style.display = 'none';
			 document.getElementById(remarkValue).value = "";
		 }
	 }
	 
	 function askReson(responseId,remarkId,remarkValue){
		 var select_id = document.getElementById(responseId);

		 var response = parseInt( select_id.options[select_id.selectedIndex].value);
		 if(response <= 3){
			 document.getElementById(remarkId).style.display = 'block';
		 }else{
			 document.getElementById(remarkId).style.display = 'none';
			 document.getElementById(remarkValue).value = "";
		 }
	 }
	 
    function validateForm(){
    	
	    var q1Response =document.getElementById("q1Response").value;
		var q2Response = document.getElementById("q2Response").value;
		var q3aResponse = parseInt(document.getElementById("q3aResponse").value);
		var q3bResponse = parseInt(document.getElementById("q3bResponse").value);
		var q3cResponse = parseInt(document.getElementById("q3cResponse").value);
		var q3dResponse = parseInt(document.getElementById("q3dResponse").value);
		var q3eResponse = parseInt(document.getElementById("q3eResponse").value);
		var q3fResponse = parseInt(document.getElementById("q3fResponse").value);
		var q4Response = document.getElementById("q4Response").value;
		var q5Response = document.getElementById("q5Response").value;
		var q6Response = document.getElementById("q6Response").value;
		var q7Response = document.getElementById("q7Response").value;
		var q8Response = document.getElementById("q8Response").value;
		
		var q1Remark = document.getElementById("q1Remark").value;
		var q2Remark = document.getElementById("q2Remark").value;
		var q3aRemark = document.getElementById("q3aRemark").value;
		var q3bRemark = document.getElementById("q3bRemark").value;
		var q3cRemark = document.getElementById("q3cRemark").value;
		var q3dRemark = document.getElementById("q3dRemark").value;
		var q3eRemark = document.getElementById("q3eRemark").value;
		var q3fRemark = document.getElementById("q3fRemark").value;
		var q4Remark = document.getElementById("q4Remark").value;
		var q5Remark = document.getElementById("q5Remark").value;
		var q6Remark = document.getElementById("q6Remark").value;
		var q7Remark = document.getElementById("q7Remark").value;
		var q8Remark = document.getElementById("q8Remark").value;
		
		//var reply = Y;
		var remarksAlertMessage = "";
		console.log("q1Response")
		console.log(q1Response)

			if(q1Response == "N"){
				console.log("q1Remark")
				console.log(q1Remark)
				if(q1Remark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of Q.1 \n";
					document.getElementById("q1Remark").focus();
				}
			}
		
		
			if(q2Response == "N"){
				if(q2Remark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of  Q.2 \n";
					document.getElementById("q2Remark").focus();
				}
			}
		
		
	 	if(!isNaN(q3aResponse)){
			reply = q3aResponse;
			if(reply <= 3){
				if(q3aRemark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of  Q.3a \n";
					document.getElementById("q3aRemark").focus();
				}
			}
		} 
	 	if(!isNaN(q3bResponse)){
			reply = q3bResponse;
			
			if(reply <= 3){
				if(q3bRemark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of  Q.3b \n";
					document.getElementById("q3bRemark").focus();
				}
			}
		} 
	 	if(!isNaN(q3cResponse)){
			reply = q3cResponse;
			if(reply <= 3){
				if(q3cRemark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of  Q.3c \n";
					document.getElementById("q3cRemark").focus();
				}
			}
		} 
	 	if(!isNaN(q3dResponse)){
			reply = q3dResponse;
			if(reply <= 3){
				if(q3dRemark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of  Q.3d \n";
					document.getElementById("q3dRemark").focus();
				}
			}
		} 
	 	if(!isNaN(q3eResponse)){
			reply = q3eResponse;
			if(reply <= 3){
				if(q3eRemark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of  Q.3e \n";
					document.getElementById("q3eRemark").focus();
				}
			}
		} 
	 	if(!isNaN(q3fResponse)){
			reply = q3fResponse;
			if(reply <= 3){
				if(q3fRemark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of  Q.3f \n";
					document.getElementById("q3fRemark").focus();
				}
			}
		} 
	 
	
			if(q4Response == "N"){
				if(q4Remark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of Q.4 \n";
					document.getElementById("q4Remark").focus();
				}
			}
		
			if(q5Response == "N"){
				if(q5Remark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of  Q.5 \n";
					document.getElementById("q5Remark").focus();
				}
			}
		
		
			if(q6Response == "N"){
				if(q6Remark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of Q.6 \n";
					document.getElementById("q6Remark").focus();
				}
			}
	
			if(q7Response == "N"){
				if(q7Remark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of Q.7 \n";
					document.getElementById("q7Remark").focus();
				}
			}
	
			if(q8Response == "N"){
				if(q8Remark.trim() == ""){
					remarksAlertMessage = remarksAlertMessage + "Please Enter remarks for low rating of Q.8 \n";
					document.getElementById("q8Remark").focus();
				}
			}
		
		
		if(remarksAlertMessage != ""){
			console.log('remarksAlertMessage null')
			alert(remarksAlertMessage);
			return false;
		}
		
	  return true;
	} 
    </script>

<body>

	<%@ include file="common/header.jsp"%>



	<div class="sz-main-content-wrapper complete-profile-warpper">

		<jsp:include page="common/breadcrum.jsp">
			<jsp:param value="Student Zone;Feedback" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="container">



				<div class="sz-content-wrapper dashBoard withBgImage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<h2 class="red text-capitalize" style="margin-top: -20px;">Feedback
						for last Academic cycle</h2>
					<div class="clearfix"></div>
					<div class="panel-content-wrapper">
						<%@ include file="common/messages.jsp"%>




						<h2 class="black">Share feedback ratings (Scale of 1 to 5, 5
							being the highest)</h2>
						<div class="clearfix"></div>

						<form:form action="saveAcadCycleFeedback" method="post"
							modelAttribute="feedback">
							<fieldset>
								<form:hidden path="sem" value="${feedback.sem}" />
								<form:hidden path="year" value="${feedback.year}" />
								<form:hidden path="month" value="${feedback.month}" />
								<c:choose>

									<c:when test="${feedback.sem == '1'}">

										<div class="row">
											<div class="col-sm-1">
												<label>1</label>
											</div>

											<div class="col-sm-5">
												<label>The Orientation program was in line with your
													Academic cycle? </label>
											</div>

											<div class="col-sm-3 form-group">
												<form:select id="q1Response" path="q1Response"
													class="form-control"
													onchange="askReason('q1Response','q1Remarks','q1Remark')"
													required="required">
													<form:option value="">Select Y/N</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>
												</form:select>
											</div>


											<div class="col-sm-3 form-group" id="q1Remarks"
												style="display: none;">
												<form:textarea path="q1Remark" value="${feedback.q1Remark}"
													class="form-control" id="q1Remark" maxlength="250"
													placeholder="Remark" />
											</div>
										</div>

										<div class="row">
											<div class="col-sm-1">
												<label>2</label>
											</div>

											<div class="col-sm-5">
												<label>Student Portal navigation for the first time
													was easy </label>
											</div>

											<div class="col-sm-3 form-group">
												<form:select id="q2Response" path="q2Response"
													class="form-control"
													onchange="askReason('q2Response','q2Remarks','q2Remark')"
													required="required">
													<form:option value="">Select Y/N</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>
												</form:select>
											</div>
											<div class="col-sm-3 form-group" id="q2Remarks"
												style="display: none;">
												<form:textarea path="q2Remark" value="${feedback.q2Remark}"
													class="form-control" id="q2Remark" maxlength="250"
													placeholder="Remark" />
											</div>
										</div>
									</c:when>

									<c:otherwise>

										<div class="row">
											<div class="col-sm-1">
												<label>1</label>
											</div>

											<div class="col-sm-5">
												<label>Re-registration process was easy? </label>
											</div>

											<div class="col-sm-3 form-group">
												<form:select id="q1Response" path="q1Response"
													class="form-control"
													onchange="askReason('q1Response','q1Remarks','q1Remark')"
													required="required">
													<form:option value="">Select Y/N</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>
												</form:select>
											</div>
											<div class="col-sm-3 form-group" id="q1Remarks"
												style="display: none;">
												<form:textarea path="q1Remark" value="${feedback.q1Remark}"
													class="form-control" id="q1Remark" maxlength="250"
													placeholder="Remark" />
											</div>
										</div>

										<div class="row">
											<div class="col-sm-1">
												<label>2</label>
											</div>

											<div class="col-sm-5">
												<label>Student Portal navigation is user friendly? </label>
											</div>

											<div class="col-sm-3 form-group">
												<form:select id="q2Response" path="q2Response"
													class="form-control"
													onchange="askReason('q2Response','q2Remarks','q2Remark')"
													required="required">
													<form:option value="">Select Y/N</form:option>
													<form:option value="Y">Yes</form:option>
													<form:option value="N">No</form:option>
												</form:select>
											</div>
											<div class="col-sm-3 form-group" id="q2Remarks"
												style="display: none;">
												<form:textarea path="q2Remark" value="${feedback.q2Remark}"
													class="form-control" id="q2Remark" maxlength="250"
													placeholder="Remark" />
											</div>
										</div>
									</c:otherwise>
								</c:choose>

								<div class="row">

									<div class="col-sm-1">
										<label>3</label>
									</div>

									<div class="col-sm-5">
										<label><b>Rate your experience on the following
												activities: (Scale of 1 to 5, 5 being the highest)</b></label>
									</div>


								</div>
								<br>
								<div class="row">

									<div class="col-sm-1">
										<label>3a</label>
									</div>

									<div class="col-sm-5">
										<label>Ease of attending online sessions </label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q3aResponse" path="q3aResponse"
											class="form-control notnull" required="required"
											onchange="askReson('q3aResponse','q3aRemarks','q3aRemark')">
											<form:option value="">Select Rating</form:option>
											<form:option value="1">1</form:option>
											<form:option value="2">2</form:option>
											<form:option value="3">3</form:option>
											<form:option value="4">4</form:option>
											<form:option value="5">5</form:option>

										</form:select>
									</div>

									<div class="col-sm-3 form-group" id="q3aRemarks"
										style="display: none;">
										<form:textarea path="q3aRemark" value="${feedback.q3aRemark}"
											class="form-control" id="q3aRemark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>

								<div class="row">

									<div class="col-sm-1">
										<label>3b</label>
									</div>

									<div class="col-sm-5">
										<label>Access recordings (Watch and download) </label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q3bResponse" path="q3bResponse"
											class="form-control notnull" required="required"
											onchange="askReson('q3bResponse','q3bRemarks','q3bRemark')">
											<form:option value="">Select Rating</form:option>
											<form:option value="1">1</form:option>
											<form:option value="2">2</form:option>
											<form:option value="3">3</form:option>
											<form:option value="4">4</form:option>
											<form:option value="5">5</form:option>

										</form:select>
									</div>

									<div class="col-sm-3 form-group" id="q3bRemarks"
										style="display: none;">
										<form:textarea path="q3bRemark" value="${feedback.q3bRemark}"
											class="form-control" id="q3bRemark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>

								<div class="row">

									<div class="col-sm-1">
										<label>3c</label>
									</div>

									<div class="col-sm-5">
										<label>Assignment preparation process </label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q3cResponse" path="q3cResponse"
											class="form-control notnull" required="required"
											onchange="askReson('q3cResponse','q3cRemarks','q3cRemark')">
											<form:option value="">Select Rating</form:option>
											<form:option value="1">1</form:option>
											<form:option value="2">2</form:option>
											<form:option value="3">3</form:option>
											<form:option value="4">4</form:option>
											<form:option value="5">5</form:option>

										</form:select>
									</div>

									<div class="col-sm-3 form-group" id="q3cRemarks"
										style="display: none;">
										<form:textarea path="q3cRemark" value="${feedback.q3cRemark}"
											class="form-control" id="q3cRemark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>

								<div class="row">

									<div class="col-sm-1">
										<label>3d</label>
									</div>

									<div class="col-sm-5">
										<label>Curriculum and content met my expectation </label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q3dResponse" path="q3dResponse"
											class="form-control notnull" required="required"
											onchange="askReson('q3dResponse','q3dRemarks','q3dRemark')">
											<form:option value="">Select Rating</form:option>
											<form:option value="1">1</form:option>
											<form:option value="2">2</form:option>
											<form:option value="3">3</form:option>
											<form:option value="4">4</form:option>
											<form:option value="5">5</form:option>

										</form:select>
									</div>

									<div class="col-sm-3 form-group" id="q3dRemarks"
										style="display: none;">
										<form:textarea path="q3dRemark" value="${feedback.q3dRemark}"
											class="form-control" id="q3dRemark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>

								<div class="row">

									<div class="col-sm-1">
										<label>3e</label>
									</div>

									<div class="col-sm-5">
										<label>Exam facilitation was seamless</label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q3eResponse" path="q3eResponse"
											class="form-control notnull" required="required"
											onchange="askReson('q3eResponse','q3eRemarks','q3eRemark')">
											<form:option value="">Select Rating</form:option>
											<form:option value="1">1</form:option>
											<form:option value="2">2</form:option>
											<form:option value="3">3</form:option>
											<form:option value="4">4</form:option>
											<form:option value="5">5</form:option>

										</form:select>
									</div>

									<div class="col-sm-3 form-group" id="q3eRemarks"
										style="display: none;">
										<form:textarea path="q3eRemark" value="${feedback.q3eRemark}"
											class="form-control" id="q3eRemark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>

								<div class="row">

									<div class="col-sm-1">
										<label>3f</label>
									</div>

									<div class="col-sm-5">
										<label>Information you need is easily available on
											Student Portal </label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q3fResponse" path="q3fResponse"
											class="form-control notnull" required="required"
											onchange="askReson('q3fResponse','q3fRemarks','q3fRemark')">
											<form:option value="">Select Rating</form:option>
											<form:option value="1">1</form:option>
											<form:option value="2">2</form:option>
											<form:option value="3">3</form:option>
											<form:option value="4">4</form:option>
											<form:option value="5">5</form:option>

										</form:select>
									</div>

									<div class="col-sm-3 form-group" id="q3fRemarks"
										style="display: none;">
										<form:textarea path="q3fRemark" value="${feedback.q3fRemark}"
											class="form-control" id="q3fRemark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>



								<div class="row">
									<div class="col-sm-1">
										<label>4</label>
									</div>

									<div class="col-sm-5">
										<label>Online sessions are sufficient and effective
											for preparation of exams </label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q4Response" path="q4Response"
											class="form-control"
											onchange="askReason('q4Response','q4Remarks','q4Remark')"
											required="required">
											<form:option value="">Select Y/N</form:option>
											<form:option value="Y">Yes</form:option>
											<form:option value="N">No</form:option>
										</form:select>
									</div>
									<div class="col-sm-3 form-group" id="q4Remarks"
										style="display: none;">
										<form:textarea path="q4Remark" value="${feedback.q4Remark}"
											class="form-control" id="q4Remark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>

								<div class="row">
									<div class="col-sm-1">
										<label>5</label>
									</div>

									<div class="col-sm-5">
										<label>Queries raised so far were answered as per my
											expectation by the University </label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q5Response" path="q5Response"
											class="form-control"
											onchange="askReason('q5Response','q5Remarks','q5Remark')"
											required="required">
											<form:option value="">Select Y/N</form:option>
											<form:option value="Y">Yes</form:option>
											<form:option value="N">No</form:option>
										</form:select>
									</div>
									<div class="col-sm-3 form-group" id="q5Remarks"
										style="display: none;">
										<form:textarea path="q5Remark" value="${feedback.q5Remark}"
											class="form-control" id="q5Remark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>


								<div class="row">
									<div class="col-sm-1">
										<label>6</label>
									</div>

									<div class="col-sm-5">
										<label>I get enough Support from the Regional office </label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q6Response" path="q6Response"
											class="form-control"
											onchange="askReason('q6Response','q6Remarks','q6Remark')"
											required="required">
											<form:option value="">Select Y/N</form:option>
											<form:option value="Y">Yes</form:option>
											<form:option value="N">No</form:option>
										</form:select>
									</div>
									<div class="col-sm-3 form-group" id="q6Remarks"
										style="display: none;">
										<form:textarea path="q6Remark" value="${feedback.q6Remark}"
											class="form-control" id="q6Remark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>

								<div class="row">
									<div class="col-sm-1">
										<label>7</label>
									</div>

									<div class="col-sm-5">
										<label>I get enough Support from the Authorised
											Partner </label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q7Response" path="q7Response"
											class="form-control"
											onchange="askReason('q7Response','q7Remarks','q7Remark')"
											required="required">
											<form:option value="">Select Y/N</form:option>
											<form:option value="Y">Yes</form:option>
											<form:option value="N">No</form:option>
										</form:select>
									</div>
									<div class="col-sm-3 form-group" id="q7Remarks"
										style="display: none;">
										<form:textarea path="q7Remark" value="${feedback.q7Remark}"
											class="form-control" id="q7Remark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>

								<div class="row">
									<div class="col-sm-1">
										<label>8</label>
									</div>

									<div class="col-sm-5">
										<label>I received all notifications on time during the
											Academic cycle (Exams, Assignment submission, Online
											sessions, etc..) </label>
									</div>

									<div class="col-sm-3 form-group">
										<form:select id="q8Response" path="q8Response"
											class="form-control"
											onchange="askReason('q8Response','q8Remarks','q8Remark')"
											required="required">
											<form:option value="">Select Y/N</form:option>
											<form:option value="Y">Yes</form:option>
											<form:option value="N">No</form:option>
										</form:select>
									</div>
									<div class="col-sm-3 form-group" id="q8Remarks"
										style="display: none;">
										<form:textarea path="q8Remark" value="${feedback.q8Remark}"
											class="form-control" id="q8Remark" maxlength="250"
											placeholder="Remark" />
									</div>
								</div>

								<div class="col-sm-6">
									<button id="submit" name="submit" class="customBtn red-btn"
										onclick="return validateForm();"
										formaction="saveAcadCycleFeedback?val=Yes">Save
										Feedback</button>
								</div>
							</fieldset>
						</form:form>
					</div>

				</div>


			</div>
		</div>
	</div>


	<jsp:include page="common/footer.jsp" />

	<script>
		
		
		
		
		
	/*     $(document).ready(function(e){
	    	$("#studentConfirmationForAttendance").val("");
	    	document.getElementById("sessionAttendId").style.display = 'none';
	    	$('.notnull').prop('required',true);
	    	
	    	$("#studentConfirmationForAttendance").change(function(){
	    		var sessionAttendanceResponse = $("#studentConfirmationForAttendance").val();
	    		if(sessionAttendanceResponse.trim() != ""){
	        		if(sessionAttendanceResponse =="Y"){
	        			document.getElementById("sessionAttendId").style.display = 'block';
	        			$('.reasonForAttend').prop('required',false);
	        			$('.otherReason').prop('required',false);
	        			document.getElementById("reasonForNotAttend").style.display ='none';
	        			document.getElementById("otherReason").style.display = 'none';
	        			$('.notnull').prop('required',true);
	        		}else{
	        			document.getElementById("sessionAttendId").style.display = 'none';
	        			$('.notnull').prop('required',false);
	        			$('.reasonForAttend').prop('required',true);
	        			$('.otherReason').prop('required',false);
	        			document.getElementById("reasonForNotAttend").style.display ='block';
	        			document.getElementById("otherReason").style.display = 'none';
	        		}
	        	}else{
	        		document.getElementById("sessionAttendId").style.display = 'none';
        			document.getElementById("reasonForNotAttend").style.display ='none';
        			$('.reasonForAttend').prop('required',false);
        			$('.otherReason').prop('required',false);
        			document.getElementById("otherReason").style.display = 'none';
	        	}
	    	});
	    	
	    	$("#reasonForNotAttending").change(function(){
	    		var reasonForNotAttending = $("#reasonForNotAttending").val();
		    	if(reasonForNotAttending.trim() != ""){
		    		if(reasonForNotAttending =="Others"){
		    			document.getElementById("otherReason").style.display = 'block';
		    			$('.otherReason').prop('required',true);
		    		}else{
		    			document.getElementById("otherReason").style.display = 'none';
		    			$('.otherReason').prop('required',false);
		    		}
		    	}else{
		    		document.getElementById("otherReason").style.display = 'none';
		    		$('.otherReason').prop('required',false);
		    	}
	    	});
	    	
	    }); */
		</script>
</body>
</html>