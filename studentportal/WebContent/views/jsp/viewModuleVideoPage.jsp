<!DOCTYPE html>
 <%@page import="com.nmims.beans.VideoContentStudentPortalBean"%>
 <%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html lang="en">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.1.6/css/swiper.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<jsp:include page="../views/jsp/common/jscss.jsp">
	<jsp:param value="Learning Resources" name="title" />
</jsp:include>
<style>

.animated{-webkit-animation-duration:1s;animation-duration:1s;-webkit-animation-fill-mode:both;animation-fill-mode:both}.animated.infinite{-webkit-animation-iteration-count:infinite;animation-iteration-count:infinite}.animated.hinge{-webkit-animation-duration:2s;animation-duration:2s
}@-webkit-keyframes zoomIn{0%{opacity:0;-webkit-transform:scale3d(.3,.3,.3);transform:scale3d(.3,.3,.3)}50%{opacity:1}}@keyframes zoomIn{0%{opacity:0;-webkit-transform:scale3d(.3,.3,.3);transform:scale3d(.3,.3,.3)}50%{opacity:1}}.zoomIn{-webkit-animation-name:zoomIn;animation-name:zoomIn}
@-webkit-keyframes zoomOut{0%{opacity:1}50%{opacity:0;-webkit-transform:scale3d(.3,.3,.3);transform:scale3d(.3,.3,.3)}100%{opacity:0}}@keyframes zoomOut{0%{opacity:1}50%{opacity:0;-webkit-transform:scale3d(.3,.3,.3);transform:scale3d(.3,.3,.3)}100%{opacity:0}}.zoomOut{-webkit-animation-name:zoomOut;animation-name:zoomOut}

#accordion .panel-title i.glyphicon{
    -moz-transition: -moz-transform 0.5s ease-in-out;
    -o-transition: -o-transform 0.5s ease-in-out;
    -webkit-transition: -webkit-transform 0.5s ease-in-out;
    transition: transform 0.5s ease-in-out;
}

.rotate-icon{
    -webkit-transform: rotate(-225deg);
    -moz-transform: rotate(-225deg);
    transform: rotate(-225deg);
}

.panel{
    border: 0px;
   
}
.panel-group .panel+.panel{
    margin-top: 0px;
}
.panel-group .panel{
    border-radius: 0px;
}
.panel-heading{
    border-radius: 0px;
    color: white;
    padding: 25px 15px;
}
.panel-custom>.panel-heading{
    background-color: #64B5F6;
}
.panel-group .panel:last-child{
    border-bottom: 5px solid #eef7f1;
}

panel-collapse .collapse.in{
    border-bottom:0;
}
.nav-pills>li.active>a, .nav-pills>li.active>a:focus, .nav-pills>li.active>a:hover {
   color: #fff1f1;
    background-color: #abaeb1;
}
@import url("https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css");
.panel-title > a:before {
    float: left !important;
    font-family: FontAwesome;
    content:"\f068";
    padding-right: 5px;
}
.panel-title > a.collapsed:before {
    float: left !important;
    content:"\f067";
}
.panel-title > a:hover, 
.panel-title > a:active, 
.panel-title > a:focus  {
    text-decoration:none;
}
</style>

<%
ArrayList<VideoContentStudentPortalBean> videoContentList =  (ArrayList<VideoContentStudentPortalBean>)session.getAttribute("VideoContentsList");
int noOfVideoContents = videoContentList != null ? videoContentList.size() : 0;
%>

<body>
	<div >
			
		
		<div >
			<div >
		<div >
		
						
					<div >
					
		
    <div class="row" style=" background-color:white;">
                 <div class="tab-content col-md-12" id="videoId">
                           <div class="">
                                 <div class="col-md-12 well giveBorder" style="margin: 0rem 0 1.5rem 0; padding:10px 10px;  ">
                                                        <div id="embed_container" class="embed-container">
                                                            <iframe height="600vh" width="100%"
                                                                src="${videoToBePlayed.videoLink}" id="video"
                                                                frameborder='0' webkitAllowFullScreen
                                                                mozallowfullscreen allowFullScreen></iframe>
                                                        </div>
                                            
                                                        <div class="col-xs-12">
                                                            <h2>${videoToBePlayed.fileName}</h2>
                                						</div>
                                						<div class="col-xs-12">
                                						<c:if test="${videoToBePlayed.type eq 'Main Video'}">
											    		<!-- 
											    		<a id="downloadVideo${videoToBePlayed.id}" href="${videoToBePlayed.mobileUrlHd}">Download</a>
											    			<span>(Right Click and Save Link As)</span>
											    		 -->
											    		</c:if>
                                						</div>
							    			
                                       </div>
                                       </div>
                                       </div>
                                       </div>
                                       </div>
                                       </div>
                                       </div>
                                       </div>
</body>

<script
  src="https://code.jquery.com/jquery-3.3.1.js"
  integrity="sha256-2Kok7MbOyxpgUVvAk/HJ2jigOSYS2auK4Pfzbm7uH60="
  crossorigin="anonymous"></script>

<c:if test="${videoToBePlayed.type eq 'Main Video'}">	
<script>
document.getElementById("downloadVideo"+${videoToBePlayed.id}).addEventListener("click", function(event){
	
	console.log('Clicked');
	event.preventDefault()
});
</script>
</c:if>

<script>
$(document).ready(function(){
    	
		 //Start 
			var videoDuration = '${videoToBePlayed.duration}'
			console.log('Got videoDuration'+videoDuration);
			var videoInSeconds=getTimeAsSeconds(videoDuration)
			console.log("video In Seconds  "+videoInSeconds);
			var type='${videoToBePlayed.type}';
			var timeout= videoInSeconds/2 *1000;
			if(isNaN(timeout)){
				timeout=60000;
		  		console.log('In IF timeout '+timeout);
		    }
	setTimeout(function () {   
		 $.ajax({url: '${SERVER_PATH}videoViewed?moduleId='+${moduleContentBean.id}+'&videoTopicId='+${videoToBePlayed.id}+'&type='+type,
		        		  success: function(result){
		        			  console.log(result);
		      		  		console.log('time out '+timeout);
		          },
		          error: function(error){
					  console.log(error);
		          }
		     });
	}, timeout);
		//End
	 
     
});

//call to convert hms to seconds
function getTimeAsSeconds(time){
    var timeArray = time.split(':');
    return Number(timeArray [0]) * 3600 + Number(timeArray [1]) * 60 + Number(timeArray[2]);
}
</script>


</html>