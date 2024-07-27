<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html> 

<html>

<jsp:include page="common/jscss.jsp">
	<jsp:param value="View Recent Results" name="title" />
</jsp:include>

<style>
#parentSpinnerDiv {
	background-color: rgba(255, 255, 255, 0.5) !important;
	z-index: 999;
	width: 100%;
	height: 100vh;
	position: fixed;
}

#childSpinnerDiv {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
}

.loading {
	box-sizing: border-box;
	display: inline-block;
	padding: 0.5em;
	vertical-align: middle;
	text-align: center;
	background-color: transparent;
	border: 5px solid transparent;
	border-top-color: grey;
	border-bottom-color: grey;
	border-radius: 50%;
}

.outer {
	animation: spin 1s infinite;
}

.inner {
	animation: spin 1s infinite;
}

@
keyframes spin { 0% {
	transform: rotateZ(0deg);
}

100%
{
transform








:




 




rotateZ








(360
deg






);
}
}
#wrap {
	box-sizing: border-box;
}

#studentsName {
	font-family: "Aller";
	font-weight: bold;
	text-transform: uppercase;
	font-size: 1.2rem;
	margin: 1.5rem 0 1rem 0;
}
</style>


<body>
	<!-- Code for Page here Starts -->

	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="#"><img
					src="assets/images/logo.png" class="img-responsive" alt="" /></a>
			</div>
			<ul class="nav navbar-nav navbar-right">
				<li class="active"><a class="btn btn-danger navbar-btn"
					href="/studentportal/logout"><span class="icon-logout"></span>
						Logout</a></li>
			</ul>
				<ul class="nav navbar-nav navbar-right">
				<li class="active"><a class="btn btn-danger navbar-btn"
					onClick="goToPortal()">Go To Portal</a></li>
			</ul>
		</div>
	</nav>

	<div class="container">


		<!--Top Student Profile bar start -->
		<div class="panel-content-wrapper">
			<div class="container">
				<img
					style="width: 80px; height: 80px; float: left; margin: 5px 15px 5px 5px;"
					src="${ studentsDataInRedisBean.resultsData.studentDetails[0].imageUrl }"
					class="img-responsive img-circle" alt="Profile Image">

				<p style="margin: 10px 10px;">
					<span id="studentsName"> <c:out
							value="${ studentsDataInRedisBean.resultsData.studentDetails[0].firstName }" />
						<c:out
							value="${ studentsDataInRedisBean.resultsData.studentDetails[0].lastName }" />
					</span> <br>

					<c:out
						value="${ studentsDataInRedisBean.resultsData.studentDetails[0].emailId }" />
					|
					<c:out
						value="${ studentsDataInRedisBean.resultsData.studentDetails[0].mobile }" />
					|
					<c:out
						value="${ studentsDataInRedisBean.resultsData.studentDetails[0].program }" />
					|

					<c:out
						value="${ studentsDataInRedisBean.resultsData.studentDetails[0].sapid }" />
					<%-- 	<fmt:parseDate value="${ studentsDataInRedisBean.resultsData.studentDetails[0].month  studentsDataInRedisBean.resultsData.studentDetails[0].year}" var="validityEndDate" pattern="yyyy/MM/dd" />
			Validity End: <fmt:formatDate pattern="dd-MMM-yyyy" value="${validityEndDate}" /> --%>
				</p>

			</div>
			<div class="clearfix"></div>

		</div>
		<!--Top Student Profile bar end -->

		<div id="resultsFromCacheMainDiv">

			<c:choose>
				<c:when
					test="${not empty studentsDataInRedisBean.resultsData && not empty studentsDataInRedisBean.resultsData.studentDetails[0] && not empty studentsDataInRedisBean.resultsData.studentMarksList && not empty studentsDataInRedisBean.resultsData.passFailStatus && not empty studentsDataInRedisBean.resultsData.studentMarksHistory}">
					<div class="clearfix"></div>
					<div id="resultsFromCacheMostRecentDiv">

						<h2 style="margin-left: 10px;" class=" text-capitalize">
							Results for
							<c:out
								value="${ studentsDataInRedisBean.resultsData.mostRecentResultPeriod[0]}" />
						</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<div class="table-responsive">
								<table class="table courses-sessions">
									<thead>
										<tr>
											<th>Sr. No.</th>
											<th style="text-align: left;">Subject</th>
											<th>Sem</th>
											<th>Term End Exam MCQ</th>
											<th>Term End Exam Descriptive</th>
											<th>Term End Exam Rounded Total</th>
											<th>Assignment</th>
											<th>Remarks</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="recent_mark"
											items="${studentsDataInRedisBean.resultsData.studentMarksList}"
											varStatus="theCount">
											<tr>
												<td><c:out value="${theCount.count}" /></td>
												<td nowrap="nowrap" style="text-align: left;"><c:out
														value="${recent_mark.subject} " /></td>
												<td><c:out value="${recent_mark.sem}" /></td>
												<c:choose>
													<c:when test="${'NV' == recent_mark.writenscore}">
														<td>NV</td>
														<td>NV</td>
													</c:when>
													<c:when test="${'RIA' == recent_mark.writenscore}">
														<td>RIA</td>
														<td>RIA</td>
													</c:when>
													<c:otherwise>
														<td><c:out
																value="${not empty recent_mark.mcq ? recent_mark.mcq: ''}" /></td>
														<td><c:out
																value="${not empty recent_mark.part4marks  ? recent_mark.part4marks: ''}" /></td>
													</c:otherwise>
												</c:choose>
												<td><c:out value="${recent_mark.writenscore}" /></td>
												<td><c:out value="${recent_mark.assignmentscore}" /></td>
												<td><c:out value="${recent_mark.remarks}" /></td>
												<td><c:out value="${recent_mark.reason}" /></td>
											</tr>
										</c:forEach>

									</tbody>
								</table>
							</div>
						</div>
					</div>
					
					
					<div class="sz-content" id = "assignmentResult">
								
              						</div>
					
					
					
					
					
					
					
					
					
					
					
					
					
					


					<div class="clearfix"></div>
					<div id="resultsFromCachePassFailDiv">
						<div class="panel panel-default panel-courses-page">
							<div class="panel-heading" role="tab" id="">
								<h2>Pass Fail Status</h2>
								<div class="custom-clearfix clearfix"></div>
								<ul class="topRightLinks list-inline">
									<li>
										<h3 class=" green">
											<span><c:out
													value="${ studentsDataInRedisBean.resultsData.passFailStatus[1].size()}" /></span>
											Records Available
										</h3>
									</li>
									<li><a class="panel-toggler collapsed" role="button"
										data-toggle="collapse" href="#collapseOne"
										aria-expanded="true"><span
											class="glyphicon glyphicon-arrow-down"></span></a></li>

								</ul>
								<div class="clearfix"></div>
							</div>


							<div id="collapseOne"
								class="panel-collapse collapse panel-content-wrapper"
								role="tabpanel">
								<div class="panel-body">

									<div class="data-content">
										<div class="panel-content-wrapper">
											<div class="table-responsive">
												<table class="table courses-sessions">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th style="text-align: left;">Subject</th>
															<th>Sem</th>
															<th style="text-align: center">TEE Marks</th>
															<th style="text-align: center">Assignment Marks</th>
															<th style="text-align: center">Grace Marks</th>
															<th style="text-align: center">Total Marks</th>
														</tr>
													</thead>
													<tbody>
														<c:forEach var="passfail"
															items="${studentsDataInRedisBean.resultsData.passFailStatus[1]}"
															varStatus="theCount">
															<tr>
																<td><c:out value="${theCount.count}" /></td>
																<td nowrap="nowrap" style="text-align: left;"><c:out
																		value="${passfail.subject}" /></td>
																<td><c:out value="${passfail.sem}" /></td>
																<td style="text-align: center"><c:out
																		value="${passfail.writtenscore}" /><sub>(<c:out
																			value="${passfail.writtenMonth}" />-<c:out
																			value="${passfail.writtenYear}" />)
																</sub></td>
																<td style="text-align: center"><c:out
																		value="${passfail.assignmentscore}" /><sub>(<c:out
																			value="${passfail.assignmentMonth }" />-<c:out
																			value="${ passfail.assignmentYear}" />)
																</sub></td>
																<td style="text-align: center"><c:out
																		value="${not empty passfail.gracemarks  ? passfail.gracemarks: ''}" /></td>
																<c:choose>
																	<c:when test="${'Y' == passfail.isPass}">
																		<td style="text-align: center; color: green"><b><c:out
																					value="${passfail.total}"></c:out></b></td>
																	</c:when>
																	<c:when
																		test="${'ANS' == passfail.assignmentscore && passfail.writtenscore != 'AB'  && passfail.writtenscore != 'RIA' && passfail.writtenscore != 'NV' &&	passfail.writtenscore > 0}">
																		<td style="text-align: center"><b>On Hold
																				(Assignment Not Submitted)</b></td>
																	</c:when>
																	<c:when test="${'N' == passfail.isPass}">
																		<td style="text-align: center; color: red"><b><c:out
																					value="${passfail.total}" /></b></td>
																	</c:when>
																	<c:otherwise>
																		<td style="text-align: center;"><b><c:out
																					value="${ passfail.total}"></c:out></b></td>
																	</c:otherwise>
																</c:choose>
															<tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="clearfix"></div>
						<div id="resultsFromCacheMarksDiv">
							<div class="panel panel-default panel-courses-page">

								<div class="panel-heading" role="tab" id="">

									<h2>Marks History</h2>

									<div class="custom-clearfix clearfix"></div>

									<ul class="topRightLinks list-inline">

										<li>
											<h3 class=" green">
												<span> <c:out
														value="${ studentsDataInRedisBean.resultsData.studentMarksHistory.size()}" />
												</span> Records Available
											</h3>
										</li>
										<li><a class="panel-toggler collapsed" role="button"
											data-toggle="collapse" href="#collapseMarksHistory"
											aria-expanded="true"><span
												class="glyphicon glyphicon-arrow-down"></span></a></li>

									</ul>
									<div class="clearfix"></div>
								</div>
								<div id="collapseMarksHistory"
									class="panel-collapse collapse panel-content-wrapper"
									role="tabpanel">
									<div class="panel-body">




										<c:choose>

											<c:when
												test="${ studentsDataInRedisBean.resultsData.studentMarksHistory.size() > 0}">
												<div class="data-content">

													<div class="panel-content-wrapper">

														<div class="table-responsive">

															<table class="table table-striped"
																style="font-size: 12px" id="studentMarksHistory">

																<thead>
																	<tr>
																		<th>Sr. No.</th>
																		<th>Exam Year</th>
																		<th>Exam Month</th>
																		<th>Sem</th>
																		<th style="text-align: left;">Subject</th>
																		<th style="text-align: center">Written</th>
																		<th style="text-align: center">Assign.</th>
																		<th style="text-align: center">Grace</th>
																	</tr>
																</thead>

																<tbody>
																	<c:forEach var="marks_history"
																		items="${studentsDataInRedisBean.resultsData.studentMarksHistory}"
																		varStatus="theCount">
																		<tr>
																			<td><c:out value="${theCount.count}" /></td>
																			<td><c:out value="${ marks_history.year }" /></td>
																			<td><c:out value="${ marks_history.month }" /></td>
																			<td><c:out value="${ marks_history.sem }" /></td>
																			<td nowrap="nowrap" style="text-align: left;"><c:out
																					value="${ marks_history.subject}" /></td>
																			<td style="text-align: center"><c:out
																					value="${ marks_history.writenscore}" /></td>
																			<td style="text-align: center"><c:out
																					value="${ marks_history.assignmentscore}" /></td>
																			<td style="text-align: center"><c:out
																					value="${ marks_history.gracemarks }" /></td>
																		<tr>
																	</c:forEach>
																</tbody>
															</table>
														</div>
													</div>
												</div>

											</c:when>
											<c:otherwise>
												<div class="no-data-wrapper">

													<p class="no-data">
														<span class="icon-exams"></span>No Mark History
													</p>

												</div>

											</c:otherwise>
										</c:choose>

									</div>
								</div>
							</div>
						</div>
						<div class="clearfix"></div>


						<div id="resultsFromCacheFooterDiv">
							<hr class="exam-separator"></hr>
							<div class="row">
								<div class="col-md-4">
									<div class="signatureLeft">
										<div>
											<img
												src="https://studentzone-ngasce.nmims.edu/exam/resources_2015/images/signature.jpg"
												height="80px" width="100px"></img>
										</div>
										<h5>Controller of Examinations</h5>
										<p>
											Result Declaration Date: <b><c:out
													value="${ studentsDataInRedisBean.resultsData.declareDate[0]}" /></b>
										</p>
									</div>
								</div>
								<div class="col-md-4">

									<c:choose>
										<c:when
											test="${'Bajaj' == studentsDataInRedisBean.resultsData.studentDetails[0].consumerType && !('Jul2014' == studentsDataInRedisBean.resultsData.studentDetails[0].prgmStructApplicable && 'DBM' == studentsDataInRedisBean.resultsData.studentDetails[0].program)}">

											<ol>
												<li>Individual CutOff or Individual Passing Criteria
													will be 40%(i.e. Aggregate Passing: Internal Continuous
													Assessments + Term-End-Examination) for each Subject</li>
											</ol>
										</c:when>
										<c:when
											test="${'Certificate' == studentsDataInRedisBean.resultsData.studentDetails[0].programType || 'Jul2017' == studentsDataInRedisBean.resultsData.studentDetails[0].prgmStructApplicable}">
											<p>Pass Marks:40 out of 100(i.e. Aggregate Passing:
												Internal Continuous Assessments + Term-End-Examination)</p>

										</c:when>

										<c:otherwise>
											<p>Pass Marks:50 out of 100(i.e. Aggregate Passing:
												Internal Continuous Assessments + Term-End-Examination)</p>

										</c:otherwise>


									</c:choose>
									<p>
										Discrepancy if any in the above information should be mailed
										with student name, Student No., Program enrolled, Semester
										details, Subject: at <a href="mailto:ngasce.exams@nmims.edu"
											target="_top">ngasce.exams@nmims.edu</a>
									</p>
									<c:if
										test='${studentsDataInRedisBean.resultsData.studentDetails[0].prgmStructApplicable  ne "Jul2017" }'>
										<p>
											Students who wish to apply for revaluation of the descriptive

											answers may apply for the same on or before <b> 7th
												February 2021 23:59 p.m. </b> using Service Request link
											available on Student Zone Home Page.
										</p>
									</c:if>
								</div>



								<div class="col-md-4">
									<div class="row">
										<div class="statusBox">
											<div class="media">
												<div class="media-left media-top">ANS</div>
												<div class="media-body">Assignment Not Submitted</div>
											</div>
											<div class="media">
												<div class="media-left media-top">AB</div>
												<div class="media-body">Absent</div>
											</div>
											<div class="media">
												<div class="media-left media-top">NV</div>
												<div class="media-body">Null & Void</div>
											</div>
											<div class="media">
												<div class="media-left media-top">CC</div>
												<div class="media-body">Copy Case</div>
											</div>
											<div class="media">
												<div class="media-left media-top">RIA</div>
												<div class="media-body">Result Kept in Abeyance</div>
											</div>
											<div class="media">
												<div class="media-left media-top">NA</div>
												<div class="media-body">Not Eligible due to non
													submission of assignment</div>
											</div>
										</div>
									</div>
								</div>
								<div style="display: inline-block; margin-left: 10px">
									<p>The results published on this website are only for
										immediate information to the examinees and cannot be
										considered as final. Information as regards marks/ grades
										published by NMIMS University in mark sheet/ grade sheet
										should only be treated as authentic.</p>
								</div>
							</div>
							<div class="clearfix"></div>
						</div>
					</div>
				</c:when>
				<c:otherwise>

					<div class="clearfix"></div>
					<div class="alert alert-danger"
						style="margin-top: 10px; font-size: 16px;">
						<strong><span class="glyphicon glyphicon-alert"></span></strong>
						Something Went Wrong, Please try again later.
					</div>

				</c:otherwise>
			</c:choose>


		</div>
		<!-- Code for Page here Ends -->
	</div>
