<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Make results live" name="title" />
</jsp:include>


<c:if test="${resultsMadeLive eq 'Y'}">
<style>
#resultsToCacheProgessContainerDiv{
	background-color: rgba(0,0,0,0.5) !important;
	z-index: 999;
	width: 100%;
	height: 100vh;
	position: fixed;
	font-size : 18px !important;
			
}

#resultsToCacheProgessMainDiv{
	
	background-color: white !important;
	text-align:center;
	color:black;	
	background-color: white !important;
  margin: auto;
  width: 40%;
  padding: 30px 20px;
  border: solid 2px grey;
}

#resultsToCacheProgessBarDiv{
	text-align:center;
  width: 100%;
}

#resultsToCacheFooterDiv{
	width: 100%;
}

#resultsToCacheButtonDiv{
	
  width: 100%;
  padding: 30px 20px;
  
}
</style>
</c:if>

<body class="inside">
<c:if test="${resultsMadeLive eq 'Y'}">
	<div id="resultsToCacheProgessContainerDiv" >
		
		<div id="resultsToCacheProgessMainDiv" style="margin-top:40px;">
			<h5 align="center">Moving Results Records To Cache.</h5>
			<div id="resultsToCacheProgessBarDiv">
			
			</div>
			<div id="resultsToCacheFooterDiv">
			
			</div>
			<div id="resultsToCacheButtonDiv">
				<a href="#" class="btn btn-info" role="button" id="closeProgress" style="float:right;" >Close</a>
				
			</div>	
		</div>
		
	</div>
</c:if>
	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row clearfix">
		<%@ include file="messages.jsp"%>
		<div class="col-md-6 column">
		
			<legend>&nbsp;Make Online Exam Result Live</legend>
			<form:form action="makeResultsLive" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${exam.month}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="live" path="live" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.live}" required="required">
								<form:option value="">Select to make live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeResultsLive">Make Online Results Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				
				<legend>&nbsp;Make Offline Exam Result Live</legend>
			<form:form action="makeOfflineResultsLive" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${exam.month}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="live" path="oflineResultslive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.oflineResultslive}" required="required">
								<form:option value="">Select to make live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeOfflineResultsLive">Make Offline Results Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				
				<legend>&nbsp;Make Exam Timetable Live</legend>
				<form:form action="makeTimetableLive" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${exam.month}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="live" path="timeTableLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.timeTableLive}" required="required">
								<form:option value="">Select to make Exam Timetable live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeTimetableLive">Make Exam Time Table Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				
				<legend>&nbsp;Make Assignment Submission Live</legend>
				<form:form action="makeTimetableLive" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${exam.month}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="assignmentLive" path="assignmentLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.assignmentLive}" required="required">
								<form:option value="">Select to make Assignment Submission live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeAssignmentSubmissionLive">Make Assignment Submission Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				
				<legend>&nbsp;Choose Fail Subject Assignment Submission Cycle</legend>
				<form:form action="makeTimetableLive" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${exam.month}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="assignmentLive" path="resitAssignmentLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.assignmentLive}" required="required">
								<form:option value="">Select to make Assignment Submission live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeResitAssignmentSubmissionLive">Make Assignment Submission Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				<legend>&nbsp;Make Project Sumission Live</legend>
				<form:form action="makeProjectSubmissionLive" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${exam.month}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="projectSubmissionLive" path="projectSubmissionLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.projectSubmissionLive}" required="required">
								<form:option value="">Select live status</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeProjectSubmissionLive">Update Project Submission Live Status!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				<legend>&nbsp;Make Assignment Marks Live</legend>
				<form:form action="makeTimetableLive" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${exam.month}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="assignmentMarksLive" path="assignmentMarksLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.assignmentMarksLive}" required="required">
								<form:option value="">Select to make Assignment Marks live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeAssignmentMarksLive">Make Assignment Marks Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				<legend>&nbsp;Make Cisco Session Calendar Live</legend>
				<form:form action="makeAcademicSessionLive" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="acadMonth" type="text" placeholder="Month" class="form-control" itemValue="${exam.acadMonth}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Jan">Jan</form:option>
								<form:option value="Jul">Jul</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="live" path="acadSessionLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.acadSessionLive}" required="required">
								<form:option value="">Select to make Session live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeAcademicSessionLive">Make Academic Session Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				
				<legend>&nbsp;Make Content Live</legend>
				<form:form action="makeAcademicSessionLive" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="acadMonth" type="text" placeholder="Month" class="form-control" itemValue="${exam.acadMonth}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Jan">Jan</form:option>
								<form:option value="Jul">Jul</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="acadContentLive" path="acadContentLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.acadContentLive}" required="required">
								<form:option value="">Select to make Content live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeAcademicContentLive">Make Content Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				 <legend>&nbsp;Make Forum Live</legend>
				<form:form action="makeForumLive" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="acadMonth" type="text" placeholder="Month" class="form-control" itemValue="${exam.acadMonth}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Jan">Jan</form:option>
								<form:option value="Jul">Jul</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="forumLive" path="forumLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.acadContentLive}" required="required">
								<form:option value="">Select to make Forum live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeForumLive">Make Forum Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form> 
				
			
				
				<legend>&nbsp;Make Written Reval Live</legend>
				<form:form action="makeWrittenRevalLiveForm" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${exam.acadMonth}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="writtenRevalLive" path="writtenRevalLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.writtenRevalLive}" required="required">
								<form:option value="">Select to make Content live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeWrittenRevalLive">Make Written Reval Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>

				<legend>&nbsp;Make Assignment Reval Live</legend>
				<form:form action="makeAssignmentRevalForm" method="post" modelAttribute="exam">
				<fieldset>
						<div class="form-group">
							<form:select id="year" path="year" type="text" placeholder="Year" class="form-control" itemValue="${exam.year}" required="required">
								<form:option value="">Select Year</form:option>
								<form:options items="${yearList}" />
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${exam.acadMonth}" required="required">
								<form:option value="">Select Month</form:option>
								<form:option value="Apr">Apr</form:option>
								<form:option value="Jun">Jun</form:option>
								<form:option value="Sep">Sep</form:option>
								<form:option value="Dec">Dec</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<form:select id="assignmentRevalLive" path="assignmentRevalLive" type="text"	placeholder="Make Live" class="form-control" itemValue="${exam.assignmentRevalLive}" required="required">
								<form:option value="">Select to make Content live</form:option>
								<form:option value="Y">Yes</form:option>
								<form:option value="N">No</form:option>
							</form:select>
						</div>



						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-sm btn-primary"	formaction="makeAssignmentRevalLive">Make Assignment Reval Live!</button>
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>

				
			</div>
			
			<div class="col-md-12 column">
			<legend>&nbsp;Current Status</legend>
				<table class="table table-striped" style="font-size: 12px">
				<thead>
					<tr>
						<th>Sr. No.</th>
						<th>Exam</th>
						<th>Online Results Live</th>
						<th>Offline Results Live</th>
						<th>Exam Registration Live</th>
						<th>Assign Submission Live</th>
						<th>Fail Subjects Submission Live</th>
						<th>Assignment Marks Live</th>
						<th>Project Submission Live</th>
						<th>Academic Session</th>
						<th>Content Live</th>
						<th>Session Calender Live</th>
						<th>Written(TEE) Reval Live</th>
						<th>Assignment Reval Live</th>
						<th>Forum Live</th>
	
	
					</tr>
				</thead>
				<tbody>
	
					<c:forEach var="exam" items="${examsList}" varStatus="status">
						<tr>
							<td><c:out value="${status.count}" /></td>
							<td nowrap="nowrap"><c:out value="${exam.month}-${exam.year}" /></td>
							<td><c:out value="${exam.live}" /></td>
							<td><c:out value="${exam.oflineResultslive}" /></td>
							<td><c:out value="${exam.timeTableLive}" /></td>
							<td><c:out value="${exam.assignmentLive}" /></td>
							<td><c:out value="${exam.resitAssignmentLive}" /></td>
							<td><c:out value="${exam.assignmentMarksLive}" /></td>
							<td><c:out value="${exam.projectSubmissionLive}" /></td>
							<td><c:out value="${exam.acadMonth}-${exam.year}" /></td>
							<td><c:out value="${exam.acadContentLive}" /></td>
							<td><c:out value="${exam.acadSessionLive}" /></td>
							<td><c:out value="${exam.writtenRevalLive}" /></td>
							<td><c:out value="${exam.assignmentRevalLive}" /></td>
						   <td><c:out value="${exam.forumLive}" /></td>
						</tr>
					</c:forEach>
				</tbody>
				</table>
			</div>
		</div>
	</div>
	</section>
	
	<jsp:include page="footer.jsp" />

