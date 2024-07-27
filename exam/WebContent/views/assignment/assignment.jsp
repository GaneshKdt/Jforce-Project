<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentBean"%>
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
	String subject = (String)request.getAttribute("subject");
	StudentBean student = (StudentBean)session.getAttribute("student");
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
	
	
	ArrayList<String> timeExtendedStudentIdSubjectList = new ArrayList();
	//Comment or uncomment as needed for allowing submission after due date
	/*
	timeExtendedStudentIdSubjectList.add("77215001549" + "Business Economics");
	timeExtendedStudentIdSubjectList.add("77215001468" + "Business Economics");
	timeExtendedStudentIdSubjectList.add("77115000342" + "Corporate Social Responsibility");
	timeExtendedStudentIdSubjectList.add("77115000891" + "Financial Accounting & Analysis");
	timeExtendedStudentIdSubjectList.add("77114001386" + "Marketing Management");
	timeExtendedStudentIdSubjectList.add("77114001386" + "Taxation- Direct and Indirect");
	timeExtendedStudentIdSubjectList.add("77214002069" + "Total Quality Management");
	timeExtendedStudentIdSubjectList.add("77214002069" + "Logistics Management");
	*/
%>

<body class="inside">


<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        
        <div class="row"><legend>${assignmentFile.subject}</legend></div>
        
        
        <%@ include file="../messages.jsp"%>
		
		<form:form  action="submitAssignment" method="post" modelAttribute="assignmentFile" enctype="multipart/form-data">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-6 column">

				<h2>Time left to submit Assignment</h2>
				<div id="DateCountdown" data-date="${assignmentFile.endDate}" style="width: 100%;"></div>
				
				<form  action="editCompany" method="post">
				<jsp:useBean id="now" class="java.util.Date" />
				<fmt:parseDate value="${assignmentFile.startDate}" var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
				<fmt:parseDate value="${assignmentFile.endDate}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
				
				<c:if test="${assignmentPaymentPending == 'true' }">
						<div style="background-color: #DF3818;color:#fff;  padding:15px; font-size:16px;">Assignment Submission Fees Pending</div>
				</c:if>	
				
				<c:if test="${assignmentPaymentPending != 'true' }">
					<p 
					
						<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149; color:#fff; padding:15px; font-size:16px;"</c:if>
						<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;  padding:15px; font-size:16px;"</c:if>
					
					><b>Status:</b> ${assignmentFile.status}</p>
				</c:if>	

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
					<a href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format" target="_blank">How to convert word file <i class="fa fa-file-word-o fa-lg"></i> into pdf <i class="fa fa-file-pdf-o fa-lg"></i> file?</a>
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
				
				<c:url value="submitAssignment" var="submitAssignmentUrl">
				  <c:param name="year" value="${assignmentFile.year}" />
				  <c:param name="month" value="${assignmentFile.month}" />
				  <c:param name="subject" value="${assignmentFile.subject}" />
				  <c:param name="status" value="${assignmentFile.status}" />
				  <c:param name="startDate" value="${assignmentFile.startDate}" />
				  <c:param name="endDate" value="${assignmentFile.endDate}" />
				</c:url>
		
				
					<div class="controls">
					
					<c:if test="${assignmentPaymentPending != 'true' }">
							<c:if test="${endDate gt now}">
								<c:if test="${assignmentFile.attempts < maxAttempts}">
									<button id="edit" name="edit" class="btn btn-primary" onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});" 
									formaction="${submitAssignmentUrl}" class="form-control">
									<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>Submit Assignment</button>
								</c:if>
							</c:if>
							
							
							<!-- Added temporarily to give access after end date. Comment and Uncomment as Needed : START-->
							<%
							if(timeExtendedStudentIdSubjectList.contains(sapId + subject)){
							%>
							<c:if test="${assignmentFile.attempts < maxAttempts}">
								<button id="edit" name="edit" class="btn btn-primary" onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});" 
								formaction="${submitAssignmentUrl}" class="form-control">
								<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>Submit Assignment</button>
							</c:if>
							<%}%>
							<!--  Added temporarily to give access after end date. Comment and Uncomment as Needed : End-->
							
					</c:if>	
						
					<c:if test="${assignmentPaymentPending == 'true' }">
						<a  href="/exam/selectAssignmentPaymentSubjectsForm" class="btn btn-primary" >Proceed to Payment</a>
					</c:if>	
						
						<c:if test="${assignmentFile.attempts >= maxAttempts}">
						<button id="edit" name="edit" class="btn btn-primary" formaction="#" class="form-control" disabled="disabled">Max Attempts Reached</button>
						
						</c:if>
						
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="viewAssignmentsForm" formnovalidate="formnovalidate" class="form-control"> Back to List</button>
					
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
 --%>


