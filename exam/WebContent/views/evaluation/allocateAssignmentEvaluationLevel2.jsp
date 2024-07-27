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
        <div class="row"><legend>Allocate Assignment Re-Revaluation</legend></div>
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
							<select data-id="consumerTypeDataId" required="required" value="${ searchBean.consumerTypeId }" id="consumerTypeId" name="consumerTypeId"   class="selectConsumerType form-control" >
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
							<select id="programStructureId" required="required" name="programStructureId"  class="selectProgramStructure form-control" >
								<option disabled selected value="">Select Program Structure</option>
							</select>
					</div>
					<div class="form-group">
							<select id="programId" required="required" name="programId"  class="selectProgram form-control" >
								<option disabled selected value="">Select Program</option>
							</select>
					</div>
					
					<div class="form-group">
						<form:select id="subject" path="subject" class="form-control"   itemValue="${searchBean.subject}">
							<form:option value="">Select Subject</form:option>
							<form:options items="${subjectList}" />
						</form:select>
					</div>


				<div class="form-group">
					<label class="control-label"></label>
					<div class="controls">
						<button id="findNoOfAssignments" name="submit" class="btn btn-large btn-primary" formaction="searchReRevalAssignmentAllocation">Find Assignments for Re-Reval</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
			</div>
			
		</div>
		
		<%
		
		HashMap<String, String> facultyMap = (HashMap<String, String>)session.getAttribute("facultyMap");
		%>
		
		<c:if test="${rowCount > 0}">
		<h2>&nbsp;Revaluation Assignments pending Allocation<font size="2px"> (${rowCount} Records Found)&nbsp;</font>
		<a href="downloadAssignmentReEvaluationExcel"> Download</a> 
		</h2>
			
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
											<th>Original/Reval Score</th>
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
											<td><c:out value="${assignmentFile.assignmentscore}"/>/<c:out value="${assignmentFile.revaluationScore}"/></td>
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
						<button id="allocateAssignmentsForReval" name="submit" class="btn btn-large btn-primary" formaction="allocateAssignmentsForReReval">Allocate Assignments</button>
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
	<script>
		 var consumerTypeId = '${ searchBean.consumerTypeId }';
		 var programStructureId = '${ searchBean.programStructureId }';
		 var programId = '${ searchBean.programId }';
		 var g_subject = '${ searchBean.subject }';
		</script>
	<script>
		console.log("==========> consumerTypeId 1: " + consumerTypeId);	
	</script>
	<%@ include file="../../views/common/consumerProgramStructure.jsp" %>
	<script>
		console.log("==========> consumerTypeId 2: " + consumerTypeId);	
	</script>

</body>
</html>
