<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js">
<!--<![endif]-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<jsp:include page="jscss.jsp">
	<jsp:param value="MBA-WX JoinLink And Exam Status Dashboard" name="title" />
</jsp:include>
<head>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.13.6/css/jquery.dataTables.min.css">
<style>
  .mandatory:after {
    content:" *";
    color: red;
  }
</style>	
</head>
<body class="inside">
	<%@ include file="header.jsp"%>
	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>MBA-WX JoinLink And Exam Status Dashboard</legend>
			</div>
			<form method="post">
				<div class="row">
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1" class="mandatory">Program Type</label> 
							<select name="programType" id="programType" class="form-control" required>
								<option value="">-- Program Type --</option>
								<option value="M.Sc. (AI & ML Ops)">M.Sc. (AI & ML Ops)</option>
								<option value="M.Sc. (AI)">M.Sc. (AI)</option>
								<option value="MBA - WX">MBA - WX</option>
								<option value="Modular PD-DM">Modular PD-DM</option>
							</select>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1" class="mandatory">Exam Type</label> 
							<select name="examType" id="examType" class="form-control "  required>
								<option value="">-- Exam Type --</option>
								<option value="40">Regular Exam</option>
								<option value="100">Re-Exam(100 marks)</option>
							</select>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1" class="mandatory">Select Exam Date</label>
							  <input type="date" name="examDate" id="examDate" class="form-control " onChange='examTimeListOnExamDate(event)' onclick='examTimeListOnExamDate(event)' required>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1" class="mandatory">Select Exam Time</label>
							 <select name="examTime" id="examTime" class="form-control " required>
								<option disabled selected value="">-- select exam time --</option>
							</select>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group">
							<label for="sel1">Sapid(Optional)</label>
							  <input type="text" name="sapid" id="sapid" class="form-control" placeholder="Sapid">
						</div>
					</div>
				</div>
				<button id="submitBtn" class="btn btn-primary" type="submit" onclick="getExamData(event)">Search</button>
				<div id='theImg' style="display:none">
					<img  src='/exam/resources_2015/gifs/loading-29.gif' alt="Loading..." style="height:40px" />
				</div>
			</form>
		</div>
		<div class="panel-body" style="display:none;" id="searchBody">
			<div class="row">
				<div class="col-sm-4 ">
					<label>Total Records Found : <span id="totalCount"></span></label>
				</div>
			</div>
			<div class="table-responsive">

				<table id="tableId" class="table table-striped table-hover dataTables"
					style="font-size: 12px;">
					<thead>
						<tr>
							<th>Sr. No.</th>
							<th>SapId</th>
							<th>Student Name</th>
							<th>Subject</th>
							<th style="white-space: nowrap; padding-right: 50px;">ExamTime</th>
							<th>Registered Email Id</th>
							<th>Portal Status</th>
							<th>Join Link</th>
							<th>Email Body</th>
							<th>Demo Exam Log</th>
							<th>Mettl Status</th>
						</tr>
					</thead>
					<tbody id="testTable">

					</tbody>
				</table>
				<br>

			</div>
		</div>
	</section>	
	
	<!-- Modal 3 Start -->
	<div class="modal fade" id="demoExamLogModal" role="dialog" >
     		<div class="modal-dialog modal-lg">
     			<div class="modal-content">
     				<div class="modal-header">
     				<button type="button" class="close" data-dismiss="modal">&times;</button>
     				<h4 class="modal-title">Total : <span id="demoExamCount"></span></h4>
     				</div>
     				<div class="modal-body">
     				<div class="panel-body">
     				<div class="row">
     					<div class="table-responsive col-sm-18">
     						<table class="table table-striped table-hover dataTables" >
     							<thead>
     								<tr>
     								
     								<th>Sr. No.</th>								
									<th>SapId</th>
									<th>Demo Exam Id</th>
									<th>Access Key</th>
									<th>Start Time</th>
									<th>End Time</th>
									<th>Mark Attend</th>
								
     								</tr>
     							</thead>
     							<tbody id="demoExamLogModalBody">
     							
     							</tbody>
     						</table>
     					</div>
     				</div>
     				</div>
     				</div>
     				<div class="modal-footer">
     					<button type="button" class="btn btn-danger" id="demoExamLogModalClose" data-dismiss="modal">Close</button>
     				</div>
 				</div>
     		</div>
     	</div>
	<!-- Modal 3 End -->
	
	<!-- Modal 4 Start -->
	<div class="modal fade" id="testTakenModal" role="dialog" >
     		<div class="modal-dialog modal-lg">
     			<div class="modal-content">
     				<div class="modal-header">
     				<button type="button" class="close" data-dismiss="modal">&times;</button>
     				</div>
     				<div class="modal-body">
     				<div class="panel-body">
     				<div class="row">
     					<div class="table-responsive col-sm-18">
     						<table class="table table-striped table-hover dataTables" >
     							<thead>
     								<tr>
     																
									<th>SapId</th>
									<th>Student Name</th>
									<th>Test Taken</th>
									
									<th>Start Details</th>
									<th>Finish Details</th>
									<th>Finish Node</th>
									<th>Graded Details</th>
									<th>Resume Details</th>
     								</tr>
     							</thead>
     							<tbody id="testTakenModalBody">
     							
     							</tbody>
     						</table>
     					</div>
     				</div>
     				</div>
     				</div>
     				<div class="modal-footer">
     					<button type="button" class="btn btn-danger" id="testTakenModalClose" data-dismiss="modal">Close</button>
     				</div>
 				</div>
     		</div>
     	</div>
	
	
	<!-- Modal 4 End -->
	
	<!-- Modal 5 Start -->
	<div class="modal fade" id="studentJsonModal" role="dialog" >
     		<div class="modal-dialog modal-lg">
     			<div class="modal-content">
     				<div class="modal-header">
     				<button type="button" class="close" data-dismiss="modal">&times;</button>
     				
     				</div>
     				<div class="modal-body">
     				<div class="panel-body">
     				
     				<pre id ="displayStudentJson"></pre>
     				
     				</div>
     				</div>
     				<div class="modal-footer">
     					<button type="button" class="btn btn-danger" id="testTakenModalClose" data-dismiss="modal">Close</button>
     				</div>
 				</div>
     		</div>
     	</div>
	
	
	<!-- Modal 5 End -->
	
	
	 <jsp:include page="footer.jsp" />
	 <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>
	<script src="https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	<script src="https://d1h28kdwpyiu28.cloudfront.net/assets/js/sendExamLinkFromDashboardMbaWx.js"></script>
	 
</body>
</html>