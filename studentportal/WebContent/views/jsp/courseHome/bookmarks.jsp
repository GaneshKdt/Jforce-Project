
<!DOCTYPE html>

<html lang="en">




<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Bookmarks" name="title" />
</jsp:include>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.nmims.beans.ContentStudentPortalBean"%>
<%@page import="com.nmims.beans.VideoContentStudentPortalBean"%>
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
	ArrayList<VideoContentStudentPortalBean> videoContentList = (ArrayList<VideoContentStudentPortalBean>) session
			.getAttribute("bookmarkVideoCotentList");
	ArrayList<ContentStudentPortalBean> contentList = (ArrayList<ContentStudentPortalBean>) session.getAttribute("bookmarkContentList");

	request.setAttribute("contentList", contentList);
%>

<head>
<style>
.card {
	background-color: #fff;
	border: 1px solid transparent;
	border-radius: 6px;
}

.card>.card-link {
	color: #333;
}

.card>.card-link:hover {
	text-decoration: none;
}

.card>.card-link .card-img img {
	border-radius: 6px 6px 0 0;
}

.card .card-img {
	position: relative;
	padding: 0;
	display: table;
}

.card .card-img .card-caption {
	position: absolute;
	right: 0;
	bottom: 16px;
	left: 0;
}

.card .card-body {
	display: table;
	width: 100%;
	padding: 12px;
}

.card .card-header {
	border-radius: 6px 6px 0 0;
	padding: 8px;
}

.card .card-footer {
	border-radius: 0 0 6px 6px;
	padding: 8px;
}

.card .card-left {
	position: relative;
	float: left;
	padding: 0 0 8px 0;
}

.card .card-right {
	position: relative;
	float: left;
	padding: 8px 0 0 0;
}

.card .card-body h1:first-child, .card .card-body h2:first-child, .card .card-body h3:first-child,
	.card .card-body h4:first-child, .card .card-body .h1, .card .card-body .h2,
	.card .card-body .h3, .card .card-body .h4 {
	margin-top: 0;
}

.card .card-body .heading {
	display: block;
}

.card .card-body .heading:last-child {
	margin-bottom: 0;
}

.card .card-body .lead {
	text-align: center;
}

@media ( min-width : 768px ) {
	.card .card-left {
		float: left;
		padding: 0 8px 0 0;
	}
	.card .card-right {
		float: left;
		padding: 0 0 0 8px;
	}
	.card .card-4-8 .card-left {
		width: 33.33333333%;
	}
	.card .card-4-8 .card-right {
		width: 66.66666667%;
	}
	.card .card-5-7 .card-left {
		width: 41.66666667%;
	}
	.card .card-5-7 .card-right {
		width: 58.33333333%;
	}
	.card .card-6-6 .card-left {
		width: 50%;
	}
	.card .card-6-6 .card-right {
		width: 50%;
	}
	.card .card-7-5 .card-left {
		width: 58.33333333%;
	}
	.card .card-7-5 .card-right {
		width: 41.66666667%;
	}
	.card .card-8-4 .card-left {
		width: 66.66666667%;
	}
	.card .card-8-4 .card-right {
		width: 33.33333333%;
	}
}

/* -- default theme ------ */
.card-default {
	border-color: #ddd;
	background-color: #fff;
	margin-bottom: 24px;
}

.card-default>.card-header, .card-default>.card-footer {
	color: #333;
	background-color: #ddd;
}

.card-default>.card-header {
	border-bottom: 1px solid #ddd;
	padding: 8px;
}

.card-default>.card-footer {
	border-top: 1px solid #ddd;
	padding: 8px;
}

.card-default>.card-body {
	
}

.card-default>.card-img:first-child img {
	border-radius: 6px 6px 0 0;
}

.card-default>.card-left {
	padding-right: 4px;
}

.card-default>.card-right {
	padding-left: 4px;
}

.card-default p:last-child {
	margin-bottom: 0;
}

.card-default .card-caption {
	color: #fff;
	text-align: center;
	text-transform: uppercase;
}

/* -- price theme ------ */
.card-resource {
	border-color: #999;
	background-color: #ededed;
	margin-bottom: 24px;
}

.card-resource>.card-heading, .card-resource>.card-footer {
	color: #333;
	background-color: #fdfdfd;
}

