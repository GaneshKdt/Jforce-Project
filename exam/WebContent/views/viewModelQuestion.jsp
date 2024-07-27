<!DOCTYPE html>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.AssignmentFileBean"%>
<%@page import="java.util.Calendar"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>

<html lang="en">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" var="server_path" />
	<jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title" /></jsp:include>

	<%
		StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
   		ArrayList<String> subjects = (ArrayList<String>)request.getSession().getAttribute("studentCourses");
 	 %>
  
 	 <head>
        <script src="https://d1h28kdwpyiu28.cloudfront.net/assets/js/bootstrapBundle.js" ></script>
         <link href="https://d1h28kdwpyiu28.cloudfront.net/assets/js/bootstrap5.css" rel="stylesheet" />
	</head>
   	 <body>
		<%@ include file="common/headerDemo.jsp"%>
		<div class="sz-main-content-wrapper">
			<jsp:include page="common/breadcrum.jsp">
				<jsp:param value="Student Zone;Demo Exam" name="breadcrumItems" />
			</jsp:include>

			<div class="sz-main-content menu-closed" >
				<div class="sz-main-content-inner ">
				    <div id="sticky-sidebar">  
						<jsp:include page="common/left-sidebar.jsp">
							<jsp:param value="Demo Exams" name="activeMenu" />
						</jsp:include>
                    </div>
					<div class="sz-content-wrapper dashBoard demoExam" >
						<%@ include file="common/studentInfoBar.jsp"%>
						<div class="sz-content" >
							<h2 class="red text-capitalize"><i class="fa-solid fa-pen-to-square"></i>Demo Exam</h2>
							<div class="clearfix"></div>
						</div>
					 <!--  <div class="alert alert-success alert-dismissible">
								Please <a href"https://tests.mettl.com/system-check?i=db696a8e#/systemCheck">Click Here</a> to take the Mettl System Compatibility test before attempting to take the demo exam.
							</div> 
							<div class="panel-content-wrapper">
								<p>Demo Exam papers below are made available for reference purpose only, 
								there may be subjects not applicable to your Semester. 
								These sample papers will help you understand how MCQ&#39;s, 
								descriptive questions are prepared.
								</p>
								
								<div class="table-responsive" id="detail">
									<table class="table table-striped" style="font-size: 12px">
										<thead>
											<tr>
											 <th>Sr No</th> 
												<th>Subject</th>
												<th>Take Exam</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="demoExam" items="${demoExamListAttempt}">
											<tr>
												<td>
													${demoExam.subject}
												</td>
												<td>
													<form action="startDemoStart" method="post">
														<input type="hidden" name="id" value="${ demoExam.id }" />
														<input type="hidden" name="key" value="${ demoExam.key }"/>
														<input type="hidden" name="link" value="${ demoExam.link }"/>
														<input type="hidden" name="subject" value="${ demoExam.subject }"/>
														<button type="submit" class="btn btn-primary">
															Click Here
														</button>
													</form>
												</td>
											</tr>
											</c:forEach>
											
											
										</tbody>
									</table>
								</div>
								
							</div> -->
							<div class="container-fluid ">
								<div  class="panel-content-wrapper " >										
										<h4 class="mb-2"  style="color:#d2232a"><b>Instruction</b></h4>								
										<p style="font-size: 14px"><b>Before you start the exam,</b> please ensure  that you fulfill the necessary requirement for the assessment.</p>							
 									<ul>	
 										<p class="mb-1 fs-5 fw-normal text-break"> <li>To ensure your system is ready for exam, you have necessary permission, and a fully functioning camera/mic, perform the <b>System Compatibility </b>Check in <b>Step 01</b>.</li></p>
 										<p class="mb-1 fs-5 fw-normal text-break"> <li>It is mandatory to perform the <b>Demo Exam</b> as mentioned in <b>Step 02</b> to simulate the exam experience and to make  sure you face no challenges during the real exam.</li></p>
 									</ul>
 										<br />
 										<p class="mb-3 fs-6 fw-normal text-break"><b>Exam Rules : </b>You are  being <b>monitored at all times,</b> make sure there are no deviations from the below mentioned rules</p>
 									<ul>
 										<p class="mb-1 fs-5 fw-normal text-break"> <li>Make sure you have a <b>clean clutter-free environment,</b>It is recommended to have a blank wall in the background</li></p>
 									 	<p class="mb-1 fs-5 fw-normal text-break"> <li>There are <b>no additional people</b> allowed in the exam room apart from the test-taker.</li></p>
 										<p class="mb-1 fs-5 fw-normal text-break"> <li>You are <b>not allowed to use your mobile phones </b> or any external reference material(book/notes) during the exam.</li></p>
 										<p class="mb-1 fs-5 fw-normal text-break"> <li>You are <b>not allowed to navigate away from  the test screen</b> to other browser/references material on the system.</li></p>
 										<p class="mb-1 fs-5 fw-normal text-break"> <li>At no point in the time should the microphone or camera to be turned off and<b> no headphones are allowed.</b></li></p>
 										<p class="mb-1 fs-5 fw-normal text-break"> <li><b>No food or beverage </b>except water are  allowed during examination</li></p>
 										<p class="mb-1 fs-5 fw-normal text-break"> <li>You are<b> not allowed any bio breaks, </b>In case any special exception are needed please submit the necessary document 7 days before the exam date to the unviersity for approvals</p>
								</ul>
								</div>
								<h4 style="font-size: 14px"><b>TEST PROCEDURE</b></h4>													
									<div  class="row ">						
											<div class="col-lg-6 mb-2">
												<div class="card h-100 ">
													<div class="card-body ">
														<h5 class="card-title">Step 01</h5>
														<p class="card-text fw-bold text-danger">System Compatibility Test</p>
														<p class="card-text">Make Sure your <b>system is compatible</b>, and all the required permission are avaliable before starting the final exam.</p>
														<p class="card-text">You need to run the <b>System Compatibility check</b> every time before the final exam.</p>
														
														<button type="button" class="btn btn-outline-danger mx-3" onclick="window.open('https://tests.mettl.com/system-check?i=db696a8e#/systemCheck', '_blank');">Run System Compatibility</button>
													</div>
												</div>
											</div>
								
								<div class="col-lg-6  mb-2 ">
									<div class="card ">
										<div class="card-body h-100">
											<h5 class="card-title">Step 02</h5>
											<p class="card-text fw-bold text-danger">Demo Exam</h5>
											<p class="card-text">Please <b>start the demo exam </b> to understand the interface before the final exam.</p>
											<p class="card-text">We <b>highly recommend </b> to take the demo before every exam to avoid problems during the exam.</p>
																	
											<c:forEach var="demoExam" items="${demoExamListAttempt}">
													<form action="startDemoStart" method="post">
															<input type="hidden" name="id" value="${ demoExam.id }" />
															<input type="hidden" name="key" value="${ demoExam.key }"/>
															<input type="hidden" name="link" value="${ demoExam.link }"/>
															<input type="hidden" name="subject" value="${ demoExam.subject }"/>
															<h6>${ demoExam.subject }</h6>
															<button type="submit" class="btn btn-danger mx-3" >Run Demo Exam</button> 														
														</form>
											</c:forEach>	
											<c:if test="${empty demoExamListAttempt}">									
												<h4 class="card-text fw-bold text-danger mt-5 pt-4 ">Max Attempt Reached</h4>
											</c:if>	
										</div>
									</div>
								</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		
		<jsp:include page="common/footer.jsp" />

    </body>


</html>