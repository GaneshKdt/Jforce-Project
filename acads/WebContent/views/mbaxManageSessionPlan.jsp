
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Manage Session Plan" name="title" />
</jsp:include>


<!-- Include Editor style. -->
<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_editor.pkgd.min.css" rel="stylesheet" type="text/css" />
<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_style.min.css" rel="stylesheet" type="text/css" />
<link data-require="sweet-alert@*" data-semver="0.4.2" rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/1.1.3/sweetalert.min.css" />
 
 <!-- for medium editor start -->
 <script src="//cdn.jsdelivr.net/npm/medium-editor@latest/dist/js/medium-editor.min.js"></script>
<link rel="stylesheet" href="//cdn.jsdelivr.net/npm/medium-editor@latest/dist/css/medium-editor.min.css" type="text/css" media="screen" charset="utf-8">
 
 <!-- for medium editor end -->
   	
<!-- css for accordian start -->
<style>


.panel-title > a:before {
    float: right !important;
    font-family: FontAwesome;
    content:"\f068";
    padding-right: 5px;
} 
.panel-title > a.collapsed:before {
    float: right !important;
    content:"\f067";
}
.panel-title > a:hover, 
.panel-title > a:active, 
.panel-title > a:focus  {
    text-decoration:none;
}
 
 
 .editable{
 	border : 1px solid grey;
 }

</style>
<!-- css for accordian end  -->

<body class="inside">

	<%@ include file="header.jsp"%>
	
	<!-- code for accordian start -->
	<div class="well well-lg">
	
	<ul class="breadcrumb">
	<li><a href="/">Home</a></li>
	<li class="active">Manage Session Plans</li>
	</ul>
	
	<%@ include file="messages.jsp"%>
		
	<div class="panel-group">
  <div class="panel panel-default">
    <div class="panel-heading">
      <h4 class="panel-title">
        <a class="btn btn-info btn-block collapsed" data-toggle="collapse" href="#collapse1">Add A New Session Plan</a>
      </h4>
    </div>
    <div id="collapse1" class="panel-collapse collapse ">
      <div class="panel-body">
      	
      	<!-- Code for Form start -->
	<div class="well well-lg">				
				<form:form modelAttribute="formBean" method="post">
					<div class="row">
					<div class="container-fluid">
					
					<div class="row" style="margin-top: 15px;">
					
					<!-- ///////////////////////////////////////////////////////////////// -->
					
						
						<!--  Commenting subject to add batch logic-->
					<!-- <div class="col-md-4">
							<div class="form-group">
							<label >Subject</label>
							<select  data-id="subjectId" name="subject" class="selectSubject">
							<option disabled selected value="">Select Subject</option>
							
							</select>
							</div>
					</div> -->
						
					</div>
					<!-- ///////////////////////////////////////////////////////////////// -->
					
					<div class="row">
						<div class="col-md-5">
						<div class="form-group">
							<label >Acads Year</label>
							<form:select id="year" path="year" type="text"	placeholder="Acads Year" 
							class="form-control resetApplicableType" required="required"  itemValue="${formBean.year}">
								<form:option value="">Select Acads Year</form:option>
								
				                 <form:options items="${acadsYearList}"/>
				                
				            
								
							</form:select>
						</div>
						</div>
						
						<div class="col-md-5">
						<div class="form-group">
							<label >Acads Month</label>
							<form:select id="month" path="month" type="text" placeholder="Acads Month" 
							class="form-control resetApplicableType" required="required" itemValue="${formBean.month}">
								<form:option value="">Select Acads Month</form:option>
								 <form:options items="${acadsMonthList}"/>
							</form:select>
						</div>
						</div>

					</div>
					<div class="row">
						<div class="col-md-5">
							<div class="form-group">
								<label for="consumerType">Consumer Type</label>
								<select data-id="consumerTypeDataId" id="consumerTypeId"
									name="consumerTypeId"   class="selectConsumerType"
									required="required">
									<option disabled selected value="">Select Consumer Type</option>
								<c:forEach var="consumerType" items="${consumerType}">
					                <option value="<c:out value="${consumerType.id}"/>">
					                  <c:out value="${consumerType.name}"/>
					                </option>
					            </c:forEach>
								</select>
							</div>
						</div>
						
						<div class="col-md-5">
							<div class="form-group">
							<label for="programStructure">Program Structure</label>
							<select  data-id="programStructureDataId" id="programStructureId" name="programStructureId" class="selectProgramStructure" required="required">
								<option disabled selected value="">Select Program Structure</option>

							</select>
							</div>
						</div> 
							
						<div class="col-md-5">
							<div class="form-group">
							<label for="programId">Program</label>
							<select path="programDataId" data-id="programDataId" id="programId" name="programId" class="selectProgram" required="required">
								<option disabled selected value="">Select Program</option>

							</select> 
							
						
							</div>
						</div>
						
