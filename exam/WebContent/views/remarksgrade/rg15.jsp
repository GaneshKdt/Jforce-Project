<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.List, java.util.ArrayList"%>
<%@page import="com.nmims.beans.RemarksGradeBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!-- views/RIANVCases.jsp -->
<!DOCTYPE html>
<html lang="en">
<head>
<style>
</style>
<script>
	
</script>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="RemarksGrade 15" name="title" />
</jsp:include>
<body>
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
						<h2 class="red text-capitalize">RemarksGrade : Eligible Student Report</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
						<div class = "js_result"></div>
							<%--@ include file="../adminCommon/messages.jsp"--%><%@ include
								file="../adminCommon/newmessages.jsp"%>
							<div class="container-fluid customTheme">
								<div class="row">
									<form:form modelAttribute="remarksGradeBean" method="post"
										enctype="multipart/form-data">
										<fieldset>
										<div class="col-md-4">
												<div class="form-group">
													<form:select id="acadYear" path="acadYear" type="text"
														placeholder="AcadYear" class="form-control"
														itemValue="${remarksGradeBean.acadYear}" required="required" disabled="true">
														<form:option value="">(*) Select Acad Year</form:option>
														<form:options items="${acadYearList}" />
													</form:select>
												</div>
												<div class="form-group">
													<form:select id="year" path="year" type="text"
														placeholder="Year" class="form-control"
														itemValue="${remarksGradeBean.year}" required="required">
														<form:option value="">(*) Select Exam Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div>
												<div class="form-group">
													<button id="submit" name="submit" formmethod="post"
														class="btn btn-large btn-primary" formaction="searchRG15ANS">Search Not Submitted</button>
													<button id="submit" name="submit" formmethod="post"
														class="btn btn-large btn-primary" formaction="searchRG15AS">Search Submitted</button>
													<button id="clear" name="clear" formmethod="post"
														formnovalidate="formnovalidate"
														class="btn btn-large btn-primary" formaction="clearRG15">Clear</button>
												</div>
											</div>
											<div class="col-md-4">
												<div class="form-group">
													<form:select id="acadMonth" path="acadMonth" type="text"
														placeholder="AcadMonth" class="form-control"
														itemValue="${remarksGradeBean.acadMonth}" required="required" disabled="true">
														<form:option value="">(*) Select Acad Month</form:option>
														<form:options items="${acadMonthList}" />
													</form:select>
												</div>
												<div class="form-group">
													<form:select id="month" path="month" type="text"
														placeholder="Month" class="form-control"
														itemValue="${remarksGradeBean.month}" required="required">
														<form:option value="">(*) Select Exam Month</form:option>
														<form:options items="${monthList}" /> %>
													</form:select>
												</div>
											</div>
										</fieldset>
									</form:form>
									<c:if test="${rowCount > 0}">
										<h2>
											Search Results <font size="2px"> (${rowCount} Records
												Found) <a id="downloadRG15Id"
												href="downloadRG15?acadyear=${acadyear}&acadmonth=${acadmonth}&eyear=${eyear}&emonth=${emonth}&assgtype=${assgtype}">
													Download to Excel ( ${assgtype} )</a>
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
														<th>Student Name</th>
														<th>Sem</th>
														<th>Subject</th>
														<th>Student Type</th>
														<th>Program Structure</th>
														<th>Program</th>
														<th>Acad Year</th>
														<th>Acad Month</th>
														<th>Exam Year</th>
														<th>Exam Month</th>
														<th>Remarks</th>
														<th>iaScore</th>
														<th>Grade</th>
													</tr>
												</thead>
												<tbody>
												<c:forEach var="d" items="${dataList}" varStatus="statusOld">
												<tr>
													<td><c:out value="${statusOld.count}" /></td>
													<td><c:out value="${d.sapid}" /></td>
													<td><c:out value="${d.name}" /></td>
													<td><c:out value="${d.sem}" /></td>
													<td><c:out value="${d.subject}" /></td>
													<td><c:out value="${d.studentType}" /></td>
													<td><c:out value="${d.programStructure}" /></td>
													<td><c:out value="${d.program}" /></td>
													<td><c:out value="${d.acadYear}" /></td>
													<td><c:out value="${d.acadMonth}" /></td>
													<td><c:out value="${d.year}" /></td>
													<td><c:out value="${d.month}" /></td>
													<td><c:out value="${d.remarks}" /></td>
													<td><c:out value="${d.scoreIA}" /></td>
													<td><c:out value="${d.grade}" /></td>
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
							<div class="clearfix"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="../adminCommon/footer.jsp" />

</body>
<script>
</script>
</html>