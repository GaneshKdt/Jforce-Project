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
    
    <jsp:include page="common/jscss.jsp">
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
.vp-player-layout {
	top: 0px !important;
}

.giveBorder1 {
	border: 2px solid black;
}

.noPaddingNOMargin {
	padding: 0px !important;
	margin: 0px !important;
}

.video {
	width: 100%;
	height: 600px;
}

.videoThumbnail {
	width: 100%;
}

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
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	font-size: 70px !important;
	color: #00000087 !important;
}

.embed-container {
	position: relative;
	height: 0;
	overflow: hidden;
	width: 100%;
	min-height: 610px !important;
}

.embed-container iframe, .embed-container object, .embed-container embed
	{
	width: 100%;
}
#topicContainer{
	overflow:scroll; 
	height:650px; !important;
}
#topicContainer::-webkit-scrollbar {
    width: 12px;
}
#topicContainer::-webkit-scrollbar-thumb{
    background-color:#d2232a;
    border-radius:15px;
}
#searchInput{
	color: black; 
	height: 30px; 
	width: 100%; 
	margin: 1em 0.25em; 
	padding: 2px 15px;
}

.disableClick{
	pointer-events: none;
	cursor: default;
}
.searchSubTopics{
	width:80%;
	margin-left: 8px;
}

/*  Media Queries Start */

	@media screen and (max-width: 699px) {
  		.embed-container {
			min-height: 250px !important;
		}
   	
   		#topicContainer{
			height:350px; !important;
		}
		
		
	}
   	@media screen and (min-width: 700px) and (max-width: 1029px) {
  		.embed-container {
			min-height: 350px !important;
		}
		#topicContainer{
			height:450px; !important;
		}
		
   	}
   	
   @media screen and (min-width: 990px) and (max-width: 1350px) {  	
	.searchSubTopics{
		width:68%;
	}
   	}

/*  Media Queries End */
#topicContainer{
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

.disable{
	pointer-events: none;
	color: gray;
	cursor: default;
}

