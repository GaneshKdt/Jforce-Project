<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Assignment Files" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme"">
			
			<div class="row"><legend>Select Year-Month</legend></div>
			<%@ include file="messages.jsp"%>
				
				<form:form modelAttribute="filesSet" method="post"	enctype="multipart/form-data" action="uploadAssignmentFiles">
					<div class="row">
					<div class="col-md-18 column">
					
					<div class="row">
						<div class="col-md-4 column">
						<div class="form-group">
							<label for="startDate">Exam Year</label>
							<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${filesSet.year}">
								<form:option value="">Select Exam Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
						</div>
						<div class="col-md-4 column">
						<div class="form-group">
							<label for="startDate">Exam Month</label>
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${filesSet.month}">
								<form:option value="">Select Exam Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label for="startDate">Start Date</label>
							<form:input path="startDate" id="startDate" type="datetime-local" />
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label for="endDate">End Date</label>
							<form:input path="endDate" id="endDate" type="datetime-local" />
						</div>
						</div> 
					<!-- 	/////////////////////////////////////////////////////////////////// -->
					
						<div class="col-md-4 column">
							<div class="form-group">
							<label for="consumerType">Consumer Type</label>
							<select data-id="consumerTypeDataId" id="consumerTypeId" name="consumerTypeId"  class="selectConsumerType" required="required">
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
							<select data-id="programStructureDataId" id="programStructureId" name="programStructureId" class="selectProgramStructure">
								<option disabled selected value="">Select Program Structure</option>

							</select>
							</div>
						</div> 
							
						<div class="col-md-4 column">
							<div class="form-group">
							<label for="Program">Program</label>
							<select data-id="programDataId" id="programId" name="programId" class="selectProgram">
								<option disabled selected value="">Select Program</option>

							</select>
							</div>
						</div>
						
						<!-- /////////////////////////////////////////////////////////////////// -->
						
					
					</div>
					<br>
					
					<legend>Select Files to Upload <font size="5px">(For Project, select Project submission guidelines as File)</font></legend>
					<% for(int i = 0 ; i < 10 ; i++) {%>
					<div class="row">
					<div class="col-md-5 column">
							<div class="form-group">
							<input id="fileData" type="file" name="assignmentFiles[<%=i%>].fileData" > 
							</div>
					</div>
					
					<div class="col-md-8 column">
							<div class="form-group">
							<select  data-id="subjectId" name="assignmentFiles[<%=i%>].subject" class="selectSubject">
							<option disabled selected value="">Select Subject</option>
							
							</select>
							</div>
					</div>
					
					
							
					<div class="col-md-3 column">
							<div class="form-group">
							<select data-id="<%=i%>" id="programid<%=i%>" name="assignmentFiles[<%=i%>].programId" class="selectProgram">
								<option disabled selected value="">Select Program</option>

							</select>
							</div>
					</div>
					
					
					<div class="col-md-3 column">
							<div class="form-group">
							<select data-id="<%=i%>" id="programStructureid<%=i%>" name="assignmentFiles[<%=i%>].programStructureId" class="selectProgramStructure">
								<option disabled selected value="">Select Program Structure</option>

							</select>
							</div>
					</div>
					
						<div class="col-md-3 column">
							<div class="form-group">
							<select id="consumerTypeid<%=i%>" name="assignmentFiles[<%=i%>].consumerTypeId" class="selectConsumerType" >
							<option disabled selected value="">Select Consumer Type</option>
							<c:forEach var="consumerType" items="${consumerTypeList}">
				                <option value="<c:out value="${consumerType.id}" />">
				                  <c:out value="${consumerType.name}" />
				                </option>
				            </c:forEach>
							</select>
							</div>
					</div>
					
			
				
					</div>
				
					
					<%} %>
					
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="uploadAssignmentFiles">Upload</button>
					</div>
					</div>

			</div>

			</form:form>
		</div>
	</section>

	<jsp:include page="footer.jsp" />

<script type="text/javascript">

