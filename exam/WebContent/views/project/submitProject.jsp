<%-- <!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.StudentBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Submit Project" name="title" />
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
			alert("Please confirm against all options in Checklist before submitting Project. You have "+ (maxAttempts - attemptsUsed) + " attempts left.");
			return false;
		}
		
		if((attemptsUsed + 1) < maxAttempts){
			return confirm('Please do not refresh screen after submitting Project, as it will consume additional attempt. This is your attempt number '+ (attemptsUsed + 1) + '. Are you sure you want to submit Project?');
		}else{
			return confirm('Please do not refresh screen after submitting Project, as it will consume additional attempt. This is your last attempt. Are you sure you want to submit Project?');
		}
		
	}
	
</script>
<%
	String sapId = (String)session.getAttribute("userId");
	StudentBean student = (StudentBean)session.getAttribute("student");
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
%>

<body class="inside">


<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        
        <div class="row"><legend>Submit Project</legend></div>
        
        <div class="panel-body">
        <%@ include file="../messages.jsp"%>
		
		<c:if test="${canSubmit != 'false' }">
		<form:form  action="submitAssignment" method="post" modelAttribute="assignmentFile" enctype="multipart/form-data">
			<fieldset>
			
			
			<div class="col-md-6 column">

				<h2>Time left to submit Project</h2>
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
					<p>Project Preparation Guidelines</p>
						<h3><a href="downloadStudentProjectFile?filePath=${assignmentFile.filePath}&subject=Project">Download</a></h3>
				</div>
				
				<div class="titleContainer titleContainerResultIns">
					<p>Project Methodology</p>
						<h3><a href="/exam/resources_2015/Project_Methodology.pdf" target="_blank">Download</a></h3>
				</div>
				
				<div class="titleContainer titleContainerResultIns">
					<p>Project Evaluation</p>
						<h3><a href="/exam/resources_2015/Project_Evaluation.pdf" target="_blank">Download</a></h3>
				</div>

				
				
				<br>
				<h5><b>Note:</b> The file size should not exceed 10 MB.<br>Please do not refresh screen after submitting Project, as it will consume additional attempt</h5>	
				<h5><b>Please use Chrome to avoid any Browser compatibility issue.</b></h5>				
				
				
				
				<div class="form-group">
					<form:label path="">Please attach your answer file in .pdf format</form:label>
					<a href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format" target="_blank">How to convert word file <i class="fa fa-file-word-o fa-lg"></i> into pdf <i class="fa fa-file-pdf-o fa-lg"></i> file?</a>
					<form:input path="fileData" type="file" itemValue="${assignmentFile.fileData}" class="form-control" required="required" />  
				</div>
				
				
				<b>Please confirm below check points before you submit Project:</b>
				<div class="checkbox">
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>Attached file size is below 10MB</span></label> <br>
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>Attached file is virus free</span></label><br>
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I have read the Project Submission Guidelines</span></label>	<br>
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I have prepared Project & not copied</span></label>	<br>
				<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I want to proceed to submit the attached file</span></label>	<br>
				</div>

						
				
				<div class="control-group">
				
				<c:url value="submitProject" var="submitProjectUrl">
				  <c:param name="year" value="${assignmentFile.year}" />
				  <c:param name="month" value="${assignmentFile.month}" />
				  <c:param name="subject" value="Project" />
				  <c:param name="status" value="${assignmentFile.status}" />
				  <c:param name="startDate" value="${assignmentFile.startDate}" />
				  <c:param name="endDate" value="${assignmentFile.endDate}" />
				</c:url>
		
				
				<div class="controls">
					<c:if test="${endDate gt now}">
						<c:if test="${assignmentFile.attempts < maxAttempts}">
							<button id="edit" name="edit" class="btn btn-primary" onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});" 
							formaction="${submitProjectUrl}" class="form-control">
							<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>Submit Project</button>
						</c:if>
					</c:if>
					
					<c:if test="${assignmentFile.attempts >= maxAttempts}">
					<button id="edit" name="edit" class="btn btn-primary" formaction="#" class="form-control" disabled="disabled">Max Attempts Reached</button>
					</c:if>
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
				
				
			
			
				
			</fieldset>
		</form:form>
		</c:if>
		
		</div>
		<br>

		
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
 
  
 
 <%--old project submission page
  <!DOCTYPE html>
