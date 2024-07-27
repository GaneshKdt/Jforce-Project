<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.ExamBookingTransactionBean"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.Format"%>
<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="jscss.jsp">
<jsp:param value="Mass Fee Receipt Download" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
       <div class="row"> <legend>Mass Fee Receipt Download</legend></div>
        <%@ include file="messages.jsp"%>
		
		<form:form  action="hallTicket" method="post" modelAttribute="examBookingBean">
			<fieldset>
			<div class="row clearfix">
			
			<div class="col-md-6 column">
					<div class="form-group">
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control">
							<form:option value="">Select Exam Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="massFeeReceiptDownload">Generate Fee Receipts</button>
						
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="${pageContext.request.contextPath}/examCenterHome" formnovalidate="formnovalidate">Cancel</button>
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
