<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@ page contentType="text/html; charset=UTF-8" %>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@page import="com.nmims.beans.*"%>
<%@page import="java.util.ArrayList"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Promotional Emails" name="title" />
</jsp:include>

<body class="inside">

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<%@ include file="messages.jsp"%>
			
			
			<div class="row"><legend>Send Promotional Email</legend></div>
			
				
				<form:form  method="post" action="downloadPhotos">

							
					<div class="panel-body">
					<div class="col-md-6 column">
						<div class="form-group" align="left" >	
							<label>Session Year</label>
							<select name="sessionYear">
								<option value="">Please Select Year</option>
								<option value="2015">2015</option>
								<option value="2016">2016</option>
								<option value="2017">2017</option>
							</select>
						</div>
						
						<div class="form-group" align="left" >	
							<label>Session Month</label>
							<select name="sessionMonth">
								<option value="">Please Select Month</option>
								<option value="January">January</option>
								<option value="July">July</option>
							</select>
						</div>
						
						<div class="form-group" align="left" >	
							<label>Lead Status</label>
							<select name="leadStatus">
								<option value="">Please Select Status</option>
								<option value="Warm">Warm</option>
								<option value="Hot">Hot</option>
							</select>
						</div>
						
						<div class="form-group" align="left" >	
							<label>Additional Email ID</label>
							<input id="email" type="email" name="email" > 
						</div>
						
						<div class="form-group">
							<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="sendMassPromotionalEmail">Send Email to All Matching Leads</button>
							<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="sendSinglePromotionalEmail">Send to mentioned ID for checking</button>
						</div>
					</div>

			</div>

			</form:form>
			
		</div>
	</section>

	<%@ include file="footer.jsp"%>

</body>
</html>
