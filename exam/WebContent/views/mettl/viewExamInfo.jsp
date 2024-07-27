<!DOCTYPE html>
<%@page import="java.util.Arrays, java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
 
<%@page import="com.nmims.beans.StudentExamBean"%>
<html lang="en">

<head>
 <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
	<link href="https://d3udzp2n88cf0o.cloudfront.net/css/bootstrap.min.css" rel="stylesheet" />
	<script src='https://kit.fontawesome.com/a076d05399.js' crossorigin='anonymous'></script>
</head>
	<jsp:include page="../common/jscss.jsp">
		<jsp:param value="View Exam Registration" name="title"/>
	</jsp:include>
	<body>
	
		<%@ include file="../common/header.jsp" %>
		<div class="sz-main-content-wrapper">
			<jsp:include page="../common/breadcrum.jsp">
				<jsp:param value="Student Zone;Exam;View Exam Info" name="breadcrumItems"/>
			</jsp:include>
			<div class="sz-main-content menu-closed">
				<div class="sz-main-content-inner">
				<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Assignment" name="activeMenu" />
				</jsp:include>
				</div>
					<div class="sz-content-wrapper examsPage">
						<%@ include file="../common/studentInfoBar.jsp" %>
						<div class="sz-content">										
							<div class="clearfix"></div>
								<%@ include file="../common/messages.jsp"%>
									<c:if test="${bookingInfo != null }">
										<fmt:parseDate value="${bookingInfo.accessStartDateTime }" pattern="yyyy-MM-dd HH:mm" var="accessStartDateTime" />
										<fmt:parseDate value="${bookingInfo.accessEndDateTime }" pattern="yyyy-MM-dd HH:mm" var="accessEndDateTime" />
										<fmt:parseDate value="${bookingInfo.reporting_start_date_time }" pattern="yyyy-MM-dd HH:mm" var="reportingStartDateTime" />
										<jsp:useBean id="now" class="java.util.Date" />
										<h4 class="text-danger text-uppercase fw-bold mt-3">${ bookingInfo.subject }</h4>
									
										<p class="text-muted fs-6"><i class="fa-solid fa-calendar-days"></i><b> Final Exam will Start on : ${ bookingInfo.examStartDateTime }</b></p>
										<p class="text-muted fs-6"><i class="fa-solid fa-calendar-days"></i><b> Exam Reporting Time is : ${ bookingInfo.reporting_start_date_time }</b></p>
										
							<div class="container-fluid">
							<div class="table-responsive mb-2">
										<table class="table " style="font-size:12px">
											<tbody>
												<c:if test="${ bookingInfo.scheduleName != null }">
													<tr>
														<th>Assessment Name</th>
														<td>${ bookingInfo.scheduleName }</td>
													</tr>
												</c:if>
												<tr>
													<th>Subject</th>
													<td>${ bookingInfo.subject }</td>
												</tr>
												<tr>
													<th>Exam Center</th>
													<td>${ bookingInfo.examCenterName }   
													
													<c:choose>
														<c:when test="${ bookingInfo.googleMapUrl != null && bookingInfo.googleMapUrl != '' }">
															<a href="${bookingInfo.googleMapUrl}" target="_blank"> &nbsp; View on Map</a> 
														</c:when>
														<c:when test="${ bookingInfo.googleMapUrl == null  && bookingInfo.examCenterName != 'AT MY LOCATION'}">
															<a href="#" onclick="getExamCenterGoogleMapUrl()"> &nbsp; View on Map</a> 
														</c:when>
													</c:choose>									
													</td>
												</tr>
												<c:if test="${ bookingInfo.maxScore != null }">
													<tr>
														<th>Weightage</th>
														<td>${ bookingInfo.maxScore } Marks</td>
													</tr>
												</c:if>								
											</tbody>
										</table>
									</div>
								<div class="row">
									<div class="col">
										<div class="card border-0 shadow-sm ">
											<div class="card-body">
												<c:set var="examDuration" value="150" />
												<%
													request.setAttribute("programList", Arrays.asList("ACDM", "ACOM", "ACWM", "CBM", "CCC", "CDM", "CITM - DB", "CITM - ES",
														"CITM - ET", "COM", "CPBM", "CPM", "CWM", "M.Sc. (AI & ML Ops)"));
												%>
												<%
													StudentExamBean student = (StudentExamBean) request.getSession().getAttribute("studentExam");
						
												List<String> l = (List<String>) request.getAttribute("programList");
												if (null != l && null != student.getProgram() && l.contains(student.getProgram().trim())) {
													pageContext.setAttribute("examDuration", "120");
												}
												%>
												<h4 class="mb-2"  style="color:#d2232a"><b>Instruction</b></h4>								
													<p style="font-size: 14px"><b>Before you start the exam,</b> please ensure  that you fulfill the necessary requirement for the assessment.</p>
						 							<ul>	
						 								<p class="mb-1 fs-5 fw-normal"> <li>To ensure your system is ready for exam, you have necessary permission, and a fully functioning camera/mic, perform the <b>System Compatibility </b>Check in <b>Step 01</b>.</li></p>
						 								<p class="mb-1 fs-5 fw-normal"> <li>It is mandatory to perform the <b>Demo Exam</b> as mentioned in <b>Step 02</b> to simulate the exam experience and to make  sure you face no challenges during the real exam.</li></p></ul>
						 								<br />
						 								<p class="mb-3 fs-6 fw-normal"><b>Exam Rules : </b>You are  being <b>monitored at all times,</b> make sure there are no deviations from the below mentioned rules</p>
						 							<ul>
						 								<p class="mb-1 fs-5 fw-normal "> <li>Make sure you have a <b>clean clutter-free environment,</b>It is recommended to have a blank wall in the background</li></p>
						 								<p class="mb-1 fs-5 fw-normal "> <li>There are <b>no additional people</b> allowed in the exam room apart from the test-taker.</li></p>
						 								<p class="mb-1 fs-5 fw-normal "> <li>You are <b>not allowed to use your mobile phones </b> or any external reference material(book/notes) during the exam.</li></p>
						 								<p class="mb-1 fs-5 fw-normal "> <li>You are <b>not allowed to navigate away from  the test screen</b> to other browser/references material on the system.</li></p>
						 								<p class="mb-1 fs-5 fw-normal"> <li>At no point in the time should the microphone or camera to be turned off and<b> no headphones are allowed.</b></li></p>
						 								<p class="mb-1 fs-5 fw-normal"> <li><b>No food or beverage </b>except water are  allowed during examination</li></p>
						 								<p class="mb-1 fs-5 fw-normal"> <li>You are<b> not allowed any bio breaks, </b>In case any special exception are needed please submit the necessary document 7 days before the exam date to the unviersity for approvals</p>
												</ul>
											</div>
										</div>
									</div>
								</div>
								<div class="mt-4 mb-3">						
										<p class="text-muted fs-6"><b>EXAM PROCEDURE</b></p>
								</div>
								
								 <div class="alert alert-warning" role="alert">
						 			<i class="fa-solid fa-circle-info"></i><b> We highly recommend following the steps prescribed to avoid any potential problems and have the best exam experience</b>
								</div>
						
								<div class="row ">
									<div class="col-lg-4 mb-2 ">
										<div class="card h-100">
											<div class="card-body">
												<h5 class="card-title">Step 01</h5>
												<p class="card-text fw-bold text-danger">System Compatibility Test</p>
												<p class="card-text">Make Sure your system is compatible, and all the required permission are avaliable before starting the final exam.</p>
												<p class="card-text">You need to run the System Compatibility check every time before the final exam.</p>																			
												<button type="button" class="btn-primary" onclick="window.open('https://tests.mettl.com/system-check?i=db696a8e#/systemCheck', '_blank');">Run System Compatibility</button>
											</div>											
										</div>																					
									</div>															
									<div class="col-lg-4  mb-2 ">
										<div class="card h-100">
											<div class=" card-body">
												<h5 class="card-title">Step 02</h5>
												<p class="card-text fw-bold text-danger">Demo Exam</p>
												<p class="card-text">Please <b>start the demo exam </b> to understand the interface before the final exam.</p>
												<p class="card-text">We <b>highly recommend </b> to take the demo before every exam to avoid problems during the exam.</p>
													<div>								
												<button type="button" class="btn btn-primary" onclick="window.open('https://studentzone-ngasce.nmims.edu/exam/student/viewModelQuestionForm', '_blank');">Run Demo Exam</button>				
											</div>
											</div>
										</div>
									</div>
						
									<div class="col-lg-4 mb-2">
										<div class="card h-100">
											<div class="card-body ">
												<h5 class="card-title">Step 03</h5>
												<p class="card-text fw-bold text-danger">Final Exam</p>
												<p class="card-text">After completing previous step you can start your final exam</p>
												<p class="card-text">You can still do a system compatibility check or run demo exam before starting the final exam</p>
												<p class="card-text  text-muted"><i class="	fas fa-calendar-alt"></i><b> Exam will Start on : ${ bookingInfo.examStartDateTime }</b></p>
												<p class="card-text  text-muted"><i class="	fas fa-calendar-alt"></i><b> Reporting time is : ${ bookingInfo.reporting_start_date_time }</b></p>
												<button type="button" onClick="startAssessment();" class="btn btn-primary">Start Final Exam</button>										
											</div>
										</div>
									</div>
								</div>
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
		
		<jsp:include page="../common/footer.jsp"/>
		
		<script> 
		function startAssessment() {
			var exam_start_time = new Date("${bookingInfo.accessStartDateTime }");
			var exam_end_time = new Date("${bookingInfo.accessEndDateTime }");
			var reporting_date_time = new Date("${bookingInfo.reporting_start_date_time }");
			var current_time = new Date(new Date().toLocaleString("en-US", {timeZone: "Asia/Kolkata"}));

			//console.log(exam_start_time);
			//console.log(exam_end_time);
			//console.log(current_time);
		if (current_time <= exam_end_time && current_time>= reporting_date_time) { 
				window.open("${joinLink}", "_blank");
				
			} else if (current_time < reporting_date_time){
				  alert("Exam Has Not Yet Started");
				  		
			}else if(current_time > exam_end_time ){
					alert("Exam Has Ended");
					
			}else{
					alert("Exam Details Not Found");
			}
	}
		
			function getExamCenterGoogleMapUrl() {
	
				let data = {
						"year" :${ bookingInfo.year } ,
						"month" :  "${ bookingInfo.month }"  ,
						"examCenterName": "${ bookingInfo.examCenterName }"  
					};
				
				$.ajax({
					type : "POST",
					url : '/exam/m/getExamCenterGoogleMapUrl',
					contentType : "application/json",
					data : JSON.stringify(data),
					dataType : "JSON",
					success : function(d) {
						window.open(d.googleMapUrl, "_blank");
					}
				});	
							
				
			}
		</script>
	</body>
 </html>