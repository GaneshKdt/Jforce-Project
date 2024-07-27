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
       <div class="row"> <legend>Insert AB Records for Online Exam</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="searchABRecordsToInsert" method="post" modelAttribute="searchBean" id="absentRecord">
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
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="searchABRecordsToInsert?studentType=Retail">Search</button>
						<button id="cancel" type="submit" class="btn btn-danger" formaction="/exam/admin/insertABRecordsForm">Cancel</button>
					</div>
			</div>
			
			</div>
			</fieldset>
		</form:form>
		
	
	
	<c:choose>
	<c:when test="${rowCount > 0}">
			<div class="panel-body">
			<legend>&nbsp;Absent Records<font size="2px"> (${rowCount} Total Records Found) &nbsp; 
			<c:if test="${studentTEEAbsentList > 0}">
			(${studentTEEAbsentList } Absent Record For TEE )
			</c:if>
			&nbsp;
			<c:if test="${projectAbsentCount > 0}">
			  (${projectAbsentCount } Absent Record For Project )
			 </c:if>
			 <a href="downloadABReport">Download AB report to verify before insertion</a></font></legend>
			<a  class="btn btn-large btn-primary"  href="/exam/admin/insertABReport">Insert AB Records</a>			
			</div>
	</c:when>
	</c:choose>
	</div>
	</section>

	  <jsp:include page="footer.jsp" />

<script>
$("#cancel").click(function(e) {
	document.getElementById('absentRecord').reset();
	$('select').prop('selectedIndex', 0);
	return true;
});
</script>
</body>
</html>
