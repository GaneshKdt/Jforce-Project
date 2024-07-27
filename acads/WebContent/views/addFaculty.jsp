<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Add Faculty" name="title" />
</jsp:include>

<!-- Froala wysiwyg editor CSS -->
<link
	href="<c:url value="/resources_2015/css/froala/froala_editor.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/froala_style.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/froala_content.min.css" />"
	rel="stylesheet">

<link
	href="<c:url value="/resources_2015/css/froala/themes/dark.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/themes/grey.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/themes/red.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/themes/royal.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="/resources_2015/css/froala/themes/blue.min.css" />"
	rel="stylesheet">
	
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/addfaculty.css"/>

<body class="inside">

	<%
		boolean isEdit = "true".equals((String) request.getAttribute("edit"));
	%>

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<%
				if ("true".equals((String) request.getAttribute("edit"))) {
			%>
			<legend>Edit Faculty</legend>
			<%
				} else {
			%>
			<legend>Add Faculty</legend>
			<%
				}
			%>

			<%@ include file="messages.jsp"%>
			<div class="panel-body">

				<form:form action="addFaculty" method="post"
					enctype="multipart/form-data" modelAttribute="faculty">
					<fieldset>
						<div class="col-md-6 column">

							<%
								if (isEdit) {
							%>
							<form:input type="hidden" path="id" value="${faculty.id}" />
							<%
								}
							%>

							<%
								if (isEdit) {
							%>
							<div class="form-group">
								<form:input id="facultyId" path="facultyId" type="text"
									placeholder="Faculty ID" class="form-control" readonly="true"
									value="${faculty.facultyId}" />
							</div>
							<%
								} else {
							%>
							<div class="form-group">
								<form:input id="facultyId" path="facultyId" type="text"
									placeholder="Faculty ID" class="form-control"
									required="required" value="${faculty.facultyId}" />
							</div>
							<%
								}
							%>

							<div class="form-group">
								<form:select id="title" path="title" type="text"
									placeholder="Title" class="form-control" onchange="isGrader(this.value);"
									required="required" itemValue="${faculty.title}">
									<form:option value="">Select Title</form:option>
									<form:option value="Director">Director</form:option>
									<form:option value="Principal">Principal</form:option>
									<form:option value="Professor & Equivalent">Professor & Equivalent</form:option>
									<form:option value="Associate Professor">Associate Professor</form:option>
									<form:option value="Reader">Reader</form:option>
									<form:option value="Lecturer (Selection Grade)">Lecturer (Selection Grade) </form:option>
									<form:option value="Assistant Professor">Assistant Professor </form:option>
									<form:option value="Lecturer (Senior Scale)">Lecturer (Senior Scale)</form:option>
									<form:option value="Lecturer">Lecturer</form:option>
									<form:option value="Tutor">Tutor</form:option>
									<form:option value="Demonstrator">Demonstrator</form:option>
									<form:option value="Part-Time Teacher">Part-Time Teacher</form:option>
									<form:option value="Ad hoc Teacher">Ad hoc Teacher</form:option>
									<form:option value="Temporary Teacher">Temporary Teacher</form:option>
									<form:option value="Contract Teacher">Contract Teacher</form:option>
									<form:option value="Visiting Teacher">Visiting Teacher</form:option>
									<form:option value="Vice-Chancellor">Vice-Chancellor</form:option>
									<form:option value="Pro-Vice-Chancellor">Pro-Vice-Chancellor</form:option>
									<form:option value="Additional Professor">Additional Professor</form:option>
									<form:option value="Principal In-charge">Principal In-charge</form:option>
									<form:option value="Grader">Grader</form:option>
									<form:option value="Insofe Faculty">Insofe Faculty</form:option>
									<form:option value="CS Panelist">CS Panelist</form:option>
								</form:select>
							</div>

							<div class="form-group">
								<form:select value="${faculty.salutation}" path="salutation" id="salutation" class="form-control">
									<form:option value="">Select Salutation</form:option>
									<form:option value="Dr">Dr.</form:option>
									<form:option value="Prof">Prof.</form:option>
								</form:select>
							</div>
							
							<div class="form-group">
								<form:input id="firstName" path="firstName" type="text"
									placeholder="First Name" class="form-control"
									required="required" value="${faculty.firstName}" />
							</div>

							<div class="form-group">
								<form:input id="lastName" path="lastName" type="text"
									placeholder="Last Name" class="form-control"
									required="required" value="${faculty.lastName}" />
							</div>

							<div class="form-group">
								<form:input id="email" path="email" type="email"
									placeholder="Email ID" class="form-control" required="required"
									value="${faculty.email}" />
							</div>

							<div class="form-group">
								<div id="countryDiv">
									<form:input type="number" id="countryCode" path="countryCode" value="${faculty.countryCode}" class="form-control" placeholder="91"/>
									<form:label path="countryCode" id="countryLable">+</form:label>
								</div>
								<form:input id="mobile" path="mobile" type="text" style="width: 80%"
									placeholder="Mobile" class="form-control" required="required"
									value="${faculty.mobile}" />
							</div>

							<%
								if (isEdit) {
							%>
							<div class="form-group">
								<form:input id="password" path="password" type="text"
									placeholder="Password" class="form-control" readonly="true"
									value="${faculty.password}" />
							</div>
							<%
								} else {
							%>
							<div class="form-group">
								<form:input id="password" path="password" type="text"
									placeholder="Password" class="form-control" required="required"
									value="${faculty.password}" />
							</div>
							<%
								}
							%>

							<div class="form-group">
								<form:select id="programGroup" path="programGroup" type="text"
									placeholder="Program Group" class="form-control"
									multiple="multiple" size="5" required="required"
									itemValue="${faculty.programGroup}">
									<form:option value="">Select Program Group</form:option>
									<form:options items="${programTypes}" />
								</form:select>
							</div>

							<div class="form-group">
								<form:select id="programName" path="programName" type="text"
									placeholder="Program Name" class="form-control"
									multiple="multiple" size="5" required="required"
									itemValue="${faculty.programName}">
									<form:option value="">Select Program Name</form:option>
									<form:options items="${programNames}" />
								</form:select>
							</div>

							<div class="form-group">
								<div class="col-md-9" style="padding: inherit;">
									<%-- <form:label for="approvedInSlab" path="approvedInSlab">Approved in Slab</form:label> --%>
									<form:select id="approvedInSlab" path="approvedInSlab"
										class="form-control" itemValue="${faculty.approvedInSlab}">
										<form:option value="">Select Approval In Slab</form:option>
										<form:option value="A (7500)">A (7500)</form:option>
										<form:option value="B (5000)">B (5000)</form:option>
										<form:option value="C (4000)">C (4000)</form:option>
										<form:option value="D (4500)">D (4500)</form:option>
										<form:option value="E (3000)">E (3000)</form:option>
										<form:option value="F (2000)">F (2000)</form:option>
										<form:option value="None">None</form:option>
									</form:select>
								</div>
								<div class="col-md-9"
									style="padding: inherit; padding-left: 1em;">
									<div class="approvedInSlab" style="display: none;">
										<%-- <form:label for="dateOfECMeetingApprovalTaken"
											path="dateOfECMeetingApprovalTaken">Date of EC Meeting Approval Taken</form:label> --%>
										<form:input type="date" id="dateOfECMeetingApprovalTaken"
											class="form-control"
											placeholder="Enter Date of EC Meeting Approval Taken"
											path="dateOfECMeetingApprovalTaken" />
									</div>
								</div>
							</div>

						</div>
						
						

						<div class="col-md-12 column">
							
				<div class="form-group">
							<div class="col-md-9" style="padding: inherit;">
								<form:label for="areaOfSpecialisation"
									path="areaOfSpecialisation">Select Area of Specialisation</form:label>
								<form:select id="areaOfSpecialisation"
									path="areaOfSpecialisation" class="form-control" onchange="Other(this.value);"
									itemValue="${faculty.areaOfSpecialisation}">
									<c:choose>
    									<c:when test="${empty faculty.areaOfSpecialisation}">
       											<form:option value="">Select Option</form:option>
    									</c:when>
    									<c:otherwise>
        										<form:option value="${faculty.areaOfSpecialisation}">${faculty.areaOfSpecialisation}</form:option>
    									</c:otherwise>
									</c:choose>
									<form:option value="Business-Environment-and-Strategy">Business-Environment-and-Strategy</form:option>
									<form:option value="Finance">Finance</form:option>  
									<form:option value="Communication">Communication</form:option>
									<form:option value="Economics">Economics</form:option>
									<form:option value="Human-Resources-And-Behavioral-Sciences">Human-Resources-And-Behavioral-Sciences</form:option>
									<form:option value="Marketing">Marketing</form:option>
									<form:option value="Information-Systems">Information-Systems</form:option>
									<form:option value="Operations and Decision Sciences">Operations and Decision Sciences</form:option>
									<form:option value="Any Other">Any Other</form:option>
								</form:select>
							</div>
							<div class="col-md-9" style="padding: inherit; padding-left: 1rem;">
							<c:choose>
    							<c:when test="${empty faculty.otherAreaOfSpecialisation}">
       								<div id="other" style="display: none;">
    							</c:when>
    							<c:otherwise>
    								<div id="other" style="display: block;">
        						</c:otherwise>
							</c:choose>
									<form:label for="otherAreaOfSpecialisation"
										path="otherAreaOfSpecialisation">Other Area of Specialisation</form:label>
									<form:input type="text" id="otherAreaOfSpecialisation"
										class="form-control"
										placeholder="Enter Other Area of Specialisation"
										path="otherAreaOfSpecialisation" />
								</div>
							</div>
						</div>

							<%-- 					<c:if test="${empty faculty.imgUrl}"> --%>
							<div class="form-group">
								<label>Upload Profile Pic :</label> <input type="file" id="facultyImageFileData"
									name="facultyImageFileData" accept=".jpg" class="form-control"
									title="${faculty.imgUrl}"/>
								<form:input type="hidden" id="facultyImageFileData" path="imgUrl" value="${faculty.imgUrl}" />
							</div>
							<%-- 					</c:if> --%>

							<div class="form-group">
								<div class="col-md-9" style="padding: inherit;">
									<form:label for="isConsentForm" path="isConsentForm">Provide Consent Form(Yes/No)</form:label>
									<form:select id="isConsentForm" path="isConsentForm"
										class="form-control" itemValue="${faculty.isConsentForm}">
										<form:option value="">Select Consent Form Approval</form:option>
										<form:option value="Y">Yes</form:option>
										<form:option value="N">No</form:option>
									</form:select>
								</div>
								<div class="col-md-9"
									style="padding: inherit; padding-left: 1em;">
									<div class="isConsentForm" style="display: none;">
										<form:label for="facultyConsentFormData"
											path="facultyConsentFormData">Upload Consent Form</form:label>
										<form:input type="file" id="facultyConsentFormData"
											class="form-control" path="facultyConsentFormData"
											title="${faculty.consentFormUrl}" />
									</div>
								</div>
							</div>

							<div class="form-group">
								<form:textarea path="facultyDescription" id="editor" class="editor"
									 title="${faculty.facultyDescription}" />
							</div>

							<div class="form-group">
								<form:input id="linkedInProfileUrl" path="linkedInProfileUrl"
									type="text" placeholder="LinkedIn Profile Url"
									class="form-control" required="required"
									value="${faculty.linkedInProfileUrl}" />
							</div>

							<div class="form-group">
								<form:textarea path="comments" id="comments" rows="5"
									placeholder="Enter Additional info(Comments)"
									class="form-control" value="${faculty.comments}" />
							</div>

					</fieldset>
					
					<div>
						<div class="form-group">
							<div class="col-md-9">
								<form:label path="ecApprovalDate" for="ECApprovalDate">EC Approval Date</form:label>
								<form:input value="${faculty.ecApprovalDate}" path="ecApprovalDate" id="ecApprovalDate" type="date" placeholder="EC Approval Date" class="form-control"/>
							</div>
							<div class="col-md-9">
								<form:label path="ecApprovalProof" for="ECApprovalProof">EC Approval Proof</form:label>
								<form:input type="file" accept="${ecApprovalProofExt}" value="${ecApprovalProofPath}${faculty.ecApprovalProof}" path="ecApprovalProof" id="ecApprovalProof" placeholder="EC Approval Proof" class="form-control"/>
								
								<c:if test="${not empty faculty.ecApprovalProofUrl}">
									<a href="<spring:eval expression="@propertyConfigurer.getProperty('EC_APPROVAL_PROOF_S3_URL')" />${faculty.ecApprovalProofUrl}" id="ecUrl" target="_blank">View Existing EC Approval Proof</a>
								</c:if>
								
								<form:input type="hidden" id="ecPath" path="ecApprovalProofUrl" value="${faculty.ecApprovalProofUrl}" />
								
							</div>
						</div>
						<div class="form-group">
							<div class="col-md-9">
								<form:label path="ecApprovalComment" for="Comments">EC Approval Comments</form:label>
								<form:textarea rows="5" style="resize: none" type="text" value="${faculty.ecApprovalComment}" path="ecApprovalComment" id="ecApprovalComment" placeholder="Enter your comment" class="form-control"/>
							</div>
							<div class="col-md-9">
								<form:label path="auditStatus" for="TechnicalAuditStatus">Technical Audit Status</form:label>
								<form:select value="${faculty.auditStatus}" path="auditStatus" id="auditStatus">
									<form:option value="">Select Technical Audit Status</form:option>
									<form:option value="Y">Accepted</form:option>
									<form:option value="N">Rejected</form:option>
								</form:select>
							</div>
							<%if (roles.indexOf("Acads Admin") != -1) { %>
								<div class="col-md-9">
									<form:label path="facultyStatus" for="FacultyStatus">Faculty Status</form:label>
									<form:select value="${faculty.facultyStatus}" path="facultyStatus" id="facultyStatus">
										<form:option value="">Select Faculty Status</form:option>
										<form:option value="Active">Active</form:option>
										<form:option value="Inactive">Inactive</form:option>
										<form:option value="OnHold">On Hold</form:option>
									</form:select>
								</div>
							<%} %>
						</div>
					</div>
					
					<div class="text-center">
						<div class="form-group">
								<label class="control-label" for="submit"></label>
								<div class="controls">
									<%
										if ("true".equals((String) request.getAttribute("edit"))) {
									%>
									<button id="submit" name="submit"
										class="btn btn-large btn-primary" formaction="updateFaculty">Update</button>
									<%
										} else {
									%>
									<button id="submit" name="submit"
										class="btn btn-large btn-primary" formaction="addFaculty">Submit</button>
									<%
										}
									%>
									<button id="cancel" name="cancel" class="btn btn-danger"
										formaction="/studentportal/home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
					</div>
				</form:form>

				<!-- </div> -->




			</div>
		</div>

	</section>

	<jsp:include page="footer.jsp" />

	<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-tagsinput/0.8.0/bootstrap-tagsinput.js"></script>

	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala_editor.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/tables.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/lists.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/colors.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/font_family.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/font_size.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/block_styles.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/media_manager.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/inline_styles.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/fullscreen.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/char_counter.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/entities.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/file_upload.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/urls.min.js"></script>
	
	<script src=" ${pageContext.request.contextPath}/assets/js/addfaculty.js" type="text/javascript"></script>
</body>
</html>
