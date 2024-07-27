<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->


<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:include page="jscss.jsp">
	<jsp:param value="Session Cancellation" name="title" />
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

	<%@ include file="header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">

			<div class="row">
				<legend>Session Cancellation</legend>
			</div>

			<%@ include file="messages.jsp"%>

			<div class="panel-body">

				<form:form  action="sessionCancellation" method="POST" modelAttribute="session">
					<fieldset>
					<form:hidden path="id"/>
					<form:hidden path="subject"/>
					<form:hidden path="sessionName"/>
					<form:hidden path="date"/>
					<form:hidden path="startTime"/>
					<form:hidden path="endTime"/>
					<form:hidden path="consumerProgramStructureId"/>
					
					<c:url value="refreshSession" var="refreshurl">
					  <c:param name="id" value="${session.id}" />
					</c:url>
					
					<div>
						<p>Subject, Session: ${session.subject} - ${session.sessionName}</p>
					</div>
					
					<div >
						<p>Faculty Name: ${session.firstName} ${session.lastName}</p>
					</div>
				
					<div >
						<p>Date & Time: ${session.day}, ${session.date}, ${session.startTime}</p>
					</div>
					
					<div >
						<p>No Of Student Registered For Subject ${noOfStudentRegisteredForSubject}</p>
					</div>
					
					<div class="clearfix"></div>	
					
					<div class="panel-body">
			
						<div class="col-md-18 column">
							<div id="cancelledSession" style="display:block;">
								<div class="form-group">
									<form:select id="isCancelled" path="isCancelled"  class="form-control" required="true" itemValue="${session.isCancelled}">
										<form:option value="">Select Y/N</form:option>
										<form:option value="Y">Yes</form:option>
										<form:option value="N">No</form:option>
									</form:select>
								</div>	
								
								<div class="form-group">
									<form:textarea path="reasonForCancellation" id="reasonForCancellation" class="form-control" required="true" placeholder="Enter reason for Session Cancellation" cols="50" rows="3" />
								</div>
								
								<div class="form-group">
									<form:input path="cancellationSubject" id="cancellationSubject" class="form-control" required="true" placeholder="Enter Subject For Announcement And Email"/>
								</div>
								
								<div class="form-group">
									<form:textarea path="cancellationSMSBody" id="cancellationSMSBody" class="form-control" required="true" placeholder="Enter SMS Text .. " cols="50" rows="3"/>
								</div>
								
								<div class="form-group">
									<form:textarea path="cancellationEmailBody" id="cancellationEmailBody" class="form-control" required="true" />
								</div>
								
								<div class="form-group">
								   <label>Announcement Start Date</label>
									<form:input id="startDate" path="startDate" type="date"  class="form-control" />
								</div>
						
								<div class="form-group">
								    <label>Announcement End Date</label>
									<form:input id="endDate" path="endDate" type="date" class="form-control" />
								</div>
							</div>
								
							<div id="reScheduleSession" style="display:none;">
							   
								   <div class="form-group">
									   <label>Session Re-Schedule Date</label>
										<form:input id="date" path="reScheduleDate" type="date"  class="form-control" />
									</div>
							
									<div class="form-group">
									   <label>Start Time</label>
										<form:input id="reScheduleStartTime" path="reScheduleStartTime" type="time" class="form-control" />
									</div>
							
									<div class="form-group">
									    <label>End Time</label>
										<form:input id="reScheduleEndTime" path="reScheduleEndTime" type="time" class="form-control" />
									</div>
									
									<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="reScheduleSession"
									>Re-Schedule Session</button>
									
						  </div>
										
							<div class="form-group" id="reScheduleSessions" style="display:block;">
								<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="sessionCancellation"
								>Cancel Session</button>
								
								
								<button id="reSheduleSession" name="reSheduleSession" type="button" class="btn btn-large btn-primary" 
								>Re-Schedule Session</button>
								
							</div>
							
						</div>
					</div>
					</fieldset>
				</form:form>
				<br>
			</div>

		</div>

	</section>

	<jsp:include page="footer.jsp" />
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

$('#cancellationEmailBody').editable({inlineMode: false,
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

$('#reSheduleSession').click(function(){
	document.getElementById("cancelledSession").style.display = 'none';
	document.getElementById("reScheduleSessions").style.display = 'none';
	document.getElementById("reScheduleSession").style.display = 'block';
});
</script>

</body>
</html>
