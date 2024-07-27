<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.ContentStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<%
	List<ContentStudentPortalBean> course = (List<ContentStudentPortalBean>)session.getAttribute("contentLastCycleList");
	int courseSize = course.size();
	String selectedSubjectforTool = (String)request.getAttribute("subject");
	System.out.println("selectedSubjectforTool in LCC "+selectedSubjectforTool);
	StudentStudentPortalBean sbeanTool = (StudentStudentPortalBean)request.getSession().getAttribute("student_studentportal");
%>



<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

<style type="text/css">
.modal-open .modal {
	overflow-x: scroll;
	overflow-y: scroll;
}

#style_prevu_kit {
	position: relative;
	-webkit-transition: top 200ms ease-in;
	-webkit-transform: scale(1);
	-ms-transition: all 200ms ease-in;
	-ms-transform: scale(1);
	-moz-transition: all 200ms ease-in;
	-moz-transform: scale(1);
	transition: all 200ms ease-in;
	transform: scale(1);
}

#style_prevu_kit:hover {
	box-shadow: 0px 0px 50px #ffccbc;
	z-index: 2;
	-webkit-transition: all 200ms ease-in;
	-webkit-transform: scale(1);
	-ms-transition: all 200ms ease-in;
	-ms-transform: scale(1);
	-moz-transition: all 200ms ease-in;
	-moz-transform: scale(1);
	transition: all 200ms ease-in;
	transform: scale(1);
}

.panel-heading {
	padding: 5px 5px 5px 5px;
}

/* new code */
.panel-group .panel {
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
</style>

<div class="course-learning-resources-m-wrapper" id="learningResources">
	<div class="panel panel-default panel-courses-page">
		<div class="panel-heading" role="tab" id="">
			<h2>Applicable Content Access</h2>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>

			<ul class="topRightLinks list-inline">
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseThree" aria-expanded="true"></a></li>
			</ul>

			<div class="clearfix"></div>

		</div>
		<div class="clearfix"></div>
		<%if(courseSize == 0 ){ %>
		<div id="collapseThree"
			class="panel-collapse collapse academic-schedule courses-panel-collapse panel-content-wrapper"
			role="tabpanel">
			<%}else{ %>
			<div id="collapseThree"
				class="panel-collapse collapse in academic-schedule courses-panel-collapse panel-content-wrapper"
				role="tabpanel">
				<%} %>


				<div class="panel-body">

					<%if(courseSize == 0 ){ %>
					<div class="no-data-wrapper">
						<p class="no-data">
							<span class="icon-icon-pdf"></span>No Applicable Content
						</p>
					</div>
					<%}else{ %>
					<div class="data-content">
						<div class="col-md-12 p-closed">
							<i class="icon-icon-view-submissions"></i>
							<h4>
								<span><%=courseSize %></span> Content <span class="expand">Expand
									to view all Applicable Content</span>
							</h4>
						</div>
						<div class="col-md-12 ">
							<div class="table-responsive">
								<!-- Content from old contentList Start -->
								<table class="table table-striped "
									id="courseHomeLearningResources">
									<thead>
										<tr>
											<th>Sr No.</th>
											<th>Name</th>
											<th>Description</th>
											<th>Year-Month</th>
											<th>Action</th>
										</tr>
									</thead>

									<tbody>

										<% try{%>
										<c:forEach var="contentFile" items="${contentLastCycleList}"
											varStatus="status">
											<tr style="display: table-row;">
												<td><c:out value="${status.count}" /></td>
												<td><c:out value="${contentFile.name}" /></td>
												<td><c:out value="${contentFile.description}" /> <c:set
														var="string1" value="${contentFile.description}" /> <c:set
														var="string2" value="${fn:toUpperCase(string1)}" /> <c:set
														var="string3" value="${fn:toUpperCase(string1)}" /> <c:if
														test="${fn:substring(string2,0,4) eq 'TO V'}"> 
										   &nbsp;
										   <a
															href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/atrecply.msi">Windows</a> &nbsp;
										   <a
															href="https://akamaicdn.webex.com/client/WBXclient-32.11.0-388/mac/intel/webexplayer_intel.dmg">Mac
															OSX</a>
													</c:if></td>
												<td><c:out
														value="${contentFile.year}-${contentFile.month}" /></td>
												<td><c:if test="${not empty contentFile.previewPath}">
														<a href="#"
															onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${contentFile.previewPath}')">
															Download</a>
													</c:if> <c:if test="${not empty contentFile.documentPath}">
														<a href="/${contentFile.documentPath}" target="_blank"
															style="margin: 0px; padding: 0px; width: 21px; bottom: 50px; left: 90%;">
															Download </a>
													</c:if> <c:if
														test="${fn:endsWith(contentFile.previewPath, '.pdf') || fn:endsWith(contentFile.previewPath, '.PDF')
										|| fn:endsWith(contentFile.previewPath, '.Pdf')}">
														<c:url value="acads/previewContent"
															var="previewContentLink">
															<c:param name="previewPath"
																value="${contentFile.previewPath}" />
															<c:param name="name" value="${contentFile.name}" />
														</c:url>
														<%--      <a href="/acads/previewContent?previewPath=${contentFile.previewPath}&name=${contentFile.name}" target="_blank">View</a>
										 --%>
														<a href="/${previewContentLink}" target="_blank">View</a>

													</c:if> <c:if test="${not empty contentFile.webFileurl}">
														<c:if
															test="${contentFile.urlType == 'View' || contentFile.urlType == '' || empty contentFile.urlType	}">
															<a href="${contentFile.webFileurl}" target="_blank">View</a>
														</c:if>

														<c:if test="${contentFile.urlType == 'Download'}">
															<a href="${contentFile.webFileurl}" target="_blank">
																Download</a>
														</c:if>
													</c:if></td>

											</tr>
										</c:forEach>


										<%	}
								catch(Exception e){
								}
								%>




									</tbody>
								</table>


							</div>

						</div>
					</div>
					<%} %>
				</div>
				<!-- End -->

			</div>
		</div>
	</div>