<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 


<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Add Student Marks" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Add Student Marks</legend></div>
		<div class="row clearfix">
		<form:form  action="addStudentMarks" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">
			<!--   -->
		

				<%if("true".equals((String)request.getAttribute("edit"))){ %>
				<form:input type="hidden" path="id" value="${studentMarks.id}"/>
				<%} %>
				<!-- Form Name -->
				

				<!-- Text input-->
				
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${studentMarks.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${studentMarks.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="grno" path="grno" type="text" placeholder="GR No." class="form-control" value="${studentMarks.grno}"/>
					</div>
					
					<div class="form-group">
							
							
							<%if("true".equals((String)request.getAttribute("edit")) && roles.indexOf("Exam Admin") == -1){ %>
								<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${studentMarks.sapid}"  readonly="true" />
							<%}else{ %>
								<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${studentMarks.sapid}" />
							<%} %>
							
							
					</div>
					
					<div class="form-group">
							<form:input id="studentname" path="studentname" type="text" placeholder="Student Name" class="form-control" value="${studentMarks.studentname}"/>
					</div>
					
					<div class="form-group">
						<form:select id="program" path="program" type="text"	placeholder="Program" class="form-control" required="required" itemValue="${studentMarks.program}">
							<form:option value="">Select Program</form:option>
							<form:options items="${programList}" />
						</form:select>
					</div>
					
					<div class="form-group">
						<form:select id="sem" path="sem" placeholder="Semester" class="form-control" required="required" value="${studentMarks.sem}">
							<form:option value="">Select Semester</form:option>
							<form:option value="1">1</form:option>
							<form:option value="2">2</form:option>
							<form:option value="3">3</form:option>
							<form:option value="4">4</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" required="required"  itemValue="${studentMarks.subject}">
								<form:option value="">Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>

				<!-- Button (Double) -->
				<div class="form-group">
					<label class="control-label" for="submit"></label>
					<div class="controls">
						<%if("true".equals((String)request.getAttribute("edit"))){ %>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="updateStudentMarks">Update</button>
						<%}else	{%>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="addStudentMarks">Submit</button>
						<%} %>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="getAllStudentMarks" formnovalidate="formnovalidate">Cancel</button>
					</div>
				</div>
				</div>
				
				<div class="col-md-6 column">
				
					
					
					<div class="form-group">
							<form:input id="writenscore" path="writenscore" type="text" placeholder="Writen Score" class="form-control" value="${studentMarks.writenscore}"/>
					</div>
					
					<div class="form-group">
							<form:input id="assignmentscore" path="assignmentscore" type="text" placeholder="Assignment Score" class="form-control" value="${studentMarks.assignmentscore}"/>
					</div>
					
					<div class="form-group">
							<form:input id="gracemarks" path="gracemarks" type="text" placeholder="Grace Marks" class="form-control" value="${studentMarks.gracemarks}"/>
					</div>
					
					<div class="form-group">
						<form:select id="attempt" path="attempt" type="text" placeholder="Attempt" class="form-control"  itemValue="${studentMarks.attempt}">
							<form:option value="">Select Attempt</form:option>
							<form:option value="Default">Default</form:option>
							<form:option value="Re-Exam">Re-Exam</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="source" path="source" type="text" placeholder="Source" class="form-control" value="${studentMarks.source}"/>
					</div>
					
					<div class="form-group">
							<form:input id="location" path="location" type="text" placeholder="Location" class="form-control" value="${studentMarks.location}"/>
					</div>
					
					<div class="form-group">
							<form:input id="centercode" path="centercode" type="text" placeholder="Center Code" class="form-control" value="${studentMarks.centercode}"/>
					</div>
					
					<div class="form-group">
							<form:input id="remarks" path="remarks" type="text" placeholder="Remarks" class="form-control" value="${studentMarks.remarks}"/>
					</div>
					
				</div>
				
			</fieldset>
		</form:form>

		</div>
		</div>
	
	</section>

	  <jsp:include page="footer.jsp" />


</body>

</html>
