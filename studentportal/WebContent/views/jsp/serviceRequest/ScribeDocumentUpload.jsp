<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html lang="en">
<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Enter Service Request Information" name="title" />
</jsp:include>
<head>
<style>
#errorButton
{
    cursor:pointer;
    text-align:right;
    color:black;
    font-size:18px;
    position:relative;
    float: right;
    display:none;
    z-index:100;
    margin-top:-60px;
}
}
</style>
</head>
<body>
	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<%@ include file="../common/breadcrum.jsp"%>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<h2 class="red text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<div class="alert alert-danger alert-dismissible" id="messages" style="display:none;">
                				<p id="errorMessage"></p>
                				</div><button onclick="removeError()"  class="bg-danger " id="errorButton">&times;</button>
							<form:form action="saveScribeSR" method="POST"
								modelAttribute="sr" enctype="multipart/form-data">
								<fieldset>
									<div class="row">
										<div class="col-md-8">
											<div class="form-group">
												<form:label path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType" />
											</div>
											<div class="form-group">
												<label>Upload Your Scribe Details (Resume and Photo ID Proof):</label> <input type="file" name="resume"
													 accept=".pdf" required="required" class="form-control" id="file" onchange="Filevalidation()">
											</div>
											<div class="form-group">
												<label>Note:</label>
												<ul style="font-size:13px;">
												<li>Upload the latest resume along with a photo ID proof of the scribe for approval, in one single PDF file format.</li>
												<li>The scribe should be a grade junior in academic qualification than the student if he/she belongs from the same stream.</li>
												<li>Approval of the document(s) is solely at the discretion of the University.</li>
												</ul>
											</div>
											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submit" name="submit"
														class="btn btn-large btn-primary"
														formaction="saveScribeSR"
														onClick="return confirm('Are you sure you want to save this information?');">Save
														Service Request</button>
													<button id="backToSR" name="BacktoNewServiceRequest"
														class="btn btn-danger" formaction="selectSRForm"
														formnovalidate="formnovalidate">Back to New
														Service Request</button>
												</div>
											</div>
										</div>
									</div>
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<jsp:include page="../common/footer.jsp" />
	 <script>
	 removeError=()=>{
		 document.getElementById("messages").style.display = "none";
		 document.getElementById("errorMessage").innerHTML="";
		 document.getElementById("errorButton").style.display = "none";
		 }
    Filevalidation = (event) => {
        const fi = document.getElementById('file');
        // Check if any file is selected.
        if (fi.files.length > 0) {
            for (const i = 0; i <= fi.files.length - 1; i++) {
 
                const fsize = fi.files.item(i).size;
                var fileName = fi.value;
                var ext = fileName.substring(fileName.lastIndexOf('.') + 1);
                const file = Math.round((fsize / 1024));
                // The size of the file.
                if(ext=="pdf"||ext=="PDF"||ext=="Pdf"){
                	   if (file > 10240) {
                          document.getElementById("messages").style.display = "block";
                          document.getElementById("errorButton").style.display = "block";
                          document.getElementById("errorMessage").innerHTML="Please Upload File size of Less than 10MB";
                       	  const file =document.querySelector('#file');
                          file.value = '';
                       }
                	   else{
                		   document.getElementById("errorButton").style.display = "none";
                		   document.getElementById("messages").style.display = "none";
                           document.getElementById("errorMessage").innerHTML="";
                    	   } 
                }else{
                    document.getElementById("messages").style.display = "block";
                    document.getElementById("errorButton").style.display = "block";
                    document.getElementById("errorMessage").innerHTML="Please Upload only PDF files";
                 	  const file =document.querySelector('#file');
                    file.value = '';
                }
             
            }
        }
    }
</script> 
</body>
</html>