$(document).ready (function(){
	
	
///////////////////////////////////////////////////////////////////
	
	
	$('.selectConsumerType').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programStructureId').html(options);
	$('#programId').html(options);
	$('.selectSubject').html(options);
	
	 
	var data = {
			id:this.value
	}
console.log(this.value)
	
	console.log("===================> data id : " + id);
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByConsumerType",   
		data : JSON.stringify(data),
		success : function(data) {
			console.log("SUCCESS Program Structure: ", data.programStructureData);
			console.log("SUCCESS Program: ", data.programData);
			console.log("SUCCESS Subject: ", data.subjectsData);
			var programData = data.programData;
			var programStructureData = data.programStructureData;
			var subjectsData = data.subjectsData;
			
			options = "";
			let allOption = "";
			
			//Data Insert For Program List
			//Start
			for(let i=0;i < programData.length;i++){
				allOption = allOption + ""+ programData[i].id +",";
				options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			console.log("==========> options\n" + options);
			$('#programId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			options = ""; 
			allOption = "";
			//Data Insert For Program Structure List
			//Start
			for(let i=0;i < programStructureData.length;i++){
				allOption = allOption + ""+ programStructureData[i].id +",";
				options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			console.log("==========> options\n" + options);
			$('#programStructureId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			
			options = ""; 
			allOption = "";
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
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
	
	
		$('.selectProgramStructure').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programId').html(options);
	$('.selectSubject').html(options);
	
	 
	var data = {
			programStructureId:this.value,
			consumerTypeId:$('#consumerTypeId').val()
	}
	console.log(this.value)
	
	console.log("===================> data id : " + $('#consumerTypeId').val());
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByProgramStructure",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data.programData);
			var programData = data.programData;
			var subjectsData = data.subjectsData;
			
			options = "";
			let allOption = "";
			
			//Data Insert For Program List
			//Start
			for(let i=0;i < programData.length;i++){
				allOption = allOption + ""+ programData[i].id +",";
				options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			console.log("==========> options\n" + options);
			$('#programId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			
			options = ""; 
			allOption = "";
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
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

	
		$('.selectProgram').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('.selectSubject').html(options);
	
	 
	var data = {
			programId:this.value,
			consumerTypeId:$('#consumerTypeId').val(),
			programStructureId:$('#programStructureId').val()
	}
	console.log(this.value)
	
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByProgram",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data.subjectsData);
			
			var subjectsData = data.subjectsData;
			
			
			
			
			options = ""; 
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
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

//////////////////////////////////////////////








	
	
	
});



</script>

</body>
</html>
 --%>





<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<jsp:include page="jscss.jsp">
	<jsp:param value="Upload Assignment Files" name="title" />
</jsp:include>
<head>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
<style>
.dataTables_filter>label>input {
	float: right !important;
}

.toggleListWell {
	cursor: pointer !important;
	margin-bottom: 0px !important;
}

.toggleWell {
	background-color: white !important;
}

input[type="radio"] {
	width: auto !important;
	height: auto !important;
}

.optionsWell {
	padding: 0px 10px;
}

.subject-checkbox {
    width: 16px;
    height: 16px;
  }
  

</style>

</head>



<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend>Select Year-Month</legend>
			</div>
			<%@ include file="messages.jsp"%>
			<div class="panel-body clearfix">
			
			
			<div class="panel-group" style="margin-top: 30px" id="accordion" role="tablist" aria-multiselectable="true">
			
					<c:if test="${ qpcopycaseList != null && qpcopycaseList.size() > 0 }">
						
						<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="DT1List">
							<h4 class="panel-configuration">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#DT1-list" aria-controls="DT1-list">
									QP Copy Cases List Of last 5 cycle with matching Percentage  
								</a>
							</h4>
						</div>

						<div id="DT1-list" class="panel-collapse collapse" role="tabpanel"
							aria-labelledby="DT1List">
							<div class="panel-body">
								<div class="table-responsive">
									<table id="DT1-list-table" class="table table-striped"
										style="width: 100% !important;">
										<thead>
											<tr>
												<!-- <th>Month</th>
												<th>Year</th> -->
												<th>Subject Name</th>
												<th>Subject code</th>
												<th>Exam Cycle</th>
												<th>Matching Percentage</th>
												<th>Uploaded pdf</th>
												<th>Matched pdf</th>
												<th>Delete</th>
												<th>View Matching Text</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="qp" items="${qpcopycaseList}">
												<fmt:parseNumber value="${qp.matching}" var="matching"
													integerOnly="true" type="number" />
											     <tr>
												  <td><c:out value="${qp.subject}" /></td>
												  <td><c:out value="${qp.subjectCode}" /></td>
												  <td><c:out value="${qp.month} ${qp.year}" /></td>
												  <td><c:out value="${matching}" /></td>
												  <td><c:out value="" /> <a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FILES_PATH')" />${qp.firstFile}" target="_blank" >Uploaded File</a></td>
												  <td><c:out value="" /> <a href="${qp.secondFile}" target="_blank" >Matched File</a></td>
												  <td><input type="checkbox" class="subject-checkbox filePath" name="deleteFile" id="check" value="${qp.firstFile}" /></td>
												  <td><a href="#" data-toggle="modal" data-target="#myModal" data-value="${qp.commonText}">View</a></td> 
													
												</tr>
											      
											</c:forEach>
										</tbody>
									</table>
								</div>
								<div>
								<button type="button" class="btn btn-primary" id="deleteButton">Delete Selected Files</button>
								</div>
							</div>
						</div>
						</div>
					</c:if>
				</div>


				 <!-- Modal -->
				<div class="modal fade" id="myModal" tabindex="-1" role="dialog"
					aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"
									aria-hidden="true">&times;</button>
								<h4 class="modal-title" id="myModalLabel">Matching Text</h4>
							</div>
							 <div class="modal-body">
								<p id="matchingText"></p>
							</div> 
							<div class="modal-footer">
								<button type="button" class="btn btn-default"
									data-dismiss="modal">Close</button>
							</div>
						</div>
					</div>
				</div>
			



				<form:form id='QPUploadForm' modelAttribute="filesSet" method="post" enctype="multipart/form-data" action="uploadAssignmentFiles">
					<fieldset>
					<div id="errorMsg" style="display:none"></div>
			<div id="successMsg" style="display:none"></div>
						<div class="row">
							<div class="col-md-18 column">

								<div class="row">
									<div class="col-md-4 column">
										<div class="form-group">
											<label for="startDate">Exam Year</label>
											<form:select id="year" path="year" type="text"
												placeholder="Year" class="form-control" required="required"
												itemValue="${filesSet.year}">
												<form:option value="">Select Exam Year</form:option>
												<form:options items="${yearList}" />
											</form:select>
										</div>
									</div>
									<div class="col-md-4 column">
										<div class="form-group">
											<label for="startDate">Exam Month</label>
											<form:select id="month" path="month" type="text"
												placeholder="Month" class="form-control" required="required"
												itemValue="${filesSet.month}">
												<form:option value="">Select Exam Month</form:option>
												<form:option value="Apr">Apr</form:option>
												<form:option value="Jun">Jun</form:option>
												<form:option value="Sep">Sep</form:option>
												<form:option value="Dec">Dec</form:option>
											</form:select>
										</div>
									</div>

									<div class="col-md-4 column">
										<div class="form-group">
											<label for="startDate">Start Date</label>
											<form:input path="startDate" id="startDate"
												type="datetime-local" />
										</div>
									</div>

									<div class="col-md-4 column">
										<div class="form-group">
											<label for="endDate">End Date</label>
											<form:input path="endDate" id="endDate" type="datetime-local" />
										</div>
									</div>
									<!-- 	/////////////////////////////////////////////////////////////////// -->

									<div class="col-md-4 column">
										<div class="form-group">
											<label for="consumerType">Consumer Type</label> <select
												data-id="consumerTypeDataId" id="consumerTypeId"
												name="consumerTypeId" class="selectConsumerType"
												required="required">
												<option disabled selected value="">Select Consumer
													Type</option>
												<c:forEach var="consumerType" items="${consumerType}">
													<option value="<c:out value="${consumerType.id}"/>">
														<c:out value="${consumerType.name}" />
													</option>
												</c:forEach>
											</select>
										</div>
									</div>

									<div class="col-md-4 column">
										<div class="form-group">
											<label for="programStructure">Program Structure</label> <select
												data-id="programStructureDataId" id="programStructureId"
												name="programStructureId" class="selectProgramStructure">
												<option disabled selected value="">Select Program
													Structure</option>

											</select>
										</div>
									</div>

									<div class="col-md-4 column">
										<div class="form-group">
											<label for="Program">Program</label> <select
												data-id="programDataId" id="programId" name="programId"
												class="selectProgram">
												<option disabled selected value="">Select Program</option>

											</select>
										</div>
									</div>

									<!-- /////////////////////////////////////////////////////////////////// -->


								</div>
								<br>
								<%-- 			<c:if test="${ qpcopycaseList != null && qpcopycaseList.size() > 0 }"> --%>

								<legend>
									Select Files to Upload <font size="5px">(For Project,
										select Project submission guidelines as File)</font>
								</legend>
								<%
									for (int i = 0; i < 10; i++) {
								%>
								<div class="row">
									<div class="col-md-5 column">
										<div class="form-group">
											<input id="fileData" type="file"
												name="assignmentFiles[<%=i%>].fileData">
										</div>
									</div>

									<div class="col-md-8 column">
										<div class="form-group">
											<select data-id="subjectId"
												name="assignmentFiles[<%=i%>].subject" class="selectSubject">
												<option disabled selected value="">Select Subject</option>

											</select>
										</div>
									</div>



									<%-- <div class="col-md-3 column">
							<div class="form-group">
							<select data-id="<%=i%>" id="programid<%=i%>" name="assignmentFiles[<%=i%>].programId" class="selectProgram">
								<option disabled selected value="">Select Program</option>

							</select>
							</div>
					</div>
					 --%>
									<%-- 		
					<div class="col-md-3 column">
							<div class="form-group">
							<select data-id="<%=i%>" id="programStructureid<%=i%>" name="assignmentFiles[<%=i%>].programStructureId" class="selectProgramStructure">
								<option disabled selected value="">Select Program Structure</option>

							</select>
							</div>
					</div>
					
						<div class="col-md-3 column">
							<div class="form-group">
							<select id="consumerTypeid<%=i%>" name="assignmentFiles[<%=i%>].consumerTypeId" class="selectConsumerType" >
							<option disabled selected value="">Select Consumer Type</option>
							<c:forEach var="consumerType" items="${consumerTypeList}">
				                <option value="<c:out value="${consumerType.id}" />">
				                  <c:out value="${consumerType.name}" />
				                </option>
				            </c:forEach>
							</select>
							</div>
					</div>
					 --%>


								</div>


								<%
									}
								%>


								<div class="form-group">
									<button id="submit" name="submit"
										class="btn btn-large btn-primary"
										formaction="uploadAssignmentFiles">Upload</button>
								</div>
							</div>

						</div>


					</fieldset>
				</form:form>




				<!-- </div> -->






				<%-- <div class="panel-group" style="margin-top: 30px" id="accordion" role="tablist" aria-multiselectable="true">
					<c:if test="${ qpcopycaseList != null && qpcopycaseList.size() > 0 }">
						
						<div class="panel panel-default">
						<div class="panel-heading" role="tab" id="DT1List">
							<h4 class="panel-configuration">
								<a role="button" data-toggle="collapse" data-parent="#accordion" href="#DT1-list" aria-controls="DT1-list">
									QP Copy Cases List last 5 cycle matching  Count: ${qpcopycaseList.size()}
								</a>
							</h4>
						</div>

						<div id="DT1-list" class="panel-collapse collapse" role="tabpanel"
							aria-labelledby="DT1List">
							<div class="panel-body">
								<!-- 								<h5></h5> -->
								<div class="table-responsive">
									<table id="DT1-list-table" class="table table-striped"
										style="width: 100% !important;">
										<thead>
											<tr>
												<!-- <th>Month</th>
												<th>Year</th> -->
												<th>Subject Name</th>
												<th>Subject code</th>
												<th>Exam Cycle</th>
												<th>Matching Percentage</th>
												<th>Current pdf</th>
												<th>Matched pdf</th>
												<th>Delete</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach var="qp" items="${qpcopycaseList}">
												<fmt:parseNumber value="${qp.matching}" var="matching"
													integerOnly="true" type="number" />
												<tr>
																										<%-- <td><c:out value="${DT1Bean.month}"/></td>
<%--  													<td><c:out value="${DT1Bean.year}"/></td>  
													<td><c:out value="${qp.subject}" /></td>
													<td><c:out value="${qp.subjectCode}" /></td>
													<td><c:out value="${qp.month} ${qp.year}" /></td>
													<td><c:out value="${matching}" /></td>
													<td><c:out value="" /> <a href="${qp.firstFile}">Your
															File</a></td>
													<td><c:out value="" /> <a href="${qp.secondFile}">Matched
															File</a></td>

													<td><button id="deletFile" value="${qp.firstFile}">Delete
															File</button></td>

												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</div>
						</div>
					</c:if>
				</div> --%>
			</div>
	</section>


	<jsp:include page="footer.jsp" />

	<script type="text/javascript">
		$(document)
				.ready(
						function() {

							///////////////////////////////////////////////////////////////////

							$('.selectConsumerType')
									.on(
											'change',
											function() {

												let id = $(this)
														.attr('data-id');

												let options = "<option>Loading... </option>";
												$('#programStructureId').html(
														options);
												$('#programId').html(options);
												$('.selectSubject').html(
														options);

												var data = {
													id : this.value
												}
												console.log(this.value)

												console
														.log("===================> data id : "
																+ id);
												$
														.ajax({
															type : "POST",
															contentType : "application/json",
															url : "getDataByConsumerType",
															data : JSON
																	.stringify(data),
															success : function(
																	data) {
																console
																		.log(
																				"SUCCESS Program Structure: ",
																				data.programStructureData);
																console
																		.log(
																				"SUCCESS Program: ",
																				data.programData);
																console
																		.log(
																				"SUCCESS Subject: ",
																				data.subjectsData);
																var programData = data.programData;
																var programStructureData = data.programStructureData;
																var subjectsData = data.subjectsData;

																options = "";
																let allOption = "";

																//Data Insert For Program List
																//Start
																for (let i = 0; i < programData.length; i++) {
																	allOption = allOption
																			+ ""
																			+ programData[i].id
																			+ ",";
																	options = options
																			+ "<option value='" + programData[i].id + "'> "
																			+ programData[i].name
																			+ " </option>";
																}
																allOption = allOption
																		.substring(
																				0,
																				allOption.length - 1);

																//console.log("==========> options\n" + options);
																$('#programId')
																		.html(
																				"<option value='"+ allOption +"'>All</option>"
																						+ options);
																//End
																options = "";
																allOption = "";
																//Data Insert For Program Structure List
																//Start
																for (let i = 0; i < programStructureData.length; i++) {
																	allOption = allOption
																			+ ""
																			+ programStructureData[i].id
																			+ ",";
																	options = options
																			+ "<option value='" + programStructureData[i].id + "'> "
																			+ programStructureData[i].name
																			+ " </option>";
																}
																allOption = allOption
																		.substring(
																				0,
																				allOption.length - 1);

																//console.log("==========> options\n" + options);
																$(
																		'#programStructureId')
																		.html(
																				"<option value='"+ allOption +"'>All</option>"
																						+ options);
																//End

																options = "";
																allOption = "";
																//Data Insert For Subjects List
																//Start
																for (let i = 0; i < subjectsData.length; i++) {
																	console
																			.log(subjectsData[i].name);
																	options = options
																			+ "<option value='"
																			+ subjectsData[i].name
																					.replace(
																							/'/g,
																							"&#39;")
																			+ "'> "
																			+ subjectsData[i].name
																			+ " </option>";
																}

																console
																		.log("==========> options2\n"
																				+ options);
																$(
																		'.selectSubject')
																		.html(
																				" <option disabled selected value=''> Select Subject </option> "
																						+ options);
																//End

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

							///////////////////////////////////////////////////////

							$('.selectProgramStructure')
									.on(
											'change',
											function() {

												let id = $(this)
														.attr('data-id');

												let options = "<option>Loading... </option>";
												$('#programId').html(options);
												$('.selectSubject').html(
														options);

												var data = {
													programStructureId : this.value,
													consumerTypeId : $(
															'#consumerTypeId')
															.val()
												}
												//console.log(this.value)

												//console.log("===================> data id : " + $('#consumerTypeId').val());
												$
														.ajax({
															type : "POST",
															contentType : "application/json",
															url : "getDataByProgramStructure",
															data : JSON
																	.stringify(data),
															success : function(
																	data) {

																//console.log("SUCCESS: ", data.programData);
																var programData = data.programData;
																var subjectsData = data.subjectsData;

																options = "";
																let allOption = "";

																//Data Insert For Program List
																//Start
																for (let i = 0; i < programData.length; i++) {
																	allOption = allOption
																			+ ""
																			+ programData[i].id
																			+ ",";
																	options = options
																			+ "<option value='" + programData[i].id + "'> "
																			+ programData[i].name
																			+ " </option>";
																}
																allOption = allOption
																		.substring(
																				0,
																				allOption.length - 1);

																//console.log("==========> options\n" + options);
																$('#programId')
																		.html(
																				"<option value='"+ allOption +"'>All</option>"
																						+ options);
																//End

																options = "";
																allOption = "";
																//Data Insert For Subjects List
																//Start
																for (let i = 0; i < subjectsData.length; i++) {

																	options = options
																			+ "<option value='" + subjectsData[i].name + "'> "
																			+ subjectsData[i].name
																			+ " </option>";
																}

																console
																		.log("==========> options\n"
																				+ options);
																$(
																		'.selectSubject')
																		.html(
																				" <option disabled selected value=''> Select Subject </option> "
																						+ options);
																//End

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

							/////////////////////////////////////////////////////////////

							$('.selectProgram')
									.on(
											'change',
											function() {

												let id = $(this)
														.attr('data-id');

												let options = "<option>Loading... </option>";
												$('.selectSubject').html(
														options);

												var data = {
													programId : this.value,
													consumerTypeId : $(
															'#consumerTypeId')
															.val(),
													programStructureId : $(
															'#programStructureId')
															.val()
												}
												//console.log(this.value)

												$
														.ajax({
															type : "POST",
															contentType : "application/json",
															url : "getDataByProgram",
															data : JSON
																	.stringify(data),
															success : function(
																	data) {

																console
																		.log(
																				"SUCCESS: ",
																				data.subjectsData);

																var subjectsData = data.subjectsData;

																options = "";
																//Data Insert For Subjects List
																//Start
																for (let i = 0; i < subjectsData.length; i++) {

																	options = options
																			+ "<option value='" + subjectsData[i].name + "'> "
																			+ subjectsData[i].name
																			+ " </option>";
																}

																console
																		.log("==========> options2\n"
																				+ options);
																$(
																		'.selectSubject')
																		.html(
																				" <option disabled selected value=''> Select Subject </option> "
																						+ options);
																//End

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

							//////////////////////////////////////////////

						});
	</script>
	<script
		src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js"></script>
	<script>
		$(document).ready(function() {
			var DT1Table = $('#DT1-list-table').DataTable({
				"autoWidth" : true,
			/* "scrollY": '500px',
			"scrollCollapse": false,
			"paging": true */
			});

		});
	</script>
	
	<!-- JavaScript code to select checkboxes -->
	<script>
  // Get all subject checkboxes
  const checkboxes = document.querySelectorAll('.subject-checkbox');
  
  // Add event listener to each checkbox
  checkboxes.forEach(checkbox => {
    checkbox.addEventListener('change', event => {
      // Get subject name of changed checkbox
      const subjectName = event.target.value;
      
      // Check all checkboxes with the same subject name
      checkboxes.forEach(checkbox => {
        if (checkbox.value === subjectName) {
          checkbox.checked = event.target.checked;
        }
      });
    });
  });
</script>

	<script>
  // Get the delete button element
  const deleteButton = document.querySelector('#deleteButton');
  const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
  
  // Add event listener to delete button click
  deleteButton.addEventListener('click', event => {
    // Get all selected checkboxes
    const checkboxes = document.querySelectorAll('.subject-checkbox:checked');

    // Create an array to store the checked values
    const checkedValues = [];

    // Loop through selected checkboxes and add their values to the checkedValues array
    checkboxes.forEach(checkbox => {
      checkedValues.push(checkbox.value);
    });
    if (checkedValues != null && checkedValues.length > 0) {
    hideSubmitBtn();
    $.ajax({
    	type:"POST",
		dataType:"json",
		contentType:"application/json",
        url: "/exam/m/admin/deletecopyCaseCheckFile",
		data:JSON.stringify(checkedValues),
        success: function(response) {
          // Handle successful response
         console.log("hello ");
        	if(response.error!=null){
            	console.log("hello ");
        		document.getElementById("successMsg").style.display="block";
				const div='<div class="alert alert-success alert-dismissible" id="successMsgDescription"></div>';
				document.getElementById("successMsg").innerHTML=div;
				document.getElementById("successMsgDescription").innerHTML=response+button;
				scrollToTop();
				showSubmitBtn();
			}else
        	if(response.success=='success'){
            	console.log("hello ");
        		document.getElementById("successMsg").style.display="block";
				const div='<div class="alert alert-success alert-dismissible" id="successMsgDescription"></div>';
				document.getElementById("successMsg").innerHTML=div;
				document.getElementById("successMsgDescription").innerHTML="QP File Successfully deleted"+button;
				scrollToTop();
				showSubmitBtn();
			}else
				{
				console.log("Hiii  ");
				document.getElementById("errorMsg").style.display="block";
				const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
				document.getElementById("errorMsg").innerHTML=div;
				document.getElementById("errorMsgDescription").innerHTML=response+button;
				scrollToTop();
				showSubmitBtn();
				}

        },
        error:function(err){
        	 console.log("hello ");
        	 console.log("err "+err);
          // Handle error
          document.getElementById("errorMsg").style.display="block";
          const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
        	document.getElementById("errorMsg").style.display="none";
			document.getElementById("errorMsg").innerHTML=div;
			document.getElementById("errorMsgDescription").innerHTML="Error in UnMark CC process : "+err+button;
			scrollToTop();

			if(err=='Successfully deleted'){
            	console.log("hello ");
        		document.getElementById("successMsg").style.display="block";
				const div='<div class="alert alert-success alert-dismissible" id="successMsgDescription"></div>';
				document.getElementById("successMsg").innerHTML=div;
				document.getElementById("successMsgDescription").innerHTML=err+button;
				scrollToTop();
				showSubmitBtn();
			}else{
				console.log("Hiii  ");
				document.getElementById("errorMsg").style.display="block";
				const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
				document.getElementById("errorMsg").innerHTML=div;
				document.getElementById("errorMsgDescription").innerHTML=err+button;
				scrollToTop();
				showSubmitBtn();
				}
        }
      });
  }
    // Loop through selected checkboxes and remove their associated rows
    checkboxes.forEach(checkbox => {
      const row = checkbox.closest('tr'); // Find the parent <tr> element'
//       const filePath = row.closest('.filePath'); 
//       console.log(filePath);
//       console.log(row.closest('.filePath'));
      row.remove(); // Remove the row from the page
    });
  });

  function scrollToTop() {
	  window.scrollTo(0, 0);
	}

  function hideSubmitBtn()
  {
  	document.getElementById("successMsg").style.display="none";
  	document.getElementById("errorMsg").style.display="none";
  }

  function showSubmitBtn()
  {
  	document.getElementById("errorMsg").style.display="block";
  	document.getElementById("successMsg").style.display="block";
  }

</script>

		 <script>
		$(document).ready(function(){
		  $('#myModal').on('show.bs.modal', function (event) {
		    var button = $(event.relatedTarget);
		    var value = button.data('value');
		    var modal = $(this);
		    modal.find('#matchingText').text(value);
		  });
		});
		</script> 


</body>
</html>
