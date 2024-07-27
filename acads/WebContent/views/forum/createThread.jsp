<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!--> 

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.PageAcads"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html class="no-js"> <!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<jsp:include page="../jscss.jsp">
<jsp:param value="Queries" name="title" />
</jsp:include>

<!-- Froala wysiwyg editor CSS -->
<link href="<c:url value="/resources_2015/css/froala/froala_editor.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources_2015/css/froala/froala_style.min.css" />" rel="stylesheet">
<link href="<c:url value="/resources_2015/css/froala/froala_content.min.css" />" rel="stylesheet">

<link href="<c:url value="/resources_2015/css/froala/themes/dark.min.css" />" rel="stylesheet">	
<link href="<c:url value="/resources_2015/css/froala/themes/grey.min.css" />" rel="stylesheet">	
<link href="<c:url value="/resources_2015/css/froala/themes/red.min.css" />" rel="stylesheet">	
<link href="<c:url value="/resources_2015/css/froala/themes/royal.min.css" />" rel="stylesheet">		
<link href="<c:url value="/resources_2015/css/froala/themes/blue.min.css" />" rel="stylesheet">	
<body class="inside">

<style>

.ui-state-hover{
	background:#DED9DA;
}

</style>

<%@ include file="../header.jsp"%>
	
    <section class="content-container login">
        <div class="container-fluid customTheme">
        <div class="row"><legend>Create Discussion Thread</legend></div>
        <%@ include file="../messages.jsp"%>
		
		<!-- Unanswered Queries section -->
		<div class="panel-body">
			<h2>Forum will be made live for ${forumLiveSession } academic cycle</h2>
			
			<form:form  action="createForumThread" method="post" modelAttribute="forumBean">
				<fieldset>
				<div class="panel-body">
				
				<div class="col-md-9 column">
						
						
						<div class="form-group" style="overflow:visible;">
								<form:select id="subject" path="subject"  class="combobox form-control" required="required"   itemValue="${forumBean.subject}">
									<form:option value="">Type OR Select Subject</form:option>
									<form:options items="${subjectList}" />
								</form:select>
						</div>
						
						<div class="form-group" >
								<form:select id="status" path="status"  class=" form-control" required="required"   itemValue="${forumBean.status}">
									<form:option value="">Select Thread Status</form:option>
									<form:option value="Draft">Draft</form:option>
									<form:option value="Active">Active</form:option>
									<form:option value="Deleted">Deleted</form:option>
								</form:select>
						</div>
						
				</div>
				
				
				<div class="col-md-18 column">
						<div class="form-group">
						<form:input id="title" path="title" type="text"
							placeholder="Insert Thread Title" class="form-control" required="required" value="${forumBean.title}"/>
					</div>
					
						<div class="form-group">
							<form:textarea path="description" id="editor" required="required" title="${forumBean.description}"/>
						</div>
					
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<button id="submit" name="submit" class="btn btn-large btn-primary"  formaction="createForumThread">Create Thread</button>
							<button id="cancel" name="cancel" class="btn btn-danger" formaction="acadsHome" formnovalidate="formnovalidate">Cancel</button>
						</div>
						
				</div>
				
				</div>
				</fieldset>
			</form:form>
		</div>
		
		
		
		
	</div>
	
	</section>

	<jsp:include page="../footer.jsp" />
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
    key:'vA-16ddvvzalxvB-13C2uF-10A-8mG-7eC5lnmhuD3mmD-16==',
    toolbarFixed: false
});

</script>
	  

</body>
</html>
