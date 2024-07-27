<!DOCTYPE html>
<html lang="en">
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="../jscss.jsp">
	<jsp:param value="MBA-X PassFail Trigger" name="title"/>
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
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
.accordion {
  background-color: #eee;
  color: #444;
  cursor: pointer;
  padding: 18px;
  width: 100%;
  border: none;
  text-align: left;
  outline: none;
  font-size: 15px;
  transition: 0.4s;
}

.active, .accordion:hover {
  background-color: #ccc;
}

.accordion:after {
  content: '\002B';
  color: #777;
  font-weight: bold;
  float: right;
  margin-left: 5px;
}

.active:after {
  content: "\2212";
}

.panel {
  padding: 0 18px;
  background-color: white;
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.2s ease-out;
}
</style>
</head>
</head>
    
    
    <body>
    
    	<%@ include file="../header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> MBA-X PassFail Trigger </legend>
			</div>

			<%@ include file="../messages.jsp"%>
						<div class = "js_result"></div>
									<form:form  method="post" modelAttribute="resultBean">
										<div class="panel-body">
											<div class="row">
												<div class="col-md-3">
													<div class="form-group">
													  	<label for="batchId">Select Batch:</label> 
														<form:select path="batchId" id="batches" >
															<form:option value="" label="-- select batch --"/>
															<form:options items="${batches}" itemLabel="name" itemValue="id"/>
														</form:select>
													</div>
												</div>
												
												<div class="col-md-3">
													<div class="form-group">
													  <label for="sel1">Select Subject:</label>
													  <select name="timebound_id" class="form-control" id="subject"   itemValue="${resultBean.timebound_id}">
													    <option disabled selected value="">-- select subject --</option>
													  </select>
													</div>
												</div>
												
												<div class="col-md-3">
													<div class="form-group">
													  <label for="sel1">Select Assessment:</label>
													  <select name="assessments_id" class="form-control" id="assessment"  itemValue="${resultBean.assessments_id}">
													  	<option disabled selected value="">-- select assessment --</option>
													  </select>
													</div>
												</div>
												
												<div class="col-md-3">
													<div class="form-group">
													  <label for="sel1">Select Schedule:</label>
													  <select name="schedule_id" class="form-control" id="schedule"  itemValue="${resultBean.schedule_id}">
													    <option disabled selected value="">-- select schedule --</option>
													  </select>
													</div>
												</div>
											</div>
											
											<div class="row">
												<div class="col-md-4 ">
													<!--   -->
													<div class="form-group">
														<button id="submit" name="submit" class="btn btn-large btn-primary"
																formaction="mbaxPassFailTriggerSearch">Search Students For Pass Fail</button>
													</div>		
												</div>
											
												<c:if test="${studentsListEligibleForPassFailSize gt 0}">
												<div class="col-md-4 ">
													<!--   -->
													<div class="form-group">
														<button id="submit" name="submit" class="btn btn-large btn-primary"
																		formaction="mbaxPassFailTrigger">Run Pass Fail</button>
													</div>	
												</div>
												</c:if>
											</div>
									</div>
							</form:form>
							<c:if test="${studentsNotProcessedSize gt 0}">
									<button class="accordion">Table 0 : ${studentsNotProcessedSize} Students Pending For Pass-Fail</button>
									<div class="panel">
											<div class="column">
											<h5> <b>Students pending for pass-fail </b></h5>
												<div class="table-responsive">
													<table id ="dataTable" class="table table-striped table-hover tables">
														<thead>
															<tr>
																<th>SapId</th>
																<th>TEE Score</th>
																<th>Status</th>
																<th>Processed</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach var="passFailBean" items="${studentsNotProcessed}">
																<tr>
																	<td><c:out value="${passFailBean.sapid}" /></td>
																	<td><c:out value="${passFailBean.score}" /></td>
																	<td><c:out value="${passFailBean.status}" /></td>
																	<td><c:out value="${passFailBean.processed}" /></td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</c:if>
							
									<c:if test="${finalListforPassFailSize gt 0}">
									<button class="accordion">Table 1</button>
									<div class="panel">
											<div class="column">
											<h5> <b>Students ready for pass-fail </b></h5>
												<div class="table-responsive">
													<table id ="dataTable" class="table table-striped table-hover tables">
														<thead>
															<tr>
																<th>SapId</th>
																<th>TEE Score</th>
																<th>IA Score</th>
																<th>Project Score</th>
																<th>Status</th>
																<th>Processed</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach var="passFailBean" items="${finalListforPassFail}">
																<tr>
																	<td><c:out value="${passFailBean.sapid}" /></td>
																	<td><c:out value="${passFailBean.teeScore}" /></td>
																	<td><c:out value="${passFailBean.iaScore}" /></td>
																	<td><c:out value="${passFailBean.project}" /></td>
																	<td><c:out value="${passFailBean.status}" /></td>
																	<td><c:out value="${passFailBean.processed}" /></td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</c:if>
									
									<c:if test="${unsuccessfulPassFailSize gt 0}">
									<button class="accordion">Table 2</button>
									<div class="panel">
											<div class="column">
												<h5><b> Students having insufficient data </b></h5>
												<div class="table-responsive">
													<table id ="dataTable1" class="table table-striped table-hover tables">
														<thead>
															<tr>
																<th>SapId</th>
																<th>TEE Score</th>
																<th>IA Score</th>
																<th>Status</th>
																<th>Processed</th>
																<th>Fail Reason</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach var="passFailBean" items="${unsuccessfulPassFail}">
																<tr>
																	<td><c:out value="${passFailBean.sapid}" /></td>
																	<td><c:out value="${passFailBean.teeScore}" /></td>
																	<td><c:out value="${passFailBean.iaScore}" /></td>
																	<td><c:out value="${passFailBean.status}" /></td>
																	<td><c:out value="${passFailBean.processed}" /></td>
																	<td><c:out value="${passFailBean.failReason}" /></td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</c:if>
						</div>
				</section>
        <jsp:include page="../footer.jsp"/>
		<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	    <script>
		    $(document).ready( function () {
		        $('#dataTable').DataTable();
		        $('#dataTable1').DataTable();
		        
		    } );
	    </script>
        <script>
		$(document).ready(function(){
	
			$(document).on('change','#assessment',function(){
				var assessment = $(this).val();
				if(assessment == ""){
					return false;
				}
				var subject = $('#subject').val();
				if(subject == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getMBAXScheduleListByAssessment?id=" + assessment+"&timeid=" + subject,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select schedule --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].schedule_id +'">'+ response[i].schedule_name +'</option>';
						}
						$('#schedule').html(optionsList);
						console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#schedule').html(optionsList);
			});
			
			
			$(document).on('change','#batches',function(){
				var batch = $(this).val();
				if(batch == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getSubjectListByBatchId?id=" + batch,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select subject --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].id +'" data-pssid="'+ response[i].prgm_sem_subj_id+'" >'+ response[i].subject +'</option>';
						}
						$('#subject').html(optionsList);
						console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#subject').html(optionsList);
			});
			
			$(document).on('change','#subject',function(){
				var subject = $(this).val();
				let pssId = $(this).find('option:selected').data('pssid');
				if(pssId == 1789 || pssId == 2712){
			    	let option =  '<option value="0" selected>Not Applicable</option>';
			    	$('#assessment').html(option);
			    	$('#schedule').html(option);
			    	return false;
				}
				
				if(subject == ""){
					return false;
				}
				let optionsList = '<option value="" disabled selected>loading</option>';
				$.ajax({
					url:"getMBAXAssessmentListByTimeBoundId?id=" + subject,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select assessment --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].id +'">'+ response[i].customAssessmentName +'</option>';
						}
						$('#assessment').html(optionsList);
						console.log(response[0]);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#assessment').html(optionsList);
			});
			
		});
	</script>
   <script>
		var acc = document.getElementsByClassName("accordion");
		var i;
		
		for (i = 0; i < acc.length; i++) {
		  acc[i].addEventListener("click", function() {
		    this.classList.toggle("active");
		    var panel = this.nextElementSibling;
		    if (panel.style.maxHeight) {
		      panel.style.maxHeight = null;
		    } else {
		      panel.style.maxHeight = panel.scrollHeight + "px";
		    } 
		  });
		}
</script>
    </body>
</html>