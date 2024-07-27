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
	<jsp:param value="Submit Assignment" name="title" />
</jsp:include>


<script language="JavaScript">
	function validateForm( attemptsUsed, maxAttempts) {
		
		var confirmationList = document.getElementsByName('confirmation');
		var allSelected = true;
		for(var i = 0; i < confirmationList.length; ++i)
		{
		    if(!confirmationList[i].checked){
		    	allSelected = false;
		    	break;
		    }
		}
		if(!allSelected){
			alert("Please confirm against all options in Checklist before submitting assignment. You have "+ (maxAttempts - attemptsUsed) + " attempts left.");
			return false;
		}
		
		if((attemptsUsed + 1) < maxAttempts){
			return confirm('This is your attempt number '+ (attemptsUsed + 1) + '. Are you sure you want to submit assignment?');
		}else{
			return confirm('This is your last attempt. Are you sure you want to submit assignment?');
		}
		
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
		
		<form:form  action="submitResitAssignment" method="post" modelAttribute="assignmentFile" enctype="multipart/form-data">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-6 column">

				<h2>Time left to submit Assignment</h2>
				<div id="DateCountdown" data-date="${assignmentFile.endDate}" style="width: 100%;"></div>
				
				<form  action="editCompany" method="post">
				<jsp:useBean id="now" class="java.util.Date" />
				<fmt:parseDate value="${assignmentFile.startDate}" var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
				<fmt:parseDate value="${assignmentFile.endDate}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
				
				<p 
				
					<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149; color:#fff; padding:15px; font-size:16px;"</c:if>
					<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;  padding:15px; font-size:16px;"</c:if>
				
				><b>Status:</b> ${assignmentFile.status}</p>


				<div class="titleContainer titleContainerResultIns">
					<p>Max Attempts Permitted</p>
					<h3>${maxAttempts}</h3>
				</div>

				<div class="titleContainer titleContainerResultIns">
					<p>Attempts Used</p>
					<h3>${assignmentFile.attempts}</h3>
				</div>

				<div class="titleContainer titleContainerResultIns">
					<p>Submission Start Date & Time</p>
					<h3><fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${startDate}" timeStyle="full"/></h3>
				</div>
				
				<div class="titleContainer titleContainerResultIns">
					<p>Submission End Date & Time</p>
					<h3><fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${endDate}" timeStyle="full"/></h3>
				</div>
				
				<div class="titleContainer titleContainerResultIns">
					<p>Assignment Question File</p>
						<h3><a href="downloadStudentAssignmentFile?filePath=${assignmentFile.filePath}&subject=${assignmentFile.subject}">Download</a></h3>
				</div>

				
				
				<br>
				<h5><b>Note:</b> The file size should not exceed 5MB.<br>Please do not refresh screen after submitting assignment, as it will consume additional attempt</h5>	
				<h5><b>Please use Chrome to avoid any Browser compatibility issue.</b></h5>				
				
				
				
				<div class="form-group">
					<form:label path="">Please attach your answer file in .pdf format</form:label>
					<a href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format" target="_blank">How to convert word file <i class="fa-regular fa-file-word fa-lg"></i> into pdf <i class="fa-solid fa-file-pdf fa-lg"></i> file?</a>
					<form:input path="fileData" type="file" itemValue="${assignmentFile.fileData}" class="form-control" required="required" />  
				</div>
				
				
				<b>Please confirm below check points before you submit assignment:</b>
				<div class="checkbox">
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>Selected subject assignment file is attached</span></label> <br>
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>Attached file contains only the selected subject assignment</span></label> <br>
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>Attached file size is below 5MB</span></label> <br>
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>Attached file is virus free</span></label><br>
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I have read the Internal Assignment Guidelines</span></label>	<br>
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I have prepared Assignment & not copied</span></label>	<br>
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I want to proceed to submit the attached file</span></label>	<br>
				</div>

						
				
				<div class="control-group">
				
				<c:url value="submitResitAssignment" var="submitAssignmentUrl">
				  <c:param name="year" value="${assignmentFile.year}" />
				  <c:param name="month" value="${assignmentFile.month}" />
				  <c:param name="subject" value="${assignmentFile.subject}" />
				  <c:param name="status" value="${assignmentFile.status}" />
				  <c:param name="startDate" value="${assignmentFile.startDate}" />
				  <c:param name="endDate" value="${assignmentFile.endDate}" />
				</c:url>
		
				
					<div class="controls">
						<c:if test="${endDate gt now}">
							<c:if test="${assignmentFile.attempts < maxAttempts}">
								<button id="edit" name="edit" class="btn btn-primary" onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});" 
								formaction="${submitAssignmentUrl}" class="form-control">
								<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>Submit Assignment</button>
							</c:if>
						</c:if>
						
						
						
						<%-- 
						<!-- Added temporarily to give access after end date. Comment and Uncomment as Needed : START-->
						<%
						if(sapId.equals("77214001071") || sapId.equals("77115002309") ){
						%>
						<c:if test="${assignmentFile.attempts < maxAttempts}">
							<button id="edit" name="edit" class="btn btn-primary" onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});" 
							formaction="${submitAssignmentUrl}" class="form-control">
							<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>Submit Assignment</button>
						</c:if>
						<%}%>
						<!--  Added temporarily to give access after end date. Comment and Uncomment as Needed : End-->
						
						 --%>
						
						
						
						<c:if test="${assignmentFile.attempts >= maxAttempts}">
						<button id="edit" name="edit" class="btn btn-primary" formaction="#" class="form-control" disabled="disabled">Max Attempts Reached</button>
						
						</c:if>
						
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="viewResitAssignmentsForm" formnovalidate="formnovalidate" class="form-control"> Back to List</button>
					
					</div>
				</div>
				</form>
				
				
				</div>
			
			
			
				<c:if test="${not empty assignmentFile.previewPath}">
				<div class="col-md-12 column">
				<h2 align="center">Preview of Submitted File</h2>
				<embed align="middle" src="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}?id=<%=Math.random() %>" 
				width = "100%" height="820px">
				</div>
				</c:if>
				
				
			</div>
			
				
			</fieldset>
		</form:form>
		<br>
		<div class="row clearfix">
			
			<div class="col-md-6 column">
				
			</div>
		</div>
		
		</div>
		
	
	</section>

	
        
	  <jsp:include page="../footer.jsp" />
	  
	  <script>
           $("#DateCountdown").TimeCircles({
        	count_past_zero: false,
			"animation": "ticks",
			"bg_width": 0.9,
			"fg_width": 0.056666666666666664,
			"circle_bg_color": "#60686F",
			"time": {
				"Days": {
					"text": "Days",
					"color": "#FFCC66",
					"show": true
				},
				"Hours": {
					"text": "Hours",
					"color": "#99CCFF",
					"show": true
				},
				"Minutes": {
					"text": "Minutes",
					"color": "#70B670",
					"show": true
				},
				"Seconds": {
					"text": "Seconds",
					"color": "#FF9999",
					"show": true
				}
			}
		});

        </script>   


</body>
</html>
