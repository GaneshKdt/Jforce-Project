
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<jsp:include page="../jscss.jsp">
<jsp:param value="Upload Registrations Failed" name="title" />
</jsp:include>
<head>
 <link rel="stylesheet" href="https://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css">
</head>
<body class="inside">
<%@ include file="../header.jsp"%>
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="clearfix"></div>
        <div class="row">
        	<form id="form">
        		<div class="panel-body">
					<div id='alertTabMsg' class="alert alert-danger" style="display:none">Please DO NOT CLOSE the tab until success or error message is displayed.</div>					
					<div id="successMsg" style="display:none"></div>
					<div id="errorMsg" style="display:none"></div>
        		<div class="row">
        			<div class="col-md-6 column">
        				<div class="form-group">
							<label for="fileData" id="fileData">Upload Excel to Register on Mettl</label>
							<input name="fileData" id="fileDataInput" class="fileData" type="file" onChange="checkFile()" />
						</div>
					</div>
				</div>
				<br>
				<div class="row">
        			<div class="col-md-6 column">
        				<div class="form-group">
							<label for="sel1">Select Program Type:</label>
							 <select name="programType" id="programType" class="form-control" disabled="disabled">
								<option value="MBA - X">MBA - X</option>					
							</select>
						</div>
					</div>
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<input type="hidden" id="createdBy" name="createdBy" value="${userId}">
						<input type="hidden" id="lastModifiedBy" name="lastModifiedBy" value="${userId}">
						<button id="submit" name="submit" class="btn btn-large btn-primary" onClick="registerCandidate(event)">
						Upload Excel
						</button>
						<div id='theImg' style="display:none">
							<img  src='/exam/resources_2015/gifs/loading-29.gif' alt="Loading..." style="height:40px" />
						</div>
						<div id = "downloadbtndiv" style="display:none; margin-top: 7px;">
							<a  href="downloadFailedRegistrationsList"  class="btn btn-large btn-primary"  id="btnDownload">Download Error Records Excel </a>  
						</div>
					</div>
				</div>
        		</div>
        	</form>
        </div>
	</div>
	</section>
<jsp:include page="../footer.jsp" />
<script>
function checkFile()
{
	var fileName= document.getElementById("fileDataInput").value;
	var fileExt=fileName.split(".").pop();
	var validExts = new Array(".xlsx", ".xls");
	var fileExtName= '.'+fileExt;
	if(validExts.indexOf(fileExtName)<0)
	{
		 alert("Invalid file selected, valid files are of " +
	               validExts.toString() + " types.");
		 document.getElementById("fileDataInput").value="";
	}
}

function registerCandidate(e)
{
	e.preventDefault();
	var fileExists=document.getElementById("fileDataInput");
	var programType=document.getElementById("programType").value;
	if(fileExists.files.length == 0)
	{
		alert("Registration input file is mandatory");
		return;
	}
	if(programType=="")
	{
		alert("Program Type is mandatory");	
		return;
	}
	var form=document.getElementById("form");
	var formData = new FormData(form);
	hideSubmitBtn();
	$.ajax({
		 url : '/exam/m/uploadMBAXRegistrationFailedMettl',
	     type : 'POST',
	     enctype: 'multipart/form-data',
	     data : formData,
	     cache : false,
	     contentType : false,
	     processData : false,
	     success:function(data){
	    	showSubmitBtn();
	    	document.getElementById("fileDataInput").value="";
	    	if(data.error!=null)
		    {
				alert(data.error)
			}
			
			if(data.downloadExcel!=null && data.downloadExcel=='true')
			{
				document.getElementById("downloadbtndiv").style.display="block";
				document.getElementById("errorMsg").style.display="block";
				const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
				const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
				document.getElementById("errorMsg").innerHTML=div;
				document.getElementById("errorMsgDescription").innerHTML="Please Download Failed Registrations Excel"+button;
			}
			else
			{
				document.getElementById("downloadbtndiv").style.display="none";
				document.getElementById("errorMsg").style.display="none";	
			}
			
			if(data.count!=null)
			{
				document.getElementById("successMsg").style.display="block";
				const div='<div class="alert alert-success alert-dismissible" id="successMsgDescription"></div>';
				const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
				document.getElementById("successMsg").innerHTML=div;
				document.getElementById("successMsgDescription").innerHTML="Successfully "+data.count+" students registered in portal"+button;
			}
			else
			{
				document.getElementById("successMsg").style.display="none";			
			} 
		 },
		 error:function(error){
		console.log(JSON.stringify(error.responseJSON.error));
			 document.getElementById("errorMsg").style.display="block";
			const div='<div class="alert alert-danger errorMsgDescription" style="word-wrap: break-word;" id="errorMsgDescription"></div>';
			document.getElementById("errorMsg").innerHTML=div;
			const errorMessage =(error.responseJSON.error).replaceAll('"', '');
			document.getElementById("errorMsgDescription").innerHTML = errorMessage;	
			document.getElementById("fileDataInput").value=""; 
			showSubmitBtn();
			
		}
	})
}

function hideSubmitBtn()
{
	document.getElementById("submit").style.display="none";
	document.getElementById("theImg").style.display="block";
	document.getElementById("alertTabMsg").style.display="block";
	document.getElementById("successMsg").style.display="none";
	document.getElementById("errorMsg").style.display="none";
	document.getElementById("downloadbtndiv").style.display="none";
}

function showSubmitBtn()
{
	document.getElementById("submit").style.display="block";
	document.getElementById("theImg").style.display="none";
	document.getElementById("alertTabMsg").style.display="none";
}
</script>
</body>
</html>