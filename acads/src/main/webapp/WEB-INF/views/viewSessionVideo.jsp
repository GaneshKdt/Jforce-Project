<html>
<head>
	<title>Session Video</title>
	<meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">
  <style>
  * {
  margin: 0px;
  padding: 0px;
}
html, body {
  height: 100%;
}
div {
  display: block;
  position: relative;
}

.fullscreen {
  height: 100%;
	overflow: hidden;
	width: 100%;
}
.video {
  display: block;
	left: 0px;
	overflow: hidden;
	padding-bottom: 56.25%; /* 56.25% = 16:9. set ratio */
	position: absolute;
	top: 50%;
	width: 100%;
	-webkit-transform-origin: 50% 0;
	transform-origin: 50% 0;
	-webkit-transform: translateY(-50%);
	transform: translateY(-50%);
}
.video .wrapper {
  display: block;
	height: 300%;
	left: 0px;
	overflow: hidden;
	position: absolute;
	top: 50%;
	width: 100%;
	-webkit-transform: translateY(-50%);
	transform: translateY(-50%);
}
.video iframe {
  display: block;
	height: 100%;
	width: 100%;
}

   .iframe{
       margin: 0px;
       padding: 0px;
       border: 0px;
       border-image-source: initial;
       border-image-slice: initial;
       border-image-width: initial;
       border-image-outset: initial;
       border-image-repeat: initial;
       position: fixed;
       min-width: 0px;
       max-width: none;
       min-height: 0px;
       max-height: none;
       width: 100%;
       height: 100%;
       left: 0px;
       top: 0px;
       }
       

body {
  background-color: black;
  color: white;
  font: bold 100%/1.4 'Helvetica Neue',sans-serif;
}
h1 {
  left: 50%;
  position: absolute;
  text-align: center;
  top: 50%;
  width: 24rem;
  z-index: 1;
  -webkit-transform: translate3d(-50%,-50%,0);
  transform: translate3d(-50%,-50%,0);
}
  </style>
  <script>
  var elem = document.getElementById("myvideo"); 
  function videoSize() {
	  var $windowHeight = $(window).height();
	  var $videoHeight = $(".video").outerHeight();
		var $scale = $windowHeight / $videoHeight;
	  
	  if ($videoHeight <= $windowHeight) {
	    $(".video").css({
	      "-webkit-transform" : "scale("+$scale+") translateY(-50%)",
				"transform" : "scale("+$scale+") translateY(-50%)"
			});
		};
	}

	$(window).on('load resize',function(){
	  videoSize();
	});</script>
</head>
<body>
<%-- <div class="fullscreen">
  <div class="video" id="myvideo">
    <div class="wrapper">
      <iframe controls=true speed=true src="${ videoUrl }" width="500" height="281" frameborder="0" allow=autoplay webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>
    </div>
  </div>
</div> --%>

<div >
  <iframe class="iframe" src="${ videoUrl }" frameborder='0' webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>
</div>

  <%-- <iframe src="${ videoUrl }" frameborder="0" allowfullscreen
    style="position:fixed;top:0;left:0;width:100%;padding:0 !important;margin:0 !important;height:100%;"></iframe> --%>

</body>
</html>