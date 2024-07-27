<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
    
    
    <jsp:include page="../common/jscss.jsp">
	<jsp:param value="Edit Reply" name="title"/>
    </jsp:include>
    
    <!-- Froala wysiwyg editor CSS -->
<link href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/froala_editor.min.css" />" rel="stylesheet">
<link href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/froala_style.min.css" />" rel="stylesheet">
<link href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/froala_content.min.css" />" rel="stylesheet">

<link href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/themes/dark.min.css" />" rel="stylesheet">	
<link href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/themes/grey.min.css" />" rel="stylesheet">	
<link href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/themes/red.min.css" />" rel="stylesheet">	
<link href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/themes/royal.min.css" />" rel="stylesheet">		
<link href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/themes/blue.min.css" />" rel="stylesheet">	
    
    <style>
    hr {
	    margin-top: 5px;
	    margin-bottom: 5px;
	}
    
    </style>
    <body>
    
    	
    	
        
        <div class="panel-content-wrapper">
			<form:form  action="replyToForumThread" method="post" modelAttribute="forumBean">
				<fieldset>
				
				<form:hidden path="parentPostId"/>
				<form:hidden path="id"/>
				<div class="row">
						
						<div class="col-md-12 post-sub">
							<div class="form-group" style="margin-top:5px;">
								<form:textarea id="editor" path="description" required="required" name="description"/>
							</div>
						</div>
						
						<div class="form-group">
							<label class="control-label" for="submit"></label>
							<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="editReply">Edit</button>
							<button id="submit" name="submit" class="btn btn-large btn-danger" onclick="window.close();">Cancel</button>
						</div>
				</div>
				
				</fieldset>
			</form:form>
		</div>
            
  	
        <script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script> 
		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script> 
		
		<!-- Custom Included JavaScript Files --> 
		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/moment.min.js"></script> 
		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/fullcalendar.min.js"></script>
		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/jquery.plugin.min.js"></script>
		
		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/jquery.countdown.js"></script>
		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/js/main.js"></script>
		<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/jquery-ui.min.js"></script>
            
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
		    placeholder: 'Enter your reply here',
		    theme: 'blue',
		    key:'vA-16ddvvzalxvB-13C2uF-10A-8mG-7eC5lnmhuD3mmD-16==',
		    toolbarFixed: false
		});
		
		</script>
    </body>
</html>