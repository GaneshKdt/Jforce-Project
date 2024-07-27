<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html class="no-js"> <!--<![endif]-->
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form" %>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.20/css/jquery.dataTables.css">
<jsp:include page="../jscss.jsp">
<jsp:param value="Update Exam Booking Records" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Update Exam Booking Records</legend></div>
        <%@ include file="../messages.jsp"%>
        
		<div id="responseMsg"></div>
		
		<%-- <form:form action= "uploadExamBookingData" modelAttribute="studentMarks"> --%>
		<div class="row">
			<div class="col-md-3">
				<div class="form-group">
				<label >Exam Year</label>
						<select id="year" onchange="callExamCenter()">
							<option value="">Select Exam Year</option>
							<c:forEach var="examYear" items="${examYearList}">
						         <option value="<c:out value="${examYear}"/>"><c:out value="${examYear}"/></option>
				            </c:forEach>		
						</select>
				</div>
			</div>
			<div class="col-md-3">
				<div class="form-group">
				<label >Exam Month</label>
						<select id="month" onchange="callExamCenter()" >
							<option value="">Select Exam Month</option>
							<c:forEach var="examMonth" items="${examMonthList}">
						    	<option value="<c:out value="${examMonth}"/>"> <c:out value="${examMonth}"/> </option>
				            </c:forEach>
						</select>
				</div>
			</div>
			
			<div class="col-md-3">
				<div class="form-group">
				<label >Center</label>
						<select id="centerDD" >
							<option value="">Select Center</option>
						</select>
				</div>
			</div>
			<div class="col-md-3">
				<div class="form-group">
				<label >Exam Dates</label>
						<select id="ExamDateDD" >
							<option value="">Select Exam Date</option>
						</select>
				</div>
			</div>
			
			<div class="col-md-3">
				<div class="form-group">
				<label >Exam Time</label>
						<select id="examTimeDD" >
							<option value="">Select Exam Time</option>
						</select>
				</div>
			</div>
			
		
		</div>	
		
		<div class="row">
			<div class="col-md-3">
				<label >SapID</label> 
				<input type="text" id="sapid" class="form-control"  placeholder="Enter SapId...." title="Type Student Sapid" onchange="checkSpecialCharInSapid()" />
			</div>
			<div class="col-md-3">
				<div class="form-group">
				<label >Portal Status</label>
						<select id="testTakenId" >
							<option value="">Select Test Taken</option>
							<option value="Not Attempted">Not Started</option>
							<option value="Portal Started">Portal Started</option>
						</select>
				</div>
			</div>
			<div class="col-md-3">
				<br>
				<input  type="button" class="btn btn-large btn-primary" value="search" id="btnSearch" onclick="displayTCSExamBookingData(1,0)" />
				
			</div>
		</div>
		<div class="row" style="margin-top: 5px;">
			<div class="col-md-3" id="fileUpdload">
				<form method="POST" enctype="multipart/form-data" id="fileUploadForm">
				<label>Upload File For Excel Bulk Update</label> 
       			<input class="form-control" id="fileId" type="file" name="fileData">
       			</form>
			</div>
			
			<div id="downloadBtn" class="col-md-3" style="display:none;">
			<br>
			<a  href="downloadExamBookingData"  class="btn btn-large btn-primary"  id="btnDownload"   > Download Excel</a>
			</div>
			
			
		</div>
		<br><br>
		
			<div class="panel-body" style="display:none;" id="searchBody">
			<div class="row">
					<div class="col-sm-4 ">
						<label>Total Records Found : <span id="totalCount"></span></label>
					</div>
				<div class="col-sm-1 col-sm-offset-11" >
						<button class="btn btn-sm btn-default" id="clearAllMulipleUpdateBtn"  onclick="clearAllMulipleUpdate();">Clear</button>
				</div>	
				<div class="col-sm-1 ">
						<button class="btn btn-sm btn-primary" id="multipleEdit" disabled="disabled" onclick="toggleMultipleUpdateModel();"><span id="selectedCount"></span> Multiple Edit</button>
				</div>
				
			
			</div>
			<div class="table-responsive">
			
					<table class="table table-striped table-hover dataTables"   style="font-size:12px;">
						<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Unique Request Id</th>
								<th>SapId</th>
								<th>Student Name</th>
								<th>Password</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Program</th>
								<th>Subject</th>
								<th>Subject Code</th>
								<th>LC</th>
								<th>IC</th>
								<th>Exam Center</th>
								<th style="white-space: nowrap;padding-right: 50px;">Exam Date</th>
								<th style="white-space: nowrap;padding-right: 50px;">Exam Time</th>
								<th>Registered Email Id</th>
								<th>Portal Status</th>
								<th>Join Link</th>
								<th>Email Body</th>
								<th>Demo Exam Log</th>
								<th>Mettl Status </th>
								<th><input  type="checkbox" id="selectAllCheckbox" name="selectAllCheckbox"  onclick="onClickSelectAllCheckBox();" style="width: 15px; height: 30px;"> </th>								
								<th>Action</th>
							</tr>
						</thead>
						<tbody id ="testTable">
					
						</tbody>
					</table>
					<!-- <div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="uploadExamBookingData">Insert</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
					</div> -->
			
			<!-- Start Pagination Code -->	
				<br>
				<div id="paginationPanel"></div>
					
			</div>
			
			</div>
		
		<%-- </form:form> --%>
		
	
	
