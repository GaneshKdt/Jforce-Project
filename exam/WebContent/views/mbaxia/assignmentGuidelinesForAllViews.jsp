<!DOCTYPE html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<html lang="en">

<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')"
	var="server_path" />


<jsp:include page="../common/jscss.jsp">
	<jsp:param value=" Assignment Guidelines " name="title" />
</jsp:include>

<%

%>
<style>
#parentSpinnerDiv {
	background-color: transparent !important;
	z-index: 999;
	width: 100%;
	height: 100vh;
	position: fixed;
}

#childSpinnerDiv {
	color: black;
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.list-group {
	list-style: decimal inside !important
}

.list-group-item {
	display: list-item !important
}

ul {
  list-style-type: none;
}
</style>
<body style="background-color: #ECE9E7;">

	<%-- <%@ include file="../common/header.jsp"%>
 --%>


	<div class="">

		<%-- <jsp:include page="../common/breadcrum.jsp">
				<jsp:param value="Exam;Assignments" name="breadcrumItems" />
			</jsp:include> --%>

		<div class="">
			<div class="">

				<%-- <jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Tests" name="activeMenu" />
					</jsp:include> --%>


				<div class="">
					<%-- <%@ include file="../common/studentInfoBar.jsp"%> --%>

					<div class="container">


						<div class="clearfix"></div>
						<div class="panel-content-wrapper">

							<!-- Code for page goes here start -->


							<div id="parentSpinnerDiv">

								<div id="childSpinnerDiv">
									<i class="fa fa-refresh fa-spin"
										style="font-size: 50px; color: grey;"></i>
								</div>

							</div>

							<div class="jumbotrom">
								<div class="container-fluid">
									<h2 class="red text-capitalize">Internal Assignment
										Guidelines</h2>
								</div>
								<div class="container-fluid">

									<div>
										<ul>
											<li class="list-group-item"><strong>Internal
													Assessment Test has 40% credence</strong>. As per the applicable
												subject, every student has to attempt and submit the
												Internal Assignment Test on or before the time slot
												suggested by the concerned subject faculty.</li>

											<li class="list-group-item">In each subject there would
												<strong>be Total 5 assessment of 10 marks each</strong>.
												Assignment result would be tentatively declared within 72
												hours on the student portal.
											</li>

											<li class="list-group-item">In case a student appears
												for all 5 assignments in a subject, the Best Four of
												assignment marks would be taken as internal assignment has
												40 marks credence.</li>

											<li class="list-group-item">In any session, if a student
												misses to submit assignment for any reason whatsoever - no
												request to re-conduct/resubmit the internal assignment again
												would be considered. Student will have to attend the next
												session assignment</li>

											<li class="list-group-item">For each subject, time
												duration of Internal Assignment Test is <strong>45
													minutes</strong>. Once the test has started, student needs to
												attempt the questions and duly submit the test before the
												set end time. The <strong>'Timer'</strong> is displayed on
												screen. If the duration of the test is over, it would be <strong>auto-submitted</strong>
												with all the answers the student has clicked on <strong>'Next'
													only</strong>.
											</li>

											<li class="list-group-item"><p class="text-danger">
													<strong>Pls. Note: Do not refresh the page, press
														back button or any navigation key</strong>
												</p> once the Internal Assignment Test has started.</li>

											<li class="list-group-item">Students must ensure there
												is proper internet connectivity at their side while
												attempting the tests. No other disruption/Power failure/any
												other IT issue faced at student side will be consider</li>

											<li class="list-group-item">In case of some technical /
												IT issue student can re-join the test but it has to be
												within the duration of assignment (i.e. within 45 mins of
												starting the test) and student can resume from where it was
												interrupted. However, no network disruption /power failure
												issues faced at the student's end will be considered.</li>

											<li class="list-group-item"><strong>Total
													weightage of Internal Assignment Test is 10 marks.</strong> Internal
												Assessment Test questions would be multiple choice questions
												MCQs (Either <strong>Single Select</strong> - i.e. one right
												option or <strong>Multiple Select</strong> - i.e. more than
												one right option) or True/False of 1 mark weightage or
												Descriptive Question/Caselet.</li>

											<li class="list-group-item"><strong>There is no
													negative marking in MCQs.</strong></li>

											<li class="list-group-item">Students taking IA test on
												mobile device, Need to use pinch to zoom feature where
												question is having any image</li>

											<li class="list-group-item">Student can attempt the
												questions in any order within time frame and complete the
												online internal test within the set time frame.</li>

											<li class="list-group-item">Only one question will be
												displayed on the screen at a time. Student need to select
												the right option/s (based on Single Select / Multi-Select)
												and click on "Next" button.</li>

											<li class="list-group-item">To navigate through question
												the status column is to be used, by clicking on particular
												question number and that question will visible in main view</li>

											<li class="list-group-item">A question can be <strong>Tagged</strong>
												for reviewing later by using button with <strong>'Bookmark
													Icon'</strong></li>

											<li class="list-group-item">After selecting option or
												completing descriptive answer/s, it is mandatory to click in
												"Save" or "Save & Next" button to save answer. "Save" button
												only save your answer whereas "Save & Next" button will save
												your answer and will go to next question.</li>

											<li class="list-group-item">After saving all the
												answers, student should click on "FINISH ASSIGNMENT", system
												will prompt, "Are you sure?" Click on Yes , I'am sure, If
												student is still not sure, click on "No Cancel it" and cross
												check once again all attempted and un attempted questions if
												there is still time and them again click on "FINISH
												ASSIGNMENT", and Yes , I'am sure.</li>

											<li class="list-group-item">"FINISH ASSIGNMENT", button
												will be active to submit only after last question is
												visited.</li>

											<li class="list-group-item">There is an <strong>on-screen
													calculator enabled</strong> which students can use during their IA.
												Use of paper pen and physical calculator is also allowed
												during the IA. However switching screens (Alt. Tab) is
												strictly prohibited.
											</li>

											<li class="list-group-item">While giving the Internal
												Assessment test <strong>student are not allowed</strong> to
												close the test page, minimize the test page or switch to a
												different page. These user activities are captured and
												student will marked for Copy Case for entire Internal
												Assessment test.
											</li>

											<li class="list-group-item">As per respective Internal
												Assessment question paper requirement, <strong>proctoring
													can be kept as enabled or disabled</strong>. In the case of <strong>Proctoring
													kept enabled- Students cannot move away from test taking
													window</strong>/ switch to a different page. In the case of <strong>Proctoring
													kept disabled- Students can move away from test taking
													window/ switch to a different page for using specific tools
													to solve the given questions.</strong> This instruction will be
												mentioned on the test page screen before the test commences.
												Student is expected to read the instructions correctly.
											</li>

											<li class="list-group-item">For Descriptive type answers
												- Student has to ensure quality of content than the word
												count while answering the questions. Write the answers in
												your own words. Students copying the matter from internet,
												other students or from any other source ad verbatim will be
												penalized. Students are only allowed to give subjective
												answers (in text or numbers) for descriptive questions.
												There is no provision to add/insert table/ image/ graph/
												diagram/ histogram/ symbol/ object/ equation etc.</li>

											<li class="list-group-item">Result of Internal
												Assessment Test will not be displayed immediately. It would
												be notified separately when the results will be displayed.</li>

											<li class="list-group-item"><strong>In each
													subject wherever applicable, student will get only one
													attempt towards Internal Assessment Test.</strong></li>

											<li class="list-group-item">Please exercise utmost
												caution while you take the online assessment test for the
												applicable subject/s.</li>

											<li class="list-group-item">After every completion of
												Internal Assessment Test an auto-generated email is sent
												from the system to the students registered email id. Student
												must keep the copy of the same for records.</li>

											<li class="list-group-item"><strong>Auto-generated
													submission email is only the acknowledgement of the test
													attempted by the student in the system (right/wrong/blank/)
													as the case may be and not confirmation from NGA-SCE
													certifying it is the rightly attempted/completed test.</strong></li>

											<li class="list-group-item">Students need to attempt and
												submit the internal assessment tests well before time and do
												not wait for the last minute submission. <strong>Students
													who are overseas need to follow Indian Standard Time.</strong>
											</li>

											<li class="list-group-item">No request for internal
												assessment test will be considered post the deadline.</li>

											<li class="list-group-item">In case of any doubt or
												query regarding Internal Assessment Tests, student can get
												in touch by email at ngasce.exams@nmims.edu for
												clarification before last date of assignment submission. No
												last minute query/request will be accepted, Pls. mention
												your student number (SAP ID) in all communication with the
												institute</li>

										</ul>
									</div>

									<p style="font-size: 16px;"></p>
									<div class="row" style="padding-bottom: 60px;">

										<div class="col-xs-12">
											<form id="startStudentTestForm"
												action="/exam/mbax/ia/s/startStudentTestForAllViews"
												method="post">

												<input type="hidden" value="${testIdForUrl}"
													name="testIdForUrl" /> <input type="hidden"
													value="${sapidForUrl}" name="sapidForUrl" /> <input
													type="hidden" value="${consumerProgramStructureIdForUrl}"
													name="consumerProgramStructureIdForUrl" />

											</form>
											
											<a id="goToTestPage" class="btn btn-primary"
												style="white-space: normal; text-align: left;" href="#">
												I have read and understood the guidelines. Start test</a>


										</div>
									</div>

								</div>
							</div>


							<!-- Code for page goes here end -->

						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>

	<script>
		$(document)
				.ready(
						function() {

							var lastClicked;

							var resquest_time = new Date().getTime();
							var response_time = new Date().getTime();

							var testId = "${testId}";

							var sapid = "${userId}";

							var body = {
								'sapid' : sapid,
								'testId' : testId
							};
							console.log(body);

							try {

								$("#goToTestPage")
										.click(
												function(e) {

													$('#parentSpinnerDiv')
															.show();
													console
															.log('Called Submit')
													e.preventDefault();
													now = new Date().getTime();

													if (lastClicked
															&& (now
																	- lastClicked < 3000)) {

													} else {
														lastClicked = now;

														goToIATestPageButtonClicked();

														setTimeout(
																function() {
																	document
																			.getElementById(
																					"startStudentTestForm")
																			.submit();
																}, 2000);

													}

												});
							} catch (err) {
								console.log("Catch error : " + err.message);

								goToIATestPageButtonClicked();

								setTimeout(function() {
									document.getElementById(
											"startStudentTestForm").submit();
								}, 2000);
							}

							function logPageLoadedEvent() {

								try {
									let apiUrl = window.location.origin
											+ "/exam/mbax/ia/s/assignmentGuidelines_pageLoaded";
									response_time = new Date().getTime();
									response_payload_size = 0;
									asyncApiLogAjaxCall(sapid, apiUrl,
											resquest_time, response_time,
											"Success", "",
											response_payload_size)

								} catch (err) {
									
								}
							}
							function goToIATestPageButtonClicked() {

								try {
									let apiUrl = window.location.origin
											+ "/exam/mbax/ia/s/startStudentTestPage_goToLinkClicked";
									response_time = new Date().getTime();
									response_payload_size = 0;
									asyncApiLogAjaxCall(sapid, apiUrl,
											resquest_time, response_time,
											"Success", "",
											response_payload_size)

								} catch (err) {
									//////////console.log("Error
								}
								//apiLogCall end
							}

							//asyncApiLogAjaxCall start
							function asyncApiLogAjaxCall(sapid, apiUrl,
									resquest_time, response_time, status,
									error_message, response_payload_size) {
								try {

									let networkInfoForAsyncApiLogAjaxCall = {}
									try {
										let deviceMemoryInfo = "";
										let platformInfo = "";
										let hardwareConcurrencyInfo = "";

										try {

											deviceMemoryInfo = "This device has at least "
													+ (navigator.deviceMemory ? navigator.deviceMemory
															+ ""
															: "")
													+ " GiB of RAM.";
										} catch (error) {
											deviceMemoryInfo = "Not Available";
										}
										try {

											platformInfo = ""
													+ (navigator.platform ? navigator.platform
															: "") + "";
										} catch (error) {
											platformInfo = "Not Available";
										}
										try {

											hardwareConcurrencyInfo = ""
													+ (window.navigator.hardwareConcurrency ? window.navigator.hardwareConcurrency
															+ ""
															: "") + "";
										} catch (error) {
											hardwareConcurrencyInfo = "Not Available";
										}

										networkInfoForAsyncApiLogAjaxCall = {
											downlink : navigator.connection.downlink,
											rtt : navigator.connection.rtt,
											downlinkMax : navigator.connection.downlinkMax,
											effectiveType : navigator.connection.effectiveType,
											type : navigator.connection.type,
											saveData : navigator.connection.saveData,
											deviceMemoryInfo : deviceMemoryInfo,
											platformInfo : platformInfo,
											hardwareConcurrencyInfo : hardwareConcurrencyInfo,
										}
									} catch (error) {
										networkInfoForAsyncApiLogAjaxCall = {
											errorMessage : 'Not Available'
										}
									}
									let bodyForAsyncApiLogAjaxCall = {

										"sapid" : sapid ? sapid.toString() : "",
										"api" : apiUrl,
										"resquest_time" : resquest_time,
										"response_time" : response_time,
										"response_payload_size" : response_payload_size ? response_payload_size
												.toString()
												: "0",
										"status" : status,
										"error_message" : error_message,
										"platform" : "Web",
										"networkInfo" : JSON
												.stringify(networkInfoForAsyncApiLogAjaxCall),

									};
									//console.log("IN asyncApiLogAjaxCall got bodyForAsyncApiLogAjaxCall : ");
									//console.log(bodyForAsyncApiLogAjaxCall);
									$
											.ajax(
													{
														type : 'POST',
														url : 'https://ngasce-content.nmims.edu/ltidemo/saveNetworkLogs',
														data : JSON
																.stringify(bodyForAsyncApiLogAjaxCall),
														contentType : "application/json",
														dataType : "json",
														timeout : 10000,

													})
											.done(
													function(data) {
														console
																.log("iN asyncApiLogAjaxCall AJAX SUCCESS");
														console.log(data);

													})
											.fail(
													function(xhr) {
														console
																.log(
																		"iN asyncApiLogAjaxCall AJAX eRROR",
																		xhr);

													});

								} catch (err) {
									console
											.log("IN asyncApiLogAjaxCall got Error : ");
									console.log(err);
								}

							}
							//asyncApiLogAjaxCall end

							$('#parentSpinnerDiv').hide();
							logPageLoadedEvent();

						});//doc.ready()
	</script>


	<script>
		try {
			parent && parent.window.setHideShowHeaderSidebarBreadcrumbs ? parent.window
					.setHideShowHeaderSidebarBreadcrumbs(false)
					: null
		} catch (err) {
			console.log(err);
		}
	</script>

</body>


</html>