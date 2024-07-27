<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<%--
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Exam Data Management" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">
		<div class="container-fluid customTheme">
		<%@ include file="messages.jsp"%>
		<div class="row"><legend>&nbsp;Records pending for Pass/Fail Processing	:${pendingRecordsCount}. Processing will take considerable time. </legend></div>
		<form role="form" id="passFailForm" action="processPassFail" method="post">
			<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="processPassFail?passFailLogicType=${passFailLogicType}">Process remaining records for Pass/Fail</button>
			<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button> <br><br>
	
		<div class = "col-md-4 column">
		<div class="panel-body">
		
		<div class="row"><h2>&nbsp;Records Count Distribution </h2></div>
		
		<div class="table-responsive">
			<table class="table table-striped" style="font-size: 12px" border="1px">
			<thead>
			  <tr>
				<td>Category</td>
				<td>Count</td>
			  </tr>
			</thead>
			<tbody>
				<tr>
					<%-- <td>Project Records
					</td>
					<td>${projectCount }
					</td> --%>
					<%--<td>Project Records Online
					</td>
					<td>${projectCountOnline }
					</td>
				</tr>
				<tr>
					<td>Project Records Offline
					</td>
					<td>${projectCountOffline }
					</td>
				</tr>
				<tr>
					<td>Absent Student Records
					</td>
					<td>${absentCount }
					</td>
				</tr>
				<tr>
					<td> NV / RIA
					</td>
					<td>${nvRiaCount }
					</td>
				</tr>
				<tr>
					<td>ANS Records
					</td>
					<td>${ansCount }
					</td>
				</tr>
				<tr>
					<td>Assignment Submitted Records Online
					</td>
					<td>${assignmentScoreOnlineCount }
					</td>
				</tr>
				<tr>
					<td>Assignment Submitted Records Offline
					</td>
					<td>${assignmentScoreOfflineCount }
					</td>
				</tr>
				<tr>
					<td>Written Score Records Online
					</td>
					<td>${writtenScoreOnlineCount }
					</td>
				</tr>
				<tr>
					<td>Written Score Records Offline
					</td>
					<td>${writtenScoreOfflineCount }
					</td>
				</tr>
				<tr>
					<td>Online 
					</td>
					<td>${onlineCount }
					</td>
				</tr>
				<tr>
					<td>Offline
					</td>
					<td>${offlineCount }
					</td>
				</tr>
			
			</tbody>
			
			</table>
	</div>
		</div>
		</div>
		</form>
		
	</div>
	</section>




	<jsp:include page="footer.jsp" />

</body>
</html>
 --%>
<html lang="en">
<!--<![endif]-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search Summary of PassFail" name="title" />
</jsp:include><head><link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/v/dt/dt-1.13.1/datatables.min.css"/>
 
<script type="text/javascript" src="https://cdn.datatables.net/v/dt/dt-1.13.1/datatables.min.js"></script></head>
<body>
	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Summary of PassFail Trigger" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">Search Summary</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 420px;">
							<%@ include file="adminCommon/messages.jsp"%>	
							<form:form role="form" id="passFailForm" action="processPassFail"
								method="post">
								<fieldset>
									<div class="row">
										<div class="col-md-12">
											<c:if test="${pendingRecordsCount > 0}">
												<h4>Records pending for Pass/Fail Processing
													:${pendingRecordsCount}. Processing will take considerable
													time.</h4>
											</c:if>
										</div>
									</div>
									<div class="row">
										<label class="control-label" for="submit"></label>
										<button id="submit" name="submit"
											class="btn btn-large btn-primary"
											formaction="processPassFail?passFailLogicType=${passFailLogicType}">Process
											remaining records for Pass/Fail</button>
										<button id="cancel" class="btn btn-danger" formaction="/exam/admin/passFailTriggerSearchForm" formmethod="get" formnovalidate="formnovalidate">Cancel</button>
									</div>
									<div class="row">
										<div class="col-md-3">
											<div class="table-responsive">
												<table class="table table-striped" style="font-size: 12px" id="passFailProcess"
													border="1px">
													<caption><h4>Records Count Distribution</h4></caption>
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
															  <c:when test ="${projectCountOnline >0}">
															<td class="text-center text-black"><a href="projectRecordOnline" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>Project Records Offline</td>
															<td>${projectCountOffline }</td>
															<c:choose>
															  <c:when test ="${projectCountOffline >0}">
														<td class="text-center text-black"><a href="projectRecordOffline" ><i class="fa-solid fa-download"></i></a></td></c:when>
														<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>Absent Student Records (TEE)</td>
															<td>${absentCount }</td>
															<c:choose>
															  <c:when test ="${absentCount >0}">
															<td class="text-center text-black"><a href="absentRecord" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>Project Absent Student Records</td>
															<td>${projectAbsentCount }</td>
															<c:choose>
															  <c:when test ="${projectAbsentCount >0}">
															<td class="text-center text-black"><a href="projectAbsent" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>Project Not Booked Student</td>
															<td>${projectNotBookedCount}</td>
															<c:choose>
															  <c:when test ="${projectNotBookedCount > 0}">
															<td class="text-center text-black"><a href="projectNotBookedStudentExcelReport" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr> 
														<tr>
															<td>NV / RIA</td>
															<td>${nvRiaCount }</td>
															<c:choose>
															  <c:when test ="${nvRiaCount >0}">
															<td class="text-center text-black"> <a href="nvriaReportDownload" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>ANS Records</td>
															<td>${ansCount }</td>
															<c:choose>
															  <c:when test ="${ansCount >0}">
															<td class="text-center text-black"><a href="ansReportDownload"><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>Assignment Submitted Records Online</td>
															<td>${assignmentScoreOnlineCount }</td>
															<c:choose>
															  <c:when test ="${assignmentScoreOnlineCount >0}">
															<td class="text-center text-black"><a href="assignmentSubmitOnline" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>Assignment Submitted Records Offline</td>
															<td>${assignemntScoreOfflineCount }</td>
															<c:choose>
															  <c:when test ="${assignmentScoreOfflineCount >0}">
															<td class="text-center text-black"><a href="assignmentSubmitOffline" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>Written Score Records Online</td>
															<td>${writtenScoreOnlineCount }</td>
															<c:choose>
															  <c:when test ="${writtenScoreOnlineCount >0}">
															<td class="text-center text-black"><a href="writtenScoreRecordsOnline" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>Written Score Records Offline</td>
															<td>${writtenScoreOfflineCount }</td>
															<c:choose>
															  <c:when test ="${writtenScoreOfflineCount >0}">
															<td class="text-center text-black"><a href="writtenScoreRecordsOffline" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>Online</td>
															<td>${onlineCount }</td>
															<c:choose>
															  <c:when test ="${onlineCount >0}">
															<td class="text-center text-black"><a href="onlineDownloadPassFailReport" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
														<tr>
															<td>Offline</td>
															<td>${offlineCount }</td>
															<c:choose>
															  <c:when test ="${offlineCount >0}">
															<td class="text-center text-black"><a href="offlineDownloadPassFailReport" ><i class="fa-solid fa-download"></i></a></td></c:when>
															<c:otherwise>
															<td class="text-center text-black"><a class="text-muted" disabled ><i class="fa-solid fa-download"></i></a></td>
															</c:otherwise>
															</c:choose>
														</tr>
													</tbody>
												</table>
											</div>
										</div>
									</div>
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="adminCommon/footer.jsp" />
</body>
</html>