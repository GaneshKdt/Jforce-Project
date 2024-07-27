<!DOCTYPE html>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.CaseStudyExamBean"%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Search and Download Submitted Case Study" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Search/Download Submitted Case Study</legend>
			</div>
			<%@ include file="../messages.jsp"%>
			<div class="panel-body clearfix">
				<form:form action="" method="post" modelAttribute="csBean">
					<fieldset>
						<div class="col-md-18 column">
							<div class="row">
								<div class="col-md-4 column">
									<div class="form-group">
									<label for="batchYear">Batch Year</label>
										 <form:select id="batchYear" path="batchYear" type="text"
											placeholder="Year" class="form-control" required="required"
											itemValue="${csBean.batchYear}">
											<form:option value="">Select Batch Year</form:option>
											<form:options items="${yearList}" /> 
										</form:select> 
									</div>
								</div>
								<div class="col-md-4 column">
									<div class="form-group">
									<label for="batchMonth">Batch Month</label>
										<form:select id="batchMonth" path="batchMonth" type="text"
											placeholder="Month" class="form-control" required="required"
											itemValue="${csBean.batchMonth}">
											<form:option value="">Select Batch Month</form:option>
										  	 <form:options items="${monthList}" />
										</form:select>
									</div>
								</div>
								<div class="col-md-4 column">
									<div class="form-group">
									<br>
										<button id="submit" name="submit"
										class="btn btn-small btn-primary"
										formaction="searchSubmittedCaseStudyFiles">Search</button>
									</div>
								</div>
							</div>
						</div>
						
					</fieldset>
				</form:form>
			</div>
			
			<c:if test="${submittedCaseStudySize > 0 }">
				<div class="row">
					<legend>Submitted Case Studies Found </legend>
				</div>
				<div class="panel-body clearfix">
						<table class="table table-striped" style="font-size: 12px;">
							<thead>
								 <tr>
								 	<td>Sr No</td>
								 	<td>Batch Year</td>
								 	<td>Batch Month</td>
								 	<td>SapId</td>
								 	<td>Topic Submitted</td>
								 	<td>File Download</td>
								 </tr>
							</thead>
							<tbody>
								<c:forEach var="caseBean" items="${submittedCaseStudy}" varStatus="status">
									<tr>
										<td><c:out value="${status.count}" /></td>
										<td nowrap="nowrap"><c:out value="${caseBean.batchYear}" /></td>
										<td><c:out value="${caseBean.batchMonth}" /></td>
										<td><c:out value="${caseBean.sapid}" /></td>
										<td><c:out value="${caseBean.topic}" /></td>
										<td><a href="#" onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CASESTUDY_PREVIEW_PATH')" />${caseBean.previewPath}')" /><i class="fa-solid fa-download fa-lg"></i></a></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
				</div>
			</c:if>
		</div>
	</section>
	<jsp:include page="../footer.jsp" />
</body>
</html>
