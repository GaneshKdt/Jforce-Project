<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<title>NMIMS Public ID Card Details</title>
 <link rel="stylesheet" type="text/css" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/openBadges.css"> 
  <link rel="stylesheet" type="text/css" href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/bootstrap.min.css"> 
   <link  href="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/css/style.css?v=4" rel="stylesheet" type="text/css" > 

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
			<img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/logo.png" class="img-responsive" alt="">
		</div>

		<div class="pull-right hidden-xs">
		 
		</div>

		<div class="clearfix"></div>
	</div>
</div>
<!-- Header END -->

<div class="container">
<%-- <c:choose>
<c:when test="${studentBean  not empty }"> --%>

					<div class="sz-content">

						<h2 class="red text-capitalize">Student Details</h2>
						<div class="clearfix"></div>
						<%@ include file="../common/messages.jsp"%>
						<div class="panel-content-wrapper">	
									<div class="panel-body" style="padding: 20px;">
										<div class="row">
											
											<div class="col-sm-3 col-md-3 col-lg-3">
												<img class=""  src="${studentBean.imageUrl}" title="${studentBean.firstName}" width="70%" height="50%">
											</div>
											
											
											<div class="col-sm-6 col-md-6 col-lg-6">
											

<%-- 												<strong style="font-size : 25px">${studentBean.firstName}</strong>
 --%>
												<p style="font-size : 20px">Id Card Belongs To <strong class="">${studentBean.firstName} ${studentBean.lastName}</strong></p>
												<p style="font-size : 20px">Student Id : <strong>${studentBean.sapid}</strong></p>
												<p style="font-size : 20px">Program : <strong>${studentBean.program}</strong> </p>
												<p style="font-size : 20px">DOB : <strong>${studentBean.dob}</strong> </p>
												<p style="font-size : 20px">Mobile : <strong>${studentBean.mobile}</strong> </p>
												<p style="font-size : 20px">Validity Month/Year : <strong>${studentBean.validityEndMonth}&nbsp;${studentBean.validityEndYear}</strong> </p>
												<%-- <p style="font-size : 20px">IC Name : <strong>${studentBean.centerName}</strong> </p> --%>
												<p style="font-size : 20px">University Regional Office & NMAT/NPAT Centre : <strong>${studentBean.lc}</strong> </p>
												<p style="font-size : 20px">Blood group : <strong>${studentBean.bloodGroup}</strong> </p>
												<p style="font-size : 20px">Batch : <strong>${studentBean.enrollmentMonth}&nbsp;${studentBean.enrollmentYear}</strong> </p>
											</div>
											
											<div class="col-sm-2 col-md-2 col-lg-2">
												<strong style="font-size : 12px">Issued By</strong>
												<p style="font-size : 16px; color: #d2232a;">NMIMS</p>
											</div>
											
										</div>
										
										<div class="row">
											<div class="col-md-4 col-sm-4 col-lg-4 col-sm-offset-3">
												
													<svg class="icon_  icon_claimed " viewBox="0 0 200 24">
														<path d="M12 1C5.935 1 1 5.935 1 12C1 18.065 5.935 23 12 23C18.065 23 23 18.065 23 12C23 5.935 18.065 1 12 1ZM10.585 16.4925L6.3425 12.25L7.7575 10.835L10.585 13.6625L16.2425 8.005L17.6575 9.42L10.585 16.4925Z"></path>
														<text x="25" y="16" font-size="10" font-weight="bold">VERIFIED</text>
													</svg>
											</div>
										</div>
										
									</div>
						</div>
						
						<div class="panel-content-wrapper">	
									<div class="panel-body" style="padding: 20px;">
										<div class="row">
											<div class="col-sm-3 col-md-3 col-lg-3">
												<dl>
												 <dt class="block-x-title">Note:</dt>
												 <dd class="block-x-desc">This student belongs to NMIMS.</dd>
												</dl>
											</div>
											<div class="col-sm-8 col-md-8 col-lg-8">
												<%-- <p>${badgesDetails.criteriaDescription}</p> --%>
											</div>
											
										</div>
									</div>
						</div>
					</div>

	<%-- </c:when>    
    <c:otherwise>
       <div class="sz-content">
       <br><br>
       	<div class="panel-content-wrapper">	
       	<div class="panel-body" style="padding: 20px;">
       		<h1>Sorry, Not student Data found...</h1>
       	</div>
       	</div>
       
       </div>

    </c:otherwise>
</c:choose> --%>
</div>






<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<!-- <script src="../../../assets/js/jquery-1.11.3.min.js"></script>
<script src="../../../assets/js/bootstrap.js"></script> -->
<script  src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/jquery-1.11.3.min.js"></script>
<script  src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/js/bootstrap.js"></script>
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