<%@page import="java.util.Arrays"%>
<%@page import="com.nmims.beans.StudentBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%try{ %>
<%
	String sapId = (String)session.getAttribute("userId");
	StudentBean student = (StudentBean)session.getAttribute("student");
	/* ArrayList<String> studentlistForBlocking =new ArrayList<String>(
			Arrays.asList("77215001571","77215000331","77215000983","77215002215","77215001568","77215000505","77215002477","77215001986")); */
	
	ArrayList<String> studentlistForBlocking =new ArrayList<String>();
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
	ArrayList<String> timeExtendedStudentIdSubjectList = (ArrayList<String>)session.getAttribute("timeExtendedStudentIdSubjectList");
	String subject = (String)session.getAttribute("subject");
	
%>

<html lang="en">
    
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Submit Project" name="title"/>
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
				alert("Please confirm against all options in Checklist before submitting Project. You have "+ (maxAttempts - attemptsUsed) + " attempts left.");
				return false;
			}
			
			if((attemptsUsed + 1) < maxAttempts){
				return confirm('Please do not refresh screen after submitting Project, as it will consume additional attempt. This is your attempt number '+ (attemptsUsed + 1) + '. Are you sure you want to submit Project?');
			}else{
				return confirm('Please do not refresh screen after submitting Project, as it will consume additional attempt. This is your last attempt. Are you sure you want to submit Project?');
			}
			
		}
		
	</script>
    
    <body>
    	<%@ include file="../common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Project Submission" name="breadcrumItems"/>
			</jsp:include>
        	
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Project Submission" name="activeMenu"/>
							</jsp:include>
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						<div class="sz-content">
										<h2 class="red text-capitalize">Submit Project</h2>
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="../common/messages.jsp" %>
											
											<c:if test= "${canSubmit != false }">
												<form:form  action="submitAssignment" method="post" modelAttribute="assignmentFile" enctype="multipart/form-data">
													<fieldset>
														<div class="col-md-4 column">
														<div id="subjectTimer" class="exam-assg-timer"></div>
														<div class="clearfix"></div>
														<hr/>
														<form  action="editCompany" method="post">
															<jsp:useBean id="now" class="java.util.Date" />
															<fmt:parseDate value="${assignmentFile.startDate}" var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
															<fmt:parseDate value="${assignmentFile.endDate}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
															<p 
																<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149; color:#fff; padding:15px; font-size:16px;"</c:if>
																<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;  padding:15px; font-size:16px;"</c:if>
															><b>Status:</b> ${assignmentFile.status}</p>
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Max Attempts Permitted</p>
															<h4 style="margin:0px;">${maxAttempts}</h4>
														</div>
										
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Attempts Used</p>
															<h4 style="margin:0px;">${assignmentFile.attempts}</h4>
														</div>
										
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Submission Start Date & Time</p>
															<h4 style="margin:0px;"><fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${startDate}" timeStyle="full"/></h4>
														</div>
														
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Submission End Date & Time</p>
															<h4 style="margin:0px;"><fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${endDate}" timeStyle="full"/></h4>
														</div>
														
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Project Preparation Guidelines</p>
																<h4 style="margin:0px;"><a href="downloadStudentProjectFile?filePath=${assignmentFile.filePath}&subject=Project">Download</a></h4>
														</div>
														
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Project Methodology</p>
																<h4 style="margin:0px;"><a href="/exam/resources_2015/Project_Methodology.pdf" target="_blank">Download</a></h4>
														</div>
														
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Project Evaluation</p>
															<h4 style="margin:0px;"><a href="/exam/resources_2015/Project_Evaluation.pdf" target="_blank">Download</a></h4>
														</div>
										
														
														
														<p align="justify" style="margin-top:10px;">
														<b>Note:</b> The file size should not exceed 10 MB.	
														Please use Chrome to avoid any Browser compatibility issue.				
														</p>
														
														
														<div class="form-group">
															<form:label path="">Please attach your answer file in .pdf format</form:label>
															<p>
															<a href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format" target="_blank">How to convert word file <i class="fa fa-file-word-o fa-lg"></i> into pdf <i class="fa fa-file-pdf-o fa-lg"></i> file?</a>
															</p>
															<form:input path="fileData" type="file" itemValue="${assignmentFile.fileData}" class="form-control" required="required" />  
														</div>
														
														
														<form:label path="">Please confirm below check points before you submit Project:</form:label>
														<div class="checkbox">
														<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>Attached file size is below 10MB</span></label> <br>
														<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>Attached file is virus free</span></label><br>
														<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I have read the Project Submission Guidelines</span></label>	<br>
														<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I have prepared Project & not copied</span></label>	<br>
														<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I want to proceed to submit the attached file</span></label>	<br>
														</div>
										
														<div class="control-group">
														<c:url value="submitProject" var="submitProjectUrl">
														  <c:param name="year" value="${assignmentFile.year}" />
														  <c:param name="month" value="${assignmentFile.month}" />
														  <c:param name="subject" value="Project" />
														  <c:param name="status" value="${assignmentFile.status}" />
														  <c:param name="startDate" value="${assignmentFile.startDate}" />
														  <c:param name="endDate" value="${assignmentFile.endDate}" />
														</c:url>
												
														
															<div class="controls">
																<c:if test="${endDate gt now}">
																	<c:if test="${assignmentFile.attempts < maxAttempts}">
																		<%if(!studentlistForBlocking.contains(student.getSapid())){ %>
																		<button id="edit" name="edit" class="btn btn-primary" onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});" 
																		formaction="${submitProjectUrl}" class="form-control">
																		<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>Submit Project</button>
																		<%} %>
																	</c:if>
																</c:if>
												            	<c:if test="${assignmentFile.attempts >= maxAttempts}">
																	<button id="edit" name="edit" class="btn btn-primary" formaction="#" class="form-control" disabled="disabled">Max Attempts Reached</button>
																</c:if>
																
															 <%if(timeExtendedStudentIdSubjectList.contains(sapId + subject)){%> 
																
															 		<c:if test="${assignmentFile.attempts < maxAttempts}">
																		<%if(!studentlistForBlocking.contains(student.getSapid())){ %>
																		<button id="edit" name="edit" class="btn btn-primary" onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});" 
																		formaction="${submitProjectUrl}" class="form-control">
																		<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>Submit Project</button>
																		<%} %>
																 	</c:if>
														 <%}%> 
											</div>
									</form>		
															
					</div>
										<c:if test="${not empty assignmentFile.previewPath}">
												<div class="col-md-8 column">
												<div>
												    <b>Preview of Submitted File |
														<a href="downloadStudentProjectFile?filePath=${assignmentFile.studentFilePath}">
															<i class="fa-solid fa-download" aria-hidden="true"></i>
															Download submitted file for cross check
														</a>
													</b>
												</div>
												<embed align="middle" src="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}?id=<%=Math.random() %>" 
												width = "100%" height="820px">
												</div>
											</c:if>
										</fieldset>
									</form:form>
								</c:if>
										</div>
              						</div>
              				</div>
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footer.jsp"/>
            
		<script>
			console.log(new Date('${assignmentFile.endDate}'));
			$('#subjectTimer').countdown({until: new Date('${assignmentFile.endDate}'), format: 'dHMS'});
			$('#subjectTimer').countdown('toggle');
		</script>
		
    </body>
