<html class="no-js"> <!--<![endif]-->

<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.2/css/all.css" integrity="sha384-oS3vJWv+0UjzBfQzYUhtDYW+Pj2yciDJxpsK1OYPAYjqT085Qq/1cq5FLXAZQ7Ay" crossorigin="anonymous">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Subject Dates" name="title" />
</jsp:include>

<style>
.column .dataTables .btn-group {
	width: auto;
}
</style>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Add Subject Date</legend></div>
        <%@ include file="messages.jsp"%>
        
        <form:form modelAttribute="studentConfig" method="post" action="addSubjectDate">
        	<div class="row">
				<div class="col-md-18 column">
					<div class="row">
						
						<div class="col-md-3 column">
							<div class="form-group">
								<label for="acadYear">Acad Year</label>
								<form:select id="acadYear" path="acadYear" type="text"	placeholder="Year" class="form-control programSemChange" required="required"  itemValue="${studentConfig.acadYear}">
									<form:option value="">Select Acad Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>
						</div>
						
						<div class="col-md-3 column">
							<div class="form-group">
								<label for="acadMonth">Acad Month</label>
								<form:select id="acadMonth" path="acadMonth" type="text" placeholder="Month" class="form-control programSemChange" required="required" itemValue="${studentConfig.acadMonth}">
									<form:option value="">Select Acad Month</form:option>
									<form:options items="${monthList }" />
								</form:select>
							</div>
						</div>
						
						<div class="col-md-3 column">
							<div class="form-group">
								<label for="examYear">Exam Year</label>
								<form:select id="examYear" path="examYear" type="text" placeholder="Year" class="form-control" required="required"  itemValue="${studentConfig.examYear}">
									<form:option value="">Select Acad Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
							</div>
						</div>
						
						<div class="col-md-3 column">
							<div class="form-group">
								<label for="examMonth">Exam Month</label>
								<form:select id="examMonth" path="examMonth" type="text" placeholder="Month" class="form-control" required="required" itemValue="${studentConfig.examMonth}">
									<form:option value="">Select Exam Month</form:option>
									<form:options items="${monthList }" />
								</form:select>
							</div>
						</div>
						
						<div class="col-md-3 column">
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
						
						<div class="col-md-3 column">
							<div class="form-group">
							<label for="programStructure">Program Structure</label>
							<select data-id="programStructureDataId" id="programStructureId" name="programStructureId" class="selectProgramStructure">
								<option disabled selected value="">Select Program Structure</option>

							</select>
							</div>
						</div> 
							
						<div class="col-md-3 column">
							<div class="form-group">
							<label for="Program">Program</label>
							<select data-id="programDataId" id="programId" name="programId" class="selectProgram programSemChange">
								<option disabled selected value="">Select Program</option>

							</select>
							</div>
						</div>
						
						<div class="col-md-3 column">
							<div class="form-group">
							<label for="sem">Sem</label>
								<select data-id="sem" name="sem" id="sem" required="required" class="programSemChange">
									<option disabled selected value="">Select Semester</option>
									<option value="1">1</option>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
									<option value="6">6</option>
									<option value="7">7</option>
									<option value="8">8</option>
								</select>
							</div>
						</div>
						
						<div class="col-md-4 column">
							<div class="form-group">
							<label for="batch">Batch</label>
								<select data-id="batchId" name="batchId" class="selectBatch" required="required">
									<option disabled selected value="">Select Batch</option>
								</select>
							</div>
						</div> 
						
					</div>
						
					<br>
					
					<!-- ******************************************************************* -->
					
					<legend>Select Subject and Time</legend>
					<% for(int i = 0 ; i < 10 ; i++) {%>
					<div class="row">
					
						<div class="col-md-6 column">
							<div class="form-group">
							<label for="subject">Subject</label>
								<select data-id="subjectId" name="studentConfigFile[<%=i%>].id" class="selectSubject">
									<option disabled selected value="">Select Subject</option>
								</select>
							</div>
						</div>
						
						<div class="col-md-4 column">
							<div class="form-group">
								<label for="startDate">Start Date</label>
								<input path="startDate" id="startDate<%=i%>" name="studentConfigFile[<%=i%>].startDate" type="datetime-local" onfocusout="startDateCheck([<%=i%>])" />
							</div>
						</div>
						
						<div class="col-md-4 column">
							<div class="form-group">
								<label for="endDate">End Date</label> 
								<input path="endDate" id="endDate<%=i%>" name="studentConfigFile[<%=i%>].endDate" type="datetime-local" onfocusout="endDateCheck([<%=i%>])"/>
							</div>
						</div> 
						
						<div class="col-md-4 column">
							<div class="form-group">
							<label for="sequence">Sequence</label>
								<select data-id="sequence" name="studentConfigFile[<%=i%>].sequence" id="sequence">
									<option disabled selected value="">Select Sequence</option>
									<c:forEach var="count" begin="1" end="20">
										<option value="${ count }">${ count }</option>
									</c:forEach>
									<option value="2">2</option>
									<option value="3">3</option>
									<option value="4">4</option>
									<option value="5">5</option>
									<option value="6">6</option>
									<option value="7">7</option>
									<option value="8">8</option>
									<option value="9">9</option>
									<option value="10">10</option>
								</select>
							</div>
						</div>
					
					</div>
					<%} %>
					<br>
					
					
				
				<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
					formaction="addSubjectDate">Submit</button>
				</div>
									
					<legend>Current Status</legend>
					
					<div class="clearfix"></div>
				
				<div class="column">
					<div class="table-responsive">
						<table class="table table-striped table-hover dataTables" style="font-size:12px">
							<thead>
							<tr>
								<th>Sr.No</th>
								<th>Subject</th>
								<th>Term</th>
								<th>Start Date</th>
								<th>End Date</th>
								<th>Acad Year</th>
								<th>Acad Month</th>
								<th>Exam Year</th>
								<th>Exam Month</th> 
								<th>Batch Name</th>
								<th>Add Faculty</th>
								<th>Add Grader</th>
								<th>Add Coordinator</th>
								<th>Add Students</th>								
								<th>Action</th>
							</tr>
							</thead>
							
							<tbody>
								<c:forEach var="bean" items="${currentLiveList }" varStatus="status">
									<tr
										value="${bean.id }~${bean.startDate}~${bean.endDate}">
										<td><c:out value="${status.count}" /></td>
										<td><c:out value="${bean.subject }" /></td>
										<td><c:out value="${bean.sem }" /></td>
										<td><c:out value="${bean.startDate }" /></td>
										<td><c:out value="${bean.endDate }" /></td>
										<td><c:out value="${bean.acadYear }" /></td>
										<td><c:out value="${bean.acadMonth }" /></td>
										<td><c:out value="${bean.examYear }" /></td>
										<td><c:out value="${bean.examMonth }" /></td>
										<td><c:out value="${bean.batchName }" /></td>
										<td><a href="addTimeBoundFacultyForm?id=${bean.id }&subjectId=${bean.prgm_sem_subj_id }&role=${'Faculty'}"><i class="fa-solid fa-user-plus"></i>Add Faculty</a></td>
										<td><a href="addTimeBoundFacultyForm?id=${bean.id }&subjectId=${bean.prgm_sem_subj_id }&role=${'Grader'}"><i class="fa-solid fa-user-plus"></i>Add Grader</a></td>
										<td><a href="addTimeBoundCoordinatorForm?id=${bean.id }&subjectId=${bean.prgm_sem_subj_id }"><i class="fa-solid fa-user-plus"></i>Add Coordinator</a></td>
										<td><a href="addTimeBoundStudentsForm?id=${bean.id }&subjectId=${bean.prgm_sem_subj_id }&batchId=${bean.batchId}"><i class="fa-solid fa-users"></i> Add Students</a></td>										
										<td></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div> 
				</div>
        	</div>	
        	</div>
        </form:form>
        
        </div>
       </section>
       <jsp:include page="footer.jsp" />
       
       
       <!-- ***************************************************************************************** -->
       
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<!-- 	<script src="assets/js/jquery-1.11.3.min.js"></script> -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>
	
	<!-- This Script tag code is used for checking subject date config mismatch -->
	<script type="text/javascript">
		function startDateCheck(id){
			
			var startId="startDate"+id;
			var endId="endDate"+id;
	
			var startdate=new Date(document.getElementById(startId).value);
			var enddate=new Date(document.getElementById(endId).value);
	
			if(enddate!=null && startdate.getTime() >= enddate.getTime()){
				alert("Start Date should be less than End Date.");
				document.getElementById(startId).focus();
			}
		}//startDateCheck
	
		function endDateCheck(id){
			
			var startId="startDate"+id;
			var endId="endDate"+id;
			
			var startdate=new Date(document.getElementById(startId).value);
			var enddate=new Date(document.getElementById(endId).value);
			
			if(startdate!=null && enddate.getTime() <= startdate.getTime()){
				alert("End Date should be grater than Start Date.");
				document.getElementById(endId).focus();	
			}
		}//endDateCheck
	</script>
	
