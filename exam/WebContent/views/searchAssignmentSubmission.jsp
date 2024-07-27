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
<jsp:param value="Search Assignment Submissions" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Search Assignment Submissions</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="row clearfix">
		<form:form  action="searchAssignmentSubmission" method="post" modelAttribute="searchBean">
			<fieldset>
				<div class="col-md-6 column">

				
					<div class="form-group">
						<form:select id="acadYear" path="acadYear" type="text"	placeholder="Academic Year" class="form-control durationFields">
							<form:option value="">Select Academic Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
										<div class="form-group">
						<form:select id="acadMonth" path="acadMonth" type="text" placeholder="Academic Month" class="form-control durationFields">
							<form:option value="">Select Academic Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Jul">Jul</form:option>
							
						</form:select>
					</div>
					
					
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control durationFields" required="required">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control durationFields" required="required">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					<%-- <div class="form-group" style="overflow:visible;">
							<form:select id="subject" path="subject" class="combobox form-control"   itemValue="${searchBean.subject}">
								<form:option value="">Type OR Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div> --%>				
					<div class="form-group">
			            <select data-id="consumerTypeDataId" id="consumerTypeId" name="consumerTypeId"  class="selectConsumerType form-control" required="required" >
			             <option disabled selected value="">Select Consumer Type</option>
			             <c:forEach var="consumerType" items="${consumerType}">
			              <c:choose>
			               <c:when test="${consumerType.id == searchBean.consumerTypeId}">
			                <option selected value="<c:out value="${consumerType.id}"/>">
			                              <c:out value="${consumerType.name}"/>
			                            </option>
			               </c:when>
			               <c:otherwise>
			                <option value="<c:out value="${consumerType.id}"/>">
			                              <c:out value="${consumerType.name}"/>
			                            </option>
			               </c:otherwise>
			              </c:choose>
			     
			                      </c:forEach>
			            </select>
			          </div>
			          <div class="form-group">
			            <select id="programStructureId" name="programStructureId"  class="selectProgramStructure form-control"  >
			             <option disabled selected value="">Select Program Structure</option>
			            </select>
			          </div>
			          <div class="form-group">
			            <select id="programId" name="programId"  class="selectProgram form-control" >
			             <option disabled selected value="">Select Program</option>
			            </select>
			          </div> 
          
				       <div class="form-group">
					      <select id="subjectId" name="subject"  class="selectSubject form-control" >
					       <option disabled selected value="">Select Subject</option>
					      </select>
					    </div>   
					
					<div class="form-group">
							<form:input id="sapId" path="sapId" type="text" placeholder="SAP ID" class="form-control" value="${searchBean.sapId}"/>
					</div>	


				

			

			</div>
			<div class="col-md-6 column">

						<div class="form-group">
							<button id="submit" name="submit" class="btn btn-large btn-primary btn-block" formaction="searchAssignmentSubmission">Search Submissions</button>
							<button id="submit" name="submit" class="btn btn-large btn-primary btn-block" formaction="findANSListForEmail">Search ANS List</button>
							<button id="cancel" name="cancel" class="btn btn-danger btn-block" formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
			</div>
			
			<%if(roles.indexOf("SR Admin") == -1 || roles.indexOf("Learning Center") == -1){%>
			<div class="col-md-6 column">

						<div class="form-group">
							<button id="submit" name="submit" class="btn btn-large btn-primary btn-block"  formaction="searchANS">Download ANS Report</button>
							<button id="submit" name="submit" class="btn btn-large btn-primary btn-block" 
							 onclick="return confirm('Are you sure you want to initiate Email reminder to ANS list that matches search criteria?. Email will have names of subjets pending Submission.');" formaction="sendANSReminderEmail">Send Email Reminder</button>
							<button id="submit" name="submit" class="btn btn-large btn-primary btn-block" 
							 onclick="return confirm('Are you sure you want to initiate Email reminder to ANS list that matches search criteria?. It will be an Email with Fixed Content for All Students.');" formaction="sendGenericANSReminderEmail">Send Generic Email Reminder</button>
						</div>
			</div>
<%} %>

		</fieldset>
		</form:form>
		
		</div>
	
	
	<c:choose>