.card-resource>.card-heading {
	border-bottom: 1px solid #ddd;
	padding: 8px;
}

.card-resource>.card-footer {
	border-top: 1px solid #ddd;
	padding: 8px;
}

.card-resource>.card-img:first-child img {
	border-radius: 6px 6px 0 0;
}

.card-resource>.card-left {
	padding-right: 4px;
}

.card-resource>.card-right {
	padding-left: 4px;
}

.card-resource .card-caption {
	color: #fff;
	text-align: center;
	text-transform: uppercase;
}

.card-resource p:last-child {
	margin-bottom: 0;
}

.card-resource .price {
	text-align: center;
	color: #337ab7;
	font-size: 3em;
	text-transform: uppercase;
	line-height: 0.7em;
	margin: 24px 0 16px;
}

.card-resource .price small {
	font-size: 0.4em;
	color: #66a5da;
}

.card-resource .details {
	list-style: none;
	margin-bottom: 24px;
	padding: 0 18px;
}

.card-resource .details li {
	text-align: center;
	margin-bottom: 8px;
}

.card-resource .buy-now {
	text-transform: uppercase;
}

.card-resource table .price {
	font-size: 1.2em;
	font-weight: 700;
	text-align: left;
}

.card-resource table .note {
	color: #666;
	font-size: 0.8em;
}

.disableClick {
	pointer-events: none;
	cursor: default;
	border-radius: 0;
	box-shadow: none;
	border-color: #d7ccc8;
}

.panel-default>.panel-heading {
	padding: 0;
	border-radius: 0;
	color: #212121;
	background-color: #ffffff;
	border-color: #EEEEEE;
}

.panel-title {
	font-size: 14px;
}

.panel-title>a {
	display: block;
	padding: 15px;
	text-decoration: none;
}

.more-less {
	float: right;
	color: #212121;
}

.panel-default>.panel-heading+.panel-collapse>.panel-body {
	border-top-color: #EEEEEE;
}

.tile-progress {
	color: #fff;
	margin-bottom: 20px;
	-webkit-border-radius: 5px;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-background-clip: padding-box;
	-moz-background-clip: padding;
	background-clip: padding-box;
	-webkit-border-radius: 3px;
	-moz-border-radius: 3px;
	border-radius: 3px;
}

.tile-progress .tile-header {
	padding: 20px 20px 20px 20px;
	background-color: #0288D1;
	border-top-left-radius: 7px;
	border-top-right-radius: 7px;
}

.tile-progress .tile-progressbar {
	height: 5px;
	background: rgba(0, 0, 0, 0.18);
	margin: 0;
}

.tile-progress .tile-progressbar span {
	background: #fff;
}

.tile-progress .tile-progressbar span {
	display: block;
	background: #e25050;
	width: 0;
	height: 100%;
	-webkit-transition: all 1.5s cubic-bezier(0.230, 1.000, 0.320, 1.000);
	-moz-transition: all 1.5s cubic-bezier(0.230, 1.000, 0.320, 1.000);
	-o-transition: all 1.5s cubic-bezier(0.230, 1.000, 0.320, 1.000);
	transition: all 1.5s cubic-bezier(0.230, 1.000, 0.320, 1.000);
}

.tile-progress .tile-footer {
	padding: 10px;
	text-align: left;
	background: white;
	color: black;
	-webkit-border-radius: 0 0 3px 3px;
	-webkit-background-clip: padding-box;
	-moz-border-radius: 0 0 3px 3px;
	-moz-background-clip: padding;
	border-radius: 0 0 3px 3px;
	background-clip: padding-box;
	-webkit-border-radius: 0 0 3px 3px;
	-moz-border-radius: 0 0 3px 3px;
	border-radius: 0 0 3px 3px;
	min-height: 100px;
}

.tile-progress.tile-red {
	background-color: #f56954;
	color: #fff;
}

.tile-progress.tile-blue {
	background-color: #0073b7;
	color: #fff;
}

.tile-progress.tile-aqua {
	background-color: #00c0ef;
	color: #fff;
}

.tile-progress.tile-green {
	background-color: #00a65a;
	color: #fff;
}

.tile-progress.tile-cyan {
	background-color: #00b29e;
	color: #fff;
}

.tile-progress.tile-purple {
	background-color: #ba79cb;
	color: #fff;
}

.tile-progress.tile-pink {
	background-color: #ec3b83;
	color: #fff;
}

