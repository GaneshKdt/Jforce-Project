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

<jsp:include page="../jscss.jsp">
<jsp:param value="Check Copy Case" name="title" />
</jsp:include>
<style>
.assessments-style{
overflow:visible !important;
display:block !important;
}
.col-md-3 {
    width: 500%;
}
.assessment-margin{
margin-bottom: 15px;
}
.error {
	transition: 0.28s;
	overflow: hidden;
	color: red;
	font-style: italic;
	background-color: #ff00007a;
	color: white;
	padding: 2px 10px 1px 10px;
}
</style>
<body class="inside">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.1/css/select2.min.css"/>

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Copy Case Check</legend></div>
        <%-- <%@ include file="../messages.jsp"%> --%>
		<div class="panel-body clearfix">
		
		<%-- <form:form  action="copyCaseCheck" method="post" modelAttribute="searchBean" id="check_copyCase_form">
			<fieldset>
			<div class="col-md-6 column">
				
					<div class="form-group">
						<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${searchBean.year}">
							<form:option value="">Select Year</form:option>
							<form:options items="${yearList}" />
						</form:select>
					</div>
				
					<div class="form-group">
						<form:select id="month" path="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${searchBean.month}">
							<form:option value="">Select Month</form:option>
							<form:option value="Apr">Apr</form:option>
							<form:option value="Jun">Jun</form:option>
							<form:option value="Sep">Sep</form:option>
							<form:option value="Dec">Dec</form:option>
						</form:select>
					</div>
					
					<div class="form-group">
							<form:select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" required="required"  itemValue="${searchBean.subject}">
								<form:option value="">Select Subject</form:option>
								<form:options items="${subjectList}" />
							</form:select>
					</div>		
					
					<div class="form-group">
							<form:input id="minMatchPercent" path="minMatchPercent" type="text" placeholder="Matching %" class="form-control" value="${searchBean.minMatchPercent}"/>
							<span>Threshold 1 Matching %</span> 
							
					</div>
					<div class="form-group">
							<form:input id="threshold2" path="threshold2" type="text" placeholder="Matching %" class="form-control" value="${searchBean.threshold2}"/>
					       	<span>Threshold 2 Matching %</span> 
					        <span class="error" style="display:none;"></span> 
					</div>
					
					
					<!-- Button (Double) -->
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						<div class="controls">
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="copyCaseCheck">Check Copy Cases</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
					</div>

			</div>
			<div class="col-md-4 column">
				<div class="form-group">
				<label for="subjectList">Select Subjects</label>
					<form:select  id="selectSubjects" path="subjectList" name="subjectList" multiple="true" class="assessments-style form-control" placeholder="Select Subjects">
						<form:options items="${searchBean.subjectList}" />
					</form:select>
				</div>
			</div>
			
			<div class="col-md-4 column">
				<label class="control-label" for="submit"></label>
					<div class="controls">
						<button type="button" class="btn btn-large btn-primary" onclick="selectAll()">Select All</button>
  						<button type="button" class="btn btn-danger"  onclick="deselectAll()">Deselect All</button>
					</div>
			</div>
			
			</fieldset>
		</form:form> --%>
		
		
		<form id="check_copyCase_form">
			<fieldset>
			<div id="successMsg" style="display:none"></div>
			<div id="errorMsg" style="display:none"></div>
			
			<div class="col-md-6 column">
				
					<div class="form-group">
						<select id="year" name="year" type="text"	placeholder="Year" class="form-control" required="required"  itemValue="${searchBean.year}">
							<option value="">Select Year</option>
							<c:forEach items="${yearList}" var="year">
								<option value="${year}">
								${year}
								</option>
								</c:forEach>
						</select>
					</div>
				
					<div class="form-group">
						<select id="month" name="month" type="text" placeholder="Month" class="form-control" required="required" itemValue="${searchBean.month}">
							<option value="">Select Month</option>
							<option value="Apr">Apr</option>
							<option value="Jun">Jun</option>
							<option value="Sep">Sep</option>
							<option value="Dec">Dec</option>
						</select>
					</div>
					
					<%-- <div class="form-group">
							<select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" required="required"  itemValue="${searchBean.subject}">
								<option value="">Select Subject</option>
								<options items="${subjectList}" />
							</select>
					</div> --%>		
					
					<div class="form-group">
							<input id="minMatchPercent" name="minMatchPercent" type="text" placeholder="Matching %" class="form-control" value="${searchBean.minMatchPercent}"/>
							<span>Threshold 1 Matching %</span> 
							
					</div>
					<div class="form-group">
							<input id="threshold2" name="threshold2" type="text" placeholder="Matching %" class="form-control" value="${searchBean.threshold2}"/>
					       	<span>Threshold 2 Matching %</span> 
					        <span class="error" style="display:none;"></span> 
					</div>
					
					
					<!-- Button (Double) -->
					<div class="form-group">
						<label class="control-label" for="submit"></label>
						
						<div class="controls">
							<!-- <button id="submit" name="submit" class="btn btn-large btn-primary" formaction="copyCaseCheck">Check Copy Cases</button> -->
							<button id="submit" name="submit" class="btn btn-large btn-primary" onClick="copyCaseCheck(event)">Check Copy Cases</button>
							<div id='theImg' style="display:none">
							<img  src='/exam/resources_2015/gifs/loading-29.gif' alt="Loading..." style="height:40px" />
							</div>
						</div>
						
						<div class="controls">
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="home" formnovalidate="formnovalidate">Cancel</button>
						</div>
						
					</div>

			</div>
			<%-- <div class="col-md-4 column">
				<div class="form-group">
				<label for="subjectList">Select Subjects</label>
					<select  id="selectSubjects"  path="subjectList" name="subjectList" multiple="true" class="assessments-style form-control" placeholder="Select Subjects">
						<options items="${searchBean.subjectList}" />
						<c:forEach items="${searchBean.subjectList}" var="subjectList">
							<option value="${subjectList}">
								${subjectList}
							</option>
						</c:forEach>
					</select>
				</div>
			</div> --%>
			
			<div class="col-md-4 column">
				<div class="form-group">
					<label for="fileData" path="fileData">Select file</label>
					<input id="fileData" name="fileData" type="file" onChange="checkFile()"/>
				</div>
			</div>
			<div class="col-md-12 column">
				<b>Format of Upload: </b>
				<br/>Subject<br>
				<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />resources_2015/templates/CC_Subject_List.xlsx" target="_blank">Download a Sample Template</a> <br>
			</div>
			
			<!-- <div class="col-md-4 column">
				<label class="control-label" for="submit"></label>
					<div class="controls">
						<button type="button" class="btn btn-large btn-primary" onclick="selectAll()">Select All</button>
  						<button type="button" class="btn btn-danger"  onclick="deselectAll()">Deselect All</button>
					</div>
			</div> -->
			
			</fieldset>
		</form>
		
		<div id="CCSubjectWIseCountLableDiv">
			<h2>&nbsp;Copy Case Marked Subject wise Student Count</font></h2>
		</div>
		<div id="CCSubjectWIseCountTableDiv"></div>
		
		</div>
	
	</div>
	
	
	</section>
	
	
	  <jsp:include page="../footer.jsp" />
	

