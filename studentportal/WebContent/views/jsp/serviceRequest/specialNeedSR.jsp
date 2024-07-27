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
							<p>Dear Student, Kindly provide the necessary information required as stated below to raise this service request.</p>
							<!-- <p>You won't be able to submit service Request for next 48hrs . For details refer to "My Communications" Tab</p> -->
							<form:form action="saveSpecialNeedSR" method="POST"
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
											<div class="form-group" >
												<form:label path="additionalInfo1"
													for="NatureOfDisability">Nature Of Disability:</form:label>
												<form:select id="NatureOfDisability"
													path="additionalInfo1" class="form-control"
													required="required">
													<form:option value="">Select Nature Of Disability</form:option>
													<form:options items="${NatureOfDisabilityTypes}" />
												</form:select>
											</div>
											<div class="form-group">
												<label>Please Upload Your Medical Certificate Of Disability:</label> <input type="file" name="medical"
													 accept=".pdf" required="required" class="form-control" id="file" onchange="Filevalidation()">
											</div>
											<div class="form-group">
												<label>Note:</label>
												<ul style="font-size:13px;">
												<li>Upload your latest Medical Certificate attested by a Registered Medical Practitioner in a single file in PDF format.</li>
												<li>Approval of the documents is solely at the discretion of the University.</li>
												<li>You can re-apply for this SR in case of rejection of the original service request.</li>
												</ul>
											</div>
											<div class="form-group">
												<label class="control-label" for="submit"></label>
												<div class="controls">
													<button id="submit" name="submit"
														class="btn btn-large btn-primary"
														formaction="saveSpecialNeedSR"
														onClick="return confirm('Please Confirm if you want to proceed with this service request?');">Save
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