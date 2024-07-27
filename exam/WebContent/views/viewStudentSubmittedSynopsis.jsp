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
              				<jsp:include page="./adminCommon/left-sidebar.jsp">
								<jsp:param value="Synopsis Submission" name="activeMenu"/>
							</jsp:include>
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="./common/studentInfoBar.jsp" %>
              						<div class="sz-content">
              							<br/>
              							<div class="panel panel-default">
	              							<div class="panel-body">

	              							<%@ include file="./common/messages.jsp" %>
	              							<c:if test="${ uploadProjectSynopsisResultBean != null }">
	              							<h2 class="red text-capitalize">PROJECT Synopsis SUBMISSION</h2>
	              							<div class="clearfix"></div>
												<div class="col-md-4 column">
													<form method="POST" id="updateStudentSubmittedSynopsisStatus" action="updateStudentSubmittedSynopsisStatus">
													<div class="clearfix"></div>
													<table class="table">
														<tr>
															<th>Student Id</th>
															<td>${ uploadProjectSynopsisResultBean.sapid }</td>
														</tr>
														<tr>
															<th>Year</th>
															<td>${ uploadProjectSynopsisResultBean.year }</td>
														</tr>
														<tr>
															<th>Month</th>
															<td>${ uploadProjectSynopsisResultBean.month }</td>
														</tr>
													</table><hr/> 
													<input type="hidden" name="sapid" value="${ uploadProjectSynopsisResultBean.sapid }" />
													<input type="hidden" name="facultyId" value="${ uploadProjectSynopsisResultBean.facultyId }"/>
													<input type="hidden" name="id" value="${ uploadProjectSynopsisResultBean.id }" />
													<input type="hidden" name="year" value="${ uploadProjectSynopsisResultBean.year }" />
													<input type="hidden" name="month" value="${ uploadProjectSynopsisResultBean.month }" />
													<c:if test="${ uploadProjectSynopsisResultBean.evaluationDate != null }">
														<div class="form-group">
															<label>Last Evaluated Date: ${ uploadProjectSynopsisResultBean.evaluationDate }</label>
														</div>
														<div class="form-group">
															<label> Evaluated Count: ${uploadProjectSynopsisResultBean.evaluationCount }</label>  
														</div> 
													</c:if>
													<div class="form-group">
														<label> Evaluated: ${uploadProjectSynopsisResultBean.evaluated }</label>  
													</div>  
													<div class="form-group">
													  <label for="score">Marks:</label>
													  <input type="number" class="form-control" id="score" name="score" value="${ uploadProjectSynopsisResultBean.score }" />
													</div>
													<div class="form-group">
													  <label for="comment">Reason:</label>
													  <textarea name="reason" class="form-control" rows="5" id="comment">${ uploadProjectSynopsisResultBean.reason }</textarea>
													</div>
													<button class="btn btn-primary">Submit</button>
													<div class="clearfix"></div>	
													
													</form>
													
													
												</div>
												<div class="col-md-8">
													
													<embed align="middle"
													src="<spring:eval expression="@propertyConfigurer.getProperty('SUBMITTED_SYNOPSIS_FILES_PATH')" />${ uploadProjectSynopsisResultBean.previewPath }?id=<%=Math.random() %>"
													width="100%" height="820px">
													
												</div>
												</c:if>
											</div>
											
										</div>
              						</div>
              				</div>
					</div>
            </div>
        </div>
            
  	
        <jsp:include page="./adminCommon/footer.jsp"/>
            
		<script>
			$(document).ready(function(){ 
				$('#updateStudentSubmittedSynopsisStatus').submit(function(e){
					e.preventDefault();  
					let marks = $('#score').val();
					if(!marks){
						alert("marks input required");
						return false;
					} 
					if(parseFloat(marks) < 0){
						alert("marks cannot be less than 0");
						return false;
					}
					if(parseFloat(marks) > 25){
						alert("marks cannot be greater than 25");
						return false;
					}
					e.currentTarget.submit();
				});
			});
		</script>
    </body>
</html>