</body>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.1/js/select2.min.js"></script>
<script>
$(document).ready(function(){
	$("#CCSubjectWIseCountLableDiv").hide();
	 $("#selectSubjects").select2({
		/* width: '350%',
		height: '50px', */
		val:''});
	$("#selectSubjects").select2('val','');
})

</script>
<script>
$("#threshold2").keyup(function() { 
	 var  threshold1 = parseInt($( "#minMatchPercent" ).val());
	   var threshold2 = parseInt($( "#threshold2" ).val());
	  if(threshold1 > threshold2){ 
		  $( ".error" ).html("threshold2 should be greater than threshold1");
		  $( ".error" ).css("display","");  
	  }else{
		  $( ".error" ).css("display","none");
	  }
}); 
$( "#check_copyCase_form" ).submit(function( event ) {
  
  var  threshold1 = parseInt($( "#minMatchPercent" ).val());
   var threshold2 = parseInt($( "#threshold2" ).val());
  if(threshold1 > threshold2){     
	  return false;   
  }
});

function selectAll() {
    $("#selectSubjects > option").prop("selected", true);
    $("#selectSubjects").trigger("change");
}

function deselectAll() {
    $("#selectSubjects > option").prop("selected", false);
    $("#selectSubjects").trigger("change");
}



function checkFile()
{
	var fileName= document.getElementById("fileData").value;
	var fileExt=fileName.split(".").pop();
	var validExts = new Array(".xlsx", ".xls");
	var fileExtName= '.'+fileExt;
	if(validExts.indexOf(fileExtName)<0)
	{
		 alert("Invalid file selected, valid files are of " +
	               validExts.toString() + " types.");
		 document.getElementById("fileData").value="";
	}
}


