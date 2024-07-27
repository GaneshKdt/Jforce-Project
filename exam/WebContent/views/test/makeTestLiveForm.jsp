<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Make Test Live" name="title" />
</jsp:include>




<body class="inside">

	<%@ include file="../header.jsp"%> 

	<section class="content-container login">
		<div class="container-fluid customTheme">
			
			<div class="row"><legend>Make Test Live</legend></div>
			<%@ include file="../messages.jsp"%>
				
				<form:form modelAttribute="searchBean" method="post" action="saveTestLiveConfig">
					<div class="row">
					<div class="col-md-16 column">
					
						<div class="row">
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Exam Year</label>
							<form:select id="examYear" path="examYear" type="text"	placeholder="Exam Year" class="form-control" required="required"  itemValue="${filesSet.examYear}">
								<form:option value="">Select Exam Year</form:option>
								<form:option value="2019">2019</form:option>
								<form:option value="2020">2020</form:option>

								


							</form:select>
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Exam Month</label>
							<form:select id="examMonth" path="examMonth" type="text" placeholder="Exam Month" class="form-control" required="required" itemValue="${filesSet.examMonth}">
								<form:option value="">Select Month</form:option>
																<form:option value="Jan">Jan</form:option>
																<form:option value="Feb">Feb</form:option>
																<form:option value="Mar">Mar</form:option>
																<form:option value="Apr">Apr</form:option>
																<form:option value="May">May</form:option>
																<form:option value="Jun">Jun</form:option>
																<form:option value="Jul">Jul</form:option>
																<form:option value="Aug">Aug</form:option>
																<form:option value="Sep">Sep</form:option>
																<form:option value="Oct">Oct</form:option>
																<form:option value="Nov">Nov</form:option>
																<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Acads Year</label>
							<form:select id="acadYear" path="acadYear" type="text"	placeholder="Acads Year" class="form-control" required="required"  itemValue="${filesSet.acadYear}">
								<form:option value="">Select Acads Year</form:option>
								
				                 <form:options items="${acadsYearList}"/>
				                
				            
								
							</form:select>
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Acads Month</label>
							<form:select id="acadMonth" path="acadMonth" type="text" placeholder="Acads Month" class="form-control" required="required" itemValue="${filesSet.acadMonth}">
								<form:option value="">Select Acads Month</form:option>
																<form:option value="Jan">Jan</form:option>
																<form:option value="Feb">Feb</form:option>
																<form:option value="Mar">Mar</form:option>
																<form:option value="Apr">Apr</form:option>
																<form:option value="May">May</form:option>
																<form:option value="Jun">Jun</form:option>
																<form:option value="Jul">Jul</form:option>
																<form:option value="Aug">Aug</form:option>
																<form:option value="Sep">Sep</form:option>
																<form:option value="Oct">Oct</form:option>
																<form:option value="Nov">Nov</form:option>
																<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>
						</div>
						
						
					
				
					
					</div>
					
					<div class="row">
					
					<!-- ///////////////////////////////////////////////////////////////// -->
					
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
							<select data-id="programStructureDataId" id="programStructureId" name="programStructureId" class="selectProgramStructure" required="required">
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
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label for="liveType">Test Type</label>
							<form:select id="liveType" path="liveType" type="text" class="form-control" required="required" itemValue="${filesSet.liveType}">
								<form:option value="">Select Test Type</form:option>
								<form:option value="Regular">Regular</form:option>
<%-- 								<form:option value="Resit">Resit</form:option> --%>
							
							</form:select>
						</div>
						</div>
						
						
						
						
						
					</div>
					<!-- ///////////////////////////////////////////////////////////////// -->
					
							
							<div class="panel-content-wrapper">
								<div class="row">
									
									<div class="col-md-4 col-sm-6 col-xs-12 column">
										<div class="form-group">	
											<form:label path="applicableType" for="applicableType">Test Applicable Type</form:label>
											<form:select id="applicableType" path="applicableType" type="text"	required="required" 
												placeholder="applicableType" 
												class="form-control" > 
													<form:option value="">Select Test Applicable Type</form:option>
