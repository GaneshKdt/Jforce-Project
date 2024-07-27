<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Person"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Create Online Test" name="title" />
</jsp:include>


<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">

<link data-require="sweet-alert@*" data-semver="0.4.2" rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" />



<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.min.css">
<script src="https://code.jquery.com/jquery-3.3.1.js"
	integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
	crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>
<style>
hr{
	border-top: 1px solid #bbbbbb82 !important; 
}  
</style> 


<body>

	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<!-- breadcrums as per add/edit test start -->
		<c:if test="${test.id eq null}">
			<c:set var = "duration"  value = "30"/><!-- duration by default 30 on addtest -->
			<c:set var = "testName"  value = "${module.subjectCode}-IA${module.sessionModuleNo }-${module.topic}-${module.batchName}"/>  
			<c:set var = "maxScore"  value = "10"/>
			<c:set var = "maxQuestnToShow"  value = "6"/>
			<c:set var = "checked"  value = "checked"/>
			<c:set var = "facultyId"  value = "${module.facultyId}"/>
			<c:set var = "facultyName"  value = "${module.facultyName}"/>
			<c:set var = "acadYear"  value = "${module.acadYear}"/>
			<c:set var = "acadMonth"  value = "${module.acadMonth}"/>
			<c:set var = "year"  value = "${module.examYear}"/>
			<c:set var = "month"  value = "${module.examMonth}"/> 
			<c:set var = "subject"  value = "${module.subject}"/>
			<c:set var = "consumerTypeIdFormValue"  value = "${module.consumerTypeIdFormValue}"/>
			<c:set var = "programStructureIdFormValue"  value = "${module.programStructureIdFormValue}"/>
			<c:set var = "programIdFormValue"  value = "${module.programIdFormValue}"/>
			<c:set var = "date"  value = "${module.date}"/>  
			<c:set var = "testType"  value = "Test"/>
			 
			<c:set var = "proctoringEnabled"  value = "Y"/> 
			<c:set var = "showCalculator"  value = "Y"/>
			
			<div class="sz-breadcrumb-wrapper">
				<div class="container-fluid">
					<ul class="sz-breadcrumbs">
						<li><a href="/exam/">Exam</a></li>
						<li><a href="/exam/viewAllTests">Tests</a></li>
						<li><a href="#">Add Test</a></li>

					</ul>
					<ul class="sz-social-icons">
						<li><a href="https://www.facebook.com/NMIMSSCE"
							class="icon-facebook" target="_blank"></a></li>
						<li><a href="https://twitter.com/NMIMS_SCE"
							class="icon-twitter" target="_blank"></a></li>
						<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->

					</ul>
				</div>
			</div>
		</c:if>
   
		<c:if test="${test.id ne null}">
		    <c:set var = "duration"  value = "${test.duration}"/>
		    <c:set var = "testName"  value = "${test.testName}"/>
		    <c:set var = "maxScore"  value = "${test.maxScore}"/>
		    <c:set var = "maxQuestnToShow"  value = "${test.maxQuestnToShow}"/> 
		    <c:set var = "checked"  value = ""/>
		    <c:set var = "facultyId"  value = "${test.facultyId}"/>
			<c:set var = "facultyName"  value = "${test.facultyName}"/>
			<c:set var = "acadYear"  value = "${test.acadYear}"/>
			<c:set var = "acadMonth"  value = "${test.acadMonth}"/>
			<c:set var = "year"  value = "${test.year}"/>
			<c:set var = "month"  value = "${test.month}"/>   
			<c:set var = "subject"  value = "${test.subject}"/> 
			<c:set var = "consumerTypeIdFormValue"  value = "${test.consumerTypeIdFormValue}"/>
			<c:set var = "programStructureIdFormValue"  value = "${test.programStructureIdFormValue}"/>
			<c:set var = "programIdFormValue"  value = "${test.programIdFormValue}"/>
			<c:set var = "date"  value = "${test.startDate}"/>  
			<c:set var = "testType"  value = "${test.testType}"/>  
			
			<c:set var = "proctoringEnabled"  value = "${test.proctoringEnabled}"/> 
			<c:set var = "showCalculator"  value = "${test.showCalculator}"/>
			<div class="sz-breadcrumb-wrapper">
				<div class="container-fluid">
					<ul class="sz-breadcrumbs">
						<li><a href="/exam/">Exam</a></li>
						<li><a href="/exam/viewAllTests">Tests</a></li>
						<li><a href="/exam/viewTestDetails?id=${test.id}">Test
								Details</a></li>
						<li><a href="#">Edit Test</a></li>

					</ul>
					<ul class="sz-social-icons">
						<li><a href="https://www.facebook.com/NMIMSSCE"
							class="icon-facebook" target="_blank"></a></li>
						<li><a href="https://twitter.com/NMIMS_SCE"
							class="icon-twitter" target="_blank"></a></li>
						<!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->

					</ul>
				</div>
			</div>
		</c:if>
		<!-- breadcrums as per add/edit test start -->

		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<c:if test="${test.id eq null}">
							<h2 class="red text-capitalize">Add Online Test</h2>
						</c:if>

						<c:if test="${test.id ne null}">
							<h2 class="red text-capitalize">
								Edit Online Test : ${test.testName} &nbsp;&nbsp; <a
									href="/exam/viewTestDetails?id=${test.id}"> <i
									class="fa-solid fa-circle-info" style="font-size: 24px"></i>
								</a>
							</h2>
						</c:if>

						<div class="clearfix"></div>
						<div class="" style="min-height: 450px;">
							<%@ include file="../adminCommon/messages.jsp"%>

							<!-- Code For Page Goes in Here Start -->

							<form:form id="addTest" action="addTest" method="post"
								modelAttribute="test">
								<!-- code for configuration start -->


								<!-- code for configuration end -->

								 
								<div class="panel-content-wrapper">
								<c:if test="${test.id ne null}">
										<!-- Static Select for editTest start -->

										<c:choose>

											<c:when test="${test.applicableType eq 'module'}">

												<!-- For Module Start -->

												<div class="row" style="display:none;">
													<div class="col-md-3 col-sm-3 col-xs-4 column"> 
														<form:label path="moduleBatchId" for="moduleBatchId">Select Batch</form:label>
													</div>
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<div id="moduleBatchIdDiv">
																<form:select id="moduleBatchId" path="moduleBatchId"
																	type="text" placeholder="moduleBatchId"
																	class="form-control" required="required"
																	itemValue="${test.batchId}">
																	<form:option value="${test.batchId}">${test.name}</form:option>
																</form:select>

															</div>
														</div>
													</div>
												</div> 


												<div class="row" style="display:none;">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<div id="referenceIdDiv"></div>
														</div>
													</div>
												</div>

												<div class="row" style="display:none;">
													<div class="col-md-3 col-sm-3 col-xs-4 column"> 
															<form:label path="subject" for="subject">Select Subject</form:label>
													</div> 
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div id="subjectSelectContainerDiv" class="form-group">
															<form:select id="subject" path="subject" type="text"
																placeholder="subject" class="form-control"
																required="required" itemValue="${subject}">
																<form:option value="${test.subject}">${test.subject}</form:option>
															</form:select>

														</div>
													</div>
												</div>

												<div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div id="subjectSelectContainerDivForOldConfig"
															class="form-group"></div>
													</div>
												</div>
												<div class="row" style="display:none;">
												<div class="col-md-3 col-sm-3 col-xs-4 column"> 
															<form:label path="referenceId" for="referenceId">Select Module</form:label> 
													</div> 
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div id="moduleReferenceIdDiv" class="form-group">
                                                        	<form:select id="referenceId" path="referenceId"
																type="text" placeholder="referenceId"
																class="form-control" required="required"
																itemValue="${test.referenceId}">
																<form:option value="${test.referenceId}">${test.referenceBatchOrModuleName}</form:option>
															</form:select>
														</div>
													</div>
												</div>
												   

												<!-- For Module End -->

											</c:when>

											<c:when test="${test.applicableType eq 'batch'}">

												<!-- For Batch Start -->

												<div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<div id="moduleBatchIdDiv"></div>
														</div>
													</div>
												</div>


												 <div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<div id="referenceIdDiv">

																<form:label path="referenceId" for="referenceId">Select Batch</form:label>
																<form:select id="referenceId" path="referenceId"
																	type="text" placeholder="referenceId"
																	class="form-control" required="required"
																	itemValue="${test.batchId}">
																	<form:option value="${test.batchId}">${test.referenceBatchOrModuleName}</form:option>
																</form:select>
															</div>
														</div>
													</div>
												</div>  

												<div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div id="subjectSelectContainerDiv" class="form-group">

															<form:label path="subject" for="subject">Select Subject</form:label>
															<form:select id="subject" path="subject" type="text"
																placeholder="subject" class="form-control"
																required="required" itemValue="${test.subject}">
																<form:option value="${test.subject}">${test.subject}</form:option>
															</form:select>

														</div>
													</div>
												</div>

												<div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div id="subjectSelectContainerDivForOldConfig"
															class="form-group"></div>
													</div>
												</div>
												<div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div id="moduleReferenceIdDiv" class="form-group"></div>
													</div>
												</div>
												<!-- For Batch End -->

											</c:when>
											<c:when test="${test.applicableType eq 'old'}">

												<!-- For old start -->

												<div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<div id="moduleBatchIdDiv"></div>
														</div>
													</div>
												</div>


												<div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<div id="referenceIdDiv"></div>
														</div>
													</div>
												</div>

												<div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div id="subjectSelectContainerDiv" class="form-group">

															<form:label path="subject" for="subject">Select Subject</form:label>
															<form:select id="subject" path="subject" type="text"
																placeholder="subject" class="form-control"
																required="required" itemValue="${test.subject}">
																<form:option value="${test.subject}">${test.subject}</form:option>
															</form:select>

														</div>
													</div>
												</div>

												<div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div id="subjectSelectContainerDivForOldConfig"
															class="form-group"></div>
													</div>
												</div>
												<div class="row">
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div id="moduleReferenceIdDiv" class="form-group"></div>
														<form:select id="subject" path="subject" type="text"
																placeholder="subject" class="form-control"
																required="required" itemValue="${test.subject}">
																<form:option value="${test.subject}">${test.subject}</form:option>
															</form:select> 
													</div>
												</div>

												<!-- For old end -->

											</c:when>
											<c:otherwise>
												<h1>Error in the test contact dev team.</h1>
											</c:otherwise>
										</c:choose>
										<!-- Static Select for editTest end -->
									</c:if>

									<c:if test="${test.id eq null}">
										<form:input  type="hidden" path="referenceId" value="${moduleId}" id="referenceId"/>
										<form:input  type="hidden" path="sessionPlanId" value="${sessionPlanId}" id="sessionPlanId"/>
									</c:if>
									<div class="row">
									<div class="col-md-3 col-sm-3 col-xs-4 column"> 
									<form:label path="testName" for="testName">Test Name</form:label>
									</div> 
										<div class="col-md-6 col-sm-8 col-xs-12 column">
											<form:hidden path="id" value="${test.id }" />
											<form:hidden path="year" value="${year }" />  
											<form:hidden path="month" value="${month }" /> 
											<form:hidden path="acadYear" value="${acadYear }" /> 
											<form:hidden path="acadMonth" value="${acadMonth }" />   
											<form:hidden path="applicableType" value="module" />  
											<form:hidden path="consumerTypeIdFormValue" value="${consumerTypeIdFormValue }" />
											<form:hidden path="programStructureIdFormValue" value="${programStructureIdFormValue }" />
											<form:hidden path="programIdFormValue" value="${programIdFormValue }" /> 
											<form:hidden path="subject" value="${subject }" /> 
											<div class="form-group">     
												<form:input id="testName" path="testName" type="text"
													placeholder="Test Name" class="form-control"
													required="required" value="${testName} " /> 
											</div> 
										</div>

									</div>
									<div class="row">
										<div class="col-md-3 col-sm-3 col-xs-4 column">
											<form:label path="facultyId" for="facultyId">Teaching Faculty</form:label>
										</div>   
										<div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="form-group" style="margin-bottom: 10px;">
												<!-- Add FacultyList Here -->
												<form:select id="facultyId" path="facultyId" type="text"
													placeholder="facultyId" class="form-control"
													required="required">
													<form:option value="${facultyId}" selected="selected">${facultyName} - ${facultyId}</form:option> 
													<c:forEach var="faculty" items="${facultyList}"
														varStatus="status">

														<form:option value="${faculty.facultyId}">${faculty.firstName} ${faculty.lastName} - ${faculty.facultyId}</form:option>
													</c:forEach>
												</form:select>

											</div>
										</div>

									</div>
									<hr>
									
									<div class="row">
									<div class="col-md-3 col-sm-3 col-xs-6 column">
											<label >Question Template :</label>
										</div>
										<c:if test="${test.id ne null}">
									    <div class="col-md-3 col-sm-3 col-xs-6 column">
									    <div class="row">
									    <div class="col-md-6">
									    <label >Template choosed :</label>
									    </div> 
									    <div class="col-md-6">
									    <c:forEach var="config" items="${test.testQuestionConfigBean}" > 
											   <p>${config.typeName } ${config.minNoOfQuestions }</p> 
										</c:forEach>
									    </div>
									    </div>
										</div>
										</c:if>
										<c:if test="${test.id ne null}">
										<div class="col-md-2 col-sm-3 col-xs-6 column">
											<label >Choose New Template :</label>
										</div>
										<div class="col-md-3 col-sm-6 col-xs-6 column">  
											<div class="form-group" >   
												<form:select id="questionTemplate" path="templateId" class="form-control"  >
												    <form:option value="" >Select</form:option>      
													<c:forEach var="templateType" items="${templateTypes}" >    
													 
												    <form:option marks="${templateType.questionMarks }" qns="${templateType.minNoOfQuestions }" type="${ templateType.testType}" dur="${ templateType.duration}"  value="${ templateType.templateId}">${ templateType.name}</form:option> 
												          
													</c:forEach>     
												</form:select>        
												<a href="createNewTemplateForm">Create a new Template</a>    
			              					</div>
	              						</div>
										</c:if>					
										<c:if test="${test.id eq null}">  
										<div class="col-md-3 col-sm-6 col-xs-6 column">  
											<div class="form-group" >   
												<form:select required="required" id="questionTemplate" path="templateId" class="form-control template"  >
												    <c:forEach var="templateType" items="${templateTypes}" >    
													 
												    <form:option marks="${templateType.questionMarks }" qns="${templateType.minNoOfQuestions }" type="${ templateType.testType}" dur="${ templateType.duration}"  value="${ templateType.templateId}">${ templateType.name}</form:option> 
												          
													</c:forEach>     
												</form:select>        
												<a href="createNewTemplateForm">Create a new Template</a>    
			              					</div>
	              						</div>
										</c:if>
										
									</div>  
									<div class="row">
										<div class="col-md-3 col-sm-3 col-xs-4 column"> 
											<form:label path="startDate" for="startDate">Start Date</form:label>
										
										</div>
									<div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="form-group">
												
												<div class='input-group date' id=''>
												
													<form:input path="startDate" id="addtest_startDate" class="sDate" 
														type="datetime-local" style="color:black;"
														 required="required" />
												  
													
													<%-- 
													<form:input id="startDate" path="startDate" type="datetimes"
														placeholder="Start Date" class="form-control"
														required="required" readonly="true" />
													<span class="input-group-addon"><span
														class="glyphicon glyphicon-calendar"></span> </span> --%>
												</div>

											</div>
										</div>
									</div>
									<div class="row">
									<div class="col-md-3 col-sm-3 col-xs-4 column"> 
										<form:label path="endDate" for="endDate">End Date</form:label>
									</div>	
										<div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="form-group">
												
												<div class='input-group date' id='   '>
													<form:input path="endDate" id="addtest_endDate" class="enDate" 
														type="datetime-local" style="color:black;"  
														 required="required" />
													 				
													 
													<%-- 
													<%-- <form:input id="endDate" path="endDate" type="datetime"
														placeholder="End Date" class="form-control"
														required="required" readonly="true" />
													<span class="input-group-addon"><span
														class="glyphicon glyphicon-calendar"></span> </span> --%>
												</div>

											</div>
										</div>

									</div>
									
									<div class="row">
										<div class="col-md-3 col-sm-3 col-xs-4 column">
											<form:label path="duration" for="duration">Duration in mins</form:label>
										</div>
										<div class="col-md-5 col-sm-7 col-xs-12 col-lg-6">
											<div class="form-group">
											<div class="row">
											<div class="col-lg-3  col-md-3 col-sm-3"> 
											<select class="form-control" id="day" required="required" >
											<option selected>0</option>
												<c:forEach begin='1' end='100' var='day'>
													<option>${day}</option>
												</c:forEach> 
											</select>
											</div>
											<div class="col-lg-1 col-md-1 col-sm-1">  
											<label>Day</label> 
											</div> 
											<div class="col-lg-3 col-md-3 col-sm-3"> 
											<select class="form-control" id="hours" required="required" >
																					
										      <c:forEach var = "i" begin = "0" end = "23">
												<option><c:out value = "${i}"/></option>
										      </c:forEach>
										      
											</select>
											</div>
											<div class="col-lg-1 col-md-1 col-sm-1">  
											<label>Hr</label> 
											</div> 
											<div class="col-lg-3  col-md-3 col-sm-3"> 
											
											<select  class="form-control" id="minutes" required="required" >
											<option selected>0</option>
												<c:forEach var="i" begin="1" end="59">
													<option>${i}</option>
												</c:forEach>
											</select> 
											</div>  
											<div class="col-lg-1 col-md-1 col-sm-1">    
											<label>Min</label>
											</div>
											</div>
											
											 <form:hidden id="duration" path="duration" min="1"
												required="required" value="${duration}" />
											</div> 
										</div> 
									</div>
																	     <div class="row">
										<div class="col-md-3 col-sm-3 col-xs-4 column">
											<form:label path="maxScore" for="maxScore">IA Type</form:label>
										</div>  
										<div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="form-group">
											<form:select  class="form-control testType" path="testType" required="required" >     
											<form:option value="Test">Test</form:option> 
											<form:option value="Assignment">Assignment</form:option>
											<form:option value="Project">Project</form:option>
											</form:select>        
											</div>
									</div>
									</div>     
									<hr>  
									<div class="row">
										<div class="col-md-3 col-sm-3 col-xs-4 column">
											<form:label path="maxScore" for="maxScore">Score Out of</form:label>
										</div>
										<div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="form-group">
											<form:select  class="form-control maxScore" placeholder="Score Out of" path="maxScore" required="required" >
											<form:option value="${maxScore}" selected="selected">${maxScore}</form:option>
											<form:option value="0">0</form:option> 
											<form:option value="10">10</form:option>
											<form:option value="12">12</form:option>
											<form:option value="15">15</form:option>
											<form:option value="15">15</form:option>
											<form:option value="20">20</form:option>
											<form:option value="25">25</form:option>
											<form:option value="30">30</form:option>
											<form:option value="35">35</form:option>
											<form:option value="40">40</form:option>
											<form:option value="45">45</form:option>
											<form:option value="50">50</form:option>
											<form:option value="60">60</form:option>
											<form:option value="70">70</form:option>
											<form:option value="80">80</form:option>
											<form:option value="90">90</form:option>
											<form:option value="100">100</form:option>
											</form:select> 
											</div>
										</div>
									</div>
									  								     
