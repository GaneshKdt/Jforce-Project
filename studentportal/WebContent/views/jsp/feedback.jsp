<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Share Feedback" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="limitedAccessHeader.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

		<div class="row">
			<legend>Feedback for ${feedback.subject } - ${feedback.sessionName }</legend>
		</div>
			
			<div class="row">

				<div class="col-sm-18 ">
					<div class="panel-body">
						
						<div class="titleContainer titleContainerResultIns">
							<p>Subject, Session</p>
							<h3>${feedback.subject} - ${feedback.sessionName}</h3>
						</div>
						<div class="titleContainer titleContainerResultIns">
							<p>Faculty Name</p>
							<h3>${feedback.firstName} ${feedback.lastName}</h3>
						</div>
					
						<div class="titleContainer titleContainerResultIns">
							<p>Date & Time</p>
							<h3>${feedback.day}, ${feedback.date}, ${feedback.startTime}</h3>
						</div>
						
				</div>
				
				</div>
			</div>
			
			<div class="panel-body">
			<h2>Share feedback ratings (1-Lowest, 5-Highest)</h2>
			<form:form action="searchSingleStudentMarks" method="post" modelAttribute="feedback">
					<fieldset>
						<form:hidden path="sessionId"/>
						<form:hidden path="subject"/>
						<form:hidden path="sessionName"/>
						
						<div class="row">
							<div class="col-sm-1">
							<label>Q. 1</label>
							</div>
							
							<div class="col-sm-10">
							<label>The Faculty delivered the session in effective and understandable manner.</label>
							</div>
							
							<div class="col-sm-4 form-group">
							<form:select id="q1Response" path="q1Response" class="form-control"	required="required">
									<form:option value="">Select Rating</form:option>
									<form:option value="1">1</form:option>
									<form:option value="2">2</form:option>
									<form:option value="3">3</form:option>
									<form:option value="4">4</form:option>
									<form:option value="5">5</form:option>
								</form:select>
							</div>
						</div>
						
						<div class="row">
							<div class="col-sm-1">
							<label>Q. 2</label>
							</div>
							
							<div class="col-sm-10">
							<label>The Faculty was responsive to students' difficulties and dealt with the questions appropriately.</label>
							</div>
							
							<div class="col-sm-4 form-group">
							<form:select id="q2Response" path="q2Response" class="form-control"	required="required">
									<form:option value="">Select Rating</form:option>
									<form:option value="1">1</form:option>
									<form:option value="2">2</form:option>
									<form:option value="3">3</form:option>
									<form:option value="4">4</form:option>
									<form:option value="5">5</form:option>
								</form:select>
							</div>
						</div>
						
						
						<div class="row">
							<div class="col-sm-1">
							<label>Q. 3</label>
							</div>
							
							<div class="col-sm-10">
							<label>Overall level of satisfaction with the session.</label>
							</div>
							
							<div class="col-sm-4 form-group">
							<form:select id="q3Response" path="q3Response" class="form-control"	required="required">
									<form:option value="">Select Rating</form:option>
									<form:option value="1">1</form:option>
									<form:option value="2">2</form:option>
									<form:option value="3">3</form:option>
									<form:option value="4">4</form:option>
									<form:option value="5">5</form:option>
								</form:select>
							</div>
						</div>
						
						<div class="row">
							<div class="col-sm-1">
							<label>Q. 4</label>
							</div>
							
							<div class="col-sm-10">
							<label>Any other constructive comments that you wish to make:</label>
							</div>
							
							<div class="col-sm-5 form-group">
							<form:textarea path="feedbackRemark" cols="35" rows="5"/>
							</div>
						</div>
						
						
						
						
						<div class="col-sm-6">
							<button id="submit" name="submit" class="red-btn"	formaction="saveFeedback">Save Feedback</button>
						</div>
					</fieldset>
				</form:form>
			</div>
			
			<form:form>
				
			
			</form:form>

		</div>
	</section>




	<jsp:include page="footer.jsp" />

</body>
</html>
 --%>

<!DOCTYPE html>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

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

