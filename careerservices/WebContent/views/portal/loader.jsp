<style>
	.spinner-border{
		display: block;
		position: fixed;
		z-index: 1031;
		color: var(--nm-red);
		opacity: 1;
		top: calc((100vh / 2) );
		right: calc((100vw / 2) );
	}
	.spinner-container{
		z-index: 1031;
		top: calc((100vh / 2) );
		right: calc((100vw / 2) );
	}
	
	#loader {
		width: 100%;
		height: 100%;
		top: 0;
		left: 0;
		position: fixed;
		display: block;
		opacity: 0.7;
		z-index: 1;
		text-align: center;
	}
	
	.loader-container {
		width: 100%;
		height: 100%;
		top: 0;
		left: 0;
		position: fixed;
		display: block;
    	background-color: #f2f2f2;
		z-index: 1;
		text-align: center;
	}
	
	.page-content{
		display:none;
	}
</style>

<div id="loading-error" class="sz-content" style="display: none;">
	<div class="p-3">
		<jsp:include page="/views/common/messages.jsp" />
		<div class="clearfix"></div>
		<div class="col-12 my-3 mx-auto">
			<h2 class="p-2 m-3 text-center text-vertical-center col-8 mx-auto">
				<i style="color: #ff8f00; font-size: 52px; text-shadow: 0px 0px 2px #000000;" class="fas fa-exclamation-triangle"></i>
				<span class="p-3 nm-red" style="font-weight: 600;font-size: 36px;">Error fetching data from server!</span>
			</h2>
		</div>
	</div>
</div>
<div id="loader-container" class="loader-container">
	<div id="loader">
		<div class="spinner-container">
	      <div class="spinner-border" role="status">
	        <span class="sr-only">Loading...</span>
	      </div>
	        <p class="text-vertical-center">Loading... Please wait</p>
		</div>
	</div>
</div>

<script>

	function resizeImages(){
		$.each($('.thumbnail-container img'), function(number,image){
			var width = $(image).width();
			$(image).height(width);
		});
	}

	function stopLoading(){
		$( "#loader-container" ).fadeOut( "slow", function() {
			$("#loader-container").hide();
			$("#page-content").slideDown( "slow", "swing", function() {
				$("#page-content").show();
		    	resizeImages();
		    	if(iFrameVideoResizer){
		    		iFrameVideoResizer();
	    		};
			} )
		});
	}
	function startLoading(){
		$( "#page-content" ).fadeOut( "slow", function() {
			$("#page-content").hide();
			$("#loader-container").fadeIn( "slow", "swing", function() {
				$("#loader-container").show();
			} )
		});
	}
	function showLoadingError(){
		$( "#loader-container" ).fadeOut( "slow", function() {
			$("#loader-container").hide();
			$("#loading-error").fadeIn( "slow", "swing", function() {
				$("#loading-error").show();
			} )
		});
	}
</script>