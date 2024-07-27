<!DOCTYPE html>

<%@page import="com.nmims.beans.MBACentersBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Update MBA-WX Time Table" name="title" />
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
		input[readonly] {
		    background-color: #bfbebe !important;
    		cursor: not-allowed;
		}
	</style>
</head>
<body class="inside">
	<%@ include file="../../header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<fieldset class="row">
				<legend>Update MBA-WX Tiem Table</legend>
			</fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="timeTableBean" method="post">
				<div class="panel-body">
					<form:hidden path="timeTableId" id="timeTableId" />
					
					<div class="col-md-18">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="program">Program Type</label>
								<form:input path="program" id="program" readonly="true"/>
							</div>
						</div>

						<div class="col-md-6 column">
							<div class="form-group">
								<label for="programStructure">Program Structure</label>
								<form:input path="programStructure" id="programStructure" readonly="true"/>
							</div>
						</div>
						
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="name">Subject</label>
								<form:input path="subjectName" id="subjectName" readonly="true"/>
							</div>
						</div>
						
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="examYear">Exam Year</label>
								<form:input path="examYear" id="examYear" readonly="true"/>
							</div>
						</div>
						
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="examMonth">Exam Month</label>
								<form:input path="examMonth" id="examMonth" readonly="true"/>
							</div>
						</div>
						
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="term">Term</label>
								<form:input path="term" id="term" readonly="true"/>
							</div>
						</div>
					</div>
					<div class="col-md-18">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for=examDate>Date</label>
								<form:input type="date" path="examDate" id="examDate"/>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="examStartTime">Start Time</label>
								<form:input type="time" path="examStartTime" id="examStartTime"/>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="examEndTime">End Time</label>
								<form:input type="time" path="examEndTime" id="examEndTime"/>
							</div>
						</div>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<a class="btn btn-large btn-danger" href="uploadCentersForm_MBAWX">
							Cancel
						</a>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateTimeTable_MBAWX">
							Update
						</button>
					</div>
				</div>
			</form:form>
		</div>
		<br/><br/>
	</section>
	<jsp:include page="../../footer.jsp" />
</body>
</html>
