<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="View UFM Show Cause" name="title" />
</jsp:include>
<spring:eval expression="@propertyConfigurer.getProperty('UMF_ACCESS_URL')" var="UMF_ACCESS_URL"/>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row"><legend>View UFM Show Cause</legend></div>
			
			<div class="panel-body">
				<%@ include file="../messages.jsp"%>
				<form:form modelAttribute="inputBean" method="post" enctype="multipart/form-data" action="listOfStudentsMarkedForUFM">
					<div class="row">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="year">Year</label>
								<form:select path="year" id="year">
									<form:options items="${ yearList }"/>
								</form:select>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="month">Month</label>
								<form:select path="month" id="month">
									<form:options items="${ monthList }"/>
								</form:select>
							</div>
						</div>
					</div>
					<br>
					<div class="row">
						<div class="col-md-6 column">
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="listOfStudentsMarkedForUFM">Search</button>
						</div>
					</div>
				</form:form>
			</div>			
			
		</div>
	</section>
	
	<c:if test="${ records != null && records.size() > 0 }">
	
		<section class="content-container login">
			<div class="container-fluid customTheme">
				<div class="row"><legend>Success List <small><a href="/exam/admin/downloadUFMStudentsList">Download List</a></small></legend></div>
				<div class="panel-body">
					<div class="panel-content-wrapper">
					<div class="table-responsive">
				 		<table id="success-table" class="table table-striped">
							<thead>
								<tr>
									<th>Sapid</th>
									<th>Name</th>
									<th>Subject</th>
									<th>Exam Year</th>
									<th>Exam Month</th>
									<!-- Added By shivam.pandey.EXT - START -->
									<th>Category</th>
									<!-- Added By shivam.pandey.EXT - END -->
									<th>Stage</th>
									<th>Show Cause Generation Date</th>
									<th>Show Cause Deadline</th>
									<th>Show Cause Response Received</th>
									<th>Show Cause Response Received Time</th>
									<th>Notice URL</th>
									<th>Decision URL</th>
									<th>Exam Date</th>
									<th>Exam Time</th>
									<th>Reason</th>
									<th>Program</th>
									<th>Program Structure</th>
									<th>Consumer Type</th>
									<th>LC Name</th>
								</tr>
							</thead>
							<tbody>
							<c:forEach items = "${ records }" var = "bean">
								<tr>
									<td>${ bean.sapid }</td>
									<td style="white-space: nowrap;">${ bean.firstName } ${ bean.lastName }</td>
									<td style="white-space: nowrap;">${ bean.subject }</td>
									<td>${ bean.year }</td>
									<td>${ bean.month }</td>
									<!-- Added By shivam.pandey.EXT - START -->
									<td>${ bean.category }</td>
									<!-- Added By shivam.pandey.EXT - END -->
									<td style="white-space: nowrap;">${ bean.stage }</td>
									<td style="white-space: nowrap;">${ bean.showCauseGenerationDate }</td>
									<td style="white-space: nowrap;">${ bean.showCauseDeadline }</td>
									<td style="white-space: nowrap;">
										${ bean.showCauseResponse != null && bean.showCauseResponse != '' ? 'Y' : 'N' }
									</td>
									<td style="white-space: nowrap;">${ bean.showCauseSubmissionDate }</td>
									
									<td>								
										<c:choose>
											<c:when test="${ bean.showCauseNoticeURL != null}">
												<a href = "${UMF_ACCESS_URL}/${bean.showCauseNoticeURL}">URL</a>
											</c:when>
											<c:otherwise>NA</c:otherwise>
										</c:choose>
									<td>
										<c:choose>
											<c:when test="${ bean.decisionNoticeURL != null}">
												<a href = "${UMF_ACCESS_URL}/${bean.decisionNoticeURL}">URL</a>
											</c:when>
											<c:otherwise>NA</c:otherwise>
										</c:choose>
									</td>
									<td style="white-space: nowrap;">${ bean.examDate }</td>
									<td style="white-space: nowrap;">${ bean.examTime }</td>
									<td>${ bean.ufmMarkReason }</td>
									<td>${ bean.program }</td>
									<td>${ bean.programStructure }</td>
									<td>${ bean.consumerType }</td>
									<td>${ bean.lcName }</td>
								</tr>
							</c:forEach>
							</tbody>
						</table>
					</div>
					</div>
				</div>
			</div>
		</section>
	</c:if>
	
	
	<c:url var="firstUrl" value="listOfStudentsMarkedForUFMPage?pageNo=1" />
	<c:url var="lastUrl" value="listOfStudentsMarkedForUFMPage?pageNo=${page.totalPages}" />
	<c:url var="prevUrl" value="listOfStudentsMarkedForUFMPage?pageNo=${page.currentIndex - 1}" />
	<c:url var="nextUrl" value="listOfStudentsMarkedForUFMPage?pageNo=${page.currentIndex + 1}" />
	
	<c:choose>
		<c:when test="${page.totalPages > 1}">
		<div align="center">
		    <ul class="pagination">
		        <c:choose>
		            <c:when test="${page.currentIndex == 1}">
		                <li class="disabled"><a href="#">&lt;&lt;</a></li>
		                <li class="disabled"><a href="#">&lt;</a></li>
		            </c:when>
		            <c:otherwise>
		                <li><a href="${firstUrl}">&lt;&lt;</a></li>
		                <li><a href="${prevUrl}">&lt;</a></li>
		            </c:otherwise>
		        </c:choose>
		        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
		            <c:url var="pageUrl" value="listOfStudentsMarkedForUFMPage?pageNo=${i}" />
		            <c:choose>
		                <c:when test="${i == page.currentIndex}">
		                    <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
		                </c:when>
		                <c:otherwise>
		                    <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
		                </c:otherwise>
		            </c:choose>
		        </c:forEach>
		        <c:choose>
		            <c:when test="${page.currentIndex == page.totalPages}">
		                <li class="disabled"><a href="#">&gt;</a></li>
		                <li class="disabled"><a href="#">&gt;&gt;</a></li>
		            </c:when>
		            <c:otherwise>
		                <li><a href="${nextUrl}">&gt;</a></li>
		                <li><a href="${lastUrl}">&gt;&gt;</a></li>
		            </c:otherwise>
		        </c:choose>
		    </ul>
		</div>
		</c:when>
	</c:choose>
	
	<jsp:include page="../footer.jsp" />

	<script>
		$(document).ready( function () {
			//$('.dataTable').DataTable();
		});
	</script>
	
</body>
</html>