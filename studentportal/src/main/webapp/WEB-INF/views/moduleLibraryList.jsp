<!DOCTYPE html>


<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html lang="en">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/Swiper/4.1.6/css/swiper.min.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

<jsp:include page="../views/common/jscss.jsp">
	<jsp:param value="Learning Resources" name="title" />
</jsp:include>
<style>
.animated {
	-webkit-animation-duration: 1s;
	animation-duration: 1s;
	-webkit-animation-fill-mode: both;
	animation-fill-mode: both
}

.animated.infinite {
	-webkit-animation-iteration-count: infinite;
	animation-iteration-count: infinite
}

.animated.hinge {
	-webkit-animation-duration: 2s;
	animation-duration: 2s
}

@
-webkit-keyframes zoomIn { 0%{
	opacity: 0;
	-webkit-transform: scale3d(.3, .3, .3);
	transform: scale3d(.3, .3, .3)
}

50%{
opacity
:
1
}
}
@
keyframes zoomIn { 0%{
	opacity: 0;
	-webkit-transform: scale3d(.3, .3, .3);
	transform: scale3d(.3, .3, .3)
}

50%{
opacity
:
1
}
}
.zoomIn {
	-webkit-animation-name: zoomIn;
	animation-name: zoomIn
}

@
-webkit-keyframes zoomOut { 0%{
	opacity: 1
}

50%{
opacity
:
0;-webkit-transform
:scale3d
(
.3
,
.3
,
.3
);transform
:scale3d
(
.3
,
.3
,
.3
)
}
100%{
opacity
:
0
}
}
@
keyframes zoomOut { 0%{
	opacity: 1
}

50%{
opacity
:
0;-webkit-transform
:scale3d
(
.3
,
.3
,
.3
);transform
:scale3d
(
.3
,
.3
,
.3
)
}
100%{
opacity
:
0
}
}
.zoomOut {
	-webkit-animation-name: zoomOut;
	animation-name: zoomOut
}

#accordion .panel-title i.glyphicon {
	-moz-transition: -moz-transform 0.5s ease-in-out;
	-o-transition: -o-transform 0.5s ease-in-out;
	-webkit-transition: -webkit-transform 0.5s ease-in-out;
	transition: transform 0.5s ease-in-out;
}

.rotate-icon {
	-webkit-transform: rotate(-225deg);
	-moz-transform: rotate(-225deg);
	transform: rotate(-225deg);
}

.panel {
	border: 0px;
}

.panel-group .panel+.panel {
	margin-top: 0px;
}

.panel-group .panel {
	border-radius: 0px;
}

.panel-heading {
	border-radius: 0px;
	color: white;
	padding: 25px 15px;
}

.panel-custom>.panel-heading {
	background-color: #64B5F6;
}

.panel-group .panel:last-child {
	border-bottom: 5px solid #eef7f1;
}

panel-collapse .collapse.in {
	border-bottom: 0;
}

.nav-pills>li.active>a, .nav-pills>li.active>a:focus, .nav-pills>li.active>a:hover
	{
	color: #fff1f1;
	background-color: #abaeb1;
}

@import
	url("https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css")
	;

.panel-title>a:before {
	float: left !important;
	font-family: FontAwesome;
	content: "\f068";
	padding-right: 5px;
}

.panel-title>a.collapsed:before {
	float: left !important;
	content: "\f067";
}

.panel-title>a:hover, .panel-title>a:active, .panel-title>a:focus {
	text-decoration: none;
}

#display {
	width: 100%;
	height: 700px;
}
</style>