</body>

<c:if test="${resultsMadeLive eq 'Y'}">
<script>
$(document).ready(function(){

	$("#closeProgress").click(function(e){
		e.preventDefault();
		$("#resultsToCacheProgessContainerDiv").hide();
	});
	
	///////////////////////////////////////////////////////
	
	
	$('#resultsToCacheProgessBarDiv').html(' <h5>Fetching Progess Data...</h5> ');

	let oldCount = 0;
	 
		setInterval( function(){
	
	
	$('#resultsToCacheProgessBarDiv').html(' <span>Fetching Progess...</span> ');
	 
	var data = {
			tableName:"RESULTS_COUNTER",
			keyName:"${exam.year}-${exam.month}"
	}

	console.log(data,JSON.stringify(data));
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/timeline/api/counter/findByTableNameKeyName",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data);
			let counterData = data.counterData;

			let counterDataArray = [];
			let count =0;

			let total =0;
			if(counterDataArray){
				counterDataArray = counterData.split("~");

				count = counterDataArray[0];
				total = counterDataArray[1];
			}

			let progressBarHtml = "<div class='progress'>"
				progressBarHtml =progressBarHtml +" <div class='progress-bar progress-bar-success' role='progressbar'"
				progressBarHtml =progressBarHtml +" aria-valuenow='"+count+"' "
				progressBarHtml =progressBarHtml +"	aria-valuemin='0' aria-valuemax='"+total+"' style='width:"+Math.round((count/total)*100)+"%'> "
				progressBarHtml =progressBarHtml +"	 "+Math.round((count/total)*100)+"%  </div> </div>";

			$('#resultsToCacheProgessBarDiv').html(progressBarHtml);

			if(oldCount != count){
				if(oldCount > 0){
					let eta = Math.round(((((total-count)*10)/(count-oldCount))/60))
					let footerHtml ="<span style='float:left'><b>ETA : "+eta+" Mins.</b></span>";
					 	footerHtml =footerHtml+"<span style='float:right'><b>"+count+"/"+total+"</b></span>";
	
						$('#resultsToCacheFooterDiv').html(footerHtml);
	
				}else{
					let eta = "NA";
					let footerHtml ="<span style='float:left'><b>ETA : "+eta+" Mins.</b></span>";
					 	footerHtml =footerHtml+"<span style='float:right'><b>"+count+"/"+total+"</b></span>";
	
					$('#resultsToCacheFooterDiv').html(footerHtml);
	
				}
			}

			oldCount = count;
		},
		error : function(e) {
			
			console.log("ERROR: ", e);
		}
	});
	
	
}, 10000); //update every 10seconds.


/////////////////////////////////////////////////////////////
	  
	});
</script>
</c:if>
</html>
