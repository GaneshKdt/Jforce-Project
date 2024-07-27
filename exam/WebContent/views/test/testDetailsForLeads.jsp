
<!DOCTYPE html>
<html lang="en">
	
<%@page import="com.nmims.beans.Person"%>
  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
  
    <jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="Create Online Test" name="title"/>
    </jsp:include>
    
 
<link
	href="https://gitcdn.github.io/bootstrap-toggle/2.2.2/css/bootstrap-toggle.min.css"
	rel="stylesheet">
<script
  src="https://code.jquery.com/jquery-3.3.1.js"
  integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
  crossorigin="anonymous"></script>
<script
	src="https://gitcdn.github.io/bootstrap-toggle/2.2.2/js/bootstrap-toggle.min.js"></script>

  <style>
  #snackbar {
	visibility: hidden;
	/* Hidden by default. Visible on click */
	min-width: 250px;
	/* Set a default minimum width */
	margin-left: -125px;
	/* Divide value of min-width by 2 */
	background-color: #333;
	/* Black background color */
	color: #fff;
	/* White text color */
	text-align: center;
	/* Centered text */
	border-radius: 2px;
	/* Rounded borders */
	padding: 16px;
	/* Padding */
	position: fixed;
	/* Sit on top of the screen */
	z-index: 1;
	/* Add a z-index if needed */
	left: 50%;
	/* Center the snackbar */
	bottom: 30px;
	/* 30px from the bottom */
}

