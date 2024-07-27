<!DOCTYPE html>

 

<%@page import="com.nmims.beans.VideoContentAcadsBean"%>
<%@page import="com.nmims.beans.StudentAcadsBean"%>
<%@page import="java.util.ArrayList"%>
<%
	VideoContentAcadsBean videoContent= (VideoContentAcadsBean)request.getAttribute("videoContent"); 
	ArrayList<VideoContentAcadsBean> relatedVideos= (ArrayList<VideoContentAcadsBean>)request.getAttribute("relatedVideos"); 
	ArrayList<VideoContentAcadsBean> videoSubTopicsList = (ArrayList<VideoContentAcadsBean>)request.getAttribute("videoSubTopicsList");
	int size = videoSubTopicsList !=null? videoSubTopicsList.size() : 0;
%>
<html lang="en">
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    
    <jsp:include page="common/jscssNew.jsp">
	<jsp:param value="Welcome to Student Zone" name="title"/>
    </jsp:include>

<%
	BaseController baseCon = new BaseController();
	String hide="";
	if(baseCon.checkLead(request, response)){
		hide="none";
	}
%>
<style>

.thumbnailDiv:hover .playIconStyle {
	color: #d2232a !important;
}

.thumbnailDiv:hover .videoThumbnail {
	-webkit-filter: grayscale(100%); /* Safari 6.0 - 9.0 */
	filter: grayscale(100%);
	-webkit-filter: blur(3px); /* Safari 6.0 - 9.0 */
	filter: blur(3px);
}

.playIconStyle {
	transform: translate(-50%, -50%);
	color: #00000087 !important;
}

#topicContainer{
	height:600px; !important;
	scroll-behavior: smooth;
}
 

@keyframes fadeInUp {
    from {
        transform: translate3d(0,40px,0)
    }

    to {
        transform: translate3d(0,0,0);
        opacity: 1
    }
}

@-webkit-keyframes fadeInUp {
    from {
        transform: translate3d(0,40px,0)
    }

    to {
        transform: translate3d(0,0,0);
        opacity: 1
    }
}

.animated {
    animation-duration: 0.4s;
    animation-fill-mode: both;
    -webkit-animation-duration: 0.4s;
    -webkit-animation-fill-mode: both
}

.animatedFadeInUp {
    opacity: 0
}

.fadeInUp {
    opacity: 0;
    animation-name: fadeInUp;
    -webkit-animation-name: fadeInUp;
}

