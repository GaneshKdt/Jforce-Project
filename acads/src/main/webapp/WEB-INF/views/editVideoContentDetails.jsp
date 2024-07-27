<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->

<%@page import="com.nmims.beans.PersonAcads"%>
<%@page import="com.nmims.beans.Page"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.VideoContentBean"%>
<html class="no-js">
<!--<![endif]-->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="jscss.jsp">
	<jsp:param value="Add Key Video Content" name="title" />
</jsp:include>

<body class="inside">
<%
	ArrayList<VideoContentBean> videoSubTopicsList = (ArrayList<VideoContentBean>)request.getAttribute("videoSubTopicsList");
	int size = videoSubTopicsList !=null? videoSubTopicsList.size() : 0;
%>
	<%@ include file="header.jsp"%>

	<section class="content-container">

		<div class="container-fluid customTheme">
		<div class="row"><legend>&nbsp; Video Content</legend></div>
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
									        
											
											<div class="container-fluid " style="color:black; padding-top:20px;">
											<div class="row">
											<div class=" col-md-5">
											<form:form  action="postVideoContents" method="post" modelAttribute="videoContent">
												<fieldset> 
													<form:hidden path="id" value="${videoContent.id }"/> 
													<div class="clearfix"></div>
				<form:input type="hidden" path="consumerProgramStructureId" value="${videoContent.consumerProgramStructureId}"/>
				<form:input type="hidden" path="allowedToUpdate" value="${videoContent.allowedToUpdate}"/>
				
	
				<c:choose>
		            <c:when  test="${videoContent.allowedToUpdate eq 'true'}">
				 <!-- code for configuration start -->
					<div class="row">
						
							
										
										<div class="form-group">
											<c:choose>
											 <c:when test="${videoContent.editSingleContentFromCommonSetup eq 'true'}">
											<form:label path="consumerTypeId" for="consumerTypeId">Consumer Type</form:label>
											<form:select id="consumerTypeId" path="consumerTypeId" 
														 type="text"	placeholder="consumerType" 
														 class="form-control selectConsumerType" required="required" 
													     
													     readonly="readonly"> 

									            	<c:forEach var="consumerType" items="${consumerType}">
									                <c:if test='${videoContent.consumerTypeId eq consumerType.id}'>
														 <form:option value="${consumerType.id}" selected="true">
									                		<c:out value="${consumerType.name}"/>
									                	 </form:option>
									               
													</c:if>
									            	</c:forEach>
											</form:select>
													
													</c:when>
													<c:otherwise>
														
													</c:otherwise>
													</c:choose>
										
										</div>	
						
						
							
							<div class="form-group">
											<c:choose>
											 <c:when test="${videoContent.editSingleContentFromCommonSetup eq 'true'}">
											
											<form:label path="programStructureId" for="programStructureId">Program Structure </form:label>
											<form:select id="programStructureId" path="programStructureId" type="text"
														
													     
													     placeholder="Program Structure" class="form-control selectProgramStructure" required="required" 
													 itemValue="${videoContent.programStructureId}"  readonly="readonly"> 
													<form:option value="">Select Program Structure</form:option>
													
													<c:choose>
											            <c:when test="${fn:length(fn:split(videoContent.programStructureId, ',')) == 1}">
													            	
											            	<c:forEach var="entry" items="${programStructureIdNameMap}">
											                <c:if test='${videoContent.programStructureId eq entry.key}'>
																 <form:option value="${entry.key}" selected="true">
											                		<c:out value="${entry.value}"/>
											                	 </form:option>
															</c:if>
											            	</c:forEach>
											            	
											            </c:when>
											            
											            <c:when test="${fn:length(fn:split(videoContent.programStructureId, ',')) > 1}">
													            	
											            		 <form:option value="${videoContent.programStructureId}" selected="true">
											                		<c:out value="All"/>
											                	 </form:option>
															
											            </c:when>
											            <c:otherwise>
											            	<form:option value="">Re-Select Consumer Type</form:option>
														</c:otherwise>
											        </c:choose>
													
													
											</form:select>
											
											</c:when>
											<c:otherwise>
											
											</c:otherwise>
											</c:choose>		
										
										</div>
							
							<div class="form-group">
											<c:choose>
											 <c:when test="${videoContent.editSingleContentFromCommonSetup eq 'true'}">
											
											<form:label path="programId" for="programId">Program </form:label>
											<form:select id="programId" path="programId" type="text"
													      placeholder="Program " class="form-control selectProgram" required="required" 
													 itemValue="${videoContent.programId}"  readonly="readonly"> 
													
													<c:choose>
											            <c:when test="${fn:length(fn:split(videoContent.programId, ',')) == 1}">
													            	
											            	<c:forEach var="entry" items="${programIdNameMap}">
											                <c:if test='${videoContent.programId eq entry.key}'>
																 <form:option value="${entry.key}" selected="true">
											                		<c:out value="${entry.value}"/>
											                	 </form:option>
															</c:if>
											            	</c:forEach>
											            	
											            </c:when>
											            
											            <c:when test="${fn:length(fn:split(videoContent.programId, ',')) > 1}">
													            	
											            		 <form:option value="${videoContent.programId}" selected="true">
											                		All
											                	 </form:option>
															
											            </c:when>
											            <c:otherwise>
											            	<form:option value="">Re-Select Consumer Type</form:option>
														</c:otherwise>
											        </c:choose>
													
											</form:select>
											
											
											</c:when>
											<c:otherwise>
											
											
											</c:otherwise>
											</c:choose>		
										
											
										</div>

						
						<!-- /////////////////////////////////////////////////////////////////// -->
						
					
					</div>
										
										<!-- code for configuration end -->
					</c:when>
		            <c:otherwise>
		            	<form:input type="hidden" path="consumerTypeId" value="${videoContent.consumerTypeId}"/>
		            </c:otherwise>
		        </c:choose>
				
													<div class="form-group">
														<label for="fileName"> Video Title </label>
														<form:input path="fileName" value="${videoContent.fileName}" required="required"  placeholder="File Name" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="keywords"> Video keywords </label>
														<form:input path="keywords" value="${videoContent.keywords}" required="required"  placeholder="Add Keywords" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
															<label for="description"> Video Description </label>
														<form:textarea path="description" id="description" maxlength="1000" class="form-control" value='${videoContent.description}' placeholder="Add description for the Video Content..." cols="50"/>
													</div>
													<div class="form-group">
														<label for="subject"> Video Subject </label>
														<form:input path="subject" value="${videoContent.subject}" required="required"  placeholder="Add Subject" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="videoLink"> Video Link </label>
														<form:input path="videoLink" value="${videoContent.videoLink}" required="required"  placeholder="Add Video Link" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="thumbnailUrl"> Video Thumbnai Url </label>
														<form:input path="thumbnailUrl" value="${videoContent.thumbnailUrl}" required="required"  placeholder="Add Thumbnail Url" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="mobileUrlHd"> HD Video Url for Mobile App </label>
														<form:input path="mobileUrlHd" value="${videoContent.mobileUrlHd}" required="required"  placeholder="Add HD Video Url for Mobile App" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="mobileUrlSd1">SD First Video Url for Mobile App </label>
														<form:input path="mobileUrlSd1" value="${videoContent.mobileUrlSd1}" required="required"  placeholder="Add SD First Video Url for Mobile App" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="mobileUrlSd2"> SD Second Video Url for Mobile App </label>
														<form:input path="mobileUrlSd2" value="${videoContent.mobileUrlSd2}" required="required"  placeholder="Add SD Second Video Url for Mobile App" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="sessionId"> Add Session ID of session this video is </label>
														<form:input path="sessionId" value="${videoContent.sessionId}" required="required"  placeholder="Add Session ID of session this video is" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="facultyId"> Add Faculty ID of session this video is </label>
														<form:input path="facultyId" value="${videoContent.facultyId}" required="required"  placeholder="Add Faculty ID of session this video is" style="color:black; padding:6px 12px; width:100%;"/>
													</div> 
													<div class="form-group">
														<label for="year"> Select Year </label>
														<form:select id="year" path="year" type="text"	placeholder="Year" class="form-control"   itemValue="${videoContent.year}">
															<form:option value="">Select Year</form:option>
															<form:option value="2015">2015</form:option>
															<form:option value="2016">2016</form:option>
															<form:option value="2017">2017</form:option>
															<form:option value="2018">2018</form:option>
															<form:option value="2019">2019</form:option>
															<form:option value="2020">2020</form:option>
															<form:option value="2021">2021</form:option> 
														</form:select>
													</div>
												
													<div class="form-group">
														<label for="month"> Select Month </label>
														<form:select id="month" path="month" type="text" placeholder="Month" class="form-control"  itemValue="${videoContent.month}">
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
															<form:option value="2021">2021</form:option>
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
													
													<c:choose>
											 <c:when test="${videoContent.editSingleContentFromCommonSetup eq 'true'}">
											
												<div class=" form-group controls">
													<button id="submit" name="submit" class="btn btn-large btn-primary" 
														formaction="updateSingleVideoContentFromCommonSetup" 
														onClick="return confirm('Are you sure? Saving these changes will delete old mappings.')"
														>Save</button>
												</div>
											</c:when>
											
											<c:otherwise>
												<div class=" form-group controls">
													<button id="submit" name="submit" class="btn btn-large btn-primary" 
														formaction="postVideoContents" >Save</button>
												</div>
											</c:otherwise>
											
											</c:choose>
													
													
									
												</fieldset>
											</form:form>
											</div>

																						
											<!-- Code for video sub topics Start-->
											<div class=" col-md-7">
											
											<!-- Code for form to upload Excel for topics Start -->
											<div class="well">
												<h4>Upload Video Topics from Excel Sheet</h4>
												<%@ include file="uploadVideoContentFilesErrorMessages.jsp"%>
											
												<form:form modelAttribute="fileBean" method="post" 	enctype="multipart/form-data" action="uploadVideoTopicFiles">
												<form:hidden path="id" value="${videoContent.id }"/> 
													
												<div class="panel-body">
												<div class="col-md-6 column">
													<div class="form-group">
														<form:label for="fileData" path="fileData">Select file</form:label>
														<form:input path="fileData" type="file" />
													</div>
													
												</div>
										
										
										<div class="col-md-12 column">
										<b>Format of Upload: </b><br>
										Year | Month | Subject | FileName | KeyWords | Description | Default Video  <br>
										<a href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />resources_2015/templates/VideoTopicsStructure.xlsx" target="_blank">Download a Sample Template</a>
										
										</div>
										
										
										</div>
										<br>
										<div class="row">
											<div class="col-md-6 column">
												<button id="submit" name="submit" class="btn btn-large btn-primary"
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
												        <a data-toggle="collapse" data-parent="#accordion" href="#addSubtopic">
												        	Add Topic
												        </a>
												      </h4>
												    </div>
												    <div id="addSubtopic" class="panel-collapse collapse">
												      <div class="panel-body">
												      	<form:form  action="postVideoSubTopic" method="post" modelAttribute="videoSubTopic">
															<fieldset> 
																<form:hidden path="id" value="${videoSubTopic.id }"/> 
																<form:hidden path="parentVideoId" value="${videoContent.id }"/> 
																<div class="clearfix"></div>
																<div class="form-group">
																	<form:input path="fileName" value="${videoSubTopic.fileName}" required="required"  placeholder="Topic Title" style="color:black; padding:6px 12px; width:100%;"/>
																</div>  
																<div class="form-group"> 
																	<label for="startTime">Topic Start Time ( HH:MM:SS )</label>
																	<form:input required="required" placeholder="HH:MM:SS" path="startTime" value="${videoSubTopic.startTime}" id="endDateTime" style="color:black;"/>
																</div>  
																<div class="form-group"> 
																	<label for="endTime">Topic End Time ( HH:MM:SS )</label>
																	<form:input placeholder="HH:MM:SS" required="required" path="endTime" value="${videoSubTopic.endTime}" id="endDateTime" style="color:black;"/>
																</div>  
																<div class="form-group"> 
																	<label for="duration">Topic Duration( HH:MM:SS )</label>
																	<form:input placeholder="HH:MM:SS" required="required" path="duration" value="${videoSubTopic.duration}" id="endDateTime" style="color:black;"/>
																</div>
													<div class="form-group">
														<label for="keywords"> Video keywords </label>
														<form:input path="keywords" value="${videoSubTopic.keywords}" required="required"  placeholder="Add Keywords" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
															<label for="description"> Video Description </label>
														<form:textarea path="description" id="description" maxlength="1000" class="form-control" value='${videoSubTopic.description}' placeholder="Add description for the Video Content..." cols="50"/>
													</div>
																<div class=" form-group controls">
																	<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="postVideoSubTopic" >Save</button>
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
														<h4> Edit Topics </h4>
											<% 
														for(VideoContentBean vcb : videoSubTopicsList){
											%>
												
												  <div class="panel panel-default">
												    <div class="panel-heading" style="padding: 5px 10px !important;">
													 <div class="row">
													 	<div class="col-sm-11">
												      <h4 class="panel-title" sytle="padding: 5px 10px !important;">
												        <a data-toggle="collapse" data-parent="#accordion" href="#<%= vcb.getId() %>" >>
												        	<%= vcb.getFileName() %>
														</a>
												      </h4>
											 		   </div>
													 	<div class="col-sm-1" style=""> 
												       <a href="/acads/admin/deleteVideoSubTopic?id=<%= vcb.getId() %>" style="float:right; padding: 5px 10px !important;" >
												      <i class="fa fa-trash-o" style="font-size:20px !important; color:black;" aria-hidden="true"></i>
												      </a> 
												      </div>
												      </div>
												    </div>
												   
											  <div id="<%= vcb.getId() %>" class="panel-collapse collapse">
												   
											   <div class="panel-body">
												      	<form:form  action="postVideoSubTopic" method="post" modelAttribute="videoSubTopic">
															<fieldset> 
															 	<form:hidden path="id" value="<%= vcb.getId() %>"/>
											 					<form:hidden path="parentVideoId" value="${videoContent.id }"/> 
															 
												<div class="clearfix"></div>
																<div class="form-group">
																	<form:input path="fileName" value="<%= vcb.getFileName() %>" required="required"  placeholder="Topic Title" style="color:black; padding:6px 12px; width:100%;"/>
																</div>  
																<div class="form-group"> 
																	<label for="startTime">Topic Start Time ( HH:MM:SS )</label>
																	<form:input required="required" placeholder="HH:MM:SS" path="startTime" value="<%= vcb.getStartTime() %>" id="endDateTime" style="color:black;"/>
																</div>  
																<div class="form-group"> 
																	<label for="endTime">Topic End Time ( HH:MM:SS )</label>
																	<form:input placeholder="HH:MM:SS" required="required" path="endTime" value="<%= vcb.getEndTime() %>" id="endDateTime" style="color:black;"/>
																</div>  
																<div class="form-group"> 
																	<label for="duration">Duration ( HH:MM:SS )</label>
																	<form:input placeholder="HH:MM:SS" required="required" path="duration" value="<%= vcb.getDuration() %>" id="endDateTime" style="color:black;"/>
																</div>
													<div class="form-group">
														<label for="keywords"> Video keywords </label>
														<form:input path="keywords" value="<%= vcb.getKeywords() %>" required="required"  placeholder="Add Keywords" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
															<label for="description"> Video Description </label>
														<form:input path="description" id="description"  class="form-control" value='<%= vcb.getDescription() %>' placeholder="Add description for the Video Content..." />
													</div> 
													<div class="form-group">
														<label for="subject"> Video Subject </label>
														<form:input path="subject" value="<%= vcb.getSubject() %>" required="required"  placeholder="Add Subject" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="videoLink"> Video Link </label>
														<form:input path="videoLink" value="<%= vcb.getVideoLink() %>" required="required"  placeholder="Add Video Link" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
													<div class="form-group">
														<label for="thumbnailUrl"> Video Thumbnai Url </label>
														<form:input path="thumbnailUrl" value="<%= vcb.getThumbnailUrl() %>" required="required"  placeholder="Add Thumbnail Url" style="color:black; padding:6px 12px; width:100%;"/>
													</div>
																<div class=" form-group controls">
																	<button id="submit" name="submit" class="btn btn-large btn-primary" formaction="postVideoSubTopic" >Save</button>
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
         
        <%}catch(Exception e){
        	e.printStackTrace();
        } %>
	</section>

	<jsp:include page="footer.jsp" />
	
