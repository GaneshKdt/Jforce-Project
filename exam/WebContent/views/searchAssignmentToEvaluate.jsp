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

<jsp:include page="jscss.jsp">
<jsp:param value="Search Assignments for Evaluation" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Assignments for Evaluation</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="searchAssignmentSubmission" method="post" modelAttribute="searchBean" role="form">
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
							<form:select id="subject" path="subject"  class="combobox form-control"   itemValue="${searchBean.subject}">
								<form:option value="" selected="selected">Type OR Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>
					
					
					
					<%if(roles.indexOf("Faculty") != -1  || roles.indexOf("Insofe") != -1 ){ %>
						<div class="form-group">
							<form:select id="evaluated" path="evaluated"  class="form-control" itemValue="${searchBean.evaluated}">
								<form:option value="">Select Evaluation Status</form:option>
								<form:option value="Y">Evaluated</form:option>
								<form:option value="N">Not Evaluated</form:option>
							</form:select>
						</div>	
						
					
					<%}else if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
						<div class="form-group">
							<form:select id="evaluated" path="evaluated"  class="form-control" itemValue="${searchBean.evaluated}">
								<form:option value="">Select Level 1 Evaluation Status</form:option>
								<form:option value="Y">Evaluated</form:option>
								<form:option value="N">Not Evaluated</form:option>
							</form:select>
						</div>	
						
						<div class="form-group">
							<form:select id="faculty2Evaluated" path="faculty2Evaluated"  class="form-control" itemValue="${searchBean.faculty2Evaluated}">
								<form:option value="">Select Level 2 Evaluation Status</form:option>
								<form:option value="Y">Evaluated</form:option>
								<form:option value="N">Not Evaluated</form:option>
							</form:select>
						</div>	
						
						<div class="form-group">
							<form:select id="faculty3Evaluated" path="faculty3Evaluated"  class="form-control" itemValue="${searchBean.faculty3Evaluated}">
								<form:option value="">Select Level 3 Evaluation Status</form:option>
								<form:option value="Y">Evaluated</form:option>
								<form:option value="N">Not Evaluated</form:option>
							</form:select>
						</div>	
					<%} %>
			</div>
			
			
			
			<div class="col-md-6 column">
					
					
					
					<%-- <div class="form-group">
						<form:select id="revisited" path="revisited"  class="form-control" itemValue="${searchBean.revisited}">
							<form:option value="">Select Revisited Status</form:option>
							<form:option value="Y">Revisited</form:option>
							<form:option value="N">Not Revisited</form:option>
						</form:select>
					</div>	 --%>
					
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
							<form:option value="Other subject Assignment">Other subject Assignment</form:option>
							<form:option value="Scanned/Handwritten assignment">Scanned/Handwritten assignment</form:option>
							<form:option value="Only Questions written">Only Questions written/Question Paper Uploaded</form:option>
							<form:option value="Blank Assignment">Blank Assignment</form:option>
							<form:option value="Corrupt file uploaded">Corrupt file uploaded</form:option>
						</form:select>
						</div>
					
					<div class="form-group">
							<form:input id="sapId" path="sapId" type="text" placeholder="SAP ID" class="form-control" value="${searchBean.sapId}"/>
					</div>	 
						
					<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
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
							<button id="submit" name="submit" class="btn btn-primary" formaction="searchAssignmentToEvaluate">Search Submissions</button>
							<%if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
								<c:if test="${rowCount > 0 && searchBean.evaluated=='N'}">
									<button id="submit" name="submit" class="btn btn-primary" formaction="sendEvaluationReminderEmail?level=1" onclick="return confirm('Are you sure you want to send Email Reminders for below Search Results record?');">L1 Reminder</button>
								</c:if>
								<c:if test="${rowCount > 0 && searchBean.faculty2Evaluated =='N'}">
									<button id="submit" name="submit" class="btn btn-primary" formaction="sendEvaluationReminderEmail?level=2" onclick="return confirm('Are you sure you want to send Email Reminders for below Search Results record?');">L2 Reminder</button>
								</c:if>
							<%} %>
							<button id="cancel" name="cancel" class="btn btn-danger " formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>
			</div>
			
			
			<c:if test="${pendingEvaluations > 0}">
				<div class="col-md-6 column">
				<h2>${pendingEvaluations} Assignment Evaluations Pending</h2>
				</div>
			</c:if>

		</fieldset>
		</form:form>
		
		
		
		</div>
	
	<hr style="background-color: rgb(228, 209, 209); height: 1px; border: 0;">
	<c:choose>
	<c:when test="${rowCount > 0}">

	<h2>&nbsp;Submission Details <font size="2px"> (${rowCount} Records Found)&nbsp; 
	<%if(roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Exam Admin") != -1){ %>
		<a href="downloadAssignmentEvaluatedExcel">Download Evaluation Excel</a>
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
								<th>Program Name</th>
								<th>Faculty ID</th>
								<th>Faculty Name</th>
								<th>Evaluated</th>
								<th>Evaluation Date</th> 
								<%if(roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Exam Admin") != -1){ %>
								<th>Evaluation Count</th>
								<%} %>
								<!-- <th>File</th> -->
								<%if(roles.indexOf("Faculty") != -1  || roles.indexOf("Insofe") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
								<th>Actions</th>
								<%} %>
							</tr>
						</thead>
						<tbody>
						
						<c:forEach var="assignmentFile" items="${assignmentFilesList}" varStatus="status">
					        <tr>
					            <td><c:out value="${status.count}"/></td>
								<td><c:out value="${assignmentFile.year}"/></td>
								<td><c:out value="${assignmentFile.month}"/></td>
								<td nowrap="nowrap"><c:out value="${assignmentFile.subject}"/></td>
								<td><c:out value="${assignmentFile.sapId}"/></td>
								<td><c:out value="${assignmentFile.program}"/></td>
								<td><c:out value="${assignmentFile.facultyId}"/></td>
								<td><c:out value="${assignmentFile.firstName}"/> <c:out value="${assignmentFile.lastName}"/></td> 
								<td>
									<c:if test="${empty assignmentFile.evaluated}">N</c:if>
									<c:if test="${not empty assignmentFile.evaluated}">
									<c:out value="${assignmentFile.evaluated}"/>
									</c:if>
								</td>
								<td><c:out value="${assignmentFile.evaluationDate}"/></td>
								
								
								<%if(roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Exam Admin") != -1){ %>
								<td nowrap="nowrap">
									<c:url value="changeEvaluationCount" var="changeEvaluationCountURL">
									  <c:param name="year" value="${assignmentFile.year}" />
									  <c:param name="month" value="${assignmentFile.month}" />
									  <c:param name="subject" value="${assignmentFile.subject}" />
									  <c:param name="sapId" value="${assignmentFile.sapId}" />
									  <c:param name="facultyId" value="${assignmentFile.facultyId}" />
									</c:url>
									
								<a href="#" class="editable" id="evaluationCount" data-type="text" data-pk="${assignment.year}" data-url="${changeEvaluationCountURL}" data-title="Change Evaluation Count">${assignmentFile.evaluationCount}</a>
								</td>
								<%} %>
								
								
								<td> 
						            <c:url value="evaluateAssignmentForm" var="evaluateurl">
									  <c:param name="year" value="${assignmentFile.year}" />
									  <c:param name="month" value="${assignmentFile.month}" />
									  <c:param name="subject" value="${assignmentFile.subject}" />
									  <c:param name="sapId" value="${assignmentFile.sapId}" />
									  <c:param name="programName" value="${assignmentFile.program}" />
									</c:url>

									<%if(roles.indexOf("Faculty") != -1  || roles.indexOf("Insofe") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
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

<c:url var="firstUrl" value="searchAssignmentToEvaluatePage?pageNo=1" />
<c:url var="lastUrl" value="searchAssignmentToEvaluatePage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchAssignmentToEvaluatePage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchAssignmentToEvaluatePage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="searchAssignmentToEvaluatePage?pageNo=${i}" />
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

	  <jsp:include page="footer.jsp" />
</body>
<script>
$(document).ready(function() {
    //toggle `popup` / `inline` mode
    $.fn.editable.defaults.mode = 'inline';     
    $('.editable').each(function() {
        $(this).editable();
    });
});
</script>
</html>
