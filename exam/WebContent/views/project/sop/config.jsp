<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../../jscss.jsp">
	<jsp:param value="Make SOP Live" name="title" />
</jsp:include>




<body class="inside">

	<%@ include file="../../header.jsp"%> 

	<section class="content-container login">
		<div class="container-fluid customTheme">
			
			<div class="row"><legend>Make SOP Live</legend></div>
				<%@ include file="../../messages.jsp"%>
					
				<form:form modelAttribute="inputBean" method="post" action="levelBasedSOPConfig">
					<div class="row">
						<div class="col-md-16 column">
							<div class="row">
								<div class="col-md-6">
									<div class="form-group">
										<label for="consumerType">Consumer Type</label>
											<form:select data-id="consumerTypeDataId" id="consumer_type" path="consumer_type" class="form-control selectConsumerType" required="required">
												<form:option value="">Select Consumer Type</form:option>
												<form:options items="${consumerType}" itemValue="id" itemLabel="name"/>
											</form:select>
									</div>
								</div>
						
								<div class="col-md-6">
									<div class="form-group">
										<label for="program_structure">Program Structure</label>
										<form:select data-id="programStructureDataId" id="programStructureId" path="program_structure" class="form-control" required="required">
											<form:option value="">Select Program Structure</form:option>
										</form:select>
									</div>
								</div> 
							
								<div class="col-md-6">
									<div class="form-group">
										<label for="program">Program</label>
										<form:select data-id="programDataId" id="programId" path="program" class="form-control" required="required">
											<form:option value="">Select Program Structure</form:option>
										</form:select>
									</div>
								</div>
						
								<div class="col-md-4 column">
									<div class="form-group">
										<label for="subjectId">Subject</label>
										<form:select data-id="subjectId" id="subjectId" path="subject" class="selectSubject" required="required">
											<form:option value="">Select Program Structure</form:option>
										</form:select>
									</div>
								</div>
								
						
								<div class="col-md-6">
									<div class="form-group">
										<label for="month">Month</label>
										<form:select id="month" path="month" class="form-control" required="required">
											<form:option value="">Select Month</form:option>
											<form:options items="${monthList}" />
										</form:select>
									</div>
								</div>
								<div class="col-md-6">
									<div class="form-group">
										<label for="year">Year</label>
										<form:select id="year" path="year" class="form-control" required="required">
											<form:option value="">Select Year</form:option>
											<form:options items="${yearList}" />
										</form:select>
									</div>
								</div>
						
								<div class="col-md-6">
									<div class="form-group">
										<label for="start_date">Start Date</label>
										<form:input type="datetime-local" id="start_date" path="start_date" />
									</div>
								</div>
								<div class="col-md-6">
									<div class="form-group">
										<label for="end_date">End Date</label>
										<form:input type="datetime-local" id="end_date" path="end_date" />
									</div>
								</div>
								<div class="col-md-6">
									<div class="form-group">
										<label for="live">Status</label>
										<form:select id="live" path="live">
											<form:option value="Y">Live</form:option>
											<form:option value="N">Not live</form:option>
										</form:select>
									</div>
								</div>
						
								<div class="col-md-6">
									<div class="form-group">
										<label for="max_attempt">Max Attempt</label>
										<form:input id="max_attempt" min="0" path="max_attempt" type="number" value="1" />
									</div>
								</div>
						
								<div class="col-md-6">
									<div class="form-group">
										<label for="payment_applicable">payment applicable</label>
										<form:select id="payment_applicable" path="payment_applicable">
											<option value="N">No</option>
											<option value="Y">Yes</option>
										</form:select>
									</div>
								</div>
								<div style="display:none" id="payment_amount_block" class="col-md-4 column">
									<div class="form-group">
										<label for="payment_applicable">Payment Amount</label>
										<form:input type="number" min="0" id="payment_amount" path="payment_amount" />
									</div>
								</div>
							</div>
							<br/>
							<div class="form-group">
								<button id="submit" name="submit" class="btn btn-large btn-primary">Make SOP Live</button>
							</div>
						</div>
					</div>
				</form:form>
				<div class="clearfix"></div>
				
				<div class="column">
					<legend>&nbsp;Live SOP </legend>
					<div class="table-responsive">
						<table class="table table-striped table-hover tables" style="font-size: 12px">
							<thead>
							<tr>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Consumer Type</th>
								<th>Program Structure</th>
								<th>Program</th>
								<th>Subject</th>
								<th>Status</th>
								<th>max_attempt</th>
								<th>payment applicable</th>
								<th>payment amount</th>
								<th>start date</th>
								<th>end date</th>
							</tr>
							</thead>
							<tbody>
							 	<c:forEach var="levelBasedSOPConfigBean" items="${ levelBasedSOPConfigBeansList }">
							 		<tr>
							 			<td>${ levelBasedSOPConfigBean.year }</td>
							 			<td>${ levelBasedSOPConfigBean.month }</td>
							 			<td>${ levelBasedSOPConfigBean.consumer_type }</td>
							 			<td>${ levelBasedSOPConfigBean.program_structure }</td>
							 			<td>${ levelBasedSOPConfigBean.program }</td>
							 			<td>${ levelBasedSOPConfigBean.subject }</td>
							 			<td>${ levelBasedSOPConfigBean.live == "Y" ? "Live" : "Not Live" }</td>
							 			<td>${ levelBasedSOPConfigBean.max_attempt }</td>
							 			<td>${ levelBasedSOPConfigBean.payment_applicable }</td>
							 			<td>${ levelBasedSOPConfigBean.payment_amount }</td>
							 			<td>${ levelBasedSOPConfigBean.start_date }</td>
							 			<td>${ levelBasedSOPConfigBean.end_date }</td>
							 		</tr>
							 	</c:forEach>
							</tbody>
						</table>
					</div>
				</div> 
		</div>
	</section>

	<jsp:include page="../../footer.jsp" />
	<script src="assets/js/jquery-1.11.3.min.js"></script>
	<script src="assets/js/bootstrap.js"></script>
	<script src="assets/js/jquery.tabledit.js"></script>
	<script src="resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	
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
						console.log("ps : ", options)
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
	</script>
</body>

</html>
