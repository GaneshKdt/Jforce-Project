<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="${sr.serviceRequestType }"  name="title" />
</jsp:include>


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
         <div class="row"><legend>${sr.serviceRequestType }</legend></div>
        <form:form  action="teeRevaluationConfirmation" method="post" modelAttribute="sr" >
		<fieldset>
       
        <%@ include file="../messages.jsp"%>
		<div class="panel-body">
		
		
			<c:if test="${size > 0 }">
		
			<div>
			Dear Student, You have chosen below Service Request. Please select the subject/s before proceeding for Payment.
			</div>
			<br>
			
			<div class="col-md-18 column">
				
				<div class="form-group">
					<form:label path="serviceRequestType" for="serviceRequestType">Service Request Type:</form:label>
					${sr.serviceRequestType } for ${mostRecentResultPeriod } Exams
					<form:hidden path="serviceRequestType"/>
				</div>
				
				<div class="form-group">
					<label>Charges:</label>
					INR. ${charges }/- per Subject
				</div>
				
								
				<div class="table-responsive">
				<table class="table table-striped" style="font-size: 12px">
					<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Select</th>
							<th style="text-align:left;">Subject</th>
							<th>Sem</th>
							<th>Marks</th>
						</tr>
					</thead>
					<tbody>

						<c:forEach var="studentMarks" items="${studentMarksList}"
							varStatus="status">
							<c:if test="${not empty studentMarks.writenscore}">
							
							<tr>
								<td><c:out value="${status.count}" /></td>
								<td>
								<c:if test="${studentMarks.markedForRevaluation == 'N' }">
									<form:checkbox path="revaluationSubjects" value="${studentMarks.subject}"  />
								</c:if>
								
								<c:if test="${studentMarks.markedForRevaluation == 'Y' }">
									Applied for Revaluation
								</c:if>
								</td>
								<td nowrap="nowrap" style="text-align:left;"><c:out value="${studentMarks.subject}" /></td>
								<td><c:out value="${studentMarks.sem}" /></td>
								<td><c:out value="${studentMarks.writenscore}" /></td>
							</tr>
							</c:if>
						</c:forEach>

					</tbody>
				</table>
				</div>
					
						
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="submit" name="submit"
							class="btn btn-large btn-primary" formaction="teeRevaluationConfirmation" >Proceed</button>
						<button id="cancel" name="cancel" class="btn btn-danger"
							formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
				
					
				</div>
				
				
			</c:if>		
			
			<c:if test="${size == 0 }">		
					<div>
					Dear Student, You have 0 records for ${mostRecentResultPeriod } Term End Exams.
					</div>
			
					<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<button id="submit" name="submit"
							class="btn btn-large btn-primary" formaction="selectSRForm">Select Another Service Request</button>
						<button id="cancel" name="cancel" class="btn btn-danger"
							formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
			</c:if>
				
		</div>
		</fieldset>
		
				
		</form:form>
	</div>
	
</section>
	
<jsp:include page="../footer.jsp" />

</body>
</html>
 --%>



<!DOCTYPE html>

<html lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="${sr.serviceRequestType }" name="title" />
</jsp:include>

<style>
	td {
		padding: 10px;
	}
	.selectCheckBox {
		width: 30px; /*Desired width*/
		height: 30px; /*Desired height*/
	}
	.red {
		color: red;
		font-size: 14px;
	}
	[type="checkbox"]:not (:checked ), [type="checkbox"]:checked {
		position: relative;
		left: 0px;
		opacity: 1;
	}
	.table {
		font-size: 12px;
	}
	#copyCaseNote {
		color: #d2232a;
		margin-bottom: 0;
	}
	@media only screen and (min-width: 1024px) {
		#copyCaseTable {
			width: 60%;
		}
	}
</style>