<%-- 									<div class="row">
										<div class="col-md-3 col-sm-3 col-xs-4 column">
											<form:label path="passScore" for="passScore">Pass Score</form:label>
										</div>
										<div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="form-group">
												<form:input id="passScore" path="passScore" type="number"
													placeholder="Pass Score" class="form-control"
													value="${test.passScore}" />
											</div>
										</div>
									</div>
									<hr>   --%>
									<div class="row">
									<div class="col-md-3 col-sm-3 col-xs-4 column">
										<form:label path="maxAttempt" for="maxAttempt">Maximum Attempts</form:label>
									</div>
										<div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="form-group">
												<form:input id="maxAttempt" min="1" path="maxAttempt"
													type="number" placeholder="Maximum Attempts"
													class="form-control" required="required"
													value="${test.maxAttempt}" />
											</div>
										</div>
									</div> 
									<div class="row">
									<div class="col-md-3 col-sm-3 col-xs-4 column">
										<form:label path="maxQuestnToShow" for="maxQuestnToShow">Maximum Questions to be Shown to students</form:label>
									</div>
										<div class="col-md-4  col-sm-6 col-xs-12 column"
											id="maxQuestions" style="">
											<div class="form-group"> 
												<form:input id="maxQuestnToShow" path="maxQuestnToShow"
													type="number" placeholder="Maximum Questions To Show"
													class="form-control" value="${maxQuestnToShow}" />
											</div>
										</div>
									</div>   
									<%-- <hr>   
									<div class="row">
										<div class="col-md-3 col-sm-3 col-xs-6 column">
											<form:label path="randomQuestion" for="randomQuestion">Random Question Required?</form:label>
										</div>
										<div class="col-md-6 col-sm-6 col-xs-6 column">
											<div class="slider round">  
												<form:radiobutton name="randomQuestion" id="Yes" value="Y"
													path="randomQuestion" required="required" checked="${checked}"/>
												Yes 
												<form:radiobutton name="randomQuestion" id="No" value="N" 
													path="randomQuestion" />
												No <br>

											</div>
										</div>
									</div> --%>
									<div class="row"></div>

									<div class="clearfix"></div>
									<div class="row">
										<%-- <div class="col-md-4 col-sm-6 col-xs-12 column"
											id="testQuestionWeightageReq" style="display: none">
											<div class="slider round">
												<form:label path="testQuestionWeightageReq"
													for="testQuestionWeightageReq">Weightage For Questions?</form:label>
												<br>
												<form:radiobutton name="randomQuestion" id="YesW" value="Y"
													path="testQuestionWeightageReq" />
												Yes <br>
												<form:radiobutton name="randomQuestion" id="NoW" value="N"
													path="testQuestionWeightageReq" />
												No <br>

											</div>
										</div> --%>

										<%-- <div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="slider round">
												<form:label path="allowAfterEndDate" for="allowAfterEndDate">Allow Submission after End date?</form:label>
												<br>
												<form:checkbox path="allowAfterEndDate" class="form-control"
													value="Y" data-toggle="toggle" data-on="Yes" data-off="No"
													data-onstyle="success" data-offstyle="danger"
													data-style="ios" data-size="mini" />
											</div>
										</div> --%>
										<%-- <div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="slider round">
												<form:label path="sendEmailAlert" for="sendEmailAlert">Send Email Alert for New Test?</form:label>
												<br>
												<form:checkbox path="sendEmailAlert" class="form-control"
													value="Y" data-toggle="toggle" data-on="Yes" data-off="No"
													data-onstyle="success" data-offstyle="danger"
													data-style="ios" data-size="mini" />
											</div>
										</div> --%>

									</div>

