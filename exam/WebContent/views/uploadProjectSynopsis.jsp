<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html lang="en">
    <jsp:include page="./common/jscss.jsp">
	<jsp:param value="Submit Synopsis" name="title"/>
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
    	<%@ include file="./common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="./common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Project Synopsis Submission" name="breadcrumItems"/>
			</jsp:include>
        	
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="./common/left-sidebar.jsp">
								<jsp:param value="Synopsis Submission" name="activeMenu"/>
							</jsp:include>
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="./common/studentInfoBar.jsp" %>
              						<div class="sz-content">
              							<div class="panel panel-default">
	              							<div class="panel-body">
	              							<%@ include file="./common/messages.jsp" %>
	              							<c:if test="${ levelBasedSynopsisConfigBean != null }">
	              							<h2 class="red text-capitalize">PROJECT SYNOPSIS SUBMISSION</h2>
	              							<div class="clearfix"></div>
												<div class="col-md-4 column">
													<div id="subjectTimer" class="exam-assg-timer"></div>
													<div class="clearfix"></div>
													<hr/> 
													<p><b>Guide: </b>  ${ resultUploadProjectBean.facultyName }</p>
													<p><b>Start Date: </b>  ${ levelBasedSynopsisConfigBean.start_date } </p>
													<p><b>End Date: </b>  ${ levelBasedSynopsisConfigBean.end_date }</p>
													
													<hr/>
													<div>
														<p style="margin-top: 10px; margin-bottom: 0px;"><b>Synopsis
															Question File</b></p>
														<h4 style="margin: 0px;">  
															<a href="<spring:eval expression="@propertyConfigurer.getProperty('SYNOPSIS_FILES_PATH')" />${levelBasedSynopsisConfigBean.question_filePath}">Download</a>
														</h4> 
													</div>
													<hr/>
													<input type="hidden" name="" value=""/>
													<c:if test="${ levelBasedSynopsisConfigBean.payment_applicable == 'Y' }">
														<p><b>Fees: </b>  ${ levelBasedSynopsisConfigBean.payment_amount }</p>
													</c:if>
													<p><b>Attempts : </b>  ${ resultUploadProjectBean.attempt } / ${ levelBasedSynopsisConfigBean.max_attempt }</p>
													<p><b>Status: </b>  ${ resultUploadProjectBean.status == null ? 'Pending' : resultUploadProjectBean.status }</p>
													<c:if test="${ resultUploadProjectBean.status == 'Rejected' }">
														<p><b>Reason: </b>  ${ resultUploadProjectBean.reason }</p>
													</c:if>
													
													<div class="clearfix"></div>
													<hr/>
														<c:if test="${ canSubmit }">
														<form id="uploadProjectSynopsis" action="uploadProjectSynopsis" method="POST" enctype="multipart/form-data">
															<div class="form-group">
															<label>Please attach your Synopsis file in .pdf format</label>
															   <p>
																		<a
																			href="http://www.wikihow.com/Convert-a-Microsoft-Word-Document-to-PDF-Format"
																			target="_blank">How to convert word file 
																			<i class="fa-regular fa-file-word fa-lg"></i> into pdf 
																			<i class="fa-solid fa-file-pdf fa-lg"></i> file?
																		</a>
																	</p>
																<input type="hidden" name="subject" value="${ resultUploadProjectBean.subject }"/>
															    <input type="file" style="color:black" name="fileData" class="form-control-file" id="fileData">
															</div>
															<button type="button" class="btn btn-primary btn-sm synopsis_submit_btn">submit</button>
														</form>
														<p align="justify" style="margin-top:10px;">
															<b>Note:</b> The file size should not exceed 10 MB.	
															Please use Chrome to avoid any Browser compatibility issue.				
														</p>
														</c:if>
													
												</div>
												<div class="col-md-8">
													<c:if test="${resultUploadProjectBean.attempt > 0 }">
													<embed align="middle"
													src="<spring:eval expression="@propertyConfigurer.getProperty('SUBMITTED_SYNOPSIS_FILES_PATH')" />${resultUploadProjectBean.previewPath}"
													width="100%" height="820px">
													</c:if>
												</div>
												</c:if>
											</div>
											
										</div>
              						</div>
              				</div>
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="./common/footer.jsp"/>
            
		<script>
			console.log(new Date('${levelBasedSynopsisConfigBean.end_date}'));
			$('#subjectTimer').countdown({until: new Date('${levelBasedSynopsisConfigBean.end_date}'), format: 'dHMS'});
			$('#subjectTimer').countdown('toggle');
			
			$(document).ready(function(){
				$('.synopsis_submit_btn').click(function(e){
					e.preventDefault();
					if($('#fileData').val() == ""){
						alert("Please choose synopsis file first");
						return false;
					}
					if(confirm("Click `OK` to submit synopsis")){
						$('#uploadProjectSynopsis').submit();	
					}
				});
			});
		</script>
		
    </body>
</html>