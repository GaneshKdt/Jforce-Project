<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.1.6/css/swiper.min.css">

  
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


</style>
<body>

	<%@ include file="../views/common/header.jsp"%>
	<div class="sz-main-content-wrapper">
		<jsp:include page="../views/common/breadcrum.jsp">
			<jsp:param
				value="Student Zone;Learning Module;Business Communication and Etiquette"
				name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../views/common/left-sidebar.jsp">
					<jsp:param value="Learning Module" name="activeMenu" />
				</jsp:include>
		<div class="sz-content-wrapper examsPage">
					<%@ include file="../views/common/studentInfoBar.jsp"%>
					<div class="sz-content">
						<div class="clearfix"></div>
						<%@ include file="../views/common/messages.jsp"%>
								<div class="col-md-6"><h2>${moduleContentBean.subject} > ${moduleContentBean.moduleName}</h2></div>
								<div class="clearfix"></div>
	


    <div class="row" style="background-color:white;">
                <div class="panel-group col-md-3" id="accordion" role="tablist" aria-multiselectable="true">
                    <div class="panel panel-custom" style="margin-bottom:10px;">
                        <div class="panel-heading" role="tab" id="headingOne" >
                            <h4 class="panel-title">
                                <a data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                    <i class="glyphicon glyphicon-plus"></i>
                                    Modules
                                </a>
                            </h4>
                        </div>
                        <div id="collapseOne" class="panel-collapse collapse " role="tabpanel" aria-labelledby="headingOne" >
                            <div class="panel-body animated zoomIn">
                                    <ul class="nav nav-pills nav-stacked ">
                                            <c:forEach var="document" items="${moduleDocumnentList}" varStatus="dStatus">
                  <li class="moduleLi"><a href="#tab_a${document.id}" data-toggle="pill">${document.documentName}</a></li>
                  </c:forEach>
                     </ul>
                            </div>
                        </div>
                    </div>
                    <div class="panel panel-custom">
                        <div class="panel-heading" role="tab" id="headingTwo" >
                            <h4 class="panel-title">
                                <a class="collapsed" data-toggle="collapse" data-parent="#accordion" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
                                    <i class="glyphicon glyphicon-plus"></i>
                                    Videos
                                </a>
                            </h4>
                        </div>
                        <div id="collapseTwo" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingTwo">
                            <div class="panel-body animated zoomIn">
                                    <ul class="nav nav-pills nav-stacked ">
                                            <!-- <c:forEach var="document" items="${moduleDocumnentList}" varStatus="dStatus"> -->
                  <li class="videoLi"><a href="#tab_a${document.id}" data-toggle="pill">${videoToBePlayed.fileName}</a></li>
                  <!-- </c:forEach> -->
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
                                                                                        <div class="swiper-slide">
                                                                                            <div class="slides_content">
                                                                                                <i class=" glyphicon glyphicon-zoom-in"
                                                                                                    id="imgSmile"></i> <a
                                                                                                    href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_ACADS_STATIC_RESOURCES')" />assets/images/first_1.png" download>Download</a>
                                                                                                <img class="bg_img"
                                                                                                    src="/acads/viewPdfImage/${document.folderPath}" 
                                                                                                    alt="Image of Topic" />
                                                                                            </div>
                                                                                        </div>
                                                                                        <div class="swiper-slide">
                                                                                            <img data-src="http://test-studentzone-ngasce.nmims.edu/lr/business_economics/12/module_12_doc_15_page_${imageCount}.png"
                                                                                             data-srcset="http://test-studentzone-ngasce.nmims.edu/lr/business_economics/12/module_12_doc_15_page_${imageCount}.png" 
                                                                                             class="swiper-lazy img-responsive">
                                                                                            <div class="swiper-lazy-preloader"></div>
                                                                                        </div>
                                                                                    </c:forEach>
                                                                                </div>
                                                                                <div class="swiper-pagination"></div>
                                                                                <div class="swiper-button-prev"></div>
                                                                                <div class="swiper-button-next"></div>
                                                                                <div class="swiper-scrollbar"></div>
                                                                            </div>
                              
                            </div>
                           </c:forEach>
                          </div>
                         
                              <div class="tab-content col-md-8" id="videoId">
                              
                                        <div class="panel-group" id="accordionVideo">
                                            <div class="panel panel-default">
                                             
                                                <div id="collapseOne" class="panel-collapse collapse in">
                                                    <div class="panel-body"
                                                        style="margin-left: 8%; margin-right: 8%;">
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
                                                         <i class="fa fa-play-circle-o" style="padding:5px 15px 5px 5px; margin:auto 0px !important; font-size: 30px !important; " aria-hidden="true"></i>
                                                         </a>
                                                     </div>
                                                     <div class="col-xs-7 giveBorder" style="background-color:#dfdfdb; padding:10px 10px;">
                                                         <a class="" href="/acads/student/viewVideoModuleTopic?moduleId=${moduleContentBean.id}&videoSubtopicId=${relatedVideo.id} " style="font-size:15px; color:black; padding-top:25px; margin:auto 0px !important;" aria-hidden="true"></i>
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
		$(function() {
        
function toggleChevron(e) {
			$(e.target)
					.prev('.panel-heading')
					.find("i")
					.toggleClass('rotate-icon');
			$('.panel-body.animated').toggleClass('zoomIn zoomOut');
		}
		
		$('#accordion').on('hide.bs.collapse', toggleChevron);
		$('#accordion').on('show.bs.collapse', toggleChevron);
		})
		
		
		
		</script>
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.1.6/js/swiper.min.js"></script>

<script>
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
	})
</script>
<script>
	$(document).ready(
			function() {     
        $('#moduleId').hide();  
        $('.videoLi').on('click', function() {
                 $('#moduleId').hide();
                 $('#videoId').show();
                 });  


				$("#myTab a").click(function(e) {
					e.preventDefault();
					$(this).tab('show');
				});
				

				var myColors = [ 'red', 'blue', 'yellow', 'green', 'purple',
						'orange' ];
				var i = 0;

				$('div#myDiv').each(function() {
					$(this).css('border-left-color', myColors[i]);
					i = (i + 1) % myColors.length;
				});
				
				

			});
</script>
   
<script>
   
    var container = document.getElementById('embed_container');
    var video = document.getElementById('video');
    var ratio = 9/14.5;
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
</html>