<%-- 									<div class="row">
										<div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="slider round">
												<form:label path="sendSmsAlert" for="sendSmsAlert">Send SMS Alert for New Test?</form:label>
												<br>
												<form:checkbox path="sendSmsAlert" class="form-control"
													value="Y" data-toggle="toggle" data-on="Yes" data-off="No"
													data-onstyle="success" data-offstyle="danger"
													data-style="ios" data-size="mini" />
											</div>
										</div>
										<div class="col-md-3 col-sm-3 col-xs-6 column">
											<form:label path="showResultsToStudents"
													for="showResultsToStudents">Show Results to Students immediately?</form:label>
										</div>
										<div class="col-md-4 col-sm-6 col-xs-6 column">  
										<div class="slider round1">
												<form:radiobutton  id="Yes" value="Y"
													path="showResultsToStudents" required="required" checked="${checked}"/>
												Yes 
												<form:radiobutton id="No" value="N"   
													path="showResultsToStudents" /> 
												No <br>

											</div>
											
										</div>
									</div> --%>

									<hr>
									<!-- Code for enable proctoring start -->
									<div class="row">
										<div class="col-md-3 col-sm-3 col-xs-4 column">
											<form:label path="proctoringEnabled" for="proctoringEnabled">Enable Proctoring</form:label>
										</div>  
										<div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="form-group">
											<form:select class="form-control" path="proctoringEnabled" required="required" >     
											<form:option value="${proctoringEnabled}" selected="selected" ><c:if test="${proctoringEnabled eq 'Y'}">Yes</c:if><c:if test="${proctoringEnabled eq 'N'}">No</c:if>
											</form:option> 
											<form:option value="Y">Yes</form:option> 
											<form:option value="N">No</form:option>
											</form:select>        
											</div>
										</div>
									</div>
									<!-- Code for enable proctoring end -->
									<hr>
									<!-- Code for showCalculator start -->
									<div class="row">
										<div class="col-md-3 col-sm-3 col-xs-4 column">
											<form:label path="showCalculator" for="showCalculator">Show Calculator</form:label>
										</div>  
										<div class="col-md-4 col-sm-6 col-xs-12 column">
											<div class="form-group">
											<form:select value="${showCalculator}"  class="form-control" path="showCalculator" required="required" >     
											<form:option value="${showCalculator}" selected="selected" >
												<c:if test="${showCalculator eq 'Y'}">Yes</c:if>
												<c:if test="${showCalculator eq 'N'}">No</c:if>
											</form:option> 
											
											<form:option value="Y">Yes</form:option> 
											<form:option value="N">No</form:option>
											</form:select>        
											</div>
										</div>
									</div>
									<!-- Code for enable proctoring end -->
									<hr>
									<div class="row">
										<div class="col-md-3 col-sm-3 col-xs-4 column">
												<form:label path="testDescription" for="testDescription">Test Description To show</form:label>
										</div>
										<div class="col-md-8">
											<form:textarea class="form-group" id="testDescription"
												path="testDescription" name="testDescription" rows="10"
												cols="80" type="textarea" placeholder="Test Description"
												value="${test.testDescription}" />
										</div>
									</div>

									<hr>  
									
									
									
									<div class="row">
										<div class="col-md-3 col-sm-3 col-xs-4 column"></div>
										<div class="col-sm-8 column">
											<div class="form-group">

												<c:choose>
													<c:when test="${testConfigIds eq null}">

														<button id="submit" class="btn btn-large btn-primary"
															onClick="return confirm('Are you sure?')"
															formaction="addTest">Save</button>

													</c:when>
													<c:otherwise>

														<form:hidden path="testConfigIds" value="${testConfigIds}" />
														<form:hidden path="testId" value="${test.testId}" />
														<form:hidden path="consumerProgramStructureId"
															value="${test.consumerProgramStructureId}" />
														<button id="submit" class="btn btn-large btn-primary submitbtn"
															onClick="return confirm('Are you sure? Saving these changes will delete old mappings.')"
															formaction="addTestForConfigEdit">Save</button>

													</c:otherwise>
												</c:choose>


												<button id="cancel" class="btn btn-danger"
													formaction="homepage" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
									</div>

								</div>

							</form:form>

							<!-- Code For Page Goes in Here End -->
						</div>

					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />


</body>
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.min.js"></script>
	  
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript"
	src="//cdn.ckeditor.com/4.8.0/standard-all/ckeditor.js"></script>
<script type="text/javascript"
	src="//cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML"></script>
<script src="https://unpkg.com/sweetalert/dist/sweetalert.min.js"></script>


<script
	src="https://cdnjs.cloudflare.com/ajax/libs/chosen/1.5.1/chosen.jquery.min.js"></script>

<script type="text/javascript">

$(".enDate").on('change', function(e) {
	var now = moment();
	var edate = moment.utc($(this).val(), "YYYY-MM-DDTHH:mm:ss");
	var sdate = moment.utc($(this).closest("form").find(".sDate").val(), "YYYY-MM-DDTHH:mm:ss");
	checkDatesValid(sdate,edate);
});
$(".sDate").on('change', function(e) {
	
	var sdate = moment.utc($(this).val(), "YYYY-MM-DDTHH:mm:ss");
	var edate = moment.utc($(this).closest("form").find(".enDate").val(), "YYYY-MM-DDTHH:mm:ss");
	checkDatesValid(sdate,edate);
});
function checkDatesValid(sdate,edate){
	var now = moment();
	var edit="${test.id}";
	if(edit==null || edit==""){//no validation for startdate on edit
		if(edate.isBefore(now) === true || sdate.isBefore(now) === true){ 
			alert("invalid start Date/end Date");     
			$(".enDate").val("");$(".sDate").val("");
		}
		var isafter = sdate.isAfter(edate);
		if(isafter === true){  
		    alert('End Date should be after Start Date');
		    $(".enDate").val("");$(".sDate").val("");  
		}
	}else{
		if(edate.isBefore(now) === true ){ 
			alert("invalid end Date");      
			$(".enDate").val("");
		}
		var isafter = sdate.isAfter(edate);
		if(isafter === true){  
		    alert('End Date should be after Start Date');
		    $(".enDate").val("");  
		}
	}
	
	
}
    
