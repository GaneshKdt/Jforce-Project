<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<%@page import="java.util.ArrayList"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Create Synopsis" name="title" />
</jsp:include>




<body class="inside">

	<%@ include file="../../header.jsp"%> 

	<section class="content-container login">
		<div class="container-fluid customTheme">
			
			<div class="row"><legend>Create Synopsis</legend></div>
			<%@ include file="../../messages.jsp"%>
				
				<form method="post" action="levelBasedSynopsisConfig" enctype="multipart/form-data">
					<div class="row">
					<div class="col-md-16 column">
					
					<div class="row">
					
					<!-- ///////////////////////////////////////////////////////////////// -->
					
							<div class="col-md-4 column">
							<div class="form-group">
							<label for="consumerType">Consumer Type</label>
							<select data-id="consumerTypeDataId" id="consumerTypeId" name="consumer_type"  class="selectConsumerType" required="required">
								<option disabled selected value="">Select Consumer Type</option>
							<c:forEach var="consumerType" items="${consumerType}">
				                <option value="<c:out value="${consumerType.id}"/>">
				                  <c:out value="${consumerType.name}"/>
				                </option>
				            </c:forEach>
							</select>
							</div>
						</div>
						
						<div class="col-md-4 column">
							<div class="form-group">
							<label for="programStructure">Program Structure</label>
							<select data-id="programStructureDataId" id="programStructureId" name="program_structure" class="selectProgramStructure" required="required">
								<option disabled selected>Select Program Structure</option>

							</select>
							</div>
						</div> 
							
						<div class="col-md-4 column">
							<div class="form-group">
							<label for="Program">Program</label>
							<select data-id="programDataId" id="programId" name="program" class="selectProgram">
								<option disabled selected>Select Program</option>

							</select>
							</div>
						</div>
						
						<div class="col-md-4 column">
							<div class="form-group">
							<label for="subjectId">Subject</label>
							<select data-id="subjectId" id="subjectId" name="subject" class="selectSubject" required>
								<option disabled selected>Select Subject</option>

							</select>
							</div>
						</div>
						
					</div>
					<!--  <hr/>
					<div class="row">
						<div class="col-md-4 column">
							<div class="form-group">
								<label >Subject Code</label>
								<input type="text" name="subject_code" />
							</div>
						</div>
					</div>
					<hr/> -->
					<div class="row">	
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Exam Year</label>
							<select name="year">
								<c:forEach var="year" items="${EXAM_YEAR_LIST}">
									<option value="${ year }">${year }</option>
								</c:forEach>
							</select>
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Exam Month</label>
							<select name="month">
								<c:forEach var="month" items="${EXAM_MONTH_LIST}">
								<option value="${ month }">${ month }</option>
								</c:forEach>
							</select>
						</div>
						</div>
						
						<div class="col-md-4 column">
							<div class="form-group">
								<label for="live">Live</label>
								<select id="live" name="live" type="datetime-local" value="" required>
									<option value="Y">Live</option>
									<option value="N">Not Live</option>
								</select>
							</div>
						</div>
						
						<div class="col-md-4 column">
							<div class="form-group">
								<label for="startDate">Start Date</label>
								<input id="startDate" name="start_date" type="datetime-local" value="" required>
							</div>
						</div>
						
						<div class="col-md-4 column">
							<div class="form-group">
								<label for="endDate">End Date</label>
								
								<input id="endDate" name="end_date" type="datetime-local" value="" required>
							</div>
						</div>
						
						
					</div>
					
					<div class="row">
						<div class="col-md-4 column">
							<div class="form-group">
								<label >Max Attempt</label>
								<input type="number" name="max_attempt" min = "0" required/>
							</div>
						</div>
						<div class="col-md-4 column">
							<div class="form-group">
								<label>Payment Applicable</label>
								
								<select id="payment_applicable" name="payment_applicable" type="datetime-local" value="" required>
									<option value="Y">Yes</option>
									<option value="N">No</option>
								</select>
							</div>
						</div>
						
						<div class="col-md-4 column">
							<div class="form-group">
								<label >Payment Amount</label>
								<input type="number" name="payment_amount" />
							</div>
						</div>
						
					</div>
					
					<div class="form-group">
						<label>Synopsis question file</label>
						<input type="file" name="fileData" style="max-width:300px;" required/>
					</div>
					<!-- ///////////////////////////////////////////////////////////////// -->
					
					<br/>
					
					
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary">Save & Update Synopsis</button>
					</div>
					</div>

			</div>
			</form>
			
		</div>
	</section>

	<div class="panel-group" style="margin-top : 30px" id="accordion" role="tablist" aria-multiselectable="true">
		<div class="panel panel-default">
			<div class="panel-heading" role="tab" id="centersList">
				<h4 class="panel-title">
					<a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordion" href="#centers-list" aria-expanded="true" aria-controls="centers-list">
						Configuration List
					</a>
				</h4>
			</div>
			<div id="centers-list" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="centersList">
				<div class="panel-body">
					<div class="table-responsive">
						<table class="table" style="background-color:white">
							<thead>
								<tr>
									<th>Year</th>
									<th>Month</th>
									<th>Consumer Type</th>
									<th>Program</th>
									<th>Program Structure</th>
									<th>Max attempt</th>
									<th>Payment Applicable</th>
									<th>Payment amount</th>
									<th>Start date</th>
									<th>End date</th>
									<th>Preview File</th>
									<th>Action</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${levelBasedSynopsisConfigBeansList}" var="levelBasedSynopsisConfig">
									<tr>
										
										<td><c:out value="${ levelBasedSynopsisConfig.year }" /></td>
										<td><c:out value="${ levelBasedSynopsisConfig.month }" /></td>
										<td><c:out value="${ levelBasedSynopsisConfig.consumer_type }" /></td>
										<td><c:out value="${ levelBasedSynopsisConfig.program }" /></td>
										<td><c:out value="${ levelBasedSynopsisConfig.program_structure }" /></td>
										<td><c:out value="${ levelBasedSynopsisConfig.max_attempt }" /></td>
										<td><c:out value="${ levelBasedSynopsisConfig.payment_applicable }" /></td>
										<td><c:out value="${ levelBasedSynopsisConfig.payment_amount }" /></td>
										<td><c:out value="${ levelBasedSynopsisConfig.start_date }" /></td>
										<td><c:out value="${ levelBasedSynopsisConfig.end_date }" /></td>
										<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('SYNOPSIS_FILES_PATH')" />${levelBasedSynopsisConfig.question_filePath}">Preview File</a></td>
										<td>   
											<a
												data-pss_id='${ levelBasedSynopsisConfig.program_sem_subject_id }' 
												data-year='${ levelBasedSynopsisConfig.year }' 
												data-month='${ levelBasedSynopsisConfig.month }' 
												class="delete_btn"
											>
												Delete
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
	<jsp:include page="../../footer.jsp" />
	
	
	

