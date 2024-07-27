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
<jsp:param value="Reset Faculty Assignment Allocation" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Reset Faculty Assignment Allocation : Level ${searchBean.level }</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="searchAssignmentSubmission" method="post" modelAttribute="searchBean">
			<fieldset>
				<div class="col-md-6 column">

					<form:hidden path="level"/>
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${searchBean.year}">
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
					
					<div class="form-group">
							<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" required="required"    itemValue="${searchBean.subject}">
								<form:option value="">Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>
					
					<div class="form-group">
							<form:input id="facultyId" path="facultyId" type="text" placeholder="Faculty ID" class="form-control" value="${searchBean.facultyId}" required="required"  />
					</div>	 
					
					<div class="form-group">
						<button id="submit" name="submit" class="btn btn-primary" formaction="searchFacultyAssignmentAllocation">Search Allocations</button>
						<button id="cancel" name="cancel" class="btn btn-danger " formaction="home" formnovalidate="formnovalidate">Cancel</button>
					</div>

			</div>
			</fieldset>
			</form:form>
			
			<c:if test="${pendingEvaluations > 0}">
				<div class="col-md-6 column">
				<h2>${pendingEvaluations} Assignment Evaluations Pending for Evaluation that can be reset</h2>
				<form:form  action="resetFacultyAssignmentAllocation" method="post" modelAttribute="searchBean">
					<form:hidden path="year"/>
					<form:hidden path="month"/>
					<form:hidden path="facultyId"/>
					<form:hidden path="subject"/>
					<form:hidden path="evaluated"/>
					<form:hidden path="level"/>
				
				<div class="form-group">
					<button id="submit" name="submit" class="btn btn-primary" formaction="resetFacultyAssignmentAllocation" 
					onclick="return confirm('Are you sure you want to reset this allocation?');">Reset Allocations</button>
					<button id="cancel" name="cancel" class="btn btn-danger " formaction="home" formnovalidate="formnovalidate">Cancel</button>
				</div>
				
				</form:form>
				</div>
			</c:if>

			<c:if test="${pendingEvaluations == 0}">
			<div class="alert alert-danger">No Allocations found.</div>
			</c:if>
		
		</div>
	
	
	</div>

	</section>

	<jsp:include page="footer.jsp" />


</body>
</html>