//alert(startDate);  
	CKEDITOR
			.replace(
					'testDescription',
					{
						extraPlugins : 'mathjax',
						mathJaxLib : 'https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.0/MathJax.js?config=TeX-AMS_HTML'
					});
</script>

<script>
	$(function() {

		$('#Yes').on('click', function() {
			//alert('slidYes');
			$('#maxQuestions').show();

			$("#maxQuestnToShow").prop('required', true);
			$("#testQuestionWeightageReq").prop('required', true);
			$('#testQuestionWeightageReq').show();

		});

		$('#No').on('click', function() {
			//alert('slidNo');
			$('#maxQuestions').hide();

			$("#maxQuestnToShow").prop('required', false);
			$('#testQuestionWeightageReq').hide();
			$('#testQuestionWeightageReq').prop('required', false);

		});

	});
</script>


<script>
	$(document)
			.ready(
					function() {

					});
	$("#datetimepicker1").on("dp.change", function(e) {

		validDateTimepicks();
	}).datetimepicker({
		//minDate:new Date(),
		useCurrent : false,
		format : 'YYYY-MM-DD HH:mm:ss'
	});

	$("#datetimepicker2").on("dp.change", function(e) {

		validDateTimepicks();
	}).datetimepicker({
		//minDate:new Date(),
		useCurrent : false,
		format : 'YYYY-MM-DD HH:mm:ss'
	});

	function validDateTimepicks() {
		if (($('#startDate').val() != '' && $('#startDate').val() != null)
				&& ($('#endDate').val() != '' && $('#endDate').val() != null)) {
			var fromDate = $('#startDate').val();
			var toDate = $('#endDate').val();
			var eDate = new Date(fromDate);
			var sDate = new Date(toDate);
			if (sDate < eDate) {
				alert("endate cannot be smaller than startDate");
				$('#startDate').val("");
				$('#endDate').val("");
			}
		}
	}
	
	
</script>

<script type="text/javascript">

$(document).ready (function(){
	
	try{

		$("#facultyId").chosen({
			width: '100%'
		});
	}catch(err){
		console.log("Got error, Refresh Page! ");
		console.log(err);
	}
	

    <c:if test="${testConfigIds ne null}">
	$("#addTestToDelete").submit(function(e){

        console.log("Clicked submit  +++++++++> ");
		event.preventDefault();

        swal({
            title: "Are you sure?",
            text: "Saving these changes will delete old mappings.",
            icon: "warning",
            buttons: [
              'No, cancel it!',
              'Yes, I am sure!'
            ],
            dangerMode: true,
          }).then(function(isConfirm) {
            if (isConfirm) {
            	alert('some test');
                $('#addTest').submit();
            } else {
	            event.preventDefault();
            }
          });
        
		});
    </c:if>

	<c:if test="${test.id ne null}">
		updateProgramNProgram();
		function updateProgramNProgram(){
			
			var programStructureIdFromDB = '${test.programStructureIdFormValue}';
			var programIdFromDB = '${test.programIdFormValue}';
			var subjectFromDB = '${test.subject}';

			console.log('${test.consumerTypeIdFormValue} '+${test.consumerTypeIdFormValue}
						+'\n programStructureIdFromDB : '+programStructureIdFromDB
						+'\n programIdFromDB : '+programIdFromDB+'\n subjectFromDB : '+subjectFromDB);
			let options = "<option>Loading... </option>";
			$('#programStructureId').html(options);
			$('#programId').html(options);
			$('.selectSubject').html(options);
			
			 
			var data = {
					id:$('#consumerTypeId').val()
			}
		console.log(data);
			
			$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "getDataByConsumerType",   
				data : JSON.stringify(data),
				success : function(data) {
					console.log("SUCCESS Program Structure: ", data.programStructureData);
					console.log("SUCCESS Program: ", data.programData);
					console.log("SUCCESS Subject: ", data.subjectsData);
					var programData = data.programData;
					var programStructureData = data.programStructureData;
					var subjectsData = data.subjectsData;
					
					options = "";
					let allOption = "";
					
					//Data Insert For Program List
					//Start
					for(let i=0;i < programData.length;i++){
						allOption = allOption + ""+ programData[i].id +",";
						if(programIdFromDB == programData[i].id){
							options = options + "<option value='" + programData[i].id + "' selected > " + programData[i].name + " </option>";
						}else{
							options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
						}
					}
					allOption = allOption.substring(0,allOption.length-1);
					
					console.log("==========> options\n" + options);
							if(programIdFromDB.split(',').length > 1){
								$('#programId').html(
										"<option value='"+ allOption +"' selected >All</option>" + options

								);
							}else{
								$('#programId').html(
										"<option value='"+ allOption +"'>All</option>" + options

								);
							}
					//End
					options = ""; 
					allOption = "";
					//Data Insert For Program Structure List
					//Start
					for(let i=0;i < programStructureData.length;i++){
						allOption = allOption + ""+ programStructureData[i].id +",";
						
						if(programStructureIdFromDB == programStructureData[i].id){
							options = options + "<option value='" + programStructureData[i].id + "' selected> " + programStructureData[i].name + " </option>";
						}else{
							options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
						}
					}
					allOption = allOption.substring(0,allOption.length-1);
					
					console.log("==========> options\n" + options);
							if(programStructureIdFromDB.split(',').length > 1){
								$('#programStructureId').html(
										"<option value='"+ allOption +"' selected >All</option>" + options

								);
							} else{
								$('#programStructureId').html(
										"<option value='"+ allOption +"'>All</option>" + options

								);
							}
					//End
					
					options = ""; 
					allOption = "";
					//Data Insert For Subjects List
					//Start
					for(let i=0;i < subjectsData.length;i++){
						if(subjectFromDB == subjectsData[i].name){
							options = options + "<option value='" + subjectsData[i].name + "' selected > " + subjectsData[i].name + " </option>";
						}else{
							options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
						}
					}
					
					
					console.log("==========> options\n" + options);
					$('.selectSubject').html(
							" <option selected disabled value=''> Select Subject </option> " + options
					);
					//End
					
					
					
				},
				error : function(e) {
					
					alert("Please Refresh The Page.")
					
					console.log("ERROR: ", e);
					display(e);
				}
			});
			
			
		}
	</c:if>
	


