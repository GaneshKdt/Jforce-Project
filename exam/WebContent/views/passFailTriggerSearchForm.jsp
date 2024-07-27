
<!DOCTYPE html>
<html lang="en">

<%--@page import="com.nmims.beans.Person"--%><%--Commented by Vilpesh on 2021-12-16  --%>
<%@page import="com.nmims.beans.Page"%>
<%--@page import="com.nmims.beans.OnlineExamMarksBean"--%><%--Commented by Vilpesh on 2021-12-16 --%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="adminCommon/jscss.jsp">
	<jsp:param value="Search for PassFail Trigger" name="title" />
</jsp:include>



<body>

	<%@ include file="adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;Search Records for PassFail Trigger" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper examsPage">
					<%@ include file="adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">

						<h2 class="red text-capitalize">Search Records For PassFail Processing</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%@ include file="adminCommon/messages.jsp"%>
							<form:form action="processPassFailForm" method="post"
								modelAttribute="bean">
								<fieldset>
									<div class="row">
									<div class="col-md-3">
										<div class="form-group">
											<form:select id="enrollmentYear" path="year"
												type="text" placeholder="Year" class="form-control"
												itemValue="${bean.year}" 
												>
												<form:option value="">Select Result Processing Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>
									</div> 
									</div>
									
									<div class="row">
									<div class="col-md-3">
											<div class="form-group">
												<form:select id="enrollmentMonth" path="month"  class="form-control"  itemValue="${bean.month}">
													<form:option value="">Select  Result Processing Month</form:option>
													<form:options items="${monthList}" />
												</form:select>
											</div>
										</div> 
										</div>

										
									<div class="row">
									<div class="col-md-3">
										<div class="form-group">
											<form:select id="program" path="program" type="text"
												placeholder="Program" class="form-control"
												itemValue="${bean.program}">
												<form:option value="">Select Program</form:option>
												<form:options items="${programList}" />
											</form:select>
										</div>
										</div> 
										</div>


										
									<div class="row">
									<div class="col-md-3">
										<div class="form-group">
											<form:select id="prgmStructApplicable"
												path="prgmStructApplicable" placeholder="Program Structure"
												class="form-control" itemValue="${bean.prgmStructApplicable}">
												<form:option value="">Select Program Structure</form:option>
												<form:options items="${progStructListFromProgramMaster}" />
											</form:select>
										</div>
										</div>
										</div>
										
									<div class="row">
									<div class="col-md-3">
										<div class="form-group">
											<form:select id="consumerType" path="consumerType"
												placeholder="consumerType" class="form-control"
												value="${bean.consumerType}">
												<form:option value="">Select Consumer Type</form:option>
												<form:option value="Retail">Retail</form:option>
												<form:option value="Verizon">Verizon</form:option>
												<form:option value="SAS">SAS</form:option>
												<form:option value="Diageo">Diageo</form:option>
												<form:option value="Bajaj">Bajaj</form:option>
												<form:option value="EMERSON">EMERSON</form:option>
												<form:option value="CIPLA">CIPLA</form:option>
												<form:option value="Disontinued from MLI (No more Employee)">Disontinued from MLI (No more Employee)</form:option>
											</form:select>
										</div>
										</div>
										</div>
										
									<div class="row">
									<div class="col-md-3">

										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<button id="submit" name="submit"
												class="btn btn-large btn-primary"
												formaction="processPassFailForm">Search</button>
											<button id="reset" type="reset" class="btn btn-danger"
												type="reset">Reset</button>
											<button id="cancel" name="cancel" class="btn btn-danger"
												formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
										</div>
									</div>
									</div>
								</fieldset>
							</form:form>
						</div>
						<c:choose>
							<c:when test="${rowCount > 0}">

								<h2>
									&nbsp;Student Records <font size="2px"> (${rowCount}
										Records Found)&nbsp; <a href="downloadStudents">Download
											to Excel</a>
									</font>
								</h2>
								<div class="clearfix"></div>
								<div class="panel-content-wrapper">
									<div class="table-responsive">
										<table class="table table-striped table-hover"
											style="font-size: 12px">
											<thead>
												<tr>
													<th>Sr. No.</th>
													<th>SAP ID</th>
													<th>First Name</th>
													<th>Last Name</th>
													<th>Program</th>
													<th>Sem</th>
													<th>Email</th>
													<th>Mobile</th>
													<th>Enrollment Year</th>
													<th>Enrollment Month</th>
													<th>Validity End Year</th>
													<th>Validity End Month</th>
													<th>Program Structure</th>
													<th>Actions</th>
												</tr>
											</thead>
											<tbody>

												<c:forEach var="student" items="${studentList}"
													varStatus="status">
													<tr>
														<td><c:out value="${status.count}" /></td>
														<td><c:out value="${bean.sapid}" /></td>
														<td><c:out value="${bean.firstName}" /></td>
														<td><c:out value="${bean.lastName}" /></td>
														<td><c:out value="${bean.program}" /></td>
														<td><c:out value="${bean.sem}" /></td>
														<td><c:out value="${bean.emailId}" /></td>
														<td><c:out value="${bean.mobile}" /></td>
														<td><c:out value="${bean.enrollmentYear}" /></td>
														<td><c:out value="${bean.enrollmentMonth}" /></td>
														<td><c:out value="${bean.validityEndYear}" /></td>
														<td><c:out value="${bean.validityEndMonth}" /></td>
														<td><c:out value="${bean.prgmStructApplicable}" /></td>
														<td><c:url value="editStudent" var="editurl">
																<c:param name="sapid" value="${bean.sapid}" />
																<c:param name="sem" value="${bean.sem}" />
															</c:url> 
					                             <%if (roles.indexOf("Exam Admin") != -1
 					                                   || roles.indexOf("Assignment Admin") != -1
 					                                   || roles.indexOf("Acads Admin") != -1) {%> 
 					                                   <a href="${editurl}" title="Edit"><i
																class="fa-solid fa-pen-to-square fa-lg"></i></a>&nbsp; 
												 <%}%> 
												            <c:url value="viewSingleStudentDetail" var="detailsUrl">
																<c:param name="sapid" value="${bean.sapid}" />
															</c:url> 
															<a href="${detailsUrl}" title="Details"><i
																class="fa-solid fa-circle-info fa-lg"></i></a>&nbsp;</td>

													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
								<br>
							</c:when>
						</c:choose>
						<c:url var="firstUrl" value="searchStudentsPage?pageNo=1" />
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
								</div>
							</c:when>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="adminCommon/footer.jsp" />


</body>
</html>