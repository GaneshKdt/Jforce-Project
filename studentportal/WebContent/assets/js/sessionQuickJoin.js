/*			function viewSession(sessionId, pssId){
				document.getElementById("sessionFrame").src="/acads/student/viewScheduledSession?id="+sessionId+"&pssId="+pssId
			}*/
		

		$("#goToPortal").click(function(){		
			window.location = "/studentportal/skipToHome";			
		}) 

		
		$(".joinSession").click(function(){	
		
			const sessions = $(this).val().split("~");			
			document.getElementById("sessionFrame").src="/acads/student/viewScheduledSession?id="+sessions[0]+"&pssId="+sessions[1];

		});
		

		var elements = document.querySelectorAll(".myField");
		for (var i=0; i < elements.length; i++) {
			document.getElementsByClassName("myDate")[i].innerHTML = moment(elements[i].value).format("YYYY-MM-DD");
			document.getElementsByClassName("myTime")[i].innerHTML = moment(elements[i].value).format("HH:mm:ss A");
		}
	
		