<!DOCTYPE html>
<html lang="en">

<jsp:include page="../common/jscss.jsp">
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

<link href="assets/css/openBadges.css" rel="stylesheet">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

</head>
<body>

	<%@ include file="../common/header.jsp"%>


	<div class="sz-main-content-wrapper">

		<jsp:include page="../common/breadcrum.jsp">
			<jsp:param value="Student Zone;My Badges" name="breadcrumItems" />
		</jsp:include>


		<div class="sz-main-content menu-closed">
			<div class="sz-main-content-inner">
				<jsp:include page="../common/left-sidebar.jsp">
					<jsp:param value="My Badges" name="activeMenu" />
				</jsp:include>


				<div class="sz-content-wrapper">
					<%@ include file="../common/studentInfoBar.jsp"%>


					<div class="sz-content">

						<h2 class="red text-capitalize">My Badges</h2>
						<div class="clearfix"></div>
						<%@ include file="../common/messages.jsp"%>
						<c:set var = "badgeCount"  value = "0" scope="page"/>
							
								<c:forEach var="badge" items="${mybadgesList}" varStatus="status">
									
									<c:if test="${ badge.openBadgesIssuedBeanList.size() > 0 }">		
									<div class="panel-content-wrapper">	
									<div class="panel-heading"><dl>
										<dt class="">${badge.badgeName}</dt>  
										<dd class="block-x-desc"> Total Badges : ${badge.totatlBadges} &nbsp;&nbsp;&nbsp;  Total Earned Badges : ${badge.issuedCount} &nbsp;&nbsp;&nbsp; Total Claimed Badges : ${badge.claimedCount} </dd> 
										<dd class="block-x-desc"> Total Revoked : ${badge.revokedCount} &nbsp;&nbsp;&nbsp; Total Locked Badges : ${badge.notIssuedCount} &nbsp;&nbsp;&nbsp; Total Not Claimed Badges : ${badge.notClaimedCount} </dd> 
										</dl>
									</div>				
									<div class="panel-body" style="padding: 20px;">
										
										<div class="badgeSlider">
										<div class="badgeSlides">
										<c:forEach var="badgesList" items="${badge.openBadgesIssuedBeanList}" varStatus="badgesListCount">
										    <div>
										    ${ badgesList.isClaimed eq 0  ? '<span class="unclaimedText">New Badge !!!</span>' : ''}
										    	
										    	<span class="${(badgesList.isBadgeIssued eq 0 ) ? 'glyphicon glyphicon-lock badge-lock' : ''}"></span>
										    	<span class="${(badgesList.isRevoked eq 1 ) ? 'glyphicon glyphicon-warning-sign badge-lock' : ''}"></span>
										    <a target="_blank" href="badgeDetails?uniquehash=${(badgesList.isBadgeIssued eq 1 ) ? badgesList.uniquehash : '0'}&badgeId=${badge.badgeId}&awardedAt=${badgesList.awardedAt}">  	
										    <img class="${(badgesList.isBadgeIssued eq 0 or badgesList.isClaimed eq 0 or badgesList.isRevoked eq 1) ? 'badgeNotIssued' : ''}"  height="80%" width="80%"  src="${badgesList.attachment}">
										    	</a>
										    	<br>
										    	<strong class="badge-awardedAt-text"><c:out value="${badgesList.awardedAt}" /> </strong>
										    </div>
										</c:forEach>    
										</div>
										</div>	
										
										</div>
									</div>
									
									<c:set var = "badgeCount"  value = "${badgeCount + 1}" scope="page"/>
									</c:if>			
								</c:forEach>
								
								<c:if test="${badgeCount eq 0}">
								<div class="panel-content-wrapper">	
									You haven't received any badges yet.
								</div>
								</c:if>

					</div>
				</div>
			</div>
		</div>
	</div>
	<script>		
	</script>
	<jsp:include page="../common/footer.jsp" />
</body>
<script>

$(document).ready(function() {

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
