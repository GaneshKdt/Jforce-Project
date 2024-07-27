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
<jsp:param value="Generate Marksheet" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Generate Custom Marksheet For Best Of Tee Passed</legend></div>
        <%@ include file="messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="getSingleStudentCustomMarksheetForBestOfTeePassed" method="post" modelAttribute="studentMarks">
			<fieldset>
			<div class="col-md-6 column">
				
					<div class="form-group">
						<form:select id="writtenYear" path="writtenYear" type="text" required="required"	placeholder="Written Year" class="form-control"   itemValue="${studentMarks.writtenYear}">
							<form:option value="">Select Written Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="writtenMonth" path="writtenMonth" type="text" required="required" placeholder="Written Month" class="form-control"  itemValue="${studentMarks.writtenMonth}">
							<form:option value="">Select Written Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
	</div>

	<div class="col-md-6 column">

					<div class="form-group">
							<form:input id="sapid" path="sapid" type="text" placeholder="SAP ID" class="form-control" value="${studentMarks.sapid}" required="required"/>
					</div>
					
					<div class="form-group">
						<form:select id="sem" path="sem" required="required" placeholder="Semester" class="form-control"  value="${studentMarks.sem}">
							<form:option value="">Select Semester</form:option>
							<form:option value="1">1</form:option>
							<form:option value="2">2</form:option>
							<form:option value="3">3</form:option>
							<form:option value="4">4</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<!-- <div class="controls"> -->
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="getSingleStudentCustomMarksheetForBestOfTeePassed">Generate</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/home" formnovalidate="formnovalidate">Cancel</button>
						<!-- </div> -->
					</div>
				</div>
			</fieldset>
		</form:form>
		
		<%if("true".equals((String)request.getAttribute("success"))){ %>
		<a href="${pageContext.request.contextPath}/admin/download">Download Marksheet</a>
		<%} %>
		</div>
	</div>

	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
