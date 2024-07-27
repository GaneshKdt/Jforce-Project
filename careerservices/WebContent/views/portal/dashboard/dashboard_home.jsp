<!DOCTYPE html>


<%@page import="com.google.gson.Gson"%>
<%@page import="com.nmims.beans.CSHomeModelBean"%>
<%@page import="com.nmims.beans.UserViewedWebinar"%>
<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@page import="java.util.Date"%>
<%@page import="com.nmims.beans.SessionDayTimeBean"%>
<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<html lang="en">
	
	<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
	
	<jsp:include page="/views/common/jscss.jsp">
		<jsp:param value="Dashboard" name="title"/>
	</jsp:include>
	
	<style>
		.feature-link{
			color: darkcyan;
		}
		
		.carousel-indicators li {
    		background-color: #d2232a;
   		}
   		.carousel-control-prev, .carousel-control-next {
    		width: 5%;
   		}
	</style>
	<body>
	
		<jsp:include page="/views/common/header.jsp" />
		<div class="sz-main-content-wrapper">
			<jsp:include page="/views/common/breadcrum.jsp">
				<jsp:param value="<a href='/careerservices/Home'>Career Services</a>;<a href='Home'>Dashboard</a>" name="breadcrumItems" />
			</jsp:include>
		
			<div class="sz-main-content menu-closed text-manager">
				<div class="sz-main-content-inner">
					<jsp:include page="/views/common/left-sidebar.jsp">
						<jsp:param value="" name="activeMenu"/>
					</jsp:include>	
		
					<div class="sz-content-wrapper">
						<jsp:include page="/views/common/studentInfoBar.jsp" />
						<jsp:include page="/views/portal/loader.jsp" />
						<div class="sz-content" style="display:none;" id="page-content">
							<div class="p-3">
								<jsp:include page="/views/common/messages.jsp" />
								<div class="clearfix"></div>
								<div class="row">
									<div class="col-xl-9 col-md-12 col-sm-12 my-3">
										<div id="purchases">
										</div>
										<div class="card" id="orientationVideoContainer">
											<div class="card-header" style="">
												<!-- <p class="header pb-0 pt-3 text-left" style="text-align: left; font-size: 33px;"> 
													Orientation Videos
												</p> -->
												<div class="mr-auto float-left">
													<h1 class="header pb-0 mb-0 font-weight-normal display-4">Orientation <!-- Videos -->
													</h1>
												</div>
												<div class="ml-auto float-right">
													<button class="btn btn-success my-0" href="#orientationVideosCarousel" role="button" data-slide="prev">
														<span class="fa fa-chevron-left text-left" aria-hidden="true"></span>
														<!-- Previous -->
													</button>
													<button class="btn btn-success my-0" href="#orientationVideosCarousel" role="button" data-slide="next">
														<!-- Next -->
														<span class="fa fa-chevron-right text-right" aria-hidden="true"></span>
													</button>
												</div>
											</div>
											<div class="mx-auto w-100">
												<div id="orientationVideosCarousel" class="carousel slide" data-interval="false" data-ride="carousel">
													<div id="orientationVideos" class="carousel-inner">
													</div>
													<ol id="orientationIndicators" class="carousel-indicators">
													</ol>
												</div>
											</div>
										</div>
									</div>
									
									<div class="col-xl-3 col-md-12 col-sm-12 my-3">
										<jsp:include page="/views/portal/upcomingAndActiveEvents.jsp" />
									</div>
									
									
									<div class="col-12">
										<div class="card mx-auto mt-3">
											<div class="p-4">
												<jsp:include page="/views/portal/TermsAndConditions.jsp" />
											</div>
										</div>
									</div>
								</div>									
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		
			<jsp:include page="/views/common/footer.jsp" />
			
  			<jsp:include page="/views/common/iFrameVideoResizer.jsp" />
  			
			<script>
				$('#orientationVideosCarousel').bind('slide.bs.carousel', function (e) {
					$.each($(".embed_container iFrame"),function(name, vimeoVideoFrame){
						// get the video source
						var vidsrc = $(vimeoVideoFrame).attr('src');
						
						//reset the sourcer
						$(vimeoVideoFrame).attr('src','');
						$(vimeoVideoFrame).attr('src', vidsrc);
					});
				});
				
				$(window).on('resize', function(){
					resizeImages();
				});
				
				$(document).on('ready', function () {
					
					$('.load-more-table a').on('click', function () {
						$("#courseHomeLearningResources").addClass('showAllEntries');
						$(this).hide();
					});
					getDashBoardInfo();
				});
				

				var sapid = '{"sapid": "<%= (String)request.getSession().getAttribute("userId") %>"}';

				var careerForumActivationsLeft = "";
				var careerForumTotalActivations = "";
				var careerForumActivationsPossible = "";
				var careerForumNextActivationPossible = "";
				function populateEvents(){
					var sapid = '{"sapid": "<%= (String)request.getSession().getAttribute("userId") %>"}';
					$.ajax({
						type: 'POST',
						url: '/careerservices/m/getUpcomingEventsSchedule',
						data: sapid,
						contentType: "application/json;", 
						dataType: "json",
						success: function(data, textStatus ){
							if(data.status != "success"){
								showLoadingError();
								return;
							}
							$.ajax({
								type: 'POST',
								url: '/careerservices/m/getCareerForumViewedEvents',
								data: sapid,
								contentType: "application/json;", 
								dataType: "json",
								success: function(data, textStatus ){
									if(data.status != "success"){
										showLoadingError();
										return;
									}
									allViewedEvents = data.response;
							    	stopLoading();
									return;
								},
								error: function (error) {
									showLoadingError();
									return;
								}
							});
							
							activeEvents(data.response.activeEvents);
							upcomingEvents(data.response.upcomingEvents);
							
							return;
						},
						error: function (error) {
							showLoadingError();
							return;
						}
					});
				}
				var termsAndConditions;
				function getDashBoardInfo(){
					
					$.ajax({
						type: 'POST',
						url: '/careerservices/m/getStudentDashboardInfo',
						data: sapid,
						contentType: "application/json;", 
						dataType: "json",
						success: function(data, textStatus ){
							populateEvents();
							if(data.status != "success"){
								showLoadingError();
								return;
							}
							var html = "";
							data.response.packages.forEach(function(packageInfo){
								html += getPackageInfo(packageInfo);
							})
							
							setTermsAndConditions(data.response.termsAndConditions);
							termsAndConditions = data.response.termsAndConditions;
							$("#purchases").html(html);

							setOrientationVideos(data.response.orientationVideos);
							
							return;
						},
						error: function (error) {
							showLoadingError();
							return;
						}
					});
				}
				
				
				var x = '';
				function getPackageInfo(packageDetails){
					var packageId = packageDetails["familyId"];
					var packageURL = `<a href='viewProductDetails?productId=` + packageId + `'><h2 class="font-size:larger">About this Package </h2></a>`;
					var packageName = packageDetails["packageName"];
					var description = packageDetails["description"];
					var features = packageDetails["entitlementsInfo"];
					var validityTo = packageDetails["validTo"];
					var validityFrom = packageDetails["validFrom"];
					var upgradeAvailable = packageDetails["upgradeAvailable"];
					var upgradeURL = packageDetails["upgradeURL"];
				
					var purchaseInfoHeader = 
					`<div class="card text-center mb-3">
						<div class=" card-special mx-auto px-5">
							<div>

								<p class="header pb-0 pt-3 text-left" style="
								    text-align: left;
								    font-size: 33px;
								    color: #d2232a;
								"> ` + packageName + ` </p>
							</div>
							<div class="text-left">
							    <p style="
							    font-size: smaller;
							">Career Development is a career enrichment program focusing on identification of the right career path for and then developing skillsets required for achieving that career goal. This program is specifically designed for people who are seeking guidance on growth prospects and would like to understand and develop their strengths for enriching their careers. </p>
							    
							</div>
						</div>
					`;
					
					var purchaseInfoBodyStart = `<div class="row mx-3 pb-4 mt-3">`;
					
					var entitlementsBody = ``;
					
					features.forEach(function(feature){
						entitlementsBody += getFeatureString(feature);
					})
					
					var purchaseInfoBodyEnd = `</div>`;
					
						var purchaseFooterStart = `
						<div id="purchase-footer" class="card-footer">
							<div class="row">
						`;
						var purchaseFooterUpgradeString = "";
						if(upgradeAvailable){

							purchaseFooterUpgradeString += `
							<a class="pb-3 pl-3 mr-auto" href="` + upgradeURL + `" ><b>Upgrade Available</b></a>
							`;
							
						}
						
						var fromDate = new Date(validityFrom);
						var toDate = new Date(validityTo);

						var fromDateString = fromDate.toDateString();

						    
					    var toDateString = "";
					    
					    if(toDate < (new Date())){
					    	var toDateString = `<span style="
					    	    text-transform: uppercase;
					    	    color: #d2232a;
					    	    font-weight: 900;
					    	    font-size: larger;
					    	">Ended</span>`;
					    }else{
							var toDateString = toDate.toDateString();
					    }
					    
					    if(toDateString == "Invalid Date"){
					    	toDateString = "Not Available";
					    }
					    
						var purchaseFooterEnd = `
							<div class="pb-0 pl-5 mr-auto text-vertical-center">
								` + packageURL + `
							</div>
							<div class="pb-0 pr-3 ml-auto">
								<p class=""> Purchased: ` + fromDateString + `</p>
								<p class=""> Valid Upto: ` + toDateString + `</p>
							</div>
						</div>
						</div>
						</div>`;
					
					var bodyText = purchaseInfoHeader + purchaseInfoBodyStart + entitlementsBody + purchaseInfoBodyEnd + purchaseFooterStart + purchaseFooterUpgradeString + purchaseFooterEnd;
				
					return bodyText;
				}
				
				var f;
				function getFeatureString(featureInfo, nextActivationDateStr){

					f = featureInfo;
					var sessionsTotalString = featureInfo["totalSessions"];
					var sessionsCompletedString = featureInfo["totalSessions"] - featureInfo["sessionsLeft"];
					var sessionsAvailableString = featureInfo["sessionsActivationsAvailable"];
					var featureName = featureInfo["entitlementName"];
					var featureType = featureInfo["entitlementType"];
					var activation
					var featureIcon = "";
					var featureLink = "";
					
					var nextActivation = "Not Valid";
					
					if(featureInfo.nextActivationAvailableDate != null){
						console.log(featureInfo.nextActivationAvailableDate);
				    	var nextActivationDate = new Date(featureInfo.nextActivationAvailableDate);
				    	nextActivation = nextActivationDate.toLocaleDateString();
						
					}else{
						nextActivation = 'Not Valid';
					}
					
					if(sessionsTotalString == 0 && sessionsCompletedString == 0 && sessionsAvailableString == 0){
						sessionsTotalString = "-";
						sessionsCompletedString = "-";
						sessionsAvailableString = "-";
					}
					
					var link_end = "<i class='fas fa-chevron-right text-right float-right'></i></h2></a>";
					var link_start = "";

					var link_start = "<a class='feature-link' href='";
					var link_href="";
					var link_middle = "'><h2 class='w-100 text-left'>";
					var link_text = "";
					var link_end = "<i class='fas fa-chevron-right text-right float-right'></i></h2></a>";
					var linkStatus;

					switch(featureName){
						case "Career Forum":
							careerForumActivationsLeft = featureInfo["sessionsLeft"];
							careerForumTotalActivations = sessionsTotalString;
							careerForumActivationsPossible = sessionsAvailableString;
							careerForumNextActivationPossible = nextActivation;
							link_href = "career_forum";
							link_text = "Career Forum ";
							
							featureLink = link_start + link_href + link_middle + link_text + link_end;
							break;
						case "Learning Portal":
							link_href = "learning_portal";
							link_text = "Learning Portal ";

							featureLink = link_start + link_href + link_middle + link_text + link_end;
							break;
						case "Career Counselling":
							sessionsAvailableString = featureInfo["sessionsLeft"];

							if( ${isCounsellingActive} ){
								link_href = "careerCounselling";
								link_text = "Career Counselling";
								
								featureLink = link_start + link_href + link_middle + link_text + link_end;			
							}else{
								featureLink = "<h2>Career Counselling</h2>";
							}
								
							break;
						case "Practice Interviews":

							sessionsAvailableString = featureInfo["sessionsLeft"];
							
							if( ${isPracticeInterviewActive} ){
								link_href = "practiceInterview";
								link_text = "Practice Interviews";
								
								featureLink = link_start + link_href + link_middle + link_text + link_end;			
							}else{
								featureLink = "<h2>Practice Interviews</h2>";
							}
							
							break;
						case "Job Search Support":
							featureLink = "<h2>Job Search Support</h2>";
							break;
						default: 
							break;
					}
					
					

					nextAvailable = `
						<div class="col-12 border-top pb-0 mb-0">
							<div class="text-right p-0 py-1">
								<p style="font-size: 0.7rem;" class="pb-0 mb-0 pt-2 my-0">Next Available: ` + nextActivation + `</p>	
							</div>
						</div>`;
						
						
					var featureInfoString = `
						<div class="col-lg-6 col-md-12 col-xs-12">
							<div class="card m-2">
								<div class="p-3" style="float: right;font-size: 1.0rem;"> 
									` + featureLink + `
								</div>
								<div class="card-footer">
									<div class="row">
										<div class="col-4 text-center">
											<small>Sessions</small>
											<br>
											<p style="font-size: 1.0rem;">` + sessionsTotalString + `</p>	
										</div>
										<div class="col-4 text-center">
											<small>Completed</small>
											<br>
											<p style="font-size: 1.0rem;">` + sessionsCompletedString + `</p>	
										</div>
										<div class="col-4 text-center">
											<small>Available</small>
											<br>
											<p style="font-size: 1.0rem;">` + sessionsAvailableString + ` </p>	
										</div>
										` + nextAvailable + `
									</div>
								</div>
							</div>
						</div>`;

						return featureInfoString;
				}
				
				
				var orientationItems = 0;
				function setOrientationVideos(videos){
					var html1 = "";
					var html2 = "";
					
					if(videos.length < 1){
						$("#orientationVideoContainer").hide();
					}
					
					videos.forEach(function(video){
						var classExtra = "";
						if(orientationItems == 0){
							classExtra = 'class="active"';
						}
						html2 += `<li data-target="#orientationVideosCarousel" data-slide-to="` + orientationItems + `" ` + classExtra + `></li>`;
						orientationItems ++ ;
						html1 += getOrientationCard(video);
					});
					

					$("#orientationVideos").html(html1);
					$("#orientationIndicators").html(html2);
				}
				
				function getOrientationCard(video){
					
					var videoTitle = video.videoTitle;
					var alt = video.videoTitle;
					var videoDescription = video.description;
					var thumbnailUrl = video.thumbnailUrl;
					var videoUrl = video.videoLink;
					var classExtra = "";
					if(orientationItems == 1){
						classExtra = "active"
					}
					var html =
						`<div class="carousel-item ` + classExtra + `">
							<div class="row card-header mx-2">
								<div class="section-title">
				    				<h1 class="border-0">` + videoTitle +`</h1>
				    			</div>
								<div class="p-special">
								    <div>` + videoDescription +`</div>
							    </div>
				    		</div>
							<div class="mx-auto text-center">
								<div class="mx-auto w-100">
									<div id="embed_container" class="embed_container vertical-center mx-auto" style="position: relative;height: 0; width:100%;" class='embed-container w-100' >
								      <iframe class="video" style="width:100%;" src='` + videoUrl + `' id="video" frameborder='0' webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>
										` + /* <a class="carousel-control-prev" href="#orientationVideosCarousel" role="button" data-slide="prev">
											<span class="fa fa-chevron-left" style="color: #d2232a; font-size: 50px;" aria-hidden="true"></span>
											<span class="sr-only">Previous</span>
										</a>
										<a class="carousel-control-next" href="#orientationVideosCarousel" role="button" data-slide="next">
											<span class="fa fa-chevron-right" style="color: #d2232a; font-size: 50px;" aria-hidden="true"></span>
											<span class="sr-only">Next</span>
										</a>
										+  */`
							      	</div>
								</div>
							</div>
						</div>`;
					
					return html;
				}

		</script>
		
	</body>
</html>