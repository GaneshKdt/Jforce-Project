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
<div class="calendarWrapper">
	<div class="panel panel-default bgColorNone">
		<div class="panel-heading" role="tab" id="">
			<h4 class="panel-title">UPCOMING EXAMS</h4>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<!-- 
                    <li><a href="#upcoming" aria-controls="upcoming" role="tab" data-toggle="tab" class="active">UPCOMING</a></li>
                    -->
				<!-- <li class="borderRight"><a href="#week" aria-controls="week" role="tab" data-toggle="tab">THIS WEEK</a></li> -->
				<li><a href="/acads/student/viewStudentTimeTable"><b>SEE ALL</b></a></li>
				<li><a class="panel-toggler collapsed" role="button" data-toggle="collapse" href="#collapseOne" aria-expanded="true"></a></li>
			</ul>
			<div class="clearfix"></div>
		</div>
		<c:choose>
			<c:when test="${ upcomingExams.size() == 0 }">
				<div id="collapseOne"
					class="panel-collapse collapse academic-schedule courses-panel-collapse"
					role="tabpanel">
					<div class="panel-body bgColorNone">
						<div class="no-data-wrapper">
							<p class="no-data">
								<span class="fa-regular fa-calendar-days"></span>No Upcoming Exams
							</p>
						</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div id="collapseOne" class="panel-collapse collapse in academic-schedule courses-panel-collapse" role="tabpanel">
					<div class="panel-body bgColorNone" style="border: none">
						<!-- Tab panes -->
						<div class="tab-content">
							<div role="tabpanel" class="tab-pane active" id="upcoming">
								<div class="row data-content">
									<div class="col-md-12 p-closed">
										<div class="no-data-wrapper">
											<p class="no-data">
												<span class="fa-regular fa-calendar-days"></span> Upcoming Exams
											</p>
										</div>
									</div>
								    <c:forEach items="${ upcomingExams }" var="exam" varStatus="loop">
								    	<fmt:parseDate value="${exam.examDate} ${exam.examTime}" pattern="yyyy-MM-dd HH:mm" var="examStartDateTime" />
		 								<fmt:parseDate value="${exam.examDate} ${exam.examEndTime}" pattern="yyyy-MM-dd HH:mm" var="examEndDateTime" />
									    <jsp:useBean id="now" class="java.util.Date"/>
									    <c:set var = "active" scope = "session" value = "${ now > examStartDateTime && now < examEndDateTime }"/>
									    
							    		<c:set var = "cardClass" value = "blue-cal"/>
								    	<c:if test="${ loop.index%2 == 0 }">
								    		<c:set var = "cardClass" value = "red-cal"/>
								    	</c:if>
								    	<c:if test="${ active }">
								    		<c:set var = "cardClass" value = "${ cardClass } active"/>
								    	</c:if>
									    <c:if test="${ loop.index < 4 }">
									    
									   		<div onClick="redirectToExam(this)" data-exam-date="${ exam.examDate }" data-subject="${ exam.subject }" data-month="${ exam.month }" data-year="${ exam.year }">
									    
											<%-- <div  data-exam-date="${ exam.examDate }" data-subject="${ exam.subject }" data-month="${ exam.month }" data-year="${ exam.year }"> --%>
												<div class="col-md-6 col-lg-3 ">
													<div class="sz-calnr cal-default ${cardClass}">
													
														<c:if test="${ active }">
															<span class='active-blink'>Live</span>
														</c:if>
														<div class="sz-date">
															<p style="font-size: 15px;">
																<strong>${ exam.subject }</strong>
															</p>
														</div>
														<div class="sz-time">
															<p style="font-size: 18px !important;">${ examStartDateTime }
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
															</p>-->
														</div>
														<div class="clearfix"></div>
													</div>
												</div>
											</div>
										</c:if>
									    
								    </c:forEach>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
</div>
</c:if>
<script>
	function redirectToExam(element) {
		var subject = encodeURIComponent(element.getAttribute("data-subject"));
		var month = element.getAttribute("data-month");
		var year = element.getAttribute("data-year");


		//commented below logic added default value show join link = true  
		/* var todaysExam = false;
		var examDate = new Date(element.getAttribute("data-exam-date") + " 00:00:00 GMT+0530 (India Standard Time)");
		var currentTime = new Date(new Date().toLocaleString("en-US", {timeZone: "Asia/Kolkata"}));

		if(examDate.setHours(0,0,0,0) == currentTime.setHours(0,0,0,0)) {
			todaysExam = true;
		} */
		
		window.open('/exam/student/viewAssessmentDetails?subject=' + subject + '&month=' + month + '&year=' + year + '&showJoinLink=true');
	}
</script>