<%-- 													<form:option value="batch">Batch</form:option> --%>
													<form:option value="module">Session Plan Module</form:option>
<%-- 													<form:option value="old">Old Process</form:option> --%>
											</form:select>
										</div>
									</div>
									
									
								</div>
								
								
								
									<div class="row">
									<div class="col-md-4 col-sm-6 col-xs-12 column">
										<div class="form-group">	
											<div id="moduleBatchIdDiv">
												
											</div>
										</div>
									</div>
									</div>
									
									
									<div class="row">
									<div class="col-md-4 col-sm-6 col-xs-12 column">
										<div class="form-group">	
											<div id="referenceIdDiv">
												
											</div>
										</div>
									</div>
									</div>
									
									<div class="row">
									<div  class="col-md-4 col-sm-6 col-xs-12 column">
										<div id="subjectSelectContainerDiv" class="form-group">	
											
										</div>
									</div>
									</div>
									
									<div class="row">
									<div  class="col-md-4 col-sm-6 col-xs-12 column">
										<div id="subjectSelectContainerDivForOldConfig" class="form-group">	
											
										</div>
									</div>
									</div>
									<div class="row">
									<div  class="col-md-4 col-sm-6 col-xs-12 column">
										<div id="moduleReferenceIdDiv" class="form-group">	
											
										</div>
									</div>
									</div>
								
							</div>
				
					
					
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="saveTestLiveConfig">Make Tests Live</button>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<button id="deleteTestLiveConfig" name="deleteTestLiveConfig" class="btn btn-large btn-danger"
						formaction="deleteTestLiveConfig"> Disable Tests Live Config </button>
						<button onclick="window.location.href = 'testNotLive';" class="btn btn-large btn-primary" >view tests to make live</button></a> 
						
					</div>
					</div>

			</div>

			
			<br></br>
			<div class="clearfix"></div>
				<div class="column">
				<legend>&nbsp;Live Test Configurations </legend>
				<div class="table-responsive">
				<table class="table table-striped table-hover tables" style="font-size: 12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Acads Year</th>
							<th>Acads Month</th>
							<th>Exam Year</th>
							<th>Exam Month</th>
							<th>Test Type</th>
							<th>Applicable Type</th>
							<th>Consumer Type</th>
							<th>Program Structure</th>
							<th>Program</th>
							<th>Batch</th>
							<th>Subject</th>
							<th>Module</th>
							
					
							
						</tr>
						</thead>
						<tbody>
						
						 <c:forEach var="config" items="${testsLiveConfigList}"   varStatus="status">
					         <tr>
					            <td ><c:out value="${status.count}" /></td>
					            <td ><c:out value="${config.acadYear}" /></td>
					            <td ><c:out value="${config.acadMonth}" /></td>
					            <td ><c:out value="${config.examYear}" /></td>
					            <td ><c:out value="${config.examMonth}" /></td>
					            <td ><c:out value="${config.liveType}" /></td>
					            <td ><c:out value="${config.applicableType}" /></td>
					            <td ><c:out value="${config.consumerType}" /></td>
					            <td ><c:out value="${config.programStructure}" /></td>
					            <td ><c:out value="${config.program}"/></td>
	            				
								 <c:choose>
					               <c:when test="${config.applicableType eq 'old'}">
									<td >NA</td>
	            				   </c:when>
					               <c:otherwise>
									 <td ><c:out value="${config.name}"/></td>
					               </c:otherwise>
					              </c:choose>
					            
								 <c:choose>
					               <c:when test="${config.applicableType eq 'old'}">
									<td ><c:out value="${config.subject}"/></td>
	            				   </c:when>
					               <c:otherwise>
									<td ><c:out value="${config.subject}"/></td>
					            	</c:otherwise>
					              </c:choose>
					            
								 <c:choose>
					               <c:when test="${config.applicableType eq 'module'}">
									<td ><c:out value="${config.topic}"/></td>
									</c:when>
					               <c:otherwise>
									<td > NA</td>
	            				   </c:otherwise>
					              </c:choose>
					            
								
									
																 
					        </tr>   
					    </c:forEach>
							
						</tbody>
					</table>
				</div>
				
				
				</div> 
			</form:form>
			
		</div>
	</section>

	<jsp:include page="../footer.jsp" />
	
	
	

