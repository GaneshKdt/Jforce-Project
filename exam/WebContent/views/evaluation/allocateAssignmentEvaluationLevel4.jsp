<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
<jsp:param value="Allocate Assignment Revaluation" name="title" />
</jsp:include>

<script type="text/javascript">
function validateForm(noOfAssignments) {
	
	var checkBoxList = document.getElementsByName('indexes');
	var atleastOneSelected = false;
	for(var i = 0; i < checkBoxList.length; ++i)
	{
	    if(checkBoxList[i].checked){
	    	
	    	var j = checkBoxList[i].value;
	    	var numberOfAssignments = document.getElementById('numberOfAssignments'+j);
	    	if(numberOfAssignments.disabled == false){
		    	atleastOneSelected = true;
		    	break;
	    	}
	    }
	}
	if(!atleastOneSelected){
		alert("Please select at least one Faculty to proceed.");
		return false;
	}
	
	var subject = document.getElementById('subject').value;
	if(subject == ''){
		alert('Please select a subject');
		return false;
	}
	
	totalAssigned = 0;
	for(var i = 0; i < checkBoxList.length; ++i)
	{
	    if(checkBoxList[i].checked){
	    	var i = checkBoxList[i].value;
	    	var numberOfAssignments = document.getElementById('numberOfAssignments'+i).value;
	    	if(numberOfAssignments == ''){
	    		alert('Please enter a value for number of assignments to be allocated');
	    		return false;
	    	}
	    	totalAssigned = (+totalAssigned) + (+numberOfAssignments);
	    	
	    }
	}
	
	if(totalAssigned > noOfAssignments){
		alert('You have assigned '+totalAssigned + ' assignments whereas pending count is '+ noOfAssignments);
		return false;
	} 
	
	return true;
}

function enableNumber(index){
	var i = index.value;
	var numberOfAssignments = document.getElementById('numberOfAssignments'+i);
	if(index.checked == true){
		numberOfAssignments.disabled = false;
	}else{
		numberOfAssignments.disabled = true;
	}
}

</script>


