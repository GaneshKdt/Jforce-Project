
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<jsp:include page="../jscss.jsp">
<jsp:param value="Apply BOD" name="title" />
</jsp:include>
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
							<select name="examYear" id="examYear" required>
								<option value="">Select Exam Year</option>
								<c:forEach items="${yearList}" var="year">
								<option value="${year}">
								${year}
								</option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group">
							<select name="examMonth" id="examMonth" required>
								<option value="">Select Exam Month</option>
								<c:forEach items="${monthList}" var="month">
								<option value="${month}">
								${month}
								</option>
								</c:forEach>
							</select>
						</div>
						<div class="form-group">
							<label for="fileData" id="fileData">Upload Question Id to apply BOD</label>
							<input name="fileData" id="fileDataInput" class="fileData" type="file" onChange="checkFile()" />(FORMAT. QUESTIONID)
						</div>
					</div>		
				</div>
				<br>
				<div class="row">
					<div class="col-md-6 column">
						<input type="hidden" id="createdBy" name="createdBy" value="${userId}">
						<input type="hidden" id="lastModifiedBy" name="lastModifiedBy" value="${userId}">
						<!-- <button id="submit" name="submit" class="btn btn-large btn-primary" onClick="applyBod(event)">
						Apply BOD
						</button> -->
						<button id = "insert_bod" name = "insert_bod" class = "btn btn-large btn-primary" onclick="insertBod(event)">
						Insert BOD
						</button>
						<div id='theImg' style="display:none">
							<img  src='/exam/resources_2015/gifs/loading-29.gif' alt="Loading..." style="height:40px" />
						</div>
						<div id = "downloadbtndiv" style="display:none; margin-top: 7px;">
							<a  href="downloadApplyBodErrorList"  class="btn btn-large btn-primary"  id="btnDownload">Download Error Records Excel </a>
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

function applyBod(e)
{
	e.preventDefault();
	var examYear=document.getElementById("examYear").value;
	var examMonth=document.getElementById("examMonth").value;
	var fileExists=document.getElementById("fileDataInput");
	if(fileExists.files.length == 0)
	{
		alert("Question Id input file is mandatory");
		return;
	}
	if(examYear=="" || examMonth=="")
	{
		alert("Exam Month and Exam Year are mandatory");	
		return;
	}
	var form=document.getElementById("form");
	var formData = new FormData(form);
	hideSubmitBtn();
	$.ajax({
		 url : '/exam/admin/m/applyBod',
	     type : 'POST',
	     enctype: 'multipart/form-data',
	     data : formData,
	     cache : false,
	     contentType : false,
	     processData : false,
	     success:function(data){
	    	showSubmitBtn();
	    	if(data.error!=null)
		    {
				alert(data.error)
			}
			
			if(data.errorList!=null && data.errorList.length>0)
			{
				document.getElementById("downloadbtndiv").style.display="block";
				document.getElementById("errorMsg").style.display="block";
				const div='<div class="alert alert-danger alert-dismissible" id="errorMsgDescription"></div>';
				const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
				document.getElementById("errorMsg").innerHTML=div;
				document.getElementById("errorMsgDescription").innerHTML="Please download excel sheet with error Question Id's list"+button;
			}
			else
			{
				document.getElementById("downloadbtndiv").style.display="none";
				document.getElementById("errorMsg").style.display="none";	
			}
			
			/* if(data.successList!=null && data.successList.length>0)
			{ */
				document.getElementById("successMsg").style.display="block";
				const div='<div class="alert alert-success alert-dismissible" id="successMsgDescription"></div>';
				const button='<button type="button" class="close" data-dismiss="alert"  aria-hidden="true">  &times; </button>';
				document.getElementById("successMsg").innerHTML=div;
				document.getElementById("successMsgDescription").innerHTML="Successfully applied BOD for "+data.successList.length+" records"+button;
		/* 	}
			else
			{
				document.getElementById("successMsg").style.display="none";			
			} */
		 },
		 error:function(err){
			showSubmitBtn();
			console.log(err);
		}
	})
}


function hideSubmitBtn()
{
	document.getElementById("submit").style.display="none";
	document.getElementById("insert_bod").style.display="none";
	document.getElementById("theImg").style.display="block";
	document.getElementById("alertTabMsg").style.display="block";
	document.getElementById("successMsg").style.display="none";
	document.getElementById("errorMsg").style.display="none";
	document.getElementById("downloadbtndiv").style.display="none";
}

function showSubmitBtn()
{
	document.getElementById("submit").style.display="block";
	document.getElementById("insert_bod").style.display="block";
	document.getElementById("theImg").style.display="none";
	document.getElementById("alertTabMsg").style.display="none";
}



	function insertBod(e) {

		e.preventDefault();
		
		var examYear = $("#examYear").val();
		var examMonth = $("#examMonth").val();
		var fileUploaded = document.getElementById("fileDataInput");
		var form = document.getElementById("form");
		var formData = new FormData(form);

		if (!examYear || !examMonth) {
			alert("Exam Year/Month Not selected!!");
			return;
		}

		if (fileUploaded.files.length == 0) {
			alert("Question Ids input file are mandatory");
			return;
		}


		$.ajax({
			url : '/exam/admin/m/insertBodQuestionIds',
			type : 'POST',
			enctype : 'multipart/form-data',
			data : formData,
			cache : false,
			contentType : false,
			processData : false,

			success : function(data) {
				$("#successMsg").html(data.success).css("display","block").addClass("alert alert-success alert-dismissible");
				$("#errorMsg").css("display","none");
			},

			error : function(err) {
				var errorMessage;
				if(err.responseJSON.error) {
					errorMessage = err.responseJSON.error;						
				} else {
					errorMessage = "Internal Server Error!";
				}
				$("#errorMsg").html(errorMessage).css("display","block").addClass("alert alert-danger alert-dismissible");
				$("#successMsg").css("display","none");
			}

		});

	}

</script>
</body>
</html>