<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

<script type="text/javascript">


$(document).ready (function(){
//////////////////////////////////////////////////////////////////

		$('.tables').DataTable( {
	        initComplete: function () {
	            this.api().columns().every( function () {
	                var column = this;
	                var headerText = $(column.header()).text();
	                console.log("header :"+headerText);
	                if(headerText == "Acads Year")
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
	              
	                if(headerText == "Acads Month")
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
	                
	                if(headerText == "Exam Year")
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
	                if(headerText == "Exam Month")
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
	                if(headerText == "Consumer Type")
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
	                if(headerText == "Test Type")
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
	                if(headerText == "Program Structure")
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
	                if(headerText == "Program")
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
	                

	                if(headerText == "Applicable Type")
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

	                if(headerText == "Module")
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
	
///////////////////////////////////////////////////////////////////
	
	
	$('.selectConsumerType').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programStructureId').html(options);
	$('#programId').html(options);
	
	document.getElementById("applicableType").options[0].selected = 'selected';

	$('#subjectSelectContainerDiv').html(' <span></span> ');
	$('#referenceIdDiv').html(' <span></span> ');
	$('#moduleBatchIdDiv').html(' <span></span> ');
	$('#moduleReferenceIdDiv').html(' <span></span> ');
	$('#subjectSelectContainerDivForOldConfig').html( ' <span></span> ' );

	 
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
			
			var programData = data.programData;
			var programStructureData = data.programStructureData;
			
			
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
	

	document.getElementById("applicableType").options[0].selected = 'selected';

	$('#subjectSelectContainerDiv').html(' <span></span> ');
	$('#referenceIdDiv').html(' <span></span> ');
	$('#moduleBatchIdDiv').html(' <span></span> ');
	$('#moduleReferenceIdDiv').html(' <span></span> ');
	$('#subjectSelectContainerDivForOldConfig').html( ' <span></span> ' );
	
	 
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
			
			
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});


/////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////
	
	
		$('#programId').on('change', function(){	
			
			document.getElementById("applicableType").options[0].selected = 'selected';

			$('#subjectSelectContainerDiv').html(' <span></span> ');
			$('#referenceIdDiv').html(' <span></span> ');
			$('#moduleBatchIdDiv').html(' <span></span> ');
			$('#moduleReferenceIdDiv').html(' <span></span> ');
			$('#subjectSelectContainerDivForOldConfig').html( ' <span></span> ' );

			
		});


/////////////////////////////////////////////////////////////

///////////////////////////////////////////////
$(document).on('change', '#referenceIdDiv select', function(){
	
	$('#subjectSelectContainerDiv').html(' <span></span> ');
	
let applicableType = $('#applicableType	').val();
console.log('IN referenceIdDiv on change event : applicableType = ');
console.log(applicableType);

var data = {
programId:$('.selectProgram').val(),
consumerTypeId:$('#consumerTypeId').val(),
programStructureId:$('#programStructureId').val(),
referenceId:this.value,
acadYear:$('#acadYear').val(),
acadMonth:$('#acadMonth').val()
};
console.log("data : ");
console.log(data);

if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === '' || data.referenceId === ''
|| data.acadYear === ''  || data.acadMonth === ''){
alert('Please Select All Consumer, Program, ProgramStructureId, batch/Module, acadMonth and acadYear');
return;
}

if( data.programId.split(',').length > 1 ||
data.consumerTypeId.split(',').length > 1 || 
data.programStructureId.split(',').length > 1 ){

document.getElementById("applicableType").options[3].selected = 'selected';

return;
}

if(applicableType === 'batch'){
	$('#moduleBatchIdDiv').html(' <span></span> ');
//make api call to get batch details 

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getSubjectsByMastKeyAndBatch",   
data : JSON.stringify(data),
success : function(data) {

console.log("SUCCESS: dataForReferenceId :");
console.log(data.listOfStringData);


var dataForSubject = data.listOfStringData;

if( !dataForSubject ){
alert("Unable to get batches for selected program config, Kindly update selections and try again.");
return;
}

if( dataForSubject.length === 0){
alert("Unable to get batches for selected program config, Kindly update selections and try again.");
return;
}

var labelForSubject = "<label for='refrenceId'>Subject</label>";
var selectElementTopPart = "<select  id='subject'  name='subject' readonly='readonly' placeholder='subject' type='text' class='form-control' required>" 
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
		+ " <option selected value='All'> All </option> " + options 
		+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#subjectSelectContainerDiv').html( htmlToAppend );
//End




},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});
//make api call to get batch details end

}else if(applicableType === 'module'){
	
	if(data.subject === 'All'){
		document.getElementById("applicableType").options[0].selected = 'selected';
		alert("Please select a subject to make test module live. ")
		return;
	}
//make api call to get module details

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getModuleDataByMasterKeyConfig",   
data : JSON.stringify(data),
success : function(data) {

console.log("SUCCESS: dataForReferenceId :");
console.log(data.dataForReferenceId);


var dataForReferenceId = data.dataForReferenceId;

if( !dataForReferenceId && dataForReferenceId.length === 0){
alert("Unable to get Modules for selected program config, Kindly update selections and try again.");
return;
}

var labelForReferenceId = "<label for='refrenceId'>Applicable Modules</label>";
var selectElementTopPart = "<select  id='referenceId'  name='referenceId' readonly='readonly' placeholder='referenceId' type='text' class='form-control' >" 
var selectElementBottomPart = " </select>";

options = ""; 
//Start
for(let i=0;i < dataForReferenceId.length;i++){

options = options + "<option value='" + dataForReferenceId[i].id + "'> " 
			+ dataForReferenceId[i].topic + " </option>";
}


console.log("==========> options\n" + options);

var htmlToAppend = labelForReferenceId 
		+ selectElementTopPart 
		+ " <option selected value='0'> All </option> " + options 
		+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#referenceIdDiv').html( htmlToAppend );
//End




},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});


//make api call to get module details end

}else if(applicableType === 'old'){ 
return; 
} else{
$('#referenceIdDiv').html( "<span></span>" );
alert('Please select an option.');
}
});

