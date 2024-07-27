
$(document).ready(function(){
	$("#myModal").modal('show');
});
	
$.ajax({
	   type : "POST",
	   contentType : "application/json",
	   url : "m/reRegForMobile",   
	   data : JSON.stringify(data),
	   success : function(data) {
		   if(data.success==true){
			   $(".re_reg_li").css("display","block");
		   }
	   } 
});