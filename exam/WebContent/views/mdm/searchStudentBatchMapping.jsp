<html class="no-js">
<!--<![endif]-->
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<link rel="stylesheet"
	href="https://use.fontawesome.com/releases/v5.8.2/css/all.css"
	integrity="sha384-oS3vJWv+0UjzBfQzYUhtDYW+Pj2yciDJxpsK1OYPAYjqT085Qq/1cq5FLXAZQ7Ay"
	crossorigin="anonymous">
<link rel="stylesheet"
	href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/multiple-select-wenzhixin/multiple-select.css">
<link rel="stylesheet"
	href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Search Student Batch Mapping" name="title" />
</jsp:include>

<style>
.select_multiple input {
	height: unset !important;
	width: unset !important;
}
</style>
<script
	src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Search Student Batch Mapping</legend>
			</div>
			<%@ include file="../messages.jsp"%>

			<form:form modelAttribute="mappingBean" method="post"
				action="searchStudentBatchMapping">

				<fieldset>
					<div class="panel-body">
						<div class="col-md-6 column">
							<div class="form-group">
								<label for="consumerType">Consumer Type</label>
								<form:select id="consumerType" path="consumerType"
									name="consumerType" class="form-control" required="required">
									<option disabled selected value="">Select Consumer
										Type</option>
									<c:forEach var="consumerType" items="${consumerType}">
										<option value="<c:out value="${consumerType.name}"/>">
											<c:out value="${consumerType.name}" />
										</option>
									</c:forEach>
								</form:select>
							</div>


							<div class="form-group">
								<label for="prgmStructApplicable">Program Structure</label>
								<form:select id="prgmStructApplicable"
									path="prgmStructApplicable" required="required"
									class="form-control"
									itemValue="${programSubjectMappingBean.prgmStructApplicable}">
									<option disabled selected value="">Select Program
										Structure</option>
								</form:select>
							</div>

							<div class="form-group">
								<label for="program">Program Name</label>
								<form:select id="program" path="program" type="text"
									required="required" class="form-control"
									itemValue="${programSubjectMappingBean.program}">
									<option disabled selected value="">Select Program Name</option>
								</form:select>
							</div>


							<div class="form-group">
								<label for="sem">Select Semester</label>
								<form:select id="sem" path="sem"
									class="form-control batchChange" required="required"
									itemValue="${fileBean.sem}">
									<form:option value="">Select Semester</form:option>
									<%-- <form:options items="${semesterList}" /> --%>
									<form:option value="1">1</form:option>
									<form:option value="2">2</form:option>
									<form:option value="3">3</form:option>
									<form:option value="4">4</form:option>
									<form:option value="4">MBA WX 3 and 4 Electives</form:option>
									<form:option value="5">5</form:option>
									<form:option value="6">6</form:option>
									<form:option value="7">7</form:option>
								</form:select>
							</div>

							<div class="form-group">
								<label for="acadYear">Select Acad Year</label>
								<form:select id="acadYear" path="acadYear"
									class="form-control batchChange" required="required"
									itemValue="${fileBean.acadYear}">
									<form:option value="">Select Academic Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>

							<div class="form-group">
								<label for="acadMonth">Select Acad Month</label>
								<form:select id="acadMonth" path="acadMonth"
									class="form-control batchChange" required="required"
									itemValue="${fileBean.acadMonth}">
									<form:option value="">Select Academic Month</form:option>
									<form:options items="${monthList }" />
								</form:select>
							</div>

							<div class="form-group">
								<label for="batch">Select Batch</label> <select id="batchId"
									data-id="batchId" name="batchId" type="text"
									placeholder="Batch" class="form-control" required="required"
									itemValue="${mappingBean.batchId}">
									<option value="">Select Batch</option>
									<%-- <c:forEach var="batch" items="${batchList }">
								<option value="${batch.id }">
									<c:out value="${batch.name }"></c:out>
								</option>
							</c:forEach> --%>
								</select>
							</div>

							<div class="form-group">
								<label for="subject">Select Subject</label> <select id="subject"
									data-id="subject" name="subject" class="form-control"
									required="required" itemValue="${mappingBean.subject}">
									<option value="">Select Subject</option>
								</select>
							</div>

							<div class="form-group">
								<label class="control-label" for="isResit">Include Resit
									Students?</label> <input class="form-control"
									style="height: 30px; width: 30px;" type="checkbox" id="isResit"
									name="isResit" value="No" onchange="checkCheckbox()" />
							</div>

							<!-- <div class="select_multiple">
											<label for="student">Select Students</label> <select
												id="selectStudent" multiple="multiple" name="userId"
												placeholder="Select Students">
											</select>
										</div> -->
							<div class="form-group">
								<label class="control-label" for="submit"></label>
								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="searchStudentBatchMapping">Search</button>
								<button id="reset" type="reset" class="btn btn-danger"
									type="reset">Reset</button>
								<button id="cancel" name="cancel" class="btn btn-danger"
									formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					</div>
				</fieldset>
			</form:form>
			<div class="clearfix"></div>
			<br>
			<c:choose>

				<c:when test="${rowCount > 0}">
					<div class="sz-content-wrapper examsPage">
						<div class="sz-content">
							<h2>
								&nbsp;Student Batch Mapping Records <font size="2px">
									(${rowCount} Records Found)&nbsp; <a style="color: #23527c;"
									href="downloadStudentBatchMapping">DOWNLOAD TO EXCEL</a>
								</font>
							</h2>

							<div class="panel-body">

								<div class="panel-content-wrapper">
									<div class="table-responsive">
										<table class="table table-striped table-hover"
											style="font-size: 12px" id="searchStudentBatchMapping">
											<thead>
												<tr>
													<th>Sr. No.</th>
													<th>SAP ID</th>
													<th>Batch Name</th>
													<th>Subject Name</th>
													<th>Student Name</th>
													<th>Email-Id</th>
													<th>Image Url</th>
													<th>Phone Number</th>
												</tr>
											</thead>
											<tbody>

												<c:forEach var="bean" items="${userList }"
													varStatus="status">
													<tr>
														<td><c:out value="${status.count}" /></td>
														<td><c:out value="${bean.userId }"></c:out>
														<td><c:out value="${bean.name }"></c:out>
														<td><c:out value="${bean.subject }"></c:out>
														<td><c:out value="${bean.studentName }"></c:out>
														<td><c:out value="${bean.emailId }"></c:out>
														<td><a href='${bean.imageUrl }' target='_blank'><c:out
																	value="${bean.imageUrl }"></c:out></a>
														<td><c:out value="${bean.mobile }"></c:out>
													</tr>
												</c:forEach>
											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</c:when>

			</c:choose>

		</div>
	</section>
	<jsp:include page="../footer.jsp" />

	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->

	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script
		src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script
		src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

	<!-- 	<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script> -->
	<script
		src="https://unpkg.com/multiple-select@1.3.1/dist/multiple-select.min.js"></script>

	<!-- Get student list by batchId Start -->

	<script type="text/javascript">
		/* $(function() {
			$('#selectStudent').multipleSelect({
				filter : true,
			//     	  width: 250
			})
		}); */
		
		function checkCheckbox(){
			if($("#isResit").prop("checked")){
				$("#isResit").val("Yes");
			} else{
				$("#isResit").val("No");
			}			
		}

		$(document)
				.ready(
						function() {
							
							$("#isResit").val("No");
							
							$('.table').DataTable( {

						        initComplete: function () {
						        	 this.api().columns().every( function () {
						               var column = this;
						                var headerText = $(column.header()).text();
						                console.log("header :"+headerText);						         						              						            						             
						            } );
						        } 
						    } );
							
					/////////////////////////////////////////////////////
					/// data loading based on selection
							
					$('#consumerType').on('change', function(){
						
						
						let id = $(this).attr('data-id');
						
						
						let options = "<option>Loading... </option>";
						$('#prgmStructApplicable').html(options);
						$('#program').html(options);
						
						
						 
						var data = {
								id:this.value
						}
						console.log(this.value)
						
						console.log("===================> data id : " + id);
						$.ajax({
							type : "POST",
							contentType : "application/json",
							url : "getValueByConsumerType",   
							data : JSON.stringify(data),
							success : function(data) {
								console.log("SUCCESS Program Structure: ", data.programStructureData);
								console.log("SUCCESS Program: ", data.programData);
								
								var programData = data.programData;
								var programStructureData = data.programStructureData;
								
								
								options = "";
								
								
								//Data Insert For Program List
								//Start
								for(let i=0;i < programData.length;i++){
									
									options = options + "<option value='" + programData[i].name + "'> " + programData[i].name + " </option>";
								}
								
								
								console.log("==========> options\n" + options);
								$('#program').html(
										" <option disabled selected value=''> Select Program Name</option> " + options
								);
								//End
								options = ""; 
								
								//Data Insert For Program Structure List
								//Start
								for(let i=0;i < programStructureData.length;i++){
									
									options = options + "<option value='" + programStructureData[i].name + "'> " + programStructureData[i].name + " </option>";
								}
								
								
								console.log("==========> options\n" + options);
								$('#prgmStructApplicable').html(
										" <option disabled selected value=''> Select Program Structure </option> " + options
								);
								//End			
							},
							error : function(e) {
								
								alert("Please Refresh The Page.")
								
								console.log("ERROR: ", e);
								display(e);
							}
						});
												
					});
						
					///////////////////////////////////////////////////////
						
						
					$('#prgmStructApplicable').on('change', function(){
						
						
						let id = $(this).attr('data-id');
						
						
						let options = "<option>Loading... </option>";
						$('#program').html(options);
						
						
						 
						var data = {
								programStructureId:this.value,
								consumerTypeId:$('#consumerType').val()
						}
						console.log(this.value)
						
						console.log("===================> data id : " + $('#consumerType').val());
						$.ajax({
							type : "POST",
							contentType : "application/json",
							url : "getValueByProgramStructure",   
							data : JSON.stringify(data),
							success : function(data) {
								
								console.log("SUCCESS: ", data.programData);
								var programData = data.programData;
								
								
								options = "";
								
								
								//Data Insert For Program List
								//Start
								for(let i=0;i < programData.length;i++){
								
									options = options + "<option value='" + programData[i].name + "'> " + programData[i].name + " </option>";
								}
								
								
								console.log("==========> options\n" + options);
								$('#program').html(
										" <option disabled selected value=''> Select Program Structure </option> " + options
								);
								//End
											
							},
							error : function(e) {
								
								alert("Please Refresh The Page.")
								
								console.log("ERROR: ", e);
								display(e);
							}
						});
												
					});
					 

					/////////////////////////////////////////////////////////////

							//Get Batches by Year/Month

							$('.batchChange')
									.on(
											'change',
											function() {

												let options = "<option>Loading... </option>";
												$('#batchId').html(options);
												$("#subject").html("<option disabled selected value=''> Select Subject </option>");
												var data = {
													consumerTypeId : $('#consumerType').val(),
													program_structure : $('#prgmStructApplicable').val(),
													programId : $('#program').val(),
													sem : $('#sem').val(),
													acadYear : $('#acadYear')
															.val(),
													acadMonth : $('#acadMonth')
															.val()
												}
												console
														.log('Request data in : ')
												console.log(data)

												$
														.ajax({
															type : "POST",
															contentType : "application/json",
															url : "getBatchesByMasterKey",
															data : JSON
																	.stringify(data),
															success : function(
																	data) {

																var batchData = data.batchData;

																options = "";
																if (batchData.length <= 0) {
																	$(
																			'#batchId')
																			.html(" <option disabled selected value=''> No Batch Available </option> ");
																} else {
																	for (let i = 0; i < batchData.length; i++) {
																		options = options
																				+ "<option value='" + batchData[i].id + "'> "
																				+ batchData[i].name
																				+ " </option>";
																	}
																	$(
																			'#batchId')
																			.html(
																					" <option disabled selected value=''> Select Batch </option> "
																							+ options);
																}
															},

															error : function(e) {
																alert("Please Refresh The Page.")
																console
																		.log(
																				"ERROR: ",
																				e);
																display(e);
															}
														});
											});
							
							$("#batchId").on("change",function(){
								let options = "<option>Loading... </option>";
								$('#subject').html(options);								

								$.ajax({
											type : "GET",											
											url : "getSubjectListByBatchId?id="+$("#batchId").val(),											
											success : function(
													data) {
												
												
												var subjectData = data;
												console.log(data);
												options = "";
												if (subjectData.length <= 0) {
													$(
															'#subject')
															.html(" <option disabled selected value=''> No Subject Available </option> ")
												} else {
													for (let i = 0; i < subjectData.length; i++) {
														options = options
																+ "<option value='" + subjectData[i].prgm_sem_subj_id + "~" + subjectData[i].startDate + "'> "
																+ subjectData[i].subject + "  [" + subjectData[i].startDate + "]"
																+ " </option>";
													}
													$(
															'#subject')
															.html(
																	" <option disabled selected value=''> Select Subject </option> "
																			+ options);
												}
											},

											error : function(e) {
												alert("Please Refresh The Page.")
												console
														.log(
																"ERROR: ",
																e);
												display(e);
											}
										});
							});

							//Get Students For Batch Insert --> START	

							/* $('#batchId')
									.on(
											'change',
											function() {
												let id = $(this)
														.attr('data-id');

												let options = "<option>Loading... </option>";
												$('#selectStudent').html(
														options);

												var data = {
													batchId : this.value,
													sem : $('#sem').val(),
													acadYear : $('#acadYear')
															.val(),
													acadMonth : $('#acadMonth')
															.val()
												}

												$
														.ajax({
															type : "POST",
															contentType : "application/json",
															url : "getMBAStudentsList",
															data : JSON
																	.stringify(data),
															success : function(
																	data) {

																var studentsData = data.studentList;

																options = "";
																//Data Insert For Subjects List

																for (let i = 0; i < studentsData.length; i++) {

																	options = options
																			+ "<option value='" + studentsData[i].sapid + "'> "
																			+ studentsData[i].sapid
																			+ " </option>";
																}

																$(
																		'#selectStudent')
																		.html(
																				options);
																$(
																		'#selectStudent')
																		.multipleSelect(
																				'refresh');
															},
															error : function(e) {
																alert("Please Refresh The Page.")
																console
																		.log(
																				"ERROR: ",
																				e);
																display(e);
															}
														});
											}) */
											
							<!-- Get student list by batchId End -->
						});
	</script>
</body>
</html>