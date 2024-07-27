<!DOCTYPE html>
<html lang="en">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="Badge Details" name="title" />
</jsp:include>

<head>
	<link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/openBadges.css" rel="stylesheet">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">

	<style type="text/css">
		@import url(https://fonts.googleapis.com/css?family=Sigmar+One);
	
		.congrats {		
			padding: 20px 10px;
			text-align: center;
		}
		
		#modalTitle {
			transform-origin: 50% 50%;
			font-family: 'Sigmar One', cursive;
			cursor: pointer;
			z-index: 2;
			position: absolute;
			top: 0;
			width: 100%;
		}
		   
		.blob {
			height: 50px;       
			width: 50px;   
			color: #ffcc00;
			position: absolute;
			top: 45%;
			left: 45%;
			z-index: 1;
			font-size: 30px;
			display: none;	
		}
	</style>
</head>

<body>
	<%@ include file="../common/header.jsp"%>
	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;My Badges;Badge Details" name="breadcrumItems" />
		</jsp:include>
   
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
	 	    	<div id="sticky-sidebar">  
					<jsp:include page="../common/left-sidebar.jsp">
						<jsp:param value="My Badges" name="activeMenu" />
					</jsp:include>
				</div>
				<div class="sz-content-wrapper">
					<%@ include file="../common/studentInfoBar.jsp"%>

					<div class="sz-content">
					
					<br/>
					<h2 class="red text-capitalize">Badge Details</h2>			
					<div class="clearfix"></div>
					<%@ include file="../common/messages.jsp"%>
					<div class="panel-content-wrapper">	
								<div class="panel-body" style="padding: 20px;">
									<div class="row">										
										<div class="col-sm-3 col-md-3 col-lg-3">
											<span class="${(badgesDetails.isBadgeIssued eq 0 ) ? 'fa-solid fa-lock' : ''}"></span>
												<img class="${(badgesDetails.isBadgeIssued eq 0 or badgesDetails.isClaimed eq 0 or badgesDetails.isRevoked eq 1) ? 'badgeNotIssued' : ''}"  src="${badgesDetails.attachment}" title="${badgesDetails.badgeName}" width="100%">
											</div>											
											
											<div class="col-sm-6 col-md-6 col-lg-6">
												<strong style="font-size : 25px">${badgesDetails.badgeName}</strong>
												<p>Awarded For <strong>${badgesDetails.awardedAt}</strong></p>
												
												<c:if test = "${badgesDetails.isBadgeIssued ne 0 }">
													<p>Issued on <strong>${badgesDetails.dateissued}</strong></p>
												</c:if>
												

												<p>${badgesDetails.badgeDescription} </p>
												
											<div class="row">
											<div class="col-md-4 col-sm-4 col-lg-4 col-sm-offset-3">
													<c:if test = "${badgesDetails.isBadgeIssued ne 0 and badgesDetails.isRevoked eq 0 }">
													<svg class="icon_ ${(badgesDetails.isClaimed eq 1 ) ? 'icon_claimed' : 'icon_not_claimed'}" viewBox="0 0 200 24">
														<path d="M12 1C5.935 1 1 5.935 1 12C1 18.065 5.935 23 12 23C18.065 23 23 18.065 23 12C23 5.935 18.065 1 12 1ZM10.585 16.4925L6.3425 12.25L7.7575 10.835L10.585 13.6625L16.2425 8.005L17.6575 9.42L10.585 16.4925Z"></path>
														<text x="25" y="16" font-size="10" font-weight="bold">CLAIMED</text>
													</svg>
												</c:if>
												
												<c:if test = "${badgesDetails.isRevoked eq 1 }">
													<svg class="icon_ " viewBox="0 0 200 24">
													<path d="M22.7,17.5l-8.1-14c-0.8-1.4-2.7-1.9-4.1-1.1C10,2.7,9.6,3.1,9.4,3.5l-8.1,14c-0.8,1.4-0.3,3.3,1.1,4.1
															c0.5,0.3,1,0.4,1.5,0.4h16.1c1.7,0,3-1.4,3-3C23.1,18.4,22.9,17.9,22.7,17.5z M12,18c-0.6,0-1-0.4-1-1s0.4-1,1-1s1,0.4,1,1
															S12.6,18,12,18z M13,13c0,0.6-0.4,1-1,1s-1-0.4-1-1V9c0-0.6,0.4-1,1-1s1,0.4,1,1V13z">
													</path>
														<text x="25" y="16" font-size="10" font-weight="bold">REVOKED</text>
													</svg>
												</c:if>												
													
												<c:if test = "${badgesDetails.isClaimed eq 0 }">
													<input type ="button"  class="btn btn-outline-danger" value="Claim My Badge" onclick="claimedMyBagde('${badgesDetails.uniquehash}')" > 
												</c:if>	
												
												<c:if test = "${badgesDetails.isClaimed eq 1 and badgesDetails.isRevoked eq 0 }">
													<input type ="button"  class="btn btn-outline-danger" value="Revoke My Badge" onclick="revokedMyBagde('${badgesDetails.uniquehash}')" > 
													<br>
													<input type ="button"  class="btn btn-outline-primary" value="Share" id="sharebtn"  > 												
												</c:if>
												
												<c:if test = "${badgesDetails.isClaimed eq 1 and badgesDetails.isRevoked eq 1 }">
													<input type ="button"  class="btn btn-outline-info" value="Reclaim My Badge" onclick="reclaimedMyBagde('${badgesDetails.uniquehash}')" > 
												</c:if>	
											</div>											
										</div>											
									</div>						
											<div class="col-sm-2 col-md-2 col-lg-2">
										<c:if test = "${badgesDetails.isBadgeIssued ne 0}">
											<strong style="font-size : 12px">Issued By</strong>
											<p style="font-size : 16px; color: #d2232a;">${badgesDetails.issuername}</p>
										</c:if>										
									</div>									
								</div>
							</div>
						</div>
						
						<div class="panel-content-wrapper">	
									<div class="panel-body" style="padding: 20px;">
										<div class="row">
											<div class="col-sm-3 col-md-3 col-lg-3">
												<dl>
												 <dt class="block-x-title">EARNING CRITERIA</dt>
												 <dd class="block-x-desc">Recipients must complete the earning criteria to earn this Badge</dd>
												</dl>
											</div>
											<div class="col-sm-8 col-md-8 col-lg-8">
												<p>${badgesDetails.criteriaDescription}</p>
											</div>
											
										</div>
									</div>
						</div>
						
						<c:if test = "${badgesDetails.isBadgeIssued ne 0 }">
						<div class="panel-content-wrapper">	
									<div class="panel-body" style="padding: 20px;">
										<div class="row">
											<div class="col-sm-3 col-md-3 col-lg-3">
												<dl>
												 <dt class="block-x-title">EVIDENCE</dt>
												 <dd class="block-x-desc">Proof that the recipient met the earning criteria</dd>
												</dl>
											</div>
											<div class="col-sm-8 col-md-8 col-lg-8">
											<ul>
											<c:forEach var="evidenceBean" items="${badgesDetails.evidenceBeanList}" varStatus="badgesListCount">
											<div class="single-card col-sm-6 col-md-6 col-lg-6">	
												<li> ${evidenceBean.evidenceValue}</li>
											</div>	
											</c:forEach>
											</ul>	
											</div>
											
										</div>
									</div>
						</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<!-- Modal 1 Start -->
	<div class="modal fade" id="claimedModal" role="dialog" >
   		<div class="modal-dialog modal-md">
   			<div class="modal-content" style="border-radius: 5px;">
   				<div class="modal-header congrats" style="background-color: #E24A4A; color: white; border-radius: 5px 5px 0px 0px;">
   					<h4 class="modal-title" id="modalTitle" style="color: white; text-transform: capitalize; text-align: center; font-size: 1.5rem;" >Congratulations!</h4>
   				</div>
   				<div class="modal-body" style="overflow: hidden;">
					<div class="row">
						<div class="col-sm-12 col-md-12 col-lg-12">
							<center><strong class="block-x-desc">You have completed the criteria for this badge. <br/>Please claim this badge to be able to add it in your profile</strong></center>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-12 col-md-12 col-lg-12">
							<center>
								<input type ="button"  class="btn btn-outline-danger" value="Claim My Badge" onclick="claimedMyBagde('${badgesDetails.uniquehash}')" >
							</center>
						</div>
					</div>
   				</div>
			</div>
   		</div>
    </div>
	
	<!-- Modal 1 End -->

	<!-- Modal 2 Start -->
	<div class="modal fade" id="shareModal" role="dialog" >
     		<div class="modal-dialog modal-md">
     			<div class="modal-content" style="border-radius: 5px;">
     				<div class="modal-header" style="overflow: hidden;">
						<div class="row col-sm-12 col-md-12 col-lg-12">
							<h1 class="modal-title fs-5" id="share-modal">Share Badge</h1>
						</div> 
					</div>
						
					<div class="modal-body" style="overflow: hidden;">  
						<div class="share-div">											
							<div class="input-group mb-3">
							  <input type="text" class="form-control" readonly class="form-control" id="shareableLink" placeholder="Shareable Url" aria-label="shareableLink" aria-describedby="basic-addon2">
							  <div class="input-group-append">
							    <button class="btn btn-outline-secondary" style="border: 1px solid #6C757D; border-radius: 5px; background-color: #F4F5F7; color: #6C757D; width: 50px" onclick="copyLink()">
									<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
									  <path d="M4 1.5H3a2 2 0 0 0-2 2V14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V3.5a2 2 0 0 0-2-2h-1v1h1a1 1 0 0 1 1 1V14a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V3.5a1 1 0 0 1 1-1h1v-1z"/>
									  <path d="M9.5 1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-3a.5.5 0 0 1-.5-.5v-1a.5.5 0 0 1 .5-.5h3zm-3-1A1.5 1.5 0 0 0 5 1.5v1A1.5 1.5 0 0 0 6.5 4h3A1.5 1.5 0 0 0 11 2.5v-1A1.5 1.5 0 0 0 9.5 0h-3z"/>
									</svg>												
								</button>
							  </div>
							</div>											
						</div>											
					</div>
					
					<div class="modal-footer">						
						<a href="#" target="_blank" id="openNewTab">Open in a New Window</a>
					</div>     				
     			</div>
     		</div>
     	</div>
	<!-- Modal 2 End -->
	
	<jsp:include page="../common/footer.jsp" />
	<script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/1.18.0/TweenMax.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/underscore.js/1.8.2/underscore-min.js"></script>	
	
