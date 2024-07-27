<script>
	var ratio = 8/16; 
	
	function getComputedHeight(video,container) {
	    var width = parseInt(window.getComputedStyle(container)['width'], 10);
	    var height = (width * ratio);
	//	$(video).css('margin-top','-3.278%');
	    
	    return height;
	}
	
	$( window ).resize(function() {
		iFrameVideoResizer();
	});
	
	function iFrameVideoResizer(){
		var containers = document.getElementsByClassName("embed_container");

		
		var maxHeight = 0;
		
		Array.prototype.forEach.call(containers, function(element) {
			var container = element;
			var video = $(container).find('.video');
			
			$(container).width('100%');
			$(video).width('100%');
			
			var height = getComputedHeight(video, container);
			
			if(height > maxHeight){
				maxHeight = height;
			}
		});
		var studentBarHeight = $(".student-info-bar").height();
		var breadcrumbWrapperHeight = $(".sz-breadcrumb-wrapper").height();
		var headerHeight = $(".sz-header").height();
		
		var topHeight = studentBarHeight + breadcrumbWrapperHeight + headerHeight;
		
		if(maxHeight > (window.innerHeight - topHeight)){
			maxHeight = window.innerHeight - topHeight;
			width = ( maxHeight / ratio);
			Array.prototype.forEach.call(containers, function(element) {
				var container = element;
				var video = $(container).find('.video');
				$(container).height((maxHeight) + 'px');
				$(video).height((maxHeight) + 'px');
				$(container).width((width) + 'px');
				$(video).width((width) + 'px');
			});	
		}else{
			Array.prototype.forEach.call(containers, function(element) {
				var container = element;
				var video = $(container).find('.video');
				$(container).height((maxHeight) + 'px');
				$(video).height((maxHeight) + 'px');
			});	
		}
	}
</script>