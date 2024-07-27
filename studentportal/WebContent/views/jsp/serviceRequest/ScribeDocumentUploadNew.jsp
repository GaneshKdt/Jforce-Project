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
}
</style>
</head> -->
<body>
<jsp:include page ="../common/headerDemo.jsp"/>
	<%-- <%@ include file="../common/headerDemo.jsp"%> --%>
	<div class="sz-main-content-wrapper">
		<div class="sz-breadcrumb-wrapper">
			<div class="container-fluid">
				<ul class="sz-breadcrumbs">
					<li><a href="/studentportal/home">Student Zone</a></li>
					<li><a href="selectSRForm">Select Service Request</a></li>
					<li>Scribe For Term end</li>
				</ul>
			</div>
		</div>
	
		<%-- <%@ include file="../common/breadcrum.jsp"%> --%>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Service Request" name="activeMenu" />
				</jsp:include>
				</div>
				<div class="sz-content-wrapper examsPage">
				<jsp:include page ="../common/studentInfoBar.jsp"/>
					<%-- <%@ include file="../common/studentInfoBar.jsp"%> --%>
					<div class="sz-content">
						<h2 class="text-danger text-capitalize">${sr.serviceRequestType }</h2>
						<div class="clearfix"></div>
						<div class="card card-body">
						<jsp:include page ="../common/messageDemo.jsp"/>
							<%-- <%@ include file="../common/messages.jsp"%> --%>
							<div class="alert alert-danger alert-dismissible" id="messages" style="display:none;">
                				<p id="errorMessage"></p>
                				<button onclick="removeError()"  class="btn-close" id="errorButton"></button>
                				</div>
							<form:form action="saveScribeSR" method="POST"
								modelAttribute="sr" enctype="multipart/form-data">
								<fieldset>
									<div class="row">
										<div class="col-md-8">
											<div class="form-group mb-3">
												<form:label class="fw-bold " path="serviceRequestType"
													for="serviceRequestType">Service Request Type:</form:label>
												<p>${sr.serviceRequestType }</p>
												<form:hidden path="serviceRequestType" />
											</div>
											<div class="form-group mb-3">
												<label class="fw-bold ">Upload Your Scribe Details (Resume and Photo ID Proof):</label> <input type="file" name="resume"
													 accept=".pdf" required="required" class="form-control" id="file" onchange="Filevalidation()">
											</div>
											<div class="form-group mb-3">
												<label class="fw-bold ">Note:</label>
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
														class="btn btn-danger"
														formaction="saveScribeSR"
														onClick="return confirm('Are you sure you want to save this information?');">Save
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
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/assets/js/serviceRequest/scribeDocumentUpload.js"></script>
</body>
</html>