<c:when test="${rowCount > 0}">

	<legend>&nbsp;Assignment Details & Files<font size="2px"> (${rowCount} Records Found)&nbsp; <a href="downloadAssignmentSubmittedExcel">Download Submission Excel</a></font></legend>
	<div class="table-responsive">
	<table class="table table-striped table-hover" style="font-size:12px">
						<thead>
							<tr> 
								<th>Sr. No.</th>
								<th>Exam Year</th>
								<th>Exam Month</th>
								<th>Subject</th>
								<th>Student ID</th>
								<th>File</th>
								<%if(roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Exam Admin") != -1){ %>
								<th>Submission Count</th>
								<%} %>
								<th>Actions</th>
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
								<td><a href="<spring:eval expression="@propertyConfigurer.getProperty('ASSIGNMENT_PREVIEW_PATH')" />${assignmentFile.previewPath}" />Download</a></td>
								<%if(roles.indexOf("Assignment Admin") != -1 || roles.indexOf("Exam Admin") != -1){ %>
								<td nowrap="nowrap">
									<c:url value="changeSubmissionCount" var="changeSubmissionCountURL">
									  <c:param name="year" value="${assignmentFile.year}" />
									  <c:param name="month" value="${assignmentFile.month}" />
									  <c:param name="subject" value="${assignmentFile.subject}" />
									  <c:param name="sapId" value="${assignmentFile.sapId}" />
									  
									</c:url>
									
								<a href="#" class="editable" id="submissionCount" data-type="text" data-pk="${assignment.year}" data-url="${changeSubmissionCountURL}" data-title="Change Submission Count">${assignmentFile.attempts}</a>
								</td>
								<%} %>
								<td> 
						            <c:url value="editAssignmentFileForm" var="editurl">
									  <c:param name="year" value="${assignmentFile.year}" />
									  <c:param name="month" value="${assignmentFile.month}" />
									  <c:param name="subject" value="${assignmentFile.subject}" />
									</c:url>

									<%//if(roles.indexOf("Exam Admin") != -1 || roles.indexOf("Assignment Admin") != -1){ %>
									<%--  <a href="${editurl}" title="Edit"><i class="fa fa-pencil-square-o fa-lg"></i></a> --%>
									<%//} %>
										
						         </td>
						            
					        </tr>   
					    </c:forEach>
							
							
						</tbody>
					</table>
	</div>
	<br>

</c:when>
</c:choose>

<c:url var="firstUrl" value="searchAssignmentSubmissionPage?pageNo=1" />
<c:url var="lastUrl" value="searchAssignmentSubmissionPage?pageNo=${page.totalPages}" />
<c:url var="prevUrl" value="searchAssignmentSubmissionPage?pageNo=${page.currentIndex - 1}" />
<c:url var="nextUrl" value="searchAssignmentSubmissionPage?pageNo=${page.currentIndex + 1}" />


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
            <c:url var="pageUrl" value="searchAssignmentSubmissionPage?pageNo=${i}" />
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
</div>

	</section>

	  <jsp:include page="footer.jsp" />
	  <script>
		 var consumerTypeId = '${ searchBean.consumerTypeId }';
		 var programStructureId = '${ searchBean.programStructureId }';
		 var programId = '${ searchBean.programId }';
		 var g_subject = '${ searchBean.subject }';
		</script>
	  <%@ include file="../views/common/consumerProgramStructure.jsp" %>

</body>
<script>
/* $(document).ready(function(){

	$(".durationFields").change(function(){
		console.log($(this).val());
		var durationField = $(this).val();
		if(durationField !='' || durationField != null){
			$(".durationFields").attr("required",true);
		}	
	}); 
}); */

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




</html>