</style>
<body>
   <% try{ 
   %> 
   
   
    	<%@ include file="common/headerDemo.jsp" %>
    	
    	 <div class="sz-main-content-wrapper">
        
			<!-- Custom breadcrumbs as requirement is diff. Start -->
			<div class="sz-breadcrumb-wrapper">
			    <div class="container-fluid">
			        <ul class="sz-breadcrumbs">
			        	<c:choose>
							<c:when test="${isLoginAsLead eq true}">
								<li><a href="/studentportal/student/getFreeCoursesList">My Courses</a></li>
				        		<li><a href="/studentportal/student/getFreeCoursesList">Subjects</a></li>
				        		<li><a href="#">${videoContent.subject}</a></li>
				        		<li><a href="#">${videoContent.fileName}</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="/studentportal/home">Student Zone</a></li>
				        		<li><a href="/acads/student/videosHome?pageNo=1&academicCycle=${currentSessionCycle}">Session Videos</a></li>
				        		<li><a href="/acads/student/videoForSubject?subjectCodeId=<%=videoContent.getSubjectCodeId()%>&subject=<%=videoContent.getSubject()%>"><%= videoContent.getSubject() %></a></li>
				        		<li><a href="#"><%= videoContent.getFileName() %></a></li>
							</c:otherwise>
						</c:choose>
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
                    	<c:if test = "${isLoginAsLead ne true}">
              				<%@ include file="common/left-sidebar.jsp" %>
              			</c:if>	
              				
              				<div class="sz-content-wrapper examsPage">
              						<%@ include file="common/studentInfoBar.jsp" %>
              						
              						<div class="sz-content">
              						
										 <div class=" container-fluid" style="width:98%" >
											
											<div class="row">
												<%
												if(videoContent !=null){
												%>
												<div class="col-md-12 col-lg-8 ">
													<div class="col-md-12">
														<form action="/acads/student/searchVideos" method="post">
												
													<div class="row mt-sm-4 mt-lg-1 mb-lg-2 mt-md-4 mb-md-2 ">
												
															<div class="form-group col-md-11  col-9 d-flex align-items-center">
																	<input type="text" class="form-control" id="searchInput" name="searchItem" placeholder="Search Videos"  required>
															</div>
													
													
															<%-- <div class="col-md-3 giveBorder noPaddingNOMargin">
																	<select name="subject" style="color: black; height: 30px; width: 100%; margin: 1em 0.25em; padding: 2px 15px;">
																		<option value="">Search By Subject (optional)</option>
																		<c:if test="${subjectList !=null }">
																			<c:forEach var="subject" items="${subjectList}">
																				<option value="${subject}">${subject}</option>
																			</c:forEach>
																		</c:if>
																     </select>																	</select>
															</div> --%>
													<div class="col-md-1 col-3" >
														
														<button type="submit" class="btn btn-dark float-end"><i class="fa fa-search pe-xl-3 ps-xl-2"></i></button>

													</div>
													</div>
														</form>
													</div>
										 			
													   
													   <div class="ratio ratio-16x9 w-100 overflow-hidden">
		 													 <iframe src='<%= videoContent.getVideoLink() %>' allow="autoplay" 
		 													  webkitAllowFullScreen mozallowfullscreen allowFullScreen allowfullscreen></iframe>
														</div>
														
										 			
														<p class="fw-bold fs-5 text-danger mt-2"> <%= videoContent.getFileName()%> </p>
														
										 				<p class="fs-6 d-inline "> Description :</p>
										 				
										 				<p class="d-inline text-muted fs-6"> <%= videoContent.getDescription() %> </p>
										 				
									 					<%if(videoContent.getFacultyName() != null){%>
														<p class="fs-6 mt-2">Session By : <%= videoContent.getFacultyName() %> </p>
														<%}%>
															
														<%if(videoContent.getSessionDate() != null && !" ".equals(videoContent.getSessionDate())){%>
																
														<p class="fs-6"> Session Date :  <%= videoContent.getSessionDate() %> </p>
															
														<%}%> 
																
														<%if(videoContent.getTrack() != null && !" ".equals(videoContent.getTrack())){%>
															
														<p class="fs-6"> Track/Group :  <%= videoContent.getTrack() %> </p>
															
														<%}%> 
										 			
																
														<p class="fs-6"> Academic Cycle :  <%= videoContent.getMonth() %> <%= videoContent.getYear() %></p>
																
																<%
																	if(videoContent.getMobileUrlHd() != null && !"".equals(videoContent.getMobileUrlHd())){
																		if( !"Enterprise Guide".equals(videoContent.getSubject()) && !"Enterprise Miner".equals(videoContent.getSubject()) && !"Visual Analytics".equals(videoContent.getSubject()) ){
																		 
																%>
																<div class="col-md-12">
																	<h5>
																	 	<!-- <style>
																	 		#videoDownloadLink {
																			  pointer-events: none;
																			  cursor: default;
																			}
																	 	</style> -->
																	 	
																	 	<!-- 
																	 	<a disabled id="videoDownloadLink" href="<%=videoContent.getMobileUrlHd()%>" class="btn btn-primary">
																	 		Download Video
																	 	</a>
																	 	<a disabled id="videoDownloadLink" href="#" class="btn btn-primary">
																	 		Download Video
																	 	</a> -->
																	 	 <%-- <a id="videoDownloadLink" 
																	 	 	href="<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_SERVER_RECORDINGS_PATH')" /><%=videoContent.getMeetingKey()%>.mp4"
																	 	 	class="btn btn-primary">
																	 		Download Video
																	 	</a> --%>
																	 	<!-- (Download Disabled, Will be availabe in Due Time) -->
																	 	<!--  (Right Click on button and Click "Save Link As") .
																	 	<span id="noticeSpan">(Right Click on button and Click "Save Link As")</span> -->
																	 </h5>
																</div>
																<%
																		}
																	}
																%>  
										 		</div>
										 		<%
													}else{
										 		%>
										 		<h2> No Video Content </h2>
										 		<% } %>
										 		<div class="col-lg-4 col-md-12 " >
									 			<p class="fs-5 d-flex justify-content-center mt-2 mb-0">Transcript</p>
													<div class="row mt-1">
													<div class="col-9 col-xl-10 d-flex align-items-center">
														<input placeholder="Search by any text/duration in the transcript"
														 id="search_subTopicsID"
														 type="text" class="form-control searchSubTopics" onchange="searchSubTopics()">
													 </div>
													 <div class="col-3 col-xl-2 d-flex align-items-center">
														<button class="btn btn-dark "  onclick="searchSubTopics()" ><i class="fa-solid fa-magnifying-glass" ></i></button>
													</div>
													</div>
										 			
										 		<div class="col-12" id="textMismatch"></div>
										 			
										 		<div id="topicContainer" class="overflow-auto position-relative" style="overflow-x:hidden !important;">	 
										 			<%
										 				if(size > 0){
										 					int vcbDivId = 0;
															for(VideoContentAcadsBean vcb : videoSubTopicsList){
										 			%>
										 			<div id="subTopicParent_Id_<%=vcbDivId %>" onclick="setTimeDuration('<%= vcb.getStartTimeInSeconds() %>')" class="row searchSubTopicsSpan  mb-1 w-100" style="cursor:pointer;background-color:#DFDFDB">
										 	
										 				<div id="subTopicChild1_Id_<%=vcbDivId %>" class="col-2   d-flex align-items-center position-relative">
										 					<a><i class="fa fa-play-circle-o fa-xl position-absolute start-50 top-50"></i></a>
										 				</div>
										 				<div  id="subTopicChild2_Id_<%=vcbDivId %>" class="col-7  d-flex align-items-center">
										 					<a class="searchSubTopicsTitle text-dark fs-6 p-2" ></i>
										 						<%= vcb.getFileName()  %>
										 					</a>
										 				</div>
										 				<div  id="subTopicChild3_Id_<%=vcbDivId %>" class="col-3  d-flex align-items-center">
										 					<%
										 						String[] timeArray = vcb.getDuration().split(":",-1);
										 						if("0".equals(timeArray[0])){
										 							vcb.setDuration(timeArray[1]+":"+timeArray[2]);
										 							
										 						}
										 					%>
										 					<span class="searchSubTopicsStartTime"><small><b><%= vcb.getStartTime() %></b></small></span>
										 				</div>
										 			</div> 
										 			
										 			<%
										 				vcbDivId++;	}}
											 		%> 
													 	 <div id="autoScrollBtn" class="fixed-bottom position-sticky animated animatedFadeInUp fadeInUp fadeInDown text-center" style="display:none;z-index:1;"> 
										 					
										 					<button type="button" class="btn btn-dark" onclick="viewCurrentElement()">
										 					<span><i class="fa fa-solid fa-arrow-up fa-1x" ></i> </span>
										 					Resume Transcript Auto-Scroll</button>
									 					</div> 
												 
										 		   </div> 
										 		  
										 		</div>
										 	</div>
										 	
										 	
													<%@ include file="queriesDemo.jsp" %> 
													 	
											
								 		<div class="col-md-12 mt-2">
									 			<h4 class="d-flex justify-content-center mt-4">Related Videos</h4>
									 		<div class="row">
									 		<%if(videoContent !=null){
												for(VideoContentAcadsBean vcb : relatedVideos){%>
												
											<div class="col-md-6 col-lg-4 mt-4 col-xxl-3 col-sm-12 ">
									 			<div class="card w-100">
													 <div class="col-md-12 thumbnailDiv position-relative">
															<a class="" href="/acads/student/watchVideos?id=<%= vcb.getId() %>&pssId=<%=vcb.getProgramSemSubjectId() %>"  title="<%= vcb.getFileName() %>"> 
									                         	<img src="<%= vcb.getThumbnailUrl()%>" alt="Video Thumbnail" class="videoThumbnail img-fluid rounded-top">
																<i class="fa fa-play-circle-o fa-4x position-absolute start-50 top-50 playIconStyle" aria-hidden="true"></i>
									 						</a>
									 				 </div>
											 				
													  <div class="card-body">
													    <h5 class="card-title text-truncate w-100">
													      <a class="text-dark " href="/acads/student/watchVideos?id=<%= vcb.getId() %>&pssId=<%=vcb.getProgramSemSubjectId() %>" title="<%= vcb.getFileName()%>">
 															  <b> <%= vcb.getFileName() %> </b>
								 						  </a>
								 						</h5>
								 						
													    <p class="card-text"> <%= vcb.getSubject() %></p>
													 	<p class="card-text"> Academic Cycle :  <%= vcb.getMonth() %>  <%= vcb.getYear() %> </p>
												
													  </div>
												</div>
										</div>
											
											<%}} %>
											</div>
											</div>
										 			<!-- End -->
										 			
										 			
										 </div> 
										 
									</div>
								    	</div>
              		</div>
			</div>
        </div>
    
    <jsp:include page="common/footerDemo.jsp"/>
   <script src="https://player.vimeo.com/api/player.js"></script>
 <script>
 var savedId;
 var iframe = document.querySelector('iframe');
 var player = new Vimeo.Player(iframe);
 var subTopicsScrolled= false;
 var containerElement = document.getElementById("topicContainer"); 
	if(containerElement != null){
		containerElement.addEventListener("wheel", (e)=>{
			subTopicsScrolled = true;
			$( "#autoScrollBtn" ).css( "display", "" );
		});
	}
	function viewCurrentElement() {
		var subTopicParentId = 'subTopicParent_Id_'+savedId		
		var element = document.getElementById(subTopicParentId);
		var containerElement = document.getElementById("topicContainer");
		if(element != null){
			containerElement.scrollTop =  element.offsetTop - (containerElement.getBoundingClientRect().height / 2);							
		}
		subTopicsScrolled = false;	
		$( "#autoScrollBtn" ).css( "display", "none" );
	}
    player.on('timeupdate', timeUpdate);
	 function timeUpdate(data) {

    	  // Function logic goes here	
    	// var x = window.matchMedia("(min-width: 992px)")	
		 //var fullScreen = false
			//if (x.matches) { 
				//fullScreen = true
			//}	
    	 player.getPaused().then(function(paused) {
				// `paused` indicates whether the player is paused
				if(paused === false){
				
					<%
	 				if(size > 0){
	 				int vcbId = 0;
						for(VideoContentAcadsBean vcb : videoSubTopicsList){%>
						var jsVcbId = <%=vcbId%>;
						
						var subTopicParentId = 'subTopicParent_Id_'+jsVcbId
						var subTopicChildId1 = 'subTopicChild1_Id_'+jsVcbId
						var subTopicChildId2 = 'subTopicChild2_Id_'+jsVcbId
						var subTopicChildId3 = 'subTopicChild3_Id_'+jsVcbId
						var element1 = document.getElementById(subTopicParentId);
						var element2 = document.getElementById(subTopicChildId1);
						var element3 = document.getElementById(subTopicChildId2);
						var element4 = document.getElementById(subTopicChildId3);
						var containerElement = document.getElementById("topicContainer");
						
							if (parseInt(data.seconds) >= <%= vcb.getStartTimeInSeconds() %> && parseInt(data.seconds) < <%= vcb.getEndTimeInSeconds()	%>) {
								
								if(<%= vcbId %>!== savedId ){
								
								savedId = <%=vcbId%>
							
								element1.style.backgroundColor = "#d5e7e7";
								element2.style.backgroundColor = "#d5e7e7";
								element3.style.backgroundColor = "#d5e7e7";
								element4.style.backgroundColor = "#d5e7e7";
									if(!subTopicsScrolled){
										//element1.scrollIntoView({ behavior: 'smooth', block: 'nearest', inline: 'start' }) 
										containerElement.scrollTop =  element1.offsetTop - (containerElement.getBoundingClientRect().height / 2);										
									}
								}
							}else{
							
								element1.style.backgroundColor = "#dfdfdb";
								element2.style.backgroundColor = "#dfdfdb";
								element3.style.backgroundColor = "#dfdfdb";
								element4.style.backgroundColor = "#dfdfdb";
							}			
							
					<%	
					vcbId++;}} %>
				}});	
    	  
    	};

   </script>
