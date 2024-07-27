<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html lang="en">
    <jsp:include page="./common/jscss.jsp">
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
    	<%@ include file="./common/header.jsp" %>
        
        <div class="sz-main-content-wrapper">
        
        	<jsp:include page="./common/breadcrum.jsp">
			<jsp:param value="Student Zone;Exams;Project SOP Submission" name="breadcrumItems"/>
			</jsp:include>
        	
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				<jsp:include page="./adminCommon/left-sidebar.jsp">
								<jsp:param value="SOP Submission" name="activeMenu"/>
							</jsp:include>
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="./common/studentInfoBar.jsp" %>
              						<div class="sz-content">
              							<br/>
              							<div class="panel panel-default">
	              							<div class="panel-body">

	              							<%@ include file="./common/messages.jsp" %>
	              							<c:if test="${ uploadProjectSOPResultBean != null }">
	              							<h2 class="red text-capitalize">PROJECT SOP SUBMISSION</h2>
	              							<div class="clearfix"></div>
												<div class="col-md-4 column">
													<form selected="selected" id="SOPSubmittedForm" method="POST" action="updateStudentSubmittedSOPStatus">
													<div class="clearfix"></div>
													<table class="table">
														<tr>
															<th>Student Id</th>
															<td>${ uploadProjectSOPResultBean.sapId }</td>
														</tr>
														<tr>
															<th>Year</th>
															<td>${ uploadProjectSOPResultBean.year }</td>
														</tr>
														<tr>
															<th>Month</th>
															<td>${ uploadProjectSOPResultBean.month }</td>
														</tr>
													</table>
													<input type="hidden" name="sapId" value="${ uploadProjectSOPResultBean.sapId }" />
													<input type="hidden" name="facultyId" value="${ uploadProjectSOPResultBean.facultyId }"/>
													<input type="hidden" name="year" value="${ uploadProjectSOPResultBean.year }" />
													<input type="hidden" name="month" value="${ uploadProjectSOPResultBean.month }" />
													<div class="form-group">
													  <label for="sel1">Select status:</label>
													  <select name="status" class="form-control" id="sel1">
													    <option value="Approved" ${ uploadProjectSOPResultBean.status == "Approved" ? "selected" : "" }>Approved</option>
													    <option value="Rejected" ${ uploadProjectSOPResultBean.status == "Rejected" ? "selected" : "" }>Rejected</option>
													  </select>
													</div>
													<div class="form-group">
													  <label for="comment">Reason:</label>
													  <textarea name="reason" class="form-control" rows="5" id="comment">${ uploadProjectSOPResultBean.reason }</textarea>
													</div>
													<c:choose>
													 <c:when test="${isEndDateExpired == 'Y'}">
														<button class="btn btn-primary" disabled="disabled" data-toggle="tooltip" data-placement="bottom" title="Cannot update status Synopsis End Date has Expired !"
														onclick="if(confirm('Are you sure you want to udpate the status ?')) { $('#SOPSubmittedForm').prop('selected', true);}else{ return false; } ">Submit</button>
														<div class="clearfix"></div>	
														</c:when>
													 <c:otherwise>
															<button class="btn btn-primary" onclick="if(confirm('Are you sure you want to udpate the status ?')) { $('#SOPSubmittedForm').prop('selected', true);}else{ return false; } ">Submit</button>
															<div class="clearfix"></div>
													 </c:otherwise>
													</c:choose>
													</form>
													
													
												</div>
												<div class="col-md-8">
													
													<embed align="middle"
													src="<spring:eval expression="@propertyConfigurer.getProperty('SUBMITTED_SOP_FILES_PATH')" />${ uploadProjectSOPResultBean.previewPath }?id=<%=Math.random() %>"
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
	<script type="text/javascript">
	$(function () {
		  $('[data-toggle="tooltip"]').tooltip()
		})
	</script>
    </body>
</html>