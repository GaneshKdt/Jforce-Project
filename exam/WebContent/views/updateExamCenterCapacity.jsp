<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Update Exam Center Capacity" name="title" />
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

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Update Exam Center Capacity</legend></div>
        <%@ include file="messages.jsp"%>
        
        <form:form  action="findNoOfAssignments" method="post" modelAttribute="searchBean">
			<fieldset>
		
		<div class="row clearfix">
		
			<div class="col-md-6 column">

				
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
							<form:select id="subject" path="subject"  class="form-control"   itemValue="${searchBean.subject}">
								<form:option value="">Select Exam Center</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>					


				<div class="form-group">
					<label class="control-label"></label>
					<div class="controls">
						<button id="findNoOfAssignments" name="submit" class="btn btn-large btn-primary" formaction="getNoOfAssignments">Find Assignments</button>
						
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
			</div>
			
		</div>
		
		
		<%if("true".equalsIgnoreCase((String)request.getAttribute("showFaculties"))){ %>
		<h2>&nbsp;Faculty List  (${numberOfSubjects } submissions pending for allocation) </h2>
		<c:if test="${numberOfSubjects > 0 }">
			<spring:eval expression="@propertyConfigurer.getProperty('MAX_ASSIGNMENTS_PER_FACULTY')" var="MAX_ASSIGNMENTS_PER_FACULTY"/>
			<%
			String maxAssignmentsStr = (String)pageContext.getAttribute("MAX_ASSIGNMENTS_PER_FACULTY");
			int maxAssignments = Integer.parseInt(maxAssignmentsStr);
			 %>
			<div class="row clearfix">
			<%int i = 0; %>
			<c:forEach var="faculty" items="${facultyList}" varStatus="status">
		        <div class="col-md-6 column">
		        	<form:checkbox path="indexes" name="indexes" style="width:15px;height:15px;margin-top:10px" value="<%=i %>" 
		        	onClick="enableNumber(this);" />
		        	<input id="numberOfAssignments<%=i %>" name="numberOfAssignments" max="${faculty.available}" style="width:100px;height:30px;margin:3px;line-height:0px" type="number" disabled="true"/>
		        	<form:input type="hidden" path="faculties" value="${faculty.facultyId}"/>
		        	<span style="padding-left:3px;margin-bottom:20px"> ${faculty.firstName} ${faculty.lastName} (${faculty.assignmentsAllocated }/${MAX_ASSIGNMENTS_PER_FACULTY }) </span>
		        	
		        </div>
		        <%i++; %>
		    </c:forEach>
			</div>
			<div class="form-group">
			<button id="allocate" class="btn btn-large btn-primary" formaction="allocateAssignmentEvaluation" onClick="return validateForm(${numberOfSubjects });">Allocate to Faculty</button>
			</div>
		</c:if>
		<%
		}//End of if %>
		
		
		</fieldset>
		</form:form>

		<br>

	</div>

	</section>

	  <jsp:include page="footer.jsp" />

	<script type="text/javascript">
	
	$(document).ready(function() {
		/* $('#allocate').hide();
		$("#findNoOfAssignments").click(function(){
			
	        $.ajax({
	            url : 'findNoOfAssignments',
	            data : {
	                year : $('#year').val(),
	                month : $('#month').val(),
	                subject : $('#subject').val()
	            },
	            success : function(responseText) {
	                $('#noOfSubjects').html(responseText + ' Unallocated Submissions Found for '+ $('#subject').val());
	                if(responseText > 0){
	                	$('#allocate').show();
	                }
	            }
	        });
	    }); */
	});
	
	</script>

</body>
</html>
