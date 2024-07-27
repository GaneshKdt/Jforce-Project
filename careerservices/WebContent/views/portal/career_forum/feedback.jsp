 <!DOCTYPE html>


<%@page import="com.nmims.beans.SessionFeedbackQuestion"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<html lang="en">
	

	
	
	<jsp:include page="/views/common/jscss.jsp">
		<jsp:param value="Session Feedback" name="title"/>
	</jsp:include>
	
	<style>
		
		.header{
			font-size: 2rem;
			padding-bottom: 30px;
			color: #d2232a;
		}
		
	</style>
	
	<body>
	
		<jsp:include page="/views/common/header.jsp"/>
	
		<div class="sz-main-content-wrapper">
		
			<jsp:include page="/views/common/breadcrum.jsp">
				<jsp:param value="<a href='/careerservices/Home'>Career Services</a>;<a href='career_forum'>Career Forum</a>;<a href='viewScheduledSession?id=${ sessionId }'>Session Details</a>;Feedback" name="breadcrumItems" />
			</jsp:include>
			
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
					<jsp:include page="/views/common/left-sidebar.jsp">
						<jsp:param value="Academic Calendar" name="activeMenu"/>
					</jsp:include>
					<div class="sz-content-wrapper examsPage">
						<jsp:include page="/views/common/studentInfoBar.jsp"/>
						<jsp:include page="/views/portal/loader.jsp" />
						<div class="sz-content" style="display:none;" id="page-content">
							<div class="sz-content padding-top ">
								<div class="card p-3">
									<div class="row">
										<div class="col-xl-9 col-md-12 col-sm-12 my-3 mx-auto ">
											<div class="card-body row mx-3">
												<div class="col-12">
													<h2 style="font-size: 1.6rem;"><span>Feedback For </span><span id="sessionName"></span></h2>
												</div>
												<div class="col-xs-12 d-none d-sm-block d-md-none">
													<span id="sessionImage"></span>
												</div>
												<div class="col-md-6 col-xs-12">
													<table class="table">
														<tr>
															<td class="text-left pl-3"><b>Speaker</b></td>
															<td class="text-left"><span><span id="facultyName"></span></span></td>
														</tr>
														
														<tr>
															<td class="text-left pl-3"><b>Date</b></td>
															<td class="text-left"><span><span id="sessionDate"></span></span></td>
														</tr>
													</table>
												</div>
												<div class="col-md-6 col-xs-12 d-sm-none d-md-block">
													<span id="sessionImage2"></span>
												</div>
											</div>
											<div class="col-12 my-4">
												<form>
													<input type="hidden" name = "sapid" value="<%= (String)request.getSession().getAttribute("userId") %>">
													<input type="hidden" name = "sessionId" value="<%= request.getParameter("sessionId") %>">
													<div id="successfullyAttendedDiv" class="row mx-3 col-12">
														<div class="form-group mx-4">
															<label style="font-size: 0.8rem">Did you successfully view this session?</label>
															<select class="form-control pl-2" id="successfullyAttended" name="successfullyAttended:boolean" onchange="successfullyViewedChanged()" required>
																<option value=""></option>
																<option value="false">No</option>
																<option value="true">Yes</option>
															</select>
														</div>
													</div>
													<div id="notAttendedReason" class="row mx-2 col-12">
														
													</div>
													<div id="questionGroups" class="row mx-2 col-12">
												
													</div>
													<div class="row mx-5 col-12">
														<button type="button" onclick="submitFeedback()" class="btn btn-primary ">Submit</button>
													</div>
												</form>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
			
 	
		<jsp:include page="/views/common/footer.jsp"/>
		
		<script>
			var sapid = '{"sapid": "<%= (String)request.getSession().getAttribute("userId") %>"}';
			var groupNumber = 0;

			function submitFeedback(){

				console.log();
				if(!checkRequiredInputs()){
					return false;
				}else{
					var ddata = formDataToJSON();
					startLoading();
					$.ajax({
						url: '/careerservices/m/submitSessionFeedback',
						type: 'post',
						dataType : 'json',
						contentType: 'application/json',
						data: ddata, 
						success : function(result) {
								if (result.status == "success") {
									window.location.href="viewScheduledSession?id=<%= request.getParameter("sessionId") %>";
								} else {
									showLoadingError();
								}
								return false;
							},
						error: function(xhr, resp, text) {
							console.log(xhr, resp, text);
							errorMessage("Error. Please check all fields and retry!");
							showLoadingError();
						}
					});
					return false;
				}
			}
			$(document).on('ready', function () {
				renderPage();
			});
			var feedbackQuestions = [];
			function renderPage(){
				
				$.ajax({
					type: 'POST',
					url: '/careerservices/m/getFeedbackQuestions?sessionId=<%= request.getParameter("sessionId") %>&sapid=<%= (String)request.getSession().getAttribute("userId") %>',
					data: sapid,
					contentType: "application/json;", 
					dataType: "json",
					success: function(data, textStatus ){

						if(data.status != "success"){
							showLoadingError();
							return;
						}
						if(data.response != null && data.response.feedbackQuestions.length > 0){
							if(data.response.sessionDetails.thumbnailUrl != null && data.response.sessionDetails.thumbnailUrl != ""){
								var imageHtml = `<img src="` + data.response.sessionDetails.thumbnailUrl + `" alt="No screenshot available" style="width:100%">`;
								$("#sessionImage").html(imageHtml);
								$("#sessionImage2").html(imageHtml);
							}
							$("#sessionName").html(data.response.sessionDetails.sessionName);
							$("#facultyName").html(data.response.sessionDetails.facultyName);
							$("#sessionDate").html(data.response.sessionDetails.date);
							feedbackQuestions = data.response.feedbackQuestions;
						}
						
						stopLoading();
					},
					error: function (error) {
						showLoadingError();
					}
				});
			}
			
			function populateQuestions(){
				var questionsHtml = ``;
				feedbackQuestions.forEach(function(questionGroup){
					questionsHtml += addQuestionGroup(questionGroup);
				})
				$('#questionGroups').html(questionsHtml);
			}
			function addQuestionGroup(questionGroup){
				if(questionGroup.questions.length > 0){
					groupNumber ++;
					var questionGroupHtml = `
						<div class="col-12 m-4">
							<h2 class="col-12"> ` + questionGroup.groupName + `</h2>
					`;
					questionGroup.questions.forEach(function(question){
						questionGroupHtml += addQuestion(question, questionGroup.feedbackQuestionGroupId);
					});
					questionGroupHtml += `</div>`;
					return questionGroupHtml;
				}else{
					return '';
				}
				
			}
			
			function addQuestion(question, groupNumber){
				var questionString = `<input type="hidden" name = "answers[` + question.feedbackQuestionId + `][feedbackQuestionId]" value="` + question.feedbackQuestionId + `">`;	
				
				questionString += `
					<div class="form-group mx-2" style=" display: inline-flex; width: 95%">`;

				questionString += `
						<div class="col-4 text-vertical-center" >
							<p style="font-size: 0.8rem">` + question.questionString + `</p>
						</div>`;
				if(question.questionType == "number"){
					
					questionString += `<div class="col-4 text-vertical-center">`;
					
					if(question.giveAdditionalComment == true){

						questionString += `
								<select class="form-control pl-2" id="answers[` + question.feedbackQuestionId +`][value]"
									name="answers[` + question.feedbackQuestionId +`][value]" onchange="checkRatingValueChanged(` + question.feedbackQuestionId +`)" required>
									<option value="0">0</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
								</select>
								</div>
								<div class="col-4 text-vertical-center" id="comment[` + question.feedbackQuestionId + `]">
									<textarea class="form-control pl-2" name = "answers[` + question.feedbackQuestionId + `][comment]" required></textarea> 
								</div>
							`;
					}else{

						questionString += `
								<select class="form-control pl-2" id="answers[` + question.feedbackQuestionId +`][value]"
									name="answers[` + question.feedbackQuestionId +`][value]" required>
									<option value="0">0</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
								</select>
							</div>
							<div class="col-4">
							</div>`;
					}
				}else if(question.questionType == "text"){

					questionString += `
						<div class="col-8 text-vertical-center">
							<textarea class="form-control pl-2" name = "answers[` + question.feedbackQuestionId + `][value]" required></textarea>	
						</div>`;
						
				}else if(question.questionType == "boolean"){
					
					questionString += `<div class="col-4 text-vertical-center">`;
					
					
							
					if(question.giveAdditionalComment == true){

							questionString += `
							
								<select class="form-control pl-2" id="answers[` + question.feedbackQuestionId +`][value]"
									name="answers[` + question.feedbackQuestionId +`][value]" onchange="checkYesNoValueChanged(` + question.feedbackQuestionId +`)" required>
									<option value="0">No</option>
									<option value="1">Yes</option>
								</select>
							</div>
							<div class="col-4 text-vertical-center" id="comment[` + question.feedbackQuestionId + `]">
								<textarea class="form-control pl-2" name = "answers[` + question.feedbackQuestionId + `][comment]" required></textarea>	
							</div>
						`;
					}else{
							questionString += `
								<select class="form-control pl-2" id="answers[` + question.feedbackQuestionId +`][value]"
									name="answers[` + question.feedbackQuestionId +`][value]" required>
									<option value="0">No</option>
									<option value="1">Yes</option>
								</select>
							</div>
							<div class="col-4">
							</div>`;
					}
				}
					

				questionString += `
					</div>
					`;
				return questionString;
			}

			function checkRatingValueChanged(id){
				if($(document.getElementById("answers[" + id + "][value]")).val() < 3){
					$(document.getElementById("comment[" + id + "]")).html(`<textarea class="form-control pl-2" name = "answers[` + id + `][comment]" required></textarea>`);
				}else{
					$(document.getElementById("comment[" + id + "]")).html(``);
				}
			}
			
			function checkYesNoValueChanged(id){
				if($(document.getElementById("answers[" + id + "][value]")).val() < 1){
					$(document.getElementById("comment[" + id + "]")).html(`<textarea class="form-control pl-2" name = "answers[` + id + `][comment]" required></textarea>`);
				}else{
					$(document.getElementById("comment[" + id + "]")).html(``);
				}
			}
			
			function formDataToJSON(){

				if($("#successfullyAttended").val() != ""){
					var myFormData = $('form').serializeJSON();
					var answers = [];
					$.each(myFormData.answers, function(key, value){
						var answer = {};
						$.each(value,function(key2, value2){
							answer[key2] = value2;
						});
						answers.push(answer);
					});
					var data = {};
					data["sessionId"] = myFormData.sessionId;
					data["sapid"] = myFormData.sapid;
					if(myFormData.successfullyAttended == true){
						data["successfullyAttended"] = true;
					}else{
						data["successfullyAttended"] = false;
					}
					data["notAttendedReason"] = myFormData.notAttendedReason;
					data["answers"] = answers;
					return JSON.stringify(data);
				}else{
					alert("Please fill all fields");
				}
			}
			
			function successfullyViewedChanged(){
				if($("#successfullyAttended").val() == "true"){
					$('#notAttendedReason').html('');
					populateQuestions();
				}else if($("#successfullyAttended").val() == "false"){					
					$('#questionGroups').html('');
					var notAttendedReasonText = `
						<div class="col-4 text-vertical-center" >
							<p style="font-size: 0.8rem">Reason for not viewing.</p>
						</div>
						<div class="col-8 text-vertical-center">
							<textarea class="form-control pl-2" name = "notAttendedReason" required></textarea> 
						</div>`;
					$('#notAttendedReason').html(notAttendedReasonText);
				}else{
					alert("Please fill all fields");
				}
			}
			
			function checkRequiredInputs() {
				var sendUpdate = true;
				$('[required]').each(function(){
					if( $(this).val() == "" ){
						alert('Please fill all the fields');
						$(this).focus();
						sendUpdate = false;
						return false;
					}
				});
				return sendUpdate;
			}
		</script>
		
		<script type="text/javascript" src="assets/js/jquery.serializejson.js"></script>
	</body>
</html>