<!DOCTYPE html>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%
	String fromCourseHomePage = request.getParameter("fromCourseHomePage");
	String sapId = (String)session.getAttribute("userId");
	String subject = (String)request.getAttribute("subject");
	StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
	ArrayList<String> timeExtendedStudentIdSubjectList = (ArrayList<String>)session.getAttribute("timeExtendedStudentIdSubjectList");
	
	if(timeExtendedStudentIdSubjectList == null) {
		timeExtendedStudentIdSubjectList = new ArrayList<String>();
	}
	//ArrayList<String> timeExtendedStudentIdSubjectList = new ArrayList();
	//Comment or uncomment as needed for allowing submission after due date

	/* 
	timeExtendedStudentIdSubjectList.add("77116001005" + "Corporate Social Responsibility");
	timeExtendedStudentIdSubjectList.add("77116001005" + "Information Systems for Managers");
	
	timeExtendedStudentIdSubjectList.add("77115001807" + "Business: Ethics, Governance & Risk");
	
	timeExtendedStudentIdSubjectList.add("77216493758" + "Business Communication and Etiquette");
	timeExtendedStudentIdSubjectList.add("77216493758" + "Integrated Marketing Communications");
	timeExtendedStudentIdSubjectList.add("77216493758" + "Mass Communication");
	timeExtendedStudentIdSubjectList.add("77216493758" + "Public Relations Theory and Practice");
	timeExtendedStudentIdSubjectList.add("77216493758" + "Written and Oral Communication");
	
	timeExtendedStudentIdSubjectList.add("77215001725" + "Industrial Relations & Labour Laws");
	
	timeExtendedStudentIdSubjectList.add("77215001052" + "Consumer Behaviour");
	timeExtendedStudentIdSubjectList.add("77215001052" + "Corporate Finance");
	timeExtendedStudentIdSubjectList.add("77215001052" + "International Business");
	timeExtendedStudentIdSubjectList.add("77215001052" + "Operations Management");
	timeExtendedStudentIdSubjectList.add("77215001052" + "Organisational Theory, Structure and Design");
	timeExtendedStudentIdSubjectList.add("77215001052" + " Taxation- Direct and Indirect");
	
	timeExtendedStudentIdSubjectList.add("77216649635" + "Business Economics");
	timeExtendedStudentIdSubjectList.add("77216649635" + "Corporate Social Responsibility");
	timeExtendedStudentIdSubjectList.add("77216649635" + "Information Systems for Managers");
	timeExtendedStudentIdSubjectList.add("77216649635" + "Management Theory and Practice");
	timeExtendedStudentIdSubjectList.add("77216649635" + "Organisational Behaviour");
	
	timeExtendedStudentIdSubjectList.add("77216100409" + "Business Communication and Etiquette");
	timeExtendedStudentIdSubjectList.add("77216100409" + "Business Economics");
	timeExtendedStudentIdSubjectList.add("77216100409" + "Corporate Social Responsibility");
	timeExtendedStudentIdSubjectList.add("77216100409" + "Information Systems for Managers");
	timeExtendedStudentIdSubjectList.add("77216100409" + "Management Theory and Practice");
	timeExtendedStudentIdSubjectList.add("77216100409" + "Organisational Behaviour");
	
	timeExtendedStudentIdSubjectList.add("77116002619" + "Essentials of HRM");
	timeExtendedStudentIdSubjectList.add("77116002619" + "Marketing Management");
	timeExtendedStudentIdSubjectList.add("77116002619" + "Strategic Management");
	
	timeExtendedStudentIdSubjectList.add("77216471034" + "Business Communication and Etiquette");
	timeExtendedStudentIdSubjectList.add("77215001964" + "Strategic Cost Management");
	timeExtendedStudentIdSubjectList.add("77115002666" + "Corporate Finance");
	
	timeExtendedStudentIdSubjectList.add("77216291224" + "Business Communication and Etiquette");
	timeExtendedStudentIdSubjectList.add("77216291224" + "Business Economics");
	timeExtendedStudentIdSubjectList.add("77216291224" + "Information Systems for Managers");
	timeExtendedStudentIdSubjectList.add("77216291224" + "Management Theory and Practice");
	timeExtendedStudentIdSubjectList.add("77216291224" + "Organisational Behaviour");
	
	timeExtendedStudentIdSubjectList.add("77116000216" + "Business Law");
	timeExtendedStudentIdSubjectList.add("77116000216" + "Business Statistics");
	timeExtendedStudentIdSubjectList.add("77116000216" + "Essentials of HRM");
	timeExtendedStudentIdSubjectList.add("77116000216" + "Financial Accounting & Analysis");
	timeExtendedStudentIdSubjectList.add("77116000216" + "Marketing Management");
	timeExtendedStudentIdSubjectList.add("77116000216" + "Strategic Management");

	timeExtendedStudentIdSubjectList.add("77215000239" + "Brand Management"); 
	timeExtendedStudentIdSubjectList.add("77214001474" + "Business Statistics");
	timeExtendedStudentIdSubjectList.add("77214001474" + "Essentials of HRM");
	timeExtendedStudentIdSubjectList.add("77214001474" + "Financial Accounting & Analysis");
	timeExtendedStudentIdSubjectList.add("77214001474" + "Marketing Management");
	
	timeExtendedStudentIdSubjectList.add("77216819787" + "Management Theory and Practice");
	timeExtendedStudentIdSubjectList.add("77116001005" + "Marketing Strategy");
	
	timeExtendedStudentIdSubjectList.add("77216471034" + "Business Economics");
	timeExtendedStudentIdSubjectList.add("77216471034" + "Corporate Social Responsibility");
	timeExtendedStudentIdSubjectList.add("77216471034" + "Information Systems for Managers");
	timeExtendedStudentIdSubjectList.add("77216471034" + "Management Theory and Practice");
	timeExtendedStudentIdSubjectList.add("77216471034" + "Organisational Behaviour");
	
	timeExtendedStudentIdSubjectList.add("77116004094" + "Project Management");
	timeExtendedStudentIdSubjectList.add("77116004094" + "Sales Management");
	timeExtendedStudentIdSubjectList.add("77116004094" + "Services Marketing"); */
