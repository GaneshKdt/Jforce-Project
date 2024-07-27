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
        <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Submit Assignment" name="title" />
</jsp:include>


<script type="text/javascript">
	
	function checkIfScoreIs0(){
		var score = document.getElementById("score").value;
		if(score == 0){
			document.getElementById("reason").disabled = false;
		}else{
			document.getElementById("reason").disabled = true;
		}
	}
	
	function validateForm(){
		score = document.getElementById('score').value;
		var remarks = document.getElementById('remarks').value;
		var reason = document.getElementById('reason').value;
		var program = '${assignmentFile.program}';
		
		var MAX_SCORE = 30;
		if(program == 'ACBM'){
			MAX_SCORE = 40;
		}
		
		if(score < 10 || score == MAX_SCORE){
			if(remarks.trim() == ''){
				alert('Please enter a remark for the score mentioned.');
				return false;
			}
		}
		if(score ==0 && reason.trim() == ''){
			alert('Please enter a reason for the score mentioned.');
			
			return false;	
		}
		
		/* if(score < 10 /* || score == MAX_SCORE ){
			if(reason.trim() == ''){
				alert('Please enter a reason for the score mentioned.');
				return false;
			}
		} */
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
		var q10Marks = parseInt(document.getElementById("q10Marks").value);
		
		var q1Remarks = document.getElementById("remarks1").value;
		var q2Remarks = document.getElementById("remarks2").value;
		var q3Remarks = document.getElementById("remarks3").value;
		var q4Remarks = document.getElementById("remarks4").value;
		var q5Remarks = document.getElementById("remarks5").value;
		var q6Remarks = document.getElementById("remarks6").value;
		var q7Remarks = document.getElementById("remarks7").value;
		var q8Remarks = document.getElementById("remarks8").value;
		var q9Remarks = document.getElementById("remarks9").value;
		var q10Remarks = document.getElementById("remarks10").value;
		
		
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
		if(!isNaN(q10Marks)){
			total += parseInt(q10Marks);
			if(q10Remarks.trim() == ""){
				remarksAlertMessage = remarksAlertMessage + "Please enter remarks for Q.10 \n";
			}
		}
		
		if(total > MAX_SCORE){
			alert('Total cannot be greater than '+MAX_SCORE);
			marksOption.value = "";
			return false;
		}
		
		if(remarksAlertMessage != ""){
			alert(remarksAlertMessage);
			return false;
		}
		
		return confirm('Are you sure you want to save marks as '+score);
	}
	
	function validateRevaluationForm(){
		score = document.getElementById('revaluationScore').value;
		return confirm('Are you sure you want to save revaluation score as '+score);
	}

	
	function validateRevisitForm(){
		var score = document.getElementById('revisitScore').value;
		var remarks = document.getElementById('revisitRemarks').value;
		var reason = document.getElementById('reason').value;
		
		if(remarks.trim() == ''){
			alert('Please enter a remark for the score mentioned.');
			return false;
		}
	
		if(score.trim() == ''){
			alert('Please enter a Score for the score mentioned.');
			return false;
		}else if(score < 0 || score > 30){
			alert('Please enter a valid score.');
			return false;
		}
		
		return confirm('Are you sure you want to save revisit marks as '+score);
	}
	
	function setTotal(marksOption){
		
		var attempts = ${assignmentFile.evaluationCount};
		if(attempts >= 3){
			return;
		}
		
		var program = '${assignmentFile.program}';
		
		var MAX_SCORE = 30;
		if(program == 'ACBM'){
			MAX_SCORE = 40;
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
		var q10Marks = parseInt(document.getElementById("q10Marks").value);
		
		
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
		if(!isNaN(q10Marks)){
			total += parseInt(q10Marks);
		}
		
		if(total > MAX_SCORE){
			alert('Total cannot be greater than ' + MAX_SCORE);
			marksOption.value = "";
			return false;
		}
		
		if(remarksAlertMessage != ""){
			alert(remarksAlertMessage);
			return false;
		}
		document.getElementById("score").value = total;
		if(total == 0){
			document.getElementById("reason").disabled = false;
		}else{
			document.getElementById("reason").disabled = true;
		}
	}

</script>
<%try{ %>
<%
	String sapId = (String)session.getAttribute("userId");
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String studentName = "";
	//String weightage = (String)request.getAttribute("weightage");
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
	
	
%>

<body class="inside" onload="checkIfScoreIs0();">


<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        
        <div class="row"><legend>${assignmentFile.subject}</legend></div>
        
        
        <%@ include file="messages.jsp"%>
		
		<form:form  action="submitAssignment" method="post" modelAttribute="assignmentFile" id="evalForm">
			<fieldset>
			<div class="panel-body">
				<c:if test="${not empty assignmentFile.previewPath}">
				<div class="col-md-12 column">
				<h2 align="center">Student Submitted File</h2>
				
				<div  id="assignment">
				<a target="_blank" href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}?id=<%=Math.random() %>">Open In New Tab</a>
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
								<label class="control-label" for="score">Weightage :${assignmentFile.weightage}  - (Total Score Calculated based on Marks for individual questions)</label>
								<form:input id="score" path="score" type="text" placeholder="Assignment Score" 
								class="form-control" value="${assignmentFile.score}" readonly="true"/>
						</div>
						
						<div class="form-group">
								<label class="control-label" for="remarks">Overall Remarks</label>
								<form:textarea path="remarks" value="${assignmentFile.remarks}" cols="47" id="remarks" required="required"/>
						</div>
						<form:hidden id="weightage" path="weightage"/>
						
						<div class="form-group">
						<form:select id="reason" path="reason" disabled="true" class="form-control"  itemValue="${assignmentFile.reason}">
							<form:option value="">Select Reason</form:option>
							<!--<form:option value="Excellent">Excellent</form:option>
							<form:option value="Very Good">Very Good</form:option>
							<form:option value="Good">Good</form:option>
							<form:option value="Average">Average</form:option>
							<form:option value="Below Average">Below Average</form:option>-->
							<%-- <form:option value="Copy Case-Internet/Course Book">Copy Case (Internet/Course Book)</form:option>
							<form:option value="Copy Case-Other Student">Copy Case (Other student/s)</form:option> --%>
							<form:option value="Wrong Answer"> Wrong Answer/s</form:option>
							<form:option value="Other subject Assignment">Other subject Assignment</form:option>
							<form:option value="Scanned/Handwritten assignment">Scanned/Handwritten assignment</form:option>
							<form:option value="Only Questions written">Only Questions written/Question Paper Uploaded</form:option>
							<form:option value="Blank Assignment">Blank Assignment</form:option>
							<form:option value="Corrupt file uploaded">Corrupt file uploaded</form:option>
							<form:option value="Unfair Means">Unfair Means</form:option>
						</form:select>
						</div>
					
					<hr>
					<h4 align="left" style="color: #c72127;">Enter Individual Questions Marks</h4>
					
					
						<table style="border-spacing: 15px;">
						<thead>
						<tr>
							<th># </th>
							<th align="center">&nbsp;Marks</th>
							<th>&nbsp;</th>
							<th>Remarks</th>
						</tr>
						</thead>
						
						<tr>
							<td>Q.1 &nbsp;&nbsp;</td>
							<td>
								<form:select id="q1Marks" path="q1Marks" itemValue="${assignmentFile.q1Marks}" class="form-control" onchange="setTotal(this);">
									<form:option value="">Select</form:option>
									<form:options items="${marksOptionsList}"/>
								</form:select>
							</td>
							<td><span style="width:10px;"></span></td>
							<td>
								<form:textarea path="q1Remarks" value="${assignmentFile.q1Remarks}" class="form-control" id="remarks1" maxlength="2000"/>
							</td>
						</tr>
						
						<tr>
							<td>Q.2 &nbsp;&nbsp;</td>
							<td>
								<form:select id="q2Marks"  path="q2Marks" itemValue="${assignmentFile.q2Marks}" class="form-control" onchange="setTotal(this);">
									<form:option value="">Select</form:option>
									<form:options items="${marksOptionsList}"/>
								</form:select>
							</td>
							<td><span style="width:10px;"></span></td>
							<td>
								<form:textarea path="q2Remarks" value="${assignmentFile.q2Remarks}" class="form-control" id="remarks2" maxlength="2000"/>
							</td>
						</tr>
						
						<tr>
							<td>Q.3 &nbsp;&nbsp;</td>
							<td>
								<form:select id="q3Marks"  path="q3Marks" itemValue="${assignmentFile.q3Marks}" class="form-control" onchange="setTotal(this);">
									<form:option value="">Select</form:option>
									<form:options items="${marksOptionsList}"/>
								</form:select>
							</td>
							<td><span style="width:10px;"></span></td>
							<td>
								<form:textarea path="q3Remarks" value="${assignmentFile.q3Remarks}" class="form-control" id="remarks3" maxlength="2000"/>
							</td>
						</tr>
						
						<tr>
							<td>Q.4 &nbsp;&nbsp;</td>
							<td>
								<form:select id="q4Marks"  path="q4Marks" itemValue="${assignmentFile.q4Marks}" class="form-control" onchange="setTotal(this);">
									<form:option value="">Select</form:option>
									<form:options items="${marksOptionsList}"/>
								</form:select>
							</td>
							<td><span style="width:10px;"></span></td>
							<td>
								<form:textarea path="q4Remarks" value="${assignmentFile.q4Remarks}" class="form-control" id="remarks4" maxlength="2000"/>
							</td>
						</tr>
						
						<tr>
							<td>Q.5 &nbsp;&nbsp;</td>
							<td>
								<form:select id="q5Marks"  path="q5Marks" itemValue="${assignmentFile.q5Marks}" class="form-control" onchange="setTotal(this);">
									<form:option value="">Select</form:option>
									<form:options items="${marksOptionsList}"/>
								</form:select>
							</td>
							<td><span style="width:10px;"></span></td>
							<td>
								<form:textarea path="q5Remarks" value="${assignmentFile.q5Remarks}" class="form-control" id="remarks5" maxlength="2000"/>
							</td>
						</tr>
						
						<tr>
							<td>Q.6 &nbsp;&nbsp;</td>
							<td>
								<form:select id="q6Marks"  path="q6Marks" itemValue="${assignmentFile.q6Marks}" class="form-control" onchange="setTotal(this);">
									<form:option value="">Select</form:option>
									<form:options items="${marksOptionsList}"/>
								</form:select>
							</td>
							<td><span style="width:10px;"></span></td>
							<td>
								<form:textarea path="q6Remarks" value="${assignmentFile.q6Remarks}" class="form-control" id="remarks6" maxlength="2000"/>
							</td>
						</tr>
						
						<tr>
							<td>Q.7 &nbsp;&nbsp;</td>
							<td>
								<form:select id="q7Marks"  path="q7Marks" itemValue="${assignmentFile.q7Marks}" class="form-control" onchange="setTotal(this);">
									<form:option value="">Select</form:option>
									<form:options items="${marksOptionsList}"/>
								</form:select>
							</td>
							<td><span style="width:10px;"></span></td>
							<td>
								<form:textarea path="q7Remarks" value="${assignmentFile.q7Remarks}" class="form-control" id="remarks7" maxlength="2000"/>
							</td>
						</tr>
						
						<tr>
							<td>Q.8 &nbsp;&nbsp;</td>
							<td>
								<form:select id="q8Marks"  path="q8Marks" itemValue="${assignmentFile.q8Marks}" class="form-control" onchange="setTotal(this);">
									<form:option value="">Select</form:option>
									<form:options items="${marksOptionsList}"/>
								</form:select>
							</td>
							<td><span style="width:10px;"></span></td>
							<td>
								<form:textarea path="q8Remarks" value="${assignmentFile.q8Remarks}" class="form-control" id="remarks8" maxlength="2000"/>
							</td>
						</tr>
						
						<tr>
							<td>Q.9 &nbsp;&nbsp;</td>
							<td>
								<form:select id="q9Marks"  path="q9Marks" itemValue="${assignmentFile.q9Marks}" class="form-control" onchange="setTotal(this);">
									<form:option value="">Select</form:option>
									<form:options items="${marksOptionsList}"/>
								</form:select>
							</td>
							<td><span style="width:10px;"></span></td>
							<td>
								<form:textarea path="q9Remarks" value="${assignmentFile.q9Remarks}" class="form-control" id="remarks9" maxlength="2000"/>
							</td>
						</tr>
						
						<tr>
							<td>Q.10 &nbsp;&nbsp;</td>
							<td>
								<form:select id="q10Marks"  path="q10Marks" itemValue="${assignmentFile.q10Marks}" class="form-control" onchange="setTotal(this);">
									<form:option value="">Select</form:option>
									<form:options items="${marksOptionsList}"/>
								</form:select>
							</td>
							<td><span style="width:10px;"></span></td>
							<td>
								<form:textarea path="q10Remarks" value="${assignmentFile.q10Remarks}" class="form-control" id="remarks10" maxlength="2000"/>
							</td>
						</tr>
						
						
						</table>
					</div>
					<br>
						
						
					<div class="control-group">
				
					<c:url value="evaluateAssignment" var="evaluateAssignmentUrl">
					  <c:param name="year" value="${assignmentFile.year}" />
					  <c:param name="month" value="${assignmentFile.month}" />
					  <c:param name="subject" value="${assignmentFile.subject}" />
					  <c:param name="sapId" value="${assignmentFile.sapId}" />
					  <c:param name="facultyId" value="${assignmentFile.facultyId}" />
					</c:url>
					
					<c:url value="reEvaluateAssignment" var="reEvaluateAssignmentUrl">
					  <c:param name="year" value="${assignmentFile.year}" />
					  <c:param name="month" value="${assignmentFile.month}" />
					  <c:param name="subject" value="${assignmentFile.subject}" />
					  <c:param name="sapId" value="${assignmentFile.sapId}" />
					  <c:param name="facultyId" value="${assignmentFile.facultyId}" />
					</c:url>
					
					<c:url value="revisitAssignment" var="revisitAssignmentUrl">
					  <c:param name="year" value="${assignmentFile.year}" />
					  <c:param name="month" value="${assignmentFile.month}" />
					  <c:param name="subject" value="${assignmentFile.subject}" />
					  <c:param name="sapId" value="${assignmentFile.sapId}" />
					  <c:param name="facultyId" value="${assignmentFile.facultyId}" />
					</c:url>
			
					<%if( roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Exam Admin") != -1){ %>
						<c:if test="${assignmentFile.markedForRevaluation == 'Y'}">
							<div class="form-group">
								<label class="control-label" for="revaluationScore">Assignment Revaluation Score(Please enter same value if it has not changed)</label>
								<form:input id="revaluationScore" path="revaluationScore" type="text" placeholder="Assignment Reevaluation Score" class="form-control" value="${assignmentFile.revaluationScore}"/>
							</div>
							
							<div class="form-group">
									<label class="control-label" for="revaluationRemarks">Revaluation Remarks</label>
									<form:textarea path="revaluationRemarks" value="${assignmentFile.revaluationRemarks}" cols="47" id="revaluationRemarks"/>
							</div>
						</c:if>
					<%} %>
					
					
					<div class="controls">
						<c:if test="${assignmentFile.evaluationCount < 3}">
							<button id="evaluateBtn" name="edit" class="btn btn-primary" onclick="return validateForm();" formaction="${evaluateAssignmentUrl}" class="form-control">
								Save Marks</button>
						</c:if>
						
						<c:if test="${assignmentFile.evaluationCount >= 3}">
							<button id="evaluateBtn" name="edit" class="btn btn-primary" disabled="true" formaction="${evaluateAssignmentUrl}" class="form-control">
							Save Marks</button>
						</c:if>
						
						<%if( roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Exam Admin") != -1){ %>
							<%-- <c:if test="${assignmentFile.markedForRevaluation == 'Y'}">
								<button id="evaluateBtn" name="edit" class="btn btn-primary" onclick="return validateRevaluationForm();" formaction="${reEvaluateAssignmentUrl}" class="form-control">
									Save Revaluation Marks</button>
							</c:if> --%>
						<%} %>
						
						<a id="cancel"  class="btn btn-danger" href="searchAssignmentToEvaluatePage"  > Back to List</a>
					</div>
					<br>
					<hr>
					<%-- <!-- Section for Revisit Start -->
						<div class="form-group">
							<label class="control-label" for="revisitScore">Revisit Score(Please enter same value if it has not changed)</label>
							<form:input id="revisitScore" path="revisitScore" type="text" placeholder="Assignment Revisit Score" class="form-control" value="${assignmentFile.revisitScore}"/>
						</div>
						
						<div class="form-group">
								<label class="control-label" for="revisitRemarks">Revisit Remarks</label>
								<form:textarea path="revisitRemarks" value="${assignmentFile.revisitRemarks}" cols="47" id="revisitRemarks"/>
						</div>
						
						
						<div class="controls">
						<button id="evaluateBtn" name="edit" class="btn btn-primary" onclick="return validateRevisitForm();" 
						formaction="${revisitAssignmentUrl}" class="form-control">
								Save Revisit Marks</button>
						
						<a id="cancel"  class="btn btn-danger" href="searchAssignmentToEvaluatePage" > Back to List</a>
						</div>
					<!-- Section for Revisit End--> --%>
					</div>
				
				</div>
				
				<br>
				<div class="titleContainer titleContainerResultIns">
					<p>Assignment Question File</p> 
						<h3><a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FILES_PATH')" />${assignmentFile.questionFilePreviewPath}">Download</a></h3>
				</div> 

				</div>				
				
			</div>
			
				
			</fieldset>
		</form:form>
		<br>
		
		
		</div>
		
	
	</section>

	
        
	  <jsp:include page="footer.jsp" />
	  
	 
	 
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
	var attempts = ${assignmentFile.evaluationCount};
	if(attempts >= 3){
		$("#marksDiv :input").attr("disabled", true);
	}
</script>
<%}catch(Exception e){}%>
}
</body>
</html>