<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Allocate Assignment Revaluation</legend></div>
        <%@ include file="../messages.jsp"%>
        
        <form:form  action="searchRevalAssignmentAllocation" method="post" modelAttribute="searchBean">
			<fieldset>
		
		<div class="panel-body clearfix">
		Note: Pass Fail Trigger must have been run before allocating Assignments for revaluation
			<div class="col-md-6 column">

					<form:hidden path="level"/>
					<div class="form-group">
						<form:select id="year" path="year" required="required" class="form-control"   itemValue="${searchBean.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" required="required" class="form-control"  itemValue="${searchBean.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					
					<div class="form-group">
							<select data-id="consumerTypeDataId" id="consumerTypeId" name="consumerTypeId"  class="selectConsumerType form-control"  required="required">
								<option disabled selected value="">Select Consumer Type</option>
								<c:forEach var="consumerType" items="${consumerType}">
									<c:choose>
										<c:when test="${consumerType.id == searchBean.consumerTypeId}">
											<option selected value="<c:out value="${consumerType.id}"/>">
							                  <c:out value="${consumerType.name}"/>
							                </option>
										</c:when>
										<c:otherwise>
											<option value="<c:out value="${consumerType.id}"/>">
							                  <c:out value="${consumerType.name}"/>
							                </option>
										</c:otherwise>
									</c:choose>

					            </c:forEach>
							</select>
					</div>
					<div class="form-group">
							<select id="programStructureId" name="programStructureId"  class="selectProgramStructure form-control"  required="required">
								<option disabled selected value="">Select Program Structure</option>
							</select>
					</div>
					<div class="form-group">
							<select id="programId" name="programId"  class="selectProgram form-control"  required="required">
								<option disabled selected value="">Select Program</option>
							</select>
					</div>	
					<div class="form-group">
							<select id="subjectId" name="subject"  class="selectSubject form-control"  required="required">
								<option disabled selected value="">Select Subject</option>
							</select>
					</div>					


				<div class="form-group">
					<label class="control-label"></label>
					<div class="controls">
						<button id="findNoOfAssignments" name="submit" class="btn btn-large btn-primary" formaction="searchRevalAssignmentAllocation">Find Assignments</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
			</div>
			
		</div>
		
		<%
		
		HashMap<String, String> facultyMap = (HashMap<String, String>)session.getAttribute("facultyMap");
		%>
		
		<c:if test="${rowCount > 0}">
		<h2>&nbsp;Revaluation Assignments pending Allocation<font size="2px"> (${rowCount} Records Found)&nbsp;</font></h2>
	
			<div class="panel-body table-responsive">
				<table class="table table-striped table-hover" style="font-size:12px">
									<thead>
										<tr> 
											<th>Sr. No.</th>
											<th>Exam Year</th>
											<th>Exam Month</th>
											<th>Subject</th>
											<th>Student ID</th>
											<th>Low Score Reason</th>
											<th>Is Pass?</th>
											<th>Select Faculty</th>
										</tr>
									</thead>
									<tbody>
									
									<c:forEach var="assignmentFile" items="${searchBean.revalAssignments}" varStatus="status">
								        <tr>
								            <td><c:out value="${status.count}"/></td>
											<td><c:out value="${assignmentFile.year}"/></td>
											<td><c:out value="${assignmentFile.month}"/></td>
											<td nowrap="nowrap"><c:out value="${assignmentFile.subject}"/></td>
											<td><c:out value="${assignmentFile.sapId}"/></td>
											<td><c:out value="${assignmentFile.finalReason}"/></td>
											<td><c:out value="${assignmentFile.isPass}"/></td>
											<td>
												<input type="hidden" name="revalAssignments[${status.index}].year" value="${assignmentFile.year}">
												<input type="hidden" name="revalAssignments[${status.index}].month" value="${assignmentFile.month}">
												<input type="hidden" name="revalAssignments[${status.index}].subject" value="${assignmentFile.subject}">
												<input type="hidden" name="revalAssignments[${status.index}].sapId" value="${assignmentFile.sapId}">
												<select name="revalAssignments[${status.index}].facultyId" >
												<option value="">Select Faculty</option>
												<%
												for (Map.Entry<String, String> entry : facultyMap.entrySet())
												{
													String facultyId = entry.getKey();
													String facultyName = entry.getValue();
												%>
												<option value="<%=facultyId%>"><%=facultyName %></option>
												<%} %>
												</select>
												
											</td>
									            
								        </tr>   
								    </c:forEach>
			
									</tbody>
								</table>
				</div>	
				
				<div class="form-group">
					<label class="control-label"></label>
					<div class="controls">
						<button id="allocateAssignmentsForReval" name="submit" class="btn btn-large btn-primary" formaction="allocateAssignmentsForReval">Allocate Assignments</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
	
		</c:if>
		
		
		</fieldset>
		</form:form>

		<br>

	</div>

	</section>

	<jsp:include page="../footer.jsp" />
	<script type="text/javascript">
	
	$(document).ready(function() {
		var consumerTypeId = '${ searchBean.consumerTypeId }';
		var programStructureId = '${ searchBean.programStructureId }';
		var programId = '${ searchBean.programId }';
		var g_subject = '${ searchBean.subject }';
		
		function getConsumerTypeData(value){
			let options = "<option>Loading... </option>";
			$('#programStructureId').html(options);
			$('#programId').html(options);
			$('.selectSubject').html(options);
			
			 
			var data = {
					id:value
			}
		console.log(this.value)
			
			
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
						if(programData[i].id == programId){
							options = options + "<option selected value='" + programData[i].id + "'> " + programData[i].name + " </option>";	
						}else{
							options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
						}
						
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
						if(programStructureData[i].id == programStructureId){
							options = options + "<option selected value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";	
						}else{
							options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
						}
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
						if(g_subject == subjectsData[i].name){
							options = options + "<option selected value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";	
						}else{
							options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
						}
						//options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
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
		}
		
		function getProgramStructureId(value){
			let options = "<option>Loading... </option>";
			$('#programId').html(options);
			$('.selectSubject').html(options);
			
			 
			var data = {
					programStructureId:value,
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
						if(programData[i].id == programId){
							options = options + "<option selected value='" + programData[i].id + "'> " + programData[i].name + " </option>";	
						}else{
							options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
						}
						//options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
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
						if(g_subject == subjectsData[i].name){
							options = options + "<option selected value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";	
						}else{
							options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
						}
						//options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
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
		}
		
		function getProgramIdData(value){
			let options = "<option>Loading... </option>";
			$('.selectSubject').html(options);
			
			 
			var data = {
					programId:value,
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
						if(g_subject == subjectsData[i].name){
							options = options + "<option selected value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";	
						}else{
							options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
						}
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
		}
		
		if(programStructureId != '' && consumerTypeId != ''){
			getConsumerTypeData(consumerTypeId);
		}
		if(programId != '' && programStructureId !=''){
			getProgramStructureId(programStructureId);
		}
		if(g_subject != '' && programId != ''){
			getProgramIdData(programId);
		}
		
		$('.selectConsumerType').on('change', function(){
			
			
			let id = $(this).attr('data-id');
			
			
			getConsumerTypeData(this.value);
			
			
		});
			
			///////////////////////////////////////////////////////
			
			
				$('.selectProgramStructure').on('change', function(){
			
			
					getProgramStructureId(this.value);
			
			
			
			
			
		});

		/////////////////////////////////////////////////////////////

			
				$('.selectProgram').on('change', function(){
			
			
					getProgramIdData(this.value);
			
			
			
				});
	});
	
	</script>

</body>
</html>