<!-- 						added batch--> 
						
						<%-- <div class="col-md-5">
							<div class="form-group" style="display:none">
								<label >Applicable Type</label>
								<form:select id="applicableType" path="applicableType" type="text" placeholder=">Applicable Type" 
								class="form-control" required="required" itemValue="${formBean.applicableType}">
									<form:option value="">Select Applicable Type</form:option>
									<form:option selected="true" value="batch">Batch</form:option>
								</form:select>
							</div>
						</div> --%>
						
					
					</div>
					<div class="row">
						<div class="col-md-5">
							<div class="form-group">	
								<div id="referenceIdDiv" style="display:none">
									
								<form:label path="referenceId" for="referenceId">Select Batch</form:label>
								<form:select id="referenceId" path="referenceId" type="text"	
									placeholder="referenceId" 
									class="form-control" required="required" 
										 itemValue="${formBean.referenceId}"  > 
										<form:option  value="${formBean.referenceId}">${formBean.referenceBatchOrModuleName}</form:option>
								</form:select>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div  class="col-md-5">
							<div class="form-group">	
								<div id="subjectSelectContainerDiv" class="form-group" style="display:none">	
									
									<form:label path="subject" for="subject">Select Subject</form:label>
									<form:select id="subject" path="subject" type="text"	
										placeholder="subject" 
										class="form-control" required="required" 
											 itemValue="${formBean.subject}"  > 
											<form:option  value="${formBean.subject}">${formBean.subject}</form:option>
									</form:select>
									
								</div>
							</div>
						</div>
					</div>
					
					<%-- 
					<div class="row">
					<div class="">
						<div class="col-md-6">
							<div class="form-group">
								<label >Session Plan Title</label>
								<form:input path="title" value="${formBean.title}" required="required"  placeholder="Session Plan Title"
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
					</div>
					</div> --%>
					
					<div class="row">
					<div class="well">
						<h6 align="left">Teaching Scheme</h6>
						
						<div class="col-md-4">
							<div class="form-group">
								<label >No Of Sessions </label>
								<form:input type="number" path="noOfClassroomSessions" value="${formBean.noOfClassroomSessions}"
									required="required"  placeholder="0" 
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
						<div class="col-md-4">
							<div class="form-group">
								<label >No Of Group work (if Any) </label>
								<form:input type="number" path="noOf_Practical_Group_Work" value="${formBean.noOf_Practical_Group_Work}"
									required="required"  placeholder="0" 
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
						<div class="col-md-4">
							<div class="form-group">
								<label >No Of Assessments </label>
								<form:input type="number" path="noOfAssessments" value="${formBean.noOfAssessments}"
									required="required"  placeholder="0" 
									style="color:black; padding:6px 12px; width:100%;"/>
							</div>
						</div>
						
					</div>
					</div>
					
					<br>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Course Rationale : </label>
								  <form:textarea class="editable"  path="courseRationale" value="${formBean.courseRationale}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Course Objectives : </label>
								  <form:textarea class="editable"   path="objectives" value="${formBean.objectives}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Learning Outcomes : </label>
								  <form:textarea class="editable"   path="learningOutcomes" value="${formBean.learningOutcomes}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Pre-requisites : </label>
								  <form:textarea class="editable"   path="prerequisites" value="${formBean.prerequisites}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Pedagogy : </label>
								  <form:textarea class="editable"   path="pedagogy" value="${formBean.pedagogy}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >TextBook : </label>
								  <form:textarea class="editable"   path="textbook" value="${formBean.textbook}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Journals for session plan module : </label>
								  <form:textarea class="editable"   path="journals" value="${formBean.journals}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					<div class="row">
					<div class="">
						<div class="col-md-12">
							<div class="form-group">
								<label >Links To Websites : </label>
								  <form:textarea class="editable"   path="links" value="${formBean.links}"  rows="5" cols="150" />
							</div>
						</div>
						
					</div>
					</div>
					
					
					<br>
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="saveSessionPlan">Save Plan</button>
					</div>
					</div>

			</div>

			</form:form>
	</div>		
	<!-- Code for Form end -->
      	
      
      </div><!-- panelbody end -->
      <div class="panel-footer"></div>
    </div>
  </div>
