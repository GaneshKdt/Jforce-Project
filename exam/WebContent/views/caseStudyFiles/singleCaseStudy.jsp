  
 <!DOCTYPE html>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%
	String fromCourseHomePage = request.getParameter("fromCourseHomePage");
	String sapId = (String)session.getAttribute("userId");
	String subject = (String)request.getAttribute("subject");
	StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String studentName = "";
	if(student != null){
		studentName = student.getFirstName() + " " + student.getLastName();
	}

%>

<c:url value="student/viewCourseHomePage" var="courseUrl">
	  <c:param name="subject" value="${assignmentFile.subject}" />
</c:url>
															
<html lang="en">
	
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Submit Assignment" name="title"/>
    </jsp:include>
    

    
    
    <body>
    
    	<%@ include file="../common/header.jsp" %>
    	
    	
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../common/breadcrum.jsp">
		<jsp:param value="Student Zone;Exam;CaseStudy" name="breadcrumItems"/>
		</jsp:include>
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../common/left-sidebar.jsp">
								<jsp:param value="CaseStudy" name="activeMenu"/>
							</jsp:include>
              				
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../common/studentInfoBar.jsp" %>
              						
              						
              						<div class="sz-content">
								
										
										<div class="clearfix"></div>
		              					<div class="panel-content-wrapper">
											<%@ include file="../common/messages.jsp" %>
												<div id="subjectTimer" class="exam-assg-timer"></div>
										        <div class="clearfix"></div>
											
											<form:form  action="" method="post" modelAttribute="caseStudyFile" enctype="multipart/form-data">
												<fieldset>
												
												<div class="col-md-4 column">
									
													<div class="clearfix"></div>
													<hr/>
													<form  action="" method="post">
													<jsp:useBean id="now" class="java.util.Date" />
													<fmt:parseDate value="${caseStudyFile.startDate}" var="startDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
													<fmt:parseDate value="${caseStudyFile.endDate}" var="endDate" pattern="yyyy-MM-dd HH:mm:ss" type="BOTH" />
													
													<%-- 	<p 
															<c:if test="${status == 'Submitted'}">style="background-color: #17B149; color:#fff; padding:15px; font-size:16px;"</c:if>
															<c:if test="${status != 'Submitted'}">style="background-color: #DF3818;color:#fff;  padding:15px; font-size:16px;"</c:if>
														
													      ><b>Status: </b> ${status}</p> --%>
														<div>
															<p style="margin-top: 10px; margin-bottom: 0px;">Submission Start Date & Time</p>
														    <h4 style="margin: 0px;">
															<fmt:formatDate pattern="dd-MMM-yyyy HH:mm"
																value="${startDate}" timeStyle="full" />
														   </h4>
													   </div>
													   
													   <div>
															<p style="margin-top: 10px; margin-bottom: 0px;">Submission End Date & Time</p>
														    <h4 style="margin: 0px;">
															<fmt:formatDate pattern="dd-MMM-yyyy HH:mm"
																value="${endDate}" timeStyle="full" />
														   </h4>
													   </div>
													
													<%-- <div>
														<p style="margin-top:10px;margin-bottom:0px;">CaseStudy Question File</p>
														<h4 style="margin:0px;"><a href="student/downloadCaseStudyQuestionFile?filePath=${caseStudyFile.filePath}&subject=${caseStudyFile.topic}">Download</a></h4>
													</div> --%>
									
													
													
													<p align="justify" style="margin-top:10px;">
													<b>Note:</b> The file size should not exceed 5MB. 	
													Please use Chrome to avoid any Browser compatibility issue.			
													</p>
													
													
													<div class="form-group">
														<form:label path="">Please attach your answer file in .pdf format</form:label>
														<p>
														<a href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format" target="_blank">How to convert word file <i class="fa-regular fa-file-word fa-lg"></i> into pdf <i class="fa-solid fa-file-pdf fa-lg"></i> file?</a>
														</p>
														<form:input path="fileData" type="file" itemValue="${caseStudyFile.fileData}" class="form-control" />  
													</div>
													
													
													<form:label path="">Please confirm below check points before you submit caseStudy:</form:label>
													<div class="checkbox">
													<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>Selected topic CaseStudy file is attached</span></label> <br>
													<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>Attached file contains only the selected topic CaseStudy</span></label> <br>
													<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>Attached file size is below 5MB</span></label> <br>
													<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>Attached file is virus free</span></label><br>
													<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>I have read the CaseStudy Guidelines</span></label>	<br>
													<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>I have prepared CaseStudy & not copied</span></label>	<br>
													<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>I want to proceed to submit the attached file</span></label>	<br>
													<label><input type="checkbox" style="width:15px;height:15px" name="confirmation" /><span>Attached File is NOT Hand Written</span></label><br>
													</div>
									
															
													
													<div class="control-group">
													
													<c:url value="submitCase" var="submitCaseUrl">
													  <c:param name="batchYear" value="${caseStudyFile.batchYear}" />
													  <c:param name="batchMonth" value="${caseStudyFile.batchMonth}" />
													  <c:param name="status" value="${caseStudyFile.status}" />
													  <c:param name="startDate" value="${caseStudyFile.startDate}" />
													  <c:param name="endDate" value="${caseStudyFile.endDate}" />
													  <c:param name="topic" value="${caseStudyFile.topic}" />
													</c:url>
											
														
														
															<c:if test="${submissionAllowed == 'true'}">
															    <c:if test = "${endDate > now &&  empty caseStudyFile.studentFilePath}">
																		<button id="edit" name="edit" class="btn btn-primary"  
																		formaction="${submitCaseUrl}" class="form-control"> Upload Case Study </button>
																</c:if>
																<c:if test = "${endDate > now && not empty caseStudyFile.studentFilePath}">
																		<button id="edit" name="edit" class="btn btn-primary"  
																		formaction="${submitCaseUrl}" class="form-control"> Re-Submit Case Study </button>
																</c:if>
															</c:if>
															<button id="back" name="back" class="btn btn-danger"  
																		formaction="caseStudy" class="form-control">Return</button>
													
													</div>
													</form>
													
													
													</div>
												
												
												
													<c:if test="${not empty caseStudyFile.studentFilePath}">
														<div class="col-md-8 column">
														<div>
															<b>Preview of Submitted File | 
																<a href="student/downloadStudentCaseStudyFile?filePath=${caseStudyFile.studentFilePath}">
																	<i class="fa-solid fa-download" aria-hidden="true"></i> 
																	Download Submitted File to cross check
																</a>
															</b>
														</div>
														<embed align="middle"
												src="<spring:eval expression="@propertyConfigurer.getProperty('CASESTUDY_PREVIEW_PATH')" />${caseStudyFile.previewPath}?id=<%=Math.random() %>"
												width="100%" height="820px">
														
													</c:if>
													
													
												
													
												</fieldset>
											</form:form>
											
										</div>
              								
              						</div>
              				</div>
              		
                            
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../common/footer.jsp"/>
   	<script type="text/javascript">
   	$('#subjectTimer').countdown({until: new Date('${caseStudyFile.endDate}'), format: 'dHMS'});
	$('#subjectTimer').countdown('toggle');
   	</script>
    </body>
</html>