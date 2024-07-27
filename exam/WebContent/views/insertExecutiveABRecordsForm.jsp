<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Insert AB Records for Online Exam" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Insert AB Records for Executive Exam</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchExecutiveABRecordsToInsert" method="post" modelAttribute="searchBean">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-6 column">
					<div class="form-group">
						<form:select id="year" path="year"  cssClass="form-control"  itemValue="${searchBean.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month"  cssClass="form-control" itemValue="${searchBean.month}">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Jan">Jan</form:option>
							<form:option value="Feb">Feb</form:option>
							<form:option value="Mar">Mar</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="May">May</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Jul">Jul</form:option>
							<form:option value="Aug">Aug</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Oct">Oct</form:option>
							<form:option value="Nov">Nov</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchExecutiveABRecordsToInsert">Search</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
			</div>
			
			</div>
			</fieldset>
		</form:form>
		
	
	
	<c:choose>
	<c:when test="${rowCount > 0}">
			<div class="panel-body">
			<legend>&nbsp;Absent Records<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadExecutiveABReport">Download AB report to verify before insertion</a></font></legend>
						
			</div>
	</c:when>
	</c:choose>
	</div>
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
