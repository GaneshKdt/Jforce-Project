<!DOCTYPE html>
<html lang="en">

<jsp:include page="../common/jscssNew.jsp">
	<jsp:param value="My Badges" name="title" />
</jsp:include>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<head>

<link href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/openBadges.css" rel="stylesheet">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

<style>

p.not-earned-msg{
    text-align: center;
    padding-top: 85px;
    opacity: 0.7;
    font-size: 25px;
    text-transform: capitalize;
}

.nav.nav-tabs > li > a{
color: #404041;
}

.text-wrap{
    white-space: normal;
    overflow: hidden;
    text-overflow: ellipsis;
    padding-left: 16px;
    padding-right: 12px;
}

.badge-div {
	width: auto;
    height: 400px;
    overflow: auto;
    padding-right: 10px;
}

.badge-img{
    margin-top: 7px;
    margin-bottom: -10px;
}

.badge-card{
	float: left;
	position: relative;
	min-height: 1px;
	padding-right: 15px;
	padding-left: 15px;
	padding-bottom: 10px;
}

.nav-tabs .badge {
    position: relative;
    top: -10px;
    right: +5px;
    background: green;
}

.button-container {
  padding-left: 10px;
  padding-right: 10px;
}

#locked-tab-pane .badge {
    position: relative;
    top: -10px;
    right: +5px;
    background: red;

}

#revoked-tab-pane .badge {
    position: relative;
    top: -10px;
    right: +5px;
    background: red;

}

.card-body {

   position:relative;
   height: 290px;
   background-color: #FFFFFF;
   padding: 0;
   -webkit-border-radius: 4px;
   -moz-border-radius: 4px;
   border-radius: 4px;
   box-shadow: 0 4px 5px 0 rgba(0, 0, 0, 0.14), 0 1px 10px 0 rgba(0, 0, 0, 0.12), 0 2px 4px -1px rgba(0, 0, 0, 0.3);
	text-align: center;
	width : 190px;
}

.btn-position
{
	position:absolute;
	bottom:0px;
	left: 50%;
    transform: translate(-50%, 0);
}

@media only screen and  (min-width: 320px){
	.badge-card {
	    width: 38.66666667%;
	}
}

@media only screen and  (min-width: 768px){
	.badge-card {
	    width: 31.66666667%;
	}
}

@media only screen and  (min-width: 1024px){
	.badge-card {
	    width: 22.66666667%;
	}
}

@media only screen and  (min-width: 1200px){
	.badge-card {
	    width: 18.66666667%;
	}
}

@media only screen and  (min-width: 1440px){
	.badge-card {
	    width: 15.66666667%;
	}
}

@media only screen and  (min-width: 2560px){
	.badge-card {
	    width: 8.66666667%;
	}
}
.badge-card:focus-within .card-body{
  outline: 2px solid black;
}
</style>