</body>
<script>

$(document).ready(function() {
	var divs = $('.single-card');
	for (var i = 0; i < divs.length; i += 2) {
			divs.slice(i, i + 2).wrapAll('<div class="row"></div>');
	}

	let isclaimed = ${badgesDetails.isClaimed};
	if(isclaimed == 0){	
		$('#claimedModal').modal('show');
		var numberOfStars = 200;
		
		for (var i = 0; i < numberOfStars; i++) {
		  $('.congrats').append('<div class="blob fa-solid fa-star ' + i + '"></div>');
		}	

		animateText();
		
		animateBlobs();
	}

	
	// this will disable right-click on all images
	$('img').bind('contextmenu', function(e) {
	    return false;
	}); 

	// this will disable dragging of all images
    $("img").mousedown(function(e){
         e.preventDefault()
    });
    

    $("#sharebtn").click(function(e){
    	let url =	window.location.origin;
		url += '/studentportal/credentials/public/badgedetails/${badgesDetails.uniquehash}';
		$('#shareableLink').val(url);
		$('#openNewTab').attr("href", url);
    	$('#shareModal').modal('show');
   });
	
});

function copyLink(){
	var copyText = document.getElementById("shareableLink");
	  copyText.select();
	  document.execCommand("copy");
	  alert("Link Copied");
}