$(document).on('change', '#referenceIdDiv select', function(){

$('#subjectSelectContainerDiv').html(' <span></span> ');

let applicableType = $('#applicableType	').val();
console.log('IN referenceIdDiv on change event : applicableType = ');
console.log(applicableType);

var data = {
programId:$('.selectProgram').val(),
consumerTypeId:$('#consumerTypeId').val(),
programStructureId:$('#programStructureId').val(),
referenceId:this.value,
acadYear:$('#acadYear').val(),
acadMonth:$('#acadMonth').val()
};
console.log("data : ");
console.log(data);

if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === '' || data.referenceId === ''
|| data.acadYear === ''  || data.acadMonth === ''){
alert('Please Select All Consumer, Program, ProgramStructureId, batch/Module, acadMonth and acadYear');
return;
}

if( data.programId.split(',').length > 1 ||
data.consumerTypeId.split(',').length > 1 || 
data.programStructureId.split(',').length > 1 ){

document.getElementById("applicableType").options[3].selected = 'selected';

return;
}

if(applicableType === 'batch'){
$('#moduleBatchIdDiv').html(' <span></span> ');
//make api call to get batch details 

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getSubjectsByMastKeyAndBatch",   
data : JSON.stringify(data),
success : function(data) {

console.log("SUCCESS: dataForReferenceId :");
console.log(data.listOfStringData);


var dataForSubject = data.listOfStringData;

if( !dataForSubject ){
alert("Unable to get batches for selected program config, Kindly update selections and try again.");
return;
}

if( dataForSubject.length === 0){
alert("Unable to get batches for selected program config, Kindly update selections and try again.");
return;
}

var labelForSubject = "<label for='refrenceId'>Subject</label>";
var selectElementTopPart = "<select  id='subject'  name='subject'    placeholder='subject' type='text' class='form-control' required>" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForSubject.length;i++){

options = options + "<option value='" + dataForSubject[i] + "'> "
+ dataForSubject[i] + " </option>";
}


console.log("==========> options\n" + options);

var htmlToAppend = labelForSubject 
+ selectElementTopPart 
+ " <option disabled selected value=''> Select Subject </option> " + options 
+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#subjectSelectContainerDiv').html( htmlToAppend );
//End




},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});
//make api call to get batch details end

}else if(applicableType === 'module'){

if(data.subject === 'All'){
document.getElementById("applicableType").options[0].selected = 'selected';
alert("Please select a subject to make test module live. ")
return;
}
//make api call to get module details

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getModuleDataByMasterKeyConfig",   
data : JSON.stringify(data),
success : function(data) {

console.log("SUCCESS: dataForReferenceId :");
console.log(data.dataForReferenceId);


var dataForReferenceId = data.dataForReferenceId;

if( !dataForReferenceId && dataForReferenceId.length === 0){
alert("Unable to get Modules for selected program config, Kindly update selections and try again.");
return;
}

var labelForReferenceId = "<label for='refrenceId'>Applicable Modules</label>";
var selectElementTopPart = "<select  id='referenceId'  name='referenceId'    placeholder='referenceId' type='text' class='form-control' >" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForReferenceId.length;i++){

options = options + "<option value='" + dataForReferenceId[i].id + "'> " 
+ dataForReferenceId[i].topic + " </option>";
}


console.log("==========> options\n" + options);

var htmlToAppend = labelForReferenceId 
+ selectElementTopPart 
+ " <option disabled selected value=''> Select Module </option> " + options 
+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#referenceIdDiv').html( htmlToAppend );
//End




},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});


//make api call to get module details end

}else if(applicableType === 'old'){ 
return; 
} else{
$('#referenceIdDiv').html( "<span></span>" );
alert('Please select an option.');
}
});

///////////////////////////////////////////////


///////////////////////////////////////////////


