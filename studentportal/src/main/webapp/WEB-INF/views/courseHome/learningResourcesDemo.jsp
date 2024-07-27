<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.nmims.beans.ContentStudentPortalBean"%>
<%@page import="com.nmims.beans.ModuleContentStudentPortalBean"%>
<%@page import="com.nmims.beans.StudentStudentPortalBean"%>
<%@page import="com.nmims.beans.VideoContentStudentPortalBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<style type="text/css">
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
<%

ArrayList<ModuleContentStudentPortalBean> moduleDocumentList =  (ArrayList<ModuleContentStudentPortalBean>)session.getAttribute("moduleDocumentList");
int noOfLearningResources = moduleDocumentList != null ? moduleDocumentList.size() : 0;

ArrayList<ModuleContentStudentPortalBean> downloadCenterLink =  (ArrayList<ModuleContentStudentPortalBean>)session.getAttribute("downloadCenterLink");

ArrayList<ModuleContentStudentPortalBean> downloadCenter =  (ArrayList<ModuleContentStudentPortalBean>)session.getAttribute("downloadCenter");

ArrayList<ContentStudentPortalBean> lastCycleContentList =  (ArrayList<ContentStudentPortalBean>)session.getAttribute("lastCycleContentList");
int noOfLastCycleRecordings = lastCycleContentList != null ? lastCycleContentList.size() : 0;

StudentStudentPortalBean studentSemCheck = (StudentStudentPortalBean)session.getAttribute("semCheck");

ArrayList<VideoContentStudentPortalBean> videoContentList =  (ArrayList<VideoContentStudentPortalBean>)session.getAttribute("videoContentList");
int noOfVideoContents = videoContentList != null ? videoContentList.size() : 0;

ArrayList<ContentStudentPortalBean> contentList =  (ArrayList<ContentStudentPortalBean>)session.getAttribute("contentList");
System.out.println("IN JSP got contentList size : "+contentList !=null ? contentList.size() : 0);

%>



<div class="course-learning-resources-m-wrapper" id="learningResources">
	<div class="panel panel-default panel-courses-page">
		<div class="panel-heading" role="tab" id="">
			<h2>Learning Resources</h2>
			<!---TOP TABS-->
			<div class="custom-clearfix clearfix"></div>
			<ul class="topRightLinks list-inline">
				<li>
					<h3 class=" green">
						<span><%=noOfLearningResources %></span> Modules
					</h3>
				</li>

				<%-- <%if(noOfLastCycleRecordings > 0){ %> 
				<li><a href="#" data-toggle="modal" data-target="#lastCycleRecordings"><h3 class="green">Last Cycle Recordings</h3></a></li>
				<%} %> --%>
				<li><a class="panel-toggler collapsed" role="button"
					data-toggle="collapse" href="#collapseThree" aria-expanded="true"></a></li>
				<div class="clearfix"></div>
			</ul>
			<div class="clearfix"></div>
		</div>
		<div class="clearfix"></div>
		<%if(noOfLearningResources == 0){ %>
		<div id="collapseThree"
			class="panel-collapse collapse academic-schedule courses-panel-collapse panel-content-wrapper"
			role="tabpanel">
			<%}else{ %>
			<div id="collapseThree"
				class="panel-collapse collapse in academic-schedule courses-panel-collapse panel-content-wrapper"
				role="tabpanel">
				<%} %>

				<div class="panel-body">
					<%if(noOfLearningResources == 0){ %>
					<div class="no-data-wrapper">
						<p class="no-data">
							<span class="icon-icon-pdf"></span>No new Learning Resources
						</p>
					</div>
					<%}else{ %>

					<div class="data-content">
						<div class="col-md-12 p-closed">
							<i class="icon-icon-view-submissions"></i>
							<h4>
								<span><%=noOfLearningResources %></span> Modules <span
									class="expand">Expand to view all Sessions</span>
							</h4>
						</div>

						<div class="col-sm-2" style="margin-top: 10px;"
							data-toggle="modal" data-target="#myModal">

							<div id="style_prevu_kit" class="tile-progress tile-primary"
								style="cursor: pointer !important;">
								<div class="tile-header"
									style="font-size: 13px; margin-bottom: -10px; min-height: 80px !important; border-radius: 2px; padding: 5px 5px 5px 5px;">
									<br>
									<p
										style="align-self: center; color: white; padding-left: 40px;">DOWNLOAD
										CENTER</p>
								</div>
								<div class="tile-footer" style="background: #0288D1;">
									<i class="fa fa-download large"
										style="font-size: 26px; color: white; padding-left: 40%;"></i>
								</div>
							</div>
						</div>
						<c:forEach var="moduleDocumentList" items="${moduleDocumentList}"
							varStatus="status">
							<a
								href="/studentportal/moduleLibraryList?moduleId=${moduleDocumentList.id}&subject=${subject}&type=doc">

								<div class="col-sm-2" style="margin-top: 10px;">
									<div id="style_prevu_kit" class="tile-progress tile-primary"
										style="cursor: pointer !important;">
										<div class="tile-header" id="tileHeader"
											style="font-size: 13px; margin-bottom: -10px; min-height: 80px !important; border-radius: 2px; padding: 5px 5px 5px 5px;">
											${moduleDocumentList.moduleName} <br>
											<%-- ${chapter.title}   --%>
										</div>
										<div class="tile-footer">

											<div class="statistics">

												<div class="col-sm-8" style="padding: 0px">
													<p>Progress: ${moduleDocumentList.percentageCombined}%
													</p>
												</div>
												<div class="col-sm-12 progress"
													style="height: 5px; padding: 0px;">
													<div class="progress-bar progress-bar-striped active"
														role="progressbar" aria-valuenow="40" aria-valuemin="0"
														aria-valuemax="100"
														style="width:${moduleDocumentList.percentageCombined}% ;
   				 background-color:#3c763d;">
														40%</div>
												</div>
											</div>
										</div>
									</div>
								</div>

							</a>
						</c:forEach>


						<!-- Modal -->
						<div class="modal fade" id="myModal" role="dialog">
							<div class="modal-dialog">

								<!-- Modal content-->
								<div class="modal-content"
									style="height: 100%; width: 150%; margin-left: -10%;">
									<div class="modal-header">
										<button type="button" class="close" data-dismiss="modal">&times;</button>
										<h4 class="modal-title">Download Center</h4>
									</div>
									<div class="modal-body">
										<div class="table-responsive">
											<!-- Content from old contentList Start -->
											<table class="table table-striped "
												id="courseHomeLearningResources">
												<thead>
													<tr>
														<th>Sr No.</th>
														<th>Name</th>
														<th>Description</th>
														<th>Action</th>
													</tr>
												</thead>

												<tbody>


													<c:forEach var="contentFile" items="${contentList}"
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

															<td><c:if
																	test="${not empty contentFile.previewPath}">
																	<a href="#"
																		onClick="window.open('<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_PREVIEW_PATH')" />${contentFile.previewPath}')">
																		Download</a>
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

												</tbody>
											</table>


										</div>
									</div>
									<div class="modal-footer" style="margin-right: 50%;">
										<button type="button" class="btn btn-default"
											data-dismiss="modal" style="width: 20%;">Close</button>
									</div>
								</div>

							</div>
						</div>
					</div>
					<%} %>

				</div>
			</div>
		</div>
	</div>

	<%-- 
