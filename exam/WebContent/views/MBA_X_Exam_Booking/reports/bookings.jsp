<!DOCTYPE html>

<%@page import="com.nmims.beans.MBACentersBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="MBA-X Exam Bookings Report" name="title" />
</jsp:include>

<head>
	<link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
	<style>
		.dataTables_filter > label > input{
			float:right !important;
		}
		.toggleListWell{
		cursor: pointer !important;
			margin-bottom:0px !important;
		}
		.toggleWell{
			background-color:white !important;
		}
		input[type="radio"]{
			width:auto !important;
			height:auto !important;
			
		}
		.optionsWell{
			padding:0px 10px;
		}
	</style>
</head>
<body class="inside">
	<%@ include file="../../header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<fieldset class="row"><legend>MBA-X Exam Bookings Report</legend></fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="searchBean" method="post" enctype="multipart/form-data">
				<div class="panel-body">
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="examYear">Exam Year</label>
							<form:select id="examYear" path="examYear" class="form-control" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
					</div>
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="examMonth">Exam Month</label>
							<form:select id="examMonth" path="examMonth" class="form-control" required="required">
								<form:option value="">Select Month</form:option>
								<form:options items="${monthList}" />
							</form:select>
						</div>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="examBookingReport_MBAX">
							Search
						</button>
					</div>
				</div>
			</form:form>
			
			<br/>
			
			<c:if test = "${ showNumRecords }">
				<fieldset class="row"><legend>Found ${ numRecords } Records</legend></fieldset>
			</c:if>
			<c:if test = "${ showDownloadButton }">
				<a href = "downloadExamBookingReport_MBAX" id="download" name="download" class="btn btn-large btn-primary">
					Download Booking Report
				</a>
			</c:if>
		</div>
		<br/><br/>
	</section>
	<jsp:include page="../../footer.jsp" />
</body>
</html>
