<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html lang="en">
<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Enter Service Request Information" name="title" />
</jsp:include>
<!-- <head>
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
</head> -->
<body>
<jsp:include page = "../common/headerDemo.jsp"/>
	<%-- <%@ include file="../common/header.jsp"%> --%>
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/studentportal/home">Student Zone</a></li>
					<li><a href="selectSRForm">Select Service Request</a></li>
					<li>Special Needs</li>
				</ul>
			</div>
		</div>
		<%-- <%@ include file="../common/breadcrum.jsp"%> --%>
		<%-- <jsp:include page = "../common/breadcrum.jsp"/> --%>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
				<jsp:include page = "../common/studentInfoBar.jsp"/>
					<%-- <%@ include file="../common/studentInfoBar.jsp"%> --%>
					<div class="sz-content">
						<h2 class="text-danger text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="card card-body">
						<%-- <%@ include file="../common/messages.jsp"%> --%>
						<jsp:include page = "../common/messageDemo.jsp"/>
							<div class="alert alert-danger alert-dismissible" role="alert" id="messages" style="display:none;">
                				<p id="errorMessage"></p>
                				<button type="button" class="btn-close" onclick="removeError()" id="errorButton" aria-label="Close"></button>
                				</div>
							<p>Dear Student, Kindly provide the necessary information required as stated below to raise this service request.</p>
							<!-- <p>You won't be able to submit service Request for next 48hrs . For details refer to "My Communications" Tab</p> -->
							<form:form action="saveSpecialNeedSR" method="POST"
								modelAttribute="sr" enctype="multipart/form-data">
								<fieldset>
									<div class="row">
										<div class="col-md-8">
											<div class="form-group">
												<form:label  class="fw-bold" path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType" />
											</div>
											<div class="form-group" >
												<form:label class="fw-bold" path="additionalInfo1"
													for="NatureOfDisability">Nature Of Disability:</form:label>
												<form:select id="NatureOfDisability"
													path="additionalInfo1" class="form-select  mb-2"
													required="required">
													<form:option value="">Select Nature Of Disability</form:option>
													<form:options items="${NatureOfDisabilityTypes}" />
												</form:select>
											</div>
											<div class="form-group">
												<label class="fw-bold">Please Upload Your Medical Certificate Of Disability:</label> <input type="file" name="medical"
													 accept=".pdf" required="required" class="form-control mb-2" id="file" onchange="Filevalidation()">
											</div>
											<div class="form-group">
												<label class="fw-bold">Note:</label>
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
														class="btn btn-danger"
														formaction="saveSpecialNeedSR"
														onClick="return confirm('Please Confirm if you want to proceed with this service request?');">Save
														Service Request</button>
													<button id="backToSR" name="BacktoNewServiceRequest"
														class="btn btn-dark" formaction="selectSRForm"
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
	
	<jsp:include page="../common/footerDemo.jsp" />
	 <script type = "text/javascript" src="${pageContext.request.contextPath}/assets/js/serviceRequest/specialNeedsSr.js"></script>
	 
</body>
</html>