<script>
	$(document).ready( function () {					
		let id = "";
	   
	     $(".dataTables").on('click','tr',function(e){
// 		    e.preventDefault();	
		    var str = $(this).attr('value');
		     id = str.split('~');
		    console.log(id);
		}); 
	    
	    $('.dataTables').Tabledit({
	    	columns: {
				  identifier: [0, 'id'],                 
				  editable: [
					  			[3, 'startDate'],
					  			[4, 'endDate']
				  			]
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
				deleteButton: true,
				// activate save button when click on edit button
				saveButton: true,
				// activate restore button to undo delete action
				restoreButton: true,
				onDraw: function() {
					$('.dataTables').DataTable({
						//Added for column filter
						initComplete: function () {
				        	this.api().columns().every( function () {
				                var column = this;
				                var headerText = $(column.header()).text();
				                console.log("header :"+headerText);
				                
				                if(headerText == "Subject"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				                
				                if(headerText == "Term"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				                
				                if(headerText == "Acad Year"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				                
				                if(headerText == "Acad Month"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}

				                if(headerText == "Exam Year"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				                
				                if(headerText == "Exam Month"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				              	
				                
				                if(headerText == "Batch Name"){
				                   	var select = $('<select style="width:100%;" class="form-control"><option value="">All</option></select>')
				                    .appendTo( $(column.header()) )
				                    .on('change', function () {
				                        var val = $.fn.dataTable.util.escapeRegex(
				                            $(this).val()
				                        );
				 
				                        column.search( val ? '^'+val+'$' : '', true, false ).draw();
				                    });
				                	column.data().unique().sort().each( function ( d, j ) {
				                    	select.append( '<option value="'+d+'">'+d+'</option>' )
				                	});
				              	}
				        	})
						}
					}); 
				},
				
				onAjax: function(action, serialize) {
					
					serialize['id'] = id[0];
					let body = JSON.stringify(serialize);
					
					var url = ''
					if(action === 'delete'){
						url = 'deleteTimeBoundMapping'
					}else if (action === 'edit'){
						url = 'updateTimeBoundMapping'
					}

					$.ajax({
						type : "POST",
						url : url,
						contentType : "application/json",
						data : body,
						dataType : "json",
						success : function(response) {
							
							if (response.Status == "Success") {
								if(action === 'delete'){
									if(!alert('Entry Deleted Successfully')){window.location.reload();}
								}else if (action === 'edit'){
									alert('Entries Saved Successfully')
								}
							}else {
								alert('Entries Failed to update : ' + response.message)
							}

						}
					});
				}
			});
		    $(".tabledit-toolbar").attr("style","text-align:center;margin-left:-80px;");
		    $(".dataTables_filter").attr("style","display:inline-flex;justify-content:space-between;");
		    $(".dataTables_filter").html("<span style='color: #333333;font-weight: 600;margin-top: 0.6rem;margin-right: 2rem;'>Search:</span><input type='search' class='form-control input-sm' placeholder='' aria-controls='DataTables_Table_0'>");
	    	    
		});		
</script>

<script type="text/javascript">
  
$(document).ready (function(){
       $('.selectConsumerType').on('change', function(){
		let id = $(this).attr('data-id');
		
		let options = "<option>Loading... </option>";
		$('#programStructureId').html(options);
		$('#programId').html(options);
		$('.selectSubject').html(options);
		$('.selectBatch').html(options);
       
       	var data = {
					id:this.value
					}
		//console.log(this.value)
		//console.log("===================> data id : " + id);
		
		$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByConsumerTypeForEMBA",   
		data : JSON.stringify(data),
		success : function(data) {
			//console.log("SUCCESS Program Structure: ", data.programStructureData);
			//console.log("SUCCESS Program: ", data.programData);
			//console.log("SUCCESS Subject: ", data.subjectsData);
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
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			//console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
			);
			//End
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			//console.log("ERROR: ", e);
			display(e);
		}
	});
				
});
       /* ********************************************************************************************** */
       
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
		url : "getDataByProgramStructureForEMBA",   
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
			
			//console.log("==========> options\n" + options);
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

       
        /* ********************************************************************************************** */
       
       $('.programSemChange').on('change', function(){
    		
    	   let id = $(this).attr('data-id');
    		let options = "<option>Loading... </option>";
    		$('.selectSubject').html(options);
    		
    		var data = {
    				programId:$('#programId').val(),
    				consumerTypeId:$('#consumerTypeId').val(),
    				programStructureId:$('#programStructureId').val(),
    				sem:$('#sem').val()
    		}
    		console.log(this.value)
    		
    		$.ajax({
				type : "POST",
				contentType : "application/json",
				url : "getDataByProgramForEMBA",   
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
			
			
			//console.log("==========> options\n" + options);
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
       
       /* ********************************************************************************************** */
       ///Batch List Loading
       
       $('.programSemChange').on('change', function(){
    	   let id = $(this).attr('data-id');
   		let options = "<option>Loading... </option>";
   		$('.selectBatch').html(options);
   		
   		var data = {
   				sem:$('#sem').val(),
   				acadYear:$('#acadYear').val(),
				acadMonth:$('#acadMonth').val(),
				programId:$('#programId').val(),
				consumerTypeId:$('#consumerTypeId').val(),
				programStructureId:$('#programStructureId').val()
		}
   		
   		$.ajax({
   			type : "POST",
			contentType : "application/json",
			url : "getBatchList",   
			data : JSON.stringify(data),
			success : function(data) {
				console.log("SUCCESS: ", data.batchData);
				var batchData = data.batchData;
				
				options = ""; 
				if (batchData.length <= 0) {
					$('.selectBatch').html(
						options = options + " <option disabled selected value=''> No Batch Available </option> "
					)
				}else{
					for(let i=0;i < batchData.length;i++){
						options = options + "<option value='" + batchData[i].id + "'> " + batchData[i].name + " </option>";
					}
					$('.selectBatch').html(
							" <option disabled selected value=''> Select Batch </option> " + options
					);
				}
			},
			
			error : function(e) {
				
				alert("Please Refresh The Page.")
				console.log("ERROR: ", e);
				display(e);
			}		
   		});
     });
       
});
</script>       
</body>
</html>