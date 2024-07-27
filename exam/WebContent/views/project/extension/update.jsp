<!DOCTYPE html>

<%@page import="com.nmims.beans.MBACentersBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Update Module Extension" name="title" />
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

			<form:form modelAttribute="projectExtension" method="post">
				<div class="panel-body">
					<form:hidden path="id" id="id" />
					
					<div class="col-md-5 column">
						<div class="form-group">
							<label for="subject">Subject</label>
							<form:input path="subject" id="subject" readonly="true"/>
						</div>
					</div>
					<div class="col-md-5 column">
						<div class="form-group">
							<label for="sapid">Sapid</label>
							<form:input path="sapid" id="sapid" readonly="true"/>
						</div>
					</div>
					<div class="col-md-5 column">
						<div class="form-group">
							<label for="sapid">Student Name</label>
							<input value = "${ projectExtension.firstname } ${ projectExtension.lastname }" readonly="true"/>
						</div>
					</div>
					<br/>
					<div class="clearfix">
						
					</div>
					<div class="col-md-5 column">
						<div class="form-group">
							<label>Extension Type</label>
							<form:select id="moduleType" path="moduleType" class="form-control" required="required">
								<form:option value="">Select Extension Type</form:option>
								<form:option value="Title Selection">Title Selection</form:option>
								<form:option value="SOP">SOP</form:option>
								<form:option value="Project Submission">Project Submission</form:option>
								<form:option value="Synopsis">Synopsis</form:option>
								<form:option value="Viva">Viva</form:option>
							</form:select>
						</div>
					</div>
					<div class="col-md-6">
						<div class="form-group">
							<label>Extension End Date</label>
							<form:input id="endDate" path="endDate" value="${projectExtension.endDate}" type="datetime-local" placeholder="endDate" class="form-control" required="required"/>
						</div>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<a class="btn btn-large btn-danger" href="projectModuleExtendedForm">
							Cancel
						</a>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateProjectExtension">
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
