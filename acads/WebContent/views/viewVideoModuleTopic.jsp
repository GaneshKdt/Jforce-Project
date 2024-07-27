<!DOCTYPE html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html lang="en">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.1.6/css/swiper.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
  
<jsp:include page="../views/common/jscss.jsp">
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


<body>

	<%@ include file="../views/common/header.jsp"%>
	<div class="sz-main-content-wrapper">
			<div class="sz-breadcrumb-wrapper">
			    <div class="container-fluid">
			        <ul class="sz-breadcrumbs">
			        		<li><a href="/studentportal/home">Student Zone</a></li>
			        		<li><a href="/acads/student/learningModule">Learning Module</a></li>
			        		<li><a href="/acads/student/learningModule">${moduleContentBean.subject}</a></li>
			        		<li><a href="#">${moduleContentBean.moduleName}</a></li>
			        </ul>
			        <ul class="sz-social-icons">
			            <li><a href="https://www.facebook.com/NMIMSSCE" class="icon-facebook" target="_blank"></a></li>
			            <li><a href="https://twitter.com/NMIMS_SCE" class="icon-twitter" target="_blank"></a></li>
			        </ul>
			    </div>
			</div>
		
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../views/common/left-sidebar.jsp">
					<jsp:param value="Learning Module" name="activeMenu" />
				</jsp:include>
		<div class="sz-content-wrapper examsPage">
		
					<%@ include file="../views/common/studentInfoBar.jsp"%>
						
					<div class="sz-content">
					
						<%@ include file="../views/common/messages.jsp"%>
						
    <div class="row" style=" background-color:white;">
    
    
                <div class="panel-group col-md-3" id="accordion"  style="padding-top:25px;" role="tablist" aria-multiselectable="true">
                 <a href="/acads/student/learningModule" style="padding-bottom:10px;"> 
    <i class="fa-solid fa-reply" ><span style="margin-left:5px;">Back</span></i></a>
                    <div class="panel panel-custom" style="margin-bottom:10px;margin-top:10px;">
                        <div class="panel-heading" role="tab" id="headingOne" style="border-radius:5px;">
                            <h4 class="panel-title">
                                <a class="collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                   Documents
                                </a>
                                
                                <div class="row" style="float:right;margin-top:-10%;width:60%;">
 														<div class="col-sm-10">
 															<h5 style="color:#fff;font-weight:normal;text-transform: capitalize;"> Progress : 
 															<b>${moduleContentBean.percentComplete} %</b></h5>
 														
 														
 														</div>
 												
 														<%-- <div class="col-sm-4">
 														
 															<h5 style="color:#fff;"><b> ${moduleContentBean.percentComplete} %</b></h5>
 															
 														</div> --%>
 													
 														<div class="col-sm-12">
 															<div class="col-sm-12" style="height:5px; background-color:white; width:100%; border:10px; padding:0px 0px; ">
 																<div class="col-sm-12" style="height:5px; background-color:#9fa1ab; width:${moduleContentBean.percentComplete}%; border:10px; padding:0px 5px;">
 																	
 																</div>
 															</div>
 														</div>
 														
 														</div> 
                            </h4>
                            
                           
                        </div>
                        <div id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne" >
                            <div class="panel-body animated zoomIn">
                                    <ul class="nav nav-pills nav-stacked ">
                                            <c:forEach var="document" items="${moduleDocumnentList}" varStatus="dStatus">
                  <li class="moduleLi" style="height:35px;"><a href="#tab_a${document.id}" onClick="goToDocumentDetail(${document.id})"  data-toggle="pill">${document.documentName}</a>
                  <a href="${SERVER_PATH}${document.previewPath}" target="_blank"
                  style="margin:0px;padding:0px;width:21px;bottom:30px;left:80%;"><i class="fa-solid fa-arrow-up-right-from-square" style="font-size:18px;"></i></a>
                  <a href="/acads/student/downloadDocument?filePath=${document.documentPath}" download
                  style="margin:0px;padding:0px;width:21px;bottom:50px;left:90%;" ><i class="fa-solid fa-download large" style="font-size:20px;"></i></a>
                  
                  
                  </li>
                
                
                  </c:forEach>
                     </ul>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-custom">
                        <div class="panel-heading" role="tab" id="headingTwo" style="border-radius:5px;">
                            <h4 class="panel-title">
                                <a class="collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="true" aria-controls="collapseTwo">
                                 
                                    Videos
                                </a>
                                <div class="row" style="float:right;margin-top:-10%;width:60%;">
 														
 														<div class="col-sm-10">
 															<h5 style="color:white;font-weight:normal;text-transform: capitalize;"> Progress:
 															<b> ${moduleContentBean.videoPercentage} %</b> </h5>
 														</div>
 												
 														<%-- <div class="col-sm-4">
 														
 															<h5 style="color:#fff;"><b> ${moduleContentBean.videoPercentage} %</b></h5>
 															
 														</div> --%>
 													
 														<div class="col-sm-12">
 															<div class="col-sm-12" style="height:5px; background-color:white; width:100%; border:10px; padding:0px 0px; ">
 																<div class="col-sm-12" style="height:5px; background-color:#9fa1ab; width:${moduleContentBean.videoPercentage}%; border:10px; padding:0px 5px;">
 																	
 																</div>
 															</div>
 														</div>
 														
 														</div> 
                            </h4>
                        </div>
                        <div id="collapseTwo" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingTwo">
                            <div class="panel-body animated zoomIn" >
                                    <ul class="nav nav-pills nav-stacked ">
                  <li class="videoLi" style="height:35px;"><a href="#tab_a${document.id}" id="videoClicked"  data-toggle="pill">${videoToBePlayed.fileName}</a></li>
                     </ul>
                            </div>
                        </div>
                    </div>
                    
                </div>
    
                <div class="tab-content col-md-8" id="moduleId">
                        <c:forEach var="document" items="${moduleDocumnentList}" varStatus="dStatus">
                            <div id="tab_a${document.id }" class="tab-pane fade in active">
                              <div class="swiper-container">
                           
                                                           <div class="swiper-wrapper">
                                                                                    <c:forEach var="imageCount" begin="1" end="${document.noOfPages}">
                                                                                        <div class="swiper-slide"  style="height:80%; width:80%;">
                             
                                                                                            <img data-src="${SERVER_PATH}${document.folderPath}${document.id }_page_${imageCount}.png" 
                                                                                             data-srcset="${SERVER_PATH}${document.folderPath}${document.id }_page_${imageCount}.png" 
                                                                                             class="swiper-lazy img-responsive" id="sky" >
                                                                                             <div style="top:50% !important" class="swiper-lazy-preloader"></div>

                                                                                        </div>
                                                                                    </c:forEach>
                                                                                </div>
                                                                                <div class="swiper-pagination"></div>
                                                                                <div class="swiper-button-prev" style="top:20% !important"></div>
                                                                                <div class="swiper-button-next" style="top:20% !important"></div>
                                                                                <div class="swiper-scrollbar"></div>
                                                                            </div>
                              
                            </div>
                           </c:forEach>
                          </div>

                      
                              <div class="tab-content col-md-8" id="videoId">
                                        <div class="panel-group" id="accordionVideo">
                                            <div class="panel panel-default">
                                                <div id="collapseTwo" class="panel-collapse collapse in">
                                                    <div class="panel-body"
                                                       style="margin-left: 0; margin-right: -12%;">
                                                        <div class="one">
                                                            
                                                        </div>
                                                        <div class="row">
                                                        <div class="col-md-8 well giveBorder" style="margin: 0rem 0 1.5rem 0; padding:10px 10px;  ">
                                             
                                                        <div id="embed_container" class="embed-container">
                                                            <iframe height="450vh" width="100%"
                                                                src="${videoToBePlayed.videoLink}" id="video"
                                                                frameborder='0' webkitAllowFullScreen
                                                                mozallowfullscreen allowFullScreen></iframe>
                                                        </div>
                                                        
                                                        <div class="col-xs-12">
                                                            <h2>${videoToBePlayed.fileName}</h2>
                                                        </div>
                                                        </div>
                                             <div class="col-md-4 well giveBorder" style="margin: 0rem 0 1.5rem 0; padding:10px 10px;  ">
                                                 <div class="row giveBorder" style="text-align:center; padding:5px 5px; ">
                                                     <span style="padding:10px; font-size:18px; ">
                                                         <b> Topics </b></span>
                                                 </div> 
                                                 <div id="topicContainer" >	 
                                                 <c:forEach var="relatedVideo" items="${relatedTopics}">
                                                 <div class="row giveBorder" style=" padding:0px 0px; margin:5px 5px !important;  background-color:#dfdfdb;">
                                                     
                                                     <div class="col-xs-2 giveBorder" style="text-align:center; background-color:#dfdfdb; padding:10px 10px;">
                                                         <a class="" href="/acads/student/viewVideoModuleTopic?moduleId=${moduleContentBean.id}&videoSubtopicId=${relatedVideo.id} " >
                                                         <i class="fa-regular fa-play-circle" style="padding:5px 15px 5px 5px; margin:auto 0px !important; font-size: 30px !important; " aria-hidden="true"></i>
                                                         </a>
                                                     </div>
                                                     <div class="col-xs-7 giveBorder" style="background-color:#dfdfdb; padding:10px 10px;">
                                                         <a class="" href="/acads/student/iewVideoModuleTopic?moduleId=${moduleContentBean.id}&videoSubtopicId=${relatedVideo.id} " style="font-size:15px; color:black; padding-top:25px; margin:auto 0px !important;" aria-hidden="true"></i>
                                                             ${relatedVideo.fileName}
                                                         </a>
                                                     </div> 
                                                 </div> 
                                                 </c:forEach>
                                                 </div> 
                                             </div>
                                             </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                    </div>


                                </div>
                            </div>

                        </div>
                    </div> 
            </div>

        </div>
 
