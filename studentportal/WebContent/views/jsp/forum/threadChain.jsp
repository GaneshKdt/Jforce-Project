<!DOCTYPE html>
<%@page import="com.nmims.beans.StudentMarksBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>  



<html lang="en">


<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Forum" name="title" />
</jsp:include>

<!-- Froala wysiwyg editor CSS -->
<link
	href="<c:url value="${pageContext.request.contextPath}/resources_2015/css/froala/froala_editor.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="${pageContext.request.contextPath}/resources_2015/css/froala/froala_style.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="${pageContext.request.contextPath}/resources_2015/css/froala/froala_content.min.css" />"
	rel="stylesheet">

<link
	href="<c:url value="${pageContext.request.contextPath}/resources_2015/css/froala/themes/dark.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="${pageContext.request.contextPath}/resources_2015/css/froala/themes/grey.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="${pageContext.request.contextPath}/resources_2015/css/froala/themes/red.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="${pageContext.request.contextPath}/resources_2015/css/froala/themes/royal.min.css" />"
	rel="stylesheet">
<link
	href="<c:url value="${pageContext.request.contextPath}/resources_2015/css/froala/themes/blue.min.css" />"
	rel="stylesheet">

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

hr {
	margin-top: 5px;
	margin-bottom: 5px;
}

.post-sub .panel-toggler[aria-expanded="true"]:before {
	content: "hide reply"; 
}

.post-sub .panel-toggler[aria-expanded="false"]:before {
	content: "view reply";
	
}

a.panel-toggler {
	font-size: 1.2em;
}
.post-sub{
    border-bottom: 3px solid #a7a7a778;
    padding: 15px; 
}
.mod-title {
    font-size: 20px; 
    color: #26a9e0;
    font-style: italic;
    font-weight:500;
}
.description{
 	padding-top: 25px;
}
.flex{
display: flex;
} 
</style>
<%
	String earlyAccess = (String)session.getAttribute("earlyAccess"); //They should not view edit,post,report abuse button.//
    System.out.println("EARLY ACCESS JSP-->"+earlyAccess);
