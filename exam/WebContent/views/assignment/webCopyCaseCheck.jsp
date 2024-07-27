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
<jsp:param value="Originality Check" name="title" />
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
        <div class="row"><legend>Originality Check</legend></div>
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
					
					<div class="form-group">
						<select id="plagiarismType" name="plagiarismType" type="text" placeholder="Select Plagiarism Check Type" class="form-control" required="required" itemValue="${searchBean.plagiarismType}">
							<option value="">Select Plagiarism Check Type</option>
							<option value="Web Plagiarisum">Web Plagiarisum</option>
							<option value="Peer-To-Peer Plagiarisum">Peer-To-Peer Plagiarisum</option>
							<option value="Both">Both</option>
						</select>
					</div>
					
					<%-- <div class="form-group">
							<select id="subject" path="subject" type="text"	placeholder="Subject" class="form-control" required="required"  itemValue="${searchBean.subject}">
								<option value="">Select Subject</option>
								<options items="${subjectList}" />
							</select>
					</div> --%>		
					<div id="dynamicTextBoxes"></div>
					<%-- <div class="form-group">
							<input id="minMatchPercent" name="minMatchPercent" type="text" placeholder="Matching %" class="form-control" value="${searchBean.minMatchPercent}"/>
							<span>Enter Max. Web plagiarism Threshold %</span> 
							
					</div>
					
					<div class="form-group">
							<input id="threshold2" name="threshold2" type="text" placeholder="Matching %" class="form-control" value="${searchBean.threshold2}"/>
					       	<span>Enter Max. Peer-To-Peer plagiarism Threshold %</span> 
					        <span class="error" style="display:none;"></span> 
					</div> --%>
					
					
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
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="<spring:eval expression="@propertyConfigurer.getProperty('SERVER_PATH')" />studentportal/home" formnovalidate="formnovalidate">Cancel</button>
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
		
		</div>
	
	</div>

	</section>

	  <jsp:include page="../footer.jsp" />
	

</body>
<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.1/js/select2.min.js"></script>
<script>
$(document).ready(function(){
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
	var plagiarismType = document.getElementById("plagiarismType").value;
	var minMatchPercent;
	var threshold2;
	if(plagiarismType == "Web Plagiarisum"){
		 minMatchPercent = document.getElementById("minMatchPercent").value;
	}else if(plagiarismType == "Peer-To-Peer Plagiarisum"){
		 threshold2 = document.getElementById("threshold2").value; 
		}
	else if(plagiarismType == "Both"){
		 minMatchPercent = document.getElementById("minMatchPercent").value;
		 threshold2 = document.getElementById("threshold2").value; 
	}
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
	if(plagiarismType == "")
	{
		alert("plagiarismType is mandatory");	
		return;
	}
	 if(minMatchPercent == "" || minMatchPercent == 0)
	{
		alert("Please Enter Max. Web plagiarism Threshold % ");	
		return;
	}
	if(threshold2 == "" || threshold2 == 0)
	{
		alert("Please Enter Max. Peer-To-Peer plagiarism Threshold % ");	
		return;
	} 
	
	/* if(subjectList=="")
	{
		alert("Please select atleast one subject");	
		return;
	} */
	console.log("fileData ----"+fileData);
	if(fileData == "")
	{
		alert("Please upload Subject List excel file.");	
		return;
	}
	var form = document.getElementById("check_copyCase_form");
	var formData = new FormData(form);
	const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
	hideSubmitBtn();
	$.ajax({
		 url : '/exam/m/admin/webCopyCaseCheck',
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


<!-- <script>
  var selectElement = document.getElementById("plagiarismType");
  var textBoxesContainer = document.getElementById("dynamicTextBoxes");

  selectElement.addEventListener("change", function() {
    var selectedValue = this.value;
    textBoxesContainer.innerHTML = "";

    if (selectedValue === "Web Plagiarisum" || selectedValue === "Both") {
      var webPlagiarismTextBox = document.createElement("input");
      webPlagiarismTextBox.type = "text";
      webPlagiarismTextBox.name = "webPlagiarism";
      webPlagiarismTextBox.placeholder = "Web Plagiarism";

      textBoxesContainer.appendChild(webPlagiarismTextBox);
    }

    if (selectedValue === "Peer-To-Peer Plagiarisum" || selectedValue === "Both") {
      var peerPlagiarismTextBox = document.createElement("input");
      peerPlagiarismTextBox.type = "text";
      peerPlagiarismTextBox.name = "peerPlagiarism";
      peerPlagiarismTextBox.placeholder = "Peer-To-Peer Plagiarism";

      textBoxesContainer.appendChild(peerPlagiarismTextBox);
    }
  });
</script> -->

<script>
  var selectElement = document.getElementById("plagiarismType");
  var textBoxesContainer = document.getElementById("dynamicTextBoxes");

  selectElement.addEventListener("change", function() {
    var selectedValue = this.value;
    textBoxesContainer.innerHTML = "";

    if (selectedValue === "Web Plagiarisum" || selectedValue === "Both") {
      var webPlagiarismTextBox = document.createElement("div");
      webPlagiarismTextBox.className = "form-group";

      var webPlagiarismInput = document.createElement("input");
      webPlagiarismInput.id = "minMatchPercent";
      webPlagiarismInput.name = "minMatchPercent";
      webPlagiarismInput.type = "text";
      webPlagiarismInput.placeholder = "Matching %";
      webPlagiarismInput.className = "form-control";
      webPlagiarismInput.value = "${searchBean.minMatchPercent}";

      var webPlagiarismSpan = document.createElement("span");
      webPlagiarismSpan.textContent = "Enter Max. Web plagiarism Threshold %";

      webPlagiarismTextBox.appendChild(webPlagiarismInput);
      webPlagiarismTextBox.appendChild(webPlagiarismSpan);

      textBoxesContainer.appendChild(webPlagiarismTextBox);
    }

    if (selectedValue === "Peer-To-Peer Plagiarisum" || selectedValue === "Both") {
      var peerPlagiarismTextBox = document.createElement("div");
      peerPlagiarismTextBox.className = "form-group";

      var peerPlagiarismInput = document.createElement("input");
      peerPlagiarismInput.id = "threshold2";
      peerPlagiarismInput.name = "threshold2";
      peerPlagiarismInput.type = "text";
      peerPlagiarismInput.placeholder = "Matching %";
      peerPlagiarismInput.className = "form-control";
      peerPlagiarismInput.value = "${searchBean.threshold2}";

      var peerPlagiarismSpan = document.createElement("span");
      peerPlagiarismSpan.textContent = "Enter Max. Peer-To-Peer plagiarism Threshold %";

      peerPlagiarismTextBox.appendChild(peerPlagiarismInput);
      peerPlagiarismTextBox.appendChild(peerPlagiarismSpan);

      textBoxesContainer.appendChild(peerPlagiarismTextBox);
    }
  });
</script>

</html>