</div>
	
	</div>	
	<!-- code for accordian end -->
	
	
		
	<!-- Code for table start -->
			<div class="clearfix"></div>
				<div class="well well-lg" style="">
					<div class="table-responsive">
						<table class="table table-striped table-hover tables"
							style="font-size: 12px">
							<thead>
								<tr>
									<th>Sr. No.</th>
									<th>Year</th>
									<th>Month</th>
									<th>Batch</th>
									<th>Subject</th>
									<th>Title</th>
									<th></th>
									<th></th>
									<th>Edit</th>
									<th>Delete</th>
					
								</tr>
							</thead>
							<tbody>
								 <c:forEach var="sessionPlan" items="${sessionPlanList}"   varStatus="status">
					        		 <tr>
							            <td ><c:out value="${status.count}" /></td>
							            <td ><c:out value="${sessionPlan.year}" /></td>
							            <td ><c:out value="${sessionPlan.month}" /></td>
							            <td ><c:out value="${sessionPlan.batchName}" /></td>
							            <td ><c:out value="${sessionPlan.subject}" /></td>
							            <td ><c:out value="${sessionPlan.title}" /></td>
							            <td ><c:out value="Common For ${sessionPlan.countOfProgramsApplicableTo} programs" /></td>
							            <td >
											<!-- <a href="" class="btn btn-success btn-block">Manage Modules</a> -->
										</td>
										<td>
											<a href="/acads/mbax/sp/a/admin/editSessionPlan?id=${sessionPlan.id }" title="Edit SessionPlan" class="">
												<b>
													<i style="font-size:20px; padding-left:5px" class="fa-solid fa-pen-to-square" aria-hidden="true"></i>
												</b>
											</a>
										</td>
										<td>
											<a href="/acads/mbax/sp/a/admin/deleteSessionPlan?id=${sessionPlan.id }" title="Delete sessionPlan" class="">
												<b>
													<i style="font-size:20px; padding-left:5px" class="fa-regular fa-trash-can" aria-hidden="true"></i>
												</b>
											</a>
										</td>
							          </tr>
							      </c:forEach>
					    			
							</tbody>
						</table>
					</div>
						
				</div> 
	<!-- Code for table end -->
	
	<jsp:include page="footer.jsp" />
	
        <!-- jQuery (necessary for Bootstrap's JavaScript plugins) --> 
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script> 


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>  
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js" ></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js" ></script>

<script>var editor = new MediumEditor('.editable');</script>


	<script>
		
	$('.tables').DataTable( {
        initComplete: function () {
            this.api().columns().every( function () {
                var column = this;
                var headerText = $(column.header()).text();
                console.log("header :"+headerText);
                if(headerText == "Subject")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                if(headerText == "Year")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }

                if(headerText == "Month")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
                if(headerText == "Batch")
                {
                   var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
                    .appendTo( $(column.header()) )
                    .on( 'change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );
 
                        column
                            .search( val ? '^'+val+'$' : '', true, false )
                            .draw();
                    } );
 
                column.data().unique().sort().each( function ( d, j ) {
                    select.append( '<option value="'+d+'">'+d+'</option>' )
                } );
              }
                
            } );
        }
    } );
	
	</script>

<script type="text/javascript">

