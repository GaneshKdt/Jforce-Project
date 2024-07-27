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

<jsp:include page="../views/jsp/common/jscss.jsp">
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
</style>


<body>


	<div class="">
		<div class="">
			<div>

				<div>

					<div class="row" style="background-color: white;">

						<div class="tab-content col-md-12" id="moduleId">
							<c:forEach var="document" items="${moduleDocumnentList}"
								varStatus="dStatus">
								<div id="tab_a${document.id }" class="tab-pane fade in active">
									<div class="swiper-container">

										<div class="swiper-wrapper">
											<c:forEach var="imageCount" begin="1"
												end="${document.noOfPages}">
												<div class="swiper-slide" style="height: 80%; width: 80%;">

													<img
														data-src="${SERVER_PATH}${document.folderPath}${document.id }_page_${imageCount}.png"
														data-srcset="${SERVER_PATH}${document.folderPath}${document.id }_page_${imageCount}.png"
														class="swiper-lazy img-responsive" id="sky">
													<div style="top: 50% !important"
														class="swiper-lazy-preloader"></div>

												</div>
											</c:forEach>
										</div>
										<div class="swiper-pagination"></div>
										<div class="swiper-button-prev" style="top: 20% !important"></div>
										<div class="swiper-button-next" style="top: 20% !important"></div>
										<div class="swiper-scrollbar"></div>
									</div>

								</div>
							</c:forEach>
						</div>


					</div>
				</div>

			</div>
		</div>
	</div>
	<div style="width: 100%; height: 40px;"></div>

</body>


<script src="https://code.jquery.com/jquery-3.3.1.min.js"
	integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
	crossorigin="anonymous"></script>

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
	
	var globalDocumentId=${documentId};
	var mySwiper = new Swiper('.swiper-container', {
		
	    // Disable preloading of all images
	    preloadImages: false,
	    // Enable lazy loading
	    lazy: true,
		// Optional parameters
		direction : 'horizontal',
		loop : false,

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
	
	
	
	
</script>
</html>