function copyCaseCheck(e)
{
	e.preventDefault();
	var year = document.getElementById("year").value;
	var month = document.getElementById("month").value;
	var minMatchPercent = document.getElementById("minMatchPercent").value;
	var threshold2 = document.getElementById("threshold2").value;
	/* var subjectList = document.getElementById("selectSubjects").value; */
	var fileData = document.getElementById("fileData").value;
	if(year == "")
	{
		alert("Exam Year is mandatory");	
		return;
	}
	if(month == "")
	{
		alert("Exam Month is mandatory");	
		return;
	}
	if(minMatchPercent == "" || minMatchPercent == 0)
	{
		alert("Please insert Threshold 1 ");	
		return;
	}
	if(threshold2 == "" || threshold2 == 0)
	{
		alert("Please insert Threshold 2 ");	
		return;
	}
	/* if(subjectList=="")
	{
		alert("Please select atleast one subject");	
		return;
	} */
	if(fileData == "")
	{
		alert("Please upload Subject List excel file.");	
		return;
	}
	var form = document.getElementById("check_copyCase_form");
	var formData = new FormData(form);
	const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
	var table = document.createElement("table");  //makes a table element for the page
    let innerT = "";
	hideSubmitBtn();
	$.ajax({
		 url : '/exam/m/admin/copyCaseCheck',
	     type : 'POST',
	     enctype: 'multipart/form-data',
	     data : formData,
	     cache : false,
	     contentType : false,
	     processData : false,
	     timeout: 100000000,
	     success:function(data){
	    	showSubmitBtn();
	    	if(data.success == "true"){
	    		document.getElementById("successMsg").style.display="block";
				const div='<div class="alert alert-success alert-dismissible" id="successMsgDescription"></div>';
				document.getElementById("successMsg").innerHTML=div;
				document.getElementById("successMsgDescription").innerHTML=data.successMessage+button;
	    	}
	    	if(data.error != null){
	    		document.getElementById("errorMsg").style.display="block";
	    		const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
				document.getElementById("errorMsg").innerHTML=div;
				document.getElementById("errorMsgDescription").innerHTML=data.error+button;
		    }

	    	var bean = data.allAssignmentFilesList;
	    	$("#CCSubjectWIseCountLableDiv").show();
	    	innerT += "<style>table {border-collapse: collapse;}th, td {border: 3px solid white;padding: 10px;text-align: left;}</style><table id='CCSubjectWIseCountTable class='table table-striped table-hover' class='table table-striped' style='width: 100% !important;'><thead><tr><th>Subject</th><th>Sapid Count</th></tr></thead><tbody>";	    		
		    for (var i = 0; i < bean.length; i++)  //loops through the array 
		    	{
		    		innerT += "<tr><td>"+ bean[i].subject + "</td>";
		    		innerT += "<td>"+ bean[i].sapId + "</td></tr>";
		    	}
		    innerT += "</tbody></table>";
	    	table.innerHTML = innerT;
	    	$('#CCSubjectWIseCountTableDiv').html(table);
	    	    /* $('#table').DataTable({ */
	    	    /* $('#CCSubjectWIseCountTable').DataTable({
	    			"autoWidth": true,
	    		}); */
		 },
		 error:function(err){
			showSubmitBtn();
			 document.getElementById("errorMsg").style.display="block";
			 const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
			document.getElementById("errorMsg").innerHTML=div;
			document.getElementById("errorMsgDescription").innerHTML="Error in Copy Case :"+err+button;
		}
	})
}


function hideSubmitBtn()
{
	document.getElementById("submit").style.display="none";
	document.getElementById("theImg").style.display="block";
	document.getElementById("successMsg").style.display="none";
	document.getElementById("errorMsg").style.display="none";
}

function showSubmitBtn()
{
	document.getElementById("submit").style.display="block";
	document.getElementById("theImg").style.display="none";
}

</script>

</html>