$(document).ready (function(){
	
	
///////////////////////////////////////////////////////////////////
	
	
	$('.selectConsumerType').on('change', function(){
	
	resetApplicableTypeAndSubject();
		
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programStructureId').html(options);
	$('#programId').html(options);
	
	
	 
	var data = {
			id:this.value
	}
console.log(this.value)
	
	console.log("===================> data id : " + id);
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam/admin/getDataByConsumerType",   
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
			
			//console.log("==========> defaultOption\n" + defaultOption);
			$('#programId').html(
					 "<option value='' selected>Select Program</option>" +
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
					"<option value='' disabled selected>Select Program Structure</option>" +
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
	
	resetApplicableTypeAndSubject();
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
		url : "/exam/admin/getDataByProgramStructure",   
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
					 "<option value='' selected>Select Program</option>" +
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
	
	resetApplicableTypeAndSubject();
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('.selectSubject').html(options);
	var programId = $('.selectProgram').val();
	var acadYear = $('#year').val();
	var acadMonth = $('#month').val();
	 
	var data = {
			programId:this.value,
			consumerTypeId:$('#consumerTypeId').val(),
			programStructureId:$('#programStructureId').val()
	}
	//console.log(this.value)
	if( programId === '' || data.consumerTypeId ==='' || data.programStructureId === ''
		|| acadYear === ''  || acadMonth === ''){
		alert('Please Select All Consumer, Program, ProgramStructureId, subject, acadMonth and acadYear');
		return;
	}
	/* original code */
	/* $.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam/getDataByProgram",   
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
	}); */
	
	console.log(" inside change-------------------1----------");

	$('#subjectSelectContainerDiv').html(' <span></span> ');
	$('#referenceIdDiv').html(' <span></span> ');
	$('#subjectSelectContainerDivForOldConfig').html( ' <span></span> ' );
	
	var data = {
	programId:$('#programId').val(),
	consumerTypeId:$('#consumerTypeId').val(),
	programStructureId:$('#programStructureId').val(),
	subject:"All",
	acadYear:$('#year').val(),
	acadMonth:$('#month').val()
	};
	console.log("data : ");
	console.log(data);
	
	if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === ''
	|| data.acadYear === ''  || data.acadMonth === ''){
	alert('Please Select All Consumer, Program, ProgramStructureId, subject, acadMonth and acadYear');
	return;
	}
	
	/* if( data.programId.split(',').length > 1 ||
	data.consumerTypeId.split(',').length > 1 || 
	data.programStructureId.split(',').length > 1 ){
	
		document.getElementById("applicableType").options[3].selected = 'selected';
	
	} */
	/* let applicableType = $('#applicableType	').val(); */
	let applicableType = 'batch';
	console.log('IN program on change event : applicableType = ');
	console.log(applicableType);
	
	console.log(" inside change-------------------2----------"+applicableType);
	
	if(applicableType === 'batch'){
	//make api call to get batch details 
	console.log(" inside if-------------------1----------"+applicableType);
	$.ajax({
	type : "POST",
	contentType : "application/json",
	url : "/exam/api/getBatchDataByMasterKeyConfig",   
	data : JSON.stringify(data),
	success : function(data) {
	console.log("SUCCESS: dataForReferenceId :");
	console.log(data.dataForReferenceId);
	
	
	var dataForReferenceId = data.dataForReferenceId;
	
	if( !dataForReferenceId){
		alert("Unable to get batches for selected program config, Kindly update selections and try again.");
	
		document.getElementById("programId").options[0].selected = 'selected';
		/* $('#referenceIdDiv').hide(); */
		return;
	}
	if(  dataForReferenceId.length === 0){
		alert("Unable to get batches for selected program config, Kindly update selections and try again.");
	
		document.getElementById("programId").options[0].selected = 'selected';
		return;
	}
	
	var labelForReferenceId = "<label for='refrenceId'>Applicable Batch</label>";
	var selectElementTopPart = "<select  id='referenceId'  name='referenceId'    placeholder='referenceId' type='text' class='form-control' required>" 
	var selectElementBottomPart = " </select>";
	
	options = ""; 
	//Start
	for(let i=0;i < dataForReferenceId.length;i++){
	
	options = options + "<option value='" + dataForReferenceId[i].id + "'> "
	+ dataForReferenceId[i].name + " </option>";
	}
	
	
	console.log("==========> options\n" + options);
	
	var htmlToAppend = labelForReferenceId 
	+ selectElementTopPart 
	+ " <option selected value=''> Select Batch </option> " + options 
	+ selectElementBottomPart
	
	console.log("htmlToAppend");
	console.log(htmlToAppend);
	$('#referenceIdDiv').html( htmlToAppend );
	$('#referenceIdDiv').show();
	//End
	
	
	
	},
	error : function(e) {
	
	alert("Please Refresh The Page.")
	
	console.log("ERROR: ", e);
	console.log(e);
	}
	

	});
}
	
	
});

//////////////////////////////////////////////
$('#applicableType').on('change', function(){

	console.log(" inside change-------------------1----------");

	$('#subjectSelectContainerDiv').html(' <span></span> ');
	$('#referenceIdDiv').html(' <span></span> ');
	$('#subjectSelectContainerDivForOldConfig').html( ' <span></span> ' );
	
	var data = {
	programId:$('#programId').val(),
	consumerTypeId:$('#consumerTypeId').val(),
	programStructureId:$('#programStructureId').val(),
	subject:"All",
	acadYear:$('#year').val(),
	acadMonth:$('#month').val()
	};
	console.log("data : ");
	console.log(data);
	
	if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === ''
	|| data.acadYear === ''  || data.acadMonth === ''){
	alert('Please Select All Consumer, Program, ProgramStructureId, subject, acadMonth and acadYear');
	return;
	}
	
	/* if( data.programId.split(',').length > 1 ||
	data.consumerTypeId.split(',').length > 1 || 
	data.programStructureId.split(',').length > 1 ){
	
		document.getElementById("applicableType").options[3].selected = 'selected';
	
	} */
	
	let applicableType = this.value;
	console.log('IN applicableType on change event : applicableType = ');
	console.log(applicableType);
	
	console.log(" inside change-------------------2----------"+applicableType);
	
	if(applicableType === 'batch'){
	//make api call to get batch details 
	console.log(" inside if-------------------1----------"+applicableType);
	$.ajax({
	type : "POST",
	contentType : "application/json",
	url : "/exam/api/getBatchDataByMasterKeyConfig",   
	data : JSON.stringify(data),
	success : function(data) {
	console.log("SUCCESS: dataForReferenceId :");
	console.log(data.dataForReferenceId);
	
	
	var dataForReferenceId = data.dataForReferenceId;
	
	if( !dataForReferenceId){
		alert("Unable to get batches for selected program config, Kindly update selections and try again.");
	
		document.getElementById("applicableType").options[0].selected = 'selected';
		$('#referenceIdDiv').hide();
		return;
	}
	if(  dataForReferenceId.length === 0){
		alert("Unable to get batches for selected program config, Kindly update selections and try again.");
	
		document.getElementById("applicableType").options[0].selected = 'selected';
		return;
	}
	
	var labelForReferenceId = "<label for='refrenceId'>Applicable Batch</label>";
	var selectElementTopPart = "<select  id='referenceId'  name='referenceId'    placeholder='referenceId' type='text' class='form-control' required>" 
	var selectElementBottomPart = " </select>";
	
	options = ""; 
	//Start
	for(let i=0;i < dataForReferenceId.length;i++){
	
	options = options + "<option value='" + dataForReferenceId[i].id + "'> "
	+ dataForReferenceId[i].name + " </option>";
	}
	
	
	console.log("==========> options\n" + options);
	
	var htmlToAppend = labelForReferenceId 
	+ selectElementTopPart 
	+ " <option disabled selected value=''> Select Batch </option> " + options 
	+ selectElementBottomPart
	
	console.log("htmlToAppend");
	console.log(htmlToAppend);
	$('#referenceIdDiv').html( htmlToAppend );
	$('#referenceIdDiv').show();
	//End
	
	
	
	},
	error : function(e) {
	
	alert("Please Refresh The Page.")
	
	console.log("ERROR: ", e);
	console.log(e);
	}
	

	});
}
})
});

