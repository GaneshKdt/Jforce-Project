<%@page import="com.nmims.beans.PacakageAvailabilityBean"%>
<%@page import="com.nmims.beans.UpgradePathDetails"%>
<%@page import="com.nmims.beans.AvailablePackagesModelBean"%>
<%@page import="com.nmims.beans.Feature"%>
<%@page import="com.nmims.beans.PackageBean"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>

<%@page import="com.nmims.helpers.*"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Calendar" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html lang="en">
<jsp:include page="/views/common/jscss.jsp">
	<jsp:param value="Career Services - Available Products" name="title" />
</jsp:include>

<style>
	.pb-100 {
		padding-bottom: 100px;
	}

	.pt-100 {
		padding-top: 100px;
	}

	a {
		text-decoration: none;
	}

	.section-title h4 {
		font-size: 14px;
		font-weight: 500;
		color: #777;
		
	}

	.section-title h1 {
		font-size: 24px;
		text-transform: capitalize;
		margin: 15px 0;
		display: inline-block;
		position: relative;
		font-weight: 700;
	    /* padding-bottom: 15px; */
	    letter-spacing: 1px;
	    text-transform: uppercase;
	    /* text-decoration: underline; */
	    border-bottom: solid 1px;
	}

	.section-title p {
		font-weight: 300;
		font-size: 14px;
	}

	.black-bg .section-title h1,
	.black-bg .section-title h4,
	.black-bg .section-title p {
		color: #fff
	}

	.section-title {
		margin-bottom: 15px;
	}

	.single-price {
		text-align: center;
		padding: 30px;
		box-shadow: 0px 0px 2px rgba(0, 0, 0, 0.2);
	}

	.price-title h4 {
		font-size: 24px;
		text-transform: uppercase;
		font-weight: 600;
	}

	.price-tag {
		margin: 30px 0;
	}

	.price-tag {
		margin: 30px 0;
		background-color: #23d6d0;
		color: #000;
		padding: 10px 0;
	}

	.center.price-tag {
		background-color: var(--nm-red);
		color: #fff
	}

	.price-tag h1 {
		font-size: 45px;
		font-weight: 600;
		font-family: poppins;
	}

	.price-tag h1 span {
		font-weight: 300;
		font-size: 16px;
		font-style: italic;
	}

	.price-item ul {
		margin: 0;
		padding: 0;
		list-style: none;
	}

	.price-item ul li {
		font-size: 14px;
		padding: 5px 0;
		border-bottom: 1px dashed #eee;
		margin: 5px 0;
	}

	.price-item ul li:last-child {
		border-bottom: 0;
	}

	.single-price a {
		margin-top: 15px;
	}

	a.box-btn {
		background-color: #d2232a;
		padding: 5px 20px;
		display: inline-block;
		color: #fff;
		text-transform: capitalize;
		border-radius: 3px;
		font-size: 15px;
		transition: .3s;
	}

	a.box-btn:hover,
	a.border-btn:hover {
		background-color: #d35400;
	}

	.pricing-card-price {
		font-size: 1.5rem;
		line-height: 1.2;
		
	}

	.pricing-card-price-type {
		font-size: 1.5rem;
		line-height: 1.2;
		font-weight: bolder;
	}

	.button-pricing {
		font-size: 14px;
		font-weight: 700;
		text-align: center;
		border-radius: 100px;
		text-transform: uppercase;
		padding: 0 20px;
		letter-spacing: 1px;
		color: #fff;
		cursor: pointer;
		-webkit-user-select: none;
		-moz-user-select: none;
		-ms-user-select: none;
		user-select: none;
		height: 50px;
		-webkit-transition: background .2s ease;
		transition: background .2s ease;
		background: var(--nm-red);
		width: 80%;
	}

	.pricing-div {
		margin-top: 15px;
		display: flex;
		font-weight: 200;
	}

	.pricing-card-body-header i {
		font-size: larger;
	}

	.pricing-card-body-header {
		padding-left: 5px;
		font-size: large;
		line-height: 1.5;
		font-weight: 700;
		text-transform: capitalize;
		color: #000;
	}

	.pricing-feature-item {
		margin-left: 2px;
		margin-top: 10px;
	}

	.pricing-feature-item-data {
		margin-top: 3px;
		font-weight: 500;
		font-size: smaller;
	}

	.pricing-feature-item-data i {
		color: green;
	}

	.pricing-card {
		padding: 0.2rem;
	}

	.pricing-card .card-body {
		padding: 0px;
		display: flex;
		flex-wrap: wrap;
	}

	.pricing-card .card-header {
		padding: 0px;
		display: flex;
		flex-wrap: wrap;
	}

	.pricing-card .card-footer {
		padding: 0px;
		display: flex;
		flex-wrap: wrap;
	}

	.pricing-card {
		padding: 1rem;
		float: none;
		text-align: center !important;
	}

	.pricing-card.header1 {
		border-top: 8px solid lightgray;
		border-top-left-radius: 0.25rem !important;
	}

	.pricing-card.header2 {
		border-top: 8px solid #37b34a;
	}

	.pricing-card.header3 {
		border-top: 8px solid #26a9e0;
		border-top-right-radius: 0.25rem !important;
	}

	.pricing-card-body {
		padding: 0.3rem;
	}

	.pricing-card-footer {
		padding: 1rem;
	}

	.pricing-card.unavailable .pricing-card-body, .pricing-card.unavailable .pricing-card-header{
    	cursor: not-allowed;
	    padding: 5px;
	    opacity: .5;
    }
    
	.pricing-card .purchased {
    	background: lightgreen;
    }
    
	.pricing-card.upcoming {
    	background: #ffdb9a;
    }
    
    .pricing-card.unavailable .pricing-card-body button {
    	cursor: not-allowed;
    }
	.card {
		overflow-x: inherit;
	}


	td {
		padding-top: .5em;
		padding-bottom: .5em;
		padding-left: 10px;
		text-align: -webkit-left;
		font-size: medium;
		line-height: 1.5;
		font-weight: 700;
		text-transform: capitalize;
		color: #000;
	}

	td.inactive {
		color: gray;
	}

	.bullet-list li {
		list-style-type: disc;
		font-size 14px;
	}

	.fa-check {
		color: green;
	}

	.fa-times {
		color: red;
	}
	
	.section-title h5{
		font-size: large;
	}
	
	
	.section-title h1, .section-title h2, .section-title h3, .section-title h4, .section-title h5{
		text-transform: none;
		color: black;
	}
