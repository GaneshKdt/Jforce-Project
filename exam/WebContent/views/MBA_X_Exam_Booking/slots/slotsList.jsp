<!DOCTYPE html>

<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Upload MBA-X Slot list" name="title" />
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
			<fieldset class="row"><legend>Upload MBA-X Slots list </legend></fieldset>
			<%@ include file="../../messages.jsp"%>
			
			<div class="panel-group" style="margin-top : 30px" id="accordion" role="tablist" aria-multiselectable="true">
				<c:if test="${fn:length(successList) gt 0}">
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="successList">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#success-list" aria-controls="success-list">
									Slots Generated
								</a>
							</h4>
						</div>
						<div id="success-list" class="panel-collapse collapse" role="tabpanel" aria-labelledby="successList">
							<div class="panel-body">
								<fieldset class="row">
									<legend>
										These slots were successfully generated.
									</legend>
								</fieldset>
								<h5></h5>
								<div class="table-responsive">
									<table id="success-list-table" class="table table-striped" style="width: 100% !important;">
										<thead>
											<tr>
												<th class="filterhead">Subject</th>
												<th class="filterhead">Term</th>
												<th class="filterhead">Exam Year</th>
												<th class="filterhead">Exam Month</th>
												<th class="filterhead">Date</th>
												<th class="filterhead">Start Time</th>
												<th class="filterhead">End Time</th>
												<th class="filterhead">Center</th>
												<th>Capacity</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="slot" items="${successList}">
												<tr>
													<td>${ slot.subjectName }</td>
													<td>${ slot.term }</td>
													<td>${ slot.examYear }</td>
													<td>${ slot.examMonth }</td>
													<td>${ slot.examDate }</td>
													<td>${ slot.examStartTime }</td>
													<td>${ slot.examEndTime }</td>
													<td>${ slot.centerName }</td>
													<td>${ slot.capacity }</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</c:if>
				<c:if test="${fn:length(errorList) gt 0}">
					<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="errorList">
							<h4 class="panel-title">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#error-list" aria-controls="error-list">
									Slots not generated
								</a>
							</h4>
						</div>
						<div id="error-list" class="panel-collapse collapse" role="tabpanel" aria-labelledby="errorList">
							<div class="panel-body">
								<fieldset class="row">
									<legend>
										These slots were not generated.
									</legend>
								</fieldset>
								<div class="table-responsive">
									<table id="error-list-table" class="table table-striped " style="width: 100% !important;">
										<thead>
											<tr>
												<th class="filterhead">Subject</th>
												<th class="filterhead">Term</th>
												<th class="filterhead">Exam Year</th>
												<th class="filterhead">Exam Month</th>
												<th class="filterhead">Date</th>
												<th class="filterhead">Start Time</th>
												<th class="filterhead">End Time</th>
												<th class="filterhead">Center</th>
												<th>Capacity</th>
												<th>Error</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="slot" items="${errorList}">
												<tr>
													<td>${ slot.subjectName }</td>
													<td>${ slot.term }</td>
													<td>${ slot.examYear }</td>
													<td>${ slot.examMonth }</td>
													<td>${ slot.examDate }</td>
													<td>${ slot.examStartTime }</td>
													<td>${ slot.examEndTime }</td>
													<td>${ slot.centerName }</td>
													<td>${ slot.capacity }</td>
													<td>${ slot.error }</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</c:if>
				<div class="panel panel-default">
					<div class="panel-heading" role="tab" id="slotsList">
						<h4 class="panel-title">
							<a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#slots-list" aria-expanded="true" aria-controls="slots-list">
								List Of Slots
							</a>
						</h4>
					</div>
					<div id="slots-list" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="slotsList">
						<div class="panel-body">
							<div class="table-responsive">
								<table id="slots-list-table" class="table table-striped " style="width: 100% !important;">
									<thead>
										<tr>
											<th class="filterhead">Subject</th>
											<th class="filterhead">Term</th>
											<th class="filterhead">Exam Year</th>
											<th class="filterhead">Exam Month</th>
											<th class="filterhead">Date</th>
											<th class="filterhead">Start Time</th>
											<th class="filterhead">End Time</th>
											<th class="filterhead">Center</th>
											<th>Capacity</th>
											<th>Active</th>
											<th>Actions</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="slot" items="${slotsList}">
											<tr>
												<td>${ slot.subjectName }</td>
												<td>${ slot.term }</td>
												<td>${ slot.examYear }</td>
												<td>${ slot.examMonth }</td>
												<td>${ slot.examDate }</td>
												<td>${ slot.examStartTime }</td>
												<td>${ slot.examEndTime }</td>
												<td>${ slot.centerName }</td>
												<td>${ slot.capacity }</td>
												<td>
													<a
														href="javascript:void(0)" 
														slotId="${slot.slotId}" 
														active="${slot.active}" 
														class="toggleSlotActive" 
														style="color:#c72127;"
													>${ slot.active }</a>
												</td>
												<td>
													<a 
														href="javascript:void(0)" 
														slotId="${slot.slotId}"
														class="deleteSlotFromList" 
														style="color:#c72127;"
													>
														Delete
													</a>
													<a href="updateSlotForm_MBAX?slotId=${slot.slotId}" style="color:#c72127;" >
														Update
													</a>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<br/><br/>
	</section>

	<jsp:include page="../../footer.jsp" />
	<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	<script>
		var tableProps = {
			"autoWidth": false,
			orderCellsTop: true,
			initComplete: function () {
				this.api().columns().every( function () {
					var column = this;
					window.column = window.column ? window.column : this
					console.debug(column.header())
					
					if($(column.header()).hasClass('filterhead')) {
						var select = $('</br><select><option value=""></option></select>')
							.appendTo( $(column.header()))
							.on( 'change', function () {
								var val  = 	$.fn.dataTable.util.escapeRegex(
												$(this).val()
											);
								column
									.search( val ? '^'+val+'$' : '', true, false )
									.draw();
							});

						column.data().unique().sort().each( function ( d, j ) {
							select.append( '<option value="'+d+'">'+d+'</option>' )
						});
					}
						
				});
			}
		}
		$(document).ready( function () {
			var successTable = $('#success-list-table').DataTable(tableProps);
			var listTable = $('#slots-list-table').DataTable(tableProps);
			var errorTable = $('#error-list-table').DataTable(tableProps);
			$(document).on('click',".deleteSlotFromList",function(){
				let prompt = confirm("Are you sure you want to delete?");
				if(!prompt){
					return false;
				}
				let slotId = $(this).attr("slotId");
				$.ajax({
					url:"deleteSlot_MBAX?slotId=" + slotId,
					method:"POST",
					success:function(response){
						if(response.status == "success"){
							alert(response.message);
							window.location.href = "slotsList_MBAX";
						}else{
							alert(response.message);
						}
					},
					error:function(error){
						alert("Failed to delete record");
						console.log(error);
					}
				});
			})

			$(document).on('click',".toggleSlotActive",function(){
				let prompt = confirm("Are you sure you want to toggle this record?");
				if(!prompt){
					return false;
				}
				let slotId = $(this).attr("slotId");
				let active = $(this).attr("active") == "Y" ? "N" : "Y";
				
				$.ajax({
					url:"toggleSlotActive_MBAX?slotId=" + slotId + "&active=" + active,
					method:"POST",
					success:function(response){
						if(response.status == "success"){
							alert(response.message);
							window.location.href = "slotsList_MBAX";
						}else{
							alert(response.message);
						}
					},
					error:function(error){
						alert("Failed to delete record");
						console.log(error);
					}
				});
			})
		} );
		
	</script>
</body>
</html>
