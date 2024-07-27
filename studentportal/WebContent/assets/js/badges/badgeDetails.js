document.addEventListener("DOMContentLoaded", function() {
	/*NOTE: isClaimed variable is initialized in badgeDetails.html, initializing variables in js will create
	 * a new instance which will return null*/
		if(isClaimed === 0){
			let claimedModal = new bootstrap.Modal(document.getElementById('claimedModal'), {});
			claimedModal.show();

			let numberOfStars = 200;
			let congratsDiv = document.querySelector('.congrats');
			let classNames = ['blob', 'fa-solid', 'fa-star'];

			Array.from({ length: numberOfStars }).forEach(function(_, i) {
			    let blobDiv = document.createElement('div');
			    blobDiv.classList.add(...classNames);
			    congratsDiv.appendChild(blobDiv);
			});
			animateText();
			animateBlobs();
		}
	});

	function openShareUrlHandler(urlStub, uniquehash) {	
		window.open(urlStub + uniquehash);
	}

	function shareOnLinkedinHandler(clientId, scope, code, redirectUri, uniqueHash) {
		/*console.log("ClientId: ", clientId, " scope: ", scope, " redirectUri: ", redirectUri, " uniqueHash: ", uniqueHash);*/
		const windowFeatures = "left=100, top=100, width=1280, height=640, popup=true";
		/*console.log('https://www.linkedin.com/oauth/v2/authorization?client_id='+ clientId +'&scope=' + scope + '&response_type='+code+'&redirect_uri='+redirectUri+'&state=' + uniqueHash);*/
		let openedWindow = window.open('https://www.linkedin.com/oauth/v2/authorization?client_id='+ clientId +'&scope=' + scope + '&response_type='+code+'&redirect_uri='+redirectUri+'&state=' + uniqueHash,
				"mozillaWindow", windowFeatures);
	}

	function copyLinkHandler(urlStub, uniquehash) {	
		navigator.clipboard.writeText(urlStub + uniquehash);
		let clipboardIcon = document.getElementById("clipBoard");
		let checkmarkIcon = document.getElementById("checkMark");
		let copiedText = document.getElementById("copiedText");
	
		if (clipboardIcon.style.display !== "none") {
			clipboardIcon.style.display = "none";
			checkmarkIcon.style.display = "inline-block";
			copiedText.style.display = "inline-block";
			
			setTimeout(function() {
				copiedText.style.display = 'none';
			  }, 3000);
		}
		/*alert("Copied share link!");*/
	}

	function claimedMyBadge(uniquehash) {	
	  	let data = {"uniquehash" : uniquehash};
	  	$.ajax({
	  		type : "POST",
	  		url : '/studentportal/m/claimedMyBadge',
	  		contentType : "application/json",
	  		data : JSON.stringify(data),
	  		dataType : "JSON",
	  		success : function(data) {
				if(data === 1) {
					location.reload();					
		  	  	} else {
					/*alert('Error In calling API !!!');*/
					document.getElementById("messages").style.display = "block";
                    document.getElementById("errorButton").style.display = "block";
                    document.getElementById("errorMessage").innerHTML="Error calling badge claim API !!!";
		  	  	}   	  	  					
	  		}
	  	});
	  }

	function revokedMyBadge(uniquehash) {	
		let data = {"uniquehash" : uniquehash};
	 	$.ajax({
	 		type : "POST",
	 		url : '/studentportal/m/revokedMyBadge',
	 		contentType : "application/json",
	 		data : JSON.stringify(data),
	 		dataType : "JSON",
	 		success : function(data) {
		  		if(data === 1){
		  			location.reload();
		  	  	}else{
					/*alert('Error In calling API !!!');*/
			  	  	document.getElementById("messages").style.display = "block";
	                document.getElementById("errorButton").style.display = "block";
	                document.getElementById("errorMessage").innerHTML="Error calling badge revoke API !!!";
		  	  	}			
	 		}
	 	});	
	 }

	function reclaimedMyBadge(uniquehash) {	
		let data = {"uniquehash" : uniquehash};
	 	$.ajax({
	 		type : "POST",
	 		url : '/studentportal/m/reClaimedRevokedMyBadge',
	 		contentType : "application/json",
	 		data : JSON.stringify(data),
	 		dataType : "JSON",
	 		success : function(data) {
		  		if(data === 1){
		  			location.reload();
		  	  	}else{
					/*alert('Error In calling API !!!');*/
			  	  	document.getElementById("messages").style.display = "block";
	                document.getElementById("errorButton").style.display = "block";
	                document.getElementById("errorMessage").innerHTML="Error calling badge reclaim API !!!";
		  	  	}
	 		}
	 	});	
	 }

	function animateText() {	
		TweenMax.from($('#modalTitle'), 0.8, {
			scale: 0.4,
			opacity: 0,
			rotation: 15,
			ease: Back.easeOut.config(4),
		});
	}
	
	function animateBlobs() {	
		let xSeed = _.random(350, 380);
		let ySeed = _.random(120, 170);
		
		$.each($('.blob'), function(i) {
			let $blob = $(this);
			let speed = _.random(1, 5);
			let rotation = _.random(5, 100);
			let scale = _.random(0.8, 1.5);
			let x = _.random(-xSeed, xSeed);
			let y = _.random(-ySeed, ySeed);
	
			TweenMax.to($blob, speed, {
				x: x,
				y: y,
				ease: Power1.easeOut,
				opacity: 0,
				rotation: rotation,
				scale: scale,
				onStartParams: [$blob],
				onStart: function($element) {
					$element.css('display', 'block');
				},
				onCompleteParams: [$blob],
				onComplete: function($element) {
					$element.css('display', 'none');
				}
			});
		});
	}