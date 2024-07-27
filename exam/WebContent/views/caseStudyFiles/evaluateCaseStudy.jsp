<!DOCTYPE html>

<%@page import="com.nmims.beans.StudentExamBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Evaluate Case Study" name="title" />
</jsp:include>


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

		
		var q1Remarks = document.getElementById("remarks1").value;
		var q2Remarks = document.getElementById("remarks2").value;
		var q3Remarks = document.getElementById("remarks3").value;
		var q4Remarks = document.getElementById("remarks4").value;
		var q5Remarks = document.getElementById("remarks5").value;
		var q6Remarks = document.getElementById("remarks6").value;
	
		
		
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
	
	function validateRevaluationForm(){
		score = document.getElementById('revaluationScore').value;
		return confirm('Are you sure you want to save revaluation score as '+score);
	}
	
	
	function validateRevisitForm(){
		var score = document.getElementById('revisitScore').value;
		var remarks = document.getElementById('revisitRemarks').value;
		
		if(remarks.trim() == ''){
			alert('Please enter a remark for the score mentioned.');
			return false;
		}
	
		if(score.trim() == ''){
			alert('Please enter a Score for the score mentioned.');
			return false;
		}else if(score < 0 || score > 100){
			alert('Please enter a valid score.');
			return false;
		}
		
		return confirm('Are you sure you want to save revisit marks as '+score);
	}
	
	function setTotal(marksOption){
		
		var attempts = ${caseStudyFile.evaluationCount};
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



<body class="inside">


<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        
        <div class="row"><legend>Case Study : ${caseStudyFile.topic}</legend></div>
        
        
        <%@ include file="../messages.jsp"%>
		
		<form:form  action="submitAssignment" method="post" modelAttribute="caseStudyFile" id="evalForm">
			<fieldset>
			<div class="panel-body">
				<c:if test="${not empty caseStudyFile.previewPath}">
				<div class="col-md-12 column">
				<h2 align="center">Student Submitted File</h2>
				
				<div  id="assignment">
				<embed  align="middle" src="<spring:eval expression="@propertyConfigurer.getProperty('CASESTUDY_PREVIEW_PATH')" />${caseStudyFile.previewPath}?id=<%=Math.random() %>" 
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
								<form:input id="score" path="score" type="text" placeholder="Total Score" 
								class="form-control" value="${caseStudyFile.score}" readonly="true"/>
						</div>
						
						<div class="form-group">
								<label class="control-label" for="remarks">Overall Remarks</label>
								<form:textarea path="remarks" value="${caseStudyFile.remarks}" cols="47" id="remarks" required="required"/>
						</div>
						
						<div class="form-group">
						<form:select id="reason" path="reason"  class="form-control"  required="required" itemValue="${caseStudyFile.reason}">
							<form:option value="">Select Reason</form:option>
							<form:option value="Excellent">Excellent</form:option>
							<form:option value="Very Good">Very Good</form:option>
							<form:option value="Good">Good</form:option>
							<form:option value="Average">Average</form:option>
							<form:option value="Below Average">Below Average</form:option>
							<form:option value="Copy Case-Internet/Course Book">Copy Case (Internet/Course Book)</form:option>
							<form:option value="Copy Case-Other Student">Copy Case (Other student/s)</form:option>
							<form:option value="Wrong Answer"> Wrong Answer/s</form:option>
							<form:option value="Other topic File">Other topic File</form:option>
							<form:option value="Scanned/Handwritten Project">Scanned/Handwritten Project</form:option>
							<form:option value="Submitted Question paper">Only Questions written/Submitted Question paper</form:option>
							<form:option value="Blank Submission">Blank Submission</form:option>
							<form:option value="Corrupt file uploaded">Corrupt file uploaded</form:option>
						</form:select>
						</div>
					
					<hr>
					<h4 align="left" style="color: #c72127;">Enter Marks per Marking Criteria</h4>
					
						<div class="form-group">
								<form:label for="q1Marks" path="q1Marks">Problem Definition or Analytics Objective</form:label>
								<form:select id="q1Marks" path="q1Marks" itemValue="${caseStudyFile.q1Marks}" class="form-control" onchange="setTotal(this);"  required="required">
									<form:option value="">Select Score</form:option>
									<form:options items="${q1MarksOptionsList}"/>
								</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q1Remarks" value="${caseStudyFile.q1Remarks}" class="form-control" id="remarks1" maxlength="500" placeholder="Enter Remarks" required="required"/>
						</div>
						
						<hr/>
						
						
						
						<div class="form-group">
							<form:label for="q2Marks" path="q2Marks">Approach Description</form:label>
							<form:select id="q2Marks"  path="q2Marks" itemValue="${caseStudyFile.q2Marks}" class="form-control" onchange="setTotal(this);"  required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q2MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q2Remarks" value="${caseStudyFile.q2Remarks}" class="form-control" id="remarks2" maxlength="500" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q3Marks" path="q3Marks">Data Exploration: (Data Manipulation, Data Derivation, Consolidation, Preparation etc.) </form:label>
							<form:select id="q3Marks"  path="q3Marks" itemValue="${caseStudyFile.q3Marks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q3MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q3Remarks" value="${caseStudyFile.q3Remarks}" class="form-control" id="remarks3" maxlength="500" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q4Marks" path="q4Marks">Data Analysis</form:label>
							<form:select id="q4Marks"  path="q4Marks" itemValue="${caseStudyFile.q4Marks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q4MarksOptionsList}"/>
							</form:select>
						</div>
						<div class="form-group">
							<form:textarea path="q4Remarks" value="${caseStudyFile.q4Remarks}" class="form-control" id="remarks4" maxlength="500" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q5Marks" path="q5Marks">Results and Conclusions</form:label>
							<form:select id="q5Marks"  path="q5Marks" itemValue="${caseStudyFile.q5Marks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q5MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q5Remarks" value="${caseStudyFile.q5Remarks}" class="form-control" id="remarks5" maxlength="500" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
						
						<div class="form-group">
							<form:label for="q6Marks" path="q6Marks">Implications</form:label>
							<form:select id="q6Marks"  path="q6Marks" itemValue="${caseStudyFile.q6Marks}" class="form-control" onchange="setTotal(this);" required="required">
								<form:option value="">Select</form:option>
								<form:options items="${q6MarksOptionsList}"/>
							</form:select>
						</div>
						
						<div class="form-group">
							<form:textarea path="q6Remarks" value="${caseStudyFile.q6Remarks}" class="form-control" id="remarks6" maxlength="500" placeholder="Enter Remarks" required="required"/>
						</div>
						<hr/>
						
					</div>
					<br>
						
						
			<div class="control-group">
				
					<c:url value="evaluateCaseStudy" var="evaluateCaseStudyUrl">
					  <c:param name="batchYear" value="${caseStudyFile.batchYear}" />
					  <c:param name="batchMonth" value="${caseStudyFile.batchMonth}" />
					  <c:param name="topic" value="${caseStudyFile.topic}" />
					  <c:param name="sapid" value="${caseStudyFile.sapid}" />
					  <c:param name="facultyId" value="${caseStudyFile.facultyId}" />
					</c:url>
					
					
					<div class="controls">
						<c:if test="${caseStudyFile.evaluationCount < 3}">
							<button id="evaluateBtn" name="edit" class="btn btn-primary" onclick="return validateForm();" formaction="${evaluateCaseStudyUrl}" class="form-control">
								Save Marks</button>
						</c:if>
						
						<c:if test="${caseStudyFile.evaluationCount >= 3}">
							<button id="evaluateBtn" name="edit" class="btn btn-primary" disabled="true" formaction="${evaluateCaseStudyUrl}" class="form-control">
							Save Marks</button>
						</c:if>
						
					
						
						<a id="cancel"  class="btn btn-danger" href="searchAssignedCaseStudyFilesForm"  > Back</a>
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
	<c:if test="${caseStudyFile.evaluated ne 'Y'}">
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
	var attempts = ${caseStudyFile.evaluationCount};
	if(attempts >= 3){
		$("#marksDiv :input").attr("disabled", true);
	}
</script>

</body>
</html>
 