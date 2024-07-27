<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PageStudentPortal"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.VideoContentStudentPortalBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Add Key Video Content" name="title" />
</jsp:include>

<body class="inside">
	<%
	ArrayList<VideoContentStudentPortalBean> videoSubTopicsList = (ArrayList<VideoContentStudentPortalBean>)request.getAttribute("videoSubTopicsList");
	int size = videoSubTopicsList !=null? videoSubTopicsList.size() : 0;
%>
	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
			<div class="row">
				<legend>&nbsp; Video Content</legend>
			</div>
			<div class="panel-body">
				<%@ include file="messages.jsp"%>

				<%try{ %>
				<div class="sz-content">
					<%-- Old Code By PS --%>
					<h2 class="red text-capitalize">
						<c:if test="${action == 'Edit VideoContent' }">
									        	Edit Video Content
									        </c:if>
					</h2>
					<c:if test="${action == 'Edit VideoContent' }">


						<div class="container-fluid "
							style="color: black; padding-top: 20px;">
							<div class="row">
								<div class=" col-md-5">
									<form:form action="postVideoContents" method="post"
										modelAttribute="videoContent">
										<fieldset>
											<form:hidden path="id" value="${videoContent.id }" />
											<div class="clearfix"></div>
											<div class="form-group">
												<label for="fileName"> Video Title </label>
												<form:input path="fileName" value="${videoContent.fileName}"
													required="required" placeholder="File Name"
													style="color:black; padding:6px 12px; width:100%;" />
											</div>
											<div class="form-group">
												<label for="keywords"> Video keywords </label>
												<form:input path="keywords" value="${videoContent.keywords}"
													required="required" placeholder="Add Keywords"
													style="color:black; padding:6px 12px; width:100%;" />
											</div>
											<div class="form-group">
												<label for="description"> Video Description </label>
												<form:textarea path="description" id="description"
													maxlength="1000" class="form-control"
													value='${videoContent.description}'
													placeholder="Add description for the Video Content..."
													cols="50" />
											</div>
											<div class="form-group">
												<label for="subject"> Video Subject </label>
												<form:input path="subject" value="${videoContent.subject}"
													required="required" placeholder="Add Subject"
													style="color:black; padding:6px 12px; width:100%;" />
											</div>
											<div class="form-group">
												<label for="videoLink"> Video Link </label>
												<form:input path="videoLink"
													value="${videoContent.videoLink}" required="required"
													placeholder="Add Video Link"
													style="color:black; padding:6px 12px; width:100%;" />
											</div>
											<div class="form-group">
												<label for="thumbnailUrl"> Video Thumbnai Url </label>
												<form:input path="thumbnailUrl"
													value="${videoContent.thumbnailUrl}" required="required"
													placeholder="Add Thumbnail Url"
													style="color:black; padding:6px 12px; width:100%;" />
											</div>
											<div class="form-group">
												<label for="mobileUrlHd"> HD Video Url for Mobile
													App </label>
												<form:input path="mobileUrlHd"
													value="${videoContent.mobileUrlHd}" required="required"
													placeholder="Add HD Video Url for Mobile App"
													style="color:black; padding:6px 12px; width:100%;" />
											</div>
											<div class="form-group">
												<label for="mobileUrlSd1">SD First Video Url for
													Mobile App </label>
												<form:input path="mobileUrlSd1"
													value="${videoContent.mobileUrlSd1}" required="required"
													placeholder="Add SD First Video Url for Mobile App"
													style="color:black; padding:6px 12px; width:100%;" />
											</div>
											<div class="form-group">
												<label for="mobileUrlSd2"> SD Second Video Url for
													Mobile App </label>
												<form:input path="mobileUrlSd2"
													value="${videoContent.mobileUrlSd2}" required="required"
													placeholder="Add SD Second Video Url for Mobile App"
													style="color:black; padding:6px 12px; width:100%;" />
											</div>
											<div class="form-group">
												<label for="sessionId"> Add Session ID of session
													this video is </label>
												<form:input path="sessionId"
													value="${videoContent.sessionId}" required="required"
													placeholder="Add Session ID of session this video is"
													style="color:black; padding:6px 12px; width:100%;" />
											</div>
											<div class="form-group">
												<label for="facultyId"> Add Faculty ID of session
													this video is </label>
												<form:input path="facultyId"
													value="${videoContent.facultyId}" required="required"
													placeholder="Add Faculty ID of session this video is"
													style="color:black; padding:6px 12px; width:100%;" />
											</div>
											<div class="form-group">
												<label for="year"> Select Year </label>
												<form:select id="year" path="year" type="text"
													placeholder="Year" class="form-control"
													itemValue="${videoContent.year}">
													<form:option value="">Select Year</form:option>
													<form:option value="2015">2015</form:option>
													<form:option value="2016">2016</form:option>
													<form:option value="2017">2017</form:option>
													<form:option value="2018">2018</form:option>
													<form:option value="2019">2019</form:option>
													<form:option value="2020">2020</form:option>
												</form:select>
											</div>

											<div class="form-group">
												<label for="month"> Select Month </label>
												<form:select id="month" path="month" type="text"
													placeholder="Month" class="form-control"
													itemValue="${videoContent.month}">
													<form:option value="">Select Month</form:option>
													<form:option value="Jan">Jan</form:option>
													<form:option value="Apr">Apr</form:option>
													<form:option value="Jun">Jun</form:option>
													<form:option value="Jul">Jul</form:option>

													<form:option value="Sep">Sep</form:option>
													<form:option value="Dec">Dec</form:option>
												</form:select>
											</div>

											<%-- Commented code for year and month as NOT NEEDED for now 
													<div class="form-group">
														<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${videoContent.year}">
															<form:option value="">Select Year</form:option>
															<form:option value="2015">2015</form:option>
															<form:option value="2016">2016</form:option>
															<form:option value="2017">2017</form:option>
															<form:option value="2018">2018</form:option>
															<form:option value="2019">2019</form:option>
															<form:option value="2020">2020</form:option> 
														</form:select>
													</div>
												
													<div class="form-group">
														<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${videoContent.month}">
															<form:option value="">Select Month</form:option>
															<form:option value="Apr">Apr</form:option>
															<form:option value="Jun">Jun</form:option>
															<form:option value="Sep">Sep</form:option>
															<form:option value="Dec">Dec</form:option>
														</form:select>
													</div>
													
													<div class="form-group"> 
														<label for="duration"> Video Duration </label>
														<form:input type="time" required="required" path="duration" value="${videoContent.duration}" id="endDateTime" style="color:black;"/>
													</div> --%>
											<div class=" form-group controls">
												<button id="submit" name="submit"
													class="btn btn-large btn-primary"
													formaction="postVideoContents">Save</button>
											</div>

										</fieldset>
									</form:form>
								</div>


								<!-- Code for video sub topics Start-->
								<div class=" col-md-7">

									<!-- Code for form to upload Excel for topics Start -->
									<div class="well">
										<h4>Upload Video Topics from Excel Sheet</h4>
										<%@ include file="uploadVideoContentFilesErrorMessages.jsp"%>

										<form:form modelAttribute="fileBean" method="post"
											enctype="multipart/form-data" action="uploadVideoTopicFiles">
											<form:hidden path="id" value="${videoContent.id }" />

											<div class="panel-body">
												<div class="col-md-6 column">
													<div class="form-group">
														<form:label for="fileData" path="fileData">Select file</form:label>
														<form:input path="fileData" type="file" />
													</div>

												</div>


												<div class="col-md-12 column">
													<b>Format of Upload: </b><br> Year | Month | Subject |
													FileName | KeyWords | Description | Default Video <br>
													<a
														href="${pageContext.request.contextPath}/resources_2015/templates/VideoTopicsStructure.xlsx"
														target="_blank">Download a Sample Template</a>

												</div>


											</div>
											<br>
											<div class="row">
												<div class="col-md-6 column">
													<button id="submit" name="submit"
														class="btn btn-large btn-primary"
														formaction="uploadVideoTopicFiles">Upload</button>
												</div>


											</div>
										</form:form>
									</div>
									<!-- Code for form to upload Excel for topics End -->
									<div class="panel-group" id="accordion">
										<!-- Code to add video sub topics Start-->
										<div class="panel panel-default">
											<div class="panel-heading">
												<h4 class="panel-title">
													<a data-toggle="collapse" data-parent="#accordion"
														href="#addSubtopic"> Add Topic </a>
												</h4>
											</div>
											<div id="addSubtopic" class="panel-collapse collapse">
												<div class="panel-body">
													<form:form action="postVideoSubTopic" method="post"
														modelAttribute="videoSubTopic">
														<fieldset>
															<form:hidden path="id" value="${videoSubTopic.id }" />
															<form:hidden path="parentVideoId"
																value="${videoContent.id }" />
															<div class="clearfix"></div>
															<div class="form-group">
																<form:input path="fileName"
																	value="${videoSubTopic.fileName}" required="required"
																	placeholder="Topic Title"
																	style="color:black; padding:6px 12px; width:100%;" />
															</div>
															<div class="form-group">
																<label for="startTime">Topic Start Time (
																	HH:MM:SS )</label>
																<form:input required="required" placeholder="HH:MM:SS"
																	path="startTime" value="${videoSubTopic.startTime}"
																	id="endDateTime" style="color:black;" />
															</div>
															<div class="form-group">
																<label for="endTime">Topic End Time ( HH:MM:SS )</label>
																<form:input placeholder="HH:MM:SS" required="required"
																	path="endTime" value="${videoSubTopic.endTime}"
																	id="endDateTime" style="color:black;" />
															</div>
															<div class="form-group">
																<label for="duration">Topic Duration( HH:MM:SS )</label>
																<form:input placeholder="HH:MM:SS" required="required"
																	path="duration" value="${videoSubTopic.duration}"
																	id="endDateTime" style="color:black;" />
															</div>
															<div class="form-group">
																<label for="keywords"> Video keywords </label>
																<form:input path="keywords"
																	value="${videoSubTopic.keywords}" required="required"
																	placeholder="Add Keywords"
																	style="color:black; padding:6px 12px; width:100%;" />
															</div>
															<div class="form-group">
																<label for="description"> Video Description </label>
																<form:textarea path="description" id="description"
																	maxlength="1000" class="form-control"
																	value='${videoSubTopic.description}'
																	placeholder="Add description for the Video Content..."
																	cols="50" />
															</div>
															<div class="form-group">
																<label for="subject"> Video Subject </label>
																<form:input path="subject"
																	value="${videoSubTopic.subject}" required="required"
																	placeholder="Add Subject"
																	style="color:black; padding:6px 12px; width:100%;" />
															</div>
															<div class="form-group">
																<label for="videoLink"> Video Link </label>
																<form:input path="videoLink"
																	value="${videoSubTopic.videoLink}" required="required"
																	placeholder="Add Video Link"
																	style="color:black; padding:6px 12px; width:100%;" />
															</div>
															<div class="form-group">
																<label for="thumbnailUrl"> Video Thumbnai Url </label>
																<form:input path="thumbnailUrl"
																	value="${videoSubTopic.thumbnailUrl}"
																	required="required" placeholder="Add Thumbnail Url"
																	style="color:black; padding:6px 12px; width:100%;" />
															</div>
															<div class=" form-group controls">
																<button id="submit" name="submit"
																	class="btn btn-large btn-primary"
																	formaction="postVideoSubTopic">Save</button>
															</div>

														</fieldset>
													</form:form>
												</div>
											</div>
										</div>
										<!-- Code to add video sub topics End-->

										<!-- Code to edit video sub topics Start-->
										<div class="well">
											<%
													if(size>0){
											%>
											<h4>Edit Topics</h4>
											<% 
														for(VideoContentStudentPortalBean vcb : videoSubTopicsList){
											%>

											<div class="panel panel-default">
												<div class="panel-heading"
													style="padding: 5px 10px !important;">
													<div class="row">
														<div class="col-sm-11">
															<h4 class="panel-title"
																sytle="padding: 5px 10px !important;">
																<a data-toggle="collapse" data-parent="#accordion"
																	href="#<%= vcb.getId() %>">> <%= vcb.getFileName() %>
																</a>
															</h4>
														</div>
														<div class="col-sm-1" style="">
															<a
																href="/studentportal/deleteVideoSubTopic?id=<%= vcb.getId() %>"
																style="float: right; padding: 5px 10px !important;">
																<i class="fa fa-trash-o"
																style="font-size: 20px !important; color: black;"
																aria-hidden="true"></i>
															</a>
														</div>
													</div>
												</div>

												<div id="<%= vcb.getId() %>" class="panel-collapse collapse">

													<div class="panel-body">
														<form:form action="postVideoSubTopic" method="post"
															modelAttribute="videoSubTopic">
															<fieldset>
																<form:hidden path="id" value="<%= vcb.getId() %>" />
																<form:hidden path="parentVideoId"
																	value="${videoContent.id }" />

																<div class="clearfix"></div>
																<div class="form-group">
																	<form:input path="fileName"
																		value="<%= vcb.getFileName() %>" required="required"
																		placeholder="Topic Title"
																		style="color:black; padding:6px 12px; width:100%;" />
																</div>
																<div class="form-group">
																	<label for="startTime">Topic Start Time (
																		HH:MM:SS )</label>
																	<form:input required="required" placeholder="HH:MM:SS"
																		path="startTime" value="<%= vcb.getStartTime() %>"
																		id="endDateTime" style="color:black;" />
																</div>
																<div class="form-group">
																	<label for="endTime">Topic End Time ( HH:MM:SS
																		)</label>
																	<form:input placeholder="HH:MM:SS" required="required"
																		path="endTime" value="<%= vcb.getEndTime() %>"
																		id="endDateTime" style="color:black;" />
																</div>
																<div class="form-group">
																	<label for="duration">Duration ( HH:MM:SS )</label>
																	<form:input placeholder="HH:MM:SS" required="required"
																		path="duration" value="<%= vcb.getDuration() %>"
																		id="endDateTime" style="color:black;" />
																</div>
																<div class="form-group">
																	<label for="keywords"> Video keywords </label>
																	<form:input path="keywords"
																		value="<%= vcb.getKeywords() %>" required="required"
																		placeholder="Add Keywords"
																		style="color:black; padding:6px 12px; width:100%;" />
																</div>
																<div class="form-group">
																	<label for="description"> Video Description </label>
																	<form:input path="description" id="description"
																		class="form-control"
																		value='<%= vcb.getDescription() %>'
																		placeholder="Add description for the Video Content..." />
																</div>
																<div class="form-group">
																	<label for="subject"> Video Subject </label>
																	<form:input path="subject"
																		value="<%= vcb.getSubject() %>" required="required"
																		placeholder="Add Subject"
																		style="color:black; padding:6px 12px; width:100%;" />
																</div>
																<div class="form-group">
																	<label for="videoLink"> Video Link </label>
																	<form:input path="videoLink"
																		value="<%= vcb.getVideoLink() %>" required="required"
																		placeholder="Add Video Link"
																		style="color:black; padding:6px 12px; width:100%;" />
																</div>
																<div class="form-group">
																	<label for="thumbnailUrl"> Video Thumbnai Url </label>
																	<form:input path="thumbnailUrl"
																		value="<%= vcb.getThumbnailUrl() %>"
																		required="required" placeholder="Add Thumbnail Url"
																		style="color:black; padding:6px 12px; width:100%;" />
																</div>
																<div class=" form-group controls">
																	<button id="submit" name="submit"
																		class="btn btn-large btn-primary"
																		formaction="postVideoSubTopic">Save</button>
																</div>

															</fieldset>
														</form:form>
													</div>
												</div>
											</div>
											<%			
														}
													}
												  %>
										</div>
										<!-- Code to edit video sub topics End-->
									</div>
								</div>
								<!-- Code for video sub topics End-->
							</div>
						</div>
					</c:if>

				</div>
			</div>
		</div>

		<%}catch(Exception e){} %>
	</section>

	<jsp:include page="footer.jsp" />

</body>
</html>
