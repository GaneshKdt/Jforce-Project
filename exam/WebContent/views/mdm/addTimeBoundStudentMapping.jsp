<html class="no-js"> <!--<![endif]-->
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.8.2/css/all.css" integrity="sha384-oS3vJWv+0UjzBfQzYUhtDYW+Pj2yciDJxpsK1OYPAYjqT085Qq/1cq5FLXAZQ7Ay" crossorigin="anonymous">
<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/multiple-select-wenzhixin/multiple-select.css">
<link rel="stylesheet" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/css/dataTables.bootstrap.css">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Add TimeBound Mapping" name="title" />
</jsp:include>

<style>
	.select_multiple input{
	height: unset !important;
	width: unset !important;
	}
</style>
<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       	<div class="row"> 
       		<legend>Add TimeBound Mapping</legend>
       	</div>
        <%@ include file="../messages.jsp"%>
       
   	 <form:form modelAttribute="mappingBean" method="post" action="addTimeBoundStudentMapping">
   		
   		<fieldset>
			<div class="panel-body">
				<div class="col-md-6 column">
				
					<div class="form-group">
					<label for="sem">Select Semester</label>
						<form:select id="sem" path="sem" class="form-control batchChange" required="required" itemValue="${fileBean.sem}">
							<form:option value="">Select Semester</form:option>
							<form:options items="${semesterList}" />
						</form:select>
					</div>
					
					<div class="form-group">
					<label for="acadYear">Select Acad Year</label>
						<form:select id="acadYear" path="acadYear" class="form-control batchChange" required="required" itemValue="${fileBean.acadYear}">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
					<label for="acadMonth">Select Acad Month</label>
						<form:select id="acadMonth" path="acadMonth"  class="form-control batchChange" required="required" itemValue="${fileBean.acadMonth}">
							<form:option value="">Select Academic Month</form:option>
							<form:options items="${monthList }" />
						</form:select>
					</div>
				
 					<div class="form-group">
						<label for="batch">Select Batch</label>
						<select id="batchId" data-id="batchId" name="batchId" type="text" placeholder="Year" class="form-control" required="required"  itemValue="${mappingBean.batchId}">
							<option value="">Select Batch</option>
							<%-- <c:forEach var="batch" items="${batchList }">
								<option value="${batch.id }">
									<c:out value="${batch.name }"></c:out>
								</option>
							</c:forEach> --%>
						</select>
					</div>

					<div class="select_multiple">
					<label for="student">Select Students</label>
						<select id="selectStudent"  multiple="multiple" name= "userId" placeholder="Select Students" > 
						</select>
					</div>
					
					<div class="clearfix"></div><br>
					
					<div class="form-group">
						<button id="submit" name="submit" type="submit" class="btn btn-large btn-primary" formaction="addTimeBoundStudentMapping">Submit</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
			
				<div class="col-md-12 column">
					<legend>&nbsp;Current Acad Cycle User Mapping<font size="2px">  </font></legend>
					<div class="table-responsive">
						<table class="table table-striped table-hover dataTables" style="font-size:12px; width: 100%">
							<thead>
							<tr>
								<th>Sr. No.</th>
								<th>Batch Name</th>
								<th>User Id</th>
								<th>TimeBound Id</th>
							</tr>
							</thead>
							<tbody>
								<c:forEach var="bean" items="${userList }" varStatus="status">
									<tr>
										<td><c:out value="${status.count}" /></td>
										<td><c:out value="${bean.name }"></c:out>
										<td><c:out value="${bean.userId }"></c:out>
										<td><c:out value="${bean.timebound_subject_config_id }"></c:out>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</fieldset>
	</form:form>
	
	 </div>
   	</section>
   	
<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />assets/js/jquery.tabledit.js"></script>

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
	<script src="https://cdn.datatables.net/1.10.13/js/jquery.dataTables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/dataTables.bootstrap.js"></script>
	<script src="https://cdn.datatables.net/buttons/1.2.4/js/dataTables.buttons.min.js"></script>

<!-- 	<script src="https://code.jquery.com/jquery-3.4.1.min.js"></script> -->
	<script src="https://unpkg.com/multiple-select@1.3.1/dist/multiple-select.min.js"></script>
	
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
				editButton: false,
				// activate delete button
				deleteButton: false,
				// activate save button when click on edit button
				saveButton: false,
				// activate restore button to undo delete action
				restoreButton: true,
				onDraw: function() { 
					$('.dataTables').DataTable(); 
				},
				onAjax: function(action, serialize) {
					
					serialize['id'] = id[0];
					let body = JSON.stringify(serialize);
					
					$.ajax({
						type : "POST",
						url : '',
						contentType : "application/json",
						data : body,
						dataType : "json",
						success : function(response) {
							console.log(response)
							console.log(JSON.stringify(response))
							if (response.Status == "Success") {
									alert('Entries Saved Successfully')
							} else {
								alert('Entries Failed to update : ' + response.message)
							}
						}
					});
				}
			});
		});
</script>


<!-- Get student list by batchId Start -->

<script type="text/javascript">
    $(function () {
      $('#selectStudent').multipleSelect({
    	  filter: true,
//     	  width: 250
      })
    });
   
    $(document).ready (function(){
    	
    //Get Batches by Year/Month
    
    	$('.batchChange').on('change', function(){
    	
    		let options = "<option>Loading... </option>";
        	$('#batchId').html(options);
    		
        	var data = {
        		sem:$('#sem').val(),
        		acadYear:$('#acadYear').val(),
        		acadMonth:$('#acadMonth').val()
            }
        	console.log('Request data in : ')
        	console.log(data)
        	
        	$.ajax({
        		type : "POST",
        		contentType : "application/json",
        		url : "getBatchListByYearMonth",
        		data : JSON.stringify(data),
        		success : function(data) {
        			
        			var batchData = data.batchData;
        			
        			options = ""; 
    				if (batchData.length <= 0) {
    					$('#batchId').html(
    						options = options + " <option disabled selected value=''> No Batch Available </option> "
    					)
    				}else{
    					for(let i=0;i < batchData.length;i++){
    						options = options + "<option value='" + batchData[i].id + "'> " + batchData[i].name + " </option>";
    					}
    					$('#batchId').html(
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
    	
    //Get Students For Batch Insert --> START	
    	
    	$('#batchId').on('change', function(){
    	let id = $(this).attr('data-id');
    	
    	let options = "<option>Loading... </option>";
    	$('#selectStudent').html(options);
    	
    	var data = {
    		batchId : this.value,
    		sem:$('#sem').val(),
    		acadYear : $('#acadYear').val(),
    		acadMonth : $('#acadMonth').val()
    	}
    	
    	$.ajax({
    		type : "POST",
    		contentType : "application/json",
    		url : "getMBAStudentsList",
    		data : JSON.stringify(data),
    		success : function(data) {
    			
    		var studentsData = data.studentList;
    		
    		options = ""; 
			//Data Insert For Subjects List
			
				for(let i=0;i < studentsData.length;i++){
					
					options = options + "<option value='" + studentsData[i].sapid + "'> " + studentsData[i].sapid + " </option>";
				}
				
				$('#selectStudent').html(
						 options
				);
				$('#selectStudent').multipleSelect('refresh');
    		},
    		error : function(e) {
    			alert("Please Refresh The Page.")
    			console.log("ERROR: ", e);
    			display(e);
    		}
    	})
    	})
    })
</script>

<!-- Get student list by batchId End -->
    
</body>
</html>