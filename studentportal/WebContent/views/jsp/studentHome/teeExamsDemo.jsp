<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.ExamBookingTransactionStudentPortalBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<style>
	.academic-schedule .panel-body .sz-calnr.active p {
	    color: #d2232a !important;
	}
	
	.active-blink {
		background-color: #d2232a !important;
		color: white !important;
		padding: 2px 10px;
		border-radius: 10px;
		float: right;
		font-size: smaller;
		animation: blinker 1s linear infinite;
	}
	.sz-calnr.cal-default {
		position: relative;
    	cursor: pointer;
	}
	@keyframes blinker {
		50% {
			opacity: 0;
		}
	}
</style> 
<c:if test="${ upcomingExams.size() != 0 }">
<div class="calendarWrapper mt-md-5 mt-lg-0 mt-0">

<div class="d-flex align-items-center text-wrap ">
		<span class="fw-bold me-3"><small class="fs-5">UPCOMING EXAMS</small></span>
		<div class="ms-auto">
			<a href="/acads/student/viewStudentTimeTable"class=" me-1 text-dark"><small class="text-nowrap">SEE ALL</small></a> 
			<a type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne" class="text-muted"
				role="button" aria-expanded="true" aria-controls="collapseOne"
				id="collapseCard"> <i class="fa-solid fa-square-minus"></i></a>
		</div>
	</div>
		<c:choose>
			<c:when test="${ upcomingExams.size() == 0 }">
				<div id="collapseOne" class="collapse show"
					<div class="card card-body text-center ">
								<h6><i class="fa-regular fa-calendar-days"></i><small> No Upcoming Exams</small></h6>
						</div>
					</div>
			</c:when>
			<c:otherwise>
				<div id="collapseOne" class="collapse">
						<!-- Tab panes -->
						<div class="card card-body text-center">
						<h6><i class="fa-regular fa-calendar-days"></i><small> ${ upcomingExams.size()} Upcoming Exam</small></h6>
								
						</div>
					</div>
					<div id="collapseOne" class="collapse show ">
						<div class="row row-cols-lg-4 ">
						
								    <c:forEach items="${ upcomingExams }" var="exam" varStatus="loop">								
			
								    	<fmt:parseDate value="${exam.examDate} ${exam.examTime}" pattern="yyyy-MM-dd HH:mm" var="examStartDateTime" />
		 								<fmt:parseDate value="${exam.examDate} ${exam.examEndTime}" pattern="yyyy-MM-dd HH:mm" var="examEndDateTime" />
		 								<fmt:parseDate value="${exam.examDate} ${exam.examReportingTime}" pattern="yyyy-MM-dd HH:mm" var="examReportingDateTime" />
									    <jsp:useBean id="now" class="java.util.Date"/>
									    <c:set var = "active" scope = "session" value = "${ now > examReportingDateTime && now < examEndDateTime }"/>
									    
							    		<c:set var = "cardClass" value = "blue-cal"/>
								    	<c:if test="${ loop.index%2 == 0 }">
								    		<c:set var = "cardClass" value = "red-cal"/>
								    	</c:if>
								    	<c:if test="${ active }">
								    		<c:set var = "cardClass" value = "${ cardClass } active"/>
								    	</c:if>
									    <c:if test="${ loop.index < 4 }">
									     <div class="col-lg-3">
								   			 <div class="card  card-body h-100 ">
									   		<div id="redirectToExam"    data-exam-date="${ exam.examDate }" data-subject="${ exam.subject }" data-month="${ exam.month }" data-year="${ exam.year }">
									    
											<%-- <div  data-exam-date="${ exam.examDate }" data-subject="${ exam.subject }" data-month="${ exam.month }" data-year="${ exam.year }"> --%>
									
													<div class="sz-calnr cal-default ${cardClass}">
													
														<c:if test="${ active }">
															<span class='active-blink'>Live</span>
														</c:if>
														<div class="sz-date">
															<p  class=" card-text fs-6 mb-2">
																<strong>${ exam.subject }</strong>
															</p>
														</div>
														<div class="sz-time">
															<p  class=" card-text fs-6">${ examStartDateTime }
															</p>
															<!-- <button type="button">Join Now</button> -->
														</div>
														<div class="clearfix"></div>
														<div class="sz-calndr-info">
															<p style="color: #a2a2a7 !important;">${ exam.examCenterName }</p>
															<!--  <p class="cal-name" style="float: right; padding-right: 10px; font-size: 15px;">
													    		<c:set var = "cardText" value = "View Exam Details"/>
														    	<c:if test="${ active }">
														    		<c:set var = "cardText" value = "Start Exam"/>
														    	</c:if>
														    	${ cardText }
														    	${examReportingDateTime}
															</p>-->
														</div>
														<div class="clearfix"></div>
													</div>
												
											</div>
											 </div>									  
									     </div>
									</c:if>					   
								    </c:forEach>
							</div>
					
						</div>
			</c:otherwise>
		</c:choose>

</div>
</c:if>
<!-- <script>
	function redirectToExam(element) {
		var subject = encodeURIComponent(element.getAttribute("data-subject"));
		var month = element.getAttribute("data-month");
		var year = element.getAttribute("data-year");

		var todaysExam = false;
		var examDate = new Date(element.getAttribute("data-exam-date") + " 00:00:00 GMT+0530 (India Standard Time)");
		var currentTime = new Date(new Date().toLocaleString("en-US", {timeZone: "Asia/Kolkata"}));

		if(examDate.setHours(0,0,0,0) == currentTime.setHours(0,0,0,0)) {
			todaysExam = true;
		}
		
		window.open('/exam/student/viewAssessmentDetails?subject=' + subject + '&month=' + month + '&year=' + year + '&showJoinLink=true');
	}
</script> -->
<script type="text/javascript" src="${pageContext.request.contextPath }/assets/js/teeExamsDemo.js"></script>