</head>
<body>

	<%@ include file="../common/header.jsp"%>

	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;My Badges" name="breadcrumItems" />
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
						<h2 class="red text-capitalize">My Badges</h2>
						<div class="clearfix"></div>
						<%@ include file="../common/messages.jsp"%>
						
													
						<ul class="nav nav-tabs" id="nav-tab" role="tablist">

							<li class="nav-item " role="presentation" id="earned-tab-pane">
								<a class="nav-link show active" type="button" id="earned-tab"
									data-bs-toggle="tab" href="#menu1"
									role="tab" aria-controls="earned-tab-pane"
									aria-selected="true">
									<!-- <div class="text-center  mt-3">
										<i class="fa-regular fa-circle-play fa-xl "></i>
									</div> -->
									<h6 class="mt-3">EARNED <span class="badge">${mybadges.earnedBadgeList.size()}</span> </h6>
								</a>
							</li>
							
							<c:if test = "${mybadges.claimedBadgeList.size() gt 0 }">
							<li class="nav-item" role="presentation" id="claimed-tab-pane"><a
								class="nav-link " type="button" data-bs-toggle="tab"
								id="claimed-tab" href="#menu2"
								role="tab" aria-controls="claimed-tab-pane"
								aria-selected="false">
									<!-- <div class="text-center mt-3">
										<i class="fa-solid fa-clone fa-xl"></i>
									</div> -->
									<h6 class="mt-3">CLAIMED <span class="badge">${mybadges.claimedBadgeList.size()}</span></h6>
							</a></li>
							</c:if>

							<c:if test = "${mybadges.lockedBadgeList.size() gt 0 }"> 
							<li class="nav-item " role="presentation" id="locked-tab-pane"><a
								class="nav-link " type="button" data-bs-toggle="tab"
								id="locked-tab" href="#menu3" role="tab"
								aria-controls="locked-tab-pane" aria-selected="false">
									<!-- <div class="text-center mt-3">
										<i class="fa-solid fa-chart-simple fa-xl"></i>
									</div> -->
									<h6 class="mt-3">LOCKED <span class="badge">${mybadges.lockedBadgeList.size()}</span></h6>
							</a></li>
							</c:if>

							<c:if test = "${mybadges.revokedBadgeList.size() gt 0 }">
							<li class="nav-item " role="presentation" id="revoked-tab-pane"><a
								class="nav-link " type="button" data-bs-toggle="tab"
								role="tab"
								id="revoked-tab" href="#menu4"
								aria-controls="revoked-tab-pane" aria-selected="false">
	<!-- 								<div class="text-center mt-3">
										<i class="fa-solid fa-clipboard-list fa-xl"></i>
									</div> -->
									<h6 class="mt-3">REVOKED <span class="badge">${mybadges.revokedBadgeList.size()}</span></h6>
							</a></li>	
							</c:if>
							</ul>	
																			
						<div class="tab-content ">
							
							<div id="menu1" class="tab-pane fade show active ">
								<c:forEach var="earnedBadge" items="${mybadges.earnedBadgeList}" varStatus="status">
									<c:url value="badgeDetails" var="earnedBadgeDetailsURL">
										<c:param name="uniquehash" value="${earnedBadge.uniquehash}" />
										<c:param name="badgeId" value="${earnedBadge.badgeId}" />
										<c:param name="awardedAt" value="${earnedBadge.awardedAt}" />
									</c:url>

									<div class="badge-card " style="max-width: 25rem; min-width: 18rem; max-height: 23rem; margin-bottom: 80px;">
										<br />										
										<div class="card" >									
											<a data-placement="bottom" title="${earnedBadge.awardedAt}"	href="${earnedBadgeDetailsURL}">
																							
												<div class="card-body" style="width: inherit; min-height: 23rem;">
													<ul class="list-group list-group-flush" >
												    	<li class="list-group-item" style="background-color: #D2D2D2; text-align: center;">
												    		<h6 class="card-title" style="color: black; font-style: italic;">${earnedBadge.awardedAtCode}</h6>
												    	</li>												    
													
													<li class="list-group-item text-center" >													
														<img class="badge-img" height="auto" width="210px"
															data-placement="bottom" title="${earnedBadge.badgeName}"
															src="${earnedBadge.attachment}">													
													</li>													
	
													</ul>
													<input type="button" style="width: 90%;"
															class="btn btn-md btn-outline-success btn-position"
															value="Claim"
															onclick="claimedMyBagde('${earnedBadge.uniquehash}')">										
												</div>
											</a>
										</div>										
									</div>
								</c:forEach>

								<c:if test="${mybadges.earnedBadgeList.size() eq 0 }">
									<p class="not-earned-msg">You haven't earned any badge yet.</p>
								</c:if>
							</div>

							<c:if test = "${mybadges.claimedBadgeList.size() gt 0 }"> 
							    <div id="menu2" class="tab-pane fade ">
							    <c:forEach var="claimedBadge" items="${mybadges.claimedBadgeList}" varStatus="status">

								    <c:url value="badgeDetails" var="claimedBadgeDetailsURL">
									  <c:param name="uniquehash" value="${claimedBadge.uniquehash}"/>
									  <c:param name="badgeId" value="${claimedBadge.badgeId}"/>
									  <c:param name="awardedAt" value="${claimedBadge.awardedAt}"/>
									</c:url>
									    
									    <div class="badge-card " style="max-width: 25rem; min-width: 18rem; max-height: 23rem; margin-bottom: 80px;">
											<br />
											<div class="card ">
											<a data-placement="bottom" title="${claimedBadge.awardedAt}"
												href="${claimedBadgeDetailsURL}">
																								
												<div class="card-body" style="width: inherit; min-height: 23rem;">
													<ul class="list-group list-group-flush" >
												    	<li class="list-group-item" style="background-color: #D2D2D2; text-align: center;">
												    		<h6 class="card-title" style="color: black; font-style: italic;">${claimedBadge.awardedAtCode}</h6>
												    	</li>												    
													
													<li class="list-group-item text-center" >											
														<img class="badge-img" height="auto" width="220px"
															data-placement="bottom" title="${claimedBadge.badgeName}"
															src="${claimedBadge.attachment}">
													</li>
													</ul>
												</div>
											</a>
											</div>
										</div>								    
							    </c:forEach>
							    </div>
							    </c:if>							    
							    
							<c:if test = "${mybadges.lockedBadgeList.size() gt 0 }">   
							    <div id="menu3" class="tab-pane fade ">
							    <c:forEach var="lockedBadge" items="${mybadges.lockedBadgeList}" varStatus="status">
							    <c:url value="badgeDetails" var="lockedBadgeDetailsURL">
								  <c:param name="uniquehash" value="0"/>
								  <c:param name="badgeId" value="${lockedBadge.badgeId}"/>
								  <c:param name="awardedAt" value="${lockedBadge.awardedAt}"/>
								</c:url>
								    
								    <div class="badge-card " style="max-width: 25rem; min-width: 18rem; max-height: 23rem; margin-bottom: 80px;">
										<br />
										<div class="card ">
											<a data-placement="bottom" title="${lockedBadge.awardedAt}" href="${lockedBadgeDetailsURL}">
									
												<div class="card-body" style="width: inherit; min-height: 23rem;">
													<ul class="list-group list-group-flush" >
												    	<li class="list-group-item" style="background-color: #D2D2D2; text-align: center;">
												    		<h6 class="card-title" style="color: black; font-style: italic;">${lockedBadge.awardedAtCode}</h6>
												    	</li>
														
														<li class="list-group-item text-center" >
															<img class="badge-img" height="auto" width="220px"
																data-placement="bottom" title="${lockedBadge.badgeName}"
																src="${lockedBadge.attachment}">
														</li>
													</ul>																					
												</div>
											</a>
										</div>
									</div>								    
							    </c:forEach>
								</div>
								</c:if>
																
							<c:if test = "${mybadges.revokedBadgeList.size() gt 0 }"> 
								<div id="menu4" class="tab-pane fade ">
							    <c:forEach var="revokedBadge" items="${mybadges.revokedBadgeList}" varStatus="status">
							    <c:url value="badgeDetails" var="revokedBadgeDetailsURL">
								  <c:param name="uniquehash" value="${revokedBadge.uniquehash}"/>
								  <c:param name="badgeId" value="${revokedBadge.badgeId}"/>
								  <c:param name="awardedAt" value="${revokedBadge.awardedAt}"/>
								</c:url>
								    
								    <div class="badge-card " style="max-width: 25rem; min-width: 18rem; max-height: 23rem; margin-bottom: 80px;">
										<br />
										<div class="card ">
										<a data-placement="bottom" title="${revokedBadge.awardedAt}"
											href="${revokedBadgeDetailsURL}">
											
											<div class="card-body" style="width: inherit; min-height: 23rem;">
												<ul class="list-group list-group-flush" >
											    	<li class="list-group-item" style="background-color: #D2D2D2; text-align: center;">
											    		<h6 class="card-title" style="color: black; font-style: italic;">${revokedBadge.awardedAtCode}</h6>
											    	</li>
																										
													<li class="list-group-item text-center">											
														<img class="badge-img" height="auto" width="210px"
															data-placement="bottom" title="${revokedBadge.badgeName}"
															src="${revokedBadge.attachment}">
													</li>
												</ul>
												<input type="button" style="width: 90%;"
													class="btn btn-md btn-outline-primary btn-position"
													value="Reclaim"
													onclick="reclaimedMyBagde('${revokedBadge.uniquehash}')">																							
											</div>
										</a>
										</div>
									</div>								    
							    </c:forEach>								
								</div>
							</c:if>							    
						</div>						
					</div>
				</div>
			</div>
		</div>
	</div>
	<br/>
	<br/>
	<jsp:include page="../common/footer.jsp" />
</body>
<script>

$(document).ready(function() {
	 $('[data-toggle="tooltip"]').tooltip(); 


	// this will disable right-click on all images
	$('img').bind('contextmenu', function(e) {
	    return false;
	}); 

	// this will disable dragging of all images
    $("img").mousedown(function(e){
         e.preventDefault()
    });
});

function claimedMyBagde(uniquehash){
	
 	 let data = {
 				"uniquehash" :  uniquehash
 			};
	
 		$.ajax({
 			type : "POST",
 			url : 'm/claimedMyBadge',
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
			url : 'm/reClaimedRevokedMyBadge',
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

$(document).ready(function() {
	  $('.badge-card').on('keydown', function(e) {
	    if (e.key === 'Tab') {
	      $('.badge-card').removeClass('focus');
	      $(this).addClass('focus');
	    }
	  });
	});

</script>

</html>