<%if(noOfLastCycleRecordings > 0){ %>
<div class="modal fade assignments" id="lastCycleRecordings" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Last Cycle Recordings</h4>
      </div>
      <div class="modal-body">
      	
      					<%
      					if(studentSemCheck != null){
      						System.out.println("Got sem in LR.jsp"+studentSemCheck.getSem());
      						if(studentSemCheck.getSem().equals("1") || studentSemCheck.getSem().equals("2")){
      					%>
      						<h4>
      							<b>
      								To view the last cycle recordings please refer to Session videos for now, when the same is updated under &quot;Last cycle recordings&quot; students will be notified via email
      							</b>
      						</h4>
      					<% 
      						}
      					
      					%>
      					
      					<%
      					if(studentSemCheck.getSem().equals("3") || studentSemCheck.getSem().equals("4")){
      					%>
      						<h4>
      							<b>
									Recordings will be uploaded shortly, you will be notified via email
      							</b>
      						</h4>
      					<%	}
      					}
      					%>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Done</button>
      </div>
    </div>
  </div>
</div>

<%}%> --%>

	<script>
function toggleIcon(e) {
    $(e.target)
        .prev('.panel-heading')
        .find(".more-less")
        .toggleClass('glyphicon glyphicon-menu-down glyphicon glyphicon-menu-up');
}
$('.panel-group').on('hidden.bs.collapse', toggleIcon);
$('.panel-group').on('shown.bs.collapse', toggleIcon);

</script>
	<script>
    $(document).ready(function(){
         var tileHeaderColors = ['#e53935 red darken-1','#3949ab indigo darken-1','#00897b teal darken-1','#fb8c00 orange darken-1',
        	 '#757575 grey darken-1','#5e35b1 deep-purple darken-1 ','#43a047 green darken-1','#ff8f00 amber darken-3'];
         var j=0;
         $('div#tileHeader').each(function(){
        	$(this).css('background-color',tileHeaderColors[j]); 
        	j=(j+1) % tileHeaderColors.length; 
         })
    });
    function goToDetails(id){
    	console.log("test");
     	var colorId =  $('div#tileHeader').css('backgroundColor');
     	console.log('color Id '+colorId);
    	window.location.replace("/acads/moduleLibraryList?moduleId="+id+"&colorId="+colorId);
    	
    }
</script>