</style>
<body>
   <% try{ 
   %> 
   
   
    	<%@ include file="common/header.jsp" %>
    	
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
              						
              						<div class="sz-content giveBorder">
              						
										 <div class=" giveBorder" >
											
											<div class="row giveBorder">
												<%
												if(videoContent !=null){
												%>
												<div class="col-md-8 giveBorder" style="padding-bottom:10px;padding-top:12px;">
													<div class="col-xs-12 giveBorder" style="background-color:#F2F2F2; ">
														<form action="/acads/student/searchVideos" method="post">
													<div class="row giveBorder">
													
													<div class="col-xs-10 giveBorder ">
															<div class="form-group">
																	<input type="text" class="form-control" id="searchInput" name="searchItem" placeholder="Search Videos"  style="" required>
																	
																	
															</div>
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
													<div class="col-xs-2 giveBorder ">
														<div class=" form-group ">
																	<button type="submit" style="width:100%; height:30px !important;">
																	<span><i class="fa-solid fa-magnifying-glass" style="font-size:15px;"></i></span>
																	</button>															
														</div>
													</div>
													</div>
														</form>
													</div>
										 			
													 <div class="col-md-12 giveBorder" style="background-color:inherit; overflow:hidden;">
														 <%-- <iframe id="frame" src="<%= videoContent.getVideoLink() %>" class="video" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>
														  --%>
 															<%-- <div id="embed_container" class='embed-container'>
														      <iframe height="770" src='<%= videoContent.getVideoLink() %>' allow="autoplay" id="video" frameborder='0'
														      webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>
														   </div> --%>
														   
														   <div style="padding:56.25% 0 0 0;position:relative;">
														      <iframe src='<%= videoContent.getVideoLink() %>' allow="autoplay" frameborder='0'
														      webkitAllowFullScreen mozallowfullscreen allowFullScreen
														      style="position:absolute;top:0;left:0;width:100%;height:100%;"></iframe>
														   </div>
														 	
													</div> 
										 			<div class="col-md-12 giveBorder">
														<div class="col-md-8 giveBorder" style="padding-left:0px !important;">
														<h2 style="margin-top:5px !important;"> <%= videoContent.getFileName() %> </h2>
										 				</div>
										 				<div class="col-md-4 giveBorder">
														<%-- <h3 style="margin: 1.5rem 0 1rem 0;"> Views : <%= videoContent.getViewCount() %> </h3>
										 				 --%>
										 				 </div>
										 			</div>
										 			<div class="col-md-12 giveBorder">
														<h4> Description </h4>
														<p> <%= videoContent.getDescription() %> </p>
										 			</div>	
										 						<%
																	if(videoContent.getFacultyName() != null){
																%>
																<div class="col-md-12" style="">
																	<h5>Session By : <b><%= videoContent.getFacultyName() %> </b></h5>
																</div> 
																<%
																	}
																%>
																<%
																	if(videoContent.getSessionDate() != null && !" ".equals(videoContent.getSessionDate())){
																%>
																<div class="col-md-12" style="">
																	<h5> Session Date :  <%= videoContent.getSessionDate() %> </h5>
																</div>
																<%
																	}
																%> 
																
																<%
																	if(videoContent.getTrack() != null && !" ".equals(videoContent.getTrack())){
																%>
																<div class="col-md-12" style="">
																	<h5> Track/Group :  <%= videoContent.getTrack() %> </h5>
																</div>
																<%
																	}
																%> 
										 			
																<div class="col-md-12" style="">
																	<h5> Academic Cycle :  <%= videoContent.getMonth() %> &nbsp; <b><%= videoContent.getYear() %> </b></h5>
																</div>
																<%
																	if(videoContent.getMobileUrlHd() != null && !"".equals(videoContent.getMobileUrlHd())){
																		if( !"Enterprise Guide".equals(videoContent.getSubject()) && !"Enterprise Miner".equals(videoContent.getSubject()) && !"Visual Analytics".equals(videoContent.getSubject()) ){
																		 
																%>
																<div class="col-md-12" style="">
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
										 		<div class="col-md-4 well giveBorder" style="margin: 0rem 0 1.5rem 0; padding:10px 10px;  ">
										 			<div class="row giveBorder" style="text-align:center; padding:5px 5px; ">
										 				<span style="padding:10px; font-size:18px; ">
										 					<b> Transcript </b></span>
										 					</div> 
					<div>
					<input placeholder="Search by any text/duration in the transcript"
					style="display:inline-block;" id="search_subTopicsID"
					 type="text" class="form-control searchSubTopics" onchange="searchSubTopics()">
					<button class="btn btn-primary" style="background-color:#404041;" onclick="searchSubTopics()" ><span><i class="fa-solid fa-magnifying-glass" style="font-size:15px;"></i></span></button>
					</div>
										 			
										 				
										 			<div id="topicContainer" style="">	 
										 			<%
										 				if(size > 0){
										 					int vcbDivId = 0;
															for(VideoContentAcadsBean vcb : videoSubTopicsList){
										 			%>
										 			<div id="subTopicParent_Id_<%=vcbDivId %>" onclick="setTimeDuration('<%= vcb.getStartTimeInSeconds() %>')" class="row giveBorder searchSubTopicsSpan" style=" padding:0px 0px; margin:5px 5px !important;  background-color:#dfdfdb;cursor:pointer">
										 	
										 				<div id="subTopicChild1_Id_<%=vcbDivId %>" class="col-xs-2 giveBorder" style="text-align:center; background-color:#dfdfdb; padding:10px 10px;">
										 					<a class="">
										 					 <!-- href="/acads/student/watchVideoTopic?id=<=vcb.getId()%>" --> 										 					 
										 					<i class="fa-solid fa-magnifying-glass" style="padding:5px 15px 5px 5px; margin:auto 0px !important; font-size: 30px !important; " aria-hidden="true"></i>
										 					</a>
										 				</div>
										 				<div  id="subTopicChild2_Id_<%=vcbDivId %>" class="col-xs-7 giveBorder" style="background-color:#dfdfdb; padding:10px 10px;">
										 					<a class="searchSubTopicsTitle" style="font-size:15px; color:black; padding-top:25px; margin:auto 0px !important;" aria-hidden="true"></i>
										 						<%= vcb.getFileName()  %>
										 					</a>
										 				</div>
										 				<div  id="subTopicChild3_Id_<%=vcbDivId %>" class="col-xs-3 giveBorder" style="font-size:14px; text-align:center; background-color:#dfdfdb; padding:20px 10px 0px 10px;">
										 					<%
										 						String[] timeArray = vcb.getDuration().split(":",-1);
										 						if("0".equals(timeArray[0])){
										 							vcb.setDuration(timeArray[1]+":"+timeArray[2]);
										 							
										 						}
										 					%>
										 					<span class="searchSubTopicsStartTime"><b><%= vcb.getStartTime() %> </b></span>
										 				</div>
										 			</div> 
										 			<%
										 				vcbDivId++;	}}
											 		%> 
											 		<div id="autoScrollBtn" style="bottom:20px;position: absolute;display:none;text-align:center; padding:5px 5px;width:100%">
										 					<button   onclick="viewCurrentElement()" className="btn btn-primary btn-md btn-responsive animated animatedFadeInUp fadeInUp fadeInDown">	
										 					<span><i class="fa-solid fa-arrow-up" style="font-size:15px;"></i> </span>
										 				
										 					 Resume Transcript Auto-Scroll</button>
										 					</div> 
											 
											 		</div> 
										 		</div>
										 	</div>
										 	<div class="container-fluid"> 
													<%@ include file="queries.jsp" %> 
													
											</div>
													<div class="col-md-12 giveBorder" style="text-align:center; background-color:#dfdfdb; padding:10px 10px; display:<%= hide %>">
										 				<div class="row giveBorder" style=" padding:5px 5px; ">
										 				<span style="padding:10px; font-size:18px; "><b>Related Videos</b></span>
										 				</div>
										 				<%
										 				if(videoContent !=null){
															for(VideoContentAcadsBean vcb : relatedVideos){
										 				%>
										 				<div class="col-md-4 giveBorder">
																
										 				<div class="row giveBorder" style="margin: 1.5rem 0 1rem 0; padding:5px 5px; border-bottom: 1px dotted black">
										 					<div class="col-md-12 thumbnailDiv giveBorder ">

																<a class="" href="/acads/student/watchVideos?id=<%= vcb.getId() %>&pssId=<%=vcb.getProgramSemSubjectId() %>"  title="<%= vcb.getFileName() %>" style=""> 

																	<img src="<%= vcb.getThumbnailUrl()%>" alt="Video Thumbnail" class="img-responsive videoThumbnail">
																	<i class="fa fa-play-circle-o playIconStyle" style="" aria-hidden="true"></i>
										 						</a>
											 				</div>	
											 				<div class="col-md-12 giveBorder" >
																<h4 >

																	<a href="/acads/student/watchVideos?id=<%= vcb.getId() %>&pssId=<%=vcb.getProgramSemSubjectId() %>" title="<%= vcb.getFileName()%>">

											 							 <b> <%= vcb.getFileName() %> </b>
											 						</a>
											 					</h4>
																
																<h5> <%= vcb.getSubject() %> </h5>
																
										 			
																<div class="col-md-12" style="">
																	<h5> Academic Cycle :  <%= vcb.getMonth() %> &nbsp; <b><%= vcb.getYear() %> </b></h5>
																</div>  
											 				</div>	
											 			</div>
											 			</div>
											 			<%
										 					}}
											 			%>
										 			</div>
										 			
										 </div> 
										 
									</div >
								    	</div>
              		</div>
			</div>
        </div>
    
    <jsp:include page="common/footer.jsp"/>
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
			document.getElementById("autoScrollBtn").style.display = "";

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
		document.getElementById("autoScrollBtn").style.display = "none";
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
			searchSubTopicsSpan[i].style.display = "";
		}else{
			searchSubTopicsSpan[i].style.display = "none";
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
    	console.log('embed_container height--> '+container.style.height);
    }

    //attach event on resize
    window.addEventListener('resize', resizer, false);

    //call function for initial sizing
    //no need for padding hack since we are setting the height based off of the width * aspect ratio
    resizer();
    container.style.padding = 0;
   	</script>
 <script>
	console.log('at addEventListener');
document.getElementById("videoDownloadLink").addEventListener("click", function(event){
	
	console.log('Clicked');
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
        	console.log("xhr >>>");
            console.log(xhr);
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