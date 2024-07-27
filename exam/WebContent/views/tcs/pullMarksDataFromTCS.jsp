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

<jsp:include page="../jscss.jsp">
<jsp:param value="Pull TCS Data" name="title" />
</jsp:include>

<body class="inside">

<%@ include file="../header.jsp"%>
	
    <section class="content-container">
        <div class="container-fluid customTheme">
      
       <div class="row"> <legend>Pull TCS Data</legend></div>
        <%@ include file="../messages.jsp"%>
		
		<form:form action="readMarksDataFromTCSAPI" modelAttribute="studentMarks">
			<fieldset>
			<div class="panel-body">
			
			<div class="col-md-6 column">
					<div class="form-group">
					<form:label path="year" for="year">Exam Year</form:label>			
	 					<form:select id="year" path="year" type="text" required="required"	placeholder="Exam Year" class="form-control"   itemValue="${studentMarks.year}">
							<form:option value="">Select Exam Year</form:option>
							<form:options items="${yearList}"/>
						</form:select>
					</div>
					<div class="form-group">
					<form:label path="month" for="month">Exam Month</form:label>			
						<form:select id="month" path="month" type="text" required="required" placeholder="Exam Month" class="form-control"  itemValue="${studentMarks.month}">
		 					<form:option value="">Select Exam Month</form:option>
							<form:options items="${monthList}"/>
						</form:select>
					</div>
					<div class="form-group">
						<form:label path="fromDate" for="fromDate">From Date</form:label>						
						<div class='input-group date' id=''>
							<form:input path="fromDate" id="fromDate" type="date" style="color:black;" value="${studentMarks.fromDate}" required="required" />
						</div>
					</div>
				
					<div class="form-group">
						<form:label path="toDate" for="toDate">To Date</form:label>						
						<div class='input-group date' id=''>
							<form:input path="toDate" id="toDate" type="date" style="color:black;" value="${studentMarks.toDate}" required="required" />
						</div>
					</div>
		
					<div class="form-group">
						<form:label path="applicationStatus" for="applicationStatus">Application Status</form:label>
						<form:select  id="applicationStatus" path="applicationStatus" required="required" class="form-control" itemValue="${studentMarks.applicationStatus}" >
								<form:option value="">Select Application Status</form:option>
								<form:option value="All">All</form:option>
								<form:option value="Pending">Pending</form:option>
								
								
						</form:select>
					</div>
					
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="readMarksDataFromTCSAPI">Generate TCS Data</button>
						<button id="cancel" name="cancel" class="btn btn-danger" formaction="examCenterHome" formnovalidate="formnovalidate">Cancel</button>
						<c:if test="${rowCount > 0}">	
						<br></br>	
							<button id="submit" name="submit" class="btn btn-large btn-primary"
							formaction="tcsSummaryReport">Generate Summary Report</button>
						</c:if>	
					</div>
			</div>
			
			</div>
			</fieldset>
		</form:form>
		
	
	
<%-- 	<c:choose> --%>
<%-- 	<c:when test="${rowCount > 0}"> --%>
<!-- 			<div class="panel-body"> -->
<%-- 			<legend>&nbsp;Absent Records<font size="2px"> (${rowCount} Records Found) &nbsp; <a href="downloadABReport">Download AB report to verify before insertion</a></font></legend> --%>
<!-- 			<a  class="btn btn-large btn-primary"  href="/exam/admin/insertABReport">Insert AB Records</a>			 -->
<!-- 			</div> -->
<%-- 	</c:when> --%>
<%-- 	</c:choose> --%>
	</div>
	</section>

	  <jsp:include page="../footer.jsp" />


</body>

<script>

function readMarksDataFromTCSAPI(){
	 var promiseObj = new Promise(function(resolve, reject){
	////console.log("In saveAnswerAjax() ENTERED...");
	var methodRetruns = false;
	//ajax to save question reponse start
	var fromDate = $('#fromDate').val().toString();
	var toDate = $('#toDate').val().toString();
	var body = {
		'fromDate' : fromDate,
		'toDate' : toDate,
		'applicationStatus' :  $('#applicationStatus').val()
		
	};
	////console.log(body);
	$.ajax({
		type : 'POST',
		url : '/exam/readMarksDataFromTCSAPI',
		data: JSON.stringify(body),
       contentType: "application/json",
       dataType : "json",
       
	}).done(function(data) {
		  console.log("iN AJAX SUCCESS");
     	console.log(data);
     	
     	methodRetruns= true;
			////console.log("In saveAnswerAjax() EXIT... got methodRetruns: "+methodRetruns);
			resolve(methodRetruns);
	}).fail(function(xhr) {
		////console.log("iN AJAX eRROR");
		
		
		console.log("inside error");
	 })
	 return promiseObj;

});
}
</script>
</html>
