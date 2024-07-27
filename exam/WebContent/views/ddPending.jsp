<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.DDDetails"%>
<%@page import="com.nmims.beans.StudentExamBean"%>

<html class="no-js"> 

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
<jsp:param value="Exam Booking Status" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="header.jsp"%>
	<%
	StudentExamBean student = (StudentExamBean)session.getAttribute("studentExam");
	String sapId = student.getSapid();
	%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Exam Booking Status</legend></div>
        <%@ include file="messages.jsp"%>
        <form:form  action="saveDDDetails" method="post" modelAttribute="ddDetails">
			<fieldset>
        <c:if test="${not empty ddDetails}">
		<div class="panel-body clearfix">
		
			<div class="col-md-9 column">
				Thank you for sharing DD Details as below: <br>
				<ul>
				<li>DD No: ${ddDetails.ddno}</li>
				<li>Bank Name: ${ddDetails.bank }</li>
				<li>DD Date: ${ddDetails.ddDate }</li>
				<li>DD Amount: ${ddDetails.amount }/- </li>
				<li>Status: ${ddDetails.tranStatus} </li>
				</ul>
				
				<p align="justify">
				<b>Your Demand Draft is not yet Received/Approved and Exam Registration is still Pending. </b> <br>
				Please take a print of this page along with the Demand Draft and submit at your registered Learning Center before <spring:eval expression="@propertyConfigurer.getProperty('EXAM_FEE_DD_LAST_DATE')" />. <br>
				Subject to DD Approval you will be notified through email and subsequently allowed to select exam centers and complete exam registration process.
					<br>
				</p>	
				</div>
			
			<div class="col-md-3 column">
			<b>Student ID: <%=sapId %></b>
			</div>
			
		</div>
		
		</c:if>
		<div class="form-group">
			<label class="control-label" for="submit"></label>
			<button id="cancel" name="cancel" type = "button" onClick ="window.print()" class="btn btn-primary" formaction="home" formnovalidate="formnovalidate">Print</button>
			<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Back to Home</button>
		</div>
		
		</fieldset>
		</form:form>
		
		</div>
		</section>

	  <jsp:include page="footer.jsp" />


</body>
</html>