$('#applicableType').on('change', function(){


$('#subjectSelectContainerDiv').html(' <span></span> ');
$('#referenceIdDiv').html(' <span></span> ');
$('#moduleBatchIdDiv').html(' <span></span> ');
$('#moduleReferenceIdDiv').html(' <span></span> ');
$('#subjectSelectContainerDivForOldConfig').html( ' <span></span> ' );

var data = {
programId:$('.selectProgram').val(),
consumerTypeId:$('#consumerTypeId').val(),
programStructureId:$('#programStructureId').val(),
subject:"All",
acadYear:$('#acadYear').val(),
acadMonth:$('#acadMonth').val()
};
console.log("data : ");
console.log(data);

if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === ''
|| data.acadYear === ''  || data.acadMonth === ''){
alert('Please Select All Consumer, Program, ProgramStructureId, subject, acadMonth and acadYear');
return;
}

if( data.programId.split(',').length > 1 ||
data.consumerTypeId.split(',').length > 1 || 
data.programStructureId.split(',').length > 1 ){

document.getElementById("applicableType").options[3].selected = 'selected';

}

let applicableType = this.value;
console.log('IN applicableType on change event : applicableType = ');
console.log(applicableType);

if(applicableType === 'batch'){
//make api call to get batch details 

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getBatchDataByMasterKeyConfig",   
data : JSON.stringify(data),
success : function(data) {
console.log("SUCCESS: dataForReferenceId :");
console.log(data.dataForReferenceId);


var dataForReferenceId = data.dataForReferenceId;

if( !dataForReferenceId){
alert("Unable to get batches for selected program config, Kindly update selections and try again.");

document.getElementById("applicableType").options[0].selected = 'selected';
return;
}
if(  dataForReferenceId.length === 0){
	alert("Unable to get batches for selected program config, Kindly update selections and try again.");

	document.getElementById("applicableType").options[0].selected = 'selected';
	return;
}

var labelForReferenceId = "<label for='refrenceId'>Applicable Batch</label>";
var selectElementTopPart = "<select  id='referenceId'  name='referenceId'    placeholder='referenceId' type='text' class='form-control' required>" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForReferenceId.length;i++){

options = options + "<option value='" + dataForReferenceId[i].id + "'> "
+ dataForReferenceId[i].name + " </option>";
}


console.log("==========> options\n" + options);

var htmlToAppend = labelForReferenceId 
+ selectElementTopPart 
+ " <option disabled selected value=''> Select Batch </option> " + options 
+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#referenceIdDiv').html( htmlToAppend );
//End

createSubjectSelectElement(data.listOfStringData);


},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});
//make api call to get batch details end

}else if(applicableType === 'module'){

//make api call to get module details

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getBatchDataByMasterKeyConfig",   
data : JSON.stringify(data),
success : function(data) {
console.log("SUCCESS: dataForReferenceId :");
console.log(data.dataForReferenceId);


var dataForReferenceId = data.dataForReferenceId;

if( !dataForReferenceId){
alert("Unable to get batches for selected program config, Kindly update selections and try again.");

document.getElementById("applicableType").options[0].selected = 'selected';
return;
}
if(  dataForReferenceId.length === 0){
	alert("Unable to get batches for selected program config, Kindly update selections and try again.");

	document.getElementById("applicableType").options[0].selected = 'selected';
	return;
}

var labelForReferenceId = "<label for='moduleBatchId'>Applicable Batch</label>";
var selectElementTopPart = "<select  id='moduleBatchId'  name='moduleBatchId'    placeholder='moduleBatchId' type='text' class='form-control' >" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForReferenceId.length;i++){

options = options + "<option value='" + dataForReferenceId[i].id + "'> "
+ dataForReferenceId[i].name + " </option>";
}


console.log("==========> options\n" + options);

var htmlToAppend = labelForReferenceId 
+ selectElementTopPart 
+ " <option disabled selected value=''> Select Batch </option> " + options 
+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#moduleBatchIdDiv').html( htmlToAppend );
//End

createSubjectSelectElement(data.listOfStringData);
createModulesSelect();

},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});


//make api call to get module details end

}else if(applicableType === 'old'){
// getSubjectsForOldConfig


$.ajax({
type : "POST",
contentType : "application/json",
url : "getDataByProgram",   
data : JSON.stringify(data),
success : function(data) {

console.log("SUCCESS: ", data.subjectsData);

var dataForSubject = data.subjectsData;




if( !dataForSubject){
alert("Unable to get batches for selected program config, Kindly update selections and try again.");

document.getElementById("applicableType").options[0].selected = 'selected';
return;
}
if(  dataForSubject.length === 0){
	alert("Unable to get batches for selected program config, Kindly update selections and try again.");

	document.getElementById("applicableType").options[0].selected = 'selected';
	return;
}

var labelForSubject = "<label for='subject'>Subject</label>";
var selectElementTopPart = "<select  id='subject'  name='subject'    placeholder='subject' type='text' class='form-control' required>" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForSubject.length;i++){

options = options + "<option value='" + dataForSubject[i].name + "'> " + dataForSubject[i].name + " </option>";

}


console.log("==========> options\n" + options);

var htmlToAppend = labelForSubject 
+ selectElementTopPart 
+ " <option disabled selected value=''> Select Subject </option> " + options 
+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#subjectSelectContainerDivForOldConfig').html( htmlToAppend );
//End


},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
display(e);
}
});

return; 
} else{
$('#referenceIdDiv').html( "<span></span>" );
alert('Please select an option.');
}
});

///////////////////////////////////////////////


///////////////////////////////////////////////
$(document).on('change', '#moduleBatchIdDiv select', function(){

$('#subjectSelectContainerDiv').html(' <span></span> ');
$('#referenceIdDiv').html(' <span></span> ');
$('#moduleReferenceIdDiv').html(' <span></span> ');

let applicableType = $('#applicableType	').val();
console.log('IN moduleBatchIdDiv on change event : applicableType = ');
console.log(applicableType);

var data = {
programId:$('.selectProgram').val(),
consumerTypeId:$('#consumerTypeId').val(),
programStructureId:$('#programStructureId').val(),
referenceId:this.value,
acadYear:$('#acadYear').val(),
acadMonth:$('#acadMonth').val()
};
console.log("data : ");
console.log(data);

if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === '' || applicableType === ''
|| data.acadYear === ''  || data.acadMonth === ''){
alert('Please Select All Consumer, Program, ProgramStructureId, batch/Module, acadMonth and acadYear');
return;
}

if( data.programId.split(',').length > 1 ||
data.consumerTypeId.split(',').length > 1 || 
data.programStructureId.split(',').length > 1 ){

document.getElementById("applicableType").options[3].selected = 'selected';

return;
}

if(applicableType === 'module'){
//make api call to get batch details 

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getSubjectsByMastKeyAndBatch",   
data : JSON.stringify(data),
success : function(data) {

console.log("SUCCESS: dataForReferenceId :");
console.log(data.listOfStringData);


var dataForSubject = data.listOfStringData;

if( !dataForSubject ){
alert("Unable to get batches for selected program config, Kindly update selections and try again.");
return;
}

if( dataForSubject.length === 0){
alert("Unable to get batches for selected program config, Kindly update selections and try again.");
return;
}

var labelForSubject = "<label for='refrenceId'>Subject</label>";
var selectElementTopPart = "<select  id='subject'  name='subject'    placeholder='subject' type='text' class='form-control' required>" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForSubject.length;i++){

options = options + "<option value='" + dataForSubject[i] + "'> "
+ dataForSubject[i] + " </option>";
}


console.log("==========> options\n" + options);

var htmlToAppend = labelForSubject 
+ selectElementTopPart 
+ " <option disabled selected value=''> Select Subject </option> " + options 
+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#subjectSelectContainerDiv').html( htmlToAppend );
//End




},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});
//make api call to get batch details end

}


});

///////////////////////////////////////////////


