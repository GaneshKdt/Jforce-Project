<!DOCTYPE html>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Upload MBA-WX Slot" name="title" />
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
				<legend>Update MBA-WX Slot</legend>
			</fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="slotBean" method="post">
				<div class="panel-body">
					<form:hidden path="slotId" id="slotId" />
					<div class="col-md-3 column">
						<div class="form-group">
							<label for="centerName">Center Name</label>
							<form:input path="centerName" id="centerName" disabled = "true" readonly = "true"/>
						</div>
					</div>
					<div class="col-md-3 column">
						<div class="form-group">
							<label for="subjectName">Subject Name</label>
							<form:input path="subjectName" id="subjectName"  disabled = "true" readonly = "true"/>
						</div>
					</div>
					<div class="col-md-3 column">
						<div class="form-group">
							<label for="examDate">Exam Date</label>
							<form:input path="examDate" id="examDate"  disabled = "true" readonly = "true"/>
						</div>
					</div>
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="examStartTime">Exam Start Time</label>
							<form:input path="examStartTime" id="examStartTime"  disabled = "true" readonly = "true"/>
						</div>
					</div>
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="examEndTime">Exam End Time</label>
							<form:input path="examEndTime" id="examEndTime"  disabled = "true" readonly = "true"/>
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="capacity">Capacity</label>
							<form:input path="capacity" id="capacity" type="number" />
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="active">Active</label>
							<form:select path="active" id="active" >
								<form:option value="Y"/>
								<form:option value="N"/>
							</form:select>
						</div>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<a class="btn btn-large btn-danger" href="slotsList_MBAWX">
							Cancel
						</a>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateSlot_MBAWX">
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
