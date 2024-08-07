<!DOCTYPE html>

<%@page import="com.nmims.controllers.BaseController"%>
<html lang="en">
    
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.VideoContentAcadsBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
    <jsp:include page="common/jscss.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
    </jsp:include>
   
	<style>
		.boxShadow {
		    box-shadow: 0 0 30px rgba(0,0,0,0.8);
		    -moz-box-shadow: 0 0 30px rgba(0,0,0,0.8); 
		    -webkit-box-shadow: 0 0 30px rgba(0,0,0,0.8);
		    -o-box-shadow: 0 0 30px rgba(0,0,0,0.8);
		}
		.video{
			width:100%;
			height:300px;
		}
		.thumbnailDiv:hover .playIconStyle{
			color:#d2232a !important;
			}
		
		.thumbnailDiv:hover .videoThumbnail{
			    -webkit-filter: grayscale(100%); /* Safari 6.0 - 9.0 */
    			filter: grayscale(100%);
			    -webkit-filter: blur(3px); /* Safari 6.0 - 9.0 */
    			filter: blur(3px);
		}
		.playIconStyle{
			position: absolute; 
			top: 50%; 
			left: 50%; 
			transform: translate(-50%, -50%); 
			font-size: 70px !important; 
			color:#00000087 !important;
		}
		#titleRow{
			margin: 0px 10px 0px 0px !important;
			padding: 0px 10px 0px 0px !important;
		}
		#titleDiv{
			margin: 0px 10px 0px 0px !important; 
			padding: 0px 10px 0px 0px !important;
		}
		#titleDiv h2{
			margin: 10px 10px 0px 0px !important; 
			padding: 10px 10px 0px 0px !important;
		}
		#searchFormDiv{
			background-color: #F2F2F2; 
			margin: 0px 10px 0px 0px !important; 
			padding: 0px 10px 0px 0px !important;
		}
		#searchInput{
			color: black;  
			width: 100%; 
			margin: 1em 0.25em 1em 0em; 
			padding: 2px 15px;
		}
		a.disabled {
		  pointer-events: none;
		  cursor: default;
		}
		
	</style>
    <body>
    <%
    	BaseController viedoCon = new BaseController();
    	String disableLink="", videoLinkColor="";
    	if(viedoCon.checkLead(request, response)){
    		disableLink = "disabled";
    		videoLinkColor = "color: #b9b9b9;";
    	}
    	ArrayList<VideoContentAcadsBean> videoContentForLeads = (ArrayList<VideoContentAcadsBean>) request.getAttribute("sessionForLeads");  
    %>
    
    <%try{ %>
		    	
		<%
		ArrayList<VideoContentAcadsBean> VideoContentsList = (ArrayList<VideoContentAcadsBean>) request.getAttribute("VideoContentsList");  
		int contentSize = VideoContentsList !=null ? VideoContentsList.size() : 0;
		%>
    	<%@ include file="common/header.jsp" %>
    	
    	 <div class="sz-main-content-wrapper">
        
			<!-- Custom breadcrumbs as requirement is diff. Start -->
			<div class="sz-breadcrumb-wrapper">
			    <div class="container-fluid">
			        <ul class="sz-breadcrumbs">
			        		<li><a href="/studentportal/home">Student Zone</a></li>
			        		<li><a href="/acads/student/videosHome?pageNo=1&academicCycle=${currentSessionCycle}">Session Videos</a></li>
			        	
			        </ul>
			        <ul class="sz-social-icons">
			            <li><a href="https://www.facebook.com/NMIMSSCE" class="icon-facebook" target="_blank"></a></li>
			            <li><a href="https://twitter.com/NMIMS_SCE" class="icon-twitter" target="_blank"></a></li>
			            <!-- <li><a href="https://plus.google.com/u/0/116325782206816676798/posts" class="icon-google-plus" target="_blank"></a></li> -->
						
			        </ul>
			    </div>
			</div>
			<!-- Custom breadcrumbs as requirement is diff. End -->
        	
            
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
				<jsp:include page="common/left-sidebar.jsp">
					<jsp:param value="Session Videos" name="activeMenu" />
				</jsp:include>

				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						 <div class="sz-content">
								    	    							
									<%-- 
									
									commented for now
								    	<div class="well">
										<h4>
										<b>
										Dear Student,
										<br><br>
										As of now you cannot access the lecture videos, we suggest you try checking after 24 hrs.
										<br><br>
										Thanks and Regards,<br>
										Team NGASCE
										
										</b>
										</h4>
										</div>
										
									commented for now
									 --%>	
										<div class="">
										<div class="container">

											<div id="titleRow" class="row"
												style="">
												<div id="titleDiv" class="col-md-12" style="">
													<h2 class="" style="">
														Session Videos
													</h2>
												</div>
												<div class="col-md-12" id="searchFormDiv"
													style="">
													<form action="/acads/student/searchVideos" method="post">  
														<div class="row giveBorder">
															<input type="hidden" name="academicCycle" value="${academicCycle}">
															<div class="col-md-10 giveBorder noPaddingNOMargin">
																<div class="form-group">
																	<input type="text" class="form-control" id="searchInput" name="searchItem"
																	value="${searchItem}"
																	placeholder="Search Videos"
																	style="" required>
																</div>
															</div>
															<div class="col-md-2 giveBorder ">
																<div class=" form-group controls">
																	<button type="submit">
																	<span><i class="fa fa-search"></i></span>
																	</button>
																</div>
															</div>
														</div>
													</form>
												</div>
												<div class="col-md-2"></div>
											</div>
											<c:if test="${academicCycleList !=null }">
											
											<h6>Filter Search Results : </h6>							
											<div class="col-md-12">																						
											
												<div class="col-md-6 ">
													<div class="col-md-4 " >
														<h6>By Professor :</h6>
													</div>
													<div class="col-md-8 ">
																<select id="facultySelect" class="filterBy" style="color: black;" type="facultySelect">
																		<option value="${selectedFaculty.facultyId}">
																			<c:if test="${selectedFaculty.facultyId == 'All'}">
																				${selectedFaculty.facultyId}
																			</c:if>
																			<c:if test="${selectedFaculty.facultyId != 'All'}">
																			Prof. ${selectedFaculty.firstName} ${selectedFaculty.lastName}
																			</c:if>
																			
																		</option>
																			
																		 <c:forEach var="faculty" items="${facultyList}">
																		 <c:if test="${faculty.facultyId != selectedFaculty.facultyId}">
																				<option value="${faculty.facultyId}"> 
																					Prof. ${faculty.firstName} ${faculty.lastName} 
																				</option>
																				</c:if>
																		</c:forEach> 
																	
																		<c:if test="${selectedFaculty.facultyId != 'All'}">
																			<option value="All"> 
																			All
																		</option>
																		</c:if>
																	
																     </select>
													</div>
												</div>
												<div class="col-md-6 ">
													<div class="col-md-4 " >
														<h6> By Subject :</h6>
													</div>
													<div class="col-md-8 ">
																<select id="subjectSelect" class="filterBy"  style="color: black; width: 80%;" type="subjectSelect">
																		<option value="${programSemSubjectIds}">
																			${selectedSubject}
																		</option>
																		<%-- 	<c:forEach var="subjectTemp" items="${allsubjects}">
																				<option value="${subjectTemp}"> 
																					${subjectTemp}
																				</option>
																			</c:forEach> --%>
																			<c:forEach var="subject" items="${programSemSubjectIdWithSubject }">
																			 <c:if test="${subject.programSemSubjectId != programSemSubjectIds}">
																				<option value="${subject.programSemSubjectId }">
																					${subject.subject }
																				</option>
																			</c:if>
 																			</c:forEach>
 																			<c:forEach var="commonSub" items="${commonSubjects}">
 																			 <c:if test="${commonSub != programSemSubjectIds}">
																				<option value="${commonSub}"> 
																					${commonSub}
																				</option>
																				</c:if>
																			</c:forEach>
																			<c:if test="${programSemSubjectIds != 'All'}">
																		<option value="All"> 
																			All
																		</option>
																		</c:if>
																     </select>
													</div> 
													
													
													<%-- <div class="col-md-8 ">
																<select id="subjectSelect" class="filterBy"  style="color: black; width: 80%;">
																		<option value="${subjectCodeIds}">
																			${selectedSubject}
																		</option>
																			<c:forEach var="subjectTemp" items="${allsubjects}">
																				<option value="${subjectTemp}"> 
																					${subjectTemp}
																				</option>
																			</c:forEach>
																			<c:forEach var="subject" items="${subjectCodeIdWithSubject }">
																				<option value="${subject.subjectCodeId }">
																					${subject.subjectName }
																				</option>
 																			</c:forEach>
 																			<c:forEach var="commonSub" items="${commonSubjects}">
																				<option value="${commonSub}"> 
																					${commonSub}
																				</option>
																			</c:forEach>
																		<option value="All"> 
																			All
																		</option>
																     </select>
													</div>  --%>
													
												</div>
												<div class="col-md-6 ">
													<div class="col-md-4 " >
														<h6>By Academic Cycle :</h6>
													</div>
													<div class="col-md-8 ">
																<select id="academicCycleSelect" class="filterBy"  style="color: black;" type="academicCycleSelect">
																		<%--  <option value="${academicCycle}">
																		${academicCycle}
																		</option>  --%>
																			<c:forEach var="cycle" items="${academicCycleList}">
																				<option value="${cycle.month}${cycle.year}"> 
																															
																					<%-- <c:set var = "string1" value = "${cycle}"/>
																					<c:set var = "string2" value = "${fn:substring(string1,0,3)}"/>
																					<c:set var = "string3" value = "${fn:substring(string1,3,7)}"/>
																					
																				
																				${string2} - ${string3} --%>
																				${cycle.month} - ${cycle.year}
																				</option>
																			</c:forEach>
																		<option value="All"> 
																			All
																		</option>
																     </select>
<!-- <<<<<<< HEAD -->
<!-- 													</div> 		 -->
<!-- 											</div> -->
<!-- 										</div> -->
<!-- 									</div> -->
<%-- 								</c:if> --%>
<%-- 								<%@ include file="messages.jsp"%> --%>
<!-- ======= -->
													</div> 																	
												</div>
											<div class="col-md-6">
												<div class="col-md-4">
													<h6>By Batch Track :</h6>
												</div>
												<div class="col-md-8">
													<select id="batchSelect" class="filterBy"
														style="color: black; width: 80%;"  type="batchSelect">
														<c:choose>
														<c:when test="${allBatchTracks.size() > 0 }">
														<option value="All">
															All</option>
														<c:forEach var="track" items="${allBatchTracks}">
															<option value="${track}">${track}</option>
														</c:forEach>					\
														</c:when>
													<c:otherwise>
													<option value="All">
															No Track</option>
													</c:otherwise>
													</c:choose>												
													</select>
												</div>
											</div>
										</div>									
															
												</c:if>
										<%@ include file="messages.jsp"%>

										</div>
									 		<div class="container" style="">
									 		<div>
									 			
									 		<% 
									 			if(viedoCon.checkLead(request, response)) {
									 		   		if(videoContentForLeads != null && videoContentForLeads.size() != 0){ %>
									 		   			<div style="margin-bottom: 20px; border-bottom: 2px solid gray; display:inline-block; width: 100%"><h2>Available Videos</h2></div>
									 					<% 
									 						for(VideoContentAcadsBean video : videoContentForLeads){
										 		   			String videoType ="watchVideos";
															int idForAllTopics= (int)(long)video.getId();
															String pssId=video.getProgramSemSubjectId();
															if(video.getParentVideoId()>0){
																videoType ="watchVideoTopic";
																idForAllTopics = (int) (long) video.getParentVideoId();
															}
									 		%>
									 		
									 			<div class="row jumbotron" style="padding:10px 10px;">
													<div class="col-md-4 thumbnailDiv" style="min-height:200px !important;">
														<a href="/acads/student/<%=videoType %>?id=<%= video.getId() %>&pssId=<%= video.getProgramSemSubjectId() %>"  title="<%= video.getFileName() %>" style="float:right !important"> 
															<img src="<%= video.getThumbnailUrl()%>" alt="Video Thumbnail" class="img-responsive videoThumbnail">
															<i class="fa-regular fa-circle-play playIconStyle" style="" aria-hidden="true"></i>
										 				</a>
													</div> 
													<div class="col-md-8 row" style="">
														<div class="col-md-12" style="">
															<a style="float:left !important;" href="/acads/student/<%=videoType %>?id=<%= video.getId() %>&pssId=<%= video.getProgramSemSubjectId() %>"  title="<%= video.getFileName() %>" style="float:right !important"> 
																<h2><%= video.getFileName() %></h2>
															</a>
														</div>
														<div class="col-md-12" style="height:65px !important; overflow:hidden;">
															<h3 align="left" style="float:left !important;" ><%= video.getDescription() %></h3> 
														</div>
																
														<div class="col-md-12" style="">
															<a style="" href="/acads/student/videoForSubject?subject=<%= video.getSubject().replaceAll("&","_") %>"  title="All Videos of this subject" > 
																<h4> Subject : 
																	 <b><%= video.getSubject() %></b> 
																</h4>
															</a>
														</div> 
																<%
																	if(video.getFacultyName() != null){
																%>
																<div class="col-md-12" style="">
																	<h5 >Session By : <a href="/studentportal/facultyProfile?facultyId=<%= video.getFacultyId() %>" target="_blank"><b><%= video.getFacultyName() %> </b></a></h5> 
																	<%-- <h5 >Session By : <b><%= video.getFacultyName() %> </b></h5> --%>
																</div> 
																<%
																	}
																%> 
																<%
																	if(video.getTrack() != null && !" ".equals(video.getTrack())){
																%>
																<div class="col-md-12" style="">
																	<h5 style="margin-bottom: 10px !important; margin-top: 10px !important;"> Track / Group :  <%= video.getTrack() %> </h5>
																</div>
																<%
																	}
																%>
																<%
																	if(video.getSessionDate() != null && !" ".equals(video.getSessionDate())){
																%>
																<div class="col-md-12" style="">
																	<h5> Session Date :  <%= video.getSessionDate() %> </h5>
																</div>
																<%
																	}
																%> 
																<div class="col-md-12" style="">
																	<h5> Academic Cycle :  <%= video.getMonth() %> &nbsp; <b><%= video.getYear() %> </b></h5>
																</div> 
																<div class="col-md-12" style="">
																	<a class="btn btn-sm btn-primary" href="/acads/student/watchVideos?id=<%= idForAllTopics %>&pssId=<%= pssId%>"  title="<%= video.getFileName() %>" style="float:right !important"> 
																	View Details >>
																	</a>
																</div>
															</div> 
																						
														</div>	
													<% } %>
													<div style="margin-bottom: 20px; border-bottom: 2px solid gray; display:inline-block; width: 100%"><h2>All Videos</</h2></div>
									 		<%  } } %>
									 		</div>
									 		
									 		
									 		<% 
												if(contentSize > 0){
													for(VideoContentAcadsBean video : VideoContentsList){	
														String videoType ="watchVideos";														
														int idForAllTopics= (int)(long)video.getId();
														String pssId=video.getProgramSemSubjectId();
														if(video.getParentVideoId()>0){
															videoType ="watchVideoTopic";
															idForAllTopics = (int) (long) video.getParentVideoId();
														}
												%>
												<div class="row jumbotron" style="padding:10px 10px;">
													<div class="col-md-4 thumbnailDiv" style="min-height:200px !important;">
														<a class="<%= disableLink %>" href="/acads/student/<%=videoType %>?id=<%= video.getId() %>&pssId=<%= video.getProgramSemSubjectId() %>"  title="<%= video.getFileName() %>" style="float:right !important"> 
															<img src="<%= video.getThumbnailUrl()%>" alt="Video Thumbnail" class="img-responsive videoThumbnail">
															<i class="fa-regular fa-circle-play playIconStyle" style="" aria-hidden="true"></i>
										 				</a>
													</div>
													<div class="col-md-8 row" style="">
														<div class="col-md-12" style="">
															<a style="float:left !important;" class="<%= disableLink %>" href="/acads/student/<%=videoType %>?id=<%= video.getId() %>&pssId=<%= video.getProgramSemSubjectId() %>"  title="<%= video.getFileName() %>" style="float:right !important"> 
																<h2><%= video.getFileName() %></h2>
															</a>
														</div>
														<div class="col-md-12" style="height:65px !important; overflow:hidden;">
															<h3 align="left" style="float:left !important;" ><%= video.getDescription() %></h3> 
														</div>
																
														<div class="col-md-12" style="">
															<a style="" class="<%= disableLink %>" href="/acads/student/videoForSubject?subjectCodeId=<%=video.getSubjectCodeId()%>&subject=<%=video.getSubject()%>"  title="All Videos of this subject" > 
																<h4 style="<%= videoLinkColor %>"> Subject : 
																	 <b><%= video.getSubject() %></b> 
																</h4>
															</a>
														</div> 
																<%
																	if(video.getFacultyName() != null){
																%>

																<div class="col-md-12" style="">
 																	<h5 style="<%= videoLinkColor %>">Session By : <a href="/studentportal/facultyProfile?facultyId=<%= video.getFacultyId() %>" target="_blank"><b><%= video.getFacultyName() %> </b></a></h5> 
																	<%-- <h5 style="<%= videoLinkColor %>">Session By : <b><%= video.getFacultyName() %> </b></h5> --%>
																</div> 
																<%
																	}
																%> 
																<%
																	if(video.getTrack() != null && !" ".equals(video.getTrack())){
																%>
																<div class="col-md-12" style="">
																	<h5 style="margin-bottom: 10px !important; margin-top: 10px !important; <%= videoLinkColor %>"> Track / Group :  <%= video.getTrack() %> </h5>
																</div>
																<%
																	}
																%>
																<%
																	if(video.getSessionDate() != null && !" ".equals(video.getSessionDate())){
																%>
																<div class="col-md-12" style="">
																	<h5 style="<%= videoLinkColor %>"> Session Date :  <%= video.getSessionDate() %> </h5>
																</div>
																<%
																	}
																%> 
																<div class="col-md-12" style="">
																	<h5 style="<%= videoLinkColor %>"> Academic Cycle :  <%= video.getMonth() %> &nbsp; <b><%= video.getYear() %> </b></h5>
																</div> 
																<div class="col-md-12" style="">
																	<a class="btn btn-sm btn-primary <%= disableLink %>" href="/acads/student/watchVideos?id=<%= idForAllTopics %>&pssId=<%= pssId%>"  title="<%= video.getFileName() %>" style="float:right !important"> 
																	View Details >>
																	</a>
																</div>
															</div> 
																						
														</div>	
												<% }} else { %>
													<div class=""  >
														<h2>No Videos Found.</h2>
													</div>
												<% }%>
										<!-- Start Pagination Code -->	
										<c:if test="${showPagination}">
										<c:url var="firstUrl" value="videosHome?pageNo=1&academicCycle=${currentSessionCycle}" />
										<c:url var="lastUrl" value="videosHome?pageNo=${page.totalPages}&academicCycle=${currentSessionCycle}" />
										<c:url var="prevUrl" value="videosHome?pageNo=${page.currentIndex - 1}&academicCycle=${currentSessionCycle}" />
										<c:url var="nextUrl" value="videosHome?pageNo=${page.currentIndex + 1}&academicCycle=${currentSessionCycle}" />
											
										<c:choose>
											<c:when test="${page.totalPages > 1}">
											<div align="center">
											    <ul class="pagination">
											        <c:choose>
											            <c:when test="${page.currentIndex == 1}">
											                <li class="disabled"><a href="#">&lt;&lt;</a></li>
											                <li class="disabled"><a href="#">&lt;</a></li>
											            </c:when>
											            <c:otherwise>
											                <li><a href="${firstUrl}">&lt;&lt;</a></li>
											                <li><a href="${prevUrl}">&lt;</a></li>
											            </c:otherwise>
											        </c:choose>
											        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
											            <c:url var="pageUrl" value="videosHome?pageNo=${i}&academicCycle=${currentSessionCycle}" />
											            <c:choose>
											                <c:when test="${i == page.currentIndex}">
											                    <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
											                </c:when>
											                <c:otherwise>
											                    <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
											                </c:otherwise>
											            </c:choose>
											        </c:forEach>
											        <c:choose>
											            <c:when test="${page.currentIndex == page.totalPages}">
											                <li class="disabled"><a href="#">&gt;</a></li>
											                <li class="disabled"><a href="#">&gt;&gt;</a></li>
											            </c:when>
											            <c:otherwise>
											                <li><a href="${nextUrl}">&gt;</a></li>
											                <li><a href="${lastUrl}">&gt;&gt;</a></li>
											            </c:otherwise>
											        </c:choose>
											    </ul>
											</div>
											</c:when>
											</c:choose>	
											</c:if>
										<!-- End Pagination Code -->
										
											</div>
									</div> 
									</div>
              				</div>
              		</div>
			</div>
        </div>
    
        <%}catch(Exception e){
        	  
        } %>
    <jsp:include page="common/footer.jsp"/>
   
    <script>
   $(document).ready(function(){
	   $("#batchSelect").val('${selectedBatch}');
	   $("#academicCycleSelect").val("${requestScope.academicCycle}").attr('selected', 'selected');
	   $("#facultySelect").val("${requestScope.selectedFaculty.facultyId}").attr('selected', 'selected');
	   
	   $('#academicCycleList').on('change', function () {
	          var cycle = $(this).val(); // get selected value
	          window.location = '/acads/student/searchByFilter?pageNo=1&academicCycle='+cycle; // redirect
	      });
	   $('.filterBy').on('change', function () {

		   var type = $(this).attr("type");
		   
		   $('.filterBy').each(function() {
			   var id = $(this).attr('id');
			  /*  if(id != type )
			   		$("#"+id).val('All'); */
		   });
		   
		   var searchInput = $('#searchInput').val(); 
		   var faculty = $('#facultySelect').val(); 
		   var subject = $('#subjectSelect').val(); 
	       var cycle = $('#academicCycleSelect').val(); 
	       var batch = $("#batchSelect").val();
			window.location = '/acads/student/searchByFilter?searchInput='+encodeURIComponent(searchInput)+'&faculty='+encodeURIComponent(faculty)+'&subject='+encodeURIComponent(subject)+'&cycle='+encodeURIComponent(cycle)+'&batch='+encodeURIComponent(batch); // redirect
	      });
   }); 

   document.getElementById("clearData").onclick = function () {
       location.href = "/acads/student/videosHome?pageNo=1&academicCycle=${defaultAcademicCycle}";
   };
   
   </script> 
   </body>
</html>