///////////////////////////////////////////////
$(document).on('change', '#subjectSelectContainerDiv select', function(){

$('#moduleReferenceIdDiv').html(' <span></span> ');

let applicableType = $('#applicableType	').val();
console.log('IN moduleBatchIdDiv on change event : applicableType = ');
console.log(applicableType);

var data = {
programId:$('.selectProgram').val(),
consumerTypeId:$('#consumerTypeId').val(),
programStructureId:$('#programStructureId').val(),
subject:this.value,
acadYear:$('#acadYear').val(),
acadMonth:$('#acadMonth').val(),
referenceId:$('#moduleBatchId').val()
};
console.log("data : ");
console.log(data);

if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === '' || data.referenceId === '' || data.subject === ''
|| data.acadYear === ''  || data.acadMonth === ''){
alert('Please Select All Consumer, Program, ProgramStructureId, batch/Module, acadMonth and acadYear');
return;
}

if( data.programId.split(',').length > 1 ||
data.consumerTypeId.split(',').length > 1 || 
data.programStructureId.split(',').length > 1 ){

document.getElementById("applicableType").options[3].selected = 'selected';

return;
}

if(applicableType === 'module'){
//make api call to get batch details 

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getModuleDataByMasterKeyConfig",   
data : JSON.stringify(data),
success : function(data) {

console.log("SUCCESS: dataForReferenceId :");
console.log(data.dataForReferenceId);


var dataForReferenceId = data.dataForReferenceId;

if( !dataForReferenceId && dataForReferenceId.length === 0){
alert("Unable to get Modules for selected program config, Kindly update selections and try again.");
return;
}

var labelForReferenceId = "<label for='refrenceId'>Applicable Modules</label>";
var selectElementTopPart = "<select  id='referenceId'  name='referenceId'    placeholder='referenceId' type='text' class='form-control' required>" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForReferenceId.length;i++){

options = options + "<option value='" + dataForReferenceId[i].id + "'> Module: "+dataForReferenceId[i].sessionModuleNo+". " 
			+ dataForReferenceId[i].topic + " </option>";
}


console.log("==========> options\n" + options);

var htmlToAppend = labelForReferenceId 
		+ selectElementTopPart 
		+ " <option disabled selected value=''> Select Modules </option> " + options 
		+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#moduleReferenceIdDiv').html( htmlToAppend );
//End




},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});
//make api call to get batch details end

}else{
return;
}


});

///////////////////////////////////////////////

//////////////////////////////////////////////

function createSubjectSelectElement(dataForSubject) {

console.log("createSubjectSelectElement: dataForSubject :");
console.log(dataForSubject);

if( !dataForSubject ){
alert("Unable to get subjects for selected program config, Kindly update selections and try again.");
return;
}

if( dataForSubject.length === 0){
alert("Unable to get subjects for selected program config, Kindly update selections and try again.");
return;
}

var labelForSubject = "<label for='subject'>Subject</label>";
var selectElementTopPart = "<select  id='subject'  name='subject'    placeholder='subject' type='text' class='form-control' required>" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForSubject.length;i++){

options = options + "<option value='" + dataForSubject[i] + "'> "
+ dataForSubject[i] + " </option>";
}


console.log("==========> options\n" + options);

var htmlToAppend = labelForSubject 
+ selectElementTopPart 
+ " <option disabled selected value=''> Select Subject </option> " + options 
+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#subjectSelectContainerDiv').html( htmlToAppend );
//End

}

//////////////////////////////////////////////

//////////////////////////////////////////////

function createModulesSelect(){

var labelForReferenceId = "<label for='refrenceId'>Applicable Modules</label>";
var selectElementTopPart = "<select  id='referenceId'  name='referenceId'    placeholder='referenceId' type='text' class='form-control' required>" 
var selectElementBottomPart = " </select>";

options = ""; 

var htmlToAppend = labelForReferenceId 
+ selectElementTopPart 
+ " <option disabled selected value=''> Select Modules </option> " + options 
+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#moduleReferenceIdDiv').html( htmlToAppend );
//End
}

//////////////////////////////////////////////





	
	
	
});
$("#questionTemplate").on('change', function(e) {
	var type = $(this).children("option:selected").attr('type');
	var duration = $(this).children("option:selected").attr('dur');
	var maxQuestnToShow = $(this).children("option:selected").attr('qns'); 
	//var marks = $(this).children("option:selected").attr('marks'); 
	var marks = ($(this).children("option:selected").attr('marks')).replace('.0',''); 
	$("#maxQuestnToShow").val(maxQuestnToShow);   
	$("#duration").val(duration);   
	$("#maxScore").val(marks);  
	var day= Math.floor((duration/60)/24);
	var remainingMins=duration - (day*24*60); 
	var hours = Math.floor(remainingMins/60); 
	var minutes = remainingMins % 60; 
	$("#day").val(day);     
	$("#hours").val(hours); 
	$("#minutes").val(minutes);  
	$(".testType").val(type);
	  
	var sessionDate=moment("${date}","YYYY-MM-DDTHH:mm:ss").format("YYYY-MM-DD"); 
	  
	var weekday = moment(sessionDate).isoWeekday();             
             
	 var testdate=(weekday=="5")?//if friday  
				testdate =moment(sessionDate,"YYYY-MM-DD").add(3,'days').format("YYYY-MM-DD")
				:testdate =moment(sessionDate,"YYYY-MM-DD").add(1,'days').format("YYYY-MM-DD"); 
    var startDate= testdate+"T"+"21:45:00"; 
	$('#addtest_startDate').val(startDate);  
	var endDate= moment(startDate,"YYYY-MM-DDTHH:mm:ss").add(20,'minutes').format("YYYY-MM-DDTHH:mm:ss");
	$('#addtest_endDate').val(endDate);   
	//var endDate= moment(startDate,"YYYY-MM-DDTHH:mm:ss").add(duration,'minutes').format("YYYY-MM-DDTHH:mm:ss");
	//$('#addtest_endDate').val(endDate);  
});       
var duration = $("#duration").val();
var day= Math.floor((duration/60)/24);
var remainingMins=duration - (day*24*60); 
var hours = Math.floor(remainingMins/60); 
var minutes = remainingMins % 60; 
$("#day").val(day);     
$("#hours").val(hours); 
$("#minutes").val(minutes);  

$("#day").on("change", function(e) {
	calculateDuration(); 
});
$("#hours").on("change", function(e) {
	calculateDuration(); 
});
$("#minutes").on("change", function(e) {
	calculateDuration();
});
function calculateDuration(){
	var d = $("#day").val();
	var hr = $("#hours").val(); 
	var min = $("#minutes").val();
	var durInHr =  (d*24)+parseInt(hr); 
	
	var durInMin = parseInt(durInHr* 60) + parseInt(min); 
	$("#duration").val(durInMin);  
}
        
$(".template").val($(".template option:first").val()).change();
     
</script>


</html>