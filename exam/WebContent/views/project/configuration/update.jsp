<!DOCTYPE html>

<%@page import="com.nmims.beans.MBACentersBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Update Configuration" name="title" />
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
				<legend>Update Title</legend>
			</fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="projectConfiguration" method="post">
				<div class="panel-body">
					<form:hidden path="id" id="id" />
					<div class="col-md-5 column">
						<div class="form-group">
							<label for="name">Program Code</label>
							<form:input path="programCode" id="programCode" readonly="true"/>
						</div>
					</div>
					
					<div class="col-md-5 column">
						<div class="form-group">
							<label for="consumerType">Consumer Type</label>
							<form:input path="consumerType" id="consumerType" readonly="true"/>
						</div>
					</div>
					
					<div class="col-md-5 column">
						<div class="form-group">
							<label for="programStructure">Program Structure</label>
							<form:input path="programStructure" id="programStructure" readonly="true"/>
						</div>
					</div>
					
					<div class="col-md-5 column">
						<div class="form-group">
							<label for="subject">Subject</label>
							<form:input path="subject" id="subject" readonly="true"/>
						</div>
					</div>
					<br/>
					<div class="clearfix">
						
					</div>
					<div class="col-md-5 column">
						<div class="form-group">
							<label >Has Title</label>
							<form:select id="hasTitle" path="hasTitle" type="text" placeholder="Exam Month" class="form-control" required="required" itemValue="${filesSet.examMonth}">
								<form:option value="">Select Has Title</form:option>
								<form:option value="Y">Y</form:option>
								<form:option value="N">N</form:option>
							</form:select>
						</div>
					</div>
					
					<div class="col-md-5 column">
						<div class="form-group">
							<label >Has SOP</label>
							<form:select id="hasSOP" path="hasSOP" type="text" placeholder="Exam Month" class="form-control" required="required" itemValue="${filesSet.examMonth}">
								<form:option value="">Select Has SOP</form:option>
								<form:option value="Y">Y</form:option>
								<form:option value="N">N</form:option>
							</form:select>
						</div>
					</div>
					<div class="col-md-5 column">
						<div class="form-group">
							<label >Has Viva</label>
							<form:select id="hasViva" path="hasViva" type="text" placeholder="Exam Month" class="form-control" required="required" itemValue="${filesSet.examMonth}">
								<form:option value="">Select Has Viva</form:option>
								<form:option value="Y">Y</form:option>
								<form:option value="N">N</form:option>
							</form:select>
						</div>
					</div>
					<div class="col-md-5 column">
						<div class="form-group">
							<label >Has Synopsis</label>
							<form:select id="hasSynopsis" path="hasSynopsis" type="text" placeholder="Exam Month" class="form-control" required="required" itemValue="${filesSet.examMonth}">
								<form:option value="">Select Has Synopsis</form:option>
								<form:option value="Y">Y</form:option>
								<form:option value="N">N</form:option>
							</form:select>
						</div>
					</div>
					<div class="col-md-5 column">
						<div class="form-group">
							<label >Has Submission</label>
							<form:select id="hasSubmission" path="hasSubmission" type="text" placeholder="Exam Month" class="form-control" required="required" itemValue="${filesSet.examMonth}">
								<form:option value="">Select Has Submission</form:option>
								<form:option value="Y">Y</form:option>
								<form:option value="N">N</form:option>
							</form:select>
						</div>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<a class="btn btn-large btn-danger" href="addProjectTitlesForm">
							Cancel
						</a>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateProjectConfiguration">
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