.collapse {
	background-color: #eeeeee;
}

.tile-header span {
	font-size: 1rem;
	float: left;
}

.modal-footer {
	padding: 0;
	border-top: none;
}

#downloadVideo_Notused {
	pointer-events: none;
	cursor: default;
}

.fa {
	cursor: pointer;
}

.nodata {
	vertical-align: middle;
	color: #a6a8ab;
	font: 1.00em "Open Sans";
	text-align: center;
	margin: 0;
}

.panel-heading .accordion-toggle:after {
	/* symbol for "opening" panels */
	font-family: 'Glyphicons Halflings';
	content: "\e114";
	float: right;
	color: grey;
}

.panel-heading .accordion-toggle.collapsed:after {
	content: "\e080";
}     
</style>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

</head>
<body>

	<%@ include file="../common/header.jsp"%>


	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;Bookmarks" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
			   <div id="sticky-sidebar">
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="Bookmarks" name="activeMenu" />
					</jsp:include>
                </div>
				<div class="sz-content-wrapper examsPage">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">Bookmarks</h2>
						<div class="clearfix"></div>
						<div class="panel-content-wrapper">
							<%@ include file="../common/messages.jsp"%>
						
<!-- 						Check isLoginAsLead then no bookmarks is active -->
							<c:if test="${isLoginAsLead eq false }">
							
							<div id="collapseFive"
								class=" collapse in academic-schedule courses-panel-collapse panel-content-wrapper accordion-has-content"
								role="tabpanel">
								<%@ include file="../messages.jsp"%>
								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a class="accordion-toggle collapsed" data-toggle="collapse"
												data-parent="#accordion" href="#collapse1"
												aria-expanded="false"> Contents </a>
										</h4>
									</div>
									<div id="collapse1" class="panel-collapse collapse"
										aria-expanded="false">
										<div class="panel-body">
											<div class="panel-body" style="padding: 20px;">
												<c:choose>
													<c:when test='${contentList.size() != 0}'>
														<div class=" data-content panel-body resourceContent">
															<c:forEach var="contentFile" items="${contentList}"
																varStatus="status">
																<div class="row content">
																	<div class="col-sm-4"></div>
																	<div class="col-sm-4">
																		<div class="card card-resource panel-body"
																			style="border: 1px solid lightgray; border-radius: .25rem;">
																			<div class="card-body">
																				<div class="row">
																					<div class="col-sm-9">
																						<div class="lead" style="text-align: left;">
																							<c:out value="${contentFile.name}" />
																						</div>
																					</div>
																					<div class="col-sm-3">
																						<div style="text-align: right; font-size: 14px;">
																							<jsp:useBean id="now" class="java.util.Date" />
																							<fmt:parseDate
																								value="${contentFile.getCreatedDate()}"
																								var="date1" pattern="yyyy-MM-dd" />

																							<fmt:parseNumber
																								value="${(now.time - date1.time) / (1000*60*60*24) }"
																								var="diffDate" integerOnly="true" />
																							<c:if test="${diffDate == 1 }">
																								<c:out value="${diffDate} day ago" />
																							</c:if>
																							<c:if test="${diffDate > 7 }">
																								<c:out
																									value="${fn:substring(contentFile.getCreatedDate(),0,10)}" />
																							</c:if>
																							<c:if test="${diffDate > 1 && diffDate < 8}">
																								<c:out value="${diffDate} days ago" />
																							</c:if>
																						</div>
																					</div>
																				</div>
																				<div class="row">
																					<div class="col-sm-12">
																						<p>
																							<c:out value="${contentFile.description}" />
																							<c:set var="string1"
																								value="${contentFile.description}" />
																							<c:set var="string2"
																								value="${fn:toUpperCase(string1)}" />
																							<c:set var="string3"
																								value="${fn:toUpperCase(string1)}" />
																							<c:if
																								test="${fn:substring(string2,0,4) eq 'TO V'}"> 
																			   &nbsp;
																			   <a
																									href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/atrecply.msi">Windows</a> &nbsp;
																			   <a
																									href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/mac/intel/webexplayer_intel.dmg">Mac
																									OSX</a>
																							</c:if>
																						</p>
																					</div>
																				</div>
																				<div class="row"
																					style="margin: 1rem; padding: 0.3rem; border: 1px solid lightgray; border-radius: .25rem;">

																					<c:if test="${not empty contentFile.previewPath}">
																						<c:if
																							test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
																|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
																							<c:url value="acads/student/previewContent"
																								var="previewContentLink">
																								<c:param name="previewPath"
																									value="${contentFile.previewPath}" />
																								<c:param name="name" value="${contentFile.name}" />
																								<c:param name="type" value="PDF"></c:param>
																							</c:url>
																							<div class="col-sm-2">
																								<img height="50px" width="50px"
																									style="margin-top: 5px;"
																									src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/pdf.png" />
																							</div>
																							<div class="col-sm-10">
																								<div class="col-sm-12">
																									<div>
																										<span>Content Type : </span>
																										<c:out value="${contentFile.contentType}" />
																									</div>
																									<div>
																										<span>File Type : </span>
																										<c:out value="PDF"></c:out>
																									</div>
																									<div style="text-align: right;">
																										<a href="/${previewContentLink}"
																											target="_blank"><i class="fa-regular fa-eye"
																											style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i></a>
																										<i class="fa-solid fa-bookmark content"
																											id="${contentFile.id}"
																											onclick="removeBookmark(this)"
																											style="font-size: 20px; color: #fabc3d; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																									</div>
																								</div>
																							</div>

																						</c:if>
																						<c:if
																							test="${fn:endsWith(contentFile.previewPath, '.ppt') || fn:endsWith(contentFile.previewPath, '.PPT') || fn:endsWith(contentFile.previewPath, '.PPTX') || fn:endsWith(contentFile.previewPath, '.pptx')}">
																							<div class="col-sm-2">
																								<img height="50px" width="50px"
																									style="margin-top: 5px;"
																									src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/powerpoint.png" />
																							</div>
																							<div class="col-sm-10">
																								<div class="col-sm-12">
																									<div>
																										<span>Content Type : </span>
																										<c:out value="${contentFile.contentType}" />
																									</div>
																									<div>
																										<span>File Type : </span>
																										<c:out value="PPT"></c:out>
																									</div>
																									<div style="text-align: right;">
																										
																										<!-- CommentedBy: Siddheshwar Khanse
																											 Date		: Jan 19, 2021 10:30 AM(IST)
																											 Reason		: Preview of PPT content should not visible on portal -->
																										
																										<%-- <c:url value="acads/previewContent"
																											var="previewContentLink">
																											<c:param name="previewPath"
																												value="${contentFile.previewPath}" />
																											<c:param name="name"
																												value="${contentFile.name}" />
																										</c:url>
																										<a href="/${previewContentLink}"
																											target="_blank"><i class="fa fa-eye"
																											style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i></a> --%>
																										
																										<a href="#"
																											onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${contentFile.previewPath}')">
																										<i class="fa-solid fa-download"
																											style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																										</a> <i class="fa-solid fa-bookmark content"
																											id="${contentFile.id}"
																											onclick="removeBookmark(this)"
																											style="font-size: 20px; color: #fabc3d; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																									</div>
																								</div>

																							</div>
																						</c:if>
																						<c:if
																							test="${fn:endsWith(contentFile.previewPath, '.doc') || fn:endsWith(contentFile.previewPath, '.DOC') || fn:endsWith(contentFile.previewPath, '.DOCX') || fn:endsWith(contentFile.previewPath, '.docx')}">
																							<div class="col-sm-2">
																								<img height="50px" width="50px"
																									style="margin-top: 5px;"
																									src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/document.png" />
																							</div>
																							<div class="col-sm-10">
																								<div class="col-sm-12">
																									<div>
																										<span>Content Type : </span>
																										<c:out value="${contentFile.contentType}" />
																									</div>
																									<div>
																										<span>File Type : </span>
																										<c:out value="Word"></c:out>
																									</div>
																									<div style="text-align: right;">
																										<c:url value="acads/student/previewContent"
																											var="previewContentLink">
																											<c:param name="previewPath"
																												value="${contentFile.previewPath}" />
																											<c:param name="name"
																												value="${contentFile.name}" />
																										</c:url>
																										<a href="/${previewContentLink}"
																											target="_blank"><i class="fa-regular fa-eye"
																											style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i></a>
																										<a href="#"
																											onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${contentFile.previewPath}')">
																											<i class="fa-solid fa-download"
																											style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																										</a> <i class="fa-solid fa-bookmark content"
																											id="${contentFile.id}"
																											onclick="removeBookmark(this)"
																											style="font-size: 20px; color: #fabc3d; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																									</div>
																								</div>
																							</div>
																						</c:if>
																						<c:if
																							test="${fn:endsWith(contentFile.previewPath, '.xls') || fn:endsWith(contentFile.previewPath, '.XLS') || fn:endsWith(contentFile.previewPath, '.XLSX') || fn:endsWith(contentFile.previewPath, '.xlsx')}">
																							<div class="col-sm-2">
																								<img height="50px" width="50px"
																									style="margin-top: 5px;"
																									src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/xls.png" />
																							</div>
																							<div class="col-sm-10">
																								<div class="col-sm-12">
																									<div>
																										<span>Content Type : </span>
																										<c:out value="${contentFile.contentType}" />
																									</div>
																									<div>
																										<span>File Type : </span>
																										<c:out value="Excel"></c:out>
																									</div>
																									<div style="text-align: right;">
																										<c:url value="acads/student/previewContent"
																											var="previewContentLink">
																											<c:param name="previewPath"
																												value="${contentFile.previewPath}" />
																											<c:param name="name"
																												value="${contentFile.name}" />
																										</c:url>
																										<a href="/${previewContentLink}"
																											target="_blank"><i class="fa-regular fa-eye"
																											style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i></a>
																										<a href="#"
																											onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_BASE_PATH')" />${contentFile.previewPath}')">
																											<i class="fa-solid fa-download"
																											style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																										</a> <i class="fa-solid fa-bookmark content"
																											id="${contentFile.id}"
																											onclick="removeBookmark(this)"
																											style="font-size: 20px; color: #fabc3d; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																									</div>
																								</div>
																							</div>
																						</c:if>
																					</c:if>
																					<c:if test="${not empty contentFile.documentPath}">
																						<c:if
																							test="${fn:endsWith(contentFile.documentPath, '.ppt') || fn:endsWith(contentFile.documentPath, '.PPT') || fn:endsWith(contentFile.documentPath, '.PPTX') || fn:endsWith(contentFile.documentPath, '.pptx')}">
																							<div class="col-sm-2">
																								<img height="50px" width="50px"
																									style="margin-top: 5px;"
																									src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/powerpoint.png" />
																							</div>
																							<div class="col-sm-10">
																								<div>
																									<span>Content Type : </span>
																									<c:out value="${contentFile.contentType}" />
																								</div>
																								<div>
																									<span>File Type : </span>
																									<c:out value="PPT"></c:out>
																								</div>
																								<div style="text-align: right;">
																									<a href="/${contentFile.documentPath}"
																										target="_blank"> <i class="fa-solid fa-download"
																										style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																									</a> <i class="fa-solid fa-bookmark content"
																										id="${contentFile.id}"
																										onclick="removeBookmark(this)"
																										style="font-size: 20px; color: #fabc3d; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																								</div>
																							</div>
																						</c:if>
																						<c:if
																							test="${fn:endsWith(contentFile.documentPath, '.doc') || fn:endsWith(contentFile.documentPath, '.DOC') || fn:endsWith(contentFile.documentPath, '.DOCX') || fn:endsWith(contentFile.documentPath, '.docx')}">
																							<div class="col-sm-2">
																								<img height="50px" width="50px"
																									style="margin-top: 5px;"
																									src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/document.png" />
																							</div>
																							<div class="col-sm-10">
																								<div class="col-sm-12">
																									<div>
																										<span>Content Type : </span>
																										<c:out value="${contentFile.contentType}" />
																									</div>
																									<div>
																										<span>File Type : </span>
																										<c:out value="Word"></c:out>
																									</div>
																									<div style="text-align: right;">
																										<a href="/${contentFile.documentPath}"
																											target="_blank"> <i class="fa-solid fa-download"
																											style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																										</a> <i class="fa-solid fa-bookmark content"
																											id="${contentFile.id}"
																											onclick="removeBookmark(this)"
																											style="font-size: 20px; color: #fabc3d; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																									</div>
																								</div>
																							</div>
																						</c:if>
																						<c:if
																							test="${fn:endsWith(contentFile.documentPath, '.xls') || fn:endsWith(contentFile.documentPath, '.XLS') || fn:endsWith(contentFile.documentPath, '.XLSX') || fn:endsWith(contentFile.documentPath, '.xlsx')}">
																							<div class="col-sm-2">
																								<img height="50px" width="50px"
																									style="margin-top: 5px;"
																									src="https://studentzone-ngasce.nmims.edu/ltidemo/assets/images/preview/xls.png" />
																							</div>
																							<div class="col-sm-10">
																								<div class="col-sm-12">
																									<div>
																										<span>Content Type : </span>
																										<c:out value="${contentFile.contentType}" />
																									</div>
																									<div>
																										<span>File Type : </span>
																										<c:out value="Excel"></c:out>
																									</div>
																									<div style="text-align: right;">
																										<a href="/${contentFile.documentPath}"
																											target="_blank"> 
																											<i class="fa-solid fa-download"
																											style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																										</a> <i class="fa-solid fa-bookmark content"
																											id="${contentFile.id}"
																											onclick="removeBookmark(this)"
																											style="font-size: 20px; color: #fabc3d; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																									</div>
																								</div>
																							</div>
																						</c:if>
																					</c:if>

																					<c:if test="${not empty contentFile.webFileurl}">
																						<c:if
																							test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
																							<a href="${contentFile.webFileurl}"
																								target="_blank"><i class="fa-regular fa-eye"
																								style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i></a>
																							<i class="fa-solid fa-bookmark content"
																								id="${contentFile.id}"
																								onclick="removeBookmark(this)"
																								style="font-size: 20px; color: #fabc3d; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																						</c:if>

																						<c:if test="${contentFile.urlType == 'Download'}">
																							<a href="${contentFile.webFileurl}"
																								target="_blank"><i class="fa-solid fa-download"
																								style="font-size: 20px; color: gray; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i></a>
																							<i class="fa-solid fa-bookmark content"
																								id="${contentFile.id}"
																								onclick="removeBookmark(this)"
																								style="font-size: 20px; color: #fabc3d; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																						</c:if>
																					</c:if>
																				</div>

																			</div>
																		</div>
																	</div>
																</div>
															</c:forEach>
														</div>

													</c:when>
													<c:otherwise>
														<div class="no-data-wrapper">
															<h6 class="no-data nodata">
																No contents bookmarked
															</h6>
														</div>
													</c:otherwise>
												</c:choose>
											</div>
										</div>
									</div>
								</div>

								<div class="panel panel-default">
									<div class="panel-heading">
										<h4 class="panel-title">
											<a class="accordion-toggle collapsed" data-toggle="collapse"
												data-parent="#accordion" href="#collapse2"
												aria-expanded="false"> Video Contents </a>
										</h4>
									</div>
									<div id="collapse2" class="panel-collapse collapse"
										aria-expanded="false">
										<div class="panel-body">
											<div class="panel-body">
												<div class="panel-body" style="padding: 20px;">
													<div class=" data-content panel-body videoContent">
														<%
															try {
																if (videoContentList.size() > 0) {
																	for (VideoContentStudentPortalBean video : videoContentList) {
																		Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(video.getSessionDate());
																		Date date2 = new Date();
														%>
														<div class="row video">
															<div class="col-sm-4"></div>

															<div class="col-sm-4">

																<div class="card card-resource panel-body"
																	style="border: 1px solid lightgray; border-radius: .25rem;">
																	<div class="card-body">
																		<div class="row">
																			<div class="col-sm-9">
																				<div class="lead" style="text-align: left;">
																					<%=video.getSubject()%>
																				</div>
																			</div>
																			<div class="col-sm-3">
																				<div style="text-align: right; font-size: 14px;">
																					<%
																						if ((TimeUnit.DAYS.convert((long) (date2.getTime() - date1.getTime()),
																											TimeUnit.MILLISECONDS)) > 1
																											&& (TimeUnit.DAYS.convert((long) (date2.getTime() - date1.getTime()),
																													TimeUnit.MILLISECONDS)) < 8) {
																					%><%=TimeUnit.DAYS.convert((long) (date2.getTime() - date1.getTime()),
									TimeUnit.MILLISECONDS) + " days ago"%>
																					<%
																						} else if (TimeUnit.DAYS.convert((long) (date2.getTime() - date1.getTime()),
																											TimeUnit.MILLISECONDS) > 7) {
																					%>
																					<%=video.getCreatedDate().substring(0, 10)%>
																					<%
																						} else {
																					%>
																					<%=TimeUnit.DAYS.convert((long) (date2.getTime() - date1.getTime()),
									TimeUnit.MILLISECONDS) + " day ago"%>
																					<%
																						}
																					%>
																				</div>
																			</div>
																		</div>
																		<div class="row">
																			<div class="col-sm-10">
																				<p>
																					<%
																						if (!StringUtils.isBlank(video.getDescription())) {
																					%>
																					<span> <%=video.getDescription()%>
																					</span><br />
																					<%
																						}
																									if (!StringUtils.isBlank(video.getFacultyName())) {
																					%>
																					<span>Faculty Name : <%=video.getFacultyName()%></span><br />
																					<%
																						}
																					%>
																					<%
																						if (!StringUtils.isBlank(video.getTrack())) {
																					%>
																					<span>Track : <%=video.getTrack()%></span>
																					<%
																						}
																					%>
																				</p>
																				<p>
																					<%
																						if (!StringUtils.isBlank(video.getFileName())) {
																					%>
																					<span>File Name : <%=video.getFileName()%></span><br />
																					<%
																						}
																					%>
																					<%
																						if (!StringUtils.isBlank(video.getDuration())) {
																					%>
																					<span>Duration : <%=video.getDuration()%></span><br />
																					<%
																						}
																					%>
																				</p>
																			</div>
																			<div class="col-sm-2">
																				<div style="text-align: right;">
																					<i class="fa-solid fa-bookmark video"
																						id="<%=video.getId()%>"
																						onclick="removeBookmark(this)"
																						style="font-size: 20px; color: #fabc3d; border: 1px solid lightgray; padding: 0.4rem; border-radius: .25rem;"></i>
																				</div>
																			</div>
																		</div>
																		<div class="row">
																			<div class="" style="margin: 0.2rem;">
																				<div>
																					<iframe width="100%" height="250px"
																						style="border: none;"
																						src="<%=video.getVideoLink()%>"<%-- src="https://youtu.be/a3ICNMQW7Ok" --%>>
																					</iframe>
																				</div>
																			</div>
																		</div>
																	</div>
																</div>
															</div>
														</div>
														<%
															}
																} else {
														%>
														<div class="no-data-wrapper">

															<h6 class="no-data nodata">
																No video contents bookmarked
															</h6>
														</div>
														<%
															}
															} catch (Exception e) {}
														%>
													</div>

												</div>
											</div>
										</div>
									</div>
								</div>

							</div>
							</c:if>
							<c:if test="${isLoginAsLead eq true }">
								<h4 style="text-align: center">
									<i class="fa-regular fa-circle-exclamation" style="font-size: 19px" aria-hidden="true"></i>
									 Bookmarks is not available !!!
								</h4>
							</c:if>
						 </div>
					 </div>			
				  </div> 			 	       	               
			  </div>		 			 
		</div>	
	</div>
