<!DOCTYPE html>
<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.CaseStudyExamBean"%>
<html class="no-js">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Search and Assign Submitted Case Study" name="title" />
</jsp:include>

<body class="inside">

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			
			<%@ include file="../messages.jsp"%>
			<div class="panel-body clearfix">
		
				<form:form action="" method="post" modelAttribute="csBean">
					<fieldset>
						<div class="col-md-18 column">
						<div class="row">
							<legend>Search Submitted Case Study</legend>
						</div>
							<div class="row">
								<div class="col-md-4 column">
									<div class="form-group">
									<label for="batchYear">Batch Year</label>
										 <form:select id="batchYear" path="batchYear" type="text"
											placeholder="Year" class="form-control" required="required"
											itemValue="${csBean.batchYear}">
											<form:option value="">Select Batch Year</form:option>
											<form:options items="${yearList}" /> 
										</form:select> 
									</div>
								</div>
								<div class="col-md-4 column">
									<div class="form-group">
									<label for="batchMonth">Batch Month</label>
										<form:select id="batchMonth" path="batchMonth" type="text"
											placeholder="Month" class="form-control" required="required"
											itemValue="${csBean.batchMonth}">
											<form:option value="">Select Batch Month</form:option>
										  	 <form:options items="${monthList}" />
										</form:select>
									</div>
								</div>
								<div class="col-md-4 column">
									<div class="form-group">
									<label for="program">Program</label>
										<form:select path="program" type="text" id="program"
											placeholder="Program" class="form-control" required="required" >
											<form:option value="">Select Program</form:option>
											<form:option value="EPBM">EPBM</form:option>
										</form:select>
									</div>
								</div>
								<div class="col-md-4 column">
									<div class="form-group">
									<br>
										<button id="submit" name="submit"
										class="btn btn-small btn-primary"
										formaction="assignSubmittedCaseStudyToFaculty">Search</button>
									</div>
								</div>
							</div>
							<c:if test="${uncheckedCaseStudySize > 0 }">
								<div class="row">
									<legend>Assign Submitted Case Study (${uncheckedCaseStudySize} pending for evaluation) </legend>
								</div>
								
									<div class="row">
											<table class="table table-striped" style="font-size: 12px;">
												<thead>
													 <tr>
													 	<td>Sr No</td>
													 	<td>CaseStudy Assigned</td>
													 </tr>
												</thead>
												<tbody>
													<c:forEach var="faculty" items="${facultyList}" varStatus="status">
														<tr>
															<td><c:out value="${status.count}" /></td>
															<td><input path="indexes" type ="checkbox" name="indexes" style="width:15px;height:15px;margin-top:10px" value="${status.count}" onClick="enableNumber(this);">
																<input id="numberOfCaseStudy${status.count}" name="numberOfCaseStudies" path = "numberOfCaseStudies"  style="width:100px;height:30px;margin:3px;line-height:0px" type="number" disabled="true"/>
																<form:input type="hidden" path="faculties" value="${faculty.facultyId}"/>
																<span style="padding-left:3px;margin-bottom:20px"> ${faculty.firstName}  ${faculty.lastName} <br> (Assigned :: ${faculty.assignmentsAllocated }) </span>
														   </td>
															
														</tr>
													</c:forEach>
													
												</tbody>
											</table>
											<button id="allocate" class="btn btn-large btn-primary" formaction="allocateCaseStudyEvaluation" 
											onClick="return validateForm(${uncheckedCaseStudySize });">Allocate to Faculty</button>
												
									</div>
				
								</c:if>
								<c:if test="${uncheckedCaseStudySize == 0 }">
									<div class="row">
										<legend>Assign Submitted Case Study (${uncheckedCaseStudySize} pending for evaluation) </legend>
									</div>
								</c:if>
							
						</div>
						
					</fieldset>
				</form:form>
			</div>
			
			
		</div>
	</section>
	<jsp:include page="../footer.jsp" />
</body>
<script type="text/javascript">
function enableNumber(index){
	console.log('enabled called');
	var i = index.value;
	var numberOfCaseStudy = document.getElementById('numberOfCaseStudy'+i);
	if(index.checked == true){
		numberOfCaseStudy.disabled = false;
	}else{
		numberOfCaseStudy.disabled = true;
	}
}

function validateForm(noOfCaseStudy) {
	
	var checkBoxList = document.getElementsByName('indexes');
	var atleastOneSelected = false;
	for(var i = 0; i < checkBoxList.length; ++i)
	{
	    if(checkBoxList[i].checked){
	    	
	    	var j = checkBoxList[i].value;
	    	var numberOfCaseStudy = document.getElementById('numberOfCaseStudy'+j);
	    	if(numberOfCaseStudy.disabled == false){
		    	atleastOneSelected = true;
		    	break;
	    	}
	    }
	}
	if(!atleastOneSelected){
		alert("Please select at least one Faculty to proceed.");
		return false;
	}
	
	var program = document.getElementById('program').value;
	if(program == ''){
		alert('Please select a Program');
		return false;
	}
	
	totalAssigned = 0;
	for(var i = 0; i < checkBoxList.length; ++i)
	{
	    if(checkBoxList[i].checked){
	    	var i = checkBoxList[i].value;
	    	var numberOfCaseStudy = document.getElementById('numberOfCaseStudy'+i).value;
	    	if(numberOfCaseStudy == ''){
	    		alert('Please enter a value for number of assignments to be allocated');
	    		return false;
	    	}
	    	console.log(numberOfCaseStudy);
	    	totalAssigned = (+totalAssigned) + (+numberOfCaseStudy);
	    	
	    }
	}
	console.log(noOfCaseStudy);
	if(totalAssigned > noOfCaseStudy){
		alert('You have assigned '+totalAssigned + ' projects whereas pending count is '+ noOfCaseStudy);
		return false;
	} 
	if(totalAssigned < 0){
		alert('You have assigned '+totalAssigned + ' projects whereas pending count is '+ noOfCaseStudy);
		return false;
	} 
	
	return true;
}


</script>
</html>