<script src="assets/js/jquery-1.11.3.min.js"></script>
<script src="assets/js/bootstrap.js"></script>
<script src="assets/js/jquery.tabledit.js"></script>
<script src="resources_2015/js/vendor/jquery-ui.min.js"></script>
<script
	src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script
	src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

<script type="text/javascript">


$(document).ready (function(){

	$('.tables').DataTable(); 

	$('.selectConsumerType').on('change', function(){
		let id = $(this).attr('data-id');
		let options = "<option>Loading... </option>";
		$('#programStructureId').html(options);
		$('#programId').html(options);
		$('.selectSubject').html(options);
		var data = {
			id:this.value
		}
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "getDataByConsumerType",   
			data : JSON.stringify(data),
			success : function(data) {
				var programData = data.programData;
				var programStructureData = data.programStructureData;
				var subjectsData = data.subjectsData;
				options = "";
				let allOption = "";
				for(let i=0;i < programData.length;i++){
					allOption = allOption + ""+ programData[i].id +",";
					options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
				}
				allOption = allOption.substring(0,allOption.length-1);
				$('#programId').html("<option value='"+ allOption +"'>All</option>" + options);
				options = ""; 
				allOption = "";
				for(let i=0;i < programStructureData.length;i++){
					allOption = allOption + ""+ programStructureData[i].id +",";
					options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
				}
				allOption = allOption.substring(0,allOption.length-1);
				$('#programStructureId').html("<option value='"+ allOption +"'>All</option>" + options);
				options = ""; 
				allOption = "";
				for(let i=0;i < subjectsData.length;i++){
					options = options + "<option value='" + subjectsData[i].name.replace(/'/g, "&#39;") + "'> " + subjectsData[i].name + " </option>";
				}
				$('.selectSubject').html(" <option disabled selected value=''> Select Subject </option> " + options);
				
			},
			error : function(e) {
				alert("Please Refresh The Page.")
				display(e);
			}
		});
	});
	$('.selectProgramStructure').on('change', function(){
		let id = $(this).attr('data-id');
		let options = "<option>Loading... </option>";
		$('#programId').html(options);
		$('.selectSubject').html(options);
		var data = {
			programStructureId:this.value,
			consumerTypeId:$('#consumerTypeId').val()
		}
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "getDataByProgramStructure",   
			data : JSON.stringify(data),
			success : function(data) {
				var programData = data.programData;
				var subjectsData = data.subjectsData;
				options = "";
				let allOption = "";
				for(let i=0;i < programData.length;i++){
					allOption = allOption + ""+ programData[i].id +",";
					options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
				}
				allOption = allOption.substring(0,allOption.length-1);
				$('#programId').html("<option value='"+ allOption +"'>All</option>" + options);
				options = ""; 
				allOption = "";
				for(let i=0;i < subjectsData.length;i++){
					options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
				}
				$('.selectSubject').html(" <option disabled selected value=''> Select Subject </option> " + options);
			},
			error : function(e) {
				alert("Please Refresh The Page.")
				display(e);
			}
		});
	});
	$('.selectProgram').on('change', function(){
		let id = $(this).attr('data-id');
		let options = "<option>Loading... </option>";
		$('.selectSubject').html(options); 
		var data = {
			programId:this.value,
			consumerTypeId:$('#consumerTypeId').val(),
			programStructureId:$('#programStructureId').val()
		}
		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "getDataByProgram",   
			data : JSON.stringify(data),
			success : function(data) {
				var subjectsData = data.subjectsData;
				options = "";
				for(let i=0;i < subjectsData.length;i++){
					options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
				}
				$('.selectSubject').html(" <option disabled selected value=''> Select Subject </option> " + options);
			},
			error : function(e) {
				alert("Please Refresh The Page.")
				console.log("ERROR: ", e);
				display(e);
			}
		});
	});

	$('#payment_applicable').change(function(){
		let result = $(this).val();
		if(result == 'Y'){
			$('#payment_amount_block').show();
		}else{
			$('#payment_amount_block').hide();
		}
	});

	
});


	$(document).ready (function(){
		$('.table').DataTable();
		$('.delete_btn').click(function(){
			if(!confirm("Do you want to delete these record")){
				return false;
			}
			var pss_id = $(this).attr('data-pss_id');
			var year = $(this).attr('data-year');
			var month = $(this).attr('data-month');
			
			$.ajax({
				url:"levelBasedSynopsisConfigDelete?year=" + year + "&month="+month + "&program_sem_subject_id=" + pss_id ,
				method:"POST",
				success:function(response){
					if(response.status == "success"){
						alert("Successfully Deleted Record");
						window.location.href = "levelBasedSynopsisConfigForm";
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
	});

</script>

</body>

</html>
