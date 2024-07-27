<!DOCTYPE html>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.CaseStudyExamBean"%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Upload Case Study" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Upload Case Study</legend>
			</div>
			<%@ include file="../messages.jsp"%>
			<div class="panel-body clearfix">
				<form:form action="uploadCaseStudyFiles" method="post" enctype="multipart/form-data"
					modelAttribute="csBean">
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
										<label for="startDate">Start Date</label>
										<form:input path="startDate" id="startDate" type="datetime-local" />
									</div>
								</div>
									
								<div class="col-md-4 column">
									<div class="form-group">
										<label for="endDate">End Date</label>
										<form:input path="endDate" id="endDate" type="datetime-local" />
									</div>
								</div>
							</div>
							
							<legend>
								Select Files to Upload <font size="5px"></font>
							</legend>
							<% for(int i = 0 ; i < 3 ; i++) {%>
							<div class="row">
								<div class="col-md-4 column">
									<div class="form-group">
										<input id="fileData" type="file"
											name="caseStudyFiles[<%=i%>].fileData">
									</div>
								</div>
							 <div class="col-md-4 column">
									<div class="form-group">
									<select id="topic" type="text"
											 
											name="caseStudyFiles[<%=i%>].topic">
											<option value="">Select Topic</option>
							<c:forEach var="topic" items="${caseStudyTopicList}">
				                <option>
				                  <c:out value="${topic}" />
				                </option>
				            </c:forEach>
										   
									</select>
									</div>
								</div> 
								<div class="col-md-4 column">
									<div class="form-group">
										<select name="caseStudyFiles[<%=i%>].program">
											<option value="">Select Program</option>
											<option value="EPBM">EPBM</option>
										</select>
									</div>
								</div>
							</div>
							<%} %>
							<div class="col-md-6 column">
								<div class="form-group">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary"
										formaction="uploadCaseStudyFiles">Upload</button>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
						</div>
					</fieldset>
				</form:form>
			</div>
		</div>
	</section>
	<jsp:include page="../footer.jsp" />
</body>
</html>
