
 <!DOCTYPE html>


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Upload Assignment Files" name="title" />
</jsp:include>
<style>
	.bgsuccess{color: #fff; 
	    background-color: #5cb85c;
	    border-color: #4cae4c;
	    color: #fff!important;
	    }
	.cardstyle{
	    padding:2rem;
	}
	.card-body .rotate i {
	    color: rgba(20, 20, 20, 0.15);
	    position: absolute;
	    left: 0;
	    left: auto;
	    right: 24px; 
	    bottom: 3px;
	    display: block;
	    -webkit-transform: rotate(-44deg);
	    -moz-transform: rotate(-44deg);
	    -o-transform: rotate(-44deg);
	    -ms-transform: rotate(-44deg);
	    transform: rotate(-44deg);
	}
	.dash-count{
		color:white!important;
	} 
</style>
<body class="inside"> 
	<section class="content-container login">
		<div class="container-fluid customTheme"> </br>   
			<div class="pull-left"> 
			<a href="/studentportal/home" ><u><i class="fa-solid fa-arrow-left " aria-hidden="true"></i> back</u></a> 
			</div> </br></br>
			<form:form modelAttribute="filesSet" enctype="multipart/form-data"  method="post"	action="uploadFacultyQpMapping">
				<div class="row"><legend>Upload File <font size="5px"></font></legend></div> 
				<%@ include file="../common/messages.jsp"%> 
				<div class="row">
					<div class="col-md-6 column">
						<div class="form-group">
						<label for="Program">Select file</label>
						<form:input path="fileData" type="file" />
						</div>
					</div>
					<div class="col-md-6  column">
						<div class="form-group">
							<b>Format of Upload: </b><br>
							Exam year	| Exam month | Creator Start Date | Creator Due Date | Consumer Type | Program Structure | Program | Subject | FacultyId | Reviewer | Reviewer Due Date<br>
							<a href="resources_2015/templates/Assignment_Qp_Mapping_Template.xlsx" target="_blank">Download a Sample Template</a>
						</div>
					</div> 
				</div>
				<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
					formaction="uploadFacultyQpMapping">Upload</button>
				</div>
			</form:form>	 
				  
			<form:form modelAttribute="filesSet" method="post"	action="saveAssignmentFacultyQpMapping">
				<div class="row"><legend>Select Year-Month</legend></div>  
				<div class="row">
					<div class="col-md-18 column">
						<div class="row">
							<div class="col-md-4 column"> 
								<div class="form-group">
									<label for="startDate">Exam Year</label>
									<form:select id="year" path="examYear" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${filesSet.year}">
										<form:option value="">Select Exam Year</form:option>
										<form:options items="${yearList}" />
									</form:select>
								</div>
							</div>
							<div class="col-md-4 column">
								<div class="form-group">
									<label for="startDate">Exam Month</label>
									<form:select id="month" path="examMonth" type="text" placeholder="Month" class="form-control" required="required" itemValue="${filesSet.month}">
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
									<label for="startDate">Creator Start Date</label>
									<form:input required="required" path="startDate" id="startDate" type="datetime-local" />
								</div>
							</div>
							
							<div class="col-md-4 column">
								<div class="form-group">
									<label for="endDate">Creator Due Date</label>
									<form:input required="required" path="endDate" id="endDate" type="datetime-local" />
								</div>
							</div> 
						
							<div class="col-md-4 column">
								<div class="form-group">
									<label for="consumerType">Consumer Type</label>
									<select required="required" data-id="consumerTypeId" id="consumerTypeId" name="consumerTypeId"  class="selectConsumerType" required="required">
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
									<select required="required" data-id="programStructureId" id="programStructureId" name="programStructureId" class="selectProgramStructure">
										<option disabled selected value="">Select Program Structure</option>
									</select>
								</div>
							</div> 
								
							<div class="col-md-4 column">
								<div class="form-group">
									<label for="Program">Program</label>
									<select required="required" data-id="programId" id="programId" name="programId" class="selectProgram">
										<option disabled selected value="">Select Program</option>
									</select>
								</div>
							</div>
							<div class="col-md-4 column">
								<div class="form-group">
									<div class="form-group">
										<label for="dueDate">Reviewer Due Date</label>
										<form:input required="required" path="dueDate" id="dueDate" type="datetime-local" />
									</div>
								</div>
							</div>  
						</div>  
						<br>  
						<div class="row"><legend>Assign Faculties</legend></div>  
						<c:forEach var="i" begin="0" end="9">           
						<div class="row">
						<div class="col-md-4 column"> 
							<div class="form-group">
								<label for="Program">Subject</label>
								<select  name="assignmentFilesSet[${i}].pss_id" class="selectSubject">
									<option disabled selected value="">Select Subject</option>
								</select>
							</div> 
						</div>
						<div class="col-md-4 column">
							<div class="form-group">
								<label for="dueDate">QP Creator</label>
								<select name="assignmentFilesSet[${i}].facultyId"  class="selectFaculty">
									<option disabled selected value="">Select Faculty</option>
									<c:forEach var="faculty" items="${facultyMap}">
									<option  value="${faculty.key}">${faculty.value}</option> 
									</c:forEach>
								</select> 
							</div>
						</div>
						<div class="col-md-4 column">
							<div class="form-group">  
								<label for="dueDate">QP Reviewer</label>
								<select name="assignmentFilesSet[${i}].reviewer" >
									<option disabled selected value="">Select Reviewer</option>
									<c:forEach var="faculty" items="${facultyMap}">
									<option  value="${faculty.key}">${faculty.value}</option> 
									</c:forEach>
								</select> 
							</div>
						</div>
						</div>
						</c:forEach>    
						<div class="form-group">  
							<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="saveAssignmentFacultyQpMapping">Save</button>
						</div>
					</div>
				</div> 
			</form:form>
		</div>
	</section>

	<jsp:include page="../footer.jsp" />

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
				
				//console.log("==========> options\n" + options);
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
				
				//console.log("==========> options\n" + options);
				$('#programStructureId').html(
						 "<option value='"+ allOption +"'>All</option>" + options
				);
				//End
				
				options = ""; 
				allOption = "";
				//Data Insert For Subjects List
				//Start
				for(let i=0;i < subjectsData.length;i++){
					console.log(subjectsData[i].name);
					options = options + "<option value='" + subjectsData[i].name.replace(/'/g, "&#39;") + "'> " + subjectsData[i].name + " </option>";
				}
				
				
				console.log("==========> options2\n" + options);
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
	//console.log(this.value)
	
	//console.log("===================> data id : " + $('#consumerTypeId').val());
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByProgramStructure",   
		data : JSON.stringify(data),
		success : function(data) {
			
			//console.log("SUCCESS: ", data.programData);
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
			
			//console.log("==========> options\n" + options);
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
	//console.log(this.value)
	var programStrAll = ($('#programStructureId').val().split(",").length>1)?true:false;
	var programAll = (this.value.split(",").length>1)?true:false;
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
			if(!programAll && !programStrAll){   
				for(let i=0;i < subjectsData.length;i++){
					
					options = options + "<option value='" + subjectsData[i].id + "'> " + subjectsData[i].name + " </option>";
					   
				} 
			}else{  
				for(let i=0;i < subjectsData.length;i++){
					
					options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
					 
				} 
			}
			   
			console.log("==========> options2\n" + options);
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
 