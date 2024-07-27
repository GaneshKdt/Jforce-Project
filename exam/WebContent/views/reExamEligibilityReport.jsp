<!DOCTYPE html>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value=" Re Exam Eligibility Report" name="title" />
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
	<%@ include file="header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<fieldset class="row"><legend> Re Exam Eligibility Report</legend></fieldset>
			<%@ include file="messages.jsp"%>

			<form:form modelAttribute="searchBean" method="post" enctype="multipart/form-data">
				<div class="panel-body">
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="productType">Product Type</label>
							<form:select id="examMonth" path="productType" class="form-control" required="required">
								<form:option value="">Select Product Type</form:option>
								<form:options items="${productTypeList}" />
							</form:select>
						</div>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-2">
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="reExamEligibilityReport">
							Search
						</button>
					</div>
					<c:if test = "${ showDownloadButton }">
						<div class="col-md-1">
							<a href = "downloadReExamEligibilityReport" id="download" name="download" class="btn btn-large btn-primary">
								Download
							</a>
						</div>
					</c:if>
				</div>
			</form:form>
			
			<br/>
		</div>
		<br/><br/>
	</section>
	<jsp:include page="footer.jsp" />
</body>
</html>
