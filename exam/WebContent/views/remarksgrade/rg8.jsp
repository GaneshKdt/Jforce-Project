<!DOCTYPE html>

<%@page import="com.nmims.beans.RemarksGradeBean"%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="RemarksGrade 8" name="title" />
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
						<h2 class="red text-capitalize">RemarksGrade : Make Live</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<!-- section class="content-container">  -->

							<div class="container-fluid customTheme">
								<div class="row clearfix">
									<%--@ include file="../adminCommon/messages.jsp"--%><%@ include
										file="../adminCommon/newmessages.jsp"%>
									<div class="col-md-6 column">

										<legend>&nbsp;Make Live (Assignment Marks)</legend>
										<form:form method="post" modelAttribute="remarksGradeBean">
											<fieldset>
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
													<form:select id="assignmentMarksLive"
														path="assignmentMarksLive" type="text"
														placeholder="Make Live" class="form-control"
														itemValue="${remarksGradeBean.assignmentMarksLive}"
														required="required">
														<form:option value="">Select to make live</form:option>
														<form:option value="${RemarksGradeBean.ASSIGNMENT_RESULT_LIVE}">Yes</form:option>
														<form:option value="${RemarksGradeBean.ASSIGNMENT_RESULT_NOTLIVE}">No</form:option>
													</form:select>
												</div>
												<div class="form-group">
													<label class="control-label" for="submit"></label>
													<div class="controls">
														<button id="submit" name="submit"
															class="btn btn-sm btn-primary"
															formaction="changeResultsLiveRG8">Process</button>
														<button id="cancel" name="cancel"
															class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home"
															formnovalidate="formnovalidate">Cancel</button>
													</div>
												</div>
											</fieldset>
										</form:form>
									</div>
									<c:if test="${rowCount > 0}">
										<div class="col-md-12 column">
											<legend>&nbsp;Current Status</legend>
											<table class="table table-striped" style="font-size: 12px">
												<thead>
													<tr>
														<th>Sr. No.</th>
														<th>Exam Year</th>
														<th>Exam Month</th>
														<th>Results Live</th>
														<th>Reval Live</th>
													</tr>
												</thead>
												<tbody>
												<tr>
														<td>1</td>
														<td><c:out value="${remarksGradeBean.year}" /></td>
														<td><c:out value="${remarksGradeBean.month}" /></td>
														<td><c:if test="${isSuccess == true}">
																<c:if
																	test="${remarksGradeBean.assignmentMarksLive == 'Y'}">Yes</c:if>
																<c:if
																	test="${remarksGradeBean.assignmentMarksLive == 'N'}">No</c:if>
															</c:if>
															<c:if test="${isSuccess == false}">Error</c:if></td>
														<td>&nbsp;</td>
													</tr>
												</tbody>
											</table>
										</div>
									</c:if>
								</div>
							</div>
							<!-- /section>  -->
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />
</body>
</html>