.slider {
  width: 85%;
  height: 2px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 2px;
  position: relative;
}
.slider .ui-slider-range {
  border-radius: 2px;
  background: #d2232a;
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
}
.slider .ui-slider-handle {
  cursor: move;
  cursor: grab;
  cursor: -webkit-grab;
  width: 32px;
  height: 32px;
  position: absolute;
  outline: none;
  top: 0;
  z-index: 1;
  border-radius: 50%;
  background: #d2232a;
  box-shadow: 0 2px 7px rgba(0, 0, 0, 0.2);
  margin: -1px 0 0 0;
  -webkit-transform: translate(-50%, -50%);
          transform: translate(-50%, -50%);
  -webkit-transition: box-shadow .3s ease;
  transition: box-shadow .3s ease;
}
.slider .ui-slider-handle .smiley {
  position: absolute;
  left: 50%;
  bottom: 100%;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 2px solid #000;
  opacity: 0;
  -webkit-transform: translate(-50%, -12px);
          transform: translate(-50%, -12px);
  -webkit-transition: all .3s ease 0s;
  transition: all .3s ease 0s;
}
.slider .ui-slider-handle .smiley:before, .slider .ui-slider-handle .smiley:after {
  content: '';
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background: #000;
  position: absolute;
  top: 8px;
}
.slider .ui-slider-handle .smiley:before {
  left: 7px;
}
.slider .ui-slider-handle .smiley:after {
  right: 7px;
}
.slider .ui-slider-handle .smiley svg {
  width: 16px;
  height: 7px;
  position: absolute;
  left: 50%;
  bottom: 5px;
  margin: 0 0 0 -8px;
}
.slider .ui-slider-handle .smiley svg path {
  stroke-width: 3.4;
  stroke: #000;
  fill: none;
  stroke-linecap: round;
}
.slider .ui-slider-handle.ui-state-active {
  cursor: grabbing;
  cursor: -webkit-grabbing;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.32);
}
.slider .ui-slider-handle.ui-state-active + .text {
  -webkit-transform: translate(0, -80px);
          transform: translate(0, -80px);
  -webkit-transition: -webkit-transform .3s ease 0s;
  transition: -webkit-transform .3s ease 0s;
  transition: transform .3s ease 0s;
  transition: transform .3s ease 0s, -webkit-transform .3s ease 0s;
}
.slider .ui-slider-handle.ui-state-active .smiley {
  opacity: 1;
  -webkit-transform: translate(-50%, -12px);
          transform: translate(-50%, -12px);
  -webkit-transition: all .3s ease .1s;
  transition: all .3s ease .1s;
}
.slider .text {
  position: absolute;
  bottom: 100%;
  left: 0;
  right: 0;
  display: -webkit-box;
  display: flex;
  -webkit-box-pack: justify;
          justify-content: space-between;
  -webkit-transform: translate(0, -44px);
          transform: translate(0, -44px);
  -webkit-transition: -webkit-transform .3s ease .2s;
  transition: -webkit-transform .3s ease .2s;
  transition: transform .3s ease .2s;
  transition: transform .3s ease .2s, -webkit-transform .3s ease .2s;
  font-size: 16px;
}
.slider .text strong {
  color: #000;
  font-weight: bold;
}
</style>

