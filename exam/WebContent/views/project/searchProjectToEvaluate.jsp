<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
<jsp:param value="Search Projects for Evaluation" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Projects for Evaluation</legend></div>
        <%@ include file="../messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="searchProjectToEvaluate" method="post" modelAttribute="searchBean" role="form">
			<fieldset>
				<div class="col-md-6 column">

				
					<div class="form-group">
						<form:select id="year" path="year"  class="form-control" required="required"  itemValue="${searchBean.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${searchBean.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group" style="overflow:visible;">
							<form:select id="program" path="program"  class="combobox form-control"   itemValue="${searchBean.program}">
								<form:option value="" selected="selected">Type OR Select Program</form:option>
								<form:options items="${programList}" />
							</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="evaluated" path="evaluated"  class="form-control" itemValue="${searchBean.evaluated}">
							<form:option value="">Select Evaluation Status</form:option>
							<form:option value="Y">Evaluated</form:option>
							<form:option value="N">Not Evaluated</form:option>
						</form:select>
					</div>	
					
				<div class="form-group">
						<form:select id="revaluated" path="revaluated"  class="form-control" itemValue="${searchBean.revaluated}">
							<form:option value="">Select Revaluation Status</form:option>
							<form:option value="Y">Revaluated</form:option>
							<form:option value="N">Not Revaluated</form:option>
						</form:select>
					</div> 
			</div>
			
			<div class="col-md-6 column">
					
					<div class="form-group">
						<form:select id="reason" path="reason"  class="form-control"   itemValue="${searchBean.reason}">
							<form:option value="">Select Reason</form:option>
							<form:option value="Excellent">Excellent</form:option>
							<form:option value="Very Good">Very Good</form:option>
							<form:option value="Good">Good</form:option>
							<form:option value="Average">Average</form:option>
							<form:option value="Below Average">Below Average</form:option>
							<form:option value="Copy Case-Internet/Course Book">Copy Case (Internet/Course Book)</form:option>
							<form:option value="Copy Case-Other Student">Copy Case (Other student/s)</form:option>
							<form:option value="Wrong Answer"> Wrong Answer/s</form:option>
							<form:option value="Other subject File">Other subject File</form:option>
							<form:option value="Scanned/Handwritten Project">Scanned/Handwritten Project</form:option>
							<form:option value="Only Questions written">Only Questions written/Question Paper Uploaded</form:option>
							<form:option value="Blank Project">Blank Project</form:option>
							<form:option value="Corrupt file uploaded">Corrupt file uploaded</form:option>
						</form:select>
						</div>
					
					<div class="form-group">
							<form:input id="sapId" path="sapId" type="text" placeholder="SAP ID" class="form-control" value="${searchBean.sapId}"/>
					</div>	 
						
					<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("TEE Admin") != -1){ %>
						 <div class="form-group">
								<form:input id="facultyId" path="facultyId" type="text" placeholder="Faculty ID" class="form-control" value="${searchBean.facultyId}"/>
						</div>	 
						
						
						<div class="form-group">
							<form:select id="revaluated" path="revaluated"  class="form-control" itemValue="${searchBean.revaluated}">
								<form:option value="">Select Revaluation Status</form:option>
								<form:option value="Y">Revaluation Complete</form:option>
								<form:option value="N">Revaluation Pending</form:option>
							</form:select>
						</div>	
						
						<div class="form-group">
							<form:select id="markedForRevaluation" path="markedForRevaluation"  class="form-control" itemValue="${searchBean.markedForRevaluation}">
								<form:option value="">Select Opted for Revaluation Status</form:option>
								<form:option value="Y">Students Opted for revaluation</form:option>
								<form:option value="N">Students Not Opted for revaluation</form:option>
							</form:select>
						</div>	
					
					<%} %>
					
					<div class="control-group">
						<div class="form-group">
							<button id="submit" name="submit" class="btn btn-primary" formaction="searchProjectToEvaluate">Search Submissions</button>
							<button id="cancel" name="cancel" class="btn btn-danger " formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>

			</div>
			
			<c:if test="${pendingEvaluations > 0}">
				<div class="col-md-6 column">
				<h2>${pendingEvaluations} Project Evaluations Pending</h2>
				</div>
			</c:if>

		</fieldset>
		</form:form>
		
		
		</div>
	
	<hr style="background-color: rgb(228, 209, 209); height: 1px; border: 0;">
	<c:choose>
	<c:when test="${rowCount > 0}">

	<h2>&nbsp;Submission Details <font size="2px"> (${rowCount} Records Found)&nbsp; 
	<%if(roles.indexOf("Assignment Admin") != -1 || roles.indexOf("TEE Admin") != -1 || roles.indexOf("Acads Admin") != -1 || roles.indexOf("Exam Admin") != -1){ %>
<a href="downloadProjectEvaluatedExcel">Download Evaluation Excel</a>
<%} %>
		
	
	</font></h2>
	<div class="panel-body table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Subject</th>
								<th>Student ID</th>
								<th>Faculty ID</th>
								<th>Faculty Name</th>
								<th>Evaluated</th>
								<th>Re-Evaluated</th>
								<th>Evaluation Date</th>
								<%if(roles.indexOf("TEE Admin") != -1 || roles.indexOf("Exam Admin") != -1){ %>
								<th>Evaluation Count</th>
								<%} %>
								<!-- <th>File</th> -->
								<th>Actions</th>
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="projectFile" items="${projectFilesList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
								<td><c:out value="${projectFile.year}"/></td>
								<td><c:out value="${projectFile.month}"/></td>
								<td nowrap="nowrap"><c:out value="${projectFile.subject}"/></td>
								<td><c:out value="${projectFile.sapId}"/></td>
								<td><c:out value="${projectFile.facultyId}"/></td>
								<td><c:out value="${projectFile.firstName}"/> <c:out value="${projectFile.lastName}"/></td> 
								<td>
									<c:if test="${empty projectFile.evaluated}">N</c:if>
									<c:if test="${not empty projectFile.evaluated}">
									<c:out value="${projectFile.evaluated}"/>
									</c:if>
								</td>
								<td>
									<c:if test="${empty projectFile.revaluated}">N</c:if>
									<c:if test="${not empty projectFile.revaluated}">
									<c:out value="${projectFile.revaluated}"/>
									</c:if>
								</td>
								<td><c:out value="${projectFile.evaluationDate}"/></td>
								
								
								<%if(roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Exam Admin") != -1){ %>
								<td nowrap="nowrap">
									<c:url value="changeProjectEvaluationCount" var="changeEvaluationCountURL">
									  <c:param name="year" value="${projectFile.year}" />
									  <c:param name="month" value="${projectFile.month}" />
									  <c:param name="subject" value="${projectFile.subject}" />
									  <c:param name="sapId" value="${projectFile.sapId}" />
									</c:url>
									
								<a href="#" class="editable" id="evaluationCount" data-type="text" data-pk="${projectFile.year}" data-url="${changeEvaluationCountURL}" data-title="Change Evaluation Count">${projectFile.evaluationCount}</a>
								</td>
								<%} %>
								
								
								<td> 
						            <c:url value="evaluateProjectForm" var="evaluateurl">
									  <c:param name="year" value="${projectFile.year}" />
									  <c:param name="month" value="${projectFile.month}" />
									  <c:param name="subject" value="${projectFile.subject}" />
									  <c:param name="sapId" value="${projectFile.sapId}" />
									</c:url>

									<%if(roles.indexOf("Faculty") != -1  || roles.indexOf("Insofe") != -1 ){ %>
									  <a href="${evaluateurl}" title="Evaluate"><i class="fa-regular fa-square-check fa-lg"></i></a> 
									<%} %>
										
						         </td>
						            
					        </tr>   
					    </c:forEach>

						</tbody>
					</table>
	</div>
	<br>

