<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>

<meta charset="ISO-8859-1">
<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Exam Info" name="title" />
</jsp:include>

		<style>
			.student-info-list li{
				color: #404041 !important;
			}
		</style>
<body>



	<div class="sz-main-content-wrapper complete-profile-warpper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Start Exam"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="container">
					<div class="panel-content-wrapper">
						<%@ include file="../common/messages.jsp"%>
							<c:if test="${mettlExamUpcomingBean != null }">
							<fmt:parseDate value="${mettlExamUpcomingBean.accessStartDateTime }" pattern="yyyy-MM-dd HH:mm" var="accessStartDateTime" />
 								<fmt:parseDate value="${mettlExamUpcomingBean.accessEndDateTime }" pattern="yyyy-MM-dd HH:mm" var="accessEndDateTime" />
 								<fmt:parseDate value="${mettlExamUpcomingBean.reporting_start_date_time }" pattern="yyyy-MM-dd HH:mm" var="reportingStartDateTime" />
 								<fmt:parseDate value="${mettlExamUpcomingBean.examEndDateTime }" pattern="yyyy-MM-dd HH:mm" var="examEndDateTime" />
 								
							<jsp:useBean id="now" class="java.util.Date"/>
								
								
								<div class="panel-content-wrapper">
									<div class="table-responsive">
										<table class="table table-striped table-hover" style="font-size:12px">
											<tbody>
												
												<tr>
													<th>Subject</th>
													<td>${ mettlExamUpcomingBean.subject }</td>
												</tr>
												<tr>
													<th>Start Time</th>
													<td>${ mettlExamUpcomingBean.examStartDateTime }</td>
												</tr>
												
												<tr>
													<th>Reporting Time</th>
													<td>${ mettlExamUpcomingBean.reporting_start_date_time }</td>
												</tr>
											
												<tr>
													<th>Check System Compatibility</th>
													<td><a href="https://tests.mettl.com/system-check?i=db696a8e#/systemCheck">Click Here To Start The System Compatibility Check</a></td>
												</tr>
											</tbody>
										</table>
									</div>
									<div class="row">
										<div class="col-md-12">	
											<c:choose>
												<c:when test="${ reportingStartDateTime < now && accessEndDateTime > now }">
													<button type="button" onClick="startAssessmentDirect();" class="btn btn-primary" style="float: right;">
														Click Here To Start The Final Exam
													</button>
												</c:when>
												<c:otherwise>
													  <button type="button" onClick="startAssessment();" class="btn btn-primary" style="float: right;">
														Click Here To Go To The Waiting Room (Link will be Active at ${mettlExamUpcomingBean.reporting_start_date_time })
													</button>
												</c:otherwise>
											</c:choose>
										</div>
									</div>
									<div class="clearfix"></div>
								</div>
								<div class="clearfix"></div>
								<div class="panel-content-wrapper">
									<div>
										<h2 class="">Instructions</h2>
										<div class="clearfix"></div>
										<ul>
											<!-- <li><p>The access time is 30 minutes from the start time to start the test on Mettl.</p></li> -->
											<li><p>The Reporting End Time is 30 minutes from the exam start time.</p></li>
											<li><p>The duration of the exam will be 2:30 hours from the start time.</p></li>
										</ul>
									</div>
								</div>
							
									<c:choose>
												<c:when test="${ examEndDateTime > now }">
													
												</c:when>
												 <c:otherwise>
													<a class="btn btn-primary"
									title="goHome"  onClick="goToPortal();" >Go To Home</a>
												</c:otherwise>
											</c:choose>
								<div class="row">
					<a href="/logout" class="btn btn-primary"
									title="logout">Logout</a>
						</div>
							</c:if>
						
				
					</div>

				</div>


			</div>
		</div>



<script>
		function startAssessment() {
				var exam_start_time = new Date("${mettlExamUpcomingBean.accessStartDateTime }");
				var exam_end_time = new Date("${mettlExamUpcomingBean.accessEndDateTime }");
				var reporting_start_time = new Date("${mettlExamUpcomingBean.reporting_start_date_time }");
				var current_time = new Date(new Date().toLocaleString("en-US", {timeZone: "Asia/Kolkata"}));

				//console.log(exam_start_time)
				//console.log(exam_end_time)
				//console.log(current_time)
			if (current_time.getTime() <= exam_end_time.getTime() 
                && current_time.getTime() >= reporting_start_time.getTime()) { 
					window.open("${joinLink}", "_blank");
				} else{
					  alert("Exam will start at "  + "${mettlExamUpcomingBean.reporting_start_date_time }");		
				}
		}
		
		function startAssessmentDirect(){
			//console.log("startAssessmentDirect");
			window.open("${joinLink}", "_blank");
		}

		function goToPortal() {
			
			//alert("You will be able to access the portal in an hour.");
					window.location = "/studentportal/skipToHome";			
						}
						
	</script>
</body>
</html>