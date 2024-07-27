<!DOCTYPE html>
<html lang="en">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<head>
<meta charset="ISO-8859-1">

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="IA Benefit of Doubt" name="title" />
</jsp:include>
<style>
	.panel-default {
		margin-top: 1em;
	}
	.panel-heading {
		padding: 1em 1.6em;
	}
	.section-head {
		padding-bottom: 2em;
	}
	.panel-body {
		padding-left: 2em;
		padding-right: 2em;
	}
	.borderless-table {
		border: none;
		background: inherit;
	}
	.question-head {
		background: inherit;
		padding-right: 0;
	}
	.question-text {
		display: inline-flex;
	}
	.question-text img {
		max-height: 100%;
		max-width: 100%;
	}
	.question-count {
		padding-right:0.8em;
	}
	.block-text {
		word-wrap: break-word;
	}
	.question-body {
		padding: 1em;
	}
	.question-head p {
		font-size: 1em !important;
	}
	.question-head span {
		font-size: 1em !important;
	}
	a {
		font-size: 0.8rem;
	}
	.link-btn:hover {
		color: cyan;
	}
	.bod-btn {
		margin-left: 0.35em;
	}
	.bod-btn:hover {
		background-color: #d2232a !important;
	}
	.attempt-table {
		max-width: fit-content;
	}
</style>
</head>
<body>
	<jsp:include page="../adminCommon/header.jsp" />
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
   			<div class="container-fluid">
       			<ul class="sz-breadcrumbs">
	        		<li><a href="/studentportal/home">Home</a></li> 
	        		<li><a href="/exam/viewAllTests">Search Tests</a></li>
	        		<li><a href="/exam/viewTestDetails?id=${testId}" class="encodedHref">View Test Details</a></li>
	        		<li><a href="/exam/admin/iaBenefitOfDoubt?testId=${testId}" class="encodedHref">Benefit of Doubt</a></li>
		        </ul>
          	</div>
        </div>
	</div>
	
	<div class="sz-main-content menu-closed">
		<div class="sz-main-content-inner">
			<jsp:include page="../adminCommon/left-sidebar.jsp">
				<jsp:param value="" name="activeMenu" />
			</jsp:include>
			
			<div class="sz-content-wrapper examsPage">
				<jsp:include page="../adminCommon/adminInfoBar.jsp" />
				<div class="sz-content">
				
					<div class="panel panel-default">
						<div class="panel-heading section-head">
							<h2 class="panel-title red">IA Benefit of Doubt</h2>
						</div>
						
						<div class="panel-body">
							<c:if test="${testCompleted ne true}">
							    <div class="alert alert-danger alert-dismissible" role="alert">
									<strong>Cannot apply Benefit of Doubt as test is still ongoing.</strong>
							    </div>
							</c:if><!-- Displays the error message if test not completed -->
							
							<input type="hidden" id="userId" value="${userName}" />
							<input type="hidden" id="testId" value="${testId}" />
							<input type="hidden" id="results-live" value="${resultsLive}" />
							<input type="hidden" id="max-attempts" value="${maxAttempts}" />
								
							<c:forEach items="${sectionQuestionMap}" var="sectionQuestion">
								<h4 class="red">${sectionQuestion.key}</h4>
								
								<c:forEach items="${sectionQuestion.value}" var="question" varStatus="counter">
									<div class="panel-group" id="accordion-${sectionQuestion.key}" role="tablist" aria-multiselectable="true">
										<div class="panel panel-default">
											<div class="panel-heading question-head" role="tab" id="heading-${question.id}">
												<div class="collapsed row" role="button" data-toggle="collapse" data-parent="#accordion" 
													data-questionId="${question.id}" data-questionType="${question.type}" href="#collapse-${question.id}" 
													aria-expanded="true" aria-controls="collapse" onclick="questionAttemptData(this)">
													<div class="col-xs-9 question-text">
														<p class="question-count">${counter.count}.</p><div>${question.question}</div>
													</div>
													<div class="col-xs-1">
														<p class="block-text" title="Type">${question.typeInString}</p>
													</div>
													<div class="col-xs-1">
														<p class="block-text" title="Marks">${question.marks}</p>
													</div>
													<div class="col-xs-1">
														<i class="fa-solid fa-angle-down link-btn"></i>
													</div>
												</div>
											</div><!-- Accordion card header -->
											
											<div id="collapse-${question.id}" class="panel-collapse collapse" 
												role="tabpanel" aria-labelledby="question-data">
												<div class="question-body">
													<div id="alert-${question.id}" class="alert alert-dismissible" hidden>
														<button type="button" class="close" aria-label="Close" onclick="closeAlertBox(this)">
															<span aria-hidden="true">&times;</span>
														</button>
														<strong></strong>
												    </div>
													<p>Question Type: ${question.typeInString}</p>
													<p>Question Marks: ${question.marks}</p>
													
													<c:if test="${testCompleted eq true}">
														<table id="attemptTable-${question.id}" class="table table-bordered attempt-table" 
															aria-describedby="student-question-attempt-count" hidden>
															<tbody>
																<tr>
																	<td>No. of students who attempted the test</td>
																	<td class="test-attempted"></td>
																</tr>
																<tr>
																	<td>No. of students who got this particular question</td>
																	<td class="student-question"></td>
																</tr>
																<c:choose>
																	<c:when test="${(question.type eq 1 || question.type eq 2 || question.type eq 5) && maxAttempts eq 1}">
																		<tr>
																			<td>No. of students who selected the correct option(s)</td>
																			<td class="correct-option"></td>
																		</tr>
																		<tr>
																			<td>No. of students who selected the wrong option(s)</td>
																			<td class="wrong-option"></td>
																		</tr>
																	</c:when>
																	<c:otherwise>
																		<tr>
																			<td>No. of students who attempted this question</td>
																			<td class="question-attempted"></td>
																		</tr>
																	</c:otherwise>
																</c:choose>
																<tr>
																	<td>No. of students who did not attempt this question</td>
																	<td class="not-attempted"></td>
																</tr>
															</tbody>
														</table>
														
														<a href="downloadQuestionAttemptsReport/?testId=${testId}&questionId=${question.id}&questionType=${question.type}&testMaxAttempts=${maxAttempts}"
															class="btn btn-danger">Download Question Attempts Report
														</a>
														
														<c:choose>
															<c:when test="${not fn:containsIgnoreCase(bodQuestionIdList, question.id)}">
																<button type="submit" class="btn btn-default bod-btn" data-questionId="${question.id}" 
																	onclick="applyBod(this)">Apply BoD
																	<c:if test="${resultsLive eq true}">
																		&nbsp;and Re-Run Results
																	</c:if></button>
															</c:when>
															<c:when test="${fn:containsIgnoreCase(bodQuestionIdList, question.id)}">
																<button type="submit" class="btn btn-danger bod-btn" data-questionId="${question.id}" 
																	onclick="removeBod(this)">Remove BoD
																	<c:if test="${resultsLive eq true}">
																		&nbsp;and Re-Run Results
																	</c:if></button>
															</c:when>
														</c:choose>
													</c:if>
												</div>
											</div><!-- Accordion card body -->
										</div><!-- Accordion card -->
									</div><!--Accordion wrapper-->
								</c:forEach>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />
	
