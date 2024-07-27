
<!DOCTYPE html>
<%@page import="com.nmims.beans.CaseStudyExamBean"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<%
	String sapId = (String)session.getAttribute("userId");
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
%>

<jsp:useBean id="now" class="java.util.Date" />

<html lang="en">
<jsp:include page="../common/jscss.jsp">
	<jsp:param value="View Case Study" name="title" />
</jsp:include>



<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Academics;CaseStudy"
				name="breadcrumItems" />
		</jsp:include>



		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Case Study" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">
						
						<h2 class="red text-capitalize" style="width: 100%">Case
							Study</h2>
						<div class="clearfix"></div>


						<div class="panel-content-wrapper">
						
						
							<%-- <c:if test="${(currentSemSubjectsCount+failSubjectsCount) == 0 }"> --%>
								<%@ include file="../common/messages.jsp"%>
						<%-- 	</c:if> --%>
						<c:if test="${isCaseStudyLive }">
							<div class = "row">
                            <div class="col-lg-5">
												<div class="row">
													<div class="timer-wrapper panel-content-wrapper">
														<span class="icon fa-solid fa-hourglass"></span>
														<h3>TIME LEFT FOR SUBMISSION</h3>
														<div id="currentCaseTimer" class="exam-assg-timer"></div>
														<div class="clearfix"></div>
													</div>
												</div>
											</div>


							<div class="col-lg-7">
								<ul class="extra-assignment-action">
					
									<li><a href="student/viewCaseStudyQuestionFiles" target="_blank" ><span
											class="icon-icon-pdf"></span>Download Case Study Question Files</a></li>
							
									<li><a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/Case Study Guidelines.pdf" ><span
											class="icon-icon-pdf"></span>Download Case Study Guidelines</a></li>

								</ul>
							</div>
							</div>

							<div class="clearfix"></div>

							<fmt:parseDate value="${currentSemEndDateTime}"
								var="currentSemEndDate" pattern="yyyy-MM-dd HH:mm:ss"
								type="BOTH" />
							
							<h2 class="text-capitalize">Current Sem Case Study:
								(${1 - noOfCaseSubmitted} Case
								Study Submission Pending)</h2>

							<div class="clearfix"></div>
						
							<form:form action="" method="post">
								<div class="table-responsive panel-content-wrapper">
								
									<table class="table table-striped" style="font-size: 12px">
										<thead>
											<tr>
												<th>Sr. No.</th>
												<th style="text-align: left;">Choose a Topic</th>
												<th>Click to Submit Case Study</th>
												<th>Status</th>
												<th>Student Case Study Submitted Date & Time</th>

											</tr>
										</thead>
										<tbody>

										<%-- 	<c:forEach var="caseStudyFile" items="${caseStudyList}"
												varStatus="status"> --%>

												<fmt:parseDate value="${submittedCaseStudy.lastModifiedDate}"
													var="submissionDate" pattern="yyyy-MM-dd HH:mm:ss"
													type="BOTH" />

												<tr>
													<td><c:out value="1" /></td>
													<td nowrap="nowrap" style="text-align: left;">
													<select id="topic"  type="text" name="topic"
														placeholder="Select Topic" class="form-control js_topic_select" required="required">
														<option value="select_topic">Select Topic</option>
															<c:choose>
																<c:when test="${submitedCaseName == null}">
																	<c:forEach var ="topic" items="${caseStudyTopicList}">
																		<option value="${topic }">${topic}</option>
														   			</c:forEach>
																</c:when>
																<c:otherwise>
																	<c:forEach var ="topic" items="${caseStudyTopicList}">
																		<c:choose>
																			<c:when test="${submitedCaseName.get(0) == topic}">
																				<option selected value="${topic }">${topic}</option>
																			</c:when>
																			<c:otherwise>
																				<option value="${topic }">${topic}</option>
																			</c:otherwise>
																		</c:choose>
																	</c:forEach>
																</c:otherwise>
															</c:choose>
													</select>
													</td>
												<c:url value="viewSingleCaseStudy" var="detailsUrl">
													<c:param name="batchYear" value="${batchYear}" />
													<c:param name="batchMonth" value="${batchMonth}" />
												    <c:param name="status" value="${status}" /> 
												</c:url>
													<td>
														 <c:choose>
																<c:when test="${submitedCaseName == null}">
																	<a  class ="js_submit_result" href="javascript:void(0)">
																</c:when>
																<c:otherwise>
																	<a  class ="js_submit_result" href="${detailsUrl}&topic=${submitedCaseName.get(0)}" >
																</c:otherwise>
														</c:choose>

															<b> <c:if
																	test="${status == 'Submitted'}">
																					Review Submitted Case Study
																				</c:if> <c:if test="${status != 'Submitted'}">
																					Submit Case Study
																				</c:if>

														</b>
													</a> 
													</td>
													<td nowrap="nowrap"
														<c:if test="${status == 'Submitted'}">style="background-color: #17B149; color:#fff;"</c:if>
														<c:if test="${status != 'Submitted'}">style="background-color: #DF3818;color:#fff;"</c:if>>
														<c:out value="${status}" />
													</td>

													<td><fmt:formatDate pattern="dd-MMM-yyyy HH:mm"
															value="${submissionDate}" timeStyle="full" /></td>

												</tr>

										</tbody>
									</table>
								</div>
							</form:form>
							</c:if>
							
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>






	<jsp:include page="../common/footer.jsp" />
	
	<script>

					$('#currentCaseTimer').countdown({until: new Date('${currentSemEndDateTime}'), format: 'dHMS'});
					$('#currentCaseTimer').countdown('toggle');
					
		$(document).ready(function(){
			var submit_topic =  '${detailsUrl}';
			var submit_topic2 =  '${detailsUrl}';
			$(document).on('change','.js_topic_select',function(){
				submit_topic = submit_topic2;
				$('.js_submit_result').attr('href',"javascript:void(0)");
				console.log('topic change');
				var topic = $(this).val();
				console.log("");
				if(topic == "select_topic"){
					alert("Please select topic");
					return false;
				}
				console.log('value of topic : ' + topic);
				submit_topic = submit_topic + "&topic=" + topic;
				$('.js_submit_result').attr('href',submit_topic);
				console.log('topic selected successfully');
			});
			
			$('.js_submit_result').click(function(){
				if($(this).attr('href') == "javascript:void(0)"){
					alert("please select topic first");
					return false;
				}
			});
			
		});
				
</script>

</body>

</html>