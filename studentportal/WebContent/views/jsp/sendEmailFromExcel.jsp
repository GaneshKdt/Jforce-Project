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
				<form action="sendEmailFromExcel" method="post"
					enctype="multipart/form-data">
					<fieldset>

						<div class="row">
							<div class="column col-md-6">
								<div class="form-group ">
									<input type="text" name="subject" id="subject"
										required="required" class="form-control"
										placeholder="Enter Email Subject" />
								</div>

								<div class="form-group ">
									<label for="file">Select Excel File for Emails</label> <input
										id="file" name="file" type="file" class="form-control"
										required="required" />
								</div>

								<div class="form-group ">
									<label for="fromEmailId">From Email ID</label> <select
										id="fromEmailId" name="fromEmailId" class="form-control"
										required="required">
										<option value="">Select Email ID</option>
										<option value="ngasce@nmims.edu">ngasce@nmims.edu</option>
										<option value="ngasce.exams@nmims.edu">ngasce.exams@nmims.edu</option>
										<option value="ngasce.academics@nmims.edu">ngasce.academics@nmims.edu</option>
									</select>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="column col-md-18">
								<div class="form-group">
									<textarea id="editor" name="body" required="required"></textarea>
								</div>

								<div class="form-group">
									<div class="controls">
										<button id="submit" name="submit"
											class="btn btn-large btn-primary"
											formaction="sendEmailFromExcel">Submit</button>
										<button id="cancel" name="cancel" class="btn btn-danger"
											formaction="home" formnovalidate="formnovalidate">Cancel</button>
									</div>
								</div>
							</div>
						</div>
					</fieldset>
				</form>
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

</script>
</body>
</html>
