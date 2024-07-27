<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html lang="en">
    <jsp:include page="../../common/jscss.jsp">
	<jsp:param value="Submit SOP" name="title"/>
    </jsp:include>
    <style>
    	.pending_block{
    		padding:8px 15px;
    		background-color:golden;
    		color:white;
    	}
    	.rejected_block{
    		padding:8px 15px;
    		background-color:red;
    		color:white;
    	}
    	.success_block{
    		padding:8px 15px;
    		background-color:green;
    		color:white;
    	}
    </style>
    <body>
    	<%@ include file="../../common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="../../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Project SOP Submission" name="breadcrumItems"/>
			</jsp:include>
        	
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="../../common/left-sidebar.jsp">
								<jsp:param value="SOP Submission" name="activeMenu"/>
							</jsp:include>
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="../../common/studentInfoBar.jsp" %>
              						<div class="sz-content">
              							<div class="panel panel-default">
	              							<div class="panel-body">
	              							<%@ include file="../../common/messages.jsp" %>
	              							<%if(!"true".equals( (String)request.getAttribute("error"))) { %>
	              							<c:if test="${(projectStatus.canSubmit == 'N' || projectStatus.sopStatus.allowSubmission == 'false') && projectStatus.cantSubmitError != null && projectStatus.cantSubmitError != ''}">
	              								<div class="alert alert-danger">
	              									${ projectStatus.cantSubmitError }
	              								</div>
											</c:if>
										
	              							<c:if test="${ projectStatus != null && projectStatus.canSubmit == 'Y' }">
	              							<h2 class="red text-capitalize">Project SOP Submission</h2>
	              							<div class="clearfix"></div>
												<div class="col-md-4 column">
													<div id="subjectTimer" class="exam-assg-timer"></div>
													<div class="clearfix"></div>
													<hr/>
													<p><b>Guide: </b>  ${ projectStatus.sopStatus.facultyName }</p>
													<p><b>Start Date: </b>  ${ projectStatus.sopStatus.startDate } </p>
													<p><b>End Date: </b>  ${ projectStatus.sopStatus.endDate }</p>
													<hr/>
													<!-- <div>
														<p style="margin-top: 10px; margin-bottom: 0px;"><b>SOP Question File</b></p>
														<h4 style="margin: 0px;">
															<a href="downloadStudentAssignmentFile?filePath">Download</a>
														</h4>
													</div> -->
													<hr/>
													<p><b>Attempts : </b>  ${ projectStatus.sopStatus.submissionsMade } / ${ projectStatus.sopStatus.maxSubmissions }</p>
													<p><b>Status: </b>  ${ projectStatus.sopStatus.status == null ? 'Pending' : projectStatus.sopStatus.status }</p>
													<c:if test="${ projectStatus.sopStatus.status == 'Rejected' }">
														<p><b>Reason: </b>  ${ projectStatus.sopStatus.reason }</p>
													</c:if>
													<c:if test="${ projectStatus.sopStatus.status == 'Submitted' && projectStatus.sopStatus.reason != '' && projectStatus.sopStatus.reason != null }">
														<p><b>Reason: </b>  ${ projectStatus.sopStatus.reason }</p>
													</c:if>
													
													<div class="clearfix"></div>
													<hr/>
														<c:if test="${ projectStatus.canSubmit == 'Y' && projectStatus.sopStatus.allowSubmission == 'true' }">
															<form id="uploadProjectSOP" action="uploadProjectSOP" method="POST" enctype="multipart/form-data">
																<div class="form-group">
																<label>Please attach your SOP file in .pdf format</label>
																   <p>
																		<a
																			href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format"
																			target="_blank">How to convert word file 
																			<i class="fa-regular fa-file-word fa-lg"></i> into pdf 
																			<i class="fa-solid fa-file-pdf fa-lg"></i> file?
																		</a>
																	</p>
																	<input type="hidden" name="subject" value="${ projectStatus.subject }"/>
																    <input type="file" style="color:black" name="fileData" class="form-control-file" id="fileData" accept=".pdf">
																</div>
																<button type="button" class="btn btn-primary btn-sm sop_submit_btn">submit</button>   
															</form>
															<p align="justify" style="margin-top:10px;">
																<b>Note:</b> The file size should not exceed 10 MB.	
																Please use Chrome to avoid any Browser compatibility issue.				
															</p>
														</c:if>
													
												</div>
												<div class="col-md-8">
													<c:if test="${projectStatus.sopStatus.submissionsMade > 0 }">
													<embed align="middle"
													src="<spring:eval expression="@propertyConfigurer.getProperty('SUBMITTED_SOP_FILES_PATH')"/>${projectStatus.sopStatus.submittedFilePath}"
													width="100%" height="820px">        
													</c:if>   
												</div>
												</c:if>
												<%} %>
											</div>
											
										</div>
              						</div>
              				</div>
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="../../common/footer.jsp"/>
        <%if(!"true".equals( (String)request.getAttribute("error"))) { %>
        <c:if test="${ projectStatus != null && projectStatus.canSubmit == 'Y' }">    
		<script>
			console.log(new Date('${projectStatus.sopStatus.endDate}'));
			$('#subjectTimer').countdown({until: new Date('${projectStatus.sopStatus.endDate}'), format: 'dHMS'});
			$('#subjectTimer').countdown('toggle');
			$(document).ready(function(){
				$('.sop_submit_btn').click(function(e){
					e.preventDefault();
					if($('#fileData').val() == ""){
						alert("Please choose SOP file first");
						return false;
					}
					if(confirm("Click `OK` to submit SOP")){
						$('#uploadProjectSOP').submit();	
					}
				});
			});
		</script>
		</c:if>
		<% } %>
    </body>
</html>