<script>
    
	 function askReson(responseId,remarkId,remarkValue){
		 var select_id = document.getElementById(responseId);

		 var response = parseInt( select_id.options[select_id.selectedIndex].value);
		 if(response <5){
			 document.getElementById(remarkId).style.display = 'block';
		 }else{
			 document.getElementById(remarkId).style.display = 'none';
			 document.getElementById(remarkValue).value = "";
		 }
	 }
	 
    function validateForm(){    	
    	/* console.log("worked",workedArr);    	
    	console.log("notWorked",notWorkedArr);
    	
	    var q1Response = parseInt(document.getElementById("q1Response").value);
		var q2Response = parseInt(document.getElementById("q2Response").value);
		var q3Response = parseInt(document.getElementById("q3Response").value);
		var q4Response = parseInt(document.getElementById("q4Response").value);
		var q5Response = parseInt(document.getElementById("q5Response").value);
		var q6Response = parseInt(document.getElementById("q6Response").value);
		var q7Response = parseInt(document.getElementById("q7Response").value);
		var q8Response = parseInt(document.getElementById("q8Response").value);
		
		var q1Remark = document.getElementById("q1Remark").value;
		var q2Remark = document.getElementById("q2Remark").value;
		var q3Remark = document.getElementById("q3Remark").value;
		var q4Remark = document.getElementById("q4Remark").value;
		var q5Remark = document.getElementById("q5Remark").value;
		var q6Remark = document.getElementById("q6Remark").value;
		var q7Remark = document.getElementById("q7Remark").value;
		var q8Remark = document.getElementById("q8Remark").value; */
		
		var total = 0, cWorked = 0, cNotWorked = 0, cRateExpNotWrked = 0;
		var workedComment="", notWorkedComment="";
		var remarksAlertMessage = "";
		if($("#studentConfirmationForAttendance").val()=="Y"){		
			if ($(".rateExp .text strong").text() == "-") {
				remarksAlertMessage += "Please rate your experience for session \n";
			} else{

			$(".worked").each(function() {
				if ($(this).data("if") == "selected") {
					cWorked++;
					if($(this).val()=="Others"){
						workedComment=$(".othersWorked input").val();		
					}
				}
				
			});
			$(".notWorked").each(function() {
				if ($(this).data("if") == "selected") {
					cNotWorked++;
					if($(this).val()=="Others"){
						notWorkedComment=$(".othersNotWorked input").val();		
					}
				}
			});					

			/* if (cWorked == 0) {
				remarksAlertMessage += "Please select at least one option which worked for you \n";
			} */

			/* if (cNotWorked == 0) {
				remarksAlertMessage += "Please select at least one option which didn't work for you \n";
			} */
			
			$(".rateExpNotWorked").each(function(){
				if($(this).data("if")=="selected"){
					cRateExpNotWrked++;
				}
			});
			
			if(cRateExpNotWrked == 0){
				remarksAlertMessage += "Please choose yes or no option for which factors didn't work for you \n";
			}
			
			if($("#rateExpYes").data("if")=="selected"){
				$(".rateExpNotWorkedDiv .slider .text strong").each(function(){
					if ($(this).text() == "-") {
						remarksAlertMessage += "Please rate your experience for : "+$(this).data("val")+" \n";
					}
				});
			}
			/* 
			$("#worked").val(workedArr.join(","));			
			$("#notWorked").val(notWorkedArr.join(","));	 */				

		/* if (!isNaN(q1Response)) {
			total = parseInt(q1Response);
			if (total < 5) {
				if (q1Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Session Q.1 \n";
					document.getElementById("q1Remark").focus();
				}
			}
		}
		if (!isNaN(q2Response)) {
			total = parseInt(q2Response);
			if (total < 5) {
				if (q2Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Session Q.2 \n";
					document.getElementById("q2Remark").focus();
				}
			}
		}

		if (!isNaN(q3Response)) {
			total = parseInt(q3Response);
			if (total < 5) {
				if (q3Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of TECHNICAL Q.1 \n";
					document.getElementById("q3Remark").focus();
				}
			}
		}
		if (!isNaN(q4Response)) {
			total = parseInt(q4Response);
			if (total < 5) {
				if (q4Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of TECHNICAL Q.2 \n";
					document.getElementById("q4Remark").focus();
				}
			}
		}
		if (!isNaN(q5Response)) {
			total = parseInt(q5Response);
			if (total < 5) {
				if (q5Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Faculty Q.1 \n";
					document.getElementById("q5Remark").focus();
				}
			}
		}
		if (!isNaN(q6Response)) {
			total = parseInt(q6Response);
			if (total < 5) {
				if (q6Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Faculty Q.2 \n";
					document.getElementById("q6Remark").focus();
				}
			}
		}
		if (!isNaN(q7Response)) {
			total = parseInt(q7Response);
			if (total < 5) {
				if (q7Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Faculty Q.3 \n";
					document.getElementById("q7Remark").focus();
				}
			}
		}
		if (!isNaN(q8Response)) {
			total = parseInt(q8Response);
			if (total < 5) {
				if (q8Remark.trim() == "") {
					remarksAlertMessage = remarksAlertMessage
							+ "Please Enter remarks for low rating of Faculty Q.4 \n";
					document.getElementById("q8Remark").focus();
				}
			}
		} */
			}
		}
		if (remarksAlertMessage != "") {
			alert(remarksAlertMessage);
			return false;
		}
		
		
		if(workedComment!="" && notWorkedComment!=""){
			$(".feedbackRemark").val(workedComment+"~|~"+notWorkedComment);
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
				<%-- <%@ include file="common/left-sidebar.jsp" %> --%>


				<div class="sz-content-wrapper dashBoard withBgImage">
					<%@ include file="common/studentInfoBar.jsp"%>


					<h2 class="red text-capitalize" style="margin-top: -20px;">Feedback
						for ${feedback.subject } - ${feedback.sessionName }</h2>
					<div class="clearfix"></div>
					<div class="panel-content-wrapper">
						<%@ include file="common/messages.jsp"%>



						<div>
							<p>Subject, Session: ${feedback.subject} -
								${feedback.sessionName}</p>
						</div>
						<div>
							<p>Faculty Name: ${feedback.firstName} ${feedback.lastName}</p>
						</div>

						<div>
							<p>Date & Time: ${feedback.day}, ${feedback.date},
								${feedback.startTime}</p>
						</div>

						<div>
							<p>Your feedback will enable us to improve/enhance it for
								better learning in future. Feedback module has been divided into
								three parts: Session conducted, Technical aspect and Faculty
								interaction.</p>
						</div>

						


						<div class="clearfix"></div>
						<h2 class="black">Share feedback ratings (1-Lowest,
							7-Highest)</h2>
						<div class="clearfix"></div>

						<form:form action="saveFeedback" method="post"
							modelAttribute="feedback">
							<fieldset>
								<form:hidden path="sessionId" />
								<form:hidden path="subject" />
								<form:hidden path="sessionName" />								
								<%-- <form:hidden path="attended" class="attended" /> --%>
								<form:hidden path="q1Response" class="q1Response" value=""/>
								<form:hidden path="q2Response" class="q2Response" value="" />
								<form:hidden path="q3Response" class="q3Response" value="" />
								<form:hidden path="q4Response" class="q4Response" value="" />
								<form:hidden path="q5Response" class="q5Response" value="" />
								<form:hidden path="q6Response" class="q6Response" value="" />
								<form:hidden path="q7Response" class="q7Response" value="" />
								<form:hidden path="q8Response" class="q8Response" value="" />
								<form:hidden path="feedbackRemarks" class="feedbackRemark" value="~|~" />								
																
								<h2 class="black">Session Attend</h2>
								<div class="clearfix"></div>
								<div class="row">
									<div class="col-sm-1">
										<label>1</label>
									</div>
		
									<div class="col-sm-5">
										<label>Have you attended the session</label>
									</div>
		
									<div class="col-sm-3 form-group">
										<form:select id="studentConfirmationForAttendance"
											path="studentConfirmationForAttendance" class="form-control"
											required="required">
											<form:option value="">Select Y/N</form:option>
											<form:option value="Y">Yes</form:option>
											<form:option value="N">No</form:option>
										</form:select>
									</div>
								</div>
		
								<div id="reasonForNotAttend" style="display: none;">
									<div class="row">
										<div class="col-sm-1">
											<label>2</label>
										</div>
		
										<div class="col-sm-5 form-group">
											<form:select id="reasonForNotAttending"
												path="reasonForNotAttending"
												class="form-control reasonForAttend">
												<form:option value="">Select Reason For Not Attending </form:option>
												<form:option value="Unable to login to Student Portal">Unable to login to Student Portal</form:option>
												<form:option
													value="Unable to join the session as the session reached full capacity">Unable to join the session as the session reached full capacity</form:option>
												<form:option
													value="Unable to install Zoom Plug-Ins and connect to session">Unable to install WebEx Plug-Ins and connect to session</form:option>
												<form:option
													value="Unable to join the session as it requested for a meeting number">Unable to join the session as it requested for a meeting number</form:option>
												<form:option
													value="Unable to join the session as the meeting number was invalid">Unable to join the session as the meeting number was invalid</form:option>
												<form:option
													value="Unable to join the session as the meeting was not in progress">Unable to join the session as the meeting was not in progress</form:option>
												<form:option value="Others">Others</form:option>
											</form:select>
										</div>
		
										<div id="otherReason" style="display: none;"
											class="col-sm-3 form-group">
											<form:textarea path="otherReasonForNotAttending"
												value="${feedback.otherReasonForNotAttending}"
												class="form-control otherReason"
												id="otherReasonForNotAttending" maxlength="250"
												placeholder="Mention other Reason For Not Attending Session" />
										</div>
									</div>
		
								</div>
								
								<div id="sessionAttendId">
									<h2 class="black">Please rate your experience for
										${feedback.subject } - ${feedback.sessionName }</h2>
									<div class="clearfix"></div>									
									<div class="row">
										<div class="col-sm-9"
											style="padding: 1rem; margin: 5rem 0rem 0rem 3rem;">

											<div class="slider rateExp">
												<div class="ui-slider-handle">
													<div class="smiley">
														<svg viewBox="0 0 34 10" version="1.1">
											                <path d=""></path>
											            </svg>
													</div>
												</div>
												<div class="text">
													<span><b>Rate</b></span> <strong data-val="Rate Exp">-</strong>
												</div>
											</div>
										</div>
									</div>
									<div class="rateDiv" style="display: none;">
										<h2 class="black">Please help us understand what worked
											for you</h2>
										<div class="clearfix"></div>										
										<div class="row workedDiv">
											<div class="col-sm-9" style="display:block;text-align:center;">	
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="worked"
														id="q1Response" value="Learning Effectiveness"
														data-if="notSelected">Learning Effectiveness</button>
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="worked"
														id="q2Response" value="Content Shared"
														data-if="notSelected">Content Shared</button>											
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="worked"
														id="q3Response" value="Audio Quality"
														data-if="notSelected">Audio Quality</button>											
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="worked"
														id="q4Response" value="Video Quality"
														data-if="notSelected">Video Quality</button>																																				
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="worked"
														id="q5Response" value="Faculty Readiness"
														data-if="notSelected">Faculty Readiness</button>												
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="worked"
														id="q6Response" value="Concept Presentation"
														data-if="notSelected">Concept Presentation</button>												
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="worked"
														id="q7Response" value="Query Responses"
														data-if="notSelected">Query Responses</button>											
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="worked"
														id="q8Response" value="Relevant Examples/Case Studies"
														data-if="notSelected">Relevant Examples/Case Studies</button>																																			
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="worked"
														id="othersWorked" value="Others" data-if="notSelected">Others</button>
												</div>

											</div>
											<div class="col-sm-8 othersWorked" style="display: none;">
												<div class="col-sm-12">
													<input type="text" class="form-control" id="others"
														placeholder="Leave a comment" />
												</div>
											</div>										
											<div class="clearfix"></div>	
										<h2 class="black">Please help us understand what didn't
											work for you</h2>
										<div class="clearfix"></div>										
										<div class="row notWorkedDiv">
											<div class="col-sm-9" style="display:block;text-align:center;">
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="notWorked"
														id="q1Response" value="Learning Effectiveness"
														data-if="notSelected">Learning Effectiveness</button>
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="notWorked"
														id="q2Response" value="Content Shared"
														data-if="notSelected">Content Shared</button>											
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="notWorked"
														id="q3Response" value="Audio Quality"
														data-if="notSelected">Audio Quality</button>											
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="notWorked"
														id="q4Response" value="Video Quality"
														data-if="notSelected">Video Quality</button>																																				
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="notWorked"
														id="q5Response" value="Faculty Readiness"
														data-if="notSelected">Faculty Readiness</button>												
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="notWorked"
														id="q6Response" value="Concept Presentation"
														data-if="notSelected">Concept Presentation</button>												
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="notWorked"
														id="q7Response" value="Query Responses"
														data-if="notSelected">Query Responses</button>											
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="notWorked"
														id="q8Response" value="Relevant Examples/Case Studies"
														data-if="notSelected">Relevant Examples/Case Studies</button>																									
													<!-- <button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="notWorked"
														id="notAttendedNotWorked" value="Not Attended"
														data-if="notSelected">Not Attended</button> -->
													<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;" class="notWorked"
														id="othersNotWorked" value="Others" data-if="notSelected">Others</button>												

											</div>
											<!-- <div class="col-sm-8 reasonForNotAttending" style="display: none;">
												<div class="col-sm-12">
													<input type="text" class="form-control" id="reasonForNotAttending"
														name="reasonForNotAttending" placeholder="Reason for not Attending" />
												</div>
											</div> -->
											<br/><br/>
											<div class="col-sm-8 othersNotWorked" style="display: none;">
												<div class="col-sm-12">
													<input type="text" class="form-control" id="others"
														placeholder="Leave a comment" />
												</div>
											</div>
										</div>
										<div class="clearfix"></div>	
										<div class="row">											
											<div class="col-sm-8">
												<div class="col-sm-9" style="padding-left: initial;">
													<h2 class="black">Would you like to rate your
														experience on the factors that didn't work for you?</h2>
													<input type="hidden" id="rateExpNotWorked" value="" />
												</div>
												<div class="col-sm-3">
													<div class="col-sm-6">
														<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;" class="rateExpNotWorked"
															id="rateExpYes" value="Y" data-if="notSelected">Yes</button>
													</div>
													<div class="col-sm-6">
														<button type="button" style="background-color:white;color:black;border:1px solid black;font-weight:bold;" class="rateExpNotWorked"
															id="rateExpNo" value="N" data-if="notSelected">No</button>
													</div>
												</div>
											</div>
										</div>

										<div class="rateExpNotWorkedDiv">											
										</div>									
									</div>
								</div>
								<div class="col-sm-6">
									<button id="submit" name="submit" class="customBtn red-btn"
										 formaction="saveFeedback">Save
										Feedback</button>
								</div>
							</fieldset>
						</form:form>

						<div class="row">
							<div class="col-sm-6">
								<a href="/studentportal/skipFeedback" class="btn btn-primary"
									title="Skip Feedback">Skip Feedback</a>
							</div>
						</div>

					</div>

				</div>


			</div>
		</div>
	</div>


	<jsp:include page="common/footer.jsp" />
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/assets/js/feedback.js"></script>
	
	<!-- <script>		
		var step = 7;
		let questions={'q1Response':"The subject matter covered in this session helped you to understand and learn effectively?",
	        'q2Response':"The course material used was helpful towards today's session?",
	        'q3Response':"Audio quality was upto the mark?",
	        'q4Response':"Video quality was upto the mark?",
	        'q5Response':"The Faculty was organized and well prepared for the class?",
	        'q6Response':"The Faculty was effective in communicating the concept in the class (in terms of clarity and presenting the concepts in understandable manner)?",
	        'q7Response':"The Faculty was responsive to student's learning difficulties and dealt with questions appropriately.",
	        'q8Response':"The learning process adopted (e.g. case studies, relevant examples and presentation work etc.) were helpful towards learning from the session?"
	        }
	    $(document).ready(function(e){	    		   	    		    		    		    		    		        				        				        			        			        		    		   
	    	$("#studentConfirmationForAttendance").val("");
	    	document.getElementById("sessionAttendId").style.display = 'none';
	    	//$('.notnull').prop('required',true);
	    	
	    	$("#studentConfirmationForAttendance").change(function(){
	    		var sessionAttendanceResponse = $("#studentConfirmationForAttendance").val();
	    		if(sessionAttendanceResponse.trim() != ""){
	        		if(sessionAttendanceResponse =="Y"){
	        			document.getElementById("sessionAttendId").style.display = 'block';
	        			$('.reasonForAttend').prop('required',false);
	        			$('.otherReason').prop('required',false);
	        			document.getElementById("reasonForNotAttend").style.display ='none';
	        			document.getElementById("otherReason").style.display = 'none';
	        			//$('.notnull').prop('required',true);
	        			
	        			$(".reasonForAttend").val("");
	        			$("#otherReasonForNotAttending").val("");
	        			$("#otherReasonForNotAttending").html("");
	        		}else{
	        			document.getElementById("sessionAttendId").style.display = 'none';
	        			//$('.notnull').prop('required',false);
	        			$('.reasonForAttend').prop('required',true);
	        			$('.otherReason').prop('required',false);
	        			document.getElementById("reasonForNotAttend").style.display ='block';
	        			document.getElementById("otherReason").style.display = 'none';
	        			
	        			$(".slider").each(function () {
	        			      $(this).slider({
	        			        value: 1,
	        			      });
	        			      $(this).find(".text strong").html("-");
	        			 });
	        			
	        			 $(".rateDiv").attr("style","display:none;");

	        			    $(".worked").each(function () {
	        			      if ($(this).data("if") == "selected") {
	        			        $(this).click();
	        			        if ($(".othersWorked input").val() != "") {
	        			          $(".othersWorked input").val("");
	        			        }
	        			      }
	        			    });

	        			    $(".notWorked").each(function () {
	        			      if ($(this).data("if") == "selected") {
	        			        $(this).click();
	        			        if ($(".othersNotWorked input").val() != "") {
	        			          $(".othersNotWorked input").val("");
	        			        }
	        			      }
	        			    });

	        			    if ($("#rateExpYes").data("if") == "selected") {
	        			        $("#rateExpYes").data("if", "notSelected");
	        			        $("#rateExpYes").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
	        			        $(".rateExpNotWorkedDiv").html("");									        			       
	        			    }

	        			    if ($("#rateExpNo").data("if") == "selected") {
	        			      $("#rateExpNo").data("if", "notSelected");
	        			      $("#rateExpNo").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
	        			    }
	        			    
	        			    $(".q1Response").val("");									
							$(".q2Response").val("");									
							$(".q3Response").val("");									
							$(".q4Response").val("");									
							$(".q5Response").val("");									
							$(".q6Response").val("");								
							$(".q7Response").val("");									
							$(".q8Response").val("");
							$("#rateExpNotWorked").val("");
							$(".feedbackRemark").val("");
	        		}
	        	}else{
	        		document.getElementById("sessionAttendId").style.display = 'none';
        			document.getElementById("reasonForNotAttend").style.display ='none';
        			$('.reasonForAttend').prop('required',false);
        			$('.otherReason').prop('required',false);
        			document.getElementById("otherReason").style.display = 'none';
        			
        			$(".slider").each(function () {
      			      $(this).slider({
      			        value: 1,
      			      });
      			      $(this).find(".text strong").html("-");
      			 });
      			
      			 $(".rateDiv").attr("style","display:none;");

      			    $(".worked").each(function () {
      			      if ($(this).data("if") == "selected") {
      			        $(this).click();
      			        if ($(".othersWorked input").val() != "") {
      			          $(".othersWorked input").val("");
      			        }
      			      }
      			    });

      			    $(".notWorked").each(function () {
      			      if ($(this).data("if") == "selected") {
      			        $(this).click();
      			        if ($(".othersNotWorked input").val() != "") {
      			          $(".othersNotWorked input").val("");
      			        }
      			      }
      			    });

      			    if ($("#rateExpYes").data("if") == "selected") {
      			        $("#rateExpYes").data("if", "notSelected");
      			        $("#rateExpYes").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
      			        $(".rateExpNotWorkedDiv").html("");									        			       
      			    }

      			    if ($("#rateExpNo").data("if") == "selected") {
      			      $("#rateExpNo").data("if", "notSelected");
      			      $("#rateExpNo").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
      			    }
      			    
      			    $(".q1Response").val("");									
					$(".q2Response").val("");									
					$(".q3Response").val("");									
					$(".q4Response").val("");									
					$(".q5Response").val("");									
					$(".q6Response").val("");								
					$(".q7Response").val("");									
					$(".q8Response").val("");
					$("#rateExpNotWorked").val("");
					$(".feedbackRemark").val("");
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
	    	
	    });
	    					//code for slider								

							$(".slider").each(
									function() {
										var self = $(this);
										var slider = self.slider({
											create : function() {
												self.find(".text strong").text("-");
												setPathData(self
														.find(".smiley").find(
																"svg path"),
														self.slider("value"));
											},
											slide : function(event, ui) {
												self.find(".text strong").text(
														ui.value);
												setPathData(self
														.find(".smiley").find(
																"svg path"),
														ui.value);
												$(".rateDiv").attr("style","");												
												$(".q1Response").val(ui.value);
												$(".q2Response").val(ui.value);
												$(".q3Response").val(ui.value);
												$(".q4Response").val(ui.value);
												$(".q5Response").val(ui.value);
												$(".q6Response").val(ui.value);
												$(".q7Response").val(ui.value);
												$(".q8Response").val(ui.value);												
											},
											range : "min",
											min : 1,
											max : step,
											value : 1,
											step : 1
										});
									});

							function setPathData(path, value) {
								var firstStep = (6 / step) * value;
								var secondStep = (2 / step) * value;
								path.attr("d", "M1," + (7 - firstStep)
										+ " C6.33333333," + (2 + secondStep)
										+ " 11.6666667," + (1 + firstStep)
										+ " 17," + (1 + firstStep)
										+ " C22.3333333," + (1 + firstStep)
										+ " 27.6666667," + (2 + secondStep)
										+ " 33," + (7 - firstStep));
							}
							
							$(".worked").click(function(){
								if($(this).data("if")=="notSelected"){
									$(this).data("if","selected");
									$(this).attr("style","background-color:#d2232a;color:white;border:1px solid #d2232a;font-weight:bold;padding:0.5rem;");																	
									
									if($(this).attr("id")=="othersWorked"){
										$(".othersWorked").attr("style","");
										$(".othersWorked input").attr("required","required");
									}else{
										$(".notWorkedDiv #"+$(this).attr("id")).attr("disabled","disabled");										
										$(".notWorkedDiv #"+$(this).attr("id")).attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;");
										$(".notWorkedDiv #"+$(this).attr("id")).data("if","notSelected");
										$(".notWorkedDiv #"+$(this).attr("id")).css("cursor","not-allowed");
									}
								} else{
									$(this).data("if","notSelected");
									$(this).attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;");																	
									
									if($(this).attr("id")=="othersWorked"){
										$(".othersWorked").attr("style","display:none;");
										$(".othersWorked input").removeAttr("required");
										$(".othersWorked input").val("");
									}else{
										$(".notWorkedDiv #"+$(this).attr("id")).removeAttr("disabled");
										$(".notWorkedDiv #"+$(this).attr("id")).css("cursor","pointer");										
									}
								}
								
								
							});
							
							$(".notWorked").click(function(){
								if($(this).data("if")=="notSelected"){
									$(this).data("if","selected");
									$(this).attr("style","background-color:#d2232a;color:white;border:1px solid #d2232a;font-weight:bold;padding:0.5rem;");
																		
									
									if($(this).attr("id")=="othersNotWorked"){
										$(".othersNotWorked").attr("style","");
										$(".othersNotWorked input").attr("required","required");
									}
									
									/* if($(this).attr("id")=="notAttendedNotWorked"){
										$(".reasonForNotAttending").attr("style","");
										$(".attended").val("N");
										$(".reasonForNotAttending input").attr("required","required");
									} */
									
									if($("#rateExpYes").data("if")=="selected"){
										$(".rateExpNotWorkedDiv").html("");																			
										
										$("#rateExpYes").data("if","notSelected");
										$("#rateExpYes").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
										
										$("#rateExpNo").data("if","notSelected");
										$("#rateExpNo").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
									}
								} else{
									$(this).data("if","notSelected");
									$(this).attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;padding:0.5rem;");																				
									
									if($(this).attr("id")=="othersNotWorked"){
										$(".othersNotWorked").attr("style","display:none;");
										$(".othersNotWorked input").removeAttr("required");
										$(".othersNotWorked input").val("");
									}
									
									/* if($(this).attr("id")=="notAttendedNotWorked"){
										$(".reasonForNotAttending").attr("style","display:none;");
										$(".reasonForNotAttending input").removeAttr("required");
										$(".attended").val("Y");
										$(".reasonForNotAttending input").val("");
									} */
									
									if($("#rateExpYes").data("if")=="selected"){
										$(".rateExpNotWorkedDiv").html("");																			
										
										$("#rateExpYes").data("if","notSelected");
										$("#rateExpYes").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
										
										$("#rateExpNo").data("if","notSelected");
										$("#rateExpNo").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
									}
								}
																
							});
							
							$("#rateExpYes").click(function(){
								if($(this).data("if")=="notSelected"){
									var c=0;
									$(".notWorked").each(function(){
										if($(this).data("if")=="selected" && $(this).val()!="Others"){
											c++;
										}
									});
									if(c==0){
										alert("Please choose at least one option which didn't work for you!");
									} else{
										$(this).attr("style","background-color:#d2232a;color:white;border:1px solid #d2232a;font-weight:bold;");
										$(this).data("if","selected");
										
										$("#rateExpNo").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
										$("#rateExpNo").data("if","notSelected");
										
										$("#rateExpNotWorked").val($(this).val());
										
										$(".notWorked").each(function(){
											if($(this).data("if")=="selected"){
												var valSlice=$(this).attr("id").slice(0,2)+"Remark";											
												var orgVal=$(this).val();
												var id=$(this).attr("id");
												if(orgVal!="Others"/*  && orgVal!="Not Attended" */){
													$(".rateExpNotWorkedDiv").html($(".rateExpNotWorkedDiv").html()+'<h2 class="black">'+questions[id]+'</h2><div class="clearfix"></div><div class="row"><div class="col-sm-9" style="padding: 1rem; margin: 6rem 0rem 2rem 3rem;"><div class="slider '+id+'"><div class="ui-slider-handle"><div class="smiley"><svg viewBox="0 0 34 10" version="1.1"><path d=""></path></svg></div></div><div class="text"><span><b>Rate</b></span> <strong data-val="'+orgVal+'">-</strong></div></div></div></div><div class="row"><div class="col-sm-8 notAttendedNotWorked" id="'+valSlice+'" style="display: none;"><div class="col-sm-12"><input type="text" class="form-control" name="'+valSlice+'" placeholder="Enter remark" /></div></div>');
												}
											}
										});
										
										$(".rateExpNotWorkedDiv .slider").each(function(){
											var self = $(this);
											var slider = self.slider({
												create : function() {
													self.find(".text strong").text("-");
													setPathData(self
															.find(".smiley").find(
																	"svg path"),
															self.slider("value"));
												},
												slide : function(event, ui) {												
													self.find(".text strong").text(
															ui.value);
													setPathData(self
															.find(".smiley").find(
																	"svg path"),
															ui.value);	
													
													$("."+self.attr("class").split(" ")[1]).val(ui.value);
													
													if(parseInt(ui.value)<5){
														$("#"+self.attr("class").split(" ")[1].slice(0,2)+"Remark").attr("style","display:block;");
														$("#"+self.attr("class").split(" ")[1].slice(0,2)+"Remark input").attr("required","true");
													} else if(parseInt(ui.value)>=5){
														$("#"+self.attr("class").split(" ")[1].slice(0,2)+"Remark").attr("style","display:none;");
														$("#"+self.attr("class").split(" ")[1].slice(0,2)+"Remark input").val("");
														$("#"+self.attr("class").split(" ")[1].slice(0,2)+"Remark input").removeAttr("required");
													}																									
												},
												range : "min",
												min : 1,
												max : step,
												value : 1,
												step : 1
											});
										});
									}
								}
							});	
																		
								$("#rateExpNo").click(function(){
									$(this).attr("style","background-color:#d2232a;color:white;border:1px solid #d2232a;font-weight:bold;");
									$(this).data("if","selected");
									
									$("#rateExpYes").attr("style","background-color:white;color:black;border:1px solid black;font-weight:bold;");
									$("#rateExpYes").data("if","notSelected");
									
									$("#rateExpNotWorked").val($(this).val());
									
									$(".rateExpNotWorkedDiv").html("");
									
									$(".q1Response").val($(".rateExp").find(".text strong").text());									
									$(".q2Response").val($(".rateExp").find(".text strong").text());									
									$(".q3Response").val($(".rateExp").find(".text strong").text());									
									$(".q4Response").val($(".rateExp").find(".text strong").text());									
									$(".q5Response").val($(".rateExp").find(".text strong").text());									
									$(".q6Response").val($(".rateExp").find(".text strong").text());								
									$(".q7Response").val($(".rateExp").find(".text strong").text());									
									$(".q8Response").val($(".rateExp").find(".text strong").text());																	
							});						
	</script> -->
</body>
</html>