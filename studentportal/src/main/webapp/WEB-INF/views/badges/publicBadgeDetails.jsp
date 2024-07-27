<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>NMIMS Public Badge Details</title>
<!-- Bootstrap -->
<link href="../../assets/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="../../assets/css/style.css?v=4">
<link href="../../assets/css/openBadges.css" rel="stylesheet">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<style>


.sz-content p {
    font-weight: 500;
    font-family : 'sans-serif';
}

</style>


</head>
<body>

<div class="sz-header visibleHeader" style="position: relative;">
	<div class="container-fluid">
		<div class="sz-logo">
			<img src="../../assets/images/logo.png" class="img-responsive" alt="">
		</div>

		<div class="pull-right hidden-xs">
		 
		</div>

		<div class="clearfix"></div>
	</div>
</div>
<!-- Header END -->

<div class="container">
<c:choose>
<c:when test="${badgesDetails.isRevoked eq 0}">

					<div class="sz-content">

						<h2 class="red text-capitalize">Badge Details</h2>
						<div class="clearfix"></div>
						<%@ include file="../common/messages.jsp"%>
						<div class="panel-content-wrapper">	
									<div class="panel-body" style="padding: 20px;">
										<div class="row">
											
											<div class="col-sm-3 col-md-3 col-lg-3">
												<span class="${(badgesDetails.isBadgeIssued eq 0 ) ? 'glyphicon glyphicon-lock badge-lock' : ''}"></span>
												<img class="${(badgesDetails.isBadgeIssued eq 0 or badgesDetails.isClaimed eq 0 or badgesDetails.isRevoked eq 1) ? 'badgeNotIssued' : ''}"  src="${badgesDetails.attachment}" title="${badgesDetails.badgeName}" width="100%">
											</div>
											
											
											<div class="col-sm-6 col-md-6 col-lg-6">
											
												<strong style="font-size : 25px">${badgesDetails.badgeName}</strong>
												<p>Badge Awarded To <strong class="">${badgesDetails.firstname} ${badgesDetails.lastname}</strong></p>
												
												<p>For <strong>${badgesDetails.awardedAt}</strong> Subject</p>
												
												<c:if test = "${badgesDetails.isBadgeIssued ne 0 }">
													<p>Issued on <strong>${badgesDetails.dateissued}</strong></p>
												</c:if>
												
												<p>${badgesDetails.badgeDescription}</p>
											
											</div>
											
											
											
											<div class="col-sm-2 col-md-2 col-lg-2">
												<strong style="font-size : 12px">Issued By</strong>
												<p style="font-size : 16px; color: #d2232a;">${badgesDetails.issuername}</p>
											</div>
											
										</div>
										
										<div class="row">
											<div class="col-md-4 col-sm-4 col-lg-4 col-sm-offset-3">
												<c:if test = "${badgesDetails.isClaimed eq 1 and badgesDetails.isRevoked eq 0 }">
													<svg class="icon_  icon_claimed " viewBox="0 0 200 24">
														<path d="M12 1C5.935 1 1 5.935 1 12C1 18.065 5.935 23 12 23C18.065 23 23 18.065 23 12C23 5.935 18.065 1 12 1ZM10.585 16.4925L6.3425 12.25L7.7575 10.835L10.585 13.6625L16.2425 8.005L17.6575 9.42L10.585 16.4925Z"></path>
														<text x="25" y="16" font-size="10" font-weight="bold">VERIFIED</text>
													</svg>
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
												<li>${evidenceBean.evidenceValue}</li>
											</div>
											</c:forEach>	
											</ul>
											</div>
											
										</div>
									</div>
						</div>
						</c:if>

					</div>

	</c:when>    
    <c:otherwise>
       <div class="sz-content">
       <br><br>
       	<div class="panel-content-wrapper">	
       	<div class="panel-body" style="padding: 20px;">
       		<h1>Sorry, Badge is Revoked...</h1>
       	</div>
       	</div>
       
       </div>

    </c:otherwise>
</c:choose>
</div>






<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="../../assets/js/jquery-1.11.3.min.js"></script>
<script src="../../assets/js/bootstrap.js"></script>

</body>

<script>

$(document).ready(function() {

	var divs = $('.single-card');
	for (var i = 0; i < divs.length; i += 2) {
			divs.slice(i, i + 2).wrapAll('<div class="row"></div>');
	}
	
	// this will disable right-click on all images
	$('img').bind('contextmenu', function(e) {
	    return false;
	}); 

	// this will disable dragging of all images
    $("img").mousedown(function(e){
         e.preventDefault()
    });

	
});


</script>

</html>