<jsp:include page="../views/common/footer.jsp" />
</body>

 <script>
            var selectIds = $('#collapseOne,#collapseTwo');
$(function ($) {
    selectIds.on('show.bs.collapse hidden.bs.collapse', function () {
        $(this).prev().find('.glyphicon').toggleClass('glyphicon-plus glyphicon-minus');
    })
});
            
            </script>
<script>

$(document).ready(function(){
    $("#videoClicked").click(function(){
      
      //alert("link clicked..."); 
		 //Start 
			var videoDuration = '${videoToBePlayed.duration}'
			console.log('Got videoDuration'+videoDuration);
			var videoInSeconds=getTimeAsSeconds(videoDuration)
			videoInSeconds = videoInSeconds*1000;
			console.log("video In Seconds  "+videoInSeconds);

			console.log('time out '+videoInSeconds/2);
			console.log('Before '+$.now());
			setTimeout(function(){ 
				//alert("Hello");
				//ajax start
				$.ajax({url: '${SERVER_PATH}acads/student/videoViewed?moduleId='+${moduleContentBean.id}+'&videoTopicId='+${videoToBePlayed.id},
		        		  success: function(result){
		        			  console.log(result);
		        			  console.log('After ajax '+$.now());
		          },
		          error: function(error){
					  console.log(error);
		          }
		     	});
				//ajax end
			}, videoInSeconds/2);
			//alert("link clicked..."++videoInSeconds/2); 
			  
		//End
	 
      
    });
    //call to convert hms to seconds
    function getTimeAsSeconds(time){
        var timeArray = time.split(':');
        console.log('timeArray [0] '+timeArray [0]);
        console.log('timeArray [1] '+timeArray [1]);
        console.log('timeArray [2] '+timeArray [2]);
		 return Number(timeArray [0]) * 3600 + Number(timeArray [1]) * 60 + Number(timeArray[2]);
    }
    
    
    function zoomin(){
        var myImg = document.getElementById("sky");
        var currWidth = myImg.clientWidth;
        if(currWidth == 500){
            alert("Maximum zoom-in level reached.");
        } else{
            myImg.style.width = (currWidth + 50) + "px";
        } 
    }
    function zoomout(){
        var myImg = document.getElementById("sky");
        var currWidth = myImg.clientWidth;
        if(currWidth == 50){
            alert("Maximum zoom-out level reached.");
        } else{
            myImg.style.width = (currWidth - 50) + "px";
        }
    }
    
});

