$(document).ready(function() {
 	    $('.tables').DataTable( {
			destroy: true,
 	        "order": [[ 3, "desc" ]]
 	    });

 	});

 	$(".current-cycle").show();   
 	$(".last-cycle").hide(); 
 	$('.switchContent').on('change', function() { 
 		var selected = $(this).find('option:selected').text();
 		console.log("selected:"+selected);
 		if(selected=="Last Cycle Content"){
 			$(".current-cycle").hide();   
 			$(".last-cycle").show(); 
 		} 
 		else {
 			$(".current-cycle").show();   
 			$(".last-cycle").hide();   
 		}    
 	});