<!DOCTYPE html>

<%@page import="com.nmims.beans.RemarksGradeBean"%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="RemarksGrade 7" name="title" />
</jsp:include>

<body class="inside">
	
	<%@ include file="../adminCommon/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../adminCommon/breadcrum.jsp">
			<jsp:param value="Exam;RemarksGrade" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../adminCommon/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../adminCommon/adminInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">RemarksGrade : Transfer</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%--@ include file="../adminCommon/messages.jsp"--%><%@ include
								file="../adminCommon/newmessages.jsp"%>
							<div class="container-fluid customTheme">
								<legend>&nbsp;Transfer out from Staging</legend>
								<div class="row">
									<form:form method="post" modelAttribute="remarksGradeBean">
										<fieldset>
											<div class="col-md-4">
												<div class="form-group">
													<form:select id="year" path="year" type="text"
														placeholder="Year" class="form-control"
														itemValue="${remarksGradeBean.year}" required="required">
														<form:option value="">(*) Select Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div>

												<div class="form-group">
													<form:select id="month" path="month" type="text"
														placeholder="Month" class="form-control"
														itemValue="${remarksGradeBean.month}" required="required">
														<form:option value="">(*) Select Month</form:option>
														<form:options items="${monthList}" />
													</form:select>
												</div>

												<div class="form-group">
													<div class="controls">
														<button id="submit" name="submit"
															class="btn btn-sm btn-primary" formaction="searchRG7">Search</button>
														<button id="submit" name="submit"
															class="btn btn-sm btn-primary" formaction="transferRG7">Transfer</button>
														<button id="cancel" name="cancel"
															class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home"
															formnovalidate="formnovalidate">Cancel</button>
													</div>
												</div>
											</div>
										</fieldset>
									</form:form>
									<c:if test="${rowCount > 0}">
										<h2>
											&nbsp;Search Results <font size="2px"> (<c:out
													value="${rowCount}" /> Records Found)&nbsp;<a
												id="downloadRG7Id"
												href="downloadRG7?syear=${syear}&smonth=${smonth}">Download
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
															<th>Exam Year</th>
															<th>Exam Month</th>
															<th>SAP ID</th>
															<th>Student Name</th>
															<th>Subject</th>
															<th>Sem</th>
															<th>Program</th>
															<th>Program Structure</th>
															<th>Student Type</th>
															<th>Written Score</th>
															<th>Assign Score</th>
															<th>Total Score</th>
															<th>Status</th>
															<th>Is Pass</th>
															<th>Remarks</th>
															<th>Grade</th>
															<th>FailReason</th>
														</tr>
													</thead>
													<tbody>
														<c:forEach var="d" items="${dataList}"
															varStatus="statusOld">
															<tr>
																<td><c:out value="${statusOld.count}" /></td>
																<td><c:out value="${d.year}" /></td>
																<td><c:out value="${d.month}" /></td>
																<td><c:out value="${d.sapid}" /></td>
																<td><c:out value="${d.name}" /></td>
																<td><c:out value="${d.subject}" /></td>
																<td><c:out value="${d.sem}" /></td>
																<td><c:out value="${d.program}" /></td>
																<td><c:out value="${d.programStructure}" /></td>
																<td><c:out value="${d.studentType}" /></td>
																<c:choose>
																	<c:when
																		test="${d.status == RemarksGradeBean.ATTEMPTED || d.status == RemarksGradeBean.CC}">
																		<td><c:out value="${d.scoreWritten}" /></td>
																		<td><c:out value="${d.scoreIA}" /></td>
																		<td><c:out value="${d.scoreTotal}" /></td>
																	</c:when>
																	<c:otherwise>
																		<td>&nbsp;</td>
																		<td>&nbsp;</td>
																		<td>&nbsp;</td>
																	</c:otherwise>
																</c:choose>
																<td><c:out value="${d.status}" /></td>
																<td><c:choose>
																		<c:when
																			test="${d.status == RemarksGradeBean.ATTEMPTED || d.status == RemarksGradeBean.CC}">
																			<c:if test="${d.pass == Boolean.TRUE}">
																				<c:out value="Pass" />
																			</c:if>
																			<c:if test="${d.pass == Boolean.FALSE}">
																				<h2>
																					<c:out value="Fail" />
																				</h2>
																			</c:if>
																		</c:when>
																		<c:otherwise>&nbsp;</c:otherwise>
																	</c:choose></td>
																<td><c:out value="${d.remarks}" /></td>
																<td><c:out value="${d.grade}" /></td>
																<td><c:out value="${d.failReason}" /></td>
															</tr>
														</c:forEach>
													</tbody>
												</table>
											</div>
										</div>
									</c:if>
									<br>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />
</body>
</html>