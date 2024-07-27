 
$(document).ready(function(){
    $('.combobox').combobox();
  });

$(document).scroll(function() {
	var y = $(this).scrollTop();
	if (y > 600) {
		$('.registerNow').fadeIn();
	}else {
	    $('.registerNow').fadeOut();
	 }
});
