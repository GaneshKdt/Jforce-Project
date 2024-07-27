function validateForm(){
		var track = document.getElementById("trackForAdd").value;
		var regex = /^[a-zA-Z\d\-_\s]+$/g;
		let result = regex.test(track);
		if(result){
			return result;
		}else{
			alert('Use  - _ space  Special Characters Only');
			return false;
		}		
	}
	
	var acc = document.getElementsByClassName("accordion");
	var i;

	for (i = 0; i < acc.length; i++) {
	  acc[i].addEventListener("click", function() {
/* 	    this.classList.toggle("active"); 
 */	    var panel = this.nextElementSibling; 
	    if (panel.style.maxHeight) {
	      panel.style.maxHeight = null;
	      let active = document.querySelectorAll(".accordion.active");
	      for(let j = 0; j < active.length; j++){
	    	    active[j].classList.add("active");
	    	    active[j].nextElementSibling.style.maxHeight = null;
	    	  }
	    	  this.classList.toggle("active");	
	    } else {
	    	let active = document.querySelectorAll(".accordion.active");
	    	  for(let j = 0; j < active.length; j++){
	    	    active[j].classList.remove("active");
	    	    active[j].nextElementSibling.style.maxHeight = null;
	    	  }
	    	  this.classList.toggle("active");	    
	      panel.style.maxHeight = panel.scrollHeight + "px";
	    } 
	  });
	}		

	$(document)
	.ready(function() {
				let options = "<option>Loading... </option>";
				$('#track').html(options);			
				$
						.ajax({
							type : "GET",
							contentType : "application/json",
							url : "getTracksName",
							success : function(data) {
								options = "";
								for (let i = 0; i < data.length; i++) {
											options = options
													+ "<option value='"
													+ data[i]
													+ "'> "
													+ data[i]
													+ " </option>";
									}
								
								$('#track')
								.html(
										" <option disabled selected value=''> Select track  </option> "
												+ options);							
							},
							error : function(e) {
								alert("Failed to get track.")
							}
						});

			});