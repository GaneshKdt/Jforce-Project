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

<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row clearfix">
		<%@ include file="messages.jsp"%>
		<div class="col-md-6 column">
		 
			
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
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="home" formnovalidate="formnovalidate">Cancel</button>
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
								<button id="cancel" name="cancel" class="btn btn-danger btn-sm" formaction="home" formnovalidate="formnovalidate">Cancel</button>
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
</html>
