<!DOCTYPE html>
<!--[if lt IE 7]>	<html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>		<html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>		<html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.EMBABatchSubjectBean"%>

<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.List"%>
<%@page import="com.nmims.beans.MettlResponseBean"%>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
	
<jsp:include page="jscss.jsp">
	<jsp:param value="Insert AB Records for Online Exam" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> Insert Project AB Records for EMBA Online Exam </legend>
			</div>

			<%@ include file="messages.jsp"%>

			<form:form action="searchProjectABRecordsToInsertMBAWX" method="post"
				modelAttribute="searchBean">

				<fieldset>
					<div class="panel-body">
						<div class="col-md-6 column">
							<div class="form-group">
								<label>Select Batch</label> <select class="form-control p-2"
									id="batchId" onchange="batchIdChanged()" required
									name="batchId">
									<option value="">Select Batch</option>
									<%
										//Loop and add options, add selected to the selected option
											for (EMBABatchSubjectBean batchDetails : (List<EMBABatchSubjectBean>) request.getSession()
													.getAttribute("batchListMBAWX")) {
									%>
									<option value="<%=batchDetails.getBatchId()%>"><%=batchDetails.getBatchName()%></option>
									<%
										}
									%>
								</select>
							</div>
							<label>Select Subject</label>
							<div class="form-group">
								<select class="form-control p-2" id="timebound_id"
									name="timebound_id" required disabled>
									<option>Select Subject</option>
								</select>
							</div>
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="searchProjectABRecordsToInsertMBAWX">Search</button>
								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>

					</div>
				</fieldset>
			</form:form>
			<c:choose>
				<c:when test="${rowCount > 0}">
					<div class="panel-body">
						<legend>
							&nbsp;Absent Records<font size="2px"> (${rowCount} Records
								Found) &nbsp; <a href="downloadABReportMBAWX">Download AB
									report to verify before insertion</a>
							</font>
						</legend>
						<a class="btn btn-large btn-primary" href="insertABReportMBAWX">Insert
							AB Records</a>
					</div>
				</c:when>
			</c:choose>
		</div>
	</section>

	<jsp:include page="footer.jsp" />
	<script type="text/javascript">

		var batchesAndSubjects = <%=(String) request.getSession().getAttribute("batchAndSubjectListMBAWX")%>;

		function batchIdChanged(){

			$('#timebound_id')
				.empty()
				.append('<option value="">Select Subject</option>');
			
			if( $("#batchId").val() ){

				$("#timebound_id").attr("disabled", false);

				let selectedIndex = $("#batchId").val();
				batchesAndSubjects[selectedIndex].forEach((subject) => {
					console.log(subject);
					$('#timebound_id').append('<option value="' + subject.timeboundId + '">' + subject.subjectName + '</option>');
				})
				
			} else {
				$("#timebound_id").attr("disabled", true);
			}
		}

		$( document ).ready(function() {
		<%MettlResponseBean searchBean = (MettlResponseBean) request.getSession().getAttribute("searchBean");
			if (searchBean != null) {
				String selectedBatch = searchBean.getBatchId();
				if (selectedBatch.equals("0")) {
					selectedBatch = "";
				}
				String selectedSubject = searchBean.getTimebound_id();
				if (StringUtils.isBlank(selectedSubject)) {
					selectedSubject = "";
				}%>
				$("#batchId").val('<%=selectedBatch%>');
				batchIdChanged();
				$("#timebound_id").val('<%= selectedSubject%>');
		<%
			}
		%>
		});
	</script>
</body>
</html>