</body>
<script src="assets/js/jquery-1.11.3.min.js"></script>

<script src="assets/js/bootstrap.js"></script>

</html>

<script type="text/javascript">
<!--

//-->

//Assign handlers immediately after making the request,
//and remember the jqxhr object for this request
$.ajax({
  type: "POST",
  url: "/exam/m/getMostRecentAssignmentResults",
  data: JSON.stringify( {
      "sapid":  "${ studentsDataInRedisBean.resultsData.studentDetails[0].sapid }",
      "sem": "${ studentsDataInRedisBean.resultsData.studentDetails[0].sem }",
      "prgmStructApplicable": "${ studentsDataInRedisBean.resultsData.studentDetails[0].prgmStructApplicable } ",
      "centerName": "${ studentsDataInRedisBean.resultsData.studentDetails[0].centerName} "
  }),
	  						dataType : 'json',

  contentType : "application/json",

  
  
  success: function(response){
	   resultLatestAssignmentList = response.studentMarksList; 
	
       
       
       console.log(resultLatestAssignmentList)
  
	  
	  if (resultLatestAssignmentList.length > 0) {





		  var assignmentresult = '' +



			  	'	<h2 style="margin-left: 10px;"  class="red text-capitalize"> ' + resultLatestAssignmentList.length + ' <span>Assignment Marks records for ' +  "${ studentsDataInRedisBean.resultsData.mostRecentResultPeriod[0]}" + ' Exam</span> </h2> ' 

+ '  <div class="clearfix"></div>  '
 
				
              							
												if("Diageo" == "{$studentsDataInRedisBean.resultsData.studentDetails[0].consumerType)}"){
												
											
											assignmentresult = assignmentresult +	'<h5>Results Data Will Be Displayed Shortly.</h5>'
												
											
												}else{
										
              							assignmentresult = assignmentresult + '<div class="panel-content-wrapper" >';
              								
												}
											





										assignmentresult = assignmentresult + 	' <div class="table-responsive"> '+ 
													 '	<table class="table table-striped" style="font-size: 12px"> ' +
															 ' <thead> ' + 
															' 	<tr> ' +
																 '	<th>Sr. No.</th> ' +
																' 	<th style="text-align:left;">Subject</th> ' +
																' 	<th>Sem</th> ' +
																 '	<th>Assignment Marks</th> ' + 
																 '	<th>Remarks</th> '  + 
																	
																' </tr> ' +
															 ' </thead> '  + 
														' 	<tbody > ' ;
					 
															
					
					
															

		  
		  $
			.each(
					resultLatestAssignmentList,
					function(i, post) {
			  if(post.reason){
						reason =  post.reason
						} else{
													reason =  "";

						}
						







						assignmentresult = assignmentresult +  '<tr> ' + 
						' <td>' + (i + 1) + '</td> ' + 
						' <td nowrap="nowrap" style="text-align:left;">' + post.subject  + '</td> ' + 
						' <td>' + post.sem + '</td> ' + 
						' <td>' + post.assignmentscore +  '</td> ' + 
						' <td>' + reason  +  '</td> ' + 
						

					' </tr> ' 
						
					})


											assignmentresult = assignmentresult + '				</tbody> ' + 
														' </table> '  +
												'	</div> '  + 



													 '<div class="notesWrapper"> ' + 
														' <h5 class="text-uppercase">Notes:</h5> ' +
														' <ul> ';
															if("ACBM" == ("${studentsDataInRedisBean.resultsData.studentDetails[0].program }")){ 
														assignmentresult = assignmentresult + 	'<li>The marks displayed are out of 40 for Internal Assignment.</li>'
															}else{ 
														assignmentresult = assignmentresult + 	'<li>The marks displayed are out of 30 for Internal Assignment.</li>'
															} 

													assignmentresult = assignmentresult + 		'<li>In case student wants to apply for Revaluation of assignment he/she can apply for Revaluation of assignment marks only through the <b> Student Zone -> Service Request Tab -> Internal Assignment Revaluation</b> option by paying applicable fees.</li>' +
															' <li>Fees for assignment revaluation is Rs.1000/- per subject.</li> ' +
															 ' <li>Payment Mode: Revaluation Fee payment is via Debit Card / Credit Card / Net Banking only. No Cash / Demand Draft facility is being offered.</li> ' 

if("Online" == "${ studentsDataInRedisBean.resultsData.studentDetails[0].examMode }" && !"JUL2017" == "${studentsDataInRedisBean.resultsData.studentDetails[0].prgmStructApplicable }" ){ 
														assignmentresult = assignmentresult + 	'	<li>Last date to apply for December, 2020 Assignment Revaluation is <b>7th February 2021 23:59 p.m. (IST)</b> No request for assignment revaluation will be  accepted after closure of revaluation window for reasons whatsoever.</li> '
}
else{
														assignmentresult = assignmentresult + 	'	<li>Last date to apply for December, 2020 Assignment Revaluation is <b>7th February 2021 23:59 p.m. (IST)</b> No request for assignment revaluation will be  accepted after closure of revaluation window for reasons whatsoever.</li> ';
														} 


														assignmentresult = assignmentresult + 	 ' </ul> ' + 
													' </div> '  


					console.log(assignmentresult)



$("#assignmentResult").html(assignmentresult)

	
		  		  
	  }
	  
	
	  
	  
  },
});





	function goToPortal() {
		
		
		
//alert("You will be able to access the portal in an hour.");
		window.location = "/studentportal/skipToHome";			
			}
			
		
</script>



