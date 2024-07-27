<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="java.util.Date"%>
<%@page import="com.ibm.icu.text.SimpleDateFormat"%>
<%@page import="com.nmims.beans.MBAWXConfigurationBean"%>
<%@page import="com.nmims.beans.AssignmentStatusBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>

<html class="no-js">
	<!--<![endif]-->
	
	<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
	<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
	
	<jsp:include page="jscss.jsp">
		<jsp:param value="Upload TEE Marks List" name="title" />
	</jsp:include>
	<head>
	   		 <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
	   		 <style>
	.dataTables_filter > label > input{
		float:right !important;
	}
	.toggleListWell{
	cursor: pointer !important;
		margin-bottom:0px !important;
	}
	.toggleWell{
		background-color:white !important;
	}
	input[type="radio"]{
		width:auto !important;
		height:auto !important;
		
	}
	.optionsWell{
		padding:0px 10px;
	}
	</style>
	</head>
	<body class="inside">
		<%@ include file="header.jsp"%>
		<section class="content-container login">
			<div class="container-fluid customTheme">
	
				<div class="row"><legend>Set Re Registration timeline</legend></div>
				
				<%@ include file="messages.jsp"%>
												
				<form:form modelAttribute="configuration" method="post">
					<div class="panel-body">
					
						<div class="row">
							<div class="col-md-12 column">
								
								<div class="col-md-4 column">
									<div class="form-group">
										<label for="startTime">Start</label>
										<% 
											MBAWXConfigurationBean configuration = (MBAWXConfigurationBean) request.getAttribute("configuration");

										    String startTimeStr = "";
										    String endTimeStr = "";
										    
										    if(configuration.getStartTime() != null && configuration.getEndTime() != null) {
										    	SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
								                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"); 
											    Date startTime = inputFormat.parse(configuration.getStartTime());  
											    Date endTime = inputFormat.parse(configuration.getEndTime());  
											    
											    startTimeStr = outputFormat.format(startTime);
											    endTimeStr = outputFormat.format(endTime);
										    }
										%>
										<input path="startTime" value="<%= startTimeStr %>" id="startTime" name="startTime" type="datetime-local" />
									</div>
								</div>
								
								<div class="col-md-4 column">
									<div class="form-group">
										<label for="endTime">End</label>
										<input path="endTime" value="<%= endTimeStr %>" id="endTime" name="endTime" type="datetime-local" />
									</div>
								</div>
							</div>
						</div>
						<br>
						<div class="row">
							<div class="col-md-12 column">
								<div class="col-md-6 column">
									<button 
										id="submit" 
										name="submit" 
										class="btn btn-large btn-primary"
										formaction="MBAWXMakeReRegistrationLive"
									>
										Save
									</button>
								</div>
							</div>
						</div>
					</div>
				</form:form>
			</div>
		</section>
		<jsp:include page="footer.jsp" />
	</body>
</html>
