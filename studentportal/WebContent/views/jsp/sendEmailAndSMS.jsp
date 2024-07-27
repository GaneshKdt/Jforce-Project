<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Send Email" name="title" />
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

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>Send Email</legend>
			</div>

			<%@ include file="messages.jsp"%>

			<div class="panel-body">
				<form:form action="sendPushNotificationforEmailAndSMS" method="post" enctype="multipart/form-data"
					modelAttribute="student">
					<fieldset>

						<div class="row">
							<div class="column col-md-6">
								<div class="form-group ">
									<form:input path="subject" id="subject" required="required"
										class="form-control" placeholder="Enter Email Subject" />
								</div>


								<div class="form-group ">
									<label for="file">Select Excel File for Emails</label> <input
										id="file" name="file" type="file" class="form-control"
										required="required" />
								</div>
							</div>

							<div class="column col-md-6">

								<div class="form-group ">
									<label for="fromEmailId">From Email ID</label>
									<form:select id="fromEmailId" path="fromEmailId"
										class="form-control" itemValue="${student.fromEmailId}"
										onchange="showCriteria();">
										<form:option value="">Select From Email ID</form:option>
										<form:option value="ngasce@nmims.edu">ngasce@nmims.edu</form:option>
										<form:option value="ngasce.exams@nmims.edu">ngasce.exams@nmims.edu</form:option>
										<form:option value="ngasce.academics@nmims.edu">ngasce.academics@nmims.edu</form:option>
									</form:select>
								</div>

								<div class="form-group">
									<label for="enrollmentYear">Type of Communication</label>
									<form:select id="notificationType" path="notificationType"
										class="form-control" required="required"
										onchange="showCriteria();">
										<form:option value="">Select Notification</form:option>
										<form:option value="Email">Email</form:option>
										<form:option value="SMS">SMS</form:option>
										<form:option value="Email-SMS">Email & SMS</form:option>
									</form:select>
								</div>

							</div>

							<div class="column col-md-6">
								<div id="criteria"></div>
							</div>
						</div>
						<div class="row">
							<div class="column col-md-18">

								<div class="form-group">
									<label for="smsContent">SMS Content</label>
									<form:textarea path="smsContent" id="smsContent" cols="50"/>
									<div id="characters"></div>
								</div>

								<div class="form-group">
									<label for="editor">Email Content</label>
									<form:textarea path="body" id="editor" />
								</div>


								<div class="form-group">
									<div class="controls">
										<button id="submit" name="submit"
											class="btn btn-large btn-primary"
											>Submit</button>
									</div>
								</div>

							</div>
						</div>
					</fieldset>
				</form:form>
			</div>

		</div>

	</section>

	<jsp:include page="footer.jsp" />
	
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala_editor.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/tables.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/lists.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/colors.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/font_family.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/font_size.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/block_styles.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/media_manager.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/inline_styles.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/fullscreen.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/char_counter.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/entities.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/file_upload.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources_2015/js/vendor/froala-plugins/urls.min.js"></script>

	<script type="text/javascript">


$('#smsContent').keyup(function () {
	  var len = $(this).val().length;
	  $('#characters').text('('+len + ' characters)');
	});
	
	
function validateForm(){
	var acadYear = document.getElementById("acadYear").value;
	var acadMonth = document.getElementById("acadMonth").value;
	var sem = document.getElementById("sem").value;
	
	/* if((acadYear == "" && acadMonth == "" && sem == "") || (acadYear != "" && acadMonth != "" && sem != "")){
		
	}else{
		alert("Registration Month/Year/Sem should all be selected or None should be selected.");
		return false;
	}
	 */
	 
	 var notificationType = document.getElementById("notificationType").value;
	 var emailBody = document.getElementById("editor").value;
	 var smsBody = document.getElementById("smsContent").value;
	 
	 if(notificationType == "Email" && emailBody.trim() === ""){
		alert("Please enter Email Body");
		return false;
	}else if(notificationType == "SMS"  && smsBody.trim() === ""){
		alert("Please enter SMS Content");
		return false;
	}else if(notificationType == "Email-SMS" && (emailBody.trim() === "" || smsBody.trim() === "") ){
		alert("Please enter Email/SMS Body");
		return false;
	}
	return true;
}


$('#editor').editable({inlineMode: false,
    buttons: ['bold', 'italic', 'underline', 'sep', 
              'strikeThrough', 'subscript', 'superscript', 'sep',
              'fontFamily', 'fontSize', 'color', 'formatBlock', 'blockStyle', 'inlineStyle','sep',
              'align', 'insertOrderedList', 'insertUnorderedList', 'outdent', 'indent', 'selectAll','sep',
              'createLink', 'table','sep',
              'undo', 'redo', 'sep',
              'insertHorizontalRule', 'removeFormat', 'fullscreen'],
    minHeight: 200,
    paragraphy: false,
    placeholder: 'Enter Email Body here',
    theme: 'blue',
    toolbarFixed: false
});

function showCriteria(){
	var enrollmentMonth = document.getElementById("enrollmentMonth").value;
	var enrollmentYear = document.getElementById("enrollmentYear").value;
	var acadYear = document.getElementById("acadYear").value;
	var acadMonth = document.getElementById("acadMonth").value;
	var sem = document.getElementById("sem").value;
	var program = document.getElementById("program").value;
	var prgmStructApplicable = document.getElementById("prgmStructApplicable").value;
	var notificationType = document.getElementById("notificationType").value;
	
	
	
	var criteria = "<b>Send " + notificationType + " to All Active Students, who... </b><br>";
	if(enrollmentMonth != "" && enrollmentYear != ""){
		criteria += " have taken admission in " + enrollmentMonth +"-" + enrollmentYear + " Drive, <br>";
	}
	
	if(acadYear != "" && acadMonth != ""){
		criteria += " AND have registered for " + acadMonth +"-" + acadYear + " Academic Cycle, ";
		if(sem != ""){
			criteria += " for Sem " + sem +", ";
		}
		criteria += "<br>";
	}
	if(program != "" ){
		criteria += " AND have enrolled for " + program + " Program, <br>";
	}
	
	if(prgmStructApplicable != "" ){
		criteria += " AND and belong to " + prgmStructApplicable + " Program Strucutre ";
	}
	
	$("#criteria").html(criteria);
	
}

</script>
</body>
</html>