///////////////////////////////////////////////


///////////////////////////////////////////////


$('#applicableType').on('change', function(){


$('#subjectSelectContainerDiv').html(' <span></span> ');
$('#referenceIdDiv').html(' <span></span> ');
$('#moduleBatchIdDiv').html(' <span></span> ');
$('#moduleReferenceIdDiv').html(' <span></span> ');
$('#subjectSelectContainerDivForOldConfig').html( ' <span></span> ' );

var data = {
programId:$('.selectProgram').val(),
consumerTypeId:$('#consumerTypeId').val(),
programStructureId:$('#programStructureId').val(),
subject:"All",
acadYear:$('#acadYear').val(),
acadMonth:$('#acadMonth').val()
};
console.log("data : ");
console.log(data);

if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === ''
	|| data.acadYear === ''  || data.acadMonth === ''){
alert('Please Select All Consumer, Program, ProgramStructureId, subject, acadMonth and acadYear');
return;
}

if( data.programId.split(',').length > 1 ||
data.consumerTypeId.split(',').length > 1 || 
data.programStructureId.split(',').length > 1 ){

document.getElementById("applicableType").options[3].selected = 'selected';

}

let applicableType = this.value;
console.log('IN applicableType on change event : applicableType = ');
console.log(applicableType);

if(applicableType === 'batch'){
//make api call to get batch details 

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
	return;
	}
	if(  dataForReferenceId.length === 0){
		alert("Unable to get batches for selected program config, Kindly update selections and try again.");

		document.getElementById("applicableType").options[0].selected = 'selected';
		return;
	}

	
	var labelForReferenceId = "<label for='refrenceId'>Applicable Batch</label>";
	var selectElementTopPart = "<select  id='referenceId'  name='referenceId' readonly='readonly' placeholder='referenceId' type='text' class='form-control' required>" 
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
						+ " <option  selected value='0'> All </option> " + options 
						+ selectElementBottomPart
	
	console.log("htmlToAppend");
	console.log(htmlToAppend);
	$('#referenceIdDiv').html( htmlToAppend );
	//End
	
	createSubjectSelectElement(data.listOfStringData);


},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});
//make api call to get batch details end

}else if(applicableType === 'module'){

//make api call to get module details

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
		return;
		}
		if(  dataForReferenceId.length === 0){
			alert("Unable to get batches for selected program config, Kindly update selections and try again.");

			document.getElementById("applicableType").options[0].selected = 'selected';
			return;
		}
	
	var labelForReferenceId = "<label for='moduleBatchId'>Applicable Batch</label>";
	var selectElementTopPart = "<select  id='moduleBatchId'  name='moduleBatchId' readonly='readonly' placeholder='moduleBatchId' type='text' class='form-control' >" 
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
						+ " <option selected value='0'> All </option> " + options 
						+ selectElementBottomPart
	
	console.log("htmlToAppend");
	console.log(htmlToAppend);
	$('#moduleBatchIdDiv').html( htmlToAppend );
	//End

	createSubjectSelectElement(data.listOfStringData);
	createModulesSelect();

},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});


