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

<jsp:include page="jscss.jsp">
	<jsp:param value="Make Content Live" name="title" />
</jsp:include>




<body class="inside">

	<%@ include file="header.jsp"%> 

	<section class="content-container login">
		<div class="container-fluid customTheme">
			
			<div class="row"><legend>Make Content Live</legend></div>
			<%@ include file="messages.jsp"%>
				
				<form:form modelAttribute="searchBean" method="post" action="saveContentLiveConfig">
					<div class="row">
					<div class="col-md-16 column">
					
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
						
						
						
						
					</div>
					<!-- ///////////////////////////////////////////////////////////////// -->
					
					<div class="row">
						
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Acads Year</label>
							<form:select id="year" path="year" type="text"	placeholder="Acads Year" class="form-control" required="required"  itemValue="${filesSet.year}">
								<form:option value="">Select Acads Year</form:option>
								
				                 <form:options items="${yearList}"/>
				                
				            
								
							</form:select>
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Acads Month</label>
							<form:select id="month" path="month" type="text" placeholder="Acads Month" class="form-control" required="required" itemValue="${filesSet.month}">
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
					<br>
					
					
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="saveContentLiveConfig">Make Contents Live</button>
					</div>
					</div>

			</div>

			
			
			<div class="clearfix"></div>
				<div class="column">
				<legend>&nbsp;Live Content Configurations </legend>
				<div class="table-responsive">
				<table class="table table-striped table-hover tables" style="font-size: 12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Acads Year</th>
							<th>Acads Month</th>
							<th>Consumer Type</th>
							<th>Program Structure</th>
							<th>Program</th>
							
					
							
						</tr>
						</thead>
						<tbody>
						
						 <c:forEach var="config" items="${contentLiveConfigList}"   varStatus="status">
					         <tr>
					           
					            <td ><c:out value="${status.count}" /></td>
					            <td ><c:out value="${config.year}" /></td>
					            <td ><c:out value="${config.month}" /></td>
					            <td ><c:out value="${config.consumerType}" /></td>
					            <td ><c:out value="${config.programStructure}" /></td>
					            <td ><c:out value="${config.program}"/></td>
																 
					        </tr>   
					    </c:forEach>
							
						</tbody>
					</table>
				</div>
				
				
				</div> 
			</form:form>
			
		</div>
	</section>

	<jsp:include page="footer.jsp" />
	
	
	


<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>

<script	src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
<script	src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

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
	                if(headerText == "Content Type")
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
	                
	            } );
	        }
	    } );
	
///////////////////////////////////////////////////////////////////
	
	
	$('.selectConsumerType').on('change', function(){
	
	
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
	
	
	 
	var data = {
			programStructureId:this.value,
			consumerTypeId:$('#consumerTypeId').val()
	}
	console.log(this.value)
	
	console.log("===================> data id : " + $('#consumerTypeId').val());
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam//admin/getDataByProgramStructure",   
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
