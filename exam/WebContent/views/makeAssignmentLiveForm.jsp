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
	<jsp:param value="Make Assignment Live" name="title" />
</jsp:include>




<body class="inside">

	<%@ include file="header.jsp"%> 

	<section class="content-container login">
		<div class="container-fluid customTheme">
			
			<div class="row"><legend>Make Assignment Live</legend></div>
			<%-- <%@ include file="messages.jsp"%> --%>
				
				<form id="form" >
					<div class="row">
					<div class="col-md-16 column">
					<div id="successMsg" style="display:none"></div>
					<div id="errorMsg" style="display:none"></div>
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
							<label for="liveType">Assignment Type</label>
							<select  id="liveType" name="liveType" type="text" required="required">
								<option value="">Select Assignment Type</option>
								<option value="Regular">Regular</option>
								<option value="Resit">Resit</option>
							</select>
						</div>
						</div>
						
						
					</div>
					<!-- ///////////////////////////////////////////////////////////////// -->
					
					<div class="row">
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Exam Year</label>
							<select id="examYear" name="examYear"  required="required">
								<option value="">Select Exam Year</option>
								<c:forEach items="${yearList}" var="examyr">
								<option value="${examyr}">
								${examyr}
								</option>
								</c:forEach>
							</select>
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Exam Month</label>
							<select id="examMonth" name="examMonth" type="text" placeholder="Exam Month" class="form-control" required="required">
								<option value="">Select Exam Month</option>
								<option value="Apr">Apr</option>
								<option value="Jun">Jun</option>
								<option value="Sep">Sep</option>
								<option value="Dec">Dec</option>
							</select>
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Acads Year</label>
							<select id="acadsYear" name="acadsYear" type="text"	placeholder="Acads Year" class="form-control" required="required">
								<option value="">Select Acads Year</option>
								
				                 <c:forEach items="${acadsYearList}" var="acadsYr">
								<option value="${acadsYr}">
								${acadsYr}
								</option>
								</c:forEach>
				                
				            
								
							</select>
						</div>
						</div>
						
						<div class="col-md-4 column">
						<div class="form-group">
							<label >Acads Month</label>
							<select id="acadsMonth" name="acadsMonth" type="text" placeholder="Acads Month" class="form-control" required="required">
								<option value="">Select Acads Month</option>
								 <options items="${acadsMonthList}"/>
								 <c:forEach items="${acadsMonthList}" var="acadsMonth">
								<option value="${acadsMonth}">
								${acadsMonth}
								</option>
								</c:forEach>
							</select>
						</div>
						</div>
						
						
					
				
					
					</div>
					<br>
					
					
					
					<div class="form-group">
						<!-- <button id="submit" name="submit" class="btn btn-large btn-primary"
						formaction="submitAssignmentLiveData">Make Assignment Live</button> -->
						<button id="submit" name="submit" class="btn btn-large btn-primary" onClick="submitAssignmentLiveData(event)">
						Make Assignment Live
						</button>
						<div id='theImg' style="display:none">
							<img  src='/exam/resources_2015/gifs/loading-29.gif' alt="Loading..." style="height:40px" />
						</div>
					</div>
					</div>

			</div>

			
			
			<div class="clearfix"></div>
				<div class="column">
				<legend>&nbsp;Live Assignments </legend>
				<div class="table-responsive">
				<table class="table table-striped table-hover tables" style="font-size: 12px">
						<thead>
						<tr>
							<th>Sr. No.</th>
							<th>Acads Year</th>
							<th>Acads Month</th>
							<th>Exam Year</th>
							<th>Exam Month</th>
							<th>Assignment Type</th>
							<th>Consumer Type</th>
							<th>Program Structure</th>
							<th>Program</th>
							
					
							
						</tr>
						</thead>
						<tbody>
						
						 <c:forEach var="AssignmentList" items="${assignmentList}"   varStatus="status">
					         <tr>
					            <td ><c:out value="${status.count}" /></td>
					            <td ><c:out value="${AssignmentList.acadsYear}" /></td>
					            <td ><c:out value="${AssignmentList.acadsMonth}" /></td>
					            <td ><c:out value="${AssignmentList.examYear}" /></td>
					            <td ><c:out value="${AssignmentList.examMonth}" /></td>
					            <td ><c:out value="${AssignmentList.liveType}" /></td>
					            <td ><c:out value="${AssignmentList.consumerType}" /></td>
					            <td ><c:out value="${AssignmentList.programStructure}" /></td>
					            <td ><c:out value="${AssignmentList.program}"/></td>
								
								
									
																 
					        </tr>   
					    </c:forEach>
							
						</tbody>
					</table>
				</div>
				
				
				</div> 
			</form>
			
		</div>
	</section>

	<jsp:include page="footer.jsp" />
	
	
	

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
 $('.tables').DataTable({
	 "searching": false
 }) 