//make api call to get module details end

}else if(applicableType === 'old'){
	// getSubjectsForOldConfig
	 
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByProgram",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data.subjectsData);
			
			var dataForSubject = data.subjectsData;
			

			if( !dataForSubject){
				alert("Unable to get subjects for selected program config, Kindly update selections and try again.");

				document.getElementById("applicableType").options[0].selected = 'selected';
				return;
				}
				if(  dataForSubject.length === 0){
					alert("Unable to get subjects for selected program config, Kindly update selections and try again.");

					document.getElementById("applicableType").options[0].selected = 'selected';
					return;
				}

			var labelForSubject = "<label for='subject'>Subject</label>";
			var selectElementTopPart = "<select  id='subject'  name='subject' readonly='readonly' placeholder='subject' type='text' class='form-control' required>" 
			var selectElementBottomPart = " </select>";

			options = ""; 
			//Start
			for(let i=0;i < dataForSubject.length;i++){
				
				options = options + "<option value='" + dataForSubject[i].name + "'> " + dataForSubject[i].name + " </option>";
			
			}


			console.log("==========> options\n" + options);

			var htmlToAppend = labelForSubject 
			+ selectElementTopPart 
			+ " <option selected value='All'> All </option> " + options 
			+ selectElementBottomPart

			console.log("htmlToAppend");
			console.log(htmlToAppend);
			$('#subjectSelectContainerDivForOldConfig').html( htmlToAppend );
			//End
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});

return; 
} else{
$('#referenceIdDiv').html( "<span></span>" );
alert('Please select an option.');
}
});

///////////////////////////////////////////////


