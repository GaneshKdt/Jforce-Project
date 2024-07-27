


<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.StudentExamBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Revaluate Project" name="title" />
</jsp:include>


<%try{ %>

<script type="text/javascript">

	function validateForm(){
		score = document.getElementById('score').value;
		var remarks = document.getElementById('remarks').value;
		var reason = document.getElementById('reason').value;
		
		if(score < 10 || score == 30){
			if(remarks.trim() == ''){
				alert('Please enter a remark for the score mentioned.');
				document.getElementById("remarks").focus();
				return false;
			}
		}
		
		if(score < 10 || score == 30){
			if(reason.trim() == ''){
				alert('Please enter a reason for the score mentioned.');
				document.getElementById("reason").focus();
				return false;
			}
		}
		var total = 0;
		var remarksAlertMessage = "";
		
		var q1Marks = parseInt(document.getElementById("q1Marks").value);
		var q2Marks = parseInt(document.getElementById("q2Marks").value);
		var q3Marks = parseInt(document.getElementById("q3Marks").value);
		var q4Marks = parseInt(document.getElementById("q4Marks").value);
		var q5Marks = parseInt(document.getElementById("q5Marks").value);
		var q6Marks = parseInt(document.getElementById("q6Marks").value);
		var q7Marks = parseInt(document.getElementById("q7Marks").value);
		var q8Marks = parseInt(document.getElementById("q8Marks").value);
		var q9Marks = parseInt(document.getElementById("q9Marks").value);
		
		var q1Remarks = document.getElementById("remarks1").value;
		var q2Remarks = document.getElementById("remarks2").value;
		var q3Remarks = document.getElementById("remarks3").value;
		var q4Remarks = document.getElementById("remarks4").value;
		var q5Remarks = document.getElementById("remarks5").value;
		var q6Remarks = document.getElementById("remarks6").value;
		var q7Remarks = document.getElementById("remarks7").value;
		var q8Remarks = document.getElementById("remarks8").value;
		var q9Remarks = document.getElementById("remarks9").value;
		
		
		if(!isNaN(q1Marks)){
			total += parseInt(q1Marks);
			if(q1Remarks.trim() == ""){
				remarksAlertMessage = remarksAlertMessage + "Please enter remarks for Q.1 \n";
			}
		}
		if(!isNaN(q2Marks)){
			total += parseInt(q2Marks);
			if(q2Remarks.trim() == ""){
				remarksAlertMessage = remarksAlertMessage + "Please enter remarks for Q.2 \n";
			}
		}
		if(!isNaN(q3Marks)){
			total += parseInt(q3Marks);
			if(q3Remarks.trim() == ""){
				remarksAlertMessage = remarksAlertMessage + "Please enter remarks for Q.3 \n";
			}
		}
		if(!isNaN(q4Marks)){
			total += parseInt(q4Marks);
			if(q4Remarks.trim() == ""){
				remarksAlertMessage = remarksAlertMessage + "Please enter remarks for Q.4 \n";
			}
		}
		if(!isNaN(q5Marks)){
			total += parseInt(q5Marks);
			if(q5Remarks.trim() == ""){
				remarksAlertMessage = remarksAlertMessage + "Please enter remarks for Q.5 \n";
			}
		}
		if(!isNaN(q6Marks)){
			total += parseInt(q6Marks);
			if(q6Remarks.trim() == ""){
				remarksAlertMessage = remarksAlertMessage + "Please enter remarks for Q.6 \n";
			}
		}
		if(!isNaN(q7Marks)){
			total += parseInt(q7Marks);
			if(q7Remarks.trim() == ""){
				remarksAlertMessage = remarksAlertMessage + "Please enter remarks for Q.7 \n";
			}
		}
		if(!isNaN(q8Marks)){
			total += parseInt(q8Marks);
			if(q8Remarks.trim() == ""){
				remarksAlertMessage = remarksAlertMessage + "Please enter remarks for Q.8 \n";
			}
		}
		if(!isNaN(q9Marks)){
			total += parseInt(q9Marks);
			if(q9Remarks.trim() == ""){
				remarksAlertMessage = remarksAlertMessage + "Please enter remarks for Q.9 \n";
			}
		}
		
		if(total > 100){
			alert('Total cannot be greater than 100');
			marksOption.value = "";
			return false;
		}
		
		if(remarksAlertMessage != ""){
			alert(remarksAlertMessage);
			return false;
		}
		
		return confirm('Are you sure you want to save marks as '+score);
	}
	
	
	
	
	
	function setTotal(marksOption){
		
		var attempts = ${assignmentFile.revaluationCount};
		if(attempts >= 3){
			return;
		}
		
		var remarksAlertMessage = "";
		
		var q1Marks = parseInt(document.getElementById("q1Marks").value);
		var q2Marks = parseInt(document.getElementById("q2Marks").value);
		var q3Marks = parseInt(document.getElementById("q3Marks").value);
		var q4Marks = parseInt(document.getElementById("q4Marks").value);
		var q5Marks = parseInt(document.getElementById("q5Marks").value);
		var q6Marks = parseInt(document.getElementById("q6Marks").value);
		var q7Marks = parseInt(document.getElementById("q7Marks").value);
		var q8Marks = parseInt(document.getElementById("q8Marks").value);
		var q9Marks = parseInt(document.getElementById("q9Marks").value);
		
		
		var total = 0;
		
		if(!isNaN(q1Marks)){
			total += parseInt(q1Marks);
		}
		if(!isNaN(q2Marks)){
			total += parseInt(q2Marks);
		}
		if(!isNaN(q3Marks)){
			total += parseInt(q3Marks);
		}
		if(!isNaN(q4Marks)){
			total += parseInt(q4Marks);
		}
		if(!isNaN(q5Marks)){
			total += parseInt(q5Marks);
		}
		if(!isNaN(q6Marks)){
			total += parseInt(q6Marks);
		}
		if(!isNaN(q7Marks)){
			total += parseInt(q7Marks);
		}
		if(!isNaN(q8Marks)){
			total += parseInt(q8Marks);
		}
		if(!isNaN(q9Marks)){
			total += parseInt(q9Marks);
		}
		
		if(total > 100){
			alert('Total cannot be greater than 100');
			marksOption.value = "";
			return false;
		}
		
		if(remarksAlertMessage != ""){
			alert(remarksAlertMessage);
			return false;
		}
		document.getElementById("score").value = total;
	}

</script>

<%
	String sapId = (String)session.getAttribute("userId");
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
	
	
%>

<body class="inside">


<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        
        <div class="row"><legend>${assignmentFile.subject}</legend></div>
        
        
        <%@ include file="../messages.jsp"%>
		
		<form:form  action="submitAssignment" method="post" modelAttribute="assignmentFile" id="evalForm">
			<fieldset>
			<div class="panel-body">
				<c:if test="${not empty assignmentFile.previewPath}">
				<div class="col-md-12 column">
				<h2 align="center">Student Submitted File</h2>
				
				<div  id="assignment">
				<embed  align="middle" src="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}?id=<%=Math.random() %>" 
				width = "100%" height="820px"></embed>
				</div>
				
				</div>
				</c:if>

				<div class="col-md-6 column">
				
				<!-- This DIV below is hidden till Faculty spends time on reading assignment and scrolls till end -->
				<div id="evaluation">
					<div id="marksDiv">
						<div class="form-group">
								<label class="control-label" for="score">Total Score (Calculated based on Marks for individual sections)</label>
								<form:input id="score" path="revaluationScore" type="text" placeholder="Project Score" 
								class="form-control" value="${assignmentFile.revaluationScore}" readonly="true"/>
						</div>
						
						<div class="form-group">
								<label class="control-label" for="remarks">Overall Remarks</label>
								<form:textarea path="revaluationRemarks" value="${assignmentFile.revaluationRemarks}" cols="47" id="remarks" required="required"/>
						</div>
						
						<div class="form-group">
						<form:select id="reason" path="revaluationReason"  class="form-control"  required="required" itemValue="${assignmentFile.revaluationReason}">
							<form:option value="">Select Reason</form:option>
							<form:option value="Excellent">Excellent</form:option>
							<form:option value="Very Good">Very Good</form:option>
							<form:option value="Good">Good</form:option>
							<form:option value="Average">Average</form:option>
							<form:option value="Below Average">Below Average</form:option>
							<form:option value="Copy Case-Internet/Course Book">Copy Case (Internet/Course Book)</form:option>
							<form:option value="Copy Case-Other Student">Copy Case (Other student/s)</form:option>
							<form:option value="Wrong Answer"> Wrong Answer/s</form:option>
							<form:option value="Other subject File">Other subject File</form:option>
							<form:option value="Scanned/Handwritten Project">Scanned/Handwritten Project</form:option>
							<form:option value="Only Questions written">Only Questions written/Question Paper Uploaded</form:option>
							<form:option value="Blank Project">Blank Project</form:option>
							<form:option value="Corrupt file uploaded">Corrupt file uploaded</form:option>
						</form:select>
						</div>
					
					<hr>
					<h4 align="left" style="color: #c72127;">Enter Marks per Marking Criteria</h4>
					
						<div class="form-group">
								<form:label for="q1Marks" path="q1RevalMarks">Clarity of objectives scope and coverage</form:label>
								<form:select id="q1Marks" path="q1RevalMarks" itemValue="${assignmentFile.q1RevalMarks}" class="form-control" onchange="setTotal(this);"  required="required">
									<form:option value="">Select Score</form:option>
									<form:options items="${q1MarksOptionsList}"/>
								</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q1RevalRemarks" value="${assignmentFile.q1RevalRemarks}" class="form-control" id="remarks1" maxlength="250" placeholder="Enter Remarks" required="required"/>
						</div>
						
						<hr/>
						
						
						
						<div class="form-group">
							<form:label for="q2Marks" path="q2RevalMarks">Study methodology for data collection</form:label>
							<form:select id="q2Marks"  path="q2RevalMarks" itemValue="${assignmentFile.q2RevalMarks}" class="form-control" onchange="setTotal(this);"  required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q2MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q2RevalRemarks" value="${assignmentFile.q2RevalRemarks}" class="form-control" id="remarks2" maxlength="250" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q3Marks" path="q3RevalMarks">Analysis of data, tools and techniques</form:label>
							<form:select id="q3Marks"  path="q3RevalMarks" itemValue="${assignmentFile.q3RevalMarks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q3MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q3RevalRemarks" value="${assignmentFile.q3RevalRemarks}" class="form-control" id="remarks3" maxlength="250" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q4Marks" path="q4RevalMarks">Understanding of the subject and conceptualization of the Key areas</form:label>
							<form:select id="q4Marks"  path="q4RevalMarks" itemValue="${assignmentFile.q4RevalMarks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q4MarksOptionsList}"/>
							</form:select>
						</div>
						<div class="form-group">
							<form:textarea path="q4RevalRemarks" value="${assignmentFile.q4RevalRemarks}" class="form-control" id="remarks4" maxlength="250" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q5Marks" path="q5RevalMarks">Innovative techniques/approach to problem scheme</form:label>
							<form:select id="q5Marks"  path="q5RevalMarks" itemValue="${assignmentFile.q5RevalMarks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q5MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q5RevalRemarks" value="${assignmentFile.q5RevalRemarks}" class="form-control" id="remarks5" maxlength="250" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q6Marks" path="q6RevalMarks">Conclusions drawn</form:label>
							<form:select id="q6Marks"  path="q6RevalMarks" itemValue="${assignmentFile.q6RevalMarks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q6MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q6RevalRemarks" value="${assignmentFile.q6RevalRemarks}" class="form-control" id="remarks6" maxlength="250" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q7Marks" path="q7RevalMarks">Recommendations, usefulness implementation scheme</form:label>
							<form:select id="q7Marks"  path="q7RevalMarks" itemValue="${assignmentFile.q7RevalMarks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q7MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q7RevalRemarks" value="${assignmentFile.q7RevalRemarks}" class="form-control" id="remarks7" maxlength="250" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q8Marks" path="q8RevalMarks">Linking of recommendations to the objectives</form:label>
							<form:select id="q8Marks"  path="q8RevalMarks" itemValue="${assignmentFile.q8RevalMarks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q8MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q8RevalRemarks" value="${assignmentFile.q8RevalRemarks}" class="form-control" id="remarks8" maxlength="250" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q9Marks" path="q9RevalMarks" >Bibliography</form:label>
							<form:select id="q9Marks"  path="q9RevalMarks" itemValue="${assignmentFile.q9RevalMarks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q9MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q9RevalRemarks" value="${assignmentFile.q9RevalRemarks}" class="form-control" id="remarks9" maxlength="250" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
					</div>
					<br>
						
						
					<div class="control-group">
				
					<c:url value="revaluateProject" var="evaluateAssignmentUrl">
					  <c:param name="year" value="${assignmentFile.year}" />
					  <c:param name="month" value="${assignmentFile.month}" />
					  <c:param name="subject" value="${assignmentFile.subject}" />
					  <c:param name="sapId" value="${assignmentFile.sapId}" />
					  <c:param name="facultyId" value="${assignmentFile.facultyId}" />
					</c:url>
					
					

					
					
					<div class="controls">
						<c:if test="${assignmentFile.revaluationCount < 3}">
							<button id="evaluateBtn" name="edit" class="btn btn-primary" onclick="return validateForm();" formaction="${evaluateAssignmentUrl}" class="form-control">
								Save Marks</button>
						</c:if>
						
						<c:if test="${assignmentFile.revaluationCount >= 3}">
							<button id="evaluateBtn" name="edit" class="btn btn-primary" disabled="true" formaction="${evaluateAssignmentUrl}" class="form-control">
							Save Marks</button>
						</c:if>
						
						
						<a id="cancel"  class="btn btn-danger" href="searchProjectToEvaluatePage"  > Back to List</a>
					</div>
					<br>
					<hr>
					
					</div>
				
				</div>
				
				<br>

				</div>				
				
			</div>
			
				
			</fieldset>
		</form:form>
		<br>
		
		
		</div>
		
	
	</section>

	
        
	  <jsp:include page="../footer.jsp" />
	  
	 
	 
	//This is to be checked only if assignment is not evaluated.
	<c:if test="${assignmentFile.evaluated ne 'Y'}">
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/js/vendor/screentime.js?id=1"></script>
	<script>

	var totalTime = 0;
	var flag = false;
	var reachedPageBottom = true;
	$('#evaluation').hide();
	$(window).load(function() {
		$.screentime({
			  fields: [
			    { selector: '#assignment',
			      name: 'Top'
			    }
			  ],
			  reportInterval: 1,
			  callback: function(data) {
			  $.each(data, function(key, val) {
			  totalTime += val;
			
			 if(totalTime > 1 && flag == false && reachedPageBottom){
					flag = true;
					$('#evaluation').show();
				}
			    });
			  }
		  
		});
	  
	
	});
	
	$("#score").keypress(function (e) {
	    if (String.fromCharCode(e.keyCode).match(/[^0-9]/g)) return false;
	});
	
	$( "#evalForm" ).validate({
	  rules: {
		  score: {
	      required: true,
	      max: 30,
	      min: 0,
	      digits:true
	    }
	  }
	});

	
	
</script>
</c:if>
<script>
	var attempts = ${assignmentFile.revaluationCount};
	if(attempts >= 3){
		$("#marksDiv :input").attr("disabled", true);
	}
</script>

<%}catch(Exception e){
	
	
}	%>


</body>
</html>