<body>
	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<%@ include file="../common/breadcrum.jsp"%>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<form:form action="teeRevaluationConfirmation" method="post"
								modelAttribute="sr">
								<fieldset>
									<c:if test="${size > 0 }">
										<p>Dear Student, You have chosen below Service Request.
											Please select the subject/s before proceeding for Payment.</p>
										<br>

										<div class="col-md-18 column">

											<div class="form-group">
												<form:label path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }for ${mostRecentResultPeriod }
													Exams</p>
												<form:hidden path="serviceRequestType" />
											</div>

											<div class="form-group">
												<label>Charges:</label>
												<p>INR. ${charges }/- per Subject</p>
											</div>


											<div class="table-responsive">
												<table class="table table-striped">
													<thead>
														<tr>
															<th>Sr. No.</th>
															<th>Select</th>
															<th style="text-align: left;">Subject</th>
															<th>Sem</th>
															<th>Marks</th>
														</tr>
													</thead>
													<tbody>

														<c:forEach var="studentMarks" items="${studentMarksList}"
															varStatus="status">
															<c:if test="${not empty studentMarks.writenscore}">
																<c:choose>
																	<c:when test="${studentMarks.writenscore == 'NV' || studentMarks.writenscore == 'RIA'}">
																		<tr>
																			<td><c:out value="${status.count}" /></td>
																			<td><b>You cannot apply for the revaluation of this subject</b>
																			</td>
																			<td nowrap="nowrap" style="text-align: left;"><c:out
																					value="${studentMarks.subject}" /></td>
																			<td><c:out value="${studentMarks.sem}" /></td>
																			<td><c:out value="${studentMarks.writenscore}" /></td>
																		</tr>
																	</c:when>
																	<%-- #Commented as per Card 11453, Copy Case Subjects shown in a different table and student unable to select those subjects for revaluation
																	<c:when test="${(studentMarks.subject == 'Project' || studentMarks.subject == 'Module 4 - Project') && fn:containsIgnoreCase(studentMarks.remarks, 'Copy Case')}">
																		<tr>
																			<td><c:out value="${status.count}" /></td>
																			<td><b>You cannot apply for the revaluation of this subject</b>
																			<td nowrap="nowrap" style="text-align: left;"><c:out
																					value="${studentMarks.subject}" /></td>
																			<td><c:out value="${studentMarks.sem}" /></td>
																			<td><c:out value="${studentMarks.writenscore}" /></td>
																		</tr>
																	</c:when> --%>
																	<c:otherwise>
																		<tr>
																			<td><c:out value="${status.count}" /></td>
																			<td><c:if test="${studentMarks.markedForRevaluation == 'N' }">
																					<form:checkbox path="revaluationSubjects" class="subjectCheck"
																						value="${fn:replace(studentMarks.subject,',','|')}" onclick="checkSelectedSubjects()"/>
																				</c:if> 
																				<c:if test="${studentMarks.markedForRevaluation == 'Y' }">
																					Applied for Revaluation
																				</c:if>
																			</td>
																			<td nowrap="nowrap" style="text-align: left;"><c:out
																					value="${studentMarks.subject}" /></td>
																			<td><c:out value="${studentMarks.sem}" /></td>
																			<td><c:out value="${studentMarks.writenscore}" /></td>
																		</tr>
																	</c:otherwise>
																</c:choose>
															</c:if>
														</c:forEach>

													</tbody>
												</table>
											</div>


											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submit" name="submit"
														class="btn btn-large btn-primary" disabled
														formaction="teeRevaluationConfirmation">Proceed</button>
													<button id="cancel" name="cancel" class="btn btn-danger"
														formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
												</div>
											</div>
										</div>
									</c:if>
									<c:if test="${size == 0 }">
										<p>Dear Student, You have 0 records for
											${mostRecentResultPeriod } Term End Exams.</p>
										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<div class="controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary" formaction="selectSRForm">Select
													Another Service Request</button>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
									</c:if>
									<c:if test="${copyCaseProjectSubjectsList.size() gt 0}">
										<br/>
										<div class="form-group">
											<label for="copyCaseNote">Copy Case Subjects:</label>
											<p id="copyCaseNote">Please Note:- Subjects marked under Copy Case cannot be selected for revaluation.</p>
										</div>
										<div id="copyCaseTable" class="table-responsive">
											<table class="table table-striped table-bordered">
												<thead>
													<tr>
														<th>Sr. No.</th>
														<th>Subject</th>
														<th>Sem</th>
													</tr>
												</thead>
												<tbody>
													<c:forEach var="copyCaseSubjects" items="${copyCaseProjectSubjectsList}" varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><c:out value="${copyCaseSubjects.subject}" /></td>
															<td><c:out value="${copyCaseSubjects.sem}" /></td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</div>
									</c:if>
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../common/footer.jsp" />
	
<script type="text/javascript"
		src="${pageContext.request.contextPath}/assets/js/serviceRequest/teeRevaluation.js"></script>
</body>
</html>