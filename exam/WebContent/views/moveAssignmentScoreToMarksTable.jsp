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
<jsp:param value="Move Assignment Score to Marks table" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Move Assignment Score to Marks table</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="downloadNormalizedScore" method="post" modelAttribute="searchBean">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-9 column">
					<div class="form-group">
						<form:select id="year" path="year"  cssClass="form-control"  itemValue="${searchBean.year}" required="required">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month"  cssClass="form-control" itemValue="${searchBean.month}" required="required">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="moveCopyScoreToMarksTable"	
							onCliCk="return confirm('Are you sure you want to move assignment copy scores to final marks table?')">Move Copy Cases To Marks Table</button>
						<button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="moveNormalizedScoreToMarksTable" 
							onCliCk="return confirm('Are you sure you want to move assignment normalized scores to final marks table?')">Normalized Score Verified! Move to Marks Table</button>
						<button id="cancel" name="cancel" class="btn btn-sm btn-danger" formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
					</div>
			</div>
			
			</div>
			</fieldset>
		</form:form>
		
	
	
	
	</div>
	</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
