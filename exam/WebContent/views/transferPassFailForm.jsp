
<!DOCTYPE html>
<html lang="en">

<%@page import="com.nmims.beans.Page"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Transfer Passfail Staging" name="title" />
</jsp:include>

<body>

	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Transfer Passfail Staging"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Transfer Passfail from
							staging Environment</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="processPassFailForm" method="post"
								modelAttribute="bean">
								<fieldset>
									<div class="row">
										<div class="col-md-3">
											<div class="form-group">
												<form:select id="enrollmentYear" path="year" type="text"
													placeholder="Year" class="form-control"
													itemValue="${bean.year}" required="required">
													<form:option value="">Select Year</form:option>
													<form:options items="${yearList}" />
												</form:select>
											</div>
										</div>
									</div>

									<div class="row">
										<div class="col-md-3">
											<div class="form-group">
												<form:select id="enrollmentMonth" path="month"
													class="form-control" itemValue="${bean.month}"
													required="required">
													<form:option value="">Select Month</form:option>
													<form:options items="${monthList}" />
												</form:select>
											</div>
										</div>
									</div>

									<div class="row">
										<div class="col-md-3">

											<div class="form-group">
												<button id="search" name="search"
													class="btn btn-large btn-primary"
													formaction="searchPassfailStaging">Search</button>
												<%-- <c:if test="${showReportDownloadTable == 'true'}">
													<label class="control-label" for="submit"></label>
													<button id="submit" name="submit"
														class="btn btn-large btn-primary"
														formaction="transferPassFail">Transfer</button>
												</c:if> --%>
												<button id="cancel" name="cancel" class="btn btn-danger"
													formaction="${pageContext.request.contextPath}/home"
													formnovalidate="formnovalidate">Cancel</button>
											</div>
										</div>
									</div>
									<c:if test="${showReportDownloadTable == 'true'}">
										<div class="row">
											<div class="col-md-3">
												<div class="table-responsive">
													<table class="table table-striped" style="font-size: 12px"
														id="passFailProcess" border="1px">
														<caption>
															<h4>Records Count Distribution</h4>
														</caption>
														<thead>
															<tr>
																<th>Category</th>
																<th>Count</th>
																<th>Download</th>
															</tr>
														</thead>
														<tbody>
															<tr>
																<!-- <td>Project Records
															</td>
															<td><%-- ${projectCount } --%>
															</td> -->
																<td>Project Records Online</td>
																<td>${projectCountOnline }</td>
																<c:choose>
																	<c:when test="${projectCountOnline >0}">
																		<td class="text-center text-black"><a
																			href="downloadPassFailTransferReport?type=projectCountOnline"><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:when>
																	<c:otherwise>
																		<td class="text-center text-black"><a
																			class="text-muted" disabled><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:otherwise>
																</c:choose>
															</tr>
															<%-- <tr>
															<td>Project Records Offline</td>
															<td>${projectCountOffline }</td>
															<c:choose>
																<c:when test="${projectCountOffline >0}">
																	<td class="text-center text-black"><a
																		href="projectRecordOffline"><i
																			class="fa-solid fa-download"></i></a></td>
																</c:when>
																<c:otherwise>
																	<td class="text-center text-black"><a
																		class="text-muted" disabled><i
																			class="fa-solid fa-download"></i></a></td>
																</c:otherwise>
															</c:choose>
														</tr> --%>
															<tr>
																<td>Absent Student Records (TEE)</td>
																<td>${absentCount }</td>
																<c:choose>
																	<c:when test="${absentCount >0}">
																		<td class="text-center text-black"><a
																			href="downloadPassFailTransferReport?type=absentCount"><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:when>
																	<c:otherwise>
																		<td class="text-center text-black"><a
																			class="text-muted" disabled><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:otherwise>
																</c:choose>
															</tr>
															<tr>
																<td>Project Absent Student Records</td>
																<td>${projectAbsentCount }</td>
																<c:choose>
																	<c:when test="${projectAbsentCount >0}">
																		<td class="text-center text-black"><a
																			href="downloadPassFailTransferReport?type=projectAbsentCount"><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:when>
																	<c:otherwise>
																		<td class="text-center text-black"><a
																			class="text-muted" disabled><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:otherwise>
																</c:choose>
															</tr>
															<tr>
																<td>Project Not Booked Student</td>
																<td>${projectNotBookedCount}</td>
																<c:choose>
																	<c:when test="${projectNotBookedCount > 0}">
																		<td class="text-center text-black"><a
																			href="downloadPassFailTransferReport?type=projectNotBookedCount"><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:when>
																	<c:otherwise>
																		<td class="text-center text-black"><a
																			class="text-muted" disabled><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:otherwise>
																</c:choose>
															</tr>
															<tr>
																<td>NV / RIA</td>
																<td>${nvRiaCount }</td>
																<c:choose>
																	<c:when test="${nvRiaCount >0}">
																		<td class="text-center text-black"><a
																			href="downloadPassFailTransferReport?type=nvRiaCount"><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:when>
																	<c:otherwise>
																		<td class="text-center text-black"><a
																			class="text-muted" disabled><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:otherwise>
																</c:choose>
															</tr>
															<tr>
																<td>ANS Records</td>
																<td>${ansCount }</td>
																<c:choose>
																	<c:when test="${ansCount >0}">
																		<td class="text-center text-black"><a
																			href="downloadPassFailTransferReport?type=ansCount"><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:when>
																	<c:otherwise>
																		<td class="text-center text-black"><a
																			class="text-muted" disabled><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:otherwise>
																</c:choose>
															</tr>
															<tr>
																<td>Assignment Submitted Records Online</td>
																<td>${assignmentScoreOnlineCount }</td>
																<c:choose>
																	<c:when test="${assignmentScoreOnlineCount >0}">
																		<td class="text-center text-black"><a
																			href="downloadPassFailTransferReport?type=assignmentScoreOnlineCount"><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:when>
																	<c:otherwise>
																		<td class="text-center text-black"><a
																			class="text-muted" disabled><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:otherwise>
																</c:choose>
															</tr>
															<%-- <tr>
															<td>Assignment Submitted Records Offline</td>
															<td>${assignemntScoreOfflineCount }</td>
															<c:choose>
																<c:when test="${assignmentScoreOfflineCount >0}">
																	<td class="text-center text-black"><a
																		href="assignmentSubmitOffline"><i
																			class="fa-solid fa-download"></i></a></td>
																</c:when>
																<c:otherwise>
																	<td class="text-center text-black"><a
																		class="text-muted" disabled><i
																			class="fa-solid fa-download"></i></a></td>
																</c:otherwise>
															</c:choose>
														</tr> --%>
															<tr>
																<td>Written Score Records Online</td>
																<td>${writtenScoreOnlineCount }</td>
																<c:choose>
																	<c:when test="${writtenScoreOnlineCount >0}">
																		<td class="text-center text-black"><a
																			href="downloadPassFailTransferReport?type=writtenScoreOnlineCount"><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:when>
																	<c:otherwise>
																		<td class="text-center text-black"><a
																			class="text-muted" disabled><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:otherwise>
																</c:choose>
															</tr>
															
															<tr>
																<td>Grace Marks Online</td>
																<td>${graceMarksOnlineCount }</td>
																<c:choose>
																	<c:when test="${graceMarksOnlineCount >0}">
																		<td class="text-center text-black"><a
																			href="downloadPassFailTransferReport?type=graceMarksOnlineCount"><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:when>
																	<c:otherwise>
																		<td class="text-center text-black"><a
																			class="text-muted" disabled><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:otherwise>
																</c:choose>
															</tr>
															<%-- <tr>
															<td>Written Score Records Offline</td>
															<td>${writtenScoreOfflineCount }</td>
															<c:choose>
																<c:when test="${writtenScoreOfflineCount >0}">
																	<td class="text-center text-black"><a
																		href="writtenScoreRecordsOffline"><i
																			class="fa-solid fa-download"></i></a></td>
																</c:when>
																<c:otherwise>
																	<td class="text-center text-black"><a
																		class="text-muted" disabled><i
																			class="fa-solid fa-download"></i></a></td>
																</c:otherwise>
															</c:choose>
														</tr> --%>
															<tr>
																<td>Online</td>
																<td>${onlineCount }</td>
																<c:choose>
																	<c:when test="${onlineCount >0}">
																		<td class="text-center text-black"><a
																			href="downloadPassFailTransferReport?type=onlineCount"><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:when>
																	<c:otherwise>
																		<td class="text-center text-black"><a
																			class="text-muted" disabled><i
																				class="fa-solid fa-download"></i></a></td>
																	</c:otherwise>
																</c:choose>
															</tr>
															<%-- <tr>
															<td>Offline</td>
															<td>${offlineCount }</td>
															<c:choose>
																<c:when test="${offlineCount >0}">
																	<td class="text-center text-black"><a
																		href="offlineDownloadPassFailReport"><i
																			class="fa-solid fa-download"></i></a></td>
																</c:when>
																<c:otherwise>
																	<td class="text-center text-black"><a
																		class="text-muted" disabled><i
																			class="fa-solid fa-download"></i></a></td>
																</c:otherwise>
															</c:choose>
														</tr> --%>
														</tbody>
													</table>
													<c:if test="${showReportDownloadTable == 'true'}">
														<label class="control-label" for="submit"></label>
														<button id="submit" name="submit"
															class="btn btn-large btn-primary"
															formaction="transferPassFail">Transfer</button>
													</c:if>
												</div>
											</div>
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
	<jsp:include page="adminCommon/footer.jsp" />
	<!-- <script type="text/javascript">
$('#search').click(function(){
	var year = $('#enrollmentYear').val();
	var month = $("#enrollmentMonth").val();
	
	if(!(year && month )){
		alert('please select year and month to search!');
	}
	
	$.ajax({
		url : '/exam/admin/getPassFailTranferReportCount',
		type : 'GET',
		contentType : 'application/json',
		dataType : 'json',
		data : {
			'year' : year,
			'month' : month
		},
		success : function(data){
			console.log(data);
		}
	})
});
</script> -->

</body>
</html>