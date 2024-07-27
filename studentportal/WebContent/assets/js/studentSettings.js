function updateSetting(text,e){
		$("#alertMessageOnUpdate").hide();
		
		 let body = { 
					'sapid':$("#userId").val(),
					'settingType':text,
					'isEnable':e.target.checked	
				} 
 			$.ajax({
				type:"POST",
				contentType:"application/json;",
				url:"updateSetting",
				data:JSON.stringify(body),
				success:function(success){
						$("#alertMessageOnUpdate").removeClass("alert-danger");
						$("#alertMessageOnUpdate").addClass("alert-success");
						$("#message").html("<strong>Successfully changes updated!</strong> please login again to see changes.");
						setTimeout(function() {
						    $("#alertMessageOnUpdate").show();
						}, 800);			
					},
				error:function(error){
					$("#alertMessageOnUpdate").removeClass("alert-success");
					$("#alertMessageOnUpdate").addClass("alert-danger");
					$("#message").html("<strong> Error while updating settings. </strong> Please try again!");
					setTimeout(function() {
					    $("#alertMessageOnUpdate").show();
					}, 800);
					}
				
			}); 
		}

	$("#successCloseButton").click(function(){
		$("#alertMessageOnUpdate").hide();
	})