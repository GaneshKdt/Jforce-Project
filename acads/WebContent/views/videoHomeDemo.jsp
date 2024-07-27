<!DOCTYPE html>

<%@page import="com.nmims.controllers.BaseController"%>
<html lang="en">
    
<%@page import="java.util.ArrayList"%>
<%@page import="com.nmims.beans.VideoContentAcadsBean"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
    <jsp:include page="common/jscssNew.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
    </jsp:include>
    
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/videohomedemo.css">
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
    	<%@ include file="common/headerDemo.jsp" %>
    	
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
        	
            <!-- Bootstrap to be update  -->
            <div class="sz-main-content menu-closed">
                    <div class="sz-main-content-inner">
              				 <%@ include file="common/left-sidebar.jsp" %>           				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>             						            						 
								    	  <div class="container-fluid " style="width:90%">
								    	  <div class="sz-content">
								    	     <div class="row">
												<div class="col-md-12 mt-md-5 mt-lg-2">
													<h2>Session Videos</h2>
												</div>
												<div class="col-md-12">
													<form action="/acads/student/searchVideos" method="post">  
														<div class="row">
															<input type="hidden" name="academicCycle" value="${academicCycle}">															
															<div class="col-md-11  d-flex align-items-center  form-group ">									
																	<input type="text" class="form-control rounded" id="searchInput" name="searchItem"
																	value="${searchItem}"
																	placeholder="Search Videos"
																	required>										
															</div>							
															<div class="col-md-1 d-flex align-items-center">
															<button type="submit" class="rounded">
																	<span><i class="fa fa-search"></i></span>
																	</button>
															</div>														 
														</div>
													</form>
												</div>
												<div class="col-md-2"></div>
												
 											<c:if test="${academicCycleList !=null }">
 												<h6 class="mt-sm-2">Filter Search Results : </h6>	
 
                                       <div class="col-lg-12 ">																						
								           <div class="row">
									       <div class="col-lg-6 ">
									         <div class="row">
												<div class="col-md-4 d-flex  align-items-center" >
													<h6>By Professor :</h6>
												</div>
													<div class="col-md-8 ">
														<select id="facultySelect" class="filterBy text-dark form-select"  type="facultySelect">
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
											</div>
									
									
											<div class="col-lg-6">
											  <div class="row">
												<div class="col-md-4 d-flex  align-items-center" >
													<h6> By Subject :</h6>
												</div>
												<div class="col-md-8 ">
													<select id="subjectSelect" class="filterBy text-dark form-select"  type="subjectSelect">
															<option value="${programSemSubjectIds}">
																${selectedSubject}
															</option>
														
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
												
										
												</div>
												</div>
									
											</div>
										</div>
									<div class="col-lg-12 mt-2">
										<div class="row">
												<div class="col-lg-6 ">
												<div class="row">
												<div class="col-md-4 d-flex  align-items-center" >
													<h6>By Academic Cycle :</h6>
												</div>
												<div class="col-md-8 ">
													<select id="academicCycleSelect" class="filterBy text-dark form-select" type="academicCycleSelect">																		
														<c:forEach var="cycle" items="${academicCycleList}">
															<option value="${cycle.month}${cycle.year}"> 																		
															${cycle.month} - ${cycle.year}
															</option>
														</c:forEach>
														<option value="All"> 
															All
														</option>
											     	</select>
			                  	               </div> 																	
											</div>
										</div>
								
								<div class="col-lg-6  col-xs-12" >
								<div class="row">
									<div class="col-md-4 col-xs-4 d-flex  align-items-center">
										<h6>By Batch Track :</h6>
									</div>
									<div class="col-md-8 col-xs-8">
										<select id="batchSelect" class="filterBy text-dark form-select" 
											 type="batchSelect">
											<c:choose>
											<c:when test="${allBatchTracks.size() > 0 }">
											<option value="All">
												All</option>
											<c:forEach var="track" items="${allBatchTracks}">
												<option value="${track}">${track}</option>
											</c:forEach>				
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
							 </div>
							</div>
					     </c:if>
					     <div class="mt-2"><%@ include file="messages.jsp"%></div>
					     
					  </div>
					 <div> 
						<% 
							if(viedoCon.checkLead(request, response)) {
						   		if(videoContentForLeads != null && videoContentForLeads.size() != 0){ %>
						   	<!-- remained to update -->		<div style="margin-bottom: 20px; border-bottom: 2px solid gray; display:inline-block; width: 100%"><h2>Available Videos</h2></div>
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
						<div class="card mb-4   bg-body rounded">
								  <div class="row gx-0">
									    <div class="col-md-5 d-flex justify-content-center align-items-center thumbnailDiv position-relative">
									        <a class="float-end !important ps-2" href="/acads/student/<%=videoType %>?id=<%= video.getId() %>&pssId=<%= video.getProgramSemSubjectId() %>"  title="<%= video.getFileName() %>"> 
												<img src="<%= video.getThumbnailUrl()%>" alt="Video Thumbnail" class="img-fluid rounded videoThumbnail border border-1" >
												<i class="fa-regular fa-circle-play  fa-5x playIconStyle position-absolute start-50 top-50" aria-hidden="true"></i> 
												</a>
									    </div>
									    <div class="col-md-7">
									      <div class="card-body">
									        
										    <a class="<%= disableLink %>" href="/acads/student/<%=videoType %>?id=<%= video.getId() %>"  title="<%= video.getFileName() %>"><h5 class="card-title d-inline-block text-truncate w-100 mt-0 mb-0"><%= video.getFileName() %> </h5></a>								     
									      
											<small class="text-muted text-truncate w-100"><%= video.getDescription() %> </small> 	
					
										    <p class="card-text"> Subject :<a href="/acads/student/videoForSubject?subject=<%= video.getSubject().replaceAll("&","_") %>"><%= video.getSubject() %></a></p>
												
											<%if(video.getFacultyName() != null){%>
									   			<p class="card-text ">  Session By : <a href="/studentportal/facultyProfile?facultyId=<%= video.getFacultyId() %>" target="_blank"><%= video.getFacultyName() %></a> 
									   	   | <%if(video.getTrack() != null && !" ".equals(video.getTrack())){%>						
												<small class="muted"> Track : <%= video.getTrack() %> </small>
											<%}%></p><%}%>
												
											<%if(video.getSessionDate() != null && !" ".equals(video.getSessionDate())){%>
											<small class="text-muted">Session Date :  <%= video.getSessionDate() %>     | Academic Cycle :  <%= video.getMonth() %><%= video.getYear() %></small>
										    <a href="/acads/student/watchVideos?id=<%= idForAllTopics %>" class="card-link  position-absolute bottom-0 end-0" title="<%= video.getFileName() %>><small>View Details>></small></a>     
											<%}%> 
									        
									      </div>
									    </div>
								  </div>
								</div>
				
								<% } %>
						<!--  remained to update -->		<div style="margin-bottom: 20px; border-bottom: 2px solid gray; display:inline-block; width: 100%"><h2>All Videos</</h2></div>
							<%  } } %>
						

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
							<div class="card mb-3 mt-2 border border-0" >
							  <div class="row gx-0">
							    <div class=" col-lg-5 col-xl-4 thumbnailDiv ">
							     <a class="<%= disableLink %> float-end !important" href="/acads/student/<%=videoType %>?id=<%= video.getId() %>&pssId=<%= video.getProgramSemSubjectId() %>"  title="<%= video.getFileName() %>"> 														
										<div class=" position-relative"><img src="<%= video.getThumbnailUrl()%>" alt="Video Thumbnail" class="img-fluid  rounded-start videoThumbnail  border border-1" >
										<i class="fa-regular fa-circle-play  fa-5x playIconStyle position-absolute start-50 top-50" aria-hidden="true"></i>
										</div>				    
										</a>
							    </div>
							    <div class=" col-lg-7 col-xl-8 position-relative">
							      <div class="card-body">
						        
						          <a href="/acads/student/<%=videoType %>?id=<%= video.getId() %>&pssId=<%= video.getProgramSemSubjectId() %>"  title="<%= video.getFileName() %>"><h5 class="card-title d-inline-block text-truncate w-100 mt-0 mb-0"><%= video.getFileName() %> </h5></a>
						
										<small class="text-muted text-truncate w-100"><%= video.getDescription() %> </small> 
									
										<p class="card-text"> Subject :<a href="/acads/student/videoForSubject?subjectCodeId=<%=video.getSubjectCodeId()%>&subject=<%=video.getSubject()%>"><%= video.getSubject() %></a>					
										</p>
										
								    <%if(video.getFacultyName() != null){%>
									   <p class="card-text ">  Session By : <a href="/studentportal/facultyProfile?facultyId=<%= video.getFacultyId() %>" target="_blank"><%= video.getFacultyName() %></a> 
									   
									   | <%if(video.getTrack() != null && !" ".equals(video.getTrack())){%>						
										<small class="muted"> Track : <%= video.getTrack() %> </small>
										<%}%>					
									   </p>					
									<%}%>
																
									<%if(video.getSessionDate() != null && !" ".equals(video.getSessionDate())){%>
										<small class="text-muted">Session Date :  <%= video.getSessionDate() %>     | Academic Cycle :  <%= video.getMonth() %><%= video.getYear() %></small>
									    <a href="/acads/student/watchVideos?id=<%= idForAllTopics %>&pssId=<%= pssId%>" class="card-link  position-absolute bottom-0 end-0 me-2 mb-2"><small>View Details>></small></a>     
									<%}%> 									  								
							      </div>
							    </div>
							  </div>
							</div>
            
							<% }} else { %>
						  <div class=""  >
							<h2>No Videos Found.</h2>
						   </div>
						</div>
					</div>
								<% }%>	
								<!-- Start Pagination Code -->	
					<div class="col d-flex justify-content-center">
					<c:if test="${showPagination}">
					<c:url var="firstUrl" value="videosHome?pageNo=1&academicCycle=${currentSessionCycle}" />
					<c:url var="lastUrl" value="videosHome?pageNo=${page.totalPages}&academicCycle=${currentSessionCycle}" />
					<c:url var="prevUrl" value="videosHome?pageNo=${page.currentIndex - 1}&academicCycle=${currentSessionCycle}" />
					<c:url var="nextUrl" value="videosHome?pageNo=${page.currentIndex + 1}&academicCycle=${currentSessionCycle}" />																						    
					<c:choose>
						<c:when test="${page.totalPages > 1}">
						<div class="paginationBlock" align="center">
						    <ul class="pagination">
						        <c:choose>
						            <c:when test="${page.currentIndex == 1}">
						             <li class="page-item"><a class="page-link disabled" href="#">&lt;&lt;</a></li>
						              <li class="page-item"><a class="page-link disabled" href="#">&lt;</a></li>
						            </c:when>
						            <c:otherwise>
						             <li class="page-item"><a class="page-link" href="${firstUrl}">&lt;&lt;</a></li>
						              <li class="page-item"><a class="page-link" href="${prevUrl}">&lt;</a></li>
						            </c:otherwise>
						        </c:choose>
						        <c:forEach var="i" begin="${page.beginIndex}" end="${page.endIndex}">
						            <c:url var="pageUrl" value="videosHome?pageNo=${i}&academicCycle=${currentSessionCycle}" />
						            <c:choose>
						                <c:when test="${i == page.currentIndex}">
						                     <li class="page-item"><a class="page-link active" href="${pageUrl}"><c:out value="${i}" /></a></li>
						                </c:when>
						                <c:otherwise>
						                     <li class="page-item"><a class="page-link" href="${pageUrl}"><c:out value="${i}" /></a></li>
						                </c:otherwise>
						            </c:choose>
						        </c:forEach>
						        <c:choose>
						            <c:when test="${page.currentIndex == page.totalPages}">
						             <li class="page-item"><a class="page-link disabled" href="#">&gt; </a></li>
						              <li class="page-item"><a class="page-link disabled" href="#">&gt;&gt;</a></li>
						            </c:when>
						            <c:otherwise>
						             <li class="page-item"><a class="page-link" href="${nextUrl}">&gt;</a></li>
						              <li class="page-item"><a class="page-link" href="${lastUrl}">&gt;&gt;</a></li>
						            </c:otherwise>
						        </c:choose>
						    </ul>
						</div>
						</c:when>
						</c:choose>											      
							   
							</c:if> 
							</div>       								      
 					    </div> 
					  </div>
					  </div>
					 </div>
					</div>
				</div>
			 </div>
        <%}catch(Exception e){
        	  
        } %>     
    <jsp:include page="common/footerDemo.jsp"/>
   
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