$(document).on('change', '#referenceIdDiv select', function(){

	$('#subjectSelectContainerDiv').html(' <span></span> ');

	/* let applicableType = $('#applicableType	').val(); */
	let applicableType = 'batch'
	console.log('IN referenceIdDiv on change event : applicableType = ');
	console.log(applicableType);
	console.log("referenceId---"+$('#referenceId').val());
	
	var data = {
		programId:$('#programId').val(),
		consumerTypeId:$('#consumerTypeId').val(),
		programStructureId:$('#programStructureId').val(),
		subject:"All",
		acadYear:$('#year').val(),
		acadMonth:$('#month').val(),
		referenceId:$('#referenceId').val()
	};
	console.log("data : ");
	console.log(data);

	if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === '' || data.referenceId === ''
	|| data.acadYear === ''  || data.acadMonth === ''){
		alert('Please Select All Consumer, Program, ProgramStructureId, batch/Module, acadMonth and acadYear');
		return;
	}

	/* if( data.programId.split(',').length > 1 ||
	data.consumerTypeId.split(',').length > 1 || 
	data.programStructureId.split(',').length > 1 ){

		document.getElementById("applicableType").options[0].selected = 'selected';
		return;
	}
 */
	if(applicableType === 'batch'){
		//make api call to get batch details 

		$.ajax({
			type : "POST",
			contentType : "application/json",
			url : "/exam/api/getSubjectsByMastKeyAndBatch",   
			data : JSON.stringify(data),
			success : function(data) {
			console.log(" inside ---------"+JSON.stringify(data));
			console.log("SUCCESS: dataForReferenceId :");
			console.log(data.listOfStringData);
	
	
			var dataForSubject = data.listOfStringData;
	
			if( !dataForSubject ){
				alert("Unable to get batches for selected program config, Kindly update selections and try again.");
				/* $('#subjectSelectContainerDiv').hide(); */
				document.getElementById("referenceId").options[0].selected = 'selected';
				return;
			}
	
			if( dataForSubject.length === 0){
				alert("Unable to get batches for selected program config, Kindly update selections and try again.");
				/* $('#subjectSelectContainerDiv').hide(); */
				document.getElementById("referenceId").options[0].selected = 'selected';
				return;
			}
	
			var labelForSubject = "<label for='subject'>Subject</label>";
			var selectElementTopPart = "<select  id='subject'  name='subject'    placeholder='subject' type='text' class='form-control' required>" 
			var selectElementBottomPart = " </select>";
	
			options = ""; 
			//Start
			for(let i=0;i < dataForSubject.length;i++){
		
				options = options + "<option value='" + dataForSubject[i] + "'> "
				+ dataForSubject[i] + " </option>";
			}
	
	
			console.log("==========> options\n" + options);
		
			var htmlToAppend = labelForSubject 
			+ selectElementTopPart 
			+ " <option selected value=''> Select Subject </option> " + options 
			+ selectElementBottomPart
		
			console.log("htmlToAppend");
			console.log(htmlToAppend);
			$('#subjectSelectContainerDiv').html( htmlToAppend );
			$('#subjectSelectContainerDiv').show();
			//End
	
	
	
	
			},
			error : function(e) {
	
				alert("Please Refresh The Page.")
			
				console.log("ERROR: ", e);
				console.log(e);
			}
		});
	}
})


$(document).on('change', '.resetApplicableType', function(){
	resetApplicableTypeAndSubject();
});

function resetApplicableTypeAndSubject(){
	/* if($('#applicableType').val() != ""){
		document.getElementById("applicableType").options[0].selected = 'selected';
	}  */
	if($('#referenceId').val() != "" && document.getElementById("referenceId") != null){
		document.getElementById("referenceId").options[0].disabled = false;
		document.getElementById("referenceId").options[0].selected = 'selected';
	}
	if($('#subject').val() != ""  && document.getElementById('subject') != null){
		document.getElementById("subject").options[0].selected = 'selected';
	}
}
</script>

</body>
</html>
