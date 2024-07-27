
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
	color:white!important;} 
</style>
<body class="inside">

	<%-- <%@ include file="header.jsp"%> --%>

	<section class="content-container login">
		<div class="container-fluid customTheme" >
			<div class="row"><legend>Search QP </legend></div>
			<div  style="margin-left:10%; min-height:50rem;">      
			  <%@ include file="../common/messages.jsp"%>  
			   
				<form:form modelAttribute="filesSet" method="post"	action="searchQp"> 
							  			
					<div class="col-md-18 column"> 
					<div class="row">
						<div class="col-md-4 column"> 
						<div class="form-group">
							<label for="startDate">Exam Year</label>
							<form:select id="year" path="examYear" type="text"	placeholder="Year" class="form-control"  itemValue="${filesSet.year}">
								<form:option value="">Select Exam Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>
						</div>
						<div class="col-md-4 column">
						<div class="form-group">
							<label for="startDate">Exam Month</label>
							<form:select id="month" path="examMonth" type="text" placeholder="Month" class="form-control" itemValue="${filesSet.month}">
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
							<label for="consumerType">Consumer Type</label>
							<select   data-id="consumerTypeId" id="consumerTypeId" name="consumerTypeId"  class="selectConsumerType" >
								<option disabled selected value="">Select Consumer Type</option>
							<c:forEach var="consumerType" items="${consumerType}">
				                <option value="<c:out value="${consumerType.id}"/>">
				                  <c:out value="${consumerType.name}"/>
				                </option>
				            </c:forEach>
							</select>
							</div>
						</div>
				    </div>	
				     
					<div class="row">
					<div class="col-md-18 column">
					
					<div class="row"> 
						<div class="col-md-4 column">
							<div class="form-group">
							<label for="programStructure">Program Structure</label>
							<select data-id="programStructureId" id="programStructureId" name="programStructureId" class="selectProgramStructure">
								<option disabled selected value="">Select Program Structure</option>

							</select>
							</div>
						</div> 
							
						<div class="col-md-4 column">
							<div class="form-group">
							<label for="Program">Program</label>
							<select data-id="programId" id="programId" name="programId" class="selectProgram">
								<option disabled selected value="">Select Program</option>

							</select>
							</div>
						</div>
						<div class="col-md-4 column"> 
							<div class="form-group">
							<label for="Program">Subject</label>
							<select data-id="pss_id" name="pss_id" class="selectSubject">
							<option disabled selected value="">Select Subject</option>
							
							</select>
							</div>
						</div>
					</div>
					<div class="row">
					<div class="col-md-9 column">   
					</div> 
					<div class="col-md-4 column">     
					<div class="form-group">         
						<button id="submit" name="submit" class="btn btn-primary pull-right"
						>Search</button>
					</div>
					</div>
					</div>  
					 
					</div> 
					</div>

			</div> 
			</form:form>  
			<c:if test="${ filesSetList.size() gt 0}">  
				<div  class="col-md-12 column alert alert-success " >  
				<span style="color:#5cb85c">${filesSetList.size()} results found.</span>   
				</div>    
			</c:if> 
			<div class="col-md-18 column" style="margin-top:3rem; ">     
				<c:forEach var="filesetList" items="${filesSetList}"> 
				<form:form modelAttribute="filesSet" method="post"	> 
				   <div class="row">  
						<div class="col-md-4 column" >   
							<div class="form-group" style="padding-top:8px;">  
								<label for="download">Download File</label>  
								<a id="download" name="submit" class="btn btn-large btn-success btn-sm"
							    href="downloadAssignmentFile?filePath=${filesetList.filePath}">Download</a>  
							</div>        
						</div>  
						<form:hidden  path="examYear" value="${filesetList.year}" />	
						<form:hidden  path="examMonth" value="${filesetList.month}" />
						<form:hidden  path="subject" value="${filesetList.subject}" />
						<form:hidden  path="consumerProgramStructureId" value="${filesetList.consumerProgramStructureId}" />
						<input type="hidden" class="startDate" value="${filesetList.startDate}" />
						<input type="hidden" class="endDate" value="${filesetList.endDate}" /> 
						<div class="col-md-4 column">
							<div class="form-group">  
								<label for="startDate">Start Date</label>      
								<input type="hidden" class="startDate" value="${filesetList.startDate}"/>  
								<input type="hidden" class="endDate" value="${filesetList.endDate}"/>  
								<form:input class="startDateFormValue" value="${filesetList.startDate}" path="startDate" id="startDate" type="datetime-local" />
							</div>
						</div> 
						
						<div class="col-md-4 column">
							<div class="form-group">
								<label for="endDate">End Date</label>
								<form:input class="endDateFormValue" path="endDate" id="endDate" type="datetime-local" />
							</div>
						</div> 
						<div class="col-md-4 column"> 
							<div class="form-group">                        
								<button id="submit" style="margin-top:37px;" name="submit" class="btn btn-large btn-sm btn-success"
								formaction="updateAssignmentStartDateEndDate">Save</button>   
							</div>   
						</div> 
					</div>
					</form:form>
				</c:forEach>
				</div>  
			  	</div>  
		</div>
	</section>
    <div style="margin-bottom:5rem;"></div> 
	<jsp:include page="../footer.jsp" />  
<script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/4.17.37/js/bootstrap-datetimepicker.min.js"></script>
        	  
<script type="text/javascript">  
$( ".startDate" ).each(function() {
	var sDate=moment($(this).val(),"YYYY-MM-DD HH:mm:ss").format("YYYY-MM-DDTHH:mm:ss"); 
	$(this).closest(".row").find(".startDateFormValue").val(sDate);  
});
$( ".endDate" ).each(function() { 
	var sDate=moment($(this).val(),"YYYY-MM-DD HH:mm:ss").format("YYYY-MM-DDTHH:mm:ss"); 
	$(this).closest(".row").find(".endDateFormValue").val(sDate);  
});

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
				
				options = options + "<option value='" + subjectsData[i].id + "'> " + subjectsData[i].name + " </option>";
				 
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
 