</script>
		
<script src="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.1.6/js/swiper.min.js"></script>
<script>
$(document).ready(function(){
	var img = document.images[0];
	console.log('img' + img);
	img.onclick = function() {
		// atob to base64_decode the data-URI
		var image_data = atob(img.src.split(',')[1]);
		// Use typed arrays to convert the binary data to a Blob
		var arraybuffer = new ArrayBuffer(image_data.length);
		var view = new Uint8Array(arraybuffer);
		for (var i = 0; i < image_data.length; i++) {
			view[i] = image_data.charCodeAt(i) & 0xff;
		}
		try {
			// This is the recommended method:
			var blob = new Blob([ arraybuffer ], {
				type : 'application/octet-stream'
			});
		} catch (e) {
			// The BlobBuilder API has been deprecated in favour of Blob, but older
			// browsers don't know about the Blob constructor
			// IE10 also supports BlobBuilder, but since the `Blob` constructor
			//  also works, there's no need to add `MSBlobBuilder`.
			var bb = new (window.WebKitBlobBuilder || window.MozBlobBuilder);
			bb.append(arraybuffer);
			var blob = bb.getBlob('application/octet-stream'); // <-- Here's the Blob
		}

		// Use the URL object to create a temporary URL
		var url = (window.webkitURL || window.URL).createObjectURL(blob);
		location.href = url; // <-- Download!
	}; 
	
	var globalModuleId=${moduleContentBean.id};
	var globalDocumentId='';
	var timeout='';
	var mySwiper = new Swiper('.swiper-container', {
		
	    // Disable preloading of all images
	    preloadImages: false,
	    // Enable lazy loading
	    lazy: true,
		// Optional parameters
		direction : 'horizontal',
		loop : true,

		// If we need pagination
		pagination : {
			el : '.swiper-pagination',
			type : 'fraction',
		},

		// Navigation arrows
		navigation : {
			nextEl : '.swiper-button-next',
			prevEl : '.swiper-button-prev',
		},

		// And if we need scrollbar
		scrollbar : {
			el : '.swiper-scrollbar',
		}, 
        on: {
        	slideNextTransitionEnd: function () {
              console.log('slider changed' + this.activeIndex);
              console.log('Ajax call for ModuleID : '+globalModuleId+' DocumentId : '+globalDocumentId+' ViewPage : ' + this.previousIndex);
              console.log('timeout: '+timeout);
            
            	  
				$.ajax({url: '${SERVER_PATH}acads/pageViewed?moduleId='+globalModuleId+'&documentId='+globalDocumentId+'&pageNo='+this.previousIndex,
					  error: function(error){
						  console.log(error);
			          },
		        		  success: function(result){
		        			  console.log(result);
		          }, 
		     	} );
         
            },
          },
		}); 
	function goToDocumentDetail(documentId){
		console.log('Clicked Link With documentId'+documentId); 
		globalDocumentId = documentId;
	}
	
});
</script>
<script>
	$(document).ready(
			function() {
				
				
				$('#videoId').show();
				 $('#moduleId').hide();
                $('.moduleLi').on('click', function() {
                    $('#videoId').hide();
                $('#moduleId').show();
});       

$('.videoLi').on('click', function() {
    $('#moduleId').hide();
                $('#videoId').show();
                });  
				$("#myTab a").click(function(e) {
					e.preventDefault();
					$(this).tab('show');
				});
			

				
			});
</script>

</html>