<script>
function setTimeDuration(startTime) {
	player.setCurrentTime(startTime)
	player.play()
   
}
function searchSubTopics(){	
	var search_subTopicsValue = document.getElementById('search_subTopicsID').value;
	var searchSubTopicsTitle = document.getElementsByClassName("searchSubTopicsTitle"); //searchSubTopicsTitle is an array
	var searchSubTopicsSpan = document.getElementsByClassName("searchSubTopicsSpan"); //searchSubTopicsSpan is an array	
	var searchSubTopicsStartTime = document.getElementsByClassName("searchSubTopicsStartTime"); //searchSubTopicsSpan is an array	
	var filter = ''	
	var searchSubTopicsTitleValue;
	var searchSubTopicsSpanValue;
	if(search_subTopicsValue){ filter = search_subTopicsValue.toUpperCase() }
		for(var i = 0; i < searchSubTopicsTitle.length; i++){	
		searchSubTopicsTitleValue = searchSubTopicsTitle[i].innerHTML;
		searchSubTopicsSpanValue =	searchSubTopicsStartTime[i].innerHTML;

		if (searchSubTopicsTitleValue.toUpperCase().indexOf(filter) > -1 || searchSubTopicsSpanValue.toUpperCase().indexOf(filter) > -1) {
			$( "#textMismatch" ).hide();
			$( "#topicContainer" ).show();
			searchSubTopicsSpan[i].style.display = "";
             	
		}else{
			searchSubTopicsSpan[i].style.display = "none";
			$('#textMismatch').html("<p class='text-muted fs-4'> We can't find the text you're looking for. </p>");
			$( "#textMismatch" ).show();
			$( "#topicContainer" ).hide();
		}
	}
}	

  </script>
    <script>
   
    var container = document.getElementById('embed_container');
    var video = document.getElementById('video');
    var ratio = 9/14.5; //this is why the 56.25% padding hack exists
   

    function resizer() {
        var width = parseInt(window.getComputedStyle(container)['width'], 10);
        var height = (width * ratio);
        
        video.style.width = width + 'px';
        video.style.height = height + 'px';
        video.style.marginTop = '-3.278%'; //~732px wide, the video border is about 24px thick (24/732)
        
        container.style.height = (height * 0.25) + 'px'; //0.88 was the magic number that you needed to shrink the height of the outer container with.
    }

    //attach event on resize
    window.addEventListener('resize', resizer, false);

    //call function for initial sizing
    //no need for padding hack since we are setting the height based off of the width * aspect ratio
    resizer();
    container.style.padding = 0;
   	</script>
 <script>