</c:when>
</c:choose>

<c:url var="firstUrl" value="searchProjectToEvaluatePage?pageNo=1" />
<c:url var="lastUrl" value="searchProjectToEvaluatePage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchProjectToEvaluatePage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchProjectToEvaluatePage?pageNo=${page.currentIndex + 1}" />


<c:choose>
<c:when test="${page.totalPages > 1}">
<div align="center">
    <ul class="pagination">
        <c:choose>
            <c:when test="${page.currentIndex == 1}">
                <li class="disabled"><a href="#">&lt;&lt;</a></li>
                <li class="disabled"><a href="#">&lt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${firstUrl}">&lt;&lt;</a></li>
                <li><a href="${prevUrl}">&lt;</a></li>
            </c:otherwise>
        </c:choose>
        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
            <c:url var="pageUrl" value="searchProjectToEvaluatePage?pageNo=${i}" />
            <c:choose>
                <c:when test="${i == page.currentIndex}">
                    <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <c:choose>
            <c:when test="${page.currentIndex == page.totalPages}">
                <li class="disabled"><a href="#">&gt;</a></li>
                <li class="disabled"><a href="#">&gt;&gt;</a></li>
            </c:when>
            <c:otherwise>
                <li><a href="${nextUrl}">&gt;</a></li>
                <li><a href="${lastUrl}">&gt;&gt;</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</div>
</c:when>
</c:choose>



					
	</section>

	  <jsp:include page="../footer.jsp" />
<script>
$(document).ready(function() {
    //toggle `popup` / `inline` mode
    $.fn.editable.defaults.mode = 'inline';     
    
    /* //make username editable
    $('#score').editable();
    
    //make username editable
    $('#remarks').editable(); */
    
    $('.editable').each(function() {
        $(this).editable();
    });
    
});
</script>



</body>
</html>