<script type="text/javascript">
	const userName = document.getElementById("userId").value;							//ID of the user
	const testId = document.getElementById("testId").value;								//ID of the test
	const isResultsLive = document.getElementById("results-live").value;				//test results live
	const maxAttempts = document.getElementById("max-attempts").value;					//test max attempts

	/*
		Student question attempts data is fetched and displayed in a tabular format.
	*/
	async function questionAttemptData(questionDiv) {
		const pathName = "/exam/m/admin/questionAttemptData?";
		const questionIdValue = questionDiv.getAttribute("data-questionId");
		const questionTypeValue = questionDiv.getAttribute("data-questionType");
		
		const url = pathName + new URLSearchParams({
														testId: testId,
														questionId: questionIdValue,
														questionType: questionTypeValue,
														testMaxAttempts: maxAttempts
													});

		const request = new Request(url, {
											method : "GET",
											headers : new Headers({
												"Content-Type" : "application/json; charset=UTF-8",
												"Accepts" : "application/json"
											})
									});

		try {
			const response = await fetch(request);
			const reponseData = await new Promise(function(resolve, reject) {
				if (response.ok)
					return resolve(response.json());
				else
					return reject(response.json());
			});

			const dataObject = await reponseData;
			console.log("Fetch question attempt data response message: ", dataObject.message);

			if(dataObject.success === true) {
				const table = document.getElementById("attemptTable-" + questionIdValue);
				table.querySelector(".test-attempted").textContent = dataObject.data["test-attempted"];									//No. of students attempted the test
				table.querySelector(".student-question").textContent = dataObject.data["applicable-students"];							//No. of students who got the particular question
				
				if((questionTypeValue === "1" || questionTypeValue === "2" || questionTypeValue === "5") && maxAttempts === "1") {		//single-select, multi-select and true-false questions
					table.querySelector(".correct-option").textContent = dataObject.data["right-selection"];							//No. of students who selected the right options
					table.querySelector(".wrong-option").textContent = dataObject.data["wrong-selection"];								//No. of students who selected the wrong options
				}
				else
					table.querySelector(".question-attempted").textContent = dataObject.data["question-attempted"];						//No. of students who attempted the question
				
				table.querySelector(".not-attempted").textContent = dataObject.data["not-attempted"];									//No. of students who did not attempt the question
				table.hidden = false;
			}
		}
		catch(error) {
			console.error("Error while calling question attempt data API: ", error);
		}
	}

	/*
		Applying Benefit of Doubt to the selected question and re-run results if test results live.
	*/
	async function applyBod(bodBtn) {
		bodBtn.disabled = true;
		let testResultsLive = isResultsLive === "true";
		
		const pathName = "/exam/m/admin/applyBenefitOfDoubt?";
		const questionId = bodBtn.getAttribute("data-questionId");
		
		const bodResponse = await benefitOfDoubt(pathName, questionId);
		console.log("BoD applied: ", bodResponse.benefitOfDoubt, " for testId: ", testId, " and questionId: ", questionId);
		
		if(bodResponse.benefitOfDoubt) {
			testResultsLive = bodResponse.resultsLive;
			bodBtn.textContent = (testResultsLive ? "Remove BoD and Re-Run Results" : "Remove BoD");								//Button text depending on test results live
			bodBtn.setAttribute("onclick", "removeBod(this)");																		//Change onclick action of button
			
			bodBtn.classList.remove("btn-primary");
			bodBtn.classList.add("btn-danger");
		}

		bodAlertBox(questionId, "Apply", bodResponse.benefitOfDoubt, testResultsLive);												//Displaying alert box with an appropriate message
		bodBtn.disabled = false;
		return bodResponse.benefitOfDoubt;
	}

	/*
		Removing Benefit of Doubt to the selected question and re-run results if test results live.
	*/
	async function removeBod(bodBtn) {
		bodBtn.disabled = true;
		let testResultsLive = isResultsLive === "true";
		
		const pathName = "/exam/m/admin/removeBenefitOfDoubt?";
		const questionId = bodBtn.getAttribute("data-questionId");
		
		const bodResponse = await benefitOfDoubt(pathName, questionId);
		console.log("BoD removed: ", bodResponse.benefitOfDoubt, " for testId: ", testId, " and questionId: ", questionId);

		if(bodResponse.benefitOfDoubt) {
			testResultsLive = bodResponse.resultsLive;
			bodBtn.textContent = (testResultsLive ? "Apply BoD and Re-Run Results" : "Apply BoD");									//Button text depending on test results live
			bodBtn.setAttribute("onclick", "applyBod(this)");																		//Change onclick action of button
			
			bodBtn.classList.remove("btn-danger");
			bodBtn.classList.add("btn-primary");
		}

		bodAlertBox(questionId, "Remove", bodResponse.benefitOfDoubt, testResultsLive);												//Displaying alert box with an appropriate message
		bodBtn.disabled = false;
		return bodResponse.benefitOfDoubt;
	}

	/*
		Apply/Remove Benefit of Doubt API call.
	*/
	async function benefitOfDoubt(pathName, questionIdValue) {
		const url = pathName + new URLSearchParams({
														testId: testId,
														questionId: questionIdValue,
														userId: userName
													});
		
		const request = new Request(url, {
											method: "POST",
											headers: new Headers({
												"Content-Type": "application/json; charset=UTF-8",
												"Accepts": "application/json"
											})
									});

		try {
			const response = await fetch(request);
			const reponseData = await new Promise(function(resolve, reject) {
				if(response.ok)
					return resolve(response.json());
				else
					return reject(response.json());
			});

			console.log("BoD response message: ", reponseData.message);
			return reponseData.data;
		}
		catch(error) {
			console.error("Error while calling BoD API: ", error);
			const bodErrorObj = {	benefitOfDoubt: false	}
			return bodErrorObj;
		}
	}

	/*
		Alert message displayed to the user with apply/remove BoD action success/failure message.
	*/
	function bodAlertBox(questionId, action, actionSuccess, resultsLive) {
		let bodAlert = document.getElementById("alert-" + questionId);
		bodAlert.classList.remove(actionSuccess ? "alert-danger" : "alert-success");
		bodAlert.classList.add(actionSuccess ? "alert-success" : "alert-danger");
		bodAlert.removeAttribute("hidden");

		const alertText = action + " Benefit of Doubt " + (actionSuccess ? (resultsLive  ? "and re-run Results " : "") + "successful!" : "unsuccessful!");
		bodAlert.getElementsByTagName("strong")[0].textContent = alertText;
	}

	/*
		Close button onclick function which hides the alert box element.
	*/
	function closeAlertBox(alertBox) {
		alertBox.parentElement.hidden = true;
	}
</script>
</body>
</html>