///////////////////////////////////////////////////////////////////
	
	
	$('.selectConsumerType').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programStructureId').html(options);
	$('#programId').html(options);
	
	
	 
	var data = {
			id:this.value
	}
/* console.log(this.value)
	
	console.log("===================> data id : " + id); */
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByConsumerType",   
		data : JSON.stringify(data),
		success : function(data) {
			/* console.log("SUCCESS Program Structure: ", data.programStructureData);
			console.log("SUCCESS Program: ", data.programData); */
			
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
			
			/* console.log("==========> options\n" + options); */
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
			
			
			/* console.log("==========> options\n" + options); */
			$('#programStructureId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			/* console.log("ERROR: ", e); */
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
	/* console.log(this.value) */
	
	/* console.log("===================> data id : " + $('#consumerTypeId').val()); */
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "getDataByProgramStructure",   
		data : JSON.stringify(data),
		success : function(data) {
			
			/* console.log("SUCCESS: ", data.programData); */
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
			
			/* console.log("==========> options\n" + options); */
			$('#programId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			
			
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			/* console.log("ERROR: ", e); */
			display(e);
		}
	});
	
	
});


/////////////////////////////////////////////////////////////

		
			


	
});



function submitAssignmentLiveData(e)
{
	e.preventDefault();
	var consumerTypeId=document.getElementById("consumerTypeId").value;
	var liveType=document.getElementById("liveType").value;
	var examYear=document.getElementById("examYear").value;
	var examMonth=document.getElementById("examMonth").value;
	var acadsYear=document.getElementById("acadsYear").value;
	var acadsMonth=document.getElementById("acadsMonth").value;
	if(consumerTypeId=="")
	{
		alert("Consumer Type is mandatory");	
		return;
	}
	if(liveType=="")
	{
		alert("Assignment Type is mandatory");	
		return;
	}
	if(examYear=="" || examMonth=="")
	{
		alert("Exam Month and Exam Year are mandatory");	
		return;
	}
	if(acadsYear=="" || acadsMonth=="")
	{
		alert("Acads Month and Exam Year are mandatory");	
		return;
	}
	var form=document.getElementById("form");
	var formData = new FormData(form);
	const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
	hideSubmitBtn();
	$.ajax({
		 url : '/exam/m/admin/submitAssignmentLiveData',
	     type : 'POST',
	     enctype: 'multipart/form-data',
	     data : formData,
	     cache : false,
	     contentType : false,
	     processData : false,
	     timeout: 10000000,
	     success:function(data){
	    	showSubmitBtn();
	    	if(data.error!=null)
		    {
	    		document.getElementById("errorMsg").style.display="block";
				const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
				document.getElementById("errorMsg").innerHTML=div;
				document.getElementById("errorMsgDescription").innerHTML="Error in Assignment Make Live : "+data.error;
			}
	    	/* location.reload(); */
	    	document.getElementById("successMsg").style.display="block";
			const div='<div class="alert alert-success alert-dismissible" id="successMsgDescription"></div>';
			document.getElementById("successMsg").innerHTML=div;
			document.getElementById("successMsgDescription").innerHTML="Assignment is Live"+button;
		 },
		 error:function(err){
			showSubmitBtn();
			 document.getElementById("errorMsg").style.display="none";
			document.getElementById("errorMsg").innerHTML=div;
			document.getElementById("errorMsgDescription").innerHTML="Error in Assignment Make Live : "+err+button;
			/* console.log(err); */
		}
	})
}


function hideSubmitBtn()
{
	document.getElementById("submit").style.display="none";
	document.getElementById("theImg").style.display="block";
}

function showSubmitBtn()
{
	document.getElementById("submit").style.display="block";
	document.getElementById("theImg").style.display="none";
}




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