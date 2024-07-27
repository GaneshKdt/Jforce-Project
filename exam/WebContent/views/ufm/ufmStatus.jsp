<!DOCTYPE html>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html lang="en">

<spring:eval expression="@propertyConfigurer.getProperty('UMF_ACCESS_URL')" var="UMF_ACCESS_URL"/>
<jsp:include page="../common/jscss.jsp">
	<jsp:param value="UFM Status" name="title" />
</jsp:include>
<style>
	.ufm-column-data {
		max-width: 20%;
	}
</style>
<body>
	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;UFM Dashboard" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			   <div id="sticky-sidebar">  
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="UFMDashboard" name="activeMenu" />
					</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize" style="width:100%">UFM Dashboard</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<c:set var="pendingShowCause" value="false"/>
							<c:set var="showCausePendingBean" value=""/>
							<jsp:useBean id="now" class="java.util.Date"/>
							<c:forEach items="${responseBean.notices}" var="bean">
								<c:if test="${ bean.canSubmitResponse == 'Y' }">
									<c:set var="pendingShowCause" value="true"/>
									<c:set var="showCausePendingBean" value="${ bean }"/>
								</c:if>
							</c:forEach>
							
							<c:if test="${ pendingShowCause == 'true' }">
								<div class="alert alert-danger alert-dismissible">
									<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times;  </button>
									Show cause pending for ${ showCausePendingBean.month }-${showCausePendingBean.year } examinations.
									Please submit your response below.
								</div>
							</c:if>
							<div class="table-responsive">
								<table class="table table-striped" style="font-size:12px; width: 150%">
									<thead>
										<tr> 
											<th>Exam Year</th>
											<th>Exam Month</th>
											<th style="text-align:left;">Subject</th>
											<!-- <th style="text-align:center;">Exam Date</th>
											<th style="text-align:center;">Exam Time</th> -->
											<th>Show Cause Date</th>
											<th>UFM Reason</th>
											<th width = "20%">
												Response Submitted<br>
												(Max 2000 Characters)
											</th>
											<th width = "20%">Response Submission Date</th>
											<th>Status</th>
											<th>Documents Available</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${responseBean.notices}" var="bean">
											<c:set var="rowSpan" value="${ fn:length(bean.subjectsList) }"/>
											<tr>
												<td>${ bean.year }</td>
												<td>${ bean.month }</td>
												<td>${ bean.subject }</td>
												<%-- <td>${ subject.examDate }</td>
												<td>${ subject.examTime }</td> --%>
												<td>${ bean.showCauseGenerationDate }</td>
												<td>${ bean.ufmMarkReason }</td>
												<td>
													<c:choose>
														<c:when test="${ bean.canSubmitResponse == 'Y' }">
															<form action="submitAssignment" method="post">
																<input type="hidden" name = "year" value="${ bean.year }">
																<input type="hidden" name = "month" value="${ bean.month }">
																<input type="hidden" name = "subject" value="${ bean.subject }">
																<input type="hidden" name = "category" value="${ bean.category }">
																<textarea 
																	class="reason-text" 
																	name="showCauseResponse" 
																	style="width:100%" 
																	rows="8" 
																	maxlength="2000" 
																	placeholder="Enter Reason Here..."
																	data-characters-subject="${ bean.subject }"
																	data-characters-month="${ bean.month }"
																	data-characters-year="${ bean.year }"
																></textarea>
																<span 
																	style="font-size: smaller; width: 100%" 
																	class="characters-left" 
																	data-characters-subject="${ bean.subject }"
																	data-characters-month="${ bean.month }"
																	data-characters-year="${ bean.year }"
																>
																</span>
																<div class="clearfix"></div>
																<button id="submit" name="submit" class="btn btn-danger" formaction="/exam/student/submitShowCause">
																	Submit
																</button>
															</form>
														</c:when>
														<c:otherwise>${ bean.showCauseResponse }</c:otherwise>
													</c:choose>
												</td>
												<td>${ bean.showCauseSubmissionDate }</td>
												<td><span style="">${ bean.status }</span></td>
												<td>
													<c:if test="${ bean.showCauseNoticeURL != null }">
														<a href="${UMF_ACCESS_URL}/${ bean.showCauseNoticeURL }">
															Download Show Cause File
														</a>
													</c:if>
													<div class="clearfix"></div>
													<c:if test="${ bean.decisionNoticeURL != null }">
														<a href="${UMF_ACCESS_URL}/${ bean.decisionNoticeURL }">
															Download Decision Notice File
														</a>
													</c:if>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
							
							<div class="clearfix"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<jsp:include page="../common/footer.jsp" />
	<script>
		$('.reason-text').on('change keyup', function() {
			var length = $(this).val().length;
			var subject = $(this).attr('data-characters-subject')
			var month = $(this).attr('data-characters-month')
			var year = $(this).attr('data-characters-year')
			$('.characters-left')
			.filter('[data-characters-subject="' + subject + '"]')
			.filter('[data-characters-month="' + month + '"]')
			.filter('[data-characters-year="' + year + '"]')
			.html('Characters Left : ' + (2000 - length))
		})
		
	</script>
</body>
</html>