<!DOCTYPE html>

<%@page import="com.nmims.beans.MBACentersBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Update MBA-X Time Table" name="title" />
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
				<legend>Update MBA-X Tiem Table</legend>
			</fieldset>
			<%@ include file="../../messages.jsp"%>

			<form:form modelAttribute="liveSettings" method="post">
				<div class="panel-body">
					<form:hidden path="id" id="id" />
					
					<div class="col-md-18">
					
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="type">Type</label>
								<form:input path="type" id="type" readonly="true"/>
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
								<label for="examYear">Exam Year</label>
								<form:select path="examYear" id="examYear" items="${ examYearList }"/>
							</div>
						</div>
						
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="examMonth">Exam Month</label>
								<form:select path="examMonth" id="examMonth"  items="${ examMonthList }"/>
							</div>
						</div>
						
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="acadsYear">Acad Year</label>
								<form:select path="acadsYear" id="acadsYear"  items="${ acadYearList }"/>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="acadsMonth">Acad Month</label>
								<form:select path="acadsMonth" id="acadsMonth"  items="${ acadMonthList }"/>
							</div>
						</div>
						
					</div>
					
					<div class="col-md-18">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for=startDateStr>Start Date</label>
								<form:input type="date" path="startDateStr" id="startDateStr"/>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="startTimeStr">Start Time</label>
								<form:input type="time" path="startTimeStr" id="startTimeStr"/>
							</div>
						</div>
						
						<div class="col-md-6 column">
							<div class="form-group">
								<label for=endDateStr>End Date</label>
								<form:input type="date" path="endDateStr" id="endDateStr"/>
							</div>
						</div>
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="endTimeStr">End Time</label>
								<form:input type="time" path="endTimeStr" id="endTimeStr"/>
							</div>
						</div>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<a class="btn btn-large btn-danger" href="uploadLiveSettingsForm_MBAX">
							Cancel
						</a>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateLiveSetting_MBAX">
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
