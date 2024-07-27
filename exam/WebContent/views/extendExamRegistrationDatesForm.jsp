<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.Person"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Set Dates for Exam Registration Configuration" name="title" />
</jsp:include>


<body class="inside">

	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row"><legend>&nbsp;Set Extend Dates For Exam Registration</legend></div>
		<div class="panel-body">
		<%@ include file="messages.jsp"%>
		<div class="col-md-6 column">
			<legend>&nbsp;Set Extended Dates For Exam Registration</legend>
			<form:form action="makeResultsLive" method="post" modelAttribute="configuration">
				<fieldset>
						<div class="form-group">
							<form:select id="configurationType" path="configurationType"  class="form-control" itemValue="${configuration.configurationType}" required="required">
								<form:option value="">Select Configuration</form:option>
								<form:option value="Exam Registration">Exam Registration</form:option>
							</form:select>
						</div>

						<div class="form-group">
							<label for="startDate">Extended Start Date</label>
							<form:input path="extendStartTime" id="startDate" type="datetime-local" required="required"/>
						</div>
						
						<div class="form-group">
							<label for="endDate">Extended End Date</label>
							<form:input path="extendEndTime" id="endDate" type="datetime-local" required="required"/>
						</div>


						<!-- Button (Double) -->
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<div class="controls">
								<button id="submit" name="submit" class="btn btn-large btn-primary"	formaction="extendExamRegistrationDates">Save Configuration</button>
								<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
							</div>
						</div>
					
				</fieldset>
				</form:form>
				
				<legend>&nbsp;Encrypt SapId </legend>
				<div class="form-group row" style="overflow:visible;">
				<div class="col-md-8 column">
						<textarea rows="3" cols="20" id="sapid" placeholder="ENTER COMMA SEPERATED SAPID"></textarea>
				</div>
				<div class="col-md-2 column" style="text-align:center">
				<br/><br/>
				<a href="JavaScript:void(0);" id="generateEncryptedId">Add &raquo;</a><br/>
				</div>
				<div class="col-md-8 column">	
    				<textarea rows="10" cols="100" id="encryptSapId"></textarea>						
    			</div>
				</div>
			</div>
			<div class="col-md-1 column">
			</div>
			<div class="col-md-11 column">
			<legend>&nbsp;Current Configuration</legend>
				<table class="table table-striped" style="font-size: 12px">
				<thead>
					<tr>
						<th>Sr. No.</th>
						<th>Configuration</th>
						<th>Start Date Time</th>
						<th>End Date Time</th>
						<th>Extended Start Date Time</th>
						<th>Extended End Date Time</th>
	
					</tr>
				</thead>
				<tbody>
	
					<c:forEach var="conf" items="${currentConfList}" varStatus="status">
						<tr>
							<td><c:out value="${status.count}" /></td>
							<td nowrap="nowrap"><c:out value="${conf.configurationType}" /></td>
							<td><c:out value="${conf.startTime}" /></td>
							<td><c:out value="${conf.endTime}" /></td>
							<td><c:out value="${conf.extendStartTime}" /></td>
							<td><c:out value="${conf.extendEndTime}" /></td>
						</tr>
					</c:forEach>
				</tbody>
				</table>
			</div>
		</div>
	</div>
	</section>
<span id="demo" style="display:none;"></span>
	<jsp:include page="footer.jsp" />
	
	<script>
		$(document).ready(function(){
			var sapidArray = new Array();
			var xhttp = new XMLHttpRequest();
			
			var encryptionURL = '${server_path}exam/selectSubjectsForm?eid=';
			console.log(encryptionURL);
			$("#sapid").change(function(){
				sapidArray = $("#sapid").val().split(',');
			});
			 $('#generateEncryptedId').click(function(){
				 	
				 	encryptSAPID(sapidArray);
				 	
			    });
			 
			 function encryptSAPID(sapidArray){
				 var textArea = $("#encryptSapId");
				 var encryptedSapID = new Array();
				  xhttp.onreadystatechange = function() {
					    if (this.readyState == 4 && this.status == 200) {
					    	response = this.responseText;
					    	encryptedSapID = response.split(',');
					    	for(var i=0;i<encryptedSapID.length-1;i++){
					    		let encodeSapid =  encodeURIComponent(encryptedSapID[i]);
						 		textArea.val(textArea.val()+"\n"+encryptionURL+encodeSapid+"\n");
						 	 } 
					    	
					    	
					    }
					  };
					  xhttp.open("GET", "/exam/encryptSAidList?saidList="+sapidArray.toString(), true);
					  xhttp.send();
			 }
		});
	</script>
</body>
</html>