%>

<c:url value="student/viewCourseHomePage" var="courseUrl">
	<c:param name="subject" value="${assignmentFile.subject}" />
</c:url>

<html lang="en">


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Submit Assignment" name="title" />
</jsp:include>

<style>
	.has-error {
		margin-bottom: 1em;
	}
	.help-block {
		display: none;
	    font-size: 15px;
	    font-weight: 600;
	    margin-top: 0.65em;
	}
	
	
</style>
<script language="JavaScript">
		function validateForm( attemptsUsed, maxAttempts) {
			
			const assignmentDocumentFile = document.getElementById("assignmentDocument").files[0];
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
			else if(!validateFileType(assignmentDocumentFile)) {
				alert("Error uploading file, selected file is not accepted." + 
    					"\nPlease upload file with a valid file type.");
				return false;
			}
		    else if(!validateFileSize(assignmentDocumentFile)) {
		    	alert("Error uploading file, selected file is too big." + 
    					"\nPlease upload a file below 5 MB.");
				return false;
			}
			

			if((attemptsUsed + 1) < maxAttempts){
				//return confirm('Please do not refresh screen after submitting assignment, as it will consume additional attempt. This is your attempt number '+ (attemptsUsed + 1) + '. Are you sure you want to submit assignment? ');
				
				if (confirm('Please do not refresh screen after submitting assignment, as it will consume additional attempt. This is your attempt number '+ (attemptsUsed + 1) + '. Are you sure you want to submit assignment? ') == true) {
					let btn = document.getElementById('edit');
					btn.style.display = 'none';
				} else {
					return false;	
				}

			}else{
			
				if (confirm('Please do not refresh screen after submitting assignment, as it will consume additional attempt. This is your last attempt. Are you sure you want to submit assignment?') == true) {
					let btn = document.getElementById('edit');
					btn.style.display = 'none';
				} else {
					return false;	
				}
			}

		}

	</script>