</style>

<body>

	<jsp:include page="/views/common/header.jsp"/>
	<div class="sz-main-content-wrapper">
		<jsp:include page="/views/common/breadcrum.jsp">
			<jsp:param value="Career Services;Available Products" name="breadcrumItems" />
		</jsp:include>
		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="/views/common/left-sidebar.jsp">
					<jsp:param value="" name="activeMenu" />
				</jsp:include>
				<div class="sz-content-wrapper dashBoard">
					<jsp:include page="/views/common/studentInfoBar.jsp"/>
					<div class="sz-content padding-top">
						<div class="panel-content-wrapper">
							<jsp:include page="/views/common/messages.jsp"/>
							<div class="col-lg-8 col-md-10 col-sm-12 mx-auto ">
								<div class="mx-auto">
									<div>
										<div class="section-title">
											<h1>About Career Serivces</h1>
										</div>
										<div class="pt-2" style="padding-bottom: 2rem;">
											${ PageData.aboutCS.text }
										</div>
									</div>
									<div class="section-title">
										<h1>Career Services Packages</h1>
										<div class="clearfix"></div>
									</div>
								</div>
								<div class="mx-auto" style="/* font-family: Open Sans,sans-serif */">
									<!-- Show packages here -->
									<div class="mx-auto wysiwyg-generated">
											<% 
												AvailablePackagesModelBean pageData = (AvailablePackagesModelBean) request.getAttribute("PageData"); 
												
												for(UpgradePathDetails upgradePath: pageData.getUpgradePathsAndPackageDetails()){
													if(upgradePath.getUpgradePath() != null && upgradePath.getUpgradePath().getPathName() != null){
											%>
											
													<div class="pricing-card row">
														<%-- <div class="section-title col-12">
															<h5><%= upgradePath.getUpgradePath().getPathName() %></h5>
														</div> --%>
														<% 
														int numCards = 0;
														for(PacakageAvailabilityBean thisPackage: upgradePath.getPackages()){
															numCards++;
															String cardClass = "card col-4 border-right pricing-card header" ;
															cardClass = cardClass + numCards;
															String cardStatusClass = "";
															if(!thisPackage.isAvailable()){ 
																cardStatusClass += " unavailable";
																if(thisPackage.isUpcoming()){
																	cardStatusClass += " upcoming";
																}
															}
															if(thisPackage.isPurchased()){ 
																cardClass += " purchased";
															}
															
														%>
															<div class="<%= cardClass %> <%= cardStatusClass %>">
																<div class="card-header pricing-card-header mx-auto">
																		<div class="pricing-card-header-title mx-auto w-100">
																			<div class="pricing-card-price-type w-100 h-100 text-center">
																				<div class="w-100 h-100" style="display: table;">
																					<div class="w-100 h-100" style="display: table-cell; vertical-align: middle;">
																						<%= thisPackage.getFamilyName() %>
																					</div>
																				</div>
																			</div>
																		</div>
																		<div class="mt-4 ml-3 mr-2 pricing-card-header-price mx-auto">
																			<div class="pricing-card-price text-center mx-auto"> &#x20B9; 
																				<span class="price"><%= thisPackage.getPrice() %></span> /-
																			</div>
																		</div>
																		<div class="mx-auto py-3 pricing-div pricing-card-header-description">
																			<div class="w-100 h-100" style="display: table;">
																				<div class="w-100 h-100" style="display: table-cell; vertical-align: middle;">
																					<%= thisPackage.getDescriptionShort() %>
																				</div>
																			</div>
																		</div>
																</div>
																<div class="card-body pricing-card-body">
																	<div class="mx-auto">
																		<table style="margin: 10px">
																			<tbody>
																			<% 
																				for(Feature feature: upgradePath.getFeaturesAvailableForThisFamily()){
																			%>
																				<tr>
																					<td>
																				<%	if(thisPackage.getAvailableFeatures().contains(feature.getFeatureId())){ %>
																						<i class="fa fa-check"></i>
																				<%  }else{ %>
																						<i class="fa fa-times"></i>
																				<%  } %>
																					</td>
																					<td>
																						<%= feature.getFeatureName() %>
																					</td>
																				</tr>
																				<% 
																				}
																				%>
																			</tbody>
																		</table>
																	</div>
																</div>
																<div class="card-footer pricing-card-footer" style="display: block">
																	<div class="pricing-card-footer-action" style="display: block">
																		<div class="w-100 h-100" style="display: table;">
																			<div class="w-100 h-100" style="display: table-cell; vertical-align: middle;">
																				<% if(thisPackage.isPurchased()){ %>
																					<div class="mx-auto text-center my-4">
																						<h2 style="float: none">Purchased</h2>
																					</div>
																				<% }else if(!thisPackage.isAvailable()){ 
																						if(thisPackage.isUpcoming()){
																				%>
																						<div class="mx-auto text-center my-4">
																							<h2 style="float: none">Coming Soon</h2>
																						</div>
																				<%
																						}else{
																				%>
																						<div class="mx-auto text-center my-4">
																							<h2 style="float: none">Unavailable</h2>
																						</div>
																					<%	} %>
																					<div class="clearfix"></div>
																				<% }else{ %>
																					<button class="button-pricing mx-auto" onclick="location.href='applyForProduct?packageId=<%= thisPackage.getPackageId() %>'" >Enroll now!</button>
																				<% } %>
																			</div>
																		</div>
																	</div>
																	<div class="pricing-card-footer-learn-more-link" style="display: block">
																		<div class="w-100 h-100" style="display: table;">
																			<div class="w-100 h-100" style="display: table-cell; vertical-align: middle;">
																				<div class="mx-auto text-center">
																					<a href="viewProductDetails?productId=<%= thisPackage.getFamilyId() %>" class="learn-more"><b>LEARN MORE</b></a>
																				</div>
																			</div>
																		</div>
																	</div>
																</div>
															</div>
														<%	} %>
													</div>
											<%	}
											}%>
									</div>
								</div>
							</div>
						</div>
						<div class="panel-content-wrapper">
							<div class="col-lg-7 col-md-10 col-sm-12 mx-auto">
								<jsp:include page="/views/portal/TermsAndConditions.jsp" />
								<jsp:include page="/views/portal/supportCallUsAt.jsp" />
							</div>
						</div>
					</div>
					<div class="clearfix"></div>
				</div>
			</div>
		</div>
		<jsp:include page="/views/common/footer.jsp" />
	</div>
	
	<script>

	
		$(window).resize(function(){
			unsetHeights();
			setPricingCardHeights();
		});
		
		function setPricingCardHeights() {

			setHeightToMax(".pricing-card-footer");

			setHeightToMax(".pricing-card-footer-action");

			setHeightToMax(".pricing-card-footer-learn-more-link");
			
			setHeightToMin(".pricing-card-body");
			
			setHeightToMax(".pricing-card-header-title");

			setHeightToMax(".pricing-card-header-price");
			
			setHeightToMax(".pricing-card-header-description");
			
			setHeightToMax(".pricing-card-header");
		}
		
		function unsetHeights(){

			unsetHeight(".pricing-card-footer");

			unsetHeight(".pricing-card-footer-action");

			unsetHeight(".pricing-card-footer-learn-more-link");
			
			unsetHeight(".pricing-card-body");
			
			unsetHeight(".pricing-card-header-title");

			unsetHeight(".pricing-card-header-price");
			
			unsetHeight(".pricing-card-header-description");
			
			unsetHeight(".pricing-card-header");
			
		}
		
		function unsetHeight(elementTypeName){
			$(elementTypeName).css('height', 'auto');
		}
		
		function setHeightToMax(elementTypeName){

			var highest = 0;
			$.each($(elementTypeName), function(name, elem){
				if(highest== 0 || highest < $(elem).height()){
					highest= $(elem).height();
			    }
			});
			
			$.each($(elementTypeName), function(name, elem){
				$(elem).height(highest);
			})
		}
		
		function setHeightToMin(elementTypeName){

			var lowest = 0;
			$.each($(elementTypeName), function(name, elem){
				if(lowest== 0 || lowest > $(elem).height()){
					lowest= $(elem).height();
			    }
			});
			
			$.each($(elementTypeName), function(name, elem){
				$(elem).height(lowest);
			})
		}
	</script>
	
	<script>
		function commaSeparateNumber(val){
			val=val.split(".")[0];
			while (/(\d+)(\d{3})/.test(val.toString())){
			  val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
			}
			return val;
		}
		$(document).on('ready', function () {

			$('.load-more-table a').on('click', function () {
				$("#courseHomeLearningResources").addClass('showAllEntries');
				$(this).hide();
			});
			setPricingCardHeights();

		});

		$(window).on('load', function () {
			setPricingCardHeights();
			getTermsAndConditions();
			$.each($('.price'), function(){
				var val = commaSeparateNumber($(this).html());
				$(this).html(val);
			});
		});

		function commaSeparateNumber(val){
			val=val.split(".")[0];
			while (/(\d+)(\d{3})/.test(val.toString())){
			  val = val.toString().replace(/(\d+)(\d{3})/, '$1'+','+'$2');
			}
			return val;
		}
		$(window).resize(function () {
			setPricingCardHeights();
		});
		
	</script>
	<script src="resources_2015/fittext.js"></script>
	<script>
	</script>
</body>

</html>