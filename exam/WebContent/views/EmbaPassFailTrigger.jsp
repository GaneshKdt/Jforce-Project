<!DOCTYPE html>
<html lang="en">
	
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
    <jsp:include page="jscss.jsp">
	<jsp:param value="EMBA PassFail Trigger" name="title"/>
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
    
    	<%@ include file="header.jsp"%>
	<section class="content-container">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend> EMBA PassFail Trigger </legend>
			</div>

			<%@ include file="messages.jsp"%>
						<div class = "js_result"></div>
									<form:form  method="post" modelAttribute="resultBean">
										<div class="panel-body">
																				
											<div class="row">
												
												<div class="col-md-3">
												<div class="form-group">
													<label for="sel1">Acad Month</label>
													<select name="acadMonth" id="acadMonth" class="form-control" >
														<option value="">-- Acad Month --</option>
														<c:forEach items="${acadMonth}" var="month">
															<option value="${month}">${month}</option>
														</c:forEach>
													</select>
												</div>
											</div>
					
												<div class="col-md-3 ">
													<div class="form-group">
														<label for="sel1">Acad Year</label>
															<select name="acadYear" id="acadYear" class="form-control">
																<option value="">-- Acad Year --</option>
																<c:forEach items="${acadYear}" var="year">
																<option value="${year}">${year}</option>
																</c:forEach>
															</select>
													</div>
												</div>
												
												<div class="col-md-3">
													<div class="form-group">
													  <label for="sel1">Select Program Type:</label>
													  <select name="programType" class="form-control programType" >
													  	<option value="">-- select Program Type --</option>
													  	<option value="MBA - WX">MBA - WX</option>
													  	<option value="M.Sc. (AI & ML Ops)">M.Sc. (AI & ML Ops)</option>
													  	<option value="M.Sc. (AI)">M.Sc. (AI)</option>
													  	<option value="Modular PD-DM">Modular PD-DM</option>
													  </select>
													</div>
												</div>
												
												<div class="col-md-3">
													<div class="form-group">
													  	<label for="sel1">Select Batch:</label>
														<select name="batchId" class="form-control batches" >
														  	<option disabled selected value="">-- select batch --</option>
					
														 </select>
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
																formaction="embaPassFailTriggerSearch">Search Students For Pass Fail</button>
													</div>		
												</div>
											
												<c:if test="${studentsListEligibleForPassFailSize gt 0}">
												<div class="col-md-4 ">
													<!--   -->
													<div class="form-group">
														<button id="submit" name="submit" class="btn btn-large btn-primary"
																		formaction="embaPassFailTrigger">Run Pass Fail</button>
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
									
									<c:choose>
									<c:when test="${displayFinalList == 1}">
									<div></div>
									<c:forEach var="scheduleStudentListMap" items="${finalListForAllSchedules}">
									<c:set var="studentList" value="${scheduleStudentListMap.value}"/>
									<button class="accordion">Final List Table ${studentList[0].assessmentName}</button>
									<div class="panel">
											<div class="column">
											<h5> <b>Students ready for pass-fail - ${studentList[0].assessmentName}</b></h5>
												<div class="table-responsive">
													<table class ="dataTable" class="table table-striped table-hover tables">
														<thead>
															<tr>
																<th>SapId</th>
																<th>TEE Score</th>
																<th>IA Score</th>
																<th>Status</th>
																<th>Processed</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach var="passFailBean" items="${scheduleStudentListMap.value}">
																<tr>
																	<td><c:out value="${passFailBean.sapid}" /></td>
																	<td><c:out value="${passFailBean.teeScore}" /></td>
																	<td><c:out value="${passFailBean.iaScore}" /></td>
																	<td><c:out value="${passFailBean.status}" /></td>
																	<td><c:out value="${passFailBean.processed}" /></td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</c:forEach> 
									</c:when>
									<c:otherwise>
									<c:choose>
									<c:when test="${finalListforPassFailSize gt 0}">
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
																	<td><c:out value="${passFailBean.status}" /></td>
																	<td><c:out value="${passFailBean.processed}" /></td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</c:when>
									</c:choose>
									</c:otherwise>
									</c:choose>
									
									
									<c:choose>
									<c:when test="${displayUnsuccessfulList == 1}">
									<c:forEach var="scheduleStudentListMap" items="${unsuccessfulListForAllSchedules}">
									<c:set var="studentList" value="${scheduleStudentListMap.value}"/>
									<button class="accordion">Insufficient Data Table ${studentList[0].assessmentName}</button>
									<div class="panel">
											<div class="column">
											<h5> <b>Students having insufficient data - ${studentList[0].assessmentName}</b></h5>
												<div class="table-responsive">
													<table class ="dataTable" class="table table-striped table-hover tables">
														<thead>
															<tr>
																<th>SapId</th>
																<th>TEE Score</th>
																<th>IA Score</th>
																<th>Status</th>
																<th>Processed</th>
															</tr>
														</thead>
														<tbody>
															<c:forEach var="passFailBean" items="${scheduleStudentListMap.value}">
																<tr>
																	<td><c:out value="${passFailBean.sapid}" /></td>
																	<td><c:out value="${passFailBean.teeScore}" /></td>
																	<td><c:out value="${passFailBean.iaScore}" /></td>
																	<td><c:out value="${passFailBean.status}" /></td>
																	<td><c:out value="${passFailBean.processed}" /></td>
																</tr>
															</c:forEach>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</c:forEach>
									</c:when>
									<c:otherwise>
									<c:choose>
									<c:when test="${unsuccessfulPassFailSize gt 0}">
									<button class="accordion">Table 2</button>
									<div class="panel">
											<div class="column">
												<h5><b> Students having insufficient data </b></h5>
												<div class="table-responsive">
													<table class ="dataTable1" class="table table-striped table-hover tables">
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
									</c:when>
									</c:choose>
									</c:otherwise>
									</c:choose> 
						</div>
				</section>
        <jsp:include page="footer.jsp"/>
		<script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	    <script>
		    $(document).ready( function () {
		        $('#dataTable').DataTable();
		        /* $('#dataTable1').DataTable(); */
		        $('.dataTable').DataTable();
		        $('.dataTable1').DataTable();
		    } );
	    </script>
        <script>
		$(document).ready(function(){

			$(document).on('click','#acadMonth,#acadYear',function(){
				var programName= $('.programType').val(); 
				if(programName != ""){
					$('.programType').val("");
				}
			});
			
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
				
				if(assessment=='selectAll'){
					optionsList = '<option value="selectAll">Select All</option>';
					$('#schedule').html(optionsList);
					return false;
				}
				
				
				$.ajax({
					url:"getScheduleListByAssessment?id=" + assessment+"&timeid=" + subject,
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
			
			
			$(document).on('change','.batches',function(){
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
							optionsList = optionsList + '<option value="'+ response[i].id +'"  data-hasia="'+ response[i].hasIA+'" data-hastee="'+ response[i].hasTEE+'" >'+ response[i].subject +'</option>';
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
				let hasia = $(this).find('option:selected').data('hasia');
				let hastee = $(this).find('option:selected').data('hastee');
				if(hasia === 'Y' && hastee === 'N' ){
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
					url:"getAssessmentListByTimeBoundId?id=" + subject,
					method:"GET",
					success:function(response){
						optionsList = '<option disabled selected value="">-- select assessment --</option>';
						for(let i=0;i < response.length;i++){
							optionsList = optionsList + '<option value="'+ response[i].id +'">'+ response[i].customAssessmentName +'</option>';
						}
						if(response.length>0){
							optionsList = optionsList + '<option value="selectAll">Select All</option>';
						}
						$('#assessment').html(optionsList);
						console.log(response);
					},
					error:function(error){
						alert("Error while getting schedule data");
					}
				});
				$('#assessment').html(optionsList);
			});

			$(document).on('change','.programType',function(){
				var programType = $(this).val();
				var acadMonth=$('#acadMonth').val();
				var acadYear=$('#acadYear').val();
				if(programType == "" || acadMonth == "" || acadYear == ""){
					return false;
				}
				getBatchList(programType,acadMonth,acadYear);
			});
			
		});

	    function getBatchList(programType,acadMonth,acadYear){
	    	  let optionsList = '<option value="" disabled selected>loading</option>';
	    	  $('.batchList').html(optionsList);
			$.ajax({
	    	 	url : '/exam/m/getActiveBatchList',
			    type : 'POST',
				contentType : "application/json",
				data : JSON.stringify({ 
					programType : programType,
					acadMonth : acadMonth,
					acadYear : acadYear
					}),
				dataType : "json",
		      success : function(response){
					let batchList = '<option disabled selected value="">-- select batch --</option>';
					for(let i=0;i < response.length;i++){
						batchList = batchList + '<option value="' + response[i].id +'" >'+ response[i].name + '</option>';
					}
					$('.batches').html(batchList);
						
		      },error: function (result, status, err) {
			          alert("Unable to fetach Batch List !");
			          let optionsList = '<option value="" disabled selected>Unable to fetach Batch List !</option>';
			    	  $('.batches').html(optionsList);        
			  }

		    });
			
		  }
		
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