<body onload="myFunct()">

	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exam;Assignment" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			   <div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Assignment" name="activeMenu" />
				</jsp:include>
				</div>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<a href="/studentportal/${courseUrl}"
							title="Navigate to Course Home Page">
							<h2 class="red text-capitalize">${assignmentFile.subject}</h2>
						</a>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<form:form action="submitAssignment" method="post"
								modelAttribute="assignmentFile" enctype="multipart/form-data">
								<fieldset>
									<div class="col-md-4 column">
									<jsp:useBean id="now" class="java.util.Date" />
									<fmt:parseDate value="${assignmentFile.endDate}"
										var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
										<c:if test="${endDate gt now}"> 
											<div id="subjectTimer" class="exam-assg-timer"></div>
										</c:if>
										<c:if test="${endDate lt now}"> 
											<div id="ExpiredErrorMessages">
												<h2 class="red text-capitalize">Assignment Submission Deadline has Passed.</h2>
											</div>
										</c:if>
										<div class="clearfix"></div>
										<hr />
										<form action="editCompany" method="post">
											<%-- <jsp:useBean id="now" class="java.util.Date" /> --%>
											<fmt:parseDate value="${assignmentFile.startDate}"
												var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
											<fmt:parseDate value="${assignmentFile.endDate}"
												var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />

											<c:if test="${assignmentPaymentPending == 'Y' }">
												<div
													style="background-color: #DF3818; color: #fff; padding: 15px; font-size: 16px;">Assignment
													Submission Fees Pending</div>
											</c:if>

											<c:if test="${assignmentPaymentPending != 'Y' }">
												<p
													<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149; color:#fff; padding:15px; font-size:16px;"</c:if>
													<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;  padding:15px; font-size:16px;"</c:if>>
													 <b>Status:</b> ${assignmentFile.status}
												</p>
											</c:if>

											<div>
												<p style="margin-top: 10px; margin-bottom: 0px;">Max
													Attempts Permitted</p>
												<h4 style="margin: 0px;">${maxAttempts}</h4>
											</div>

											<div>
												<p style="margin-top: 10px; margin-bottom: 0px;">Attempts
													Used</p>
												<h4 style="margin: 0px;">${assignmentFile.attempts}</h4>
											</div>

											<div>
												<p style="margin-top: 10px; margin-bottom: 0px;">Submission
													Start Date & Time</p>
												<h4 style="margin: 0px;">
													<fmt:formatDate pattern="dd-MMM-yyyy HH:mm"
														value="${startDate}" timeStyle="full" />
												</h4>
											</div>

											<div>
												<p style="margin-top: 10px; margin-bottom: 0px;">Submission
													End Date & Time</p>
												<h4 style="margin: 0px;">
													<fmt:formatDate pattern="dd-MMM-yyyy HH:mm"
														value="${endDate}" timeStyle="full" />
												</h4>
											</div>

											<div>
												<p style="margin-top: 10px; margin-bottom: 0px;">Assignment
													Question File</p>
												<h4 style="margin: 0px;">
													<a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FILES_PATH')" />${assignmentFile.questionFilePreviewPath}">Download</a>
												</h4>
											</div>

											<!--START  For students who's end date is not yet expired -->
											<c:if test="${endDate gt now}">
												<p align="justify" style="margin-top: 10px;">
												<b>Note:</b> The file size should not exceed 5MB. Please use Chrome to avoid any Browser compatibility issue.</p>
												<div class="form-group">
													<form:label path="">Please attach your answer file in .pdf format</form:label>
														<p>
															<a href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format" target="_blank">How to convert word file 
															<i class="fa-regular fa-file-word fa-lg"></i> into pdf 
															<i class="fa-solid fa-file-pdf fa-lg"></i> file?
															</a>
														</p>
													<form:input path="fileData" type="file" id="assignmentDocument" accept="application/pdf" itemValue="${assignmentFile.fileData}" class="form-control"
														required="required" onchange="checkFile(event)" />
													<span class="help-block"></span>
												</div>

												<form:label path="">Please confirm below check points before you submit assignment:</form:label>
													<div class="checkbox">
														<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
															Selected subject assignment file is attached</span>
														</label> <br> 
														<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
															Attached file contains only the selected subject assignment</span>
														</label> <br> 
														<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
															Attached file size is below 5MB</span>
														</label> <br> 
														<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
															Attached file is virus free</span>
														</label> <br> 
														<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
															I have read the Internal Assignment Guidelines</span>
														</label> <br> 
														<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
															I have prepared Assignment & not copied</span>
														</label> <br> 
														<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
															I want to proceed to submit the attached file</span>
														</label> <br> 
														<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
															Attached File is NOT Hand Written</span>
														</label><br>
													</div>
											</c:if>
											<!--END For students who end date is not yet expired -->
											
											<!--START For extended students -->
											<%if(timeExtendedStudentIdSubjectList.contains(sapId + subject)){%>
												<p align="justify" style="margin-top: 10px;">
													<b>Note:</b> The file size should not exceed 5MB. Please use Chrome to avoid any Browser compatibility issue.</p>
													<div class="form-group">
														<form:label path="">Please attach your answer file in .pdf format</form:label>
															<p>
																<a href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format" target="_blank">How to convert word file 
																<i class="fa-regular fa-file-word fa-lg"></i> into pdf 
																<i class="fa-solid fa-file-pdf fa-lg"></i> file?
																</a>
															</p>
														<form:input path="fileData" type="file" id="assignmentDocument" accept="application/pdf" itemValue="${assignmentFile.fileData}" class="form-control"
															required="required" onchange="checkFile(event)" />
														<span class="help-block"></span>
													</div>
	
													<form:label path="">Please confirm below check points before you submit assignment:</form:label>
														<div class="checkbox">
															<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
																Selected subject assignment file is attached</span>
															</label> <br> 
															<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
																Attached file contains only the selected subject assignment</span>
															</label> <br> 
															<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
																Attached file size is below 5MB</span>
															</label> <br> 
															<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
																Attached file is virus free</span>
															</label> <br> 
															<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
																I have read the Internal Assignment Guidelines</span>
															</label> <br> 
															<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
																I have prepared Assignment & not copied</span>
															</label> <br> 
															<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
																I want to proceed to submit the attached file</span>
															</label> <br> 
															<label><input type="checkbox" style="width: 15px; height: 15px" name="confirmation" /><span>
																Attached File is NOT Hand Written</span>
															</label><br>
														</div>
											<%}%>
											<!--END For extended students -->

											<div class="control-group">

												<c:url value="submitAssignment" var="submitAssignmentUrl">
													<c:param name="year" value="${assignmentFile.year}" />
													<c:param name="month" value="${assignmentFile.month}" />
													<c:param name="subject" value="${assignmentFile.subject}" />
													<c:param name="status" value="${assignmentFile.status}" />
													<c:param name="startDate"
														value="${assignmentFile.startDate}" />
													<c:param name="endDate" value="${assignmentFile.endDate}" />
													<c:param name="questionFilePreviewPath" value="${assignmentFile.questionFilePreviewPath}" />
												</c:url>
												<c:url value="selectAssignmentPaymentSubjectsForm" 
													var="selectAssignmentPaymentSubjectsFormUrl">
												  <c:param name="year" value="${assignmentFile.year}" />
												  <c:param name="month" value="${assignmentFile.month}" />
												  <c:param name="subject" value="${assignmentFile.subject}" />
												  <c:param name="status" value="${assignmentFile.status}" />
							 					  <c:param name="startDate" value="${assignmentFile.startDate}" />
												  <c:param name="endDate" value="${assignmentFile.endDate}" />
												</c:url>
												
												<div class="controls" id="controlsId">
													<c:if test="${endDate gt now}"> 
														<c:if test="${(assignmentPaymentPending) != 'Y' && (submissionAllowed) == 'Y'}">
															<c:if test="${assignmentFile.attempts < maxAttempts}">
																	<button id="edit" name="edit" class="btn btn-primary" onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});"
																		formaction="${submitAssignmentUrl}" class="form-control">
																		<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>
																	 		Submit Assignment
																	</button>
															</c:if>
														</c:if>  
														
														<c:if test="${assignmentPaymentPending == 'Y'}">
															<a href="${selectAssignmentPaymentSubjectsFormUrl}"
																class="btn btn-primary">Proceed to Payment</a>
														</c:if>
														
													</c:if> 

													<!-- Added  to give access after end date. Comment and Uncomment as Needed : START-->
													<%
														if(timeExtendedStudentIdSubjectList.contains(sapId + subject)){
													%>
													 <c:if test="${(assignmentPaymentPending) != 'Y' && (submissionAllowed) == 'Y'}">
														<c:if test="${assignmentFile.attempts < maxAttempts}">
															<button id="edit" name="edit" class="btn btn-primary"
																onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});"
																formaction="${submitAssignmentUrl}" class="form-control">
																<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>
																	Submit Assignment
															</button>
														</c:if>
													</c:if> 
													
													<c:if test="${assignmentPaymentPending == 'Y' }">
															<a href="${selectAssignmentPaymentSubjectsFormUrl}" 
																class="btn btn-primary">Proceed to Payment</a>
													</c:if>
													
													<%
														}
													%>
													<!-- Added  to give access after end date. Comment and Uncomment as Needed : End-->

													<c:if test="${assignmentFile.attempts >= maxAttempts}">
														<button class="btn btn-primary" formaction="#"
															class="form-control" disabled="disabled">Max Attempts Reached</button>
													</c:if>

													<c:if test="${submissionAllow == 'N'}">  
														<button class="btn btn-primary" formaction="#"
															class="form-control" disabled="disabled">Results
															Awaited for Previous Submission</button>
													</c:if>

													<%
														if("true".equals(fromCourseHomePage)){
													%>
													<button id="cancel" name="cancel" class="btn btn-danger"
														formaction="/studentportal/${courseUrl}"
														formnovalidate="formnovalidate" class="form-control">
														Back to Course</button>
													<%
														}else{
													%>
													<a href="/exam/student/viewAssignmentsForm" title="Back to List" class="btn btn-warning" > Back to List</a>
													<!-- 
													<button id="cancel" name="cancel" class="btn btn-danger"
														formaction="viewAssignmentsForm"
														formnovalidate="formnovalidate" class="form-control">
														Back to List</button> -->
													<%
														}
													%>
												</div>
											</div>
										</form>
									</div>

									<c:if test="${not empty assignmentFile.previewPath}">
										<div class="col-md-8 column">
											<div>
												<b>Preview of Submitted File | 
													<a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}">
														<i class="fa-solid fa-download" aria-hidden="true"></i> Download Submitted File to cross check
													</a>         
												</b>
											</div>  
											<embed align="middle"
												src="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}"
												width="100%" height="820px">
										</div>
									</c:if>
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<jsp:include page="../common/footer.jsp" />
	<script>
		const acceptedFileType = "application/pdf";
		
		var endDateStr = '${assignmentFile.endDate}'
		if(endDateStr) {
			var endDate = moment(endDateStr, 'YYYY-MM-DD HH:mm:ss').toDate();
			$('#subjectTimer').countdown('destroy');
			$('#subjectTimer').countdown({until: endDate});
			$('#subjectTimer').countdown('toggle');
		}
		
		$(document).ready(function(){
			$.ajax({
				url:'/studentportal/refreshAssignmentFilesStatus',
				type:'GET',
				success:function(){
					console.log("Refresh Student Portal successfully");
				},
				error:function(){
					console.log("Refresh Student Portal Failed");
				}
			});
			
		});
		function myFunct(){
			var sapid = <%=sapId%>
			if(sapid =='77777777777'){
				document.getElementById("controlsId").style.display = 'none';
			}else{
				document.getElementById("controlsId").style.display = 'block';
			}
		}

		/*
			Checks if the file selected by the user for upload is of valid type and size or not.
		*/
		function checkFile(e) {
			const fileUpload = e.target.files[0];
			displayDocumentErrorMessage(false, "");			//hide the span help-block if shown

			if(!validateFileType(fileUpload))
				displayDocumentErrorMessage(true, "Selected file type not accepted. Please upload a PDF File.");
			else if(!validateFileSize(fileUpload))
				displayDocumentErrorMessage(true, "Selected file size too big. Please upload a file below 5 MB.");
		}

		/*
			Checks if file type matches the accepted file types.
			localeCompare() is used with the sensitivity: 'accent' option to allow case insensitive comparison.
			Content-Type of file should be checked case-insensitively as mentioned in RFC 2045 
		*/
		function validateFileType(file) {
			return acceptedFileType.localeCompare(file.type, undefined, { sensitivity: 'accent' }) === 0;		//check if selected file type matches the accepted file type. 
		}
	
		/*
			Checks if the file size is 6 MB or below.
			User asked to upload file below 5 MB, 1 MB kept as threshold.
		*/
		function validateFileSize(file) {
			return (file.size <= 6 * 1024 * 1024) ? true : false;
		}

		/*
			Method which displays and highlights error message for the assignmentDocument element.
		*/
		function displayDocumentErrorMessage(showError, errorMessage) {
			let helpBlock = document.querySelector(".help-block");
			helpBlock.textContent = errorMessage;

			if(showError) {
				document.getElementById("assignmentDocument").parentNode.classList.add("has-error");
				helpBlock.style.setProperty("display", "block");
			}
			else {
				document.getElementById("assignmentDocument").parentNode.classList.remove("has-error");
				helpBlock.style.setProperty("display", "none");
			}
		}


		
	</script>

</body>
</html>