function claimedMyBagde(uniquehash){
	
  	 let data = {
  				"uniquehash" :  uniquehash
  			};
  		$.ajax({
  			type : "POST",
  			url : '/studentportal/m/claimedMyBadge',
  			contentType : "application/json",
  			data : JSON.stringify(data),
  			dataType : "JSON",
  			success : function(data) {
  			 console.log("data "+data);	
	  			if(data == 1){
	  				location.reload();
	  	  	  	}else{
					alert('Error In calling API !!!');
	  	  	  	}
   	  	  					
  			}
  		});	
  }

function revokedMyBagde(uniquehash){
	
 	 let data = {
 				"uniquehash" :  uniquehash
 			};
 		$.ajax({
 			type : "POST",
 			url : '/studentportal/m/revokedMyBadge',
 			contentType : "application/json",
 			data : JSON.stringify(data),
 			dataType : "JSON",
 			success : function(data) {
 			 console.log("data "+data);	
	  			if(data == 1){
	  				location.reload();
	  	  	  	}else{
					alert('Error In calling API !!!');
	  	  	  	}
  	  	  					
 			}
 		});	
 }

function reclaimedMyBagde(uniquehash){
	
 	 let data = {
 				"uniquehash" :  uniquehash
 			};
 		$.ajax({
 			type : "POST",
 			url : '/studentportal/m/reClaimedRevokedMyBadge',
 			contentType : "application/json",
 			data : JSON.stringify(data),
 			dataType : "JSON",
 			success : function(data) {
 			 console.log("data "+data);	
	  			if(data == 1){
	  				location.reload();
	  	  	  	}else{
					alert('Error In calling API !!!');
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
	
	var xSeed = _.random(350, 380);
	var ySeed = _.random(120, 170);
	
	$.each($('.blob'), function(i) {
		var $blob = $(this);
		var speed = _.random(1, 5);
		var rotation = _.random(5, 100);
		var scale = _.random(0.8, 1.5);
		var x = _.random(-xSeed, xSeed);
		var y = _.random(-ySeed, ySeed);

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

</script>

</html>