<%-- 	<c:choose> --%>
<%-- 	<c:when test="${rowCount > 0}"> --%>
<!-- 			<div class="panel-body"> -->
<%-- 			<legend>&nbsp;Absent Records<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadABReport">Download AB report to verify before insertion</a></font></legend> --%>
<!-- 			<a  class="btn btn-large btn-primary"  href="/exam/admin/insertABReport">Insert AB Records</a>			 -->
<!-- 			</div> -->
<%-- 	</c:when> --%>
<%-- 	</c:choose> --%>
	</div>
	</section>


	<!-- Modal 1 Start -->
	<div class="modal fade" id="previewModal" role="dialog" >
     		<div class="modal-dialog modal-lg">
     			<div class="modal-content">
     				<div class="modal-header">
     				<button type="button" class="close" data-dismiss="modal">&times;</button>
     				<h4 class="modal-title">Total : <span id="total"></span></h4>
     				</div>
     				<div class="modal-body">
     				<div class="panel-body">
     				<div class="row">
     					<div class="table-responsive col-sm-18">
     						<table class="table table-striped table-hover dataTables" >
     							<thead>
     								<tr>
     								<th>Sr. No.</th>
									<th>Unique Request Id</th>
									<th>SapId</th>
									<th>Student Name</th>
									
									<th>Exam Year</th>
									<th>Exam Month</th>
									<th>Program</th>
									<th>Subject</th>
									<th>Center </th>
									<th style="white-space: nowrap;padding-right: 50px;">Exam Date</th>
									<th style="white-space: nowrap;padding-right: 50px;">Exam Time</th>
									<th>Action</th>
     								</tr>
     							</thead>
     							<tbody id="previewExcelBody">
     							
     							</tbody>
     						</table>
     					</div>
     				</div>
     				</div>
     				</div>
     				<div class="modal-footer">
     					<button type="button" class="btn btn-large btn-primary" id="bulkUploadExcel" disabled="true" >Bulk Update Upload Excel</button>
     					<button type="button" class="btn btn-danger" id="bulkUploadCancel" data-dismiss="modal">Cancel</button>
     				</div>
 				</div>
     		</div>
     	</div>
	
	
	<!-- Modal 1 End -->
	
	
	<!-- Modal 2 Start -->
	<div class="modal fade" id="mulitpleUpdateModal" role="dialog" >
     		<div class="modal-dialog modal-lg">
     			<div class="modal-content">
     				<div class="modal-header">
     				<button type="button" class="close" data-dismiss="modal">&times;</button>
     				<h4 class="modal-title">Total : <span id="multipleUpdateTotal"></span></h4>
     				</div>
     				<div class="modal-body">
     				
     				<div class="row">
     					<div class="col-md-6 col-md-offset-2">
							<div class="form-group">
							<label >Exam Dates</label>
									<select id="mutlipleUpdateExamDateDD" >
										<option value="">Select Exam Date</option>
									</select>
							</div>
						</div>
						
						<div class="col-md-6 col-md-offset-1">
							<div class="form-group">
							<label >Exam Time</label>
									<select id="mutlipleUpdateExamTimeDD" >
										<option value="">Select Exam Time</option>
									</select>
							</div>
						</div>
     				</div>
     				
     				</div>
     				<div class="modal-footer">
     					<button type="button" class="btn btn-large btn-primary" id="multipleUpdateBtn" disabled="true" >Update</button>
     					<button type="button" class="btn btn-danger" id="multipleUpdateCancel" data-dismiss="modal">Cancel</button>
     				</div>
 				</div>
     		</div>
     	</div>
	
	
	<!-- Modal 2 End -->
	
	
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



	  <jsp:include page="../footer.jsp" />
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/updateTcsExamBooking.js"></script>

</body>


</html>