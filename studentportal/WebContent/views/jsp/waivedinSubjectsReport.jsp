
<!DOCTYPE html>
<html lang="en">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>



<!-- head>

<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.13.4/css/dataTables.bootstrap.min.css" />
</head> -->

<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search WaivedIn Subjects" name="title" />
</jsp:include>


<body>

	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search WaivedIn Subjects" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						
						<h2 class="red text-capitalize">SEARCH WAIVEDIN SUBJECTS</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 200px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="getWaivedInSubjectsReport" method="post"
								modelAttribute="studentBean">
								<fieldset>
									<div class="col-md-4">

										<div class="form-group">
											<form:select id="enrollmentYear" path="acadYear" type="text"
												class="form-control" required="required" itemValue="${studentBean.acadYear}">
												<form:option value="">Select Enrollment Year</form:option>
												<form:options items="${acadYearList}" />
											</form:select>
										</div>

										<div class="form-group">
											<form:select id="enrollmentMonth" path="acadMonth"
												type="text" class="form-control" required="required"
												itemValue="${studentBean.acadMonth}">
												<form:option value="">Select Enrollment Month</form:option>
												<form:options items="${acadMonthList}" />
											</form:select>
										</div>



										<div class="col-md-6">

											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="getWaivedInSubjectsReport">Search</button>

											</div>
										</div>
								</fieldset>
							</form:form>
						</div>

						<%--  <c:choose>
							<c:when test="${ studentlist != null && studentlist.size() > 0 }"> 
 --%>
 						<c:if test="${ studentlist != null && studentlist.size() > 0 }">
						<h2>
							&nbsp;Student Records <font size="2px">
								<%-- (${rowCount}
										Records Found)&nbsp;  --%> <a href="downloadWaivedInReport" target="_blank">Download
									to Excel</a>
							</font>
						</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<div class="table-responsive ">
								<table class="table table-striped table-hover"
									style="font-size: 12px" id="searchStudent">

									<thead>
										<tr>
											<th>SAP ID</th>
											<th>First Name</th>
											<th>Last Name</th>
											<th>Program</th>
											<th>Sem</th>
											<th>subject</th>
											<th>Closed Won Date</th>
										</tr>
									</thead>
									<tbody>

										<c:forEach var="student" items="${studentlist}"
											varStatus="status">
											<tr>
												<td><c:out value="${student.sapid}" /></td>
												<td><c:out value="${student.firstName}" /></td>
												<td><c:out value="${student.lastName}" /></td>
												<td><c:out value="${student.program}" /></td>
												<td><c:out value="${student.sem}" /></td>
												<td><c:out value="${student.subject}" /></td>
												<td><c:out value="${student.regDate}" /></td>

											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
						<br>
						<%-- </c:when>
						</c:choose> --%>
						<%--					<c:url var="firstUrl" value="searchStudentsPage?pageNo=1" />
						<c:url var="lastUrl"
							value="searchStudentsPage?pageNo=${page.totalPages}" />
						<c:url var="prevUrl"
							value="searchStudentsPage?pageNo=${page.currentIndex - 1}" />
						<c:url var="nextUrl"
							value="searchStudentsPage?pageNo=${page.currentIndex + 1}" />


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
										<c:forEach var="i" begin="${page.beginIndex}"
											end="${page.endIndex}">
											<c:url var="pageUrl" value="searchStudentsPage?pageNo=${i}" />
											<c:choose>
												<c:when test="${i == page.currentIndex}">
													<li class="active"><a href="${pageUrl}"><c:out
																value="${i}" /></a></li>
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
								</div>--%>
							<%-- </c:when>
						</c:choose> --%>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="adminCommon/footer.jsp" />
	



</body>
</html>