/* Show the snackbar when clicking on a button (class added with JavaScript) */
#snackbar.show {
	visibility: visible;
	/* Show the snackbar */
	/* Add animation: Take 0.5 seconds to fade in and out the snackbar. 
However, delay the fade out process for 2.5 seconds */
	-webkit-animation: fadein 0.5s, fadeout 0.5s 2.5s;
	animation: fadein 0.5s, fadeout 0.5s 2.5s;
}
.statis {
  color: #EEE;
  margin-top: 15px;
}
.statis .box {
  position: relative;
  padding: 15px;
  overflow: hidden;
  border-radius: 3px;
  margin-bottom: 25px;
  min-height:9rem;
}
.statis .box h5:after {
  content: "";
  height: 2px;
  width: 70%;
  margin: auto;
  background-color: rgba(255, 255, 255, 0.12);
  display: block;
  margin-top: 10px;
}
.statis .box i {
  position: absolute;
  height: 70px;
  width: 70px;
  font-size: 25px;
  padding: 15px;
  top: -25px;
  left: -25px;
  background-color: rgba(255, 255, 255, 0.15);
  line-height: 60px;
  text-align: right;
  border-radius: 50%;
}
    </style>  

    <body>
    
    	<%@ include file="../adminCommon/header.jsp" %>
        <div class="sz-main-content-wrapper">
        
        <!-- Custom breadcrumbs as requirement is diff. Start -->
			<div class="sz-breadcrumb-wrapper">
			    <div class="container-fluid">
			        <ul class="sz-breadcrumbs">
			        		<li><a href="/exam/">Exam</a></li>
			        		<li><a href="/exam/admin/viewAllTestsForLeads">Tests</a></li>
			        		<li><a href="#">Test Details</a></li>
			        	
			        </ul>
			        <ul class="sz-social-icons">
			            <li><a href="https://www.facebook.com/NMIMSSCE" class="icon-facebook" target="_blank"></a></li>
			            <li><a href="https://twitter.com/NMIMS_SCE" class="icon-twitter" target="_blank"></a></li>
			            <!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->
						
			        </ul>
			    </div>
			</div>
			<!-- Custom breadcrumbs as requirement is diff. End -->
        	
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../adminCommon/left-sidebar.jsp">
								<jsp:param value="" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../adminCommon/adminInfoBar.jsp" %>
              						<div class="sz-content">
								
											<h2 class="red text-capitalize">Test Details </h2>
											<div class="clearfix"></div>
							<div class="panel-content-wrapper" style="min-height:450px;">
								<%@ include file="../adminCommon/messages.jsp" %>
							
							<!-- Code For Page Goes in Here Start -->
							

											<div class="row">
												
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Subject  : </b>
														${test.subject}
													</div>
												</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Exam Month : </b>
														${test.month}
													</div>
												</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Exam Year : </b>
														${test.year}
													</div>
												</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Acad Month : </b>
														${test.acadMonth}
													</div>
												</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Acad Year : </b>
														${test.acadYear}
													</div>
												</div>
														
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Consumer Type : </b>
														${test.consumerType}
													</div>
												</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Program Structure : </b>
														${test.programStructure}
													</div>
												</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Program : </b>
														${test.program}
													</div>
												</div>
												
												
											   	<c:choose>
										         <c:when test="${test.applicableType eq 'module'}">
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Batch Name : </b>
														${test.name}
													</div>
												</div>
										         
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Subject : </b>
														${test.subject}
													</div>
												</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Module Name : </b>
														${test.referenceBatchOrModuleName}
													</div>
												</div>
										         </c:when>
										         <c:when test="${test.applicableType eq 'batch'}">
										         <div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Batch Name : </b>
														${test.referenceBatchOrModuleName}
													</div>
												</div>
										         
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Subject : </b>
														${test.subject}
													</div>
												</div>
										         </c:when>
										         <c:when test="${test.applicableType eq 'old'}">
										         </c:when>
										         <c:otherwise>
										         </c:otherwise>
										       </c:choose>
																		     	
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Test Name : </b>
														${test.testName}
													</div>
												</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Duration : </b>
														<c:out value="${fn:replace(test.duration,'T', ' ')}${' Minutes'}"></c:out>
													</div>
												</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Start Date</b>
														<c:out value="${fn:replace(test.startDate, 'T', ' ')}"></c:out>
													</div>
												</div>
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<b>End Date</b>
															<c:out value="${fn:replace(test.endDate,'T', ' ')}"></c:out>
														</div>
													</div>
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<b>Allow After End Date : </b>
															${test.allowAfterEndDate}

														</div>
													</div>
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<b>Send Email Alert : </b>
															${test.sendEmailAlert}
														</div>
													</div>
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<b>Send SMS Alert : </b>
															${test.sendSmsAlert}
														</div>
													</div>
													<div class="col-md-4 col-sm-6 col-xs-12 column">
														<div class="form-group">
															<b>Show Results to Students immediately : </b>
															${test.showResultsToStudents}
														</div>
													</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Score out of : </b>
														${test.maxScore}
													</div>
												</div>
												
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Test Description : </b>
														${test.testDescription}
													</div>
												</div>
												
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Max Questions To Show to Students: </b>
														${test.maxQuestnToShow}
													</div>
												</div>
												
												
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Proctoring Enabled </b>
														${test.proctoringEnabled}
													</div>
												</div>
												
												
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Show Calculator</b>
														${test.showCalculator}
													</div>
												</div>
												
												
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Questions Configured :</b>
														<div class="config_list">
															<c:forEach var="config" items="${qnsConfigured}"	varStatus="status">    
																<b style="font-size:13px;"><c:out value="${config.typeName}" /></b>: ${config.maxNoOfQuestions} (${config.questionMarks} Marks)
																<br>   
															</c:forEach> 
														</div>
													</div>
												</div>
												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
														<b>Questions Uploaded :</b>
														<div class="config_list">
															<c:forEach var="qns" items="${qnsUpoaded}"	varStatus="status">    
																<b style="font-size:13px;"><c:out value="${qns.typeName}" /></b>: ${qns.minNoOfQuestions}
																<br>   
															</c:forEach> 
														</div>
													</div>
												</div>
											<div class="col-md-12 col-sm-12 col-xs-12 column">	
												<div class="col-md-4 col-sm-12 col-xs-12 " style="margin-left:-13px">
													<ul class="list-group">
													  <li class="list-group-item">Question Upload
													  	<c:choose>
															<c:when test="${noOfQuestions eq 0}">
		 																<span class="badge">Pending</span> 
																	</c:when>
															<c:otherwise>
		                                                                <span class="badge">Uploaded</span>
																	</c:otherwise>
														</c:choose>
													  </li>
													  <li class="list-group-item test_conf_status">Configure Test Question
													    <c:choose>
															<c:when test="${questionConfiguredList.size() eq 0}">
		 																<span class="badge">Pending</span> 
																	</c:when>
															<c:otherwise>
		                                                                <span class="badge">Configured</span>
																	</c:otherwise>
														</c:choose>
													  </li>
													  <li class="list-group-item">Make Live
													  	<c:choose>
															<c:when test="${ifTestlive}">
		 																<span class="badge">Live</span>
																	</c:when>
															<c:otherwise>
		                                                                <span class="badge"> Pending</span>
																	</c:otherwise>
														</c:choose>
													  </li>
													</ul>
												</div> 
											</div>
											</div>
											
			              					</form>
											<div class="row">

												<div class="col-md-4 col-sm-6 col-xs-12 column">
													<div class="form-group">
													<!-- Change editTestUrlForLeads By Abhay    -->
															<c:url value="editTestForLeadsForm" var="editTestUrl">
																 <c:param name="id">${test.id}</c:param> 
															</c:url>
															

															<c:url value="addTestQuestionForm" var="addTestQuestion">
																<c:param name="id">${test.id}</c:param>
															</c:url>
															<c:if test="${test.testQuestionWeightageReq ne 'Y'}">
																<c:url value="uploadTestQuestionFormForLeads"
																	var="uploadTestQuestion">
																	<c:param name="id">${test.id}</c:param>
																</c:url>
															</c:if>
															<%-- <c:if test="${test.testQuestionWeightageReq eq 'Y'}">
																<c:url value="uploadTestQuestionForm"
																	var="uploadTestQuestion">
																	<c:param name="id">${test.id}</c:param>
																</c:url>
															</c:if> --%>
															<c:if test="${test.testQuestionWeightageReq eq 'Y'}">
																<c:url value="uploadTestQuestionWeightageForm"
																	var="addTestQuestionWeightage">
																	<c:param name="testId">${test.id}</c:param>
																</c:url>
															</c:if>
															
															<c:url value="configureTestQuestions"
																	var="configureTestQuestions">
																	<c:param name="id">${test.id}</c:param>
															</c:url>
															
															<c:url value="allocateFacultyToTestAnswersForm"
																	var="aFTT">
																	<c:param name="id">${test.id}</c:param>
															</c:url>
															
															
															<c:url value="addTestQuestionsForFacultyForm"
																	var="aTQFFF">
																	<c:param name="id">${test.id}</c:param>
															</c:url>
															
															
															<c:url value="testCopyCaseForm"
																	var="tCCF">
																	<c:param name="id">${test.id}</c:param>
															</c:url>

															<a id="submit" class="btn btn-large btn-primary"
																href="${editTestUrl}">Edit</a>
															
															<a id="cancel" class="btn btn-danger"
																href="/studentportal/home" >Cancel</a><br>
															<!--
															<a class="btn btn-large btn-primary"
																href="testNotLive" >Make Live</a><br>
															 <input type="button" class="btn btn-large btn-primary make_test_live" value="Make Live"/> 	</br>
															 -->
															 <a class="btn btn-large btn-primary " 
																href="${uploadTestQuestion}">Upload Test
																Questions</a> ( ${noOfQuestions} entries )  <br> 
															<!--
															<a class="btn btn-large btn-primary " 
																href="${aTQFFF}">Manage 
																Questions</a> ( ${noOfQuestions} questions added )  <br>
															
															
															<c:if test="${test.testQuestionWeightageReq eq 'Y'}">
																<a class="btn btn-large btn-primary"
																	href="${addTestQuestionWeightage}">Add
																	Test Questions Weightage</a> (${noOfQuestionWeightages} entries ) <br>
															</c:if>
															<a class="btn btn-large btn-primary"
																href="${configureTestQuestions}">Configure Test
																Questions</a>   <br>
																
															<a class="btn btn-large btn-primary"
															   href="${tCCF}">
																Copy Cases
															</a>   <br>
																
															<a class="btn btn-large btn-primary"
																href="${aFTT}">Allocate Faculty Test Answers
																</a>   <br>
																
																
															<a class="btn btn-large btn-primary"
																href="/exam/extendedTestsStartEndTimeBySapidForm?id=${test.id}">Extend StartEnd DateTime For Particular Students</a>   <br>
																	
																<a class="btn btn-large btn-primary"
																href="/exam/TestDetailsReport?id=${test.id}">Download Test Results in Excel</a><br>
															
																<a class="btn btn-large btn-primary"
																href="/exam/TestAttendenceDetailsReport?id=${test.id}">Download Test Attendence in Excel</a> <br>	
																
																<c:if test="${noOfQuestions gt 0 && testStudentId ne null}">
																	 adding test preview as student
																	<a target="_blank" class="btn btn-large btn-primary" 
 																	href="/exam/viewTestDetailsForStudentsForAllViews?userId=${testStudentId}&id=${test.id}&message=''">Preview Test As Student  (Test student : ${testStudentId})</a> 
																	
																	<a target="_blank" class="btn btn-large btn-primary"
																	href="/exam/startStudentTestForAllViewsForFacultyPreview?testId=${test.id}&sapId=${testStudentId}">Preview Test As Student  (Test student : ${testStudentId})</a> <br>
																	
																</c:if> 

																	<a class="btn btn-large btn-primary"
																	href="/exam/lostFocusDetails?testId=${test.id}&testName=${test.testName}">Lost Focus Details</a>
																  -->
															
																 <div id="snackbar">Some text some message..</div>					
							<!-- Code For Page Goes in Here End -->
							</div>
							
							</div>
              			</div>
    				</div>
			   </div>
		    </div>
		    
		    </div>
		    </div>
		    </div>
		    
		    
        <jsp:include page="../adminCommon/footer.jsp"/>
    <script type="text/javascript">
   
	 
	function showSnackBar(message) {
	    // Get the snackbar DIV
	    var x = document.getElementById("snackbar");
		console.log("In showSnackBar() got message "+message);
	    x.innerHTML = message;
	    
	    // Add the "show" class to DIV
	    x.className = "show";

	    // After 3 seconds, remove the show class from DIV
	    setTimeout(function(){ x.className = x.className.replace("show", ""); }, 3000);
		console.log("Exiting showSnackBar()");
	}

    
     </script>   
    </body>    
</html>