<body>
	<%Integer moduleId = (Integer)session.getAttribute("moduleId"); %>
	<%@ include file="../views/common/header.jsp"%>
	<div class="sz-main-content-wrapper">

			<div class="sz-breadcrumb-wrapper">
			    <div class="container-fluid">
			        <ul class="sz-breadcrumbs">
			        		<li><a href="/studentportal/home">Student Zone</a></li>
			        		<li><a href="viewCourseHomePage">My Courses</a></li>
			        		<li><a href="${moduleContentBean.subject}" class="encodedHref">${moduleContentBean.subject}</a></li>
			        		<li><a href="#">${moduleContentBean.moduleName}</a></li>
			        </ul>
			        <ul class="sz-social-icons">
			            <li><a href="https://www.facebook.com/NMIMSSCE" class="icon-facebook" target="_blank"></a></li>
			            <li><a href="https://twitter.com/NMIMS_SCE" class="icon-twitter" target="_blank"></a></li>
			        </ul>
			    </div>
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

						<div class="row" style="background-color: white;">


							<div class="panel-group col-md-3" id="accordion"
								style="padding-top: 25px;" role="tablist"
								aria-multiselectable="true">
								<a href="#" onclick="history.go(-1)"
									style="padding-bottom: 10px;"> <i class="fa fa-mail-reply"><span
										style="margin-left: 5px;">Back</span></i>
								</a>
								<div class="panel panel-custom"
									style="margin-bottom: 10px; margin-top: 10px;">
									<div class="panel-heading" role="tab" id="headingOne"
										style="border-radius: 5px;">
										<h4 class="panel-title">
											<a class="collapsed" data-toggle="collapse"
												data-parent="#accordion" href="#collapseOne"
												aria-expanded="true" aria-controls="collapseOne">
												Documents </a>

											<div class="row"
												style="float: right; margin-top: -10%; width: 60%;">
												<div class="col-sm-10">
													<h5
														style="color: #fff; font-weight: normal; text-transform: capitalize;">
														Progress : <b>${moduleContentBean.percentComplete} %</b>
													</h5>


												</div>



												<div class="col-sm-12">
													<div class="col-sm-12"
														style="height: 5px; background-color: white; width: 100%; border: 10px; padding: 0px 0px;">
														<div class="col-sm-12"
															style="height:5px; background-color:#9fa1ab; width:${moduleContentBean.percentComplete}%; border:10px; padding:0px 5px;">

														</div>
													</div>
												</div>

											</div>
										</h4>


									</div>


									<div id="collapseOne" class="panel-collapse collapse"
										role="tabpanel" aria-labelledby="headingOne">
										<div class="panel-body animated zoomIn">
											<ul class="nav nav-pills nav-stacked ">
												<c:forEach var="document" items="${moduleDocumnentList}"
													varStatus="dStatus">
													<li class="moduleLi" style="height: 35px;"><a href="#"
														onClick="showPdfPage(${moduleId},'${moduleContentBean.subject}')"
														data-toggle="pill">${document.documentName}</a> <a
														href="${SERVER_PATH}${document.previewPath}#toolbar=0"
														target="_blank"
														style="margin: 0px; padding: 0px; width: 21px; bottom: 30px; left: 80%;"><i
															class="fa fa-external-link" style="font-size: 18px;"></i></a>
														<%-- do not allow download of pdf files
                  <a href="downloadDocument?filePath=${document.documentPath}" download
                  style="margin:0px;padding:0px;width:21px;bottom:50px;left:90%;" ><i class="fa fa-download large" style="font-size:20px;"></i></a> --%>
													</li>


												</c:forEach>
											</ul>
										</div>
									</div>
								</div>
								<div class="panel panel-custom">
									<div class="panel-heading" role="tab" id="headingTwo"
										style="border-radius: 5px;">
										<h4 class="panel-title">
											<a class="collapsed" data-toggle="collapse"
												data-parent="#accordion" href="#collapseTwo"
												aria-expanded="true" aria-controls="collapseTwo"> Videos
											</a>
											<div class="row"
												style="float: right; margin-top: -10%; width: 60%;">

												<div class="col-sm-10">
													<h5
														style="color: white; font-weight: normal; text-transform: capitalize;">
														Progress: <b> ${moduleContentBean.videoPercentage} %</b>
													</h5>
												</div>

												<div class="col-sm-12">
													<div class="col-sm-12"
														style="height: 5px; background-color: white; width: 100%; border: 10px; padding: 0px 0px;">
														<div class="col-sm-12"
															style="height:5px; background-color:#9fa1ab; width:${moduleContentBean.videoPercentage}%; border:10px; padding:0px 5px;">

														</div>
													</div>
												</div>

											</div>
										</h4>
									</div>
									<div id="collapseTwo" class="panel-collapse collapse "
										role="tabpanel" aria-labelledby="headingTwo">
										<div class="panel-body animated zoomIn">
											<ul class="nav nav-pills nav-stacked ">
												<%--  <li class="videoLi" style="height:35px;">
                 					
                 					 	
                 					 	<a href="#" onClick="showVideoPage(${moduleContentBean.id},${videoToBePlayed.id},'${videoToBePlayed.type}')"  data-toggle="pill">
                 					 		${videoToBePlayed.fileName}
                 					 	</a>
                 					 	
                 					 </li> --%>
												<c:forEach var="relatedVideoLink" items="${videoTopicsList}">
													<li class="videoLi" style="height: 35px;"><a href="#"
														onClick="showVideoPage(${moduleContentBean.id},${relatedVideoLink.id},'${relatedVideoLink.type}')"
														data-toggle="pill"> ${relatedVideoLink.fileName} </a></li>

												</c:forEach>
											</ul>
										</div>
									</div>
								</div>

							</div>

							<div class="tab-content col-md-8" id="moduleId">
								<div class="" style="width: 100%;">
									<iframe id="display"></iframe>
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
$(document).ready(function() {
    $("#videoClicked").click(function() {
		 //Start 
			var videoDuration = '${videoToBePlayed.duration}'
			console.log('Got videoDuration'+videoDuration);
			var videoInSeconds=getTimeAsSeconds(videoDuration)
			console.log("video In Seconds  "+videoInSeconds);
		   
	 $.ajax({url: '${SERVER_PATH}studentportal/videoViewed?moduleId='+${moduleContentBean.id}+'&videoTopicId='+${videoToBePlayed.id}+,
		        		  success: function(result){
		        			  console.log(result);
		        			  var timeout= videoInSeconds/2;
		      		  		console.log('time out '+timeout);
		          },
		          error: function(error){
					  console.log(error);
		          }
		     });  
		//End
	 
      
    });
    //call to convert hms to seconds
    function getTimeAsSeconds(time){
        var timeArray = time.split(':');
        return Number(timeArray [0]) * 3600 + Number(timeArray [1]) * 60 + Number(timeArray[2]);
    }

    
});
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
	
	var globalModuleId=${moduleContentBean.id};
	var globalDocumentId='';
	
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
              
              $.ajax({url: '${SERVER_PATH}studentportal/pageViewed?moduleId='+globalModuleId+'&documentId='+globalDocumentId+'&pageNo='+this.previousIndex,
        		  success: function(result){
        			  console.log(result);
          },
          error: function(error){
			  console.log(error);
  		}
              
              
              });
            },
          },
		}); 
	
	function goToDocumentDetail(documentId){
		console.log('Clicked Link With documentId'+documentId); 
		globalDocumentId = documentId;
	}
	
	
</script>

<script>
	$(document).ready(function() {

	});       
	function showVideoPage(moduleId,videoId,type) {
		console.log('showVideoPage '+moduleId+''+videoId+''+type);
		$('#display').attr('src', '/studentportal/viewModuleVideoPage?moduleId='+moduleId+'&videoSubtopicId='+videoId+'&type='+type);
	}

	function showPdfPage(moduleId,subject) {
		console.log('showPdfPage '+moduleId+''+subject);
		$('#display').attr('src', '/studentportal/viewModulePdfPage?moduleId='+moduleId+'&subject='+subject);
	} 
  	$('.encodedHref').each(function() {
  		var subject = $(this).attr("href");    
  		$(this).attr("href","viewCourseDetails?subject="+encodeURIComponent(subject)); 
  	}); 
</script>



</html>