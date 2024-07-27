<!DOCTYPE html>

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Set Dates for Exam Registration Extension"
		name="title" />
</jsp:include>


<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
			<div class="panel-body">
				<%@ include file="messages.jsp"%>
				<div class="row">
					<div class="col-md-6 column">
						<!-- <legend>&nbsp;Set Extended Dates For Exam Registration</legend> -->
						<form action="javascript:void(0);" method="post">
							<fieldset>

								<div class="form-group col-4">
									<label for="examYear">Exam Year</label> <select id="examYear">
										<c:forEach var="year" items="${ examYearList }">
											<option value="${year}">${year}</option>
										</c:forEach>
									</select>

								</div>

								<div class="form-group col-4">
									<label for="examMonth">Exam Month</label> <select
										id="examMonth">
										<c:forEach var="month" items="${ examMonthList }">
											<option value="${month}">${month}</option>
										</c:forEach>
									</select>
								</div>

								<div class="form-group">
									<label for="startDate">Extended Start Date</label> <input
										id="startDate" type="datetime-local" required="required" />
								</div>

								<div class="form-group">
									<label for="endDate">Extended End Date</label> <input
										id="endDate" type="datetime-local" required="required" />
								</div>

								<legend>&nbsp;Extend Registration End Time and Date </legend>
								<div class="form-group row" style="overflow: visible;">
									<div class="col-md-8 column">
										<textarea rows="10" cols="80" id="sapid"
											placeholder="Please Enter Comma Separated Sapids"></textarea>
									</div>
								</div>

								<div class="form-group">
									<label class="control-label" for="submit"></label>
									<div class="controls">
										<button id="submit" name="submit"
											class="btn btn-large btn-primary">Extend Date Time</button>
										<!-- <button id="cancel" name="cancel" class="btn btn-danger"
											formaction="home" formnovalidate="formnovalidate">Home</button> -->
									</div>
								</div>
							</fieldset>
						</form>

					</div>
					<div class="col-md-1 column"></div>
					<div class="col-md-11 column">
						<legend>&nbsp;Extended Student List</legend>
						<table id="extendedStudents" class="table table-striped"
							style="font-size: 12px">
							<thead>
								<tr>
									<th>Sap id</th>
									<th>Exam Month</th>
									<th>Exam Year</th>
									<th>Extended Start Date Time</th>
									<th>Extended End Date Time</th>
									<th>Delete</th>
								</tr>
							</thead>
							<tbody id="tableBody">

								<%-- <c:forEach var="student" items="${extendedStudents}">
									<tr>
										<td><c:out value="${student.sapid}" /></td>
										<td><c:out value="${student.extendStartDateTime}" /></td>
										<td><c:out value="${student.extendEndDateTime}" /></td>
									</tr>
								</c:forEach> --%>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</section>

	<div class="modal" tabindex="-1" role="dialog">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title"></h5>
				</div>
				<div class="modal-body">
					<p id="errorBody"></p>
				</div>
				<div class="modal-footer">
					<button type="button" id="error-modal-button"
						class="btn btn-secondary">Back</button>
				</div>
			</div>
		</div>
	</div>

	<span id="demo" style="display: none;"></span>
	<jsp:include page="footer.jsp" />

	<script
		src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			loadDataTable();
		});

		$('#submit').click(function() 
				{
					var startDate = $('#startDate').val();
					var endDate = $('#endDate').val();
					var sapIds = $('#sapid').val();
					var examMonth = $('#examMonth').val();
					var examYear = $('#examYear').val();

					if (!(startDate && endDate && sapIds && examYear && examMonth)) {
						alert('Please make sure input fields are not empty!');
						return false;
					}

					if(startDate > endDate){
						alert('End date cannot be set before start date!');
						return false;
					}

					$.ajax({
							url : '/exam/m/extendRegistrationDateTimeForSapIds',
							dataType : 'json',
							contentType : 'application/json',
							type : 'POST',
							data : JSON.stringify({
								'extendStartDateTime' : startDate,
								'extendEndDateTime' : endDate,
								'sapid' : sapIds,
								'createdBy' : "${userId}",
								'examMonth' : examMonth,
								'examYear' : examYear
							}),
							success : function(result) {
								if(result.error.length > 0){
									
								$('.modal-title').html(' Error Sapids : ');
								$('#errorBody').html('Unable to extend date and time for sapid : '+ result.error.toString());
									
								$('.modal').modal({
									backdrop : 'static'
								});

								} else {
									 window.location.href = "getExtendExamForTimeboundStudentForm"; 
									}
							},
							error : function(error) {
								$('.modal-title').html(' Error ');
								$('#errorBody').html('Error while extending time and date for sapids!' + error.statusText);
								
								$('.modal').modal({
									backdrop : 'static'
								});
								}
							})
						})

		function loadDataTable() {

			$.ajax({
					url : "/exam/m/getRegistrationExtendedStudents",
					type : "GET",
					dataType : "json",
					success : function(result) {
						$(result).each(function() 
								{
									
									$("#tableBody").append("<tr> <td name = 'sapid'>"
															+ this.sapid
															+ "</td>"
															+ " <td name = 'examMonth'>"
															+ this.examMonth
															+ "</td> <td name = 'examYear'>"
															+ this.examYear
															+ "</td>  <td name = 'extendStartDateTime'>"
															+ this.extendStartDateTime
															+ "</td> <td name = 'extendEndDateTime'>"
															+ this.extendEndDateTime
															+ "</td> <td>"
															+ "<button class = 'deleteExtendedRegistration btn btn-danger' value = 'Delete'> Delete </button>"
															+ "</td> </tr>");
											});
						
							$('#extendedStudents').DataTable({
								"autoWidth" : false
							});
						},
						error : function(error) {
							
							$('.modal-title').html(' Error ');
							$('#errorBody').html('Error while fetching extended sapids !' + error.statusText);
							
							$('.modal').modal({
								backdrop : 'static'
							});
						}
					});
		}

		$('#error-modal-button').click(function() {
			window.location.href = "getExtendExamForTimeboundStudentForm";
		})

		$('#tableBody').on('click','.deleteExtendedRegistration', function() {

			let prompt = confirm("Are you sure you want to delete ? ");

					if (!prompt) {
						return false;
					}

					var sapid = $(this).closest('tr').find( "[name = 'sapid']")[0].innerHTML;
					var examMonth = $(this).closest('tr').find("[name = 'examMonth']")[0].innerHTML;
					var examYear = $(this).closest('tr').find("[name = 'examYear']")[0].innerHTML;
					var extendStartDateTime = $(this).closest('tr').find("[name = 'extendStartDateTime']")[0].innerHTML;
					var extendEndDateTime = $(this).closest('tr').find("[name = 'extendEndDateTime']")[0].innerHTML;

					$.ajax({
							url : '/exam/m/deleteExtendedRegistration',
							type : 'POST',
							dataType : 'json',
							contentType : 'application/json',
							data : JSON.stringify({
													'sapid' : sapid,
													'examMonth' : examMonth,
													'examYear' : examYear,
													'extendStartDateTime' : extendStartDateTime,
													'extendEndDateTime' : extendEndDateTime,
													'lastModifiedBy' : "${userId}"
												}),
							success : function(response) {
								if(response.status == 'successful'){
									$('.modal-title').html('success');
									
									$('#errorBody').html('successfuly deleted registration configuration for sapid : ' + sapid);
									}

								$('.modal').modal({
									backdrop : 'static'
								});
								
							},
							error : function(error) {
								$('.modal-title').html('Error');
								
								$('#errorBody').html('failed to delete registration configuration for sapid : ' + sapid + ' ' + error.statusText);

							$('.modal').modal({
								backdrop : 'static'
							});
							}
						})
					})
	</script>
</body>
</html>