<div class="footer">
<jsp:include page="../common/footer.jsp"/>
</div>
	<script>
		function removeBookmark(obj){
			
			if(confirm("Are you sure you want to remove this content from bookmark?")){
				let data = {
						'id' : $(obj).attr("id"),
						'bookmarked' : "N"					
					};
				
					if($(obj).attr("class").split(" ")[2]=="content"){
						$(obj).parents(".content").remove();
						if($(".resourceContent").html().trim()==""){
							$(".resourceContent").html("<div class='no-data-wrapper'><h6 class='no-data nodata'>No contents bookmarked</h6></div>");
						}
					} else{
						$(obj).parents(".video").remove();
						if($(".videoContent").html().trim()==""){
							$(".videoContent").html("<div class='no-data-wrapper'><h6 class='no-data nodata'>No video contents bookmarked</h6></div>");
						}
					}
										
					
					$.ajax({
						type : "POST",
						contentType : "application/json",
						url : "setBookmark",
						data : JSON.stringify(data),
						success : function(data) {					
						},
						error : function(e) {
							console.log("error",e);
						}
					});
				
			}
		}
	
		/*  $('#mobileToggleNav').on('click', function() {
		 	$('.menu-closed').toggleClass('menu-opened');
		 });

		 $('#toggle-nav').on('click', function() {
		 	$('.sz-main-content').toggleClass('menu-closed');	
		 });
 */
	</script>
</body>
</html>