</html>

<%}catch(Exception e){

}
%> --%>

 <!-- Changes done for Project Payment -->
 <!DOCTYPE html>
<%@page import="java.util.Arrays"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%try{ %>
<%
	String sapId = (String)session.getAttribute("userId");
StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	
	ArrayList<String> studentlistForBlocking =new ArrayList<String>();
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}
	ArrayList<String> timeExtendedStudentIdSubjectList = (ArrayList<String>)session.getAttribute("timeExtendedStudentIdSubjectList");
	String subject = (String)session.getAttribute("subject");
	
%>

<html lang="en">
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Submit ${subject}" name="title"/>
    </jsp:include>
    <style>
    	.has-error {
			margin-bottom: 1em;
		}
		.help-block {
		    font-size: 15px;
		    font-weight: 600;
		    margin-top: 0.65em;
		}
    </style>
    <script language="JavaScript">
		function validateForm( attemptsUsed, maxAttempts) {
			const projectDocumentFile = document.getElementById("projectDocument").files[0];
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
				alert("Please confirm against all options in Checklist before submitting Project. You have "+ (maxAttempts - attemptsUsed) + " attempts left.");
				return false;
			}
			else if(!validateFileType(projectDocumentFile)) {
				alert("Error uploading file, selected file is not accepted." + 
    					"\nPlease upload file with a valid file type.");
				return false;
			}
		    else if(!validateFileSize(projectDocumentFile)) {
		    	alert("Error uploading file, selected file is too big." + 
    					"\nPlease upload a file below 10 MB.");
				return false;
			}
			
			if((attemptsUsed + 1) < maxAttempts){

				//return confirm('Please do not refresh screen after submitting Project, as it will consume additional attempt. This is your attempt number '+ (attemptsUsed + 1) + '. Are you sure you want to submit Project?');
				if (confirm('Please do not refresh screen after submitting Project, as it will consume additional attempt. This is your attempt number '+ (attemptsUsed + 1) + '. Are you sure you want to submit Project? ') == true) {
					let btn = document.getElementById('edit');
					btn.style.display = 'none';
				} else {
					return false;	
				}
			}else{

				//return confirm('Please do not refresh screen after submitting Project, as it will consume additional attempt. This is your last attempt. Are you sure you want to submit Project?');
				if (confirm('Please do not refresh screen after submitting Project, as it will consume additional attempt. This is your last attempt. Are you sure you want to submit Project?') == true) {
					let btn = document.getElementById('edit');
					btn.style.display = 'none';
				} else {
					return false;	
				}
			}
			
		}
		
	</script>
    
    <body>

    	<%@ include file="../common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Project Submission" name="breadcrumItems"/>
			</jsp:include>
        	
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
                         <div id="sticky-sidebar">  
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="Project Submission" name="activeMenu"/>
							</jsp:include>
              			 </div>              			
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						<div class="sz-content"><br/> 
              							<%@ include file="../common/messages.jsp" %>
										
										<c:if test= "${canSubmit == false }">
										<h2 class="red text-capitalize">${subject} GuideLines</h2>
										<div class="clearfix"></div>
											<div style="min-height:210px;"> 
												<div class="col-md-6">
													<ul class="extra-assignment-action" >
														<%-- <li><a href="downloadStudentProjectFile?filePath=${assignmentFile.filePath}&subject=${subject}"><span class="fa-solid fa-file-pdf"></span>Download Project Guidelines</a></li> --%>
														<li><a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FILES_PATH')" />${assignmentFile.questionFilePreviewPath}"><span class="fa-solid fa-file-pdf"></span>Download ${subject} Guidelines</a></li>
														<li><a href="viewPreviousProjects" target="_blank"><span class="fa-solid fa-book-bookmark"></span>View Previous Submissions</a></li>
													  </ul>
												</div>
											</div>
											
											<c:if test="${submitted != false }">
											<div class="panel-content-wrapper">
											<form:form  action="" method="post" modelAttribute="assignmentFile" enctype="multipart/form-data">
												<fieldset>
													<c:if test="${not empty assignmentFile.previewPath}">
														<div class="col-md-8 column">
														<div>
														    <b>Preview of Submitted File |  
																<a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}">
																	<i class="fa-solid fa-download" aria-hidden="true"></i> Download Submitted File to cross check
																</a>  
															</b>
														</div>
														<embed align="middle" src="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}?id=<%=Math.random() %>" 
														width = "100%" height="820px">
														</div>
													</c:if>
												</fieldset>
											</form:form>
											</div>
											</c:if>
										</c:if>
										<div class="clearfix"></div>
		              					
											<c:if test= "${canSubmit != false }">
											<h2 class="red text-capitalize">Submit ${subject}</h2>
											<div class="clearfix"></div>
											<div class="panel-content-wrapper">
												<form:form  action="submitAssignment" method="post" modelAttribute="assignmentFile" enctype="multipart/form-data">
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
																<h2 class="red text-capitalize">${subject} Submission Deadline has Passed.</h2>
															</div>
														</c:if>
														<div class="clearfix"></div>
														<hr/>
														<form  action="editCompany" method="post">
															<%-- <jsp:useBean id="now" class="java.util.Date" /> --%>
															<fmt:parseDate value="${assignmentFile.startDate}" var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
															<fmt:parseDate value="${assignmentFile.endDate}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
															
															<c:set var="containsKey" value="false" />
															<c:forEach var="item" items="${timeExtendedStudentIdSubjectList}">
														  		<c:if test="${item eq key}">
														    		<c:set var="containsKey" value="true" />
														  		</c:if>
															</c:forEach>
															
															<p 
																<c:if test="${assignmentFile.status == 'Submitted'}">style="background-color: #17B149; color:#fff; padding:15px; font-size:16px;"</c:if>
																<c:if test="${assignmentFile.status != 'Submitted'}">style="background-color: #DF3818;color:#fff;  padding:15px; font-size:16px;"</c:if>
															><b>Status:</b> ${assignmentFile.status}</p>
															<c:catch var="projectTitleException">${projectTitle}</c:catch>
															<c:if test="${projectTitleException==null}">
																<div>
																	<p style="margin-top:10px;margin-bottom:0px;">Title</p>
																	<h4 style="margin:0px;">${projectTitle}</h4>
																</div>
															</c:if>
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Max Attempts Permitted</p>
															<h4 style="margin:0px;">${maxAttempts}</h4>
														</div>
										
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Attempts Used</p>
															<h4 style="margin:0px;">${assignmentFile.attempts}</h4>
														</div>
										
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Submission Start Date & Time</p>
															<h4 style="margin:0px;"><fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${startDate}" timeStyle="full"/></h4>
														</div>
														
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Submission End Date & Time</p>
															<h4 style="margin:0px;"><fmt:formatDate  pattern="dd-MMM-yyyy HH:mm"  value="${endDate}" timeStyle="full"/></h4>
														</div>
														
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">${subject} Preparation Guidelines</p>
																<%-- <h4 style="margin:0px;"><a href="downloadStudentProjectFile?filePath=${assignmentFile.filePath}&subject=${subject}">Download</a></h4> --%>
																<h4 style="margin:0px;"><a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_FILES_PATH')" />${assignmentFile.questionFilePreviewPath}">Download</a></h4>
														</div>
														
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Project Methodology</p>
																<h4 style="margin:0px;"><a href="/exam/resources_2015/Project_Methodology.pdf" target="_blank">Download</a></h4>
														</div>
														
														<div>
															<p style="margin-top:10px;margin-bottom:0px;">Project Evaluation</p>
															<h4 style="margin:0px;"><a href="/exam/resources_2015/Project_Evaluation.pdf" target="_blank">Download</a></h4>
														</div>
										
														<!--START  For students who's end date is not yet expired -->
														<c:if test="${endDate gt now}">
															<p align="justify" style="margin-top:10px;">
																<b>Note:</b> The file size should not exceed 10 MB.	
																Please use Chrome to avoid any Browser compatibility issue.				
															</p>
																
																<%-- 
																 <div class="form-group">
																	<form:label path="">Please enter title for the project</form:label>
																	<form:textarea path="title" type="text" itemValue="${assignmentFile.title}" class="form-control" required="required" />  
																</div>  --%>
																
															<div class="form-group">
																<form:label path="">Please attach your answer file in .pdf format</form:label>
																<p>
																<a href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format" target="_blank">How to convert word file <i class="fa-regular fa-file-word fa-lg"></i> into pdf <i class="fa-solid fa-file-pdf fa-lg"></i> file?</a>
																</p>
																<form:input path="fileData" type="file" id="projectDocument" itemValue="${assignmentFile.fileData}" 
																	accept="application/pdf" class="form-control" required="required" onchange="checkFile(event)" />  
															</div>
																
																
															<form:label path="">Please confirm below check points before you submit ${subject}:</form:label>
															<div class="checkbox">
															<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>Attached file size is below 10MB</span></label> <br>
															<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>Attached file is virus free</span></label><br>
															<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I have read the ${subject} Submission Guidelines</span></label>	<br>
															<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I have prepared ${subject} & not copied</span></label>	<br>
															<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I want to proceed to submit the attached file</span></label>	<br>
															</div>
														</c:if>
														<!--END For students who end date is not yet expired -->
											
														<!--START For extended students -->
														<c:if test="${containsKey == true}">
															<p align="justify" style="margin-top:10px;">
																<b>Note:</b> The file size should not exceed 10 MB.	
																Please use Chrome to avoid any Browser compatibility issue.				
															</p>
																
																<%-- 
																 <div class="form-group">
																	<form:label path="">Please enter title for the project</form:label>
																	<form:textarea path="title" type="text" itemValue="${assignmentFile.title}" class="form-control" required="required" />  
																</div>  --%>
																
															<div class="form-group">
																<form:label path="">Please attach your answer file in .pdf format</form:label>
																<p>
																<a href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format" target="_blank">How to convert word file <i class="fa-regular fa-file-word fa-lg"></i> into pdf <i class="fa-solid fa-file-pdf fa-lg"></i> file?</a>
																</p>
																<form:input path="fileData" type="file" id="projectDocument" itemValue="${assignmentFile.fileData}" 
																	accept="application/pdf" class="form-control" required="required" onchange="checkFile(event)" />  
															</div>
																
																
															<form:label path="">Please confirm below check points before you submit ${subject}:</form:label>
															<div class="checkbox">
															<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>Attached file size is below 10MB</span></label> <br>
															<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>Attached file is virus free</span></label><br>
															<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I have read the ${subject} Submission Guidelines</span></label>	<br>
															<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I have prepared ${subject} & not copied</span></label>	<br>
															<label><input type="checkbox" style="width:15px;height:15px" name="confirmation"/><span>I want to proceed to submit the attached file</span></label>	<br>
															</div>
														</c:if>
														<!--END For students who end date is not yet expired -->
														
														<div class="control-group">
														<c:url value="submitProject" var="submitProjectUrl">
														  <c:param name="year" value="${assignmentFile.year}" />
														  <c:param name="month" value="${assignmentFile.month}" />
														  <c:param name="subject" value="${subject}" />
														  <c:param name="status" value="${assignmentFile.status}" />
														  <c:param name="startDate" value="${assignmentFile.startDate}" />
														  <c:param name="endDate" value="${assignmentFile.endDate}" />
														  <c:param name="questionFilePreviewPath" value="${assignmentFile.questionFilePreviewPath}" />
														</c:url>
												
														
														<div class="controls">
															<c:if test="${endDate gt now}">
																<c:if test="${assignmentFile.attempts < maxAttempts}">
																	<%if(!studentlistForBlocking.contains(student.getSapid())){ %>
																	<button id="edit" name="edit" class="btn btn-primary" onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});" 
																	formaction="${submitProjectUrl}" class="form-control">
																	<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>Submit ${subject}</button>
																<%} %>
																</c:if>
															</c:if>
											            	<c:if test="${assignmentFile.attempts >= maxAttempts}">
																<button id="edit" name="edit" class="btn btn-primary" formaction="#" class="form-control" disabled="disabled">Max Attempts Reached</button>
															</c:if>
															
														
															<c:if test="${containsKey == true}">
														 		<c:if test="${assignmentFile.attempts < maxAttempts}">
																	<%if(!studentlistForBlocking.contains(student.getSapid())){ %>
																	<button id="edit" name="edit" class="btn btn-primary" onclick="return validateForm(${assignmentFile.attempts}, ${maxAttempts});" 
																	formaction="${submitProjectUrl}" class="form-control">
																	<c:if test="${assignmentFile.attempts > 0}">Re-</c:if>Submit ${subject}</button>
																	<%} %>
															 	</c:if>
															</c:if>
													
														</div>
													</div>
												</form>
											</div>
											<c:if test="${not empty assignmentFile.previewPath}">
												<div class="col-md-8 column">
												<div>
												    <b>Preview of Submitted File |
														<a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.studentFilePath}">
															<i class="fa-solid fa-download" aria-hidden="true"></i>
															Download submitted file for cross check   
														</a>
													</b>
												</div>
												<embed align="middle" src="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.studentFilePath}" 
												width = "100%" height="820px">          
												</div>  
											</c:if>
										</fieldset>
									</form:form>
								</c:if>
										  <c:if test="${paymentApplicable == true}">
											<%try{ %>	
											
												<form:form  action="goToProjectPaymentGateway" method="post" modelAttribute="assignmentFile" >
													<fieldset>
													
														<jsp:useBean id="today" class="java.util.Date" />
														<fmt:parseDate value="${assignmentFile.startDate}" var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
														<fmt:parseDate value="${assignmentFile.endDate}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
														<c:set var="contains" value="false" />
														<c:forEach var="item" items="${timeExtendedStudentIdSubjectList}">
														  <c:if test="${item eq key}">
														    <c:set var="contains" value="true" />
														  </c:if>
														</c:forEach>
													<div class ="row"></div>
													<table class = "table table-striped table-hover tables dataTable no-footer">
														<thead>
															<tr> 
																<th>Exam Month </th>
																<th>Exam Year </th>
																<th>Start Date </th>
																<th>End Date </th>
																<th>Pay </th>
															</tr>
														</thead>
														<tbody>
														<tr>
															<td>${assignmentFile.month}</td>
															<td>${assignmentFile.year}</td>
															<td>${assignmentFile.startDate}</td>
															<td>${assignmentFile.endDate}</td>
															<td>
																<c:choose>
																    <c:when test="${endDate gt today }">
																		<button id="btn" name="btn" class="btn btn-primary" formaction="goToProjectPaymentGateway"  >Proceed to Payment</button>
																    </c:when> 
																    <c:when test="${contains == true}">
																   		 <button id="btn" name="btn" class="btn btn-primary" formaction="goToProjectPaymentGateway"  >Proceed to Payment</button>
																    </c:when>   
																    <c:otherwise>
																    	 Time's up
																    </c:otherwise>
																</c:choose>
															</td> 
														</tr>
														</tbody>
													</table>
													
												 </fieldset>
													
												</form:form>
												
												<%}catch(Exception e){ %>
												
												<%} %>
											</div>
											</c:if>   
										
              						</div>
              				</div>
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footer.jsp"/>
            
		<script>
			const acceptedFileType = "application/pdf";
			let helpBlockTimeout;
			
			var endDateTime = '${assignmentFile.endDate}'
			if(endDateTime) {
				var endDate = moment(endDateTime, 'YYYY-MM-DD HH:mm:ss').toDate();
				$('#subjectTimer').countdown('destroy');
				$('#subjectTimer').countdown({until: endDate});
				$('#subjectTimer').countdown('toggle');
			}

			/*
				Checks if the file selected by the user for upload is of valid type and size or not.
			*/
			function checkFile(e) {
				const fileUpload = e.target.files[0];
	
				if(!validateFileType(fileUpload)) {
					displayErrorMessage(document.getElementById("projectDocument"), "Selected file type not accepted. Please upload a PDF File.");
					e.target.value = "";
				}
				else if(!validateFileSize(fileUpload)) {
					displayErrorMessage(document.getElementById("projectDocument"), "Selected file size too big. Please upload a file below 10 MB.");
					e.target.value = "";
				}
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
				Checks if the file size is 11 MB or below.
				User asked to upload file below 10 MB, 1 MB kept as threshold.
			*/
			function validateFileSize(file) {
				return (file.size <= 11 * 1024 * 1024) ? true : false;
			}
	
			/*
				Method which displays an error message for the passed element.
			*/
			function displayErrorMessage(element, errorMessage) {
				const elParent = element.parentNode;
				removeHelpBlockFromElement(elParent);		//remove the span help-block if it already exists
				clearTimeout(helpBlockTimeout);				//clearTimeout of previous block if present
				
				const helpNode = document.createElement("span");
				helpNode.classList.add("help-block");
				helpNode.textContent = errorMessage;
		
				elParent.classList.add("has-error");
				elParent.appendChild(helpNode);
				
				helpBlockTimeout = setTimeout(removeHelpBlockFromElement, 6500, elParent);		//execute function to remove help-block after 6.5 seconds
			}
	
			/*
				Removes the span help-block child element if present in the passed parent element.
				Also removes it's has-error class if present.
			*/
			function removeHelpBlockFromElement(element) {
				element.querySelectorAll("span.help-block")
						.forEach(childElement => element.removeChild(childElement));
				element.classList.remove("has-error");
			}

		</script>
		
    </body>
</html>

<%}catch(Exception e){

}
%> 