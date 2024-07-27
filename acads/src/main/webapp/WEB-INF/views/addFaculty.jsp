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
								<form:input id="mobile" path="mobile" type="text"
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
								<label>Upload Profile Pic :</label> <input type="file"
									name="facultyImageFileData" class="form-control"
									title="${faculty.imgUrl}">
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

						</div>

						<div class="col-md-6 column">
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
										formaction="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_EXAM_STATIC_RESOURCES')" />home" formnovalidate="formnovalidate">Cancel</button>
								</div>
							</div>
						</div>


					</fieldset>
				</form:form>

				<!-- </div> -->




			</div>
		</div>

	</section>

	<jsp:include page="footer.jsp" />

	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-tagsinput/0.8.0/bootstrap-tagsinput.js"></script>

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

	<script type="text/javascript">
		$('#editor').editable(
				{
					inlineMode : false,
					buttons : [ 'bold', 'italic', 'underline', 'sep',
							'strikeThrough', 'subscript', 'superscript', 'sep',
							'fontFamily', 'fontSize', 'color', 'formatBlock',
							'blockStyle', 'inlineStyle', 'sep', 'align',
							'insertOrderedList', 'insertUnorderedList',
							'outdent', 'indent', 'selectAll', 'sep',
							'createLink', 'table', 'sep', 'undo', 'redo',
							'sep', 'insertHorizontalRule', 'removeFormat',
							'fullscreen' ],
					minHeight : 200,
					paragraphy : false,
					placeholder : 'Enter Faculty Description here...',
					theme : 'blue',
					key : 'vA-16ddvvzalxvB-13C2uF-10A-8mG-7eC5lnmhuD3mmD-16==',
					toolbarFixed : false
				});
	</script>

	<script>

	    
		var selectedProgGroups = "${selectedProgGroups}";
		if (selectedProgGroups.length != 0) {
			var types = selectedProgGroups.split(",");
			for (var i = 0; i < types.length; i++) {
				$('#programGroup option[value="' + types[i] + '"]').prop(
						"selected", true);
			}
		}
				
		var selectedProgNames = "${selectedProgNames}";
		if (selectedProgNames.length != 0) {
			var types = selectedProgNames.split(",");
			for (var i = 0; i < types.length; i++) {
				$('#programName option[value="' + types[i] + '"]').prop(
						"selected", true);
			}
		}
		
			
		$("#programGroup")
				.change(
						function() {
							$("#programName option").remove();
							if ($(this).val() != "") {
								$
										.ajax({
											url : '/acads/admin/getProgramNames/'
													+ $(this).val(),
											type : 'GET',
											success : function(data) {
												console.log("data", data);
												var formOptions = "<option value=''>Select Program Name</option>";
												for (var i = 0; i < data.length; i++) {
													formOptions += "<option value='"+data[i]+"'>"
															+ data[i]
															+ "</option>"
												}
												console.log(formOptions);
												$("#programName").append(
														formOptions);
											},
											error : function(error) {
												alert(error.responseText);
												console.log("Refresh");
											}
										});
							}

						});
	</script>
	
	<script>
		$("select").each(function(){
			if ($(this).attr("id") == "approvedInSlab"
				&& ($(this).val() == "A (7500)" || $(
						this).val() == "B (5000)")) {
				$("." + $(this).attr("id")).attr("style","display:block;");
				$("."+ $(this).attr("id")+ " input").attr("required","required");
			} 
			
			else if ($(this).attr("id") == "isConsentForm"
				&& ($(this).val() == "Y")) {
				$("." + $(this).attr("id")).attr("style","display:block;");
				$("."+ $(this).attr("id")+ " input").attr("required","required");
			}
		});
		
		
		$("select").change(
				function() {
					if ($(this).attr("id") == "approvedInSlab") {
						if ($(this).val() == "A (7500)") {
							$("." + $(this).attr("id")).attr("style",
									"display:block;");
							$("." + $(this).attr("id") + " input").attr(
									"required", "required");
						} else if ($(this).val() == "B (5000)"
								&& $(this).attr("id") == "approvedInSlab") {
							$("." + $(this).attr("id")).attr("style",
									"display:block;");
							$("." + $(this).attr("id") + " input").attr(
									"required", "required");
						} else {
							$("." + $(this).attr("id")).attr("style",
									"display:none;");
							$("." + $(this).attr("id") + " input").removeAttr(
									"required");
						}
					}

					else if ($(this).attr("id") == "isConsentForm") {
						if ($(this).val() == "Y") {
							$("." + $(this).attr("id")).attr("style",
									"display:block;");
							$("." + $(this).attr("id") + " input").attr(
									"required", "required");
						} else {
							$("." + $(this).attr("id")).attr("style",
									"display:none;");
							$("." + $(this).attr("id") + " input").removeAttr(
									"required");
						}
					}
		});

		$("#submit").click(function() {
			if ($("#approvedInSlab").val() == "C (3000)") {
				$("#dateOfECMeetingApprovalTaken").val("");
			}
			
			
			//Validating a mobile number
			var number = $("#mobile").val();
			
			if(!(/^[0-9]*$/.test(number)))
			{
				alert("Please enter Valid Mobile number");
				return false;
			}
			
	
			if($("#title").val() != 'Grader'){
			//please fill out Description
				if($("#editor").val() == '')
				{
					alert("Please fill the description");
					return false;
				}
			
			}
			
			if ($("#isConsentForm").val() == "N") {
				$("#facultyConsentFormData").val("");
			}
		});
	</script>

	
	<script>
	
 	function Other(val){
		var element = document.getElementById("other");
		if(val == "Any Other"){
			alert("Type your desired area of specialisation.");
			element.style.display='block';	
		}else {
			element.style.display='none'; 
			document.getElementById("otherAreaOfSpecialisation").value = '';
		} 
	}
	
   
	</script>
	
	

	<script>
	function isGrader(value)
	{ 
		if(value == 'Grader')
			document.getElementById("linkedInProfileUrl").required = false;		
		else	
			document.getElementById("linkedInProfileUrl").required = true;		
		 
	}

	
	</script>
<script src="https://code.jquery.com/jquery-3.3.1.js" ></script>
</body>
</html>
