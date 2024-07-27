<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="java.util.*"%>
<%@page import="com.nmims.beans.RemarksGradeBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html lang="en">
<head>
<style>
</style>
<script>
	
</script>
<jsp:include page="../adminCommon/jscss.jsp">
	<jsp:param value="RemarksGrade 1" name="title" />
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
						<h2 class="red text-capitalize">RemarksGrade : Upload Marks
							(Assignment)</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper" style="min-height: 450px;">
							<%--@ include file="../adminCommon/messages.jsp"--%><%@ include
								file="../adminCommon/newmessages.jsp"%>

							<div class="container-fluid customTheme">
								<div class="row">
									<form:form modelAttribute="remarksGradeBean" method="post"
										enctype="multipart/form-data">
										<div class="row">
											<div class="col-md-6 column">

												<div class="form-group">
													<form:label for="fileData" path="fileData">Select file</form:label>
													<form:input path="fileData" type="file" />
												</div>
												<div class="form-group">
													<form:select id="year" path="year" type="text"
														placeholder="Year" class="form-control"
														required="required" itemValue="${remarksGradeBean.year}">
														<form:option value="">Select Exam Year</form:option>
														<form:options items="${yearList}" />
													</form:select>
												</div>

												<div class="form-group">
													<form:select id="month" path="month" type="text"
														placeholder="Month" class="form-control"
														required="required" itemValue="${remarksGradeBean.month}">
														<form:option value="">Select Exam Month</form:option>
														<form:options items="${monthList}" />
													</form:select>
												</div>
												<div class="form-group">
													<button id="submit" name="submit"
														class="btn btn-large btn-primary"
														formaction="uploadMarksRG1">Upload</button>
												</div>
											</div>

											<div class="col-md-12 column">
												<b>Format of Upload: </b><br> Student No | Student Name
												| Program | Subject | Assignment Score <br>
											</div>
										</div>

									</form:form>
									<div class="col-md-6 column">
										<a
											href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/UPLOAD_UG_REMARKGRADING_TEMPLATE.xlsx"
											target="_blank">Download Sample Template</a>
									</div>
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
</html>