%>
<body>

	<%@ include file="../common/header.jsp"%>



	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Acads;${forumBean.subject};Forum"
				name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			<div id="sticky-sidebar"> 
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="Marks History" name="activeMenu" />
				</jsp:include>
			</div>

				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						
						<div class="clearfix"></div>

						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
							<form:form action="replyToForumThread" method="post"
								modelAttribute="forumBean">
								<fieldset>

									<form:hidden path="parentPostId" />
									<form:hidden path="id" />
									<form:hidden path="subject" />

									<div class="row">
										<div class="col-md-12">
											<div class="pull-right">
												<a href="${newest}">Newest</a> | <a href="${oldest}">Oldest</a>
											</div>
											</br></br>
											<div class="post-sub" data-toggle="modal"
												data-target="#postSubject" data-dismiss="modal">
												<div class="col-sm-9">
												<div class="flex">
												<div class=""><img src="${pageContext.request.contextPath}/assets/images/admin.png" width="35px" style="border-radius: 50%;">&nbsp&nbsp
												</div >
												<div class="">
												<a class="red text-capitalize mod-title">${forumBean.title}</a></br>
													<span class="mod-name"><b>Prof.${forumBean.firstName }
															${forumBean.lastName }</b>  </span>
												</div>
												</div>
												<span></span>
												</div>
												<div class="col-sm-3">
													<span class="mod-date pull-right">on ${forumBean.createdDate}</span>
												</div>
												<div class="col-sm-12">
													<div class="dummyForumClass description">${forumBean.description }</div>
												</div>
												<div class="clearfix"></div>
											</div>
											
											<c:forEach items="${forumBean.threadReplies}" var="reply"
												varStatus="status">

												<div class="post-sub" id="demo">
													<div class="col-sm-9">
														<div class="flex">
															<div class=""><img src="${reply.imageUrl}" width="35px" style="border-radius: 50%;">&nbsp&nbsp
															</div >
															<div class="">
																<span class="mod-name"><b><c:if test="${reply.facultyId ne null}">Prof.</c:if>
																${reply.firstName} ${reply.lastName}</b>  </span>
															</div>
														</div>
													</div>
													<div class="col-sm-3">
														<span class="mod-date pull-right">on ${reply.createdDate}</span> 
													</div>

													<div class="col-sm-12">
														<div class="dummyForumClass description">${reply.description}</div>
													</div>
													<%if("No".equalsIgnoreCase(earlyAccess)){%>
													<div class="pull-right">
														<a href="#"
															onClick="window.open('/studentportal/student/postLevel2ReplyForm?parentReplyId=${reply.id }', 'Post Reply', 'width=800px,height=500px');"><i class="fa fa-reply" aria-hidden="true"></i> Reply</a>
														<%if(roles.indexOf("Acads Admin") != -1 || roles.indexOf("Faculty") != -1){%>
														| <a
															href="/studentportal/student/deleteForumReply?replyId=${reply.id}&parentPostId=${reply.parentPostId}">Delete</a>
														<%}%>
														<c:if test="${reply.createdBy == userId}">
												   		| <a href="#"
																onClick="window.open('/studentportal/student/editReplyForm?id=${reply.id}', 'Edit Reply', 'width=800px,height=500px');">Edit</a>
														</c:if>
														<%-- | <a href="#"
																onClick="window.open('/studentportal/student/FlagForumReply?id=${reply.id}', 'Edit Reply', 'width=800px,height=500px');">
													  		<i class="fa fa-flag"  aria-hidden="true"></i>
														  </a>
														 --%>
														| <a href="/studentportal/student/reportAbuse?id=${reply.id}"
															onClick="return confirm('Are you sure you want to report this reply as Abuse?')">Report
															Abuse</a>
													</div>
													<%}%>
													<div class="clearfix"></div>
													<c:if test="${fn:length(reply.threadReplies) >0}">
													
														<div style="margin-left: 60px; margin-top: 7px;"
															id="level2Replyid${reply.id}"
															class="panel-collapse collapse in">
															
															<p>
															  <a class="panel-toggler collapsed "  data-toggle="collapse" href="#collapseExample${reply.id}"  
															  aria-expanded="false" aria-controls="collapseExample${reply.id}"> (${fn:length(reply.threadReplies)})</a>
															</p>
															<div class="collapse" id="collapseExample${reply.id}">
															  	<c:forEach items="${reply.threadReplies}"
																	var="level2Reply">
																	<div class="col-sm-9">
																		<div class="flex">
																			<div class="">
																				<img src="${level2Reply.imageUrl}" width="35px"
																					style="border-radius: 50%;">&nbsp&nbsp
																			</div>
																			<div class="">
																				<span class="mod-name"><b><c:if
																							test="${level2Reply.facultyId ne null}">Prof.</c:if>
																						${level2Reply.firstName} ${level2Reply.lastName}</b> </span>
																			</div>
																		</div>
																	</div>
																	<div class="col-sm-3">
																		<span class="mod-date pull-right">on
																			${level2Reply.createdDate}</span>
																	</div>
																	<div class="col-sm-12">
																		<div class="dummyForumClass description">
																			${level2Reply.description }</div>
																	</div>
																	<%if("No".equalsIgnoreCase(earlyAccess)){%>
																	<div class="pull-right">
																		<a href="#"
																			onClick="window.open('/studentportal/student/postLevel2ReplyForm?parentReplyId=${level2Reply.parentReplyId }', 'Post Reply', 'width=800px,height=500px');"><i class="fa-solid fa-reply" aria-hidden="true"></i> Reply</a>
																		<%if(roles.indexOf("Acads Admin") != -1 ){%>
																		| <a
																			href="/studentportal/student/deleteForumReply?replyId=${level2Reply.id}">Delete</a>
																		<%}%>
																		<c:if test="${level2Reply.createdBy == userId}">
																		| <a href="#"
																				onClick="window.open('/studentportal/student/editReplyForm?id=${level2Reply.id }', 'Edit Reply', 'width=800px,height=500px');">Edit</a>
																		</c:if>
																		<%-- | <a href="#"
																			onClick="window.open('/studentportal/student/FlagForumReply?id=${reply.id}', 'Edit Reply', 'width=800px,height=500px');">
																			<i class="fa fa-flag" aria-hidden="true"></i>
																		</a> --%> | 
																		<a
																			href="/studentportal/student/reportAbuse?id=${level2Reply.id}&isLevel2Reply=true"
																			onClick="return confirm('Are you sure you want to report this reply as Abuse?')">Report
																			Abuse</a>
																	</div>
																	<%}%>
																	<div class="clearfix"></div>
																</c:forEach>
															</div>
														</div>
													</c:if>
												</div>
												
											</c:forEach>
										</div>
										<div class="col-md-12 post-sub">
											<div class="form-group" style="margin-top: 5px;">
												<textarea id="editor" required="required" name="description"></textarea>
											</div>
										</div>
										<div class="form-group">
											<label class="control-label" for="submit"></label>
											<%if("No".equalsIgnoreCase(earlyAccess)){%>
											<button id="submit" name="submit"
												class="btn btn-large btn-primary"
												formaction="replyToForumThread">Reply</button>
											<%}%>
											<button id=cancel name="cancel"
												class="btn btn-large btn-danger" formaction="home"
												formnovalidate="formnovalidate">Back to Course</button>
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


	<jsp:include page="../common/footer.jsp" />

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
					placeholder : 'Enter your reply here',
					theme : 'blue',
					key : 'vA-16ddvvzalxvB-13C2uF-10A-8mG-7eC5lnmhuD3mmD-16==',
					toolbarFixed : false
				});
	</script>
</body>
</html>