<script type="text/javascript">

$(document).ready (function(){

///////////////////////////////////////////////////////////////////
	
	
	$('.selectConsumerType').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programStructureId').html(options);
	$('#programId').html(options);
	$('.selectSubject').html(options);
	
	 
	var data = {
			id:this.value
	}
console.log(this.value)
	
	console.log("===================> data id : " + id);
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam/getDataByConsumerType",   
		data : JSON.stringify(data),
		success : function(data) {
			console.log("SUCCESS Program Structure: ", data.programStructureData);
			console.log("SUCCESS Program: ", data.programData);
			console.log("SUCCESS Subject: ", data.subjectsData);
			var programData = data.programData;
			var programStructureData = data.programStructureData;
			var subjectsData = data.subjectsData;
			
			options = "";
			let allOption = "";
			
			//Data Insert For Program List
			//Start
			for(let i=0;i < programData.length;i++){
				allOption = allOption + ""+ programData[i].id +",";
				options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			console.log("==========> options\n" + options);
			$('#programId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			options = ""; 
			allOption = "";
			//Data Insert For Program Structure List
			//Start
			for(let i=0;i < programStructureData.length;i++){
				allOption = allOption + ""+ programStructureData[i].id +",";
				options = options + "<option value='" + programStructureData[i].id + "'> " + programStructureData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			console.log("==========> options\n" + options);
			$('#programStructureId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			
			options = ""; 
			allOption = "";
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
			);
			//End
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});
	
	///////////////////////////////////////////////////////
	
	
		$('.selectProgramStructure').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('#programId').html(options);
	$('.selectSubject').html(options);
	
	 
	var data = {
			programStructureId:this.value,
			consumerTypeId:$('#consumerTypeId').val()
	}
	console.log(this.value)
	
	console.log("===================> data id : " + $('#consumerTypeId').val());
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam/getDataByProgramStructure",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data.programData);
			var programData = data.programData;
			var subjectsData = data.subjectsData;
			
			options = "";
			let allOption = "";
			
			//Data Insert For Program List
			//Start
			for(let i=0;i < programData.length;i++){
				allOption = allOption + ""+ programData[i].id +",";
				options = options + "<option value='" + programData[i].id + "'> " + programData[i].name + " </option>";
			}
			allOption = allOption.substring(0,allOption.length-1);
			
			console.log("==========> options\n" + options);
			$('#programId').html(
					 "<option value='"+ allOption +"'>All</option>" + options
			);
			//End
			
			options = ""; 
			allOption = "";
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
			);
			//End
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});


/////////////////////////////////////////////////////////////

	
		$('.selectProgram').on('change', function(){
	
	
	let id = $(this).attr('data-id');
	
	
	let options = "<option>Loading... </option>";
	$('.selectSubject').html(options);
	
	 
	var data = {
			programId:this.value,
			consumerTypeId:$('#consumerTypeId').val(),
			programStructureId:$('#programStructureId').val()
	}
	console.log(this.value)
	
	
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : "/exam/getDataByProgram",   
		data : JSON.stringify(data),
		success : function(data) {
			
			console.log("SUCCESS: ", data.subjectsData);
			
			var subjectsData = data.subjectsData;
			
			
			
			
			options = ""; 
			//Data Insert For Subjects List
			//Start
			for(let i=0;i < subjectsData.length;i++){
				
				options = options + "<option value='" + subjectsData[i].name + "'> " + subjectsData[i].name + " </option>";
			}
			
			
			console.log("==========> options\n" + options);
			$('.selectSubject').html(
					" <option disabled selected value=''> Select Subject </option> " + options
			);
			//End
			
			
			
			
		},
		error : function(e) {
			
			alert("Please Refresh The Page.")
			
			console.log("ERROR: ", e);
			display(e);
		}
	});
	
	
});

//////////////////////////////////////////////








	
	
	
});



</script>

</body>
</html>