document.getElementById("videoDownloadLink").addEventListener("click", function(event){
	
	event.preventDefault()
}); 
</script> 

<script type="text/javascript">
$(document).ready(function(){

	console.log('Doc ready for download check ');
function get_filesize(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open("HEAD", url, true); // Notice "HEAD" instead of "GET",
                                 //  to get only the header
    xhr.onreadystatechange = function() {
        if (this.readyState == this.DONE) {
            callback(parseInt(xhr.getResponseHeader("Content-Length")));
        }
    };
    xhr.send();
}
<%-- 
	Commented the content server recordings path
	get_filesize("<spring:eval expression="@propertyConfigurer.getProperty('CONTENT_SERVER_RECORDINGS_PATH')" /><%=videoContent.getMeetingKey()%>.mp4", function(size) {
	console.log("size : "+size);
	if(!isNaN(size)){
		if(size > 5000000){
			document.getElementById("noticeSpan")
				.innerHTML="(Right Click on button and Click 'Save Link As') .";
		}else{

			document.getElementById("noticeSpan")
				.innerHTML="( Video is currently unavailable to download, The link will automatically be enabled as soon as video is available ) .";
			  var element = document.getElementById("videoDownloadLink");
			  element.classList.add("disableClick");
		}	
	}else{

		document.getElementById("noticeSpan")
			.innerHTML="( Video is currently unavailable to download, The link will automatically be enabled as soon as video is available ) .";
		  var element = document.getElementById("videoDownloadLink");
		  element.classList.add("disableClick");
	}
	
	
}); 
--%>

})
</script>

   <%-- Commented as not able to implement this logic
     <script>
    $(document).ready(function(){
    	
    <% 
    	if(size > 0){
		for(VideoContentBean vcb : videoSubTopicsList){
		String[] timeArray=	vcb.getStartTime().split(":",-1);
		String timeStamp="#t=";
		int count=0;
		for(String t : timeArray){
			if(count==0){
			timeStamp=timeStamp+t+"h";
			}
			if(count==1){
				timeStamp=timeStamp+t+"m";
			}
			if(count==2){
				timeStamp=timeStamp+t+"s";
			}
			count++;
		}
		String tempLink = videoContent.getVideoLink()+timeStamp;
		
	%>

	$(".<%=vcb.getId()%>").click(function (event) {
		$("#frame").attr("src","<%= tempLink%>");
		document.getElementById('frame').contentWindow.location.reload();
		console.log('Clicked Link ====>  <%= tempLink%>');
	});
	<%
		}}
	%>

    });
	</script>
     --%>
     
     <% 
     }catch(Exception pageError){
    	 
     }
     %>
   </body>
</html>