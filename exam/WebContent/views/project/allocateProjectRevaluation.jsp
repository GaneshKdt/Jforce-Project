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
<jsp:param value="Allocate Project Revaluation" name="title" />
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
        <div class="row"><legend>Allocate Project Revaluation</legend></div>
        <%@ include file="../messages.jsp"%>
        
        <form:form  action="searchRevalAssignmentAllocation" method="post" modelAttribute="searchBean">
			<fieldset>
		
		<div class="panel-body clearfix">
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
					<label class="control-label"></label>
					<div class="controls">
						<button id="findNoOfAssignments" name="submit" class="btn btn-large btn-primary" formaction="searchProjectsForReval">Find Projects for Reval</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
			</div>
			
		</div>
		
		<%
		
		HashMap<String, String> facultyMap = (HashMap<String, String>)session.getAttribute("facultyMap");
		%>
		
		<c:if test="${rowCount > 0}">
		<h2>&nbsp;Revaluation pending Allocation<font size="2px"> (${rowCount} Records Found)&nbsp;</font></h2>
	
			<div class="panel-body table-responsive">
				<table class="table table-striped table-hover" style="font-size:12px">
									<thead>
										<tr> 
											<th>Sr. No.</th>
											<th>Exam Year</th>
											<th>Exam Month</th>
											<th>Student ID</th>
											<th>Select Faculty</th>
										</tr>
									</thead>
									<tbody>
									
									<c:forEach var="assignmentFile" items="${searchBean.revalAssignments}" varStatus="status">
								        <tr>
								            <td><c:out value="${status.count}"/></td>
											<td><c:out value="${assignmentFile.year}"/></td>
											<td><c:out value="${assignmentFile.month}"/></td>
											<td><c:out value="${assignmentFile.sapId}"/></td>
											<td>
												<input type="hidden" name="revalAssignments[${status.index}].year" value="${assignmentFile.year}">
												<input type="hidden" name="revalAssignments[${status.index}].month" value="${assignmentFile.month}">
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
						<button id="allocateAssignmentsForReval" name="submit" class="btn btn-large btn-primary" formaction="allocateProjectsForReval">Allocate Revaluation</button>
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
	

</body>
</html>
