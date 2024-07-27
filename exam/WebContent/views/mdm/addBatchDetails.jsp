<html class="no-js"> <!--<![endif]-->

<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.2/css/all.css" integrity="sha384-oS3vJWv+0UjzBfQzYUhtDYW+Pj2yciDJxpsK1OYPAYjqT085Qq/1cq5FLXAZQ7Ay" crossorigin="anonymous">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
<jsp:param value="Batch Details" name="title" />
</jsp:include>

<style>
.column .dataTables .btn-group {
	width: auto;
}
</style>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       	<div class="row"> 
       		<legend>Add Batch Details</legend>
       	</div>
        <%@ include file="../messages.jsp"%>
        
        <form:form modelAttribute="bean" method="post" action="addBatchDetails">
    	<div class="row">
			<div class="col-md-18 column">
				<div class="row">
					<div class="col-md-4 column">
						<div class="form-group">
								<label for="acadYear">Acad Year</label>
								<form:select id="acadYear" path="acadYear" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${bean.acadYear}">
									<form:option value="">Select Acad Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
						</div>
					</div>
						
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="acadMonth">Acad Month</label>
							<form:select id="acadMonth" path="acadMonth" type="text" placeholder="Month" class="form-control" required="required" itemValue="${bean.acadMonth}">
								<form:option value="">Select Acad Month</form:option>
								<form:options items="${monthList }" />
							</form:select>
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
								<label for="examYear">Exam Year</label>
								<form:select id="examYear" path="examYear" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${bean.examYear}">
									<form:option value="">Select Exam Year</form:option>
									<form:options items="${yearList}" />
								</form:select>
						</div>
					</div>
						
					<div class="col-md-4 column">
						<div class="form-group">
							<label for="examMonth">Exam Month</label>
							<form:select id="examMonth" path="examMonth" type="text" placeholder="Month" class="form-control" required="required" itemValue="${bean.examMonth}">
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
						<select data-id="programDataId" id="programId" name="programId" class="selectProgram">
							<option disabled selected value="">Select Program</option>

						</select>
						</div>
					</div>
					
					<div class="col-md-3 column">
						<div class="form-group">
							<label for="sem">Select Semester</label>
							<form:select id="sem" path="sem" class="form-control" required="required"  itemValue="${bean.sem}">
								<form:option value="">Select Semester</form:option>
								<form:options items="${semesterList}" />
							</form:select>
						</div>
					</div>
					
					<div class="col-md-4 column">
						<div class="form-group">
						<label for="batch">Enter Batch Name</label>
							<input name="name" type="text" placeholder="Enter Batch Name">
						</div>
					</div>
					
				</div>
				<div class="form-group">
					<button id="submit" name="submit" class="btn btn-large btn-primary"
					formaction="addBatchDetails">Submit</button>
				</div>
			</div>
		</div>
     </form:form>
     
    	<legend>Existing Batch Entries</legend>
			
		<div class="clearfix"></div>
			<div class="column">
			<div class="table-responsive">
				<table class="table table-striped table-hover dataTables" style="font-size:12px; width: 100%">
					<thead>
					<tr>
						<th>Sr.No</th>
						<th>Batch Name</th>
						<th>Acad Year</th>
						<th>Acad Month</th>
						<th>Exam Year</th>
						<th>Exam Month</th>
						<th>Action</th>
					</tr>
					</thead>
					
					<tbody>
						<c:forEach var="bean" items="${batchList }" varStatus="status">
							<tr value="${bean.id}~${bean.name}">
								<td><c:out value="${status.count}" /></td>
								<td><c:out value="${bean.name }"></c:out></td>
								<td><c:out value="${bean.acadYear }"></c:out></td>
								<td><c:out value="${bean.acadMonth }"></c:out></td>
								<td><c:out value="${bean.examYear }"></c:out></td>
								<td><c:out value="${bean.examMonth }"></c:out></td>
								<td></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div> 
    		</div>
   
    
        </div>
    </section>
    <jsp:include page="../footer.jsp" />

<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

<script>
	$(document).ready( function () {
		let id = "";
	   
	     $(".dataTables").on('click','tr',function(e){
		    e.preventDefault();	
		    var str = $(this).attr('value');
		     id = str.split('~');
		    console.log(id);
		}); 
	    
	    $('.dataTables').Tabledit({
	    	columns: {
				  identifier: [0, 'id'],                 
				  editable: [[1, 'name']]
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
					$('.dataTables').DataTable(); 
				},
				onAjax: function(action, serialize) {
					
					serialize['id'] = id[0];
					let body = JSON.stringify(serialize);
					
					var url = ''
					if(action === 'delete'){
						url = 'deleteBatch'
					}else if (action === 'edit'){
						url = 'updateBatchName'
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
									if(!alert('Entry Deleted Successfully')){window.location.assign('addBatchDetailsForm');}
								}else if (action === 'edit'){
									alert('Entries Saved Successfully')
								}
							} else {
								alert('Entries Failed to update : ' + response.message)
							}

						}
					});
				}
			});
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
       
       	var data = {
					id:this.value
					}
		
		$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "admin/getDataByConsumerTypeForEMBA",   
		data : JSON.stringify(data),
		success : function(data) {
			
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
				url : "admin/getDataByProgramStructureForEMBA",   
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

       
        /* ********************************************************************************************** */
       
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
				url : "admin/getDataByProgramForEMBA",   
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
       
       /* ********************************************************************************************** */
       
});
		
</script>    

</body>
</html>