///////////////////////////////////////////////
$(document).on('change', '#moduleBatchIdDiv select', function(){

$('#subjectSelectContainerDiv').html(' <span></span> ');
$('#referenceIdDiv').html(' <span></span> ');
$('#moduleReferenceIdDiv').html(' <span></span> ');

let applicableType = $('#applicableType	').val();
console.log('IN moduleBatchIdDiv on change event : applicableType = ');
console.log(applicableType);

var data = {
programId:$('.selectProgram').val(),
consumerTypeId:$('#consumerTypeId').val(),
programStructureId:$('#programStructureId').val(),
referenceId:this.value,
acadYear:$('#acadYear').val(),
acadMonth:$('#acadMonth').val()
};
console.log("data : ");
console.log(data);

if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === '' || applicableType === ''
|| data.acadYear === ''  || data.acadMonth === ''){
alert('Please Select All Consumer, Program, ProgramStructureId, batch/Module, acadMonth and acadYear');
return;
}

if( data.programId.split(',').length > 1 ||
data.consumerTypeId.split(',').length > 1 || 
data.programStructureId.split(',').length > 1 ){

document.getElementById("applicableType").options[3].selected = 'selected';

return;
}

if(applicableType === 'module'){
//make api call to get batch details 

$.ajax({
type : "POST",
contentType : "application/json",
url : "/exam/api/getSubjectsByMastKeyAndBatch",   
data : JSON.stringify(data),
success : function(data) {

console.log("SUCCESS: dataForReferenceId :");
console.log(data.listOfStringData);


var dataForSubject = data.listOfStringData;

if( !dataForSubject){
	alert("Unable to get subjects for selected program config, Kindly update selections and try again.");

	document.getElementById("applicableType").options[0].selected = 'selected';
	return;
	}
	if(  dataForSubject.length === 0){
		alert("Unable to get subjects for selected program config, Kindly update selections and try again.");

		document.getElementById("applicableType").options[0].selected = 'selected';
		return;
	}

var labelForSubject = "<label for='refrenceId'>Subject</label>";
var selectElementTopPart = "<select  id='subject'  name='subject' readonly='readonly' placeholder='subject' type='text' class='form-control' required>" 
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
+ " <option selected value='All'> All </option> " + options 
+ selectElementBottomPart

console.log("htmlToAppend");
console.log(htmlToAppend);
$('#subjectSelectContainerDiv').html( htmlToAppend );
//End




},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});
//make api call to get batch details end

}


});

///////////////////////////////////////////////


///////////////////////////////////////////////
$(document).on('change', '#subjectSelectContainerDiv select', function(){

$('#moduleReferenceIdDiv').html(' <span></span> ');

let applicableType = $('#applicableType	').val();
console.log('IN moduleBatchIdDiv on change event : applicableType = ');
console.log(applicableType);

var data = {
programId:$('.selectProgram').val(),
consumerTypeId:$('#consumerTypeId').val(),
programStructureId:$('#programStructureId').val(),
subject:this.value,
acadYear:$('#acadYear').val(),
acadMonth:$('#acadMonth').val(),
referenceId:$('#moduleBatchId').val()
};
console.log("data : ");
console.log(data);

if( data.programId === '' || data.consumerTypeId ==='' || data.programStructureId === '' || data.referenceId === '' || data.subject === ''
|| data.acadYear === ''  || data.acadMonth === ''){
alert('Please Select All Consumer, Program, ProgramStructureId, batch/Module, acadMonth and acadYear');
return;
}

if( data.programId.split(',').length > 1 ||
data.consumerTypeId.split(',').length > 1 || 
data.programStructureId.split(',').length > 1 ){

document.getElementById("applicableType").options[3].selected = 'selected';

return;
}

if(applicableType === 'module'){
//make api call to get batch details 

				$.ajax({
					type : "POST",
					contentType : "application/json",
					url : "/exam/api/getModuleDataByMasterKeyConfig",   
					data : JSON.stringify(data),
					success : function(data) {
						
						console.log("SUCCESS: dataForReferenceId :");
						console.log(data.dataForReferenceId);
						
						
						var dataForReferenceId = data.dataForReferenceId;
						
						if( !dataForReferenceId && dataForReferenceId.length === 0){
							alert("Unable to get Modules for selected program config, Kindly update selections and try again.");
							return;
						}
						
						var labelForReferenceId = "<label for='refrenceId'>Applicable Modules</label>";
						var selectElementTopPart = "<select  id='referenceId'  name='referenceId' readonly='readonly' placeholder='referenceId' type='text' class='form-control' required>" 
						var selectElementBottomPart = " </select>";
						
						options = ""; 
						//Start
						for(let i=0;i < dataForReferenceId.length;i++){
							
							options = options + "<option value='" + dataForReferenceId[i].id + "'> Module : "+dataForReferenceId[i].sessionModuleNo+". "
												+ dataForReferenceId[i].topic + " </option>";
						}
						
						
						console.log("==========> options\n" + options);
						
						var htmlToAppend = labelForReferenceId 
											+ selectElementTopPart 
											+ " <option  selected value='0'> All </option> " + options 
											+ selectElementBottomPart
						
						console.log("htmlToAppend");
						console.log(htmlToAppend);
						$('#moduleReferenceIdDiv').html( htmlToAppend );
						//End




},
error : function(e) {

alert("Please Refresh The Page.")

console.log("ERROR: ", e);
console.log(e);
}
});
//make api call to get batch details end

}else{
	return;
}


});

