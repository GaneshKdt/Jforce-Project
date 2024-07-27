<!DOCTYPE html>

<%@page import="com.nmims.beans.ProjectTitle"%>
<%@page import="com.nmims.beans.MBACentersBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Add Project Titles" name="title" />
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
		
		td, th {
			font-size : larger
		}
		
	</style>
</head>
<body class="inside">
	<%@ include file="../header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<fieldset class="row"><legend>Level Based Project Configuration Checklist</legend></fieldset>
			<%@ include file="../messages.jsp"%>
				<div class="panel-body">
							
				<div class="col-md-16 column">

					<div class="table-responsive">
						<table class="table table-striped" style="font-size: 12px">
							<thead>
								<tr>
									<th>SR. No</th>
									<th>Module</th>
									<th>Description</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>1</td>
									<td><a href="projectConfigurationForm">Configuration</a></td>
									<td>Add/Edit Level Based Project Configurations</td>
								</tr>
								<tr>
									<td>2</td>
									<td><a href="addProjectTitlesConfigForm">Project Titles Configuration</a></td>
									<td>Program-Subject-Wise Live Settings for Project Titles</td>
								</tr>
								<tr>
									<td>3</td>
									<td><a href="addProjectTitlesForm">Manage Project Titles</a></td>
									<td>Add/Edit Titles and set them Active/Inactive as per Program/Program Structure/Subject</td>
								</tr>
								<tr>
									<td>4</td>
									<td><a href="studentAndGuidMappingForm">Student-Guide Mapping</a></td>
									<td>Map Students and Faculty</td>
								</tr>
								<tr>
									<td>5</td>
									<td><a href="levelBasedSOPConfigForm">SOP Configuration Form</a></td>
									<td>Configure SOP Submission</td>
								</tr>
								<tr>
									<td>6</td>
									<td><a href="levelBasedSynopsisConfigForm">Synopsis Configuration Form</a></td>
									<td>Configure Synopsis Submission</td>
								</tr>
								<tr>
									<td>7</td>
									<td><a href="#">Viva</a></td>
									<td>Configure Viva</td>
								</tr>
								<tr>
									<td>8</td>
									<td><a href="projectModuleExtendedForm">Single Student Level-Based Project Extension</a></td>
									<td>Extend Submission Date for a Single Student for a specific level</td>
								</tr>
								<tr>
									<td>9</td>
									<td><a href="downloadEligibleStudentsExcelReport">Download Eligible Students Report</a></td>
									<td>Eligible Students List for PD-WM Module-4-Project</td>
								</tr>
								<tr>
									<td>10</td>
									<td><a href="downloadSOPorSynopsisSubmitted&Transaction_or_SynopsisScoreReportForm">SOP/Synopsis Submitted and Transaction or Synopsis Score Report Form</a></td>
									<td>SOP/Synopsis Submitted and Transaction Report or Synopsis Evaluated Score Report Form</td>
								</tr>
								<!-- <tr>
									<td>7</td>
									<td><a href="#">Configuration</a></td>
								</tr>
								<tr>
									<td>4</td>
									<td><a href="#">Configuration</a></td>
								</tr> -->
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<br/><br/>
	</section>

	<jsp:include page="../footer.jsp" />
</body>
</html>
