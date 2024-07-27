<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Search Student Marks" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
	<div class ="row">
	<div class ="col-md-12">
		<div class="container-fluid customTheme">
			<%@ include file="messages.jsp"%>
			<br/>
			<div class="row clearfix">
				<form:form action="applyGrace" method="post">
					<fieldset>
						<div class="col-md-6 column">
							<div class="form-group">
								<div class="controls">
								<c:choose>
									<c:when test="${rowCount > 0}">
									<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="applyGrace">Apply Grace to below results</button>
									</c:when>
								</c:choose>
									<button id="cancel" name="cancel" class="btn btn-danger" formaction="getGraceEligibleForm"	formnovalidate="formnovalidate">Back to Grace Module</button>
								</div>
							</div>
						</div>

					</fieldset>
				</form:form>

			</div>
		</div>
		</div>
		</div>

		<c:choose>
			<c:when test="${rowCount > 0}">

				<legend>
					&nbsp;Student Eligible for Grace <font size="2px">(${rowCount} Records
						Found)&nbsp; <a href="downloadGraceEligible">Download to Excel</a></font> 
				</legend> 
				<div class="table-responsive">
				<table class="table table-striped" style="font-size: 12px">
					<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Written Year</th>
							<th>Written Month</th>
							<th>Assignment Year</th>
							<th>Assignment Month</th>
							<th>GR No.</th>
							<th>SAP ID</th>
							<th>Student Name</th>
							<th>Program</th>
							<th>Sem</th>
							<th>Subject</th>
							<th>Written</th>
							<th>Assign.</th>
							<!-- <th>Grace</th> -->
							<th>Total</th>
						</tr>
					</thead>
					<tbody>
						<!--  Display Non-ACBM students first -->
						<c:forEach var="studentMarks" items="${studentMarksList}"
							varStatus="status">
							<tr>
								<td><c:out value="${status.count}" /></td>
								<td><c:out value="${studentMarks.writtenYear}" /></td>
								<td><c:out value="${studentMarks.writtenMonth}" /></td>
								<td><c:out value="${studentMarks.assignmentYear}"/></td>
								<td><c:out value="${studentMarks.assignmentMonth}"/></td>
								<td><c:out value="${studentMarks.grno}" /></td>
								<td><c:out value="${studentMarks.sapid}" /></td>
								<td><c:out value="${studentMarks.name}" /></td>
								<td><c:out value="${studentMarks.program}" /></td>
								<td><c:out value="${studentMarks.sem}" /></td>
								<td><c:out value="${studentMarks.subject}" /></td>
								<td><c:out value="${studentMarks.writtenscore}" /></td>
								<td><c:out value="${studentMarks.assignmentscore}" /></td>
								<td><c:out value="${studentMarks.total}" /></td>
							</tr>
						</c:forEach>
						
						
						<!--  Display BAJAJ(prev. ACBM) students list -->
						<%-- <c:forEach var="studentMarks" items="${acbmStudentMarksList}" --%>
						<c:forEach var="studentMarks" items="${bajajStudentMarksList}"
							varStatus="status">
							<tr>
								<td><c:out value="${status.count}" /></td>
								<td><c:out value="${studentMarks.writtenYear}" /></td>
								<td><c:out value="${studentMarks.writtenMonth}" /></td>
								<td><c:out value="${studentMarks.assignmentYear}"/></td>
								<td><c:out value="${studentMarks.assignmentMonth}"/></td>
								<td><c:out value="${studentMarks.grno}" /></td>
								<td><c:out value="${studentMarks.sapid}" /></td>
								<td><c:out value="${studentMarks.name}" /></td>
								<td><c:out value="${studentMarks.program}" /></td>
								<td><c:out value="${studentMarks.sem}" /></td>
								<td><c:out value="${studentMarks.subject}" /></td>
								<td><c:out value="${studentMarks.writtenscore}" /></td>
								<td><c:out value="${studentMarks.assignmentscore}" /></td>
								<td><c:out value="${studentMarks.total}" /></td>
							</tr>
						</c:forEach>


					</tbody>
				</table>
				</div>
				<br>

			</c:when>
		</c:choose>
		<%-- 
<c:url var="firstUrl" value="searchStudentMarksPage?pageNo=1" />
<c:url var="lastUrl" value="searchStudentMarksPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchStudentMarksPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchStudentMarksPage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="searchStudentMarksPage?pageNo=${i}" />
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
</c:choose> --%>


	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