///////////////////////////////////////////////

//////////////////////////////////////////////

function createSubjectSelectElement(dataForSubject) {

	console.log("createSubjectSelectElement: dataForSubject :");
	console.log(dataForSubject);

	if( !dataForSubject ){
	alert("Unable to get subjects for selected program config, Kindly update selections and try again.");
	return;
	}

	if( dataForSubject.length === 0){
	alert("Unable to get subjects for selected program config, Kindly update selections and try again.");
	return;
	}

	var labelForSubject = "<label for='subject'>Subject</label>";
	var selectElementTopPart = "<select  id='subject'  name='subject' readonly='readonly' placeholder='subject' type='text' class='form-control' required>" 
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
	+ " <option selected value='All'> All </option> " + options 
	+ selectElementBottomPart

	console.log("htmlToAppend");
	console.log(htmlToAppend);
	$('#subjectSelectContainerDiv').html( htmlToAppend );
	//End

}

//////////////////////////////////////////////
			
//////////////////////////////////////////////

function createModulesSelect(){

	var labelForReferenceId = "<label for='refrenceId'>Applicable Modules</label>";
	var selectElementTopPart = "<select  id='referenceId'  name='referenceId' readonly='readonly' placeholder='referenceId' type='text' class='form-control' required>" 
	var selectElementBottomPart = " </select>";
	
	options = ""; 
	
	var htmlToAppend = labelForReferenceId 
						+ selectElementTopPart 
						+ " <option selected value='0'> All </option> " + options 
						+ selectElementBottomPart
	
	console.log("htmlToAppend");
	console.log(htmlToAppend);
	$('#moduleReferenceIdDiv').html( htmlToAppend );
	//End
}

//////////////////////////////////////////////

	
});


</script>

<!-- <script>
	 var id
		 $(".tables").on('click','tr',function(e){
			    e.preventDefault();	
			    var str = $(this).attr('value');
			     id = str.split('~');
			    console.log(id)
			  
			    
			}); 
		$('.tables').Tabledit({

			columns: {
			  identifier: [0, 'id'],                 
			  editable: []
			},
			
		// link to server script
		// e.g. 'ajax.php'
		url: "",
		// class for form inputs
		inputClass: 'form-control input-sm',
		// // class for toolbar
		toolbarClass: 'btn-toolbar',
		// class for buttons group
		groupClass: 'btn-group btn-group-sm',
		// class for row when ajax request fails
		 dangerClass: 'warning',
		// class for row when save changes
		warningClass: 'warning',
		// class for row when is removed
		mutedClass: 'text-muted',
		// trigger to change for edit mode.
		// e.g. 'dblclick'
		eventType: 'click',
		// change the name of attribute in td element for the row identifier
		rowIdentifier: 'id',
		// activate focus on first input of a row when click in save button
		autoFocus: true,
		// hide the column that has the identifier
		hideIdentifier: false,
		// activate edit button instead of spreadsheet style
		editButton: true,
		// activate delete button
		deleteButton: false,
		// activate save button when click on edit button
		saveButton: true,
		// activate restore button to undo delete action
		restoreButton: true,
		// custom action buttons
		// executed after draw the structure
		onDraw: function() { 

			$('.tables').DataTable( {
	        initComplete: function () {
	            this.api().columns().every( function () {
	                var column = this;
	                var headerText = $(column.header()).text();
	                console.log("header :"+headerText);
	             
	   
	              
	            } );
	        }
			
			
	    } );
			return; },

		onSuccess: function() { 

			return; },

		
		onFail: function() { 
return; },

		
		onAlways: function() { return; },

	
		
		onAjax: function(action, serialize) {
	
		}
		
		});
		</script>   -->
</body>
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	

</html>
