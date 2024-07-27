
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<%@page import="com.nmims.beans.ForumBean"%>
<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>

<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../jscss.jsp">
	<jsp:param value="Queries" name="title" />
</jsp:include>

<!-- Froala wysiwyg editor CSS -->
<link
	href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/froala_editor.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/froala_style.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/froala_content.min.css" />"
	rel="stylesheet">

<link
	href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/themes/dark.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/themes/grey.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/themes/red.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/themes/royal.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/css/froala/themes/blue.min.css" />"
	rel="stylesheet">

<body class="inside">

	<style>
span.student-image {
	display: block;
	float: left;
	height: 40px;
	width: 40px;
	background-size: cover;
	background-position: center;
	border-radius: 50%;
}

.ui-state-hover {
	background: #DED9DA;
}
</style>

	<%@ include file="../header.jsp"%>

	<section class="content-container login">
		<div class="container-fluid customTheme">
			<div class="row">
				<legend>View Discussion Thread</legend>
			</div>
			<%@ include file="../messages.jsp"%>
			<form:form action="replyToForumThread" method="post"
				modelAttribute="forumBean">
				<fieldset>
					<!-- Unanswered Queries section -->
					<form:hidden path="parentPostId" />
					<form:hidden path="id" />
					<form:hidden path="subject" />
					<div class="panel-body">


						<div class="panel-body">
							<div class="col-md-12">
								<div class="pull-right">
									<a href="${newest}">Newest</a> | <a href="${oldest}">Oldest</a>
								</div>
								<div class="post-sub" data-toggle="modal"
									data-target="#postSubject" data-dismiss="modal">
									<div class="col-sm-9">
										<span class="mod-name"><b>Prof.
												${forumBean.firstName } ${forumBean.lastName	 }</b><span>
												said:</span></span>
									</div>
									<div class="col-sm-3">
										<span class="mod-date">on ${forumBean.createdDate}</span>
									</div>
									<div class="col-sm-12">
										<div class="dummyForumClass">${forumBean.description }</div>
									</div>
									<div class="clearfix"></div>
								</div>
								<hr style="margin-top: 10px; margin-bottom: 10px;" />

								<c:forEach items="${forumBean.threadReplies}" var="reply"
									varStatus="status">

									<div class="post-sub" id="demo">
										<div class="col-sm-9">
											<span class="student-image"
												style="background-image:url(${reply.imageUrl});"></span> 
												<span class="mod-name" style="padding-left: 10px;"><b>
												<c:if test="${reply.facultyId ne null}">Prof.</c:if>${reply.firstName}
													${reply.lastName}</b> <span> said:</span></span>
										</div>
										<div class="col-sm-3">
											<span class="mod-date">on ${reply.createdDate}</span> <a
												class="panel-toggler collapsed pull-right" role="button"
												data-toggle="collapse" href="#level2Replyid${reply.id}"
												aria-expanded="true"></a>
										</div>

										<div class="col-sm-12">
											<div class="dummyForumClass">${reply.description}</div>
										</div>
										<div class="pull-right">
											<a href="#"
												onClick="window.open('/acads/admin/facultyReplyForm?parentReplyId=${reply.id }', 'Post Reply', 'width=800px,height=300px');">Reply</a>
											| <a href="/acads/admin/reportAbuse?id=${reply.id}"
												onClick="return confirm('Are you sure you want to report this reply as Abuse?')">Report
												Abuse</a> | <a href="#" class="editable" id="requestStatus"
												data-type="select" data-pk="${reply.id}"
												data-source="[{value: 'Draft', text: 'Draft'},{value: 'Deleted', text: 'Deleted'},{value: 'Active', text: 'Active'}]"
												data-url="saveThreadReplyStatus"
												data-title="Select Status of Forum">${reply.status}</a>
										</div>
										<div class="clearfix"></div>


										<div style="margin-left: 60px; margin-top: 7px;"
											id="level2Replyid${reply.id}"
											class="panel-collapse collapse in">
											<c:forEach items="${reply.threadReplies}" var="level2Reply">
												<hr />
												<div class="col-sm-9">
													<span class="student-image"
														style="background-image:url(${level2Reply.imageUrl});"></span>
													<span class="mod-name" style="padding-left: 10px;"><b><c:if
																test="${level2Reply.facultyId ne null}">Prof.</c:if>${level2Reply.firstName}
															${level2Reply.lastName} </b> <span> said:</span></span>
												</div>
												<div class="col-sm-3">
													<span class="mod-date">on ${level2Reply.createdDate}</span>
												</div>
												<div class="col-sm-12">
													<div class="dummyForumClass">
														${level2Reply.description}</div>
												</div>
												<div class="pull-right">
													<a href="#"
														onClick="window.open('/acads/admin/facultyReplyForm?parentReplyId=${level2Reply.parentReplyId }', 'Post Reply', 'width=800px,height=300px');">Reply</a>
													| <a
														href="/acads/admin/reportAbuse?id=${level2Reply.id}&isLevel2Reply=true"
														onClick="return confirm('Are you sure you want to report this reply as Abuse?')">Report
														Abuse</a> | <a href=""
														onClick="window.open('/acads/admin/editReplyForm?id=${level2Reply.id}', 'Edit Reply', 'width=800px,height=300px');">Edit</a>
													| <a href="#" class="editable" id="requestStatus"
														data-type="select" data-pk="${level2Reply.id}"
														data-source="[{value: 'Draft', text: 'Draft'},{value: 'Deleted', text: 'Deleted'},{value: 'Active', text: 'Active'}]"
														data-url="saveThreadReplyStatus"
														data-title="Select Status of Forum">${level2Reply.status}</a>
												</div>
												<div class="clearfix"></div>
											</c:forEach>
										</div>
									</div>
									<hr />
								</c:forEach>

							</div>
							<div class="col-md-12 post-sub">
								<div class="form-group" style="margin-top: 5px;">
									<textarea id="editor" required="required" name="description"></textarea>
								</div>
								<button id="submit" name="submit"
									class="btn btn-large btn-primary"
									formaction="replyToForumThread">Reply</button>
								<button id=cancel name="cancel" class="btn btn-large btn-danger"
									formaction="searchForumThreadForm"
									formnovalidate="formnovalidate">Back to Course</button>

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
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/font_family.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/font_size.min.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/block_styles.min.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/media_manager.min.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/inline_styles.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/fullscreen.min.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/char_counter.min.js"></script>
	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/entities.min.js"></script>
	<script
		src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/froala-plugins/file_upload.min.js"></script>
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
					placeholder : 'Enter Email Body here',
					theme : 'blue',
					key : 'vA-16ddvvzalxvB-13C2uF-10A-8mG-7eC5lnmhuD3mmD-16==',
					toolbarFixed : false
				});
	</script>


	<script src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/js/vendor/bootstrap-editable.js"></script>

	<script>
		$(function() {
			//toggle `popup` / `inline` mode
			$.fn.editable.defaults.mode = 'inline';

			$('.editable').each(function() {
				$(this).editable({
					success : function(response, newValue) {
						obj = JSON.parse(response);
						if (obj.status == 'error') {
							return obj.msg; //msg will be shown in editable form
						}
					}
				});
			});

		});
	</script>

</body>
</html>
