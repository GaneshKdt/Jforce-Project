<!DOCTYPE html>
<%@page import="java.util.List"%>
<html lang="en">

<jsp:include page="../common/jscss.jsp">
	<jsp:param value="Ranks" name="title" />
</jsp:include>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="com.nmims.controllers.BaseController"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<head>
<style>

	.rankStudent-image {
		height: 2.7em;
		width: 2.7em;
		border: 2px solid #fff;
		margin: 0em 1.5em 0em 0;
		display: block;
		float: left;
		background-size: cover;
		background-position: center;
		border-radius: 50%;
	}
	
</style>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

</head>
<body>

	<div class="sz-main-content-wrapper">

		<div class="sz-main-content menu-closed">


			<div class="sz-content" style="max-width: 70%; margin: auto;">

				<div class="clearfix"></div>

				<div class="panel-content-wrapper">

					<div class="courses-panel-collapse panel-content-wrapper">
						<div class="sz-logo">
							<img src="<spring:eval expression="@propertyConfigurer.getProperty('BASE_URL_STUDENTPORTAL_STATIC_RESOURCES')" />assets/images/nmims-logo.png" class="img-responsive" alt="" style="margin: auto; width: 400px;">
						</div>
						<c:choose>
							<c:when test="${ rankType == 'cycleWise' }">
								<div class='row' style="text-align: center;">
									<h2 style="float: none;">Cycle Wise Rank</h2>
								</div><br>
								<div id="cycle">
									<div class="ranks">
										<c:forEach var="student" items="${ rank }">
											<div class='row'>
												<div class='col-sm-4'></div>
												<div class='panel-body col-sm-4'
													style='border-top: none; border-bottom: 4px solid #d2232a'>
													<span class="rankStudent-image"
														style="background-image:url(${ student.studentImage });"></span>
													<h6 class='rankStudent-name'>${ student.name }</h6>
													<span class='rankStudent-info-list'> <span class=''
														style='font-weight: bold'>${ student.rank } Rank</span> | <span
														class='' style='font-weight: bold;'>Score : ${ student.total }</span>
													</span>
												</div>
											</div>
										</c:forEach>

										<div class='row'>
											<div class='col-sm-4'></div>
											<div class='panel-body col-sm-4' style='border-top: none'>
												<c:choose>
													<c:when test="${ not empty studentRankDetails.rank }">
														<span class='' style='font-weight: bold'>${ studentRankDetails.name }'s
															rank : ${ studentRankDetails.rank }</span>
														<br>
														<span class='' style='font-weight: bold'>${ studentRankDetails.name }'s
															score : ${ studentRankDetails.total }</span>
													</c:when>
													<c:otherwise>
														<span class='' style='font-weight: bold'>${ studentRankDetails.name }'s
															rank : Not Applicable</span>
														<br>
														<span class='' style='font-weight: bold'>${ studentRankDetails.name }'s
															score : Not Applicable</span>
													</c:otherwise>
												</c:choose>
											</div>
										</div>

										<div id="rankdescription">
											<p style="font-size: 16px;">How Your Rank is Calculated:</p>
											<ul>
												<li>There is one leaderboard for every semester of your
													program.</li>
												<li>The leaderboard will display the names and scores
													of the top 5 ranked students across all subjects in that
													semester.</li>
												<li>The leaderboard will also display where do you
													stand (your rank) among your fellow students in the same
													semester of your program.</li>
												<li>You will see your rank only if you have cleared all
													of your subjects in the very first attempt as per your
													semester registration month and year. The ranking will not
													be displayed for the students that pass a subject in
													backlog.</li>
												<li>The scores are calculated based on the Assignment
													and TEE marks obtained in each subject, out of 100.</li>
												<li>Group of students having the same semester
													registration month/year, program and program structure is
													considered as a batch for rank calculation.</li>
											</ul>
										</div>
									</div>
								</div>
							</c:when>

							<c:otherwise>
								<div class='row' style="text-align: center;">
									<h2 style="float: none;">Subject Wise Rank</h2>
								</div><br>
								<div id="subject">
									<div class="subjectRanks">

										<div class="ranks">
											<c:forEach var="student" items="${ rank }">
												<div class='row'>
													<div class='col-sm-4'></div>
													<div class='panel-body col-sm-4'
														style='border-top: none; border-bottom: 4px solid #d2232a'>
														<span class="rankStudent-image"
															style="background-image:url(${ student.studentImage });"></span>
														<h6 class='rankStudent-name'>${ student.name }</h6>
														<span class='rankStudent-info-list'> <span class=''
															style='font-weight: bold'>${ student.rank } Rank</span> | <span
															class='' style='font-weight: bold;'>Score : ${ student.total }</span>
														</span>
													</div>
												</div>
											</c:forEach>
										</div>
										
										<div class='row'>
											<div class='col-sm-4'></div>
											<div class='panel-body col-sm-4' style='border-top: none'>
												<c:choose>
													<c:when test="${ not empty studentRankDetails.rank }">
														<span class='' style='font-weight: bold'>${ studentRankDetails.name }'s
															rank : ${ studentRankDetails.rank }</span>
														<br>
														<span class='' style='font-weight: bold'>${ studentRankDetails.name }'s
															score : ${ studentRankDetails.total }</span>
													</c:when>
													<c:otherwise>
														<span class='' style='font-weight: bold'>${ studentRankDetails.name }'s
															rank : Not Applicable</span>
														<br>
														<span class='' style='font-weight: bold'>${ studentRankDetails.name }'s
															score : Not Applicable</span>
													</c:otherwise>
												</c:choose>
											</div>
										</div>
										
										<div id="rankdescription">
											<p style="font-size: 16px;">How Your Rank is Calculated:</p>
											<ul>
												<li>There is one leaderboard for every subject in the
													semester of your program.</li>
												<li>This leaderboard will display the names and scores
													of the top 5 ranked students.</li>
												<li>The leaderboard will also display where do you
													stand (your rank) among your fellow students in that same
													subject of the semester.</li>
												<li>You will see your rank only if you have cleared the
													subject in the very first attempt as per your semester
													registration month and year.</li>
												<li>The scores are calculated based on the Assignment
													and TEE marks obtained in the subject, out of 100.</li>
												<li>Group of students having the same subject, semester
													registration month/year, program and program structure is
													considered as a batch for rank calculation.</li>
											</ul>
										</div>
									</div>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
