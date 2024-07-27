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


<script type="text/javascript">

function validate(){
	var subject = document.getElementById("subject").value;
	if(subject == ""){
		alert("Please enter a subject");
		return false;
	}
}

</script>

<jsp:include page="../jscss.jsp">
<jsp:param value="Project Submission Pending Report Form" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Project Submission Pending Report Form</legend></div>
        <%@ include file="../messages.jsp"%>
		<div class="panel-body clearfix">
		<form:form  action="projectPendingReport" method="post" modelAttribute="searchBean">
			<fieldset>
			<div class="col-md-6 column">
				
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" itemValue="${searchBean.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" itemValue="${searchBean.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					    
					</div>		
					
					
					<div class="col-md-6 column">
						 
						<div class="form-group">
							<div class="controls">
								 <button id="submit" name="submit" class="btn btn-sm btn-primary" formaction="projectPendingReport">Download</button> 
							</div>
						</div>
					</div>	
			</fieldset>
			</form:form>
			</div> 
		</div>
	
		

	</section>

	  <jsp:include page="../footer.jsp" />


</body>




</html>
